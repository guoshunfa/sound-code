package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.xs.opti.SchemaDOMParser;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SAXLocatorWrapper;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;

final class SchemaContentHandler implements ContentHandler {
   private SymbolTable fSymbolTable;
   private SchemaDOMParser fSchemaDOMParser;
   private final SAXLocatorWrapper fSAXLocatorWrapper = new SAXLocatorWrapper();
   private NamespaceSupport fNamespaceContext = new NamespaceSupport();
   private boolean fNeedPushNSContext;
   private boolean fNamespacePrefixes = false;
   private boolean fStringsInternalized = false;
   private final QName fElementQName = new QName();
   private final QName fAttributeQName = new QName();
   private final XMLAttributesImpl fAttributes = new XMLAttributesImpl();
   private final XMLString fTempString = new XMLString();
   private final XMLStringBuffer fStringBuffer = new XMLStringBuffer();

   public SchemaContentHandler() {
   }

   public Document getDocument() {
      return this.fSchemaDOMParser.getDocument();
   }

   public void setDocumentLocator(Locator locator) {
      this.fSAXLocatorWrapper.setLocator(locator);
   }

   public void startDocument() throws SAXException {
      this.fNeedPushNSContext = true;
      this.fNamespaceContext.reset();

      try {
         this.fSchemaDOMParser.startDocument(this.fSAXLocatorWrapper, (String)null, this.fNamespaceContext, (Augmentations)null);
      } catch (XMLParseException var2) {
         convertToSAXParseException(var2);
      } catch (XNIException var3) {
         convertToSAXException(var3);
      }

   }

   public void endDocument() throws SAXException {
      this.fSAXLocatorWrapper.setLocator((Locator)null);

      try {
         this.fSchemaDOMParser.endDocument((Augmentations)null);
      } catch (XMLParseException var2) {
         convertToSAXParseException(var2);
      } catch (XNIException var3) {
         convertToSAXException(var3);
      }

   }

   public void startPrefixMapping(String prefix, String uri) throws SAXException {
      if (this.fNeedPushNSContext) {
         this.fNeedPushNSContext = false;
         this.fNamespaceContext.pushContext();
      }

      if (!this.fStringsInternalized) {
         prefix = prefix != null ? this.fSymbolTable.addSymbol(prefix) : XMLSymbols.EMPTY_STRING;
         uri = uri != null && uri.length() > 0 ? this.fSymbolTable.addSymbol(uri) : null;
      } else {
         if (prefix == null) {
            prefix = XMLSymbols.EMPTY_STRING;
         }

         if (uri != null && uri.length() == 0) {
            uri = null;
         }
      }

      this.fNamespaceContext.declarePrefix(prefix, uri);
   }

   public void endPrefixMapping(String prefix) throws SAXException {
   }

   public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
      if (this.fNeedPushNSContext) {
         this.fNamespaceContext.pushContext();
      }

      this.fNeedPushNSContext = true;
      this.fillQName(this.fElementQName, uri, localName, qName);
      this.fillXMLAttributes(atts);
      if (!this.fNamespacePrefixes) {
         int prefixCount = this.fNamespaceContext.getDeclaredPrefixCount();
         if (prefixCount > 0) {
            this.addNamespaceDeclarations(prefixCount);
         }
      }

