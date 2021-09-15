package sun.misc;

import java.util.Arrays;

public class FormattedFloatingDecimal {
   private int decExponentRounded;
   private char[] mantissa;
   private char[] exponent;
   private static final ThreadLocal<Object> threadLocalCharBuffer = new ThreadLocal<Object>() {
      protected Object initialValue() {
         return new char[20];
      }
   };

   public static FormattedFloatingDecimal valueOf(double var0, int var2, FormattedFloatingDecimal.Form var3) {
      FloatingDecimal.BinaryToASCIIConverter var4 = FloatingDecimal.getBinaryToASCIIConverter(var0, var3 == FormattedFloatingDecimal.Form.COMPATIBLE);
      return new FormattedFloatingDecimal(var2, var3, var4);
   }

   private static char[] getBuffer() {
      return (char[])((char[])threadLocalCharBuffer.get());
   }

   private FormattedFloatingDecimal(int var1, FormattedFloatingDecimal.Form var2, FloatingDecimal.BinaryToASCIIConverter var3) {
      if (var3.isExceptional()) {
         this.mantissa = var3.toJavaFormatString().toCharArray();
         this.exponent = null;
      } else {
         char[] var4 = getBuffer();
         int var5 = var3.getDigits(var4);
         int var6 = var3.getDecimalExponent();
         boolean var8 = var3.isNegative();
         int var7;
         switch(var2) {
         case COMPATIBLE:
            this.decExponentRounded = var6;
            this.fillCompatible(var1, var4, var5, var6, var8);
            break;
         case DECIMAL_FLOAT:
            var7 = applyPrecision(var6, var4, var5, var6 + var1);
            this.fillDecimal(var1, var4, var5, var7, var8);
            this.decExponentRounded = var7;
            break;
         case SCIENTIFIC:
            var7 = applyPrecision(var6, var4, var5, var1 + 1);
            this.fillScientific(var1, var4, var5, var7, var8);
            this.decExponentRounded = var7;
            break;
         case GENERAL:
            var7 = applyPrecision(var6, var4, var5, var1);
            if (var7 - 1 >= -4 && var7 - 1 < var1) {
               var1 -= var7;
               this.fillDecimal(var1, var4, var5, var7, var8);
            } else {
               --var1;
               this.fillScientific(var1, var4, var5, var7, var8);
            }

            this.decExponentRounded = var7;
            break;
         default:
            assert false;
         }

      }
   }

   public int getExponentRounded() {
      return this.decExponentRounded - 1;
   }

   public char[] getMantissa() {
      return this.mantissa;
   }

   public char[] getExponent() {
      return this.exponent;
   }

   private static int applyPrecision(int var0, char[] var1, int var2, int var3) {
      if (var3 < var2 && var3 >= 0) {
         if (var3 == 0) {
            if (var1[0] >= '5') {
               var1[0] = '1';
               Arrays.fill((char[])var1, 1, var2, (char)'0');
               return var0 + 1;
            } else {
               Arrays.fill((char[])var1, 0, var2, (char)'0');
               return var0;
            }
         } else {
            char var4 = var1[var3];
            if (var4 >= '5') {
               int var5 = var3 - 1;
               var4 = var1[var5];
               if (var4 == '9') {
                  while(var4 == '9' && var5 > 0) {
                     --var5;
                     var4 = var1[var5];
                  }

                  if (var4 == '9') {
                     var1[0] = '1';
                     Arrays.fill((char[])var1, 1, var2, (char)'0');
                     return var0 + 1;
                  }
               }

               var1[var5] = (char)(var4 + 1);
               Arrays.fill(var1, var5 + 1, var2, '0');
            } else {
               Arrays.fill(var1, var3, var2, '0');
            }

            return var0;
         }
      } else {
         return var0;
      }
   }

