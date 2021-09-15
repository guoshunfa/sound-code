package sun.java2d.x11;

import java.awt.Composite;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.awt.SunToolkit;
import sun.awt.X11ComponentPeer;
import sun.awt.X11GraphicsConfig;
import sun.awt.image.PixelConverter;
import sun.font.X11TextRenderer;
import sun.java2d.InvalidPipeException;
import sun.java2d.SunGraphics2D;
import sun.java2d.SunGraphicsEnvironment;
import sun.java2d.SurfaceData;
import sun.java2d.SurfaceDataProxy;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.loops.RenderLoops;
import sun.java2d.loops.SurfaceType;
import sun.java2d.loops.XORComposite;
import sun.java2d.pipe.PixelToShapeConverter;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.TextPipe;
import sun.java2d.pipe.ValidatePipe;
import sun.security.action.GetPropertyAction;

public abstract class X11SurfaceData extends XSurfaceData {
   X11ComponentPeer peer;
   X11GraphicsConfig graphicsConfig;
   private RenderLoops solidloops;
   protected int depth;
   public static final String DESC_INT_BGR_X11 = "Integer BGR Pixmap";
   public static final String DESC_INT_RGB_X11 = "Integer RGB Pixmap";
   public static final String DESC_4BYTE_ABGR_PRE_X11 = "4 byte ABGR Pixmap with pre-multplied alpha";
   public static final String DESC_INT_ARGB_PRE_X11 = "Integer ARGB Pixmap with pre-multiplied alpha";
   public static final String DESC_BYTE_IND_OPQ_X11 = "Byte Indexed Opaque Pixmap";
   public static final String DESC_INT_BGR_X11_BM = "Integer BGR Pixmap with 1-bit transp";
   public static final String DESC_INT_RGB_X11_BM = "Integer RGB Pixmap with 1-bit transp";
   public static final String DESC_BYTE_IND_X11_BM = "Byte Indexed Pixmap with 1-bit transp";
   public static final String DESC_BYTE_GRAY_X11 = "Byte Gray Opaque Pixmap";
   public static final String DESC_INDEX8_GRAY_X11 = "Index8 Gray Opaque Pixmap";
   public static final String DESC_BYTE_GRAY_X11_BM = "Byte Gray Opaque Pixmap with 1-bit transp";
   public static final String DESC_INDEX8_GRAY_X11_BM = "Index8 Gray Opaque Pixmap with 1-bit transp";
   public static final String DESC_3BYTE_RGB_X11 = "3 Byte RGB Pixmap";
   public static final String DESC_3BYTE_BGR_X11 = "3 Byte BGR Pixmap";
   public static final String DESC_3BYTE_RGB_X11_BM = "3 Byte RGB Pixmap with 1-bit transp";
   public static final String DESC_3BYTE_BGR_X11_BM = "3 Byte BGR Pixmap with 1-bit transp";
   public static final String DESC_USHORT_555_RGB_X11 = "Ushort 555 RGB Pixmap";
   public static final String DESC_USHORT_565_RGB_X11 = "Ushort 565 RGB Pixmap";
   public static final String DESC_USHORT_555_RGB_X11_BM = "Ushort 555 RGB Pixmap with 1-bit transp";
   public static final String DESC_USHORT_565_RGB_X11_BM = "Ushort 565 RGB Pixmap with 1-bit transp";
   public static final String DESC_USHORT_INDEXED_X11 = "Ushort Indexed Pixmap";
   public static final String DESC_USHORT_INDEXED_X11_BM = "Ushort Indexed Pixmap with 1-bit transp";
   public static final SurfaceType IntBgrX11;
   public static final SurfaceType IntRgbX11;
   public static final SurfaceType FourByteAbgrPreX11;
   public static final SurfaceType IntArgbPreX11;
   public static final SurfaceType ThreeByteRgbX11;
   public static final SurfaceType ThreeByteBgrX11;
   public static final SurfaceType UShort555RgbX11;
   public static final SurfaceType UShort565RgbX11;
   public static final SurfaceType UShortIndexedX11;
   public static final SurfaceType ByteIndexedOpaqueX11;
   public static final SurfaceType ByteGrayX11;
   public static final SurfaceType Index8GrayX11;
   public static final SurfaceType IntBgrX11_BM;
   public static final SurfaceType IntRgbX11_BM;
   public static final SurfaceType ThreeByteRgbX11_BM;
   public static final SurfaceType ThreeByteBgrX11_BM;
   public static final SurfaceType UShort555RgbX11_BM;
   public static final SurfaceType UShort565RgbX11_BM;
   public static final SurfaceType UShortIndexedX11_BM;
   public static final SurfaceType ByteIndexedX11_BM;
   public static final SurfaceType ByteGrayX11_BM;
   public static final SurfaceType Index8GrayX11_BM;
   private static Boolean accelerationEnabled;
   protected X11Renderer x11pipe;
   protected PixelToShapeConverter x11txpipe;
   protected static TextPipe x11textpipe;
   protected static boolean dgaAvailable;
   private long xgc;
   private Region validatedClip;
   private XORComposite validatedXorComp;
   private int xorpixelmod;
   private int validatedPixel;
   private boolean validatedExposures = true;
   private static X11SurfaceData.LazyPipe lazypipe;

