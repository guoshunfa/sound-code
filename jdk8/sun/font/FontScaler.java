package sun.font;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

public abstract class FontScaler implements DisposerRecord {
   private static FontScaler nullScaler = null;
   private static Constructor<FontScaler> scalerConstructor = null;
   protected WeakReference<Font2D> font = null;
   protected long nativeScaler = 0L;
   protected boolean disposed = false;

   public static FontScaler getScaler(Font2D var0, int var1, boolean var2, int var3) {
      FontScaler var4 = null;

      try {
         Object[] var5 = new Object[]{var0, var1, var2, var3};
         var4 = (FontScaler)scalerConstructor.newInstance(var5);
         Disposer.addObjectRecord(var0, var4);
      } catch (Throwable var7) {
         var4 = nullScaler;
         FontManager var6 = FontManagerFactory.getInstance();
         var6.deRegisterBadFont(var0);
      }

      return var4;
   }

   public static synchronized FontScaler getNullScaler() {
      if (nullScaler == null) {
         nullScaler = new NullFontScaler();
      }

      return nullScaler;
   }

   abstract StrikeMetrics getFontMetrics(long var1) throws FontScalerException;

   abstract float getGlyphAdvance(long var1, int var3) throws FontScalerException;

   abstract void getGlyphMetrics(long var1, int var3, Point2D.Float var4) throws FontScalerException;

   abstract long getGlyphImage(long var1, int var3) throws FontScalerException;

   abstract Rectangle2D.Float getGlyphOutlineBounds(long var1, int var3) throws FontScalerException;

   abstract GeneralPath getGlyphOutline(long var1, int var3, float var4, float var5) throws FontScalerException;

   abstract GeneralPath getGlyphVectorOutline(long var1, int[] var3, int var4, float var5, float var6) throws FontScalerException;

   public void dispose() {
   }

   abstract int getNumGlyphs() throws FontScalerException;

   abstract int getMissingGlyphCode() throws FontScalerException;

   abstract int getGlyphCode(char var1) throws FontScalerException;

   abstract long getLayoutTableCache() throws FontScalerException;

   abstract Point2D.Float getGlyphPoint(long var1, int var3, int var4) throws FontScalerException;

   abstract long getUnitsPerEm();

   abstract long createScalerContext(double[] var1, int var2, int var3, float var4, float var5, boolean var6);

   abstract void invalidateScalerContext(long var1);

   static {
      Class var0 = null;
      Class[] var1 = new Class[]{Font2D.class, Integer.TYPE, Boolean.TYPE, Integer.TYPE};

      try {
         if (FontUtilities.isOpenJDK) {
            var0 = Class.forName("sun.font.FreetypeFontScaler");
         } else {
            var0 = Class.forName("sun.font.T2KFontScaler");
         }
      } catch (ClassNotFoundException var4) {
         var0 = NullFontScaler.class;
      }

      try {
         scalerConstructor = var0.getConstructor(var1);
      } catch (NoSuchMethodException var3) {
      }

   }
}
