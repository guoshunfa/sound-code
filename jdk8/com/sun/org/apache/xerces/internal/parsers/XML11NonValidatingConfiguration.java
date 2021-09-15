package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.impl.XML11DTDScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XML11DocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XML11NSDocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLDTDScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLDocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLEntityHandler;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.XMLNSDocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLVersionDetector;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidatorFilter;
import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
import com.sun.org.apache.xerces.internal.util.PropertyState;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDScanner;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentScanner;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLPullParserConfiguration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class XML11NonValidatingConfiguration extends ParserConfigurationSettings implements XMLPullParserConfiguration, XML11Configurable {
   protected static final String XML11_DATATYPE_VALIDATOR_FACTORY = "com.sun.org.apache.xerces.internal.impl.dv.dtd.XML11DTDDVFactoryImpl";
   protected static final String VALIDATION = "http://xml.org/sax/features/validation";
   protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
   protected static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
   protected static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
   protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
   protected static final String XML_STRING = "http://xml.org/sax/properties/xml-string";
   protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
   protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
   protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
   protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
   protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
   protected static final String DOCUMENT_SCANNER = "http://apache.org/xml/properties/internal/document-scanner";
   protected static final String DTD_SCANNER = "http://apache.org/xml/properties/internal/dtd-scanner";
   protected static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
   protected static final String DTD_VALIDATOR = "http://apache.org/xml/properties/internal/validator/dtd";
   protected static final String NAMESPACE_BINDER = "http://apache.org/xml/properties/internal/namespace-binder";
   protected static final String DATATYPE_VALIDATOR_FACTORY = "http://apache.org/xml/properties/internal/datatype-validator-factory";
   protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
   protected static final boolean PRINT_EXCEPTION_STACK_TRACE = false;
   protected SymbolTable fSymbolTable;
   protected XMLInputSource fInputSource;
   protected ValidationManager fValidationManager;
   protected XMLVersionDetector fVersionDetector;
   protected XMLLocator fLocator;
   protected Locale fLocale;
   protected ArrayList fComponents;
   protected ArrayList fXML11Components;
   protected ArrayList fCommonComponents;
   protected XMLDocumentHandler fDocumentHandler;
   protected XMLDTDHandler fDTDHandler;
   protected XMLDTDContentModelHandler fDTDContentModelHandler;
   protected XMLDocumentSource fLastComponent;
   protected boolean fParseInProgress;
   protected boolean fConfigUpdated;
   protected DTDDVFactory fDatatypeValidatorFactory;
   protected XMLNSDocumentScannerImpl fNamespaceScanner;
   protected XMLDocumentScannerImpl fNonNSScanner;
   protected XMLDTDScanner fDTDScanner;
   protected DTDDVFactory fXML11DatatypeFactory;
   protected XML11NSDocumentScannerImpl fXML11NSDocScanner;
   protected XML11DocumentScannerImpl fXML11DocScanner;
   protected XML11DTDScannerImpl fXML11DTDScanner;
   protected XMLGrammarPool fGrammarPool;
   protected XMLErrorReporter fErrorReporter;
   protected XMLEntityManager fEntityManager;
   protected XMLDocumentScanner fCurrentScanner;
   protected DTDDVFactory fCurrentDVFactory;
   protected XMLDTDScanner fCurrentDTDScanner;
   private boolean f11Initialized;

   public XML11NonValidatingConfiguration() {
      this((SymbolTable)null, (XMLGrammarPool)null, (XMLComponentManager)null);
   }

   public XML11NonValidatingConfiguration(SymbolTable symbolTable) {
      this(symbolTable, (XMLGrammarPool)null, (XMLComponentManager)null);
   }

   public XML11NonValidatingConfiguration(SymbolTable symbolTable, XMLGrammarPool grammarPool) {
      this(symbolTable, grammarPool, (XMLComponentManager)null);
   }

   public XML11NonValidatingConfiguration(SymbolTable symbolTable, XMLGrammarPool grammarPool, XMLComponentManager parentSettings) {
      super(parentSettings);
      this.fXML11Components = null;
      this.fCommonComponents = null;
      this.fParseInProgress = false;
      this.fConfigUpdated = false;
      this.fXML11DatatypeFactory = null;
      this.fXML11NSDocScanner = null;
      this.fXML11DocScanner = null;
      this.fXML11DTDScanner = null;
      this.f11Initialized = false;
      this.fComponents = new ArrayList();
      this.fXML11Components = new ArrayList();
      this.fCommonComponents = new ArrayList();
      this.fFeatures = new HashMap();
      this.fProperties = new HashMap();
      String[] recognizedFeatures = new String[]{"http://apache.org/xml/features/continue-after-fatal-error", "http://xml.org/sax/features/validation", "http://xml.org/sax/features/namespaces", "http://xml.org/sax/features/external-general-entities", "http://xml.org/sax/features/external-parameter-entities", "http://apache.org/xml/features/internal/parser-settings"};
      this.addRecognizedFeatures(recognizedFeatures);
      this.fFeatures.put("http://xml.org/sax/features/validation", Boolean.FALSE);
      this.fFeatures.put("http://xml.org/sax/features/namespaces", Boolean.TRUE);
      this.fFeatures.put("http://xml.org/sax/features/external-general-entities", Boolean.TRUE);
      this.fFeatures.put("http://xml.org/sax/features/external-parameter-entities", Boolean.TRUE);
      this.fFeatures.put("http://apache.org/xml/features/continue-after-fatal-error", Boolean.FALSE);
      this.fFeatures.put("http://apache.org/xml/features/internal/parser-settings", Boolean.TRUE);
      String[] recognizedProperties = new String[]{"http://xml.org/sax/properties/xml-string", "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/document-scanner", "http://apache.org/xml/properties/internal/dtd-scanner", "http://apache.org/xml/properties/internal/validator/dtd", "http://apache.org/xml/properties/internal/datatype-validator-factory", "http://apache.org/xml/properties/internal/validation-manager", "http://xml.org/sax/properties/xml-string", "http://apache.org/xml/properties/internal/grammar-pool"};
      this.addRecognizedProperties(recognizedProperties);
      if (symbolTable == null) {
         symbolTable = new SymbolTable();
      }

      this.fSymbolTable = symbolTable;
      this.fProperties.put("http://apache.org/xml/properties/internal/symbol-table", this.fSymbolTable);
      this.fGrammarPool = grammarPool;
      if (this.fGrammarPool != null) {
         this.fProperties.put("http://apache.org/xml/properties/internal/grammar-pool", this.fGrammarPool);
      }

      this.fEntityManager = new XMLEntityManager();
      this.fProperties.put("http://apache.org/xml/properties/internal/entity-manager", this.fEntityManager);
      this.addCommonComponent(this.fEntityManager);
      this.fErrorReporter = new XMLErrorReporter();
      this.fErrorReporter.setDocumentLocator(this.fEntityManager.getEntityScanner());
      this.fProperties.put("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
      this.addCommonComponent(this.fErrorReporter);
      this.fNamespaceScanner = new XMLNSDocumentScannerImpl();
      this.fProperties.put("http://apache.org/xml/properties/internal/document-scanner", this.fNamespaceScanner);
      this.addComponent(this.fNamespaceScanner);
      this.fDTDScanner = new XMLDTDScannerImpl();
      this.fProperties.put("http://apache.org/xml/properties/internal/dtd-scanner", this.fDTDScanner);
      this.addComponent((XMLComponent)this.fDTDScanner);
      this.fDatatypeValidatorFactory = DTDDVFactory.getInstance();
      this.fProperties.put("http://apache.org/xml/properties/internal/datatype-validator-factory", this.fDatatypeValidatorFactory);
      this.fValidationManager = new ValidationManager();
      this.fProperties.put("http://apache.org/xml/properties/internal/validation-manager", this.fValidationManager);
      this.fVersionDetector = new XMLVersionDetector();
      if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null) {
         XMLMessageFormatter xmft = new XMLMessageFormatter();
         this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", xmft);
         this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", xmft);
      }

      try {
         this.setLocale(Locale.getDefault());
      } catch (XNIException var7) {
      }

      this.fConfigUpdated = false;
   }

   public void setInputSource(XMLInputSource inputSource) throws XMLConfigurationException, IOException {
      this.fInputSource = inputSource;
   }

   public void setLocale(Locale locale) throws XNIException {
      this.fLocale = locale;
      this.fErrorReporter.setLocale(locale);
   }

   public void setDocumentHandler(XMLDocumentHandler documentHandler) {
      this.fDocumentHandler = documentHandler;
      if (this.fLastComponent != null) {
         this.fLastComponent.setDocumentHandler(this.fDocumentHandler);
         if (this.fDocumentHandler != null) {
            this.fDocumentHandler.setDocumentSource(this.fLastComponent);
         }
      }

   }

   public XMLDocumentHandler getDocumentHandler() {
      return this.fDocumentHandler;
   }

   public void setDTDHandler(XMLDTDHandler dtdHandler) {
      this.fDTDHandler = dtdHandler;
   }

   public XMLDTDHandler getDTDHandler() {
      return this.fDTDHandler;
   }

   public void setDTDContentModelHandler(XMLDTDContentModelHandler handler) {
      this.fDTDContentModelHandler = handler;
   }

   public XMLDTDContentModelHandler getDTDContentModelHandler() {
      return this.fDTDContentModelHandler;
   }

   public void setEntityResolver(XMLEntityResolver resolver) {
      this.fProperties.put("http://apache.org/xml/properties/internal/entity-resolver", resolver);
   }

   public XMLEntityResolver getEntityResolver() {
      return (XMLEntityResolver)this.fProperties.get("http://apache.org/xml/properties/internal/entity-resolver");
   }

   public void setErrorHandler(XMLErrorHandler errorHandler) {
      this.fProperties.put("http://apache.org/xml/properties/internal/error-handler", errorHandler);
   }

   public XMLErrorHandler getErrorHandler() {
      return (XMLErrorHandler)this.fProperties.get("http://apache.org/xml/properties/internal/error-handler");
   }

   public void cleanup() {
      this.fEntityManager.closeReaders();
   }

   public void parse(XMLInputSource source) throws XNIException, IOException {
      if (this.fParseInProgress) {
         throw new XNIException("FWK005 parse may not be called while parsing.");
      } else {
         this.fParseInProgress = true;

         try {
            this.setInputSource(source);
            this.parse(true);
         } catch (XNIException var9) {
            throw var9;
         } catch (IOException var10) {
            throw var10;
         } catch (RuntimeException var11) {
            throw var11;
         } catch (Exception var12) {
            throw new XNIException(var12);
         } finally {
            this.fParseInProgress = false;
            this.cleanup();
         }

      }
   }

   public boolean parse(boolean complete) throws XNIException, IOException {
      if (this.fInputSource != null) {
         try {
            this.fValidationManager.reset();
            this.fVersionDetector.reset(this);
            this.resetCommon();
            short version = this.fVersionDetector.determineDocVersion(this.fInputSource);
            if (version == 2) {
               this.initXML11Components();
               this.configureXML11Pipeline();
               this.resetXML11();
            } else {
               this.configurePipeline();
               this.reset();
            }

            this.fConfigUpdated = false;
            this.fVersionDetector.startDocumentParsing((XMLEntityHandler)this.fCurrentScanner, version);
            this.fInputSource = null;
         } catch (XNIException var7) {
            throw var7;
         } catch (IOException var8) {
            throw var8;
         } catch (RuntimeException var9) {
            throw var9;
         } catch (Exception var10) {
            throw new XNIException(var10);
         }
      }

      try {
         return this.fCurrentScanner.scanDocument(complete);
      } catch (XNIException var3) {
         throw var3;
      } catch (IOException var4) {
         throw var4;
      } catch (RuntimeException var5) {
         throw var5;
      } catch (Exception var6) {
         throw new XNIException(var6);
      }
   }

   public FeatureState getFeatureState(String featureId) throws XMLConfigurationException {
      return featureId.equals("http://apache.org/xml/features/internal/parser-settings") ? FeatureState.is(this.fConfigUpdated) : super.getFeatureState(featureId);
   }

   public void setFeature(String featureId, boolean state) throws XMLConfigurationException {
      this.fConfigUpdated = true;
      int count = this.fComponents.size();

      int i;
      XMLComponent c;
      for(i = 0; i < count; ++i) {
         c = (XMLComponent)this.fComponents.get(i);
         c.setFeature(featureId, state);
      }

      count = this.fCommonComponents.size();

      for(i = 0; i < count; ++i) {
         c = (XMLComponent)this.fCommonComponents.get(i);
         c.setFeature(featureId, state);
      }

      count = this.fXML11Components.size();

      for(i = 0; i < count; ++i) {
         c = (XMLComponent)this.fXML11Components.get(i);

         try {
            c.setFeature(featureId, state);
         } catch (Exception var7) {
         }
      }

      super.setFeature(featureId, state);
   }

   public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
      this.fConfigUpdated = true;
      int count = this.fComponents.size();

      int i;
      XMLComponent c;
      for(i = 0; i < count; ++i) {
         c = (XMLComponent)this.fComponents.get(i);
         c.setProperty(propertyId, value);
      }

      count = this.fCommonComponents.size();

      for(i = 0; i < count; ++i) {
         c = (XMLComponent)this.fCommonComponents.get(i);
         c.setProperty(propertyId, value);
      }

      count = this.fXML11Components.size();

      for(i = 0; i < count; ++i) {
         c = (XMLComponent)this.fXML11Components.get(i);

         try {
            c.setProperty(propertyId, value);
         } catch (Exception var7) {
         }
      }

      super.setProperty(propertyId, value);
   }

   public Locale getLocale() {
      return this.fLocale;
   }

   protected void reset() throws XNIException {
      int count = this.fComponents.size();

      for(int i = 0; i < count; ++i) {
         XMLComponent c = (XMLComponent)this.fComponents.get(i);
         c.reset(this);
      }

   }

   protected void resetCommon() throws XNIException {
      int count = this.fCommonComponents.size();

      for(int i = 0; i < count; ++i) {
         XMLComponent c = (XMLComponent)this.fCommonComponents.get(i);
         c.reset(this);
      }

   }

   protected void resetXML11() throws XNIException {
      int count = this.fXML11Components.size();

      for(int i = 0; i < count; ++i) {
         XMLComponent c = (XMLComponent)this.fXML11Components.get(i);
         c.reset(this);
      }

   }

   protected void configureXML11Pipeline() {
      if (this.fCurrentDVFactory != this.fXML11DatatypeFactory) {
         this.fCurrentDVFactory = this.fXML11DatatypeFactory;
         this.setProperty("http://apache.org/xml/properties/internal/datatype-validator-factory", this.fCurrentDVFactory);
      }

      if (this.fCurrentDTDScanner != this.fXML11DTDScanner) {
         this.fCurrentDTDScanner = this.fXML11DTDScanner;
         this.setProperty("http://apache.org/xml/properties/internal/dtd-scanner", this.fCurrentDTDScanner);
      }

      this.fXML11DTDScanner.setDTDHandler(this.fDTDHandler);
      this.fXML11DTDScanner.setDTDContentModelHandler(this.fDTDContentModelHandler);
      if (this.fFeatures.get("http://xml.org/sax/features/namespaces") == Boolean.TRUE) {
         if (this.fCurrentScanner != this.fXML11NSDocScanner) {
            this.fCurrentScanner = this.fXML11NSDocScanner;
            this.setProperty("http://apache.org/xml/properties/internal/document-scanner", this.fXML11NSDocScanner);
         }

         this.fXML11NSDocScanner.setDTDValidator((XMLDTDValidatorFilter)null);
         this.fXML11NSDocScanner.setDocumentHandler(this.fDocumentHandler);
         if (this.fDocumentHandler != null) {
            this.fDocumentHandler.setDocumentSource(this.fXML11NSDocScanner);
         }

         this.fLastComponent = this.fXML11NSDocScanner;
      } else {
         if (this.fXML11DocScanner == null) {
            this.fXML11DocScanner = new XML11DocumentScannerImpl();
            this.addXML11Component(this.fXML11DocScanner);
         }

         if (this.fCurrentScanner != this.fXML11DocScanner) {
            this.fCurrentScanner = this.fXML11DocScanner;
            this.setProperty("http://apache.org/xml/properties/internal/document-scanner", this.fXML11DocScanner);
         }

         this.fXML11DocScanner.setDocumentHandler(this.fDocumentHandler);
         if (this.fDocumentHandler != null) {
            this.fDocumentHandler.setDocumentSource(this.fXML11DocScanner);
         }

         this.fLastComponent = this.fXML11DocScanner;
      }

   }

   protected void configurePipeline() {
      if (this.fCurrentDVFactory != this.fDatatypeValidatorFactory) {
         this.fCurrentDVFactory = this.fDatatypeValidatorFactory;
         this.setProperty("http://apache.org/xml/properties/internal/datatype-validator-factory", this.fCurrentDVFactory);
      }

      if (this.fCurrentDTDScanner != this.fDTDScanner) {
         this.fCurrentDTDScanner = this.fDTDScanner;
         this.setProperty("http://apache.org/xml/properties/internal/dtd-scanner", this.fCurrentDTDScanner);
      }

      this.fDTDScanner.setDTDHandler(this.fDTDHandler);
      this.fDTDScanner.setDTDContentModelHandler(this.fDTDContentModelHandler);
      if (this.fFeatures.get("http://xml.org/sax/features/namespaces") == Boolean.TRUE) {
         if (this.fCurrentScanner != this.fNamespaceScanner) {
            this.fCurrentScanner = this.fNamespaceScanner;
            this.setProperty("http://apache.org/xml/properties/internal/document-scanner", this.fNamespaceScanner);
         }

         this.fNamespaceScanner.setDTDValidator((XMLDTDValidatorFilter)null);
         this.fNamespaceScanner.setDocumentHandler(this.fDocumentHandler);
         if (this.fDocumentHandler != null) {
            this.fDocumentHandler.setDocumentSource(this.fNamespaceScanner);
         }

         this.fLastComponent = this.fNamespaceScanner;
      } else {
         if (this.fNonNSScanner == null) {
            this.fNonNSScanner = new XMLDocumentScannerImpl();
            this.addComponent(this.fNonNSScanner);
         }

         if (this.fCurrentScanner != this.fNonNSScanner) {
            this.fCurrentScanner = this.fNonNSScanner;
            this.setProperty("http://apache.org/xml/properties/internal/document-scanner", this.fNonNSScanner);
         }

         this.fNonNSScanner.setDocumentHandler(this.fDocumentHandler);
         if (this.fDocumentHandler != null) {
            this.fDocumentHandler.setDocumentSource(this.fNonNSScanner);
         }

         this.fLastComponent = this.fNonNSScanner;
      }

   }

   protected FeatureState checkFeature(String featureId) throws XMLConfigurationException {
      if (featureId.startsWith("http://apache.org/xml/features/")) {
         int suffixLength = featureId.length() - "http://apache.org/xml/features/".length();
         if (suffixLength == "validation/dynamic".length() && featureId.endsWith("validation/dynamic")) {
            return FeatureState.RECOGNIZED;
         }

         if (suffixLength == "validation/default-attribute-values".length() && featureId.endsWith("validation/default-attribute-values")) {
            return FeatureState.NOT_SUPPORTED;
         }

         if (suffixLength == "validation/validate-content-models".length() && featureId.endsWith("validation/validate-content-models")) {
            return FeatureState.NOT_SUPPORTED;
         }

         if (suffixLength == "nonvalidating/load-dtd-grammar".length() && featureId.endsWith("nonvalidating/load-dtd-grammar")) {
            return FeatureState.RECOGNIZED;
         }

         if (suffixLength == "nonvalidating/load-external-dtd".length() && featureId.endsWith("nonvalidating/load-external-dtd")) {
            return FeatureState.RECOGNIZED;
         }

         if (suffixLength == "validation/validate-datatypes".length() && featureId.endsWith("validation/validate-datatypes")) {
            return FeatureState.NOT_SUPPORTED;
         }

         if (suffixLength == "internal/parser-settings".length() && featureId.endsWith("internal/parser-settings")) {
            return FeatureState.NOT_SUPPORTED;
         }
      }

      return super.checkFeature(featureId);
   }

   protected PropertyState checkProperty(String propertyId) throws XMLConfigurationException {
      int suffixLength;
      if (propertyId.startsWith("http://apache.org/xml/properties/")) {
         suffixLength = propertyId.length() - "http://apache.org/xml/properties/".length();
         if (suffixLength == "internal/dtd-scanner".length() && propertyId.endsWith("internal/dtd-scanner")) {
            return PropertyState.RECOGNIZED;
         }
      }

      if (propertyId.startsWith("http://java.sun.com/xml/jaxp/properties/")) {
         suffixLength = propertyId.length() - "http://java.sun.com/xml/jaxp/properties/".length();
         if (suffixLength == "schemaSource".length() && propertyId.endsWith("schemaSource")) {
            return PropertyState.RECOGNIZED;
         }
      }

      if (propertyId.startsWith("http://xml.org/sax/properties/")) {
         suffixLength = propertyId.length() - "http://xml.org/sax/properties/".length();
         if (suffixLength == "xml-string".length() && propertyId.endsWith("xml-string")) {
            return PropertyState.NOT_SUPPORTED;
         }
      }

      return super.checkProperty(propertyId);
   }

   protected void addComponent(XMLComponent component) {
      if (!this.fComponents.contains(component)) {
         this.fComponents.add(component);
         this.addRecognizedParamsAndSetDefaults(component);
      }
   }

   protected void addCommonComponent(XMLComponent component) {
      if (!this.fCommonComponents.contains(component)) {
         this.fCommonComponents.add(component);
         this.addRecognizedParamsAndSetDefaults(component);
      }
   }

   protected void addXML11Component(XMLComponent component) {
      if (!this.fXML11Components.contains(component)) {
         this.fXML11Components.add(component);
         this.addRecognizedParamsAndSetDefaults(component);
      }
   }

   protected void addRecognizedParamsAndSetDefaults(XMLComponent component) {
      String[] recognizedFeatures = component.getRecognizedFeatures();
      this.addRecognizedFeatures(recognizedFeatures);
      String[] recognizedProperties = component.getRecognizedProperties();
      this.addRecognizedProperties(recognizedProperties);
      int i;
      String propertyId;
      if (recognizedFeatures != null) {
         for(i = 0; i < recognizedFeatures.length; ++i) {
            propertyId = recognizedFeatures[i];
            Boolean state = component.getFeatureDefault(propertyId);
            if (state != null && !this.fFeatures.containsKey(propertyId)) {
               this.fFeatures.put(propertyId, state);
               this.fConfigUpdated = true;
            }
         }
      }

      if (recognizedProperties != null) {
         for(i = 0; i < recognizedProperties.length; ++i) {
            propertyId = recognizedProperties[i];
            Object value = component.getPropertyDefault(propertyId);
            if (value != null && !this.fProperties.containsKey(propertyId)) {
               this.fProperties.put(propertyId, value);
               this.fConfigUpdated = true;
            }
         }
      }

   }

   private void initXML11Components() {
      if (!this.f11Initialized) {
         this.fXML11DatatypeFactory = DTDDVFactory.getInstance("com.sun.org.apache.xerces.internal.impl.dv.dtd.XML11DTDDVFactoryImpl");
         this.fXML11DTDScanner = new XML11DTDScannerImpl();
         this.addXML11Component(this.fXML11DTDScanner);
         this.fXML11NSDocScanner = new XML11NSDocumentScannerImpl();
         this.addXML11Component(this.fXML11NSDocScanner);
         this.f11Initialized = true;
      }

   }
}
