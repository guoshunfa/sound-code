package sun.font;

import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

class FontStrikeDisposer implements DisposerRecord, Disposer.PollDisposable {
   Font2D font2D;
   FontStrikeDesc desc;
   long[] longGlyphImages;
   int[] intGlyphImages;
   int[][] segIntGlyphImages;
   long[][] segLongGlyphImages;
   long pScalerContext = 0L;
   boolean disposed = false;
   boolean comp = false;

   public FontStrikeDisposer(Font2D var1, FontStrikeDesc var2, long var3, int[] var5) {
      this.font2D = var1;
      this.desc = var2;
      this.pScalerContext = var3;
      this.intGlyphImages = var5;
   }

   public FontStrikeDisposer(Font2D var1, FontStrikeDesc var2, long var3, long[] var5) {
      this.font2D = var1;
      this.desc = var2;
      this.pScalerContext = var3;
      this.longGlyphImages = var5;
   }

   public FontStrikeDisposer(Font2D var1, FontStrikeDesc var2, long var3) {
      this.font2D = var1;
      this.desc = var2;
      this.pScalerContext = var3;
   }

   public FontStrikeDisposer(Font2D var1, FontStrikeDesc var2) {
      this.font2D = var1;
      this.desc = var2;
      this.comp = true;
   }

   public synchronized void dispose() {
      if (!this.disposed) {
         this.font2D.removeFromCache(this.desc);
         StrikeCache.disposeStrike(this);
         this.disposed = true;
      }

   }
}
