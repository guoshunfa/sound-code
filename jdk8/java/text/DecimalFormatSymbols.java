package java.text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.spi.DecimalFormatSymbolsProvider;
import java.util.Currency;
import java.util.Locale;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleServiceProviderPool;
import sun.util.locale.provider.ResourceBundleBasedAdapter;

public class DecimalFormatSymbols implements Cloneable, Serializable {
   private char zeroDigit;
   private char groupingSeparator;
   private char decimalSeparator;
   private char perMill;
   private char percent;
   private char digit;
   private char patternSeparator;
   private String infinity;
   private String NaN;
   private char minusSign;
   private String currencySymbol;
   private String intlCurrencySymbol;
   private char monetarySeparator;
   private char exponential;
   private String exponentialSeparator;
   private Locale locale;
   private transient Currency currency;
   static final long serialVersionUID = 5772796243397350300L;
   private static final int currentSerialVersion = 3;
   private int serialVersionOnStream = 3;

   public DecimalFormatSymbols() {
      this.initialize(Locale.getDefault(Locale.Category.FORMAT));
   }

   public DecimalFormatSymbols(Locale var1) {
      this.initialize(var1);
   }

   public static Locale[] getAvailableLocales() {
      LocaleServiceProviderPool var0 = LocaleServiceProviderPool.getPool(DecimalFormatSymbolsProvider.class);
      return var0.getAvailableLocales();
   }

   public static final DecimalFormatSymbols getInstance() {
      return getInstance(Locale.getDefault(Locale.Category.FORMAT));
   }

   public static final DecimalFormatSymbols getInstance(Locale var0) {
      LocaleProviderAdapter var1 = LocaleProviderAdapter.getAdapter(DecimalFormatSymbolsProvider.class, var0);
      DecimalFormatSymbolsProvider var2 = var1.getDecimalFormatSymbolsProvider();
      DecimalFormatSymbols var3 = var2.getInstance(var0);
      if (var3 == null) {
         var2 = LocaleProviderAdapter.forJRE().getDecimalFormatSymbolsProvider();
         var3 = var2.getInstance(var0);
      }

      return var3;
   }

   public char getZeroDigit() {
      return this.zeroDigit;
   }

   public void setZeroDigit(char var1) {
      this.zeroDigit = var1;
   }

   public char getGroupingSeparator() {
      return this.groupingSeparator;
   }

   public void setGroupingSeparator(char var1) {
      this.groupingSeparator = var1;
   }

   public char getDecimalSeparator() {
      return this.decimalSeparator;
   }

   public void setDecimalSeparator(char var1) {
      this.decimalSeparator = var1;
   }

   public char getPerMill() {
      return this.perMill;
   }

   public void setPerMill(char var1) {
      this.perMill = var1;
   }

   public char getPercent() {
      return this.percent;
   }

   public void setPercent(char var1) {
      this.percent = var1;
   }

   public char getDigit() {
      return this.digit;
   }

   public void setDigit(char var1) {
      this.digit = var1;
   }

   public char getPatternSeparator() {
      return this.patternSeparator;
   }

   public void setPatternSeparator(char var1) {
      this.patternSeparator = var1;
   }

   public String getInfinity() {
      return this.infinity;
   }

   public void setInfinity(String var1) {
      this.infinity = var1;
   }

   public String getNaN() {
      return this.NaN;
   }

   public void setNaN(String var1) {
      this.NaN = var1;
   }

   public char getMinusSign() {
      return this.minusSign;
   }

   public void setMinusSign(char var1) {
      this.minusSign = var1;
   }

   public String getCurrencySymbol() {
      return this.currencySymbol;
   }

   public void setCurrencySymbol(String var1) {
      this.currencySymbol = var1;
   }

   public String getInternationalCurrencySymbol() {
      return this.intlCurrencySymbol;
   }

   public void setInternationalCurrencySymbol(String var1) {
      this.intlCurrencySymbol = var1;
      this.currency = null;
      if (var1 != null) {
         try {
            this.currency = Currency.getInstance(var1);
            this.currencySymbol = this.currency.getSymbol();
         } catch (IllegalArgumentException var3) {
         }
      }

   }

   public Currency getCurrency() {
      return this.currency;
   }

