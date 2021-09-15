package sun.awt.image;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.geom.Dimension2D;
import java.awt.image.ImageObserver;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MultiResolutionCachedImage extends AbstractMultiResolutionImage {
   private final int baseImageWidth;
   private final int baseImageHeight;
   private final Dimension2D[] sizes;
   private final BiFunction<Integer, Integer, Image> mapper;
   private int availableInfo;

   public MultiResolutionCachedImage(int var1, int var2, BiFunction<Integer, Integer, Image> var3) {
      this(var1, var2, new Dimension[]{new Dimension(var1, var2)}, var3);
   }

   public MultiResolutionCachedImage(int var1, int var2, Dimension2D[] var3, BiFunction<Integer, Integer, Image> var4) {
      this.baseImageWidth = var1;
      this.baseImageHeight = var2;
      this.sizes = var3 == null ? null : (Dimension2D[])Arrays.copyOf((Object[])var3, var3.length);
      this.mapper = var4;
   }

   public Image getResolutionVariant(int var1, int var2) {
      ImageCache var3 = ImageCache.getInstance();
      MultiResolutionCachedImage.ImageCacheKey var4 = new MultiResolutionCachedImage.ImageCacheKey(this, var1, var2);
      Image var5 = var3.getImage(var4);
      if (var5 == null) {
         var5 = (Image)this.mapper.apply(var1, var2);
         var3.setImage(var4, var5);
      }

      preload(var5, this.availableInfo);
      return var5;
   }

   public List<Image> getResolutionVariants() {
      return (List)Arrays.stream((Object[])this.sizes).map((var1) -> {
         return this.getResolutionVariant((int)var1.getWidth(), (int)var1.getHeight());
      }).collect(Collectors.toList());
   }

   public MultiResolutionCachedImage map(Function<Image, Image> var1) {
      return new MultiResolutionCachedImage(this.baseImageWidth, this.baseImageHeight, this.sizes, (var2, var3) -> {
         return (Image)var1.apply(this.getResolutionVariant(var2, var3));
      });
   }

   public int getWidth(ImageObserver var1) {
      this.updateInfo(var1, 1);
      return super.getWidth(var1);
   }

   public int getHeight(ImageObserver var1) {
      this.updateInfo(var1, 2);
      return super.getHeight(var1);
   }

   public Object getProperty(String var1, ImageObserver var2) {
      this.updateInfo(var2, 4);
      return super.getProperty(var1, var2);
   }

   protected Image getBaseImage() {
      return this.getResolutionVariant(this.baseImageWidth, this.baseImageHeight);
   }

   private void updateInfo(ImageObserver var1, int var2) {
      this.availableInfo |= var1 == null ? 32 : var2;
   }

   private static int getInfo(Image var0) {
      return var0 instanceof ToolkitImage ? ((ToolkitImage)var0).getImageRep().check((var0x, var1, var2, var3, var4, var5) -> {
         return false;
      }) : 0;
   }

   private static void preload(Image var0, final int var1) {
      if (var1 != 0 && var0 instanceof ToolkitImage) {
         ((ToolkitImage)var0).preload(new ImageObserver() {
            int flags = var1;

            public boolean imageUpdate(Image var1x, int var2, int var3, int var4, int var5, int var6) {
               this.flags &= ~var2;
               return this.flags != 0 && (var2 & 192) == 0;
            }
         });
      }

   }

   private static class ImageCacheKey implements ImageCache.PixelsKey {
      private final int pixelCount;
      private final int hash;
      private final int w;
      private final int h;
      private final Image baseImage;

      ImageCacheKey(Image var1, int var2, int var3) {
         this.baseImage = var1;
         this.w = var2;
         this.h = var3;
         this.pixelCount = var2 * var3;
         this.hash = this.hash();
      }

      public int getPixelCount() {
         return this.pixelCount;
      }

      private int hash() {
         int var1 = this.baseImage.hashCode();
         var1 = 31 * var1 + this.w;
         var1 = 31 * var1 + this.h;
         return var1;
      }

      public int hashCode() {
         return this.hash;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof MultiResolutionCachedImage.ImageCacheKey)) {
            return false;
         } else {
            MultiResolutionCachedImage.ImageCacheKey var2 = (MultiResolutionCachedImage.ImageCacheKey)var1;
            return this.baseImage == var2.baseImage && this.w == var2.w && this.h == var2.h;
         }
      }
   }
}
