package sun.util.locale.provider;

import java.text.spi.BreakIteratorProvider;
import java.text.spi.CollatorProvider;
import java.text.spi.DateFormatProvider;
import java.text.spi.DateFormatSymbolsProvider;
import java.text.spi.DecimalFormatSymbolsProvider;
import java.text.spi.NumberFormatProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.IllformedLocaleException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.spi.CalendarDataProvider;
import java.util.spi.CurrencyNameProvider;
import java.util.spi.LocaleNameProvider;
import java.util.spi.LocaleServiceProvider;
import java.util.spi.TimeZoneNameProvider;
import sun.util.logging.PlatformLogger;

public final class LocaleServiceProviderPool {
   private static ConcurrentMap<Class<? extends LocaleServiceProvider>, LocaleServiceProviderPool> poolOfPools = new ConcurrentHashMap();
   private ConcurrentMap<LocaleProviderAdapter.Type, LocaleServiceProvider> providers = new ConcurrentHashMap();
   private ConcurrentMap<Locale, List<LocaleProviderAdapter.Type>> providersCache = new ConcurrentHashMap();
   private Set<Locale> availableLocales = null;
   private Class<? extends LocaleServiceProvider> providerClass;
   static final Class<LocaleServiceProvider>[] spiClasses = (Class[])(new Class[]{BreakIteratorProvider.class, CollatorProvider.class, DateFormatProvider.class, DateFormatSymbolsProvider.class, DecimalFormatSymbolsProvider.class, NumberFormatProvider.class, CurrencyNameProvider.class, LocaleNameProvider.class, TimeZoneNameProvider.class, CalendarDataProvider.class});
   private static List<LocaleProviderAdapter.Type> NULL_LIST = Collections.emptyList();

   public static LocaleServiceProviderPool getPool(Class<? extends LocaleServiceProvider> var0) {
      LocaleServiceProviderPool var1 = (LocaleServiceProviderPool)poolOfPools.get(var0);
      if (var1 == null) {
         LocaleServiceProviderPool var2 = new LocaleServiceProviderPool(var0);
         var1 = (LocaleServiceProviderPool)poolOfPools.putIfAbsent(var0, var2);
         if (var1 == null) {
            var1 = var2;
         }
      }

      return var1;
   }

   private LocaleServiceProviderPool(Class<? extends LocaleServiceProvider> var1) {
      this.providerClass = var1;
      Iterator var2 = LocaleProviderAdapter.getAdapterPreference().iterator();

      while(var2.hasNext()) {
         LocaleProviderAdapter.Type var3 = (LocaleProviderAdapter.Type)var2.next();
         LocaleProviderAdapter var4 = LocaleProviderAdapter.forType(var3);
         if (var4 != null) {
            LocaleServiceProvider var5 = var4.getLocaleServiceProvider(var1);
            if (var5 != null) {
               this.providers.putIfAbsent(var3, var5);
            }
         }
      }

   }

   static void config(Class<? extends Object> var0, String var1) {
      PlatformLogger var2 = PlatformLogger.getLogger(var0.getCanonicalName());
      var2.config(var1);
   }

   public static Locale[] getAllAvailableLocales() {
      return (Locale[])LocaleServiceProviderPool.AllAvailableLocales.allAvailableLocales.clone();
   }

   public Locale[] getAvailableLocales() {
      HashSet var1 = new HashSet();
      var1.addAll(this.getAvailableLocaleSet());
      var1.addAll(Arrays.asList(LocaleProviderAdapter.forJRE().getAvailableLocales()));
      Locale[] var2 = new Locale[var1.size()];
      var1.toArray(var2);
      return var2;
   }

   private synchronized Set<Locale> getAvailableLocaleSet() {
      if (this.availableLocales == null) {
         this.availableLocales = new HashSet();
         Iterator var1 = this.providers.values().iterator();

         while(var1.hasNext()) {
            LocaleServiceProvider var2 = (LocaleServiceProvider)var1.next();
            Locale[] var3 = var2.getAvailableLocales();
            Locale[] var4 = var3;
            int var5 = var3.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               Locale var7 = var4[var6];
               this.availableLocales.add(getLookupLocale(var7));
            }
         }
      }

