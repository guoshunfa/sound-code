package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.util.DOMEntityResolverWrapper;
import com.sun.org.apache.xerces.internal.util.DOMErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
import com.sun.org.apache.xerces.internal.util.PropertyState;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;
import jdk.xml.internal.JdkXmlUtils;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMStringList;
import org.w3c.dom.ls.LSResourceResolver;

public class DOMConfigurationImpl extends ParserConfigurationSettings implements XMLParserConfiguration, DOMConfiguration {
   protected static final String XERCES_VALIDATION = "http://xml.org/sax/features/validation";
   protected static final String XERCES_NAMESPACES = "http://xml.org/sax/features/namespaces";
   protected static final String SCHEMA = "http://apache.org/xml/features/validation/schema";
   protected static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
   protected static final String DYNAMIC_VALIDATION = "http://apache.org/xml/features/validation/dynamic";
   protected static final String NORMALIZE_DATA = "http://apache.org/xml/features/validation/schema/normalized-value";
   protected static final String SEND_PSVI = "http://apache.org/xml/features/validation/schema/augment-psvi";
   protected static final String DTD_VALIDATOR_FACTORY_PROPERTY = "http://apache.org/xml/properties/internal/datatype-validator-factory";
   protected static final String NAMESPACE_GROWTH = "http://apache.org/xml/features/namespace-growth";
   protected static final String TOLERATE_DUPLICATES = "http://apache.org/xml/features/internal/tolerate-duplicates";
   protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
   protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
   protected static final String XML_STRING = "http://xml.org/sax/properties/xml-string";
   protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
   protected static final String GRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
   protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
   protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
   protected static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
   protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
   protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
   protected static final String SCHEMA_DV_FACTORY = "http://apache.org/xml/properties/internal/validation/schema/dv-factory";
   private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
   private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
   XMLDocumentHandler fDocumentHandler;
   protected short features;
   protected static final short NAMESPACES = 1;
   protected static final short DTNORMALIZATION = 2;
   protected static final short ENTITIES = 4;
   protected static final short CDATA = 8;
   protected static final short SPLITCDATA = 16;
   protected static final short COMMENTS = 32;
   protected static final short VALIDATE = 64;
   protected static final short PSVI = 128;
   protected static final short WELLFORMED = 256;
   protected static final short NSDECL = 512;
   protected static final short INFOSET_TRUE_PARAMS = 801;
   protected static final short INFOSET_FALSE_PARAMS = 14;
   protected static final short INFOSET_MASK = 815;
   protected SymbolTable fSymbolTable;
   protected ArrayList fComponents;
   protected ValidationManager fValidationManager;
   protected Locale fLocale;
   protected XMLErrorReporter fErrorReporter;
   protected final DOMErrorHandlerWrapper fErrorHandlerWrapper;
   private DOMStringList fRecognizedParameters;

   protected DOMConfigurationImpl() {
      this((SymbolTable)null, (XMLComponentManager)null);
   }

   protected DOMConfigurationImpl(SymbolTable symbolTable) {
      this(symbolTable, (XMLComponentManager)null);
   }

