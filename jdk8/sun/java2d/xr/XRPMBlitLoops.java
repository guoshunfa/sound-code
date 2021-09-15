package sun.java2d.xr;

import java.awt.AlphaComposite;
import java.lang.ref.WeakReference;
import sun.awt.image.SunVolatileImage;
import sun.java2d.InvalidPipeException;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.loops.GraphicsPrimitiveMgr;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.Region;

public class XRPMBlitLoops {
   static WeakReference<SunVolatileImage> argbTmpPM = new WeakReference((Object)null);
   static WeakReference<SunVolatileImage> rgbTmpPM = new WeakReference((Object)null);

   public static void register() {
      GraphicsPrimitive[] var0 = new GraphicsPrimitive[]{new XRPMBlit(XRSurfaceData.IntRgbX11, XRSurfaceData.IntRgbX11), new XRPMBlit(XRSurfaceData.IntRgbX11, XRSurfaceData.IntArgbPreX11), new XRPMBlit(XRSurfaceData.IntArgbPreX11, XRSurfaceData.IntRgbX11), new XRPMBlit(XRSurfaceData.IntArgbPreX11, XRSurfaceData.IntArgbPreX11), new XRPMScaledBlit(XRSurfaceData.IntRgbX11, XRSurfaceData.IntRgbX11), new XRPMScaledBlit(XRSurfaceData.IntRgbX11, XRSurfaceData.IntArgbPreX11), new XRPMScaledBlit(XRSurfaceData.IntArgbPreX11, XRSurfaceData.IntRgbX11), new XRPMScaledBlit(XRSurfaceData.IntArgbPreX11, XRSurfaceData.IntArgbPreX11), new XRPMTransformedBlit(XRSurfaceData.IntRgbX11, XRSurfaceData.IntRgbX11), new XRPMTransformedBlit(XRSurfaceData.IntRgbX11, XRSurfaceData.IntArgbPreX11), new XRPMTransformedBlit(XRSurfaceData.IntArgbPreX11, XRSurfaceData.IntRgbX11), new XRPMTransformedBlit(XRSurfaceData.IntArgbPreX11, XRSurfaceData.IntArgbPreX11), new XrSwToPMBlit(SurfaceType.IntArgb, XRSurfaceData.IntRgbX11), new XrSwToPMBlit(SurfaceType.IntRgb, XRSurfaceData.IntRgbX11), new XrSwToPMBlit(SurfaceType.IntBgr, XRSurfaceData.IntRgbX11), new XrSwToPMBlit(SurfaceType.ThreeByteBgr, XRSurfaceData.IntRgbX11), new XrSwToPMBlit(SurfaceType.Ushort565Rgb, XRSurfaceData.IntRgbX11), new XrSwToPMBlit(SurfaceType.Ushort555Rgb, XRSurfaceData.IntRgbX11), new XrSwToPMBlit(SurfaceType.ByteIndexed, XRSurfaceData.IntRgbX11), new XrSwToPMBlit(SurfaceType.IntArgb, XRSurfaceData.IntArgbPreX11), new XrSwToPMBlit(SurfaceType.IntRgb, XRSurfaceData.IntArgbPreX11), new XrSwToPMBlit(SurfaceType.IntBgr, XRSurfaceData.IntArgbPreX11), new XrSwToPMBlit(SurfaceType.ThreeByteBgr, XRSurfaceData.IntArgbPreX11), new XrSwToPMBlit(SurfaceType.Ushort565Rgb, XRSurfaceData.IntArgbPreX11), new XrSwToPMBlit(SurfaceType.Ushort555Rgb, XRSurfaceData.IntArgbPreX11), new XrSwToPMBlit(SurfaceType.ByteIndexed, XRSurfaceData.IntArgbPreX11), new XrSwToPMScaledBlit(SurfaceType.IntArgb, XRSurfaceData.IntRgbX11), new XrSwToPMScaledBlit(SurfaceType.IntRgb, XRSurfaceData.IntRgbX11), new XrSwToPMScaledBlit(SurfaceType.IntBgr, XRSurfaceData.IntRgbX11), new XrSwToPMScaledBlit(SurfaceType.ThreeByteBgr, XRSurfaceData.IntRgbX11), new XrSwToPMScaledBlit(SurfaceType.Ushort565Rgb, XRSurfaceData.IntRgbX11), new XrSwToPMScaledBlit(SurfaceType.Ushort555Rgb, XRSurfaceData.IntRgbX11), new XrSwToPMScaledBlit(SurfaceType.ByteIndexed, XRSurfaceData.IntRgbX11), new XrSwToPMScaledBlit(SurfaceType.IntArgb, XRSurfaceData.IntArgbPreX11), new XrSwToPMScaledBlit(SurfaceType.IntRgb, XRSurfaceData.IntArgbPreX11), new XrSwToPMScaledBlit(SurfaceType.IntBgr, XRSurfaceData.IntArgbPreX11), new XrSwToPMScaledBlit(SurfaceType.ThreeByteBgr, XRSurfaceData.IntArgbPreX11), new XrSwToPMScaledBlit(SurfaceType.Ushort565Rgb, XRSurfaceData.IntArgbPreX11), new XrSwToPMScaledBlit(SurfaceType.Ushort555Rgb, XRSurfaceData.IntArgbPreX11), new XrSwToPMScaledBlit(SurfaceType.ByteIndexed, XRSurfaceData.IntArgbPreX11), new XrSwToPMTransformedBlit(SurfaceType.IntArgb, XRSurfaceData.IntRgbX11), new XrSwToPMTransformedBlit(SurfaceType.IntRgb, XRSurfaceData.IntRgbX11), new XrSwToPMTransformedBlit(SurfaceType.IntBgr, XRSurfaceData.IntRgbX11), new XrSwToPMTransformedBlit(SurfaceType.ThreeByteBgr, XRSurfaceData.IntRgbX11), new XrSwToPMTransformedBlit(SurfaceType.Ushort565Rgb, XRSurfaceData.IntRgbX11), new XrSwToPMTransformedBlit(SurfaceType.Ushort555Rgb, XRSurfaceData.IntRgbX11), new XrSwToPMTransformedBlit(SurfaceType.ByteIndexed, XRSurfaceData.IntRgbX11), new XrSwToPMTransformedBlit(SurfaceType.IntArgb, XRSurfaceData.IntArgbPreX11), new XrSwToPMTransformedBlit(SurfaceType.IntRgb, XRSurfaceData.IntArgbPreX11), new XrSwToPMTransformedBlit(SurfaceType.IntBgr, XRSurfaceData.IntArgbPreX11), new XrSwToPMTransformedBlit(SurfaceType.ThreeByteBgr, XRSurfaceData.IntArgbPreX11), new XrSwToPMTransformedBlit(SurfaceType.Ushort565Rgb, XRSurfaceData.IntArgbPreX11), new XrSwToPMTransformedBlit(SurfaceType.Ushort555Rgb, XRSurfaceData.IntArgbPreX11), new XrSwToPMTransformedBlit(SurfaceType.ByteIndexed, XRSurfaceData.IntArgbPreX11)};
      GraphicsPrimitiveMgr.register(var0);
   }

