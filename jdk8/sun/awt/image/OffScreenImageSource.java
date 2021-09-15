package sun.awt.image;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

public class OffScreenImageSource implements ImageProducer {
   BufferedImage image;
   int width;
   int height;
   Hashtable properties;
   private ImageConsumer theConsumer;

   public OffScreenImageSource(BufferedImage var1, Hashtable var2) {
      this.image = var1;
      if (var2 != null) {
         this.properties = var2;
      } else {
         this.properties = new Hashtable();
      }

      this.width = var1.getWidth();
      this.height = var1.getHeight();
   }

   public OffScreenImageSource(BufferedImage var1) {
      this(var1, (Hashtable)null);
   }

   public synchronized void addConsumer(ImageConsumer var1) {
      this.theConsumer = var1;
      this.produce();
   }

   public synchronized boolean isConsumer(ImageConsumer var1) {
      return var1 == this.theConsumer;
   }

   public synchronized void removeConsumer(ImageConsumer var1) {
      if (this.theConsumer == var1) {
         this.theConsumer = null;
      }

   }

   public void startProduction(ImageConsumer var1) {
      this.addConsumer(var1);
   }

   public void requestTopDownLeftRightResend(ImageConsumer var1) {
   }

   private void sendPixels() {
      ColorModel var1 = this.image.getColorModel();
      WritableRaster var2 = this.image.getRaster();
      int var3 = var2.getNumDataElements();
      int var4 = var2.getDataBuffer().getDataType();
      int[] var5 = new int[this.width * var3];
      boolean var6 = true;
      byte[] var7;
      int var8;
      int var9;
      if (var1 instanceof IndexColorModel) {
         var7 = new byte[this.width];
         this.theConsumer.setColorModel(var1);
         if (var2 instanceof ByteComponentRaster) {
            var6 = false;

            for(var8 = 0; var8 < this.height; ++var8) {
               var2.getDataElements(0, var8, this.width, 1, var7);
               this.theConsumer.setPixels(0, var8, this.width, 1, var1, (byte[])var7, 0, this.width);
            }
         } else if (var2 instanceof BytePackedRaster) {
            var6 = false;

            for(var8 = 0; var8 < this.height; ++var8) {
               var2.getPixels(0, var8, this.width, 1, var5);

               for(var9 = 0; var9 < this.width; ++var9) {
                  var7[var9] = (byte)var5[var9];
               }

               this.theConsumer.setPixels(0, var8, this.width, 1, var1, (byte[])var7, 0, this.width);
            }
         } else if (var4 == 2 || var4 == 3) {
            var6 = false;

            for(var8 = 0; var8 < this.height; ++var8) {
               var2.getPixels(0, var8, this.width, 1, var5);
               this.theConsumer.setPixels(0, var8, this.width, 1, var1, (int[])var5, 0, this.width);
            }
         }
      } else if (var1 instanceof DirectColorModel) {
         this.theConsumer.setColorModel(var1);
         var6 = false;
         label93:
         switch(var4) {
         case 0:
            var7 = new byte[this.width];
            var8 = 0;

            while(true) {
               if (var8 >= this.height) {
                  break label93;
               }

               var2.getDataElements(0, var8, this.width, 1, var7);

               for(var9 = 0; var9 < this.width; ++var9) {
                  var5[var9] = var7[var9] & 255;
               }

               this.theConsumer.setPixels(0, var8, this.width, 1, var1, (int[])var5, 0, this.width);
               ++var8;
            }
         case 1:
            short[] var13 = new short[this.width];
            var9 = 0;

            while(true) {
               if (var9 >= this.height) {
                  break label93;
               }

               var2.getDataElements(0, var9, this.width, 1, var13);

               for(int var10 = 0; var10 < this.width; ++var10) {
                  var5[var10] = var13[var10] & '\uffff';
               }

               this.theConsumer.setPixels(0, var9, this.width, 1, var1, (int[])var5, 0, this.width);
               ++var9;
            }
         case 2:
         default:
            var6 = true;
            break;
         case 3:
            for(int var11 = 0; var11 < this.height; ++var11) {
               var2.getDataElements(0, var11, this.width, 1, var5);
               this.theConsumer.setPixels(0, var11, this.width, 1, var1, (int[])var5, 0, this.width);
            }
         }
      }

      if (var6) {
         ColorModel var12 = ColorModel.getRGBdefault();
         this.theConsumer.setColorModel(var12);

         for(var8 = 0; var8 < this.height; ++var8) {
            for(var9 = 0; var9 < this.width; ++var9) {
               var5[var9] = this.image.getRGB(var9, var8);
            }

            this.theConsumer.setPixels(0, var8, this.width, 1, var12, (int[])var5, 0, this.width);
         }
      }

   }

   private void produce() {
      try {
         this.theConsumer.setDimensions(this.image.getWidth(), this.image.getHeight());
         this.theConsumer.setProperties(this.properties);
         this.sendPixels();
         this.theConsumer.imageComplete(2);
         this.theConsumer.imageComplete(3);
      } catch (NullPointerException var2) {
         if (this.theConsumer != null) {
            this.theConsumer.imageComplete(1);
         }
      }

   }
}
