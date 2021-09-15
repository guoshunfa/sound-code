package sun.java2d.loops;

import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class DrawParallelogram extends GraphicsPrimitive {
   public static final String methodSignature = "DrawParallelogram(...)".toString();
   public static final int primTypeID = makePrimTypeID();

   public static DrawParallelogram locate(SurfaceType var0, CompositeType var1, SurfaceType var2) {
      return (DrawParallelogram)GraphicsPrimitiveMgr.locate(primTypeID, var0, var1, var2);
   }

   protected DrawParallelogram(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      super(methodSignature, primTypeID, var1, var2, var3);
   }

   public DrawParallelogram(long var1, SurfaceType var3, CompositeType var4, SurfaceType var5) {
      super(var1, methodSignature, primTypeID, var3, var4, var5);
   }

   public native void DrawParallelogram(SunGraphics2D var1, SurfaceData var2, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17);

   public GraphicsPrimitive makePrimitive(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      throw new InternalError("DrawParallelogram not implemented for " + var1 + " with " + var2);
   }

   public GraphicsPrimitive traceWrap() {
      return new DrawParallelogram.TraceDrawParallelogram(this);
   }

   private static class TraceDrawParallelogram extends DrawParallelogram {
      DrawParallelogram target;

      public TraceDrawParallelogram(DrawParallelogram var1) {
         super(var1.getSourceType(), var1.getCompositeType(), var1.getDestType());
         this.target = var1;
      }

      public GraphicsPrimitive traceWrap() {
         return this;
      }

      public void DrawParallelogram(SunGraphics2D var1, SurfaceData var2, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17) {
         tracePrimitive(this.target);
         this.target.DrawParallelogram(var1, var2, var3, var5, var7, var9, var11, var13, var15, var17);
      }
   }
}
