package com.sun.xml.internal.ws.client.dispatch;

import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.message.saaj.SAAJFactory;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import com.sun.xml.internal.ws.resources.DispatchMessages;
import com.sun.xml.internal.ws.transport.Headers;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

public class SOAPMessageDispatch extends DispatchImpl<SOAPMessage> {
   /** @deprecated */
   @Deprecated
   public SOAPMessageDispatch(QName port, Service.Mode mode, WSServiceDelegate owner, Tube pipe, BindingImpl binding, WSEndpointReference epr) {
      super(port, mode, owner, pipe, binding, epr);
   }

   public SOAPMessageDispatch(WSPortInfo portInfo, Service.Mode mode, BindingImpl binding, WSEndpointReference epr) {
      super(portInfo, mode, binding, epr);
   }

   Packet createPacket(SOAPMessage arg) {
      Iterator iter = arg.getMimeHeaders().getAllHeaders();
      Headers ch = new Headers();

      while(iter.hasNext()) {
         MimeHeader mh = (MimeHeader)iter.next();
         ch.add(mh.getName(), mh.getValue());
      }

      Packet packet = new Packet(SAAJFactory.create(arg));
      packet.invocationProperties.put("javax.xml.ws.http.request.headers", ch);
      return packet;
   }

   SOAPMessage toReturnValue(Packet response) {
      try {
         if (response != null && response.getMessage() != null) {
            return response.getMessage().readAsSOAPMessage();
         } else {
            throw new WebServiceException(DispatchMessages.INVALID_RESPONSE());
         }
      } catch (SOAPException var3) {
         throw new WebServiceException(var3);
      }
   }
}
