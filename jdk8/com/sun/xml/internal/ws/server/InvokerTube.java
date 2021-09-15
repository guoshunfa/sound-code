package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.api.server.AsyncProviderCallback;
import com.sun.xml.internal.ws.api.server.Invoker;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WSWebServiceContext;
import com.sun.xml.internal.ws.resources.ServerMessages;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceException;

public abstract class InvokerTube<T> extends com.sun.xml.internal.ws.server.sei.InvokerTube<Invoker> implements EndpointAwareTube {
   private WSEndpoint endpoint;
   private static final ThreadLocal<Packet> packets = new ThreadLocal();
   private final Invoker wrapper = new Invoker() {
      public Object invoke(Packet p, Method m, Object... args) throws InvocationTargetException, IllegalAccessException {
         Packet old = this.set(p);

         Object var5;
         try {
            var5 = ((Invoker)InvokerTube.this.invoker).invoke(p, m, args);
         } finally {
            this.set(old);
         }

         return var5;
      }

      public <T> T invokeProvider(Packet p, T arg) throws IllegalAccessException, InvocationTargetException {
         Packet old = this.set(p);

         Object var4;
         try {
            var4 = ((Invoker)InvokerTube.this.invoker).invokeProvider(p, arg);
         } finally {
            this.set(old);
         }

         return var4;
      }

      public <T> void invokeAsyncProvider(Packet p, T arg, AsyncProviderCallback cbak, WebServiceContext ctxt) throws IllegalAccessException, InvocationTargetException {
         Packet old = this.set(p);

         try {
            ((Invoker)InvokerTube.this.invoker).invokeAsyncProvider(p, arg, cbak, ctxt);
         } finally {
            this.set(old);
         }

      }

      private Packet set(Packet p) {
         Packet old = (Packet)InvokerTube.packets.get();
         InvokerTube.packets.set(p);
         return old;
      }
   };

   protected InvokerTube(Invoker invoker) {
      super(invoker);
   }

   public void setEndpoint(WSEndpoint endpoint) {
      this.endpoint = endpoint;
      WSWebServiceContext webServiceContext = new AbstractWebServiceContext(endpoint) {
         @Nullable
         public Packet getRequestPacket() {
            Packet p = (Packet)InvokerTube.packets.get();
            return p;
         }
      };
      ((Invoker)this.invoker).start(webServiceContext, endpoint);
   }

   protected WSEndpoint getEndpoint() {
      return this.endpoint;
   }

   @NotNull
   public final Invoker getInvoker(Packet request) {
      return this.wrapper;
   }

   public final AbstractTubeImpl copy(TubeCloner cloner) {
      cloner.add(this, this);
      return this;
   }

   public void preDestroy() {
      ((Invoker)this.invoker).dispose();
   }

   @NotNull
   public static Packet getCurrentPacket() {
      Packet packet = (Packet)packets.get();
      if (packet == null) {
         throw new WebServiceException(ServerMessages.NO_CURRENT_PACKET());
      } else {
         return packet;
      }
   }
}
