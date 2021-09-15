package com.sun.rowset.internal;

import com.sun.rowset.CachedRowSetImpl;
import com.sun.rowset.JdbcRowSetResourceBundle;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Reader;
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
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import javax.sql.RowSet;
import javax.sql.RowSetEvent;
import javax.sql.RowSetMetaData;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetMetaDataImpl;
import javax.sql.rowset.RowSetWarning;
import javax.sql.rowset.spi.SyncProvider;
import javax.sql.rowset.spi.SyncProviderException;
import javax.sql.rowset.spi.SyncResolver;

public class SyncResolverImpl extends CachedRowSetImpl implements SyncResolver {
   private CachedRowSetImpl crsRes;
   private CachedRowSetImpl crsSync;
   private ArrayList<?> stats;
   private CachedRowSetWriter crw;
   private int rowStatus;
   private int sz;
   private transient Connection con;
   private CachedRowSet row;
   private JdbcRowSetResourceBundle resBundle;
   static final long serialVersionUID = -3345004441725080251L;

   public SyncResolverImpl() throws SQLException {
      try {
         this.crsSync = new CachedRowSetImpl();
         this.crsRes = new CachedRowSetImpl();
         this.crw = new CachedRowSetWriter();
         this.row = new CachedRowSetImpl();
         this.rowStatus = 1;

         try {
            this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
         } catch (IOException var2) {
            throw new RuntimeException(var2);
         }
      } catch (SQLException var3) {
      }

   }

   public int getStatus() {
      return (Integer)this.stats.get(this.rowStatus - 1);
   }

   public Object getConflictValue(int var1) throws SQLException {
      try {
         return this.crsRes.getObject(var1);
      } catch (SQLException var3) {
         throw new SQLException(var3.getMessage());
      }
   }

   public Object getConflictValue(String var1) throws SQLException {
      try {
         return this.crsRes.getObject(var1);
      } catch (SQLException var3) {
         throw new SQLException(var3.getMessage());
      }
   }

   public void setResolvedValue(int var1, Object var2) throws SQLException {
      try {
         label54: {
            if (var1 > 0 && var1 <= this.crsSync.getMetaData().getColumnCount()) {
               if (this.crsRes.getObject(var1) != null) {
                  break label54;
               }

               throw new SQLException(this.resBundle.handleGetObject("syncrsimpl.noconflict").toString());
            }

            throw new SQLException(this.resBundle.handleGetObject("syncrsimpl.indexval").toString() + var1);
         }
      } catch (SQLException var7) {
         throw new SQLException(var7.getMessage());
      }

      try {
         boolean var3 = true;
         if (!this.crsSync.getObject(var1).toString().equals(var2.toString()) && !this.crsRes.getObject(var1).toString().equals(var2.toString())) {
            throw new SQLException(this.resBundle.handleGetObject("syncrsimpl.valtores").toString());
         } else {
            this.crsRes.updateNull(var1);
            this.crsRes.updateRow();
            if (this.row.size() != 1) {
               this.row = this.buildCachedRow();
            }

            this.row.updateObject(var1, var2);
            this.row.updateRow();

            for(int var4 = 1; var4 < this.crsRes.getMetaData().getColumnCount(); ++var4) {
               if (this.crsRes.getObject(var4) != null) {
                  var3 = false;
                  break;
               }
            }

            if (var3) {
               try {
                  this.writeData(this.row);
               } catch (SyncProviderException var5) {
                  throw new SQLException(this.resBundle.handleGetObject("syncrsimpl.syncnotpos").toString());
               }
            }

         }
      } catch (SQLException var6) {
         throw new SQLException(var6.getMessage());
      }
   }

   private void writeData(CachedRowSet var1) throws SQLException {
      this.crw.updateResolvedConflictToDB(var1, this.crw.getReader().connect(this.crsSync));
   }

