package java.awt;

import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import sun.awt.image.IntegerComponentRaster;

class ColorPaintContext implements PaintContext {
   int color;
   WritableRaster savedTile;

   protected ColorPaintContext(int var1, ColorModel var2) {
      this.color = var1;
   }

   public void dispose() {
   }

   int getRGB() {
      return this.color;
   }

   public ColorModel getColorModel() {
      return ColorModel.getRGBdefault();
   }

   public synchronized Raster getRaster(int var1, int var2, int var3, int var4) {
      WritableRaster var5 = this.savedTile;
      if (var5 == null || var3 > var5.getWidth() || var4 > var5.getHeight()) {
         var5 = this.getColorModel().createCompatibleWritableRaster(var3, var4);
         IntegerComponentRaster var6 = (IntegerComponentRaster)var5;
         Arrays.fill(var6.getDataStorage(), this.color);
         var6.markDirty();
         if (var3 <= 64 && var4 <= 64) {
            this.savedTile = var5;
         }
      }

      return var5;
   }
}
