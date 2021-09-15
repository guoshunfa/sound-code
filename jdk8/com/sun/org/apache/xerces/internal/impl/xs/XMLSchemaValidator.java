package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.RevalidationHandler;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.dv.DatatypeException;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationState;
import com.sun.org.apache.xerces.internal.impl.xs.identity.Field;
import com.sun.org.apache.xerces.internal.impl.xs.identity.FieldActivator;
import com.sun.org.apache.xerces.internal.impl.xs.identity.IdentityConstraint;
import com.sun.org.apache.xerces.internal.impl.xs.identity.KeyRef;
import com.sun.org.apache.xerces.internal.impl.xs.identity.Selector;
import com.sun.org.apache.xerces.internal.impl.xs.identity.UniqueOrKey;
import com.sun.org.apache.xerces.internal.impl.xs.identity.ValueStore;
import com.sun.org.apache.xerces.internal.impl.xs.identity.XPathMatcher;
import com.sun.org.apache.xerces.internal.impl.xs.models.CMBuilder;
import com.sun.org.apache.xerces.internal.impl.xs.models.CMNodeFactory;
import com.sun.org.apache.xerces.internal.impl.xs.models.XSCMValidator;
import com.sun.org.apache.xerces.internal.parsers.XMLParser;
import com.sun.org.apache.xerces.internal.util.AugmentationsImpl;
import com.sun.org.apache.xerces.internal.util.IntStack;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.util.XMLChar;
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
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import jdk.xml.internal.JdkXmlUtils;

public class XMLSchemaValidator implements XMLComponent, XMLDocumentFilter, FieldActivator, RevalidationHandler {
   private static final boolean DEBUG = false;
   protected static final String VALIDATION = "http://xml.org/sax/features/validation";
   protected static final String SCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
   protected static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
   protected static final String DYNAMIC_VALIDATION = "http://apache.org/xml/features/validation/dynamic";
   protected static final String NORMALIZE_DATA = "http://apache.org/xml/features/validation/schema/normalized-value";
   protected static final String SCHEMA_ELEMENT_DEFAULT = "http://apache.org/xml/features/validation/schema/element-default";
   protected static final String SCHEMA_AUGMENT_PSVI = "http://apache.org/xml/features/validation/schema/augment-psvi";
   protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
   protected static final String STANDARD_URI_CONFORMANT_FEATURE = "http://apache.org/xml/features/standard-uri-conformant";
   protected static final String GENERATE_SYNTHETIC_ANNOTATIONS = "http://apache.org/xml/features/generate-synthetic-annotations";
   protected static final String VALIDATE_ANNOTATIONS = "http://apache.org/xml/features/validate-annotations";
   protected static final String HONOUR_ALL_SCHEMALOCATIONS = "http://apache.org/xml/features/honour-all-schemaLocations";
   protected static final String USE_GRAMMAR_POOL_ONLY = "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only";
   protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
   protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
   protected static final String NAMESPACE_GROWTH = "http://apache.org/xml/features/namespace-growth";
   protected static final String TOLERATE_DUPLICATES = "http://apache.org/xml/features/internal/tolerate-duplicates";
   protected static final String REPORT_WHITESPACE = "http://java.sun.com/xml/schema/features/report-ignored-element-content-whitespace";
   public static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
   public static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
   public static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
   public static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
   protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
   protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
   protected static final String SCHEMA_LOCATION = "http://apache.org/xml/properties/schema/external-schemaLocation";
   protected static final String SCHEMA_NONS_LOCATION = "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";
   protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
   protected static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
   protected static final String SCHEMA_DV_FACTORY = "http://apache.org/xml/properties/internal/validation/schema/dv-factory";
   private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
   protected static final String OVERRIDE_PARSER = "jdk.xml.overrideDefaultParser";
   private static final String[] RECOGNIZED_FEATURES = new String[]{"http://xml.org/sax/features/validation", "http://apache.org/xml/features/validation/schema", "http://apache.org/xml/features/validation/dynamic", "http://apache.org/xml/features/validation/schema-full-checking", "http://apache.org/xml/features/allow-java-encodings", "http://apache.org/xml/features/continue-after-fatal-error", "http://apache.org/xml/features/standard-uri-conformant", "http://apache.org/xml/features/generate-synthetic-annotations", "http://apache.org/xml/features/validate-annotations", "http://apache.org/xml/features/honour-all-schemaLocations", "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only", "http://apache.org/xml/features/namespace-growth", "http://apache.org/xml/features/internal/tolerate-duplicates", "jdk.xml.overrideDefaultParser"};
   private static final Boolean[] FEATURE_DEFAULTS;
   private static final String[] RECOGNIZED_PROPERTIES;
   private static final Object[] PROPERTY_DEFAULTS;
   protected static final int ID_CONSTRAINT_NUM = 1;
   protected ElementPSVImpl fCurrentPSVI = new ElementPSVImpl();
   protected final AugmentationsImpl fAugmentations = new AugmentationsImpl();
   protected final HashMap fMayMatchFieldMap = new HashMap();
   protected XMLString fDefaultValue;
   protected boolean fDynamicValidation = false;
   protected boolean fSchemaDynamicValidation = false;
   protected boolean fDoValidation = false;
   protected boolean fFullChecking = false;
   protected boolean fNormalizeData = true;
   protected boolean fSchemaElementDefault = true;
   protected boolean fAugPSVI = true;
   protected boolean fIdConstraint = false;
   protected boolean fUseGrammarPoolOnly = false;
   protected boolean fNamespaceGrowth = false;
   private String fSchemaType = null;
   protected boolean fEntityRef = false;
   protected boolean fInCDATA = false;
   protected boolean fSawOnlyWhitespaceInElementContent = false;
   protected SymbolTable fSymbolTable;
   private XMLLocator fLocator;
   protected final XMLSchemaValidator.XSIErrorReporter fXSIErrorReporter = new XMLSchemaValidator.XSIErrorReporter();
   protected XMLEntityResolver fEntityResolver;
   protected ValidationManager fValidationManager = null;
   protected ValidationState fValidationState = new ValidationState();
   protected XMLGrammarPool fGrammarPool;
   protected String fExternalSchemas = null;
   protected String fExternalNoNamespaceSchema = null;
   protected Object fJaxpSchemaSource = null;
   protected final XSDDescription fXSDDescription = new XSDDescription();
   protected final Map<String, XMLSchemaLoader.LocationArray> fLocationPairs = new HashMap();
   protected XMLDocumentHandler fDocumentHandler;
   protected XMLDocumentSource fDocumentSource;
   boolean reportWhitespace = false;
   static final int INITIAL_STACK_SIZE = 8;
   static final int INC_STACK_SIZE = 8;
   private static final boolean DEBUG_NORMALIZATION = false;
   private final XMLString fEmptyXMLStr = new XMLString((char[])null, 0, -1);
   private static final int BUFFER_SIZE = 20;
   private final XMLString fNormalizedStr = new XMLString();
   private boolean fFirstChunk = true;
   private boolean fTrailing = false;
   private short fWhiteSpace = -1;
   private boolean fUnionType = false;
   private final XSGrammarBucket fGrammarBucket = new XSGrammarBucket();
   private final SubstitutionGroupHandler fSubGroupHandler;
   private final XSSimpleType fQNameDV;
   private final CMNodeFactory nodeFactory;
   private final CMBuilder fCMBuilder;
   private final XMLSchemaLoader fSchemaLoader;
   private String fValidationRoot;
   private int fSkipValidationDepth;
   private int fNFullValidationDepth;
   private int fNNoneValidationDepth;
   private int fElementDepth;
   private boolean fSubElement;
   private boolean[] fSubElementStack;
   private XSElementDecl fCurrentElemDecl;
   private XSElementDecl[] fElemDeclStack;
   private boolean fNil;
   private boolean[] fNilStack;
   private XSNotationDecl fNotation;
   private XSNotationDecl[] fNotationStack;
   private XSTypeDefinition fCurrentType;
   private XSTypeDefinition[] fTypeStack;
   private XSCMValidator fCurrentCM;
   private XSCMValidator[] fCMStack;
   private int[] fCurrCMState;
   private int[][] fCMStateStack;
   private boolean fStrictAssess;
   private boolean[] fStrictAssessStack;
   private final StringBuffer fBuffer;
   private boolean fAppendBuffer;
   private boolean fSawText;
   private boolean[] fSawTextStack;
   private boolean fSawCharacters;
   private boolean[] fStringContent;
   private final QName fTempQName;
   private ValidatedInfo fValidatedInfo;
   private ValidationState fState4XsiType;
   private ValidationState fState4ApplyDefault;
   protected XMLSchemaValidator.XPathMatcherStack fMatcherStack;
   protected XMLSchemaValidator.ValueStoreCache fValueStoreCache;

   public String[] getRecognizedFeatures() {
      return (String[])((String[])RECOGNIZED_FEATURES.clone());
   }

   public void setFeature(String featureId, boolean state) throws XMLConfigurationException {
   }

   public String[] getRecognizedProperties() {
      return (String[])((String[])RECOGNIZED_PROPERTIES.clone());
   }

   public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
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
      if (documentHandler instanceof XMLParser) {
         try {
            this.reportWhitespace = ((XMLParser)documentHandler).getFeature("http://java.sun.com/xml/schema/features/report-ignored-element-content-whitespace");
         } catch (Exception var3) {
            this.reportWhitespace = false;
         }
      }

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

