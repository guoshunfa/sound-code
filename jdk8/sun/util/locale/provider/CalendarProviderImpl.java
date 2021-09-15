package sun.util.locale.provider;

import java.util.Calendar;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import sun.util.spi.CalendarProvider;

public class CalendarProviderImpl extends CalendarProvider implements AvailableLanguageTags {
   private final LocaleProviderAdapter.Type type;
   private final Set<String> langtags;

   public CalendarProviderImpl(LocaleProviderAdapter.Type var1, Set<String> var2) {
      this.type = var1;
      this.langtags = var2;
   }

   public Locale[] getAvailableLocales() {
      return LocaleProviderAdapter.toLocaleArray(this.langtags);
   }

   public boolean isSupportedLocale(Locale var1) {
      return true;
   }

   public Calendar getInstance(TimeZone var1, Locale var2) {
      return (new Calendar.Builder()).setLocale(var2).setTimeZone(var1).setInstant(System.currentTimeMillis()).build();
   }

   public Set<String> getAvailableLanguageTags() {
      return this.langtags;
   }
}
