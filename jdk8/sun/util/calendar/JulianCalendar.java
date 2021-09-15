package sun.util.calendar;

import java.util.TimeZone;

public class JulianCalendar extends BaseCalendar {
   private static final int BCE = 0;
   private static final int CE = 1;
   private static final Era[] eras = new Era[]{new Era("BeforeCommonEra", "B.C.E.", Long.MIN_VALUE, false), new Era("CommonEra", "C.E.", -62135709175808L, true)};
   private static final int JULIAN_EPOCH = -1;

   JulianCalendar() {
      this.setEras(eras);
   }

   public String getName() {
      return "julian";
   }

   public JulianCalendar.Date getCalendarDate() {
      return this.getCalendarDate(System.currentTimeMillis(), (CalendarDate)this.newCalendarDate());
   }

   public JulianCalendar.Date getCalendarDate(long var1) {
      return this.getCalendarDate(var1, (CalendarDate)this.newCalendarDate());
   }

   public JulianCalendar.Date getCalendarDate(long var1, CalendarDate var3) {
      return (JulianCalendar.Date)super.getCalendarDate(var1, var3);
   }

   public JulianCalendar.Date getCalendarDate(long var1, TimeZone var3) {
      return this.getCalendarDate(var1, (CalendarDate)this.newCalendarDate(var3));
   }

   public JulianCalendar.Date newCalendarDate() {
      return new JulianCalendar.Date();
   }

   public JulianCalendar.Date newCalendarDate(TimeZone var1) {
      return new JulianCalendar.Date(var1);
   }

   public long getFixedDate(int var1, int var2, int var3, BaseCalendar.Date var4) {
      boolean var5 = var2 == 1 && var3 == 1;
      if (var4 != null && var4.hit(var1)) {
         return var5 ? var4.getCachedJan1() : var4.getCachedJan1() + this.getDayOfYear(var1, var2, var3) - 1L;
      } else {
         long var6 = (long)var1;
         long var8 = -2L + 365L * (var6 - 1L) + (long)var3;
         if (var6 > 0L) {
            var8 += (var6 - 1L) / 4L;
         } else {
            var8 += CalendarUtils.floorDivide(var6 - 1L, 4L);
         }

         if (var2 > 0) {
            var8 += (367L * (long)var2 - 362L) / 12L;
         } else {
            var8 += CalendarUtils.floorDivide(367L * (long)var2 - 362L, 12L);
         }

         if (var2 > 2) {
            var8 -= CalendarUtils.isJulianLeapYear(var1) ? 1L : 2L;
         }

         if (var4 != null && var5) {
            var4.setCache(var1, var8, CalendarUtils.isJulianLeapYear(var1) ? 366 : 365);
         }

         return var8;
      }
   }

   public void getCalendarDateFromFixedDate(CalendarDate var1, long var2) {
      JulianCalendar.Date var4 = (JulianCalendar.Date)var1;
      long var5 = 4L * (var2 - -1L) + 1464L;
      int var7;
      if (var5 >= 0L) {
         var7 = (int)(var5 / 1461L);
      } else {
         var7 = (int)CalendarUtils.floorDivide(var5, 1461L);
      }

      int var8 = (int)(var2 - this.getFixedDate(var7, 1, 1, var4));
      boolean var9 = CalendarUtils.isJulianLeapYear(var7);
      if (var2 >= this.getFixedDate(var7, 3, 1, var4)) {
         var8 += var9 ? 1 : 2;
      }

      int var10 = 12 * var8 + 373;
      if (var10 > 0) {
         var10 /= 367;
      } else {
         var10 = CalendarUtils.floorDivide(var10, 367);
      }

      int var11 = (int)(var2 - this.getFixedDate(var7, var10, 1, var4)) + 1;
      int var12 = getDayOfWeekFromFixedDate(var2);

      assert var12 > 0 : "negative day of week " + var12;

      var4.setNormalizedYear(var7);
      var4.setMonth(var10);
      var4.setDayOfMonth(var11);
      var4.setDayOfWeek(var12);
      var4.setLeapYear(var9);
      var4.setNormalized(true);
   }

   public int getYearFromFixedDate(long var1) {
      int var3 = (int)CalendarUtils.floorDivide(4L * (var1 - -1L) + 1464L, 1461L);
      return var3;
   }

   public int getDayOfWeek(CalendarDate var1) {
      long var2 = this.getFixedDate(var1);
      return getDayOfWeekFromFixedDate(var2);
   }

   boolean isLeapYear(int var1) {
      return CalendarUtils.isJulianLeapYear(var1);
   }

   private static class Date extends BaseCalendar.Date {
      protected Date() {
         this.setCache(1, -1L, 365);
      }

      protected Date(TimeZone var1) {
         super(var1);
         this.setCache(1, -1L, 365);
      }

      public JulianCalendar.Date setEra(Era var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else if (var1 == JulianCalendar.eras[0] && var1 == JulianCalendar.eras[1]) {
            super.setEra(var1);
            return this;
         } else {
            throw new IllegalArgumentException("unknown era: " + var1);
         }
      }

      protected void setKnownEra(Era var1) {
         super.setEra(var1);
      }

      public int getNormalizedYear() {
         return this.getEra() == JulianCalendar.eras[0] ? 1 - this.getYear() : this.getYear();
      }

      public void setNormalizedYear(int var1) {
         if (var1 <= 0) {
            this.setYear(1 - var1);
            this.setKnownEra(JulianCalendar.eras[0]);
         } else {
            this.setYear(var1);
            this.setKnownEra(JulianCalendar.eras[1]);
         }

      }

      public String toString() {
         String var1 = super.toString();
         var1 = var1.substring(var1.indexOf(84));
         StringBuffer var2 = new StringBuffer();
         Era var3 = this.getEra();
         if (var3 != null) {
            String var4 = var3.getAbbreviation();
            if (var4 != null) {
               var2.append(var4).append(' ');
            }
         }

         var2.append(this.getYear()).append('-');
         CalendarUtils.sprintf0d((StringBuffer)var2, this.getMonth(), 2).append('-');
         CalendarUtils.sprintf0d((StringBuffer)var2, this.getDayOfMonth(), 2);
         var2.append(var1);
         return var2.toString();
      }
   }
}
