package sun.awt;

import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.awt.ImageCapabilities;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.VolatileImage;
import java.awt.image.WritableRaster;
import sun.awt.image.OffScreenImage;
import sun.awt.image.SunVolatileImage;
import sun.awt.image.SurfaceManager;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.RenderLoops;
import sun.java2d.loops.SurfaceType;
import sun.java2d.x11.X11SurfaceData;

public class X11GraphicsConfig extends GraphicsConfiguration implements SurfaceManager.ProxiedGraphicsConfig {
   protected X11GraphicsDevice screen;
   protected int visual;
   int depth;
   int colormap;
   ColorModel colorModel;
   long aData;
   boolean doubleBuffer;
   private Object disposerReferent = new Object();
   private BufferCapabilities bufferCaps;
   private static ImageCapabilities imageCaps = new ImageCapabilities(X11SurfaceData.isAccelerationEnabled());
   protected int bitsPerPixel;
   protected SurfaceType surfaceType;
   public RenderLoops solidloops;

   public static X11GraphicsConfig getConfig(X11GraphicsDevice var0, int var1, int var2, int var3, boolean var4) {
      return new X11GraphicsConfig(var0, var1, var2, var3, var4);
   }

   public static X11GraphicsConfig getConfig(X11GraphicsDevice var0, int var1, int var2, int var3, int var4) {
      return new X11GraphicsConfig(var0, var1, var2, var3, false);
   }

   private native int getNumColors();

   private native void init(int var1, int var2);

   private native ColorModel makeColorModel();

   protected X11GraphicsConfig(X11GraphicsDevice var1, int var2, int var3, int var4, boolean var5) {
      this.screen = var1;
      this.visual = var2;
      this.doubleBuffer = var5;
      this.depth = var3;
      this.colormap = var4;
      this.init(var2, this.screen.getScreen());
      long var6 = this.getAData();
      Disposer.addRecord(this.disposerReferent, new X11GraphicsConfig.X11GCDisposerRecord(var6));
   }

   public GraphicsDevice getDevice() {
      return this.screen;
   }

   public int getVisual() {
      return this.visual;
   }

   public int getDepth() {
      return this.depth;
   }

   public int getColormap() {
      return this.colormap;
   }

   public int getBitsPerPixel() {
      return this.bitsPerPixel;
   }

   public synchronized SurfaceType getSurfaceType() {
      if (this.surfaceType != null) {
         return this.surfaceType;
      } else {
         this.surfaceType = X11SurfaceData.getSurfaceType(this, 1);
         return this.surfaceType;
      }
   }

   public Object getProxyKey() {
      return this.screen.getProxyKeyFor(this.getSurfaceType());
   }

   public synchronized RenderLoops getSolidLoops(SurfaceType var1) {
      if (this.solidloops == null) {
         this.solidloops = SurfaceData.makeRenderLoops(SurfaceType.OpaqueColor, CompositeType.SrcNoEa, var1);
      }

      return this.solidloops;
   }

   public synchronized ColorModel getColorModel() {
      if (this.colorModel == null) {
         SystemColor.window.getRGB();
         this.colorModel = this.makeColorModel();
         if (this.colorModel == null) {
            this.colorModel = Toolkit.getDefaultToolkit().getColorModel();
         }
      }

      return this.colorModel;
   }

   public ColorModel getColorModel(int var1) {
      switch(var1) {
      case 1:
         return this.getColorModel();
      case 2:
         return new DirectColorModel(25, 16711680, 65280, 255, 16777216);
      case 3:
         return ColorModel.getRGBdefault();
      default:
         return null;
      }
   }

   public static DirectColorModel createDCM32(int var0, int var1, int var2, int var3, boolean var4) {
      return new DirectColorModel(ColorSpace.getInstance(1000), 32, var0, var1, var2, var3, var4, 3);
   }

   public static ComponentColorModel createABGRCCM() {
      ColorSpace var0 = ColorSpace.getInstance(1000);
      int[] var1 = new int[]{8, 8, 8, 8};
      int[] var10000 = new int[]{3, 2, 1, 0};
      return new ComponentColorModel(var0, var1, true, true, 3, 0);
   }

   public AffineTransform getDefaultTransform() {
      return new AffineTransform();
   }

