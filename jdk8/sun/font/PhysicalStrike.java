package sun.font;

import java.awt.geom.Point2D;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PhysicalStrike extends FontStrike {
   static final long INTMASK = 4294967295L;
   static boolean longAddresses;
   private PhysicalFont physicalFont;
   protected CharToGlyphMapper mapper;
   protected long pScalerContext;
   protected long[] longGlyphImages;
   protected int[] intGlyphImages;
   ConcurrentHashMap<Integer, Point2D.Float> glyphPointMapCache;
   protected boolean getImageWithAdvance;
   protected static final int complexTX = 124;

   PhysicalStrike(PhysicalFont var1, FontStrikeDesc var2) {
      this.physicalFont = var1;
      this.desc = var2;
   }

   protected PhysicalStrike() {
   }

   public int getNumGlyphs() {
      return this.physicalFont.getNumGlyphs();
   }

   StrikeMetrics getFontMetrics() {
      if (this.strikeMetrics == null) {
         this.strikeMetrics = this.physicalFont.getFontMetrics(this.pScalerContext);
      }

      return this.strikeMetrics;
   }

   float getCodePointAdvance(int var1) {
      return this.getGlyphAdvance(this.physicalFont.getMapper().charToGlyph(var1));
   }

   Point2D.Float getCharMetrics(char var1) {
      return this.getGlyphMetrics(this.physicalFont.getMapper().charToGlyph(var1));
   }

   int getSlot0GlyphImagePtrs(int[] var1, long[] var2, int var3) {
      return 0;
   }

   Point2D.Float getGlyphPoint(int var1, int var2) {
      Point2D.Float var3 = null;
      Integer var4 = var1 << 16 | var2;
      if (this.glyphPointMapCache == null) {
         synchronized(this) {
            if (this.glyphPointMapCache == null) {
               this.glyphPointMapCache = new ConcurrentHashMap();
            }
         }
      } else {
         var3 = (Point2D.Float)this.glyphPointMapCache.get(var4);
      }

      if (var3 == null) {
         var3 = this.physicalFont.getGlyphPoint(this.pScalerContext, var1, var2);
         this.adjustPoint(var3);
         this.glyphPointMapCache.put(var4, var3);
      }

      return var3;
   }

   protected void adjustPoint(Point2D.Float var1) {
   }

   static {
      switch(StrikeCache.nativeAddressSize) {
      case 4:
         longAddresses = false;
         break;
      case 8:
         longAddresses = true;
         break;
      default:
         throw new RuntimeException("Unexpected address size");
      }

   }
}
