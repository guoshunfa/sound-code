package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class SAXParserFactoryImpl extends SAXParserFactory {
   private static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation";
   private static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces";
   private static final String XINCLUDE_FEATURE = "http://apache.org/xml/features/xinclude";
   private Map<String, Boolean> features;
   private Schema grammar;
   private boolean isXIncludeAware;
   private boolean fSecureProcess = true;

   public SAXParser newSAXParser() throws ParserConfigurationException {
      try {
         SAXParser saxParserImpl = new SAXParserImpl(this, this.features, this.fSecureProcess);
         return saxParserImpl;
      } catch (SAXException var3) {
         throw new ParserConfigurationException(var3.getMessage());
      }
   }

   private SAXParserImpl newSAXParserImpl() throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
      try {
         SAXParserImpl saxParserImpl = new SAXParserImpl(this, this.features);
         return saxParserImpl;
      } catch (SAXNotSupportedException var3) {
         throw var3;
      } catch (SAXNotRecognizedException var4) {
         throw var4;
      } catch (SAXException var5) {
         throw new ParserConfigurationException(var5.getMessage());
      }
   }

   public void setFeature(String name, boolean value) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
      if (name == null) {
         throw new NullPointerException();
      } else if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
         if (System.getSecurityManager() != null && !value) {
            throw new ParserConfigurationException(SAXMessageFormatter.formatMessage((Locale)null, "jaxp-secureprocessing-feature", (Object[])null));
         } else {
            this.fSecureProcess = value;
            this.putInFeatures(name, value);
         }
      } else {
         this.putInFeatures(name, value);

         try {
            this.newSAXParserImpl();
         } catch (SAXNotSupportedException var4) {
            this.features.remove(name);
            throw var4;
         } catch (SAXNotRecognizedException var5) {
            this.features.remove(name);
            throw var5;
         }
      }
   }

   public boolean getFeature(String name) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
      if (name == null) {
         throw new NullPointerException();
      } else {
         return name.equals("http://javax.xml.XMLConstants/feature/secure-processing") ? this.fSecureProcess : this.newSAXParserImpl().getXMLReader().getFeature(name);
      }
   }

   public Schema getSchema() {
      return this.grammar;
   }

   public void setSchema(Schema grammar) {
      this.grammar = grammar;
   }

   public boolean isXIncludeAware() {
      return this.getFromFeatures("http://apache.org/xml/features/xinclude");
   }

   public void setXIncludeAware(boolean state) {
      this.putInFeatures("http://apache.org/xml/features/xinclude", state);
   }

   public void setValidating(boolean validating) {
      this.putInFeatures("http://xml.org/sax/features/validation", validating);
   }

   public boolean isValidating() {
      return this.getFromFeatures("http://xml.org/sax/features/validation");
   }

   private void putInFeatures(String name, boolean value) {
      if (this.features == null) {
         this.features = new HashMap();
      }

      this.features.put(name, value ? Boolean.TRUE : Boolean.FALSE);
   }

   private boolean getFromFeatures(String name) {
      if (this.features == null) {
         return false;
      } else {
         Boolean value = (Boolean)this.features.get(name);
         return value == null ? false : value;
      }
   }

   public boolean isNamespaceAware() {
      return this.getFromFeatures("http://xml.org/sax/features/namespaces");
   }

   public void setNamespaceAware(boolean awareness) {
      this.putInFeatures("http://xml.org/sax/features/namespaces", awareness);
   }
}
