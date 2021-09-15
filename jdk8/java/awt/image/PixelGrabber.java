package java.awt.image;

import java.awt.Image;
import java.util.Hashtable;

public class PixelGrabber implements ImageConsumer {
   ImageProducer producer;
   int dstX;
   int dstY;
   int dstW;
   int dstH;
   ColorModel imageModel;
   byte[] bytePixels;
   int[] intPixels;
   int dstOff;
   int dstScan;
   private boolean grabbing;
   private int flags;
   private static final int GRABBEDBITS = 48;
   private static final int DONEBITS = 112;

   public PixelGrabber(Image var1, int var2, int var3, int var4, int var5, int[] var6, int var7, int var8) {
      this(var1.getSource(), var2, var3, var4, var5, var6, var7, var8);
   }

   public PixelGrabber(ImageProducer var1, int var2, int var3, int var4, int var5, int[] var6, int var7, int var8) {
      this.producer = var1;
      this.dstX = var2;
      this.dstY = var3;
      this.dstW = var4;
      this.dstH = var5;
      this.dstOff = var7;
      this.dstScan = var8;
      this.intPixels = var6;
      this.imageModel = ColorModel.getRGBdefault();
   }

   public PixelGrabber(Image var1, int var2, int var3, int var4, int var5, boolean var6) {
      this.producer = var1.getSource();
      this.dstX = var2;
      this.dstY = var3;
      this.dstW = var4;
      this.dstH = var5;
      if (var6) {
         this.imageModel = ColorModel.getRGBdefault();
      }

   }

   public synchronized void startGrabbing() {
      if ((this.flags & 112) == 0) {
         if (!this.grabbing) {
            this.grabbing = true;
            this.flags &= -129;
            this.producer.startProduction(this);
         }

      }
   }

   public synchronized void abortGrabbing() {
      this.imageComplete(4);
   }

   public boolean grabPixels() throws InterruptedException {
      return this.grabPixels(0L);
   }

   public synchronized boolean grabPixels(long var1) throws InterruptedException {
      if ((this.flags & 112) != 0) {
         return (this.flags & 48) != 0;
      } else {
         long var3 = var1 + System.currentTimeMillis();
         if (!this.grabbing) {
            this.grabbing = true;
            this.flags &= -129;
            this.producer.startProduction(this);
         }

         long var5;
         for(; this.grabbing; this.wait(var5)) {
            if (var1 == 0L) {
               var5 = 0L;
            } else {
               var5 = var3 - System.currentTimeMillis();
               if (var5 <= 0L) {
                  break;
               }
            }
         }

         return (this.flags & 48) != 0;
      }
   }

   public synchronized int getStatus() {
      return this.flags;
   }

   public synchronized int getWidth() {
      return this.dstW < 0 ? -1 : this.dstW;
   }

   public synchronized int getHeight() {
      return this.dstH < 0 ? -1 : this.dstH;
   }

   public synchronized Object getPixels() {
      return this.bytePixels == null ? this.intPixels : this.bytePixels;
   }

   public synchronized ColorModel getColorModel() {
      return this.imageModel;
   }

   public void setDimensions(int var1, int var2) {
      if (this.dstW < 0) {
         this.dstW = var1 - this.dstX;
      }

      if (this.dstH < 0) {
         this.dstH = var2 - this.dstY;
      }

      if (this.dstW > 0 && this.dstH > 0) {
         if (this.intPixels == null && this.imageModel == ColorModel.getRGBdefault()) {
            this.intPixels = new int[this.dstW * this.dstH];
            this.dstScan = this.dstW;
            this.dstOff = 0;
         }
      } else {
         this.imageComplete(3);
      }

      this.flags |= 3;
   }

   public void setHints(int var1) {
   }

   public void setProperties(Hashtable<?, ?> var1) {
   }

   public void setColorModel(ColorModel var1) {
   }

   private void convertToRGB() {
      int var1 = this.dstW * this.dstH;
      int[] var2 = new int[var1];
      int var3;
      if (this.bytePixels != null) {
         for(var3 = 0; var3 < var1; ++var3) {
            var2[var3] = this.imageModel.getRGB(this.bytePixels[var3] & 255);
         }
      } else if (this.intPixels != null) {
         for(var3 = 0; var3 < var1; ++var3) {
            var2[var3] = this.imageModel.getRGB(this.intPixels[var3]);
         }
      }

      this.bytePixels = null;
      this.intPixels = var2;
      this.dstScan = this.dstW;
      this.dstOff = 0;
      this.imageModel = ColorModel.getRGBdefault();
   }

