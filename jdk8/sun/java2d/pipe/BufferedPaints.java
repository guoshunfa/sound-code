package sun.java2d.pipe;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import sun.awt.image.PixelConverter;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;

public class BufferedPaints {
   public static final int MULTI_MAX_FRACTIONS = 12;

   static void setPaint(RenderQueue var0, SunGraphics2D var1, Paint var2, int var3) {
      if (var1.paintState <= 1) {
         setColor(var0, var1.pixel);
      } else {
         boolean var4 = (var3 & 2) != 0;
         switch(var1.paintState) {
         case 2:
            setGradientPaint(var0, var1, (GradientPaint)var2, var4);
            break;
         case 3:
            setLinearGradientPaint(var0, var1, (LinearGradientPaint)var2, var4);
            break;
         case 4:
            setRadialGradientPaint(var0, var1, (RadialGradientPaint)var2, var4);
            break;
         case 5:
            setTexturePaint(var0, var1, (TexturePaint)var2, var4);
         }
      }

   }

   static void resetPaint(RenderQueue var0) {
      var0.ensureCapacity(4);
      RenderBuffer var1 = var0.getBuffer();
      var1.putInt(100);
   }

   private static void setColor(RenderQueue var0, int var1) {
      var0.ensureCapacity(8);
      RenderBuffer var2 = var0.getBuffer();
      var2.putInt(101);
      var2.putInt(var1);
   }

   private static void setGradientPaint(RenderQueue var0, AffineTransform var1, Color var2, Color var3, Point2D var4, Point2D var5, boolean var6, boolean var7) {
      PixelConverter var8 = PixelConverter.ArgbPre.instance;
      int var9 = var8.rgbToPixel(var2.getRGB(), (ColorModel)null);
      int var10 = var8.rgbToPixel(var3.getRGB(), (ColorModel)null);
      double var11 = var4.getX();
      double var13 = var4.getY();
      var1.translate(var11, var13);
      var11 = var5.getX() - var11;
      var13 = var5.getY() - var13;
      double var15 = Math.sqrt(var11 * var11 + var13 * var13);
      var1.rotate(var11, var13);
      var1.scale(2.0D * var15, 1.0D);
      var1.translate(-0.25D, 0.0D);

      double var17;
      double var19;
      double var21;
      try {
         var1.invert();
         var17 = var1.getScaleX();
         var19 = var1.getShearX();
         var21 = var1.getTranslateX();
      } catch (NoninvertibleTransformException var24) {
         var21 = 0.0D;
         var19 = 0.0D;
         var17 = 0.0D;
      }

      var0.ensureCapacityAndAlignment(44, 12);
      RenderBuffer var23 = var0.getBuffer();
      var23.putInt(102);
      var23.putInt(var7 ? 1 : 0);
      var23.putInt(var6 ? 1 : 0);
      var23.putDouble(var17).putDouble(var19).putDouble(var21);
      var23.putInt(var9).putInt(var10);
   }

   private static void setGradientPaint(RenderQueue var0, SunGraphics2D var1, GradientPaint var2, boolean var3) {
      setGradientPaint(var0, (AffineTransform)var1.transform.clone(), var2.getColor1(), var2.getColor2(), var2.getPoint1(), var2.getPoint2(), var2.isCyclic(), var3);
   }

   private static void setTexturePaint(RenderQueue var0, SunGraphics2D var1, TexturePaint var2, boolean var3) {
      BufferedImage var4 = var2.getImage();
      SurfaceData var5 = var1.surfaceData;
      SurfaceData var6 = var5.getSourceSurfaceData(var4, 0, CompositeType.SrcOver, (Color)null);
      boolean var7 = var1.interpolationType != 1;
      AffineTransform var8 = (AffineTransform)var1.transform.clone();
      Rectangle2D var9 = var2.getAnchorRect();
      var8.translate(var9.getX(), var9.getY());
      var8.scale(var9.getWidth(), var9.getHeight());

      double var10;
      double var12;
      double var14;
      double var16;
      double var18;
      double var20;
      try {
         var8.invert();
         var10 = var8.getScaleX();
         var12 = var8.getShearX();
         var14 = var8.getTranslateX();
         var16 = var8.getShearY();
         var18 = var8.getScaleY();
         var20 = var8.getTranslateY();
      } catch (NoninvertibleTransformException var23) {
         var20 = 0.0D;
         var18 = 0.0D;
         var16 = 0.0D;
         var14 = 0.0D;
         var12 = 0.0D;
         var10 = 0.0D;
      }

      var0.ensureCapacityAndAlignment(68, 12);
      RenderBuffer var22 = var0.getBuffer();
      var22.putInt(105);
      var22.putInt(var3 ? 1 : 0);
      var22.putInt(var7 ? 1 : 0);
      var22.putLong(var6.getNativeOps());
      var22.putDouble(var10).putDouble(var12).putDouble(var14);
      var22.putDouble(var16).putDouble(var18).putDouble(var20);
   }

   public static int convertSRGBtoLinearRGB(int var0) {
      float var1 = (float)var0 / 255.0F;
      float var2;
      if (var1 <= 0.04045F) {
         var2 = var1 / 12.92F;
      } else {
         var2 = (float)Math.pow(((double)var1 + 0.055D) / 1.055D, 2.4D);
      }

      return Math.round(var2 * 255.0F);
   }

