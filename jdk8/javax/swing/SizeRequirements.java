package javax.swing;

import java.io.Serializable;

public class SizeRequirements implements Serializable {
   public int minimum;
   public int preferred;
   public int maximum;
   public float alignment;

   public SizeRequirements() {
      this.minimum = 0;
      this.preferred = 0;
      this.maximum = 0;
      this.alignment = 0.5F;
   }

   public SizeRequirements(int var1, int var2, int var3, float var4) {
      this.minimum = var1;
      this.preferred = var2;
      this.maximum = var3;
      this.alignment = var4 > 1.0F ? 1.0F : (var4 < 0.0F ? 0.0F : var4);
   }

   public String toString() {
      return "[" + this.minimum + "," + this.preferred + "," + this.maximum + "]@" + this.alignment;
   }

   public static SizeRequirements getTiledSizeRequirements(SizeRequirements[] var0) {
      SizeRequirements var1 = new SizeRequirements();

      for(int var2 = 0; var2 < var0.length; ++var2) {
         SizeRequirements var3 = var0[var2];
         var1.minimum = (int)Math.min((long)var1.minimum + (long)var3.minimum, 2147483647L);
         var1.preferred = (int)Math.min((long)var1.preferred + (long)var3.preferred, 2147483647L);
         var1.maximum = (int)Math.min((long)var1.maximum + (long)var3.maximum, 2147483647L);
      }

      return var1;
   }

   public static SizeRequirements getAlignedSizeRequirements(SizeRequirements[] var0) {
      SizeRequirements var1 = new SizeRequirements();
      SizeRequirements var2 = new SizeRequirements();

      int var3;
      int var5;
      for(var3 = 0; var3 < var0.length; ++var3) {
         SizeRequirements var4 = var0[var3];
         var5 = (int)(var4.alignment * (float)var4.minimum);
         int var6 = var4.minimum - var5;
         var1.minimum = Math.max(var5, var1.minimum);
         var2.minimum = Math.max(var6, var2.minimum);
         var5 = (int)(var4.alignment * (float)var4.preferred);
         var6 = var4.preferred - var5;
         var1.preferred = Math.max(var5, var1.preferred);
         var2.preferred = Math.max(var6, var2.preferred);
         var5 = (int)(var4.alignment * (float)var4.maximum);
         var6 = var4.maximum - var5;
         var1.maximum = Math.max(var5, var1.maximum);
         var2.maximum = Math.max(var6, var2.maximum);
      }

      var3 = (int)Math.min((long)var1.minimum + (long)var2.minimum, 2147483647L);
      int var7 = (int)Math.min((long)var1.preferred + (long)var2.preferred, 2147483647L);
      var5 = (int)Math.min((long)var1.maximum + (long)var2.maximum, 2147483647L);
      float var8 = 0.0F;
      if (var3 > 0) {
         var8 = (float)var1.minimum / (float)var3;
         var8 = var8 > 1.0F ? 1.0F : (var8 < 0.0F ? 0.0F : var8);
      }

      return new SizeRequirements(var3, var7, var5, var8);
   }

   public static void calculateTiledPositions(int var0, SizeRequirements var1, SizeRequirements[] var2, int[] var3, int[] var4) {
      calculateTiledPositions(var0, var1, var2, var3, var4, true);
   }

   public static void calculateTiledPositions(int var0, SizeRequirements var1, SizeRequirements[] var2, int[] var3, int[] var4, boolean var5) {
      long var6 = 0L;
      long var8 = 0L;
      long var10 = 0L;

      for(int var12 = 0; var12 < var2.length; ++var12) {
         var6 += (long)var2[var12].minimum;
         var8 += (long)var2[var12].preferred;
         var10 += (long)var2[var12].maximum;
      }

      if ((long)var0 >= var8) {
         expandedTile(var0, var6, var8, var10, var2, var3, var4, var5);
      } else {
         compressedTile(var0, var6, var8, var10, var2, var3, var4, var5);
      }

   }

   private static void compressedTile(int var0, long var1, long var3, long var5, SizeRequirements[] var7, int[] var8, int[] var9, boolean var10) {
      float var11 = (float)Math.min(var3 - (long)var0, var3 - var1);
      float var12 = var3 - var1 == 0L ? 0.0F : var11 / (float)(var3 - var1);
      int var13;
      int var14;
      SizeRequirements var15;
      float var16;
      if (var10) {
         var13 = 0;

         for(var14 = 0; var14 < var9.length; ++var14) {
            var8[var14] = var13;
            var15 = var7[var14];
            var16 = var12 * (float)(var15.preferred - var15.minimum);
            var9[var14] = (int)((float)var15.preferred - var16);
            var13 = (int)Math.min((long)var13 + (long)var9[var14], 2147483647L);
         }
      } else {
         var13 = var0;

         for(var14 = 0; var14 < var9.length; ++var14) {
            var15 = var7[var14];
            var16 = var12 * (float)(var15.preferred - var15.minimum);
            var9[var14] = (int)((float)var15.preferred - var16);
            var8[var14] = var13 - var9[var14];
            var13 = (int)Math.max((long)var13 - (long)var9[var14], 0L);
         }
      }

   }

   private static void expandedTile(int var0, long var1, long var3, long var5, SizeRequirements[] var7, int[] var8, int[] var9, boolean var10) {
      float var11 = (float)Math.min((long)var0 - var3, var5 - var3);
      float var12 = var5 - var3 == 0L ? 0.0F : var11 / (float)(var5 - var3);
      int var13;
      int var14;
      SizeRequirements var15;
      int var16;
      if (var10) {
         var13 = 0;

         for(var14 = 0; var14 < var9.length; ++var14) {
            var8[var14] = var13;
            var15 = var7[var14];
            var16 = (int)(var12 * (float)(var15.maximum - var15.preferred));
            var9[var14] = (int)Math.min((long)var15.preferred + (long)var16, 2147483647L);
            var13 = (int)Math.min((long)var13 + (long)var9[var14], 2147483647L);
         }
      } else {
         var13 = var0;

         for(var14 = 0; var14 < var9.length; ++var14) {
            var15 = var7[var14];
            var16 = (int)(var12 * (float)(var15.maximum - var15.preferred));
            var9[var14] = (int)Math.min((long)var15.preferred + (long)var16, 2147483647L);
            var8[var14] = var13 - var9[var14];
            var13 = (int)Math.max((long)var13 - (long)var9[var14], 0L);
         }
      }

   }

   public static void calculateAlignedPositions(int var0, SizeRequirements var1, SizeRequirements[] var2, int[] var3, int[] var4) {
      calculateAlignedPositions(var0, var1, var2, var3, var4, true);
   }

   public static void calculateAlignedPositions(int var0, SizeRequirements var1, SizeRequirements[] var2, int[] var3, int[] var4, boolean var5) {
      float var6 = var5 ? var1.alignment : 1.0F - var1.alignment;
      int var7 = (int)((float)var0 * var6);
      int var8 = var0 - var7;

      for(int var9 = 0; var9 < var2.length; ++var9) {
         SizeRequirements var10 = var2[var9];
         float var11 = var5 ? var10.alignment : 1.0F - var10.alignment;
         int var12 = (int)((float)var10.maximum * var11);
         int var13 = var10.maximum - var12;
         int var14 = Math.min(var7, var12);
         int var15 = Math.min(var8, var13);
         var3[var9] = var7 - var14;
         var4[var9] = (int)Math.min((long)var14 + (long)var15, 2147483647L);
      }

   }

   public static int[] adjustSizes(int var0, SizeRequirements[] var1) {
      return new int[0];
   }
}
