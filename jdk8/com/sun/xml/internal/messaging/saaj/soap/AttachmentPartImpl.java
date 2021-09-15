package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.InternetHeaders;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeBodyPart;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimePartDataSource;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.ASCIIUtility;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import com.sun.xml.internal.org.jvnet.mimepull.Header;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEPart;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.CommandInfo;
import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.MailcapCommandMap;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;

public class AttachmentPartImpl extends AttachmentPart {
   protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
   private final MimeHeaders headers = new MimeHeaders();
   private MimeBodyPart rawContent = null;
   private DataHandler dataHandler = null;
   private MIMEPart mimePart = null;

   public AttachmentPartImpl() {
      initializeJavaActivationHandlers();
   }

   public AttachmentPartImpl(MIMEPart part) {
      this.mimePart = part;
      List<? extends Header> hdrs = part.getAllHeaders();
      Iterator var3 = hdrs.iterator();

      while(var3.hasNext()) {
         Header hd = (Header)var3.next();
         this.headers.addHeader(hd.getName(), hd.getValue());
      }

   }

   public int getSize() throws SOAPException {
      if (this.mimePart != null) {
         try {
            return this.mimePart.read().available();
         } catch (IOException var4) {
            return -1;
         }
      } else if (this.rawContent == null && this.dataHandler == null) {
         return 0;
      } else if (this.rawContent != null) {
         try {
            return this.rawContent.getSize();
         } catch (Exception var5) {
            log.log(Level.SEVERE, (String)"SAAJ0573.soap.attachment.getrawbytes.ioexception", (Object[])(new String[]{var5.getLocalizedMessage()}));
            throw new SOAPExceptionImpl("Raw InputStream Error: " + var5);
         }
      } else {
         ByteOutputStream bout = new ByteOutputStream();

         try {
            this.dataHandler.writeTo(bout);
         } catch (IOException var6) {
            log.log(Level.SEVERE, (String)"SAAJ0501.soap.data.handler.err", (Object[])(new String[]{var6.getLocalizedMessage()}));
            throw new SOAPExceptionImpl("Data handler error: " + var6);
         }

         return bout.size();
      }
   }

   public void clearContent() {
      if (this.mimePart != null) {
         this.mimePart.close();
         this.mimePart = null;
      }

      this.dataHandler = null;
      this.rawContent = null;
   }

   public Object getContent() throws SOAPException {
      try {
         if (this.mimePart != null) {
            return this.mimePart.read();
         } else if (this.dataHandler != null) {
            return this.getDataHandler().getContent();
         } else if (this.rawContent != null) {
            return this.rawContent.getContent();
         } else {
            log.severe("SAAJ0572.soap.no.content.for.attachment");
            throw new SOAPExceptionImpl("No data handler/content associated with this attachment");
         }
      } catch (Exception var2) {
         log.log(Level.SEVERE, (String)"SAAJ0575.soap.attachment.getcontent.exception", (Throwable)var2);
         throw new SOAPExceptionImpl(var2.getLocalizedMessage());
      }
   }

   public void setContent(Object object, String contentType) throws IllegalArgumentException {
      if (this.mimePart != null) {
         this.mimePart.close();
         this.mimePart = null;
      }

      DataHandler dh = new DataHandler(object, contentType);
      this.setDataHandler(dh);
   }

   public DataHandler getDataHandler() throws SOAPException {
      if (this.mimePart != null) {
         return new DataHandler(new DataSource() {
            public InputStream getInputStream() throws IOException {
               return AttachmentPartImpl.this.mimePart.read();
            }

            public OutputStream getOutputStream() throws IOException {
               throw new UnsupportedOperationException("getOutputStream cannot be supported : You have enabled LazyAttachments Option");
            }

            public String getContentType() {
               return AttachmentPartImpl.this.mimePart.getContentType();
            }

            public String getName() {
               return "MIMEPart Wrapper DataSource";
            }
         });
      } else if (this.dataHandler == null) {
         if (this.rawContent != null) {
            return new DataHandler(new MimePartDataSource(this.rawContent));
         } else {
            log.severe("SAAJ0502.soap.no.handler.for.attachment");
            throw new SOAPExceptionImpl("No data handler associated with this attachment");
         }
      } else {
         return this.dataHandler;
      }
   }

