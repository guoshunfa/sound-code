package com.sun.xml.internal.ws.client.dispatch;

import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.Headers;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import com.sun.xml.internal.ws.message.jaxb.JAXBDispatchMessage;
import com.sun.xml.internal.ws.spi.db.BindingContextFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

public class JAXBDispatch extends DispatchImpl<Object> {
   private final JAXBContext jaxbcontext;
   private final boolean isContextSupported;

   /** @deprecated */
   @Deprecated
   public JAXBDispatch(QName port, JAXBContext jc, Service.Mode mode, WSServiceDelegate service, Tube pipe, BindingImpl binding, WSEndpointReference epr) {
      super(port, mode, service, pipe, binding, epr);
      this.jaxbcontext = jc;
      this.isContextSupported = BindingContextFactory.isContextSupported(jc);
   }

   public JAXBDispatch(WSPortInfo portInfo, JAXBContext jc, Service.Mode mode, BindingImpl binding, WSEndpointReference epr) {
      super(portInfo, mode, binding, epr);
      this.jaxbcontext = jc;
      this.isContextSupported = BindingContextFactory.isContextSupported(jc);
   }

   Object toReturnValue(Packet response) {
      try {
         Unmarshaller unmarshaller = this.jaxbcontext.createUnmarshaller();
         Message msg = response.getMessage();
         switch(this.mode) {
         case PAYLOAD:
            return msg.readPayloadAsJAXB(unmarshaller);
         case MESSAGE:
            Source result = msg.readEnvelopeAsSource();
            return unmarshaller.unmarshal(result);
         default:
            throw new WebServiceException("Unrecognized dispatch mode");
         }
      } catch (JAXBException var5) {
         throw new WebServiceException(var5);
      }
   }

   Packet createPacket(Object msg) {
      assert this.jaxbcontext != null;

      Object message;
      if (this.mode == Service.Mode.MESSAGE) {
         message = this.isContextSupported ? new JAXBDispatchMessage(BindingContextFactory.create(this.jaxbcontext), msg, this.soapVersion) : new JAXBDispatchMessage(this.jaxbcontext, msg, this.soapVersion);
      } else if (msg == null) {
         message = Messages.createEmpty(this.soapVersion);
      } else {
         message = this.isContextSupported ? Messages.create(this.jaxbcontext, msg, this.soapVersion) : Messages.createRaw(this.jaxbcontext, msg, this.soapVersion);
      }

      return new Packet((Message)message);
   }

   public void setOutboundHeaders(Object... headers) {
      if (headers == null) {
         throw new IllegalArgumentException();
      } else {
         Header[] hl = new Header[headers.length];

         for(int i = 0; i < hl.length; ++i) {
            if (headers[i] == null) {
               throw new IllegalArgumentException();
            }

            hl[i] = Headers.create((JAXBContext)((JAXBRIContext)this.jaxbcontext), (Object)headers[i]);
         }

         super.setOutboundHeaders(hl);
      }
   }
}
