package sun.awt.image;

import java.awt.image.ColorModel;

public class PixelConverter {
   public static final PixelConverter instance = new PixelConverter();
   protected int alphaMask = 0;

   protected PixelConverter() {
   }

   public int rgbToPixel(int var1, ColorModel var2) {
      Object var3 = var2.getDataElements(var1, (Object)null);
      switch(var2.getTransferType()) {
      case 0:
         byte[] var4 = (byte[])((byte[])var3);
         int var5 = 0;
         switch(var4.length) {
         default:
            var5 = var4[3] << 24;
         case 3:
            var5 |= (var4[2] & 255) << 16;
         case 2:
            var5 |= (var4[1] & 255) << 8;
         case 1:
            var5 |= var4[0] & 255;
            return var5;
         }
      case 1:
      case 2:
         short[] var6 = (short[])((short[])var3);
         return (var6.length > 1 ? var6[1] << 16 : 0) | var6[0] & '\uffff';
      case 3:
         return ((int[])((int[])var3))[0];
      default:
         return var1;
      }
   }

   public int pixelToRgb(int var1, ColorModel var2) {
      return var1;
   }

   public final int getAlphaMask() {
      return this.alphaMask;
   }

   public static class UshortGray extends PixelConverter.ByteGray {
      static final double SHORT_MULT = 257.0D;
      static final double USHORT_RED_MULT = 76.843D;
      static final double USHORT_GRN_MULT = 150.85899999999998D;
      static final double USHORT_BLU_MULT = 29.298000000000002D;
      public static final PixelConverter instance = new PixelConverter.UshortGray();

      private UshortGray() {
         super(null);
      }

      public int rgbToPixel(int var1, ColorModel var2) {
         int var3 = var1 >> 16 & 255;
         int var4 = var1 >> 8 & 255;
         int var5 = var1 & 255;
         return (int)((double)var3 * 76.843D + (double)var4 * 150.85899999999998D + (double)var5 * 29.298000000000002D + 0.5D);
      }

      public int pixelToRgb(int var1, ColorModel var2) {
         var1 >>= 8;
         return (('\uff00' | var1) << 8 | var1) << 8 | var1;
      }
   }

   public static class ByteGray extends PixelConverter {
      static final double RED_MULT = 0.299D;
      static final double GRN_MULT = 0.587D;
      static final double BLU_MULT = 0.114D;
      public static final PixelConverter instance = new PixelConverter.ByteGray();

      private ByteGray() {
      }

      public int rgbToPixel(int var1, ColorModel var2) {
         int var3 = var1 >> 16 & 255;
         int var4 = var1 >> 8 & 255;
         int var5 = var1 & 255;
         return (int)((double)var3 * 0.299D + (double)var4 * 0.587D + (double)var5 * 0.114D + 0.5D);
      }

      public int pixelToRgb(int var1, ColorModel var2) {
         return (('\uff00' | var1) << 8 | var1) << 8 | var1;
      }

      // $FF: synthetic method
      ByteGray(Object var1) {
         this();
      }
   }

   public static class ArgbBm extends PixelConverter {
      public static final PixelConverter instance = new PixelConverter.ArgbBm();

      private ArgbBm() {
      }

      public int rgbToPixel(int var1, ColorModel var2) {
         return var1 | var1 >> 31 << 24;
      }

      public int pixelToRgb(int var1, ColorModel var2) {
         return var1 << 7 >> 7;
      }
   }

   public static class ArgbPre extends PixelConverter {
      public static final PixelConverter instance = new PixelConverter.ArgbPre();

      private ArgbPre() {
         this.alphaMask = -16777216;
      }

      public int rgbToPixel(int var1, ColorModel var2) {
         if (var1 >> 24 == -1) {
            return var1;
         } else {
            int var3 = var1 >>> 24;
            int var4 = var1 >> 16 & 255;
            int var5 = var1 >> 8 & 255;
            int var6 = var1 & 255;
            int var7 = var3 + (var3 >> 7);
            var4 = var4 * var7 >> 8;
            var5 = var5 * var7 >> 8;
            var6 = var6 * var7 >> 8;
            return var3 << 24 | var4 << 16 | var5 << 8 | var6;
         }
      }

