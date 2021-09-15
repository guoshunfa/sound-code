package sun.java2d;

import sun.awt.image.SunVolatileImage;
import sun.awt.image.VolatileSurfaceManager;

public abstract class SurfaceManagerFactory {
   private static SurfaceManagerFactory instance;

   public static synchronized SurfaceManagerFactory getInstance() {
      if (instance == null) {
         throw new IllegalStateException("No SurfaceManagerFactory set.");
      } else {
         return instance;
      }
   }

   public static synchronized void setInstance(SurfaceManagerFactory var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("factory must be non-null");
      } else if (instance != null) {
         throw new IllegalStateException("The surface manager factory is already initialized");
      } else {
         instance = var0;
      }
   }

   public abstract VolatileSurfaceManager createVolatileManager(SunVolatileImage var1, Object var2);
}
