package javax.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

public interface RowSet extends ResultSet {
   String getUrl() throws SQLException;

   void setUrl(String var1) throws SQLException;

   String getDataSourceName();

   void setDataSourceName(String var1) throws SQLException;

   String getUsername();

   void setUsername(String var1) throws SQLException;

   String getPassword();

   void setPassword(String var1) throws SQLException;

   int getTransactionIsolation();

   void setTransactionIsolation(int var1) throws SQLException;

   Map<String, Class<?>> getTypeMap() throws SQLException;

   void setTypeMap(Map<String, Class<?>> var1) throws SQLException;

   String getCommand();

   void setCommand(String var1) throws SQLException;

   boolean isReadOnly();

   void setReadOnly(boolean var1) throws SQLException;

   int getMaxFieldSize() throws SQLException;

   void setMaxFieldSize(int var1) throws SQLException;

   int getMaxRows() throws SQLException;

   void setMaxRows(int var1) throws SQLException;

   boolean getEscapeProcessing() throws SQLException;

   void setEscapeProcessing(boolean var1) throws SQLException;

   int getQueryTimeout() throws SQLException;

   void setQueryTimeout(int var1) throws SQLException;

   void setType(int var1) throws SQLException;

   void setConcurrency(int var1) throws SQLException;

   void setNull(int var1, int var2) throws SQLException;

   void setNull(String var1, int var2) throws SQLException;

   void setNull(int var1, int var2, String var3) throws SQLException;

   void setNull(String var1, int var2, String var3) throws SQLException;

   void setBoolean(int var1, boolean var2) throws SQLException;

   void setBoolean(String var1, boolean var2) throws SQLException;

   void setByte(int var1, byte var2) throws SQLException;

   void setByte(String var1, byte var2) throws SQLException;

   void setShort(int var1, short var2) throws SQLException;

   void setShort(String var1, short var2) throws SQLException;

   void setInt(int var1, int var2) throws SQLException;

   void setInt(String var1, int var2) throws SQLException;

   void setLong(int var1, long var2) throws SQLException;

   void setLong(String var1, long var2) throws SQLException;

   void setFloat(int var1, float var2) throws SQLException;

   void setFloat(String var1, float var2) throws SQLException;

   void setDouble(int var1, double var2) throws SQLException;

   void setDouble(String var1, double var2) throws SQLException;

   void setBigDecimal(int var1, BigDecimal var2) throws SQLException;

   void setBigDecimal(String var1, BigDecimal var2) throws SQLException;

   void setString(int var1, String var2) throws SQLException;

   void setString(String var1, String var2) throws SQLException;

   void setBytes(int var1, byte[] var2) throws SQLException;

   void setBytes(String var1, byte[] var2) throws SQLException;

   void setDate(int var1, Date var2) throws SQLException;

   void setTime(int var1, Time var2) throws SQLException;

   void setTimestamp(int var1, Timestamp var2) throws SQLException;

   void setTimestamp(String var1, Timestamp var2) throws SQLException;

   void setAsciiStream(int var1, InputStream var2, int var3) throws SQLException;

   void setAsciiStream(String var1, InputStream var2, int var3) throws SQLException;

   void setBinaryStream(int var1, InputStream var2, int var3) throws SQLException;

   void setBinaryStream(String var1, InputStream var2, int var3) throws SQLException;

   void setCharacterStream(int var1, Reader var2, int var3) throws SQLException;

   void setCharacterStream(String var1, Reader var2, int var3) throws SQLException;

   void setAsciiStream(int var1, InputStream var2) throws SQLException;

   void setAsciiStream(String var1, InputStream var2) throws SQLException;

   void setBinaryStream(int var1, InputStream var2) throws SQLException;

   void setBinaryStream(String var1, InputStream var2) throws SQLException;

   void setCharacterStream(int var1, Reader var2) throws SQLException;

   void setCharacterStream(String var1, Reader var2) throws SQLException;

   void setNCharacterStream(int var1, Reader var2) throws SQLException;

   void setObject(int var1, Object var2, int var3, int var4) throws SQLException;

   void setObject(String var1, Object var2, int var3, int var4) throws SQLException;

   void setObject(int var1, Object var2, int var3) throws SQLException;

   void setObject(String var1, Object var2, int var3) throws SQLException;

   void setObject(String var1, Object var2) throws SQLException;

   void setObject(int var1, Object var2) throws SQLException;

   void setRef(int var1, Ref var2) throws SQLException;

   void setBlob(int var1, Blob var2) throws SQLException;

   void setBlob(int var1, InputStream var2, long var3) throws SQLException;

   void setBlob(int var1, InputStream var2) throws SQLException;

   void setBlob(String var1, InputStream var2, long var3) throws SQLException;

   void setBlob(String var1, Blob var2) throws SQLException;

   void setBlob(String var1, InputStream var2) throws SQLException;

   void setClob(int var1, Clob var2) throws SQLException;

   void setClob(int var1, Reader var2, long var3) throws SQLException;

   void setClob(int var1, Reader var2) throws SQLException;

   void setClob(String var1, Reader var2, long var3) throws SQLException;

   void setClob(String var1, Clob var2) throws SQLException;

   void setClob(String var1, Reader var2) throws SQLException;

   void setArray(int var1, Array var2) throws SQLException;

   void setDate(int var1, Date var2, Calendar var3) throws SQLException;

   void setDate(String var1, Date var2) throws SQLException;

   void setDate(String var1, Date var2, Calendar var3) throws SQLException;

   void setTime(int var1, Time var2, Calendar var3) throws SQLException;

   void setTime(String var1, Time var2) throws SQLException;

   void setTime(String var1, Time var2, Calendar var3) throws SQLException;

   void setTimestamp(int var1, Timestamp var2, Calendar var3) throws SQLException;

   void setTimestamp(String var1, Timestamp var2, Calendar var3) throws SQLException;

   void clearParameters() throws SQLException;

   void execute() throws SQLException;

   void addRowSetListener(RowSetListener var1);

   void removeRowSetListener(RowSetListener var1);

   void setSQLXML(int var1, SQLXML var2) throws SQLException;

   void setSQLXML(String var1, SQLXML var2) throws SQLException;

   void setRowId(int var1, RowId var2) throws SQLException;

   void setRowId(String var1, RowId var2) throws SQLException;

   void setNString(int var1, String var2) throws SQLException;

   void setNString(String var1, String var2) throws SQLException;

   void setNCharacterStream(int var1, Reader var2, long var3) throws SQLException;

   void setNCharacterStream(String var1, Reader var2, long var3) throws SQLException;

   void setNCharacterStream(String var1, Reader var2) throws SQLException;

   void setNClob(String var1, NClob var2) throws SQLException;

   void setNClob(String var1, Reader var2, long var3) throws SQLException;

   void setNClob(String var1, Reader var2) throws SQLException;

   void setNClob(int var1, Reader var2, long var3) throws SQLException;

   void setNClob(int var1, NClob var2) throws SQLException;

   void setNClob(int var1, Reader var2) throws SQLException;

   void setURL(int var1, URL var2) throws SQLException;
}
