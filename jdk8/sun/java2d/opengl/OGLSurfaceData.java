package sun.java2d.opengl;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.awt.image.PixelConverter;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.SurfaceDataProxy;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.loops.MaskFill;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.ParallelogramPipe;
import sun.java2d.pipe.PixelToParallelogramConverter;
import sun.java2d.pipe.RenderBuffer;
import sun.java2d.pipe.TextPipe;
import sun.java2d.pipe.hw.AccelSurface;
import sun.security.action.GetPropertyAction;

public abstract class OGLSurfaceData extends SurfaceData implements AccelSurface {
   public static final int PBUFFER = 2;
   public static final int FBOBJECT = 5;
   public static final int PF_INT_ARGB = 0;
   public static final int PF_INT_ARGB_PRE = 1;
   public static final int PF_INT_RGB = 2;
   public static final int PF_INT_RGBX = 3;
   public static final int PF_INT_BGR = 4;
   public static final int PF_INT_BGRX = 5;
   public static final int PF_USHORT_565_RGB = 6;
   public static final int PF_USHORT_555_RGB = 7;
   public static final int PF_USHORT_555_RGBX = 8;
   public static final int PF_BYTE_GRAY = 9;
   public static final int PF_USHORT_GRAY = 10;
   public static final int PF_3BYTE_BGR = 11;
   private static final String DESC_OPENGL_SURFACE = "OpenGL Surface";
   private static final String DESC_OPENGL_SURFACE_RTT = "OpenGL Surface (render-to-texture)";
   private static final String DESC_OPENGL_TEXTURE = "OpenGL Texture";
   static final SurfaceType OpenGLSurface;
   static final SurfaceType OpenGLSurfaceRTT;
   static final SurfaceType OpenGLTexture;
   private static boolean isFBObjectEnabled;
   private static boolean isLCDShaderEnabled;
   private static boolean isBIOpShaderEnabled;
   private static boolean isGradShaderEnabled;
   private OGLGraphicsConfig graphicsConfig;
   protected int type;
   private int nativeWidth;
   private int nativeHeight;
   protected static OGLRenderer oglRenderPipe;
   protected static PixelToParallelogramConverter oglTxRenderPipe;
   protected static ParallelogramPipe oglAAPgramPipe;
   protected static OGLTextRenderer oglTextPipe;
   protected static OGLDrawImage oglImagePipe;

   protected native boolean initTexture(long var1, boolean var3, boolean var4, boolean var5, int var6, int var7);

   protected native boolean initFBObject(long var1, boolean var3, boolean var4, boolean var5, int var6, int var7);

   protected native boolean initFlipBackbuffer(long var1);

   protected abstract boolean initPbuffer(long var1, long var3, boolean var5, int var6, int var7);

   private native int getTextureTarget(long var1);

   private native int getTextureID(long var1);

   protected OGLSurfaceData(OGLGraphicsConfig var1, ColorModel var2, int var3) {
      super(getCustomSurfaceType(var3), var2);
      this.graphicsConfig = var1;
      this.type = var3;
      this.setBlitProxyKey(var1.getProxyKey());
   }

   public SurfaceDataProxy makeProxyFor(SurfaceData var1) {
      return OGLSurfaceDataProxy.createProxy(var1, this.graphicsConfig);
   }

   private static SurfaceType getCustomSurfaceType(int var0) {
      switch(var0) {
      case 2:
      case 4:
      default:
         return OpenGLSurface;
      case 3:
         return OpenGLTexture;
      case 5:
         return OpenGLSurfaceRTT;
      }
   }

   private void initSurfaceNow(int var1, int var2) {
      boolean var3 = this.getTransparency() == 1;
      boolean var4 = false;
      switch(this.type) {
      case 2:
         var4 = this.initPbuffer(this.getNativeOps(), this.graphicsConfig.getNativeConfigInfo(), var3, var1, var2);
         break;
      case 3:
         var4 = this.initTexture(this.getNativeOps(), var3, this.isTexNonPow2Available(), this.isTexRectAvailable(), var1, var2);
         break;
      case 4:
         var4 = this.initFlipBackbuffer(this.getNativeOps());
         break;
      case 5:
         var4 = this.initFBObject(this.getNativeOps(), var3, this.isTexNonPow2Available(), this.isTexRectAvailable(), var1, var2);
      }

      if (!var4) {
         throw new OutOfMemoryError("can't create offscreen surface");
      }
   }

