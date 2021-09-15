package java.text;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.spi.NumberFormatProvider;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.ResourceBundleBasedAdapter;

public class DecimalFormat extends NumberFormat {
   private transient BigInteger bigIntegerMultiplier;
   private transient BigDecimal bigDecimalMultiplier;
   private static final int STATUS_INFINITE = 0;
   private static final int STATUS_POSITIVE = 1;
   private static final int STATUS_LENGTH = 2;
   private transient DigitList digitList = new DigitList();
   private String positivePrefix = "";
   private String positiveSuffix = "";
   private String negativePrefix = "-";
   private String negativeSuffix = "";
   private String posPrefixPattern;
   private String posSuffixPattern;
   private String negPrefixPattern;
   private String negSuffixPattern;
   private int multiplier = 1;
   private byte groupingSize = 3;
   private boolean decimalSeparatorAlwaysShown = false;
   private boolean parseBigDecimal = false;
   private transient boolean isCurrencyFormat = false;
   private DecimalFormatSymbols symbols = null;
   private boolean useExponentialNotation;
   private transient FieldPosition[] positivePrefixFieldPositions;
   private transient FieldPosition[] positiveSuffixFieldPositions;
   private transient FieldPosition[] negativePrefixFieldPositions;
   private transient FieldPosition[] negativeSuffixFieldPositions;
   private byte minExponentDigits;
   private int maximumIntegerDigits = super.getMaximumIntegerDigits();
   private int minimumIntegerDigits = super.getMinimumIntegerDigits();
   private int maximumFractionDigits = super.getMaximumFractionDigits();
   private int minimumFractionDigits = super.getMinimumFractionDigits();
   private RoundingMode roundingMode;
   private transient boolean isFastPath;
   private transient boolean fastPathCheckNeeded;
   private transient DecimalFormat.FastPathData fastPathData;
   static final int currentSerialVersion = 4;
   private int serialVersionOnStream;
   private static final double MAX_INT_AS_DOUBLE = 2.147483647E9D;
   private static final char PATTERN_ZERO_DIGIT = '0';
   private static final char PATTERN_GROUPING_SEPARATOR = ',';
   private static final char PATTERN_DECIMAL_SEPARATOR = '.';
   private static final char PATTERN_PER_MILLE = '‰';
   private static final char PATTERN_PERCENT = '%';
   private static final char PATTERN_DIGIT = '#';
   private static final char PATTERN_SEPARATOR = ';';
   private static final String PATTERN_EXPONENT = "E";
   private static final char PATTERN_MINUS = '-';
   private static final char CURRENCY_SIGN = '¤';
   private static final char QUOTE = '\'';
   private static FieldPosition[] EmptyFieldPositionArray = new FieldPosition[0];
   static final int DOUBLE_INTEGER_DIGITS = 309;
   static final int DOUBLE_FRACTION_DIGITS = 340;
   static final int MAXIMUM_INTEGER_DIGITS = Integer.MAX_VALUE;
   static final int MAXIMUM_FRACTION_DIGITS = Integer.MAX_VALUE;
   static final long serialVersionUID = 864413376551465018L;

   public DecimalFormat() {
      this.roundingMode = RoundingMode.HALF_EVEN;
      this.isFastPath = false;
      this.fastPathCheckNeeded = true;
      this.serialVersionOnStream = 4;
      Locale var1 = Locale.getDefault(Locale.Category.FORMAT);
      LocaleProviderAdapter var2 = LocaleProviderAdapter.getAdapter(NumberFormatProvider.class, var1);
      if (!(var2 instanceof ResourceBundleBasedAdapter)) {
         var2 = LocaleProviderAdapter.getResourceBundleBased();
      }

      String[] var3 = var2.getLocaleResources(var1).getNumberPatterns();
      this.symbols = DecimalFormatSymbols.getInstance(var1);
      this.applyPattern(var3[0], false);
   }

   public DecimalFormat(String var1) {
      this.roundingMode = RoundingMode.HALF_EVEN;
      this.isFastPath = false;
      this.fastPathCheckNeeded = true;
      this.serialVersionOnStream = 4;
      this.symbols = DecimalFormatSymbols.getInstance(Locale.getDefault(Locale.Category.FORMAT));
      this.applyPattern(var1, false);
   }

   public DecimalFormat(String var1, DecimalFormatSymbols var2) {
      this.roundingMode = RoundingMode.HALF_EVEN;
      this.isFastPath = false;
      this.fastPathCheckNeeded = true;
      this.serialVersionOnStream = 4;
      this.symbols = (DecimalFormatSymbols)var2.clone();
      this.applyPattern(var1, false);
   }

   public final StringBuffer format(Object var1, StringBuffer var2, FieldPosition var3) {
      if (!(var1 instanceof Long) && !(var1 instanceof Integer) && !(var1 instanceof Short) && !(var1 instanceof Byte) && !(var1 instanceof AtomicInteger) && !(var1 instanceof AtomicLong) && (!(var1 instanceof BigInteger) || ((BigInteger)var1).bitLength() >= 64)) {
         if (var1 instanceof BigDecimal) {
            return this.format((BigDecimal)var1, var2, var3);
         } else if (var1 instanceof BigInteger) {
            return this.format((BigInteger)var1, var2, var3);
         } else if (var1 instanceof Number) {
            return this.format(((Number)var1).doubleValue(), var2, var3);
         } else {
            throw new IllegalArgumentException("Cannot format given Object as a Number");
         }
      } else {
         return this.format(((Number)var1).longValue(), var2, var3);
      }
   }

   public StringBuffer format(double var1, StringBuffer var3, FieldPosition var4) {
      boolean var5 = false;
      if (var4 == DontCareFieldPosition.INSTANCE) {
         var5 = true;
      } else {
         var4.setBeginIndex(0);
         var4.setEndIndex(0);
      }

      if (var5) {
         String var6 = this.fastFormat(var1);
         if (var6 != null) {
            var3.append(var6);
            return var3;
         }
      }

      return this.format(var1, var3, var4.getFieldDelegate());
   }

   private StringBuffer format(double var1, StringBuffer var3, Format.FieldDelegate var4) {
      if (!Double.isNaN(var1) && (!Double.isInfinite(var1) || this.multiplier != 0)) {
         boolean var13 = (var1 < 0.0D || var1 == 0.0D && 1.0D / var1 < 0.0D) ^ this.multiplier < 0;
         if (this.multiplier != 1) {
            var1 *= (double)this.multiplier;
         }

         if (Double.isInfinite(var1)) {
            if (var13) {
               this.append(var3, this.negativePrefix, var4, this.getNegativePrefixFieldPositions(), NumberFormat.Field.SIGN);
            } else {
               this.append(var3, this.positivePrefix, var4, this.getPositivePrefixFieldPositions(), NumberFormat.Field.SIGN);
            }

            int var6 = var3.length();
            var3.append(this.symbols.getInfinity());
            var4.formatted(0, NumberFormat.Field.INTEGER, NumberFormat.Field.INTEGER, var6, var3.length(), var3);
            if (var13) {
               this.append(var3, this.negativeSuffix, var4, this.getNegativeSuffixFieldPositions(), NumberFormat.Field.SIGN);
            } else {
               this.append(var3, this.positiveSuffix, var4, this.getPositiveSuffixFieldPositions(), NumberFormat.Field.SIGN);
            }

            return var3;
         } else {
            if (var13) {
               var1 = -var1;
            }

            assert var1 >= 0.0D && !Double.isInfinite(var1);

            synchronized(this.digitList) {
               int var7 = super.getMaximumIntegerDigits();
               int var8 = super.getMinimumIntegerDigits();
               int var9 = super.getMaximumFractionDigits();
               int var10 = super.getMinimumFractionDigits();
               this.digitList.set(var13, var1, this.useExponentialNotation ? var7 + var9 : var9, !this.useExponentialNotation);
               return this.subformat(var3, var4, var13, false, var7, var8, var9, var10);
            }
         }
      } else {
         int var5 = var3.length();
         var3.append(this.symbols.getNaN());
         var4.formatted(0, NumberFormat.Field.INTEGER, NumberFormat.Field.INTEGER, var5, var3.length(), var3);
         return var3;
      }
   }

   public StringBuffer format(long var1, StringBuffer var3, FieldPosition var4) {
      var4.setBeginIndex(0);
      var4.setEndIndex(0);
      return this.format(var1, var3, var4.getFieldDelegate());
   }

