package java.util.spi;

import java.util.Locale;
import java.util.Map;

public abstract class CalendarNameProvider extends LocaleServiceProvider {
   protected CalendarNameProvider() {
   }

   public abstract String getDisplayName(String var1, int var2, int var3, int var4, Locale var5);

   public abstract Map<String, Integer> getDisplayNames(String var1, int var2, int var3, Locale var4);
}
