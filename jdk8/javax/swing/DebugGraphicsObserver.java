package javax.swing;

import java.awt.Image;
import java.awt.image.ImageObserver;

class DebugGraphicsObserver implements ImageObserver {
   int lastInfo;

   synchronized boolean allBitsPresent() {
      return (this.lastInfo & 32) != 0;
   }

   synchronized boolean imageHasProblem() {
      return (this.lastInfo & 64) != 0 || (this.lastInfo & 128) != 0;
   }

   public synchronized boolean imageUpdate(Image var1, int var2, int var3, int var4, int var5, int var6) {
      this.lastInfo = var2;
      return true;
   }
}
