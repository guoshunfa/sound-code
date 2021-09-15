package java.time.chrono;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class IsoChronology extends AbstractChronology implements Serializable {
   public static final IsoChronology INSTANCE = new IsoChronology();
   private static final long serialVersionUID = -1440403870442975015L;

   private IsoChronology() {
   }

   public String getId() {
      return "ISO";
   }

   public String getCalendarType() {
      return "iso8601";
   }

   public LocalDate date(Era var1, int var2, int var3, int var4) {
      return this.date(this.prolepticYear(var1, var2), var3, var4);
   }

   public LocalDate date(int var1, int var2, int var3) {
      return LocalDate.of(var1, var2, var3);
   }

   public LocalDate dateYearDay(Era var1, int var2, int var3) {
      return this.dateYearDay(this.prolepticYear(var1, var2), var3);
   }

   public LocalDate dateYearDay(int var1, int var2) {
      return LocalDate.ofYearDay(var1, var2);
   }

   public LocalDate dateEpochDay(long var1) {
      return LocalDate.ofEpochDay(var1);
   }

   public LocalDate date(TemporalAccessor var1) {
      return LocalDate.from(var1);
   }

   public LocalDateTime localDateTime(TemporalAccessor var1) {
      return LocalDateTime.from(var1);
   }

   public ZonedDateTime zonedDateTime(TemporalAccessor var1) {
      return ZonedDateTime.from(var1);
   }

   public ZonedDateTime zonedDateTime(Instant var1, ZoneId var2) {
      return ZonedDateTime.ofInstant(var1, var2);
   }

   public LocalDate dateNow() {
      return this.dateNow(Clock.systemDefaultZone());
   }

   public LocalDate dateNow(ZoneId var1) {
      return this.dateNow(Clock.system(var1));
   }

   public LocalDate dateNow(Clock var1) {
      Objects.requireNonNull(var1, (String)"clock");
      return this.date(LocalDate.now(var1));
   }

   public boolean isLeapYear(long var1) {
      return (var1 & 3L) == 0L && (var1 % 100L != 0L || var1 % 400L == 0L);
   }

   public int prolepticYear(Era var1, int var2) {
      if (!(var1 instanceof IsoEra)) {
         throw new ClassCastException("Era must be IsoEra");
      } else {
         return var1 == IsoEra.CE ? var2 : 1 - var2;
      }
   }

   public IsoEra eraOf(int var1) {
      return IsoEra.of(var1);
   }

   public List<Era> eras() {
      return Arrays.asList(IsoEra.values());
   }

   public LocalDate resolveDate(Map<TemporalField, Long> var1, ResolverStyle var2) {
      return (LocalDate)super.resolveDate(var1, var2);
   }

   void resolveProlepticMonth(Map<TemporalField, Long> var1, ResolverStyle var2) {
      Long var3 = (Long)var1.remove(ChronoField.PROLEPTIC_MONTH);
      if (var3 != null) {
         if (var2 != ResolverStyle.LENIENT) {
            ChronoField.PROLEPTIC_MONTH.checkValidValue(var3);
         }

         this.addFieldValue(var1, ChronoField.MONTH_OF_YEAR, Math.floorMod(var3, 12L) + 1L);
         this.addFieldValue(var1, ChronoField.YEAR, Math.floorDiv(var3, 12L));
      }

   }

   LocalDate resolveYearOfEra(Map<TemporalField, Long> var1, ResolverStyle var2) {
      Long var3 = (Long)var1.remove(ChronoField.YEAR_OF_ERA);
      if (var3 != null) {
         if (var2 != ResolverStyle.LENIENT) {
            ChronoField.YEAR_OF_ERA.checkValidValue(var3);
         }

         Long var4 = (Long)var1.remove(ChronoField.ERA);
         if (var4 == null) {
            Long var5 = (Long)var1.get(ChronoField.YEAR);
            if (var2 == ResolverStyle.STRICT) {
               if (var5 != null) {
                  this.addFieldValue(var1, ChronoField.YEAR, var5 > 0L ? var3 : Math.subtractExact(1L, var3));
               } else {
                  var1.put(ChronoField.YEAR_OF_ERA, var3);
               }
            } else {
               this.addFieldValue(var1, ChronoField.YEAR, var5 != null && var5 <= 0L ? Math.subtractExact(1L, var3) : var3);
            }
         } else if (var4 == 1L) {
            this.addFieldValue(var1, ChronoField.YEAR, var3);
         } else {
            if (var4 != 0L) {
               throw new DateTimeException("Invalid value for era: " + var4);
            }

            this.addFieldValue(var1, ChronoField.YEAR, Math.subtractExact(1L, var3));
         }
      } else if (var1.containsKey(ChronoField.ERA)) {
         ChronoField.ERA.checkValidValue((Long)var1.get(ChronoField.ERA));
      }

      return null;
   }

   LocalDate resolveYMD(Map<TemporalField, Long> var1, ResolverStyle var2) {
      int var3 = ChronoField.YEAR.checkValidIntValue((Long)var1.remove(ChronoField.YEAR));
      if (var2 == ResolverStyle.LENIENT) {
         long var8 = Math.subtractExact((Long)var1.remove(ChronoField.MONTH_OF_YEAR), 1L);
         long var6 = Math.subtractExact((Long)var1.remove(ChronoField.DAY_OF_MONTH), 1L);
         return LocalDate.of(var3, 1, 1).plusMonths(var8).plusDays(var6);
      } else {
         int var4 = ChronoField.MONTH_OF_YEAR.checkValidIntValue((Long)var1.remove(ChronoField.MONTH_OF_YEAR));
         int var5 = ChronoField.DAY_OF_MONTH.checkValidIntValue((Long)var1.remove(ChronoField.DAY_OF_MONTH));
         if (var2 == ResolverStyle.SMART) {
            if (var4 != 4 && var4 != 6 && var4 != 9 && var4 != 11) {
               if (var4 == 2) {
                  var5 = Math.min(var5, Month.FEBRUARY.length(Year.isLeap((long)var3)));
               }
            } else {
               var5 = Math.min(var5, 30);
            }
         }

         return LocalDate.of(var3, var4, var5);
      }
   }

   public ValueRange range(ChronoField var1) {
      return var1.range();
   }

   public Period period(int var1, int var2, int var3) {
      return Period.of(var1, var2, var3);
   }

   Object writeReplace() {
      return super.writeReplace();
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }
}
