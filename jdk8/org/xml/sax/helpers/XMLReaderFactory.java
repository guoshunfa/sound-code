package org.xml.sax.helpers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public final class XMLReaderFactory {
   private static final String property = "org.xml.sax.driver";
   private static SecuritySupport ss = new SecuritySupport();
   private static String _clsFromJar = null;
   private static boolean _jarread = false;

   private XMLReaderFactory() {
   }

   public static XMLReader createXMLReader() throws SAXException {
      String className = null;
      ClassLoader cl = ss.getContextClassLoader();

      try {
         className = ss.getSystemProperty("org.xml.sax.driver");
      } catch (RuntimeException var8) {
      }

      if (className == null) {
         if (!_jarread) {
            _jarread = true;
            String service = "META-INF/services/org.xml.sax.driver";

            try {
               InputStream in;
               if (cl != null) {
                  in = ss.getResourceAsStream(cl, service);
                  if (in == null) {
                     cl = null;
                     in = ss.getResourceAsStream(cl, service);
                  }
               } else {
                  in = ss.getResourceAsStream(cl, service);
               }

               if (in != null) {
                  BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF8"));
                  _clsFromJar = reader.readLine();
                  in.close();
               }
            } catch (Exception var7) {
            }
         }

         className = _clsFromJar;
      }

      if (className == null) {
         className = "com.sun.org.apache.xerces.internal.parsers.SAXParser";
      }

      if (className != null) {
         return loadClass(cl, className);
      } else {
         try {
            return new ParserAdapter(ParserFactory.makeParser());
         } catch (Exception var6) {
            throw new SAXException("Can't create default XMLReader; is system property org.xml.sax.driver set?");
         }
      }
   }

   public static XMLReader createXMLReader(String className) throws SAXException {
      return loadClass(ss.getContextClassLoader(), className);
   }

   private static XMLReader loadClass(ClassLoader loader, String className) throws SAXException {
      try {
         return (XMLReader)NewInstance.newInstance(loader, className);
      } catch (ClassNotFoundException var3) {
         throw new SAXException("SAX2 driver class " + className + " not found", var3);
      } catch (IllegalAccessException var4) {
         throw new SAXException("SAX2 driver class " + className + " found but cannot be loaded", var4);
      } catch (InstantiationException var5) {
         throw new SAXException("SAX2 driver class " + className + " loaded but cannot be instantiated (no empty public constructor?)", var5);
      } catch (ClassCastException var6) {
         throw new SAXException("SAX2 driver class " + className + " does not implement XMLReader", var6);
      }
   }
}
