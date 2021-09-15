package java.time.temporal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.chrono.Chronology;

public final class TemporalQueries {
   static final TemporalQuery<ZoneId> ZONE_ID = (var0) -> {
      return (ZoneId)var0.query(ZONE_ID);
   };
   static final TemporalQuery<Chronology> CHRONO = (var0) -> {
      return (Chronology)var0.query(CHRONO);
   };
   static final TemporalQuery<TemporalUnit> PRECISION = (var0) -> {
      return (TemporalUnit)var0.query(PRECISION);
   };
   static final TemporalQuery<ZoneOffset> OFFSET = (var0) -> {
      return var0.isSupported(ChronoField.OFFSET_SECONDS) ? ZoneOffset.ofTotalSeconds(var0.get(ChronoField.OFFSET_SECONDS)) : null;
   };
   static final TemporalQuery<ZoneId> ZONE = (var0) -> {
      ZoneId var1 = (ZoneId)var0.query(ZONE_ID);
      return var1 != null ? var1 : (ZoneId)var0.query(OFFSET);
   };
   static final TemporalQuery<LocalDate> LOCAL_DATE = (var0) -> {
      return var0.isSupported(ChronoField.EPOCH_DAY) ? LocalDate.ofEpochDay(var0.getLong(ChronoField.EPOCH_DAY)) : null;
   };
   static final TemporalQuery<LocalTime> LOCAL_TIME = (var0) -> {
      return var0.isSupported(ChronoField.NANO_OF_DAY) ? LocalTime.ofNanoOfDay(var0.getLong(ChronoField.NANO_OF_DAY)) : null;
   };

   private TemporalQueries() {
   }

   public static TemporalQuery<ZoneId> zoneId() {
      return ZONE_ID;
   }

   public static TemporalQuery<Chronology> chronology() {
      return CHRONO;
   }

   public static TemporalQuery<TemporalUnit> precision() {
      return PRECISION;
   }

   public static TemporalQuery<ZoneId> zone() {
      return ZONE;
   }

   public static TemporalQuery<ZoneOffset> offset() {
      return OFFSET;
   }

   public static TemporalQuery<LocalDate> localDate() {
      return LOCAL_DATE;
   }

   public static TemporalQuery<LocalTime> localTime() {
      return LOCAL_TIME;
   }
}