      public int pixelToRgb(int var1, ColorModel var2) {
         int var3 = var1 >>> 24;
         if (var3 != 255 && var3 != 0) {
            int var4 = var1 >> 16 & 255;
            int var5 = var1 >> 8 & 255;
            int var6 = var1 & 255;
            var4 = ((var4 << 8) - var4) / var3;
            var5 = ((var5 << 8) - var5) / var3;
            var6 = ((var6 << 8) - var6) / var3;
            return var3 << 24 | var4 << 16 | var5 << 8 | var6;
         } else {
            return var1;
         }
      }
   }

   public static class RgbaPre extends PixelConverter {
      public static final PixelConverter instance = new PixelConverter.RgbaPre();

      private RgbaPre() {
         this.alphaMask = 255;
      }

      public int rgbToPixel(int var1, ColorModel var2) {
         if (var1 >> 24 == -1) {
            return var1 << 8 | var1 >>> 24;
         } else {
            int var3 = var1 >>> 24;
            int var4 = var1 >> 16 & 255;
            int var5 = var1 >> 8 & 255;
            int var6 = var1 & 255;
            int var7 = var3 + (var3 >> 7);
            var4 = var4 * var7 >> 8;
            var5 = var5 * var7 >> 8;
            var6 = var6 * var7 >> 8;
            return var4 << 24 | var5 << 16 | var6 << 8 | var3;
         }
      }

      public int pixelToRgb(int var1, ColorModel var2) {
         int var3 = var1 & 255;
         if (var3 != 255 && var3 != 0) {
            int var4 = var1 >>> 24;
            int var5 = var1 >> 16 & 255;
            int var6 = var1 >> 8 & 255;
            var4 = ((var4 << 8) - var4) / var3;
            var5 = ((var5 << 8) - var5) / var3;
            var6 = ((var6 << 8) - var6) / var3;
            return var4 << 24 | var5 << 16 | var6 << 8 | var3;
         } else {
            return var1 >>> 8 | var1 << 24;
         }
      }
   }

   public static class Rgba extends PixelConverter {
      public static final PixelConverter instance = new PixelConverter.Rgba();

      private Rgba() {
         this.alphaMask = 255;
      }

      public int rgbToPixel(int var1, ColorModel var2) {
         return var1 << 8 | var1 >>> 24;
      }

      public int pixelToRgb(int var1, ColorModel var2) {
         return var1 << 24 | var1 >>> 8;
      }
   }

   public static class Bgrx extends PixelConverter {
      public static final PixelConverter instance = new PixelConverter.Bgrx();

      private Bgrx() {
      }

      public int rgbToPixel(int var1, ColorModel var2) {
         return var1 << 24 | (var1 & '\uff00') << 8 | var1 >> 8 & '\uff00';
      }

      public int pixelToRgb(int var1, ColorModel var2) {
         return -16777216 | (var1 & '\uff00') << 8 | var1 >> 8 & '\uff00' | var1 >>> 24;
      }
   }

   public static class Xbgr extends PixelConverter {
      public static final PixelConverter instance = new PixelConverter.Xbgr();

      private Xbgr() {
      }

      public int rgbToPixel(int var1, ColorModel var2) {
         return (var1 & 255) << 16 | var1 & '\uff00' | var1 >> 16 & 255;
      }

      public int pixelToRgb(int var1, ColorModel var2) {
         return -16777216 | (var1 & 255) << 16 | var1 & '\uff00' | var1 >> 16 & 255;
      }
   }

   public static class Ushort4444Argb extends PixelConverter {
      public static final PixelConverter instance = new PixelConverter.Ushort4444Argb();

      private Ushort4444Argb() {
         this.alphaMask = 61440;
      }

      public int rgbToPixel(int var1, ColorModel var2) {
         int var3 = var1 >> 16 & '\uf000';
         int var4 = var1 >> 12 & 3840;
         int var5 = var1 >> 8 & 240;
         int var6 = var1 >> 4 & 15;
         return var3 | var4 | var5 | var6;
      }

