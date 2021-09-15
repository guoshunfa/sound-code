package sun.util.locale.provider;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.text.BreakIterator;
import java.text.Collator;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.spi.BreakIteratorProvider;
import java.text.spi.CollatorProvider;
import java.text.spi.DateFormatProvider;
import java.text.spi.DateFormatSymbolsProvider;
import java.text.spi.DecimalFormatSymbolsProvider;
import java.text.spi.NumberFormatProvider;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.spi.CalendarDataProvider;
import java.util.spi.CalendarNameProvider;
import java.util.spi.CurrencyNameProvider;
import java.util.spi.LocaleNameProvider;
import java.util.spi.LocaleServiceProvider;
import java.util.spi.TimeZoneNameProvider;

public class SPILocaleProviderAdapter extends AuxLocaleProviderAdapter {
   public LocaleProviderAdapter.Type getAdapterType() {
      return LocaleProviderAdapter.Type.SPI;
   }

   protected <P extends LocaleServiceProvider> P findInstalledProvider(final Class<P> var1) {
      try {
         return (LocaleServiceProvider)AccessController.doPrivileged(new PrivilegedExceptionAction<P>() {
            public P run() {
               LocaleServiceProvider var1x = null;

               LocaleServiceProvider var3;
               for(Iterator var2 = ServiceLoader.loadInstalled(var1).iterator(); var2.hasNext(); ((SPILocaleProviderAdapter.Delegate)var1x).addImpl(var3)) {
                  var3 = (LocaleServiceProvider)var2.next();
                  if (var1x == null) {
                     try {
                        var1x = (LocaleServiceProvider)Class.forName(SPILocaleProviderAdapter.class.getCanonicalName() + "$" + var1.getSimpleName() + "Delegate").newInstance();
                     } catch (InstantiationException | IllegalAccessException | ClassNotFoundException var5) {
                        LocaleServiceProviderPool.config(SPILocaleProviderAdapter.class, var5.toString());
                        return null;
                     }
                  }
               }

               return var1x;
            }
         });
      } catch (PrivilegedActionException var3) {
         LocaleServiceProviderPool.config(SPILocaleProviderAdapter.class, var3.toString());
         return null;
      }
   }

   private static <P extends LocaleServiceProvider> P getImpl(Map<Locale, P> var0, Locale var1) {
      Iterator var2 = LocaleServiceProviderPool.getLookupLocales(var1).iterator();

      LocaleServiceProvider var4;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         Locale var3 = (Locale)var2.next();
         var4 = (LocaleServiceProvider)var0.get(var3);
      } while(var4 == null);

