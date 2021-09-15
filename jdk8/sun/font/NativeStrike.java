package sun.font;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

class NativeStrike extends PhysicalStrike {
   NativeFont nativeFont;
   int numGlyphs;
   AffineTransform invertDevTx;
   AffineTransform fontTx;

   private int getNativePointSize() {
      double[] var1 = new double[4];
      this.desc.glyphTx.getMatrix(var1);
      this.fontTx = new AffineTransform(var1);
      if (!this.desc.devTx.isIdentity() && this.desc.devTx.getType() != 1) {
         try {
            this.invertDevTx = this.desc.devTx.createInverse();
            this.fontTx.concatenate(this.invertDevTx);
         } catch (NoninvertibleTransformException var6) {
            var6.printStackTrace();
         }
      }

      Point2D.Float var2 = new Point2D.Float(1.0F, 1.0F);
      this.fontTx.deltaTransform(var2, var2);
      double var3 = (double)Math.abs(var2.y);
      int var5 = this.fontTx.getType();
      if ((var5 & -3) == 0 && this.fontTx.getScaleY() > 0.0D) {
         this.fontTx = null;
      } else {
         this.fontTx.scale(1.0D / var3, 1.0D / var3);
      }

      return (int)var3;
   }

   NativeStrike(NativeFont var1, FontStrikeDesc var2) {
      super(var1, var2);
      this.nativeFont = var1;
      int var3;
      if (var1.isBitmapDelegate) {
         var3 = var2.glyphTx.getType();
         if ((var3 & -3) != 0 || var2.glyphTx.getScaleX() <= 0.0D) {
            this.numGlyphs = 0;
            return;
         }
      }

      var3 = this.getNativePointSize();
      byte[] var4 = var1.getPlatformNameBytes(var3);
      double var5 = Math.abs(var2.devTx.getScaleX());
      this.pScalerContext = this.createScalerContext(var4, var3, var5);
      if (this.pScalerContext == 0L) {
         SunFontManager.getInstance().deRegisterBadFont(var1);
         this.pScalerContext = this.createNullScalerContext();
         this.numGlyphs = 0;
         if (FontUtilities.isLogging()) {
            FontUtilities.getLogger().severe("Could not create native strike " + new String(var4));
         }

      } else {
         this.numGlyphs = var1.getMapper().getNumGlyphs();
         this.disposer = new NativeStrikeDisposer(var1, var2, this.pScalerContext);
      }
   }

   private boolean usingIntGlyphImages() {
      if (this.intGlyphImages != null) {
         return true;
      } else if (longAddresses) {
         return false;
      } else {
         int var1 = this.getMaxGlyph(this.pScalerContext);
         if (var1 < this.numGlyphs) {
            var1 = this.numGlyphs;
         }

         this.intGlyphImages = new int[var1];
         this.disposer.intGlyphImages = this.intGlyphImages;
         return true;
      }
   }

   private long[] getLongGlyphImages() {
      if (this.longGlyphImages == null && longAddresses) {
         int var1 = this.getMaxGlyph(this.pScalerContext);
         if (var1 < this.numGlyphs) {
            var1 = this.numGlyphs;
         }

         this.longGlyphImages = new long[var1];
         this.disposer.longGlyphImages = this.longGlyphImages;
      }

      return this.longGlyphImages;
   }

   NativeStrike(NativeFont var1, FontStrikeDesc var2, boolean var3) {
      super(var1, var2);
      this.nativeFont = var1;
      int var4 = (int)var2.glyphTx.getScaleY();
      double var5 = var2.devTx.getScaleX();
      byte[] var7 = var1.getPlatformNameBytes(var4);
      this.pScalerContext = this.createScalerContext(var7, var4, var5);
      int var8 = var1.getMapper().getNumGlyphs();
   }

   StrikeMetrics getFontMetrics() {
      if (this.strikeMetrics == null) {
         if (this.pScalerContext != 0L) {
            this.strikeMetrics = this.nativeFont.getFontMetrics(this.pScalerContext);
         }

         if (this.strikeMetrics != null && this.fontTx != null) {
            this.strikeMetrics.convertToUserSpace(this.fontTx);
         }
      }

      return this.strikeMetrics;
   }

   private native long createScalerContext(byte[] var1, int var2, double var3);

   private native int getMaxGlyph(long var1);

   private native long createNullScalerContext();

   void getGlyphImagePtrs(int[] var1, long[] var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         var2[var4] = this.getGlyphImagePtr(var1[var4]);
      }

   }

   long getGlyphImagePtr(int var1) {
      long var2;
      if (this.usingIntGlyphImages()) {
         if ((var2 = (long)this.intGlyphImages[var1] & 4294967295L) != 0L) {
            return var2;
         } else {
            var2 = this.nativeFont.getGlyphImage(this.pScalerContext, var1);
            synchronized(this) {
               if (this.intGlyphImages[var1] == 0) {
                  this.intGlyphImages[var1] = (int)var2;
                  return var2;
               } else {
                  StrikeCache.freeIntPointer((int)var2);
                  return (long)this.intGlyphImages[var1] & 4294967295L;
               }
            }
         }
      } else if ((var2 = this.getLongGlyphImages()[var1]) != 0L) {
         return var2;
      } else {
         var2 = this.nativeFont.getGlyphImage(this.pScalerContext, var1);
         synchronized(this) {
            if (this.longGlyphImages[var1] == 0L) {
               this.longGlyphImages[var1] = var2;
               return var2;
            } else {
               StrikeCache.freeLongPointer(var2);
               return this.longGlyphImages[var1];
            }
         }
      }
   }

   long getGlyphImagePtrNoCache(int var1) {
      return this.nativeFont.getGlyphImageNoDefault(this.pScalerContext, var1);
   }

   void getGlyphImageBounds(int var1, Point2D.Float var2, Rectangle var3) {
   }

   Point2D.Float getGlyphMetrics(int var1) {
      Point2D.Float var2 = new Point2D.Float(this.getGlyphAdvance(var1), 0.0F);
      return var2;
   }

   float getGlyphAdvance(int var1) {
      return this.nativeFont.getGlyphAdvance(this.pScalerContext, var1);
   }

   Rectangle2D.Float getGlyphOutlineBounds(int var1) {
      return this.nativeFont.getGlyphOutlineBounds(this.pScalerContext, var1);
   }

   GeneralPath getGlyphOutline(int var1, float var2, float var3) {
      return new GeneralPath();
   }

   GeneralPath getGlyphVectorOutline(int[] var1, float var2, float var3) {
      return new GeneralPath();
   }
}