   public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext, Augmentations augs) throws XNIException {
      this.fValidationState.setNamespaceSupport(namespaceContext);
      this.fState4XsiType.setNamespaceSupport(namespaceContext);
      this.fState4ApplyDefault.setNamespaceSupport(namespaceContext);
      this.fLocator = locator;
      this.handleStartDocument(locator, encoding);
      if (this.fDocumentHandler != null) {
         this.fDocumentHandler.startDocument(locator, encoding, namespaceContext, augs);
      }

   }

   public void xmlDecl(String version, String encoding, String standalone, Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null) {
         this.fDocumentHandler.xmlDecl(version, encoding, standalone, augs);
      }

   }

   public void doctypeDecl(String rootElement, String publicId, String systemId, Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null) {
         this.fDocumentHandler.doctypeDecl(rootElement, publicId, systemId, augs);
      }

   }

   public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
      Augmentations modifiedAugs = this.handleStartElement(element, attributes, augs);
      if (this.fDocumentHandler != null) {
         this.fDocumentHandler.startElement(element, attributes, modifiedAugs);
      }

   }

   public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
      Augmentations modifiedAugs = this.handleStartElement(element, attributes, augs);
      this.fDefaultValue = null;
      if (this.fElementDepth != -2) {
         modifiedAugs = this.handleEndElement(element, modifiedAugs);
      }

      if (this.fDocumentHandler != null) {
         if (this.fSchemaElementDefault && this.fDefaultValue != null) {
            this.fDocumentHandler.startElement(element, attributes, modifiedAugs);
            this.fDocumentHandler.characters(this.fDefaultValue, (Augmentations)null);
            this.fDocumentHandler.endElement(element, modifiedAugs);
         } else {
            this.fDocumentHandler.emptyElement(element, attributes, modifiedAugs);
         }
      }

   }

   public void characters(XMLString text, Augmentations augs) throws XNIException {
      text = this.handleCharacters(text);
      if (this.fSawOnlyWhitespaceInElementContent) {
         this.fSawOnlyWhitespaceInElementContent = false;
         if (!this.reportWhitespace) {
            this.ignorableWhitespace(text, augs);
            return;
         }
      }

      if (this.fDocumentHandler != null) {
         if (this.fNormalizeData && this.fUnionType) {
            if (augs != null) {
               this.fDocumentHandler.characters(this.fEmptyXMLStr, augs);
            }
         } else {
            this.fDocumentHandler.characters(text, augs);
         }
      }

   }

   public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
      this.handleIgnorableWhitespace(text);
      if (this.fDocumentHandler != null) {
         this.fDocumentHandler.ignorableWhitespace(text, augs);
      }

   }

   public void endElement(QName element, Augmentations augs) throws XNIException {
      this.fDefaultValue = null;
      Augmentations modifiedAugs = this.handleEndElement(element, augs);
      if (this.fDocumentHandler != null) {
         if (this.fSchemaElementDefault && this.fDefaultValue != null) {
            this.fDocumentHandler.characters(this.fDefaultValue, (Augmentations)null);
            this.fDocumentHandler.endElement(element, modifiedAugs);
         } else {
            this.fDocumentHandler.endElement(element, modifiedAugs);
         }
      }

   }

   public void startCDATA(Augmentations augs) throws XNIException {
      this.fInCDATA = true;
      if (this.fDocumentHandler != null) {
         this.fDocumentHandler.startCDATA(augs);
      }

   }

   public void endCDATA(Augmentations augs) throws XNIException {
      this.fInCDATA = false;
      if (this.fDocumentHandler != null) {
         this.fDocumentHandler.endCDATA(augs);
      }

   }

   public void endDocument(Augmentations augs) throws XNIException {
      this.handleEndDocument();
      if (this.fDocumentHandler != null) {
         this.fDocumentHandler.endDocument(augs);
      }

      this.fLocator = null;
   }

   public boolean characterData(String data, Augmentations augs) {
      this.fSawText = this.fSawText || data.length() > 0;
      if (this.fNormalizeData && this.fWhiteSpace != -1 && this.fWhiteSpace != 0) {
         this.normalizeWhitespace(data, this.fWhiteSpace == 2);
         this.fBuffer.append(this.fNormalizedStr.ch, this.fNormalizedStr.offset, this.fNormalizedStr.length);
      } else if (this.fAppendBuffer) {
         this.fBuffer.append(data);
      }

      boolean allWhiteSpace = true;
      if (this.fCurrentType != null && this.fCurrentType.getTypeCategory() == 15) {
         XSComplexTypeDecl ctype = (XSComplexTypeDecl)this.fCurrentType;
         if (ctype.fContentType == 2) {
            for(int i = 0; i < data.length(); ++i) {
               if (!XMLChar.isSpace(data.charAt(i))) {
                  allWhiteSpace = false;
                  this.fSawCharacters = true;
                  break;
               }
            }
         }
      }

      return allWhiteSpace;
   }

   public void elementDefault(String data) {
   }

   public void startGeneralEntity(String name, XMLResourceIdentifier identifier, String encoding, Augmentations augs) throws XNIException {
      this.fEntityRef = true;
      if (this.fDocumentHandler != null) {
         this.fDocumentHandler.startGeneralEntity(name, identifier, encoding, augs);
      }

   }

   public void textDecl(String version, String encoding, Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null) {
         this.fDocumentHandler.textDecl(version, encoding, augs);
      }

   }

   public void comment(XMLString text, Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null) {
         this.fDocumentHandler.comment(text, augs);
      }

   }

   public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
      if (this.fDocumentHandler != null) {
         this.fDocumentHandler.processingInstruction(target, data, augs);
      }

   }

   public void endGeneralEntity(String name, Augmentations augs) throws XNIException {
      this.fEntityRef = false;
      if (this.fDocumentHandler != null) {
         this.fDocumentHandler.endGeneralEntity(name, augs);
      }

   }

   public XMLSchemaValidator() {
      this.fSubGroupHandler = new SubstitutionGroupHandler(this.fGrammarBucket);
      this.fQNameDV = (XSSimpleType)SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl("QName");
      this.nodeFactory = new CMNodeFactory();
      this.fCMBuilder = new CMBuilder(this.nodeFactory);
      this.fSchemaLoader = new XMLSchemaLoader(this.fXSIErrorReporter.fErrorReporter, this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder);
      this.fSubElementStack = new boolean[8];
      this.fElemDeclStack = new XSElementDecl[8];
      this.fNilStack = new boolean[8];
      this.fNotationStack = new XSNotationDecl[8];
      this.fTypeStack = new XSTypeDefinition[8];
      this.fCMStack = new XSCMValidator[8];
      this.fCMStateStack = new int[8][];
      this.fStrictAssess = true;
      this.fStrictAssessStack = new boolean[8];
      this.fBuffer = new StringBuffer();
      this.fAppendBuffer = true;
      this.fSawText = false;
      this.fSawTextStack = new boolean[8];
      this.fSawCharacters = false;
      this.fStringContent = new boolean[8];
      this.fTempQName = new QName();
      this.fValidatedInfo = new ValidatedInfo();
      this.fState4XsiType = new ValidationState();
      this.fState4ApplyDefault = new ValidationState();
      this.fMatcherStack = new XMLSchemaValidator.XPathMatcherStack();
      this.fValueStoreCache = new XMLSchemaValidator.ValueStoreCache();
      this.fState4XsiType.setExtraChecking(false);
      this.fState4ApplyDefault.setFacetChecking(false);
   }

   public void reset(XMLComponentManager componentManager) throws XMLConfigurationException {
      this.fIdConstraint = false;
      this.fLocationPairs.clear();
      this.fValidationState.resetIDTables();
      this.nodeFactory.reset(componentManager);
      this.fSchemaLoader.reset(componentManager);
      this.fCurrentElemDecl = null;
      this.fCurrentCM = null;
      this.fCurrCMState = null;
      this.fSkipValidationDepth = -1;
      this.fNFullValidationDepth = -1;
      this.fNNoneValidationDepth = -1;
      this.fElementDepth = -1;
      this.fSubElement = false;
      this.fSchemaDynamicValidation = false;
      this.fEntityRef = false;
      this.fInCDATA = false;
      this.fMatcherStack.clear();
      if (!this.fMayMatchFieldMap.isEmpty()) {
         this.fMayMatchFieldMap.clear();
      }

      this.fXSIErrorReporter.reset((XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
      boolean parser_settings = componentManager.getFeature("http://apache.org/xml/features/internal/parser-settings", true);
      if (!parser_settings) {
         this.fValidationManager.addValidationState(this.fValidationState);
         XMLSchemaLoader.processExternalHints(this.fExternalSchemas, this.fExternalNoNamespaceSchema, this.fLocationPairs, this.fXSIErrorReporter.fErrorReporter);
      } else {
         SymbolTable symbolTable = (SymbolTable)componentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
         if (symbolTable != this.fSymbolTable) {
            this.fSymbolTable = symbolTable;
         }

         this.fNamespaceGrowth = componentManager.getFeature("http://apache.org/xml/features/namespace-growth", false);
         this.fDynamicValidation = componentManager.getFeature("http://apache.org/xml/features/validation/dynamic", false);
         if (this.fDynamicValidation) {
            this.fDoValidation = true;
         } else {
            this.fDoValidation = componentManager.getFeature("http://xml.org/sax/features/validation", false);
         }

         if (this.fDoValidation) {
            this.fDoValidation |= componentManager.getFeature("http://apache.org/xml/features/validation/schema", false);
         }

         this.fFullChecking = componentManager.getFeature("http://apache.org/xml/features/validation/schema-full-checking", false);
         this.fNormalizeData = componentManager.getFeature("http://apache.org/xml/features/validation/schema/normalized-value", false);
         this.fSchemaElementDefault = componentManager.getFeature("http://apache.org/xml/features/validation/schema/element-default", false);
         this.fAugPSVI = componentManager.getFeature("http://apache.org/xml/features/validation/schema/augment-psvi", true);
         this.fSchemaType = (String)componentManager.getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", (Object)null);
         this.fUseGrammarPoolOnly = componentManager.getFeature("http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only", false);
         this.fEntityResolver = (XMLEntityResolver)componentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager");
         this.fValidationManager = (ValidationManager)componentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager");
         this.fValidationManager.addValidationState(this.fValidationState);
         this.fValidationState.setSymbolTable(this.fSymbolTable);

         try {
            this.fExternalSchemas = (String)componentManager.getProperty("http://apache.org/xml/properties/schema/external-schemaLocation");
            this.fExternalNoNamespaceSchema = (String)componentManager.getProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation");
         } catch (XMLConfigurationException var5) {
            this.fExternalSchemas = null;
            this.fExternalNoNamespaceSchema = null;
         }

         XMLSchemaLoader.processExternalHints(this.fExternalSchemas, this.fExternalNoNamespaceSchema, this.fLocationPairs, this.fXSIErrorReporter.fErrorReporter);
         this.fJaxpSchemaSource = componentManager.getProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", (Object)null);
         this.fGrammarPool = (XMLGrammarPool)componentManager.getProperty("http://apache.org/xml/properties/internal/grammar-pool", (Object)null);
         this.fState4XsiType.setSymbolTable(symbolTable);
         this.fState4ApplyDefault.setSymbolTable(symbolTable);
      }
   }

   public void startValueScopeFor(IdentityConstraint identityConstraint, int initialDepth) {
      XMLSchemaValidator.ValueStoreBase valueStore = this.fValueStoreCache.getValueStoreFor(identityConstraint, initialDepth);
      valueStore.startValueScope();
   }

   public XPathMatcher activateField(Field field, int initialDepth) {
      ValueStore valueStore = this.fValueStoreCache.getValueStoreFor(field.getIdentityConstraint(), initialDepth);
      this.setMayMatch(field, Boolean.TRUE);
      XPathMatcher matcher = field.createMatcher(this, valueStore);
      this.fMatcherStack.addMatcher(matcher);
      matcher.startDocumentFragment();
      return matcher;
   }

   public void endValueScopeFor(IdentityConstraint identityConstraint, int initialDepth) {
      XMLSchemaValidator.ValueStoreBase valueStore = this.fValueStoreCache.getValueStoreFor(identityConstraint, initialDepth);
      valueStore.endValueScope();
   }

   public void setMayMatch(Field field, Boolean state) {
      this.fMayMatchFieldMap.put(field, state);
   }

   public Boolean mayMatch(Field field) {
      return (Boolean)this.fMayMatchFieldMap.get(field);
   }

   private void activateSelectorFor(IdentityConstraint ic) {
      Selector selector = ic.getSelector();
      if (selector != null) {
         XPathMatcher matcher = selector.createMatcher(this, this.fElementDepth);
         this.fMatcherStack.addMatcher(matcher);
         matcher.startDocumentFragment();
      }
   }

   void ensureStackCapacity() {
      if (this.fElementDepth == this.fElemDeclStack.length) {
         int newSize = this.fElementDepth + 8;
         boolean[] newArrayB = new boolean[newSize];
         System.arraycopy(this.fSubElementStack, 0, newArrayB, 0, this.fElementDepth);
         this.fSubElementStack = newArrayB;
         XSElementDecl[] newArrayE = new XSElementDecl[newSize];
         System.arraycopy(this.fElemDeclStack, 0, newArrayE, 0, this.fElementDepth);
         this.fElemDeclStack = newArrayE;
         newArrayB = new boolean[newSize];
         System.arraycopy(this.fNilStack, 0, newArrayB, 0, this.fElementDepth);
         this.fNilStack = newArrayB;
         XSNotationDecl[] newArrayN = new XSNotationDecl[newSize];
         System.arraycopy(this.fNotationStack, 0, newArrayN, 0, this.fElementDepth);
         this.fNotationStack = newArrayN;
         XSTypeDefinition[] newArrayT = new XSTypeDefinition[newSize];
         System.arraycopy(this.fTypeStack, 0, newArrayT, 0, this.fElementDepth);
         this.fTypeStack = newArrayT;
         XSCMValidator[] newArrayC = new XSCMValidator[newSize];
         System.arraycopy(this.fCMStack, 0, newArrayC, 0, this.fElementDepth);
         this.fCMStack = newArrayC;
         newArrayB = new boolean[newSize];
         System.arraycopy(this.fSawTextStack, 0, newArrayB, 0, this.fElementDepth);
         this.fSawTextStack = newArrayB;
         newArrayB = new boolean[newSize];
         System.arraycopy(this.fStringContent, 0, newArrayB, 0, this.fElementDepth);
         this.fStringContent = newArrayB;
         newArrayB = new boolean[newSize];
         System.arraycopy(this.fStrictAssessStack, 0, newArrayB, 0, this.fElementDepth);
         this.fStrictAssessStack = newArrayB;
         int[][] newArrayIA = new int[newSize][];
         System.arraycopy(this.fCMStateStack, 0, newArrayIA, 0, this.fElementDepth);
         this.fCMStateStack = newArrayIA;
      }

   }

   void handleStartDocument(XMLLocator locator, String encoding) {
      this.fValueStoreCache.startDocument();
      if (this.fAugPSVI) {
         this.fCurrentPSVI.fGrammars = null;
         this.fCurrentPSVI.fSchemaInformation = null;
      }

   }

   void handleEndDocument() {
      this.fValueStoreCache.endDocument();
   }

   XMLString handleCharacters(XMLString text) {
      if (this.fSkipValidationDepth >= 0) {
         return text;
      } else {
         this.fSawText = this.fSawText || text.length > 0;
         if (this.fNormalizeData && this.fWhiteSpace != -1 && this.fWhiteSpace != 0) {
            this.normalizeWhitespace(text, this.fWhiteSpace == 2);
            text = this.fNormalizedStr;
         }

         if (this.fAppendBuffer) {
            this.fBuffer.append(text.ch, text.offset, text.length);
         }

         this.fSawOnlyWhitespaceInElementContent = false;
         if (this.fCurrentType != null && this.fCurrentType.getTypeCategory() == 15) {
            XSComplexTypeDecl ctype = (XSComplexTypeDecl)this.fCurrentType;
            if (ctype.fContentType == 2) {
               for(int i = text.offset; i < text.offset + text.length; ++i) {
                  if (!XMLChar.isSpace(text.ch[i])) {
                     this.fSawCharacters = true;
                     break;
                  }

                  this.fSawOnlyWhitespaceInElementContent = !this.fSawCharacters;
               }
            }
         }

         return text;
      }
   }

   private void normalizeWhitespace(XMLString value, boolean collapse) {
      boolean skipSpace = collapse;
      boolean sawNonWS = false;
      boolean leading = false;
      boolean trailing = false;
      int size = value.offset + value.length;
      if (this.fNormalizedStr.ch == null || this.fNormalizedStr.ch.length < value.length + 1) {
         this.fNormalizedStr.ch = new char[value.length + 1];
      }

      this.fNormalizedStr.offset = 1;
      this.fNormalizedStr.length = 1;

      for(int i = value.offset; i < size; ++i) {
         char c = value.ch[i];
         if (XMLChar.isSpace(c)) {
            if (!skipSpace) {
               this.fNormalizedStr.ch[this.fNormalizedStr.length++] = ' ';
               skipSpace = collapse;
            }

            if (!sawNonWS) {
               leading = true;
            }
         } else {
            this.fNormalizedStr.ch[this.fNormalizedStr.length++] = c;
            skipSpace = false;
            sawNonWS = true;
         }
      }

      if (skipSpace) {
         if (this.fNormalizedStr.length > 1) {
            --this.fNormalizedStr.length;
            trailing = true;
         } else if (leading && !this.fFirstChunk) {
            trailing = true;
         }
      }

      if (this.fNormalizedStr.length > 1 && !this.fFirstChunk && this.fWhiteSpace == 2) {
         if (this.fTrailing) {
            this.fNormalizedStr.offset = 0;
            this.fNormalizedStr.ch[0] = ' ';
         } else if (leading) {
            this.fNormalizedStr.offset = 0;
            this.fNormalizedStr.ch[0] = ' ';
         }
      }

      XMLString var10000 = this.fNormalizedStr;
      var10000.length -= this.fNormalizedStr.offset;
      this.fTrailing = trailing;
      if (trailing || sawNonWS) {
         this.fFirstChunk = false;
      }

   }

   private void normalizeWhitespace(String value, boolean collapse) {
      boolean skipSpace = collapse;
      int size = value.length();
      if (this.fNormalizedStr.ch == null || this.fNormalizedStr.ch.length < size) {
         this.fNormalizedStr.ch = new char[size];
      }

      this.fNormalizedStr.offset = 0;
      this.fNormalizedStr.length = 0;

      for(int i = 0; i < size; ++i) {
         char c = value.charAt(i);
         if (XMLChar.isSpace(c)) {
            if (!skipSpace) {
               this.fNormalizedStr.ch[this.fNormalizedStr.length++] = ' ';
               skipSpace = collapse;
            }
         } else {
            this.fNormalizedStr.ch[this.fNormalizedStr.length++] = c;
            skipSpace = false;
         }
      }

      if (skipSpace && this.fNormalizedStr.length != 0) {
         --this.fNormalizedStr.length;
      }

   }

   void handleIgnorableWhitespace(XMLString text) {
      if (this.fSkipValidationDepth < 0) {
         ;
      }
   }

   Augmentations handleStartElement(QName element, XMLAttributes attributes, Augmentations augs) {
      if (this.fElementDepth == -1 && this.fValidationManager.isGrammarFound() && this.fSchemaType == null) {
         this.fSchemaDynamicValidation = true;
      }

      String sLocation = attributes.getValue(SchemaSymbols.URI_XSI, SchemaSymbols.XSI_SCHEMALOCATION);
      String nsLocation = attributes.getValue(SchemaSymbols.URI_XSI, SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION);
      this.storeLocations(sLocation, nsLocation);
      if (this.fSkipValidationDepth >= 0) {
         ++this.fElementDepth;
         if (this.fAugPSVI) {
            augs = this.getEmptyAugs(augs);
         }

         return augs;
      } else {
         SchemaGrammar sGrammar = this.findSchemaGrammar((short)5, element.uri, (QName)null, element, attributes);
         Object decl = null;
         String xsiNil;
         if (this.fCurrentCM != null) {
            decl = this.fCurrentCM.oneTransition(element, this.fCurrCMState, this.fSubGroupHandler);
            if (this.fCurrCMState[0] == -1) {
               XSComplexTypeDecl ctype = (XSComplexTypeDecl)this.fCurrentType;
               Vector next;
               if (ctype.fParticle != null && (next = this.fCurrentCM.whatCanGoHere(this.fCurrCMState)).size() > 0) {
                  xsiNil = this.expectedStr(next);
                  this.reportSchemaError("cvc-complex-type.2.4.a", new Object[]{element.rawname, xsiNil});
               } else {
                  this.reportSchemaError("cvc-complex-type.2.4.d", new Object[]{element.rawname});
               }
            }
         }

         if (this.fElementDepth != -1) {
            this.ensureStackCapacity();
            this.fSubElementStack[this.fElementDepth] = true;
            this.fSubElement = false;
            this.fElemDeclStack[this.fElementDepth] = this.fCurrentElemDecl;
            this.fNilStack[this.fElementDepth] = this.fNil;
            this.fNotationStack[this.fElementDepth] = this.fNotation;
            this.fTypeStack[this.fElementDepth] = this.fCurrentType;
            this.fStrictAssessStack[this.fElementDepth] = this.fStrictAssess;
            this.fCMStack[this.fElementDepth] = this.fCurrentCM;
            this.fCMStateStack[this.fElementDepth] = this.fCurrCMState;
            this.fSawTextStack[this.fElementDepth] = this.fSawText;
            this.fStringContent[this.fElementDepth] = this.fSawCharacters;
         }

         ++this.fElementDepth;
         this.fCurrentElemDecl = null;
         XSWildcardDecl wildcard = null;
         this.fCurrentType = null;
         this.fStrictAssess = true;
         this.fNil = false;
         this.fNotation = null;
         this.fBuffer.setLength(0);
         this.fSawText = false;
         this.fSawCharacters = false;
         if (decl != null) {
            if (decl instanceof XSElementDecl) {
               this.fCurrentElemDecl = (XSElementDecl)decl;
            } else {
               wildcard = (XSWildcardDecl)decl;
            }
         }

         if (wildcard != null && wildcard.fProcessContents == 2) {
            this.fSkipValidationDepth = this.fElementDepth;
            if (this.fAugPSVI) {
               augs = this.getEmptyAugs(augs);
            }

            return augs;
         } else {
            if (this.fCurrentElemDecl == null && sGrammar != null) {
               this.fCurrentElemDecl = sGrammar.getGlobalElementDecl(element.localpart);
            }

            if (this.fCurrentElemDecl != null) {
               this.fCurrentType = this.fCurrentElemDecl.fType;
            }

            String xsiType = attributes.getValue(SchemaSymbols.URI_XSI, SchemaSymbols.XSI_TYPE);
            XSComplexTypeDecl ctype;
            if (this.fCurrentType == null && xsiType == null) {
               if (this.fElementDepth == 0) {
                  if (this.fDynamicValidation || this.fSchemaDynamicValidation) {
                     if (this.fDocumentSource != null) {
                        this.fDocumentSource.setDocumentHandler(this.fDocumentHandler);
                        if (this.fDocumentHandler != null) {
                           this.fDocumentHandler.setDocumentSource(this.fDocumentSource);
                        }

                        this.fElementDepth = -2;
                        return augs;
                     }

                     this.fSkipValidationDepth = this.fElementDepth;
                     if (this.fAugPSVI) {
                        augs = this.getEmptyAugs(augs);
                     }

                     return augs;
                  }

                  this.fXSIErrorReporter.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "cvc-elt.1", new Object[]{element.rawname}, (short)1);
               } else if (wildcard != null && wildcard.fProcessContents == 1) {
                  this.reportSchemaError("cvc-complex-type.2.4.c", new Object[]{element.rawname});
               }

               this.fCurrentType = SchemaGrammar.fAnyType;
               this.fStrictAssess = false;
               this.fNFullValidationDepth = this.fElementDepth;
               this.fAppendBuffer = false;
               this.fXSIErrorReporter.pushContext();
            } else {
               this.fXSIErrorReporter.pushContext();
               if (xsiType != null) {
                  XSTypeDefinition oldType = this.fCurrentType;
                  this.fCurrentType = this.getAndCheckXsiType(element, xsiType, attributes);
                  if (this.fCurrentType == null) {
                     if (oldType == null) {
                        this.fCurrentType = SchemaGrammar.fAnyType;
                     } else {
                        this.fCurrentType = oldType;
                     }
                  }
               }

               this.fNNoneValidationDepth = this.fElementDepth;
               if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.getConstraintType() == 2) {
                  this.fAppendBuffer = true;
               } else if (this.fCurrentType.getTypeCategory() == 16) {
                  this.fAppendBuffer = true;
               } else {
                  ctype = (XSComplexTypeDecl)this.fCurrentType;
                  this.fAppendBuffer = ctype.fContentType == 1;
               }
            }

            if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.getAbstract()) {
               this.reportSchemaError("cvc-elt.2", new Object[]{element.rawname});
            }

            if (this.fElementDepth == 0) {
               this.fValidationRoot = element.rawname;
            }

            if (this.fNormalizeData) {
               this.fFirstChunk = true;
               this.fTrailing = false;
               this.fUnionType = false;
               this.fWhiteSpace = -1;
            }

            if (this.fCurrentType.getTypeCategory() == 15) {
               ctype = (XSComplexTypeDecl)this.fCurrentType;
               if (ctype.getAbstract()) {
                  this.reportSchemaError("cvc-type.2", new Object[]{element.rawname});
               }

               if (this.fNormalizeData && ctype.fContentType == 1) {
                  if (ctype.fXSSimpleType.getVariety() == 3) {
                     this.fUnionType = true;
                  } else {
                     try {
                        this.fWhiteSpace = ctype.fXSSimpleType.getWhitespace();
                     } catch (DatatypeException var16) {
                     }
                  }
               }
            } else if (this.fNormalizeData) {
               XSSimpleType dv = (XSSimpleType)this.fCurrentType;
               if (dv.getVariety() == 3) {
                  this.fUnionType = true;
               } else {
                  try {
                     this.fWhiteSpace = dv.getWhitespace();
                  } catch (DatatypeException var15) {
                  }
               }
            }

            this.fCurrentCM = null;
            if (this.fCurrentType.getTypeCategory() == 15) {
               this.fCurrentCM = ((XSComplexTypeDecl)this.fCurrentType).getContentModel(this.fCMBuilder);
            }

            this.fCurrCMState = null;
            if (this.fCurrentCM != null) {
               this.fCurrCMState = this.fCurrentCM.startContentModel();
            }

            xsiNil = attributes.getValue(SchemaSymbols.URI_XSI, SchemaSymbols.XSI_NIL);
            if (xsiNil != null && this.fCurrentElemDecl != null) {
               this.fNil = this.getXsiNil(element, xsiNil);
            }

            XSAttributeGroupDecl attrGrp = null;
            if (this.fCurrentType.getTypeCategory() == 15) {
               XSComplexTypeDecl ctype = (XSComplexTypeDecl)this.fCurrentType;
               attrGrp = ctype.getAttrGrp();
            }

            this.fValueStoreCache.startElement();
            this.fMatcherStack.pushContext();
            if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.fIDCPos > 0) {
               this.fIdConstraint = true;
               this.fValueStoreCache.initValueStoresFor(this.fCurrentElemDecl, this);
            }

            this.processAttributes(element, attributes, attrGrp);
            if (attrGrp != null) {
               this.addDefaultAttributes(element, attributes, attrGrp);
            }

            int count = this.fMatcherStack.getMatcherCount();

            for(int i = 0; i < count; ++i) {
               XPathMatcher matcher = this.fMatcherStack.getMatcherAt(i);
               matcher.startElement(element, attributes);
            }

            if (this.fAugPSVI) {
               augs = this.getEmptyAugs(augs);
               this.fCurrentPSVI.fValidationContext = this.fValidationRoot;
               this.fCurrentPSVI.fDeclaration = this.fCurrentElemDecl;
               this.fCurrentPSVI.fTypeDecl = this.fCurrentType;
               this.fCurrentPSVI.fNotation = this.fNotation;
            }

            return augs;
         }
      }
   }

   Augmentations handleEndElement(QName element, Augmentations augs) {
      if (this.fSkipValidationDepth >= 0) {
         if (this.fSkipValidationDepth == this.fElementDepth && this.fSkipValidationDepth > 0) {
            this.fNFullValidationDepth = this.fSkipValidationDepth - 1;
            this.fSkipValidationDepth = -1;
            --this.fElementDepth;
            this.fSubElement = this.fSubElementStack[this.fElementDepth];
            this.fCurrentElemDecl = this.fElemDeclStack[this.fElementDepth];
            this.fNil = this.fNilStack[this.fElementDepth];
            this.fNotation = this.fNotationStack[this.fElementDepth];
            this.fCurrentType = this.fTypeStack[this.fElementDepth];
            this.fCurrentCM = this.fCMStack[this.fElementDepth];
            this.fStrictAssess = this.fStrictAssessStack[this.fElementDepth];
            this.fCurrCMState = this.fCMStateStack[this.fElementDepth];
            this.fSawText = this.fSawTextStack[this.fElementDepth];
            this.fSawCharacters = this.fStringContent[this.fElementDepth];
         } else {
            --this.fElementDepth;
         }

         if (this.fElementDepth == -1 && this.fFullChecking) {
            XSConstraints.fullSchemaChecking(this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder, this.fXSIErrorReporter.fErrorReporter);
         }

         if (this.fAugPSVI) {
            augs = this.getEmptyAugs(augs);
         }

         return augs;
      } else {
         this.processElementContent(element);
         int oldCount = this.fMatcherStack.getMatcherCount();

         int newCount;
         XPathMatcher grammars;
         for(newCount = oldCount - 1; newCount >= 0; --newCount) {
            grammars = this.fMatcherStack.getMatcherAt(newCount);
            if (this.fCurrentElemDecl == null) {
               grammars.endElement(element, (XSTypeDefinition)null, false, this.fValidatedInfo.actualValue, this.fValidatedInfo.actualValueType, this.fValidatedInfo.itemValueTypes);
            } else {
               grammars.endElement(element, this.fCurrentType, this.fCurrentElemDecl.getNillable(), this.fDefaultValue == null ? this.fValidatedInfo.actualValue : this.fCurrentElemDecl.fDefault.actualValue, this.fDefaultValue == null ? this.fValidatedInfo.actualValueType : this.fCurrentElemDecl.fDefault.actualValueType, this.fDefaultValue == null ? this.fValidatedInfo.itemValueTypes : this.fCurrentElemDecl.fDefault.itemValueTypes);
            }
         }

         if (this.fMatcherStack.size() > 0) {
            this.fMatcherStack.popContext();
         }

         newCount = this.fMatcherStack.getMatcherCount();

         XPathMatcher matcher;
         Selector.Matcher selMatcher;
         IdentityConstraint id;
         int i;
         for(i = oldCount - 1; i >= newCount; --i) {
            matcher = this.fMatcherStack.getMatcherAt(i);
            if (matcher instanceof Selector.Matcher) {
               selMatcher = (Selector.Matcher)matcher;
               if ((id = selMatcher.getIdentityConstraint()) != null && id.getCategory() != 2) {
                  this.fValueStoreCache.transplant(id, selMatcher.getInitialDepth());
               }
            }
         }

         for(i = oldCount - 1; i >= newCount; --i) {
            matcher = this.fMatcherStack.getMatcherAt(i);
            if (matcher instanceof Selector.Matcher) {
               selMatcher = (Selector.Matcher)matcher;
               if ((id = selMatcher.getIdentityConstraint()) != null && id.getCategory() == 2) {
                  XMLSchemaValidator.ValueStoreBase values = this.fValueStoreCache.getValueStoreFor(id, selMatcher.getInitialDepth());
                  if (values != null) {
                     values.endDocumentFragment();
                  }
               }
            }
         }

         this.fValueStoreCache.endElement();
         grammars = null;
         if (this.fElementDepth == 0) {
            String invIdRef = this.fValidationState.checkIDRefID();
            this.fValidationState.resetIDTables();
            if (invIdRef != null) {
               this.reportSchemaError("cvc-id.1", new Object[]{invIdRef});
            }

            if (this.fFullChecking) {
               XSConstraints.fullSchemaChecking(this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder, this.fXSIErrorReporter.fErrorReporter);
            }

            SchemaGrammar[] grammars = this.fGrammarBucket.getGrammars();
            if (this.fGrammarPool != null) {
               for(int k = 0; k < grammars.length; ++k) {
                  grammars[k].setImmutable(true);
               }

               this.fGrammarPool.cacheGrammars("http://www.w3.org/2001/XMLSchema", grammars);
            }

            augs = this.endElementPSVI(true, grammars, augs);
         } else {
            augs = this.endElementPSVI(false, grammars, augs);
            --this.fElementDepth;
            this.fSubElement = this.fSubElementStack[this.fElementDepth];
            this.fCurrentElemDecl = this.fElemDeclStack[this.fElementDepth];
            this.fNil = this.fNilStack[this.fElementDepth];
            this.fNotation = this.fNotationStack[this.fElementDepth];
            this.fCurrentType = this.fTypeStack[this.fElementDepth];
            this.fCurrentCM = this.fCMStack[this.fElementDepth];
            this.fStrictAssess = this.fStrictAssessStack[this.fElementDepth];
            this.fCurrCMState = this.fCMStateStack[this.fElementDepth];
            this.fSawText = this.fSawTextStack[this.fElementDepth];
            this.fSawCharacters = this.fStringContent[this.fElementDepth];
            this.fWhiteSpace = -1;
            this.fAppendBuffer = false;
            this.fUnionType = false;
         }

         return augs;
      }
   }

   final Augmentations endElementPSVI(boolean root, SchemaGrammar[] grammars, Augmentations augs) {
      if (this.fAugPSVI) {
         augs = this.getEmptyAugs(augs);
         this.fCurrentPSVI.fDeclaration = this.fCurrentElemDecl;
         this.fCurrentPSVI.fTypeDecl = this.fCurrentType;
         this.fCurrentPSVI.fNotation = this.fNotation;
         this.fCurrentPSVI.fValidationContext = this.fValidationRoot;
         if (this.fElementDepth > this.fNFullValidationDepth) {
            this.fCurrentPSVI.fValidationAttempted = 2;
         } else if (this.fElementDepth > this.fNNoneValidationDepth) {
            this.fCurrentPSVI.fValidationAttempted = 0;
         } else {
            this.fCurrentPSVI.fValidationAttempted = 1;
            this.fNFullValidationDepth = this.fNNoneValidationDepth = this.fElementDepth - 1;
         }

         if (this.fDefaultValue != null) {
            this.fCurrentPSVI.fSpecified = true;
         }

         this.fCurrentPSVI.fNil = this.fNil;
         this.fCurrentPSVI.fMemberType = this.fValidatedInfo.memberType;
         this.fCurrentPSVI.fNormalizedValue = this.fValidatedInfo.normalizedValue;
         this.fCurrentPSVI.fActualValue = this.fValidatedInfo.actualValue;
         this.fCurrentPSVI.fActualValueType = this.fValidatedInfo.actualValueType;
         this.fCurrentPSVI.fItemValueTypes = this.fValidatedInfo.itemValueTypes;
         if (this.fStrictAssess) {
            String[] errors = this.fXSIErrorReporter.mergeContext();
            this.fCurrentPSVI.fErrorCodes = errors;
            this.fCurrentPSVI.fValidity = (short)(errors == null ? 2 : 1);
         } else {
            this.fCurrentPSVI.fValidity = 0;
            this.fXSIErrorReporter.popContext();
         }

         if (root) {
            this.fCurrentPSVI.fGrammars = grammars;
            this.fCurrentPSVI.fSchemaInformation = null;
         }
      }

      return augs;
   }

   Augmentations getEmptyAugs(Augmentations augs) {
      if (augs == null) {
         augs = this.fAugmentations;
         ((Augmentations)augs).removeAllItems();
      }

      ((Augmentations)augs).putItem("ELEMENT_PSVI", this.fCurrentPSVI);
      this.fCurrentPSVI.reset();
      return (Augmentations)augs;
   }

   void storeLocations(String sLocation, String nsLocation) {
      if (sLocation != null && !XMLSchemaLoader.tokenizeSchemaLocationStr(sLocation, this.fLocationPairs)) {
         this.fXSIErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "SchemaLocation", new Object[]{sLocation}, (short)0);
      }

      if (nsLocation != null) {
         XMLSchemaLoader.LocationArray la = (XMLSchemaLoader.LocationArray)this.fLocationPairs.get(XMLSymbols.EMPTY_STRING);
         if (la == null) {
            la = new XMLSchemaLoader.LocationArray();
            this.fLocationPairs.put(XMLSymbols.EMPTY_STRING, la);
         }

         la.addLocation(nsLocation);
      }

   }

   SchemaGrammar findSchemaGrammar(short contextType, String namespace, QName enclosingElement, QName triggeringComponet, XMLAttributes attributes) {
      SchemaGrammar grammar = null;
      grammar = this.fGrammarBucket.getGrammar(namespace);
      if (grammar == null) {
         this.fXSDDescription.setNamespace(namespace);
         if (this.fGrammarPool != null) {
            grammar = (SchemaGrammar)this.fGrammarPool.retrieveGrammar(this.fXSDDescription);
            if (grammar != null && !this.fGrammarBucket.putGrammar(grammar, true, this.fNamespaceGrowth)) {
               this.fXSIErrorReporter.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "GrammarConflict", (Object[])null, (short)0);
               grammar = null;
            }
         }
      }

      if (grammar == null && !this.fUseGrammarPoolOnly || this.fNamespaceGrowth) {
         this.fXSDDescription.reset();
         this.fXSDDescription.fContextType = contextType;
         this.fXSDDescription.setNamespace(namespace);
         this.fXSDDescription.fEnclosedElementName = enclosingElement;
         this.fXSDDescription.fTriggeringComponent = triggeringComponet;
         this.fXSDDescription.fAttributes = attributes;
         if (this.fLocator != null) {
            this.fXSDDescription.setBaseSystemId(this.fLocator.getExpandedSystemId());
         }

         Map<String, XMLSchemaLoader.LocationArray> locationPairs = this.fLocationPairs;
         XMLSchemaLoader.LocationArray locationArray = (XMLSchemaLoader.LocationArray)locationPairs.get(namespace == null ? XMLSymbols.EMPTY_STRING : namespace);
         if (locationArray != null) {
            String[] temp = locationArray.getLocationArray();
            if (temp.length != 0) {
               this.setLocationHints(this.fXSDDescription, temp, grammar);
            }
         }

         if (grammar == null || this.fXSDDescription.fLocationHints != null) {
            boolean toParseSchema = true;
            if (grammar != null) {
               locationPairs = Collections.emptyMap();
            }

            try {
               XMLInputSource xis = XMLSchemaLoader.resolveDocument(this.fXSDDescription, locationPairs, this.fEntityResolver);
               if (grammar != null && this.fNamespaceGrowth) {
                  try {
                     if (grammar.getDocumentLocations().contains(XMLEntityManager.expandSystemId(xis.getSystemId(), xis.getBaseSystemId(), false))) {
                        toParseSchema = false;
                     }
                  } catch (URI.MalformedURIException var12) {
                  }
               }

               if (toParseSchema) {
                  grammar = this.fSchemaLoader.loadSchema(this.fXSDDescription, xis, this.fLocationPairs);
               }
            } catch (IOException var13) {
               String[] locationHints = this.fXSDDescription.getLocationHints();
               this.fXSIErrorReporter.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "schema_reference.4", new Object[]{locationHints != null ? locationHints[0] : XMLSymbols.EMPTY_STRING}, (short)0);
            }
         }
      }

      return grammar;
   }

   private void setLocationHints(XSDDescription desc, String[] locations, SchemaGrammar grammar) {
      int length = locations.length;
      if (grammar == null) {
         this.fXSDDescription.fLocationHints = new String[length];
         System.arraycopy(locations, 0, this.fXSDDescription.fLocationHints, 0, length);
      } else {
         this.setLocationHints(desc, locations, grammar.getDocumentLocations());
      }

   }

   private void setLocationHints(XSDDescription desc, String[] locations, StringList docLocations) {
      int length = locations.length;
      String[] hints = new String[length];
      int counter = 0;

      for(int i = 0; i < length; ++i) {
         try {
            String id = XMLEntityManager.expandSystemId(locations[i], desc.getBaseSystemId(), false);
            if (!docLocations.contains(id)) {
               hints[counter++] = locations[i];
            }
         } catch (URI.MalformedURIException var9) {
         }
      }

      if (counter > 0) {
         if (counter == length) {
            this.fXSDDescription.fLocationHints = hints;
         } else {
            this.fXSDDescription.fLocationHints = new String[counter];
            System.arraycopy(hints, 0, this.fXSDDescription.fLocationHints, 0, counter);
         }
      }

   }

   XSTypeDefinition getAndCheckXsiType(QName element, String xsiType, XMLAttributes attributes) {
      QName typeName = null;

      try {
         typeName = (QName)this.fQNameDV.validate((String)xsiType, this.fValidationState, (ValidatedInfo)null);
      } catch (InvalidDatatypeValueException var7) {
         this.reportSchemaError(var7.getKey(), var7.getArgs());
         this.reportSchemaError("cvc-elt.4.1", new Object[]{element.rawname, SchemaSymbols.URI_XSI + "," + SchemaSymbols.XSI_TYPE, xsiType});
         return null;
      }

      XSTypeDefinition type = null;
      if (typeName.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA) {
         type = SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl(typeName.localpart);
      }

      if (type == null) {
         SchemaGrammar grammar = this.findSchemaGrammar((short)7, typeName.uri, element, typeName, attributes);
         if (grammar != null) {
            type = grammar.getGlobalTypeDecl(typeName.localpart);
         }
      }

      if (type == null) {
         this.reportSchemaError("cvc-elt.4.2", new Object[]{element.rawname, xsiType});
         return null;
      } else {
         if (this.fCurrentType != null) {
            short block = this.fCurrentElemDecl.fBlock;
            if (this.fCurrentType.getTypeCategory() == 15) {
               block |= ((XSComplexTypeDecl)this.fCurrentType).fBlock;
            }

            if (!XSConstraints.checkTypeDerivationOk(type, this.fCurrentType, block)) {
               this.reportSchemaError("cvc-elt.4.3", new Object[]{element.rawname, xsiType, this.fCurrentType.getName()});
            }
         }

         return type;
      }
   }

   boolean getXsiNil(QName element, String xsiNil) {
      if (this.fCurrentElemDecl != null && !this.fCurrentElemDecl.getNillable()) {
         this.reportSchemaError("cvc-elt.3.1", new Object[]{element.rawname, SchemaSymbols.URI_XSI + "," + SchemaSymbols.XSI_NIL});
      } else {
         String value = XMLChar.trim(xsiNil);
         if (value.equals("true") || value.equals("1")) {
            if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.getConstraintType() == 2) {
               this.reportSchemaError("cvc-elt.3.2.2", new Object[]{element.rawname, SchemaSymbols.URI_XSI + "," + SchemaSymbols.XSI_NIL});
            }

            return true;
         }
      }

      return false;
   }

   void processAttributes(QName element, XMLAttributes attributes, XSAttributeGroupDecl attrGrp) {
      String wildcardIDName = null;
      int attCount = attributes.getLength();
      Augmentations augs = null;
      AttributePSVImpl attrPSVI = null;
      boolean isSimple = this.fCurrentType == null || this.fCurrentType.getTypeCategory() == 16;
      XSObjectList attrUses = null;
      int useCount = 0;
      XSWildcardDecl attrWildcard = null;
      if (!isSimple) {
         attrUses = attrGrp.getAttributeUses();
         useCount = attrUses.getLength();
         attrWildcard = attrGrp.fAttributeWC;
      }

      for(int index = 0; index < attCount; ++index) {
         attributes.getName(index, this.fTempQName);
         if (this.fAugPSVI || this.fIdConstraint) {
            augs = attributes.getAugmentations(index);
            attrPSVI = (AttributePSVImpl)augs.getItem("ATTRIBUTE_PSVI");
            if (attrPSVI != null) {
               attrPSVI.reset();
            } else {
               attrPSVI = new AttributePSVImpl();
               augs.putItem("ATTRIBUTE_PSVI", attrPSVI);
            }

            attrPSVI.fValidationContext = this.fValidationRoot;
         }

         if (this.fTempQName.uri == SchemaSymbols.URI_XSI) {
            XSAttributeDecl attrDecl = null;
            if (this.fTempQName.localpart == SchemaSymbols.XSI_SCHEMALOCATION) {
               attrDecl = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_SCHEMALOCATION);
            } else if (this.fTempQName.localpart == SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION) {
               attrDecl = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION);
            } else if (this.fTempQName.localpart == SchemaSymbols.XSI_NIL) {
               attrDecl = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_NIL);
            } else if (this.fTempQName.localpart == SchemaSymbols.XSI_TYPE) {
               attrDecl = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_TYPE);
            }

            if (attrDecl != null) {
               this.processOneAttribute(element, attributes, index, attrDecl, (XSAttributeUseImpl)null, attrPSVI);
               continue;
            }
         }

         if (this.fTempQName.rawname != XMLSymbols.PREFIX_XMLNS && !this.fTempQName.rawname.startsWith("xmlns:")) {
            if (isSimple) {
               this.reportSchemaError("cvc-type.3.1.1", new Object[]{element.rawname, this.fTempQName.rawname});
            } else {
               XSAttributeUseImpl currUse = null;

               for(int i = 0; i < useCount; ++i) {
                  XSAttributeUseImpl oneUse = (XSAttributeUseImpl)attrUses.item(i);
                  if (oneUse.fAttrDecl.fName == this.fTempQName.localpart && oneUse.fAttrDecl.fTargetNamespace == this.fTempQName.uri) {
                     currUse = oneUse;
                     break;
                  }
               }

               if (currUse != null || attrWildcard != null && attrWildcard.allowNamespace(this.fTempQName.uri)) {
                  XSAttributeDecl currDecl = null;
                  if (currUse != null) {
                     currDecl = currUse.fAttrDecl;
                  } else {
                     if (attrWildcard.fProcessContents == 2) {
                        continue;
                     }

                     SchemaGrammar grammar = this.findSchemaGrammar((short)6, this.fTempQName.uri, element, this.fTempQName, attributes);
                     if (grammar != null) {
                        currDecl = grammar.getGlobalAttributeDecl(this.fTempQName.localpart);
                     }

                     if (currDecl == null) {
                        if (attrWildcard.fProcessContents == 1) {
                           this.reportSchemaError("cvc-complex-type.3.2.2", new Object[]{element.rawname, this.fTempQName.rawname});
                        }
                        continue;
                     }

                     if (currDecl.fType.getTypeCategory() == 16 && currDecl.fType.isIDType()) {
                        if (wildcardIDName != null) {
                           this.reportSchemaError("cvc-complex-type.5.1", new Object[]{element.rawname, currDecl.fName, wildcardIDName});
                        } else {
                           wildcardIDName = currDecl.fName;
                        }
                     }
                  }

                  this.processOneAttribute(element, attributes, index, currDecl, currUse, attrPSVI);
               } else {
                  this.reportSchemaError("cvc-complex-type.3.2.2", new Object[]{element.rawname, this.fTempQName.rawname});
               }
            }
         }
      }

      if (!isSimple && attrGrp.fIDAttrName != null && wildcardIDName != null) {
         this.reportSchemaError("cvc-complex-type.5.2", new Object[]{element.rawname, wildcardIDName, attrGrp.fIDAttrName});
      }

   }

   void processOneAttribute(QName element, XMLAttributes attributes, int index, XSAttributeDecl currDecl, XSAttributeUseImpl currUse, AttributePSVImpl attrPSVI) {
      String attrValue = attributes.getValue(index);
      this.fXSIErrorReporter.pushContext();
      XSSimpleType attDV = currDecl.fType;
      Object actualValue = null;

      try {
         actualValue = attDV.validate((String)attrValue, this.fValidationState, this.fValidatedInfo);
         if (this.fNormalizeData) {
            attributes.setValue(index, this.fValidatedInfo.normalizedValue);
         }

         if (attributes instanceof XMLAttributesImpl) {
            XMLAttributesImpl attrs = (XMLAttributesImpl)attributes;
            boolean schemaId = this.fValidatedInfo.memberType != null ? this.fValidatedInfo.memberType.isIDType() : attDV.isIDType();
            attrs.setSchemaId(index, schemaId);
         }

         if (attDV.getVariety() == 1 && attDV.getPrimitiveKind() == 20) {
            QName qName = (QName)actualValue;
            SchemaGrammar grammar = this.fGrammarBucket.getGrammar(qName.uri);
            if (grammar != null) {
               this.fNotation = grammar.getGlobalNotationDecl(qName.localpart);
            }
         }
      } catch (InvalidDatatypeValueException var12) {
         this.reportSchemaError(var12.getKey(), var12.getArgs());
         this.reportSchemaError("cvc-attribute.3", new Object[]{element.rawname, this.fTempQName.rawname, attrValue, attDV.getName()});
      }

      if (actualValue != null && currDecl.getConstraintType() == 2 && (!this.isComparable(this.fValidatedInfo, currDecl.fDefault) || !actualValue.equals(currDecl.fDefault.actualValue))) {
         this.reportSchemaError("cvc-attribute.4", new Object[]{element.rawname, this.fTempQName.rawname, attrValue, currDecl.fDefault.stringValue()});
      }

      if (actualValue != null && currUse != null && currUse.fConstraintType == 2 && (!this.isComparable(this.fValidatedInfo, currUse.fDefault) || !actualValue.equals(currUse.fDefault.actualValue))) {
         this.reportSchemaError("cvc-complex-type.3.1", new Object[]{element.rawname, this.fTempQName.rawname, attrValue, currUse.fDefault.stringValue()});
      }

      if (this.fIdConstraint) {
         attrPSVI.fActualValue = actualValue;
      }

      if (this.fAugPSVI) {
         attrPSVI.fDeclaration = currDecl;
         attrPSVI.fTypeDecl = attDV;
         attrPSVI.fMemberType = this.fValidatedInfo.memberType;
         attrPSVI.fNormalizedValue = this.fValidatedInfo.normalizedValue;
         attrPSVI.fActualValue = this.fValidatedInfo.actualValue;
         attrPSVI.fActualValueType = this.fValidatedInfo.actualValueType;
         attrPSVI.fItemValueTypes = this.fValidatedInfo.itemValueTypes;
         attrPSVI.fValidationAttempted = 2;
         String[] errors = this.fXSIErrorReporter.mergeContext();
         attrPSVI.fErrorCodes = errors;
         attrPSVI.fValidity = (short)(errors == null ? 2 : 1);
      }

   }

   void addDefaultAttributes(QName element, XMLAttributes attributes, XSAttributeGroupDecl attrGrp) {
      XSObjectList attrUses = attrGrp.getAttributeUses();
      int useCount = attrUses.getLength();

      for(int i = 0; i < useCount; ++i) {
         XSAttributeUseImpl currUse = (XSAttributeUseImpl)attrUses.item(i);
         XSAttributeDecl currDecl = currUse.fAttrDecl;
         short constType = currUse.fConstraintType;
         ValidatedInfo defaultValue = currUse.fDefault;
         if (constType == 0) {
            constType = currDecl.getConstraintType();
            defaultValue = currDecl.fDefault;
         }

         boolean isSpecified = attributes.getValue(currDecl.fTargetNamespace, currDecl.fName) != null;
         if (currUse.fUse == 1 && !isSpecified) {
            this.reportSchemaError("cvc-complex-type.4", new Object[]{element.rawname, currDecl.fName});
         }

         if (!isSpecified && constType != 0) {
            QName attName = new QName((String)null, currDecl.fName, currDecl.fName, currDecl.fTargetNamespace);
            String normalized = defaultValue != null ? defaultValue.stringValue() : "";
            int attrIndex = attributes.addAttribute(attName, "CDATA", normalized);
            if (attributes instanceof XMLAttributesImpl) {
               XMLAttributesImpl attrs = (XMLAttributesImpl)attributes;
               boolean schemaId = defaultValue != null && defaultValue.memberType != null ? defaultValue.memberType.isIDType() : currDecl.fType.isIDType();
               attrs.setSchemaId(attrIndex, schemaId);
            }

            if (this.fAugPSVI) {
               Augmentations augs = attributes.getAugmentations(attrIndex);
               AttributePSVImpl attrPSVI = new AttributePSVImpl();
               augs.putItem("ATTRIBUTE_PSVI", attrPSVI);
               attrPSVI.fDeclaration = currDecl;
               attrPSVI.fTypeDecl = currDecl.fType;
               attrPSVI.fMemberType = defaultValue.memberType;
               attrPSVI.fNormalizedValue = normalized;
               attrPSVI.fActualValue = defaultValue.actualValue;
               attrPSVI.fActualValueType = defaultValue.actualValueType;
               attrPSVI.fItemValueTypes = defaultValue.itemValueTypes;
               attrPSVI.fValidationContext = this.fValidationRoot;
               attrPSVI.fValidity = 2;
               attrPSVI.fValidationAttempted = 2;
               attrPSVI.fSpecified = true;
            }
         }
      }

   }

   void processElementContent(QName element) {
      String content;
      int bufLen;
      if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.fDefault != null && !this.fSawText && !this.fSubElement && !this.fNil) {
         content = this.fCurrentElemDecl.fDefault.stringValue();
         bufLen = content.length();
         if (this.fNormalizedStr.ch == null || this.fNormalizedStr.ch.length < bufLen) {
            this.fNormalizedStr.ch = new char[bufLen];
         }

         content.getChars(0, bufLen, this.fNormalizedStr.ch, 0);
         this.fNormalizedStr.offset = 0;
         this.fNormalizedStr.length = bufLen;
         this.fDefaultValue = this.fNormalizedStr;
      }

      this.fValidatedInfo.normalizedValue = null;
      if (this.fNil && (this.fSubElement || this.fSawText)) {
         this.reportSchemaError("cvc-elt.3.2.1", new Object[]{element.rawname, SchemaSymbols.URI_XSI + "," + SchemaSymbols.XSI_NIL});
      }

      this.fValidatedInfo.reset();
      if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.getConstraintType() != 0 && !this.fSubElement && !this.fSawText && !this.fNil) {
         if (this.fCurrentType != this.fCurrentElemDecl.fType && XSConstraints.ElementDefaultValidImmediate(this.fCurrentType, this.fCurrentElemDecl.fDefault.stringValue(), this.fState4XsiType, (ValidatedInfo)null) == null) {
            this.reportSchemaError("cvc-elt.5.1.1", new Object[]{element.rawname, this.fCurrentType.getName(), this.fCurrentElemDecl.fDefault.stringValue()});
         }

         this.elementLocallyValidType(element, this.fCurrentElemDecl.fDefault.stringValue());
      } else {
         Object actualValue = this.elementLocallyValidType(element, this.fBuffer);
         if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.getConstraintType() == 2 && !this.fNil) {
            String content = this.fBuffer.toString();
            if (this.fSubElement) {
               this.reportSchemaError("cvc-elt.5.2.2.1", new Object[]{element.rawname});
            }

            if (this.fCurrentType.getTypeCategory() == 15) {
               XSComplexTypeDecl ctype = (XSComplexTypeDecl)this.fCurrentType;
               if (ctype.fContentType == 3) {
                  if (!this.fCurrentElemDecl.fDefault.normalizedValue.equals(content)) {
                     this.reportSchemaError("cvc-elt.5.2.2.2.1", new Object[]{element.rawname, content, this.fCurrentElemDecl.fDefault.normalizedValue});
                  }
               } else if (ctype.fContentType == 1 && actualValue != null && (!this.isComparable(this.fValidatedInfo, this.fCurrentElemDecl.fDefault) || !actualValue.equals(this.fCurrentElemDecl.fDefault.actualValue))) {
                  this.reportSchemaError("cvc-elt.5.2.2.2.2", new Object[]{element.rawname, content, this.fCurrentElemDecl.fDefault.stringValue()});
               }
            } else if (this.fCurrentType.getTypeCategory() == 16 && actualValue != null && (!this.isComparable(this.fValidatedInfo, this.fCurrentElemDecl.fDefault) || !actualValue.equals(this.fCurrentElemDecl.fDefault.actualValue))) {
               this.reportSchemaError("cvc-elt.5.2.2.2.2", new Object[]{element.rawname, content, this.fCurrentElemDecl.fDefault.stringValue()});
            }
         }
      }

      if (this.fDefaultValue == null && this.fNormalizeData && this.fDocumentHandler != null && this.fUnionType) {
         content = this.fValidatedInfo.normalizedValue;
         if (content == null) {
            content = this.fBuffer.toString();
         }

         bufLen = content.length();
         if (this.fNormalizedStr.ch == null || this.fNormalizedStr.ch.length < bufLen) {
            this.fNormalizedStr.ch = new char[bufLen];
         }

         content.getChars(0, bufLen, this.fNormalizedStr.ch, 0);
         this.fNormalizedStr.offset = 0;
         this.fNormalizedStr.length = bufLen;
         this.fDocumentHandler.characters(this.fNormalizedStr, (Augmentations)null);
      }

   }

   Object elementLocallyValidType(QName element, Object textContent) {
      if (this.fCurrentType == null) {
         return null;
      } else {
         Object retValue = null;
         if (this.fCurrentType.getTypeCategory() == 16) {
            if (this.fSubElement) {
               this.reportSchemaError("cvc-type.3.1.2", new Object[]{element.rawname});
            }

            if (!this.fNil) {
               XSSimpleType dv = (XSSimpleType)this.fCurrentType;

               try {
                  if (!this.fNormalizeData || this.fUnionType) {
                     this.fValidationState.setNormalizationRequired(true);
                  }

                  retValue = dv.validate((Object)textContent, this.fValidationState, this.fValidatedInfo);
               } catch (InvalidDatatypeValueException var6) {
                  this.reportSchemaError(var6.getKey(), var6.getArgs());
                  this.reportSchemaError("cvc-type.3.1.3", new Object[]{element.rawname, textContent});
               }
            }
         } else {
            retValue = this.elementLocallyValidComplexType(element, textContent);
         }

         return retValue;
      }
   }

   Object elementLocallyValidComplexType(QName element, Object textContent) {
      Object actualValue = null;
      XSComplexTypeDecl ctype = (XSComplexTypeDecl)this.fCurrentType;
      if (!this.fNil) {
         if (ctype.fContentType != 0 || !this.fSubElement && !this.fSawText) {
            if (ctype.fContentType != 1) {
               if (ctype.fContentType == 2 && this.fSawCharacters) {
                  this.reportSchemaError("cvc-complex-type.2.3", new Object[]{element.rawname});
               }
            } else {
               if (this.fSubElement) {
                  this.reportSchemaError("cvc-complex-type.2.2", new Object[]{element.rawname});
               }

               XSSimpleType dv = ctype.fXSSimpleType;

               try {
                  if (!this.fNormalizeData || this.fUnionType) {
                     this.fValidationState.setNormalizationRequired(true);
                  }

                  actualValue = dv.validate((Object)textContent, this.fValidationState, this.fValidatedInfo);
               } catch (InvalidDatatypeValueException var7) {
                  this.reportSchemaError(var7.getKey(), var7.getArgs());
                  this.reportSchemaError("cvc-complex-type.2.2", new Object[]{element.rawname});
               }
            }
         } else {
            this.reportSchemaError("cvc-complex-type.2.1", new Object[]{element.rawname});
         }

         if (ctype.fContentType == 2 || ctype.fContentType == 3) {
            if (this.fCurrCMState[0] >= 0 && !this.fCurrentCM.endContentModel(this.fCurrCMState)) {
               String expected = this.expectedStr(this.fCurrentCM.whatCanGoHere(this.fCurrCMState));
               this.reportSchemaError("cvc-complex-type.2.4.b", new Object[]{element.rawname, expected});
            } else {
               ArrayList errors = this.fCurrentCM.checkMinMaxBounds();
               if (errors != null) {
                  for(int i = 0; i < errors.size(); i += 2) {
                     this.reportSchemaError((String)errors.get(i), new Object[]{element.rawname, errors.get(i + 1)});
                  }
               }
            }
         }
      }

      return actualValue;
   }

   void reportSchemaError(String key, Object[] arguments) {
      if (this.fDoValidation) {
         this.fXSIErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", key, arguments, (short)1);
      }

   }

   private boolean isComparable(ValidatedInfo info1, ValidatedInfo info2) {
      short primitiveType1 = this.convertToPrimitiveKind(info1.actualValueType);
      short primitiveType2 = this.convertToPrimitiveKind(info2.actualValueType);
      if (primitiveType1 != primitiveType2) {
         return primitiveType1 == 1 && primitiveType2 == 2 || primitiveType1 == 2 && primitiveType2 == 1;
      } else {
         if (primitiveType1 == 44 || primitiveType1 == 43) {
            ShortList typeList1 = info1.itemValueTypes;
            ShortList typeList2 = info2.itemValueTypes;
            int typeList1Length = typeList1 != null ? typeList1.getLength() : 0;
            int typeList2Length = typeList2 != null ? typeList2.getLength() : 0;
            if (typeList1Length != typeList2Length) {
               return false;
            }

            for(int i = 0; i < typeList1Length; ++i) {
               short primitiveItem1 = this.convertToPrimitiveKind(typeList1.item(i));
               short primitiveItem2 = this.convertToPrimitiveKind(typeList2.item(i));
               if (primitiveItem1 != primitiveItem2 && (primitiveItem1 != 1 || primitiveItem2 != 2) && (primitiveItem1 != 2 || primitiveItem2 != 1)) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   private short convertToPrimitiveKind(short valueType) {
      if (valueType <= 20) {
         return valueType;
      } else if (valueType <= 29) {
         return 2;
      } else {
         return valueType <= 42 ? 4 : valueType;
      }
   }

   private String expectedStr(Vector expected) {
      StringBuffer ret = new StringBuffer("{");
      int size = expected.size();

      for(int i = 0; i < size; ++i) {
         if (i > 0) {
            ret.append(", ");
         }

         ret.append(expected.elementAt(i).toString());
      }

      ret.append('}');
      return ret.toString();
   }

   static {
      FEATURE_DEFAULTS = new Boolean[]{null, null, null, null, null, null, null, null, null, null, null, null, null, JdkXmlUtils.OVERRIDE_PARSER_DEFAULT};
      RECOGNIZED_PROPERTIES = new String[]{"http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/validation-manager", "http://apache.org/xml/properties/schema/external-schemaLocation", "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", "http://java.sun.com/xml/jaxp/properties/schemaSource", "http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://apache.org/xml/properties/internal/validation/schema/dv-factory", "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager"};
      PROPERTY_DEFAULTS = new Object[]{null, null, null, null, null, null, null, null, null, null, null, null, null};
   }

   protected static final class ShortVector {
      private int fLength;
      private short[] fData;

      public ShortVector() {
      }

      public ShortVector(int initialCapacity) {
         this.fData = new short[initialCapacity];
      }

      public int length() {
         return this.fLength;
      }

      public void add(short value) {
         this.ensureCapacity(this.fLength + 1);
         this.fData[this.fLength++] = value;
      }

      public short valueAt(int position) {
         return this.fData[position];
      }

      public void clear() {
         this.fLength = 0;
      }

      public boolean contains(short value) {
         for(int i = 0; i < this.fLength; ++i) {
            if (this.fData[i] == value) {
               return true;
            }
         }

         return false;
      }

      private void ensureCapacity(int size) {
         if (this.fData == null) {
            this.fData = new short[8];
         } else if (this.fData.length <= size) {
            short[] newdata = new short[this.fData.length * 2];
            System.arraycopy(this.fData, 0, newdata, 0, this.fData.length);
            this.fData = newdata;
         }

      }
   }

   protected class LocalIDKey {
      public IdentityConstraint fId;
      public int fDepth;

      public LocalIDKey() {
      }

      public LocalIDKey(IdentityConstraint id, int depth) {
         this.fId = id;
         this.fDepth = depth;
      }

      public int hashCode() {
         return this.fId.hashCode() + this.fDepth;
      }

      public boolean equals(Object localIDKey) {
         if (!(localIDKey instanceof XMLSchemaValidator.LocalIDKey)) {
            return false;
         } else {
            XMLSchemaValidator.LocalIDKey lIDKey = (XMLSchemaValidator.LocalIDKey)localIDKey;
            return lIDKey.fId == this.fId && lIDKey.fDepth == this.fDepth;
         }
      }
   }

   protected class ValueStoreCache {
      final XMLSchemaValidator.LocalIDKey fLocalId = XMLSchemaValidator.this.new LocalIDKey();
      protected final Vector fValueStores = new Vector();
      protected final Map<XMLSchemaValidator.LocalIDKey, XMLSchemaValidator.ValueStoreBase> fIdentityConstraint2ValueStoreMap = new HashMap();
      protected final Stack<Map<IdentityConstraint, XMLSchemaValidator.ValueStoreBase>> fGlobalMapStack = new Stack();
      protected final Map<IdentityConstraint, XMLSchemaValidator.ValueStoreBase> fGlobalIDConstraintMap = new HashMap();

      public ValueStoreCache() {
      }

      public void startDocument() {
         this.fValueStores.removeAllElements();
         this.fIdentityConstraint2ValueStoreMap.clear();
         this.fGlobalIDConstraintMap.clear();
         this.fGlobalMapStack.removeAllElements();
      }

      public void startElement() {
         if (this.fGlobalIDConstraintMap.size() > 0) {
            this.fGlobalMapStack.push((Map)((HashMap)this.fGlobalIDConstraintMap).clone());
         } else {
            this.fGlobalMapStack.push((Object)null);
         }

         this.fGlobalIDConstraintMap.clear();
      }

      public void endElement() {
         if (!this.fGlobalMapStack.isEmpty()) {
            Map<IdentityConstraint, XMLSchemaValidator.ValueStoreBase> oldMap = (Map)this.fGlobalMapStack.pop();
            if (oldMap != null) {
               Iterator var2 = oldMap.entrySet().iterator();

               while(var2.hasNext()) {
                  Map.Entry<IdentityConstraint, XMLSchemaValidator.ValueStoreBase> entry = (Map.Entry)var2.next();
                  IdentityConstraint id = (IdentityConstraint)entry.getKey();
                  XMLSchemaValidator.ValueStoreBase oldVal = (XMLSchemaValidator.ValueStoreBase)entry.getValue();
                  if (oldVal != null) {
                     XMLSchemaValidator.ValueStoreBase currVal = (XMLSchemaValidator.ValueStoreBase)this.fGlobalIDConstraintMap.get(id);
                     if (currVal == null) {
                        this.fGlobalIDConstraintMap.put(id, oldVal);
                     } else if (currVal != oldVal) {
                        currVal.append(oldVal);
                     }
                  }
               }

            }
         }
      }

      public void initValueStoresFor(XSElementDecl eDecl, FieldActivator activator) {
         IdentityConstraint[] icArray = eDecl.fIDConstraints;
         int icCount = eDecl.fIDCPos;

         for(int i = 0; i < icCount; ++i) {
            XMLSchemaValidator.LocalIDKey toHash;
            switch(icArray[i].getCategory()) {
            case 1:
               UniqueOrKey key = (UniqueOrKey)icArray[i];
               toHash = XMLSchemaValidator.this.new LocalIDKey(key, XMLSchemaValidator.this.fElementDepth);
               XMLSchemaValidator.KeyValueStore keyValueStore = (XMLSchemaValidator.KeyValueStore)this.fIdentityConstraint2ValueStoreMap.get(toHash);
               if (keyValueStore == null) {
                  keyValueStore = XMLSchemaValidator.this.new KeyValueStore(key);
                  this.fIdentityConstraint2ValueStoreMap.put(toHash, keyValueStore);
               } else {
                  keyValueStore.clear();
               }

               this.fValueStores.addElement(keyValueStore);
               XMLSchemaValidator.this.activateSelectorFor(icArray[i]);
               break;
            case 2:
               KeyRef keyRef = (KeyRef)icArray[i];
               toHash = XMLSchemaValidator.this.new LocalIDKey(keyRef, XMLSchemaValidator.this.fElementDepth);
               XMLSchemaValidator.KeyRefValueStore keyRefValueStore = (XMLSchemaValidator.KeyRefValueStore)this.fIdentityConstraint2ValueStoreMap.get(toHash);
               if (keyRefValueStore == null) {
                  keyRefValueStore = XMLSchemaValidator.this.new KeyRefValueStore(keyRef, (XMLSchemaValidator.KeyValueStore)null);
                  this.fIdentityConstraint2ValueStoreMap.put(toHash, keyRefValueStore);
               } else {
                  keyRefValueStore.clear();
               }

               this.fValueStores.addElement(keyRefValueStore);
               XMLSchemaValidator.this.activateSelectorFor(icArray[i]);
               break;
            case 3:
               UniqueOrKey unique = (UniqueOrKey)icArray[i];
               toHash = XMLSchemaValidator.this.new LocalIDKey(unique, XMLSchemaValidator.this.fElementDepth);
               XMLSchemaValidator.UniqueValueStore uniqueValueStore = (XMLSchemaValidator.UniqueValueStore)this.fIdentityConstraint2ValueStoreMap.get(toHash);
               if (uniqueValueStore == null) {
                  uniqueValueStore = XMLSchemaValidator.this.new UniqueValueStore(unique);
                  this.fIdentityConstraint2ValueStoreMap.put(toHash, uniqueValueStore);
               } else {
                  uniqueValueStore.clear();
               }

               this.fValueStores.addElement(uniqueValueStore);
               XMLSchemaValidator.this.activateSelectorFor(icArray[i]);
            }
         }

      }

      public XMLSchemaValidator.ValueStoreBase getValueStoreFor(IdentityConstraint id, int initialDepth) {
         this.fLocalId.fDepth = initialDepth;
         this.fLocalId.fId = id;
         return (XMLSchemaValidator.ValueStoreBase)this.fIdentityConstraint2ValueStoreMap.get(this.fLocalId);
      }

      public XMLSchemaValidator.ValueStoreBase getGlobalValueStoreFor(IdentityConstraint id) {
         return (XMLSchemaValidator.ValueStoreBase)this.fGlobalIDConstraintMap.get(id);
      }

      public void transplant(IdentityConstraint id, int initialDepth) {
         this.fLocalId.fDepth = initialDepth;
         this.fLocalId.fId = id;
         XMLSchemaValidator.ValueStoreBase newVals = (XMLSchemaValidator.ValueStoreBase)this.fIdentityConstraint2ValueStoreMap.get(this.fLocalId);
         if (id.getCategory() != 2) {
            XMLSchemaValidator.ValueStoreBase currVals = (XMLSchemaValidator.ValueStoreBase)this.fGlobalIDConstraintMap.get(id);
            if (currVals != null) {
               currVals.append(newVals);
               this.fGlobalIDConstraintMap.put(id, currVals);
            } else {
               this.fGlobalIDConstraintMap.put(id, newVals);
            }

         }
      }

      public void endDocument() {
         int count = this.fValueStores.size();

         for(int i = 0; i < count; ++i) {
            XMLSchemaValidator.ValueStoreBase valueStore = (XMLSchemaValidator.ValueStoreBase)this.fValueStores.elementAt(i);
            valueStore.endDocument();
         }

      }

      public String toString() {
         String s = super.toString();
         int index1 = s.lastIndexOf(36);
         if (index1 != -1) {
            return s.substring(index1 + 1);
         } else {
            int index2 = s.lastIndexOf(46);
            return index2 != -1 ? s.substring(index2 + 1) : s;
         }
      }
   }

   protected class KeyRefValueStore extends XMLSchemaValidator.ValueStoreBase {
      protected XMLSchemaValidator.ValueStoreBase fKeyValueStore;

      public KeyRefValueStore(KeyRef keyRef, XMLSchemaValidator.KeyValueStore keyValueStore) {
         super(keyRef);
         this.fKeyValueStore = keyValueStore;
      }

      public void endDocumentFragment() {
         super.endDocumentFragment();
         this.fKeyValueStore = (XMLSchemaValidator.ValueStoreBase)XMLSchemaValidator.this.fValueStoreCache.fGlobalIDConstraintMap.get(((KeyRef)this.fIdentityConstraint).getKey());
         String codex;
         if (this.fKeyValueStore == null) {
            String code = "KeyRefOutOfScope";
            codex = this.fIdentityConstraint.toString();
            XMLSchemaValidator.this.reportSchemaError(code, new Object[]{codex});
         } else {
            int errorIndex = this.fKeyValueStore.contains(this);
            if (errorIndex != -1) {
               codex = "KeyNotFound";
               String values = this.toString(this.fValues, errorIndex, this.fFieldCount);
               String element = this.fIdentityConstraint.getElementName();
               String name = this.fIdentityConstraint.getName();
               XMLSchemaValidator.this.reportSchemaError(codex, new Object[]{name, values, element});
            }

         }
      }

      public void endDocument() {
         super.endDocument();
      }
   }

   protected class KeyValueStore extends XMLSchemaValidator.ValueStoreBase {
      public KeyValueStore(UniqueOrKey key) {
         super(key);
      }

      protected void checkDuplicateValues() {
         if (this.contains()) {
            String code = "DuplicateKey";
            String value = this.toString(this.fLocalValues);
            String eName = this.fIdentityConstraint.getElementName();
            String cName = this.fIdentityConstraint.getIdentityConstraintName();
            XMLSchemaValidator.this.reportSchemaError(code, new Object[]{value, eName, cName});
         }

      }
   }

   protected class UniqueValueStore extends XMLSchemaValidator.ValueStoreBase {
      public UniqueValueStore(UniqueOrKey unique) {
         super(unique);
      }

      protected void checkDuplicateValues() {
         if (this.contains()) {
            String code = "DuplicateUnique";
            String value = this.toString(this.fLocalValues);
            String eName = this.fIdentityConstraint.getElementName();
            String cName = this.fIdentityConstraint.getIdentityConstraintName();
            XMLSchemaValidator.this.reportSchemaError(code, new Object[]{value, eName, cName});
         }

      }
   }

   protected abstract class ValueStoreBase implements ValueStore {
      protected IdentityConstraint fIdentityConstraint;
      protected int fFieldCount = 0;
      protected Field[] fFields = null;
      protected Object[] fLocalValues = null;
      protected short[] fLocalValueTypes = null;
      protected ShortList[] fLocalItemValueTypes = null;
      protected int fValuesCount;
      public final Vector fValues = new Vector();
      public XMLSchemaValidator.ShortVector fValueTypes = null;
      public Vector fItemValueTypes = null;
      private boolean fUseValueTypeVector = false;
      private int fValueTypesLength = 0;
      private short fValueType = 0;
      private boolean fUseItemValueTypeVector = false;
      private int fItemValueTypesLength = 0;
      private ShortList fItemValueType = null;
      final StringBuffer fTempBuffer = new StringBuffer();

      protected ValueStoreBase(IdentityConstraint identityConstraint) {
         this.fIdentityConstraint = identityConstraint;
         this.fFieldCount = this.fIdentityConstraint.getFieldCount();
         this.fFields = new Field[this.fFieldCount];
         this.fLocalValues = new Object[this.fFieldCount];
         this.fLocalValueTypes = new short[this.fFieldCount];
         this.fLocalItemValueTypes = new ShortList[this.fFieldCount];

         for(int i = 0; i < this.fFieldCount; ++i) {
            this.fFields[i] = this.fIdentityConstraint.getFieldAt(i);
         }

      }

      public void clear() {
         this.fValuesCount = 0;
         this.fUseValueTypeVector = false;
         this.fValueTypesLength = 0;
         this.fValueType = 0;
         this.fUseItemValueTypeVector = false;
         this.fItemValueTypesLength = 0;
         this.fItemValueType = null;
         this.fValues.setSize(0);
         if (this.fValueTypes != null) {
            this.fValueTypes.clear();
         }

         if (this.fItemValueTypes != null) {
            this.fItemValueTypes.setSize(0);
         }

      }

      public void append(XMLSchemaValidator.ValueStoreBase newVal) {
         for(int i = 0; i < newVal.fValues.size(); ++i) {
            this.fValues.addElement(newVal.fValues.elementAt(i));
         }

      }

      public void startValueScope() {
         this.fValuesCount = 0;

         for(int i = 0; i < this.fFieldCount; ++i) {
            this.fLocalValues[i] = null;
            this.fLocalValueTypes[i] = 0;
            this.fLocalItemValueTypes[i] = null;
         }

      }

      public void endValueScope() {
         String code;
         String eName;
         if (this.fValuesCount == 0) {
            if (this.fIdentityConstraint.getCategory() == 1) {
               code = "AbsentKeyValue";
               String eNamex = this.fIdentityConstraint.getElementName();
               eName = this.fIdentityConstraint.getIdentityConstraintName();
               XMLSchemaValidator.this.reportSchemaError(code, new Object[]{eNamex, eName});
            }

         } else if (this.fValuesCount != this.fFieldCount) {
            if (this.fIdentityConstraint.getCategory() == 1) {
               code = "KeyNotEnoughValues";
               UniqueOrKey key = (UniqueOrKey)this.fIdentityConstraint;
               eName = this.fIdentityConstraint.getElementName();
               String cName = key.getIdentityConstraintName();
               XMLSchemaValidator.this.reportSchemaError(code, new Object[]{eName, cName});
            }

         }
      }

      public void endDocumentFragment() {
      }

      public void endDocument() {
      }

      public void reportError(String key, Object[] args) {
         XMLSchemaValidator.this.reportSchemaError(key, args);
      }

      public void addValue(Field field, Object actualValue, short valueType, ShortList itemValueType) {
         int i;
         for(i = this.fFieldCount - 1; i > -1 && this.fFields[i] != field; --i) {
         }

         String code;
         String cName;
         if (i == -1) {
            code = "UnknownField";
            cName = this.fIdentityConstraint.getElementName();
            String cNamex = this.fIdentityConstraint.getIdentityConstraintName();
            XMLSchemaValidator.this.reportSchemaError(code, new Object[]{field.toString(), cName, cNamex});
         } else {
            if (Boolean.TRUE != XMLSchemaValidator.this.mayMatch(field)) {
               code = "FieldMultipleMatch";
               cName = this.fIdentityConstraint.getIdentityConstraintName();
               XMLSchemaValidator.this.reportSchemaError(code, new Object[]{field.toString(), cName});
            } else {
               ++this.fValuesCount;
            }

            this.fLocalValues[i] = actualValue;
            this.fLocalValueTypes[i] = valueType;
            this.fLocalItemValueTypes[i] = itemValueType;
            if (this.fValuesCount == this.fFieldCount) {
               this.checkDuplicateValues();

               for(i = 0; i < this.fFieldCount; ++i) {
                  this.fValues.addElement(this.fLocalValues[i]);
                  this.addValueType(this.fLocalValueTypes[i]);
                  this.addItemValueType(this.fLocalItemValueTypes[i]);
               }
            }

         }
      }

      public boolean contains() {
         int nextx = false;
         int size = this.fValues.size();

         int next;
         label49:
         for(int i = 0; i < size; i = next) {
            next = i + this.fFieldCount;

            for(int j = 0; j < this.fFieldCount; ++j) {
               Object value1 = this.fLocalValues[j];
               Object value2 = this.fValues.elementAt(i);
               short valueType1 = this.fLocalValueTypes[j];
               short valueType2 = this.getValueTypeAt(i);
               if (value1 == null || value2 == null || valueType1 != valueType2 || !value1.equals(value2)) {
                  continue label49;
               }

               if (valueType1 == 44 || valueType1 == 43) {
                  ShortList list1 = this.fLocalItemValueTypes[j];
                  ShortList list2 = this.getItemValueTypeAt(i);
                  if (list1 == null || list2 == null || !list1.equals(list2)) {
                     continue label49;
                  }
               }

               ++i;
            }

            return true;
         }

         return false;
      }

      public int contains(XMLSchemaValidator.ValueStoreBase vsb) {
         Vector values = vsb.fValues;
         int size1 = values.size();
         int ix;
         if (this.fFieldCount > 1) {
            ix = this.fValues.size();

            label72:
            for(int i = 0; i < size1; i += this.fFieldCount) {
               for(int j = 0; j < ix; j += this.fFieldCount) {
                  int k = 0;

                  while(true) {
                     if (k >= this.fFieldCount) {
                        continue label72;
                     }

                     Object value1 = values.elementAt(i + k);
                     Object value2 = this.fValues.elementAt(j + k);
                     short valueType1 = vsb.getValueTypeAt(i + k);
                     short valueType2 = this.getValueTypeAt(j + k);
                     if (value1 != value2 && (valueType1 != valueType2 || value1 == null || !value1.equals(value2))) {
                        break;
                     }

                     if (valueType1 == 44 || valueType1 == 43) {
                        ShortList list1 = vsb.getItemValueTypeAt(i + k);
                        ShortList list2 = this.getItemValueTypeAt(j + k);
                        if (list1 == null || list2 == null || !list1.equals(list2)) {
                           break;
                        }
                     }

                     ++k;
                  }
               }

               return i;
            }
         } else {
            for(ix = 0; ix < size1; ++ix) {
               short val = vsb.getValueTypeAt(ix);
               if (!this.valueTypeContains(val) || !this.fValues.contains(values.elementAt(ix))) {
                  return ix;
               }

               if (val == 44 || val == 43) {
                  ShortList list1x = vsb.getItemValueTypeAt(ix);
                  if (!this.itemValueTypeContains(list1x)) {
                     return ix;
                  }
               }
            }
         }

         return -1;
      }

      protected void checkDuplicateValues() {
      }

      protected String toString(Object[] values) {
         int size = values.length;
         if (size == 0) {
            return "";
         } else {
            this.fTempBuffer.setLength(0);

            for(int i = 0; i < size; ++i) {
               if (i > 0) {
                  this.fTempBuffer.append(',');
               }

               this.fTempBuffer.append(values[i]);
            }

            return this.fTempBuffer.toString();
         }
      }

      protected String toString(Vector values, int start, int length) {
         if (length == 0) {
            return "";
         } else if (length == 1) {
            return String.valueOf(values.elementAt(start));
         } else {
            StringBuffer str = new StringBuffer();

            for(int i = 0; i < length; ++i) {
               if (i > 0) {
                  str.append(',');
               }

               str.append(values.elementAt(start + i));
            }

            return str.toString();
         }
      }

      public String toString() {
         String s = super.toString();
         int index1 = s.lastIndexOf(36);
         if (index1 != -1) {
            s = s.substring(index1 + 1);
         }

         int index2 = s.lastIndexOf(46);
         if (index2 != -1) {
            s = s.substring(index2 + 1);
         }

         return s + '[' + this.fIdentityConstraint + ']';
      }

      private void addValueType(short type) {
         if (this.fUseValueTypeVector) {
            this.fValueTypes.add(type);
         } else if (this.fValueTypesLength++ == 0) {
            this.fValueType = type;
         } else if (this.fValueType != type) {
            this.fUseValueTypeVector = true;
            if (this.fValueTypes == null) {
               this.fValueTypes = new XMLSchemaValidator.ShortVector(this.fValueTypesLength * 2);
            }

            for(int i = 1; i < this.fValueTypesLength; ++i) {
               this.fValueTypes.add(this.fValueType);
            }

            this.fValueTypes.add(type);
         }

      }

      private short getValueTypeAt(int index) {
         return this.fUseValueTypeVector ? this.fValueTypes.valueAt(index) : this.fValueType;
      }

      private boolean valueTypeContains(short value) {
         if (this.fUseValueTypeVector) {
            return this.fValueTypes.contains(value);
         } else {
            return this.fValueType == value;
         }
      }

      private void addItemValueType(ShortList itemValueType) {
         if (this.fUseItemValueTypeVector) {
            this.fItemValueTypes.add(itemValueType);
         } else if (this.fItemValueTypesLength++ == 0) {
            this.fItemValueType = itemValueType;
         } else if (this.fItemValueType != itemValueType && (this.fItemValueType == null || !this.fItemValueType.equals(itemValueType))) {
            this.fUseItemValueTypeVector = true;
            if (this.fItemValueTypes == null) {
               this.fItemValueTypes = new Vector(this.fItemValueTypesLength * 2);
            }

            for(int i = 1; i < this.fItemValueTypesLength; ++i) {
               this.fItemValueTypes.add(this.fItemValueType);
            }

            this.fItemValueTypes.add(itemValueType);
         }

      }

      private ShortList getItemValueTypeAt(int index) {
         return this.fUseItemValueTypeVector ? (ShortList)this.fItemValueTypes.elementAt(index) : this.fItemValueType;
      }

      private boolean itemValueTypeContains(ShortList value) {
         if (this.fUseItemValueTypeVector) {
            return this.fItemValueTypes.contains(value);
         } else {
            return this.fItemValueType == value || this.fItemValueType != null && this.fItemValueType.equals(value);
         }
      }
   }

   protected static class XPathMatcherStack {
      protected XPathMatcher[] fMatchers = new XPathMatcher[4];
      protected int fMatchersCount;
      protected IntStack fContextStack = new IntStack();

      public XPathMatcherStack() {
      }

      public void clear() {
         for(int i = 0; i < this.fMatchersCount; ++i) {
            this.fMatchers[i] = null;
         }

         this.fMatchersCount = 0;
         this.fContextStack.clear();
      }

      public int size() {
         return this.fContextStack.size();
      }

      public int getMatcherCount() {
         return this.fMatchersCount;
      }

      public void addMatcher(XPathMatcher matcher) {
         this.ensureMatcherCapacity();
         this.fMatchers[this.fMatchersCount++] = matcher;
      }

      public XPathMatcher getMatcherAt(int index) {
         return this.fMatchers[index];
      }

      public void pushContext() {
         this.fContextStack.push(this.fMatchersCount);
      }

      public void popContext() {
         this.fMatchersCount = this.fContextStack.pop();
      }

      private void ensureMatcherCapacity() {
         if (this.fMatchersCount == this.fMatchers.length) {
            XPathMatcher[] array = new XPathMatcher[this.fMatchers.length * 2];
            System.arraycopy(this.fMatchers, 0, array, 0, this.fMatchers.length);
            this.fMatchers = array;
         }

      }
   }

   protected final class XSIErrorReporter {
      XMLErrorReporter fErrorReporter;
      Vector fErrors = new Vector();
      int[] fContext = new int[8];
      int fContextCount;

      public void reset(XMLErrorReporter errorReporter) {
         this.fErrorReporter = errorReporter;
         this.fErrors.removeAllElements();
         this.fContextCount = 0;
      }

      public void pushContext() {
         if (XMLSchemaValidator.this.fAugPSVI) {
            if (this.fContextCount == this.fContext.length) {
               int newSize = this.fContextCount + 8;
               int[] newArray = new int[newSize];
               System.arraycopy(this.fContext, 0, newArray, 0, this.fContextCount);
               this.fContext = newArray;
            }

            this.fContext[this.fContextCount++] = this.fErrors.size();
         }
      }

      public String[] popContext() {
         if (!XMLSchemaValidator.this.fAugPSVI) {
            return null;
         } else {
            int contextPos = this.fContext[--this.fContextCount];
            int size = this.fErrors.size() - contextPos;
            if (size == 0) {
               return null;
            } else {
               String[] errors = new String[size];

               for(int i = 0; i < size; ++i) {
                  errors[i] = (String)this.fErrors.elementAt(contextPos + i);
               }

               this.fErrors.setSize(contextPos);
               return errors;
            }
         }
      }

      public String[] mergeContext() {
         if (!XMLSchemaValidator.this.fAugPSVI) {
            return null;
         } else {
            int contextPos = this.fContext[--this.fContextCount];
            int size = this.fErrors.size() - contextPos;
            if (size == 0) {
               return null;
            } else {
               String[] errors = new String[size];

               for(int i = 0; i < size; ++i) {
                  errors[i] = (String)this.fErrors.elementAt(contextPos + i);
               }

               return errors;
            }
         }
      }

      public void reportError(String domain, String key, Object[] arguments, short severity) throws XNIException {
         this.fErrorReporter.reportError(domain, key, arguments, severity);
         if (XMLSchemaValidator.this.fAugPSVI) {
            this.fErrors.addElement(key);
         }

      }

      public void reportError(XMLLocator location, String domain, String key, Object[] arguments, short severity) throws XNIException {
         this.fErrorReporter.reportError(location, domain, key, arguments, severity);
         if (XMLSchemaValidator.this.fAugPSVI) {
            this.fErrors.addElement(key);
         }

      }
   }
}
