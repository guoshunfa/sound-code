package java.awt;

import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.beans.ConstructorProperties;
import java.io.Serializable;

public class Color implements Paint, Serializable {
   public static final Color white = new Color(255, 255, 255);
   public static final Color WHITE;
   public static final Color lightGray;
   public static final Color LIGHT_GRAY;
   public static final Color gray;
   public static final Color GRAY;
   public static final Color darkGray;
   public static final Color DARK_GRAY;
   public static final Color black;
   public static final Color BLACK;
   public static final Color red;
   public static final Color RED;
   public static final Color pink;
   public static final Color PINK;
   public static final Color orange;
   public static final Color ORANGE;
   public static final Color yellow;
   public static final Color YELLOW;
   public static final Color green;
   public static final Color GREEN;
   public static final Color magenta;
   public static final Color MAGENTA;
   public static final Color cyan;
   public static final Color CYAN;
   public static final Color blue;
   public static final Color BLUE;
   int value;
   private float[] frgbvalue;
   private float[] fvalue;
   private float falpha;
   private ColorSpace cs;
   private static final long serialVersionUID = 118526816881161077L;
   private static final double FACTOR = 0.7D;

   private static native void initIDs();

   private static void testColorValueRange(int var0, int var1, int var2, int var3) {
      boolean var4 = false;
      String var5 = "";
      if (var3 < 0 || var3 > 255) {
         var4 = true;
         var5 = var5 + " Alpha";
      }

      if (var0 < 0 || var0 > 255) {
         var4 = true;
         var5 = var5 + " Red";
      }

      if (var1 < 0 || var1 > 255) {
         var4 = true;
         var5 = var5 + " Green";
      }

      if (var2 < 0 || var2 > 255) {
         var4 = true;
         var5 = var5 + " Blue";
      }

      if (var4) {
         throw new IllegalArgumentException("Color parameter outside of expected range:" + var5);
      }
   }

   private static void testColorValueRange(float var0, float var1, float var2, float var3) {
      boolean var4 = false;
      String var5 = "";
      if ((double)var3 < 0.0D || (double)var3 > 1.0D) {
         var4 = true;
         var5 = var5 + " Alpha";
      }

      if ((double)var0 < 0.0D || (double)var0 > 1.0D) {
         var4 = true;
         var5 = var5 + " Red";
      }

      if ((double)var1 < 0.0D || (double)var1 > 1.0D) {
         var4 = true;
         var5 = var5 + " Green";
      }

      if ((double)var2 < 0.0D || (double)var2 > 1.0D) {
         var4 = true;
         var5 = var5 + " Blue";
      }

      if (var4) {
         throw new IllegalArgumentException("Color parameter outside of expected range:" + var5);
      }
   }

   public Color(int var1, int var2, int var3) {
      this(var1, var2, var3, 255);
   }

   @ConstructorProperties({"red", "green", "blue", "alpha"})
   public Color(int var1, int var2, int var3, int var4) {
      this.frgbvalue = null;
      this.fvalue = null;
      this.falpha = 0.0F;
      this.cs = null;
      this.value = (var4 & 255) << 24 | (var1 & 255) << 16 | (var2 & 255) << 8 | (var3 & 255) << 0;
      testColorValueRange(var1, var2, var3, var4);
   }

   public Color(int var1) {
      this.frgbvalue = null;
      this.fvalue = null;
      this.falpha = 0.0F;
      this.cs = null;
      this.value = -16777216 | var1;
   }

   public Color(int var1, boolean var2) {
      this.frgbvalue = null;
      this.fvalue = null;
      this.falpha = 0.0F;
      this.cs = null;
      if (var2) {
         this.value = var1;
      } else {
         this.value = -16777216 | var1;
      }

   }

   public Color(float var1, float var2, float var3) {
      this((int)((double)(var1 * 255.0F) + 0.5D), (int)((double)(var2 * 255.0F) + 0.5D), (int)((double)(var3 * 255.0F) + 0.5D));
      testColorValueRange(var1, var2, var3, 1.0F);
      this.frgbvalue = new float[3];
      this.frgbvalue[0] = var1;
      this.frgbvalue[1] = var2;
      this.frgbvalue[2] = var3;
      this.falpha = 1.0F;
      this.fvalue = this.frgbvalue;
   }

