package java.lang;

import java.math.BigInteger;

public final class Long extends Number implements Comparable<Long> {
   public static final long MIN_VALUE = MIN_VALUE;
   public static final long MAX_VALUE = MAX_VALUE;
   public static final Class<Long> TYPE = Class.getPrimitiveClass("long");
   private final long value;
   public static final int SIZE = 64;
   public static final int BYTES = 8;
   private static final long serialVersionUID = 4290774380558885855L;

   public static String toString(long var0, int var2) {
      if (var2 < 2 || var2 > 36) {
         var2 = 10;
      }

      if (var2 == 10) {
         return toString(var0);
      } else {
         char[] var3 = new char[65];
         int var4 = 64;
         boolean var5 = var0 < 0L;
         if (!var5) {
            var0 = -var0;
         }

         while(var0 <= (long)(-var2)) {
            var3[var4--] = Integer.digits[(int)(-(var0 % (long)var2))];
            var0 /= (long)var2;
         }

         var3[var4] = Integer.digits[(int)(-var0)];
         if (var5) {
            --var4;
            var3[var4] = '-';
         }

         return new String(var3, var4, 65 - var4);
      }
   }

   public static String toUnsignedString(long var0, int var2) {
      if (var0 >= 0L) {
         return toString(var0, var2);
      } else {
         switch(var2) {
         case 2:
            return toBinaryString(var0);
         case 4:
            return toUnsignedString0(var0, 2);
         case 8:
            return toOctalString(var0);
         case 10:
            long var3 = (var0 >>> 1) / 5L;
            long var5 = var0 - var3 * 10L;
            return toString(var3) + var5;
         case 16:
            return toHexString(var0);
         case 32:
            return toUnsignedString0(var0, 5);
         default:
            return toUnsignedBigInteger(var0).toString(var2);
         }
      }
   }

   private static BigInteger toUnsignedBigInteger(long var0) {
      if (var0 >= 0L) {
         return BigInteger.valueOf(var0);
      } else {
         int var2 = (int)(var0 >>> 32);
         int var3 = (int)var0;
         return BigInteger.valueOf(Integer.toUnsignedLong(var2)).shiftLeft(32).add(BigInteger.valueOf(Integer.toUnsignedLong(var3)));
      }
   }

   public static String toHexString(long var0) {
      return toUnsignedString0(var0, 4);
   }

   public static String toOctalString(long var0) {
      return toUnsignedString0(var0, 3);
   }

   public static String toBinaryString(long var0) {
      return toUnsignedString0(var0, 1);
   }

   static String toUnsignedString0(long var0, int var2) {
      int var3 = 64 - numberOfLeadingZeros(var0);
      int var4 = Math.max((var3 + (var2 - 1)) / var2, 1);
      char[] var5 = new char[var4];
      formatUnsignedLong(var0, var2, var5, 0, var4);
      return new String(var5, true);
   }

   static int formatUnsignedLong(long var0, int var2, char[] var3, int var4, int var5) {
      int var6 = var5;
      int var7 = 1 << var2;
      int var8 = var7 - 1;

      do {
         --var6;
         var3[var4 + var6] = Integer.digits[(int)var0 & var8];
         var0 >>>= var2;
      } while(var0 != 0L && var6 > 0);

      return var6;
   }

   public static String toString(long var0) {
      if (var0 == MIN_VALUE) {
         return "-9223372036854775808";
      } else {
         int var2 = var0 < 0L ? stringSize(-var0) + 1 : stringSize(var0);
         char[] var3 = new char[var2];
         getChars(var0, var2, var3);
         return new String(var3, true);
      }
   }

   public static String toUnsignedString(long var0) {
      return toUnsignedString(var0, 10);
   }

