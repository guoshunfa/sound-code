package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.parsers.XML11Configuration;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import java.io.IOException;
import java.lang.ref.SoftReference;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import jdk.xml.internal.JdkXmlUtils;
import org.xml.sax.SAXException;

final class StreamValidatorHelper implements ValidatorHelper {
   private static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
   private static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
   private static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
   private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
   private static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
   private static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
   private static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
   private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
   private SoftReference fConfiguration = new SoftReference((Object)null);
   private XMLSchemaValidator fSchemaValidator;
   private XMLSchemaValidatorComponentManager fComponentManager;
   private ValidatorHandlerImpl handler = null;

   public StreamValidatorHelper(XMLSchemaValidatorComponentManager componentManager) {
      this.fComponentManager = componentManager;
      this.fSchemaValidator = (XMLSchemaValidator)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validator/schema");
   }

   public void validate(Source source, Result result) throws SAXException, IOException {
      if (result != null && !(result instanceof StreamResult)) {
         throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceResultMismatch", new Object[]{source.getClass().getName(), result.getClass().getName()}));
      } else {
         StreamSource streamSource = (StreamSource)source;
         if (result != null) {
            TransformerHandler identityTransformerHandler;
            try {
               SAXTransformerFactory tf = JdkXmlUtils.getSAXTransformFactory(this.fComponentManager.getFeature("jdk.xml.overrideDefaultParser"));
               identityTransformerHandler = tf.newTransformerHandler();
            } catch (TransformerConfigurationException var10) {
               throw new TransformerFactoryConfigurationError(var10);
            }

            this.handler = new ValidatorHandlerImpl(this.fComponentManager);
            this.handler.setContentHandler(identityTransformerHandler);
            identityTransformerHandler.setResult(result);
         }

         XMLInputSource input = new XMLInputSource(streamSource.getPublicId(), streamSource.getSystemId(), (String)null);
         input.setByteStream(streamSource.getInputStream());
         input.setCharacterStream(streamSource.getReader());
         XMLParserConfiguration config = (XMLParserConfiguration)this.fConfiguration.get();
         if (config == null) {
            config = this.initialize();
         } else if (this.fComponentManager.getFeature("http://apache.org/xml/features/internal/parser-settings")) {
            config.setProperty("http://apache.org/xml/properties/internal/entity-resolver", this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver"));
            config.setProperty("http://apache.org/xml/properties/internal/error-handler", this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/error-handler"));
         }

         this.fComponentManager.reset();
         this.fSchemaValidator.setDocumentHandler(this.handler);

         try {
            config.parse(input);
         } catch (XMLParseException var8) {
            throw Util.toSAXParseException(var8);
         } catch (XNIException var9) {
            throw Util.toSAXException(var9);
         }
      }
   }

   private XMLParserConfiguration initialize() {
      XML11Configuration config = new XML11Configuration();
      if (this.fComponentManager.getFeature("http://javax.xml.XMLConstants/feature/secure-processing")) {
         config.setProperty("http://apache.org/xml/properties/security-manager", new XMLSecurityManager());
      }

      config.setProperty("http://apache.org/xml/properties/internal/entity-resolver", this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver"));
      config.setProperty("http://apache.org/xml/properties/internal/error-handler", this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/error-handler"));
      XMLErrorReporter errorReporter = (XMLErrorReporter)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
      config.setProperty("http://apache.org/xml/properties/internal/error-reporter", errorReporter);
      if (errorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null) {
         XMLMessageFormatter xmft = new XMLMessageFormatter();
         errorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", xmft);
         errorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", xmft);
      }

      config.setProperty("http://apache.org/xml/properties/internal/symbol-table", this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table"));
      config.setProperty("http://apache.org/xml/properties/internal/validation-manager", this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager"));
      config.setDocumentHandler(this.fSchemaValidator);
      config.setDTDHandler((XMLDTDHandler)null);
      config.setDTDContentModelHandler((XMLDTDContentModelHandler)null);
      config.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.fComponentManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager"));
      config.setProperty("http://apache.org/xml/properties/security-manager", this.fComponentManager.getProperty("http://apache.org/xml/properties/security-manager"));
      this.fConfiguration = new SoftReference(config);
      return config;
   }
}
