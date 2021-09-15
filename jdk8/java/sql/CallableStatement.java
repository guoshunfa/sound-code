package java.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Calendar;
import java.util.Map;

public interface CallableStatement extends PreparedStatement {
   void registerOutParameter(int var1, int var2) throws SQLException;

   void registerOutParameter(int var1, int var2, int var3) throws SQLException;

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

   Object getObject(int var1) throws SQLException;

   BigDecimal getBigDecimal(int var1) throws SQLException;

   Object getObject(int var1, Map<String, Class<?>> var2) throws SQLException;

   Ref getRef(int var1) throws SQLException;

   Blob getBlob(int var1) throws SQLException;

   Clob getClob(int var1) throws SQLException;

   Array getArray(int var1) throws SQLException;

   Date getDate(int var1, Calendar var2) throws SQLException;

   Time getTime(int var1, Calendar var2) throws SQLException;

   Timestamp getTimestamp(int var1, Calendar var2) throws SQLException;

   void registerOutParameter(int var1, int var2, String var3) throws SQLException;

   void registerOutParameter(String var1, int var2) throws SQLException;

   void registerOutParameter(String var1, int var2, int var3) throws SQLException;

   void registerOutParameter(String var1, int var2, String var3) throws SQLException;

   URL getURL(int var1) throws SQLException;

   void setURL(String var1, URL var2) throws SQLException;

   void setNull(String var1, int var2) throws SQLException;

   void setBoolean(String var1, boolean var2) throws SQLException;

   void setByte(String var1, byte var2) throws SQLException;

   void setShort(String var1, short var2) throws SQLException;

   void setInt(String var1, int var2) throws SQLException;

   void setLong(String var1, long var2) throws SQLException;

   void setFloat(String var1, float var2) throws SQLException;

   void setDouble(String var1, double var2) throws SQLException;

   void setBigDecimal(String var1, BigDecimal var2) throws SQLException;

   void setString(String var1, String var2) throws SQLException;

   void setBytes(String var1, byte[] var2) throws SQLException;

   void setDate(String var1, Date var2) throws SQLException;

   void setTime(String var1, Time var2) throws SQLException;

   void setTimestamp(String var1, Timestamp var2) throws SQLException;

   void setAsciiStream(String var1, InputStream var2, int var3) throws SQLException;

   void setBinaryStream(String var1, InputStream var2, int var3) throws SQLException;

   void setObject(String var1, Object var2, int var3, int var4) throws SQLException;

   void setObject(String var1, Object var2, int var3) throws SQLException;

   void setObject(String var1, Object var2) throws SQLException;

   void setCharacterStream(String var1, Reader var2, int var3) throws SQLException;

   void setDate(String var1, Date var2, Calendar var3) throws SQLException;

   void setTime(String var1, Time var2, Calendar var3) throws SQLException;

   void setTimestamp(String var1, Timestamp var2, Calendar var3) throws SQLException;

   void setNull(String var1, int var2, String var3) throws SQLException;

   String getString(String var1) throws SQLException;

   boolean getBoolean(String var1) throws SQLException;

   byte getByte(String var1) throws SQLException;

   short getShort(String var1) throws SQLException;

   int getInt(String var1) throws SQLException;

   long getLong(String var1) throws SQLException;

   float getFloat(String var1) throws SQLException;

   double getDouble(String var1) throws SQLException;

   byte[] getBytes(String var1) throws SQLException;

   Date getDate(String var1) throws SQLException;

   Time getTime(String var1) throws SQLException;

   Timestamp getTimestamp(String var1) throws SQLException;

   Object getObject(String var1) throws SQLException;

   BigDecimal getBigDecimal(String var1) throws SQLException;

   Object getObject(String var1, Map<String, Class<?>> var2) throws SQLException;

   Ref getRef(String var1) throws SQLException;

   Blob getBlob(String var1) throws SQLException;

   Clob getClob(String var1) throws SQLException;

   Array getArray(String var1) throws SQLException;

   Date getDate(String var1, Calendar var2) throws SQLException;

   Time getTime(String var1, Calendar var2) throws SQLException;

   Timestamp getTimestamp(String var1, Calendar var2) throws SQLException;

   URL getURL(String var1) throws SQLException;

   RowId getRowId(int var1) throws SQLException;

   RowId getRowId(String var1) throws SQLException;

   void setRowId(String var1, RowId var2) throws SQLException;

   void setNString(String var1, String var2) throws SQLException;

   void setNCharacterStream(String var1, Reader var2, long var3) throws SQLException;

   void setNClob(String var1, NClob var2) throws SQLException;

   void setClob(String var1, Reader var2, long var3) throws SQLException;

   void setBlob(String var1, InputStream var2, long var3) throws SQLException;

   void setNClob(String var1, Reader var2, long var3) throws SQLException;

   NClob getNClob(int var1) throws SQLException;

   NClob getNClob(String var1) throws SQLException;

   void setSQLXML(String var1, SQLXML var2) throws SQLException;

   SQLXML getSQLXML(int var1) throws SQLException;

   SQLXML getSQLXML(String var1) throws SQLException;

   String getNString(int var1) throws SQLException;

   String getNString(String var1) throws SQLException;

   Reader getNCharacterStream(int var1) throws SQLException;

   Reader getNCharacterStream(String var1) throws SQLException;

   Reader getCharacterStream(int var1) throws SQLException;

   Reader getCharacterStream(String var1) throws SQLException;

   void setBlob(String var1, Blob var2) throws SQLException;

   void setClob(String var1, Clob var2) throws SQLException;

   void setAsciiStream(String var1, InputStream var2, long var3) throws SQLException;

   void setBinaryStream(String var1, InputStream var2, long var3) throws SQLException;

   void setCharacterStream(String var1, Reader var2, long var3) throws SQLException;

   void setAsciiStream(String var1, InputStream var2) throws SQLException;

   void setBinaryStream(String var1, InputStream var2) throws SQLException;

   void setCharacterStream(String var1, Reader var2) throws SQLException;

   void setNCharacterStream(String var1, Reader var2) throws SQLException;

   void setClob(String var1, Reader var2) throws SQLException;

   void setBlob(String var1, InputStream var2) throws SQLException;

   void setNClob(String var1, Reader var2) throws SQLException;

   <T> T getObject(int var1, Class<T> var2) throws SQLException;

   <T> T getObject(String var1, Class<T> var2) throws SQLException;

   default void setObject(String var1, Object var2, SQLType var3, int var4) throws SQLException {
      throw new SQLFeatureNotSupportedException("setObject not implemented");
   }

   default void setObject(String var1, Object var2, SQLType var3) throws SQLException {
      throw new SQLFeatureNotSupportedException("setObject not implemented");
   }

   default void registerOutParameter(int var1, SQLType var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("registerOutParameter not implemented");
   }

   default void registerOutParameter(int var1, SQLType var2, int var3) throws SQLException {
      throw new SQLFeatureNotSupportedException("registerOutParameter not implemented");
   }

   default void registerOutParameter(int var1, SQLType var2, String var3) throws SQLException {
      throw new SQLFeatureNotSupportedException("registerOutParameter not implemented");
   }

   default void registerOutParameter(String var1, SQLType var2) throws SQLException {
      throw new SQLFeatureNotSupportedException("registerOutParameter not implemented");
   }

   default void registerOutParameter(String var1, SQLType var2, int var3) throws SQLException {
      throw new SQLFeatureNotSupportedException("registerOutParameter not implemented");
   }

   default void registerOutParameter(String var1, SQLType var2, String var3) throws SQLException {
      throw new SQLFeatureNotSupportedException("registerOutParameter not implemented");
   }
}