   private StringBuffer format(long var1, StringBuffer var3, Format.FieldDelegate var4) {
      boolean var5 = var1 < 0L;
      if (var5) {
         var1 = -var1;
      }

      boolean var6 = false;
      if (var1 < 0L) {
         if (this.multiplier != 0) {
            var6 = true;
         }
      } else if (this.multiplier != 1 && this.multiplier != 0) {
         long var7 = Long.MAX_VALUE / (long)this.multiplier;
         if (var7 < 0L) {
            var7 = -var7;
         }

         var6 = var1 > var7;
      }

      if (var6) {
         if (var5) {
            var1 = -var1;
         }

         BigInteger var14 = BigInteger.valueOf(var1);
         return this.format(var14, var3, var4, true);
      } else {
         var1 *= (long)this.multiplier;
         if (var1 == 0L) {
            var5 = false;
         } else if (this.multiplier < 0) {
            var1 = -var1;
            var5 = !var5;
         }

         synchronized(this.digitList) {
            int var8 = super.getMaximumIntegerDigits();
            int var9 = super.getMinimumIntegerDigits();
            int var10 = super.getMaximumFractionDigits();
            int var11 = super.getMinimumFractionDigits();
            this.digitList.set(var5, var1, this.useExponentialNotation ? var8 + var10 : 0);
            return this.subformat(var3, var4, var5, true, var8, var9, var10, var11);
         }
      }
   }

   private StringBuffer format(BigDecimal var1, StringBuffer var2, FieldPosition var3) {
      var3.setBeginIndex(0);
      var3.setEndIndex(0);
      return this.format(var1, var2, var3.getFieldDelegate());
   }

   private StringBuffer format(BigDecimal var1, StringBuffer var2, Format.FieldDelegate var3) {
      if (this.multiplier != 1) {
         var1 = var1.multiply(this.getBigDecimalMultiplier());
      }

      boolean var4 = var1.signum() == -1;
      if (var4) {
         var1 = var1.negate();
      }

      synchronized(this.digitList) {
         int var6 = this.getMaximumIntegerDigits();
         int var7 = this.getMinimumIntegerDigits();
         int var8 = this.getMaximumFractionDigits();
         int var9 = this.getMinimumFractionDigits();
         int var10 = var6 + var8;
         this.digitList.set(var4, var1, this.useExponentialNotation ? (var10 < 0 ? Integer.MAX_VALUE : var10) : var8, !this.useExponentialNotation);
         return this.subformat(var2, var3, var4, false, var6, var7, var8, var9);
      }
   }

   private StringBuffer format(BigInteger var1, StringBuffer var2, FieldPosition var3) {
      var3.setBeginIndex(0);
      var3.setEndIndex(0);
      return this.format(var1, var2, var3.getFieldDelegate(), false);
   }

   private StringBuffer format(BigInteger var1, StringBuffer var2, Format.FieldDelegate var3, boolean var4) {
      if (this.multiplier != 1) {
         var1 = var1.multiply(this.getBigIntegerMultiplier());
      }

      boolean var5 = var1.signum() == -1;
      if (var5) {
         var1 = var1.negate();
      }

      synchronized(this.digitList) {
         int var7;
         int var8;
         int var9;
         int var10;
         int var11;
         if (var4) {
            var7 = super.getMaximumIntegerDigits();
            var8 = super.getMinimumIntegerDigits();
            var9 = super.getMaximumFractionDigits();
            var10 = super.getMinimumFractionDigits();
            var11 = var7 + var9;
         } else {
            var7 = this.getMaximumIntegerDigits();
            var8 = this.getMinimumIntegerDigits();
            var9 = this.getMaximumFractionDigits();
            var10 = this.getMinimumFractionDigits();
            var11 = var7 + var9;
            if (var11 < 0) {
               var11 = Integer.MAX_VALUE;
            }
         }

         this.digitList.set(var5, var1, this.useExponentialNotation ? var11 : 0);
         return this.subformat(var2, var3, var5, true, var7, var8, var9, var10);
      }
   }

   public AttributedCharacterIterator formatToCharacterIterator(Object var1) {
      CharacterIteratorFieldDelegate var2 = new CharacterIteratorFieldDelegate();
      StringBuffer var3 = new StringBuffer();
      if (!(var1 instanceof Double) && !(var1 instanceof Float)) {
         if (!(var1 instanceof Long) && !(var1 instanceof Integer) && !(var1 instanceof Short) && !(var1 instanceof Byte) && !(var1 instanceof AtomicInteger) && !(var1 instanceof AtomicLong)) {
            if (var1 instanceof BigDecimal) {
               this.format((BigDecimal)((BigDecimal)var1), var3, (Format.FieldDelegate)var2);
            } else {
               if (!(var1 instanceof BigInteger)) {
                  if (var1 == null) {
                     throw new NullPointerException("formatToCharacterIterator must be passed non-null object");
                  }

                  throw new IllegalArgumentException("Cannot format given Object as a Number");
               }

               this.format((BigInteger)var1, var3, var2, false);
            }
         } else {
            this.format(((Number)var1).longValue(), var3, (Format.FieldDelegate)var2);
         }
      } else {
         this.format(((Number)var1).doubleValue(), var3, (Format.FieldDelegate)var2);
      }

      return var2.getIterator(var3.toString());
   }

   private boolean checkAndSetFastPathStatus() {
      boolean var1 = this.isFastPath;
      if (this.roundingMode == RoundingMode.HALF_EVEN && this.isGroupingUsed() && this.groupingSize == 3 && this.multiplier == 1 && !this.decimalSeparatorAlwaysShown && !this.useExponentialNotation) {
         this.isFastPath = this.minimumIntegerDigits == 1 && this.maximumIntegerDigits >= 10;
         if (this.isFastPath) {
            if (this.isCurrencyFormat) {
               if (this.minimumFractionDigits != 2 || this.maximumFractionDigits != 2) {
                  this.isFastPath = false;
               }
            } else if (this.minimumFractionDigits != 0 || this.maximumFractionDigits != 3) {
               this.isFastPath = false;
            }
         }
      } else {
         this.isFastPath = false;
      }

      this.resetFastPathData(var1);
      this.fastPathCheckNeeded = false;
      return true;
   }

   private void resetFastPathData(boolean var1) {
      if (this.isFastPath) {
         if (this.fastPathData == null) {
            this.fastPathData = new DecimalFormat.FastPathData();
         }

         this.fastPathData.zeroDelta = this.symbols.getZeroDigit() - 48;
         this.fastPathData.groupingChar = this.symbols.getGroupingSeparator();
         this.fastPathData.fractionalMaxIntBound = this.isCurrencyFormat ? 99 : 999;
         this.fastPathData.fractionalScaleFactor = this.isCurrencyFormat ? 100.0D : 1000.0D;
         this.fastPathData.positiveAffixesRequired = this.positivePrefix.length() != 0 || this.positiveSuffix.length() != 0;
         this.fastPathData.negativeAffixesRequired = this.negativePrefix.length() != 0 || this.negativeSuffix.length() != 0;
         byte var2 = 10;
         byte var3 = 3;
         int var4 = Math.max(this.positivePrefix.length(), this.negativePrefix.length()) + var2 + var3 + 1 + this.maximumFractionDigits + Math.max(this.positiveSuffix.length(), this.negativeSuffix.length());
         this.fastPathData.fastPathContainer = new char[var4];
         this.fastPathData.charsPositiveSuffix = this.positiveSuffix.toCharArray();
         this.fastPathData.charsNegativeSuffix = this.negativeSuffix.toCharArray();
         this.fastPathData.charsPositivePrefix = this.positivePrefix.toCharArray();
         this.fastPathData.charsNegativePrefix = this.negativePrefix.toCharArray();
         int var5 = Math.max(this.positivePrefix.length(), this.negativePrefix.length());
         int var6 = var2 + var3 + var5;
         this.fastPathData.integralLastIndex = var6 - 1;
         this.fastPathData.fractionalFirstIndex = var6 + 1;
         this.fastPathData.fastPathContainer[var6] = this.isCurrencyFormat ? this.symbols.getMonetaryDecimalSeparator() : this.symbols.getDecimalSeparator();
      } else if (var1) {
         this.fastPathData.fastPathContainer = null;
         this.fastPathData.charsPositiveSuffix = null;
         this.fastPathData.charsNegativeSuffix = null;
         this.fastPathData.charsPositivePrefix = null;
         this.fastPathData.charsNegativePrefix = null;
      }

   }

