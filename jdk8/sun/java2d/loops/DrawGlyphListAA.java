package sun.java2d.loops;

import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

public class DrawGlyphListAA extends GraphicsPrimitive {
   public static final String methodSignature = "DrawGlyphListAA(...)".toString();
   public static final int primTypeID = makePrimTypeID();

   public static DrawGlyphListAA locate(SurfaceType var0, CompositeType var1, SurfaceType var2) {
      return (DrawGlyphListAA)GraphicsPrimitiveMgr.locate(primTypeID, var0, var1, var2);
   }

   protected DrawGlyphListAA(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      super(methodSignature, primTypeID, var1, var2, var3);
   }

   public DrawGlyphListAA(long var1, SurfaceType var3, CompositeType var4, SurfaceType var5) {
      super(var1, methodSignature, primTypeID, var3, var4, var5);
   }

   public native void DrawGlyphListAA(SunGraphics2D var1, SurfaceData var2, GlyphList var3);

   public GraphicsPrimitive makePrimitive(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      return new DrawGlyphListAA.General(var1, var2, var3);
   }

   public GraphicsPrimitive traceWrap() {
      return new DrawGlyphListAA.TraceDrawGlyphListAA(this);
   }

   static {
      GraphicsPrimitiveMgr.registerGeneral(new DrawGlyphListAA((SurfaceType)null, (CompositeType)null, (SurfaceType)null));
   }

   private static class TraceDrawGlyphListAA extends DrawGlyphListAA {
      DrawGlyphListAA target;

      public TraceDrawGlyphListAA(DrawGlyphListAA var1) {
         super(var1.getSourceType(), var1.getCompositeType(), var1.getDestType());
         this.target = var1;
      }

      public GraphicsPrimitive traceWrap() {
         return this;
      }

      public void DrawGlyphListAA(SunGraphics2D var1, SurfaceData var2, GlyphList var3) {
         tracePrimitive(this.target);
         this.target.DrawGlyphListAA(var1, var2, var3);
      }
   }

   public static class General extends DrawGlyphListAA {
      MaskFill maskop;

      public General(SurfaceType var1, CompositeType var2, SurfaceType var3) {
         super(var1, var2, var3);
         this.maskop = MaskFill.locate(var1, var2, var3);
      }

      public void DrawGlyphListAA(SunGraphics2D var1, SurfaceData var2, GlyphList var3) {
         var3.getBounds();
         int var4 = var3.getNumGlyphs();
         Region var5 = var1.getCompClip();
         int var6 = var5.getLoX();
         int var7 = var5.getLoY();
         int var8 = var5.getHiX();
         int var9 = var5.getHiY();

         for(int var10 = 0; var10 < var4; ++var10) {
            var3.setGlyphIndex(var10);
            int[] var11 = var3.getMetrics();
            int var12 = var11[0];
            int var13 = var11[1];
            int var14 = var11[2];
            int var15 = var12 + var14;
            int var16 = var13 + var11[3];
            int var17 = 0;
            if (var12 < var6) {
               var17 = var6 - var12;
               var12 = var6;
            }

            if (var13 < var7) {
               var17 += (var7 - var13) * var14;
               var13 = var7;
            }

            if (var15 > var8) {
               var15 = var8;
            }

            if (var16 > var9) {
               var16 = var9;
            }

            if (var15 > var12 && var16 > var13) {
               byte[] var18 = var3.getGrayBits();
               this.maskop.MaskFill(var1, var2, var1.composite, var12, var13, var15 - var12, var16 - var13, var18, var17, var14);
            }
         }

      }
   }
}