   private void fillCompatible(int var1, char[] var2, int var3, int var4, boolean var5) {
      int var6 = var5 ? 1 : 0;
      int var7;
      if (var4 > 0 && var4 < 8) {
         if (var3 < var4) {
            var7 = var4 - var3;
            this.mantissa = create(var5, var3 + var7 + 2);
            System.arraycopy(var2, 0, this.mantissa, var6, var3);
            Arrays.fill(this.mantissa, var6 + var3, var6 + var3 + var7, '0');
            this.mantissa[var6 + var3 + var7] = '.';
            this.mantissa[var6 + var3 + var7 + 1] = '0';
         } else if (var4 < var3) {
            var7 = Math.min(var3 - var4, var1);
            this.mantissa = create(var5, var4 + 1 + var7);
            System.arraycopy(var2, 0, this.mantissa, var6, var4);
            this.mantissa[var6 + var4] = '.';
            System.arraycopy(var2, var4, this.mantissa, var6 + var4 + 1, var7);
         } else {
            this.mantissa = create(var5, var3 + 2);
            System.arraycopy(var2, 0, this.mantissa, var6, var3);
            this.mantissa[var6 + var3] = '.';
            this.mantissa[var6 + var3 + 1] = '0';
         }
      } else if (var4 <= 0 && var4 > -3) {
         var7 = Math.max(0, Math.min(-var4, var1));
         int var10 = Math.max(0, Math.min(var3, var1 + var4));
         if (var7 > 0) {
            this.mantissa = create(var5, var7 + 2 + var10);
            this.mantissa[var6] = '0';
            this.mantissa[var6 + 1] = '.';
            Arrays.fill(this.mantissa, var6 + 2, var6 + 2 + var7, '0');
            if (var10 > 0) {
               System.arraycopy(var2, 0, this.mantissa, var6 + 2 + var7, var10);
            }
         } else if (var10 > 0) {
            this.mantissa = create(var5, var7 + 2 + var10);
            this.mantissa[var6] = '0';
            this.mantissa[var6 + 1] = '.';
            System.arraycopy(var2, 0, this.mantissa, var6 + 2, var10);
         } else {
            this.mantissa = create(var5, 1);
            this.mantissa[var6] = '0';
         }
      } else {
         if (var3 > 1) {
            this.mantissa = create(var5, var3 + 1);
            this.mantissa[var6] = var2[0];
            this.mantissa[var6 + 1] = '.';
            System.arraycopy(var2, 1, this.mantissa, var6 + 2, var3 - 1);
         } else {
            this.mantissa = create(var5, 3);
            this.mantissa[var6] = var2[0];
            this.mantissa[var6 + 1] = '.';
            this.mantissa[var6 + 2] = '0';
         }

         boolean var9 = var4 <= 0;
         byte var8;
         if (var9) {
            var7 = -var4 + 1;
            var8 = 1;
         } else {
            var7 = var4 - 1;
            var8 = 0;
         }

         if (var7 <= 9) {
            this.exponent = create(var9, 1);
            this.exponent[var8] = (char)(var7 + 48);
         } else if (var7 <= 99) {
            this.exponent = create(var9, 2);
            this.exponent[var8] = (char)(var7 / 10 + 48);
            this.exponent[var8 + 1] = (char)(var7 % 10 + 48);
         } else {
            this.exponent = create(var9, 3);
            this.exponent[var8] = (char)(var7 / 100 + 48);
            var7 %= 100;
            this.exponent[var8 + 1] = (char)(var7 / 10 + 48);
            this.exponent[var8 + 2] = (char)(var7 % 10 + 48);
         }
      }

   }

   private static char[] create(boolean var0, int var1) {
      if (var0) {
         char[] var2 = new char[var1 + 1];
         var2[0] = '-';
         return var2;
      } else {
         return new char[var1];
      }
   }

   private void fillDecimal(int var1, char[] var2, int var3, int var4, boolean var5) {
      int var6 = var5 ? 1 : 0;
      int var7;
      if (var4 > 0) {
         if (var3 < var4) {
            this.mantissa = create(var5, var4);
            System.arraycopy(var2, 0, this.mantissa, var6, var3);
            Arrays.fill(this.mantissa, var6 + var3, var6 + var4, '0');
         } else {
            var7 = Math.min(var3 - var4, var1);
            this.mantissa = create(var5, var4 + (var7 > 0 ? var7 + 1 : 0));
            System.arraycopy(var2, 0, this.mantissa, var6, var4);
            if (var7 > 0) {
               this.mantissa[var6 + var4] = '.';
               System.arraycopy(var2, var4, this.mantissa, var6 + var4 + 1, var7);
            }
         }
      } else if (var4 <= 0) {
         var7 = Math.max(0, Math.min(-var4, var1));
         int var8 = Math.max(0, Math.min(var3, var1 + var4));
         if (var7 > 0) {
            this.mantissa = create(var5, var7 + 2 + var8);
            this.mantissa[var6] = '0';
            this.mantissa[var6 + 1] = '.';
            Arrays.fill(this.mantissa, var6 + 2, var6 + 2 + var7, '0');
            if (var8 > 0) {
               System.arraycopy(var2, 0, this.mantissa, var6 + 2 + var7, var8);
            }
         } else if (var8 > 0) {
            this.mantissa = create(var5, var7 + 2 + var8);
            this.mantissa[var6] = '0';
            this.mantissa[var6 + 1] = '.';
            System.arraycopy(var2, 0, this.mantissa, var6 + 2, var8);
         } else {
            this.mantissa = create(var5, 1);
            this.mantissa[var6] = '0';
         }
      }

   }

   private void fillScientific(int var1, char[] var2, int var3, int var4, boolean var5) {
      int var6 = var5 ? 1 : 0;
      int var7 = Math.max(0, Math.min(var3 - 1, var1));
      if (var7 > 0) {
         this.mantissa = create(var5, var7 + 2);
         this.mantissa[var6] = var2[0];
         this.mantissa[var6 + 1] = '.';
         System.arraycopy(var2, 1, this.mantissa, var6 + 2, var7);
      } else {
         this.mantissa = create(var5, 1);
         this.mantissa[var6] = var2[0];
      }

      char var8;
      int var9;
      if (var4 <= 0) {
         var8 = '-';
         var9 = -var4 + 1;
      } else {
         var8 = '+';
         var9 = var4 - 1;
      }

      if (var9 <= 9) {
         this.exponent = new char[]{var8, '0', (char)(var9 + 48)};
      } else if (var9 <= 99) {
         this.exponent = new char[]{var8, (char)(var9 / 10 + 48), (char)(var9 % 10 + 48)};
      } else {
         char var10 = (char)(var9 / 100 + 48);
         var9 %= 100;
         this.exponent = new char[]{var8, var10, (char)(var9 / 10 + 48), (char)(var9 % 10 + 48)};
      }

   }

   public static enum Form {
      SCIENTIFIC,
      COMPATIBLE,
      DECIMAL_FLOAT,
      GENERAL;
   }
}
