package sun.util.locale.provider;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.spi.TimeZoneNameProvider;

public class TimeZoneNameProviderImpl extends TimeZoneNameProvider {
   private final LocaleProviderAdapter.Type type;
   private final Set<String> langtags;

   TimeZoneNameProviderImpl(LocaleProviderAdapter.Type var1, Set<String> var2) {
      this.type = var1;
      this.langtags = var2;
   }

   public Locale[] getAvailableLocales() {
      return LocaleProviderAdapter.toLocaleArray(this.langtags);
   }

   public boolean isSupportedLocale(Locale var1) {
      return LocaleProviderAdapter.isSupportedLocale(var1, this.type, this.langtags);
   }

   public String getDisplayName(String var1, boolean var2, int var3, Locale var4) {
      String[] var5 = this.getDisplayNameArray(var1, var4);
      if (Objects.nonNull(var5)) {
         assert var5.length >= 7;

         int var6 = var2 ? 3 : 1;
         if (var3 == 0) {
            ++var6;
         }

         return var5[var6];
      } else {
         return null;
      }
   }

   public String getGenericDisplayName(String var1, int var2, Locale var3) {
      String[] var4 = this.getDisplayNameArray(var1, var3);
      if (Objects.nonNull(var4)) {
         assert var4.length >= 7;

         return var4[var2 == 1 ? 5 : 6];
      } else {
         return null;
      }
   }

   private String[] getDisplayNameArray(String var1, Locale var2) {
      Objects.requireNonNull(var1);
      Objects.requireNonNull(var2);
      return LocaleProviderAdapter.forType(this.type).getLocaleResources(var2).getTimeZoneNames(var1);
   }

   String[][] getZoneStrings(Locale var1) {
      return LocaleProviderAdapter.forType(this.type).getLocaleResources(var1).getZoneStrings();
   }
}
