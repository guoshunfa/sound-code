package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.message.saaj.SAAJFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class SOAPMessageContextImpl extends MessageUpdatableContext implements SOAPMessageContext {
   private Set<String> roles;
   private SOAPMessage soapMsg = null;
   private WSBinding binding;

   public SOAPMessageContextImpl(WSBinding binding, Packet packet, Set<String> roles) {
      super(packet);
      this.binding = binding;
      this.roles = roles;
   }

   public SOAPMessage getMessage() {
      if (this.soapMsg == null) {
         try {
            Message m = this.packet.getMessage();
            this.soapMsg = m != null ? m.readAsSOAPMessage() : null;
         } catch (SOAPException var2) {
            throw new WebServiceException(var2);
         }
      }

      return this.soapMsg;
   }

   public void setMessage(SOAPMessage soapMsg) {
      try {
         this.soapMsg = soapMsg;
      } catch (Exception var3) {
         throw new WebServiceException(var3);
      }
   }

   void setPacketMessage(Message newMessage) {
      if (newMessage != null) {
         this.packet.setMessage(newMessage);
         this.soapMsg = null;
      }

   }

   protected void updateMessage() {
      if (this.soapMsg != null) {
         this.packet.setMessage(SAAJFactory.create(this.soapMsg));
         this.soapMsg = null;
      }

   }

   public Object[] getHeaders(QName header, JAXBContext jaxbContext, boolean allRoles) {
      SOAPVersion soapVersion = this.binding.getSOAPVersion();
      ArrayList beanList = new ArrayList();

      try {
         Iterator<Header> itr = this.packet.getMessage().getHeaders().getHeaders(header, false);
         if (allRoles) {
            while(itr.hasNext()) {
               beanList.add(((Header)itr.next()).readAsJAXB(jaxbContext.createUnmarshaller()));
            }
         } else {
            while(itr.hasNext()) {
               Header soapHeader = (Header)itr.next();
               String role = soapHeader.getRole(soapVersion);
               if (this.getRoles().contains(role)) {
                  beanList.add(soapHeader.readAsJAXB(jaxbContext.createUnmarshaller()));
               }
            }
         }

         return beanList.toArray();
      } catch (Exception var9) {
         throw new WebServiceException(var9);
      }
   }

   public Set<String> getRoles() {
      return this.roles;
   }
}
