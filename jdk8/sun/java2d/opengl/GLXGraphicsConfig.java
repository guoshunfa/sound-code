package sun.java2d.opengl;

import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.ImageCapabilities;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.VolatileImage;
import java.awt.image.WritableRaster;
import java.util.Hashtable;
import sun.awt.X11ComponentPeer;
import sun.awt.X11GraphicsConfig;
import sun.awt.X11GraphicsDevice;
import sun.awt.X11GraphicsEnvironment;
import sun.awt.image.OffScreenImage;
import sun.awt.image.SunVolatileImage;
import sun.awt.image.SurfaceManager;
import sun.java2d.SunGraphics2D;
import sun.java2d.Surface;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.hw.AccelDeviceEventListener;
import sun.java2d.pipe.hw.AccelDeviceEventNotifier;
import sun.java2d.pipe.hw.AccelSurface;
import sun.java2d.pipe.hw.AccelTypedVolatileImage;
import sun.java2d.pipe.hw.ContextCapabilities;

public class GLXGraphicsConfig extends X11GraphicsConfig implements OGLGraphicsConfig {
   private static ImageCapabilities imageCaps = new GLXGraphicsConfig.GLXImageCaps();
   private BufferCapabilities bufferCaps;
   private long pConfigInfo;
   private ContextCapabilities oglCaps;
   private OGLContext context;

   private static native long getGLXConfigInfo(int var0, int var1);

   private static native int getOGLCapabilities(long var0);

   private native void initConfig(long var1, long var3);

   private GLXGraphicsConfig(X11GraphicsDevice var1, int var2, long var3, ContextCapabilities var5) {
      super(var1, var2, 0, 0, (var5.getCaps() & 65536) != 0);
      this.pConfigInfo = var3;
      this.initConfig(this.getAData(), var3);
      this.oglCaps = var5;
      this.context = new OGLContext(OGLRenderQueue.getInstance(), this);
   }

   public Object getProxyKey() {
      return this;
   }

   public SurfaceData createManagedSurface(int var1, int var2, int var3) {
      return GLXSurfaceData.createData(this, var1, var2, this.getColorModel(var3), (Image)null, 3);
   }

   public static GLXGraphicsConfig getConfig(X11GraphicsDevice var0, int var1) {
      if (!X11GraphicsEnvironment.isGLXAvailable()) {
         return null;
      } else {
         long var2 = 0L;
         final String[] var4 = new String[1];
         OGLRenderQueue var5 = OGLRenderQueue.getInstance();
         var5.lock();

         try {
            OGLContext.invalidateCurrentContext();
            GLXGraphicsConfig.GLXGetConfigInfo var6 = new GLXGraphicsConfig.GLXGetConfigInfo(var0.getScreen(), var1);
            var5.flushAndInvokeNow(var6);
            var2 = var6.getConfigInfo();
            if (var2 != 0L) {
               OGLContext.setScratchSurface(var2);
               var5.flushAndInvokeNow(new Runnable() {
                  public void run() {
                     var4[0] = OGLContext.getOGLIdString();
                  }
               });
            }
         } finally {
            var5.unlock();
         }

         if (var2 == 0L) {
            return null;
         } else {
            int var10 = getOGLCapabilities(var2);
            OGLContext.OGLContextCaps var7 = new OGLContext.OGLContextCaps(var10, var4[0]);
            return new GLXGraphicsConfig(var0, var1, var2, var7);
         }
      }
   }

   public final boolean isCapPresent(int var1) {
      return (this.oglCaps.getCaps() & var1) != 0;
   }

   public final long getNativeConfigInfo() {
      return this.pConfigInfo;
   }

   public final OGLContext getContext() {
      return this.context;
   }

   public BufferedImage createCompatibleImage(int var1, int var2) {
      DirectColorModel var3 = new DirectColorModel(24, 16711680, 65280, 255);
      WritableRaster var4 = var3.createCompatibleWritableRaster(var1, var2);
      return new BufferedImage(var3, var4, var3.isAlphaPremultiplied(), (Hashtable)null);
   }

   public ColorModel getColorModel(int var1) {
      switch(var1) {
      case 1:
         return new DirectColorModel(24, 16711680, 65280, 255);
      case 2:
         return new DirectColorModel(25, 16711680, 65280, 255, 16777216);
      case 3:
         ColorSpace var2 = ColorSpace.getInstance(1000);
         return new DirectColorModel(var2, 32, 16711680, 65280, 255, -16777216, true, 3);
      default:
         return null;
      }
   }

   public String toString() {
      return "GLXGraphicsConfig[dev=" + this.screen + ",vis=0x" + Integer.toHexString(this.visual) + "]";
   }

   public SurfaceData createSurfaceData(X11ComponentPeer var1) {
      return GLXSurfaceData.createData(var1);
   }

   public Image createAcceleratedImage(Component var1, int var2, int var3) {
      ColorModel var4 = this.getColorModel(1);
      WritableRaster var5 = var4.createCompatibleWritableRaster(var2, var3);
      return new OffScreenImage(var1, var4, var5, var4.isAlphaPremultiplied());
   }

