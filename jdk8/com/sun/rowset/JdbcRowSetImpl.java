package com.sun.rowset;

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
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import java.util.Vector;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.sql.RowSetMetaData;
import javax.sql.rowset.BaseRowSet;
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.Joinable;
import javax.sql.rowset.RowSetMetaDataImpl;
import javax.sql.rowset.RowSetWarning;

public class JdbcRowSetImpl extends BaseRowSet implements JdbcRowSet, Joinable {
   private Connection conn;
   private PreparedStatement ps;
   private ResultSet rs;
   private RowSetMetaDataImpl rowsMD;
   private ResultSetMetaData resMD;
   private Vector<Integer> iMatchColumns;
   private Vector<String> strMatchColumns;
   protected transient JdbcRowSetResourceBundle resBundle;
   static final long serialVersionUID = -3591946023893483003L;

   public JdbcRowSetImpl() {
      this.conn = null;
      this.ps = null;
      this.rs = null;

      try {
         this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
      } catch (IOException var10) {
         throw new RuntimeException(var10);
      }

      this.initParams();

      try {
         this.setShowDeleted(false);
      } catch (SQLException var9) {
         System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.setshowdeleted").toString() + var9.getLocalizedMessage());
      }

      try {
         this.setQueryTimeout(0);
      } catch (SQLException var8) {
         System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.setquerytimeout").toString() + var8.getLocalizedMessage());
      }

      try {
         this.setMaxRows(0);
      } catch (SQLException var7) {
         System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.setmaxrows").toString() + var7.getLocalizedMessage());
      }

      try {
         this.setMaxFieldSize(0);
      } catch (SQLException var6) {
         System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.setmaxfieldsize").toString() + var6.getLocalizedMessage());
      }

      try {
         this.setEscapeProcessing(true);
      } catch (SQLException var5) {
         System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.setescapeprocessing").toString() + var5.getLocalizedMessage());
      }

      try {
         this.setConcurrency(1008);
      } catch (SQLException var4) {
         System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.setconcurrency").toString() + var4.getLocalizedMessage());
      }

      this.setTypeMap((Map)null);

      try {
         this.setType(1004);
      } catch (SQLException var3) {
         System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.settype").toString() + var3.getLocalizedMessage());
      }

      this.setReadOnly(true);

      try {
         this.setTransactionIsolation(2);
      } catch (SQLException var2) {
         System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.settransactionisolation").toString() + var2.getLocalizedMessage());
      }

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

   public JdbcRowSetImpl(Connection var1) throws SQLException {
      this.conn = var1;
      this.ps = null;
      this.rs = null;

      try {
         this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }

      this.initParams();
      this.setShowDeleted(false);
      this.setQueryTimeout(0);
      this.setMaxRows(0);
      this.setMaxFieldSize(0);
      this.setParams();
      this.setReadOnly(true);
      this.setTransactionIsolation(2);
      this.setEscapeProcessing(true);
      this.setTypeMap((Map)null);
      this.iMatchColumns = new Vector(10);

      int var2;
      for(var2 = 0; var2 < 10; ++var2) {
         this.iMatchColumns.add(var2, -1);
      }

      this.strMatchColumns = new Vector(10);

      for(var2 = 0; var2 < 10; ++var2) {
         this.strMatchColumns.add(var2, (Object)null);
      }

   }

   public JdbcRowSetImpl(String var1, String var2, String var3) throws SQLException {
      this.conn = null;
      this.ps = null;
      this.rs = null;

      try {
         this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
      } catch (IOException var5) {
         throw new RuntimeException(var5);
      }

      this.initParams();
      this.setUsername(var2);
      this.setPassword(var3);
      this.setUrl(var1);
      this.setShowDeleted(false);
      this.setQueryTimeout(0);
      this.setMaxRows(0);
      this.setMaxFieldSize(0);
      this.setParams();
      this.setReadOnly(true);
      this.setTransactionIsolation(2);
      this.setEscapeProcessing(true);
      this.setTypeMap((Map)null);
      this.iMatchColumns = new Vector(10);

      int var4;
      for(var4 = 0; var4 < 10; ++var4) {
         this.iMatchColumns.add(var4, -1);
      }

      this.strMatchColumns = new Vector(10);

      for(var4 = 0; var4 < 10; ++var4) {
         this.strMatchColumns.add(var4, (Object)null);
      }

   }

   public JdbcRowSetImpl(ResultSet var1) throws SQLException {
      this.conn = null;
      this.ps = null;
      this.rs = var1;

      try {
         this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }

      this.initParams();
      this.setShowDeleted(false);
      this.setQueryTimeout(0);
      this.setMaxRows(0);
      this.setMaxFieldSize(0);
      this.setParams();
      this.setReadOnly(true);
      this.setTransactionIsolation(2);
      this.setEscapeProcessing(true);
      this.setTypeMap((Map)null);
      this.resMD = this.rs.getMetaData();
      this.rowsMD = new RowSetMetaDataImpl();
      this.initMetaData(this.rowsMD, this.resMD);
      this.iMatchColumns = new Vector(10);

      int var2;
      for(var2 = 0; var2 < 10; ++var2) {
         this.iMatchColumns.add(var2, -1);
      }

      this.strMatchColumns = new Vector(10);

      for(var2 = 0; var2 < 10; ++var2) {
         this.strMatchColumns.add(var2, (Object)null);
      }

   }

   protected void initMetaData(RowSetMetaData var1, ResultSetMetaData var2) throws SQLException {
      int var3 = var2.getColumnCount();
      var1.setColumnCount(var3);

      for(int var4 = 1; var4 <= var3; ++var4) {
         var1.setAutoIncrement(var4, var2.isAutoIncrement(var4));
         var1.setCaseSensitive(var4, var2.isCaseSensitive(var4));
         var1.setCurrency(var4, var2.isCurrency(var4));
         var1.setNullable(var4, var2.isNullable(var4));
         var1.setSigned(var4, var2.isSigned(var4));
         var1.setSearchable(var4, var2.isSearchable(var4));
         var1.setColumnDisplaySize(var4, var2.getColumnDisplaySize(var4));
         var1.setColumnLabel(var4, var2.getColumnLabel(var4));
         var1.setColumnName(var4, var2.getColumnName(var4));
         var1.setSchemaName(var4, var2.getSchemaName(var4));
         var1.setPrecision(var4, var2.getPrecision(var4));
         var1.setScale(var4, var2.getScale(var4));
         var1.setTableName(var4, var2.getTableName(var4));
         var1.setCatalogName(var4, var2.getCatalogName(var4));
         var1.setColumnType(var4, var2.getColumnType(var4));
         var1.setColumnTypeName(var4, var2.getColumnTypeName(var4));
      }

   }

   protected void checkState() throws SQLException {
      if (this.conn == null && this.ps == null && this.rs == null) {
         throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.invalstate").toString());
      }
   }

   public void execute() throws SQLException {
      this.prepare();
      this.setProperties(this.ps);
      this.decodeParams(this.getParams(), this.ps);
      this.rs = this.ps.executeQuery();
      this.notifyRowSetChanged();
   }

   protected void setProperties(PreparedStatement var1) throws SQLException {
      try {
         var1.setEscapeProcessing(this.getEscapeProcessing());
      } catch (SQLException var6) {
         System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.setescapeprocessing").toString() + var6.getLocalizedMessage());
      }

      try {
         var1.setMaxFieldSize(this.getMaxFieldSize());
      } catch (SQLException var5) {
         System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.setmaxfieldsize").toString() + var5.getLocalizedMessage());
      }

      try {
         var1.setMaxRows(this.getMaxRows());
      } catch (SQLException var4) {
         System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.setmaxrows").toString() + var4.getLocalizedMessage());
      }

      try {
         var1.setQueryTimeout(this.getQueryTimeout());
      } catch (SQLException var3) {
         System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.setquerytimeout").toString() + var3.getLocalizedMessage());
      }

   }

   private Connection connect() throws SQLException {
      if (this.conn != null) {
         return this.conn;
      } else if (this.getDataSourceName() != null) {
         try {
            InitialContext var1 = new InitialContext();
            DataSource var2 = (DataSource)var1.lookup(this.getDataSourceName());
            return this.getUsername() != null && !this.getUsername().equals("") ? var2.getConnection(this.getUsername(), this.getPassword()) : var2.getConnection();
         } catch (NamingException var3) {
            throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.connect").toString());
         }
      } else {
         return this.getUrl() != null ? DriverManager.getConnection(this.getUrl(), this.getUsername(), this.getPassword()) : null;
      }
   }

   protected PreparedStatement prepare() throws SQLException {
      this.conn = this.connect();

      try {
         Map var1 = this.getTypeMap();
         if (var1 != null) {
            this.conn.setTypeMap(var1);
         }

         this.ps = this.conn.prepareStatement(this.getCommand(), 1004, 1008);
      } catch (SQLException var2) {
         System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.prepare").toString() + var2.getLocalizedMessage());
         if (this.ps != null) {
            this.ps.close();
         }

         if (this.conn != null) {
            this.conn.close();
         }

         throw new SQLException(var2.getMessage());
      }

      return this.ps;
   }

   private void decodeParams(Object[] var1, PreparedStatement var2) throws SQLException {
      Object[] var4 = null;

      for(int var5 = 0; var5 < var1.length; ++var5) {
         if (var1[var5] instanceof Object[]) {
            var4 = (Object[])((Object[])var1[var5]);
            if (var4.length == 2) {
               if (var4[0] == null) {
                  var2.setNull(var5 + 1, (Integer)var4[1]);
               } else if (!(var4[0] instanceof Date) && !(var4[0] instanceof Time) && !(var4[0] instanceof Timestamp)) {
                  if (var4[0] instanceof Reader) {
                     var2.setCharacterStream(var5 + 1, (Reader)var4[0], (Integer)var4[1]);
                  } else if (var4[1] instanceof Integer) {
                     var2.setObject(var5 + 1, var4[0], (Integer)var4[1]);
                  }
               } else {
                  System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.detecteddate"));
                  if (!(var4[1] instanceof Calendar)) {
                     throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.paramtype").toString());
                  }

                  System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.detectedcalendar"));
                  var2.setDate(var5 + 1, (Date)var4[0], (Calendar)var4[1]);
               }
            } else if (var4.length == 3) {
               if (var4[0] == null) {
                  var2.setNull(var5 + 1, (Integer)var4[1], (String)var4[2]);
               } else {
                  if (var4[0] instanceof InputStream) {
                     switch((Integer)var4[2]) {
                     case 0:
                        var2.setUnicodeStream(var5 + 1, (InputStream)var4[0], (Integer)var4[1]);
                        break;
                     case 1:
                        var2.setBinaryStream(var5 + 1, (InputStream)var4[0], (Integer)var4[1]);
                        break;
                     case 2:
                        var2.setAsciiStream(var5 + 1, (InputStream)var4[0], (Integer)var4[1]);
                        break;
                     default:
                        throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.paramtype").toString());
                     }
                  }

                  if (!(var4[1] instanceof Integer) || !(var4[2] instanceof Integer)) {
                     throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.paramtype").toString());
                  }

                  var2.setObject(var5 + 1, var4[0], (Integer)var4[1], (Integer)var4[2]);
               }
            } else {
               var2.setObject(var5 + 1, var1[var5]);
            }
         } else {
            var2.setObject(var5 + 1, var1[var5]);
         }
      }

   }

   public boolean next() throws SQLException {
      this.checkState();
      boolean var1 = this.rs.next();
      this.notifyCursorMoved();
      return var1;
   }

   public void close() throws SQLException {
      if (this.rs != null) {
         this.rs.close();
      }

      if (this.ps != null) {
         this.ps.close();
      }

      if (this.conn != null) {
         this.conn.close();
      }

   }

   public boolean wasNull() throws SQLException {
      this.checkState();
      return this.rs.wasNull();
   }

   public String getString(int var1) throws SQLException {
      this.checkState();
      return this.rs.getString(var1);
   }

   public boolean getBoolean(int var1) throws SQLException {
      this.checkState();
      return this.rs.getBoolean(var1);
   }

   public byte getByte(int var1) throws SQLException {
      this.checkState();
      return this.rs.getByte(var1);
   }

   public short getShort(int var1) throws SQLException {
      this.checkState();
      return this.rs.getShort(var1);
   }

   public int getInt(int var1) throws SQLException {
      this.checkState();
      return this.rs.getInt(var1);
   }

   public long getLong(int var1) throws SQLException {
      this.checkState();
      return this.rs.getLong(var1);
   }

   public float getFloat(int var1) throws SQLException {
      this.checkState();
      return this.rs.getFloat(var1);
   }

   public double getDouble(int var1) throws SQLException {
      this.checkState();
      return this.rs.getDouble(var1);
   }

   /** @deprecated */
   @Deprecated
   public BigDecimal getBigDecimal(int var1, int var2) throws SQLException {
      this.checkState();
      return this.rs.getBigDecimal(var1, var2);
   }

   public byte[] getBytes(int var1) throws SQLException {
      this.checkState();
      return this.rs.getBytes(var1);
   }

   public Date getDate(int var1) throws SQLException {
      this.checkState();
      return this.rs.getDate(var1);
   }

   public Time getTime(int var1) throws SQLException {
      this.checkState();
      return this.rs.getTime(var1);
   }

   public Timestamp getTimestamp(int var1) throws SQLException {
      this.checkState();
      return this.rs.getTimestamp(var1);
   }

   public InputStream getAsciiStream(int var1) throws SQLException {
      this.checkState();
      return this.rs.getAsciiStream(var1);
   }

   /** @deprecated */
   @Deprecated
   public InputStream getUnicodeStream(int var1) throws SQLException {
      this.checkState();
      return this.rs.getUnicodeStream(var1);
   }

   public InputStream getBinaryStream(int var1) throws SQLException {
      this.checkState();
      return this.rs.getBinaryStream(var1);
   }

   public String getString(String var1) throws SQLException {
      return this.getString(this.findColumn(var1));
   }

   public boolean getBoolean(String var1) throws SQLException {
      return this.getBoolean(this.findColumn(var1));
   }

   public byte getByte(String var1) throws SQLException {
      return this.getByte(this.findColumn(var1));
   }

   public short getShort(String var1) throws SQLException {
      return this.getShort(this.findColumn(var1));
   }

   public int getInt(String var1) throws SQLException {
      return this.getInt(this.findColumn(var1));
   }

   public long getLong(String var1) throws SQLException {
      return this.getLong(this.findColumn(var1));
   }

   public float getFloat(String var1) throws SQLException {
      return this.getFloat(this.findColumn(var1));
   }

   public double getDouble(String var1) throws SQLException {
      return this.getDouble(this.findColumn(var1));
   }

   /** @deprecated */
   @Deprecated
   public BigDecimal getBigDecimal(String var1, int var2) throws SQLException {
      return this.getBigDecimal(this.findColumn(var1), var2);
   }

   public byte[] getBytes(String var1) throws SQLException {
      return this.getBytes(this.findColumn(var1));
   }

   public Date getDate(String var1) throws SQLException {
      return this.getDate(this.findColumn(var1));
   }

   public Time getTime(String var1) throws SQLException {
      return this.getTime(this.findColumn(var1));
   }

   public Timestamp getTimestamp(String var1) throws SQLException {
      return this.getTimestamp(this.findColumn(var1));
   }

   public InputStream getAsciiStream(String var1) throws SQLException {
      return this.getAsciiStream(this.findColumn(var1));
   }

   /** @deprecated */
   @Deprecated
   public InputStream getUnicodeStream(String var1) throws SQLException {
      return this.getUnicodeStream(this.findColumn(var1));
   }

   public InputStream getBinaryStream(String var1) throws SQLException {
      return this.getBinaryStream(this.findColumn(var1));
   }

   public SQLWarning getWarnings() throws SQLException {
      this.checkState();
      return this.rs.getWarnings();
   }

   public void clearWarnings() throws SQLException {
      this.checkState();
      this.rs.clearWarnings();
   }

   public String getCursorName() throws SQLException {
      this.checkState();
      return this.rs.getCursorName();
   }

   public ResultSetMetaData getMetaData() throws SQLException {
      this.checkState();

      try {
         this.checkState();
      } catch (SQLException var2) {
         this.prepare();
         return this.ps.getMetaData();
      }

      return this.rs.getMetaData();
   }

   public Object getObject(int var1) throws SQLException {
      this.checkState();
      return this.rs.getObject(var1);
   }

   public Object getObject(String var1) throws SQLException {
      return this.getObject(this.findColumn(var1));
   }

   public int findColumn(String var1) throws SQLException {
      this.checkState();
      return this.rs.findColumn(var1);
   }

   public Reader getCharacterStream(int var1) throws SQLException {
      this.checkState();
      return this.rs.getCharacterStream(var1);
   }

   public Reader getCharacterStream(String var1) throws SQLException {
      return this.getCharacterStream(this.findColumn(var1));
   }

   public BigDecimal getBigDecimal(int var1) throws SQLException {
      this.checkState();
      return this.rs.getBigDecimal(var1);
   }

   public BigDecimal getBigDecimal(String var1) throws SQLException {
      return this.getBigDecimal(this.findColumn(var1));
   }

   public boolean isBeforeFirst() throws SQLException {
      this.checkState();
      return this.rs.isBeforeFirst();
   }

   public boolean isAfterLast() throws SQLException {
      this.checkState();
      return this.rs.isAfterLast();
   }

   public boolean isFirst() throws SQLException {
      this.checkState();
      return this.rs.isFirst();
   }

   public boolean isLast() throws SQLException {
      this.checkState();
      return this.rs.isLast();
   }

   public void beforeFirst() throws SQLException {
      this.checkState();
      this.rs.beforeFirst();
      this.notifyCursorMoved();
   }

   public void afterLast() throws SQLException {
      this.checkState();
      this.rs.afterLast();
      this.notifyCursorMoved();
   }

   public boolean first() throws SQLException {
      this.checkState();
      boolean var1 = this.rs.first();
      this.notifyCursorMoved();
      return var1;
   }

   public boolean last() throws SQLException {
      this.checkState();
      boolean var1 = this.rs.last();
      this.notifyCursorMoved();
      return var1;
   }

   public int getRow() throws SQLException {
      this.checkState();
      return this.rs.getRow();
   }

   public boolean absolute(int var1) throws SQLException {
      this.checkState();
      boolean var2 = this.rs.absolute(var1);
      this.notifyCursorMoved();
      return var2;
   }

   public boolean relative(int var1) throws SQLException {
      this.checkState();
      boolean var2 = this.rs.relative(var1);
      this.notifyCursorMoved();
      return var2;
   }

   public boolean previous() throws SQLException {
      this.checkState();
      boolean var1 = this.rs.previous();
      this.notifyCursorMoved();
      return var1;
   }

   public void setFetchDirection(int var1) throws SQLException {
      this.checkState();
      this.rs.setFetchDirection(var1);
   }

   public int getFetchDirection() throws SQLException {
      try {
         this.checkState();
      } catch (SQLException var2) {
         super.getFetchDirection();
      }

      return this.rs.getFetchDirection();
   }

   public void setFetchSize(int var1) throws SQLException {
      this.checkState();
      this.rs.setFetchSize(var1);
   }

   public int getType() throws SQLException {
      try {
         this.checkState();
      } catch (SQLException var2) {
         return super.getType();
      }

      if (this.rs == null) {
         return super.getType();
      } else {
         int var1 = this.rs.getType();
         return var1;
      }
   }

   public int getConcurrency() throws SQLException {
      try {
         this.checkState();
      } catch (SQLException var2) {
         super.getConcurrency();
      }

      return this.rs.getConcurrency();
   }

   public boolean rowUpdated() throws SQLException {
      this.checkState();
      return this.rs.rowUpdated();
   }

   public boolean rowInserted() throws SQLException {
      this.checkState();
      return this.rs.rowInserted();
   }

   public boolean rowDeleted() throws SQLException {
      this.checkState();
      return this.rs.rowDeleted();
   }

   public void updateNull(int var1) throws SQLException {
      this.checkState();
      this.checkTypeConcurrency();
      this.rs.updateNull(var1);
   }

   public void updateBoolean(int var1, boolean var2) throws SQLException {
      this.checkState();
      this.checkTypeConcurrency();
      this.rs.updateBoolean(var1, var2);
   }

   public void updateByte(int var1, byte var2) throws SQLException {
      this.checkState();
      this.checkTypeConcurrency();
      this.rs.updateByte(var1, var2);
   }

   public void updateShort(int var1, short var2) throws SQLException {
      this.checkState();
      this.checkTypeConcurrency();
      this.rs.updateShort(var1, var2);
   }

   public void updateInt(int var1, int var2) throws SQLException {
      this.checkState();
      this.checkTypeConcurrency();
      this.rs.updateInt(var1, var2);
   }

   public void updateLong(int var1, long var2) throws SQLException {
      this.checkState();
      this.checkTypeConcurrency();
      this.rs.updateLong(var1, var2);
   }

   public void updateFloat(int var1, float var2) throws SQLException {
      this.checkState();
      this.checkTypeConcurrency();
      this.rs.updateFloat(var1, var2);
   }

   public void updateDouble(int var1, double var2) throws SQLException {
      this.checkState();
      this.checkTypeConcurrency();
      this.rs.updateDouble(var1, var2);
   }

   public void updateBigDecimal(int var1, BigDecimal var2) throws SQLException {
      this.checkState();
      this.checkTypeConcurrency();
      this.rs.updateBigDecimal(var1, var2);
   }

   public void updateString(int var1, String var2) throws SQLException {
      this.checkState();
      this.checkTypeConcurrency();
      this.rs.updateString(var1, var2);
   }

   public void updateBytes(int var1, byte[] var2) throws SQLException {
      this.checkState();
      this.checkTypeConcurrency();
      this.rs.updateBytes(var1, var2);
   }

   public void updateDate(int var1, Date var2) throws SQLException {
      this.checkState();
      this.checkTypeConcurrency();
      this.rs.updateDate(var1, var2);
   }

   public void updateTime(int var1, Time var2) throws SQLException {
      this.checkState();
      this.checkTypeConcurrency();
      this.rs.updateTime(var1, var2);
   }

   public void updateTimestamp(int var1, Timestamp var2) throws SQLException {
      this.checkState();
      this.checkTypeConcurrency();
      this.rs.updateTimestamp(var1, var2);
   }

   public void updateAsciiStream(int var1, InputStream var2, int var3) throws SQLException {
      this.checkState();
      this.checkTypeConcurrency();
      this.rs.updateAsciiStream(var1, var2, var3);
   }

   public void updateBinaryStream(int var1, InputStream var2, int var3) throws SQLException {
      this.checkState();
      this.checkTypeConcurrency();
      this.rs.updateBinaryStream(var1, var2, var3);
   }

   public void updateCharacterStream(int var1, Reader var2, int var3) throws SQLException {
      this.checkState();
      this.checkTypeConcurrency();
      this.rs.updateCharacterStream(var1, var2, var3);
   }

   public void updateObject(int var1, Object var2, int var3) throws SQLException {
      this.checkState();
      this.checkTypeConcurrency();
      this.rs.updateObject(var1, var2, var3);
   }

   public void updateObject(int var1, Object var2) throws SQLException {
      this.checkState();
      this.checkTypeConcurrency();
      this.rs.updateObject(var1, var2);
   }

   public void updateNull(String var1) throws SQLException {
      this.updateNull(this.findColumn(var1));
   }

   public void updateBoolean(String var1, boolean var2) throws SQLException {
      this.updateBoolean(this.findColumn(var1), var2);
   }

   public void updateByte(String var1, byte var2) throws SQLException {
      this.updateByte(this.findColumn(var1), var2);
   }

   public void updateShort(String var1, short var2) throws SQLException {
      this.updateShort(this.findColumn(var1), var2);
   }

   public void updateInt(String var1, int var2) throws SQLException {
      this.updateInt(this.findColumn(var1), var2);
   }

   public void updateLong(String var1, long var2) throws SQLException {
      this.updateLong(this.findColumn(var1), var2);
   }

   public void updateFloat(String var1, float var2) throws SQLException {
      this.updateFloat(this.findColumn(var1), var2);
   }

   public void updateDouble(String var1, double var2) throws SQLException {
      this.updateDouble(this.findColumn(var1), var2);
   }

   public void updateBigDecimal(String var1, BigDecimal var2) throws SQLException {
      this.updateBigDecimal(this.findColumn(var1), var2);
   }

   public void updateString(String var1, String var2) throws SQLException {
      this.updateString(this.findColumn(var1), var2);
   }

   public void updateBytes(String var1, byte[] var2) throws SQLException {
      this.updateBytes(this.findColumn(var1), var2);
   }

   public void updateDate(String var1, Date var2) throws SQLException {
      this.updateDate(this.findColumn(var1), var2);
   }

   public void updateTime(String var1, Time var2) throws SQLException {
      this.updateTime(this.findColumn(var1), var2);
   }

   public void updateTimestamp(String var1, Timestamp var2) throws SQLException {
      this.updateTimestamp(this.findColumn(var1), var2);
   }

   public void updateAsciiStream(String var1, InputStream var2, int var3) throws SQLException {
      this.updateAsciiStream(this.findColumn(var1), var2, var3);
   }

   public void updateBinaryStream(String var1, InputStream var2, int var3) throws SQLException {
      this.updateBinaryStream(this.findColumn(var1), var2, var3);
   }

   public void updateCharacterStream(String var1, Reader var2, int var3) throws SQLException {
      this.updateCharacterStream(this.findColumn(var1), var2, var3);
   }

   public void updateObject(String var1, Object var2, int var3) throws SQLException {
      this.updateObject(this.findColumn(var1), var2, var3);
   }

   public void updateObject(String var1, Object var2) throws SQLException {
      this.updateObject(this.findColumn(var1), var2);
   }

   public void insertRow() throws SQLException {
      this.checkState();
      this.rs.insertRow();
      this.notifyRowChanged();
   }

   public void updateRow() throws SQLException {
      this.checkState();
      this.rs.updateRow();
      this.notifyRowChanged();
   }

   public void deleteRow() throws SQLException {
      this.checkState();
      this.rs.deleteRow();
      this.notifyRowChanged();
   }

   public void refreshRow() throws SQLException {
      this.checkState();
      this.rs.refreshRow();
   }

   public void cancelRowUpdates() throws SQLException {
      this.checkState();
      this.rs.cancelRowUpdates();
      this.notifyRowChanged();
   }

   public void moveToInsertRow() throws SQLException {
      this.checkState();
      this.rs.moveToInsertRow();
   }

   public void moveToCurrentRow() throws SQLException {
      this.checkState();
      this.rs.moveToCurrentRow();
   }

   public Statement getStatement() throws SQLException {
      return this.rs != null ? this.rs.getStatement() : null;
   }

   public Object getObject(int var1, Map<String, Class<?>> var2) throws SQLException {
      this.checkState();
      return this.rs.getObject(var1, var2);
   }

   public Ref getRef(int var1) throws SQLException {
      this.checkState();
      return this.rs.getRef(var1);
   }

   public Blob getBlob(int var1) throws SQLException {
      this.checkState();
      return this.rs.getBlob(var1);
   }

   public Clob getClob(int var1) throws SQLException {
      this.checkState();
      return this.rs.getClob(var1);
   }

   public Array getArray(int var1) throws SQLException {
      this.checkState();
      return this.rs.getArray(var1);
   }

   public Object getObject(String var1, Map<String, Class<?>> var2) throws SQLException {
      return this.getObject(this.findColumn(var1), var2);
   }

   public Ref getRef(String var1) throws SQLException {
      return this.getRef(this.findColumn(var1));
   }

   public Blob getBlob(String var1) throws SQLException {
      return this.getBlob(this.findColumn(var1));
   }

   public Clob getClob(String var1) throws SQLException {
      return this.getClob(this.findColumn(var1));
   }

   public Array getArray(String var1) throws SQLException {
      return this.getArray(this.findColumn(var1));
   }

   public Date getDate(int var1, Calendar var2) throws SQLException {
      this.checkState();
      return this.rs.getDate(var1, var2);
   }

   public Date getDate(String var1, Calendar var2) throws SQLException {
      return this.getDate(this.findColumn(var1), var2);
   }

   public Time getTime(int var1, Calendar var2) throws SQLException {
      this.checkState();
      return this.rs.getTime(var1, var2);
   }

   public Time getTime(String var1, Calendar var2) throws SQLException {
      return this.getTime(this.findColumn(var1), var2);
   }

   public Timestamp getTimestamp(int var1, Calendar var2) throws SQLException {
      this.checkState();
      return this.rs.getTimestamp(var1, var2);
   }

   public Timestamp getTimestamp(String var1, Calendar var2) throws SQLException {
      return this.getTimestamp(this.findColumn(var1), var2);
   }

   public void updateRef(int var1, Ref var2) throws SQLException {
      this.checkState();
      this.rs.updateRef(var1, var2);
   }

   public void updateRef(String var1, Ref var2) throws SQLException {
      this.updateRef(this.findColumn(var1), var2);
   }

   public void updateClob(int var1, Clob var2) throws SQLException {
      this.checkState();
      this.rs.updateClob(var1, var2);
   }

   public void updateClob(String var1, Clob var2) throws SQLException {
      this.updateClob(this.findColumn(var1), var2);
   }

   public void updateBlob(int var1, Blob var2) throws SQLException {
      this.checkState();
      this.rs.updateBlob(var1, var2);
   }

   public void updateBlob(String var1, Blob var2) throws SQLException {
      this.updateBlob(this.findColumn(var1), var2);
   }

   public void updateArray(int var1, Array var2) throws SQLException {
      this.checkState();
      this.rs.updateArray(var1, var2);
   }

   public void updateArray(String var1, Array var2) throws SQLException {
      this.updateArray(this.findColumn(var1), var2);
   }

   public URL getURL(int var1) throws SQLException {
      this.checkState();
      return this.rs.getURL(var1);
   }

   public URL getURL(String var1) throws SQLException {
      return this.getURL(this.findColumn(var1));
   }

   public RowSetWarning getRowSetWarnings() throws SQLException {
      return null;
   }

   public void unsetMatchColumn(int[] var1) throws SQLException {
      int var3;
      for(var3 = 0; var3 < var1.length; ++var3) {
         int var2 = Integer.parseInt(((Integer)this.iMatchColumns.get(var3)).toString());
         if (var1[var3] != var2) {
            throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.matchcols").toString());
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
            throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.matchcols").toString());
         }
      }

      for(var2 = 0; var2 < var1.length; ++var2) {
         this.strMatchColumns.set(var2, (Object)null);
      }

   }

   public String[] getMatchColumnNames() throws SQLException {
      String[] var1 = new String[this.strMatchColumns.size()];
      if (this.strMatchColumns.get(0) == null) {
         throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.setmatchcols").toString());
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
         throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.setmatchcols").toString());
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
            throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.matchcols1").toString());
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
            throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.matchcols2").toString());
         }
      }

      for(var2 = 0; var2 < var1.length; ++var2) {
         this.strMatchColumns.add(var2, var1[var2]);
      }

   }

