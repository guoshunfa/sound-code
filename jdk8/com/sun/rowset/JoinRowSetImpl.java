package com.sun.rowset;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.Vector;
import javax.sql.RowSet;
import javax.sql.RowSetListener;
import javax.sql.RowSetMetaData;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.JoinRowSet;
import javax.sql.rowset.Joinable;
import javax.sql.rowset.RowSetMetaDataImpl;
import javax.sql.rowset.WebRowSet;
import javax.sql.rowset.spi.SyncProvider;
import javax.sql.rowset.spi.SyncProviderException;

public class JoinRowSetImpl extends WebRowSetImpl implements JoinRowSet {
   private Vector<CachedRowSetImpl> vecRowSetsInJOIN = new Vector();
   private CachedRowSetImpl crsInternal = new CachedRowSetImpl();
   private Vector<Integer> vecJoinType = new Vector();
   private Vector<String> vecTableNames = new Vector();
   private int iMatchKey = -1;
   private String strMatchKey = null;
   boolean[] supportedJOINs = new boolean[]{false, true, false, false, false};
   private WebRowSet wrs;
   static final long serialVersionUID = -5590501621560008453L;

   public JoinRowSetImpl() throws SQLException {
      try {
         this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }
   }

   public void addRowSet(Joinable var1) throws SQLException {
      boolean var2 = false;
      boolean var3 = false;
      if (!(var1 instanceof RowSet)) {
         throw new SQLException(this.resBundle.handleGetObject("joinrowsetimpl.notinstance").toString());
      } else {
         CachedRowSetImpl var4;
         int var6;
         int var7;
         int[] var12;
         if (var1 instanceof JdbcRowSetImpl) {
            var4 = new CachedRowSetImpl();
            var4.populate((RowSet)var1);
            if (var4.size() == 0) {
               throw new SQLException(this.resBundle.handleGetObject("joinrowsetimpl.emptyrowset").toString());
            }

            try {
               int var5 = 0;

               for(var6 = 0; var6 < var1.getMatchColumnIndexes().length && var1.getMatchColumnIndexes()[var6] != -1; ++var6) {
                  ++var5;
               }

               var12 = new int[var5];

               for(var7 = 0; var7 < var5; ++var7) {
                  var12[var7] = var1.getMatchColumnIndexes()[var7];
               }

               var4.setMatchColumn(var12);
            } catch (SQLException var10) {
            }
         } else {
            var4 = (CachedRowSetImpl)var1;
            if (var4.size() == 0) {
               throw new SQLException(this.resBundle.handleGetObject("joinrowsetimpl.emptyrowset").toString());
            }
         }

         try {
            this.iMatchKey = var4.getMatchColumnIndexes()[0];
         } catch (SQLException var9) {
            var2 = true;
         }

         try {
            this.strMatchKey = var4.getMatchColumnNames()[0];
         } catch (SQLException var8) {
            var3 = true;
         }

         if (var2 && var3) {
            throw new SQLException(this.resBundle.handleGetObject("joinrowsetimpl.matchnotset").toString());
         } else {
            if (var2) {
               ArrayList var11 = new ArrayList();

               for(var6 = 0; var6 < var4.getMatchColumnNames().length && (this.strMatchKey = var4.getMatchColumnNames()[var6]) != null; ++var6) {
                  this.iMatchKey = var4.findColumn(this.strMatchKey);
                  var11.add(this.iMatchKey);
               }

               var12 = new int[var11.size()];

               for(var7 = 0; var7 < var11.size(); ++var7) {
                  var12[var7] = (Integer)var11.get(var7);
               }

               var4.setMatchColumn(var12);
            }

            this.initJOIN(var4);
         }
      }
   }

   public void addRowSet(RowSet var1, int var2) throws SQLException {
      ((CachedRowSetImpl)var1).setMatchColumn(var2);
      this.addRowSet((Joinable)var1);
   }

   public void addRowSet(RowSet var1, String var2) throws SQLException {
      ((CachedRowSetImpl)var1).setMatchColumn(var2);
      this.addRowSet((Joinable)var1);
   }

   public void addRowSet(RowSet[] var1, int[] var2) throws SQLException {
      if (var1.length != var2.length) {
         throw new SQLException(this.resBundle.handleGetObject("joinrowsetimpl.numnotequal").toString());
      } else {
         for(int var3 = 0; var3 < var1.length; ++var3) {
            ((CachedRowSetImpl)var1[var3]).setMatchColumn(var2[var3]);
            this.addRowSet((Joinable)var1[var3]);
         }

      }
   }

