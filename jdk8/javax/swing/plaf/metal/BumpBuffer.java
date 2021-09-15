package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;

class BumpBuffer {
   static final int IMAGE_SIZE = 64;
   transient Image image;
   Color topColor;
   Color shadowColor;
   Color backColor;
   private GraphicsConfiguration gc;

   public BumpBuffer(GraphicsConfiguration var1, Color var2, Color var3, Color var4) {
      this.gc = var1;
      this.topColor = var2;
      this.shadowColor = var3;
      this.backColor = var4;
      this.createImage();
      this.fillBumpBuffer();
   }

   public boolean hasSameConfiguration(GraphicsConfiguration var1, Color var2, Color var3, Color var4) {
      if (this.gc != null) {
         if (!this.gc.equals(var1)) {
            return false;
         }
      } else if (var1 != null) {
         return false;
      }

      return this.topColor.equals(var2) && this.shadowColor.equals(var3) && this.backColor.equals(var4);
   }

   public Image getImage() {
      return this.image;
   }

   private void fillBumpBuffer() {
      Graphics var1 = this.image.getGraphics();
      var1.setColor(this.backColor);
      var1.fillRect(0, 0, 64, 64);
      var1.setColor(this.topColor);

      int var2;
      int var3;
      for(var2 = 0; var2 < 64; var2 += 4) {
         for(var3 = 0; var3 < 64; var3 += 4) {
            var1.drawLine(var2, var3, var2, var3);
            var1.drawLine(var2 + 2, var3 + 2, var2 + 2, var3 + 2);
         }
      }

      var1.setColor(this.shadowColor);

      for(var2 = 0; var2 < 64; var2 += 4) {
         for(var3 = 0; var3 < 64; var3 += 4) {
            var1.drawLine(var2 + 1, var3 + 1, var2 + 1, var3 + 1);
            var1.drawLine(var2 + 3, var3 + 3, var2 + 3, var3 + 3);
         }
      }

      var1.dispose();
   }

   private void createImage() {
      if (this.gc != null) {
         this.image = this.gc.createCompatibleImage(64, 64, this.backColor != MetalBumps.ALPHA ? 1 : 2);
      } else {
         int[] var1 = new int[]{this.backColor.getRGB(), this.topColor.getRGB(), this.shadowColor.getRGB()};
         IndexColorModel var2 = new IndexColorModel(8, 3, var1, 0, false, this.backColor == MetalBumps.ALPHA ? 0 : -1, 0);
         this.image = new BufferedImage(64, 64, 13, var2);
      }

   }
}