   private static native void initIDs(Class var0, boolean var1);

   protected native void initSurface(int var1, int var2, int var3, long var4);

   public Raster getRaster(int var1, int var2, int var3, int var4) {
      throw new InternalError("not implemented yet");
   }

   public static native boolean isDgaAvailable();

   private static native boolean isShmPMAvailable();

   public static boolean isAccelerationEnabled() {
      if (accelerationEnabled == null) {
         if (GraphicsEnvironment.isHeadless()) {
            accelerationEnabled = Boolean.FALSE;
         } else {
            String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.java2d.pmoffscreen")));
            if (var0 != null) {
               accelerationEnabled = Boolean.valueOf(var0);
            } else {
               boolean var1 = false;
               GraphicsEnvironment var2 = GraphicsEnvironment.getLocalGraphicsEnvironment();
               if (var2 instanceof SunGraphicsEnvironment) {
                  var1 = ((SunGraphicsEnvironment)var2).isDisplayLocal();
               }

               accelerationEnabled = !isDgaAvailable() && (!var1 || isShmPMAvailable());
            }
         }
      }

      return accelerationEnabled;
   }

   public SurfaceDataProxy makeProxyFor(SurfaceData var1) {
      return X11SurfaceDataProxy.createProxy(var1, this.graphicsConfig);
   }

   public void validatePipe(SunGraphics2D var1) {
      if (var1.antialiasHint != 2 && var1.paintState <= 1 && (var1.compositeState <= 0 || var1.compositeState == 2)) {
         if (this.x11txpipe == null) {
            var1.drawpipe = lazypipe;
            var1.fillpipe = lazypipe;
            var1.shapepipe = lazypipe;
            var1.imagepipe = lazypipe;
            var1.textpipe = lazypipe;
            return;
         }

         if (var1.clipState == 2) {
            super.validatePipe(var1);
         } else {
            switch(var1.textAntialiasHint) {
            case 0:
            case 1:
               if (var1.compositeState == 0) {
                  var1.textpipe = x11textpipe;
               } else {
                  var1.textpipe = solidTextRenderer;
               }
               break;
            case 2:
               var1.textpipe = aaTextRenderer;
               break;
            default:
               switch(var1.getFontInfo().aaHint) {
               case 1:
                  if (var1.compositeState == 0) {
                     var1.textpipe = x11textpipe;
                  } else {
                     var1.textpipe = solidTextRenderer;
                  }
                  break;
               case 2:
                  var1.textpipe = aaTextRenderer;
                  break;
               case 3:
               case 5:
               default:
                  var1.textpipe = solidTextRenderer;
                  break;
               case 4:
               case 6:
                  var1.textpipe = lcdTextRenderer;
               }
            }
         }

         if (var1.transformState >= 3) {
            var1.drawpipe = this.x11txpipe;
            var1.fillpipe = this.x11txpipe;
         } else if (var1.strokeState != 0) {
            var1.drawpipe = this.x11txpipe;
            var1.fillpipe = this.x11pipe;
         } else {
            var1.drawpipe = this.x11pipe;
            var1.fillpipe = this.x11pipe;
         }

         var1.shapepipe = this.x11pipe;
         var1.imagepipe = imagepipe;
         if (var1.loops == null) {
            var1.loops = this.getRenderLoops(var1);
         }
      } else {
         super.validatePipe(var1);
      }

   }

