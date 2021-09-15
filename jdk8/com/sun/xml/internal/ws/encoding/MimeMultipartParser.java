package com.sun.xml.internal.ws.encoding;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.org.jvnet.mimepull.Header;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEMessage;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEPart;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentEx;
import com.sun.xml.internal.ws.developer.StreamingAttachmentFeature;
import com.sun.xml.internal.ws.developer.StreamingDataHandler;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import com.sun.xml.internal.ws.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceException;

public final class MimeMultipartParser {
   private final String start;
   private final MIMEMessage message;
   private Attachment root;
   private ContentTypeImpl contentType;
   private final Map<String, Attachment> attachments = new HashMap();
   private boolean gotAll;

   public MimeMultipartParser(InputStream in, String cType, StreamingAttachmentFeature feature) {
      this.contentType = new ContentTypeImpl(cType);
      String boundary = this.contentType.getBoundary();
      if (boundary != null && !boundary.equals("")) {
         this.message = feature != null ? new MIMEMessage(in, boundary, feature.getConfig()) : new MIMEMessage(in, boundary);
         String st = this.contentType.getRootId();
         if (st != null && st.length() > 2 && st.charAt(0) == '<' && st.charAt(st.length() - 1) == '>') {
            st = st.substring(1, st.length() - 1);
         }

         this.start = st;
      } else {
         throw new WebServiceException("MIME boundary parameter not found" + this.contentType);
      }
   }

   @Nullable
   public Attachment getRootPart() {
      if (this.root == null) {
         this.root = new MimeMultipartParser.PartAttachment(this.start != null ? this.message.getPart(this.start) : this.message.getPart(0));
      }

      return this.root;
   }

   @NotNull
   public Map<String, Attachment> getAttachmentParts() {
      if (!this.gotAll) {
         MIMEPart rootPart = this.start != null ? this.message.getPart(this.start) : this.message.getPart(0);
         List<MIMEPart> parts = this.message.getAttachments();
         Iterator var3 = parts.iterator();

         while(var3.hasNext()) {
            MIMEPart part = (MIMEPart)var3.next();
            if (part != rootPart) {
               String cid = part.getContentId();
               if (!this.attachments.containsKey(cid)) {
                  MimeMultipartParser.PartAttachment attach = new MimeMultipartParser.PartAttachment(part);
                  this.attachments.put(attach.getContentId(), attach);
               }
            }
         }

         this.gotAll = true;
      }

      return this.attachments;
   }

   @Nullable
   public Attachment getAttachmentPart(String contentId) throws IOException {
      Attachment attach = (Attachment)this.attachments.get(contentId);
      if (attach == null) {
         MIMEPart part = this.message.getPart(contentId);
         attach = new MimeMultipartParser.PartAttachment(part);
         this.attachments.put(contentId, attach);
      }

      return (Attachment)attach;
   }

   public ContentTypeImpl getContentType() {
      return this.contentType;
   }

   static class PartAttachment implements AttachmentEx {
      final MIMEPart part;
      byte[] buf;
      private StreamingDataHandler streamingDataHandler;

      PartAttachment(MIMEPart part) {
         this.part = part;
      }

      @NotNull
      public String getContentId() {
         return this.part.getContentId();
      }

      @NotNull
      public String getContentType() {
         return this.part.getContentType();
      }

      public byte[] asByteArray() {
         if (this.buf == null) {
            ByteArrayBuffer baf = new ByteArrayBuffer();

            try {
               baf.write(this.part.readOnce());
            } catch (IOException var10) {
               throw new WebServiceException(var10);
            } finally {
               if (baf != null) {
                  try {
                     baf.close();
                  } catch (IOException var9) {
                     Logger.getLogger(MimeMultipartParser.class.getName()).log(Level.FINE, (String)null, (Throwable)var9);
                  }
               }

            }

            this.buf = baf.toByteArray();
         }

         return this.buf;
      }

      public DataHandler asDataHandler() {
         if (this.streamingDataHandler == null) {
            this.streamingDataHandler = (StreamingDataHandler)(this.buf != null ? new DataSourceStreamingDataHandler(new ByteArrayDataSource(this.buf, this.getContentType())) : new MIMEPartStreamingDataHandler(this.part));
         }

         return this.streamingDataHandler;
      }

      public Source asSource() {
         return this.buf != null ? new StreamSource(new ByteArrayInputStream(this.buf)) : new StreamSource(this.part.read());
      }

      public InputStream asInputStream() {
         return (InputStream)(this.buf != null ? new ByteArrayInputStream(this.buf) : this.part.read());
      }

      public void writeTo(OutputStream os) throws IOException {
         if (this.buf != null) {
            os.write(this.buf);
         } else {
            InputStream in = this.part.read();
            byte[] temp = new byte[8192];

            int len;
            while((len = in.read(temp)) != -1) {
               os.write(temp, 0, len);
            }

            in.close();
         }

      }

      public void writeTo(SOAPMessage saaj) throws SOAPException {
         saaj.createAttachmentPart().setDataHandler(this.asDataHandler());
      }

      public Iterator<AttachmentEx.MimeHeader> getMimeHeaders() {
         final Iterator<? extends Header> ih = this.part.getAllHeaders().iterator();
         return new Iterator<AttachmentEx.MimeHeader>() {
            public boolean hasNext() {
               return ih.hasNext();
            }

            public AttachmentEx.MimeHeader next() {
               final Header hdr = (Header)ih.next();
               return new AttachmentEx.MimeHeader() {
                  public String getValue() {
                     return hdr.getValue();
                  }

                  public String getName() {
                     return hdr.getName();
                  }
               };
            }

            public void remove() {
               throw new UnsupportedOperationException();
            }
         };
      }
   }
}
