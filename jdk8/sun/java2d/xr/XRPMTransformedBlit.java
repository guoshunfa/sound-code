package sun.java2d.xr;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import sun.awt.SunToolkit;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.SurfaceType;
import sun.java2d.loops.TransformBlit;
import sun.java2d.pipe.Region;

class XRPMTransformedBlit extends TransformBlit {
   final Rectangle compositeBounds = new Rectangle();
   final double[] srcCoords = new double[8];
   final double[] dstCoords = new double[8];

   public XRPMTransformedBlit(SurfaceType var1, SurfaceType var2) {
      super(var1, CompositeType.AnyAlpha, var2);
   }

   protected void adjustCompositeBounds(boolean var1, AffineTransform var2, int var3, int var4, int var5, int var6) {
      this.srcCoords[0] = (double)var3;
      this.srcCoords[1] = (double)var4;
      this.srcCoords[2] = (double)(var3 + var5);
      this.srcCoords[3] = (double)(var4 + var6);
      double var7;
      double var9;
      double var11;
      double var13;
      if (var1) {
         var2.transform((double[])this.srcCoords, 0, (double[])this.dstCoords, 0, 2);
         var7 = Math.min(this.dstCoords[0], this.dstCoords[2]);
         var9 = Math.min(this.dstCoords[1], this.dstCoords[3]);
         var11 = Math.max(this.dstCoords[0], this.dstCoords[2]);
         var13 = Math.max(this.dstCoords[1], this.dstCoords[3]);
         var7 = Math.ceil(var7 - 0.5D);
         var9 = Math.ceil(var9 - 0.5D);
         var11 = Math.ceil(var11 - 0.5D);
         var13 = Math.ceil(var13 - 0.5D);
      } else {
         this.srcCoords[4] = (double)var3;
         this.srcCoords[5] = (double)(var4 + var6);
         this.srcCoords[6] = (double)(var3 + var5);
         this.srcCoords[7] = (double)var4;
         var2.transform((double[])this.srcCoords, 0, (double[])this.dstCoords, 0, 4);
         var7 = Math.min(this.dstCoords[0], Math.min(this.dstCoords[2], Math.min(this.dstCoords[4], this.dstCoords[6])));
         var9 = Math.min(this.dstCoords[1], Math.min(this.dstCoords[3], Math.min(this.dstCoords[5], this.dstCoords[7])));
         var11 = Math.max(this.dstCoords[0], Math.max(this.dstCoords[2], Math.max(this.dstCoords[4], this.dstCoords[6])));
         var13 = Math.max(this.dstCoords[1], Math.max(this.dstCoords[3], Math.max(this.dstCoords[5], this.dstCoords[7])));
         var7 = Math.floor(var7);
         var9 = Math.floor(var9);
         var11 = Math.ceil(var11);
         var13 = Math.ceil(var13);
      }

      this.compositeBounds.x = (int)var7;
      this.compositeBounds.y = (int)var9;
      this.compositeBounds.width = (int)(var11 - var7);
      this.compositeBounds.height = (int)(var13 - var9);
   }

   public void Transform(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, AffineTransform var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12) {
      try {
         SunToolkit.awtLock();
         XRSurfaceData var13 = (XRSurfaceData)var2;
         XRSurfaceData var14 = (XRSurfaceData)var1;
         XRCompositeManager var15 = XRCompositeManager.getInstance(var14);
         float var16 = ((AlphaComposite)var3).getAlpha();
         int var17 = XRUtils.ATransOpToXRQuality(var6);
         boolean var18 = XRUtils.isTransformQuadrantRotated(var5);
         this.adjustCompositeBounds(var18, var5, var9, var10, var11, var12);
         var13.validateAsDestination((SunGraphics2D)null, var4);
         var13.maskBuffer.validateCompositeState(var3, (AffineTransform)null, (Paint)null, (SunGraphics2D)null);
         AffineTransform var19 = AffineTransform.getTranslateInstance((double)(-this.compositeBounds.x), (double)(-this.compositeBounds.y));
         var19.concatenate(var5);
         AffineTransform var20 = (AffineTransform)var19.clone();
         var19.translate((double)(-var7), (double)(-var8));

         try {
            var19.invert();
         } catch (NoninvertibleTransformException var26) {
            var19.setToIdentity();
         }

         if (var17 != 0 && (!var18 || var16 != 1.0F)) {
            XRMaskImage var28 = var14.maskBuffer.getMaskImage();
            int var22 = var18 ? var15.getExtraAlphaMask() : var28.prepareBlitMask(var13, var20, var11, var12);
            var14.validateAsSource(var19, 2, var17);
            var13.maskBuffer.con.renderComposite(var15.getCompRule(), var14.picture, var22, var13.picture, 0, 0, 0, 0, this.compositeBounds.x, this.compositeBounds.y, this.compositeBounds.width, this.compositeBounds.height);
         } else {
            int var21 = var17 == 0 ? 0 : 2;
            var14.validateAsSource(var19, var21, var17);
            var13.maskBuffer.compositeBlit(var14, var13, 0, 0, this.compositeBounds.x, this.compositeBounds.y, this.compositeBounds.width, this.compositeBounds.height);
         }
      } finally {
         SunToolkit.awtUnlock();
      }

   }
}
