package sun.java2d;

import java.awt.AWTPermission;
import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.security.Permission;
import sun.awt.image.SurfaceManager;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.DrawGlyphList;
import sun.java2d.loops.DrawGlyphListAA;
import sun.java2d.loops.DrawGlyphListLCD;
import sun.java2d.loops.DrawLine;
import sun.java2d.loops.DrawParallelogram;
import sun.java2d.loops.DrawPath;
import sun.java2d.loops.DrawPolygons;
import sun.java2d.loops.DrawRect;
import sun.java2d.loops.FillParallelogram;
import sun.java2d.loops.FillPath;
import sun.java2d.loops.FillRect;
import sun.java2d.loops.FillSpans;
import sun.java2d.loops.MaskFill;
import sun.java2d.loops.RenderCache;
import sun.java2d.loops.RenderLoops;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.AAShapePipe;
import sun.java2d.pipe.AATextRenderer;
import sun.java2d.pipe.AlphaColorPipe;
import sun.java2d.pipe.AlphaPaintPipe;
import sun.java2d.pipe.CompositePipe;
import sun.java2d.pipe.DrawImage;
import sun.java2d.pipe.DrawImagePipe;
import sun.java2d.pipe.GeneralCompositePipe;
import sun.java2d.pipe.LCDTextRenderer;
import sun.java2d.pipe.LoopBasedPipe;
import sun.java2d.pipe.LoopPipe;
import sun.java2d.pipe.OutlineTextRenderer;
import sun.java2d.pipe.ParallelogramPipe;
import sun.java2d.pipe.PixelDrawPipe;
import sun.java2d.pipe.PixelFillPipe;
import sun.java2d.pipe.PixelToParallelogramConverter;
import sun.java2d.pipe.PixelToShapeConverter;
import sun.java2d.pipe.ShapeDrawPipe;
import sun.java2d.pipe.SolidTextRenderer;
import sun.java2d.pipe.SpanClipRenderer;
import sun.java2d.pipe.SpanShapeRenderer;
import sun.java2d.pipe.TextPipe;
import sun.java2d.pipe.TextRenderer;

public abstract class SurfaceData implements Transparency, DisposerTarget, StateTrackable, Surface {
   private long pData;
   private boolean valid;
   private boolean surfaceLost;
   private SurfaceType surfaceType;
   private ColorModel colorModel;
   private Object disposerReferent;
   private Object blitProxyKey;
   private StateTrackableDelegate stateDelegate;
   protected static final LoopPipe colorPrimitives;
   public static final TextPipe outlineTextRenderer;
   public static final TextPipe solidTextRenderer;
   public static final TextPipe aaTextRenderer;
   public static final TextPipe lcdTextRenderer;
   protected static final AlphaColorPipe colorPipe;
   protected static final PixelToShapeConverter colorViaShape;
   protected static final PixelToParallelogramConverter colorViaPgram;
   protected static final TextPipe colorText;
   protected static final CompositePipe clipColorPipe;
   protected static final TextPipe clipColorText;
   protected static final AAShapePipe AAColorShape;
   protected static final PixelToParallelogramConverter AAColorViaShape;
   protected static final PixelToParallelogramConverter AAColorViaPgram;
   protected static final AAShapePipe AAClipColorShape;
   protected static final PixelToParallelogramConverter AAClipColorViaShape;
   protected static final CompositePipe paintPipe;
   protected static final SpanShapeRenderer paintShape;
   protected static final PixelToShapeConverter paintViaShape;
   protected static final TextPipe paintText;
   protected static final CompositePipe clipPaintPipe;
   protected static final TextPipe clipPaintText;
   protected static final AAShapePipe AAPaintShape;
   protected static final PixelToParallelogramConverter AAPaintViaShape;
   protected static final AAShapePipe AAClipPaintShape;
   protected static final PixelToParallelogramConverter AAClipPaintViaShape;
   protected static final CompositePipe compPipe;
   protected static final SpanShapeRenderer compShape;
   protected static final PixelToShapeConverter compViaShape;
   protected static final TextPipe compText;
   protected static final CompositePipe clipCompPipe;
   protected static final TextPipe clipCompText;
   protected static final AAShapePipe AACompShape;
   protected static final PixelToParallelogramConverter AACompViaShape;
   protected static final AAShapePipe AAClipCompShape;
   protected static final PixelToParallelogramConverter AAClipCompViaShape;
   protected static final DrawImagePipe imagepipe;
   static final int LOOP_UNKNOWN = 0;
   static final int LOOP_FOUND = 1;
   static final int LOOP_NOTFOUND = 2;
   int haveLCDLoop;
   int havePgramXORLoop;
   int havePgramSolidLoop;
   private static RenderCache loopcache;
   static Permission compPermission;

