package com.sun.imageio.plugins.bmp;

import com.sun.imageio.plugins.common.I18N;
import com.sun.imageio.plugins.common.ImageUtil;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BandedSampleModel;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.event.IIOWriteProgressListener;
import javax.imageio.event.IIOWriteWarningListener;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.bmp.BMPImageWriteParam;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

public class BMPImageWriter extends ImageWriter implements BMPConstants {
   private ImageOutputStream stream = null;
   private ByteArrayOutputStream embedded_stream = null;
   private int version;
   private int compressionType;
   private boolean isTopDown;
   private int w;
   private int h;
   private int compImageSize = 0;
   private int[] bitMasks;
   private int[] bitPos;
   private byte[] bpixels;
   private short[] spixels;
   private int[] ipixels;

   public BMPImageWriter(ImageWriterSpi var1) {
      super(var1);
   }

   public void setOutput(Object var1) {
      super.setOutput(var1);
      if (var1 != null) {
         if (!(var1 instanceof ImageOutputStream)) {
            throw new IllegalArgumentException(I18N.getString("BMPImageWriter0"));
         }

         this.stream = (ImageOutputStream)var1;
         this.stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
      } else {
         this.stream = null;
      }

   }

   public ImageWriteParam getDefaultWriteParam() {
      return new BMPImageWriteParam();
   }

   public IIOMetadata getDefaultStreamMetadata(ImageWriteParam var1) {
      return null;
   }

