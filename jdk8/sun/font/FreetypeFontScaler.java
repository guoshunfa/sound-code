package sun.font;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.ref.WeakReference;

class FreetypeFontScaler extends FontScaler {
   private static final int TRUETYPE_FONT = 1;
   private static final int TYPE1_FONT = 2;

   private static native void initIDs(Class var0);

   private void invalidateScaler() throws FontScalerException {
      this.nativeScaler = 0L;
      this.font = null;
      throw new FontScalerException();
   }

   public FreetypeFontScaler(Font2D var1, int var2, boolean var3, int var4) {
      byte var5 = 1;
      if (var1 instanceof Type1Font) {
         var5 = 2;
      }

      this.nativeScaler = this.initNativeScaler(var1, var5, var2, var3, var4);
      this.font = new WeakReference(var1);
   }

   synchronized StrikeMetrics getFontMetrics(long var1) throws FontScalerException {
      return this.nativeScaler != 0L ? this.getFontMetricsNative((Font2D)this.font.get(), var1, this.nativeScaler) : FontScaler.getNullScaler().getFontMetrics(0L);
   }

   synchronized float getGlyphAdvance(long var1, int var3) throws FontScalerException {
      return this.nativeScaler != 0L ? this.getGlyphAdvanceNative((Font2D)this.font.get(), var1, this.nativeScaler, var3) : FontScaler.getNullScaler().getGlyphAdvance(0L, var3);
   }

   synchronized void getGlyphMetrics(long var1, int var3, Point2D.Float var4) throws FontScalerException {
      if (this.nativeScaler != 0L) {
         this.getGlyphMetricsNative((Font2D)this.font.get(), var1, this.nativeScaler, var3, var4);
      } else {
         FontScaler.getNullScaler().getGlyphMetrics(0L, var3, var4);
      }
   }

   synchronized long getGlyphImage(long var1, int var3) throws FontScalerException {
      return this.nativeScaler != 0L ? this.getGlyphImageNative((Font2D)this.font.get(), var1, this.nativeScaler, var3) : FontScaler.getNullScaler().getGlyphImage(0L, var3);
   }

   synchronized Rectangle2D.Float getGlyphOutlineBounds(long var1, int var3) throws FontScalerException {
      return this.nativeScaler != 0L ? this.getGlyphOutlineBoundsNative((Font2D)this.font.get(), var1, this.nativeScaler, var3) : FontScaler.getNullScaler().getGlyphOutlineBounds(0L, var3);
   }

   synchronized GeneralPath getGlyphOutline(long var1, int var3, float var4, float var5) throws FontScalerException {
      return this.nativeScaler != 0L ? this.getGlyphOutlineNative((Font2D)this.font.get(), var1, this.nativeScaler, var3, var4, var5) : FontScaler.getNullScaler().getGlyphOutline(0L, var3, var4, var5);
   }

   synchronized GeneralPath getGlyphVectorOutline(long var1, int[] var3, int var4, float var5, float var6) throws FontScalerException {
      return this.nativeScaler != 0L ? this.getGlyphVectorOutlineNative((Font2D)this.font.get(), var1, this.nativeScaler, var3, var4, var5, var6) : FontScaler.getNullScaler().getGlyphVectorOutline(0L, var3, var4, var5, var6);
   }

   synchronized long getLayoutTableCache() throws FontScalerException {
      return this.getLayoutTableCacheNative(this.nativeScaler);
   }

   public synchronized void dispose() {
      if (this.nativeScaler != 0L) {
         this.disposeNativeScaler((Font2D)this.font.get(), this.nativeScaler);
         this.nativeScaler = 0L;
      }

   }

   synchronized int getNumGlyphs() throws FontScalerException {
      return this.nativeScaler != 0L ? this.getNumGlyphsNative(this.nativeScaler) : FontScaler.getNullScaler().getNumGlyphs();
   }

   synchronized int getMissingGlyphCode() throws FontScalerException {
      return this.nativeScaler != 0L ? this.getMissingGlyphCodeNative(this.nativeScaler) : FontScaler.getNullScaler().getMissingGlyphCode();
   }

   synchronized int getGlyphCode(char var1) throws FontScalerException {
      return this.nativeScaler != 0L ? this.getGlyphCodeNative((Font2D)this.font.get(), this.nativeScaler, var1) : FontScaler.getNullScaler().getGlyphCode(var1);
   }

   synchronized Point2D.Float getGlyphPoint(long var1, int var3, int var4) throws FontScalerException {
      return this.nativeScaler != 0L ? this.getGlyphPointNative((Font2D)this.font.get(), var1, this.nativeScaler, var3, var4) : FontScaler.getNullScaler().getGlyphPoint(var1, var3, var4);
   }

   synchronized long getUnitsPerEm() {
      return this.getUnitsPerEMNative(this.nativeScaler);
   }

   long createScalerContext(double[] var1, int var2, int var3, float var4, float var5, boolean var6) {
      return this.nativeScaler != 0L ? this.createScalerContextNative(this.nativeScaler, var1, var2, var3, var4, var5) : NullFontScaler.getNullScalerContext();
   }

   private native long initNativeScaler(Font2D var1, int var2, int var3, boolean var4, int var5);

   private native StrikeMetrics getFontMetricsNative(Font2D var1, long var2, long var4);

   private native float getGlyphAdvanceNative(Font2D var1, long var2, long var4, int var6);

   private native void getGlyphMetricsNative(Font2D var1, long var2, long var4, int var6, Point2D.Float var7);

   private native long getGlyphImageNative(Font2D var1, long var2, long var4, int var6);

   private native Rectangle2D.Float getGlyphOutlineBoundsNative(Font2D var1, long var2, long var4, int var6);

   private native GeneralPath getGlyphOutlineNative(Font2D var1, long var2, long var4, int var6, float var7, float var8);

   private native GeneralPath getGlyphVectorOutlineNative(Font2D var1, long var2, long var4, int[] var6, int var7, float var8, float var9);

   native Point2D.Float getGlyphPointNative(Font2D var1, long var2, long var4, int var6, int var7);

   private native long getLayoutTableCacheNative(long var1);

   private native void disposeNativeScaler(Font2D var1, long var2);

   private native int getGlyphCodeNative(Font2D var1, long var2, char var4);

   private native int getNumGlyphsNative(long var1);

   private native int getMissingGlyphCodeNative(long var1);

   private native long getUnitsPerEMNative(long var1);

   native long createScalerContextNative(long var1, double[] var3, int var4, int var5, float var6, float var7);

   void invalidateScalerContext(long var1) {
   }

   static {
      FontManagerNativeLibrary.load();
      initIDs(FreetypeFontScaler.class);
   }
}
