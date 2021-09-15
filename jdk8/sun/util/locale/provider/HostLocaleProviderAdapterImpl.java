package sun.util.locale.provider;

import java.lang.ref.SoftReference;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.text.spi.DateFormatProvider;
import java.text.spi.DateFormatSymbolsProvider;
import java.text.spi.DecimalFormatSymbolsProvider;
import java.text.spi.NumberFormatProvider;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.spi.CalendarDataProvider;
import java.util.spi.CalendarNameProvider;
import java.util.spi.CurrencyNameProvider;
import java.util.spi.LocaleNameProvider;
import java.util.spi.TimeZoneNameProvider;
import sun.util.spi.CalendarProvider;

public class HostLocaleProviderAdapterImpl {
   private static ConcurrentMap<Locale, SoftReference<AtomicReferenceArray<String>>> dateFormatPatternsMap = new ConcurrentHashMap(2);
   private static ConcurrentMap<Locale, SoftReference<AtomicReferenceArray<String>>> numberFormatPatternsMap = new ConcurrentHashMap(2);
   private static ConcurrentMap<Locale, SoftReference<DateFormatSymbols>> dateFormatSymbolsMap = new ConcurrentHashMap(2);
   private static ConcurrentMap<Locale, SoftReference<DecimalFormatSymbols>> decimalFormatSymbolsMap = new ConcurrentHashMap(2);
   private static final int CAT_DISPLAY = 0;
   private static final int CAT_FORMAT = 1;
   private static final int NF_NUMBER = 0;
   private static final int NF_CURRENCY = 1;
   private static final int NF_PERCENT = 2;
   private static final int NF_INTEGER = 3;
   private static final int NF_MAX = 3;
   private static final int CD_FIRSTDAYOFWEEK = 0;
   private static final int CD_MINIMALDAYSINFIRSTWEEK = 1;
   private static final int DN_LOCALE_LANGUAGE = 0;
   private static final int DN_LOCALE_SCRIPT = 1;
   private static final int DN_LOCALE_REGION = 2;
   private static final int DN_LOCALE_VARIANT = 3;
   private static final int DN_CURRENCY_CODE = 4;
   private static final int DN_CURRENCY_SYMBOL = 5;
   private static final int DN_TZ_SHORT_STANDARD = 0;
   private static final int DN_TZ_SHORT_DST = 1;
   private static final int DN_TZ_LONG_STANDARD = 2;
   private static final int DN_TZ_LONG_DST = 3;
   private static final Set<Locale> supportedLocaleSet;
   private static final Locale[] supportedLocale;

   private static Locale convertMacOSXLocaleToJavaLocale(String var0) {
      String[] var1 = var0.split("@");
      String var2 = var1[0].replace('_', '-');
      if (var1.length > 1) {
         String[] var3 = var1[1].split(";");
         String[] var4 = var3;
         int var5 = var3.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String var7 = var4[var6];
            if (var7.startsWith("calendar=")) {
               String var8 = var7.substring(var7.indexOf(61) + 1);
               byte var10 = -1;
               switch(var8.hashCode()) {
               case -752730191:
                  if (var8.equals("japanese")) {
                     var10 = 1;
                  }
                  break;
               case 2126038758:
                  if (var8.equals("gregorian")) {
                     var10 = 0;
                  }
               }

               switch(var10) {
               case 0:
                  var2 = var2 + "-u-ca-gregory";
                  break;
               case 1:
                  if (var1[0].equals("ja_JP")) {
                     return JRELocaleConstants.JA_JP_JP;
                  }
               default:
                  var2 = var2 + "-u-ca-" + var8;
               }
            }
         }
      }

