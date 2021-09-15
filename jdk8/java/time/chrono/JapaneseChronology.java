package java.time.chrono;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import sun.util.calendar.CalendarSystem;
import sun.util.calendar.LocalGregorianCalendar;

public final class JapaneseChronology extends AbstractChronology implements Serializable {
   static final LocalGregorianCalendar JCAL = (LocalGregorianCalendar)CalendarSystem.forName("japanese");
   static final Locale LOCALE = Locale.forLanguageTag("ja-JP-u-ca-japanese");
   public static final JapaneseChronology INSTANCE = new JapaneseChronology();
   private static final long serialVersionUID = 459996390165777884L;

   private JapaneseChronology() {
   }

   public String getId() {
      return "Japanese";
   }

   public String getCalendarType() {
      return "japanese";
   }

   public JapaneseDate date(Era var1, int var2, int var3, int var4) {
      if (!(var1 instanceof JapaneseEra)) {
         throw new ClassCastException("Era must be JapaneseEra");
      } else {
         return JapaneseDate.of((JapaneseEra)var1, var2, var3, var4);
      }
   }

   public JapaneseDate date(int var1, int var2, int var3) {
      return new JapaneseDate(LocalDate.of(var1, var2, var3));
   }

   public JapaneseDate dateYearDay(Era var1, int var2, int var3) {
      return JapaneseDate.ofYearDay((JapaneseEra)var1, var2, var3);
   }

   public JapaneseDate dateYearDay(int var1, int var2) {
      return new JapaneseDate(LocalDate.ofYearDay(var1, var2));
   }

   public JapaneseDate dateEpochDay(long var1) {
      return new JapaneseDate(LocalDate.ofEpochDay(var1));
   }

   public JapaneseDate dateNow() {
      return this.dateNow(Clock.systemDefaultZone());
   }

   public JapaneseDate dateNow(ZoneId var1) {
      return this.dateNow(Clock.system(var1));
   }

   public JapaneseDate dateNow(Clock var1) {
      return this.date(LocalDate.now(var1));
   }

   public JapaneseDate date(TemporalAccessor var1) {
      return var1 instanceof JapaneseDate ? (JapaneseDate)var1 : new JapaneseDate(LocalDate.from(var1));
   }

   public ChronoLocalDateTime<JapaneseDate> localDateTime(TemporalAccessor var1) {
      return super.localDateTime(var1);
   }

   public ChronoZonedDateTime<JapaneseDate> zonedDateTime(TemporalAccessor var1) {
      return super.zonedDateTime(var1);
   }

   public ChronoZonedDateTime<JapaneseDate> zonedDateTime(Instant var1, ZoneId var2) {
      return super.zonedDateTime(var1, var2);
   }

   public boolean isLeapYear(long var1) {
      return IsoChronology.INSTANCE.isLeapYear(var1);
   }

   public int prolepticYear(Era var1, int var2) {
      if (!(var1 instanceof JapaneseEra)) {
         throw new ClassCastException("Era must be JapaneseEra");
      } else {
         JapaneseEra var3 = (JapaneseEra)var1;
         int var4 = var3.getPrivateEra().getSinceDate().getYear() + var2 - 1;
         if (var2 == 1) {
            return var4;
         } else {
            if (var4 >= -999999999 && var4 <= 999999999) {
               LocalGregorianCalendar.Date var5 = JCAL.newCalendarDate((TimeZone)null);
               var5.setEra(var3.getPrivateEra()).setDate(var2, 1, 1);
               if (JCAL.validate(var5)) {
                  return var4;
               }
            }

            throw new DateTimeException("Invalid yearOfEra value");
         }
      }
   }

   public JapaneseEra eraOf(int var1) {
      return JapaneseEra.of(var1);
   }

   public List<Era> eras() {
      return Arrays.asList(JapaneseEra.values());
   }

   JapaneseEra getCurrentEra() {
      JapaneseEra[] var1 = JapaneseEra.values();
      return var1[var1.length - 1];
   }

   public ValueRange range(ChronoField var1) {
      Calendar var2;
      switch(var1) {
      case ALIGNED_DAY_OF_WEEK_IN_MONTH:
      case ALIGNED_DAY_OF_WEEK_IN_YEAR:
      case ALIGNED_WEEK_OF_MONTH:
      case ALIGNED_WEEK_OF_YEAR:
         throw new UnsupportedTemporalTypeException("Unsupported field: " + var1);
      case YEAR_OF_ERA:
         var2 = Calendar.getInstance(LOCALE);
         int var4 = this.getCurrentEra().getPrivateEra().getSinceDate().getYear();
         return ValueRange.of(1L, (long)var2.getGreatestMinimum(1), (long)(var2.getLeastMaximum(1) + 1), (long)(999999999 - var4));
      case DAY_OF_YEAR:
         var2 = Calendar.getInstance(LOCALE);
         byte var3 = 6;
         return ValueRange.of((long)var2.getMinimum(var3), (long)var2.getGreatestMinimum(var3), (long)var2.getLeastMaximum(var3), (long)var2.getMaximum(var3));
      case YEAR:
         return ValueRange.of((long)JapaneseDate.MEIJI_6_ISODATE.getYear(), 999999999L);
      case ERA:
         return ValueRange.of((long)JapaneseEra.MEIJI.getValue(), (long)this.getCurrentEra().getValue());
      default:
         return var1.range();
      }
   }

