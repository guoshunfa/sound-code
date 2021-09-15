package sun.java2d.opengl;

import java.awt.Composite;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.loops.GraphicsPrimitiveMgr;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.RenderBuffer;
import sun.java2d.pipe.RenderQueue;

final class OGLBlitLoops {
   private static final int OFFSET_SRCTYPE = 16;
   private static final int OFFSET_HINT = 8;
   private static final int OFFSET_TEXTURE = 3;
   private static final int OFFSET_RTT = 2;
   private static final int OFFSET_XFORM = 1;
   private static final int OFFSET_ISOBLIT = 0;

   static void register() {
      OGLSwToSurfaceBlit var0 = new OGLSwToSurfaceBlit(SurfaceType.IntArgbPre, 1);
      OGLSwToTextureBlit var1 = new OGLSwToTextureBlit(SurfaceType.IntArgbPre, 1);
      OGLSwToSurfaceTransform var2 = new OGLSwToSurfaceTransform(SurfaceType.IntArgbPre, 1);
      OGLSurfaceToSwBlit var3 = new OGLSurfaceToSwBlit(SurfaceType.IntArgbPre, 1);
      GraphicsPrimitive[] var4 = new GraphicsPrimitive[]{new OGLSurfaceToSurfaceBlit(), new OGLSurfaceToSurfaceScale(), new OGLSurfaceToSurfaceTransform(), new OGLRTTSurfaceToSurfaceBlit(), new OGLRTTSurfaceToSurfaceScale(), new OGLRTTSurfaceToSurfaceTransform(), new OGLSurfaceToSwBlit(SurfaceType.IntArgb, 0), var3, var0, new OGLSwToSurfaceBlit(SurfaceType.IntRgb, 2), new OGLSwToSurfaceBlit(SurfaceType.IntRgbx, 3), new OGLSwToSurfaceBlit(SurfaceType.IntBgr, 4), new OGLSwToSurfaceBlit(SurfaceType.IntBgrx, 5), new OGLSwToSurfaceBlit(SurfaceType.ThreeByteBgr, 11), new OGLSwToSurfaceBlit(SurfaceType.Ushort565Rgb, 6), new OGLSwToSurfaceBlit(SurfaceType.Ushort555Rgb, 7), new OGLSwToSurfaceBlit(SurfaceType.Ushort555Rgbx, 8), new OGLSwToSurfaceBlit(SurfaceType.ByteGray, 9), new OGLSwToSurfaceBlit(SurfaceType.UshortGray, 10), new OGLGeneralBlit(OGLSurfaceData.OpenGLSurface, CompositeType.AnyAlpha, var0), new OGLAnyCompositeBlit(OGLSurfaceData.OpenGLSurface, var3, var3, var0), new OGLAnyCompositeBlit(SurfaceType.Any, (Blit)null, var3, var0), new OGLSwToSurfaceScale(SurfaceType.IntRgb, 2), new OGLSwToSurfaceScale(SurfaceType.IntRgbx, 3), new OGLSwToSurfaceScale(SurfaceType.IntBgr, 4), new OGLSwToSurfaceScale(SurfaceType.IntBgrx, 5), new OGLSwToSurfaceScale(SurfaceType.ThreeByteBgr, 11), new OGLSwToSurfaceScale(SurfaceType.Ushort565Rgb, 6), new OGLSwToSurfaceScale(SurfaceType.Ushort555Rgb, 7), new OGLSwToSurfaceScale(SurfaceType.Ushort555Rgbx, 8), new OGLSwToSurfaceScale(SurfaceType.ByteGray, 9), new OGLSwToSurfaceScale(SurfaceType.UshortGray, 10), new OGLSwToSurfaceScale(SurfaceType.IntArgbPre, 1), new OGLSwToSurfaceTransform(SurfaceType.IntRgb, 2), new OGLSwToSurfaceTransform(SurfaceType.IntRgbx, 3), new OGLSwToSurfaceTransform(SurfaceType.IntBgr, 4), new OGLSwToSurfaceTransform(SurfaceType.IntBgrx, 5), new OGLSwToSurfaceTransform(SurfaceType.ThreeByteBgr, 11), new OGLSwToSurfaceTransform(SurfaceType.Ushort565Rgb, 6), new OGLSwToSurfaceTransform(SurfaceType.Ushort555Rgb, 7), new OGLSwToSurfaceTransform(SurfaceType.Ushort555Rgbx, 8), new OGLSwToSurfaceTransform(SurfaceType.ByteGray, 9), new OGLSwToSurfaceTransform(SurfaceType.UshortGray, 10), var2, new OGLGeneralTransformedBlit(var2), new OGLTextureToSurfaceBlit(), new OGLTextureToSurfaceScale(), new OGLTextureToSurfaceTransform(), var1, new OGLSwToTextureBlit(SurfaceType.IntRgb, 2), new OGLSwToTextureBlit(SurfaceType.IntRgbx, 3), new OGLSwToTextureBlit(SurfaceType.IntBgr, 4), new OGLSwToTextureBlit(SurfaceType.IntBgrx, 5), new OGLSwToTextureBlit(SurfaceType.ThreeByteBgr, 11), new OGLSwToTextureBlit(SurfaceType.Ushort565Rgb, 6), new OGLSwToTextureBlit(SurfaceType.Ushort555Rgb, 7), new OGLSwToTextureBlit(SurfaceType.Ushort555Rgbx, 8), new OGLSwToTextureBlit(SurfaceType.ByteGray, 9), new OGLSwToTextureBlit(SurfaceType.UshortGray, 10), new OGLGeneralBlit(OGLSurfaceData.OpenGLTexture, CompositeType.SrcNoEa, var1)};
      GraphicsPrimitiveMgr.register(var4);
   }

