package sun.font;

import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public final class CompositeStrike extends FontStrike {
   static final int SLOTMASK = 16777215;
   private CompositeFont compFont;
   private PhysicalStrike[] strikes;
   int numGlyphs = 0;

   CompositeStrike(CompositeFont var1, FontStrikeDesc var2) {
      this.compFont = var1;
      this.desc = var2;
      this.disposer = new FontStrikeDisposer(this.compFont, var2);
      if (var2.style != this.compFont.style) {
         this.algoStyle = true;
         if ((var2.style & 1) == 1 && (this.compFont.style & 1) == 0) {
            this.boldness = 1.33F;
         }

         if ((var2.style & 2) == 2 && (this.compFont.style & 2) == 0) {
            this.italic = 0.7F;
         }
      }

      this.strikes = new PhysicalStrike[this.compFont.numSlots];
   }

   PhysicalStrike getStrikeForGlyph(int var1) {
      return this.getStrikeForSlot(var1 >>> 24);
   }

   PhysicalStrike getStrikeForSlot(int var1) {
      if (var1 >= this.strikes.length) {
         var1 = 0;
      }

      PhysicalStrike var2 = this.strikes[var1];
      if (var2 == null) {
         var2 = (PhysicalStrike)((PhysicalStrike)this.compFont.getSlotFont(var1).getStrike(this.desc));
         this.strikes[var1] = var2;
      }

      return var2;
   }

   public int getNumGlyphs() {
      return this.compFont.getNumGlyphs();
   }

   StrikeMetrics getFontMetrics() {
      if (this.strikeMetrics == null) {
         StrikeMetrics var1 = new StrikeMetrics();

         for(int var2 = 0; var2 < this.compFont.numMetricsSlots; ++var2) {
            var1.merge(this.getStrikeForSlot(var2).getFontMetrics());
         }

         this.strikeMetrics = var1;
      }

      return this.strikeMetrics;
   }

   void getGlyphImagePtrs(int[] var1, long[] var2, int var3) {
      PhysicalStrike var4 = this.getStrikeForSlot(0);
      int var5 = var4.getSlot0GlyphImagePtrs(var1, var2, var3);
      if (var5 != var3) {
         for(int var6 = var5; var6 < var3; ++var6) {
            var4 = this.getStrikeForGlyph(var1[var6]);
            var2[var6] = var4.getGlyphImagePtr(var1[var6] & 16777215);
         }

      }
   }

   long getGlyphImagePtr(int var1) {
      PhysicalStrike var2 = this.getStrikeForGlyph(var1);
      return var2.getGlyphImagePtr(var1 & 16777215);
   }

   void getGlyphImageBounds(int var1, Point2D.Float var2, Rectangle var3) {
      PhysicalStrike var4 = this.getStrikeForGlyph(var1);
      var4.getGlyphImageBounds(var1 & 16777215, var2, var3);
   }

   Point2D.Float getGlyphMetrics(int var1) {
      PhysicalStrike var2 = this.getStrikeForGlyph(var1);
      return var2.getGlyphMetrics(var1 & 16777215);
   }

   Point2D.Float getCharMetrics(char var1) {
      return this.getGlyphMetrics(this.compFont.getMapper().charToGlyph(var1));
   }

   float getGlyphAdvance(int var1) {
      PhysicalStrike var2 = this.getStrikeForGlyph(var1);
      return var2.getGlyphAdvance(var1 & 16777215);
   }

   float getCodePointAdvance(int var1) {
      return this.getGlyphAdvance(this.compFont.getMapper().charToGlyph(var1));
   }

   Rectangle2D.Float getGlyphOutlineBounds(int var1) {
      PhysicalStrike var2 = this.getStrikeForGlyph(var1);
      return var2.getGlyphOutlineBounds(var1 & 16777215);
   }

   GeneralPath getGlyphOutline(int var1, float var2, float var3) {
      PhysicalStrike var4 = this.getStrikeForGlyph(var1);
      GeneralPath var5 = var4.getGlyphOutline(var1 & 16777215, var2, var3);
      return var5 == null ? new GeneralPath() : var5;
   }

   GeneralPath getGlyphVectorOutline(int[] var1, float var2, float var3) {
      GeneralPath var4 = null;
      int var6 = 0;

      while(var6 < var1.length) {
         int var8 = var6;

         int var9;
         for(var9 = var1[var6] >>> 24; var6 < var1.length && var1[var6 + 1] >>> 24 == var9; ++var6) {
         }

         int var10 = var6 - var8 + 1;
         int[] var7 = new int[var10];

         for(int var11 = 0; var11 < var10; ++var11) {
            var7[var11] = var1[var11] & 16777215;
         }

         GeneralPath var5 = this.getStrikeForSlot(var9).getGlyphVectorOutline(var7, var2, var3);
         if (var4 == null) {
            var4 = var5;
         } else if (var5 != null) {
            var4.append(var5, false);
         }
      }

      if (var4 == null) {
         return new GeneralPath();
      } else {
         return var4;
      }
   }
}
