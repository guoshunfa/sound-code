package java.text;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.util.calendar.CalendarUtils;
import sun.util.calendar.ZoneInfoFile;
import sun.util.locale.provider.LocaleProviderAdapter;

public class SimpleDateFormat extends DateFormat {
   static final long serialVersionUID = 4774881970558875024L;
   static final int currentSerialVersion = 1;
   private int serialVersionOnStream;
   private String pattern;
   private transient NumberFormat originalNumberFormat;
   private transient String originalNumberPattern;
   private transient char minusSign;
   private transient boolean hasFollowingMinusSign;
   private transient boolean forceStandaloneForm;
   private transient char[] compiledPattern;
   private static final int TAG_QUOTE_ASCII_CHAR = 100;
   private static final int TAG_QUOTE_CHARS = 101;
   private transient char zeroDigit;
   private DateFormatSymbols formatData;
   private Date defaultCenturyStart;
   private transient int defaultCenturyStartYear;
   private static final int MILLIS_PER_MINUTE = 60000;
   private static final String GMT = "GMT";
   private static final ConcurrentMap<Locale, NumberFormat> cachedNumberFormatData = new ConcurrentHashMap(3);
   private Locale locale;
   transient boolean useDateFormatSymbols;
   private static final int[] PATTERN_INDEX_TO_CALENDAR_FIELD = new int[]{0, 1, 2, 5, 11, 11, 12, 13, 14, 7, 6, 8, 3, 4, 9, 10, 10, 15, 15, 17, 1000, 15, 2};
   private static final int[] PATTERN_INDEX_TO_DATE_FORMAT_FIELD = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 17, 1, 9, 17, 2};
   private static final DateFormat.Field[] PATTERN_INDEX_TO_DATE_FORMAT_FIELD_ID;
   private static final int[] REST_OF_STYLES;

   public SimpleDateFormat() {
      this("", Locale.getDefault(Locale.Category.FORMAT));
      this.applyPatternImpl(LocaleProviderAdapter.getResourceBundleBased().getLocaleResources(this.locale).getDateTimePattern(3, 3, this.calendar));
   }

   public SimpleDateFormat(String var1) {
      this(var1, Locale.getDefault(Locale.Category.FORMAT));
   }

   public SimpleDateFormat(String var1, Locale var2) {
      this.serialVersionOnStream = 1;
      this.minusSign = '-';
      this.hasFollowingMinusSign = false;
      this.forceStandaloneForm = false;
      if (var1 != null && var2 != null) {
         this.initializeCalendar(var2);
         this.pattern = var1;
         this.formatData = DateFormatSymbols.getInstanceRef(var2);
         this.locale = var2;
         this.initialize(var2);
      } else {
         throw new NullPointerException();
      }
   }

   public SimpleDateFormat(String var1, DateFormatSymbols var2) {
      this.serialVersionOnStream = 1;
      this.minusSign = '-';
      this.hasFollowingMinusSign = false;
      this.forceStandaloneForm = false;
      if (var1 != null && var2 != null) {
         this.pattern = var1;
         this.formatData = (DateFormatSymbols)var2.clone();
         this.locale = Locale.getDefault(Locale.Category.FORMAT);
         this.initializeCalendar(this.locale);
         this.initialize(this.locale);
         this.useDateFormatSymbols = true;
      } else {
         throw new NullPointerException();
      }
   }

   private void initialize(Locale var1) {
      this.compiledPattern = this.compile(this.pattern);
      this.numberFormat = (NumberFormat)cachedNumberFormatData.get(var1);
      if (this.numberFormat == null) {
         this.numberFormat = NumberFormat.getIntegerInstance(var1);
         this.numberFormat.setGroupingUsed(false);
         cachedNumberFormatData.putIfAbsent(var1, this.numberFormat);
      }

      this.numberFormat = (NumberFormat)this.numberFormat.clone();
      this.initializeDefaultCentury();
   }

   private void initializeCalendar(Locale var1) {
      if (this.calendar == null) {
         assert var1 != null;

         this.calendar = Calendar.getInstance(TimeZone.getDefault(), var1);
      }

   }

   private char[] compile(String var1) {
      int var2 = var1.length();
      boolean var3 = false;
      StringBuilder var4 = new StringBuilder(var2 * 2);
      StringBuilder var5 = null;
      int var6 = 0;
      int var7 = 0;
      int var8 = -1;
      int var9 = -1;

      int var10;
      for(var10 = 0; var10 < var2; ++var10) {
         char var11 = var1.charAt(var10);
         int var12;
         char var13;
         if (var11 == '\'') {
            if (var10 + 1 < var2) {
               var11 = var1.charAt(var10 + 1);
               if (var11 == '\'') {
                  ++var10;
                  if (var6 != 0) {
                     encode(var8, var6, var4);
                     ++var7;
                     var9 = var8;
                     var8 = -1;
                     var6 = 0;
                  }

                  if (var3) {
                     var5.append(var11);
                  } else {
                     var4.append((char)(25600 | var11));
                  }
                  continue;
               }
            }

            if (!var3) {
               if (var6 != 0) {
                  encode(var8, var6, var4);
                  ++var7;
                  var9 = var8;
                  var8 = -1;
                  var6 = 0;
               }

               if (var5 == null) {
                  var5 = new StringBuilder(var2);
               } else {
                  var5.setLength(0);
               }

               var3 = true;
            } else {
               var12 = var5.length();
               if (var12 == 1) {
                  var13 = var5.charAt(0);
                  if (var13 < 128) {
                     var4.append((char)(25600 | var13));
                  } else {
                     var4.append('攁');
                     var4.append(var13);
                  }
               } else {
                  encode(101, var12, var4);
                  var4.append((CharSequence)var5);
               }

               var3 = false;
            }
         } else if (var3) {
            var5.append(var11);
         } else if ((var11 < 'a' || var11 > 'z') && (var11 < 'A' || var11 > 'Z')) {
            if (var6 != 0) {
               encode(var8, var6, var4);
               ++var7;
               var9 = var8;
               var8 = -1;
               var6 = 0;
            }

            if (var11 < 128) {
               var4.append((char)(25600 | var11));
            } else {
               for(var12 = var10 + 1; var12 < var2; ++var12) {
                  var13 = var1.charAt(var12);
                  if (var13 == '\'' || var13 >= 'a' && var13 <= 'z' || var13 >= 'A' && var13 <= 'Z') {
                     break;
                  }
               }

               var4.append((char)(25856 | var12 - var10));

               while(var10 < var12) {
                  var4.append(var1.charAt(var10));
                  ++var10;
               }

               --var10;
            }
         } else {
            if ((var12 = "GyMdkHmsSEDFwWahKzZYuXL".indexOf(var11)) == -1) {
               throw new IllegalArgumentException("Illegal pattern character '" + var11 + "'");
            }

            if (var8 != -1 && var8 != var12) {
               encode(var8, var6, var4);
               ++var7;
               var9 = var8;
               var8 = var12;
               var6 = 1;
            } else {
               var8 = var12;
               ++var6;
            }
         }
      }

      if (var3) {
         throw new IllegalArgumentException("Unterminated quote");
      } else {
         if (var6 != 0) {
            encode(var8, var6, var4);
            ++var7;
            var9 = var8;
         }

         this.forceStandaloneForm = var7 == 1 && var9 == 2;
         var10 = var4.length();
         char[] var14 = new char[var10];
         var4.getChars(0, var10, var14, 0);
         return var14;
      }
   }

   private static void encode(int var0, int var1, StringBuilder var2) {
      if (var0 == 21 && var1 >= 4) {
         throw new IllegalArgumentException("invalid ISO 8601 format: length=" + var1);
      } else {
         if (var1 < 255) {
            var2.append((char)(var0 << 8 | var1));
         } else {
            var2.append((char)(var0 << 8 | 255));
            var2.append((char)(var1 >>> 16));
            var2.append((char)(var1 & '\uffff'));
         }

      }
   }

   private void initializeDefaultCentury() {
      this.calendar.setTimeInMillis(System.currentTimeMillis());
      this.calendar.add(1, -80);
      this.parseAmbiguousDatesAsAfter(this.calendar.getTime());
   }

   private void parseAmbiguousDatesAsAfter(Date var1) {
      this.defaultCenturyStart = var1;
      this.calendar.setTime(var1);
      this.defaultCenturyStartYear = this.calendar.get(1);
   }

   public void set2DigitYearStart(Date var1) {
      this.parseAmbiguousDatesAsAfter(new Date(var1.getTime()));
   }

   public Date get2DigitYearStart() {
      return (Date)this.defaultCenturyStart.clone();
   }

   public StringBuffer format(Date var1, StringBuffer var2, FieldPosition var3) {
      var3.beginIndex = var3.endIndex = 0;
      return this.format(var1, var2, var3.getFieldDelegate());
   }

   private StringBuffer format(Date var1, StringBuffer var2, Format.FieldDelegate var3) {
      this.calendar.setTime(var1);
      boolean var4 = this.useDateFormatSymbols();
      int var5 = 0;

      while(var5 < this.compiledPattern.length) {
         int var6 = this.compiledPattern[var5] >>> 8;
         int var7 = this.compiledPattern[var5++] & 255;
         if (var7 == 255) {
            var7 = this.compiledPattern[var5++] << 16;
            var7 |= this.compiledPattern[var5++];
         }

         switch(var6) {
         case 100:
            var2.append((char)var7);
            break;
         case 101:
            var2.append(this.compiledPattern, var5, var7);
            var5 += var7;
            break;
         default:
            this.subFormat(var6, var7, var3, var2, var4);
         }
      }

      return var2;
   }

   public AttributedCharacterIterator formatToCharacterIterator(Object var1) {
      StringBuffer var2 = new StringBuffer();
      CharacterIteratorFieldDelegate var3 = new CharacterIteratorFieldDelegate();
      if (var1 instanceof Date) {
         this.format((Date)var1, var2, (Format.FieldDelegate)var3);
      } else {
         if (!(var1 instanceof Number)) {
            if (var1 == null) {
               throw new NullPointerException("formatToCharacterIterator must be passed non-null object");
            }

            throw new IllegalArgumentException("Cannot format given Object as a Date");
         }

         this.format(new Date(((Number)var1).longValue()), var2, (Format.FieldDelegate)var3);
      }

      return var3.getIterator(var2.toString());
   }

   private void subFormat(int var1, int var2, Format.FieldDelegate var3, StringBuffer var4, boolean var5) {
      int var6 = Integer.MAX_VALUE;
      String var7 = null;
      int var8 = var4.length();
      int var9 = PATTERN_INDEX_TO_CALENDAR_FIELD[var1];
      int var10;
      if (var9 == 17) {
         if (this.calendar.isWeekDateSupported()) {
            var10 = this.calendar.getWeekYear();
         } else {
            var1 = 1;
            var9 = PATTERN_INDEX_TO_CALENDAR_FIELD[var1];
            var10 = this.calendar.get(var9);
         }
      } else if (var9 == 1000) {
         var10 = CalendarBuilder.toISODayOfWeek(this.calendar.get(7));
      } else {
         var10 = this.calendar.get(var9);
      }

      int var11 = var2 >= 4 ? 2 : 1;
      if (!var5 && var9 < 15 && var1 != 22) {
         var7 = this.calendar.getDisplayName(var9, var11, this.locale);
      }

      String[] var12;
      int var13;
      int var15;
      switch(var1) {
      case 0:
         if (var5) {
            var12 = this.formatData.getEras();
            if (var10 < var12.length) {
               var7 = var12[var10];
            }
         }

         if (var7 == null) {
            var7 = "";
         }
         break;
      case 1:
      case 19:
         if (this.calendar instanceof GregorianCalendar) {
            if (var2 != 2) {
               this.zeroPaddingNumber(var10, var2, var6, var4);
            } else {
               this.zeroPaddingNumber(var10, 2, 2, var4);
            }
         } else if (var7 == null) {
            this.zeroPaddingNumber(var10, var11 == 2 ? 1 : var2, var6, var4);
         }
         break;
      case 2:
         if (var5) {
            if (var2 >= 4) {
               var12 = this.formatData.getMonths();
               var7 = var12[var10];
            } else if (var2 == 3) {
               var12 = this.formatData.getShortMonths();
               var7 = var12[var10];
            }
         } else if (var2 < 3) {
            var7 = null;
         } else if (this.forceStandaloneForm) {
            var7 = this.calendar.getDisplayName(var9, var11 | '耀', this.locale);
            if (var7 == null) {
               var7 = this.calendar.getDisplayName(var9, var11, this.locale);
            }
         }

         if (var7 == null) {
            this.zeroPaddingNumber(var10 + 1, var2, var6, var4);
         }
         break;
      case 3:
      case 5:
      case 6:
      case 7:
      case 8:
      case 10:
      case 11:
      case 12:
      case 13:
      case 16:
      case 20:
      default:
         if (var7 == null) {
            this.zeroPaddingNumber(var10, var2, var6, var4);
         }
         break;
      case 4:
         if (var7 == null) {
            if (var10 == 0) {
               this.zeroPaddingNumber(this.calendar.getMaximum(11) + 1, var2, var6, var4);
            } else {
               this.zeroPaddingNumber(var10, var2, var6, var4);
            }
         }
         break;
      case 9:
         if (var5) {
            if (var2 >= 4) {
               var12 = this.formatData.getWeekdays();
               var7 = var12[var10];
            } else {
               var12 = this.formatData.getShortWeekdays();
               var7 = var12[var10];
            }
         }
         break;
      case 14:
         if (var5) {
            var12 = this.formatData.getAmPmStrings();
            var7 = var12[var10];
         }
         break;
      case 15:
         if (var7 == null) {
            if (var10 == 0) {
               this.zeroPaddingNumber(this.calendar.getLeastMaximum(10) + 1, var2, var6, var4);
            } else {
               this.zeroPaddingNumber(var10, var2, var6, var4);
            }
         }
         break;
      case 17:
         if (var7 == null) {
            if (this.formatData.locale != null && !this.formatData.isZoneStringsSet) {
               TimeZone var16 = this.calendar.getTimeZone();
               boolean var17 = this.calendar.get(16) != 0;
               int var18 = var2 < 4 ? 0 : 1;
               var4.append(var16.getDisplayName(var17, var18, this.formatData.locale));
            } else {
               var15 = this.formatData.getZoneIndex(this.calendar.getTimeZone().getID());
               if (var15 == -1) {
                  var10 = this.calendar.get(15) + this.calendar.get(16);
                  var4.append(ZoneInfoFile.toCustomID(var10));
               } else {
                  var13 = this.calendar.get(16) == 0 ? 1 : 3;
                  if (var2 < 4) {
                     ++var13;
                  }

                  String[][] var14 = this.formatData.getZoneStringsWrapper();
                  var4.append(var14[var15][var13]);
               }
            }
         }
         break;
      case 18:
         var10 = (this.calendar.get(15) + this.calendar.get(16)) / '\uea60';
         var15 = 4;
         if (var10 >= 0) {
            var4.append('+');
         } else {
            ++var15;
         }

         var13 = var10 / 60 * 100 + var10 % 60;
         CalendarUtils.sprintf0d(var4, var13, var15);
         break;
      case 21:
         var10 = this.calendar.get(15) + this.calendar.get(16);
         if (var10 == 0) {
            var4.append('Z');
         } else {
            var10 /= 60000;
            if (var10 >= 0) {
               var4.append('+');
            } else {
               var4.append('-');
               var10 = -var10;
            }

            CalendarUtils.sprintf0d((StringBuffer)var4, var10 / 60, 2);
            if (var2 != 1) {
               if (var2 == 3) {
                  var4.append(':');
               }

               CalendarUtils.sprintf0d((StringBuffer)var4, var10 % 60, 2);
            }
         }
         break;
      case 22:
         assert var7 == null;

         if (this.locale == null) {
            if (var2 >= 4) {
               var12 = this.formatData.getMonths();
               var7 = var12[var10];
            } else if (var2 == 3) {
               var12 = this.formatData.getShortMonths();
               var7 = var12[var10];
            }
         } else if (var2 >= 3) {
            var7 = this.calendar.getDisplayName(var9, var11 | '耀', this.locale);
         }

         if (var7 == null) {
            this.zeroPaddingNumber(var10 + 1, var2, var6, var4);
         }
      }

      if (var7 != null) {
         var4.append(var7);
      }

      var15 = PATTERN_INDEX_TO_DATE_FORMAT_FIELD[var1];
      DateFormat.Field var19 = PATTERN_INDEX_TO_DATE_FORMAT_FIELD_ID[var1];
      var3.formatted(var15, var19, var19, var8, var4.length(), var4);
   }

   private void zeroPaddingNumber(int var1, int var2, int var3, StringBuffer var4) {
      try {
         if (this.zeroDigit == 0) {
            this.zeroDigit = ((DecimalFormat)this.numberFormat).getDecimalFormatSymbols().getZeroDigit();
         }

         if (var1 >= 0) {
            if (var1 < 100 && var2 >= 1 && var2 <= 2) {
               if (var1 < 10) {
                  if (var2 == 2) {
                     var4.append(this.zeroDigit);
                  }

                  var4.append((char)(this.zeroDigit + var1));
               } else {
                  var4.append((char)(this.zeroDigit + var1 / 10));
                  var4.append((char)(this.zeroDigit + var1 % 10));
               }

               return;
            }

            if (var1 >= 1000 && var1 < 10000) {
               if (var2 == 4) {
                  var4.append((char)(this.zeroDigit + var1 / 1000));
                  var1 %= 1000;
                  var4.append((char)(this.zeroDigit + var1 / 100));
                  var1 %= 100;
                  var4.append((char)(this.zeroDigit + var1 / 10));
                  var4.append((char)(this.zeroDigit + var1 % 10));
                  return;
               }

               if (var2 == 2 && var3 == 2) {
                  this.zeroPaddingNumber(var1 % 100, 2, 2, var4);
                  return;
               }
            }
         }
      } catch (Exception var6) {
      }

      this.numberFormat.setMinimumIntegerDigits(var2);
      this.numberFormat.setMaximumIntegerDigits(var3);
      this.numberFormat.format((long)var1, var4, DontCareFieldPosition.INSTANCE);
   }

   public Date parse(String var1, ParsePosition var2) {
      this.checkNegativeNumberExpression();
      int var3 = var2.index;
      int var4 = var3;
      int var5 = var1.length();
      boolean[] var6 = new boolean[]{false};
      CalendarBuilder var7 = new CalendarBuilder();
      int var8 = 0;

      label82:
      while(var8 < this.compiledPattern.length) {
         int var9 = this.compiledPattern[var8] >>> 8;
         int var10 = this.compiledPattern[var8++] & 255;
         if (var10 == 255) {
            var10 = this.compiledPattern[var8++] << 16;
            var10 |= this.compiledPattern[var8++];
         }

         switch(var9) {
         case 100:
            if (var3 < var5 && var1.charAt(var3) == (char)var10) {
               ++var3;
               break;
            }

            var2.index = var4;
            var2.errorIndex = var3;
            return null;
         case 101:
            while(true) {
               if (var10-- <= 0) {
                  continue label82;
               }

               if (var3 >= var5 || var1.charAt(var3) != this.compiledPattern[var8++]) {
                  var2.index = var4;
                  var2.errorIndex = var3;
                  return null;
               }

               ++var3;
            }
         default:
            boolean var11 = false;
            boolean var12 = false;
            if (var8 < this.compiledPattern.length) {
               int var13 = this.compiledPattern[var8] >>> 8;
               if (var13 != 100 && var13 != 101) {
                  var11 = true;
               }

               if (this.hasFollowingMinusSign && (var13 == 100 || var13 == 101)) {
                  int var14;
                  if (var13 == 100) {
                     var14 = this.compiledPattern[var8] & 255;
                  } else {
                     var14 = this.compiledPattern[var8 + 1];
                  }

                  if (var14 == this.minusSign) {
                     var12 = true;
                  }
               }
            }

            var3 = this.subParse(var1, var3, var9, var10, var11, var6, var2, var12, var7);
            if (var3 < 0) {
               var2.index = var4;
               return null;
            }
         }
      }

      var2.index = var3;

      try {
         Date var16 = var7.establish(this.calendar).getTime();
         if (var6[0] && var16.before(this.defaultCenturyStart)) {
            var16 = var7.addYear(100).establish(this.calendar).getTime();
         }

         return var16;
      } catch (IllegalArgumentException var15) {
         var2.errorIndex = var3;
         var2.index = var4;
         return null;
      }
   }

   private int matchString(String var1, int var2, int var3, String[] var4, CalendarBuilder var5) {
      int var6 = 0;
      int var7 = var4.length;
      if (var3 == 7) {
         var6 = 1;
      }

      int var8 = 0;

      int var9;
      for(var9 = -1; var6 < var7; ++var6) {
         int var10 = var4[var6].length();
         if (var10 > var8 && var1.regionMatches(true, var2, var4[var6], 0, var10)) {
            var9 = var6;
            var8 = var10;
         }
      }

      if (var9 >= 0) {
         var5.set(var3, var9);
         return var2 + var8;
      } else {
         return -var2;
      }
   }

   private int matchString(String var1, int var2, int var3, Map<String, Integer> var4, CalendarBuilder var5) {
      if (var4 != null) {
         if (var4 instanceof SortedMap) {
            Iterator var10 = var4.keySet().iterator();

            String var11;
            do {
               if (!var10.hasNext()) {
                  return -var2;
               }

               var11 = (String)var10.next();
            } while(!var1.regionMatches(true, var2, var11, 0, var11.length()));

            var5.set(var3, (Integer)var4.get(var11));
            return var2 + var11.length();
         } else {
            String var6 = null;
            Iterator var7 = var4.keySet().iterator();

            while(true) {
               String var8;
               int var9;
               do {
                  if (!var7.hasNext()) {
                     if (var6 != null) {
                        var5.set(var3, (Integer)var4.get(var6));
                        return var2 + var6.length();
                     }

                     return -var2;
                  }

                  var8 = (String)var7.next();
                  var9 = var8.length();
               } while(var6 != null && var9 <= var6.length());

               if (var1.regionMatches(true, var2, var8, 0, var9)) {
                  var6 = var8;
               }
            }
         }
      } else {
         return -var2;
      }
   }

   private int matchZoneString(String var1, int var2, String[] var3) {
      for(int var4 = 1; var4 <= 4; ++var4) {
         String var5 = var3[var4];
         if (var1.regionMatches(true, var2, var5, 0, var5.length())) {
            return var4;
         }
      }

      return -1;
   }

   private boolean matchDSTString(String var1, int var2, int var3, int var4, String[][] var5) {
      int var6 = var4 + 2;
      String var7 = var5[var3][var6];
      return var1.regionMatches(true, var2, var7, 0, var7.length());
   }

   private int subParseZoneString(String var1, int var2, CalendarBuilder var3) {
      boolean var4 = false;
      TimeZone var5 = this.getTimeZone();
      int var6 = this.formatData.getZoneIndex(var5.getID());
      TimeZone var7 = null;
      String[][] var8 = this.formatData.getZoneStringsWrapper();
      String[] var9 = null;
      int var10 = 0;
      if (var6 != -1) {
         var9 = var8[var6];
         if ((var10 = this.matchZoneString(var1, var2, var9)) > 0) {
            if (var10 <= 2) {
               var4 = var9[var10].equalsIgnoreCase(var9[var10 + 2]);
            }

            var7 = TimeZone.getTimeZone(var9[0]);
         }
      }

      if (var7 == null) {
         var6 = this.formatData.getZoneIndex(TimeZone.getDefault().getID());
         if (var6 != -1) {
            var9 = var8[var6];
            if ((var10 = this.matchZoneString(var1, var2, var9)) > 0) {
               if (var10 <= 2) {
                  var4 = var9[var10].equalsIgnoreCase(var9[var10 + 2]);
               }

               var7 = TimeZone.getTimeZone(var9[0]);
            }
         }
      }

      int var11;
      if (var7 == null) {
         var11 = var8.length;

         for(int var12 = 0; var12 < var11; ++var12) {
            var9 = var8[var12];
            if ((var10 = this.matchZoneString(var1, var2, var9)) > 0) {
               if (var10 <= 2) {
                  var4 = var9[var10].equalsIgnoreCase(var9[var10 + 2]);
               }

               var7 = TimeZone.getTimeZone(var9[0]);
               break;
            }
         }
      }

      if (var7 == null) {
         return -var2;
      } else {
         if (!var7.equals(var5)) {
            this.setTimeZone(var7);
         }

         var11 = var10 >= 3 ? var7.getDSTSavings() : 0;
         if (!var4 && (var10 < 3 || var11 != 0)) {
            var3.clear(15).set(16, var11);
         }

         return var2 + var9[var10].length();
      }
   }

   private int subParseNumericZone(String var1, int var2, int var3, int var4, boolean var5, CalendarBuilder var6) {
      int var7 = var2;

      try {
         char var8 = var1.charAt(var7++);
         if (this.isDigit(var8)) {
            int var9 = var8 - 48;
            var8 = var1.charAt(var7++);
            if (this.isDigit(var8)) {
               var9 = var9 * 10 + (var8 - 48);
            } else {
               if (var4 > 0 || !var5) {
                  return 1 - var7;
               }

               --var7;
            }

            if (var9 <= 23) {
               int var10 = 0;
               if (var4 != 1) {
                  var8 = var1.charAt(var7++);
                  if (var5) {
                     if (var8 != ':') {
                        return 1 - var7;
                     }

                     var8 = var1.charAt(var7++);
                  }

                  if (!this.isDigit(var8)) {
                     return 1 - var7;
                  }

                  var10 = var8 - 48;
                  var8 = var1.charAt(var7++);
                  if (!this.isDigit(var8)) {
                     return 1 - var7;
                  }

                  var10 = var10 * 10 + (var8 - 48);
                  if (var10 > 59) {
                     return 1 - var7;
                  }
               }

               var10 += var9 * 60;
               var6.set(15, var10 * '\uea60' * var3).set(16, 0);
               return var7;
            }
         }
      } catch (IndexOutOfBoundsException var11) {
      }

      return 1 - var7;
   }

   private boolean isDigit(char var1) {
      return var1 >= '0' && var1 <= '9';
   }

   private int subParse(String var1, int var2, int var3, int var4, boolean var5, boolean[] var6, ParsePosition var7, boolean var8, CalendarBuilder var9) {
      int var11 = 0;
      ParsePosition var12 = new ParsePosition(0);
      var12.index = var2;
      if (var3 == 19 && !this.calendar.isWeekDateSupported()) {
         var3 = 1;
      }

      for(int var13 = PATTERN_INDEX_TO_CALENDAR_FIELD[var3]; var12.index < var1.length(); ++var12.index) {
         char var14 = var1.charAt(var12.index);
         if (var14 != ' ' && var14 != '\t') {
            label362: {
               int var24 = var12.index;
               Number var10;
               if (var3 == 4 || var3 == 15 || var3 == 2 && var4 <= 2 || var3 == 1 || var3 == 19) {
                  if (var5) {
                     if (var2 + var4 > var1.length()) {
                        break label362;
                     }

                     var10 = this.numberFormat.parse(var1.substring(0, var2 + var4), var12);
                  } else {
                     var10 = this.numberFormat.parse(var1, var12);
                  }

                  if (var10 == null) {
                     if (var3 != 1 || this.calendar instanceof GregorianCalendar) {
                        break label362;
                     }
                  } else {
                     var11 = var10.intValue();
                     if (var8 && var11 < 0 && (var12.index < var1.length() && var1.charAt(var12.index) != this.minusSign || var12.index == var1.length() && var1.charAt(var12.index - 1) == this.minusSign)) {
                        var11 = -var11;
                        --var12.index;
                     }
                  }
               }

               boolean var15 = this.useDateFormatSymbols();
               int var16;
               byte var17;
               char var18;
               int var19;
               Map var27;
               int var28;
               label291:
               switch(var3) {
               case 0:
                  if (var15) {
                     if ((var16 = this.matchString(var1, var2, 0, (String[])this.formatData.getEras(), var9)) > 0) {
                        return var16;
                     }
                  } else {
                     var27 = this.getDisplayNamesMap(var13, this.locale);
                     if ((var16 = this.matchString(var1, var2, var13, var27, var9)) > 0) {
                        return var16;
                     }
                  }
                  break;
               case 1:
               case 19:
                  if (!(this.calendar instanceof GregorianCalendar)) {
                     var28 = var4 >= 4 ? 2 : 1;
                     Map var26 = this.calendar.getDisplayNames(var13, var28, this.locale);
                     if (var26 != null && (var16 = this.matchString(var1, var2, var13, var26, var9)) > 0) {
                        return var16;
                     }

                     var9.set(var13, var11);
                     return var12.index;
                  }

                  if (var4 <= 2 && var12.index - var24 == 2 && Character.isDigit(var1.charAt(var24)) && Character.isDigit(var1.charAt(var24 + 1))) {
                     var28 = this.defaultCenturyStartYear % 100;
                     var6[0] = var11 == var28;
                     var11 += this.defaultCenturyStartYear / 100 * 100 + (var11 < var28 ? 100 : 0);
                  }

                  var9.set(var13, var11);
                  return var12.index;
               case 2:
                  if (var4 <= 2) {
                     var9.set(2, var11 - 1);
                     return var12.index;
                  }

                  if (var15) {
                     if ((var28 = this.matchString(var1, var2, 2, (String[])this.formatData.getMonths(), var9)) > 0) {
                        return var28;
                     }

                     if ((var16 = this.matchString(var1, var2, 2, (String[])this.formatData.getShortMonths(), var9)) > 0) {
                        return var16;
                     }
                  } else {
                     var27 = this.getDisplayNamesMap(var13, this.locale);
                     if ((var16 = this.matchString(var1, var2, var13, var27, var9)) > 0) {
                        return var16;
                     }
                  }
                  break;
               case 3:
               case 5:
               case 6:
               case 7:
               case 8:
               case 10:
               case 11:
               case 12:
               case 13:
               case 16:
               case 20:
               default:
                  if (var5) {
                     if (var2 + var4 > var1.length()) {
                        break;
                     }

                     var10 = this.numberFormat.parse(var1.substring(0, var2 + var4), var12);
                  } else {
                     var10 = this.numberFormat.parse(var1, var12);
                  }

                  if (var10 != null) {
                     var11 = var10.intValue();
                     if (var8 && var11 < 0 && (var12.index < var1.length() && var1.charAt(var12.index) != this.minusSign || var12.index == var1.length() && var1.charAt(var12.index - 1) == this.minusSign)) {
                        var11 = -var11;
                        --var12.index;
                     }

                     var9.set(var13, var11);
                     return var12.index;
                  }
                  break;
               case 4:
                  if (this.isLenient() || var11 >= 1 && var11 <= 24) {
                     if (var11 == this.calendar.getMaximum(11) + 1) {
                        var11 = 0;
                     }

                     var9.set(11, var11);
                     return var12.index;
                  }
                  break;
               case 9:
                  if (var15) {
                     if ((var28 = this.matchString(var1, var2, 7, (String[])this.formatData.getWeekdays(), var9)) > 0) {
                        return var28;
                     }

                     if ((var16 = this.matchString(var1, var2, 7, (String[])this.formatData.getShortWeekdays(), var9)) > 0) {
                        return var16;
                     }
                     break;
                  } else {
                     int[] var29 = new int[]{2, 1};
                     int[] var25 = var29;
                     var19 = var29.length;
                     int var20 = 0;

                     while(true) {
                        if (var20 >= var19) {
                           break label291;
                        }

                        int var21 = var25[var20];
                        Map var22 = this.calendar.getDisplayNames(var13, var21, this.locale);
                        if ((var16 = this.matchString(var1, var2, var13, var22, var9)) > 0) {
                           return var16;
                        }

                        ++var20;
                     }
                  }
               case 14:
                  if (var15) {
                     if ((var16 = this.matchString(var1, var2, 9, (String[])this.formatData.getAmPmStrings(), var9)) > 0) {
                        return var16;
                     }
                  } else {
                     var27 = this.getDisplayNamesMap(var13, this.locale);
                     if ((var16 = this.matchString(var1, var2, var13, var27, var9)) > 0) {
                        return var16;
                     }
                  }
                  break;
               case 15:
                  if (this.isLenient() || var11 >= 1 && var11 <= 12) {
                     if (var11 == this.calendar.getLeastMaximum(10) + 1) {
                        var11 = 0;
                     }

                     var9.set(10, var11);
                     return var12.index;
                  }
                  break;
               case 17:
               case 18:
                  var17 = 0;

                  try {
                     var18 = var1.charAt(var12.index);
                     if (var18 == '+') {
                        var17 = 1;
                     } else if (var18 == '-') {
                        var17 = -1;
                     }

                     if (var17 == 0) {
                        if ((var18 == 'G' || var18 == 'g') && var1.length() - var2 >= "GMT".length() && var1.regionMatches(true, var2, "GMT", 0, "GMT".length())) {
                           var12.index = var2 + "GMT".length();
                           if (var1.length() - var12.index > 0) {
                              var18 = var1.charAt(var12.index);
                              if (var18 == '+') {
                                 var17 = 1;
                              } else if (var18 == '-') {
                                 var17 = -1;
                              }
                           }

                           if (var17 == 0) {
                              var9.set(15, 0).set(16, 0);
                              return var12.index;
                           }

                           var19 = this.subParseNumericZone(var1, ++var12.index, var17, 0, true, var9);
                           if (var19 > 0) {
                              return var19;
                           }

                           var12.index = -var19;
                        } else {
                           var19 = this.subParseZoneString(var1, var12.index, var9);
                           if (var19 > 0) {
                              return var19;
                           }

                           var12.index = -var19;
                        }
                     } else {
                        var19 = this.subParseNumericZone(var1, ++var12.index, var17, 0, false, var9);
                        if (var19 > 0) {
                           return var19;
                        }

                        var12.index = -var19;
                     }
                  } catch (IndexOutOfBoundsException var23) {
                  }
                  break;
               case 21:
                  if (var1.length() - var12.index > 0) {
                     label343: {
                        var18 = var1.charAt(var12.index);
                        if (var18 == 'Z') {
                           var9.set(15, 0).set(16, 0);
                           return ++var12.index;
                        }

                        if (var18 == '+') {
                           var17 = 1;
                        } else {
                           if (var18 != '-') {
                              ++var12.index;
                              break label343;
                           }

                           var17 = -1;
                        }

                        var19 = this.subParseNumericZone(var1, ++var12.index, var17, var4, var4 == 3, var9);
                        if (var19 > 0) {
                           return var19;
                        }

                        var12.index = -var19;
                     }
                  }
               }
            }

            var7.errorIndex = var12.index;
            return -1;
         }
      }

      var7.errorIndex = var2;
      return -1;
   }

   private boolean useDateFormatSymbols() {
      return this.useDateFormatSymbols || this.locale == null;
   }

   private String translatePattern(String var1, String var2, String var3) {
      StringBuilder var4 = new StringBuilder();
      boolean var5 = false;

      for(int var6 = 0; var6 < var1.length(); ++var6) {
         char var7 = var1.charAt(var6);
         if (var5) {
            if (var7 == '\'') {
               var5 = false;
            }
         } else if (var7 == '\'') {
            var5 = true;
         } else if (var7 >= 'a' && var7 <= 'z' || var7 >= 'A' && var7 <= 'Z') {
            int var8 = var2.indexOf(var7);
            if (var8 < 0) {
               throw new IllegalArgumentException("Illegal pattern  character '" + var7 + "'");
            }

            if (var8 < var3.length()) {
               var7 = var3.charAt(var8);
            }
         }

         var4.append(var7);
      }

      if (var5) {
         throw new IllegalArgumentException("Unfinished quote in pattern");
      } else {
         return var4.toString();
      }
   }

   public String toPattern() {
      return this.pattern;
   }

   public String toLocalizedPattern() {
      return this.translatePattern(this.pattern, "GyMdkHmsSEDFwWahKzZYuXL", this.formatData.getLocalPatternChars());
   }

   public void applyPattern(String var1) {
      this.applyPatternImpl(var1);
   }

   private void applyPatternImpl(String var1) {
      this.compiledPattern = this.compile(var1);
      this.pattern = var1;
   }

   public void applyLocalizedPattern(String var1) {
      String var2 = this.translatePattern(var1, this.formatData.getLocalPatternChars(), "GyMdkHmsSEDFwWahKzZYuXL");
      this.compiledPattern = this.compile(var2);
      this.pattern = var2;
   }

   public DateFormatSymbols getDateFormatSymbols() {
      return (DateFormatSymbols)this.formatData.clone();
   }

   public void setDateFormatSymbols(DateFormatSymbols var1) {
      this.formatData = (DateFormatSymbols)var1.clone();
      this.useDateFormatSymbols = true;
   }

   public Object clone() {
      SimpleDateFormat var1 = (SimpleDateFormat)super.clone();
      var1.formatData = (DateFormatSymbols)this.formatData.clone();
      return var1;
   }

   public int hashCode() {
      return this.pattern.hashCode();
   }

   public boolean equals(Object var1) {
      if (!super.equals(var1)) {
         return false;
      } else {
         SimpleDateFormat var2 = (SimpleDateFormat)var1;
         return this.pattern.equals(var2.pattern) && this.formatData.equals(var2.formatData);
      }
   }

   private Map<String, Integer> getDisplayNamesMap(int var1, Locale var2) {
      Map var3 = this.calendar.getDisplayNames(var1, 1, var2);
      int[] var4 = REST_OF_STYLES;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         int var7 = var4[var6];
         Map var8 = this.calendar.getDisplayNames(var1, var7, var2);
         if (var8 != null) {
            var3.putAll(var8);
         }
      }

      return var3;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();

      try {
         this.compiledPattern = this.compile(this.pattern);
      } catch (Exception var5) {
         throw new InvalidObjectException("invalid pattern");
      }

      if (this.serialVersionOnStream < 1) {
         this.initializeDefaultCentury();
      } else {
         this.parseAmbiguousDatesAsAfter(this.defaultCenturyStart);
      }

      this.serialVersionOnStream = 1;
      TimeZone var2 = this.getTimeZone();
      if (var2 instanceof SimpleTimeZone) {
         String var3 = var2.getID();
         TimeZone var4 = TimeZone.getTimeZone(var3);
         if (var4 != null && var4.hasSameRules(var2) && var4.getID().equals(var3)) {
            this.setTimeZone(var4);
         }
      }

   }

   private void checkNegativeNumberExpression() {
      if (this.numberFormat instanceof DecimalFormat && !this.numberFormat.equals(this.originalNumberFormat)) {
         String var1 = ((DecimalFormat)this.numberFormat).toPattern();
         if (!var1.equals(this.originalNumberPattern)) {
            this.hasFollowingMinusSign = false;
            int var2 = var1.indexOf(59);
            if (var2 > -1) {
               int var3 = var1.indexOf(45, var2);
               if (var3 > var1.lastIndexOf(48) && var3 > var1.lastIndexOf(35)) {
                  this.hasFollowingMinusSign = true;
                  this.minusSign = ((DecimalFormat)this.numberFormat).getDecimalFormatSymbols().getMinusSign();
               }
            }

            this.originalNumberPattern = var1;
         }

         this.originalNumberFormat = this.numberFormat;
      }

   }

   static {
      PATTERN_INDEX_TO_DATE_FORMAT_FIELD_ID = new DateFormat.Field[]{DateFormat.Field.ERA, DateFormat.Field.YEAR, DateFormat.Field.MONTH, DateFormat.Field.DAY_OF_MONTH, DateFormat.Field.HOUR_OF_DAY1, DateFormat.Field.HOUR_OF_DAY0, DateFormat.Field.MINUTE, DateFormat.Field.SECOND, DateFormat.Field.MILLISECOND, DateFormat.Field.DAY_OF_WEEK, DateFormat.Field.DAY_OF_YEAR, DateFormat.Field.DAY_OF_WEEK_IN_MONTH, DateFormat.Field.WEEK_OF_YEAR, DateFormat.Field.WEEK_OF_MONTH, DateFormat.Field.AM_PM, DateFormat.Field.HOUR1, DateFormat.Field.HOUR0, DateFormat.Field.TIME_ZONE, DateFormat.Field.TIME_ZONE, DateFormat.Field.YEAR, DateFormat.Field.DAY_OF_WEEK, DateFormat.Field.TIME_ZONE, DateFormat.Field.MONTH};
      REST_OF_STYLES = new int[]{32769, 2, 32770};
   }
}
