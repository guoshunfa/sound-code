package java.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Calendar;
import java.util.Map;

public interface ResultSet extends Wrapper, AutoCloseable {
   int FETCH_FORWARD = 1000;
   int FETCH_REVERSE = 1001;
   int FETCH_UNKNOWN = 1002;
   int TYPE_FORWARD_ONLY = 1003;
   int TYPE_SCROLL_INSENSITIVE = 1004;
   int TYPE_SCROLL_SENSITIVE = 1005;
   int CONCUR_READ_ONLY = 1007;
   int CONCUR_UPDATABLE = 1008;
   int HOLD_CURSORS_OVER_COMMIT = 1;
   int CLOSE_CURSORS_AT_COMMIT = 2;

   boolean next() throws SQLException;

   void close() throws SQLException;

   boolean wasNull() throws SQLException;

   String getString(int var1) throws SQLException;

   boolean getBoolean(int var1) throws SQLException;

   byte getByte(int var1) throws SQLException;

   short getShort(int var1) throws SQLException;

   int getInt(int var1) throws SQLException;

   long getLong(int var1) throws SQLException;

   float getFloat(int var1) throws SQLException;

   double getDouble(int var1) throws SQLException;

   /** @deprecated */
   @Deprecated
   BigDecimal getBigDecimal(int var1, int var2) throws SQLException;

   byte[] getBytes(int var1) throws SQLException;

   Date getDate(int var1) throws SQLException;

   Time getTime(int var1) throws SQLException;

   Timestamp getTimestamp(int var1) throws SQLException;

   InputStream getAsciiStream(int var1) throws SQLException;

   /** @deprecated */
   @Deprecated
   InputStream getUnicodeStream(int var1) throws SQLException;

   InputStream getBinaryStream(int var1) throws SQLException;

   String getString(String var1) throws SQLException;

   boolean getBoolean(String var1) throws SQLException;

   byte getByte(String var1) throws SQLException;

   short getShort(String var1) throws SQLException;

   int getInt(String var1) throws SQLException;

   long getLong(String var1) throws SQLException;

   float getFloat(String var1) throws SQLException;

   double getDouble(String var1) throws SQLException;

   /** @deprecated */
   @Deprecated
   BigDecimal getBigDecimal(String var1, int var2) throws SQLException;

   byte[] getBytes(String var1) throws SQLException;

   Date getDate(String var1) throws SQLException;

   Time getTime(String var1) throws SQLException;

   Timestamp getTimestamp(String var1) throws SQLException;

   InputStream getAsciiStream(String var1) throws SQLException;

   /** @deprecated */
   @Deprecated
   InputStream getUnicodeStream(String var1) throws SQLException;

   InputStream getBinaryStream(String var1) throws SQLException;

   SQLWarning getWarnings() throws SQLException;

   void clearWarnings() throws SQLException;

   String getCursorName() throws SQLException;

   ResultSetMetaData getMetaData() throws SQLException;

   Object getObject(int var1) throws SQLException;

   Object getObject(String var1) throws SQLException;

   int findColumn(String var1) throws SQLException;

   Reader getCharacterStream(int var1) throws SQLException;

   Reader getCharacterStream(String var1) throws SQLException;

   BigDecimal getBigDecimal(int var1) throws SQLException;

   BigDecimal getBigDecimal(String var1) throws SQLException;

   boolean isBeforeFirst() throws SQLException;

   boolean isAfterLast() throws SQLException;

   boolean isFirst() throws SQLException;

   boolean isLast() throws SQLException;

   void beforeFirst() throws SQLException;

   void afterLast() throws SQLException;

   boolean first() throws SQLException;

   boolean last() throws SQLException;

   int getRow() throws SQLException;

   boolean absolute(int var1) throws SQLException;

   boolean relative(int var1) throws SQLException;

   boolean previous() throws SQLException;

   void setFetchDirection(int var1) throws SQLException;

   int getFetchDirection() throws SQLException;

   void setFetchSize(int var1) throws SQLException;

   int getFetchSize() throws SQLException;

   int getType() throws SQLException;

   int getConcurrency() throws SQLException;

   boolean rowUpdated() throws SQLException;

   boolean rowInserted() throws SQLException;

   boolean rowDeleted() throws SQLException;

