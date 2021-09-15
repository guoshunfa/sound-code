package sun.misc;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FloatingDecimal {
   static final int EXP_SHIFT = 52;
   static final long FRACT_HOB = 4503599627370496L;
   static final long EXP_ONE = 4607182418800017408L;
   static final int MAX_SMALL_BIN_EXP = 62;
   static final int MIN_SMALL_BIN_EXP = -21;
   static final int MAX_DECIMAL_DIGITS = 15;
   static final int MAX_DECIMAL_EXPONENT = 308;
   static final int MIN_DECIMAL_EXPONENT = -324;
   static final int BIG_DECIMAL_EXPONENT = 324;
   static final int MAX_NDIGITS = 1100;
   static final int SINGLE_EXP_SHIFT = 23;
   static final int SINGLE_FRACT_HOB = 8388608;
   static final int SINGLE_MAX_DECIMAL_DIGITS = 7;
   static final int SINGLE_MAX_DECIMAL_EXPONENT = 38;
   static final int SINGLE_MIN_DECIMAL_EXPONENT = -45;
   static final int SINGLE_MAX_NDIGITS = 200;
   static final int INT_DECIMAL_DIGITS = 9;
   private static final String INFINITY_REP = "Infinity";
   private static final int INFINITY_LENGTH = "Infinity".length();
   private static final String NAN_REP = "NaN";
   private static final int NAN_LENGTH = "NaN".length();
   private static final FloatingDecimal.BinaryToASCIIConverter B2AC_POSITIVE_INFINITY = new FloatingDecimal.ExceptionalBinaryToASCIIBuffer("Infinity", false);
   private static final FloatingDecimal.BinaryToASCIIConverter B2AC_NEGATIVE_INFINITY = new FloatingDecimal.ExceptionalBinaryToASCIIBuffer("-Infinity", true);
   private static final FloatingDecimal.BinaryToASCIIConverter B2AC_NOT_A_NUMBER = new FloatingDecimal.ExceptionalBinaryToASCIIBuffer("NaN", false);
   private static final FloatingDecimal.BinaryToASCIIConverter B2AC_POSITIVE_ZERO = new FloatingDecimal.BinaryToASCIIBuffer(false, new char[]{'0'});
   private static final FloatingDecimal.BinaryToASCIIConverter B2AC_NEGATIVE_ZERO = new FloatingDecimal.BinaryToASCIIBuffer(true, new char[]{'0'});
   private static final ThreadLocal<FloatingDecimal.BinaryToASCIIBuffer> threadLocalBinaryToASCIIBuffer = new ThreadLocal<FloatingDecimal.BinaryToASCIIBuffer>() {
      protected FloatingDecimal.BinaryToASCIIBuffer initialValue() {
         return new FloatingDecimal.BinaryToASCIIBuffer();
      }
   };
   static final FloatingDecimal.ASCIIToBinaryConverter A2BC_POSITIVE_INFINITY = new FloatingDecimal.PreparedASCIIToBinaryBuffer(Double.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
   static final FloatingDecimal.ASCIIToBinaryConverter A2BC_NEGATIVE_INFINITY = new FloatingDecimal.PreparedASCIIToBinaryBuffer(Double.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
   static final FloatingDecimal.ASCIIToBinaryConverter A2BC_NOT_A_NUMBER = new FloatingDecimal.PreparedASCIIToBinaryBuffer(Double.NaN, Float.NaN);
   static final FloatingDecimal.ASCIIToBinaryConverter A2BC_POSITIVE_ZERO = new FloatingDecimal.PreparedASCIIToBinaryBuffer(0.0D, 0.0F);
   static final FloatingDecimal.ASCIIToBinaryConverter A2BC_NEGATIVE_ZERO = new FloatingDecimal.PreparedASCIIToBinaryBuffer(-0.0D, -0.0F);

   public static String toJavaFormatString(double var0) {
      return getBinaryToASCIIConverter(var0).toJavaFormatString();
   }

   public static String toJavaFormatString(float var0) {
      return getBinaryToASCIIConverter(var0).toJavaFormatString();
   }

   public static void appendTo(double var0, Appendable var2) {
      getBinaryToASCIIConverter(var0).appendTo(var2);
   }

   public static void appendTo(float var0, Appendable var1) {
      getBinaryToASCIIConverter(var0).appendTo(var1);
   }

   public static double parseDouble(String var0) throws NumberFormatException {
      return readJavaFormatString(var0).doubleValue();
   }

   public static float parseFloat(String var0) throws NumberFormatException {
      return readJavaFormatString(var0).floatValue();
   }

   private static FloatingDecimal.BinaryToASCIIBuffer getBinaryToASCIIBuffer() {
      return (FloatingDecimal.BinaryToASCIIBuffer)threadLocalBinaryToASCIIBuffer.get();
   }

   public static FloatingDecimal.BinaryToASCIIConverter getBinaryToASCIIConverter(double var0) {
      return getBinaryToASCIIConverter(var0, true);
   }

   static FloatingDecimal.BinaryToASCIIConverter getBinaryToASCIIConverter(double var0, boolean var2) {
      long var3 = Double.doubleToRawLongBits(var0);
      boolean var5 = (var3 & Long.MIN_VALUE) != 0L;
      long var6 = var3 & 4503599627370495L;
      int var8 = (int)((var3 & 9218868437227405312L) >> 52);
      if (var8 == 2047) {
         if (var6 == 0L) {
            return var5 ? B2AC_NEGATIVE_INFINITY : B2AC_POSITIVE_INFINITY;
         } else {
            return B2AC_NOT_A_NUMBER;
         }
      } else {
         int var9;
         if (var8 == 0) {
            if (var6 == 0L) {
               return var5 ? B2AC_NEGATIVE_ZERO : B2AC_POSITIVE_ZERO;
            }

            int var10 = Long.numberOfLeadingZeros(var6);
            int var11 = var10 - 11;
            var6 <<= var11;
            var8 = 1 - var11;
            var9 = 64 - var10;
         } else {
            var6 |= 4503599627370496L;
            var9 = 53;
         }

         var8 -= 1023;
         FloatingDecimal.BinaryToASCIIBuffer var12 = getBinaryToASCIIBuffer();
         var12.setSign(var5);
         var12.dtoa(var8, var6, var9, var2);
         return var12;
      }
   }

   private static FloatingDecimal.BinaryToASCIIConverter getBinaryToASCIIConverter(float var0) {
      int var1 = Float.floatToRawIntBits(var0);
      boolean var2 = (var1 & Integer.MIN_VALUE) != 0;
      int var3 = var1 & 8388607;
      int var4 = (var1 & 2139095040) >> 23;
      if (var4 == 255) {
         if ((long)var3 == 0L) {
            return var2 ? B2AC_NEGATIVE_INFINITY : B2AC_POSITIVE_INFINITY;
         } else {
            return B2AC_NOT_A_NUMBER;
         }
      } else {
         int var5;
         if (var4 == 0) {
            if (var3 == 0) {
               return var2 ? B2AC_NEGATIVE_ZERO : B2AC_POSITIVE_ZERO;
            }

            int var6 = Integer.numberOfLeadingZeros(var3);
            int var7 = var6 - 8;
            var3 <<= var7;
            var4 = 1 - var7;
            var5 = 32 - var6;
         } else {
            var3 |= 8388608;
            var5 = 24;
         }

         var4 -= 127;
         FloatingDecimal.BinaryToASCIIBuffer var8 = getBinaryToASCIIBuffer();
         var8.setSign(var2);
         var8.dtoa(var4, (long)var3 << 29, var5, true);
         return var8;
      }
   }

   static FloatingDecimal.ASCIIToBinaryConverter readJavaFormatString(String var0) throws NumberFormatException {
      boolean var1 = false;
      boolean var2 = false;

      try {
         var0 = var0.trim();
         int var5 = var0.length();
         if (var5 == 0) {
            throw new NumberFormatException("empty String");
         }

         int var6 = 0;
         switch(var0.charAt(var6)) {
         case '-':
            var1 = true;
         case '+':
            ++var6;
            var2 = true;
         }

         char var4 = var0.charAt(var6);
         if (var4 == 'N') {
            if (var5 - var6 == NAN_LENGTH && var0.indexOf("NaN", var6) == var6) {
               return A2BC_NOT_A_NUMBER;
            }
         } else if (var4 == 'I') {
            if (var5 - var6 == INFINITY_LENGTH && var0.indexOf("Infinity", var6) == var6) {
               return var1 ? A2BC_NEGATIVE_INFINITY : A2BC_POSITIVE_INFINITY;
            }
         } else {
            if (var4 == '0' && var5 > var6 + 1) {
               char var7 = var0.charAt(var6 + 1);
               if (var7 == 'x' || var7 == 'X') {
                  return parseHexString(var0);
               }
            }

            char[] var21 = new char[var5];
            int var8 = 0;
            boolean var9 = false;
            int var10 = 0;
            int var11 = 0;

            int var12;
            for(var12 = 0; var6 < var5; ++var6) {
               var4 = var0.charAt(var6);
               if (var4 == '0') {
                  ++var11;
               } else {
                  if (var4 != '.') {
                     break;
                  }

                  if (var9) {
                     throw new NumberFormatException("multiple points");
                  }

                  var10 = var6;
                  if (var2) {
                     var10 = var6 - 1;
                  }

                  var9 = true;
               }
            }

            for(; var6 < var5; ++var6) {
               var4 = var0.charAt(var6);
               if (var4 >= '1' && var4 <= '9') {
                  var21[var8++] = var4;
                  var12 = 0;
               } else if (var4 == '0') {
                  var21[var8++] = var4;
                  ++var12;
               } else {
                  if (var4 != '.') {
                     break;
                  }

                  if (var9) {
                     throw new NumberFormatException("multiple points");
                  }

                  var10 = var6;
                  if (var2) {
                     var10 = var6 - 1;
                  }

                  var9 = true;
               }
            }

            var8 -= var12;
            boolean var13 = var8 == 0;
            if (!var13 || var11 != 0) {
               int var3;
               if (var9) {
                  var3 = var10 - var11;
               } else {
                  var3 = var8 + var12;
               }

               if (var6 < var5 && ((var4 = var0.charAt(var6)) == 'e' || var4 == 'E')) {
                  byte var14 = 1;
                  int var15 = 0;
                  int var16 = 214748364;
                  boolean var17 = false;
                  ++var6;
                  int var18;
                  switch(var0.charAt(var6)) {
                  case '-':
                     var14 = -1;
                  case '+':
                     ++var6;
                  default:
                     var18 = var6;
                  }

                  while(var6 < var5) {
                     if (var15 >= var16) {
                        var17 = true;
                     }

                     var4 = var0.charAt(var6++);
                     if (var4 < '0' || var4 > '9') {
                        --var6;
                        break;
                     }

                     var15 = var15 * 10 + (var4 - 48);
                  }

                  int var19 = 324 + var8 + var12;
                  if (!var17 && var15 <= var19) {
                     var3 += var14 * var15;
                  } else {
                     var3 = var14 * var19;
                  }

                  if (var6 == var18) {
                     throw new NumberFormatException("For input string: \"" + var0 + "\"");
                  }
               }

               if (var6 >= var5 || var6 == var5 - 1 && (var0.charAt(var6) == 'f' || var0.charAt(var6) == 'F' || var0.charAt(var6) == 'd' || var0.charAt(var6) == 'D')) {
                  if (var13) {
                     return var1 ? A2BC_NEGATIVE_ZERO : A2BC_POSITIVE_ZERO;
                  }

                  return new FloatingDecimal.ASCIIToBinaryBuffer(var1, var3, var21, var8);
               }
            }
         }
      } catch (StringIndexOutOfBoundsException var20) {
      }

      throw new NumberFormatException("For input string: \"" + var0 + "\"");
   }

   static FloatingDecimal.ASCIIToBinaryConverter parseHexString(String var0) {
      Matcher var1 = FloatingDecimal.HexFloatPattern.VALUE.matcher(var0);
      boolean var2 = var1.matches();
      if (!var2) {
         throw new NumberFormatException("For input string: \"" + var0 + "\"");
      } else {
         String var3 = var1.group(1);
         boolean var4 = var3 != null && var3.equals("-");
         String var5 = null;
         boolean var6 = false;
         boolean var7 = false;
         boolean var8 = false;
         int var9 = 0;
         String var10;
         int var32;
         if ((var10 = var1.group(4)) != null) {
            var5 = stripLeadingZeros(var10);
            var32 = var5.length();
         } else {
            String var11 = stripLeadingZeros(var1.group(6));
            var32 = var11.length();
            String var12 = var1.group(7);
            var9 = var12.length();
            var5 = (var11 == null ? "" : var11) + var12;
         }

         var5 = stripLeadingZeros(var5);
         int var30 = var5.length();
         int var31;
         if (var32 >= 1) {
            var31 = 4 * (var32 - 1);
         } else {
            var31 = -4 * (var9 - var30 + 1);
         }

         if (var30 == 0) {
            return var4 ? A2BC_NEGATIVE_ZERO : A2BC_POSITIVE_ZERO;
         } else {
            String var33 = var1.group(8);
            boolean var34 = var33 == null || var33.equals("+");

            long var35;
            try {
               var35 = (long)Integer.parseInt(var1.group(9));
            } catch (NumberFormatException var29) {
               return var4 ? (var34 ? A2BC_NEGATIVE_INFINITY : A2BC_NEGATIVE_ZERO) : (var34 ? A2BC_POSITIVE_INFINITY : A2BC_POSITIVE_ZERO);
            }

            long var36 = (var34 ? 1L : -1L) * var35;
            long var14 = var36 + (long)var31;
            boolean var16 = false;
            boolean var17 = false;
            boolean var18 = false;
            long var19 = 0L;
            long var21 = (long)getHexDigit(var5, 0);
            int var37;
            if (var21 == 1L) {
               var19 |= var21 << 52;
               var37 = 48;
            } else if (var21 <= 3L) {
               var19 |= var21 << 51;
               var37 = 47;
               ++var14;
            } else if (var21 <= 7L) {
               var19 |= var21 << 50;
               var37 = 46;
               var14 += 2L;
            } else {
               if (var21 > 15L) {
                  throw new AssertionError("Result from digit conversion too large!");
               }

               var19 |= var21 << 49;
               var37 = 45;
               var14 += 3L;
            }

            boolean var23 = false;

            long var24;
            int var38;
            for(var38 = 1; var38 < var30 && var37 >= 0; ++var38) {
               var24 = (long)getHexDigit(var5, var38);
               var19 |= var24 << var37;
               var37 -= 4;
            }

            if (var38 < var30) {
               var24 = (long)getHexDigit(var5, var38);
               switch(var37) {
               case -4:
                  var16 = (var24 & 8L) != 0L;
                  var17 = (var24 & 7L) != 0L;
                  break;
               case -3:
                  var19 |= (var24 & 8L) >> 3;
                  var16 = (var24 & 4L) != 0L;
                  var17 = (var24 & 3L) != 0L;
                  break;
               case -2:
                  var19 |= (var24 & 12L) >> 2;
                  var16 = (var24 & 2L) != 0L;
                  var17 = (var24 & 1L) != 0L;
                  break;
               case -1:
                  var19 |= (var24 & 14L) >> 1;
                  var16 = (var24 & 1L) != 0L;
                  break;
               default:
                  throw new AssertionError("Unexpected shift distance remainder.");
               }

               ++var38;

               while(var38 < var30 && !var17) {
                  var24 = (long)getHexDigit(var5, var38);
                  var17 = var17 || var24 != 0L;
                  ++var38;
               }
            }

            int var39 = var4 ? Integer.MIN_VALUE : 0;
            boolean var26;
            int var27;
            if (var14 >= -126L) {
               if (var14 > 127L) {
                  var39 |= 2139095040;
               } else {
                  byte var25 = 28;
                  var26 = (var19 & (1L << var25) - 1L) != 0L || var16 || var17;
                  var27 = (int)(var19 >>> var25);
                  if ((var27 & 3) != 1 || var26) {
                     ++var27;
                  }

                  var39 |= ((int)var14 + 126 << 23) + (var27 >> 1);
               }
            } else if (var14 >= -150L) {
               int var40 = (int)(-98L - var14);

               assert var40 >= 29;

               assert var40 < 53;

               var26 = (var19 & (1L << var40) - 1L) != 0L || var16 || var17;
               var27 = (int)(var19 >>> var40);
               if ((var27 & 3) != 1 || var26) {
                  ++var27;
               }

               var39 |= var27 >> 1;
            }

            float var41 = Float.intBitsToFloat(var39);
            if (var14 > 1023L) {
               return var4 ? A2BC_NEGATIVE_INFINITY : A2BC_POSITIVE_INFINITY;
            } else {
               if (var14 <= 1023L && var14 >= -1022L) {
                  var19 = var14 + 1023L << 52 & 9218868437227405312L | 4503599627370495L & var19;
               } else {
                  if (var14 < -1075L) {
                     return var4 ? A2BC_NEGATIVE_ZERO : A2BC_POSITIVE_ZERO;
                  }

                  var17 = var17 || var16;
                  var16 = false;
                  int var42 = 53 - ((int)var14 - -1074 + 1);

                  assert var42 >= 1 && var42 <= 53;

                  var16 = (var19 & 1L << var42 - 1) != 0L;
                  if (var42 > 1) {
                     long var43 = ~(-1L << var42 - 1);
                     var17 = var17 || (var19 & var43) != 0L;
                  }

                  var19 >>= var42;
                  var19 = 0L | 4503599627370495L & var19;
               }

               var26 = (var19 & 1L) == 0L;
               if (var26 && var16 && var17 || !var26 && var16) {
                  ++var19;
               }

               double var44 = var4 ? Double.longBitsToDouble(var19 | Long.MIN_VALUE) : Double.longBitsToDouble(var19);
               return new FloatingDecimal.PreparedASCIIToBinaryBuffer(var44, var41);
            }
         }
      }
   }

   static String stripLeadingZeros(String var0) {
      if (!var0.isEmpty() && var0.charAt(0) == '0') {
         for(int var1 = 1; var1 < var0.length(); ++var1) {
            if (var0.charAt(var1) != '0') {
               return var0.substring(var1);
            }
         }

         return "";
      } else {
         return var0;
      }
   }

   static int getHexDigit(String var0, int var1) {
      int var2 = Character.digit((char)var0.charAt(var1), 16);
      if (var2 > -1 && var2 < 16) {
         return var2;
      } else {
         throw new AssertionError("Unexpected failure of digit conversion of " + var0.charAt(var1));
      }
   }

   private static class HexFloatPattern {
      private static final Pattern VALUE = Pattern.compile("([-+])?0[xX](((\\p{XDigit}+)\\.?)|((\\p{XDigit}*)\\.(\\p{XDigit}+)))[pP]([-+])?(\\p{Digit}+)[fFdD]?");
   }

   static class ASCIIToBinaryBuffer implements FloatingDecimal.ASCIIToBinaryConverter {
      boolean isNegative;
      int decExponent;
      char[] digits;
      int nDigits;
      private static final double[] SMALL_10_POW = new double[]{1.0D, 10.0D, 100.0D, 1000.0D, 10000.0D, 100000.0D, 1000000.0D, 1.0E7D, 1.0E8D, 1.0E9D, 1.0E10D, 1.0E11D, 1.0E12D, 1.0E13D, 1.0E14D, 1.0E15D, 1.0E16D, 1.0E17D, 1.0E18D, 1.0E19D, 1.0E20D, 1.0E21D, 1.0E22D};
      private static final float[] SINGLE_SMALL_10_POW = new float[]{1.0F, 10.0F, 100.0F, 1000.0F, 10000.0F, 100000.0F, 1000000.0F, 1.0E7F, 1.0E8F, 1.0E9F, 1.0E10F};
      private static final double[] BIG_10_POW = new double[]{1.0E16D, 1.0E32D, 1.0E64D, 1.0E128D, 1.0E256D};
      private static final double[] TINY_10_POW = new double[]{1.0E-16D, 1.0E-32D, 1.0E-64D, 1.0E-128D, 1.0E-256D};
      private static final int MAX_SMALL_TEN;
      private static final int SINGLE_MAX_SMALL_TEN;

      ASCIIToBinaryBuffer(boolean var1, int var2, char[] var3, int var4) {
         this.isNegative = var1;
         this.decExponent = var2;
         this.digits = var3;
         this.nDigits = var4;
      }

      public double doubleValue() {
         int var1 = Math.min(this.nDigits, 16);
         int var2 = this.digits[0] - 48;
         int var3 = Math.min(var1, 9);

         for(int var4 = 1; var4 < var3; ++var4) {
            var2 = var2 * 10 + this.digits[var4] - 48;
         }

         long var31 = (long)var2;

         for(int var6 = var3; var6 < var1; ++var6) {
            var31 = var31 * 10L + (long)(this.digits[var6] - 48);
         }

         double var32 = (double)var31;
         int var8 = this.decExponent - var1;
         int var9;
         double var10;
         if (this.nDigits <= 15) {
            if (var8 == 0 || var32 == 0.0D) {
               return this.isNegative ? -var32 : var32;
            }

            double var33;
            if (var8 >= 0) {
               if (var8 <= MAX_SMALL_TEN) {
                  var33 = var32 * SMALL_10_POW[var8];
                  return this.isNegative ? -var33 : var33;
               }

               var9 = 15 - var1;
               if (var8 <= MAX_SMALL_TEN + var9) {
                  var32 *= SMALL_10_POW[var9];
                  var10 = var32 * SMALL_10_POW[var8 - var9];
                  return this.isNegative ? -var10 : var10;
               }
            } else if (var8 >= -MAX_SMALL_TEN) {
               var33 = var32 / SMALL_10_POW[-var8];
               return this.isNegative ? -var33 : var33;
            }
         }

         if (var8 > 0) {
            if (this.decExponent > 309) {
               return this.isNegative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
            }

            if ((var8 & 15) != 0) {
               var32 *= SMALL_10_POW[var8 & 15];
            }

            if ((var8 >>= 4) != 0) {
               for(var9 = 0; var8 > 1; var8 >>= 1) {
                  if ((var8 & 1) != 0) {
                     var32 *= BIG_10_POW[var9];
                  }

                  ++var9;
               }

               var10 = var32 * BIG_10_POW[var9];
               if (Double.isInfinite(var10)) {
                  var10 = var32 / 2.0D;
                  var10 *= BIG_10_POW[var9];
                  if (Double.isInfinite(var10)) {
                     return this.isNegative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
                  }

                  var10 = Double.MAX_VALUE;
               }

               var32 = var10;
            }
         } else if (var8 < 0) {
            var8 = -var8;
            if (this.decExponent < -325) {
               return this.isNegative ? -0.0D : 0.0D;
            }

            if ((var8 & 15) != 0) {
               var32 /= SMALL_10_POW[var8 & 15];
            }

            if ((var8 >>= 4) != 0) {
               for(var9 = 0; var8 > 1; var8 >>= 1) {
                  if ((var8 & 1) != 0) {
                     var32 *= TINY_10_POW[var9];
                  }

                  ++var9;
               }

               var10 = var32 * TINY_10_POW[var9];
               if (var10 == 0.0D) {
                  var10 = var32 * 2.0D;
                  var10 *= TINY_10_POW[var9];
                  if (var10 == 0.0D) {
                     return this.isNegative ? -0.0D : 0.0D;
                  }

                  var10 = Double.MIN_VALUE;
               }

               var32 = var10;
            }
         }

         if (this.nDigits > 1100) {
            this.nDigits = 1101;
            this.digits[1100] = '1';
         }

         FDBigInteger var34 = new FDBigInteger(var31, this.digits, var1, this.nDigits);
         var8 = this.decExponent - this.nDigits;
         long var35 = Double.doubleToRawLongBits(var32);
         int var12 = Math.max(0, -var8);
         int var13 = Math.max(0, var8);
         var34 = var34.multByPow52(var13, 0);
         var34.makeImmutable();
         FDBigInteger var14 = null;
         int var15 = 0;

         do {
            int var16 = (int)(var35 >>> 52);
            long var17 = var35 & 4503599627370495L;
            int var19;
            int var20;
            if (var16 > 0) {
               var17 |= 4503599627370496L;
            } else {
               assert var17 != 0L : var17;

               var19 = Long.numberOfLeadingZeros(var17);
               var20 = var19 - 11;
               var17 <<= var20;
               var16 = 1 - var20;
            }

            var16 -= 1023;
            var19 = Long.numberOfTrailingZeros(var17);
            var17 >>>= var19;
            var20 = var16 - 52 + var19;
            int var21 = 53 - var19;
            int var22 = var12;
            int var23 = var13;
            if (var20 >= 0) {
               var22 = var12 + var20;
            } else {
               var23 = var13 - var20;
            }

            int var24 = var22;
            int var25;
            if (var16 <= -1023) {
               var25 = var16 + var19 + 1023;
            } else {
               var25 = 1 + var19;
            }

            var22 += var25;
            var23 += var25;
            int var26 = Math.min(var22, Math.min(var23, var24));
            var22 -= var26;
            var23 -= var26;
            var24 -= var26;
            FDBigInteger var27 = FDBigInteger.valueOfMulPow52(var17, var12, var22);
            if (var14 == null || var15 != var23) {
               var14 = var34.leftShift(var23);
               var15 = var23;
            }

            FDBigInteger var28;
            int var29;
            boolean var30;
            if ((var29 = var27.cmp(var14)) > 0) {
               var30 = true;
               var28 = var27.leftInplaceSub(var14);
               if (var21 == 1 && var20 > -1022) {
                  --var24;
                  if (var24 < 0) {
                     var24 = 0;
                     var28 = var28.leftShift(1);
                  }
               }
            } else {
               if (var29 >= 0) {
                  break;
               }

               var30 = false;
               var28 = var14.rightInplaceSub(var27);
            }

            var29 = var28.cmpPow52(var12, var24);
            if (var29 < 0) {
               break;
            }

            if (var29 == 0) {
               if ((var35 & 1L) != 0L) {
                  var35 += var30 ? -1L : 1L;
               }
               break;
            }

            var35 += var30 ? -1L : 1L;
         } while(var35 != 0L && var35 != 9218868437227405312L);

         if (this.isNegative) {
            var35 |= Long.MIN_VALUE;
         }

         return Double.longBitsToDouble(var35);
      }

      public float floatValue() {
         int var1 = Math.min(this.nDigits, 8);
         int var2 = this.digits[0] - 48;

         for(int var3 = 1; var3 < var1; ++var3) {
            var2 = var2 * 10 + this.digits[var3] - 48;
         }

         float var27 = (float)var2;
         int var4 = this.decExponent - var1;
         int var7;
         if (this.nDigits <= 7) {
            if (var4 == 0 || var27 == 0.0F) {
               return this.isNegative ? -var27 : var27;
            }

            if (var4 >= 0) {
               if (var4 <= SINGLE_MAX_SMALL_TEN) {
                  var27 *= SINGLE_SMALL_10_POW[var4];
                  return this.isNegative ? -var27 : var27;
               }

               int var5 = 7 - var1;
               if (var4 <= SINGLE_MAX_SMALL_TEN + var5) {
                  var27 *= SINGLE_SMALL_10_POW[var5];
                  var27 *= SINGLE_SMALL_10_POW[var4 - var5];
                  return this.isNegative ? -var27 : var27;
               }
            } else if (var4 >= -SINGLE_MAX_SMALL_TEN) {
               var27 /= SINGLE_SMALL_10_POW[-var4];
               return this.isNegative ? -var27 : var27;
            }
         } else if (this.decExponent >= this.nDigits && this.nDigits + this.decExponent <= 15) {
            long var29 = (long)var2;

            for(var7 = var1; var7 < this.nDigits; ++var7) {
               var29 = var29 * 10L + (long)(this.digits[var7] - 48);
            }

            double var31 = (double)var29;
            var4 = this.decExponent - this.nDigits;
            var31 *= SMALL_10_POW[var4];
            var27 = (float)var31;
            return this.isNegative ? -var27 : var27;
         }

         double var28 = (double)var27;
         if (var4 > 0) {
            if (this.decExponent > 39) {
               return this.isNegative ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
            }

            if ((var4 & 15) != 0) {
               var28 *= SMALL_10_POW[var4 & 15];
            }

            if ((var4 >>= 4) != 0) {
               for(var7 = 0; var4 > 0; var4 >>= 1) {
                  if ((var4 & 1) != 0) {
                     var28 *= BIG_10_POW[var7];
                  }

                  ++var7;
               }
            }
         } else if (var4 < 0) {
            var4 = -var4;
            if (this.decExponent < -46) {
               return this.isNegative ? -0.0F : 0.0F;
            }

            if ((var4 & 15) != 0) {
               var28 /= SMALL_10_POW[var4 & 15];
            }

            if ((var4 >>= 4) != 0) {
               for(var7 = 0; var4 > 0; var4 >>= 1) {
                  if ((var4 & 1) != 0) {
                     var28 *= TINY_10_POW[var7];
                  }

                  ++var7;
               }
            }
         }

         var27 = Math.max(Float.MIN_VALUE, Math.min(Float.MAX_VALUE, (float)var28));
         if (this.nDigits > 200) {
            this.nDigits = 201;
            this.digits[200] = '1';
         }

         FDBigInteger var30 = new FDBigInteger((long)var2, this.digits, var1, this.nDigits);
         var4 = this.decExponent - this.nDigits;
         int var8 = Float.floatToRawIntBits(var27);
         int var9 = Math.max(0, -var4);
         int var10 = Math.max(0, var4);
         var30 = var30.multByPow52(var10, 0);
         var30.makeImmutable();
         FDBigInteger var11 = null;
         int var12 = 0;

         do {
            int var13 = var8 >>> 23;
            int var14 = var8 & 8388607;
            int var15;
            int var16;
            if (var13 > 0) {
               var14 |= 8388608;
            } else {
               assert var14 != 0 : var14;

               var15 = Integer.numberOfLeadingZeros(var14);
               var16 = var15 - 8;
               var14 <<= var16;
               var13 = 1 - var16;
            }

            var13 -= 127;
            var15 = Integer.numberOfTrailingZeros(var14);
            var14 >>>= var15;
            var16 = var13 - 23 + var15;
            int var17 = 24 - var15;
            int var18 = var9;
            int var19 = var10;
            if (var16 >= 0) {
               var18 = var9 + var16;
            } else {
               var19 = var10 - var16;
            }

            int var20 = var18;
            int var21;
            if (var13 <= -127) {
               var21 = var13 + var15 + 127;
            } else {
               var21 = 1 + var15;
            }

            var18 += var21;
            var19 += var21;
            int var22 = Math.min(var18, Math.min(var19, var20));
            var18 -= var22;
            var19 -= var22;
            var20 -= var22;
            FDBigInteger var23 = FDBigInteger.valueOfMulPow52((long)var14, var9, var18);
            if (var11 == null || var12 != var19) {
               var11 = var30.leftShift(var19);
               var12 = var19;
            }

            FDBigInteger var24;
            int var25;
            boolean var26;
            if ((var25 = var23.cmp(var11)) > 0) {
               var26 = true;
               var24 = var23.leftInplaceSub(var11);
               if (var17 == 1 && var16 > -126) {
                  --var20;
                  if (var20 < 0) {
                     var20 = 0;
                     var24 = var24.leftShift(1);
                  }
               }
            } else {
               if (var25 >= 0) {
                  break;
               }

               var26 = false;
               var24 = var11.rightInplaceSub(var23);
            }

            var25 = var24.cmpPow52(var9, var20);
            if (var25 < 0) {
               break;
            }

            if (var25 == 0) {
               if ((var8 & 1) != 0) {
                  var8 += var26 ? -1 : 1;
               }
               break;
            }

            var8 += var26 ? -1 : 1;
         } while(var8 != 0 && var8 != 2139095040);

         if (this.isNegative) {
            var8 |= Integer.MIN_VALUE;
         }

         return Float.intBitsToFloat(var8);
      }

      static {
         MAX_SMALL_TEN = SMALL_10_POW.length - 1;
         SINGLE_MAX_SMALL_TEN = SINGLE_SMALL_10_POW.length - 1;
      }
   }

   static class PreparedASCIIToBinaryBuffer implements FloatingDecimal.ASCIIToBinaryConverter {
      private final double doubleVal;
      private final float floatVal;

      public PreparedASCIIToBinaryBuffer(double var1, float var3) {
         this.doubleVal = var1;
         this.floatVal = var3;
      }

      public double doubleValue() {
         return this.doubleVal;
      }

      public float floatValue() {
         return this.floatVal;
      }
   }

   interface ASCIIToBinaryConverter {
      double doubleValue();

      float floatValue();
   }

   static class BinaryToASCIIBuffer implements FloatingDecimal.BinaryToASCIIConverter {
      private boolean isNegative;
      private int decExponent;
      private int firstDigitIndex;
      private int nDigits;
      private final char[] digits;
      private final char[] buffer = new char[26];
      private boolean exactDecimalConversion = false;
      private boolean decimalDigitsRoundedUp = false;
      private static int[] insignificantDigitsNumber = new int[]{0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7, 8, 8, 8, 9, 9, 9, 9, 10, 10, 10, 11, 11, 11, 12, 12, 12, 12, 13, 13, 13, 14, 14, 14, 15, 15, 15, 15, 16, 16, 16, 17, 17, 17, 18, 18, 18, 19};
      private static final int[] N_5_BITS = new int[]{0, 3, 5, 7, 10, 12, 14, 17, 19, 21, 24, 26, 28, 31, 33, 35, 38, 40, 42, 45, 47, 49, 52, 54, 56, 59, 61};

      BinaryToASCIIBuffer() {
         this.digits = new char[20];
      }

      BinaryToASCIIBuffer(boolean var1, char[] var2) {
         this.isNegative = var1;
         this.decExponent = 0;
         this.digits = var2;
         this.firstDigitIndex = 0;
         this.nDigits = var2.length;
      }

      public String toJavaFormatString() {
         int var1 = this.getChars(this.buffer);
         return new String(this.buffer, 0, var1);
      }

      public void appendTo(Appendable var1) {
         int var2 = this.getChars(this.buffer);
         if (var1 instanceof StringBuilder) {
            ((StringBuilder)var1).append((char[])this.buffer, 0, var2);
         } else if (var1 instanceof StringBuffer) {
            ((StringBuffer)var1).append((char[])this.buffer, 0, var2);
         } else {
            assert false;
         }

      }

      public int getDecimalExponent() {
         return this.decExponent;
      }

      public int getDigits(char[] var1) {
         System.arraycopy(this.digits, this.firstDigitIndex, var1, 0, this.nDigits);
         return this.nDigits;
      }

      public boolean isNegative() {
         return this.isNegative;
      }

      public boolean isExceptional() {
         return false;
      }

      public boolean digitsRoundedUp() {
         return this.decimalDigitsRoundedUp;
      }

      public boolean decimalDigitsExact() {
         return this.exactDecimalConversion;
      }

      private void setSign(boolean var1) {
         this.isNegative = var1;
      }

      private void developLongDigits(int var1, long var2, int var4) {
         if (var4 != 0) {
            long var5 = FDBigInteger.LONG_5_POW[var4] << var4;
            long var7 = var2 % var5;
            var2 /= var5;
            var1 += var4;
            if (var7 >= var5 >> 1) {
               ++var2;
            }
         }

         int var9 = this.digits.length - 1;
         int var6;
         if (var2 <= 2147483647L) {
            assert var2 > 0L : var2;

            int var10 = (int)var2;
            var6 = var10 % 10;

            for(var10 /= 10; var6 == 0; var10 /= 10) {
               ++var1;
               var6 = var10 % 10;
            }

            while(var10 != 0) {
               this.digits[var9--] = (char)(var6 + 48);
               ++var1;
               var6 = var10 % 10;
               var10 /= 10;
            }

            this.digits[var9] = (char)(var6 + 48);
         } else {
            var6 = (int)(var2 % 10L);

            for(var2 /= 10L; var6 == 0; var2 /= 10L) {
               ++var1;
               var6 = (int)(var2 % 10L);
            }

            while(var2 != 0L) {
               this.digits[var9--] = (char)(var6 + 48);
               ++var1;
               var6 = (int)(var2 % 10L);
               var2 /= 10L;
            }

            this.digits[var9] = (char)(var6 + 48);
         }

         this.decExponent = var1 + 1;
         this.firstDigitIndex = var9;
         this.nDigits = this.digits.length - var9;
      }

      private void dtoa(int var1, long var2, int var4, boolean var5) {
         assert var2 > 0L;

         assert (var2 & 4503599627370496L) != 0L;

         int var6 = Long.numberOfTrailingZeros(var2);
         int var7 = 53 - var6;
         this.decimalDigitsRoundedUp = false;
         this.exactDecimalConversion = false;
         int var8 = Math.max(0, var7 - var1 - 1);
         int var9;
         if (var1 <= 62 && var1 >= -21 && var8 < FDBigInteger.LONG_5_POW.length && var7 + N_5_BITS[var8] < 64 && var8 == 0) {
            if (var1 > var4) {
               var9 = insignificantDigitsForPow2(var1 - var4 - 1);
            } else {
               var9 = 0;
            }

            if (var1 >= 52) {
               var2 <<= var1 - 52;
            } else {
               var2 >>>= 52 - var1;
            }

            this.developLongDigits(0, var2, var9);
         } else {
            var9 = estimateDecExp(var2, var1);
            int var11 = Math.max(0, -var9);
            int var10 = var11 + var8 + var1;
            int var13 = Math.max(0, var9);
            int var12 = var13 + var8;
            int var14 = var10 - var4;
            var2 >>>= var6;
            var10 -= var7 - 1;
            int var16 = Math.min(var10, var12);
            var10 -= var16;
            var12 -= var16;
            var14 -= var16;
            if (var7 == 1) {
               --var14;
            }

            if (var14 < 0) {
               var10 -= var14;
               var12 -= var14;
               var14 = 0;
            }

            boolean var17 = false;
            int var23 = var7 + var10 + (var11 < N_5_BITS.length ? N_5_BITS[var11] : var11 * 3);
            int var24 = var12 + 1 + (var13 + 1 < N_5_BITS.length ? N_5_BITS[var13 + 1] : (var13 + 1) * 3);
            boolean var18;
            boolean var19;
            long var20;
            int var22;
            int var26;
            int var33;
            if (var23 < 64 && var24 < 64) {
               if (var23 < 32 && var24 < 32) {
                  int var35 = (int)var2 * FDBigInteger.SMALL_5_POW[var11] << var10;
                  var26 = FDBigInteger.SMALL_5_POW[var13] << var12;
                  int var37 = FDBigInteger.SMALL_5_POW[var11] << var14;
                  int var38 = var26 * 10;
                  var33 = 0;
                  var22 = var35 / var26;
                  var35 = 10 * (var35 % var26);
                  var37 *= 10;
                  var18 = var35 < var37;
                  var19 = var35 + var37 > var38;

                  assert var22 < 10 : var22;

                  if (var22 == 0 && !var19) {
                     --var9;
                  } else {
                     this.digits[var33++] = (char)(48 + var22);
                  }

                  if (!var5 || var9 < -3 || var9 >= 8) {
                     var18 = false;
                     var19 = false;
                  }

                  for(; !var18 && !var19; this.digits[var33++] = (char)(48 + var22)) {
                     var22 = var35 / var26;
                     var35 = 10 * (var35 % var26);
                     var37 *= 10;

                     assert var22 < 10 : var22;

                     if ((long)var37 > 0L) {
                        var18 = var35 < var37;
                        var19 = var35 + var37 > var38;
                     } else {
                        var18 = true;
                        var19 = true;
                     }
                  }

                  var20 = (long)((var35 << 1) - var38);
                  this.exactDecimalConversion = var35 == 0;
               } else {
                  long var34 = var2 * FDBigInteger.LONG_5_POW[var11] << var10;
                  long var36 = FDBigInteger.LONG_5_POW[var13] << var12;
                  long var39 = FDBigInteger.LONG_5_POW[var11] << var14;
                  long var31 = var36 * 10L;
                  var33 = 0;
                  var22 = (int)(var34 / var36);
                  var34 = 10L * (var34 % var36);
                  var39 *= 10L;
                  var18 = var34 < var39;
                  var19 = var34 + var39 > var31;

                  assert var22 < 10 : var22;

                  if (var22 == 0 && !var19) {
                     --var9;
                  } else {
                     this.digits[var33++] = (char)(48 + var22);
                  }

                  if (!var5 || var9 < -3 || var9 >= 8) {
                     var18 = false;
                     var19 = false;
                  }

                  for(; !var18 && !var19; this.digits[var33++] = (char)(48 + var22)) {
                     var22 = (int)(var34 / var36);
                     var34 = 10L * (var34 % var36);
                     var39 *= 10L;

                     assert var22 < 10 : var22;

                     if (var39 > 0L) {
                        var18 = var34 < var39;
                        var19 = var34 + var39 > var31;
                     } else {
                        var18 = true;
                        var19 = true;
                     }
                  }

                  var20 = (var34 << 1) - var31;
                  this.exactDecimalConversion = var34 == 0L;
               }
            } else {
               FDBigInteger var25 = FDBigInteger.valueOfPow52(var13, var12);
               var26 = var25.getNormalizationBias();
               var25 = var25.leftShift(var26);
               FDBigInteger var27 = FDBigInteger.valueOfMulPow52(var2, var11, var10 + var26);
               FDBigInteger var28 = FDBigInteger.valueOfPow52(var11 + 1, var14 + var26 + 1);
               FDBigInteger var29 = FDBigInteger.valueOfPow52(var13 + 1, var12 + var26 + 1);
               var33 = 0;
               var22 = var27.quoRemIteration(var25);
               var18 = var27.cmp(var28) < 0;
               var19 = var29.addAndCmp(var27, var28) <= 0;

               assert var22 < 10 : var22;

               if (var22 == 0 && !var19) {
                  --var9;
               } else {
                  this.digits[var33++] = (char)(48 + var22);
               }

               if (!var5 || var9 < -3 || var9 >= 8) {
                  var18 = false;
                  var19 = false;
               }

               while(!var18 && !var19) {
                  var22 = var27.quoRemIteration(var25);

                  assert var22 < 10 : var22;

                  var28 = var28.multBy10();
                  var18 = var27.cmp(var28) < 0;
                  var19 = var29.addAndCmp(var27, var28) <= 0;
                  this.digits[var33++] = (char)(48 + var22);
               }

               if (var19 && var18) {
                  var27 = var27.leftShift(1);
                  var20 = (long)var27.cmp(var29);
               } else {
                  var20 = 0L;
               }

               this.exactDecimalConversion = var27.cmp(FDBigInteger.ZERO) == 0;
            }

            this.decExponent = var9 + 1;
            this.firstDigitIndex = 0;
            this.nDigits = var33;
            if (var19) {
               if (var18) {
                  if (var20 == 0L) {
                     if ((this.digits[this.firstDigitIndex + this.nDigits - 1] & 1) != 0) {
                        this.roundup();
                     }
                  } else if (var20 > 0L) {
                     this.roundup();
                  }
               } else {
                  this.roundup();
               }
            }

         }
      }

      private void roundup() {
         int var1 = this.firstDigitIndex + this.nDigits - 1;
         char var2 = this.digits[var1];
         if (var2 == '9') {
            while(true) {
               if (var2 != '9' || var1 <= this.firstDigitIndex) {
                  if (var2 == '9') {
                     ++this.decExponent;
                     this.digits[this.firstDigitIndex] = '1';
                     return;
                  }
                  break;
               }

               this.digits[var1] = '0';
               --var1;
               var2 = this.digits[var1];
            }
         }

         this.digits[var1] = (char)(var2 + 1);
         this.decimalDigitsRoundedUp = true;
      }

      static int estimateDecExp(long var0, int var2) {
         double var3 = Double.longBitsToDouble(4607182418800017408L | var0 & 4503599627370495L);
         double var5 = (var3 - 1.5D) * 0.289529654D + 0.176091259D + (double)var2 * 0.301029995663981D;
         long var7 = Double.doubleToRawLongBits(var5);
         int var9 = (int)((var7 & 9218868437227405312L) >> 52) - 1023;
         boolean var10 = (var7 & Long.MIN_VALUE) != 0L;
         if (var9 >= 0 && var9 < 52) {
            long var11 = 4503599627370495L >> var9;
            int var13 = (int)((var7 & 4503599627370495L | 4503599627370496L) >> 52 - var9);
            return var10 ? ((var11 & var7) == 0L ? -var13 : -var13 - 1) : var13;
         } else if (var9 < 0) {
            return (var7 & Long.MAX_VALUE) == 0L ? 0 : (var10 ? -1 : 0);
         } else {
            return (int)var5;
         }
      }

      private static int insignificantDigits(int var0) {
         int var1;
         for(var1 = 0; (long)var0 >= 10L; ++var1) {
            var0 = (int)((long)var0 / 10L);
         }

         return var1;
      }

      private static int insignificantDigitsForPow2(int var0) {
         return var0 > 1 && var0 < insignificantDigitsNumber.length ? insignificantDigitsNumber[var0] : 0;
      }

      private int getChars(char[] var1) {
         assert this.nDigits <= 19 : this.nDigits;

         byte var2 = 0;
         if (this.isNegative) {
            var1[0] = '-';
            var2 = 1;
         }

         int var3;
         int var5;
         if (this.decExponent > 0 && this.decExponent < 8) {
            var3 = Math.min(this.nDigits, this.decExponent);
            System.arraycopy(this.digits, this.firstDigitIndex, var1, var2, var3);
            var5 = var2 + var3;
            if (var3 < this.decExponent) {
               var3 = this.decExponent - var3;
               Arrays.fill(var1, var5, var5 + var3, '0');
               var5 += var3;
               var1[var5++] = '.';
               var1[var5++] = '0';
            } else {
               var1[var5++] = '.';
               if (var3 < this.nDigits) {
                  int var4 = this.nDigits - var3;
                  System.arraycopy(this.digits, this.firstDigitIndex + var3, var1, var5, var4);
                  var5 += var4;
               } else {
                  var1[var5++] = '0';
               }
            }
         } else if (this.decExponent <= 0 && this.decExponent > -3) {
            var5 = var2 + 1;
            var1[var2] = '0';
            var1[var5++] = '.';
            if (this.decExponent != 0) {
               Arrays.fill(var1, var5, var5 - this.decExponent, '0');
               var5 -= this.decExponent;
            }

            System.arraycopy(this.digits, this.firstDigitIndex, var1, var5, this.nDigits);
            var5 += this.nDigits;
         } else {
            var5 = var2 + 1;
            var1[var2] = this.digits[this.firstDigitIndex];
            var1[var5++] = '.';
            if (this.nDigits > 1) {
               System.arraycopy(this.digits, this.firstDigitIndex + 1, var1, var5, this.nDigits - 1);
               var5 += this.nDigits - 1;
            } else {
               var1[var5++] = '0';
            }

            var1[var5++] = 'E';
            if (this.decExponent <= 0) {
               var1[var5++] = '-';
               var3 = -this.decExponent + 1;
            } else {
               var3 = this.decExponent - 1;
            }

            if (var3 <= 9) {
               var1[var5++] = (char)(var3 + 48);
            } else if (var3 <= 99) {
               var1[var5++] = (char)(var3 / 10 + 48);
               var1[var5++] = (char)(var3 % 10 + 48);
            } else {
               var1[var5++] = (char)(var3 / 100 + 48);
               var3 %= 100;
               var1[var5++] = (char)(var3 / 10 + 48);
               var1[var5++] = (char)(var3 % 10 + 48);
            }
         }

         return var5;
      }
   }

   private static class ExceptionalBinaryToASCIIBuffer implements FloatingDecimal.BinaryToASCIIConverter {
      private final String image;
      private boolean isNegative;

      public ExceptionalBinaryToASCIIBuffer(String var1, boolean var2) {
         this.image = var1;
         this.isNegative = var2;
      }

      public String toJavaFormatString() {
         return this.image;
      }

      public void appendTo(Appendable var1) {
         if (var1 instanceof StringBuilder) {
            ((StringBuilder)var1).append(this.image);
         } else if (var1 instanceof StringBuffer) {
            ((StringBuffer)var1).append(this.image);
         } else {
            assert false;
         }

      }

      public int getDecimalExponent() {
         throw new IllegalArgumentException("Exceptional value does not have an exponent");
      }

      public int getDigits(char[] var1) {
         throw new IllegalArgumentException("Exceptional value does not have digits");
      }

      public boolean isNegative() {
         return this.isNegative;
      }

      public boolean isExceptional() {
         return true;
      }

      public boolean digitsRoundedUp() {
         throw new IllegalArgumentException("Exceptional value is not rounded");
      }

      public boolean decimalDigitsExact() {
         throw new IllegalArgumentException("Exceptional value is not exact");
      }
   }

   public interface BinaryToASCIIConverter {
      String toJavaFormatString();

      void appendTo(Appendable var1);

      int getDecimalExponent();

      int getDigits(char[] var1);

      boolean isNegative();

      boolean isExceptional();

      boolean digitsRoundedUp();

      boolean decimalDigitsExact();
   }
}
