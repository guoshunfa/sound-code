package com.sun.xml.internal.ws.message;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.encoding.DataSourceStreamingDataHandler;
import com.sun.xml.internal.ws.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataHandler;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

public final class ByteArrayAttachment implements Attachment {
   private final String contentId;
   private byte[] data;
   private int start;
   private final int len;
   private final String mimeType;

   public ByteArrayAttachment(@NotNull String contentId, byte[] data, int start, int len, String mimeType) {
      this.contentId = contentId;
      this.data = data;
      this.start = start;
      this.len = len;
      this.mimeType = mimeType;
   }

   public ByteArrayAttachment(@NotNull String contentId, byte[] data, String mimeType) {
      this(contentId, data, 0, data.length, mimeType);
   }

   public String getContentId() {
      return this.contentId;
   }

   public String getContentType() {
      return this.mimeType;
   }

   public byte[] asByteArray() {
      if (this.start != 0 || this.len != this.data.length) {
         byte[] exact = new byte[this.len];
         System.arraycopy(this.data, this.start, exact, 0, this.len);
         this.start = 0;
         this.data = exact;
      }

      return this.data;
   }

   public DataHandler asDataHandler() {
      return new DataSourceStreamingDataHandler(new ByteArrayDataSource(this.data, this.start, this.len, this.getContentType()));
   }

   public Source asSource() {
      return new StreamSource(this.asInputStream());
   }

   public InputStream asInputStream() {
      return new ByteArrayInputStream(this.data, this.start, this.len);
   }

   public void writeTo(OutputStream os) throws IOException {
      os.write(this.asByteArray());
   }

   public void writeTo(SOAPMessage saaj) throws SOAPException {
      AttachmentPart part = saaj.createAttachmentPart();
      part.setDataHandler(this.asDataHandler());
      part.setContentId(this.contentId);
      saaj.addAttachmentPart(part);
   }
}