   protected void initSurface(final int var1, final int var2) {
      OGLRenderQueue var3 = OGLRenderQueue.getInstance();
      var3.lock();

      try {
         switch(this.type) {
         case 2:
         case 3:
         case 5:
            OGLContext.setScratchSurface(this.graphicsConfig);
         case 4:
         }

         var3.flushAndInvokeNow(new Runnable() {
            public void run() {
               OGLSurfaceData.this.initSurfaceNow(var1, var2);
            }
         });
      } finally {
         var3.unlock();
      }

   }

   public final OGLContext getContext() {
      return this.graphicsConfig.getContext();
   }

   final OGLGraphicsConfig getOGLGraphicsConfig() {
      return this.graphicsConfig;
   }

   public final int getType() {
      return this.type;
   }

   public final int getTextureTarget() {
      return this.getTextureTarget(this.getNativeOps());
   }

   public final int getTextureID() {
      return this.getTextureID(this.getNativeOps());
   }

   public long getNativeResource(int var1) {
      return var1 == 3 ? (long)this.getTextureID() : 0L;
   }

   public Raster getRaster(int var1, int var2, int var3, int var4) {
      throw new InternalError("not implemented yet");
   }

   public boolean canRenderLCDText(SunGraphics2D var1) {
      return this.graphicsConfig.isCapPresent(131072) && var1.surfaceData.getTransparency() == 1 && var1.paintState <= 0 && (var1.compositeState <= 0 || var1.compositeState <= 1 && this.canHandleComposite(var1.composite));
   }

   private boolean canHandleComposite(Composite var1) {
      if (!(var1 instanceof AlphaComposite)) {
         return false;
      } else {
         AlphaComposite var2 = (AlphaComposite)var1;
         return var2.getRule() == 3 && var2.getAlpha() >= 1.0F;
      }
   }

   public void validatePipe(SunGraphics2D var1) {
      boolean var3 = false;
      Object var2;
      if ((var1.compositeState > 0 || var1.paintState > 1) && (var1.compositeState != 1 || var1.paintState > 1 || ((AlphaComposite)var1.composite).getRule() != 3) && (var1.compositeState != 2 || var1.paintState > 1)) {
         super.validatePipe(var1);
         var2 = var1.textpipe;
         var3 = true;
      } else {
         var2 = oglTextPipe;
      }

      PixelToParallelogramConverter var4 = null;
      OGLRenderer var5 = null;
      if (var1.antialiasHint != 2) {
         if (var1.paintState <= 1) {
            if (var1.compositeState <= 2) {
               var4 = oglTxRenderPipe;
               var5 = oglRenderPipe;
            }
         } else if (var1.compositeState <= 1 && OGLPaints.isValid(var1)) {
            var4 = oglTxRenderPipe;
            var5 = oglRenderPipe;
         }
      } else if (var1.paintState <= 1) {
         if (this.graphicsConfig.isCapPresent(256) && (var1.imageComp == CompositeType.SrcOverNoEa || var1.imageComp == CompositeType.SrcOver)) {
            if (!var3) {
               super.validatePipe(var1);
               var3 = true;
            }

            PixelToParallelogramConverter var6 = new PixelToParallelogramConverter(var1.shapepipe, oglAAPgramPipe, 0.125D, 0.499D, false);
            var1.drawpipe = var6;
            var1.fillpipe = var6;
            var1.shapepipe = var6;
         } else if (var1.compositeState == 2) {
            var4 = oglTxRenderPipe;
            var5 = oglRenderPipe;
         }
      }

      if (var4 != null) {
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

         var1.shapepipe = var4;
      } else if (!var3) {
         super.validatePipe(var1);
      }

      var1.textpipe = (TextPipe)var2;
      var1.imagepipe = oglImagePipe;
   }

