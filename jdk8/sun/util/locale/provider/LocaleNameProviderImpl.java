package sun.util.locale.provider;

import java.util.Locale;
import java.util.Set;
import java.util.spi.LocaleNameProvider;

public class LocaleNameProviderImpl extends LocaleNameProvider implements AvailableLanguageTags {
   private final LocaleProviderAdapter.Type type;
   private final Set<String> langtags;

   public LocaleNameProviderImpl(LocaleProviderAdapter.Type var1, Set<String> var2) {
      this.type = var1;
      this.langtags = var2;
   }

   public Locale[] getAvailableLocales() {
      return LocaleProviderAdapter.toLocaleArray(this.langtags);
   }

   public boolean isSupportedLocale(Locale var1) {
      return LocaleProviderAdapter.isSupportedLocale(var1, this.type, this.langtags);
   }

   public String getDisplayLanguage(String var1, Locale var2) {
      return this.getDisplayString(var1, var2);
   }

   public String getDisplayScript(String var1, Locale var2) {
      return this.getDisplayString(var1, var2);
   }

   public String getDisplayCountry(String var1, Locale var2) {
      return this.getDisplayString(var1, var2);
   }

   public String getDisplayVariant(String var1, Locale var2) {
      return this.getDisplayString("%%" + var1, var2);
   }

   private String getDisplayString(String var1, Locale var2) {
      if (var1 != null && var2 != null) {
         return LocaleProviderAdapter.forType(this.type).getLocaleResources(var2).getLocaleName(var1);
      } else {
         throw new NullPointerException();
      }
   }

   public Set<String> getAvailableLanguageTags() {
      return this.langtags;
   }
}
