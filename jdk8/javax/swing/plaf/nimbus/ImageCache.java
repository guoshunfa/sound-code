package javax.swing.plaf.nimbus;

import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class ImageCache {
   private final LinkedHashMap<Integer, ImageCache.PixelCountSoftReference> map = new LinkedHashMap(16, 0.75F, true);
   private final int maxPixelCount;
   private final int maxSingleImagePixelSize;
   private int currentPixelCount = 0;
   private ReadWriteLock lock = new ReentrantReadWriteLock();
   private ReferenceQueue<Image> referenceQueue = new ReferenceQueue();
   private static final ImageCache instance = new ImageCache();

   static ImageCache getInstance() {
      return instance;
   }

   public ImageCache() {
      this.maxPixelCount = 2097152;
      this.maxSingleImagePixelSize = 90000;
   }

   public ImageCache(int var1, int var2) {
      this.maxPixelCount = var1;
      this.maxSingleImagePixelSize = var2;
   }

   public void flush() {
      this.lock.readLock().lock();

      try {
         this.map.clear();
      } finally {
         this.lock.readLock().unlock();
      }

   }

   public boolean isImageCachable(int var1, int var2) {
      return var1 * var2 < this.maxSingleImagePixelSize;
   }

   public Image getImage(GraphicsConfiguration var1, int var2, int var3, Object... var4) {
      this.lock.readLock().lock();

      Image var6;
      try {
         ImageCache.PixelCountSoftReference var5 = (ImageCache.PixelCountSoftReference)this.map.get(this.hash(var1, var2, var3, var4));
         if (var5 != null && var5.equals(var1, var2, var3, var4)) {
            var6 = (Image)var5.get();
            return var6;
         }

         var6 = null;
      } finally {
         this.lock.readLock().unlock();
      }

      return var6;
   }

   public boolean setImage(Image var1, GraphicsConfiguration var2, int var3, int var4, Object... var5) {
      if (!this.isImageCachable(var3, var4)) {
         return false;
      } else {
         int var6 = this.hash(var2, var3, var4, var5);
         this.lock.writeLock().lock();

         boolean var16;
         try {
            ImageCache.PixelCountSoftReference var7 = (ImageCache.PixelCountSoftReference)this.map.get(var6);
            if (var7 != null && var7.get() == var1) {
               boolean var15 = true;
               return var15;
            }

            if (var7 != null) {
               this.currentPixelCount -= var7.pixelCount;
               this.map.remove(var6);
            }

            int var8 = var1.getWidth((ImageObserver)null) * var1.getHeight((ImageObserver)null);
            this.currentPixelCount += var8;
            if (this.currentPixelCount > this.maxPixelCount) {
               while((var7 = (ImageCache.PixelCountSoftReference)this.referenceQueue.poll()) != null) {
                  this.map.remove(var7.hash);
                  this.currentPixelCount -= var7.pixelCount;
               }
            }

            Map.Entry var10;
            if (this.currentPixelCount > this.maxPixelCount) {
               for(Iterator var9 = this.map.entrySet().iterator(); this.currentPixelCount > this.maxPixelCount && var9.hasNext(); this.currentPixelCount -= ((ImageCache.PixelCountSoftReference)var10.getValue()).pixelCount) {
                  var10 = (Map.Entry)var9.next();
                  var9.remove();
                  Image var11 = (Image)((ImageCache.PixelCountSoftReference)var10.getValue()).get();
                  if (var11 != null) {
                     var11.flush();
                  }
               }
            }

            this.map.put(var6, new ImageCache.PixelCountSoftReference(var1, this.referenceQueue, var8, var6, var2, var3, var4, var5));
            var16 = true;
         } finally {
            this.lock.writeLock().unlock();
         }

         return var16;
      }
   }

   private int hash(GraphicsConfiguration var1, int var2, int var3, Object... var4) {
      int var5 = var1 != null ? var1.hashCode() : 0;
      var5 = 31 * var5 + var2;
      var5 = 31 * var5 + var3;
      var5 = 31 * var5 + Arrays.deepHashCode(var4);
      return var5;
   }

   private static class PixelCountSoftReference extends SoftReference<Image> {
      private final int pixelCount;
      private final int hash;
      private final GraphicsConfiguration config;
      private final int w;
      private final int h;
      private final Object[] args;

      public PixelCountSoftReference(Image var1, ReferenceQueue<? super Image> var2, int var3, int var4, GraphicsConfiguration var5, int var6, int var7, Object[] var8) {
         super(var1, var2);
         this.pixelCount = var3;
         this.hash = var4;
         this.config = var5;
         this.w = var6;
         this.h = var7;
         this.args = var8;
      }

      public boolean equals(GraphicsConfiguration var1, int var2, int var3, Object[] var4) {
         return var1 == this.config && var2 == this.w && var3 == this.h && Arrays.equals(var4, this.args);
      }
   }
}
