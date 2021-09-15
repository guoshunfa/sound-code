package sun.java2d.xr;

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
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;

abstract class XRPaints {
   static XRCompositeManager xrCompMan;
   static final XRPaints.XRGradient xrGradient = new XRPaints.XRGradient();
   static final XRPaints.XRLinearGradient xrLinearGradient = new XRPaints.XRLinearGradient();
   static final XRPaints.XRRadialGradient xrRadialGradient = new XRPaints.XRRadialGradient();
   static final XRPaints.XRTexture xrTexture = new XRPaints.XRTexture();

   public static void register(XRCompositeManager var0) {
      xrCompMan = var0;
   }

   private static XRPaints getXRPaint(SunGraphics2D var0) {
      switch(var0.paintState) {
      case 2:
         return xrGradient;
      case 3:
         return xrLinearGradient;
      case 4:
         return xrRadialGradient;
      case 5:
         return xrTexture;
      default:
         return null;
      }
   }

   static boolean isValid(SunGraphics2D var0) {
      XRPaints var1 = getXRPaint(var0);
      return var1 != null && var1.isPaintValid(var0);
   }

   static void setPaint(SunGraphics2D var0, Paint var1) {
      XRPaints var2 = getXRPaint(var0);
      if (var2 != null) {
         var2.setXRPaint(var0, var1);
      }

   }

   abstract boolean isPaintValid(SunGraphics2D var1);

   abstract void setXRPaint(SunGraphics2D var1, Paint var2);

   public int getGradientLength(Point2D var1, Point2D var2) {
      double var3 = Math.max(var1.getX(), var2.getX()) - Math.min(var1.getX(), var2.getX());
      double var5 = Math.max(var1.getY(), var2.getY()) - Math.min(var1.getY(), var2.getY());
      return (int)Math.ceil(Math.sqrt(var3 * var3 + var5 * var5));
   }

   public int[] convertToIntArgbPixels(Color[] var1) {
      int[] var2 = new int[var1.length];

      for(int var3 = 0; var3 < var1.length; ++var3) {
         var2[var3] = this.colorToIntArgbPixel(var1[var3]);
      }

      return var2;
   }

   public int colorToIntArgbPixel(Color var1) {
      int var2 = var1.getRGB();
      int var3 = Math.round(xrCompMan.getExtraAlpha() * (float)(var2 >>> 24));
      return var3 << 24 | var2 & 16777215;
   }

   private static class XRTexture extends XRPaints {
      private XRTexture() {
      }

      private XRSurfaceData getAccSrcSurface(XRSurfaceData var1, BufferedImage var2) {
         SurfaceData var3 = var1.getSourceSurfaceData(var2, 0, CompositeType.SrcOver, (Color)null);
         if (!(var3 instanceof XRSurfaceData)) {
            var3 = var1.getSourceSurfaceData(var2, 0, CompositeType.SrcOver, (Color)null);
            if (!(var3 instanceof XRSurfaceData)) {
               throw new InternalError("Surface not cachable");
            }
         }

         return (XRSurfaceData)var3;
      }

      boolean isPaintValid(SunGraphics2D var1) {
         TexturePaint var2 = (TexturePaint)var1.paint;
         BufferedImage var3 = var2.getImage();
         XRSurfaceData var4 = (XRSurfaceData)var1.getDestSurface();
         return this.getAccSrcSurface(var4, var3) != null;
      }

      void setXRPaint(SunGraphics2D var1, Paint var2) {
         TexturePaint var3 = (TexturePaint)var2;
         BufferedImage var4 = var3.getImage();
         Rectangle2D var5 = var3.getAnchorRect();
         XRSurfaceData var6 = (XRSurfaceData)var1.surfaceData;
         XRSurfaceData var7 = this.getAccSrcSurface(var6, var4);
         AffineTransform var8 = new AffineTransform();
         var8.translate(var5.getX(), var5.getY());
         var8.scale(var5.getWidth() / (double)var4.getWidth(), var5.getHeight() / (double)var4.getHeight());

         try {
            var8.invert();
         } catch (NoninvertibleTransformException var10) {
            var8.setToIdentity();
         }

         var7.setStaticSrcTx(var8);
         var7.validateAsSource(var8, 1, XRUtils.ATransOpToXRQuality(var1.interpolationType));
         xrCompMan.setTexturePaint(var7);
      }

