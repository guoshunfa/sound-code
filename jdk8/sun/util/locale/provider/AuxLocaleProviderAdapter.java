package sun.util.locale.provider;

import java.text.spi.BreakIteratorProvider;
import java.text.spi.CollatorProvider;
import java.text.spi.DateFormatProvider;
import java.text.spi.DateFormatSymbolsProvider;
import java.text.spi.DecimalFormatSymbolsProvider;
import java.text.spi.NumberFormatProvider;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.spi.CalendarDataProvider;
import java.util.spi.CalendarNameProvider;
import java.util.spi.CurrencyNameProvider;
import java.util.spi.LocaleNameProvider;
import java.util.spi.LocaleServiceProvider;
import java.util.spi.TimeZoneNameProvider;
import sun.util.spi.CalendarProvider;

public abstract class AuxLocaleProviderAdapter extends LocaleProviderAdapter {
   private ConcurrentMap<Class<? extends LocaleServiceProvider>, LocaleServiceProvider> providersMap = new ConcurrentHashMap();
   private static Locale[] availableLocales = null;
   private static AuxLocaleProviderAdapter.NullProvider NULL_PROVIDER = new AuxLocaleProviderAdapter.NullProvider();

   public <P extends LocaleServiceProvider> P getLocaleServiceProvider(Class<P> var1) {
      LocaleServiceProvider var2 = (LocaleServiceProvider)this.providersMap.get(var1);
      if (var2 == null) {
         var2 = this.findInstalledProvider(var1);
         this.providersMap.putIfAbsent(var1, var2 == null ? NULL_PROVIDER : var2);
      }

      return var2;
   }

   protected abstract <P extends LocaleServiceProvider> P findInstalledProvider(Class<P> var1);

   public BreakIteratorProvider getBreakIteratorProvider() {
      return (BreakIteratorProvider)this.getLocaleServiceProvider(BreakIteratorProvider.class);
   }

   public CollatorProvider getCollatorProvider() {
      return (CollatorProvider)this.getLocaleServiceProvider(CollatorProvider.class);
   }

   public DateFormatProvider getDateFormatProvider() {
      return (DateFormatProvider)this.getLocaleServiceProvider(DateFormatProvider.class);
   }

   public DateFormatSymbolsProvider getDateFormatSymbolsProvider() {
      return (DateFormatSymbolsProvider)this.getLocaleServiceProvider(DateFormatSymbolsProvider.class);
   }

   public DecimalFormatSymbolsProvider getDecimalFormatSymbolsProvider() {
      return (DecimalFormatSymbolsProvider)this.getLocaleServiceProvider(DecimalFormatSymbolsProvider.class);
   }

   public NumberFormatProvider getNumberFormatProvider() {
      return (NumberFormatProvider)this.getLocaleServiceProvider(NumberFormatProvider.class);
   }

   public CurrencyNameProvider getCurrencyNameProvider() {
      return (CurrencyNameProvider)this.getLocaleServiceProvider(CurrencyNameProvider.class);
   }

   public LocaleNameProvider getLocaleNameProvider() {
      return (LocaleNameProvider)this.getLocaleServiceProvider(LocaleNameProvider.class);
   }

   public TimeZoneNameProvider getTimeZoneNameProvider() {
      return (TimeZoneNameProvider)this.getLocaleServiceProvider(TimeZoneNameProvider.class);
   }

   public CalendarDataProvider getCalendarDataProvider() {
      return (CalendarDataProvider)this.getLocaleServiceProvider(CalendarDataProvider.class);
   }

   public CalendarNameProvider getCalendarNameProvider() {
      return (CalendarNameProvider)this.getLocaleServiceProvider(CalendarNameProvider.class);
   }

   public CalendarProvider getCalendarProvider() {
      return (CalendarProvider)this.getLocaleServiceProvider(CalendarProvider.class);
   }

   public LocaleResources getLocaleResources(Locale var1) {
      return null;
   }

   public Locale[] getAvailableLocales() {
      if (availableLocales == null) {
         HashSet var1 = new HashSet();
         Class[] var2 = LocaleServiceProviderPool.spiClasses;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Class var5 = var2[var4];
            LocaleServiceProvider var6 = this.getLocaleServiceProvider(var5);
            if (var6 != null) {
               var1.addAll(Arrays.asList(var6.getAvailableLocales()));
            }
         }

         availableLocales = (Locale[])var1.toArray(new Locale[0]);
      }

      return availableLocales;
   }

   private static class NullProvider extends LocaleServiceProvider {
      private NullProvider() {
      }

      public Locale[] getAvailableLocales() {
         return new Locale[0];
      }

      // $FF: synthetic method
      NullProvider(Object var1) {
         this();
      }
   }
}
