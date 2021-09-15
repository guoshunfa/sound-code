package java.awt.image;

import java.awt.Point;
import java.awt.color.ColorSpace;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Hashtable;
import sun.awt.image.BufImgSurfaceData;

public class IndexColorModel extends ColorModel {
   private int[] rgb;
   private int map_size;
   private int pixel_mask;
   private int transparent_index;
   private boolean allgrayopaque;
   private BigInteger validBits;
   private BufImgSurfaceData.ICMColorData colorData;
   private static int[] opaqueBits = new int[]{8, 8, 8};
   private static int[] alphaBits = new int[]{8, 8, 8, 8};
   private static final int CACHESIZE = 40;
   private int[] lookupcache;

   private static native void initIDs();

   public IndexColorModel(int var1, int var2, byte[] var3, byte[] var4, byte[] var5) {
      super(var1, opaqueBits, ColorSpace.getInstance(1000), false, false, 1, ColorModel.getDefaultTransferType(var1));
      this.transparent_index = -1;
      this.colorData = null;
      this.lookupcache = new int[40];
      if (var1 >= 1 && var1 <= 16) {
         this.setRGBs(var2, var3, var4, var5, (byte[])null);
         this.calculatePixelMask();
      } else {
         throw new IllegalArgumentException("Number of bits must be between 1 and 16.");
      }
   }

   public IndexColorModel(int var1, int var2, byte[] var3, byte[] var4, byte[] var5, int var6) {
      super(var1, opaqueBits, ColorSpace.getInstance(1000), false, false, 1, ColorModel.getDefaultTransferType(var1));
      this.transparent_index = -1;
      this.colorData = null;
      this.lookupcache = new int[40];
      if (var1 >= 1 && var1 <= 16) {
         this.setRGBs(var2, var3, var4, var5, (byte[])null);
         this.setTransparentPixel(var6);
         this.calculatePixelMask();
      } else {
         throw new IllegalArgumentException("Number of bits must be between 1 and 16.");
      }
   }

   public IndexColorModel(int var1, int var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6) {
      super(var1, alphaBits, ColorSpace.getInstance(1000), true, false, 3, ColorModel.getDefaultTransferType(var1));
      this.transparent_index = -1;
      this.colorData = null;
      this.lookupcache = new int[40];
      if (var1 >= 1 && var1 <= 16) {
         this.setRGBs(var2, var3, var4, var5, var6);
         this.calculatePixelMask();
      } else {
         throw new IllegalArgumentException("Number of bits must be between 1 and 16.");
      }
   }

   public IndexColorModel(int var1, int var2, byte[] var3, int var4, boolean var5) {
      this(var1, var2, var3, var4, var5, -1);
      if (var1 < 1 || var1 > 16) {
         throw new IllegalArgumentException("Number of bits must be between 1 and 16.");
      }
   }

   public IndexColorModel(int var1, int var2, byte[] var3, int var4, boolean var5, int var6) {
      super(var1, opaqueBits, ColorSpace.getInstance(1000), false, false, 1, ColorModel.getDefaultTransferType(var1));
      this.transparent_index = -1;
      this.colorData = null;
      this.lookupcache = new int[40];
      if (var1 >= 1 && var1 <= 16) {
         if (var2 < 1) {
            throw new IllegalArgumentException("Map size (" + var2 + ") must be >= 1");
         } else {
            this.map_size = var2;
            this.rgb = new int[this.calcRealMapSize(var1, var2)];
            int var7 = var4;
            int var8 = 255;
            boolean var9 = true;
            byte var10 = 1;

            for(int var11 = 0; var11 < var2; ++var11) {
               int var12 = var3[var7++] & 255;
               int var13 = var3[var7++] & 255;
               int var14 = var3[var7++] & 255;
               var9 = var9 && var12 == var13 && var13 == var14;
               if (var5) {
                  var8 = var3[var7++] & 255;
                  if (var8 != 255) {
                     if (var8 == 0) {
                        if (var10 == 1) {
                           var10 = 2;
                        }

                        if (this.transparent_index < 0) {
                           this.transparent_index = var11;
                        }
                     } else {
                        var10 = 3;
                     }

                     var9 = false;
                  }
               }

               this.rgb[var11] = var8 << 24 | var12 << 16 | var13 << 8 | var14;
            }

            this.allgrayopaque = var9;
            this.setTransparency(var10);
            this.setTransparentPixel(var6);
            this.calculatePixelMask();
         }
      } else {
         throw new IllegalArgumentException("Number of bits must be between 1 and 16.");
      }
   }

