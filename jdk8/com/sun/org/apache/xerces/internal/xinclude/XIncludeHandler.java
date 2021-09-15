package com.sun.org.apache.xerces.internal.xinclude;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.io.MalformedByteSequenceException;
import com.sun.org.apache.xerces.internal.util.AugmentationsImpl;
import com.sun.org.apache.xerces.internal.util.HTTPInputSource;
import com.sun.org.apache.xerces.internal.util.IntStack;
import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDFilter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import com.sun.org.apache.xerces.internal.xpointer.XPointerHandler;
import com.sun.org.apache.xerces.internal.xpointer.XPointerProcessor;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Objects;
import java.util.Stack;
import java.util.StringTokenizer;

public class XIncludeHandler implements XMLComponent, XMLDocumentFilter, XMLDTDFilter {
   public static final String XINCLUDE_DEFAULT_CONFIGURATION = "com.sun.org.apache.xerces.internal.parsers.XIncludeParserConfiguration";
   public static final String HTTP_ACCEPT = "Accept";
   public static final String HTTP_ACCEPT_LANGUAGE = "Accept-Language";
   public static final String XPOINTER = "xpointer";
   public static final String XINCLUDE_NS_URI = "http://www.w3.org/2001/XInclude".intern();
   public static final String XINCLUDE_INCLUDE = "include".intern();
   public static final String XINCLUDE_FALLBACK = "fallback".intern();
   public static final String XINCLUDE_PARSE_XML = "xml".intern();
   public static final String XINCLUDE_PARSE_TEXT = "text".intern();
   public static final String XINCLUDE_ATTR_HREF = "href".intern();
   public static final String XINCLUDE_ATTR_PARSE = "parse".intern();
   public static final String XINCLUDE_ATTR_ENCODING = "encoding".intern();
   public static final String XINCLUDE_ATTR_ACCEPT = "accept".intern();
   public static final String XINCLUDE_ATTR_ACCEPT_LANGUAGE = "accept-language".intern();
   public static final String XINCLUDE_INCLUDED = "[included]".intern();
   public static final String CURRENT_BASE_URI = "currentBaseURI";
   public static final String XINCLUDE_BASE = "base".intern();
   public static final QName XML_BASE_QNAME;
   public static final String XINCLUDE_LANG;
   public static final QName XML_LANG_QNAME;
   public static final QName NEW_NS_ATTR_QNAME;
   private static final int STATE_NORMAL_PROCESSING = 1;
   private static final int STATE_IGNORE = 2;
   private static final int STATE_EXPECT_FALLBACK = 3;
   protected static final String VALIDATION = "http://xml.org/sax/features/validation";
   protected static final String SCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
   protected static final String DYNAMIC_VALIDATION = "http://apache.org/xml/features/validation/dynamic";
   protected static final String ALLOW_UE_AND_NOTATION_EVENTS = "http://xml.org/sax/features/allow-dtd-events-after-endDTD";
   protected static final String XINCLUDE_FIXUP_BASE_URIS = "http://apache.org/xml/features/xinclude/fixup-base-uris";
   protected static final String XINCLUDE_FIXUP_LANGUAGE = "http://apache.org/xml/features/xinclude/fixup-language";
   protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
   protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
   protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
   protected static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
   public static final String BUFFER_SIZE = "http://apache.org/xml/properties/input-buffer-size";
   protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
   protected static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
   private static final String[] RECOGNIZED_FEATURES;
   private static final Boolean[] FEATURE_DEFAULTS;
   private static final String[] RECOGNIZED_PROPERTIES;
   private static final Object[] PROPERTY_DEFAULTS;
   protected XMLDocumentHandler fDocumentHandler;
   protected XMLDocumentSource fDocumentSource;
   protected XMLDTDHandler fDTDHandler;
   protected XMLDTDSource fDTDSource;
   protected XIncludeHandler fParentXIncludeHandler;
   protected int fBufferSize = 8192;
   protected String fParentRelativeURI;
   protected XMLParserConfiguration fChildConfig;
   protected XMLParserConfiguration fXIncludeChildConfig;
   protected XMLParserConfiguration fXPointerChildConfig;
   protected XPointerProcessor fXPtrProcessor = null;
   protected XMLLocator fDocLocation;
   protected XIncludeMessageFormatter fXIncludeMessageFormatter = new XIncludeMessageFormatter();
   protected XIncludeNamespaceSupport fNamespaceContext;
   protected SymbolTable fSymbolTable;
   protected XMLErrorReporter fErrorReporter;
   protected XMLEntityResolver fEntityResolver;
   protected XMLSecurityManager fSecurityManager;
   protected XMLSecurityPropertyManager fSecurityPropertyMgr;
   protected XIncludeTextReader fXInclude10TextReader;
   protected XIncludeTextReader fXInclude11TextReader;
   protected XMLResourceIdentifier fCurrentBaseURI;
   protected IntStack fBaseURIScope;
   protected Stack fBaseURI;
   protected Stack fLiteralSystemID;
   protected Stack fExpandedSystemID;
   protected IntStack fLanguageScope;
   protected Stack fLanguageStack;
   protected String fCurrentLanguage;
   protected ParserConfigurationSettings fSettings;
   private int fDepth = 0;
   private int fResultDepth;
   private static final int INITIAL_SIZE = 8;
   private boolean[] fSawInclude = new boolean[8];
   private boolean[] fSawFallback = new boolean[8];
   private int[] fState = new int[8];
   private ArrayList fNotations;
   private ArrayList fUnparsedEntities;
   private boolean fFixupBaseURIs = true;
   private boolean fFixupLanguage = true;
   private boolean fSendUEAndNotationEvents;
   private boolean fIsXML11;
   private boolean fInDTD;
   private boolean fSeenRootElement;
   private boolean fNeedCopyFeatures = true;
   private static final boolean[] gNeedEscaping;
   private static final char[] gAfterEscaping1;
   private static final char[] gAfterEscaping2;
   private static final char[] gHexChs;

   public XIncludeHandler() {
      this.fSawFallback[this.fDepth] = false;
      this.fSawInclude[this.fDepth] = false;
      this.fState[this.fDepth] = 1;
      this.fNotations = new ArrayList();
      this.fUnparsedEntities = new ArrayList();
      this.fBaseURIScope = new IntStack();
      this.fBaseURI = new Stack();
      this.fLiteralSystemID = new Stack();
      this.fExpandedSystemID = new Stack();
      this.fCurrentBaseURI = new XMLResourceIdentifierImpl();
      this.fLanguageScope = new IntStack();
      this.fLanguageStack = new Stack();
      this.fCurrentLanguage = null;
   }

