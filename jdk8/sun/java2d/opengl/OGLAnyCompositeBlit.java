package sun.java2d.opengl;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.lang.ref.WeakReference;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.Region;

final class OGLAnyCompositeBlit extends Blit {
   private WeakReference<SurfaceData> dstTmp;
   private WeakReference<SurfaceData> srcTmp;
   private final Blit convertsrc;
   private final Blit convertdst;
   private final Blit convertresult;

   OGLAnyCompositeBlit(SurfaceType var1, Blit var2, Blit var3, Blit var4) {
      super(var1, CompositeType.Any, OGLSurfaceData.OpenGLSurface);
      this.convertsrc = var2;
      this.convertdst = var3;
      this.convertresult = var4;
   }

   public synchronized void Blit(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      SurfaceData var11;
      if (this.convertsrc != null) {
         var11 = null;
         if (this.srcTmp != null) {
            var11 = (SurfaceData)this.srcTmp.get();
         }

         var1 = convertFrom(this.convertsrc, var1, var5, var6, var9, var10, var11, 3);
         if (var1 != var11) {
            this.srcTmp = new WeakReference(var1);
         }
      }

      var11 = null;
      if (this.dstTmp != null) {
         var11 = (SurfaceData)this.dstTmp.get();
      }

      SurfaceData var12 = convertFrom(this.convertdst, var2, var7, var8, var9, var10, var11, 3);
      Region var13 = var4 == null ? null : var4.getTranslatedRegion(-var7, -var8);
      Blit var14 = Blit.getFromCache(var1.getSurfaceType(), CompositeType.Any, var12.getSurfaceType());
      var14.Blit(var1, var12, var3, var13, var5, var6, 0, 0, var9, var10);
      if (var12 != var11) {
         this.dstTmp = new WeakReference(var12);
      }

      this.convertresult.Blit(var12, var2, AlphaComposite.Src, var4, 0, 0, var7, var8, var9, var10);
   }
}
