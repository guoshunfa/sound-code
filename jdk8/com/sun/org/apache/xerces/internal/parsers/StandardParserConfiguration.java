package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.util.PropertyState;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;

public class StandardParserConfiguration extends DTDConfiguration {
   protected static final String NORMALIZE_DATA = "http://apache.org/xml/features/validation/schema/normalized-value";
   protected static final String SCHEMA_ELEMENT_DEFAULT = "http://apache.org/xml/features/validation/schema/element-default";
   protected static final String SCHEMA_AUGMENT_PSVI = "http://apache.org/xml/features/validation/schema/augment-psvi";
   protected static final String XMLSCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
   protected static final String XMLSCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
   protected static final String GENERATE_SYNTHETIC_ANNOTATIONS = "http://apache.org/xml/features/generate-synthetic-annotations";
   protected static final String VALIDATE_ANNOTATIONS = "http://apache.org/xml/features/validate-annotations";
   protected static final String HONOUR_ALL_SCHEMALOCATIONS = "http://apache.org/xml/features/honour-all-schemaLocations";
   protected static final String NAMESPACE_GROWTH = "http://apache.org/xml/features/namespace-growth";
   protected static final String TOLERATE_DUPLICATES = "http://apache.org/xml/features/internal/tolerate-duplicates";
   protected static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
   protected static final String SCHEMA_LOCATION = "http://apache.org/xml/properties/schema/external-schemaLocation";
   protected static final String SCHEMA_NONS_LOCATION = "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";
   protected static final String SCHEMA_DV_FACTORY = "http://apache.org/xml/properties/internal/validation/schema/dv-factory";
   protected XMLSchemaValidator fSchemaValidator;

   public StandardParserConfiguration() {
      this((SymbolTable)null, (XMLGrammarPool)null, (XMLComponentManager)null);
   }

   public StandardParserConfiguration(SymbolTable symbolTable) {
      this(symbolTable, (XMLGrammarPool)null, (XMLComponentManager)null);
   }

   public StandardParserConfiguration(SymbolTable symbolTable, XMLGrammarPool grammarPool) {
      this(symbolTable, grammarPool, (XMLComponentManager)null);
   }

   public StandardParserConfiguration(SymbolTable symbolTable, XMLGrammarPool grammarPool, XMLComponentManager parentSettings) {
      super(symbolTable, grammarPool, parentSettings);
      String[] recognizedFeatures = new String[]{"http://apache.org/xml/features/validation/schema/normalized-value", "http://apache.org/xml/features/validation/schema/element-default", "http://apache.org/xml/features/validation/schema/augment-psvi", "http://apache.org/xml/features/generate-synthetic-annotations", "http://apache.org/xml/features/validate-annotations", "http://apache.org/xml/features/honour-all-schemaLocations", "http://apache.org/xml/features/namespace-growth", "http://apache.org/xml/features/internal/tolerate-duplicates", "http://apache.org/xml/features/validation/schema", "http://apache.org/xml/features/validation/schema-full-checking"};
      this.addRecognizedFeatures(recognizedFeatures);
      this.setFeature("http://apache.org/xml/features/validation/schema/element-default", true);
      this.setFeature("http://apache.org/xml/features/validation/schema/normalized-value", true);
      this.setFeature("http://apache.org/xml/features/validation/schema/augment-psvi", true);
      this.setFeature("http://apache.org/xml/features/generate-synthetic-annotations", false);
      this.setFeature("http://apache.org/xml/features/validate-annotations", false);
      this.setFeature("http://apache.org/xml/features/honour-all-schemaLocations", false);
      this.setFeature("http://apache.org/xml/features/namespace-growth", false);
      this.setFeature("http://apache.org/xml/features/internal/tolerate-duplicates", false);
      String[] recognizedProperties = new String[]{"http://apache.org/xml/properties/schema/external-schemaLocation", "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", "http://apache.org/xml/properties/internal/validation/schema/dv-factory"};
      this.addRecognizedProperties(recognizedProperties);
   }

   protected void configurePipeline() {
      super.configurePipeline();
      if (this.getFeature("http://apache.org/xml/features/validation/schema")) {
         if (this.fSchemaValidator == null) {
            this.fSchemaValidator = new XMLSchemaValidator();
            this.fProperties.put("http://apache.org/xml/properties/internal/validator/schema", this.fSchemaValidator);
            this.addComponent(this.fSchemaValidator);
            if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
               XSMessageFormatter xmft = new XSMessageFormatter();
               this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", xmft);
            }
         }

         this.fLastComponent = this.fSchemaValidator;
         this.fNamespaceBinder.setDocumentHandler(this.fSchemaValidator);
         this.fSchemaValidator.setDocumentHandler(this.fDocumentHandler);
         this.fSchemaValidator.setDocumentSource(this.fNamespaceBinder);
      }

   }

   protected FeatureState checkFeature(String featureId) throws XMLConfigurationException {
      if (featureId.startsWith("http://apache.org/xml/features/")) {
         int suffixLength = featureId.length() - "http://apache.org/xml/features/".length();
         if (suffixLength == "validation/schema".length() && featureId.endsWith("validation/schema")) {
            return FeatureState.RECOGNIZED;
         }

         if (suffixLength == "validation/schema-full-checking".length() && featureId.endsWith("validation/schema-full-checking")) {
            return FeatureState.RECOGNIZED;
         }

         if (suffixLength == "validation/schema/normalized-value".length() && featureId.endsWith("validation/schema/normalized-value")) {
            return FeatureState.RECOGNIZED;
         }

         if (suffixLength == "validation/schema/element-default".length() && featureId.endsWith("validation/schema/element-default")) {
            return FeatureState.RECOGNIZED;
         }
      }

      return super.checkFeature(featureId);
   }

   protected PropertyState checkProperty(String propertyId) throws XMLConfigurationException {
      int suffixLength;
      if (propertyId.startsWith("http://apache.org/xml/properties/")) {
         suffixLength = propertyId.length() - "http://apache.org/xml/properties/".length();
         if (suffixLength == "schema/external-schemaLocation".length() && propertyId.endsWith("schema/external-schemaLocation")) {
            return PropertyState.RECOGNIZED;
         }

         if (suffixLength == "schema/external-noNamespaceSchemaLocation".length() && propertyId.endsWith("schema/external-noNamespaceSchemaLocation")) {
            return PropertyState.RECOGNIZED;
         }
      }

      if (propertyId.startsWith("http://java.sun.com/xml/jaxp/properties/")) {
         suffixLength = propertyId.length() - "http://java.sun.com/xml/jaxp/properties/".length();
         if (suffixLength == "schemaSource".length() && propertyId.endsWith("schemaSource")) {
            return PropertyState.RECOGNIZED;
         }
      }

      return super.checkProperty(propertyId);
   }
}
