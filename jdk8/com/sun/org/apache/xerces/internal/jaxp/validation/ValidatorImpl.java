package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xs.PSVIProvider;
import java.io.IOException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

final class ValidatorImpl extends Validator implements PSVIProvider {
   private XMLSchemaValidatorComponentManager fComponentManager;
   private ValidatorHandlerImpl fSAXValidatorHelper;
   private DOMValidatorHelper fDOMValidatorHelper;
   private StreamValidatorHelper fStreamValidatorHelper;
   private StAXValidatorHelper fStaxValidatorHelper;
   private boolean fConfigurationChanged = false;
   private boolean fErrorHandlerChanged = false;
   private boolean fResourceResolverChanged = false;
   private static final String CURRENT_ELEMENT_NODE = "http://apache.org/xml/properties/dom/current-element-node";

   public ValidatorImpl(XSGrammarPoolContainer grammarContainer) {
      this.fComponentManager = new XMLSchemaValidatorComponentManager(grammarContainer);
      this.setErrorHandler((ErrorHandler)null);
      this.setResourceResolver((LSResourceResolver)null);
   }

   public void validate(Source source, Result result) throws SAXException, IOException {
      if (source instanceof SAXSource) {
         if (this.fSAXValidatorHelper == null) {
            this.fSAXValidatorHelper = new ValidatorHandlerImpl(this.fComponentManager);
         }

         this.fSAXValidatorHelper.validate(source, result);
      } else if (source instanceof DOMSource) {
         if (this.fDOMValidatorHelper == null) {
            this.fDOMValidatorHelper = new DOMValidatorHelper(this.fComponentManager);
         }

         this.fDOMValidatorHelper.validate(source, result);
      } else if (source instanceof StreamSource) {
         if (this.fStreamValidatorHelper == null) {
            this.fStreamValidatorHelper = new StreamValidatorHelper(this.fComponentManager);
         }

         this.fStreamValidatorHelper.validate(source, result);
      } else {
         if (!(source instanceof StAXSource)) {
            if (source == null) {
               throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceParameterNull", (Object[])null));
            }

            throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceNotAccepted", new Object[]{source.getClass().getName()}));
         }

         if (this.fStaxValidatorHelper == null) {
            this.fStaxValidatorHelper = new StAXValidatorHelper(this.fComponentManager);
         }

         this.fStaxValidatorHelper.validate(source, result);
      }

   }

   public void setErrorHandler(ErrorHandler errorHandler) {
      this.fErrorHandlerChanged = errorHandler != null;
      this.fComponentManager.setErrorHandler(errorHandler);
   }

   public ErrorHandler getErrorHandler() {
      return this.fComponentManager.getErrorHandler();
   }

   public void setResourceResolver(LSResourceResolver resourceResolver) {
      this.fResourceResolverChanged = resourceResolver != null;
      this.fComponentManager.setResourceResolver(resourceResolver);
   }

   public LSResourceResolver getResourceResolver() {
      return this.fComponentManager.getResourceResolver();
   }

   public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
      if (name == null) {
         throw new NullPointerException();
      } else {
         try {
            return this.fComponentManager.getFeature(name);
         } catch (XMLConfigurationException var5) {
            String identifier = var5.getIdentifier();
            String key = var5.getType() == Status.NOT_RECOGNIZED ? "feature-not-recognized" : "feature-not-supported";
            throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), key, new Object[]{identifier}));
         }
      }
   }

   public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
      if (name == null) {
         throw new NullPointerException();
      } else {
         try {
            this.fComponentManager.setFeature(name, value);
         } catch (XMLConfigurationException var6) {
            String identifier = var6.getIdentifier();
            if (var6.getType() == Status.NOT_ALLOWED) {
               throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "jaxp-secureprocessing-feature", (Object[])null));
            }

            String key;
            if (var6.getType() == Status.NOT_RECOGNIZED) {
               key = "feature-not-recognized";
            } else {
               key = "feature-not-supported";
            }

            throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), key, new Object[]{identifier}));
         }

         this.fConfigurationChanged = true;
      }
   }

   public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
      if (name == null) {
         throw new NullPointerException();
      } else if ("http://apache.org/xml/properties/dom/current-element-node".equals(name)) {
         return this.fDOMValidatorHelper != null ? this.fDOMValidatorHelper.getCurrentElement() : null;
      } else {
         try {
            return this.fComponentManager.getProperty(name);
         } catch (XMLConfigurationException var5) {
            String identifier = var5.getIdentifier();
            String key = var5.getType() == Status.NOT_RECOGNIZED ? "property-not-recognized" : "property-not-supported";
            throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), key, new Object[]{identifier}));
         }
      }
   }

   public void setProperty(String name, Object object) throws SAXNotRecognizedException, SAXNotSupportedException {
      if (name == null) {
         throw new NullPointerException();
      } else {
         try {
            this.fComponentManager.setProperty(name, object);
         } catch (XMLConfigurationException var6) {
            String identifier = var6.getIdentifier();
            String key = var6.getType() == Status.NOT_RECOGNIZED ? "property-not-recognized" : "property-not-supported";
            throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), key, new Object[]{identifier}));
         }

         this.fConfigurationChanged = true;
      }
   }

   public void reset() {
      if (this.fConfigurationChanged) {
         this.fComponentManager.restoreInitialState();
         this.setErrorHandler((ErrorHandler)null);
         this.setResourceResolver((LSResourceResolver)null);
         this.fConfigurationChanged = false;
         this.fErrorHandlerChanged = false;
         this.fResourceResolverChanged = false;
      } else {
         if (this.fErrorHandlerChanged) {
            this.setErrorHandler((ErrorHandler)null);
            this.fErrorHandlerChanged = false;
         }

         if (this.fResourceResolverChanged) {
            this.setResourceResolver((LSResourceResolver)null);
            this.fResourceResolverChanged = false;
         }
      }

   }

   public ElementPSVI getElementPSVI() {
      return this.fSAXValidatorHelper != null ? this.fSAXValidatorHelper.getElementPSVI() : null;
   }

   public AttributePSVI getAttributePSVI(int index) {
      return this.fSAXValidatorHelper != null ? this.fSAXValidatorHelper.getAttributePSVI(index) : null;
   }

   public AttributePSVI getAttributePSVIByName(String uri, String localname) {
      return this.fSAXValidatorHelper != null ? this.fSAXValidatorHelper.getAttributePSVIByName(uri, localname) : null;
   }
}
