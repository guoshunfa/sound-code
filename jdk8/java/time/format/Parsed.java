package java.time.format;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.chrono.Chronology;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

final class Parsed implements TemporalAccessor {
   final Map<TemporalField, Long> fieldValues = new HashMap();
   ZoneId zone;
   Chronology chrono;
   boolean leapSecond;
   private ResolverStyle resolverStyle;
   private ChronoLocalDate date;
   private LocalTime time;
   Period excessDays;

   Parsed() {
      this.excessDays = Period.ZERO;
   }

   Parsed copy() {
      Parsed var1 = new Parsed();
      var1.fieldValues.putAll(this.fieldValues);
      var1.zone = this.zone;
      var1.chrono = this.chrono;
      var1.leapSecond = this.leapSecond;
      return var1;
   }

   public boolean isSupported(TemporalField var1) {
      if (this.fieldValues.containsKey(var1) || this.date != null && this.date.isSupported(var1) || this.time != null && this.time.isSupported(var1)) {
         return true;
      } else {
         return var1 != null && !(var1 instanceof ChronoField) && var1.isSupportedBy(this);
      }
   }

   public long getLong(TemporalField var1) {
      Objects.requireNonNull(var1, (String)"field");
      Long var2 = (Long)this.fieldValues.get(var1);
      if (var2 != null) {
         return var2;
      } else if (this.date != null && this.date.isSupported(var1)) {
         return this.date.getLong(var1);
      } else if (this.time != null && this.time.isSupported(var1)) {
         return this.time.getLong(var1);
      } else if (var1 instanceof ChronoField) {
         throw new UnsupportedTemporalTypeException("Unsupported field: " + var1);
      } else {
         return var1.getFrom(this);
      }
   }

   public <R> R query(TemporalQuery<R> var1) {
      if (var1 == TemporalQueries.zoneId()) {
         return this.zone;
      } else if (var1 == TemporalQueries.chronology()) {
         return this.chrono;
      } else if (var1 == TemporalQueries.localDate()) {
         return this.date != null ? LocalDate.from(this.date) : null;
      } else if (var1 == TemporalQueries.localTime()) {
         return this.time;
      } else if (var1 != TemporalQueries.zone() && var1 != TemporalQueries.offset()) {
         return var1 == TemporalQueries.precision() ? null : var1.queryFrom(this);
      } else {
         return var1.queryFrom(this);
      }
   }

   TemporalAccessor resolve(ResolverStyle var1, Set<TemporalField> var2) {
      if (var2 != null) {
         this.fieldValues.keySet().retainAll(var2);
      }

      this.resolverStyle = var1;
      this.resolveFields();
      this.resolveTimeLenient();
      this.crossCheck();
      this.resolvePeriod();
      this.resolveFractional();
      this.resolveInstant();
      return this;
   }

   private void resolveFields() {
      this.resolveInstantFields();
      this.resolveDateFields();
      this.resolveTimeFields();
      if (this.fieldValues.size() > 0) {
         int var1 = 0;

         label57:
         while(true) {
            label55:
            while(true) {
               if (var1 >= 50) {
                  break label57;
               }

               Iterator var2 = this.fieldValues.entrySet().iterator();

               TemporalField var4;
               do {
                  if (!var2.hasNext()) {
                     break label57;
                  }

                  Map.Entry var3 = (Map.Entry)var2.next();
                  var4 = (TemporalField)var3.getKey();
                  Object var5 = var4.resolve(this.fieldValues, this, this.resolverStyle);
                  if (var5 != null) {
                     if (var5 instanceof ChronoZonedDateTime) {
                        ChronoZonedDateTime var6 = (ChronoZonedDateTime)var5;
                        if (this.zone == null) {
                           this.zone = var6.getZone();
                        } else if (!this.zone.equals(var6.getZone())) {
                           throw new DateTimeException("ChronoZonedDateTime must use the effective parsed zone: " + this.zone);
                        }

                        var5 = var6.toLocalDateTime();
                     }

                     if (var5 instanceof ChronoLocalDateTime) {
                        ChronoLocalDateTime var7 = (ChronoLocalDateTime)var5;
                        this.updateCheckConflict(var7.toLocalTime(), Period.ZERO);
                        this.updateCheckConflict(var7.toLocalDate());
                        ++var1;
                     } else if (var5 instanceof ChronoLocalDate) {
                        this.updateCheckConflict((ChronoLocalDate)var5);
                        ++var1;
                     } else {
                        if (!(var5 instanceof LocalTime)) {
                           throw new DateTimeException("Method resolve() can only return ChronoZonedDateTime, ChronoLocalDateTime, ChronoLocalDate or LocalTime");
                        }

                        this.updateCheckConflict((LocalTime)var5, Period.ZERO);
                        ++var1;
                     }
                     continue label55;
                  }
               } while(this.fieldValues.containsKey(var4));

               ++var1;
            }
         }

         if (var1 == 50) {
            throw new DateTimeException("One of the parsed fields has an incorrectly implemented resolve method");
         }

         if (var1 > 0) {
            this.resolveInstantFields();
            this.resolveDateFields();
            this.resolveTimeFields();
         }
      }

   }

