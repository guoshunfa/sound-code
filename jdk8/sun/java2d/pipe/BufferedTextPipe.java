package sun.java2d.pipe;

import java.awt.AlphaComposite;
import java.awt.Composite;
import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;

public abstract class BufferedTextPipe extends GlyphListPipe {
   private static final int BYTES_PER_GLYPH_IMAGE = 8;
   private static final int BYTES_PER_GLYPH_POSITION = 8;
   private static final int OFFSET_CONTRAST = 8;
   private static final int OFFSET_RGBORDER = 2;
   private static final int OFFSET_SUBPIXPOS = 1;
   private static final int OFFSET_POSITIONS = 0;
   protected final RenderQueue rq;

   private static int createPackedParams(SunGraphics2D var0, GlyphList var1) {
      return (var1.usePositions() ? 1 : 0) << 0 | (var1.isSubPixPos() ? 1 : 0) << 1 | (var1.isRGBOrder() ? 1 : 0) << 2 | (var0.lcdTextContrast & 255) << 8;
   }

   protected BufferedTextPipe(RenderQueue var1) {
      this.rq = var1;
   }

   protected void drawGlyphList(SunGraphics2D var1, GlyphList var2) {
      Object var3 = var1.composite;
      if (var3 == AlphaComposite.Src) {
         var3 = AlphaComposite.SrcOver;
      }

      this.rq.lock();

      try {
         this.validateContext(var1, (Composite)var3);
         this.enqueueGlyphList(var1, var2);
      } finally {
         this.rq.unlock();
      }

   }

   private void enqueueGlyphList(final SunGraphics2D var1, final GlyphList var2) {
      RenderBuffer var3 = this.rq.getBuffer();
      final int var4 = var2.getNumGlyphs();
      int var5 = var4 * 8;
      int var6 = var2.usePositions() ? var4 * 8 : 0;
      int var7 = 24 + var5 + var6;
      final long[] var8 = var2.getImages();
      final float var9 = var2.getX() + 0.5F;
      final float var10 = var2.getY() + 0.5F;
      this.rq.addReference(var2.getStrike());
      if (var7 <= var3.capacity()) {
         if (var7 > var3.remaining()) {
            this.rq.flushNow();
         }

         this.rq.ensureAlignment(20);
         var3.putInt(40);
         var3.putInt(var4);
         var3.putInt(createPackedParams(var1, var2));
         var3.putFloat(var9);
         var3.putFloat(var10);
         var3.put((long[])var8, 0, var4);
         if (var2.usePositions()) {
            float[] var11 = var2.getPositions();
            var3.put((float[])var11, 0, 2 * var4);
         }
      } else {
         this.rq.flushAndInvokeNow(new Runnable() {
            public void run() {
               BufferedTextPipe.this.drawGlyphList(var4, var2.usePositions(), var2.isSubPixPos(), var2.isRGBOrder(), var1.lcdTextContrast, var9, var10, var8, var2.getPositions());
            }
         });
      }

   }

   protected abstract void drawGlyphList(int var1, boolean var2, boolean var3, boolean var4, int var5, float var6, float var7, long[] var8, float[] var9);

   protected abstract void validateContext(SunGraphics2D var1, Composite var2);
}
