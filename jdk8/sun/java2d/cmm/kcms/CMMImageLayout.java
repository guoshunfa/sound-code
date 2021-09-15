package sun.java2d.cmm.kcms;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import sun.awt.image.ByteComponentRaster;
import sun.awt.image.IntegerComponentRaster;
import sun.awt.image.ShortComponentRaster;

class CMMImageLayout {
   private static final int typeBase = 256;
   public static final int typeComponentUByte = 256;
   public static final int typeComponentUShort12 = 257;
   public static final int typeComponentUShort = 258;
   public static final int typePixelUByte = 259;
   public static final int typePixelUShort12 = 260;
   public static final int typePixelUShort = 261;
   public static final int typeShort555 = 262;
   public static final int typeShort565 = 263;
   public static final int typeInt101010 = 264;
   public static final int typeIntRGBPacked = 265;
   public int Type;
   public int NumCols;
   public int NumRows;
   public int OffsetColumn;
   public int OffsetRow;
   public int NumChannels;
   public final boolean hasAlpha;
   public Object[] chanData;
   public int[] DataOffsets;
   public int[] sampleInfo;
   private int[] dataArrayLength;
   private static final int MAX_NumChannels = 9;

   public CMMImageLayout(byte[] var1, int var2, int var3) throws CMMImageLayout.ImageLayoutException {
      this.Type = 256;
      this.chanData = new Object[var3];
      this.DataOffsets = new int[var3];
      this.dataArrayLength = new int[var3];
      this.NumCols = var2;
      this.NumRows = 1;
      this.OffsetColumn = var3;
      this.OffsetRow = this.NumCols * this.OffsetColumn;
      this.NumChannels = var3;

      for(int var4 = 0; var4 < var3; ++var4) {
         this.chanData[var4] = var1;
         this.DataOffsets[var4] = var4;
         this.dataArrayLength[var4] = var1.length;
      }

      this.hasAlpha = false;
      this.verify();
   }

   public CMMImageLayout(short[] var1, int var2, int var3) throws CMMImageLayout.ImageLayoutException {
      this.Type = 258;
      this.chanData = new Object[var3];
      this.DataOffsets = new int[var3];
      this.dataArrayLength = new int[var3];
      this.NumCols = var2;
      this.NumRows = 1;
      this.OffsetColumn = safeMult(2, var3);
      this.OffsetRow = this.NumCols * this.OffsetColumn;
      this.NumChannels = var3;

      for(int var4 = 0; var4 < var3; ++var4) {
         this.chanData[var4] = var1;
         this.DataOffsets[var4] = var4 * 2;
         this.dataArrayLength[var4] = 2 * var1.length;
      }

      this.hasAlpha = false;
      this.verify();
   }

