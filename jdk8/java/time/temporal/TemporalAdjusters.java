package java.time.temporal;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Objects;
import java.util.function.UnaryOperator;

public final class TemporalAdjusters {
   private TemporalAdjusters() {
   }

   public static TemporalAdjuster ofDateAdjuster(UnaryOperator<LocalDate> var0) {
      Objects.requireNonNull(var0, (String)"dateBasedAdjuster");
      return (var1) -> {
         LocalDate var2 = LocalDate.from(var1);
         LocalDate var3 = (LocalDate)var0.apply(var2);
         return var1.with(var3);
      };
   }

   public static TemporalAdjuster firstDayOfMonth() {
      return (var0) -> {
         return var0.with(ChronoField.DAY_OF_MONTH, 1L);
      };
   }

   public static TemporalAdjuster lastDayOfMonth() {
      return (var0) -> {
         return var0.with(ChronoField.DAY_OF_MONTH, var0.range(ChronoField.DAY_OF_MONTH).getMaximum());
      };
   }

   public static TemporalAdjuster firstDayOfNextMonth() {
      return (var0) -> {
         return var0.with(ChronoField.DAY_OF_MONTH, 1L).plus(1L, ChronoUnit.MONTHS);
      };
   }

   public static TemporalAdjuster firstDayOfYear() {
      return (var0) -> {
         return var0.with(ChronoField.DAY_OF_YEAR, 1L);
      };
   }

   public static TemporalAdjuster lastDayOfYear() {
      return (var0) -> {
         return var0.with(ChronoField.DAY_OF_YEAR, var0.range(ChronoField.DAY_OF_YEAR).getMaximum());
      };
   }

   public static TemporalAdjuster firstDayOfNextYear() {
      return (var0) -> {
         return var0.with(ChronoField.DAY_OF_YEAR, 1L).plus(1L, ChronoUnit.YEARS);
      };
   }

   public static TemporalAdjuster firstInMonth(DayOfWeek var0) {
      return dayOfWeekInMonth(1, var0);
   }

   public static TemporalAdjuster lastInMonth(DayOfWeek var0) {
      return dayOfWeekInMonth(-1, var0);
   }

   public static TemporalAdjuster dayOfWeekInMonth(int var0, DayOfWeek var1) {
      Objects.requireNonNull(var1, (String)"dayOfWeek");
      int var2 = var1.getValue();
      return var0 >= 0 ? (var2x) -> {
         Temporal var3 = var2x.with(ChronoField.DAY_OF_MONTH, 1L);
         int var4 = var3.get(ChronoField.DAY_OF_WEEK);
         int var5 = (var2 - var4 + 7) % 7;
         var5 = (int)((long)var5 + ((long)var0 - 1L) * 7L);
         return var3.plus((long)var5, ChronoUnit.DAYS);
      } : (var2x) -> {
         Temporal var3 = var2x.with(ChronoField.DAY_OF_MONTH, var2x.range(ChronoField.DAY_OF_MONTH).getMaximum());
         int var4 = var3.get(ChronoField.DAY_OF_WEEK);
         int var5 = var2 - var4;
         var5 = var5 == 0 ? 0 : (var5 > 0 ? var5 - 7 : var5);
         var5 = (int)((long)var5 - ((long)(-var0) - 1L) * 7L);
         return var3.plus((long)var5, ChronoUnit.DAYS);
      };
   }

   public static TemporalAdjuster next(DayOfWeek var0) {
      int var1 = var0.getValue();
      return (var1x) -> {
         int var2 = var1x.get(ChronoField.DAY_OF_WEEK);
         int var3 = var2 - var1;
         return var1x.plus(var3 >= 0 ? (long)(7 - var3) : (long)(-var3), ChronoUnit.DAYS);
      };
   }

   public static TemporalAdjuster nextOrSame(DayOfWeek var0) {
      int var1 = var0.getValue();
      return (var1x) -> {
         int var2 = var1x.get(ChronoField.DAY_OF_WEEK);
         if (var2 == var1) {
            return var1x;
         } else {
            int var3 = var2 - var1;
            return var1x.plus(var3 >= 0 ? (long)(7 - var3) : (long)(-var3), ChronoUnit.DAYS);
         }
      };
   }

   public static TemporalAdjuster previous(DayOfWeek var0) {
      int var1 = var0.getValue();
      return (var1x) -> {
         int var2 = var1x.get(ChronoField.DAY_OF_WEEK);
         int var3 = var1 - var2;
         return var1x.minus(var3 >= 0 ? (long)(7 - var3) : (long)(-var3), ChronoUnit.DAYS);
      };
   }

   public static TemporalAdjuster previousOrSame(DayOfWeek var0) {
      int var1 = var0.getValue();
      return (var1x) -> {
         int var2 = var1x.get(ChronoField.DAY_OF_WEEK);
         if (var2 == var1) {
            return var1x;
         } else {
            int var3 = var1 - var2;
            return var1x.minus(var3 >= 0 ? (long)(7 - var3) : (long)(-var3), ChronoUnit.DAYS);
         }
      };
   }
}