   private boolean exactRoundUp(double var1, int var3) {
      double var10 = 0.0D;
      double var12 = 0.0D;
      double var14 = 0.0D;
      double var4;
      double var6;
      double var8;
      if (this.isCurrencyFormat) {
         var4 = var1 * 128.0D;
         var6 = -(var1 * 32.0D);
         var8 = var1 * 4.0D;
      } else {
         var4 = var1 * 1024.0D;
         var6 = -(var1 * 16.0D);
         var8 = -(var1 * 8.0D);
      }

      assert -var6 >= Math.abs(var8);

      var10 = var6 + var8;
      var14 = var10 - var6;
      var12 = var8 - var14;
      double var16 = var10;
      double var18 = var12;

      assert var4 >= Math.abs(var10);

      var10 += var4;
      var14 = var10 - var4;
      var12 = var16 - var14;
      double var22 = var10;
      double var24 = var18 + var12;

      assert var10 >= Math.abs(var24);

      var10 += var24;
      var14 = var10 - var22;
      double var26 = var24 - var14;
      if (var26 > 0.0D) {
         return true;
      } else if (var26 < 0.0D) {
         return false;
      } else {
         return (var3 & 1) != 0;
      }
   }

   private void collectIntegralDigits(int var1, char[] var2, int var3) {
      int var4;
      for(var4 = var3; var1 > 999; var2[var4--] = this.fastPathData.groupingChar) {
         int var5 = var1 / 1000;
         int var6 = var1 - (var5 << 10) + (var5 << 4) + (var5 << 3);
         var1 = var5;
         var2[var4--] = DecimalFormat.DigitArrays.DigitOnes1000[var6];
         var2[var4--] = DecimalFormat.DigitArrays.DigitTens1000[var6];
         var2[var4--] = DecimalFormat.DigitArrays.DigitHundreds1000[var6];
      }

      var2[var4] = DecimalFormat.DigitArrays.DigitOnes1000[var1];
      if (var1 > 9) {
         --var4;
         var2[var4] = DecimalFormat.DigitArrays.DigitTens1000[var1];
         if (var1 > 99) {
            --var4;
            var2[var4] = DecimalFormat.DigitArrays.DigitHundreds1000[var1];
         }
      }

      this.fastPathData.firstUsedIndex = var4;
   }

   private void collectFractionalDigits(int var1, char[] var2, int var3) {
      char var5 = DecimalFormat.DigitArrays.DigitOnes1000[var1];
      char var6 = DecimalFormat.DigitArrays.DigitTens1000[var1];
      int var4;
      if (this.isCurrencyFormat) {
         var4 = var3 + 1;
         var2[var3] = var6;
         var2[var4++] = var5;
      } else if (var1 != 0) {
         var4 = var3 + 1;
         var2[var3] = DecimalFormat.DigitArrays.DigitHundreds1000[var1];
         if (var5 != '0') {
            var2[var4++] = var6;
            var2[var4++] = var5;
         } else if (var6 != '0') {
            var2[var4++] = var6;
         }
      } else {
         var4 = var3 - 1;
      }

      this.fastPathData.lastFreeIndex = var4;
   }

   private void addAffixes(char[] var1, char[] var2, char[] var3) {
      int var4 = var2.length;
      int var5 = var3.length;
      if (var4 != 0) {
         this.prependPrefix(var2, var4, var1);
      }

      if (var5 != 0) {
         this.appendSuffix(var3, var5, var1);
      }

   }

   private void prependPrefix(char[] var1, int var2, char[] var3) {
      DecimalFormat.FastPathData var10000 = this.fastPathData;
      var10000.firstUsedIndex -= var2;
      int var4 = this.fastPathData.firstUsedIndex;
      if (var2 == 1) {
         var3[var4] = var1[0];
      } else if (var2 <= 4) {
         int var6 = var4 + var2 - 1;
         int var7 = var2 - 1;
         var3[var4] = var1[0];
         var3[var6] = var1[var7];
         if (var2 > 2) {
            int var5 = var4 + 1;
            var3[var5] = var1[1];
         }

         if (var2 == 4) {
            --var6;
            var3[var6] = var1[2];
         }
      } else {
         System.arraycopy(var1, 0, var3, var4, var2);
      }

   }

   private void appendSuffix(char[] var1, int var2, char[] var3) {
      int var4 = this.fastPathData.lastFreeIndex;
      if (var2 == 1) {
         var3[var4] = var1[0];
      } else if (var2 <= 4) {
         int var6 = var4 + var2 - 1;
         int var7 = var2 - 1;
         var3[var4] = var1[0];
         var3[var6] = var1[var7];
         if (var2 > 2) {
            int var5 = var4 + 1;
            var3[var5] = var1[1];
         }

         if (var2 == 4) {
            --var6;
            var3[var6] = var1[2];
         }
      } else {
         System.arraycopy(var1, 0, var3, var4, var2);
      }

      DecimalFormat.FastPathData var10000 = this.fastPathData;
      var10000.lastFreeIndex += var2;
   }

   private void localizeDigits(char[] var1) {
      int var2 = this.fastPathData.lastFreeIndex - this.fastPathData.fractionalFirstIndex;
      if (var2 < 0) {
         var2 = this.groupingSize;
      }

      for(int var3 = this.fastPathData.lastFreeIndex - 1; var3 >= this.fastPathData.firstUsedIndex; --var3) {
         if (var2 != 0) {
            var1[var3] = (char)(var1[var3] + this.fastPathData.zeroDelta);
            --var2;
         } else {
            var2 = this.groupingSize;
         }
      }

   }

   private void fastDoubleFormat(double var1, boolean var3) {
      char[] var4 = this.fastPathData.fastPathContainer;
      int var5 = (int)var1;
      double var6 = var1 - (double)var5;
      double var8 = var6 * this.fastPathData.fractionalScaleFactor;
      int var10 = (int)var8;
      var8 -= (double)var10;
      boolean var11 = false;
      if (var8 >= 0.5D) {
         if (var8 == 0.5D) {
            var11 = this.exactRoundUp(var6, var10);
         } else {
            var11 = true;
         }

         if (var11) {
            if (var10 < this.fastPathData.fractionalMaxIntBound) {
               ++var10;
            } else {
               var10 = 0;
               ++var5;
            }
         }
      }

      this.collectFractionalDigits(var10, var4, this.fastPathData.fractionalFirstIndex);
      this.collectIntegralDigits(var5, var4, this.fastPathData.integralLastIndex);
      if (this.fastPathData.zeroDelta != 0) {
         this.localizeDigits(var4);
      }

      if (var3) {
         if (this.fastPathData.negativeAffixesRequired) {
            this.addAffixes(var4, this.fastPathData.charsNegativePrefix, this.fastPathData.charsNegativeSuffix);
         }
      } else if (this.fastPathData.positiveAffixesRequired) {
         this.addAffixes(var4, this.fastPathData.charsPositivePrefix, this.fastPathData.charsPositiveSuffix);
      }

   }

   String fastFormat(double var1) {
      boolean var3 = false;
      if (this.fastPathCheckNeeded) {
         var3 = this.checkAndSetFastPathStatus();
      }

      if (!this.isFastPath) {
         return null;
      } else if (!Double.isFinite(var1)) {
         return null;
      } else {
         boolean var4 = false;
         if (var1 < 0.0D) {
            var4 = true;
            var1 = -var1;
         } else if (var1 == 0.0D) {
            var4 = Math.copySign(1.0D, var1) == -1.0D;
            var1 = 0.0D;
         }

         if (var1 > 2.147483647E9D) {
            return null;
         } else {
            if (!var3) {
               this.resetFastPathData(this.isFastPath);
            }

            this.fastDoubleFormat(var1, var4);
            return new String(this.fastPathData.fastPathContainer, this.fastPathData.firstUsedIndex, this.fastPathData.lastFreeIndex - this.fastPathData.firstUsedIndex);
         }
      }
   }