   public void setMatchColumn(int var1) throws SQLException {
      if (var1 < 0) {
         throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.matchcols1").toString());
      } else {
         this.iMatchColumns.set(0, var1);
      }
   }

   public void setMatchColumn(String var1) throws SQLException {
      if (var1 != null && !(var1 = var1.trim()).equals("")) {
         this.strMatchColumns.set(0, var1);
      } else {
         throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.matchcols2").toString());
      }
   }

   public void unsetMatchColumn(int var1) throws SQLException {
      if (!((Integer)this.iMatchColumns.get(0)).equals(var1)) {
         throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.unsetmatch").toString());
      } else if (this.strMatchColumns.get(0) != null) {
         throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.usecolname").toString());
      } else {
         this.iMatchColumns.set(0, -1);
      }
   }

   public void unsetMatchColumn(String var1) throws SQLException {
      var1 = var1.trim();
      if (!((String)this.strMatchColumns.get(0)).equals(var1)) {
         throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.unsetmatch").toString());
      } else if ((Integer)this.iMatchColumns.get(0) > 0) {
         throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.usecolid").toString());
      } else {
         this.strMatchColumns.set(0, (Object)null);
      }
   }

   public DatabaseMetaData getDatabaseMetaData() throws SQLException {
      Connection var1 = this.connect();
      return var1.getMetaData();
   }

