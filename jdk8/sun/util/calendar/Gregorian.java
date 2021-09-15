package sun.util.calendar;

import java.util.TimeZone;

public class Gregorian extends BaseCalendar {
   Gregorian() {
   }

   public String getName() {
      return "gregorian";
   }

   public Gregorian.Date getCalendarDate() {
      return this.getCalendarDate(System.currentTimeMillis(), (CalendarDate)this.newCalendarDate());
   }

   public Gregorian.Date getCalendarDate(long var1) {
      return this.getCalendarDate(var1, (CalendarDate)this.newCalendarDate());
   }

   public Gregorian.Date getCalendarDate(long var1, CalendarDate var3) {
      return (Gregorian.Date)super.getCalendarDate(var1, var3);
   }

   public Gregorian.Date getCalendarDate(long var1, TimeZone var3) {
      return this.getCalendarDate(var1, (CalendarDate)this.newCalendarDate(var3));
   }

   public Gregorian.Date newCalendarDate() {
      return new Gregorian.Date();
   }

   public Gregorian.Date newCalendarDate(TimeZone var1) {
      return new Gregorian.Date(var1);
   }

   static class Date extends BaseCalendar.Date {
      protected Date() {
      }

      protected Date(TimeZone var1) {
         super(var1);
      }

      public int getNormalizedYear() {
         return this.getYear();
      }

      public void setNormalizedYear(int var1) {
         this.setYear(var1);
      }
   }
}
