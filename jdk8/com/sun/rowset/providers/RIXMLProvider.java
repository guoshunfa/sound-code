package com.sun.rowset.providers;

import com.sun.rowset.JdbcRowSetResourceBundle;
import java.io.IOException;
import java.sql.SQLException;
import javax.sql.RowSetReader;
import javax.sql.RowSetWriter;
import javax.sql.rowset.spi.SyncProvider;
import javax.sql.rowset.spi.SyncProviderException;
import javax.sql.rowset.spi.XmlReader;
import javax.sql.rowset.spi.XmlWriter;

public final class RIXMLProvider extends SyncProvider {
   private String providerID = "com.sun.rowset.providers.RIXMLProvider";
   private String vendorName = "Oracle Corporation";
   private String versionNumber = "1.0";
   private JdbcRowSetResourceBundle resBundle;
   private XmlReader xmlReader;
   private XmlWriter xmlWriter;

   public RIXMLProvider() {
      this.providerID = this.getClass().getName();

      try {
         this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }
   }

   public String getProviderID() {
      return this.providerID;
   }

   public void setXmlReader(XmlReader var1) throws SQLException {
      this.xmlReader = var1;
   }

   public void setXmlWriter(XmlWriter var1) throws SQLException {
      this.xmlWriter = var1;
   }

   public XmlReader getXmlReader() throws SQLException {
      return this.xmlReader;
   }

   public XmlWriter getXmlWriter() throws SQLException {
      return this.xmlWriter;
   }

   public int getProviderGrade() {
      return 1;
   }

   public int supportsUpdatableView() {
      return 6;
   }

   public int getDataSourceLock() throws SyncProviderException {
      return 1;
   }

   public void setDataSourceLock(int var1) throws SyncProviderException {
      throw new UnsupportedOperationException(this.resBundle.handleGetObject("rixml.unsupp").toString());
   }

   public RowSetWriter getRowSetWriter() {
      return null;
   }

   public RowSetReader getRowSetReader() {
      return null;
   }

   public String getVersion() {
      return this.versionNumber;
   }

   public String getVendor() {
      return this.vendorName;
   }
}
