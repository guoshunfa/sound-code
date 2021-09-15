package sun.font;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;
import sun.misc.InnocuousThread;

class T2KFontScaler extends FontScaler {
   private int[] bwGlyphs;
   private static final int TRUETYPE_FONT = 1;
   private static final int TYPE1_FONT = 2;
   private long layoutTablePtr = 0L;

   private void initBWGlyphs() {
      if (this.font.get() != null && "Courier New".equals(((Font2D)this.font.get()).getFontName((Locale)null))) {
         this.bwGlyphs = new int[2];
         CharToGlyphMapper var1 = ((Font2D)this.font.get()).getMapper();
         this.bwGlyphs[0] = var1.charToGlyph('W');
         this.bwGlyphs[1] = var1.charToGlyph('w');
      }

   }

   private static native void initIDs(Class var0);

   private void invalidateScaler() throws FontScalerException {
      this.nativeScaler = 0L;
      this.font = null;
      throw new FontScalerException();
   }

   public T2KFontScaler(Font2D var1, int var2, boolean var3, int var4) {
      byte var5 = 1;
      if (var1 instanceof Type1Font) {
         var5 = 2;
      }

      this.font = new WeakReference(var1);
      this.initBWGlyphs();
      this.nativeScaler = this.initNativeScaler(var1, var5, var2, var3, var4, this.bwGlyphs);
   }

   synchronized StrikeMetrics getFontMetrics(long var1) throws FontScalerException {
      return this.nativeScaler != 0L ? this.getFontMetricsNative((Font2D)this.font.get(), var1, this.nativeScaler) : getNullScaler().getFontMetrics(0L);
   }

   synchronized float getGlyphAdvance(long var1, int var3) throws FontScalerException {
      return this.nativeScaler != 0L ? this.getGlyphAdvanceNative((Font2D)this.font.get(), var1, this.nativeScaler, var3) : getNullScaler().getGlyphAdvance(0L, var3);
   }

   synchronized void getGlyphMetrics(long var1, int var3, Point2D.Float var4) throws FontScalerException {
      if (this.nativeScaler != 0L) {
         this.getGlyphMetricsNative((Font2D)this.font.get(), var1, this.nativeScaler, var3, var4);
      } else {
         getNullScaler().getGlyphMetrics(0L, var3, var4);
      }

   }

   synchronized long getGlyphImage(long var1, int var3) throws FontScalerException {
      return this.nativeScaler != 0L ? this.getGlyphImageNative((Font2D)this.font.get(), var1, this.nativeScaler, var3) : getNullScaler().getGlyphImage(0L, var3);
   }

   synchronized Rectangle2D.Float getGlyphOutlineBounds(long var1, int var3) throws FontScalerException {
      return this.nativeScaler != 0L ? this.getGlyphOutlineBoundsNative((Font2D)this.font.get(), var1, this.nativeScaler, var3) : getNullScaler().getGlyphOutlineBounds(0L, var3);
   }

   synchronized GeneralPath getGlyphOutline(long var1, int var3, float var4, float var5) throws FontScalerException {
      return this.nativeScaler != 0L ? this.getGlyphOutlineNative((Font2D)this.font.get(), var1, this.nativeScaler, var3, var4, var5) : getNullScaler().getGlyphOutline(0L, var3, var4, var5);
   }

   synchronized GeneralPath getGlyphVectorOutline(long var1, int[] var3, int var4, float var5, float var6) throws FontScalerException {
      return this.nativeScaler != 0L ? this.getGlyphVectorOutlineNative((Font2D)this.font.get(), var1, this.nativeScaler, var3, var4, var5, var6) : getNullScaler().getGlyphVectorOutline(0L, var3, var4, var5, var6);
   }

   synchronized int getNumGlyphs() throws FontScalerException {
      return this.nativeScaler != 0L ? this.getNumGlyphsNative(this.nativeScaler) : getNullScaler().getNumGlyphs();
   }

