package java.awt;

import java.awt.image.ImageObserver;
import java.io.Serializable;

class ImageMediaEntry extends MediaEntry implements ImageObserver, Serializable {
   Image image;
   int width;
   int height;
   private static final long serialVersionUID = 4739377000350280650L;

   ImageMediaEntry(MediaTracker var1, Image var2, int var3, int var4, int var5) {
      super(var1, var3);
      this.image = var2;
      this.width = var4;
      this.height = var5;
   }

   boolean matches(Image var1, int var2, int var3) {
      return this.image == var1 && this.width == var2 && this.height == var3;
   }

   Object getMedia() {
      return this.image;
   }

   synchronized int getStatus(boolean var1, boolean var2) {
      if (var2) {
         int var3 = this.tracker.target.checkImage(this.image, this.width, this.height, (ImageObserver)null);
         int var4 = this.parseflags(var3);
         if (var4 == 0) {
            if ((this.status & 12) != 0) {
               this.setStatus(2);
            }
         } else if (var4 != this.status) {
            this.setStatus(var4);
         }
      }

      return super.getStatus(var1, var2);
   }

   void startLoad() {
      if (this.tracker.target.prepareImage(this.image, this.width, this.height, this)) {
         this.setStatus(8);
      }

   }

   int parseflags(int var1) {
      if ((var1 & 64) != 0) {
         return 4;
      } else if ((var1 & 128) != 0) {
         return 2;
      } else {
         return (var1 & 48) != 0 ? 8 : 0;
      }
   }

   public boolean imageUpdate(Image var1, int var2, int var3, int var4, int var5, int var6) {
      if (this.cancelled) {
         return false;
      } else {
         int var7 = this.parseflags(var2);
         if (var7 != 0 && var7 != this.status) {
            this.setStatus(var7);
         }

         return (this.status & 1) != 0;
      }
   }
}
