package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownServiceException;
import javax.activation.DataSource;

public final class MimePartDataSource implements DataSource {
   private final MimeBodyPart part;

   public MimePartDataSource(MimeBodyPart part) {
      this.part = part;
   }

   public InputStream getInputStream() throws IOException {
      try {
         InputStream is = this.part.getContentStream();
         String encoding = this.part.getEncoding();
         return encoding != null ? MimeUtility.decode(is, encoding) : is;
      } catch (MessagingException var3) {
         throw new IOException(var3.getMessage());
      }
   }

   public OutputStream getOutputStream() throws IOException {
      throw new UnknownServiceException();
   }

   public String getContentType() {
      return this.part.getContentType();
   }

   public String getName() {
      try {
         return this.part.getFileName();
      } catch (MessagingException var2) {
         return "";
      }
   }
}
