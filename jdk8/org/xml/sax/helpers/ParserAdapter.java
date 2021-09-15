package org.xml.sax.helpers;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import org.xml.sax.AttributeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public class ParserAdapter implements XMLReader, DocumentHandler {
   private static SecuritySupport ss = new SecuritySupport();
   private static final String FEATURES = "http://xml.org/sax/features/";
   private static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
   private static final String NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
   private static final String XMLNS_URIs = "http://xml.org/sax/features/xmlns-uris";
   private NamespaceSupport nsSupport;
   private ParserAdapter.AttributeListAdapter attAdapter;
   private boolean parsing = false;
   private String[] nameParts = new String[3];
   private Parser parser = null;
   private AttributesImpl atts = null;
   private boolean namespaces = true;
   private boolean prefixes = false;
   private boolean uris = false;
   Locator locator;
   EntityResolver entityResolver = null;
   DTDHandler dtdHandler = null;
   ContentHandler contentHandler = null;
   ErrorHandler errorHandler = null;

   public ParserAdapter() throws SAXException {
      String driver = ss.getSystemProperty("org.xml.sax.parser");

      try {
         this.setup(ParserFactory.makeParser());
      } catch (ClassNotFoundException var3) {
         throw new SAXException("Cannot find SAX1 driver class " + driver, var3);
      } catch (IllegalAccessException var4) {
         throw new SAXException("SAX1 driver class " + driver + " found but cannot be loaded", var4);
      } catch (InstantiationException var5) {
         throw new SAXException("SAX1 driver class " + driver + " loaded but cannot be instantiated", var5);
      } catch (ClassCastException var6) {
         throw new SAXException("SAX1 driver class " + driver + " does not implement org.xml.sax.Parser");
      } catch (NullPointerException var7) {
         throw new SAXException("System property org.xml.sax.parser not specified");
      }
   }

   public ParserAdapter(Parser parser) {
      this.setup(parser);
   }

   private void setup(Parser parser) {
      if (parser == null) {
         throw new NullPointerException("Parser argument must not be null");
      } else {
         this.parser = parser;
         this.atts = new AttributesImpl();
         this.nsSupport = new NamespaceSupport();
         this.attAdapter = new ParserAdapter.AttributeListAdapter();
      }
   }

   public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
      if (name.equals("http://xml.org/sax/features/namespaces")) {
         this.checkNotParsing("feature", name);
         this.namespaces = value;
         if (!this.namespaces && !this.prefixes) {
            this.prefixes = true;
         }
      } else if (name.equals("http://xml.org/sax/features/namespace-prefixes")) {
         this.checkNotParsing("feature", name);
         this.prefixes = value;
         if (!this.prefixes && !this.namespaces) {
            this.namespaces = true;
         }
      } else {
         if (!name.equals("http://xml.org/sax/features/xmlns-uris")) {
            throw new SAXNotRecognizedException("Feature: " + name);
         }

         this.checkNotParsing("feature", name);
         this.uris = value;
      }

   }

   public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
      if (name.equals("http://xml.org/sax/features/namespaces")) {
         return this.namespaces;
      } else if (name.equals("http://xml.org/sax/features/namespace-prefixes")) {
         return this.prefixes;
      } else if (name.equals("http://xml.org/sax/features/xmlns-uris")) {
         return this.uris;
      } else {
         throw new SAXNotRecognizedException("Feature: " + name);
      }
   }

   public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
      throw new SAXNotRecognizedException("Property: " + name);
   }

   public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
      throw new SAXNotRecognizedException("Property: " + name);
   }

   public void setEntityResolver(EntityResolver resolver) {
      this.entityResolver = resolver;
   }

   public EntityResolver getEntityResolver() {
      return this.entityResolver;
   }

   public void setDTDHandler(DTDHandler handler) {
      this.dtdHandler = handler;
   }

   public DTDHandler getDTDHandler() {
      return this.dtdHandler;
   }

   public void setContentHandler(ContentHandler handler) {
      this.contentHandler = handler;
   }

   public ContentHandler getContentHandler() {
      return this.contentHandler;
   }

   public void setErrorHandler(ErrorHandler handler) {
      this.errorHandler = handler;
   }

   public ErrorHandler getErrorHandler() {
      return this.errorHandler;
   }

   public void parse(String systemId) throws IOException, SAXException {
      this.parse(new InputSource(systemId));
   }

   public void parse(InputSource input) throws IOException, SAXException {
      if (this.parsing) {
         throw new SAXException("Parser is already in use");
      } else {
         this.setupParser();
         this.parsing = true;

         try {
            this.parser.parse(input);
         } finally {
            this.parsing = false;
         }

         this.parsing = false;
      }
   }

   public void setDocumentLocator(Locator locator) {
      this.locator = locator;
      if (this.contentHandler != null) {
         this.contentHandler.setDocumentLocator(locator);
      }

   }

   public void startDocument() throws SAXException {
      if (this.contentHandler != null) {
         this.contentHandler.startDocument();
      }

   }

   public void endDocument() throws SAXException {
      if (this.contentHandler != null) {
         this.contentHandler.endDocument();
      }

   }

   public void startElement(String qName, AttributeList qAtts) throws SAXException {
      Vector exceptions = null;
      if (!this.namespaces) {
         if (this.contentHandler != null) {
            this.attAdapter.setAttributeList(qAtts);
            this.contentHandler.startElement("", "", qName.intern(), this.attAdapter);
         }

      } else {
         this.nsSupport.pushContext();
         int length = qAtts.getLength();

         int i;
         String attQName;
         String type;
         String prefix;
         for(i = 0; i < length; ++i) {
            attQName = qAtts.getName(i);
            if (attQName.startsWith("xmlns")) {
               int n = attQName.indexOf(58);
               if (n == -1 && attQName.length() == 5) {
                  type = "";
               } else {
                  if (n != 5) {
                     continue;
                  }

                  type = attQName.substring(n + 1);
               }

               prefix = qAtts.getValue(i);
               if (!this.nsSupport.declarePrefix(type, prefix)) {
                  this.reportError("Illegal Namespace prefix: " + type);
               } else if (this.contentHandler != null) {
                  this.contentHandler.startPrefixMapping(type, prefix);
               }
            }
         }

         this.atts.clear();

         for(i = 0; i < length; ++i) {
            attQName = qAtts.getName(i);
            type = qAtts.getType(i);
            String value = qAtts.getValue(i);
            if (attQName.startsWith("xmlns")) {
               int n = attQName.indexOf(58);
               if (n == -1 && attQName.length() == 5) {
                  prefix = "";
               } else if (n != 5) {
                  prefix = null;
               } else {
                  prefix = attQName.substring(6);
               }

               if (prefix != null) {
                  if (this.prefixes) {
                     if (this.uris) {
                        NamespaceSupport var10001 = this.nsSupport;
                        this.atts.addAttribute("http://www.w3.org/XML/1998/namespace", prefix, attQName.intern(), type, value);
                     } else {
                        this.atts.addAttribute("", "", attQName.intern(), type, value);
                     }
                  }
                  continue;
               }
            }

            try {
               String[] attName = this.processName(attQName, true, true);
               this.atts.addAttribute(attName[0], attName[1], attName[2], type, value);
            } catch (SAXException var11) {
               if (exceptions == null) {
                  exceptions = new Vector();
               }

               exceptions.addElement(var11);
               this.atts.addAttribute("", attQName, attQName, type, value);
            }
         }

         if (exceptions != null && this.errorHandler != null) {
            for(i = 0; i < exceptions.size(); ++i) {
               this.errorHandler.error((SAXParseException)((SAXParseException)exceptions.elementAt(i)));
            }
         }

         if (this.contentHandler != null) {
            String[] name = this.processName(qName, false, false);
            this.contentHandler.startElement(name[0], name[1], name[2], this.atts);
         }

      }
   }

   public void endElement(String qName) throws SAXException {
      if (!this.namespaces) {
         if (this.contentHandler != null) {
            this.contentHandler.endElement("", "", qName.intern());
         }

      } else {
         String[] names = this.processName(qName, false, false);
         if (this.contentHandler != null) {
            this.contentHandler.endElement(names[0], names[1], names[2]);
            Enumeration prefixes = this.nsSupport.getDeclaredPrefixes();

            while(prefixes.hasMoreElements()) {
               String prefix = (String)prefixes.nextElement();
               this.contentHandler.endPrefixMapping(prefix);
            }
         }

         this.nsSupport.popContext();
      }
   }

   public void characters(char[] ch, int start, int length) throws SAXException {
      if (this.contentHandler != null) {
         this.contentHandler.characters(ch, start, length);
      }

   }

   public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
      if (this.contentHandler != null) {
         this.contentHandler.ignorableWhitespace(ch, start, length);
      }

   }

   public void processingInstruction(String target, String data) throws SAXException {
      if (this.contentHandler != null) {
         this.contentHandler.processingInstruction(target, data);
      }

   }

   private void setupParser() {
      if (!this.prefixes && !this.namespaces) {
         throw new IllegalStateException();
      } else {
         this.nsSupport.reset();
         if (this.uris) {
            this.nsSupport.setNamespaceDeclUris(true);
         }

         if (this.entityResolver != null) {
            this.parser.setEntityResolver(this.entityResolver);
         }

         if (this.dtdHandler != null) {
            this.parser.setDTDHandler(this.dtdHandler);
         }

         if (this.errorHandler != null) {
            this.parser.setErrorHandler(this.errorHandler);
         }

         this.parser.setDocumentHandler(this);
         this.locator = null;
      }
   }

   private String[] processName(String qName, boolean isAttribute, boolean useException) throws SAXException {
      String[] parts = this.nsSupport.processName(qName, this.nameParts, isAttribute);
      if (parts == null) {
         if (useException) {
            throw this.makeException("Undeclared prefix: " + qName);
         }

         this.reportError("Undeclared prefix: " + qName);
         parts = new String[3];
         parts[0] = parts[1] = "";
         parts[2] = qName.intern();
      }

      return parts;
   }

   void reportError(String message) throws SAXException {
      if (this.errorHandler != null) {
         this.errorHandler.error(this.makeException(message));
      }

   }

   private SAXParseException makeException(String message) {
      return this.locator != null ? new SAXParseException(message, this.locator) : new SAXParseException(message, (String)null, (String)null, -1, -1);
   }

   private void checkNotParsing(String type, String name) throws SAXNotSupportedException {
      if (this.parsing) {
         throw new SAXNotSupportedException("Cannot change " + type + ' ' + name + " while parsing");
      }
   }

   final class AttributeListAdapter implements Attributes {
      private AttributeList qAtts;

      void setAttributeList(AttributeList qAtts) {
         this.qAtts = qAtts;
      }

      public int getLength() {
         return this.qAtts.getLength();
      }

      public String getURI(int i) {
         return "";
      }

      public String getLocalName(int i) {
         return "";
      }

      public String getQName(int i) {
         return this.qAtts.getName(i).intern();
      }

      public String getType(int i) {
         return this.qAtts.getType(i).intern();
      }

      public String getValue(int i) {
         return this.qAtts.getValue(i);
      }

      public int getIndex(String uri, String localName) {
         return -1;
      }

      public int getIndex(String qName) {
         int max = ParserAdapter.this.atts.getLength();

         for(int i = 0; i < max; ++i) {
            if (this.qAtts.getName(i).equals(qName)) {
               return i;
            }
         }

         return -1;
      }

      public String getType(String uri, String localName) {
         return null;
      }

      public String getType(String qName) {
         return this.qAtts.getType(qName).intern();
      }

      public String getValue(String uri, String localName) {
         return null;
      }

      public String getValue(String qName) {
         return this.qAtts.getValue(qName);
      }
   }
}
