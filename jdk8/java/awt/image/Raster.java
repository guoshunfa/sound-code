package java.awt.image;

import java.awt.Point;
import java.awt.Rectangle;
import sun.awt.image.ByteBandedRaster;
import sun.awt.image.ByteInterleavedRaster;
import sun.awt.image.BytePackedRaster;
import sun.awt.image.IntegerInterleavedRaster;
import sun.awt.image.ShortBandedRaster;
import sun.awt.image.ShortInterleavedRaster;
import sun.awt.image.SunWritableRaster;

public class Raster {
   protected SampleModel sampleModel;
   protected DataBuffer dataBuffer;
   protected int minX;
   protected int minY;
   protected int width;
   protected int height;
   protected int sampleModelTranslateX;
   protected int sampleModelTranslateY;
   protected int numBands;
   protected int numDataElements;
   protected Raster parent;

   private static native void initIDs();

   public static WritableRaster createInterleavedRaster(int var0, int var1, int var2, int var3, Point var4) {
      int[] var5 = new int[var3];

      for(int var6 = 0; var6 < var3; var5[var6] = var6++) {
      }

      return createInterleavedRaster(var0, var1, var2, var1 * var3, var3, var5, var4);
   }

   public static WritableRaster createInterleavedRaster(int var0, int var1, int var2, int var3, int var4, int[] var5, Point var6) {
      int var8 = var3 * (var2 - 1) + var4 * var1;
      Object var7;
      switch(var0) {
      case 0:
         var7 = new DataBufferByte(var8);
         break;
      case 1:
         var7 = new DataBufferUShort(var8);
         break;
      default:
         throw new IllegalArgumentException("Unsupported data type " + var0);
      }

      return createInterleavedRaster((DataBuffer)var7, var1, var2, var3, var4, var5, var6);
   }

   public static WritableRaster createBandedRaster(int var0, int var1, int var2, int var3, Point var4) {
      if (var3 < 1) {
         throw new ArrayIndexOutOfBoundsException("Number of bands (" + var3 + ") must be greater than 0");
      } else {
         int[] var5 = new int[var3];
         int[] var6 = new int[var3];

         for(int var7 = 0; var7 < var3; ++var7) {
            var5[var7] = var7;
            var6[var7] = 0;
         }

         return createBandedRaster(var0, var1, var2, var1, var5, var6, var4);
      }
   }

   public static WritableRaster createBandedRaster(int var0, int var1, int var2, int var3, int[] var4, int[] var5, Point var6) {
      int var8 = var5.length;
      if (var4 == null) {
         throw new ArrayIndexOutOfBoundsException("Bank indices array is null");
      } else if (var5 == null) {
         throw new ArrayIndexOutOfBoundsException("Band offsets array is null");
      } else {
         int var9 = var4[0];
         int var10 = var5[0];

         int var11;
         for(var11 = 1; var11 < var8; ++var11) {
            if (var4[var11] > var9) {
               var9 = var4[var11];
            }

            if (var5[var11] > var10) {
               var10 = var5[var11];
            }
         }

         var11 = var9 + 1;
         int var12 = var10 + var3 * (var2 - 1) + var1;
         Object var7;
         switch(var0) {
         case 0:
            var7 = new DataBufferByte(var12, var11);
            break;
         case 1:
            var7 = new DataBufferUShort(var12, var11);
            break;
         case 2:
         default:
            throw new IllegalArgumentException("Unsupported data type " + var0);
         case 3:
            var7 = new DataBufferInt(var12, var11);
         }

         return createBandedRaster((DataBuffer)var7, var1, var2, var3, var4, var5, var6);
      }
   }

   public static WritableRaster createPackedRaster(int var0, int var1, int var2, int[] var3, Point var4) {
      Object var5;
      switch(var0) {
      case 0:
         var5 = new DataBufferByte(var1 * var2);
         break;
      case 1:
         var5 = new DataBufferUShort(var1 * var2);
         break;
      case 2:
      default:
         throw new IllegalArgumentException("Unsupported data type " + var0);
      case 3:
         var5 = new DataBufferInt(var1 * var2);
      }

      return createPackedRaster((DataBuffer)var5, var1, var2, var1, var3, var4);
   }

