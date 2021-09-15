package javax.sql.rowset.serial;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Arrays;

public class SerialClob implements Clob, Serializable, Cloneable {
   private char[] buf;
   private Clob clob;
   private long len;
   private long origLen;
   static final long serialVersionUID = -1662519690087375313L;

   public SerialClob(char[] var1) throws SerialException, SQLException {
      this.len = (long)var1.length;
      this.buf = new char[(int)this.len];

      for(int var2 = 0; (long)var2 < this.len; ++var2) {
         this.buf[var2] = var1[var2];
      }

      this.origLen = this.len;
      this.clob = null;
   }

   public SerialClob(Clob var1) throws SerialException, SQLException {
      if (var1 == null) {
         throw new SQLException("Cannot instantiate a SerialClob object with a null Clob object");
      } else {
         this.len = var1.length();
         this.clob = var1;
         this.buf = new char[(int)this.len];
         boolean var2 = false;
         int var3 = 0;

         try {
            Reader var4 = var1.getCharacterStream();
            Throwable var5 = null;

            try {
               if (var4 == null) {
                  throw new SQLException("Invalid Clob object. The call to getCharacterStream returned null which cannot be serialized.");
               }

               InputStream var6 = var1.getAsciiStream();
               Throwable var7 = null;

               try {
                  if (var6 == null) {
                     throw new SQLException("Invalid Clob object. The call to getAsciiStream returned null which cannot be serialized.");
                  }
               } catch (Throwable var56) {
                  var7 = var56;
                  throw var56;
               } finally {
                  if (var6 != null) {
                     if (var7 != null) {
                        try {
                           var6.close();
                        } catch (Throwable var53) {
                           var7.addSuppressed(var53);
                        }
                     } else {
                        var6.close();
                     }
                  }

               }

               BufferedReader var62 = new BufferedReader(var4);
               var7 = null;

               try {
                  int var61;
                  try {
                     do {
                        var61 = var62.read(this.buf, var3, (int)(this.len - (long)var3));
                        var3 += var61;
                     } while(var61 > 0);
                  } catch (Throwable var54) {
                     var7 = var54;
                     throw var54;
                  }
               } finally {
                  if (var62 != null) {
                     if (var7 != null) {
                        try {
                           var62.close();
                        } catch (Throwable var52) {
                           var7.addSuppressed(var52);
                        }
                     } else {
                        var62.close();
                     }
                  }

               }
            } catch (Throwable var58) {
               var5 = var58;
               throw var58;
            } finally {
               if (var4 != null) {
                  if (var5 != null) {
                     try {
                        var4.close();
                     } catch (Throwable var51) {
                        var5.addSuppressed(var51);
                     }
                  } else {
                     var4.close();
                  }
               }

            }
         } catch (IOException var60) {
            throw new SerialException("SerialClob: " + var60.getMessage());
         }

         this.origLen = this.len;
      }
   }

   public long length() throws SerialException {
      this.isValid();
      return this.len;
   }

   public Reader getCharacterStream() throws SerialException {
      this.isValid();
      return new CharArrayReader(this.buf);
   }

   public InputStream getAsciiStream() throws SerialException, SQLException {
      this.isValid();
      if (this.clob != null) {
         return this.clob.getAsciiStream();
      } else {
         throw new SerialException("Unsupported operation. SerialClob cannot return a the CLOB value as an ascii stream, unless instantiated with a fully implemented Clob object.");
      }
   }

   public String getSubString(long var1, int var3) throws SerialException {
      this.isValid();
      if (var1 >= 1L && var1 <= this.length()) {
         if (var1 - 1L + (long)var3 > this.length()) {
            throw new SerialException("Invalid position and substring length");
         } else {
            try {
               return new String(this.buf, (int)var1 - 1, var3);
            } catch (StringIndexOutOfBoundsException var5) {
               throw new SerialException("StringIndexOutOfBoundsException: " + var5.getMessage());
            }
         }
      } else {
         throw new SerialException("Invalid position in SerialClob object set");
      }
   }

   public long position(String var1, long var2) throws SerialException, SQLException {
      this.isValid();
      if (var2 >= 1L && var2 <= this.len) {
         char[] var4 = var1.toCharArray();
         int var5 = (int)var2 - 1;
         int var6 = 0;
         long var7 = (long)var4.length;

         while((long)var5 < this.len) {
            if (var4[var6] == this.buf[var5]) {
               if ((long)(var6 + 1) == var7) {
                  return (long)(var5 + 1) - (var7 - 1L);
               }

               ++var6;
               ++var5;
            } else if (var4[var6] != this.buf[var5]) {
               ++var5;
            }
         }

         return -1L;
      } else {
         return -1L;
      }
   }

