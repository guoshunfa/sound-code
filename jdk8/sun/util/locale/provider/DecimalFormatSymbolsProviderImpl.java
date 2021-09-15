package sun.util.locale.provider;

import java.text.DecimalFormatSymbols;
import java.text.spi.DecimalFormatSymbolsProvider;
import java.util.Locale;
import java.util.Set;

public class DecimalFormatSymbolsProviderImpl extends DecimalFormatSymbolsProvider implements AvailableLanguageTags {
   private final LocaleProviderAdapter.Type type;
   private final Set<String> langtags;

   public DecimalFormatSymbolsProviderImpl(LocaleProviderAdapter.Type var1, Set<String> var2) {
      this.type = var1;
      this.langtags = var2;
   }

   public Locale[] getAvailableLocales() {
      return LocaleProviderAdapter.toLocaleArray(this.langtags);
   }

   public boolean isSupportedLocale(Locale var1) {
      return LocaleProviderAdapter.isSupportedLocale(var1, this.type, this.langtags);
   }

   public DecimalFormatSymbols getInstance(Locale var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return new DecimalFormatSymbols(var1);
      }
   }

   public Set<String> getAvailableLanguageTags() {
      return this.langtags;
   }
}
