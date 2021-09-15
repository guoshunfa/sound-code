package sun.lwawt.macosx;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

public class CCustomCursor extends Cursor {
   static Dimension sMaxCursorSize;
   Image fImage;
   Point fHotspot;
   int fWidth;
   int fHeight;
   CImage fCImage;

   static Dimension getMaxCursorSize() {
      if (sMaxCursorSize != null) {
         return sMaxCursorSize;
      } else {
         Rectangle var0 = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds();
         return sMaxCursorSize = new Dimension(var0.width / 2, var0.height / 2);
      }
   }

   public CCustomCursor(Image var1, Point var2, String var3) throws IndexOutOfBoundsException, HeadlessException {
      super(var3);
      this.fImage = var1;
      this.fHotspot = var2;
      Toolkit var4 = Toolkit.getDefaultToolkit();
      Canvas var5 = new Canvas();
      MediaTracker var6 = new MediaTracker(var5);
      var6.addImage(this.fImage, 0);

      try {
         var6.waitForAll();
      } catch (InterruptedException var10) {
      }

      int var7 = this.fImage.getWidth(var5);
      int var8 = this.fImage.getHeight(var5);
      if (!var6.isErrorAny() && var7 >= 0 && var8 >= 0) {
         Dimension var9 = var4.getBestCursorSize(var7, var8);
         var7 = var9.width;
         var8 = var9.height;
      } else {
         this.fHotspot.x = this.fHotspot.y = 0;
         var8 = 1;
         var7 = 1;
         this.fImage = createTransparentImage(var7, var8);
      }

      this.fWidth = var7;
      this.fHeight = var8;
      if (this.fHotspot.x < var7 && this.fHotspot.y < var8 && this.fHotspot.x >= 0 && this.fHotspot.y >= 0) {
         if (this.fHotspot.x >= var7) {
            this.fHotspot.x = var7 - 1;
         } else if (this.fHotspot.x < 0) {
            this.fHotspot.x = 0;
         }

         if (this.fHotspot.y >= var8) {
            this.fHotspot.y = var8 - 1;
         } else if (this.fHotspot.y < 0) {
            this.fHotspot.y = 0;
         }

      } else {
         throw new IndexOutOfBoundsException("invalid hotSpot");
      }
   }

   private static BufferedImage createTransparentImage(int var0, int var1) {
      GraphicsEnvironment var2 = GraphicsEnvironment.getLocalGraphicsEnvironment();
      GraphicsDevice var3 = var2.getDefaultScreenDevice();
      GraphicsConfiguration var4 = var3.getDefaultConfiguration();
      BufferedImage var5 = var4.createCompatibleImage(var0, var1, 2);
      Graphics2D var6 = (Graphics2D)var5.getGraphics();
      var6.setBackground(new Color(0, 0, 0, 0));
      var6.clearRect(0, 0, var0, var1);
      var6.dispose();
      return var5;
   }

   public static Dimension getBestCursorSize(int var0, int var1) {
      Dimension var2 = getMaxCursorSize();
      Dimension var3 = new Dimension(Math.max(1, Math.abs(var0)), Math.max(1, Math.abs(var1)));
      return new Dimension(Math.min(var3.width, var2.width), Math.min(var3.height, var2.height));
   }

   long getImageData() {
      if (this.fCImage != null) {
         return this.fCImage.ptr;
      } else {
         try {
            this.fCImage = CImage.getCreator().createFromImage(this.fImage);
            if (this.fCImage == null) {
               return 0L;
            } else {
               this.fCImage.resizeRepresentations((double)this.fWidth, (double)this.fHeight);
               return this.fCImage.ptr;
            }
         } catch (IllegalArgumentException var2) {
            return 0L;
         }
      }
   }

   Point getHotSpot() {
      return this.fHotspot;
   }
}
