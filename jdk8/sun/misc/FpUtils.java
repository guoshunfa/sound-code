package sun.misc;

public class FpUtils {
   private FpUtils() {
   }

   /** @deprecated */
   @Deprecated
   public static int getExponent(double var0) {
      return Math.getExponent(var0);
   }

   /** @deprecated */
   @Deprecated
   public static int getExponent(float var0) {
      return Math.getExponent(var0);
   }

   /** @deprecated */
   @Deprecated
   public static double rawCopySign(double var0, double var2) {
      return Math.copySign(var0, var2);
   }

   /** @deprecated */
   @Deprecated
   public static float rawCopySign(float var0, float var1) {
      return Math.copySign(var0, var1);
   }

   /** @deprecated */
   @Deprecated
   public static boolean isFinite(double var0) {
      return Double.isFinite(var0);
   }

   /** @deprecated */
   @Deprecated
   public static boolean isFinite(float var0) {
      return Float.isFinite(var0);
   }

   public static boolean isInfinite(double var0) {
      return Double.isInfinite(var0);
   }

   public static boolean isInfinite(float var0) {
      return Float.isInfinite(var0);
   }

   public static boolean isNaN(double var0) {
      return Double.isNaN(var0);
   }

   public static boolean isNaN(float var0) {
      return Float.isNaN(var0);
   }

   public static boolean isUnordered(double var0, double var2) {
      return isNaN(var0) || isNaN(var2);
   }

   public static boolean isUnordered(float var0, float var1) {
      return isNaN(var0) || isNaN(var1);
   }

   public static int ilogb(double var0) {
      int var2 = getExponent(var0);
      switch(var2) {
      case -1023:
         if (var0 == 0.0D) {
            return -268435456;
         } else {
            long var3 = Double.doubleToRawLongBits(var0);
            var3 &= 4503599627370495L;

            assert var3 != 0L;

            while(var3 < 4503599627370496L) {
               var3 *= 2L;
               --var2;
            }

            ++var2;

            assert var2 >= -1074 && var2 < -1022;

            return var2;
         }
      case 1024:
         if (isNaN(var0)) {
            return 1073741824;
         }

         return 268435456;
      default:
         assert var2 >= -1022 && var2 <= 1023;

         return var2;
      }
   }

   public static int ilogb(float var0) {
      int var1 = getExponent(var0);
      switch(var1) {
      case -127:
         if (var0 == 0.0F) {
            return -268435456;
         } else {
            int var2 = Float.floatToRawIntBits(var0);
            var2 &= 8388607;

            assert var2 != 0;

            while(var2 < 8388608) {
               var2 *= 2;
               --var1;
            }

            ++var1;

            assert var1 >= -149 && var1 < -126;

            return var1;
         }
      case 128:
         if (isNaN(var0)) {
            return 1073741824;
         }

         return 268435456;
      default:
         assert var1 >= -126 && var1 <= 127;

         return var1;
      }
   }

   /** @deprecated */
   @Deprecated
   public static double scalb(double var0, int var2) {
      return Math.scalb(var0, var2);
   }

   /** @deprecated */
   @Deprecated
   public static float scalb(float var0, int var1) {
      return Math.scalb(var0, var1);
   }

   /** @deprecated */
   @Deprecated
   public static double nextAfter(double var0, double var2) {
      return Math.nextAfter(var0, var2);
   }

   /** @deprecated */
   @Deprecated
   public static float nextAfter(float var0, double var1) {
      return Math.nextAfter(var0, var1);
   }

   /** @deprecated */
   @Deprecated
   public static double nextUp(double var0) {
      return Math.nextUp(var0);
   }

   /** @deprecated */
   @Deprecated
   public static float nextUp(float var0) {
      return Math.nextUp(var0);
   }

   /** @deprecated */
   @Deprecated
   public static double nextDown(double var0) {
      return Math.nextDown(var0);
   }

   /** @deprecated */
   @Deprecated
   public static double nextDown(float var0) {
      return (double)Math.nextDown(var0);
   }

   /** @deprecated */
   @Deprecated
   public static double copySign(double var0, double var2) {
      return StrictMath.copySign(var0, var2);
   }

   /** @deprecated */
   @Deprecated
   public static float copySign(float var0, float var1) {
      return StrictMath.copySign(var0, var1);
   }

   /** @deprecated */
   @Deprecated
   public static double ulp(double var0) {
      return Math.ulp(var0);
   }

   /** @deprecated */
   @Deprecated
   public static float ulp(float var0) {
      return Math.ulp(var0);
   }

   /** @deprecated */
   @Deprecated
   public static double signum(double var0) {
      return Math.signum(var0);
   }

   /** @deprecated */
   @Deprecated
   public static float signum(float var0) {
      return Math.signum(var0);
   }
}
