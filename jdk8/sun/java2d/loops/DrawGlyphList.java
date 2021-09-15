package sun.java2d.loops;

import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

public class DrawGlyphList extends GraphicsPrimitive {
   public static final String methodSignature = "DrawGlyphList(...)".toString();
   public static final int primTypeID = makePrimTypeID();

   public static DrawGlyphList locate(SurfaceType var0, CompositeType var1, SurfaceType var2) {
      return (DrawGlyphList)GraphicsPrimitiveMgr.locate(primTypeID, var0, var1, var2);
   }

   protected DrawGlyphList(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      super(methodSignature, primTypeID, var1, var2, var3);
   }

   public DrawGlyphList(long var1, SurfaceType var3, CompositeType var4, SurfaceType var5) {
      super(var1, methodSignature, primTypeID, var3, var4, var5);
   }

   public native void DrawGlyphList(SunGraphics2D var1, SurfaceData var2, GlyphList var3);

   public GraphicsPrimitive makePrimitive(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      return new DrawGlyphList.General(var1, var2, var3);
   }

   public GraphicsPrimitive traceWrap() {
      return new DrawGlyphList.TraceDrawGlyphList(this);
   }

   static {
      GraphicsPrimitiveMgr.registerGeneral(new DrawGlyphList((SurfaceType)null, (CompositeType)null, (SurfaceType)null));
   }

   private static class TraceDrawGlyphList extends DrawGlyphList {
      DrawGlyphList target;

      public TraceDrawGlyphList(DrawGlyphList var1) {
         super(var1.getSourceType(), var1.getCompositeType(), var1.getDestType());
         this.target = var1;
      }

      public GraphicsPrimitive traceWrap() {
         return this;
      }

      public void DrawGlyphList(SunGraphics2D var1, SurfaceData var2, GlyphList var3) {
         tracePrimitive(this.target);
         this.target.DrawGlyphList(var1, var2, var3);
      }
   }

   private static class General extends DrawGlyphList {
      MaskFill maskop;

      public General(SurfaceType var1, CompositeType var2, SurfaceType var3) {
         super(var1, var2, var3);
         this.maskop = MaskFill.locate(var1, var2, var3);
      }

      public void DrawGlyphList(SunGraphics2D var1, SurfaceData var2, GlyphList var3) {
         int[] var4 = var3.getBounds();
         int var5 = var3.getNumGlyphs();
         Region var6 = var1.getCompClip();
         int var7 = var6.getLoX();
         int var8 = var6.getLoY();
         int var9 = var6.getHiX();
         int var10 = var6.getHiY();

         for(int var11 = 0; var11 < var5; ++var11) {
            var3.setGlyphIndex(var11);
            int[] var12 = var3.getMetrics();
            int var13 = var12[0];
            int var14 = var12[1];
            int var15 = var12[2];
            int var16 = var13 + var15;
            int var17 = var14 + var12[3];
            int var18 = 0;
            if (var13 < var7) {
               var18 = var7 - var13;
               var13 = var7;
            }

            if (var14 < var8) {
               var18 += (var8 - var14) * var15;
               var14 = var8;
            }

            if (var16 > var9) {
               var16 = var9;
            }

            if (var17 > var10) {
               var17 = var10;
            }

            if (var16 > var13 && var17 > var14) {
               byte[] var19 = var3.getGrayBits();
               this.maskop.MaskFill(var1, var2, var1.composite, var13, var14, var16 - var13, var17 - var14, var19, var18, var15);
            }
         }

      }
   }
}
