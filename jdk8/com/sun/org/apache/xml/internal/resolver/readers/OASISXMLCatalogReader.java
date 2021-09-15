package com.sun.org.apache.xml.internal.resolver.readers;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogEntry;
import com.sun.org.apache.xml.internal.resolver.CatalogException;
import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class OASISXMLCatalogReader extends SAXCatalogReader implements SAXCatalogParser {
   protected Catalog catalog = null;
   public static final String namespaceName = "urn:oasis:names:tc:entity:xmlns:xml:catalog";
   public static final String tr9401NamespaceName = "urn:oasis:names:tc:entity:xmlns:tr9401:catalog";
   protected Stack baseURIStack = new Stack();
   protected Stack overrideStack = new Stack();
   protected Stack namespaceStack = new Stack();

   public void setCatalog(Catalog catalog) {
      this.catalog = catalog;
      this.debug = catalog.getCatalogManager().debug;
   }

   public Catalog getCatalog() {
      return this.catalog;
   }

   protected boolean inExtensionNamespace() {
      boolean inExtension = false;
      Enumeration elements = this.namespaceStack.elements();

      while(!inExtension && elements.hasMoreElements()) {
         String ns = (String)elements.nextElement();
         if (ns == null) {
            inExtension = true;
         } else {
            inExtension = !ns.equals("urn:oasis:names:tc:entity:xmlns:tr9401:catalog") && !ns.equals("urn:oasis:names:tc:entity:xmlns:xml:catalog");
         }
      }

      return inExtension;
   }

   public void setDocumentLocator(Locator locator) {
   }

   public void startDocument() throws SAXException {
      this.baseURIStack.push(this.catalog.getCurrentBase());
      this.overrideStack.push(this.catalog.getDefaultOverride());
   }

   public void endDocument() throws SAXException {
   }

   public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
      int entryType = -1;
      Vector entryArgs = new Vector();
      this.namespaceStack.push(namespaceURI);
      boolean inExtension = this.inExtensionNamespace();
      String override;
      CatalogEntry ce;
      CatalogEntry ce;
      if (namespaceURI != null && "urn:oasis:names:tc:entity:xmlns:xml:catalog".equals(namespaceURI) && !inExtension) {
         if (atts.getValue("xml:base") != null) {
            override = atts.getValue("xml:base");
            entryType = Catalog.BASE;
            entryArgs.add(override);
            this.baseURIStack.push(override);
            this.debug.message(4, "xml:base", override);

            try {
               ce = new CatalogEntry(entryType, entryArgs);
               this.catalog.addEntry(ce);
            } catch (CatalogException var14) {
               if (var14.getExceptionType() == 3) {
                  this.debug.message(1, "Invalid catalog entry type", localName);
               } else if (var14.getExceptionType() == 2) {
                  this.debug.message(1, "Invalid catalog entry (base)", localName);
               }
            }

            entryType = -1;
            entryArgs = new Vector();
         } else {
            this.baseURIStack.push(this.baseURIStack.peek());
         }

         if ((localName.equals("catalog") || localName.equals("group")) && atts.getValue("prefer") != null) {
            override = atts.getValue("prefer");
            if (override.equals("public")) {
               override = "yes";
            } else if (override.equals("system")) {
               override = "no";
            } else {
               this.debug.message(1, "Invalid prefer: must be 'system' or 'public'", localName);
               override = this.catalog.getDefaultOverride();
            }

            entryType = Catalog.OVERRIDE;
            entryArgs.add(override);
            this.overrideStack.push(override);
            this.debug.message(4, "override", override);

            try {
               ce = new CatalogEntry(entryType, entryArgs);
               this.catalog.addEntry(ce);
            } catch (CatalogException var13) {
               if (var13.getExceptionType() == 3) {
                  this.debug.message(1, "Invalid catalog entry type", localName);
               } else if (var13.getExceptionType() == 2) {
                  this.debug.message(1, "Invalid catalog entry (override)", localName);
               }
            }

            entryType = -1;
            entryArgs = new Vector();
         } else {
            this.overrideStack.push(this.overrideStack.peek());
         }

         if (localName.equals("delegatePublic")) {
            if (this.checkAttributes(atts, "publicIdStartString", "catalog")) {
               entryType = Catalog.DELEGATE_PUBLIC;
               entryArgs.add(atts.getValue("publicIdStartString"));
               entryArgs.add(atts.getValue("catalog"));
               this.debug.message(4, "delegatePublic", PublicId.normalize(atts.getValue("publicIdStartString")), atts.getValue("catalog"));
            }
         } else if (localName.equals("delegateSystem")) {
            if (this.checkAttributes(atts, "systemIdStartString", "catalog")) {
               entryType = Catalog.DELEGATE_SYSTEM;
               entryArgs.add(atts.getValue("systemIdStartString"));
               entryArgs.add(atts.getValue("catalog"));
               this.debug.message(4, "delegateSystem", atts.getValue("systemIdStartString"), atts.getValue("catalog"));
            }
         } else if (localName.equals("delegateURI")) {
            if (this.checkAttributes(atts, "uriStartString", "catalog")) {
               entryType = Catalog.DELEGATE_URI;
               entryArgs.add(atts.getValue("uriStartString"));
               entryArgs.add(atts.getValue("catalog"));
               this.debug.message(4, "delegateURI", atts.getValue("uriStartString"), atts.getValue("catalog"));
            }
         } else if (localName.equals("rewriteSystem")) {
            if (this.checkAttributes(atts, "systemIdStartString", "rewritePrefix")) {
               entryType = Catalog.REWRITE_SYSTEM;
               entryArgs.add(atts.getValue("systemIdStartString"));
               entryArgs.add(atts.getValue("rewritePrefix"));
               this.debug.message(4, "rewriteSystem", atts.getValue("systemIdStartString"), atts.getValue("rewritePrefix"));
            }
         } else if (localName.equals("systemSuffix")) {
            if (this.checkAttributes(atts, "systemIdSuffix", "uri")) {
               entryType = Catalog.SYSTEM_SUFFIX;
               entryArgs.add(atts.getValue("systemIdSuffix"));
               entryArgs.add(atts.getValue("uri"));
               this.debug.message(4, "systemSuffix", atts.getValue("systemIdSuffix"), atts.getValue("uri"));
            }
         } else if (localName.equals("rewriteURI")) {
            if (this.checkAttributes(atts, "uriStartString", "rewritePrefix")) {
               entryType = Catalog.REWRITE_URI;
               entryArgs.add(atts.getValue("uriStartString"));
               entryArgs.add(atts.getValue("rewritePrefix"));
               this.debug.message(4, "rewriteURI", atts.getValue("uriStartString"), atts.getValue("rewritePrefix"));
            }
         } else if (localName.equals("uriSuffix")) {
            if (this.checkAttributes(atts, "uriSuffix", "uri")) {
               entryType = Catalog.URI_SUFFIX;
               entryArgs.add(atts.getValue("uriSuffix"));
               entryArgs.add(atts.getValue("uri"));
               this.debug.message(4, "uriSuffix", atts.getValue("uriSuffix"), atts.getValue("uri"));
            }
         } else if (localName.equals("nextCatalog")) {
            if (this.checkAttributes(atts, "catalog")) {
               entryType = Catalog.CATALOG;
               entryArgs.add(atts.getValue("catalog"));
               this.debug.message(4, "nextCatalog", atts.getValue("catalog"));
            }
         } else if (localName.equals("public")) {
            if (this.checkAttributes(atts, "publicId", "uri")) {
               entryType = Catalog.PUBLIC;
               entryArgs.add(atts.getValue("publicId"));
               entryArgs.add(atts.getValue("uri"));
               this.debug.message(4, "public", PublicId.normalize(atts.getValue("publicId")), atts.getValue("uri"));
            }
         } else if (localName.equals("system")) {
            if (this.checkAttributes(atts, "systemId", "uri")) {
               entryType = Catalog.SYSTEM;
               entryArgs.add(atts.getValue("systemId"));
               entryArgs.add(atts.getValue("uri"));
               this.debug.message(4, "system", atts.getValue("systemId"), atts.getValue("uri"));
            }
         } else if (localName.equals("uri")) {
            if (this.checkAttributes(atts, "name", "uri")) {
               entryType = Catalog.URI;
               entryArgs.add(atts.getValue("name"));
               entryArgs.add(atts.getValue("uri"));
               this.debug.message(4, "uri", atts.getValue("name"), atts.getValue("uri"));
            }
         } else if (!localName.equals("catalog") && !localName.equals("group")) {
            this.debug.message(1, "Invalid catalog entry type", localName);
         }

         if (entryType >= 0) {
            try {
               ce = new CatalogEntry(entryType, entryArgs);
               this.catalog.addEntry(ce);
            } catch (CatalogException var12) {
               if (var12.getExceptionType() == 3) {
                  this.debug.message(1, "Invalid catalog entry type", localName);
               } else if (var12.getExceptionType() == 2) {
                  this.debug.message(1, "Invalid catalog entry", localName);
               }
            }
         }
      }

      if (namespaceURI != null && "urn:oasis:names:tc:entity:xmlns:tr9401:catalog".equals(namespaceURI) && !inExtension) {
         if (atts.getValue("xml:base") != null) {
            override = atts.getValue("xml:base");
            entryType = Catalog.BASE;
            entryArgs.add(override);
            this.baseURIStack.push(override);
            this.debug.message(4, "xml:base", override);

            try {
               ce = new CatalogEntry(entryType, entryArgs);
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

         Catalog var10000;
         if (localName.equals("doctype")) {
            var10000 = this.catalog;
            entryType = Catalog.DOCTYPE;
            entryArgs.add(atts.getValue("name"));
            entryArgs.add(atts.getValue("uri"));
         } else if (localName.equals("document")) {
            var10000 = this.catalog;
            entryType = Catalog.DOCUMENT;
            entryArgs.add(atts.getValue("uri"));
         } else if (localName.equals("dtddecl")) {
            var10000 = this.catalog;
            entryType = Catalog.DTDDECL;
            entryArgs.add(atts.getValue("publicId"));
            entryArgs.add(atts.getValue("uri"));
         } else if (localName.equals("entity")) {
            entryType = Catalog.ENTITY;
            entryArgs.add(atts.getValue("name"));
            entryArgs.add(atts.getValue("uri"));
         } else if (localName.equals("linktype")) {
            entryType = Catalog.LINKTYPE;
            entryArgs.add(atts.getValue("name"));
            entryArgs.add(atts.getValue("uri"));
         } else if (localName.equals("notation")) {
            entryType = Catalog.NOTATION;
            entryArgs.add(atts.getValue("name"));
            entryArgs.add(atts.getValue("uri"));
         } else if (localName.equals("sgmldecl")) {
            entryType = Catalog.SGMLDECL;
            entryArgs.add(atts.getValue("uri"));
         } else {
            this.debug.message(1, "Invalid catalog entry type", localName);
         }

         if (entryType >= 0) {
            try {
               ce = new CatalogEntry(entryType, entryArgs);
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

   public boolean checkAttributes(Attributes atts, String attName) {
      if (atts.getValue(attName) == null) {
         this.debug.message(1, "Error: required attribute " + attName + " missing.");
         return false;
      } else {
         return true;
      }
   }

   public boolean checkAttributes(Attributes atts, String attName1, String attName2) {
      return this.checkAttributes(atts, attName1) && this.checkAttributes(atts, attName2);
   }

   public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
      int entryType = true;
      Vector entryArgs = new Vector();
      boolean inExtension = this.inExtensionNamespace();
      String popOverride;
      String override;
      CatalogEntry ce;
      int entryType;
      Catalog var10000;
      if (namespaceURI != null && !inExtension && ("urn:oasis:names:tc:entity:xmlns:xml:catalog".equals(namespaceURI) || "urn:oasis:names:tc:entity:xmlns:tr9401:catalog".equals(namespaceURI))) {
         popOverride = (String)this.baseURIStack.pop();
         override = (String)this.baseURIStack.peek();
         if (!override.equals(popOverride)) {
            var10000 = this.catalog;
            entryType = Catalog.BASE;
            entryArgs.add(override);
            this.debug.message(4, "(reset) xml:base", override);

            try {
               ce = new CatalogEntry(entryType, entryArgs);
               this.catalog.addEntry(ce);
            } catch (CatalogException var11) {
               if (var11.getExceptionType() == 3) {
                  this.debug.message(1, "Invalid catalog entry type", localName);
               } else if (var11.getExceptionType() == 2) {
                  this.debug.message(1, "Invalid catalog entry (rbase)", localName);
               }
            }
         }
      }

      if (namespaceURI != null && "urn:oasis:names:tc:entity:xmlns:xml:catalog".equals(namespaceURI) && !inExtension && (localName.equals("catalog") || localName.equals("group"))) {
         popOverride = (String)this.overrideStack.pop();
         override = (String)this.overrideStack.peek();
         if (!override.equals(popOverride)) {
            var10000 = this.catalog;
            entryType = Catalog.OVERRIDE;
            entryArgs.add(override);
            this.overrideStack.push(override);
            this.debug.message(4, "(reset) override", override);

            try {
               ce = new CatalogEntry(entryType, entryArgs);
               this.catalog.addEntry(ce);
            } catch (CatalogException var10) {
               if (var10.getExceptionType() == 3) {
                  this.debug.message(1, "Invalid catalog entry type", localName);
               } else if (var10.getExceptionType() == 2) {
                  this.debug.message(1, "Invalid catalog entry (roverride)", localName);
               }
            }
         }
      }

      this.namespaceStack.pop();
   }

   public void characters(char[] ch, int start, int length) throws SAXException {
   }

   public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
   }

   public void processingInstruction(String target, String data) throws SAXException {
   }

   public void skippedEntity(String name) throws SAXException {
   }

   public void startPrefixMapping(String prefix, String uri) throws SAXException {
   }

   public void endPrefixMapping(String prefix) throws SAXException {
   }
}
