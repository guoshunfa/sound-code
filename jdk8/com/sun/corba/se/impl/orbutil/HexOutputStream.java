package com.sun.corba.se.impl.orbutil;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;

public class HexOutputStream extends OutputStream {
   private static final char[] hex = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
   private StringWriter writer;

   public HexOutputStream(StringWriter var1) {
      this.writer = var1;
   }

   public synchronized void write(int var1) throws IOException {
      this.writer.write(hex[var1 >> 4 & 15]);
      this.writer.write(hex[var1 >> 0 & 15]);
   }

   public synchronized void write(byte[] var1) throws IOException {
      this.write(var1, 0, var1.length);
   }

   public synchronized void write(byte[] var1, int var2, int var3) throws IOException {
      for(int var4 = 0; var4 < var3; ++var4) {
         this.write(var1[var2 + var4]);
      }

   }
}
