package sun.font;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import sun.awt.SunHints;

public class FontStrikeDesc {
   static final int AA_ON = 16;
   static final int AA_LCD_H = 32;
   static final int AA_LCD_V = 64;
   static final int FRAC_METRICS_ON = 256;
   static final int FRAC_METRICS_SP = 512;
   AffineTransform devTx;
   AffineTransform glyphTx;
   int style;
   int aaHint;
   int fmHint;
   private int hashCode;
   private int valuemask;

   public int hashCode() {
      if (this.hashCode == 0) {
         this.hashCode = this.glyphTx.hashCode() + this.devTx.hashCode() + this.valuemask;
      }

      return this.hashCode;
   }

   public boolean equals(Object var1) {
      try {
         FontStrikeDesc var2 = (FontStrikeDesc)var1;
         return var2.valuemask == this.valuemask && var2.glyphTx.equals(this.glyphTx) && var2.devTx.equals(this.devTx);
      } catch (Exception var3) {
         return false;
      }
   }

   FontStrikeDesc() {
   }

   public static int getAAHintIntVal(Object var0, Font2D var1, int var2) {
      if (var0 != SunHints.VALUE_TEXT_ANTIALIAS_OFF && var0 != SunHints.VALUE_TEXT_ANTIALIAS_DEFAULT) {
         if (var0 == SunHints.VALUE_TEXT_ANTIALIAS_ON) {
            return 2;
         } else if (var0 == SunHints.VALUE_TEXT_ANTIALIAS_GASP) {
            return var1.useAAForPtSize(var2) ? 2 : 1;
         } else if (var0 != SunHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB && var0 != SunHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR) {
            return var0 != SunHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB && var0 != SunHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR ? 1 : 6;
         } else {
            return 4;
         }
      } else {
         return 1;
      }
   }

   public static int getAAHintIntVal(Font2D var0, Font var1, FontRenderContext var2) {
      Object var3 = var2.getAntiAliasingHint();
      if (var3 != SunHints.VALUE_TEXT_ANTIALIAS_OFF && var3 != SunHints.VALUE_TEXT_ANTIALIAS_DEFAULT) {
         if (var3 == SunHints.VALUE_TEXT_ANTIALIAS_ON) {
            return 2;
         } else if (var3 != SunHints.VALUE_TEXT_ANTIALIAS_GASP) {
            if (var3 != SunHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB && var3 != SunHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR) {
               return var3 != SunHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB && var3 != SunHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR ? 1 : 6;
            } else {
               return 4;
            }
         } else {
            AffineTransform var5 = var2.getTransform();
            int var4;
            if (var5.isIdentity() && !var1.isTransformed()) {
               var4 = var1.getSize();
            } else {
               float var6 = var1.getSize2D();
               if (var5.isIdentity()) {
                  var5 = var1.getTransform();
                  var5.scale((double)var6, (double)var6);
               } else {
                  var5.scale((double)var6, (double)var6);
                  if (var1.isTransformed()) {
                     var5.concatenate(var1.getTransform());
                  }
               }

               double var7 = var5.getShearX();
               double var9 = var5.getScaleY();
               if (var7 != 0.0D) {
                  var9 = Math.sqrt(var7 * var7 + var9 * var9);
               }

               var4 = (int)(Math.abs(var9) + 0.5D);
            }

            return var0.useAAForPtSize(var4) ? 2 : 1;
         }
      } else {
         return 1;
      }
   }

   public static int getFMHintIntVal(Object var0) {
      return var0 != SunHints.VALUE_FRACTIONALMETRICS_OFF && var0 != SunHints.VALUE_FRACTIONALMETRICS_DEFAULT ? 2 : 1;
   }

   public FontStrikeDesc(AffineTransform var1, AffineTransform var2, int var3, int var4, int var5) {
      this.devTx = var1;
      this.glyphTx = var2;
      this.style = var3;
      this.aaHint = var4;
      this.fmHint = var5;
      this.valuemask = var3;
      switch(var4) {
      case 1:
      case 3:
      default:
         break;
      case 2:
         this.valuemask |= 16;
         break;
      case 4:
      case 5:
         this.valuemask |= 32;
         break;
      case 6:
      case 7:
         this.valuemask |= 64;
      }

      if (var5 == 2) {
         this.valuemask |= 256;
      }

   }

   FontStrikeDesc(FontStrikeDesc var1) {
      this.devTx = var1.devTx;
      this.glyphTx = (AffineTransform)var1.glyphTx.clone();
      this.style = var1.style;
      this.aaHint = var1.aaHint;
      this.fmHint = var1.fmHint;
      this.hashCode = var1.hashCode;
      this.valuemask = var1.valuemask;
   }

   public String toString() {
      return "FontStrikeDesc: Style=" + this.style + " AA=" + this.aaHint + " FM=" + this.fmHint + " devTx=" + this.devTx + " devTx.FontTx.ptSize=" + this.glyphTx;
   }
}
