package sun.util.locale.provider;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.spi.BreakIteratorProvider;
import java.text.spi.CollatorProvider;
import java.text.spi.DateFormatProvider;
import java.text.spi.DateFormatSymbolsProvider;
import java.text.spi.DecimalFormatSymbolsProvider;
import java.text.spi.NumberFormatProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.spi.CalendarDataProvider;
import java.util.spi.CalendarNameProvider;
import java.util.spi.CurrencyNameProvider;
import java.util.spi.LocaleNameProvider;
import java.util.spi.LocaleServiceProvider;
import java.util.spi.TimeZoneNameProvider;
import sun.security.action.GetPropertyAction;
import sun.util.cldr.CLDRLocaleProviderAdapter;
import sun.util.spi.CalendarProvider;

public abstract class LocaleProviderAdapter {
   private static final List<LocaleProviderAdapter.Type> adapterPreference;
   private static LocaleProviderAdapter jreLocaleProviderAdapter = new JRELocaleProviderAdapter();
   private static LocaleProviderAdapter spiLocaleProviderAdapter = new SPILocaleProviderAdapter();
   private static LocaleProviderAdapter cldrLocaleProviderAdapter = null;
   private static LocaleProviderAdapter hostLocaleProviderAdapter = null;
   private static LocaleProviderAdapter fallbackLocaleProviderAdapter = null;
   static LocaleProviderAdapter.Type defaultLocaleProviderAdapter = null;
   private static ConcurrentMap<Class<? extends LocaleServiceProvider>, ConcurrentMap<Locale, LocaleProviderAdapter>> adapterCache = new ConcurrentHashMap();

   public static LocaleProviderAdapter forType(LocaleProviderAdapter.Type var0) {
      switch(var0) {
      case CLDR:
         return cldrLocaleProviderAdapter;
      case HOST:
         return hostLocaleProviderAdapter;
      case JRE:
         return jreLocaleProviderAdapter;
      case SPI:
         return spiLocaleProviderAdapter;
      case FALLBACK:
         return fallbackLocaleProviderAdapter;
      default:
         throw new InternalError("unknown locale data adapter type");
      }
   }

   public static LocaleProviderAdapter forJRE() {
      return jreLocaleProviderAdapter;
   }

   public static LocaleProviderAdapter getResourceBundleBased() {
      Iterator var0 = getAdapterPreference().iterator();

      LocaleProviderAdapter.Type var1;
      do {
         if (!var0.hasNext()) {
            throw new InternalError();
         }

         var1 = (LocaleProviderAdapter.Type)var0.next();
      } while(var1 != LocaleProviderAdapter.Type.JRE && var1 != LocaleProviderAdapter.Type.CLDR && var1 != LocaleProviderAdapter.Type.FALLBACK);

      return forType(var1);
   }

   public static List<LocaleProviderAdapter.Type> getAdapterPreference() {
      return adapterPreference;
   }

   public static LocaleProviderAdapter getAdapter(Class<? extends LocaleServiceProvider> var0, Locale var1) {
      Object var3 = (ConcurrentMap)adapterCache.get(var0);
      LocaleProviderAdapter var2;
      if (var3 != null) {
         if ((var2 = (LocaleProviderAdapter)((ConcurrentMap)var3).get(var1)) != null) {
            return var2;
         }
      } else {
         var3 = new ConcurrentHashMap();
         adapterCache.putIfAbsent(var0, var3);
      }

      var2 = findAdapter(var0, var1);
      if (var2 != null) {
         ((ConcurrentMap)var3).putIfAbsent(var1, var2);
         return var2;
      } else {
         List var4 = ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_DEFAULT).getCandidateLocales("", var1);
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            Locale var6 = (Locale)var5.next();
            if (!var6.equals(var1)) {
               var2 = findAdapter(var0, var6);
               if (var2 != null) {
                  ((ConcurrentMap)var3).putIfAbsent(var1, var2);
                  return var2;
               }
            }
         }