   private static int createPackedParams(boolean var0, boolean var1, boolean var2, boolean var3, int var4, int var5) {
      return var5 << 16 | var4 << 8 | (var1 ? 1 : 0) << 3 | (var2 ? 1 : 0) << 2 | (var3 ? 1 : 0) << 1 | (var0 ? 1 : 0) << 0;
   }

   private static void enqueueBlit(RenderQueue var0, SurfaceData var1, SurfaceData var2, int var3, int var4, int var5, int var6, int var7, double var8, double var10, double var12, double var14) {
      RenderBuffer var16 = var0.getBuffer();
      var0.ensureCapacityAndAlignment(72, 24);
      var16.putInt(31);
      var16.putInt(var3);
      var16.putInt(var4).putInt(var5);
      var16.putInt(var6).putInt(var7);
      var16.putDouble(var8).putDouble(var10);
      var16.putDouble(var12).putDouble(var14);
      var16.putLong(var1.getNativeOps());
      var16.putLong(var2.getNativeOps());
   }

   static void Blit(SurfaceData var0, SurfaceData var1, Composite var2, Region var3, AffineTransform var4, int var5, int var6, int var7, int var8, int var9, double var10, double var12, double var14, double var16, int var18, boolean var19) {
      int var20 = 0;
      if (var0.getTransparency() == 1) {
         var20 |= 1;
      }

      OGLRenderQueue var21 = OGLRenderQueue.getInstance();
      var21.lock();

      try {
         var21.addReference(var0);
         OGLSurfaceData var22 = (OGLSurfaceData)var1;
         if (var19) {
            OGLGraphicsConfig var23 = var22.getOGLGraphicsConfig();
            OGLContext.setScratchSurface(var23);
         } else {
            OGLContext.validateContext(var22, var22, var3, var2, var4, (Paint)null, (SunGraphics2D)null, var20);
         }

         int var27 = createPackedParams(false, var19, false, var4 != null, var5, var18);
         enqueueBlit(var21, var0, var1, var27, var6, var7, var8, var9, var10, var12, var14, var16);
         var21.flushNow();
      } finally {
         var21.unlock();
      }

   }

   static void IsoBlit(SurfaceData var0, SurfaceData var1, BufferedImage var2, BufferedImageOp var3, Composite var4, Region var5, AffineTransform var6, int var7, int var8, int var9, int var10, int var11, double var12, double var14, double var16, double var18, boolean var20) {
      int var21 = 0;
      if (var0.getTransparency() == 1) {
         var21 |= 1;
      }

      OGLRenderQueue var22 = OGLRenderQueue.getInstance();
      var22.lock();

      try {
         OGLSurfaceData var23 = (OGLSurfaceData)var0;
         OGLSurfaceData var24 = (OGLSurfaceData)var1;
         int var25 = var23.getType();
         boolean var26;
         OGLSurfaceData var27;
         if (var25 == 3) {
            var26 = false;
            var27 = var24;
         } else {
            var26 = true;
            if (var25 == 5) {
               var27 = var24;
            } else {
               var27 = var23;
            }
         }

         OGLContext.validateContext(var27, var24, var5, var4, var6, (Paint)null, (SunGraphics2D)null, var21);
         if (var3 != null) {
            OGLBufImgOps.enableBufImgOp(var22, var23, var2, var3);
         }

         int var28 = createPackedParams(true, var20, var26, var6 != null, var7, 0);
         enqueueBlit(var22, var0, var1, var28, var8, var9, var10, var11, var12, var14, var16, var18);
         if (var3 != null) {
            OGLBufImgOps.disableBufImgOp(var22, var3);
         }

         if (var26 && var24.isOnScreen()) {
            var22.flushNow();
         }
      } finally {
         var22.unlock();
      }

   }
}
