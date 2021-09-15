package sun.java2d.opengl;

import java.awt.Composite;
import java.lang.ref.WeakReference;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.Region;

class OGLGeneralBlit extends Blit {
   private final Blit performop;
   private WeakReference srcTmp;

   OGLGeneralBlit(SurfaceType var1, CompositeType var2, Blit var3) {
      super(SurfaceType.Any, var2, var1);
      this.performop = var3;
   }

   public synchronized void Blit(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      Blit var11 = Blit.getFromCache(var1.getSurfaceType(), CompositeType.SrcNoEa, SurfaceType.IntArgbPre);
      SurfaceData var12 = null;
      if (this.srcTmp != null) {
         var12 = (SurfaceData)this.srcTmp.get();
      }

      var1 = convertFrom(var11, var1, var5, var6, var9, var10, var12, 3);
      this.performop.Blit(var1, var2, var3, var4, 0, 0, var7, var8, var9, var10);
      if (var1 != var12) {
         this.srcTmp = new WeakReference(var1);
      }

   }
}
