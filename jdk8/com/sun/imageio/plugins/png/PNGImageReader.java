package com.sun.imageio.plugins.png;

import com.sun.imageio.plugins.common.ReaderUtil;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipException;
import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import sun.awt.image.ByteInterleavedRaster;

public class PNGImageReader extends ImageReader {
   static final int IHDR_TYPE = 1229472850;
   static final int PLTE_TYPE = 1347179589;
   static final int IDAT_TYPE = 1229209940;
   static final int IEND_TYPE = 1229278788;
   static final int bKGD_TYPE = 1649100612;
   static final int cHRM_TYPE = 1665684045;
   static final int gAMA_TYPE = 1732332865;
   static final int hIST_TYPE = 1749635924;
   static final int iCCP_TYPE = 1766015824;
   static final int iTXt_TYPE = 1767135348;
   static final int pHYs_TYPE = 1883789683;
   static final int sBIT_TYPE = 1933723988;
   static final int sPLT_TYPE = 1934642260;
   static final int sRGB_TYPE = 1934772034;
   static final int tEXt_TYPE = 1950701684;
   static final int tIME_TYPE = 1950960965;
   static final int tRNS_TYPE = 1951551059;
   static final int zTXt_TYPE = 2052348020;
   static final int PNG_COLOR_GRAY = 0;
   static final int PNG_COLOR_RGB = 2;
   static final int PNG_COLOR_PALETTE = 3;
   static final int PNG_COLOR_GRAY_ALPHA = 4;
   static final int PNG_COLOR_RGB_ALPHA = 6;
   static final int[] inputBandsForColorType = new int[]{1, -1, 3, 1, 2, -1, 4};
   static final int PNG_FILTER_NONE = 0;
   static final int PNG_FILTER_SUB = 1;
   static final int PNG_FILTER_UP = 2;
   static final int PNG_FILTER_AVERAGE = 3;
   static final int PNG_FILTER_PAETH = 4;
   static final int[] adam7XOffset = new int[]{0, 4, 0, 2, 0, 1, 0};
   static final int[] adam7YOffset = new int[]{0, 0, 4, 0, 2, 0, 1};
   static final int[] adam7XSubsampling = new int[]{8, 8, 4, 4, 2, 2, 1, 1};
   static final int[] adam7YSubsampling = new int[]{8, 8, 8, 4, 4, 2, 2, 1};
   private static final boolean debug = true;
   ImageInputStream stream = null;
   boolean gotHeader = false;
   boolean gotMetadata = false;
   ImageReadParam lastParam = null;
   long imageStartPosition = -1L;
   Rectangle sourceRegion = null;
   int sourceXSubsampling = -1;
   int sourceYSubsampling = -1;
   int sourceMinProgressivePass = 0;
   int sourceMaxProgressivePass = 6;
   int[] sourceBands = null;
   int[] destinationBands = null;
   Point destinationOffset = new Point(0, 0);
   PNGMetadata metadata = new PNGMetadata();
   DataInputStream pixelStream = null;
   BufferedImage theImage = null;
   int pixelsDone = 0;
   int totalPixels;
   private static final int[][] bandOffsets = new int[][]{null, {0}, {0, 1}, {0, 1, 2}, {0, 1, 2, 3}};

   public PNGImageReader(ImageReaderSpi var1) {
      super(var1);
   }

   public void setInput(Object var1, boolean var2, boolean var3) {
      super.setInput(var1, var2, var3);
      this.stream = (ImageInputStream)var1;
      this.resetStreamSettings();
   }

   private String readNullTerminatedString(String var1, int var2) throws IOException {
      ByteArrayOutputStream var3 = new ByteArrayOutputStream();
      int var5 = 0;

      int var4;
      while(var2 > var5++ && (var4 = this.stream.read()) != 0) {
         if (var4 == -1) {
            throw new EOFException();
         }

         var3.write(var4);
      }

      return new String(var3.toByteArray(), var1);
   }

