package sun.security.jgss;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class GSSToken {
   public static final void writeLittleEndian(int var0, byte[] var1) {
      writeLittleEndian(var0, var1, 0);
   }

   public static final void writeLittleEndian(int var0, byte[] var1, int var2) {
      var1[var2++] = (byte)var0;
      var1[var2++] = (byte)(var0 >>> 8);
      var1[var2++] = (byte)(var0 >>> 16);
      var1[var2++] = (byte)(var0 >>> 24);
   }

   public static final void writeBigEndian(int var0, byte[] var1) {
      writeBigEndian(var0, var1, 0);
   }

   public static final void writeBigEndian(int var0, byte[] var1, int var2) {
      var1[var2++] = (byte)(var0 >>> 24);
      var1[var2++] = (byte)(var0 >>> 16);
      var1[var2++] = (byte)(var0 >>> 8);
      var1[var2++] = (byte)var0;
   }

   public static final int readLittleEndian(byte[] var0, int var1, int var2) {
      int var3 = 0;

      for(int var4 = 0; var2 > 0; --var2) {
         var3 += (var0[var1] & 255) << var4;
         var4 += 8;
         ++var1;
      }

      return var3;
   }

   public static final int readBigEndian(byte[] var0, int var1, int var2) {
      int var3 = 0;

      for(int var4 = (var2 - 1) * 8; var2 > 0; --var2) {
         var3 += (var0[var1] & 255) << var4;
         var4 -= 8;
         ++var1;
      }

      return var3;
   }

   public static final void writeInt(int var0, OutputStream var1) throws IOException {
      var1.write(var0 >>> 8);
      var1.write(var0);
   }

   public static final int writeInt(int var0, byte[] var1, int var2) {
      var1[var2++] = (byte)(var0 >>> 8);
      var1[var2++] = (byte)var0;
      return var2;
   }

   public static final int readInt(InputStream var0) throws IOException {
      return (255 & var0.read()) << 8 | 255 & var0.read();
   }

   public static final int readInt(byte[] var0, int var1) {
      return (255 & var0[var1]) << 8 | 255 & var0[var1 + 1];
   }

   public static final void readFully(InputStream var0, byte[] var1) throws IOException {
      readFully(var0, var1, 0, var1.length);
   }

   public static final void readFully(InputStream var0, byte[] var1, int var2, int var3) throws IOException {
      while(var3 > 0) {
         int var4 = var0.read(var1, var2, var3);
         if (var4 == -1) {
            throw new EOFException("Cannot read all " + var3 + " bytes needed to form this token!");
         }

         var2 += var4;
         var3 -= var4;
      }

   }

   public static final void debug(String var0) {
      System.err.print(var0);
   }

   public static final String getHexBytes(byte[] var0) {
      return getHexBytes(var0, 0, var0.length);
   }

   public static final String getHexBytes(byte[] var0, int var1) {
      return getHexBytes(var0, 0, var1);
   }

   public static final String getHexBytes(byte[] var0, int var1, int var2) {
      StringBuffer var3 = new StringBuffer();

      for(int var4 = var1; var4 < var1 + var2; ++var4) {
         int var5 = var0[var4] >> 4 & 15;
         int var6 = var0[var4] & 15;
         var3.append(Integer.toHexString(var5));
         var3.append(Integer.toHexString(var6));
         var3.append(' ');
      }

      return var3.toString();
   }
}
