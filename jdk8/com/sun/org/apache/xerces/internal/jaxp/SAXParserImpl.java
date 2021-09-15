package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.jaxp.validation.XSGrammarPoolContainer;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xs.PSVIProvider;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import javax.xml.parsers.SAXParser;
import javax.xml.validation.Schema;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class SAXParserImpl extends SAXParser implements JAXPConstants, PSVIProvider {
   private static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces";
   private static final String NAMESPACE_PREFIXES_FEATURE = "http://xml.org/sax/features/namespace-prefixes";
   private static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation";
   private static final String XMLSCHEMA_VALIDATION_FEATURE = "http://apache.org/xml/features/validation/schema";
   private static final String XINCLUDE_FEATURE = "http://apache.org/xml/features/xinclude";
   private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
   private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
   private final SAXParserImpl.JAXPSAXParser xmlReader;
   private String schemaLanguage;
   private final Schema grammar;
   private final XMLComponent fSchemaValidator;
   private final XMLComponentManager fSchemaValidatorComponentManager;
   private final ValidationManager fSchemaValidationManager;
   private final UnparsedEntityHandler fUnparsedEntityHandler;
   private final ErrorHandler fInitErrorHandler;
   private final EntityResolver fInitEntityResolver;
   private final XMLSecurityManager fSecurityManager;
   private final XMLSecurityPropertyManager fSecurityPropertyMgr;

   SAXParserImpl(SAXParserFactoryImpl spf, Map<String, Boolean> features) throws SAXException {
      this(spf, features, false);
   }

   SAXParserImpl(SAXParserFactoryImpl spf, Map<String, Boolean> features, boolean secureProcessing) throws SAXException {
      this.schemaLanguage = null;
      this.fSecurityManager = new XMLSecurityManager(secureProcessing);
      this.fSecurityPropertyMgr = new XMLSecurityPropertyManager();
      this.xmlReader = new SAXParserImpl.JAXPSAXParser(this, this.fSecurityPropertyMgr, this.fSecurityManager);
      this.xmlReader.setFeature0("http://xml.org/sax/features/namespaces", spf.isNamespaceAware());
      this.xmlReader.setFeature0("http://xml.org/sax/features/namespace-prefixes", !spf.isNamespaceAware());
      if (spf.isXIncludeAware()) {
         this.xmlReader.setFeature0("http://apache.org/xml/features/xinclude", true);
      }

      this.xmlReader.setProperty0("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.fSecurityPropertyMgr);
      this.xmlReader.setProperty0("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
      if (secureProcessing && features != null) {
         Boolean temp = (Boolean)features.get("http://javax.xml.XMLConstants/feature/secure-processing");
         if (temp != null && temp && Constants.IS_JDK8_OR_ABOVE) {
            this.fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD, XMLSecurityPropertyManager.State.FSP, "");
            this.fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_SCHEMA, XMLSecurityPropertyManager.State.FSP, "");
         }
      }

      this.setFeatures(features);
      if (spf.isValidating()) {
         this.fInitErrorHandler = new DefaultValidationErrorHandler(this.xmlReader.getLocale());
         this.xmlReader.setErrorHandler(this.fInitErrorHandler);
      } else {
         this.fInitErrorHandler = this.xmlReader.getErrorHandler();
      }

      this.xmlReader.setFeature0("http://xml.org/sax/features/validation", spf.isValidating());
      this.grammar = spf.getSchema();
      if (this.grammar != null) {
         XMLParserConfiguration config = this.xmlReader.getXMLParserConfiguration();
         XMLComponent validatorComponent = null;
         if (this.grammar instanceof XSGrammarPoolContainer) {
            validatorComponent = new XMLSchemaValidator();
            this.fSchemaValidationManager = new ValidationManager();
            this.fUnparsedEntityHandler = new UnparsedEntityHandler(this.fSchemaValidationManager);
            config.setDTDHandler(this.fUnparsedEntityHandler);
            this.fUnparsedEntityHandler.setDTDHandler(this.xmlReader);
            this.xmlReader.setDTDSource(this.fUnparsedEntityHandler);
            this.fSchemaValidatorComponentManager = new SchemaValidatorConfiguration(config, (XSGrammarPoolContainer)this.grammar, this.fSchemaValidationManager);
         } else {
            validatorComponent = new JAXPValidatorComponent(this.grammar.newValidatorHandler());
            this.fSchemaValidationManager = null;
            this.fUnparsedEntityHandler = null;
            this.fSchemaValidatorComponentManager = config;
         }

         config.addRecognizedFeatures(((XMLComponent)validatorComponent).getRecognizedFeatures());
         config.addRecognizedProperties(((XMLComponent)validatorComponent).getRecognizedProperties());
         config.setDocumentHandler((XMLDocumentHandler)validatorComponent);
         ((XMLDocumentSource)validatorComponent).setDocumentHandler(this.xmlReader);
         this.xmlReader.setDocumentSource((XMLDocumentSource)validatorComponent);
         this.fSchemaValidator = (XMLComponent)validatorComponent;
      } else {
         this.fSchemaValidationManager = null;
         this.fUnparsedEntityHandler = null;
         this.fSchemaValidatorComponentManager = null;
         this.fSchemaValidator = null;
      }

      this.fInitEntityResolver = this.xmlReader.getEntityResolver();
   }

   private void setFeatures(Map<String, Boolean> features) throws SAXNotSupportedException, SAXNotRecognizedException {
      if (features != null) {
         Iterator var2 = features.entrySet().iterator();

         while(var2.hasNext()) {
            Map.Entry<String, Boolean> entry = (Map.Entry)var2.next();
            this.xmlReader.setFeature0((String)entry.getKey(), (Boolean)entry.getValue());
         }
      }

   }

   public Parser getParser() throws SAXException {
      return this.xmlReader;
   }

   public XMLReader getXMLReader() {
      return this.xmlReader;
   }

   public boolean isNamespaceAware() {
      try {
         return this.xmlReader.getFeature("http://xml.org/sax/features/namespaces");
      } catch (SAXException var2) {
         throw new IllegalStateException(var2.getMessage());
      }
   }

   public boolean isValidating() {
      try {
         return this.xmlReader.getFeature("http://xml.org/sax/features/validation");
      } catch (SAXException var2) {
         throw new IllegalStateException(var2.getMessage());
      }
   }

   public boolean isXIncludeAware() {
      try {
         return this.xmlReader.getFeature("http://apache.org/xml/features/xinclude");
      } catch (SAXException var2) {
         return false;
      }
   }

   public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
      this.xmlReader.setProperty(name, value);
   }

   public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
      return this.xmlReader.getProperty(name);
   }

   public void parse(InputSource is, DefaultHandler dh) throws SAXException, IOException {
      if (is == null) {
         throw new IllegalArgumentException();
      } else {
         if (dh != null) {
            this.xmlReader.setContentHandler(dh);
            this.xmlReader.setEntityResolver(dh);
            this.xmlReader.setErrorHandler(dh);
            this.xmlReader.setDTDHandler(dh);
            this.xmlReader.setDocumentHandler((DocumentHandler)null);
         }

         this.xmlReader.parse(is);
      }
   }

   public void parse(InputSource is, HandlerBase hb) throws SAXException, IOException {
      if (is == null) {
         throw new IllegalArgumentException();
      } else {
         if (hb != null) {
            this.xmlReader.setDocumentHandler(hb);
            this.xmlReader.setEntityResolver(hb);
            this.xmlReader.setErrorHandler(hb);
            this.xmlReader.setDTDHandler(hb);
            this.xmlReader.setContentHandler((ContentHandler)null);
         }

         this.xmlReader.parse(is);
      }
   }

   public Schema getSchema() {
      return this.grammar;
   }

   public void reset() {
      try {
         this.xmlReader.restoreInitState();
      } catch (SAXException var2) {
      }

      this.xmlReader.setContentHandler((ContentHandler)null);
      this.xmlReader.setDTDHandler((DTDHandler)null);
      if (this.xmlReader.getErrorHandler() != this.fInitErrorHandler) {
         this.xmlReader.setErrorHandler(this.fInitErrorHandler);
      }

      if (this.xmlReader.getEntityResolver() != this.fInitEntityResolver) {
         this.xmlReader.setEntityResolver(this.fInitEntityResolver);
      }

   }

   public ElementPSVI getElementPSVI() {
      return this.xmlReader.getElementPSVI();
   }

   public AttributePSVI getAttributePSVI(int index) {
      return this.xmlReader.getAttributePSVI(index);
   }

   public AttributePSVI getAttributePSVIByName(String uri, String localname) {
      return this.xmlReader.getAttributePSVIByName(uri, localname);
   }

   public static class JAXPSAXParser extends com.sun.org.apache.xerces.internal.parsers.SAXParser {
      private final HashMap fInitFeatures;
      private final HashMap fInitProperties;
      private final SAXParserImpl fSAXParser;
      private XMLSecurityManager fSecurityManager;
      private XMLSecurityPropertyManager fSecurityPropertyMgr;

      public JAXPSAXParser() {
         this((SAXParserImpl)null, (XMLSecurityPropertyManager)null, (XMLSecurityManager)null);
      }

      JAXPSAXParser(SAXParserImpl saxParser, XMLSecurityPropertyManager securityPropertyMgr, XMLSecurityManager securityManager) {
         this.fInitFeatures = new HashMap();
         this.fInitProperties = new HashMap();
         this.fSAXParser = saxParser;
         this.fSecurityManager = securityManager;
         this.fSecurityPropertyMgr = securityPropertyMgr;
         if (this.fSecurityManager == null) {
            this.fSecurityManager = new XMLSecurityManager(true);

            try {
               super.setProperty("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
            } catch (SAXException var6) {
               throw new UnsupportedOperationException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-recognized", new Object[]{"http://apache.org/xml/properties/security-manager"}), var6);
            }
         }

         if (this.fSecurityPropertyMgr == null) {
            this.fSecurityPropertyMgr = new XMLSecurityPropertyManager();

            try {
               super.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.fSecurityPropertyMgr);
            } catch (SAXException var5) {
               throw new UnsupportedOperationException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-recognized", new Object[]{"http://apache.org/xml/properties/security-manager"}), var5);
            }
         }

      }

      public synchronized void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
         if (name == null) {
            throw new NullPointerException();
         } else if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
            try {
               this.fSecurityManager.setSecureProcessing(value);
               this.setProperty("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
            } catch (SAXNotRecognizedException var4) {
               if (value) {
                  throw var4;
               }
            } catch (SAXNotSupportedException var5) {
               if (value) {
                  throw var5;
               }
            }

         } else {
            if (!this.fInitFeatures.containsKey(name)) {
               boolean current = super.getFeature(name);
               this.fInitFeatures.put(name, current ? Boolean.TRUE : Boolean.FALSE);
            }

            if (this.fSAXParser != null && this.fSAXParser.fSchemaValidator != null) {
               this.setSchemaValidatorFeature(name, value);
            }

            super.setFeature(name, value);
         }
      }

      public synchronized boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
         if (name == null) {
            throw new NullPointerException();
         } else {
            return name.equals("http://javax.xml.XMLConstants/feature/secure-processing") ? this.fSecurityManager.isSecureProcessing() : super.getFeature(name);
         }
      }

      public synchronized void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
         if (name == null) {
            throw new NullPointerException();
         } else {
            if (this.fSAXParser != null) {
               if ("http://java.sun.com/xml/jaxp/properties/schemaLanguage".equals(name)) {
                  if (this.fSAXParser.grammar != null) {
                     throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "schema-already-specified", new Object[]{name}));
                  }

                  if ("http://www.w3.org/2001/XMLSchema".equals(value)) {
                     if (this.fSAXParser.isValidating()) {
                        this.fSAXParser.schemaLanguage = "http://www.w3.org/2001/XMLSchema";
                        this.setFeature("http://apache.org/xml/features/validation/schema", true);
                        if (!this.fInitProperties.containsKey("http://java.sun.com/xml/jaxp/properties/schemaLanguage")) {
                           this.fInitProperties.put("http://java.sun.com/xml/jaxp/properties/schemaLanguage", super.getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage"));
                        }

                        super.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
                     }
                  } else {
                     if (value != null) {
                        throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "schema-not-supported", (Object[])null));
                     }

                     this.fSAXParser.schemaLanguage = null;
                     this.setFeature("http://apache.org/xml/features/validation/schema", false);
                  }

                  return;
               }

               if ("http://java.sun.com/xml/jaxp/properties/schemaSource".equals(name)) {
                  if (this.fSAXParser.grammar != null) {
                     throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "schema-already-specified", new Object[]{name}));
                  }

                  String val = (String)this.getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage");
                  if (val != null && "http://www.w3.org/2001/XMLSchema".equals(val)) {
                     if (!this.fInitProperties.containsKey("http://java.sun.com/xml/jaxp/properties/schemaSource")) {
                        this.fInitProperties.put("http://java.sun.com/xml/jaxp/properties/schemaSource", super.getProperty("http://java.sun.com/xml/jaxp/properties/schemaSource"));
                     }

                     super.setProperty(name, value);
                     return;
                  }

                  throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "jaxp-order-not-supported", new Object[]{"http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://java.sun.com/xml/jaxp/properties/schemaSource"}));
               }
            }

            if (this.fSAXParser != null && this.fSAXParser.fSchemaValidator != null) {
               this.setSchemaValidatorProperty(name, value);
            }

            if ((this.fSecurityManager == null || !this.fSecurityManager.setLimit(name, XMLSecurityManager.State.APIPROPERTY, value)) && (this.fSecurityPropertyMgr == null || !this.fSecurityPropertyMgr.setValue(name, XMLSecurityPropertyManager.State.APIPROPERTY, value))) {
               if (!this.fInitProperties.containsKey(name)) {
                  this.fInitProperties.put(name, super.getProperty(name));
               }

               super.setProperty(name, value);
            }

         }
      }

      public synchronized Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
         if (name == null) {
            throw new NullPointerException();
         } else if (this.fSAXParser != null && "http://java.sun.com/xml/jaxp/properties/schemaLanguage".equals(name)) {
            return this.fSAXParser.schemaLanguage;
         } else {
            String propertyValue = this.fSecurityManager != null ? this.fSecurityManager.getLimitAsString(name) : null;
            if (propertyValue != null) {
               return propertyValue;
            } else {
               propertyValue = this.fSecurityPropertyMgr != null ? this.fSecurityPropertyMgr.getValue(name) : null;
               return propertyValue != null ? propertyValue : super.getProperty(name);
            }
         }
      }

      synchronized void restoreInitState() throws SAXNotRecognizedException, SAXNotSupportedException {
         Iterator iter;
         Map.Entry entry;
         String name;
         if (!this.fInitFeatures.isEmpty()) {
            iter = this.fInitFeatures.entrySet().iterator();

            while(iter.hasNext()) {
               entry = (Map.Entry)iter.next();
               name = (String)entry.getKey();
               boolean value = (Boolean)entry.getValue();
               super.setFeature(name, value);
            }

            this.fInitFeatures.clear();
         }

         if (!this.fInitProperties.isEmpty()) {
            iter = this.fInitProperties.entrySet().iterator();

            while(iter.hasNext()) {
               entry = (Map.Entry)iter.next();
               name = (String)entry.getKey();
               Object value = entry.getValue();
               super.setProperty(name, value);
            }

            this.fInitProperties.clear();
         }

      }

      public void parse(InputSource inputSource) throws SAXException, IOException {
         if (this.fSAXParser != null && this.fSAXParser.fSchemaValidator != null) {
            if (this.fSAXParser.fSchemaValidationManager != null) {
               this.fSAXParser.fSchemaValidationManager.reset();
               this.fSAXParser.fUnparsedEntityHandler.reset();
            }

            this.resetSchemaValidator();
         }

         super.parse(inputSource);
      }

      public void parse(String systemId) throws SAXException, IOException {
         if (this.fSAXParser != null && this.fSAXParser.fSchemaValidator != null) {
            if (this.fSAXParser.fSchemaValidationManager != null) {
               this.fSAXParser.fSchemaValidationManager.reset();
               this.fSAXParser.fUnparsedEntityHandler.reset();
            }

            this.resetSchemaValidator();
         }

         super.parse(systemId);
      }

      XMLParserConfiguration getXMLParserConfiguration() {
         return this.fConfiguration;
      }

      void setFeature0(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
         super.setFeature(name, value);
      }

      boolean getFeature0(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
         return super.getFeature(name);
      }

      void setProperty0(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
         super.setProperty(name, value);
      }

      Object getProperty0(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
         return super.getProperty(name);
      }

      Locale getLocale() {
         return this.fConfiguration.getLocale();
      }

      private void setSchemaValidatorFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
         try {
            this.fSAXParser.fSchemaValidator.setFeature(name, value);
         } catch (XMLConfigurationException var5) {
            String identifier = var5.getIdentifier();
            if (var5.getType() == Status.NOT_RECOGNIZED) {
               throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-recognized", new Object[]{identifier}));
            } else {
               throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-supported", new Object[]{identifier}));
            }
         }
      }

      private void setSchemaValidatorProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
         try {
            this.fSAXParser.fSchemaValidator.setProperty(name, value);
         } catch (XMLConfigurationException var5) {
            String identifier = var5.getIdentifier();
            if (var5.getType() == Status.NOT_RECOGNIZED) {
               throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-recognized", new Object[]{identifier}));
            } else {
               throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-supported", new Object[]{identifier}));
            }
         }
      }

      private void resetSchemaValidator() throws SAXException {
         try {
            this.fSAXParser.fSchemaValidator.reset(this.fSAXParser.fSchemaValidatorComponentManager);
         } catch (XMLConfigurationException var2) {
            throw new SAXException(var2);
         }
      }
   }
}
