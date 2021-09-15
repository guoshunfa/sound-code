package java.time.temporal;

import java.time.Duration;

public enum ChronoUnit implements TemporalUnit {
   NANOS("Nanos", Duration.ofNanos(1L)),
   MICROS("Micros", Duration.ofNanos(1000L)),
   MILLIS("Millis", Duration.ofNanos(1000000L)),
   SECONDS("Seconds", Duration.ofSeconds(1L)),
   MINUTES("Minutes", Duration.ofSeconds(60L)),
   HOURS("Hours", Duration.ofSeconds(3600L)),
   HALF_DAYS("HalfDays", Duration.ofSeconds(43200L)),
   DAYS("Days", Duration.ofSeconds(86400L)),
   WEEKS("Weeks", Duration.ofSeconds(604800L)),
   MONTHS("Months", Duration.ofSeconds(2629746L)),
   YEARS("Years", Duration.ofSeconds(31556952L)),
   DECADES("Decades", Duration.ofSeconds(315569520L)),
   CENTURIES("Centuries", Duration.ofSeconds(3155695200L)),
   MILLENNIA("Millennia", Duration.ofSeconds(31556952000L)),
   ERAS("Eras", Duration.ofSeconds(31556952000000000L)),
   FOREVER("Forever", Duration.ofSeconds(Long.MAX_VALUE, 999999999L));

   private final String name;
   private final Duration duration;

   private ChronoUnit(String var3, Duration var4) {
      this.name = var3;
      this.duration = var4;
   }

   public Duration getDuration() {
      return this.duration;
   }

   public boolean isDurationEstimated() {
      return this.compareTo(DAYS) >= 0;
   }

   public boolean isDateBased() {
      return this.compareTo(DAYS) >= 0 && this != FOREVER;
   }

   public boolean isTimeBased() {
      return this.compareTo(DAYS) < 0;
   }

   public boolean isSupportedBy(Temporal var1) {
      return var1.isSupported(this);
   }

   public <R extends Temporal> R addTo(R var1, long var2) {
      return var1.plus(var2, this);
   }

   public long between(Temporal var1, Temporal var2) {
      return var1.until(var2, this);
   }

   public String toString() {
      return this.name;
   }
}
