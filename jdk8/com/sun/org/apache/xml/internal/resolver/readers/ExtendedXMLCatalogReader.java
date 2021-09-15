package com.sun.org.apache.xml.internal.resolver.readers;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogEntry;
import com.sun.org.apache.xml.internal.resolver.CatalogException;
import com.sun.org.apache.xml.internal.resolver.Resolver;
import java.util.Vector;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ExtendedXMLCatalogReader extends OASISXMLCatalogReader {
   public static final String extendedNamespaceName = "http://nwalsh.com/xcatalog/1.0";

   public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
      boolean inExtension = this.inExtensionNamespace();
      super.startElement(namespaceURI, localName, qName, atts);
      int entryType = -1;
      Vector entryArgs = new Vector();
      if (namespaceURI != null && "http://nwalsh.com/xcatalog/1.0".equals(namespaceURI) && !inExtension) {
         if (atts.getValue("xml:base") != null) {
            String baseURI = atts.getValue("xml:base");
            entryType = Catalog.BASE;
            entryArgs.add(baseURI);
            this.baseURIStack.push(baseURI);
            this.debug.message(4, "xml:base", baseURI);

            try {
               CatalogEntry ce = new CatalogEntry(entryType, entryArgs);
               this.catalog.addEntry(ce);
            } catch (CatalogException var11) {
               if (var11.getExceptionType() == 3) {
                  this.debug.message(1, "Invalid catalog entry type", localName);
               } else if (var11.getExceptionType() == 2) {
                  this.debug.message(1, "Invalid catalog entry (base)", localName);
               }
            }

            entryType = -1;
            entryArgs = new Vector();
         } else {
            this.baseURIStack.push(this.baseURIStack.peek());
         }

         if (localName.equals("uriSuffix")) {
            if (this.checkAttributes(atts, "suffix", "uri")) {
               entryType = Resolver.URISUFFIX;
               entryArgs.add(atts.getValue("suffix"));
               entryArgs.add(atts.getValue("uri"));
               this.debug.message(4, "uriSuffix", atts.getValue("suffix"), atts.getValue("uri"));
            }
         } else if (localName.equals("systemSuffix")) {
            if (this.checkAttributes(atts, "suffix", "uri")) {
               entryType = Resolver.SYSTEMSUFFIX;
               entryArgs.add(atts.getValue("suffix"));
               entryArgs.add(atts.getValue("uri"));
               this.debug.message(4, "systemSuffix", atts.getValue("suffix"), atts.getValue("uri"));
            }
         } else {
            this.debug.message(1, "Invalid catalog entry type", localName);
         }

         if (entryType >= 0) {
            try {
               CatalogEntry ce = new CatalogEntry(entryType, entryArgs);
               this.catalog.addEntry(ce);
            } catch (CatalogException var10) {
               if (var10.getExceptionType() == 3) {
                  this.debug.message(1, "Invalid catalog entry type", localName);
               } else if (var10.getExceptionType() == 2) {
                  this.debug.message(1, "Invalid catalog entry", localName);
               }
            }
         }
      }

   }

   public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
      super.endElement(namespaceURI, localName, qName);
      boolean inExtension = this.inExtensionNamespace();
      int entryType = true;
      Vector entryArgs = new Vector();
      if (namespaceURI != null && "http://nwalsh.com/xcatalog/1.0".equals(namespaceURI) && !inExtension) {
         String popURI = (String)this.baseURIStack.pop();
         String baseURI = (String)this.baseURIStack.peek();
         if (!baseURI.equals(popURI)) {
            Catalog var10000 = this.catalog;
            int entryType = Catalog.BASE;
            entryArgs.add(baseURI);
            this.debug.message(4, "(reset) xml:base", baseURI);

            try {
               CatalogEntry ce = new CatalogEntry(entryType, entryArgs);
               this.catalog.addEntry(ce);
            } catch (CatalogException var10) {
               if (var10.getExceptionType() == 3) {
                  this.debug.message(1, "Invalid catalog entry type", localName);
               } else if (var10.getExceptionType() == 2) {
                  this.debug.message(1, "Invalid catalog entry (rbase)", localName);
               }
            }
         }
      }

   }
}
