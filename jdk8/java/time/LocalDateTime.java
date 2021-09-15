package java.time;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ValueRange;
import java.time.zone.ZoneRules;
import java.util.Objects;

public final class LocalDateTime implements Temporal, TemporalAdjuster, ChronoLocalDateTime<LocalDate>, Serializable {
   public static final LocalDateTime MIN;
   public static final LocalDateTime MAX;
   private static final long serialVersionUID = 6207766400415563566L;
   private final LocalDate date;
   private final LocalTime time;

   public static LocalDateTime now() {
      return now(Clock.systemDefaultZone());
   }

   public static LocalDateTime now(ZoneId var0) {
      return now(Clock.system(var0));
   }

   public static LocalDateTime now(Clock var0) {
      Objects.requireNonNull(var0, (String)"clock");
      Instant var1 = var0.instant();
      ZoneOffset var2 = var0.getZone().getRules().getOffset(var1);
      return ofEpochSecond(var1.getEpochSecond(), var1.getNano(), var2);
   }

   public static LocalDateTime of(int var0, Month var1, int var2, int var3, int var4) {
      LocalDate var5 = LocalDate.of(var0, var1, var2);
      LocalTime var6 = LocalTime.of(var3, var4);
      return new LocalDateTime(var5, var6);
   }

   public static LocalDateTime of(int var0, Month var1, int var2, int var3, int var4, int var5) {
      LocalDate var6 = LocalDate.of(var0, var1, var2);
      LocalTime var7 = LocalTime.of(var3, var4, var5);
      return new LocalDateTime(var6, var7);
   }

   public static LocalDateTime of(int var0, Month var1, int var2, int var3, int var4, int var5, int var6) {
      LocalDate var7 = LocalDate.of(var0, var1, var2);
      LocalTime var8 = LocalTime.of(var3, var4, var5, var6);
      return new LocalDateTime(var7, var8);
   }

   public static LocalDateTime of(int var0, int var1, int var2, int var3, int var4) {
      LocalDate var5 = LocalDate.of(var0, var1, var2);
      LocalTime var6 = LocalTime.of(var3, var4);
      return new LocalDateTime(var5, var6);
   }

   public static LocalDateTime of(int var0, int var1, int var2, int var3, int var4, int var5) {
      LocalDate var6 = LocalDate.of(var0, var1, var2);
      LocalTime var7 = LocalTime.of(var3, var4, var5);
      return new LocalDateTime(var6, var7);
   }

   public static LocalDateTime of(int var0, int var1, int var2, int var3, int var4, int var5, int var6) {
      LocalDate var7 = LocalDate.of(var0, var1, var2);
      LocalTime var8 = LocalTime.of(var3, var4, var5, var6);
      return new LocalDateTime(var7, var8);
   }

   public static LocalDateTime of(LocalDate var0, LocalTime var1) {
      Objects.requireNonNull(var0, (String)"date");
      Objects.requireNonNull(var1, (String)"time");
      return new LocalDateTime(var0, var1);
   }

   public static LocalDateTime ofInstant(Instant var0, ZoneId var1) {
      Objects.requireNonNull(var0, (String)"instant");
      Objects.requireNonNull(var1, (String)"zone");
      ZoneRules var2 = var1.getRules();
      ZoneOffset var3 = var2.getOffset(var0);
      return ofEpochSecond(var0.getEpochSecond(), var0.getNano(), var3);
   }

   public static LocalDateTime ofEpochSecond(long var0, int var2, ZoneOffset var3) {
      Objects.requireNonNull(var3, (String)"offset");
      ChronoField.NANO_OF_SECOND.checkValidValue((long)var2);
      long var4 = var0 + (long)var3.getTotalSeconds();
      long var6 = Math.floorDiv(var4, 86400L);
      int var8 = (int)Math.floorMod(var4, 86400L);
      LocalDate var9 = LocalDate.ofEpochDay(var6);
      LocalTime var10 = LocalTime.ofNanoOfDay((long)var8 * 1000000000L + (long)var2);
      return new LocalDateTime(var9, var10);
   }