   public void reset(XMLComponentManager componentManager) throws XNIException {
      this.fNamespaceContext = null;
      this.fDepth = 0;
      this.fResultDepth = this.isRootDocument() ? 0 : this.fParentXIncludeHandler.getResultDepth();
      this.fNotations.clear();
      this.fUnparsedEntities.clear();
      this.fParentRelativeURI = null;
      this.fIsXML11 = false;
      this.fInDTD = false;
      this.fSeenRootElement = false;
      this.fBaseURIScope.clear();
      this.fBaseURI.clear();
      this.fLiteralSystemID.clear();
      this.fExpandedSystemID.clear();
      this.fLanguageScope.clear();
      this.fLanguageStack.clear();

      int i;
      for(i = 0; i < this.fState.length; ++i) {
         this.fState[i] = 1;
      }

      for(i = 0; i < this.fSawFallback.length; ++i) {
         this.fSawFallback[i] = false;
      }

      for(i = 0; i < this.fSawInclude.length; ++i) {
         this.fSawInclude[i] = false;
      }

      try {
         if (!componentManager.getFeature("http://apache.org/xml/features/internal/parser-settings")) {
            return;
         }
      } catch (XMLConfigurationException var11) {
      }

      this.fNeedCopyFeatures = true;

      try {
         this.fSendUEAndNotationEvents = componentManager.getFeature("http://xml.org/sax/features/allow-dtd-events-after-endDTD");
         if (this.fChildConfig != null) {
            this.fChildConfig.setFeature("http://xml.org/sax/features/allow-dtd-events-after-endDTD", this.fSendUEAndNotationEvents);
         }
      } catch (XMLConfigurationException var10) {
      }

      try {
         this.fFixupBaseURIs = componentManager.getFeature("http://apache.org/xml/features/xinclude/fixup-base-uris");
         if (this.fChildConfig != null) {
            this.fChildConfig.setFeature("http://apache.org/xml/features/xinclude/fixup-base-uris", this.fFixupBaseURIs);
         }
      } catch (XMLConfigurationException var9) {
         this.fFixupBaseURIs = true;
      }

      try {
         this.fFixupLanguage = componentManager.getFeature("http://apache.org/xml/features/xinclude/fixup-language");
         if (this.fChildConfig != null) {
            this.fChildConfig.setFeature("http://apache.org/xml/features/xinclude/fixup-language", this.fFixupLanguage);
         }
      } catch (XMLConfigurationException var8) {
         this.fFixupLanguage = true;
      }

      try {
         SymbolTable value = (SymbolTable)componentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
         if (value != null) {
            this.fSymbolTable = value;
            if (this.fChildConfig != null) {
               this.fChildConfig.setProperty("http://apache.org/xml/properties/internal/symbol-table", value);
            }
         }
      } catch (XMLConfigurationException var7) {
         this.fSymbolTable = null;
      }

      try {
         XMLErrorReporter value = (XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
         if (value != null) {
            this.setErrorReporter(value);
            if (this.fChildConfig != null) {
               this.fChildConfig.setProperty("http://apache.org/xml/properties/internal/error-reporter", value);
            }
         }
      } catch (XMLConfigurationException var6) {
         this.fErrorReporter = null;
      }

      try {
         XMLEntityResolver value = (XMLEntityResolver)componentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
         if (value != null) {
            this.fEntityResolver = value;
            if (this.fChildConfig != null) {
               this.fChildConfig.setProperty("http://apache.org/xml/properties/internal/entity-resolver", value);
            }
         }
      } catch (XMLConfigurationException var5) {
         this.fEntityResolver = null;
      }

      try {
         XMLSecurityManager value = (XMLSecurityManager)componentManager.getProperty("http://apache.org/xml/properties/security-manager");
         if (value != null) {
            this.fSecurityManager = value;
            if (this.fChildConfig != null) {
               this.fChildConfig.setProperty("http://apache.org/xml/properties/security-manager", value);
            }
         }
      } catch (XMLConfigurationException var4) {
         this.fSecurityManager = null;
      }

      this.fSecurityPropertyMgr = (XMLSecurityPropertyManager)componentManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager");

      try {
         Integer value = (Integer)componentManager.getProperty("http://apache.org/xml/properties/input-buffer-size");
         if (value != null && value > 0) {
            this.fBufferSize = value;
            if (this.fChildConfig != null) {
               this.fChildConfig.setProperty("http://apache.org/xml/properties/input-buffer-size", value);
            }
         } else {
            this.fBufferSize = (Integer)this.getPropertyDefault("http://apache.org/xml/properties/input-buffer-size");
         }
      } catch (XMLConfigurationException var12) {
         this.fBufferSize = (Integer)this.getPropertyDefault("http://apache.org/xml/properties/input-buffer-size");
      }

      if (this.fXInclude10TextReader != null) {
         this.fXInclude10TextReader.setBufferSize(this.fBufferSize);
      }

      if (this.fXInclude11TextReader != null) {
         this.fXInclude11TextReader.setBufferSize(this.fBufferSize);
      }

      this.fSettings = new ParserConfigurationSettings();
      this.copyFeatures(componentManager, this.fSettings);

      try {
         if (componentManager.getFeature("http://apache.org/xml/features/validation/schema")) {
            this.fSettings.setFeature("http://apache.org/xml/features/validation/schema", false);
            if (componentManager.getFeature("http://xml.org/sax/features/validation")) {
               this.fSettings.setFeature("http://apache.org/xml/features/validation/dynamic", true);
            }
         }
      } catch (XMLConfigurationException var3) {
      }

   }

   public String[] getRecognizedFeatures() {
      return (String[])((String[])RECOGNIZED_FEATURES.clone());
   }

   public void setFeature(String featureId, boolean state) throws XMLConfigurationException {
      if (featureId.equals("http://xml.org/sax/features/allow-dtd-events-after-endDTD")) {
         this.fSendUEAndNotationEvents = state;
      }

      if (this.fSettings != null) {
         this.fNeedCopyFeatures = true;
         this.fSettings.setFeature(featureId, state);
      }

   }

   public String[] getRecognizedProperties() {
      return (String[])((String[])RECOGNIZED_PROPERTIES.clone());
   }

   public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
      if (propertyId.equals("http://apache.org/xml/properties/internal/symbol-table")) {
         this.fSymbolTable = (SymbolTable)value;
         if (this.fChildConfig != null) {
            this.fChildConfig.setProperty(propertyId, value);
         }

      } else if (propertyId.equals("http://apache.org/xml/properties/internal/error-reporter")) {
         this.setErrorReporter((XMLErrorReporter)value);
         if (this.fChildConfig != null) {
            this.fChildConfig.setProperty(propertyId, value);
         }

      } else if (propertyId.equals("http://apache.org/xml/properties/internal/entity-resolver")) {
         this.fEntityResolver = (XMLEntityResolver)value;
         if (this.fChildConfig != null) {
            this.fChildConfig.setProperty(propertyId, value);
         }

      } else if (propertyId.equals("http://apache.org/xml/properties/security-manager")) {
         this.fSecurityManager = (XMLSecurityManager)value;
         if (this.fChildConfig != null) {
            this.fChildConfig.setProperty(propertyId, value);
         }

      } else if (propertyId.equals("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager")) {
         this.fSecurityPropertyMgr = (XMLSecurityPropertyManager)value;
         if (this.fChildConfig != null) {
            this.fChildConfig.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", value);
         }

      } else if (propertyId.equals("http://apache.org/xml/properties/input-buffer-size")) {
         Integer bufferSize = (Integer)value;
         if (this.fChildConfig != null) {
            this.fChildConfig.setProperty(propertyId, value);
         }

         if (bufferSize != null && bufferSize > 0) {
            this.fBufferSize = bufferSize;
            if (this.fXInclude10TextReader != null) {
               this.fXInclude10TextReader.setBufferSize(this.fBufferSize);
            }

            if (this.fXInclude11TextReader != null) {
               this.fXInclude11TextReader.setBufferSize(this.fBufferSize);
            }
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

   public void setDocumentHandler(XMLDocumentHandler handler) {
      this.fDocumentHandler = handler;
   }

   public XMLDocumentHandler getDocumentHandler() {
      return this.fDocumentHandler;
   }

   public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext, Augmentations augs) throws XNIException {
      this.fErrorReporter.setDocumentLocator(locator);
      if (!this.isRootDocument() && this.fParentXIncludeHandler.searchForRecursiveIncludes(locator)) {
         this.reportFatalError("RecursiveInclude", new Object[]{locator.getExpandedSystemId()});
      }

      if (!(namespaceContext instanceof XIncludeNamespaceSupport)) {
         this.reportFatalError("IncompatibleNamespaceContext");
      }

      this.fNamespaceContext = (XIncludeNamespaceSupport)namespaceContext;
      this.fDocLocation = locator;
      this.fCurrentBaseURI.setBaseSystemId(locator.getBaseSystemId());
      this.fCurrentBaseURI.setExpandedSystemId(locator.getExpandedSystemId());
      this.fCurrentBaseURI.setLiteralSystemId(locator.getLiteralSystemId());
      this.saveBaseURI();
      if (augs == null) {
         augs = new AugmentationsImpl();
      }

      ((Augmentations)augs).putItem("currentBaseURI", this.fCurrentBaseURI);
      this.fCurrentLanguage = XMLSymbols.EMPTY_STRING;
      this.saveLanguage(this.fCurrentLanguage);
      if (this.isRootDocument() && this.fDocumentHandler != null) {
         this.fDocumentHandler.startDocument(locator, encoding, namespaceContext, (Augmentations)augs);
      }

   }

   public void xmlDecl(String version, String encoding, String standalone, Augmentations augs) throws XNIException {
      this.fIsXML11 = "1.1".equals(version);
      if (this.isRootDocument() && this.fDocumentHandler != null) {
         this.fDocumentHandler.xmlDecl(version, encoding, standalone, augs);
      }

   }

   public void doctypeDecl(String rootElement, String publicId, String systemId, Augmentations augs) throws XNIException {
      if (this.isRootDocument() && this.fDocumentHandler != null) {
         this.fDocumentHandler.doctypeDecl(rootElement, publicId, systemId, augs);
      }

   }

   public void comment(XMLString text, Augmentations augs) throws XNIException {
      if (!this.fInDTD) {
         if (this.fDocumentHandler != null && this.getState() == 1) {
            ++this.fDepth;
            augs = this.modifyAugmentations(augs);
            this.fDocumentHandler.comment(text, augs);
            --this.fDepth;
         }
      } else if (this.fDTDHandler != null) {
         this.fDTDHandler.comment(text, augs);
      }

   }

   public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
      if (!this.fInDTD) {
         if (this.fDocumentHandler != null && this.getState() == 1) {
            ++this.fDepth;
            augs = this.modifyAugmentations(augs);
            this.fDocumentHandler.processingInstruction(target, data, augs);
            --this.fDepth;
         }
      } else if (this.fDTDHandler != null) {
         this.fDTDHandler.processingInstruction(target, data, augs);
      }

   }

   public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
      ++this.fDepth;
      int lastState = this.getState(this.fDepth - 1);
      if (lastState == 3 && this.getState(this.fDepth - 2) == 3) {
         this.setState(2);
      } else {
         this.setState(lastState);
      }

      this.processXMLBaseAttributes(attributes);
      if (this.fFixupLanguage) {
         this.processXMLLangAttributes(attributes);
      }

      if (this.isIncludeElement(element)) {
         boolean success = this.handleIncludeElement(attributes);
         if (success) {
            this.setState(2);
         } else {
            this.setState(3);
         }
      } else if (this.isFallbackElement(element)) {
         this.handleFallbackElement();
      } else if (this.hasXIncludeNamespace(element)) {
         if (this.getSawInclude(this.fDepth - 1)) {
            this.reportFatalError("IncludeChild", new Object[]{element.rawname});
         }

         if (this.getSawFallback(this.fDepth - 1)) {
            this.reportFatalError("FallbackChild", new Object[]{element.rawname});
         }

         if (this.getState() == 1) {
            if (this.fResultDepth++ == 0) {
               this.checkMultipleRootElements();
            }

            if (this.fDocumentHandler != null) {
               augs = this.modifyAugmentations(augs);
               attributes = this.processAttributes(attributes);
               this.fDocumentHandler.startElement(element, attributes, augs);
            }
         }
      } else if (this.getState() == 1) {
         if (this.fResultDepth++ == 0) {
            this.checkMultipleRootElements();
         }

         if (this.fDocumentHandler != null) {
            augs = this.modifyAugmentations(augs);
            attributes = this.processAttributes(attributes);
            this.fDocumentHandler.startElement(element, attributes, augs);
         }
      }

   }

