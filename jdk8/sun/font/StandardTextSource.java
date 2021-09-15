package sun.font;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;

final class StandardTextSource extends TextSource {
   private final char[] chars;
   private final int start;
   private final int len;
   private final int cstart;
   private final int clen;
   private final int level;
   private final int flags;
   private final Font font;
   private final FontRenderContext frc;
   private final CoreMetrics cm;

   StandardTextSource(char[] var1, int var2, int var3, int var4, int var5, int var6, int var7, Font var8, FontRenderContext var9, CoreMetrics var10) {
      if (var1 == null) {
         throw new IllegalArgumentException("bad chars: null");
      } else if (var4 < 0) {
         throw new IllegalArgumentException("bad cstart: " + var4);
      } else if (var2 < var4) {
         throw new IllegalArgumentException("bad start: " + var2 + " for cstart: " + var4);
      } else if (var5 < 0) {
         throw new IllegalArgumentException("bad clen: " + var5);
      } else if (var4 + var5 > var1.length) {
         throw new IllegalArgumentException("bad clen: " + var5 + " cstart: " + var4 + " for array len: " + var1.length);
      } else if (var3 < 0) {
         throw new IllegalArgumentException("bad len: " + var3);
      } else if (var2 + var3 > var4 + var5) {
         throw new IllegalArgumentException("bad len: " + var3 + " start: " + var2 + " for cstart: " + var4 + " clen: " + var5);
      } else if (var8 == null) {
         throw new IllegalArgumentException("bad font: null");
      } else if (var9 == null) {
         throw new IllegalArgumentException("bad frc: null");
      } else {
         this.chars = var1;
         this.start = var2;
         this.len = var3;
         this.cstart = var4;
         this.clen = var5;
         this.level = var6;
         this.flags = var7;
         this.font = var8;
         this.frc = var9;
         if (var10 != null) {
            this.cm = var10;
         } else {
            LineMetrics var11 = var8.getLineMetrics(var1, var4, var5, var9);
            this.cm = ((FontLineMetrics)var11).cm;
         }

      }
   }

   public char[] getChars() {
      return this.chars;
   }

   public int getStart() {
      return this.start;
   }

   public int getLength() {
      return this.len;
   }

   public int getContextStart() {
      return this.cstart;
   }

   public int getContextLength() {
      return this.clen;
   }

   public int getLayoutFlags() {
      return this.flags;
   }

   public int getBidiLevel() {
      return this.level;
   }

   public Font getFont() {
      return this.font;
   }

   public FontRenderContext getFRC() {
      return this.frc;
   }

   public CoreMetrics getCoreMetrics() {
      return this.cm;
   }

   public TextSource getSubSource(int var1, int var2, int var3) {
      if (var1 >= 0 && var2 >= 0 && var1 + var2 <= this.len) {
         int var4 = this.level;
         if (var3 != 2) {
            boolean var5 = (this.flags & 8) == 0;
            if ((var3 != 0 || !var5) && (var3 != 1 || var5)) {
               throw new IllegalArgumentException("direction flag is invalid");
            }

            var4 = var5 ? 0 : 1;
         }

         return new StandardTextSource(this.chars, this.start + var1, var2, this.cstart, this.clen, var4, this.flags, this.font, this.frc, this.cm);
      } else {
         throw new IllegalArgumentException("bad start (" + var1 + ") or length (" + var2 + ")");
      }
   }

   public String toString() {
      return this.toString(true);
   }

   public String toString(boolean var1) {
      StringBuffer var2 = new StringBuffer(super.toString());
      var2.append("[start:");
      var2.append(this.start);
      var2.append(", len:");
      var2.append(this.len);
      var2.append(", cstart:");
      var2.append(this.cstart);
      var2.append(", clen:");
      var2.append(this.clen);
      var2.append(", chars:\"");
      int var3;
      int var4;
      if (var1) {
         var3 = this.cstart;
         var4 = this.cstart + this.clen;
      } else {
         var3 = this.start;
         var4 = this.start + this.len;
      }

      for(int var5 = var3; var5 < var4; ++var5) {
         if (var5 > var3) {
            var2.append(" ");
         }

         var2.append(Integer.toHexString(this.chars[var5]));
      }

      var2.append("\"");
      var2.append(", level:");
      var2.append(this.level);
      var2.append(", flags:");
      var2.append(this.flags);
      var2.append(", font:");
      var2.append((Object)this.font);
      var2.append(", frc:");
      var2.append((Object)this.frc);
      var2.append(", cm:");
      var2.append((Object)this.cm);
      var2.append("]");
      return var2.toString();
   }
}