   public void addRowSet(RowSet[] var1, String[] var2) throws SQLException {
      if (var1.length != var2.length) {
         throw new SQLException(this.resBundle.handleGetObject("joinrowsetimpl.numnotequal").toString());
      } else {
         for(int var3 = 0; var3 < var1.length; ++var3) {
            ((CachedRowSetImpl)var1[var3]).setMatchColumn(var2[var3]);
            this.addRowSet((Joinable)var1[var3]);
         }

      }
   }

   public Collection getRowSets() throws SQLException {
      return this.vecRowSetsInJOIN;
   }

   public String[] getRowSetNames() throws SQLException {
      Object[] var1 = this.vecTableNames.toArray();
      String[] var2 = new String[var1.length];

      for(int var3 = 0; var3 < var1.length; ++var3) {
         var2[var3] = var1[var3].toString();
      }

      return var2;
   }

   public CachedRowSet toCachedRowSet() throws SQLException {
      return this.crsInternal;
   }

   public boolean supportsCrossJoin() {
      return this.supportedJOINs[0];
   }

   public boolean supportsInnerJoin() {
      return this.supportedJOINs[1];
   }

   public boolean supportsLeftOuterJoin() {
      return this.supportedJOINs[2];
   }

   public boolean supportsRightOuterJoin() {
      return this.supportedJOINs[3];
   }

   public boolean supportsFullJoin() {
      return this.supportedJOINs[4];
   }

   public void setJoinType(int var1) throws SQLException {
      if (var1 >= 0 && var1 <= 4) {
         if (var1 != 1) {
            throw new SQLException(this.resBundle.handleGetObject("joinrowsetimpl.notsupported").toString());
         } else {
            Integer var2 = 1;
            this.vecJoinType.add(var2);
         }
      } else {
         throw new SQLException(this.resBundle.handleGetObject("joinrowsetimpl.notdefined").toString());
      }
   }

   private boolean checkforMatchColumn(Joinable var1) throws SQLException {
      int[] var2 = var1.getMatchColumnIndexes();
      return var2.length > 0;
   }

