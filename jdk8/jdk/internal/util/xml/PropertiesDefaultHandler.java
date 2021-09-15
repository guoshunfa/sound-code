package jdk.internal.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import jdk.internal.org.xml.sax.Attributes;
import jdk.internal.org.xml.sax.InputSource;
import jdk.internal.org.xml.sax.Locator;
import jdk.internal.org.xml.sax.SAXException;
import jdk.internal.org.xml.sax.SAXParseException;
import jdk.internal.org.xml.sax.helpers.DefaultHandler;
import jdk.internal.util.xml.impl.SAXParserImpl;
import jdk.internal.util.xml.impl.XMLStreamWriterImpl;

public class PropertiesDefaultHandler extends DefaultHandler {
   private static final String ELEMENT_ROOT = "properties";
   private static final String ELEMENT_COMMENT = "comment";
   private static final String ELEMENT_ENTRY = "entry";
   private static final String ATTR_KEY = "key";
   private static final String PROPS_DTD_DECL = "<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">";
   private static final String PROPS_DTD_URI = "http://java.sun.com/dtd/properties.dtd";
   private static final String PROPS_DTD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- DTD for properties --><!ELEMENT properties ( comment?, entry* ) ><!ATTLIST properties version CDATA #FIXED \"1.0\"><!ELEMENT comment (#PCDATA) ><!ELEMENT entry (#PCDATA) ><!ATTLIST entry  key CDATA #REQUIRED>";
   private static final String EXTERNAL_XML_VERSION = "1.0";
   private Properties properties;
   static final String ALLOWED_ELEMENTS = "properties, comment, entry";
   static final String ALLOWED_COMMENT = "comment";
   StringBuffer buf = new StringBuffer();
   boolean sawComment = false;
   boolean validEntry = false;
   int rootElem = 0;
   String key;
   String rootElm;

   public void load(Properties var1, InputStream var2) throws IOException, InvalidPropertiesFormatException, UnsupportedEncodingException {
      this.properties = var1;

      try {
         SAXParserImpl var3 = new SAXParserImpl();
         var3.parse((InputStream)var2, this);
      } catch (SAXException var4) {
         throw new InvalidPropertiesFormatException(var4);
      }
   }

   public void store(Properties var1, OutputStream var2, String var3, String var4) throws IOException {
      try {
         XMLStreamWriterImpl var5 = new XMLStreamWriterImpl(var2, var4);
         var5.writeStartDocument();
         var5.writeDTD("<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">");
         var5.writeStartElement("properties");
         if (var3 != null && var3.length() > 0) {
            var5.writeStartElement("comment");
            var5.writeCharacters(var3);
            var5.writeEndElement();
         }

         synchronized(var1) {
            Iterator var7 = var1.entrySet().iterator();

            while(var7.hasNext()) {
               Map.Entry var8 = (Map.Entry)var7.next();
               Object var9 = var8.getKey();
               Object var10 = var8.getValue();
               if (var9 instanceof String && var10 instanceof String) {
                  var5.writeStartElement("entry");
                  var5.writeAttribute("key", (String)var9);
                  var5.writeCharacters((String)var10);
                  var5.writeEndElement();
               }
            }
         }

         var5.writeEndElement();
         var5.writeEndDocument();
         var5.close();
      } catch (XMLStreamException var13) {
         if (var13.getCause() instanceof UnsupportedEncodingException) {
            throw (UnsupportedEncodingException)var13.getCause();
         } else {
            throw new IOException(var13);
         }
      }
   }

   public void startElement(String var1, String var2, String var3, Attributes var4) throws SAXException {
      if (this.rootElem < 2) {
         ++this.rootElem;
      }

      if (this.rootElm == null) {
         this.fatalError(new SAXParseException("An XML properties document must contain the DOCTYPE declaration as defined by java.util.Properties.", (Locator)null));
      }

      if (this.rootElem == 1 && !this.rootElm.equals(var3)) {
         this.fatalError(new SAXParseException("Document root element \"" + var3 + "\", must match DOCTYPE root \"" + this.rootElm + "\"", (Locator)null));
      }

      if (!"properties, comment, entry".contains(var3)) {
         this.fatalError(new SAXParseException("Element type \"" + var3 + "\" must be declared.", (Locator)null));
      }

      if (var3.equals("entry")) {
         this.validEntry = true;
         this.key = var4.getValue("key");
         if (this.key == null) {
            this.fatalError(new SAXParseException("Attribute \"key\" is required and must be specified for element type \"entry\"", (Locator)null));
         }
      } else if (var3.equals("comment")) {
         if (this.sawComment) {
            this.fatalError(new SAXParseException("Only one comment element may be allowed. The content of element type \"properties\" must match \"(comment?,entry*)\"", (Locator)null));
         }

         this.sawComment = true;
      }

   }

   public void characters(char[] var1, int var2, int var3) throws SAXException {
      if (this.validEntry) {
         this.buf.append(var1, var2, var3);
      }

   }

   public void endElement(String var1, String var2, String var3) throws SAXException {
      if (!"properties, comment, entry".contains(var3)) {
         this.fatalError(new SAXParseException("Element: " + var3 + " is invalid, must match  \"(comment?,entry*)\".", (Locator)null));
      }

      if (this.validEntry) {
         this.properties.setProperty(this.key, this.buf.toString());
         this.buf.delete(0, this.buf.length());
         this.validEntry = false;
      }

   }

   public void notationDecl(String var1, String var2, String var3) throws SAXException {
      this.rootElm = var1;
   }

   public InputSource resolveEntity(String var1, String var2) throws SAXException, IOException {
      if (var2.equals("http://java.sun.com/dtd/properties.dtd")) {
         InputSource var3 = new InputSource(new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- DTD for properties --><!ELEMENT properties ( comment?, entry* ) ><!ATTLIST properties version CDATA #FIXED \"1.0\"><!ELEMENT comment (#PCDATA) ><!ELEMENT entry (#PCDATA) ><!ATTLIST entry  key CDATA #REQUIRED>"));
         var3.setSystemId("http://java.sun.com/dtd/properties.dtd");
         return var3;
      } else {
         throw new SAXException("Invalid system identifier: " + var2);
      }
   }

   public void error(SAXParseException var1) throws SAXException {
      throw var1;
   }

   public void fatalError(SAXParseException var1) throws SAXException {
      throw var1;
   }

   public void warning(SAXParseException var1) throws SAXException {
      throw var1;
   }
}