   void updateNull(int var1) throws SQLException;

   void updateBoolean(int var1, boolean var2) throws SQLException;

   void updateByte(int var1, byte var2) throws SQLException;

   void updateShort(int var1, short var2) throws SQLException;

   void updateInt(int var1, int var2) throws SQLException;

   void updateLong(int var1, long var2) throws SQLException;

   void updateFloat(int var1, float var2) throws SQLException;

   void updateDouble(int var1, double var2) throws SQLException;

   void updateBigDecimal(int var1, BigDecimal var2) throws SQLException;

   void updateString(int var1, String var2) throws SQLException;

   void updateBytes(int var1, byte[] var2) throws SQLException;

   void updateDate(int var1, Date var2) throws SQLException;

   void updateTime(int var1, Time var2) throws SQLException;

   void updateTimestamp(int var1, Timestamp var2) throws SQLException;

   void updateAsciiStream(int var1, InputStream var2, int var3) throws SQLException;

   void updateBinaryStream(int var1, InputStream var2, int var3) throws SQLException;

   void updateCharacterStream(int var1, Reader var2, int var3) throws SQLException;

   void updateObject(int var1, Object var2, int var3) throws SQLException;

   void updateObject(int var1, Object var2) throws SQLException;

   void updateNull(String var1) throws SQLException;

   void updateBoolean(String var1, boolean var2) throws SQLException;

   void updateByte(String var1, byte var2) throws SQLException;

   void updateShort(String var1, short var2) throws SQLException;

   void updateInt(String var1, int var2) throws SQLException;

   void updateLong(String var1, long var2) throws SQLException;

   void updateFloat(String var1, float var2) throws SQLException;

   void updateDouble(String var1, double var2) throws SQLException;

   void updateBigDecimal(String var1, BigDecimal var2) throws SQLException;

   void updateString(String var1, String var2) throws SQLException;

   void updateBytes(String var1, byte[] var2) throws SQLException;

   void updateDate(String var1, Date var2) throws SQLException;

   void updateTime(String var1, Time var2) throws SQLException;

   void updateTimestamp(String var1, Timestamp var2) throws SQLException;

   void updateAsciiStream(String var1, InputStream var2, int var3) throws SQLException;

   void updateBinaryStream(String var1, InputStream var2, int var3) throws SQLException;

   void updateCharacterStream(String var1, Reader var2, int var3) throws SQLException;

   void updateObject(String var1, Object var2, int var3) throws SQLException;

   void updateObject(String var1, Object var2) throws SQLException;

   void insertRow() throws SQLException;

   void updateRow() throws SQLException;

   void deleteRow() throws SQLException;

   void refreshRow() throws SQLException;

   void cancelRowUpdates() throws SQLException;

   void moveToInsertRow() throws SQLException;

   void moveToCurrentRow() throws SQLException;

   Statement getStatement() throws SQLException;

   Object getObject(int var1, Map<String, Class<?>> var2) throws SQLException;

   Ref getRef(int var1) throws SQLException;

   Blob getBlob(int var1) throws SQLException;

   Clob getClob(int var1) throws SQLException;

   Array getArray(int var1) throws SQLException;

   Object getObject(String var1, Map<String, Class<?>> var2) throws SQLException;

   Ref getRef(String var1) throws SQLException;

   Blob getBlob(String var1) throws SQLException;

   Clob getClob(String var1) throws SQLException;

   Array getArray(String var1) throws SQLException;

   Date getDate(int var1, Calendar var2) throws SQLException;

   Date getDate(String var1, Calendar var2) throws SQLException;

   Time getTime(int var1, Calendar var2) throws SQLException;

   Time getTime(String var1, Calendar var2) throws SQLException;

   Timestamp getTimestamp(int var1, Calendar var2) throws SQLException;

   Timestamp getTimestamp(String var1, Calendar var2) throws SQLException;

   URL getURL(int var1) throws SQLException;

   URL getURL(String var1) throws SQLException;

   void updateRef(int var1, Ref var2) throws SQLException;

   void updateRef(String var1, Ref var2) throws SQLException;

   void updateBlob(int var1, Blob var2) throws SQLException;

   void updateBlob(String var1, Blob var2) throws SQLException;

   void updateClob(int var1, Clob var2) throws SQLException;

