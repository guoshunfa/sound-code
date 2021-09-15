package com.sun.xml.internal.ws.message;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.HeaderList;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public class EmptyMessageImpl extends AbstractMessageImpl {
   private final MessageHeaders headers;
   private final AttachmentSet attachmentSet;

   public EmptyMessageImpl(SOAPVersion version) {
      super(version);
      this.headers = new HeaderList(version);
      this.attachmentSet = new AttachmentSetImpl();
   }

   public EmptyMessageImpl(MessageHeaders headers, @NotNull AttachmentSet attachmentSet, SOAPVersion version) {
      super(version);
      if (headers == null) {
         headers = new HeaderList(version);
      }

      this.attachmentSet = attachmentSet;
      this.headers = (MessageHeaders)headers;
   }

   private EmptyMessageImpl(EmptyMessageImpl that) {
      super((AbstractMessageImpl)that);
      this.headers = new HeaderList(that.headers);
      this.attachmentSet = that.attachmentSet;
   }

   public boolean hasHeaders() {
      return this.headers.hasHeaders();
   }

   public MessageHeaders getHeaders() {
      return this.headers;
   }

   public String getPayloadLocalPart() {
      return null;
   }

   public String getPayloadNamespaceURI() {
      return null;
   }

   public boolean hasPayload() {
      return false;
   }

   public Source readPayloadAsSource() {
      return null;
   }

   public XMLStreamReader readPayload() throws XMLStreamException {
      return null;
   }

   public void writePayloadTo(XMLStreamWriter sw) throws XMLStreamException {
   }

   public void writePayloadTo(ContentHandler contentHandler, ErrorHandler errorHandler, boolean fragment) throws SAXException {
   }

   public Message copy() {
      return new EmptyMessageImpl(this);
   }
}
