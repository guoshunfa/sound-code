package sun.awt.image;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RasterFormatException;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

public class BytePackedRaster extends SunWritableRaster {
   int dataBitOffset;
   int scanlineStride;
   int pixelBitStride;
   int bitMask;
   byte[] data;
   int shiftOffset;
   int type;
   private int maxX;
   private int maxY;

   private static native void initIDs();

   public BytePackedRaster(SampleModel var1, Point var2) {
      this(var1, var1.createDataBuffer(), new Rectangle(var2.x, var2.y, var1.getWidth(), var1.getHeight()), var2, (BytePackedRaster)null);
   }

   public BytePackedRaster(SampleModel var1, DataBuffer var2, Point var3) {
      this(var1, var2, new Rectangle(var3.x, var3.y, var1.getWidth(), var1.getHeight()), var3, (BytePackedRaster)null);
   }

   public BytePackedRaster(SampleModel var1, DataBuffer var2, Rectangle var3, Point var4, BytePackedRaster var5) {
      super(var1, var2, var3, var4, var5);
      this.maxX = this.minX + this.width;
      this.maxY = this.minY + this.height;
      if (!(var2 instanceof DataBufferByte)) {
         throw new RasterFormatException("BytePackedRasters must havebyte DataBuffers");
      } else {
         DataBufferByte var6 = (DataBufferByte)var2;
         this.data = stealData(var6, 0);
         if (var6.getNumBanks() != 1) {
            throw new RasterFormatException("DataBuffer for BytePackedRasters must only have 1 bank.");
         } else {
            int var7 = var6.getOffset();
            if (var1 instanceof MultiPixelPackedSampleModel) {
               MultiPixelPackedSampleModel var8 = (MultiPixelPackedSampleModel)var1;
               this.type = 11;
               this.pixelBitStride = var8.getPixelBitStride();
               if (this.pixelBitStride != 1 && this.pixelBitStride != 2 && this.pixelBitStride != 4) {
                  throw new RasterFormatException("BytePackedRasters must have a bit depth of 1, 2, or 4");
               } else {
                  this.scanlineStride = var8.getScanlineStride();
                  this.dataBitOffset = var8.getDataBitOffset() + var7 * 8;
                  int var9 = var3.x - var4.x;
                  int var10 = var3.y - var4.y;
                  this.dataBitOffset += var9 * this.pixelBitStride + var10 * this.scanlineStride * 8;
                  this.bitMask = (1 << this.pixelBitStride) - 1;
                  this.shiftOffset = 8 - this.pixelBitStride;
                  this.verify(false);
               }
            } else {
               throw new RasterFormatException("BytePackedRasters must haveMultiPixelPackedSampleModel");
            }
         }
      }
   }

   public int getDataBitOffset() {
      return this.dataBitOffset;
   }

   public int getScanlineStride() {
      return this.scanlineStride;
   }

   public int getPixelBitStride() {
      return this.pixelBitStride;
   }

   public byte[] getDataStorage() {
      return this.data;
   }

