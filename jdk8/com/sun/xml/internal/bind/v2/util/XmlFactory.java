package com.sun.xml.internal.bind.v2.util;

import com.sun.xml.internal.bind.v2.Messages;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class XmlFactory {
   public static final String ACCESS_EXTERNAL_SCHEMA = "http://javax.xml.XMLConstants/property/accessExternalSchema";
   public static final String ACCESS_EXTERNAL_DTD = "http://javax.xml.XMLConstants/property/accessExternalDTD";
   private static final Logger LOGGER = Logger.getLogger(XmlFactory.class.getName());
   private static final String DISABLE_XML_SECURITY = "com.sun.xml.internal.bind.disableXmlSecurity";
   private static final boolean XML_SECURITY_DISABLED = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
      public Boolean run() {
         return Boolean.getBoolean("com.sun.xml.internal.bind.disableXmlSecurity");
      }
   });

   private static boolean isXMLSecurityDisabled(boolean runtimeSetting) {
      return XML_SECURITY_DISABLED || runtimeSetting;
   }

   public static SchemaFactory createSchemaFactory(String language, boolean disableSecureProcessing) throws IllegalStateException {
      try {
         SchemaFactory factory = SchemaFactory.newInstance(language);
         if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, (String)"SchemaFactory instance: {0}", (Object)factory);
         }

         factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", !isXMLSecurityDisabled(disableSecureProcessing));
         return factory;
      } catch (SAXNotRecognizedException var3) {
         LOGGER.log(Level.SEVERE, (String)null, (Throwable)var3);
         throw new IllegalStateException(var3);
      } catch (SAXNotSupportedException var4) {
         LOGGER.log(Level.SEVERE, (String)null, (Throwable)var4);
         throw new IllegalStateException(var4);
      } catch (AbstractMethodError var5) {
         LOGGER.log(Level.SEVERE, (String)null, (Throwable)var5);
         throw new IllegalStateException(Messages.INVALID_JAXP_IMPLEMENTATION.format(), var5);
      }
   }

   public static SAXParserFactory createParserFactory(boolean disableSecureProcessing) throws IllegalStateException {
      try {
         SAXParserFactory factory = SAXParserFactory.newInstance();
         if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, (String)"SAXParserFactory instance: {0}", (Object)factory);
         }

         factory.setNamespaceAware(true);
         factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", !isXMLSecurityDisabled(disableSecureProcessing));
         return factory;
      } catch (ParserConfigurationException var2) {
         LOGGER.log(Level.SEVERE, (String)null, (Throwable)var2);
         throw new IllegalStateException(var2);
      } catch (SAXNotRecognizedException var3) {
         LOGGER.log(Level.SEVERE, (String)null, (Throwable)var3);
         throw new IllegalStateException(var3);
      } catch (SAXNotSupportedException var4) {
         LOGGER.log(Level.SEVERE, (String)null, (Throwable)var4);
         throw new IllegalStateException(var4);
      } catch (AbstractMethodError var5) {
         LOGGER.log(Level.SEVERE, (String)null, (Throwable)var5);
         throw new IllegalStateException(Messages.INVALID_JAXP_IMPLEMENTATION.format(), var5);
      }
   }

   public static XPathFactory createXPathFactory(boolean disableSecureProcessing) throws IllegalStateException {
      try {
         XPathFactory factory = XPathFactory.newInstance();
         if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, (String)"XPathFactory instance: {0}", (Object)factory);
         }

         factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", !isXMLSecurityDisabled(disableSecureProcessing));
         return factory;
      } catch (XPathFactoryConfigurationException var2) {
         LOGGER.log(Level.SEVERE, (String)null, (Throwable)var2);
         throw new IllegalStateException(var2);
      } catch (AbstractMethodError var3) {
         LOGGER.log(Level.SEVERE, (String)null, (Throwable)var3);
         throw new IllegalStateException(Messages.INVALID_JAXP_IMPLEMENTATION.format(), var3);
      }
   }

   public static TransformerFactory createTransformerFactory(boolean disableSecureProcessing) throws IllegalStateException {
      try {
         TransformerFactory factory = TransformerFactory.newInstance();
         if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, (String)"TransformerFactory instance: {0}", (Object)factory);
         }

         factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", !isXMLSecurityDisabled(disableSecureProcessing));
         return factory;
      } catch (TransformerConfigurationException var2) {
         LOGGER.log(Level.SEVERE, (String)null, (Throwable)var2);
         throw new IllegalStateException(var2);
      } catch (AbstractMethodError var3) {
         LOGGER.log(Level.SEVERE, (String)null, (Throwable)var3);
         throw new IllegalStateException(Messages.INVALID_JAXP_IMPLEMENTATION.format(), var3);
      }
   }

   public static DocumentBuilderFactory createDocumentBuilderFactory(boolean disableSecureProcessing) throws IllegalStateException {
      try {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, (String)"DocumentBuilderFactory instance: {0}", (Object)factory);
         }

         factory.setNamespaceAware(true);
         factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", !isXMLSecurityDisabled(disableSecureProcessing));
         return factory;
      } catch (ParserConfigurationException var2) {
         LOGGER.log(Level.SEVERE, (String)null, (Throwable)var2);
         throw new IllegalStateException(var2);
      } catch (AbstractMethodError var3) {
         LOGGER.log(Level.SEVERE, (String)null, (Throwable)var3);
         throw new IllegalStateException(Messages.INVALID_JAXP_IMPLEMENTATION.format(), var3);
      }
   }

   public static SchemaFactory allowExternalAccess(SchemaFactory sf, String value, boolean disableSecureProcessing) {
      if (isXMLSecurityDisabled(disableSecureProcessing)) {
         if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, Messages.JAXP_XML_SECURITY_DISABLED.format());
         }

         return sf;
      } else if (System.getProperty("javax.xml.accessExternalSchema") != null) {
         if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, Messages.JAXP_EXTERNAL_ACCESS_CONFIGURED.format());
         }

         return sf;
      } else {
         try {
            sf.setProperty("http://javax.xml.XMLConstants/property/accessExternalSchema", value);
            if (LOGGER.isLoggable(Level.FINE)) {
               LOGGER.log(Level.FINE, Messages.JAXP_SUPPORTED_PROPERTY.format("http://javax.xml.XMLConstants/property/accessExternalSchema"));
            }
         } catch (SAXException var4) {
            if (LOGGER.isLoggable(Level.CONFIG)) {
               LOGGER.log(Level.CONFIG, (String)Messages.JAXP_UNSUPPORTED_PROPERTY.format("http://javax.xml.XMLConstants/property/accessExternalSchema"), (Throwable)var4);
            }
         }

         return sf;
      }
   }

   public static SchemaFactory allowExternalDTDAccess(SchemaFactory sf, String value, boolean disableSecureProcessing) {
      if (isXMLSecurityDisabled(disableSecureProcessing)) {
         if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, Messages.JAXP_XML_SECURITY_DISABLED.format());
         }

         return sf;
      } else if (System.getProperty("javax.xml.accessExternalDTD") != null) {
         if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, Messages.JAXP_EXTERNAL_ACCESS_CONFIGURED.format());
         }

         return sf;
      } else {
         try {
            sf.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", value);
            if (LOGGER.isLoggable(Level.FINE)) {
               LOGGER.log(Level.FINE, Messages.JAXP_SUPPORTED_PROPERTY.format("http://javax.xml.XMLConstants/property/accessExternalDTD"));
            }
         } catch (SAXException var4) {
            if (LOGGER.isLoggable(Level.CONFIG)) {
               LOGGER.log(Level.CONFIG, (String)Messages.JAXP_UNSUPPORTED_PROPERTY.format("http://javax.xml.XMLConstants/property/accessExternalDTD"), (Throwable)var4);
            }
         }

         return sf;
      }
   }
}