   protected static XRSurfaceData cacheToTmpSurface(SurfaceData var0, XRSurfaceData var1, int var2, int var3, int var4, int var5) {
      SunVolatileImage var6;
      SurfaceType var7;
      if (var0.getTransparency() == 1) {
         var6 = (SunVolatileImage)rgbTmpPM.get();
         var7 = SurfaceType.IntRgb;
      } else {
         var6 = (SunVolatileImage)argbTmpPM.get();
         var7 = SurfaceType.IntArgbPre;
      }

      if (var6 == null || var6.getWidth() < var2 || var6.getHeight() < var3 || !(var6.getDestSurface() instanceof XRSurfaceData)) {
         if (var6 != null) {
            var6.flush();
         }

         var6 = (SunVolatileImage)var1.getGraphicsConfig().createCompatibleVolatileImage(var2, var3, var0.getTransparency());
         var6.setAccelerationPriority(1.0F);
         if (!(var6.getDestSurface() instanceof XRSurfaceData)) {
            throw new InvalidPipeException("Could not create XRSurfaceData");
         }

         if (var0.getTransparency() == 1) {
            rgbTmpPM = new WeakReference(var6);
         } else {
            argbTmpPM = new WeakReference(var6);
         }
      }

      Blit var8 = Blit.getFromCache(var0.getSurfaceType(), CompositeType.SrcNoEa, var7);
      if (!(var6.getDestSurface() instanceof XRSurfaceData)) {
         throw new InvalidPipeException("wrong surface data type: " + var6.getDestSurface());
      } else {
         XRSurfaceData var9 = (XRSurfaceData)var6.getDestSurface();
         var8.Blit(var0, var9, AlphaComposite.Src, (Region)null, var4, var5, 0, 0, var2, var3);
         return var9;
      }
   }
}
