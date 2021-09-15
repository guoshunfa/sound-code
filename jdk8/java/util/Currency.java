package java.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.spi.CurrencyNameProvider;
import sun.util.locale.provider.LocaleServiceProviderPool;
import sun.util.logging.PlatformLogger;

public final class Currency implements Serializable {
   private static final long serialVersionUID = -158308464356906721L;
   private final String currencyCode;
   private final transient int defaultFractionDigits;
   private final transient int numericCode;
   private static ConcurrentMap<String, Currency> instances = new ConcurrentHashMap(7);
   private static HashSet<Currency> available;
   static int formatVersion;
   static int dataVersion;
   static int[] mainTable;
   static long[] scCutOverTimes;
   static String[] scOldCurrencies;
   static String[] scNewCurrencies;
   static int[] scOldCurrenciesDFD;
   static int[] scNewCurrenciesDFD;
   static int[] scOldCurrenciesNumericCode;
   static int[] scNewCurrenciesNumericCode;
   static String otherCurrencies;
   static int[] otherCurrenciesDFD;
   static int[] otherCurrenciesNumericCode;
   private static final int MAGIC_NUMBER = 1131770436;
   private static final int A_TO_Z = 26;
   private static final int INVALID_COUNTRY_ENTRY = 127;
   private static final int COUNTRY_WITHOUT_CURRENCY_ENTRY = 512;
   private static final int SIMPLE_CASE_COUNTRY_MASK = 0;
   private static final int SIMPLE_CASE_COUNTRY_FINAL_CHAR_MASK = 31;
   private static final int SIMPLE_CASE_COUNTRY_DEFAULT_DIGITS_MASK = 480;
   private static final int SIMPLE_CASE_COUNTRY_DEFAULT_DIGITS_SHIFT = 5;
   private static final int SIMPLE_CASE_COUNTRY_MAX_DEFAULT_DIGITS = 9;
   private static final int SPECIAL_CASE_COUNTRY_MASK = 512;
   private static final int SPECIAL_CASE_COUNTRY_INDEX_MASK = 31;
   private static final int SPECIAL_CASE_COUNTRY_INDEX_DELTA = 1;
   private static final int COUNTRY_TYPE_MASK = 512;
   private static final int NUMERIC_CODE_MASK = 1047552;
   private static final int NUMERIC_CODE_SHIFT = 10;
   private static final int VALID_FORMAT_VERSION = 2;
   private static final int SYMBOL = 0;
   private static final int DISPLAYNAME = 1;

   private Currency(String var1, int var2, int var3) {
      this.currencyCode = var1;
      this.defaultFractionDigits = var2;
      this.numericCode = var3;
   }

   public static Currency getInstance(String var0) {
      return getInstance(var0, Integer.MIN_VALUE, 0);
   }

   private static Currency getInstance(String var0, int var1, int var2) {
      Currency var3 = (Currency)instances.get(var0);
      if (var3 != null) {
         return var3;
      } else {
         if (var1 == Integer.MIN_VALUE) {
            if (var0.length() != 3) {
               throw new IllegalArgumentException();
            }

            char var4 = var0.charAt(0);
            char var5 = var0.charAt(1);
            int var6 = getMainTableEntry(var4, var5);
            if ((var6 & 512) == 0 && var6 != 127 && var0.charAt(2) - 65 == (var6 & 31)) {
               var1 = (var6 & 480) >> 5;
               var2 = (var6 & 1047552) >> 10;
            } else {
               if (var0.charAt(2) == '-') {
                  throw new IllegalArgumentException();
               }

               int var7 = otherCurrencies.indexOf(var0);
               if (var7 == -1) {
                  throw new IllegalArgumentException();
               }

               var1 = otherCurrenciesDFD[var7 / 4];
               var2 = otherCurrenciesNumericCode[var7 / 4];
            }
         }

         Currency var8 = new Currency(var0, var1, var2);
         var3 = (Currency)instances.putIfAbsent(var0, var8);
         return var3 != null ? var3 : var8;
      }
   }

