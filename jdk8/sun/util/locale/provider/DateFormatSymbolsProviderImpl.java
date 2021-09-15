package sun.util.locale.provider;

import java.text.DateFormatSymbols;
import java.text.spi.DateFormatSymbolsProvider;
import java.util.Locale;
import java.util.Set;

public class DateFormatSymbolsProviderImpl extends DateFormatSymbolsProvider implements AvailableLanguageTags {
   private final LocaleProviderAdapter.Type type;
   private final Set<String> langtags;

   public DateFormatSymbolsProviderImpl(LocaleProviderAdapter.Type var1, Set<String> var2) {
      this.type = var1;
      this.langtags = var2;
   }

   public Locale[] getAvailableLocales() {
      return LocaleProviderAdapter.toLocaleArray(this.langtags);
   }

   public boolean isSupportedLocale(Locale var1) {
      return LocaleProviderAdapter.isSupportedLocale(var1, this.type, this.langtags);
   }

   public DateFormatSymbols getInstance(Locale var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return new DateFormatSymbols(var1);
      }
   }

   public Set<String> getAvailableLanguageTags() {
      return this.langtags;
   }
}