   private CachedRowSet buildCachedRow() throws SQLException {
      CachedRowSetImpl var2 = new CachedRowSetImpl();
      new RowSetMetaDataImpl();
      RowSetMetaDataImpl var4 = (RowSetMetaDataImpl)this.crsSync.getMetaData();
      RowSetMetaDataImpl var5 = new RowSetMetaDataImpl();
      int var1 = var4.getColumnCount();
      var5.setColumnCount(var1);

      int var6;
      for(var6 = 1; var6 <= var1; ++var6) {
         var5.setColumnType(var6, var4.getColumnType(var6));
         var5.setColumnName(var6, var4.getColumnName(var6));
         var5.setNullable(var6, 2);

         try {
            var5.setCatalogName(var6, var4.getCatalogName(var6));
            var5.setSchemaName(var6, var4.getSchemaName(var6));
         } catch (SQLException var13) {
            var13.printStackTrace();
         }
      }

      var2.setMetaData(var5);
      var2.moveToInsertRow();

      for(var6 = 1; var6 <= this.crsSync.getMetaData().getColumnCount(); ++var6) {
         var2.updateObject(var6, this.crsSync.getObject(var6));
      }

      var2.insertRow();
      var2.moveToCurrentRow();
      var2.absolute(1);
      var2.setOriginalRow();

      try {
         var2.setUrl(this.crsSync.getUrl());
      } catch (SQLException var12) {
      }

      try {
         var2.setDataSourceName(this.crsSync.getCommand());
      } catch (SQLException var11) {
      }

      try {
         if (this.crsSync.getTableName() != null) {
            var2.setTableName(this.crsSync.getTableName());
         }
      } catch (SQLException var10) {
      }

      try {
         if (this.crsSync.getCommand() != null) {
            var2.setCommand(this.crsSync.getCommand());
         }
      } catch (SQLException var9) {
      }

      try {
         var2.setKeyColumns(this.crsSync.getKeyColumns());
      } catch (SQLException var8) {
      }

      return var2;
   }

   public void setResolvedValue(String var1, Object var2) throws SQLException {
   }

   void setCachedRowSet(CachedRowSet var1) {
      this.crsSync = (CachedRowSetImpl)var1;
   }

   void setCachedRowSetResolver(CachedRowSet var1) {
      try {
         this.crsRes = (CachedRowSetImpl)var1;
         this.crsRes.afterLast();
         this.sz = this.crsRes.size();
      } catch (SQLException var3) {
      }

   }

   void setStatus(ArrayList var1) {
      this.stats = var1;
   }

   void setCachedRowSetWriter(CachedRowSetWriter var1) {
      this.crw = var1;
   }

   public boolean nextConflict() throws SQLException {
      boolean var1 = false;
      this.crsSync.setShowDeleted(true);

      while(this.crsSync.next()) {
         this.crsRes.previous();
         ++this.rowStatus;
         if (this.rowStatus - 1 >= this.stats.size()) {
            var1 = false;
            break;
         }

         if ((Integer)this.stats.get(this.rowStatus - 1) != 3) {
            var1 = true;
            break;
         }
      }

      this.crsSync.setShowDeleted(false);
      return var1;
   }

