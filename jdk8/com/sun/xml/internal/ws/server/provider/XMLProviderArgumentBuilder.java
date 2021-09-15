package com.sun.xml.internal.ws.server.provider;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.encoding.xml.XMLMessage;
import com.sun.xml.internal.ws.resources.ServerMessages;
import javax.activation.DataSource;
import javax.xml.transform.Source;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.http.HTTPException;

abstract class XMLProviderArgumentBuilder<T> extends ProviderArgumentsBuilder<T> {
   protected Packet getResponse(Packet request, Exception e, WSDLPort port, WSBinding binding) {
      Packet response = super.getResponse(request, e, port, binding);
      if (e instanceof HTTPException && response.supports("javax.xml.ws.http.response.code")) {
         response.put("javax.xml.ws.http.response.code", ((HTTPException)e).getStatusCode());
      }

      return response;
   }

   static XMLProviderArgumentBuilder createBuilder(ProviderEndpointModel model, WSBinding binding) {
      if (model.mode == Service.Mode.PAYLOAD) {
         return new XMLProviderArgumentBuilder.PayloadSource();
      } else if (model.datatype == Source.class) {
         return new XMLProviderArgumentBuilder.PayloadSource();
      } else if (model.datatype == DataSource.class) {
         return new XMLProviderArgumentBuilder.DataSourceParameter(binding);
      } else {
         throw new WebServiceException(ServerMessages.PROVIDER_INVALID_PARAMETER_TYPE(model.implClass, model.datatype));
      }
   }

   private static final class DataSourceParameter extends XMLProviderArgumentBuilder<DataSource> {
      private final WSBinding binding;

      DataSourceParameter(WSBinding binding) {
         this.binding = binding;
      }

      public DataSource getParameter(Packet packet) {
         Message msg = packet.getInternalMessage();
         return msg instanceof XMLMessage.MessageDataSource ? ((XMLMessage.MessageDataSource)msg).getDataSource() : XMLMessage.getDataSource(msg, this.binding.getFeatures());
      }

      public Message getResponseMessage(DataSource ds) {
         return XMLMessage.create(ds, this.binding.getFeatures());
      }

      protected Message getResponseMessage(Exception e) {
         return XMLMessage.create(e);
      }
   }

   private static final class PayloadSource extends XMLProviderArgumentBuilder<Source> {
      private PayloadSource() {
      }

      public Source getParameter(Packet packet) {
         return packet.getMessage().readPayloadAsSource();
      }

      public Message getResponseMessage(Source source) {
         return Messages.createUsingPayload(source, SOAPVersion.SOAP_11);
      }

      protected Message getResponseMessage(Exception e) {
         return XMLMessage.create(e);
      }

      // $FF: synthetic method
      PayloadSource(Object x0) {
         this();
      }
   }
}
