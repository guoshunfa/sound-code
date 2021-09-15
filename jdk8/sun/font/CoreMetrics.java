package sun.font;

import java.awt.font.LineMetrics;

public final class CoreMetrics {
   public final float ascent;
   public final float descent;
   public final float leading;
   public final float height;
   public final int baselineIndex;
   public final float[] baselineOffsets;
   public final float strikethroughOffset;
   public final float strikethroughThickness;
   public final float underlineOffset;
   public final float underlineThickness;
   public final float ssOffset;
   public final float italicAngle;

   public CoreMetrics(float var1, float var2, float var3, float var4, int var5, float[] var6, float var7, float var8, float var9, float var10, float var11, float var12) {
      this.ascent = var1;
      this.descent = var2;
      this.leading = var3;
      this.height = var4;
      this.baselineIndex = var5;
      this.baselineOffsets = var6;
      this.strikethroughOffset = var7;
      this.strikethroughThickness = var8;
      this.underlineOffset = var9;
      this.underlineThickness = var10;
      this.ssOffset = var11;
      this.italicAngle = var12;
   }

   public static CoreMetrics get(LineMetrics var0) {
      return ((FontLineMetrics)var0).cm;
   }

   public final int hashCode() {
      return Float.floatToIntBits(this.ascent + this.ssOffset);
   }

   public final boolean equals(Object var1) {
      try {
         return this.equals((CoreMetrics)var1);
      } catch (ClassCastException var3) {
         return false;
      }
   }

   public final boolean equals(CoreMetrics var1) {
      if (var1 != null) {
         if (this == var1) {
            return true;
         } else {
            return this.ascent == var1.ascent && this.descent == var1.descent && this.leading == var1.leading && this.baselineIndex == var1.baselineIndex && this.baselineOffsets[0] == var1.baselineOffsets[0] && this.baselineOffsets[1] == var1.baselineOffsets[1] && this.baselineOffsets[2] == var1.baselineOffsets[2] && this.strikethroughOffset == var1.strikethroughOffset && this.strikethroughThickness == var1.strikethroughThickness && this.underlineOffset == var1.underlineOffset && this.underlineThickness == var1.underlineThickness && this.ssOffset == var1.ssOffset && this.italicAngle == var1.italicAngle;
         }
      } else {
         return false;
      }
   }

   public final float effectiveBaselineOffset(float[] var1) {
      switch(this.baselineIndex) {
      case -2:
         return var1[3] - this.descent;
      case -1:
         return var1[4] + this.ascent;
      default:
         return var1[this.baselineIndex];
      }
   }
}
