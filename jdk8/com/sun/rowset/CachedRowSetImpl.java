package com.sun.rowset;

import com.sun.rowset.internal.BaseRow;
import com.sun.rowset.internal.CachedRowSetReader;
import com.sun.rowset.internal.CachedRowSetWriter;
import com.sun.rowset.internal.InsertRow;
import com.sun.rowset.internal.Row;
import com.sun.rowset.providers.RIOptimisticProvider;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import javax.sql.RowSet;
import javax.sql.RowSetEvent;
import javax.sql.RowSetInternal;
import javax.sql.RowSetMetaData;
import javax.sql.RowSetReader;
import javax.sql.RowSetWriter;
import javax.sql.rowset.BaseRowSet;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetMetaDataImpl;
import javax.sql.rowset.RowSetWarning;
import javax.sql.rowset.serial.SQLInputImpl;
import javax.sql.rowset.serial.SerialArray;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialRef;
import javax.sql.rowset.serial.SerialStruct;
import javax.sql.rowset.spi.SyncFactory;
import javax.sql.rowset.spi.SyncProvider;
import javax.sql.rowset.spi.SyncProviderException;
import javax.sql.rowset.spi.TransactionalWriter;
import sun.reflect.misc.ReflectUtil;

public class CachedRowSetImpl extends BaseRowSet implements RowSet, RowSetInternal, Serializable, Cloneable, CachedRowSet {
   private SyncProvider provider;
   private RowSetReader rowSetReader;
   private RowSetWriter rowSetWriter;
   private transient Connection conn;
   private transient ResultSetMetaData RSMD;
   private RowSetMetaDataImpl RowSetMD;
   private int[] keyCols;
   private String tableName;
   private Vector<Object> rvh;
   private int cursorPos;
   private int absolutePos;
   private int numDeleted;
   private int numRows;
   private InsertRow insertRow;
   private boolean onInsertRow;
   private int currentRow;
   private boolean lastValueNull;
   private SQLWarning sqlwarn;
   private String strMatchColumn = "";
   private int iMatchColumn = -1;
   private RowSetWarning rowsetWarning;
   private String DEFAULT_SYNC_PROVIDER = "com.sun.rowset.providers.RIOptimisticProvider";
   private boolean dbmslocatorsUpdateCopy;
   private transient ResultSet resultSet;
   private int endPos;
   private int prevEndPos;
   private int startPos;
   private int startPrev;
   private int pageSize;
   private int maxRowsreached;
   private boolean pagenotend = true;
   private boolean onFirstPage;
   private boolean onLastPage;
   private int populatecallcount;
   private int totalRows;
   private boolean callWithCon;
   private CachedRowSetReader crsReader;
   private Vector<Integer> iMatchColumns;
   private Vector<String> strMatchColumns;
   private boolean tXWriter = false;
   private TransactionalWriter tWriter = null;
   protected transient JdbcRowSetResourceBundle resBundle;
   private boolean updateOnInsert;
   static final long serialVersionUID = 1884577171200622428L;

