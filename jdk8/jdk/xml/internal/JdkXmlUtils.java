package jdk.xml.internal;

import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;
import com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class JdkXmlUtils {
   private static final String DOM_FACTORY_ID = "javax.xml.parsers.DocumentBuilderFactory";
   private static final String SAX_FACTORY_ID = "javax.xml.parsers.SAXParserFactory";
   private static final String SAX_DRIVER = "org.xml.sax.driver";
   public static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces";
   public static final String NAMESPACE_PREFIXES_FEATURE = "http://xml.org/sax/features/namespace-prefixes";
   public static final String OVERRIDE_PARSER = "jdk.xml.overrideDefaultParser";
   public static final boolean OVERRIDE_PARSER_DEFAULT = (Boolean)SecuritySupport.getJAXPSystemProperty(Boolean.class, "jdk.xml.overrideDefaultParser", "false");
   public static final String FEATURE_TRUE = "true";
   public static final String FEATURE_FALSE = "false";
   private static final SAXParserFactory defaultSAXFactory = getSAXFactory(false);

   public static int getValue(Object value, int defValue) {
      if (value == null) {
         return defValue;
      } else if (value instanceof Number) {
         return ((Number)value).intValue();
      } else if (value instanceof String) {
         return Integer.parseInt(String.valueOf(value));
      } else {
         throw new IllegalArgumentException("Unexpected class: " + value.getClass());
      }
   }

   public static void setXMLReaderPropertyIfSupport(XMLReader reader, String property, Object value, boolean warn) {
      try {
         reader.setProperty(property, value);
      } catch (SAXNotSupportedException | SAXNotRecognizedException var5) {
         if (warn) {
            XMLSecurityManager.printWarning(reader.getClass().getName(), property, var5);
         }
      }

   }

   public static XMLReader getXMLReader(boolean overrideDefaultParser, boolean secureProcessing) {
      XMLReader reader = null;
      String spSAXDriver = SecuritySupport.getSystemProperty("org.xml.sax.driver");
      if (spSAXDriver != null) {
         reader = getXMLReaderWXMLReaderFactory();
      } else if (overrideDefaultParser) {
         reader = getXMLReaderWSAXFactory(overrideDefaultParser);
      }

      if (reader != null) {
         if (secureProcessing) {
            try {
               reader.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", secureProcessing);
            } catch (SAXException var7) {
               XMLSecurityManager.printWarning(reader.getClass().getName(), "http://javax.xml.XMLConstants/feature/secure-processing", var7);
            }
         }

         try {
            reader.setFeature("http://xml.org/sax/features/namespaces", true);
            reader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
         } catch (SAXException var6) {
         }

         return reader;
      } else {
         SAXParserFactory saxFactory = defaultSAXFactory;

         try {
            reader = saxFactory.newSAXParser().getXMLReader();
         } catch (SAXException | ParserConfigurationException var8) {
         }

         return reader;
      }
   }

   public static Document getDOMDocument() {
      try {
         DocumentBuilderFactory dbf = getDOMFactory(false);
         return dbf.newDocumentBuilder().newDocument();
      } catch (ParserConfigurationException var1) {
         return null;
      }
   }

   public static DocumentBuilderFactory getDOMFactory(boolean overrideDefaultParser) {
      boolean override = overrideDefaultParser;
      String spDOMFactory = SecuritySupport.getJAXPSystemProperty("javax.xml.parsers.DocumentBuilderFactory");
      if (spDOMFactory != null && System.getSecurityManager() == null) {
         override = true;
      }

      DocumentBuilderFactory dbf = !override ? new DocumentBuilderFactoryImpl() : DocumentBuilderFactory.newInstance();
      ((DocumentBuilderFactory)dbf).setNamespaceAware(true);
      ((DocumentBuilderFactory)dbf).setValidating(false);
      return (DocumentBuilderFactory)dbf;
   }

   public static SAXParserFactory getSAXFactory(boolean overrideDefaultParser) {
      boolean override = overrideDefaultParser;
      String spSAXFactory = SecuritySupport.getJAXPSystemProperty("javax.xml.parsers.SAXParserFactory");
      if (spSAXFactory != null && System.getSecurityManager() == null) {
         override = true;
      }

      SAXParserFactory factory = !override ? new SAXParserFactoryImpl() : SAXParserFactory.newInstance();
      ((SAXParserFactory)factory).setNamespaceAware(true);
      return (SAXParserFactory)factory;
   }

   public static SAXTransformerFactory getSAXTransformFactory(boolean overrideDefaultParser) {
      Object tf = overrideDefaultParser ? (SAXTransformerFactory)SAXTransformerFactory.newInstance() : new TransformerFactoryImpl();

      try {
         ((SAXTransformerFactory)tf).setFeature("jdk.xml.overrideDefaultParser", overrideDefaultParser);
      } catch (TransformerConfigurationException var3) {
      }

      return (SAXTransformerFactory)tf;
   }

   private static XMLReader getXMLReaderWSAXFactory(boolean overrideDefaultParser) {
      SAXParserFactory saxFactory = getSAXFactory(overrideDefaultParser);

      try {
         return saxFactory.newSAXParser().getXMLReader();
      } catch (SAXException | ParserConfigurationException var3) {
         return getXMLReaderWXMLReaderFactory();
      }
   }

   private static XMLReader getXMLReaderWXMLReaderFactory() {
      try {
         return XMLReaderFactory.createXMLReader();
      } catch (SAXException var1) {
         return null;
      }
   }
}