   public Color(float var1, float var2, float var3, float var4) {
      this((int)((double)(var1 * 255.0F) + 0.5D), (int)((double)(var2 * 255.0F) + 0.5D), (int)((double)(var3 * 255.0F) + 0.5D), (int)((double)(var4 * 255.0F) + 0.5D));
      this.frgbvalue = new float[3];
      this.frgbvalue[0] = var1;
      this.frgbvalue[1] = var2;
      this.frgbvalue[2] = var3;
      this.falpha = var4;
      this.fvalue = this.frgbvalue;
   }

   public Color(ColorSpace var1, float[] var2, float var3) {
      this.frgbvalue = null;
      this.fvalue = null;
      this.falpha = 0.0F;
      this.cs = null;
      boolean var4 = false;
      String var5 = "";
      int var6 = var1.getNumComponents();
      this.fvalue = new float[var6];

      for(int var7 = 0; var7 < var6; ++var7) {
         if ((double)var2[var7] >= 0.0D && (double)var2[var7] <= 1.0D) {
            this.fvalue[var7] = var2[var7];
         } else {
            var4 = true;
            var5 = var5 + "Component " + var7 + " ";
         }
      }

      if ((double)var3 >= 0.0D && (double)var3 <= 1.0D) {
         this.falpha = var3;
      } else {
         var4 = true;
         var5 = var5 + "Alpha";
      }

      if (var4) {
         throw new IllegalArgumentException("Color parameter outside of expected range: " + var5);
      } else {
         this.frgbvalue = var1.toRGB(this.fvalue);
         this.cs = var1;
         this.value = ((int)(this.falpha * 255.0F) & 255) << 24 | ((int)(this.frgbvalue[0] * 255.0F) & 255) << 16 | ((int)(this.frgbvalue[1] * 255.0F) & 255) << 8 | ((int)(this.frgbvalue[2] * 255.0F) & 255) << 0;
      }
   }

   public int getRed() {
      return this.getRGB() >> 16 & 255;
   }

   public int getGreen() {
      return this.getRGB() >> 8 & 255;
   }

   public int getBlue() {
      return this.getRGB() >> 0 & 255;
   }

   public int getAlpha() {
      return this.getRGB() >> 24 & 255;
   }

   public int getRGB() {
      return this.value;
   }

   public Color brighter() {
      int var1 = this.getRed();
      int var2 = this.getGreen();
      int var3 = this.getBlue();
      int var4 = this.getAlpha();
      byte var5 = 3;
      if (var1 == 0 && var2 == 0 && var3 == 0) {
         return new Color(var5, var5, var5, var4);
      } else {
         if (var1 > 0 && var1 < var5) {
            var1 = var5;
         }

         if (var2 > 0 && var2 < var5) {
            var2 = var5;
         }

         if (var3 > 0 && var3 < var5) {
            var3 = var5;
         }

         return new Color(Math.min((int)((double)var1 / 0.7D), 255), Math.min((int)((double)var2 / 0.7D), 255), Math.min((int)((double)var3 / 0.7D), 255), var4);
      }
   }

   public Color darker() {
      return new Color(Math.max((int)((double)this.getRed() * 0.7D), 0), Math.max((int)((double)this.getGreen() * 0.7D), 0), Math.max((int)((double)this.getBlue() * 0.7D), 0), this.getAlpha());
   }

   public int hashCode() {
      return this.value;
   }

   public boolean equals(Object var1) {
      return var1 instanceof Color && ((Color)var1).getRGB() == this.getRGB();
   }

   public String toString() {
      return this.getClass().getName() + "[r=" + this.getRed() + ",g=" + this.getGreen() + ",b=" + this.getBlue() + "]";
   }

   public static Color decode(String var0) throws NumberFormatException {
      Integer var1 = Integer.decode(var0);
      int var2 = var1;
      return new Color(var2 >> 16 & 255, var2 >> 8 & 255, var2 & 255);
   }

   public static Color getColor(String var0) {
      return getColor(var0, (Color)null);
   }

   public static Color getColor(String var0, Color var1) {
      Integer var2 = Integer.getInteger(var0);
      if (var2 == null) {
         return var1;
      } else {
         int var3 = var2;
         return new Color(var3 >> 16 & 255, var3 >> 8 & 255, var3 & 255);
      }
   }

   public static Color getColor(String var0, int var1) {
      Integer var2 = Integer.getInteger(var0);
      int var3 = var2 != null ? var2 : var1;
      return new Color(var3 >> 16 & 255, var3 >> 8 & 255, var3 >> 0 & 255);
   }