   public static WritableRaster createPackedRaster(int var0, int var1, int var2, int var3, int var4, Point var5) {
      if (var3 <= 0) {
         throw new IllegalArgumentException("Number of bands (" + var3 + ") must be greater than 0");
      } else if (var4 <= 0) {
         throw new IllegalArgumentException("Bits per band (" + var4 + ") must be greater than 0");
      } else if (var3 == 1) {
         double var11 = (double)var1;
         Object var6;
         switch(var0) {
         case 0:
            var6 = new DataBufferByte((int)Math.ceil(var11 / (double)(8 / var4)) * var2);
            break;
         case 1:
            var6 = new DataBufferUShort((int)Math.ceil(var11 / (double)(16 / var4)) * var2);
            break;
         case 2:
         default:
            throw new IllegalArgumentException("Unsupported data type " + var0);
         case 3:
            var6 = new DataBufferInt((int)Math.ceil(var11 / (double)(32 / var4)) * var2);
         }

         return createPackedRaster((DataBuffer)var6, var1, var2, var4, var5);
      } else {
         int[] var7 = new int[var3];
         int var8 = (1 << var4) - 1;
         int var9 = (var3 - 1) * var4;
         if (var9 + var4 > DataBuffer.getDataTypeSize(var0)) {
            throw new IllegalArgumentException("bitsPerBand(" + var4 + ") * bands is  greater than data type size.");
         } else {
            switch(var0) {
            case 0:
            case 1:
            case 3:
               for(int var10 = 0; var10 < var3; ++var10) {
                  var7[var10] = var8 << var9;
                  var9 -= var4;
               }

               return createPackedRaster(var0, var1, var2, var7, var5);
            case 2:
            default:
               throw new IllegalArgumentException("Unsupported data type " + var0);
            }
         }
      }
   }

   public static WritableRaster createInterleavedRaster(DataBuffer var0, int var1, int var2, int var3, int var4, int[] var5, Point var6) {
      if (var0 == null) {
         throw new NullPointerException("DataBuffer cannot be null");
      } else {
         if (var6 == null) {
            var6 = new Point(0, 0);
         }

         int var7 = var0.getDataType();
         PixelInterleavedSampleModel var8 = new PixelInterleavedSampleModel(var7, var1, var2, var4, var3, var5);
         switch(var7) {
         case 0:
            return new ByteInterleavedRaster(var8, var0, var6);
         case 1:
            return new ShortInterleavedRaster(var8, var0, var6);
         default:
            throw new IllegalArgumentException("Unsupported data type " + var7);
         }
      }
   }

   public static WritableRaster createBandedRaster(DataBuffer var0, int var1, int var2, int var3, int[] var4, int[] var5, Point var6) {
      if (var0 == null) {
         throw new NullPointerException("DataBuffer cannot be null");
      } else {
         if (var6 == null) {
            var6 = new Point(0, 0);
         }

         int var7 = var0.getDataType();
         int var8 = var4.length;
         if (var5.length != var8) {
            throw new IllegalArgumentException("bankIndices.length != bandOffsets.length");
         } else {
            BandedSampleModel var9 = new BandedSampleModel(var7, var1, var2, var3, var4, var5);
            switch(var7) {
            case 0:
               return new ByteBandedRaster(var9, var0, var6);
            case 1:
               return new ShortBandedRaster(var9, var0, var6);
            case 2:
            default:
               throw new IllegalArgumentException("Unsupported data type " + var7);
            case 3:
               return new SunWritableRaster(var9, var0, var6);
            }
         }
      }
   }

   public static WritableRaster createPackedRaster(DataBuffer var0, int var1, int var2, int var3, int[] var4, Point var5) {
      if (var0 == null) {
         throw new NullPointerException("DataBuffer cannot be null");
      } else {
         if (var5 == null) {
            var5 = new Point(0, 0);
         }

         int var6 = var0.getDataType();
         SinglePixelPackedSampleModel var7 = new SinglePixelPackedSampleModel(var6, var1, var2, var3, var4);
         switch(var6) {
         case 0:
            return new ByteInterleavedRaster(var7, var0, var5);
         case 1:
            return new ShortInterleavedRaster(var7, var0, var5);
         case 2:
         default:
            throw new IllegalArgumentException("Unsupported data type " + var6);
         case 3:
            return new IntegerInterleavedRaster(var7, var0, var5);
         }
      }
   }

