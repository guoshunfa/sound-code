package java.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;

public interface SQLOutput {
   void writeString(String var1) throws SQLException;

   void writeBoolean(boolean var1) throws SQLException;

   void writeByte(byte var1) throws SQLException;

   void writeShort(short var1) throws SQLException;

   void writeInt(int var1) throws SQLException;

   void writeLong(long var1) throws SQLException;

   void writeFloat(float var1) throws SQLException;

   void writeDouble(double var1) throws SQLException;

   void writeBigDecimal(BigDecimal var1) throws SQLException;

   void writeBytes(byte[] var1) throws SQLException;

   void writeDate(Date var1) throws SQLException;

   void writeTime(Time var1) throws SQLException;

   void writeTimestamp(Timestamp var1) throws SQLException;

   void writeCharacterStream(Reader var1) throws SQLException;

   void writeAsciiStream(InputStream var1) throws SQLException;

   void writeBinaryStream(InputStream var1) throws SQLException;

   void writeObject(SQLData var1) throws SQLException;

   void writeRef(Ref var1) throws SQLException;

   void writeBlob(Blob var1) throws SQLException;

   void writeClob(Clob var1) throws SQLException;

   void writeStruct(Struct var1) throws SQLException;

   void writeArray(Array var1) throws SQLException;

   void writeURL(URL var1) throws SQLException;

   void writeNString(String var1) throws SQLException;

   void writeNClob(NClob var1) throws SQLException;

   void writeRowId(RowId var1) throws SQLException;

   void writeSQLXML(SQLXML var1) throws SQLException;

   default void writeObject(Object var1, SQLType var2) throws SQLException {
      throw new SQLFeatureNotSupportedException();
   }
}