      return Locale.forLanguageTag(var2);
   }

   public static DateFormatProvider getDateFormatProvider() {
      return new DateFormatProvider() {
         public Locale[] getAvailableLocales() {
            return HostLocaleProviderAdapterImpl.getSupportedCalendarLocales();
         }

         public boolean isSupportedLocale(Locale var1) {
            return HostLocaleProviderAdapterImpl.isSupportedCalendarLocale(var1);
         }

         public DateFormat getDateInstance(int var1, Locale var2) {
            return new SimpleDateFormat(this.getDateTimePattern(var1, -1, var2), HostLocaleProviderAdapterImpl.getCalendarLocale(var2));
         }

         public DateFormat getTimeInstance(int var1, Locale var2) {
            return new SimpleDateFormat(this.getDateTimePattern(-1, var1, var2), HostLocaleProviderAdapterImpl.getCalendarLocale(var2));
         }

         public DateFormat getDateTimeInstance(int var1, int var2, Locale var3) {
            return new SimpleDateFormat(this.getDateTimePattern(var1, var2, var3), HostLocaleProviderAdapterImpl.getCalendarLocale(var3));
         }

         private String getDateTimePattern(int var1, int var2, Locale var3) {
            SoftReference var5 = (SoftReference)HostLocaleProviderAdapterImpl.dateFormatPatternsMap.get(var3);
            AtomicReferenceArray var4;
            if (var5 == null || (var4 = (AtomicReferenceArray)var5.get()) == null) {
               var4 = new AtomicReferenceArray(25);
               var5 = new SoftReference(var4);
               HostLocaleProviderAdapterImpl.dateFormatPatternsMap.put(var3, var5);
            }

            int var6 = (var1 + 1) * 5 + var2 + 1;
            String var7 = (String)var4.get(var6);
            if (var7 == null) {
               String var8 = var3.toLanguageTag();
               var7 = HostLocaleProviderAdapterImpl.translateDateFormatLetters(HostLocaleProviderAdapterImpl.getCalendarID(var8), HostLocaleProviderAdapterImpl.getDateTimePatternNative(var1, var2, var8));
               if (!var4.compareAndSet(var6, (Object)null, var7)) {
                  var7 = (String)var4.get(var6);
               }
            }

            return var7;
         }
      };
   }

   public static DateFormatSymbolsProvider getDateFormatSymbolsProvider() {
      return new DateFormatSymbolsProvider() {
         public Locale[] getAvailableLocales() {
            return this.isSupportedLocale(Locale.getDefault(Locale.Category.FORMAT)) ? HostLocaleProviderAdapterImpl.supportedLocale : new Locale[0];
         }

         public boolean isSupportedLocale(Locale var1) {
            Locale var2 = var1.stripExtensions();
            return HostLocaleProviderAdapterImpl.supportedLocaleSet.contains(var2) ? HostLocaleProviderAdapterImpl.getCalendarID(var1.toLanguageTag()).equals("gregorian") : false;
         }

         public DateFormatSymbols getInstance(Locale var1) {
            SoftReference var3 = (SoftReference)HostLocaleProviderAdapterImpl.dateFormatSymbolsMap.get(var1);
            DateFormatSymbols var2;
            if (var3 == null || (var2 = (DateFormatSymbols)var3.get()) == null) {
               var2 = new DateFormatSymbols(var1);
               String var4 = var1.toLanguageTag();
               var2.setAmPmStrings(HostLocaleProviderAdapterImpl.getAmPmStrings(var4, var2.getAmPmStrings()));
               var2.setEras(HostLocaleProviderAdapterImpl.getEras(var4, var2.getEras()));
               var2.setMonths(HostLocaleProviderAdapterImpl.getMonths(var4, var2.getMonths()));
               var2.setShortMonths(HostLocaleProviderAdapterImpl.getShortMonths(var4, var2.getShortMonths()));
               var2.setWeekdays(HostLocaleProviderAdapterImpl.getWeekdays(var4, var2.getWeekdays()));
               var2.setShortWeekdays(HostLocaleProviderAdapterImpl.getShortWeekdays(var4, var2.getShortWeekdays()));
               var3 = new SoftReference(var2);
               HostLocaleProviderAdapterImpl.dateFormatSymbolsMap.put(var1, var3);
            }

            return (DateFormatSymbols)var2.clone();
         }
      };
   }

   public static NumberFormatProvider getNumberFormatProvider() {
      return new NumberFormatProvider() {
         public Locale[] getAvailableLocales() {
            return HostLocaleProviderAdapterImpl.supportedLocale;
         }

         public boolean isSupportedLocale(Locale var1) {
            return HostLocaleProviderAdapterImpl.supportedLocaleSet.contains(var1.stripExtensions());
         }

         public NumberFormat getCurrencyInstance(Locale var1) {
            return new DecimalFormat(this.getNumberPattern(1, var1), DecimalFormatSymbols.getInstance(var1));
         }

         public NumberFormat getIntegerInstance(Locale var1) {
            return new DecimalFormat(this.getNumberPattern(3, var1), DecimalFormatSymbols.getInstance(var1));
         }

         public NumberFormat getNumberInstance(Locale var1) {
            return new DecimalFormat(this.getNumberPattern(0, var1), DecimalFormatSymbols.getInstance(var1));
         }

         public NumberFormat getPercentInstance(Locale var1) {
            return new DecimalFormat(this.getNumberPattern(2, var1), DecimalFormatSymbols.getInstance(var1));
         }

         private String getNumberPattern(int var1, Locale var2) {
            SoftReference var4 = (SoftReference)HostLocaleProviderAdapterImpl.numberFormatPatternsMap.get(var2);
            AtomicReferenceArray var3;
            if (var4 == null || (var3 = (AtomicReferenceArray)var4.get()) == null) {
               var3 = new AtomicReferenceArray(4);
               var4 = new SoftReference(var3);
               HostLocaleProviderAdapterImpl.numberFormatPatternsMap.put(var2, var4);
            }

            String var5 = (String)var3.get(var1);
            if (var5 == null) {
               var5 = HostLocaleProviderAdapterImpl.getNumberPatternNative(var1, var2.toLanguageTag());
               if (!var3.compareAndSet(var1, (Object)null, var5)) {
                  var5 = (String)var3.get(var1);
               }
            }

            return var5;
         }
      };
   }

   public static DecimalFormatSymbolsProvider getDecimalFormatSymbolsProvider() {
      return new DecimalFormatSymbolsProvider() {
         public Locale[] getAvailableLocales() {
            return HostLocaleProviderAdapterImpl.supportedLocale;
         }

         public boolean isSupportedLocale(Locale var1) {
            return HostLocaleProviderAdapterImpl.supportedLocaleSet.contains(var1.stripExtensions());
         }

         public DecimalFormatSymbols getInstance(Locale var1) {
            SoftReference var3 = (SoftReference)HostLocaleProviderAdapterImpl.decimalFormatSymbolsMap.get(var1);
            DecimalFormatSymbols var2;
            if (var3 == null || (var2 = (DecimalFormatSymbols)var3.get()) == null) {
               var2 = new DecimalFormatSymbols(var1);
               String var4 = var1.toLanguageTag();
               var2.setInternationalCurrencySymbol(HostLocaleProviderAdapterImpl.getInternationalCurrencySymbol(var4, var2.getInternationalCurrencySymbol()));
               var2.setCurrencySymbol(HostLocaleProviderAdapterImpl.getCurrencySymbol(var4, var2.getCurrencySymbol()));
               var2.setDecimalSeparator(HostLocaleProviderAdapterImpl.getDecimalSeparator(var4, var2.getDecimalSeparator()));
               var2.setGroupingSeparator(HostLocaleProviderAdapterImpl.getGroupingSeparator(var4, var2.getGroupingSeparator()));
               var2.setInfinity(HostLocaleProviderAdapterImpl.getInfinity(var4, var2.getInfinity()));
               var2.setMinusSign(HostLocaleProviderAdapterImpl.getMinusSign(var4, var2.getMinusSign()));
               var2.setMonetaryDecimalSeparator(HostLocaleProviderAdapterImpl.getMonetaryDecimalSeparator(var4, var2.getMonetaryDecimalSeparator()));
               var2.setNaN(HostLocaleProviderAdapterImpl.getNaN(var4, var2.getNaN()));
               var2.setPercent(HostLocaleProviderAdapterImpl.getPercent(var4, var2.getPercent()));
               var2.setPerMill(HostLocaleProviderAdapterImpl.getPerMill(var4, var2.getPerMill()));
               var2.setZeroDigit(HostLocaleProviderAdapterImpl.getZeroDigit(var4, var2.getZeroDigit()));
               var2.setExponentSeparator(HostLocaleProviderAdapterImpl.getExponentSeparator(var4, var2.getExponentSeparator()));
               var3 = new SoftReference(var2);
               HostLocaleProviderAdapterImpl.decimalFormatSymbolsMap.put(var1, var3);
            }

            return (DecimalFormatSymbols)var2.clone();
         }
      };
   }

   public static CalendarDataProvider getCalendarDataProvider() {
      return new CalendarDataProvider() {
         public Locale[] getAvailableLocales() {
            return HostLocaleProviderAdapterImpl.getSupportedCalendarLocales();
         }

         public boolean isSupportedLocale(Locale var1) {
            return HostLocaleProviderAdapterImpl.isSupportedCalendarLocale(var1);
         }

         public int getFirstDayOfWeek(Locale var1) {
            return HostLocaleProviderAdapterImpl.getCalendarInt(var1.toLanguageTag(), 0);
         }

         public int getMinimalDaysInFirstWeek(Locale var1) {
            return HostLocaleProviderAdapterImpl.getCalendarInt(var1.toLanguageTag(), 1);
         }
      };
   }

   public static CalendarNameProvider getCalendarNameProvider() {
      return new CalendarNameProvider() {
         public Locale[] getAvailableLocales() {
            return HostLocaleProviderAdapterImpl.getSupportedCalendarLocales();
         }

         public boolean isSupportedLocale(Locale var1) {
            return HostLocaleProviderAdapterImpl.isSupportedCalendarLocale(var1);
         }

         public String getDisplayName(String var1, int var2, int var3, int var4, Locale var5) {
            return null;
         }

         public Map<String, Integer> getDisplayNames(String var1, int var2, int var3, Locale var4) {
            return null;
         }
      };
   }

   public static CalendarProvider getCalendarProvider() {
      return new CalendarProvider() {
         public Locale[] getAvailableLocales() {
            return HostLocaleProviderAdapterImpl.getSupportedCalendarLocales();
         }

         public boolean isSupportedLocale(Locale var1) {
            return HostLocaleProviderAdapterImpl.isSupportedCalendarLocale(var1);
         }

         public Calendar getInstance(TimeZone var1, Locale var2) {
            return (new Calendar.Builder()).setLocale(var2).setCalendarType(HostLocaleProviderAdapterImpl.getCalendarID(var2.toLanguageTag())).setTimeZone(var1).setInstant(System.currentTimeMillis()).build();
         }
      };
   }

   public static CurrencyNameProvider getCurrencyNameProvider() {
      return new CurrencyNameProvider() {
         public Locale[] getAvailableLocales() {
            return HostLocaleProviderAdapterImpl.supportedLocale;
         }

         public boolean isSupportedLocale(Locale var1) {
            return HostLocaleProviderAdapterImpl.supportedLocaleSet.contains(var1.stripExtensions());
         }

         public String getDisplayName(String var1, Locale var2) {
            return HostLocaleProviderAdapterImpl.getDisplayString(var2.toLanguageTag(), 4, var1);
         }

         public String getSymbol(String var1, Locale var2) {
            return HostLocaleProviderAdapterImpl.getDisplayString(var2.toLanguageTag(), 5, var1);
         }
      };
   }

   public static LocaleNameProvider getLocaleNameProvider() {
      return new LocaleNameProvider() {
         public Locale[] getAvailableLocales() {
            return HostLocaleProviderAdapterImpl.supportedLocale;
         }

         public boolean isSupportedLocale(Locale var1) {
            return HostLocaleProviderAdapterImpl.supportedLocaleSet.contains(var1.stripExtensions());
         }

         public String getDisplayLanguage(String var1, Locale var2) {
            return HostLocaleProviderAdapterImpl.getDisplayString(var2.toLanguageTag(), 0, var1);
         }

         public String getDisplayCountry(String var1, Locale var2) {
            return HostLocaleProviderAdapterImpl.getDisplayString(var2.toLanguageTag(), 2, var1);
         }

         public String getDisplayScript(String var1, Locale var2) {
            return HostLocaleProviderAdapterImpl.getDisplayString(var2.toLanguageTag(), 1, var1);
         }

         public String getDisplayVariant(String var1, Locale var2) {
            return HostLocaleProviderAdapterImpl.getDisplayString(var2.toLanguageTag(), 3, var1);
         }
      };
   }

   public static TimeZoneNameProvider getTimeZoneNameProvider() {
      return new TimeZoneNameProvider() {
         public Locale[] getAvailableLocales() {
            return HostLocaleProviderAdapterImpl.supportedLocale;
         }

         public boolean isSupportedLocale(Locale var1) {
            return HostLocaleProviderAdapterImpl.supportedLocaleSet.contains(var1.stripExtensions());
         }

         public String getDisplayName(String var1, boolean var2, int var3, Locale var4) {
            return HostLocaleProviderAdapterImpl.getTimeZoneDisplayString(var4.toLanguageTag(), var3 * 2 + (var2 ? 1 : 0), var1);
         }
      };
   }

   private static Locale[] getSupportedCalendarLocales() {
      if (supportedLocale.length != 0 && supportedLocaleSet.contains(Locale.JAPAN) && isJapaneseCalendar()) {
         Locale[] var0 = new Locale[supportedLocale.length + 1];
         var0[0] = JRELocaleConstants.JA_JP_JP;
         System.arraycopy(supportedLocale, 0, var0, 1, supportedLocale.length);
         return var0;
      } else {
         return supportedLocale;
      }
   }

   private static boolean isSupportedCalendarLocale(Locale var0) {
      Locale var1 = var0;
      if (var0.hasExtensions() || var0.getVariant() != "") {
         var1 = (new Locale.Builder()).setLocale(var0).clearExtensions().build();
      }

      if (!supportedLocaleSet.contains(var1)) {
         return false;
      } else {
         String var2 = var0.getUnicodeLocaleType("ca");
         String var3 = getCalendarID(var1.toLanguageTag()).replaceFirst("gregorian", "gregory");
         return var2 == null ? Calendar.getAvailableCalendarTypes().contains(var3) : var2.equals(var3);
      }
   }

   private static boolean isJapaneseCalendar() {
      return getCalendarID("ja-JP").equals("japanese");
   }

   private static Locale getCalendarLocale(Locale var0) {
      String var1 = getCalendarID(var0.toLanguageTag()).replaceFirst("gregorian", "gregory");
      return Calendar.getAvailableCalendarTypes().contains(var1) ? (new Locale.Builder()).setLocale(var0).setUnicodeLocaleKeyword("ca", var1).build() : var0;
   }

   private static String translateDateFormatLetters(String var0, String var1) {
      String var2 = var1;
      int var3 = var1.length();
      boolean var4 = false;
      StringBuilder var5 = new StringBuilder(var3);
      int var6 = 0;
      char var7 = 0;

      for(int var8 = 0; var8 < var3; ++var8) {
         char var9 = var2.charAt(var8);
         if (var9 == '\'') {
            if (var8 + 1 < var3) {
               char var10 = var2.charAt(var8 + 1);
               if (var10 == '\'') {
                  ++var8;
                  if (var6 != 0) {
                     convert(var0, var7, var6, var5);
                     var7 = 0;
                     var6 = 0;
                  }

                  var5.append("''");
                  continue;
               }
            }

            if (!var4) {
               if (var6 != 0) {
                  convert(var0, var7, var6, var5);
                  var7 = 0;
                  var6 = 0;
               }

               var4 = true;
            } else {
               var4 = false;
            }

            var5.append(var9);
         } else if (var4) {
            var5.append(var9);
         } else if ((var9 < 'a' || var9 > 'z') && (var9 < 'A' || var9 > 'Z')) {
            if (var6 != 0) {
               convert(var0, var7, var6, var5);
               var7 = 0;
               var6 = 0;
            }

            var5.append(var9);
         } else if (var7 != 0 && var7 != var9) {
            convert(var0, var7, var6, var5);
            var7 = var9;
            var6 = 1;
         } else {
            var7 = var9;
            ++var6;
         }
      }

      if (var6 != 0) {
         convert(var0, var7, var6, var5);
      }

      if (var1.contentEquals((CharSequence)var5)) {
         return var1;
      } else {
         return var5.toString();
      }
   }

   private static void convert(String var0, char var1, int var2, StringBuilder var3) {
      switch(var1) {
      case 'A':
      case 'Q':
      case 'U':
      case 'g':
      case 'j':
      case 'l':
      case 'q':
      case 'u':
         var3.append('\'');
         var3.append(var1);
         var3.append('\'');
         break;
      case 'B':
      case 'C':
      case 'D':
      case 'E':
      case 'F':
      case 'H':
      case 'I':
      case 'J':
      case 'K':
      case 'L':
      case 'M':
      case 'N':
      case 'O':
      case 'P':
      case 'R':
      case 'S':
      case 'T':
      case 'W':
      case 'X':
      case 'Y':
      case '[':
      case '\\':
      case ']':
      case '^':
      case '_':
      case '`':
      case 'a':
      case 'b':
      case 'd':
      case 'f':
      case 'h':
      case 'i':
      case 'k':
      case 'm':
      case 'n':
      case 'o':
      case 'p':
      case 'r':
      case 's':
      case 't':
      default:
         appendN(var1, var2, var3);
         break;
      case 'G':
         if (!var0.equals("gregorian")) {
            if (var2 == 5) {
               var2 = 1;
            } else if (var2 == 1) {
               var2 = 4;
            }
         }

         appendN(var1, var2, var3);
         break;
      case 'V':
      case 'v':
         appendN('z', var2, var3);
         break;
      case 'Z':
         if (var2 == 4 || var2 == 5) {
            var3.append("XXX");
         }
         break;
      case 'c':
      case 'e':
         switch(var2) {
         case 1:
            var3.append('u');
         case 2:
         default:
            break;
         case 3:
         case 4:
            appendN('E', var2, var3);
            break;
         case 5:
            appendN('E', 3, var3);
         }
      }

   }

   private static void appendN(char var0, int var1, StringBuilder var2) {
      for(int var3 = 0; var3 < var1; ++var3) {
         var2.append(var0);
      }

   }

   private static native String getDefaultLocale(int var0);

   private static native String getDateTimePatternNative(int var0, int var1, String var2);

   private static native String getCalendarID(String var0);

   private static native String getNumberPatternNative(int var0, String var1);

   private static native String[] getAmPmStrings(String var0, String[] var1);

   private static native String[] getEras(String var0, String[] var1);

   private static native String[] getMonths(String var0, String[] var1);

   private static native String[] getShortMonths(String var0, String[] var1);

   private static native String[] getWeekdays(String var0, String[] var1);

   private static native String[] getShortWeekdays(String var0, String[] var1);

   private static native String getCurrencySymbol(String var0, String var1);

   private static native char getDecimalSeparator(String var0, char var1);

   private static native char getGroupingSeparator(String var0, char var1);

   private static native String getInfinity(String var0, String var1);

   private static native String getInternationalCurrencySymbol(String var0, String var1);

   private static native char getMinusSign(String var0, char var1);

   private static native char getMonetaryDecimalSeparator(String var0, char var1);

   private static native String getNaN(String var0, String var1);

   private static native char getPercent(String var0, char var1);

   private static native char getPerMill(String var0, char var1);

   private static native char getZeroDigit(String var0, char var1);

   private static native String getExponentSeparator(String var0, String var1);

   private static native int getCalendarInt(String var0, int var1);

   private static native String getDisplayString(String var0, int var1, String var2);

   private static native String getTimeZoneDisplayString(String var0, int var1, String var2);

   static {
      HashSet var0 = new HashSet();
      Locale var1 = convertMacOSXLocaleToJavaLocale(getDefaultLocale(1));
      var0.addAll(ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT).getCandidateLocales("", var1));
      var1 = convertMacOSXLocaleToJavaLocale(getDefaultLocale(0));
      var0.addAll(ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT).getCandidateLocales("", var1));
      supportedLocaleSet = Collections.unmodifiableSet(var0);
      supportedLocale = (Locale[])supportedLocaleSet.toArray(new Locale[0]);
   }
}
