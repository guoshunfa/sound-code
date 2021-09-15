package sun.awt.image;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

public class GifImageDecoder extends ImageDecoder {
   private static final boolean verbose = false;
   private static final int IMAGESEP = 44;
   private static final int EXBLOCK = 33;
   private static final int EX_GRAPHICS_CONTROL = 249;
   private static final int EX_COMMENT = 254;
   private static final int EX_APPLICATION = 255;
   private static final int TERMINATOR = 59;
   private static final int TRANSPARENCYMASK = 1;
   private static final int INTERLACEMASK = 64;
   private static final int COLORMAPMASK = 128;
   int num_global_colors;
   byte[] global_colormap;
   int trans_pixel = -1;
   IndexColorModel global_model;
   Hashtable props = new Hashtable();
   byte[] saved_image;
   IndexColorModel saved_model;
   int global_width;
   int global_height;
   int global_bgpixel;
   GifFrame curframe;
   private static final int normalflags = 30;
   private static final int interlaceflags = 29;
   private short[] prefix = new short[4096];
   private byte[] suffix = new byte[4096];
   private byte[] outCode = new byte[4097];

   public GifImageDecoder(InputStreamImageSource var1, InputStream var2) {
      super(var1, var2);
   }

   private static void error(String var0) throws ImageFormatException {
      throw new ImageFormatException(var0);
   }

   private int readBytes(byte[] var1, int var2, int var3) {
      while(true) {
         if (var3 > 0) {
            try {
               int var4 = this.input.read(var1, var2, var3);
               if (var4 >= 0) {
                  var2 += var4;
                  var3 -= var4;
                  continue;
               }
            } catch (IOException var5) {
            }
         }

         return var3;
      }
   }

   private static final int ExtractByte(byte[] var0, int var1) {
      return var0[var1] & 255;
   }

   private static final int ExtractWord(byte[] var0, int var1) {
      return var0[var1] & 255 | (var0[var1 + 1] & 255) << 8;
   }

   public void produceImage() throws IOException, ImageFormatException {
      try {
         this.readHeader();
         int var1 = 0;
         int var2 = 0;
         int var3 = -1;
         int var4 = 0;
         int var5 = -1;
         boolean var6 = false;
         boolean var7 = false;

         label295:
         while(!this.aborted) {
            switch(this.input.read()) {
            case -1:
            default:
               if (var2 == 0) {
                  return;
               }
            case 59:
               if (var3 != 0 && var3-- < 0) {
                  this.imageComplete(3, true);
                  return;
               }

               try {
                  if (this.curframe != null) {
                     this.curframe.dispose();
                     this.curframe = null;
                  }

                  this.input.reset();
                  this.saved_image = null;
                  this.saved_model = null;
                  var2 = 0;
                  break;
               } catch (IOException var17) {
                  return;
               }
            case 33:
               int var8;
               switch(var8 = this.input.read()) {
               case -1:
                  return;
               case 249:
                  byte[] var9 = new byte[6];
                  if (this.readBytes(var9, 0, 6) != 0) {
                     return;
                  }

                  if (var9[0] != 4 || var9[5] != 0) {
                     return;
                  }

                  var5 = ExtractWord(var9, 2) * 10;
                  if (var5 > 0 && !var7) {
                     var7 = true;
                     ImageFetcher.startingAnimation();
                  }

                  var4 = var9[1] >> 2 & 7;
                  if ((var9[1] & 1) != 0) {
                     this.trans_pixel = ExtractByte(var9, 4);
                  } else {
                     this.trans_pixel = -1;
                  }
                  continue;
               case 254:
               case 255:
               default:
                  boolean var20 = false;
                  String var10 = "";

                  while(true) {
                     int var11 = this.input.read();
                     if (var11 <= 0) {
                        if (var8 == 254) {
                           this.props.put("comment", var10);
                        }

                        if (var20 && !var7) {
                           var7 = true;
                           ImageFetcher.startingAnimation();
                        }
                        continue label295;
                     }

                     byte[] var12 = new byte[var11];
                     if (this.readBytes(var12, 0, var11) != 0) {
                        return;
                     }

                     if (var8 == 254) {
                        var10 = var10 + new String(var12, 0);
                     } else if (var8 == 255) {
                        if (var20) {
                           if (var11 == 3 && var12[0] == 1) {
                              if (var6) {
                                 ExtractWord(var12, 1);
                              } else {
                                 var3 = ExtractWord(var12, 1);
                                 var6 = true;
                              }
                           } else {
                              var20 = false;
                           }
                        }

                        if ("NETSCAPE2.0".equals(new String(var12, 0))) {
                           var20 = true;
                        }
                     }
                  }
               }
            case 44:
               if (!var7) {
                  this.input.mark(0);
               }

               try {
                  if (!this.readImage(var1 == 0, var4, var5)) {
                     return;
                  }
               } catch (Exception var18) {
                  return;
               }

               ++var2;
               ++var1;
            }
         }

      } finally {
         this.close();
      }
   }

