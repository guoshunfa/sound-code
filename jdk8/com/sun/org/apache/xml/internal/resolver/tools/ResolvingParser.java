package com.sun.org.apache.xml.internal.resolver.tools;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import com.sun.org.apache.xml.internal.resolver.helpers.FileURL;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import jdk.xml.internal.JdkXmlUtils;
import org.xml.sax.AttributeList;
import org.xml.sax.DTDHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;

/** @deprecated */
public class ResolvingParser implements Parser, DTDHandler, DocumentHandler, EntityResolver {
   public static boolean namespaceAware = true;
   public static boolean validating = false;
   public static boolean suppressExplanation = false;
   private SAXParser saxParser = null;
   private Parser parser = null;
   private DocumentHandler documentHandler = null;
   private DTDHandler dtdHandler = null;
   private CatalogManager catalogManager = CatalogManager.getStaticManager();
   private CatalogResolver catalogResolver = null;
   private CatalogResolver piCatalogResolver = null;
   private boolean allowXMLCatalogPI = false;
   private boolean oasisXMLCatalogPI = false;
   private URL baseURL = null;

   public ResolvingParser() {
      this.initParser();
   }

   public ResolvingParser(CatalogManager manager) {
      this.catalogManager = manager;
      this.initParser();
   }

   private void initParser() {
      this.catalogResolver = new CatalogResolver(this.catalogManager);
      SAXParserFactory spf = JdkXmlUtils.getSAXFactory(this.catalogManager.overrideDefaultParser());
      spf.setValidating(validating);

      try {
         this.saxParser = spf.newSAXParser();
         this.parser = this.saxParser.getParser();
         this.documentHandler = null;
         this.dtdHandler = null;
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   public Catalog getCatalog() {
      return this.catalogResolver.getCatalog();
   }

   public void parse(InputSource input) throws IOException, SAXException {
      this.setupParse(input.getSystemId());

      try {
         this.parser.parse(input);
      } catch (InternalError var3) {
         this.explain(input.getSystemId());
         throw var3;
      }
   }

   public void parse(String systemId) throws IOException, SAXException {
      this.setupParse(systemId);

      try {
         this.parser.parse(systemId);
      } catch (InternalError var3) {
         this.explain(systemId);
         throw var3;
      }
   }

   public void setDocumentHandler(DocumentHandler handler) {
      this.documentHandler = handler;
   }

   public void setDTDHandler(DTDHandler handler) {
      this.dtdHandler = handler;
   }

   public void setEntityResolver(EntityResolver resolver) {
   }

   public void setErrorHandler(ErrorHandler handler) {
      this.parser.setErrorHandler(handler);
   }

   public void setLocale(Locale locale) throws SAXException {
      this.parser.setLocale(locale);
   }

   public void characters(char[] ch, int start, int length) throws SAXException {
      if (this.documentHandler != null) {
         this.documentHandler.characters(ch, start, length);
      }

   }

   public void endDocument() throws SAXException {
      if (this.documentHandler != null) {
         this.documentHandler.endDocument();
      }

   }

   public void endElement(String name) throws SAXException {
      if (this.documentHandler != null) {
         this.documentHandler.endElement(name);
      }

   }

   public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
      if (this.documentHandler != null) {
         this.documentHandler.ignorableWhitespace(ch, start, length);
      }

   }

   public void processingInstruction(String target, String pidata) throws SAXException {
      if (target.equals("oasis-xml-catalog")) {
         URL catalog = null;
         int pos = pidata.indexOf("catalog=");
         if (pos >= 0) {
            String data = pidata.substring(pos + 8);
            if (data.length() > 1) {
               String quote = data.substring(0, 1);
               data = data.substring(1);
               pos = data.indexOf(quote);
               if (pos >= 0) {
                  data = data.substring(0, pos);

                  try {
                     if (this.baseURL != null) {
                        catalog = new URL(this.baseURL, data);
                     } else {
                        catalog = new URL(data);
                     }
                  } catch (MalformedURLException var9) {
                  }
               }
            }
         }

         if (this.allowXMLCatalogPI) {
            if (this.catalogManager.getAllowOasisXMLCatalogPI()) {
               this.catalogManager.debug.message(4, "oasis-xml-catalog PI", pidata);
               if (catalog != null) {
                  try {
                     this.catalogManager.debug.message(4, "oasis-xml-catalog", catalog.toString());
                     this.oasisXMLCatalogPI = true;
                     if (this.piCatalogResolver == null) {
                        this.piCatalogResolver = new CatalogResolver(true);
                     }

                     this.piCatalogResolver.getCatalog().parseCatalog(catalog.toString());
                  } catch (Exception var8) {
                     this.catalogManager.debug.message(3, "Exception parsing oasis-xml-catalog: " + catalog.toString());
                  }
               } else {
                  this.catalogManager.debug.message(3, "PI oasis-xml-catalog unparseable: " + pidata);
               }
            } else {
               this.catalogManager.debug.message(4, "PI oasis-xml-catalog ignored: " + pidata);
            }
         } else {
            this.catalogManager.debug.message(3, "PI oasis-xml-catalog occurred in an invalid place: " + pidata);
         }
      } else if (this.documentHandler != null) {
         this.documentHandler.processingInstruction(target, pidata);
      }

   }

   public void setDocumentLocator(Locator locator) {
      if (this.documentHandler != null) {
         this.documentHandler.setDocumentLocator(locator);
      }

   }

   public void startDocument() throws SAXException {
      if (this.documentHandler != null) {
         this.documentHandler.startDocument();
      }

   }

   public void startElement(String name, AttributeList atts) throws SAXException {
      this.allowXMLCatalogPI = false;
      if (this.documentHandler != null) {
         this.documentHandler.startElement(name, atts);
      }

   }

   public void notationDecl(String name, String publicId, String systemId) throws SAXException {
      this.allowXMLCatalogPI = false;
      if (this.dtdHandler != null) {
         this.dtdHandler.notationDecl(name, publicId, systemId);
      }

   }

   public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
      this.allowXMLCatalogPI = false;
      if (this.dtdHandler != null) {
         this.dtdHandler.unparsedEntityDecl(name, publicId, systemId, notationName);
      }

   }

