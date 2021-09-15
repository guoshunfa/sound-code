package com.sun.java.swing.plaf.gtk;

import java.awt.Color;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.synth.ColorType;

public class GTKColorType extends ColorType {
   public static final ColorType LIGHT = new GTKColorType("Light");
   public static final ColorType DARK = new GTKColorType("Dark");
   public static final ColorType MID = new GTKColorType("Mid");
   public static final ColorType BLACK = new GTKColorType("Black");
   public static final ColorType WHITE = new GTKColorType("White");
   public static final int MAX_COUNT;
   private static final float[] HLS_COLORS = new float[3];
   private static final Object HLS_COLOR_LOCK = new Object();

   private static int hlsToRGB(float var0, float var1, float var2) {
      float var3 = var1 <= 0.5F ? var1 * (1.0F + var2) : var1 + var2 - var1 * var2;
      float var4 = 2.0F * var1 - var3;
      float var5;
      float var6;
      float var7;
      if ((double)var2 == 0.0D) {
         if ((double)var0 == 0.0D) {
            var7 = var1;
            var6 = var1;
            var5 = var1;
         } else {
            var7 = 0.0F;
            var6 = 0.0F;
            var5 = 0.0F;
         }
      } else {
         var5 = hlsValue(var4, var3, var0 + 120.0F);
         var6 = hlsValue(var4, var3, var0);
         var7 = hlsValue(var4, var3, var0 - 120.0F);
      }

      return (int)(var5 * 255.0F) << 16 | (int)((double)var6 * 255.0D) << 8 | (int)(var7 * 255.0F);
   }

   private static float hlsValue(float var0, float var1, float var2) {
      if (var2 > 360.0F) {
         var2 -= 360.0F;
      } else if (var2 < 0.0F) {
         var2 += 360.0F;
      }

      if (var2 < 60.0F) {
         return var0 + (var1 - var0) * var2 / 60.0F;
      } else if (var2 < 180.0F) {
         return var1;
      } else {
         return var2 < 240.0F ? var0 + (var1 - var0) * (240.0F - var2) / 60.0F : var0;
      }
   }

   private static float[] rgbToHLS(int var0, float[] var1) {
      float var2 = (float)((var0 & 16711680) >> 16) / 255.0F;
      float var3 = (float)((var0 & '\uff00') >> 8) / 255.0F;
      float var4 = (float)(var0 & 255) / 255.0F;
      float var5 = Math.max(Math.max(var2, var3), var4);
      float var6 = Math.min(Math.min(var2, var3), var4);
      float var7 = (var5 + var6) / 2.0F;
      float var8 = 0.0F;
      float var9 = 0.0F;
      if (var5 != var6) {
         float var10 = var5 - var6;
         var8 = var7 <= 0.5F ? var10 / (var5 + var6) : var10 / (2.0F - var5 - var6);
         if (var2 == var5) {
            var9 = (var3 - var4) / var10;
         } else if (var3 == var5) {
            var9 = 2.0F + (var4 - var2) / var10;
         } else {
            var9 = 4.0F + (var2 - var3) / var10;
         }

         var9 *= 60.0F;
         if (var9 < 0.0F) {
            var9 += 360.0F;
         }
      }

      if (var1 == null) {
         var1 = new float[3];
      }

      var1[0] = var9;
      var1[1] = var7;
      var1[2] = var8;
      return var1;
   }

   static Color adjustColor(Color var0, float var1, float var2, float var3) {
      float var4;
      float var5;
      float var6;
      synchronized(HLS_COLOR_LOCK) {
         float[] var8 = rgbToHLS(var0.getRGB(), HLS_COLORS);
         var4 = var8[0];
         var5 = var8[1];
         var6 = var8[2];
      }

      var4 = Math.min(360.0F, var1 * var4);
      var5 = Math.min(1.0F, var2 * var5);
      var6 = Math.min(1.0F, var3 * var6);
      return new ColorUIResource(hlsToRGB(var4, var5, var6));
   }

   protected GTKColorType(String var1) {
      super(var1);
   }

   static {
      MAX_COUNT = WHITE.getID() + 1;
   }
}