   protected DOMConfigurationImpl(SymbolTable symbolTable, XMLComponentManager parentSettings) {
      super(parentSettings);
      this.features = 0;
      this.fErrorHandlerWrapper = new DOMErrorHandlerWrapper();
      this.fFeatures = new HashMap();
      this.fProperties = new HashMap();
      String[] recognizedFeatures = new String[]{"http://xml.org/sax/features/validation", "http://xml.org/sax/features/namespaces", "http://apache.org/xml/features/validation/schema", "http://apache.org/xml/features/validation/schema-full-checking", "http://apache.org/xml/features/validation/dynamic", "http://apache.org/xml/features/validation/schema/normalized-value", "http://apache.org/xml/features/validation/schema/augment-psvi", "http://apache.org/xml/features/namespace-growth", "http://apache.org/xml/features/internal/tolerate-duplicates", "jdk.xml.overrideDefaultParser"};
      this.addRecognizedFeatures(recognizedFeatures);
      this.setFeature("http://xml.org/sax/features/validation", false);
      this.setFeature("http://apache.org/xml/features/validation/schema", false);
      this.setFeature("http://apache.org/xml/features/validation/schema-full-checking", false);
      this.setFeature("http://apache.org/xml/features/validation/dynamic", false);
      this.setFeature("http://apache.org/xml/features/validation/schema/normalized-value", false);
      this.setFeature("http://xml.org/sax/features/namespaces", true);
      this.setFeature("http://apache.org/xml/features/validation/schema/augment-psvi", true);
      this.setFeature("http://apache.org/xml/features/namespace-growth", false);
      this.setFeature("jdk.xml.overrideDefaultParser", JdkXmlUtils.OVERRIDE_PARSER_DEFAULT);
      String[] recognizedProperties = new String[]{"http://xml.org/sax/properties/xml-string", "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/validation-manager", "http://apache.org/xml/properties/internal/grammar-pool", "http://java.sun.com/xml/jaxp/properties/schemaSource", "http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://apache.org/xml/properties/internal/datatype-validator-factory", "http://apache.org/xml/properties/internal/validation/schema/dv-factory", "http://apache.org/xml/properties/security-manager", "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager"};
      this.addRecognizedProperties(recognizedProperties);
      this.features = (short)(this.features | 1);
      this.features = (short)(this.features | 4);
      this.features = (short)(this.features | 32);
      this.features = (short)(this.features | 8);
      this.features = (short)(this.features | 16);
      this.features = (short)(this.features | 256);
      this.features = (short)(this.features | 512);
      if (symbolTable == null) {
         symbolTable = new SymbolTable();
      }

      this.fSymbolTable = symbolTable;
      this.fComponents = new ArrayList();
      this.setProperty("http://apache.org/xml/properties/internal/symbol-table", this.fSymbolTable);
      this.fErrorReporter = new XMLErrorReporter();
      this.setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
      this.addComponent(this.fErrorReporter);
      this.setProperty("http://apache.org/xml/properties/internal/datatype-validator-factory", DTDDVFactory.getInstance());
      XMLEntityManager manager = new XMLEntityManager();
      this.setProperty("http://apache.org/xml/properties/internal/entity-manager", manager);
      this.addComponent(manager);
      this.fValidationManager = this.createValidationManager();
      this.setProperty("http://apache.org/xml/properties/internal/validation-manager", this.fValidationManager);
      this.setProperty("http://apache.org/xml/properties/security-manager", new XMLSecurityManager(true));
      this.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", new XMLSecurityPropertyManager());
      if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null) {
         XMLMessageFormatter xmft = new XMLMessageFormatter();
         this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", xmft);
         this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", xmft);
      }

      if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
         MessageFormatter xmft = null;

         try {
            xmft = (MessageFormatter)((MessageFormatter)ObjectFactory.newInstance("com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter", true));
         } catch (Exception var9) {
         }