   private void updateCheckConflict(TemporalField var1, TemporalField var2, Long var3) {
      Long var4 = (Long)this.fieldValues.put(var2, var3);
      if (var4 != null && var4 != var3) {
         throw new DateTimeException("Conflict found: " + var2 + " " + var4 + " differs from " + var2 + " " + var3 + " while resolving  " + var1);
      }
   }

   private void resolveInstantFields() {
      if (this.fieldValues.containsKey(ChronoField.INSTANT_SECONDS)) {
         if (this.zone != null) {
            this.resolveInstantFields0(this.zone);
         } else {
            Long var1 = (Long)this.fieldValues.get(ChronoField.OFFSET_SECONDS);
            if (var1 != null) {
               ZoneOffset var2 = ZoneOffset.ofTotalSeconds(var1.intValue());
               this.resolveInstantFields0(var2);
            }
         }
      }

   }

   private void resolveInstantFields0(ZoneId var1) {
      Instant var2 = Instant.ofEpochSecond((Long)this.fieldValues.remove(ChronoField.INSTANT_SECONDS));
      ChronoZonedDateTime var3 = this.chrono.zonedDateTime(var2, var1);
      this.updateCheckConflict(var3.toLocalDate());
      this.updateCheckConflict(ChronoField.INSTANT_SECONDS, ChronoField.SECOND_OF_DAY, (long)var3.toLocalTime().toSecondOfDay());
   }

   private void resolveDateFields() {
      this.updateCheckConflict(this.chrono.resolveDate(this.fieldValues, this.resolverStyle));
   }

   private void updateCheckConflict(ChronoLocalDate var1) {
      if (this.date != null) {
         if (var1 != null && !this.date.equals(var1)) {
            throw new DateTimeException("Conflict found: Fields resolved to two different dates: " + this.date + " " + var1);
         }
      } else if (var1 != null) {
         if (!this.chrono.equals(var1.getChronology())) {
            throw new DateTimeException("ChronoLocalDate must use the effective parsed chronology: " + this.chrono);
         }

         this.date = var1;
      }

   }

