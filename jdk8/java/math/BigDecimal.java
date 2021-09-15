package java.math;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Arrays;
import sun.misc.Unsafe;

public class BigDecimal extends Number implements Comparable<BigDecimal> {
   private final BigInteger intVal;
   private final int scale;
   private transient int precision;
   private transient String stringCache;
   static final long INFLATED = Long.MIN_VALUE;
   private static final BigInteger INFLATED_BIGINT = BigInteger.valueOf(Long.MIN_VALUE);
   private final transient long intCompact;
   private static final int MAX_COMPACT_DIGITS = 18;
   private static final long serialVersionUID = 6108874887143696463L;
   private static final ThreadLocal<BigDecimal.StringBuilderHelper> threadLocalStringBuilderHelper = new ThreadLocal<BigDecimal.StringBuilderHelper>() {
      protected BigDecimal.StringBuilderHelper initialValue() {
         return new BigDecimal.StringBuilderHelper();
      }
   };
   private static final BigDecimal[] zeroThroughTen;
   private static final BigDecimal[] ZERO_SCALED_BY;
   private static final long HALF_LONG_MAX_VALUE = 4611686018427387903L;
   private static final long HALF_LONG_MIN_VALUE = -4611686018427387904L;
   public static final BigDecimal ZERO;
   public static final BigDecimal ONE;
   public static final BigDecimal TEN;
   public static final int ROUND_UP = 0;
   public static final int ROUND_DOWN = 1;
   public static final int ROUND_CEILING = 2;
   public static final int ROUND_FLOOR = 3;
   public static final int ROUND_HALF_UP = 4;
   public static final int ROUND_HALF_DOWN = 5;
   public static final int ROUND_HALF_EVEN = 6;
   public static final int ROUND_UNNECESSARY = 7;
   private static final double[] double10pow;
   private static final float[] float10pow;
   private static final long[] LONG_TEN_POWERS_TABLE;
   private static volatile BigInteger[] BIG_TEN_POWERS_TABLE;
   private static final int BIG_TEN_POWERS_TABLE_INITLEN;
   private static final int BIG_TEN_POWERS_TABLE_MAX;
   private static final long[] THRESHOLDS_TABLE;
   private static final long DIV_NUM_BASE = 4294967296L;
   private static final long[][] LONGLONG_TEN_POWERS_TABLE;

   BigDecimal(BigInteger var1, long var2, int var4, int var5) {
      this.scale = var4;
      this.precision = var5;
      this.intCompact = var2;
      this.intVal = var1;
   }

   public BigDecimal(char[] var1, int var2, int var3) {
      this(var1, var2, var3, MathContext.UNLIMITED);
   }

   public BigDecimal(char[] var1, int var2, int var3, MathContext var4) {
      if (var2 + var3 <= var1.length && var2 >= 0) {
         int var5 = 0;
         int var6 = 0;
         long var7 = 0L;
         BigInteger var9 = null;

         try {
            boolean var10 = false;
            if (var1[var2] == '-') {
               var10 = true;
               ++var2;
               --var3;
            } else if (var1[var2] == '+') {
               ++var2;
               --var3;
            }

            boolean var11 = false;
            long var12 = 0L;
            boolean var15 = var3 <= 18;
            int var16 = 0;
            char var14;
            int var18;
            if (!var15) {
               char[] var22;
               for(var22 = new char[var3]; var3 > 0; --var3) {
                  var14 = var1[var2];
                  if ((var14 < '0' || var14 > '9') && !Character.isDigit(var14)) {
                     if (var14 != '.') {
                        if (var14 != 'e' && var14 != 'E') {
                           throw new NumberFormatException();
                        }

                        var12 = parseExp(var1, var2, var3);
                        if ((long)((int)var12) != var12) {
                           throw new NumberFormatException();
                        }
                        break;
                     }

                     if (var11) {
                        throw new NumberFormatException();
                     }

                     var11 = true;
                  } else {
                     if (var14 != '0' && Character.digit((char)var14, 10) != 0) {
                        if (var5 != 1 || var16 != 0) {
                           ++var5;
                        }

                        var22[var16++] = var14;
                     } else if (var5 == 0) {
                        var22[var16] = var14;
                        var5 = 1;
                     } else if (var16 != 0) {
                        var22[var16++] = var14;
                        ++var5;
                     }

                     if (var11) {
                        ++var6;
                     }
                  }

                  ++var2;
               }

               if (var5 == 0) {
                  throw new NumberFormatException();
               }

               if (var12 != 0L) {
                  var6 = this.adjustScale(var6, var12);
               }

               var9 = new BigInteger(var22, var10 ? -1 : 1, var5);
               var7 = compactValFor(var9);
               var18 = var4.precision;
               if (var18 > 0 && var5 > var18) {
                  int var19;
                  if (var7 == Long.MIN_VALUE) {
                     for(var19 = var5 - var18; var19 > 0; var19 = var5 - var18) {
                        var6 = checkScaleNonZero((long)var6 - (long)var19);
                        var9 = divideAndRoundByTenPow(var9, var19, var4.roundingMode.oldMode);
                        var7 = compactValFor(var9);
                        if (var7 != Long.MIN_VALUE) {
                           var5 = longDigitLength(var7);
                           break;
                        }

                        var5 = bigDigitLength(var9);
                     }
                  }

                  if (var7 != Long.MIN_VALUE) {
                     for(var19 = var5 - var18; var19 > 0; var19 = var5 - var18) {
                        var6 = checkScaleNonZero((long)var6 - (long)var19);
                        var7 = divideAndRound(var7, LONG_TEN_POWERS_TABLE[var19], var4.roundingMode.oldMode);
                        var5 = longDigitLength(var7);
                     }

                     var9 = null;
                  }
               }
            } else {
               int var17;
               while(var3 > 0) {
                  var14 = var1[var2];
                  if (var14 == '0') {
                     if (var5 == 0) {
                        var5 = 1;
                     } else if (var7 != 0L) {
                        var7 *= 10L;
                        ++var5;
                     }

                     if (var11) {
                        ++var6;
                     }
                  } else if (var14 >= '1' && var14 <= '9') {
                     var17 = var14 - 48;
                     if (var5 != 1 || var7 != 0L) {
                        ++var5;
                     }

                     var7 = var7 * 10L + (long)var17;
                     if (var11) {
                        ++var6;
                     }
                  } else if (var14 == '.') {
                     if (var11) {
                        throw new NumberFormatException();
                     }

                     var11 = true;
                  } else {
                     if (!Character.isDigit(var14)) {
                        if (var14 != 'e' && var14 != 'E') {
                           throw new NumberFormatException();
                        }

                        var12 = parseExp(var1, var2, var3);
                        if ((long)((int)var12) != var12) {
                           throw new NumberFormatException();
                        }
                        break;
                     }

                     var17 = Character.digit((char)var14, 10);
                     if (var17 == 0) {
                        if (var5 == 0) {
                           var5 = 1;
                        } else if (var7 != 0L) {
                           var7 *= 10L;
                           ++var5;
                        }
                     } else {
                        if (var5 != 1 || var7 != 0L) {
                           ++var5;
                        }

                        var7 = var7 * 10L + (long)var17;
                     }

                     if (var11) {
                        ++var6;
                     }
                  }

                  ++var2;
                  --var3;
               }

               if (var5 == 0) {
                  throw new NumberFormatException();
               }

               if (var12 != 0L) {
                  var6 = this.adjustScale(var6, var12);
               }

               var7 = var10 ? -var7 : var7;
               var17 = var4.precision;
               var18 = var5 - var17;
               if (var17 > 0 && var18 > 0) {
                  while(var18 > 0) {
                     var6 = checkScaleNonZero((long)var6 - (long)var18);
                     var7 = divideAndRound(var7, LONG_TEN_POWERS_TABLE[var18], var4.roundingMode.oldMode);
                     var5 = longDigitLength(var7);
                     var18 = var5 - var17;
                  }
               }
            }
         } catch (ArrayIndexOutOfBoundsException var20) {
            throw new NumberFormatException();
         } catch (NegativeArraySizeException var21) {
            throw new NumberFormatException();
         }

         this.scale = var6;
         this.precision = var5;
         this.intCompact = var7;
         this.intVal = var9;
      } else {
         throw new NumberFormatException("Bad offset or len arguments for char[] input.");
      }
   }

   private int adjustScale(int var1, long var2) {
      long var4 = (long)var1 - var2;
      if (var4 <= 2147483647L && var4 >= -2147483648L) {
         var1 = (int)var4;
         return var1;
      } else {
         throw new NumberFormatException("Scale out of range.");
      }
   }

   private static long parseExp(char[] var0, int var1, int var2) {
      long var3 = 0L;
      ++var1;
      char var5 = var0[var1];
      --var2;
      boolean var6 = var5 == '-';
      if (var6 || var5 == '+') {
         ++var1;
         var5 = var0[var1];
         --var2;
      }

      if (var2 <= 0) {
         throw new NumberFormatException();
      } else {
         while(var2 > 10 && (var5 == '0' || Character.digit((char)var5, 10) == 0)) {
            ++var1;
            var5 = var0[var1];
            --var2;
         }

         if (var2 > 10) {
            throw new NumberFormatException();
         } else {
            while(true) {
               int var7;
               if (var5 >= '0' && var5 <= '9') {
                  var7 = var5 - 48;
               } else {
                  var7 = Character.digit((char)var5, 10);
                  if (var7 < 0) {
                     throw new NumberFormatException();
                  }
               }

               var3 = var3 * 10L + (long)var7;
               if (var2 == 1) {
                  if (var6) {
                     var3 = -var3;
                  }

                  return var3;
               }

               ++var1;
               var5 = var0[var1];
               --var2;
            }
         }
      }
   }

   public BigDecimal(char[] var1) {
      this(var1, 0, var1.length);
   }

   public BigDecimal(char[] var1, MathContext var2) {
      this(var1, 0, var1.length, var2);
   }

   public BigDecimal(String var1) {
      this(var1.toCharArray(), 0, var1.length());
   }

   public BigDecimal(String var1, MathContext var2) {
      this(var1.toCharArray(), 0, var1.length(), var2);
   }

   public BigDecimal(double var1) {
      this(var1, MathContext.UNLIMITED);
   }

   public BigDecimal(double var1, MathContext var3) {
      if (!Double.isInfinite(var1) && !Double.isNaN(var1)) {
         long var4 = Double.doubleToLongBits(var1);
         int var6 = var4 >> 63 == 0L ? 1 : -1;
         int var7 = (int)(var4 >> 52 & 2047L);
         long var8 = var7 == 0 ? (var4 & 4503599627370495L) << 1 : var4 & 4503599627370495L | 4503599627370496L;
         var7 -= 1075;
         if (var8 == 0L) {
            this.intVal = BigInteger.ZERO;
            this.scale = 0;
            this.intCompact = 0L;
            this.precision = 1;
         } else {
            while((var8 & 1L) == 0L) {
               var8 >>= 1;
               ++var7;
            }

            int var10 = 0;
            long var12 = (long)var6 * var8;
            BigInteger var11;
            if (var7 == 0) {
               var11 = var12 == Long.MIN_VALUE ? INFLATED_BIGINT : null;
            } else {
               if (var7 < 0) {
                  var11 = BigInteger.valueOf(5L).pow(-var7).multiply(var12);
                  var10 = -var7;
               } else {
                  var11 = BigInteger.valueOf(2L).pow(var7).multiply(var12);
               }

               var12 = compactValFor(var11);
            }

            int var14 = 0;
            int var15 = var3.precision;
            if (var15 > 0) {
               int var16 = var3.roundingMode.oldMode;
               int var17;
               if (var12 == Long.MIN_VALUE) {
                  var14 = bigDigitLength(var11);

                  for(var17 = var14 - var15; var17 > 0; var17 = var14 - var15) {
                     var10 = checkScaleNonZero((long)var10 - (long)var17);
                     var11 = divideAndRoundByTenPow(var11, var17, var16);
                     var12 = compactValFor(var11);
                     if (var12 != Long.MIN_VALUE) {
                        break;
                     }

                     var14 = bigDigitLength(var11);
                  }
               }

               if (var12 != Long.MIN_VALUE) {
                  var14 = longDigitLength(var12);

                  for(var17 = var14 - var15; var17 > 0; var17 = var14 - var15) {
                     var10 = checkScaleNonZero((long)var10 - (long)var17);
                     var12 = divideAndRound(var12, LONG_TEN_POWERS_TABLE[var17], var3.roundingMode.oldMode);
                     var14 = longDigitLength(var12);
                  }

                  var11 = null;
               }
            }

            this.intVal = var11;
            this.intCompact = var12;
            this.scale = var10;
            this.precision = var14;
         }
      } else {
         throw new NumberFormatException("Infinite or NaN");
      }
   }

   public BigDecimal(BigInteger var1) {
      this.scale = 0;
      this.intVal = var1;
      this.intCompact = compactValFor(var1);
   }

   public BigDecimal(BigInteger var1, MathContext var2) {
      this(var1, 0, var2);
   }

