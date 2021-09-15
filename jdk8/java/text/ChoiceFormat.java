package java.text;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.Arrays;

public class ChoiceFormat extends NumberFormat {
   private static final long serialVersionUID = 1795184449645032964L;
   private double[] choiceLimits;
   private String[] choiceFormats;
   static final long SIGN = Long.MIN_VALUE;
   static final long EXPONENT = 9218868437227405312L;
   static final long POSITIVEINFINITY = 9218868437227405312L;

   public void applyPattern(String var1) {
      StringBuffer[] var2 = new StringBuffer[2];

      for(int var3 = 0; var3 < var2.length; ++var3) {
         var2[var3] = new StringBuffer();
      }

      double[] var16 = new double[30];
      String[] var4 = new String[30];
      int var5 = 0;
      byte var6 = 0;
      double var7 = 0.0D;
      double var9 = Double.NaN;
      boolean var11 = false;

      for(int var12 = 0; var12 < var1.length(); ++var12) {
         char var13 = var1.charAt(var12);
         if (var13 == '\'') {
            if (var12 + 1 < var1.length() && var1.charAt(var12 + 1) == var13) {
               var2[var6].append(var13);
               ++var12;
            } else {
               var11 = !var11;
            }
         } else if (var11) {
            var2[var6].append(var13);
         } else if (var13 != '<' && var13 != '#' && var13 != 8804) {
            if (var13 == '|') {
               if (var5 == var16.length) {
                  var16 = doubleArraySize(var16);
                  var4 = this.doubleArraySize(var4);
               }

               var16[var5] = var7;
               var4[var5] = var2[1].toString();
               ++var5;
               var9 = var7;
               var2[1].setLength(0);
               var6 = 0;
            } else {
               var2[var6].append(var13);
            }
         } else {
            if (var2[0].length() == 0) {
               throw new IllegalArgumentException();
            }

            try {
               String var14 = var2[0].toString();
               if (var14.equals("∞")) {
                  var7 = Double.POSITIVE_INFINITY;
               } else if (var14.equals("-∞")) {
                  var7 = Double.NEGATIVE_INFINITY;
               } else {
                  var7 = Double.valueOf(var2[0].toString());
               }
            } catch (Exception var15) {
               throw new IllegalArgumentException();
            }

            if (var13 == '<' && var7 != Double.POSITIVE_INFINITY && var7 != Double.NEGATIVE_INFINITY) {
               var7 = nextDouble(var7);
            }

            if (var7 <= var9) {
               throw new IllegalArgumentException();
            }

            var2[0].setLength(0);
            var6 = 1;
         }
      }

      if (var6 == 1) {
         if (var5 == var16.length) {
            var16 = doubleArraySize(var16);
            var4 = this.doubleArraySize(var4);
         }

         var16[var5] = var7;
         var4[var5] = var2[1].toString();
         ++var5;
      }

      this.choiceLimits = new double[var5];
      System.arraycopy(var16, 0, this.choiceLimits, 0, var5);
      this.choiceFormats = new String[var5];
      System.arraycopy(var4, 0, this.choiceFormats, 0, var5);
   }

   public String toPattern() {
      StringBuffer var1 = new StringBuffer();

      for(int var2 = 0; var2 < this.choiceLimits.length; ++var2) {
         if (var2 != 0) {
            var1.append('|');
         }

         double var3 = previousDouble(this.choiceLimits[var2]);
         double var5 = Math.abs(Math.IEEEremainder(this.choiceLimits[var2], 1.0D));
         double var7 = Math.abs(Math.IEEEremainder(var3, 1.0D));
         if (var5 < var7) {
            var1.append("" + this.choiceLimits[var2]);
            var1.append('#');
         } else {
            if (this.choiceLimits[var2] == Double.POSITIVE_INFINITY) {
               var1.append("∞");
            } else if (this.choiceLimits[var2] == Double.NEGATIVE_INFINITY) {
               var1.append("-∞");
            } else {
               var1.append("" + var3);
            }

            var1.append('<');
         }

         String var9 = this.choiceFormats[var2];
         boolean var10 = var9.indexOf(60) >= 0 || var9.indexOf(35) >= 0 || var9.indexOf(8804) >= 0 || var9.indexOf(124) >= 0;
         if (var10) {
            var1.append('\'');
         }

         if (var9.indexOf(39) < 0) {
            var1.append(var9);
         } else {
            for(int var11 = 0; var11 < var9.length(); ++var11) {
               char var12 = var9.charAt(var11);
               var1.append(var12);
               if (var12 == '\'') {
                  var1.append(var12);
               }
            }
         }

         if (var10) {
            var1.append('\'');
         }
      }

      return var1.toString();
   }

