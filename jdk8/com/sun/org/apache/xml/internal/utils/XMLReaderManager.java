package com.sun.org.apache.xml.internal.utils;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager;
import java.util.HashMap;
import jdk.xml.internal.JdkXmlUtils;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class XMLReaderManager {
   private static final XMLReaderManager m_singletonManager = new XMLReaderManager();
   private static final String property = "org.xml.sax.driver";
   private ThreadLocal<XMLReaderManager.ReaderWrapper> m_readers;
   private boolean m_overrideDefaultParser;
   private HashMap m_inUse;
   private boolean _secureProcessing;
   private String _accessExternalDTD = "all";
   private XMLSecurityManager _xmlSecurityManager;

   private XMLReaderManager() {
   }

   public static XMLReaderManager getInstance(boolean overrideDefaultParser) {
      m_singletonManager.setOverrideDefaultParser(overrideDefaultParser);
      return m_singletonManager;
   }

   public synchronized XMLReader getXMLReader() throws SAXException {
      if (this.m_readers == null) {
         this.m_readers = new ThreadLocal();
      }

      if (this.m_inUse == null) {
         this.m_inUse = new HashMap();
      }

      XMLReaderManager.ReaderWrapper rw = (XMLReaderManager.ReaderWrapper)this.m_readers.get();
      boolean threadHasReader = rw != null;
      XMLReader reader = threadHasReader ? rw.reader : null;
      String factory = SecuritySupport.getSystemProperty("org.xml.sax.driver");
      if (!threadHasReader || this.m_inUse.get(reader) == Boolean.TRUE || rw.overrideDefaultParser != this.m_overrideDefaultParser || factory != null && !reader.getClass().getName().equals(factory)) {
         reader = JdkXmlUtils.getXMLReader(this.m_overrideDefaultParser, this._secureProcessing);
         if (!threadHasReader) {
            this.m_readers.set(new XMLReaderManager.ReaderWrapper(reader, this.m_overrideDefaultParser));
            this.m_inUse.put(reader, Boolean.TRUE);
         }
      } else {
         this.m_inUse.put(reader, Boolean.TRUE);
      }

      try {
         reader.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", this._accessExternalDTD);
      } catch (SAXException var10) {
         XMLSecurityManager.printWarning(reader.getClass().getName(), "http://javax.xml.XMLConstants/property/accessExternalDTD", var10);
      }

      String lastProperty = "";

      try {
         if (this._xmlSecurityManager != null) {
            XMLSecurityManager.Limit[] var6 = XMLSecurityManager.Limit.values();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               XMLSecurityManager.Limit limit = var6[var8];
               lastProperty = limit.apiProperty();
               reader.setProperty(lastProperty, this._xmlSecurityManager.getLimitValueAsString(limit));
            }

            if (this._xmlSecurityManager.printEntityCountInfo()) {
               lastProperty = "http://www.oracle.com/xml/jaxp/properties/getEntityCountInfo";
               reader.setProperty("http://www.oracle.com/xml/jaxp/properties/getEntityCountInfo", "yes");
            }
         }
      } catch (SAXException var11) {
         XMLSecurityManager.printWarning(reader.getClass().getName(), lastProperty, var11);
      }

      return reader;
   }

   public synchronized void releaseXMLReader(XMLReader reader) {
      XMLReaderManager.ReaderWrapper rw = (XMLReaderManager.ReaderWrapper)this.m_readers.get();
      if (rw.reader == reader && reader != null) {
         this.m_inUse.remove(reader);
      }

   }

   public boolean overrideDefaultParser() {
      return this.m_overrideDefaultParser;
   }

   public void setOverrideDefaultParser(boolean flag) {
      this.m_overrideDefaultParser = flag;
   }

   public void setFeature(String name, boolean value) {
      if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
         this._secureProcessing = value;
      }

   }

   public Object getProperty(String name) {
      if (name.equals("http://javax.xml.XMLConstants/property/accessExternalDTD")) {
         return this._accessExternalDTD;
      } else {
         return name.equals("http://apache.org/xml/properties/security-manager") ? this._xmlSecurityManager : null;
      }
   }

   public void setProperty(String name, Object value) {
      if (name.equals("http://javax.xml.XMLConstants/property/accessExternalDTD")) {
         this._accessExternalDTD = (String)value;
      } else if (name.equals("http://apache.org/xml/properties/security-manager")) {
         this._xmlSecurityManager = (XMLSecurityManager)value;
      }

   }

   class ReaderWrapper {
      XMLReader reader;
      boolean overrideDefaultParser;

      public ReaderWrapper(XMLReader reader, boolean overrideDefaultParser) {
         this.reader = reader;
         this.overrideDefaultParser = overrideDefaultParser;
      }
   }
}
