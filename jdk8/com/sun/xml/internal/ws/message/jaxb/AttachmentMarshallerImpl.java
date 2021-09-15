package com.sun.xml.internal.ws.message.jaxb;

import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.message.DataHandlerAttachment;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.logging.Level;
import javax.activation.DataHandler;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.ws.WebServiceException;

final class AttachmentMarshallerImpl extends AttachmentMarshaller {
   private static final Logger LOGGER = Logger.getLogger(AttachmentMarshallerImpl.class);
   private AttachmentSet attachments;

   public AttachmentMarshallerImpl(AttachmentSet attachemnts) {
      this.attachments = attachemnts;
   }

   void cleanup() {
      this.attachments = null;
   }

   public String addMtomAttachment(DataHandler data, String elementNamespace, String elementLocalName) {
      throw new IllegalStateException();
   }

   public String addMtomAttachment(byte[] data, int offset, int length, String mimeType, String elementNamespace, String elementLocalName) {
      throw new IllegalStateException();
   }

   public String addSwaRefAttachment(DataHandler data) {
      String cid = this.encodeCid((String)null);
      Attachment att = new DataHandlerAttachment(cid, data);
      this.attachments.add(att);
      cid = "cid:" + cid;
      return cid;
   }

   private String encodeCid(String ns) {
      String cid = "example.jaxws.sun.com";
      String name = UUID.randomUUID() + "@";
      if (ns != null && ns.length() > 0) {
         try {
            URI uri = new URI(ns);
            cid = uri.toURL().getHost();
         } catch (URISyntaxException var7) {
            if (LOGGER.isLoggable(Level.INFO)) {
               LOGGER.log(Level.INFO, (String)null, (Throwable)var7);
            }

            return null;
         } catch (MalformedURLException var8) {
            try {
               cid = URLEncoder.encode(ns, "UTF-8");
            } catch (UnsupportedEncodingException var6) {
               throw new WebServiceException(var8);
            }
         }
      }

      return name + cid;
   }
}