         ((ConcurrentMap)var3).putIfAbsent(var1, fallbackLocaleProviderAdapter);
         return fallbackLocaleProviderAdapter;
      }
   }

   private static LocaleProviderAdapter findAdapter(Class<? extends LocaleServiceProvider> var0, Locale var1) {
      Iterator var2 = getAdapterPreference().iterator();

      LocaleProviderAdapter var4;
      LocaleServiceProvider var5;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         LocaleProviderAdapter.Type var3 = (LocaleProviderAdapter.Type)var2.next();
         var4 = forType(var3);
         var5 = var4.getLocaleServiceProvider(var0);
      } while(var5 == null || !var5.isSupportedLocale(var1));

      return var4;
   }

   public static boolean isSupportedLocale(Locale var0, LocaleProviderAdapter.Type var1, Set<String> var2) {
      assert var1 == LocaleProviderAdapter.Type.JRE || var1 == LocaleProviderAdapter.Type.CLDR || var1 == LocaleProviderAdapter.Type.FALLBACK;

      if (Locale.ROOT.equals(var0)) {
         return true;
      } else if (var1 == LocaleProviderAdapter.Type.FALLBACK) {
         return false;
      } else {
         var0 = var0.stripExtensions();
         if (var2.contains(var0.toLanguageTag())) {
            return true;
         } else if (var1 != LocaleProviderAdapter.Type.JRE) {
            return false;
         } else {
            String var3 = var0.toString().replace('_', '-');
            return var2.contains(var3) || "ja-JP-JP".equals(var3) || "th-TH-TH".equals(var3) || "no-NO-NY".equals(var3);
         }
      }
   }

   public static Locale[] toLocaleArray(Set<String> var0) {
      Locale[] var1 = new Locale[var0.size() + 1];
      byte var2 = 0;
      int var7 = var2 + 1;
      var1[var2] = Locale.ROOT;
      Iterator var3 = var0.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         byte var6 = -1;
         switch(var4.hashCode()) {
         case -518283308:
            if (var4.equals("th-TH-TH")) {
               var6 = 1;
            }
            break;
         case 1601894167:
            if (var4.equals("ja-JP-JP")) {
               var6 = 0;
            }
         }

         switch(var6) {
         case 0:
            var1[var7++] = JRELocaleConstants.JA_JP_JP;
            break;
         case 1:
            var1[var7++] = JRELocaleConstants.TH_TH_TH;
            break;
         default:
            var1[var7++] = Locale.forLanguageTag(var4);
         }
      }

      return var1;
   }

   public abstract LocaleProviderAdapter.Type getAdapterType();

   public abstract <P extends LocaleServiceProvider> P getLocaleServiceProvider(Class<P> var1);

   public abstract BreakIteratorProvider getBreakIteratorProvider();

   public abstract CollatorProvider getCollatorProvider();

   public abstract DateFormatProvider getDateFormatProvider();

   public abstract DateFormatSymbolsProvider getDateFormatSymbolsProvider();

   public abstract DecimalFormatSymbolsProvider getDecimalFormatSymbolsProvider();

   public abstract NumberFormatProvider getNumberFormatProvider();

   public abstract CurrencyNameProvider getCurrencyNameProvider();

   public abstract LocaleNameProvider getLocaleNameProvider();

   public abstract TimeZoneNameProvider getTimeZoneNameProvider();

   public abstract CalendarDataProvider getCalendarDataProvider();

   public abstract CalendarNameProvider getCalendarNameProvider();

   public abstract CalendarProvider getCalendarProvider();

   public abstract LocaleResources getLocaleResources(Locale var1);

   public abstract Locale[] getAvailableLocales();

   static {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.locale.providers")));
      ArrayList var1 = new ArrayList();
      if (var0 != null && var0.length() != 0) {
         String[] var2 = var0.split(",");
         String[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String var6 = var3[var5];

            try {
               LocaleProviderAdapter.Type var7 = LocaleProviderAdapter.Type.valueOf(var6.trim().toUpperCase(Locale.ROOT));
               switch(var7) {
               case CLDR:
                  if (cldrLocaleProviderAdapter == null) {
                     cldrLocaleProviderAdapter = new CLDRLocaleProviderAdapter();
                  }
                  break;
               case HOST:
                  if (hostLocaleProviderAdapter == null) {
                     hostLocaleProviderAdapter = new HostLocaleProviderAdapter();
                  }
               }

               if (!var1.contains(var7)) {
                  var1.add(var7);
               }
            } catch (UnsupportedOperationException | IllegalArgumentException var8) {
               LocaleServiceProviderPool.config(LocaleProviderAdapter.class, var8.toString());
            }
         }
      }

      if (!var1.isEmpty()) {
         if (!var1.contains(LocaleProviderAdapter.Type.JRE)) {
            fallbackLocaleProviderAdapter = new FallbackLocaleProviderAdapter();
            var1.add(LocaleProviderAdapter.Type.FALLBACK);
            defaultLocaleProviderAdapter = LocaleProviderAdapter.Type.FALLBACK;
         } else {
            defaultLocaleProviderAdapter = LocaleProviderAdapter.Type.JRE;
         }
      } else {
         var1.add(LocaleProviderAdapter.Type.JRE);
         var1.add(LocaleProviderAdapter.Type.SPI);
         defaultLocaleProviderAdapter = LocaleProviderAdapter.Type.JRE;
      }

      adapterPreference = Collections.unmodifiableList(var1);
   }

   public static enum Type {
      JRE("sun.util.resources", "sun.text.resources"),
      CLDR("sun.util.resources.cldr", "sun.text.resources.cldr"),
      SPI,
      HOST,
      FALLBACK("sun.util.resources", "sun.text.resources");

      private final String UTIL_RESOURCES_PACKAGE;
      private final String TEXT_RESOURCES_PACKAGE;

      private Type() {
         this((String)null, (String)null);
      }

      private Type(String var3, String var4) {
         this.UTIL_RESOURCES_PACKAGE = var3;
         this.TEXT_RESOURCES_PACKAGE = var4;
      }

      public String getUtilResourcesPackage() {
         return this.UTIL_RESOURCES_PACKAGE;
      }

      public String getTextResourcesPackage() {
         return this.TEXT_RESOURCES_PACKAGE;
      }
   }
}
