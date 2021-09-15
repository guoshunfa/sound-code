package com.sun.org.apache.xml.internal.resolver.readers;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogException;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import com.sun.org.apache.xml.internal.resolver.helpers.Debug;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.AttributeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import sun.reflect.misc.ReflectUtil;

public class SAXCatalogReader implements CatalogReader, ContentHandler, DocumentHandler {
   protected SAXParserFactory parserFactory = null;
   protected String parserClass = null;
   protected Map<String, String> namespaceMap = new HashMap();
   private SAXCatalogParser saxParser = null;
   private boolean abandonHope = false;
   private Catalog catalog;
   protected Debug debug;

   public void setParserFactory(SAXParserFactory parserFactory) {
      this.parserFactory = parserFactory;
   }

   public void setParserClass(String parserClass) {
      this.parserClass = parserClass;
   }

   public SAXParserFactory getParserFactory() {
      return this.parserFactory;
   }

   public String getParserClass() {
      return this.parserClass;
   }

   public SAXCatalogReader() {
      this.debug = CatalogManager.getStaticManager().debug;
      this.parserFactory = null;
      this.parserClass = null;
   }

   public SAXCatalogReader(SAXParserFactory parserFactory) {
      this.debug = CatalogManager.getStaticManager().debug;
      this.parserFactory = parserFactory;
   }

   public SAXCatalogReader(String parserClass) {
      this.debug = CatalogManager.getStaticManager().debug;
      this.parserClass = parserClass;
   }

   public void setCatalogParser(String namespaceURI, String rootElement, String parserClass) {
      if (namespaceURI == null) {
         this.namespaceMap.put(rootElement, parserClass);
      } else {
         this.namespaceMap.put("{" + namespaceURI + "}" + rootElement, parserClass);
      }

   }

   public String getCatalogParser(String namespaceURI, String rootElement) {
      return namespaceURI == null ? (String)this.namespaceMap.get(rootElement) : (String)this.namespaceMap.get("{" + namespaceURI + "}" + rootElement);
   }

   public void readCatalog(Catalog catalog, String fileUrl) throws MalformedURLException, IOException, CatalogException {
      URL url = null;

      try {
         url = new URL(fileUrl);
      } catch (MalformedURLException var6) {
         url = new URL("file:///" + fileUrl);
      }

      this.debug = catalog.getCatalogManager().debug;

      try {
         URLConnection urlCon = url.openConnection();
         this.readCatalog(catalog, urlCon.getInputStream());
      } catch (FileNotFoundException var5) {
         catalog.getCatalogManager().debug.message(1, "Failed to load catalog, file not found", url.toString());
      }

   }

   public void readCatalog(Catalog catalog, InputStream is) throws IOException, CatalogException {
      if (this.parserFactory == null && this.parserClass == null) {
         this.debug.message(1, "Cannot read SAX catalog without a parser");
         throw new CatalogException(6);
      } else {
         this.debug = catalog.getCatalogManager().debug;
         EntityResolver bResolver = catalog.getCatalogManager().getBootstrapResolver();
         this.catalog = catalog;

         try {
            if (this.parserFactory != null) {
               SAXParser parser = this.parserFactory.newSAXParser();
               SAXParserHandler spHandler = new SAXParserHandler();
               spHandler.setContentHandler(this);
               if (bResolver != null) {
                  spHandler.setEntityResolver(bResolver);
               }

               parser.parse((InputSource)(new InputSource(is)), (DefaultHandler)spHandler);
            } else {
               Parser parser = (Parser)ReflectUtil.forName(this.parserClass).newInstance();
               parser.setDocumentHandler(this);
               if (bResolver != null) {
                  parser.setEntityResolver(bResolver);
               }

               parser.parse(new InputSource(is));
            }

         } catch (ClassNotFoundException var8) {
            throw new CatalogException(6);
         } catch (IllegalAccessException var9) {
            throw new CatalogException(6);
         } catch (InstantiationException var10) {
            throw new CatalogException(6);
         } catch (ParserConfigurationException var11) {
            throw new CatalogException(5);
         } catch (SAXException var12) {
            Exception e = var12.getException();
            UnknownHostException uhe = new UnknownHostException();
            FileNotFoundException fnfe = new FileNotFoundException();
            if (e != null) {
               if (e.getClass() == uhe.getClass()) {
                  throw new CatalogException(7, e.toString());
               }

               if (e.getClass() == fnfe.getClass()) {
                  throw new CatalogException(7, e.toString());
               }
            }

            throw new CatalogException(var12);
         }
      }
   }

   public void setDocumentLocator(Locator locator) {
      if (this.saxParser != null) {
         this.saxParser.setDocumentLocator(locator);
      }

   }

