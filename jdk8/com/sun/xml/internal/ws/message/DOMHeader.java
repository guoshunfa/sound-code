package com.sun.xml.internal.ws.message;

import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.unmarshaller.DOMScanner;
import com.sun.xml.internal.ws.streaming.DOMStreamReader;
import com.sun.xml.internal.ws.util.DOMUtil;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public class DOMHeader<N extends Element> extends AbstractHeaderImpl {
   protected final N node;
   private final String nsUri;
   private final String localName;

   public DOMHeader(N node) {
      assert node != null;

      this.node = node;
      this.nsUri = fixNull(node.getNamespaceURI());
      this.localName = node.getLocalName();
   }

   public String getNamespaceURI() {
      return this.nsUri;
   }

   public String getLocalPart() {
      return this.localName;
   }

   public XMLStreamReader readHeader() throws XMLStreamException {
      DOMStreamReader r = new DOMStreamReader(this.node);
      r.nextTag();
      return r;
   }

   public <T> T readAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
      return unmarshaller.unmarshal((Node)this.node);
   }

   /** @deprecated */
   public <T> T readAsJAXB(Bridge<T> bridge) throws JAXBException {
      return bridge.unmarshal((Node)this.node);
   }

   public void writeTo(XMLStreamWriter w) throws XMLStreamException {
      DOMUtil.serializeNode(this.node, w);
   }

   private static String fixNull(String s) {
      return s != null ? s : "";
   }

   public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
      DOMScanner ds = new DOMScanner();
      ds.setContentHandler(contentHandler);
      ds.scan(this.node);
   }

   public String getAttribute(String nsUri, String localName) {
      if (nsUri.length() == 0) {
         nsUri = null;
      }

      return this.node.getAttributeNS(nsUri, localName);
   }

   public void writeTo(SOAPMessage saaj) throws SOAPException {
      SOAPHeader header = saaj.getSOAPHeader();
      if (header == null) {
         header = saaj.getSOAPPart().getEnvelope().addHeader();
      }

      Node clone = header.getOwnerDocument().importNode(this.node, true);
      header.appendChild(clone);
   }

   public String getStringContent() {
      return this.node.getTextContent();
   }

   public N getWrappedNode() {
      return this.node;
   }

   public int hashCode() {
      return this.getWrappedNode().hashCode();
   }

   public boolean equals(Object obj) {
      return obj instanceof DOMHeader ? this.getWrappedNode().equals(((DOMHeader)obj).getWrappedNode()) : false;
   }
}