   public void setDataHandler(DataHandler dataHandler) throws IllegalArgumentException {
      if (this.mimePart != null) {
         this.mimePart.close();
         this.mimePart = null;
      }

      if (dataHandler == null) {
         log.severe("SAAJ0503.soap.no.null.to.dataHandler");
         throw new IllegalArgumentException("Null dataHandler argument to setDataHandler");
      } else {
         this.dataHandler = dataHandler;
         this.rawContent = null;
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, (String)"SAAJ0580.soap.set.Content-Type", (Object[])(new String[]{dataHandler.getContentType()}));
         }

         this.setMimeHeader("Content-Type", dataHandler.getContentType());
      }
   }

   public void removeAllMimeHeaders() {
      this.headers.removeAllHeaders();
   }

   public void removeMimeHeader(String header) {
      this.headers.removeHeader(header);
   }

   public String[] getMimeHeader(String name) {
      return this.headers.getHeader(name);
   }

   public void setMimeHeader(String name, String value) {
      this.headers.setHeader(name, value);
   }

   public void addMimeHeader(String name, String value) {
      this.headers.addHeader(name, value);
   }

   public Iterator getAllMimeHeaders() {
      return this.headers.getAllHeaders();
   }

   public Iterator getMatchingMimeHeaders(String[] names) {
      return this.headers.getMatchingHeaders(names);
   }

   public Iterator getNonMatchingMimeHeaders(String[] names) {
      return this.headers.getNonMatchingHeaders(names);
   }

   boolean hasAllHeaders(MimeHeaders hdrs) {
      if (hdrs != null) {
         Iterator i = hdrs.getAllHeaders();

         while(i.hasNext()) {
            MimeHeader hdr = (MimeHeader)i.next();
            String[] values = this.headers.getHeader(hdr.getName());
            boolean found = false;
            if (values != null) {
               for(int j = 0; j < values.length; ++j) {
                  if (hdr.getValue().equalsIgnoreCase(values[j])) {
                     found = true;
                     break;
                  }
               }
            }

            if (!found) {
               return false;
            }
         }
      }

      return true;
   }

   MimeBodyPart getMimePart() throws SOAPException {
      try {
         if (this.mimePart != null) {
            return new MimeBodyPart(this.mimePart);
         } else if (this.rawContent != null) {
            copyMimeHeaders(this.headers, this.rawContent);
            return this.rawContent;
         } else {
            MimeBodyPart envelope = new MimeBodyPart();
            envelope.setDataHandler(this.dataHandler);
            copyMimeHeaders(this.headers, envelope);
            return envelope;
         }
      } catch (Exception var2) {
         log.severe("SAAJ0504.soap.cannot.externalize.attachment");
         throw new SOAPExceptionImpl("Unable to externalize attachment", var2);
      }
   }

   public static void copyMimeHeaders(MimeHeaders headers, MimeBodyPart mbp) throws SOAPException {
      Iterator i = headers.getAllHeaders();

      while(i.hasNext()) {
         try {
            MimeHeader mh = (MimeHeader)i.next();
            mbp.setHeader(mh.getName(), mh.getValue());
         } catch (Exception var4) {
            log.severe("SAAJ0505.soap.cannot.copy.mime.hdr");
            throw new SOAPExceptionImpl("Unable to copy MIME header", var4);
         }
      }

   }

   public static void copyMimeHeaders(MimeBodyPart mbp, AttachmentPartImpl ap) throws SOAPException {
      try {
         List hdr = mbp.getAllHeaders();
         int sz = hdr.size();

         for(int i = 0; i < sz; ++i) {
            com.sun.xml.internal.messaging.saaj.packaging.mime.Header h = (com.sun.xml.internal.messaging.saaj.packaging.mime.Header)hdr.get(i);
            if (!h.getName().equalsIgnoreCase("Content-Type")) {
               ap.addMimeHeader(h.getName(), h.getValue());
            }
         }

      } catch (Exception var6) {
         log.severe("SAAJ0506.soap.cannot.copy.mime.hdrs.into.attachment");
         throw new SOAPExceptionImpl("Unable to copy MIME headers into attachment", var6);
      }
   }

   public void setBase64Content(InputStream content, String contentType) throws SOAPException {
      if (this.mimePart != null) {
         this.mimePart.close();
         this.mimePart = null;
      }

      this.dataHandler = null;
      InputStream decoded = null;

      try {
         decoded = MimeUtility.decode(content, "base64");
         InternetHeaders hdrs = new InternetHeaders();
         hdrs.setHeader("Content-Type", contentType);
         ByteOutputStream bos = new ByteOutputStream();
         bos.write(decoded);
         this.rawContent = new MimeBodyPart(hdrs, bos.getBytes(), bos.getCount());
         this.setMimeHeader("Content-Type", contentType);
      } catch (Exception var13) {
         log.log(Level.SEVERE, (String)"SAAJ0578.soap.attachment.setbase64content.exception", (Throwable)var13);
         throw new SOAPExceptionImpl(var13.getLocalizedMessage());
      } finally {
         try {
            decoded.close();
         } catch (IOException var12) {
            throw new SOAPException(var12);
         }
      }

   }

   public InputStream getBase64Content() throws SOAPException {
      InputStream stream;
      if (this.mimePart != null) {
         stream = this.mimePart.read();
      } else if (this.rawContent != null) {
         try {
            stream = this.rawContent.getInputStream();
         } catch (Exception var19) {
            log.log(Level.SEVERE, (String)"SAAJ0579.soap.attachment.getbase64content.exception", (Throwable)var19);
            throw new SOAPExceptionImpl(var19.getLocalizedMessage());
         }
      } else {
         if (this.dataHandler == null) {
            log.severe("SAAJ0572.soap.no.content.for.attachment");
            throw new SOAPExceptionImpl("No data handler/content associated with this attachment");
         }

         try {
            stream = this.dataHandler.getInputStream();
         } catch (IOException var18) {
            log.severe("SAAJ0574.soap.attachment.datahandler.ioexception");
            throw new SOAPExceptionImpl("DataHandler error" + var18);
         }
      }

      int size = 1024;
      if (stream != null) {
         ByteArrayInputStream var7;
         try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(size);
            OutputStream ret = MimeUtility.encode(bos, "base64");
            byte[] buf = new byte[size];

            int len;
            while((len = stream.read(buf, 0, size)) != -1) {
               ret.write(buf, 0, len);
            }

            ret.flush();
            buf = bos.toByteArray();
            var7 = new ByteArrayInputStream(buf);
         } catch (Exception var20) {
            log.log(Level.SEVERE, (String)"SAAJ0579.soap.attachment.getbase64content.exception", (Throwable)var20);
            throw new SOAPExceptionImpl(var20.getLocalizedMessage());
         } finally {
            try {
               stream.close();
            } catch (IOException var17) {
            }

         }

         return var7;
      } else {
         log.log(Level.SEVERE, "SAAJ0572.soap.no.content.for.attachment");
         throw new SOAPExceptionImpl("No data handler/content associated with this attachment");
      }
   }

   public void setRawContent(InputStream content, String contentType) throws SOAPException {
      if (this.mimePart != null) {
         this.mimePart.close();
         this.mimePart = null;
      }

      this.dataHandler = null;

      try {
         InternetHeaders hdrs = new InternetHeaders();
         hdrs.setHeader("Content-Type", contentType);
         ByteOutputStream bos = new ByteOutputStream();
         bos.write(content);
         this.rawContent = new MimeBodyPart(hdrs, bos.getBytes(), bos.getCount());
         this.setMimeHeader("Content-Type", contentType);
      } catch (Exception var12) {
         log.log(Level.SEVERE, (String)"SAAJ0576.soap.attachment.setrawcontent.exception", (Throwable)var12);
         throw new SOAPExceptionImpl(var12.getLocalizedMessage());
      } finally {
         try {
            content.close();
         } catch (IOException var11) {
            throw new SOAPException(var11);
         }
      }

   }

   public void setRawContentBytes(byte[] content, int off, int len, String contentType) throws SOAPException {
      if (this.mimePart != null) {
         this.mimePart.close();
         this.mimePart = null;
      }

      if (content == null) {
         throw new SOAPExceptionImpl("Null content passed to setRawContentBytes");
      } else {
         this.dataHandler = null;

         try {
            InternetHeaders hdrs = new InternetHeaders();
            hdrs.setHeader("Content-Type", contentType);
            this.rawContent = new MimeBodyPart(hdrs, content, off, len);
            this.setMimeHeader("Content-Type", contentType);
         } catch (Exception var6) {
            log.log(Level.SEVERE, (String)"SAAJ0576.soap.attachment.setrawcontent.exception", (Throwable)var6);
            throw new SOAPExceptionImpl(var6.getLocalizedMessage());
         }
      }
   }

   public InputStream getRawContent() throws SOAPException {
      if (this.mimePart != null) {
         return this.mimePart.read();
      } else if (this.rawContent != null) {
         try {
            return this.rawContent.getInputStream();
         } catch (Exception var2) {
            log.log(Level.SEVERE, (String)"SAAJ0577.soap.attachment.getrawcontent.exception", (Throwable)var2);
            throw new SOAPExceptionImpl(var2.getLocalizedMessage());
         }
      } else if (this.dataHandler != null) {
         try {
            return this.dataHandler.getInputStream();
         } catch (IOException var3) {
            log.severe("SAAJ0574.soap.attachment.datahandler.ioexception");
            throw new SOAPExceptionImpl("DataHandler error" + var3);
         }
      } else {
         log.severe("SAAJ0572.soap.no.content.for.attachment");
         throw new SOAPExceptionImpl("No data handler/content associated with this attachment");
      }
   }

   public byte[] getRawContentBytes() throws SOAPException {
      InputStream ret;
      if (this.mimePart != null) {
         try {
            ret = this.mimePart.read();
            return ASCIIUtility.getBytes(ret);
         } catch (IOException var3) {
            log.log(Level.SEVERE, (String)"SAAJ0577.soap.attachment.getrawcontent.exception", (Throwable)var3);
            throw new SOAPExceptionImpl(var3);
         }
      } else if (this.rawContent != null) {
         try {
            ret = this.rawContent.getInputStream();
            return ASCIIUtility.getBytes(ret);
         } catch (Exception var4) {
            log.log(Level.SEVERE, (String)"SAAJ0577.soap.attachment.getrawcontent.exception", (Throwable)var4);
            throw new SOAPExceptionImpl(var4);
         }
      } else if (this.dataHandler != null) {
         try {
            ret = this.dataHandler.getInputStream();
            return ASCIIUtility.getBytes(ret);
         } catch (IOException var5) {
            log.severe("SAAJ0574.soap.attachment.datahandler.ioexception");
            throw new SOAPExceptionImpl("DataHandler error" + var5);
         }
      } else {
         log.severe("SAAJ0572.soap.no.content.for.attachment");
         throw new SOAPExceptionImpl("No data handler/content associated with this attachment");
      }
   }

   public boolean equals(Object o) {
      return this == o;
   }

   public int hashCode() {
      return super.hashCode();
   }

   public MimeHeaders getMimeHeaders() {
      return this.headers;
   }

   public static void initializeJavaActivationHandlers() {
      try {
         CommandMap map = CommandMap.getDefaultCommandMap();
         if (map instanceof MailcapCommandMap) {
            MailcapCommandMap mailMap = (MailcapCommandMap)map;
            if (!cmdMapInitialized(mailMap)) {
               mailMap.addMailcap("text/xml;;x-java-content-handler=com.sun.xml.internal.messaging.saaj.soap.XmlDataContentHandler");
               mailMap.addMailcap("application/xml;;x-java-content-handler=com.sun.xml.internal.messaging.saaj.soap.XmlDataContentHandler");
               mailMap.addMailcap("application/fastinfoset;;x-java-content-handler=com.sun.xml.internal.messaging.saaj.soap.FastInfosetDataContentHandler");
               mailMap.addMailcap("image/*;;x-java-content-handler=com.sun.xml.internal.messaging.saaj.soap.ImageDataContentHandler");
               mailMap.addMailcap("text/plain;;x-java-content-handler=com.sun.xml.internal.messaging.saaj.soap.StringDataContentHandler");
            }
         }
      } catch (Throwable var2) {
      }

   }

   private static boolean cmdMapInitialized(MailcapCommandMap mailMap) {
      CommandInfo[] commands = mailMap.getAllCommands("application/fastinfoset");
      if (commands != null && commands.length != 0) {
         String saajClassName = "com.sun.xml.internal.ws.binding.FastInfosetDataContentHandler";
         CommandInfo[] var3 = commands;
         int var4 = commands.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            CommandInfo command = var3[var5];
            String commandClass = command.getCommandClass();
            if (saajClassName.equals(commandClass)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }
}