   public RenderLoops getRenderLoops(SunGraphics2D var1) {
      return var1.paintState <= 1 && var1.compositeState <= 0 ? this.solidloops : super.getRenderLoops(var1);
   }

   public GraphicsConfiguration getDeviceConfiguration() {
      return this.graphicsConfig;
   }

   public static X11SurfaceData.X11WindowSurfaceData createData(X11ComponentPeer var0) {
      X11GraphicsConfig var1 = getGC(var0);
      return new X11SurfaceData.X11WindowSurfaceData(var0, var1, var1.getSurfaceType());
   }

   public static X11SurfaceData.X11PixmapSurfaceData createData(X11GraphicsConfig var0, int var1, int var2, ColorModel var3, Image var4, long var5, int var7) {
      return new X11SurfaceData.X11PixmapSurfaceData(var0, var1, var2, var4, getSurfaceType(var0, var7, true), var3, var5, var7);
   }

   protected X11SurfaceData(X11ComponentPeer var1, X11GraphicsConfig var2, SurfaceType var3, ColorModel var4) {
      super(var3, var4);
      this.peer = var1;
      this.graphicsConfig = var2;
      this.solidloops = this.graphicsConfig.getSolidLoops(var3);
      this.depth = var4.getPixelSize();
      this.initOps(var1, this.graphicsConfig, this.depth);
      if (isAccelerationEnabled()) {
         this.setBlitProxyKey(var2.getProxyKey());
      }

   }

   public static X11GraphicsConfig getGC(X11ComponentPeer var0) {
      if (var0 != null) {
         return (X11GraphicsConfig)var0.getGraphicsConfiguration();
      } else {
         GraphicsEnvironment var1 = GraphicsEnvironment.getLocalGraphicsEnvironment();
         GraphicsDevice var2 = var1.getDefaultScreenDevice();
         return (X11GraphicsConfig)var2.getDefaultConfiguration();
      }
   }

   public abstract boolean canSourceSendExposures(int var1, int var2, int var3, int var4);

   public boolean copyArea(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      if (this.x11pipe == null) {
         if (!this.isDrawableValid()) {
            return true;
         }

         this.makePipes();
      }

      CompositeType var8 = var1.imageComp;
      if (var1.transformState >= 3 || !CompositeType.SrcOverNoEa.equals(var8) && !CompositeType.SrcNoEa.equals(var8)) {
         return false;
      } else {
         var2 += var1.transX;
         var3 += var1.transY;
         SunToolkit.awtLock();

         try {
            boolean var9 = this.canSourceSendExposures(var2, var3, var4, var5);
            long var10 = this.getBlitGC(var1.getCompClip(), var9);
            this.x11pipe.devCopyArea(this.getNativeOps(), var10, var2, var3, var2 + var6, var3 + var7, var4, var5);
         } finally {
            SunToolkit.awtUnlock();
         }

         return true;
      }
   }

   public static SurfaceType getSurfaceType(X11GraphicsConfig var0, int var1) {
      return getSurfaceType(var0, var1, false);
   }