   private void initJOIN(CachedRowSet var1) throws SQLException {
      try {
         CachedRowSetImpl var2 = (CachedRowSetImpl)var1;
         CachedRowSetImpl var3 = new CachedRowSetImpl();
         RowSetMetaDataImpl var4 = new RowSetMetaDataImpl();
         if (this.vecRowSetsInJOIN.isEmpty()) {
            this.crsInternal = (CachedRowSetImpl)var1.createCopy();
            this.crsInternal.setMetaData((RowSetMetaDataImpl)var2.getMetaData());
            this.vecRowSetsInJOIN.add(var2);
         } else {
            if (this.vecRowSetsInJOIN.size() - this.vecJoinType.size() == 2) {
               this.setJoinType(1);
            } else if (this.vecRowSetsInJOIN.size() - this.vecJoinType.size() == 1) {
            }

            this.vecTableNames.add(this.crsInternal.getTableName());
            this.vecTableNames.add(var2.getTableName());
            int var5 = var2.size();
            int var6 = this.crsInternal.size();
            int var7 = 0;

            int var8;
            for(var8 = 0; var8 < this.crsInternal.getMatchColumnIndexes().length && this.crsInternal.getMatchColumnIndexes()[var8] != -1; ++var8) {
               ++var7;
            }

            var4.setColumnCount(this.crsInternal.getMetaData().getColumnCount() + var2.getMetaData().getColumnCount() - var7);
            var3.setMetaData(var4);
            this.crsInternal.beforeFirst();
            var2.beforeFirst();

            int var9;
            for(var8 = 1; var8 <= var6 && !this.crsInternal.isAfterLast(); ++var8) {
               if (this.crsInternal.next()) {
                  var2.beforeFirst();

                  for(var9 = 1; var9 <= var5 && !var2.isAfterLast(); ++var9) {
                     if (var2.next()) {
                        boolean var10 = true;

                        int var11;
                        for(var11 = 0; var11 < var7; ++var11) {
                           if (!this.crsInternal.getObject(this.crsInternal.getMatchColumnIndexes()[var11]).equals(var2.getObject(var2.getMatchColumnIndexes()[var11]))) {
                              var10 = false;
                              break;
                           }
                        }

                        if (var10) {
                           int var12 = 0;
                           var3.moveToInsertRow();

                           int var13;
                           for(var11 = 1; var11 <= this.crsInternal.getMetaData().getColumnCount(); ++var11) {
                              var10 = false;

                              for(var13 = 0; var13 < var7; ++var13) {
                                 if (var11 == this.crsInternal.getMatchColumnIndexes()[var13]) {
                                    var10 = true;
                                    break;
                                 }
                              }

                              if (!var10) {
                                 ++var12;
                                 var3.updateObject(var12, this.crsInternal.getObject(var11));
                                 var4.setColumnName(var12, this.crsInternal.getMetaData().getColumnName(var11));
                                 var4.setTableName(var12, this.crsInternal.getTableName());
                                 var4.setColumnType(var11, this.crsInternal.getMetaData().getColumnType(var11));
                                 var4.setAutoIncrement(var11, this.crsInternal.getMetaData().isAutoIncrement(var11));
                                 var4.setCaseSensitive(var11, this.crsInternal.getMetaData().isCaseSensitive(var11));
                                 var4.setCatalogName(var11, this.crsInternal.getMetaData().getCatalogName(var11));
                                 var4.setColumnDisplaySize(var11, this.crsInternal.getMetaData().getColumnDisplaySize(var11));
                                 var4.setColumnLabel(var11, this.crsInternal.getMetaData().getColumnLabel(var11));
                                 var4.setColumnType(var11, this.crsInternal.getMetaData().getColumnType(var11));
                                 var4.setColumnTypeName(var11, this.crsInternal.getMetaData().getColumnTypeName(var11));
                                 var4.setCurrency(var11, this.crsInternal.getMetaData().isCurrency(var11));
                                 var4.setNullable(var11, this.crsInternal.getMetaData().isNullable(var11));
                                 var4.setPrecision(var11, this.crsInternal.getMetaData().getPrecision(var11));
                                 var4.setScale(var11, this.crsInternal.getMetaData().getScale(var11));
                                 var4.setSchemaName(var11, this.crsInternal.getMetaData().getSchemaName(var11));
                                 var4.setSearchable(var11, this.crsInternal.getMetaData().isSearchable(var11));
                                 var4.setSigned(var11, this.crsInternal.getMetaData().isSigned(var11));
                              } else {
                                 ++var12;
                                 var3.updateObject(var12, this.crsInternal.getObject(var11));
                                 var4.setColumnName(var12, this.crsInternal.getMetaData().getColumnName(var11));
                                 var4.setTableName(var12, this.crsInternal.getTableName() + "#" + var2.getTableName());
                                 var4.setColumnType(var11, this.crsInternal.getMetaData().getColumnType(var11));
                                 var4.setAutoIncrement(var11, this.crsInternal.getMetaData().isAutoIncrement(var11));
                                 var4.setCaseSensitive(var11, this.crsInternal.getMetaData().isCaseSensitive(var11));
                                 var4.setCatalogName(var11, this.crsInternal.getMetaData().getCatalogName(var11));
                                 var4.setColumnDisplaySize(var11, this.crsInternal.getMetaData().getColumnDisplaySize(var11));
                                 var4.setColumnLabel(var11, this.crsInternal.getMetaData().getColumnLabel(var11));
                                 var4.setColumnType(var11, this.crsInternal.getMetaData().getColumnType(var11));
                                 var4.setColumnTypeName(var11, this.crsInternal.getMetaData().getColumnTypeName(var11));
                                 var4.setCurrency(var11, this.crsInternal.getMetaData().isCurrency(var11));
                                 var4.setNullable(var11, this.crsInternal.getMetaData().isNullable(var11));
                                 var4.setPrecision(var11, this.crsInternal.getMetaData().getPrecision(var11));
                                 var4.setScale(var11, this.crsInternal.getMetaData().getScale(var11));
                                 var4.setSchemaName(var11, this.crsInternal.getMetaData().getSchemaName(var11));
                                 var4.setSearchable(var11, this.crsInternal.getMetaData().isSearchable(var11));
                                 var4.setSigned(var11, this.crsInternal.getMetaData().isSigned(var11));
                              }
                           }

                           for(var13 = 1; var13 <= var2.getMetaData().getColumnCount(); ++var13) {
                              var10 = false;

                              for(int var14 = 0; var14 < var7; ++var14) {
                                 if (var13 == var2.getMatchColumnIndexes()[var14]) {
                                    var10 = true;
                                    break;
                                 }
                              }

                              if (!var10) {
                                 ++var12;
                                 var3.updateObject(var12, var2.getObject(var13));
                                 var4.setColumnName(var12, var2.getMetaData().getColumnName(var13));
                                 var4.setTableName(var12, var2.getTableName());
                                 var4.setColumnType(var11 + var13 - 1, var2.getMetaData().getColumnType(var13));
                                 var4.setAutoIncrement(var11 + var13 - 1, var2.getMetaData().isAutoIncrement(var13));
                                 var4.setCaseSensitive(var11 + var13 - 1, var2.getMetaData().isCaseSensitive(var13));
                                 var4.setCatalogName(var11 + var13 - 1, var2.getMetaData().getCatalogName(var13));
                                 var4.setColumnDisplaySize(var11 + var13 - 1, var2.getMetaData().getColumnDisplaySize(var13));
                                 var4.setColumnLabel(var11 + var13 - 1, var2.getMetaData().getColumnLabel(var13));
                                 var4.setColumnType(var11 + var13 - 1, var2.getMetaData().getColumnType(var13));
                                 var4.setColumnTypeName(var11 + var13 - 1, var2.getMetaData().getColumnTypeName(var13));
                                 var4.setCurrency(var11 + var13 - 1, var2.getMetaData().isCurrency(var13));
                                 var4.setNullable(var11 + var13 - 1, var2.getMetaData().isNullable(var13));
                                 var4.setPrecision(var11 + var13 - 1, var2.getMetaData().getPrecision(var13));
                                 var4.setScale(var11 + var13 - 1, var2.getMetaData().getScale(var13));
                                 var4.setSchemaName(var11 + var13 - 1, var2.getMetaData().getSchemaName(var13));
                                 var4.setSearchable(var11 + var13 - 1, var2.getMetaData().isSearchable(var13));
                                 var4.setSigned(var11 + var13 - 1, var2.getMetaData().isSigned(var13));
                              } else {
                                 --var11;
                              }
                           }

                           var3.insertRow();
                           var3.moveToCurrentRow();
                        }
                     }
                  }
               }
            }

            var3.setMetaData(var4);
            var3.setOriginal();
            int[] var17 = new int[var7];

            for(var9 = 0; var9 < var7; ++var9) {
               var17[var9] = this.crsInternal.getMatchColumnIndexes()[var9];
            }

            this.crsInternal = (CachedRowSetImpl)var3.createCopy();
            this.crsInternal.setMatchColumn(var17);
            this.crsInternal.setMetaData(var4);
            this.vecRowSetsInJOIN.add(var2);
         }

      } catch (SQLException var15) {
         var15.printStackTrace();
         throw new SQLException(this.resBundle.handleGetObject("joinrowsetimpl.initerror").toString() + var15);
      } catch (Exception var16) {
         var16.printStackTrace();
         throw new SQLException(this.resBundle.handleGetObject("joinrowsetimpl.genericerr").toString() + var16);
      }
   }

