package sun.java2d.loops;

import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class DrawGlyphListLCD extends GraphicsPrimitive {
   public static final String methodSignature = "DrawGlyphListLCD(...)".toString();
   public static final int primTypeID = makePrimTypeID();

   public static DrawGlyphListLCD locate(SurfaceType var0, CompositeType var1, SurfaceType var2) {
      return (DrawGlyphListLCD)GraphicsPrimitiveMgr.locate(primTypeID, var0, var1, var2);
   }

   protected DrawGlyphListLCD(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      super(methodSignature, primTypeID, var1, var2, var3);
   }

   public DrawGlyphListLCD(long var1, SurfaceType var3, CompositeType var4, SurfaceType var5) {
      super(var1, methodSignature, primTypeID, var3, var4, var5);
   }

   public native void DrawGlyphListLCD(SunGraphics2D var1, SurfaceData var2, GlyphList var3);

   public GraphicsPrimitive makePrimitive(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      return null;
   }

   public GraphicsPrimitive traceWrap() {
      return new DrawGlyphListLCD.TraceDrawGlyphListLCD(this);
   }

   static {
      GraphicsPrimitiveMgr.registerGeneral(new DrawGlyphListLCD((SurfaceType)null, (CompositeType)null, (SurfaceType)null));
   }

   private static class TraceDrawGlyphListLCD extends DrawGlyphListLCD {
      DrawGlyphListLCD target;

      public TraceDrawGlyphListLCD(DrawGlyphListLCD var1) {
         super(var1.getSourceType(), var1.getCompositeType(), var1.getDestType());
         this.target = var1;
      }

      public GraphicsPrimitive traceWrap() {
         return this;
      }

      public void DrawGlyphListLCD(SunGraphics2D var1, SurfaceData var2, GlyphList var3) {
         tracePrimitive(this.target);
         this.target.DrawGlyphListLCD(var1, var2, var3);
      }
   }
}