   private static int colorToIntArgbPrePixel(Color var0, boolean var1) {
      int var2 = var0.getRGB();
      if (!var1 && var2 >> 24 == -1) {
         return var2;
      } else {
         int var3 = var2 >>> 24;
         int var4 = var2 >> 16 & 255;
         int var5 = var2 >> 8 & 255;
         int var6 = var2 & 255;
         if (var1) {
            var4 = convertSRGBtoLinearRGB(var4);
            var5 = convertSRGBtoLinearRGB(var5);
            var6 = convertSRGBtoLinearRGB(var6);
         }

         int var7 = var3 + (var3 >> 7);
         var4 = var4 * var7 >> 8;
         var5 = var5 * var7 >> 8;
         var6 = var6 * var7 >> 8;
         return var3 << 24 | var4 << 16 | var5 << 8 | var6;
      }
   }

   private static int[] convertToIntArgbPrePixels(Color[] var0, boolean var1) {
      int[] var2 = new int[var0.length];

      for(int var3 = 0; var3 < var0.length; ++var3) {
         var2[var3] = colorToIntArgbPrePixel(var0[var3], var1);
      }

      return var2;
   }

   private static void setLinearGradientPaint(RenderQueue var0, SunGraphics2D var1, LinearGradientPaint var2, boolean var3) {
      boolean var4 = var2.getColorSpace() == MultipleGradientPaint.ColorSpaceType.LINEAR_RGB;
      Color[] var5 = var2.getColors();
      int var6 = var5.length;
      Point2D var7 = var2.getStartPoint();
      Point2D var8 = var2.getEndPoint();
      AffineTransform var9 = var2.getTransform();
      var9.preConcatenate(var1.transform);
      if (!var4 && var6 == 2 && var2.getCycleMethod() != MultipleGradientPaint.CycleMethod.REPEAT) {
         boolean var24 = var2.getCycleMethod() != MultipleGradientPaint.CycleMethod.NO_CYCLE;
         setGradientPaint(var0, var9, var5[0], var5[1], var7, var8, var24, var3);
      } else {
         int var10 = var2.getCycleMethod().ordinal();
         float[] var11 = var2.getFractions();
         int[] var12 = convertToIntArgbPrePixels(var5, var4);
         double var13 = var7.getX();
         double var15 = var7.getY();
         var9.translate(var13, var15);
         var13 = var8.getX() - var13;
         var15 = var8.getY() - var15;
         double var17 = Math.sqrt(var13 * var13 + var15 * var15);
         var9.rotate(var13, var15);
         var9.scale(var17, 1.0D);

         float var19;
         float var20;
         float var21;
         try {
            var9.invert();
            var19 = (float)var9.getScaleX();
            var20 = (float)var9.getShearX();
            var21 = (float)var9.getTranslateX();
         } catch (NoninvertibleTransformException var23) {
            var21 = 0.0F;
            var20 = 0.0F;
            var19 = 0.0F;
         }

         var0.ensureCapacity(32 + var6 * 4 * 2);
         RenderBuffer var22 = var0.getBuffer();
         var22.putInt(103);
         var22.putInt(var3 ? 1 : 0);
         var22.putInt(var4 ? 1 : 0);
         var22.putInt(var10);
         var22.putInt(var6);
         var22.putFloat(var19);
         var22.putFloat(var20);
         var22.putFloat(var21);
         var22.put(var11);
         var22.put(var12);
      }
   }

   private static void setRadialGradientPaint(RenderQueue var0, SunGraphics2D var1, RadialGradientPaint var2, boolean var3) {
      boolean var4 = var2.getColorSpace() == MultipleGradientPaint.ColorSpaceType.LINEAR_RGB;
      int var5 = var2.getCycleMethod().ordinal();
      float[] var6 = var2.getFractions();
      Color[] var7 = var2.getColors();
      int var8 = var7.length;
      int[] var9 = convertToIntArgbPrePixels(var7, var4);
      Point2D var10 = var2.getCenterPoint();
      Point2D var11 = var2.getFocusPoint();
      float var12 = var2.getRadius();
      double var13 = var10.getX();
      double var15 = var10.getY();
      double var17 = var11.getX();
      double var19 = var11.getY();
      AffineTransform var21 = var2.getTransform();
      var21.preConcatenate(var1.transform);
      var11 = var21.transform(var11, var11);
      var21.translate(var13, var15);
      var21.rotate(var17 - var13, var19 - var15);
      var21.scale((double)var12, (double)var12);

      try {
         var21.invert();
      } catch (Exception var23) {
         var21.setToScale(0.0D, 0.0D);
      }

      var11 = var21.transform(var11, var11);
      var17 = Math.min(var11.getX(), 0.99D);
      var0.ensureCapacity(48 + var8 * 4 * 2);
      RenderBuffer var22 = var0.getBuffer();
      var22.putInt(104);
      var22.putInt(var3 ? 1 : 0);
      var22.putInt(var4 ? 1 : 0);
      var22.putInt(var8);
      var22.putInt(var5);
      var22.putFloat((float)var21.getScaleX());
      var22.putFloat((float)var21.getShearX());
      var22.putFloat((float)var21.getTranslateX());
      var22.putFloat((float)var21.getShearY());
      var22.putFloat((float)var21.getScaleY());
      var22.putFloat((float)var21.getTranslateY());
      var22.putFloat((float)var17);
      var22.put(var6);
      var22.put(var9);
   }
}
