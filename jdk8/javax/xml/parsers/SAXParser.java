package javax.xml.parsers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.validation.Schema;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public abstract class SAXParser {
   protected SAXParser() {
   }

   public void reset() {
      throw new UnsupportedOperationException("This SAXParser, \"" + this.getClass().getName() + "\", does not support the reset functionality.  Specification \"" + this.getClass().getPackage().getSpecificationTitle() + "\" version \"" + this.getClass().getPackage().getSpecificationVersion() + "\"");
   }

   public void parse(InputStream is, HandlerBase hb) throws SAXException, IOException {
      if (is == null) {
         throw new IllegalArgumentException("InputStream cannot be null");
      } else {
         InputSource input = new InputSource(is);
         this.parse(input, hb);
      }
   }

   public void parse(InputStream is, HandlerBase hb, String systemId) throws SAXException, IOException {
      if (is == null) {
         throw new IllegalArgumentException("InputStream cannot be null");
      } else {
         InputSource input = new InputSource(is);
         input.setSystemId(systemId);
         this.parse(input, hb);
      }
   }

   public void parse(InputStream is, DefaultHandler dh) throws SAXException, IOException {
      if (is == null) {
         throw new IllegalArgumentException("InputStream cannot be null");
      } else {
         InputSource input = new InputSource(is);
         this.parse(input, dh);
      }
   }

   public void parse(InputStream is, DefaultHandler dh, String systemId) throws SAXException, IOException {
      if (is == null) {
         throw new IllegalArgumentException("InputStream cannot be null");
      } else {
         InputSource input = new InputSource(is);
         input.setSystemId(systemId);
         this.parse(input, dh);
      }
   }

   public void parse(String uri, HandlerBase hb) throws SAXException, IOException {
      if (uri == null) {
         throw new IllegalArgumentException("uri cannot be null");
      } else {
         InputSource input = new InputSource(uri);
         this.parse(input, hb);
      }
   }

   public void parse(String uri, DefaultHandler dh) throws SAXException, IOException {
      if (uri == null) {
         throw new IllegalArgumentException("uri cannot be null");
      } else {
         InputSource input = new InputSource(uri);
         this.parse(input, dh);
      }
   }

   public void parse(File f, HandlerBase hb) throws SAXException, IOException {
      if (f == null) {
         throw new IllegalArgumentException("File cannot be null");
      } else {
         InputSource input = new InputSource(f.toURI().toASCIIString());
         this.parse(input, hb);
      }
   }

   public void parse(File f, DefaultHandler dh) throws SAXException, IOException {
      if (f == null) {
         throw new IllegalArgumentException("File cannot be null");
      } else {
         InputSource input = new InputSource(f.toURI().toASCIIString());
         this.parse(input, dh);
      }
   }

   public void parse(InputSource is, HandlerBase hb) throws SAXException, IOException {
      if (is == null) {
         throw new IllegalArgumentException("InputSource cannot be null");
      } else {
         Parser parser = this.getParser();
         if (hb != null) {
            parser.setDocumentHandler(hb);
            parser.setEntityResolver(hb);
            parser.setErrorHandler(hb);
            parser.setDTDHandler(hb);
         }

         parser.parse(is);
      }
   }

   public void parse(InputSource is, DefaultHandler dh) throws SAXException, IOException {
      if (is == null) {
         throw new IllegalArgumentException("InputSource cannot be null");
      } else {
         XMLReader reader = this.getXMLReader();
         if (dh != null) {
            reader.setContentHandler(dh);
            reader.setEntityResolver(dh);
            reader.setErrorHandler(dh);
            reader.setDTDHandler(dh);
         }

         reader.parse(is);
      }
   }

   public abstract Parser getParser() throws SAXException;

   public abstract XMLReader getXMLReader() throws SAXException;

   public abstract boolean isNamespaceAware();

   public abstract boolean isValidating();

   public abstract void setProperty(String var1, Object var2) throws SAXNotRecognizedException, SAXNotSupportedException;

   public abstract Object getProperty(String var1) throws SAXNotRecognizedException, SAXNotSupportedException;

   public Schema getSchema() {
      throw new UnsupportedOperationException("This parser does not support specification \"" + this.getClass().getPackage().getSpecificationTitle() + "\" version \"" + this.getClass().getPackage().getSpecificationVersion() + "\"");
   }

   public boolean isXIncludeAware() {
      throw new UnsupportedOperationException("This parser does not support specification \"" + this.getClass().getPackage().getSpecificationTitle() + "\" version \"" + this.getClass().getPackage().getSpecificationVersion() + "\"");
   }
}
