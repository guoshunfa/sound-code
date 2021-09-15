package com.sun.rowset.internal;

import com.sun.rowset.JdbcRowSetResourceBundle;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.MessageFormat;
import javax.sql.RowSetInternal;
import javax.sql.rowset.WebRowSet;
import javax.sql.rowset.spi.XmlReader;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public class WebRowSetXmlReader implements XmlReader, Serializable {
   private JdbcRowSetResourceBundle resBundle;
   static final long serialVersionUID = -9127058392819008014L;

   public WebRowSetXmlReader() {
      try {
         this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }
   }

   public void readXML(WebRowSet var1, Reader var2) throws SQLException {
      try {
         InputSource var3 = new InputSource(var2);
         XmlErrorHandler var13 = new XmlErrorHandler();
         XmlReaderContentHandler var5 = new XmlReaderContentHandler(var1);
         SAXParserFactory var6 = SAXParserFactory.newInstance();
         var6.setNamespaceAware(true);
         var6.setValidating(true);
         SAXParser var7 = var6.newSAXParser();
         var7.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
         XMLReader var8 = var7.getXMLReader();
         var8.setEntityResolver(new XmlResolver());
         var8.setContentHandler(var5);
         var8.setErrorHandler(var13);
         var8.parse(var3);
      } catch (SAXParseException var9) {
         System.out.println(MessageFormat.format(this.resBundle.handleGetObject("wrsxmlreader.parseerr").toString(), var9.getMessage(), var9.getLineNumber(), var9.getSystemId()));
         var9.printStackTrace();
         throw new SQLException(var9.getMessage());
      } catch (SAXException var10) {
         Object var4 = var10;
         if (var10.getException() != null) {
            var4 = var10.getException();
         }

         ((Exception)var4).printStackTrace();
         throw new SQLException(((Exception)var4).getMessage());
      } catch (ArrayIndexOutOfBoundsException var11) {
         throw new SQLException(this.resBundle.handleGetObject("wrsxmlreader.invalidcp").toString());
      } catch (Throwable var12) {
         throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("wrsxmlreader.readxml").toString(), var12.getMessage()));
      }
   }

   public void readXML(WebRowSet var1, InputStream var2) throws SQLException {
      try {
         InputSource var3 = new InputSource(var2);
         XmlErrorHandler var13 = new XmlErrorHandler();
         XmlReaderContentHandler var5 = new XmlReaderContentHandler(var1);
         SAXParserFactory var6 = SAXParserFactory.newInstance();
         var6.setNamespaceAware(true);
         var6.setValidating(true);
         SAXParser var7 = var6.newSAXParser();
         var7.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
         XMLReader var8 = var7.getXMLReader();
         var8.setEntityResolver(new XmlResolver());
         var8.setContentHandler(var5);
         var8.setErrorHandler(var13);
         var8.parse(var3);
      } catch (SAXParseException var9) {
         System.out.println(MessageFormat.format(this.resBundle.handleGetObject("wrsxmlreader.parseerr").toString(), var9.getLineNumber(), var9.getSystemId()));
         System.out.println("   " + var9.getMessage());
         var9.printStackTrace();
         throw new SQLException(var9.getMessage());
      } catch (SAXException var10) {
         Object var4 = var10;
         if (var10.getException() != null) {
            var4 = var10.getException();
         }

         ((Exception)var4).printStackTrace();
         throw new SQLException(((Exception)var4).getMessage());
      } catch (ArrayIndexOutOfBoundsException var11) {
         throw new SQLException(this.resBundle.handleGetObject("wrsxmlreader.invalidcp").toString());
      } catch (Throwable var12) {
         throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("wrsxmlreader.readxml").toString(), var12.getMessage()));
      }
   }

   public void readData(RowSetInternal var1) {
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();

      try {
         this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }
}