      // $FF: synthetic method
      XRTexture(Object var1) {
         this();
      }
   }

   private static class XRRadialGradient extends XRPaints {
      private XRRadialGradient() {
      }

      boolean isPaintValid(SunGraphics2D var1) {
         RadialGradientPaint var2 = (RadialGradientPaint)var1.paint;
         return var2.getFocusPoint().equals(var2.getCenterPoint()) && var2.getColorSpace() == MultipleGradientPaint.ColorSpaceType.SRGB;
      }

      void setXRPaint(SunGraphics2D var1, Paint var2) {
         RadialGradientPaint var3 = (RadialGradientPaint)var2;
         Color[] var4 = var3.getColors();
         Point2D var5 = var3.getCenterPoint();
         int var6 = XRUtils.getRepeatForCycleMethod(var3.getCycleMethod());
         float[] var7 = var3.getFractions();
         int[] var8 = this.convertToIntArgbPixels(var4);
         float var9 = var3.getRadius();
         float var10 = (float)var5.getX();
         float var11 = (float)var5.getY();
         AffineTransform var12 = var3.getTransform();

         try {
            var12.invert();
         } catch (NoninvertibleTransformException var16) {
            var16.printStackTrace();
         }

         XRBackend var13 = xrCompMan.getBackend();
         int var14 = var13.createRadialGradient(var10, var11, 0.0F, var9, var7, var8, var6);
         XRSurfaceData.XRInternalSurfaceData var15 = new XRSurfaceData.XRInternalSurfaceData(var13, var14);
         var15.setStaticSrcTx(var12);
         xrCompMan.setGradientPaint(var15);
      }

      // $FF: synthetic method
      XRRadialGradient(Object var1) {
         this();
      }
   }

   private static class XRLinearGradient extends XRPaints {
      private XRLinearGradient() {
      }

      boolean isPaintValid(SunGraphics2D var1) {
         return ((LinearGradientPaint)var1.getPaint()).getColorSpace() == MultipleGradientPaint.ColorSpaceType.SRGB;
      }

      void setXRPaint(SunGraphics2D var1, Paint var2) {
         LinearGradientPaint var3 = (LinearGradientPaint)var2;
         Color[] var4 = var3.getColors();
         Point2D var5 = var3.getStartPoint();
         Point2D var6 = var3.getEndPoint();
         int var7 = XRUtils.getRepeatForCycleMethod(var3.getCycleMethod());
         float[] var8 = var3.getFractions();
         int[] var9 = this.convertToIntArgbPixels(var4);
         AffineTransform var10 = var3.getTransform();

         try {
            var10.invert();
         } catch (NoninvertibleTransformException var14) {
            var14.printStackTrace();
         }

         XRBackend var11 = xrCompMan.getBackend();
         int var12 = var11.createLinearGradient(var5, var6, var8, var9, var7);
         XRSurfaceData.XRInternalSurfaceData var13 = new XRSurfaceData.XRInternalSurfaceData(var11, var12);
         var13.setStaticSrcTx(var10);
         xrCompMan.setGradientPaint(var13);
      }

      // $FF: synthetic method
      XRLinearGradient(Object var1) {
         this();
      }
   }

   private static class XRGradient extends XRPaints {
      private XRGradient() {
      }

      boolean isPaintValid(SunGraphics2D var1) {
         return true;
      }

      void setXRPaint(SunGraphics2D var1, Paint var2) {
         GradientPaint var3 = (GradientPaint)var2;
         int var4 = var3.isCyclic() ? 3 : 2;
         float[] var5 = new float[]{0.0F, 1.0F};
         int[] var6 = this.convertToIntArgbPixels(new Color[]{var3.getColor1(), var3.getColor2()});
         Point2D var7 = var3.getPoint1();
         Point2D var8 = var3.getPoint2();
         XRBackend var9 = xrCompMan.getBackend();
         int var10 = var9.createLinearGradient(var7, var8, var5, var6, var4);
         xrCompMan.setGradientPaint(new XRSurfaceData.XRInternalSurfaceData(var9, var10));
      }

      // $FF: synthetic method
      XRGradient(Object var1) {
         this();
      }
   }
}