   public long position(Clob var1, long var2) throws SerialException, SQLException {
      this.isValid();
      return this.position(var1.getSubString(1L, (int)var1.length()), var2);
   }

   public int setString(long var1, String var3) throws SerialException {
      return this.setString(var1, var3, 0, var3.length());
   }

   public int setString(long var1, String var3, int var4, int var5) throws SerialException {
      this.isValid();
      String var6 = var3.substring(var4);
      char[] var7 = var6.toCharArray();
      if (var4 >= 0 && var4 <= var3.length()) {
         if (var1 >= 1L && var1 <= this.length()) {
            if ((long)var5 > this.origLen) {
               throw new SerialException("Buffer is not sufficient to hold the value");
            } else if (var5 + var4 > var3.length()) {
               throw new SerialException("Invalid OffSet. Cannot have combined offset  and length that is greater that the Blob buffer");
            } else {
               int var8 = 0;
               --var1;

               while(var8 < var5 || var4 + var8 + 1 < var3.length() - var4) {
                  this.buf[(int)var1 + var8] = var7[var4 + var8];
                  ++var8;
               }

               return var8;
            }
         } else {
            throw new SerialException("Invalid position in Clob object set");
         }
      } else {
         throw new SerialException("Invalid offset in byte array set");
      }
   }

   public OutputStream setAsciiStream(long var1) throws SerialException, SQLException {
      this.isValid();
      if (this.clob != null) {
         return this.clob.setAsciiStream(var1);
      } else {
         throw new SerialException("Unsupported operation. SerialClob cannot return a writable ascii stream\n unless instantiated with a Clob object that has a setAsciiStream() implementation");
      }
   }

   public Writer setCharacterStream(long var1) throws SerialException, SQLException {
      this.isValid();
      if (this.clob != null) {
         return this.clob.setCharacterStream(var1);
      } else {
         throw new SerialException("Unsupported operation. SerialClob cannot return a writable character stream\n unless instantiated with a Clob object that has a setCharacterStream implementation");
      }
   }

   public void truncate(long var1) throws SerialException {
      this.isValid();
      if (var1 > this.len) {
         throw new SerialException("Length more than what can be truncated");
      } else {
         this.len = var1;
         if (this.len == 0L) {
            this.buf = new char[0];
         } else {
            this.buf = this.getSubString(1L, (int)this.len).toCharArray();
         }

      }
   }

   public Reader getCharacterStream(long var1, long var3) throws SQLException {
      this.isValid();
      if (var1 >= 1L && var1 <= this.len) {
         if (var1 - 1L + var3 > this.len) {
            throw new SerialException("Invalid position and substring length");
         } else if (var3 <= 0L) {
            throw new SerialException("Invalid length specified");
         } else {
            return new CharArrayReader(this.buf, (int)var1, (int)var3);
         }
      } else {
         throw new SerialException("Invalid position in Clob object set");
      }
   }

   public void free() throws SQLException {
      if (this.buf != null) {
         this.buf = null;
         if (this.clob != null) {
            this.clob.free();
         }

         this.clob = null;
      }

   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         if (var1 instanceof SerialClob) {
            SerialClob var2 = (SerialClob)var1;
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
         SerialClob var1 = (SerialClob)super.clone();
         var1.buf = this.buf != null ? Arrays.copyOf(this.buf, (int)this.len) : null;
         var1.clob = null;
         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError();
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField var2 = var1.readFields();
      char[] var3 = (char[])((char[])var2.get("buf", (Object)null));
      if (var3 == null) {
         throw new InvalidObjectException("buf is null and should not be!");
      } else {
         this.buf = (char[])var3.clone();
         this.len = var2.get("len", 0L);
         if ((long)this.buf.length != this.len) {
            throw new InvalidObjectException("buf is not the expected size");
         } else {
            this.origLen = var2.get("origLen", 0L);
            this.clob = (Clob)var2.get("clob", (Object)null);
         }
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException, ClassNotFoundException {
      ObjectOutputStream.PutField var2 = var1.putFields();
      var2.put("buf", this.buf);
      var2.put("len", this.len);
      var2.put("origLen", this.origLen);
      var2.put("clob", this.clob instanceof Serializable ? this.clob : null);
      var1.writeFields();
   }

   private void isValid() throws SerialException {
      if (this.buf == null) {
         throw new SerialException("Error: You cannot call a method on a SerialClob instance once free() has been called.");
      }
   }
}