   public ParameterMetaData getParameterMetaData() throws SQLException {
      this.prepare();
      return this.ps.getParameterMetaData();
   }

   public void commit() throws SQLException {
      this.conn.commit();
      if (this.conn.getHoldability() != 1) {
         this.rs = null;
      }

   }

   public void setAutoCommit(boolean var1) throws SQLException {
      if (this.conn != null) {
         this.conn.setAutoCommit(var1);
      } else {
         this.conn = this.connect();
         this.conn.setAutoCommit(var1);
      }

   }

   public boolean getAutoCommit() throws SQLException {
      return this.conn.getAutoCommit();
   }

   public void rollback() throws SQLException {
      this.conn.rollback();
      this.rs = null;
   }

   public void rollback(Savepoint var1) throws SQLException {
      this.conn.rollback(var1);
   }

   protected void setParams() throws SQLException {
      if (this.rs == null) {
         this.setType(1004);
         this.setConcurrency(1008);
      } else {
         this.setType(this.rs.getType());
         this.setConcurrency(this.rs.getConcurrency());
      }

   }

   private void checkTypeConcurrency() throws SQLException {
      if (this.rs.getType() == 1003 || this.rs.getConcurrency() == 1007) {
         throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.resnotupd").toString());
      }
   }

   protected Connection getConnection() {
      return this.conn;
   }

