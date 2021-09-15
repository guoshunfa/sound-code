package java.sql;

import java.io.InputStream;
import java.io.OutputStream;

public interface Blob {
   long length() throws SQLException;

   byte[] getBytes(long var1, int var3) throws SQLException;

   InputStream getBinaryStream() throws SQLException;

   long position(byte[] var1, long var2) throws SQLException;

   long position(Blob var1, long var2) throws SQLException;

   int setBytes(long var1, byte[] var3) throws SQLException;

   int setBytes(long var1, byte[] var3, int var4, int var5) throws SQLException;

   OutputStream setBinaryStream(long var1) throws SQLException;

   void truncate(long var1) throws SQLException;

   void free() throws SQLException;

   InputStream getBinaryStream(long var1, long var3) throws SQLException;
}
