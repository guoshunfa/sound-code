package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.xsltc.dom.SAXImpl;
import java.io.IOException;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
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

public class StAXEvent2SAX implements XMLReader, Locator {
   private final XMLEventReader staxEventReader;
   private ContentHandler _sax = null;
   private LexicalHandler _lex = null;
   private SAXImpl _saxImpl = null;
   private String version = null;
   private String encoding = null;

   public StAXEvent2SAX(XMLEventReader staxCore) {
      this.staxEventReader = staxCore;
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

   private void bridge() throws XMLStreamException {
      try {
         int depth = 0;
         boolean startedAtDocument = false;
         XMLEvent event = this.staxEventReader.peek();
         if (!event.isStartDocument() && !event.isStartElement()) {
            throw new IllegalStateException();
         } else {
            if (event.getEventType() == 7) {
               startedAtDocument = true;
               this.version = ((StartDocument)event).getVersion();
               if (((StartDocument)event).encodingSet()) {
                  this.encoding = ((StartDocument)event).getCharacterEncodingScheme();
               }

               event = this.staxEventReader.nextEvent();
               event = this.staxEventReader.nextEvent();
            }

            this.handleStartDocument(event);

            for(; event.getEventType() != 1; event = this.staxEventReader.nextEvent()) {
               switch(event.getEventType()) {
               case 3:
                  this.handlePI((ProcessingInstruction)event);
                  break;
               case 4:
                  this.handleCharacters(event.asCharacters());
                  break;
               case 5:
                  this.handleComment();
                  break;
               case 6:
                  this.handleSpace();
                  break;
               case 7:
               case 8:
               case 9:
               case 10:
               default:
                  throw new InternalError("processing prolog event: " + event);
               case 11:
                  this.handleDTD();
               }
            }

            do {
               switch(event.getEventType()) {
               case 1:
                  ++depth;
                  this.handleStartElement(event.asStartElement());
                  break;
               case 2:
                  this.handleEndElement(event.asEndElement());
                  --depth;
                  break;
               case 3:
                  this.handlePI((ProcessingInstruction)event);
                  break;
               case 4:
                  this.handleCharacters(event.asCharacters());
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

               event = this.staxEventReader.nextEvent();
            } while(depth != 0);

            if (startedAtDocument) {
               for(; event.getEventType() != 8; event = this.staxEventReader.nextEvent()) {
                  switch(event.getEventType()) {
                  case 3:
                     this.handlePI((ProcessingInstruction)event);
                     break;
                  case 4:
                     this.handleCharacters(event.asCharacters());
                     break;
                  case 5:
                     this.handleComment();
                     break;
                  case 6:
                     this.handleSpace();
                     break;
                  default:
                     throw new InternalError("processing misc event after document element: " + event);
                  }
               }
            }

            this.handleEndDocument();
         }
      } catch (SAXException var4) {
         throw new XMLStreamException(var4);
      }
   }

   private void handleEndDocument() throws SAXException {
      this._sax.endDocument();
   }

   private void handleStartDocument(final XMLEvent event) throws SAXException {
      this._sax.setDocumentLocator(new Locator2() {
         public int getColumnNumber() {
            return event.getLocation().getColumnNumber();
         }

         public int getLineNumber() {
            return event.getLocation().getLineNumber();
         }

         public String getPublicId() {
            return event.getLocation().getPublicId();
         }

         public String getSystemId() {
            return event.getLocation().getSystemId();
         }

         public String getXMLVersion() {
            return StAXEvent2SAX.this.version;
         }

         public String getEncoding() {
            return StAXEvent2SAX.this.encoding;
         }
      });
      this._sax.startDocument();
   }

   private void handlePI(ProcessingInstruction event) throws XMLStreamException {
      try {
         this._sax.processingInstruction(event.getTarget(), event.getData());
      } catch (SAXException var3) {
         throw new XMLStreamException(var3);
      }
   }

   private void handleCharacters(Characters event) throws XMLStreamException {
      try {
         this._sax.characters(event.getData().toCharArray(), 0, event.getData().length());
      } catch (SAXException var3) {
         throw new XMLStreamException(var3);
      }
   }

   private void handleEndElement(EndElement event) throws XMLStreamException {
      QName qName = event.getName();
      String qname = "";
      if (qName.getPrefix() != null && qName.getPrefix().trim().length() != 0) {
         qname = qName.getPrefix() + ":";
      }

      qname = qname + qName.getLocalPart();

      try {
         this._sax.endElement(qName.getNamespaceURI(), qName.getLocalPart(), qname);

         String prefix;
         for(Iterator i = event.getNamespaces(); i.hasNext(); this._sax.endPrefixMapping(prefix)) {
            prefix = (String)i.next();
            if (prefix == null) {
               prefix = "";
            }
         }

      } catch (SAXException var6) {
         throw new XMLStreamException(var6);
      }
   }

   private void handleStartElement(StartElement event) throws XMLStreamException {
      try {
         String prefix;
         for(Iterator i = event.getNamespaces(); i.hasNext(); this._sax.startPrefixMapping(prefix, event.getNamespaceURI(prefix))) {
            prefix = ((Namespace)i.next()).getPrefix();
            if (prefix == null) {
               prefix = "";
            }
         }

         QName qName = event.getName();
         prefix = qName.getPrefix();
         String rawname;
         if (prefix != null && prefix.length() != 0) {
            rawname = prefix + ':' + qName.getLocalPart();
         } else {
            rawname = qName.getLocalPart();
         }

         Attributes saxAttrs = this.getAttributes(event);
         this._sax.startElement(qName.getNamespaceURI(), qName.getLocalPart(), rawname, saxAttrs);
      } catch (SAXException var6) {
         throw new XMLStreamException(var6);
      }
   }

   private Attributes getAttributes(StartElement event) {
      AttributesImpl attrs = new AttributesImpl();
      if (!event.isStartElement()) {
         throw new InternalError("getAttributes() attempting to process: " + event);
      } else {
         Iterator i = event.getAttributes();

         while(i.hasNext()) {
            Attribute staxAttr = (Attribute)i.next();
            String uri = staxAttr.getName().getNamespaceURI();
            if (uri == null) {
               uri = "";
            }

            String localName = staxAttr.getName().getLocalPart();
            String prefix = staxAttr.getName().getPrefix();
            String qName;
            if (prefix != null && prefix.length() != 0) {
               qName = prefix + ':' + localName;
            } else {
               qName = localName;
            }

            String type = staxAttr.getDTDType();
            String value = staxAttr.getValue();
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

   public void parse(String sysId) throws IOException, SAXException {
      throw new IOException("This method is not yet implemented.");
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