   public CMMImageLayout(BufferedImage var1) throws CMMImageLayout.ImageLayoutException {
      super();
      this.Type = var1.getType();
      this.NumCols = var1.getWidth();
      this.NumRows = var1.getHeight();
      WritableRaster var3 = var1.getRaster();
      int var4;
      int var6;
      int var8;
      ByteComponentRaster var10;
      byte[] var12;
      label88:
      switch(this.Type) {
      case 1:
      case 2:
      case 4:
         this.NumChannels = 3;
         this.hasAlpha = this.Type == 2;
         int var9 = this.hasAlpha ? 4 : 3;
         this.chanData = new Object[var9];
         this.DataOffsets = new int[var9];
         this.dataArrayLength = new int[var9];
         this.sampleInfo = new int[var9];
         this.OffsetColumn = 4;
         if (!(var3 instanceof IntegerComponentRaster)) {
            throw new CMMImageLayout.ImageLayoutException("Incompatible raster type");
         }

         IntegerComponentRaster var11 = (IntegerComponentRaster)var3;
         var6 = safeMult(4, var11.getPixelStride());
         if (var6 != this.OffsetColumn) {
            throw new CMMImageLayout.ImageLayoutException("Incompatible raster type");
         }

         this.OffsetRow = safeMult(4, var11.getScanlineStride());
         var4 = safeMult(4, var11.getDataOffset(0));
         int[] var13 = var11.getDataStorage();

         for(var8 = 0; var8 < 3; ++var8) {
            this.chanData[var8] = var13;
            this.DataOffsets[var8] = var4;
            this.dataArrayLength[var8] = 4 * var13.length;
            if (this.Type == 4) {
               this.sampleInfo[var8] = 3 - var8;
            } else {
               this.sampleInfo[var8] = var8 + 1;
            }
         }

         if (this.hasAlpha) {
            this.chanData[3] = var13;
            this.DataOffsets[3] = var4;
            this.dataArrayLength[3] = 4 * var13.length;
            this.sampleInfo[3] = 0;
         }
         break;
      case 3:
      case 7:
      case 8:
      case 9:
      default:
         throw new IllegalArgumentException("CMMImageLayout - bad image type passed to constructor");
      case 5:
      case 6:
         this.NumChannels = 3;
         this.hasAlpha = this.Type == 6;
         byte var2;
         if (this.hasAlpha) {
            this.OffsetColumn = 4;
            var2 = 4;
         } else {
            this.OffsetColumn = 3;
            var2 = 3;
         }

         this.chanData = new Object[var2];
         this.DataOffsets = new int[var2];
         this.dataArrayLength = new int[var2];
         if (!(var3 instanceof ByteComponentRaster)) {
            throw new CMMImageLayout.ImageLayoutException("Incompatible raster type");
         }

         var10 = (ByteComponentRaster)var3;
         var6 = var10.getPixelStride();
         if (var6 != this.OffsetColumn) {
            throw new CMMImageLayout.ImageLayoutException("Incompatible raster type");
         }

         this.OffsetRow = var10.getScanlineStride();
         var4 = var10.getDataOffset(0);
         var12 = var10.getDataStorage();
         var8 = 0;

         while(true) {
            if (var8 >= var2) {
               break label88;
            }

            this.chanData[var8] = var12;
            this.DataOffsets[var8] = var4 - var8;
            this.dataArrayLength[var8] = var12.length;
            ++var8;
         }
      case 10:
         this.Type = 256;
         this.NumChannels = 1;
         this.hasAlpha = false;
         this.chanData = new Object[1];
         this.DataOffsets = new int[1];
         this.dataArrayLength = new int[1];
         this.OffsetColumn = 1;
         if (!(var3 instanceof ByteComponentRaster)) {
            throw new CMMImageLayout.ImageLayoutException("Incompatible raster type");
         }

         var10 = (ByteComponentRaster)var3;
         var6 = var10.getPixelStride();
         if (var6 != this.OffsetColumn) {
            throw new CMMImageLayout.ImageLayoutException("Incompatible raster type");
         }

         this.OffsetRow = var10.getScanlineStride();
         var12 = var10.getDataStorage();
         this.chanData[0] = var12;
         this.dataArrayLength[0] = var12.length;
         this.DataOffsets[0] = var10.getDataOffset(0);
         break;
      case 11:
         this.Type = 258;
         this.NumChannels = 1;
         this.hasAlpha = false;
         this.chanData = new Object[1];
         this.DataOffsets = new int[1];
         this.dataArrayLength = new int[1];
         this.OffsetColumn = 2;
         if (!(var3 instanceof ShortComponentRaster)) {
            throw new CMMImageLayout.ImageLayoutException("Incompatible raster type");
         }

         ShortComponentRaster var5 = (ShortComponentRaster)var3;
         var6 = safeMult(2, var5.getPixelStride());
         if (var6 != this.OffsetColumn) {
            throw new CMMImageLayout.ImageLayoutException("Incompatible raster type");
         }

         this.OffsetRow = safeMult(2, var5.getScanlineStride());
         this.DataOffsets[0] = safeMult(2, var5.getDataOffset(0));
         short[] var7 = var5.getDataStorage();
         this.chanData[0] = var7;
         this.dataArrayLength[0] = 2 * var7.length;
      }

      this.verify();
   }

