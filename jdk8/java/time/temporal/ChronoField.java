package java.time.temporal;

import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleResources;

public enum ChronoField implements TemporalField {
   NANO_OF_SECOND("NanoOfSecond", ChronoUnit.NANOS, ChronoUnit.SECONDS, ValueRange.of(0L, 999999999L)),
   NANO_OF_DAY("NanoOfDay", ChronoUnit.NANOS, ChronoUnit.DAYS, ValueRange.of(0L, 86399999999999L)),
   MICRO_OF_SECOND("MicroOfSecond", ChronoUnit.MICROS, ChronoUnit.SECONDS, ValueRange.of(0L, 999999L)),
   MICRO_OF_DAY("MicroOfDay", ChronoUnit.MICROS, ChronoUnit.DAYS, ValueRange.of(0L, 86399999999L)),
   MILLI_OF_SECOND("MilliOfSecond", ChronoUnit.MILLIS, ChronoUnit.SECONDS, ValueRange.of(0L, 999L)),
   MILLI_OF_DAY("MilliOfDay", ChronoUnit.MILLIS, ChronoUnit.DAYS, ValueRange.of(0L, 86399999L)),
   SECOND_OF_MINUTE("SecondOfMinute", ChronoUnit.SECONDS, ChronoUnit.MINUTES, ValueRange.of(0L, 59L), "second"),
   SECOND_OF_DAY("SecondOfDay", ChronoUnit.SECONDS, ChronoUnit.DAYS, ValueRange.of(0L, 86399L)),
   MINUTE_OF_HOUR("MinuteOfHour", ChronoUnit.MINUTES, ChronoUnit.HOURS, ValueRange.of(0L, 59L), "minute"),
   MINUTE_OF_DAY("MinuteOfDay", ChronoUnit.MINUTES, ChronoUnit.DAYS, ValueRange.of(0L, 1439L)),
   HOUR_OF_AMPM("HourOfAmPm", ChronoUnit.HOURS, ChronoUnit.HALF_DAYS, ValueRange.of(0L, 11L)),
   CLOCK_HOUR_OF_AMPM("ClockHourOfAmPm", ChronoUnit.HOURS, ChronoUnit.HALF_DAYS, ValueRange.of(1L, 12L)),
   HOUR_OF_DAY("HourOfDay", ChronoUnit.HOURS, ChronoUnit.DAYS, ValueRange.of(0L, 23L), "hour"),
   CLOCK_HOUR_OF_DAY("ClockHourOfDay", ChronoUnit.HOURS, ChronoUnit.DAYS, ValueRange.of(1L, 24L)),
   AMPM_OF_DAY("AmPmOfDay", ChronoUnit.HALF_DAYS, ChronoUnit.DAYS, ValueRange.of(0L, 1L), "dayperiod"),
   DAY_OF_WEEK("DayOfWeek", ChronoUnit.DAYS, ChronoUnit.WEEKS, ValueRange.of(1L, 7L), "weekday"),
   ALIGNED_DAY_OF_WEEK_IN_MONTH("AlignedDayOfWeekInMonth", ChronoUnit.DAYS, ChronoUnit.WEEKS, ValueRange.of(1L, 7L)),
   ALIGNED_DAY_OF_WEEK_IN_YEAR("AlignedDayOfWeekInYear", ChronoUnit.DAYS, ChronoUnit.WEEKS, ValueRange.of(1L, 7L)),
   DAY_OF_MONTH("DayOfMonth", ChronoUnit.DAYS, ChronoUnit.MONTHS, ValueRange.of(1L, 28L, 31L), "day"),
   DAY_OF_YEAR("DayOfYear", ChronoUnit.DAYS, ChronoUnit.YEARS, ValueRange.of(1L, 365L, 366L)),
   EPOCH_DAY("EpochDay", ChronoUnit.DAYS, ChronoUnit.FOREVER, ValueRange.of(-365249999634L, 365249999634L)),
   ALIGNED_WEEK_OF_MONTH("AlignedWeekOfMonth", ChronoUnit.WEEKS, ChronoUnit.MONTHS, ValueRange.of(1L, 4L, 5L)),
   ALIGNED_WEEK_OF_YEAR("AlignedWeekOfYear", ChronoUnit.WEEKS, ChronoUnit.YEARS, ValueRange.of(1L, 53L)),
   MONTH_OF_YEAR("MonthOfYear", ChronoUnit.MONTHS, ChronoUnit.YEARS, ValueRange.of(1L, 12L), "month"),
   PROLEPTIC_MONTH("ProlepticMonth", ChronoUnit.MONTHS, ChronoUnit.FOREVER, ValueRange.of(-11999999988L, 11999999999L)),
   YEAR_OF_ERA("YearOfEra", ChronoUnit.YEARS, ChronoUnit.FOREVER, ValueRange.of(1L, 999999999L, 1000000000L)),
   YEAR("Year", ChronoUnit.YEARS, ChronoUnit.FOREVER, ValueRange.of(-999999999L, 999999999L), "year"),
   ERA("Era", ChronoUnit.ERAS, ChronoUnit.FOREVER, ValueRange.of(0L, 1L), "era"),
   INSTANT_SECONDS("InstantSeconds", ChronoUnit.SECONDS, ChronoUnit.FOREVER, ValueRange.of(Long.MIN_VALUE, Long.MAX_VALUE)),
   OFFSET_SECONDS("OffsetSeconds", ChronoUnit.SECONDS, ChronoUnit.FOREVER, ValueRange.of(-64800L, 64800L));