   void updateClob(String var1, Clob var2) throws SQLException;

   void updateArray(int var1, Array var2) throws SQLException;

   void updateArray(String var1, Array var2) throws SQLException;

   RowId getRowId(int var1) throws SQLException;

   RowId getRowId(String var1) throws SQLException;

   void updateRowId(int var1, RowId var2) throws SQLException;

   void updateRowId(String var1, RowId var2) throws SQLException;

   int getHoldability() throws SQLException;

   boolean isClosed() throws SQLException;

   void updateNString(int var1, String var2) throws SQLException;

   void updateNString(String var1, String var2) throws SQLException;

   void updateNClob(int var1, NClob var2) throws SQLException;

   void updateNClob(String var1, NClob var2) throws SQLException;

   NClob getNClob(int var1) throws SQLException;

   NClob getNClob(String var1) throws SQLException;

   SQLXML getSQLXML(int var1) throws SQLException;

   SQLXML getSQLXML(String var1) throws SQLException;

   void updateSQLXML(int var1, SQLXML var2) throws SQLException;

   void updateSQLXML(String var1, SQLXML var2) throws SQLException;

   String getNString(int var1) throws SQLException;

   String getNString(String var1) throws SQLException;

   Reader getNCharacterStream(int var1) throws SQLException;

   Reader getNCharacterStream(String var1) throws SQLException;

   void updateNCharacterStream(int var1, Reader var2, long var3) throws SQLException;

   void updateNCharacterStream(String var1, Reader var2, long var3) throws SQLException;

   void updateAsciiStream(int var1, InputStream var2, long var3) throws SQLException;

   void updateBinaryStream(int var1, InputStream var2, long var3) throws SQLException;

   void updateCharacterStream(int var1, Reader var2, long var3) throws SQLException;

   void updateAsciiStream(String var1, InputStream var2, long var3) throws SQLException;

   void updateBinaryStream(String var1, InputStream var2, long var3) throws SQLException;

   void updateCharacterStream(String var1, Reader var2, long var3) throws SQLException;

   void updateBlob(int var1, InputStream var2, long var3) throws SQLException;

   void updateBlob(String var1, InputStream var2, long var3) throws SQLException;

   void updateClob(int var1, Reader var2, long var3) throws SQLException;

   void updateClob(String var1, Reader var2, long var3) throws SQLException;

   void updateNClob(int var1, Reader var2, long var3) throws SQLException;

   void updateNClob(String var1, Reader var2, long var3) throws SQLException;

   void updateNCharacterStream(int var1, Reader var2) throws SQLException;

   void updateNCharacterStream(String var1, Reader var2) throws SQLException;

   void updateAsciiStream(int var1, InputStream var2) throws SQLException;

   void updateBinaryStream(int var1, InputStream var2) throws SQLException;

   void updateCharacterStream(int var1, Reader var2) throws SQLException;

   void updateAsciiStream(String var1, InputStream var2) throws SQLException;

   void updateBinaryStream(String var1, InputStream var2) throws SQLException;

   void updateCharacterStream(String var1, Reader var2) throws SQLException;

   void updateBlob(int var1, InputStream var2) throws SQLException;

   void updateBlob(String var1, InputStream var2) throws SQLException;

   void updateClob(int var1, Reader var2) throws SQLException;

   void updateClob(String var1, Reader var2) throws SQLException;

   void updateNClob(int var1, Reader var2) throws SQLException;

   void updateNClob(String var1, Reader var2) throws SQLException;

   <T> T getObject(int var1, Class<T> var2) throws SQLException;

   <T> T getObject(String var1, Class<T> var2) throws SQLException;

   default void updateObject(int var1, Object var2, SQLType var3, int var4) throws SQLException {
      throw new SQLFeatureNotSupportedException("updateObject not implemented");
   }

   default void updateObject(String var1, Object var2, SQLType var3, int var4) throws SQLException {
      throw new SQLFeatureNotSupportedException("updateObject not implemented");
   }

   default void updateObject(int var1, Object var2, SQLType var3) throws SQLException {
      throw new SQLFeatureNotSupportedException("updateObject not implemented");
   }

   default void updateObject(String var1, Object var2, SQLType var3) throws SQLException {
      throw new SQLFeatureNotSupportedException("updateObject not implemented");
   }
}
