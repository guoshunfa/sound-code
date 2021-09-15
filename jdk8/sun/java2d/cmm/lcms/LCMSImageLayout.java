package sun.java2d.cmm.lcms;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;
import sun.awt.image.ByteComponentRaster;
import sun.awt.image.IntegerComponentRaster;
import sun.awt.image.ShortComponentRaster;

class LCMSImageLayout {
   public static final int SWAPFIRST = 16384;
   public static final int DOSWAP = 1024;
   public static final int PT_RGB_8 = CHANNELS_SH(3) | BYTES_SH(1);
   public static final int PT_GRAY_8 = CHANNELS_SH(1) | BYTES_SH(1);
   public static final int PT_GRAY_16 = CHANNELS_SH(1) | BYTES_SH(2);
   public static final int PT_RGBA_8 = EXTRA_SH(1) | CHANNELS_SH(3) | BYTES_SH(1);
   public static final int PT_ARGB_8 = EXTRA_SH(1) | CHANNELS_SH(3) | BYTES_SH(1) | 16384;
   public static final int PT_BGR_8 = 1024 | CHANNELS_SH(3) | BYTES_SH(1);
   public static final int PT_ABGR_8 = 1024 | EXTRA_SH(1) | CHANNELS_SH(3) | BYTES_SH(1);
   public static final int PT_BGRA_8 = EXTRA_SH(1) | CHANNELS_SH(3) | BYTES_SH(1) | 1024 | 16384;
   public static final int DT_BYTE = 0;
   public static final int DT_SHORT = 1;
   public static final int DT_INT = 2;
   public static final int DT_DOUBLE = 3;
   boolean isIntPacked;
   int pixelType;
   int dataType;
   int width;
   int height;
   int nextRowOffset;
   private int nextPixelOffset;
   int offset;
   private boolean imageAtOnce;
   Object dataArray;
   private int dataArrayLength;

   public static int BYTES_SH(int var0) {
      return var0;
   }

   public static int EXTRA_SH(int var0) {
      return var0 << 7;
   }

   public static int CHANNELS_SH(int var0) {
      return var0 << 3;
   }

   private LCMSImageLayout(int var1, int var2, int var3) throws LCMSImageLayout.ImageLayoutException {
      this.isIntPacked = false;
      this.imageAtOnce = false;
      this.pixelType = var2;
      this.width = var1;
      this.height = 1;
      this.nextPixelOffset = var3;
      this.nextRowOffset = safeMult(var3, var1);
      this.offset = 0;
   }

   private LCMSImageLayout(int var1, int var2, int var3, int var4) throws LCMSImageLayout.ImageLayoutException {
      this.isIntPacked = false;
      this.imageAtOnce = false;
      this.pixelType = var3;
      this.width = var1;
      this.height = var2;
      this.nextPixelOffset = var4;
      this.nextRowOffset = safeMult(var4, var1);
      this.offset = 0;
   }

   public LCMSImageLayout(byte[] var1, int var2, int var3, int var4) throws LCMSImageLayout.ImageLayoutException {
      this(var2, var3, var4);
      this.dataType = 0;
      this.dataArray = var1;
      this.dataArrayLength = var1.length;
      this.verify();
   }

   public LCMSImageLayout(short[] var1, int var2, int var3, int var4) throws LCMSImageLayout.ImageLayoutException {
      this(var2, var3, var4);
      this.dataType = 1;
      this.dataArray = var1;
      this.dataArrayLength = 2 * var1.length;
      this.verify();
   }

   public LCMSImageLayout(int[] var1, int var2, int var3, int var4) throws LCMSImageLayout.ImageLayoutException {
      this(var2, var3, var4);
      this.dataType = 2;
      this.dataArray = var1;
      this.dataArrayLength = 4 * var1.length;
      this.verify();
   }

   public LCMSImageLayout(double[] var1, int var2, int var3, int var4) throws LCMSImageLayout.ImageLayoutException {
      this(var2, var3, var4);
      this.dataType = 3;
      this.dataArray = var1;
      this.dataArrayLength = 8 * var1.length;
      this.verify();
   }

   private LCMSImageLayout() {
      this.isIntPacked = false;
      this.imageAtOnce = false;
   }

