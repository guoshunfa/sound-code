package javax.sql.rowset.serial;

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
import java.sql.RowId;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLXML;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Map;
import sun.reflect.misc.ReflectUtil;

public class SQLInputImpl implements SQLInput {
   private boolean lastValueWasNull;
   private int idx;
   private Object[] attrib;
   private Map<String, Class<?>> map;

   public SQLInputImpl(Object[] var1, Map<String, Class<?>> var2) throws SQLException {
      if (var1 != null && var2 != null) {
         this.attrib = Arrays.copyOf(var1, var1.length);
         this.idx = -1;
         this.map = var2;
      } else {
         throw new SQLException("Cannot instantiate a SQLInputImpl object with null parameters");
      }
   }

   private Object getNextAttribute() throws SQLException {
      if (++this.idx >= this.attrib.length) {
         throw new SQLException("SQLInputImpl exception: Invalid read position");
      } else {
         this.lastValueWasNull = this.attrib[this.idx] == null;
         return this.attrib[this.idx];
      }
   }

   public String readString() throws SQLException {
      return (String)this.getNextAttribute();
   }

   public boolean readBoolean() throws SQLException {
      Boolean var1 = (Boolean)this.getNextAttribute();
      return var1 == null ? false : var1;
   }

   public byte readByte() throws SQLException {
      Byte var1 = (Byte)this.getNextAttribute();
      return var1 == null ? 0 : var1;
   }

   public short readShort() throws SQLException {
      Short var1 = (Short)this.getNextAttribute();
      return var1 == null ? 0 : var1;
   }

   public int readInt() throws SQLException {
      Integer var1 = (Integer)this.getNextAttribute();
      return var1 == null ? 0 : var1;
   }

   public long readLong() throws SQLException {
      Long var1 = (Long)this.getNextAttribute();
      return var1 == null ? 0L : var1;
   }

   public float readFloat() throws SQLException {
      Float var1 = (Float)this.getNextAttribute();
      return var1 == null ? 0.0F : var1;
   }

   public double readDouble() throws SQLException {
      Double var1 = (Double)this.getNextAttribute();
      return var1 == null ? 0.0D : var1;
   }

   public BigDecimal readBigDecimal() throws SQLException {
      return (BigDecimal)this.getNextAttribute();
   }

   public byte[] readBytes() throws SQLException {
      return (byte[])((byte[])this.getNextAttribute());
   }

   public Date readDate() throws SQLException {
      return (Date)this.getNextAttribute();
   }

   public Time readTime() throws SQLException {
      return (Time)this.getNextAttribute();
   }

   public Timestamp readTimestamp() throws SQLException {
      return (Timestamp)this.getNextAttribute();
   }

   public Reader readCharacterStream() throws SQLException {
      return (Reader)this.getNextAttribute();
   }

   public InputStream readAsciiStream() throws SQLException {
      return (InputStream)this.getNextAttribute();
   }

   public InputStream readBinaryStream() throws SQLException {
      return (InputStream)this.getNextAttribute();
   }

   public Object readObject() throws SQLException {
      Object var1 = this.getNextAttribute();
      if (var1 instanceof Struct) {
         Struct var2 = (Struct)var1;
         Class var3 = (Class)this.map.get(var2.getSQLTypeName());
         if (var3 != null) {
            SQLData var4 = null;

            try {
               var4 = (SQLData)ReflectUtil.newInstance(var3);
            } catch (Exception var7) {
               throw new SQLException("Unable to Instantiate: ", var7);
            }

            Object[] var5 = var2.getAttributes(this.map);
            SQLInputImpl var6 = new SQLInputImpl(var5, this.map);
            var4.readSQL(var6, var2.getSQLTypeName());
            return var4;
         }
      }

      return var1;
   }

   public Ref readRef() throws SQLException {
      return (Ref)this.getNextAttribute();
   }

   public Blob readBlob() throws SQLException {
      return (Blob)this.getNextAttribute();
   }

   public Clob readClob() throws SQLException {
      return (Clob)this.getNextAttribute();
   }

   public Array readArray() throws SQLException {
      return (Array)this.getNextAttribute();
   }

   public boolean wasNull() throws SQLException {
      return this.lastValueWasNull;
   }

   public URL readURL() throws SQLException {
      return (URL)this.getNextAttribute();
   }

   public NClob readNClob() throws SQLException {
      return (NClob)this.getNextAttribute();
   }

   public String readNString() throws SQLException {
      return (String)this.getNextAttribute();
   }

   public SQLXML readSQLXML() throws SQLException {
      return (SQLXML)this.getNextAttribute();
   }

   public RowId readRowId() throws SQLException {
      return (RowId)this.getNextAttribute();
   }
}
