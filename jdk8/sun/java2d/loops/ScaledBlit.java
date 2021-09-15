package sun.java2d.loops;

import java.awt.Composite;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

public class ScaledBlit extends GraphicsPrimitive {
   public static final String methodSignature = "ScaledBlit(...)".toString();
   public static final int primTypeID = makePrimTypeID();
   private static RenderCache blitcache = new RenderCache(20);

   public static ScaledBlit locate(SurfaceType var0, CompositeType var1, SurfaceType var2) {
      return (ScaledBlit)GraphicsPrimitiveMgr.locate(primTypeID, var0, var1, var2);
   }

   public static ScaledBlit getFromCache(SurfaceType var0, CompositeType var1, SurfaceType var2) {
      Object var3 = blitcache.get(var0, var1, var2);
      if (var3 != null) {
         return (ScaledBlit)var3;
      } else {
         ScaledBlit var4 = locate(var0, var1, var2);
         if (var4 != null) {
            blitcache.put(var0, var1, var2, var4);
         }

         return var4;
      }
   }

   protected ScaledBlit(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      super(methodSignature, primTypeID, var1, var2, var3);
   }

   public ScaledBlit(long var1, SurfaceType var3, CompositeType var4, SurfaceType var5) {
      super(var1, methodSignature, primTypeID, var3, var4, var5);
   }

   public native void Scale(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, double var9, double var11, double var13, double var15);

   public GraphicsPrimitive makePrimitive(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      return null;
   }

   public GraphicsPrimitive traceWrap() {
      return new ScaledBlit.TraceScaledBlit(this);
   }

   static {
      GraphicsPrimitiveMgr.registerGeneral(new ScaledBlit((SurfaceType)null, (CompositeType)null, (SurfaceType)null));
   }

   private static class TraceScaledBlit extends ScaledBlit {
      ScaledBlit target;

      public TraceScaledBlit(ScaledBlit var1) {
         super(var1.getSourceType(), var1.getCompositeType(), var1.getDestType());
         this.target = var1;
      }

      public GraphicsPrimitive traceWrap() {
         return this;
      }

      public void Scale(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, double var9, double var11, double var13, double var15) {
         tracePrimitive(this.target);
         this.target.Scale(var1, var2, var3, var4, var5, var6, var7, var8, var9, var11, var13, var15);
      }
   }
}