   private void readHeader() throws IOException, ImageFormatException {
      byte[] var1 = new byte[13];
      if (this.readBytes(var1, 0, 13) != 0) {
         throw new IOException();
      } else {
         if (var1[0] != 71 || var1[1] != 73 || var1[2] != 70) {
            error("not a GIF file.");
         }

         this.global_width = ExtractWord(var1, 6);
         this.global_height = ExtractWord(var1, 8);
         int var2 = ExtractByte(var1, 10);
         if ((var2 & 128) == 0) {
            this.num_global_colors = 2;
            this.global_bgpixel = 0;
            this.global_colormap = new byte[6];
            this.global_colormap[0] = this.global_colormap[1] = this.global_colormap[2] = 0;
            this.global_colormap[3] = this.global_colormap[4] = this.global_colormap[5] = -1;
         } else {
            this.num_global_colors = 1 << (var2 & 7) + 1;
            this.global_bgpixel = ExtractByte(var1, 11);
            if (var1[12] != 0) {
               this.props.put("aspectratio", "" + (double)(ExtractByte(var1, 12) + 15) / 64.0D);
            }

            this.global_colormap = new byte[this.num_global_colors * 3];
            if (this.readBytes(this.global_colormap, 0, this.num_global_colors * 3) != 0) {
               throw new IOException();
            }
         }

         this.input.mark(Integer.MAX_VALUE);
      }
   }

   private static native void initIDs();

   private native boolean parseImage(int var1, int var2, int var3, int var4, boolean var5, int var6, byte[] var7, byte[] var8, IndexColorModel var9);

   private int sendPixels(int var1, int var2, int var3, int var4, byte[] var5, ColorModel var6) {
      if (var2 < 0) {
         var4 += var2;
         var2 = 0;
      }

      if (var2 + var4 > this.global_height) {
         var4 = this.global_height - var2;
      }

      if (var4 <= 0) {
         return 1;
      } else {
         int var7;
         int var9;
         if (var1 < 0) {
            var7 = -var1;
            var3 += var1;
            var9 = 0;
         } else {
            var7 = 0;
            var9 = var1;
         }

         if (var9 + var3 > this.global_width) {
            var3 = this.global_width - var9;
         }

         if (var3 <= 0) {
            return 1;
         } else {
            int var8 = var7 + var3;
            int var10 = var2 * this.global_width + var9;
            boolean var11 = this.curframe.disposal_method == 1;
            int var12;
            if (this.trans_pixel >= 0 && !this.curframe.initialframe) {
               if (this.saved_image == null || !var6.equals(this.saved_model)) {
                  var12 = -1;
                  int var16 = 1;

                  for(int var14 = var7; var14 < var8; ++var10) {
                     byte var15 = var5[var14];
                     if ((var15 & 255) == this.trans_pixel) {
                        if (var12 >= 0) {
                           var16 = this.setPixels(var1 + var12, var2, var14 - var12, 1, var6, var5, var12, 0);
                           if (var16 == 0) {
                              break;
                           }
                        }

                        var12 = -1;
                     } else {
                        if (var12 < 0) {
                           var12 = var14;
                        }

                        if (var11) {
                           this.saved_image[var10] = var15;
                        }
                     }

                     ++var14;
                  }

                  if (var12 >= 0) {
                     var16 = this.setPixels(var1 + var12, var2, var8 - var12, 1, var6, var5, var12, 0);
                  }

                  return var16;
               }

               for(var12 = var7; var12 < var8; ++var10) {
                  byte var13 = var5[var12];
                  if ((var13 & 255) == this.trans_pixel) {
                     var5[var12] = this.saved_image[var10];
                  } else if (var11) {
                     this.saved_image[var10] = var13;
                  }

                  ++var12;
               }
            } else if (var11) {
               System.arraycopy(var5, var7, this.saved_image, var10, var3);
            }

            var12 = this.setPixels(var9, var2, var3, var4, var6, var5, var7, 0);
            return var12;
         }
      }
   }

