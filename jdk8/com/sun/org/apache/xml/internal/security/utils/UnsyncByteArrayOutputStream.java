package com.sun.org.apache.xml.internal.security.utils;

import java.io.OutputStream;

public class UnsyncByteArrayOutputStream extends OutputStream {
   private static final int INITIAL_SIZE = 8192;
   private byte[] buf = new byte[8192];
   private int size = 8192;
   private int pos = 0;

   public void write(byte[] var1) {
      if (Integer.MAX_VALUE - this.pos < var1.length) {
         throw new OutOfMemoryError();
      } else {
         int var2 = this.pos + var1.length;
         if (var2 > this.size) {
            this.expandSize(var2);
         }

         System.arraycopy(var1, 0, this.buf, this.pos, var1.length);
         this.pos = var2;
      }
   }

   public void write(byte[] var1, int var2, int var3) {
      if (Integer.MAX_VALUE - this.pos < var3) {
         throw new OutOfMemoryError();
      } else {
         int var4 = this.pos + var3;
         if (var4 > this.size) {
            this.expandSize(var4);
         }

         System.arraycopy(var1, var2, this.buf, this.pos, var3);
         this.pos = var4;
      }
   }

   public void write(int var1) {
      if (Integer.MAX_VALUE - this.pos == 0) {
         throw new OutOfMemoryError();
      } else {
         int var2 = this.pos + 1;
         if (var2 > this.size) {
            this.expandSize(var2);
         }

         this.buf[this.pos++] = (byte)var1;
      }
   }

   public byte[] toByteArray() {
      byte[] var1 = new byte[this.pos];
      System.arraycopy(this.buf, 0, var1, 0, this.pos);
      return var1;
   }

   public void reset() {
      this.pos = 0;
   }

   private void expandSize(int var1) {
      int var2 = this.size;

      while(var1 > var2) {
         var2 <<= 1;
         if (var2 < 0) {
            var2 = Integer.MAX_VALUE;
         }
      }

      byte[] var3 = new byte[var2];
      System.arraycopy(this.buf, 0, var3, 0, this.pos);
      this.buf = var3;
      this.size = var2;
   }
}
