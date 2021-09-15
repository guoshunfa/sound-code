package java.lang;

import sun.misc.FloatingDecimal;

public final class Float extends Number implements Comparable<Float> {
   public static final float POSITIVE_INFINITY = POSITIVE_INFINITY;
   public static final float NEGATIVE_INFINITY = NEGATIVE_INFINITY;
   public static final float NaN = NaN;
   public static final float MAX_VALUE = MAX_VALUE;
   public static final float MIN_NORMAL = 1.17549435E-38F;
   public static final float MIN_VALUE = MIN_VALUE;
   public static final int MAX_EXPONENT = 127;
   public static final int MIN_EXPONENT = -126;
   public static final int SIZE = 32;
   public static final int BYTES = 4;
   public static final Class<Float> TYPE = Class.getPrimitiveClass("float");
   private final float value;
   private static final long serialVersionUID = -2671257302660747028L;

   public static String toString(float var0) {
      return FloatingDecimal.toJavaFormatString(var0);
   }

   public static String toHexString(float var0) {
      if (Math.abs(var0) < 1.17549435E-38F && var0 != 0.0F) {
         String var1 = Double.toHexString(Math.scalb((double)var0, -896));
         return var1.replaceFirst("p-1022$", "p-126");
      } else {
         return Double.toHexString((double)var0);
      }
   }

   public static Float valueOf(String var0) throws NumberFormatException {
      return new Float(parseFloat(var0));
   }

   public static Float valueOf(float var0) {
      return new Float(var0);
   }

   public static float parseFloat(String var0) throws NumberFormatException {
      return FloatingDecimal.parseFloat(var0);
   }

   public static boolean isNaN(float var0) {
      return var0 != var0;
   }

   public static boolean isInfinite(float var0) {
      return var0 == POSITIVE_INFINITY || var0 == NEGATIVE_INFINITY;
   }

   public static boolean isFinite(float var0) {
      return Math.abs(var0) <= MAX_VALUE;
   }

   public Float(float var1) {
      this.value = var1;
   }

   public Float(double var1) {
      this.value = (float)var1;
   }

   public Float(String var1) throws NumberFormatException {
      this.value = parseFloat(var1);
   }

   public boolean isNaN() {
      return isNaN(this.value);
   }

   public boolean isInfinite() {
      return isInfinite(this.value);
   }

   public String toString() {
      return toString(this.value);
   }

   public byte byteValue() {
      return (byte)((int)this.value);
   }

   public short shortValue() {
      return (short)((int)this.value);
   }

   public int intValue() {
      return (int)this.value;
   }

   public long longValue() {
      return (long)this.value;
   }

   public float floatValue() {
      return this.value;
   }

   public double doubleValue() {
      return (double)this.value;
   }

   public int hashCode() {
      return hashCode(this.value);
   }

   public static int hashCode(float var0) {
      return floatToIntBits(var0);
   }

   public boolean equals(Object var1) {
      return var1 instanceof Float && floatToIntBits(((Float)var1).value) == floatToIntBits(this.value);
   }

   public static int floatToIntBits(float var0) {
      int var1 = floatToRawIntBits(var0);
      if ((var1 & 2139095040) == 2139095040 && (var1 & 8388607) != 0) {
         var1 = 2143289344;
      }

      return var1;
   }

   public static native int floatToRawIntBits(float var0);

   public static native float intBitsToFloat(int var0);

   public int compareTo(Float var1) {
      return compare(this.value, var1.value);
   }

   public static int compare(float var0, float var1) {
      if (var0 < var1) {
         return -1;
      } else if (var0 > var1) {
         return 1;
      } else {
         int var2 = floatToIntBits(var0);
         int var3 = floatToIntBits(var1);
         return var2 == var3 ? 0 : (var2 < var3 ? -1 : 1);
      }
   }

   public static float sum(float var0, float var1) {
      return var0 + var1;
   }

   public static float max(float var0, float var1) {
      return Math.max(var0, var1);
   }

   public static float min(float var0, float var1) {
      return Math.min(var0, var1);
   }
}