   public CMMImageLayout(BufferedImage var1, SinglePixelPackedSampleModel var2, int var3, int var4, int var5, int var6) throws CMMImageLayout.ImageLayoutException {
      super();
      this.Type = 265;
      this.NumChannels = 3;
      this.NumCols = var1.getWidth();
      this.NumRows = var1.getHeight();
      this.hasAlpha = var6 >= 0;
      int var7 = this.hasAlpha ? 4 : 3;
      this.chanData = new Object[var7];
      this.DataOffsets = new int[var7];
      this.dataArrayLength = new int[var7];
      this.sampleInfo = new int[var7];
      this.OffsetColumn = 4;
      int var8 = var2.getScanlineStride();
      this.OffsetRow = safeMult(4, var8);
      WritableRaster var9 = var1.getRaster();
      DataBufferInt var10 = (DataBufferInt)var9.getDataBuffer();
      int var11 = var9.getSampleModelTranslateX();
      int var12 = var9.getSampleModelTranslateY();
      int var13 = safeMult(var12, var8);
      int var14 = safeMult(4, var11);
      var14 = safeAdd(var14, var13);
      int var15 = safeAdd(var10.getOffset(), -var14);
      int[] var16 = var10.getData();

      for(int var17 = 0; var17 < var7; ++var17) {
         this.chanData[var17] = var16;
         this.DataOffsets[var17] = var15;
         this.dataArrayLength[var17] = var16.length * 4;
      }

      this.sampleInfo[0] = var3;
      this.sampleInfo[1] = var4;
      this.sampleInfo[2] = var5;
      if (this.hasAlpha) {
         this.sampleInfo[3] = var6;
      }

      this.verify();
   }

   public CMMImageLayout(BufferedImage var1, ComponentSampleModel var2) throws CMMImageLayout.ImageLayoutException {
      super();
      ColorModel var3 = var1.getColorModel();
      int var4 = var3.getNumColorComponents();
      if (var4 >= 0 && var4 <= 9) {
         this.hasAlpha = var3.hasAlpha();
         WritableRaster var5 = var1.getRaster();
         int[] var6 = var2.getBankIndices();
         int[] var7 = var2.getBandOffsets();
         this.NumChannels = var4;
         this.NumCols = var1.getWidth();
         this.NumRows = var1.getHeight();
         if (this.hasAlpha) {
            ++var4;
         }

         this.chanData = new Object[var4];
         this.DataOffsets = new int[var4];
         this.dataArrayLength = new int[var4];
         int var8 = var5.getSampleModelTranslateY();
         int var9 = var5.getSampleModelTranslateX();
         int var10 = var2.getScanlineStride();
         int var11 = var2.getPixelStride();
         int var12 = safeMult(var10, var8);
         int var13 = safeMult(var11, var9);
         var13 = safeAdd(var13, var12);
         int[] var15;
         int var16;
         int var18;
         label31:
         switch(var2.getDataType()) {
         case 0:
            this.Type = 256;
            this.OffsetColumn = var11;
            this.OffsetRow = var10;
            DataBufferByte var19 = (DataBufferByte)var5.getDataBuffer();
            var15 = var19.getOffsets();
            var16 = 0;

            while(true) {
               if (var16 >= var4) {
                  break label31;
               }

               byte[] var20 = var19.getData(var6[var16]);
               this.chanData[var16] = var20;
               this.dataArrayLength[var16] = var20.length;
               var18 = safeAdd(var15[var6[var16]], -var13);
               var18 = safeAdd(var18, var7[var16]);
               this.DataOffsets[var16] = var18;
               ++var16;
            }
         case 1:
            this.Type = 258;
            this.OffsetColumn = safeMult(2, var11);
            this.OffsetRow = safeMult(2, var10);
            DataBufferUShort var14 = (DataBufferUShort)var5.getDataBuffer();
            var15 = var14.getOffsets();
            var16 = 0;

            while(true) {
               if (var16 >= var4) {
                  break label31;
               }

               short[] var17 = var14.getData(var6[var16]);
               this.chanData[var16] = var17;
               this.dataArrayLength[var16] = var17.length * 2;
               var18 = safeAdd(var15[var6[var16]], -var13);
               var18 = safeAdd(var18, var7[var16]);
               this.DataOffsets[var16] = safeMult(2, var18);
               ++var16;
            }
         default:
            throw new IllegalArgumentException("CMMImageLayout - bad image type passed to constructor");
         }

         this.verify();
      } else {
         throw new CMMImageLayout.ImageLayoutException("Invalid image layout");
      }
   }

