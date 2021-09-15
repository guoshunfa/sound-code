package sun.java2d.loops;

import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class DrawLine extends GraphicsPrimitive {
   public static final String methodSignature = "DrawLine(...)".toString();
   public static final int primTypeID = makePrimTypeID();

   public static DrawLine locate(SurfaceType var0, CompositeType var1, SurfaceType var2) {
      return (DrawLine)GraphicsPrimitiveMgr.locate(primTypeID, var0, var1, var2);
   }

   protected DrawLine(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      super(methodSignature, primTypeID, var1, var2, var3);
   }

   public DrawLine(long var1, SurfaceType var3, CompositeType var4, SurfaceType var5) {
      super(var1, methodSignature, primTypeID, var3, var4, var5);
   }

   public native void DrawLine(SunGraphics2D var1, SurfaceData var2, int var3, int var4, int var5, int var6);

   public GraphicsPrimitive makePrimitive(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      throw new InternalError("DrawLine not implemented for " + var1 + " with " + var2);
   }

   public GraphicsPrimitive traceWrap() {
      return new DrawLine.TraceDrawLine(this);
   }

   private static class TraceDrawLine extends DrawLine {
      DrawLine target;

      public TraceDrawLine(DrawLine var1) {
         super(var1.getSourceType(), var1.getCompositeType(), var1.getDestType());
         this.target = var1;
      }

      public GraphicsPrimitive traceWrap() {
         return this;
      }

      public void DrawLine(SunGraphics2D var1, SurfaceData var2, int var3, int var4, int var5, int var6) {
         tracePrimitive(this.target);
         this.target.DrawLine(var1, var2, var3, var4, var5, var6);
      }
   }
}