   public IndexColorModel(int var1, int var2, int[] var3, int var4, boolean var5, int var6, int var7) {
      super(var1, opaqueBits, ColorSpace.getInstance(1000), false, false, 1, var7);
      this.transparent_index = -1;
      this.colorData = null;
      this.lookupcache = new int[40];
      if (var1 >= 1 && var1 <= 16) {
         if (var2 < 1) {
            throw new IllegalArgumentException("Map size (" + var2 + ") must be >= 1");
         } else if (var7 != 0 && var7 != 1) {
            throw new IllegalArgumentException("transferType must be eitherDataBuffer.TYPE_BYTE or DataBuffer.TYPE_USHORT");
         } else {
            this.setRGBs(var2, var3, var4, var5);
            this.setTransparentPixel(var6);
            this.calculatePixelMask();
         }
      } else {
         throw new IllegalArgumentException("Number of bits must be between 1 and 16.");
      }
   }

   public IndexColorModel(int var1, int var2, int[] var3, int var4, int var5, BigInteger var6) {
      super(var1, alphaBits, ColorSpace.getInstance(1000), true, false, 3, var5);
      this.transparent_index = -1;
      this.colorData = null;
      this.lookupcache = new int[40];
      if (var1 >= 1 && var1 <= 16) {
         if (var2 < 1) {
            throw new IllegalArgumentException("Map size (" + var2 + ") must be >= 1");
         } else if (var5 != 0 && var5 != 1) {
            throw new IllegalArgumentException("transferType must be eitherDataBuffer.TYPE_BYTE or DataBuffer.TYPE_USHORT");
         } else {
            if (var6 != null) {
               for(int var7 = 0; var7 < var2; ++var7) {
                  if (!var6.testBit(var7)) {
                     this.validBits = var6;
                     break;
                  }
               }
            }

            this.setRGBs(var2, var3, var4, true);
            this.calculatePixelMask();
         }
      } else {
         throw new IllegalArgumentException("Number of bits must be between 1 and 16.");
      }
   }

   private void setRGBs(int var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5) {
      if (var1 < 1) {
         throw new IllegalArgumentException("Map size (" + var1 + ") must be >= 1");
      } else {
         this.map_size = var1;
         this.rgb = new int[this.calcRealMapSize(this.pixel_bits, var1)];
         int var6 = 255;
         byte var7 = 1;
         boolean var8 = true;

         for(int var9 = 0; var9 < var1; ++var9) {
            int var10 = var2[var9] & 255;
            int var11 = var3[var9] & 255;
            int var12 = var4[var9] & 255;
            var8 = var8 && var10 == var11 && var11 == var12;
            if (var5 != null) {
               var6 = var5[var9] & 255;
               if (var6 != 255) {
                  if (var6 == 0) {
                     if (var7 == 1) {
                        var7 = 2;
                     }

                     if (this.transparent_index < 0) {
                        this.transparent_index = var9;
                     }
                  } else {
                     var7 = 3;
                  }

                  var8 = false;
               }
            }

            this.rgb[var9] = var6 << 24 | var10 << 16 | var11 << 8 | var12;
         }

         this.allgrayopaque = var8;
         this.setTransparency(var7);
      }
   }

   private void setRGBs(int var1, int[] var2, int var3, boolean var4) {
      this.map_size = var1;
      this.rgb = new int[this.calcRealMapSize(this.pixel_bits, var1)];
      int var5 = var3;
      byte var6 = 1;
      boolean var7 = true;
      BigInteger var8 = this.validBits;

      for(int var9 = 0; var9 < var1; ++var5) {
         if (var8 == null || var8.testBit(var9)) {
            int var10 = var2[var5];
            int var11 = var10 >> 16 & 255;
            int var12 = var10 >> 8 & 255;
            int var13 = var10 & 255;
            var7 = var7 && var11 == var12 && var12 == var13;
            if (var4) {
               int var14 = var10 >>> 24;
               if (var14 != 255) {
                  if (var14 == 0) {
                     if (var6 == 1) {
                        var6 = 2;
                     }

                     if (this.transparent_index < 0) {
                        this.transparent_index = var9;
                     }
                  } else {
                     var6 = 3;
                  }

                  var7 = false;
               }
            } else {
               var10 |= -16777216;
            }

            this.rgb[var9] = var10;
         }

         ++var9;
      }

      this.allgrayopaque = var7;
      this.setTransparency(var6);
   }

   private int calcRealMapSize(int var1, int var2) {
      int var3 = Math.max(1 << var1, var2);
      return Math.max(var3, 256);
   }

