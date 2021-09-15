package com.sun.org.apache.xalan.internal.xsltc.trax;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.Locator2;

public class SAX2StAXStreamWriter extends SAX2StAXBaseWriter {
   private XMLStreamWriter writer;
   private boolean needToCallStartDocument = false;

   public SAX2StAXStreamWriter() {
   }

   public SAX2StAXStreamWriter(XMLStreamWriter writer) {
      this.writer = writer;
   }

   public XMLStreamWriter getStreamWriter() {
      return this.writer;
   }

   public void setStreamWriter(XMLStreamWriter writer) {
      this.writer = writer;
   }

   public void startDocument() throws SAXException {
      super.startDocument();
      this.needToCallStartDocument = true;
   }

   public void endDocument() throws SAXException {
      try {
         this.writer.writeEndDocument();
      } catch (XMLStreamException var2) {
         throw new SAXException(var2);
      }

      super.endDocument();
   }

   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (this.needToCallStartDocument) {
         try {
            if (this.docLocator == null) {
               this.writer.writeStartDocument();
            } else {
               try {
                  this.writer.writeStartDocument(((Locator2)this.docLocator).getXMLVersion());
               } catch (ClassCastException var18) {
                  this.writer.writeStartDocument();
               }
            }
         } catch (XMLStreamException var19) {
            throw new SAXException(var19);
         }

         this.needToCallStartDocument = false;
      }

      try {
         String[] qname = new String[]{null, null};
         parseQName(qName, qname);
         this.writer.writeStartElement(qName);
         int i = 0;

         for(int s = attributes.getLength(); i < s; ++i) {
            parseQName(attributes.getQName(i), qname);
            String attrPrefix = qname[0];
            String attrLocal = qname[1];
            String attrQName = attributes.getQName(i);
            String attrValue = attributes.getValue(i);
            String attrURI = attributes.getURI(i);
            if (!"xmlns".equals(attrPrefix) && !"xmlns".equals(attrQName)) {
               if (attrPrefix.length() > 0) {
                  this.writer.writeAttribute(attrPrefix, attrURI, attrLocal, attrValue);
               } else {
                  this.writer.writeAttribute(attrQName, attrValue);
               }
            } else {
               if (attrLocal.length() == 0) {
                  this.writer.setDefaultNamespace(attrValue);
               } else {
                  this.writer.setPrefix(attrLocal, attrValue);
               }

               this.writer.writeNamespace(attrLocal, attrValue);
            }
         }
      } catch (XMLStreamException var20) {
         throw new SAXException(var20);
      } finally {
         super.startElement(uri, localName, qName, attributes);
      }

   }

   public void endElement(String uri, String localName, String qName) throws SAXException {
      try {
         this.writer.writeEndElement();
      } catch (XMLStreamException var8) {
         throw new SAXException(var8);
      } finally {
         super.endElement(uri, localName, qName);
      }

   }

   public void comment(char[] ch, int start, int length) throws SAXException {
      super.comment(ch, start, length);

      try {
         this.writer.writeComment(new String(ch, start, length));
      } catch (XMLStreamException var5) {
         throw new SAXException(var5);
      }
   }

   public void characters(char[] ch, int start, int length) throws SAXException {
      super.characters(ch, start, length);

      try {
         if (!this.isCDATA) {
            this.writer.writeCharacters(ch, start, length);
         }

      } catch (XMLStreamException var5) {
         throw new SAXException(var5);
      }
   }

   public void endCDATA() throws SAXException {
      try {
         this.writer.writeCData(this.CDATABuffer.toString());
      } catch (XMLStreamException var2) {
         throw new SAXException(var2);
      }

      super.endCDATA();
   }

   public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
      super.ignorableWhitespace(ch, start, length);

      try {
         this.writer.writeCharacters(ch, start, length);
      } catch (XMLStreamException var5) {
         throw new SAXException(var5);
      }
   }

   public void processingInstruction(String target, String data) throws SAXException {
      super.processingInstruction(target, data);

      try {
         this.writer.writeProcessingInstruction(target, data);
      } catch (XMLStreamException var4) {
         throw new SAXException(var4);
      }
   }
}