   private static native void initIDs();

   protected SurfaceData(SurfaceType var1, ColorModel var2) {
      this(StateTrackable.State.STABLE, var1, var2);
   }

   protected SurfaceData(StateTrackable.State var1, SurfaceType var2, ColorModel var3) {
      this(StateTrackableDelegate.createInstance(var1), var2, var3);
   }

   protected SurfaceData(StateTrackableDelegate var1, SurfaceType var2, ColorModel var3) {
      this.disposerReferent = new Object();
      this.stateDelegate = var1;
      this.colorModel = var3;
      this.surfaceType = var2;
      this.valid = true;
   }

   protected SurfaceData(StateTrackable.State var1) {
      this.disposerReferent = new Object();
      this.stateDelegate = StateTrackableDelegate.createInstance(var1);
      this.valid = true;
   }

   protected void setBlitProxyKey(Object var1) {
      if (SurfaceDataProxy.isCachingAllowed()) {
         this.blitProxyKey = var1;
      }

   }

   public SurfaceData getSourceSurfaceData(Image var1, int var2, CompositeType var3, Color var4) {
      SurfaceManager var5 = SurfaceManager.getManager(var1);
      SurfaceData var6 = var5.getPrimarySurfaceData();
      if (var1.getAccelerationPriority() > 0.0F && this.blitProxyKey != null) {
         SurfaceDataProxy var7 = (SurfaceDataProxy)var5.getCacheData(this.blitProxyKey);
         if (var7 == null || !var7.isValid()) {
            if (var6.getState() == StateTrackable.State.UNTRACKABLE) {
               var7 = SurfaceDataProxy.UNCACHED;
            } else {
               var7 = this.makeProxyFor(var6);
            }

            var5.setCacheData(this.blitProxyKey, var7);
         }

         var6 = var7.replaceData(var6, var2, var3, var4);
      }

      return var6;
   }

   public SurfaceDataProxy makeProxyFor(SurfaceData var1) {
      return SurfaceDataProxy.UNCACHED;
   }

   public static SurfaceData getPrimarySurfaceData(Image var0) {
      SurfaceManager var1 = SurfaceManager.getManager(var0);
      return var1.getPrimarySurfaceData();
   }

   public static SurfaceData restoreContents(Image var0) {
      SurfaceManager var1 = SurfaceManager.getManager(var0);
      return var1.restoreContents();
   }

   public StateTrackable.State getState() {
      return this.stateDelegate.getState();
   }

   public StateTracker getStateTracker() {
      return this.stateDelegate.getStateTracker();
   }

   public final void markDirty() {
      this.stateDelegate.markDirty();
   }

   public void setSurfaceLost(boolean var1) {
      this.surfaceLost = var1;
      this.stateDelegate.markDirty();
   }

   public boolean isSurfaceLost() {
      return this.surfaceLost;
   }

   public final boolean isValid() {
      return this.valid;
   }

   public Object getDisposerReferent() {
      return this.disposerReferent;
   }

   public long getNativeOps() {
      return this.pData;
   }

   public void invalidate() {
      this.valid = false;
      this.stateDelegate.markDirty();
   }

