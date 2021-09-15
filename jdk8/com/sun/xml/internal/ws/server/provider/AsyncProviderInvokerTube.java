package com.sun.xml.internal.ws.server.provider;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.internal.ws.api.server.AsyncProviderCallback;
import com.sun.xml.internal.ws.api.server.Invoker;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.server.AbstractWebServiceContext;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AsyncProviderInvokerTube<T> extends ProviderInvokerTube<T> {
   private static final Logger LOGGER = Logger.getLogger("com.sun.xml.internal.ws.server.AsyncProviderInvokerTube");

   public AsyncProviderInvokerTube(Invoker invoker, ProviderArgumentsBuilder<T> argsBuilder) {
      super(invoker, argsBuilder);
   }

   @NotNull
   public NextAction processRequest(@NotNull Packet request) {
      T param = this.argsBuilder.getParameter(request);
      AsyncProviderInvokerTube<T>.NoSuspendResumer resumer = new AsyncProviderInvokerTube.NoSuspendResumer();
      AsyncProviderInvokerTube<T>.AsyncProviderCallbackImpl callback = new AsyncProviderInvokerTube.AsyncProviderCallbackImpl(request, resumer);
      AsyncProviderInvokerTube<T>.AsyncWebServiceContext ctxt = new AsyncProviderInvokerTube.AsyncWebServiceContext(this.getEndpoint(), request);
      LOGGER.fine("Invoking AsyncProvider Endpoint");

      try {
         this.getInvoker(request).invokeAsyncProvider(request, param, callback, ctxt);
      } catch (Throwable var11) {
         LOGGER.log(Level.SEVERE, var11.getMessage(), var11);
         return this.doThrow(var11);
      }

      synchronized(callback) {
         if (resumer.response != null) {
            ThrowableContainerPropertySet tc = (ThrowableContainerPropertySet)resumer.response.getSatellite(ThrowableContainerPropertySet.class);
            Throwable t = tc != null ? tc.getThrowable() : null;
            return t != null ? this.doThrow(resumer.response, t) : this.doReturnWith(resumer.response);
         } else {
            callback.resumer = new AsyncProviderInvokerTube.FiberResumer();
            return this.doSuspend();
         }
      }
   }

   @NotNull
   public NextAction processResponse(@NotNull Packet response) {
      return this.doReturnWith(response);
   }

   @NotNull
   public NextAction processException(@NotNull Throwable t) {
      return this.doThrow(t);
   }

   public class AsyncWebServiceContext extends AbstractWebServiceContext {
      final Packet packet;

      public AsyncWebServiceContext(WSEndpoint endpoint, Packet packet) {
         super(endpoint);
         this.packet = packet;
      }

      @NotNull
      public Packet getRequestPacket() {
         return this.packet;
      }
   }

   public class AsyncProviderCallbackImpl implements AsyncProviderCallback<T> {
      private final Packet request;
      private AsyncProviderInvokerTube.Resumer resumer;

      public AsyncProviderCallbackImpl(Packet request, AsyncProviderInvokerTube.Resumer resumer) {
         this.request = request;
         this.resumer = resumer;
      }

      public void send(@Nullable T param) {
         if (param == null && this.request.transportBackChannel != null) {
            this.request.transportBackChannel.close();
         }

         Packet packet = AsyncProviderInvokerTube.this.argsBuilder.getResponse(this.request, param, AsyncProviderInvokerTube.this.getEndpoint().getPort(), AsyncProviderInvokerTube.this.getEndpoint().getBinding());
         synchronized(this) {
            this.resumer.onResume(packet);
         }
      }

      public void sendError(@NotNull Throwable t) {
         Object e;
         if (t instanceof Exception) {
            e = (Exception)t;
         } else {
            e = new RuntimeException(t);
         }

         Packet packet = AsyncProviderInvokerTube.this.argsBuilder.getResponse(this.request, (Exception)e, AsyncProviderInvokerTube.this.getEndpoint().getPort(), AsyncProviderInvokerTube.this.getEndpoint().getBinding());
         synchronized(this) {
            this.resumer.onResume(packet);
         }
      }
   }

   private class NoSuspendResumer implements AsyncProviderInvokerTube.Resumer {
      protected Packet response;

      private NoSuspendResumer() {
         this.response = null;
      }

      public void onResume(Packet response) {
         this.response = response;
      }

      // $FF: synthetic method
      NoSuspendResumer(Object x1) {
         this();
      }
   }

   public class FiberResumer implements AsyncProviderInvokerTube.Resumer {
      private final Fiber fiber = Fiber.current();

      public void onResume(Packet response) {
         ThrowableContainerPropertySet tc = (ThrowableContainerPropertySet)response.getSatellite(ThrowableContainerPropertySet.class);
         Throwable t = tc != null ? tc.getThrowable() : null;
         this.fiber.resume(t, response);
      }
   }

   private interface Resumer {
      void onResume(Packet var1);
   }
}
