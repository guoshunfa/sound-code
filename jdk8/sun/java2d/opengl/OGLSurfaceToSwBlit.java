package sun.java2d.opengl;

import java.awt.Composite;
import java.lang.ref.WeakReference;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.RenderBuffer;

final class OGLSurfaceToSwBlit extends Blit {
   private final int typeval;
   private WeakReference<SurfaceData> srcTmp;

   OGLSurfaceToSwBlit(SurfaceType var1, int var2) {
      super(OGLSurfaceData.OpenGLSurface, CompositeType.SrcNoEa, var1);
      this.typeval = var2;
   }

   private synchronized void complexClipBlit(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      SurfaceData var11 = null;
      if (this.srcTmp != null) {
         var11 = (SurfaceData)this.srcTmp.get();
      }

      int var12 = this.typeval == 1 ? 3 : 2;
      var1 = convertFrom(this, var1, var5, var6, var9, var10, var11, var12);
      Blit var13 = Blit.getFromCache(var1.getSurfaceType(), CompositeType.SrcNoEa, var2.getSurfaceType());
      var13.Blit(var1, var2, var3, var4, 0, 0, var7, var8, var9, var10);
      if (var1 != var11) {
         this.srcTmp = new WeakReference(var1);
      }

   }

   public void Blit(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      if (var4 != null) {
         var4 = var4.getIntersectionXYWH(var7, var8, var9, var10);
         if (var4.isEmpty()) {
            return;
         }

         var5 += var4.getLoX() - var7;
         var6 += var4.getLoY() - var8;
         var7 = var4.getLoX();
         var8 = var4.getLoY();
         var9 = var4.getWidth();
         var10 = var4.getHeight();
         if (!var4.isRectangular()) {
            this.complexClipBlit(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
            return;
         }
      }

      OGLRenderQueue var11 = OGLRenderQueue.getInstance();
      var11.lock();

      try {
         var11.addReference(var2);
         RenderBuffer var12 = var11.getBuffer();
         OGLContext.validateContext((OGLSurfaceData)var1);
         var11.ensureCapacityAndAlignment(48, 32);
         var12.putInt(34);
         var12.putInt(var5).putInt(var6);
         var12.putInt(var7).putInt(var8);
         var12.putInt(var9).putInt(var10);
         var12.putInt(this.typeval);
         var12.putLong(var1.getNativeOps());
         var12.putLong(var2.getNativeOps());
         var11.flushNow();
      } finally {
         var11.unlock();
      }

   }
}
