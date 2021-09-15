package com.sun.xml.internal.ws.message;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Attachment;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataHandler;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceException;

public final class DataHandlerAttachment implements Attachment {
   private final DataHandler dh;
   private final String contentId;
   String contentIdNoAngleBracket;

   public DataHandlerAttachment(@NotNull String contentId, @NotNull DataHandler dh) {
      this.dh = dh;
      this.contentId = contentId;
   }

   public String getContentId() {
      if (this.contentIdNoAngleBracket == null) {
         this.contentIdNoAngleBracket = this.contentId;
         if (this.contentIdNoAngleBracket != null && this.contentIdNoAngleBracket.charAt(0) == '<') {
            this.contentIdNoAngleBracket = this.contentIdNoAngleBracket.substring(1, this.contentIdNoAngleBracket.length() - 1);
         }
      }

      return this.contentIdNoAngleBracket;
   }

   public String getContentType() {
      return this.dh.getContentType();
   }

   public byte[] asByteArray() {
      try {
         ByteArrayOutputStream os = new ByteArrayOutputStream();
         this.dh.writeTo(os);
         return os.toByteArray();
      } catch (IOException var2) {
         throw new WebServiceException(var2);
      }
   }

   public DataHandler asDataHandler() {
      return this.dh;
   }

   public Source asSource() {
      try {
         return new StreamSource(this.dh.getInputStream());
      } catch (IOException var2) {
         throw new WebServiceException(var2);
      }
   }

   public InputStream asInputStream() {
      try {
         return this.dh.getInputStream();
      } catch (IOException var2) {
         throw new WebServiceException(var2);
      }
   }

   public void writeTo(OutputStream os) throws IOException {
      this.dh.writeTo(os);
   }

   public void writeTo(SOAPMessage saaj) throws SOAPException {
      AttachmentPart part = saaj.createAttachmentPart();
      part.setDataHandler(this.dh);
      part.setContentId(this.contentId);
      saaj.addAttachmentPart(part);
   }
}