   public void setPixels(int var1, int var2, int var3, int var4, ColorModel var5, byte[] var6, int var7, int var8) {
      int var9;
      if (var2 < this.dstY) {
         var9 = this.dstY - var2;
         if (var9 >= var4) {
            return;
         }

         var7 += var8 * var9;
         var2 += var9;
         var4 -= var9;
      }

      if (var2 + var4 > this.dstY + this.dstH) {
         var4 = this.dstY + this.dstH - var2;
         if (var4 <= 0) {
            return;
         }
      }

      if (var1 < this.dstX) {
         var9 = this.dstX - var1;
         if (var9 >= var3) {
            return;
         }

         var7 += var9;
         var1 += var9;
         var3 -= var9;
      }

      if (var1 + var3 > this.dstX + this.dstW) {
         var3 = this.dstX + this.dstW - var1;
         if (var3 <= 0) {
            return;
         }
      }

      var9 = this.dstOff + (var2 - this.dstY) * this.dstScan + (var1 - this.dstX);
      int var10;
      if (this.intPixels == null) {
         if (this.bytePixels == null) {
            this.bytePixels = new byte[this.dstW * this.dstH];
            this.dstScan = this.dstW;
            this.dstOff = 0;
            this.imageModel = var5;
         } else if (this.imageModel != var5) {
            this.convertToRGB();
         }

         if (this.bytePixels != null) {
            for(var10 = var4; var10 > 0; --var10) {
               System.arraycopy(var6, var7, this.bytePixels, var9, var3);
               var7 += var8;
               var9 += this.dstScan;
            }
         }
      }

      if (this.intPixels != null) {
         var10 = this.dstScan - var3;
         int var11 = var8 - var3;

         for(int var12 = var4; var12 > 0; --var12) {
            for(int var13 = var3; var13 > 0; --var13) {
               this.intPixels[var9++] = var5.getRGB(var6[var7++] & 255);
            }

            var7 += var11;
            var9 += var10;
         }
      }

      this.flags |= 8;
   }

   public void setPixels(int var1, int var2, int var3, int var4, ColorModel var5, int[] var6, int var7, int var8) {
      int var9;
      if (var2 < this.dstY) {
         var9 = this.dstY - var2;
         if (var9 >= var4) {
            return;
         }

         var7 += var8 * var9;
         var2 += var9;
         var4 -= var9;
      }

      if (var2 + var4 > this.dstY + this.dstH) {
         var4 = this.dstY + this.dstH - var2;
         if (var4 <= 0) {
            return;
         }
      }

      if (var1 < this.dstX) {
         var9 = this.dstX - var1;
         if (var9 >= var3) {
            return;
         }

         var7 += var9;
         var1 += var9;
         var3 -= var9;
      }

      if (var1 + var3 > this.dstX + this.dstW) {
         var3 = this.dstX + this.dstW - var1;
         if (var3 <= 0) {
            return;
         }
      }

      if (this.intPixels == null) {
         if (this.bytePixels == null) {
            this.intPixels = new int[this.dstW * this.dstH];
            this.dstScan = this.dstW;
            this.dstOff = 0;
            this.imageModel = var5;
         } else {
            this.convertToRGB();
         }
      }

      var9 = this.dstOff + (var2 - this.dstY) * this.dstScan + (var1 - this.dstX);
      int var10;
      if (this.imageModel == var5) {
         for(var10 = var4; var10 > 0; --var10) {
            System.arraycopy(var6, var7, this.intPixels, var9, var3);
            var7 += var8;
            var9 += this.dstScan;
         }
      } else {
         if (this.imageModel != ColorModel.getRGBdefault()) {
            this.convertToRGB();
         }

         var10 = this.dstScan - var3;
         int var11 = var8 - var3;

         for(int var12 = var4; var12 > 0; --var12) {
            for(int var13 = var3; var13 > 0; --var13) {
               this.intPixels[var9++] = var5.getRGB(var6[var7++]);
            }

            var7 += var11;
            var9 += var10;
         }
      }

      this.flags |= 8;
   }

   public synchronized void imageComplete(int var1) {
      this.grabbing = false;
      switch(var1) {
      case 1:
      default:
         this.flags |= 192;
         break;
      case 2:
         this.flags |= 16;
         break;
      case 3:
         this.flags |= 32;
         break;
      case 4:
         this.flags |= 128;
      }

      this.producer.removeConsumer(this);
      this.notifyAll();
   }

   public synchronized int status() {
      return this.flags;
   }
}