   public abstract SurfaceData getReplacement();

   private static PixelToParallelogramConverter makeConverter(AAShapePipe var0, ParallelogramPipe var1) {
      return new PixelToParallelogramConverter(var0, var1, 0.125D, 0.499D, false);
   }

   private static PixelToParallelogramConverter makeConverter(AAShapePipe var0) {
      return makeConverter(var0, var0);
   }

   public boolean canRenderLCDText(SunGraphics2D var1) {
      if (var1.compositeState <= 0 && var1.paintState <= 1 && var1.clipState <= 1 && var1.surfaceData.getTransparency() == 1) {
         if (this.haveLCDLoop == 0) {
            DrawGlyphListLCD var2 = DrawGlyphListLCD.locate(SurfaceType.AnyColor, CompositeType.SrcNoEa, this.getSurfaceType());
            this.haveLCDLoop = var2 != null ? 1 : 2;
         }

         return this.haveLCDLoop == 1;
      } else {
         return false;
      }
   }

   public boolean canRenderParallelograms(SunGraphics2D var1) {
      if (var1.paintState <= 1) {
         FillParallelogram var2;
         if (var1.compositeState == 2) {
            if (this.havePgramXORLoop == 0) {
               var2 = FillParallelogram.locate(SurfaceType.AnyColor, CompositeType.Xor, this.getSurfaceType());
               this.havePgramXORLoop = var2 != null ? 1 : 2;
            }

            return this.havePgramXORLoop == 1;
         }

         if (var1.compositeState <= 0 && var1.antialiasHint != 2 && var1.clipState != 2) {
            if (this.havePgramSolidLoop == 0) {
               var2 = FillParallelogram.locate(SurfaceType.AnyColor, CompositeType.SrcNoEa, this.getSurfaceType());
               this.havePgramSolidLoop = var2 != null ? 1 : 2;
            }

            return this.havePgramSolidLoop == 1;
         }
      }

      return false;
   }

