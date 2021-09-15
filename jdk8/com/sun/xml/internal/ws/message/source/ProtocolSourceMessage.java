package com.sun.xml.internal.ws.message.source;

import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codecs;
import com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.streaming.SourceReaderFactory;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public class ProtocolSourceMessage extends Message {
   private final Message sm;

   public ProtocolSourceMessage(Source source, SOAPVersion soapVersion) {
      XMLStreamReader reader = SourceReaderFactory.createSourceReader(source, true);
      StreamSOAPCodec codec = Codecs.createSOAPEnvelopeXmlCodec(soapVersion);
      this.sm = codec.decode(reader);
   }

   public boolean hasHeaders() {
      return this.sm.hasHeaders();
   }

   public String getPayloadLocalPart() {
      return this.sm.getPayloadLocalPart();
   }

   public String getPayloadNamespaceURI() {
      return this.sm.getPayloadNamespaceURI();
   }

   public boolean hasPayload() {
      return this.sm.hasPayload();
   }

   public Source readPayloadAsSource() {
      return this.sm.readPayloadAsSource();
   }

   public XMLStreamReader readPayload() throws XMLStreamException {
      return this.sm.readPayload();
   }

   public void writePayloadTo(XMLStreamWriter sw) throws XMLStreamException {
      this.sm.writePayloadTo(sw);
   }

   public void writeTo(XMLStreamWriter sw) throws XMLStreamException {
      this.sm.writeTo(sw);
   }

   public Message copy() {
      return this.sm.copy();
   }

   public Source readEnvelopeAsSource() {
      return this.sm.readEnvelopeAsSource();
   }

   public SOAPMessage readAsSOAPMessage() throws SOAPException {
      return this.sm.readAsSOAPMessage();
   }

   public SOAPMessage readAsSOAPMessage(Packet packet, boolean inbound) throws SOAPException {
      return this.sm.readAsSOAPMessage(packet, inbound);
   }

   public <T> T readPayloadAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
      return this.sm.readPayloadAsJAXB(unmarshaller);
   }

   /** @deprecated */
   public <T> T readPayloadAsJAXB(Bridge<T> bridge) throws JAXBException {
      return this.sm.readPayloadAsJAXB(bridge);
   }

   public <T> T readPayloadAsJAXB(XMLBridge<T> bridge) throws JAXBException {
      return this.sm.readPayloadAsJAXB(bridge);
   }

   public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
      this.sm.writeTo(contentHandler, errorHandler);
   }

   public SOAPVersion getSOAPVersion() {
      return this.sm.getSOAPVersion();
   }

   public MessageHeaders getHeaders() {
      return this.sm.getHeaders();
   }
}