   public ChoiceFormat(String var1) {
      this.applyPattern(var1);
   }

   public ChoiceFormat(double[] var1, String[] var2) {
      this.setChoices(var1, var2);
   }

   public void setChoices(double[] var1, String[] var2) {
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("Array and limit arrays must be of the same length.");
      } else {
         this.choiceLimits = Arrays.copyOf(var1, var1.length);
         this.choiceFormats = (String[])Arrays.copyOf((Object[])var2, var2.length);
      }
   }

   public double[] getLimits() {
      double[] var1 = Arrays.copyOf(this.choiceLimits, this.choiceLimits.length);
      return var1;
   }

   public Object[] getFormats() {
      Object[] var1 = Arrays.copyOf((Object[])this.choiceFormats, this.choiceFormats.length);
      return var1;
   }

   public StringBuffer format(long var1, StringBuffer var3, FieldPosition var4) {
      return this.format((double)var1, var3, var4);
   }

   public StringBuffer format(double var1, StringBuffer var3, FieldPosition var4) {
      int var5;
      for(var5 = 0; var5 < this.choiceLimits.length && var1 >= this.choiceLimits[var5]; ++var5) {
      }

      --var5;
      if (var5 < 0) {
         var5 = 0;
      }

      return var3.append(this.choiceFormats[var5]);
   }

   public Number parse(String var1, ParsePosition var2) {
      int var3 = var2.index;
      int var4 = var3;
      double var5 = Double.NaN;
      double var7 = 0.0D;

      for(int var9 = 0; var9 < this.choiceFormats.length; ++var9) {
         String var10 = this.choiceFormats[var9];
         if (var1.regionMatches(var3, var10, 0, var10.length())) {
            var2.index = var3 + var10.length();
            var7 = this.choiceLimits[var9];
            if (var2.index > var4) {
               var4 = var2.index;
               var5 = var7;
               if (var4 == var1.length()) {
                  break;
               }
            }
         }
      }

      var2.index = var4;
      if (var2.index == var3) {
         var2.errorIndex = var4;
      }

      return new Double(var5);
   }

   public static final double nextDouble(double var0) {
      return nextDouble(var0, true);
   }

   public static final double previousDouble(double var0) {
      return nextDouble(var0, false);
   }

   public Object clone() {
      ChoiceFormat var1 = (ChoiceFormat)super.clone();
      var1.choiceLimits = (double[])this.choiceLimits.clone();
      var1.choiceFormats = (String[])this.choiceFormats.clone();
      return var1;
   }

   public int hashCode() {
      int var1 = this.choiceLimits.length;
      if (this.choiceFormats.length > 0) {
         var1 ^= this.choiceFormats[this.choiceFormats.length - 1].hashCode();
      }

      return var1;
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (this == var1) {
         return true;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         ChoiceFormat var2 = (ChoiceFormat)var1;
         return Arrays.equals(this.choiceLimits, var2.choiceLimits) && Arrays.equals((Object[])this.choiceFormats, (Object[])var2.choiceFormats);
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      if (this.choiceLimits.length != this.choiceFormats.length) {
         throw new InvalidObjectException("limits and format arrays of different length.");
      }
   }

   public static double nextDouble(double var0, boolean var2) {
      if (Double.isNaN(var0)) {
         return var0;
      } else if (var0 == 0.0D) {
         double var9 = Double.longBitsToDouble(1L);
         return var2 ? var9 : -var9;
      } else {
         long var3 = Double.doubleToLongBits(var0);
         long var5 = var3 & Long.MAX_VALUE;
         if (var3 > 0L == var2) {
            if (var5 != 9218868437227405312L) {
               ++var5;
            }
         } else {
            --var5;
         }

         long var7 = var3 & Long.MIN_VALUE;
         return Double.longBitsToDouble(var5 | var7);
      }
   }

   private static double[] doubleArraySize(double[] var0) {
      int var1 = var0.length;
      double[] var2 = new double[var1 * 2];
      System.arraycopy(var0, 0, var2, 0, var1);
      return var2;
   }

   private String[] doubleArraySize(String[] var1) {
      int var2 = var1.length;
      String[] var3 = new String[var2 * 2];
      System.arraycopy(var1, 0, var3, 0, var2);
      return var3;
   }
}
