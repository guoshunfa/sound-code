package sun.font;

import sun.awt.SunToolkit;
import sun.java2d.SunGraphics2D;
import sun.java2d.pipe.GlyphListPipe;
import sun.java2d.xr.GrowableEltArray;
import sun.java2d.xr.XRBackend;
import sun.java2d.xr.XRCompositeManager;
import sun.java2d.xr.XRSurfaceData;

public class XRTextRenderer extends GlyphListPipe {
   static final int MAX_ELT_GLYPH_COUNT = 253;
   XRGlyphCache glyphCache;
   XRCompositeManager maskBuffer;
   XRBackend backend;
   GrowableEltArray eltList;

   public XRTextRenderer(XRCompositeManager var1) {
      this.glyphCache = new XRGlyphCache(var1);
      this.maskBuffer = var1;
      this.backend = var1.getBackend();
      this.eltList = new GrowableEltArray(64);
   }

   protected void drawGlyphList(SunGraphics2D var1, GlyphList var2) {
      if (var2.getNumGlyphs() != 0) {
         try {
            SunToolkit.awtLock();
            XRSurfaceData var3 = (XRSurfaceData)var1.surfaceData;
            var3.validateAsDestination((SunGraphics2D)null, var1.getCompClip());
            var3.maskBuffer.validateCompositeState(var1.composite, var1.transform, var1.paint, var1);
            float var4 = var2.getX();
            float var5 = var2.getY();
            int var6 = 0;
            int var7 = 0;
            if (var2.isSubPixPos()) {
               var4 += 0.1666667F;
               var5 += 0.1666667F;
            } else {
               var4 += 0.5F;
               var5 += 0.5F;
            }

            XRGlyphCacheEntry[] var8 = this.glyphCache.cacheGlyphs(var2);
            boolean var9 = false;
            int var10 = var8[0].getGlyphSet();
            int var11 = -1;
            var2.getBounds();
            float[] var12 = var2.getPositions();

            int var13;
            for(var13 = 0; var13 < var2.getNumGlyphs(); ++var13) {
               var2.setGlyphIndex(var13);
               XRGlyphCacheEntry var14 = var8[var13];
               this.eltList.getGlyphs().addInt(var14.getGlyphID());
               int var15 = var14.getGlyphSet();
               var9 |= var15 == this.glyphCache.lcdGlyphSet;
               boolean var16 = false;
               boolean var17 = false;
               if (!var2.usePositions() && var14.getXAdvance() == (float)var14.getXOff() && var14.getYAdvance() == (float)var14.getYOff() && var15 == var10 && var11 >= 0 && this.eltList.getCharCnt(var11) != 253) {
                  this.eltList.setCharCnt(var11, this.eltList.getCharCnt(var11) + 1);
               } else {
                  var11 = this.eltList.getNextIndex();
                  this.eltList.setCharCnt(var11, 1);
                  var10 = var15;
                  this.eltList.setGlyphSet(var11, var15);
                  int var23;
                  int var24;
                  if (var2.usePositions()) {
                     float var18 = var12[var13 * 2] + var4;
                     float var19 = var12[var13 * 2 + 1] + var5;
                     var23 = (int)Math.floor((double)var18);
                     var24 = (int)Math.floor((double)var19);
                     var4 -= (float)var14.getXOff();
                     var5 -= (float)var14.getYOff();
                  } else {
                     var23 = (int)Math.floor((double)var4);
                     var24 = (int)Math.floor((double)var5);
                     var4 += var14.getXAdvance() - (float)var14.getXOff();
                     var5 += var14.getYAdvance() - (float)var14.getYOff();
                  }

                  this.eltList.setXOff(var11, var23 - var6);
                  this.eltList.setYOff(var11, var24 - var7);
                  var6 = var23;
                  var7 = var24;
               }
            }

            var13 = var9 ? 0 : 2;
            this.maskBuffer.compositeText(var3, (int)var2.getX(), (int)var2.getY(), 0, var13, this.eltList);
            this.eltList.clear();
         } finally {
            SunToolkit.awtUnlock();
         }

      }
   }
}