   public InputSource resolveEntity(String publicId, String systemId) {
      this.allowXMLCatalogPI = false;
      String resolved = this.catalogResolver.getResolvedEntity(publicId, systemId);
      if (resolved == null && this.piCatalogResolver != null) {
         resolved = this.piCatalogResolver.getResolvedEntity(publicId, systemId);
      }

      if (resolved != null) {
         try {
            InputSource iSource = new InputSource(resolved);
            iSource.setPublicId(publicId);
            URL url = new URL(resolved);
            InputStream iStream = url.openStream();
            iSource.setByteStream(iStream);
            return iSource;
         } catch (Exception var7) {
            this.catalogManager.debug.message(1, "Failed to create InputSource", resolved);
            return null;
         }
      } else {
         return null;
      }
   }

   private void setupParse(String systemId) {
      this.allowXMLCatalogPI = true;
      this.parser.setEntityResolver(this);
      this.parser.setDocumentHandler(this);
      this.parser.setDTDHandler(this);
      URL cwd = null;

      try {
         cwd = FileURL.makeURL("basename");
      } catch (MalformedURLException var6) {
         cwd = null;
      }

      try {
         this.baseURL = new URL(systemId);
      } catch (MalformedURLException var7) {
         if (cwd != null) {
            try {
               this.baseURL = new URL(cwd, systemId);
            } catch (MalformedURLException var5) {
               this.baseURL = null;
            }
         } else {
            this.baseURL = null;
         }
      }

   }

   private void explain(String systemId) {
      if (!suppressExplanation) {
         System.out.println("Parser probably encountered bad URI in " + systemId);
         System.out.println("For example, replace '/some/uri' with 'file:/some/uri'.");
      }

   }
}
