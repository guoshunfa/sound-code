package com.sun.rowset.internal;

import com.sun.rowset.JdbcRowSetResourceBundle;
import com.sun.rowset.WebRowSetImpl;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import javax.sql.RowSet;
import javax.sql.RowSetMetaData;
import javax.sql.rowset.RowSetMetaDataImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import sun.reflect.misc.ReflectUtil;

public class XmlReaderContentHandler extends DefaultHandler {
   private HashMap<String, Integer> propMap;
   private HashMap<String, Integer> colDefMap;
   private HashMap<String, Integer> dataMap;
   private HashMap<String, Class<?>> typeMap;
   private Vector<Object[]> updates;
   private Vector<String> keyCols;
   private String columnValue;
   private String propertyValue;
   private String metaDataValue;
   private int tag;
   private int state;
   private WebRowSetImpl rs;
   private boolean nullVal;
   private boolean emptyStringVal;
   private RowSetMetaData md;
   private int idx;
   private String lastval;
   private String Key_map;
   private String Value_map;
   private String tempStr;
   private String tempUpdate;
   private String tempCommand;
   private Object[] upd;
   private String[] properties = new String[]{"command", "concurrency", "datasource", "escape-processing", "fetch-direction", "fetch-size", "isolation-level", "key-columns", "map", "max-field-size", "max-rows", "query-timeout", "read-only", "rowset-type", "show-deleted", "table-name", "url", "null", "column", "type", "class", "sync-provider", "sync-provider-name", "sync-provider-vendor", "sync-provider-version", "sync-provider-grade", "data-source-lock"};
   private static final int CommandTag = 0;
   private static final int ConcurrencyTag = 1;
   private static final int DatasourceTag = 2;
   private static final int EscapeProcessingTag = 3;
   private static final int FetchDirectionTag = 4;
   private static final int FetchSizeTag = 5;
   private static final int IsolationLevelTag = 6;
   private static final int KeycolsTag = 7;
   private static final int MapTag = 8;
   private static final int MaxFieldSizeTag = 9;
   private static final int MaxRowsTag = 10;
   private static final int QueryTimeoutTag = 11;
   private static final int ReadOnlyTag = 12;
   private static final int RowsetTypeTag = 13;
   private static final int ShowDeletedTag = 14;
   private static final int TableNameTag = 15;
   private static final int UrlTag = 16;
   private static final int PropNullTag = 17;
   private static final int PropColumnTag = 18;
   private static final int PropTypeTag = 19;
   private static final int PropClassTag = 20;
   private static final int SyncProviderTag = 21;
   private static final int SyncProviderNameTag = 22;
   private static final int SyncProviderVendorTag = 23;
   private static final int SyncProviderVersionTag = 24;
   private static final int SyncProviderGradeTag = 25;
   private static final int DataSourceLock = 26;
   private String[] colDef = new String[]{"column-count", "column-definition", "column-index", "auto-increment", "case-sensitive", "currency", "nullable", "signed", "searchable", "column-display-size", "column-label", "column-name", "schema-name", "column-precision", "column-scale", "table-name", "catalog-name", "column-type", "column-type-name", "null"};
   private static final int ColumnCountTag = 0;
   private static final int ColumnDefinitionTag = 1;
   private static final int ColumnIndexTag = 2;
   private static final int AutoIncrementTag = 3;
   private static final int CaseSensitiveTag = 4;
   private static final int CurrencyTag = 5;
   private static final int NullableTag = 6;
   private static final int SignedTag = 7;
   private static final int SearchableTag = 8;
   private static final int ColumnDisplaySizeTag = 9;
   private static final int ColumnLabelTag = 10;
   private static final int ColumnNameTag = 11;
   private static final int SchemaNameTag = 12;
   private static final int ColumnPrecisionTag = 13;
   private static final int ColumnScaleTag = 14;
   private static final int MetaTableNameTag = 15;
   private static final int CatalogNameTag = 16;
   private static final int ColumnTypeTag = 17;
   private static final int ColumnTypeNameTag = 18;
   private static final int MetaNullTag = 19;
   private String[] data = new String[]{"currentRow", "columnValue", "insertRow", "deleteRow", "insdel", "updateRow", "null", "emptyString"};
   private static final int RowTag = 0;
   private static final int ColTag = 1;
   private static final int InsTag = 2;
   private static final int DelTag = 3;
   private static final int InsDelTag = 4;
   private static final int UpdTag = 5;
   private static final int NullTag = 6;
   private static final int EmptyStringTag = 7;
   private static final int INITIAL = 0;
   private static final int PROPERTIES = 1;
   private static final int METADATA = 2;
   private static final int DATA = 3;
   private JdbcRowSetResourceBundle resBundle;

