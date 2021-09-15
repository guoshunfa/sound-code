package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
import com.sun.org.apache.xerces.internal.util.PropertyState;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public abstract class BasicParserConfiguration extends ParserConfigurationSettings implements XMLParserConfiguration {
   protected static final String VALIDATION = "http://xml.org/sax/features/validation";
   protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
   protected static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
   protected static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
   protected static final String XML_STRING = "http://xml.org/sax/properties/xml-string";
   protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
   protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
   protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
   protected SymbolTable fSymbolTable;
   protected Locale fLocale;
   protected ArrayList fComponents;
   protected XMLDocumentHandler fDocumentHandler;
   protected XMLDTDHandler fDTDHandler;
   protected XMLDTDContentModelHandler fDTDContentModelHandler;
   protected XMLDocumentSource fLastComponent;

   protected BasicParserConfiguration() {
      this((SymbolTable)null, (XMLComponentManager)null);
   }

   protected BasicParserConfiguration(SymbolTable symbolTable) {
      this(symbolTable, (XMLComponentManager)null);
   }

   protected BasicParserConfiguration(SymbolTable symbolTable, XMLComponentManager parentSettings) {
      super(parentSettings);
      this.fComponents = new ArrayList();
      this.fFeatures = new HashMap();
      this.fProperties = new HashMap();
      String[] recognizedFeatures = new String[]{"http://apache.org/xml/features/internal/parser-settings", "http://xml.org/sax/features/validation", "http://xml.org/sax/features/namespaces", "http://xml.org/sax/features/external-general-entities", "http://xml.org/sax/features/external-parameter-entities"};
      this.addRecognizedFeatures(recognizedFeatures);
      this.fFeatures.put("http://apache.org/xml/features/internal/parser-settings", Boolean.TRUE);
      this.fFeatures.put("http://xml.org/sax/features/validation", Boolean.FALSE);
      this.fFeatures.put("http://xml.org/sax/features/namespaces", Boolean.TRUE);
      this.fFeatures.put("http://xml.org/sax/features/external-general-entities", Boolean.TRUE);
      this.fFeatures.put("http://xml.org/sax/features/external-parameter-entities", Boolean.TRUE);
      String[] recognizedProperties = new String[]{"http://xml.org/sax/properties/xml-string", "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/internal/entity-resolver"};
      this.addRecognizedProperties(recognizedProperties);
      if (symbolTable == null) {
         symbolTable = new SymbolTable();
      }

      this.fSymbolTable = symbolTable;
      this.fProperties.put("http://apache.org/xml/properties/internal/symbol-table", this.fSymbolTable);
   }

   protected void addComponent(XMLComponent component) {
      if (!this.fComponents.contains(component)) {
         this.fComponents.add(component);
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
               if (state != null) {
                  super.setFeature(propertyId, state);
               }
            }
         }

         if (recognizedProperties != null) {
            for(i = 0; i < recognizedProperties.length; ++i) {
               propertyId = recognizedProperties[i];
               Object value = component.getPropertyDefault(propertyId);
               if (value != null) {
                  super.setProperty(propertyId, value);
               }
            }
         }

      }
   }

   public abstract void parse(XMLInputSource var1) throws XNIException, IOException;

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

   public void setFeature(String featureId, boolean state) throws XMLConfigurationException {
      int count = this.fComponents.size();

      for(int i = 0; i < count; ++i) {
         XMLComponent c = (XMLComponent)this.fComponents.get(i);
         c.setFeature(featureId, state);
      }

      super.setFeature(featureId, state);
   }

   public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
      int count = this.fComponents.size();

      for(int i = 0; i < count; ++i) {
         XMLComponent c = (XMLComponent)this.fComponents.get(i);
         c.setProperty(propertyId, value);
      }

      super.setProperty(propertyId, value);
   }

   public void setLocale(Locale locale) throws XNIException {
      this.fLocale = locale;
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

   protected PropertyState checkProperty(String propertyId) throws XMLConfigurationException {
      if (propertyId.startsWith("http://xml.org/sax/properties/")) {
         int suffixLength = propertyId.length() - "http://xml.org/sax/properties/".length();
         if (suffixLength == "xml-string".length() && propertyId.endsWith("xml-string")) {
            return PropertyState.NOT_SUPPORTED;
         }
      }

      return super.checkProperty(propertyId);
   }

   protected FeatureState checkFeature(String featureId) throws XMLConfigurationException {
      if (featureId.startsWith("http://apache.org/xml/features/")) {
         int suffixLength = featureId.length() - "http://apache.org/xml/features/".length();
         if (suffixLength == "internal/parser-settings".length() && featureId.endsWith("internal/parser-settings")) {
            return FeatureState.NOT_SUPPORTED;
         }
      }

      return super.checkFeature(featureId);
   }
}