   static void getChars(long var0, int var2, char[] var3) {
      int var7 = var2;
      byte var8 = 0;
      if (var0 < 0L) {
         var8 = 45;
         var0 = -var0;
      }

      int var6;
      while(var0 > 2147483647L) {
         long var4 = var0 / 100L;
         var6 = (int)(var0 - ((var4 << 6) + (var4 << 5) + (var4 << 2)));
         var0 = var4;
         --var7;
         var3[var7] = Integer.DigitOnes[var6];
         --var7;
         var3[var7] = Integer.DigitTens[var6];
      }

      int var9;
      int var10;
      for(var10 = (int)var0; var10 >= 65536; var3[var7] = Integer.DigitTens[var6]) {
         var9 = var10 / 100;
         var6 = var10 - ((var9 << 6) + (var9 << 5) + (var9 << 2));
         var10 = var9;
         --var7;
         var3[var7] = Integer.DigitOnes[var6];
         --var7;
      }

      do {
         var9 = var10 * '쳍' >>> 19;
         var6 = var10 - ((var9 << 3) + (var9 << 1));
         --var7;
         var3[var7] = Integer.digits[var6];
         var10 = var9;
      } while(var9 != 0);

      if (var8 != 0) {
         --var7;
         var3[var7] = (char)var8;
      }

   }

   static int stringSize(long var0) {
      long var2 = 10L;

      for(int var4 = 1; var4 < 19; ++var4) {
         if (var0 < var2) {
            return var4;
         }

         var2 = 10L * var2;
      }

      return 19;
   }

   public static long parseLong(String var0, int var1) throws NumberFormatException {
      if (var0 == null) {
         throw new NumberFormatException("null");
      } else if (var1 < 2) {
         throw new NumberFormatException("radix " + var1 + " less than Character.MIN_RADIX");
      } else if (var1 > 36) {
         throw new NumberFormatException("radix " + var1 + " greater than Character.MAX_RADIX");
      } else {
         long var2 = 0L;
         boolean var4 = false;
         int var5 = 0;
         int var6 = var0.length();
         long var7 = -9223372036854775807L;
         if (var6 > 0) {
            char var12 = var0.charAt(0);
            if (var12 < '0') {
               if (var12 == '-') {
                  var4 = true;
                  var7 = MIN_VALUE;
               } else if (var12 != '+') {
                  throw NumberFormatException.forInputString(var0);
               }

               if (var6 == 1) {
                  throw NumberFormatException.forInputString(var0);
               }

               ++var5;
            }

            int var11;
            for(long var9 = var7 / (long)var1; var5 < var6; var2 -= (long)var11) {
               var11 = Character.digit(var0.charAt(var5++), var1);
               if (var11 < 0) {
                  throw NumberFormatException.forInputString(var0);
               }

               if (var2 < var9) {
                  throw NumberFormatException.forInputString(var0);
               }

               var2 *= (long)var1;
               if (var2 < var7 + (long)var11) {
                  throw NumberFormatException.forInputString(var0);
               }
            }

            return var4 ? var2 : -var2;
         } else {
            throw NumberFormatException.forInputString(var0);
         }
      }
   }

   public static long parseLong(String var0) throws NumberFormatException {
      return parseLong(var0, 10);
   }

   public static long parseUnsignedLong(String var0, int var1) throws NumberFormatException {
      if (var0 == null) {
         throw new NumberFormatException("null");
      } else {
         int var2 = var0.length();
         if (var2 > 0) {
            char var3 = var0.charAt(0);
            if (var3 == '-') {
               throw new NumberFormatException(String.format("Illegal leading minus sign on unsigned string %s.", var0));
            } else if (var2 <= 12 || var1 == 10 && var2 <= 18) {
               return parseLong(var0, var1);
            } else {
               long var4 = parseLong(var0.substring(0, var2 - 1), var1);
               int var6 = Character.digit(var0.charAt(var2 - 1), var1);
               if (var6 < 0) {
                  throw new NumberFormatException("Bad digit at end of " + var0);
               } else {
                  long var7 = var4 * (long)var1 + (long)var6;
                  if (compareUnsigned(var7, var4) < 0) {
                     throw new NumberFormatException(String.format("String value %s exceeds range of unsigned long.", var0));
                  } else {
                     return var7;
                  }
               }
            }
         } else {
            throw NumberFormatException.forInputString(var0);
         }
      }
   }

   public static long parseUnsignedLong(String var0) throws NumberFormatException {
      return parseUnsignedLong(var0, 10);
   }

