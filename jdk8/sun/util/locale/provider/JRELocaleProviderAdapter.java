package sun.util.locale.provider;

import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.spi.BreakIteratorProvider;
import java.text.spi.CollatorProvider;
import java.text.spi.DateFormatProvider;
import java.text.spi.DateFormatSymbolsProvider;
import java.text.spi.DecimalFormatSymbolsProvider;
import java.text.spi.NumberFormatProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.spi.CalendarDataProvider;
import java.util.spi.CalendarNameProvider;
import java.util.spi.CurrencyNameProvider;
import java.util.spi.LocaleNameProvider;
import java.util.spi.LocaleServiceProvider;
import java.util.spi.TimeZoneNameProvider;
import sun.security.action.GetPropertyAction;
import sun.util.resources.LocaleData;
import sun.util.spi.CalendarProvider;

public class JRELocaleProviderAdapter extends LocaleProviderAdapter implements ResourceBundleBasedAdapter {
   private static final String LOCALE_DATA_JAR_NAME = "localedata.jar";
   private final ConcurrentMap<String, Set<String>> langtagSets = new ConcurrentHashMap();
   private final ConcurrentMap<Locale, LocaleResources> localeResourcesMap = new ConcurrentHashMap();
   private volatile LocaleData localeData;
   private volatile BreakIteratorProvider breakIteratorProvider = null;
   private volatile CollatorProvider collatorProvider = null;
   private volatile DateFormatProvider dateFormatProvider = null;
   private volatile DateFormatSymbolsProvider dateFormatSymbolsProvider = null;
   private volatile DecimalFormatSymbolsProvider decimalFormatSymbolsProvider = null;
   private volatile NumberFormatProvider numberFormatProvider = null;
   private volatile CurrencyNameProvider currencyNameProvider = null;
   private volatile LocaleNameProvider localeNameProvider = null;
   private volatile TimeZoneNameProvider timeZoneNameProvider = null;
   private volatile CalendarDataProvider calendarDataProvider = null;
   private volatile CalendarNameProvider calendarNameProvider = null;
   private volatile CalendarProvider calendarProvider = null;
   private static volatile Boolean isNonENSupported = null;

   public LocaleProviderAdapter.Type getAdapterType() {
      return LocaleProviderAdapter.Type.JRE;
   }

   public <P extends LocaleServiceProvider> P getLocaleServiceProvider(Class<P> var1) {
      String var2 = var1.getSimpleName();
      byte var3 = -1;
      switch(var2.hashCode()) {
      case -1614968633:
         if (var2.equals("DateFormatSymbolsProvider")) {
            var3 = 3;
         }
         break;
      case -1353096495:
         if (var2.equals("NumberFormatProvider")) {
            var3 = 5;
         }
         break;
      case -798486419:
         if (var2.equals("CurrencyNameProvider")) {
            var3 = 6;
         }
         break;
      case -125048202:
         if (var2.equals("DateFormatProvider")) {
            var3 = 2;
         }
         break;
      case 38915396:
         if (var2.equals("DecimalFormatSymbolsProvider")) {
            var3 = 4;
         }
         break;
      case 90734195:
         if (var2.equals("CollatorProvider")) {
            var3 = 1;
         }
         break;
      case 231772470:
         if (var2.equals("LocaleNameProvider")) {
            var3 = 7;
         }
         break;
      case 759873562:
         if (var2.equals("CalendarNameProvider")) {
            var3 = 10;
         }
         break;
      case 907948926:
         if (var2.equals("BreakIteratorProvider")) {
            var3 = 0;
         }
         break;
      case 1028302393:
         if (var2.equals("CalendarDataProvider")) {
            var3 = 9;
         }
         break;
      case 1334414191:
         if (var2.equals("CalendarProvider")) {
            var3 = 11;
         }
         break;
      case 1502810229:
         if (var2.equals("TimeZoneNameProvider")) {
            var3 = 8;
         }
      }

      switch(var3) {
      case 0:
         return this.getBreakIteratorProvider();
      case 1:
         return this.getCollatorProvider();
      case 2:
         return this.getDateFormatProvider();
      case 3:
         return this.getDateFormatSymbolsProvider();
      case 4:
         return this.getDecimalFormatSymbolsProvider();
      case 5:
         return this.getNumberFormatProvider();
      case 6:
         return this.getCurrencyNameProvider();
      case 7:
         return this.getLocaleNameProvider();
      case 8:
         return this.getTimeZoneNameProvider();
      case 9:
         return this.getCalendarDataProvider();
      case 10:
         return this.getCalendarNameProvider();
      case 11:
         return this.getCalendarProvider();
      default:
         throw new InternalError("should not come down here");
      }
   }