   synchronized int getMissingGlyphCode() throws FontScalerException {
      return this.nativeScaler != 0L ? this.getMissingGlyphCodeNative(this.nativeScaler) : getNullScaler().getMissingGlyphCode();
   }

   synchronized int getGlyphCode(char var1) throws FontScalerException {
      return this.nativeScaler != 0L ? this.getGlyphCodeNative(this.nativeScaler, var1) : getNullScaler().getGlyphCode(var1);
   }

   synchronized long getLayoutTableCache() throws FontScalerException {
      if (this.nativeScaler == 0L) {
         return getNullScaler().getLayoutTableCache();
      } else {
         if (this.layoutTablePtr == 0L) {
            this.layoutTablePtr = this.getLayoutTableCacheNative(this.nativeScaler);
         }

         return this.layoutTablePtr;
      }
   }

   private synchronized void disposeScaler() {
      this.disposeNativeScaler(this.nativeScaler, this.layoutTablePtr);
      this.nativeScaler = 0L;
      this.layoutTablePtr = 0L;
   }

   public synchronized void dispose() {
      if (this.nativeScaler != 0L || this.layoutTablePtr != 0L) {
         Runnable var2 = new Runnable() {
            public void run() {
               T2KFontScaler.this.disposeScaler();
            }
         };
         (new InnocuousThread(var2)).start();
      }

   }

   synchronized Point2D.Float getGlyphPoint(long var1, int var3, int var4) throws FontScalerException {
      return this.nativeScaler != 0L ? this.getGlyphPointNative((Font2D)this.font.get(), var1, this.nativeScaler, var3, var4) : getNullScaler().getGlyphPoint(var1, var3, var4);
   }

   synchronized long getUnitsPerEm() {
      return this.nativeScaler != 0L ? this.getUnitsPerEMNative(this.nativeScaler) : getNullScaler().getUnitsPerEm();
   }

   synchronized long createScalerContext(double[] var1, int var2, int var3, float var4, float var5, boolean var6) {
      return this.nativeScaler != 0L ? this.createScalerContextNative(this.nativeScaler, var1, var2, var3, var4, var5, var6) : NullFontScaler.getNullScalerContext();
   }

   private native long initNativeScaler(Font2D var1, int var2, int var3, boolean var4, int var5, int[] var6);

   private native StrikeMetrics getFontMetricsNative(Font2D var1, long var2, long var4);

   private native float getGlyphAdvanceNative(Font2D var1, long var2, long var4, int var6);

   private native void getGlyphMetricsNative(Font2D var1, long var2, long var4, int var6, Point2D.Float var7);

   private native long getGlyphImageNative(Font2D var1, long var2, long var4, int var6);

   private native Rectangle2D.Float getGlyphOutlineBoundsNative(Font2D var1, long var2, long var4, int var6);

   private native GeneralPath getGlyphOutlineNative(Font2D var1, long var2, long var4, int var6, float var7, float var8);

   private native GeneralPath getGlyphVectorOutlineNative(Font2D var1, long var2, long var4, int[] var6, int var7, float var8, float var9);

   private native int getGlyphCodeNative(long var1, char var3);

   private native long getLayoutTableCacheNative(long var1);

   private native void disposeNativeScaler(long var1, long var3);

   private native int getNumGlyphsNative(long var1);

   private native int getMissingGlyphCodeNative(long var1);

   private native long getUnitsPerEMNative(long var1);

   private native long createScalerContextNative(long var1, double[] var3, int var4, int var5, float var6, float var7, boolean var8);

   private native Point2D.Float getGlyphPointNative(Font2D var1, long var2, long var4, int var6, int var7);

   void invalidateScalerContext(long var1) {
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            FontManagerNativeLibrary.load();
            System.loadLibrary("t2k");
            return null;
         }
      });
      initIDs(T2KFontScaler.class);
   }
}