   private void readHeader() throws IIOException {
      if (!this.gotHeader) {
         if (this.stream == null) {
            throw new IllegalStateException("Input source not set!");
         } else {
            try {
               byte[] var1 = new byte[8];
               this.stream.readFully(var1);
               if (var1[0] == -119 && var1[1] == 80 && var1[2] == 78 && var1[3] == 71 && var1[4] == 13 && var1[5] == 10 && var1[6] == 26 && var1[7] == 10) {
                  int var2 = this.stream.readInt();
                  if (var2 != 13) {
                     throw new IIOException("Bad length for IHDR chunk!");
                  } else {
                     int var3 = this.stream.readInt();
                     if (var3 != 1229472850) {
                        throw new IIOException("Bad type for IHDR chunk!");
                     } else {
                        this.metadata = new PNGMetadata();
                        int var4 = this.stream.readInt();
                        int var5 = this.stream.readInt();
                        this.stream.readFully((byte[])var1, 0, 5);
                        int var6 = var1[0] & 255;
                        int var7 = var1[1] & 255;
                        int var8 = var1[2] & 255;
                        int var9 = var1[3] & 255;
                        int var10 = var1[4] & 255;
                        this.stream.skipBytes(4);
                        this.stream.flushBefore(this.stream.getStreamPosition());
                        if (var4 == 0) {
                           throw new IIOException("Image width == 0!");
                        } else if (var5 == 0) {
                           throw new IIOException("Image height == 0!");
                        } else if (var6 != 1 && var6 != 2 && var6 != 4 && var6 != 8 && var6 != 16) {
                           throw new IIOException("Bit depth must be 1, 2, 4, 8, or 16!");
                        } else if (var7 != 0 && var7 != 2 && var7 != 3 && var7 != 4 && var7 != 6) {
                           throw new IIOException("Color type must be 0, 2, 3, 4, or 6!");
                        } else if (var7 == 3 && var6 == 16) {
                           throw new IIOException("Bad color type/bit depth combination!");
                        } else if ((var7 == 2 || var7 == 6 || var7 == 4) && var6 != 8 && var6 != 16) {
                           throw new IIOException("Bad color type/bit depth combination!");
                        } else if (var8 != 0) {
                           throw new IIOException("Unknown compression method (not 0)!");
                        } else if (var9 != 0) {
                           throw new IIOException("Unknown filter method (not 0)!");
                        } else if (var10 != 0 && var10 != 1) {
                           throw new IIOException("Unknown interlace method (not 0 or 1)!");
                        } else {
                           this.metadata.IHDR_present = true;
                           this.metadata.IHDR_width = var4;
                           this.metadata.IHDR_height = var5;
                           this.metadata.IHDR_bitDepth = var6;
                           this.metadata.IHDR_colorType = var7;
                           this.metadata.IHDR_compressionMethod = var8;
                           this.metadata.IHDR_filterMethod = var9;
                           this.metadata.IHDR_interlaceMethod = var10;
                           this.gotHeader = true;
                        }
                     }
                  }
               } else {
                  throw new IIOException("Bad PNG signature!");
               }
            } catch (IOException var11) {
               throw new IIOException("I/O error reading PNG header!", var11);
            }
         }
      }
   }

   private void parse_PLTE_chunk(int var1) throws IOException {
      if (this.metadata.PLTE_present) {
         this.processWarningOccurred("A PNG image may not contain more than one PLTE chunk.\nThe chunk wil be ignored.");
      } else if (this.metadata.IHDR_colorType != 0 && this.metadata.IHDR_colorType != 4) {
         byte[] var2 = new byte[var1];
         this.stream.readFully(var2);
         int var3 = var1 / 3;
         if (this.metadata.IHDR_colorType == 3) {
            int var4 = 1 << this.metadata.IHDR_bitDepth;
            if (var3 > var4) {
               this.processWarningOccurred("PLTE chunk contains too many entries for bit depth, ignoring extras.");
               var3 = var4;
            }

            var3 = Math.min(var3, var4);
         }

         short var7;
         if (var3 > 16) {
            var7 = 256;
         } else if (var3 > 4) {
            var7 = 16;
         } else if (var3 > 2) {
            var7 = 4;
         } else {
            var7 = 2;
         }

         this.metadata.PLTE_present = true;
         this.metadata.PLTE_red = new byte[var7];
         this.metadata.PLTE_green = new byte[var7];
         this.metadata.PLTE_blue = new byte[var7];
         int var5 = 0;

         for(int var6 = 0; var6 < var3; ++var6) {
            this.metadata.PLTE_red[var6] = var2[var5++];
            this.metadata.PLTE_green[var6] = var2[var5++];
            this.metadata.PLTE_blue[var6] = var2[var5++];
         }

      } else {
         this.processWarningOccurred("A PNG gray or gray alpha image cannot have a PLTE chunk.\nThe chunk wil be ignored.");
      }
   }

   private void parse_bKGD_chunk() throws IOException {
      if (this.metadata.IHDR_colorType == 3) {
         this.metadata.bKGD_colorType = 3;
         this.metadata.bKGD_index = this.stream.readUnsignedByte();
      } else if (this.metadata.IHDR_colorType != 0 && this.metadata.IHDR_colorType != 4) {
         this.metadata.bKGD_colorType = 2;
         this.metadata.bKGD_red = this.stream.readUnsignedShort();
         this.metadata.bKGD_green = this.stream.readUnsignedShort();
         this.metadata.bKGD_blue = this.stream.readUnsignedShort();
      } else {
         this.metadata.bKGD_colorType = 0;
         this.metadata.bKGD_gray = this.stream.readUnsignedShort();
      }

      this.metadata.bKGD_present = true;
   }

   private void parse_cHRM_chunk() throws IOException {
      this.metadata.cHRM_whitePointX = this.stream.readInt();
      this.metadata.cHRM_whitePointY = this.stream.readInt();
      this.metadata.cHRM_redX = this.stream.readInt();
      this.metadata.cHRM_redY = this.stream.readInt();
      this.metadata.cHRM_greenX = this.stream.readInt();
      this.metadata.cHRM_greenY = this.stream.readInt();
      this.metadata.cHRM_blueX = this.stream.readInt();
      this.metadata.cHRM_blueY = this.stream.readInt();
      this.metadata.cHRM_present = true;
   }

   private void parse_gAMA_chunk() throws IOException {
      int var1 = this.stream.readInt();
      this.metadata.gAMA_gamma = var1;
      this.metadata.gAMA_present = true;
   }

   private void parse_hIST_chunk(int var1) throws IOException, IIOException {
      if (!this.metadata.PLTE_present) {
         throw new IIOException("hIST chunk without prior PLTE chunk!");
      } else {
         this.metadata.hIST_histogram = new char[var1 / 2];
         this.stream.readFully((char[])this.metadata.hIST_histogram, 0, this.metadata.hIST_histogram.length);
         this.metadata.hIST_present = true;
      }
   }