   protected void setConnection(Connection var1) {
      this.conn = var1;
   }

   protected PreparedStatement getPreparedStatement() {
      return this.ps;
   }

   protected void setPreparedStatement(PreparedStatement var1) {
      this.ps = var1;
   }

   protected ResultSet getResultSet() throws SQLException {
      this.checkState();
      return this.rs;
   }

   protected void setResultSet(ResultSet var1) {
      this.rs = var1;
   }

   public void setCommand(String var1) throws SQLException {
      if (this.getCommand() != null) {
         if (!this.getCommand().equals(var1)) {
            super.setCommand(var1);
            this.ps = null;
            this.rs = null;
         }
      } else {
         super.setCommand(var1);
      }

   }

   public void setDataSourceName(String var1) throws SQLException {
      if (this.getDataSourceName() != null) {
         if (!this.getDataSourceName().equals(var1)) {
            super.setDataSourceName(var1);
            this.conn = null;
            this.ps = null;
            this.rs = null;
         }
      } else {
         super.setDataSourceName(var1);
      }

   }

   public void setUrl(String var1) throws SQLException {
      if (this.getUrl() != null) {
         if (!this.getUrl().equals(var1)) {
            super.setUrl(var1);
            this.conn = null;
            this.ps = null;
            this.rs = null;
         }
      } else {
         super.setUrl(var1);
      }

   }