      try {
         this.fSchemaDOMParser.startElement(this.fElementQName, this.fAttributes, (Augmentations)null);
      } catch (XMLParseException var6) {
         convertToSAXParseException(var6);
      } catch (XNIException var7) {
         convertToSAXException(var7);
      }

   }

   public void endElement(String uri, String localName, String qName) throws SAXException {
      this.fillQName(this.fElementQName, uri, localName, qName);

      try {
         this.fSchemaDOMParser.endElement(this.fElementQName, (Augmentations)null);
      } catch (XMLParseException var9) {
         convertToSAXParseException(var9);
      } catch (XNIException var10) {
         convertToSAXException(var10);
      } finally {
         this.fNamespaceContext.popContext();
      }

   }

   public void characters(char[] ch, int start, int length) throws SAXException {
      try {
         this.fTempString.setValues(ch, start, length);
         this.fSchemaDOMParser.characters(this.fTempString, (Augmentations)null);
      } catch (XMLParseException var5) {
         convertToSAXParseException(var5);
      } catch (XNIException var6) {
         convertToSAXException(var6);
      }

   }

   public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
      try {
         this.fTempString.setValues(ch, start, length);
         this.fSchemaDOMParser.ignorableWhitespace(this.fTempString, (Augmentations)null);
      } catch (XMLParseException var5) {
         convertToSAXParseException(var5);
      } catch (XNIException var6) {
         convertToSAXException(var6);
      }

   }

   public void processingInstruction(String target, String data) throws SAXException {
      try {
         this.fTempString.setValues(data.toCharArray(), 0, data.length());
         this.fSchemaDOMParser.processingInstruction(target, this.fTempString, (Augmentations)null);
      } catch (XMLParseException var4) {
         convertToSAXParseException(var4);
      } catch (XNIException var5) {
         convertToSAXException(var5);
      }

   }

   public void skippedEntity(String arg) throws SAXException {
   }

   private void fillQName(QName toFill, String uri, String localpart, String rawname) {
      if (!this.fStringsInternalized) {
         uri = uri != null && uri.length() > 0 ? this.fSymbolTable.addSymbol(uri) : null;
         localpart = localpart != null ? this.fSymbolTable.addSymbol(localpart) : XMLSymbols.EMPTY_STRING;
         rawname = rawname != null ? this.fSymbolTable.addSymbol(rawname) : XMLSymbols.EMPTY_STRING;
      } else {
         if (uri != null && uri.length() == 0) {
            uri = null;
         }

         if (localpart == null) {
            localpart = XMLSymbols.EMPTY_STRING;
         }

         if (rawname == null) {
            rawname = XMLSymbols.EMPTY_STRING;
         }
      }

      String prefix = XMLSymbols.EMPTY_STRING;
      int prefixIdx = rawname.indexOf(58);
      if (prefixIdx != -1) {
         prefix = this.fSymbolTable.addSymbol(rawname.substring(0, prefixIdx));
         if (localpart == XMLSymbols.EMPTY_STRING) {
            localpart = this.fSymbolTable.addSymbol(rawname.substring(prefixIdx + 1));
         }
      } else if (localpart == XMLSymbols.EMPTY_STRING) {
         localpart = rawname;
      }

      toFill.setValues(prefix, localpart, rawname, uri);
   }

   private void fillXMLAttributes(Attributes atts) {
      this.fAttributes.removeAllAttributes();
      int attrCount = atts.getLength();

      for(int i = 0; i < attrCount; ++i) {
         this.fillQName(this.fAttributeQName, atts.getURI(i), atts.getLocalName(i), atts.getQName(i));
         String type = atts.getType(i);
         this.fAttributes.addAttributeNS(this.fAttributeQName, type != null ? type : XMLSymbols.fCDATASymbol, atts.getValue(i));
         this.fAttributes.setSpecified(i, true);
      }

   }

   private void addNamespaceDeclarations(int prefixCount) {
      String prefix = null;
      String localpart = null;
      String rawname = null;
      String nsPrefix = null;
      String nsURI = null;

      for(int i = 0; i < prefixCount; ++i) {
         nsPrefix = this.fNamespaceContext.getDeclaredPrefixAt(i);
         nsURI = this.fNamespaceContext.getURI(nsPrefix);
         if (nsPrefix.length() > 0) {
            prefix = XMLSymbols.PREFIX_XMLNS;
            localpart = nsPrefix;
            this.fStringBuffer.clear();
            this.fStringBuffer.append(prefix);
            this.fStringBuffer.append(':');
            this.fStringBuffer.append(nsPrefix);
            rawname = this.fSymbolTable.addSymbol(this.fStringBuffer.ch, this.fStringBuffer.offset, this.fStringBuffer.length);
         } else {
            prefix = XMLSymbols.EMPTY_STRING;
            localpart = XMLSymbols.PREFIX_XMLNS;
            rawname = XMLSymbols.PREFIX_XMLNS;
         }

         this.fAttributeQName.setValues(prefix, localpart, rawname, NamespaceContext.XMLNS_URI);
         this.fAttributes.addAttribute(this.fAttributeQName, XMLSymbols.fCDATASymbol, nsURI != null ? nsURI : XMLSymbols.EMPTY_STRING);
      }

   }

   public void reset(SchemaDOMParser schemaDOMParser, SymbolTable symbolTable, boolean namespacePrefixes, boolean stringsInternalized) {
      this.fSchemaDOMParser = schemaDOMParser;
      this.fSymbolTable = symbolTable;
      this.fNamespacePrefixes = namespacePrefixes;
      this.fStringsInternalized = stringsInternalized;
   }

   static void convertToSAXParseException(XMLParseException e) throws SAXException {
      Exception ex = e.getException();
      if (ex == null) {
         LocatorImpl locatorImpl = new LocatorImpl();
         locatorImpl.setPublicId(e.getPublicId());
         locatorImpl.setSystemId(e.getExpandedSystemId());
         locatorImpl.setLineNumber(e.getLineNumber());
         locatorImpl.setColumnNumber(e.getColumnNumber());
         throw new SAXParseException(e.getMessage(), locatorImpl);
      } else if (ex instanceof SAXException) {
         throw (SAXException)ex;
      } else {
         throw new SAXException(ex);
      }
   }

   static void convertToSAXException(XNIException e) throws SAXException {
      Exception ex = e.getException();
      if (ex == null) {
         throw new SAXException(e.getMessage());
      } else if (ex instanceof SAXException) {
         throw (SAXException)ex;
      } else {
         throw new SAXException(ex);
      }
   }
}