   public BreakIteratorProvider getBreakIteratorProvider() {
      if (this.breakIteratorProvider == null) {
         BreakIteratorProviderImpl var1 = new BreakIteratorProviderImpl(this.getAdapterType(), this.getLanguageTagSet("FormatData"));
         synchronized(this) {
            if (this.breakIteratorProvider == null) {
               this.breakIteratorProvider = var1;
            }
         }
      }

      return this.breakIteratorProvider;
   }

   public CollatorProvider getCollatorProvider() {
      if (this.collatorProvider == null) {
         CollatorProviderImpl var1 = new CollatorProviderImpl(this.getAdapterType(), this.getLanguageTagSet("CollationData"));
         synchronized(this) {
            if (this.collatorProvider == null) {
               this.collatorProvider = var1;
            }
         }
      }

      return this.collatorProvider;
   }

   public DateFormatProvider getDateFormatProvider() {
      if (this.dateFormatProvider == null) {
         DateFormatProviderImpl var1 = new DateFormatProviderImpl(this.getAdapterType(), this.getLanguageTagSet("FormatData"));
         synchronized(this) {
            if (this.dateFormatProvider == null) {
               this.dateFormatProvider = var1;
            }
         }
      }

      return this.dateFormatProvider;
   }

   public DateFormatSymbolsProvider getDateFormatSymbolsProvider() {
      if (this.dateFormatSymbolsProvider == null) {
         DateFormatSymbolsProviderImpl var1 = new DateFormatSymbolsProviderImpl(this.getAdapterType(), this.getLanguageTagSet("FormatData"));
         synchronized(this) {
            if (this.dateFormatSymbolsProvider == null) {
               this.dateFormatSymbolsProvider = var1;
            }
         }
      }

      return this.dateFormatSymbolsProvider;
   }

   public DecimalFormatSymbolsProvider getDecimalFormatSymbolsProvider() {
      if (this.decimalFormatSymbolsProvider == null) {
         DecimalFormatSymbolsProviderImpl var1 = new DecimalFormatSymbolsProviderImpl(this.getAdapterType(), this.getLanguageTagSet("FormatData"));
         synchronized(this) {
            if (this.decimalFormatSymbolsProvider == null) {
               this.decimalFormatSymbolsProvider = var1;
            }
         }
      }

      return this.decimalFormatSymbolsProvider;
   }

   public NumberFormatProvider getNumberFormatProvider() {
      if (this.numberFormatProvider == null) {
         NumberFormatProviderImpl var1 = new NumberFormatProviderImpl(this.getAdapterType(), this.getLanguageTagSet("FormatData"));
         synchronized(this) {
            if (this.numberFormatProvider == null) {
               this.numberFormatProvider = var1;
            }
         }
      }

      return this.numberFormatProvider;
   }

   public CurrencyNameProvider getCurrencyNameProvider() {
      if (this.currencyNameProvider == null) {
         CurrencyNameProviderImpl var1 = new CurrencyNameProviderImpl(this.getAdapterType(), this.getLanguageTagSet("CurrencyNames"));
         synchronized(this) {
            if (this.currencyNameProvider == null) {
               this.currencyNameProvider = var1;
            }
         }
      }

      return this.currencyNameProvider;
   }

   public LocaleNameProvider getLocaleNameProvider() {
      if (this.localeNameProvider == null) {
         LocaleNameProviderImpl var1 = new LocaleNameProviderImpl(this.getAdapterType(), this.getLanguageTagSet("LocaleNames"));
         synchronized(this) {
            if (this.localeNameProvider == null) {
               this.localeNameProvider = var1;
            }
         }
      }

      return this.localeNameProvider;
   }

   public TimeZoneNameProvider getTimeZoneNameProvider() {
      if (this.timeZoneNameProvider == null) {
         TimeZoneNameProviderImpl var1 = new TimeZoneNameProviderImpl(this.getAdapterType(), this.getLanguageTagSet("TimeZoneNames"));
         synchronized(this) {
            if (this.timeZoneNameProvider == null) {
               this.timeZoneNameProvider = var1;
            }
         }
      }

      return this.timeZoneNameProvider;
   }

   public CalendarDataProvider getCalendarDataProvider() {
      if (this.calendarDataProvider == null) {
         CalendarDataProviderImpl var1 = new CalendarDataProviderImpl(this.getAdapterType(), this.getLanguageTagSet("CalendarData"));
         synchronized(this) {
            if (this.calendarDataProvider == null) {
               this.calendarDataProvider = var1;
            }
         }
      }

      return this.calendarDataProvider;
   }