   public void setUsername(String var1) {
      if (this.getUsername() != null) {
         if (!this.getUsername().equals(var1)) {
            super.setUsername(var1);
            this.conn = null;
            this.ps = null;
            this.rs = null;
         }
      } else {
         super.setUsername(var1);
      }

   }

   public void setPassword(String var1) {
      if (this.getPassword() != null) {
         if (!this.getPassword().equals(var1)) {
            super.setPassword(var1);
            this.conn = null;
            this.ps = null;
            this.rs = null;
         }
      } else {
         super.setPassword(var1);
      }

   }

   public void setType(int var1) throws SQLException {
      int var2;
      try {
         var2 = this.getType();
      } catch (SQLException var4) {
         var2 = 0;
      }

      if (var2 != var1) {
         super.setType(var1);
      }

   }

   public void setConcurrency(int var1) throws SQLException {
      int var2;
      try {
         var2 = this.getConcurrency();
      } catch (NullPointerException var4) {
         var2 = 0;
      }

      if (var2 != var1) {
         super.setConcurrency(var1);
      }

   }

   public SQLXML getSQLXML(int var1) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public SQLXML getSQLXML(String var1) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public RowId getRowId(int var1) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public RowId getRowId(String var1) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateRowId(int var1, RowId var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateRowId(String var1, RowId var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public int getHoldability() throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public boolean isClosed() throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateNString(int var1, String var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateNString(String var1, String var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateNClob(int var1, NClob var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateNClob(String var1, NClob var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public NClob getNClob(int var1) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public NClob getNClob(String var1) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public <T> T unwrap(Class<T> var1) throws SQLException {
      return null;
   }

   public boolean isWrapperFor(Class<?> var1) throws SQLException {
      return false;
   }

   public void setSQLXML(int var1, SQLXML var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setSQLXML(String var1, SQLXML var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setRowId(int var1, RowId var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setRowId(String var1, RowId var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setNString(int var1, String var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setNCharacterStream(int var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setNClob(String var1, NClob var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public Reader getNCharacterStream(int var1) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public Reader getNCharacterStream(String var1) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateSQLXML(int var1, SQLXML var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateSQLXML(String var1, SQLXML var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public String getNString(int var1) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public String getNString(String var1) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateNCharacterStream(int var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateNCharacterStream(String var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateNCharacterStream(int var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateNCharacterStream(String var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateBlob(int var1, InputStream var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateBlob(String var1, InputStream var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateBlob(int var1, InputStream var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateBlob(String var1, InputStream var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateClob(int var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateClob(String var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateClob(int var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateClob(String var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateNClob(int var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateNClob(String var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateNClob(int var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateNClob(String var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateAsciiStream(int var1, InputStream var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateBinaryStream(int var1, InputStream var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateCharacterStream(int var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateAsciiStream(String var1, InputStream var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateAsciiStream(int var1, InputStream var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateAsciiStream(String var1, InputStream var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateBinaryStream(String var1, InputStream var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateBinaryStream(int var1, InputStream var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateBinaryStream(String var1, InputStream var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateCharacterStream(String var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateCharacterStream(int var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void updateCharacterStream(String var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setURL(int var1, URL var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setNClob(int var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setNClob(String var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setNClob(String var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setNClob(int var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setNClob(int var1, NClob var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setNString(String var1, String var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setNCharacterStream(int var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setNCharacterStream(String var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setNCharacterStream(String var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setTimestamp(String var1, Timestamp var2, Calendar var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setClob(String var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setClob(String var1, Clob var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setClob(String var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setDate(String var1, Date var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setDate(String var1, Date var2, Calendar var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setTime(String var1, Time var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setTime(String var1, Time var2, Calendar var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setClob(int var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setClob(int var1, Reader var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setBlob(int var1, InputStream var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setBlob(int var1, InputStream var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setBlob(String var1, InputStream var2, long var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setBlob(String var1, Blob var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setBlob(String var1, InputStream var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setObject(String var1, Object var2, int var3, int var4) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setObject(String var1, Object var2, int var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setObject(String var1, Object var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setAsciiStream(String var1, InputStream var2, int var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setBinaryStream(String var1, InputStream var2, int var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setCharacterStream(String var1, Reader var2, int var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setAsciiStream(String var1, InputStream var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setBinaryStream(String var1, InputStream var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setCharacterStream(String var1, Reader var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setBigDecimal(String var1, BigDecimal var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setString(String var1, String var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setBytes(String var1, byte[] var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setTimestamp(String var1, Timestamp var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setNull(String var1, int var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setNull(String var1, int var2, String var3) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setBoolean(String var1, boolean var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setByte(String var1, byte var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setShort(String var1, short var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setInt(String var1, int var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setLong(String var1, long var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setFloat(String var1, float var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   public void setDouble(String var1, double var2) throws SQLException {
      throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();

      try {
         this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
      } catch (IOException var3) {
      }

   }

   public <T> T getObject(int var1, Class<T> var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Not supported yet.");
   }

   public <T> T getObject(String var1, Class<T> var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("Not supported yet.");
   }
}