   private void resolveTimeFields() {
      long var1;
      if (this.fieldValues.containsKey(ChronoField.CLOCK_HOUR_OF_DAY)) {
         var1 = (Long)this.fieldValues.remove(ChronoField.CLOCK_HOUR_OF_DAY);
         if (this.resolverStyle == ResolverStyle.STRICT || this.resolverStyle == ResolverStyle.SMART && var1 != 0L) {
            ChronoField.CLOCK_HOUR_OF_DAY.checkValidValue(var1);
         }

         this.updateCheckConflict(ChronoField.CLOCK_HOUR_OF_DAY, ChronoField.HOUR_OF_DAY, var1 == 24L ? 0L : var1);
      }

      if (this.fieldValues.containsKey(ChronoField.CLOCK_HOUR_OF_AMPM)) {
         var1 = (Long)this.fieldValues.remove(ChronoField.CLOCK_HOUR_OF_AMPM);
         if (this.resolverStyle == ResolverStyle.STRICT || this.resolverStyle == ResolverStyle.SMART && var1 != 0L) {
            ChronoField.CLOCK_HOUR_OF_AMPM.checkValidValue(var1);
         }

         this.updateCheckConflict(ChronoField.CLOCK_HOUR_OF_AMPM, ChronoField.HOUR_OF_AMPM, var1 == 12L ? 0L : var1);
      }

      long var3;
      if (this.fieldValues.containsKey(ChronoField.AMPM_OF_DAY) && this.fieldValues.containsKey(ChronoField.HOUR_OF_AMPM)) {
         var1 = (Long)this.fieldValues.remove(ChronoField.AMPM_OF_DAY);
         var3 = (Long)this.fieldValues.remove(ChronoField.HOUR_OF_AMPM);
         if (this.resolverStyle == ResolverStyle.LENIENT) {
            this.updateCheckConflict(ChronoField.AMPM_OF_DAY, ChronoField.HOUR_OF_DAY, Math.addExact(Math.multiplyExact(var1, 12L), var3));
         } else {
            ChronoField.AMPM_OF_DAY.checkValidValue(var1);
            ChronoField.HOUR_OF_AMPM.checkValidValue(var1);
            this.updateCheckConflict(ChronoField.AMPM_OF_DAY, ChronoField.HOUR_OF_DAY, var1 * 12L + var3);
         }
      }

      if (this.fieldValues.containsKey(ChronoField.NANO_OF_DAY)) {
         var1 = (Long)this.fieldValues.remove(ChronoField.NANO_OF_DAY);
         if (this.resolverStyle != ResolverStyle.LENIENT) {
            ChronoField.NANO_OF_DAY.checkValidValue(var1);
         }

         this.updateCheckConflict(ChronoField.NANO_OF_DAY, ChronoField.HOUR_OF_DAY, var1 / 3600000000000L);
         this.updateCheckConflict(ChronoField.NANO_OF_DAY, ChronoField.MINUTE_OF_HOUR, var1 / 60000000000L % 60L);
         this.updateCheckConflict(ChronoField.NANO_OF_DAY, ChronoField.SECOND_OF_MINUTE, var1 / 1000000000L % 60L);
         this.updateCheckConflict(ChronoField.NANO_OF_DAY, ChronoField.NANO_OF_SECOND, var1 % 1000000000L);
      }

      if (this.fieldValues.containsKey(ChronoField.MICRO_OF_DAY)) {
         var1 = (Long)this.fieldValues.remove(ChronoField.MICRO_OF_DAY);
         if (this.resolverStyle != ResolverStyle.LENIENT) {
            ChronoField.MICRO_OF_DAY.checkValidValue(var1);
         }

         this.updateCheckConflict(ChronoField.MICRO_OF_DAY, ChronoField.SECOND_OF_DAY, var1 / 1000000L);
         this.updateCheckConflict(ChronoField.MICRO_OF_DAY, ChronoField.MICRO_OF_SECOND, var1 % 1000000L);
      }

      if (this.fieldValues.containsKey(ChronoField.MILLI_OF_DAY)) {
         var1 = (Long)this.fieldValues.remove(ChronoField.MILLI_OF_DAY);
         if (this.resolverStyle != ResolverStyle.LENIENT) {
            ChronoField.MILLI_OF_DAY.checkValidValue(var1);
         }

         this.updateCheckConflict(ChronoField.MILLI_OF_DAY, ChronoField.SECOND_OF_DAY, var1 / 1000L);
         this.updateCheckConflict(ChronoField.MILLI_OF_DAY, ChronoField.MILLI_OF_SECOND, var1 % 1000L);
      }

      if (this.fieldValues.containsKey(ChronoField.SECOND_OF_DAY)) {
         var1 = (Long)this.fieldValues.remove(ChronoField.SECOND_OF_DAY);
         if (this.resolverStyle != ResolverStyle.LENIENT) {
            ChronoField.SECOND_OF_DAY.checkValidValue(var1);
         }

         this.updateCheckConflict(ChronoField.SECOND_OF_DAY, ChronoField.HOUR_OF_DAY, var1 / 3600L);
         this.updateCheckConflict(ChronoField.SECOND_OF_DAY, ChronoField.MINUTE_OF_HOUR, var1 / 60L % 60L);
         this.updateCheckConflict(ChronoField.SECOND_OF_DAY, ChronoField.SECOND_OF_MINUTE, var1 % 60L);
      }

      if (this.fieldValues.containsKey(ChronoField.MINUTE_OF_DAY)) {
         var1 = (Long)this.fieldValues.remove(ChronoField.MINUTE_OF_DAY);
         if (this.resolverStyle != ResolverStyle.LENIENT) {
            ChronoField.MINUTE_OF_DAY.checkValidValue(var1);
         }

         this.updateCheckConflict(ChronoField.MINUTE_OF_DAY, ChronoField.HOUR_OF_DAY, var1 / 60L);
         this.updateCheckConflict(ChronoField.MINUTE_OF_DAY, ChronoField.MINUTE_OF_HOUR, var1 % 60L);
      }

      if (this.fieldValues.containsKey(ChronoField.NANO_OF_SECOND)) {
         var1 = (Long)this.fieldValues.get(ChronoField.NANO_OF_SECOND);
         if (this.resolverStyle != ResolverStyle.LENIENT) {
            ChronoField.NANO_OF_SECOND.checkValidValue(var1);
         }

         if (this.fieldValues.containsKey(ChronoField.MICRO_OF_SECOND)) {
            var3 = (Long)this.fieldValues.remove(ChronoField.MICRO_OF_SECOND);
            if (this.resolverStyle != ResolverStyle.LENIENT) {
               ChronoField.MICRO_OF_SECOND.checkValidValue(var3);
            }

            var1 = var3 * 1000L + var1 % 1000L;
            this.updateCheckConflict(ChronoField.MICRO_OF_SECOND, ChronoField.NANO_OF_SECOND, var1);
         }

         if (this.fieldValues.containsKey(ChronoField.MILLI_OF_SECOND)) {
            var3 = (Long)this.fieldValues.remove(ChronoField.MILLI_OF_SECOND);
            if (this.resolverStyle != ResolverStyle.LENIENT) {
               ChronoField.MILLI_OF_SECOND.checkValidValue(var3);
            }

            this.updateCheckConflict(ChronoField.MILLI_OF_SECOND, ChronoField.NANO_OF_SECOND, var3 * 1000000L + var1 % 1000000L);
         }
      }

      if (this.fieldValues.containsKey(ChronoField.HOUR_OF_DAY) && this.fieldValues.containsKey(ChronoField.MINUTE_OF_HOUR) && this.fieldValues.containsKey(ChronoField.SECOND_OF_MINUTE) && this.fieldValues.containsKey(ChronoField.NANO_OF_SECOND)) {
         var1 = (Long)this.fieldValues.remove(ChronoField.HOUR_OF_DAY);
         var3 = (Long)this.fieldValues.remove(ChronoField.MINUTE_OF_HOUR);
         long var5 = (Long)this.fieldValues.remove(ChronoField.SECOND_OF_MINUTE);
         long var7 = (Long)this.fieldValues.remove(ChronoField.NANO_OF_SECOND);
         this.resolveTime(var1, var3, var5, var7);
      }

   }

