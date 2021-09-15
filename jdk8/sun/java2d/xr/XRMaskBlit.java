package sun.java2d.xr;

import java.awt.Composite;
import java.awt.geom.AffineTransform;
import sun.awt.SunToolkit;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.loops.GraphicsPrimitiveMgr;
import sun.java2d.loops.MaskBlit;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.Region;

public class XRMaskBlit extends MaskBlit {
   static void register() {
      GraphicsPrimitive[] var0 = new GraphicsPrimitive[]{new XRMaskBlit(XRSurfaceData.IntArgbPreX11, CompositeType.SrcOver, XRSurfaceData.IntArgbPreX11), new XRMaskBlit(XRSurfaceData.IntRgbX11, CompositeType.SrcOver, XRSurfaceData.IntRgbX11), new XRMaskBlit(XRSurfaceData.IntArgbPreX11, CompositeType.SrcNoEa, XRSurfaceData.IntRgbX11), new XRMaskBlit(XRSurfaceData.IntRgbX11, CompositeType.SrcNoEa, XRSurfaceData.IntArgbPreX11)};
      GraphicsPrimitiveMgr.register(var0);
   }

   public XRMaskBlit(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      super(var1, CompositeType.AnyAlpha, var3);
   }

   protected native void maskBlit(long var1, long var3, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13, byte[] var14);

   public void MaskBlit(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, int var9, int var10, byte[] var11, int var12, int var13) {
      if (var9 > 0 && var10 > 0) {
         try {
            SunToolkit.awtLock();
            XRSurfaceData var14 = (XRSurfaceData)var1;
            var14.validateAsSource((AffineTransform)null, 0, 0);
            XRCompositeManager var15 = var14.maskBuffer;
            XRSurfaceData var16 = (XRSurfaceData)var2;
            var16.validateAsDestination((SunGraphics2D)null, var4);
            int var17 = var15.getMaskBuffer().uploadMask(var9, var10, var13, var12, var11);
            var15.XRComposite(var14.getPicture(), var17, var16.getPicture(), var5, var6, 0, 0, var7, var8, var9, var10);
            var15.getMaskBuffer().clearUploadMask(var17, var9, var10);
         } finally {
            SunToolkit.awtUnlock();
         }

      }
   }
}
