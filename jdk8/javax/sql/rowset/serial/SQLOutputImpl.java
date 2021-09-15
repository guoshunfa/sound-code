package javax.sql.rowset.serial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.sql.SQLXML;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Vector;

public class SQLOutputImpl implements SQLOutput {
   private Vector attribs;
   private Map map;

   public SQLOutputImpl(Vector<?> var1, Map<String, ?> var2) throws SQLException {
      if (var1 != null && var2 != null) {
         this.attribs = var1;
         this.map = var2;
      } else {
         throw new SQLException("Cannot instantiate a SQLOutputImpl instance with null parameters");
      }
   }

   public void writeString(String var1) throws SQLException {
      this.attribs.add(var1);
   }

   public void writeBoolean(boolean var1) throws SQLException {
      this.attribs.add(var1);
   }

   public void writeByte(byte var1) throws SQLException {
      this.attribs.add(var1);
   }

   public void writeShort(short var1) throws SQLException {
      this.attribs.add(var1);
   }

   public void writeInt(int var1) throws SQLException {
      this.attribs.add(var1);
   }

   public void writeLong(long var1) throws SQLException {
      this.attribs.add(var1);
   }

   public void writeFloat(float var1) throws SQLException {
      this.attribs.add(var1);
   }

   public void writeDouble(double var1) throws SQLException {
      this.attribs.add(var1);
   }

   public void writeBigDecimal(BigDecimal var1) throws SQLException {
      this.attribs.add(var1);
   }

   public void writeBytes(byte[] var1) throws SQLException {
      this.attribs.add(var1);
   }

   public void writeDate(Date var1) throws SQLException {
      this.attribs.add(var1);
   }

   public void writeTime(Time var1) throws SQLException {
      this.attribs.add(var1);
   }

   public void writeTimestamp(Timestamp var1) throws SQLException {
      this.attribs.add(var1);
   }

   public void writeCharacterStream(Reader var1) throws SQLException {
      BufferedReader var2 = new BufferedReader(var1);

      int var3;
      try {
         while((var3 = var2.read()) != -1) {
            char var4 = (char)var3;
            StringBuffer var5 = new StringBuffer();
            var5.append(var4);
            String var6 = new String(var5);
            String var7 = var2.readLine();
            this.writeString(var6.concat(var7));
         }
      } catch (IOException var8) {
      }

   }

   public void writeAsciiStream(InputStream var1) throws SQLException {
      BufferedReader var2 = new BufferedReader(new InputStreamReader(var1));

      try {
         int var3;
         while((var3 = var2.read()) != -1) {
            char var4 = (char)var3;
            StringBuffer var5 = new StringBuffer();
            var5.append(var4);
            String var6 = new String(var5);
            String var7 = var2.readLine();
            this.writeString(var6.concat(var7));
         }

      } catch (IOException var8) {
         throw new SQLException(var8.getMessage());
      }
   }

   public void writeBinaryStream(InputStream var1) throws SQLException {
      BufferedReader var2 = new BufferedReader(new InputStreamReader(var1));

      try {
         int var3;
         while((var3 = var2.read()) != -1) {
            char var4 = (char)var3;
            StringBuffer var5 = new StringBuffer();
            var5.append(var4);
            String var6 = new String(var5);
            String var7 = var2.readLine();
            this.writeString(var6.concat(var7));
         }

      } catch (IOException var8) {
         throw new SQLException(var8.getMessage());
      }
   }

   public void writeObject(SQLData var1) throws SQLException {
      if (var1 == null) {
         this.attribs.add((Object)null);
      } else {
         this.attribs.add(new SerialStruct(var1, this.map));
      }

   }

   public void writeRef(Ref var1) throws SQLException {
      if (var1 == null) {
         this.attribs.add((Object)null);
      } else {
         this.attribs.add(new SerialRef(var1));
      }

   }

   public void writeBlob(Blob var1) throws SQLException {
      if (var1 == null) {
         this.attribs.add((Object)null);
      } else {
         this.attribs.add(new SerialBlob(var1));
      }

   }

   public void writeClob(Clob var1) throws SQLException {
      if (var1 == null) {
         this.attribs.add((Object)null);
      } else {
         this.attribs.add(new SerialClob(var1));
      }

   }

   public void writeStruct(Struct var1) throws SQLException {
      SerialStruct var2 = new SerialStruct(var1, this.map);
      this.attribs.add(var2);
   }

   public void writeArray(Array var1) throws SQLException {
      if (var1 == null) {
         this.attribs.add((Object)null);
      } else {
         this.attribs.add(new SerialArray(var1, this.map));
      }

   }

   public void writeURL(URL var1) throws SQLException {
      if (var1 == null) {
         this.attribs.add((Object)null);
      } else {
         this.attribs.add(new SerialDatalink(var1));
      }

   }

   public void writeNString(String var1) throws SQLException {
      this.attribs.add(var1);
   }

   public void writeNClob(NClob var1) throws SQLException {
      this.attribs.add(var1);
   }

   public void writeRowId(RowId var1) throws SQLException {
      this.attribs.add(var1);
   }

   public void writeSQLXML(SQLXML var1) throws SQLException {
      this.attribs.add(var1);
   }
}