   public AffineTransform getNormalizingTransform() {
      double var1 = this.getXResolution(this.screen.getScreen()) / 72.0D;
      double var3 = this.getYResolution(this.screen.getScreen()) / 72.0D;
      return new AffineTransform(var1, 0.0D, 0.0D, var3, 0.0D, 0.0D);
   }

   private native double getXResolution(int var1);

   private native double getYResolution(int var1);

   public long getAData() {
      return this.aData;
   }

   public String toString() {
      return "X11GraphicsConfig[dev=" + this.screen + ",vis=0x" + Integer.toHexString(this.visual) + "]";
   }

   private static native void initIDs();

   public Rectangle getBounds() {
      return this.pGetBounds(this.screen.getScreen());
   }

   public native Rectangle pGetBounds(int var1);

   public BufferCapabilities getBufferCapabilities() {
      if (this.bufferCaps == null) {
         if (this.doubleBuffer) {
            this.bufferCaps = new X11GraphicsConfig.XDBECapabilities();
         } else {
            this.bufferCaps = super.getBufferCapabilities();
         }
      }

      return this.bufferCaps;
   }

   public ImageCapabilities getImageCapabilities() {
      return imageCaps;
   }

   public boolean isDoubleBuffered() {
      return this.doubleBuffer;
   }

   private static native void dispose(long var0);

   public SurfaceData createSurfaceData(X11ComponentPeer var1) {
      return X11SurfaceData.createData(var1);
   }

   public Image createAcceleratedImage(Component var1, int var2, int var3) {
      ColorModel var4 = this.getColorModel(1);
      WritableRaster var5 = var4.createCompatibleWritableRaster(var2, var3);
      return new OffScreenImage(var1, var4, var5, var4.isAlphaPremultiplied());
   }

   private native long createBackBuffer(long var1, int var3);

   private native void swapBuffers(long var1, int var3);

   public long createBackBuffer(X11ComponentPeer var1, int var2, BufferCapabilities var3) throws AWTException {
      if (!X11GraphicsDevice.isDBESupported()) {
         throw new AWTException("Page flipping is not supported");
      } else if (var2 > 2) {
         throw new AWTException("Only double or single buffering is supported");
      } else {
         BufferCapabilities var4 = this.getBufferCapabilities();
         if (!var4.isPageFlipping()) {
            throw new AWTException("Page flipping is not supported");
         } else {
            long var5 = var1.getContentWindow();
            int var7 = getSwapAction(var3.getFlipContents());
            return this.createBackBuffer(var5, var7);
         }
      }
   }

   public native void destroyBackBuffer(long var1);

   public VolatileImage createBackBufferImage(Component var1, long var2) {
      int var4 = Math.max(1, var1.getWidth());
      int var5 = Math.max(1, var1.getHeight());
      return new SunVolatileImage(var1, var4, var5, var2);
   }

   public void flip(X11ComponentPeer var1, Component var2, VolatileImage var3, int var4, int var5, int var6, int var7, BufferCapabilities.FlipContents var8) {
      long var9 = var1.getContentWindow();
      int var11 = getSwapAction(var8);
      this.swapBuffers(var9, var11);
   }

   private static int getSwapAction(BufferCapabilities.FlipContents var0) {
      if (var0 == BufferCapabilities.FlipContents.BACKGROUND) {
         return 1;
      } else if (var0 == BufferCapabilities.FlipContents.PRIOR) {
         return 2;
      } else {
         return var0 == BufferCapabilities.FlipContents.COPIED ? 3 : 0;
      }
   }

   public boolean isTranslucencyCapable() {
      return this.isTranslucencyCapable(this.getAData());
   }

   private native boolean isTranslucencyCapable(long var1);

   static {
      initIDs();
   }

   private static class X11GCDisposerRecord implements DisposerRecord {
      private long x11ConfigData;

      public X11GCDisposerRecord(long var1) {
         this.x11ConfigData = var1;
      }

      public synchronized void dispose() {
         if (this.x11ConfigData != 0L) {
            X11GraphicsConfig.dispose(this.x11ConfigData);
            this.x11ConfigData = 0L;
         }

      }
   }

   private static class XDBECapabilities extends BufferCapabilities {
      public XDBECapabilities() {
         super(X11GraphicsConfig.imageCaps, X11GraphicsConfig.imageCaps, BufferCapabilities.FlipContents.UNDEFINED);
      }
   }
}