   private StringBuffer subformat(StringBuffer var1, Format.FieldDelegate var2, boolean var3, boolean var4, int var5, int var6, int var7, int var8) {
      char var9 = this.symbols.getZeroDigit();
      int var10 = var9 - 48;
      char var11 = this.symbols.getGroupingSeparator();
      char var12 = this.isCurrencyFormat ? this.symbols.getMonetaryDecimalSeparator() : this.symbols.getDecimalSeparator();
      if (this.digitList.isZero()) {
         this.digitList.decimalAt = 0;
      }

      if (var3) {
         this.append(var1, this.negativePrefix, var2, this.getNegativePrefixFieldPositions(), NumberFormat.Field.SIGN);
      } else {
         this.append(var1, this.positivePrefix, var2, this.getPositivePrefixFieldPositions(), NumberFormat.Field.SIGN);
      }

      int var13;
      int var14;
      int var15;
      int var16;
      int var18;
      int var19;
      int var20;
      if (this.useExponentialNotation) {
         var13 = var1.length();
         var14 = -1;
         var15 = -1;
         var16 = this.digitList.decimalAt;
         var18 = var6;
         if (var5 > 1 && var5 > var6) {
            if (var16 >= 1) {
               var16 = (var16 - 1) / var5 * var5;
            } else {
               var16 = (var16 - var5) / var5 * var5;
            }

            var18 = 1;
         } else {
            var16 -= var6;
         }

         var19 = var6 + var8;
         if (var19 < 0) {
            var19 = Integer.MAX_VALUE;
         }

         var20 = this.digitList.isZero() ? var18 : this.digitList.decimalAt - var16;
         if (var19 < var20) {
            var19 = var20;
         }

         int var21 = this.digitList.count;
         if (var19 > var21) {
            var21 = var19;
         }

         boolean var22 = false;

         int var23;
         for(var23 = 0; var23 < var21; ++var23) {
            if (var23 == var20) {
               var14 = var1.length();
               var1.append(var12);
               var22 = true;
               var15 = var1.length();
            }

            var1.append(var23 < this.digitList.count ? (char)(this.digitList.digits[var23] + var10) : var9);
         }

         if (this.decimalSeparatorAlwaysShown && var21 == var20) {
            var14 = var1.length();
            var1.append(var12);
            var22 = true;
            var15 = var1.length();
         }

         if (var14 == -1) {
            var14 = var1.length();
         }

         var2.formatted(0, NumberFormat.Field.INTEGER, NumberFormat.Field.INTEGER, var13, var14, var1);
         if (var22) {
            var2.formatted(NumberFormat.Field.DECIMAL_SEPARATOR, NumberFormat.Field.DECIMAL_SEPARATOR, var14, var15, var1);
         }

         if (var15 == -1) {
            var15 = var1.length();
         }

         var2.formatted(1, NumberFormat.Field.FRACTION, NumberFormat.Field.FRACTION, var15, var1.length(), var1);
         var23 = var1.length();
         var1.append(this.symbols.getExponentSeparator());
         var2.formatted(NumberFormat.Field.EXPONENT_SYMBOL, NumberFormat.Field.EXPONENT_SYMBOL, var23, var1.length(), var1);
         if (this.digitList.isZero()) {
            var16 = 0;
         }

         boolean var24 = var16 < 0;
         if (var24) {
            var16 = -var16;
            var23 = var1.length();
            var1.append(this.symbols.getMinusSign());
            var2.formatted(NumberFormat.Field.EXPONENT_SIGN, NumberFormat.Field.EXPONENT_SIGN, var23, var1.length(), var1);
         }

         this.digitList.set(var24, (long)var16);
         int var25 = var1.length();

         int var26;
         for(var26 = this.digitList.decimalAt; var26 < this.minExponentDigits; ++var26) {
            var1.append(var9);
         }

         for(var26 = 0; var26 < this.digitList.decimalAt; ++var26) {
            var1.append(var26 < this.digitList.count ? (char)(this.digitList.digits[var26] + var10) : var9);
         }

         var2.formatted(NumberFormat.Field.EXPONENT, NumberFormat.Field.EXPONENT, var25, var1.length(), var1);
      } else {
         var13 = var1.length();
         var14 = var6;
         var15 = 0;
         if (this.digitList.decimalAt > 0 && var6 < this.digitList.decimalAt) {
            var14 = this.digitList.decimalAt;
         }

         if (var14 > var5) {
            var14 = var5;
            var15 = this.digitList.decimalAt - var5;
         }

         var16 = var1.length();

         for(int var17 = var14 - 1; var17 >= 0; --var17) {
            if (var17 < this.digitList.decimalAt && var15 < this.digitList.count) {
               var1.append((char)(this.digitList.digits[var15++] + var10));
            } else {
               var1.append(var9);
            }

            if (this.isGroupingUsed() && var17 > 0 && this.groupingSize != 0 && var17 % this.groupingSize == 0) {
               var18 = var1.length();
               var1.append(var11);
               var2.formatted(NumberFormat.Field.GROUPING_SEPARATOR, NumberFormat.Field.GROUPING_SEPARATOR, var18, var1.length(), var1);
            }
         }

         boolean var27 = var8 > 0 || !var4 && var15 < this.digitList.count;
         if (!var27 && var1.length() == var16) {
            var1.append(var9);
         }

         var2.formatted(0, NumberFormat.Field.INTEGER, NumberFormat.Field.INTEGER, var13, var1.length(), var1);
         var18 = var1.length();
         if (this.decimalSeparatorAlwaysShown || var27) {
            var1.append(var12);
         }

         if (var18 != var1.length()) {
            var2.formatted(NumberFormat.Field.DECIMAL_SEPARATOR, NumberFormat.Field.DECIMAL_SEPARATOR, var18, var1.length(), var1);
         }

         var19 = var1.length();

         for(var20 = 0; var20 < var7 && (var20 < var8 || !var4 && var15 < this.digitList.count); ++var20) {
            if (-1 - var20 > this.digitList.decimalAt - 1) {
               var1.append(var9);
            } else if (!var4 && var15 < this.digitList.count) {
               var1.append((char)(this.digitList.digits[var15++] + var10));
            } else {
               var1.append(var9);
            }
         }

         var2.formatted(1, NumberFormat.Field.FRACTION, NumberFormat.Field.FRACTION, var19, var1.length(), var1);
      }

      if (var3) {
         this.append(var1, this.negativeSuffix, var2, this.getNegativeSuffixFieldPositions(), NumberFormat.Field.SIGN);
      } else {
         this.append(var1, this.positiveSuffix, var2, this.getPositiveSuffixFieldPositions(), NumberFormat.Field.SIGN);
      }

      return var1;
   }

   private void append(StringBuffer var1, String var2, Format.FieldDelegate var3, FieldPosition[] var4, Format.Field var5) {
      int var6 = var1.length();
      if (var2.length() > 0) {
         var1.append(var2);
         int var7 = 0;

         for(int var8 = var4.length; var7 < var8; ++var7) {
            FieldPosition var9 = var4[var7];
            Format.Field var10 = var9.getFieldAttribute();
            if (var10 == NumberFormat.Field.SIGN) {
               var10 = var5;
            }

            var3.formatted(var10, var10, var6 + var9.getBeginIndex(), var6 + var9.getEndIndex(), var1);
         }
      }

   }

   public Number parse(String var1, ParsePosition var2) {
      if (var1.regionMatches(var2.index, this.symbols.getNaN(), 0, this.symbols.getNaN().length())) {
         var2.index += this.symbols.getNaN().length();
         return new Double(Double.NaN);
      } else {
         boolean[] var3 = new boolean[2];
         if (!this.subparse(var1, var2, this.positivePrefix, this.negativePrefix, this.digitList, false, var3)) {
            return null;
         } else if (var3[0]) {
            return var3[1] == this.multiplier >= 0 ? new Double(Double.POSITIVE_INFINITY) : new Double(Double.NEGATIVE_INFINITY);
         } else if (this.multiplier == 0) {
            if (this.digitList.isZero()) {
               return new Double(Double.NaN);
            } else {
               return var3[1] ? new Double(Double.POSITIVE_INFINITY) : new Double(Double.NEGATIVE_INFINITY);
            }
         } else if (this.isParseBigDecimal()) {
            BigDecimal var11 = this.digitList.getBigDecimal();
            if (this.multiplier != 1) {
               try {
                  var11 = var11.divide(this.getBigDecimalMultiplier());
               } catch (ArithmeticException var10) {
                  var11 = var11.divide(this.getBigDecimalMultiplier(), this.roundingMode);
               }
            }

            if (!var3[1]) {
               var11 = var11.negate();
            }

            return var11;
         } else {
            boolean var4 = true;
            boolean var5 = false;
            double var6 = 0.0D;
            long var8 = 0L;
            if (this.digitList.fitsIntoLong(var3[1], this.isParseIntegerOnly())) {
               var4 = false;
               var8 = this.digitList.getLong();
               if (var8 < 0L) {
                  var5 = true;
               }
            } else {
               var6 = this.digitList.getDouble();
            }

            if (this.multiplier != 1) {
               if (var4) {
                  var6 /= (double)this.multiplier;
               } else if (var8 % (long)this.multiplier == 0L) {
                  var8 /= (long)this.multiplier;
               } else {
                  var6 = (double)var8 / (double)this.multiplier;
                  var4 = true;
               }
            }

            if (!var3[1] && !var5) {
               var6 = -var6;
               var8 = -var8;
            }

            if (this.multiplier != 1 && var4) {
               var8 = (long)var6;
               var4 = (var6 != (double)var8 || var6 == 0.0D && 1.0D / var6 < 0.0D) && !this.isParseIntegerOnly();
            }

            return (Number)(var4 ? new Double(var6) : new Long(var8));
         }
      }
   }

