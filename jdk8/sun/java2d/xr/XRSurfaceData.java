package sun.java2d.xr;

import java.awt.AlphaComposite;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import sun.awt.SunToolkit;
import sun.awt.X11ComponentPeer;
import sun.font.FontManagerNativeLibrary;
import sun.java2d.InvalidPipeException;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.SurfaceDataProxy;
import sun.java2d.jules.JulesPathBuf;
import sun.java2d.jules.JulesShapePipe;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.MaskFill;
import sun.java2d.loops.RenderLoops;
import sun.java2d.loops.SurfaceType;
import sun.java2d.loops.XORComposite;
import sun.java2d.pipe.PixelToShapeConverter;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.ShapeDrawPipe;
import sun.java2d.pipe.TextPipe;
import sun.java2d.pipe.ValidatePipe;
import sun.java2d.x11.XSurfaceData;

public abstract class XRSurfaceData extends XSurfaceData {
   X11ComponentPeer peer;
   XRGraphicsConfig graphicsConfig;
   XRBackend renderQueue;
   private RenderLoops solidloops;
   protected int depth;
   public static final String DESC_BYTE_A8_X11 = "Byte A8 Pixmap";
   public static final String DESC_INT_RGB_X11 = "Integer RGB Pixmap";
   public static final String DESC_INT_ARGB_X11 = "Integer ARGB-Pre Pixmap";
   public static final SurfaceType ByteA8X11;
   public static final SurfaceType IntRgbX11;
   public static final SurfaceType IntArgbPreX11;
   protected XRRenderer xrpipe;
   protected PixelToShapeConverter xrtxpipe;
   protected TextPipe xrtextpipe;
   protected XRDrawImage xrDrawImage;
   protected ShapeDrawPipe aaShapePipe;
   protected PixelToShapeConverter aaPixelToShapeConv;
   private long xgc;
   private int validatedGCForegroundPixel = 0;
   private XORComposite validatedXorComp;
   private int xid;
   public int picture;
   public XRCompositeManager maskBuffer;
   private Region validatedClip;
   private Region validatedGCClip;
   private boolean validatedExposures = true;
   boolean transformInUse = false;
   AffineTransform validatedSourceTransform = new AffineTransform();
   AffineTransform staticSrcTx = null;
   int validatedRepeat = 0;
   int validatedFilter = 0;

   private static native void initIDs();

   protected native void XRInitSurface(int var1, int var2, int var3, long var4, int var6);

   native void initXRPicture(long var1, int var3);

   native void freeXSDOPicture(long var1);

   public Raster getRaster(int var1, int var2, int var3, int var4) {
      throw new InternalError("not implemented yet");
   }

   public static void initXRSurfaceData() {
      if (!isX11SurfaceDataInitialized()) {
         FontManagerNativeLibrary.load();
         initIDs();
         XRPMBlitLoops.register();
         XRMaskFill.register();
         XRMaskBlit.register();
         setX11SurfaceDataInitialized();
      }

   }

   protected boolean isXRDrawableValid() {
      boolean var1;
      try {
         SunToolkit.awtLock();
         var1 = this.isDrawableValid();
      } finally {
         SunToolkit.awtUnlock();
      }

      return var1;
   }

   public SurfaceDataProxy makeProxyFor(SurfaceData var1) {
      return XRSurfaceDataProxy.createProxy(var1, this.graphicsConfig);
   }

   public void validatePipe(SunGraphics2D var1) {
      boolean var3 = false;
      TextPipe var2;
      if ((var2 = this.getTextPipe(var1)) == null) {
         super.validatePipe(var1);
         var2 = var1.textpipe;
         var3 = true;
      }

      PixelToShapeConverter var4 = null;
      XRRenderer var5 = null;
      if (var1.antialiasHint != 2) {
         if (var1.paintState <= 1) {
            if (var1.compositeState <= 2) {
               var4 = this.xrtxpipe;
               var5 = this.xrpipe;
            }
         } else if (var1.compositeState <= 1 && XRPaints.isValid(var1)) {
            var4 = this.xrtxpipe;
            var5 = this.xrpipe;
         }
      }

      if (var1.antialiasHint == 2 && JulesPathBuf.isCairoAvailable()) {
         var1.shapepipe = this.aaShapePipe;
         var1.drawpipe = this.aaPixelToShapeConv;
         var1.fillpipe = this.aaPixelToShapeConv;
      } else if (var4 != null) {
         if (var1.transformState >= 3) {
            var1.drawpipe = var4;
            var1.fillpipe = var4;
         } else if (var1.strokeState != 0) {
            var1.drawpipe = var4;
            var1.fillpipe = var5;
         } else {
            var1.drawpipe = var5;
            var1.fillpipe = var5;
         }

         var1.shapepipe = var5;
      } else if (!var3) {
         super.validatePipe(var1);
      }

      var1.textpipe = var2;
      var1.imagepipe = this.xrDrawImage;
   }

