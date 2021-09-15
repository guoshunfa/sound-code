package sun.java2d.xr;

import java.awt.GraphicsConfiguration;
import java.awt.ImageCapabilities;
import java.awt.image.ColorModel;
import sun.awt.image.SunVolatileImage;
import sun.awt.image.VolatileSurfaceManager;
import sun.java2d.SurfaceData;

public class XRVolatileSurfaceManager extends VolatileSurfaceManager {
   public XRVolatileSurfaceManager(SunVolatileImage var1, Object var2) {
      super(var1, var2);
   }

   protected boolean isAccelerationEnabled() {
      return true;
   }

   protected SurfaceData initAcceleratedSurface() {
      XRSurfaceData.XRPixmapSurfaceData var1;
      try {
         XRGraphicsConfig var2 = (XRGraphicsConfig)this.vImg.getGraphicsConfig();
         ColorModel var3 = var2.getColorModel();
         long var4 = 0L;
         if (this.context instanceof Long) {
            var4 = (Long)this.context;
         }

         var1 = XRSurfaceData.createData(var2, this.vImg.getWidth(), this.vImg.getHeight(), var3, this.vImg, var4, this.vImg.getTransparency());
      } catch (NullPointerException var6) {
         var1 = null;
      } catch (OutOfMemoryError var7) {
         var1 = null;
      }

      return var1;
   }

   protected boolean isConfigValid(GraphicsConfiguration var1) {
      return true;
   }

   public ImageCapabilities getCapabilities(GraphicsConfiguration var1) {
      return this.isConfigValid(var1) && this.isAccelerationEnabled() ? new ImageCapabilities(true) : new ImageCapabilities(false);
   }
}
