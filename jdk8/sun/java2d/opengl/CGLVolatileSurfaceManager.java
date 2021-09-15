package sun.java2d.opengl;

import java.awt.BufferCapabilities;
import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.image.ColorModel;
import java.awt.peer.ComponentPeer;
import sun.awt.image.SunVolatileImage;
import sun.awt.image.VolatileSurfaceManager;
import sun.java2d.BackBufferCapsProvider;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.hw.ExtendedBufferCapabilities;

public class CGLVolatileSurfaceManager extends VolatileSurfaceManager {
   private boolean accelerationEnabled;

   public CGLVolatileSurfaceManager(SunVolatileImage var1, Object var2) {
      super(var1, var2);
      int var3 = var1.getTransparency();
      CGLGraphicsConfig var4 = (CGLGraphicsConfig)var1.getGraphicsConfig();
      this.accelerationEnabled = var3 == 1 || var3 == 3 && (var4.isCapPresent(12) || var4.isCapPresent(2));
   }

   protected boolean isAccelerationEnabled() {
      return this.accelerationEnabled;
   }

   protected SurfaceData initAcceleratedSurface() {
      CGLSurfaceData.CGLOffScreenSurfaceData var1 = null;
      Component var2 = this.vImg.getComponent();
      ComponentPeer var3 = var2 != null ? var2.getPeer() : null;

      try {
         boolean var4 = false;
         boolean var5 = false;
         if (this.context instanceof Boolean) {
            var5 = (Boolean)this.context;
            if (var5 && var3 instanceof BackBufferCapsProvider) {
               BackBufferCapsProvider var6 = (BackBufferCapsProvider)var3;
               BufferCapabilities var7 = var6.getBackBufferCaps();
               if (var7 instanceof ExtendedBufferCapabilities) {
                  ExtendedBufferCapabilities var8 = (ExtendedBufferCapabilities)var7;
                  if (var8.getVSync() == ExtendedBufferCapabilities.VSyncType.VSYNC_ON && var8.getFlipContents() == BufferCapabilities.FlipContents.COPIED) {
                     var4 = true;
                     var5 = false;
                  }
               }
            }
         }

         if (!var5) {
            CGLGraphicsConfig var11 = (CGLGraphicsConfig)this.vImg.getGraphicsConfig();
            ColorModel var12 = var11.getColorModel(this.vImg.getTransparency());
            int var13 = this.vImg.getForcedAccelSurfaceType();
            if (var13 == 0) {
               var13 = var11.isCapPresent(12) ? 5 : 2;
            }

            if (!var4) {
               var1 = CGLSurfaceData.createData(var11, this.vImg.getWidth(), this.vImg.getHeight(), var12, this.vImg, var13);
            }
         }
      } catch (NullPointerException var9) {
         var1 = null;
      } catch (OutOfMemoryError var10) {
         var1 = null;
      }

      return var1;
   }

   protected boolean isConfigValid(GraphicsConfiguration var1) {
      return var1 == null || var1 == this.vImg.getGraphicsConfig();
   }

   public void initContents() {
      if (this.vImg.getForcedAccelSurfaceType() != 3) {
         super.initContents();
      }

   }
}
