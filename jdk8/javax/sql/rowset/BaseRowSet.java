package javax.sql.rowset;

import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import javax.sql.RowSet;
import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;
import javax.sql.rowset.serial.SerialArray;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialRef;

public abstract class BaseRowSet implements Serializable, Cloneable {
   public static final int UNICODE_STREAM_PARAM = 0;
   public static final int BINARY_STREAM_PARAM = 1;
   public static final int ASCII_STREAM_PARAM = 2;
   protected InputStream binaryStream;
   protected InputStream unicodeStream;
   protected InputStream asciiStream;
   protected Reader charStream;
   private String command;
   private String URL;
   private String dataSource;
   private transient String username;
   private transient String password;
   private int rowSetType = 1004;
   private boolean showDeleted = false;
   private int queryTimeout = 0;
   private int maxRows = 0;
   private int maxFieldSize = 0;
   private int concurrency = 1008;
   private boolean readOnly;
   private boolean escapeProcessing = true;
   private int isolation;
   private int fetchDir = 1000;
   private int fetchSize = 0;
   private Map<String, Class<?>> map;
   private Vector<RowSetListener> listeners = new Vector();
   private Hashtable<Integer, Object> params;
   static final long serialVersionUID = 4886719666485113312L;

   protected void initParams() {
      this.params = new Hashtable();
   }

   public void addRowSetListener(RowSetListener var1) {
      this.listeners.add(var1);
   }

   public void removeRowSetListener(RowSetListener var1) {
      this.listeners.remove(var1);
   }

   private void checkforRowSetInterface() throws SQLException {
      if (!(this instanceof RowSet)) {
         throw new SQLException("The class extending abstract class BaseRowSet must implement javax.sql.RowSet or one of it's sub-interfaces.");
      }
   }

   protected void notifyCursorMoved() throws SQLException {
      this.checkforRowSetInterface();
      if (!this.listeners.isEmpty()) {
         RowSetEvent var1 = new RowSetEvent((RowSet)this);
         Iterator var2 = this.listeners.iterator();

         while(var2.hasNext()) {
            RowSetListener var3 = (RowSetListener)var2.next();
            var3.cursorMoved(var1);
         }
      }

   }

   protected void notifyRowChanged() throws SQLException {
      this.checkforRowSetInterface();
      if (!this.listeners.isEmpty()) {
         RowSetEvent var1 = new RowSetEvent((RowSet)this);
         Iterator var2 = this.listeners.iterator();

         while(var2.hasNext()) {
            RowSetListener var3 = (RowSetListener)var2.next();
            var3.rowChanged(var1);
         }
      }

   }

   protected void notifyRowSetChanged() throws SQLException {
      this.checkforRowSetInterface();
      if (!this.listeners.isEmpty()) {
         RowSetEvent var1 = new RowSetEvent((RowSet)this);
         Iterator var2 = this.listeners.iterator();

         while(var2.hasNext()) {
            RowSetListener var3 = (RowSetListener)var2.next();
            var3.rowSetChanged(var1);
         }
      }

   }

   public String getCommand() {
      return this.command;
   }

   public void setCommand(String var1) throws SQLException {
      if (var1 == null) {
         this.command = null;
      } else {
         if (var1.length() == 0) {
            throw new SQLException("Invalid command string detected. Cannot be of length less than 0");
         }

         if (this.params == null) {
            throw new SQLException("Set initParams() before setCommand");
         }

         this.params.clear();
         this.command = var1;
      }

   }

   public String getUrl() throws SQLException {
      return this.URL;
   }

   public void setUrl(String var1) throws SQLException {
      if (var1 == null) {
         var1 = null;
      } else {
         if (var1.length() < 1) {
            throw new SQLException("Invalid url string detected. Cannot be of length less than 1");
         }

         this.URL = var1;
      }

      this.dataSource = null;
   }

   public String getDataSourceName() {
      return this.dataSource;
   }

