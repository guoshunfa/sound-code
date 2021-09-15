package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;

public class XMLNamespaceBinder implements XMLComponent, XMLDocumentFilter {
   protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
   protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
   protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
   private static final String[] RECOGNIZED_FEATURES = new String[]{"http://xml.org/sax/features/namespaces"};
   private static final Boolean[] FEATURE_DEFAULTS = new Boolean[]{null};
   private static final String[] RECOGNIZED_PROPERTIES = new String[]{"http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter"};
   private static final Object[] PROPERTY_DEFAULTS = new Object[]{null, null};
   protected boolean fNamespaces;
   protected SymbolTable fSymbolTable;
   protected XMLErrorReporter fErrorReporter;
   protected XMLDocumentHandler fDocumentHandler;
   protected XMLDocumentSource fDocumentSource;
   protected boolean fOnlyPassPrefixMappingEvents;
   private NamespaceContext fNamespaceContext;
   private QName fAttributeQName = new QName();

   public void setOnlyPassPrefixMappingEvents(boolean onlyPassPrefixMappingEvents) {
      this.fOnlyPassPrefixMappingEvents = onlyPassPrefixMappingEvents;
   }

   public boolean getOnlyPassPrefixMappingEvents() {
      return this.fOnlyPassPrefixMappingEvents;
   }

   public void reset(XMLComponentManager componentManager) throws XNIException {
      this.fNamespaces = componentManager.getFeature("http://xml.org/sax/features/namespaces", true);
      this.fSymbolTable = (SymbolTable)componentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
      this.fErrorReporter = (XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
   }

   public String[] getRecognizedFeatures() {
      return (String[])((String[])RECOGNIZED_FEATURES.clone());
   }

   public void setFeature(String featureId, boolean state) throws XMLConfigurationException {
   }

   public String[] getRecognizedProperties() {
      return (String[])((String[])RECOGNIZED_PROPERTIES.clone());
   }

   public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
      if (propertyId.startsWith("http://apache.org/xml/properties/")) {
         int suffixLength = propertyId.length() - "http://apache.org/xml/properties/".length();
         if (suffixLength == "internal/symbol-table".length() && propertyId.endsWith("internal/symbol-table")) {
            this.fSymbolTable = (SymbolTable)value;
         } else if (suffixLength == "internal/error-reporter".length() && propertyId.endsWith("internal/error-reporter")) {
            this.fErrorReporter = (XMLErrorReporter)value;
         }

      }
   }

   public Boolean getFeatureDefault(String featureId) {
      for(int i = 0; i < RECOGNIZED_FEATURES.length; ++i) {
         if (RECOGNIZED_FEATURES[i].equals(featureId)) {
            return FEATURE_DEFAULTS[i];
         }
      }

      return null;
   }

   public Object getPropertyDefault(String propertyId) {
      for(int i = 0; i < RECOGNIZED_PROPERTIES.length; ++i) {
         if (RECOGNIZED_PROPERTIES[i].equals(propertyId)) {
            return PROPERTY_DEFAULTS[i];
         }
      }

      return null;
   }

   public void setDocumentHandler(XMLDocumentHandler documentHandler) {
      this.fDocumentHandler = documentHandler;
   }

   public XMLDocumentHandler getDocumentHandler() {
      return this.fDocumentHandler;
   }

   public void setDocumentSource(XMLDocumentSource source) {
      this.fDocumentSource = source;
   }

   public XMLDocumentSource getDocumentSource() {
      return this.fDocumentSource;
   }

