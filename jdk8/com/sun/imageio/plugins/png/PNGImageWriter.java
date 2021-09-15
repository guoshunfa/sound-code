package com.sun.imageio.plugins.png;

import java.awt.Rectangle;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.zip.DeflaterOutputStream;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

public class PNGImageWriter extends ImageWriter {
   ImageOutputStream stream = null;
   PNGMetadata metadata = null;
   int sourceXOffset = 0;
   int sourceYOffset = 0;
   int sourceWidth = 0;
   int sourceHeight = 0;
   int[] sourceBands = null;
   int periodX = 1;
   int periodY = 1;
   int numBands;
   int bpp;
   RowFilter rowFilter = new RowFilter();
   byte[] prevRow = null;
   byte[] currRow = null;
   byte[][] filteredRows = (byte[][])null;
   int[] sampleSize = null;
   int scalingBitDepth = -1;
   byte[][] scale = (byte[][])null;
   byte[] scale0 = null;
   byte[][] scaleh = (byte[][])null;
   byte[][] scalel = (byte[][])null;
   int totalPixels;
   int pixelsDone;
   private static int[] allowedProgressivePasses = new int[]{1, 7};

   public PNGImageWriter(ImageWriterSpi var1) {
      super(var1);
   }

   public void setOutput(Object var1) {
      super.setOutput(var1);
      if (var1 != null) {
         if (!(var1 instanceof ImageOutputStream)) {
            throw new IllegalArgumentException("output not an ImageOutputStream!");
         }

         this.stream = (ImageOutputStream)var1;
      } else {
         this.stream = null;
      }

   }

   public ImageWriteParam getDefaultWriteParam() {
      return new PNGImageWriteParam(this.getLocale());
   }

   public IIOMetadata getDefaultStreamMetadata(ImageWriteParam var1) {
      return null;
   }

