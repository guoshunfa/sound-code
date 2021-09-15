package sun.java2d.pisces;

import java.awt.geom.AffineTransform;
import sun.awt.geom.PathConsumer2D;

final class TransformingPathConsumer2D {
   public static PathConsumer2D transformConsumer(PathConsumer2D var0, AffineTransform var1) {
      if (var1 == null) {
         return var0;
      } else {
         float var2 = (float)var1.getScaleX();
         float var3 = (float)var1.getShearX();
         float var4 = (float)var1.getTranslateX();
         float var5 = (float)var1.getShearY();
         float var6 = (float)var1.getScaleY();
         float var7 = (float)var1.getTranslateY();
         if (var3 == 0.0F && var5 == 0.0F) {
            if (var2 == 1.0F && var6 == 1.0F) {
               return (PathConsumer2D)(var4 == 0.0F && var7 == 0.0F ? var0 : new TransformingPathConsumer2D.TranslateFilter(var0, var4, var7));
            } else {
               return (PathConsumer2D)(var4 == 0.0F && var7 == 0.0F ? new TransformingPathConsumer2D.DeltaScaleFilter(var0, var2, var6) : new TransformingPathConsumer2D.ScaleFilter(var0, var2, var6, var4, var7));
            }
         } else {
            return (PathConsumer2D)(var4 == 0.0F && var7 == 0.0F ? new TransformingPathConsumer2D.DeltaTransformFilter(var0, var2, var3, var5, var6) : new TransformingPathConsumer2D.TransformFilter(var0, var2, var3, var4, var5, var6, var7));
         }
      }
   }

   public static PathConsumer2D deltaTransformConsumer(PathConsumer2D var0, AffineTransform var1) {
      if (var1 == null) {
         return var0;
      } else {
         float var2 = (float)var1.getScaleX();
         float var3 = (float)var1.getShearX();
         float var4 = (float)var1.getShearY();
         float var5 = (float)var1.getScaleY();
         if (var3 == 0.0F && var4 == 0.0F) {
            return (PathConsumer2D)(var2 == 1.0F && var5 == 1.0F ? var0 : new TransformingPathConsumer2D.DeltaScaleFilter(var0, var2, var5));
         } else {
            return new TransformingPathConsumer2D.DeltaTransformFilter(var0, var2, var3, var4, var5);
         }
      }
   }

   public static PathConsumer2D inverseDeltaTransformConsumer(PathConsumer2D var0, AffineTransform var1) {
      if (var1 == null) {
         return var0;
      } else {
         float var2 = (float)var1.getScaleX();
         float var3 = (float)var1.getShearX();
         float var4 = (float)var1.getShearY();
         float var5 = (float)var1.getScaleY();
         if (var3 == 0.0F && var4 == 0.0F) {
            return (PathConsumer2D)(var2 == 1.0F && var5 == 1.0F ? var0 : new TransformingPathConsumer2D.DeltaScaleFilter(var0, 1.0F / var2, 1.0F / var5));
         } else {
            float var6 = var2 * var5 - var3 * var4;
            return new TransformingPathConsumer2D.DeltaTransformFilter(var0, var5 / var6, -var3 / var6, -var4 / var6, var2 / var6);
         }
      }
   }

   static final class DeltaTransformFilter implements PathConsumer2D {
      private PathConsumer2D out;
      private final float Mxx;
      private final float Mxy;
      private final float Myx;
      private final float Myy;

      DeltaTransformFilter(PathConsumer2D var1, float var2, float var3, float var4, float var5) {
         this.out = var1;
         this.Mxx = var2;
         this.Mxy = var3;
         this.Myx = var4;
         this.Myy = var5;
      }

      public void moveTo(float var1, float var2) {
         this.out.moveTo(var1 * this.Mxx + var2 * this.Mxy, var1 * this.Myx + var2 * this.Myy);
      }

      public void lineTo(float var1, float var2) {
         this.out.lineTo(var1 * this.Mxx + var2 * this.Mxy, var1 * this.Myx + var2 * this.Myy);
      }

      public void quadTo(float var1, float var2, float var3, float var4) {
         this.out.quadTo(var1 * this.Mxx + var2 * this.Mxy, var1 * this.Myx + var2 * this.Myy, var3 * this.Mxx + var4 * this.Mxy, var3 * this.Myx + var4 * this.Myy);
      }

      public void curveTo(float var1, float var2, float var3, float var4, float var5, float var6) {
         this.out.curveTo(var1 * this.Mxx + var2 * this.Mxy, var1 * this.Myx + var2 * this.Myy, var3 * this.Mxx + var4 * this.Mxy, var3 * this.Myx + var4 * this.Myy, var5 * this.Mxx + var6 * this.Mxy, var5 * this.Myx + var6 * this.Myy);
      }

      public void closePath() {
         this.out.closePath();
      }

      public void pathDone() {
         this.out.pathDone();
      }

      public long getNativeConsumer() {
         return 0L;
      }
   }

   static final class DeltaScaleFilter implements PathConsumer2D {
      private final float sx;
      private final float sy;
      private final PathConsumer2D out;

      public DeltaScaleFilter(PathConsumer2D var1, float var2, float var3) {
         this.sx = var2;
         this.sy = var3;
         this.out = var1;
      }

      public void moveTo(float var1, float var2) {
         this.out.moveTo(var1 * this.sx, var2 * this.sy);
      }

