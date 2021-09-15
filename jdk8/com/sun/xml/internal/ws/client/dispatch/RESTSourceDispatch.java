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
import com.sun.xml.internal.ws.encoding.xml.XMLMessage;
import com.sun.xml.internal.ws.message.source.PayloadSourceMessage;
import java.io.IOException;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Service;

final class RESTSourceDispatch extends DispatchImpl<Source> {
   /** @deprecated */
   @Deprecated
   public RESTSourceDispatch(QName port, Service.Mode mode, WSServiceDelegate owner, Tube pipe, BindingImpl binding, WSEndpointReference epr) {
      super(port, mode, owner, pipe, binding, epr);

      assert isXMLHttp(binding);

   }

   public RESTSourceDispatch(WSPortInfo portInfo, Service.Mode mode, BindingImpl binding, WSEndpointReference epr) {
      super(portInfo, mode, binding, epr);

      assert isXMLHttp(binding);

   }

   Source toReturnValue(Packet response) {
      Message msg = response.getMessage();

      try {
         return new StreamSource(XMLMessage.getDataSource(msg, this.binding.getFeatures()).getInputStream());
      } catch (IOException var4) {
         throw new RuntimeException(var4);
      }
   }

   Packet createPacket(Source msg) {
      Object message;
      if (msg == null) {
         message = Messages.createEmpty(this.soapVersion);
      } else {
         message = new PayloadSourceMessage((MessageHeaders)null, msg, this.setOutboundAttachments(), this.soapVersion);
      }

      return new Packet((Message)message);
   }
}
