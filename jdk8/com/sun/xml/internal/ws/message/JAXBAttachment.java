package com.sun.xml.internal.ws.message;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.encoding.DataSourceStreamingDataHandler;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.bind.JAXBException;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceException;

public final class JAXBAttachment implements Attachment, DataSource {
   private final String contentId;
   private final String mimeType;
   private final Object jaxbObject;
   private final XMLBridge bridge;

   public JAXBAttachment(@NotNull String contentId, Object jaxbObject, XMLBridge bridge, String mimeType) {
      this.contentId = contentId;
      this.jaxbObject = jaxbObject;
      this.bridge = bridge;
      this.mimeType = mimeType;
   }

   public String getContentId() {
      return this.contentId;
   }

   public String getContentType() {
      return this.mimeType;
   }

   public byte[] asByteArray() {
      ByteArrayBuffer bab = new ByteArrayBuffer();

      try {
         this.writeTo((OutputStream)bab);
      } catch (IOException var3) {
         throw new WebServiceException(var3);
      }

      return bab.getRawData();
   }

   public DataHandler asDataHandler() {
      return new DataSourceStreamingDataHandler(this);
   }

   public Source asSource() {
      return new StreamSource(this.asInputStream());
   }

   public InputStream asInputStream() {
      ByteArrayBuffer bab = new ByteArrayBuffer();

      try {
         this.writeTo((OutputStream)bab);
      } catch (IOException var3) {
         throw new WebServiceException(var3);
      }

      return bab.newInputStream();
   }

   public void writeTo(OutputStream os) throws IOException {
      try {
         this.bridge.marshal(this.jaxbObject, os, (NamespaceContext)null, (AttachmentMarshaller)null);
      } catch (JAXBException var3) {
         throw new WebServiceException(var3);
      }
   }

   public void writeTo(SOAPMessage saaj) throws SOAPException {
      AttachmentPart part = saaj.createAttachmentPart();
      part.setDataHandler(this.asDataHandler());
      part.setContentId(this.contentId);
      saaj.addAttachmentPart(part);
   }

   public InputStream getInputStream() throws IOException {
      return this.asInputStream();
   }

   public OutputStream getOutputStream() throws IOException {
      throw new UnsupportedOperationException();
   }

   public String getName() {
      return null;
   }
}