   private BigInteger getBigIntegerMultiplier() {
      if (this.bigIntegerMultiplier == null) {
         this.bigIntegerMultiplier = BigInteger.valueOf((long)this.multiplier);
      }

      return this.bigIntegerMultiplier;
   }

   private BigDecimal getBigDecimalMultiplier() {
      if (this.bigDecimalMultiplier == null) {
         this.bigDecimalMultiplier = new BigDecimal(this.multiplier);
      }

      return this.bigDecimalMultiplier;
   }

   private final boolean subparse(String var1, ParsePosition var2, String var3, String var4, DigitList var5, boolean var6, boolean[] var7) {
      int var8 = var2.index;
      int var9 = var2.index;
      boolean var11 = var1.regionMatches(var8, var3, 0, var3.length());
      boolean var12 = var1.regionMatches(var8, var4, 0, var4.length());
      if (var11 && var12) {
         if (var3.length() > var4.length()) {
            var12 = false;
         } else if (var3.length() < var4.length()) {
            var11 = false;
         }
      }

      if (var11) {
         var8 += var3.length();
      } else {
         if (!var12) {
            var2.errorIndex = var8;
            return false;
         }

         var8 += var4.length();
      }

      var7[0] = false;
      if (!var6 && var1.regionMatches(var8, this.symbols.getInfinity(), 0, this.symbols.getInfinity().length())) {
         var8 += this.symbols.getInfinity().length();
         var7[0] = true;
      } else {
         var5.decimalAt = var5.count = 0;
         char var13 = this.symbols.getZeroDigit();
         char var14 = this.isCurrencyFormat ? this.symbols.getMonetaryDecimalSeparator() : this.symbols.getDecimalSeparator();
         char var15 = this.symbols.getGroupingSeparator();
         String var16 = this.symbols.getExponentSeparator();
         boolean var17 = false;
         boolean var18 = false;
         boolean var19 = false;
         int var20 = 0;
         int var21 = 0;

         int var10;
         for(var10 = -1; var8 < var1.length(); ++var8) {
            char var22 = var1.charAt(var8);
            int var23 = var22 - var13;
            if (var23 < 0 || var23 > 9) {
               var23 = Character.digit((char)var22, 10);
            }

            if (var23 == 0) {
               var10 = -1;
               var19 = true;
               if (var5.count == 0) {
                  if (var17) {
                     --var5.decimalAt;
                  }
               } else {
                  ++var21;
                  var5.append((char)(var23 + 48));
               }
            } else if (var23 > 0 && var23 <= 9) {
               var19 = true;
               ++var21;
               var5.append((char)(var23 + 48));
               var10 = -1;
            } else if (!var6 && var22 == var14) {
               if (this.isParseIntegerOnly() || var17) {
                  break;
               }

               var5.decimalAt = var21;
               var17 = true;
            } else {
               if (var6 || var22 != var15 || !this.isGroupingUsed()) {
                  if (!var6 && var1.regionMatches(var8, var16, 0, var16.length()) && !var18) {
                     ParsePosition var24 = new ParsePosition(var8 + var16.length());
                     boolean[] var25 = new boolean[2];
                     DigitList var26 = new DigitList();
                     if (this.subparse(var1, var24, "", Character.toString(this.symbols.getMinusSign()), var26, true, var25) && var26.fitsIntoLong(var25[1], true)) {
                        var8 = var24.index;
                        var20 = (int)var26.getLong();
                        if (!var25[1]) {
                           var20 = -var20;
                        }

                        var18 = true;
                     }
                  }
                  break;
               }

               if (var17) {
                  break;
               }

               var10 = var8;
            }
         }

         if (var10 != -1) {
            var8 = var10;
         }

         if (!var17) {
            var5.decimalAt = var21;
         }

         var5.decimalAt += var20;
         if (!var19 && var21 == 0) {
            var2.index = var9;
            var2.errorIndex = var9;
            return false;
         }
      }

      if (!var6) {
         if (var11) {
            var11 = var1.regionMatches(var8, this.positiveSuffix, 0, this.positiveSuffix.length());
         }

         if (var12) {
            var12 = var1.regionMatches(var8, this.negativeSuffix, 0, this.negativeSuffix.length());
         }

         if (var11 && var12) {
            if (this.positiveSuffix.length() > this.negativeSuffix.length()) {
               var12 = false;
            } else if (this.positiveSuffix.length() < this.negativeSuffix.length()) {
               var11 = false;
            }
         }

         if (var11 == var12) {
            var2.errorIndex = var8;
            return false;
         }

         var2.index = var8 + (var11 ? this.positiveSuffix.length() : this.negativeSuffix.length());
      } else {
         var2.index = var8;
      }

      var7[1] = var11;
      if (var2.index == var9) {
         var2.errorIndex = var8;
         return false;
      } else {
         return true;
      }
   }

   public DecimalFormatSymbols getDecimalFormatSymbols() {
      try {
         return (DecimalFormatSymbols)this.symbols.clone();
      } catch (Exception var2) {
         return null;
      }
   }

   public void setDecimalFormatSymbols(DecimalFormatSymbols var1) {
      try {
         this.symbols = (DecimalFormatSymbols)var1.clone();
         this.expandAffixes();
         this.fastPathCheckNeeded = true;
      } catch (Exception var3) {
      }

   }

   public String getPositivePrefix() {
      return this.positivePrefix;
   }

   public void setPositivePrefix(String var1) {
      this.positivePrefix = var1;
      this.posPrefixPattern = null;
      this.positivePrefixFieldPositions = null;
      this.fastPathCheckNeeded = true;
   }

   private FieldPosition[] getPositivePrefixFieldPositions() {
      if (this.positivePrefixFieldPositions == null) {
         if (this.posPrefixPattern != null) {
            this.positivePrefixFieldPositions = this.expandAffix(this.posPrefixPattern);
         } else {
            this.positivePrefixFieldPositions = EmptyFieldPositionArray;
         }
      }

      return this.positivePrefixFieldPositions;
   }

   public String getNegativePrefix() {
      return this.negativePrefix;
   }

   public void setNegativePrefix(String var1) {
      this.negativePrefix = var1;
      this.negPrefixPattern = null;
      this.fastPathCheckNeeded = true;
   }

   private FieldPosition[] getNegativePrefixFieldPositions() {
      if (this.negativePrefixFieldPositions == null) {
         if (this.negPrefixPattern != null) {
            this.negativePrefixFieldPositions = this.expandAffix(this.negPrefixPattern);
         } else {
            this.negativePrefixFieldPositions = EmptyFieldPositionArray;
         }
      }

      return this.negativePrefixFieldPositions;
   }

   public String getPositiveSuffix() {
      return this.positiveSuffix;
   }

   public void setPositiveSuffix(String var1) {
      this.positiveSuffix = var1;
      this.posSuffixPattern = null;
      this.fastPathCheckNeeded = true;
   }

   private FieldPosition[] getPositiveSuffixFieldPositions() {
      if (this.positiveSuffixFieldPositions == null) {
         if (this.posSuffixPattern != null) {
            this.positiveSuffixFieldPositions = this.expandAffix(this.posSuffixPattern);
         } else {
            this.positiveSuffixFieldPositions = EmptyFieldPositionArray;
         }
      }

      return this.positiveSuffixFieldPositions;
   }

   public String getNegativeSuffix() {
      return this.negativeSuffix;
   }

   public void setNegativeSuffix(String var1) {
      this.negativeSuffix = var1;
      this.negSuffixPattern = null;
      this.fastPathCheckNeeded = true;
   }

   private FieldPosition[] getNegativeSuffixFieldPositions() {
      if (this.negativeSuffixFieldPositions == null) {
         if (this.negSuffixPattern != null) {
            this.negativeSuffixFieldPositions = this.expandAffix(this.negSuffixPattern);
         } else {
            this.negativeSuffixFieldPositions = EmptyFieldPositionArray;
         }
      }

      return this.negativeSuffixFieldPositions;
   }

   public int getMultiplier() {
      return this.multiplier;
   }

   public void setMultiplier(int var1) {
      this.multiplier = var1;
      this.bigDecimalMultiplier = null;
      this.bigIntegerMultiplier = null;
      this.fastPathCheckNeeded = true;
   }

   public void setGroupingUsed(boolean var1) {
      super.setGroupingUsed(var1);
      this.fastPathCheckNeeded = true;
   }

   public int getGroupingSize() {
      return this.groupingSize;
   }

   public void setGroupingSize(int var1) {
      this.groupingSize = (byte)var1;
      this.fastPathCheckNeeded = true;
   }

   public boolean isDecimalSeparatorAlwaysShown() {
      return this.decimalSeparatorAlwaysShown;
   }

