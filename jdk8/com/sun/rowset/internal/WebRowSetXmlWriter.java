package com.sun.rowset.internal;

import com.sun.rowset.JdbcRowSetResourceBundle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import javax.sql.RowSet;
import javax.sql.RowSetInternal;
import javax.sql.rowset.WebRowSet;
import javax.sql.rowset.spi.XmlWriter;

public class WebRowSetXmlWriter implements XmlWriter, Serializable {
   private transient Writer writer;
   private Stack<String> stack;
   private JdbcRowSetResourceBundle resBundle;
   static final long serialVersionUID = 7163134986189677641L;

   public WebRowSetXmlWriter() {
      try {
         this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }
   }

   public void writeXML(WebRowSet var1, Writer var2) throws SQLException {
      this.stack = new Stack();
      this.writer = var2;
      this.writeRowSet(var1);
   }

   public void writeXML(WebRowSet var1, OutputStream var2) throws SQLException {
      this.stack = new Stack();
      this.writer = new OutputStreamWriter(var2);
      this.writeRowSet(var1);
   }

   private void writeRowSet(WebRowSet var1) throws SQLException {
      try {
         this.startHeader();
         this.writeProperties(var1);
         this.writeMetaData(var1);
         this.writeData(var1);
         this.endHeader();
      } catch (IOException var3) {
         throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("wrsxmlwriter.ioex").toString(), var3.getMessage()));
      }
   }

   private void startHeader() throws IOException {
      this.setTag("webRowSet");
      this.writer.write("<?xml version=\"1.0\"?>\n");
      this.writer.write("<webRowSet xmlns=\"http://java.sun.com/xml/ns/jdbc\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
      this.writer.write("xsi:schemaLocation=\"http://java.sun.com/xml/ns/jdbc http://java.sun.com/xml/ns/jdbc/webrowset.xsd\">\n");
   }

   private void endHeader() throws IOException {
      this.endTag("webRowSet");
   }

   private void writeProperties(WebRowSet var1) throws IOException {
      this.beginSection("properties");

      try {
         this.propString("command", this.processSpecialCharacters(var1.getCommand()));
         this.propInteger("concurrency", var1.getConcurrency());
         this.propString("datasource", var1.getDataSourceName());
         this.propBoolean("escape-processing", var1.getEscapeProcessing());

         try {
            this.propInteger("fetch-direction", var1.getFetchDirection());
         } catch (SQLException var8) {
         }

         this.propInteger("fetch-size", var1.getFetchSize());
         this.propInteger("isolation-level", var1.getTransactionIsolation());
         this.beginSection("key-columns");
         int[] var2 = var1.getKeyColumns();

         for(int var3 = 0; var2 != null && var3 < var2.length; ++var3) {
            this.propInteger("column", var2[var3]);
         }

         this.endSection("key-columns");
         this.beginSection("map");
         Map var10 = var1.getTypeMap();
         if (var10 != null) {
            Iterator var4 = var10.entrySet().iterator();

            while(var4.hasNext()) {
               Map.Entry var5 = (Map.Entry)var4.next();
               this.propString("type", (String)var5.getKey());
               this.propString("class", ((Class)var5.getValue()).getName());
            }
         }

         this.endSection("map");
         this.propInteger("max-field-size", var1.getMaxFieldSize());
         this.propInteger("max-rows", var1.getMaxRows());
         this.propInteger("query-timeout", var1.getQueryTimeout());
         this.propBoolean("read-only", var1.isReadOnly());
         int var11 = var1.getType();
         String var12 = "";
         if (var11 == 1003) {
            var12 = "ResultSet.TYPE_FORWARD_ONLY";
         } else if (var11 == 1004) {
            var12 = "ResultSet.TYPE_SCROLL_INSENSITIVE";
         } else if (var11 == 1005) {
            var12 = "ResultSet.TYPE_SCROLL_SENSITIVE";
         }

         this.propString("rowset-type", var12);
         this.propBoolean("show-deleted", var1.getShowDeleted());
         this.propString("table-name", var1.getTableName());
         this.propString("url", var1.getUrl());
         this.beginSection("sync-provider");
         String var6 = var1.getSyncProvider().toString();
         String var7 = var6.substring(0, var1.getSyncProvider().toString().indexOf("@"));
         this.propString("sync-provider-name", var7);
         this.propString("sync-provider-vendor", "Oracle Corporation");
         this.propString("sync-provider-version", "1.0");
         this.propInteger("sync-provider-grade", var1.getSyncProvider().getProviderGrade());
         this.propInteger("data-source-lock", var1.getSyncProvider().getDataSourceLock());
         this.endSection("sync-provider");
      } catch (SQLException var9) {
         throw new IOException(MessageFormat.format(this.resBundle.handleGetObject("wrsxmlwriter.sqlex").toString(), var9.getMessage()));
      }

      this.endSection("properties");
   }

   private void writeMetaData(WebRowSet var1) throws IOException {
      this.beginSection("metadata");

      try {
         ResultSetMetaData var3 = var1.getMetaData();
         int var2 = var3.getColumnCount();
         this.propInteger("column-count", var2);

         for(int var4 = 1; var4 <= var2; ++var4) {
            this.beginSection("column-definition");
            this.propInteger("column-index", var4);
            this.propBoolean("auto-increment", var3.isAutoIncrement(var4));
            this.propBoolean("case-sensitive", var3.isCaseSensitive(var4));
            this.propBoolean("currency", var3.isCurrency(var4));
            this.propInteger("nullable", var3.isNullable(var4));
            this.propBoolean("signed", var3.isSigned(var4));
            this.propBoolean("searchable", var3.isSearchable(var4));
            this.propInteger("column-display-size", var3.getColumnDisplaySize(var4));
            this.propString("column-label", var3.getColumnLabel(var4));
            this.propString("column-name", var3.getColumnName(var4));
            this.propString("schema-name", var3.getSchemaName(var4));
            this.propInteger("column-precision", var3.getPrecision(var4));
            this.propInteger("column-scale", var3.getScale(var4));
            this.propString("table-name", var3.getTableName(var4));
            this.propString("catalog-name", var3.getCatalogName(var4));
            this.propInteger("column-type", var3.getColumnType(var4));
            this.propString("column-type-name", var3.getColumnTypeName(var4));
            this.endSection("column-definition");
         }
      } catch (SQLException var5) {
         throw new IOException(MessageFormat.format(this.resBundle.handleGetObject("wrsxmlwriter.sqlex").toString(), var5.getMessage()));
      }

      this.endSection("metadata");
   }

   private void writeData(WebRowSet var1) throws IOException {
      try {
         ResultSetMetaData var3 = var1.getMetaData();
         int var4 = var3.getColumnCount();
         this.beginSection("data");
         var1.beforeFirst();
         var1.setShowDeleted(true);

         while(var1.next()) {
            if (var1.rowDeleted() && var1.rowInserted()) {
               this.beginSection("modifyRow");
            } else if (var1.rowDeleted()) {
               this.beginSection("deleteRow");
            } else if (var1.rowInserted()) {
               this.beginSection("insertRow");
            } else {
               this.beginSection("currentRow");
            }

            for(int var5 = 1; var5 <= var4; ++var5) {
               if (var1.columnUpdated(var5)) {
                  ResultSet var2 = var1.getOriginalRow();
                  var2.next();
                  this.beginTag("columnValue");
                  this.writeValue(var5, (RowSet)var2);
                  this.endTag("columnValue");
                  this.beginTag("updateRow");
                  this.writeValue(var5, var1);
                  this.endTag("updateRow");
               } else {
                  this.beginTag("columnValue");
                  this.writeValue(var5, var1);
                  this.endTag("columnValue");
               }
            }

            this.endSection();
         }

         this.endSection("data");
      } catch (SQLException var6) {
         throw new IOException(MessageFormat.format(this.resBundle.handleGetObject("wrsxmlwriter.sqlex").toString(), var6.getMessage()));
      }
   }

   private void writeValue(int var1, RowSet var2) throws IOException {
      try {
         int var3 = var2.getMetaData().getColumnType(var1);
         switch(var3) {
         case -7:
         case 16:
            boolean var4 = var2.getBoolean(var1);
            if (var2.wasNull()) {
               this.writeNull();
            } else {
               this.writeBoolean(var4);
            }
            break;
         case -6:
         case 5:
            short var5 = var2.getShort(var1);
            if (var2.wasNull()) {
               this.writeNull();
            } else {
               this.writeShort(var5);
            }
            break;
         case -5:
            long var7 = var2.getLong(var1);
            if (var2.wasNull()) {
               this.writeNull();
            } else {
               this.writeLong(var7);
            }
         case -4:
         case -3:
         case -2:
            break;
         case -1:
         case 1:
         case 12:
            this.writeStringData(var2.getString(var1));
            break;
         case 2:
         case 3:
            this.writeBigDecimal(var2.getBigDecimal(var1));
            break;
         case 4:
            int var6 = var2.getInt(var1);
            if (var2.wasNull()) {
               this.writeNull();
            } else {
               this.writeInteger(var6);
            }
            break;
         case 6:
         case 7:
            float var9 = var2.getFloat(var1);
            if (var2.wasNull()) {
               this.writeNull();
            } else {
               this.writeFloat(var9);
            }
            break;
         case 8:
            double var10 = var2.getDouble(var1);
            if (var2.wasNull()) {
               this.writeNull();
            } else {
               this.writeDouble(var10);
            }
            break;
         case 91:
            Date var12 = var2.getDate(var1);
            if (var2.wasNull()) {
               this.writeNull();
            } else {
               this.writeLong(var12.getTime());
            }
            break;
         case 92:
            Time var13 = var2.getTime(var1);
            if (var2.wasNull()) {
               this.writeNull();
            } else {
               this.writeLong(var13.getTime());
            }
            break;
         case 93:
            Timestamp var14 = var2.getTimestamp(var1);
            if (var2.wasNull()) {
               this.writeNull();
            } else {
               this.writeLong(var14.getTime());
            }
            break;
         default:
            System.out.println(this.resBundle.handleGetObject("wsrxmlwriter.notproper").toString());
         }

      } catch (SQLException var15) {
         throw new IOException(this.resBundle.handleGetObject("wrsxmlwriter.failedwrite").toString() + var15.getMessage());
      }
   }

   private void beginSection(String var1) throws IOException {
      this.setTag(var1);
      this.writeIndent(this.stack.size());
      this.writer.write("<" + var1 + ">\n");
   }

   private void endSection(String var1) throws IOException {
      this.writeIndent(this.stack.size());
      String var2 = this.getTag();
      if (var2.indexOf("webRowSet") != -1) {
         var2 = "webRowSet";
      }

      if (var1.equals(var2)) {
         this.writer.write("</" + var2 + ">\n");
      }

      this.writer.flush();
   }

   private void endSection() throws IOException {
      this.writeIndent(this.stack.size());
      String var1 = this.getTag();
      this.writer.write("</" + var1 + ">\n");
      this.writer.flush();
   }

   private void beginTag(String var1) throws IOException {
      this.setTag(var1);
      this.writeIndent(this.stack.size());
      this.writer.write("<" + var1 + ">");
   }

   private void endTag(String var1) throws IOException {
      String var2 = this.getTag();
      if (var1.equals(var2)) {
         this.writer.write("</" + var2 + ">\n");
      }

      this.writer.flush();
   }

   private void emptyTag(String var1) throws IOException {
      this.writer.write("<" + var1 + "/>");
   }

   private void setTag(String var1) {
      this.stack.push(var1);
   }

   private String getTag() {
      return (String)this.stack.pop();
   }

   private void writeNull() throws IOException {
      this.emptyTag("null");
   }

   private void writeStringData(String var1) throws IOException {
      if (var1 == null) {
         this.writeNull();
      } else if (var1.equals("")) {
         this.writeEmptyString();
      } else {
         var1 = this.processSpecialCharacters(var1);
         this.writer.write(var1);
      }

   }

   private void writeString(String var1) throws IOException {
      if (var1 != null) {
         this.writer.write(var1);
      } else {
         this.writeNull();
      }

   }

   private void writeShort(short var1) throws IOException {
      this.writer.write(Short.toString(var1));
   }

   private void writeLong(long var1) throws IOException {
      this.writer.write(Long.toString(var1));
   }

   private void writeInteger(int var1) throws IOException {
      this.writer.write(Integer.toString(var1));
   }

   private void writeBoolean(boolean var1) throws IOException {
      this.writer.write(Boolean.valueOf(var1).toString());
   }

   private void writeFloat(float var1) throws IOException {
      this.writer.write(Float.toString(var1));
   }

   private void writeDouble(double var1) throws IOException {
      this.writer.write(Double.toString(var1));
   }

   private void writeBigDecimal(BigDecimal var1) throws IOException {
      if (var1 != null) {
         this.writer.write(var1.toString());
      } else {
         this.emptyTag("null");
      }

   }

   private void writeIndent(int var1) throws IOException {
      for(int var2 = 1; var2 < var1; ++var2) {
         this.writer.write("  ");
      }

   }

   private void propString(String var1, String var2) throws IOException {
      this.beginTag(var1);
      this.writeString(var2);
      this.endTag(var1);
   }

   private void propInteger(String var1, int var2) throws IOException {
      this.beginTag(var1);
      this.writeInteger(var2);
      this.endTag(var1);
   }

   private void propBoolean(String var1, boolean var2) throws IOException {
      this.beginTag(var1);
      this.writeBoolean(var2);
      this.endTag(var1);
   }

   private void writeEmptyString() throws IOException {
      this.emptyTag("emptyString");
   }

   public boolean writeData(RowSetInternal var1) {
      return false;
   }

   private String processSpecialCharacters(String var1) {
      if (var1 == null) {
         return null;
      } else {
         char[] var2 = var1.toCharArray();
         String var3 = "";

         for(int var4 = 0; var4 < var2.length; ++var4) {
            if (var2[var4] == '&') {
               var3 = var3.concat("&amp;");
            } else if (var2[var4] == '<') {
               var3 = var3.concat("&lt;");
            } else if (var2[var4] == '>') {
               var3 = var3.concat("&gt;");
            } else if (var2[var4] == '\'') {
               var3 = var3.concat("&apos;");
            } else if (var2[var4] == '"') {
               var3 = var3.concat("&quot;");
            } else {
               var3 = var3.concat(String.valueOf(var2[var4]));
            }
         }

         return var3;
      }
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
