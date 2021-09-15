package sun.java2d.opengl;

import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.ImageCapabilities;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.VolatileImage;
import java.awt.image.WritableRaster;
import java.util.Hashtable;
import sun.awt.CGraphicsConfig;
import sun.awt.CGraphicsDevice;
import sun.awt.image.OffScreenImage;
import sun.awt.image.SunVolatileImage;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;
import sun.java2d.Surface;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.hw.AccelDeviceEventListener;
import sun.java2d.pipe.hw.AccelDeviceEventNotifier;
import sun.java2d.pipe.hw.AccelSurface;
import sun.java2d.pipe.hw.AccelTypedVolatileImage;
import sun.java2d.pipe.hw.ContextCapabilities;
import sun.lwawt.LWComponentPeer;
import sun.lwawt.macosx.CPlatformView;

public final class CGLGraphicsConfig extends CGraphicsConfig implements OGLGraphicsConfig {
   private static final int kOpenGLSwapInterval = 0;
   private static boolean cglAvailable = initCGL();
   private static ImageCapabilities imageCaps = new CGLGraphicsConfig.CGLImageCaps();
   private int pixfmt;
   private BufferCapabilities bufferCaps;
   private long pConfigInfo;
   private ContextCapabilities oglCaps;
   private OGLContext context;
   private final Object disposerReferent = new Object();
   private final int maxTextureSize;

   private static native boolean initCGL();

   private static native long getCGLConfigInfo(int var0, int var1, int var2);

   private static native int getOGLCapabilities(long var0);

   private static native int nativeGetMaxTextureSize();

   private CGLGraphicsConfig(CGraphicsDevice var1, int var2, long var3, int var5, ContextCapabilities var6) {
      super(var1);
      this.pixfmt = var2;
      this.pConfigInfo = var3;
      this.oglCaps = var6;
      this.maxTextureSize = var5;
      this.context = new OGLContext(OGLRenderQueue.getInstance(), this);
      Disposer.addRecord(this.disposerReferent, new CGLGraphicsConfig.CGLGCDisposerRecord(this.pConfigInfo));
   }

   public Object getProxyKey() {
      return this;
   }

   public SurfaceData createManagedSurface(int var1, int var2, int var3) {
      return CGLSurfaceData.createData(this, var1, var2, this.getColorModel(var3), (Image)null, 3);
   }

   public static CGLGraphicsConfig getConfig(CGraphicsDevice var0, int var1) {
      if (!cglAvailable) {
         return null;
      } else {
         long var2 = 0L;
         int var4 = 0;
         String[] var5 = new String[1];
         OGLRenderQueue var6 = OGLRenderQueue.getInstance();
         var6.lock();

         try {
            OGLContext.invalidateCurrentContext();
            var2 = getCGLConfigInfo(var0.getCGDisplayID(), var1, 0);
            if (var2 != 0L) {
               var4 = nativeGetMaxTextureSize();
               var4 = var4 <= 16384 ? var4 / 2 : 8192;
               OGLContext.setScratchSurface(var2);
               var6.flushAndInvokeNow(() -> {
                  var5[0] = OGLContext.getOGLIdString();
               });
            }
         } finally {
            var6.unlock();
         }

         if (var2 == 0L) {
            return null;
         } else {
            int var7 = getOGLCapabilities(var2);
            OGLContext.OGLContextCaps var8 = new OGLContext.OGLContextCaps(var7, var5[0]);
            return new CGLGraphicsConfig(var0, var1, var2, var4, var8);
         }
      }
   }

   public static boolean isCGLAvailable() {
      return cglAvailable;
   }

   public boolean isCapPresent(int var1) {
      return (this.oglCaps.getCaps() & var1) != 0;
   }

   public long getNativeConfigInfo() {
      return this.pConfigInfo;
   }

