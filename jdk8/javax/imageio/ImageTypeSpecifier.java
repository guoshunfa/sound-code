package javax.imageio;

import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

public class ImageTypeSpecifier {
   protected ColorModel colorModel;
   protected SampleModel sampleModel;
   private static ImageTypeSpecifier[] BISpecifier = new ImageTypeSpecifier[14];
   private static ColorSpace sRGB = ColorSpace.getInstance(1000);

   private ImageTypeSpecifier() {
   }

   public ImageTypeSpecifier(ColorModel var1, SampleModel var2) {
      if (var1 == null) {
         throw new IllegalArgumentException("colorModel == null!");
      } else if (var2 == null) {
         throw new IllegalArgumentException("sampleModel == null!");
      } else if (!var1.isCompatibleSampleModel(var2)) {
         throw new IllegalArgumentException("sampleModel is incompatible with colorModel!");
      } else {
         this.colorModel = var1;
         this.sampleModel = var2;
      }
   }

   public ImageTypeSpecifier(RenderedImage var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("image == null!");
      } else {
         this.colorModel = var1.getColorModel();
         this.sampleModel = var1.getSampleModel();
      }
   }

   public static ImageTypeSpecifier createPacked(ColorSpace var0, int var1, int var2, int var3, int var4, int var5, boolean var6) {
      return new ImageTypeSpecifier.Packed(var0, var1, var2, var3, var4, var5, var6);
   }

   static ColorModel createComponentCM(ColorSpace var0, int var1, int var2, boolean var3, boolean var4) {
      int var5 = var3 ? 3 : 1;
      int[] var6 = new int[var1];
      int var7 = DataBuffer.getDataTypeSize(var2);

      for(int var8 = 0; var8 < var1; ++var8) {
         var6[var8] = var7;
      }

      return new ComponentColorModel(var0, var6, var3, var4, var5, var2);
   }

   public static ImageTypeSpecifier createInterleaved(ColorSpace var0, int[] var1, int var2, boolean var3, boolean var4) {
      return new ImageTypeSpecifier.Interleaved(var0, var1, var2, var3, var4);
   }

   public static ImageTypeSpecifier createBanded(ColorSpace var0, int[] var1, int[] var2, int var3, boolean var4, boolean var5) {
      return new ImageTypeSpecifier.Banded(var0, var1, var2, var3, var4, var5);
   }

   public static ImageTypeSpecifier createGrayscale(int var0, int var1, boolean var2) {
      return new ImageTypeSpecifier.Grayscale(var0, var1, var2, false, false);
   }

   public static ImageTypeSpecifier createGrayscale(int var0, int var1, boolean var2, boolean var3) {
      return new ImageTypeSpecifier.Grayscale(var0, var1, var2, true, var3);
   }

   public static ImageTypeSpecifier createIndexed(byte[] var0, byte[] var1, byte[] var2, byte[] var3, int var4, int var5) {
      return new ImageTypeSpecifier.Indexed(var0, var1, var2, var3, var4, var5);
   }

   public static ImageTypeSpecifier createFromBufferedImageType(int var0) {
      if (var0 >= 1 && var0 <= 13) {
         return getSpecifier(var0);
      } else if (var0 == 0) {
         throw new IllegalArgumentException("Cannot create from TYPE_CUSTOM!");
      } else {
         throw new IllegalArgumentException("Invalid BufferedImage type!");
      }
   }

   public static ImageTypeSpecifier createFromRenderedImage(RenderedImage var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("image == null!");
      } else {
         if (var0 instanceof BufferedImage) {
            int var1 = ((BufferedImage)var0).getType();
            if (var1 != 0) {
               return getSpecifier(var1);
            }
         }

         return new ImageTypeSpecifier(var0);
      }
   }

   public int getBufferedImageType() {
      BufferedImage var1 = this.createBufferedImage(1, 1);
      return var1.getType();
   }

   public int getNumComponents() {
      return this.colorModel.getNumComponents();
   }

   public int getNumBands() {
      return this.sampleModel.getNumBands();
   }

   public int getBitsPerBand(int var1) {
      if (var1 < 0 | var1 >= this.getNumBands()) {
         throw new IllegalArgumentException("band out of range!");
      } else {
         return this.sampleModel.getSampleSize(var1);
      }
   }

   public SampleModel getSampleModel() {
      return this.sampleModel;
   }

   public SampleModel getSampleModel(int var1, int var2) {
      if ((long)var1 * (long)var2 > 2147483647L) {
         throw new IllegalArgumentException("width*height > Integer.MAX_VALUE!");
      } else {
         return this.sampleModel.createCompatibleSampleModel(var1, var2);
      }
   }

   public ColorModel getColorModel() {
      return this.colorModel;
   }

   public BufferedImage createBufferedImage(int var1, int var2) {
      try {
         SampleModel var3 = this.getSampleModel(var1, var2);
         WritableRaster var4 = Raster.createWritableRaster(var3, new Point(0, 0));
         return new BufferedImage(this.colorModel, var4, this.colorModel.isAlphaPremultiplied(), new Hashtable());
      } catch (NegativeArraySizeException var5) {
         throw new IllegalArgumentException("Array size > Integer.MAX_VALUE!");
      }
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof ImageTypeSpecifier) {
         ImageTypeSpecifier var2 = (ImageTypeSpecifier)var1;
         return this.colorModel.equals(var2.colorModel) && this.sampleModel.equals(var2.sampleModel);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return 9 * this.colorModel.hashCode() + 14 * this.sampleModel.hashCode();
   }

   private static ImageTypeSpecifier getSpecifier(int var0) {
      if (BISpecifier[var0] == null) {
         BISpecifier[var0] = createSpecifier(var0);
      }

      return BISpecifier[var0];
   }

   private static ImageTypeSpecifier createSpecifier(int var0) {
      switch(var0) {
      case 1:
         return createPacked(sRGB, 16711680, 65280, 255, 0, 3, false);
      case 2:
         return createPacked(sRGB, 16711680, 65280, 255, -16777216, 3, false);
      case 3:
         return createPacked(sRGB, 16711680, 65280, 255, -16777216, 3, true);
      case 4:
         return createPacked(sRGB, 255, 65280, 16711680, 0, 3, false);
      case 5:
         return createInterleaved(sRGB, new int[]{2, 1, 0}, 0, false, false);
      case 6:
         return createInterleaved(sRGB, new int[]{3, 2, 1, 0}, 0, true, false);
      case 7:
         return createInterleaved(sRGB, new int[]{3, 2, 1, 0}, 0, true, true);
      case 8:
         return createPacked(sRGB, 63488, 2016, 31, 0, 1, false);
      case 9:
         return createPacked(sRGB, 31744, 992, 31, 0, 1, false);
      case 10:
         return createGrayscale(8, 0, false);
      case 11:
         return createGrayscale(16, 1, false);
      case 12:
         return createGrayscale(1, 0, false);
      case 13:
         BufferedImage var1 = new BufferedImage(1, 1, 13);
         IndexColorModel var2 = (IndexColorModel)var1.getColorModel();
         int var3 = var2.getMapSize();
         byte[] var4 = new byte[var3];
         byte[] var5 = new byte[var3];
         byte[] var6 = new byte[var3];
         byte[] var7 = new byte[var3];
         var2.getReds(var4);
         var2.getGreens(var5);
         var2.getBlues(var6);
         var2.getAlphas(var7);
         return createIndexed(var4, var5, var6, var7, 8, 0);
      default:
         throw new IllegalArgumentException("Invalid BufferedImage type!");
      }
   }

   // $FF: synthetic method
   ImageTypeSpecifier(Object var1) {
      this();
   }

   static class Indexed extends ImageTypeSpecifier {
      byte[] redLUT;
      byte[] greenLUT;
      byte[] blueLUT;
      byte[] alphaLUT = null;
      int bits;
      int dataType;

      public Indexed(byte[] var1, byte[] var2, byte[] var3, byte[] var4, int var5, int var6) {
         super((<undefinedtype>)null);
         if (var1 != null && var2 != null && var3 != null) {
            if (var5 != 1 && var5 != 2 && var5 != 4 && var5 != 8 && var5 != 16) {
               throw new IllegalArgumentException("Bad value for bits!");
            } else if (var6 != 0 && var6 != 2 && var6 != 1 && var6 != 3) {
               throw new IllegalArgumentException("Bad value for dataType!");
            } else if ((var5 <= 8 || var6 != 0) && (var5 <= 16 || var6 == 3)) {
               int var7 = 1 << var5;
               if (var1.length != var7 || var2.length != var7 || var3.length != var7 || var4 != null && var4.length != var7) {
                  throw new IllegalArgumentException("LUT has improper length!");
               } else {
                  this.redLUT = (byte[])((byte[])var1.clone());
                  this.greenLUT = (byte[])((byte[])var2.clone());
                  this.blueLUT = (byte[])((byte[])var3.clone());
                  if (var4 != null) {
                     this.alphaLUT = (byte[])((byte[])var4.clone());
                  }

                  this.bits = var5;
                  this.dataType = var6;
                  if (var4 == null) {
                     this.colorModel = new IndexColorModel(var5, var1.length, var1, var2, var3);
                  } else {
                     this.colorModel = new IndexColorModel(var5, var1.length, var1, var2, var3, var4);
                  }

                  if ((var5 != 8 || var6 != 0) && (var5 != 16 || var6 != 2 && var6 != 1)) {
                     this.sampleModel = new MultiPixelPackedSampleModel(var6, 1, 1, var5);
                  } else {
                     int[] var8 = new int[]{0};
                     this.sampleModel = new PixelInterleavedSampleModel(var6, 1, 1, 1, 1, var8);
                  }

               }
            } else {
               throw new IllegalArgumentException("Too many bits for dataType!");
            }
         } else {
            throw new IllegalArgumentException("LUT is null!");
         }
      }
   }

   static class Grayscale extends ImageTypeSpecifier {
      int bits;
      int dataType;
      boolean isSigned;
      boolean hasAlpha;
      boolean isAlphaPremultiplied;

      public Grayscale(int var1, int var2, boolean var3, boolean var4, boolean var5) {
         super((<undefinedtype>)null);
         if (var1 != 1 && var1 != 2 && var1 != 4 && var1 != 8 && var1 != 16) {
            throw new IllegalArgumentException("Bad value for bits!");
         } else if (var2 != 0 && var2 != 2 && var2 != 1) {
            throw new IllegalArgumentException("Bad value for dataType!");
         } else if (var1 > 8 && var2 == 0) {
            throw new IllegalArgumentException("Too many bits for dataType!");
         } else {
            this.bits = var1;
            this.dataType = var2;
            this.isSigned = var3;
            this.hasAlpha = var4;
            this.isAlphaPremultiplied = var5;
            ColorSpace var6 = ColorSpace.getInstance(1003);
            int var7;
            if (var1 == 8 && var2 == 0 || var1 == 16 && (var2 == 2 || var2 == 1)) {
               var7 = var4 ? 2 : 1;
               int var13 = var4 ? 3 : 1;
               int[] var14 = new int[var7];
               var14[0] = var1;
               if (var7 == 2) {
                  var14[1] = var1;
               }

               this.colorModel = new ComponentColorModel(var6, var14, var4, var5, var13, var2);
               int[] var10 = new int[var7];
               var10[0] = 0;
               if (var7 == 2) {
                  var10[1] = 1;
               }

               byte var11 = 1;
               byte var12 = 1;
               this.sampleModel = new PixelInterleavedSampleModel(var2, var11, var12, var7, var11 * var7, var10);
            } else {
               var7 = 1 << var1;
               byte[] var8 = new byte[var7];

               for(int var9 = 0; var9 < var7; ++var9) {
                  var8[var9] = (byte)(var9 * 255 / (var7 - 1));
               }

               this.colorModel = new IndexColorModel(var1, var7, var8, var8, var8);
               this.sampleModel = new MultiPixelPackedSampleModel(var2, 1, 1, var1);
            }

         }
      }
   }

   static class Banded extends ImageTypeSpecifier {
      ColorSpace colorSpace;
      int[] bankIndices;
      int[] bandOffsets;
      int dataType;
      boolean hasAlpha;
      boolean isAlphaPremultiplied;

      public Banded(ColorSpace var1, int[] var2, int[] var3, int var4, boolean var5, boolean var6) {
         super((<undefinedtype>)null);
         if (var1 == null) {
            throw new IllegalArgumentException("colorSpace == null!");
         } else if (var2 == null) {
            throw new IllegalArgumentException("bankIndices == null!");
         } else if (var3 == null) {
            throw new IllegalArgumentException("bandOffsets == null!");
         } else if (var2.length != var3.length) {
            throw new IllegalArgumentException("bankIndices.length != bandOffsets.length!");
         } else if (var4 != 0 && var4 != 2 && var4 != 1 && var4 != 3 && var4 != 4 && var4 != 5) {
            throw new IllegalArgumentException("Bad value for dataType!");
         } else {
            int var7 = var1.getNumComponents() + (var5 ? 1 : 0);
            if (var3.length != var7) {
               throw new IllegalArgumentException("bandOffsets.length is wrong!");
            } else {
               this.colorSpace = var1;
               this.bankIndices = (int[])((int[])var2.clone());
               this.bandOffsets = (int[])((int[])var3.clone());
               this.dataType = var4;
               this.hasAlpha = var5;
               this.isAlphaPremultiplied = var6;
               this.colorModel = ImageTypeSpecifier.createComponentCM(var1, var2.length, var4, var5, var6);
               byte var8 = 1;
               byte var9 = 1;
               this.sampleModel = new BandedSampleModel(var4, var8, var9, var8, var2, var3);
            }
         }
      }

      public boolean equals(Object var1) {
         if (var1 != null && var1 instanceof ImageTypeSpecifier.Banded) {
            ImageTypeSpecifier.Banded var2 = (ImageTypeSpecifier.Banded)var1;
            if (this.colorSpace.equals(var2.colorSpace) && this.dataType == var2.dataType && this.hasAlpha == var2.hasAlpha && this.isAlphaPremultiplied == var2.isAlphaPremultiplied && this.bankIndices.length == var2.bankIndices.length && this.bandOffsets.length == var2.bandOffsets.length) {
               int var3;
               for(var3 = 0; var3 < this.bankIndices.length; ++var3) {
                  if (this.bankIndices[var3] != var2.bankIndices[var3]) {
                     return false;
                  }
               }

               for(var3 = 0; var3 < this.bandOffsets.length; ++var3) {
                  if (this.bandOffsets[var3] != var2.bandOffsets[var3]) {
                     return false;
                  }
               }

               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }

      public int hashCode() {
         return super.hashCode() + 3 * this.bandOffsets.length + 7 * this.bankIndices.length + 21 * this.dataType + (this.hasAlpha ? 19 : 29);
      }
   }

   static class Interleaved extends ImageTypeSpecifier {
      ColorSpace colorSpace;
      int[] bandOffsets;
      int dataType;
      boolean hasAlpha;
      boolean isAlphaPremultiplied;

      public Interleaved(ColorSpace var1, int[] var2, int var3, boolean var4, boolean var5) {
         super((<undefinedtype>)null);
         if (var1 == null) {
            throw new IllegalArgumentException("colorSpace == null!");
         } else if (var2 == null) {
            throw new IllegalArgumentException("bandOffsets == null!");
         } else {
            int var6 = var1.getNumComponents() + (var4 ? 1 : 0);
            if (var2.length != var6) {
               throw new IllegalArgumentException("bandOffsets.length is wrong!");
            } else if (var3 != 0 && var3 != 2 && var3 != 1 && var3 != 3 && var3 != 4 && var3 != 5) {
               throw new IllegalArgumentException("Bad value for dataType!");
            } else {
               this.colorSpace = var1;
               this.bandOffsets = (int[])((int[])var2.clone());
               this.dataType = var3;
               this.hasAlpha = var4;
               this.isAlphaPremultiplied = var5;
               this.colorModel = ImageTypeSpecifier.createComponentCM(var1, var2.length, var3, var4, var5);
               int var7 = var2[0];
               int var8 = var7;

               int var9;
               for(var9 = 0; var9 < var2.length; ++var9) {
                  int var10 = var2[var9];
                  var7 = Math.min(var10, var7);
                  var8 = Math.max(var10, var8);
               }

               var9 = var8 - var7 + 1;
               byte var12 = 1;
               byte var11 = 1;
               this.sampleModel = new PixelInterleavedSampleModel(var3, var12, var11, var9, var12 * var9, var2);
            }
         }
      }

      public boolean equals(Object var1) {
         if (var1 != null && var1 instanceof ImageTypeSpecifier.Interleaved) {
            ImageTypeSpecifier.Interleaved var2 = (ImageTypeSpecifier.Interleaved)var1;
            if (this.colorSpace.equals(var2.colorSpace) && this.dataType == var2.dataType && this.hasAlpha == var2.hasAlpha && this.isAlphaPremultiplied == var2.isAlphaPremultiplied && this.bandOffsets.length == var2.bandOffsets.length) {
               for(int var3 = 0; var3 < this.bandOffsets.length; ++var3) {
                  if (this.bandOffsets[var3] != var2.bandOffsets[var3]) {
                     return false;
                  }
               }

               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }

      public int hashCode() {
         return super.hashCode() + 4 * this.bandOffsets.length + 25 * this.dataType + (this.hasAlpha ? 17 : 18);
      }
   }

   static class Packed extends ImageTypeSpecifier {
      ColorSpace colorSpace;
      int redMask;
      int greenMask;
      int blueMask;
      int alphaMask;
      int transferType;
      boolean isAlphaPremultiplied;

      public Packed(ColorSpace var1, int var2, int var3, int var4, int var5, int var6, boolean var7) {
         super((<undefinedtype>)null);
         if (var1 == null) {
            throw new IllegalArgumentException("colorSpace == null!");
         } else if (var1.getType() != 5) {
            throw new IllegalArgumentException("colorSpace is not of type TYPE_RGB!");
         } else if (var6 != 0 && var6 != 1 && var6 != 3) {
            throw new IllegalArgumentException("Bad value for transferType!");
         } else if (var2 == 0 && var3 == 0 && var4 == 0 && var5 == 0) {
            throw new IllegalArgumentException("No mask has at least 1 bit set!");
         } else {
            this.colorSpace = var1;
            this.redMask = var2;
            this.greenMask = var3;
            this.blueMask = var4;
            this.alphaMask = var5;
            this.transferType = var6;
            this.isAlphaPremultiplied = var7;
            byte var8 = 32;
            this.colorModel = new DirectColorModel(var1, var8, var2, var3, var4, var5, var7, var6);
            this.sampleModel = this.colorModel.createCompatibleSampleModel(1, 1);
         }
      }
   }
}