   public static LocalDateTime from(TemporalAccessor var0) {
      if (var0 instanceof LocalDateTime) {
         return (LocalDateTime)var0;
      } else if (var0 instanceof ZonedDateTime) {
         return ((ZonedDateTime)var0).toLocalDateTime();
      } else if (var0 instanceof OffsetDateTime) {
         return ((OffsetDateTime)var0).toLocalDateTime();
      } else {
         try {
            LocalDate var1 = LocalDate.from(var0);
            LocalTime var2 = LocalTime.from(var0);
            return new LocalDateTime(var1, var2);
         } catch (DateTimeException var3) {
            throw new DateTimeException("Unable to obtain LocalDateTime from TemporalAccessor: " + var0 + " of type " + var0.getClass().getName(), var3);
         }
      }
   }

   public static LocalDateTime parse(CharSequence var0) {
      return parse(var0, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
   }

   public static LocalDateTime parse(CharSequence var0, DateTimeFormatter var1) {
      Objects.requireNonNull(var1, (String)"formatter");
      return (LocalDateTime)var1.parse(var0, LocalDateTime::from);
   }

   private LocalDateTime(LocalDate var1, LocalTime var2) {
      this.date = var1;
      this.time = var2;
   }

   private LocalDateTime with(LocalDate var1, LocalTime var2) {
      return this.date == var1 && this.time == var2 ? this : new LocalDateTime(var1, var2);
   }

   public boolean isSupported(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         ChronoField var2 = (ChronoField)var1;
         return var2.isDateBased() || var2.isTimeBased();
      } else {
         return var1 != null && var1.isSupportedBy(this);
      }
   }

   public boolean isSupported(TemporalUnit var1) {
      return ChronoLocalDateTime.super.isSupported(var1);
   }