      public void lineTo(float var1, float var2) {
         this.out.lineTo(var1 * this.sx, var2 * this.sy);
      }

      public void quadTo(float var1, float var2, float var3, float var4) {
         this.out.quadTo(var1 * this.sx, var2 * this.sy, var3 * this.sx, var4 * this.sy);
      }

      public void curveTo(float var1, float var2, float var3, float var4, float var5, float var6) {
         this.out.curveTo(var1 * this.sx, var2 * this.sy, var3 * this.sx, var4 * this.sy, var5 * this.sx, var6 * this.sy);
      }

      public void closePath() {
         this.out.closePath();
      }

      public void pathDone() {
         this.out.pathDone();
      }

      public long getNativeConsumer() {
         return 0L;
      }
   }

   static final class TransformFilter implements PathConsumer2D {
      private final PathConsumer2D out;
      private final float Mxx;
      private final float Mxy;
      private final float Mxt;
      private final float Myx;
      private final float Myy;
      private final float Myt;

      TransformFilter(PathConsumer2D var1, float var2, float var3, float var4, float var5, float var6, float var7) {
         this.out = var1;
         this.Mxx = var2;
         this.Mxy = var3;
         this.Mxt = var4;
         this.Myx = var5;
         this.Myy = var6;
         this.Myt = var7;
      }

      public void moveTo(float var1, float var2) {
         this.out.moveTo(var1 * this.Mxx + var2 * this.Mxy + this.Mxt, var1 * this.Myx + var2 * this.Myy + this.Myt);
      }

      public void lineTo(float var1, float var2) {
         this.out.lineTo(var1 * this.Mxx + var2 * this.Mxy + this.Mxt, var1 * this.Myx + var2 * this.Myy + this.Myt);
      }

      public void quadTo(float var1, float var2, float var3, float var4) {
         this.out.quadTo(var1 * this.Mxx + var2 * this.Mxy + this.Mxt, var1 * this.Myx + var2 * this.Myy + this.Myt, var3 * this.Mxx + var4 * this.Mxy + this.Mxt, var3 * this.Myx + var4 * this.Myy + this.Myt);
      }

      public void curveTo(float var1, float var2, float var3, float var4, float var5, float var6) {
         this.out.curveTo(var1 * this.Mxx + var2 * this.Mxy + this.Mxt, var1 * this.Myx + var2 * this.Myy + this.Myt, var3 * this.Mxx + var4 * this.Mxy + this.Mxt, var3 * this.Myx + var4 * this.Myy + this.Myt, var5 * this.Mxx + var6 * this.Mxy + this.Mxt, var5 * this.Myx + var6 * this.Myy + this.Myt);
      }

      public void closePath() {
         this.out.closePath();
      }

      public void pathDone() {
         this.out.pathDone();
      }

      public long getNativeConsumer() {
         return 0L;
      }
   }

   static final class ScaleFilter implements PathConsumer2D {
      private final PathConsumer2D out;
      private final float sx;
      private final float sy;
      private final float tx;
      private final float ty;

      ScaleFilter(PathConsumer2D var1, float var2, float var3, float var4, float var5) {
         this.out = var1;
         this.sx = var2;
         this.sy = var3;
         this.tx = var4;
         this.ty = var5;
      }

      public void moveTo(float var1, float var2) {
         this.out.moveTo(var1 * this.sx + this.tx, var2 * this.sy + this.ty);
      }

      public void lineTo(float var1, float var2) {
         this.out.lineTo(var1 * this.sx + this.tx, var2 * this.sy + this.ty);
      }

      public void quadTo(float var1, float var2, float var3, float var4) {
         this.out.quadTo(var1 * this.sx + this.tx, var2 * this.sy + this.ty, var3 * this.sx + this.tx, var4 * this.sy + this.ty);
      }

      public void curveTo(float var1, float var2, float var3, float var4, float var5, float var6) {
         this.out.curveTo(var1 * this.sx + this.tx, var2 * this.sy + this.ty, var3 * this.sx + this.tx, var4 * this.sy + this.ty, var5 * this.sx + this.tx, var6 * this.sy + this.ty);
      }

      public void closePath() {
         this.out.closePath();
      }

      public void pathDone() {
         this.out.pathDone();
      }

      public long getNativeConsumer() {
         return 0L;
      }
   }

   static final class TranslateFilter implements PathConsumer2D {
      private final PathConsumer2D out;
      private final float tx;
      private final float ty;

      TranslateFilter(PathConsumer2D var1, float var2, float var3) {
         this.out = var1;
         this.tx = var2;
         this.ty = var3;
      }

      public void moveTo(float var1, float var2) {
         this.out.moveTo(var1 + this.tx, var2 + this.ty);
      }

      public void lineTo(float var1, float var2) {
         this.out.lineTo(var1 + this.tx, var2 + this.ty);
      }

      public void quadTo(float var1, float var2, float var3, float var4) {
         this.out.quadTo(var1 + this.tx, var2 + this.ty, var3 + this.tx, var4 + this.ty);
      }

      public void curveTo(float var1, float var2, float var3, float var4, float var5, float var6) {
         this.out.curveTo(var1 + this.tx, var2 + this.ty, var3 + this.tx, var4 + this.ty, var5 + this.tx, var6 + this.ty);
      }

      public void closePath() {
         this.out.closePath();
      }

      public void pathDone() {
         this.out.pathDone();
      }

      public long getNativeConsumer() {
         return 0L;
      }
   }
}
