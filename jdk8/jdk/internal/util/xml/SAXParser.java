package jdk.internal.util.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import jdk.internal.org.xml.sax.InputSource;
import jdk.internal.org.xml.sax.SAXException;
import jdk.internal.org.xml.sax.XMLReader;
import jdk.internal.org.xml.sax.helpers.DefaultHandler;

public abstract class SAXParser {
   protected SAXParser() {
   }

   public void parse(InputStream var1, DefaultHandler var2) throws SAXException, IOException {
      if (var1 == null) {
         throw new IllegalArgumentException("InputStream cannot be null");
      } else {
         InputSource var3 = new InputSource(var1);
         this.parse(var3, var2);
      }
   }

   public void parse(String var1, DefaultHandler var2) throws SAXException, IOException {
      if (var1 == null) {
         throw new IllegalArgumentException("uri cannot be null");
      } else {
         InputSource var3 = new InputSource(var1);
         this.parse(var3, var2);
      }
   }

   public void parse(File var1, DefaultHandler var2) throws SAXException, IOException {
      if (var1 == null) {
         throw new IllegalArgumentException("File cannot be null");
      } else {
         InputSource var3 = new InputSource(var1.toURI().toASCIIString());
         this.parse(var3, var2);
      }
   }

   public void parse(InputSource var1, DefaultHandler var2) throws SAXException, IOException {
      if (var1 == null) {
         throw new IllegalArgumentException("InputSource cannot be null");
      } else {
         XMLReader var3 = this.getXMLReader();
         if (var2 != null) {
            var3.setContentHandler(var2);
            var3.setEntityResolver(var2);
            var3.setErrorHandler(var2);
            var3.setDTDHandler(var2);
         }

         var3.parse(var1);
      }
   }

   public abstract XMLReader getXMLReader() throws SAXException;

   public abstract boolean isNamespaceAware();

   public abstract boolean isValidating();

   public boolean isXIncludeAware() {
      throw new UnsupportedOperationException("This parser does not support specification \"" + this.getClass().getPackage().getSpecificationTitle() + "\" version \"" + this.getClass().getPackage().getSpecificationVersion() + "\"");
   }
}