         if (xmft != null) {
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", xmft);
         }
      }

      try {
         this.setLocale(Locale.getDefault());
      } catch (XNIException var8) {
      }

   }

   public void parse(XMLInputSource inputSource) throws XNIException, IOException {
   }

   public void setDocumentHandler(XMLDocumentHandler documentHandler) {
      this.fDocumentHandler = documentHandler;
   }

   public XMLDocumentHandler getDocumentHandler() {
      return this.fDocumentHandler;
   }

   public void setDTDHandler(XMLDTDHandler dtdHandler) {
   }

   public XMLDTDHandler getDTDHandler() {
      return null;
   }

   public void setDTDContentModelHandler(XMLDTDContentModelHandler handler) {
   }

   public XMLDTDContentModelHandler getDTDContentModelHandler() {
      return null;
   }

   public void setEntityResolver(XMLEntityResolver resolver) {
      if (resolver != null) {
         this.fProperties.put("http://apache.org/xml/properties/internal/entity-resolver", resolver);
      }

   }

   public XMLEntityResolver getEntityResolver() {
      return (XMLEntityResolver)this.fProperties.get("http://apache.org/xml/properties/internal/entity-resolver");
   }

   public void setErrorHandler(XMLErrorHandler errorHandler) {
      if (errorHandler != null) {
         this.fProperties.put("http://apache.org/xml/properties/internal/error-handler", errorHandler);
      }

   }

   public XMLErrorHandler getErrorHandler() {
      return (XMLErrorHandler)this.fProperties.get("http://apache.org/xml/properties/internal/error-handler");
   }

   public void setFeature(String featureId, boolean state) throws XMLConfigurationException {
      super.setFeature(featureId, state);
   }

   public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
      super.setProperty(propertyId, value);
   }

   public void setLocale(Locale locale) throws XNIException {
      this.fLocale = locale;
      this.fErrorReporter.setLocale(locale);
   }

   public Locale getLocale() {
      return this.fLocale;
   }

   public void setParameter(String name, Object value) throws DOMException {
      boolean found = true;
      if (value instanceof Boolean) {
         boolean state = (Boolean)value;
         if (name.equalsIgnoreCase("comments")) {
            this.features = (short)(state ? this.features | 32 : this.features & -33);
         } else if (name.equalsIgnoreCase("datatype-normalization")) {
            this.setFeature("http://apache.org/xml/features/validation/schema/normalized-value", state);
            this.features = (short)(state ? this.features | 2 : this.features & -3);
            if (state) {
               this.features = (short)(this.features | 64);
            }
         } else if (name.equalsIgnoreCase("namespaces")) {
            this.features = (short)(state ? this.features | 1 : this.features & -2);
         } else if (name.equalsIgnoreCase("cdata-sections")) {
            this.features = (short)(state ? this.features | 8 : this.features & -9);
         } else if (name.equalsIgnoreCase("entities")) {
            this.features = (short)(state ? this.features | 4 : this.features & -5);
         } else if (name.equalsIgnoreCase("split-cdata-sections")) {
            this.features = (short)(state ? this.features | 16 : this.features & -17);
         } else if (name.equalsIgnoreCase("validate")) {
            this.features = (short)(state ? this.features | 64 : this.features & -65);
         } else if (name.equalsIgnoreCase("well-formed")) {
            this.features = (short)(state ? this.features | 256 : this.features & -257);
         } else if (name.equalsIgnoreCase("namespace-declarations")) {
            this.features = (short)(state ? this.features | 512 : this.features & -513);
         } else if (name.equalsIgnoreCase("infoset")) {
            if (state) {
               this.features = (short)(this.features | 801);
               this.features = (short)(this.features & -15);
               this.setFeature("http://apache.org/xml/features/validation/schema/normalized-value", false);
            }
         } else {
            String msg;
            if (!name.equalsIgnoreCase("normalize-characters") && !name.equalsIgnoreCase("canonical-form") && !name.equalsIgnoreCase("validate-if-schema") && !name.equalsIgnoreCase("check-character-normalization")) {
               if (name.equalsIgnoreCase("element-content-whitespace")) {
                  if (!state) {
                     msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[]{name});
                     throw new DOMException((short)9, msg);
                  }
               } else if (name.equalsIgnoreCase("http://apache.org/xml/features/validation/schema/augment-psvi")) {
                  if (!state) {
                     msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[]{name});
                     throw new DOMException((short)9, msg);
                  }
               } else if (name.equalsIgnoreCase("psvi")) {
                  this.features = (short)(state ? this.features | 128 : this.features & -129);
               } else {
                  found = false;
               }
            } else if (state) {
               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[]{name});
               throw new DOMException((short)9, msg);
            }
         }
      }

      if (!found || !(value instanceof Boolean)) {
         found = true;
         String msg;
         if (name.equalsIgnoreCase("error-handler")) {
            if (!(value instanceof DOMErrorHandler) && value != null) {
               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[]{name});
               throw new DOMException((short)17, msg);
            }

            this.fErrorHandlerWrapper.setErrorHandler((DOMErrorHandler)value);
            this.setErrorHandler(this.fErrorHandlerWrapper);
         } else if (name.equalsIgnoreCase("resource-resolver")) {
            if (!(value instanceof LSResourceResolver) && value != null) {
               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[]{name});
               throw new DOMException((short)17, msg);
            }

            try {
               this.setEntityResolver(new DOMEntityResolverWrapper((LSResourceResolver)value));
            } catch (XMLConfigurationException var8) {
            }
         } else if (name.equalsIgnoreCase("schema-location")) {
            if (!(value instanceof String) && value != null) {
               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[]{name});
               throw new DOMException((short)17, msg);
            }

            try {
               this.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", value);
            } catch (XMLConfigurationException var7) {
            }
         } else if (name.equalsIgnoreCase("schema-type")) {
            if (!(value instanceof String) && value != null) {
               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[]{name});
               throw new DOMException((short)17, msg);
            }

            try {
               if (value == null) {
                  this.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", (Object)null);
               } else if (value.equals(Constants.NS_XMLSCHEMA)) {
                  this.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", Constants.NS_XMLSCHEMA);
               } else if (value.equals(Constants.NS_DTD)) {
                  this.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", Constants.NS_DTD);
               }
            } catch (XMLConfigurationException var6) {
            }
         } else if (name.equalsIgnoreCase("http://apache.org/xml/properties/internal/symbol-table")) {
            if (!(value instanceof SymbolTable)) {
               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[]{name});
               throw new DOMException((short)17, msg);
            }

            this.setProperty("http://apache.org/xml/properties/internal/symbol-table", value);
         } else {
            if (!name.equalsIgnoreCase("http://apache.org/xml/properties/internal/grammar-pool")) {
               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_FOUND", new Object[]{name});
               throw new DOMException((short)8, msg);
            }

            if (!(value instanceof XMLGrammarPool)) {
               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[]{name});
               throw new DOMException((short)17, msg);
            }

            this.setProperty("http://apache.org/xml/properties/internal/grammar-pool", value);
         }
      }

   }

   public Object getParameter(String name) throws DOMException {
      if (name.equalsIgnoreCase("comments")) {
         return (this.features & 32) != 0 ? Boolean.TRUE : Boolean.FALSE;
      } else if (name.equalsIgnoreCase("namespaces")) {
         return (this.features & 1) != 0 ? Boolean.TRUE : Boolean.FALSE;
      } else if (name.equalsIgnoreCase("datatype-normalization")) {
         return (this.features & 2) != 0 ? Boolean.TRUE : Boolean.FALSE;
      } else if (name.equalsIgnoreCase("cdata-sections")) {
         return (this.features & 8) != 0 ? Boolean.TRUE : Boolean.FALSE;
      } else if (name.equalsIgnoreCase("entities")) {
         return (this.features & 4) != 0 ? Boolean.TRUE : Boolean.FALSE;
      } else if (name.equalsIgnoreCase("split-cdata-sections")) {
         return (this.features & 16) != 0 ? Boolean.TRUE : Boolean.FALSE;
      } else if (name.equalsIgnoreCase("validate")) {
         return (this.features & 64) != 0 ? Boolean.TRUE : Boolean.FALSE;
      } else if (name.equalsIgnoreCase("well-formed")) {
         return (this.features & 256) != 0 ? Boolean.TRUE : Boolean.FALSE;
      } else if (name.equalsIgnoreCase("namespace-declarations")) {
         return (this.features & 512) != 0 ? Boolean.TRUE : Boolean.FALSE;
      } else if (name.equalsIgnoreCase("infoset")) {
         return (this.features & 815) == 801 ? Boolean.TRUE : Boolean.FALSE;
      } else if (!name.equalsIgnoreCase("normalize-characters") && !name.equalsIgnoreCase("canonical-form") && !name.equalsIgnoreCase("validate-if-schema") && !name.equalsIgnoreCase("check-character-normalization")) {
         if (name.equalsIgnoreCase("http://apache.org/xml/features/validation/schema/augment-psvi")) {
            return Boolean.TRUE;
         } else if (name.equalsIgnoreCase("psvi")) {
            return (this.features & 128) != 0 ? Boolean.TRUE : Boolean.FALSE;
         } else if (name.equalsIgnoreCase("element-content-whitespace")) {
            return Boolean.TRUE;
         } else if (name.equalsIgnoreCase("error-handler")) {
            return this.fErrorHandlerWrapper.getErrorHandler();
         } else if (name.equalsIgnoreCase("resource-resolver")) {
            XMLEntityResolver entityResolver = this.getEntityResolver();
            return entityResolver != null && entityResolver instanceof DOMEntityResolverWrapper ? ((DOMEntityResolverWrapper)entityResolver).getEntityResolver() : null;
         } else if (name.equalsIgnoreCase("schema-type")) {
            return this.getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage");
         } else if (name.equalsIgnoreCase("schema-location")) {
            return this.getProperty("http://java.sun.com/xml/jaxp/properties/schemaSource");
         } else if (name.equalsIgnoreCase("http://apache.org/xml/properties/internal/symbol-table")) {
            return this.getProperty("http://apache.org/xml/properties/internal/symbol-table");
         } else if (name.equalsIgnoreCase("http://apache.org/xml/properties/internal/grammar-pool")) {
            return this.getProperty("http://apache.org/xml/properties/internal/grammar-pool");
         } else {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_FOUND", new Object[]{name});
            throw new DOMException((short)8, msg);
         }
      } else {
         return Boolean.FALSE;
      }
   }

   public boolean canSetParameter(String name, Object value) {
      if (value == null) {
         return true;
      } else if (value instanceof Boolean) {
         if (!name.equalsIgnoreCase("comments") && !name.equalsIgnoreCase("datatype-normalization") && !name.equalsIgnoreCase("cdata-sections") && !name.equalsIgnoreCase("entities") && !name.equalsIgnoreCase("split-cdata-sections") && !name.equalsIgnoreCase("namespaces") && !name.equalsIgnoreCase("validate") && !name.equalsIgnoreCase("well-formed") && !name.equalsIgnoreCase("infoset") && !name.equalsIgnoreCase("namespace-declarations")) {
            if (!name.equalsIgnoreCase("normalize-characters") && !name.equalsIgnoreCase("canonical-form") && !name.equalsIgnoreCase("validate-if-schema") && !name.equalsIgnoreCase("check-character-normalization")) {
               if (!name.equalsIgnoreCase("element-content-whitespace") && !name.equalsIgnoreCase("http://apache.org/xml/features/validation/schema/augment-psvi")) {
                  return false;
               } else {
                  return value.equals(Boolean.TRUE);
               }
            } else {
               return !value.equals(Boolean.TRUE);
            }
         } else {
            return true;
         }
      } else if (name.equalsIgnoreCase("error-handler")) {
         return value instanceof DOMErrorHandler;
      } else if (name.equalsIgnoreCase("resource-resolver")) {
         return value instanceof LSResourceResolver;
      } else if (name.equalsIgnoreCase("schema-location")) {
         return value instanceof String;
      } else if (!name.equalsIgnoreCase("schema-type")) {
         if (name.equalsIgnoreCase("http://apache.org/xml/properties/internal/symbol-table")) {
            return value instanceof SymbolTable;
         } else if (name.equalsIgnoreCase("http://apache.org/xml/properties/internal/grammar-pool")) {
            return value instanceof XMLGrammarPool;
         } else {
            return false;
         }
      } else {
         return value instanceof String && value.equals(Constants.NS_XMLSCHEMA);
      }
   }

   public DOMStringList getParameterNames() {
      if (this.fRecognizedParameters == null) {
         Vector parameters = new Vector();
         parameters.add("comments");
         parameters.add("datatype-normalization");
         parameters.add("cdata-sections");
         parameters.add("entities");
         parameters.add("split-cdata-sections");
         parameters.add("namespaces");
         parameters.add("validate");
         parameters.add("infoset");
         parameters.add("normalize-characters");
         parameters.add("canonical-form");
         parameters.add("validate-if-schema");
         parameters.add("check-character-normalization");
         parameters.add("well-formed");
         parameters.add("namespace-declarations");
         parameters.add("element-content-whitespace");
         parameters.add("error-handler");
         parameters.add("schema-type");
         parameters.add("schema-location");
         parameters.add("resource-resolver");
         parameters.add("http://apache.org/xml/properties/internal/grammar-pool");
         parameters.add("http://apache.org/xml/properties/internal/symbol-table");
         parameters.add("http://apache.org/xml/features/validation/schema/augment-psvi");
         this.fRecognizedParameters = new DOMStringListImpl(parameters);
      }

      return this.fRecognizedParameters;
   }

   protected void reset() throws XNIException {
      if (this.fValidationManager != null) {
         this.fValidationManager.reset();
      }

      int count = this.fComponents.size();

      for(int i = 0; i < count; ++i) {
         XMLComponent c = (XMLComponent)this.fComponents.get(i);
         c.reset(this);
      }

   }

   protected PropertyState checkProperty(String propertyId) throws XMLConfigurationException {
      if (propertyId.startsWith("http://xml.org/sax/properties/")) {
         int suffixLength = propertyId.length() - "http://xml.org/sax/properties/".length();
         if (suffixLength == "xml-string".length() && propertyId.endsWith("xml-string")) {
            return PropertyState.NOT_SUPPORTED;
         }
      }

      return super.checkProperty(propertyId);
   }

   protected void addComponent(XMLComponent component) {
      if (!this.fComponents.contains(component)) {
         this.fComponents.add(component);
         String[] recognizedFeatures = component.getRecognizedFeatures();
         this.addRecognizedFeatures(recognizedFeatures);
         String[] recognizedProperties = component.getRecognizedProperties();
         this.addRecognizedProperties(recognizedProperties);
      }
   }

   protected ValidationManager createValidationManager() {
      return new ValidationManager();
   }
}