   protected TextPipe getTextPipe(SunGraphics2D var1) {
      boolean var2 = var1.compositeState <= 1 && (var1.paintState <= 1 || var1.composite == null);
      boolean var3 = false;
      if (var1.composite instanceof AlphaComposite) {
         int var4 = ((AlphaComposite)var1.composite).getRule();
         var3 = XRUtils.isMaskEvaluated(XRUtils.j2dAlphaCompToXR(var4)) || var4 == 2 && var1.paintState <= 1;
      }

      return var2 && var3 ? this.xrtextpipe : null;
   }

   protected MaskFill getMaskFill(SunGraphics2D var1) {
      AlphaComposite var2 = null;
      if (var1.composite != null && var1.composite instanceof AlphaComposite) {
         var2 = (AlphaComposite)var1.composite;
      }

      boolean var3 = var1.paintState <= 1 || XRPaints.isValid(var1);
      boolean var4 = false;
      if (var2 != null) {
         int var5 = var2.getRule();
         var4 = XRUtils.isMaskEvaluated(XRUtils.j2dAlphaCompToXR(var5));
      }

      return var3 && var4 ? super.getMaskFill(var1) : null;
   }

   public RenderLoops getRenderLoops(SunGraphics2D var1) {
      return var1.paintState <= 1 && var1.compositeState <= 1 ? this.solidloops : super.getRenderLoops(var1);
   }

   public GraphicsConfiguration getDeviceConfiguration() {
      return this.graphicsConfig;
   }

   public static XRSurfaceData.XRWindowSurfaceData createData(X11ComponentPeer var0) {
      XRGraphicsConfig var1 = getGC(var0);
      return new XRSurfaceData.XRWindowSurfaceData(var0, var1, var1.getSurfaceType());
   }

   public static XRSurfaceData.XRPixmapSurfaceData createData(XRGraphicsConfig var0, int var1, int var2, ColorModel var3, Image var4, long var5, int var7) {
      int var8;
      if (var0.getColorModel().getPixelSize() == 32) {
         var8 = 32;
         var7 = 3;
      } else {
         var8 = var7 > 1 ? 32 : 24;
      }

      DirectColorModel var9;
      if (var8 == 24) {
         var9 = new DirectColorModel(var8, 16711680, 65280, 255);
      } else {
         var9 = new DirectColorModel(var8, 16711680, 65280, 255, -16777216);
      }

      return new XRSurfaceData.XRPixmapSurfaceData(var0, var1, var2, var4, getSurfaceType(var0, var7), var9, var5, var7, XRUtils.getPictureFormatForTransparency(var7), var8);
   }

   protected XRSurfaceData(X11ComponentPeer var1, XRGraphicsConfig var2, SurfaceType var3, ColorModel var4, int var5, int var6) {
      super(var3, var4);
      this.peer = var1;
      this.graphicsConfig = var2;
      this.solidloops = this.graphicsConfig.getSolidLoops(var3);
      this.depth = var5;
      this.initOps(var1, this.graphicsConfig, var5);
      this.setBlitProxyKey(var2.getProxyKey());
   }

   protected XRSurfaceData(XRBackend var1) {
      super(IntRgbX11, new DirectColorModel(24, 16711680, 65280, 255));
      this.renderQueue = var1;
   }

   public void initXRender(int var1) {
      try {
         SunToolkit.awtLock();
         this.initXRPicture(this.getNativeOps(), var1);
         this.renderQueue = XRCompositeManager.getInstance(this).getBackend();
         this.maskBuffer = XRCompositeManager.getInstance(this);
      } catch (Throwable var6) {
         var6.printStackTrace();
      } finally {
         SunToolkit.awtUnlock();
      }

   }