   public CachedRowSetImpl() throws SQLException {
      try {
         this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }

      this.provider = SyncFactory.getInstance(this.DEFAULT_SYNC_PROVIDER);
      if (!(this.provider instanceof RIOptimisticProvider)) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidp").toString());
      } else {
         this.rowSetReader = (CachedRowSetReader)this.provider.getRowSetReader();
         this.rowSetWriter = (CachedRowSetWriter)this.provider.getRowSetWriter();
         this.initParams();
         this.initContainer();
         this.initProperties();
         this.onInsertRow = false;
         this.insertRow = null;
         this.sqlwarn = new SQLWarning();
         this.rowsetWarning = new RowSetWarning();
      }
   }

   public CachedRowSetImpl(Hashtable var1) throws SQLException {
      try {
         this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }

      if (var1 == null) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.nullhash").toString());
      } else {
         String var2 = (String)var1.get("rowset.provider.classname");
         this.provider = SyncFactory.getInstance(var2);
         this.rowSetReader = this.provider.getRowSetReader();
         this.rowSetWriter = this.provider.getRowSetWriter();
         this.initParams();
         this.initContainer();
         this.initProperties();
      }
   }

   private void initContainer() {
      this.rvh = new Vector(100);
      this.cursorPos = 0;
      this.absolutePos = 0;
      this.numRows = 0;
      this.numDeleted = 0;
   }

   private void initProperties() throws SQLException {
      if (this.resBundle == null) {
         try {
            this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
         } catch (IOException var2) {
            throw new RuntimeException(var2);
         }
      }

      this.setShowDeleted(false);
      this.setQueryTimeout(0);
      this.setMaxRows(0);
      this.setMaxFieldSize(0);
      this.setType(1004);
      this.setConcurrency(1008);
      if (this.rvh.size() > 0 && !this.isReadOnly()) {
         this.setReadOnly(false);
      } else {
         this.setReadOnly(true);
      }

      this.setTransactionIsolation(2);
      this.setEscapeProcessing(true);
      this.checkTransactionalWriter();
      this.iMatchColumns = new Vector(10);

      int var1;
      for(var1 = 0; var1 < 10; ++var1) {
         this.iMatchColumns.add(var1, -1);
      }

      this.strMatchColumns = new Vector(10);

      for(var1 = 0; var1 < 10; ++var1) {
         this.strMatchColumns.add(var1, (Object)null);
      }

   }

   private void checkTransactionalWriter() {
      if (this.rowSetWriter != null) {
         Class var1 = this.rowSetWriter.getClass();
         if (var1 != null) {
            Class[] var2 = var1.getInterfaces();

            for(int var3 = 0; var3 < var2.length; ++var3) {
               if (var2[var3].getName().indexOf("TransactionalWriter") > 0) {
                  this.tXWriter = true;
                  this.establishTransactionalWriter();
               }
            }
         }
      }

   }

   private void establishTransactionalWriter() {
      this.tWriter = (TransactionalWriter)this.provider.getRowSetWriter();
   }

   public void setCommand(String var1) throws SQLException {
      super.setCommand(var1);
      if (!this.buildTableName(var1).equals("")) {
         this.setTableName(this.buildTableName(var1));
      }

   }

   public void populate(ResultSet var1) throws SQLException {
      Map var6 = this.getTypeMap();
      if (var1 == null) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.populate").toString());
      } else {
         this.resultSet = var1;
         this.RSMD = var1.getMetaData();
         this.RowSetMD = new RowSetMetaDataImpl();
         this.initMetaData(this.RowSetMD, this.RSMD);
         this.RSMD = null;
         int var4 = this.RowSetMD.getColumnCount();
         int var8 = this.getMaxRows();
         int var2 = 0;
         Row var3 = null;

         while(var1.next()) {
            var3 = new Row(var4);
            if (var2 > var8 && var8 > 0) {
               this.rowsetWarning.setNextWarning(new RowSetWarning("Populating rows setting has exceeded max row setting"));
            }

            for(int var5 = 1; var5 <= var4; ++var5) {
               Object var7;
               if (var6 != null && !var6.isEmpty()) {
                  var7 = var1.getObject(var5, var6);
               } else {
                  var7 = var1.getObject(var5);
               }

               if (var7 instanceof Struct) {
                  var7 = new SerialStruct((Struct)var7, var6);
               } else if (var7 instanceof SQLData) {
                  var7 = new SerialStruct((SQLData)var7, var6);
               } else if (var7 instanceof Blob) {
                  var7 = new SerialBlob((Blob)var7);
               } else if (var7 instanceof Clob) {
                  var7 = new SerialClob((Clob)var7);
               } else if (var7 instanceof Array) {
                  if (var6 != null) {
                     var7 = new SerialArray((Array)var7, var6);
                  } else {
                     var7 = new SerialArray((Array)var7);
                  }
               }

               var3.initColumnObject(var5, var7);
            }

            ++var2;
            this.rvh.add(var3);
         }

         this.numRows = var2;
         this.notifyRowSetChanged();
      }
   }

   private void initMetaData(RowSetMetaDataImpl var1, ResultSetMetaData var2) throws SQLException {
      int var3 = var2.getColumnCount();
      var1.setColumnCount(var3);

      for(int var4 = 1; var4 <= var3; ++var4) {
         var1.setAutoIncrement(var4, var2.isAutoIncrement(var4));
         if (var2.isAutoIncrement(var4)) {
            this.updateOnInsert = true;
         }

         var1.setCaseSensitive(var4, var2.isCaseSensitive(var4));
         var1.setCurrency(var4, var2.isCurrency(var4));
         var1.setNullable(var4, var2.isNullable(var4));
         var1.setSigned(var4, var2.isSigned(var4));
         var1.setSearchable(var4, var2.isSearchable(var4));
         int var5 = var2.getColumnDisplaySize(var4);
         if (var5 < 0) {
            var5 = 0;
         }

         var1.setColumnDisplaySize(var4, var5);
         var1.setColumnLabel(var4, var2.getColumnLabel(var4));
         var1.setColumnName(var4, var2.getColumnName(var4));
         var1.setSchemaName(var4, var2.getSchemaName(var4));
         int var6 = var2.getPrecision(var4);
         if (var6 < 0) {
            var6 = 0;
         }

         var1.setPrecision(var4, var6);
         int var7 = var2.getScale(var4);
         if (var7 < 0) {
            var7 = 0;
         }

         var1.setScale(var4, var7);
         var1.setTableName(var4, var2.getTableName(var4));
         var1.setCatalogName(var4, var2.getCatalogName(var4));
         var1.setColumnType(var4, var2.getColumnType(var4));
         var1.setColumnTypeName(var4, var2.getColumnTypeName(var4));
      }

      if (this.conn != null) {
         this.dbmslocatorsUpdateCopy = this.conn.getMetaData().locatorsUpdateCopy();
      }

   }

   public void execute(Connection var1) throws SQLException {
      this.setConnection(var1);
      if (this.getPageSize() != 0) {
         this.crsReader = (CachedRowSetReader)this.provider.getRowSetReader();
         this.crsReader.setStartPosition(1);
         this.callWithCon = true;
         this.crsReader.readData(this);
      } else {
         this.rowSetReader.readData(this);
      }

      this.RowSetMD = (RowSetMetaDataImpl)this.getMetaData();
      if (var1 != null) {
         this.dbmslocatorsUpdateCopy = var1.getMetaData().locatorsUpdateCopy();
      }

   }

   private void setConnection(Connection var1) {
      this.conn = var1;
   }

   public void acceptChanges() throws SyncProviderException {
      if (this.onInsertRow) {
         throw new SyncProviderException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidop").toString());
      } else {
         int var1 = this.cursorPos;
         boolean var2 = false;
         boolean var3 = false;

         try {
            if (this.rowSetWriter != null) {
               var1 = this.cursorPos;
               var3 = this.rowSetWriter.writeData(this);
               this.cursorPos = var1;
            }

            if (this.tXWriter) {
               if (!var3) {
                  this.tWriter = (TransactionalWriter)this.rowSetWriter;
                  this.tWriter.rollback();
                  var2 = false;
               } else {
                  this.tWriter = (TransactionalWriter)this.rowSetWriter;
                  if (this.tWriter instanceof CachedRowSetWriter) {
                     ((CachedRowSetWriter)this.tWriter).commit(this, this.updateOnInsert);
                  } else {
                     this.tWriter.commit();
                  }

                  var2 = true;
               }
            }

            if (var2) {
               this.setOriginal();
            } else if (!var2) {
               throw new SyncProviderException(this.resBundle.handleGetObject("cachedrowsetimpl.accfailed").toString());
            }

         } catch (SyncProviderException var5) {
            throw var5;
         } catch (SQLException var6) {
            var6.printStackTrace();
            throw new SyncProviderException(var6.getMessage());
         } catch (SecurityException var7) {
            throw new SyncProviderException(var7.getMessage());
         }
      }
   }

   public void acceptChanges(Connection var1) throws SyncProviderException {
      this.setConnection(var1);
      this.acceptChanges();
   }

   public void restoreOriginal() throws SQLException {
      Iterator var2 = this.rvh.iterator();

      while(var2.hasNext()) {
         Row var1 = (Row)var2.next();
         if (var1.getInserted()) {
            var2.remove();
            --this.numRows;
         } else {
            if (var1.getDeleted()) {
               var1.clearDeleted();
            }

            if (var1.getUpdated()) {
               var1.clearUpdated();
            }
         }
      }

      this.cursorPos = 0;
      this.notifyRowSetChanged();
   }

   public void release() throws SQLException {
      this.initContainer();
      this.notifyRowSetChanged();
   }

   public void undoDelete() throws SQLException {
      if (this.getShowDeleted()) {
         this.checkCursor();
         if (this.onInsertRow) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
         } else {
            Row var1 = (Row)this.getCurrentRow();
            if (var1.getDeleted()) {
               var1.clearDeleted();
               --this.numDeleted;
               this.notifyRowChanged();
            }

         }
      }
   }

   public void undoInsert() throws SQLException {
      this.checkCursor();
      if (this.onInsertRow) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
      } else {
         Row var1 = (Row)this.getCurrentRow();
         if (var1.getInserted()) {
            this.rvh.remove(this.cursorPos - 1);
            --this.numRows;
            this.notifyRowChanged();
         } else {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.illegalop").toString());
         }
      }
   }

   public void undoUpdate() throws SQLException {
      this.moveToCurrentRow();
      this.undoDelete();
      this.undoInsert();
   }

   public RowSet createShared() throws SQLException {
      try {
         RowSet var1 = (RowSet)this.clone();
         return var1;
      } catch (CloneNotSupportedException var3) {
         throw new SQLException(var3.getMessage());
      }
   }

   protected Object clone() throws CloneNotSupportedException {
      return super.clone();
   }

   public CachedRowSet createCopy() throws SQLException {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream();

      try {
         ObjectOutputStream var1 = new ObjectOutputStream(var2);
         var1.writeObject(this);
      } catch (IOException var10) {
         throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.clonefail").toString(), var10.getMessage()));
      }

      ObjectInputStream var3;
      try {
         ByteArrayInputStream var4 = new ByteArrayInputStream(var2.toByteArray());
         var3 = new ObjectInputStream(var4);
      } catch (StreamCorruptedException var8) {
         throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.clonefail").toString(), var8.getMessage()));
      } catch (IOException var9) {
         throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.clonefail").toString(), var9.getMessage()));
      }

      try {
         CachedRowSetImpl var11 = (CachedRowSetImpl)var3.readObject();
         var11.resBundle = this.resBundle;
         return var11;
      } catch (ClassNotFoundException var5) {
         throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.clonefail").toString(), var5.getMessage()));
      } catch (OptionalDataException var6) {
         throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.clonefail").toString(), var6.getMessage()));
      } catch (IOException var7) {
         throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.clonefail").toString(), var7.getMessage()));
      }
   }

   public CachedRowSet createCopySchema() throws SQLException {
      int var1 = this.numRows;
      this.numRows = 0;
      CachedRowSet var2 = this.createCopy();
      this.numRows = var1;
      return var2;
   }

   public CachedRowSet createCopyNoConstraints() throws SQLException {
      CachedRowSetImpl var1 = (CachedRowSetImpl)this.createCopy();
      var1.initProperties();

      try {
         var1.unsetMatchColumn(var1.getMatchColumnIndexes());
      } catch (SQLException var4) {
      }

      try {
         var1.unsetMatchColumn(var1.getMatchColumnNames());
      } catch (SQLException var3) {
      }

      return var1;
   }

   public Collection<?> toCollection() throws SQLException {
      TreeMap var1 = new TreeMap();

      for(int var2 = 0; var2 < this.numRows; ++var2) {
         var1.put(var2, this.rvh.get(var2));
      }

      return var1.values();
   }

   public Collection<?> toCollection(int var1) throws SQLException {
      int var2 = this.numRows;
      Vector var3 = new Vector(var2);

      for(CachedRowSetImpl var4 = (CachedRowSetImpl)this.createCopy(); var2 != 0; --var2) {
         var4.next();
         var3.add(var4.getObject(var1));
      }

      return var3;
   }

   public Collection<?> toCollection(String var1) throws SQLException {
      return this.toCollection(this.getColIdxByName(var1));
   }

   public SyncProvider getSyncProvider() throws SQLException {
      return this.provider;
   }

   public void setSyncProvider(String var1) throws SQLException {
      this.provider = SyncFactory.getInstance(var1);
      this.rowSetReader = this.provider.getRowSetReader();
      this.rowSetWriter = this.provider.getRowSetWriter();
   }

   public void execute() throws SQLException {
      this.execute((Connection)null);
   }

   public boolean next() throws SQLException {
      if (this.cursorPos >= 0 && this.cursorPos < this.numRows + 1) {
         boolean var1 = this.internalNext();
         this.notifyCursorMoved();
         return var1;
      } else {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
      }
   }

   protected boolean internalNext() throws SQLException {
      boolean var1 = false;

      do {
         if (this.cursorPos < this.numRows) {
            ++this.cursorPos;
            var1 = true;
         } else if (this.cursorPos == this.numRows) {
            ++this.cursorPos;
            var1 = false;
            break;
         }
      } while(!this.getShowDeleted() && this.rowDeleted());

      if (var1) {
         ++this.absolutePos;
      } else {
         this.absolutePos = 0;
      }

      return var1;
   }

   public void close() throws SQLException {
      this.cursorPos = 0;
      this.absolutePos = 0;
      this.numRows = 0;
      this.numDeleted = 0;
      this.initProperties();
      this.rvh.clear();
   }

   public boolean wasNull() throws SQLException {
      return this.lastValueNull;
   }

   private void setLastValueNull(boolean var1) {
      this.lastValueNull = var1;
   }

   private void checkIndex(int var1) throws SQLException {
      if (var1 < 1 || var1 > this.RowSetMD.getColumnCount()) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcol").toString());
      }
   }

   private void checkCursor() throws SQLException {
      if (this.isAfterLast() || this.isBeforeFirst()) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
      }
   }

   private int getColIdxByName(String var1) throws SQLException {
      this.RowSetMD = (RowSetMetaDataImpl)this.getMetaData();
      int var2 = this.RowSetMD.getColumnCount();

      for(int var3 = 1; var3 <= var2; ++var3) {
         String var4 = this.RowSetMD.getColumnName(var3);
         if (var4 != null && var1.equalsIgnoreCase(var4)) {
            return var3;
         }
      }

      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalcolnm").toString());
   }

   protected BaseRow getCurrentRow() {
      return (BaseRow)(this.onInsertRow ? this.insertRow : (BaseRow)((BaseRow)this.rvh.get(this.cursorPos - 1)));
   }

   protected void removeCurrentRow() {
      ((Row)this.getCurrentRow()).setDeleted();
      this.rvh.remove(this.cursorPos - 1);
      --this.numRows;
   }

   public String getString(int var1) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      this.setLastValueNull(false);
      Object var2 = this.getCurrentRow().getColumnObject(var1);
      if (var2 == null) {
         this.setLastValueNull(true);
         return null;
      } else {
         return var2.toString();
      }
   }

   public boolean getBoolean(int var1) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      this.setLastValueNull(false);
      Object var2 = this.getCurrentRow().getColumnObject(var1);
      if (var2 == null) {
         this.setLastValueNull(true);
         return false;
      } else if (var2 instanceof Boolean) {
         return (Boolean)var2;
      } else {
         try {
            return Double.compare(Double.parseDouble(var2.toString()), 0.0D) != 0;
         } catch (NumberFormatException var4) {
            throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.boolfail").toString(), var2.toString().trim(), var1));
         }
      }
   }

   public byte getByte(int var1) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      this.setLastValueNull(false);
      Object var2 = this.getCurrentRow().getColumnObject(var1);
      if (var2 == null) {
         this.setLastValueNull(true);
         return 0;
      } else {
         try {
            return Byte.valueOf(var2.toString());
         } catch (NumberFormatException var4) {
            throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.bytefail").toString(), var2.toString().trim(), var1));
         }
      }
   }

   public short getShort(int var1) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      this.setLastValueNull(false);
      Object var2 = this.getCurrentRow().getColumnObject(var1);
      if (var2 == null) {
         this.setLastValueNull(true);
         return 0;
      } else {
         try {
            return Short.valueOf(var2.toString().trim());
         } catch (NumberFormatException var4) {
            throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.shortfail").toString(), var2.toString().trim(), var1));
         }
      }
   }

   public int getInt(int var1) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      this.setLastValueNull(false);
      Object var2 = this.getCurrentRow().getColumnObject(var1);
      if (var2 == null) {
         this.setLastValueNull(true);
         return 0;
      } else {
         try {
            return Integer.valueOf(var2.toString().trim());
         } catch (NumberFormatException var4) {
            throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.intfail").toString(), var2.toString().trim(), var1));
         }
      }
   }

   public long getLong(int var1) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      this.setLastValueNull(false);
      Object var2 = this.getCurrentRow().getColumnObject(var1);
      if (var2 == null) {
         this.setLastValueNull(true);
         return 0L;
      } else {
         try {
            return Long.valueOf(var2.toString().trim());
         } catch (NumberFormatException var4) {
            throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.longfail").toString(), var2.toString().trim(), var1));
         }
      }
   }

   public float getFloat(int var1) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      this.setLastValueNull(false);
      Object var2 = this.getCurrentRow().getColumnObject(var1);
      if (var2 == null) {
         this.setLastValueNull(true);
         return 0.0F;
      } else {
         try {
            return new Float(var2.toString());
         } catch (NumberFormatException var4) {
            throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.floatfail").toString(), var2.toString().trim(), var1));
         }
      }
   }

   public double getDouble(int var1) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      this.setLastValueNull(false);
      Object var2 = this.getCurrentRow().getColumnObject(var1);
      if (var2 == null) {
         this.setLastValueNull(true);
         return 0.0D;
      } else {
         try {
            return new Double(var2.toString().trim());
         } catch (NumberFormatException var4) {
            throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.doublefail").toString(), var2.toString().trim(), var1));
         }
      }
   }

   /** @deprecated */
   @Deprecated
   public BigDecimal getBigDecimal(int var1, int var2) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      this.setLastValueNull(false);
      Object var3 = this.getCurrentRow().getColumnObject(var1);
      if (var3 == null) {
         this.setLastValueNull(true);
         return new BigDecimal(0);
      } else {
         BigDecimal var4 = this.getBigDecimal(var1);
         BigDecimal var5 = var4.setScale(var2);
         return var5;
      }
   }

   public byte[] getBytes(int var1) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      if (!this.isBinary(this.RowSetMD.getColumnType(var1))) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
      } else {
         return (byte[])((byte[])this.getCurrentRow().getColumnObject(var1));
      }
   }

   public Date getDate(int var1) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      this.setLastValueNull(false);
      Object var2 = this.getCurrentRow().getColumnObject(var1);
      if (var2 == null) {
         this.setLastValueNull(true);
         return null;
      } else {
         long var3;
         switch(this.RowSetMD.getColumnType(var1)) {
         case -1:
         case 1:
         case 12:
            try {
               DateFormat var6 = DateFormat.getDateInstance();
               return (Date)((Date)var6.parse(var2.toString()));
            } catch (ParseException var5) {
               throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.datefail").toString(), var2.toString().trim(), var1));
            }
         case 91:
            var3 = ((Date)var2).getTime();
            return new Date(var3);
         case 93:
            var3 = ((Timestamp)var2).getTime();
            return new Date(var3);
         default:
            throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.datefail").toString(), var2.toString().trim(), var1));
         }
      }
   }

   public Time getTime(int var1) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      this.setLastValueNull(false);
      Object var2 = this.getCurrentRow().getColumnObject(var1);
      if (var2 == null) {
         this.setLastValueNull(true);
         return null;
      } else {
         switch(this.RowSetMD.getColumnType(var1)) {
         case -1:
         case 1:
         case 12:
            try {
               DateFormat var6 = DateFormat.getTimeInstance();
               return (Time)((Time)var6.parse(var2.toString()));
            } catch (ParseException var5) {
               throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.timefail").toString(), var2.toString().trim(), var1));
            }
         case 92:
            return (Time)var2;
         case 93:
            long var3 = ((Timestamp)var2).getTime();
            return new Time(var3);
         default:
            throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.timefail").toString(), var2.toString().trim(), var1));
         }
      }
   }

   public Timestamp getTimestamp(int var1) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      this.setLastValueNull(false);
      Object var2 = this.getCurrentRow().getColumnObject(var1);
      if (var2 == null) {
         this.setLastValueNull(true);
         return null;
      } else {
         long var3;
         switch(this.RowSetMD.getColumnType(var1)) {
         case -1:
         case 1:
         case 12:
            try {
               DateFormat var6 = DateFormat.getTimeInstance();
               return (Timestamp)((Timestamp)var6.parse(var2.toString()));
            } catch (ParseException var5) {
               throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.timefail").toString(), var2.toString().trim(), var1));
            }
         case 91:
            var3 = ((Date)var2).getTime();
            return new Timestamp(var3);
         case 92:
            var3 = ((Time)var2).getTime();
            return new Timestamp(var3);
         case 93:
            return (Timestamp)var2;
         default:
            throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.timefail").toString(), var2.toString().trim(), var1));
         }
      }
   }

   public InputStream getAsciiStream(int var1) throws SQLException {
      this.asciiStream = null;
      this.checkIndex(var1);
      this.checkCursor();
      Object var2 = this.getCurrentRow().getColumnObject(var1);
      if (var2 == null) {
         this.lastValueNull = true;
         return null;
      } else {
         try {
            if (!this.isString(this.RowSetMD.getColumnType(var1))) {
               throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
            }

            this.asciiStream = new ByteArrayInputStream(((String)var2).getBytes("ASCII"));
         } catch (UnsupportedEncodingException var4) {
            throw new SQLException(var4.getMessage());
         }

         return this.asciiStream;
      }
   }

   /** @deprecated */
   @Deprecated
   public InputStream getUnicodeStream(int var1) throws SQLException {
      this.unicodeStream = null;
      this.checkIndex(var1);
      this.checkCursor();
      if (!this.isBinary(this.RowSetMD.getColumnType(var1)) && !this.isString(this.RowSetMD.getColumnType(var1))) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
      } else {
         Object var2 = this.getCurrentRow().getColumnObject(var1);
         if (var2 == null) {
            this.lastValueNull = true;
            return null;
         } else {
            this.unicodeStream = new StringBufferInputStream(var2.toString());
            return this.unicodeStream;
         }
      }
   }

   public InputStream getBinaryStream(int var1) throws SQLException {
      this.binaryStream = null;
      this.checkIndex(var1);
      this.checkCursor();
      if (!this.isBinary(this.RowSetMD.getColumnType(var1))) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
      } else {
         Object var2 = this.getCurrentRow().getColumnObject(var1);
         if (var2 == null) {
            this.lastValueNull = true;
            return null;
         } else {
            this.binaryStream = new ByteArrayInputStream((byte[])((byte[])var2));
            return this.binaryStream;
         }
      }
   }

   public String getString(String var1) throws SQLException {
      return this.getString(this.getColIdxByName(var1));
   }

   public boolean getBoolean(String var1) throws SQLException {
      return this.getBoolean(this.getColIdxByName(var1));
   }

   public byte getByte(String var1) throws SQLException {
      return this.getByte(this.getColIdxByName(var1));
   }

   public short getShort(String var1) throws SQLException {
      return this.getShort(this.getColIdxByName(var1));
   }

   public int getInt(String var1) throws SQLException {
      return this.getInt(this.getColIdxByName(var1));
   }

   public long getLong(String var1) throws SQLException {
      return this.getLong(this.getColIdxByName(var1));
   }

   public float getFloat(String var1) throws SQLException {
      return this.getFloat(this.getColIdxByName(var1));
   }

   public double getDouble(String var1) throws SQLException {
      return this.getDouble(this.getColIdxByName(var1));
   }

   /** @deprecated */
   @Deprecated
   public BigDecimal getBigDecimal(String var1, int var2) throws SQLException {
      return this.getBigDecimal(this.getColIdxByName(var1), var2);
   }

   public byte[] getBytes(String var1) throws SQLException {
      return this.getBytes(this.getColIdxByName(var1));
   }

   public Date getDate(String var1) throws SQLException {
      return this.getDate(this.getColIdxByName(var1));
   }

   public Time getTime(String var1) throws SQLException {
      return this.getTime(this.getColIdxByName(var1));
   }

   public Timestamp getTimestamp(String var1) throws SQLException {
      return this.getTimestamp(this.getColIdxByName(var1));
   }

   public InputStream getAsciiStream(String var1) throws SQLException {
      return this.getAsciiStream(this.getColIdxByName(var1));
   }

   /** @deprecated */
   @Deprecated
   public InputStream getUnicodeStream(String var1) throws SQLException {
      return this.getUnicodeStream(this.getColIdxByName(var1));
   }

   public InputStream getBinaryStream(String var1) throws SQLException {
      return this.getBinaryStream(this.getColIdxByName(var1));
   }

   public SQLWarning getWarnings() {
      return this.sqlwarn;
   }

   public void clearWarnings() {
      this.sqlwarn = null;
   }

   public String getCursorName() throws SQLException {
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.posupdate").toString());
   }

   public ResultSetMetaData getMetaData() throws SQLException {
      return this.RowSetMD;
   }

   public Object getObject(int var1) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      this.setLastValueNull(false);
      Object var2 = this.getCurrentRow().getColumnObject(var1);
      if (var2 == null) {
         this.setLastValueNull(true);
         return null;
      } else {
         if (var2 instanceof Struct) {
            Struct var4 = (Struct)var2;
            Map var3 = this.getTypeMap();
            Class var5 = (Class)var3.get(var4.getSQLTypeName());
            if (var5 != null) {
               SQLData var6 = null;

               try {
                  var6 = (SQLData)ReflectUtil.newInstance(var5);
               } catch (Exception var9) {
                  throw new SQLException("Unable to Instantiate: ", var9);
               }

               Object[] var7 = var4.getAttributes(var3);
               SQLInputImpl var8 = new SQLInputImpl(var7, var3);
               var6.readSQL(var8, var4.getSQLTypeName());
               return var6;
            }
         }

         return var2;
      }
   }

   public Object getObject(String var1) throws SQLException {
      return this.getObject(this.getColIdxByName(var1));
   }

   public int findColumn(String var1) throws SQLException {
      return this.getColIdxByName(var1);
   }

   public Reader getCharacterStream(int var1) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      Object var2;
      if (this.isBinary(this.RowSetMD.getColumnType(var1))) {
         var2 = this.getCurrentRow().getColumnObject(var1);
         if (var2 == null) {
            this.lastValueNull = true;
            return null;
         }

         this.charStream = new InputStreamReader(new ByteArrayInputStream((byte[])((byte[])var2)));
      } else {
         if (!this.isString(this.RowSetMD.getColumnType(var1))) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
         }

         var2 = this.getCurrentRow().getColumnObject(var1);
         if (var2 == null) {
            this.lastValueNull = true;
            return null;
         }

         this.charStream = new StringReader(var2.toString());
      }

      return this.charStream;
   }

   public Reader getCharacterStream(String var1) throws SQLException {
      return this.getCharacterStream(this.getColIdxByName(var1));
   }

   public BigDecimal getBigDecimal(int var1) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      this.setLastValueNull(false);
      Object var2 = this.getCurrentRow().getColumnObject(var1);
      if (var2 == null) {
         this.setLastValueNull(true);
         return null;
      } else {
         try {
            return new BigDecimal(var2.toString().trim());
         } catch (NumberFormatException var4) {
            throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.doublefail").toString(), var2.toString().trim(), var1));
         }
      }
   }

   public BigDecimal getBigDecimal(String var1) throws SQLException {
      return this.getBigDecimal(this.getColIdxByName(var1));
   }

   public int size() {
      return this.numRows;
   }

   public boolean isBeforeFirst() throws SQLException {
      return this.cursorPos == 0 && this.numRows > 0;
   }

   public boolean isAfterLast() throws SQLException {
      return this.cursorPos == this.numRows + 1 && this.numRows > 0;
   }

   public boolean isFirst() throws SQLException {
      int var1 = this.cursorPos;
      int var2 = this.absolutePos;
      this.internalFirst();
      if (this.cursorPos == var1) {
         return true;
      } else {
         this.cursorPos = var1;
         this.absolutePos = var2;
         return false;
      }
   }

   public boolean isLast() throws SQLException {
      int var1 = this.cursorPos;
      int var2 = this.absolutePos;
      boolean var3 = this.getShowDeleted();
      this.setShowDeleted(true);
      this.internalLast();
      if (this.cursorPos == var1) {
         this.setShowDeleted(var3);
         return true;
      } else {
         this.setShowDeleted(var3);
         this.cursorPos = var1;
         this.absolutePos = var2;
         return false;
      }
   }

   public void beforeFirst() throws SQLException {
      if (this.getType() == 1003) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.beforefirst").toString());
      } else {
         this.cursorPos = 0;
         this.absolutePos = 0;
         this.notifyCursorMoved();
      }
   }

   public void afterLast() throws SQLException {
      if (this.numRows > 0) {
         this.cursorPos = this.numRows + 1;
         this.absolutePos = 0;
         this.notifyCursorMoved();
      }

   }

   public boolean first() throws SQLException {
      if (this.getType() == 1003) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.first").toString());
      } else {
         boolean var1 = this.internalFirst();
         this.notifyCursorMoved();
         return var1;
      }
   }

   protected boolean internalFirst() throws SQLException {
      boolean var1 = false;
      if (this.numRows > 0) {
         this.cursorPos = 1;
         if (!this.getShowDeleted() && this.rowDeleted()) {
            var1 = this.internalNext();
         } else {
            var1 = true;
         }
      }

      if (var1) {
         this.absolutePos = 1;
      } else {
         this.absolutePos = 0;
      }

      return var1;
   }

   public boolean last() throws SQLException {
      if (this.getType() == 1003) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.last").toString());
      } else {
         boolean var1 = this.internalLast();
         this.notifyCursorMoved();
         return var1;
      }
   }

   protected boolean internalLast() throws SQLException {
      boolean var1 = false;
      if (this.numRows > 0) {
         this.cursorPos = this.numRows;
         if (!this.getShowDeleted() && this.rowDeleted()) {
            var1 = this.internalPrevious();
         } else {
            var1 = true;
         }
      }

      if (var1) {
         this.absolutePos = this.numRows - this.numDeleted;
      } else {
         this.absolutePos = 0;
      }

      return var1;
   }

   public int getRow() throws SQLException {
      if (this.numRows > 0 && this.cursorPos > 0 && this.cursorPos < this.numRows + 1 && !this.getShowDeleted() && !this.rowDeleted()) {
         return this.absolutePos;
      } else {
         return this.getShowDeleted() ? this.cursorPos : 0;
      }
   }

   public boolean absolute(int var1) throws SQLException {
      if (var1 != 0 && this.getType() != 1003) {
         if (var1 > 0) {
            if (var1 > this.numRows) {
               this.afterLast();
               return false;
            }

            if (this.absolutePos <= 0) {
               this.internalFirst();
            }
         } else {
            if (this.cursorPos + var1 < 0) {
               this.beforeFirst();
               return false;
            }

            if (this.absolutePos >= 0) {
               this.internalLast();
            }
         }

         while(this.absolutePos != var1) {
            if (this.absolutePos < var1) {
               if (!this.internalNext()) {
                  break;
               }
            } else if (!this.internalPrevious()) {
               break;
            }
         }

         this.notifyCursorMoved();
         return !this.isAfterLast() && !this.isBeforeFirst();
      } else {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.absolute").toString());
      }
   }

   public boolean relative(int var1) throws SQLException {
      if (this.numRows != 0 && !this.isBeforeFirst() && !this.isAfterLast() && this.getType() != 1003) {
         if (var1 == 0) {
            return true;
         } else {
            int var2;
            if (var1 > 0) {
               if (this.cursorPos + var1 > this.numRows) {
                  this.afterLast();
               } else {
                  for(var2 = 0; var2 < var1 && this.internalNext(); ++var2) {
                  }
               }
            } else if (this.cursorPos + var1 < 0) {
               this.beforeFirst();
            } else {
               for(var2 = var1; var2 < 0 && this.internalPrevious(); ++var2) {
               }
            }

            this.notifyCursorMoved();
            return !this.isAfterLast() && !this.isBeforeFirst();
         }
      } else {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.relative").toString());
      }
   }

   public boolean previous() throws SQLException {
      if (this.getType() == 1003) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.last").toString());
      } else if (this.cursorPos >= 0 && this.cursorPos <= this.numRows + 1) {
         boolean var1 = this.internalPrevious();
         this.notifyCursorMoved();
         return var1;
      } else {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
      }
   }

   protected boolean internalPrevious() throws SQLException {
      boolean var1 = false;

      do {
         if (this.cursorPos > 1) {
            --this.cursorPos;
            var1 = true;
         } else if (this.cursorPos == 1) {
            --this.cursorPos;
            var1 = false;
            break;
         }
      } while(!this.getShowDeleted() && this.rowDeleted());

      if (var1) {
         --this.absolutePos;
      } else {
         this.absolutePos = 0;
      }

      return var1;
   }

   public boolean rowUpdated() throws SQLException {
      this.checkCursor();
      if (this.onInsertRow) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidop").toString());
      } else {
         return ((Row)this.getCurrentRow()).getUpdated();
      }
   }

   public boolean columnUpdated(int var1) throws SQLException {
      this.checkCursor();
      if (this.onInsertRow) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidop").toString());
      } else {
         return ((Row)this.getCurrentRow()).getColUpdated(var1 - 1);
      }
   }

   public boolean columnUpdated(String var1) throws SQLException {
      return this.columnUpdated(this.getColIdxByName(var1));
   }

   public boolean rowInserted() throws SQLException {
      this.checkCursor();
      if (this.onInsertRow) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidop").toString());
      } else {
         return ((Row)this.getCurrentRow()).getInserted();
      }
   }

   public boolean rowDeleted() throws SQLException {
      if (!this.isAfterLast() && !this.isBeforeFirst() && !this.onInsertRow) {
         return ((Row)this.getCurrentRow()).getDeleted();
      } else {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
      }
   }

   private boolean isNumeric(int var1) {
      switch(var1) {
      case -7:
      case -6:
      case -5:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
         return true;
      case -4:
      case -3:
      case -2:
      case -1:
      case 0:
      case 1:
      default:
         return false;
      }
   }

   private boolean isString(int var1) {
      switch(var1) {
      case -1:
      case 1:
      case 12:
         return true;
      default:
         return false;
      }
   }

   private boolean isBinary(int var1) {
      switch(var1) {
      case -4:
      case -3:
      case -2:
         return true;
      default:
         return false;
      }
   }

   private boolean isTemporal(int var1) {
      switch(var1) {
      case 91:
      case 92:
      case 93:
         return true;
      default:
         return false;
      }
   }

   private boolean isBoolean(int var1) {
      switch(var1) {
      case -7:
      case 16:
         return true;
      default:
         return false;
      }
   }

   private Object convertNumeric(Object var1, int var2, int var3) throws SQLException {
      if (var2 == var3) {
         return var1;
      } else if (!this.isNumeric(var3) && !this.isString(var3)) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString() + var3);
      } else {
         try {
            switch(var3) {
            case -7:
               Integer var4 = Integer.valueOf(var1.toString().trim());
               return var4.equals(0) ? false : true;
            case -6:
               return Byte.valueOf(var1.toString().trim());
            case -5:
               return Long.valueOf(var1.toString().trim());
            case -4:
            case -3:
            case -2:
            case 0:
            case 9:
            case 10:
            case 11:
            default:
               throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString() + var3);
            case -1:
            case 1:
            case 12:
               return var1.toString();
            case 2:
            case 3:
               return new BigDecimal(var1.toString().trim());
            case 4:
               return Integer.valueOf(var1.toString().trim());
            case 5:
               return Short.valueOf(var1.toString().trim());
            case 6:
            case 7:
               return new Float(var1.toString().trim());
            case 8:
               return new Double(var1.toString().trim());
            }
         } catch (NumberFormatException var5) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString() + var3);
         }
      }
   }

   private Object convertTemporal(Object var1, int var2, int var3) throws SQLException {
      if (var2 == var3) {
         return var1;
      } else if (!this.isNumeric(var3) && (this.isString(var3) || this.isTemporal(var3))) {
         try {
            switch(var3) {
            case -1:
            case 1:
            case 12:
               return var1.toString();
            case 91:
               if (var2 == 93) {
                  return new Date(((Timestamp)var1).getTime());
               }

               throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
            case 92:
               if (var2 == 93) {
                  return new Time(((Timestamp)var1).getTime());
               }

               throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
            case 93:
               if (var2 == 92) {
                  return new Timestamp(((Time)var1).getTime());
               }

               return new Timestamp(((Date)var1).getTime());
            default:
               throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
            }
         } catch (NumberFormatException var5) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
         }
      } else {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
      }
   }

   private Object convertBoolean(Object var1, int var2, int var3) throws SQLException {
      if (var2 == var3) {
         return var1;
      } else if (!this.isNumeric(var3) && (this.isString(var3) || this.isBoolean(var3))) {
         try {
            switch(var3) {
            case -7:
               Integer var4 = Integer.valueOf(var1.toString().trim());
               return var4.equals(0) ? false : true;
            case 16:
               return Boolean.valueOf(var1.toString().trim());
            default:
               throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString() + var3);
            }
         } catch (NumberFormatException var5) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString() + var3);
         }
      } else {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
      }
   }

   public void updateNull(int var1) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      BaseRow var2 = this.getCurrentRow();
      var2.setColumnObject(var1, (Object)null);
   }

   public void updateBoolean(int var1, boolean var2) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      Object var3 = this.convertBoolean(var2, -7, this.RowSetMD.getColumnType(var1));
      this.getCurrentRow().setColumnObject(var1, var3);
   }

   public void updateByte(int var1, byte var2) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      Object var3 = this.convertNumeric(var2, -6, this.RowSetMD.getColumnType(var1));
      this.getCurrentRow().setColumnObject(var1, var3);
   }

   public void updateShort(int var1, short var2) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      Object var3 = this.convertNumeric(var2, 5, this.RowSetMD.getColumnType(var1));
      this.getCurrentRow().setColumnObject(var1, var3);
   }

   public void updateInt(int var1, int var2) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      Object var3 = this.convertNumeric(var2, 4, this.RowSetMD.getColumnType(var1));
      this.getCurrentRow().setColumnObject(var1, var3);
   }

   public void updateLong(int var1, long var2) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      Object var4 = this.convertNumeric(var2, -5, this.RowSetMD.getColumnType(var1));
      this.getCurrentRow().setColumnObject(var1, var4);
   }

   public void updateFloat(int var1, float var2) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      Object var3 = this.convertNumeric(var2, 7, this.RowSetMD.getColumnType(var1));
      this.getCurrentRow().setColumnObject(var1, var3);
   }

   public void updateDouble(int var1, double var2) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      Object var4 = this.convertNumeric(var2, 8, this.RowSetMD.getColumnType(var1));
      this.getCurrentRow().setColumnObject(var1, var4);
   }

   public void updateBigDecimal(int var1, BigDecimal var2) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      Object var3 = this.convertNumeric(var2, 2, this.RowSetMD.getColumnType(var1));
      this.getCurrentRow().setColumnObject(var1, var3);
   }

   public void updateString(int var1, String var2) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      this.getCurrentRow().setColumnObject(var1, var2);
   }

   public void updateBytes(int var1, byte[] var2) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      if (!this.isBinary(this.RowSetMD.getColumnType(var1))) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
      } else {
         this.getCurrentRow().setColumnObject(var1, var2);
      }
   }

   public void updateDate(int var1, Date var2) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      Object var3 = this.convertTemporal(var2, 91, this.RowSetMD.getColumnType(var1));
      this.getCurrentRow().setColumnObject(var1, var3);
   }

   public void updateTime(int var1, Time var2) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      Object var3 = this.convertTemporal(var2, 92, this.RowSetMD.getColumnType(var1));
      this.getCurrentRow().setColumnObject(var1, var3);
   }

   public void updateTimestamp(int var1, Timestamp var2) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      Object var3 = this.convertTemporal(var2, 93, this.RowSetMD.getColumnType(var1));
      this.getCurrentRow().setColumnObject(var1, var3);
   }

   public void updateAsciiStream(int var1, InputStream var2, int var3) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      if (!this.isString(this.RowSetMD.getColumnType(var1)) && !this.isBinary(this.RowSetMD.getColumnType(var1))) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
      } else {
         byte[] var4 = new byte[var3];

         try {
            int var5 = 0;

            do {
               var5 += var2.read(var4, var5, var3 - var5);
            } while(var5 != var3);
         } catch (IOException var6) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.asciistream").toString());
         }

         String var7 = new String(var4);
         this.getCurrentRow().setColumnObject(var1, var7);
      }
   }

   public void updateBinaryStream(int var1, InputStream var2, int var3) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      if (!this.isBinary(this.RowSetMD.getColumnType(var1))) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
      } else {
         byte[] var4 = new byte[var3];

         try {
            int var5 = 0;

            do {
               var5 += var2.read(var4, var5, var3 - var5);
            } while(var5 != -1);
         } catch (IOException var6) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.binstream").toString());
         }

         this.getCurrentRow().setColumnObject(var1, var4);
      }
   }

   public void updateCharacterStream(int var1, Reader var2, int var3) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      if (!this.isString(this.RowSetMD.getColumnType(var1)) && !this.isBinary(this.RowSetMD.getColumnType(var1))) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
      } else {
         char[] var4 = new char[var3];

         try {
            int var5 = 0;

            do {
               var5 += var2.read(var4, var5, var3 - var5);
            } while(var5 != var3);
         } catch (IOException var6) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.binstream").toString());
         }

         String var7 = new String(var4);
         this.getCurrentRow().setColumnObject(var1, var7);
      }
   }

   public void updateObject(int var1, Object var2, int var3) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      int var4 = this.RowSetMD.getColumnType(var1);
      if (var4 == 3 || var4 == 2) {
         ((BigDecimal)var2).setScale(var3);
      }

      this.getCurrentRow().setColumnObject(var1, var2);
   }

   public void updateObject(int var1, Object var2) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      this.getCurrentRow().setColumnObject(var1, var2);
   }

   public void updateNull(String var1) throws SQLException {
      this.updateNull(this.getColIdxByName(var1));
   }

   public void updateBoolean(String var1, boolean var2) throws SQLException {
      this.updateBoolean(this.getColIdxByName(var1), var2);
   }

   public void updateByte(String var1, byte var2) throws SQLException {
      this.updateByte(this.getColIdxByName(var1), var2);
   }

   public void updateShort(String var1, short var2) throws SQLException {
      this.updateShort(this.getColIdxByName(var1), var2);
   }

   public void updateInt(String var1, int var2) throws SQLException {
      this.updateInt(this.getColIdxByName(var1), var2);
   }

   public void updateLong(String var1, long var2) throws SQLException {
      this.updateLong(this.getColIdxByName(var1), var2);
   }

   public void updateFloat(String var1, float var2) throws SQLException {
      this.updateFloat(this.getColIdxByName(var1), var2);
   }

   public void updateDouble(String var1, double var2) throws SQLException {
      this.updateDouble(this.getColIdxByName(var1), var2);
   }

   public void updateBigDecimal(String var1, BigDecimal var2) throws SQLException {
      this.updateBigDecimal(this.getColIdxByName(var1), var2);
   }

   public void updateString(String var1, String var2) throws SQLException {
      this.updateString(this.getColIdxByName(var1), var2);
   }

   public void updateBytes(String var1, byte[] var2) throws SQLException {
      this.updateBytes(this.getColIdxByName(var1), var2);
   }

   public void updateDate(String var1, Date var2) throws SQLException {
      this.updateDate(this.getColIdxByName(var1), var2);
   }

   public void updateTime(String var1, Time var2) throws SQLException {
      this.updateTime(this.getColIdxByName(var1), var2);
   }

   public void updateTimestamp(String var1, Timestamp var2) throws SQLException {
      this.updateTimestamp(this.getColIdxByName(var1), var2);
   }

   public void updateAsciiStream(String var1, InputStream var2, int var3) throws SQLException {
      this.updateAsciiStream(this.getColIdxByName(var1), var2, var3);
   }

   public void updateBinaryStream(String var1, InputStream var2, int var3) throws SQLException {
      this.updateBinaryStream(this.getColIdxByName(var1), var2, var3);
   }

   public void updateCharacterStream(String var1, Reader var2, int var3) throws SQLException {
      this.updateCharacterStream(this.getColIdxByName(var1), var2, var3);
   }

   public void updateObject(String var1, Object var2, int var3) throws SQLException {
      this.updateObject(this.getColIdxByName(var1), var2, var3);
   }

   public void updateObject(String var1, Object var2) throws SQLException {
      this.updateObject(this.getColIdxByName(var1), var2);
   }

   public void insertRow() throws SQLException {
      if (this.onInsertRow && this.insertRow.isCompleteRow(this.RowSetMD)) {
         Object[] var2 = this.getParams();

         for(int var3 = 0; var3 < var2.length; ++var3) {
            this.insertRow.setColumnObject(var3 + 1, var2[var3]);
         }

         Row var4 = new Row(this.RowSetMD.getColumnCount(), this.insertRow.getOrigRow());
         var4.setInserted();
         int var1;
         if (this.currentRow < this.numRows && this.currentRow >= 0) {
            var1 = this.currentRow;
         } else {
            var1 = this.numRows;
         }

         this.rvh.add(var1, var4);
         ++this.numRows;
         this.notifyRowChanged();
      } else {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.failedins").toString());
      }
   }

   public void updateRow() throws SQLException {
      if (this.onInsertRow) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.updateins").toString());
      } else {
         ((Row)this.getCurrentRow()).setUpdated();
         this.notifyRowChanged();
      }
   }

   public void deleteRow() throws SQLException {
      this.checkCursor();
      ((Row)this.getCurrentRow()).setDeleted();
      ++this.numDeleted;
      this.notifyRowChanged();
   }

   public void refreshRow() throws SQLException {
      this.checkCursor();
      if (this.onInsertRow) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
      } else {
         Row var1 = (Row)this.getCurrentRow();
         var1.clearUpdated();
      }
   }

   public void cancelRowUpdates() throws SQLException {
      this.checkCursor();
      if (this.onInsertRow) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
      } else {
         Row var1 = (Row)this.getCurrentRow();
         if (var1.getUpdated()) {
            var1.clearUpdated();
            this.notifyRowChanged();
         }

      }
   }

   public void moveToInsertRow() throws SQLException {
      if (this.getConcurrency() == 1007) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.movetoins").toString());
      } else {
         if (this.insertRow == null) {
            if (this.RowSetMD == null) {
               throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.movetoins1").toString());
            }

            int var1 = this.RowSetMD.getColumnCount();
            if (var1 <= 0) {
               throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.movetoins2").toString());
            }

            this.insertRow = new InsertRow(var1);
         }

         this.onInsertRow = true;
         this.currentRow = this.cursorPos;
         this.cursorPos = -1;
         this.insertRow.initInsertRow();
      }
   }

   public void moveToCurrentRow() throws SQLException {
      if (this.onInsertRow) {
         this.cursorPos = this.currentRow;
         this.onInsertRow = false;
      }
   }

   public Statement getStatement() throws SQLException {
      return null;
   }

   public Object getObject(int var1, Map<String, Class<?>> var2) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      this.setLastValueNull(false);
      Object var3 = this.getCurrentRow().getColumnObject(var1);
      if (var3 == null) {
         this.setLastValueNull(true);
         return null;
      } else {
         if (var3 instanceof Struct) {
            Struct var4 = (Struct)var3;
            Class var5 = (Class)var2.get(var4.getSQLTypeName());
            if (var5 != null) {
               SQLData var6 = null;

               try {
                  var6 = (SQLData)ReflectUtil.newInstance(var5);
               } catch (Exception var9) {
                  throw new SQLException("Unable to Instantiate: ", var9);
               }

               Object[] var7 = var4.getAttributes(var2);
               SQLInputImpl var8 = new SQLInputImpl(var7, var2);
               var6.readSQL(var8, var4.getSQLTypeName());
               return var6;
            }
         }

         return var3;
      }
   }

   public Ref getRef(int var1) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      if (this.RowSetMD.getColumnType(var1) != 2006) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
      } else {
         this.setLastValueNull(false);
         Ref var2 = (Ref)((Ref)this.getCurrentRow().getColumnObject(var1));
         if (var2 == null) {
            this.setLastValueNull(true);
            return null;
         } else {
            return var2;
         }
      }
   }

   public Blob getBlob(int var1) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      if (this.RowSetMD.getColumnType(var1) != 2004) {
         System.out.println(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.type").toString(), this.RowSetMD.getColumnType(var1)));
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
      } else {
         this.setLastValueNull(false);
         Blob var2 = (Blob)((Blob)this.getCurrentRow().getColumnObject(var1));
         if (var2 == null) {
            this.setLastValueNull(true);
            return null;
         } else {
            return var2;
         }
      }
   }

   public Clob getClob(int var1) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      if (this.RowSetMD.getColumnType(var1) != 2005) {
         System.out.println(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.type").toString(), this.RowSetMD.getColumnType(var1)));
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
      } else {
         this.setLastValueNull(false);
         Clob var2 = (Clob)((Clob)this.getCurrentRow().getColumnObject(var1));
         if (var2 == null) {
            this.setLastValueNull(true);
            return null;
         } else {
            return var2;
         }
      }
   }

   public Array getArray(int var1) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      if (this.RowSetMD.getColumnType(var1) != 2003) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
      } else {
         this.setLastValueNull(false);
         Array var2 = (Array)((Array)this.getCurrentRow().getColumnObject(var1));
         if (var2 == null) {
            this.setLastValueNull(true);
            return null;
         } else {
            return var2;
         }
      }
   }

   public Object getObject(String var1, Map<String, Class<?>> var2) throws SQLException {
      return this.getObject(this.getColIdxByName(var1), var2);
   }

   public Ref getRef(String var1) throws SQLException {
      return this.getRef(this.getColIdxByName(var1));
   }

   public Blob getBlob(String var1) throws SQLException {
      return this.getBlob(this.getColIdxByName(var1));
   }

   public Clob getClob(String var1) throws SQLException {
      return this.getClob(this.getColIdxByName(var1));
   }

   public Array getArray(String var1) throws SQLException {
      return this.getArray(this.getColIdxByName(var1));
   }

   public Date getDate(int var1, Calendar var2) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      this.setLastValueNull(false);
      Object var3 = this.getCurrentRow().getColumnObject(var1);
      if (var3 == null) {
         this.setLastValueNull(true);
         return null;
      } else {
         var3 = this.convertTemporal(var3, this.RowSetMD.getColumnType(var1), 91);
         Calendar var4 = Calendar.getInstance();
         var4.setTime((java.util.Date)var3);
         var2.set(1, var4.get(1));
         var2.set(2, var4.get(2));
         var2.set(5, var4.get(5));
         return new Date(var2.getTime().getTime());
      }
   }

   public Date getDate(String var1, Calendar var2) throws SQLException {
      return this.getDate(this.getColIdxByName(var1), var2);
   }

   public Time getTime(int var1, Calendar var2) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      this.setLastValueNull(false);
      Object var3 = this.getCurrentRow().getColumnObject(var1);
      if (var3 == null) {
         this.setLastValueNull(true);
         return null;
      } else {
         var3 = this.convertTemporal(var3, this.RowSetMD.getColumnType(var1), 92);
         Calendar var4 = Calendar.getInstance();
         var4.setTime((java.util.Date)var3);
         var2.set(11, var4.get(11));
         var2.set(12, var4.get(12));
         var2.set(13, var4.get(13));
         return new Time(var2.getTime().getTime());
      }
   }

   public Time getTime(String var1, Calendar var2) throws SQLException {
      return this.getTime(this.getColIdxByName(var1), var2);
   }

   public Timestamp getTimestamp(int var1, Calendar var2) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      this.setLastValueNull(false);
      Object var3 = this.getCurrentRow().getColumnObject(var1);
      if (var3 == null) {
         this.setLastValueNull(true);
         return null;
      } else {
         var3 = this.convertTemporal(var3, this.RowSetMD.getColumnType(var1), 93);
         Calendar var4 = Calendar.getInstance();
         var4.setTime((java.util.Date)var3);
         var2.set(1, var4.get(1));
         var2.set(2, var4.get(2));
         var2.set(5, var4.get(5));
         var2.set(11, var4.get(11));
         var2.set(12, var4.get(12));
         var2.set(13, var4.get(13));
         return new Timestamp(var2.getTime().getTime());
      }
   }

   public Timestamp getTimestamp(String var1, Calendar var2) throws SQLException {
      return this.getTimestamp(this.getColIdxByName(var1), var2);
   }

   public Connection getConnection() throws SQLException {
      return this.conn;
   }

   public void setMetaData(RowSetMetaData var1) throws SQLException {
      this.RowSetMD = (RowSetMetaDataImpl)var1;
   }

   public ResultSet getOriginal() throws SQLException {
      CachedRowSetImpl var1 = new CachedRowSetImpl();
      var1.RowSetMD = this.RowSetMD;
      var1.numRows = this.numRows;
      var1.cursorPos = 0;
      int var2 = this.RowSetMD.getColumnCount();
      Iterator var4 = this.rvh.iterator();

      while(var4.hasNext()) {
         Row var3 = new Row(var2, ((Row)var4.next()).getOrigRow());
         var1.rvh.add(var3);
      }

      return var1;
   }

   public ResultSet getOriginalRow() throws SQLException {
      CachedRowSetImpl var1 = new CachedRowSetImpl();
      var1.RowSetMD = this.RowSetMD;
      var1.numRows = 1;
      var1.cursorPos = 0;
      var1.setTypeMap(this.getTypeMap());
      Row var2 = new Row(this.RowSetMD.getColumnCount(), this.getCurrentRow().getOrigRow());
      var1.rvh.add(var2);
      return var1;
   }

   public void setOriginalRow() throws SQLException {
      if (this.onInsertRow) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidop").toString());
      } else {
         Row var1 = (Row)this.getCurrentRow();
         this.makeRowOriginal(var1);
         if (var1.getDeleted()) {
            this.removeCurrentRow();
         }

      }
   }

   private void makeRowOriginal(Row var1) {
      if (var1.getInserted()) {
         var1.clearInserted();
      }

      if (var1.getUpdated()) {
         var1.moveCurrentToOrig();
      }

   }

   public void setOriginal() throws SQLException {
      Iterator var1 = this.rvh.iterator();

      while(var1.hasNext()) {
         Row var2 = (Row)var1.next();
         this.makeRowOriginal(var2);
         if (var2.getDeleted()) {
            var1.remove();
            --this.numRows;
         }
      }

      this.numDeleted = 0;
      this.notifyRowSetChanged();
   }

   public String getTableName() throws SQLException {
      return this.tableName;
   }

   public void setTableName(String var1) throws SQLException {
      if (var1 == null) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.tablename").toString());
      } else {
         this.tableName = var1;
      }
   }

   public int[] getKeyColumns() throws SQLException {
      int[] var1 = this.keyCols;
      return var1 == null ? null : Arrays.copyOf(var1, var1.length);
   }

   public void setKeyColumns(int[] var1) throws SQLException {
      int var2 = 0;
      if (this.RowSetMD != null) {
         var2 = this.RowSetMD.getColumnCount();
         if (var1.length > var2) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.keycols").toString());
         }
      }

      this.keyCols = new int[var1.length];

      for(int var3 = 0; var3 < var1.length; ++var3) {
         if (this.RowSetMD != null && (var1[var3] <= 0 || var1[var3] > var2)) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcol").toString() + var1[var3]);
         }

         this.keyCols[var3] = var1[var3];
      }

   }

   public void updateRef(int var1, Ref var2) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      this.getCurrentRow().setColumnObject(var1, new SerialRef(var2));
   }

   public void updateRef(String var1, Ref var2) throws SQLException {
      this.updateRef(this.getColIdxByName(var1), var2);
   }

   public void updateClob(int var1, Clob var2) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      if (this.dbmslocatorsUpdateCopy) {
         this.getCurrentRow().setColumnObject(var1, new SerialClob(var2));
      } else {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotsupp").toString());
      }
   }

   public void updateClob(String var1, Clob var2) throws SQLException {
      this.updateClob(this.getColIdxByName(var1), var2);
   }

   public void updateBlob(int var1, Blob var2) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      if (this.dbmslocatorsUpdateCopy) {
         this.getCurrentRow().setColumnObject(var1, new SerialBlob(var2));
      } else {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotsupp").toString());
      }
   }

   public void updateBlob(String var1, Blob var2) throws SQLException {
      this.updateBlob(this.getColIdxByName(var1), var2);
   }

   public void updateArray(int var1, Array var2) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      this.getCurrentRow().setColumnObject(var1, new SerialArray(var2));
   }

   public void updateArray(String var1, Array var2) throws SQLException {
      this.updateArray(this.getColIdxByName(var1), var2);
   }

   public URL getURL(int var1) throws SQLException {
      this.checkIndex(var1);
      this.checkCursor();
      if (this.RowSetMD.getColumnType(var1) != 70) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
      } else {
         this.setLastValueNull(false);
         URL var2 = (URL)((URL)this.getCurrentRow().getColumnObject(var1));
         if (var2 == null) {
            this.setLastValueNull(true);
            return null;
         } else {
            return var2;
         }
      }
   }

   public URL getURL(String var1) throws SQLException {
      return this.getURL(this.getColIdxByName(var1));
   }

   public RowSetWarning getRowSetWarnings() {
      try {
         this.notifyCursorMoved();
      } catch (SQLException var2) {
      }

      return this.rowsetWarning;
   }

   private String buildTableName(String var1) throws SQLException {
      String var4 = "";
      var1 = var1.trim();
      if (var1.toLowerCase().startsWith("select")) {
         int var2 = var1.toLowerCase().indexOf("from");
         int var3 = var1.indexOf(",", var2);
         if (var3 == -1) {
            var4 = var1.substring(var2 + "from".length(), var1.length()).trim();
            String var5 = var4;
            int var6 = var4.toLowerCase().indexOf("where");
            if (var6 != -1) {
               var5 = var4.substring(0, var6).trim();
            }

            var4 = var5;
         }
      } else if (!var1.toLowerCase().startsWith("insert") && var1.toLowerCase().startsWith("update")) {
      }

      return var4;
   }

   public void commit() throws SQLException {
      this.conn.commit();
   }

   public void rollback() throws SQLException {
      this.conn.rollback();
   }

   public void rollback(Savepoint var1) throws SQLException {
      this.conn.rollback(var1);
   }

   public void unsetMatchColumn(int[] var1) throws SQLException {
      int var3;
      for(var3 = 0; var3 < var1.length; ++var3) {
         int var2 = Integer.parseInt(((Integer)this.iMatchColumns.get(var3)).toString());
         if (var1[var3] != var2) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.matchcols").toString());
         }
      }

      for(var3 = 0; var3 < var1.length; ++var3) {
         this.iMatchColumns.set(var3, -1);
      }

   }

   public void unsetMatchColumn(String[] var1) throws SQLException {
      int var2;
      for(var2 = 0; var2 < var1.length; ++var2) {
         if (!var1[var2].equals(this.strMatchColumns.get(var2))) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.matchcols").toString());
         }
      }

      for(var2 = 0; var2 < var1.length; ++var2) {
         this.strMatchColumns.set(var2, (Object)null);
      }

   }

   public String[] getMatchColumnNames() throws SQLException {
      String[] var1 = new String[this.strMatchColumns.size()];
      if (this.strMatchColumns.get(0) == null) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.setmatchcols").toString());
      } else {
         this.strMatchColumns.copyInto(var1);
         return var1;
      }
   }

   public int[] getMatchColumnIndexes() throws SQLException {
      Integer[] var1 = new Integer[this.iMatchColumns.size()];
      int[] var2 = new int[this.iMatchColumns.size()];
      int var3 = (Integer)this.iMatchColumns.get(0);
      if (var3 == -1) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.setmatchcols").toString());
      } else {
         this.iMatchColumns.copyInto(var1);

         for(int var4 = 0; var4 < var1.length; ++var4) {
            var2[var4] = var1[var4];
         }

         return var2;
      }
   }

   public void setMatchColumn(int[] var1) throws SQLException {
      int var2;
      for(var2 = 0; var2 < var1.length; ++var2) {
         if (var1[var2] < 0) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.matchcols1").toString());
         }
      }

      for(var2 = 0; var2 < var1.length; ++var2) {
         this.iMatchColumns.add(var2, var1[var2]);
      }

   }

   public void setMatchColumn(String[] var1) throws SQLException {
      int var2;
      for(var2 = 0; var2 < var1.length; ++var2) {
         if (var1[var2] == null || var1[var2].equals("")) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.matchcols2").toString());
         }
      }

      for(var2 = 0; var2 < var1.length; ++var2) {
         this.strMatchColumns.add(var2, var1[var2]);
      }

   }

   public void setMatchColumn(int var1) throws SQLException {
      if (var1 < 0) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.matchcols1").toString());
      } else {
         this.iMatchColumns.set(0, var1);
      }
   }

   public void setMatchColumn(String var1) throws SQLException {
      if (var1 != null && !(var1 = var1.trim()).equals("")) {
         this.strMatchColumns.set(0, var1);
      } else {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.matchcols2").toString());
      }
   }

   public void unsetMatchColumn(int var1) throws SQLException {
      if (!((Integer)this.iMatchColumns.get(0)).equals(var1)) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.unsetmatch").toString());
      } else if (this.strMatchColumns.get(0) != null) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.unsetmatch1").toString());
      } else {
         this.iMatchColumns.set(0, -1);
      }
   }

   public void unsetMatchColumn(String var1) throws SQLException {
      var1 = var1.trim();
      if (!((String)this.strMatchColumns.get(0)).equals(var1)) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.unsetmatch").toString());
      } else if ((Integer)this.iMatchColumns.get(0) > 0) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.unsetmatch2").toString());
      } else {
         this.strMatchColumns.set(0, (Object)null);
      }
   }

   public void rowSetPopulated(RowSetEvent var1, int var2) throws SQLException {
      if (var2 >= 0 && var2 >= this.getFetchSize()) {
         if (this.size() % var2 == 0) {
            RowSetEvent var3 = new RowSetEvent(this);
            this.notifyRowSetChanged();
         }

      } else {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.numrows").toString());
      }
   }

   public void populate(ResultSet var1, int var2) throws SQLException {
      Map var7 = this.getTypeMap();
      this.cursorPos = 0;
      if (this.populatecallcount == 0) {
         if (var2 < 0) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.startpos").toString());
         }

         if (this.getMaxRows() == 0) {
            var1.absolute(var2);

            while(var1.next()) {
               ++this.totalRows;
            }

            ++this.totalRows;
         }

         this.startPos = var2;
      }

      ++this.populatecallcount;
      this.resultSet = var1;
      if (this.endPos - this.startPos >= this.getMaxRows() && this.getMaxRows() > 0) {
         this.endPos = this.prevEndPos;
         this.pagenotend = false;
      } else {
         if ((this.maxRowsreached != this.getMaxRows() || this.maxRowsreached != this.totalRows) && this.pagenotend) {
            this.startPrev = var2 - this.getPageSize();
         }

         if (this.pageSize == 0) {
            this.prevEndPos = this.endPos;
            this.endPos = var2 + this.getMaxRows();
         } else {
            this.prevEndPos = this.endPos;
            this.endPos = var2 + this.getPageSize();
         }

         if (var2 == 1) {
            this.resultSet.beforeFirst();
         } else {
            this.resultSet.absolute(var2 - 1);
         }

         if (this.pageSize == 0) {
            this.rvh = new Vector(this.getMaxRows());
         } else {
            this.rvh = new Vector(this.getPageSize());
         }

         if (var1 == null) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.populate").toString());
         } else {
            this.RSMD = var1.getMetaData();
            this.RowSetMD = new RowSetMetaDataImpl();
            this.initMetaData(this.RowSetMD, this.RSMD);
            this.RSMD = null;
            int var5 = this.RowSetMD.getColumnCount();
            int var9 = this.getMaxRows();
            int var3 = 0;
            Row var4 = null;
            if (!var1.next() && var9 == 0) {
               this.endPos = this.prevEndPos;
               this.pagenotend = false;
            } else {
               var1.previous();

               while(var1.next()) {
                  var4 = new Row(var5);
                  if (this.pageSize == 0) {
                     if (var3 >= var9 && var9 > 0) {
                        this.rowsetWarning.setNextException(new SQLException("Populating rows setting has exceeded max row setting"));
                        break;
                     }
                  } else if (var3 >= this.pageSize || this.maxRowsreached >= var9 && var9 > 0) {
                     this.rowsetWarning.setNextException(new SQLException("Populating rows setting has exceeded max row setting"));
                     break;
                  }

                  for(int var6 = 1; var6 <= var5; ++var6) {
                     Object var8;
                     if (var7 == null) {
                        var8 = var1.getObject(var6);
                     } else {
                        var8 = var1.getObject(var6, var7);
                     }

                     if (var8 instanceof Struct) {
                        var8 = new SerialStruct((Struct)var8, var7);
                     } else if (var8 instanceof SQLData) {
                        var8 = new SerialStruct((SQLData)var8, var7);
                     } else if (var8 instanceof Blob) {
                        var8 = new SerialBlob((Blob)var8);
                     } else if (var8 instanceof Clob) {
                        var8 = new SerialClob((Clob)var8);
                     } else if (var8 instanceof Array) {
                        var8 = new SerialArray((Array)var8, var7);
                     }

                     var4.initColumnObject(var6, var8);
                  }

                  ++var3;
                  ++this.maxRowsreached;
                  this.rvh.add(var4);
               }

               this.numRows = var3;
               this.notifyRowSetChanged();
            }
         }
      }
   }

   public boolean nextPage() throws SQLException {
      if (this.populatecallcount == 0) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.nextpage").toString());
      } else {
         this.onFirstPage = false;
         if (this.callWithCon) {
            this.crsReader.setStartPosition(this.endPos);
            this.crsReader.readData(this);
            this.resultSet = null;
         } else {
            this.populate(this.resultSet, this.endPos);
         }

         return this.pagenotend;
      }
   }

   public void setPageSize(int var1) throws SQLException {
      if (var1 < 0) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.pagesize").toString());
      } else if (var1 > this.getMaxRows() && this.getMaxRows() != 0) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.pagesize1").toString());
      } else {
         this.pageSize = var1;
      }
   }

   public int getPageSize() {
      return this.pageSize;
   }

   public boolean previousPage() throws SQLException {
      int var1 = this.getPageSize();
      int var2 = this.maxRowsreached;
      if (this.populatecallcount == 0) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.nextpage").toString());
      } else if (!this.callWithCon && this.resultSet.getType() == 1003) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.fwdonly").toString());
      } else {
         this.pagenotend = true;
         if (this.startPrev < this.startPos) {
            this.onFirstPage = true;
            return false;
         } else if (this.onFirstPage) {
            return false;
         } else {
            int var3 = var2 % var1;
            if (var3 == 0) {
               this.maxRowsreached -= 2 * var1;
               if (this.callWithCon) {
                  this.crsReader.setStartPosition(this.startPrev);
                  this.crsReader.readData(this);
                  this.resultSet = null;
               } else {
                  this.populate(this.resultSet, this.startPrev);
               }

               return true;
            } else {
               this.maxRowsreached -= var1 + var3;
               if (this.callWithCon) {
                  this.crsReader.setStartPosition(this.startPrev);
                  this.crsReader.readData(this);
                  this.resultSet = null;
               } else {
                  this.populate(this.resultSet, this.startPrev);
               }

               return true;
            }
         }
      }
   }

   public void setRowInserted(boolean var1) throws SQLException {
      this.checkCursor();
      if (this.onInsertRow) {
         throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidop").toString());
      } else {
         if (var1) {
            ((Row)this.getCurrentRow()).setInserted();
         } else {
            ((Row)this.getCurrentRow()).clearInserted();
         }

      }
   }

   public SQLXML getSQLXML(int var1) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
   }

   public SQLXML getSQLXML(String var1) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
   }

   public RowId getRowId(int var1) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
   }

   public RowId getRowId(String var1) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
   }

   public void updateRowId(int var1, RowId var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
   }

   public void updateRowId(String var1, RowId var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
   }

   public int getHoldability() throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
   }

   public boolean isClosed() throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
   }

   public void updateNString(int var1, String var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
   }

   public void updateNString(String var1, String var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
   }

   public void updateNClob(int var1, NClob var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
   }

   public void updateNClob(String var1, NClob var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
   }

   public NClob getNClob(int var1) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
   }

   public NClob getNClob(String var1) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
   }

   public <T> T unwrap(Class<T> var1) throws SQLException {
      return null;
   }

   public boolean isWrapperFor(Class<?> var1) throws SQLException {
      return false;
   }

   public void setSQLXML(int var1, SQLXML var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
   }

   public void setSQLXML(String var1, SQLXML var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
   }

   public void setRowId(int var1, RowId var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
   }

   public void setRowId(String var1, RowId var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
   }

   public void setNCharacterStream(int var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setNClob(String var1, NClob var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
   }

   public Reader getNCharacterStream(int var1) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
   }

   public Reader getNCharacterStream(String var1) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
   }

   public void updateSQLXML(int var1, SQLXML var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
   }

   public void updateSQLXML(String var1, SQLXML var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
   }

   public String getNString(int var1) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
   }

   public String getNString(String var1) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
   }

   public void updateNCharacterStream(int var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
   }

   public void updateNCharacterStream(String var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
   }

   public void updateNCharacterStream(int var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void updateNCharacterStream(String var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void updateBlob(int var1, InputStream var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void updateBlob(String var1, InputStream var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void updateBlob(int var1, InputStream var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void updateBlob(String var1, InputStream var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void updateClob(int var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void updateClob(String var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void updateClob(int var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void updateClob(String var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void updateNClob(int var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void updateNClob(String var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void updateNClob(int var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void updateNClob(String var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void updateAsciiStream(int var1, InputStream var2, long var3) throws SQLException {
   }

   public void updateBinaryStream(int var1, InputStream var2, long var3) throws SQLException {
   }

   public void updateCharacterStream(int var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void updateCharacterStream(String var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void updateAsciiStream(String var1, InputStream var2, long var3) throws SQLException {
   }

   public void updateBinaryStream(String var1, InputStream var2, long var3) throws SQLException {
   }

   public void updateBinaryStream(int var1, InputStream var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void updateBinaryStream(String var1, InputStream var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void updateCharacterStream(int var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void updateCharacterStream(String var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void updateAsciiStream(int var1, InputStream var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void updateAsciiStream(String var1, InputStream var2) throws SQLException {
   }

   public void setURL(int var1, URL var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setNClob(int var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setNClob(String var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setNClob(String var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setNClob(int var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setNClob(int var1, NClob var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setNString(int var1, String var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setNString(String var1, String var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setNCharacterStream(int var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setNCharacterStream(String var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setNCharacterStream(String var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setTimestamp(String var1, Timestamp var2, Calendar var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setClob(String var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setClob(String var1, Clob var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setClob(String var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setDate(String var1, Date var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setDate(String var1, Date var2, Calendar var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setTime(String var1, Time var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setTime(String var1, Time var2, Calendar var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setClob(int var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setClob(int var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setBlob(int var1, InputStream var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setBlob(int var1, InputStream var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setBlob(String var1, InputStream var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setBlob(String var1, Blob var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setBlob(String var1, InputStream var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setObject(String var1, Object var2, int var3, int var4) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setObject(String var1, Object var2, int var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setObject(String var1, Object var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setAsciiStream(String var1, InputStream var2, int var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setBinaryStream(String var1, InputStream var2, int var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setCharacterStream(String var1, Reader var2, int var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setAsciiStream(String var1, InputStream var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setBinaryStream(String var1, InputStream var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setCharacterStream(String var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setBigDecimal(String var1, BigDecimal var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setString(String var1, String var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setBytes(String var1, byte[] var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setTimestamp(String var1, Timestamp var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setNull(String var1, int var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setNull(String var1, int var2, String var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setBoolean(String var1, boolean var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setByte(String var1, byte var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setShort(String var1, short var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setInt(String var1, int var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setLong(String var1, long var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setFloat(String var1, float var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   public void setDouble(String var1, double var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();

      try {
         this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   public <T> T getObject(int var1, Class<T> var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Not supported yet.");
   }

   public <T> T getObject(String var1, Class<T> var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Not supported yet.");
   }
}