   public void startDocument() throws SAXException {
      this.saxParser = null;
      this.abandonHope = false;
   }

   public void endDocument() throws SAXException {
      if (this.saxParser != null) {
         this.saxParser.endDocument();
      }

   }

   public void startElement(String name, AttributeList atts) throws SAXException {
      if (!this.abandonHope) {
         if (this.saxParser == null) {
            String prefix = "";
            if (name.indexOf(58) > 0) {
               prefix = name.substring(0, name.indexOf(58));
            }

            String localName = name;
            if (name.indexOf(58) > 0) {
               localName = name.substring(name.indexOf(58) + 1);
            }

            String namespaceURI = null;
            if (prefix.equals("")) {
               namespaceURI = atts.getValue("xmlns");
            } else {
               namespaceURI = atts.getValue("xmlns:" + prefix);
            }

            String saxParserClass = this.getCatalogParser(namespaceURI, localName);
            if (saxParserClass == null) {
               this.abandonHope = true;
               if (namespaceURI == null) {
                  this.debug.message(2, "No Catalog parser for " + name);
               } else {
                  this.debug.message(2, "No Catalog parser for {" + namespaceURI + "}" + name);
               }

               return;
            }

            try {
               this.saxParser = (SAXCatalogParser)ReflectUtil.forName(saxParserClass).newInstance();
               this.saxParser.setCatalog(this.catalog);
               this.saxParser.startDocument();
               this.saxParser.startElement(name, atts);
            } catch (ClassNotFoundException var8) {
               this.saxParser = null;
               this.abandonHope = true;
               this.debug.message(2, var8.toString());
            } catch (InstantiationException var9) {
               this.saxParser = null;
               this.abandonHope = true;
               this.debug.message(2, var9.toString());
            } catch (IllegalAccessException var10) {
               this.saxParser = null;
               this.abandonHope = true;
               this.debug.message(2, var10.toString());
            } catch (ClassCastException var11) {
               this.saxParser = null;
               this.abandonHope = true;
               this.debug.message(2, var11.toString());
            }
         } else {
            this.saxParser.startElement(name, atts);
         }

      }
   }

   public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
      if (!this.abandonHope) {
         if (this.saxParser == null) {
            String saxParserClass = this.getCatalogParser(namespaceURI, localName);
            if (saxParserClass == null) {
               this.abandonHope = true;
               if (namespaceURI == null) {
                  this.debug.message(2, "No Catalog parser for " + localName);
               } else {
                  this.debug.message(2, "No Catalog parser for {" + namespaceURI + "}" + localName);
               }

               return;
            }

            try {
               this.saxParser = (SAXCatalogParser)ReflectUtil.forName(saxParserClass).newInstance();
               this.saxParser.setCatalog(this.catalog);
               this.saxParser.startDocument();
               this.saxParser.startElement(namespaceURI, localName, qName, atts);
            } catch (ClassNotFoundException var7) {
               this.saxParser = null;
               this.abandonHope = true;
               this.debug.message(2, var7.toString());
            } catch (InstantiationException var8) {
               this.saxParser = null;
               this.abandonHope = true;
               this.debug.message(2, var8.toString());
            } catch (IllegalAccessException var9) {
               this.saxParser = null;
               this.abandonHope = true;
               this.debug.message(2, var9.toString());
            } catch (ClassCastException var10) {
               this.saxParser = null;
               this.abandonHope = true;
               this.debug.message(2, var10.toString());
            }
         } else {
            this.saxParser.startElement(namespaceURI, localName, qName, atts);
         }

      }
   }

   public void endElement(String name) throws SAXException {
      if (this.saxParser != null) {
         this.saxParser.endElement(name);
      }

   }

   public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
      if (this.saxParser != null) {
         this.saxParser.endElement(namespaceURI, localName, qName);
      }

   }

   public void characters(char[] ch, int start, int length) throws SAXException {
      if (this.saxParser != null) {
         this.saxParser.characters(ch, start, length);
      }

   }

   public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
      if (this.saxParser != null) {
         this.saxParser.ignorableWhitespace(ch, start, length);
      }

   }

   public void processingInstruction(String target, String data) throws SAXException {
      if (this.saxParser != null) {
         this.saxParser.processingInstruction(target, data);
      }

   }

   public void startPrefixMapping(String prefix, String uri) throws SAXException {
      if (this.saxParser != null) {
         this.saxParser.startPrefixMapping(prefix, uri);
      }

   }

   public void endPrefixMapping(String prefix) throws SAXException {
      if (this.saxParser != null) {
         this.saxParser.endPrefixMapping(prefix);
      }

   }

   public void skippedEntity(String name) throws SAXException {
      if (this.saxParser != null) {
         this.saxParser.skippedEntity(name);
      }

   }
}