   private void parse_iCCP_chunk(int var1) throws IOException {
      String var2 = this.readNullTerminatedString("ISO-8859-1", 80);
      this.metadata.iCCP_profileName = var2;
      this.metadata.iCCP_compressionMethod = this.stream.readUnsignedByte();
      byte[] var3 = new byte[var1 - var2.length() - 2];
      this.stream.readFully(var3);
      this.metadata.iCCP_compressedProfile = var3;
      this.metadata.iCCP_present = true;
   }

   private void parse_iTXt_chunk(int var1) throws IOException {
      long var2 = this.stream.getStreamPosition();
      String var4 = this.readNullTerminatedString("ISO-8859-1", 80);
      this.metadata.iTXt_keyword.add(var4);
      int var5 = this.stream.readUnsignedByte();
      this.metadata.iTXt_compressionFlag.add(var5 == 1);
      int var6 = this.stream.readUnsignedByte();
      this.metadata.iTXt_compressionMethod.add(var6);
      String var7 = this.readNullTerminatedString("UTF8", 80);
      this.metadata.iTXt_languageTag.add(var7);
      long var8 = this.stream.getStreamPosition();
      int var10 = (int)(var2 + (long)var1 - var8);
      String var11 = this.readNullTerminatedString("UTF8", var10);
      this.metadata.iTXt_translatedKeyword.add(var11);
      var8 = this.stream.getStreamPosition();
      byte[] var13 = new byte[(int)(var2 + (long)var1 - var8)];
      this.stream.readFully(var13);
      String var12;
      if (var5 == 1) {
         var12 = new String(inflate(var13), "UTF8");
      } else {
         var12 = new String(var13, "UTF8");
      }

      this.metadata.iTXt_text.add(var12);
   }

   private void parse_pHYs_chunk() throws IOException {
      this.metadata.pHYs_pixelsPerUnitXAxis = this.stream.readInt();
      this.metadata.pHYs_pixelsPerUnitYAxis = this.stream.readInt();
      this.metadata.pHYs_unitSpecifier = this.stream.readUnsignedByte();
      this.metadata.pHYs_present = true;
   }

   private void parse_sBIT_chunk() throws IOException {
      int var1 = this.metadata.IHDR_colorType;
      if (var1 != 0 && var1 != 4) {
         if (var1 == 2 || var1 == 3 || var1 == 6) {
            this.metadata.sBIT_redBits = this.stream.readUnsignedByte();
            this.metadata.sBIT_greenBits = this.stream.readUnsignedByte();
            this.metadata.sBIT_blueBits = this.stream.readUnsignedByte();
         }
      } else {
         this.metadata.sBIT_grayBits = this.stream.readUnsignedByte();
      }

      if (var1 == 4 || var1 == 6) {
         this.metadata.sBIT_alphaBits = this.stream.readUnsignedByte();
      }

      this.metadata.sBIT_colorType = var1;
      this.metadata.sBIT_present = true;
   }

   private void parse_sPLT_chunk(int var1) throws IOException, IIOException {
      this.metadata.sPLT_paletteName = this.readNullTerminatedString("ISO-8859-1", 80);
      var1 -= this.metadata.sPLT_paletteName.length() + 1;
      int var2 = this.stream.readUnsignedByte();
      this.metadata.sPLT_sampleDepth = var2;
      int var3 = var1 / (4 * (var2 / 8) + 2);
      this.metadata.sPLT_red = new int[var3];
      this.metadata.sPLT_green = new int[var3];
      this.metadata.sPLT_blue = new int[var3];
      this.metadata.sPLT_alpha = new int[var3];
      this.metadata.sPLT_frequency = new int[var3];
      int var4;
      if (var2 == 8) {
         for(var4 = 0; var4 < var3; ++var4) {
            this.metadata.sPLT_red[var4] = this.stream.readUnsignedByte();
            this.metadata.sPLT_green[var4] = this.stream.readUnsignedByte();
            this.metadata.sPLT_blue[var4] = this.stream.readUnsignedByte();
            this.metadata.sPLT_alpha[var4] = this.stream.readUnsignedByte();
            this.metadata.sPLT_frequency[var4] = this.stream.readUnsignedShort();
         }
      } else {
         if (var2 != 16) {
            throw new IIOException("sPLT sample depth not 8 or 16!");
         }

         for(var4 = 0; var4 < var3; ++var4) {
            this.metadata.sPLT_red[var4] = this.stream.readUnsignedShort();
            this.metadata.sPLT_green[var4] = this.stream.readUnsignedShort();
            this.metadata.sPLT_blue[var4] = this.stream.readUnsignedShort();
            this.metadata.sPLT_alpha[var4] = this.stream.readUnsignedShort();
            this.metadata.sPLT_frequency[var4] = this.stream.readUnsignedShort();
         }
      }

      this.metadata.sPLT_present = true;
   }

   private void parse_sRGB_chunk() throws IOException {
      this.metadata.sRGB_renderingIntent = this.stream.readUnsignedByte();
      this.metadata.sRGB_present = true;
   }

   private void parse_tEXt_chunk(int var1) throws IOException {
      String var2 = this.readNullTerminatedString("ISO-8859-1", 80);
      this.metadata.tEXt_keyword.add(var2);
      byte[] var3 = new byte[var1 - var2.length() - 1];
      this.stream.readFully(var3);
      this.metadata.tEXt_text.add(new String(var3, "ISO-8859-1"));
   }

