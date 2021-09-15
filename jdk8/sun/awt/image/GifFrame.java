package sun.awt.image;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;

class GifFrame {
   private static final boolean verbose = false;
   private static IndexColorModel trans_model;
   static final int DISPOSAL_NONE = 0;
   static final int DISPOSAL_SAVE = 1;
   static final int DISPOSAL_BGCOLOR = 2;
   static final int DISPOSAL_PREVIOUS = 3;
   GifImageDecoder decoder;
   int disposal_method;
   int delay;
   IndexColorModel model;
   int x;
   int y;
   int width;
   int height;
   boolean initialframe;

   public GifFrame(GifImageDecoder var1, int var2, int var3, boolean var4, IndexColorModel var5, int var6, int var7, int var8, int var9) {
      this.decoder = var1;
      this.disposal_method = var2;
      this.delay = var3;
      this.model = var5;
      this.initialframe = var4;
      this.x = var6;
      this.y = var7;
      this.width = var8;
      this.height = var9;
   }

   private void setPixels(int var1, int var2, int var3, int var4, ColorModel var5, byte[] var6, int var7, int var8) {
      this.decoder.setPixels(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public boolean dispose() {
      if (this.decoder.imageComplete(2, false) == 0) {
         return false;
      } else {
         if (this.delay > 0) {
            try {
               Thread.sleep((long)this.delay);
            } catch (InterruptedException var8) {
               return false;
            }
         } else {
            Thread.yield();
         }

         int var1 = this.decoder.global_width;
         int var2 = this.decoder.global_height;
         if (this.x < 0) {
            this.width += this.x;
            this.x = 0;
         }

         if (this.x + this.width > var1) {
            this.width = var1 - this.x;
         }

         if (this.width <= 0) {
            this.disposal_method = 0;
         } else {
            if (this.y < 0) {
               this.height += this.y;
               this.y = 0;
            }

            if (this.y + this.height > var2) {
               this.height = var2 - this.y;
            }

            if (this.height <= 0) {
               this.disposal_method = 0;
            }
         }

         switch(this.disposal_method) {
         case 1:
            this.decoder.saved_model = this.model;
            break;
         case 2:
            byte var5;
            if (this.model.getTransparentPixel() < 0) {
               this.model = trans_model;
               if (this.model == null) {
                  this.model = new IndexColorModel(8, 1, new byte[4], 0, true);
                  trans_model = this.model;
               }

               var5 = 0;
            } else {
               var5 = (byte)this.model.getTransparentPixel();
            }

            byte[] var6 = new byte[this.width];
            int var7;
            if (var5 != 0) {
               for(var7 = 0; var7 < this.width; ++var7) {
                  var6[var7] = var5;
               }
            }

            if (this.decoder.saved_image != null) {
               for(var7 = 0; var7 < var1 * var2; ++var7) {
                  this.decoder.saved_image[var7] = var5;
               }
            }

            this.setPixels(this.x, this.y, this.width, this.height, this.model, var6, 0, 0);
            break;
         case 3:
            byte[] var3 = this.decoder.saved_image;
            IndexColorModel var4 = this.decoder.saved_model;
            if (var3 != null) {
               this.setPixels(this.x, this.y, this.width, this.height, var4, var3, this.y * var1 + this.x, var1);
            }
         }

         return true;
      }
   }
}
