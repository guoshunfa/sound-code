package sun.java2d.pipe;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import sun.java2d.InvalidPipeException;
import sun.java2d.SunGraphics2D;
import sun.java2d.loops.XORComposite;
import sun.java2d.pipe.hw.AccelSurface;

public abstract class BufferedContext {
   public static final int NO_CONTEXT_FLAGS = 0;
   public static final int SRC_IS_OPAQUE = 1;
   public static final int USE_MASK = 2;
   protected RenderQueue rq;
   protected RenderBuffer buf;
   protected static BufferedContext currentContext;
   private Reference<AccelSurface> validSrcDataRef = new WeakReference((Object)null);
   private Reference<AccelSurface> validDstDataRef = new WeakReference((Object)null);
   private Reference<Region> validClipRef = new WeakReference((Object)null);
   private Reference<Composite> validCompRef = new WeakReference((Object)null);
   private Reference<Paint> validPaintRef = new WeakReference((Object)null);
   private boolean isValidatedPaintJustAColor;
   private int validatedRGB;
   private int validatedFlags;
   private boolean xformInUse;
   private AffineTransform transform;

   protected BufferedContext(RenderQueue var1) {
      this.rq = var1;
      this.buf = var1.getBuffer();
   }

   public static void validateContext(AccelSurface var0, AccelSurface var1, Region var2, Composite var3, AffineTransform var4, Paint var5, SunGraphics2D var6, int var7) {
      BufferedContext var8 = var1.getContext();
      var8.validate(var0, var1, var2, var3, var4, var5, var6, var7);
   }

   public static void validateContext(AccelSurface var0) {
      validateContext(var0, var0, (Region)null, (Composite)null, (AffineTransform)null, (Paint)null, (SunGraphics2D)null, 0);
   }

   public void validate(AccelSurface var1, AccelSurface var2, Region var3, Composite var4, AffineTransform var5, Paint var6, SunGraphics2D var7, int var8) {
      boolean var9 = false;
      boolean var10 = false;
      if (var2.isValid() && !var2.isSurfaceLost() && !var1.isSurfaceLost()) {
         if (var6 instanceof Color) {
            int var11 = ((Color)var6).getRGB();
            if (this.isValidatedPaintJustAColor) {
               if (var11 != this.validatedRGB) {
                  this.validatedRGB = var11;
                  var10 = true;
               }
            } else {
               this.validatedRGB = var11;
               var10 = true;
               this.isValidatedPaintJustAColor = true;
            }
         } else if (this.validPaintRef.get() != var6) {
            var10 = true;
            this.isValidatedPaintJustAColor = false;
         }

         AccelSurface var15 = (AccelSurface)this.validSrcDataRef.get();
         AccelSurface var12 = (AccelSurface)this.validDstDataRef.get();
         if (currentContext != this || var1 != var15 || var2 != var12) {
            if (var2 != var12) {
               var9 = true;
            }

            if (var6 == null) {
               var10 = true;
            }

            this.setSurfaces(var1, var2);
            currentContext = this;
            this.validSrcDataRef = new WeakReference(var1);
            this.validDstDataRef = new WeakReference(var2);
         }

         Region var13 = (Region)this.validClipRef.get();
         if (var3 != var13 || var9) {
            if (var3 != null) {
               if (var9 || var13 == null || !var13.isRectangular() || !var3.isRectangular() || var3.getLoX() != var13.getLoX() || var3.getLoY() != var13.getLoY() || var3.getHiX() != var13.getHiX() || var3.getHiY() != var13.getHiY()) {
                  this.setClip(var3);
               }
            } else {
               this.resetClip();
            }

            this.validClipRef = new WeakReference(var3);
         }

         if (var4 != this.validCompRef.get() || var8 != this.validatedFlags) {
            if (var4 != null) {
               this.setComposite(var4, var8);
            } else {
               this.resetComposite();
            }

            var10 = true;
            this.validCompRef = new WeakReference(var4);
            this.validatedFlags = var8;
         }

         boolean var14 = false;
         if (var5 == null) {
            if (this.xformInUse) {
               this.resetTransform();
               this.xformInUse = false;
               var14 = true;
            } else if (var7 != null && !var7.transform.equals(this.transform)) {
               var14 = true;
            }

            if (var7 != null && var14) {
               this.transform = new AffineTransform(var7.transform);
            }
         } else {
            this.setTransform(var5);
            this.xformInUse = true;
            var14 = true;
         }

         if (!this.isValidatedPaintJustAColor && var14) {
            var10 = true;
         }

         if (var10) {
            if (var6 != null) {
               BufferedPaints.setPaint(this.rq, var7, var6, var8);
            } else {
               BufferedPaints.resetPaint(this.rq);
            }

            this.validPaintRef = new WeakReference(var6);
         }

         var2.markDirty();
      } else {
         this.invalidateContext();
         throw new InvalidPipeException("bounds changed or surface lost");
      }
   }

