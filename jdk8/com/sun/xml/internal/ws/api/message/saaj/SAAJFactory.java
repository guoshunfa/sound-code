package com.sun.xml.internal.ws.api.message.saaj;

import com.sun.xml.internal.bind.marshaller.SAX2DOMEx;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentEx;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.message.saaj.SAAJMessage;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.util.Iterator;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public class SAAJFactory {
   private static final SAAJFactory instance = new SAAJFactory();

   public static MessageFactory getMessageFactory(String protocol) throws SOAPException {
      Iterator var1 = ServiceFinder.find(SAAJFactory.class).iterator();

      MessageFactory mf;
      do {
         if (!var1.hasNext()) {
            return instance.createMessageFactory(protocol);
         }

         SAAJFactory s = (SAAJFactory)var1.next();
         mf = s.createMessageFactory(protocol);
      } while(mf == null);

      return mf;
   }

   public static SOAPFactory getSOAPFactory(String protocol) throws SOAPException {
      Iterator var1 = ServiceFinder.find(SAAJFactory.class).iterator();

      SOAPFactory sf;
      do {
         if (!var1.hasNext()) {
            return instance.createSOAPFactory(protocol);
         }

         SAAJFactory s = (SAAJFactory)var1.next();
         sf = s.createSOAPFactory(protocol);
      } while(sf == null);

      return sf;
   }

   public static Message create(SOAPMessage saaj) {
      Iterator var1 = ServiceFinder.find(SAAJFactory.class).iterator();

      Message m;
      do {
         if (!var1.hasNext()) {
            return instance.createMessage(saaj);
         }

         SAAJFactory s = (SAAJFactory)var1.next();
         m = s.createMessage(saaj);
      } while(m == null);

      return m;
   }

   public static SOAPMessage read(SOAPVersion soapVersion, Message message) throws SOAPException {
      Iterator var2 = ServiceFinder.find(SAAJFactory.class).iterator();

      SOAPMessage msg;
      do {
         if (!var2.hasNext()) {
            return instance.readAsSOAPMessage(soapVersion, message);
         }

         SAAJFactory s = (SAAJFactory)var2.next();
         msg = s.readAsSOAPMessage(soapVersion, message);
      } while(msg == null);

      return msg;
   }

   public static SOAPMessage read(SOAPVersion soapVersion, Message message, Packet packet) throws SOAPException {
      Iterator var3 = ServiceFinder.find(SAAJFactory.class).iterator();

      SOAPMessage msg;
      do {
         if (!var3.hasNext()) {
            return instance.readAsSOAPMessage(soapVersion, message, packet);
         }

         SAAJFactory s = (SAAJFactory)var3.next();
         msg = s.readAsSOAPMessage(soapVersion, message, packet);
      } while(msg == null);

      return msg;
   }

   public static SAAJMessage read(Packet packet) throws SOAPException {
      ServiceFinder<SAAJFactory> factories = packet.component != null ? ServiceFinder.find(SAAJFactory.class, packet.component) : ServiceFinder.find(SAAJFactory.class);
      Iterator var2 = factories.iterator();

      SAAJMessage msg;
      do {
         if (!var2.hasNext()) {
            return instance.readAsSAAJ(packet);
         }

         SAAJFactory s = (SAAJFactory)var2.next();
         msg = s.readAsSAAJ(packet);
      } while(msg == null);

      return msg;
   }

   public SAAJMessage readAsSAAJ(Packet packet) throws SOAPException {
      SOAPVersion v = packet.getMessage().getSOAPVersion();
      SOAPMessage msg = this.readAsSOAPMessage(v, packet.getMessage());
      return new SAAJMessage(msg);
   }

   public MessageFactory createMessageFactory(String protocol) throws SOAPException {
      return MessageFactory.newInstance(protocol);
   }

   public SOAPFactory createSOAPFactory(String protocol) throws SOAPException {
      return SOAPFactory.newInstance(protocol);
   }

   public Message createMessage(SOAPMessage saaj) {
      return new SAAJMessage(saaj);
   }

   public SOAPMessage readAsSOAPMessage(SOAPVersion soapVersion, Message message) throws SOAPException {
      SOAPMessage msg = soapVersion.getMessageFactory().createMessage();
      SaajStaxWriter writer = new SaajStaxWriter(msg);

      try {
         message.writeTo(writer);
      } catch (XMLStreamException var6) {
         throw var6.getCause() instanceof SOAPException ? (SOAPException)var6.getCause() : new SOAPException(var6);
      }

      msg = writer.getSOAPMessage();
      addAttachmentsToSOAPMessage(msg, message);
      if (msg.saveRequired()) {
         msg.saveChanges();
      }

      return msg;
   }

   public SOAPMessage readAsSOAPMessageSax2Dom(SOAPVersion soapVersion, Message message) throws SOAPException {
      SOAPMessage msg = soapVersion.getMessageFactory().createMessage();
      SAX2DOMEx s2d = new SAX2DOMEx(msg.getSOAPPart());

      try {
         message.writeTo(s2d, XmlUtil.DRACONIAN_ERROR_HANDLER);
      } catch (SAXException var6) {
         throw new SOAPException(var6);
      }

      addAttachmentsToSOAPMessage(msg, message);
      if (msg.saveRequired()) {
         msg.saveChanges();
      }

      return msg;
   }

   protected static void addAttachmentsToSOAPMessage(SOAPMessage msg, Message message) {
      AttachmentPart part;
      for(Iterator var2 = message.getAttachments().iterator(); var2.hasNext(); msg.addAttachmentPart(part)) {
         Attachment att = (Attachment)var2.next();
         part = msg.createAttachmentPart();
         part.setDataHandler(att.asDataHandler());
         String cid = att.getContentId();
         if (cid != null) {
            if (cid.startsWith("<") && cid.endsWith(">")) {
               part.setContentId(cid);
            } else {
               part.setContentId('<' + cid + '>');
            }
         }

         if (att instanceof AttachmentEx) {
            AttachmentEx ax = (AttachmentEx)att;
            Iterator imh = ax.getMimeHeaders();

            while(imh.hasNext()) {
               AttachmentEx.MimeHeader ame = (AttachmentEx.MimeHeader)imh.next();
               if (!"Content-ID".equals(ame.getName()) && !"Content-Type".equals(ame.getName())) {
                  part.addMimeHeader(ame.getName(), ame.getValue());
               }
            }
         }
      }

   }

   public SOAPMessage readAsSOAPMessage(SOAPVersion soapVersion, Message message, Packet packet) throws SOAPException {
      return this.readAsSOAPMessage(soapVersion, message);
   }
}
