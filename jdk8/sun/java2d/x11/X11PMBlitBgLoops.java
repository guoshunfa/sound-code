package sun.java2d.x11;

import java.awt.Composite;
import sun.awt.SunToolkit;
import sun.java2d.SurfaceData;
import sun.java2d.loops.BlitBg;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.loops.GraphicsPrimitiveMgr;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.Region;

public class X11PMBlitBgLoops extends BlitBg {
   public static void register() {
      GraphicsPrimitive[] var0 = new GraphicsPrimitive[]{new X11PMBlitBgLoops(X11SurfaceData.IntBgrX11_BM, X11SurfaceData.IntBgrX11), new X11PMBlitBgLoops(X11SurfaceData.IntRgbX11_BM, X11SurfaceData.IntRgbX11), new X11PMBlitBgLoops(X11SurfaceData.ThreeByteBgrX11_BM, X11SurfaceData.ThreeByteBgrX11), new X11PMBlitBgLoops(X11SurfaceData.ThreeByteRgbX11_BM, X11SurfaceData.ThreeByteRgbX11), new X11PMBlitBgLoops(X11SurfaceData.ByteIndexedX11_BM, X11SurfaceData.ByteIndexedOpaqueX11), new X11PMBlitBgLoops(X11SurfaceData.ByteGrayX11_BM, X11SurfaceData.ByteGrayX11), new X11PMBlitBgLoops(X11SurfaceData.Index8GrayX11_BM, X11SurfaceData.Index8GrayX11), new X11PMBlitBgLoops(X11SurfaceData.UShort555RgbX11_BM, X11SurfaceData.UShort555RgbX11), new X11PMBlitBgLoops(X11SurfaceData.UShort565RgbX11_BM, X11SurfaceData.UShort565RgbX11), new X11PMBlitBgLoops(X11SurfaceData.UShortIndexedX11_BM, X11SurfaceData.UShortIndexedX11), new X11PMBlitBgLoops(X11SurfaceData.IntRgbX11_BM, X11SurfaceData.IntArgbPreX11), new X11PMBlitBgLoops(X11SurfaceData.IntBgrX11_BM, X11SurfaceData.FourByteAbgrPreX11)};
      GraphicsPrimitiveMgr.register(var0);
   }

   public X11PMBlitBgLoops(SurfaceType var1, SurfaceType var2) {
      super(var1, CompositeType.SrcNoEa, var2);
   }

   public void BlitBg(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11) {
      SunToolkit.awtLock();

      try {
         int var12 = var2.pixelFor(var5);
         X11SurfaceData var13 = (X11SurfaceData)var2;
         long var14 = var13.getBlitGC(var4, false);
         this.nativeBlitBg(var1.getNativeOps(), var2.getNativeOps(), var14, var12, var6, var7, var8, var9, var10, var11);
      } finally {
         SunToolkit.awtUnlock();
      }

   }

   private native void nativeBlitBg(long var1, long var3, long var5, int var7, int var8, int var9, int var10, int var11, int var12, int var13);
}
