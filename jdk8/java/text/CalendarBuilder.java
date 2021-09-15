package java.text;

import java.util.Calendar;

class CalendarBuilder {
   private static final int UNSET = 0;
   private static final int COMPUTED = 1;
   private static final int MINIMUM_USER_STAMP = 2;
   private static final int MAX_FIELD = 18;
   public static final int WEEK_YEAR = 17;
   public static final int ISO_DAY_OF_WEEK = 1000;
   private final int[] field = new int[36];
   private int nextStamp = 2;
   private int maxFieldIndex = -1;

   CalendarBuilder set(int var1, int var2) {
      if (var1 == 1000) {
         var1 = 7;
         var2 = toCalendarDayOfWeek(var2);
      }

      this.field[var1] = this.nextStamp++;
      this.field[18 + var1] = var2;
      if (var1 > this.maxFieldIndex && var1 < 17) {
         this.maxFieldIndex = var1;
      }

      return this;
   }

   CalendarBuilder addYear(int var1) {
      int[] var10000 = this.field;
      var10000[19] += var1;
      var10000 = this.field;
      var10000[35] += var1;
      return this;
   }

   boolean isSet(int var1) {
      if (var1 == 1000) {
         var1 = 7;
      }

      return this.field[var1] > 0;
   }

   CalendarBuilder clear(int var1) {
      if (var1 == 1000) {
         var1 = 7;
      }

      this.field[var1] = 0;
      this.field[18 + var1] = 0;
      return this;
   }

   Calendar establish(Calendar var1) {
      boolean var2 = this.isSet(17) && this.field[17] > this.field[1];
      if (var2 && !var1.isWeekDateSupported()) {
         if (!this.isSet(1)) {
            this.set(1, this.field[35]);
         }

         var2 = false;
      }

      var1.clear();

      int var3;
      int var4;
      for(var3 = 2; var3 < this.nextStamp; ++var3) {
         for(var4 = 0; var4 <= this.maxFieldIndex; ++var4) {
            if (this.field[var4] == var3) {
               var1.set(var4, this.field[18 + var4]);
               break;
            }
         }
      }

      if (var2) {
         var3 = this.isSet(3) ? this.field[21] : 1;
         var4 = this.isSet(7) ? this.field[25] : var1.getFirstDayOfWeek();
         if (!isValidDayOfWeek(var4) && var1.isLenient()) {
            if (var4 >= 8) {
               --var4;
               var3 += var4 / 7;
               var4 = var4 % 7 + 1;
            } else {
               while(var4 <= 0) {
                  var4 += 7;
                  --var3;
               }
            }

            var4 = toCalendarDayOfWeek(var4);
         }

         var1.setWeekDate(this.field[35], var3, var4);
      }

      return var1;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("CalendarBuilder:[");

      int var2;
      for(var2 = 0; var2 < this.field.length; ++var2) {
         if (this.isSet(var2)) {
            var1.append(var2).append('=').append(this.field[18 + var2]).append(',');
         }
      }

      var2 = var1.length() - 1;
      if (var1.charAt(var2) == ',') {
         var1.setLength(var2);
      }

      var1.append(']');
      return var1.toString();
   }

   static int toISODayOfWeek(int var0) {
      return var0 == 1 ? 7 : var0 - 1;
   }

   static int toCalendarDayOfWeek(int var0) {
      if (!isValidDayOfWeek(var0)) {
         return var0;
      } else {
         return var0 == 7 ? 1 : var0 + 1;
      }
   }

   static boolean isValidDayOfWeek(int var0) {
      return var0 > 0 && var0 <= 7;
   }
}