   private void parse_tIME_chunk() throws IOException {
      this.metadata.tIME_year = this.stream.readUnsignedShort();
      this.metadata.tIME_month = this.stream.readUnsignedByte();
      this.metadata.tIME_day = this.stream.readUnsignedByte();
      this.metadata.tIME_hour = this.stream.readUnsignedByte();
      this.metadata.tIME_minute = this.stream.readUnsignedByte();
      this.metadata.tIME_second = this.stream.readUnsignedByte();
      this.metadata.tIME_present = true;
   }

   private void parse_tRNS_chunk(int var1) throws IOException {
      int var2 = this.metadata.IHDR_colorType;
      if (var2 == 3) {
         if (!this.metadata.PLTE_present) {
            this.processWarningOccurred("tRNS chunk without prior PLTE chunk, ignoring it.");
            return;
         }

         int var3 = this.metadata.PLTE_red.length;
         int var4 = var1;
         if (var1 > var3) {
            this.processWarningOccurred("tRNS chunk has more entries than prior PLTE chunk, ignoring extras.");
            var4 = var3;
         }

         this.metadata.tRNS_alpha = new byte[var4];
         this.metadata.tRNS_colorType = 3;
         this.stream.read(this.metadata.tRNS_alpha, 0, var4);
         this.stream.skipBytes(var1 - var4);
      } else if (var2 == 0) {
         if (var1 != 2) {
            this.processWarningOccurred("tRNS chunk for gray image must have length 2, ignoring chunk.");
            this.stream.skipBytes(var1);
            return;
         }

         this.metadata.tRNS_gray = this.stream.readUnsignedShort();
         this.metadata.tRNS_colorType = 0;
      } else {
         if (var2 != 2) {
            this.processWarningOccurred("Gray+Alpha and RGBS images may not have a tRNS chunk, ignoring it.");
            return;
         }

         if (var1 != 6) {
            this.processWarningOccurred("tRNS chunk for RGB image must have length 6, ignoring chunk.");
            this.stream.skipBytes(var1);
            return;
         }

         this.metadata.tRNS_red = this.stream.readUnsignedShort();
         this.metadata.tRNS_green = this.stream.readUnsignedShort();
         this.metadata.tRNS_blue = this.stream.readUnsignedShort();
         this.metadata.tRNS_colorType = 2;
      }

      this.metadata.tRNS_present = true;
   }

   private static byte[] inflate(byte[] var0) throws IOException {
      ByteArrayInputStream var1 = new ByteArrayInputStream(var0);
      InflaterInputStream var2 = new InflaterInputStream(var1);
      ByteArrayOutputStream var3 = new ByteArrayOutputStream();

      int var4;
      try {
         while((var4 = var2.read()) != -1) {
            var3.write(var4);
         }
      } finally {
         var2.close();
      }

      return var3.toByteArray();
   }

   private void parse_zTXt_chunk(int var1) throws IOException {
      String var2 = this.readNullTerminatedString("ISO-8859-1", 80);
      this.metadata.zTXt_keyword.add(var2);
      int var3 = this.stream.readUnsignedByte();
      this.metadata.zTXt_compressionMethod.add(new Integer(var3));
      byte[] var4 = new byte[var1 - var2.length() - 2];
      this.stream.readFully(var4);
      this.metadata.zTXt_text.add(new String(inflate(var4), "ISO-8859-1"));
   }

