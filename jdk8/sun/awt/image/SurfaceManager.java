package sun.awt.image;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.ImageCapabilities;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import sun.java2d.SurfaceData;
import sun.java2d.SurfaceDataProxy;

public abstract class SurfaceManager {
   private static SurfaceManager.ImageAccessor imgaccessor;
   private ConcurrentHashMap<Object, Object> cacheMap;

   public static void setImageAccessor(SurfaceManager.ImageAccessor var0) {
      if (imgaccessor != null) {
         throw new InternalError("Attempt to set ImageAccessor twice");
      } else {
         imgaccessor = var0;
      }
   }

   public static SurfaceManager getManager(Image var0) {
      Object var1 = imgaccessor.getSurfaceManager(var0);
      if (var1 == null) {
         try {
            BufferedImage var2 = (BufferedImage)var0;
            var1 = new BufImgSurfaceManager(var2);
            setManager(var2, (SurfaceManager)var1);
         } catch (ClassCastException var3) {
            throw new IllegalArgumentException("Invalid Image variant");
         }
      }

      return (SurfaceManager)var1;
   }

   public static void setManager(Image var0, SurfaceManager var1) {
      imgaccessor.setSurfaceManager(var0, var1);
   }

   public Object getCacheData(Object var1) {
      return this.cacheMap == null ? null : this.cacheMap.get(var1);
   }

   public void setCacheData(Object var1, Object var2) {
      if (this.cacheMap == null) {
         synchronized(this) {
            if (this.cacheMap == null) {
               this.cacheMap = new ConcurrentHashMap(2);
            }
         }
      }

      this.cacheMap.put(var1, var2);
   }

   public abstract SurfaceData getPrimarySurfaceData();

   public abstract SurfaceData restoreContents();

   public void acceleratedSurfaceLost() {
   }

   public ImageCapabilities getCapabilities(GraphicsConfiguration var1) {
      return new SurfaceManager.ImageCapabilitiesGc(var1);
   }

   public synchronized void flush() {
      this.flush(false);
   }

   synchronized void flush(boolean var1) {
      if (this.cacheMap != null) {
         Iterator var2 = this.cacheMap.values().iterator();

         while(var2.hasNext()) {
            Object var3 = var2.next();
            if (var3 instanceof SurfaceManager.FlushableCacheData && ((SurfaceManager.FlushableCacheData)var3).flush(var1)) {
               var2.remove();
            }
         }
      }

   }

   public void setAccelerationPriority(float var1) {
      if (var1 == 0.0F) {
         this.flush(true);
      }

   }

   public static int getImageScale(Image var0) {
      if (!(var0 instanceof VolatileImage)) {
         return 1;
      } else {
         SurfaceManager var1 = getManager(var0);
         return var1.getPrimarySurfaceData().getDefaultScale();
      }
   }

   public interface FlushableCacheData {
      boolean flush(boolean var1);
   }

   public interface ProxiedGraphicsConfig {
      Object getProxyKey();
   }

   class ImageCapabilitiesGc extends ImageCapabilities {
      GraphicsConfiguration gc;

      public ImageCapabilitiesGc(GraphicsConfiguration var2) {
         super(false);
         this.gc = var2;
      }

      public boolean isAccelerated() {
         GraphicsConfiguration var1 = this.gc;
         if (var1 == null) {
            var1 = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
         }

         if (var1 instanceof SurfaceManager.ProxiedGraphicsConfig) {
            Object var2 = ((SurfaceManager.ProxiedGraphicsConfig)var1).getProxyKey();
            if (var2 != null) {
               SurfaceDataProxy var3 = (SurfaceDataProxy)SurfaceManager.this.getCacheData(var2);
               return var3 != null && var3.isAccelerated();
            }
         }

         return false;
      }
   }

   public abstract static class ImageAccessor {
      public abstract SurfaceManager getSurfaceManager(Image var1);

      public abstract void setSurfaceManager(Image var1, SurfaceManager var2);
   }
}