   public String getWhereClause() throws SQLException {
      String var1 = "Select ";
      String var3 = "";
      String var4 = "";
      int var5 = this.vecRowSetsInJOIN.size();

      int var9;
      for(var9 = 0; var9 < var5; ++var9) {
         CachedRowSetImpl var8 = (CachedRowSetImpl)this.vecRowSetsInJOIN.get(var9);
         int var6 = var8.getMetaData().getColumnCount();
         var3 = var3.concat(var8.getTableName());
         var4 = var4.concat(var3 + ", ");

         for(int var7 = 1; var7 < var6; var1 = var1.concat(", ")) {
            var1 = var1.concat(var3 + "." + var8.getMetaData().getColumnName(var7++));
         }
      }

      var1 = var1.substring(0, var1.lastIndexOf(","));
      var1 = var1.concat(" from ");
      var1 = var1.concat(var4);
      var1 = var1.substring(0, var1.lastIndexOf(","));
      var1 = var1.concat(" where ");

      for(var9 = 0; var9 < var5; ++var9) {
         var1 = var1.concat(((CachedRowSetImpl)this.vecRowSetsInJOIN.get(var9)).getMatchColumnNames()[0]);
         if (var9 % 2 != 0) {
            var1 = var1.concat("=");
         } else {
            var1 = var1.concat(" and");
         }

         var1 = var1.concat(" ");
      }

      return var1;
   }

   public boolean next() throws SQLException {
      return this.crsInternal.next();
   }

   public void close() throws SQLException {
      this.crsInternal.close();
   }

   public boolean wasNull() throws SQLException {
      return this.crsInternal.wasNull();
   }

   public String getString(int var1) throws SQLException {
      return this.crsInternal.getString(var1);
   }

   public boolean getBoolean(int var1) throws SQLException {
      return this.crsInternal.getBoolean(var1);
   }

   public byte getByte(int var1) throws SQLException {
      return this.crsInternal.getByte(var1);
   }

   public short getShort(int var1) throws SQLException {
      return this.crsInternal.getShort(var1);
   }

   public int getInt(int var1) throws SQLException {
      return this.crsInternal.getInt(var1);
   }

   public long getLong(int var1) throws SQLException {
      return this.crsInternal.getLong(var1);
   }