   private void readMetadata() throws IIOException {
      if (!this.gotMetadata) {
         this.readHeader();
         int var1 = this.metadata.IHDR_colorType;
         int var2;
         int var3;
         if (this.ignoreMetadata && var1 != 3) {
            try {
               while(true) {
                  var2 = this.stream.readInt();
                  var3 = this.stream.readInt();
                  if (var3 == 1229209940) {
                     this.stream.skipBytes(-8);
                     this.imageStartPosition = this.stream.getStreamPosition();
                     break;
                  }

                  this.stream.skipBytes(var2 + 4);
               }
            } catch (IOException var9) {
               throw new IIOException("Error skipping PNG metadata", var9);
            }

            this.gotMetadata = true;
         } else {
            try {
               label81:
               while(true) {
                  var2 = this.stream.readInt();
                  var3 = this.stream.readInt();
                  if (var2 < 0) {
                     throw new IIOException("Invalid chunk lenght " + var2);
                  }

                  int var4;
                  try {
                     this.stream.mark();
                     this.stream.seek(this.stream.getStreamPosition() + (long)var2);
                     var4 = this.stream.readInt();
                     this.stream.reset();
                  } catch (IOException var8) {
                     throw new IIOException("Invalid chunk length " + var2);
                  }

                  switch(var3) {
                  case 1229209940:
                     this.stream.skipBytes(-8);
                     this.imageStartPosition = this.stream.getStreamPosition();
                     break label81;
                  case 1347179589:
                     this.parse_PLTE_chunk(var2);
                     break;
                  case 1649100612:
                     this.parse_bKGD_chunk();
                     break;
                  case 1665684045:
                     this.parse_cHRM_chunk();
                     break;
                  case 1732332865:
                     this.parse_gAMA_chunk();
                     break;
                  case 1749635924:
                     this.parse_hIST_chunk(var2);
                     break;
                  case 1766015824:
                     this.parse_iCCP_chunk(var2);
                     break;
                  case 1767135348:
                     if (this.ignoreMetadata) {
                        this.stream.skipBytes(var2);
                     } else {
                        this.parse_iTXt_chunk(var2);
                     }
                     break;
                  case 1883789683:
                     this.parse_pHYs_chunk();
                     break;
                  case 1933723988:
                     this.parse_sBIT_chunk();
                     break;
                  case 1934642260:
                     this.parse_sPLT_chunk(var2);
                     break;
                  case 1934772034:
                     this.parse_sRGB_chunk();
                     break;
                  case 1950701684:
                     this.parse_tEXt_chunk(var2);
                     break;
                  case 1950960965:
                     this.parse_tIME_chunk();
                     break;
                  case 1951551059:
                     this.parse_tRNS_chunk(var2);
                     break;
                  case 2052348020:
                     if (this.ignoreMetadata) {
                        this.stream.skipBytes(var2);
                     } else {
                        this.parse_zTXt_chunk(var2);
                     }
                     break;
                  default:
                     byte[] var5 = new byte[var2];
                     this.stream.readFully(var5);
                     StringBuilder var6 = new StringBuilder(4);
                     var6.append((char)(var3 >>> 24));
                     var6.append((char)(var3 >> 16 & 255));
                     var6.append((char)(var3 >> 8 & 255));
                     var6.append((char)(var3 & 255));
                     int var7 = var3 >>> 28;
                     if (var7 == 0) {
                        this.processWarningOccurred("Encountered unknown chunk with critical bit set!");
                     }

                     this.metadata.unknownChunkType.add(var6.toString());
                     this.metadata.unknownChunkData.add(var5);
                  }

                  if (var4 != this.stream.readInt()) {
                     throw new IIOException("Failed to read a chunk of type " + var3);
                  }

                  this.stream.flushBefore(this.stream.getStreamPosition());
               }
            } catch (IOException var10) {
               throw new IIOException("Error reading PNG metadata", var10);
            }

            this.gotMetadata = true;
         }
      }
   }

   private static void decodeSubFilter(byte[] var0, int var1, int var2, int var3) {
      for(int var4 = var3; var4 < var2; ++var4) {
         int var5 = var0[var4 + var1] & 255;
         var5 += var0[var4 + var1 - var3] & 255;
         var0[var4 + var1] = (byte)var5;
      }

   }

   private static void decodeUpFilter(byte[] var0, int var1, byte[] var2, int var3, int var4) {
      for(int var5 = 0; var5 < var4; ++var5) {
         int var6 = var0[var5 + var1] & 255;
         int var7 = var2[var5 + var3] & 255;
         var0[var5 + var1] = (byte)(var6 + var7);
      }

   }

   private static void decodeAverageFilter(byte[] var0, int var1, byte[] var2, int var3, int var4, int var5) {
      int var6;
      int var8;
      int var9;
      for(var9 = 0; var9 < var5; ++var9) {
         var6 = var0[var9 + var1] & 255;
         var8 = var2[var9 + var3] & 255;
         var0[var9 + var1] = (byte)(var6 + var8 / 2);
      }

      for(var9 = var5; var9 < var4; ++var9) {
         var6 = var0[var9 + var1] & 255;
         int var7 = var0[var9 + var1 - var5] & 255;
         var8 = var2[var9 + var3] & 255;
         var0[var9 + var1] = (byte)(var6 + (var7 + var8) / 2);
      }

   }

   private static int paethPredictor(int var0, int var1, int var2) {
      int var3 = var0 + var1 - var2;
      int var4 = Math.abs(var3 - var0);
      int var5 = Math.abs(var3 - var1);
      int var6 = Math.abs(var3 - var2);
      if (var4 <= var5 && var4 <= var6) {
         return var0;
      } else {
         return var5 <= var6 ? var1 : var2;
      }
   }

   private static void decodePaethFilter(byte[] var0, int var1, byte[] var2, int var3, int var4, int var5) {
      int var6;
      int var8;
      int var10;
      for(var10 = 0; var10 < var5; ++var10) {
         var6 = var0[var10 + var1] & 255;
         var8 = var2[var10 + var3] & 255;
         var0[var10 + var1] = (byte)(var6 + var8);
      }

      for(var10 = var5; var10 < var4; ++var10) {
         var6 = var0[var10 + var1] & 255;
         int var7 = var0[var10 + var1 - var5] & 255;
         var8 = var2[var10 + var3] & 255;
         int var9 = var2[var10 + var3 - var5] & 255;
         var0[var10 + var1] = (byte)(var6 + paethPredictor(var7, var8, var9));
      }

   }

   private WritableRaster createRaster(int var1, int var2, int var3, int var4, int var5) {
      WritableRaster var7 = null;
      Point var8 = new Point(0, 0);
      DataBufferByte var6;
      if (var5 < 8 && var3 == 1) {
         var6 = new DataBufferByte(var2 * var4);
         var7 = Raster.createPackedRaster(var6, var1, var2, var5, var8);
      } else if (var5 <= 8) {
         var6 = new DataBufferByte(var2 * var4);
         var7 = Raster.createInterleavedRaster(var6, var1, var2, var4, var3, bandOffsets[var3], var8);
      } else {
         DataBufferUShort var9 = new DataBufferUShort(var2 * var4);
         var7 = Raster.createInterleavedRaster(var9, var1, var2, var4, var3, bandOffsets[var3], var8);
      }

      return var7;
   }

