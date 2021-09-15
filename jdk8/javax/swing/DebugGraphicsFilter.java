package javax.swing;

import java.awt.Color;
import java.awt.image.RGBImageFilter;

class DebugGraphicsFilter extends RGBImageFilter {
   Color color;

   DebugGraphicsFilter(Color var1) {
      this.canFilterIndexColorModel = true;
      this.color = var1;
   }

   public int filterRGB(int var1, int var2, int var3) {
      return this.color.getRGB() | var3 & -16777216;
   }
}
