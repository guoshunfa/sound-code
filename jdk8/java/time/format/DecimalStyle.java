package java.time.format;

import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class DecimalStyle {
   public static final DecimalStyle STANDARD = new DecimalStyle('0', '+', '-', '.');
   private static final ConcurrentMap<Locale, DecimalStyle> CACHE = new ConcurrentHashMap(16, 0.75F, 2);
   private final char zeroDigit;
   private final char positiveSign;
   private final char negativeSign;
   private final char decimalSeparator;

   public static Set<Locale> getAvailableLocales() {
      Locale[] var0 = DecimalFormatSymbols.getAvailableLocales();
      HashSet var1 = new HashSet(var0.length);
      Collections.addAll(var1, var0);
      return var1;
   }

   public static DecimalStyle ofDefaultLocale() {
      return of(Locale.getDefault(Locale.Category.FORMAT));
   }

   public static DecimalStyle of(Locale var0) {
      Objects.requireNonNull(var0, (String)"locale");
      DecimalStyle var1 = (DecimalStyle)CACHE.get(var0);
      if (var1 == null) {
         var1 = create(var0);
         CACHE.putIfAbsent(var0, var1);
         var1 = (DecimalStyle)CACHE.get(var0);
      }

      return var1;
   }

   private static DecimalStyle create(Locale var0) {
      DecimalFormatSymbols var1 = DecimalFormatSymbols.getInstance(var0);
      char var2 = var1.getZeroDigit();
      char var3 = '+';
      char var4 = var1.getMinusSign();
      char var5 = var1.getDecimalSeparator();
      return var2 == '0' && var4 == '-' && var5 == '.' ? STANDARD : new DecimalStyle(var2, var3, var4, var5);
   }

   private DecimalStyle(char var1, char var2, char var3, char var4) {
      this.zeroDigit = var1;
      this.positiveSign = var2;
      this.negativeSign = var3;
      this.decimalSeparator = var4;
   }

   public char getZeroDigit() {
      return this.zeroDigit;
   }

   public DecimalStyle withZeroDigit(char var1) {
      return var1 == this.zeroDigit ? this : new DecimalStyle(var1, this.positiveSign, this.negativeSign, this.decimalSeparator);
   }

   public char getPositiveSign() {
      return this.positiveSign;
   }

   public DecimalStyle withPositiveSign(char var1) {
      return var1 == this.positiveSign ? this : new DecimalStyle(this.zeroDigit, var1, this.negativeSign, this.decimalSeparator);
   }

   public char getNegativeSign() {
      return this.negativeSign;
   }

   public DecimalStyle withNegativeSign(char var1) {
      return var1 == this.negativeSign ? this : new DecimalStyle(this.zeroDigit, this.positiveSign, var1, this.decimalSeparator);
   }

   public char getDecimalSeparator() {
      return this.decimalSeparator;
   }

   public DecimalStyle withDecimalSeparator(char var1) {
      return var1 == this.decimalSeparator ? this : new DecimalStyle(this.zeroDigit, this.positiveSign, this.negativeSign, var1);
   }

   int convertToDigit(char var1) {
      int var2 = var1 - this.zeroDigit;
      return var2 >= 0 && var2 <= 9 ? var2 : -1;
   }

   String convertNumberToI18N(String var1) {
      if (this.zeroDigit == '0') {
         return var1;
      } else {
         int var2 = this.zeroDigit - 48;
         char[] var3 = var1.toCharArray();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            var3[var4] = (char)(var3[var4] + var2);
         }

         return new String(var3);
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof DecimalStyle)) {
         return false;
      } else {
         DecimalStyle var2 = (DecimalStyle)var1;
         return this.zeroDigit == var2.zeroDigit && this.positiveSign == var2.positiveSign && this.negativeSign == var2.negativeSign && this.decimalSeparator == var2.decimalSeparator;
      }
   }

   public int hashCode() {
      return this.zeroDigit + this.positiveSign + this.negativeSign + this.decimalSeparator;
   }

   public String toString() {
      return "DecimalStyle[" + this.zeroDigit + this.positiveSign + this.negativeSign + this.decimalSeparator + "]";
   }
}