   private void skipPass(int var1, int var2) throws IOException, IIOException {
      if (var1 != 0 && var2 != 0) {
         int var3 = inputBandsForColorType[this.metadata.IHDR_colorType];
         int var4 = (var3 * var1 * this.metadata.IHDR_bitDepth + 7) / 8;

         for(int var5 = 0; var5 < var2; ++var5) {
            this.pixelStream.skipBytes(1 + var4);
            if (this.abortRequested()) {
               return;
            }
         }

      }
   }

   private void updateImageProgress(int var1) {
      this.pixelsDone += var1;
      this.processImageProgress(100.0F * (float)this.pixelsDone / (float)this.totalPixels);
   }

   private void decodePass(int var1, int var2, int var3, int var4, int var5, int var6, int var7) throws IOException {
      if (var6 != 0 && var7 != 0) {
         WritableRaster var8 = this.theImage.getWritableTile(0, 0);
         int var9 = var8.getMinX();
         int var10 = var9 + var8.getWidth() - 1;
         int var11 = var8.getMinY();
         int var12 = var11 + var8.getHeight() - 1;
         int[] var13 = ReaderUtil.computeUpdatedPixels(this.sourceRegion, this.destinationOffset, var9, var11, var10, var12, this.sourceXSubsampling, this.sourceYSubsampling, var2, var3, var6, var7, var4, var5);
         int var14 = var13[0];
         int var15 = var13[1];
         int var16 = var13[2];
         int var17 = var13[4];
         int var18 = var13[5];
         int var19 = this.metadata.IHDR_bitDepth;
         int var20 = inputBandsForColorType[this.metadata.IHDR_colorType];
         int var21 = var19 == 16 ? 2 : 1;
         var21 *= var20;
         int var22 = (var20 * var6 * var19 + 7) / 8;
         int var23 = var19 == 16 ? var22 / 2 : var22;
         int var24;
         if (var16 == 0) {
            for(var24 = 0; var24 < var7; ++var24) {
               this.updateImageProgress(var6);
               this.pixelStream.skipBytes(1 + var22);
            }

         } else {
            var24 = (var14 - this.destinationOffset.x) * this.sourceXSubsampling + this.sourceRegion.x;
            int var25 = (var24 - var2) / var4;
            int var26 = var17 * this.sourceXSubsampling / var4;
            byte[] var27 = null;
            short[] var28 = null;
            byte[] var29 = new byte[var22];
            byte[] var30 = new byte[var22];
            WritableRaster var31 = this.createRaster(var6, 1, var20, var23, var19);
            int[] var32 = var31.getPixel(0, 0, (int[])null);
            DataBuffer var33 = var31.getDataBuffer();
            int var34 = var33.getDataType();
            if (var34 == 0) {
               var27 = ((DataBufferByte)var33).getData();
            } else {
               var28 = ((DataBufferUShort)var33).getData();
            }

            this.processPassStarted(this.theImage, var1, this.sourceMinProgressivePass, this.sourceMaxProgressivePass, var14, var15, var17, var18, this.destinationBands);
            if (this.sourceBands != null) {
               var31 = var31.createWritableChild(0, 0, var31.getWidth(), 1, 0, 0, this.sourceBands);
            }

            if (this.destinationBands != null) {
               var8 = var8.createWritableChild(0, 0, var8.getWidth(), var8.getHeight(), 0, 0, this.destinationBands);
            }

            boolean var35 = false;
            int[] var36 = var8.getSampleModel().getSampleSize();
            int var37 = var36.length;

            for(int var38 = 0; var38 < var37; ++var38) {
               if (var36[var38] != var19) {
                  var35 = true;
                  break;
               }
            }

            int[][] var48 = (int[][])null;
            int var40;
            int var41;
            int var42;
            int var43;
            if (var35) {
               int var39 = (1 << var19) - 1;
               var40 = var39 / 2;
               var48 = new int[var37][];

               for(var41 = 0; var41 < var37; ++var41) {
                  var42 = (1 << var36[var41]) - 1;
                  var48[var41] = new int[var39 + 1];

                  for(var43 = 0; var43 <= var39; ++var43) {
                     var48[var41][var43] = (var43 * var42 + var40) / var39;
                  }
               }
            }

            boolean var49 = var26 == 1 && var17 == 1 && !var35 && var8 instanceof ByteInterleavedRaster;
            if (var49) {
               var31 = var31.createWritableChild(var25, 0, var16, 1, 0, 0, (int[])null);
            }

            for(var40 = 0; var40 < var7; ++var40) {
               this.updateImageProgress(var6);
               var41 = this.pixelStream.read();

               try {
                  byte[] var50 = var30;
                  var30 = var29;
                  var29 = var50;
                  this.pixelStream.readFully(var50, 0, var22);
               } catch (ZipException var47) {
                  throw var47;
               }

               switch(var41) {
               case 0:
                  break;
               case 1:
                  decodeSubFilter(var29, 0, var22, var21);
                  break;
               case 2:
                  decodeUpFilter(var29, 0, var30, 0, var22);
                  break;
               case 3:
                  decodeAverageFilter(var29, 0, var30, 0, var22, var21);
                  break;
               case 4:
                  decodePaethFilter(var29, 0, var30, 0, var22, var21);
                  break;
               default:
                  throw new IIOException("Unknown row filter type (= " + var41 + ")!");
               }

               if (var19 < 16) {
                  System.arraycopy(var29, 0, var27, 0, var22);
               } else {
                  var42 = 0;

                  for(var43 = 0; var43 < var23; ++var43) {
                     var28[var43] = (short)(var29[var42] << 8 | var29[var42 + 1] & 255);
                     var42 += 2;
                  }
               }

               var42 = var40 * var5 + var3;
               if (var42 >= this.sourceRegion.y && var42 < this.sourceRegion.y + this.sourceRegion.height && (var42 - this.sourceRegion.y) % this.sourceYSubsampling == 0) {
                  var43 = this.destinationOffset.y + (var42 - this.sourceRegion.y) / this.sourceYSubsampling;
                  if (var43 >= var11) {
                     if (var43 > var12) {
                        break;
                     }

                     if (var49) {
                        var8.setRect(var14, var43, var31);
                     } else {
                        int var44 = var25;

                        for(int var45 = var14; var45 < var14 + var16; var45 += var17) {
                           var31.getPixel(var44, 0, var32);
                           if (var35) {
                              for(int var46 = 0; var46 < var37; ++var46) {
                                 var32[var46] = var48[var46][var32[var46]];
                              }
                           }

                           var8.setPixel(var45, var43, var32);
                           var44 += var26;
                        }
                     }

                     this.processImageUpdate(this.theImage, var14, var43, var16, 1, var17, var18, this.destinationBands);
                     if (this.abortRequested()) {
                        return;
                     }
                  }
               }
            }

            this.processPassComplete(this.theImage);
         }
      }
   }

