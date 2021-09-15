package com.sun.java.util.jar.pack;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;

final class Histogram {
   protected final int[][] matrix;
   protected final int totalWeight;
   protected final int[] values;
   protected final int[] counts;
   private static final long LOW32 = 4294967295L;
   private static double log2 = Math.log(2.0D);
   private final Histogram.BitMetric bitMetric;

   public Histogram(int[] var1) {
      this.bitMetric = new Histogram.BitMetric() {
         public double getBitLength(int var1) {
            return Histogram.this.getBitLength(var1);
         }
      };
      long[] var2 = computeHistogram2Col(maybeSort(var1));
      int[][] var3 = makeTable(var2);
      this.values = var3[0];
      this.counts = var3[1];
      this.matrix = makeMatrix(var2);
      this.totalWeight = var1.length;

      assert this.assertWellFormed(var1);

   }

   public Histogram(int[] var1, int var2, int var3) {
      this(sortedSlice(var1, var2, var3));
   }

   public Histogram(int[][] var1) {
      this.bitMetric = new Histogram.BitMetric() {
         public double getBitLength(int var1) {
            return Histogram.this.getBitLength(var1);
         }
      };
      var1 = this.normalizeMatrix(var1);
      this.matrix = var1;
      int var2 = 0;
      int var3 = 0;

      int var5;
      for(int var4 = 0; var4 < var1.length; ++var4) {
         var5 = var1[var4].length - 1;
         var2 += var5;
         var3 += var1[var4][0] * var5;
      }

      this.totalWeight = var3;
      long[] var8 = new long[var2];
      var5 = 0;

      for(int var6 = 0; var6 < var1.length; ++var6) {
         for(int var7 = 1; var7 < var1[var6].length; ++var7) {
            var8[var5++] = (long)var1[var6][var7] << 32 | 4294967295L & (long)var1[var6][0];
         }
      }

      assert var5 == var8.length;

      Arrays.sort(var8);
      int[][] var9 = makeTable(var8);
      this.values = var9[1];
      this.counts = var9[0];

      assert this.assertWellFormed((int[])null);

   }

   public int[][] getMatrix() {
      return this.matrix;
   }

   public int getRowCount() {
      return this.matrix.length;
   }

   public int getRowFrequency(int var1) {
      return this.matrix[var1][0];
   }

   public int getRowLength(int var1) {
      return this.matrix[var1].length - 1;
   }

   public int getRowValue(int var1, int var2) {
      return this.matrix[var1][var2 + 1];
   }

   public int getRowWeight(int var1) {
      return this.getRowFrequency(var1) * this.getRowLength(var1);
   }

   public int getTotalWeight() {
      return this.totalWeight;
   }

   public int getTotalLength() {
      return this.values.length;
   }

   public int[] getAllValues() {
      return this.values;
   }

   public int[] getAllFrequencies() {
      return this.counts;
   }

   public int getFrequency(int var1) {
      int var2 = Arrays.binarySearch(this.values, var1);
      if (var2 < 0) {
         return 0;
      } else {
         assert this.values[var2] == var1;

         return this.counts[var2];
      }
   }

   public double getBitLength(int var1) {
      double var2 = (double)this.getFrequency(var1) / (double)this.getTotalWeight();
      return -Math.log(var2) / log2;
   }

   public double getRowBitLength(int var1) {
      double var2 = (double)this.getRowFrequency(var1) / (double)this.getTotalWeight();
      return -Math.log(var2) / log2;
   }

   public Histogram.BitMetric getBitMetric() {
      return this.bitMetric;
   }

   public double getBitLength() {
      double var1 = 0.0D;

      for(int var3 = 0; var3 < this.matrix.length; ++var3) {
         var1 += this.getRowBitLength(var3) * (double)this.getRowWeight(var3);
      }

      assert 0.1D > Math.abs(var1 - this.getBitLength(this.bitMetric));

      return var1;
   }

   public double getBitLength(Histogram.BitMetric var1) {
      double var2 = 0.0D;

      for(int var4 = 0; var4 < this.matrix.length; ++var4) {
         for(int var5 = 1; var5 < this.matrix[var4].length; ++var5) {
            var2 += (double)this.matrix[var4][0] * var1.getBitLength(this.matrix[var4][var5]);
         }
      }

      return var2;
   }

   private static double round(double var0, double var2) {
      return (double)Math.round(var0 * var2) / var2;
   }