   public static SurfaceType getSurfaceType(X11GraphicsConfig var0, int var1, boolean var2) {
      boolean var3 = var1 == 2;
      ColorModel var5 = var0.getColorModel();
      SurfaceType var4;
      switch(var5.getPixelSize()) {
      case 8:
         if (var5.getColorSpace().getType() == 6 && var5 instanceof ComponentColorModel) {
            var4 = var3 ? ByteGrayX11_BM : ByteGrayX11;
         } else if (var5 instanceof IndexColorModel && isOpaqueGray((IndexColorModel)var5)) {
            var4 = var3 ? Index8GrayX11_BM : Index8GrayX11;
         } else {
            var4 = var3 ? ByteIndexedX11_BM : ByteIndexedOpaqueX11;
         }
         break;
      case 12:
         if (!(var5 instanceof IndexColorModel)) {
            throw new InvalidPipeException("Unsupported bit depth: " + var5.getPixelSize() + " cm=" + var5);
         }

         var4 = var3 ? UShortIndexedX11_BM : UShortIndexedX11;
         break;
      case 15:
         var4 = var3 ? UShort555RgbX11_BM : UShort555RgbX11;
         break;
      case 16:
         if (var5 instanceof DirectColorModel && ((DirectColorModel)var5).getGreenMask() == 992) {
            var4 = var3 ? UShort555RgbX11_BM : UShort555RgbX11;
         } else {
            var4 = var3 ? UShort565RgbX11_BM : UShort565RgbX11;
         }
         break;
      case 24:
         if (var0.getBitsPerPixel() == 24) {
            if (!(var5 instanceof DirectColorModel)) {
               throw new InvalidPipeException("Unsupported bit depth/cm combo: " + var5.getPixelSize() + ", " + var5);
            }

            var4 = var3 ? ThreeByteBgrX11_BM : ThreeByteBgrX11;
            break;
         }
      case 32:
         if (var5 instanceof DirectColorModel) {
            if (((SunToolkit)Toolkit.getDefaultToolkit()).isTranslucencyCapable(var0) && !var2) {
               var4 = IntArgbPreX11;
            } else if (((DirectColorModel)var5).getRedMask() == 16711680) {
               var4 = var3 ? IntRgbX11_BM : IntRgbX11;
            } else {
               var4 = var3 ? IntBgrX11_BM : IntBgrX11;
            }
         } else {
            if (!(var5 instanceof ComponentColorModel)) {
               throw new InvalidPipeException("Unsupported bit depth/cm combo: " + var5.getPixelSize() + ", " + var5);
            }

            var4 = FourByteAbgrPreX11;
         }
         break;
      default:
         throw new InvalidPipeException("Unsupported bit depth: " + var5.getPixelSize());
      }

      return var4;
   }

   public void invalidate() {
      if (this.isValid()) {
         this.setInvalid();
         super.invalidate();
      }

   }

   private static native void XSetCopyMode(long var0);

   private static native void XSetXorMode(long var0);

   private static native void XSetForeground(long var0, int var2);

   public final long getRenderGC(Region var1, int var2, Composite var3, int var4) {
      return this.getGC(var1, var2, var3, var4, this.validatedExposures);
   }

   public final long getBlitGC(Region var1, boolean var2) {
      return this.getGC(var1, 0, (Composite)null, this.validatedPixel, var2);
   }

