package java.util;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import sun.util.calendar.BaseCalendar;
import sun.util.calendar.CalendarSystem;
import sun.util.calendar.CalendarUtils;
import sun.util.calendar.Gregorian;

public class SimpleTimeZone extends TimeZone {
   private int startMonth;
   private int startDay;
   private int startDayOfWeek;
   private int startTime;
   private int startTimeMode;
   private int endMonth;
   private int endDay;
   private int endDayOfWeek;
   private int endTime;
   private int endTimeMode;
   private int startYear;
   private int rawOffset;
   private boolean useDaylight;
   private static final int millisPerHour = 3600000;
   private static final int millisPerDay = 86400000;
   private final byte[] monthLength;
   private static final byte[] staticMonthLength = new byte[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
   private static final byte[] staticLeapMonthLength = new byte[]{31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
   private int startMode;
   private int endMode;
   private int dstSavings;
   private static final Gregorian gcal = CalendarSystem.getGregorianCalendar();
   private transient long cacheYear;
   private transient long cacheStart;
   private transient long cacheEnd;
   private static final int DOM_MODE = 1;
   private static final int DOW_IN_MONTH_MODE = 2;
   private static final int DOW_GE_DOM_MODE = 3;
   private static final int DOW_LE_DOM_MODE = 4;
   public static final int WALL_TIME = 0;
   public static final int STANDARD_TIME = 1;
   public static final int UTC_TIME = 2;
   static final long serialVersionUID = -403250971215465050L;
   static final int currentSerialVersion = 2;
   private int serialVersionOnStream;
   private static final int MAX_RULE_NUM = 6;

   public SimpleTimeZone(int var1, String var2) {
      this.useDaylight = false;
      this.monthLength = staticMonthLength;
      this.serialVersionOnStream = 2;
      this.rawOffset = var1;
      this.setID(var2);
      this.dstSavings = 3600000;
   }

   public SimpleTimeZone(int var1, String var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      this(var1, var2, var3, var4, var5, var6, 0, var7, var8, var9, var10, 0, 3600000);
   }

   public SimpleTimeZone(int var1, String var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11) {
      this(var1, var2, var3, var4, var5, var6, 0, var7, var8, var9, var10, 0, var11);
   }

   public SimpleTimeZone(int var1, String var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13) {
      this.useDaylight = false;
      this.monthLength = staticMonthLength;
      this.serialVersionOnStream = 2;
      this.setID(var2);
      this.rawOffset = var1;
      this.startMonth = var3;
      this.startDay = var4;
      this.startDayOfWeek = var5;
      this.startTime = var6;
      this.startTimeMode = var7;
      this.endMonth = var8;
      this.endDay = var9;
      this.endDayOfWeek = var10;
      this.endTime = var11;
      this.endTimeMode = var12;
      this.dstSavings = var13;
      this.decodeRules();
      if (var13 <= 0) {
         throw new IllegalArgumentException("Illegal daylight saving value: " + var13);
      }
   }

   public void setStartYear(int var1) {
      this.startYear = var1;
      this.invalidateCache();
   }

   public void setStartRule(int var1, int var2, int var3, int var4) {
      this.startMonth = var1;
      this.startDay = var2;
      this.startDayOfWeek = var3;
      this.startTime = var4;
      this.startTimeMode = 0;
      this.decodeStartRule();
      this.invalidateCache();
   }

   public void setStartRule(int var1, int var2, int var3) {
      this.setStartRule(var1, var2, 0, var3);
   }

   public void setStartRule(int var1, int var2, int var3, int var4, boolean var5) {
      if (var5) {
         this.setStartRule(var1, var2, -var3, var4);
      } else {
         this.setStartRule(var1, -var2, -var3, var4);
      }

   }

   public void setEndRule(int var1, int var2, int var3, int var4) {
      this.endMonth = var1;
      this.endDay = var2;
      this.endDayOfWeek = var3;
      this.endTime = var4;
      this.endTimeMode = 0;
      this.decodeEndRule();
      this.invalidateCache();
   }

   public void setEndRule(int var1, int var2, int var3) {
      this.setEndRule(var1, var2, 0, var3);
   }

   public void setEndRule(int var1, int var2, int var3, int var4, boolean var5) {
      if (var5) {
         this.setEndRule(var1, var2, -var3, var4);
      } else {
         this.setEndRule(var1, -var2, -var3, var4);
      }

   }

   public int getOffset(long var1) {
      return this.getOffsets(var1, (int[])null);
   }

   int getOffsets(long var1, int[] var3) {
      int var4 = this.rawOffset;
      if (this.useDaylight) {
         label44: {
            synchronized(this) {
               if (this.cacheStart != 0L && var1 >= this.cacheStart && var1 < this.cacheEnd) {
                  var4 += this.dstSavings;
                  break label44;
               }
            }

            Object var5 = var1 >= -12219292800000L ? gcal : (BaseCalendar)CalendarSystem.forName("julian");
            BaseCalendar.Date var6 = (BaseCalendar.Date)((BaseCalendar)var5).newCalendarDate(TimeZone.NO_TIMEZONE);
            ((BaseCalendar)var5).getCalendarDate(var1 + (long)this.rawOffset, var6);
            int var7 = var6.getNormalizedYear();
            if (var7 >= this.startYear) {
               var6.setTimeOfDay(0, 0, 0, 0);
               var4 = this.getOffset((BaseCalendar)var5, var6, var7, var1);
            }
         }
      }

      if (var3 != null) {
         var3[0] = this.rawOffset;
         var3[1] = var4 - this.rawOffset;
      }

      return var4;
   }

   public int getOffset(int var1, int var2, int var3, int var4, int var5, int var6) {
      if (var1 != 1 && var1 != 0) {
         throw new IllegalArgumentException("Illegal era " + var1);
      } else {
         int var7 = var2;
         if (var1 == 0) {
            var7 = 1 - var2;
         }

         if (var7 >= 292278994) {
            var7 = 2800 + var7 % 2800;
         } else if (var7 <= -292269054) {
            var7 = (int)CalendarUtils.mod((long)var7, 28L);
         }

         int var8 = var3 + 1;
         Object var9 = gcal;
         BaseCalendar.Date var10 = (BaseCalendar.Date)((BaseCalendar)var9).newCalendarDate(TimeZone.NO_TIMEZONE);
         var10.setDate(var7, var8, var4);
         long var11 = ((BaseCalendar)var9).getTime(var10);
         var11 += (long)(var6 - this.rawOffset);
         if (var11 < -12219292800000L) {
            var9 = (BaseCalendar)CalendarSystem.forName("julian");
            var10 = (BaseCalendar.Date)((BaseCalendar)var9).newCalendarDate(TimeZone.NO_TIMEZONE);
            var10.setNormalizedDate(var7, var8, var4);
            var11 = ((BaseCalendar)var9).getTime(var10) + (long)var6 - (long)this.rawOffset;
         }

         if (var10.getNormalizedYear() == var7 && var10.getMonth() == var8 && var10.getDayOfMonth() == var4 && var5 >= 1 && var5 <= 7 && var6 >= 0 && var6 < 86400000) {
            return this.useDaylight && var2 >= this.startYear && var1 == 1 ? this.getOffset((BaseCalendar)var9, var10, var7, var11) : this.rawOffset;
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   private int getOffset(BaseCalendar var1, BaseCalendar.Date var2, int var3, long var4) {
      synchronized(this) {
         if (this.cacheStart != 0L) {
            if (var4 >= this.cacheStart && var4 < this.cacheEnd) {
               return this.rawOffset + this.dstSavings;
            }

            if ((long)var3 == this.cacheYear) {
               return this.rawOffset;
            }
         }
      }

      long var6 = this.getStart(var1, var2, var3);
      long var8 = this.getEnd(var1, var2, var3);
      int var10 = this.rawOffset;
      if (var6 <= var8) {
         if (var4 >= var6 && var4 < var8) {
            var10 += this.dstSavings;
         }

         synchronized(this) {
            this.cacheYear = (long)var3;
            this.cacheStart = var6;
            this.cacheEnd = var8;
         }
      } else {
         if (var4 < var8) {
            var6 = this.getStart(var1, var2, var3 - 1);
            if (var4 >= var6) {
               var10 += this.dstSavings;
            }
         } else if (var4 >= var6) {
            var8 = this.getEnd(var1, var2, var3 + 1);
            if (var4 < var8) {
               var10 += this.dstSavings;
            }
         }

         if (var6 <= var8) {
            synchronized(this) {
               this.cacheYear = (long)this.startYear - 1L;
               this.cacheStart = var6;
               this.cacheEnd = var8;
            }
         }
      }

      return var10;
   }

   private long getStart(BaseCalendar var1, BaseCalendar.Date var2, int var3) {
      int var4 = this.startTime;
      if (this.startTimeMode != 2) {
         var4 -= this.rawOffset;
      }

      return this.getTransition(var1, var2, this.startMode, var3, this.startMonth, this.startDay, this.startDayOfWeek, var4);
   }

   private long getEnd(BaseCalendar var1, BaseCalendar.Date var2, int var3) {
      int var4 = this.endTime;
      if (this.endTimeMode != 2) {
         var4 -= this.rawOffset;
      }

      if (this.endTimeMode == 0) {
         var4 -= this.dstSavings;
      }

      return this.getTransition(var1, var2, this.endMode, var3, this.endMonth, this.endDay, this.endDayOfWeek, var4);
   }

   private long getTransition(BaseCalendar var1, BaseCalendar.Date var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      var2.setNormalizedYear(var4);
      var2.setMonth(var5 + 1);
      switch(var3) {
      case 1:
         var2.setDayOfMonth(var6);
         break;
      case 2:
         var2.setDayOfMonth(1);
         if (var6 < 0) {
            var2.setDayOfMonth(var1.getMonthLength(var2));
         }

         var2 = (BaseCalendar.Date)var1.getNthDayOfWeek(var6, var7, var2);
         break;
      case 3:
         var2.setDayOfMonth(var6);
         var2 = (BaseCalendar.Date)var1.getNthDayOfWeek(1, var7, var2);
         break;
      case 4:
         var2.setDayOfMonth(var6);
         var2 = (BaseCalendar.Date)var1.getNthDayOfWeek(-1, var7, var2);
      }

      return var1.getTime(var2) + (long)var8;
   }

   public int getRawOffset() {
      return this.rawOffset;
   }

   public void setRawOffset(int var1) {
      this.rawOffset = var1;
   }

   public void setDSTSavings(int var1) {
      if (var1 <= 0) {
         throw new IllegalArgumentException("Illegal daylight saving value: " + var1);
      } else {
         this.dstSavings = var1;
      }
   }

   public int getDSTSavings() {
      return this.useDaylight ? this.dstSavings : 0;
   }

   public boolean useDaylightTime() {
      return this.useDaylight;
   }

   public boolean observesDaylightTime() {
      return this.useDaylightTime();
   }

   public boolean inDaylightTime(Date var1) {
      return this.getOffset(var1.getTime()) != this.rawOffset;
   }

   public Object clone() {
      return super.clone();
   }

   public synchronized int hashCode() {
      return this.startMonth ^ this.startDay ^ this.startDayOfWeek ^ this.startTime ^ this.endMonth ^ this.endDay ^ this.endDayOfWeek ^ this.endTime ^ this.rawOffset;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SimpleTimeZone)) {
         return false;
      } else {
         SimpleTimeZone var2 = (SimpleTimeZone)var1;
         return this.getID().equals(var2.getID()) && this.hasSameRules(var2);
      }
   }

   public boolean hasSameRules(TimeZone var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SimpleTimeZone)) {
         return false;
      } else {
         SimpleTimeZone var2 = (SimpleTimeZone)var1;
         return this.rawOffset == var2.rawOffset && this.useDaylight == var2.useDaylight && (!this.useDaylight || this.dstSavings == var2.dstSavings && this.startMode == var2.startMode && this.startMonth == var2.startMonth && this.startDay == var2.startDay && this.startDayOfWeek == var2.startDayOfWeek && this.startTime == var2.startTime && this.startTimeMode == var2.startTimeMode && this.endMode == var2.endMode && this.endMonth == var2.endMonth && this.endDay == var2.endDay && this.endDayOfWeek == var2.endDayOfWeek && this.endTime == var2.endTime && this.endTimeMode == var2.endTimeMode && this.startYear == var2.startYear);
      }
   }

   public String toString() {
      return this.getClass().getName() + "[id=" + this.getID() + ",offset=" + this.rawOffset + ",dstSavings=" + this.dstSavings + ",useDaylight=" + this.useDaylight + ",startYear=" + this.startYear + ",startMode=" + this.startMode + ",startMonth=" + this.startMonth + ",startDay=" + this.startDay + ",startDayOfWeek=" + this.startDayOfWeek + ",startTime=" + this.startTime + ",startTimeMode=" + this.startTimeMode + ",endMode=" + this.endMode + ",endMonth=" + this.endMonth + ",endDay=" + this.endDay + ",endDayOfWeek=" + this.endDayOfWeek + ",endTime=" + this.endTime + ",endTimeMode=" + this.endTimeMode + ']';
   }

   private synchronized void invalidateCache() {
      this.cacheYear = (long)(this.startYear - 1);
      this.cacheStart = this.cacheEnd = 0L;
   }

   private void decodeRules() {
      this.decodeStartRule();
      this.decodeEndRule();
   }

   private void decodeStartRule() {
      this.useDaylight = this.startDay != 0 && this.endDay != 0;
      if (this.startDay != 0) {
         if (this.startMonth < 0 || this.startMonth > 11) {
            throw new IllegalArgumentException("Illegal start month " + this.startMonth);
         }

         if (this.startTime < 0 || this.startTime > 86400000) {
            throw new IllegalArgumentException("Illegal start time " + this.startTime);
         }

         if (this.startDayOfWeek == 0) {
            this.startMode = 1;
         } else {
            if (this.startDayOfWeek > 0) {
               this.startMode = 2;
            } else {
               this.startDayOfWeek = -this.startDayOfWeek;
               if (this.startDay > 0) {
                  this.startMode = 3;
               } else {
                  this.startDay = -this.startDay;
                  this.startMode = 4;
               }
            }

            if (this.startDayOfWeek > 7) {
               throw new IllegalArgumentException("Illegal start day of week " + this.startDayOfWeek);
            }
         }

         if (this.startMode == 2) {
            if (this.startDay < -5 || this.startDay > 5) {
               throw new IllegalArgumentException("Illegal start day of week in month " + this.startDay);
            }
         } else if (this.startDay < 1 || this.startDay > staticMonthLength[this.startMonth]) {
            throw new IllegalArgumentException("Illegal start day " + this.startDay);
         }
      }

   }

   private void decodeEndRule() {
      this.useDaylight = this.startDay != 0 && this.endDay != 0;
      if (this.endDay != 0) {
         if (this.endMonth < 0 || this.endMonth > 11) {
            throw new IllegalArgumentException("Illegal end month " + this.endMonth);
         }

         if (this.endTime < 0 || this.endTime > 86400000) {
            throw new IllegalArgumentException("Illegal end time " + this.endTime);
         }

         if (this.endDayOfWeek == 0) {
            this.endMode = 1;
         } else {
            if (this.endDayOfWeek > 0) {
               this.endMode = 2;
            } else {
               this.endDayOfWeek = -this.endDayOfWeek;
               if (this.endDay > 0) {
                  this.endMode = 3;
               } else {
                  this.endDay = -this.endDay;
                  this.endMode = 4;
               }
            }

            if (this.endDayOfWeek > 7) {
               throw new IllegalArgumentException("Illegal end day of week " + this.endDayOfWeek);
            }
         }

         if (this.endMode == 2) {
            if (this.endDay < -5 || this.endDay > 5) {
               throw new IllegalArgumentException("Illegal end day of week in month " + this.endDay);
            }
         } else if (this.endDay < 1 || this.endDay > staticMonthLength[this.endMonth]) {
            throw new IllegalArgumentException("Illegal end day " + this.endDay);
         }
      }

   }

   private void makeRulesCompatible() {
      switch(this.startMode) {
      case 1:
         this.startDay = 1 + this.startDay / 7;
         this.startDayOfWeek = 1;
      case 2:
      default:
         break;
      case 3:
         if (this.startDay != 1) {
            this.startDay = 1 + this.startDay / 7;
         }
         break;
      case 4:
         if (this.startDay >= 30) {
            this.startDay = -1;
         } else {
            this.startDay = 1 + this.startDay / 7;
         }
      }

      switch(this.endMode) {
      case 1:
         this.endDay = 1 + this.endDay / 7;
         this.endDayOfWeek = 1;
      case 2:
      default:
         break;
      case 3:
         if (this.endDay != 1) {
            this.endDay = 1 + this.endDay / 7;
         }
         break;
      case 4:
         if (this.endDay >= 30) {
            this.endDay = -1;
         } else {
            this.endDay = 1 + this.endDay / 7;
         }
      }

      switch(this.startTimeMode) {
      case 2:
         this.startTime += this.rawOffset;
      }

      while(this.startTime < 0) {
         this.startTime += 86400000;
         this.startDayOfWeek = 1 + (this.startDayOfWeek + 5) % 7;
      }

      while(this.startTime >= 86400000) {
         this.startTime -= 86400000;
         this.startDayOfWeek = 1 + this.startDayOfWeek % 7;
      }

      switch(this.endTimeMode) {
      case 1:
         this.endTime += this.dstSavings;
         break;
      case 2:
         this.endTime += this.rawOffset + this.dstSavings;
      }

      while(this.endTime < 0) {
         this.endTime += 86400000;
         this.endDayOfWeek = 1 + (this.endDayOfWeek + 5) % 7;
      }

      while(this.endTime >= 86400000) {
         this.endTime -= 86400000;
         this.endDayOfWeek = 1 + this.endDayOfWeek % 7;
      }

   }

   private byte[] packRules() {
      byte[] var1 = new byte[]{(byte)this.startDay, (byte)this.startDayOfWeek, (byte)this.endDay, (byte)this.endDayOfWeek, (byte)this.startTimeMode, (byte)this.endTimeMode};
      return var1;
   }

   private void unpackRules(byte[] var1) {
      this.startDay = var1[0];
      this.startDayOfWeek = var1[1];
      this.endDay = var1[2];
      this.endDayOfWeek = var1[3];
      if (var1.length >= 6) {
         this.startTimeMode = var1[4];
         this.endTimeMode = var1[5];
      }

   }

   private int[] packTimes() {
      int[] var1 = new int[]{this.startTime, this.endTime};
      return var1;
   }

   private void unpackTimes(int[] var1) {
      this.startTime = var1[0];
      this.endTime = var1[1];
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      byte[] var2 = this.packRules();
      int[] var3 = this.packTimes();
      this.makeRulesCompatible();
      var1.defaultWriteObject();
      var1.writeInt(var2.length);
      var1.write(var2);
      var1.writeObject(var3);
      this.unpackRules(var2);
      this.unpackTimes(var3);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      if (this.serialVersionOnStream < 1) {
         if (this.startDayOfWeek == 0) {
            this.startDayOfWeek = 1;
         }

         if (this.endDayOfWeek == 0) {
            this.endDayOfWeek = 1;
         }

         this.startMode = this.endMode = 2;
         this.dstSavings = 3600000;
      } else {
         int var2 = var1.readInt();
         if (var2 > 6) {
            throw new InvalidObjectException("Too many rules: " + var2);
         }

         byte[] var3 = new byte[var2];
         var1.readFully(var3);
         this.unpackRules(var3);
      }

      if (this.serialVersionOnStream >= 2) {
         int[] var4 = (int[])((int[])var1.readObject());
         this.unpackTimes(var4);
      }

      this.serialVersionOnStream = 2;
   }
}