   public int[][] normalizeMatrix(int[][] var1) {
      long[] var2 = new long[var1.length];

      int var4;
      for(int var3 = 0; var3 < var1.length; ++var3) {
         if (var1[var3].length > 1) {
            var4 = var1[var3][0];
            if (var4 > 0) {
               var2[var3] = (long)var4 << 32 | (long)var3;
            }
         }
      }

      Arrays.sort(var2);
      int[][] var14 = new int[var1.length][];
      var4 = -1;
      int var5 = 0;
      int var6 = 0;
      int var7 = 0;

      while(true) {
         label116: {
            int[] var8;
            if (var7 < var1.length) {
               long var9 = var2[var2.length - var7 - 1];
               if (var9 == 0L) {
                  break label116;
               }

               var8 = var1[(int)var9];

               assert var9 >>> 32 == (long)var8[0];
            } else {
               var8 = new int[]{-1};
            }

            if (var8[0] != var4 && var6 > var5) {
               int var15 = 0;

               for(int var10 = var5; var10 < var6; ++var10) {
                  int[] var11 = var14[var10];

                  assert var11[0] == var4;

                  var15 += var11.length - 1;
               }

               int[] var16 = new int[1 + var15];
               var16[0] = var4;
               int var17 = 1;

               int var12;
               int[] var13;
               for(var12 = var5; var12 < var6; ++var12) {
                  var13 = var14[var12];

                  assert var13[0] == var4;

                  System.arraycopy(var13, 1, var16, var17, var13.length - 1);
                  var17 += var13.length - 1;
               }

               if (!isSorted(var16, 1, true)) {
                  Arrays.sort((int[])var16, 1, var16.length);
                  var12 = 2;

                  for(int var18 = 2; var18 < var16.length; ++var18) {
                     if (var16[var18] != var16[var18 - 1]) {
                        var16[var12++] = var16[var18];
                     }
                  }

                  if (var12 < var16.length) {
                     var13 = new int[var12];
                     System.arraycopy(var16, 0, var13, 0, var12);
                     var16 = var13;
                  }
               }

               var14[var5++] = var16;
               var6 = var5;
            }

            if (var7 == var1.length) {
               assert var5 == var6;

               var1 = var14;
               if (var5 < var14.length) {
                  var14 = new int[var5][];
                  System.arraycopy(var1, 0, var14, 0, var5);
                  var1 = var14;
               }

               return var1;
            }

            var4 = var8[0];
            var14[var6++] = var8;
         }

         ++var7;
      }
   }

   public String[] getRowTitles(String var1) {
      int var2 = this.getTotalLength();
      int var3 = this.getTotalWeight();
      String[] var4 = new String[this.matrix.length];
      int var5 = 0;
      int var6 = 0;

      for(int var7 = 0; var7 < this.matrix.length; ++var7) {
         int var8 = this.getRowFrequency(var7);
         int var9 = this.getRowLength(var7);
         int var10 = this.getRowWeight(var7);
         var5 += var10;
         var6 += var9;
         long var11 = ((long)var5 * 100L + (long)(var3 / 2)) / (long)var3;
         long var13 = ((long)var6 * 100L + (long)(var2 / 2)) / (long)var2;
         double var15 = this.getRowBitLength(var7);

         assert 0.1D > Math.abs(var15 - this.getBitLength(this.matrix[var7][1]));

         var4[var7] = var1 + "[" + var7 + "] len=" + round(var15, 10.0D) + " (" + var8 + "*[" + var9 + "]) (" + var5 + ":" + var11 + "%) [" + var6 + ":" + var13 + "%]";
      }

      return var4;
   }

   public void print(PrintStream var1) {
      this.print("hist", var1);
   }

   public void print(String var1, PrintStream var2) {
      this.print(var1, this.getRowTitles(var1), var2);
   }

   public void print(String var1, String[] var2, PrintStream var3) {
      int var4 = this.getTotalLength();
      int var5 = this.getTotalWeight();
      double var6 = this.getBitLength();
      double var8 = var6 / (double)var5;
      double var10 = (double)var5 / (double)var4;
      String var12 = var1 + " len=" + round(var6, 10.0D) + " avgLen=" + round(var8, 10.0D) + " weight(" + var5 + ") unique[" + var4 + "] avgWeight(" + round(var10, 100.0D) + ")";
      if (var2 == null) {
         var3.println(var12);
      } else {
         var3.println(var12 + " {");
         StringBuffer var13 = new StringBuffer();

         for(int var14 = 0; var14 < this.matrix.length; ++var14) {
            var13.setLength(0);
            var13.append("  ").append(var2[var14]).append(" {");

            for(int var15 = 1; var15 < this.matrix[var14].length; ++var15) {
               var13.append(" ").append(this.matrix[var14][var15]);
            }

            var13.append(" }");
            var3.println((Object)var13);
         }

         var3.println("}");
      }

   }