   public void setDataSourceName(String var1) throws SQLException {
      if (var1 == null) {
         this.dataSource = null;
      } else {
         if (var1.equals("")) {
            throw new SQLException("DataSource name cannot be empty string");
         }

         this.dataSource = var1;
      }

      this.URL = null;
   }

   public String getUsername() {
      return this.username;
   }

   public void setUsername(String var1) {
      if (var1 == null) {
         this.username = null;
      } else {
         this.username = var1;
      }

   }

   public String getPassword() {
      return this.password;
   }

   public void setPassword(String var1) {
      if (var1 == null) {
         this.password = null;
      } else {
         this.password = var1;
      }

   }

   public void setType(int var1) throws SQLException {
      if (var1 != 1003 && var1 != 1004 && var1 != 1005) {
         throw new SQLException("Invalid type of RowSet set. Must be either ResultSet.TYPE_FORWARD_ONLY or ResultSet.TYPE_SCROLL_INSENSITIVE or ResultSet.TYPE_SCROLL_SENSITIVE.");
      } else {
         this.rowSetType = var1;
      }
   }

   public int getType() throws SQLException {
      return this.rowSetType;
   }

   public void setConcurrency(int var1) throws SQLException {
      if (var1 != 1007 && var1 != 1008) {
         throw new SQLException("Invalid concurrency set. Must be either ResultSet.CONCUR_READ_ONLY or ResultSet.CONCUR_UPDATABLE.");
      } else {
         this.concurrency = var1;
      }
   }

   public boolean isReadOnly() {
      return this.readOnly;
   }

   public void setReadOnly(boolean var1) {
      this.readOnly = var1;
   }

   public int getTransactionIsolation() {
      return this.isolation;
   }

   public void setTransactionIsolation(int var1) throws SQLException {
      if (var1 != 0 && var1 != 2 && var1 != 1 && var1 != 4 && var1 != 8) {
         throw new SQLException("Invalid transaction isolation set. Must be either Connection.TRANSACTION_NONE or Connection.TRANSACTION_READ_UNCOMMITTED or Connection.TRANSACTION_READ_COMMITTED or Connection.RRANSACTION_REPEATABLE_READ or Connection.TRANSACTION_SERIALIZABLE");
      } else {
         this.isolation = var1;
      }
   }

   public Map<String, Class<?>> getTypeMap() {
      return this.map;
   }

   public void setTypeMap(Map<String, Class<?>> var1) {
      this.map = var1;
   }

   public int getMaxFieldSize() throws SQLException {
      return this.maxFieldSize;
   }

   public void setMaxFieldSize(int var1) throws SQLException {
      if (var1 < 0) {
         throw new SQLException("Invalid max field size set. Cannot be of value: " + var1);
      } else {
         this.maxFieldSize = var1;
      }
   }

   public int getMaxRows() throws SQLException {
      return this.maxRows;
   }

   public void setMaxRows(int var1) throws SQLException {
      if (var1 < 0) {
         throw new SQLException("Invalid max row size set. Cannot be of value: " + var1);
      } else if (var1 < this.getFetchSize()) {
         throw new SQLException("Invalid max row size set. Cannot be less than the fetchSize.");
      } else {
         this.maxRows = var1;
      }
   }

   public void setEscapeProcessing(boolean var1) throws SQLException {
      this.escapeProcessing = var1;
   }

   public int getQueryTimeout() throws SQLException {
      return this.queryTimeout;
   }

   public void setQueryTimeout(int var1) throws SQLException {
      if (var1 < 0) {
         throw new SQLException("Invalid query timeout value set. Cannot be of value: " + var1);
      } else {
         this.queryTimeout = var1;
      }
   }

   public boolean getShowDeleted() throws SQLException {
      return this.showDeleted;
   }

   public void setShowDeleted(boolean var1) throws SQLException {
      this.showDeleted = var1;
   }

   public boolean getEscapeProcessing() throws SQLException {
      return this.escapeProcessing;
   }