   public void setDecimalSeparatorAlwaysShown(boolean var1) {
      this.decimalSeparatorAlwaysShown = var1;
      this.fastPathCheckNeeded = true;
   }

   public boolean isParseBigDecimal() {
      return this.parseBigDecimal;
   }

   public void setParseBigDecimal(boolean var1) {
      this.parseBigDecimal = var1;
   }

   public Object clone() {
      DecimalFormat var1 = (DecimalFormat)super.clone();
      var1.symbols = (DecimalFormatSymbols)this.symbols.clone();
      var1.digitList = (DigitList)this.digitList.clone();
      var1.fastPathCheckNeeded = true;
      var1.isFastPath = false;
      var1.fastPathData = null;
      return var1;
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (!super.equals(var1)) {
         return false;
      } else {
         DecimalFormat var2 = (DecimalFormat)var1;
         return (this.posPrefixPattern == var2.posPrefixPattern && this.positivePrefix.equals(var2.positivePrefix) || this.posPrefixPattern != null && this.posPrefixPattern.equals(var2.posPrefixPattern)) && (this.posSuffixPattern == var2.posSuffixPattern && this.positiveSuffix.equals(var2.positiveSuffix) || this.posSuffixPattern != null && this.posSuffixPattern.equals(var2.posSuffixPattern)) && (this.negPrefixPattern == var2.negPrefixPattern && this.negativePrefix.equals(var2.negativePrefix) || this.negPrefixPattern != null && this.negPrefixPattern.equals(var2.negPrefixPattern)) && (this.negSuffixPattern == var2.negSuffixPattern && this.negativeSuffix.equals(var2.negativeSuffix) || this.negSuffixPattern != null && this.negSuffixPattern.equals(var2.negSuffixPattern)) && this.multiplier == var2.multiplier && this.groupingSize == var2.groupingSize && this.decimalSeparatorAlwaysShown == var2.decimalSeparatorAlwaysShown && this.parseBigDecimal == var2.parseBigDecimal && this.useExponentialNotation == var2.useExponentialNotation && (!this.useExponentialNotation || this.minExponentDigits == var2.minExponentDigits) && this.maximumIntegerDigits == var2.maximumIntegerDigits && this.minimumIntegerDigits == var2.minimumIntegerDigits && this.maximumFractionDigits == var2.maximumFractionDigits && this.minimumFractionDigits == var2.minimumFractionDigits && this.roundingMode == var2.roundingMode && this.symbols.equals(var2.symbols);
      }
   }

   public int hashCode() {
      return super.hashCode() * 37 + this.positivePrefix.hashCode();
   }

   public String toPattern() {
      return this.toPattern(false);
   }

   public String toLocalizedPattern() {
      return this.toPattern(true);
   }

   private void expandAffixes() {
      StringBuffer var1 = new StringBuffer();
      if (this.posPrefixPattern != null) {
         this.positivePrefix = this.expandAffix(this.posPrefixPattern, var1);
         this.positivePrefixFieldPositions = null;
      }

      if (this.posSuffixPattern != null) {
         this.positiveSuffix = this.expandAffix(this.posSuffixPattern, var1);
         this.positiveSuffixFieldPositions = null;
      }

      if (this.negPrefixPattern != null) {
         this.negativePrefix = this.expandAffix(this.negPrefixPattern, var1);
         this.negativePrefixFieldPositions = null;
      }

      if (this.negSuffixPattern != null) {
         this.negativeSuffix = this.expandAffix(this.negSuffixPattern, var1);
         this.negativeSuffixFieldPositions = null;
      }

   }

   private String expandAffix(String var1, StringBuffer var2) {
      var2.setLength(0);
      int var3 = 0;

      while(true) {
         while(true) {
            while(var3 < var1.length()) {
               char var4 = var1.charAt(var3++);
               if (var4 == '\'') {
                  var4 = var1.charAt(var3++);
                  switch(var4) {
                  case '%':
                     var4 = this.symbols.getPercent();
                     break;
                  case '-':
                     var4 = this.symbols.getMinusSign();
                     break;
                  case '¤':
                     if (var3 < var1.length() && var1.charAt(var3) == 164) {
                        ++var3;
                        var2.append(this.symbols.getInternationalCurrencySymbol());
                        continue;
                     }

                     var2.append(this.symbols.getCurrencySymbol());
                     continue;
                  case '‰':
                     var4 = this.symbols.getPerMill();
                  }
               }

               var2.append(var4);
            }

            return var2.toString();
         }
      }
   }

   private FieldPosition[] expandAffix(String var1) {
      ArrayList var2 = null;
      int var3 = 0;
      int var4 = 0;

      while(true) {
         while(var4 < var1.length()) {
            char var5 = var1.charAt(var4++);
            if (var5 == '\'') {
               byte var6 = -1;
               NumberFormat.Field var7 = null;
               var5 = var1.charAt(var4++);
               switch(var5) {
               case '%':
                  var5 = this.symbols.getPercent();
                  var6 = -1;
                  var7 = NumberFormat.Field.PERCENT;
                  break;
               case '-':
                  var5 = this.symbols.getMinusSign();
                  var6 = -1;
                  var7 = NumberFormat.Field.SIGN;
                  break;
               case '¤':
                  String var8;
                  if (var4 < var1.length() && var1.charAt(var4) == 164) {
                     ++var4;
                     var8 = this.symbols.getInternationalCurrencySymbol();
                  } else {
                     var8 = this.symbols.getCurrencySymbol();
                  }

                  if (var8.length() > 0) {
                     if (var2 == null) {
                        var2 = new ArrayList(2);
                     }

                     FieldPosition var9 = new FieldPosition(NumberFormat.Field.CURRENCY);
                     var9.setBeginIndex(var3);
                     var9.setEndIndex(var3 + var8.length());
                     var2.add(var9);
                     var3 += var8.length();
                  }
                  continue;
               case '‰':
                  var5 = this.symbols.getPerMill();
                  var6 = -1;
                  var7 = NumberFormat.Field.PERMILLE;
               }

               if (var7 != null) {
                  if (var2 == null) {
                     var2 = new ArrayList(2);
                  }

                  FieldPosition var10 = new FieldPosition(var7, var6);
                  var10.setBeginIndex(var3);
                  var10.setEndIndex(var3 + 1);
                  var2.add(var10);
               }
            }

            ++var3;
         }

         if (var2 != null) {
            return (FieldPosition[])var2.toArray(EmptyFieldPositionArray);
         }

         return EmptyFieldPositionArray;
      }
   }

   private void appendAffix(StringBuffer var1, String var2, String var3, boolean var4) {
      int var5;
      if (var2 == null) {
         this.appendAffix(var1, var3, var4);
      } else {
         for(int var6 = 0; var6 < var2.length(); var6 = var5) {
            var5 = var2.indexOf(39, var6);
            if (var5 < 0) {
               this.appendAffix(var1, var2.substring(var6), var4);
               break;
            }

            if (var5 > var6) {
               this.appendAffix(var1, var2.substring(var6, var5), var4);
            }

            ++var5;
            char var7 = var2.charAt(var5);
            ++var5;
            if (var7 == '\'') {
               var1.append(var7);
            } else if (var7 == 164 && var5 < var2.length() && var2.charAt(var5) == 164) {
               ++var5;
               var1.append(var7);
            } else if (var4) {
               switch(var7) {
               case '%':
                  var7 = this.symbols.getPercent();
                  break;
               case '-':
                  var7 = this.symbols.getMinusSign();
                  break;
               case '‰':
                  var7 = this.symbols.getPerMill();
               }
            }

            var1.append(var7);
         }
      }

   }

   private void appendAffix(StringBuffer var1, String var2, boolean var3) {
      boolean var4;
      if (var3) {
         var4 = var2.indexOf(this.symbols.getZeroDigit()) >= 0 || var2.indexOf(this.symbols.getGroupingSeparator()) >= 0 || var2.indexOf(this.symbols.getDecimalSeparator()) >= 0 || var2.indexOf(this.symbols.getPercent()) >= 0 || var2.indexOf(this.symbols.getPerMill()) >= 0 || var2.indexOf(this.symbols.getDigit()) >= 0 || var2.indexOf(this.symbols.getPatternSeparator()) >= 0 || var2.indexOf(this.symbols.getMinusSign()) >= 0 || var2.indexOf(164) >= 0;
      } else {
         var4 = var2.indexOf(48) >= 0 || var2.indexOf(44) >= 0 || var2.indexOf(46) >= 0 || var2.indexOf(37) >= 0 || var2.indexOf(8240) >= 0 || var2.indexOf(35) >= 0 || var2.indexOf(59) >= 0 || var2.indexOf(45) >= 0 || var2.indexOf(164) >= 0;
      }

      if (var4) {
         var1.append('\'');
      }

      if (var2.indexOf(39) < 0) {
         var1.append(var2);
      } else {
         for(int var5 = 0; var5 < var2.length(); ++var5) {
            char var6 = var2.charAt(var5);
            var1.append(var6);
            if (var6 == '\'') {
               var1.append(var6);
            }
         }
      }

      if (var4) {
         var1.append('\'');
      }

   }

