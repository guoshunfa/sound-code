package sun.java2d.xr;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

public class XRMaskImage {
   private static final int MASK_SCALE_FACTOR = 8;
   private static final int BLIT_MASK_SIZE = 8;
   Dimension blitMaskDimensions = new Dimension(8, 8);
   int blitMaskPixmap;
   int blitMaskPicture;
   int lastMaskWidth = 0;
   int lastMaskHeight = 0;
   int lastEA = -1;
   AffineTransform lastMaskTransform;
   XRCompositeManager xrMgr;
   XRBackend con;

   public XRMaskImage(XRCompositeManager var1, int var2) {
      this.xrMgr = var1;
      this.con = var1.getBackend();
      this.initBlitMask(var2, 8, 8);
   }

   public int prepareBlitMask(XRSurfaceData var1, AffineTransform var2, int var3, int var4) {
      int var5 = Math.max(var3 / 8, 1);
      int var6 = Math.max(var4 / 8, 1);
      var2.scale((double)var3 / (double)var5, (double)var4 / (double)var6);

      try {
         var2.invert();
      } catch (NoninvertibleTransformException var8) {
         var2.setToIdentity();
      }

      this.ensureBlitMaskSize(var5, var6);
      if (this.lastMaskTransform == null || !this.lastMaskTransform.equals(var2)) {
         this.con.setPictureTransform(this.blitMaskPicture, var2);
         this.lastMaskTransform = var2;
      }

      int var7 = this.xrMgr.getAlphaColor().getAlpha();
      if (this.lastMaskWidth != var5 || this.lastMaskHeight != var6 || this.lastEA != var7) {
         if (this.lastMaskWidth > var5 || this.lastMaskHeight > var6) {
            this.con.renderRectangle(this.blitMaskPicture, (byte)0, XRColor.NO_ALPHA, 0, 0, this.lastMaskWidth, this.lastMaskHeight);
         }

         this.con.renderRectangle(this.blitMaskPicture, (byte)1, this.xrMgr.getAlphaColor(), 0, 0, var5, var6);
         this.lastEA = var7;
      }

      this.lastMaskWidth = var5;
      this.lastMaskHeight = var6;
      return this.blitMaskPicture;
   }

   private void initBlitMask(int var1, int var2, int var3) {
      int var4 = this.con.createPixmap(var1, 8, var2, var3);
      int var5 = this.con.createPicture(var4, 2);
      if (this.blitMaskPixmap != 0) {
         this.con.freePixmap(this.blitMaskPixmap);
         this.con.freePicture(this.blitMaskPicture);
      }

      this.blitMaskPixmap = var4;
      this.blitMaskPicture = var5;
      this.con.renderRectangle(this.blitMaskPicture, (byte)0, XRColor.NO_ALPHA, 0, 0, var2, var3);
      this.blitMaskDimensions.width = var2;
      this.blitMaskDimensions.height = var3;
      this.lastMaskWidth = 0;
      this.lastMaskHeight = 0;
      this.lastMaskTransform = null;
   }

   private void ensureBlitMaskSize(int var1, int var2) {
      if (var1 > this.blitMaskDimensions.width || var2 > this.blitMaskDimensions.height) {
         int var3 = Math.max(var1, this.blitMaskDimensions.width);
         int var4 = Math.max(var2, this.blitMaskDimensions.height);
         this.initBlitMask(this.blitMaskPixmap, var3, var4);
      }

   }
}
