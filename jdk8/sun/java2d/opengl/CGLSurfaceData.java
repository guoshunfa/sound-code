package sun.java2d.opengl;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.lwawt.macosx.CPlatformView;

public abstract class CGLSurfaceData extends OGLSurfaceData {
   protected final int scale;
   protected final int width;
   protected final int height;
   protected CPlatformView pView;
   private CGLGraphicsConfig graphicsConfig;

   native void validate(int var1, int var2, int var3, int var4, boolean var5);

   private native void initOps(long var1, long var3, long var5, int var7, int var8, boolean var9);

   protected native boolean initPbuffer(long var1, long var3, boolean var5, int var6, int var7);

   protected CGLSurfaceData(CGLGraphicsConfig var1, ColorModel var2, int var3, int var4, int var5) {
      super(var1, var2, var3);
      this.scale = var3 == 3 ? 1 : var1.getDevice().getScaleFactor();
      this.width = var4 * this.scale;
      this.height = var5 * this.scale;
   }

   protected CGLSurfaceData(CPlatformView var1, CGLGraphicsConfig var2, ColorModel var3, int var4, int var5, int var6) {
      this(var2, var3, var4, var5, var6);
      this.pView = var1;
      this.graphicsConfig = var2;
      long var7 = var2.getNativeConfigInfo();
      long var9 = 0L;
      boolean var11 = true;
      if (var1 != null) {
         var9 = var1.getAWTView();
         var11 = var1.isOpaque();
      }

      this.initOps(var7, var9, 0L, 0, 0, var11);
   }

   protected CGLSurfaceData(CGLLayer var1, CGLGraphicsConfig var2, ColorModel var3, int var4, int var5, int var6) {
      this(var2, var3, var4, var5, var6);
      this.graphicsConfig = var2;
      long var7 = var2.getNativeConfigInfo();
      long var9 = 0L;
      boolean var11 = true;
      if (var1 != null) {
         var9 = var1.getPointer();
         var11 = var1.isOpaque();
      }

      this.initOps(var7, 0L, var9, 0, 0, var11);
   }

   public GraphicsConfiguration getDeviceConfiguration() {
      return this.graphicsConfig;
   }

   public static CGLSurfaceData.CGLWindowSurfaceData createData(CPlatformView var0) {
      CGLGraphicsConfig var1 = getGC(var0);
      return new CGLSurfaceData.CGLWindowSurfaceData(var0, var1);
   }

   public static CGLSurfaceData.CGLLayerSurfaceData createData(CGLLayer var0) {
      CGLGraphicsConfig var1 = getGC(var0);
      Rectangle var2 = var0.getBounds();
      return new CGLSurfaceData.CGLLayerSurfaceData(var0, var1, var2.width, var2.height);
   }

   public static CGLSurfaceData.CGLOffScreenSurfaceData createData(CPlatformView var0, Image var1, int var2) {
      CGLGraphicsConfig var3 = getGC(var0);
      Rectangle var4 = var0.getBounds();
      return (CGLSurfaceData.CGLOffScreenSurfaceData)(var2 == 4 ? new CGLSurfaceData.CGLOffScreenSurfaceData(var0, var3, var4.width, var4.height, var1, var3.getColorModel(), 4) : new CGLSurfaceData.CGLVSyncOffScreenSurfaceData(var0, var3, var4.width, var4.height, var1, var3.getColorModel(), var2));
   }

   public static CGLSurfaceData.CGLOffScreenSurfaceData createData(CGLGraphicsConfig var0, int var1, int var2, ColorModel var3, Image var4, int var5) {
      return new CGLSurfaceData.CGLOffScreenSurfaceData((CPlatformView)null, var0, var1, var2, var4, var3, var5);
   }

   public static CGLGraphicsConfig getGC(CPlatformView var0) {
      if (var0 != null) {
         return (CGLGraphicsConfig)var0.getGraphicsConfiguration();
      } else {
         GraphicsEnvironment var1 = GraphicsEnvironment.getLocalGraphicsEnvironment();
         GraphicsDevice var2 = var1.getDefaultScreenDevice();
         return (CGLGraphicsConfig)var2.getDefaultConfiguration();
      }
   }

   public static CGLGraphicsConfig getGC(CGLLayer var0) {
      return (CGLGraphicsConfig)var0.getGraphicsConfiguration();
   }

   public void validate() {
   }

   public int getDefaultScale() {
      return this.scale;
   }

   public boolean copyArea(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      int var8 = var1.transformState;
      if (var8 <= 3 && var1.compositeState < 2) {
         if (var8 <= 2) {
            var2 += var1.transX;
            var3 += var1.transY;
         } else if (var8 == 3) {
            double[] var9 = new double[]{(double)var2, (double)var3, (double)(var2 + var4), (double)(var3 + var5), (double)(var2 + var6), (double)(var3 + var7)};
            var1.transform.transform((double[])var9, 0, (double[])var9, 0, 3);
            var2 = (int)Math.ceil(var9[0] - 0.5D);
            var3 = (int)Math.ceil(var9[1] - 0.5D);
            var4 = (int)Math.ceil(var9[2] - 0.5D) - var2;
            var5 = (int)Math.ceil(var9[3] - 0.5D) - var3;
            var6 = (int)Math.ceil(var9[4] - 0.5D) - var2;
            var7 = (int)Math.ceil(var9[5] - 0.5D) - var3;
         }

         oglRenderPipe.copyArea(var1, var2, var3, var4, var5, var6, var7);
         return true;
      } else {
         return false;
      }
   }