   protected MaskFill getMaskFill(SunGraphics2D var1) {
      return var1.paintState <= 1 || OGLPaints.isValid(var1) && this.graphicsConfig.isCapPresent(16) ? super.getMaskFill(var1) : null;
   }

   public boolean copyArea(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      if (var1.transformState < 3 && var1.compositeState < 2) {
         var2 += var1.transX;
         var3 += var1.transY;
         oglRenderPipe.copyArea(var1, var2, var3, var4, var5, var6, var7);
         return true;
      } else {
         return false;
      }
   }

   public void flush() {
      this.invalidate();
      OGLRenderQueue var1 = OGLRenderQueue.getInstance();
      var1.lock();

      try {
         OGLContext.setScratchSurface(this.graphicsConfig);
         RenderBuffer var2 = var1.getBuffer();
         var1.ensureCapacityAndAlignment(12, 4);
         var2.putInt(72);
         var2.putLong(this.getNativeOps());
         var1.flushNow();
      } finally {
         var1.unlock();
      }

   }

   static void dispose(long var0, long var2) {
      OGLRenderQueue var4 = OGLRenderQueue.getInstance();
      var4.lock();

      try {
         OGLContext.setScratchSurface(var2);
         RenderBuffer var5 = var4.getBuffer();
         var4.ensureCapacityAndAlignment(12, 4);
         var5.putInt(73);
         var5.putLong(var0);
         var4.flushNow();
      } finally {
         var4.unlock();
      }

   }

   static void swapBuffers(long var0) {
      OGLRenderQueue var2 = OGLRenderQueue.getInstance();
      var2.lock();

      try {
         RenderBuffer var3 = var2.getBuffer();
         var2.ensureCapacityAndAlignment(12, 4);
         var3.putInt(80);
         var3.putLong(var0);
         var2.flushNow();
      } finally {
         var2.unlock();
      }

   }

   boolean isTexNonPow2Available() {
      return this.graphicsConfig.isCapPresent(32);
   }

   boolean isTexRectAvailable() {
      return this.graphicsConfig.isCapPresent(1048576);
   }

   public Rectangle getNativeBounds() {
      OGLRenderQueue var1 = OGLRenderQueue.getInstance();
      var1.lock();

      Rectangle var2;
      try {
         var2 = new Rectangle(this.nativeWidth, this.nativeHeight);
      } finally {
         var1.unlock();
      }

      return var2;
   }

   boolean isOnScreen() {
      return this.getType() == 1;
   }

   static {
      OpenGLSurface = SurfaceType.Any.deriveSubType("OpenGL Surface", PixelConverter.ArgbPre.instance);
      OpenGLSurfaceRTT = OpenGLSurface.deriveSubType("OpenGL Surface (render-to-texture)");
      OpenGLTexture = SurfaceType.Any.deriveSubType("OpenGL Texture");
      if (!GraphicsEnvironment.isHeadless()) {
         String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.java2d.opengl.fbobject")));
         isFBObjectEnabled = !"false".equals(var0);
         String var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.java2d.opengl.lcdshader")));
         isLCDShaderEnabled = !"false".equals(var1);
         String var2 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.java2d.opengl.biopshader")));
         isBIOpShaderEnabled = !"false".equals(var2);
         String var3 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.java2d.opengl.gradshader")));
         isGradShaderEnabled = !"false".equals(var3);
         OGLRenderQueue var4 = OGLRenderQueue.getInstance();
         oglImagePipe = new OGLDrawImage();
         oglTextPipe = new OGLTextRenderer(var4);
         oglRenderPipe = new OGLRenderer(var4);
         if (GraphicsPrimitive.tracingEnabled()) {
            oglTextPipe = oglTextPipe.traceWrap();
         }

         oglAAPgramPipe = oglRenderPipe.getAAParallelogramPipe();
         oglTxRenderPipe = new PixelToParallelogramConverter(oglRenderPipe, oglRenderPipe, 1.0D, 0.25D, true);
         OGLBlitLoops.register();
         OGLMaskFill.register();
         OGLMaskBlit.register();
      }

   }
}