   private BigInteger getAllValid() {
      int var1 = (this.map_size + 7) / 8;
      byte[] var2 = new byte[var1];
      Arrays.fill(var2, (byte)-1);
      var2[0] = (byte)(255 >>> var1 * 8 - this.map_size);
      return new BigInteger(1, var2);
   }

   public int getTransparency() {
      return this.transparency;
   }

   public int[] getComponentSize() {
      if (this.nBits == null) {
         if (this.supportsAlpha) {
            this.nBits = new int[4];
            this.nBits[3] = 8;
         } else {
            this.nBits = new int[3];
         }

         this.nBits[0] = this.nBits[1] = this.nBits[2] = 8;
      }

      return (int[])this.nBits.clone();
   }

   public final int getMapSize() {
      return this.map_size;
   }

   public final int getTransparentPixel() {
      return this.transparent_index;
   }

   public final void getReds(byte[] var1) {
      for(int var2 = 0; var2 < this.map_size; ++var2) {
         var1[var2] = (byte)(this.rgb[var2] >> 16);
      }

   }

   public final void getGreens(byte[] var1) {
      for(int var2 = 0; var2 < this.map_size; ++var2) {
         var1[var2] = (byte)(this.rgb[var2] >> 8);
      }

   }

   public final void getBlues(byte[] var1) {
      for(int var2 = 0; var2 < this.map_size; ++var2) {
         var1[var2] = (byte)this.rgb[var2];
      }

   }

   public final void getAlphas(byte[] var1) {
      for(int var2 = 0; var2 < this.map_size; ++var2) {
         var1[var2] = (byte)(this.rgb[var2] >> 24);
      }

   }

   public final void getRGBs(int[] var1) {
      System.arraycopy(this.rgb, 0, var1, 0, this.map_size);
   }

   private void setTransparentPixel(int var1) {
      if (var1 >= 0 && var1 < this.map_size) {
         int[] var10000 = this.rgb;
         var10000[var1] &= 16777215;
         this.transparent_index = var1;
         this.allgrayopaque = false;
         if (this.transparency == 1) {
            this.setTransparency(2);
         }
      }

   }

   private void setTransparency(int var1) {
      if (this.transparency != var1) {
         this.transparency = var1;
         if (var1 == 1) {
            this.supportsAlpha = false;
            this.numComponents = 3;
            this.nBits = opaqueBits;
         } else {
            this.supportsAlpha = true;
            this.numComponents = 4;
            this.nBits = alphaBits;
         }
      }

   }

   private final void calculatePixelMask() {
      int var1 = this.pixel_bits;
      if (var1 == 3) {
         var1 = 4;
      } else if (var1 > 4 && var1 < 8) {
         var1 = 8;
      }

      this.pixel_mask = (1 << var1) - 1;
   }

   public final int getRed(int var1) {
      return this.rgb[var1 & this.pixel_mask] >> 16 & 255;
   }

   public final int getGreen(int var1) {
      return this.rgb[var1 & this.pixel_mask] >> 8 & 255;
   }

   public final int getBlue(int var1) {
      return this.rgb[var1 & this.pixel_mask] & 255;
   }

   public final int getAlpha(int var1) {
      return this.rgb[var1 & this.pixel_mask] >> 24 & 255;
   }

   public final int getRGB(int var1) {
      return this.rgb[var1 & this.pixel_mask];
   }

