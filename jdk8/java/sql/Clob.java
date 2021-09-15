package java.sql;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public interface Clob {
   long length() throws SQLException;

   String getSubString(long var1, int var3) throws SQLException;

   Reader getCharacterStream() throws SQLException;

   InputStream getAsciiStream() throws SQLException;

   long position(String var1, long var2) throws SQLException;

   long position(Clob var1, long var2) throws SQLException;

   int setString(long var1, String var3) throws SQLException;

   int setString(long var1, String var3, int var4, int var5) throws SQLException;

   OutputStream setAsciiStream(long var1) throws SQLException;

   Writer setCharacterStream(long var1) throws SQLException;

   void truncate(long var1) throws SQLException;

   void free() throws SQLException;

   Reader getCharacterStream(long var1, long var3) throws SQLException;
}