   public static WritableRaster createPackedRaster(DataBuffer var0, int var1, int var2, int var3, Point var4) {
      if (var0 == null) {
         throw new NullPointerException("DataBuffer cannot be null");
      } else {
         if (var4 == null) {
            var4 = new Point(0, 0);
         }

         int var5 = var0.getDataType();
         if (var5 != 0 && var5 != 1 && var5 != 3) {
            throw new IllegalArgumentException("Unsupported data type " + var5);
         } else if (var0.getNumBanks() != 1) {
            throw new RasterFormatException("DataBuffer for packed Rasters must only have 1 bank.");
         } else {
            MultiPixelPackedSampleModel var6 = new MultiPixelPackedSampleModel(var5, var1, var2, var3);
            return (WritableRaster)(var5 == 0 && (var3 == 1 || var3 == 2 || var3 == 4) ? new BytePackedRaster(var6, var0, var4) : new SunWritableRaster(var6, var0, var4));
         }
      }
   }

   public static Raster createRaster(SampleModel var0, DataBuffer var1, Point var2) {
      if (var0 != null && var1 != null) {
         if (var2 == null) {
            var2 = new Point(0, 0);
         }

         int var3 = var0.getDataType();
         if (var0 instanceof PixelInterleavedSampleModel) {
            switch(var3) {
            case 0:
               return new ByteInterleavedRaster(var0, var1, var2);
            case 1:
               return new ShortInterleavedRaster(var0, var1, var2);
            }
         } else if (var0 instanceof SinglePixelPackedSampleModel) {
            switch(var3) {
            case 0:
               return new ByteInterleavedRaster(var0, var1, var2);
            case 1:
               return new ShortInterleavedRaster(var0, var1, var2);
            case 2:
            default:
               break;
            case 3:
               return new IntegerInterleavedRaster(var0, var1, var2);
            }
         } else if (var0 instanceof MultiPixelPackedSampleModel && var3 == 0 && var0.getSampleSize(0) < 8) {
            return new BytePackedRaster(var0, var1, var2);
         }

         return new Raster(var0, var1, var2);
      } else {
         throw new NullPointerException("SampleModel and DataBuffer cannot be null");
      }
   }

   public static WritableRaster createWritableRaster(SampleModel var0, Point var1) {
      if (var1 == null) {
         var1 = new Point(0, 0);
      }

      return createWritableRaster(var0, var0.createDataBuffer(), var1);
   }

   public static WritableRaster createWritableRaster(SampleModel var0, DataBuffer var1, Point var2) {
      if (var0 != null && var1 != null) {
         if (var2 == null) {
            var2 = new Point(0, 0);
         }

         int var3 = var0.getDataType();
         if (var0 instanceof PixelInterleavedSampleModel) {
            switch(var3) {
            case 0:
               return new ByteInterleavedRaster(var0, var1, var2);
            case 1:
               return new ShortInterleavedRaster(var0, var1, var2);
            }
         } else if (var0 instanceof SinglePixelPackedSampleModel) {
            switch(var3) {
            case 0:
               return new ByteInterleavedRaster(var0, var1, var2);
            case 1:
               return new ShortInterleavedRaster(var0, var1, var2);
            case 2:
            default:
               break;
            case 3:
               return new IntegerInterleavedRaster(var0, var1, var2);
            }
         } else if (var0 instanceof MultiPixelPackedSampleModel && var3 == 0 && var0.getSampleSize(0) < 8) {
            return new BytePackedRaster(var0, var1, var2);
         }

         return new SunWritableRaster(var0, var1, var2);
      } else {
         throw new NullPointerException("SampleModel and DataBuffer cannot be null");
      }
   }

   protected Raster(SampleModel var1, Point var2) {
      this(var1, var1.createDataBuffer(), new Rectangle(var2.x, var2.y, var1.getWidth(), var1.getHeight()), var2, (Raster)null);
   }

