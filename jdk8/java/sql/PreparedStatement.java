package java.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Calendar;

public interface PreparedStatement extends Statement {
   ResultSet executeQuery() throws SQLException;

   int executeUpdate() throws SQLException;

   void setNull(int var1, int var2) throws SQLException;

   void setBoolean(int var1, boolean var2) throws SQLException;

   void setByte(int var1, byte var2) throws SQLException;

   void setShort(int var1, short var2) throws SQLException;

   void setInt(int var1, int var2) throws SQLException;

   void setLong(int var1, long var2) throws SQLException;

   void setFloat(int var1, float var2) throws SQLException;

   void setDouble(int var1, double var2) throws SQLException;

   void setBigDecimal(int var1, BigDecimal var2) throws SQLException;

   void setString(int var1, String var2) throws SQLException;

   void setBytes(int var1, byte[] var2) throws SQLException;

   void setDate(int var1, Date var2) throws SQLException;

   void setTime(int var1, Time var2) throws SQLException;

   void setTimestamp(int var1, Timestamp var2) throws SQLException;

   void setAsciiStream(int var1, InputStream var2, int var3) throws SQLException;

   /** @deprecated */
   @Deprecated
   void setUnicodeStream(int var1, InputStream var2, int var3) throws SQLException;

   void setBinaryStream(int var1, InputStream var2, int var3) throws SQLException;

   void clearParameters() throws SQLException;

   void setObject(int var1, Object var2, int var3) throws SQLException;

   void setObject(int var1, Object var2) throws SQLException;

   boolean execute() throws SQLException;

   void addBatch() throws SQLException;

   void setCharacterStream(int var1, Reader var2, int var3) throws SQLException;

   void setRef(int var1, Ref var2) throws SQLException;

   void setBlob(int var1, Blob var2) throws SQLException;

   void setClob(int var1, Clob var2) throws SQLException;

   void setArray(int var1, Array var2) throws SQLException;

   ResultSetMetaData getMetaData() throws SQLException;

   void setDate(int var1, Date var2, Calendar var3) throws SQLException;

   void setTime(int var1, Time var2, Calendar var3) throws SQLException;

   void setTimestamp(int var1, Timestamp var2, Calendar var3) throws SQLException;

   void setNull(int var1, int var2, String var3) throws SQLException;

   void setURL(int var1, URL var2) throws SQLException;

   ParameterMetaData getParameterMetaData() throws SQLException;

   void setRowId(int var1, RowId var2) throws SQLException;

   void setNString(int var1, String var2) throws SQLException;

   void setNCharacterStream(int var1, Reader var2, long var3) throws SQLException;

   void setNClob(int var1, NClob var2) throws SQLException;

   void setClob(int var1, Reader var2, long var3) throws SQLException;

   void setBlob(int var1, InputStream var2, long var3) throws SQLException;

   void setNClob(int var1, Reader var2, long var3) throws SQLException;

   void setSQLXML(int var1, SQLXML var2) throws SQLException;

   void setObject(int var1, Object var2, int var3, int var4) throws SQLException;

   void setAsciiStream(int var1, InputStream var2, long var3) throws SQLException;

   void setBinaryStream(int var1, InputStream var2, long var3) throws SQLException;

   void setCharacterStream(int var1, Reader var2, long var3) throws SQLException;

   void setAsciiStream(int var1, InputStream var2) throws SQLException;

   void setBinaryStream(int var1, InputStream var2) throws SQLException;

   void setCharacterStream(int var1, Reader var2) throws SQLException;

   void setNCharacterStream(int var1, Reader var2) throws SQLException;

   void setClob(int var1, Reader var2) throws SQLException;

   void setBlob(int var1, InputStream var2) throws SQLException;

   void setNClob(int var1, Reader var2) throws SQLException;

   default void setObject(int var1, Object var2, SQLType var3, int var4) throws SQLException {
      throw new SQLFeatureNotSupportedException("setObject not implemented");
   }

   default void setObject(int var1, Object var2, SQLType var3) throws SQLException {
      throw new SQLFeatureNotSupportedException("setObject not implemented");
   }

   default long executeLargeUpdate() throws SQLException {
      throw new UnsupportedOperationException("executeLargeUpdate not implemented");
   }
}