   public OGLContext getContext() {
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

   public boolean isDoubleBuffered() {
      return this.isCapPresent(65536);
   }

   public synchronized void displayChanged() {
      OGLRenderQueue var1 = OGLRenderQueue.getInstance();
      var1.lock();

      try {
         OGLContext.invalidateCurrentContext();
      } finally {
         var1.unlock();
      }

   }

   public String toString() {
      int var1 = this.getDevice().getCGDisplayID();
      return "CGLGraphicsConfig[dev=" + var1 + ",pixfmt=" + this.pixfmt + "]";
   }

   public SurfaceData createSurfaceData(CPlatformView var1) {
      return CGLSurfaceData.createData(var1);
   }

   public SurfaceData createSurfaceData(CGLLayer var1) {
      return CGLSurfaceData.createData(var1);
   }

   public Image createAcceleratedImage(Component var1, int var2, int var3) {
      ColorModel var4 = this.getColorModel(1);
      WritableRaster var5 = var4.createCompatibleWritableRaster(var2, var3);
      return new OffScreenImage(var1, var4, var5, var4.isAlphaPremultiplied());
   }

   public void assertOperationSupported(int var1, BufferCapabilities var2) throws AWTException {
      if (var1 != 2) {
         throw new AWTException("Only double buffering is supported");
      } else {
         BufferCapabilities var3 = this.getBufferCapabilities();
         if (!var3.isPageFlipping()) {
            throw new AWTException("Page flipping is not supported");
         } else if (var2.getFlipContents() == BufferCapabilities.FlipContents.PRIOR) {
            throw new AWTException("FlipContents.PRIOR is not supported");
         }
      }
   }

   public Image createBackBuffer(LWComponentPeer<?, ?> var1) {
      Rectangle var2 = var1.getBounds();
      int var3 = Math.max(1, var2.width);
      int var4 = Math.max(1, var2.height);
      int var5 = var1.isTranslucent() ? 3 : 1;
      return new SunVolatileImage(this, var3, var4, var5, (ImageCapabilities)null);
   }

   public void destroyBackBuffer(Image var1) {
      if (var1 != null) {
         var1.flush();
      }

   }

   public void flip(LWComponentPeer<?, ?> var1, Image var2, int var3, int var4, int var5, int var6, BufferCapabilities.FlipContents var7) {
      Graphics var8 = var1.getGraphics();

      try {
         var8.drawImage(var2, var3, var4, var5, var6, var3, var4, var5, var6, (ImageObserver)null);
      } finally {
         var8.dispose();
      }

      if (var7 == BufferCapabilities.FlipContents.BACKGROUND) {
         Graphics2D var9 = (Graphics2D)var2.getGraphics();

         try {
            var9.setBackground(var1.getBackground());
            var9.clearRect(0, 0, var2.getWidth((ImageObserver)null), var2.getHeight((ImageObserver)null));
         } finally {
            var9.dispose();
         }
      }

   }

   public BufferCapabilities getBufferCapabilities() {
      if (this.bufferCaps == null) {
         this.bufferCaps = new CGLGraphicsConfig.CGLBufferCaps(this.isDoubleBuffered());
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
      int var2 = this.getDevice().getCGDisplayID();
      AccelDeviceEventNotifier.addListener(var1, var2);
   }

   public void removeDeviceEventListener(AccelDeviceEventListener var1) {
      AccelDeviceEventNotifier.removeListener(var1);
   }

   public int getMaxTextureWidth() {
      return Math.max(this.maxTextureSize / this.getDevice().getScaleFactor(), this.getBounds().width);
   }

   public int getMaxTextureHeight() {
      return Math.max(this.maxTextureSize / this.getDevice().getScaleFactor(), this.getBounds().height);
   }

   private static class CGLImageCaps extends ImageCapabilities {
      private CGLImageCaps() {
         super(true);
      }

      public boolean isTrueVolatile() {
         return true;
      }

      // $FF: synthetic method
      CGLImageCaps(Object var1) {
         this();
      }
   }

   private static class CGLBufferCaps extends BufferCapabilities {
      public CGLBufferCaps(boolean var1) {
         super(CGLGraphicsConfig.imageCaps, CGLGraphicsConfig.imageCaps, var1 ? BufferCapabilities.FlipContents.UNDEFINED : null);
      }
   }

   private static class CGLGCDisposerRecord implements DisposerRecord {
      private long pCfgInfo;

      public CGLGCDisposerRecord(long var1) {
         this.pCfgInfo = var1;
      }

      public void dispose() {
         if (this.pCfgInfo != 0L) {
            OGLRenderQueue.disposeGraphicsConfig(this.pCfgInfo);
            this.pCfgInfo = 0L;
         }

      }
   }
}
