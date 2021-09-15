package java.util.spi;

import java.util.Locale;

public abstract class CalendarDataProvider extends LocaleServiceProvider {
   protected CalendarDataProvider() {
   }

   public abstract int getFirstDayOfWeek(Locale var1);

   public abstract int getMinimalDaysInFirstWeek(Locale var1);
}
