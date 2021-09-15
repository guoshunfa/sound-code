package com.sun.xml.internal.ws.message;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.MessageWritable;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.message.saaj.SAAJFactory;
import com.sun.xml.internal.ws.encoding.TagInfoset;
import com.sun.xml.internal.ws.message.saaj.SAAJMessage;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.LocatorImpl;

public abstract class AbstractMessageImpl extends Message {
   protected final SOAPVersion soapVersion;
   @NotNull
   protected TagInfoset envelopeTag;
   @NotNull
   protected TagInfoset headerTag;
   @NotNull
   protected TagInfoset bodyTag;
   protected static final AttributesImpl EMPTY_ATTS = new AttributesImpl();
   protected static final LocatorImpl NULL_LOCATOR = new LocatorImpl();
   protected static final List<TagInfoset> DEFAULT_TAGS;

   static void create(SOAPVersion v, List c) {
      int base = v.ordinal() * 3;
      c.add(base, new TagInfoset(v.nsUri, "Envelope", "S", EMPTY_ATTS, new String[]{"S", v.nsUri}));
      c.add(base + 1, new TagInfoset(v.nsUri, "Header", "S", EMPTY_ATTS, new String[0]));
      c.add(base + 2, new TagInfoset(v.nsUri, "Body", "S", EMPTY_ATTS, new String[0]));
   }

   protected AbstractMessageImpl(SOAPVersion soapVersion) {
      this.soapVersion = soapVersion;
   }

   public SOAPVersion getSOAPVersion() {
      return this.soapVersion;
   }

   protected AbstractMessageImpl(AbstractMessageImpl that) {
      this.soapVersion = that.soapVersion;
   }

   public Source readEnvelopeAsSource() {
      return new SAXSource(new XMLReaderImpl(this), XMLReaderImpl.THE_SOURCE);
   }

   public <T> T readPayloadAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
      if (this.hasAttachments()) {
         unmarshaller.setAttachmentUnmarshaller(new AttachmentUnmarshallerImpl(this.getAttachments()));
      }

      Object var2;
      try {
         var2 = unmarshaller.unmarshal(this.readPayloadAsSource());
      } finally {
         unmarshaller.setAttachmentUnmarshaller((AttachmentUnmarshaller)null);
      }

      return var2;
   }

   /** @deprecated */
   public <T> T readPayloadAsJAXB(Bridge<T> bridge) throws JAXBException {
      return bridge.unmarshal((Source)this.readPayloadAsSource(), (AttachmentUnmarshaller)(this.hasAttachments() ? new AttachmentUnmarshallerImpl(this.getAttachments()) : null));
   }

   public <T> T readPayloadAsJAXB(XMLBridge<T> bridge) throws JAXBException {
      return bridge.unmarshal((Source)this.readPayloadAsSource(), this.hasAttachments() ? new AttachmentUnmarshallerImpl(this.getAttachments()) : null);
   }

   public void writeTo(XMLStreamWriter w) throws XMLStreamException {
      String soapNsUri = this.soapVersion.nsUri;
      w.writeStartDocument();
      w.writeStartElement("S", "Envelope", soapNsUri);
      w.writeNamespace("S", soapNsUri);
      if (this.hasHeaders()) {
         w.writeStartElement("S", "Header", soapNsUri);
         MessageHeaders headers = this.getHeaders();
         Iterator var4 = headers.asList().iterator();

         while(var4.hasNext()) {
            Header h = (Header)var4.next();
            h.writeTo(w);
         }

         w.writeEndElement();
      }

      w.writeStartElement("S", "Body", soapNsUri);
      this.writePayloadTo(w);
      w.writeEndElement();
      w.writeEndElement();
      w.writeEndDocument();
   }

   public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
      String soapNsUri = this.soapVersion.nsUri;
      contentHandler.setDocumentLocator(NULL_LOCATOR);
      contentHandler.startDocument();
      contentHandler.startPrefixMapping("S", soapNsUri);
      contentHandler.startElement(soapNsUri, "Envelope", "S:Envelope", EMPTY_ATTS);
      if (this.hasHeaders()) {
         contentHandler.startElement(soapNsUri, "Header", "S:Header", EMPTY_ATTS);
         MessageHeaders headers = this.getHeaders();
         Iterator var5 = headers.asList().iterator();

         while(var5.hasNext()) {
            Header h = (Header)var5.next();
            h.writeTo(contentHandler, errorHandler);
         }

         contentHandler.endElement(soapNsUri, "Header", "S:Header");
      }

      contentHandler.startElement(soapNsUri, "Body", "S:Body", EMPTY_ATTS);
      this.writePayloadTo(contentHandler, errorHandler, true);
      contentHandler.endElement(soapNsUri, "Body", "S:Body");
      contentHandler.endElement(soapNsUri, "Envelope", "S:Envelope");
   }

   protected abstract void writePayloadTo(ContentHandler var1, ErrorHandler var2, boolean var3) throws SAXException;

   public Message toSAAJ(Packet p, Boolean inbound) throws SOAPException {
      SAAJMessage message = SAAJFactory.read(p);
      if (message instanceof MessageWritable) {
         ((MessageWritable)message).setMTOMConfiguration(p.getMtomFeature());
      }

      if (inbound != null) {
         this.transportHeaders(p, inbound, message.readAsSOAPMessage());
      }

      return message;
   }

   public SOAPMessage readAsSOAPMessage() throws SOAPException {
      return SAAJFactory.read(this.soapVersion, this);
   }

   public SOAPMessage readAsSOAPMessage(Packet packet, boolean inbound) throws SOAPException {
      SOAPMessage msg = SAAJFactory.read(this.soapVersion, this, packet);
      this.transportHeaders(packet, inbound, msg);
      return msg;
   }

   private void transportHeaders(Packet packet, boolean inbound, SOAPMessage msg) throws SOAPException {
      Map<String, List<String>> headers = getTransportHeaders(packet, inbound);
      if (headers != null) {
         addSOAPMimeHeaders(msg.getMimeHeaders(), headers);
      }

      if (msg.saveRequired()) {
         msg.saveChanges();
      }

   }

   static {
      List<TagInfoset> tagList = new ArrayList();
      create(SOAPVersion.SOAP_11, tagList);
      create(SOAPVersion.SOAP_12, tagList);
      DEFAULT_TAGS = Collections.unmodifiableList(tagList);
   }
}