   public XmlReaderContentHandler(RowSet var1) {
      this.rs = (WebRowSetImpl)var1;
      this.initMaps();
      this.updates = new Vector();
      this.columnValue = "";
      this.propertyValue = "";
      this.metaDataValue = "";
      this.nullVal = false;
      this.idx = 0;
      this.tempStr = "";
      this.tempUpdate = "";
      this.tempCommand = "";

      try {
         this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   private void initMaps() {
      this.propMap = new HashMap();
      int var1 = this.properties.length;

      int var2;
      for(var2 = 0; var2 < var1; ++var2) {
         this.propMap.put(this.properties[var2], var2);
      }

      this.colDefMap = new HashMap();
      var1 = this.colDef.length;

      for(var2 = 0; var2 < var1; ++var2) {
         this.colDefMap.put(this.colDef[var2], var2);
      }

      this.dataMap = new HashMap();
      var1 = this.data.length;

      for(var2 = 0; var2 < var1; ++var2) {
         this.dataMap.put(this.data[var2], var2);
      }

      this.typeMap = new HashMap();
   }

   public void startDocument() throws SAXException {
   }

   public void endDocument() throws SAXException {
   }

   public void startElement(String var1, String var2, String var3, Attributes var4) throws SAXException {
      String var6 = "";
      int var5;
      switch(this.getState()) {
      case 1:
         this.tempCommand = "";
         var5 = (Integer)this.propMap.get(var2);
         if (var5 == 17) {
            this.setNullValue(true);
         } else {
            this.setTag(var5);
         }
         break;
      case 2:
         var5 = (Integer)this.colDefMap.get(var2);
         if (var5 == 19) {
            this.setNullValue(true);
         } else {
            this.setTag(var5);
         }
         break;
      case 3:
         this.tempStr = "";
         this.tempUpdate = "";
         if (this.dataMap.get(var2) == null) {
            var5 = 6;
         } else if ((Integer)this.dataMap.get(var2) == 7) {
            var5 = 7;
         } else {
            var5 = (Integer)this.dataMap.get(var2);
         }

         if (var5 == 6) {
            this.setNullValue(true);
         } else if (var5 == 7) {
            this.setEmptyStringValue(true);
         } else {
            this.setTag(var5);
            if (var5 == 0 || var5 == 3 || var5 == 2) {
               this.idx = 0;

               try {
                  this.rs.moveToInsertRow();
               } catch (SQLException var8) {
               }
            }
         }
         break;
      default:
         this.setState(var2);
      }

   }

   public void endElement(String var1, String var2, String var3) throws SAXException {
      String var5 = "";
      var5 = var2;
      int var4;
      switch(this.getState()) {
      case 1:
         if (var2.equals("properties")) {
            this.state = 0;
         } else {
            try {
               var4 = (Integer)this.propMap.get(var5);
               switch(var4) {
               case 7:
                  if (this.keyCols == null) {
                     break;
                  }

                  int[] var6 = new int[this.keyCols.size()];

                  for(int var7 = 0; var7 < var6.length; ++var7) {
                     var6[var7] = Integer.parseInt((String)this.keyCols.elementAt(var7));
                  }

                  this.rs.setKeyColumns(var6);
                  break;
               case 8:
                  this.rs.setTypeMap(this.typeMap);
                  break;
               case 20:
                  try {
                     this.typeMap.put(this.Key_map, ReflectUtil.forName(this.Value_map));
                  } catch (ClassNotFoundException var16) {
                     throw new SAXException(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.errmap").toString(), var16.getMessage()));
                  }
               }

               if (this.getNullValue()) {
                  this.setPropertyValue((String)null);
                  this.setNullValue(false);
               } else {
                  this.setPropertyValue(this.propertyValue);
               }
            } catch (SQLException var17) {
               throw new SAXException(var17.getMessage());
            }

            this.propertyValue = "";
            this.setTag(-1);
         }
         break;
      case 2:
         if (var2.equals("metadata")) {
            try {
               this.rs.setMetaData(this.md);
               this.state = 0;
            } catch (SQLException var15) {
               throw new SAXException(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.errmetadata").toString(), var15.getMessage()));
            }
         } else {
            try {
               if (this.getNullValue()) {
                  this.setMetaDataValue((String)null);
                  this.setNullValue(false);
               } else {
                  this.setMetaDataValue(this.metaDataValue);
               }
            } catch (SQLException var14) {
               throw new SAXException(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.errmetadata").toString(), var14.getMessage()));
            }

            this.metaDataValue = "";
         }

         this.setTag(-1);
         break;
      case 3:
         if (var2.equals("data")) {
            this.state = 0;
            return;
         }

         if (this.dataMap.get(var2) == null) {
            var4 = 6;
         } else {
            var4 = (Integer)this.dataMap.get(var2);
         }

         switch(var4) {
         case 0:
            try {
               this.rs.insertRow();
               this.rs.moveToCurrentRow();
               this.rs.next();
               this.rs.setOriginalRow();
               this.applyUpdates();
               break;
            } catch (SQLException var12) {
               throw new SAXException(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.errconstr").toString(), var12.getMessage()));
            }
         case 1:
            try {
               ++this.idx;
               if (this.getNullValue()) {
                  this.insertValue((String)null);
                  this.setNullValue(false);
               } else {
                  this.insertValue(this.tempStr);
               }

               this.columnValue = "";
               break;
            } catch (SQLException var13) {
               throw new SAXException(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.errinsertval").toString(), var13.getMessage()));
            }
         case 2:
            try {
               this.rs.insertRow();
               this.rs.moveToCurrentRow();
               this.rs.next();
               this.applyUpdates();
               break;
            } catch (SQLException var10) {
               throw new SAXException(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.errinsert").toString(), var10.getMessage()));
            }
         case 3:
            try {
               this.rs.insertRow();
               this.rs.moveToCurrentRow();
               this.rs.next();
               this.rs.setOriginalRow();
               this.applyUpdates();
               this.rs.deleteRow();
               break;
            } catch (SQLException var11) {
               throw new SAXException(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.errdel").toString(), var11.getMessage()));
            }
         case 4:
            try {
               this.rs.insertRow();
               this.rs.moveToCurrentRow();
               this.rs.next();
               this.rs.setOriginalRow();
               this.applyUpdates();
               break;
            } catch (SQLException var9) {
               throw new SAXException(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.errinsdel").toString(), var9.getMessage()));
            }
         case 5:
            try {
               if (this.getNullValue()) {
                  this.insertValue((String)null);
                  this.setNullValue(false);
               } else if (this.getEmptyStringValue()) {
                  this.insertValue("");
                  this.setEmptyStringValue(false);
               } else {
                  this.updates.add(this.upd);
               }
            } catch (SQLException var8) {
               throw new SAXException(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.errupdate").toString(), var8.getMessage()));
            }
         }
      }

   }

   private void applyUpdates() throws SAXException {
      if (this.updates.size() > 0) {
         try {
            Iterator var2 = this.updates.iterator();

            while(true) {
               if (!var2.hasNext()) {
                  this.rs.updateRow();
                  break;
               }

               Object[] var1 = (Object[])((Object[])var2.next());
               this.idx = (Integer)var1[0];
               if (!this.lastval.equals(var1[1])) {
                  this.insertValue((String)((String)var1[1]));
               }
            }
         } catch (SQLException var3) {
            throw new SAXException(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.errupdrow").toString(), var3.getMessage()));
         }

         this.updates.removeAllElements();
      }

   }

   public void characters(char[] var1, int var2, int var3) throws SAXException {
      try {
         switch(this.getState()) {
         case 1:
            this.propertyValue = new String(var1, var2, var3);
            this.tempCommand = this.tempCommand.concat(this.propertyValue);
            this.propertyValue = this.tempCommand;
            if (this.tag == 19) {
               this.Key_map = this.propertyValue;
            } else if (this.tag == 20) {
               this.Value_map = this.propertyValue;
            }
            break;
         case 2:
            if (this.tag != -1) {
               this.metaDataValue = new String(var1, var2, var3);
            }
            break;
         case 3:
            this.setDataValue(var1, var2, var3);
         }

      } catch (SQLException var5) {
         throw new SAXException(this.resBundle.handleGetObject("xmlrch.chars").toString() + var5.getMessage());
      }
   }

   private void setState(String var1) throws SAXException {
      if (var1.equals("webRowSet")) {
         this.state = 0;
      } else if (var1.equals("properties")) {
         if (this.state != 1) {
            this.state = 1;
         } else {
            this.state = 0;
         }
      } else if (var1.equals("metadata")) {
         if (this.state != 2) {
            this.state = 2;
         } else {
            this.state = 0;
         }
      } else if (var1.equals("data")) {
         if (this.state != 3) {
            this.state = 3;
         } else {
            this.state = 0;
         }
      }

   }

   private int getState() {
      return this.state;
   }

   private void setTag(int var1) {
      this.tag = var1;
   }

   private int getTag() {
      return this.tag;
   }

   private void setNullValue(boolean var1) {
      this.nullVal = var1;
   }

   private boolean getNullValue() {
      return this.nullVal;
   }

   private void setEmptyStringValue(boolean var1) {
      this.emptyStringVal = var1;
   }

   private boolean getEmptyStringValue() {
      return this.emptyStringVal;
   }

   private String getStringValue(String var1) {
      return var1;
   }

   private int getIntegerValue(String var1) {
      return Integer.parseInt(var1);
   }

   private boolean getBooleanValue(String var1) {
      return Boolean.valueOf(var1);
   }

   private BigDecimal getBigDecimalValue(String var1) {
      return new BigDecimal(var1);
   }

   private byte getByteValue(String var1) {
      return Byte.parseByte(var1);
   }

   private short getShortValue(String var1) {
      return Short.parseShort(var1);
   }

   private long getLongValue(String var1) {
      return Long.parseLong(var1);
   }

   private float getFloatValue(String var1) {
      return Float.parseFloat(var1);
   }

   private double getDoubleValue(String var1) {
      return Double.parseDouble(var1);
   }

   private byte[] getBinaryValue(String var1) {
      return var1.getBytes();
   }

   private Date getDateValue(String var1) {
      return new Date(this.getLongValue(var1));
   }

   private Time getTimeValue(String var1) {
      return new Time(this.getLongValue(var1));
   }

   private Timestamp getTimestampValue(String var1) {
      return new Timestamp(this.getLongValue(var1));
   }

   private void setPropertyValue(String var1) throws SQLException {
      boolean var2 = this.getNullValue();
      String var3;
      switch(this.getTag()) {
      case 0:
         if (!var2) {
            this.rs.setCommand(var1);
         }
         break;
      case 1:
         if (var2) {
            throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString());
         }

         this.rs.setConcurrency(this.getIntegerValue(var1));
         break;
      case 2:
         if (var2) {
            this.rs.setDataSourceName((String)null);
         } else {
            this.rs.setDataSourceName(var1);
         }
         break;
      case 3:
         if (var2) {
            throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString());
         }

         this.rs.setEscapeProcessing(this.getBooleanValue(var1));
         break;
      case 4:
         if (var2) {
            throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString());
         }

         this.rs.setFetchDirection(this.getIntegerValue(var1));
         break;
      case 5:
         if (var2) {
            throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString());
         }

         this.rs.setFetchSize(this.getIntegerValue(var1));
         break;
      case 6:
         if (var2) {
            throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString());
         }

         this.rs.setTransactionIsolation(this.getIntegerValue(var1));
      case 7:
      case 8:
      case 17:
      case 19:
      case 20:
      case 21:
      case 23:
      case 24:
      case 25:
      case 26:
      default:
         break;
      case 9:
         if (var2) {
            throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString());
         }

         this.rs.setMaxFieldSize(this.getIntegerValue(var1));
         break;
      case 10:
         if (var2) {
            throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString());
         }

         this.rs.setMaxRows(this.getIntegerValue(var1));
         break;
      case 11:
         if (var2) {
            throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString());
         }

         this.rs.setQueryTimeout(this.getIntegerValue(var1));
         break;
      case 12:
         if (var2) {
            throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString());
         }

         this.rs.setReadOnly(this.getBooleanValue(var1));
         break;
      case 13:
         if (var2) {
            throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString());
         }

         var3 = this.getStringValue(var1);
         short var4 = 0;
         if (var3.trim().equals("ResultSet.TYPE_SCROLL_INSENSITIVE")) {
            var4 = 1004;
         } else if (var3.trim().equals("ResultSet.TYPE_SCROLL_SENSITIVE")) {
            var4 = 1005;
         } else if (var3.trim().equals("ResultSet.TYPE_FORWARD_ONLY")) {
            var4 = 1003;
         }

         this.rs.setType(var4);
         break;
      case 14:
         if (var2) {
            throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString());
         }

         this.rs.setShowDeleted(this.getBooleanValue(var1));
         break;
      case 15:
         if (!var2) {
            this.rs.setTableName(var1);
         }
         break;
      case 16:
         if (var2) {
            this.rs.setUrl((String)null);
         } else {
            this.rs.setUrl(var1);
         }
         break;
      case 18:
         if (this.keyCols == null) {
            this.keyCols = new Vector();
         }

         this.keyCols.add(var1);
         break;
      case 22:
         if (var2) {
            this.rs.setSyncProvider((String)null);
         } else {
            var3 = var1.substring(0, var1.indexOf("@") + 1);
            this.rs.setSyncProvider(var3);
         }
      }

   }

   private void setMetaDataValue(String var1) throws SQLException {
      boolean var2 = this.getNullValue();
      switch(this.getTag()) {
      case 0:
         this.md = new RowSetMetaDataImpl();
         this.idx = 0;
         if (var2) {
            throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString());
         }

         this.md.setColumnCount(this.getIntegerValue(var1));
      case 1:
      default:
         break;
      case 2:
         ++this.idx;
         break;
      case 3:
         if (var2) {
            throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString());
         }

         this.md.setAutoIncrement(this.idx, this.getBooleanValue(var1));
         break;
      case 4:
         if (var2) {
            throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString());
         }

