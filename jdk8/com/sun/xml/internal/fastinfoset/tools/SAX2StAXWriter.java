package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.QualifiedName;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class SAX2StAXWriter extends DefaultHandler implements LexicalHandler {
   private static final Logger logger = Logger.getLogger(SAX2StAXWriter.class.getName());
   XMLStreamWriter _writer;
   ArrayList _namespaces = new ArrayList();

   public SAX2StAXWriter(XMLStreamWriter writer) {
      this._writer = writer;
   }

   public XMLStreamWriter getWriter() {
      return this._writer;
   }

   public void startDocument() throws SAXException {
      try {
         this._writer.writeStartDocument();
      } catch (XMLStreamException var2) {
         throw new SAXException(var2);
      }
   }

   public void endDocument() throws SAXException {
      try {
         this._writer.writeEndDocument();
         this._writer.flush();
      } catch (XMLStreamException var2) {
         throw new SAXException(var2);
      }
   }

   public void characters(char[] ch, int start, int length) throws SAXException {
      try {
         this._writer.writeCharacters(ch, start, length);
      } catch (XMLStreamException var5) {
         throw new SAXException(var5);
      }
   }

   public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
      try {
         int k = qName.indexOf(58);
         String prefix = k > 0 ? qName.substring(0, k) : "";
         this._writer.writeStartElement(prefix, localName, namespaceURI);
         int length = this._namespaces.size();

         int i;
         for(i = 0; i < length; ++i) {
            QualifiedName nsh = (QualifiedName)this._namespaces.get(i);
            this._writer.writeNamespace(nsh.prefix, nsh.namespaceName);
         }

         this._namespaces.clear();
         length = atts.getLength();

         for(i = 0; i < length; ++i) {
            this._writer.writeAttribute(atts.getURI(i), atts.getLocalName(i), atts.getValue(i));
         }

      } catch (XMLStreamException var10) {
         throw new SAXException(var10);
      }
   }

   public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
      try {
         this._writer.writeEndElement();
      } catch (XMLStreamException var5) {
         logger.log(Level.FINE, (String)"Exception on endElement", (Throwable)var5);
         throw new SAXException(var5);
      }
   }

   public void startPrefixMapping(String prefix, String uri) throws SAXException {
      this._namespaces.add(new QualifiedName(prefix, uri));
   }

   public void endPrefixMapping(String prefix) throws SAXException {
   }

   public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
      this.characters(ch, start, length);
   }

   public void processingInstruction(String target, String data) throws SAXException {
      try {
         this._writer.writeProcessingInstruction(target, data);
      } catch (XMLStreamException var4) {
         throw new SAXException(var4);
      }
   }

   public void setDocumentLocator(Locator locator) {
   }

   public void skippedEntity(String name) throws SAXException {
   }

   public void comment(char[] ch, int start, int length) throws SAXException {
      try {
         this._writer.writeComment(new String(ch, start, length));
      } catch (XMLStreamException var5) {
         throw new SAXException(var5);
      }
   }

   public void endCDATA() throws SAXException {
   }

   public void endDTD() throws SAXException {
   }

   public void endEntity(String name) throws SAXException {
   }

   public void startCDATA() throws SAXException {
   }

   public void startDTD(String name, String publicId, String systemId) throws SAXException {
   }

   public void startEntity(String name) throws SAXException {
   }
}
