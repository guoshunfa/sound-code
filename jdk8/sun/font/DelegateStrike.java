package sun.font;

import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

class DelegateStrike extends NativeStrike {
   private FontStrike delegateStrike;

   DelegateStrike(NativeFont var1, FontStrikeDesc var2, FontStrike var3) {
      super(var1, var2);
      this.delegateStrike = var3;
   }

   StrikeMetrics getFontMetrics() {
      if (this.strikeMetrics == null) {
         if (this.pScalerContext != 0L) {
            this.strikeMetrics = super.getFontMetrics();
         }

         if (this.strikeMetrics == null) {
            this.strikeMetrics = this.delegateStrike.getFontMetrics();
         }
      }

      return this.strikeMetrics;
   }

   void getGlyphImagePtrs(int[] var1, long[] var2, int var3) {
      this.delegateStrike.getGlyphImagePtrs(var1, var2, var3);
   }

   long getGlyphImagePtr(int var1) {
      return this.delegateStrike.getGlyphImagePtr(var1);
   }

   void getGlyphImageBounds(int var1, Point2D.Float var2, Rectangle var3) {
      this.delegateStrike.getGlyphImageBounds(var1, var2, var3);
   }

   Point2D.Float getGlyphMetrics(int var1) {
      return this.delegateStrike.getGlyphMetrics(var1);
   }

   float getGlyphAdvance(int var1) {
      return this.delegateStrike.getGlyphAdvance(var1);
   }

   Point2D.Float getCharMetrics(char var1) {
      return this.delegateStrike.getCharMetrics(var1);
   }

   float getCodePointAdvance(int var1) {
      if (var1 < 0 || var1 >= 65536) {
         var1 = 65535;
      }

      return this.delegateStrike.getGlyphAdvance(var1);
   }

   Rectangle2D.Float getGlyphOutlineBounds(int var1) {
      return this.delegateStrike.getGlyphOutlineBounds(var1);
   }

   GeneralPath getGlyphOutline(int var1, float var2, float var3) {
      return this.delegateStrike.getGlyphOutline(var1, var2, var3);
   }

   GeneralPath getGlyphVectorOutline(int[] var1, float var2, float var3) {
      return this.delegateStrike.getGlyphVectorOutline(var1, var2, var3);
   }
}
