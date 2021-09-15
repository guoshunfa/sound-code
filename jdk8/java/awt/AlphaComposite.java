package java.awt;

import java.awt.image.ColorModel;
import sun.java2d.SunCompositeContext;

public final class AlphaComposite implements Composite {
   public static final int CLEAR = 1;
   public static final int SRC = 2;
   public static final int DST = 9;
   public static final int SRC_OVER = 3;
   public static final int DST_OVER = 4;
   public static final int SRC_IN = 5;
   public static final int DST_IN = 6;
   public static final int SRC_OUT = 7;
   public static final int DST_OUT = 8;
   public static final int SRC_ATOP = 10;
   public static final int DST_ATOP = 11;
   public static final int XOR = 12;
   public static final AlphaComposite Clear = new AlphaComposite(1);
   public static final AlphaComposite Src = new AlphaComposite(2);
   public static final AlphaComposite Dst = new AlphaComposite(9);
   public static final AlphaComposite SrcOver = new AlphaComposite(3);
   public static final AlphaComposite DstOver = new AlphaComposite(4);
   public static final AlphaComposite SrcIn = new AlphaComposite(5);
   public static final AlphaComposite DstIn = new AlphaComposite(6);
   public static final AlphaComposite SrcOut = new AlphaComposite(7);
   public static final AlphaComposite DstOut = new AlphaComposite(8);
   public static final AlphaComposite SrcAtop = new AlphaComposite(10);
   public static final AlphaComposite DstAtop = new AlphaComposite(11);
   public static final AlphaComposite Xor = new AlphaComposite(12);
   private static final int MIN_RULE = 1;
   private static final int MAX_RULE = 12;
   float extraAlpha;
   int rule;

   private AlphaComposite(int var1) {
      this(var1, 1.0F);
   }

   private AlphaComposite(int var1, float var2) {
      if (var1 >= 1 && var1 <= 12) {
         if (var2 >= 0.0F && var2 <= 1.0F) {
            this.rule = var1;
            this.extraAlpha = var2;
         } else {
            throw new IllegalArgumentException("alpha value out of range");
         }
      } else {
         throw new IllegalArgumentException("unknown composite rule");
      }
   }

   public static AlphaComposite getInstance(int var0) {
      switch(var0) {
      case 1:
         return Clear;
      case 2:
         return Src;
      case 3:
         return SrcOver;
      case 4:
         return DstOver;
      case 5:
         return SrcIn;
      case 6:
         return DstIn;
      case 7:
         return SrcOut;
      case 8:
         return DstOut;
      case 9:
         return Dst;
      case 10:
         return SrcAtop;
      case 11:
         return DstAtop;
      case 12:
         return Xor;
      default:
         throw new IllegalArgumentException("unknown composite rule");
      }
   }

   public static AlphaComposite getInstance(int var0, float var1) {
      return var1 == 1.0F ? getInstance(var0) : new AlphaComposite(var0, var1);
   }

   public CompositeContext createContext(ColorModel var1, ColorModel var2, RenderingHints var3) {
      return new SunCompositeContext(this, var1, var2);
   }

   public float getAlpha() {
      return this.extraAlpha;
   }

   public int getRule() {
      return this.rule;
   }

   public AlphaComposite derive(int var1) {
      return this.rule == var1 ? this : getInstance(var1, this.extraAlpha);
   }

   public AlphaComposite derive(float var1) {
      return this.extraAlpha == var1 ? this : getInstance(this.rule, var1);
   }

   public int hashCode() {
      return Float.floatToIntBits(this.extraAlpha) * 31 + this.rule;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof AlphaComposite)) {
         return false;
      } else {
         AlphaComposite var2 = (AlphaComposite)var1;
         if (this.rule != var2.rule) {
            return false;
         } else {
            return this.extraAlpha == var2.extraAlpha;
         }
      }
   }
}
