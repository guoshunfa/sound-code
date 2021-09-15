package java.text;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import sun.misc.FloatingDecimal;

final class DigitList implements Cloneable {
   public static final int MAX_COUNT = 19;
   public int decimalAt = 0;
   public int count = 0;
   public char[] digits = new char[19];
   private char[] data;
   private RoundingMode roundingMode;
   private boolean isNegative;
   private static final char[] LONG_MIN_REP = "9223372036854775808".toCharArray();
   private StringBuffer tempBuffer;

   DigitList() {
      this.roundingMode = RoundingMode.HALF_EVEN;
      this.isNegative = false;
   }

   boolean isZero() {
      for(int var1 = 0; var1 < this.count; ++var1) {
         if (this.digits[var1] != '0') {
            return false;
         }
      }

      return true;
   }

   void setRoundingMode(RoundingMode var1) {
      this.roundingMode = var1;
   }

   public void clear() {
      this.decimalAt = 0;
      this.count = 0;
   }

   public void append(char var1) {
      if (this.count == this.digits.length) {
         char[] var2 = new char[this.count + 100];
         System.arraycopy(this.digits, 0, var2, 0, this.count);
         this.digits = var2;
      }

      this.digits[this.count++] = var1;
   }

   public final double getDouble() {
      if (this.count == 0) {
         return 0.0D;
      } else {
         StringBuffer var1 = this.getStringBuffer();
         var1.append('.');
         var1.append((char[])this.digits, 0, this.count);
         var1.append('E');
         var1.append(this.decimalAt);
         return Double.parseDouble(var1.toString());
      }
   }

   public final long getLong() {
      if (this.count == 0) {
         return 0L;
      } else if (this.isLongMIN_VALUE()) {
         return Long.MIN_VALUE;
      } else {
         StringBuffer var1 = this.getStringBuffer();
         var1.append((char[])this.digits, 0, this.count);

         for(int var2 = this.count; var2 < this.decimalAt; ++var2) {
            var1.append('0');
         }

         return Long.parseLong(var1.toString());
      }
   }

   public final BigDecimal getBigDecimal() {
      if (this.count == 0) {
         return this.decimalAt == 0 ? BigDecimal.ZERO : new BigDecimal("0E" + this.decimalAt);
      } else {
         return this.decimalAt == this.count ? new BigDecimal(this.digits, 0, this.count) : (new BigDecimal(this.digits, 0, this.count)).scaleByPowerOfTen(this.decimalAt - this.count);
      }
   }

   boolean fitsIntoLong(boolean var1, boolean var2) {
      while(this.count > 0 && this.digits[this.count - 1] == '0') {
         --this.count;
      }

      if (this.count != 0) {
         if (this.decimalAt >= this.count && this.decimalAt <= 19) {
            if (this.decimalAt < 19) {
               return true;
            } else {
               for(int var3 = 0; var3 < this.count; ++var3) {
                  char var4 = this.digits[var3];
                  char var5 = LONG_MIN_REP[var3];
                  if (var4 > var5) {
                     return false;
                  }

                  if (var4 < var5) {
                     return true;
                  }
               }

               if (this.count < this.decimalAt) {
                  return true;
               } else {
                  return !var1;
               }
            }
         } else {
            return false;
         }
      } else {
         return var1 || var2;
      }
   }

   final void set(boolean var1, double var2, int var4) {
      this.set(var1, var2, var4, true);
   }

   final void set(boolean var1, double var2, int var4, boolean var5) {
      FloatingDecimal.BinaryToASCIIConverter var6 = FloatingDecimal.getBinaryToASCIIConverter(var2);
      boolean var7 = var6.digitsRoundedUp();
      boolean var8 = var6.decimalDigitsExact();

      assert !var6.isExceptional();

      String var9 = var6.toJavaFormatString();
      this.set(var1, var9, var7, var8, var4, var5);
   }

