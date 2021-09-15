package com.sun.imageio.plugins.jpeg;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.image.ColorModel;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.imageio.plugins.jpeg.JPEGQTable;

public class JPEG {
   public static final int TEM = 1;
   public static final int SOF0 = 192;
   public static final int SOF1 = 193;
   public static final int SOF2 = 194;
   public static final int SOF3 = 195;
   public static final int DHT = 196;
   public static final int SOF5 = 197;
   public static final int SOF6 = 198;
   public static final int SOF7 = 199;
   public static final int JPG = 200;
   public static final int SOF9 = 201;
   public static final int SOF10 = 202;
   public static final int SOF11 = 203;
   public static final int DAC = 204;
   public static final int SOF13 = 205;
   public static final int SOF14 = 206;
   public static final int SOF15 = 207;
   public static final int RST0 = 208;
   public static final int RST1 = 209;
   public static final int RST2 = 210;
   public static final int RST3 = 211;
   public static final int RST4 = 212;
   public static final int RST5 = 213;
   public static final int RST6 = 214;
   public static final int RST7 = 215;
   public static final int RESTART_RANGE = 8;
   public static final int SOI = 216;
   public static final int EOI = 217;
   public static final int SOS = 218;
   public static final int DQT = 219;
   public static final int DNL = 220;
   public static final int DRI = 221;
   public static final int DHP = 222;
   public static final int EXP = 223;
   public static final int APP0 = 224;
   public static final int APP1 = 225;
   public static final int APP2 = 226;
   public static final int APP3 = 227;
   public static final int APP4 = 228;
   public static final int APP5 = 229;
   public static final int APP6 = 230;
   public static final int APP7 = 231;
   public static final int APP8 = 232;
   public static final int APP9 = 233;
   public static final int APP10 = 234;
   public static final int APP11 = 235;
   public static final int APP12 = 236;
   public static final int APP13 = 237;
   public static final int APP14 = 238;
   public static final int APP15 = 239;
   public static final int COM = 254;
   public static final int DENSITY_UNIT_ASPECT_RATIO = 0;
   public static final int DENSITY_UNIT_DOTS_INCH = 1;
   public static final int DENSITY_UNIT_DOTS_CM = 2;
   public static final int NUM_DENSITY_UNIT = 3;
   public static final int ADOBE_IMPOSSIBLE = -1;
   public static final int ADOBE_UNKNOWN = 0;
   public static final int ADOBE_YCC = 1;
   public static final int ADOBE_YCCK = 2;
   public static final String vendor = "Oracle Corporation";
   public static final String version = "0.5";
   static final String[] names = new String[]{"JPEG", "jpeg", "JPG", "jpg"};
   static final String[] suffixes = new String[]{"jpg", "jpeg"};
   static final String[] MIMETypes = new String[]{"image/jpeg"};
   public static final String nativeImageMetadataFormatName = "javax_imageio_jpeg_image_1.0";
   public static final String nativeImageMetadataFormatClassName = "com.sun.imageio.plugins.jpeg.JPEGImageMetadataFormat";
   public static final String nativeStreamMetadataFormatName = "javax_imageio_jpeg_stream_1.0";
   public static final String nativeStreamMetadataFormatClassName = "com.sun.imageio.plugins.jpeg.JPEGStreamMetadataFormat";
   public static final int JCS_UNKNOWN = 0;
   public static final int JCS_GRAYSCALE = 1;
   public static final int JCS_RGB = 2;
   public static final int JCS_YCbCr = 3;
   public static final int JCS_CMYK = 4;
   public static final int JCS_YCC = 5;
   public static final int JCS_RGBA = 6;
   public static final int JCS_YCbCrA = 7;
   public static final int JCS_YCCA = 10;
   public static final int JCS_YCCK = 11;
   public static final int NUM_JCS_CODES = 12;
   static final int[][] bandOffsets = new int[][]{{0}, {0, 1}, {0, 1, 2}, {0, 1, 2, 3}};
   static final int[] bOffsRGB = new int[]{2, 1, 0};
   public static final float DEFAULT_QUALITY = 0.75F;

   static boolean isNonStandardICC(ColorSpace var0) {
      boolean var1 = false;
      if (var0 instanceof ICC_ColorSpace && !var0.isCS_sRGB() && !var0.equals(ColorSpace.getInstance(1001)) && !var0.equals(ColorSpace.getInstance(1003)) && !var0.equals(ColorSpace.getInstance(1004)) && !var0.equals(ColorSpace.getInstance(1002))) {
         var1 = true;
      }

      return var1;
   }

   static boolean isJFIFcompliant(ImageTypeSpecifier var0, boolean var1) {
      ColorModel var2 = var0.getColorModel();
      if (var2.hasAlpha()) {
         return false;
      } else {
         int var3 = var0.getNumComponents();
         if (var3 == 1) {
            return true;
         } else if (var3 != 3) {
            return false;
         } else {
            if (var1) {
               if (var2.getColorSpace().getType() == 5) {
                  return true;
               }
            } else if (var2.getColorSpace().getType() == 3) {
               return true;
            }

            return false;
         }
      }
   }

   static int transformForType(ImageTypeSpecifier var0, boolean var1) {
      int var2 = -1;
      ColorModel var3 = var0.getColorModel();
      switch(var3.getColorSpace().getType()) {
      case 3:
         var2 = 1;
      case 4:
      case 7:
      case 8:
      default:
         break;
      case 5:
         var2 = var1 ? 1 : 0;
         break;
      case 6:
         var2 = 0;
         break;
      case 9:
         var2 = var1 ? 2 : -1;
      }

      return var2;
   }

   static float convertToLinearQuality(float var0) {
      if (var0 <= 0.0F) {
         var0 = 0.01F;
      }

      if (var0 > 1.0F) {
         var0 = 1.0F;
      }

      if (var0 < 0.5F) {
         var0 = 0.5F / var0;
      } else {
         var0 = 2.0F - var0 * 2.0F;
      }

      return var0;
   }

   static JPEGQTable[] getDefaultQTables() {
      JPEGQTable[] var0 = new JPEGQTable[]{JPEGQTable.K1Div2Luminance, JPEGQTable.K2Div2Chrominance};
      return var0;
   }

   static JPEGHuffmanTable[] getDefaultHuffmanTables(boolean var0) {
      JPEGHuffmanTable[] var1 = new JPEGHuffmanTable[2];
      if (var0) {
         var1[0] = JPEGHuffmanTable.StdDCLuminance;
         var1[1] = JPEGHuffmanTable.StdDCChrominance;
      } else {
         var1[0] = JPEGHuffmanTable.StdACLuminance;
         var1[1] = JPEGHuffmanTable.StdACChrominance;
      }

      return var1;
   }

   public static class JCS {
      public static final ColorSpace sRGB = ColorSpace.getInstance(1000);
      private static ColorSpace YCC = null;
      private static boolean yccInited = false;

      public static ColorSpace getYCC() {
         if (!yccInited) {
            try {
               YCC = ColorSpace.getInstance(1002);
            } catch (IllegalArgumentException var4) {
            } finally {
               yccInited = true;
            }
         }

         return YCC;
      }
   }
}
