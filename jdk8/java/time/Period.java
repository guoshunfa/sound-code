package java.time;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.chrono.ChronoPeriod;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Period implements ChronoPeriod, Serializable {
   public static final Period ZERO = new Period(0, 0, 0);
   private static final long serialVersionUID = -3587258372562876L;
   private static final Pattern PATTERN = Pattern.compile("([-+]?)P(?:([-+]?[0-9]+)Y)?(?:([-+]?[0-9]+)M)?(?:([-+]?[0-9]+)W)?(?:([-+]?[0-9]+)D)?", 2);
   private static final List<TemporalUnit> SUPPORTED_UNITS;
   private final int years;
   private final int months;
   private final int days;

   public static Period ofYears(int var0) {
      return create(var0, 0, 0);
   }

   public static Period ofMonths(int var0) {
      return create(0, var0, 0);
   }

   public static Period ofWeeks(int var0) {
      return create(0, 0, Math.multiplyExact(var0, 7));
   }

   public static Period ofDays(int var0) {
      return create(0, 0, var0);
   }

   public static Period of(int var0, int var1, int var2) {
      return create(var0, var1, var2);
   }

   public static Period from(TemporalAmount var0) {
      if (var0 instanceof Period) {
         return (Period)var0;
      } else if (var0 instanceof ChronoPeriod && !IsoChronology.INSTANCE.equals(((ChronoPeriod)var0).getChronology())) {
         throw new DateTimeException("Period requires ISO chronology: " + var0);
      } else {
         Objects.requireNonNull(var0, (String)"amount");
         int var1 = 0;
         int var2 = 0;
         int var3 = 0;
         Iterator var4 = var0.getUnits().iterator();

         while(var4.hasNext()) {
            TemporalUnit var5 = (TemporalUnit)var4.next();
            long var6 = var0.get(var5);
            if (var5 == ChronoUnit.YEARS) {
               var1 = Math.toIntExact(var6);
            } else if (var5 == ChronoUnit.MONTHS) {
               var2 = Math.toIntExact(var6);
            } else {
               if (var5 != ChronoUnit.DAYS) {
                  throw new DateTimeException("Unit must be Years, Months or Days, but was " + var5);
               }

               var3 = Math.toIntExact(var6);
            }
         }

         return create(var1, var2, var3);
      }
   }

   public static Period parse(CharSequence var0) {
      Objects.requireNonNull(var0, (String)"text");
      Matcher var1 = PATTERN.matcher(var0);
      if (var1.matches()) {
         int var2 = "-".equals(var1.group(1)) ? -1 : 1;
         String var3 = var1.group(2);
         String var4 = var1.group(3);
         String var5 = var1.group(4);
         String var6 = var1.group(5);
         if (var3 != null || var4 != null || var6 != null || var5 != null) {
            try {
               int var7 = parseNumber(var0, var3, var2);
               int var8 = parseNumber(var0, var4, var2);
               int var9 = parseNumber(var0, var5, var2);
               int var10 = parseNumber(var0, var6, var2);
               var10 = Math.addExact(var10, Math.multiplyExact(var9, 7));
               return create(var7, var8, var10);
            } catch (NumberFormatException var11) {
               throw new DateTimeParseException("Text cannot be parsed to a Period", var0, 0, var11);
            }
         }
      }

      throw new DateTimeParseException("Text cannot be parsed to a Period", var0, 0);
   }

   private static int parseNumber(CharSequence var0, String var1, int var2) {
      if (var1 == null) {
         return 0;
      } else {
         int var3 = Integer.parseInt(var1);

         try {
            return Math.multiplyExact(var3, var2);
         } catch (ArithmeticException var5) {
            throw new DateTimeParseException("Text cannot be parsed to a Period", var0, 0, var5);
         }
      }
   }

   public static Period between(LocalDate var0, LocalDate var1) {
      return var0.until(var1);
   }

   private static Period create(int var0, int var1, int var2) {
      return (var0 | var1 | var2) == 0 ? ZERO : new Period(var0, var1, var2);
   }

   private Period(int var1, int var2, int var3) {
      this.years = var1;
      this.months = var2;
      this.days = var3;
   }

   public long get(TemporalUnit var1) {
      if (var1 == ChronoUnit.YEARS) {
         return (long)this.getYears();
      } else if (var1 == ChronoUnit.MONTHS) {
         return (long)this.getMonths();
      } else if (var1 == ChronoUnit.DAYS) {
         return (long)this.getDays();
      } else {
         throw new UnsupportedTemporalTypeException("Unsupported unit: " + var1);
      }
   }

   public List<TemporalUnit> getUnits() {
      return SUPPORTED_UNITS;
   }

   public IsoChronology getChronology() {
      return IsoChronology.INSTANCE;
   }

   public boolean isZero() {
      return this == ZERO;
   }

   public boolean isNegative() {
      return this.years < 0 || this.months < 0 || this.days < 0;
   }

   public int getYears() {
      return this.years;
   }

   public int getMonths() {
      return this.months;
   }

   public int getDays() {
      return this.days;
   }

   public Period withYears(int var1) {
      return var1 == this.years ? this : create(var1, this.months, this.days);
   }

   public Period withMonths(int var1) {
      return var1 == this.months ? this : create(this.years, var1, this.days);
   }

   public Period withDays(int var1) {
      return var1 == this.days ? this : create(this.years, this.months, var1);
   }

   public Period plus(TemporalAmount var1) {
      Period var2 = from(var1);
      return create(Math.addExact(this.years, var2.years), Math.addExact(this.months, var2.months), Math.addExact(this.days, var2.days));
   }

   public Period plusYears(long var1) {
      return var1 == 0L ? this : create(Math.toIntExact(Math.addExact((long)this.years, var1)), this.months, this.days);
   }

   public Period plusMonths(long var1) {
      return var1 == 0L ? this : create(this.years, Math.toIntExact(Math.addExact((long)this.months, var1)), this.days);
   }

   public Period plusDays(long var1) {
      return var1 == 0L ? this : create(this.years, this.months, Math.toIntExact(Math.addExact((long)this.days, var1)));
   }

   public Period minus(TemporalAmount var1) {
      Period var2 = from(var1);
      return create(Math.subtractExact(this.years, var2.years), Math.subtractExact(this.months, var2.months), Math.subtractExact(this.days, var2.days));
   }

   public Period minusYears(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusYears(Long.MAX_VALUE).plusYears(1L) : this.plusYears(-var1);
   }

   public Period minusMonths(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusMonths(Long.MAX_VALUE).plusMonths(1L) : this.plusMonths(-var1);
   }

   public Period minusDays(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusDays(Long.MAX_VALUE).plusDays(1L) : this.plusDays(-var1);
   }

   public Period multipliedBy(int var1) {
      return this != ZERO && var1 != 1 ? create(Math.multiplyExact(this.years, var1), Math.multiplyExact(this.months, var1), Math.multiplyExact(this.days, var1)) : this;
   }

   public Period negated() {
      return this.multipliedBy(-1);
   }

   public Period normalized() {
      long var1 = this.toTotalMonths();
      long var3 = var1 / 12L;
      int var5 = (int)(var1 % 12L);
      return var3 == (long)this.years && var5 == this.months ? this : create(Math.toIntExact(var3), var5, this.days);
   }

   public long toTotalMonths() {
      return (long)this.years * 12L + (long)this.months;
   }

   public Temporal addTo(Temporal var1) {
      this.validateChrono(var1);
      if (this.months == 0) {
         if (this.years != 0) {
            var1 = var1.plus((long)this.years, ChronoUnit.YEARS);
         }
      } else {
         long var2 = this.toTotalMonths();
         if (var2 != 0L) {
            var1 = var1.plus(var2, ChronoUnit.MONTHS);
         }
      }

      if (this.days != 0) {
         var1 = var1.plus((long)this.days, ChronoUnit.DAYS);
      }

      return var1;
   }

   public Temporal subtractFrom(Temporal var1) {
      this.validateChrono(var1);
      if (this.months == 0) {
         if (this.years != 0) {
            var1 = var1.minus((long)this.years, ChronoUnit.YEARS);
         }
      } else {
         long var2 = this.toTotalMonths();
         if (var2 != 0L) {
            var1 = var1.minus(var2, ChronoUnit.MONTHS);
         }
      }

      if (this.days != 0) {
         var1 = var1.minus((long)this.days, ChronoUnit.DAYS);
      }

      return var1;
   }

   private void validateChrono(TemporalAccessor var1) {
      Objects.requireNonNull(var1, (String)"temporal");
      Chronology var2 = (Chronology)var1.query(TemporalQueries.chronology());
      if (var2 != null && !IsoChronology.INSTANCE.equals(var2)) {
         throw new DateTimeException("Chronology mismatch, expected: ISO, actual: " + var2.getId());
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Period)) {
         return false;
      } else {
         Period var2 = (Period)var1;
         return this.years == var2.years && this.months == var2.months && this.days == var2.days;
      }
   }

   public int hashCode() {
      return this.years + Integer.rotateLeft(this.months, 8) + Integer.rotateLeft(this.days, 16);
   }

   public String toString() {
      if (this == ZERO) {
         return "P0D";
      } else {
         StringBuilder var1 = new StringBuilder();
         var1.append('P');
         if (this.years != 0) {
            var1.append(this.years).append('Y');
         }

         if (this.months != 0) {
            var1.append(this.months).append('M');
         }

         if (this.days != 0) {
            var1.append(this.days).append('D');
         }

         return var1.toString();
      }
   }

   private Object writeReplace() {
      return new Ser((byte)14, this);
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }

   void writeExternal(DataOutput var1) throws IOException {
      var1.writeInt(this.years);
      var1.writeInt(this.months);
      var1.writeInt(this.days);
   }

   static Period readExternal(DataInput var0) throws IOException {
      int var1 = var0.readInt();
      int var2 = var0.readInt();
      int var3 = var0.readInt();
      return of(var1, var2, var3);
   }

   static {
      SUPPORTED_UNITS = Collections.unmodifiableList(Arrays.asList(ChronoUnit.YEARS, ChronoUnit.MONTHS, ChronoUnit.DAYS));
   }
}
