package sun.java2d.loops;

import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class FillRect extends GraphicsPrimitive {
   public static final String methodSignature = "FillRect(...)".toString();
   public static final int primTypeID = makePrimTypeID();

   public static FillRect locate(SurfaceType var0, CompositeType var1, SurfaceType var2) {
      return (FillRect)GraphicsPrimitiveMgr.locate(primTypeID, var0, var1, var2);
   }

   protected FillRect(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      super(methodSignature, primTypeID, var1, var2, var3);
   }

   public FillRect(long var1, SurfaceType var3, CompositeType var4, SurfaceType var5) {
      super(var1, methodSignature, primTypeID, var3, var4, var5);
   }

   public native void FillRect(SunGraphics2D var1, SurfaceData var2, int var3, int var4, int var5, int var6);

   public GraphicsPrimitive makePrimitive(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      return new FillRect.General(var1, var2, var3);
   }

   public GraphicsPrimitive traceWrap() {
      return new FillRect.TraceFillRect(this);
   }

   static {
      GraphicsPrimitiveMgr.registerGeneral(new FillRect((SurfaceType)null, (CompositeType)null, (SurfaceType)null));
   }

   private static class TraceFillRect extends FillRect {
      FillRect target;

      public TraceFillRect(FillRect var1) {
         super(var1.getSourceType(), var1.getCompositeType(), var1.getDestType());
         this.target = var1;
      }

      public GraphicsPrimitive traceWrap() {
         return this;
      }

      public void FillRect(SunGraphics2D var1, SurfaceData var2, int var3, int var4, int var5, int var6) {
         tracePrimitive(this.target);
         this.target.FillRect(var1, var2, var3, var4, var5, var6);
      }
   }

   public static class General extends FillRect {
      public MaskFill fillop;

      public General(SurfaceType var1, CompositeType var2, SurfaceType var3) {
         super(var1, var2, var3);
         this.fillop = MaskFill.locate(var1, var2, var3);
      }

      public void FillRect(SunGraphics2D var1, SurfaceData var2, int var3, int var4, int var5, int var6) {
         this.fillop.MaskFill(var1, var2, var1.composite, var3, var4, var5, var6, (byte[])null, 0, 0);
      }
   }
}
