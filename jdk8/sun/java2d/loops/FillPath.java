package sun.java2d.loops;

import java.awt.geom.Path2D;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class FillPath extends GraphicsPrimitive {
   public static final String methodSignature = "FillPath(...)".toString();
   public static final int primTypeID = makePrimTypeID();

   public static FillPath locate(SurfaceType var0, CompositeType var1, SurfaceType var2) {
      return (FillPath)GraphicsPrimitiveMgr.locate(primTypeID, var0, var1, var2);
   }

   protected FillPath(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      super(methodSignature, primTypeID, var1, var2, var3);
   }

   public FillPath(long var1, SurfaceType var3, CompositeType var4, SurfaceType var5) {
      super(var1, methodSignature, primTypeID, var3, var4, var5);
   }

   public native void FillPath(SunGraphics2D var1, SurfaceData var2, int var3, int var4, Path2D.Float var5);

   public GraphicsPrimitive makePrimitive(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      throw new InternalError("FillPath not implemented for " + var1 + " with " + var2);
   }

   public GraphicsPrimitive traceWrap() {
      return new FillPath.TraceFillPath(this);
   }

   private static class TraceFillPath extends FillPath {
      FillPath target;

      public TraceFillPath(FillPath var1) {
         super(var1.getSourceType(), var1.getCompositeType(), var1.getDestType());
         this.target = var1;
      }

      public GraphicsPrimitive traceWrap() {
         return this;
      }

      public void FillPath(SunGraphics2D var1, SurfaceData var2, int var3, int var4, Path2D.Float var5) {
         tracePrimitive(this.target);
         this.target.FillPath(var1, var2, var3, var4, var5);
      }
   }
}