   private String toPattern(boolean var1) {
      StringBuffer var2 = new StringBuffer();

      for(int var3 = 1; var3 >= 0; --var3) {
         if (var3 == 1) {
            this.appendAffix(var2, this.posPrefixPattern, this.positivePrefix, var1);
         } else {
            this.appendAffix(var2, this.negPrefixPattern, this.negativePrefix, var1);
         }

         int var5 = this.useExponentialNotation ? this.getMaximumIntegerDigits() : Math.max(this.groupingSize, this.getMinimumIntegerDigits()) + 1;

         int var4;
         for(var4 = var5; var4 > 0; --var4) {
            if (var4 != var5 && this.isGroupingUsed() && this.groupingSize != 0 && var4 % this.groupingSize == 0) {
               var2.append(var1 ? this.symbols.getGroupingSeparator() : ',');
            }

            var2.append(var4 <= this.getMinimumIntegerDigits() ? (var1 ? this.symbols.getZeroDigit() : '0') : (var1 ? this.symbols.getDigit() : '#'));
         }

         if (this.getMaximumFractionDigits() > 0 || this.decimalSeparatorAlwaysShown) {
            var2.append(var1 ? this.symbols.getDecimalSeparator() : '.');
         }

         for(var4 = 0; var4 < this.getMaximumFractionDigits(); ++var4) {
            if (var4 < this.getMinimumFractionDigits()) {
               var2.append(var1 ? this.symbols.getZeroDigit() : '0');
            } else {
               var2.append(var1 ? this.symbols.getDigit() : '#');
            }
         }

         if (this.useExponentialNotation) {
            var2.append(var1 ? this.symbols.getExponentSeparator() : "E");

            for(var4 = 0; var4 < this.minExponentDigits; ++var4) {
               var2.append(var1 ? this.symbols.getZeroDigit() : '0');
            }
         }

         if (var3 == 1) {
            this.appendAffix(var2, this.posSuffixPattern, this.positiveSuffix, var1);
            if ((this.negSuffixPattern == this.posSuffixPattern && this.negativeSuffix.equals(this.positiveSuffix) || this.negSuffixPattern != null && this.negSuffixPattern.equals(this.posSuffixPattern)) && (this.negPrefixPattern != null && this.posPrefixPattern != null && this.negPrefixPattern.equals("'-" + this.posPrefixPattern) || this.negPrefixPattern == this.posPrefixPattern && this.negativePrefix.equals(this.symbols.getMinusSign() + this.positivePrefix))) {
               break;
            }

            var2.append(var1 ? this.symbols.getPatternSeparator() : ';');
         } else {
            this.appendAffix(var2, this.negSuffixPattern, this.negativeSuffix, var1);
         }
      }

      return var2.toString();
   }

   public void applyPattern(String var1) {
      this.applyPattern(var1, false);
   }

   public void applyLocalizedPattern(String var1) {
      this.applyPattern(var1, true);
   }

   private void applyPattern(String var1, boolean var2) {
      char var3 = '0';
      char var4 = ',';
      char var5 = '.';
      char var6 = '%';
      char var7 = 8240;
      char var8 = '#';
      char var9 = ';';
      String var10 = "E";
      char var11 = '-';
      if (var2) {
         var3 = this.symbols.getZeroDigit();
         var4 = this.symbols.getGroupingSeparator();
         var5 = this.symbols.getDecimalSeparator();
         var6 = this.symbols.getPercent();
         var7 = this.symbols.getPerMill();
         var8 = this.symbols.getDigit();
         var9 = this.symbols.getPatternSeparator();
         var10 = this.symbols.getExponentSeparator();
         var11 = this.symbols.getMinusSign();
      }

      boolean var12 = false;
      this.decimalSeparatorAlwaysShown = false;
      this.isCurrencyFormat = false;
      this.useExponentialNotation = false;
      boolean var13 = false;
      int var14 = 0;
      int var15 = 0;
      int var16 = 1;

      while(true) {
         if (var16 >= 0 && var15 < var1.length()) {
            boolean var17 = false;
            StringBuffer var18 = new StringBuffer();
            StringBuffer var19 = new StringBuffer();
            int var20 = -1;
            short var21 = 1;
            int var22 = 0;
            int var23 = 0;
            int var24 = 0;
            byte var25 = -1;
            byte var26 = 0;
            StringBuffer var27 = var18;

            int var28;
            for(var28 = var15; var28 < var1.length(); ++var28) {
               char var29 = var1.charAt(var28);
               switch(var26) {
               case 0:
               case 2:
                  if (var17) {
                     if (var29 == '\'') {
                        if (var28 + 1 < var1.length() && var1.charAt(var28 + 1) == '\'') {
                           ++var28;
                           var27.append("''");
                        } else {
                           var17 = false;
                        }
                        break;
                     }
                  } else {
                     if (var29 == var8 || var29 == var3 || var29 == var4 || var29 == var5) {
                        var26 = 1;
                        if (var16 == 1) {
                           ;
                        }

                        --var28;
                        break;
                     }

                     if (var29 == 164) {
                        boolean var30 = var28 + 1 < var1.length() && var1.charAt(var28 + 1) == 164;
                        if (var30) {
                           ++var28;
                        }

                        this.isCurrencyFormat = true;
                        var27.append(var30 ? "'¤¤" : "'¤");
                        break;
                     }

                     if (var29 == '\'') {
                        if (var29 == '\'') {
                           if (var28 + 1 < var1.length() && var1.charAt(var28 + 1) == '\'') {
                              ++var28;
                              var27.append("''");
                           } else {
                              var17 = true;
                           }
                           break;
                        }
                     } else {
                        if (var29 == var9) {
                           if (var26 != 0 && var16 != 0) {
                              var15 = var28 + 1;
                              var28 = var1.length();
                              break;
                           }

                           throw new IllegalArgumentException("Unquoted special character '" + var29 + "' in pattern \"" + var1 + '"');
                        }

                        if (var29 == var6) {
                           if (var21 != 1) {
                              throw new IllegalArgumentException("Too many percent/per mille characters in pattern \"" + var1 + '"');
                           }

                           var21 = 100;
                           var27.append("'%");
                           break;
                        }

                        if (var29 == var7) {
                           if (var21 != 1) {
                              throw new IllegalArgumentException("Too many percent/per mille characters in pattern \"" + var1 + '"');
                           }

                           var21 = 1000;
                           var27.append("'‰");
                           break;
                        }

                        if (var29 == var11) {
                           var27.append("'-");
                           break;
                        }
                     }
                  }

                  var27.append(var29);
                  break;
               case 1:
                  if (var16 == 1) {
                     ++var14;
                     if (var29 == var8) {
                        if (var23 > 0) {
                           ++var24;
                        } else {
                           ++var22;
                        }

                        if (var25 >= 0 && var20 < 0) {
                           ++var25;
                        }
                     } else if (var29 == var3) {
                        if (var24 > 0) {
                           throw new IllegalArgumentException("Unexpected '0' in pattern \"" + var1 + '"');
                        }

                        ++var23;
                        if (var25 >= 0 && var20 < 0) {
                           ++var25;
                        }
                     } else if (var29 == var4) {
                        var25 = 0;
                     } else if (var29 == var5) {
                        if (var20 >= 0) {
                           throw new IllegalArgumentException("Multiple decimal separators in pattern \"" + var1 + '"');
                        }

                        var20 = var22 + var23 + var24;
                     } else if (!var1.regionMatches(var28, var10, 0, var10.length())) {
                        var26 = 2;
                        var27 = var19;
                        --var28;
                        --var14;
                     } else {
                        if (this.useExponentialNotation) {
                           throw new IllegalArgumentException("Multiple exponential symbols in pattern \"" + var1 + '"');
                        }

                        this.useExponentialNotation = true;
                        this.minExponentDigits = 0;

                        for(var28 += var10.length(); var28 < var1.length() && var1.charAt(var28) == var3; ++var28) {
                           ++this.minExponentDigits;
                           ++var14;
                        }

                        if (var22 + var23 < 1 || this.minExponentDigits < 1) {
                           throw new IllegalArgumentException("Malformed exponential pattern \"" + var1 + '"');
                        }

                        var26 = 2;
                        var27 = var19;
                        --var28;
                     }
                  } else {
                     --var14;
                     if (var14 == 0) {
                        var26 = 2;
                        var27 = var19;
                     }
                  }
               }
            }

            if (var23 == 0 && var22 > 0 && var20 >= 0) {
               var28 = var20;
               if (var20 == 0) {
                  var28 = var20 + 1;
               }

               var24 = var22 - var28;
               var22 = var28 - 1;
               var23 = 1;
            }

            if ((var20 >= 0 || var24 <= 0) && (var20 < 0 || var20 >= var22 && var20 <= var22 + var23) && var25 != 0 && !var17) {
               if (var16 == 1) {
                  this.posPrefixPattern = var18.toString();
                  this.posSuffixPattern = var19.toString();
                  this.negPrefixPattern = this.posPrefixPattern;
                  this.negSuffixPattern = this.posSuffixPattern;
                  var28 = var22 + var23 + var24;
                  int var31 = var20 >= 0 ? var20 : var28;
                  this.setMinimumIntegerDigits(var31 - var22);
                  this.setMaximumIntegerDigits(this.useExponentialNotation ? var22 + this.getMinimumIntegerDigits() : Integer.MAX_VALUE);
                  this.setMaximumFractionDigits(var20 >= 0 ? var28 - var20 : 0);
                  this.setMinimumFractionDigits(var20 >= 0 ? var22 + var23 - var20 : 0);
                  this.setGroupingUsed(var25 > 0);
                  this.groupingSize = var25 > 0 ? var25 : 0;
                  this.multiplier = var21;
                  this.setDecimalSeparatorAlwaysShown(var20 == 0 || var20 == var28);
               } else {
                  this.negPrefixPattern = var18.toString();
                  this.negSuffixPattern = var19.toString();
                  var12 = true;
               }

               --var16;
               continue;
            }

            throw new IllegalArgumentException("Malformed pattern \"" + var1 + '"');
         }

         if (var1.length() == 0) {
            this.posPrefixPattern = this.posSuffixPattern = "";
            this.setMinimumIntegerDigits(0);
            this.setMaximumIntegerDigits(Integer.MAX_VALUE);
            this.setMinimumFractionDigits(0);
            this.setMaximumFractionDigits(Integer.MAX_VALUE);
         }

         if (!var12 || this.negPrefixPattern.equals(this.posPrefixPattern) && this.negSuffixPattern.equals(this.posSuffixPattern)) {
            this.negSuffixPattern = this.posSuffixPattern;
            this.negPrefixPattern = "'-" + this.posPrefixPattern;
         }

         this.expandAffixes();
         return;
      }
   }

