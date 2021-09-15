package sun.java2d.x11;

import java.awt.Composite;
import java.awt.image.IndexColorModel;
import sun.awt.SunToolkit;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.loops.GraphicsPrimitiveMgr;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.Region;

public class X11PMBlitLoops extends Blit {
   public static void register() {
      GraphicsPrimitive[] var0 = new GraphicsPrimitive[]{new X11PMBlitLoops(X11SurfaceData.IntBgrX11, X11SurfaceData.IntBgrX11, false), new X11PMBlitLoops(X11SurfaceData.IntRgbX11, X11SurfaceData.IntRgbX11, false), new X11PMBlitLoops(X11SurfaceData.ThreeByteBgrX11, X11SurfaceData.ThreeByteBgrX11, false), new X11PMBlitLoops(X11SurfaceData.ThreeByteRgbX11, X11SurfaceData.ThreeByteRgbX11, false), new X11PMBlitLoops(X11SurfaceData.ByteIndexedOpaqueX11, X11SurfaceData.ByteIndexedOpaqueX11, false), new X11PMBlitLoops(X11SurfaceData.ByteGrayX11, X11SurfaceData.ByteGrayX11, false), new X11PMBlitLoops(X11SurfaceData.Index8GrayX11, X11SurfaceData.Index8GrayX11, false), new X11PMBlitLoops(X11SurfaceData.UShort555RgbX11, X11SurfaceData.UShort555RgbX11, false), new X11PMBlitLoops(X11SurfaceData.UShort565RgbX11, X11SurfaceData.UShort565RgbX11, false), new X11PMBlitLoops(X11SurfaceData.UShortIndexedX11, X11SurfaceData.UShortIndexedX11, false), new X11PMBlitLoops(X11SurfaceData.IntBgrX11_BM, X11SurfaceData.IntBgrX11, true), new X11PMBlitLoops(X11SurfaceData.IntRgbX11_BM, X11SurfaceData.IntRgbX11, true), new X11PMBlitLoops(X11SurfaceData.ThreeByteBgrX11_BM, X11SurfaceData.ThreeByteBgrX11, true), new X11PMBlitLoops(X11SurfaceData.ThreeByteRgbX11_BM, X11SurfaceData.ThreeByteRgbX11, true), new X11PMBlitLoops(X11SurfaceData.ByteIndexedX11_BM, X11SurfaceData.ByteIndexedOpaqueX11, true), new X11PMBlitLoops(X11SurfaceData.ByteGrayX11_BM, X11SurfaceData.ByteGrayX11, true), new X11PMBlitLoops(X11SurfaceData.Index8GrayX11_BM, X11SurfaceData.Index8GrayX11, true), new X11PMBlitLoops(X11SurfaceData.UShort555RgbX11_BM, X11SurfaceData.UShort555RgbX11, true), new X11PMBlitLoops(X11SurfaceData.UShort565RgbX11_BM, X11SurfaceData.UShort565RgbX11, true), new X11PMBlitLoops(X11SurfaceData.UShortIndexedX11_BM, X11SurfaceData.UShortIndexedX11, true), new X11PMBlitLoops(X11SurfaceData.IntRgbX11, X11SurfaceData.IntArgbPreX11, true), new X11PMBlitLoops(X11SurfaceData.IntRgbX11, X11SurfaceData.IntArgbPreX11, false), new X11PMBlitLoops(X11SurfaceData.IntRgbX11_BM, X11SurfaceData.IntArgbPreX11, true), new X11PMBlitLoops(X11SurfaceData.IntBgrX11, X11SurfaceData.FourByteAbgrPreX11, true), new X11PMBlitLoops(X11SurfaceData.IntBgrX11, X11SurfaceData.FourByteAbgrPreX11, false), new X11PMBlitLoops(X11SurfaceData.IntBgrX11_BM, X11SurfaceData.FourByteAbgrPreX11, true), new X11PMBlitLoops.DelegateBlitLoop(X11SurfaceData.IntBgrX11_BM, X11SurfaceData.IntBgrX11), new X11PMBlitLoops.DelegateBlitLoop(X11SurfaceData.IntRgbX11_BM, X11SurfaceData.IntRgbX11), new X11PMBlitLoops.DelegateBlitLoop(X11SurfaceData.ThreeByteBgrX11_BM, X11SurfaceData.ThreeByteBgrX11), new X11PMBlitLoops.DelegateBlitLoop(X11SurfaceData.ThreeByteRgbX11_BM, X11SurfaceData.ThreeByteRgbX11), new X11PMBlitLoops.DelegateBlitLoop(X11SurfaceData.ByteIndexedX11_BM, X11SurfaceData.ByteIndexedOpaqueX11), new X11PMBlitLoops.DelegateBlitLoop(X11SurfaceData.ByteGrayX11_BM, X11SurfaceData.ByteGrayX11), new X11PMBlitLoops.DelegateBlitLoop(X11SurfaceData.Index8GrayX11_BM, X11SurfaceData.Index8GrayX11), new X11PMBlitLoops.DelegateBlitLoop(X11SurfaceData.UShort555RgbX11_BM, X11SurfaceData.UShort555RgbX11), new X11PMBlitLoops.DelegateBlitLoop(X11SurfaceData.UShort565RgbX11_BM, X11SurfaceData.UShort565RgbX11), new X11PMBlitLoops.DelegateBlitLoop(X11SurfaceData.UShortIndexedX11_BM, X11SurfaceData.UShortIndexedX11)};
      GraphicsPrimitiveMgr.register(var0);
   }

   public X11PMBlitLoops(SurfaceType var1, SurfaceType var2, boolean var3) {
      super(var1, var3 ? CompositeType.SrcOverNoEa : CompositeType.SrcNoEa, var2);
   }

   public void Blit(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      SunToolkit.awtLock();

      try {
         X11SurfaceData var11 = (X11SurfaceData)var2;
         long var12 = var11.getBlitGC((Region)null, false);
         this.nativeBlit(var1.getNativeOps(), var2.getNativeOps(), var12, var4, var5, var6, var7, var8, var9, var10);
      } finally {
         SunToolkit.awtUnlock();
      }

   }

   private native void nativeBlit(long var1, long var3, long var5, Region var7, int var8, int var9, int var10, int var11, int var12, int var13);

   private static native void updateBitmask(SurfaceData var0, SurfaceData var1, boolean var2);

   static class DelegateBlitLoop extends Blit {
      SurfaceType dstType;

      public DelegateBlitLoop(SurfaceType var1, SurfaceType var2) {
         super(SurfaceType.Any, CompositeType.SrcNoEa, var1);
         this.dstType = var2;
      }

      public void Blit(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, int var9, int var10) {
         Blit var11 = Blit.getFromCache(var1.getSurfaceType(), CompositeType.SrcNoEa, this.dstType);
         var11.Blit(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
         X11PMBlitLoops.updateBitmask(var1, var2, var1.getColorModel() instanceof IndexColorModel);
      }
   }
}