   public float getFloat(int var1) throws SQLException {
      return this.crsInternal.getFloat(var1);
   }

   public double getDouble(int var1) throws SQLException {
      return this.crsInternal.getDouble(var1);
   }

   /** @deprecated */
   @Deprecated
   public BigDecimal getBigDecimal(int var1, int var2) throws SQLException {
      return this.crsInternal.getBigDecimal(var1);
   }

   public byte[] getBytes(int var1) throws SQLException {
      return this.crsInternal.getBytes(var1);
   }

   public Date getDate(int var1) throws SQLException {
      return this.crsInternal.getDate(var1);
   }

   public Time getTime(int var1) throws SQLException {
      return this.crsInternal.getTime(var1);
   }

   public Timestamp getTimestamp(int var1) throws SQLException {
      return this.crsInternal.getTimestamp(var1);
   }

   public InputStream getAsciiStream(int var1) throws SQLException {
      return this.crsInternal.getAsciiStream(var1);
   }

   /** @deprecated */
   @Deprecated
   public InputStream getUnicodeStream(int var1) throws SQLException {
      return this.crsInternal.getUnicodeStream(var1);
   }

   public InputStream getBinaryStream(int var1) throws SQLException {
      return this.crsInternal.getBinaryStream(var1);
   }

   public String getString(String var1) throws SQLException {
      return this.crsInternal.getString(var1);
   }

   public boolean getBoolean(String var1) throws SQLException {
      return this.crsInternal.getBoolean(var1);
   }

   public byte getByte(String var1) throws SQLException {
      return this.crsInternal.getByte(var1);
   }

   public short getShort(String var1) throws SQLException {
      return this.crsInternal.getShort(var1);
   }

   public int getInt(String var1) throws SQLException {
      return this.crsInternal.getInt(var1);
   }

   public long getLong(String var1) throws SQLException {
      return this.crsInternal.getLong(var1);
   }

   public float getFloat(String var1) throws SQLException {
      return this.crsInternal.getFloat(var1);
   }

   public double getDouble(String var1) throws SQLException {
      return this.crsInternal.getDouble(var1);
   }

   /** @deprecated */
   @Deprecated
   public BigDecimal getBigDecimal(String var1, int var2) throws SQLException {
      return this.crsInternal.getBigDecimal(var1);
   }

   public byte[] getBytes(String var1) throws SQLException {
      return this.crsInternal.getBytes(var1);
   }

   public Date getDate(String var1) throws SQLException {
      return this.crsInternal.getDate(var1);
   }

   public Time getTime(String var1) throws SQLException {
      return this.crsInternal.getTime(var1);
   }

   public Timestamp getTimestamp(String var1) throws SQLException {
      return this.crsInternal.getTimestamp(var1);
   }

   public InputStream getAsciiStream(String var1) throws SQLException {
      return this.crsInternal.getAsciiStream(var1);
   }

   /** @deprecated */
   @Deprecated
   public InputStream getUnicodeStream(String var1) throws SQLException {
      return this.crsInternal.getUnicodeStream(var1);
   }

   public InputStream getBinaryStream(String var1) throws SQLException {
      return this.crsInternal.getBinaryStream(var1);
   }

   public SQLWarning getWarnings() {
      return this.crsInternal.getWarnings();
   }

   public void clearWarnings() {
      this.crsInternal.clearWarnings();
   }

   public String getCursorName() throws SQLException {
      return this.crsInternal.getCursorName();
   }

   public ResultSetMetaData getMetaData() throws SQLException {
      return this.crsInternal.getMetaData();
   }

   public Object getObject(int var1) throws SQLException {
      return this.crsInternal.getObject(var1);
   }

   public Object getObject(int var1, Map<String, Class<?>> var2) throws SQLException {
      return this.crsInternal.getObject(var1, var2);
   }

   public Object getObject(String var1) throws SQLException {
      return this.crsInternal.getObject(var1);
   }

   public Object getObject(String var1, Map<String, Class<?>> var2) throws SQLException {
      return this.crsInternal.getObject(var1, var2);
   }

   public Reader getCharacterStream(int var1) throws SQLException {
      return this.crsInternal.getCharacterStream(var1);
   }

   public Reader getCharacterStream(String var1) throws SQLException {
      return this.crsInternal.getCharacterStream(var1);
   }

   public BigDecimal getBigDecimal(int var1) throws SQLException {
      return this.crsInternal.getBigDecimal(var1);
   }

   public BigDecimal getBigDecimal(String var1) throws SQLException {
      return this.crsInternal.getBigDecimal(var1);
   }

   public int size() {
      return this.crsInternal.size();
   }

