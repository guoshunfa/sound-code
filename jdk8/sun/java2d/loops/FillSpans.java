package sun.java2d.loops;

import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.SpanIterator;

public class FillSpans extends GraphicsPrimitive {
   public static final String methodSignature = "FillSpans(...)".toString();
   public static final int primTypeID = makePrimTypeID();

   public static FillSpans locate(SurfaceType var0, CompositeType var1, SurfaceType var2) {
      return (FillSpans)GraphicsPrimitiveMgr.locate(primTypeID, var0, var1, var2);
   }

   protected FillSpans(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      super(methodSignature, primTypeID, var1, var2, var3);
   }

   public FillSpans(long var1, SurfaceType var3, CompositeType var4, SurfaceType var5) {
      super(var1, methodSignature, primTypeID, var3, var4, var5);
   }

   private native void FillSpans(SunGraphics2D var1, SurfaceData var2, int var3, long var4, SpanIterator var6);

   public void FillSpans(SunGraphics2D var1, SurfaceData var2, SpanIterator var3) {
      this.FillSpans(var1, var2, var1.pixel, var3.getNativeIterator(), var3);
   }

   public GraphicsPrimitive makePrimitive(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      throw new InternalError("FillSpans not implemented for " + var1 + " with " + var2);
   }

   public GraphicsPrimitive traceWrap() {
      return new FillSpans.TraceFillSpans(this);
   }

   private static class TraceFillSpans extends FillSpans {
      FillSpans target;

      public TraceFillSpans(FillSpans var1) {
         super(var1.getSourceType(), var1.getCompositeType(), var1.getDestType());
         this.target = var1;
      }

      public GraphicsPrimitive traceWrap() {
         return this;
      }

      public void FillSpans(SunGraphics2D var1, SurfaceData var2, SpanIterator var3) {
         tracePrimitive(this.target);
         this.target.FillSpans(var1, var2, var3);
      }
   }
}
