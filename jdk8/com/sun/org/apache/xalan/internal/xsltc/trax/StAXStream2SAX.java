package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.xsltc.dom.SAXImpl;
import java.io.IOException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.Locator2;
import org.xml.sax.helpers.AttributesImpl;

public class StAXStream2SAX implements XMLReader, Locator {
   private final XMLStreamReader staxStreamReader;
   private ContentHandler _sax = null;
   private LexicalHandler _lex = null;
   private SAXImpl _saxImpl = null;

   public StAXStream2SAX(XMLStreamReader staxSrc) {
      this.staxStreamReader = staxSrc;
   }

   public ContentHandler getContentHandler() {
      return this._sax;
   }

   public void setContentHandler(ContentHandler handler) throws NullPointerException {
      this._sax = handler;
      if (handler instanceof LexicalHandler) {
         this._lex = (LexicalHandler)handler;
      }

      if (handler instanceof SAXImpl) {
         this._saxImpl = (SAXImpl)handler;
      }

   }

   public void parse(InputSource unused) throws IOException, SAXException {
      try {
         this.bridge();
      } catch (XMLStreamException var3) {
         throw new SAXException(var3);
      }
   }

   public void parse() throws IOException, SAXException, XMLStreamException {
      this.bridge();
   }

   public void parse(String sysId) throws IOException, SAXException {
      throw new IOException("This method is not yet implemented.");
   }

   public void bridge() throws XMLStreamException {
      try {
         int depth = 0;
         int event = this.staxStreamReader.getEventType();
         if (event == 7) {
            event = this.staxStreamReader.next();
         }

         if (event != 1) {
            event = this.staxStreamReader.nextTag();
            if (event != 1) {
               throw new IllegalStateException("The current event is not START_ELEMENT\n but" + event);
            }
         }

         this.handleStartDocument();

         do {
            switch(event) {
            case 1:
               ++depth;
               this.handleStartElement();
               break;
            case 2:
               this.handleEndElement();
               --depth;
               break;
            case 3:
               this.handlePI();
               break;
            case 4:
               this.handleCharacters();
               break;
            case 5:
               this.handleComment();
               break;
            case 6:
               this.handleSpace();
               break;
            case 7:
            case 8:
            default:
               throw new InternalError("processing event: " + event);
            case 9:
               this.handleEntityReference();
               break;
            case 10:
               this.handleAttribute();
               break;
            case 11:
               this.handleDTD();
               break;
            case 12:
               this.handleCDATA();
               break;
            case 13:
               this.handleNamespace();
               break;
            case 14:
               this.handleNotationDecl();
               break;
            case 15:
               this.handleEntityDecl();
            }

            event = this.staxStreamReader.next();
         } while(depth != 0);

         this.handleEndDocument();
      } catch (SAXException var3) {
         throw new XMLStreamException(var3);
      }
   }

   private void handleEndDocument() throws SAXException {
      this._sax.endDocument();
   }

   private void handleStartDocument() throws SAXException {
      this._sax.setDocumentLocator(new Locator2() {
         public int getColumnNumber() {
            return StAXStream2SAX.this.staxStreamReader.getLocation().getColumnNumber();
         }

         public int getLineNumber() {
            return StAXStream2SAX.this.staxStreamReader.getLocation().getLineNumber();
         }

         public String getPublicId() {
            return StAXStream2SAX.this.staxStreamReader.getLocation().getPublicId();
         }

         public String getSystemId() {
            return StAXStream2SAX.this.staxStreamReader.getLocation().getSystemId();
         }

         public String getXMLVersion() {
            return StAXStream2SAX.this.staxStreamReader.getVersion();
         }

         public String getEncoding() {
            return StAXStream2SAX.this.staxStreamReader.getEncoding();
         }
      });
      this._sax.startDocument();
   }

   private void handlePI() throws XMLStreamException {
      try {
         this._sax.processingInstruction(this.staxStreamReader.getPITarget(), this.staxStreamReader.getPIData());
      } catch (SAXException var2) {
         throw new XMLStreamException(var2);
      }
   }