   public static LCMSImageLayout createImageLayout(BufferedImage var0) throws LCMSImageLayout.ImageLayoutException {
      LCMSImageLayout var1 = new LCMSImageLayout();
      switch(var0.getType()) {
      case 1:
         var1.pixelType = PT_ARGB_8;
         var1.isIntPacked = true;
         break;
      case 2:
         var1.pixelType = PT_ARGB_8;
         var1.isIntPacked = true;
         break;
      case 3:
      case 7:
      case 8:
      case 9:
      default:
         ColorModel var11 = var0.getColorModel();
         if (var11 instanceof ComponentColorModel) {
            ComponentColorModel var12 = (ComponentColorModel)var11;
            int[] var4 = var12.getComponentSize();
            int[] var5 = var4;
            int var6 = var4.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               int var8 = var5[var7];
               if (var8 != 8) {
                  return null;
               }
            }

            return createImageLayout((Raster)var0.getRaster());
         }

         return null;
      case 4:
         var1.pixelType = PT_ABGR_8;
         var1.isIntPacked = true;
         break;
      case 5:
         var1.pixelType = PT_BGR_8;
         break;
      case 6:
         var1.pixelType = PT_ABGR_8;
         break;
      case 10:
         var1.pixelType = PT_GRAY_8;
         break;
      case 11:
         var1.pixelType = PT_GRAY_16;
      }

      var1.width = var0.getWidth();
      var1.height = var0.getHeight();
      ByteComponentRaster var9;
      switch(var0.getType()) {
      case 1:
      case 2:
      case 4:
         IntegerComponentRaster var10 = (IntegerComponentRaster)var0.getRaster();
         var1.nextRowOffset = safeMult(4, var10.getScanlineStride());
         var1.nextPixelOffset = safeMult(4, var10.getPixelStride());
         var1.offset = safeMult(4, var10.getDataOffset(0));
         var1.dataArray = var10.getDataStorage();
         var1.dataArrayLength = 4 * var10.getDataStorage().length;
         var1.dataType = 2;
         if (var1.nextRowOffset == var1.width * 4 * var10.getPixelStride()) {
            var1.imageAtOnce = true;
         }
         break;
      case 3:
      case 7:
      case 8:
      case 9:
      default:
         return null;
      case 5:
      case 6:
         var9 = (ByteComponentRaster)var0.getRaster();
         var1.nextRowOffset = var9.getScanlineStride();
         var1.nextPixelOffset = var9.getPixelStride();
         int var3 = var0.getSampleModel().getNumBands() - 1;
         var1.offset = var9.getDataOffset(var3);
         var1.dataArray = var9.getDataStorage();
         var1.dataArrayLength = var9.getDataStorage().length;
         var1.dataType = 0;
         if (var1.nextRowOffset == var1.width * var9.getPixelStride()) {
            var1.imageAtOnce = true;
         }
         break;
      case 10:
         var9 = (ByteComponentRaster)var0.getRaster();
         var1.nextRowOffset = var9.getScanlineStride();
         var1.nextPixelOffset = var9.getPixelStride();
         var1.dataArrayLength = var9.getDataStorage().length;
         var1.offset = var9.getDataOffset(0);
         var1.dataArray = var9.getDataStorage();
         var1.dataType = 0;
         if (var1.nextRowOffset == var1.width * var9.getPixelStride()) {
            var1.imageAtOnce = true;
         }
         break;
      case 11:
         ShortComponentRaster var2 = (ShortComponentRaster)var0.getRaster();
         var1.nextRowOffset = safeMult(2, var2.getScanlineStride());
         var1.nextPixelOffset = safeMult(2, var2.getPixelStride());
         var1.offset = safeMult(2, var2.getDataOffset(0));
         var1.dataArray = var2.getDataStorage();
         var1.dataArrayLength = 2 * var2.getDataStorage().length;
         var1.dataType = 1;
         if (var1.nextRowOffset == var1.width * 2 * var2.getPixelStride()) {
            var1.imageAtOnce = true;
         }
      }