   public static Long valueOf(String var0, int var1) throws NumberFormatException {
      return parseLong(var0, var1);
   }

   public static Long valueOf(String var0) throws NumberFormatException {
      return parseLong(var0, 10);
   }

   public static Long valueOf(long var0) {
      return var0 >= -128L && var0 <= 127L ? Long.LongCache.cache[(int)var0 + 128] : new Long(var0);
   }

   public static Long decode(String var0) throws NumberFormatException {
      byte var1 = 10;
      int var2 = 0;
      boolean var3 = false;
      if (var0.length() == 0) {
         throw new NumberFormatException("Zero length string");
      } else {
         char var5 = var0.charAt(0);
         if (var5 == '-') {
            var3 = true;
            ++var2;
         } else if (var5 == '+') {
            ++var2;
         }

         if (!var0.startsWith("0x", var2) && !var0.startsWith("0X", var2)) {
            if (var0.startsWith("#", var2)) {
               ++var2;
               var1 = 16;
            } else if (var0.startsWith("0", var2) && var0.length() > 1 + var2) {
               ++var2;
               var1 = 8;
            }
         } else {
            var2 += 2;
            var1 = 16;
         }

         if (!var0.startsWith("-", var2) && !var0.startsWith("+", var2)) {
            Long var4;
            try {
               var4 = valueOf(var0.substring(var2), var1);
               var4 = var3 ? -var4 : var4;
            } catch (NumberFormatException var8) {
               String var7 = var3 ? "-" + var0.substring(var2) : var0.substring(var2);
               var4 = valueOf(var7, var1);
            }

            return var4;
         } else {
            throw new NumberFormatException("Sign character in wrong position");
         }
      }
   }

   public Long(long var1) {
      this.value = var1;
   }

   public Long(String var1) throws NumberFormatException {
      this.value = parseLong(var1, 10);
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
      return this.value;
   }

   public float floatValue() {
      return (float)this.value;
   }

   public double doubleValue() {
      return (double)this.value;
   }

   public String toString() {
      return toString(this.value);
   }

   public int hashCode() {
      return hashCode(this.value);
   }

   public static int hashCode(long var0) {
      return (int)(var0 ^ var0 >>> 32);
   }

   public boolean equals(Object var1) {
      if (var1 instanceof Long) {
         return this.value == (Long)var1;
      } else {
         return false;
      }
   }

   public static Long getLong(String var0) {
      return getLong(var0, (Long)null);
   }

   public static Long getLong(String var0, long var1) {
      Long var3 = getLong(var0, (Long)null);
      return var3 == null ? var1 : var3;
   }

   public static Long getLong(String var0, Long var1) {
      String var2 = null;

      try {
         var2 = System.getProperty(var0);
      } catch (NullPointerException | IllegalArgumentException var4) {
      }

      if (var2 != null) {
         try {
            return decode(var2);
         } catch (NumberFormatException var5) {
         }
      }

      return var1;
   }

   public int compareTo(Long var1) {
      return compare(this.value, var1.value);
   }

   public static int compare(long var0, long var2) {
      return var0 < var2 ? -1 : (var0 == var2 ? 0 : 1);
   }

   public static int compareUnsigned(long var0, long var2) {
      return compare(var0 + MIN_VALUE, var2 + MIN_VALUE);
   }

   public static long divideUnsigned(long var0, long var2) {
      if (var2 < 0L) {
         return compareUnsigned(var0, var2) < 0 ? 0L : 1L;
      } else {
         return var0 > 0L ? var0 / var2 : toUnsignedBigInteger(var0).divide(toUnsignedBigInteger(var2)).longValue();
      }
   }

   public static long remainderUnsigned(long var0, long var2) {
      if (var0 > 0L && var2 > 0L) {
         return var0 % var2;
      } else {
         return compareUnsigned(var0, var2) < 0 ? var0 : toUnsignedBigInteger(var0).remainder(toUnsignedBigInteger(var2)).longValue();
      }
   }