   private void resolveTimeLenient() {
      if (this.time == null) {
         long var1;
         if (this.fieldValues.containsKey(ChronoField.MILLI_OF_SECOND)) {
            var1 = (Long)this.fieldValues.remove(ChronoField.MILLI_OF_SECOND);
            if (this.fieldValues.containsKey(ChronoField.MICRO_OF_SECOND)) {
               long var3 = var1 * 1000L + (Long)this.fieldValues.get(ChronoField.MICRO_OF_SECOND) % 1000L;
               this.updateCheckConflict(ChronoField.MILLI_OF_SECOND, ChronoField.MICRO_OF_SECOND, var3);
               this.fieldValues.remove(ChronoField.MICRO_OF_SECOND);
               this.fieldValues.put(ChronoField.NANO_OF_SECOND, var3 * 1000L);
            } else {
               this.fieldValues.put(ChronoField.NANO_OF_SECOND, var1 * 1000000L);
            }
         } else if (this.fieldValues.containsKey(ChronoField.MICRO_OF_SECOND)) {
            var1 = (Long)this.fieldValues.remove(ChronoField.MICRO_OF_SECOND);
            this.fieldValues.put(ChronoField.NANO_OF_SECOND, var1 * 1000L);
         }

         Long var11 = (Long)this.fieldValues.get(ChronoField.HOUR_OF_DAY);
         if (var11 != null) {
            Long var2 = (Long)this.fieldValues.get(ChronoField.MINUTE_OF_HOUR);
            Long var14 = (Long)this.fieldValues.get(ChronoField.SECOND_OF_MINUTE);
            Long var4 = (Long)this.fieldValues.get(ChronoField.NANO_OF_SECOND);
            if (var2 == null && (var14 != null || var4 != null) || var2 != null && var14 == null && var4 != null) {
               return;
            }

            long var5 = var2 != null ? var2 : 0L;
            long var7 = var14 != null ? var14 : 0L;
            long var9 = var4 != null ? var4 : 0L;
            this.resolveTime(var11, var5, var7, var9);
            this.fieldValues.remove(ChronoField.HOUR_OF_DAY);
            this.fieldValues.remove(ChronoField.MINUTE_OF_HOUR);
            this.fieldValues.remove(ChronoField.SECOND_OF_MINUTE);
            this.fieldValues.remove(ChronoField.NANO_OF_SECOND);
         }
      }

      if (this.resolverStyle != ResolverStyle.LENIENT && this.fieldValues.size() > 0) {
         Iterator var12 = this.fieldValues.entrySet().iterator();

         while(var12.hasNext()) {
            Map.Entry var13 = (Map.Entry)var12.next();
            TemporalField var15 = (TemporalField)var13.getKey();
            if (var15 instanceof ChronoField && var15.isTimeBased()) {
               ((ChronoField)var15).checkValidValue((Long)var13.getValue());
            }
         }
      }

   }