   public void validatePipe(SunGraphics2D var1) {
      var1.imagepipe = imagepipe;
      Object var2;
      if (var1.compositeState == 2) {
         if (var1.paintState > 1) {
            var1.drawpipe = paintViaShape;
            var1.fillpipe = paintViaShape;
            var1.shapepipe = paintShape;
            var1.textpipe = outlineTextRenderer;
         } else {
            if (this.canRenderParallelograms(var1)) {
               var2 = colorViaPgram;
               var1.shapepipe = colorViaPgram;
            } else {
               var2 = colorViaShape;
               var1.shapepipe = colorPrimitives;
            }

            if (var1.clipState == 2) {
               var1.drawpipe = (PixelDrawPipe)var2;
               var1.fillpipe = (PixelFillPipe)var2;
               var1.textpipe = outlineTextRenderer;
            } else {
               if (var1.transformState >= 3) {
                  var1.drawpipe = (PixelDrawPipe)var2;
                  var1.fillpipe = (PixelFillPipe)var2;
               } else {
                  if (var1.strokeState != 0) {
                     var1.drawpipe = (PixelDrawPipe)var2;
                  } else {
                     var1.drawpipe = colorPrimitives;
                  }

                  var1.fillpipe = colorPrimitives;
               }

               var1.textpipe = solidTextRenderer;
            }
         }
      } else if (var1.compositeState == 3) {
         if (var1.antialiasHint == 2) {
            if (var1.clipState == 2) {
               var1.drawpipe = AAClipCompViaShape;
               var1.fillpipe = AAClipCompViaShape;
               var1.shapepipe = AAClipCompViaShape;
               var1.textpipe = clipCompText;
            } else {
               var1.drawpipe = AACompViaShape;
               var1.fillpipe = AACompViaShape;
               var1.shapepipe = AACompViaShape;
               var1.textpipe = compText;
            }
         } else {
            var1.drawpipe = compViaShape;
            var1.fillpipe = compViaShape;
            var1.shapepipe = compShape;
            if (var1.clipState == 2) {
               var1.textpipe = clipCompText;
            } else {
               var1.textpipe = compText;
            }
         }
      } else if (var1.antialiasHint == 2) {
         var1.alphafill = this.getMaskFill(var1);
         if (var1.alphafill != null) {
            if (var1.clipState == 2) {
               var1.drawpipe = AAClipColorViaShape;
               var1.fillpipe = AAClipColorViaShape;
               var1.shapepipe = AAClipColorViaShape;
               var1.textpipe = clipColorText;
            } else {
               PixelToParallelogramConverter var3 = var1.alphafill.canDoParallelograms() ? AAColorViaPgram : AAColorViaShape;
               var1.drawpipe = var3;
               var1.fillpipe = var3;
               var1.shapepipe = var3;
               if (var1.paintState <= 1 && var1.compositeState <= 0) {
                  var1.textpipe = this.getTextPipe(var1, true);
               } else {
                  var1.textpipe = colorText;
               }
            }
         } else if (var1.clipState == 2) {
            var1.drawpipe = AAClipPaintViaShape;
            var1.fillpipe = AAClipPaintViaShape;
            var1.shapepipe = AAClipPaintViaShape;
            var1.textpipe = clipPaintText;
         } else {
            var1.drawpipe = AAPaintViaShape;
            var1.fillpipe = AAPaintViaShape;
            var1.shapepipe = AAPaintViaShape;
            var1.textpipe = paintText;
         }
      } else if (var1.paintState <= 1 && var1.compositeState <= 0 && var1.clipState != 2) {
         if (this.canRenderParallelograms(var1)) {
            var2 = colorViaPgram;
            var1.shapepipe = colorViaPgram;
         } else {
            var2 = colorViaShape;
            var1.shapepipe = colorPrimitives;
         }

         if (var1.transformState >= 3) {
            var1.drawpipe = (PixelDrawPipe)var2;
            var1.fillpipe = (PixelFillPipe)var2;
         } else {
            if (var1.strokeState != 0) {
               var1.drawpipe = (PixelDrawPipe)var2;
            } else {
               var1.drawpipe = colorPrimitives;
            }

            var1.fillpipe = colorPrimitives;
         }

         var1.textpipe = this.getTextPipe(var1, false);
      } else {
         var1.drawpipe = paintViaShape;
         var1.fillpipe = paintViaShape;
         var1.shapepipe = paintShape;
         var1.alphafill = this.getMaskFill(var1);
         if (var1.alphafill != null) {
            if (var1.clipState == 2) {
               var1.textpipe = clipColorText;
            } else {
               var1.textpipe = colorText;
            }
         } else if (var1.clipState == 2) {
            var1.textpipe = clipPaintText;
         } else {
            var1.textpipe = paintText;
         }
      }

      if (var1.textpipe instanceof LoopBasedPipe || var1.shapepipe instanceof LoopBasedPipe || var1.fillpipe instanceof LoopBasedPipe || var1.drawpipe instanceof LoopBasedPipe || var1.imagepipe instanceof LoopBasedPipe) {
         var1.loops = this.getRenderLoops(var1);
      }

   }

   private TextPipe getTextPipe(SunGraphics2D var1, boolean var2) {
      switch(var1.textAntialiasHint) {
      case 0:
         if (var2) {
            return aaTextRenderer;
         }

         return solidTextRenderer;
      case 1:
         return solidTextRenderer;
      case 2:
         return aaTextRenderer;
      default:
         switch(var1.getFontInfo().aaHint) {
         case 1:
            return solidTextRenderer;
         case 2:
            return aaTextRenderer;
         case 3:
         case 5:
         default:
            if (var2) {
               return aaTextRenderer;
            }

            return solidTextRenderer;
         case 4:
         case 6:
            return lcdTextRenderer;
         }
      }
   }