   public void startGeneralEntity(String name, XMLResourceIdentifier identifier, String encoding, Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
         this.fDocumentHandler.startGeneralEntity(name, identifier, encoding, augs);
      }

   }

   public void textDecl(String version, String encoding, Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
         this.fDocumentHandler.textDecl(version, encoding, augs);
      }

   }

   public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext, Augmentations augs) throws XNIException {
      this.fNamespaceContext = namespaceContext;
      if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
         this.fDocumentHandler.startDocument(locator, encoding, namespaceContext, augs);
      }

   }

   public void xmlDecl(String version, String encoding, String standalone, Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
         this.fDocumentHandler.xmlDecl(version, encoding, standalone, augs);
      }

   }

   public void doctypeDecl(String rootElement, String publicId, String systemId, Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
         this.fDocumentHandler.doctypeDecl(rootElement, publicId, systemId, augs);
      }

   }

   public void comment(XMLString text, Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
         this.fDocumentHandler.comment(text, augs);
      }

   }

   public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
         this.fDocumentHandler.processingInstruction(target, data, augs);
      }

   }

   public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
      if (this.fNamespaces) {
         this.handleStartElement(element, attributes, augs, false);
      } else if (this.fDocumentHandler != null) {
         this.fDocumentHandler.startElement(element, attributes, augs);
      }

   }

   public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
      if (this.fNamespaces) {
         this.handleStartElement(element, attributes, augs, true);
         this.handleEndElement(element, augs, true);
      } else if (this.fDocumentHandler != null) {
         this.fDocumentHandler.emptyElement(element, attributes, augs);
      }

   }

   public void characters(XMLString text, Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
         this.fDocumentHandler.characters(text, augs);
      }

   }

   public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
         this.fDocumentHandler.ignorableWhitespace(text, augs);
      }

   }

   public void endElement(QName element, Augmentations augs) throws XNIException {
      if (this.fNamespaces) {
         this.handleEndElement(element, augs, false);
      } else if (this.fDocumentHandler != null) {
         this.fDocumentHandler.endElement(element, augs);
      }

   }

   public void startCDATA(Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
         this.fDocumentHandler.startCDATA(augs);
      }

   }

   public void endCDATA(Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
         this.fDocumentHandler.endCDATA(augs);
      }

   }

   public void endDocument(Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
         this.fDocumentHandler.endDocument(augs);
      }

   }

   public void endGeneralEntity(String name, Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
         this.fDocumentHandler.endGeneralEntity(name, augs);
      }

   }

   protected void handleStartElement(QName element, XMLAttributes attributes, Augmentations augs, boolean isEmpty) throws XNIException {
      this.fNamespaceContext.pushContext();
      if (element.prefix == XMLSymbols.PREFIX_XMLNS) {
         this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementXMLNSPrefix", new Object[]{element.rawname}, (short)2);
      }

      int length = attributes.getLength();

      String aprefix;
      String auri;
      for(int i = 0; i < length; ++i) {
         String localpart = attributes.getLocalName(i);
         aprefix = attributes.getPrefix(i);
         if (aprefix == XMLSymbols.PREFIX_XMLNS || aprefix == XMLSymbols.EMPTY_STRING && localpart == XMLSymbols.PREFIX_XMLNS) {
            auri = this.fSymbolTable.addSymbol(attributes.getValue(i));
            if (aprefix == XMLSymbols.PREFIX_XMLNS && localpart == XMLSymbols.PREFIX_XMLNS) {
               this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[]{attributes.getQName(i)}, (short)2);
            }

            if (auri == NamespaceContext.XMLNS_URI) {
               this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[]{attributes.getQName(i)}, (short)2);
            }

            if (localpart == XMLSymbols.PREFIX_XML) {
               if (auri != NamespaceContext.XML_URI) {
                  this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[]{attributes.getQName(i)}, (short)2);
               }
            } else if (auri == NamespaceContext.XML_URI) {
               this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[]{attributes.getQName(i)}, (short)2);
            }

            aprefix = localpart != XMLSymbols.PREFIX_XMLNS ? localpart : XMLSymbols.EMPTY_STRING;
            if (this.prefixBoundToNullURI(auri, localpart)) {
               this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "EmptyPrefixedAttName", new Object[]{attributes.getQName(i)}, (short)2);
            } else {
               this.fNamespaceContext.declarePrefix(aprefix, auri.length() != 0 ? auri : null);
            }
         }
      }

      String prefix = element.prefix != null ? element.prefix : XMLSymbols.EMPTY_STRING;
      element.uri = this.fNamespaceContext.getURI(prefix);
      if (element.prefix == null && element.uri != null) {
         element.prefix = XMLSymbols.EMPTY_STRING;
      }

      if (element.prefix != null && element.uri == null) {
         this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementPrefixUnbound", new Object[]{element.prefix, element.rawname}, (short)2);
      }

      int attrCount;
      for(attrCount = 0; attrCount < length; ++attrCount) {
         attributes.getName(attrCount, this.fAttributeQName);
         aprefix = this.fAttributeQName.prefix != null ? this.fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
         auri = this.fAttributeQName.rawname;
         if (auri == XMLSymbols.PREFIX_XMLNS) {
            this.fAttributeQName.uri = this.fNamespaceContext.getURI(XMLSymbols.PREFIX_XMLNS);
            attributes.setName(attrCount, this.fAttributeQName);
         } else if (aprefix != XMLSymbols.EMPTY_STRING) {
            this.fAttributeQName.uri = this.fNamespaceContext.getURI(aprefix);
            if (this.fAttributeQName.uri == null) {
               this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributePrefixUnbound", new Object[]{element.rawname, auri, aprefix}, (short)2);
            }

            attributes.setName(attrCount, this.fAttributeQName);
         }
      }

      attrCount = attributes.getLength();

      for(int i = 0; i < attrCount - 1; ++i) {
         auri = attributes.getURI(i);
         if (auri != null && auri != NamespaceContext.XMLNS_URI) {
            String alocalpart = attributes.getLocalName(i);

            for(int j = i + 1; j < attrCount; ++j) {
               String blocalpart = attributes.getLocalName(j);
               String buri = attributes.getURI(j);
               if (alocalpart == blocalpart && auri == buri) {
                  this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNSNotUnique", new Object[]{element.rawname, alocalpart, auri}, (short)2);
               }
            }
         }
      }

      if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
         if (isEmpty) {
            this.fDocumentHandler.emptyElement(element, attributes, augs);
         } else {
            this.fDocumentHandler.startElement(element, attributes, augs);
         }
      }

   }

   protected void handleEndElement(QName element, Augmentations augs, boolean isEmpty) throws XNIException {
      String eprefix = element.prefix != null ? element.prefix : XMLSymbols.EMPTY_STRING;
      element.uri = this.fNamespaceContext.getURI(eprefix);
      if (element.uri != null) {
         element.prefix = eprefix;
      }

      if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents && !isEmpty) {
         this.fDocumentHandler.endElement(element, augs);
      }

      this.fNamespaceContext.popContext();
   }

   protected boolean prefixBoundToNullURI(String uri, String localpart) {
      return uri == XMLSymbols.EMPTY_STRING && localpart != XMLSymbols.PREFIX_XMLNS;
   }
}