      public int pixelToRgb(int var1, ColorModel var2) {
         int var3 = var1 & '\uf000';
         var3 = (var1 << 16 | var1 << 12) & -16777216;
         int var4 = var1 & 3840;
         var4 = (var1 << 12 | var1 << 8) & 16711680;
         int var5 = var1 & 240;
         var5 = (var1 << 8 | var1 << 4) & '\uff00';
         int var6 = var1 & 15;
         var6 = (var1 << 4 | var1 << 0) & 255;
         return var3 | var4 | var5 | var6;
      }
   }

   public static class Ushort555Rgb extends PixelConverter {
      public static final PixelConverter instance = new PixelConverter.Ushort555Rgb();

      private Ushort555Rgb() {
      }

      public int rgbToPixel(int var1, ColorModel var2) {
         return var1 >> 9 & 31744 | var1 >> 6 & 992 | var1 >> 3 & 31;
      }

      public int pixelToRgb(int var1, ColorModel var2) {
         int var3 = var1 >> 10 & 31;
         var3 = var3 << 3 | var3 >> 2;
         int var4 = var1 >> 5 & 31;
         var4 = var4 << 3 | var4 >> 2;
         int var5 = var1 & 31;
         var5 = var5 << 3 | var5 >> 2;
         return -16777216 | var3 << 16 | var4 << 8 | var5;
      }
   }

   public static class Ushort555Rgbx extends PixelConverter {
      public static final PixelConverter instance = new PixelConverter.Ushort555Rgbx();

      private Ushort555Rgbx() {
      }

      public int rgbToPixel(int var1, ColorModel var2) {
         return var1 >> 8 & '\uf800' | var1 >> 5 & 1984 | var1 >> 2 & 62;
      }

      public int pixelToRgb(int var1, ColorModel var2) {
         int var3 = var1 >> 11 & 31;
         var3 = var3 << 3 | var3 >> 2;
         int var4 = var1 >> 6 & 31;
         var4 = var4 << 3 | var4 >> 2;
         int var5 = var1 >> 1 & 31;
         var5 = var5 << 3 | var5 >> 2;
         return -16777216 | var3 << 16 | var4 << 8 | var5;
      }
   }

   public static class Ushort565Rgb extends PixelConverter {
      public static final PixelConverter instance = new PixelConverter.Ushort565Rgb();

      private Ushort565Rgb() {
      }

      public int rgbToPixel(int var1, ColorModel var2) {
         return var1 >> 8 & '\uf800' | var1 >> 5 & 2016 | var1 >> 3 & 31;
      }

      public int pixelToRgb(int var1, ColorModel var2) {
         int var3 = var1 >> 11 & 31;
         var3 = var3 << 3 | var3 >> 2;
         int var4 = var1 >> 5 & 63;
         var4 = var4 << 2 | var4 >> 4;
         int var5 = var1 & 31;
         var5 = var5 << 3 | var5 >> 2;
         return -16777216 | var3 << 16 | var4 << 8 | var5;
      }
   }

   public static class Argb extends PixelConverter {
      public static final PixelConverter instance = new PixelConverter.Argb();

      private Argb() {
         this.alphaMask = -16777216;
      }

      public int rgbToPixel(int var1, ColorModel var2) {
         return var1;
      }

      public int pixelToRgb(int var1, ColorModel var2) {
         return var1;
      }
   }

   public static class Xrgb extends PixelConverter {
      public static final PixelConverter instance = new PixelConverter.Xrgb();

      private Xrgb() {
      }

      public int rgbToPixel(int var1, ColorModel var2) {
         return var1;
      }

      public int pixelToRgb(int var1, ColorModel var2) {
         return -16777216 | var1;
      }
   }

   public static class Rgbx extends PixelConverter {
      public static final PixelConverter instance = new PixelConverter.Rgbx();

      private Rgbx() {
      }

      public int rgbToPixel(int var1, ColorModel var2) {
         return var1 << 8;
      }

      public int pixelToRgb(int var1, ColorModel var2) {
         return -16777216 | var1 >> 8;
      }
   }
}
