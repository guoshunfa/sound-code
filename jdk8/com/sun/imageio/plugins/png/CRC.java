package com.sun.imageio.plugins.png;

class CRC {
   private static int[] crcTable = new int[256];
   private int crc = -1;

   public CRC() {
   }

   public void reset() {
      this.crc = -1;
   }

   public void update(byte[] var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         this.crc = crcTable[(this.crc ^ var1[var2 + var4]) & 255] ^ this.crc >>> 8;
      }

   }

   public void update(int var1) {
      this.crc = crcTable[(this.crc ^ var1) & 255] ^ this.crc >>> 8;
   }

   public int getValue() {
      return ~this.crc;
   }

   static {
      for(int var0 = 0; var0 < 256; ++var0) {
         int var1 = var0;

         for(int var2 = 0; var2 < 8; ++var2) {
            if ((var1 & 1) == 1) {
               var1 = -306674912 ^ var1 >>> 1;
            } else {
               var1 >>>= 1;
            }

            crcTable[var0] = var1;
         }
      }

   }
}