   public static int HSBtoRGB(float var0, float var1, float var2) {
      int var3 = 0;
      int var4 = 0;
      int var5 = 0;
      if (var1 == 0.0F) {
         var3 = var4 = var5 = (int)(var2 * 255.0F + 0.5F);
      } else {
         float var6 = (var0 - (float)Math.floor((double)var0)) * 6.0F;
         float var7 = var6 - (float)Math.floor((double)var6);
         float var8 = var2 * (1.0F - var1);
         float var9 = var2 * (1.0F - var1 * var7);
         float var10 = var2 * (1.0F - var1 * (1.0F - var7));
         switch((int)var6) {
         case 0:
            var3 = (int)(var2 * 255.0F + 0.5F);
            var4 = (int)(var10 * 255.0F + 0.5F);
            var5 = (int)(var8 * 255.0F + 0.5F);
            break;
         case 1:
            var3 = (int)(var9 * 255.0F + 0.5F);
            var4 = (int)(var2 * 255.0F + 0.5F);
            var5 = (int)(var8 * 255.0F + 0.5F);
            break;
         case 2:
            var3 = (int)(var8 * 255.0F + 0.5F);
            var4 = (int)(var2 * 255.0F + 0.5F);
            var5 = (int)(var10 * 255.0F + 0.5F);
            break;
         case 3:
            var3 = (int)(var8 * 255.0F + 0.5F);
            var4 = (int)(var9 * 255.0F + 0.5F);
            var5 = (int)(var2 * 255.0F + 0.5F);
            break;
         case 4:
            var3 = (int)(var10 * 255.0F + 0.5F);
            var4 = (int)(var8 * 255.0F + 0.5F);
            var5 = (int)(var2 * 255.0F + 0.5F);
            break;
         case 5:
            var3 = (int)(var2 * 255.0F + 0.5F);
            var4 = (int)(var8 * 255.0F + 0.5F);
            var5 = (int)(var9 * 255.0F + 0.5F);
         }
      }

      return -16777216 | var3 << 16 | var4 << 8 | var5 << 0;
   }

   public static float[] RGBtoHSB(int var0, int var1, int var2, float[] var3) {
      if (var3 == null) {
         var3 = new float[3];
      }

      int var7 = var0 > var1 ? var0 : var1;
      if (var2 > var7) {
         var7 = var2;
      }

      int var8 = var0 < var1 ? var0 : var1;
      if (var2 < var8) {
         var8 = var2;
      }

      float var6 = (float)var7 / 255.0F;
      float var5;
      if (var7 != 0) {
         var5 = (float)(var7 - var8) / (float)var7;
      } else {
         var5 = 0.0F;
      }

      float var4;
      if (var5 == 0.0F) {
         var4 = 0.0F;
      } else {
         float var9 = (float)(var7 - var0) / (float)(var7 - var8);
         float var10 = (float)(var7 - var1) / (float)(var7 - var8);
         float var11 = (float)(var7 - var2) / (float)(var7 - var8);
         if (var0 == var7) {
            var4 = var11 - var10;
         } else if (var1 == var7) {
            var4 = 2.0F + var9 - var11;
         } else {
            var4 = 4.0F + var10 - var9;
         }

         var4 /= 6.0F;
         if (var4 < 0.0F) {
            ++var4;
         }
      }

      var3[0] = var4;
      var3[1] = var5;
      var3[2] = var6;
      return var3;
   }

   public static Color getHSBColor(float var0, float var1, float var2) {
      return new Color(HSBtoRGB(var0, var1, var2));
   }

   public float[] getRGBComponents(float[] var1) {
      float[] var2;
      if (var1 == null) {
         var2 = new float[4];
      } else {
         var2 = var1;
      }

      if (this.frgbvalue == null) {
         var2[0] = (float)this.getRed() / 255.0F;
         var2[1] = (float)this.getGreen() / 255.0F;
         var2[2] = (float)this.getBlue() / 255.0F;
         var2[3] = (float)this.getAlpha() / 255.0F;
      } else {
         var2[0] = this.frgbvalue[0];
         var2[1] = this.frgbvalue[1];
         var2[2] = this.frgbvalue[2];
         var2[3] = this.falpha;
      }

      return var2;
   }

