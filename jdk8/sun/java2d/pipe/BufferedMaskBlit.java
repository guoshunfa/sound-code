package sun.java2d.pipe;

import java.awt.AlphaComposite;
import java.awt.Composite;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.MaskBlit;
import sun.java2d.loops.SurfaceType;

public abstract class BufferedMaskBlit extends MaskBlit {
   private static final int ST_INT_ARGB = 0;
   private static final int ST_INT_ARGB_PRE = 1;
   private static final int ST_INT_RGB = 2;
   private static final int ST_INT_BGR = 3;
   private final RenderQueue rq;
   private final int srcTypeVal;
   private Blit blitop;

   protected BufferedMaskBlit(RenderQueue var1, SurfaceType var2, CompositeType var3, SurfaceType var4) {
      super(var2, var3, var4);
      this.rq = var1;
      if (var2 == SurfaceType.IntArgb) {
         this.srcTypeVal = 0;
      } else if (var2 == SurfaceType.IntArgbPre) {
         this.srcTypeVal = 1;
      } else if (var2 == SurfaceType.IntRgb) {
         this.srcTypeVal = 2;
      } else {
         if (var2 != SurfaceType.IntBgr) {
            throw new InternalError("unrecognized source surface type");
         }

         this.srcTypeVal = 3;
      }

   }

   public void MaskBlit(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, int var9, int var10, byte[] var11, int var12, int var13) {
      if (var9 > 0 && var10 > 0) {
         if (var11 == null) {
            if (this.blitop == null) {
               this.blitop = Blit.getFromCache(var1.getSurfaceType(), CompositeType.AnyAlpha, this.getDestType());
            }

            this.blitop.Blit(var1, var2, (Composite)var3, var4, var5, var6, var7, var8, var9, var10);
         } else {
            AlphaComposite var14 = (AlphaComposite)var3;
            if (var14.getRule() != 3) {
               var3 = AlphaComposite.SrcOver;
            }

            this.rq.lock();

            try {
               this.validateContext(var2, (Composite)var3, var4);
               RenderBuffer var15 = this.rq.getBuffer();
               int var16 = 20 + var9 * var10 * 4;
               this.rq.ensureCapacity(var16);
               int var17 = this.enqueueTile(var15.getAddress(), var15.position(), var1, var1.getNativeOps(), this.srcTypeVal, var11, var11.length, var12, var13, var5, var6, var7, var8, var9, var10);
               var15.position((long)var17);
            } finally {
               this.rq.unlock();
            }

         }
      }
   }

   private native int enqueueTile(long var1, int var3, SurfaceData var4, long var5, int var7, byte[] var8, int var9, int var10, int var11, int var12, int var13, int var14, int var15, int var16, int var17);

   protected abstract void validateContext(SurfaceData var1, Composite var2, Region var3);
}