   private static SurfaceType getPaintSurfaceType(SunGraphics2D var0) {
      switch(var0.paintState) {
      case 0:
         return SurfaceType.OpaqueColor;
      case 1:
         return SurfaceType.AnyColor;
      case 2:
         if (var0.paint.getTransparency() == 1) {
            return SurfaceType.OpaqueGradientPaint;
         }

         return SurfaceType.GradientPaint;
      case 3:
         if (var0.paint.getTransparency() == 1) {
            return SurfaceType.OpaqueLinearGradientPaint;
         }

         return SurfaceType.LinearGradientPaint;
      case 4:
         if (var0.paint.getTransparency() == 1) {
            return SurfaceType.OpaqueRadialGradientPaint;
         }

         return SurfaceType.RadialGradientPaint;
      case 5:
         if (var0.paint.getTransparency() == 1) {
            return SurfaceType.OpaqueTexturePaint;
         }

         return SurfaceType.TexturePaint;
      case 6:
      default:
         return SurfaceType.AnyPaint;
      }
   }

   private static CompositeType getFillCompositeType(SunGraphics2D var0) {
      CompositeType var1 = var0.imageComp;
      if (var0.compositeState == 0) {
         if (var1 == CompositeType.SrcOverNoEa) {
            var1 = CompositeType.OpaqueSrcOverNoEa;
         } else {
            var1 = CompositeType.SrcNoEa;
         }
      }

      return var1;
   }

   protected MaskFill getMaskFill(SunGraphics2D var1) {
      SurfaceType var2 = getPaintSurfaceType(var1);
      CompositeType var3 = getFillCompositeType(var1);
      SurfaceType var4 = this.getSurfaceType();
      return MaskFill.getFromCache(var2, var3, var4);
   }

   public RenderLoops getRenderLoops(SunGraphics2D var1) {
      SurfaceType var2 = getPaintSurfaceType(var1);
      CompositeType var3 = getFillCompositeType(var1);
      SurfaceType var4 = var1.getSurfaceData().getSurfaceType();
      Object var5 = loopcache.get(var2, var3, var4);
      if (var5 != null) {
         return (RenderLoops)var5;
      } else {
         RenderLoops var6 = makeRenderLoops(var2, var3, var4);
         loopcache.put(var2, var3, var4, var6);
         return var6;
      }
   }

   public static RenderLoops makeRenderLoops(SurfaceType var0, CompositeType var1, SurfaceType var2) {
      RenderLoops var3 = new RenderLoops();
      var3.drawLineLoop = DrawLine.locate(var0, var1, var2);
      var3.fillRectLoop = FillRect.locate(var0, var1, var2);
      var3.drawRectLoop = DrawRect.locate(var0, var1, var2);
      var3.drawPolygonsLoop = DrawPolygons.locate(var0, var1, var2);
      var3.drawPathLoop = DrawPath.locate(var0, var1, var2);
      var3.fillPathLoop = FillPath.locate(var0, var1, var2);
      var3.fillSpansLoop = FillSpans.locate(var0, var1, var2);
      var3.fillParallelogramLoop = FillParallelogram.locate(var0, var1, var2);
      var3.drawParallelogramLoop = DrawParallelogram.locate(var0, var1, var2);
      var3.drawGlyphListLoop = DrawGlyphList.locate(var0, var1, var2);
      var3.drawGlyphListAALoop = DrawGlyphListAA.locate(var0, var1, var2);
      var3.drawGlyphListLCDLoop = DrawGlyphListLCD.locate(var0, var1, var2);
      return var3;
   }

   public abstract GraphicsConfiguration getDeviceConfiguration();

   public final SurfaceType getSurfaceType() {
      return this.surfaceType;
   }

   public final ColorModel getColorModel() {
      return this.colorModel;
   }

   public int getTransparency() {
      return this.getColorModel().getTransparency();
   }

   public abstract Raster getRaster(int var1, int var2, int var3, int var4);

   public boolean useTightBBoxes() {
      return true;
   }

