package com.sun.xml.internal.ws.message.stream;

import com.sun.istack.internal.FinalArrayList;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferException;
import com.sun.xml.internal.ws.message.AbstractHeaderImpl;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public final class OutboundStreamHeader extends AbstractHeaderImpl {
   private final XMLStreamBuffer infoset;
   private final String nsUri;
   private final String localName;
   private FinalArrayList<OutboundStreamHeader.Attribute> attributes;
   private static final String TRUE_VALUE = "1";
   private static final String IS_REFERENCE_PARAMETER = "IsReferenceParameter";

   public OutboundStreamHeader(XMLStreamBuffer infoset, String nsUri, String localName) {
      this.infoset = infoset;
      this.nsUri = nsUri;
      this.localName = localName;
   }

   @NotNull
   public String getNamespaceURI() {
      return this.nsUri;
   }

   @NotNull
   public String getLocalPart() {
      return this.localName;
   }

   public String getAttribute(String nsUri, String localName) {
      if (this.attributes == null) {
         this.parseAttributes();
      }

      for(int i = this.attributes.size() - 1; i >= 0; --i) {
         OutboundStreamHeader.Attribute a = (OutboundStreamHeader.Attribute)this.attributes.get(i);
         if (a.localName.equals(localName) && a.nsUri.equals(nsUri)) {
            return a.value;
         }
      }

      return null;
   }

   private void parseAttributes() {
      try {
         XMLStreamReader reader = this.readHeader();
         this.attributes = new FinalArrayList();

         for(int i = 0; i < reader.getAttributeCount(); ++i) {
            String localName = reader.getAttributeLocalName(i);
            String namespaceURI = reader.getAttributeNamespace(i);
            String value = reader.getAttributeValue(i);
            this.attributes.add(new OutboundStreamHeader.Attribute(namespaceURI, localName, value));
         }

      } catch (XMLStreamException var6) {
         throw new WebServiceException("Unable to read the attributes for {" + this.nsUri + "}" + this.localName + " header", var6);
      }
   }

   public XMLStreamReader readHeader() throws XMLStreamException {
      return this.infoset.readAsXMLStreamReader();
   }

   public void writeTo(XMLStreamWriter w) throws XMLStreamException {
      this.infoset.writeToXMLStreamWriter(w, true);
   }

   public void writeTo(SOAPMessage saaj) throws SOAPException {
      try {
         SOAPHeader header = saaj.getSOAPHeader();
         if (header == null) {
            header = saaj.getSOAPPart().getEnvelope().addHeader();
         }

         this.infoset.writeTo((Node)header);
      } catch (XMLStreamBufferException var3) {
         throw new SOAPException(var3);
      }
   }

   public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
      this.infoset.writeTo(contentHandler, errorHandler);
   }

   static final class Attribute {
      final String nsUri;
      final String localName;
      final String value;

      public Attribute(String nsUri, String localName, String value) {
         this.nsUri = fixNull(nsUri);
         this.localName = localName;
         this.value = value;
      }

      private static String fixNull(String s) {
         return s == null ? "" : s;
      }
   }
}