   final long getGC(Region var1, int var2, Composite var3, int var4, boolean var5) {
      if (!this.isValid()) {
         throw new InvalidPipeException("bounds changed");
      } else {
         if (var1 != this.validatedClip) {
            this.validatedClip = var1;
            if (var1 != null) {
               XSetClip(this.xgc, var1.getLoX(), var1.getLoY(), var1.getHiX(), var1.getHiY(), var1.isRectangular() ? null : var1);
            } else {
               XResetClip(this.xgc);
            }
         }

         if (var2 == 0) {
            if (this.validatedXorComp != null) {
               this.validatedXorComp = null;
               this.xorpixelmod = 0;
               XSetCopyMode(this.xgc);
            }
         } else if (this.validatedXorComp != var3) {
            this.validatedXorComp = (XORComposite)var3;
            this.xorpixelmod = this.validatedXorComp.getXorPixel();
            XSetXorMode(this.xgc);
         }

         var4 ^= this.xorpixelmod;
         if (var4 != this.validatedPixel) {
            this.validatedPixel = var4;
            XSetForeground(this.xgc, var4);
         }

         if (this.validatedExposures != var5) {
            this.validatedExposures = var5;
            XSetGraphicsExposures(this.xgc, var5);
         }

         return this.xgc;
      }
   }

   public synchronized void makePipes() {
      if (this.x11pipe == null) {
         SunToolkit.awtLock();

         try {
            this.xgc = XCreateGC(this.getNativeOps());
         } finally {
            SunToolkit.awtUnlock();
         }

         this.x11pipe = X11Renderer.getInstance();
         this.x11txpipe = new PixelToShapeConverter(this.x11pipe);
      }

   }

   static {
      IntBgrX11 = SurfaceType.IntBgr.deriveSubType("Integer BGR Pixmap");
      IntRgbX11 = SurfaceType.IntRgb.deriveSubType("Integer RGB Pixmap");
      FourByteAbgrPreX11 = SurfaceType.FourByteAbgrPre.deriveSubType("4 byte ABGR Pixmap with pre-multplied alpha");
      IntArgbPreX11 = SurfaceType.IntArgbPre.deriveSubType("Integer ARGB Pixmap with pre-multiplied alpha");
      ThreeByteRgbX11 = SurfaceType.ThreeByteRgb.deriveSubType("3 Byte RGB Pixmap");
      ThreeByteBgrX11 = SurfaceType.ThreeByteBgr.deriveSubType("3 Byte BGR Pixmap");
      UShort555RgbX11 = SurfaceType.Ushort555Rgb.deriveSubType("Ushort 555 RGB Pixmap");
      UShort565RgbX11 = SurfaceType.Ushort565Rgb.deriveSubType("Ushort 565 RGB Pixmap");
      UShortIndexedX11 = SurfaceType.UshortIndexed.deriveSubType("Ushort Indexed Pixmap");
      ByteIndexedOpaqueX11 = SurfaceType.ByteIndexedOpaque.deriveSubType("Byte Indexed Opaque Pixmap");
      ByteGrayX11 = SurfaceType.ByteGray.deriveSubType("Byte Gray Opaque Pixmap");
      Index8GrayX11 = SurfaceType.Index8Gray.deriveSubType("Index8 Gray Opaque Pixmap");
      IntBgrX11_BM = SurfaceType.Custom.deriveSubType("Integer BGR Pixmap with 1-bit transp", PixelConverter.Xbgr.instance);
      IntRgbX11_BM = SurfaceType.Custom.deriveSubType("Integer RGB Pixmap with 1-bit transp", PixelConverter.Xrgb.instance);
      ThreeByteRgbX11_BM = SurfaceType.Custom.deriveSubType("3 Byte RGB Pixmap with 1-bit transp", PixelConverter.Xbgr.instance);
      ThreeByteBgrX11_BM = SurfaceType.Custom.deriveSubType("3 Byte BGR Pixmap with 1-bit transp", PixelConverter.Xrgb.instance);
      UShort555RgbX11_BM = SurfaceType.Custom.deriveSubType("Ushort 555 RGB Pixmap with 1-bit transp", PixelConverter.Ushort555Rgb.instance);
      UShort565RgbX11_BM = SurfaceType.Custom.deriveSubType("Ushort 565 RGB Pixmap with 1-bit transp", PixelConverter.Ushort565Rgb.instance);
      UShortIndexedX11_BM = SurfaceType.Custom.deriveSubType("Ushort Indexed Pixmap with 1-bit transp");
      ByteIndexedX11_BM = SurfaceType.Custom.deriveSubType("Byte Indexed Pixmap with 1-bit transp");
      ByteGrayX11_BM = SurfaceType.Custom.deriveSubType("Byte Gray Opaque Pixmap with 1-bit transp");
      Index8GrayX11_BM = SurfaceType.Custom.deriveSubType("Index8 Gray Opaque Pixmap with 1-bit transp");
      accelerationEnabled = null;
      if (!isX11SurfaceDataInitialized() && !GraphicsEnvironment.isHeadless()) {
         String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("javax.accessibility.screen_magnifier_present")));
         boolean var1 = var0 == null || !"true".equals(var0);
         initIDs(XORComposite.class, var1);
         String var2 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.java2d.xtextpipe")));
         if (var2 != null && !"true".startsWith(var2)) {
            if ("false".equals(var2)) {
               System.out.println("using DGA text renderer");
            }

            x11textpipe = solidTextRenderer;
         } else {
            if ("true".equals(var2)) {
               System.out.println("using X11 text renderer");
            }

            x11textpipe = new X11TextRenderer();
            if (GraphicsPrimitive.tracingEnabled()) {
               x11textpipe = ((X11TextRenderer)x11textpipe).traceWrap();
            }
         }