      return this.availableLocales;
   }

   boolean hasProviders() {
      return this.providers.size() != 1 || this.providers.get(LocaleProviderAdapter.Type.JRE) == null && this.providers.get(LocaleProviderAdapter.Type.FALLBACK) == null;
   }

   public <P extends LocaleServiceProvider, S> S getLocalizedObject(LocaleServiceProviderPool.LocalizedObjectGetter<P, S> var1, Locale var2, Object... var3) {
      return this.getLocalizedObjectImpl(var1, var2, true, (String)null, var3);
   }

   public <P extends LocaleServiceProvider, S> S getLocalizedObject(LocaleServiceProviderPool.LocalizedObjectGetter<P, S> var1, Locale var2, String var3, Object... var4) {
      return this.getLocalizedObjectImpl(var1, var2, false, var3, var4);
   }

   private <P extends LocaleServiceProvider, S> S getLocalizedObjectImpl(LocaleServiceProviderPool.LocalizedObjectGetter<P, S> var1, Locale var2, boolean var3, String var4, Object... var5) {
      if (var2 == null) {
         throw new NullPointerException();
      } else if (!this.hasProviders()) {
         return var1.getObject((LocaleServiceProvider)this.providers.get(LocaleProviderAdapter.defaultLocaleProviderAdapter), var2, var4, var5);
      } else {
         List var6 = getLookupLocales(var2);
         Set var7 = this.getAvailableLocaleSet();
         Iterator var8 = var6.iterator();

         while(true) {
            Locale var9;
            do {
               if (!var8.hasNext()) {
                  return null;
               }

               var9 = (Locale)var8.next();
            } while(!var7.contains(var9));

            Iterator var11 = this.findProviders(var9).iterator();

            while(var11.hasNext()) {
               LocaleProviderAdapter.Type var12 = (LocaleProviderAdapter.Type)var11.next();
               LocaleServiceProvider var13 = (LocaleServiceProvider)this.providers.get(var12);
               Object var10 = var1.getObject(var13, var2, var4, var5);
               if (var10 != null) {
                  return var10;
               }

               if (var3) {
                  config(LocaleServiceProviderPool.class, "A locale sensitive service provider returned null for a localized objects,  which should not happen.  provider: " + var13 + " locale: " + var2);
               }
            }
         }
      }
   }

   private List<LocaleProviderAdapter.Type> findProviders(Locale var1) {
      Object var2 = (List)this.providersCache.get(var1);
      if (var2 == null) {
         Iterator var3 = LocaleProviderAdapter.getAdapterPreference().iterator();

         while(var3.hasNext()) {
            LocaleProviderAdapter.Type var4 = (LocaleProviderAdapter.Type)var3.next();
            LocaleServiceProvider var5 = (LocaleServiceProvider)this.providers.get(var4);
            if (var5 != null && var5.isSupportedLocale(var1)) {
               if (var2 == null) {
                  var2 = new ArrayList(2);
               }

               ((List)var2).add(var4);
            }
         }

         if (var2 == null) {
            var2 = NULL_LIST;
         }

         List var6 = (List)this.providersCache.putIfAbsent(var1, var2);
         if (var6 != null) {
            var2 = var6;
         }
      }

      return (List)var2;
   }

   static List<Locale> getLookupLocales(Locale var0) {
      List var1 = ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT).getCandidateLocales("", var0);
      return var1;
   }

   static Locale getLookupLocale(Locale var0) {
      Locale var1 = var0;
      if (var0.hasExtensions() && !var0.equals(JRELocaleConstants.JA_JP_JP) && !var0.equals(JRELocaleConstants.TH_TH_TH)) {
         Locale.Builder var2 = new Locale.Builder();

         try {
            var2.setLocale(var0);
            var2.clearExtensions();
            var1 = var2.build();
         } catch (IllformedLocaleException var4) {
            config(LocaleServiceProviderPool.class, "A locale(" + var0 + ") has non-empty extensions, but has illformed fields.");
            var1 = new Locale(var0.getLanguage(), var0.getCountry(), var0.getVariant());
         }
      }

      return var1;
   }

   public interface LocalizedObjectGetter<P extends LocaleServiceProvider, S> {
      S getObject(P var1, Locale var2, String var3, Object... var4);
   }

   private static class AllAvailableLocales {
      static final Locale[] allAvailableLocales;

      static {
         HashSet var0 = new HashSet();
         Class[] var1 = LocaleServiceProviderPool.spiClasses;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Class var4 = var1[var3];
            LocaleServiceProviderPool var5 = LocaleServiceProviderPool.getPool(var4);
            var0.addAll(var5.getAvailableLocaleSet());
         }

         allAvailableLocales = (Locale[])var0.toArray(new Locale[0]);
      }
   }
}
