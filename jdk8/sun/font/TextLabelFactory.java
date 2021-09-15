package sun.font;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.text.Bidi;

public final class TextLabelFactory {
   private final FontRenderContext frc;
   private final char[] text;
   private final Bidi bidi;
   private Bidi lineBidi;
   private final int flags;
   private int lineStart;
   private int lineLimit;

   public TextLabelFactory(FontRenderContext var1, char[] var2, Bidi var3, int var4) {
      this.frc = var1;
      this.text = (char[])var2.clone();
      this.bidi = var3;
      this.flags = var4;
      this.lineBidi = var3;
      this.lineStart = 0;
      this.lineLimit = var2.length;
   }

   public FontRenderContext getFontRenderContext() {
      return this.frc;
   }

   public Bidi getLineBidi() {
      return this.lineBidi;
   }

   public void setLineContext(int var1, int var2) {
      this.lineStart = var1;
      this.lineLimit = var2;
      if (this.bidi != null) {
         this.lineBidi = this.bidi.createLineBidi(var1, var2);
      }

   }

   public ExtendedTextLabel createExtended(Font var1, CoreMetrics var2, Decoration var3, int var4, int var5) {
      if (var4 < var5 && var4 >= this.lineStart && var5 <= this.lineLimit) {
         int var6 = this.lineBidi == null ? 0 : this.lineBidi.getLevelAt(var4 - this.lineStart);
         boolean var7 = this.lineBidi != null && !this.lineBidi.baseIsLeftToRight();
         int var8 = this.flags & -10;
         if ((var6 & 1) != 0) {
            var8 |= 1;
         }

         if (var7 & true) {
            var8 |= 8;
         }

         StandardTextSource var9 = new StandardTextSource(this.text, var4, var5 - var4, this.lineStart, this.lineLimit - this.lineStart, var6, var8, var1, this.frc, var2);
         return new ExtendedTextSourceLabel(var9, var3);
      } else {
         throw new IllegalArgumentException("bad start: " + var4 + " or limit: " + var5);
      }
   }

   public TextLabel createSimple(Font var1, CoreMetrics var2, int var3, int var4) {
      if (var3 < var4 && var3 >= this.lineStart && var4 <= this.lineLimit) {
         int var5 = this.lineBidi == null ? 0 : this.lineBidi.getLevelAt(var3 - this.lineStart);
         boolean var6 = this.lineBidi != null && !this.lineBidi.baseIsLeftToRight();
         int var7 = this.flags & -10;
         if ((var5 & 1) != 0) {
            var7 |= 1;
         }

         if (var6 & true) {
            var7 |= 8;
         }

         StandardTextSource var8 = new StandardTextSource(this.text, var3, var4 - var3, this.lineStart, this.lineLimit - this.lineStart, var5, var7, var1, this.frc, var2);
         return new TextSourceLabel(var8);
      } else {
         throw new IllegalArgumentException("bad start: " + var3 + " or limit: " + var4);
      }
   }
}
