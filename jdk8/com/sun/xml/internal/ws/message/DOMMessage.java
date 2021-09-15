package com.sun.xml.internal.ws.message;

import com.sun.istack.internal.FragmentContentHandler;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.unmarshaller.DOMScanner;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.HeaderList;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.streaming.DOMStreamReader;
import com.sun.xml.internal.ws.util.DOMUtil;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public final class DOMMessage extends AbstractMessageImpl {
   private MessageHeaders headers;
   private final Element payload;

   public DOMMessage(SOAPVersion ver, Element payload) {
      this(ver, (MessageHeaders)null, payload);
   }

   public DOMMessage(SOAPVersion ver, MessageHeaders headers, Element payload) {
      this(ver, headers, payload, (AttachmentSet)null);
   }

   public DOMMessage(SOAPVersion ver, MessageHeaders headers, Element payload, AttachmentSet attachments) {
      super(ver);
      this.headers = headers;
      this.payload = payload;
      this.attachmentSet = attachments;

      assert payload != null;

   }

   private DOMMessage(DOMMessage that) {
      super((AbstractMessageImpl)that);
      this.headers = HeaderList.copy(that.headers);
      this.payload = that.payload;
   }

   public boolean hasHeaders() {
      return this.getHeaders().hasHeaders();
   }

   public MessageHeaders getHeaders() {
      if (this.headers == null) {
         this.headers = new HeaderList(this.getSOAPVersion());
      }

      return this.headers;
   }

   public String getPayloadLocalPart() {
      return this.payload.getLocalName();
   }

   public String getPayloadNamespaceURI() {
      return this.payload.getNamespaceURI();
   }

   public boolean hasPayload() {
      return true;
   }

   public Source readPayloadAsSource() {
      return new DOMSource(this.payload);
   }

   public <T> T readPayloadAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
      if (this.hasAttachments()) {
         unmarshaller.setAttachmentUnmarshaller(new AttachmentUnmarshallerImpl(this.getAttachments()));
      }

      Object var2;
      try {
         var2 = unmarshaller.unmarshal((Node)this.payload);
      } finally {
         unmarshaller.setAttachmentUnmarshaller((AttachmentUnmarshaller)null);
      }

      return var2;
   }

   /** @deprecated */
   public <T> T readPayloadAsJAXB(Bridge<T> bridge) throws JAXBException {
      return bridge.unmarshal((Node)this.payload, (AttachmentUnmarshaller)(this.hasAttachments() ? new AttachmentUnmarshallerImpl(this.getAttachments()) : null));
   }

   public XMLStreamReader readPayload() throws XMLStreamException {
      DOMStreamReader dss = new DOMStreamReader();
      dss.setCurrentNode(this.payload);
      dss.nextTag();

      assert dss.getEventType() == 1;

      return dss;
   }

   public void writePayloadTo(XMLStreamWriter sw) {
      try {
         if (this.payload != null) {
            DOMUtil.serializeNode(this.payload, sw);
         }

      } catch (XMLStreamException var3) {
         throw new WebServiceException(var3);
      }
   }

   protected void writePayloadTo(ContentHandler contentHandler, ErrorHandler errorHandler, boolean fragment) throws SAXException {
      if (fragment) {
         contentHandler = new FragmentContentHandler((ContentHandler)contentHandler);
      }

      DOMScanner ds = new DOMScanner();
      ds.setContentHandler((ContentHandler)contentHandler);
      ds.scan(this.payload);
   }

   public Message copy() {
      return new DOMMessage(this);
   }
}