      var1.verify();
      return var1;
   }

   private void verify() throws LCMSImageLayout.ImageLayoutException {
      if (this.offset >= 0 && this.offset < this.dataArrayLength) {
         if (this.nextPixelOffset != getBytesPerPixel(this.pixelType)) {
            throw new LCMSImageLayout.ImageLayoutException("Invalid image layout");
         } else {
            int var1 = safeMult(this.nextRowOffset, this.height - 1);
            int var2 = safeMult(this.nextPixelOffset, this.width - 1);
            var2 = safeAdd(var2, var1);
            int var3 = safeAdd(this.offset, var2);
            if (var3 < 0 || var3 >= this.dataArrayLength) {
               throw new LCMSImageLayout.ImageLayoutException("Invalid image layout");
            }
         }
      } else {
         throw new LCMSImageLayout.ImageLayoutException("Invalid image layout");
      }
   }

   static int safeAdd(int var0, int var1) throws LCMSImageLayout.ImageLayoutException {
      long var2 = (long)var0;
      var2 += (long)var1;
      if (var2 >= -2147483648L && var2 <= 2147483647L) {
         return (int)var2;
      } else {
         throw new LCMSImageLayout.ImageLayoutException("Invalid image layout");
      }
   }

   static int safeMult(int var0, int var1) throws LCMSImageLayout.ImageLayoutException {
      long var2 = (long)var0;
      var2 *= (long)var1;
      if (var2 >= -2147483648L && var2 <= 2147483647L) {
         return (int)var2;
      } else {
         throw new LCMSImageLayout.ImageLayoutException("Invalid image layout");
      }
   }

   public static LCMSImageLayout createImageLayout(Raster var0) {
      LCMSImageLayout var1 = new LCMSImageLayout();
      if (var0 instanceof ByteComponentRaster && var0.getSampleModel() instanceof ComponentSampleModel) {
         ByteComponentRaster var2 = (ByteComponentRaster)var0;
         ComponentSampleModel var3 = (ComponentSampleModel)var0.getSampleModel();
         var1.pixelType = CHANNELS_SH(var2.getNumBands()) | BYTES_SH(1);
         int[] var4 = var3.getBandOffsets();
         LCMSImageLayout.BandOrder var5 = LCMSImageLayout.BandOrder.getBandOrder(var4);
         int var6 = 0;
         switch(var5) {
         case INVERTED:
            var1.pixelType |= 1024;
            var6 = var3.getNumBands() - 1;
         case DIRECT:
            var1.nextRowOffset = var2.getScanlineStride();
            var1.nextPixelOffset = var2.getPixelStride();
            var1.offset = var2.getDataOffset(var6);
            var1.dataArray = var2.getDataStorage();
            var1.dataType = 0;
            var1.width = var2.getWidth();
            var1.height = var2.getHeight();
            if (var1.nextRowOffset == var1.width * var2.getPixelStride()) {
               var1.imageAtOnce = true;
            }

            return var1;
         default:
            return null;
         }
      } else {
         return null;
      }
   }

   private static int getBytesPerPixel(int var0) {
      int var1 = 7 & var0;
      int var2 = 15 & var0 >> 3;
      int var3 = 7 & var0 >> 7;
      return var1 * (var2 + var3);
   }

   public static class ImageLayoutException extends Exception {
      public ImageLayoutException(String var1) {
         super(var1);
      }
   }

   private static enum BandOrder {
      DIRECT,
      INVERTED,
      ARBITRARY,
      UNKNOWN;

      public static LCMSImageLayout.BandOrder getBandOrder(int[] var0) {
         LCMSImageLayout.BandOrder var1 = UNKNOWN;
         int var2 = var0.length;

         for(int var3 = 0; var1 != ARBITRARY && var3 < var0.length; ++var3) {
            switch(var1) {
            case UNKNOWN:
               if (var0[var3] == var3) {
                  var1 = DIRECT;
               } else if (var0[var3] == var2 - 1 - var3) {
                  var1 = INVERTED;
               } else {
                  var1 = ARBITRARY;
               }
               break;
            case DIRECT:
               if (var0[var3] != var3) {
                  var1 = ARBITRARY;
               }
               break;
            case INVERTED:
               if (var0[var3] != var2 - 1 - var3) {
                  var1 = ARBITRARY;
               }
            }
         }

         return var1;
      }
   }
}
