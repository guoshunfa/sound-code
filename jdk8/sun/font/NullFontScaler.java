package sun.font;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

class NullFontScaler extends FontScaler {
   NullFontScaler() {
   }

   public NullFontScaler(Font2D var1, int var2, boolean var3, int var4) {
   }

   StrikeMetrics getFontMetrics(long var1) {
      return new StrikeMetrics(240.0F, 240.0F, 240.0F, 240.0F, 240.0F, 240.0F, 240.0F, 240.0F, 240.0F, 240.0F);
   }

   float getGlyphAdvance(long var1, int var3) {
      return 0.0F;
   }

   void getGlyphMetrics(long var1, int var3, Point2D.Float var4) {
      var4.x = 0.0F;
      var4.y = 0.0F;
   }

   Rectangle2D.Float getGlyphOutlineBounds(long var1, int var3) {
      return new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   }

   GeneralPath getGlyphOutline(long var1, int var3, float var4, float var5) {
      return new GeneralPath();
   }

   GeneralPath getGlyphVectorOutline(long var1, int[] var3, int var4, float var5, float var6) {
      return new GeneralPath();
   }

   long getLayoutTableCache() {
      return 0L;
   }

   long createScalerContext(double[] var1, int var2, int var3, float var4, float var5, boolean var6) {
      return getNullScalerContext();
   }

   void invalidateScalerContext(long var1) {
   }

   int getNumGlyphs() throws FontScalerException {
      return 1;
   }

   int getMissingGlyphCode() throws FontScalerException {
      return 0;
   }

   int getGlyphCode(char var1) throws FontScalerException {
      return 0;
   }

   long getUnitsPerEm() {
      return 2048L;
   }

   Point2D.Float getGlyphPoint(long var1, int var3, int var4) {
      return null;
   }

   static native long getNullScalerContext();

   native long getGlyphImage(long var1, int var3);
}
