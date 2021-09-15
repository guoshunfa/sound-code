package com.sun.xml.internal.ws.message.stream;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.message.AbstractMessageImpl;
import com.sun.xml.internal.ws.message.AttachmentSetImpl;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public class PayloadStreamReaderMessage extends AbstractMessageImpl {
   private final StreamMessage message;

   public PayloadStreamReaderMessage(XMLStreamReader reader, SOAPVersion soapVer) {
      this((MessageHeaders)null, reader, new AttachmentSetImpl(), soapVer);
   }

   public PayloadStreamReaderMessage(@Nullable MessageHeaders headers, @NotNull XMLStreamReader reader, @NotNull AttachmentSet attSet, @NotNull SOAPVersion soapVersion) {
      super(soapVersion);
      this.message = new StreamMessage(headers, attSet, reader, soapVersion);
   }

   public boolean hasHeaders() {
      return this.message.hasHeaders();
   }

   public AttachmentSet getAttachments() {
      return this.message.getAttachments();
   }

   public String getPayloadLocalPart() {
      return this.message.getPayloadLocalPart();
   }

   public String getPayloadNamespaceURI() {
      return this.message.getPayloadNamespaceURI();
   }

   public boolean hasPayload() {
      return true;
   }

   public Source readPayloadAsSource() {
      return this.message.readPayloadAsSource();
   }

   public XMLStreamReader readPayload() throws XMLStreamException {
      return this.message.readPayload();
   }

   public void writePayloadTo(XMLStreamWriter sw) throws XMLStreamException {
      this.message.writePayloadTo(sw);
   }

   public <T> T readPayloadAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
      return this.message.readPayloadAsJAXB(unmarshaller);
   }

   public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
      this.message.writeTo(contentHandler, errorHandler);
   }

   protected void writePayloadTo(ContentHandler contentHandler, ErrorHandler errorHandler, boolean fragment) throws SAXException {
      this.message.writePayloadTo(contentHandler, errorHandler, fragment);
   }

   public Message copy() {
      return this.message.copy();
   }

   @NotNull
   public MessageHeaders getHeaders() {
      return this.message.getHeaders();
   }
}