   protected Raster(SampleModel var1, DataBuffer var2, Point var3) {
      this(var1, var2, new Rectangle(var3.x, var3.y, var1.getWidth(), var1.getHeight()), var3, (Raster)null);
   }

   protected Raster(SampleModel var1, DataBuffer var2, Rectangle var3, Point var4, Raster var5) {
      if (var1 != null && var2 != null && var3 != null && var4 != null) {
         this.sampleModel = var1;
         this.dataBuffer = var2;
         this.minX = var3.x;
         this.minY = var3.y;
         this.width = var3.width;
         this.height = var3.height;
         if (this.width > 0 && this.height > 0) {
            if (this.minX + this.width < this.minX) {
               throw new RasterFormatException("overflow condition for X coordinates of Raster");
            } else if (this.minY + this.height < this.minY) {
               throw new RasterFormatException("overflow condition for Y coordinates of Raster");
            } else {
               this.sampleModelTranslateX = var4.x;
               this.sampleModelTranslateY = var4.y;
               this.numBands = var1.getNumBands();
               this.numDataElements = var1.getNumDataElements();
               this.parent = var5;
            }
         } else {
            throw new RasterFormatException("negative or zero " + (this.width <= 0 ? "width" : "height"));
         }
      } else {
         throw new NullPointerException("SampleModel, dataBuffer, aRegion and sampleModelTranslate cannot be null");
      }
   }

   public Raster getParent() {
      return this.parent;
   }

   public final int getSampleModelTranslateX() {
      return this.sampleModelTranslateX;
   }

   public final int getSampleModelTranslateY() {
      return this.sampleModelTranslateY;
   }

   public WritableRaster createCompatibleWritableRaster() {
      return new SunWritableRaster(this.sampleModel, new Point(0, 0));
   }

   public WritableRaster createCompatibleWritableRaster(int var1, int var2) {
      if (var1 > 0 && var2 > 0) {
         SampleModel var3 = this.sampleModel.createCompatibleSampleModel(var1, var2);
         return new SunWritableRaster(var3, new Point(0, 0));
      } else {
         throw new RasterFormatException("negative " + (var1 <= 0 ? "width" : "height"));
      }
   }

   public WritableRaster createCompatibleWritableRaster(Rectangle var1) {
      if (var1 == null) {
         throw new NullPointerException("Rect cannot be null");
      } else {
         return this.createCompatibleWritableRaster(var1.x, var1.y, var1.width, var1.height);
      }
   }

   public WritableRaster createCompatibleWritableRaster(int var1, int var2, int var3, int var4) {
      WritableRaster var5 = this.createCompatibleWritableRaster(var3, var4);
      return var5.createWritableChild(0, 0, var3, var4, var1, var2, (int[])null);
   }

   public Raster createTranslatedChild(int var1, int var2) {
      return this.createChild(this.minX, this.minY, this.width, this.height, var1, var2, (int[])null);
   }

   public Raster createChild(int var1, int var2, int var3, int var4, int var5, int var6, int[] var7) {
      if (var1 < this.minX) {
         throw new RasterFormatException("parentX lies outside raster");
      } else if (var2 < this.minY) {
         throw new RasterFormatException("parentY lies outside raster");
      } else if (var1 + var3 >= var1 && var1 + var3 <= this.width + this.minX) {
         if (var2 + var4 >= var2 && var2 + var4 <= this.height + this.minY) {
            SampleModel var8;
            if (var7 == null) {
               var8 = this.sampleModel;
            } else {
               var8 = this.sampleModel.createSubsetSampleModel(var7);
            }

            int var9 = var5 - var1;
            int var10 = var6 - var2;
            return new Raster(var8, this.getDataBuffer(), new Rectangle(var5, var6, var3, var4), new Point(this.sampleModelTranslateX + var9, this.sampleModelTranslateY + var10), this);
         } else {
            throw new RasterFormatException("(parentY + height) is outside raster");
         }
      } else {
         throw new RasterFormatException("(parentX + width) is outside raster");
      }
   }

   public Rectangle getBounds() {
      return new Rectangle(this.minX, this.minY, this.width, this.height);
   }

   public final int getMinX() {
      return this.minX;
   }

