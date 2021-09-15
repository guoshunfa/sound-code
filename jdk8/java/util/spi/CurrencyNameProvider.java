package java.util.spi;

import java.util.Locale;
import java.util.ResourceBundle;

public abstract class CurrencyNameProvider extends LocaleServiceProvider {
   protected CurrencyNameProvider() {
   }

   public abstract String getSymbol(String var1, Locale var2);

   public String getDisplayName(String var1, Locale var2) {
      if (var1 != null && var2 != null) {
         char[] var3 = var1.toCharArray();
         if (var3.length != 3) {
            throw new IllegalArgumentException("The currencyCode is not in the form of three upper-case letters.");
         } else {
            char[] var4 = var3;
            int var5 = var3.length;

            int var6;
            for(var6 = 0; var6 < var5; ++var6) {
               char var7 = var4[var6];
               if (var7 < 'A' || var7 > 'Z') {
                  throw new IllegalArgumentException("The currencyCode is not in the form of three upper-case letters.");
               }
            }

            ResourceBundle.Control var9 = ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT);
            Locale[] var10 = this.getAvailableLocales();
            var6 = var10.length;

            for(int var11 = 0; var11 < var6; ++var11) {
               Locale var8 = var10[var11];
               if (var9.getCandidateLocales("", var8).contains(var2)) {
                  return null;
               }
            }

            throw new IllegalArgumentException("The locale is not available");
         }
      } else {
         throw new NullPointerException();
      }
   }
}
