package sun.awt.image;

import java.awt.Image;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import sun.awt.AppContext;

public final class ImageCache {
   private final LinkedHashMap<ImageCache.PixelsKey, ImageCache.ImageSoftReference> map;
   private final int maxPixelCount;
   private int currentPixelCount;
   private final ReadWriteLock lock;
   private final ReferenceQueue<Image> referenceQueue;

   public static ImageCache getInstance() {
      return (ImageCache)AppContext.getSoftReferenceValue(ImageCache.class, () -> {
         return new ImageCache();
      });
   }

   ImageCache(int var1) {
      this.map = new LinkedHashMap(16, 0.75F, true);
      this.currentPixelCount = 0;
      this.lock = new ReentrantReadWriteLock();
      this.referenceQueue = new ReferenceQueue();
      this.maxPixelCount = var1;
   }

   ImageCache() {
      this(2097152);
   }

   public void flush() {
      this.lock.writeLock().lock();

      try {
         this.map.clear();
      } finally {
         this.lock.writeLock().unlock();
      }

   }

   public Image getImage(ImageCache.PixelsKey var1) {
      this.lock.readLock().lock();

      ImageCache.ImageSoftReference var2;
      try {
         var2 = (ImageCache.ImageSoftReference)this.map.get(var1);
      } finally {
         this.lock.readLock().unlock();
      }

      return var2 == null ? null : (Image)var2.get();
   }

   public void setImage(ImageCache.PixelsKey var1, Image var2) {
      this.lock.writeLock().lock();

      try {
         ImageCache.ImageSoftReference var3 = (ImageCache.ImageSoftReference)this.map.get(var1);
         if (var3 != null) {
            if (var3.get() != null) {
               return;
            }

            this.currentPixelCount -= var1.getPixelCount();
            this.map.remove(var1);
         }

         int var4 = var1.getPixelCount();
         this.currentPixelCount += var4;
         if (this.currentPixelCount > this.maxPixelCount) {
            while((var3 = (ImageCache.ImageSoftReference)this.referenceQueue.poll()) != null) {
               this.map.remove(var3.key);
               this.currentPixelCount -= var3.key.getPixelCount();
            }
         }

         Map.Entry var6;
         if (this.currentPixelCount > this.maxPixelCount) {
            for(Iterator var5 = this.map.entrySet().iterator(); this.currentPixelCount > this.maxPixelCount && var5.hasNext(); this.currentPixelCount -= ((ImageCache.ImageSoftReference)var6.getValue()).key.getPixelCount()) {
               var6 = (Map.Entry)var5.next();
               var5.remove();
               Image var7 = (Image)((ImageCache.ImageSoftReference)var6.getValue()).get();
               if (var7 != null) {
                  var7.flush();
               }
            }
         }

         this.map.put(var1, new ImageCache.ImageSoftReference(var1, var2, this.referenceQueue));
      } finally {
         this.lock.writeLock().unlock();
      }
   }

   private static class ImageSoftReference extends SoftReference<Image> {
      final ImageCache.PixelsKey key;

      ImageSoftReference(ImageCache.PixelsKey var1, Image var2, ReferenceQueue<? super Image> var3) {
         super(var2, var3);
         this.key = var1;
      }
   }

   public interface PixelsKey {
      int getPixelCount();
   }
}