   private void set(boolean var1, String var2, boolean var3, boolean var4, int var5, boolean var6) {
      this.isNegative = var1;
      int var7 = var2.length();
      char[] var8 = this.getDataChars(var7);
      var2.getChars(0, var7, var8, 0);
      this.decimalAt = -1;
      this.count = 0;
      int var9 = 0;
      int var10 = 0;
      boolean var11 = false;
      int var12 = 0;

      while(var12 < var7) {
         char var13 = var8[var12++];
         if (var13 == '.') {
            this.decimalAt = this.count;
         } else {
            if (var13 == 'e' || var13 == 'E') {
               var9 = parseInt(var8, var12, var7);
               break;
            }

            if (!var11) {
               var11 = var13 != '0';
               if (!var11 && this.decimalAt != -1) {
                  ++var10;
               }
            }

            if (var11) {
               this.digits[this.count++] = var13;
            }
         }
      }

      if (this.decimalAt == -1) {
         this.decimalAt = this.count;
      }

      if (var11) {
         this.decimalAt += var9 - var10;
      }

      if (var6) {
         if (-this.decimalAt > var5) {
            this.count = 0;
            return;
         }

         if (-this.decimalAt == var5) {
            if (this.shouldRoundUp(0, var3, var4)) {
               this.count = 1;
               ++this.decimalAt;
               this.digits[0] = '1';
            } else {
               this.count = 0;
            }

            return;
         }
      }

      while(this.count > 1 && this.digits[this.count - 1] == '0') {
         --this.count;
      }

      this.round(var6 ? var5 + this.decimalAt : var5, var3, var4);
   }

   private final void round(int var1, boolean var2, boolean var3) {
      if (var1 >= 0 && var1 < this.count) {
         if (this.shouldRoundUp(var1, var2, var3)) {
            do {
               --var1;
               if (var1 < 0) {
                  this.digits[0] = '1';
                  ++this.decimalAt;
                  var1 = 0;
                  break;
               }

               ++this.digits[var1];
            } while(this.digits[var1] > '9');

            ++var1;
         }

         for(this.count = var1; this.count > 1 && this.digits[this.count - 1] == '0'; --this.count) {
         }
      }

   }

   private boolean shouldRoundUp(int var1, boolean var2, boolean var3) {
      if (var1 < this.count) {
         int var4;
         switch(this.roundingMode) {
         case UP:
            for(var4 = var1; var4 < this.count; ++var4) {
               if (this.digits[var4] != '0') {
                  return true;
               }
            }
         case DOWN:
            break;
         case CEILING:
            for(var4 = var1; var4 < this.count; ++var4) {
               if (this.digits[var4] != '0') {
                  return !this.isNegative;
               }
            }

            return false;
         case FLOOR:
            for(var4 = var1; var4 < this.count; ++var4) {
               if (this.digits[var4] != '0') {
                  return this.isNegative;
               }
            }

            return false;
         case HALF_UP:
         case HALF_DOWN:
            if (this.digits[var1] > '5') {
               return true;
            }

            if (this.digits[var1] == '5') {
               if (var1 != this.count - 1) {
                  return true;
               }

               if (var3) {
                  return this.roundingMode == RoundingMode.HALF_UP;
               }

               return !var2;
            }
            break;
         case HALF_EVEN:
            if (this.digits[var1] > '5') {
               return true;
            }

            if (this.digits[var1] == '5') {
               if (var1 == this.count - 1) {
                  if (var2) {
                     return false;
                  }

                  if (!var3) {
                     return true;
                  }

                  return var1 > 0 && this.digits[var1 - 1] % 2 != 0;
               }

               for(var4 = var1 + 1; var4 < this.count; ++var4) {
                  if (this.digits[var4] != '0') {
                     return true;
                  }
               }
            }
            break;
         case UNNECESSARY:
            for(var4 = var1; var4 < this.count; ++var4) {
               if (this.digits[var4] != '0') {
                  throw new ArithmeticException("Rounding needed with the rounding mode being set to RoundingMode.UNNECESSARY");
               }
            }

            return false;
         default:
            assert false;
         }
      }

      return false;
   }

   final void set(boolean var1, long var2) {
      this.set(var1, var2, 0);
   }