      return var4;
   }

   static class TimeZoneNameProviderDelegate extends TimeZoneNameProvider implements SPILocaleProviderAdapter.Delegate<TimeZoneNameProvider> {
      private ConcurrentMap<Locale, TimeZoneNameProvider> map = new ConcurrentHashMap();

      public void addImpl(TimeZoneNameProvider var1) {
         Locale[] var2 = var1.getAvailableLocales();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Locale var5 = var2[var4];
            this.map.putIfAbsent(var5, var1);
         }

      }

      public TimeZoneNameProvider getImpl(Locale var1) {
         return (TimeZoneNameProvider)SPILocaleProviderAdapter.getImpl(this.map, var1);
      }

      public Locale[] getAvailableLocales() {
         return (Locale[])this.map.keySet().toArray(new Locale[0]);
      }

      public boolean isSupportedLocale(Locale var1) {
         return this.map.containsKey(var1);
      }

      public String getDisplayName(String var1, boolean var2, int var3, Locale var4) {
         TimeZoneNameProvider var5 = this.getImpl(var4);

         assert var5 != null;

         return var5.getDisplayName(var1, var2, var3, var4);
      }

      public String getGenericDisplayName(String var1, int var2, Locale var3) {
         TimeZoneNameProvider var4 = this.getImpl(var3);

         assert var4 != null;

         return var4.getGenericDisplayName(var1, var2, var3);
      }
   }

   static class LocaleNameProviderDelegate extends LocaleNameProvider implements SPILocaleProviderAdapter.Delegate<LocaleNameProvider> {
      private ConcurrentMap<Locale, LocaleNameProvider> map = new ConcurrentHashMap();

      public void addImpl(LocaleNameProvider var1) {
         Locale[] var2 = var1.getAvailableLocales();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Locale var5 = var2[var4];
            this.map.putIfAbsent(var5, var1);
         }

      }

      public LocaleNameProvider getImpl(Locale var1) {
         return (LocaleNameProvider)SPILocaleProviderAdapter.getImpl(this.map, var1);
      }

      public Locale[] getAvailableLocales() {
         return (Locale[])this.map.keySet().toArray(new Locale[0]);
      }

      public boolean isSupportedLocale(Locale var1) {
         return this.map.containsKey(var1);
      }

      public String getDisplayLanguage(String var1, Locale var2) {
         LocaleNameProvider var3 = this.getImpl(var2);

         assert var3 != null;

         return var3.getDisplayLanguage(var1, var2);
      }

      public String getDisplayScript(String var1, Locale var2) {
         LocaleNameProvider var3 = this.getImpl(var2);

         assert var3 != null;

         return var3.getDisplayScript(var1, var2);
      }

      public String getDisplayCountry(String var1, Locale var2) {
         LocaleNameProvider var3 = this.getImpl(var2);

         assert var3 != null;

         return var3.getDisplayCountry(var1, var2);
      }

      public String getDisplayVariant(String var1, Locale var2) {
         LocaleNameProvider var3 = this.getImpl(var2);

         assert var3 != null;

         return var3.getDisplayVariant(var1, var2);
      }
   }

   static class CurrencyNameProviderDelegate extends CurrencyNameProvider implements SPILocaleProviderAdapter.Delegate<CurrencyNameProvider> {
      private ConcurrentMap<Locale, CurrencyNameProvider> map = new ConcurrentHashMap();

      public void addImpl(CurrencyNameProvider var1) {
         Locale[] var2 = var1.getAvailableLocales();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Locale var5 = var2[var4];
            this.map.putIfAbsent(var5, var1);
         }

      }

      public CurrencyNameProvider getImpl(Locale var1) {
         return (CurrencyNameProvider)SPILocaleProviderAdapter.getImpl(this.map, var1);
      }

      public Locale[] getAvailableLocales() {
         return (Locale[])this.map.keySet().toArray(new Locale[0]);
      }

      public boolean isSupportedLocale(Locale var1) {
         return this.map.containsKey(var1);
      }

      public String getSymbol(String var1, Locale var2) {
         CurrencyNameProvider var3 = this.getImpl(var2);

         assert var3 != null;

         return var3.getSymbol(var1, var2);
      }

      public String getDisplayName(String var1, Locale var2) {
         CurrencyNameProvider var3 = this.getImpl(var2);

         assert var3 != null;

         return var3.getDisplayName(var1, var2);
      }
   }

   static class CalendarNameProviderDelegate extends CalendarNameProvider implements SPILocaleProviderAdapter.Delegate<CalendarNameProvider> {
      private ConcurrentMap<Locale, CalendarNameProvider> map = new ConcurrentHashMap();

      public void addImpl(CalendarNameProvider var1) {
         Locale[] var2 = var1.getAvailableLocales();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Locale var5 = var2[var4];
            this.map.putIfAbsent(var5, var1);
         }

      }

      public CalendarNameProvider getImpl(Locale var1) {
         return (CalendarNameProvider)SPILocaleProviderAdapter.getImpl(this.map, var1);
      }

      public Locale[] getAvailableLocales() {
         return (Locale[])this.map.keySet().toArray(new Locale[0]);
      }

      public boolean isSupportedLocale(Locale var1) {
         return this.map.containsKey(var1);
      }

      public String getDisplayName(String var1, int var2, int var3, int var4, Locale var5) {
         CalendarNameProvider var6 = this.getImpl(var5);

         assert var6 != null;

         return var6.getDisplayName(var1, var2, var3, var4, var5);
      }

      public Map<String, Integer> getDisplayNames(String var1, int var2, int var3, Locale var4) {
         CalendarNameProvider var5 = this.getImpl(var4);

         assert var5 != null;

         return var5.getDisplayNames(var1, var2, var3, var4);
      }
   }

   static class CalendarDataProviderDelegate extends CalendarDataProvider implements SPILocaleProviderAdapter.Delegate<CalendarDataProvider> {
      private ConcurrentMap<Locale, CalendarDataProvider> map = new ConcurrentHashMap();

      public void addImpl(CalendarDataProvider var1) {
         Locale[] var2 = var1.getAvailableLocales();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Locale var5 = var2[var4];
            this.map.putIfAbsent(var5, var1);
         }

      }

      public CalendarDataProvider getImpl(Locale var1) {
         return (CalendarDataProvider)SPILocaleProviderAdapter.getImpl(this.map, var1);
      }

      public Locale[] getAvailableLocales() {
         return (Locale[])this.map.keySet().toArray(new Locale[0]);
      }

      public boolean isSupportedLocale(Locale var1) {
         return this.map.containsKey(var1);
      }

      public int getFirstDayOfWeek(Locale var1) {
         CalendarDataProvider var2 = this.getImpl(var1);

         assert var2 != null;

         return var2.getFirstDayOfWeek(var1);
      }

      public int getMinimalDaysInFirstWeek(Locale var1) {
         CalendarDataProvider var2 = this.getImpl(var1);

         assert var2 != null;

         return var2.getMinimalDaysInFirstWeek(var1);
      }
   }

   static class NumberFormatProviderDelegate extends NumberFormatProvider implements SPILocaleProviderAdapter.Delegate<NumberFormatProvider> {
      private ConcurrentMap<Locale, NumberFormatProvider> map = new ConcurrentHashMap();

      public void addImpl(NumberFormatProvider var1) {
         Locale[] var2 = var1.getAvailableLocales();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Locale var5 = var2[var4];
            this.map.putIfAbsent(var5, var1);
         }

      }

      public NumberFormatProvider getImpl(Locale var1) {
         return (NumberFormatProvider)SPILocaleProviderAdapter.getImpl(this.map, var1);
      }

      public Locale[] getAvailableLocales() {
         return (Locale[])this.map.keySet().toArray(new Locale[0]);
      }

      public boolean isSupportedLocale(Locale var1) {
         return this.map.containsKey(var1);
      }

      public NumberFormat getCurrencyInstance(Locale var1) {
         NumberFormatProvider var2 = this.getImpl(var1);

         assert var2 != null;

         return var2.getCurrencyInstance(var1);
      }

      public NumberFormat getIntegerInstance(Locale var1) {
         NumberFormatProvider var2 = this.getImpl(var1);

         assert var2 != null;

         return var2.getIntegerInstance(var1);
      }

      public NumberFormat getNumberInstance(Locale var1) {
         NumberFormatProvider var2 = this.getImpl(var1);

         assert var2 != null;

         return var2.getNumberInstance(var1);
      }

      public NumberFormat getPercentInstance(Locale var1) {
         NumberFormatProvider var2 = this.getImpl(var1);

         assert var2 != null;

         return var2.getPercentInstance(var1);
      }
   }

   static class DecimalFormatSymbolsProviderDelegate extends DecimalFormatSymbolsProvider implements SPILocaleProviderAdapter.Delegate<DecimalFormatSymbolsProvider> {
      private ConcurrentMap<Locale, DecimalFormatSymbolsProvider> map = new ConcurrentHashMap();

      public void addImpl(DecimalFormatSymbolsProvider var1) {
         Locale[] var2 = var1.getAvailableLocales();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Locale var5 = var2[var4];
            this.map.putIfAbsent(var5, var1);
         }

      }

      public DecimalFormatSymbolsProvider getImpl(Locale var1) {
         return (DecimalFormatSymbolsProvider)SPILocaleProviderAdapter.getImpl(this.map, var1);
      }

      public Locale[] getAvailableLocales() {
         return (Locale[])this.map.keySet().toArray(new Locale[0]);
      }

      public boolean isSupportedLocale(Locale var1) {
         return this.map.containsKey(var1);
      }

      public DecimalFormatSymbols getInstance(Locale var1) {
         DecimalFormatSymbolsProvider var2 = this.getImpl(var1);

         assert var2 != null;

         return var2.getInstance(var1);
      }
   }

   static class DateFormatSymbolsProviderDelegate extends DateFormatSymbolsProvider implements SPILocaleProviderAdapter.Delegate<DateFormatSymbolsProvider> {
      private ConcurrentMap<Locale, DateFormatSymbolsProvider> map = new ConcurrentHashMap();

      public void addImpl(DateFormatSymbolsProvider var1) {
         Locale[] var2 = var1.getAvailableLocales();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Locale var5 = var2[var4];
            this.map.putIfAbsent(var5, var1);
         }

      }

      public DateFormatSymbolsProvider getImpl(Locale var1) {
         return (DateFormatSymbolsProvider)SPILocaleProviderAdapter.getImpl(this.map, var1);
      }

      public Locale[] getAvailableLocales() {
         return (Locale[])this.map.keySet().toArray(new Locale[0]);
      }

      public boolean isSupportedLocale(Locale var1) {
         return this.map.containsKey(var1);
      }

      public DateFormatSymbols getInstance(Locale var1) {
         DateFormatSymbolsProvider var2 = this.getImpl(var1);

         assert var2 != null;

         return var2.getInstance(var1);
      }
   }

   static class DateFormatProviderDelegate extends DateFormatProvider implements SPILocaleProviderAdapter.Delegate<DateFormatProvider> {
      private ConcurrentMap<Locale, DateFormatProvider> map = new ConcurrentHashMap();

      public void addImpl(DateFormatProvider var1) {
         Locale[] var2 = var1.getAvailableLocales();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Locale var5 = var2[var4];
            this.map.putIfAbsent(var5, var1);
         }

      }

      public DateFormatProvider getImpl(Locale var1) {
         return (DateFormatProvider)SPILocaleProviderAdapter.getImpl(this.map, var1);
      }

      public Locale[] getAvailableLocales() {
         return (Locale[])this.map.keySet().toArray(new Locale[0]);
      }

      public boolean isSupportedLocale(Locale var1) {
         return this.map.containsKey(var1);
      }

      public DateFormat getTimeInstance(int var1, Locale var2) {
         DateFormatProvider var3 = this.getImpl(var2);

         assert var3 != null;

         return var3.getTimeInstance(var1, var2);
      }

      public DateFormat getDateInstance(int var1, Locale var2) {
         DateFormatProvider var3 = this.getImpl(var2);

         assert var3 != null;

         return var3.getDateInstance(var1, var2);
      }

      public DateFormat getDateTimeInstance(int var1, int var2, Locale var3) {
         DateFormatProvider var4 = this.getImpl(var3);

         assert var4 != null;

         return var4.getDateTimeInstance(var1, var2, var3);
      }
   }

   static class CollatorProviderDelegate extends CollatorProvider implements SPILocaleProviderAdapter.Delegate<CollatorProvider> {
      private ConcurrentMap<Locale, CollatorProvider> map = new ConcurrentHashMap();

      public void addImpl(CollatorProvider var1) {
         Locale[] var2 = var1.getAvailableLocales();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Locale var5 = var2[var4];
            this.map.putIfAbsent(var5, var1);
         }

      }

      public CollatorProvider getImpl(Locale var1) {
         return (CollatorProvider)SPILocaleProviderAdapter.getImpl(this.map, var1);
      }

      public Locale[] getAvailableLocales() {
         return (Locale[])this.map.keySet().toArray(new Locale[0]);
      }

      public boolean isSupportedLocale(Locale var1) {
         return this.map.containsKey(var1);
      }

      public Collator getInstance(Locale var1) {
         CollatorProvider var2 = this.getImpl(var1);

         assert var2 != null;

         return var2.getInstance(var1);
      }
   }

   static class BreakIteratorProviderDelegate extends BreakIteratorProvider implements SPILocaleProviderAdapter.Delegate<BreakIteratorProvider> {
      private ConcurrentMap<Locale, BreakIteratorProvider> map = new ConcurrentHashMap();

      public void addImpl(BreakIteratorProvider var1) {
         Locale[] var2 = var1.getAvailableLocales();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Locale var5 = var2[var4];
            this.map.putIfAbsent(var5, var1);
         }

      }

      public BreakIteratorProvider getImpl(Locale var1) {
         return (BreakIteratorProvider)SPILocaleProviderAdapter.getImpl(this.map, var1);
      }

      public Locale[] getAvailableLocales() {
         return (Locale[])this.map.keySet().toArray(new Locale[0]);
      }

      public boolean isSupportedLocale(Locale var1) {
         return this.map.containsKey(var1);
      }

      public BreakIterator getWordInstance(Locale var1) {
         BreakIteratorProvider var2 = this.getImpl(var1);

         assert var2 != null;

         return var2.getWordInstance(var1);
      }

      public BreakIterator getLineInstance(Locale var1) {
         BreakIteratorProvider var2 = this.getImpl(var1);

         assert var2 != null;

         return var2.getLineInstance(var1);
      }

      public BreakIterator getCharacterInstance(Locale var1) {
         BreakIteratorProvider var2 = this.getImpl(var1);

         assert var2 != null;

         return var2.getCharacterInstance(var1);
      }

      public BreakIterator getSentenceInstance(Locale var1) {
         BreakIteratorProvider var2 = this.getImpl(var1);

         assert var2 != null;

         return var2.getSentenceInstance(var1);
      }
   }

   interface Delegate<P extends LocaleServiceProvider> {
      void addImpl(P var1);

      P getImpl(Locale var1);
   }
}
