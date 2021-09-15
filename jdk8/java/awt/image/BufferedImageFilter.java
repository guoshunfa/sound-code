package java.awt.image;

import java.awt.Point;
import java.util.Hashtable;

public class BufferedImageFilter extends ImageFilter implements Cloneable {
   BufferedImageOp bufferedImageOp;
   ColorModel model;
   int width;
   int height;
   byte[] bytePixels;
   int[] intPixels;

   public BufferedImageFilter(BufferedImageOp var1) {
      if (var1 == null) {
         throw new NullPointerException("Operation cannot be null");
      } else {
         this.bufferedImageOp = var1;
      }
   }

   public BufferedImageOp getBufferedImageOp() {
      return this.bufferedImageOp;
   }

   public void setDimensions(int var1, int var2) {
      if (var1 > 0 && var2 > 0) {
         this.width = var1;
         this.height = var2;
      } else {
         this.imageComplete(3);
      }
   }

   public void setColorModel(ColorModel var1) {
      this.model = var1;
   }

   private void convertToRGB() {
      int var1 = this.width * this.height;
      int[] var2 = new int[var1];
      int var3;
      if (this.bytePixels != null) {
         for(var3 = 0; var3 < var1; ++var3) {
            var2[var3] = this.model.getRGB(this.bytePixels[var3] & 255);
         }
      } else if (this.intPixels != null) {
         for(var3 = 0; var3 < var1; ++var3) {
            var2[var3] = this.model.getRGB(this.intPixels[var3]);
         }
      }

      this.bytePixels = null;
      this.intPixels = var2;
      this.model = ColorModel.getRGBdefault();
   }

   public void setPixels(int var1, int var2, int var3, int var4, ColorModel var5, byte[] var6, int var7, int var8) {
      if (var3 >= 0 && var4 >= 0) {
         if (var3 != 0 && var4 != 0) {
            int var9;
            if (var2 < 0) {
               var9 = -var2;
               if (var9 >= var4) {
                  return;
               }

               var7 += var8 * var9;
               var2 += var9;
               var4 -= var9;
            }

            if (var2 + var4 > this.height) {
               var4 = this.height - var2;
               if (var4 <= 0) {
                  return;
               }
            }

            if (var1 < 0) {
               var9 = -var1;
               if (var9 >= var3) {
                  return;
               }

               var7 += var9;
               var1 += var9;
               var3 -= var9;
            }

            if (var1 + var3 > this.width) {
               var3 = this.width - var1;
               if (var3 <= 0) {
                  return;
               }
            }

            var9 = var2 * this.width + var1;
            int var10;
            if (this.intPixels == null) {
               if (this.bytePixels == null) {
                  this.bytePixels = new byte[this.width * this.height];
                  this.model = var5;
               } else if (this.model != var5) {
                  this.convertToRGB();
               }

               if (this.bytePixels != null) {
                  for(var10 = var4; var10 > 0; --var10) {
                     System.arraycopy(var6, var7, this.bytePixels, var9, var3);
                     var7 += var8;
                     var9 += this.width;
                  }
               }
            }

            if (this.intPixels != null) {
               var10 = this.width - var3;
               int var11 = var8 - var3;

               for(int var12 = var4; var12 > 0; --var12) {
                  for(int var13 = var3; var13 > 0; --var13) {
                     this.intPixels[var9++] = var5.getRGB(var6[var7++] & 255);
                  }

                  var7 += var11;
                  var9 += var10;
               }
            }

         }
      } else {
         throw new IllegalArgumentException("Width (" + var3 + ") and height (" + var4 + ") must be > 0");
      }
   }

