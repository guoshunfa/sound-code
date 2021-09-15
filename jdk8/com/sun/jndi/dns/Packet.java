package com.sun.jndi.dns;

class Packet {
   byte[] buf;

   Packet(int var1) {
      this.buf = new byte[var1];
   }

   Packet(byte[] var1, int var2) {
      this.buf = new byte[var2];
      System.arraycopy(var1, 0, this.buf, 0, var2);
   }

   void putInt(int var1, int var2) {
      this.buf[var2 + 0] = (byte)(var1 >> 24);
      this.buf[var2 + 1] = (byte)(var1 >> 16);
      this.buf[var2 + 2] = (byte)(var1 >> 8);
      this.buf[var2 + 3] = (byte)var1;
   }

   void putShort(int var1, int var2) {
      this.buf[var2 + 0] = (byte)(var1 >> 8);
      this.buf[var2 + 1] = (byte)var1;
   }

   void putByte(int var1, int var2) {
      this.buf[var2] = (byte)var1;
   }

   void putBytes(byte[] var1, int var2, int var3, int var4) {
      System.arraycopy(var1, var2, this.buf, var3, var4);
   }

   int length() {
      return this.buf.length;
   }

   byte[] getData() {
      return this.buf;
   }
}
