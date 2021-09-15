package sun.java2d.opengl;

import java.awt.BufferCapabilities;
import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.image.ColorModel;
import sun.awt.X11ComponentPeer;
import sun.awt.image.SunVolatileImage;
import sun.awt.image.VolatileSurfaceManager;
import sun.java2d.BackBufferCapsProvider;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.hw.ExtendedBufferCapabilities;

public class GLXVolatileSurfaceManager extends VolatileSurfaceManager {
   private boolean accelerationEnabled;

   public GLXVolatileSurfaceManager(SunVolatileImage var1, Object var2) {
      super(var1, var2);
      int var3 = var1.getTransparency();
      GLXGraphicsConfig var4 = (GLXGraphicsConfig)var1.getGraphicsConfig();
      this.accelerationEnabled = var3 == 1 || var3 == 3 && (var4.isCapPresent(12) || var4.isCapPresent(2));
   }

   protected boolean isAccelerationEnabled() {
      return this.accelerationEnabled;
   }

   protected SurfaceData initAcceleratedSurface() {
      Component var2 = this.vImg.getComponent();
      X11ComponentPeer var3 = var2 != null ? (X11ComponentPeer)var2.getPeer() : null;

      GLXSurfaceData.GLXOffScreenSurfaceData var1;
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

         if (var5) {
            var1 = GLXSurfaceData.createData(var3, this.vImg, 4);
         } else {
            GLXGraphicsConfig var11 = (GLXGraphicsConfig)this.vImg.getGraphicsConfig();
            ColorModel var12 = var11.getColorModel(this.vImg.getTransparency());
            int var13 = this.vImg.getForcedAccelSurfaceType();
            if (var13 == 0) {
               var13 = var11.isCapPresent(12) ? 5 : 2;
            }

            if (var4) {
               var1 = GLXSurfaceData.createData(var3, this.vImg, var13);
            } else {
               var1 = GLXSurfaceData.createData(var11, this.vImg.getWidth(), this.vImg.getHeight(), var12, this.vImg, var13);
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
