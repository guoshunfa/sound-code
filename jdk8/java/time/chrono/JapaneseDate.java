package java.time.chrono;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;
import sun.util.calendar.CalendarDate;
import sun.util.calendar.LocalGregorianCalendar;

public final class JapaneseDate extends ChronoLocalDateImpl<JapaneseDate> implements ChronoLocalDate, Serializable {
   private static final long serialVersionUID = -305327627230580483L;
   private final transient LocalDate isoDate;
   private transient JapaneseEra era;
   private transient int yearOfEra;
   static final LocalDate MEIJI_6_ISODATE = LocalDate.of(1873, 1, 1);

   public static JapaneseDate now() {
      return now(Clock.systemDefaultZone());
   }

   public static JapaneseDate now(ZoneId var0) {
      return now(Clock.system(var0));
   }

   public static JapaneseDate now(Clock var0) {
      return new JapaneseDate(LocalDate.now(var0));
   }

   public static JapaneseDate of(JapaneseEra var0, int var1, int var2, int var3) {
      Objects.requireNonNull(var0, (String)"era");
      LocalGregorianCalendar.Date var4 = JapaneseChronology.JCAL.newCalendarDate((TimeZone)null);
      var4.setEra(var0.getPrivateEra()).setDate(var1, var2, var3);
      if (!JapaneseChronology.JCAL.validate(var4)) {
         throw new DateTimeException("year, month, and day not valid for Era");
      } else {
         LocalDate var5 = LocalDate.of(var4.getNormalizedYear(), var2, var3);
         return new JapaneseDate(var0, var1, var5);
      }
   }

   public static JapaneseDate of(int var0, int var1, int var2) {
      return new JapaneseDate(LocalDate.of(var0, var1, var2));
   }

   static JapaneseDate ofYearDay(JapaneseEra var0, int var1, int var2) {
      Objects.requireNonNull(var0, (String)"era");
      CalendarDate var3 = var0.getPrivateEra().getSinceDate();
      LocalGregorianCalendar.Date var4 = JapaneseChronology.JCAL.newCalendarDate((TimeZone)null);
      var4.setEra(var0.getPrivateEra());
      if (var1 == 1) {
         var4.setDate(var1, var3.getMonth(), var3.getDayOfMonth() + var2 - 1);
      } else {
         var4.setDate(var1, 1, var2);
      }

      JapaneseChronology.JCAL.normalize(var4);
      if (var0.getPrivateEra() == var4.getEra() && var1 == var4.getYear()) {
         LocalDate var5 = LocalDate.of(var4.getNormalizedYear(), var4.getMonth(), var4.getDayOfMonth());
         return new JapaneseDate(var0, var1, var5);
      } else {
         throw new DateTimeException("Invalid parameters");
      }
   }

   public static JapaneseDate from(TemporalAccessor var0) {
      return JapaneseChronology.INSTANCE.date(var0);
   }

   JapaneseDate(LocalDate var1) {
      if (var1.isBefore(MEIJI_6_ISODATE)) {
         throw new DateTimeException("JapaneseDate before Meiji 6 is not supported");
      } else {
         LocalGregorianCalendar.Date var2 = toPrivateJapaneseDate(var1);
         this.era = JapaneseEra.toJapaneseEra(var2.getEra());
         this.yearOfEra = var2.getYear();
         this.isoDate = var1;
      }
   }

   JapaneseDate(JapaneseEra var1, int var2, LocalDate var3) {
      if (var3.isBefore(MEIJI_6_ISODATE)) {
         throw new DateTimeException("JapaneseDate before Meiji 6 is not supported");
      } else {
         this.era = var1;
         this.yearOfEra = var2;
         this.isoDate = var3;
      }
   }

   public JapaneseChronology getChronology() {
      return JapaneseChronology.INSTANCE;
   }

   public JapaneseEra getEra() {
      return this.era;
   }

   public int lengthOfMonth() {
      return this.isoDate.lengthOfMonth();
   }

   public int lengthOfYear() {
      Calendar var1 = Calendar.getInstance(JapaneseChronology.LOCALE);
      var1.set(0, this.era.getValue() + 2);
      var1.set(this.yearOfEra, this.isoDate.getMonthValue() - 1, this.isoDate.getDayOfMonth());
      return var1.getActualMaximum(6);
   }

   public boolean isSupported(TemporalField var1) {
      return var1 != ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH && var1 != ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR && var1 != ChronoField.ALIGNED_WEEK_OF_MONTH && var1 != ChronoField.ALIGNED_WEEK_OF_YEAR ? ChronoLocalDate.super.isSupported(var1) : false;
   }