   private void resolveTime(long var1, long var3, long var5, long var7) {
      int var11;
      if (this.resolverStyle == ResolverStyle.LENIENT) {
         long var9 = Math.multiplyExact(var1, 3600000000000L);
         var9 = Math.addExact(var9, Math.multiplyExact(var3, 60000000000L));
         var9 = Math.addExact(var9, Math.multiplyExact(var5, 1000000000L));
         var9 = Math.addExact(var9, var7);
         var11 = (int)Math.floorDiv(var9, 86400000000000L);
         long var12 = Math.floorMod(var9, 86400000000000L);
         this.updateCheckConflict(LocalTime.ofNanoOfDay(var12), Period.ofDays(var11));
      } else {
         int var14 = ChronoField.MINUTE_OF_HOUR.checkValidIntValue(var3);
         int var10 = ChronoField.NANO_OF_SECOND.checkValidIntValue(var7);
         if (this.resolverStyle == ResolverStyle.SMART && var1 == 24L && var14 == 0 && var5 == 0L && var10 == 0) {
            this.updateCheckConflict(LocalTime.MIDNIGHT, Period.ofDays(1));
         } else {
            var11 = ChronoField.HOUR_OF_DAY.checkValidIntValue(var1);
            int var15 = ChronoField.SECOND_OF_MINUTE.checkValidIntValue(var5);
            this.updateCheckConflict(LocalTime.of(var11, var14, var15, var10), Period.ZERO);
         }
      }

   }

   private void resolvePeriod() {
      if (this.date != null && this.time != null && !this.excessDays.isZero()) {
         this.date = this.date.plus(this.excessDays);
         this.excessDays = Period.ZERO;
      }

   }

