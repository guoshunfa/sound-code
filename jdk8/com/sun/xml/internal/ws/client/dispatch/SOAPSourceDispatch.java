package com.sun.xml.internal.ws.client.dispatch;

import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import com.sun.xml.internal.ws.message.source.PayloadSourceMessage;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

final class SOAPSourceDispatch extends DispatchImpl<Source> {
   /** @deprecated */
   @Deprecated
   public SOAPSourceDispatch(QName port, Service.Mode mode, WSServiceDelegate owner, Tube pipe, BindingImpl binding, WSEndpointReference epr) {
      super(port, mode, owner, pipe, binding, epr);

      assert !isXMLHttp(binding);

   }

   public SOAPSourceDispatch(WSPortInfo portInfo, Service.Mode mode, BindingImpl binding, WSEndpointReference epr) {
      super(portInfo, mode, binding, epr);

      assert !isXMLHttp(binding);

   }

   Source toReturnValue(Packet response) {
      Message msg = response.getMessage();
      switch(this.mode) {
      case PAYLOAD:
         return msg.readPayloadAsSource();
      case MESSAGE:
         return msg.readEnvelopeAsSource();
      default:
         throw new WebServiceException("Unrecognized dispatch mode");
      }
   }

   Packet createPacket(Source msg) {
      Object message;
      if (msg == null) {
         message = Messages.createEmpty(this.soapVersion);
      } else {
         switch(this.mode) {
         case PAYLOAD:
            message = new PayloadSourceMessage((MessageHeaders)null, msg, this.setOutboundAttachments(), this.soapVersion);
            break;
         case MESSAGE:
            message = Messages.create(msg, this.soapVersion);
            break;
         default:
            throw new WebServiceException("Unrecognized message mode");
         }
      }

      return new Packet((Message)message);
   }
}