   private static int[][] makeMatrix(long[] var0) {
      Arrays.sort(var0);
      int[] var1 = new int[var0.length];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2] = (int)(var0[var2] >>> 32);
      }

      long[] var15 = computeHistogram2Col(var1);
      int[][] var3 = new int[var15.length][];
      int var4 = 0;
      int var5 = 0;
      int var6 = var3.length;

      while(true) {
         --var6;
         if (var6 < 0) {
            assert var4 == var0.length;

            return var3;
         }

         long var7 = var15[var5++];
         int var9 = (int)var7;
         int var10 = (int)(var7 >>> 32);
         int[] var11 = new int[1 + var10];
         var11[0] = var9;

         for(int var12 = 0; var12 < var10; ++var12) {
            long var13 = var0[var4++];

            assert var13 >>> 32 == (long)var9;

            var11[1 + var12] = (int)var13;
         }

         var3[var6] = var11;
      }
   }

   private static int[][] makeTable(long[] var0) {
      int[][] var1 = new int[2][var0.length];

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1[0][var2] = (int)var0[var2];
         var1[1][var2] = (int)(var0[var2] >>> 32);
      }

      return var1;
   }

   private static long[] computeHistogram2Col(int[] var0) {
      switch(var0.length) {
      case 0:
         return new long[0];
      case 1:
         return new long[]{4294967296L | 4294967295L & (long)var0[0]};
      default:
         long[] var1 = null;
         boolean var2 = true;

         while(true) {
            int var3 = -1;
            int var4 = ~var0[0];
            int var5 = 0;

            for(int var6 = 0; var6 <= var0.length; ++var6) {
               int var7;
               if (var6 < var0.length) {
                  var7 = var0[var6];
               } else {
                  var7 = ~var4;
               }

               if (var7 == var4) {
                  ++var5;
               } else {
                  if (!var2 && var5 != 0) {
                     var1[var3] = (long)var5 << 32 | 4294967295L & (long)var4;
                  }

                  var4 = var7;
                  var5 = 1;
                  ++var3;
               }
            }

            if (!var2) {
               return var1;
            }

            var1 = new long[var3];
            var2 = false;
         }
      }
   }

   private static int[][] regroupHistogram(int[][] var0, int[] var1) {
      long var2 = 0L;

      for(int var4 = 0; var4 < var0.length; ++var4) {
         var2 += (long)(var0[var4].length - 1);
      }

      long var17 = 0L;

      int var6;
      for(var6 = 0; var6 < var1.length; ++var6) {
         var17 += (long)var1[var6];
      }

      int var9;
      if (var17 > var2) {
         var6 = var1.length;
         long var7 = var2;

         for(var9 = 0; var9 < var1.length; ++var9) {
            if (var7 < (long)var1[var9]) {
               int[] var10 = new int[var9 + 1];
               System.arraycopy(var1, 0, var10, 0, var9 + 1);
               var1 = var10;
               var10[var9] = (int)var7;
               var7 = 0L;
               break;
            }

            var7 -= (long)var1[var9];
         }
      } else {
         long var18 = var2 - var17;
         int[] var8 = new int[var1.length + 1];
         System.arraycopy(var1, 0, var8, 0, var1.length);
         var8[var1.length] = (int)var18;
         var1 = var8;
      }

      int[][] var19 = new int[var1.length][];
      int var20 = 0;
      byte var21 = 1;
      var9 = var0[var20].length;

      for(int var22 = 0; var22 < var1.length; ++var22) {
         int var11 = var1[var22];
         int[] var12 = new int[1 + var11];
         long var13 = 0L;
         var19[var22] = var12;

         int var16;
         for(int var15 = 1; var15 < var12.length; var15 += var16) {
            for(var16 = var12.length - var15; var21 == var9; var9 = var0[var20].length) {
               var21 = 1;
               ++var20;
            }

            if (var16 > var9 - var21) {
               var16 = var9 - var21;
            }

            var13 += (long)var0[var20][0] * (long)var16;
            System.arraycopy(var0[var20], var9 - var16, var12, var15, var16);
            var9 -= var16;
         }

         Arrays.sort((int[])var12, 1, var12.length);
         var12[0] = (int)((var13 + (long)(var11 / 2)) / (long)var11);
      }

      assert var21 == var9;

      assert var20 == var0.length - 1;

      return var19;
   }

   public static Histogram makeByteHistogram(InputStream var0) throws IOException {
      byte[] var1 = new byte[4096];
      int[] var2 = new int[256];

      int var3;
      int var4;
      while((var3 = var0.read(var1)) > 0) {
         for(var4 = 0; var4 < var3; ++var4) {
            ++var2[var1[var4] & 255];
         }
      }

      int[][] var5 = new int[256][2];

      for(var4 = 0; var4 < var2.length; var5[var4][1] = var4++) {
         var5[var4][0] = var2[var4];
      }

      return new Histogram(var5);
   }

   private static int[] sortedSlice(int[] var0, int var1, int var2) {
      if (var1 == 0 && var2 == var0.length && isSorted(var0, 0, false)) {
         return var0;
      } else {
         int[] var3 = new int[var2 - var1];
         System.arraycopy(var0, var1, var3, 0, var3.length);
         Arrays.sort(var3);
         return var3;
      }
   }

   private static boolean isSorted(int[] var0, int var1, boolean var2) {
      int var3 = var1 + 1;

      while(true) {
         if (var3 >= var0.length) {
            return true;
         }

         if (var2) {
            if (var0[var3 - 1] >= var0[var3]) {
               break;
            }
         } else if (var0[var3 - 1] > var0[var3]) {
            break;
         }

         ++var3;
      }

      return false;
   }

   private static int[] maybeSort(int[] var0) {
      if (!isSorted(var0, 0, false)) {
         var0 = (int[])var0.clone();
         Arrays.sort(var0);
      }

      return var0;
   }

   private boolean assertWellFormed(int[] var1) {
      return true;
   }

   public interface BitMetric {
      double getBitLength(int var1);
   }
}
