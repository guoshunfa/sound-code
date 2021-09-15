package sun.java2d.pipe;

import java.awt.AlphaComposite;
import java.awt.CompositeContext;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Hashtable;
import sun.awt.image.BufImgSurfaceData;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.MaskBlit;

public class GeneralCompositePipe implements CompositePipe {
   public Object startSequence(SunGraphics2D var1, Shape var2, Rectangle var3, int[] var4) {
      RenderingHints var5 = var1.getRenderingHints();
      ColorModel var6 = var1.getDeviceColorModel();
      PaintContext var7 = var1.paint.createContext(var6, var3, var2.getBounds2D(), var1.cloneTransform(), var5);
      CompositeContext var8 = var1.composite.createContext(var7.getColorModel(), var6, var5);
      return new GeneralCompositePipe.TileContext(var1, var7, var8, var6);
   }

   public boolean needTile(Object var1, int var2, int var3, int var4, int var5) {
      return true;
   }

   public void renderPathTile(Object var1, byte[] var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      GeneralCompositePipe.TileContext var9 = (GeneralCompositePipe.TileContext)var1;
      PaintContext var10 = var9.paintCtxt;
      CompositeContext var11 = var9.compCtxt;
      SunGraphics2D var12 = var9.sunG2D;
      Raster var13 = var10.getRaster(var5, var6, var7, var8);
      ColorModel var14 = var10.getColorModel();
      SurfaceData var18 = var12.getSurfaceData();
      Raster var15 = var18.getRaster(var5, var6, var7, var8);
      Object var16;
      WritableRaster var17;
      if (var15 instanceof WritableRaster && var2 == null) {
         var17 = (WritableRaster)var15;
         var17 = var17.createWritableChild(var5, var6, var7, var8, 0, 0, (int[])null);
         var16 = var17;
      } else {
         var16 = var15.createChild(var5, var6, var7, var8, 0, 0, (int[])null);
         var17 = ((Raster)var16).createCompatibleWritableRaster();
      }

      var11.compose(var13, (Raster)var16, var17);
      if (var15 != var17 && var17.getParent() != var15) {
         if (var15 instanceof WritableRaster && var2 == null) {
            ((WritableRaster)var15).setDataElements(var5, var6, (Raster)var17);
         } else {
            ColorModel var19 = var12.getDeviceColorModel();
            BufferedImage var20 = new BufferedImage(var19, var17, var19.isAlphaPremultiplied(), (Hashtable)null);
            SurfaceData var21 = BufImgSurfaceData.createData(var20);
            if (var2 == null) {
               Blit var22 = Blit.getFromCache(var21.getSurfaceType(), CompositeType.SrcNoEa, var18.getSurfaceType());
               var22.Blit(var21, var18, AlphaComposite.Src, (Region)null, 0, 0, var5, var6, var7, var8);
            } else {
               MaskBlit var23 = MaskBlit.getFromCache(var21.getSurfaceType(), CompositeType.SrcNoEa, var18.getSurfaceType());
               var23.MaskBlit(var21, var18, AlphaComposite.Src, (Region)null, 0, 0, var5, var6, var7, var8, var2, var3, var4);
            }
         }
      }

   }

   public void skipTile(Object var1, int var2, int var3) {
   }

   public void endSequence(Object var1) {
      GeneralCompositePipe.TileContext var2 = (GeneralCompositePipe.TileContext)var1;
      if (var2.paintCtxt != null) {
         var2.paintCtxt.dispose();
      }

      if (var2.compCtxt != null) {
         var2.compCtxt.dispose();
      }

   }

   class TileContext {
      SunGraphics2D sunG2D;
      PaintContext paintCtxt;
      CompositeContext compCtxt;
      ColorModel compModel;
      Object pipeState;

      public TileContext(SunGraphics2D var2, PaintContext var3, CompositeContext var4, ColorModel var5) {
         this.sunG2D = var2;
         this.paintCtxt = var3;
         this.compCtxt = var4;
         this.compModel = var5;
      }
   }
}
