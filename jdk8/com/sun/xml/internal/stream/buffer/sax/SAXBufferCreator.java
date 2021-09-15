package com.sun.xml.internal.stream.buffer.sax;

import com.sun.xml.internal.stream.buffer.AbstractCreator;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import java.io.IOException;
import java.io.InputStream;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

public class SAXBufferCreator extends AbstractCreator implements EntityResolver, DTDHandler, ContentHandler, ErrorHandler, LexicalHandler {
   protected String[] _namespaceAttributes;
   protected int _namespaceAttributesPtr;
   private int depth;

   public SAXBufferCreator() {
      this.depth = 0;
      this._namespaceAttributes = new String[32];
   }

   public SAXBufferCreator(MutableXMLStreamBuffer buffer) {
      this();
      this.setBuffer(buffer);
   }

   public MutableXMLStreamBuffer create(XMLReader reader, InputStream in) throws IOException, SAXException {
      return this.create(reader, in, (String)null);
   }

   public MutableXMLStreamBuffer create(XMLReader reader, InputStream in, String systemId) throws IOException, SAXException {
      if (this._buffer == null) {
         this.createBuffer();
      }

      this._buffer.setSystemId(systemId);
      reader.setContentHandler(this);
      reader.setProperty("http://xml.org/sax/properties/lexical-handler", this);

      try {
         this.setHasInternedStrings(reader.getFeature("http://xml.org/sax/features/string-interning"));
      } catch (SAXException var5) {
      }

      if (systemId != null) {
         InputSource s = new InputSource(systemId);
         s.setByteStream(in);
         reader.parse(s);
      } else {
         reader.parse(new InputSource(in));
      }

      return this.getXMLStreamBuffer();
   }

   public void reset() {
      this._buffer = null;
      this._namespaceAttributesPtr = 0;
      this.depth = 0;
   }

   public void startDocument() throws SAXException {
      this.storeStructure(16);
   }

   public void endDocument() throws SAXException {
      this.storeStructure(144);
   }

   public void startPrefixMapping(String prefix, String uri) throws SAXException {
      this.cacheNamespaceAttribute(prefix, uri);
   }

   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      this.storeQualifiedName(32, uri, localName, qName);
      if (this._namespaceAttributesPtr > 0) {
         this.storeNamespaceAttributes();
      }

      if (attributes.getLength() > 0) {
         this.storeAttributes(attributes);
      }

      ++this.depth;
   }

   public void endElement(String uri, String localName, String qName) throws SAXException {
      this.storeStructure(144);
      if (--this.depth == 0) {
         this.increaseTreeCount();
      }

   }

   public void characters(char[] ch, int start, int length) throws SAXException {
      this.storeContentCharacters(80, ch, start, length);
   }

   public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
      this.characters(ch, start, length);
   }

   public void processingInstruction(String target, String data) throws SAXException {
      this.storeStructure(112);
      this.storeStructureString(target);
      this.storeStructureString(data);
   }

   public void comment(char[] ch, int start, int length) throws SAXException {
      this.storeContentCharacters(96, ch, start, length);
   }

   private void cacheNamespaceAttribute(String prefix, String uri) {
      this._namespaceAttributes[this._namespaceAttributesPtr++] = prefix;
      this._namespaceAttributes[this._namespaceAttributesPtr++] = uri;
      if (this._namespaceAttributesPtr == this._namespaceAttributes.length) {
         String[] namespaceAttributes = new String[this._namespaceAttributesPtr * 2];
         System.arraycopy(this._namespaceAttributes, 0, namespaceAttributes, 0, this._namespaceAttributesPtr);
         this._namespaceAttributes = namespaceAttributes;
      }

   }

   private void storeNamespaceAttributes() {
      for(int i = 0; i < this._namespaceAttributesPtr; i += 2) {
         int item = 64;
         if (this._namespaceAttributes[i].length() > 0) {
            item |= 1;
            this.storeStructureString(this._namespaceAttributes[i]);
         }

         if (this._namespaceAttributes[i + 1].length() > 0) {
            item |= 2;
            this.storeStructureString(this._namespaceAttributes[i + 1]);
         }

         this.storeStructure(item);
      }

      this._namespaceAttributesPtr = 0;
   }

   private void storeAttributes(Attributes attributes) {
      for(int i = 0; i < attributes.getLength(); ++i) {
         if (!attributes.getQName(i).startsWith("xmlns")) {
            this.storeQualifiedName(48, attributes.getURI(i), attributes.getLocalName(i), attributes.getQName(i));
            this.storeStructureString(attributes.getType(i));
            this.storeContentString(attributes.getValue(i));
         }
      }

   }

   private void storeQualifiedName(int item, String uri, String localName, String qName) {
      if (uri.length() > 0) {
         item |= 2;
         this.storeStructureString(uri);
      }

      this.storeStructureString(localName);
      if (qName.indexOf(58) >= 0) {
         item |= 4;
         this.storeStructureString(qName);
      }

      this.storeStructure(item);
   }

   public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
      return null;
   }

   public void notationDecl(String name, String publicId, String systemId) throws SAXException {
   }

   public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
   }

   public void setDocumentLocator(Locator locator) {
   }

   public void endPrefixMapping(String prefix) throws SAXException {
   }

   public void skippedEntity(String name) throws SAXException {
   }

   public void startDTD(String name, String publicId, String systemId) throws SAXException {
   }

   public void endDTD() throws SAXException {
   }

   public void startEntity(String name) throws SAXException {
   }

   public void endEntity(String name) throws SAXException {
   }

   public void startCDATA() throws SAXException {
   }

   public void endCDATA() throws SAXException {
   }

   public void warning(SAXParseException e) throws SAXException {
   }

   public void error(SAXParseException e) throws SAXException {
   }

   public void fatalError(SAXParseException e) throws SAXException {
      throw e;
   }
}
