package sun.java2d.loops;

import java.awt.Composite;
import java.awt.geom.AffineTransform;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

public class TransformHelper extends GraphicsPrimitive {
   public static final String methodSignature = "TransformHelper(...)".toString();
   public static final int primTypeID = makePrimTypeID();
   private static RenderCache helpercache = new RenderCache(10);

   public static TransformHelper locate(SurfaceType var0) {
      return (TransformHelper)GraphicsPrimitiveMgr.locate(primTypeID, var0, CompositeType.SrcNoEa, SurfaceType.IntArgbPre);
   }

   public static synchronized TransformHelper getFromCache(SurfaceType var0) {
      Object var1 = helpercache.get(var0, (CompositeType)null, (SurfaceType)null);
      if (var1 != null) {
         return (TransformHelper)var1;
      } else {
         TransformHelper var2 = locate(var0);
         if (var2 != null) {
            helpercache.put(var0, (CompositeType)null, (SurfaceType)null, var2);
         }

         return var2;
      }
   }

   protected TransformHelper(SurfaceType var1) {
      super(methodSignature, primTypeID, var1, CompositeType.SrcNoEa, SurfaceType.IntArgbPre);
   }

   public TransformHelper(long var1, SurfaceType var3, CompositeType var4, SurfaceType var5) {
      super(var1, methodSignature, primTypeID, var3, var4, var5);
   }

   public native void Transform(MaskBlit var1, SurfaceData var2, SurfaceData var3, Composite var4, Region var5, AffineTransform var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, int var15, int[] var16, int var17, int var18);

   public GraphicsPrimitive makePrimitive(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      return null;
   }

   public GraphicsPrimitive traceWrap() {
      return new TransformHelper.TraceTransformHelper(this);
   }

   private static class TraceTransformHelper extends TransformHelper {
      TransformHelper target;

      public TraceTransformHelper(TransformHelper var1) {
         super(var1.getSourceType());
         this.target = var1;
      }

      public GraphicsPrimitive traceWrap() {
         return this;
      }

      public void Transform(MaskBlit var1, SurfaceData var2, SurfaceData var3, Composite var4, Region var5, AffineTransform var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, int var15, int[] var16, int var17, int var18) {
         tracePrimitive(this.target);
         this.target.Transform(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16, var17, var18);
      }
   }
}
