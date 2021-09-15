package sun.util.locale.provider;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.spi.NumberFormatProvider;
import java.util.Currency;
import java.util.Locale;
import java.util.Set;

public class NumberFormatProviderImpl extends NumberFormatProvider implements AvailableLanguageTags {
   private static final int NUMBERSTYLE = 0;
   private static final int CURRENCYSTYLE = 1;
   private static final int PERCENTSTYLE = 2;
   private static final int SCIENTIFICSTYLE = 3;
   private static final int INTEGERSTYLE = 4;
   private final LocaleProviderAdapter.Type type;
   private final Set<String> langtags;

   public NumberFormatProviderImpl(LocaleProviderAdapter.Type var1, Set<String> var2) {
      this.type = var1;
      this.langtags = var2;
   }

   public Locale[] getAvailableLocales() {
      return LocaleProviderAdapter.forType(this.type).getAvailableLocales();
   }

   public boolean isSupportedLocale(Locale var1) {
      return LocaleProviderAdapter.isSupportedLocale(var1, this.type, this.langtags);
   }

   public NumberFormat getCurrencyInstance(Locale var1) {
      return this.getInstance(var1, 1);
   }

   public NumberFormat getIntegerInstance(Locale var1) {
      return this.getInstance(var1, 4);
   }

   public NumberFormat getNumberInstance(Locale var1) {
      return this.getInstance(var1, 0);
   }

   public NumberFormat getPercentInstance(Locale var1) {
      return this.getInstance(var1, 2);
   }

   private NumberFormat getInstance(Locale var1, int var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         LocaleProviderAdapter var3 = LocaleProviderAdapter.forType(this.type);
         String[] var4 = var3.getLocaleResources(var1).getNumberPatterns();
         DecimalFormatSymbols var5 = DecimalFormatSymbols.getInstance(var1);
         int var6 = var2 == 4 ? 0 : var2;
         DecimalFormat var7 = new DecimalFormat(var4[var6], var5);
         if (var2 == 4) {
            var7.setMaximumFractionDigits(0);
            var7.setDecimalSeparatorAlwaysShown(false);
            var7.setParseIntegerOnly(true);
         } else if (var2 == 1) {
            adjustForCurrencyDefaultFractionDigits(var7, var5);
         }

         return var7;
      }
   }

   private static void adjustForCurrencyDefaultFractionDigits(DecimalFormat var0, DecimalFormatSymbols var1) {
      Currency var2 = var1.getCurrency();
      if (var2 == null) {
         try {
            var2 = Currency.getInstance(var1.getInternationalCurrencySymbol());
         } catch (IllegalArgumentException var5) {
         }
      }

      if (var2 != null) {
         int var3 = var2.getDefaultFractionDigits();
         if (var3 != -1) {
            int var4 = var0.getMinimumFractionDigits();
            if (var4 == var0.getMaximumFractionDigits()) {
               var0.setMinimumFractionDigits(var3);
               var0.setMaximumFractionDigits(var3);
            } else {
               var0.setMinimumFractionDigits(Math.min(var3, var4));
               var0.setMaximumFractionDigits(var3);
            }
         }
      }

   }

   public Set<String> getAvailableLanguageTags() {
      return this.langtags;
   }
}
