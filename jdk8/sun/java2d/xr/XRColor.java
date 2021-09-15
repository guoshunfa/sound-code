package sun.java2d.xr;

import java.awt.Color;

public class XRColor {
   public static final XRColor FULL_ALPHA = new XRColor(65535, 0, 0, 0);
   public static final XRColor NO_ALPHA = new XRColor(0, 0, 0, 0);
   int red;
   int green;
   int blue;
   int alpha;

   public XRColor() {
      this.red = 0;
      this.green = 0;
      this.blue = 0;
      this.alpha = 0;
   }

   public XRColor(int var1, int var2, int var3, int var4) {
      this.alpha = var1;
      this.red = var2;
      this.green = var3;
      this.blue = var4;
   }

   public XRColor(Color var1) {
      this.setColorValues(var1);
   }

   public void setColorValues(Color var1) {
      this.alpha = byteToXRColorValue(var1.getAlpha());
      this.red = byteToXRColorValue((int)((double)(var1.getRed() * var1.getAlpha()) / 255.0D));
      this.green = byteToXRColorValue((int)((double)(var1.getGreen() * var1.getAlpha()) / 255.0D));
      this.blue = byteToXRColorValue((int)((double)(var1.getBlue() * var1.getAlpha()) / 255.0D));
   }

   public static int[] ARGBPrePixelToXRColors(int[] var0) {
      int[] var1 = new int[var0.length * 4];
      XRColor var2 = new XRColor();

      for(int var3 = 0; var3 < var0.length; ++var3) {
         var2.setColorValues(var0[var3], true);
         var1[var3 * 4 + 0] = var2.alpha;
         var1[var3 * 4 + 1] = var2.red;
         var1[var3 * 4 + 2] = var2.green;
         var1[var3 * 4 + 3] = var2.blue;
      }

      return var1;
   }

   public void setColorValues(int var1, boolean var2) {
      long var3 = XRUtils.intToULong(var1);
      this.alpha = (int)(((var3 & -16777216L) >> 16) + 255L);
      this.red = (int)(((var3 & 16711680L) >> 8) + 255L);
      this.green = (int)(((var3 & 65280L) >> 0) + 255L);
      this.blue = (int)(((var3 & 255L) << 8) + 255L);
      if (this.alpha == 255) {
         this.alpha = 0;
      }

      if (!var2) {
         double var5 = XRUtils.XFixedToDouble(this.alpha);
         this.red = (int)((double)this.red * var5);
         this.green = (int)((double)this.green * var5);
         this.blue = (int)((double)this.blue * var5);
      }

   }

   public static int byteToXRColorValue(int var0) {
      int var1 = 0;
      if (var0 != 0) {
         if (var0 == 255) {
            var1 = 65535;
         } else {
            var1 = (var0 << 8) + 255;
         }
      }

      return var1;
   }

   public String toString() {
      return "A:" + this.alpha + "  R:" + this.red + "  G:" + this.green + " B:" + this.blue;
   }

   public void setAlpha(int var1) {
      this.alpha = var1;
   }

   public int getAlpha() {
      return this.alpha;
   }

   public int getRed() {
      return this.red;
   }

   public int getGreen() {
      return this.green;
   }

   public int getBlue() {
      return this.blue;
   }
}
