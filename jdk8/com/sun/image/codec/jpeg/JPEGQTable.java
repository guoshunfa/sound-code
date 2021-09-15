package com.sun.image.codec.jpeg;

public class JPEGQTable {
   private int[] quantval;
   private static final byte QTABLESIZE = 64;
   public static final JPEGQTable StdLuminance = new JPEGQTable();
   public static final JPEGQTable StdChrominance;

   private JPEGQTable() {
      this.quantval = new int[64];
   }

   public JPEGQTable(int[] var1) {
      if (var1.length != 64) {
         throw new IllegalArgumentException("Quantization table is the wrong size.");
      } else {
         this.quantval = new int[64];
         System.arraycopy(var1, 0, this.quantval, 0, 64);
      }
   }

   public int[] getTable() {
      int[] var1 = new int[64];
      System.arraycopy(this.quantval, 0, var1, 0, 64);
      return var1;
   }

   public JPEGQTable getScaledInstance(float var1, boolean var2) {
      long var3 = var2 ? 255L : 32767L;
      int[] var5 = new int[64];

      for(int var6 = 0; var6 < 64; ++var6) {
         long var7 = (long)((double)((float)this.quantval[var6] * var1) + 0.5D);
         if (var7 <= 0L) {
            var7 = 1L;
         }

         if (var7 > var3) {
            var7 = var3;
         }

         var5[var6] = (int)var7;
      }

      return new JPEGQTable(var5);
   }

   static {
      int[] var0 = new int[]{16, 11, 12, 14, 12, 10, 16, 14, 13, 14, 18, 17, 16, 19, 24, 40, 26, 24, 22, 22, 24, 49, 35, 37, 29, 40, 58, 51, 61, 60, 57, 51, 56, 55, 64, 72, 92, 78, 64, 68, 87, 69, 55, 56, 80, 109, 81, 87, 95, 98, 103, 104, 103, 62, 77, 113, 121, 112, 100, 120, 92, 101, 103, 99};
      StdLuminance.quantval = var0;
      StdChrominance = new JPEGQTable();
      var0 = new int[]{17, 18, 18, 24, 21, 24, 47, 26, 26, 47, 99, 66, 56, 66, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99};
      StdChrominance.quantval = var0;
   }
}
