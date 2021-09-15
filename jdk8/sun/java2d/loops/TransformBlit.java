package sun.java2d.loops;

import java.awt.Composite;
import java.awt.geom.AffineTransform;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

public class TransformBlit extends GraphicsPrimitive {
   public static final String methodSignature = "TransformBlit(...)".toString();
   public static final int primTypeID = makePrimTypeID();
   private static RenderCache blitcache = new RenderCache(10);

   public static TransformBlit locate(SurfaceType var0, CompositeType var1, SurfaceType var2) {
      return (TransformBlit)GraphicsPrimitiveMgr.locate(primTypeID, var0, var1, var2);
   }

   public static TransformBlit getFromCache(SurfaceType var0, CompositeType var1, SurfaceType var2) {
      Object var3 = blitcache.get(var0, var1, var2);
      if (var3 != null) {
         return (TransformBlit)var3;
      } else {
         TransformBlit var4 = locate(var0, var1, var2);
         if (var4 != null) {
            blitcache.put(var0, var1, var2, var4);
         }

         return var4;
      }
   }

   protected TransformBlit(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      super(methodSignature, primTypeID, var1, var2, var3);
   }

   public TransformBlit(long var1, SurfaceType var3, CompositeType var4, SurfaceType var5) {
      super(var1, methodSignature, primTypeID, var3, var4, var5);
   }

   public native void Transform(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, AffineTransform var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12);

   public GraphicsPrimitive makePrimitive(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      return null;
   }

   public GraphicsPrimitive traceWrap() {
      return new TransformBlit.TraceTransformBlit(this);
   }

   static {
      GraphicsPrimitiveMgr.registerGeneral(new TransformBlit((SurfaceType)null, (CompositeType)null, (SurfaceType)null));
   }

   private static class TraceTransformBlit extends TransformBlit {
      TransformBlit target;

      public TraceTransformBlit(TransformBlit var1) {
         super(var1.getSourceType(), var1.getCompositeType(), var1.getDestType());
         this.target = var1;
      }

      public GraphicsPrimitive traceWrap() {
         return this;
      }

      public void Transform(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, AffineTransform var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12) {
         tracePrimitive(this.target);
         this.target.Transform(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
      }
   }
}