   public boolean isBeforeFirst() throws SQLException {
      return this.crsInternal.isBeforeFirst();
   }

   public boolean isAfterLast() throws SQLException {
      return this.crsInternal.isAfterLast();
   }

   public boolean isFirst() throws SQLException {
      return this.crsInternal.isFirst();
   }

   public boolean isLast() throws SQLException {
      return this.crsInternal.isLast();
   }

   public void beforeFirst() throws SQLException {
      this.crsInternal.beforeFirst();
   }

   public void afterLast() throws SQLException {
      this.crsInternal.afterLast();
   }

   public boolean first() throws SQLException {
      return this.crsInternal.first();
   }

   public boolean last() throws SQLException {
      return this.crsInternal.last();
   }

   public int getRow() throws SQLException {
      return this.crsInternal.getRow();
   }

   public boolean absolute(int var1) throws SQLException {
      return this.crsInternal.absolute(var1);
   }

   public boolean relative(int var1) throws SQLException {
      return this.crsInternal.relative(var1);
   }

   public boolean previous() throws SQLException {
      return this.crsInternal.previous();
   }

   public int findColumn(String var1) throws SQLException {
      return this.crsInternal.findColumn(var1);
   }

   public boolean rowUpdated() throws SQLException {
      return this.crsInternal.rowUpdated();
   }

   public boolean columnUpdated(int var1) throws SQLException {
      return this.crsInternal.columnUpdated(var1);
   }

   public boolean rowInserted() throws SQLException {
      return this.crsInternal.rowInserted();
   }

   public boolean rowDeleted() throws SQLException {
      return this.crsInternal.rowDeleted();
   }

   public void updateNull(int var1) throws SQLException {
      this.crsInternal.updateNull(var1);
   }

   public void updateBoolean(int var1, boolean var2) throws SQLException {
      this.crsInternal.updateBoolean(var1, var2);
   }

   public void updateByte(int var1, byte var2) throws SQLException {
      this.crsInternal.updateByte(var1, var2);
   }

   public void updateShort(int var1, short var2) throws SQLException {
      this.crsInternal.updateShort(var1, var2);
   }

   public void updateInt(int var1, int var2) throws SQLException {
      this.crsInternal.updateInt(var1, var2);
   }

   public void updateLong(int var1, long var2) throws SQLException {
      this.crsInternal.updateLong(var1, var2);
   }

   public void updateFloat(int var1, float var2) throws SQLException {
      this.crsInternal.updateFloat(var1, var2);
   }

   public void updateDouble(int var1, double var2) throws SQLException {
      this.crsInternal.updateDouble(var1, var2);
   }

   public void updateBigDecimal(int var1, BigDecimal var2) throws SQLException {
      this.crsInternal.updateBigDecimal(var1, var2);
   }

   public void updateString(int var1, String var2) throws SQLException {
      this.crsInternal.updateString(var1, var2);
   }

   public void updateBytes(int var1, byte[] var2) throws SQLException {
      this.crsInternal.updateBytes(var1, var2);
   }

   public void updateDate(int var1, Date var2) throws SQLException {
      this.crsInternal.updateDate(var1, var2);
   }

   public void updateTime(int var1, Time var2) throws SQLException {
      this.crsInternal.updateTime(var1, var2);
   }

   public void updateTimestamp(int var1, Timestamp var2) throws SQLException {
      this.crsInternal.updateTimestamp(var1, var2);
   }

   public void updateAsciiStream(int var1, InputStream var2, int var3) throws SQLException {
      this.crsInternal.updateAsciiStream(var1, var2, var3);
   }

   public void updateBinaryStream(int var1, InputStream var2, int var3) throws SQLException {
      this.crsInternal.updateBinaryStream(var1, var2, var3);
   }

   public void updateCharacterStream(int var1, Reader var2, int var3) throws SQLException {
      this.crsInternal.updateCharacterStream(var1, var2, var3);
   }

   public void updateObject(int var1, Object var2, int var3) throws SQLException {
      this.crsInternal.updateObject(var1, var2, var3);
   }

   public void updateObject(int var1, Object var2) throws SQLException {
      this.crsInternal.updateObject(var1, var2);
   }

   public void updateNull(String var1) throws SQLException {
      this.crsInternal.updateNull(var1);
   }

   public void updateBoolean(String var1, boolean var2) throws SQLException {
      this.crsInternal.updateBoolean(var1, var2);
   }

   public void updateByte(String var1, byte var2) throws SQLException {
      this.crsInternal.updateByte(var1, var2);
   }

   public void updateShort(String var1, short var2) throws SQLException {
      this.crsInternal.updateShort(var1, var2);
   }

