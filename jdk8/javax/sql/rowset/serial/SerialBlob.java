package javax.sql.rowset.serial;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Arrays;

public class SerialBlob implements Blob, Serializable, Cloneable {
   private byte[] buf;
   private Blob blob;
   private long len;
   private long origLen;
   static final long serialVersionUID = -8144641928112860441L;

   public SerialBlob(byte[] var1) throws SerialException, SQLException {
      this.len = (long)var1.length;
      this.buf = new byte[(int)this.len];

      for(int var2 = 0; (long)var2 < this.len; ++var2) {
         this.buf[var2] = var1[var2];
      }

      this.origLen = this.len;
   }

   public SerialBlob(Blob var1) throws SerialException, SQLException {
      if (var1 == null) {
         throw new SQLException("Cannot instantiate a SerialBlob object with a null Blob object");
      } else {
         this.len = var1.length();
         this.buf = var1.getBytes(1L, (int)this.len);
         this.blob = var1;
         this.origLen = this.len;
      }
   }

   public byte[] getBytes(long var1, int var3) throws SerialException {
      this.isValid();
      if ((long)var3 > this.len) {
         var3 = (int)this.len;
      }

      if (var1 >= 1L && this.len - var1 >= 0L) {
         --var1;
         byte[] var4 = new byte[var3];

         for(int var5 = 0; var5 < var3; ++var5) {
            var4[var5] = this.buf[(int)var1];
            ++var1;
         }

         return var4;
      } else {
         throw new SerialException("Invalid arguments: position cannot be less than 1 or greater than the length of the SerialBlob");
      }
   }

   public long length() throws SerialException {
      this.isValid();
      return this.len;
   }

   public InputStream getBinaryStream() throws SerialException {
      this.isValid();
      ByteArrayInputStream var1 = new ByteArrayInputStream(this.buf);
      return var1;
   }

   public long position(byte[] var1, long var2) throws SerialException, SQLException {
      this.isValid();
      if (var2 >= 1L && var2 <= this.len) {
         int var4 = (int)var2 - 1;
         int var5 = 0;
         long var6 = (long)var1.length;

         while((long)var4 < this.len) {
            if (var1[var5] == this.buf[var4]) {
               if ((long)(var5 + 1) == var6) {
                  return (long)(var4 + 1) - (var6 - 1L);
               }

               ++var5;
               ++var4;
            } else if (var1[var5] != this.buf[var4]) {
               ++var4;
            }
         }

         return -1L;
      } else {
         return -1L;
      }
   }

   public long position(Blob var1, long var2) throws SerialException, SQLException {
      this.isValid();
      return this.position(var1.getBytes(1L, (int)var1.length()), var2);
   }

   public int setBytes(long var1, byte[] var3) throws SerialException, SQLException {
      return this.setBytes(var1, var3, 0, var3.length);
   }

   public int setBytes(long var1, byte[] var3, int var4, int var5) throws SerialException, SQLException {
      this.isValid();
      if (var4 >= 0 && var4 <= var3.length) {
         if (var1 >= 1L && var1 <= this.length()) {
            if ((long)var5 > this.origLen) {
               throw new SerialException("Buffer is not sufficient to hold the value");
            } else if (var5 + var4 > var3.length) {
               throw new SerialException("Invalid OffSet. Cannot have combined offset and length that is greater that the Blob buffer");
            } else {
               int var6 = 0;
               --var1;

               while(var6 < var5 || var4 + var6 + 1 < var3.length - var4) {
                  this.buf[(int)var1 + var6] = var3[var4 + var6];
                  ++var6;
               }

               return var6;
            }
         } else {
            throw new SerialException("Invalid position in BLOB object set");
         }
      } else {
         throw new SerialException("Invalid offset in byte array set");
      }
   }

   public OutputStream setBinaryStream(long var1) throws SerialException, SQLException {
      this.isValid();
      if (this.blob != null) {
         return this.blob.setBinaryStream(var1);
      } else {
         throw new SerialException("Unsupported operation. SerialBlob cannot return a writable binary stream, unless instantiated with a Blob object that provides a setBinaryStream() implementation");
      }
   }

   public void truncate(long var1) throws SerialException {
      this.isValid();
      if (var1 > this.len) {
         throw new SerialException("Length more than what can be truncated");
      } else {
         if ((int)var1 == 0) {
            this.buf = new byte[0];
            this.len = var1;
         } else {
            this.len = var1;
            this.buf = this.getBytes(1L, (int)this.len);
         }

      }
   }

   public InputStream getBinaryStream(long var1, long var3) throws SQLException {
      this.isValid();
      if (var1 >= 1L && var1 <= this.length()) {
         if (var3 >= 1L && var3 <= this.len - var1 + 1L) {
            return new ByteArrayInputStream(this.buf, (int)var1 - 1, (int)var3);
         } else {
            throw new SerialException("length is < 1 or pos + length > total number of bytes");
         }
      } else {
         throw new SerialException("Invalid position in BLOB object set");
      }
   }

   public void free() throws SQLException {
      if (this.buf != null) {
         this.buf = null;
         if (this.blob != null) {
            this.blob.free();
         }

         this.blob = null;
      }

   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         if (var1 instanceof SerialBlob) {
            SerialBlob var2 = (SerialBlob)var1;
            if (this.len == var2.len) {
               return Arrays.equals(this.buf, var2.buf);
            }
         }

         return false;
      }
   }

   public int hashCode() {
      return ((31 + Arrays.hashCode(this.buf)) * 31 + (int)this.len) * 31 + (int)this.origLen;
   }

   public Object clone() {
      try {
         SerialBlob var1 = (SerialBlob)super.clone();
         var1.buf = this.buf != null ? Arrays.copyOf(this.buf, (int)this.len) : null;
         var1.blob = null;
         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError();
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField var2 = var1.readFields();
      byte[] var3 = (byte[])((byte[])var2.get("buf", (Object)null));
      if (var3 == null) {
         throw new InvalidObjectException("buf is null and should not be!");
      } else {
         this.buf = (byte[])var3.clone();
         this.len = var2.get("len", 0L);
         if ((long)this.buf.length != this.len) {
            throw new InvalidObjectException("buf is not the expected size");
         } else {
            this.origLen = var2.get("origLen", 0L);
            this.blob = (Blob)var2.get("blob", (Object)null);
         }
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException, ClassNotFoundException {
      ObjectOutputStream.PutField var2 = var1.putFields();
      var2.put("buf", this.buf);
      var2.put("len", this.len);
      var2.put("origLen", this.origLen);
      var2.put("blob", this.blob instanceof Serializable ? this.blob : null);
      var1.writeFields();
   }

   private void isValid() throws SerialException {
      if (this.buf == null) {
         throw new SerialException("Error: You cannot call a method on a SerialBlob instance once free() has been called.");
      }
   }
}