   private void invalidateSurfaces() {
      this.validSrcDataRef.clear();
      this.validDstDataRef.clear();
   }

   private void setSurfaces(AccelSurface var1, AccelSurface var2) {
      this.rq.ensureCapacityAndAlignment(20, 4);
      this.buf.putInt(70);
      this.buf.putLong(var1.getNativeOps());
      this.buf.putLong(var2.getNativeOps());
   }

   private void resetClip() {
      this.rq.ensureCapacity(4);
      this.buf.putInt(55);
   }

   private void setClip(Region var1) {
      if (var1.isRectangular()) {
         this.rq.ensureCapacity(20);
         this.buf.putInt(51);
         this.buf.putInt(var1.getLoX()).putInt(var1.getLoY());
         this.buf.putInt(var1.getHiX()).putInt(var1.getHiY());
      } else {
         this.rq.ensureCapacity(28);
         this.buf.putInt(52);
         this.buf.putInt(53);
         int var2 = this.buf.position();
         this.buf.putInt(0);
         int var3 = 0;
         int var4 = this.buf.remaining() / 16;
         int[] var5 = new int[4];

         for(SpanIterator var6 = var1.getSpanIterator(); var6.nextSpan(var5); --var4) {
            if (var4 == 0) {
               this.buf.putInt(var2, var3);
               this.rq.flushNow();
               this.buf.putInt(53);
               var2 = this.buf.position();
               this.buf.putInt(0);
               var3 = 0;
               var4 = this.buf.remaining() / 16;
            }

            this.buf.putInt(var5[0]);
            this.buf.putInt(var5[1]);
            this.buf.putInt(var5[2]);
            this.buf.putInt(var5[3]);
            ++var3;
         }

         this.buf.putInt(var2, var3);
         this.rq.ensureCapacity(4);
         this.buf.putInt(54);
      }

   }

   private void resetComposite() {
      this.rq.ensureCapacity(4);
      this.buf.putInt(58);
   }

   private void setComposite(Composite var1, int var2) {
      if (var1 instanceof AlphaComposite) {
         AlphaComposite var3 = (AlphaComposite)var1;
         this.rq.ensureCapacity(16);
         this.buf.putInt(56);
         this.buf.putInt(var3.getRule());
         this.buf.putFloat(var3.getAlpha());
         this.buf.putInt(var2);
      } else {
         if (!(var1 instanceof XORComposite)) {
            throw new InternalError("not yet implemented");
         }

         int var4 = ((XORComposite)var1).getXorPixel();
         this.rq.ensureCapacity(8);
         this.buf.putInt(57);
         this.buf.putInt(var4);
      }

   }

   private void resetTransform() {
      this.rq.ensureCapacity(4);
      this.buf.putInt(60);
   }

   private void setTransform(AffineTransform var1) {
      this.rq.ensureCapacityAndAlignment(52, 4);
      this.buf.putInt(59);
      this.buf.putDouble(var1.getScaleX());
      this.buf.putDouble(var1.getShearY());
      this.buf.putDouble(var1.getShearX());
      this.buf.putDouble(var1.getScaleY());
      this.buf.putDouble(var1.getTranslateX());
      this.buf.putDouble(var1.getTranslateY());
   }

   public void invalidateContext() {
      this.resetTransform();
      this.resetComposite();
      this.resetClip();
      BufferedPaints.resetPaint(this.rq);
      this.invalidateSurfaces();
      this.validCompRef.clear();
      this.validClipRef.clear();
      this.validPaintRef.clear();
      this.isValidatedPaintJustAColor = false;
      this.xformInUse = false;
   }

   public abstract RenderQueue getRenderQueue();

   public abstract void saveState();

   public abstract void restoreState();
}