   public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier var1, ImageWriteParam var2) {
      PNGMetadata var3 = new PNGMetadata();
      var3.initialize(var1, var1.getSampleModel().getNumBands());
      return var3;
   }

   public IIOMetadata convertStreamMetadata(IIOMetadata var1, ImageWriteParam var2) {
      return null;
   }

   public IIOMetadata convertImageMetadata(IIOMetadata var1, ImageTypeSpecifier var2, ImageWriteParam var3) {
      return var1 instanceof PNGMetadata ? (PNGMetadata)((PNGMetadata)var1).clone() : new PNGMetadata(var1);
   }

   private void write_magic() throws IOException {
      byte[] var1 = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10};
      this.stream.write(var1);
   }

   private void write_IHDR() throws IOException {
      ChunkStream var1 = new ChunkStream(1229472850, this.stream);
      var1.writeInt(this.metadata.IHDR_width);
      var1.writeInt(this.metadata.IHDR_height);
      var1.writeByte(this.metadata.IHDR_bitDepth);
      var1.writeByte(this.metadata.IHDR_colorType);
      if (this.metadata.IHDR_compressionMethod != 0) {
         throw new IIOException("Only compression method 0 is defined in PNG 1.1");
      } else {
         var1.writeByte(this.metadata.IHDR_compressionMethod);
         if (this.metadata.IHDR_filterMethod != 0) {
            throw new IIOException("Only filter method 0 is defined in PNG 1.1");
         } else {
            var1.writeByte(this.metadata.IHDR_filterMethod);
            if (this.metadata.IHDR_interlaceMethod >= 0 && this.metadata.IHDR_interlaceMethod <= 1) {
               var1.writeByte(this.metadata.IHDR_interlaceMethod);
               var1.finish();
            } else {
               throw new IIOException("Only interlace methods 0 (node) and 1 (adam7) are defined in PNG 1.1");
            }
         }
      }
   }

   private void write_cHRM() throws IOException {
      if (this.metadata.cHRM_present) {
         ChunkStream var1 = new ChunkStream(1665684045, this.stream);
         var1.writeInt(this.metadata.cHRM_whitePointX);
         var1.writeInt(this.metadata.cHRM_whitePointY);
         var1.writeInt(this.metadata.cHRM_redX);
         var1.writeInt(this.metadata.cHRM_redY);
         var1.writeInt(this.metadata.cHRM_greenX);
         var1.writeInt(this.metadata.cHRM_greenY);
         var1.writeInt(this.metadata.cHRM_blueX);
         var1.writeInt(this.metadata.cHRM_blueY);
         var1.finish();
      }

   }

   private void write_gAMA() throws IOException {
      if (this.metadata.gAMA_present) {
         ChunkStream var1 = new ChunkStream(1732332865, this.stream);
         var1.writeInt(this.metadata.gAMA_gamma);
         var1.finish();
      }

   }

   private void write_iCCP() throws IOException {
      if (this.metadata.iCCP_present) {
         ChunkStream var1 = new ChunkStream(1766015824, this.stream);
         var1.writeBytes(this.metadata.iCCP_profileName);
         var1.writeByte(0);
         var1.writeByte(this.metadata.iCCP_compressionMethod);
         var1.write(this.metadata.iCCP_compressedProfile);
         var1.finish();
      }

   }

   private void write_sBIT() throws IOException {
      if (this.metadata.sBIT_present) {
         ChunkStream var1 = new ChunkStream(1933723988, this.stream);
         int var2 = this.metadata.IHDR_colorType;
         if (this.metadata.sBIT_colorType != var2) {
            this.processWarningOccurred(0, "sBIT metadata has wrong color type.\nThe chunk will not be written.");
            return;
         }

         if (var2 != 0 && var2 != 4) {
            if (var2 == 2 || var2 == 3 || var2 == 6) {
               var1.writeByte(this.metadata.sBIT_redBits);
               var1.writeByte(this.metadata.sBIT_greenBits);
               var1.writeByte(this.metadata.sBIT_blueBits);
            }
         } else {
            var1.writeByte(this.metadata.sBIT_grayBits);
         }

         if (var2 == 4 || var2 == 6) {
            var1.writeByte(this.metadata.sBIT_alphaBits);
         }

         var1.finish();
      }

   }

   private void write_sRGB() throws IOException {
      if (this.metadata.sRGB_present) {
         ChunkStream var1 = new ChunkStream(1934772034, this.stream);
         var1.writeByte(this.metadata.sRGB_renderingIntent);
         var1.finish();
      }

   }

   private void write_PLTE() throws IOException {
      if (this.metadata.PLTE_present) {
         if (this.metadata.IHDR_colorType == 0 || this.metadata.IHDR_colorType == 4) {
            this.processWarningOccurred(0, "A PLTE chunk may not appear in a gray or gray alpha image.\nThe chunk will not be written");
            return;
         }

         ChunkStream var1 = new ChunkStream(1347179589, this.stream);
         int var2 = this.metadata.PLTE_red.length;
         byte[] var3 = new byte[var2 * 3];
         int var4 = 0;

         for(int var5 = 0; var5 < var2; ++var5) {
            var3[var4++] = this.metadata.PLTE_red[var5];
            var3[var4++] = this.metadata.PLTE_green[var5];
            var3[var4++] = this.metadata.PLTE_blue[var5];
         }

         var1.write(var3);
         var1.finish();
      }

   }

   private void write_hIST() throws IOException, IIOException {
      if (this.metadata.hIST_present) {
         ChunkStream var1 = new ChunkStream(1749635924, this.stream);
         if (!this.metadata.PLTE_present) {
            throw new IIOException("hIST chunk without PLTE chunk!");
         }

         var1.writeChars(this.metadata.hIST_histogram, 0, this.metadata.hIST_histogram.length);
         var1.finish();
      }

   }

   private void write_tRNS() throws IOException, IIOException {
      if (this.metadata.tRNS_present) {
         ChunkStream var1 = new ChunkStream(1951551059, this.stream);
         int var2 = this.metadata.IHDR_colorType;
         int var3 = this.metadata.tRNS_colorType;
         int var4 = this.metadata.tRNS_red;
         int var5 = this.metadata.tRNS_green;
         int var6 = this.metadata.tRNS_blue;
         if (var2 == 2 && var3 == 0) {
            var3 = var2;
            var4 = var5 = var6 = this.metadata.tRNS_gray;
         }

         if (var3 != var2) {
            this.processWarningOccurred(0, "tRNS metadata has incompatible color type.\nThe chunk will not be written.");
            return;
         }

         if (var2 == 3) {
            if (!this.metadata.PLTE_present) {
               throw new IIOException("tRNS chunk without PLTE chunk!");
            }

            var1.write(this.metadata.tRNS_alpha);
         } else if (var2 == 0) {
            var1.writeShort(this.metadata.tRNS_gray);
         } else {
            if (var2 != 2) {
               throw new IIOException("tRNS chunk for color type 4 or 6!");
            }

            var1.writeShort(var4);
            var1.writeShort(var5);
            var1.writeShort(var6);
         }

         var1.finish();
      }

   }

   private void write_bKGD() throws IOException {
      if (this.metadata.bKGD_present) {
         ChunkStream var1 = new ChunkStream(1649100612, this.stream);
         int var2 = this.metadata.IHDR_colorType & 3;
         int var3 = this.metadata.bKGD_colorType;
         int var4 = this.metadata.bKGD_red;
         int var5 = this.metadata.bKGD_red;
         int var6 = this.metadata.bKGD_red;
         if (var2 == 2 && var3 == 0) {
            var3 = var2;
            var4 = var5 = var6 = this.metadata.bKGD_gray;
         }

         if (var3 != var2) {
            this.processWarningOccurred(0, "bKGD metadata has incompatible color type.\nThe chunk will not be written.");
            return;
         }

         if (var2 == 3) {
            var1.writeByte(this.metadata.bKGD_index);
         } else if (var2 != 0 && var2 != 4) {
            var1.writeShort(var4);
            var1.writeShort(var5);
            var1.writeShort(var6);
         } else {
            var1.writeShort(this.metadata.bKGD_gray);
         }

         var1.finish();
      }

   }

   private void write_pHYs() throws IOException {
      if (this.metadata.pHYs_present) {
         ChunkStream var1 = new ChunkStream(1883789683, this.stream);
         var1.writeInt(this.metadata.pHYs_pixelsPerUnitXAxis);
         var1.writeInt(this.metadata.pHYs_pixelsPerUnitYAxis);
         var1.writeByte(this.metadata.pHYs_unitSpecifier);
         var1.finish();
      }

   }

   private void write_sPLT() throws IOException {
      if (this.metadata.sPLT_present) {
         ChunkStream var1 = new ChunkStream(1934642260, this.stream);
         var1.writeBytes(this.metadata.sPLT_paletteName);
         var1.writeByte(0);
         var1.writeByte(this.metadata.sPLT_sampleDepth);
         int var2 = this.metadata.sPLT_red.length;
         int var3;
         if (this.metadata.sPLT_sampleDepth == 8) {
            for(var3 = 0; var3 < var2; ++var3) {
               var1.writeByte(this.metadata.sPLT_red[var3]);
               var1.writeByte(this.metadata.sPLT_green[var3]);
               var1.writeByte(this.metadata.sPLT_blue[var3]);
               var1.writeByte(this.metadata.sPLT_alpha[var3]);
               var1.writeShort(this.metadata.sPLT_frequency[var3]);
            }
         } else {
            for(var3 = 0; var3 < var2; ++var3) {
               var1.writeShort(this.metadata.sPLT_red[var3]);
               var1.writeShort(this.metadata.sPLT_green[var3]);
               var1.writeShort(this.metadata.sPLT_blue[var3]);
               var1.writeShort(this.metadata.sPLT_alpha[var3]);
               var1.writeShort(this.metadata.sPLT_frequency[var3]);
            }
         }

         var1.finish();
      }

   }

   private void write_tIME() throws IOException {
      if (this.metadata.tIME_present) {
         ChunkStream var1 = new ChunkStream(1950960965, this.stream);
         var1.writeShort(this.metadata.tIME_year);
         var1.writeByte(this.metadata.tIME_month);
         var1.writeByte(this.metadata.tIME_day);
         var1.writeByte(this.metadata.tIME_hour);
         var1.writeByte(this.metadata.tIME_minute);
         var1.writeByte(this.metadata.tIME_second);
         var1.finish();
      }

   }

   private void write_tEXt() throws IOException {
      Iterator var1 = this.metadata.tEXt_keyword.iterator();
      Iterator var2 = this.metadata.tEXt_text.iterator();

      while(var1.hasNext()) {
         ChunkStream var3 = new ChunkStream(1950701684, this.stream);
         String var4 = (String)var1.next();
         var3.writeBytes(var4);
         var3.writeByte(0);
         String var5 = (String)var2.next();
         var3.writeBytes(var5);
         var3.finish();
      }

   }

   private byte[] deflate(byte[] var1) throws IOException {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream();
      DeflaterOutputStream var3 = new DeflaterOutputStream(var2);
      var3.write(var1);
      var3.close();
      return var2.toByteArray();
   }

   private void write_iTXt() throws IOException {
      Iterator var1 = this.metadata.iTXt_keyword.iterator();
      Iterator var2 = this.metadata.iTXt_compressionFlag.iterator();
      Iterator var3 = this.metadata.iTXt_compressionMethod.iterator();
      Iterator var4 = this.metadata.iTXt_languageTag.iterator();
      Iterator var5 = this.metadata.iTXt_translatedKeyword.iterator();

      ChunkStream var7;
      for(Iterator var6 = this.metadata.iTXt_text.iterator(); var1.hasNext(); var7.finish()) {
         var7 = new ChunkStream(1767135348, this.stream);
         var7.writeBytes((String)var1.next());
         var7.writeByte(0);
         Boolean var8 = (Boolean)var2.next();
         var7.writeByte(var8 ? 1 : 0);
         var7.writeByte((Integer)var3.next());
         var7.writeBytes((String)var4.next());
         var7.writeByte(0);
         var7.write(((String)var5.next()).getBytes("UTF8"));
         var7.writeByte(0);
         String var9 = (String)var6.next();
         if (var8) {
            var7.write(this.deflate(var9.getBytes("UTF8")));
         } else {
            var7.write(var9.getBytes("UTF8"));
         }
      }

   }

   private void write_zTXt() throws IOException {
      Iterator var1 = this.metadata.zTXt_keyword.iterator();
      Iterator var2 = this.metadata.zTXt_compressionMethod.iterator();
      Iterator var3 = this.metadata.zTXt_text.iterator();

      while(var1.hasNext()) {
         ChunkStream var4 = new ChunkStream(2052348020, this.stream);
         String var5 = (String)var1.next();
         var4.writeBytes(var5);
         var4.writeByte(0);
         int var6 = (Integer)var2.next();
         var4.writeByte(var6);
         String var7 = (String)var3.next();
         var4.write(this.deflate(var7.getBytes("ISO-8859-1")));
         var4.finish();
      }

   }

   private void writeUnknownChunks() throws IOException {
      Iterator var1 = this.metadata.unknownChunkType.iterator();
      Iterator var2 = this.metadata.unknownChunkData.iterator();

      while(var1.hasNext() && var2.hasNext()) {
         String var3 = (String)var1.next();
         ChunkStream var4 = new ChunkStream(chunkType(var3), this.stream);
         byte[] var5 = (byte[])((byte[])var2.next());
         var4.write(var5);
         var4.finish();
      }

   }

   private static int chunkType(String var0) {
      char var1 = var0.charAt(0);
      char var2 = var0.charAt(1);
      char var3 = var0.charAt(2);
      char var4 = var0.charAt(3);
      int var5 = var1 << 24 | var2 << 16 | var3 << 8 | var4;
      return var5;
   }

   private void encodePass(ImageOutputStream var1, RenderedImage var2, int var3, int var4, int var5, int var6) throws IOException {
      int var7 = this.sourceXOffset;
      int var8 = this.sourceYOffset;
      int var9 = this.sourceWidth;
      int var10 = this.sourceHeight;
      var3 *= this.periodX;
      var5 *= this.periodX;
      var4 *= this.periodY;
      var6 *= this.periodY;
      int var11 = (var9 - var3 + var5 - 1) / var5;
      int var12 = (var10 - var4 + var6 - 1) / var6;
      if (var11 != 0 && var12 != 0) {
         var3 *= this.numBands;
         var5 *= this.numBands;
         int var13 = 8 / this.metadata.IHDR_bitDepth;
         int var14 = var9 * this.numBands;
         int[] var15 = new int[var14];
         int var16 = var11 * this.numBands;
         if (this.metadata.IHDR_bitDepth < 8) {
            var16 = (var16 + var13 - 1) / var13;
         } else if (this.metadata.IHDR_bitDepth == 16) {
            var16 *= 2;
         }

         IndexColorModel var17 = null;
         if (this.metadata.IHDR_colorType == 4 && var2.getColorModel() instanceof IndexColorModel) {
            var16 *= 2;
            var17 = (IndexColorModel)var2.getColorModel();
         }

         this.currRow = new byte[var16 + this.bpp];
         this.prevRow = new byte[var16 + this.bpp];
         this.filteredRows = new byte[5][var16 + this.bpp];
         int var18 = this.metadata.IHDR_bitDepth;

         for(int var19 = var8 + var4; var19 < var8 + var10; var19 += var6) {
            Rectangle var20 = new Rectangle(var7, var19, var9, 1);
            Raster var21 = var2.getData(var20);
            if (this.sourceBands != null) {
               var21 = var21.createChild(var7, var19, var9, 1, var7, var19, this.sourceBands);
            }

            var21.getPixels(var7, var19, var9, 1, (int[])var15);
            if (var2.getColorModel().isAlphaPremultiplied()) {
               WritableRaster var22 = var21.createCompatibleWritableRaster();
               var22.setPixels(var22.getMinX(), var22.getMinY(), var22.getWidth(), var22.getHeight(), var15);
               var2.getColorModel().coerceData(var22, false);
               var22.getPixels(var22.getMinX(), var22.getMinY(), var22.getWidth(), var22.getHeight(), var15);
            }

            int[] var29 = this.metadata.PLTE_order;
            int var23;
            if (var29 != null) {
               for(var23 = 0; var23 < var14; ++var23) {
                  var15[var23] = var29[var15[var23]];
               }
            }

            int var26;
            var23 = this.bpp;
            int var24 = 0;
            int var25 = 0;
            int var27;
            int var28;
            label109:
            switch(var18) {
            case 1:
            case 2:
            case 4:
               var26 = var13 - 1;
               var27 = var3;

               for(; var27 < var14; var27 += var5) {
                  byte var30 = this.scale0[var15[var27]];
                  var25 = var25 << var18 | var30;
                  if ((var24++ & var26) == var26) {
                     this.currRow[var23++] = (byte)var25;
                     var25 = 0;
                     var24 = 0;
                  }
               }

               if ((var24 & var26) != 0) {
                  var25 <<= (8 / var18 - var24) * var18;
                  this.currRow[var23++] = (byte)var25;
               }
               break;
            case 8:
               if (this.numBands == 1) {
                  var27 = var3;

                  while(true) {
                     if (var27 >= var14) {
                        break label109;
                     }

                     this.currRow[var23++] = this.scale0[var15[var27]];
                     if (var17 != null) {
                        this.currRow[var23++] = this.scale0[var17.getAlpha(255 & var15[var27])];
                     }

                     var27 += var5;
                  }
               } else {
                  var27 = var3;

                  while(true) {
                     if (var27 >= var14) {
                        break label109;
                     }

                     for(var28 = 0; var28 < this.numBands; ++var28) {
                        this.currRow[var23++] = this.scale[var28][var15[var27 + var28]];
                     }

                     var27 += var5;
                  }
               }
            case 16:
               for(var27 = var3; var27 < var14; var27 += var5) {
                  for(var28 = 0; var28 < this.numBands; ++var28) {
                     this.currRow[var23++] = this.scaleh[var28][var15[var27 + var28]];
                     this.currRow[var23++] = this.scalel[var28][var15[var27 + var28]];
                  }
               }
            }

            var26 = this.rowFilter.filterRow(this.metadata.IHDR_colorType, this.currRow, this.prevRow, this.filteredRows, var16, this.bpp);
            var1.write(var26);
            var1.write(this.filteredRows[var26], this.bpp, var16);
            byte[] var31 = this.currRow;
            this.currRow = this.prevRow;
            this.prevRow = var31;
            this.pixelsDone += var11;
            this.processImageProgress(100.0F * (float)this.pixelsDone / (float)this.totalPixels);
            if (this.abortRequested()) {
               return;
            }
         }

      }
   }

   private void write_IDAT(RenderedImage var1) throws IOException {
      IDATOutputStream var2 = new IDATOutputStream(this.stream, 32768);

      try {
         if (this.metadata.IHDR_interlaceMethod == 1) {
            for(int var3 = 0; var3 < 7; ++var3) {
               this.encodePass(var2, var1, PNGImageReader.adam7XOffset[var3], PNGImageReader.adam7YOffset[var3], PNGImageReader.adam7XSubsampling[var3], PNGImageReader.adam7YSubsampling[var3]);
               if (this.abortRequested()) {
                  break;
               }
            }
         } else {
            this.encodePass(var2, var1, 0, 0, 1, 1);
         }
      } finally {
         var2.finish();
      }

   }

   private void writeIEND() throws IOException {
      ChunkStream var1 = new ChunkStream(1229278788, this.stream);
      var1.finish();
   }

   private boolean equals(int[] var1, int[] var2) {
      if (var1 != null && var2 != null) {
         if (var1.length != var2.length) {
            return false;
         } else {
            for(int var3 = 0; var3 < var1.length; ++var3) {
               if (var1[var3] != var2[var3]) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private void initializeScaleTables(int[] var1) {
      int var2 = this.metadata.IHDR_bitDepth;
      if (var2 != this.scalingBitDepth || !this.equals(var1, this.sampleSize)) {
         this.sampleSize = var1;
         this.scalingBitDepth = var2;
         int var3 = (1 << var2) - 1;
         int var4;
         int var5;
         int var6;
         int var7;
         if (var2 <= 8) {
            this.scale = new byte[this.numBands][];

            for(var4 = 0; var4 < this.numBands; ++var4) {
               var5 = (1 << var1[var4]) - 1;
               var6 = var5 / 2;
               this.scale[var4] = new byte[var5 + 1];

               for(var7 = 0; var7 <= var5; ++var7) {
                  this.scale[var4][var7] = (byte)((var7 * var3 + var6) / var5);
               }
            }

            this.scale0 = this.scale[0];
            this.scaleh = this.scalel = (byte[][])null;
         } else {
            this.scaleh = new byte[this.numBands][];
            this.scalel = new byte[this.numBands][];

            for(var4 = 0; var4 < this.numBands; ++var4) {
               var5 = (1 << var1[var4]) - 1;
               var6 = var5 / 2;
               this.scaleh[var4] = new byte[var5 + 1];
               this.scalel[var4] = new byte[var5 + 1];

               for(var7 = 0; var7 <= var5; ++var7) {
                  int var8 = (var7 * var3 + var6) / var5;
                  this.scaleh[var4][var7] = (byte)(var8 >> 8);
                  this.scalel[var4][var7] = (byte)(var8 & 255);
               }
            }

            this.scale = (byte[][])null;
            this.scale0 = null;
         }

      }
   }

   public void write(IIOMetadata var1, IIOImage var2, ImageWriteParam var3) throws IIOException {
      if (this.stream == null) {
         throw new IllegalStateException("output == null!");
      } else if (var2 == null) {
         throw new IllegalArgumentException("image == null!");
      } else if (var2.hasRaster()) {
         throw new UnsupportedOperationException("image has a Raster!");
      } else {
         RenderedImage var4 = var2.getRenderedImage();
         SampleModel var5 = var4.getSampleModel();
         this.numBands = var5.getNumBands();
         this.sourceXOffset = var4.getMinX();
         this.sourceYOffset = var4.getMinY();
         this.sourceWidth = var4.getWidth();
         this.sourceHeight = var4.getHeight();
         this.sourceBands = null;
         this.periodX = 1;
         this.periodY = 1;
         int var12;
         if (var3 != null) {
            Rectangle var6 = var3.getSourceRegion();
            if (var6 != null) {
               Rectangle var7 = new Rectangle(var4.getMinX(), var4.getMinY(), var4.getWidth(), var4.getHeight());
               var6 = var6.intersection(var7);
               this.sourceXOffset = var6.x;
               this.sourceYOffset = var6.y;
               this.sourceWidth = var6.width;
               this.sourceHeight = var6.height;
            }

            var12 = var3.getSubsamplingXOffset();
            int var8 = var3.getSubsamplingYOffset();
            this.sourceXOffset += var12;
            this.sourceYOffset += var8;
            this.sourceWidth -= var12;
            this.sourceHeight -= var8;
            this.periodX = var3.getSourceXSubsampling();
            this.periodY = var3.getSourceYSubsampling();
            int[] var9 = var3.getSourceBands();
            if (var9 != null) {
               this.sourceBands = var9;
               this.numBands = this.sourceBands.length;
            }
         }

         int var11 = (this.sourceWidth + this.periodX - 1) / this.periodX;
         var12 = (this.sourceHeight + this.periodY - 1) / this.periodY;
         if (var11 > 0 && var12 > 0) {
            this.totalPixels = var11 * var12;
            this.pixelsDone = 0;
            IIOMetadata var13 = var2.getMetadata();
            if (var13 != null) {
               this.metadata = (PNGMetadata)this.convertImageMetadata(var13, ImageTypeSpecifier.createFromRenderedImage(var4), (ImageWriteParam)null);
            } else {
               this.metadata = new PNGMetadata();
            }

            if (var3 != null) {
               switch(var3.getProgressiveMode()) {
               case 0:
                  this.metadata.IHDR_interlaceMethod = 0;
                  break;
               case 1:
                  this.metadata.IHDR_interlaceMethod = 1;
               }
            }

            this.metadata.initialize(new ImageTypeSpecifier(var4), this.numBands);
            this.metadata.IHDR_width = var11;
            this.metadata.IHDR_height = var12;
            this.bpp = this.numBands * (this.metadata.IHDR_bitDepth == 16 ? 2 : 1);
            this.initializeScaleTables(var5.getSampleSize());
            this.clearAbortRequest();
            this.processImageStarted(0);

            try {
               this.write_magic();
               this.write_IHDR();
               this.write_cHRM();
               this.write_gAMA();
               this.write_iCCP();
               this.write_sBIT();
               this.write_sRGB();
               this.write_PLTE();
               this.write_hIST();
               this.write_tRNS();
               this.write_bKGD();
               this.write_pHYs();
               this.write_sPLT();
               this.write_tIME();
               this.write_tEXt();
               this.write_iTXt();
               this.write_zTXt();
               this.writeUnknownChunks();
               this.write_IDAT(var4);
               if (this.abortRequested()) {
                  this.processWriteAborted();
               } else {
                  this.writeIEND();
                  this.processImageComplete();
               }

            } catch (IOException var10) {
               throw new IIOException("I/O error writing PNG file!", var10);
            }
         } else {
            throw new IllegalArgumentException("Empty source region!");
         }
      }
   }
}