   private void resolveFractional() {
      if (this.time == null && (this.fieldValues.containsKey(ChronoField.INSTANT_SECONDS) || this.fieldValues.containsKey(ChronoField.SECOND_OF_DAY) || this.fieldValues.containsKey(ChronoField.SECOND_OF_MINUTE))) {
         if (this.fieldValues.containsKey(ChronoField.NANO_OF_SECOND)) {
            long var1 = (Long)this.fieldValues.get(ChronoField.NANO_OF_SECOND);
            this.fieldValues.put(ChronoField.MICRO_OF_SECOND, var1 / 1000L);
            this.fieldValues.put(ChronoField.MILLI_OF_SECOND, var1 / 1000000L);
         } else {
            this.fieldValues.put(ChronoField.NANO_OF_SECOND, 0L);
            this.fieldValues.put(ChronoField.MICRO_OF_SECOND, 0L);
            this.fieldValues.put(ChronoField.MILLI_OF_SECOND, 0L);
         }
      }

   }

   private void resolveInstant() {
      if (this.date != null && this.time != null) {
         if (this.zone != null) {
            long var1 = this.date.atTime(this.time).atZone(this.zone).getLong(ChronoField.INSTANT_SECONDS);
            this.fieldValues.put(ChronoField.INSTANT_SECONDS, var1);
         } else {
            Long var5 = (Long)this.fieldValues.get(ChronoField.OFFSET_SECONDS);
            if (var5 != null) {
               ZoneOffset var2 = ZoneOffset.ofTotalSeconds(var5.intValue());
               long var3 = this.date.atTime(this.time).atZone(var2).getLong(ChronoField.INSTANT_SECONDS);
               this.fieldValues.put(ChronoField.INSTANT_SECONDS, var3);
            }
         }
      }

   }

   private void updateCheckConflict(LocalTime var1, Period var2) {
      if (this.time != null) {
         if (!this.time.equals(var1)) {
            throw new DateTimeException("Conflict found: Fields resolved to different times: " + this.time + " " + var1);
         }

         if (!this.excessDays.isZero() && !var2.isZero() && !this.excessDays.equals(var2)) {
            throw new DateTimeException("Conflict found: Fields resolved to different excess periods: " + this.excessDays + " " + var2);
         }

         this.excessDays = var2;
      } else {
         this.time = var1;
         this.excessDays = var2;
      }

   }

   private void crossCheck() {
      if (this.date != null) {
         this.crossCheck(this.date);
      }

      if (this.time != null) {
         this.crossCheck(this.time);
         if (this.date != null && this.fieldValues.size() > 0) {
            this.crossCheck(this.date.atTime(this.time));
         }
      }

   }

   private void crossCheck(TemporalAccessor var1) {
      Iterator var2 = this.fieldValues.entrySet().iterator();

      while(true) {
         Map.Entry var3;
         TemporalField var4;
         do {
            if (!var2.hasNext()) {
               return;
            }

            var3 = (Map.Entry)var2.next();
            var4 = (TemporalField)var3.getKey();
         } while(!var1.isSupported(var4));

         long var5;
         try {
            var5 = var1.getLong(var4);
         } catch (RuntimeException var9) {
            continue;
         }

         long var7 = (Long)var3.getValue();
         if (var5 != var7) {
            throw new DateTimeException("Conflict found: Field " + var4 + " " + var5 + " differs from " + var4 + " " + var7 + " derived from " + var1);
         }

         var2.remove();
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(64);
      var1.append((Object)this.fieldValues).append(',').append((Object)this.chrono);
      if (this.zone != null) {
         var1.append(',').append((Object)this.zone);
      }

      if (this.date != null || this.time != null) {
         var1.append(" resolved to ");
         if (this.date != null) {
            var1.append((Object)this.date);
            if (this.time != null) {
               var1.append('T').append((Object)this.time);
            }
         } else {
            var1.append((Object)this.time);
         }
      }

      return var1.toString();
   }
}
