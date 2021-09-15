package sun.java2d.loops;

import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class FillParallelogram extends GraphicsPrimitive {
   public static final String methodSignature = "FillParallelogram(...)".toString();
   public static final int primTypeID = makePrimTypeID();

   public static FillParallelogram locate(SurfaceType var0, CompositeType var1, SurfaceType var2) {
      return (FillParallelogram)GraphicsPrimitiveMgr.locate(primTypeID, var0, var1, var2);
   }

   protected FillParallelogram(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      super(methodSignature, primTypeID, var1, var2, var3);
   }

   public FillParallelogram(long var1, SurfaceType var3, CompositeType var4, SurfaceType var5) {
      super(var1, methodSignature, primTypeID, var3, var4, var5);
   }

   public native void FillParallelogram(SunGraphics2D var1, SurfaceData var2, double var3, double var5, double var7, double var9, double var11, double var13);

   public GraphicsPrimitive makePrimitive(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      throw new InternalError("FillParallelogram not implemented for " + var1 + " with " + var2);
   }

   public GraphicsPrimitive traceWrap() {
      return new FillParallelogram.TraceFillParallelogram(this);
   }

   private static class TraceFillParallelogram extends FillParallelogram {
      FillParallelogram target;

      public TraceFillParallelogram(FillParallelogram var1) {
         super(var1.getSourceType(), var1.getCompositeType(), var1.getDestType());
         this.target = var1;
      }

      public GraphicsPrimitive traceWrap() {
         return this;
      }

      public void FillParallelogram(SunGraphics2D var1, SurfaceData var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         tracePrimitive(this.target);
         this.target.FillParallelogram(var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