   public void updateInt(String var1, int var2) throws SQLException {
      this.crsInternal.updateInt(var1, var2);
   }

   public void updateLong(String var1, long var2) throws SQLException {
      this.crsInternal.updateLong(var1, var2);
   }

   public void updateFloat(String var1, float var2) throws SQLException {
      this.crsInternal.updateFloat(var1, var2);
   }

   public void updateDouble(String var1, double var2) throws SQLException {
      this.crsInternal.updateDouble(var1, var2);
   }

   public void updateBigDecimal(String var1, BigDecimal var2) throws SQLException {
      this.crsInternal.updateBigDecimal(var1, var2);
   }

   public void updateString(String var1, String var2) throws SQLException {
      this.crsInternal.updateString(var1, var2);
   }

   public void updateBytes(String var1, byte[] var2) throws SQLException {
      this.crsInternal.updateBytes(var1, var2);
   }

   public void updateDate(String var1, Date var2) throws SQLException {
      this.crsInternal.updateDate(var1, var2);
   }

   public void updateTime(String var1, Time var2) throws SQLException {
      this.crsInternal.updateTime(var1, var2);
   }

   public void updateTimestamp(String var1, Timestamp var2) throws SQLException {
      this.crsInternal.updateTimestamp(var1, var2);
   }

   public void updateAsciiStream(String var1, InputStream var2, int var3) throws SQLException {
      this.crsInternal.updateAsciiStream(var1, var2, var3);
   }

   public void updateBinaryStream(String var1, InputStream var2, int var3) throws SQLException {
      this.crsInternal.updateBinaryStream(var1, var2, var3);
   }

   public void updateCharacterStream(String var1, Reader var2, int var3) throws SQLException {
      this.crsInternal.updateCharacterStream(var1, var2, var3);
   }

   public void updateObject(String var1, Object var2, int var3) throws SQLException {
      this.crsInternal.updateObject(var1, var2, var3);
   }

   public void updateObject(String var1, Object var2) throws SQLException {
      this.crsInternal.updateObject(var1, var2);
   }

   public void insertRow() throws SQLException {
      this.crsInternal.insertRow();
   }

   public void updateRow() throws SQLException {
      this.crsInternal.updateRow();
   }

   public void deleteRow() throws SQLException {
      this.crsInternal.deleteRow();
   }

   public void refreshRow() throws SQLException {
      this.crsInternal.refreshRow();
   }

   public void cancelRowUpdates() throws SQLException {
      this.crsInternal.cancelRowUpdates();
   }

   public void moveToInsertRow() throws SQLException {
      this.crsInternal.moveToInsertRow();
   }

   public void moveToCurrentRow() throws SQLException {
      this.crsInternal.moveToCurrentRow();
   }

   public Statement getStatement() throws SQLException {
      return this.crsInternal.getStatement();
   }

   public Ref getRef(int var1) throws SQLException {
      return this.crsInternal.getRef(var1);
   }

   public Blob getBlob(int var1) throws SQLException {
      return this.crsInternal.getBlob(var1);
   }

   public Clob getClob(int var1) throws SQLException {
      return this.crsInternal.getClob(var1);
   }

   public Array getArray(int var1) throws SQLException {
      return this.crsInternal.getArray(var1);
   }

   public Ref getRef(String var1) throws SQLException {
      return this.crsInternal.getRef(var1);
   }

   public Blob getBlob(String var1) throws SQLException {
      return this.crsInternal.getBlob(var1);
   }

   public Clob getClob(String var1) throws SQLException {
      return this.crsInternal.getClob(var1);
   }

   public Array getArray(String var1) throws SQLException {
      return this.crsInternal.getArray(var1);
   }

   public Date getDate(int var1, Calendar var2) throws SQLException {
      return this.crsInternal.getDate(var1, var2);
   }

   public Date getDate(String var1, Calendar var2) throws SQLException {
      return this.crsInternal.getDate(var1, var2);
   }

   public Time getTime(int var1, Calendar var2) throws SQLException {
      return this.crsInternal.getTime(var1, var2);
   }

   public Time getTime(String var1, Calendar var2) throws SQLException {
      return this.crsInternal.getTime(var1, var2);
   }

   public Timestamp getTimestamp(int var1, Calendar var2) throws SQLException {
      return this.crsInternal.getTimestamp(var1, var2);
   }

   public Timestamp getTimestamp(String var1, Calendar var2) throws SQLException {
      return this.crsInternal.getTimestamp(var1, var2);
   }

   public void setMetaData(RowSetMetaData var1) throws SQLException {
      this.crsInternal.setMetaData(var1);
   }

