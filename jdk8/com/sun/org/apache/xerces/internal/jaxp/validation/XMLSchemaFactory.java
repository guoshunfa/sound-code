package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaLoader;
import com.sun.org.apache.xerces.internal.util.DOMEntityResolverWrapper;
import com.sun.org.apache.xerces.internal.util.DOMInputSource;
import com.sun.org.apache.xerces.internal.util.ErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.util.SAXInputSource;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import com.sun.org.apache.xerces.internal.util.StAXInputSource;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Locale;
import javax.xml.stream.XMLEventReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import jdk.xml.internal.JdkXmlFeatures;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

public final class XMLSchemaFactory extends SchemaFactory {
   private static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
   private static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
   private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
   private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
   private final XMLSchemaLoader fXMLSchemaLoader = new XMLSchemaLoader();
   private ErrorHandler fErrorHandler;
   private LSResourceResolver fLSResourceResolver;
   private final DOMEntityResolverWrapper fDOMEntityResolverWrapper = new DOMEntityResolverWrapper();
   private ErrorHandlerWrapper fErrorHandlerWrapper = new ErrorHandlerWrapper(DraconianErrorHandler.getInstance());
   private XMLSecurityManager fSecurityManager;
   private XMLSecurityPropertyManager fSecurityPropertyMgr;
   private XMLSchemaFactory.XMLGrammarPoolWrapper fXMLGrammarPoolWrapper = new XMLSchemaFactory.XMLGrammarPoolWrapper();
   private final JdkXmlFeatures fXmlFeatures;
   private final boolean fOverrideDefaultParser;

   public XMLSchemaFactory() {
      this.fXMLSchemaLoader.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
      this.fXMLSchemaLoader.setProperty("http://apache.org/xml/properties/internal/grammar-pool", this.fXMLGrammarPoolWrapper);
      this.fXMLSchemaLoader.setEntityResolver(this.fDOMEntityResolverWrapper);
      this.fXMLSchemaLoader.setErrorHandler(this.fErrorHandlerWrapper);
      this.fSecurityManager = new XMLSecurityManager(true);
      this.fXMLSchemaLoader.setProperty("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
      this.fSecurityPropertyMgr = new XMLSecurityPropertyManager();
      this.fXMLSchemaLoader.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.fSecurityPropertyMgr);
      this.fXmlFeatures = new JdkXmlFeatures(this.fSecurityManager.isSecureProcessing());
      this.fOverrideDefaultParser = this.fXmlFeatures.getFeature(JdkXmlFeatures.XmlFeature.JDK_OVERRIDE_PARSER);
      this.fXMLSchemaLoader.setFeature("jdk.xml.overrideDefaultParser", this.fOverrideDefaultParser);
   }