   public void setCurrency(Currency var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.currency = var1;
         this.intlCurrencySymbol = var1.getCurrencyCode();
         this.currencySymbol = var1.getSymbol(this.locale);
      }
   }

   public char getMonetaryDecimalSeparator() {
      return this.monetarySeparator;
   }

   public void setMonetaryDecimalSeparator(char var1) {
      this.monetarySeparator = var1;
   }

   char getExponentialSymbol() {
      return this.exponential;
   }

   public String getExponentSeparator() {
      return this.exponentialSeparator;
   }

   void setExponentialSymbol(char var1) {
      this.exponential = var1;
   }

   public void setExponentSeparator(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.exponentialSeparator = var1;
      }
   }

   public Object clone() {
      try {
         return (DecimalFormatSymbols)super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2);
      }
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (this == var1) {
         return true;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         DecimalFormatSymbols var2 = (DecimalFormatSymbols)var1;
         return this.zeroDigit == var2.zeroDigit && this.groupingSeparator == var2.groupingSeparator && this.decimalSeparator == var2.decimalSeparator && this.percent == var2.percent && this.perMill == var2.perMill && this.digit == var2.digit && this.minusSign == var2.minusSign && this.patternSeparator == var2.patternSeparator && this.infinity.equals(var2.infinity) && this.NaN.equals(var2.NaN) && this.currencySymbol.equals(var2.currencySymbol) && this.intlCurrencySymbol.equals(var2.intlCurrencySymbol) && this.currency == var2.currency && this.monetarySeparator == var2.monetarySeparator && this.exponentialSeparator.equals(var2.exponentialSeparator) && this.locale.equals(var2.locale);
      }
   }

   public int hashCode() {
      char var1 = this.zeroDigit;
      int var2 = var1 * 37 + this.groupingSeparator;
      var2 = var2 * 37 + this.decimalSeparator;
      return var2;
   }

   private void initialize(Locale var1) {
      this.locale = var1;
      LocaleProviderAdapter var2 = LocaleProviderAdapter.getAdapter(DecimalFormatSymbolsProvider.class, var1);
      if (!(var2 instanceof ResourceBundleBasedAdapter)) {
         var2 = LocaleProviderAdapter.getResourceBundleBased();
      }

      Object[] var3 = var2.getLocaleResources(var1).getDecimalFormatSymbolsData();
      String[] var4 = (String[])((String[])var3[0]);
      this.decimalSeparator = var4[0].charAt(0);
      this.groupingSeparator = var4[1].charAt(0);
      this.patternSeparator = var4[2].charAt(0);
      this.percent = var4[3].charAt(0);
      this.zeroDigit = var4[4].charAt(0);
      this.digit = var4[5].charAt(0);
      this.minusSign = var4[6].charAt(0);
      this.exponential = var4[7].charAt(0);
      this.exponentialSeparator = var4[7];
      this.perMill = var4[8].charAt(0);
      this.infinity = var4[9];
      this.NaN = var4[10];
      if (var1.getCountry().length() > 0) {
         try {
            this.currency = Currency.getInstance(var1);
         } catch (IllegalArgumentException var7) {
         }
      }

      if (this.currency != null) {
         this.intlCurrencySymbol = this.currency.getCurrencyCode();
         if (var3[1] != null && var3[1] == this.intlCurrencySymbol) {
            this.currencySymbol = (String)var3[2];
         } else {
            this.currencySymbol = this.currency.getSymbol(var1);
            var3[1] = this.intlCurrencySymbol;
            var3[2] = this.currencySymbol;
         }
      } else {
         this.intlCurrencySymbol = "XXX";

         try {
            this.currency = Currency.getInstance(this.intlCurrencySymbol);
         } catch (IllegalArgumentException var6) {
         }

         this.currencySymbol = "¤";
      }

      this.monetarySeparator = this.decimalSeparator;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      if (this.serialVersionOnStream < 1) {
         this.monetarySeparator = this.decimalSeparator;
         this.exponential = 'E';
      }

      if (this.serialVersionOnStream < 2) {
         this.locale = Locale.ROOT;
      }

      if (this.serialVersionOnStream < 3) {
         this.exponentialSeparator = Character.toString(this.exponential);
      }

      this.serialVersionOnStream = 3;
      if (this.intlCurrencySymbol != null) {
         try {
            this.currency = Currency.getInstance(this.intlCurrencySymbol);
         } catch (IllegalArgumentException var3) {
         }
      }

   }
}