   public BigDecimal(BigInteger var1, int var2) {
      this.intVal = var1;
      this.intCompact = compactValFor(var1);
      this.scale = var2;
   }

   public BigDecimal(BigInteger var1, int var2, MathContext var3) {
      long var4 = compactValFor(var1);
      int var6 = var3.precision;
      int var7 = 0;
      if (var6 > 0) {
         int var8 = var3.roundingMode.oldMode;
         int var9;
         if (var4 == Long.MIN_VALUE) {
            var7 = bigDigitLength(var1);

            for(var9 = var7 - var6; var9 > 0; var9 = var7 - var6) {
               var2 = checkScaleNonZero((long)var2 - (long)var9);
               var1 = divideAndRoundByTenPow(var1, var9, var8);
               var4 = compactValFor(var1);
               if (var4 != Long.MIN_VALUE) {
                  break;
               }

               var7 = bigDigitLength(var1);
            }
         }

         if (var4 != Long.MIN_VALUE) {
            var7 = longDigitLength(var4);

            for(var9 = var7 - var6; var9 > 0; var9 = var7 - var6) {
               var2 = checkScaleNonZero((long)var2 - (long)var9);
               var4 = divideAndRound(var4, LONG_TEN_POWERS_TABLE[var9], var8);
               var7 = longDigitLength(var4);
            }

            var1 = null;
         }
      }

      this.intVal = var1;
      this.intCompact = var4;
      this.scale = var2;
      this.precision = var7;
   }

   public BigDecimal(int var1) {
      this.intCompact = (long)var1;
      this.scale = 0;
      this.intVal = null;
   }

   public BigDecimal(int var1, MathContext var2) {
      int var3 = var2.precision;
      long var4 = (long)var1;
      int var6 = 0;
      int var7 = 0;
      if (var3 > 0) {
         var7 = longDigitLength(var4);

         for(int var8 = var7 - var3; var8 > 0; var8 = var7 - var3) {
            var6 = checkScaleNonZero((long)var6 - (long)var8);
            var4 = divideAndRound(var4, LONG_TEN_POWERS_TABLE[var8], var2.roundingMode.oldMode);
            var7 = longDigitLength(var4);
         }
      }

      this.intVal = null;
      this.intCompact = var4;
      this.scale = var6;
      this.precision = var7;
   }

   public BigDecimal(long var1) {
      this.intCompact = var1;
      this.intVal = var1 == Long.MIN_VALUE ? INFLATED_BIGINT : null;
      this.scale = 0;
   }

   public BigDecimal(long var1, MathContext var3) {
      int var4 = var3.precision;
      int var5 = var3.roundingMode.oldMode;
      int var6 = 0;
      int var7 = 0;
      BigInteger var8 = var1 == Long.MIN_VALUE ? INFLATED_BIGINT : null;
      if (var4 > 0) {
         int var9;
         if (var1 == Long.MIN_VALUE) {
            var6 = 19;

            for(var9 = var6 - var4; var9 > 0; var9 = var6 - var4) {
               var7 = checkScaleNonZero((long)var7 - (long)var9);
               var8 = divideAndRoundByTenPow(var8, var9, var5);
               var1 = compactValFor(var8);
               if (var1 != Long.MIN_VALUE) {
                  break;
               }

               var6 = bigDigitLength(var8);
            }
         }

         if (var1 != Long.MIN_VALUE) {
            var6 = longDigitLength(var1);

            for(var9 = var6 - var4; var9 > 0; var9 = var6 - var4) {
               var7 = checkScaleNonZero((long)var7 - (long)var9);
               var1 = divideAndRound(var1, LONG_TEN_POWERS_TABLE[var9], var3.roundingMode.oldMode);
               var6 = longDigitLength(var1);
            }

            var8 = null;
         }
      }

      this.intVal = var8;
      this.intCompact = var1;
      this.scale = var7;
      this.precision = var6;
   }

   public static BigDecimal valueOf(long var0, int var2) {
      if (var2 == 0) {
         return valueOf(var0);
      } else {
         return var0 == 0L ? zeroValueOf(var2) : new BigDecimal(var0 == Long.MIN_VALUE ? INFLATED_BIGINT : null, var0, var2, 0);
      }
   }

   public static BigDecimal valueOf(long var0) {
      if (var0 >= 0L && var0 < (long)zeroThroughTen.length) {
         return zeroThroughTen[(int)var0];
      } else {
         return var0 != Long.MIN_VALUE ? new BigDecimal((BigInteger)null, var0, 0, 0) : new BigDecimal(INFLATED_BIGINT, var0, 0, 0);
      }
   }

   static BigDecimal valueOf(long var0, int var2, int var3) {
      if (var2 == 0 && var0 >= 0L && var0 < (long)zeroThroughTen.length) {
         return zeroThroughTen[(int)var0];
      } else {
         return var0 == 0L ? zeroValueOf(var2) : new BigDecimal(var0 == Long.MIN_VALUE ? INFLATED_BIGINT : null, var0, var2, var3);
      }
   }

   static BigDecimal valueOf(BigInteger var0, int var1, int var2) {
      long var3 = compactValFor(var0);
      if (var3 == 0L) {
         return zeroValueOf(var1);
      } else {
         return var1 == 0 && var3 >= 0L && var3 < (long)zeroThroughTen.length ? zeroThroughTen[(int)var3] : new BigDecimal(var0, var3, var1, var2);
      }
   }

   static BigDecimal zeroValueOf(int var0) {
      return var0 >= 0 && var0 < ZERO_SCALED_BY.length ? ZERO_SCALED_BY[var0] : new BigDecimal(BigInteger.ZERO, 0L, var0, 1);
   }

   public static BigDecimal valueOf(double var0) {
      return new BigDecimal(Double.toString(var0));
   }

   public BigDecimal add(BigDecimal var1) {
      if (this.intCompact != Long.MIN_VALUE) {
         return var1.intCompact != Long.MIN_VALUE ? add(this.intCompact, this.scale, var1.intCompact, var1.scale) : add(this.intCompact, this.scale, var1.intVal, var1.scale);
      } else {
         return var1.intCompact != Long.MIN_VALUE ? add(var1.intCompact, var1.scale, this.intVal, this.scale) : add(this.intVal, this.scale, var1.intVal, var1.scale);
      }
   }

   public BigDecimal add(BigDecimal var1, MathContext var2) {
      if (var2.precision == 0) {
         return this.add(var1);
      } else {
         BigDecimal var3 = this;
         boolean var4 = this.signum() == 0;
         boolean var5 = var1.signum() == 0;
         if (!var4 && !var5) {
            long var10 = (long)this.scale - (long)var1.scale;
            if (var10 != 0L) {
               BigDecimal[] var11 = this.preAlign(this, var1, var10, var2);
               matchScale(var11);
               var3 = var11[0];
               var1 = var11[1];
            }

            return doRound(var3.inflated().add(var1.inflated()), var3.scale, var2);
         } else {
            int var6 = Math.max(this.scale(), var1.scale());
            if (var4 && var5) {
               return zeroValueOf(var6);
            } else {
               BigDecimal var7 = var4 ? doRound(var1, var2) : doRound(this, var2);
               if (var7.scale() == var6) {
                  return var7;
               } else if (var7.scale() > var6) {
                  return stripZerosToMatchScale(var7.intVal, var7.intCompact, var7.scale, var6);
               } else {
                  int var8 = var2.precision - var7.precision();
                  int var9 = var6 - var7.scale();
                  return var8 >= var9 ? var7.setScale(var6) : var7.setScale(var7.scale() + var8);
               }
            }
         }
      }
   }

   private BigDecimal[] preAlign(BigDecimal var1, BigDecimal var2, long var3, MathContext var5) {
      assert var3 != 0L;

      BigDecimal var6;
      BigDecimal var7;
      if (var3 < 0L) {
         var6 = var1;
         var7 = var2;
      } else {
         var6 = var2;
         var7 = var1;
      }

      long var8 = (long)var6.scale - (long)var6.precision() + (long)var5.precision;
      long var10 = (long)var7.scale - (long)var7.precision() + 1L;
      if (var10 > (long)(var6.scale + 2) && var10 > var8 + 2L) {
         var7 = valueOf((long)var7.signum(), this.checkScale(Math.max((long)var6.scale, var8) + 3L));
      }

      BigDecimal[] var12 = new BigDecimal[]{var6, var7};
      return var12;
   }

   public BigDecimal subtract(BigDecimal var1) {
      if (this.intCompact != Long.MIN_VALUE) {
         return var1.intCompact != Long.MIN_VALUE ? add(this.intCompact, this.scale, -var1.intCompact, var1.scale) : add(this.intCompact, this.scale, var1.intVal.negate(), var1.scale);
      } else {
         return var1.intCompact != Long.MIN_VALUE ? add(-var1.intCompact, var1.scale, this.intVal, this.scale) : add(this.intVal, this.scale, var1.intVal.negate(), var1.scale);
      }
   }

   public BigDecimal subtract(BigDecimal var1, MathContext var2) {
      return var2.precision == 0 ? this.subtract(var1) : this.add(var1.negate(), var2);
   }

   public BigDecimal multiply(BigDecimal var1) {
      int var2 = this.checkScale((long)this.scale + (long)var1.scale);
      if (this.intCompact != Long.MIN_VALUE) {
         return var1.intCompact != Long.MIN_VALUE ? multiply(this.intCompact, var1.intCompact, var2) : multiply(this.intCompact, var1.intVal, var2);
      } else {
         return var1.intCompact != Long.MIN_VALUE ? multiply(var1.intCompact, this.intVal, var2) : multiply(this.intVal, var1.intVal, var2);
      }
   }

   public BigDecimal multiply(BigDecimal var1, MathContext var2) {
      if (var2.precision == 0) {
         return this.multiply(var1);
      } else {
         int var3 = this.checkScale((long)this.scale + (long)var1.scale);
         if (this.intCompact != Long.MIN_VALUE) {
            return var1.intCompact != Long.MIN_VALUE ? multiplyAndRound(this.intCompact, var1.intCompact, var3, var2) : multiplyAndRound(this.intCompact, var1.intVal, var3, var2);
         } else {
            return var1.intCompact != Long.MIN_VALUE ? multiplyAndRound(var1.intCompact, this.intVal, var3, var2) : multiplyAndRound(this.intVal, var1.intVal, var3, var2);
         }
      }
   }

   public BigDecimal divide(BigDecimal var1, int var2, int var3) {
      if (var3 >= 0 && var3 <= 7) {
         if (this.intCompact != Long.MIN_VALUE) {
            return var1.intCompact != Long.MIN_VALUE ? divide(this.intCompact, this.scale, var1.intCompact, var1.scale, var2, var3) : divide(this.intCompact, this.scale, var1.intVal, var1.scale, var2, var3);
         } else {
            return var1.intCompact != Long.MIN_VALUE ? divide(this.intVal, this.scale, var1.intCompact, var1.scale, var2, var3) : divide(this.intVal, this.scale, var1.intVal, var1.scale, var2, var3);
         }
      } else {
         throw new IllegalArgumentException("Invalid rounding mode");
      }
   }

   public BigDecimal divide(BigDecimal var1, int var2, RoundingMode var3) {
      return this.divide(var1, var2, var3.oldMode);
   }

   public BigDecimal divide(BigDecimal var1, int var2) {
      return this.divide(var1, this.scale, var2);
   }

   public BigDecimal divide(BigDecimal var1, RoundingMode var2) {
      return this.divide(var1, this.scale, var2.oldMode);
   }

   public BigDecimal divide(BigDecimal var1) {
      if (var1.signum() == 0) {
         if (this.signum() == 0) {
            throw new ArithmeticException("Division undefined");
         } else {
            throw new ArithmeticException("Division by zero");
         }
      } else {
         int var2 = saturateLong((long)this.scale - (long)var1.scale);
         if (this.signum() == 0) {
            return zeroValueOf(var2);
         } else {
            MathContext var3 = new MathContext((int)Math.min((long)this.precision() + (long)Math.ceil(10.0D * (double)var1.precision() / 3.0D), 2147483647L), RoundingMode.UNNECESSARY);

            BigDecimal var4;
            try {
               var4 = this.divide(var1, var3);
            } catch (ArithmeticException var6) {
               throw new ArithmeticException("Non-terminating decimal expansion; no exact representable decimal result.");
            }

            int var5 = var4.scale();
            return var2 > var5 ? var4.setScale(var2, 7) : var4;
         }
      }
   }

   public BigDecimal divide(BigDecimal var1, MathContext var2) {
      int var3 = var2.precision;
      if (var3 == 0) {
         return this.divide(var1);
      } else {
         long var5 = (long)this.scale - (long)var1.scale;
         if (var1.signum() == 0) {
            if (this.signum() == 0) {
               throw new ArithmeticException("Division undefined");
            } else {
               throw new ArithmeticException("Division by zero");
            }
         } else if (this.signum() == 0) {
            return zeroValueOf(saturateLong(var5));
         } else {
            int var7 = this.precision();
            int var8 = var1.precision();
            if (this.intCompact != Long.MIN_VALUE) {
               return var1.intCompact != Long.MIN_VALUE ? divide(this.intCompact, var7, var1.intCompact, var8, var5, var2) : divide(this.intCompact, var7, var1.intVal, var8, var5, var2);
            } else {
               return var1.intCompact != Long.MIN_VALUE ? divide(this.intVal, var7, var1.intCompact, var8, var5, var2) : divide(this.intVal, var7, var1.intVal, var8, var5, var2);
            }
         }
      }
   }

