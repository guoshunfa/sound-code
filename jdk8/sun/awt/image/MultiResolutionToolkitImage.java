package sun.awt.image;

import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.Arrays;
import java.util.List;
import sun.misc.SoftCache;

public class MultiResolutionToolkitImage extends ToolkitImage implements MultiResolutionImage {
   Image resolutionVariant;
   private static final int BITS_INFO = 56;

   public MultiResolutionToolkitImage(Image var1, Image var2) {
      super(var1.getSource());
      this.resolutionVariant = var2;
   }

   public Image getResolutionVariant(int var1, int var2) {
      return (Image)(var1 <= this.getWidth() && var2 <= this.getHeight() ? this : this.resolutionVariant);
   }

   public Image getResolutionVariant() {
      return this.resolutionVariant;
   }

   public List<Image> getResolutionVariants() {
      return Arrays.asList(this, this.resolutionVariant);
   }

   public static ImageObserver getResolutionVariantObserver(Image var0, ImageObserver var1, int var2, int var3, int var4, int var5) {
      return getResolutionVariantObserver(var0, var1, var2, var3, var4, var5, false);
   }

   public static ImageObserver getResolutionVariantObserver(Image var0, ImageObserver var1, int var2, int var3, int var4, int var5, boolean var6) {
      if (var1 == null) {
         return null;
      } else {
         synchronized(MultiResolutionToolkitImage.ObserverCache.INSTANCE) {
            ImageObserver var8 = (ImageObserver)MultiResolutionToolkitImage.ObserverCache.INSTANCE.get(var1);
            if (var8 == null) {
               var8 = (var3x, var4x, var5x, var6x, var7, var8x) -> {
                  if ((var4x & 57) != 0) {
                     var7 = (var7 + 1) / 2;
                  }

                  if ((var4x & 58) != 0) {
                     var8x = (var8x + 1) / 2;
                  }

                  if ((var4x & 56) != 0) {
                     var5x /= 2;
                     var6x /= 2;
                  }

                  if (var6) {
                     var4x &= ((ToolkitImage)var0).getImageRep().check((ImageObserver)null);
                  }

                  return var1.imageUpdate(var0, var4x, var5x, var6x, var7, var8x);
               };
               MultiResolutionToolkitImage.ObserverCache.INSTANCE.put(var1, var8);
            }

            return var8;
         }
      }
   }

   private static class ObserverCache {
      static final SoftCache INSTANCE = new SoftCache();
   }
}
