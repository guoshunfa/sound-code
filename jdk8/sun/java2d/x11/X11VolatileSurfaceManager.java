package sun.java2d.x11;

import java.awt.GraphicsConfiguration;
import java.awt.ImageCapabilities;
import java.awt.image.ColorModel;
import sun.awt.X11GraphicsConfig;
import sun.awt.image.SunVolatileImage;
import sun.awt.image.VolatileSurfaceManager;
import sun.java2d.SurfaceData;

public class X11VolatileSurfaceManager extends VolatileSurfaceManager {
   private boolean accelerationEnabled;

   public X11VolatileSurfaceManager(SunVolatileImage var1, Object var2) {
      super(var1, var2);
      this.accelerationEnabled = X11SurfaceData.isAccelerationEnabled() && var1.getTransparency() == 1;
      if (var2 != null && !this.accelerationEnabled) {
         this.accelerationEnabled = true;
         this.sdAccel = this.initAcceleratedSurface();
         this.sdCurrent = this.sdAccel;
         if (this.sdBackup != null) {
            this.sdBackup = null;
         }
      }

   }

   protected boolean isAccelerationEnabled() {
      return this.accelerationEnabled;
   }

   protected SurfaceData initAcceleratedSurface() {
      X11SurfaceData.X11PixmapSurfaceData var1;
      try {
         X11GraphicsConfig var2 = (X11GraphicsConfig)this.vImg.getGraphicsConfig();
         ColorModel var3 = var2.getColorModel();
         long var4 = 0L;
         if (this.context instanceof Long) {
            var4 = (Long)this.context;
         }

         var1 = X11SurfaceData.createData(var2, this.vImg.getWidth(), this.vImg.getHeight(), var3, this.vImg, var4, 1);
      } catch (NullPointerException var6) {
         var1 = null;
      } catch (OutOfMemoryError var7) {
         var1 = null;
      }

      return var1;
   }

   protected boolean isConfigValid(GraphicsConfiguration var1) {
      return var1 == null || var1 == this.vImg.getGraphicsConfig();
   }

   public ImageCapabilities getCapabilities(GraphicsConfiguration var1) {
      return this.isConfigValid(var1) && this.isAccelerationEnabled() ? new ImageCapabilities(true) : new ImageCapabilities(false);
   }
}
