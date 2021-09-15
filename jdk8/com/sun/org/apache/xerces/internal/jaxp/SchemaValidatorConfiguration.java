package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
import com.sun.org.apache.xerces.internal.jaxp.validation.XSGrammarPoolContainer;
import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.util.PropertyState;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;

final class SchemaValidatorConfiguration implements XMLComponentManager {
   private static final String SCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
   private static final String VALIDATION = "http://xml.org/sax/features/validation";
   private static final String USE_GRAMMAR_POOL_ONLY = "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only";
   private static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
   private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
   private static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
   private static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
   private final XMLComponentManager fParentComponentManager;
   private final XMLGrammarPool fGrammarPool;
   private final boolean fUseGrammarPoolOnly;
   private final ValidationManager fValidationManager;

   public SchemaValidatorConfiguration(XMLComponentManager parentManager, XSGrammarPoolContainer grammarContainer, ValidationManager validationManager) {
      this.fParentComponentManager = parentManager;
      this.fGrammarPool = grammarContainer.getGrammarPool();
      this.fUseGrammarPoolOnly = grammarContainer.isFullyComposed();
      this.fValidationManager = validationManager;

      try {
         XMLErrorReporter errorReporter = (XMLErrorReporter)this.fParentComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
         if (errorReporter != null) {
            errorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", new XSMessageFormatter());
         }
      } catch (XMLConfigurationException var5) {
      }

   }

   public boolean getFeature(String featureId) throws XMLConfigurationException {
      FeatureState state = this.getFeatureState(featureId);
      if (state.isExceptional()) {
         throw new XMLConfigurationException(state.status, featureId);
      } else {
         return state.state;
      }
   }

   public FeatureState getFeatureState(String featureId) {
      if ("http://apache.org/xml/features/internal/parser-settings".equals(featureId)) {
         return this.fParentComponentManager.getFeatureState(featureId);
      } else if (!"http://xml.org/sax/features/validation".equals(featureId) && !"http://apache.org/xml/features/validation/schema".equals(featureId)) {
         return "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only".equals(featureId) ? FeatureState.is(this.fUseGrammarPoolOnly) : this.fParentComponentManager.getFeatureState(featureId);
      } else {
         return FeatureState.is(true);
      }
   }

   public PropertyState getPropertyState(String propertyId) {
      if ("http://apache.org/xml/properties/internal/grammar-pool".equals(propertyId)) {
         return PropertyState.is(this.fGrammarPool);
      } else {
         return "http://apache.org/xml/properties/internal/validation-manager".equals(propertyId) ? PropertyState.is(this.fValidationManager) : this.fParentComponentManager.getPropertyState(propertyId);
      }
   }

   public Object getProperty(String propertyId) throws XMLConfigurationException {
      PropertyState state = this.getPropertyState(propertyId);
      if (state.isExceptional()) {
         throw new XMLConfigurationException(state.status, propertyId);
      } else {
         return state.state;
      }
   }

   public boolean getFeature(String featureId, boolean defaultValue) {
      FeatureState state = this.getFeatureState(featureId);
      return state.isExceptional() ? defaultValue : state.state;
   }

   public Object getProperty(String propertyId, Object defaultValue) {
      PropertyState state = this.getPropertyState(propertyId);
      return state.isExceptional() ? defaultValue : state.state;
   }
}