   public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
      ++this.fDepth;
      int lastState = this.getState(this.fDepth - 1);
      if (lastState == 3 && this.getState(this.fDepth - 2) == 3) {
         this.setState(2);
      } else {
         this.setState(lastState);
      }

      this.processXMLBaseAttributes(attributes);
      if (this.fFixupLanguage) {
         this.processXMLLangAttributes(attributes);
      }

      if (this.isIncludeElement(element)) {
         boolean success = this.handleIncludeElement(attributes);
         if (success) {
            this.setState(2);
         } else {
            this.reportFatalError("NoFallback", new Object[]{attributes.getValue((String)null, "href")});
         }
      } else if (this.isFallbackElement(element)) {
         this.handleFallbackElement();
      } else if (this.hasXIncludeNamespace(element)) {
         if (this.getSawInclude(this.fDepth - 1)) {
            this.reportFatalError("IncludeChild", new Object[]{element.rawname});
         }

         if (this.getSawFallback(this.fDepth - 1)) {
            this.reportFatalError("FallbackChild", new Object[]{element.rawname});
         }

         if (this.getState() == 1) {
            if (this.fResultDepth == 0) {
               this.checkMultipleRootElements();
            }

            if (this.fDocumentHandler != null) {
               augs = this.modifyAugmentations(augs);
               attributes = this.processAttributes(attributes);
               this.fDocumentHandler.emptyElement(element, attributes, augs);
            }
         }
      } else if (this.getState() == 1) {
         if (this.fResultDepth == 0) {
            this.checkMultipleRootElements();
         }

         if (this.fDocumentHandler != null) {
            augs = this.modifyAugmentations(augs);
            attributes = this.processAttributes(attributes);
            this.fDocumentHandler.emptyElement(element, attributes, augs);
         }
      }

      this.setSawFallback(this.fDepth + 1, false);
      this.setSawInclude(this.fDepth, false);
      if (this.fBaseURIScope.size() > 0 && this.fDepth == this.fBaseURIScope.peek()) {
         this.restoreBaseURI();
      }

