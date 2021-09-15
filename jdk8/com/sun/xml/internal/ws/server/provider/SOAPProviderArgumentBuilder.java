package com.sun.xml.internal.ws.server.provider;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.resources.ServerMessages;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

abstract class SOAPProviderArgumentBuilder<T> extends ProviderArgumentsBuilder<T> {
   protected final SOAPVersion soapVersion;

   private SOAPProviderArgumentBuilder(SOAPVersion soapVersion) {
      this.soapVersion = soapVersion;
   }

   static ProviderArgumentsBuilder create(ProviderEndpointModel model, SOAPVersion soapVersion) {
      if (model.mode == Service.Mode.PAYLOAD) {
         return new SOAPProviderArgumentBuilder.PayloadSource(soapVersion);
      } else if (model.datatype == Source.class) {
         return new SOAPProviderArgumentBuilder.MessageSource(soapVersion);
      } else if (model.datatype == SOAPMessage.class) {
         return new SOAPProviderArgumentBuilder.SOAPMessageParameter(soapVersion);
      } else if (model.datatype == Message.class) {
         return new MessageProviderArgumentBuilder(soapVersion);
      } else {
         throw new WebServiceException(ServerMessages.PROVIDER_INVALID_PARAMETER_TYPE(model.implClass, model.datatype));
      }
   }

   // $FF: synthetic method
   SOAPProviderArgumentBuilder(SOAPVersion x0, Object x1) {
      this(x0);
   }

   private static final class SOAPMessageParameter extends SOAPProviderArgumentBuilder<SOAPMessage> {
      SOAPMessageParameter(SOAPVersion soapVersion) {
         super(soapVersion, null);
      }

      public SOAPMessage getParameter(Packet packet) {
         try {
            return packet.getMessage().readAsSOAPMessage(packet, true);
         } catch (SOAPException var3) {
            throw new WebServiceException(var3);
         }
      }

      protected Message getResponseMessage(SOAPMessage soapMsg) {
         return Messages.create(soapMsg);
      }

      protected Message getResponseMessage(Exception e) {
         return SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, (CheckedExceptionImpl)null, (Throwable)e);
      }

      protected Packet getResponse(Packet request, @Nullable SOAPMessage returnValue, WSDLPort port, WSBinding binding) {
         Packet response = super.getResponse(request, returnValue, port, binding);
         if (returnValue != null && response.supports("com.sun.xml.internal.ws.api.message.packet.outbound.transport.headers")) {
            MimeHeaders hdrs = returnValue.getMimeHeaders();
            Map<String, List<String>> headers = new HashMap();
            Iterator i = hdrs.getAllHeaders();

            while(i.hasNext()) {
               MimeHeader header = (MimeHeader)i.next();
               if (!header.getName().equalsIgnoreCase("SOAPAction")) {
                  List<String> list = (List)headers.get(header.getName());
                  if (list == null) {
                     list = new ArrayList();
                     headers.put(header.getName(), list);
                  }

                  ((List)list).add(header.getValue());
               }
            }

            response.put("com.sun.xml.internal.ws.api.message.packet.outbound.transport.headers", headers);
         }

         return response;
      }
   }

   private static final class MessageSource extends SOAPProviderArgumentBuilder<Source> {
      MessageSource(SOAPVersion soapVersion) {
         super(soapVersion, null);
      }

      public Source getParameter(Packet packet) {
         return packet.getMessage().readEnvelopeAsSource();
      }

      protected Message getResponseMessage(Source source) {
         return Messages.create(source, this.soapVersion);
      }

      protected Message getResponseMessage(Exception e) {
         return SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, (CheckedExceptionImpl)null, (Throwable)e);
      }
   }

   private static final class PayloadSource extends SOAPProviderArgumentBuilder<Source> {
      PayloadSource(SOAPVersion soapVersion) {
         super(soapVersion, null);
      }

      public Source getParameter(Packet packet) {
         return packet.getMessage().readPayloadAsSource();
      }

      protected Message getResponseMessage(Source source) {
         return Messages.createUsingPayload(source, this.soapVersion);
      }

      protected Message getResponseMessage(Exception e) {
         return SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, (CheckedExceptionImpl)null, (Throwable)e);
      }
   }
}
