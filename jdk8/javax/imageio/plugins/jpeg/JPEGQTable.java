package javax.imageio.plugins.jpeg;

import java.util.Arrays;

public class JPEGQTable {
   private static final int[] k1 = new int[]{16, 11, 10, 16, 24, 40, 51, 61, 12, 12, 14, 19, 26, 58, 60, 55, 14, 13, 16, 24, 40, 57, 69, 56, 14, 17, 22, 29, 51, 87, 80, 62, 18, 22, 37, 56, 68, 109, 103, 77, 24, 35, 55, 64, 81, 104, 113, 92, 49, 64, 78, 87, 103, 121, 120, 101, 72, 92, 95, 98, 112, 100, 103, 99};
   private static final int[] k1div2 = new int[]{8, 6, 5, 8, 12, 20, 26, 31, 6, 6, 7, 10, 13, 29, 30, 28, 7, 7, 8, 12, 20, 29, 35, 28, 7, 9, 11, 15, 26, 44, 40, 31, 9, 11, 19, 28, 34, 55, 52, 39, 12, 18, 28, 32, 41, 52, 57, 46, 25, 32, 39, 44, 52, 61, 60, 51, 36, 46, 48, 49, 56, 50, 52, 50};
   private static final int[] k2 = new int[]{17, 18, 24, 47, 99, 99, 99, 99, 18, 21, 26, 66, 99, 99, 99, 99, 24, 26, 56, 99, 99, 99, 99, 99, 47, 66, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99};
   private static final int[] k2div2 = new int[]{9, 9, 12, 24, 50, 50, 50, 50, 9, 11, 13, 33, 50, 50, 50, 50, 12, 13, 28, 50, 50, 50, 50, 50, 24, 33, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50};
   public static final JPEGQTable K1Luminance;
   public static final JPEGQTable K1Div2Luminance;
   public static final JPEGQTable K2Chrominance;
   public static final JPEGQTable K2Div2Chrominance;
   private int[] qTable;

   private JPEGQTable(int[] var1, boolean var2) {
      this.qTable = var2 ? Arrays.copyOf(var1, var1.length) : var1;
   }

   public JPEGQTable(int[] var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("table must not be null.");
      } else if (var1.length != 64) {
         throw new IllegalArgumentException("table.length != 64");
      } else {
         this.qTable = Arrays.copyOf(var1, var1.length);
      }
   }

   public int[] getTable() {
      return Arrays.copyOf(this.qTable, this.qTable.length);
   }

   public JPEGQTable getScaledInstance(float var1, boolean var2) {
      int var3 = var2 ? 255 : 32767;
      int[] var4 = new int[this.qTable.length];

      for(int var5 = 0; var5 < this.qTable.length; ++var5) {
         int var6 = (int)((float)this.qTable[var5] * var1 + 0.5F);
         if (var6 < 1) {
            var6 = 1;
         }

         if (var6 > var3) {
            var6 = var3;
         }

         var4[var5] = var6;
      }

      return new JPEGQTable(var4);
   }

   public String toString() {
      String var1 = System.getProperty("line.separator", "\n");
      StringBuilder var2 = new StringBuilder("JPEGQTable:" + var1);

      for(int var3 = 0; var3 < this.qTable.length; ++var3) {
         if (var3 % 8 == 0) {
            var2.append('\t');
         }

         var2.append(this.qTable[var3]);
         var2.append(var3 % 8 == 7 ? var1 : ' ');
      }

      return var2.toString();
   }

   static {
      K1Luminance = new JPEGQTable(k1, false);
      K1Div2Luminance = new JPEGQTable(k1div2, false);
      K2Chrominance = new JPEGQTable(k2, false);
      K2Div2Chrominance = new JPEGQTable(k2div2, false);
   }
}
