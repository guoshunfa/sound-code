package sun.java2d.xr;

import java.awt.Composite;
import sun.awt.SunToolkit;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.loops.GraphicsPrimitiveMgr;
import sun.java2d.loops.MaskFill;
import sun.java2d.loops.SurfaceType;

public class XRMaskFill extends MaskFill {
   static void register() {
      GraphicsPrimitive[] var0 = new GraphicsPrimitive[]{new XRMaskFill(SurfaceType.AnyColor, CompositeType.SrcOver, XRSurfaceData.IntRgbX11), new XRMaskFill(SurfaceType.OpaqueColor, CompositeType.SrcNoEa, XRSurfaceData.IntRgbX11), new XRMaskFill(SurfaceType.GradientPaint, CompositeType.SrcOver, XRSurfaceData.IntRgbX11), new XRMaskFill(SurfaceType.OpaqueGradientPaint, CompositeType.SrcNoEa, XRSurfaceData.IntRgbX11), new XRMaskFill(SurfaceType.LinearGradientPaint, CompositeType.SrcOver, XRSurfaceData.IntRgbX11), new XRMaskFill(SurfaceType.OpaqueLinearGradientPaint, CompositeType.SrcNoEa, XRSurfaceData.IntRgbX11), new XRMaskFill(SurfaceType.RadialGradientPaint, CompositeType.SrcOver, XRSurfaceData.IntRgbX11), new XRMaskFill(SurfaceType.OpaqueRadialGradientPaint, CompositeType.SrcNoEa, XRSurfaceData.IntRgbX11), new XRMaskFill(SurfaceType.TexturePaint, CompositeType.SrcOver, XRSurfaceData.IntRgbX11), new XRMaskFill(SurfaceType.OpaqueTexturePaint, CompositeType.SrcNoEa, XRSurfaceData.IntRgbX11), new XRMaskFill(SurfaceType.AnyColor, CompositeType.SrcOver, XRSurfaceData.IntArgbPreX11), new XRMaskFill(SurfaceType.OpaqueColor, CompositeType.SrcNoEa, XRSurfaceData.IntArgbPreX11), new XRMaskFill(SurfaceType.GradientPaint, CompositeType.SrcOver, XRSurfaceData.IntArgbPreX11), new XRMaskFill(SurfaceType.OpaqueGradientPaint, CompositeType.SrcNoEa, XRSurfaceData.IntArgbPreX11), new XRMaskFill(SurfaceType.LinearGradientPaint, CompositeType.SrcOver, XRSurfaceData.IntArgbPreX11), new XRMaskFill(SurfaceType.OpaqueLinearGradientPaint, CompositeType.SrcNoEa, XRSurfaceData.IntArgbPreX11), new XRMaskFill(SurfaceType.RadialGradientPaint, CompositeType.SrcOver, XRSurfaceData.IntArgbPreX11), new XRMaskFill(SurfaceType.OpaqueRadialGradientPaint, CompositeType.SrcNoEa, XRSurfaceData.IntArgbPreX11), new XRMaskFill(SurfaceType.TexturePaint, CompositeType.SrcOver, XRSurfaceData.IntArgbPreX11), new XRMaskFill(SurfaceType.OpaqueTexturePaint, CompositeType.SrcNoEa, XRSurfaceData.IntArgbPreX11)};
      GraphicsPrimitiveMgr.register(var0);
   }

   protected XRMaskFill(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      super(var1, var2, var3);
   }

   protected native void maskFill(long var1, int var3, int var4, int var5, int var6, int var7, int var8, int var9, byte[] var10);

   public void MaskFill(SunGraphics2D var1, SurfaceData var2, Composite var3, int var4, int var5, int var6, int var7, byte[] var8, int var9, int var10) {
      try {
         SunToolkit.awtLock();
         XRSurfaceData var11 = (XRSurfaceData)var2;
         var11.validateAsDestination((SunGraphics2D)null, var1.getCompClip());
         XRCompositeManager var12 = var11.maskBuffer;
         var12.validateCompositeState(var3, var1.transform, var1.paint, var1);
         int var13 = var12.getMaskBuffer().uploadMask(var6, var7, var10, var9, var8);
         var12.XRComposite(0, var13, var11.picture, var4, var5, 0, 0, var4, var5, var6, var7);
         var12.getMaskBuffer().clearUploadMask(var13, var6, var7);
      } finally {
         SunToolkit.awtUnlock();
      }

   }
}
