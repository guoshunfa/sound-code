package java.awt.image;

import java.awt.Rectangle;
import java.util.Hashtable;

public class CropImageFilter extends ImageFilter {
   int cropX;
   int cropY;
   int cropW;
   int cropH;

   public CropImageFilter(int var1, int var2, int var3, int var4) {
      this.cropX = var1;
      this.cropY = var2;
      this.cropW = var3;
      this.cropH = var4;
   }

   public void setProperties(Hashtable<?, ?> var1) {
      Hashtable var2 = (Hashtable)var1.clone();
      var2.put("croprect", new Rectangle(this.cropX, this.cropY, this.cropW, this.cropH));
      super.setProperties(var2);
   }

   public void setDimensions(int var1, int var2) {
      this.consumer.setDimensions(this.cropW, this.cropH);
   }

   public void setPixels(int var1, int var2, int var3, int var4, ColorModel var5, byte[] var6, int var7, int var8) {
      int var9 = var1;
      if (var1 < this.cropX) {
         var9 = this.cropX;
      }

      int var10 = this.addWithoutOverflow(var1, var3);
      if (var10 > this.cropX + this.cropW) {
         var10 = this.cropX + this.cropW;
      }

      int var11 = var2;
      if (var2 < this.cropY) {
         var11 = this.cropY;
      }

      int var12 = this.addWithoutOverflow(var2, var4);
      if (var12 > this.cropY + this.cropH) {
         var12 = this.cropY + this.cropH;
      }

      if (var9 < var10 && var11 < var12) {
         this.consumer.setPixels(var9 - this.cropX, var11 - this.cropY, var10 - var9, var12 - var11, var5, var6, var7 + (var11 - var2) * var8 + (var9 - var1), var8);
      }
   }

   public void setPixels(int var1, int var2, int var3, int var4, ColorModel var5, int[] var6, int var7, int var8) {
      int var9 = var1;
      if (var1 < this.cropX) {
         var9 = this.cropX;
      }

      int var10 = this.addWithoutOverflow(var1, var3);
      if (var10 > this.cropX + this.cropW) {
         var10 = this.cropX + this.cropW;
      }

      int var11 = var2;
      if (var2 < this.cropY) {
         var11 = this.cropY;
      }

      int var12 = this.addWithoutOverflow(var2, var4);
      if (var12 > this.cropY + this.cropH) {
         var12 = this.cropY + this.cropH;
      }

      if (var9 < var10 && var11 < var12) {
         this.consumer.setPixels(var9 - this.cropX, var11 - this.cropY, var10 - var9, var12 - var11, var5, var6, var7 + (var11 - var2) * var8 + (var9 - var1), var8);
      }
   }

   private int addWithoutOverflow(int var1, int var2) {
      int var3 = var1 + var2;
      if (var1 > 0 && var2 > 0 && var3 < 0) {
         var3 = Integer.MAX_VALUE;
      } else if (var1 < 0 && var2 < 0 && var3 > 0) {
         var3 = Integer.MIN_VALUE;
      }

      return var3;
   }
}
