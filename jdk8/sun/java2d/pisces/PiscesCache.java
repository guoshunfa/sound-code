package sun.java2d.pisces;

import java.util.Arrays;

final class PiscesCache {
   final int bboxX0;
   final int bboxY0;
   final int bboxX1;
   final int bboxY1;
   final int[][] rowAARLE;
   private int x0 = Integer.MIN_VALUE;
   private int y0 = Integer.MIN_VALUE;
   private final int[][] touchedTile;
   static final int TILE_SIZE_LG = 5;
   static final int TILE_SIZE = 32;
   private static final int INIT_ROW_SIZE = 8;

   PiscesCache(int var1, int var2, int var3, int var4) {
      assert var4 >= var2 && var3 >= var1;

      this.bboxX0 = var1;
      this.bboxY0 = var2;
      this.bboxX1 = var3 + 1;
      this.bboxY1 = var4 + 1;
      this.rowAARLE = new int[this.bboxY1 - this.bboxY0 + 1][8];
      this.x0 = 0;
      this.y0 = -1;
      int var5 = var4 - var2 + 32 >> 5;
      int var6 = var3 - var1 + 32 >> 5;
      this.touchedTile = new int[var5][var6];
   }

   void addRLERun(int var1, int var2) {
      if (var2 > 0) {
         this.addTupleToRow(this.y0, var1, var2);
         if (var1 != 0) {
            int var3 = this.x0 >> 5;
            int var4 = this.y0 >> 5;
            int var5 = this.x0 + var2 - 1 >> 5;
            if (var5 >= this.touchedTile[var4].length) {
               var5 = this.touchedTile[var4].length - 1;
            }

            int[] var10000;
            int var6;
            if (var3 <= var5) {
               var6 = var3 + 1 << 5;
               if (var6 > this.x0 + var2) {
                  var10000 = this.touchedTile[var4];
                  var10000[var3] += var1 * var2;
               } else {
                  var10000 = this.touchedTile[var4];
                  var10000[var3] += var1 * (var6 - this.x0);
               }

               ++var3;
            }

            while(var3 < var5) {
               var10000 = this.touchedTile[var4];
               var10000[var3] += var1 << 5;
               ++var3;
            }

            if (var3 == var5) {
               var6 = Math.min(this.x0 + var2, var3 + 1 << 5);
               int var7 = var3 << 5;
               var10000 = this.touchedTile[var4];
               var10000[var3] += var1 * (var6 - var7);
            }
         }

         this.x0 += var2;
      }

   }

   void startRow(int var1, int var2) {
      assert var1 - this.bboxY0 > this.y0;

      assert var1 <= this.bboxY1;

      this.y0 = var1 - this.bboxY0;

      assert this.rowAARLE[this.y0][1] == 0;

      this.x0 = var2 - this.bboxX0;

      assert this.x0 >= 0 : "Input must not be to the left of bbox bounds";

      this.rowAARLE[this.y0][0] = var2;
      this.rowAARLE[this.y0][1] = 2;
   }

   int alphaSumInTile(int var1, int var2) {
      var1 -= this.bboxX0;
      var2 -= this.bboxY0;
      return this.touchedTile[var2 >> 5][var1 >> 5];
   }

   int minTouched(int var1) {
      return this.rowAARLE[var1][0];
   }

   int rowLength(int var1) {
      return this.rowAARLE[var1][1];
   }

   private void addTupleToRow(int var1, int var2, int var3) {
      int var4 = this.rowAARLE[var1][1];
      this.rowAARLE[var1] = Helpers.widenArray((int[])this.rowAARLE[var1], var4, 2);
      this.rowAARLE[var1][var4++] = var2;
      this.rowAARLE[var1][var4++] = var3;
      this.rowAARLE[var1][1] = var4;
   }

   public String toString() {
      String var1 = "bbox = [" + this.bboxX0 + ", " + this.bboxY0 + " => " + this.bboxX1 + ", " + this.bboxY1 + "]\n";
      int[][] var2 = this.rowAARLE;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int[] var5 = var2[var4];
         if (var5 != null) {
            var1 = var1 + "minTouchedX=" + var5[0] + "\tRLE Entries: " + Arrays.toString(Arrays.copyOfRange((int[])var5, 2, var5[1])) + "\n";
         } else {
            var1 = var1 + "[]\n";
         }
      }

      return var1;
   }
}
