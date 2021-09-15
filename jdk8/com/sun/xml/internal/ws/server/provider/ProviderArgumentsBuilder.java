package com.sun.xml.internal.ws.server.provider;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import javax.xml.ws.soap.SOAPBinding;

public abstract class ProviderArgumentsBuilder<T> {
   protected abstract Message getResponseMessage(Exception var1);

   protected Packet getResponse(Packet request, Exception e, WSDLPort port, WSBinding binding) {
      Message message = this.getResponseMessage(e);
      Packet response = request.createServerResponse(message, (WSDLPort)port, (SEIModel)null, (WSBinding)binding);
      return response;
   }

   public abstract T getParameter(Packet var1);

   protected abstract Message getResponseMessage(T var1);

   protected Packet getResponse(Packet request, @Nullable T returnValue, WSDLPort port, WSBinding binding) {
      Message message = null;
      if (returnValue != null) {
         message = this.getResponseMessage(returnValue);
      }

      Packet response = request.createServerResponse(message, (WSDLPort)port, (SEIModel)null, (WSBinding)binding);
      return response;
   }

   public static ProviderArgumentsBuilder<?> create(ProviderEndpointModel model, WSBinding binding) {
      if (model.datatype == Packet.class) {
         return new ProviderArgumentsBuilder.PacketProviderArgumentsBuilder(binding.getSOAPVersion());
      } else {
         return (ProviderArgumentsBuilder)(binding instanceof SOAPBinding ? SOAPProviderArgumentBuilder.create(model, binding.getSOAPVersion()) : XMLProviderArgumentBuilder.createBuilder(model, binding));
      }
   }

   private static class PacketProviderArgumentsBuilder extends ProviderArgumentsBuilder<Packet> {
      private final SOAPVersion soapVersion;

      public PacketProviderArgumentsBuilder(SOAPVersion soapVersion) {
         this.soapVersion = soapVersion;
      }

      protected Message getResponseMessage(Exception e) {
         return SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, (CheckedExceptionImpl)null, (Throwable)e);
      }

      public Packet getParameter(Packet packet) {
         return packet;
      }

      protected Message getResponseMessage(Packet returnValue) {
         throw new IllegalStateException();
      }

      protected Packet getResponse(Packet request, @Nullable Packet returnValue, WSDLPort port, WSBinding binding) {
         return returnValue;
      }
   }
}
