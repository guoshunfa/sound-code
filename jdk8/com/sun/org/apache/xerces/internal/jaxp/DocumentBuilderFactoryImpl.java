package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class DocumentBuilderFactoryImpl extends DocumentBuilderFactory {
   private Map<String, Object> attributes;
   private Map<String, Boolean> features;
   private Schema grammar;
   private boolean isXIncludeAware;
   private boolean fSecureProcess = true;

   public DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
      if (this.grammar != null && this.attributes != null) {
         if (this.attributes.containsKey("http://java.sun.com/xml/jaxp/properties/schemaLanguage")) {
            throw new ParserConfigurationException(SAXMessageFormatter.formatMessage((Locale)null, "schema-already-specified", new Object[]{"http://java.sun.com/xml/jaxp/properties/schemaLanguage"}));
         }

         if (this.attributes.containsKey("http://java.sun.com/xml/jaxp/properties/schemaSource")) {
            throw new ParserConfigurationException(SAXMessageFormatter.formatMessage((Locale)null, "schema-already-specified", new Object[]{"http://java.sun.com/xml/jaxp/properties/schemaSource"}));
         }
      }

      try {
         return new DocumentBuilderImpl(this, this.attributes, this.features, this.fSecureProcess);
      } catch (SAXException var2) {
         throw new ParserConfigurationException(var2.getMessage());
      }
   }

   public void setAttribute(String name, Object value) throws IllegalArgumentException {
      if (value == null) {
         if (this.attributes != null) {
            this.attributes.remove(name);
         }

      } else {
         if (this.attributes == null) {
            this.attributes = new HashMap();
         }

         this.attributes.put(name, value);

         try {
            new DocumentBuilderImpl(this, this.attributes, this.features);
         } catch (Exception var4) {
            this.attributes.remove(name);
            throw new IllegalArgumentException(var4.getMessage());
         }
      }
   }

   public Object getAttribute(String name) throws IllegalArgumentException {
      Object domParser;
      if (this.attributes != null) {
         domParser = this.attributes.get(name);
         if (domParser != null) {
            return domParser;
         }
      }

      domParser = null;

      try {
         DOMParser domParser = (new DocumentBuilderImpl(this, this.attributes, this.features)).getDOMParser();
         return domParser.getProperty(name);
      } catch (SAXException var6) {
         try {
            boolean result = ((DOMParser)domParser).getFeature(name);
            return result ? Boolean.TRUE : Boolean.FALSE;
         } catch (SAXException var5) {
            throw new IllegalArgumentException(var6.getMessage());
         }
      }
   }

   public Schema getSchema() {
      return this.grammar;
   }

   public void setSchema(Schema grammar) {
      this.grammar = grammar;
   }

   public boolean isXIncludeAware() {
      return this.isXIncludeAware;
   }

   public void setXIncludeAware(boolean state) {
      this.isXIncludeAware = state;
   }

   public boolean getFeature(String name) throws ParserConfigurationException {
      if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
         return this.fSecureProcess;
      } else {
         if (this.features != null) {
            Boolean val = (Boolean)this.features.get(name);
            if (val != null) {
               return val;
            }
         }

         try {
            DOMParser domParser = (new DocumentBuilderImpl(this, this.attributes, this.features)).getDOMParser();
            return domParser.getFeature(name);
         } catch (SAXException var3) {
            throw new ParserConfigurationException(var3.getMessage());
         }
      }
   }

   public void setFeature(String name, boolean value) throws ParserConfigurationException {
      if (this.features == null) {
         this.features = new HashMap();
      }

      if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
         if (System.getSecurityManager() != null && !value) {
            throw new ParserConfigurationException(SAXMessageFormatter.formatMessage((Locale)null, "jaxp-secureprocessing-feature", (Object[])null));
         } else {
            this.fSecureProcess = value;
            this.features.put(name, value ? Boolean.TRUE : Boolean.FALSE);
         }
      } else {
         this.features.put(name, value ? Boolean.TRUE : Boolean.FALSE);

         try {
            new DocumentBuilderImpl(this, this.attributes, this.features);
         } catch (SAXNotSupportedException var4) {
            this.features.remove(name);
            throw new ParserConfigurationException(var4.getMessage());
         } catch (SAXNotRecognizedException var5) {
            this.features.remove(name);
            throw new ParserConfigurationException(var5.getMessage());
         }
      }
   }
}
