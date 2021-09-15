package java.time.chrono;

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

public interface Era extends TemporalAccessor, TemporalAdjuster {
   int getValue();

   default boolean isSupported(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         return var1 == ChronoField.ERA;
      } else {
         return var1 != null && var1.isSupportedBy(this);
      }
   }

   default ValueRange range(TemporalField var1) {
      return TemporalAccessor.super.range(var1);
   }

   default int get(TemporalField var1) {
      return var1 == ChronoField.ERA ? this.getValue() : TemporalAccessor.super.get(var1);
   }

   default long getLong(TemporalField var1) {
      if (var1 == ChronoField.ERA) {
         return (long)this.getValue();
      } else if (var1 instanceof ChronoField) {
         throw new UnsupportedTemporalTypeException("Unsupported field: " + var1);
      } else {
         return var1.getFrom(this);
      }
   }

   default <R> R query(TemporalQuery<R> var1) {
      return var1 == TemporalQueries.precision() ? ChronoUnit.ERAS : TemporalAccessor.super.query(var1);
   }

   default Temporal adjustInto(Temporal var1) {
      return var1.with(ChronoField.ERA, (long)this.getValue());
   }

   default String getDisplayName(TextStyle var1, Locale var2) {
      return (new DateTimeFormatterBuilder()).appendText(ChronoField.ERA, (TextStyle)var1).toFormatter(var2).format(this);
   }
}
