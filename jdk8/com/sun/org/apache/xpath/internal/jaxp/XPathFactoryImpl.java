package com.sun.org.apache.xpath.internal.jaxp;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;
import jdk.xml.internal.JdkXmlFeatures;

public class XPathFactoryImpl extends XPathFactory {
   private static final String CLASS_NAME = "XPathFactoryImpl";
   private XPathFunctionResolver xPathFunctionResolver = null;
   private XPathVariableResolver xPathVariableResolver = null;
   private boolean _isNotSecureProcessing = true;
   private boolean _isSecureMode = false;
   private final JdkXmlFeatures _featureManager;

   public XPathFactoryImpl() {
      if (System.getSecurityManager() != null) {
         this._isSecureMode = true;
         this._isNotSecureProcessing = false;
      }

      this._featureManager = new JdkXmlFeatures(!this._isNotSecureProcessing);
   }

   public boolean isObjectModelSupported(String objectModel) {
      String fmsg;
      if (objectModel == null) {
         fmsg = XSLMessages.createXPATHMessage("ER_OBJECT_MODEL_NULL", new Object[]{this.getClass().getName()});
         throw new NullPointerException(fmsg);
      } else if (objectModel.length() == 0) {
         fmsg = XSLMessages.createXPATHMessage("ER_OBJECT_MODEL_EMPTY", new Object[]{this.getClass().getName()});
         throw new IllegalArgumentException(fmsg);
      } else {
         return objectModel.equals("http://java.sun.com/jaxp/xpath/dom");
      }
   }

   public XPath newXPath() {
      return new XPathImpl(this.xPathVariableResolver, this.xPathFunctionResolver, !this._isNotSecureProcessing, this._featureManager);
   }

   public void setFeature(String name, boolean value) throws XPathFactoryConfigurationException {
      String fmsg;
      if (name == null) {
         fmsg = XSLMessages.createXPATHMessage("ER_FEATURE_NAME_NULL", new Object[]{"XPathFactoryImpl", new Boolean(value)});
         throw new NullPointerException(fmsg);
      } else if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
         if (this._isSecureMode && !value) {
            fmsg = XSLMessages.createXPATHMessage("ER_SECUREPROCESSING_FEATURE", new Object[]{name, "XPathFactoryImpl", new Boolean(value)});
            throw new XPathFactoryConfigurationException(fmsg);
         } else {
            this._isNotSecureProcessing = !value;
            if (value && this._featureManager != null) {
               this._featureManager.setFeature(JdkXmlFeatures.XmlFeature.ENABLE_EXTENSION_FUNCTION, JdkXmlFeatures.State.FSP, false);
            }

         }
      } else if (!name.equals("http://www.oracle.com/feature/use-service-mechanism") || !this._isSecureMode) {
         if (this._featureManager == null || !this._featureManager.setFeature(name, JdkXmlFeatures.State.APIPROPERTY, value)) {
            fmsg = XSLMessages.createXPATHMessage("ER_FEATURE_UNKNOWN", new Object[]{name, "XPathFactoryImpl", value});
            throw new XPathFactoryConfigurationException(fmsg);
         }
      }
   }

   public boolean getFeature(String name) throws XPathFactoryConfigurationException {
      if (name == null) {
         String fmsg = XSLMessages.createXPATHMessage("ER_GETTING_NULL_FEATURE", new Object[]{"XPathFactoryImpl"});
         throw new NullPointerException(fmsg);
      } else if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
         return !this._isNotSecureProcessing;
      } else {
         int index = this._featureManager.getIndex(name);
         if (index > -1) {
            return this._featureManager.getFeature(index);
         } else {
            String fmsg = XSLMessages.createXPATHMessage("ER_GETTING_UNKNOWN_FEATURE", new Object[]{name, "XPathFactoryImpl"});
            throw new XPathFactoryConfigurationException(fmsg);
         }
      }
   }

   public void setXPathFunctionResolver(XPathFunctionResolver resolver) {
      if (resolver == null) {
         String fmsg = XSLMessages.createXPATHMessage("ER_NULL_XPATH_FUNCTION_RESOLVER", new Object[]{"XPathFactoryImpl"});
         throw new NullPointerException(fmsg);
      } else {
         this.xPathFunctionResolver = resolver;
      }
   }

   public void setXPathVariableResolver(XPathVariableResolver resolver) {
      if (resolver == null) {
         String fmsg = XSLMessages.createXPATHMessage("ER_NULL_XPATH_VARIABLE_RESOLVER", new Object[]{"XPathFactoryImpl"});
         throw new NullPointerException(fmsg);
      } else {
         this.xPathVariableResolver = resolver;
      }
   }
}