   public void setFetchDirection(int var1) throws SQLException {
      if ((this.getType() != 1003 || var1 == 1000) && (var1 == 1000 || var1 == 1001 || var1 == 1002)) {
         this.fetchDir = var1;
      } else {
         throw new SQLException("Invalid Fetch Direction");
      }
   }

   public int getFetchDirection() throws SQLException {
      return this.fetchDir;
   }

   public void setFetchSize(int var1) throws SQLException {
      if (this.getMaxRows() == 0 && var1 >= 0) {
         this.fetchSize = var1;
      } else if (var1 >= 0 && var1 <= this.getMaxRows()) {
         this.fetchSize = var1;
      } else {
         throw new SQLException("Invalid fetch size set. Cannot be of value: " + var1);
      }
   }

   public int getFetchSize() throws SQLException {
      return this.fetchSize;
   }

   public int getConcurrency() throws SQLException {
      return this.concurrency;
   }

   private void checkParamIndex(int var1) throws SQLException {
      if (var1 < 1) {
         throw new SQLException("Invalid Parameter Index");
      }
   }

   public void setNull(int var1, int var2) throws SQLException {
      this.checkParamIndex(var1);
      Object[] var3 = new Object[]{null, var2};
      if (this.params == null) {
         throw new SQLException("Set initParams() before setNull");
      } else {
         this.params.put(var1 - 1, var3);
      }
   }

   public void setNull(int var1, int var2, String var3) throws SQLException {
      this.checkParamIndex(var1);
      Object[] var4 = new Object[]{null, var2, var3};
      if (this.params == null) {
         throw new SQLException("Set initParams() before setNull");
      } else {
         this.params.put(var1 - 1, var4);
      }
   }

   public void setBoolean(int var1, boolean var2) throws SQLException {
      this.checkParamIndex(var1);
      if (this.params == null) {
         throw new SQLException("Set initParams() before setNull");
      } else {
         this.params.put(var1 - 1, var2);
      }
   }

   public void setByte(int var1, byte var2) throws SQLException {
      this.checkParamIndex(var1);
      if (this.params == null) {
         throw new SQLException("Set initParams() before setByte");
      } else {
         this.params.put(var1 - 1, var2);
      }
   }

   public void setShort(int var1, short var2) throws SQLException {
      this.checkParamIndex(var1);
      if (this.params == null) {
         throw new SQLException("Set initParams() before setShort");
      } else {
         this.params.put(var1 - 1, var2);
      }
   }

   public void setInt(int var1, int var2) throws SQLException {
      this.checkParamIndex(var1);
      if (this.params == null) {
         throw new SQLException("Set initParams() before setInt");
      } else {
         this.params.put(var1 - 1, var2);
      }
   }

   public void setLong(int var1, long var2) throws SQLException {
      this.checkParamIndex(var1);
      if (this.params == null) {
         throw new SQLException("Set initParams() before setLong");
      } else {
         this.params.put(var1 - 1, var2);
      }
   }

   public void setFloat(int var1, float var2) throws SQLException {
      this.checkParamIndex(var1);
      if (this.params == null) {
         throw new SQLException("Set initParams() before setFloat");
      } else {
         this.params.put(var1 - 1, var2);
      }
   }

   public void setDouble(int var1, double var2) throws SQLException {
      this.checkParamIndex(var1);
      if (this.params == null) {
         throw new SQLException("Set initParams() before setDouble");
      } else {
         this.params.put(var1 - 1, var2);
      }
   }

   public void setBigDecimal(int var1, BigDecimal var2) throws SQLException {
      this.checkParamIndex(var1);
      if (this.params == null) {
         throw new SQLException("Set initParams() before setBigDecimal");
      } else {
         this.params.put(var1 - 1, var2);
      }
   }

   public void setString(int var1, String var2) throws SQLException {
      this.checkParamIndex(var1);
      if (this.params == null) {
         throw new SQLException("Set initParams() before setString");
      } else {
         this.params.put(var1 - 1, var2);
      }
   }

