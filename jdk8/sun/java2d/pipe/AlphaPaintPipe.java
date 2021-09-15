package sun.java2d.pipe;

import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.lang.ref.WeakReference;
import java.util.Hashtable;
import sun.awt.image.BufImgSurfaceData;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.MaskBlit;

public class AlphaPaintPipe implements CompositePipe {
   static WeakReference cachedLastRaster;
   static WeakReference cachedLastColorModel;
   static WeakReference cachedLastData;
   private static final int TILE_SIZE = 32;

   public Object startSequence(SunGraphics2D var1, Shape var2, Rectangle var3, int[] var4) {
      PaintContext var5 = var1.paint.createContext(var1.getDeviceColorModel(), var3, var2.getBounds2D(), var1.cloneTransform(), var1.getRenderingHints());
      return new AlphaPaintPipe.TileContext(var1, var5);
   }

   public boolean needTile(Object var1, int var2, int var3, int var4, int var5) {
      return true;
   }

   public void renderPathTile(Object var1, byte[] var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      AlphaPaintPipe.TileContext var9 = (AlphaPaintPipe.TileContext)var1;
      PaintContext var10 = var9.paintCtxt;
      SunGraphics2D var11 = var9.sunG2D;
      SurfaceData var12 = var9.dstData;
      SurfaceData var13 = null;
      Raster var14 = null;
      if (var9.lastData != null && var9.lastRaster != null) {
         var13 = (SurfaceData)var9.lastData.get();
         var14 = (Raster)var9.lastRaster.get();
         if (var13 == null || var14 == null) {
            var13 = null;
            var14 = null;
         }
      }

      ColorModel var15 = var9.paintModel;

      for(int var16 = 0; var16 < var8; var16 += 32) {
         int var17 = var6 + var16;
         int var18 = Math.min(var8 - var16, 32);

         for(int var19 = 0; var19 < var7; var19 += 32) {
            int var20 = var5 + var19;
            int var21 = Math.min(var7 - var19, 32);
            Raster var22 = var10.getRaster(var20, var17, var21, var18);
            if (var22.getMinX() != 0 || var22.getMinY() != 0) {
               var22 = var22.createTranslatedChild(0, 0);
            }

            if (var14 != var22) {
               var14 = var22;
               var9.lastRaster = new WeakReference(var22);
               BufferedImage var23 = new BufferedImage(var15, (WritableRaster)var22, var15.isAlphaPremultiplied(), (Hashtable)null);
               var13 = BufImgSurfaceData.createData(var23);
               var9.lastData = new WeakReference(var13);
               var9.lastMask = null;
               var9.lastBlit = null;
            }

            CompositeType var24;
            if (var2 == null) {
               if (var9.lastBlit == null) {
                  var24 = var11.imageComp;
                  if (CompositeType.SrcOverNoEa.equals(var24) && var15.getTransparency() == 1) {
                     var24 = CompositeType.SrcNoEa;
                  }

                  var9.lastBlit = Blit.getFromCache(var13.getSurfaceType(), var24, var12.getSurfaceType());
               }

               var9.lastBlit.Blit(var13, var12, var11.composite, (Region)null, 0, 0, var20, var17, var21, var18);
            } else {
               if (var9.lastMask == null) {
                  var24 = var11.imageComp;
                  if (CompositeType.SrcOverNoEa.equals(var24) && var15.getTransparency() == 1) {
                     var24 = CompositeType.SrcNoEa;
                  }

                  var9.lastMask = MaskBlit.getFromCache(var13.getSurfaceType(), var24, var12.getSurfaceType());
               }

               int var25 = var3 + var16 * var4 + var19;
               var9.lastMask.MaskBlit(var13, var12, var11.composite, (Region)null, 0, 0, var20, var17, var21, var18, var2, var25, var4);
            }
         }
      }

   }

   public void skipTile(Object var1, int var2, int var3) {
   }

   public void endSequence(Object var1) {
      AlphaPaintPipe.TileContext var2 = (AlphaPaintPipe.TileContext)var1;
      if (var2.paintCtxt != null) {
         var2.paintCtxt.dispose();
      }

      Class var3 = AlphaPaintPipe.class;
      synchronized(AlphaPaintPipe.class) {
         if (var2.lastData != null) {
            cachedLastRaster = var2.lastRaster;
            if (cachedLastColorModel == null || cachedLastColorModel.get() != var2.paintModel) {
               cachedLastColorModel = new WeakReference(var2.paintModel);
            }

            cachedLastData = var2.lastData;
         }

      }
   }

   static class TileContext {
      SunGraphics2D sunG2D;
      PaintContext paintCtxt;
      ColorModel paintModel;
      WeakReference lastRaster;
      WeakReference lastData;
      MaskBlit lastMask;
      Blit lastBlit;
      SurfaceData dstData;

      public TileContext(SunGraphics2D var1, PaintContext var2) {
         this.sunG2D = var1;
         this.paintCtxt = var2;
         this.paintModel = var2.getColorModel();
         this.dstData = var1.getSurfaceData();
         Class var3 = AlphaPaintPipe.class;
         synchronized(AlphaPaintPipe.class) {
            if (AlphaPaintPipe.cachedLastColorModel != null && AlphaPaintPipe.cachedLastColorModel.get() == this.paintModel) {
               this.lastRaster = AlphaPaintPipe.cachedLastRaster;
               this.lastData = AlphaPaintPipe.cachedLastData;
            }

         }
      }
   }
}