   public boolean previousConflict() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void setCommand(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void populate(ResultSet var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void execute(Connection var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void acceptChanges() throws SyncProviderException {
      throw new UnsupportedOperationException();
   }

   public void acceptChanges(Connection var1) throws SyncProviderException {
      throw new UnsupportedOperationException();
   }

   public void restoreOriginal() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void release() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void undoDelete() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void undoInsert() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void undoUpdate() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public RowSet createShared() throws SQLException {
      throw new UnsupportedOperationException();
   }

   protected Object clone() throws CloneNotSupportedException {
      throw new UnsupportedOperationException();
   }

   public CachedRowSet createCopy() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public CachedRowSet createCopySchema() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public CachedRowSet createCopyNoConstraints() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Collection toCollection() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Collection toCollection(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Collection toCollection(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public SyncProvider getSyncProvider() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void setSyncProvider(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void execute() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public boolean next() throws SQLException {
      throw new UnsupportedOperationException();
   }

   protected boolean internalNext() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void close() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public boolean wasNull() throws SQLException {
      throw new UnsupportedOperationException();
   }

   protected BaseRow getCurrentRow() {
      throw new UnsupportedOperationException();
   }

   protected void removeCurrentRow() {
      throw new UnsupportedOperationException();
   }

   public String getString(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public boolean getBoolean(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public byte getByte(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public short getShort(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public int getInt(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public long getLong(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public float getFloat(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public double getDouble(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public BigDecimal getBigDecimal(int var1, int var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public byte[] getBytes(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Date getDate(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Time getTime(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Timestamp getTimestamp(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public InputStream getAsciiStream(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public InputStream getUnicodeStream(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public InputStream getBinaryStream(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public String getString(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public boolean getBoolean(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public byte getByte(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public short getShort(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public int getInt(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public long getLong(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public float getFloat(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public double getDouble(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public BigDecimal getBigDecimal(String var1, int var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public byte[] getBytes(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Date getDate(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Time getTime(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Timestamp getTimestamp(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public InputStream getAsciiStream(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public InputStream getUnicodeStream(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public InputStream getBinaryStream(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public SQLWarning getWarnings() {
      throw new UnsupportedOperationException();
   }

   public void clearWarnings() {
      throw new UnsupportedOperationException();
   }

   public String getCursorName() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public ResultSetMetaData getMetaData() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Object getObject(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Object getObject(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public int findColumn(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Reader getCharacterStream(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Reader getCharacterStream(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public BigDecimal getBigDecimal(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public BigDecimal getBigDecimal(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public int size() {
      throw new UnsupportedOperationException();
   }

   public boolean isBeforeFirst() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public boolean isAfterLast() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public boolean isFirst() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public boolean isLast() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void beforeFirst() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void afterLast() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public boolean first() throws SQLException {
      throw new UnsupportedOperationException();
   }

   protected boolean internalFirst() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public boolean last() throws SQLException {
      throw new UnsupportedOperationException();
   }

   protected boolean internalLast() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public int getRow() throws SQLException {
      return this.crsSync.getRow();
   }

   public boolean absolute(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public boolean relative(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public boolean previous() throws SQLException {
      throw new UnsupportedOperationException();
   }

   protected boolean internalPrevious() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public boolean rowUpdated() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public boolean columnUpdated(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public boolean columnUpdated(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public boolean rowInserted() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public boolean rowDeleted() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateNull(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateBoolean(int var1, boolean var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateByte(int var1, byte var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateShort(int var1, short var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateInt(int var1, int var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateLong(int var1, long var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateFloat(int var1, float var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateDouble(int var1, double var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateBigDecimal(int var1, BigDecimal var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateString(int var1, String var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateBytes(int var1, byte[] var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateDate(int var1, Date var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateTime(int var1, Time var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateTimestamp(int var1, Timestamp var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateAsciiStream(int var1, InputStream var2, int var3) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateBinaryStream(int var1, InputStream var2, int var3) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateCharacterStream(int var1, Reader var2, int var3) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateObject(int var1, Object var2, int var3) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateObject(int var1, Object var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateNull(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateBoolean(String var1, boolean var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateByte(String var1, byte var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateShort(String var1, short var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateInt(String var1, int var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateLong(String var1, long var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateFloat(String var1, float var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateDouble(String var1, double var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateBigDecimal(String var1, BigDecimal var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateString(String var1, String var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateBytes(String var1, byte[] var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateDate(String var1, Date var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateTime(String var1, Time var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateTimestamp(String var1, Timestamp var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateAsciiStream(String var1, InputStream var2, int var3) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateBinaryStream(String var1, InputStream var2, int var3) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateCharacterStream(String var1, Reader var2, int var3) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateObject(String var1, Object var2, int var3) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateObject(String var1, Object var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void insertRow() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateRow() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void deleteRow() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void refreshRow() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void cancelRowUpdates() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void moveToInsertRow() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void moveToCurrentRow() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Statement getStatement() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Object getObject(int var1, Map<String, Class<?>> var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Ref getRef(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Blob getBlob(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Clob getClob(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Array getArray(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Object getObject(String var1, Map<String, Class<?>> var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Ref getRef(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Blob getBlob(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Clob getClob(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Array getArray(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Date getDate(int var1, Calendar var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Date getDate(String var1, Calendar var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Time getTime(int var1, Calendar var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Time getTime(String var1, Calendar var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Timestamp getTimestamp(int var1, Calendar var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Timestamp getTimestamp(String var1, Calendar var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public Connection getConnection() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void setMetaData(RowSetMetaData var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public ResultSet getOriginal() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public ResultSet getOriginalRow() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void setOriginalRow() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void setOriginal() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public String getTableName() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void setTableName(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public int[] getKeyColumns() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void setKeyColumns(int[] var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateRef(int var1, Ref var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateRef(String var1, Ref var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateClob(int var1, Clob var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateClob(String var1, Clob var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateBlob(int var1, Blob var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateBlob(String var1, Blob var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateArray(int var1, Array var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateArray(String var1, Array var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public URL getURL(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public URL getURL(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public RowSetWarning getRowSetWarnings() {
      throw new UnsupportedOperationException();
   }

   public void commit() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void rollback() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void rollback(Savepoint var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void unsetMatchColumn(int[] var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void unsetMatchColumn(String[] var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public String[] getMatchColumnNames() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public int[] getMatchColumnIndexes() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void setMatchColumn(int[] var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void setMatchColumn(String[] var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void setMatchColumn(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void setMatchColumn(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void unsetMatchColumn(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void unsetMatchColumn(String var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void rowSetPopulated(RowSetEvent var1, int var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void populate(ResultSet var1, int var2) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public boolean nextPage() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void setPageSize(int var1) throws SQLException {
      throw new UnsupportedOperationException();
   }

   public int getPageSize() {
      throw new UnsupportedOperationException();
   }

   public boolean previousPage() throws SQLException {
      throw new UnsupportedOperationException();
   }

   public void updateNCharacterStream(int var1, Reader var2, int var3) throws SQLException {
      throw new UnsupportedOperationException("Operation not yet supported");
   }

   public void updateNCharacterStream(String var1, Reader var2, int var3) throws SQLException {
      throw new UnsupportedOperationException("Operation not yet supported");
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