   public void setBytes(int var1, byte[] var2) throws SQLException {
      this.checkParamIndex(var1);
      if (this.params == null) {
         throw new SQLException("Set initParams() before setBytes");
      } else {
         this.params.put(var1 - 1, var2);
      }
   }

   public void setDate(int var1, Date var2) throws SQLException {
      this.checkParamIndex(var1);
      if (this.params == null) {
         throw new SQLException("Set initParams() before setDate");
      } else {
         this.params.put(var1 - 1, var2);
      }
   }

   public void setTime(int var1, Time var2) throws SQLException {
      this.checkParamIndex(var1);
      if (this.params == null) {
         throw new SQLException("Set initParams() before setTime");
      } else {
         this.params.put(var1 - 1, var2);
      }
   }

   public void setTimestamp(int var1, Timestamp var2) throws SQLException {
      this.checkParamIndex(var1);
      if (this.params == null) {
         throw new SQLException("Set initParams() before setTimestamp");
      } else {
         this.params.put(var1 - 1, var2);
      }
   }

   public void setAsciiStream(int var1, InputStream var2, int var3) throws SQLException {
      this.checkParamIndex(var1);
      Object[] var4 = new Object[]{var2, var3, 2};
      if (this.params == null) {
         throw new SQLException("Set initParams() before setAsciiStream");
      } else {
         this.params.put(var1 - 1, var4);
      }
   }

