package sun.util.locale.provider;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

public class FallbackLocaleProviderAdapter extends JRELocaleProviderAdapter {
   private static final Set<String> rootTagSet;
   private final LocaleResources rootLocaleResources;

   public FallbackLocaleProviderAdapter() {
      this.rootLocaleResources = new LocaleResources(this, Locale.ROOT);
   }

   public LocaleProviderAdapter.Type getAdapterType() {
      return LocaleProviderAdapter.Type.FALLBACK;
   }

   public LocaleResources getLocaleResources(Locale var1) {
      return this.rootLocaleResources;
   }

   protected Set<String> createLanguageTagSet(String var1) {
      return rootTagSet;
   }

   static {
      rootTagSet = Collections.singleton(Locale.ROOT.toLanguageTag());
   }
}
