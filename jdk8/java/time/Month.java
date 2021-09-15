package java.time;

import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
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

public enum Month implements TemporalAccessor, TemporalAdjuster {
   JANUARY,
   FEBRUARY,
   MARCH,
   APRIL,
   MAY,
   JUNE,
   JULY,
   AUGUST,
   SEPTEMBER,
   OCTOBER,
   NOVEMBER,
   DECEMBER;

   private static final Month[] ENUMS = values();

   public static Month of(int var0) {
      if (var0 >= 1 && var0 <= 12) {
         return ENUMS[var0 - 1];
      } else {
         throw new DateTimeException("Invalid value for MonthOfYear: " + var0);
      }
   }

   public static Month from(TemporalAccessor var0) {
      if (var0 instanceof Month) {
         return (Month)var0;
      } else {
         try {
            if (!IsoChronology.INSTANCE.equals(Chronology.from((TemporalAccessor)var0))) {
               var0 = LocalDate.from((TemporalAccessor)var0);
            }

            return of(((TemporalAccessor)var0).get(ChronoField.MONTH_OF_YEAR));
         } catch (DateTimeException var2) {
            throw new DateTimeException("Unable to obtain Month from TemporalAccessor: " + var0 + " of type " + var0.getClass().getName(), var2);
         }
      }
   }

   public int getValue() {
      return this.ordinal() + 1;
   }

   public String getDisplayName(TextStyle var1, Locale var2) {
      return (new DateTimeFormatterBuilder()).appendText(ChronoField.MONTH_OF_YEAR, (TextStyle)var1).toFormatter(var2).format(this);
   }

   public boolean isSupported(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         return var1 == ChronoField.MONTH_OF_YEAR;
      } else {
         return var1 != null && var1.isSupportedBy(this);
      }
   }

   public ValueRange range(TemporalField var1) {
      return var1 == ChronoField.MONTH_OF_YEAR ? var1.range() : TemporalAccessor.super.range(var1);
   }

   public int get(TemporalField var1) {
      return var1 == ChronoField.MONTH_OF_YEAR ? this.getValue() : TemporalAccessor.super.get(var1);
   }

   public long getLong(TemporalField var1) {
      if (var1 == ChronoField.MONTH_OF_YEAR) {
         return (long)this.getValue();
      } else if (var1 instanceof ChronoField) {
         throw new UnsupportedTemporalTypeException("Unsupported field: " + var1);
      } else {
         return var1.getFrom(this);
      }
   }

   public Month plus(long var1) {
      int var3 = (int)(var1 % 12L);
      return ENUMS[(this.ordinal() + var3 + 12) % 12];
   }

   public Month minus(long var1) {
      return this.plus(-(var1 % 12L));
   }

   public int length(boolean var1) {
      switch(this) {
      case FEBRUARY:
         return var1 ? 29 : 28;
      case APRIL:
      case JUNE:
      case SEPTEMBER:
      case NOVEMBER:
         return 30;
      default:
         return 31;
      }
   }

   public int minLength() {
      switch(this) {
      case FEBRUARY:
         return 28;
      case APRIL:
      case JUNE:
      case SEPTEMBER:
      case NOVEMBER:
         return 30;
      default:
         return 31;
      }
   }

   public int maxLength() {
      switch(this) {
      case FEBRUARY:
         return 29;
      case APRIL:
      case JUNE:
      case SEPTEMBER:
      case NOVEMBER:
         return 30;
      default:
         return 31;
      }
   }

   public int firstDayOfYear(boolean var1) {
      int var2 = var1 ? 1 : 0;
      switch(this) {
      case FEBRUARY:
         return 32;
      case APRIL:
         return 91 + var2;
      case JUNE:
         return 152 + var2;
      case SEPTEMBER:
         return 244 + var2;
      case NOVEMBER:
         return 305 + var2;
      case JANUARY:
         return 1;
      case MARCH:
         return 60 + var2;
      case MAY:
         return 121 + var2;
      case JULY:
         return 182 + var2;
      case AUGUST:
         return 213 + var2;
      case OCTOBER:
         return 274 + var2;
      case DECEMBER:
      default:
         return 335 + var2;
      }
   }

   public Month firstMonthOfQuarter() {
      return ENUMS[this.ordinal() / 3 * 3];
   }

   public <R> R query(TemporalQuery<R> var1) {
      if (var1 == TemporalQueries.chronology()) {
         return IsoChronology.INSTANCE;
      } else {
         return var1 == TemporalQueries.precision() ? ChronoUnit.MONTHS : TemporalAccessor.super.query(var1);
      }
   }

   public Temporal adjustInto(Temporal var1) {
      if (!Chronology.from(var1).equals(IsoChronology.INSTANCE)) {
         throw new DateTimeException("Adjustment only supported on ISO date-time");
      } else {
         return var1.with(ChronoField.MONTH_OF_YEAR, (long)this.getValue());
      }
   }
}
