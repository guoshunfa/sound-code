package sun.font;

import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;

public final class FontLineMetrics extends LineMetrics implements Cloneable {
   public int numchars;
   public final CoreMetrics cm;
   public final FontRenderContext frc;

   public FontLineMetrics(int var1, CoreMetrics var2, FontRenderContext var3) {
      this.numchars = var1;
      this.cm = var2;
      this.frc = var3;
   }

   public final int getNumChars() {
      return this.numchars;
   }

   public final float getAscent() {
      return this.cm.ascent;
   }

   public final float getDescent() {
      return this.cm.descent;
   }

   public final float getLeading() {
      return this.cm.leading;
   }

   public final float getHeight() {
      return this.cm.height;
   }

   public final int getBaselineIndex() {
      return this.cm.baselineIndex;
   }

   public final float[] getBaselineOffsets() {
      return (float[])((float[])this.cm.baselineOffsets.clone());
   }

   public final float getStrikethroughOffset() {
      return this.cm.strikethroughOffset;
   }

   public final float getStrikethroughThickness() {
      return this.cm.strikethroughThickness;
   }

   public final float getUnderlineOffset() {
      return this.cm.underlineOffset;
   }

   public final float getUnderlineThickness() {
      return this.cm.underlineThickness;
   }

   public final int hashCode() {
      return this.cm.hashCode();
   }

   public final boolean equals(Object var1) {
      try {
         return this.cm.equals(((FontLineMetrics)var1).cm);
      } catch (ClassCastException var3) {
         return false;
      }
   }

   public final Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2);
      }
   }
}