   protected native void clearWindow();

   private static native long createCGLContextOnSurface(CGLSurfaceData var0, long var1);

   public static long createOGLContextOnSurface(Graphics var0, long var1) {
      SurfaceData var3 = ((SunGraphics2D)var0).surfaceData;
      if (var3 instanceof CGLSurfaceData) {
         CGLSurfaceData var4 = (CGLSurfaceData)var3;
         return createCGLContextOnSurface(var4, var1);
      } else {
         return 0L;
      }
   }

   static native boolean makeCGLContextCurrentOnSurface(CGLSurfaceData var0, long var1);

   public static boolean makeOGLContextCurrentOnSurface(Graphics var0, long var1) {
      SurfaceData var3 = ((SunGraphics2D)var0).surfaceData;
      if (var1 != 0L && var3 instanceof CGLSurfaceData) {
         CGLSurfaceData var4 = (CGLSurfaceData)var3;
         return makeCGLContextCurrentOnSurface(var4, var1);
      } else {
         return false;
      }
   }

   private static native void destroyCGLContext(long var0);

   public static void destroyOGLContext(long var0) {
      if (var0 != 0L) {
         destroyCGLContext(var0);
      }

   }

   public static class CGLOffScreenSurfaceData extends CGLSurfaceData {
      private Image offscreenImage;

      public CGLOffScreenSurfaceData(CPlatformView var1, CGLGraphicsConfig var2, int var3, int var4, Image var5, ColorModel var6, int var7) {
         super(var1, var2, var6, var7, var3, var4);
         this.offscreenImage = var5;
         this.initSurface(this.width, this.height);
      }

      public SurfaceData getReplacement() {
         return restoreContents(this.offscreenImage);
      }

      public Rectangle getBounds() {
         if (this.type == 4) {
            Rectangle var1 = this.pView.getBounds();
            return new Rectangle(0, 0, var1.width, var1.height);
         } else {
            return new Rectangle(this.width, this.height);
         }
      }

      public Object getDestination() {
         return this.offscreenImage;
      }
   }

   public static class CGLVSyncOffScreenSurfaceData extends CGLSurfaceData.CGLOffScreenSurfaceData {
      private CGLSurfaceData.CGLOffScreenSurfaceData flipSurface;

      public CGLVSyncOffScreenSurfaceData(CPlatformView var1, CGLGraphicsConfig var2, int var3, int var4, Image var5, ColorModel var6, int var7) {
         super(var1, var2, var3, var4, var5, var6, var7);
         this.flipSurface = CGLSurfaceData.createData(var1, var5, 4);
      }

      public SurfaceData getFlipSurface() {
         return this.flipSurface;
      }

      public void flush() {
         this.flipSurface.flush();
         super.flush();
      }
   }

   public static class CGLLayerSurfaceData extends CGLSurfaceData {
      private CGLLayer layer;

      public CGLLayerSurfaceData(CGLLayer var1, CGLGraphicsConfig var2, int var3, int var4) {
         super((CGLLayer)var1, var2, var2.getColorModel(), 5, var3, var4);
         this.layer = var1;
         this.initSurface(this.width, this.height);
      }

      public SurfaceData getReplacement() {
         return this.layer.getSurfaceData();
      }

      boolean isOnScreen() {
         return true;
      }

      public Rectangle getBounds() {
         return new Rectangle(this.width, this.height);
      }

      public Object getDestination() {
         return this.layer.getDestination();
      }

      public int getTransparency() {
         return this.layer.getTransparency();
      }

      public void invalidate() {
         super.invalidate();
         this.clearWindow();
      }
   }

   public static class CGLWindowSurfaceData extends CGLSurfaceData {
      public CGLWindowSurfaceData(CPlatformView var1, CGLGraphicsConfig var2) {
         super((CPlatformView)var1, var2, var2.getColorModel(), 1, 0, 0);
      }

      public SurfaceData getReplacement() {
         return this.pView.getSurfaceData();
      }

      public Rectangle getBounds() {
         Rectangle var1 = this.pView.getBounds();
         return new Rectangle(0, 0, var1.width, var1.height);
      }

      public Object getDestination() {
         return this.pView.getDestination();
      }

      public void validate() {
         OGLRenderQueue var1 = OGLRenderQueue.getInstance();
         var1.lock();

         try {
            var1.flushAndInvokeNow(new Runnable() {
               public void run() {
                  Rectangle var1 = CGLWindowSurfaceData.this.pView.getBounds();
                  CGLWindowSurfaceData.this.validate(0, 0, var1.width, var1.height, CGLWindowSurfaceData.this.pView.isOpaque());
               }
            });
         } finally {
            var1.unlock();
         }

      }

      public void invalidate() {
         super.invalidate();
         this.clearWindow();
      }
   }
}
