package sun.awt;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.Hashtable;
import sun.awt.image.ImageRepresentation;
import sun.awt.image.ToolkitImage;

public class IconInfo {
   private int[] intIconData;
   private long[] longIconData;
   private Image image;
   private final int width;
   private final int height;
   private int scaledWidth;
   private int scaledHeight;
   private int rawLength;

   public IconInfo(int[] var1) {
      this.intIconData = null == var1 ? null : Arrays.copyOf(var1, var1.length);
      this.width = var1[0];
      this.height = var1[1];
      this.scaledWidth = this.width;
      this.scaledHeight = this.height;
      this.rawLength = this.width * this.height + 2;
   }

   public IconInfo(long[] var1) {
      this.longIconData = null == var1 ? null : Arrays.copyOf(var1, var1.length);
      this.width = (int)var1[0];
      this.height = (int)var1[1];
      this.scaledWidth = this.width;
      this.scaledHeight = this.height;
      this.rawLength = this.width * this.height + 2;
   }

   public IconInfo(Image var1) {
      this.image = var1;
      if (var1 instanceof ToolkitImage) {
         ImageRepresentation var2 = ((ToolkitImage)var1).getImageRep();
         var2.reconstruct(32);
         this.width = var2.getWidth();
         this.height = var2.getHeight();
      } else {
         this.width = var1.getWidth((ImageObserver)null);
         this.height = var1.getHeight((ImageObserver)null);
      }

      this.scaledWidth = this.width;
      this.scaledHeight = this.height;
      this.rawLength = this.width * this.height + 2;
   }

   public void setScaledSize(int var1, int var2) {
      this.scaledWidth = var1;
      this.scaledHeight = var2;
      this.rawLength = var1 * var2 + 2;
   }

   public boolean isValid() {
      return this.width > 0 && this.height > 0;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public String toString() {
      return "IconInfo[w=" + this.width + ",h=" + this.height + ",sw=" + this.scaledWidth + ",sh=" + this.scaledHeight + "]";
   }

   public int getRawLength() {
      return this.rawLength;
   }

   public int[] getIntData() {
      if (this.intIconData == null) {
         if (this.longIconData != null) {
            this.intIconData = longArrayToIntArray(this.longIconData);
         } else if (this.image != null) {
            this.intIconData = imageToIntArray(this.image, this.scaledWidth, this.scaledHeight);
         }
      }

      return this.intIconData;
   }

   public long[] getLongData() {
      if (this.longIconData == null) {
         if (this.intIconData != null) {
            this.longIconData = intArrayToLongArray(this.intIconData);
         } else if (this.image != null) {
            int[] var1 = imageToIntArray(this.image, this.scaledWidth, this.scaledHeight);
            this.longIconData = intArrayToLongArray(var1);
         }
      }

      return this.longIconData;
   }

   public Image getImage() {
      if (this.image == null) {
         if (this.intIconData != null) {
            this.image = intArrayToImage(this.intIconData);
         } else if (this.longIconData != null) {
            int[] var1 = longArrayToIntArray(this.longIconData);
            this.image = intArrayToImage(var1);
         }
      }

      return this.image;
   }

   private static int[] longArrayToIntArray(long[] var0) {
      int[] var1 = new int[var0.length];

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1[var2] = (int)var0[var2];
      }

      return var1;
   }

   private static long[] intArrayToLongArray(int[] var0) {
      long[] var1 = new long[var0.length];

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1[var2] = (long)var0[var2];
      }

      return var1;
   }

   static Image intArrayToImage(int[] var0) {
      DirectColorModel var1 = new DirectColorModel(ColorSpace.getInstance(1000), 32, 16711680, 65280, 255, -16777216, false, 3);
      DataBufferInt var2 = new DataBufferInt(var0, var0.length - 2, 2);
      WritableRaster var3 = Raster.createPackedRaster(var2, var0[0], var0[1], var0[0], new int[]{16711680, 65280, 255, -16777216}, (Point)null);
      BufferedImage var4 = new BufferedImage(var1, var3, false, (Hashtable)null);
      return var4;
   }

   static int[] imageToIntArray(Image var0, int var1, int var2) {
      if (var1 > 0 && var2 > 0) {
         DirectColorModel var3 = new DirectColorModel(ColorSpace.getInstance(1000), 32, 16711680, 65280, 255, -16777216, false, 3);
         DataBufferInt var4 = new DataBufferInt(var1 * var2);
         WritableRaster var5 = Raster.createPackedRaster(var4, var1, var2, var1, new int[]{16711680, 65280, 255, -16777216}, (Point)null);
         BufferedImage var6 = new BufferedImage(var3, var5, false, (Hashtable)null);
         Graphics var7 = var6.getGraphics();
         var7.drawImage(var0, 0, 0, var1, var2, (ImageObserver)null);
         var7.dispose();
         int[] var8 = var4.getData();
         int[] var9 = new int[var1 * var2 + 2];
         var9[0] = var1;
         var9[1] = var2;
         System.arraycopy(var8, 0, var9, 2, var1 * var2);
         return var9;
      } else {
         return null;
      }
   }
}