   public Object getDataElements(int var1, int var2, Object var3) {
      if (var1 >= this.minX && var2 >= this.minY && var1 < this.maxX && var2 < this.maxY) {
         byte[] var4;
         if (var3 == null) {
            var4 = new byte[this.numDataElements];
         } else {
            var4 = (byte[])((byte[])var3);
         }

         int var5 = this.dataBitOffset + (var1 - this.minX) * this.pixelBitStride;
         int var6 = this.data[(var2 - this.minY) * this.scanlineStride + (var5 >> 3)] & 255;
         int var7 = this.shiftOffset - (var5 & 7);
         var4[0] = (byte)(var6 >> var7 & this.bitMask);
         return var4;
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public Object getDataElements(int var1, int var2, int var3, int var4, Object var5) {
      return this.getByteData(var1, var2, var3, var4, (byte[])((byte[])var5));
   }

   public Object getPixelData(int var1, int var2, int var3, int var4, Object var5) {
      if (var1 >= this.minX && var2 >= this.minY && var1 + var3 <= this.maxX && var2 + var4 <= this.maxY) {
         byte[] var6;
         if (var5 == null) {
            var6 = new byte[this.numDataElements * var3 * var4];
         } else {
            var6 = (byte[])((byte[])var5);
         }

         int var7 = this.pixelBitStride;
         int var8 = this.dataBitOffset + (var1 - this.minX) * var7;
         int var9 = (var2 - this.minY) * this.scanlineStride;
         int var10 = 0;
         byte[] var11 = this.data;

         for(int var12 = 0; var12 < var4; ++var12) {
            int var13 = var8;

            for(int var14 = 0; var14 < var3; ++var14) {
               int var15 = this.shiftOffset - (var13 & 7);
               var6[var10++] = (byte)(this.bitMask & var11[var9 + (var13 >> 3)] >> var15);
               var13 += var7;
            }

            var9 += this.scanlineStride;
         }

         return var6;
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public byte[] getByteData(int var1, int var2, int var3, int var4, int var5, byte[] var6) {
      return this.getByteData(var1, var2, var3, var4, var6);
   }

   public byte[] getByteData(int var1, int var2, int var3, int var4, byte[] var5) {
      if (var1 >= this.minX && var2 >= this.minY && var1 + var3 <= this.maxX && var2 + var4 <= this.maxY) {
         if (var5 == null) {
            var5 = new byte[var3 * var4];
         }

         int var6 = this.pixelBitStride;
         int var7 = this.dataBitOffset + (var1 - this.minX) * var6;
         int var8 = (var2 - this.minY) * this.scanlineStride;
         int var9 = 0;
         byte[] var10 = this.data;

         for(int var11 = 0; var11 < var4; ++var11) {
            int var12 = var7;

            int var14;
            int var15;
            for(var14 = 0; var14 < var3 && (var12 & 7) != 0; ++var14) {
               var15 = this.shiftOffset - (var12 & 7);
               var5[var9++] = (byte)(this.bitMask & var10[var8 + (var12 >> 3)] >> var15);
               var12 += var6;
            }

            var15 = var8 + (var12 >> 3);
            byte var13;
            label49:
            switch(var6) {
            case 1:
               while(true) {
                  if (var14 >= var3 - 7) {
                     break label49;
                  }

                  var13 = var10[var15++];
                  var5[var9++] = (byte)(var13 >> 7 & 1);
                  var5[var9++] = (byte)(var13 >> 6 & 1);
                  var5[var9++] = (byte)(var13 >> 5 & 1);
                  var5[var9++] = (byte)(var13 >> 4 & 1);
                  var5[var9++] = (byte)(var13 >> 3 & 1);
                  var5[var9++] = (byte)(var13 >> 2 & 1);
                  var5[var9++] = (byte)(var13 >> 1 & 1);
                  var5[var9++] = (byte)(var13 & 1);
                  var12 += 8;
                  var14 += 8;
               }
            case 2:
               while(var14 < var3 - 7) {
                  var13 = var10[var15++];
                  var5[var9++] = (byte)(var13 >> 6 & 3);
                  var5[var9++] = (byte)(var13 >> 4 & 3);
                  var5[var9++] = (byte)(var13 >> 2 & 3);
                  var5[var9++] = (byte)(var13 & 3);
                  var13 = var10[var15++];
                  var5[var9++] = (byte)(var13 >> 6 & 3);
                  var5[var9++] = (byte)(var13 >> 4 & 3);
                  var5[var9++] = (byte)(var13 >> 2 & 3);
                  var5[var9++] = (byte)(var13 & 3);
                  var12 += 16;
                  var14 += 8;
               }
            case 3:
            default:
               break;
            case 4:
               while(var14 < var3 - 7) {
                  var13 = var10[var15++];
                  var5[var9++] = (byte)(var13 >> 4 & 15);
                  var5[var9++] = (byte)(var13 & 15);
                  var13 = var10[var15++];
                  var5[var9++] = (byte)(var13 >> 4 & 15);
                  var5[var9++] = (byte)(var13 & 15);
                  var13 = var10[var15++];
                  var5[var9++] = (byte)(var13 >> 4 & 15);
                  var5[var9++] = (byte)(var13 & 15);
                  var13 = var10[var15++];
                  var5[var9++] = (byte)(var13 >> 4 & 15);
                  var5[var9++] = (byte)(var13 & 15);
                  var12 += 32;
                  var14 += 8;
               }
            }

            while(var14 < var3) {
               int var16 = this.shiftOffset - (var12 & 7);
               var5[var9++] = (byte)(this.bitMask & var10[var8 + (var12 >> 3)] >> var16);
               var12 += var6;
               ++var14;
            }

            var8 += this.scanlineStride;
         }

         return var5;
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setDataElements(int var1, int var2, Object var3) {
      if (var1 >= this.minX && var2 >= this.minY && var1 < this.maxX && var2 < this.maxY) {
         byte[] var4 = (byte[])((byte[])var3);
         int var5 = this.dataBitOffset + (var1 - this.minX) * this.pixelBitStride;
         int var6 = (var2 - this.minY) * this.scanlineStride + (var5 >> 3);
         int var7 = this.shiftOffset - (var5 & 7);
         byte var8 = this.data[var6];
         var8 = (byte)(var8 & ~(this.bitMask << var7));
         var8 = (byte)(var8 | (var4[0] & this.bitMask) << var7);
         this.data[var6] = var8;
         this.markDirty();
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setDataElements(int var1, int var2, Raster var3) {
      if (var3 instanceof BytePackedRaster && ((BytePackedRaster)var3).pixelBitStride == this.pixelBitStride) {
         int var4 = var3.getMinX();
         int var5 = var3.getMinY();
         int var6 = var4 + var1;
         int var7 = var5 + var2;
         int var8 = var3.getWidth();
         int var9 = var3.getHeight();
         if (var6 >= this.minX && var7 >= this.minY && var6 + var8 <= this.maxX && var7 + var9 <= this.maxY) {
            this.setDataElements(var6, var7, var4, var5, var8, var9, (BytePackedRaster)var3);
         } else {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
         }
      } else {
         super.setDataElements(var1, var2, var3);
      }
   }

   private void setDataElements(int var1, int var2, int var3, int var4, int var5, int var6, BytePackedRaster var7) {
      if (var5 > 0 && var6 > 0) {
         byte[] var8 = var7.data;
         byte[] var9 = this.data;
         int var10 = var7.scanlineStride;
         int var11 = this.scanlineStride;
         int var12 = var7.dataBitOffset + 8 * (var4 - var7.minY) * var10 + (var3 - var7.minX) * var7.pixelBitStride;
         int var13 = this.dataBitOffset + 8 * (var2 - this.minY) * var11 + (var1 - this.minX) * this.pixelBitStride;
         int var14 = var5 * this.pixelBitStride;
         int var15;
         int var16;
         int var17;
         int var18;
         int var19;
         int var20;
         int var29;
         if ((var12 & 7) == (var13 & 7)) {
            var15 = var13 & 7;
            if (var15 != 0) {
               var16 = 8 - var15;
               var17 = var12 >> 3;
               var18 = var13 >> 3;
               var19 = 255 >> var15;
               if (var14 < var16) {
                  var19 &= 255 << var16 - var14;
                  var16 = var14;
               }

               for(var20 = 0; var20 < var6; ++var20) {
                  byte var21 = var9[var18];
                  var29 = var21 & ~var19;
                  var29 |= var8[var17] & var19;
                  var9[var18] = (byte)var29;
                  var17 += var10;
                  var18 += var11;
               }

               var12 += var16;
               var13 += var16;
               var14 -= var16;
            }

            if (var14 >= 8) {
               var16 = var12 >> 3;
               var17 = var13 >> 3;
               var18 = var14 >> 3;
               if (var18 == var10 && var10 == var11) {
                  System.arraycopy(var8, var16, var9, var17, var10 * var6);
               } else {
                  for(var19 = 0; var19 < var6; ++var19) {
                     System.arraycopy(var8, var16, var9, var17, var18);
                     var16 += var10;
                     var17 += var11;
                  }
               }

               var19 = var18 * 8;
               var12 += var19;
               var13 += var19;
               var14 -= var19;
            }

            if (var14 > 0) {
               var16 = var12 >> 3;
               var17 = var13 >> 3;
               var18 = '\uff00' >> var14 & 255;

               for(var19 = 0; var19 < var6; ++var19) {
                  byte var28 = var9[var17];
                  var20 = var28 & ~var18;
                  var20 |= var8[var16] & var18;
                  var9[var17] = (byte)var20;
                  var16 += var10;
                  var17 += var11;
               }
            }
         } else {
            var15 = var13 & 7;
            int var22;
            int var23;
            byte var24;
            byte var25;
            byte var26;
            if (var15 != 0 || var14 < 8) {
               var16 = 8 - var15;
               var17 = var12 >> 3;
               var18 = var13 >> 3;
               var19 = var12 & 7;
               var20 = 8 - var19;
               var29 = 255 >> var15;
               if (var14 < var16) {
                  var29 &= 255 << var16 - var14;
                  var16 = var14;
               }

               var22 = var8.length - 1;

               for(var23 = 0; var23 < var6; ++var23) {
                  var24 = var8[var17];
                  var25 = 0;
                  if (var17 < var22) {
                     var25 = var8[var17 + 1];
                  }

                  var26 = var9[var18];
                  int var32 = var26 & ~var29;
                  var32 |= (var24 << var19 | (var25 & 255) >> var20) >> var15 & var29;
                  var9[var18] = (byte)var32;
                  var17 += var10;
                  var18 += var11;
               }

               var12 += var16;
               var13 += var16;
               var14 -= var16;
            }

            int var31;
            if (var14 >= 8) {
               var16 = var12 >> 3;
               var17 = var13 >> 3;
               var18 = var14 >> 3;
               var19 = var12 & 7;
               var20 = 8 - var19;
               var29 = 0;

               while(true) {
                  if (var29 >= var6) {
                     var29 = var18 * 8;
                     var12 += var29;
                     var13 += var29;
                     var14 -= var29;
                     break;
                  }

                  var22 = var16 + var29 * var10;
                  var23 = var17 + var29 * var11;
                  var24 = var8[var22];

                  for(var31 = 0; var31 < var18; ++var31) {
                     var26 = var8[var22 + 1];
                     int var27 = var24 << var19 | (var26 & 255) >> var20;
                     var9[var23] = (byte)var27;
                     var24 = var26;
                     ++var22;
                     ++var23;
                  }

                  ++var29;
               }
            }

            if (var14 > 0) {
               var16 = var12 >> 3;
               var17 = var13 >> 3;
               var18 = '\uff00' >> var14 & 255;
               var19 = var12 & 7;
               var20 = 8 - var19;
               var29 = var8.length - 1;

               for(var22 = 0; var22 < var6; ++var22) {
                  byte var30 = var8[var16];
                  var24 = 0;
                  if (var16 < var29) {
                     var24 = var8[var16 + 1];
                  }

                  var25 = var9[var17];
                  var31 = var25 & ~var18;
                  var31 |= (var30 << var19 | (var24 & 255) >> var20) & var18;
                  var9[var17] = (byte)var31;
                  var16 += var10;
                  var17 += var11;
               }
            }
         }

         this.markDirty();
      }
   }

   public void setRect(int var1, int var2, Raster var3) {
      if (var3 instanceof BytePackedRaster && ((BytePackedRaster)var3).pixelBitStride == this.pixelBitStride) {
         int var4 = var3.getWidth();
         int var5 = var3.getHeight();
         int var6 = var3.getMinX();
         int var7 = var3.getMinY();
         int var8 = var1 + var6;
         int var9 = var2 + var7;
         int var10;
         if (var8 < this.minX) {
            var10 = this.minX - var8;
            var4 -= var10;
            var6 += var10;
            var8 = this.minX;
         }

         if (var9 < this.minY) {
            var10 = this.minY - var9;
            var5 -= var10;
            var7 += var10;
            var9 = this.minY;
         }

         if (var8 + var4 > this.maxX) {
            var4 = this.maxX - var8;
         }

         if (var9 + var5 > this.maxY) {
            var5 = this.maxY - var9;
         }

         this.setDataElements(var8, var9, var6, var7, var4, var5, (BytePackedRaster)var3);
      } else {
         super.setRect(var1, var2, var3);
      }
   }

   public void setDataElements(int var1, int var2, int var3, int var4, Object var5) {
      this.putByteData(var1, var2, var3, var4, (byte[])((byte[])var5));
   }

   public void putByteData(int var1, int var2, int var3, int var4, int var5, byte[] var6) {
      this.putByteData(var1, var2, var3, var4, var6);
   }

   public void putByteData(int var1, int var2, int var3, int var4, byte[] var5) {
      if (var1 >= this.minX && var2 >= this.minY && var1 + var3 <= this.maxX && var2 + var4 <= this.maxY) {
         if (var3 != 0 && var4 != 0) {
            int var6 = this.pixelBitStride;
            int var7 = this.dataBitOffset + (var1 - this.minX) * var6;
            int var8 = (var2 - this.minY) * this.scanlineStride;
            int var9 = 0;
            byte[] var10 = this.data;

            for(int var11 = 0; var11 < var4; ++var11) {
               int var12 = var7;

               byte var13;
               int var14;
               int var15;
               int var17;
               for(var14 = 0; var14 < var3 && (var12 & 7) != 0; ++var14) {
                  var15 = this.shiftOffset - (var12 & 7);
                  var13 = var10[var8 + (var12 >> 3)];
                  var17 = var13 & ~(this.bitMask << var15);
                  var17 |= (var5[var9++] & this.bitMask) << var15;
                  var10[var8 + (var12 >> 3)] = (byte)var17;
                  var12 += var6;
               }

               var15 = var8 + (var12 >> 3);
               label48:
               switch(var6) {
               case 1:
                  while(true) {
                     if (var14 >= var3 - 7) {
                        break label48;
                     }

                     var17 = (var5[var9++] & 1) << 7;
                     var17 |= (var5[var9++] & 1) << 6;
                     var17 |= (var5[var9++] & 1) << 5;
                     var17 |= (var5[var9++] & 1) << 4;
                     var17 |= (var5[var9++] & 1) << 3;
                     var17 |= (var5[var9++] & 1) << 2;
                     var17 |= (var5[var9++] & 1) << 1;
                     var17 |= var5[var9++] & 1;
                     var10[var15++] = (byte)var17;
                     var12 += 8;
                     var14 += 8;
                  }
               case 2:
                  while(var14 < var3 - 7) {
                     var17 = (var5[var9++] & 3) << 6;
                     var17 |= (var5[var9++] & 3) << 4;
                     var17 |= (var5[var9++] & 3) << 2;
                     var17 |= var5[var9++] & 3;
                     var10[var15++] = (byte)var17;
                     var17 = (var5[var9++] & 3) << 6;
                     var17 |= (var5[var9++] & 3) << 4;
                     var17 |= (var5[var9++] & 3) << 2;
                     var17 |= var5[var9++] & 3;
                     var10[var15++] = (byte)var17;
                     var12 += 16;
                     var14 += 8;
                  }
               case 3:
               default:
                  break;
               case 4:
                  while(var14 < var3 - 7) {
                     var17 = (var5[var9++] & 15) << 4;
                     var17 |= var5[var9++] & 15;
                     var10[var15++] = (byte)var17;
                     var17 = (var5[var9++] & 15) << 4;
                     var17 |= var5[var9++] & 15;
                     var10[var15++] = (byte)var17;
                     var17 = (var5[var9++] & 15) << 4;
                     var17 |= var5[var9++] & 15;
                     var10[var15++] = (byte)var17;
                     var17 = (var5[var9++] & 15) << 4;
                     var17 |= var5[var9++] & 15;
                     var10[var15++] = (byte)var17;
                     var12 += 32;
                     var14 += 8;
                  }
               }

               while(var14 < var3) {
                  int var16 = this.shiftOffset - (var12 & 7);
                  var13 = var10[var8 + (var12 >> 3)];
                  var17 = var13 & ~(this.bitMask << var16);
                  var17 |= (var5[var9++] & this.bitMask) << var16;
                  var10[var8 + (var12 >> 3)] = (byte)var17;
                  var12 += var6;
                  ++var14;
               }

               var8 += this.scanlineStride;
            }

            this.markDirty();
         }
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public int[] getPixels(int var1, int var2, int var3, int var4, int[] var5) {
      if (var1 >= this.minX && var2 >= this.minY && var1 + var3 <= this.maxX && var2 + var4 <= this.maxY) {
         if (var5 == null) {
            var5 = new int[var3 * var4];
         }

         int var6 = this.pixelBitStride;
         int var7 = this.dataBitOffset + (var1 - this.minX) * var6;
         int var8 = (var2 - this.minY) * this.scanlineStride;
         int var9 = 0;
         byte[] var10 = this.data;

         for(int var11 = 0; var11 < var4; ++var11) {
            int var12 = var7;

            int var14;
            int var15;
            for(var14 = 0; var14 < var3 && (var12 & 7) != 0; ++var14) {
               var15 = this.shiftOffset - (var12 & 7);
               var5[var9++] = this.bitMask & var10[var8 + (var12 >> 3)] >> var15;
               var12 += var6;
            }

            var15 = var8 + (var12 >> 3);
            byte var13;
            label49:
            switch(var6) {
            case 1:
               while(true) {
                  if (var14 >= var3 - 7) {
                     break label49;
                  }

                  var13 = var10[var15++];
                  var5[var9++] = var13 >> 7 & 1;
                  var5[var9++] = var13 >> 6 & 1;
                  var5[var9++] = var13 >> 5 & 1;
                  var5[var9++] = var13 >> 4 & 1;
                  var5[var9++] = var13 >> 3 & 1;
                  var5[var9++] = var13 >> 2 & 1;
                  var5[var9++] = var13 >> 1 & 1;
                  var5[var9++] = var13 & 1;
                  var12 += 8;
                  var14 += 8;
               }
            case 2:
               while(var14 < var3 - 7) {
                  var13 = var10[var15++];
                  var5[var9++] = var13 >> 6 & 3;
                  var5[var9++] = var13 >> 4 & 3;
                  var5[var9++] = var13 >> 2 & 3;
                  var5[var9++] = var13 & 3;
                  var13 = var10[var15++];
                  var5[var9++] = var13 >> 6 & 3;
                  var5[var9++] = var13 >> 4 & 3;
                  var5[var9++] = var13 >> 2 & 3;
                  var5[var9++] = var13 & 3;
                  var12 += 16;
                  var14 += 8;
               }
            case 3:
            default:
               break;
            case 4:
               while(var14 < var3 - 7) {
                  var13 = var10[var15++];
                  var5[var9++] = var13 >> 4 & 15;
                  var5[var9++] = var13 & 15;
                  var13 = var10[var15++];
                  var5[var9++] = var13 >> 4 & 15;
                  var5[var9++] = var13 & 15;
                  var13 = var10[var15++];
                  var5[var9++] = var13 >> 4 & 15;
                  var5[var9++] = var13 & 15;
                  var13 = var10[var15++];
                  var5[var9++] = var13 >> 4 & 15;
                  var5[var9++] = var13 & 15;
                  var12 += 32;
                  var14 += 8;
               }
            }

            while(var14 < var3) {
               int var16 = this.shiftOffset - (var12 & 7);
               var5[var9++] = this.bitMask & var10[var8 + (var12 >> 3)] >> var16;
               var12 += var6;
               ++var14;
            }

            var8 += this.scanlineStride;
         }

         return var5;
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public void setPixels(int var1, int var2, int var3, int var4, int[] var5) {
      if (var1 >= this.minX && var2 >= this.minY && var1 + var3 <= this.maxX && var2 + var4 <= this.maxY) {
         int var6 = this.pixelBitStride;
         int var7 = this.dataBitOffset + (var1 - this.minX) * var6;
         int var8 = (var2 - this.minY) * this.scanlineStride;
         int var9 = 0;
         byte[] var10 = this.data;

         for(int var11 = 0; var11 < var4; ++var11) {
            int var12 = var7;

            byte var13;
            int var14;
            int var15;
            int var17;
            for(var14 = 0; var14 < var3 && (var12 & 7) != 0; ++var14) {
               var15 = this.shiftOffset - (var12 & 7);
               var13 = var10[var8 + (var12 >> 3)];
               var17 = var13 & ~(this.bitMask << var15);
               var17 |= (var5[var9++] & this.bitMask) << var15;
               var10[var8 + (var12 >> 3)] = (byte)var17;
               var12 += var6;
            }

            var15 = var8 + (var12 >> 3);
            label45:
            switch(var6) {
            case 1:
               while(true) {
                  if (var14 >= var3 - 7) {
                     break label45;
                  }

                  var17 = (var5[var9++] & 1) << 7;
                  var17 |= (var5[var9++] & 1) << 6;
                  var17 |= (var5[var9++] & 1) << 5;
                  var17 |= (var5[var9++] & 1) << 4;
                  var17 |= (var5[var9++] & 1) << 3;
                  var17 |= (var5[var9++] & 1) << 2;
                  var17 |= (var5[var9++] & 1) << 1;
                  var17 |= var5[var9++] & 1;
                  var10[var15++] = (byte)var17;
                  var12 += 8;
                  var14 += 8;
               }
            case 2:
               while(var14 < var3 - 7) {
                  var17 = (var5[var9++] & 3) << 6;
                  var17 |= (var5[var9++] & 3) << 4;
                  var17 |= (var5[var9++] & 3) << 2;
                  var17 |= var5[var9++] & 3;
                  var10[var15++] = (byte)var17;
                  var17 = (var5[var9++] & 3) << 6;
                  var17 |= (var5[var9++] & 3) << 4;
                  var17 |= (var5[var9++] & 3) << 2;
                  var17 |= var5[var9++] & 3;
                  var10[var15++] = (byte)var17;
                  var12 += 16;
                  var14 += 8;
               }
            case 3:
            default:
               break;
            case 4:
               while(var14 < var3 - 7) {
                  var17 = (var5[var9++] & 15) << 4;
                  var17 |= var5[var9++] & 15;
                  var10[var15++] = (byte)var17;
                  var17 = (var5[var9++] & 15) << 4;
                  var17 |= var5[var9++] & 15;
                  var10[var15++] = (byte)var17;
                  var17 = (var5[var9++] & 15) << 4;
                  var17 |= var5[var9++] & 15;
                  var10[var15++] = (byte)var17;
                  var17 = (var5[var9++] & 15) << 4;
                  var17 |= var5[var9++] & 15;
                  var10[var15++] = (byte)var17;
                  var12 += 32;
                  var14 += 8;
               }
            }

            while(var14 < var3) {
               int var16 = this.shiftOffset - (var12 & 7);
               var13 = var10[var8 + (var12 >> 3)];
               var17 = var13 & ~(this.bitMask << var16);
               var17 |= (var5[var9++] & this.bitMask) << var16;
               var10[var8 + (var12 >> 3)] = (byte)var17;
               var12 += var6;
               ++var14;
            }

            var8 += this.scanlineStride;
         }

         this.markDirty();
      } else {
         throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
      }
   }

   public Raster createChild(int var1, int var2, int var3, int var4, int var5, int var6, int[] var7) {
      WritableRaster var8 = this.createWritableChild(var1, var2, var3, var4, var5, var6, var7);
      return var8;
   }

   public WritableRaster createWritableChild(int var1, int var2, int var3, int var4, int var5, int var6, int[] var7) {
      if (var1 < this.minX) {
         throw new RasterFormatException("x lies outside the raster");
      } else if (var2 < this.minY) {
         throw new RasterFormatException("y lies outside the raster");
      } else if (var1 + var3 >= var1 && var1 + var3 <= this.minX + this.width) {
         if (var2 + var4 >= var2 && var2 + var4 <= this.minY + this.height) {
            SampleModel var8;
            if (var7 != null) {
               var8 = this.sampleModel.createSubsetSampleModel(var7);
            } else {
               var8 = this.sampleModel;
            }

            int var9 = var5 - var1;
            int var10 = var6 - var2;
            return new BytePackedRaster(var8, this.dataBuffer, new Rectangle(var5, var6, var3, var4), new Point(this.sampleModelTranslateX + var9, this.sampleModelTranslateY + var10), this);
         } else {
            throw new RasterFormatException("(y + height) is outside of Raster");
         }
      } else {
         throw new RasterFormatException("(x + width) is outside of Raster");
      }
   }

   public WritableRaster createCompatibleWritableRaster(int var1, int var2) {
      if (var1 > 0 && var2 > 0) {
         SampleModel var3 = this.sampleModel.createCompatibleSampleModel(var1, var2);
         return new BytePackedRaster(var3, new Point(0, 0));
      } else {
         throw new RasterFormatException("negative " + (var1 <= 0 ? "width" : "height"));
      }
   }

   public WritableRaster createCompatibleWritableRaster() {
      return this.createCompatibleWritableRaster(this.width, this.height);
   }

   private void verify(boolean var1) {
      if (this.dataBitOffset < 0) {
         throw new RasterFormatException("Data offsets must be >= 0");
      } else if (this.width > 0 && this.height > 0 && this.height <= Integer.MAX_VALUE / this.width) {
         if (this.width - 1 > Integer.MAX_VALUE / this.pixelBitStride) {
            throw new RasterFormatException("Invalid raster dimension");
         } else if ((long)this.minX - (long)this.sampleModelTranslateX >= 0L && (long)this.minY - (long)this.sampleModelTranslateY >= 0L) {
            if (this.scanlineStride >= 0 && this.scanlineStride <= Integer.MAX_VALUE / this.height) {
               if ((this.height > 1 || this.minY - this.sampleModelTranslateY > 0) && this.scanlineStride > this.data.length) {
                  throw new RasterFormatException("Incorrect scanline stride: " + this.scanlineStride);
               } else {
                  long var2 = (long)this.dataBitOffset + (long)(this.height - 1) * (long)this.scanlineStride * 8L + (long)(this.width - 1) * (long)this.pixelBitStride + (long)this.pixelBitStride - 1L;
                  if (var2 >= 0L && var2 / 8L < (long)this.data.length) {
                     if (var1 && this.height > 1) {
                        var2 = (long)(this.width * this.pixelBitStride - 1);
                        if (var2 / 8L >= (long)this.scanlineStride) {
                           throw new RasterFormatException("data for adjacent scanlines overlaps");
                        }
                     }

                  } else {
                     throw new RasterFormatException("raster dimensions overflow array bounds");
                  }
               }
            } else {
               throw new RasterFormatException("Invalid scanline stride");
            }
         } else {
            throw new RasterFormatException("Incorrect origin/translate: (" + this.minX + ", " + this.minY + ") / (" + this.sampleModelTranslateX + ", " + this.sampleModelTranslateY + ")");
         }
      } else {
         throw new RasterFormatException("Invalid raster dimension");
      }
   }

   public String toString() {
      return new String("BytePackedRaster: width = " + this.width + " height = " + this.height + " #channels " + this.numBands + " xOff = " + this.sampleModelTranslateX + " yOff = " + this.sampleModelTranslateY);
   }

   static {
      NativeLibLoader.loadLibraries();
      initIDs();
   }
}
