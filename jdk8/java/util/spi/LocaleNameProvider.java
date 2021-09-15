package java.util.spi;

import java.util.Locale;

public abstract class LocaleNameProvider extends LocaleServiceProvider {
   protected LocaleNameProvider() {
   }

   public abstract String getDisplayLanguage(String var1, Locale var2);

   public String getDisplayScript(String var1, Locale var2) {
      return null;
   }

   public abstract String getDisplayCountry(String var1, Locale var2);

   public abstract String getDisplayVariant(String var1, Locale var2);
}
