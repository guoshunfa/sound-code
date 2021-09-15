package java.time.format;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.ValueRange;
import java.util.Locale;
import java.util.Objects;

final class DateTimePrintContext {
   private TemporalAccessor temporal;
   private DateTimeFormatter formatter;
   private int optional;

   DateTimePrintContext(TemporalAccessor var1, DateTimeFormatter var2) {
      this.temporal = adjust(var1, var2);
      this.formatter = var2;
   }

   private static TemporalAccessor adjust(final TemporalAccessor var0, DateTimeFormatter var1) {
      Chronology var2 = var1.getChronology();
      ZoneId var3 = var1.getZone();
      if (var2 == null && var3 == null) {
         return var0;
      } else {
         Chronology var4 = (Chronology)var0.query(TemporalQueries.chronology());
         ZoneId var5 = (ZoneId)var0.query(TemporalQueries.zoneId());
         if (Objects.equals(var2, var4)) {
            var2 = null;
         }

         if (Objects.equals(var3, var5)) {
            var3 = null;
         }

         if (var2 == null && var3 == null) {
            return var0;
         } else {
            final Chronology var6 = var2 != null ? var2 : var4;
            if (var3 != null) {
               if (var0.isSupported(ChronoField.INSTANT_SECONDS)) {
                  Object var13 = var6 != null ? var6 : IsoChronology.INSTANCE;
                  return ((Chronology)var13).zonedDateTime(Instant.from(var0), var3);
               }

               if (var3.normalized() instanceof ZoneOffset && var0.isSupported(ChronoField.OFFSET_SECONDS) && var0.get(ChronoField.OFFSET_SECONDS) != var3.getRules().getOffset(Instant.EPOCH).getTotalSeconds()) {
                  throw new DateTimeException("Unable to apply override zone '" + var3 + "' because the temporal object being formatted has a different offset but does not represent an instant: " + var0);
               }
            }

            final ZoneId var7 = var3 != null ? var3 : var5;
            final ChronoLocalDate var8;
            if (var2 != null) {
               if (var0.isSupported(ChronoField.EPOCH_DAY)) {
                  var8 = var6.date(var0);
               } else {
                  if (var2 != IsoChronology.INSTANCE || var4 != null) {
                     ChronoField[] var9 = ChronoField.values();
                     int var10 = var9.length;

                     for(int var11 = 0; var11 < var10; ++var11) {
                        ChronoField var12 = var9[var11];
                        if (var12.isDateBased() && var0.isSupported(var12)) {
                           throw new DateTimeException("Unable to apply override chronology '" + var2 + "' because the temporal object being formatted contains date fields but does not represent a whole date: " + var0);
                        }
                     }
                  }

                  var8 = null;
               }
            } else {
               var8 = null;
            }

            return new TemporalAccessor() {
               public boolean isSupported(TemporalField var1) {
                  return var8 != null && var1.isDateBased() ? var8.isSupported(var1) : var0.isSupported(var1);
               }

               public ValueRange range(TemporalField var1) {
                  return var8 != null && var1.isDateBased() ? var8.range(var1) : var0.range(var1);
               }

               public long getLong(TemporalField var1) {
                  return var8 != null && var1.isDateBased() ? var8.getLong(var1) : var0.getLong(var1);
               }

               public <R> R query(TemporalQuery<R> var1) {
                  if (var1 == TemporalQueries.chronology()) {
                     return var6;
                  } else if (var1 == TemporalQueries.zoneId()) {
                     return var7;
                  } else {
                     return var1 == TemporalQueries.precision() ? var0.query(var1) : var1.queryFrom(this);
                  }
               }
            };
         }
      }
   }

   TemporalAccessor getTemporal() {
      return this.temporal;
   }

   Locale getLocale() {
      return this.formatter.getLocale();
   }

   DecimalStyle getDecimalStyle() {
      return this.formatter.getDecimalStyle();
   }

   void startOptional() {
      ++this.optional;
   }

   void endOptional() {
      --this.optional;
   }

   <R> R getValue(TemporalQuery<R> var1) {
      Object var2 = this.temporal.query(var1);
      if (var2 == null && this.optional == 0) {
         throw new DateTimeException("Unable to extract value: " + this.temporal.getClass());
      } else {
         return var2;
      }
   }

   Long getValue(TemporalField var1) {
      try {
         return this.temporal.getLong(var1);
      } catch (DateTimeException var3) {
         if (this.optional > 0) {
            return null;
         } else {
            throw var3;
         }
      }
   }

   public String toString() {
      return this.temporal.toString();
   }
}
