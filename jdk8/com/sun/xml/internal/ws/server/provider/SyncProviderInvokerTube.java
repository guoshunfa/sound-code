package com.sun.xml.internal.ws.server.provider;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.internal.ws.api.server.Invoker;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SyncProviderInvokerTube<T> extends ProviderInvokerTube<T> {
   private static final Logger LOGGER = Logger.getLogger("com.sun.xml.internal.ws.server.SyncProviderInvokerTube");

   public SyncProviderInvokerTube(Invoker invoker, ProviderArgumentsBuilder<T> argsBuilder) {
      super(invoker, argsBuilder);
   }

   public NextAction processRequest(Packet request) {
      WSDLPort port = this.getEndpoint().getPort();
      WSBinding binding = this.getEndpoint().getBinding();
      T param = this.argsBuilder.getParameter(request);
      LOGGER.fine("Invoking Provider Endpoint");

      Object returnValue;
      try {
         returnValue = this.getInvoker(request).invokeProvider(request, param);
      } catch (Exception var9) {
         LOGGER.log(Level.SEVERE, (String)var9.getMessage(), (Throwable)var9);
         Packet response = this.argsBuilder.getResponse(request, var9, port, binding);
         return this.doReturnWith(response);
      }

      if (returnValue == null && request.transportBackChannel != null) {
         request.transportBackChannel.close();
      }

      Packet response = this.argsBuilder.getResponse(request, returnValue, port, binding);
      ThrowableContainerPropertySet tc = (ThrowableContainerPropertySet)response.getSatellite(ThrowableContainerPropertySet.class);
      Throwable t = tc != null ? tc.getThrowable() : null;
      return t != null ? this.doThrow(response, t) : this.doReturnWith(response);
   }

   @NotNull
   public NextAction processResponse(@NotNull Packet response) {
      return this.doReturnWith(response);
   }

   @NotNull
   public NextAction processException(@NotNull Throwable t) {
      return this.doThrow(t);
   }
}
