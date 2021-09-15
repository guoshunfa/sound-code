package sun.java2d.pipe;

import java.awt.AlphaComposite;
import java.awt.Composite;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.MaskFill;
import sun.java2d.loops.SurfaceType;

public abstract class BufferedMaskFill extends MaskFill {
   protected final RenderQueue rq;

   protected BufferedMaskFill(RenderQueue var1, SurfaceType var2, CompositeType var3, SurfaceType var4) {
      super(var2, var3, var4);
      this.rq = var1;
   }

   public void MaskFill(SunGraphics2D var1, SurfaceData var2, Composite var3, final int var4, final int var5, final int var6, final int var7, final byte[] var8, final int var9, final int var10) {
      AlphaComposite var11 = (AlphaComposite)var3;
      if (var11.getRule() != 3) {
         var3 = AlphaComposite.SrcOver;
      }

      this.rq.lock();

      try {
         this.validateContext(var1, (Composite)var3, 2);
         int var12;
         if (var8 != null) {
            var12 = var8.length + 3 & -4;
         } else {
            var12 = 0;
         }

         int var13 = 32 + var12;
         RenderBuffer var14 = this.rq.getBuffer();
         if (var13 <= var14.capacity()) {
            if (var13 > var14.remaining()) {
               this.rq.flushNow();
            }

            var14.putInt(32);
            var14.putInt(var4).putInt(var5).putInt(var6).putInt(var7);
            var14.putInt(var9);
            var14.putInt(var10);
            var14.putInt(var12);
            if (var8 != null) {
               int var15 = var12 - var8.length;
               var14.put(var8);
               if (var15 != 0) {
                  var14.position((long)(var14.position() + var15));
               }
            }
         } else {
            this.rq.flushAndInvokeNow(new Runnable() {
               public void run() {
                  BufferedMaskFill.this.maskFill(var4, var5, var6, var7, var9, var10, var8.length, var8);
               }
            });
         }
      } finally {
         this.rq.unlock();
      }

   }

   protected abstract void maskFill(int var1, int var2, int var3, int var4, int var5, int var6, int var7, byte[] var8);

   protected abstract void validateContext(SunGraphics2D var1, Composite var2, int var3);
}