   public static Currency getInstance(Locale var0) {
      String var1 = var0.getCountry();
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var1.length() != 2) {
         throw new IllegalArgumentException();
      } else {
         char var2 = var1.charAt(0);
         char var3 = var1.charAt(1);
         int var4 = getMainTableEntry(var2, var3);
         if ((var4 & 512) == 0 && var4 != 127) {
            char var9 = (char)((var4 & 31) + 65);
            int var6 = (var4 & 480) >> 5;
            int var7 = (var4 & 1047552) >> 10;
            StringBuilder var8 = new StringBuilder(var1);
            var8.append(var9);
            return getInstance(var8.toString(), var6, var7);
         } else if (var4 == 127) {
            throw new IllegalArgumentException();
         } else if (var4 == 512) {
            return null;
         } else {
            int var5 = (var4 & 31) - 1;
            return scCutOverTimes[var5] != Long.MAX_VALUE && System.currentTimeMillis() >= scCutOverTimes[var5] ? getInstance(scNewCurrencies[var5], scNewCurrenciesDFD[var5], scNewCurrenciesNumericCode[var5]) : getInstance(scOldCurrencies[var5], scOldCurrenciesDFD[var5], scOldCurrenciesNumericCode[var5]);
         }
      }
   }

   public static Set<Currency> getAvailableCurrencies() {
      Class var0 = Currency.class;
      synchronized(Currency.class) {
         if (available == null) {
            available = new HashSet(256);
            char var1 = 'A';

            label43:
            while(true) {
               if (var1 > 'Z') {
                  StringTokenizer var11 = new StringTokenizer(otherCurrencies, "-");

                  while(true) {
                     if (!var11.hasMoreElements()) {
                        break label43;
                     }

                     available.add(getInstance((String)var11.nextElement()));
                  }
               }

               for(char var2 = 'A'; var2 <= 'Z'; ++var2) {
                  int var3 = getMainTableEntry(var1, var2);
                  if ((var3 & 512) == 0 && var3 != 127) {
                     char var4 = (char)((var3 & 31) + 65);
                     int var5 = (var3 & 480) >> 5;
                     int var6 = (var3 & 1047552) >> 10;
                     StringBuilder var7 = new StringBuilder();
                     var7.append(var1);
                     var7.append(var2);
                     var7.append(var4);
                     available.add(getInstance(var7.toString(), var5, var6));
                  }
               }

               ++var1;
            }
         }
      }

      Set var10 = (Set)available.clone();
      return var10;
   }

   public String getCurrencyCode() {
      return this.currencyCode;
   }

   public String getSymbol() {
      return this.getSymbol(Locale.getDefault(Locale.Category.DISPLAY));
   }

   public String getSymbol(Locale var1) {
      LocaleServiceProviderPool var2 = LocaleServiceProviderPool.getPool(CurrencyNameProvider.class);
      String var3 = (String)var2.getLocalizedObject(Currency.CurrencyNameGetter.INSTANCE, var1, this.currencyCode, 0);
      return var3 != null ? var3 : this.currencyCode;
   }

   public int getDefaultFractionDigits() {
      return this.defaultFractionDigits;
   }

   public int getNumericCode() {
      return this.numericCode;
   }

   public String getDisplayName() {
      return this.getDisplayName(Locale.getDefault(Locale.Category.DISPLAY));
   }

   public String getDisplayName(Locale var1) {
      LocaleServiceProviderPool var2 = LocaleServiceProviderPool.getPool(CurrencyNameProvider.class);
      String var3 = (String)var2.getLocalizedObject(Currency.CurrencyNameGetter.INSTANCE, var1, this.currencyCode, 1);
      return var3 != null ? var3 : this.currencyCode;
   }

   public String toString() {
      return this.currencyCode;
   }

   private Object readResolve() {
      return getInstance(this.currencyCode);
   }

   private static int getMainTableEntry(char var0, char var1) {
      if (var0 >= 'A' && var0 <= 'Z' && var1 >= 'A' && var1 <= 'Z') {
         return mainTable[(var0 - 65) * 26 + (var1 - 65)];
      } else {
         throw new IllegalArgumentException();
      }
   }

   private static void setMainTableEntry(char var0, char var1, int var2) {
      if (var0 >= 'A' && var0 <= 'Z' && var1 >= 'A' && var1 <= 'Z') {
         mainTable[(var0 - 65) * 26 + (var1 - 65)] = var2;
      } else {
         throw new IllegalArgumentException();
      }
   }

   private static int[] readIntArray(DataInputStream var0, int var1) throws IOException {
      int[] var2 = new int[var1];

      for(int var3 = 0; var3 < var1; ++var3) {
         var2[var3] = var0.readInt();
      }

      return var2;
   }

   private static long[] readLongArray(DataInputStream var0, int var1) throws IOException {
      long[] var2 = new long[var1];

      for(int var3 = 0; var3 < var1; ++var3) {
         var2[var3] = var0.readLong();
      }

      return var2;
   }

   private static String[] readStringArray(DataInputStream var0, int var1) throws IOException {
      String[] var2 = new String[var1];

      for(int var3 = 0; var3 < var1; ++var3) {
         var2[var3] = var0.readUTF();
      }

      return var2;
   }

   private static void replaceCurrencyData(Pattern var0, String var1, String var2) {
      if (var1.length() != 2) {
         info("currency.properties entry for " + var1 + " is ignored because of the invalid country code.", (Throwable)null);
      } else {
         Matcher var3 = var0.matcher(var2);
         if (var3.find() && (var3.group(4) != null || countOccurrences(var2, ',') < 3)) {
            try {
               if (var3.group(4) != null && !isPastCutoverDate(var3.group(4))) {
                  info("currency.properties entry for " + var1 + " ignored since cutover date has not passed :" + var2, (Throwable)null);
                  return;
               }
            } catch (ParseException var9) {
               info("currency.properties entry for " + var1 + " ignored since exception encountered :" + var9.getMessage(), (Throwable)null);
               return;
            }

            String var4 = var3.group(1);
            int var5 = Integer.parseInt(var3.group(2));
            int var6 = var5 << 10;
            int var7 = Integer.parseInt(var3.group(3));
            if (var7 > 9) {
               info("currency.properties entry for " + var1 + " ignored since the fraction is more than " + 9 + ":" + var2, (Throwable)null);
            } else {
               int var8;
               for(var8 = 0; var8 < scOldCurrencies.length && !scOldCurrencies[var8].equals(var4); ++var8) {
               }

               if (var8 == scOldCurrencies.length) {
                  var6 |= var7 << 5 | var4.charAt(2) - 65;
               } else {
                  var6 |= 512 | var8 + 1;
               }

               setMainTableEntry(var1.charAt(0), var1.charAt(1), var6);
            }
         } else {
            info("currency.properties entry for " + var1 + " ignored because the value format is not recognized.", (Throwable)null);
         }
      }
   }

   private static boolean isPastCutoverDate(String var0) throws ParseException {
      SimpleDateFormat var1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT);
      var1.setTimeZone(TimeZone.getTimeZone("UTC"));
      var1.setLenient(false);
      long var2 = var1.parse(var0.trim()).getTime();
      return System.currentTimeMillis() > var2;
   }

   private static int countOccurrences(String var0, char var1) {
      int var2 = 0;
      char[] var3 = var0.toCharArray();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         char var6 = var3[var5];
         if (var6 == var1) {
            ++var2;
         }
      }

      return var2;
   }

   private static void info(String var0, Throwable var1) {
      PlatformLogger var2 = PlatformLogger.getLogger("java.util.Currency");
      if (var2.isLoggable(PlatformLogger.Level.INFO)) {
         if (var1 != null) {
            var2.info(var0, var1);
         } else {
            var2.info(var0);
         }
      }

   }

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            String var1 = System.getProperty("java.home");

            String var2;
            try {
               var2 = var1 + File.separator + "lib" + File.separator + "currency.data";
               DataInputStream var3 = new DataInputStream(new BufferedInputStream(new FileInputStream(var2)));
               Throwable var4 = null;

               try {
                  if (var3.readInt() != 1131770436) {
                     throw new InternalError("Currency data is possibly corrupted");
                  }

                  Currency.formatVersion = var3.readInt();
                  if (Currency.formatVersion != 2) {
                     throw new InternalError("Currency data format is incorrect");
                  }

                  Currency.dataVersion = var3.readInt();
                  Currency.mainTable = Currency.readIntArray(var3, 676);
                  int var5 = var3.readInt();
                  Currency.scCutOverTimes = Currency.readLongArray(var3, var5);
                  Currency.scOldCurrencies = Currency.readStringArray(var3, var5);
                  Currency.scNewCurrencies = Currency.readStringArray(var3, var5);
                  Currency.scOldCurrenciesDFD = Currency.readIntArray(var3, var5);
                  Currency.scNewCurrenciesDFD = Currency.readIntArray(var3, var5);
                  Currency.scOldCurrenciesNumericCode = Currency.readIntArray(var3, var5);
                  Currency.scNewCurrenciesNumericCode = Currency.readIntArray(var3, var5);
                  int var6 = var3.readInt();
                  Currency.otherCurrencies = var3.readUTF();
                  Currency.otherCurrenciesDFD = Currency.readIntArray(var3, var6);
                  Currency.otherCurrenciesNumericCode = Currency.readIntArray(var3, var6);
               } catch (Throwable var35) {
                  var4 = var35;
                  throw var35;
               } finally {
                  if (var3 != null) {
                     if (var4 != null) {
                        try {
                           var3.close();
                        } catch (Throwable var30) {
                           var4.addSuppressed(var30);
                        }
                     } else {
                        var3.close();
                     }
                  }

               }
            } catch (IOException var37) {
               throw new InternalError(var37);
            }

            var2 = System.getProperty("java.util.currency.data");
            if (var2 == null) {
               var2 = var1 + File.separator + "lib" + File.separator + "currency.properties";
            }

            try {
               File var38 = new File(var2);
               if (var38.exists()) {
                  Properties var39 = new Properties();
                  FileReader var40 = new FileReader(var38);
                  Throwable var42 = null;

                  try {
                     var39.load((Reader)var40);
                  } catch (Throwable var32) {
                     var42 = var32;
                     throw var32;
                  } finally {
                     if (var40 != null) {
                        if (var42 != null) {
                           try {
                              var40.close();
                           } catch (Throwable var31) {
                              var42.addSuppressed(var31);
                           }
                        } else {
                           var40.close();
                        }
                     }

                  }

                  Set var41 = var39.stringPropertyNames();
                  Pattern var43 = Pattern.compile("([A-Z]{3})\\s*,\\s*(\\d{3})\\s*,\\s*(\\d+)\\s*,?\\s*(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2})?");
                  Iterator var7 = var41.iterator();

                  while(var7.hasNext()) {
                     String var8 = (String)var7.next();
                     Currency.replaceCurrencyData(var43, var8.toUpperCase(Locale.ROOT), var39.getProperty(var8).toUpperCase(Locale.ROOT));
                  }
               }
            } catch (IOException var34) {
               Currency.info("currency.properties is ignored because of an IOException", var34);
            }

            return null;
         }
      });
   }

   private static class CurrencyNameGetter implements LocaleServiceProviderPool.LocalizedObjectGetter<CurrencyNameProvider, String> {
      private static final Currency.CurrencyNameGetter INSTANCE = new Currency.CurrencyNameGetter();

      public String getObject(CurrencyNameProvider var1, Locale var2, String var3, Object... var4) {
         assert var4.length == 1;

         int var5 = (Integer)var4[0];
         switch(var5) {
         case 0:
            return var1.getSymbol(var3, var2);
         case 1:
            return var1.getDisplayName(var3, var2);
         default:
            assert false;

            return null;
         }
      }
   }
}
