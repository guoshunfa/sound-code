package sun.awt;

import java.awt.Canvas;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.ImageProducer;
import java.awt.image.PixelGrabber;

public abstract class CustomCursor extends Cursor {
   protected Image image;

   public CustomCursor(Image var1, Point var2, String var3) throws IndexOutOfBoundsException {
      super(var3);
      this.image = var1;
      Toolkit var4 = Toolkit.getDefaultToolkit();
      Canvas var5 = new Canvas();
      MediaTracker var6 = new MediaTracker(var5);
      var6.addImage(var1, 0);

      try {
         var6.waitForAll();
      } catch (InterruptedException var15) {
      }

      int var7 = var1.getWidth(var5);
      int var8 = var1.getHeight(var5);
      if (var6.isErrorAny() || var7 < 0 || var8 < 0) {
         var2.x = var2.y = 0;
      }

      Dimension var9 = var4.getBestCursorSize(var7, var8);
      if ((var9.width != var7 || var9.height != var8) && var9.width != 0 && var9.height != 0) {
         var1 = var1.getScaledInstance(var9.width, var9.height, 1);
         var7 = var9.width;
         var8 = var9.height;
      }

      if (var2.x < var7 && var2.y < var8 && var2.x >= 0 && var2.y >= 0) {
         int[] var10 = new int[var7 * var8];
         ImageProducer var11 = var1.getSource();
         PixelGrabber var12 = new PixelGrabber(var11, 0, 0, var7, var8, var10, 0, var7);

         try {
            var12.grabPixels();
         } catch (InterruptedException var14) {
         }

         this.createNativeCursor(this.image, var10, var7, var8, var2.x, var2.y);
      } else {
         throw new IndexOutOfBoundsException("invalid hotSpot");
      }
   }

   protected abstract void createNativeCursor(Image var1, int[] var2, int var3, int var4, int var5, int var6);
}