   public ValueRange range(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         if (this.isSupported(var1)) {
            ChronoField var2 = (ChronoField)var1;
            switch(var2) {
            case DAY_OF_MONTH:
               return ValueRange.of(1L, (long)this.lengthOfMonth());
            case DAY_OF_YEAR:
               return ValueRange.of(1L, (long)this.lengthOfYear());
            case YEAR_OF_ERA:
               Calendar var3 = Calendar.getInstance(JapaneseChronology.LOCALE);
               var3.set(0, this.era.getValue() + 2);
               var3.set(this.yearOfEra, this.isoDate.getMonthValue() - 1, this.isoDate.getDayOfMonth());
               return ValueRange.of(1L, (long)var3.getActualMaximum(1));
            default:
               return this.getChronology().range(var2);
            }
         } else {
            throw new UnsupportedTemporalTypeException("Unsupported field: " + var1);
         }
      } else {
         return var1.rangeRefinedBy(this);
      }
   }

   public long getLong(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         switch((ChronoField)var1) {
         case DAY_OF_YEAR:
            Calendar var2 = Calendar.getInstance(JapaneseChronology.LOCALE);
            var2.set(0, this.era.getValue() + 2);
            var2.set(this.yearOfEra, this.isoDate.getMonthValue() - 1, this.isoDate.getDayOfMonth());
            return (long)var2.get(6);
         case YEAR_OF_ERA:
            return (long)this.yearOfEra;
         case ALIGNED_DAY_OF_WEEK_IN_MONTH:
         case ALIGNED_DAY_OF_WEEK_IN_YEAR:
         case ALIGNED_WEEK_OF_MONTH:
         case ALIGNED_WEEK_OF_YEAR:
            throw new UnsupportedTemporalTypeException("Unsupported field: " + var1);
         case ERA:
            return (long)this.era.getValue();
         default:
            return this.isoDate.getLong(var1);
         }
      } else {
         return var1.getFrom(this);
      }
   }

   private static LocalGregorianCalendar.Date toPrivateJapaneseDate(LocalDate var0) {
      LocalGregorianCalendar.Date var1 = JapaneseChronology.JCAL.newCalendarDate((TimeZone)null);
      sun.util.calendar.Era var2 = JapaneseEra.privateEraFrom(var0);
      int var3 = var0.getYear();
      if (var2 != null) {
         var3 -= var2.getSinceDate().getYear() - 1;
      }

      var1.setEra(var2).setYear(var3).setMonth(var0.getMonthValue()).setDayOfMonth(var0.getDayOfMonth());
      JapaneseChronology.JCAL.normalize(var1);
      return var1;
   }

   public JapaneseDate with(TemporalField var1, long var2) {
      if (var1 instanceof ChronoField) {
         ChronoField var4 = (ChronoField)var1;
         if (this.getLong(var4) == var2) {
            return this;
         } else {
            switch(var4) {
            case YEAR_OF_ERA:
            case ERA:
            case YEAR:
               int var5 = this.getChronology().range(var4).checkValidIntValue(var2, var4);
               switch(var4) {
               case YEAR_OF_ERA:
                  return this.withYear(var5);
               case ERA:
                  return this.withYear(JapaneseEra.of(var5), this.yearOfEra);
               case YEAR:
                  return this.with(this.isoDate.withYear(var5));
               }
            default:
               return this.with(this.isoDate.with(var1, var2));
            }
         }
      } else {
         return (JapaneseDate)super.with(var1, var2);
      }
   }

   public JapaneseDate with(TemporalAdjuster var1) {
      return (JapaneseDate)super.with(var1);
   }

   public JapaneseDate plus(TemporalAmount var1) {
      return (JapaneseDate)super.plus(var1);
   }

   public JapaneseDate minus(TemporalAmount var1) {
      return (JapaneseDate)super.minus(var1);
   }

   private JapaneseDate withYear(JapaneseEra var1, int var2) {
      int var3 = JapaneseChronology.INSTANCE.prolepticYear(var1, var2);
      return this.with(this.isoDate.withYear(var3));
   }

   private JapaneseDate withYear(int var1) {
      return this.withYear(this.getEra(), var1);
   }

   JapaneseDate plusYears(long var1) {
      return this.with(this.isoDate.plusYears(var1));
   }

   JapaneseDate plusMonths(long var1) {
      return this.with(this.isoDate.plusMonths(var1));
   }

   JapaneseDate plusWeeks(long var1) {
      return this.with(this.isoDate.plusWeeks(var1));
   }

   JapaneseDate plusDays(long var1) {
      return this.with(this.isoDate.plusDays(var1));
   }

   public JapaneseDate plus(long var1, TemporalUnit var3) {
      return (JapaneseDate)super.plus(var1, var3);
   }

   public JapaneseDate minus(long var1, TemporalUnit var3) {
      return (JapaneseDate)super.minus(var1, var3);
   }

   JapaneseDate minusYears(long var1) {
      return (JapaneseDate)super.minusYears(var1);
   }

   JapaneseDate minusMonths(long var1) {
      return (JapaneseDate)super.minusMonths(var1);
   }

   JapaneseDate minusWeeks(long var1) {
      return (JapaneseDate)super.minusWeeks(var1);
   }

   JapaneseDate minusDays(long var1) {
      return (JapaneseDate)super.minusDays(var1);
   }

   private JapaneseDate with(LocalDate var1) {
      return var1.equals(this.isoDate) ? this : new JapaneseDate(var1);
   }

   public final ChronoLocalDateTime<JapaneseDate> atTime(LocalTime var1) {
      return super.atTime(var1);
   }

   public ChronoPeriod until(ChronoLocalDate var1) {
      Period var2 = this.isoDate.until(var1);
      return this.getChronology().period(var2.getYears(), var2.getMonths(), var2.getDays());
   }

   public long toEpochDay() {
      return this.isoDate.toEpochDay();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof JapaneseDate) {
         JapaneseDate var2 = (JapaneseDate)var1;
         return this.isoDate.equals(var2.isoDate);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.getChronology().getId().hashCode() ^ this.isoDate.hashCode();
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }

   private Object writeReplace() {
      return new Ser((byte)4, this);
   }

   void writeExternal(DataOutput var1) throws IOException {
      var1.writeInt(this.get(ChronoField.YEAR));
      var1.writeByte(this.get(ChronoField.MONTH_OF_YEAR));
      var1.writeByte(this.get(ChronoField.DAY_OF_MONTH));
   }

   static JapaneseDate readExternal(DataInput var0) throws IOException {
      int var1 = var0.readInt();
      byte var2 = var0.readByte();
      byte var3 = var0.readByte();
      return JapaneseChronology.INSTANCE.date(var1, var2, var3);
   }
}
