package sun.print;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;

class ImagePrinter implements Printable {
   BufferedImage image;

   ImagePrinter(InputStream var1) {
      try {
         this.image = ImageIO.read(var1);
      } catch (Exception var3) {
      }

   }

   ImagePrinter(URL var1) {
      try {
         this.image = ImageIO.read(var1);
      } catch (Exception var3) {
      }

   }

   public int print(Graphics var1, PageFormat var2, int var3) {
      if (var3 <= 0 && this.image != null) {
         ((Graphics2D)var1).translate(var2.getImageableX(), var2.getImageableY());
         int var4 = this.image.getWidth((ImageObserver)null);
         int var5 = this.image.getHeight((ImageObserver)null);
         int var6 = (int)var2.getImageableWidth();
         int var7 = (int)var2.getImageableHeight();
         int var8 = var4;
         int var9 = var5;
         if (var4 > var6) {
            var9 = (int)((float)var5 * ((float)var6 / (float)var4));
            var8 = var6;
         }

         if (var9 > var7) {
            var8 = (int)((float)var8 * ((float)var7 / (float)var9));
            var9 = var7;
         }

         int var10 = (var6 - var8) / 2;
         int var11 = (var7 - var9) / 2;
         var1.drawImage(this.image, var10, var11, var10 + var8, var11 + var9, 0, 0, var4, var5, (ImageObserver)null);
         return 0;
      } else {
         return 1;
      }
   }
}