   public synchronized Object getDataElements(int var1, Object var2) {
      int var3 = var1 >> 16 & 255;
      int var4 = var1 >> 8 & 255;
      int var5 = var1 & 255;
      int var6 = var1 >>> 24;
      int var7 = 0;

      int var8;
      for(var8 = 38; var8 >= 0 && (var7 = this.lookupcache[var8]) != 0; var8 -= 2) {
         if (var1 == this.lookupcache[var8 + 1]) {
            return this.installpixel(var2, ~var7);
         }
      }

      int var10;
      int var11;
      if (this.allgrayopaque) {
         var8 = 256;
         var10 = (var3 * 77 + var4 * 150 + var5 * 29 + 128) / 256;

         for(var11 = 0; var11 < this.map_size; ++var11) {
            if (this.rgb[var11] != 0) {
               int var9 = (this.rgb[var11] & 255) - var10;
               if (var9 < 0) {
                  var9 = -var9;
               }

               if (var9 < var8) {
                  var7 = var11;
                  if (var9 == 0) {
                     break;
                  }

                  var8 = var9;
               }
            }
         }
      } else {
         int var12;
         int var13;
         int[] var14;
         if (this.transparency == 1) {
            var8 = Integer.MAX_VALUE;
            var14 = this.rgb;

            for(var11 = 0; var11 < this.map_size; ++var11) {
               var10 = var14[var11];
               if (var10 == var1 && var10 != 0) {
                  var7 = var11;
                  var8 = 0;
                  break;
               }
            }

            if (var8 != 0) {
               for(var11 = 0; var11 < this.map_size; ++var11) {
                  var10 = var14[var11];
                  if (var10 != 0) {
                     var12 = (var10 >> 16 & 255) - var3;
                     var13 = var12 * var12;
                     if (var13 < var8) {
                        var12 = (var10 >> 8 & 255) - var4;
                        var13 += var12 * var12;
                        if (var13 < var8) {
                           var12 = (var10 & 255) - var5;
                           var13 += var12 * var12;
                           if (var13 < var8) {
                              var7 = var11;
                              var8 = var13;
                           }
                        }
                     }
                  }
               }
            }
         } else if (var6 == 0 && this.transparent_index >= 0) {
            var7 = this.transparent_index;
         } else {
            var8 = Integer.MAX_VALUE;
            var14 = this.rgb;

            for(var10 = 0; var10 < this.map_size; ++var10) {
               var11 = var14[var10];
               if (var11 == var1) {
                  if (this.validBits == null || this.validBits.testBit(var10)) {
                     var7 = var10;
                     break;
                  }
               } else {
                  var12 = (var11 >> 16 & 255) - var3;
                  var13 = var12 * var12;
                  if (var13 < var8) {
                     var12 = (var11 >> 8 & 255) - var4;
                     var13 += var12 * var12;
                     if (var13 < var8) {
                        var12 = (var11 & 255) - var5;
                        var13 += var12 * var12;
                        if (var13 < var8) {
                           var12 = (var11 >>> 24) - var6;
                           var13 += var12 * var12;
                           if (var13 < var8 && (this.validBits == null || this.validBits.testBit(var10))) {
                              var7 = var10;
                              var8 = var13;
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      System.arraycopy(this.lookupcache, 2, this.lookupcache, 0, 38);
      this.lookupcache[39] = var1;
      this.lookupcache[38] = ~var7;
      return this.installpixel(var2, var7);
   }

   private Object installpixel(Object var1, int var2) {
      switch(this.transferType) {
      case 0:
         byte[] var4;
         if (var1 == null) {
            var1 = var4 = new byte[1];
         } else {
            var4 = (byte[])((byte[])var1);
         }

         var4[0] = (byte)var2;
         break;
      case 1:
         short[] var5;
         if (var1 == null) {
            var1 = var5 = new short[1];
         } else {
            var5 = (short[])((short[])var1);
         }

         var5[0] = (short)var2;
         break;
      case 2:
      default:
         throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
      case 3:
         int[] var3;
         if (var1 == null) {
            var1 = var3 = new int[1];
         } else {
            var3 = (int[])((int[])var1);
         }

         var3[0] = var2;
      }

      return var1;
   }

   public int[] getComponents(int var1, int[] var2, int var3) {
      if (var2 == null) {
         var2 = new int[var3 + this.numComponents];
      }

      var2[var3 + 0] = this.getRed(var1);
      var2[var3 + 1] = this.getGreen(var1);
      var2[var3 + 2] = this.getBlue(var1);
      if (this.supportsAlpha && var2.length - var3 > 3) {
         var2[var3 + 3] = this.getAlpha(var1);
      }

      return var2;
   }

   public int[] getComponents(Object var1, int[] var2, int var3) {
      int var4;
      switch(this.transferType) {
      case 0:
         byte[] var5 = (byte[])((byte[])var1);
         var4 = var5[0] & 255;
         break;
      case 1:
         short[] var6 = (short[])((short[])var1);
         var4 = var6[0] & '\uffff';
         break;
      case 2:
      default:
         throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
      case 3:
         int[] var7 = (int[])((int[])var1);
         var4 = var7[0];
      }

      return this.getComponents(var4, var2, var3);
   }

   public int getDataElement(int[] var1, int var2) {
      int var3 = var1[var2 + 0] << 16 | var1[var2 + 1] << 8 | var1[var2 + 2];
      if (this.supportsAlpha) {
         var3 |= var1[var2 + 3] << 24;
      } else {
         var3 |= -16777216;
      }

      Object var4 = this.getDataElements(var3, (Object)null);
      int var5;
      switch(this.transferType) {
      case 0:
         byte[] var6 = (byte[])((byte[])var4);
         var5 = var6[0] & 255;
         break;
      case 1:
         short[] var7 = (short[])((short[])var4);
         var5 = var7[0];
         break;
      case 2:
      default:
         throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
      case 3:
         int[] var8 = (int[])((int[])var4);
         var5 = var8[0];
      }

      return var5;
   }

   public Object getDataElements(int[] var1, int var2, Object var3) {
      int var4 = var1[var2 + 0] << 16 | var1[var2 + 1] << 8 | var1[var2 + 2];
      if (this.supportsAlpha) {
         var4 |= var1[var2 + 3] << 24;
      } else {
         var4 &= -16777216;
      }

      return this.getDataElements(var4, var3);
   }

   public WritableRaster createCompatibleWritableRaster(int var1, int var2) {
      WritableRaster var3;
      if (this.pixel_bits != 1 && this.pixel_bits != 2 && this.pixel_bits != 4) {
         if (this.pixel_bits <= 8) {
            var3 = Raster.createInterleavedRaster(0, var1, var2, 1, (Point)null);
         } else {
            if (this.pixel_bits > 16) {
               throw new UnsupportedOperationException("This method is not supported  for pixel bits > 16.");
            }

            var3 = Raster.createInterleavedRaster(1, var1, var2, 1, (Point)null);
         }
      } else {
         var3 = Raster.createPackedRaster(0, var1, var2, 1, this.pixel_bits, (Point)null);
      }

      return var3;
   }

   public boolean isCompatibleRaster(Raster var1) {
      int var2 = var1.getSampleModel().getSampleSize(0);
      return var1.getTransferType() == this.transferType && var1.getNumBands() == 1 && 1 << var2 >= this.map_size;
   }

   public SampleModel createCompatibleSampleModel(int var1, int var2) {
      int[] var3 = new int[]{0};
      return (SampleModel)(this.pixel_bits != 1 && this.pixel_bits != 2 && this.pixel_bits != 4 ? new ComponentSampleModel(this.transferType, var1, var2, 1, var1, var3) : new MultiPixelPackedSampleModel(this.transferType, var1, var2, this.pixel_bits));
   }

   public boolean isCompatibleSampleModel(SampleModel var1) {
      if (!(var1 instanceof ComponentSampleModel) && !(var1 instanceof MultiPixelPackedSampleModel)) {
         return false;
      } else if (var1.getTransferType() != this.transferType) {
         return false;
      } else {
         return var1.getNumBands() == 1;
      }
   }

   public BufferedImage convertToIntDiscrete(Raster var1, boolean var2) {
      if (!this.isCompatibleRaster(var1)) {
         throw new IllegalArgumentException("This raster is not compatiblewith this IndexColorModel.");
      } else {
         Object var3;
         if (!var2 && this.transparency != 3) {
            if (this.transparency == 2) {
               var3 = new DirectColorModel(25, 16711680, 65280, 255, 16777216);
            } else {
               var3 = new DirectColorModel(24, 16711680, 65280, 255);
            }
         } else {
            var3 = ColorModel.getRGBdefault();
         }

         int var4 = var1.getWidth();
         int var5 = var1.getHeight();
         WritableRaster var6 = ((ColorModel)var3).createCompatibleWritableRaster(var4, var5);
         Object var7 = null;
         Object var8 = null;
         int var9 = var1.getMinX();
         int var10 = var1.getMinY();

         for(int var11 = 0; var11 < var5; ++var10) {
            var7 = var1.getDataElements(var9, var10, var4, 1, var7);
            int[] var13;
            if (var7 instanceof int[]) {
               var13 = (int[])((int[])var7);
            } else {
               var13 = DataBuffer.toIntArray(var7);
            }

            for(int var12 = 0; var12 < var4; ++var12) {
               var13[var12] = this.rgb[var13[var12] & this.pixel_mask];
            }

            var6.setDataElements(0, var11, var4, 1, var13);
            ++var11;
         }

         return new BufferedImage((ColorModel)var3, var6, false, (Hashtable)null);
      }
   }

   public boolean isValid(int var1) {
      return var1 >= 0 && var1 < this.map_size && (this.validBits == null || this.validBits.testBit(var1));
   }

   public boolean isValid() {
      return this.validBits == null;
   }

   public BigInteger getValidPixels() {
      return this.validBits == null ? this.getAllValid() : this.validBits;
   }

   public void finalize() {
   }

   public String toString() {
      return new String("IndexColorModel: #pixelBits = " + this.pixel_bits + " numComponents = " + this.numComponents + " color space = " + this.colorSpace + " transparency = " + this.transparency + " transIndex   = " + this.transparent_index + " has alpha = " + this.supportsAlpha + " isAlphaPre = " + this.isAlphaPremultiplied);
   }

   static {
      ColorModel.loadLibraries();
      initIDs();
   }
}