   private void handleCharacters() throws XMLStreamException {
      int textLength = this.staxStreamReader.getTextLength();
      char[] chars = new char[textLength];
      this.staxStreamReader.getTextCharacters(0, chars, 0, textLength);

      try {
         this._sax.characters(chars, 0, chars.length);
      } catch (SAXException var4) {
         throw new XMLStreamException(var4);
      }
   }

   private void handleEndElement() throws XMLStreamException {
      QName qName = this.staxStreamReader.getName();

      try {
         String qname = "";
         if (qName.getPrefix() != null && qName.getPrefix().trim().length() != 0) {
            qname = qName.getPrefix() + ":";
         }

         qname = qname + qName.getLocalPart();
         this._sax.endElement(qName.getNamespaceURI(), qName.getLocalPart(), qname);
         int nsCount = this.staxStreamReader.getNamespaceCount();

         for(int i = nsCount - 1; i >= 0; --i) {
            String prefix = this.staxStreamReader.getNamespacePrefix(i);
            if (prefix == null) {
               prefix = "";
            }

            this._sax.endPrefixMapping(prefix);
         }

      } catch (SAXException var6) {
         throw new XMLStreamException(var6);
      }
   }

   private void handleStartElement() throws XMLStreamException {
      try {
         int nsCount = this.staxStreamReader.getNamespaceCount();

         String prefix;
         for(int i = 0; i < nsCount; ++i) {
            prefix = this.staxStreamReader.getNamespacePrefix(i);
            if (prefix == null) {
               prefix = "";
            }

            this._sax.startPrefixMapping(prefix, this.staxStreamReader.getNamespaceURI(i));
         }

         QName qName = this.staxStreamReader.getName();
         prefix = qName.getPrefix();
         String rawname;
         if (prefix != null && prefix.length() != 0) {
            rawname = prefix + ':' + qName.getLocalPart();
         } else {
            rawname = qName.getLocalPart();
         }

         Attributes attrs = this.getAttributes();
         this._sax.startElement(qName.getNamespaceURI(), qName.getLocalPart(), rawname, attrs);
      } catch (SAXException var6) {
         throw new XMLStreamException(var6);
      }
   }

   private Attributes getAttributes() {
      AttributesImpl attrs = new AttributesImpl();
      int eventType = this.staxStreamReader.getEventType();
      if (eventType != 10 && eventType != 1) {
         throw new InternalError("getAttributes() attempting to process: " + eventType);
      } else {
         for(int i = 0; i < this.staxStreamReader.getAttributeCount(); ++i) {
            String uri = this.staxStreamReader.getAttributeNamespace(i);
            if (uri == null) {
               uri = "";
            }

            String localName = this.staxStreamReader.getAttributeLocalName(i);
            String prefix = this.staxStreamReader.getAttributePrefix(i);
            String qName;
            if (prefix != null && prefix.length() != 0) {
               qName = prefix + ':' + localName;
            } else {
               qName = localName;
            }

            String type = this.staxStreamReader.getAttributeType(i);
            String value = this.staxStreamReader.getAttributeValue(i);
            attrs.addAttribute(uri, localName, qName, type, value);
         }

         return attrs;
      }
   }

   private void handleNamespace() {
   }

   private void handleAttribute() {
   }

   private void handleDTD() {
   }

   private void handleComment() {
   }

   private void handleEntityReference() {
   }

   private void handleSpace() {
   }

   private void handleNotationDecl() {
   }

   private void handleEntityDecl() {
   }

   private void handleCDATA() {
   }

   public DTDHandler getDTDHandler() {
      return null;
   }

   public ErrorHandler getErrorHandler() {
      return null;
   }

   public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
      return false;
   }

   public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
   }

   public void setDTDHandler(DTDHandler handler) throws NullPointerException {
   }

   public void setEntityResolver(EntityResolver resolver) throws NullPointerException {
   }

   public EntityResolver getEntityResolver() {
      return null;
   }

   public void setErrorHandler(ErrorHandler handler) throws NullPointerException {
   }

   public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
   }

   public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
      return null;
   }

   public int getColumnNumber() {
      return 0;
   }

   public int getLineNumber() {
      return 0;
   }

   public String getPublicId() {
      return null;
   }

   public String getSystemId() {
      return null;
   }
}
