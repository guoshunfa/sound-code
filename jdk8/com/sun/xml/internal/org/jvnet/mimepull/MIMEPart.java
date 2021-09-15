package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MIMEPart {
   private static final Logger LOGGER = Logger.getLogger(MIMEPart.class.getName());
   private volatile InternetHeaders headers;
   private volatile String contentId;
   private String contentType;
   private String contentTransferEncoding;
   volatile boolean parsed;
   final MIMEMessage msg;
   private final DataHead dataHead;

   MIMEPart(MIMEMessage msg) {
      this.msg = msg;
      this.dataHead = new DataHead(this);
   }

   MIMEPart(MIMEMessage msg, String contentId) {
      this(msg);
      this.contentId = contentId;
   }

   public InputStream read() {
      InputStream is = null;

      try {
         is = MimeUtility.decode(this.dataHead.read(), this.contentTransferEncoding);
      } catch (DecodingException var3) {
         if (LOGGER.isLoggable(Level.WARNING)) {
            LOGGER.log(Level.WARNING, (String)null, (Throwable)var3);
         }
      }

      return is;
   }

   public void close() {
      this.dataHead.close();
   }

   public InputStream readOnce() {
      InputStream is = null;

      try {
         is = MimeUtility.decode(this.dataHead.readOnce(), this.contentTransferEncoding);
      } catch (DecodingException var3) {
         if (LOGGER.isLoggable(Level.WARNING)) {
            LOGGER.log(Level.WARNING, (String)null, (Throwable)var3);
         }
      }

      return is;
   }

   public void moveTo(File f) {
      this.dataHead.moveTo(f);
   }

   public String getContentId() {
      if (this.contentId == null) {
         this.getHeaders();
      }

      return this.contentId;
   }

   public String getContentTransferEncoding() {
      if (this.contentTransferEncoding == null) {
         this.getHeaders();
      }

      return this.contentTransferEncoding;
   }

   public String getContentType() {
      if (this.contentType == null) {
         this.getHeaders();
      }

      return this.contentType;
   }

   private void getHeaders() {
      while(true) {
         if (this.headers == null) {
            if (this.msg.makeProgress() || this.headers != null) {
               continue;
            }

            throw new IllegalStateException("Internal Error. Didn't get Headers even after complete parsing.");
         }

         return;
      }
   }

   public List<String> getHeader(String name) {
      this.getHeaders();

      assert this.headers != null;

      return this.headers.getHeader(name);
   }

   public List<? extends Header> getAllHeaders() {
      this.getHeaders();

      assert this.headers != null;

      return this.headers.getAllHeaders();
   }

   void setHeaders(InternetHeaders headers) {
      this.headers = headers;
      List<String> ct = this.getHeader("Content-Type");
      this.contentType = ct == null ? "application/octet-stream" : (String)ct.get(0);
      List<String> cte = this.getHeader("Content-Transfer-Encoding");
      this.contentTransferEncoding = cte == null ? "binary" : (String)cte.get(0);
   }

   void addBody(ByteBuffer buf) {
      this.dataHead.addBody(buf);
   }

   void doneParsing() {
      this.parsed = true;
      this.dataHead.doneParsing();
   }

   void setContentId(String cid) {
      this.contentId = cid;
   }

   void setContentTransferEncoding(String cte) {
      this.contentTransferEncoding = cte;
   }

   public String toString() {
      return "Part=" + this.contentId + ":" + this.contentTransferEncoding;
   }
}