   private boolean readImage(boolean var1, int var2, int var3) throws IOException {
      if (this.curframe != null && !this.curframe.dispose()) {
         this.abort();
         return false;
      } else {
         long var4 = 0L;
         byte[] var6 = new byte[259];
         if (this.readBytes(var6, 0, 10) != 0) {
            throw new IOException();
         } else {
            int var7 = ExtractWord(var6, 0);
            int var8 = ExtractWord(var6, 2);
            int var9 = ExtractWord(var6, 4);
            int var10 = ExtractWord(var6, 6);
            if (var9 == 0 && this.global_width != 0) {
               var9 = this.global_width - var7;
            }

            if (var10 == 0 && this.global_height != 0) {
               var10 = this.global_height - var8;
            }

            boolean var11 = (var6[8] & 64) != 0;
            IndexColorModel var12 = this.global_model;
            int var13;
            byte[] var14;
            if ((var6[8] & 128) != 0) {
               var13 = 1 << (var6[8] & 7) + 1;
               var14 = new byte[var13 * 3];
               var14[0] = var6[9];
               if (this.readBytes(var14, 1, var13 * 3 - 1) != 0) {
                  throw new IOException();
               }

               if (this.readBytes(var6, 9, 1) != 0) {
                  throw new IOException();
               }

               if (this.trans_pixel >= var13) {
                  var13 = this.trans_pixel + 1;
                  var14 = grow_colormap(var14, var13);
               }

               var12 = new IndexColorModel(8, var13, var14, 0, false, this.trans_pixel);
            } else if (var12 == null || this.trans_pixel != var12.getTransparentPixel()) {
               if (this.trans_pixel >= this.num_global_colors) {
                  this.num_global_colors = this.trans_pixel + 1;
                  this.global_colormap = grow_colormap(this.global_colormap, this.num_global_colors);
               }

               var12 = new IndexColorModel(8, this.num_global_colors, this.global_colormap, 0, false, this.trans_pixel);
               this.global_model = var12;
            }

            if (var1) {
               if (this.global_width == 0) {
                  this.global_width = var9;
               }

               if (this.global_height == 0) {
                  this.global_height = var10;
               }

               this.setDimensions(this.global_width, this.global_height);
               this.setProperties(this.props);
               this.setColorModel(var12);
               this.headerComplete();
            }

            int var15;
            if (var2 == 1 && this.saved_image == null) {
               this.saved_image = new byte[this.global_width * this.global_height];
               if (var10 < this.global_height && var12 != null) {
                  byte var17 = (byte)var12.getTransparentPixel();
                  if (var17 >= 0) {
                     var14 = new byte[this.global_width];

                     for(var15 = 0; var15 < this.global_width; ++var15) {
                        var14[var15] = var17;
                     }

                     this.setPixels(0, 0, this.global_width, var8, var12, var14, 0, 0);
                     this.setPixels(0, var8 + var10, this.global_width, this.global_height - var10 - var8, var12, var14, 0, 0);
                  }
               }
            }

            var13 = var11 ? 29 : 30;
            this.setHints(var13);
            this.curframe = new GifFrame(this, var2, var3, this.curframe == null, var12, var7, var8, var9, var10);
            var14 = new byte[var9];
            var15 = ExtractByte(var6, 9);
            if (var15 >= 12) {
               return false;
            } else {
               boolean var16 = this.parseImage(var7, var8, var9, var10, var11, var15, var6, var14, var12);
               if (!var16) {
                  this.abort();
               }

               return var16;
            }
         }
      }
   }

   public static byte[] grow_colormap(byte[] var0, int var1) {
      byte[] var2 = new byte[var1 * 3];
      System.arraycopy(var0, 0, var2, 0, var0.length);
      return var2;
   }

   static {
      NativeLibLoader.loadLibraries();
      initIDs();
   }
}