   public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier var1, ImageWriteParam var2) {
      BMPMetadata var3 = new BMPMetadata();
      var3.bmpVersion = "BMP v. 3.x";
      var3.compression = this.getPreferredCompressionType(var1);
      if (var2 != null && var2.getCompressionMode() == 2) {
         var3.compression = BMPCompressionTypes.getType(var2.getCompressionType());
      }

      var3.bitsPerPixel = (short)var1.getColorModel().getPixelSize();
      return var3;
   }

   public IIOMetadata convertStreamMetadata(IIOMetadata var1, ImageWriteParam var2) {
      return null;
   }

   public IIOMetadata convertImageMetadata(IIOMetadata var1, ImageTypeSpecifier var2, ImageWriteParam var3) {
      return null;
   }

   public boolean canWriteRasters() {
      return true;
   }

   public void write(IIOMetadata var1, IIOImage var2, ImageWriteParam var3) throws IOException {
      if (this.stream == null) {
         throw new IllegalStateException(I18N.getString("BMPImageWriter7"));
      } else if (var2 == null) {
         throw new IllegalArgumentException(I18N.getString("BMPImageWriter8"));
      } else {
         this.clearAbortRequest();
         this.processImageStarted(0);
         if (var3 == null) {
            var3 = this.getDefaultWriteParam();
         }

         BMPImageWriteParam var4 = (BMPImageWriteParam)var3;
         int var5 = 24;
         boolean var6 = false;
         int var7 = 0;
         IndexColorModel var8 = null;
         RenderedImage var9 = null;
         Raster var10 = null;
         boolean var11 = var2.hasRaster();
         Rectangle var12 = var3.getSourceRegion();
         SampleModel var13 = null;
         ColorModel var14 = null;
         this.compImageSize = 0;
         if (var11) {
            var10 = var2.getRaster();
            var13 = var10.getSampleModel();
            var14 = ImageUtil.createColorModel((ColorSpace)null, var13);
            if (var12 == null) {
               var12 = var10.getBounds();
            } else {
               var12 = var12.intersection(var10.getBounds());
            }
         } else {
            var9 = var2.getRenderedImage();
            var13 = var9.getSampleModel();
            var14 = var9.getColorModel();
            Rectangle var15 = new Rectangle(var9.getMinX(), var9.getMinY(), var9.getWidth(), var9.getHeight());
            if (var12 == null) {
               var12 = var15;
            } else {
               var12 = var12.intersection(var15);
            }
         }

         IIOMetadata var66 = var2.getMetadata();
         BMPMetadata var16 = null;
         if (var66 != null && var66 instanceof BMPMetadata) {
            var16 = (BMPMetadata)var66;
         } else {
            ImageTypeSpecifier var17 = new ImageTypeSpecifier(var14, var13);
            var16 = (BMPMetadata)this.getDefaultImageMetadata(var17, var3);
         }

         if (var12.isEmpty()) {
            throw new RuntimeException(I18N.getString("BMPImageWrite0"));
         } else {
            int var67 = var3.getSourceXSubsampling();
            int var18 = var3.getSourceYSubsampling();
            int var19 = var3.getSubsamplingXOffset();
            int var20 = var3.getSubsamplingYOffset();
            int var21 = var13.getDataType();
            var12.translate(var19, var20);
            var12.width -= var19;
            var12.height -= var20;
            int var22 = var12.x / var67;
            int var23 = var12.y / var18;
            this.w = (var12.width + var67 - 1) / var67;
            this.h = (var12.height + var18 - 1) / var18;
            var19 = var12.x % var67;
            var20 = var12.y % var18;
            Rectangle var24 = new Rectangle(var22, var23, this.w, this.h);
            boolean var25 = var24.equals(var12);
            int[] var26 = var3.getSourceBands();
            boolean var27 = true;
            int var28 = var13.getNumBands();
            if (var26 != null) {
               var13 = var13.createSubsetSampleModel(var26);
               var14 = null;
               var27 = false;
               var28 = var13.getNumBands();
            } else {
               var26 = new int[var28];

               for(int var29 = 0; var29 < var28; var26[var29] = var29++) {
               }
            }

            int[] var68 = null;
            boolean var30 = true;
            int var31;
            int var32;
            int[] var69;
            if (var13 instanceof ComponentSampleModel) {
               var68 = ((ComponentSampleModel)var13).getBandOffsets();
               if (var13 instanceof BandedSampleModel) {
                  var30 = false;
               } else {
                  for(var31 = 0; var31 < var68.length; ++var31) {
                     var30 &= var68[var31] == var68.length - var31 - 1;
                  }
               }
            } else if (var13 instanceof SinglePixelPackedSampleModel) {
               var69 = ((SinglePixelPackedSampleModel)var13).getBitOffsets();

               for(var32 = 0; var32 < var69.length - 1; ++var32) {
                  var30 &= var69[var32] > var69[var32 + 1];
               }
            }

            if (var68 == null) {
               var68 = new int[var28];

               for(var31 = 0; var31 < var28; var68[var31] = var31++) {
               }
            }

            var25 &= var30;
            var69 = var13.getSampleSize();
            var32 = this.w * var28;
            switch(var4.getCompressionMode()) {
            case 1:
               this.compressionType = this.getPreferredCompressionType(var14, var13);
               break;
            case 2:
               this.compressionType = BMPCompressionTypes.getType(var4.getCompressionType());
               break;
            case 3:
               this.compressionType = var16.compression;
               break;
            default:
               this.compressionType = 0;
            }

            if (!this.canEncodeImage(this.compressionType, var14, var13)) {
               throw new IOException("Image can not be encoded with compression type " + BMPCompressionTypes.getName(this.compressionType));
            } else {
               byte[] var33 = null;
               byte[] var34 = null;
               byte[] var35 = null;
               byte[] var36 = null;
               int var37;
               int var38;
               int var39;
               int var74;
               if (this.compressionType == 3) {
                  var5 = DataBuffer.getDataTypeSize(var13.getDataType());
                  if (var5 != 16 && var5 != 32) {
                     var5 = 32;
                     var25 = false;
                  }

                  var32 = this.w * var5 + 7 >> 3;
                  var6 = true;
                  var7 = 3;
                  var33 = new byte[var7];
                  var34 = new byte[var7];
                  var35 = new byte[var7];
                  var36 = new byte[var7];
                  var37 = 16711680;
                  var38 = 65280;
                  var39 = 255;
                  if (var5 == 16) {
                     if (!(var14 instanceof DirectColorModel)) {
                        throw new IOException("Image can not be encoded with compression type " + BMPCompressionTypes.getName(this.compressionType));
                     }

                     DirectColorModel var40 = (DirectColorModel)var14;
                     var37 = var40.getRedMask();
                     var38 = var40.getGreenMask();
                     var39 = var40.getBlueMask();
                  }

                  this.writeMaskToPalette(var37, 0, var33, var34, var35, var36);
                  this.writeMaskToPalette(var38, 1, var33, var34, var35, var36);
                  this.writeMaskToPalette(var39, 2, var33, var34, var35, var36);
                  if (!var25) {
                     this.bitMasks = new int[3];
                     this.bitMasks[0] = var37;
                     this.bitMasks[1] = var38;
                     this.bitMasks[2] = var39;
                     this.bitPos = new int[3];
                     this.bitPos[0] = this.firstLowBit(var37);
                     this.bitPos[1] = this.firstLowBit(var38);
                     this.bitPos[2] = this.firstLowBit(var39);
                  }

                  if (var14 instanceof IndexColorModel) {
                     var8 = (IndexColorModel)var14;
                  }
               } else if (var14 instanceof IndexColorModel) {
                  var6 = true;
                  var8 = (IndexColorModel)var14;
                  var7 = var8.getMapSize();
                  if (var7 <= 2) {
                     var5 = 1;
                     var32 = this.w + 7 >> 3;
                  } else if (var7 <= 16) {
                     var5 = 4;
                     var32 = this.w + 1 >> 1;
                  } else if (var7 <= 256) {
                     var5 = 8;
                  } else {
                     var5 = 24;
                     var6 = false;
                     var7 = 0;
                     var32 = this.w * 3;
                  }

                  if (var6) {
                     var33 = new byte[var7];
                     var34 = new byte[var7];
                     var35 = new byte[var7];
                     var36 = new byte[var7];
                     var8.getAlphas(var36);
                     var8.getReds(var33);
                     var8.getGreens(var34);
                     var8.getBlues(var35);
                  }
               } else if (var28 == 1) {
                  var6 = true;
                  var7 = 256;
                  var5 = var69[0];
                  var32 = this.w * var5 + 7 >> 3;
                  var33 = new byte[256];
                  var34 = new byte[256];
                  var35 = new byte[256];
                  var36 = new byte[256];

                  for(var37 = 0; var37 < 256; ++var37) {
                     var33[var37] = (byte)var37;
                     var34[var37] = (byte)var37;
                     var35[var37] = (byte)var37;
                     var36[var37] = -1;
                  }
               } else if (var13 instanceof SinglePixelPackedSampleModel && var27) {
                  int[] var70 = var13.getSampleSize();
                  var5 = 0;
                  int[] var71 = var70;
                  var39 = var70.length;

                  for(var74 = 0; var74 < var39; ++var74) {
                     int var41 = var71[var74];
                     var5 += var41;
                  }

                  var5 = this.roundBpp(var5);
                  if (var5 != DataBuffer.getDataTypeSize(var13.getDataType())) {
                     var25 = false;
                  }

                  var32 = this.w * var5 + 7 >> 3;
               }

               boolean var72 = false;
               boolean var73 = false;
               boolean var75 = false;
               boolean var77 = false;
               byte var78 = 0;
               byte var42 = 0;
               byte var43 = 0;
               int var45 = var32 % 4;
               if (var45 != 0) {
                  var45 = 4 - var45;
               }

               var38 = 54 + var7 * 4;
               var74 = (var32 + var45) * this.h;
               var37 = var74 + var38;
               byte var76 = 40;
               long var46 = this.stream.getStreamPosition();
               this.writeFileHeader(var37, var38);
               if (this.compressionType != 0 && this.compressionType != 3) {
                  this.isTopDown = false;
               } else {
                  this.isTopDown = var4.isTopDown();
               }

               this.writeInfoHeader(var76, var5);
               this.stream.writeInt(this.compressionType);
               this.stream.writeInt(var74);
               this.stream.writeInt(var78);
               this.stream.writeInt(var42);
               this.stream.writeInt(var43);
               this.stream.writeInt(var7);
               int var48;
               if (var6) {
                  if (this.compressionType == 3) {
                     for(var48 = 0; var48 < 3; ++var48) {
                        int var49 = (var36[var48] & 255) + (var33[var48] & 255) * 256 + (var34[var48] & 255) * 65536 + (var35[var48] & 255) * 16777216;
                        this.stream.writeInt(var49);
                     }
                  } else {
                     for(var48 = 0; var48 < var7; ++var48) {
                        this.stream.writeByte(var35[var48]);
                        this.stream.writeByte(var34[var48]);
                        this.stream.writeByte(var33[var48]);
                        this.stream.writeByte(var36[var48]);
                     }
                  }
               }

               var48 = this.w * var28;
               int[] var79 = new int[var48 * var67];
               this.bpixels = new byte[var32];
               if (this.compressionType != 4 && this.compressionType != 5) {
                  int var80 = var68[0];

                  for(int var52 = 1; var52 < var68.length; ++var52) {
                     if (var68[var52] > var80) {
                        var80 = var68[var52];
                     }
                  }

                  int[] var81 = new int[var80 + 1];
                  int var53 = var32;
                  if (var25 && var27) {
                     var53 = var32 / (DataBuffer.getDataTypeSize(var21) >> 3);
                  }

                  for(int var54 = 0; var54 < this.h && !this.abortRequested(); ++var54) {
                     int var55 = var23 + var54;
                     if (!this.isTopDown) {
                        var55 = var23 + this.h - var54 - 1;
                     }

                     Raster var56 = var10;
                     Rectangle var57 = new Rectangle(var22 * var67 + var19, var55 * var18 + var20, (this.w - 1) * var67 + 1, 1);
                     if (!var11) {
                        var56 = var9.getData(var57);
                     }

                     int var59;
                     int var60;
                     int var61;
                     if (var25 && var27) {
                        SampleModel var83 = var56.getSampleModel();
                        var59 = 0;
                        var60 = var57.x - var56.getSampleModelTranslateX();
                        var61 = var57.y - var56.getSampleModelTranslateY();
                        if (var83 instanceof ComponentSampleModel) {
                           ComponentSampleModel var85 = (ComponentSampleModel)var83;
                           var59 = var85.getOffset(var60, var61, 0);

                           for(int var63 = 1; var63 < var85.getNumBands(); ++var63) {
                              if (var59 > var85.getOffset(var60, var61, var63)) {
                                 var59 = var85.getOffset(var60, var61, var63);
                              }
                           }
                        } else if (var83 instanceof MultiPixelPackedSampleModel) {
                           MultiPixelPackedSampleModel var62 = (MultiPixelPackedSampleModel)var83;
                           var59 = var62.getOffset(var60, var61);
                        } else if (var83 instanceof SinglePixelPackedSampleModel) {
                           SinglePixelPackedSampleModel var84 = (SinglePixelPackedSampleModel)var83;
                           var59 = var84.getOffset(var60, var61);
                        }

                        int var86;
                        if (this.compressionType == 0 || this.compressionType == 3) {
                           switch(var21) {
                           case 0:
                              byte[] var88 = ((DataBufferByte)var56.getDataBuffer()).getData();
                              this.stream.write(var88, var59, var53);
                              break;
                           case 1:
                              short[] var64 = ((DataBufferUShort)var56.getDataBuffer()).getData();
                              this.stream.writeShorts(var64, var59, var53);
                              break;
                           case 2:
                              short[] var87 = ((DataBufferShort)var56.getDataBuffer()).getData();
                              this.stream.writeShorts(var87, var59, var53);
                              break;
                           case 3:
                              int[] var65 = ((DataBufferInt)var56.getDataBuffer()).getData();
                              this.stream.writeInts(var65, var59, var53);
                           }

                           for(var86 = 0; var86 < var45; ++var86) {
                              this.stream.writeByte(0);
                           }
                        } else if (this.compressionType == 2) {
                           if (this.bpixels == null || this.bpixels.length < var48) {
                              this.bpixels = new byte[var48];
                           }

                           var56.getPixels(var57.x, var57.y, var57.width, var57.height, var79);

                           for(var86 = 0; var86 < var48; ++var86) {
                              this.bpixels[var86] = (byte)var79[var86];
                           }

                           this.encodeRLE4(this.bpixels, var48);
                        } else if (this.compressionType == 1) {
                           if (this.bpixels == null || this.bpixels.length < var48) {
                              this.bpixels = new byte[var48];
                           }

                           var56.getPixels(var57.x, var57.y, var57.width, var57.height, var79);

                           for(var86 = 0; var86 < var48; ++var86) {
                              this.bpixels[var86] = (byte)var79[var86];
                           }

                           this.encodeRLE8(this.bpixels, var48);
                        }
                     } else {
                        var56.getPixels(var57.x, var57.y, var57.width, var57.height, var79);
                        if (var67 != 1 || var80 != var28 - 1) {
                           int var58 = 0;
                           var59 = 0;

                           for(var60 = 0; var58 < this.w; var60 += var28) {
                              System.arraycopy(var79, var59, var81, 0, var81.length);

                              for(var61 = 0; var61 < var28; ++var61) {
                                 var79[var60 + var61] = var81[var26[var61]];
                              }

                              ++var58;
                              var59 += var67 * var28;
                           }
                        }

                        this.writePixels(0, var48, var5, var79, var45, var28, var8);
                     }

                     this.processImageProgress(100.0F * ((float)var54 / (float)this.h));
                  }

                  if (this.compressionType == 2 || this.compressionType == 1) {
                     this.stream.writeByte(0);
                     this.stream.writeByte(1);
                     this.incCompImageSize(2);
                     var74 = this.compImageSize;
                     var37 = this.compImageSize + var38;
                     long var82 = this.stream.getStreamPosition();
                     this.stream.seek(var46);
                     this.writeSize(var37, 2);
                     this.stream.seek(var46);
                     this.writeSize(var74, 34);
                     this.stream.seek(var82);
                  }

                  if (this.abortRequested()) {
                     this.processWriteAborted();
                  } else {
                     this.processImageComplete();
                     this.stream.flushBefore(this.stream.getStreamPosition());
                  }

               } else {
                  this.embedded_stream = new ByteArrayOutputStream();
                  this.writeEmbedded(var2, var4);
                  this.embedded_stream.flush();
                  var74 = this.embedded_stream.size();
                  long var51 = this.stream.getStreamPosition();
                  var37 = var38 + var74;
                  this.stream.seek(var46);
                  this.writeSize(var37, 2);
                  this.stream.seek(var46);
                  this.writeSize(var74, 34);
                  this.stream.seek(var51);
                  this.stream.write(this.embedded_stream.toByteArray());
                  this.embedded_stream = null;
                  if (this.abortRequested()) {
                     this.processWriteAborted();
                  } else {
                     this.processImageComplete();
                     this.stream.flushBefore(this.stream.getStreamPosition());
                  }

               }
            }
         }
      }
   }

   private void writePixels(int var1, int var2, int var3, int[] var4, int var5, int var6, IndexColorModel var7) throws IOException {
      boolean var8 = false;
      int var9 = 0;
      int var10;
      int var11;
      int var12;
      int var16;
      switch(var3) {
      case 1:
         for(var10 = 0; var10 < var2 / 8; ++var10) {
            this.bpixels[var9++] = (byte)(var4[var1++] << 7 | var4[var1++] << 6 | var4[var1++] << 5 | var4[var1++] << 4 | var4[var1++] << 3 | var4[var1++] << 2 | var4[var1++] << 1 | var4[var1++]);
         }

         if (var2 % 8 > 0) {
            var16 = 0;

            for(var10 = 0; var10 < var2 % 8; ++var10) {
               var16 |= var4[var1++] << 7 - var10;
            }

            this.bpixels[var9++] = (byte)var16;
         }

         this.stream.write(this.bpixels, 0, (var2 + 7) / 8);
         break;
      case 4:
         if (this.compressionType == 2) {
            byte[] var19 = new byte[var2];

            for(var11 = 0; var11 < var2; ++var11) {
               var19[var11] = (byte)var4[var1++];
            }

            this.encodeRLE4(var19, var2);
         } else {
            for(var10 = 0; var10 < var2 / 2; ++var10) {
               var16 = var4[var1++] << 4 | var4[var1++];
               this.bpixels[var9++] = (byte)var16;
            }

            if (var2 % 2 == 1) {
               var16 = var4[var1] << 4;
               this.bpixels[var9++] = (byte)var16;
            }

            this.stream.write(this.bpixels, 0, (var2 + 1) / 2);
         }
         break;
      case 8:
         if (this.compressionType == 1) {
            for(var10 = 0; var10 < var2; ++var10) {
               this.bpixels[var10] = (byte)var4[var1++];
            }

            this.encodeRLE8(this.bpixels, var2);
         } else {
            for(var10 = 0; var10 < var2; ++var10) {
               this.bpixels[var10] = (byte)var4[var1++];
            }

            this.stream.write(this.bpixels, 0, var2);
         }
         break;
      case 16:
         if (this.spixels == null) {
            this.spixels = new short[var2 / var6];
         }

         var10 = 0;

         for(var11 = 0; var10 < var2; ++var11) {
            this.spixels[var11] = 0;
            if (this.compressionType == 0) {
               this.spixels[var11] = (short)((31 & var4[var10]) << 10 | (31 & var4[var10 + 1]) << 5 | 31 & var4[var10 + 2]);
               var10 += 3;
            } else {
               for(var12 = 0; var12 < var6; ++var10) {
                  short[] var20 = this.spixels;
                  var20[var11] = (short)(var20[var11] | var4[var10] << this.bitPos[var12] & this.bitMasks[var12]);
                  ++var12;
               }
            }
         }

         this.stream.writeShorts(this.spixels, 0, this.spixels.length);
         break;
      case 24:
         if (var6 == 3) {
            for(var10 = 0; var10 < var2; var10 += 3) {
               this.bpixels[var9++] = (byte)var4[var1 + 2];
               this.bpixels[var9++] = (byte)var4[var1 + 1];
               this.bpixels[var9++] = (byte)var4[var1];
               var1 += 3;
            }

            this.stream.write(this.bpixels, 0, var2);
         } else {
            var10 = var7.getMapSize();
            byte[] var17 = new byte[var10];
            byte[] var18 = new byte[var10];
            byte[] var13 = new byte[var10];
            var7.getReds(var17);
            var7.getGreens(var18);
            var7.getBlues(var13);

            for(int var15 = 0; var15 < var2; ++var15) {
               int var14 = var4[var1];
               this.bpixels[var9++] = var13[var14];
               this.bpixels[var9++] = var18[var14];
               this.bpixels[var9++] = var13[var14];
               ++var1;
            }

            this.stream.write(this.bpixels, 0, var2 * 3);
         }
         break;
      case 32:
         if (this.ipixels == null) {
            this.ipixels = new int[var2 / var6];
         }

         if (var6 != 3) {
            for(var10 = 0; var10 < var2; ++var10) {
               if (var7 != null) {
                  this.ipixels[var10] = var7.getRGB(var4[var10]);
               } else {
                  this.ipixels[var10] = var4[var10] << 16 | var4[var10] << 8 | var4[var10];
               }
            }
         } else {
            var10 = 0;

            for(var11 = 0; var10 < var2; ++var11) {
               this.ipixels[var11] = 0;
               if (this.compressionType == 0) {
                  this.ipixels[var11] = (255 & var4[var10 + 2]) << 16 | (255 & var4[var10 + 1]) << 8 | 255 & var4[var10];
                  var10 += 3;
               } else {
                  for(var12 = 0; var12 < var6; ++var10) {
                     int[] var10000 = this.ipixels;
                     var10000[var11] |= var4[var10] << this.bitPos[var12] & this.bitMasks[var12];
                     ++var12;
                  }
               }
            }
         }

         this.stream.writeInts(this.ipixels, 0, this.ipixels.length);
      }

      if (this.compressionType == 0 || this.compressionType == 3) {
         for(var9 = 0; var9 < var5; ++var9) {
            this.stream.writeByte(0);
         }
      }

   }

   private void encodeRLE8(byte[] var1, int var2) throws IOException {
      int var3 = 1;
      int var4 = -1;
      byte var5 = -1;
      boolean var6 = false;
      boolean var7 = false;
      int var10 = var5 + 1;
      byte var11 = var1[var10];
      byte[] var8 = new byte[256];

      while(true) {
         int var9;
         do {
            if (var10 >= var2 - 1) {
               return;
            }

            ++var10;
            byte var12 = var1[var10];
            if (var12 == var11) {
               if (var4 >= 3) {
                  this.stream.writeByte(0);
                  this.stream.writeByte(var4);
                  this.incCompImageSize(2);

                  for(var9 = 0; var9 < var4; ++var9) {
                     this.stream.writeByte(var8[var9]);
                     this.incCompImageSize(1);
                  }

                  if (!this.isEven(var4)) {
                     this.stream.writeByte(0);
                     this.incCompImageSize(1);
                  }
               } else if (var4 > -1) {
                  for(var9 = 0; var9 < var4; ++var9) {
                     this.stream.writeByte(1);
                     this.stream.writeByte(var8[var9]);
                     this.incCompImageSize(2);
                  }
               }

               var4 = -1;
               ++var3;
               if (var3 == 256) {
                  this.stream.writeByte(var3 - 1);
                  this.stream.writeByte(var11);
                  this.incCompImageSize(2);
                  var3 = 1;
               }
            } else {
               if (var3 > 1) {
                  this.stream.writeByte(var3);
                  this.stream.writeByte(var11);
                  this.incCompImageSize(2);
               } else if (var4 < 0) {
                  ++var4;
                  var8[var4] = var11;
                  ++var4;
                  var8[var4] = var12;
               } else if (var4 < 254) {
                  ++var4;
                  var8[var4] = var12;
               } else {
                  this.stream.writeByte(0);
                  this.stream.writeByte(var4 + 1);
                  this.incCompImageSize(2);

                  for(var9 = 0; var9 <= var4; ++var9) {
                     this.stream.writeByte(var8[var9]);
                     this.incCompImageSize(1);
                  }

                  this.stream.writeByte(0);
                  this.incCompImageSize(1);
                  var4 = -1;
               }

               var11 = var12;
               var3 = 1;
            }
         } while(var10 != var2 - 1);

         if (var4 == -1) {
            this.stream.writeByte(var3);
            this.stream.writeByte(var11);
            this.incCompImageSize(2);
            var3 = 1;
         } else if (var4 < 2) {
            if (var4 > -1) {
               for(var9 = 0; var9 <= var4; ++var9) {
                  this.stream.writeByte(1);
                  this.stream.writeByte(var8[var9]);
                  this.incCompImageSize(2);
               }
            }
         } else {
            this.stream.writeByte(0);
            this.stream.writeByte(var4 + 1);
            this.incCompImageSize(2);

            for(var9 = 0; var9 <= var4; ++var9) {
               this.stream.writeByte(var8[var9]);
               this.incCompImageSize(1);
            }

            if (!this.isEven(var4 + 1)) {
               this.stream.writeByte(0);
               this.incCompImageSize(1);
            }
         }

         this.stream.writeByte(0);
         this.stream.writeByte(0);
         this.incCompImageSize(2);
      }
   }

   private void encodeRLE4(byte[] var1, int var2) throws IOException {
      int var3 = 2;
      int var4 = -1;
      byte var5 = -1;
      boolean var6 = false;
      boolean var7 = false;
      boolean var8 = false;
      boolean var9 = false;
      boolean var10 = false;
      boolean var11 = false;
      byte[] var12 = new byte[256];
      int var14 = var5 + 1;
      byte var17 = var1[var14];
      ++var14;
      byte var18 = var1[var14];

      while(true) {
         int var13;
         int var15;
         int var16;
         do {
            if (var14 >= var2 - 2) {
               return;
            }

            ++var14;
            byte var19 = var1[var14];
            ++var14;
            byte var20 = var1[var14];
            if (var19 == var17) {
               if (var4 >= 4) {
                  this.stream.writeByte(0);
                  this.stream.writeByte(var4 - 1);
                  this.incCompImageSize(2);

                  for(var13 = 0; var13 < var4 - 2; var13 += 2) {
                     var15 = var12[var13] << 4 | var12[var13 + 1];
                     this.stream.writeByte((byte)var15);
                     this.incCompImageSize(1);
                  }

                  if (!this.isEven(var4 - 1)) {
                     var16 = var12[var4 - 2] << 4 | 0;
                     this.stream.writeByte(var16);
                     this.incCompImageSize(1);
                  }

                  if (!this.isEven((int)Math.ceil((double)((var4 - 1) / 2)))) {
                     this.stream.writeByte(0);
                     this.incCompImageSize(1);
                  }
               } else if (var4 > -1) {
                  this.stream.writeByte(2);
                  var15 = var12[0] << 4 | var12[1];
                  this.stream.writeByte(var15);
                  this.incCompImageSize(2);
               }

               var4 = -1;
               if (var20 == var18) {
                  var3 += 2;
                  if (var3 == 256) {
                     this.stream.writeByte(var3 - 1);
                     var15 = var17 << 4 | var18;
                     this.stream.writeByte(var15);
                     this.incCompImageSize(2);
                     var3 = 2;
                     if (var14 < var2 - 1) {
                        var17 = var18;
                        ++var14;
                        var18 = var1[var14];
                     } else {
                        this.stream.writeByte(1);
                        var13 = var18 << 4 | 0;
                        this.stream.writeByte(var13);
                        this.incCompImageSize(2);
                        var3 = -1;
                     }
                  }
               } else {
                  ++var3;
                  var15 = var17 << 4 | var18;
                  this.stream.writeByte(var3);
                  this.stream.writeByte(var15);
                  this.incCompImageSize(2);
                  var3 = 2;
                  var17 = var20;
                  if (var14 < var2 - 1) {
                     ++var14;
                     var18 = var1[var14];
                  } else {
                     this.stream.writeByte(1);
                     var13 = var20 << 4 | 0;
                     this.stream.writeByte(var13);
                     this.incCompImageSize(2);
                     var3 = -1;
                  }
               }
            } else {
               if (var3 > 2) {
                  var15 = var17 << 4 | var18;
                  this.stream.writeByte(var3);
                  this.stream.writeByte(var15);
                  this.incCompImageSize(2);
               } else if (var4 < 0) {
                  ++var4;
                  var12[var4] = var17;
                  ++var4;
                  var12[var4] = var18;
                  ++var4;
                  var12[var4] = var19;
                  ++var4;
                  var12[var4] = var20;
               } else if (var4 < 253) {
                  ++var4;
                  var12[var4] = var19;
                  ++var4;
                  var12[var4] = var20;
               } else {
                  this.stream.writeByte(0);
                  this.stream.writeByte(var4 + 1);
                  this.incCompImageSize(2);

                  for(var13 = 0; var13 < var4; var13 += 2) {
                     var15 = var12[var13] << 4 | var12[var13 + 1];
                     this.stream.writeByte((byte)var15);
                     this.incCompImageSize(1);
                  }

                  this.stream.writeByte(0);
                  this.incCompImageSize(1);
                  var4 = -1;
               }

               var17 = var19;
               var18 = var20;
               var3 = 2;
            }
         } while(var14 < var2 - 2);

         if (var4 == -1 && var3 >= 2) {
            if (var14 == var2 - 2) {
               ++var14;
               if (var1[var14] == var17) {
                  ++var3;
                  var15 = var17 << 4 | var18;
                  this.stream.writeByte(var3);
                  this.stream.writeByte(var15);
                  this.incCompImageSize(2);
               } else {
                  var15 = var17 << 4 | var18;
                  this.stream.writeByte(var3);
                  this.stream.writeByte(var15);
                  this.stream.writeByte(1);
                  var15 = var1[var14] << 4 | 0;
                  this.stream.writeByte(var15);
                  var13 = var1[var14] << 4 | 0;
                  this.incCompImageSize(4);
               }
            } else {
               this.stream.writeByte(var3);
               var15 = var17 << 4 | var18;
               this.stream.writeByte(var15);
               this.incCompImageSize(2);
            }
         } else if (var4 > -1) {
            if (var14 == var2 - 2) {
               ++var4;
               ++var14;
               var12[var4] = var1[var14];
            }

            if (var4 < 2) {
               switch(var4) {
               case 0:
                  this.stream.writeByte(1);
                  var13 = var12[0] << 4 | 0;
                  this.stream.writeByte(var13);
                  this.incCompImageSize(2);
                  break;
               case 1:
                  this.stream.writeByte(2);
                  var15 = var12[0] << 4 | var12[1];
                  this.stream.writeByte(var15);
                  this.incCompImageSize(2);
               }
            } else {
               this.stream.writeByte(0);
               this.stream.writeByte(var4 + 1);
               this.incCompImageSize(2);

               for(var13 = 0; var13 < var4; var13 += 2) {
                  var15 = var12[var13] << 4 | var12[var13 + 1];
                  this.stream.writeByte((byte)var15);
                  this.incCompImageSize(1);
               }

               if (!this.isEven(var4 + 1)) {
                  var16 = var12[var4] << 4 | 0;
                  this.stream.writeByte(var16);
                  this.incCompImageSize(1);
               }

               if (!this.isEven((int)Math.ceil((double)((var4 + 1) / 2)))) {
                  this.stream.writeByte(0);
                  this.incCompImageSize(1);
               }
            }
         }

         this.stream.writeByte(0);
         this.stream.writeByte(0);
         this.incCompImageSize(2);
      }
   }

   private synchronized void incCompImageSize(int var1) {
      this.compImageSize += var1;
   }

   private boolean isEven(int var1) {
      return var1 % 2 == 0;
   }

   private void writeFileHeader(int var1, int var2) throws IOException {
      this.stream.writeByte(66);
      this.stream.writeByte(77);
      this.stream.writeInt(var1);
      this.stream.writeInt(0);
      this.stream.writeInt(var2);
   }

   private void writeInfoHeader(int var1, int var2) throws IOException {
      this.stream.writeInt(var1);
      this.stream.writeInt(this.w);
      this.stream.writeInt(this.isTopDown ? -this.h : this.h);
      this.stream.writeShort(1);
      this.stream.writeShort(var2);
   }

   private void writeSize(int var1, int var2) throws IOException {
      this.stream.skipBytes(var2);
      this.stream.writeInt(var1);
   }

   public void reset() {
      super.reset();
      this.stream = null;
   }

   private void writeEmbedded(IIOImage var1, ImageWriteParam var2) throws IOException {
      String var3 = this.compressionType == 4 ? "jpeg" : "png";
      Iterator var4 = ImageIO.getImageWritersByFormatName(var3);
      ImageWriter var5 = null;
      if (var4.hasNext()) {
         var5 = (ImageWriter)var4.next();
      }

      if (var5 != null) {
         if (this.embedded_stream == null) {
            throw new RuntimeException("No stream for writing embedded image!");
         } else {
            var5.addIIOWriteProgressListener(new BMPImageWriter.IIOWriteProgressAdapter() {
               public void imageProgress(ImageWriter var1, float var2) {
                  BMPImageWriter.this.processImageProgress(var2);
               }
            });
            var5.addIIOWriteWarningListener(new IIOWriteWarningListener() {
               public void warningOccurred(ImageWriter var1, int var2, String var3) {
                  BMPImageWriter.this.processWarningOccurred(var2, var3);
               }
            });
            var5.setOutput(ImageIO.createImageOutputStream(this.embedded_stream));
            ImageWriteParam var6 = var5.getDefaultWriteParam();
            var6.setDestinationOffset(var2.getDestinationOffset());
            var6.setSourceBands(var2.getSourceBands());
            var6.setSourceRegion(var2.getSourceRegion());
            var6.setSourceSubsampling(var2.getSourceXSubsampling(), var2.getSourceYSubsampling(), var2.getSubsamplingXOffset(), var2.getSubsamplingYOffset());
            var5.write((IIOMetadata)null, var1, var6);
         }
      } else {
         throw new RuntimeException(I18N.getString("BMPImageWrite5") + " " + var3);
      }
   }

   private int firstLowBit(int var1) {
      int var2;
      for(var2 = 0; (var1 & 1) == 0; var1 >>>= 1) {
         ++var2;
      }

      return var2;
   }

   protected int getPreferredCompressionType(ColorModel var1, SampleModel var2) {
      ImageTypeSpecifier var3 = new ImageTypeSpecifier(var1, var2);
      return this.getPreferredCompressionType(var3);
   }

   protected int getPreferredCompressionType(ImageTypeSpecifier var1) {
      return var1.getBufferedImageType() == 8 ? 3 : 0;
   }

   protected boolean canEncodeImage(int var1, ColorModel var2, SampleModel var3) {
      ImageTypeSpecifier var4 = new ImageTypeSpecifier(var2, var3);
      return this.canEncodeImage(var1, var4);
   }

   protected boolean canEncodeImage(int var1, ImageTypeSpecifier var2) {
      ImageWriterSpi var3 = this.getOriginatingProvider();
      if (!var3.canEncodeImage(var2)) {
         return false;
      } else {
         int var4 = var2.getBufferedImageType();
         int var5 = var2.getColorModel().getPixelSize();
         if (this.compressionType == 2 && var5 != 4) {
            return false;
         } else if (this.compressionType == 1 && var5 != 8) {
            return false;
         } else if (var5 != 16) {
            return true;
         } else {
            boolean var6 = false;
            boolean var7 = false;
            SampleModel var8 = var2.getSampleModel();
            if (var8 instanceof SinglePixelPackedSampleModel) {
               int[] var9 = ((SinglePixelPackedSampleModel)var8).getSampleSize();
               var6 = true;
               var7 = true;

               for(int var10 = 0; var10 < var9.length; ++var10) {
                  var6 &= var9[var10] == 5;
                  var7 &= var9[var10] == 5 || var10 == 1 && var9[var10] == 6;
               }
            }

            return this.compressionType == 0 && var6 || this.compressionType == 3 && var7;
         }
      }
   }

   protected void writeMaskToPalette(int var1, int var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6) {
      var5[var2] = (byte)(255 & var1 >> 24);
      var4[var2] = (byte)(255 & var1 >> 16);
      var3[var2] = (byte)(255 & var1 >> 8);
      var6[var2] = (byte)(255 & var1);
   }

   private int roundBpp(int var1) {
      if (var1 <= 8) {
         return 8;
      } else if (var1 <= 16) {
         return 16;
      } else {
         return var1 <= 24 ? 24 : 32;
      }
   }

   private class IIOWriteProgressAdapter implements IIOWriteProgressListener {
      private IIOWriteProgressAdapter() {
      }

      public void imageComplete(ImageWriter var1) {
      }

      public void imageProgress(ImageWriter var1, float var2) {
      }

      public void imageStarted(ImageWriter var1, int var2) {
      }

      public void thumbnailComplete(ImageWriter var1) {
      }

      public void thumbnailProgress(ImageWriter var1, float var2) {
      }

      public void thumbnailStarted(ImageWriter var1, int var2, int var3) {
      }

      public void writeAborted(ImageWriter var1) {
      }

      // $FF: synthetic method
      IIOWriteProgressAdapter(Object var2) {
         this();
      }
   }
}
