package java.util.spi;

import java.util.Locale;

public abstract class TimeZoneNameProvider extends LocaleServiceProvider {
   protected TimeZoneNameProvider() {
   }

   public abstract String getDisplayName(String var1, boolean var2, int var3, Locale var4);

   public String getGenericDisplayName(String var1, int var2, Locale var3) {
      return null;
   }
}
