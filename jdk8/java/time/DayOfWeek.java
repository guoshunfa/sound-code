package java.time;

import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Locale;

public enum DayOfWeek implements TemporalAccessor, TemporalAdjuster {
   MONDAY,
   TUESDAY,
   WEDNESDAY,
   THURSDAY,
   FRIDAY,
   SATURDAY,
   SUNDAY;

   private static final DayOfWeek[] ENUMS = values();

   public static DayOfWeek of(int var0) {
      if (var0 >= 1 && var0 <= 7) {
         return ENUMS[var0 - 1];
      } else {
         throw new DateTimeException("Invalid value for DayOfWeek: " + var0);
      }
   }

   public static DayOfWeek from(TemporalAccessor var0) {
      if (var0 instanceof DayOfWeek) {
         return (DayOfWeek)var0;
      } else {
         try {
            return of(var0.get(ChronoField.DAY_OF_WEEK));
         } catch (DateTimeException var2) {
            throw new DateTimeException("Unable to obtain DayOfWeek from TemporalAccessor: " + var0 + " of type " + var0.getClass().getName(), var2);
         }
      }
   }

   public int getValue() {
      return this.ordinal() + 1;
   }

   public String getDisplayName(TextStyle var1, Locale var2) {
      return (new DateTimeFormatterBuilder()).appendText(ChronoField.DAY_OF_WEEK, (TextStyle)var1).toFormatter(var2).format(this);
   }

   public boolean isSupported(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         return var1 == ChronoField.DAY_OF_WEEK;
      } else {
         return var1 != null && var1.isSupportedBy(this);
      }
   }

   public ValueRange range(TemporalField var1) {
      return var1 == ChronoField.DAY_OF_WEEK ? var1.range() : TemporalAccessor.super.range(var1);
   }

   public int get(TemporalField var1) {
      return var1 == ChronoField.DAY_OF_WEEK ? this.getValue() : TemporalAccessor.super.get(var1);
   }

   public long getLong(TemporalField var1) {
      if (var1 == ChronoField.DAY_OF_WEEK) {
         return (long)this.getValue();
      } else if (var1 instanceof ChronoField) {
         throw new UnsupportedTemporalTypeException("Unsupported field: " + var1);
      } else {
         return var1.getFrom(this);
      }
   }

   public DayOfWeek plus(long var1) {
      int var3 = (int)(var1 % 7L);
      return ENUMS[(this.ordinal() + var3 + 7) % 7];
   }

   public DayOfWeek minus(long var1) {
      return this.plus(-(var1 % 7L));
   }

   public <R> R query(TemporalQuery<R> var1) {
      return var1 == TemporalQueries.precision() ? ChronoUnit.DAYS : TemporalAccessor.super.query(var1);
   }

   public Temporal adjustInto(Temporal var1) {
      return var1.with(ChronoField.DAY_OF_WEEK, (long)this.getValue());
   }
}