   public BigDecimal divideToIntegralValue(BigDecimal var1) {
      int var2 = saturateLong((long)this.scale - (long)var1.scale);
      if (this.compareMagnitude(var1) < 0) {
         return zeroValueOf(var2);
      } else if (this.signum() == 0 && var1.signum() != 0) {
         return this.setScale(var2, 7);
      } else {
         int var3 = (int)Math.min((long)this.precision() + (long)Math.ceil(10.0D * (double)var1.precision() / 3.0D) + Math.abs((long)this.scale() - (long)var1.scale()) + 2L, 2147483647L);
         BigDecimal var4 = this.divide(var1, new MathContext(var3, RoundingMode.DOWN));
         if (var4.scale > 0) {
            var4 = var4.setScale(0, RoundingMode.DOWN);
            var4 = stripZerosToMatchScale(var4.intVal, var4.intCompact, var4.scale, var2);
         }

         if (var4.scale < var2) {
            var4 = var4.setScale(var2, 7);
         }

         return var4;
      }
   }

   public BigDecimal divideToIntegralValue(BigDecimal var1, MathContext var2) {
      if (var2.precision != 0 && this.compareMagnitude(var1) >= 0) {
         int var3 = saturateLong((long)this.scale - (long)var1.scale);
         BigDecimal var4 = this.divide(var1, new MathContext(var2.precision, RoundingMode.DOWN));
         if (var4.scale() < 0) {
            BigDecimal var5 = var4.multiply(var1);
            if (this.subtract(var5).compareMagnitude(var1) >= 0) {
               throw new ArithmeticException("Division impossible");
            }
         } else if (var4.scale() > 0) {
            var4 = var4.setScale(0, RoundingMode.DOWN);
         }

         int var6;
         return var3 > var4.scale() && (var6 = var2.precision - var4.precision()) > 0 ? var4.setScale(var4.scale() + Math.min(var6, var3 - var4.scale)) : stripZerosToMatchScale(var4.intVal, var4.intCompact, var4.scale, var3);
      } else {
         return this.divideToIntegralValue(var1);
      }
   }

   public BigDecimal remainder(BigDecimal var1) {
      BigDecimal[] var2 = this.divideAndRemainder(var1);
      return var2[1];
   }

   public BigDecimal remainder(BigDecimal var1, MathContext var2) {
      BigDecimal[] var3 = this.divideAndRemainder(var1, var2);
      return var3[1];
   }

   public BigDecimal[] divideAndRemainder(BigDecimal var1) {
      BigDecimal[] var2 = new BigDecimal[]{this.divideToIntegralValue(var1), null};
      var2[1] = this.subtract(var2[0].multiply(var1));
      return var2;
   }

   public BigDecimal[] divideAndRemainder(BigDecimal var1, MathContext var2) {
      if (var2.precision == 0) {
         return this.divideAndRemainder(var1);
      } else {
         BigDecimal[] var3 = new BigDecimal[]{this.divideToIntegralValue(var1, var2), null};
         var3[1] = this.subtract(var3[0].multiply(var1));
         return var3;
      }
   }

   public BigDecimal pow(int var1) {
      if (var1 >= 0 && var1 <= 999999999) {
         int var2 = this.checkScale((long)this.scale * (long)var1);
         return new BigDecimal(this.inflated().pow(var1), var2);
      } else {
         throw new ArithmeticException("Invalid operation");
      }
   }

   public BigDecimal pow(int var1, MathContext var2) {
      if (var2.precision == 0) {
         return this.pow(var1);
      } else if (var1 >= -999999999 && var1 <= 999999999) {
         if (var1 == 0) {
            return ONE;
         } else {
            BigDecimal var3 = this;
            MathContext var4 = var2;
            int var5 = Math.abs(var1);
            if (var2.precision > 0) {
               int var6 = longDigitLength((long)var5);
               if (var6 > var2.precision) {
                  throw new ArithmeticException("Invalid operation");
               }

               var4 = new MathContext(var2.precision + var6 + 1, var2.roundingMode);
            }

            BigDecimal var9 = ONE;
            boolean var7 = false;
            int var8 = 1;

            while(true) {
               var5 += var5;
               if (var5 < 0) {
                  var7 = true;
                  var9 = var9.multiply(var3, var4);
               }

               if (var8 == 31) {
                  if (var1 < 0) {
                     var9 = ONE.divide(var9, var4);
                  }

                  return doRound(var9, var2);
               }

               if (var7) {
                  var9 = var9.multiply(var9, var4);
               }

               ++var8;
            }
         }
      } else {
         throw new ArithmeticException("Invalid operation");
      }
   }

   public BigDecimal abs() {
      return this.signum() < 0 ? this.negate() : this;
   }

   public BigDecimal abs(MathContext var1) {
      return this.signum() < 0 ? this.negate(var1) : this.plus(var1);
   }

   public BigDecimal negate() {
      return this.intCompact == Long.MIN_VALUE ? new BigDecimal(this.intVal.negate(), Long.MIN_VALUE, this.scale, this.precision) : valueOf(-this.intCompact, this.scale, this.precision);
   }

   public BigDecimal negate(MathContext var1) {
      return this.negate().plus(var1);
   }

   public BigDecimal plus() {
      return this;
   }

   public BigDecimal plus(MathContext var1) {
      return var1.precision == 0 ? this : doRound(this, var1);
   }

   public int signum() {
      return this.intCompact != Long.MIN_VALUE ? Long.signum(this.intCompact) : this.intVal.signum();
   }

   public int scale() {
      return this.scale;
   }

   public int precision() {
      int var1 = this.precision;
      if (var1 == 0) {
         long var2 = this.intCompact;
         if (var2 != Long.MIN_VALUE) {
            var1 = longDigitLength(var2);
         } else {
            var1 = bigDigitLength(this.intVal);
         }

         this.precision = var1;
      }

      return var1;
   }

   public BigInteger unscaledValue() {
      return this.inflated();
   }

   public BigDecimal round(MathContext var1) {
      return this.plus(var1);
   }

   public BigDecimal setScale(int var1, RoundingMode var2) {
      return this.setScale(var1, var2.oldMode);
   }

   public BigDecimal setScale(int var1, int var2) {
      if (var2 >= 0 && var2 <= 7) {
         int var3 = this.scale;
         if (var1 == var3) {
            return this;
         } else if (this.signum() == 0) {
            return zeroValueOf(var1);
         } else if (this.intCompact != Long.MIN_VALUE) {
            long var8 = this.intCompact;
            int var6;
            if (var1 > var3) {
               var6 = this.checkScale((long)var1 - (long)var3);
               if ((var8 = longMultiplyPowerTen(var8, var6)) != Long.MIN_VALUE) {
                  return valueOf(var8, var1);
               } else {
                  BigInteger var7 = this.bigMultiplyPowerTen(var6);
                  return new BigDecimal(var7, Long.MIN_VALUE, var1, this.precision > 0 ? this.precision + var6 : 0);
               }
            } else {
               var6 = this.checkScale((long)var3 - (long)var1);
               return var6 < LONG_TEN_POWERS_TABLE.length ? divideAndRound(var8, LONG_TEN_POWERS_TABLE[var6], var1, var2, var1) : divideAndRound(this.inflated(), bigTenToThe(var6), var1, var2, var1);
            }
         } else {
            int var4;
            if (var1 > var3) {
               var4 = this.checkScale((long)var1 - (long)var3);
               BigInteger var5 = bigMultiplyPowerTen(this.intVal, var4);
               return new BigDecimal(var5, Long.MIN_VALUE, var1, this.precision > 0 ? this.precision + var4 : 0);
            } else {
               var4 = this.checkScale((long)var3 - (long)var1);
               return var4 < LONG_TEN_POWERS_TABLE.length ? divideAndRound(this.intVal, LONG_TEN_POWERS_TABLE[var4], var1, var2, var1) : divideAndRound(this.intVal, bigTenToThe(var4), var1, var2, var1);
            }
         }
      } else {
         throw new IllegalArgumentException("Invalid rounding mode");
      }
   }

   public BigDecimal setScale(int var1) {
      return this.setScale(var1, 7);
   }

   public BigDecimal movePointLeft(int var1) {
      int var2 = this.checkScale((long)this.scale + (long)var1);
      BigDecimal var3 = new BigDecimal(this.intVal, this.intCompact, var2, 0);
      return var3.scale < 0 ? var3.setScale(0, 7) : var3;
   }

   public BigDecimal movePointRight(int var1) {
      int var2 = this.checkScale((long)this.scale - (long)var1);
      BigDecimal var3 = new BigDecimal(this.intVal, this.intCompact, var2, 0);
      return var3.scale < 0 ? var3.setScale(0, 7) : var3;
   }

   public BigDecimal scaleByPowerOfTen(int var1) {
      return new BigDecimal(this.intVal, this.intCompact, this.checkScale((long)this.scale - (long)var1), this.precision);
   }

   public BigDecimal stripTrailingZeros() {
      if (this.intCompact != 0L && (this.intVal == null || this.intVal.signum() != 0)) {
         return this.intCompact != Long.MIN_VALUE ? createAndStripZerosToMatchScale(this.intCompact, this.scale, Long.MIN_VALUE) : createAndStripZerosToMatchScale(this.intVal, this.scale, Long.MIN_VALUE);
      } else {
         return ZERO;
      }
   }

   public int compareTo(BigDecimal var1) {
      if (this.scale == var1.scale) {
         long var2 = this.intCompact;
         long var4 = var1.intCompact;
         if (var2 != Long.MIN_VALUE && var4 != Long.MIN_VALUE) {
            return var2 != var4 ? (var2 > var4 ? 1 : -1) : 0;
         }
      }

      int var6 = this.signum();
      int var3 = var1.signum();
      if (var6 != var3) {
         return var6 > var3 ? 1 : -1;
      } else if (var6 == 0) {
         return 0;
      } else {
         int var7 = this.compareMagnitude(var1);
         return var6 > 0 ? var7 : -var7;
      }
   }