   private final String name;
   private final TemporalUnit baseUnit;
   private final TemporalUnit rangeUnit;
   private final ValueRange range;
   private final String displayNameKey;

   private ChronoField(String var3, TemporalUnit var4, TemporalUnit var5, ValueRange var6) {
      this.name = var3;
      this.baseUnit = var4;
      this.rangeUnit = var5;
      this.range = var6;
      this.displayNameKey = null;
   }

   private ChronoField(String var3, TemporalUnit var4, TemporalUnit var5, ValueRange var6, String var7) {
      this.name = var3;
      this.baseUnit = var4;
      this.rangeUnit = var5;
      this.range = var6;
      this.displayNameKey = var7;
   }

   public String getDisplayName(Locale var1) {
      Objects.requireNonNull(var1, (String)"locale");
      if (this.displayNameKey == null) {
         return this.name;
      } else {
         LocaleResources var2 = LocaleProviderAdapter.getResourceBundleBased().getLocaleResources(var1);
         ResourceBundle var3 = var2.getJavaTimeFormatData();
         String var4 = "field." + this.displayNameKey;
         return var3.containsKey(var4) ? var3.getString(var4) : this.name;
      }
   }

   public TemporalUnit getBaseUnit() {
      return this.baseUnit;
   }

   public TemporalUnit getRangeUnit() {
      return this.rangeUnit;
   }

   public ValueRange range() {
      return this.range;
   }

   public boolean isDateBased() {
      return this.ordinal() >= DAY_OF_WEEK.ordinal() && this.ordinal() <= ERA.ordinal();
   }

   public boolean isTimeBased() {
      return this.ordinal() < DAY_OF_WEEK.ordinal();
   }

   public long checkValidValue(long var1) {
      return this.range().checkValidValue(var1, this);
   }

   public int checkValidIntValue(long var1) {
      return this.range().checkValidIntValue(var1, this);
   }

   public boolean isSupportedBy(TemporalAccessor var1) {
      return var1.isSupported(this);
   }

   public ValueRange rangeRefinedBy(TemporalAccessor var1) {
      return var1.range(this);
   }

   public long getFrom(TemporalAccessor var1) {
      return var1.getLong(this);
   }

   public <R extends Temporal> R adjustInto(R var1, long var2) {
      return var1.with(this, var2);
   }

   public String toString() {
      return this.name;
   }
}