   public void setMaximumIntegerDigits(int var1) {
      this.maximumIntegerDigits = Math.min(Math.max(0, var1), Integer.MAX_VALUE);
      super.setMaximumIntegerDigits(this.maximumIntegerDigits > 309 ? 309 : this.maximumIntegerDigits);
      if (this.minimumIntegerDigits > this.maximumIntegerDigits) {
         this.minimumIntegerDigits = this.maximumIntegerDigits;
         super.setMinimumIntegerDigits(this.minimumIntegerDigits > 309 ? 309 : this.minimumIntegerDigits);
      }

      this.fastPathCheckNeeded = true;
   }

   public void setMinimumIntegerDigits(int var1) {
      this.minimumIntegerDigits = Math.min(Math.max(0, var1), Integer.MAX_VALUE);
      super.setMinimumIntegerDigits(this.minimumIntegerDigits > 309 ? 309 : this.minimumIntegerDigits);
      if (this.minimumIntegerDigits > this.maximumIntegerDigits) {
         this.maximumIntegerDigits = this.minimumIntegerDigits;
         super.setMaximumIntegerDigits(this.maximumIntegerDigits > 309 ? 309 : this.maximumIntegerDigits);
      }

      this.fastPathCheckNeeded = true;
   }

   public void setMaximumFractionDigits(int var1) {
      this.maximumFractionDigits = Math.min(Math.max(0, var1), Integer.MAX_VALUE);
      super.setMaximumFractionDigits(this.maximumFractionDigits > 340 ? 340 : this.maximumFractionDigits);
      if (this.minimumFractionDigits > this.maximumFractionDigits) {
         this.minimumFractionDigits = this.maximumFractionDigits;
         super.setMinimumFractionDigits(this.minimumFractionDigits > 340 ? 340 : this.minimumFractionDigits);
      }

      this.fastPathCheckNeeded = true;
   }

   public void setMinimumFractionDigits(int var1) {
      this.minimumFractionDigits = Math.min(Math.max(0, var1), Integer.MAX_VALUE);
      super.setMinimumFractionDigits(this.minimumFractionDigits > 340 ? 340 : this.minimumFractionDigits);
      if (this.minimumFractionDigits > this.maximumFractionDigits) {
         this.maximumFractionDigits = this.minimumFractionDigits;
         super.setMaximumFractionDigits(this.maximumFractionDigits > 340 ? 340 : this.maximumFractionDigits);
      }

      this.fastPathCheckNeeded = true;
   }

   public int getMaximumIntegerDigits() {
      return this.maximumIntegerDigits;
   }

   public int getMinimumIntegerDigits() {
      return this.minimumIntegerDigits;
   }

   public int getMaximumFractionDigits() {
      return this.maximumFractionDigits;
   }

   public int getMinimumFractionDigits() {
      return this.minimumFractionDigits;
   }

   public Currency getCurrency() {
      return this.symbols.getCurrency();
   }

   public void setCurrency(Currency var1) {
      if (var1 != this.symbols.getCurrency()) {
         this.symbols.setCurrency(var1);
         if (this.isCurrencyFormat) {
            this.expandAffixes();
         }
      }

      this.fastPathCheckNeeded = true;
   }

   public RoundingMode getRoundingMode() {
      return this.roundingMode;
   }

   public void setRoundingMode(RoundingMode var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.roundingMode = var1;
         this.digitList.setRoundingMode(var1);
         this.fastPathCheckNeeded = true;
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.digitList = new DigitList();
      this.fastPathCheckNeeded = true;
      this.isFastPath = false;
      this.fastPathData = null;
      if (this.serialVersionOnStream < 4) {
         this.setRoundingMode(RoundingMode.HALF_EVEN);
      } else {
         this.setRoundingMode(this.getRoundingMode());
      }

      if (super.getMaximumIntegerDigits() <= 309 && super.getMaximumFractionDigits() <= 340) {
         if (this.serialVersionOnStream < 3) {
            this.setMaximumIntegerDigits(super.getMaximumIntegerDigits());
            this.setMinimumIntegerDigits(super.getMinimumIntegerDigits());
            this.setMaximumFractionDigits(super.getMaximumFractionDigits());
            this.setMinimumFractionDigits(super.getMinimumFractionDigits());
         }

         if (this.serialVersionOnStream < 1) {
            this.useExponentialNotation = false;
         }

         this.serialVersionOnStream = 4;
      } else {
         throw new InvalidObjectException("Digit count out of range");
      }
   }

   private static class DigitArrays {
      static final char[] DigitOnes1000 = new char[1000];
      static final char[] DigitTens1000 = new char[1000];
      static final char[] DigitHundreds1000 = new char[1000];

      static {
         int var0 = 0;
         int var1 = 0;
         char var2 = '0';
         char var3 = '0';
         char var4 = '0';

         for(int var5 = 0; var5 < 1000; ++var5) {
            DigitOnes1000[var5] = var2;
            if (var2 == '9') {
               var2 = '0';
            } else {
               ++var2;
            }

            DigitTens1000[var5] = var3;
            if (var5 == var0 + 9) {
               var0 += 10;
               if (var3 == '9') {
                  var3 = '0';
               } else {
                  ++var3;
               }
            }

            DigitHundreds1000[var5] = var4;
            if (var5 == var1 + 99) {
               ++var4;
               var1 += 100;
            }
         }

      }
   }

   private static class FastPathData {
      int lastFreeIndex;
      int firstUsedIndex;
      int zeroDelta;
      char groupingChar;
      int integralLastIndex;
      int fractionalFirstIndex;
      double fractionalScaleFactor;
      int fractionalMaxIntBound;
      char[] fastPathContainer;
      char[] charsPositivePrefix;
      char[] charsNegativePrefix;
      char[] charsPositiveSuffix;
      char[] charsNegativeSuffix;
      boolean positiveAffixesRequired;
      boolean negativeAffixesRequired;

      private FastPathData() {
         this.positiveAffixesRequired = true;
         this.negativeAffixesRequired = true;
      }

      // $FF: synthetic method
      FastPathData(Object var1) {
         this();
      }
   }
}
