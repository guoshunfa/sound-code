package com.sun.rowset;

import com.sun.rowset.internal.WebRowSetXmlReader;
import com.sun.rowset.internal.WebRowSetXmlWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import javax.sql.rowset.WebRowSet;
import javax.sql.rowset.spi.SyncFactory;
import javax.sql.rowset.spi.SyncProvider;

public class WebRowSetImpl extends CachedRowSetImpl implements WebRowSet {
   private WebRowSetXmlReader xmlReader;
   private WebRowSetXmlWriter xmlWriter;
   private int curPosBfrWrite;
   private SyncProvider provider;
   static final long serialVersionUID = -8771775154092422943L;

   public WebRowSetImpl() throws SQLException {
      this.xmlReader = new WebRowSetXmlReader();
      this.xmlWriter = new WebRowSetXmlWriter();
   }

   public WebRowSetImpl(Hashtable var1) throws SQLException {
      try {
         this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }

      if (var1 == null) {
         throw new SQLException(this.resBundle.handleGetObject("webrowsetimpl.nullhash").toString());
      } else {
         String var2 = (String)var1.get("rowset.provider.classname");
         this.provider = SyncFactory.getInstance(var2);
      }
   }

   public void writeXml(ResultSet var1, Writer var2) throws SQLException {
      this.populate(var1);
      this.curPosBfrWrite = this.getRow();
      this.writeXml(var2);
   }

   public void writeXml(Writer var1) throws SQLException {
      if (this.xmlWriter != null) {
         this.curPosBfrWrite = this.getRow();
         this.xmlWriter.writeXML(this, (Writer)var1);
      } else {
         throw new SQLException(this.resBundle.handleGetObject("webrowsetimpl.invalidwr").toString());
      }
   }

   public void readXml(Reader var1) throws SQLException {
      try {
         if (var1 != null) {
            this.xmlReader.readXML(this, (Reader)var1);
            if (this.curPosBfrWrite == 0) {
               this.beforeFirst();
            } else {
               this.absolute(this.curPosBfrWrite);
            }

         } else {
            throw new SQLException(this.resBundle.handleGetObject("webrowsetimpl.invalidrd").toString());
         }
      } catch (Exception var3) {
         throw new SQLException(var3.getMessage());
      }
   }

   public void readXml(InputStream var1) throws SQLException, IOException {
      if (var1 != null) {
         this.xmlReader.readXML(this, (InputStream)var1);
         if (this.curPosBfrWrite == 0) {
            this.beforeFirst();
         } else {
            this.absolute(this.curPosBfrWrite);
         }

      } else {
         throw new SQLException(this.resBundle.handleGetObject("webrowsetimpl.invalidrd").toString());
      }
   }

   public void writeXml(OutputStream var1) throws SQLException, IOException {
      if (this.xmlWriter != null) {
         this.curPosBfrWrite = this.getRow();
         this.xmlWriter.writeXML(this, (OutputStream)var1);
      } else {
         throw new SQLException(this.resBundle.handleGetObject("webrowsetimpl.invalidwr").toString());
      }
   }

   public void writeXml(ResultSet var1, OutputStream var2) throws SQLException, IOException {
      this.populate(var1);
      this.curPosBfrWrite = this.getRow();
      this.writeXml(var2);
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
