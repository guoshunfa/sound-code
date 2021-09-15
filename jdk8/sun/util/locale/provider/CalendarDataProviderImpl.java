package sun.util.locale.provider;

import java.util.Locale;
import java.util.Set;
import java.util.spi.CalendarDataProvider;

public class CalendarDataProviderImpl extends CalendarDataProvider implements AvailableLanguageTags {
   private final LocaleProviderAdapter.Type type;
   private final Set<String> langtags;

   public CalendarDataProviderImpl(LocaleProviderAdapter.Type var1, Set<String> var2) {
      this.type = var1;
      this.langtags = var2;
   }

   public int getFirstDayOfWeek(Locale var1) {
      return LocaleProviderAdapter.forType(this.type).getLocaleResources(var1).getCalendarData("firstDayOfWeek");
   }

   public int getMinimalDaysInFirstWeek(Locale var1) {
      return LocaleProviderAdapter.forType(this.type).getLocaleResources(var1).getCalendarData("minimalDaysInFirstWeek");
   }

   public Locale[] getAvailableLocales() {
      return LocaleProviderAdapter.toLocaleArray(this.langtags);
   }

   public Set<String> getAvailableLanguageTags() {
      return this.langtags;
   }
}
