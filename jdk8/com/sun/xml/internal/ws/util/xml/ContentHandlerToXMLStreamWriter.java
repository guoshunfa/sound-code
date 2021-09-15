package com.sun.xml.internal.ws.util.xml;

import java.util.Stack;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ContentHandlerToXMLStreamWriter extends DefaultHandler {
   private final XMLStreamWriter staxWriter;
   private final Stack prefixBindings;

   public ContentHandlerToXMLStreamWriter(XMLStreamWriter staxCore) {
      this.staxWriter = staxCore;
      this.prefixBindings = new Stack();
   }

   public void endDocument() throws SAXException {
      try {
         this.staxWriter.writeEndDocument();
         this.staxWriter.flush();
      } catch (XMLStreamException var2) {
         throw new SAXException(var2);
      }
   }

   public void startDocument() throws SAXException {
      try {
         this.staxWriter.writeStartDocument();
      } catch (XMLStreamException var2) {
         throw new SAXException(var2);
      }
   }

   public void characters(char[] ch, int start, int length) throws SAXException {
      try {
         this.staxWriter.writeCharacters(ch, start, length);
      } catch (XMLStreamException var5) {
         throw new SAXException(var5);
      }
   }

   public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
      this.characters(ch, start, length);
   }

   public void endPrefixMapping(String prefix) throws SAXException {
   }

   public void skippedEntity(String name) throws SAXException {
      try {
         this.staxWriter.writeEntityRef(name);
      } catch (XMLStreamException var3) {
         throw new SAXException(var3);
      }
   }

   public void setDocumentLocator(Locator locator) {
   }

   public void processingInstruction(String target, String data) throws SAXException {
      try {
         this.staxWriter.writeProcessingInstruction(target, data);
      } catch (XMLStreamException var4) {
         throw new SAXException(var4);
      }
   }

   public void startPrefixMapping(String prefix, String uri) throws SAXException {
      if (prefix == null) {
         prefix = "";
      }

      if (!prefix.equals("xml")) {
         this.prefixBindings.add(prefix);
         this.prefixBindings.add(uri);
      }
   }

   public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
      try {
         this.staxWriter.writeEndElement();
      } catch (XMLStreamException var5) {
         throw new SAXException(var5);
      }
   }

   public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
      try {
         this.staxWriter.writeStartElement(this.getPrefix(qName), localName, namespaceURI);

         String uri;
         String prefix;
         for(; this.prefixBindings.size() != 0; this.staxWriter.writeNamespace(prefix, uri)) {
            uri = (String)this.prefixBindings.pop();
            prefix = (String)this.prefixBindings.pop();
            if (prefix.length() == 0) {
               this.staxWriter.setDefaultNamespace(uri);
            } else {
               this.staxWriter.setPrefix(prefix, uri);
            }
         }

         this.writeAttributes(atts);
      } catch (XMLStreamException var7) {
         throw new SAXException(var7);
      }
   }

   private void writeAttributes(Attributes atts) throws XMLStreamException {
      for(int i = 0; i < atts.getLength(); ++i) {
         String prefix = this.getPrefix(atts.getQName(i));
         if (!prefix.equals("xmlns")) {
            this.staxWriter.writeAttribute(prefix, atts.getURI(i), atts.getLocalName(i), atts.getValue(i));
         }
      }

   }

   private String getPrefix(String qName) {
      int idx = qName.indexOf(58);
      return idx == -1 ? "" : qName.substring(0, idx);
   }
}
