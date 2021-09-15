package com.sun.xml.internal.ws.message.jaxb;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.XMLStreamException2;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.message.AbstractHeaderImpl;
import com.sun.xml.internal.ws.message.RootElementSniffer;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.streaming.XMLStreamWriterUtil;
import java.io.OutputStream;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.bind.util.JAXBResult;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public final class JAXBHeader extends AbstractHeaderImpl {
   private final Object jaxbObject;
   private final XMLBridge bridge;
   private String nsUri;
   private String localName;
   private Attributes atts;
   private XMLStreamBuffer infoset;

   public JAXBHeader(BindingContext context, Object jaxbObject) {
      this.jaxbObject = jaxbObject;
      this.bridge = context.createFragmentBridge();
      if (jaxbObject instanceof JAXBElement) {
         JAXBElement e = (JAXBElement)jaxbObject;
         this.nsUri = e.getName().getNamespaceURI();
         this.localName = e.getName().getLocalPart();
      }

   }

   public JAXBHeader(XMLBridge bridge, Object jaxbObject) {
      this.jaxbObject = jaxbObject;
      this.bridge = bridge;
      QName tagName = bridge.getTypeInfo().tagName;
      this.nsUri = tagName.getNamespaceURI();
      this.localName = tagName.getLocalPart();
   }

   private void parse() {
      RootElementSniffer sniffer = new RootElementSniffer();

      try {
         this.bridge.marshal(this.jaxbObject, (ContentHandler)sniffer, (AttachmentMarshaller)null);
      } catch (JAXBException var3) {
         this.nsUri = sniffer.getNsUri();
         this.localName = sniffer.getLocalName();
         this.atts = sniffer.getAttributes();
      }

   }

   @NotNull
   public String getNamespaceURI() {
      if (this.nsUri == null) {
         this.parse();
      }

      return this.nsUri;
   }

   @NotNull
   public String getLocalPart() {
      if (this.localName == null) {
         this.parse();
      }

      return this.localName;
   }

   public String getAttribute(String nsUri, String localName) {
      if (this.atts == null) {
         this.parse();
      }

      return this.atts.getValue(nsUri, localName);
   }

   public XMLStreamReader readHeader() throws XMLStreamException {
      if (this.infoset == null) {
         MutableXMLStreamBuffer buffer = new MutableXMLStreamBuffer();
         this.writeTo(buffer.createFromXMLStreamWriter());
         this.infoset = buffer;
      }

      return this.infoset.readAsXMLStreamReader();
   }

   public <T> T readAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
      try {
         JAXBResult r = new JAXBResult(unmarshaller);
         r.getHandler().startDocument();
         this.bridge.marshal(this.jaxbObject, (Result)r);
         r.getHandler().endDocument();
         return r.getResult();
      } catch (SAXException var3) {
         throw new JAXBException(var3);
      }
   }

   /** @deprecated */
   public <T> T readAsJAXB(Bridge<T> bridge) throws JAXBException {
      return bridge.unmarshal((Source)(new JAXBBridgeSource(this.bridge, this.jaxbObject)));
   }

   public <T> T readAsJAXB(XMLBridge<T> bond) throws JAXBException {
      return bond.unmarshal((Source)(new JAXBBridgeSource(this.bridge, this.jaxbObject)), (AttachmentUnmarshaller)null);
   }

   public void writeTo(XMLStreamWriter sw) throws XMLStreamException {
      try {
         String encoding = XMLStreamWriterUtil.getEncoding(sw);
         OutputStream os = this.bridge.supportOutputStream() ? XMLStreamWriterUtil.getOutputStream(sw) : null;
         if (os != null && encoding != null && encoding.equalsIgnoreCase("utf-8")) {
            this.bridge.marshal(this.jaxbObject, os, sw.getNamespaceContext(), (AttachmentMarshaller)null);
         } else {
            this.bridge.marshal(this.jaxbObject, (XMLStreamWriter)sw, (AttachmentMarshaller)null);
         }

      } catch (JAXBException var4) {
         throw new XMLStreamException2(var4);
      }
   }

   public void writeTo(SOAPMessage saaj) throws SOAPException {
      try {
         SOAPHeader header = saaj.getSOAPHeader();
         if (header == null) {
            header = saaj.getSOAPPart().getEnvelope().addHeader();
         }

         this.bridge.marshal(this.jaxbObject, (Node)header);
      } catch (JAXBException var3) {
         throw new SOAPException(var3);
      }
   }

   public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
      try {
         this.bridge.marshal(this.jaxbObject, (ContentHandler)contentHandler, (AttachmentMarshaller)null);
      } catch (JAXBException var5) {
         SAXParseException x = new SAXParseException(var5.getMessage(), (String)null, (String)null, -1, -1, var5);
         errorHandler.fatalError(x);
         throw x;
      }
   }
}
