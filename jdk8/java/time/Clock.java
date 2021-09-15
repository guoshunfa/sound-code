package java.time;

import java.io.Serializable;
import java.util.Objects;

public abstract class Clock {
   public static Clock systemUTC() {
      return new Clock.SystemClock(ZoneOffset.UTC);
   }

   public static Clock systemDefaultZone() {
      return new Clock.SystemClock(ZoneId.systemDefault());
   }

   public static Clock system(ZoneId var0) {
      Objects.requireNonNull(var0, (String)"zone");
      return new Clock.SystemClock(var0);
   }

   public static Clock tickSeconds(ZoneId var0) {
      return new Clock.TickClock(system(var0), 1000000000L);
   }

   public static Clock tickMinutes(ZoneId var0) {
      return new Clock.TickClock(system(var0), 60000000000L);
   }

   public static Clock tick(Clock var0, Duration var1) {
      Objects.requireNonNull(var0, (String)"baseClock");
      Objects.requireNonNull(var1, (String)"tickDuration");
      if (var1.isNegative()) {
         throw new IllegalArgumentException("Tick duration must not be negative");
      } else {
         long var2 = var1.toNanos();
         if (var2 % 1000000L != 0L && 1000000000L % var2 != 0L) {
            throw new IllegalArgumentException("Invalid tick duration");
         } else {
            return (Clock)(var2 <= 1L ? var0 : new Clock.TickClock(var0, var2));
         }
      }
   }

   public static Clock fixed(Instant var0, ZoneId var1) {
      Objects.requireNonNull(var0, (String)"fixedInstant");
      Objects.requireNonNull(var1, (String)"zone");
      return new Clock.FixedClock(var0, var1);
   }

   public static Clock offset(Clock var0, Duration var1) {
      Objects.requireNonNull(var0, (String)"baseClock");
      Objects.requireNonNull(var1, (String)"offsetDuration");
      return (Clock)(var1.equals(Duration.ZERO) ? var0 : new Clock.OffsetClock(var0, var1));
   }

   protected Clock() {
   }

   public abstract ZoneId getZone();

   public abstract Clock withZone(ZoneId var1);

   public long millis() {
      return this.instant().toEpochMilli();
   }

   public abstract Instant instant();

   public boolean equals(Object var1) {
      return super.equals(var1);
   }

   public int hashCode() {
      return super.hashCode();
   }

   static final class TickClock extends Clock implements Serializable {
      private static final long serialVersionUID = 6504659149906368850L;
      private final Clock baseClock;
      private final long tickNanos;

      TickClock(Clock var1, long var2) {
         this.baseClock = var1;
         this.tickNanos = var2;
      }

      public ZoneId getZone() {
         return this.baseClock.getZone();
      }

      public Clock withZone(ZoneId var1) {
         return var1.equals(this.baseClock.getZone()) ? this : new Clock.TickClock(this.baseClock.withZone(var1), this.tickNanos);
      }

      public long millis() {
         long var1 = this.baseClock.millis();
         return var1 - Math.floorMod(var1, this.tickNanos / 1000000L);
      }

      public Instant instant() {
         if (this.tickNanos % 1000000L == 0L) {
            long var6 = this.baseClock.millis();
            return Instant.ofEpochMilli(var6 - Math.floorMod(var6, this.tickNanos / 1000000L));
         } else {
            Instant var1 = this.baseClock.instant();
            long var2 = (long)var1.getNano();
            long var4 = Math.floorMod(var2, this.tickNanos);
            return var1.minusNanos(var4);
         }
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof Clock.TickClock)) {
            return false;
         } else {
            Clock.TickClock var2 = (Clock.TickClock)var1;
            return this.baseClock.equals(var2.baseClock) && this.tickNanos == var2.tickNanos;
         }
      }

      public int hashCode() {
         return this.baseClock.hashCode() ^ (int)(this.tickNanos ^ this.tickNanos >>> 32);
      }

      public String toString() {
         return "TickClock[" + this.baseClock + "," + Duration.ofNanos(this.tickNanos) + "]";
      }
   }

   static final class OffsetClock extends Clock implements Serializable {
      private static final long serialVersionUID = 2007484719125426256L;
      private final Clock baseClock;
      private final Duration offset;

      OffsetClock(Clock var1, Duration var2) {
         this.baseClock = var1;
         this.offset = var2;
      }

      public ZoneId getZone() {
         return this.baseClock.getZone();
      }

      public Clock withZone(ZoneId var1) {
         return var1.equals(this.baseClock.getZone()) ? this : new Clock.OffsetClock(this.baseClock.withZone(var1), this.offset);
      }

      public long millis() {
         return Math.addExact(this.baseClock.millis(), this.offset.toMillis());
      }

      public Instant instant() {
         return this.baseClock.instant().plus(this.offset);
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof Clock.OffsetClock)) {
            return false;
         } else {
            Clock.OffsetClock var2 = (Clock.OffsetClock)var1;
            return this.baseClock.equals(var2.baseClock) && this.offset.equals(var2.offset);
         }
      }

      public int hashCode() {
         return this.baseClock.hashCode() ^ this.offset.hashCode();
      }

      public String toString() {
         return "OffsetClock[" + this.baseClock + "," + this.offset + "]";
      }
   }

   static final class FixedClock extends Clock implements Serializable {
      private static final long serialVersionUID = 7430389292664866958L;
      private final Instant instant;
      private final ZoneId zone;

      FixedClock(Instant var1, ZoneId var2) {
         this.instant = var1;
         this.zone = var2;
      }

      public ZoneId getZone() {
         return this.zone;
      }

      public Clock withZone(ZoneId var1) {
         return var1.equals(this.zone) ? this : new Clock.FixedClock(this.instant, var1);
      }

      public long millis() {
         return this.instant.toEpochMilli();
      }

      public Instant instant() {
         return this.instant;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof Clock.FixedClock)) {
            return false;
         } else {
            Clock.FixedClock var2 = (Clock.FixedClock)var1;
            return this.instant.equals(var2.instant) && this.zone.equals(var2.zone);
         }
      }

      public int hashCode() {
         return this.instant.hashCode() ^ this.zone.hashCode();
      }

      public String toString() {
         return "FixedClock[" + this.instant + "," + this.zone + "]";
      }
   }

   static final class SystemClock extends Clock implements Serializable {
      private static final long serialVersionUID = 6740630888130243051L;
      private final ZoneId zone;

      SystemClock(ZoneId var1) {
         this.zone = var1;
      }

      public ZoneId getZone() {
         return this.zone;
      }

      public Clock withZone(ZoneId var1) {
         return var1.equals(this.zone) ? this : new Clock.SystemClock(var1);
      }

      public long millis() {
         return System.currentTimeMillis();
      }

      public Instant instant() {
         return Instant.ofEpochMilli(this.millis());
      }

      public boolean equals(Object var1) {
         return var1 instanceof Clock.SystemClock ? this.zone.equals(((Clock.SystemClock)var1).zone) : false;
      }

      public int hashCode() {
         return this.zone.hashCode() + 1;
      }

      public String toString() {
         return "SystemClock[" + this.zone + "]";
      }
   }
}