   public float[] getRGBColorComponents(float[] var1) {
      float[] var2;
      if (var1 == null) {
         var2 = new float[3];
      } else {
         var2 = var1;
      }

      if (this.frgbvalue == null) {
         var2[0] = (float)this.getRed() / 255.0F;
         var2[1] = (float)this.getGreen() / 255.0F;
         var2[2] = (float)this.getBlue() / 255.0F;
      } else {
         var2[0] = this.frgbvalue[0];
         var2[1] = this.frgbvalue[1];
         var2[2] = this.frgbvalue[2];
      }

      return var2;
   }

   public float[] getComponents(float[] var1) {
      if (this.fvalue == null) {
         return this.getRGBComponents(var1);
      } else {
         int var3 = this.fvalue.length;
         float[] var2;
         if (var1 == null) {
            var2 = new float[var3 + 1];
         } else {
            var2 = var1;
         }

         for(int var4 = 0; var4 < var3; ++var4) {
            var2[var4] = this.fvalue[var4];
         }

         var2[var3] = this.falpha;
         return var2;
      }
   }

   public float[] getColorComponents(float[] var1) {
      if (this.fvalue == null) {
         return this.getRGBColorComponents(var1);
      } else {
         int var3 = this.fvalue.length;
         float[] var2;
         if (var1 == null) {
            var2 = new float[var3];
         } else {
            var2 = var1;
         }

         for(int var4 = 0; var4 < var3; ++var4) {
            var2[var4] = this.fvalue[var4];
         }

         return var2;
      }
   }

   public float[] getComponents(ColorSpace var1, float[] var2) {
      if (this.cs == null) {
         this.cs = ColorSpace.getInstance(1000);
      }

      float[] var3;
      if (this.fvalue == null) {
         var3 = new float[]{(float)this.getRed() / 255.0F, (float)this.getGreen() / 255.0F, (float)this.getBlue() / 255.0F};
      } else {
         var3 = this.fvalue;
      }

      float[] var4 = this.cs.toCIEXYZ(var3);
      float[] var5 = var1.fromCIEXYZ(var4);
      if (var2 == null) {
         var2 = new float[var5.length + 1];
      }

      for(int var6 = 0; var6 < var5.length; ++var6) {
         var2[var6] = var5[var6];
      }

      if (this.fvalue == null) {
         var2[var5.length] = (float)this.getAlpha() / 255.0F;
      } else {
         var2[var5.length] = this.falpha;
      }

      return var2;
   }

   public float[] getColorComponents(ColorSpace var1, float[] var2) {
      if (this.cs == null) {
         this.cs = ColorSpace.getInstance(1000);
      }

      float[] var3;
      if (this.fvalue == null) {
         var3 = new float[]{(float)this.getRed() / 255.0F, (float)this.getGreen() / 255.0F, (float)this.getBlue() / 255.0F};
      } else {
         var3 = this.fvalue;
      }

      float[] var4 = this.cs.toCIEXYZ(var3);
      float[] var5 = var1.fromCIEXYZ(var4);
      if (var2 == null) {
         return var5;
      } else {
         for(int var6 = 0; var6 < var5.length; ++var6) {
            var2[var6] = var5[var6];
         }

         return var2;
      }
   }

   public ColorSpace getColorSpace() {
      if (this.cs == null) {
         this.cs = ColorSpace.getInstance(1000);
      }

      return this.cs;
   }

   public synchronized PaintContext createContext(ColorModel var1, Rectangle var2, Rectangle2D var3, AffineTransform var4, RenderingHints var5) {
      return new ColorPaintContext(this.getRGB(), var1);
   }

   public int getTransparency() {
      int var1 = this.getAlpha();
      if (var1 == 255) {
         return 1;
      } else {
         return var1 == 0 ? 2 : 3;
      }
   }

   static {
      WHITE = white;
      lightGray = new Color(192, 192, 192);
      LIGHT_GRAY = lightGray;
      gray = new Color(128, 128, 128);
      GRAY = gray;
      darkGray = new Color(64, 64, 64);
      DARK_GRAY = darkGray;
      black = new Color(0, 0, 0);
      BLACK = black;
      red = new Color(255, 0, 0);
      RED = red;
      pink = new Color(255, 175, 175);
      PINK = pink;
      orange = new Color(255, 200, 0);
      ORANGE = orange;
      yellow = new Color(255, 255, 0);
      YELLOW = yellow;
      green = new Color(0, 255, 0);
      GREEN = green;
      magenta = new Color(255, 0, 255);
      MAGENTA = magenta;
      cyan = new Color(0, 255, 255);
      CYAN = cyan;
      blue = new Color(0, 0, 255);
      BLUE = blue;
      Toolkit.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

   }
}