   public JapaneseDate resolveDate(Map<TemporalField, Long> var1, ResolverStyle var2) {
      return (JapaneseDate)super.resolveDate(var1, var2);
   }

   ChronoLocalDate resolveYearOfEra(Map<TemporalField, Long> var1, ResolverStyle var2) {
      Long var3 = (Long)var1.get(ChronoField.ERA);
      JapaneseEra var4 = null;
      if (var3 != null) {
         var4 = this.eraOf(this.range(ChronoField.ERA).checkValidIntValue(var3, ChronoField.ERA));
      }

      Long var5 = (Long)var1.get(ChronoField.YEAR_OF_ERA);
      int var6 = 0;
      if (var5 != null) {
         var6 = this.range(ChronoField.YEAR_OF_ERA).checkValidIntValue(var5, ChronoField.YEAR_OF_ERA);
      }

      if (var4 == null && var5 != null && !var1.containsKey(ChronoField.YEAR) && var2 != ResolverStyle.STRICT) {
         var4 = JapaneseEra.values()[JapaneseEra.values().length - 1];
      }

      if (var5 != null && var4 != null) {
         if (var1.containsKey(ChronoField.MONTH_OF_YEAR) && var1.containsKey(ChronoField.DAY_OF_MONTH)) {
            return this.resolveYMD(var4, var6, var1, var2);
         }

         if (var1.containsKey(ChronoField.DAY_OF_YEAR)) {
            return this.resolveYD(var4, var6, var1, var2);
         }
      }

      return null;
   }

   private int prolepticYearLenient(JapaneseEra var1, int var2) {
      return var1.getPrivateEra().getSinceDate().getYear() + var2 - 1;
   }

   private ChronoLocalDate resolveYMD(JapaneseEra var1, int var2, Map<TemporalField, Long> var3, ResolverStyle var4) {
      var3.remove(ChronoField.ERA);
      var3.remove(ChronoField.YEAR_OF_ERA);
      int var5;
      if (var4 == ResolverStyle.LENIENT) {
         var5 = this.prolepticYearLenient(var1, var2);
         long var11 = Math.subtractExact((Long)var3.remove(ChronoField.MONTH_OF_YEAR), 1L);
         long var12 = Math.subtractExact((Long)var3.remove(ChronoField.DAY_OF_MONTH), 1L);
         return this.date(var5, 1, 1).plus(var11, ChronoUnit.MONTHS).plus(var12, ChronoUnit.DAYS);
      } else {
         var5 = this.range(ChronoField.MONTH_OF_YEAR).checkValidIntValue((Long)var3.remove(ChronoField.MONTH_OF_YEAR), ChronoField.MONTH_OF_YEAR);
         int var6 = this.range(ChronoField.DAY_OF_MONTH).checkValidIntValue((Long)var3.remove(ChronoField.DAY_OF_MONTH), ChronoField.DAY_OF_MONTH);
         if (var4 == ResolverStyle.SMART) {
            if (var2 < 1) {
               throw new DateTimeException("Invalid YearOfEra: " + var2);
            } else {
               int var7 = this.prolepticYearLenient(var1, var2);

               JapaneseDate var8;
               try {
                  var8 = this.date(var7, var5, var6);
               } catch (DateTimeException var10) {
                  var8 = this.date(var7, var5, 1).with(TemporalAdjusters.lastDayOfMonth());
               }

               if (var8.getEra() != var1 && var8.get(ChronoField.YEAR_OF_ERA) > 1 && var2 > 1) {
                  throw new DateTimeException("Invalid YearOfEra for Era: " + var1 + " " + var2);
               } else {
                  return var8;
               }
            }
         } else {
            return this.date(var1, var2, var5, var6);
         }
      }
   }

   private ChronoLocalDate resolveYD(JapaneseEra var1, int var2, Map<TemporalField, Long> var3, ResolverStyle var4) {
      var3.remove(ChronoField.ERA);
      var3.remove(ChronoField.YEAR_OF_ERA);
      int var5;
      if (var4 == ResolverStyle.LENIENT) {
         var5 = this.prolepticYearLenient(var1, var2);
         long var6 = Math.subtractExact((Long)var3.remove(ChronoField.DAY_OF_YEAR), 1L);
         return this.dateYearDay(var5, 1).plus(var6, ChronoUnit.DAYS);
      } else {
         var5 = this.range(ChronoField.DAY_OF_YEAR).checkValidIntValue((Long)var3.remove(ChronoField.DAY_OF_YEAR), ChronoField.DAY_OF_YEAR);
         return this.dateYearDay(var1, var2, var5);
      }
   }

   Object writeReplace() {
      return super.writeReplace();
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }
}
