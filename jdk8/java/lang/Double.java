package java.lang;

import sun.misc.FloatingDecimal;

public final class Double extends Number implements Comparable<Double> {
   public static final double POSITIVE_INFINITY = POSITIVE_INFINITY;
   public static final double NEGATIVE_INFINITY = NEGATIVE_INFINITY;
   public static final double NaN = NaN;
   public static final double MAX_VALUE = MAX_VALUE;
   public static final double MIN_NORMAL = 2.2250738585072014E-308D;
   public static final double MIN_VALUE = MIN_VALUE;
   public static final int MAX_EXPONENT = 1023;
   public static final int MIN_EXPONENT = -1022;
   public static final int SIZE = 64;
   public static final int BYTES = 8;
   public static final Class<Double> TYPE = Class.getPrimitiveClass("double");
   private final double value;
   private static final long serialVersionUID = -9172774392245257468L;

   public static String toString(double var0) {
      return FloatingDecimal.toJavaFormatString(var0);
   }

   public static String toHexString(double var0) {
      if (!isFinite(var0)) {
         return toString(var0);
      } else {
         StringBuilder var2 = new StringBuilder(24);
         if (Math.copySign(1.0D, var0) == -1.0D) {
            var2.append("-");
         }

         var2.append("0x");
         var0 = Math.abs(var0);
         if (var0 == 0.0D) {
            var2.append("0.0p0");
         } else {
            boolean var3 = var0 < 2.2250738585072014E-308D;
            long var4 = doubleToLongBits(var0) & 4503599627370495L | 1152921504606846976L;
            var2.append(var3 ? "0." : "1.");
            String var6 = Long.toHexString(var4).substring(3, 16);
            var2.append(var6.equals("0000000000000") ? "0" : var6.replaceFirst("0{1,12}$", ""));
            var2.append('p');
            var2.append(var3 ? -1022 : Math.getExponent(var0));
         }

         return var2.toString();
      }
   }

   public static Double valueOf(String var0) throws NumberFormatException {
      return new Double(parseDouble(var0));
   }

   public static Double valueOf(double var0) {
      return new Double(var0);
   }

   public static double parseDouble(String var0) throws NumberFormatException {
      return FloatingDecimal.parseDouble(var0);
   }

   public static boolean isNaN(double var0) {
      return var0 != var0;
   }

   public static boolean isInfinite(double var0) {
      return var0 == POSITIVE_INFINITY || var0 == NEGATIVE_INFINITY;
   }

   public static boolean isFinite(double var0) {
      return Math.abs(var0) <= MAX_VALUE;
   }

   public Double(double var1) {
      this.value = var1;
   }

   public Double(String var1) throws NumberFormatException {
      this.value = parseDouble(var1);
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
      return (float)this.value;
   }

   public double doubleValue() {
      return this.value;
   }

   public int hashCode() {
      return hashCode(this.value);
   }

   public static int hashCode(double var0) {
      long var2 = doubleToLongBits(var0);
      return (int)(var2 ^ var2 >>> 32);
   }

   public boolean equals(Object var1) {
      return var1 instanceof Double && doubleToLongBits(((Double)var1).value) == doubleToLongBits(this.value);
   }

   public static long doubleToLongBits(double var0) {
      long var2 = doubleToRawLongBits(var0);
      if ((var2 & 9218868437227405312L) == 9218868437227405312L && (var2 & 4503599627370495L) != 0L) {
         var2 = 9221120237041090560L;
      }

      return var2;
   }

   public static native long doubleToRawLongBits(double var0);

   public static native double longBitsToDouble(long var0);

   public int compareTo(Double var1) {
      return compare(this.value, var1.value);
   }

   public static int compare(double var0, double var2) {
      if (var0 < var2) {
         return -1;
      } else if (var0 > var2) {
         return 1;
      } else {
         long var4 = doubleToLongBits(var0);
         long var6 = doubleToLongBits(var2);
         return var4 == var6 ? 0 : (var4 < var6 ? -1 : 1);
      }
   }

   public static double sum(double var0, double var2) {
      return var0 + var2;
   }

   public static double max(double var0, double var2) {
      return Math.max(var0, var2);
   }

   public static double min(double var0, double var2) {
      return Math.min(var0, var2);
   }
}