   public void setAsciiStream(int var1, InputStream var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setBinaryStream(int var1, InputStream var2, int var3) throws SQLException {
      this.checkParamIndex(var1);
      Object[] var4 = new Object[]{var2, var3, 1};
      if (this.params == null) {
         throw new SQLException("Set initParams() before setBinaryStream");
      } else {
         this.params.put(var1 - 1, var4);
      }
   }

   public void setBinaryStream(int var1, InputStream var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   /** @deprecated */
   @Deprecated
   public void setUnicodeStream(int var1, InputStream var2, int var3) throws SQLException {
      this.checkParamIndex(var1);
      Object[] var4 = new Object[]{var2, var3, 0};
      if (this.params == null) {
         throw new SQLException("Set initParams() before setUnicodeStream");
      } else {
         this.params.put(var1 - 1, var4);
      }
   }

   public void setCharacterStream(int var1, Reader var2, int var3) throws SQLException {
      this.checkParamIndex(var1);
      Object[] var4 = new Object[]{var2, var3};
      if (this.params == null) {
         throw new SQLException("Set initParams() before setCharacterStream");
      } else {
         this.params.put(var1 - 1, var4);
      }
   }

   public void setCharacterStream(int var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setObject(int var1, Object var2, int var3, int var4) throws SQLException {
      this.checkParamIndex(var1);
      Object[] var5 = new Object[]{var2, var3, var4};
      if (this.params == null) {
         throw new SQLException("Set initParams() before setObject");
      } else {
         this.params.put(var1 - 1, var5);
      }
   }

   public void setObject(int var1, Object var2, int var3) throws SQLException {
      this.checkParamIndex(var1);
      Object[] var4 = new Object[]{var2, var3};
      if (this.params == null) {
         throw new SQLException("Set initParams() before setObject");
      } else {
         this.params.put(var1 - 1, var4);
      }
   }

   public void setObject(int var1, Object var2) throws SQLException {
      this.checkParamIndex(var1);
      if (this.params == null) {
         throw new SQLException("Set initParams() before setObject");
      } else {
         this.params.put(var1 - 1, var2);
      }
   }

   public void setRef(int var1, Ref var2) throws SQLException {
      this.checkParamIndex(var1);
      if (this.params == null) {
         throw new SQLException("Set initParams() before setRef");
      } else {
         this.params.put(var1 - 1, new SerialRef(var2));
      }
   }

   public void setBlob(int var1, Blob var2) throws SQLException {
      this.checkParamIndex(var1);
      if (this.params == null) {
         throw new SQLException("Set initParams() before setBlob");
      } else {
         this.params.put(var1 - 1, new SerialBlob(var2));
      }
   }

   public void setClob(int var1, Clob var2) throws SQLException {
      this.checkParamIndex(var1);
      if (this.params == null) {
         throw new SQLException("Set initParams() before setClob");
      } else {
         this.params.put(var1 - 1, new SerialClob(var2));
      }
   }

   public void setArray(int var1, Array var2) throws SQLException {
      this.checkParamIndex(var1);
      if (this.params == null) {
         throw new SQLException("Set initParams() before setArray");
      } else {
         this.params.put(var1 - 1, new SerialArray(var2));
      }
   }

   public void setDate(int var1, Date var2, Calendar var3) throws SQLException {
      this.checkParamIndex(var1);
      Object[] var4 = new Object[]{var2, var3};
      if (this.params == null) {
         throw new SQLException("Set initParams() before setDate");
      } else {
         this.params.put(var1 - 1, var4);
      }
   }

   public void setTime(int var1, Time var2, Calendar var3) throws SQLException {
      this.checkParamIndex(var1);
      Object[] var4 = new Object[]{var2, var3};
      if (this.params == null) {
         throw new SQLException("Set initParams() before setTime");
      } else {
         this.params.put(var1 - 1, var4);
      }
   }

   public void setTimestamp(int var1, Timestamp var2, Calendar var3) throws SQLException {
      this.checkParamIndex(var1);
      Object[] var4 = new Object[]{var2, var3};
      if (this.params == null) {
         throw new SQLException("Set initParams() before setTimestamp");
      } else {
         this.params.put(var1 - 1, var4);
      }
   }

   public void clearParameters() throws SQLException {
      this.params.clear();
   }

   public Object[] getParams() throws SQLException {
      Object[] var1;
      if (this.params == null) {
         this.initParams();
         var1 = new Object[this.params.size()];
         return var1;
      } else {
         var1 = new Object[this.params.size()];

         for(int var2 = 0; var2 < this.params.size(); ++var2) {
            var1[var2] = this.params.get(var2);
            if (var1[var2] == null) {
               throw new SQLException("missing parameter: " + (var2 + 1));
            }
         }

         return var1;
      }
   }

   public void setNull(String var1, int var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setNull(String var1, int var2, String var3) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setBoolean(String var1, boolean var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setByte(String var1, byte var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setShort(String var1, short var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setInt(String var1, int var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setLong(String var1, long var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setFloat(String var1, float var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setDouble(String var1, double var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setBigDecimal(String var1, BigDecimal var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setString(String var1, String var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setBytes(String var1, byte[] var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setTimestamp(String var1, Timestamp var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setAsciiStream(String var1, InputStream var2, int var3) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setBinaryStream(String var1, InputStream var2, int var3) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setCharacterStream(String var1, Reader var2, int var3) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setAsciiStream(String var1, InputStream var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setBinaryStream(String var1, InputStream var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setCharacterStream(String var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setNCharacterStream(int var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setObject(String var1, Object var2, int var3, int var4) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setObject(String var1, Object var2, int var3) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setObject(String var1, Object var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setBlob(int var1, InputStream var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setBlob(int var1, InputStream var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setBlob(String var1, InputStream var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setBlob(String var1, Blob var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setBlob(String var1, InputStream var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setClob(int var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setClob(int var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setClob(String var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setClob(String var1, Clob var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setClob(String var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setDate(String var1, Date var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setDate(String var1, Date var2, Calendar var3) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setTime(String var1, Time var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setTime(String var1, Time var2, Calendar var3) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setTimestamp(String var1, Timestamp var2, Calendar var3) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setSQLXML(int var1, SQLXML var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setSQLXML(String var1, SQLXML var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setRowId(int var1, RowId var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setRowId(String var1, RowId var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setNString(int var1, String var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setNString(String var1, String var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setNCharacterStream(int var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setNCharacterStream(String var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setNCharacterStream(String var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setNClob(String var1, NClob var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setNClob(String var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setNClob(String var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setNClob(int var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setNClob(int var1, NClob var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setNClob(int var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }

   public void setURL(int var1, URL var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Feature not supported");
   }
}