   public static XRGraphicsConfig getGC(X11ComponentPeer var0) {
      if (var0 != null) {
         return (XRGraphicsConfig)var0.getGraphicsConfiguration();
      } else {
         GraphicsEnvironment var1 = GraphicsEnvironment.getLocalGraphicsEnvironment();
         GraphicsDevice var2 = var1.getDefaultScreenDevice();
         return (XRGraphicsConfig)var2.getDefaultConfiguration();
      }
   }

   public abstract boolean canSourceSendExposures(int var1, int var2, int var3, int var4);

   public void validateCopyAreaGC(Region var1, boolean var2) {
      if (this.validatedGCClip != var1) {
         if (var1 != null) {
            this.renderQueue.setGCClipRectangles(this.xgc, var1);
         }

         this.validatedGCClip = var1;
      }

      if (this.validatedExposures != var2) {
         this.validatedExposures = var2;
         this.renderQueue.setGCExposures(this.xgc, var2);
      }

      if (this.validatedXorComp != null) {
         this.renderQueue.setGCMode(this.xgc, true);
         this.renderQueue.setGCForeground(this.xgc, this.validatedGCForegroundPixel);
         this.validatedXorComp = null;
      }

   }

   public boolean copyArea(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      if (this.xrpipe == null) {
         if (!this.isXRDrawableValid()) {
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

         try {
            SunToolkit.awtLock();
            boolean var9 = this.canSourceSendExposures(var2, var3, var4, var5);
            this.validateCopyAreaGC(var1.getCompClip(), var9);
            this.renderQueue.copyArea(this.xid, this.xid, this.xgc, var2, var3, var4, var5, var2 + var6, var3 + var7);
         } finally {
            SunToolkit.awtUnlock();
         }

         return true;
      }
   }

   public static SurfaceType getSurfaceType(XRGraphicsConfig var0, int var1) {
      SurfaceType var2 = null;
      switch(var1) {
      case 1:
         var2 = IntRgbX11;
         break;
      case 2:
      case 3:
         var2 = IntArgbPreX11;
      }

      return var2;
   }

   public void invalidate() {
      if (this.isValid()) {
         this.setInvalid();
         super.invalidate();
      }

   }

   void validateAsSource(AffineTransform var1, int var2, int var3) {
      if (this.validatedClip != null) {
         this.validatedClip = null;
         this.renderQueue.setClipRectangles(this.picture, (Region)null);
      }

      if (this.validatedRepeat != var2 && var2 != -1) {
         this.validatedRepeat = var2;
         this.renderQueue.setPictureRepeat(this.picture, var2);
      }

      if (var1 == null) {
         if (this.transformInUse) {
            this.validatedSourceTransform.setToIdentity();
            this.renderQueue.setPictureTransform(this.picture, this.validatedSourceTransform);
            this.transformInUse = false;
         }
      } else if (!this.transformInUse || this.transformInUse && !var1.equals(this.validatedSourceTransform)) {
         this.validatedSourceTransform.setTransform(var1.getScaleX(), var1.getShearY(), var1.getShearX(), var1.getScaleY(), var1.getTranslateX(), var1.getTranslateY());
         AffineTransform var4 = this.validatedSourceTransform;
         if (this.staticSrcTx != null) {
            var4 = new AffineTransform(this.validatedSourceTransform);
            var4.preConcatenate(this.staticSrcTx);
         }

         this.renderQueue.setPictureTransform(this.picture, var4);
         this.transformInUse = true;
      }

      if (var3 != this.validatedFilter && var3 != -1) {
         this.renderQueue.setFilter(this.picture, var3);
         this.validatedFilter = var3;
      }

   }

   public void validateAsDestination(SunGraphics2D var1, Region var2) {
      if (!this.isValid()) {
         throw new InvalidPipeException("bounds changed");
      } else {
         boolean var3 = false;
         if (var2 != this.validatedClip) {
            this.renderQueue.setClipRectangles(this.picture, var2);
            this.validatedClip = var2;
            var3 = true;
         }

         if (var1 != null && var1.compositeState == 2) {
            if (this.validatedXorComp != var1.getComposite()) {
               this.validatedXorComp = (XORComposite)var1.getComposite();
               this.renderQueue.setGCMode(this.xgc, false);
            }

            int var4 = var1.pixel;
            if (this.validatedGCForegroundPixel != var4) {
               int var5 = this.validatedXorComp.getXorPixel();
               this.renderQueue.setGCForeground(this.xgc, var4 ^ var5);
               this.validatedGCForegroundPixel = var4;
            }

            if (var3) {
               this.renderQueue.setGCClipRectangles(this.xgc, var2);
            }
         }

      }
   }

   public synchronized void makePipes() {
      if (this.xrpipe == null) {
         try {
            SunToolkit.awtLock();
            this.xgc = XCreateGC(this.getNativeOps());
            this.xrpipe = new XRRenderer(this.maskBuffer.getMaskBuffer());
            this.xrtxpipe = new PixelToShapeConverter(this.xrpipe);
            this.xrtextpipe = this.maskBuffer.getTextRenderer();
            this.xrDrawImage = new XRDrawImage();
            if (JulesPathBuf.isCairoAvailable()) {
               this.aaShapePipe = new JulesShapePipe(XRCompositeManager.getInstance(this));
               this.aaPixelToShapeConv = new PixelToShapeConverter(this.aaShapePipe);
            }
         } finally {
            SunToolkit.awtUnlock();
         }
      }

   }

   public long getGC() {
      return this.xgc;
   }

   public int getPicture() {
      return this.picture;
   }

   public int getXid() {
      return this.xid;
   }

   public XRGraphicsConfig getGraphicsConfig() {
      return this.graphicsConfig;
   }

   public void setStaticSrcTx(AffineTransform var1) {
      this.staticSrcTx = var1;
   }

   static {
      ByteA8X11 = SurfaceType.ByteGray.deriveSubType("Byte A8 Pixmap");
      IntRgbX11 = SurfaceType.IntRgb.deriveSubType("Integer RGB Pixmap");
      IntArgbPreX11 = SurfaceType.IntArgbPre.deriveSubType("Integer ARGB-Pre Pixmap");
   }

   public static class LazyPipe extends ValidatePipe {
      public boolean validate(SunGraphics2D var1) {
         XRSurfaceData var2 = (XRSurfaceData)var1.surfaceData;
         if (!var2.isXRDrawableValid()) {
            return false;
         } else {
            var2.makePipes();
            return super.validate(var1);
         }
      }
   }

   public static class XRPixmapSurfaceData extends XRSurfaceData {
      Image offscreenImage;
      int width;
      int height;
      int transparency;

      public XRPixmapSurfaceData(XRGraphicsConfig var1, int var2, int var3, Image var4, SurfaceType var5, ColorModel var6, long var7, int var9, int var10, int var11) {
         super((X11ComponentPeer)null, var1, var5, var6, var11, var9);
         this.width = var2;
         this.height = var3;
         this.offscreenImage = var4;
         this.transparency = var9;
         this.initSurface(var11, var2, var3, var7, var10);
         this.initXRender(var10);
         this.makePipes();
      }

      public void initSurface(int var1, int var2, int var3, long var4, int var6) {
         try {
            SunToolkit.awtLock();
            this.XRInitSurface(var1, var2, var3, var4, var6);
         } finally {
            SunToolkit.awtUnlock();
         }

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

   public static class XRInternalSurfaceData extends XRSurfaceData {
      public XRInternalSurfaceData(XRBackend var1, int var2) {
         super(var1);
         this.picture = var2;
         this.transformInUse = false;
      }

      public boolean canSourceSendExposures(int var1, int var2, int var3, int var4) {
         return false;
      }

      public Rectangle getBounds() {
         return null;
      }

      public Object getDestination() {
         return null;
      }

      public SurfaceData getReplacement() {
         return null;
      }
   }

   public static class XRWindowSurfaceData extends XRSurfaceData {
      public XRWindowSurfaceData(X11ComponentPeer var1, XRGraphicsConfig var2, SurfaceType var3) {
         super(var1, var2, var3, var1.getColorModel(), var1.getColorModel().getPixelSize(), 1);
         if (this.isXRDrawableValid()) {
            this.initXRender(XRUtils.getPictureFormatForTransparency(1));
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

      public void invalidate() {
         try {
            SunToolkit.awtLock();
            this.freeXSDOPicture(this.getNativeOps());
         } finally {
            SunToolkit.awtUnlock();
         }

         super.invalidate();
      }
   }
}
