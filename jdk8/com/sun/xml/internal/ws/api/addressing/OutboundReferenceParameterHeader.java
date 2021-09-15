package com.sun.xml.internal.ws.api.addressing;

import com.sun.istack.internal.FinalArrayList;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferException;
import com.sun.xml.internal.ws.message.AbstractHeaderImpl;
import com.sun.xml.internal.ws.util.xml.XMLStreamWriterFilter;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.util.StreamReaderDelegate;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

final class OutboundReferenceParameterHeader extends AbstractHeaderImpl {
   private final XMLStreamBuffer infoset;
   private final String nsUri;
   private final String localName;
   private FinalArrayList<OutboundReferenceParameterHeader.Attribute> attributes;
   private static final String TRUE_VALUE = "1";
   private static final String IS_REFERENCE_PARAMETER = "IsReferenceParameter";

   OutboundReferenceParameterHeader(XMLStreamBuffer infoset, String nsUri, String localName) {
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
         OutboundReferenceParameterHeader.Attribute a = (OutboundReferenceParameterHeader.Attribute)this.attributes.get(i);
         if (a.localName.equals(localName) && a.nsUri.equals(nsUri)) {
            return a.value;
         }
      }

      return null;
   }

   private void parseAttributes() {
      try {
         XMLStreamReader reader = this.readHeader();
         reader.nextTag();
         this.attributes = new FinalArrayList();
         boolean refParamAttrWritten = false;

         for(int i = 0; i < reader.getAttributeCount(); ++i) {
            String attrLocalName = reader.getAttributeLocalName(i);
            String namespaceURI = reader.getAttributeNamespace(i);
            String value = reader.getAttributeValue(i);
            if (namespaceURI.equals(AddressingVersion.W3C.nsUri) && attrLocalName.equals("IS_REFERENCE_PARAMETER")) {
               refParamAttrWritten = true;
            }

            this.attributes.add(new OutboundReferenceParameterHeader.Attribute(namespaceURI, attrLocalName, value));
         }

         if (!refParamAttrWritten) {
            this.attributes.add(new OutboundReferenceParameterHeader.Attribute(AddressingVersion.W3C.nsUri, "IsReferenceParameter", "1"));
         }

      } catch (XMLStreamException var7) {
         throw new WebServiceException("Unable to read the attributes for {" + this.nsUri + "}" + this.localName + " header", var7);
      }
   }

   public XMLStreamReader readHeader() throws XMLStreamException {
      return new StreamReaderDelegate(this.infoset.readAsXMLStreamReader()) {
         int state = 0;

         public int next() throws XMLStreamException {
            return this.check(super.next());
         }

         public int nextTag() throws XMLStreamException {
            return this.check(super.nextTag());
         }

         private int check(int type) {
            switch(this.state) {
            case 0:
               if (type == 1) {
                  this.state = 1;
               }
               break;
            case 1:
               this.state = 2;
            }

            return type;
         }

         public int getAttributeCount() {
            return this.state == 1 ? super.getAttributeCount() + 1 : super.getAttributeCount();
         }

         public String getAttributeLocalName(int index) {
            return this.state == 1 && index == super.getAttributeCount() ? "IsReferenceParameter" : super.getAttributeLocalName(index);
         }

         public String getAttributeNamespace(int index) {
            return this.state == 1 && index == super.getAttributeCount() ? AddressingVersion.W3C.nsUri : super.getAttributeNamespace(index);
         }

         public String getAttributePrefix(int index) {
            return this.state == 1 && index == super.getAttributeCount() ? "wsa" : super.getAttributePrefix(index);
         }

         public String getAttributeType(int index) {
            return this.state == 1 && index == super.getAttributeCount() ? "CDATA" : super.getAttributeType(index);
         }

         public String getAttributeValue(int index) {
            return this.state == 1 && index == super.getAttributeCount() ? "1" : super.getAttributeValue(index);
         }

         public QName getAttributeName(int index) {
            return this.state == 1 && index == super.getAttributeCount() ? new QName(AddressingVersion.W3C.nsUri, "IsReferenceParameter", "wsa") : super.getAttributeName(index);
         }

         public String getAttributeValue(String namespaceUri, String localName) {
            return this.state == 1 && localName.equals("IsReferenceParameter") && namespaceUri.equals(AddressingVersion.W3C.nsUri) ? "1" : super.getAttributeValue(namespaceUri, localName);
         }
      };
   }

   public void writeTo(XMLStreamWriter w) throws XMLStreamException {
      this.infoset.writeToXMLStreamWriter(new XMLStreamWriterFilter(w) {
         private boolean root = true;
         private boolean onRootEl = true;

         public void writeStartElement(String localName) throws XMLStreamException {
            super.writeStartElement(localName);
            this.writeAddedAttribute();
         }

         private void writeAddedAttribute() throws XMLStreamException {
            if (!this.root) {
               this.onRootEl = false;
            } else {
               this.root = false;
               this.writeNamespace("wsa", AddressingVersion.W3C.nsUri);
               super.writeAttribute("wsa", AddressingVersion.W3C.nsUri, "IsReferenceParameter", "1");
            }
         }

         public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
            super.writeStartElement(namespaceURI, localName);
            this.writeAddedAttribute();
         }

         public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
            boolean prefixDeclared = this.isPrefixDeclared(prefix, namespaceURI);
            super.writeStartElement(prefix, localName, namespaceURI);
            if (!prefixDeclared && !prefix.equals("")) {
               super.writeNamespace(prefix, namespaceURI);
            }

            this.writeAddedAttribute();
         }

         public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
            if (!this.isPrefixDeclared(prefix, namespaceURI)) {
               super.writeNamespace(prefix, namespaceURI);
            }

         }

         public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
            if (!this.onRootEl || !namespaceURI.equals(AddressingVersion.W3C.nsUri) || !localName.equals("IsReferenceParameter")) {
               this.writer.writeAttribute(prefix, namespaceURI, localName, value);
            }
         }

         public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
            this.writer.writeAttribute(namespaceURI, localName, value);
         }

         private boolean isPrefixDeclared(String prefix, String namespaceURI) {
            return namespaceURI.equals(this.getNamespaceContext().getNamespaceURI(prefix));
         }
      }, true);
   }

   public void writeTo(SOAPMessage saaj) throws SOAPException {
      try {
         SOAPHeader header = saaj.getSOAPHeader();
         if (header == null) {
            header = saaj.getSOAPPart().getEnvelope().addHeader();
         }

         Element node = (Element)this.infoset.writeTo((Node)header);
         node.setAttributeNS(AddressingVersion.W3C.nsUri, AddressingVersion.W3C.getPrefix() + ":" + "IsReferenceParameter", "1");
      } catch (XMLStreamBufferException var4) {
         throw new SOAPException(var4);
      }
   }

   public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
      class Filter extends XMLFilterImpl {
         private int depth = 0;

         Filter(ContentHandler ch) {
            this.setContentHandler(ch);
         }

         public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            if (this.depth++ == 0) {
               super.startPrefixMapping("wsa", AddressingVersion.W3C.nsUri);
               if (((Attributes)atts).getIndex(AddressingVersion.W3C.nsUri, "IsReferenceParameter") == -1) {
                  AttributesImpl atts2 = new AttributesImpl((Attributes)atts);
                  atts2.addAttribute(AddressingVersion.W3C.nsUri, "IsReferenceParameter", "wsa:IsReferenceParameter", "CDATA", "1");
                  atts = atts2;
               }
            }

            super.startElement(uri, localName, qName, (Attributes)atts);
         }

         public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if (--this.depth == 0) {
               super.endPrefixMapping("wsa");
            }

         }
      }

      this.infoset.writeTo(new Filter(contentHandler), errorHandler);
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
