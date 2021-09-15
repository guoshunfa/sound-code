package sun.awt.image;

import java.awt.Color;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class PNGImageDecoder extends ImageDecoder {
   private static final int GRAY = 0;
   private static final int PALETTE = 1;
   private static final int COLOR = 2;
   private static final int ALPHA = 4;
   private static final int bKGDChunk = 1649100612;
   private static final int cHRMChunk = 1665684045;
   private static final int gAMAChunk = 1732332865;
   private static final int hISTChunk = 1749635924;
   private static final int IDATChunk = 1229209940;
   private static final int IENDChunk = 1229278788;
   private static final int IHDRChunk = 1229472850;
   private static final int PLTEChunk = 1347179589;
   private static final int pHYsChunk = 1883789683;
   private static final int sBITChunk = 1933723988;
   private static final int tEXtChunk = 1950701684;
   private static final int tIMEChunk = 1950960965;
   private static final int tRNSChunk = 1951551059;
   private static final int zTXtChunk = 2052348020;
   private int width;
   private int height;
   private int bitDepth;
   private int colorType;
   private int compressionMethod;
   private int filterMethod;
   private int interlaceMethod;
   private int gamma = 100000;
   private Hashtable properties;
   private ColorModel cm;
   private byte[] red_map;
   private byte[] green_map;
   private byte[] blue_map;
   private byte[] alpha_map;
   private int transparentPixel = -1;
   private byte[] transparentPixel_16 = null;
   private static ColorModel[] greyModels = new ColorModel[4];
   private static final byte[] startingRow = new byte[]{0, 0, 0, 4, 0, 2, 0, 1};
   private static final byte[] startingCol = new byte[]{0, 0, 4, 0, 2, 0, 1, 0};
   private static final byte[] rowIncrement = new byte[]{1, 8, 8, 8, 4, 4, 2, 2};
   private static final byte[] colIncrement = new byte[]{1, 8, 8, 4, 4, 2, 2, 1};
   private static final byte[] blockHeight = new byte[]{1, 8, 8, 4, 4, 2, 2, 1};
   private static final byte[] blockWidth = new byte[]{1, 8, 4, 4, 2, 2, 1, 1};
   int pos;
   int limit;
   int chunkStart;
   int chunkKey;
   int chunkLength;
   int chunkCRC;
   boolean seenEOF;
   private static final byte[] signature = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10};
   PNGFilterInputStream inputStream;
   InputStream underlyingInputStream;
   byte[] inbuf = new byte[4096];
   private static boolean checkCRC = true;
   private static final int[] crc_table = new int[256];

   private void property(String var1, Object var2) {
      if (var2 != null) {
         if (this.properties == null) {
            this.properties = new Hashtable();
         }

         this.properties.put(var1, var2);
      }
   }

   private void property(String var1, float var2) {
      this.property(var1, new Float(var2));
   }

   private final void pngassert(boolean var1) throws IOException {
      if (!var1) {
         PNGImageDecoder.PNGException var2 = new PNGImageDecoder.PNGException("Broken file");
         var2.printStackTrace();
         throw var2;
      }
   }

   protected boolean handleChunk(int var1, byte[] var2, int var3, int var4) throws IOException {
      int var6;
      int var7;
      int var8;
      switch(var1) {
      case 1229209940:
         return false;
      case 1229278788:
      case 1749635924:
      case 1883789683:
      case 1933723988:
      case 2052348020:
      default:
         break;
      case 1229472850:
         if (var4 != 13 || (this.width = this.getInt(var3)) == 0 || (this.height = this.getInt(var3 + 4)) == 0) {
            throw new PNGImageDecoder.PNGException("bogus IHDR");
         }

         this.bitDepth = this.getByte(var3 + 8);
         this.colorType = this.getByte(var3 + 9);
         this.compressionMethod = this.getByte(var3 + 10);
         this.filterMethod = this.getByte(var3 + 11);
         this.interlaceMethod = this.getByte(var3 + 12);
         break;
      case 1347179589:
         var6 = var4 / 3;
         this.red_map = new byte[var6];
         this.green_map = new byte[var6];
         this.blue_map = new byte[var6];
         var7 = 0;

         for(var8 = var3; var7 < var6; var8 += 3) {
            this.red_map[var7] = var2[var8];
            this.green_map[var7] = var2[var8 + 1];
            this.blue_map[var7] = var2[var8 + 2];
            ++var7;
         }

         return true;
      case 1649100612:
         Color var5 = null;
         switch(this.colorType) {
         case 0:
         case 4:
            this.pngassert(var4 == 2);
            var7 = var2[var3] & 255;
            var5 = new Color(var7, var7, var7);
         case 1:
         case 5:
         default:
            break;
         case 2:
         case 6:
            this.pngassert(var4 == 6);
            var5 = new Color(var2[var3] & 255, var2[var3 + 2] & 255, var2[var3 + 4] & 255);
            break;
         case 3:
         case 7:
            this.pngassert(var4 == 1);
            var6 = var2[var3] & 255;
            this.pngassert(this.red_map != null && var6 < this.red_map.length);
            var5 = new Color(this.red_map[var6] & 255, this.green_map[var6] & 255, this.blue_map[var6] & 255);
         }

         if (var5 != null) {
            this.property("background", var5);
         }
         break;
      case 1665684045:
         this.property("chromaticities", new PNGImageDecoder.Chromaticities(this.getInt(var3), this.getInt(var3 + 4), this.getInt(var3 + 8), this.getInt(var3 + 12), this.getInt(var3 + 16), this.getInt(var3 + 20), this.getInt(var3 + 24), this.getInt(var3 + 28)));
         break;
      case 1732332865:
         if (var4 != 4) {
            throw new PNGImageDecoder.PNGException("bogus gAMA");
         }

         this.gamma = this.getInt(var3);
         if (this.gamma != 100000) {
            this.property("gamma", (float)this.gamma / 100000.0F);
         }
         break;
      case 1950701684:
         for(var6 = 0; var6 < var4 && var2[var3 + var6] != 0; ++var6) {
         }

         if (var6 < var4) {
            String var9 = new String(var2, var3, var6);
            String var10 = new String(var2, var3 + var6 + 1, var4 - var6 - 1);
            this.property(var9, var10);
         }
         break;
      case 1950960965:
         this.property("modtime", (new GregorianCalendar(this.getShort(var3 + 0), this.getByte(var3 + 2) - 1, this.getByte(var3 + 3), this.getByte(var3 + 4), this.getByte(var3 + 5), this.getByte(var3 + 6))).getTime());
         break;
      case 1951551059:
         switch(this.colorType) {
         case 0:
         case 4:
            this.pngassert(var4 == 2);
            var8 = this.getShort(var3);
            var8 = 255 & (this.bitDepth == 16 ? var8 >> 8 : var8);
            this.transparentPixel = var8 << 16 | var8 << 8 | var8;
         case 1:
         case 5:
         default:
            break;
         case 2:
         case 6:
            this.pngassert(var4 == 6);
            if (this.bitDepth == 16) {
               this.transparentPixel_16 = new byte[6];

               for(var8 = 0; var8 < 6; ++var8) {
                  this.transparentPixel_16[var8] = (byte)this.getByte(var3 + var8);
               }

               return true;
            } else {
               this.transparentPixel = (this.getShort(var3 + 0) & 255) << 16 | (this.getShort(var3 + 2) & 255) << 8 | this.getShort(var3 + 4) & 255;
               break;
            }
         case 3:
         case 7:
            var7 = var4;
            if (this.red_map != null) {
               var7 = this.red_map.length;
            }

            this.alpha_map = new byte[var7];
            System.arraycopy(var2, var3, this.alpha_map, 0, var4 < var7 ? var4 : var7);

            while(true) {
               --var7;
               if (var7 < var4) {
                  break;
               }

               this.alpha_map[var7] = -1;
            }
         }
      }

      return true;
   }

   public void produceImage() throws IOException, ImageFormatException {
      try {
         for(int var1 = 0; var1 < signature.length; ++var1) {
            if ((signature[var1] & 255) != this.underlyingInputStream.read()) {
               throw new PNGImageDecoder.PNGException("Chunk signature mismatch");
            }
         }

         BufferedInputStream var44 = new BufferedInputStream(new InflaterInputStream(this.inputStream, new Inflater()));
         this.getData();
         byte[] var2 = null;
         int[] var3 = null;
         int var4 = this.width;
         boolean var6 = false;
         byte var45;
         switch(this.bitDepth) {
         case 1:
            var45 = 0;
            break;
         case 2:
            var45 = 1;
            break;
         case 4:
            var45 = 2;
            break;
         case 8:
            var45 = 3;
            break;
         case 16:
            var45 = 4;
            break;
         default:
            throw new PNGImageDecoder.PNGException("invalid depth");
         }

         int var5;
         if (this.interlaceMethod != 0) {
            var4 *= this.height;
            var5 = this.width;
         } else {
            var5 = 0;
         }

         int var7 = this.colorType | this.bitDepth << 3;
         int var8 = (1 << (this.bitDepth >= 8 ? 8 : this.bitDepth)) - 1;
         int var10;
         int var12;
         switch(this.colorType) {
         case 0:
            byte var9 = var45 >= 4 ? 3 : var45;
            if ((this.cm = greyModels[var9]) == null) {
               var10 = 1 << (1 << var9);
               byte[] var11 = new byte[var10];

               for(var12 = 0; var12 < var10; ++var12) {
                  var11[var12] = (byte)(255 * var12 / (var10 - 1));
               }

               if (this.transparentPixel == -1) {
                  this.cm = new IndexColorModel(this.bitDepth, var11.length, var11, var11, var11);
               } else {
                  this.cm = new IndexColorModel(this.bitDepth, var11.length, var11, var11, var11, this.transparentPixel & 255);
               }

               greyModels[var9] = this.cm;
            }

            var2 = new byte[var4];
            break;
         case 1:
         case 5:
         default:
            throw new PNGImageDecoder.PNGException("invalid color type");
         case 2:
         case 4:
         case 6:
            this.cm = ColorModel.getRGBdefault();
            var3 = new int[var4];
            break;
         case 3:
         case 7:
            if (this.red_map == null) {
               throw new PNGImageDecoder.PNGException("palette expected");
            }

            if (this.alpha_map == null) {
               this.cm = new IndexColorModel(this.bitDepth, this.red_map.length, this.red_map, this.green_map, this.blue_map);
            } else {
               this.cm = new IndexColorModel(this.bitDepth, this.red_map.length, this.red_map, this.green_map, this.blue_map, this.alpha_map);
            }

            var2 = new byte[var4];
         }

         this.setDimensions(this.width, this.height);
         this.setColorModel(this.cm);
         int var46 = this.interlaceMethod != 0 ? 6 : 30;
         this.setHints(var46);
         this.headerComplete();
         var10 = (this.colorType & 1) != 0 ? 1 : ((this.colorType & 2) != 0 ? 3 : 1) + ((this.colorType & 4) != 0 ? 1 : 0);
         int var47 = var10 * this.bitDepth;
         var12 = var47 + 7 >> 3;
         int var13;
         byte var14;
         if (this.interlaceMethod == 0) {
            var13 = -1;
            var14 = 0;
         } else {
            var13 = 0;
            var14 = 7;
         }

         while(true) {
            int var15;
            byte var16;
            byte var17;
            byte var20;
            int var22;
            do {
               ++var13;
               if (var13 > var14) {
                  this.imageComplete(3, true);
                  return;
               }

               var15 = startingRow[var13];
               var16 = rowIncrement[var13];
               var17 = colIncrement[var13];
               byte var10000 = blockWidth[var13];
               var10000 = blockHeight[var13];
               var20 = startingCol[var13];
               int var21 = (this.width - var20 + (var17 - 1)) / var17;
               var22 = var21 * var47 + 7 >> 3;
            } while(var22 == 0);

            if (this.interlaceMethod == 0) {
               int var50 = var16 * this.width;
            } else {
               boolean var51 = false;
            }

            int var24 = var5 * var15;
            boolean var25 = true;
            byte[] var26 = new byte[var22];

            for(byte[] var27 = new byte[var22]; var15 < this.height; var25 = false) {
               int var28 = var44.read();

               int var29;
               int var30;
               for(var29 = 0; var29 < var22; var29 += var30) {
                  var30 = var44.read(var26, var29, var22 - var29);
                  if (var30 <= 0) {
                     throw new PNGImageDecoder.PNGException("missing data");
                  }
               }

               this.filterRow(var26, var25 ? null : var27, var28, var22, var12);
               var29 = var20;
               var30 = 0;

               for(boolean var31 = false; var29 < this.width; var29 += var17) {
                  if (var3 == null) {
                     switch(this.bitDepth) {
                     case 1:
                        var2[var29 + var24] = (byte)(var26[var30 >> 3] >> 7 - (var30 & 7) & 1);
                        ++var30;
                        break;
                     case 2:
                        var2[var29 + var24] = (byte)(var26[var30 >> 2] >> (3 - (var30 & 3)) * 2 & 3);
                        ++var30;
                        break;
                     case 4:
                        var2[var29 + var24] = (byte)(var26[var30 >> 1] >> (1 - (var30 & 1)) * 4 & 15);
                        ++var30;
                        break;
                     case 8:
                        var2[var29 + var24] = var26[var30++];
                        break;
                     case 16:
                        var2[var29 + var24] = var26[var30];
                        var30 += 2;
                        break;
                     default:
                        throw new PNGImageDecoder.PNGException("illegal type/depth");
                     }
                  } else {
                     int var33;
                     int var48;
                     switch(var7) {
                     case 66:
                        var48 = (var26[var30] & 255) << 16 | (var26[var30 + 1] & 255) << 8 | var26[var30 + 2] & 255;
                        if (var48 != this.transparentPixel) {
                           var48 |= -16777216;
                        }

                        var3[var29 + var24] = var48;
                        var30 += 3;
                        break;
                     case 68:
                        var33 = var26[var30] & 255;
                        var3[var29 + var24] = var33 << 16 | var33 << 8 | var33 | (var26[var30 + 1] & 255) << 24;
                        var30 += 2;
                        break;
                     case 70:
                        var3[var29 + var24] = (var26[var30] & 255) << 16 | (var26[var30 + 1] & 255) << 8 | var26[var30 + 2] & 255 | (var26[var30 + 3] & 255) << 24;
                        var30 += 4;
                        break;
                     case 130:
                        var48 = (var26[var30] & 255) << 16 | (var26[var30 + 2] & 255) << 8 | var26[var30 + 4] & 255;
                        boolean var32 = this.transparentPixel_16 != null;

                        for(var33 = 0; var32 && var33 < 6; ++var33) {
                           var32 &= (var26[var30 + var33] & 255) == (this.transparentPixel_16[var33] & 255);
                        }

                        if (!var32) {
                           var48 |= -16777216;
                        }

                        var3[var29 + var24] = var48;
                        var30 += 6;
                        break;
                     case 132:
                        var33 = var26[var30] & 255;
                        var3[var29 + var24] = var33 << 16 | var33 << 8 | var33 | (var26[var30 + 2] & 255) << 24;
                        var30 += 4;
                        break;
                     case 134:
                        var3[var29 + var24] = (var26[var30] & 255) << 16 | (var26[var30 + 2] & 255) << 8 | var26[var30 + 4] & 255 | (var26[var30 + 6] & 255) << 24;
                        var30 += 8;
                        break;
                     default:
                        throw new PNGImageDecoder.PNGException("illegal type/depth");
                     }
                  }
               }

               if (this.interlaceMethod == 0) {
                  if (var3 != null) {
                     this.sendPixels(0, var15, this.width, 1, (int[])var3, 0, this.width);
                  } else {
                     this.sendPixels(0, var15, this.width, 1, (byte[])var2, 0, this.width);
                  }
               }

               var15 += var16;
               var24 += var16 * var5;
               byte[] var49 = var26;
               var26 = var27;
               var27 = var49;
            }

            if (this.interlaceMethod != 0) {
               if (var3 != null) {
                  this.sendPixels(0, 0, this.width, this.height, (int[])var3, 0, this.width);
               } else {
                  this.sendPixels(0, 0, this.width, this.height, (byte[])var2, 0, this.width);
               }
            }
         }
      } catch (IOException var42) {
         if (!this.aborted) {
            this.property("error", var42);
            this.imageComplete(3, true);
            throw var42;
         }
      } finally {
         try {
            this.close();
         } catch (Throwable var41) {
         }

      }

   }

   private boolean sendPixels(int var1, int var2, int var3, int var4, int[] var5, int var6, int var7) {
      int var8 = this.setPixels(var1, var2, var3, var4, this.cm, var5, var6, var7);
      if (var8 <= 0) {
         this.aborted = true;
      }

      return !this.aborted;
   }

   private boolean sendPixels(int var1, int var2, int var3, int var4, byte[] var5, int var6, int var7) {
      int var8 = this.setPixels(var1, var2, var3, var4, this.cm, var5, var6, var7);
      if (var8 <= 0) {
         this.aborted = true;
      }

      return !this.aborted;
   }

   private void filterRow(byte[] var1, byte[] var2, int var3, int var4, int var5) throws IOException {
      int var6 = 0;
      switch(var3) {
      case 0:
         break;
      case 1:
         for(var6 = var5; var6 < var4; ++var6) {
            var1[var6] += var1[var6 - var5];
         }

         return;
      case 2:
         if (var2 != null) {
            while(var6 < var4) {
               var1[var6] += var2[var6];
               ++var6;
            }
         }
         break;
      case 3:
         if (var2 == null) {
            for(var6 = var5; var6 < var4; ++var6) {
               var1[var6] = (byte)(var1[var6] + ((var1[var6 - var5] & 255) >> 1));
            }

            return;
         } else {
            while(var6 < var5) {
               var1[var6] = (byte)(var1[var6] + ((255 & var2[var6]) >> 1));
               ++var6;
            }

            while(var6 < var4) {
               var1[var6] = (byte)(var1[var6] + ((var2[var6] & 255) + (var1[var6 - var5] & 255) >> 1));
               ++var6;
            }

            return;
         }
      case 4:
         if (var2 == null) {
            for(var6 = var5; var6 < var4; ++var6) {
               var1[var6] += var1[var6 - var5];
            }

            return;
         } else {
            while(var6 < var5) {
               var1[var6] += var2[var6];
               ++var6;
            }

            while(var6 < var4) {
               int var7 = var1[var6 - var5] & 255;
               int var8 = var2[var6] & 255;
               int var9 = var2[var6 - var5] & 255;
               int var10 = var7 + var8 - var9;
               int var11 = var10 > var7 ? var10 - var7 : var7 - var10;
               int var12 = var10 > var8 ? var10 - var8 : var8 - var10;
               int var13 = var10 > var9 ? var10 - var9 : var9 - var10;
               var1[var6] = (byte)(var1[var6] + (var11 <= var12 && var11 <= var13 ? var7 : (var12 <= var13 ? var8 : var9)));
               ++var6;
            }

            return;
         }
      default:
         throw new PNGImageDecoder.PNGException("Illegal filter");
      }

   }

   public PNGImageDecoder(InputStreamImageSource var1, InputStream var2) throws IOException {
      super(var1, var2);
      this.inputStream = new PNGFilterInputStream(this, var2);
      this.underlyingInputStream = this.inputStream.underlyingInputStream;
   }

   private void fill() throws IOException {
      if (!this.seenEOF) {
         if (this.pos > 0 && this.pos < this.limit) {
            System.arraycopy(this.inbuf, this.pos, this.inbuf, 0, this.limit - this.pos);
            this.limit -= this.pos;
            this.pos = 0;
         } else if (this.pos >= this.limit) {
            this.pos = 0;
            this.limit = 0;
         }

         int var2;
         for(int var1 = this.inbuf.length; this.limit < var1; this.limit += var2) {
            var2 = this.underlyingInputStream.read(this.inbuf, this.limit, var1 - this.limit);
            if (var2 <= 0) {
               this.seenEOF = true;
               break;
            }
         }
      }

   }

   private boolean need(int var1) throws IOException {
      if (this.limit - this.pos >= var1) {
         return true;
      } else {
         this.fill();
         if (this.limit - this.pos >= var1) {
            return true;
         } else if (this.seenEOF) {
            return false;
         } else {
            byte[] var2 = new byte[var1 + 100];
            System.arraycopy(this.inbuf, this.pos, var2, 0, this.limit - this.pos);
            this.limit -= this.pos;
            this.pos = 0;
            this.inbuf = var2;
            this.fill();
            return this.limit - this.pos >= var1;
         }
      }
   }

   private final int getInt(int var1) {
      return (this.inbuf[var1] & 255) << 24 | (this.inbuf[var1 + 1] & 255) << 16 | (this.inbuf[var1 + 2] & 255) << 8 | this.inbuf[var1 + 3] & 255;
   }

   private final int getShort(int var1) {
      return (short)((this.inbuf[var1] & 255) << 8 | this.inbuf[var1 + 1] & 255);
   }

   private final int getByte(int var1) {
      return this.inbuf[var1] & 255;
   }

   private final boolean getChunk() throws IOException {
      this.chunkLength = 0;
      if (!this.need(8)) {
         return false;
      } else {
         this.chunkLength = this.getInt(this.pos);
         this.chunkKey = this.getInt(this.pos + 4);
         if (this.chunkLength < 0) {
            throw new PNGImageDecoder.PNGException("bogus length: " + this.chunkLength);
         } else if (!this.need(this.chunkLength + 12)) {
            return false;
         } else {
            this.chunkCRC = this.getInt(this.pos + 8 + this.chunkLength);
            this.chunkStart = this.pos + 8;
            int var1 = crc(this.inbuf, this.pos + 4, this.chunkLength + 4);
            if (this.chunkCRC != var1 && checkCRC) {
               throw new PNGImageDecoder.PNGException("crc corruption");
            } else {
               this.pos += this.chunkLength + 12;
               return true;
            }
         }
      }
   }

   private void readAll() throws IOException {
      while(this.getChunk()) {
         this.handleChunk(this.chunkKey, this.inbuf, this.chunkStart, this.chunkLength);
      }

   }

   boolean getData() throws IOException {
      while(this.chunkLength == 0 && this.getChunk()) {
         if (this.handleChunk(this.chunkKey, this.inbuf, this.chunkStart, this.chunkLength)) {
            this.chunkLength = 0;
         }
      }

      return this.chunkLength > 0;
   }

   public static boolean getCheckCRC() {
      return checkCRC;
   }

   public static void setCheckCRC(boolean var0) {
      checkCRC = var0;
   }

   protected void wrc(int var1) {
      var1 &= 255;
      if (var1 <= 32 || var1 > 122) {
         var1 = 63;
      }

      System.out.write(var1);
   }

   protected void wrk(int var1) {
      this.wrc(var1 >> 24);
      this.wrc(var1 >> 16);
      this.wrc(var1 >> 8);
      this.wrc(var1);
   }

   public void print() {
      this.wrk(this.chunkKey);
      System.out.print(" " + this.chunkLength + "\n");
   }

   private static int update_crc(int var0, byte[] var1, int var2, int var3) {
      int var4 = var0;

      while(true) {
         --var3;
         if (var3 < 0) {
            return var4;
         }

         var4 = crc_table[(var4 ^ var1[var2++]) & 255] ^ var4 >>> 8;
      }
   }

   private static int crc(byte[] var0, int var1, int var2) {
      return ~update_crc(-1, var0, var1, var2);
   }

   static {
      for(int var0 = 0; var0 < 256; ++var0) {
         int var1 = var0;

         for(int var2 = 0; var2 < 8; ++var2) {
            if ((var1 & 1) != 0) {
               var1 = -306674912 ^ var1 >>> 1;
            } else {
               var1 >>>= 1;
            }
         }

         crc_table[var0] = var1;
      }

   }

   public static class Chromaticities {
      public float whiteX;
      public float whiteY;
      public float redX;
      public float redY;
      public float greenX;
      public float greenY;
      public float blueX;
      public float blueY;

      Chromaticities(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
         this.whiteX = (float)var1 / 100000.0F;
         this.whiteY = (float)var2 / 100000.0F;
         this.redX = (float)var3 / 100000.0F;
         this.redY = (float)var4 / 100000.0F;
         this.greenX = (float)var5 / 100000.0F;
         this.greenY = (float)var6 / 100000.0F;
         this.blueX = (float)var7 / 100000.0F;
         this.blueY = (float)var8 / 100000.0F;
      }

      public String toString() {
         return "Chromaticities(white=" + this.whiteX + "," + this.whiteY + ";red=" + this.redX + "," + this.redY + ";green=" + this.greenX + "," + this.greenY + ";blue=" + this.blueX + "," + this.blueY + ")";
      }
   }

   public class PNGException extends IOException {
      PNGException(String var2) {
         super(var2);
      }
   }
}