   public boolean isSchemaLanguageSupported(String schemaLanguage) {
      if (schemaLanguage == null) {
         throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "SchemaLanguageNull", (Object[])null));
      } else if (schemaLanguage.length() == 0) {
         throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "SchemaLanguageLengthZero", (Object[])null));
      } else {
         return schemaLanguage.equals("http://www.w3.org/2001/XMLSchema");
      }
   }

   public LSResourceResolver getResourceResolver() {
      return this.fLSResourceResolver;
   }

   public void setResourceResolver(LSResourceResolver resourceResolver) {
      this.fLSResourceResolver = resourceResolver;
      this.fDOMEntityResolverWrapper.setEntityResolver(resourceResolver);
      this.fXMLSchemaLoader.setEntityResolver(this.fDOMEntityResolverWrapper);
   }

   public ErrorHandler getErrorHandler() {
      return this.fErrorHandler;
   }

   public void setErrorHandler(ErrorHandler errorHandler) {
      this.fErrorHandler = errorHandler;
      this.fErrorHandlerWrapper.setErrorHandler((ErrorHandler)(errorHandler != null ? errorHandler : DraconianErrorHandler.getInstance()));
      this.fXMLSchemaLoader.setErrorHandler(this.fErrorHandlerWrapper);
   }

   public Schema newSchema(Source[] schemas) throws SAXException {
      XMLSchemaFactory.XMLGrammarPoolImplExtension pool = new XMLSchemaFactory.XMLGrammarPoolImplExtension();
      this.fXMLGrammarPoolWrapper.setGrammarPool(pool);
      XMLInputSource[] xmlInputSources = new XMLInputSource[schemas.length];

      int i;
      Source source;
      for(i = 0; i < schemas.length; ++i) {
         source = schemas[i];
         String systemID;
         if (source instanceof StreamSource) {
            StreamSource streamSource = (StreamSource)source;
            String publicId = streamSource.getPublicId();
            systemID = streamSource.getSystemId();
            InputStream inputStream = streamSource.getInputStream();
            Reader reader = streamSource.getReader();
            xmlInputSources[i] = new XMLInputSource(publicId, systemID, (String)null);
            xmlInputSources[i].setByteStream(inputStream);
            xmlInputSources[i].setCharacterStream(reader);
         } else if (source instanceof SAXSource) {
            SAXSource saxSource = (SAXSource)source;
            InputSource inputSource = saxSource.getInputSource();
            if (inputSource == null) {
               throw new SAXException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "SAXSourceNullInputSource", (Object[])null));
            }

            xmlInputSources[i] = new SAXInputSource(saxSource.getXMLReader(), inputSource);
         } else if (source instanceof DOMSource) {
            DOMSource domSource = (DOMSource)source;
            Node node = domSource.getNode();
            systemID = domSource.getSystemId();
            xmlInputSources[i] = new DOMInputSource(node, systemID);
         } else {
            if (!(source instanceof StAXSource)) {
               if (source == null) {
                  throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "SchemaSourceArrayMemberNull", (Object[])null));
               }

               throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "SchemaFactorySourceUnrecognized", new Object[]{source.getClass().getName()}));
            }

            StAXSource staxSource = (StAXSource)source;
            XMLEventReader eventReader = staxSource.getXMLEventReader();
            if (eventReader != null) {
               xmlInputSources[i] = new StAXInputSource(eventReader);
            } else {
               xmlInputSources[i] = new StAXInputSource(staxSource.getXMLStreamReader());
            }
         }
      }

      try {
         this.fXMLSchemaLoader.loadGrammar(xmlInputSources);
      } catch (XNIException var11) {
         throw Util.toSAXException(var11);
      } catch (IOException var12) {
         SAXParseException se = new SAXParseException(var12.getMessage(), (Locator)null, var12);
         this.fErrorHandler.error(se);
         throw se;
      }

      this.fXMLGrammarPoolWrapper.setGrammarPool((XMLGrammarPool)null);
      i = pool.getGrammarCount();
      source = null;
      Object schema;
      if (i > 1) {
         schema = new XMLSchema(new ReadOnlyGrammarPool(pool));
      } else if (i == 1) {
         Grammar[] grammars = pool.retrieveInitialGrammarSet("http://www.w3.org/2001/XMLSchema");
         schema = new SimpleXMLSchema(grammars[0]);
      } else {
         schema = new EmptyXMLSchema();
      }

      this.propagateFeatures((AbstractXMLSchema)schema);
      this.propagateProperties((AbstractXMLSchema)schema);
      return (Schema)schema;
   }

   public Schema newSchema() throws SAXException {
      AbstractXMLSchema schema = new WeakReferenceXMLSchema();
      this.propagateFeatures(schema);
      this.propagateProperties(schema);
      return schema;
   }

   public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
      if (name == null) {
         throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "FeatureNameNull", (Object[])null));
      } else if (!name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
         try {
            return this.fXMLSchemaLoader.getFeature(name);
         } catch (XMLConfigurationException var4) {
            String identifier = var4.getIdentifier();
            if (var4.getType() == Status.NOT_RECOGNIZED) {
               throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "feature-not-recognized", new Object[]{identifier}));
            } else {
               throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "feature-not-supported", new Object[]{identifier}));
            }
         }
      } else {
         return this.fSecurityManager != null && this.fSecurityManager.isSecureProcessing();
      }
   }

   public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
      if (name == null) {
         throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "ProperyNameNull", (Object[])null));
      } else if (name.equals("http://apache.org/xml/properties/security-manager")) {
         return this.fSecurityManager;
      } else if (name.equals("http://apache.org/xml/properties/internal/grammar-pool")) {
         throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "property-not-supported", new Object[]{name}));
      } else {
         int index = this.fXmlFeatures.getIndex(name);
         if (index > -1) {
            return this.fXmlFeatures.getFeature(index);
         } else {
            try {
               return this.fXMLSchemaLoader.getProperty(name);
            } catch (XMLConfigurationException var5) {
               String identifier = var5.getIdentifier();
               if (var5.getType() == Status.NOT_RECOGNIZED) {
                  throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "property-not-recognized", new Object[]{identifier}));
               } else {
                  throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "property-not-supported", new Object[]{identifier}));
               }
            }
         }
      }
   }

   public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
      if (name == null) {
         throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "FeatureNameNull", (Object[])null));
      } else if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
         if (System.getSecurityManager() != null && !value) {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage((Locale)null, "jaxp-secureprocessing-feature", (Object[])null));
         } else {
            this.fSecurityManager.setSecureProcessing(value);
            if (value && Constants.IS_JDK8_OR_ABOVE) {
               this.fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD, XMLSecurityPropertyManager.State.FSP, "");
               this.fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_SCHEMA, XMLSecurityPropertyManager.State.FSP, "");
            }

            this.fXMLSchemaLoader.setProperty("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
         }
      } else if (!name.equals("http://www.oracle.com/feature/use-service-mechanism") || System.getSecurityManager() == null) {
         if (this.fXmlFeatures != null && this.fXmlFeatures.setFeature(name, JdkXmlFeatures.State.APIPROPERTY, value)) {
            if (name.equals("jdk.xml.overrideDefaultParser") || name.equals("http://www.oracle.com/feature/use-service-mechanism")) {
               this.fXMLSchemaLoader.setFeature(name, value);
            }

         } else {
            try {
               this.fXMLSchemaLoader.setFeature(name, value);
            } catch (XMLConfigurationException var5) {
               String identifier = var5.getIdentifier();
               if (var5.getType() == Status.NOT_RECOGNIZED) {
                  throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "feature-not-recognized", new Object[]{identifier}));
               } else {
                  throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "feature-not-supported", new Object[]{identifier}));
               }
            }
         }
      }
   }

   public void setProperty(String name, Object object) throws SAXNotRecognizedException, SAXNotSupportedException {
      if (name == null) {
         throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "ProperyNameNull", (Object[])null));
      } else if (name.equals("http://apache.org/xml/properties/security-manager")) {
         this.fSecurityManager = XMLSecurityManager.convert(object, this.fSecurityManager);
         this.fXMLSchemaLoader.setProperty("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
      } else if (name.equals("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager")) {
         if (object == null) {
            this.fSecurityPropertyMgr = new XMLSecurityPropertyManager();
         } else {
            this.fSecurityPropertyMgr = (XMLSecurityPropertyManager)object;
         }

         this.fXMLSchemaLoader.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.fSecurityPropertyMgr);
      } else if (name.equals("http://apache.org/xml/properties/internal/grammar-pool")) {
         throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "property-not-supported", new Object[]{name}));
      } else {
         try {
            if ((this.fSecurityManager == null || !this.fSecurityManager.setLimit(name, XMLSecurityManager.State.APIPROPERTY, object)) && (this.fSecurityPropertyMgr == null || !this.fSecurityPropertyMgr.setValue(name, XMLSecurityPropertyManager.State.APIPROPERTY, object))) {
               this.fXMLSchemaLoader.setProperty(name, object);
            }

         } catch (XMLConfigurationException var5) {
            String identifier = var5.getIdentifier();
            if (var5.getType() == Status.NOT_RECOGNIZED) {
               throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "property-not-recognized", new Object[]{identifier}));
            } else {
               throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "property-not-supported", new Object[]{identifier}));
            }
         }
      }
   }

   private void propagateFeatures(AbstractXMLSchema schema) {
      schema.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", this.fSecurityManager != null && this.fSecurityManager.isSecureProcessing());
      schema.setFeature("jdk.xml.overrideDefaultParser", this.fOverrideDefaultParser);
      String[] features = this.fXMLSchemaLoader.getRecognizedFeatures();

      for(int i = 0; i < features.length; ++i) {
         boolean state = this.fXMLSchemaLoader.getFeature(features[i]);
         schema.setFeature(features[i], state);
      }

   }

   private void propagateProperties(AbstractXMLSchema schema) {
      String[] properties = this.fXMLSchemaLoader.getRecognizedProperties();

      for(int i = 0; i < properties.length; ++i) {
         Object state = this.fXMLSchemaLoader.getProperty(properties[i]);
         schema.setProperty(properties[i], state);
      }

   }

   static class XMLGrammarPoolWrapper implements XMLGrammarPool {
      private XMLGrammarPool fGrammarPool;

      public Grammar[] retrieveInitialGrammarSet(String grammarType) {
         return this.fGrammarPool.retrieveInitialGrammarSet(grammarType);
      }

      public void cacheGrammars(String grammarType, Grammar[] grammars) {
         this.fGrammarPool.cacheGrammars(grammarType, grammars);
      }

      public Grammar retrieveGrammar(XMLGrammarDescription desc) {
         return this.fGrammarPool.retrieveGrammar(desc);
      }

      public void lockPool() {
         this.fGrammarPool.lockPool();
      }

      public void unlockPool() {
         this.fGrammarPool.unlockPool();
      }

      public void clear() {
         this.fGrammarPool.clear();
      }

      void setGrammarPool(XMLGrammarPool grammarPool) {
         this.fGrammarPool = grammarPool;
      }

      XMLGrammarPool getGrammarPool() {
         return this.fGrammarPool;
      }
   }

   static class XMLGrammarPoolImplExtension extends XMLGrammarPoolImpl {
      public XMLGrammarPoolImplExtension() {
      }

      public XMLGrammarPoolImplExtension(int initialCapacity) {
         super(initialCapacity);
      }

      int getGrammarCount() {
         return this.fGrammarCount;
      }
   }
}