   final void set(boolean var1, long var2, int var4) {
      this.isNegative = var1;
      if (var2 <= 0L) {
         if (var2 == Long.MIN_VALUE) {
            this.decimalAt = this.count = 19;
            System.arraycopy(LONG_MIN_REP, 0, this.digits, 0, this.count);
         } else {
            this.decimalAt = this.count = 0;
         }
      } else {
         int var5;
         for(var5 = 19; var2 > 0L; var2 /= 10L) {
            --var5;
            this.digits[var5] = (char)((int)(48L + var2 % 10L));
         }

         this.decimalAt = 19 - var5;

         int var6;
         for(var6 = 18; this.digits[var6] == '0'; --var6) {
         }

         this.count = var6 - var5 + 1;
         System.arraycopy(this.digits, var5, this.digits, 0, this.count);
      }

      if (var4 > 0) {
         this.round(var4, false, true);
      }

   }

   final void set(boolean var1, BigDecimal var2, int var3, boolean var4) {
      String var5 = var2.toString();
      this.extendDigits(var5.length());
      this.set(var1, var5, false, true, var3, var4);
   }

   final void set(boolean var1, BigInteger var2, int var3) {
      this.isNegative = var1;
      String var4 = var2.toString();
      int var5 = var4.length();
      this.extendDigits(var5);
      var4.getChars(0, var5, this.digits, 0);
      this.decimalAt = var5;

      int var6;
      for(var6 = var5 - 1; var6 >= 0 && this.digits[var6] == '0'; --var6) {
      }

      this.count = var6 + 1;
      if (var3 > 0) {
         this.round(var3, false, true);
      }

   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof DigitList)) {
         return false;
      } else {
         DigitList var2 = (DigitList)var1;
         if (this.count == var2.count && this.decimalAt == var2.decimalAt) {
            for(int var3 = 0; var3 < this.count; ++var3) {
               if (this.digits[var3] != var2.digits[var3]) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public int hashCode() {
      int var1 = this.decimalAt;

      for(int var2 = 0; var2 < this.count; ++var2) {
         var1 = var1 * 37 + this.digits[var2];
      }

      return var1;
   }

   public Object clone() {
      try {
         DigitList var1 = (DigitList)super.clone();
         char[] var2 = new char[this.digits.length];
         System.arraycopy(this.digits, 0, var2, 0, this.digits.length);
         var1.digits = var2;
         var1.tempBuffer = null;
         return var1;
      } catch (CloneNotSupportedException var3) {
         throw new InternalError(var3);
      }
   }

   private boolean isLongMIN_VALUE() {
      if (this.decimalAt == this.count && this.count == 19) {
         for(int var1 = 0; var1 < this.count; ++var1) {
            if (this.digits[var1] != LONG_MIN_REP[var1]) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static final int parseInt(char[] var0, int var1, int var2) {
      boolean var4 = true;
      char var3;
      if ((var3 = var0[var1]) == '-') {
         var4 = false;
         ++var1;
      } else if (var3 == '+') {
         ++var1;
      }

      int var5;
      for(var5 = 0; var1 < var2; var5 = var5 * 10 + (var3 - 48)) {
         var3 = var0[var1++];
         if (var3 < '0' || var3 > '9') {
            break;
         }
      }

      return var4 ? var5 : -var5;
   }

   public String toString() {
      if (this.isZero()) {
         return "0";
      } else {
         StringBuffer var1 = this.getStringBuffer();
         var1.append("0.");
         var1.append((char[])this.digits, 0, this.count);
         var1.append("x10^");
         var1.append(this.decimalAt);
         return var1.toString();
      }
   }

   private StringBuffer getStringBuffer() {
      if (this.tempBuffer == null) {
         this.tempBuffer = new StringBuffer(19);
      } else {
         this.tempBuffer.setLength(0);
      }

      return this.tempBuffer;
   }

   private void extendDigits(int var1) {
      if (var1 > this.digits.length) {
         this.digits = new char[var1];
      }

   }

   private final char[] getDataChars(int var1) {
      if (this.data == null || this.data.length < var1) {
         this.data = new char[var1];
      }

      return this.data;
   }
}