   private void decodeImage() throws IOException, IIOException {
      int var1 = this.metadata.IHDR_width;
      int var2 = this.metadata.IHDR_height;
      this.pixelsDone = 0;
      this.totalPixels = var1 * var2;
      this.clearAbortRequest();
      if (this.metadata.IHDR_interlaceMethod == 0) {
         this.decodePass(0, 0, 0, 1, 1, var1, var2);
      } else {
         for(int var3 = 0; var3 <= this.sourceMaxProgressivePass; ++var3) {
            int var4 = adam7XOffset[var3];
            int var5 = adam7YOffset[var3];
            int var6 = adam7XSubsampling[var3];
            int var7 = adam7YSubsampling[var3];
            int var8 = adam7XSubsampling[var3 + 1] - 1;
            int var9 = adam7YSubsampling[var3 + 1] - 1;
            if (var3 >= this.sourceMinProgressivePass) {
               this.decodePass(var3, var4, var5, var6, var7, (var1 + var8) / var6, (var2 + var9) / var7);
            } else {
               this.skipPass((var1 + var8) / var6, (var2 + var9) / var7);
            }

            if (this.abortRequested()) {
               return;
            }
         }
      }

   }

   private void readImage(ImageReadParam var1) throws IIOException {
      this.readMetadata();
      int var2 = this.metadata.IHDR_width;
      int var3 = this.metadata.IHDR_height;
      this.sourceXSubsampling = 1;
      this.sourceYSubsampling = 1;
      this.sourceMinProgressivePass = 0;
      this.sourceMaxProgressivePass = 6;
      this.sourceBands = null;
      this.destinationBands = null;
      this.destinationOffset = new Point(0, 0);
      if (var1 != null) {
         this.sourceXSubsampling = var1.getSourceXSubsampling();
         this.sourceYSubsampling = var1.getSourceYSubsampling();
         this.sourceMinProgressivePass = Math.max(var1.getSourceMinProgressivePass(), 0);
         this.sourceMaxProgressivePass = Math.min(var1.getSourceMaxProgressivePass(), 6);
         this.sourceBands = var1.getSourceBands();
         this.destinationBands = var1.getDestinationBands();
         this.destinationOffset = var1.getDestinationOffset();
      }

      Inflater var4 = null;

      try {
         this.stream.seek(this.imageStartPosition);
         PNGImageDataEnumeration var5 = new PNGImageDataEnumeration(this.stream);
         SequenceInputStream var6 = new SequenceInputStream(var5);
         var4 = new Inflater();
         InflaterInputStream var14 = new InflaterInputStream(var6, var4);
         BufferedInputStream var15 = new BufferedInputStream(var14);
         this.pixelStream = new DataInputStream(var15);
         this.theImage = getDestination(var1, this.getImageTypes(0), var2, var3);
         Rectangle var7 = new Rectangle(0, 0, 0, 0);
         this.sourceRegion = new Rectangle(0, 0, 0, 0);
         computeRegions(var1, var2, var3, this.theImage, this.sourceRegion, var7);
         this.destinationOffset.setLocation(var7.getLocation());
         int var8 = this.metadata.IHDR_colorType;
         checkReadParamBandSettings(var1, inputBandsForColorType[var8], this.theImage.getSampleModel().getNumBands());
         this.processImageStarted(0);
         this.decodeImage();
         if (this.abortRequested()) {
            this.processReadAborted();
         } else {
            this.processImageComplete();
         }
      } catch (IOException var12) {
         throw new IIOException("Error reading PNG image data", var12);
      } finally {
         if (var4 != null) {
            var4.end();
         }

      }

   }

   public int getNumImages(boolean var1) throws IIOException {
      if (this.stream == null) {
         throw new IllegalStateException("No input source set!");
      } else if (this.seekForwardOnly && var1) {
         throw new IllegalStateException("seekForwardOnly and allowSearch can't both be true!");
      } else {
         return 1;
      }
   }