   public int pixelFor(int var1) {
      return this.surfaceType.pixelFor(var1, this.colorModel);
   }

   public int pixelFor(Color var1) {
      return this.pixelFor(var1.getRGB());
   }

   public int rgbFor(int var1) {
      return this.surfaceType.rgbFor(var1, this.colorModel);
   }

   public abstract Rectangle getBounds();

   protected void checkCustomComposite() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         if (compPermission == null) {
            compPermission = new AWTPermission("readDisplayPixels");
         }

         var1.checkPermission(compPermission);
      }

   }

   protected static native boolean isOpaqueGray(IndexColorModel var0);

   public static boolean isNull(SurfaceData var0) {
      return var0 == null || var0 == NullSurfaceData.theInstance;
   }

   public boolean copyArea(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      return false;
   }

   public void flush() {
   }

   public abstract Object getDestination();

   public int getDefaultScale() {
      return 1;
   }

   static {
      initIDs();
      colorPrimitives = new LoopPipe();
      outlineTextRenderer = new OutlineTextRenderer();
      solidTextRenderer = new SolidTextRenderer();
      aaTextRenderer = new AATextRenderer();
      lcdTextRenderer = new LCDTextRenderer();
      colorPipe = new AlphaColorPipe();
      colorViaShape = new SurfaceData.PixelToShapeLoopConverter(colorPrimitives);
      colorViaPgram = new SurfaceData.PixelToPgramLoopConverter(colorPrimitives, colorPrimitives, 1.0D, 0.25D, true);
      colorText = new TextRenderer(colorPipe);
      clipColorPipe = new SpanClipRenderer(colorPipe);
      clipColorText = new TextRenderer(clipColorPipe);
      AAColorShape = new AAShapePipe(colorPipe);
      AAColorViaShape = makeConverter(AAColorShape);
      AAColorViaPgram = makeConverter(AAColorShape, colorPipe);
      AAClipColorShape = new AAShapePipe(clipColorPipe);
      AAClipColorViaShape = makeConverter(AAClipColorShape);
      paintPipe = new AlphaPaintPipe();
      paintShape = new SpanShapeRenderer.Composite(paintPipe);
      paintViaShape = new PixelToShapeConverter(paintShape);
      paintText = new TextRenderer(paintPipe);
      clipPaintPipe = new SpanClipRenderer(paintPipe);
      clipPaintText = new TextRenderer(clipPaintPipe);
      AAPaintShape = new AAShapePipe(paintPipe);
      AAPaintViaShape = makeConverter(AAPaintShape);
      AAClipPaintShape = new AAShapePipe(clipPaintPipe);
      AAClipPaintViaShape = makeConverter(AAClipPaintShape);
      compPipe = new GeneralCompositePipe();
      compShape = new SpanShapeRenderer.Composite(compPipe);
      compViaShape = new PixelToShapeConverter(compShape);
      compText = new TextRenderer(compPipe);
      clipCompPipe = new SpanClipRenderer(compPipe);
      clipCompText = new TextRenderer(clipCompPipe);
      AACompShape = new AAShapePipe(compPipe);
      AACompViaShape = makeConverter(AACompShape);
      AAClipCompShape = new AAShapePipe(clipCompPipe);
      AAClipCompViaShape = makeConverter(AAClipCompShape);
      imagepipe = new DrawImage();
      loopcache = new RenderCache(30);
   }

   static class PixelToPgramLoopConverter extends PixelToParallelogramConverter implements LoopBasedPipe {
      public PixelToPgramLoopConverter(ShapeDrawPipe var1, ParallelogramPipe var2, double var3, double var5, boolean var7) {
         super(var1, var2, var3, var5, var7);
      }
   }

   static class PixelToShapeLoopConverter extends PixelToShapeConverter implements LoopBasedPipe {
      public PixelToShapeLoopConverter(ShapeDrawPipe var1) {
         super(var1);
      }
   }
}