   public ResultSet getOriginal() throws SQLException {
      return this.crsInternal.getOriginal();
   }

   public ResultSet getOriginalRow() throws SQLException {
      return this.crsInternal.getOriginalRow();
   }

   public void setOriginalRow() throws SQLException {
      this.crsInternal.setOriginalRow();
   }

   public int[] getKeyColumns() throws SQLException {
      return this.crsInternal.getKeyColumns();
   }

   public void setKeyColumns(int[] var1) throws SQLException {
      this.crsInternal.setKeyColumns(var1);
   }

   public void updateRef(int var1, Ref var2) throws SQLException {
      this.crsInternal.updateRef(var1, var2);
   }

   public void updateRef(String var1, Ref var2) throws SQLException {
      this.crsInternal.updateRef(var1, var2);
   }

   public void updateClob(int var1, Clob var2) throws SQLException {
      this.crsInternal.updateClob(var1, var2);
   }

   public void updateClob(String var1, Clob var2) throws SQLException {
      this.crsInternal.updateClob(var1, var2);
   }

   public void updateBlob(int var1, Blob var2) throws SQLException {
      this.crsInternal.updateBlob(var1, var2);
   }

   public void updateBlob(String var1, Blob var2) throws SQLException {
      this.crsInternal.updateBlob(var1, var2);
   }

   public void updateArray(int var1, Array var2) throws SQLException {
      this.crsInternal.updateArray(var1, var2);
   }

   public void updateArray(String var1, Array var2) throws SQLException {
      this.crsInternal.updateArray(var1, var2);
   }

   public void execute() throws SQLException {
      this.crsInternal.execute();
   }

   public void execute(Connection var1) throws SQLException {
      this.crsInternal.execute(var1);
   }

   public URL getURL(int var1) throws SQLException {
      return this.crsInternal.getURL(var1);
   }

   public URL getURL(String var1) throws SQLException {
      return this.crsInternal.getURL(var1);
   }

   public void writeXml(ResultSet var1, Writer var2) throws SQLException {
      this.wrs = new WebRowSetImpl();
      this.wrs.populate(var1);
      this.wrs.writeXml(var2);
   }

   public void writeXml(Writer var1) throws SQLException {
      this.createWebRowSet().writeXml(var1);
   }

   public void readXml(Reader var1) throws SQLException {
      this.wrs = new WebRowSetImpl();
      this.wrs.readXml(var1);
      this.crsInternal = (CachedRowSetImpl)this.wrs;
   }

   public void readXml(InputStream var1) throws SQLException, IOException {
      this.wrs = new WebRowSetImpl();
      this.wrs.readXml(var1);
      this.crsInternal = (CachedRowSetImpl)this.wrs;
   }

   public void writeXml(OutputStream var1) throws SQLException, IOException {
      this.createWebRowSet().writeXml(var1);
   }

   public void writeXml(ResultSet var1, OutputStream var2) throws SQLException, IOException {
      this.wrs = new WebRowSetImpl();
      this.wrs.populate(var1);
      this.wrs.writeXml(var2);
   }

   private WebRowSet createWebRowSet() throws SQLException {
      if (this.wrs != null) {
         return this.wrs;
      } else {
         this.wrs = new WebRowSetImpl();
         this.crsInternal.beforeFirst();
         this.wrs.populate(this.crsInternal);
         return this.wrs;
      }
   }

   public int getJoinType() throws SQLException {
      if (this.vecJoinType == null) {
         this.setJoinType(1);
      }

      Integer var1 = (Integer)this.vecJoinType.get(this.vecJoinType.size() - 1);
      return var1;
   }

   public void addRowSetListener(RowSetListener var1) {
      this.crsInternal.addRowSetListener(var1);
   }

   public void removeRowSetListener(RowSetListener var1) {
      this.crsInternal.removeRowSetListener(var1);
   }

   public Collection<?> toCollection() throws SQLException {
      return this.crsInternal.toCollection();
   }

   public Collection<?> toCollection(int var1) throws SQLException {
      return this.crsInternal.toCollection(var1);
   }

   public Collection<?> toCollection(String var1) throws SQLException {
      return this.crsInternal.toCollection(var1);
   }

   public CachedRowSet createCopySchema() throws SQLException {
      return this.crsInternal.createCopySchema();
   }

   public void setSyncProvider(String var1) throws SQLException {
      this.crsInternal.setSyncProvider(var1);
   }

   public void acceptChanges() throws SyncProviderException {
      this.crsInternal.acceptChanges();
   }

   public SyncProvider getSyncProvider() throws SQLException {
      return this.crsInternal.getSyncProvider();
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
