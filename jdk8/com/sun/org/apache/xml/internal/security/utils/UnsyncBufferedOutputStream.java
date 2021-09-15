package com.sun.org.apache.xml.internal.security.utils;

import java.io.IOException;
import java.io.OutputStream;

public class UnsyncBufferedOutputStream extends OutputStream {
   static final int size = 8192;
   private int pointer = 0;
   private final OutputStream out;
   private final byte[] buf = new byte[8192];

   public UnsyncBufferedOutputStream(OutputStream var1) {
      this.out = var1;
   }

   public void write(byte[] var1) throws IOException {
      this.write(var1, 0, var1.length);
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      int var4 = this.pointer + var3;
      if (var4 > 8192) {
         this.flushBuffer();
         if (var3 > 8192) {
            this.out.write(var1, var2, var3);
            return;
         }

         var4 = var3;
      }

      System.arraycopy(var1, var2, this.buf, this.pointer, var3);
      this.pointer = var4;
   }

   private void flushBuffer() throws IOException {
      if (this.pointer > 0) {
         this.out.write(this.buf, 0, this.pointer);
      }

      this.pointer = 0;
   }

   public void write(int var1) throws IOException {
      if (this.pointer >= 8192) {
         this.flushBuffer();
      }

      this.buf[this.pointer++] = (byte)var1;
   }

   public void flush() throws IOException {
      this.flushBuffer();
      this.out.flush();
   }

   public void close() throws IOException {
      this.flush();
      this.out.close();
   }
}
