package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.impl.xs.util.SimpleLocator;
import com.sun.org.apache.xerces.internal.jaxp.validation.WrappedSAXException;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class SAX2XNI implements ContentHandler, XMLDocumentSource {
   private XMLDocumentHandler fCore;
   private final NamespaceSupport nsContext = new NamespaceSupport();
   private final SymbolTable symbolTable = new SymbolTable();
   private Locator locator;
   private final XMLAttributes xa = new XMLAttributesImpl();

   public SAX2XNI(XMLDocumentHandler core) {
      this.fCore = core;
   }

   public void setDocumentHandler(XMLDocumentHandler handler) {
      this.fCore = handler;
   }

   public XMLDocumentHandler getDocumentHandler() {
      return this.fCore;
   }

   public void startDocument() throws SAXException {
      try {
         this.nsContext.reset();
         Object xmlLocator;
         if (this.locator == null) {
            xmlLocator = new SimpleLocator((String)null, (String)null, -1, -1);
         } else {
            xmlLocator = new LocatorWrapper(this.locator);
         }

         this.fCore.startDocument((XMLLocator)xmlLocator, (String)null, this.nsContext, (Augmentations)null);
      } catch (WrappedSAXException var2) {
         throw var2.exception;
      }
   }

   public void endDocument() throws SAXException {
      try {
         this.fCore.endDocument((Augmentations)null);
      } catch (WrappedSAXException var2) {
         throw var2.exception;
      }
   }

   public void startElement(String uri, String local, String qname, Attributes att) throws SAXException {
      try {
         this.fCore.startElement(this.createQName(uri, local, qname), this.createAttributes(att), (Augmentations)null);
      } catch (WrappedSAXException var6) {
         throw var6.exception;
      }
   }

   public void endElement(String uri, String local, String qname) throws SAXException {
      try {
         this.fCore.endElement(this.createQName(uri, local, qname), (Augmentations)null);
      } catch (WrappedSAXException var5) {
         throw var5.exception;
      }
   }

   public void characters(char[] buf, int offset, int len) throws SAXException {
      try {
         this.fCore.characters(new XMLString(buf, offset, len), (Augmentations)null);
      } catch (WrappedSAXException var5) {
         throw var5.exception;
      }
   }

   public void ignorableWhitespace(char[] buf, int offset, int len) throws SAXException {
      try {
         this.fCore.ignorableWhitespace(new XMLString(buf, offset, len), (Augmentations)null);
      } catch (WrappedSAXException var5) {
         throw var5.exception;
      }
   }

   public void startPrefixMapping(String prefix, String uri) {
      this.nsContext.pushContext();
      this.nsContext.declarePrefix(prefix, uri);
   }

   public void endPrefixMapping(String prefix) {
      this.nsContext.popContext();
   }

   public void processingInstruction(String target, String data) throws SAXException {
      try {
         this.fCore.processingInstruction(this.symbolize(target), this.createXMLString(data), (Augmentations)null);
      } catch (WrappedSAXException var4) {
         throw var4.exception;
      }
   }

   public void skippedEntity(String name) {
   }

   public void setDocumentLocator(Locator _loc) {
      this.locator = _loc;
   }

   private QName createQName(String uri, String local, String raw) {
      int idx = raw.indexOf(58);
      if (local.length() == 0) {
         uri = "";
         if (idx < 0) {
            local = raw;
         } else {
            local = raw.substring(idx + 1);
         }
      }

      String prefix;
      if (idx < 0) {
         prefix = null;
      } else {
         prefix = raw.substring(0, idx);
      }

      if (uri != null && uri.length() == 0) {
         uri = null;
      }

      return new QName(this.symbolize(prefix), this.symbolize(local), this.symbolize(raw), this.symbolize(uri));
   }

   private String symbolize(String s) {
      return s == null ? null : this.symbolTable.addSymbol(s);
   }

   private XMLString createXMLString(String str) {
      return new XMLString(str.toCharArray(), 0, str.length());
   }

   private XMLAttributes createAttributes(Attributes att) {
      this.xa.removeAllAttributes();
      int len = att.getLength();

      for(int i = 0; i < len; ++i) {
         this.xa.addAttribute(this.createQName(att.getURI(i), att.getLocalName(i), att.getQName(i)), att.getType(i), att.getValue(i));
      }

      return this.xa;
   }
}
