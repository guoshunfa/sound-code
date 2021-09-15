package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import java.io.IOException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stax.StAXResult;
import jdk.xml.internal.JdkXmlUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public final class StAXValidatorHelper implements ValidatorHelper {
   private XMLSchemaValidatorComponentManager fComponentManager;
   private Transformer identityTransformer1 = null;
   private TransformerHandler identityTransformer2 = null;
   private ValidatorHandlerImpl handler = null;

   public StAXValidatorHelper(XMLSchemaValidatorComponentManager componentManager) {
      this.fComponentManager = componentManager;
   }

   public void validate(Source source, Result result) throws SAXException, IOException {
      if (result != null && !(result instanceof StAXResult)) {
         throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceResultMismatch", new Object[]{source.getClass().getName(), result.getClass().getName()}));
      } else {
         if (this.identityTransformer1 == null) {
            try {
               SAXTransformerFactory tf = JdkXmlUtils.getSAXTransformFactory(this.fComponentManager.getFeature("jdk.xml.overrideDefaultParser"));
               XMLSecurityManager securityManager = (XMLSecurityManager)this.fComponentManager.getProperty("http://apache.org/xml/properties/security-manager");
               if (securityManager != null) {
                  XMLSecurityManager.Limit[] var5 = XMLSecurityManager.Limit.values();
                  int var6 = var5.length;

                  for(int var7 = 0; var7 < var6; ++var7) {
                     XMLSecurityManager.Limit limit = var5[var7];
                     if (securityManager.isSet(limit.ordinal())) {
                        tf.setAttribute(limit.apiProperty(), securityManager.getLimitValueAsString(limit));
                     }
                  }

                  if (securityManager.printEntityCountInfo()) {
                     tf.setAttribute("http://www.oracle.com/xml/jaxp/properties/getEntityCountInfo", "yes");
                  }
               }

               this.identityTransformer1 = tf.newTransformer();
               this.identityTransformer2 = tf.newTransformerHandler();
            } catch (TransformerConfigurationException var15) {
               throw new TransformerFactoryConfigurationError(var15);
            }
         }

         this.handler = new ValidatorHandlerImpl(this.fComponentManager);
         if (result != null) {
            this.handler.setContentHandler(this.identityTransformer2);
            this.identityTransformer2.setResult(result);
         }

         try {
            this.identityTransformer1.transform(source, new SAXResult(this.handler));
         } catch (TransformerException var13) {
            if (var13.getException() instanceof SAXException) {
               throw (SAXException)var13.getException();
            }

            throw new SAXException(var13);
         } finally {
            this.handler.setContentHandler((ContentHandler)null);
         }

      }
   }
}