   public static long highestOneBit(long var0) {
      var0 |= var0 >> 1;
      var0 |= var0 >> 2;
      var0 |= var0 >> 4;
      var0 |= var0 >> 8;
      var0 |= var0 >> 16;
      var0 |= var0 >> 32;
      return var0 - (var0 >>> 1);
   }

   public static long lowestOneBit(long var0) {
      return var0 & -var0;
   }

   public static int numberOfLeadingZeros(long var0) {
      if (var0 == 0L) {
         return 64;
      } else {
         int var2 = 1;
         int var3 = (int)(var0 >>> 32);
         if (var3 == 0) {
            var2 += 32;
            var3 = (int)var0;
         }

         if (var3 >>> 16 == 0) {
            var2 += 16;
            var3 <<= 16;
         }

         if (var3 >>> 24 == 0) {
            var2 += 8;
            var3 <<= 8;
         }

         if (var3 >>> 28 == 0) {
            var2 += 4;
            var3 <<= 4;
         }

         if (var3 >>> 30 == 0) {
            var2 += 2;
            var3 <<= 2;
         }

         var2 -= var3 >>> 31;
         return var2;
      }
   }

   public static int numberOfTrailingZeros(long var0) {
      if (var0 == 0L) {
         return 64;
      } else {
         int var4 = 63;
         int var3 = (int)var0;
         int var2;
         if (var3 != 0) {
            var4 -= 32;
            var2 = var3;
         } else {
            var2 = (int)(var0 >>> 32);
         }

         var3 = var2 << 16;
         if (var3 != 0) {
            var4 -= 16;
            var2 = var3;
         }

         var3 = var2 << 8;
         if (var3 != 0) {
            var4 -= 8;
            var2 = var3;
         }

         var3 = var2 << 4;
         if (var3 != 0) {
            var4 -= 4;
            var2 = var3;
         }

         var3 = var2 << 2;
         if (var3 != 0) {
            var4 -= 2;
            var2 = var3;
         }

         return var4 - (var2 << 1 >>> 31);
      }
   }

   public static int bitCount(long var0) {
      var0 -= var0 >>> 1 & 6148914691236517205L;
      var0 = (var0 & 3689348814741910323L) + (var0 >>> 2 & 3689348814741910323L);
      var0 = var0 + (var0 >>> 4) & 1085102592571150095L;
      var0 += var0 >>> 8;
      var0 += var0 >>> 16;
      var0 += var0 >>> 32;
      return (int)var0 & 127;
   }

   public static long rotateLeft(long var0, int var2) {
      return var0 << var2 | var0 >>> -var2;
   }

   public static long rotateRight(long var0, int var2) {
      return var0 >>> var2 | var0 << -var2;
   }

   public static long reverse(long var0) {
      var0 = (var0 & 6148914691236517205L) << 1 | var0 >>> 1 & 6148914691236517205L;
      var0 = (var0 & 3689348814741910323L) << 2 | var0 >>> 2 & 3689348814741910323L;
      var0 = (var0 & 1085102592571150095L) << 4 | var0 >>> 4 & 1085102592571150095L;
      var0 = (var0 & 71777214294589695L) << 8 | var0 >>> 8 & 71777214294589695L;
      var0 = var0 << 48 | (var0 & 4294901760L) << 16 | var0 >>> 16 & 4294901760L | var0 >>> 48;
      return var0;
   }

   public static int signum(long var0) {
      return (int)(var0 >> 63 | -var0 >>> 63);
   }

   public static long reverseBytes(long var0) {
      var0 = (var0 & 71777214294589695L) << 8 | var0 >>> 8 & 71777214294589695L;
      return var0 << 48 | (var0 & 4294901760L) << 16 | var0 >>> 16 & 4294901760L | var0 >>> 48;
   }

   public static long sum(long var0, long var2) {
      return var0 + var2;
   }

   public static long max(long var0, long var2) {
      return Math.max(var0, var2);
   }

   public static long min(long var0, long var2) {
      return Math.min(var0, var2);
   }

   private static class LongCache {
      static final Long[] cache = new Long[256];

      static {
         for(int var0 = 0; var0 < cache.length; ++var0) {
            cache[var0] = new Long((long)(var0 - 128));
         }

      }
   }
}