   public CMMImageLayout(Raster var1, ComponentSampleModel var2) throws CMMImageLayout.ImageLayoutException {
      super();
      int var3 = var1.getNumBands();
      if (var3 >= 0 && var3 <= 9) {
         int[] var4 = var2.getBankIndices();
         int[] var5 = var2.getBandOffsets();
         this.NumChannels = var3;
         this.NumCols = var1.getWidth();
         this.NumRows = var1.getHeight();
         this.hasAlpha = false;
         this.chanData = new Object[var3];
         this.DataOffsets = new int[var3];
         this.dataArrayLength = new int[var3];
         int var6 = var2.getScanlineStride();
         int var7 = var2.getPixelStride();
         int var8 = var1.getMinX();
         int var9 = var1.getMinY();
         int var10 = var1.getSampleModelTranslateX();
         int var11 = var1.getSampleModelTranslateY();
         int var12 = safeAdd(var9, -var11);
         var12 = safeMult(var12, var6);
         int var13 = safeAdd(var8, -var10);
         var13 = safeMult(var13, var7);
         var13 = safeAdd(var13, var12);
         int[] var15;
         int var16;
         int var18;
         label28:
         switch(var2.getDataType()) {
         case 0:
            this.Type = 256;
            this.OffsetColumn = var7;
            this.OffsetRow = var6;
            DataBufferByte var19 = (DataBufferByte)var1.getDataBuffer();
            var15 = var19.getOffsets();
            var16 = 0;

            while(true) {
               if (var16 >= var3) {
                  break label28;
               }

               byte[] var20 = var19.getData(var4[var16]);
               this.chanData[var16] = var20;
               this.dataArrayLength[var16] = var20.length;
               var18 = safeAdd(var15[var4[var16]], var13);
               this.DataOffsets[var16] = safeAdd(var18, var5[var16]);
               ++var16;
            }
         case 1:
            this.Type = 258;
            this.OffsetColumn = safeMult(2, var7);
            this.OffsetRow = safeMult(2, var6);
            DataBufferUShort var14 = (DataBufferUShort)var1.getDataBuffer();
            var15 = var14.getOffsets();
            var16 = 0;

            while(true) {
               if (var16 >= var3) {
                  break label28;
               }

               short[] var17 = var14.getData(var4[var16]);
               this.chanData[var16] = var17;
               this.dataArrayLength[var16] = var17.length * 2;
               var18 = safeAdd(var15[var4[var16]], var13);
               var18 = safeAdd(var18, var5[var16]);
               this.DataOffsets[var16] = safeMult(2, var18);
               ++var16;
            }
         default:
            throw new IllegalArgumentException("CMMImageLayout - bad image type passed to constructor");
         }

         this.verify();
      } else {
         throw new CMMImageLayout.ImageLayoutException("Invalid image layout");
      }
   }

   private final void verify() throws CMMImageLayout.ImageLayoutException {
      int var1 = safeMult(this.OffsetRow, this.NumRows - 1);
      int var2 = safeMult(this.OffsetColumn, this.NumCols - 1);
      var1 = safeAdd(var1, var2);
      int var3 = this.NumChannels;
      if (this.hasAlpha) {
         ++var3;
      }

      int var4 = 0;

      while(var4 < var3) {
         int var5 = this.DataOffsets[var4];
         if (var5 >= 0 && var5 < this.dataArrayLength[var4]) {
            var5 = safeAdd(var5, var1);
            if (var5 >= 0 && var5 < this.dataArrayLength[var4]) {
               ++var4;
               continue;
            }

            throw new CMMImageLayout.ImageLayoutException("Invalid image layout");
         }

         throw new CMMImageLayout.ImageLayoutException("Invalid image layout");
      }

   }

   static int safeAdd(int var0, int var1) throws CMMImageLayout.ImageLayoutException {
      long var2 = (long)var0;
      var2 += (long)var1;
      if (var2 >= -2147483648L && var2 <= 2147483647L) {
         return (int)var2;
      } else {
         throw new CMMImageLayout.ImageLayoutException("Invalid image layout");
      }
   }

   static int safeMult(int var0, int var1) throws CMMImageLayout.ImageLayoutException {
      long var2 = (long)var0;
      var2 *= (long)var1;
      if (var2 >= -2147483648L && var2 <= 2147483647L) {
         return (int)var2;
      } else {
         throw new CMMImageLayout.ImageLayoutException("Invalid image layout");
      }
   }

   public static class ImageLayoutException extends Exception {
      public ImageLayoutException(String var1) {
         super(var1);
      }

      public ImageLayoutException(String var1, Throwable var2) {
         super(var1, var2);
      }
   }
}
