package com.sun.xml.internal.ws.api.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.message.AbstractHeaderImpl;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

final class EPRHeader extends AbstractHeaderImpl {
   private final String nsUri;
   private final String localName;
   private final WSEndpointReference epr;

   EPRHeader(QName tagName, WSEndpointReference epr) {
      this.nsUri = tagName.getNamespaceURI();
      this.localName = tagName.getLocalPart();
      this.epr = epr;
   }

   @NotNull
   public String getNamespaceURI() {
      return this.nsUri;
   }

   @NotNull
   public String getLocalPart() {
      return this.localName;
   }

   @Nullable
   public String getAttribute(@NotNull String nsUri, @NotNull String localName) {
      try {
         XMLStreamReader sr = this.epr.read("EndpointReference");

         while(sr.getEventType() != 1) {
            sr.next();
         }

         return sr.getAttributeValue(nsUri, localName);
      } catch (XMLStreamException var4) {
         throw new AssertionError(var4);
      }
   }

   public XMLStreamReader readHeader() throws XMLStreamException {
      return this.epr.read(this.localName);
   }

   public void writeTo(XMLStreamWriter w) throws XMLStreamException {
      this.epr.writeTo(this.localName, w);
   }

   public void writeTo(SOAPMessage saaj) throws SOAPException {
      try {
         Transformer t = XmlUtil.newTransformer();
         SOAPHeader header = saaj.getSOAPHeader();
         if (header == null) {
            header = saaj.getSOAPPart().getEnvelope().addHeader();
         }

         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         XMLStreamWriter w = XMLOutputFactory.newFactory().createXMLStreamWriter((OutputStream)baos);
         this.epr.writeTo(this.localName, w);
         w.flush();
         ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
         DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
         fac.setNamespaceAware(true);
         Node eprNode = fac.newDocumentBuilder().parse((InputStream)bais).getDocumentElement();
         Node eprNodeToAdd = header.getOwnerDocument().importNode(eprNode, true);
         header.appendChild(eprNodeToAdd);
      } catch (Exception var10) {
         throw new SOAPException(var10);
      }
   }

   public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
      this.epr.writeTo(this.localName, contentHandler, errorHandler, true);
   }
}
