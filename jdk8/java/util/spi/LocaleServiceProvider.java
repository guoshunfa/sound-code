package java.util.spi;

import java.util.Locale;

public abstract class LocaleServiceProvider {
   protected LocaleServiceProvider() {
   }

   public abstract Locale[] getAvailableLocales();

   public boolean isSupportedLocale(Locale var1) {
      var1 = var1.stripExtensions();
      Locale[] var2 = this.getAvailableLocales();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Locale var5 = var2[var4];
         if (var1.equals(var5.stripExtensions())) {
            return true;
         }
      }

      return false;
   }
}