         this.md.setCaseSensitive(this.idx, this.getBooleanValue(var1));
         break;
      case 5:
         if (var2) {
            throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString());
         }

         this.md.setCurrency(this.idx, this.getBooleanValue(var1));
         break;
      case 6:
         if (var2) {
            throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString());
         }

         this.md.setNullable(this.idx, this.getIntegerValue(var1));
         break;
      case 7:
         if (var2) {
            throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString());
         }

         this.md.setSigned(this.idx, this.getBooleanValue(var1));
         break;
      case 8:
         if (var2) {
            throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString());
         }

         this.md.setSearchable(this.idx, this.getBooleanValue(var1));
         break;
      case 9:
         if (var2) {
            throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString());
         }

         this.md.setColumnDisplaySize(this.idx, this.getIntegerValue(var1));
         break;
      case 10:
         if (var2) {
            this.md.setColumnLabel(this.idx, (String)null);
         } else {
            this.md.setColumnLabel(this.idx, var1);
         }
         break;
      case 11:
         if (var2) {
            this.md.setColumnName(this.idx, (String)null);
         } else {
            this.md.setColumnName(this.idx, var1);
         }
         break;
      case 12:
         if (var2) {
            this.md.setSchemaName(this.idx, (String)null);
         } else {
            this.md.setSchemaName(this.idx, var1);
         }
         break;
      case 13:
         if (var2) {
            throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString());
         }

         this.md.setPrecision(this.idx, this.getIntegerValue(var1));
         break;
      case 14:
         if (var2) {
            throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString());
         }

         this.md.setScale(this.idx, this.getIntegerValue(var1));
         break;
      case 15:
         if (var2) {
            this.md.setTableName(this.idx, (String)null);
         } else {
            this.md.setTableName(this.idx, var1);
         }
         break;
      case 16:
         if (var2) {
            this.md.setCatalogName(this.idx, (String)null);
         } else {
            this.md.setCatalogName(this.idx, var1);
         }
         break;
      case 17:
         if (var2) {
            throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString());
         }

         this.md.setColumnType(this.idx, this.getIntegerValue(var1));
         break;
      case 18:
         if (var2) {
            this.md.setColumnTypeName(this.idx, (String)null);
         } else {
            this.md.setColumnTypeName(this.idx, var1);
         }
      }

   }

   private void setDataValue(char[] var1, int var2, int var3) throws SQLException {
      switch(this.getTag()) {
      case 1:
         this.columnValue = new String(var1, var2, var3);
         this.tempStr = this.tempStr.concat(this.columnValue);
      case 2:
      case 3:
      case 4:
      default:
         break;
      case 5:
         this.upd = new Object[2];
         this.tempUpdate = this.tempUpdate.concat(new String(var1, var2, var3));
         this.upd[0] = this.idx;
         this.upd[1] = this.tempUpdate;
         this.lastval = (String)this.upd[1];
      }

   }

   private void insertValue(String var1) throws SQLException {
      if (this.getNullValue()) {
         this.rs.updateNull(this.idx);
      } else {
         int var2 = this.rs.getMetaData().getColumnType(this.idx);
         switch(var2) {
         case -7:
            this.rs.updateBoolean(this.idx, this.getBooleanValue(var1));
            break;
         case -6:
         case 5:
            this.rs.updateShort(this.idx, this.getShortValue(var1));
            break;
         case -5:
            this.rs.updateLong(this.idx, this.getLongValue(var1));
            break;
         case -4:
         case -3:
         case -2:
            this.rs.updateBytes(this.idx, this.getBinaryValue(var1));
            break;
         case -1:
         case 1:
         case 12:
            this.rs.updateString(this.idx, this.getStringValue(var1));
            break;
         case 2:
         case 3:
            this.rs.updateObject(this.idx, this.getBigDecimalValue(var1));
            break;
         case 4:
            this.rs.updateInt(this.idx, this.getIntegerValue(var1));
            break;
         case 6:
         case 7:
            this.rs.updateFloat(this.idx, this.getFloatValue(var1));
            break;
         case 8:
            this.rs.updateDouble(this.idx, this.getDoubleValue(var1));
            break;
         case 16:
            this.rs.updateBoolean(this.idx, this.getBooleanValue(var1));
            break;
         case 91:
            this.rs.updateDate(this.idx, this.getDateValue(var1));
            break;
         case 92:
            this.rs.updateTime(this.idx, this.getTimeValue(var1));
            break;
         case 93:
            this.rs.updateTimestamp(this.idx, this.getTimestampValue(var1));
         }

      }
   }

   public void error(SAXParseException var1) throws SAXParseException {
      throw var1;
   }

   public void warning(SAXParseException var1) throws SAXParseException {
      System.out.println(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.warning").toString(), var1.getMessage(), var1.getLineNumber(), var1.getSystemId()));
   }

   public void notationDecl(String var1, String var2, String var3) {
   }

   public void unparsedEntityDecl(String var1, String var2, String var3, String var4) {
   }

   private Row getPresentRow(WebRowSetImpl var1) throws SQLException {
      return null;
   }
}