   private int compareMagnitude(BigDecimal var1) {
      long var2 = var1.intCompact;
      long var4 = this.intCompact;
      if (var4 == 0L) {
         return var2 == 0L ? 0 : -1;
      } else if (var2 == 0L) {
         return 1;
      } else {
         long var6 = (long)this.scale - (long)var1.scale;
         if (var6 != 0L) {
            long var8 = (long)this.precision() - (long)this.scale;
            long var10 = (long)var1.precision() - (long)var1.scale;
            if (var8 < var10) {
               return -1;
            }

            if (var8 > var10) {
               return 1;
            }

            BigInteger var12 = null;
            if (var6 < 0L) {
               if (var6 > -2147483648L && (var4 == Long.MIN_VALUE || (var4 = longMultiplyPowerTen(var4, (int)(-var6))) == Long.MIN_VALUE) && var2 == Long.MIN_VALUE) {
                  var12 = this.bigMultiplyPowerTen((int)(-var6));
                  return var12.compareMagnitude(var1.intVal);
               }
            } else if (var6 <= 2147483647L && (var2 == Long.MIN_VALUE || (var2 = longMultiplyPowerTen(var2, (int)var6)) == Long.MIN_VALUE) && var4 == Long.MIN_VALUE) {
               var12 = var1.bigMultiplyPowerTen((int)var6);
               return this.intVal.compareMagnitude(var12);
            }
         }

         if (var4 != Long.MIN_VALUE) {
            return var2 != Long.MIN_VALUE ? longCompareMagnitude(var4, var2) : -1;
         } else {
            return var2 != Long.MIN_VALUE ? 1 : this.intVal.compareMagnitude(var1.intVal);
         }
      }
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof BigDecimal)) {
         return false;
      } else {
         BigDecimal var2 = (BigDecimal)var1;
         if (var1 == this) {
            return true;
         } else if (this.scale != var2.scale) {
            return false;
         } else {
            long var3 = this.intCompact;
            long var5 = var2.intCompact;
            if (var3 != Long.MIN_VALUE) {
               if (var5 == Long.MIN_VALUE) {
                  var5 = compactValFor(var2.intVal);
               }

               return var5 == var3;
            } else if (var5 != Long.MIN_VALUE) {
               return var5 == compactValFor(this.intVal);
            } else {
               return this.inflated().equals(var2.inflated());
            }
         }
      }
   }

   public BigDecimal min(BigDecimal var1) {
      return this.compareTo(var1) <= 0 ? this : var1;
   }

   public BigDecimal max(BigDecimal var1) {
      return this.compareTo(var1) >= 0 ? this : var1;
   }

   public int hashCode() {
      if (this.intCompact != Long.MIN_VALUE) {
         long var1 = this.intCompact < 0L ? -this.intCompact : this.intCompact;
         int var3 = (int)((long)((int)(var1 >>> 32) * 31) + (var1 & 4294967295L));
         return 31 * (this.intCompact < 0L ? -var3 : var3) + this.scale;
      } else {
         return 31 * this.intVal.hashCode() + this.scale;
      }
   }

   public String toString() {
      String var1 = this.stringCache;
      if (var1 == null) {
         this.stringCache = var1 = this.layoutChars(true);
      }

      return var1;
   }

   public String toEngineeringString() {
      return this.layoutChars(false);
   }

   public String toPlainString() {
      if (this.scale == 0) {
         return this.intCompact != Long.MIN_VALUE ? Long.toString(this.intCompact) : this.intVal.toString();
      } else if (this.scale >= 0) {
         String var4;
         if (this.intCompact != Long.MIN_VALUE) {
            var4 = Long.toString(Math.abs(this.intCompact));
         } else {
            var4 = this.intVal.abs().toString();
         }

         return this.getValueString(this.signum(), var4, this.scale);
      } else if (this.signum() == 0) {
         return "0";
      } else {
         int var1 = checkScaleNonZero(-((long)this.scale));
         StringBuilder var2;
         if (this.intCompact != Long.MIN_VALUE) {
            var2 = new StringBuilder(20 + var1);
            var2.append(this.intCompact);
         } else {
            String var3 = this.intVal.toString();
            var2 = new StringBuilder(var3.length() + var1);
            var2.append(var3);
         }

         for(int var5 = 0; var5 < var1; ++var5) {
            var2.append('0');
         }

         return var2.toString();
      }
   }

   private String getValueString(int var1, String var2, int var3) {
      int var5 = var2.length() - var3;
      if (var5 == 0) {
         return (var1 < 0 ? "-0." : "0.") + var2;
      } else {
         StringBuilder var4;
         if (var5 > 0) {
            var4 = new StringBuilder(var2);
            var4.insert(var5, '.');
            if (var1 < 0) {
               var4.insert(0, (char)'-');
            }
         } else {
            var4 = new StringBuilder(3 - var5 + var2.length());
            var4.append(var1 < 0 ? "-0." : "0.");

            for(int var6 = 0; var6 < -var5; ++var6) {
               var4.append('0');
            }

            var4.append(var2);
         }

         return var4.toString();
      }
   }

   public BigInteger toBigInteger() {
      return this.setScale(0, 1).inflated();
   }

   public BigInteger toBigIntegerExact() {
      return this.setScale(0, 7).inflated();
   }

   public long longValue() {
      return this.intCompact != Long.MIN_VALUE && this.scale == 0 ? this.intCompact : this.toBigInteger().longValue();
   }

   public long longValueExact() {
      if (this.intCompact != Long.MIN_VALUE && this.scale == 0) {
         return this.intCompact;
      } else if (this.precision() - this.scale > 19) {
         throw new ArithmeticException("Overflow");
      } else if (this.signum() == 0) {
         return 0L;
      } else if (this.precision() - this.scale <= 0) {
         throw new ArithmeticException("Rounding necessary");
      } else {
         BigDecimal var1 = this.setScale(0, 7);
         if (var1.precision() >= 19) {
            BigDecimal.LongOverflow.check(var1);
         }

         return var1.inflated().longValue();
      }
   }

   public int intValue() {
      return this.intCompact != Long.MIN_VALUE && this.scale == 0 ? (int)this.intCompact : this.toBigInteger().intValue();
   }

   public int intValueExact() {
      long var1 = this.longValueExact();
      if ((long)((int)var1) != var1) {
         throw new ArithmeticException("Overflow");
      } else {
         return (int)var1;
      }
   }

   public short shortValueExact() {
      long var1 = this.longValueExact();
      if ((long)((short)((int)var1)) != var1) {
         throw new ArithmeticException("Overflow");
      } else {
         return (short)((int)var1);
      }
   }

   public byte byteValueExact() {
      long var1 = this.longValueExact();
      if ((long)((byte)((int)var1)) != var1) {
         throw new ArithmeticException("Overflow");
      } else {
         return (byte)((int)var1);
      }
   }

   public float floatValue() {
      if (this.intCompact != Long.MIN_VALUE) {
         if (this.scale == 0) {
            return (float)this.intCompact;
         }

         if (Math.abs(this.intCompact) < 4194304L) {
            if (this.scale > 0 && this.scale < float10pow.length) {
               return (float)this.intCompact / float10pow[this.scale];
            }

            if (this.scale < 0 && this.scale > -float10pow.length) {
               return (float)this.intCompact * float10pow[-this.scale];
            }
         }
      }

      return Float.parseFloat(this.toString());
   }

   public double doubleValue() {
      if (this.intCompact != Long.MIN_VALUE) {
         if (this.scale == 0) {
            return (double)this.intCompact;
         }

         if (Math.abs(this.intCompact) < 4503599627370496L) {
            if (this.scale > 0 && this.scale < double10pow.length) {
               return (double)this.intCompact / double10pow[this.scale];
            }

            if (this.scale < 0 && this.scale > -double10pow.length) {
               return (double)this.intCompact * double10pow[-this.scale];
            }
         }
      }

      return Double.parseDouble(this.toString());
   }

   public BigDecimal ulp() {
      return valueOf(1L, this.scale(), 1);
   }

   private String layoutChars(boolean var1) {
      if (this.scale == 0) {
         return this.intCompact != Long.MIN_VALUE ? Long.toString(this.intCompact) : this.intVal.toString();
      } else if (this.scale == 2 && this.intCompact >= 0L && this.intCompact < 2147483647L) {
         int var11 = (int)this.intCompact % 100;
         int var12 = (int)this.intCompact / 100;
         return Integer.toString(var12) + '.' + BigDecimal.StringBuilderHelper.DIGIT_TENS[var11] + BigDecimal.StringBuilderHelper.DIGIT_ONES[var11];
      } else {
         BigDecimal.StringBuilderHelper var2 = (BigDecimal.StringBuilderHelper)threadLocalStringBuilderHelper.get();
         char[] var3;
         int var4;
         if (this.intCompact != Long.MIN_VALUE) {
            var4 = var2.putIntCompact(Math.abs(this.intCompact));
            var3 = var2.getCompactCharArray();
         } else {
            var4 = 0;
            var3 = this.intVal.abs().toString().toCharArray();
         }

         StringBuilder var5 = var2.getStringBuilder();
         if (this.signum() < 0) {
            var5.append('-');
         }

         int var6 = var3.length - var4;
         long var7 = -((long)this.scale) + (long)(var6 - 1);
         int var9;
         if (this.scale >= 0 && var7 >= -6L) {
            var9 = this.scale - var6;
            if (var9 >= 0) {
               var5.append('0');
               var5.append('.');

               while(var9 > 0) {
                  var5.append('0');
                  --var9;
               }

               var5.append(var3, var4, var6);
            } else {
               var5.append(var3, var4, -var9);
               var5.append('.');
               var5.append(var3, -var9 + var4, this.scale);
            }
         } else {
            if (var1) {
               var5.append(var3[var4]);
               if (var6 > 1) {
                  var5.append('.');
                  var5.append(var3, var4 + 1, var6 - 1);
               }
            } else {
               var9 = (int)(var7 % 3L);
               if (var9 < 0) {
                  var9 += 3;
               }

               var7 -= (long)var9;
               ++var9;
               if (this.signum() == 0) {
                  switch(var9) {
                  case 1:
                     var5.append('0');
                     break;
                  case 2:
                     var5.append("0.00");
                     var7 += 3L;
                     break;
                  case 3:
                     var5.append("0.0");
                     var7 += 3L;
                     break;
                  default:
                     throw new AssertionError("Unexpected sig value " + var9);
                  }
               } else if (var9 >= var6) {
                  var5.append(var3, var4, var6);

                  for(int var10 = var9 - var6; var10 > 0; --var10) {
                     var5.append('0');
                  }
               } else {
                  var5.append(var3, var4, var9);
                  var5.append('.');
                  var5.append(var3, var4 + var9, var6 - var9);
               }
            }

            if (var7 != 0L) {
               var5.append('E');
               if (var7 > 0L) {
                  var5.append('+');
               }

               var5.append(var7);
            }
         }

         return var5.toString();
      }
   }

   private static BigInteger bigTenToThe(int var0) {
      if (var0 < 0) {
         return BigInteger.ZERO;
      } else if (var0 < BIG_TEN_POWERS_TABLE_MAX) {
         BigInteger[] var1 = BIG_TEN_POWERS_TABLE;
         return var0 < var1.length ? var1[var0] : expandBigIntegerTenPowers(var0);
      } else {
         return BigInteger.TEN.pow(var0);
      }
   }

   private static BigInteger expandBigIntegerTenPowers(int var0) {
      Class var1 = BigDecimal.class;
      synchronized(BigDecimal.class) {
         BigInteger[] var2 = BIG_TEN_POWERS_TABLE;
         int var3 = var2.length;
         if (var3 <= var0) {
            int var4;
            for(var4 = var3 << 1; var4 <= var0; var4 <<= 1) {
            }

            var2 = (BigInteger[])Arrays.copyOf((Object[])var2, var4);

            for(int var5 = var3; var5 < var4; ++var5) {
               var2[var5] = var2[var5 - 1].multiply(BigInteger.TEN);
            }

            BIG_TEN_POWERS_TABLE = var2;
         }

         return var2[var0];
      }
   }

   private static long longMultiplyPowerTen(long var0, int var2) {
      if (var0 != 0L && var2 > 0) {
         long[] var3 = LONG_TEN_POWERS_TABLE;
         long[] var4 = THRESHOLDS_TABLE;
         if (var2 < var3.length && var2 < var4.length) {
            long var5 = var3[var2];
            if (var0 == 1L) {
               return var5;
            }

            if (Math.abs(var0) <= var4[var2]) {
               return var0 * var5;
            }
         }

         return Long.MIN_VALUE;
      } else {
         return var0;
      }
   }

   private BigInteger bigMultiplyPowerTen(int var1) {
      if (var1 <= 0) {
         return this.inflated();
      } else {
         return this.intCompact != Long.MIN_VALUE ? bigTenToThe(var1).multiply(this.intCompact) : this.intVal.multiply(bigTenToThe(var1));
      }
   }

   private BigInteger inflated() {
      return this.intVal == null ? BigInteger.valueOf(this.intCompact) : this.intVal;
   }

   private static void matchScale(BigDecimal[] var0) {
      if (var0[0].scale != var0[1].scale) {
         if (var0[0].scale < var0[1].scale) {
            var0[0] = var0[0].setScale(var0[1].scale, 7);
         } else if (var0[1].scale < var0[0].scale) {
            var0[1] = var0[1].setScale(var0[0].scale, 7);
         }

      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      if (this.intVal == null) {
         String var2 = "BigDecimal: null intVal in stream";
         throw new StreamCorruptedException(var2);
      } else {
         BigDecimal.UnsafeHolder.setIntCompactVolatile(this, compactValFor(this.intVal));
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      if (this.intVal == null) {
         BigDecimal.UnsafeHolder.setIntValVolatile(this, BigInteger.valueOf(this.intCompact));
      }

      var1.defaultWriteObject();
   }

   static int longDigitLength(long var0) {
      assert var0 != Long.MIN_VALUE;

      if (var0 < 0L) {
         var0 = -var0;
      }

      if (var0 < 10L) {
         return 1;
      } else {
         int var2 = (64 - Long.numberOfLeadingZeros(var0) + 1) * 1233 >>> 12;
         long[] var3 = LONG_TEN_POWERS_TABLE;
         return var2 < var3.length && var0 >= var3[var2] ? var2 + 1 : var2;
      }
   }

   private static int bigDigitLength(BigInteger var0) {
      if (var0.signum == 0) {
         return 1;
      } else {
         int var1 = (int)(((long)var0.bitLength() + 1L) * 646456993L >>> 31);
         return var0.compareMagnitude(bigTenToThe(var1)) < 0 ? var1 : var1 + 1;
      }
   }

   private int checkScale(long var1) {
      int var3 = (int)var1;
      if ((long)var3 != var1) {
         var3 = var1 > 2147483647L ? Integer.MAX_VALUE : Integer.MIN_VALUE;
         BigInteger var4;
         if (this.intCompact != 0L && ((var4 = this.intVal) == null || var4.signum() != 0)) {
            throw new ArithmeticException(var3 > 0 ? "Underflow" : "Overflow");
         }
      }

      return var3;
   }

   private static long compactValFor(BigInteger var0) {
      int[] var1 = var0.mag;
      int var2 = var1.length;
      if (var2 == 0) {
         return 0L;
      } else {
         int var3 = var1[0];
         if (var2 > 2 || var2 == 2 && var3 < 0) {
            return Long.MIN_VALUE;
         } else {
            long var4 = var2 == 2 ? ((long)var1[1] & 4294967295L) + ((long)var3 << 32) : (long)var3 & 4294967295L;
            return var0.signum < 0 ? -var4 : var4;
         }
      }
   }

   private static int longCompareMagnitude(long var0, long var2) {
      if (var0 < 0L) {
         var0 = -var0;
      }

      if (var2 < 0L) {
         var2 = -var2;
      }

      return var0 < var2 ? -1 : (var0 == var2 ? 0 : 1);
   }

   private static int saturateLong(long var0) {
      int var2 = (int)var0;
      return var0 == (long)var2 ? var2 : (var0 < 0L ? Integer.MIN_VALUE : Integer.MAX_VALUE);
   }

   private static void print(String var0, BigDecimal var1) {
      System.err.format("%s:\tintCompact %d\tintVal %d\tscale %d\tprecision %d%n", var0, var1.intCompact, var1.intVal, var1.scale, var1.precision);
   }

   private BigDecimal audit() {
      if (this.intCompact == Long.MIN_VALUE) {
         if (this.intVal == null) {
            print("audit", this);
            throw new AssertionError("null intVal");
         }

         if (this.precision > 0 && this.precision != bigDigitLength(this.intVal)) {
            print("audit", this);
            throw new AssertionError("precision mismatch");
         }
      } else {
         if (this.intVal != null) {
            long var1 = this.intVal.longValue();
            if (var1 != this.intCompact) {
               print("audit", this);
               throw new AssertionError("Inconsistent state, intCompact=" + this.intCompact + "\t intVal=" + var1);
            }
         }

         if (this.precision > 0 && this.precision != longDigitLength(this.intCompact)) {
            print("audit", this);
            throw new AssertionError("precision mismatch");
         }
      }

      return this;
   }

   private static int checkScaleNonZero(long var0) {
      int var2 = (int)var0;
      if ((long)var2 != var0) {
         throw new ArithmeticException(var2 > 0 ? "Underflow" : "Overflow");
      } else {
         return var2;
      }
   }

   private static int checkScale(long var0, long var2) {
      int var4 = (int)var2;
      if ((long)var4 != var2) {
         var4 = var2 > 2147483647L ? Integer.MAX_VALUE : Integer.MIN_VALUE;
         if (var0 != 0L) {
            throw new ArithmeticException(var4 > 0 ? "Underflow" : "Overflow");
         }
      }

      return var4;
   }

   private static int checkScale(BigInteger var0, long var1) {
      int var3 = (int)var1;
      if ((long)var3 != var1) {
         var3 = var1 > 2147483647L ? Integer.MAX_VALUE : Integer.MIN_VALUE;
         if (var0.signum() != 0) {
            throw new ArithmeticException(var3 > 0 ? "Underflow" : "Overflow");
         }
      }

      return var3;
   }

   private static BigDecimal doRound(BigDecimal var0, MathContext var1) {
      int var2 = var1.precision;
      boolean var3 = false;
      if (var2 <= 0) {
         return var0;
      } else {
         BigInteger var4 = var0.intVal;
         long var5 = var0.intCompact;
         int var7 = var0.scale;
         int var8 = var0.precision();
         int var9 = var1.roundingMode.oldMode;
         int var10;
         if (var5 == Long.MIN_VALUE) {
            for(var10 = var8 - var2; var10 > 0; var10 = var8 - var2) {
               var7 = checkScaleNonZero((long)var7 - (long)var10);
               var4 = divideAndRoundByTenPow(var4, var10, var9);
               var3 = true;
               var5 = compactValFor(var4);
               if (var5 != Long.MIN_VALUE) {
                  var8 = longDigitLength(var5);
                  break;
               }

               var8 = bigDigitLength(var4);
            }
         }

         if (var5 != Long.MIN_VALUE) {
            for(var10 = var8 - var2; var10 > 0; var4 = null) {
               var7 = checkScaleNonZero((long)var7 - (long)var10);
               var5 = divideAndRound(var5, LONG_TEN_POWERS_TABLE[var10], var1.roundingMode.oldMode);
               var3 = true;
               var8 = longDigitLength(var5);
               var10 = var8 - var2;
            }
         }

         return var3 ? new BigDecimal(var4, var5, var7, var8) : var0;
      }
   }

   private static BigDecimal doRound(long var0, int var2, MathContext var3) {
      int var4 = var3.precision;
      if (var4 > 0 && var4 < 19) {
         int var5 = longDigitLength(var0);

         for(int var6 = var5 - var4; var6 > 0; var6 = var5 - var4) {
            var2 = checkScaleNonZero((long)var2 - (long)var6);
            var0 = divideAndRound(var0, LONG_TEN_POWERS_TABLE[var6], var3.roundingMode.oldMode);
            var5 = longDigitLength(var0);
         }

         return valueOf(var0, var2, var5);
      } else {
         return valueOf(var0, var2);
      }
   }

   private static BigDecimal doRound(BigInteger var0, int var1, MathContext var2) {
      int var3 = var2.precision;
      int var4 = 0;
      if (var3 > 0) {
         long var5 = compactValFor(var0);
         int var7 = var2.roundingMode.oldMode;
         int var8;
         if (var5 == Long.MIN_VALUE) {
            var4 = bigDigitLength(var0);

            for(var8 = var4 - var3; var8 > 0; var8 = var4 - var3) {
               var1 = checkScaleNonZero((long)var1 - (long)var8);
               var0 = divideAndRoundByTenPow(var0, var8, var7);
               var5 = compactValFor(var0);
               if (var5 != Long.MIN_VALUE) {
                  break;
               }

               var4 = bigDigitLength(var0);
            }
         }

         if (var5 != Long.MIN_VALUE) {
            var4 = longDigitLength(var5);

            for(var8 = var4 - var3; var8 > 0; var8 = var4 - var3) {
               var1 = checkScaleNonZero((long)var1 - (long)var8);
               var5 = divideAndRound(var5, LONG_TEN_POWERS_TABLE[var8], var2.roundingMode.oldMode);
               var4 = longDigitLength(var5);
            }

            return valueOf(var5, var1, var4);
         }
      }

      return new BigDecimal(var0, Long.MIN_VALUE, var1, var4);
   }

   private static BigInteger divideAndRoundByTenPow(BigInteger var0, int var1, int var2) {
      if (var1 < LONG_TEN_POWERS_TABLE.length) {
         var0 = divideAndRound(var0, LONG_TEN_POWERS_TABLE[var1], var2);
      } else {
         var0 = divideAndRound(var0, bigTenToThe(var1), var2);
      }

      return var0;
   }

   private static BigDecimal divideAndRound(long var0, long var2, int var4, int var5, int var6) {
      long var8 = var0 / var2;
      if (var5 == 1 && var4 == var6) {
         return valueOf(var8, var4);
      } else {
         long var10 = var0 % var2;
         int var7 = var0 < 0L == var2 < 0L ? 1 : -1;
         if (var10 != 0L) {
            boolean var12 = needIncrement(var2, var5, var7, var8, var10);
            return valueOf(var12 ? var8 + (long)var7 : var8, var4);
         } else {
            return var6 != var4 ? createAndStripZerosToMatchScale(var8, var4, (long)var6) : valueOf(var8, var4);
         }
      }
   }

   private static long divideAndRound(long var0, long var2, int var4) {
      long var6 = var0 / var2;
      if (var4 == 1) {
         return var6;
      } else {
         long var8 = var0 % var2;
         int var5 = var0 < 0L == var2 < 0L ? 1 : -1;
         if (var8 != 0L) {
            boolean var10 = needIncrement(var2, var4, var5, var6, var8);
            return var10 ? var6 + (long)var5 : var6;
         } else {
            return var6;
         }
      }
   }

   private static boolean commonNeedIncrement(int var0, int var1, int var2, boolean var3) {
      switch(var0) {
      case 0:
         return true;
      case 1:
         return false;
      case 2:
         return var1 > 0;
      case 3:
         return var1 < 0;
      case 4:
      case 5:
      case 6:
      default:
         assert var0 >= 4 && var0 <= 6 : "Unexpected rounding mode" + RoundingMode.valueOf(var0);

         if (var2 < 0) {
            return false;
         } else if (var2 > 0) {
            return true;
         } else {
            assert var2 == 0;

            switch(var0) {
            case 4:
               return true;
            case 5:
               return false;
            case 6:
               return var3;
            default:
               throw new AssertionError("Unexpected rounding mode" + var0);
            }
         }
      case 7:
         throw new ArithmeticException("Rounding necessary");
      }
   }

   private static boolean needIncrement(long var0, int var2, int var3, long var4, long var6) {
      assert var6 != 0L;

      int var8;
      if (var6 > -4611686018427387904L && var6 <= 4611686018427387903L) {
         var8 = longCompareMagnitude(2L * var6, var0);
      } else {
         var8 = 1;
      }

      return commonNeedIncrement(var2, var3, var8, (var4 & 1L) != 0L);
   }

   private static BigInteger divideAndRound(BigInteger var0, long var1, int var3) {
      long var6 = 0L;
      MutableBigInteger var8 = null;
      MutableBigInteger var9 = new MutableBigInteger(var0.mag);
      var8 = new MutableBigInteger();
      var6 = var9.divide(var1, var8);
      boolean var4 = var6 == 0L;
      int var5 = var1 < 0L ? -var0.signum : var0.signum;
      if (!var4 && needIncrement(var1, var3, var5, var8, var6)) {
         var8.add(MutableBigInteger.ONE);
      }

      return var8.toBigInteger(var5);
   }

   private static BigDecimal divideAndRound(BigInteger var0, long var1, int var3, int var4, int var5) {
      long var8 = 0L;
      MutableBigInteger var10 = null;
      MutableBigInteger var11 = new MutableBigInteger(var0.mag);
      var10 = new MutableBigInteger();
      var8 = var11.divide(var1, var10);
      boolean var6 = var8 == 0L;
      int var7 = var1 < 0L ? -var0.signum : var0.signum;
      if (!var6) {
         if (needIncrement(var1, var4, var7, var10, var8)) {
            var10.add(MutableBigInteger.ONE);
         }

         return var10.toBigDecimal(var7, var3);
      } else if (var5 != var3) {
         long var12 = var10.toCompactValue(var7);
         if (var12 != Long.MIN_VALUE) {
            return createAndStripZerosToMatchScale(var12, var3, (long)var5);
         } else {
            BigInteger var14 = var10.toBigInteger(var7);
            return createAndStripZerosToMatchScale(var14, var3, (long)var5);
         }
      } else {
         return var10.toBigDecimal(var7, var3);
      }
   }

   private static boolean needIncrement(long var0, int var2, int var3, MutableBigInteger var4, long var5) {
      assert var5 != 0L;

      int var7;
      if (var5 > -4611686018427387904L && var5 <= 4611686018427387903L) {
         var7 = longCompareMagnitude(2L * var5, var0);
      } else {
         var7 = 1;
      }

      return commonNeedIncrement(var2, var3, var7, var4.isOdd());
   }

   private static BigInteger divideAndRound(BigInteger var0, BigInteger var1, int var2) {
      MutableBigInteger var5 = new MutableBigInteger(var0.mag);
      MutableBigInteger var6 = new MutableBigInteger();
      MutableBigInteger var7 = new MutableBigInteger(var1.mag);
      MutableBigInteger var8 = var5.divide(var7, var6);
      boolean var3 = var8.isZero();
      int var4 = var0.signum != var1.signum ? -1 : 1;
      if (!var3 && needIncrement(var7, var2, var4, var6, var8)) {
         var6.add(MutableBigInteger.ONE);
      }

      return var6.toBigInteger(var4);
   }

   private static BigDecimal divideAndRound(BigInteger var0, BigInteger var1, int var2, int var3, int var4) {
      MutableBigInteger var7 = new MutableBigInteger(var0.mag);
      MutableBigInteger var8 = new MutableBigInteger();
      MutableBigInteger var9 = new MutableBigInteger(var1.mag);
      MutableBigInteger var10 = var7.divide(var9, var8);
      boolean var5 = var10.isZero();
      int var6 = var0.signum != var1.signum ? -1 : 1;
      if (!var5) {
         if (needIncrement(var9, var3, var6, var8, var10)) {
            var8.add(MutableBigInteger.ONE);
         }

         return var8.toBigDecimal(var6, var2);
      } else if (var4 != var2) {
         long var11 = var8.toCompactValue(var6);
         if (var11 != Long.MIN_VALUE) {
            return createAndStripZerosToMatchScale(var11, var2, (long)var4);
         } else {
            BigInteger var13 = var8.toBigInteger(var6);
            return createAndStripZerosToMatchScale(var13, var2, (long)var4);
         }
      } else {
         return var8.toBigDecimal(var6, var2);
      }
   }

   private static boolean needIncrement(MutableBigInteger var0, int var1, int var2, MutableBigInteger var3, MutableBigInteger var4) {
      assert !var4.isZero();

      int var5 = var4.compareHalf(var0);
      return commonNeedIncrement(var1, var2, var5, var3.isOdd());
   }

   private static BigDecimal createAndStripZerosToMatchScale(BigInteger var0, int var1, long var2) {
      while(true) {
         if (var0.compareMagnitude(BigInteger.TEN) >= 0 && (long)var1 > var2 && !var0.testBit(0)) {
            BigInteger[] var4 = var0.divideAndRemainder(BigInteger.TEN);
            if (var4[1].signum() == 0) {
               var0 = var4[0];
               var1 = checkScale(var0, (long)var1 - 1L);
               continue;
            }
         }

         return valueOf(var0, var1, 0);
      }
   }

   private static BigDecimal createAndStripZerosToMatchScale(long var0, int var2, long var3) {
      while(true) {
         if (Math.abs(var0) >= 10L && (long)var2 > var3 && (var0 & 1L) == 0L) {
            long var5 = var0 % 10L;
            if (var5 == 0L) {
               var0 /= 10L;
               var2 = checkScale(var0, (long)var2 - 1L);
               continue;
            }
         }

         return valueOf(var0, var2);
      }
   }

   private static BigDecimal stripZerosToMatchScale(BigInteger var0, long var1, int var3, int var4) {
      return var1 != Long.MIN_VALUE ? createAndStripZerosToMatchScale(var1, var3, (long)var4) : createAndStripZerosToMatchScale(var0 == null ? INFLATED_BIGINT : var0, var3, (long)var4);
   }

   private static long add(long var0, long var2) {
      long var4 = var0 + var2;
      return ((var4 ^ var0) & (var4 ^ var2)) >= 0L ? var4 : Long.MIN_VALUE;
   }

   private static BigDecimal add(long var0, long var2, int var4) {
      long var5 = add(var0, var2);
      return var5 != Long.MIN_VALUE ? valueOf(var5, var4) : new BigDecimal(BigInteger.valueOf(var0).add(var2), var4);
   }

   private static BigDecimal add(long var0, int var2, long var3, int var5) {
      long var6 = (long)var2 - (long)var5;
      if (var6 == 0L) {
         return add(var0, var3, var2);
      } else {
         int var8;
         long var9;
         BigInteger var11;
         if (var6 < 0L) {
            var8 = checkScale(var0, -var6);
            var9 = longMultiplyPowerTen(var0, var8);
            if (var9 != Long.MIN_VALUE) {
               return add(var9, var3, var5);
            } else {
               var11 = bigMultiplyPowerTen(var0, var8).add(var3);
               return (var0 ^ var3) >= 0L ? new BigDecimal(var11, Long.MIN_VALUE, var5, 0) : valueOf(var11, var5, 0);
            }
         } else {
            var8 = checkScale(var3, var6);
            var9 = longMultiplyPowerTen(var3, var8);
            if (var9 != Long.MIN_VALUE) {
               return add(var0, var9, var2);
            } else {
               var11 = bigMultiplyPowerTen(var3, var8).add(var0);
               return (var0 ^ var3) >= 0L ? new BigDecimal(var11, Long.MIN_VALUE, var2, 0) : valueOf(var11, var2, 0);
            }
         }
      }
   }

   private static BigDecimal add(long var0, int var2, BigInteger var3, int var4) {
      int var5 = var2;
      long var6 = (long)var2 - (long)var4;
      boolean var8 = Long.signum(var0) == var3.signum;
      BigInteger var9;
      int var10;
      if (var6 < 0L) {
         var10 = checkScale(var0, -var6);
         var5 = var4;
         long var11 = longMultiplyPowerTen(var0, var10);
         if (var11 == Long.MIN_VALUE) {
            var9 = var3.add(bigMultiplyPowerTen(var0, var10));
         } else {
            var9 = var3.add(var11);
         }
      } else {
         var10 = checkScale(var3, var6);
         var3 = bigMultiplyPowerTen(var3, var10);
         var9 = var3.add(var0);
      }

      return var8 ? new BigDecimal(var9, Long.MIN_VALUE, var5, 0) : valueOf(var9, var5, 0);
   }

   private static BigDecimal add(BigInteger var0, int var1, BigInteger var2, int var3) {
      int var4 = var1;
      long var5 = (long)var1 - (long)var3;
      if (var5 != 0L) {
         int var7;
         if (var5 < 0L) {
            var7 = checkScale(var0, -var5);
            var4 = var3;
            var0 = bigMultiplyPowerTen(var0, var7);
         } else {
            var7 = checkScale(var2, var5);
            var2 = bigMultiplyPowerTen(var2, var7);
         }
      }

      BigInteger var8 = var0.add(var2);
      return var0.signum == var2.signum ? new BigDecimal(var8, Long.MIN_VALUE, var4, 0) : valueOf(var8, var4, 0);
   }

   private static BigInteger bigMultiplyPowerTen(long var0, int var2) {
      return var2 <= 0 ? BigInteger.valueOf(var0) : bigTenToThe(var2).multiply(var0);
   }

   private static BigInteger bigMultiplyPowerTen(BigInteger var0, int var1) {
      if (var1 <= 0) {
         return var0;
      } else {
         return var1 < LONG_TEN_POWERS_TABLE.length ? var0.multiply(LONG_TEN_POWERS_TABLE[var1]) : var0.multiply(bigTenToThe(var1));
      }
   }

   private static BigDecimal divideSmallFastPath(long var0, int var2, long var3, int var5, long var6, MathContext var8) {
      int var9 = var8.precision;
      int var10 = var8.roundingMode.oldMode;

      assert var2 <= var5 && var5 < 18 && var9 < 18;

      int var11 = var5 - var2;
      long var12 = var11 == 0 ? var0 : longMultiplyPowerTen(var0, var11);
      int var15 = longCompareMagnitude(var12, var3);
      BigDecimal var14;
      int var16;
      if (var15 > 0) {
         --var5;
         var16 = checkScaleNonZero(var6 + (long)var5 - (long)var2 + (long)var9);
         int var17;
         if (checkScaleNonZero((long)var9 + (long)var5 - (long)var2) > 0) {
            var17 = checkScaleNonZero((long)var9 + (long)var5 - (long)var2);
            long var18;
            if ((var18 = longMultiplyPowerTen(var0, var17)) == Long.MIN_VALUE) {
               var14 = null;
               if (var9 - 1 >= 0 && var9 - 1 < LONG_TEN_POWERS_TABLE.length) {
                  var14 = multiplyDivideAndRound(LONG_TEN_POWERS_TABLE[var9 - 1], var12, var3, var16, var10, checkScaleNonZero(var6));
               }

               if (var14 == null) {
                  BigInteger var20 = bigMultiplyPowerTen(var12, var9 - 1);
                  var14 = divideAndRound(var20, var3, var16, var10, checkScaleNonZero(var6));
               }
            } else {
               var14 = divideAndRound(var18, var3, var16, var10, checkScaleNonZero(var6));
            }
         } else {
            var17 = checkScaleNonZero((long)var2 - (long)var9);
            if (var17 == var5) {
               var14 = divideAndRound(var0, var3, var16, var10, checkScaleNonZero(var6));
            } else {
               int var23 = checkScaleNonZero((long)var17 - (long)var5);
               long var19;
               if ((var19 = longMultiplyPowerTen(var3, var23)) == Long.MIN_VALUE) {
                  BigInteger var21 = bigMultiplyPowerTen(var3, var23);
                  var14 = divideAndRound(BigInteger.valueOf(var0), var21, var16, var10, checkScaleNonZero(var6));
               } else {
                  var14 = divideAndRound(var0, var19, var16, var10, checkScaleNonZero(var6));
               }
            }
         }
      } else {
         var16 = checkScaleNonZero(var6 + (long)var5 - (long)var2 + (long)var9);
         if (var15 == 0) {
            var14 = roundedTenPower(var12 < 0L == var3 < 0L ? 1 : -1, var9, var16, checkScaleNonZero(var6));
         } else {
            long var22;
            if ((var22 = longMultiplyPowerTen(var12, var9)) == Long.MIN_VALUE) {
               var14 = null;
               if (var9 < LONG_TEN_POWERS_TABLE.length) {
                  var14 = multiplyDivideAndRound(LONG_TEN_POWERS_TABLE[var9], var12, var3, var16, var10, checkScaleNonZero(var6));
               }

               if (var14 == null) {
                  BigInteger var24 = bigMultiplyPowerTen(var12, var9);
                  var14 = divideAndRound(var24, var3, var16, var10, checkScaleNonZero(var6));
               }
            } else {
               var14 = divideAndRound(var22, var3, var16, var10, checkScaleNonZero(var6));
            }
         }
      }

      return doRound(var14, var8);
   }

   private static BigDecimal divide(long var0, int var2, long var3, int var5, long var6, MathContext var8) {
      int var9 = var8.precision;
      if (var2 <= var5 && var5 < 18 && var9 < 18) {
         return divideSmallFastPath(var0, var2, var3, var5, var6, var8);
      } else {
         if (compareMagnitudeNormalized(var0, var2, var3, var5) > 0) {
            --var5;
         }

         int var10 = var8.roundingMode.oldMode;
         int var11 = checkScaleNonZero(var6 + (long)var5 - (long)var2 + (long)var9);
         BigDecimal var12;
         int var13;
         if (checkScaleNonZero((long)var9 + (long)var5 - (long)var2) > 0) {
            var13 = checkScaleNonZero((long)var9 + (long)var5 - (long)var2);
            long var14;
            if ((var14 = longMultiplyPowerTen(var0, var13)) == Long.MIN_VALUE) {
               BigInteger var16 = bigMultiplyPowerTen(var0, var13);
               var12 = divideAndRound(var16, var3, var11, var10, checkScaleNonZero(var6));
            } else {
               var12 = divideAndRound(var14, var3, var11, var10, checkScaleNonZero(var6));
            }
         } else {
            var13 = checkScaleNonZero((long)var2 - (long)var9);
            if (var13 == var5) {
               var12 = divideAndRound(var0, var3, var11, var10, checkScaleNonZero(var6));
            } else {
               int var18 = checkScaleNonZero((long)var13 - (long)var5);
               long var15;
               if ((var15 = longMultiplyPowerTen(var3, var18)) == Long.MIN_VALUE) {
                  BigInteger var17 = bigMultiplyPowerTen(var3, var18);
                  var12 = divideAndRound(BigInteger.valueOf(var0), var17, var11, var10, checkScaleNonZero(var6));
               } else {
                  var12 = divideAndRound(var0, var15, var11, var10, checkScaleNonZero(var6));
               }
            }
         }

         return doRound(var12, var8);
      }
   }

   private static BigDecimal divide(BigInteger var0, int var1, long var2, int var4, long var5, MathContext var7) {
      if (-compareMagnitudeNormalized(var2, var4, var0, var1) > 0) {
         --var4;
      }

      int var8 = var7.precision;
      int var9 = var7.roundingMode.oldMode;
      int var11 = checkScaleNonZero(var5 + (long)var4 - (long)var1 + (long)var8);
      BigDecimal var10;
      int var12;
      if (checkScaleNonZero((long)var8 + (long)var4 - (long)var1) > 0) {
         var12 = checkScaleNonZero((long)var8 + (long)var4 - (long)var1);
         BigInteger var13 = bigMultiplyPowerTen(var0, var12);
         var10 = divideAndRound(var13, var2, var11, var9, checkScaleNonZero(var5));
      } else {
         var12 = checkScaleNonZero((long)var1 - (long)var8);
         if (var12 == var4) {
            var10 = divideAndRound(var0, var2, var11, var9, checkScaleNonZero(var5));
         } else {
            int var17 = checkScaleNonZero((long)var12 - (long)var4);
            long var14;
            if ((var14 = longMultiplyPowerTen(var2, var17)) == Long.MIN_VALUE) {
               BigInteger var16 = bigMultiplyPowerTen(var2, var17);
               var10 = divideAndRound(var0, var16, var11, var9, checkScaleNonZero(var5));
            } else {
               var10 = divideAndRound(var0, var14, var11, var9, checkScaleNonZero(var5));
            }
         }
      }

      return doRound(var10, var7);
   }

   private static BigDecimal divide(long var0, int var2, BigInteger var3, int var4, long var5, MathContext var7) {
      if (compareMagnitudeNormalized(var0, var2, var3, var4) > 0) {
         --var4;
      }

      int var8 = var7.precision;
      int var9 = var7.roundingMode.oldMode;
      int var11 = checkScaleNonZero(var5 + (long)var4 - (long)var2 + (long)var8);
      BigDecimal var10;
      int var12;
      if (checkScaleNonZero((long)var8 + (long)var4 - (long)var2) > 0) {
         var12 = checkScaleNonZero((long)var8 + (long)var4 - (long)var2);
         BigInteger var13 = bigMultiplyPowerTen(var0, var12);
         var10 = divideAndRound(var13, var3, var11, var9, checkScaleNonZero(var5));
      } else {
         var12 = checkScaleNonZero((long)var2 - (long)var8);
         int var15 = checkScaleNonZero((long)var12 - (long)var4);
         BigInteger var14 = bigMultiplyPowerTen(var3, var15);
         var10 = divideAndRound(BigInteger.valueOf(var0), var14, var11, var9, checkScaleNonZero(var5));
      }

      return doRound(var10, var7);
   }

   private static BigDecimal divide(BigInteger var0, int var1, BigInteger var2, int var3, long var4, MathContext var6) {
      if (compareMagnitudeNormalized(var0, var1, var2, var3) > 0) {
         --var3;
      }

      int var7 = var6.precision;
      int var8 = var6.roundingMode.oldMode;
      int var10 = checkScaleNonZero(var4 + (long)var3 - (long)var1 + (long)var7);
      BigDecimal var9;
      int var11;
      if (checkScaleNonZero((long)var7 + (long)var3 - (long)var1) > 0) {
         var11 = checkScaleNonZero((long)var7 + (long)var3 - (long)var1);
         BigInteger var12 = bigMultiplyPowerTen(var0, var11);
         var9 = divideAndRound(var12, var2, var10, var8, checkScaleNonZero(var4));
      } else {
         var11 = checkScaleNonZero((long)var1 - (long)var7);
         int var14 = checkScaleNonZero((long)var11 - (long)var3);
         BigInteger var13 = bigMultiplyPowerTen(var2, var14);
         var9 = divideAndRound(var0, var13, var10, var8, checkScaleNonZero(var4));
      }

      return doRound(var9, var6);
   }

   private static BigDecimal multiplyDivideAndRound(long var0, long var2, long var4, int var6, int var7, int var8) {
      int var9 = Long.signum(var0) * Long.signum(var2) * Long.signum(var4);
      var0 = Math.abs(var0);
      var2 = Math.abs(var2);
      var4 = Math.abs(var4);
      long var10 = var0 >>> 32;
      long var12 = var0 & 4294967295L;
      long var14 = var2 >>> 32;
      long var16 = var2 & 4294967295L;
      long var18 = var12 * var16;
      long var20 = var18 & 4294967295L;
      long var22 = var18 >>> 32;
      var18 = var10 * var16 + var22;
      var22 = var18 & 4294967295L;
      long var24 = var18 >>> 32;
      var18 = var12 * var14 + var22;
      var22 = var18 & 4294967295L;
      var24 += var18 >>> 32;
      long var26 = var24 >>> 32;
      var24 &= 4294967295L;
      var18 = var10 * var14 + var24;
      var24 = var18 & 4294967295L;
      var26 = (var18 >>> 32) + var26 & 4294967295L;
      long var28 = make64(var26, var24);
      long var30 = make64(var22, var20);
      return divideAndRound128(var28, var30, var4, var9, var6, var7, var8);
   }

   private static BigDecimal divideAndRound128(long var0, long var2, long var4, int var6, int var7, int var8, int var9) {
      if (var0 >= var4) {
         return null;
      } else {
         int var10 = Long.numberOfLeadingZeros(var4);
         var4 <<= var10;
         long var11 = var4 >>> 32;
         long var13 = var4 & 4294967295L;
         long var15 = var2 << var10;
         long var17 = var15 >>> 32;
         long var19 = var15 & 4294967295L;
         var15 = var0 << var10 | var2 >>> 64 - var10;
         long var21 = var15 & 4294967295L;
         long var23;
         long var25;
         if (var11 == 1L) {
            var23 = var15;
            var25 = 0L;
         } else if (var15 >= 0L) {
            var23 = var15 / var11;
            var25 = var15 - var23 * var11;
         } else {
            long[] var27 = divRemNegativeLong(var15, var11);
            var23 = var27[1];
            var25 = var27[0];
         }

         while(var23 >= 4294967296L || unsignedLongCompare(var23 * var13, make64(var25, var17))) {
            --var23;
            var25 += var11;
            if (var25 >= 4294967296L) {
               break;
            }
         }

         var15 = mulsub(var21, var17, var11, var13, var23);
         var17 = var15 & 4294967295L;
         long var34;
         if (var11 == 1L) {
            var34 = var15;
            var25 = 0L;
         } else if (var15 >= 0L) {
            var34 = var15 / var11;
            var25 = var15 - var34 * var11;
         } else {
            long[] var29 = divRemNegativeLong(var15, var11);
            var34 = var29[1];
            var25 = var29[0];
         }

         while(var34 >= 4294967296L || unsignedLongCompare(var34 * var13, make64(var25, var19))) {
            --var34;
            var25 += var11;
            if (var25 >= 4294967296L) {
               break;
            }
         }

         if ((int)var23 < 0) {
            MutableBigInteger var36 = new MutableBigInteger(new int[]{(int)var23, (int)var34});
            if (var8 == 1 && var7 == var9) {
               return var36.toBigDecimal(var6, var7);
            } else {
               long var30 = mulsub(var17, var19, var11, var13, var34) >>> var10;
               if (var30 != 0L) {
                  if (needIncrement(var4 >>> var10, var8, var6, var36, var30)) {
                     var36.add(MutableBigInteger.ONE);
                  }

                  return var36.toBigDecimal(var6, var7);
               } else if (var9 != var7) {
                  BigInteger var32 = var36.toBigInteger(var6);
                  return createAndStripZerosToMatchScale(var32, var7, (long)var9);
               } else {
                  return var36.toBigDecimal(var6, var7);
               }
            }
         } else {
            long var35 = make64(var23, var34);
            var35 *= (long)var6;
            if (var8 == 1 && var7 == var9) {
               return valueOf(var35, var7);
            } else {
               long var31 = mulsub(var17, var19, var11, var13, var34) >>> var10;
               if (var31 != 0L) {
                  boolean var33 = needIncrement(var4 >>> var10, var8, var6, var35, var31);
                  return valueOf(var33 ? var35 + (long)var6 : var35, var7);
               } else {
                  return var9 != var7 ? createAndStripZerosToMatchScale(var35, var7, (long)var9) : valueOf(var35, var7);
               }
            }
         }
      }
   }

   private static BigDecimal roundedTenPower(int var0, int var1, int var2, int var3) {
      if (var2 > var3) {
         int var4 = var2 - var3;
         return var4 < var1 ? scaledTenPow(var1 - var4, var0, var3) : valueOf((long)var0, var2 - var1);
      } else {
         return scaledTenPow(var1, var0, var2);
      }
   }

   static BigDecimal scaledTenPow(int var0, int var1, int var2) {
      if (var0 < LONG_TEN_POWERS_TABLE.length) {
         return valueOf((long)var1 * LONG_TEN_POWERS_TABLE[var0], var2);
      } else {
         BigInteger var3 = bigTenToThe(var0);
         if (var1 == -1) {
            var3 = var3.negate();
         }

         return new BigDecimal(var3, Long.MIN_VALUE, var2, var0 + 1);
      }
   }

   private static long[] divRemNegativeLong(long var0, long var2) {
      assert var0 < 0L : "Non-negative numerator " + var0;

      assert var2 != 1L : "Unity denominator";

      long var4 = (var0 >>> 1) / (var2 >>> 1);

      long var6;
      for(var6 = var0 - var4 * var2; var6 < 0L; --var4) {
         var6 += var2;
      }

      while(var6 >= var2) {
         var6 -= var2;
         ++var4;
      }

      return new long[]{var6, var4};
   }

   private static long make64(long var0, long var2) {
      return var0 << 32 | var2;
   }

   private static long mulsub(long var0, long var2, long var4, long var6, long var8) {
      long var10 = var2 - var8 * var6;
      return make64(var0 + (var10 >>> 32) - var8 * var4, var10 & 4294967295L);
   }

   private static boolean unsignedLongCompare(long var0, long var2) {
      return var0 + Long.MIN_VALUE > var2 + Long.MIN_VALUE;
   }

   private static boolean unsignedLongCompareEq(long var0, long var2) {
      return var0 + Long.MIN_VALUE >= var2 + Long.MIN_VALUE;
   }

   private static int compareMagnitudeNormalized(long var0, int var2, long var3, int var5) {
      int var6 = var2 - var5;
      if (var6 != 0) {
         if (var6 < 0) {
            var0 = longMultiplyPowerTen(var0, -var6);
         } else {
            var3 = longMultiplyPowerTen(var3, var6);
         }
      }

      if (var0 != Long.MIN_VALUE) {
         return var3 != Long.MIN_VALUE ? longCompareMagnitude(var0, var3) : -1;
      } else {
         return 1;
      }
   }

   private static int compareMagnitudeNormalized(long var0, int var2, BigInteger var3, int var4) {
      if (var0 == 0L) {
         return -1;
      } else {
         int var5 = var2 - var4;
         return var5 < 0 && longMultiplyPowerTen(var0, -var5) == Long.MIN_VALUE ? bigMultiplyPowerTen(var0, -var5).compareMagnitude(var3) : -1;
      }
   }

   private static int compareMagnitudeNormalized(BigInteger var0, int var1, BigInteger var2, int var3) {
      int var4 = var1 - var3;
      return var4 < 0 ? bigMultiplyPowerTen(var0, -var4).compareMagnitude(var2) : var0.compareMagnitude(bigMultiplyPowerTen(var2, var4));
   }

   private static long multiply(long var0, long var2) {
      long var4 = var0 * var2;
      long var6 = Math.abs(var0);
      long var8 = Math.abs(var2);
      return (var6 | var8) >>> 31 != 0L && var2 != 0L && var4 / var2 != var0 ? Long.MIN_VALUE : var4;
   }

   private static BigDecimal multiply(long var0, long var2, int var4) {
      long var5 = multiply(var0, var2);
      return var5 != Long.MIN_VALUE ? valueOf(var5, var4) : new BigDecimal(BigInteger.valueOf(var0).multiply(var2), Long.MIN_VALUE, var4, 0);
   }

   private static BigDecimal multiply(long var0, BigInteger var2, int var3) {
      return var0 == 0L ? zeroValueOf(var3) : new BigDecimal(var2.multiply(var0), Long.MIN_VALUE, var3, 0);
   }

   private static BigDecimal multiply(BigInteger var0, BigInteger var1, int var2) {
      return new BigDecimal(var0.multiply(var1), Long.MIN_VALUE, var2, 0);
   }

   private static BigDecimal multiplyAndRound(long var0, long var2, int var4, MathContext var5) {
      long var6 = multiply(var0, var2);
      if (var6 != Long.MIN_VALUE) {
         return doRound(var6, var4, var5);
      } else {
         int var8 = 1;
         if (var0 < 0L) {
            var0 = -var0;
            var8 = -1;
         }

         if (var2 < 0L) {
            var2 = -var2;
            var8 *= -1;
         }

         long var9 = var0 >>> 32;
         long var11 = var0 & 4294967295L;
         long var13 = var2 >>> 32;
         long var15 = var2 & 4294967295L;
         var6 = var11 * var15;
         long var17 = var6 & 4294967295L;
         long var19 = var6 >>> 32;
         var6 = var9 * var15 + var19;
         var19 = var6 & 4294967295L;
         long var21 = var6 >>> 32;
         var6 = var11 * var13 + var19;
         var19 = var6 & 4294967295L;
         var21 += var6 >>> 32;
         long var23 = var21 >>> 32;
         var21 &= 4294967295L;
         var6 = var9 * var13 + var21;
         var21 = var6 & 4294967295L;
         var23 = (var6 >>> 32) + var23 & 4294967295L;
         long var25 = make64(var23, var21);
         long var27 = make64(var19, var17);
         BigDecimal var29 = doRound128(var25, var27, var8, var4, var5);
         if (var29 != null) {
            return var29;
         } else {
            var29 = new BigDecimal(BigInteger.valueOf(var0).multiply(var2 * (long)var8), Long.MIN_VALUE, var4, 0);
            return doRound(var29, var5);
         }
      }
   }

   private static BigDecimal multiplyAndRound(long var0, BigInteger var2, int var3, MathContext var4) {
      return var0 == 0L ? zeroValueOf(var3) : doRound(var2.multiply(var0), var3, var4);
   }

   private static BigDecimal multiplyAndRound(BigInteger var0, BigInteger var1, int var2, MathContext var3) {
      return doRound(var0.multiply(var1), var2, var3);
   }

   private static BigDecimal doRound128(long var0, long var2, int var4, int var5, MathContext var6) {
      int var7 = var6.precision;
      BigDecimal var9 = null;
      int var8;
      if ((var8 = precision(var0, var2) - var7) > 0 && var8 < LONG_TEN_POWERS_TABLE.length) {
         var5 = checkScaleNonZero((long)var5 - (long)var8);
         var9 = divideAndRound128(var0, var2, LONG_TEN_POWERS_TABLE[var8], var4, var5, var6.roundingMode.oldMode, var5);
      }

      return var9 != null ? doRound(var9, var6) : null;
   }

   private static int precision(long var0, long var2) {
      if (var0 == 0L) {
         if (var2 >= 0L) {
            return longDigitLength(var2);
         } else {
            return unsignedLongCompareEq(var2, LONGLONG_TEN_POWERS_TABLE[0][1]) ? 20 : 19;
         }
      } else {
         int var4 = (128 - Long.numberOfLeadingZeros(var0) + 1) * 1233 >>> 12;
         int var5 = var4 - 19;
         return var5 < LONGLONG_TEN_POWERS_TABLE.length && !longLongCompareMagnitude(var0, var2, LONGLONG_TEN_POWERS_TABLE[var5][0], LONGLONG_TEN_POWERS_TABLE[var5][1]) ? var4 + 1 : var4;
      }
   }

   private static boolean longLongCompareMagnitude(long var0, long var2, long var4, long var6) {
      if (var0 != var4) {
         return var0 < var4;
      } else {
         return var2 + Long.MIN_VALUE < var6 + Long.MIN_VALUE;
      }
   }

   private static BigDecimal divide(long var0, int var2, long var3, int var5, int var6, int var7) {
      int var8;
      int var9;
      long var10;
      BigInteger var13;
      if (checkScale(var0, (long)var6 + (long)var5) > var2) {
         var8 = var6 + var5;
         var9 = var8 - var2;
         if (var9 < LONG_TEN_POWERS_TABLE.length) {
            if ((var10 = longMultiplyPowerTen(var0, var9)) != Long.MIN_VALUE) {
               return divideAndRound(var10, var3, var6, var7, var6);
            }

            BigDecimal var12 = multiplyDivideAndRound(LONG_TEN_POWERS_TABLE[var9], var0, var3, var6, var7, var6);
            if (var12 != null) {
               return var12;
            }
         }

         var13 = bigMultiplyPowerTen(var0, var9);
         return divideAndRound(var13, var3, var6, var7, var6);
      } else {
         var8 = checkScale(var3, (long)var2 - (long)var6);
         var9 = var8 - var5;
         if (var9 < LONG_TEN_POWERS_TABLE.length && (var10 = longMultiplyPowerTen(var3, var9)) != Long.MIN_VALUE) {
            return divideAndRound(var0, var10, var6, var7, var6);
         } else {
            var13 = bigMultiplyPowerTen(var3, var9);
            return divideAndRound(BigInteger.valueOf(var0), var13, var6, var7, var6);
         }
      }
   }

   private static BigDecimal divide(BigInteger var0, int var1, long var2, int var4, int var5, int var6) {
      int var7;
      int var8;
      BigInteger var11;
      if (checkScale(var0, (long)var5 + (long)var4) > var1) {
         var7 = var5 + var4;
         var8 = var7 - var1;
         var11 = bigMultiplyPowerTen(var0, var8);
         return divideAndRound(var11, var2, var5, var6, var5);
      } else {
         var7 = checkScale(var2, (long)var1 - (long)var5);
         var8 = var7 - var4;
         long var9;
         if (var8 < LONG_TEN_POWERS_TABLE.length && (var9 = longMultiplyPowerTen(var2, var8)) != Long.MIN_VALUE) {
            return divideAndRound(var0, var9, var5, var6, var5);
         } else {
            var11 = bigMultiplyPowerTen(var2, var8);
            return divideAndRound(var0, var11, var5, var6, var5);
         }
      }
   }

   private static BigDecimal divide(long var0, int var2, BigInteger var3, int var4, int var5, int var6) {
      int var7;
      int var8;
      BigInteger var9;
      if (checkScale(var0, (long)var5 + (long)var4) > var2) {
         var7 = var5 + var4;
         var8 = var7 - var2;
         var9 = bigMultiplyPowerTen(var0, var8);
         return divideAndRound(var9, var3, var5, var6, var5);
      } else {
         var7 = checkScale(var3, (long)var2 - (long)var5);
         var8 = var7 - var4;
         var9 = bigMultiplyPowerTen(var3, var8);
         return divideAndRound(BigInteger.valueOf(var0), var9, var5, var6, var5);
      }
   }

   private static BigDecimal divide(BigInteger var0, int var1, BigInteger var2, int var3, int var4, int var5) {
      int var6;
      int var7;
      BigInteger var8;
      if (checkScale(var0, (long)var4 + (long)var3) > var1) {
         var6 = var4 + var3;
         var7 = var6 - var1;
         var8 = bigMultiplyPowerTen(var0, var7);
         return divideAndRound(var8, var2, var4, var5, var4);
      } else {
         var6 = checkScale(var2, (long)var1 - (long)var4);
         var7 = var6 - var3;
         var8 = bigMultiplyPowerTen(var2, var7);
         return divideAndRound(var0, var8, var4, var5, var4);
      }
   }

   static {
      zeroThroughTen = new BigDecimal[]{new BigDecimal(BigInteger.ZERO, 0L, 0, 1), new BigDecimal(BigInteger.ONE, 1L, 0, 1), new BigDecimal(BigInteger.valueOf(2L), 2L, 0, 1), new BigDecimal(BigInteger.valueOf(3L), 3L, 0, 1), new BigDecimal(BigInteger.valueOf(4L), 4L, 0, 1), new BigDecimal(BigInteger.valueOf(5L), 5L, 0, 1), new BigDecimal(BigInteger.valueOf(6L), 6L, 0, 1), new BigDecimal(BigInteger.valueOf(7L), 7L, 0, 1), new BigDecimal(BigInteger.valueOf(8L), 8L, 0, 1), new BigDecimal(BigInteger.valueOf(9L), 9L, 0, 1), new BigDecimal(BigInteger.TEN, 10L, 0, 2)};
      ZERO_SCALED_BY = new BigDecimal[]{zeroThroughTen[0], new BigDecimal(BigInteger.ZERO, 0L, 1, 1), new BigDecimal(BigInteger.ZERO, 0L, 2, 1), new BigDecimal(BigInteger.ZERO, 0L, 3, 1), new BigDecimal(BigInteger.ZERO, 0L, 4, 1), new BigDecimal(BigInteger.ZERO, 0L, 5, 1), new BigDecimal(BigInteger.ZERO, 0L, 6, 1), new BigDecimal(BigInteger.ZERO, 0L, 7, 1), new BigDecimal(BigInteger.ZERO, 0L, 8, 1), new BigDecimal(BigInteger.ZERO, 0L, 9, 1), new BigDecimal(BigInteger.ZERO, 0L, 10, 1), new BigDecimal(BigInteger.ZERO, 0L, 11, 1), new BigDecimal(BigInteger.ZERO, 0L, 12, 1), new BigDecimal(BigInteger.ZERO, 0L, 13, 1), new BigDecimal(BigInteger.ZERO, 0L, 14, 1), new BigDecimal(BigInteger.ZERO, 0L, 15, 1)};
      ZERO = zeroThroughTen[0];
      ONE = zeroThroughTen[1];
      TEN = zeroThroughTen[10];
      double10pow = new double[]{1.0D, 10.0D, 100.0D, 1000.0D, 10000.0D, 100000.0D, 1000000.0D, 1.0E7D, 1.0E8D, 1.0E9D, 1.0E10D, 1.0E11D, 1.0E12D, 1.0E13D, 1.0E14D, 1.0E15D, 1.0E16D, 1.0E17D, 1.0E18D, 1.0E19D, 1.0E20D, 1.0E21D, 1.0E22D};
      float10pow = new float[]{1.0F, 10.0F, 100.0F, 1000.0F, 10000.0F, 100000.0F, 1000000.0F, 1.0E7F, 1.0E8F, 1.0E9F, 1.0E10F};
      LONG_TEN_POWERS_TABLE = new long[]{1L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L, 10000000L, 100000000L, 1000000000L, 10000000000L, 100000000000L, 1000000000000L, 10000000000000L, 100000000000000L, 1000000000000000L, 10000000000000000L, 100000000000000000L, 1000000000000000000L};
      BIG_TEN_POWERS_TABLE = new BigInteger[]{BigInteger.ONE, BigInteger.valueOf(10L), BigInteger.valueOf(100L), BigInteger.valueOf(1000L), BigInteger.valueOf(10000L), BigInteger.valueOf(100000L), BigInteger.valueOf(1000000L), BigInteger.valueOf(10000000L), BigInteger.valueOf(100000000L), BigInteger.valueOf(1000000000L), BigInteger.valueOf(10000000000L), BigInteger.valueOf(100000000000L), BigInteger.valueOf(1000000000000L), BigInteger.valueOf(10000000000000L), BigInteger.valueOf(100000000000000L), BigInteger.valueOf(1000000000000000L), BigInteger.valueOf(10000000000000000L), BigInteger.valueOf(100000000000000000L), BigInteger.valueOf(1000000000000000000L)};
      BIG_TEN_POWERS_TABLE_INITLEN = BIG_TEN_POWERS_TABLE.length;
      BIG_TEN_POWERS_TABLE_MAX = 16 * BIG_TEN_POWERS_TABLE_INITLEN;
      THRESHOLDS_TABLE = new long[]{Long.MAX_VALUE, 922337203685477580L, 92233720368547758L, 9223372036854775L, 922337203685477L, 92233720368547L, 9223372036854L, 922337203685L, 92233720368L, 9223372036L, 922337203L, 92233720L, 9223372L, 922337L, 92233L, 9223L, 922L, 92L, 9L};
      LONGLONG_TEN_POWERS_TABLE = new long[][]{{0L, -8446744073709551616L}, {5L, 7766279631452241920L}, {54L, 3875820019684212736L}, {542L, 1864712049423024128L}, {5421L, 200376420520689664L}, {54210L, 2003764205206896640L}, {542101L, 1590897978359414784L}, {5421010L, -2537764290115403776L}, {54210108L, -6930898827444486144L}, {542101086L, 4477988020393345024L}, {5421010862L, 7886392056514347008L}, {54210108624L, 5076944270305263616L}, {542101086242L, -4570789518076018688L}, {5421010862427L, -8814407033341083648L}, {54210108624275L, 4089650035136921600L}, {542101086242752L, 4003012203950112768L}, {5421010862427522L, 3136633892082024448L}, {54210108624275221L, -5527149226598858752L}, {542101086242752217L, 68739955140067328L}, {5421010862427522170L, 687399551400673280L}};
   }

   private static class UnsafeHolder {
      private static final Unsafe unsafe;
      private static final long intCompactOffset;
      private static final long intValOffset;

      static void setIntCompactVolatile(BigDecimal var0, long var1) {
         unsafe.putLongVolatile(var0, intCompactOffset, var1);
      }

      static void setIntValVolatile(BigDecimal var0, BigInteger var1) {
         unsafe.putObjectVolatile(var0, intValOffset, var1);
      }

      static {
         try {
            unsafe = Unsafe.getUnsafe();
            intCompactOffset = unsafe.objectFieldOffset(BigDecimal.class.getDeclaredField("intCompact"));
            intValOffset = unsafe.objectFieldOffset(BigDecimal.class.getDeclaredField("intVal"));
         } catch (Exception var1) {
            throw new ExceptionInInitializerError(var1);
         }
      }
   }

   static class StringBuilderHelper {
      final StringBuilder sb = new StringBuilder();
      final char[] cmpCharArray = new char[19];
      static final char[] DIGIT_TENS = new char[]{'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '2', '2', '2', '2', '2', '2', '2', '2', '2', '2', '3', '3', '3', '3', '3', '3', '3', '3', '3', '3', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '5', '5', '5', '5', '5', '5', '5', '5', '5', '5', '6', '6', '6', '6', '6', '6', '6', '6', '6', '6', '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '8', '8', '8', '8', '8', '8', '8', '8', '8', '8', '9', '9', '9', '9', '9', '9', '9', '9', '9', '9'};
      static final char[] DIGIT_ONES = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

      StringBuilder getStringBuilder() {
         this.sb.setLength(0);
         return this.sb;
      }

      char[] getCompactCharArray() {
         return this.cmpCharArray;
      }

      int putIntCompact(long var1) {
         assert var1 >= 0L;

         int var5;
         int var6;
         for(var6 = this.cmpCharArray.length; var1 > 2147483647L; this.cmpCharArray[var6] = DIGIT_TENS[var5]) {
            long var3 = var1 / 100L;
            var5 = (int)(var1 - var3 * 100L);
            var1 = var3;
            --var6;
            this.cmpCharArray[var6] = DIGIT_ONES[var5];
            --var6;
         }

         int var8;
         for(var8 = (int)var1; var8 >= 100; this.cmpCharArray[var6] = DIGIT_TENS[var5]) {
            int var7 = var8 / 100;
            var5 = var8 - var7 * 100;
            var8 = var7;
            --var6;
            this.cmpCharArray[var6] = DIGIT_ONES[var5];
            --var6;
         }

         --var6;
         this.cmpCharArray[var6] = DIGIT_ONES[var8];
         if (var8 >= 10) {
            --var6;
            this.cmpCharArray[var6] = DIGIT_TENS[var8];
         }

         return var6;
      }
   }

   private static class LongOverflow {
      private static final BigInteger LONGMIN = BigInteger.valueOf(Long.MIN_VALUE);
      private static final BigInteger LONGMAX = BigInteger.valueOf(Long.MAX_VALUE);

      public static void check(BigDecimal var0) {
         BigInteger var1 = var0.inflated();
         if (var1.compareTo(LONGMIN) < 0 || var1.compareTo(LONGMAX) > 0) {
            throw new ArithmeticException("Overflow");
         }
      }
   }
}