   public ValueRange range(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         ChronoField var2 = (ChronoField)var1;
         return var2.isTimeBased() ? this.time.range(var1) : this.date.range(var1);
      } else {
         return var1.rangeRefinedBy(this);
      }
   }

   public int get(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         ChronoField var2 = (ChronoField)var1;
         return var2.isTimeBased() ? this.time.get(var1) : this.date.get(var1);
      } else {
         return ChronoLocalDateTime.super.get(var1);
      }
   }

   public long getLong(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         ChronoField var2 = (ChronoField)var1;
         return var2.isTimeBased() ? this.time.getLong(var1) : this.date.getLong(var1);
      } else {
         return var1.getFrom(this);
      }
   }

   public LocalDate toLocalDate() {
      return this.date;
   }

   public int getYear() {
      return this.date.getYear();
   }

   public int getMonthValue() {
      return this.date.getMonthValue();
   }

   public Month getMonth() {
      return this.date.getMonth();
   }

   public int getDayOfMonth() {
      return this.date.getDayOfMonth();
   }

   public int getDayOfYear() {
      return this.date.getDayOfYear();
   }

   public DayOfWeek getDayOfWeek() {
      return this.date.getDayOfWeek();
   }

   public LocalTime toLocalTime() {
      return this.time;
   }

   public int getHour() {
      return this.time.getHour();
   }

   public int getMinute() {
      return this.time.getMinute();
   }

   public int getSecond() {
      return this.time.getSecond();
   }

   public int getNano() {
      return this.time.getNano();
   }

   public LocalDateTime with(TemporalAdjuster var1) {
      if (var1 instanceof LocalDate) {
         return this.with((LocalDate)var1, this.time);
      } else if (var1 instanceof LocalTime) {
         return this.with(this.date, (LocalTime)var1);
      } else {
         return var1 instanceof LocalDateTime ? (LocalDateTime)var1 : (LocalDateTime)var1.adjustInto(this);
      }
   }

   public LocalDateTime with(TemporalField var1, long var2) {
      if (var1 instanceof ChronoField) {
         ChronoField var4 = (ChronoField)var1;
         return var4.isTimeBased() ? this.with(this.date, this.time.with(var1, var2)) : this.with(this.date.with(var1, var2), this.time);
      } else {
         return (LocalDateTime)var1.adjustInto(this, var2);
      }
   }

   public LocalDateTime withYear(int var1) {
      return this.with(this.date.withYear(var1), this.time);
   }

   public LocalDateTime withMonth(int var1) {
      return this.with(this.date.withMonth(var1), this.time);
   }

   public LocalDateTime withDayOfMonth(int var1) {
      return this.with(this.date.withDayOfMonth(var1), this.time);
   }

   public LocalDateTime withDayOfYear(int var1) {
      return this.with(this.date.withDayOfYear(var1), this.time);
   }

   public LocalDateTime withHour(int var1) {
      LocalTime var2 = this.time.withHour(var1);
      return this.with(this.date, var2);
   }

   public LocalDateTime withMinute(int var1) {
      LocalTime var2 = this.time.withMinute(var1);
      return this.with(this.date, var2);
   }

   public LocalDateTime withSecond(int var1) {
      LocalTime var2 = this.time.withSecond(var1);
      return this.with(this.date, var2);
   }

   public LocalDateTime withNano(int var1) {
      LocalTime var2 = this.time.withNano(var1);
      return this.with(this.date, var2);
   }

   public LocalDateTime truncatedTo(TemporalUnit var1) {
      return this.with(this.date, this.time.truncatedTo(var1));
   }

   public LocalDateTime plus(TemporalAmount var1) {
      if (var1 instanceof Period) {
         Period var2 = (Period)var1;
         return this.with(this.date.plus(var2), this.time);
      } else {
         Objects.requireNonNull(var1, (String)"amountToAdd");
         return (LocalDateTime)var1.addTo(this);
      }
   }

   public LocalDateTime plus(long var1, TemporalUnit var3) {
      if (var3 instanceof ChronoUnit) {
         ChronoUnit var4 = (ChronoUnit)var3;
         switch(var4) {
         case NANOS:
            return this.plusNanos(var1);
         case MICROS:
            return this.plusDays(var1 / 86400000000L).plusNanos(var1 % 86400000000L * 1000L);
         case MILLIS:
            return this.plusDays(var1 / 86400000L).plusNanos(var1 % 86400000L * 1000000L);
         case SECONDS:
            return this.plusSeconds(var1);
         case MINUTES:
            return this.plusMinutes(var1);
         case HOURS:
            return this.plusHours(var1);
         case HALF_DAYS:
            return this.plusDays(var1 / 256L).plusHours(var1 % 256L * 12L);
         default:
            return this.with(this.date.plus(var1, var3), this.time);
         }
      } else {
         return (LocalDateTime)var3.addTo(this, var1);
      }
   }

   public LocalDateTime plusYears(long var1) {
      LocalDate var3 = this.date.plusYears(var1);
      return this.with(var3, this.time);
   }

   public LocalDateTime plusMonths(long var1) {
      LocalDate var3 = this.date.plusMonths(var1);
      return this.with(var3, this.time);
   }

   public LocalDateTime plusWeeks(long var1) {
      LocalDate var3 = this.date.plusWeeks(var1);
      return this.with(var3, this.time);
   }

   public LocalDateTime plusDays(long var1) {
      LocalDate var3 = this.date.plusDays(var1);
      return this.with(var3, this.time);
   }

   public LocalDateTime plusHours(long var1) {
      return this.plusWithOverflow(this.date, var1, 0L, 0L, 0L, 1);
   }

   public LocalDateTime plusMinutes(long var1) {
      return this.plusWithOverflow(this.date, 0L, var1, 0L, 0L, 1);
   }

   public LocalDateTime plusSeconds(long var1) {
      return this.plusWithOverflow(this.date, 0L, 0L, var1, 0L, 1);
   }

   public LocalDateTime plusNanos(long var1) {
      return this.plusWithOverflow(this.date, 0L, 0L, 0L, var1, 1);
   }

   public LocalDateTime minus(TemporalAmount var1) {
      if (var1 instanceof Period) {
         Period var2 = (Period)var1;
         return this.with(this.date.minus(var2), this.time);
      } else {
         Objects.requireNonNull(var1, (String)"amountToSubtract");
         return (LocalDateTime)var1.subtractFrom(this);
      }
   }

   public LocalDateTime minus(long var1, TemporalUnit var3) {
      return var1 == Long.MIN_VALUE ? this.plus(Long.MAX_VALUE, var3).plus(1L, var3) : this.plus(-var1, var3);
   }

   public LocalDateTime minusYears(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusYears(Long.MAX_VALUE).plusYears(1L) : this.plusYears(-var1);
   }

   public LocalDateTime minusMonths(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusMonths(Long.MAX_VALUE).plusMonths(1L) : this.plusMonths(-var1);
   }

   public LocalDateTime minusWeeks(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusWeeks(Long.MAX_VALUE).plusWeeks(1L) : this.plusWeeks(-var1);
   }

   public LocalDateTime minusDays(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusDays(Long.MAX_VALUE).plusDays(1L) : this.plusDays(-var1);
   }

   public LocalDateTime minusHours(long var1) {
      return this.plusWithOverflow(this.date, var1, 0L, 0L, 0L, -1);
   }

   public LocalDateTime minusMinutes(long var1) {
      return this.plusWithOverflow(this.date, 0L, var1, 0L, 0L, -1);
   }

   public LocalDateTime minusSeconds(long var1) {
      return this.plusWithOverflow(this.date, 0L, 0L, var1, 0L, -1);
   }

   public LocalDateTime minusNanos(long var1) {
      return this.plusWithOverflow(this.date, 0L, 0L, 0L, var1, -1);
   }

   private LocalDateTime plusWithOverflow(LocalDate var1, long var2, long var4, long var6, long var8, int var10) {
      if ((var2 | var4 | var6 | var8) == 0L) {
         return this.with(var1, this.time);
      } else {
         long var11 = var8 / 86400000000000L + var6 / 86400L + var4 / 1440L + var2 / 24L;
         var11 *= (long)var10;
         long var13 = var8 % 86400000000000L + var6 % 86400L * 1000000000L + var4 % 1440L * 60000000000L + var2 % 24L * 3600000000000L;
         long var15 = this.time.toNanoOfDay();
         var13 = var13 * (long)var10 + var15;
         var11 += Math.floorDiv(var13, 86400000000000L);
         long var17 = Math.floorMod(var13, 86400000000000L);
         LocalTime var19 = var17 == var15 ? this.time : LocalTime.ofNanoOfDay(var17);
         return this.with(var1.plusDays(var11), var19);
      }
   }

   public <R> R query(TemporalQuery<R> var1) {
      return var1 == TemporalQueries.localDate() ? this.date : ChronoLocalDateTime.super.query(var1);
   }

   public Temporal adjustInto(Temporal var1) {
      return ChronoLocalDateTime.super.adjustInto(var1);
   }

   public long until(Temporal var1, TemporalUnit var2) {
      LocalDateTime var3 = from(var1);
      if (var2 instanceof ChronoUnit) {
         if (var2.isTimeBased()) {
            long var8 = this.date.daysUntil(var3.date);
            if (var8 == 0L) {
               return this.time.until(var3.time, var2);
            } else {
               long var6 = var3.time.toNanoOfDay() - this.time.toNanoOfDay();
               if (var8 > 0L) {
                  --var8;
                  var6 += 86400000000000L;
               } else {
                  ++var8;
                  var6 -= 86400000000000L;
               }

               switch((ChronoUnit)var2) {
               case NANOS:
                  var8 = Math.multiplyExact(var8, 86400000000000L);
                  break;
               case MICROS:
                  var8 = Math.multiplyExact(var8, 86400000000L);
                  var6 /= 1000L;
                  break;
               case MILLIS:
                  var8 = Math.multiplyExact(var8, 86400000L);
                  var6 /= 1000000L;
                  break;
               case SECONDS:
                  var8 = Math.multiplyExact(var8, 86400L);
                  var6 /= 1000000000L;
                  break;
               case MINUTES:
                  var8 = Math.multiplyExact(var8, 1440L);
                  var6 /= 60000000000L;
                  break;
               case HOURS:
                  var8 = Math.multiplyExact(var8, 24L);
                  var6 /= 3600000000000L;
                  break;
               case HALF_DAYS:
                  var8 = Math.multiplyExact(var8, 2L);
                  var6 /= 43200000000000L;
               }

               return Math.addExact(var8, var6);
            }
         } else {
            LocalDate var4 = var3.date;
            if (var4.isAfter(this.date) && var3.time.isBefore(this.time)) {
               var4 = var4.minusDays(1L);
            } else if (var4.isBefore(this.date) && var3.time.isAfter(this.time)) {
               var4 = var4.plusDays(1L);
            }

            return this.date.until(var4, var2);
         }
      } else {
         return var2.between(this, var3);
      }
   }

   public String format(DateTimeFormatter var1) {
      Objects.requireNonNull(var1, (String)"formatter");
      return var1.format(this);
   }

   public OffsetDateTime atOffset(ZoneOffset var1) {
      return OffsetDateTime.of(this, var1);
   }

   public ZonedDateTime atZone(ZoneId var1) {
      return ZonedDateTime.of(this, var1);
   }

   public int compareTo(ChronoLocalDateTime<?> var1) {
      return var1 instanceof LocalDateTime ? this.compareTo0((LocalDateTime)var1) : ChronoLocalDateTime.super.compareTo(var1);
   }

   private int compareTo0(LocalDateTime var1) {
      int var2 = this.date.compareTo0(var1.toLocalDate());
      if (var2 == 0) {
         var2 = this.time.compareTo(var1.toLocalTime());
      }

      return var2;
   }

   public boolean isAfter(ChronoLocalDateTime<?> var1) {
      if (var1 instanceof LocalDateTime) {
         return this.compareTo0((LocalDateTime)var1) > 0;
      } else {
         return ChronoLocalDateTime.super.isAfter(var1);
      }
   }

   public boolean isBefore(ChronoLocalDateTime<?> var1) {
      if (var1 instanceof LocalDateTime) {
         return this.compareTo0((LocalDateTime)var1) < 0;
      } else {
         return ChronoLocalDateTime.super.isBefore(var1);
      }
   }

   public boolean isEqual(ChronoLocalDateTime<?> var1) {
      if (var1 instanceof LocalDateTime) {
         return this.compareTo0((LocalDateTime)var1) == 0;
      } else {
         return ChronoLocalDateTime.super.isEqual(var1);
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof LocalDateTime)) {
         return false;
      } else {
         LocalDateTime var2 = (LocalDateTime)var1;
         return this.date.equals(var2.date) && this.time.equals(var2.time);
      }
   }

   public int hashCode() {
      return this.date.hashCode() ^ this.time.hashCode();
   }

   public String toString() {
      return this.date.toString() + 'T' + this.time.toString();
   }

   private Object writeReplace() {
      return new Ser((byte)5, this);
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }

   void writeExternal(DataOutput var1) throws IOException {
      this.date.writeExternal(var1);
      this.time.writeExternal(var1);
   }

   static LocalDateTime readExternal(DataInput var0) throws IOException {
      LocalDate var1 = LocalDate.readExternal(var0);
      LocalTime var2 = LocalTime.readExternal(var0);
      return of(var1, var2);
   }

   static {
      MIN = of(LocalDate.MIN, LocalTime.MIN);
      MAX = of(LocalDate.MAX, LocalTime.MAX);
   }
}