   public CalendarNameProvider getCalendarNameProvider() {
      if (this.calendarNameProvider == null) {
         CalendarNameProviderImpl var1 = new CalendarNameProviderImpl(this.getAdapterType(), this.getLanguageTagSet("FormatData"));
         synchronized(this) {
            if (this.calendarNameProvider == null) {
               this.calendarNameProvider = var1;
            }
         }
      }

      return this.calendarNameProvider;
   }

   public CalendarProvider getCalendarProvider() {
      if (this.calendarProvider == null) {
         CalendarProviderImpl var1 = new CalendarProviderImpl(this.getAdapterType(), this.getLanguageTagSet("CalendarData"));
         synchronized(this) {
            if (this.calendarProvider == null) {
               this.calendarProvider = var1;
            }
         }
      }

      return this.calendarProvider;
   }

   public LocaleResources getLocaleResources(Locale var1) {
      LocaleResources var2 = (LocaleResources)this.localeResourcesMap.get(var1);
      if (var2 == null) {
         var2 = new LocaleResources(this, var1);
         LocaleResources var3 = (LocaleResources)this.localeResourcesMap.putIfAbsent(var1, var2);
         if (var3 != null) {
            var2 = var3;
         }
      }

      return var2;
   }

   public LocaleData getLocaleData() {
      if (this.localeData == null) {
         synchronized(this) {
            if (this.localeData == null) {
               this.localeData = new LocaleData(this.getAdapterType());
            }
         }
      }

      return this.localeData;
   }

   public Locale[] getAvailableLocales() {
      return (Locale[])JRELocaleProviderAdapter.AvailableJRELocales.localeList.clone();
   }

   public Set<String> getLanguageTagSet(String var1) {
      Set var2 = (Set)this.langtagSets.get(var1);
      if (var2 == null) {
         var2 = this.createLanguageTagSet(var1);
         Set var3 = (Set)this.langtagSets.putIfAbsent(var1, var2);
         if (var3 != null) {
            var2 = var3;
         }
      }

      return var2;
   }

   protected Set<String> createLanguageTagSet(String var1) {
      String var2 = LocaleDataMetaInfo.getSupportedLocaleString(var1);
      if (var2 == null) {
         return Collections.emptySet();
      } else {
         HashSet var3 = new HashSet();
         StringTokenizer var4 = new StringTokenizer(var2);

         while(var4.hasMoreTokens()) {
            String var5 = var4.nextToken();
            if (var5.equals("|")) {
               if (isNonENLangSupported()) {
                  continue;
               }
               break;
            } else {
               var3.add(var5);
            }
         }

         return var3;
      }
   }

   private static Locale[] createAvailableLocales() {
      String var0 = LocaleDataMetaInfo.getSupportedLocaleString("AvailableLocales");
      if (var0.length() == 0) {
         throw new InternalError("No available locales for JRE");
      } else {
         int var1 = var0.indexOf(124);
         StringTokenizer var2;
         if (isNonENLangSupported()) {
            var2 = new StringTokenizer(var0.substring(0, var1) + var0.substring(var1 + 1));
         } else {
            var2 = new StringTokenizer(var0.substring(0, var1));
         }

         int var3 = var2.countTokens();
         Locale[] var4 = new Locale[var3 + 1];
         var4[0] = Locale.ROOT;

         for(int var5 = 1; var5 <= var3; ++var5) {
            String var6 = var2.nextToken();
            byte var8 = -1;
            switch(var6.hashCode()) {
            case -518283308:
               if (var6.equals("th-TH-TH")) {
                  var8 = 2;
               }
               break;
            case -472985013:
               if (var6.equals("no-NO-NY")) {
                  var8 = 1;
               }
               break;
            case 1601894167:
               if (var6.equals("ja-JP-JP")) {
                  var8 = 0;
               }
            }

            switch(var8) {
            case 0:
               var4[var5] = JRELocaleConstants.JA_JP_JP;
               break;
            case 1:
               var4[var5] = JRELocaleConstants.NO_NO_NY;
               break;
            case 2:
               var4[var5] = JRELocaleConstants.TH_TH_TH;
               break;
            default:
               var4[var5] = Locale.forLanguageTag(var6);
            }
         }

         return var4;
      }
   }

   private static boolean isNonENLangSupported() {
      if (isNonENSupported == null) {
         Class var0 = JRELocaleProviderAdapter.class;
         synchronized(JRELocaleProviderAdapter.class) {
            if (isNonENSupported == null) {
               String var1 = File.separator;
               String var2 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.home"))) + var1 + "lib" + var1 + "ext" + var1 + "localedata.jar";
               final File var3 = new File(var2);
               isNonENSupported = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
                  public Boolean run() {
                     return var3.exists();
                  }
               });
            }
         }
      }

      return isNonENSupported;
   }

   private static class AvailableJRELocales {
      private static final Locale[] localeList = JRELocaleProviderAdapter.createAvailableLocales();
   }
}
