package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xalan.internal.xsltc.compiler.XSLTC;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import java.io.InputStream;
import java.io.Reader;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import jdk.xml.internal.JdkXmlFeatures;
import jdk.xml.internal.JdkXmlUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

public final class Util {
   private static final String property = "org.xml.sax.driver";

   public static String baseName(String name) {
      return com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util.baseName(name);
   }

   public static String noExtName(String name) {
      return com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util.noExtName(name);
   }

   public static String toJavaName(String name) {
      return com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util.toJavaName(name);
   }

   public static InputSource getInputSource(XSLTC xsltc, Source source) throws TransformerConfigurationException {
      InputSource input = null;
      String systemId = source.getSystemId();

      ErrorMsg staxevent2sax;
      try {
         String lastProperty;
         if (source instanceof SAXSource) {
            SAXSource sax = (SAXSource)source;
            input = sax.getInputSource();

            try {
               XMLReader reader = sax.getXMLReader();
               if (reader == null) {
                  boolean overrideDefaultParser = xsltc.getFeature(JdkXmlFeatures.XmlFeature.JDK_OVERRIDE_PARSER);
                  reader = JdkXmlUtils.getXMLReader(overrideDefaultParser, xsltc.isSecureProcessing());
               } else {
                  reader.setFeature("http://xml.org/sax/features/namespaces", true);
                  reader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
               }

               try {
                  reader.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", xsltc.getProperty("http://javax.xml.XMLConstants/property/accessExternalDTD"));
               } catch (SAXNotRecognizedException var12) {
                  XMLSecurityManager.printWarning(reader.getClass().getName(), "http://javax.xml.XMLConstants/property/accessExternalDTD", var12);
               }

               lastProperty = "";

               try {
                  XMLSecurityManager securityManager = (XMLSecurityManager)xsltc.getProperty("http://apache.org/xml/properties/security-manager");
                  if (securityManager != null) {
                     XMLSecurityManager.Limit[] var8 = XMLSecurityManager.Limit.values();
                     int var9 = var8.length;

                     for(int var10 = 0; var10 < var9; ++var10) {
                        XMLSecurityManager.Limit limit = var8[var10];
                        lastProperty = limit.apiProperty();
                        reader.setProperty(lastProperty, securityManager.getLimitValueAsString(limit));
                     }

                     if (securityManager.printEntityCountInfo()) {
                        lastProperty = "http://www.oracle.com/xml/jaxp/properties/getEntityCountInfo";
                        reader.setProperty("http://www.oracle.com/xml/jaxp/properties/getEntityCountInfo", "yes");
                     }
                  }
               } catch (SAXException var13) {
                  XMLSecurityManager.printWarning(reader.getClass().getName(), lastProperty, var13);
               }

               xsltc.setXMLReader(reader);
            } catch (SAXNotRecognizedException var14) {
               throw new TransformerConfigurationException("SAXNotRecognizedException ", var14);
            } catch (SAXNotSupportedException var15) {
               throw new TransformerConfigurationException("SAXNotSupportedException ", var15);
            }
         } else if (source instanceof DOMSource) {
            DOMSource domsrc = (DOMSource)source;
            Document dom = (Document)domsrc.getNode();
            DOM2SAX dom2sax = new DOM2SAX(dom);
            xsltc.setXMLReader(dom2sax);
            input = SAXSource.sourceToInputSource(source);
            if (input == null) {
               input = new InputSource(domsrc.getSystemId());
            }
         } else if (source instanceof StAXSource) {
            StAXSource staxSource = (StAXSource)source;
            staxevent2sax = null;
            lastProperty = null;
            if (staxSource.getXMLEventReader() != null) {
               XMLEventReader xmlEventReader = staxSource.getXMLEventReader();
               StAXEvent2SAX staxevent2sax = new StAXEvent2SAX(xmlEventReader);
               xsltc.setXMLReader(staxevent2sax);
            } else if (staxSource.getXMLStreamReader() != null) {
               XMLStreamReader xmlStreamReader = staxSource.getXMLStreamReader();
               StAXStream2SAX staxStream2SAX = new StAXStream2SAX(xmlStreamReader);
               xsltc.setXMLReader(staxStream2SAX);
            }

            input = SAXSource.sourceToInputSource(source);
            if (input == null) {
               input = new InputSource(staxSource.getSystemId());
            }
         } else {
            if (!(source instanceof StreamSource)) {
               ErrorMsg err = new ErrorMsg("JAXP_UNKNOWN_SOURCE_ERR");
               throw new TransformerConfigurationException(err.toString());
            }

            StreamSource stream = (StreamSource)source;
            InputStream istream = stream.getInputStream();
            Reader reader = stream.getReader();
            xsltc.setXMLReader((XMLReader)null);
            if (istream != null) {
               input = new InputSource(istream);
            } else if (reader != null) {
               input = new InputSource(reader);
            } else {
               input = new InputSource(systemId);
            }
         }

         input.setSystemId(systemId);
         return input;
      } catch (NullPointerException var16) {
         staxevent2sax = new ErrorMsg("JAXP_NO_SOURCE_ERR", "TransformerFactory.newTemplates()");
         throw new TransformerConfigurationException(staxevent2sax.toString());
      } catch (SecurityException var17) {
         staxevent2sax = new ErrorMsg("FILE_ACCESS_ERR", systemId);
         throw new TransformerConfigurationException(staxevent2sax.toString());
      }
   }
}