      --this.fDepth;
   }

   public void endElement(QName element, Augmentations augs) throws XNIException {
      if (this.isIncludeElement(element) && this.getState() == 3 && !this.getSawFallback(this.fDepth + 1)) {
         this.reportFatalError("NoFallback", new Object[]{"unknown"});
      }

      if (this.isFallbackElement(element)) {
         if (this.getState() == 1) {
            this.setState(2);
         }
      } else if (this.getState() == 1) {
         --this.fResultDepth;
         if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endElement(element, augs);
         }
      }

      this.setSawFallback(this.fDepth + 1, false);
      this.setSawInclude(this.fDepth, false);
      if (this.fBaseURIScope.size() > 0 && this.fDepth == this.fBaseURIScope.peek()) {
         this.restoreBaseURI();
      }

      if (this.fLanguageScope.size() > 0 && this.fDepth == this.fLanguageScope.peek()) {
         this.fCurrentLanguage = this.restoreLanguage();
      }

      --this.fDepth;
   }

   public void startGeneralEntity(String name, XMLResourceIdentifier resId, String encoding, Augmentations augs) throws XNIException {
      if (this.getState() == 1) {
         if (this.fResultDepth == 0) {
            if (augs != null && Boolean.TRUE.equals(augs.getItem("ENTITY_SKIPPED"))) {
               this.reportFatalError("UnexpandedEntityReferenceIllegal");
            }
         } else if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startGeneralEntity(name, resId, encoding, augs);
         }
      }

   }

   public void textDecl(String version, String encoding, Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null && this.getState() == 1) {
         this.fDocumentHandler.textDecl(version, encoding, augs);
      }

   }

   public void endGeneralEntity(String name, Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null && this.getState() == 1 && this.fResultDepth != 0) {
         this.fDocumentHandler.endGeneralEntity(name, augs);
      }

   }

   public void characters(XMLString text, Augmentations augs) throws XNIException {
      if (this.getState() == 1) {
         if (this.fResultDepth == 0) {
            this.checkWhitespace(text);
         } else if (this.fDocumentHandler != null) {
            ++this.fDepth;
            augs = this.modifyAugmentations(augs);
            this.fDocumentHandler.characters(text, augs);
            --this.fDepth;
         }
      }

   }

   public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null && this.getState() == 1 && this.fResultDepth != 0) {
         this.fDocumentHandler.ignorableWhitespace(text, augs);
      }

   }

   public void startCDATA(Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null && this.getState() == 1 && this.fResultDepth != 0) {
         this.fDocumentHandler.startCDATA(augs);
      }

   }

   public void endCDATA(Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null && this.getState() == 1 && this.fResultDepth != 0) {
         this.fDocumentHandler.endCDATA(augs);
      }

   }

   public void endDocument(Augmentations augs) throws XNIException {
      if (this.isRootDocument()) {
         if (!this.fSeenRootElement) {
            this.reportFatalError("RootElementRequired");
         }

         if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endDocument(augs);
         }
      }

   }

   public void setDocumentSource(XMLDocumentSource source) {
      this.fDocumentSource = source;
   }

   public XMLDocumentSource getDocumentSource() {
      return this.fDocumentSource;
   }

   public void attributeDecl(String elementName, String attributeName, String type, String[] enumeration, String defaultType, XMLString defaultValue, XMLString nonNormalizedDefaultValue, Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.attributeDecl(elementName, attributeName, type, enumeration, defaultType, defaultValue, nonNormalizedDefaultValue, augmentations);
      }

   }

   public void elementDecl(String name, String contentModel, Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.elementDecl(name, contentModel, augmentations);
      }

   }

   public void endAttlist(Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.endAttlist(augmentations);
      }

   }

   public void endConditional(Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.endConditional(augmentations);
      }

   }

   public void endDTD(Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.endDTD(augmentations);
      }

      this.fInDTD = false;
   }

   public void endExternalSubset(Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.endExternalSubset(augmentations);
      }

   }

   public void endParameterEntity(String name, Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.endParameterEntity(name, augmentations);
      }

   }

   public void externalEntityDecl(String name, XMLResourceIdentifier identifier, Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.externalEntityDecl(name, identifier, augmentations);
      }

   }

   public XMLDTDSource getDTDSource() {
      return this.fDTDSource;
   }

   public void ignoredCharacters(XMLString text, Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.ignoredCharacters(text, augmentations);
      }

   }

   public void internalEntityDecl(String name, XMLString text, XMLString nonNormalizedText, Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.internalEntityDecl(name, text, nonNormalizedText, augmentations);
      }

   }

   public void notationDecl(String name, XMLResourceIdentifier identifier, Augmentations augmentations) throws XNIException {
      this.addNotation(name, identifier, augmentations);
      if (this.fDTDHandler != null) {
         this.fDTDHandler.notationDecl(name, identifier, augmentations);
      }

   }

   public void setDTDSource(XMLDTDSource source) {
      this.fDTDSource = source;
   }

   public void startAttlist(String elementName, Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.startAttlist(elementName, augmentations);
      }

   }

   public void startConditional(short type, Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.startConditional(type, augmentations);
      }

   }

   public void startDTD(XMLLocator locator, Augmentations augmentations) throws XNIException {
      this.fInDTD = true;
      if (this.fDTDHandler != null) {
         this.fDTDHandler.startDTD(locator, augmentations);
      }

   }

   public void startExternalSubset(XMLResourceIdentifier identifier, Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.startExternalSubset(identifier, augmentations);
      }

   }

   public void startParameterEntity(String name, XMLResourceIdentifier identifier, String encoding, Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.startParameterEntity(name, identifier, encoding, augmentations);
      }

   }

   public void unparsedEntityDecl(String name, XMLResourceIdentifier identifier, String notation, Augmentations augmentations) throws XNIException {
      this.addUnparsedEntity(name, identifier, notation, augmentations);
      if (this.fDTDHandler != null) {
         this.fDTDHandler.unparsedEntityDecl(name, identifier, notation, augmentations);
      }

   }

   public XMLDTDHandler getDTDHandler() {
      return this.fDTDHandler;
   }

   public void setDTDHandler(XMLDTDHandler handler) {
      this.fDTDHandler = handler;
   }

   private void setErrorReporter(XMLErrorReporter reporter) {
      this.fErrorReporter = reporter;
      if (this.fErrorReporter != null) {
         this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xinclude", this.fXIncludeMessageFormatter);
         if (this.fDocLocation != null) {
            this.fErrorReporter.setDocumentLocator(this.fDocLocation);
         }
      }

   }

   protected void handleFallbackElement() {
      if (!this.getSawInclude(this.fDepth - 1)) {
         if (this.getState() == 2) {
            return;
         }

         this.reportFatalError("FallbackParent");
      }

      this.setSawInclude(this.fDepth, false);
      this.fNamespaceContext.setContextInvalid();
      if (this.getSawFallback(this.fDepth)) {
         this.reportFatalError("MultipleFallbacks");
      } else {
         this.setSawFallback(this.fDepth, true);
      }

      if (this.getState() == 3) {
         this.setState(1);
      }

   }

   protected boolean handleIncludeElement(XMLAttributes attributes) throws XNIException {
      if (this.getSawInclude(this.fDepth - 1)) {
         this.reportFatalError("IncludeChild", new Object[]{XINCLUDE_INCLUDE});
      }

      if (this.getState() == 2) {
         return true;
      } else {
         this.setSawInclude(this.fDepth, true);
         this.fNamespaceContext.setContextInvalid();
         String href = attributes.getValue(XINCLUDE_ATTR_HREF);
         String parse = attributes.getValue(XINCLUDE_ATTR_PARSE);
         String xpointer = attributes.getValue("xpointer");
         String accept = attributes.getValue(XINCLUDE_ATTR_ACCEPT);
         String acceptLanguage = attributes.getValue(XINCLUDE_ATTR_ACCEPT_LANGUAGE);
         if (parse == null) {
            parse = XINCLUDE_PARSE_XML;
         }

         if (href == null) {
            href = XMLSymbols.EMPTY_STRING;
         }

         if (href.length() == 0 && XINCLUDE_PARSE_XML.equals(parse)) {
            if (xpointer != null) {
               Locale locale = this.fErrorReporter != null ? this.fErrorReporter.getLocale() : null;
               String reason = this.fXIncludeMessageFormatter.formatMessage(locale, "XPointerStreamability", (Object[])null);
               this.reportResourceError("XMLResourceError", new Object[]{href, reason});
               return false;
            }

            this.reportFatalError("XpointerMissing");
         }

         URI hrefURI = null;

         String parserName;
         try {
            hrefURI = new URI(href, true);
            if (hrefURI.getFragment() != null) {
               this.reportFatalError("HrefFragmentIdentifierIllegal", new Object[]{href});
            }
         } catch (URI.MalformedURIException var55) {
            parserName = this.escapeHref(href);
            if (href != parserName) {
               href = parserName;

               try {
                  hrefURI = new URI(href, true);
                  if (hrefURI.getFragment() != null) {
                     this.reportFatalError("HrefFragmentIdentifierIllegal", new Object[]{href});
                  }
               } catch (URI.MalformedURIException var46) {
                  this.reportFatalError("HrefSyntacticallyInvalid", new Object[]{parserName});
               }
            } else {
               this.reportFatalError("HrefSyntacticallyInvalid", new Object[]{href});
            }
         }

         if (accept != null && !this.isValidInHTTPHeader(accept)) {
            this.reportFatalError("AcceptMalformed", (Object[])null);
            accept = null;
         }

         if (acceptLanguage != null && !this.isValidInHTTPHeader(acceptLanguage)) {
            this.reportFatalError("AcceptLanguageMalformed", (Object[])null);
            acceptLanguage = null;
         }

         XMLInputSource includedSource = null;
         if (this.fEntityResolver != null) {
            try {
               XMLResourceIdentifier resourceIdentifier = new XMLResourceIdentifierImpl((String)null, href, this.fCurrentBaseURI.getExpandedSystemId(), XMLEntityManager.expandSystemId(href, this.fCurrentBaseURI.getExpandedSystemId(), false));
               includedSource = this.fEntityResolver.resolveEntity(resourceIdentifier);
               if (includedSource != null && !(includedSource instanceof HTTPInputSource) && (accept != null || acceptLanguage != null) && includedSource.getCharacterStream() == null && includedSource.getByteStream() == null) {
                  includedSource = this.createInputSource(includedSource.getPublicId(), includedSource.getSystemId(), includedSource.getBaseSystemId(), accept, acceptLanguage);
               }
            } catch (IOException var54) {
               this.reportResourceError("XMLResourceError", new Object[]{href, var54.getMessage()});
               return false;
            }
         }

         if (includedSource == null) {
            if (accept == null && acceptLanguage == null) {
               includedSource = new XMLInputSource((String)null, href, this.fCurrentBaseURI.getExpandedSystemId());
            } else {
               includedSource = this.createInputSource((String)null, href, this.fCurrentBaseURI.getExpandedSystemId(), accept, acceptLanguage);
            }
         }

         if (parse.equals(XINCLUDE_PARSE_XML)) {
            if (xpointer != null && this.fXPointerChildConfig == null || xpointer == null && this.fXIncludeChildConfig == null) {
               parserName = "com.sun.org.apache.xerces.internal.parsers.XIncludeParserConfiguration";
               if (xpointer != null) {
                  parserName = "com.sun.org.apache.xerces.internal.parsers.XPointerParserConfiguration";
               }

               this.fChildConfig = (XMLParserConfiguration)ObjectFactory.newInstance(parserName, true);
               if (this.fSymbolTable != null) {
                  this.fChildConfig.setProperty("http://apache.org/xml/properties/internal/symbol-table", this.fSymbolTable);
               }

               if (this.fErrorReporter != null) {
                  this.fChildConfig.setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
               }

               if (this.fEntityResolver != null) {
                  this.fChildConfig.setProperty("http://apache.org/xml/properties/internal/entity-resolver", this.fEntityResolver);
               }

               this.fChildConfig.setProperty("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
               this.fChildConfig.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.fSecurityPropertyMgr);
               this.fChildConfig.setProperty("http://apache.org/xml/properties/input-buffer-size", new Integer(this.fBufferSize));
               this.fNeedCopyFeatures = true;
               this.fChildConfig.setProperty("http://apache.org/xml/properties/internal/namespace-context", this.fNamespaceContext);
               this.fChildConfig.setFeature("http://apache.org/xml/features/xinclude/fixup-base-uris", this.fFixupBaseURIs);
               this.fChildConfig.setFeature("http://apache.org/xml/features/xinclude/fixup-language", this.fFixupLanguage);
               if (xpointer != null) {
                  XPointerHandler newHandler = (XPointerHandler)this.fChildConfig.getProperty("http://apache.org/xml/properties/internal/xpointer-handler");
                  this.fXPtrProcessor = newHandler;
                  ((XPointerHandler)this.fXPtrProcessor).setProperty("http://apache.org/xml/properties/internal/namespace-context", this.fNamespaceContext);
                  ((XPointerHandler)this.fXPtrProcessor).setProperty("http://apache.org/xml/features/xinclude/fixup-base-uris", this.fFixupBaseURIs);
                  ((XPointerHandler)this.fXPtrProcessor).setProperty("http://apache.org/xml/features/xinclude/fixup-language", this.fFixupLanguage);
                  if (this.fErrorReporter != null) {
                     ((XPointerHandler)this.fXPtrProcessor).setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
                  }

                  newHandler.setParent(this);
                  newHandler.setDocumentHandler(this.getDocumentHandler());
                  this.fXPointerChildConfig = this.fChildConfig;
               } else {
                  XIncludeHandler newHandler = (XIncludeHandler)this.fChildConfig.getProperty("http://apache.org/xml/properties/internal/xinclude-handler");
                  newHandler.setParent(this);
                  newHandler.setDocumentHandler(this.getDocumentHandler());
                  this.fXIncludeChildConfig = this.fChildConfig;
               }
            }

            if (xpointer != null) {
               this.fChildConfig = this.fXPointerChildConfig;

               try {
                  this.fXPtrProcessor.parseXPointer(xpointer);
               } catch (XNIException var45) {
                  this.reportResourceError("XMLResourceError", new Object[]{href, var45.getMessage()});
                  return false;
               }
            } else {
               this.fChildConfig = this.fXIncludeChildConfig;
            }

            if (this.fNeedCopyFeatures) {
               this.copyFeatures(this.fSettings, (XMLParserConfiguration)this.fChildConfig);
            }

            this.fNeedCopyFeatures = false;

            boolean var62;
            try {
               this.fNamespaceContext.pushScope();
               this.fChildConfig.parse(includedSource);
               if (this.fErrorReporter != null) {
                  this.fErrorReporter.setDocumentLocator(this.fDocLocation);
               }

               if (xpointer == null || this.fXPtrProcessor.isXPointerResolved()) {
                  return true;
               }

               Locale locale = this.fErrorReporter != null ? this.fErrorReporter.getLocale() : null;
               String reason = this.fXIncludeMessageFormatter.formatMessage(locale, "XPointerResolutionUnsuccessful", (Object[])null);
               this.reportResourceError("XMLResourceError", new Object[]{href, reason});
               boolean var11 = false;
               return var11;
            } catch (XNIException var47) {
               if (this.fErrorReporter != null) {
                  this.fErrorReporter.setDocumentLocator(this.fDocLocation);
               }

               this.reportFatalError("XMLParseError", new Object[]{href, var47.getMessage()});
               return true;
            } catch (IOException var48) {
               if (this.fErrorReporter != null) {
                  this.fErrorReporter.setDocumentLocator(this.fDocLocation);
               }

               this.reportResourceError("XMLResourceError", new Object[]{href, var48.getMessage()});
               var62 = false;
            } finally {
               this.fNamespaceContext.popScope();
            }

            return var62;
         } else if (parse.equals(XINCLUDE_PARSE_TEXT)) {
            parserName = attributes.getValue(XINCLUDE_ATTR_ENCODING);
            includedSource.setEncoding(parserName);
            XIncludeTextReader textReader = null;

            boolean var12;
            try {
               if (!this.fIsXML11) {
                  if (this.fXInclude10TextReader == null) {
                     this.fXInclude10TextReader = new XIncludeTextReader(includedSource, this, this.fBufferSize);
                  } else {
                     this.fXInclude10TextReader.setInputSource(includedSource);
                  }

                  textReader = this.fXInclude10TextReader;
               } else {
                  if (this.fXInclude11TextReader == null) {
                     this.fXInclude11TextReader = new XInclude11TextReader(includedSource, this, this.fBufferSize);
                  } else {
                     this.fXInclude11TextReader.setInputSource(includedSource);
                  }

                  textReader = this.fXInclude11TextReader;
               }

               textReader.setErrorReporter(this.fErrorReporter);
               textReader.parse();
               return true;
            } catch (MalformedByteSequenceException var50) {
               this.fErrorReporter.reportError(var50.getDomain(), var50.getKey(), var50.getArguments(), (short)2);
               return true;
            } catch (CharConversionException var51) {
               this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "CharConversionFailure", (Object[])null, (short)2);
               return true;
            } catch (IOException var52) {
               this.reportResourceError("TextResourceError", new Object[]{href, var52.getMessage()});
               var12 = false;
            } finally {
               if (textReader != null) {
                  try {
                     textReader.close();
                  } catch (IOException var44) {
                     this.reportResourceError("TextResourceError", new Object[]{href, var44.getMessage()});
                     return false;
                  }
               }

            }

            return var12;
         } else {
            this.reportFatalError("InvalidParseValue", new Object[]{parse});
            return true;
         }
      }
   }

   protected boolean hasXIncludeNamespace(QName element) {
      return element.uri == XINCLUDE_NS_URI || this.fNamespaceContext.getURI(element.prefix) == XINCLUDE_NS_URI;
   }

   protected boolean isIncludeElement(QName element) {
      return element.localpart.equals(XINCLUDE_INCLUDE) && this.hasXIncludeNamespace(element);
   }

   protected boolean isFallbackElement(QName element) {
      return element.localpart.equals(XINCLUDE_FALLBACK) && this.hasXIncludeNamespace(element);
   }

   protected boolean sameBaseURIAsIncludeParent() {
      String parentBaseURI = this.getIncludeParentBaseURI();
      String baseURI = this.fCurrentBaseURI.getExpandedSystemId();
      return parentBaseURI != null && parentBaseURI.equals(baseURI);
   }

   protected boolean sameLanguageAsIncludeParent() {
      String parentLanguage = this.getIncludeParentLanguage();
      return parentLanguage != null && parentLanguage.equalsIgnoreCase(this.fCurrentLanguage);
   }

   protected boolean searchForRecursiveIncludes(XMLLocator includedSource) {
      String includedSystemId = includedSource.getExpandedSystemId();
      if (includedSystemId == null) {
         try {
            includedSystemId = XMLEntityManager.expandSystemId(includedSource.getLiteralSystemId(), includedSource.getBaseSystemId(), false);
         } catch (URI.MalformedURIException var4) {
            this.reportFatalError("ExpandedSystemId");
         }
      }

      if (includedSystemId.equals(this.fCurrentBaseURI.getExpandedSystemId())) {
         return true;
      } else {
         return this.fParentXIncludeHandler == null ? false : this.fParentXIncludeHandler.searchForRecursiveIncludes(includedSource);
      }
   }

   protected boolean isTopLevelIncludedItem() {
      return this.isTopLevelIncludedItemViaInclude() || this.isTopLevelIncludedItemViaFallback();
   }

   protected boolean isTopLevelIncludedItemViaInclude() {
      return this.fDepth == 1 && !this.isRootDocument();
   }

   protected boolean isTopLevelIncludedItemViaFallback() {
      return this.getSawFallback(this.fDepth - 1);
   }

   protected XMLAttributes processAttributes(XMLAttributes attributes) {
      int index;
      String type;
      String uri;
      int index;
      if (this.isTopLevelIncludedItem()) {
         if (this.fFixupBaseURIs && !this.sameBaseURIAsIncludeParent()) {
            if (attributes == null) {
               attributes = new XMLAttributesImpl();
            }

            String uri = null;

            try {
               uri = this.getRelativeBaseURI();
            } catch (URI.MalformedURIException var8) {
               uri = this.fCurrentBaseURI.getExpandedSystemId();
            }

            index = ((XMLAttributes)attributes).addAttribute(XML_BASE_QNAME, XMLSymbols.fCDATASymbol, uri);
            ((XMLAttributes)attributes).setSpecified(index, true);
         }

         if (this.fFixupLanguage && !this.sameLanguageAsIncludeParent()) {
            if (attributes == null) {
               attributes = new XMLAttributesImpl();
            }

            index = ((XMLAttributes)attributes).addAttribute(XML_LANG_QNAME, XMLSymbols.fCDATASymbol, this.fCurrentLanguage);
            ((XMLAttributes)attributes).setSpecified(index, true);
         }

         Enumeration inscopeNS = this.fNamespaceContext.getAllPrefixes();

         while(inscopeNS.hasMoreElements()) {
            String prefix = (String)inscopeNS.nextElement();
            type = this.fNamespaceContext.getURIFromIncludeParent(prefix);
            uri = this.fNamespaceContext.getURI(prefix);
            if (type != uri && attributes != null) {
               QName ns;
               int index;
               if (prefix == XMLSymbols.EMPTY_STRING) {
                  if (((XMLAttributes)attributes).getValue(NamespaceContext.XMLNS_URI, XMLSymbols.PREFIX_XMLNS) == null) {
                     if (attributes == null) {
                        attributes = new XMLAttributesImpl();
                     }

                     ns = (QName)NEW_NS_ATTR_QNAME.clone();
                     ns.prefix = null;
                     ns.localpart = XMLSymbols.PREFIX_XMLNS;
                     ns.rawname = XMLSymbols.PREFIX_XMLNS;
                     index = ((XMLAttributes)attributes).addAttribute(ns, XMLSymbols.fCDATASymbol, uri != null ? uri : XMLSymbols.EMPTY_STRING);
                     ((XMLAttributes)attributes).setSpecified(index, true);
                     this.fNamespaceContext.declarePrefix(prefix, uri);
                  }
               } else if (((XMLAttributes)attributes).getValue(NamespaceContext.XMLNS_URI, prefix) == null) {
                  if (attributes == null) {
                     attributes = new XMLAttributesImpl();
                  }

                  ns = (QName)NEW_NS_ATTR_QNAME.clone();
                  ns.localpart = prefix;
                  ns.rawname = ns.rawname + prefix;
                  ns.rawname = this.fSymbolTable != null ? this.fSymbolTable.addSymbol(ns.rawname) : ns.rawname.intern();
                  index = ((XMLAttributes)attributes).addAttribute(ns, XMLSymbols.fCDATASymbol, uri != null ? uri : XMLSymbols.EMPTY_STRING);
                  ((XMLAttributes)attributes).setSpecified(index, true);
                  this.fNamespaceContext.declarePrefix(prefix, uri);
               }
            }
         }
      }

      if (attributes != null) {
         index = ((XMLAttributes)attributes).getLength();

         for(index = 0; index < index; ++index) {
            type = ((XMLAttributes)attributes).getType(index);
            uri = ((XMLAttributes)attributes).getValue(index);
            if (type == XMLSymbols.fENTITYSymbol) {
               this.checkUnparsedEntity(uri);
            }

            if (type == XMLSymbols.fENTITIESSymbol) {
               StringTokenizer st = new StringTokenizer(uri);

               while(st.hasMoreTokens()) {
                  String entName = st.nextToken();
                  this.checkUnparsedEntity(entName);
               }
            } else if (type == XMLSymbols.fNOTATIONSymbol) {
               this.checkNotation(uri);
            }
         }
      }

      return (XMLAttributes)attributes;
   }

   protected String getRelativeBaseURI() throws URI.MalformedURIException {
      int includeParentDepth = this.getIncludeParentDepth();
      String relativeURI = this.getRelativeURI(includeParentDepth);
      if (this.isRootDocument()) {
         return relativeURI;
      } else {
         if (relativeURI.equals("")) {
            relativeURI = this.fCurrentBaseURI.getLiteralSystemId();
         }

         if (includeParentDepth == 0) {
            if (this.fParentRelativeURI == null) {
               this.fParentRelativeURI = this.fParentXIncludeHandler.getRelativeBaseURI();
            }

            if (this.fParentRelativeURI.equals("")) {
               return relativeURI;
            } else {
               URI base = new URI(this.fParentRelativeURI, true);
               URI uri = new URI(base, relativeURI);
               String baseScheme = base.getScheme();
               String literalScheme = uri.getScheme();
               if (!Objects.equals(baseScheme, literalScheme)) {
                  return relativeURI;
               } else {
                  String baseAuthority = base.getAuthority();
                  String literalAuthority = uri.getAuthority();
                  if (!Objects.equals(baseAuthority, literalAuthority)) {
                     return uri.getSchemeSpecificPart();
                  } else {
                     String literalPath = uri.getPath();
                     String literalQuery = uri.getQueryString();
                     String literalFragment = uri.getFragment();
                     if (literalQuery == null && literalFragment == null) {
                        return literalPath;
                     } else {
                        StringBuilder buffer = new StringBuilder();
                        if (literalPath != null) {
                           buffer.append(literalPath);
                        }

                        if (literalQuery != null) {
                           buffer.append('?');
                           buffer.append(literalQuery);
                        }

                        if (literalFragment != null) {
                           buffer.append('#');
                           buffer.append(literalFragment);
                        }

                        return buffer.toString();
                     }
                  }
               }
            }
         } else {
            return relativeURI;
         }
      }
   }

   private String getIncludeParentBaseURI() {
      int depth = this.getIncludeParentDepth();
      return !this.isRootDocument() && depth == 0 ? this.fParentXIncludeHandler.getIncludeParentBaseURI() : this.getBaseURI(depth);
   }

   private String getIncludeParentLanguage() {
      int depth = this.getIncludeParentDepth();
      return !this.isRootDocument() && depth == 0 ? this.fParentXIncludeHandler.getIncludeParentLanguage() : this.getLanguage(depth);
   }

   private int getIncludeParentDepth() {
      for(int i = this.fDepth - 1; i >= 0; --i) {
         if (!this.getSawInclude(i) && !this.getSawFallback(i)) {
            return i;
         }
      }

      return 0;
   }

   private int getResultDepth() {
      return this.fResultDepth;
   }

   protected Augmentations modifyAugmentations(Augmentations augs) {
      return this.modifyAugmentations(augs, false);
   }

   protected Augmentations modifyAugmentations(Augmentations augs, boolean force) {
      if (force || this.isTopLevelIncludedItem()) {
         if (augs == null) {
            augs = new AugmentationsImpl();
         }

         ((Augmentations)augs).putItem(XINCLUDE_INCLUDED, Boolean.TRUE);
      }

      return (Augmentations)augs;
   }

   protected int getState(int depth) {
      return this.fState[depth];
   }

   protected int getState() {
      return this.fState[this.fDepth];
   }

   protected void setState(int state) {
      if (this.fDepth >= this.fState.length) {
         int[] newarray = new int[this.fDepth * 2];
         System.arraycopy(this.fState, 0, newarray, 0, this.fState.length);
         this.fState = newarray;
      }

      this.fState[this.fDepth] = state;
   }

   protected void setSawFallback(int depth, boolean val) {
      if (depth >= this.fSawFallback.length) {
         boolean[] newarray = new boolean[depth * 2];
         System.arraycopy(this.fSawFallback, 0, newarray, 0, this.fSawFallback.length);
         this.fSawFallback = newarray;
      }

      this.fSawFallback[depth] = val;
   }

   protected boolean getSawFallback(int depth) {
      return depth >= this.fSawFallback.length ? false : this.fSawFallback[depth];
   }

   protected void setSawInclude(int depth, boolean val) {
      if (depth >= this.fSawInclude.length) {
         boolean[] newarray = new boolean[depth * 2];
         System.arraycopy(this.fSawInclude, 0, newarray, 0, this.fSawInclude.length);
         this.fSawInclude = newarray;
      }

      this.fSawInclude[depth] = val;
   }

   protected boolean getSawInclude(int depth) {
      return depth >= this.fSawInclude.length ? false : this.fSawInclude[depth];
   }

   protected void reportResourceError(String key) {
      this.reportFatalError(key, (Object[])null);
   }

   protected void reportResourceError(String key, Object[] args) {
      this.reportError(key, args, (short)0);
   }

   protected void reportFatalError(String key) {
      this.reportFatalError(key, (Object[])null);
   }

   protected void reportFatalError(String key, Object[] args) {
      this.reportError(key, args, (short)2);
   }

   private void reportError(String key, Object[] args, short severity) {
      if (this.fErrorReporter != null) {
         this.fErrorReporter.reportError("http://www.w3.org/TR/xinclude", key, args, severity);
      }

   }

   protected void setParent(XIncludeHandler parent) {
      this.fParentXIncludeHandler = parent;
   }

   protected boolean isRootDocument() {
      return this.fParentXIncludeHandler == null;
   }

   protected void addUnparsedEntity(String name, XMLResourceIdentifier identifier, String notation, Augmentations augmentations) {
      XIncludeHandler.UnparsedEntity ent = new XIncludeHandler.UnparsedEntity();
      ent.name = name;
      ent.systemId = identifier.getLiteralSystemId();
      ent.publicId = identifier.getPublicId();
      ent.baseURI = identifier.getBaseSystemId();
      ent.expandedSystemId = identifier.getExpandedSystemId();
      ent.notation = notation;
      ent.augmentations = augmentations;
      this.fUnparsedEntities.add(ent);
   }

   protected void addNotation(String name, XMLResourceIdentifier identifier, Augmentations augmentations) {
      XIncludeHandler.Notation not = new XIncludeHandler.Notation();
      not.name = name;
      not.systemId = identifier.getLiteralSystemId();
      not.publicId = identifier.getPublicId();
      not.baseURI = identifier.getBaseSystemId();
      not.expandedSystemId = identifier.getExpandedSystemId();
      not.augmentations = augmentations;
      this.fNotations.add(not);
   }

   protected void checkUnparsedEntity(String entName) {
      XIncludeHandler.UnparsedEntity ent = new XIncludeHandler.UnparsedEntity();
      ent.name = entName;
      int index = this.fUnparsedEntities.indexOf(ent);
      if (index != -1) {
         ent = (XIncludeHandler.UnparsedEntity)this.fUnparsedEntities.get(index);
         this.checkNotation(ent.notation);
         this.checkAndSendUnparsedEntity(ent);
      }

   }

   protected void checkNotation(String notName) {
      XIncludeHandler.Notation not = new XIncludeHandler.Notation();
      not.name = notName;
      int index = this.fNotations.indexOf(not);
      if (index != -1) {
         not = (XIncludeHandler.Notation)this.fNotations.get(index);
         this.checkAndSendNotation(not);
      }

   }

   protected void checkAndSendUnparsedEntity(XIncludeHandler.UnparsedEntity ent) {
      if (this.isRootDocument()) {
         int index = this.fUnparsedEntities.indexOf(ent);
         if (index == -1) {
            XMLResourceIdentifier id = new XMLResourceIdentifierImpl(ent.publicId, ent.systemId, ent.baseURI, ent.expandedSystemId);
            this.addUnparsedEntity(ent.name, id, ent.notation, ent.augmentations);
            if (this.fSendUEAndNotationEvents && this.fDTDHandler != null) {
               this.fDTDHandler.unparsedEntityDecl(ent.name, id, ent.notation, ent.augmentations);
            }
         } else {
            XIncludeHandler.UnparsedEntity localEntity = (XIncludeHandler.UnparsedEntity)this.fUnparsedEntities.get(index);
            if (!ent.isDuplicate(localEntity)) {
               this.reportFatalError("NonDuplicateUnparsedEntity", new Object[]{ent.name});
            }
         }
      } else {
         this.fParentXIncludeHandler.checkAndSendUnparsedEntity(ent);
      }

   }

   protected void checkAndSendNotation(XIncludeHandler.Notation not) {
      if (this.isRootDocument()) {
         int index = this.fNotations.indexOf(not);
         if (index == -1) {
            XMLResourceIdentifier id = new XMLResourceIdentifierImpl(not.publicId, not.systemId, not.baseURI, not.expandedSystemId);
            this.addNotation(not.name, id, not.augmentations);
            if (this.fSendUEAndNotationEvents && this.fDTDHandler != null) {
               this.fDTDHandler.notationDecl(not.name, id, not.augmentations);
            }
         } else {
            XIncludeHandler.Notation localNotation = (XIncludeHandler.Notation)this.fNotations.get(index);
            if (!not.isDuplicate(localNotation)) {
               this.reportFatalError("NonDuplicateNotation", new Object[]{not.name});
            }
         }
      } else {
         this.fParentXIncludeHandler.checkAndSendNotation(not);
      }

   }

   private void checkWhitespace(XMLString value) {
      int end = value.offset + value.length;

      for(int i = value.offset; i < end; ++i) {
         if (!XMLChar.isSpace(value.ch[i])) {
            this.reportFatalError("ContentIllegalAtTopLevel");
            return;
         }
      }

   }

   private void checkMultipleRootElements() {
      if (this.getRootElementProcessed()) {
         this.reportFatalError("MultipleRootElements");
      }

      this.setRootElementProcessed(true);
   }

   private void setRootElementProcessed(boolean seenRoot) {
      if (this.isRootDocument()) {
         this.fSeenRootElement = seenRoot;
      } else {
         this.fParentXIncludeHandler.setRootElementProcessed(seenRoot);
      }
   }

   private boolean getRootElementProcessed() {
      return this.isRootDocument() ? this.fSeenRootElement : this.fParentXIncludeHandler.getRootElementProcessed();
   }

   protected void copyFeatures(XMLComponentManager from, ParserConfigurationSettings to) {
      Enumeration features = Constants.getXercesFeatures();
      this.copyFeatures1(features, "http://apache.org/xml/features/", from, to);
      features = Constants.getSAXFeatures();
      this.copyFeatures1(features, "http://xml.org/sax/features/", from, to);
   }

   protected void copyFeatures(XMLComponentManager from, XMLParserConfiguration to) {
      Enumeration features = Constants.getXercesFeatures();
      this.copyFeatures1(features, "http://apache.org/xml/features/", from, to);
      features = Constants.getSAXFeatures();
      this.copyFeatures1(features, "http://xml.org/sax/features/", from, to);
   }

   private void copyFeatures1(Enumeration features, String featurePrefix, XMLComponentManager from, ParserConfigurationSettings to) {
      while(features.hasMoreElements()) {
         String featureId = featurePrefix + (String)features.nextElement();
         to.addRecognizedFeatures(new String[]{featureId});

         try {
            to.setFeature(featureId, from.getFeature(featureId));
         } catch (XMLConfigurationException var7) {
         }
      }

   }

   private void copyFeatures1(Enumeration features, String featurePrefix, XMLComponentManager from, XMLParserConfiguration to) {
      while(features.hasMoreElements()) {
         String featureId = featurePrefix + (String)features.nextElement();
         boolean value = from.getFeature(featureId);

         try {
            to.setFeature(featureId, value);
         } catch (XMLConfigurationException var8) {
         }
      }

   }

   protected void saveBaseURI() {
      this.fBaseURIScope.push(this.fDepth);
      this.fBaseURI.push(this.fCurrentBaseURI.getBaseSystemId());
      this.fLiteralSystemID.push(this.fCurrentBaseURI.getLiteralSystemId());
      this.fExpandedSystemID.push(this.fCurrentBaseURI.getExpandedSystemId());
   }

   protected void restoreBaseURI() {
      this.fBaseURI.pop();
      this.fLiteralSystemID.pop();
      this.fExpandedSystemID.pop();
      this.fBaseURIScope.pop();
      this.fCurrentBaseURI.setBaseSystemId((String)this.fBaseURI.peek());
      this.fCurrentBaseURI.setLiteralSystemId((String)this.fLiteralSystemID.peek());
      this.fCurrentBaseURI.setExpandedSystemId((String)this.fExpandedSystemID.peek());
   }

   protected void saveLanguage(String language) {
      this.fLanguageScope.push(this.fDepth);
      this.fLanguageStack.push(language);
   }

   public String restoreLanguage() {
      this.fLanguageStack.pop();
      this.fLanguageScope.pop();
      return (String)this.fLanguageStack.peek();
   }

   public String getBaseURI(int depth) {
      int scope = this.scopeOfBaseURI(depth);
      return (String)this.fExpandedSystemID.elementAt(scope);
   }

   public String getLanguage(int depth) {
      int scope = this.scopeOfLanguage(depth);
      return (String)this.fLanguageStack.elementAt(scope);
   }

   public String getRelativeURI(int depth) throws URI.MalformedURIException {
      int start = this.scopeOfBaseURI(depth) + 1;
      if (start == this.fBaseURIScope.size()) {
         return "";
      } else {
         URI uri = new URI("file", (String)this.fLiteralSystemID.elementAt(start));

         for(int i = start + 1; i < this.fBaseURIScope.size(); ++i) {
            uri = new URI(uri, (String)this.fLiteralSystemID.elementAt(i));
         }

         return uri.getPath();
      }
   }

   private int scopeOfBaseURI(int depth) {
      for(int i = this.fBaseURIScope.size() - 1; i >= 0; --i) {
         if (this.fBaseURIScope.elementAt(i) <= depth) {
            return i;
         }
      }

      return -1;
   }

   private int scopeOfLanguage(int depth) {
      for(int i = this.fLanguageScope.size() - 1; i >= 0; --i) {
         if (this.fLanguageScope.elementAt(i) <= depth) {
            return i;
         }
      }

      return -1;
   }

   protected void processXMLBaseAttributes(XMLAttributes attributes) {
      String baseURIValue = attributes.getValue(NamespaceContext.XML_URI, "base");
      if (baseURIValue != null) {
         try {
            String expandedValue = XMLEntityManager.expandSystemId(baseURIValue, this.fCurrentBaseURI.getExpandedSystemId(), false);
            this.fCurrentBaseURI.setLiteralSystemId(baseURIValue);
            this.fCurrentBaseURI.setBaseSystemId(this.fCurrentBaseURI.getExpandedSystemId());
            this.fCurrentBaseURI.setExpandedSystemId(expandedValue);
            this.saveBaseURI();
         } catch (URI.MalformedURIException var4) {
         }
      }

   }

   protected void processXMLLangAttributes(XMLAttributes attributes) {
      String language = attributes.getValue(NamespaceContext.XML_URI, "lang");
      if (language != null) {
         this.fCurrentLanguage = language;
         this.saveLanguage(this.fCurrentLanguage);
      }

   }

   private boolean isValidInHTTPHeader(String value) {
      for(int i = value.length() - 1; i >= 0; --i) {
         char ch = value.charAt(i);
         if (ch < ' ' || ch > '~') {
            return false;
         }
      }

      return true;
   }

   private XMLInputSource createInputSource(String publicId, String systemId, String baseSystemId, String accept, String acceptLanguage) {
      HTTPInputSource httpSource = new HTTPInputSource(publicId, systemId, baseSystemId);
      if (accept != null && accept.length() > 0) {
         httpSource.setHTTPRequestProperty("Accept", accept);
      }

      if (acceptLanguage != null && acceptLanguage.length() > 0) {
         httpSource.setHTTPRequestProperty("Accept-Language", acceptLanguage);
      }

      return httpSource;
   }

   private String escapeHref(String href) {
      int len = href.length();
      StringBuilder buffer = new StringBuilder(len * 3);

      char ch;
      int i;
      for(i = 0; i < len; ++i) {
         ch = href.charAt(i);
         if (ch > '~') {
            break;
         }

         if (ch < ' ') {
            return href;
         }

         if (gNeedEscaping[ch]) {
            buffer.append('%');
            buffer.append(gAfterEscaping1[ch]);
            buffer.append(gAfterEscaping2[ch]);
         } else {
            buffer.append((char)ch);
         }
      }

      if (i < len) {
         int j = i;

         while(true) {
            if (j >= len) {
               Object var11 = null;

               byte[] bytes;
               try {
                  bytes = href.substring(i).getBytes("UTF-8");
               } catch (UnsupportedEncodingException var9) {
                  return href;
               }

               len = bytes.length;

               for(i = 0; i < len; ++i) {
                  byte b = bytes[i];
                  if (b < 0) {
                     int ch = b + 256;
                     buffer.append('%');
                     buffer.append(gHexChs[ch >> 4]);
                     buffer.append(gHexChs[ch & 15]);
                  } else if (gNeedEscaping[b]) {
                     buffer.append('%');
                     buffer.append(gAfterEscaping1[b]);
                     buffer.append(gAfterEscaping2[b]);
                  } else {
                     buffer.append((char)b);
                  }
               }

               return buffer.length() != len ? buffer.toString() : href;
            }

            ch = href.charAt(j);
            if ((ch < ' ' || ch > '~') && (ch < 160 || ch > '\ud7ff') && (ch < '豈' || ch > '\ufdcf') && (ch < 'ﷰ' || ch > '\uffef')) {
               if (!XMLChar.isHighSurrogate(ch)) {
                  break;
               }

               ++j;
               if (j >= len) {
                  break;
               }

               int ch2 = href.charAt(j);
               if (!XMLChar.isLowSurrogate(ch2)) {
                  break;
               }

               int ch2 = XMLChar.supplemental((char)ch, (char)ch2);
               if (ch2 >= 983040 || (ch2 & '\uffff') > 65533) {
                  break;
               }
            }

            ++j;
         }

         return href;
      } else {
         return buffer.length() != len ? buffer.toString() : href;
      }
   }

   static {
      XML_BASE_QNAME = new QName(XMLSymbols.PREFIX_XML, XINCLUDE_BASE, (XMLSymbols.PREFIX_XML + ":" + XINCLUDE_BASE).intern(), NamespaceContext.XML_URI);
      XINCLUDE_LANG = "lang".intern();
      XML_LANG_QNAME = new QName(XMLSymbols.PREFIX_XML, XINCLUDE_LANG, (XMLSymbols.PREFIX_XML + ":" + XINCLUDE_LANG).intern(), NamespaceContext.XML_URI);
      NEW_NS_ATTR_QNAME = new QName(XMLSymbols.PREFIX_XMLNS, "", XMLSymbols.PREFIX_XMLNS + ":", NamespaceContext.XMLNS_URI);
      RECOGNIZED_FEATURES = new String[]{"http://xml.org/sax/features/allow-dtd-events-after-endDTD", "http://apache.org/xml/features/xinclude/fixup-base-uris", "http://apache.org/xml/features/xinclude/fixup-language"};
      FEATURE_DEFAULTS = new Boolean[]{Boolean.TRUE, Boolean.TRUE, Boolean.TRUE};
      RECOGNIZED_PROPERTIES = new String[]{"http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/security-manager", "http://apache.org/xml/properties/input-buffer-size"};
      PROPERTY_DEFAULTS = new Object[]{null, null, null, new Integer(8192)};
      gNeedEscaping = new boolean[128];
      gAfterEscaping1 = new char[128];
      gAfterEscaping2 = new char[128];
      gHexChs = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
      char[] escChs = new char[]{' ', '<', '>', '"', '{', '}', '|', '\\', '^', '`'};
      int len = escChs.length;

      for(int i = 0; i < len; ++i) {
         char ch = escChs[i];
         gNeedEscaping[ch] = true;
         gAfterEscaping1[ch] = gHexChs[ch >> 4];
         gAfterEscaping2[ch] = gHexChs[ch & 15];
      }

   }

   protected static class UnparsedEntity {
      public String name;
      public String systemId;
      public String baseURI;
      public String publicId;
      public String expandedSystemId;
      public String notation;
      public Augmentations augmentations;

      public boolean equals(Object obj) {
         return obj == this || obj instanceof XIncludeHandler.UnparsedEntity && Objects.equals(this.name, ((XIncludeHandler.UnparsedEntity)obj).name);
      }

      public int hashCode() {
         return Objects.hashCode(this.name);
      }

      public boolean isDuplicate(Object obj) {
         if (obj != null && obj instanceof XIncludeHandler.UnparsedEntity) {
            XIncludeHandler.UnparsedEntity other = (XIncludeHandler.UnparsedEntity)obj;
            return Objects.equals(this.name, other.name) && Objects.equals(this.publicId, other.publicId) && Objects.equals(this.expandedSystemId, other.expandedSystemId) && Objects.equals(this.notation, other.notation);
         } else {
            return false;
         }
      }
   }

   protected static class Notation {
      public String name;
      public String systemId;
      public String baseURI;
      public String publicId;
      public String expandedSystemId;
      public Augmentations augmentations;

      public boolean equals(Object obj) {
         return obj == this || obj instanceof XIncludeHandler.Notation && Objects.equals(this.name, ((XIncludeHandler.Notation)obj).name);
      }

      public int hashCode() {
         return Objects.hashCode(this.name);
      }

      public boolean isDuplicate(Object obj) {
         if (obj != null && obj instanceof XIncludeHandler.Notation) {
            XIncludeHandler.Notation other = (XIncludeHandler.Notation)obj;
            return Objects.equals(this.name, other.name) && Objects.equals(this.publicId, other.publicId) && Objects.equals(this.expandedSystemId, other.expandedSystemId);
         } else {
            return false;
         }
      }
   }
}
