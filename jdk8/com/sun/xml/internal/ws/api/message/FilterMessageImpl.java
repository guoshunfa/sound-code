package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
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

public class FilterMessageImpl extends Message {
   private final Message delegate;

   protected FilterMessageImpl(Message delegate) {
      this.delegate = delegate;
   }

   public boolean hasHeaders() {
      return this.delegate.hasHeaders();
   }

   @NotNull
   public MessageHeaders getHeaders() {
      return this.delegate.getHeaders();
   }

   @NotNull
   public AttachmentSet getAttachments() {
      return this.delegate.getAttachments();
   }

   protected boolean hasAttachments() {
      return this.delegate.hasAttachments();
   }

   public boolean isOneWay(@NotNull WSDLPort port) {
      return this.delegate.isOneWay(port);
   }

   @Nullable
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

   @Nullable
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
      return this.delegate.readAsSOAPMessage();
   }

   public SOAPMessage readAsSOAPMessage(Packet packet, boolean inbound) throws SOAPException {
      return this.delegate.readAsSOAPMessage(packet, inbound);
   }

   public <T> T readPayloadAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
      return this.delegate.readPayloadAsJAXB(unmarshaller);
   }

   /** @deprecated */
   public <T> T readPayloadAsJAXB(Bridge<T> bridge) throws JAXBException {
      return this.delegate.readPayloadAsJAXB(bridge);
   }

   public <T> T readPayloadAsJAXB(XMLBridge<T> bridge) throws JAXBException {
      return this.delegate.readPayloadAsJAXB(bridge);
   }

   public XMLStreamReader readPayload() throws XMLStreamException {
      return this.delegate.readPayload();
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

   @NotNull
   public String getID(@NotNull WSBinding binding) {
      return this.delegate.getID(binding);
   }

   @NotNull
   public String getID(AddressingVersion av, SOAPVersion sv) {
      return this.delegate.getID(av, sv);
   }

   public SOAPVersion getSOAPVersion() {
      return this.delegate.getSOAPVersion();
   }
}
