package java.lang;

import java.util.Random;

public final class StrictMath {
   public static final double E = 2.718281828459045D;
   public static final double PI = 3.141592653589793D;

   private StrictMath() {
   }

   public static native double sin(double var0);

   public static native double cos(double var0);

   public static native double tan(double var0);

   public static native double asin(double var0);

   public static native double acos(double var0);

   public static native double atan(double var0);

   public static strictfp double toRadians(double var0) {
      return var0 / 180.0D * 3.141592653589793D;
   }

   public static strictfp double toDegrees(double var0) {
      return var0 * 180.0D / 3.141592653589793D;
   }

   public static native double exp(double var0);

   public static native double log(double var0);

   public static native double log10(double var0);

   public static native double sqrt(double var0);

   public static native double cbrt(double var0);

   public static native double IEEEremainder(double var0, double var2);

   public static double ceil(double var0) {
      return floorOrCeil(var0, -0.0D, 1.0D, 1.0D);
   }

   public static double floor(double var0) {
      return floorOrCeil(var0, -1.0D, 0.0D, -1.0D);
   }

   private static double floorOrCeil(double var0, double var2, double var4, double var6) {
      int var8 = Math.getExponent(var0);
      if (var8 < 0) {
         return var0 == 0.0D ? var0 : (var0 < 0.0D ? var2 : var4);
      } else if (var8 >= 52) {
         return var0;
      } else {
         assert var8 >= 0 && var8 <= 51;

         long var9 = Double.doubleToRawLongBits(var0);
         long var11 = 4503599627370495L >> var8;
         if ((var11 & var9) == 0L) {
            return var0;
         } else {
            double var13 = Double.longBitsToDouble(var9 & ~var11);
            if (var6 * var0 > 0.0D) {
               var13 += var6;
            }

            return var13;
         }
      }
   }

   public static double rint(double var0) {
      double var2 = 4.503599627370496E15D;
      double var4 = Math.copySign(1.0D, var0);
      var0 = Math.abs(var0);
      if (var0 < var2) {
         var0 = var2 + var0 - var2;
      }

      return var4 * var0;
   }

   public static native double atan2(double var0, double var2);

   public static native double pow(double var0, double var2);

   public static int round(float var0) {
      return Math.round(var0);
   }

   public static long round(double var0) {
      return Math.round(var0);
   }

   public static double random() {
      return StrictMath.RandomNumberGeneratorHolder.randomNumberGenerator.nextDouble();
   }

   public static int addExact(int var0, int var1) {
      return Math.addExact(var0, var1);
   }

   public static long addExact(long var0, long var2) {
      return Math.addExact(var0, var2);
   }

   public static int subtractExact(int var0, int var1) {
      return Math.subtractExact(var0, var1);
   }

   public static long subtractExact(long var0, long var2) {
      return Math.subtractExact(var0, var2);
   }

   public static int multiplyExact(int var0, int var1) {
      return Math.multiplyExact(var0, var1);
   }

   public static long multiplyExact(long var0, long var2) {
      return Math.multiplyExact(var0, var2);
   }

   public static int toIntExact(long var0) {
      return Math.toIntExact(var0);
   }

   public static int floorDiv(int var0, int var1) {
      return Math.floorDiv(var0, var1);
   }

   public static long floorDiv(long var0, long var2) {
      return Math.floorDiv(var0, var2);
   }

   public static int floorMod(int var0, int var1) {
      return Math.floorMod(var0, var1);
   }

   public static long floorMod(long var0, long var2) {
      return Math.floorMod(var0, var2);
   }

   public static int abs(int var0) {
      return Math.abs(var0);
   }

   public static long abs(long var0) {
      return Math.abs(var0);
   }

   public static float abs(float var0) {
      return Math.abs(var0);
   }

   public static double abs(double var0) {
      return Math.abs(var0);
   }

   public static int max(int var0, int var1) {
      return Math.max(var0, var1);
   }

   public static long max(long var0, long var2) {
      return Math.max(var0, var2);
   }

   public static float max(float var0, float var1) {
      return Math.max(var0, var1);
   }

   public static double max(double var0, double var2) {
      return Math.max(var0, var2);
   }

   public static int min(int var0, int var1) {
      return Math.min(var0, var1);
   }

   public static long min(long var0, long var2) {
      return Math.min(var0, var2);
   }

   public static float min(float var0, float var1) {
      return Math.min(var0, var1);
   }

   public static double min(double var0, double var2) {
      return Math.min(var0, var2);
   }

   public static double ulp(double var0) {
      return Math.ulp(var0);
   }

   public static float ulp(float var0) {
      return Math.ulp(var0);
   }

   public static double signum(double var0) {
      return Math.signum(var0);
   }

   public static float signum(float var0) {
      return Math.signum(var0);
   }

   public static native double sinh(double var0);

   public static native double cosh(double var0);

   public static native double tanh(double var0);

   public static native double hypot(double var0, double var2);

   public static native double expm1(double var0);

   public static native double log1p(double var0);

   public static double copySign(double var0, double var2) {
      return Math.copySign(var0, Double.isNaN(var2) ? 1.0D : var2);
   }

   public static float copySign(float var0, float var1) {
      return Math.copySign(var0, Float.isNaN(var1) ? 1.0F : var1);
   }

   public static int getExponent(float var0) {
      return Math.getExponent(var0);
   }

   public static int getExponent(double var0) {
      return Math.getExponent(var0);
   }

   public static double nextAfter(double var0, double var2) {
      return Math.nextAfter(var0, var2);
   }

   public static float nextAfter(float var0, double var1) {
      return Math.nextAfter(var0, var1);
   }

   public static double nextUp(double var0) {
      return Math.nextUp(var0);
   }

   public static float nextUp(float var0) {
      return Math.nextUp(var0);
   }

   public static double nextDown(double var0) {
      return Math.nextDown(var0);
   }

   public static float nextDown(float var0) {
      return Math.nextDown(var0);
   }

   public static double scalb(double var0, int var2) {
      return Math.scalb(var0, var2);
   }

   public static float scalb(float var0, int var1) {
      return Math.scalb(var0, var1);
   }

   private static final class RandomNumberGeneratorHolder {
      static final Random randomNumberGenerator = new Random();
   }
}