         dgaAvailable = isDgaAvailable();
         if (isAccelerationEnabled()) {
            X11PMBlitLoops.register();
            X11PMBlitBgLoops.register();
         }
      }

      lazypipe = new X11SurfaceData.LazyPipe();
   }

   public static class LazyPipe extends ValidatePipe {
      public boolean validate(SunGraphics2D var1) {
         X11SurfaceData var2 = (X11SurfaceData)var1.surfaceData;
         if (!var2.isDrawableValid()) {
            return false;
         } else {
            var2.makePipes();
            return super.validate(var1);
         }
      }
   }

   public static class X11PixmapSurfaceData extends X11SurfaceData {
      Image offscreenImage;
      int width;
      int height;
      int transparency;

      public X11PixmapSurfaceData(X11GraphicsConfig var1, int var2, int var3, Image var4, SurfaceType var5, ColorModel var6, long var7, int var9) {
         super((X11ComponentPeer)null, var1, var5, var6);
         this.width = var2;
         this.height = var3;
         this.offscreenImage = var4;
         this.transparency = var9;
         this.initSurface(this.depth, var2, var3, var7);
         this.makePipes();
      }

      public SurfaceData getReplacement() {
         return restoreContents(this.offscreenImage);
      }

      public int getTransparency() {
         return this.transparency;
      }

      public Rectangle getBounds() {
         return new Rectangle(this.width, this.height);
      }

      public boolean canSourceSendExposures(int var1, int var2, int var3, int var4) {
         return var1 < 0 || var2 < 0 || var1 + var3 > this.width || var2 + var4 > this.height;
      }

      public void flush() {
         this.invalidate();
         this.flushNativeSurface();
      }

      public Object getDestination() {
         return this.offscreenImage;
      }
   }

   public static class X11WindowSurfaceData extends X11SurfaceData {
      public X11WindowSurfaceData(X11ComponentPeer var1, X11GraphicsConfig var2, SurfaceType var3) {
         super(var1, var2, var3, var1.getColorModel());
         if (this.isDrawableValid()) {
            this.makePipes();
         }

      }

      public SurfaceData getReplacement() {
         return this.peer.getSurfaceData();
      }

      public Rectangle getBounds() {
         Rectangle var1 = this.peer.getBounds();
         var1.x = var1.y = 0;
         return var1;
      }

      public boolean canSourceSendExposures(int var1, int var2, int var3, int var4) {
         return true;
      }

      public Object getDestination() {
         return this.peer.getTarget();
      }
   }
}
