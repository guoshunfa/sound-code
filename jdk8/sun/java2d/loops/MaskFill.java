package sun.java2d.loops;

import java.awt.Composite;
import java.awt.image.BufferedImage;
import sun.awt.image.BufImgSurfaceData;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

public class MaskFill extends GraphicsPrimitive {
   public static final String methodSignature = "MaskFill(...)".toString();
   public static final String fillPgramSignature = "FillAAPgram(...)".toString();
   public static final String drawPgramSignature = "DrawAAPgram(...)".toString();
   public static final int primTypeID = makePrimTypeID();
   private static RenderCache fillcache = new RenderCache(10);

   public static MaskFill locate(SurfaceType var0, CompositeType var1, SurfaceType var2) {
      return (MaskFill)GraphicsPrimitiveMgr.locate(primTypeID, var0, var1, var2);
   }

   public static MaskFill locatePrim(SurfaceType var0, CompositeType var1, SurfaceType var2) {
      return (MaskFill)GraphicsPrimitiveMgr.locatePrim(primTypeID, var0, var1, var2);
   }

   public static MaskFill getFromCache(SurfaceType var0, CompositeType var1, SurfaceType var2) {
      Object var3 = fillcache.get(var0, var1, var2);
      if (var3 != null) {
         return (MaskFill)var3;
      } else {
         MaskFill var4 = locatePrim(var0, var1, var2);
         if (var4 != null) {
            fillcache.put(var0, var1, var2, var4);
         }

         return var4;
      }
   }

   protected MaskFill(String var1, SurfaceType var2, CompositeType var3, SurfaceType var4) {
      super(var1, primTypeID, var2, var3, var4);
   }

   protected MaskFill(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      super(methodSignature, primTypeID, var1, var2, var3);
   }

   public MaskFill(long var1, SurfaceType var3, CompositeType var4, SurfaceType var5) {
      super(var1, methodSignature, primTypeID, var3, var4, var5);
   }

   public native void MaskFill(SunGraphics2D var1, SurfaceData var2, Composite var3, int var4, int var5, int var6, int var7, byte[] var8, int var9, int var10);

   public native void FillAAPgram(SunGraphics2D var1, SurfaceData var2, Composite var3, double var4, double var6, double var8, double var10, double var12, double var14);

   public native void DrawAAPgram(SunGraphics2D var1, SurfaceData var2, Composite var3, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18);

   public boolean canDoParallelograms() {
      return this.getNativePrim() != 0L;
   }

   public GraphicsPrimitive makePrimitive(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      if (!SurfaceType.OpaqueColor.equals(var1) && !SurfaceType.AnyColor.equals(var1)) {
         throw new InternalError("MaskFill can only fill with colors");
      } else if (CompositeType.Xor.equals(var2)) {
         throw new InternalError("Cannot construct MaskFill for XOR mode");
      } else {
         return new MaskFill.General(var1, var2, var3);
      }
   }

   public GraphicsPrimitive traceWrap() {
      return new MaskFill.TraceMaskFill(this);
   }

   static {
      GraphicsPrimitiveMgr.registerGeneral(new MaskFill((SurfaceType)null, (CompositeType)null, (SurfaceType)null));
   }

   private static class TraceMaskFill extends MaskFill {
      MaskFill target;
      MaskFill fillPgramTarget;
      MaskFill drawPgramTarget;

      public TraceMaskFill(MaskFill var1) {
         super(var1.getSourceType(), var1.getCompositeType(), var1.getDestType());
         this.target = var1;
         this.fillPgramTarget = new MaskFill(fillPgramSignature, var1.getSourceType(), var1.getCompositeType(), var1.getDestType());
         this.drawPgramTarget = new MaskFill(drawPgramSignature, var1.getSourceType(), var1.getCompositeType(), var1.getDestType());
      }

      public GraphicsPrimitive traceWrap() {
         return this;
      }

      public void MaskFill(SunGraphics2D var1, SurfaceData var2, Composite var3, int var4, int var5, int var6, int var7, byte[] var8, int var9, int var10) {
         tracePrimitive(this.target);
         this.target.MaskFill(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      }

      public void FillAAPgram(SunGraphics2D var1, SurfaceData var2, Composite var3, double var4, double var6, double var8, double var10, double var12, double var14) {
         tracePrimitive(this.fillPgramTarget);
         this.target.FillAAPgram(var1, var2, var3, var4, var6, var8, var10, var12, var14);
      }

      public void DrawAAPgram(SunGraphics2D var1, SurfaceData var2, Composite var3, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18) {
         tracePrimitive(this.drawPgramTarget);
         this.target.DrawAAPgram(var1, var2, var3, var4, var6, var8, var10, var12, var14, var16, var18);
      }

      public boolean canDoParallelograms() {
         return this.target.canDoParallelograms();
      }
   }

   private static class General extends MaskFill {
      FillRect fillop;
      MaskBlit maskop;

      public General(SurfaceType var1, CompositeType var2, SurfaceType var3) {
         super(var1, var2, var3);
         this.fillop = FillRect.locate(var1, CompositeType.SrcNoEa, SurfaceType.IntArgb);
         this.maskop = MaskBlit.locate(SurfaceType.IntArgb, var2, var3);
      }

      public void MaskFill(SunGraphics2D var1, SurfaceData var2, Composite var3, int var4, int var5, int var6, int var7, byte[] var8, int var9, int var10) {
         BufferedImage var11 = new BufferedImage(var6, var7, 2);
         SurfaceData var12 = BufImgSurfaceData.createData(var11);
         Region var13 = var1.clipRegion;
         var1.clipRegion = null;
         int var14 = var1.pixel;
         var1.pixel = var12.pixelFor(var1.getColor());
         this.fillop.FillRect(var1, var12, 0, 0, var6, var7);
         var1.pixel = var14;
         var1.clipRegion = var13;
         this.maskop.MaskBlit(var12, var2, var3, (Region)null, 0, 0, var4, var5, var6, var7, var8, var9, var10);
      }
   }
}
