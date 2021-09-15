package com.sun.org.apache.xml.internal.resolver.readers;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogEntry;
import com.sun.org.apache.xml.internal.resolver.CatalogException;
import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;
import java.util.Vector;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class XCatalogReader extends SAXCatalogReader implements SAXCatalogParser {
   protected Catalog catalog = null;

   public void setCatalog(Catalog catalog) {
      this.catalog = catalog;
   }

   public Catalog getCatalog() {
      return this.catalog;
   }

   public XCatalogReader(SAXParserFactory parserFactory) {
      super(parserFactory);
   }

   public void setDocumentLocator(Locator locator) {
   }

   public void startDocument() throws SAXException {
   }

   public void endDocument() throws SAXException {
   }

   public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
      int entryType = -1;
      Vector entryArgs = new Vector();
      Catalog var10000;
      if (localName.equals("Base")) {
         var10000 = this.catalog;
         entryType = Catalog.BASE;
         entryArgs.add(atts.getValue("HRef"));
         this.catalog.getCatalogManager().debug.message(4, "Base", atts.getValue("HRef"));
      } else if (localName.equals("Delegate")) {
         var10000 = this.catalog;
         entryType = Catalog.DELEGATE_PUBLIC;
         entryArgs.add(atts.getValue("PublicId"));
         entryArgs.add(atts.getValue("HRef"));
         this.catalog.getCatalogManager().debug.message(4, "Delegate", PublicId.normalize(atts.getValue("PublicId")), atts.getValue("HRef"));
      } else if (localName.equals("Extend")) {
         var10000 = this.catalog;
         entryType = Catalog.CATALOG;
         entryArgs.add(atts.getValue("HRef"));
         this.catalog.getCatalogManager().debug.message(4, "Extend", atts.getValue("HRef"));
      } else if (localName.equals("Map")) {
         var10000 = this.catalog;
         entryType = Catalog.PUBLIC;
         entryArgs.add(atts.getValue("PublicId"));
         entryArgs.add(atts.getValue("HRef"));
         this.catalog.getCatalogManager().debug.message(4, "Map", PublicId.normalize(atts.getValue("PublicId")), atts.getValue("HRef"));
      } else if (localName.equals("Remap")) {
         var10000 = this.catalog;
         entryType = Catalog.SYSTEM;
         entryArgs.add(atts.getValue("SystemId"));
         entryArgs.add(atts.getValue("HRef"));
         this.catalog.getCatalogManager().debug.message(4, "Remap", atts.getValue("SystemId"), atts.getValue("HRef"));
      } else if (!localName.equals("XMLCatalog")) {
         this.catalog.getCatalogManager().debug.message(1, "Invalid catalog entry type", localName);
      }

      if (entryType >= 0) {
         try {
            CatalogEntry ce = new CatalogEntry(entryType, entryArgs);
            this.catalog.addEntry(ce);
         } catch (CatalogException var8) {
            if (var8.getExceptionType() == 3) {
               this.catalog.getCatalogManager().debug.message(1, "Invalid catalog entry type", localName);
            } else if (var8.getExceptionType() == 2) {
               this.catalog.getCatalogManager().debug.message(1, "Invalid catalog entry", localName);
            }
         }
      }

   }

   public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
   }

   public void characters(char[] ch, int start, int length) throws SAXException {
   }

   public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
   }

   public void processingInstruction(String target, String data) throws SAXException {
   }
}