   public final int getMinY() {
      return this.minY;
   }

   public final int getWidth() {
      return this.width;
   }

   public final int getHeight() {
      return this.height;
   }

   public final int getNumBands() {
      return this.numBands;
   }

   public final int getNumDataElements() {
      return this.sampleModel.getNumDataElements();
   }

   public final int getTransferType() {
      return this.sampleModel.getTransferType();
   }

   public DataBuffer getDataBuffer() {
      return this.dataBuffer;
   }

   public SampleModel getSampleModel() {
      return this.sampleModel;
   }

   public Object getDataElements(int var1, int var2, Object var3) {
      return this.sampleModel.getDataElements(var1 - this.sampleModelTranslateX, var2 - this.sampleModelTranslateY, var3, this.dataBuffer);
   }

   public Object getDataElements(int var1, int var2, int var3, int var4, Object var5) {
      return this.sampleModel.getDataElements(var1 - this.sampleModelTranslateX, var2 - this.sampleModelTranslateY, var3, var4, var5, this.dataBuffer);
   }

   public int[] getPixel(int var1, int var2, int[] var3) {
      return this.sampleModel.getPixel(var1 - this.sampleModelTranslateX, var2 - this.sampleModelTranslateY, var3, this.dataBuffer);
   }

   public float[] getPixel(int var1, int var2, float[] var3) {
      return this.sampleModel.getPixel(var1 - this.sampleModelTranslateX, var2 - this.sampleModelTranslateY, var3, this.dataBuffer);
   }

   public double[] getPixel(int var1, int var2, double[] var3) {
      return this.sampleModel.getPixel(var1 - this.sampleModelTranslateX, var2 - this.sampleModelTranslateY, var3, this.dataBuffer);
   }

   public int[] getPixels(int var1, int var2, int var3, int var4, int[] var5) {
      return this.sampleModel.getPixels(var1 - this.sampleModelTranslateX, var2 - this.sampleModelTranslateY, var3, var4, var5, this.dataBuffer);
   }

   public float[] getPixels(int var1, int var2, int var3, int var4, float[] var5) {
      return this.sampleModel.getPixels(var1 - this.sampleModelTranslateX, var2 - this.sampleModelTranslateY, var3, var4, var5, this.dataBuffer);
   }

   public double[] getPixels(int var1, int var2, int var3, int var4, double[] var5) {
      return this.sampleModel.getPixels(var1 - this.sampleModelTranslateX, var2 - this.sampleModelTranslateY, var3, var4, var5, this.dataBuffer);
   }

   public int getSample(int var1, int var2, int var3) {
      return this.sampleModel.getSample(var1 - this.sampleModelTranslateX, var2 - this.sampleModelTranslateY, var3, this.dataBuffer);
   }

   public float getSampleFloat(int var1, int var2, int var3) {
      return this.sampleModel.getSampleFloat(var1 - this.sampleModelTranslateX, var2 - this.sampleModelTranslateY, var3, this.dataBuffer);
   }

   public double getSampleDouble(int var1, int var2, int var3) {
      return this.sampleModel.getSampleDouble(var1 - this.sampleModelTranslateX, var2 - this.sampleModelTranslateY, var3, this.dataBuffer);
   }

   public int[] getSamples(int var1, int var2, int var3, int var4, int var5, int[] var6) {
      return this.sampleModel.getSamples(var1 - this.sampleModelTranslateX, var2 - this.sampleModelTranslateY, var3, var4, var5, var6, this.dataBuffer);
   }

   public float[] getSamples(int var1, int var2, int var3, int var4, int var5, float[] var6) {
      return this.sampleModel.getSamples(var1 - this.sampleModelTranslateX, var2 - this.sampleModelTranslateY, var3, var4, var5, var6, this.dataBuffer);
   }

   public double[] getSamples(int var1, int var2, int var3, int var4, int var5, double[] var6) {
      return this.sampleModel.getSamples(var1 - this.sampleModelTranslateX, var2 - this.sampleModelTranslateY, var3, var4, var5, var6, this.dataBuffer);
   }

   static {
      ColorModel.loadLibraries();
      initIDs();
   }
}