   public void setPixels(int var1, int var2, int var3, int var4, ColorModel var5, int[] var6, int var7, int var8) {
      if (var3 >= 0 && var4 >= 0) {
         if (var3 != 0 && var4 != 0) {
            int var9;
            if (var2 < 0) {
               var9 = -var2;
               if (var9 >= var4) {
                  return;
               }

               var7 += var8 * var9;
               var2 += var9;
               var4 -= var9;
            }

            if (var2 + var4 > this.height) {
               var4 = this.height - var2;
               if (var4 <= 0) {
                  return;
               }
            }

            if (var1 < 0) {
               var9 = -var1;
               if (var9 >= var3) {
                  return;
               }

               var7 += var9;
               var1 += var9;
               var3 -= var9;
            }

            if (var1 + var3 > this.width) {
               var3 = this.width - var1;
               if (var3 <= 0) {
                  return;
               }
            }

            if (this.intPixels == null) {
               if (this.bytePixels == null) {
                  this.intPixels = new int[this.width * this.height];
                  this.model = var5;
               } else {
                  this.convertToRGB();
               }
            }

            var9 = var2 * this.width + var1;
            int var10;
            if (this.model == var5) {
               for(var10 = var4; var10 > 0; --var10) {
                  System.arraycopy(var6, var7, this.intPixels, var9, var3);
                  var7 += var8;
                  var9 += this.width;
               }
            } else {
               if (this.model != ColorModel.getRGBdefault()) {
                  this.convertToRGB();
               }

               var10 = this.width - var3;
               int var11 = var8 - var3;

               for(int var12 = var4; var12 > 0; --var12) {
                  for(int var13 = var3; var13 > 0; --var13) {
                     this.intPixels[var9++] = var5.getRGB(var6[var7++]);
                  }

                  var7 += var11;
                  var9 += var10;
               }
            }

         }
      } else {
         throw new IllegalArgumentException("Width (" + var3 + ") and height (" + var4 + ") must be > 0");
      }
   }

   public void imageComplete(int var1) {
      switch(var1) {
      case 1:
      case 4:
         this.model = null;
         this.width = -1;
         this.height = -1;
         this.intPixels = null;
         this.bytePixels = null;
         break;
      case 2:
      case 3:
         if (this.width > 0 && this.height > 0) {
            label38: {
               WritableRaster var2;
               if (this.model instanceof DirectColorModel) {
                  if (this.intPixels == null) {
                     break label38;
                  }

                  var2 = this.createDCMraster();
               } else if (this.model instanceof IndexColorModel) {
                  int[] var3 = new int[]{0};
                  if (this.bytePixels == null) {
                     break label38;
                  }

                  DataBufferByte var4 = new DataBufferByte(this.bytePixels, this.width * this.height);
                  var2 = Raster.createInterleavedRaster(var4, this.width, this.height, this.width, 1, var3, (Point)null);
               } else {
                  this.convertToRGB();
                  if (this.intPixels == null) {
                     break label38;
                  }

                  var2 = this.createDCMraster();
               }

               BufferedImage var9 = new BufferedImage(this.model, var2, this.model.isAlphaPremultiplied(), (Hashtable)null);
               var9 = this.bufferedImageOp.filter(var9, (BufferedImage)null);
               WritableRaster var10 = var9.getRaster();
               ColorModel var5 = var9.getColorModel();
               int var6 = var10.getWidth();
               int var7 = var10.getHeight();
               this.consumer.setDimensions(var6, var7);
               this.consumer.setColorModel(var5);
               if (var5 instanceof DirectColorModel) {
                  DataBufferInt var8 = (DataBufferInt)var10.getDataBuffer();
                  this.consumer.setPixels(0, 0, var6, var7, var5, (int[])var8.getData(), 0, var6);
               } else {
                  if (!(var5 instanceof IndexColorModel)) {
                     throw new InternalError("Unknown color model " + var5);
                  }

                  DataBufferByte var11 = (DataBufferByte)var10.getDataBuffer();
                  this.consumer.setPixels(0, 0, var6, var7, var5, (byte[])var11.getData(), 0, var6);
               }
            }
         }
      }

      this.consumer.imageComplete(var1);
   }

   private final WritableRaster createDCMraster() {
      DirectColorModel var2 = (DirectColorModel)this.model;
      boolean var3 = this.model.hasAlpha();
      int[] var4 = new int[3 + (var3 ? 1 : 0)];
      var4[0] = var2.getRedMask();
      var4[1] = var2.getGreenMask();
      var4[2] = var2.getBlueMask();
      if (var3) {
         var4[3] = var2.getAlphaMask();
      }

      DataBufferInt var5 = new DataBufferInt(this.intPixels, this.width * this.height);
      WritableRaster var1 = Raster.createPackedRaster(var5, this.width, this.height, this.width, var4, (Point)null);
      return var1;
   }
}
