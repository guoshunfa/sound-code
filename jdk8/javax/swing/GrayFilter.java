package javax.swing;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

public class GrayFilter extends RGBImageFilter {
   private boolean brighter;
   private int percent;

   public static Image createDisabledImage(Image var0) {
      GrayFilter var1 = new GrayFilter(true, 50);
      FilteredImageSource var2 = new FilteredImageSource(var0.getSource(), var1);
      Image var3 = Toolkit.getDefaultToolkit().createImage((ImageProducer)var2);
      return var3;
   }

   public GrayFilter(boolean var1, int var2) {
      this.brighter = var1;
      this.percent = var2;
      this.canFilterIndexColorModel = true;
   }

   public int filterRGB(int var1, int var2, int var3) {
      int var4 = (int)((0.3D * (double)(var3 >> 16 & 255) + 0.59D * (double)(var3 >> 8 & 255) + 0.11D * (double)(var3 & 255)) / 3.0D);
      if (this.brighter) {
         var4 = 255 - (255 - var4) * (100 - this.percent) / 100;
      } else {
         var4 = var4 * (100 - this.percent) / 100;
      }

      if (var4 < 0) {
         var4 = 0;
      }

      if (var4 > 255) {
         var4 = 255;
      }

      return var3 & -16777216 | var4 << 16 | var4 << 8 | var4 << 0;
   }
}
