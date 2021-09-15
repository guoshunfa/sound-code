package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.message.saaj.SAAJMessage;
import com.sun.xml.internal.ws.message.stream.StreamMessage;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

class MessageWrapper extends StreamMessage {
   Packet packet;
   Message delegate;
   StreamMessage streamDelegate;

   public void writePayloadTo(ContentHandler contentHandler, ErrorHandler errorHandler, boolean fragment) throws SAXException {
      this.streamDelegate.writePayloadTo(contentHandler, errorHandler, fragment);
   }

   public String getBodyPrologue() {
      return this.streamDelegate.getBodyPrologue();
   }

   public String getBodyEpilogue() {
      return this.streamDelegate.getBodyEpilogue();
   }

   MessageWrapper(Packet p, Message m) {
      super(m.getSOAPVersion());
      this.packet = p;
      this.delegate = m;
      this.streamDelegate = m instanceof StreamMessage ? (StreamMessage)m : null;
      this.setMessageMedadata(p);
   }

   public int hashCode() {
      return this.delegate.hashCode();
   }

   public boolean equals(Object obj) {
      return this.delegate.equals(obj);
   }

   public boolean hasHeaders() {
      return this.delegate.hasHeaders();
   }

   public AttachmentSet getAttachments() {
      return this.delegate.getAttachments();
   }

   public String toString() {
      return this.delegate.toString();
   }

   public boolean isOneWay(WSDLPort port) {
      return this.delegate.isOneWay(port);
   }

   public String getPayloadLocalPart() {
      return this.delegate.getPayloadLocalPart();
   }

   public String getPayloadNamespaceURI() {
      return this.delegate.getPayloadNamespaceURI();
   }

   public boolean hasPayload() {
      return this.delegate.hasPayload();
   }

   public boolean isFault() {
      return this.delegate.isFault();
   }

   public QName getFirstDetailEntryName() {
      return this.delegate.getFirstDetailEntryName();
   }

   public Source readEnvelopeAsSource() {
      return this.delegate.readEnvelopeAsSource();
   }

   public Source readPayloadAsSource() {
      return this.delegate.readPayloadAsSource();
   }

   public SOAPMessage readAsSOAPMessage() throws SOAPException {
      if (!(this.delegate instanceof SAAJMessage)) {
         this.delegate = this.toSAAJ(this.packet, (Boolean)null);
      }

      return this.delegate.readAsSOAPMessage();
   }

   public SOAPMessage readAsSOAPMessage(Packet p, boolean inbound) throws SOAPException {
      if (!(this.delegate instanceof SAAJMessage)) {
         this.delegate = this.toSAAJ(p, inbound);
      }

      return this.delegate.readAsSOAPMessage();
   }

   public Object readPayloadAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
      return this.delegate.readPayloadAsJAXB(unmarshaller);
   }

   public <T> T readPayloadAsJAXB(Bridge<T> bridge) throws JAXBException {
      return this.delegate.readPayloadAsJAXB(bridge);
   }

   public <T> T readPayloadAsJAXB(XMLBridge<T> bridge) throws JAXBException {
      return this.delegate.readPayloadAsJAXB(bridge);
   }

   public XMLStreamReader readPayload() {
      try {
         return this.delegate.readPayload();
      } catch (XMLStreamException var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public void consume() {
      this.delegate.consume();
   }

   public void writePayloadTo(XMLStreamWriter sw) throws XMLStreamException {
      this.delegate.writePayloadTo(sw);
   }

   public void writeTo(XMLStreamWriter sw) throws XMLStreamException {
      this.delegate.writeTo(sw);
   }

   public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
      this.delegate.writeTo(contentHandler, errorHandler);
   }

   public Message copy() {
      return this.delegate.copy();
   }

   public String getID(WSBinding binding) {
      return this.delegate.getID(binding);
   }

   public String getID(AddressingVersion av, SOAPVersion sv) {
      return this.delegate.getID(av, sv);
   }

   public SOAPVersion getSOAPVersion() {
      return this.delegate.getSOAPVersion();
   }

   @NotNull
   public MessageHeaders getHeaders() {
      return this.delegate.getHeaders();
   }

   public void setMessageMedadata(MessageMetadata metadata) {
      super.setMessageMedadata(metadata);
      this.delegate.setMessageMedadata(metadata);
   }
}