   public long createBackBuffer(X11ComponentPeer var1, int var2, BufferCapabilities var3) throws AWTException {
      if (var2 > 2) {
         throw new AWTException("Only double or single buffering is supported");
      } else {
         BufferCapabilities var4 = this.getBufferCapabilities();
         if (!var4.isPageFlipping()) {
            throw new AWTException("Page flipping is not supported");
         } else if (var3.getFlipContents() == BufferCapabilities.FlipContents.PRIOR) {
            throw new AWTException("FlipContents.PRIOR is not supported");
         } else {
            return 1L;
         }
      }
   }

   public void destroyBackBuffer(long var1) {
   }

   public VolatileImage createBackBufferImage(Component var1, long var2) {
      return new SunVolatileImage(var1, var1.getWidth(), var1.getHeight(), Boolean.TRUE);
   }

   public void flip(X11ComponentPeer var1, Component var2, VolatileImage var3, int var4, int var5, int var6, int var7, BufferCapabilities.FlipContents var8) {
      if (var8 == BufferCapabilities.FlipContents.COPIED) {
         SurfaceManager var9 = SurfaceManager.getManager(var3);
         SurfaceData var10 = var9.getPrimarySurfaceData();
         if (!(var10 instanceof GLXSurfaceData.GLXVSyncOffScreenSurfaceData)) {
            Graphics var29 = var1.getGraphics();

            try {
               var29.drawImage(var3, var4, var5, var6, var7, var4, var5, var6, var7, (ImageObserver)null);
            } finally {
               var29.dispose();
            }

            return;
         }

         GLXSurfaceData.GLXVSyncOffScreenSurfaceData var11 = (GLXSurfaceData.GLXVSyncOffScreenSurfaceData)var10;
         SurfaceData var12 = var11.getFlipSurface();
         SunGraphics2D var13 = new SunGraphics2D(var12, Color.black, Color.white, (Font)null);

         try {
            var13.drawImage(var3, 0, 0, (ImageObserver)null);
         } finally {
            var13.dispose();
         }
      } else if (var8 == BufferCapabilities.FlipContents.PRIOR) {
         return;
      }

      OGLSurfaceData.swapBuffers(var1.getContentWindow());
      if (var8 == BufferCapabilities.FlipContents.BACKGROUND) {
         Graphics var30 = var3.getGraphics();

         try {
            var30.setColor(var2.getBackground());
            var30.fillRect(0, 0, var3.getWidth(), var3.getHeight());
         } finally {
            var30.dispose();
         }
      }

   }

   public BufferCapabilities getBufferCapabilities() {
      if (this.bufferCaps == null) {
         this.bufferCaps = new GLXGraphicsConfig.GLXBufferCaps(this.isDoubleBuffered());
      }

      return this.bufferCaps;
   }

   public ImageCapabilities getImageCapabilities() {
      return imageCaps;
   }

   public VolatileImage createCompatibleVolatileImage(int var1, int var2, int var3, int var4) {
      if (var4 != 4 && var4 != 1 && var4 != 0 && var3 != 2) {
         if (var4 == 5) {
            if (!this.isCapPresent(12)) {
               return null;
            }
         } else if (var4 == 2) {
            boolean var5 = var3 == 1;
            if (!var5 && !this.isCapPresent(2)) {
               return null;
            }
         }

         AccelTypedVolatileImage var7 = new AccelTypedVolatileImage(this, var1, var2, var3, var4);
         Surface var6 = var7.getDestSurface();
         if (!(var6 instanceof AccelSurface) || ((AccelSurface)var6).getType() != var4) {
            var7.flush();
            var7 = null;
         }

         return var7;
      } else {
         return null;
      }
   }

   public ContextCapabilities getContextCapabilities() {
      return this.oglCaps;
   }

   public void addDeviceEventListener(AccelDeviceEventListener var1) {
      AccelDeviceEventNotifier.addListener(var1, this.screen.getScreen());
   }

   public void removeDeviceEventListener(AccelDeviceEventListener var1) {
      AccelDeviceEventNotifier.removeListener(var1);
   }

   private static class GLXImageCaps extends ImageCapabilities {
      private GLXImageCaps() {
         super(true);
      }

      public boolean isTrueVolatile() {
         return true;
      }

      // $FF: synthetic method
      GLXImageCaps(Object var1) {
         this();
      }
   }

   private static class GLXBufferCaps extends BufferCapabilities {
      public GLXBufferCaps(boolean var1) {
         super(GLXGraphicsConfig.imageCaps, GLXGraphicsConfig.imageCaps, var1 ? BufferCapabilities.FlipContents.UNDEFINED : null);
      }
   }

   private static class GLXGetConfigInfo implements Runnable {
      private int screen;
      private int visual;
      private long cfginfo;

      private GLXGetConfigInfo(int var1, int var2) {
         this.screen = var1;
         this.visual = var2;
      }

      public void run() {
         this.cfginfo = GLXGraphicsConfig.getGLXConfigInfo(this.screen, this.visual);
      }

      public long getConfigInfo() {
         return this.cfginfo;
      }

      // $FF: synthetic method
      GLXGetConfigInfo(int var1, int var2, Object var3) {
         this(var1, var2);
      }
   }
}