   public int getWidth(int var1) throws IIOException {
      if (var1 != 0) {
         throw new IndexOutOfBoundsException("imageIndex != 0!");
      } else {
         this.readHeader();
         return this.metadata.IHDR_width;
      }
   }

   public int getHeight(int var1) throws IIOException {
      if (var1 != 0) {
         throw new IndexOutOfBoundsException("imageIndex != 0!");
      } else {
         this.readHeader();
         return this.metadata.IHDR_height;
      }
   }

   public Iterator<ImageTypeSpecifier> getImageTypes(int var1) throws IIOException {
      if (var1 != 0) {
         throw new IndexOutOfBoundsException("imageIndex != 0!");
      } else {
         this.readHeader();
         ArrayList var2 = new ArrayList(1);
         int var6 = this.metadata.IHDR_bitDepth;
         int var7 = this.metadata.IHDR_colorType;
         byte var8;
         if (var6 <= 8) {
            var8 = 0;
         } else {
            var8 = 1;
         }

         ColorSpace var3;
         int[] var5;
         switch(var7) {
         case 0:
            var2.add(ImageTypeSpecifier.createGrayscale(var6, var8, false));
         case 1:
         case 5:
         default:
            break;
         case 2:
            if (var6 == 8) {
               var2.add(ImageTypeSpecifier.createFromBufferedImageType(5));
               var2.add(ImageTypeSpecifier.createFromBufferedImageType(1));
               var2.add(ImageTypeSpecifier.createFromBufferedImageType(4));
            }

            var3 = ColorSpace.getInstance(1000);
            var5 = new int[]{0, 1, 2};
            var2.add(ImageTypeSpecifier.createInterleaved(var3, var5, var8, false, false));
            break;
         case 3:
            this.readMetadata();
            int var9 = 1 << var6;
            byte[] var10 = this.metadata.PLTE_red;
            byte[] var11 = this.metadata.PLTE_green;
            byte[] var12 = this.metadata.PLTE_blue;
            if (this.metadata.PLTE_red.length < var9) {
               var10 = Arrays.copyOf(this.metadata.PLTE_red, var9);
               Arrays.fill(var10, this.metadata.PLTE_red.length, var9, this.metadata.PLTE_red[this.metadata.PLTE_red.length - 1]);
               var11 = Arrays.copyOf(this.metadata.PLTE_green, var9);
               Arrays.fill(var11, this.metadata.PLTE_green.length, var9, this.metadata.PLTE_green[this.metadata.PLTE_green.length - 1]);
               var12 = Arrays.copyOf(this.metadata.PLTE_blue, var9);
               Arrays.fill(var12, this.metadata.PLTE_blue.length, var9, this.metadata.PLTE_blue[this.metadata.PLTE_blue.length - 1]);
            }

            byte[] var13 = null;
            if (this.metadata.tRNS_present && this.metadata.tRNS_alpha != null) {
               if (this.metadata.tRNS_alpha.length == var10.length) {
                  var13 = this.metadata.tRNS_alpha;
               } else {
                  var13 = Arrays.copyOf(this.metadata.tRNS_alpha, var10.length);
                  Arrays.fill(var13, this.metadata.tRNS_alpha.length, var10.length, (byte)-1);
               }
            }

            var2.add(ImageTypeSpecifier.createIndexed(var10, var11, var12, var13, var6, 0));
            break;
         case 4:
            ColorSpace var4 = ColorSpace.getInstance(1003);
            var5 = new int[]{0, 1};
            var2.add(ImageTypeSpecifier.createInterleaved(var4, var5, var8, true, false));
            break;
         case 6:
            if (var6 == 8) {
               var2.add(ImageTypeSpecifier.createFromBufferedImageType(6));
               var2.add(ImageTypeSpecifier.createFromBufferedImageType(2));
            }

            var3 = ColorSpace.getInstance(1000);
            var5 = new int[]{0, 1, 2, 3};
            var2.add(ImageTypeSpecifier.createInterleaved(var3, var5, var8, true, false));
         }

         return var2.iterator();
      }
   }

   public ImageTypeSpecifier getRawImageType(int var1) throws IOException {
      Iterator var2 = this.getImageTypes(var1);
      ImageTypeSpecifier var3 = null;

      do {
         var3 = (ImageTypeSpecifier)var2.next();
      } while(var2.hasNext());

      return var3;
   }

   public ImageReadParam getDefaultReadParam() {
      return new ImageReadParam();
   }

   public IIOMetadata getStreamMetadata() throws IIOException {
      return null;
   }

   public IIOMetadata getImageMetadata(int var1) throws IIOException {
      if (var1 != 0) {
         throw new IndexOutOfBoundsException("imageIndex != 0!");
      } else {
         this.readMetadata();
         return this.metadata;
      }
   }

   public BufferedImage read(int var1, ImageReadParam var2) throws IIOException {
      if (var1 != 0) {
         throw new IndexOutOfBoundsException("imageIndex != 0!");
      } else {
         this.readImage(var2);
         return this.theImage;
      }
   }

   public void reset() {
      super.reset();
      this.resetStreamSettings();
   }

   private void resetStreamSettings() {
      this.gotHeader = false;
      this.gotMetadata = false;
      this.metadata = null;
      this.pixelStream = null;
   }
}
