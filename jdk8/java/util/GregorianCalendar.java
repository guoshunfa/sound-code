package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import sun.util.calendar.BaseCalendar;
import sun.util.calendar.CalendarDate;
import sun.util.calendar.CalendarSystem;
import sun.util.calendar.CalendarUtils;
import sun.util.calendar.Era;
import sun.util.calendar.Gregorian;
import sun.util.calendar.JulianCalendar;
import sun.util.calendar.ZoneInfo;

public class GregorianCalendar extends Calendar {
   public static final int BC = 0;
   static final int BCE = 0;
   public static final int AD = 1;
   static final int CE = 1;
   private static final int EPOCH_OFFSET = 719163;
   private static final int EPOCH_YEAR = 1970;
   static final int[] MONTH_LENGTH = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
   static final int[] LEAP_MONTH_LENGTH = new int[]{31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
   private static final int ONE_SECOND = 1000;
   private static final int ONE_MINUTE = 60000;
   private static final int ONE_HOUR = 3600000;
   private static final long ONE_DAY = 86400000L;
   private static final long ONE_WEEK = 604800000L;
   static final int[] MIN_VALUES = new int[]{0, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, -46800000, 0};
   static final int[] LEAST_MAX_VALUES = new int[]{1, 292269054, 11, 52, 4, 28, 365, 7, 4, 1, 11, 23, 59, 59, 999, 50400000, 1200000};
   static final int[] MAX_VALUES = new int[]{1, 292278994, 11, 53, 6, 31, 366, 7, 6, 1, 11, 23, 59, 59, 999, 50400000, 7200000};
   static final long serialVersionUID = -8125100834729963327L;
   private static final Gregorian gcal = CalendarSystem.getGregorianCalendar();
   private static JulianCalendar jcal;
   private static Era[] jeras;
   static final long DEFAULT_GREGORIAN_CUTOVER = -12219292800000L;
   private long gregorianCutover;
   private transient long gregorianCutoverDate;
   private transient int gregorianCutoverYear;
   private transient int gregorianCutoverYearJulian;
   private transient BaseCalendar.Date gdate;
   private transient BaseCalendar.Date cdate;
   private transient BaseCalendar calsys;
   private transient int[] zoneOffsets;
   private transient int[] originalFields;
   private transient long cachedFixedDate;

   public GregorianCalendar() {
      this(TimeZone.getDefaultRef(), Locale.getDefault(Locale.Category.FORMAT));
      this.setZoneShared(true);
   }

   public GregorianCalendar(TimeZone var1) {
      this(var1, Locale.getDefault(Locale.Category.FORMAT));
   }

   public GregorianCalendar(Locale var1) {
      this(TimeZone.getDefaultRef(), var1);
      this.setZoneShared(true);
   }

   public GregorianCalendar(TimeZone var1, Locale var2) {
      super(var1, var2);
      this.gregorianCutover = -12219292800000L;
      this.gregorianCutoverDate = 577736L;
      this.gregorianCutoverYear = 1582;
      this.gregorianCutoverYearJulian = 1582;
      this.cachedFixedDate = Long.MIN_VALUE;
      this.gdate = gcal.newCalendarDate(var1);
      this.setTimeInMillis(System.currentTimeMillis());
   }

   public GregorianCalendar(int var1, int var2, int var3) {
      this(var1, var2, var3, 0, 0, 0, 0);
   }

   public GregorianCalendar(int var1, int var2, int var3, int var4, int var5) {
      this(var1, var2, var3, var4, var5, 0, 0);
   }

   public GregorianCalendar(int var1, int var2, int var3, int var4, int var5, int var6) {
      this(var1, var2, var3, var4, var5, var6, 0);
   }

   GregorianCalendar(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.gregorianCutover = -12219292800000L;
      this.gregorianCutoverDate = 577736L;
      this.gregorianCutoverYear = 1582;
      this.gregorianCutoverYearJulian = 1582;
      this.cachedFixedDate = Long.MIN_VALUE;
      this.gdate = gcal.newCalendarDate(this.getZone());
      this.set(1, var1);
      this.set(2, var2);
      this.set(5, var3);
      if (var4 >= 12 && var4 <= 23) {
         this.internalSet(9, 1);
         this.internalSet(10, var4 - 12);
      } else {
         this.internalSet(10, var4);
      }

      this.setFieldsComputed(1536);
      this.set(11, var4);
      this.set(12, var5);
      this.set(13, var6);
      this.internalSet(14, var7);
   }

   GregorianCalendar(TimeZone var1, Locale var2, boolean var3) {
      super(var1, var2);
      this.gregorianCutover = -12219292800000L;
      this.gregorianCutoverDate = 577736L;
      this.gregorianCutoverYear = 1582;
      this.gregorianCutoverYearJulian = 1582;
      this.cachedFixedDate = Long.MIN_VALUE;
      this.gdate = gcal.newCalendarDate(this.getZone());
   }

   public void setGregorianChange(Date var1) {
      long var2 = var1.getTime();
      if (var2 != this.gregorianCutover) {
         this.complete();
         this.setGregorianChange(var2);
      }
   }

   private void setGregorianChange(long var1) {
      this.gregorianCutover = var1;
      this.gregorianCutoverDate = CalendarUtils.floorDivide(var1, 86400000L) + 719163L;
      if (var1 == Long.MAX_VALUE) {
         ++this.gregorianCutoverDate;
      }

      BaseCalendar.Date var3 = this.getGregorianCutoverDate();
      this.gregorianCutoverYear = var3.getYear();
      BaseCalendar var4 = getJulianCalendarSystem();
      var3 = (BaseCalendar.Date)var4.newCalendarDate(TimeZone.NO_TIMEZONE);
      var4.getCalendarDateFromFixedDate(var3, this.gregorianCutoverDate - 1L);
      this.gregorianCutoverYearJulian = var3.getNormalizedYear();
      if (this.time < this.gregorianCutover) {
         this.setUnnormalized();
      }

   }

   public final Date getGregorianChange() {
      return new Date(this.gregorianCutover);
   }

   public boolean isLeapYear(int var1) {
      if ((var1 & 3) != 0) {
         return false;
      } else if (var1 > this.gregorianCutoverYear) {
         return var1 % 100 != 0 || var1 % 400 == 0;
      } else if (var1 < this.gregorianCutoverYearJulian) {
         return true;
      } else {
         boolean var2;
         if (this.gregorianCutoverYear == this.gregorianCutoverYearJulian) {
            BaseCalendar.Date var3 = this.getCalendarDate(this.gregorianCutoverDate);
            var2 = var3.getMonth() < 3;
         } else {
            var2 = var1 == this.gregorianCutoverYear;
         }

         return var2 ? var1 % 100 != 0 || var1 % 400 == 0 : true;
      }
   }

   public String getCalendarType() {
      return "gregory";
   }

   public boolean equals(Object var1) {
      return var1 instanceof GregorianCalendar && super.equals(var1) && this.gregorianCutover == ((GregorianCalendar)var1).gregorianCutover;
   }

   public int hashCode() {
      return super.hashCode() ^ (int)this.gregorianCutoverDate;
   }

   public void add(int var1, int var2) {
      if (var2 != 0) {
         if (var1 >= 0 && var1 < 15) {
            this.complete();
            int var3;
            if (var1 == 1) {
               var3 = this.internalGet(1);
               if (this.internalGetEra() == 1) {
                  var3 += var2;
                  if (var3 > 0) {
                     this.set(1, var3);
                  } else {
                     this.set(1, 1 - var3);
                     this.set(0, 0);
                  }
               } else {
                  var3 -= var2;
                  if (var3 > 0) {
                     this.set(1, var3);
                  } else {
                     this.set(1, 1 - var3);
                     this.set(0, 1);
                  }
               }

               this.pinDayOfMonth();
            } else if (var1 == 2) {
               var3 = this.internalGet(2) + var2;
               int var4 = this.internalGet(1);
               int var5;
               if (var3 >= 0) {
                  var5 = var3 / 12;
               } else {
                  var5 = (var3 + 1) / 12 - 1;
               }

               if (var5 != 0) {
                  if (this.internalGetEra() == 1) {
                     var4 += var5;
                     if (var4 > 0) {
                        this.set(1, var4);
                     } else {
                        this.set(1, 1 - var4);
                        this.set(0, 0);
                     }
                  } else {
                     var4 -= var5;
                     if (var4 > 0) {
                        this.set(1, var4);
                     } else {
                        this.set(1, 1 - var4);
                        this.set(0, 1);
                     }
                  }
               }

               if (var3 >= 0) {
                  this.set(2, var3 % 12);
               } else {
                  var3 %= 12;
                  if (var3 < 0) {
                     var3 += 12;
                  }

                  this.set(2, 0 + var3);
               }

               this.pinDayOfMonth();
            } else if (var1 == 0) {
               var3 = this.internalGet(0) + var2;
               if (var3 < 0) {
                  var3 = 0;
               }

               if (var3 > 1) {
                  var3 = 1;
               }

               this.set(0, var3);
            } else {
               long var13 = (long)var2;
               long var12 = 0L;
               switch(var1) {
               case 3:
               case 4:
               case 8:
                  var13 *= 7L;
               case 5:
               case 6:
               case 7:
               case 14:
               default:
                  break;
               case 9:
                  var13 = (long)(var2 / 2);
                  var12 = (long)(12 * (var2 % 2));
                  break;
               case 10:
               case 11:
                  var13 *= 3600000L;
                  break;
               case 12:
                  var13 *= 60000L;
                  break;
               case 13:
                  var13 *= 1000L;
               }

               if (var1 >= 10) {
                  this.setTimeInMillis(this.time + var13);
                  return;
               }

               long var7 = this.getCurrentFixedDate();
               var12 += (long)this.internalGet(11);
               var12 *= 60L;
               var12 += (long)this.internalGet(12);
               var12 *= 60L;
               var12 += (long)this.internalGet(13);
               var12 *= 1000L;
               var12 += (long)this.internalGet(14);
               if (var12 >= 86400000L) {
                  ++var7;
                  var12 -= 86400000L;
               } else if (var12 < 0L) {
                  --var7;
                  var12 += 86400000L;
               }

               var7 += var13;
               int var9 = this.internalGet(15) + this.internalGet(16);
               this.setTimeInMillis((var7 - 719163L) * 86400000L + var12 - (long)var9);
               var9 -= this.internalGet(15) + this.internalGet(16);
               if (var9 != 0) {
                  this.setTimeInMillis(this.time + (long)var9);
                  long var10 = this.getCurrentFixedDate();
                  if (var10 != var7) {
                     this.setTimeInMillis(this.time - (long)var9);
                  }
               }
            }

         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   public void roll(int var1, boolean var2) {
      this.roll(var1, var2 ? 1 : -1);
   }

   public void roll(int var1, int var2) {
      if (var2 != 0) {
         if (var1 >= 0 && var1 < 15) {
            long var19;
            long var20;
            label208: {
               this.complete();
               int var3 = this.getMinimum(var1);
               int var4 = this.getMaximum(var1);
               int var5;
               int var6;
               int var7;
               int var9;
               int var10;
               int var11;
               int var21;
               BaseCalendar.Date var25;
               long var26;
               switch(var1) {
               case 0:
               case 1:
               case 9:
               case 12:
               case 13:
               case 14:
               default:
                  break;
               case 2:
                  if (!this.isCutoverYear(this.cdate.getNormalizedYear())) {
                     var5 = (this.internalGet(2) + var2) % 12;
                     if (var5 < 0) {
                        var5 += 12;
                     }

                     this.set(2, var5);
                     var6 = this.monthLength(var5);
                     if (this.internalGet(5) > var6) {
                        this.set(5, var6);
                     }
                  } else {
                     var5 = this.getActualMaximum(2) + 1;
                     var6 = (this.internalGet(2) + var2) % var5;
                     if (var6 < 0) {
                        var6 += var5;
                     }

                     this.set(2, var6);
                     var7 = this.getActualMaximum(5);
                     if (this.internalGet(5) > var7) {
                        this.set(5, var7);
                     }
                  }

                  return;
               case 3:
                  var5 = this.cdate.getNormalizedYear();
                  var4 = this.getActualMaximum(3);
                  this.set(7, this.internalGet(7));
                  var6 = this.internalGet(3);
                  var7 = var6 + var2;
                  long var29;
                  if (!this.isCutoverYear(var5)) {
                     var21 = this.getWeekYear();
                     if (var21 == var5) {
                        if (var7 > var3 && var7 < var4) {
                           this.set(3, var7);
                           return;
                        }

                        var26 = this.getCurrentFixedDate();
                        var29 = var26 - (long)(7 * (var6 - var3));
                        if (this.calsys.getYearFromFixedDate(var29) != var5) {
                           ++var3;
                        }

                        var26 += (long)(7 * (var4 - this.internalGet(3)));
                        if (this.calsys.getYearFromFixedDate(var26) != var5) {
                           --var4;
                        }
                     } else if (var21 > var5) {
                        if (var2 < 0) {
                           ++var2;
                        }

                        var6 = var4;
                     } else {
                        if (var2 > 0) {
                           var2 -= var6 - var4;
                        }

                        var6 = var3;
                     }

                     this.set(var1, getRolledValue(var6, var2, var3, var4));
                     return;
                  }

                  long var22 = this.getCurrentFixedDate();
                  Object var27;
                  if (this.gregorianCutoverYear == this.gregorianCutoverYearJulian) {
                     var27 = this.getCutoverCalendarSystem();
                  } else if (var5 == this.gregorianCutoverYear) {
                     var27 = gcal;
                  } else {
                     var27 = getJulianCalendarSystem();
                  }

                  var29 = var22 - (long)(7 * (var6 - var3));
                  if (((BaseCalendar)var27).getYearFromFixedDate(var29) != var5) {
                     ++var3;
                  }

                  var22 += (long)(7 * (var4 - var6));
                  var27 = var22 >= this.gregorianCutoverDate ? gcal : getJulianCalendarSystem();
                  if (((BaseCalendar)var27).getYearFromFixedDate(var22) != var5) {
                     --var4;
                  }

                  var7 = getRolledValue(var6, var2, var3, var4) - 1;
                  BaseCalendar.Date var30 = this.getCalendarDate(var29 + (long)(var7 * 7));
                  this.set(2, var30.getMonth() - 1);
                  this.set(5, var30.getDayOfMonth());
                  return;
               case 4:
                  boolean var23 = this.isCutoverYear(this.cdate.getNormalizedYear());
                  var6 = this.internalGet(7) - this.getFirstDayOfWeek();
                  if (var6 < 0) {
                     var6 += 7;
                  }

                  var20 = this.getCurrentFixedDate();
                  if (var23) {
                     var26 = this.getFixedDateMonth1(this.cdate, var20);
                     var11 = this.actualMonthLength();
                  } else {
                     var26 = var20 - (long)this.internalGet(5) + 1L;
                     var11 = this.calsys.getMonthLength(this.cdate);
                  }

                  long var28 = BaseCalendar.getDayOfWeekDateOnOrBefore(var26 + 6L, this.getFirstDayOfWeek());
                  if ((int)(var28 - var26) >= this.getMinimalDaysInFirstWeek()) {
                     var28 -= 7L;
                  }

                  var4 = this.getActualMaximum(var1);
                  int var31 = getRolledValue(this.internalGet(var1), var2, 1, var4) - 1;
                  long var15 = var28 + (long)(var31 * 7) + (long)var6;
                  if (var15 < var26) {
                     var15 = var26;
                  } else if (var15 >= var26 + (long)var11) {
                     var15 = var26 + (long)var11 - 1L;
                  }

                  int var17;
                  if (var23) {
                     BaseCalendar.Date var18 = this.getCalendarDate(var15);
                     var17 = var18.getDayOfMonth();
                  } else {
                     var17 = (int)(var15 - var26) + 1;
                  }

                  this.set(5, var17);
                  return;
               case 5:
                  if (this.isCutoverYear(this.cdate.getNormalizedYear())) {
                     var19 = this.getCurrentFixedDate();
                     var20 = this.getFixedDateMonth1(this.cdate, var19);
                     var9 = getRolledValue((int)(var19 - var20), var2, 0, this.actualMonthLength() - 1);
                     var25 = this.getCalendarDate(var20 + (long)var9);

                     assert var25.getMonth() - 1 == this.internalGet(2);

                     this.set(5, var25.getDayOfMonth());
                     return;
                  }

                  var4 = this.calsys.getMonthLength(this.cdate);
                  break;
               case 6:
                  var4 = this.getActualMaximum(var1);
                  if (this.isCutoverYear(this.cdate.getNormalizedYear())) {
                     var19 = this.getCurrentFixedDate();
                     var20 = var19 - (long)this.internalGet(6) + 1L;
                     var9 = getRolledValue((int)(var19 - var20) + 1, var2, var3, var4);
                     var25 = this.getCalendarDate(var20 + (long)var9 - 1L);
                     this.set(2, var25.getMonth() - 1);
                     this.set(5, var25.getDayOfMonth());
                     return;
                  }
                  break;
               case 7:
                  if (this.isCutoverYear(this.cdate.getNormalizedYear())) {
                     break label208;
                  }

                  var5 = this.internalGet(3);
                  if (var5 <= 1 || var5 >= 52) {
                     break label208;
                  }

                  this.set(3, var5);
                  var4 = 7;
                  break;
               case 8:
                  var3 = 1;
                  if (this.isCutoverYear(this.cdate.getNormalizedYear())) {
                     var19 = this.getCurrentFixedDate();
                     var20 = this.getFixedDateMonth1(this.cdate, var19);
                     var9 = this.actualMonthLength();
                     var10 = var9 % 7;
                     var4 = var9 / 7;
                     var11 = (int)(var19 - var20) % 7;
                     if (var11 < var10) {
                        ++var4;
                     }

                     int var12 = getRolledValue(this.internalGet(var1), var2, var3, var4) - 1;
                     var19 = var20 + (long)(var12 * 7) + (long)var11;
                     Object var13 = var19 >= this.gregorianCutoverDate ? gcal : getJulianCalendarSystem();
                     BaseCalendar.Date var14 = (BaseCalendar.Date)((BaseCalendar)var13).newCalendarDate(TimeZone.NO_TIMEZONE);
                     ((BaseCalendar)var13).getCalendarDateFromFixedDate(var14, var19);
                     this.set(5, var14.getDayOfMonth());
                     return;
                  }

                  var5 = this.internalGet(5);
                  var6 = this.calsys.getMonthLength(this.cdate);
                  var7 = var6 % 7;
                  var4 = var6 / 7;
                  var21 = (var5 - 1) % 7;
                  if (var21 < var7) {
                     ++var4;
                  }

                  this.set(7, this.internalGet(7));
                  break;
               case 10:
               case 11:
                  var5 = var4 + 1;
                  var6 = this.internalGet(var1);
                  var7 = (var6 + var2) % var5;
                  if (var7 < 0) {
                     var7 += var5;
                  }

                  this.time += (long)(3600000 * (var7 - var6));
                  CalendarDate var8 = this.calsys.getCalendarDate(this.time, this.getZone());
                  if (this.internalGet(5) != var8.getDayOfMonth()) {
                     var8.setDate(this.internalGet(1), this.internalGet(2) + 1, this.internalGet(5));
                     if (var1 == 10) {
                        assert this.internalGet(9) == 1;

                        var8.addHours(12);
                     }

                     this.time = this.calsys.getTime(var8);
                  }

                  var9 = var8.getHours();
                  this.internalSet(var1, var9 % var5);
                  if (var1 == 10) {
                     this.internalSet(11, var9);
                  } else {
                     this.internalSet(9, var9 / 12);
                     this.internalSet(10, var9 % 12);
                  }

                  var10 = var8.getZoneOffset();
                  var11 = var8.getDaylightSaving();
                  this.internalSet(15, var10 - var11);
                  this.internalSet(16, var11);
                  return;
               }

               this.set(var1, getRolledValue(this.internalGet(var1), var2, var3, var4));
               return;
            }

            var2 %= 7;
            if (var2 != 0) {
               var19 = this.getCurrentFixedDate();
               var20 = BaseCalendar.getDayOfWeekDateOnOrBefore(var19, this.getFirstDayOfWeek());
               var19 += (long)var2;
               if (var19 < var20) {
                  var19 += 7L;
               } else if (var19 >= var20 + 7L) {
                  var19 -= 7L;
               }

               BaseCalendar.Date var24 = this.getCalendarDate(var19);
               this.set(0, var24.getNormalizedYear() <= 0 ? 0 : 1);
               this.set(var24.getYear(), var24.getMonth() - 1, var24.getDayOfMonth());
            }
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   public int getMinimum(int var1) {
      return MIN_VALUES[var1];
   }

   public int getMaximum(int var1) {
      switch(var1) {
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 8:
         if (this.gregorianCutoverYear <= 200) {
            GregorianCalendar var2 = (GregorianCalendar)this.clone();
            var2.setLenient(true);
            var2.setTimeInMillis(this.gregorianCutover);
            int var3 = var2.getActualMaximum(var1);
            var2.setTimeInMillis(this.gregorianCutover - 1L);
            int var4 = var2.getActualMaximum(var1);
            return Math.max(MAX_VALUES[var1], Math.max(var3, var4));
         }
      case 7:
      default:
         return MAX_VALUES[var1];
      }
   }

   public int getGreatestMinimum(int var1) {
      if (var1 == 5) {
         BaseCalendar.Date var2 = this.getGregorianCutoverDate();
         long var3 = this.getFixedDateMonth1(var2, this.gregorianCutoverDate);
         var2 = this.getCalendarDate(var3);
         return Math.max(MIN_VALUES[var1], var2.getDayOfMonth());
      } else {
         return MIN_VALUES[var1];
      }
   }

   public int getLeastMaximum(int var1) {
      switch(var1) {
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 8:
         GregorianCalendar var2 = (GregorianCalendar)this.clone();
         var2.setLenient(true);
         var2.setTimeInMillis(this.gregorianCutover);
         int var3 = var2.getActualMaximum(var1);
         var2.setTimeInMillis(this.gregorianCutover - 1L);
         int var4 = var2.getActualMaximum(var1);
         return Math.min(LEAST_MAX_VALUES[var1], Math.min(var3, var4));
      case 7:
      default:
         return LEAST_MAX_VALUES[var1];
      }
   }

   public int getActualMinimum(int var1) {
      if (var1 == 5) {
         GregorianCalendar var2 = this.getNormalizedCalendar();
         int var3 = var2.cdate.getNormalizedYear();
         if (var3 == this.gregorianCutoverYear || var3 == this.gregorianCutoverYearJulian) {
            long var4 = this.getFixedDateMonth1(var2.cdate, var2.calsys.getFixedDate(var2.cdate));
            BaseCalendar.Date var6 = this.getCalendarDate(var4);
            return var6.getDayOfMonth();
         }
      }

      return this.getMinimum(var1);
   }

   public int getActualMaximum(int var1) {
      if ((130689 & 1 << var1) != 0) {
         return this.getMaximum(var1);
      } else {
         GregorianCalendar var3 = this.getNormalizedCalendar();
         BaseCalendar.Date var4 = var3.cdate;
         BaseCalendar var5 = var3.calsys;
         int var6 = var4.getNormalizedYear();
         boolean var7 = true;
         int var8;
         int var9;
         int var10;
         int var14;
         long var15;
         long var17;
         CalendarDate var18;
         int var19;
         switch(var1) {
         case 1:
            if (var3 == this) {
               var3 = (GregorianCalendar)this.clone();
            }

            var15 = var3.getYearOffsetInMillis();
            if (var3.internalGetEra() == 1) {
               var3.setTimeInMillis(Long.MAX_VALUE);
               var14 = var3.get(1);
               var17 = var3.getYearOffsetInMillis();
               if (var15 > var17) {
                  --var14;
               }
            } else {
               Object var23 = var3.getTimeInMillis() >= this.gregorianCutover ? gcal : getJulianCalendarSystem();
               CalendarDate var21 = ((CalendarSystem)var23).getCalendarDate(Long.MIN_VALUE, this.getZone());
               long var12 = (var5.getDayOfYear(var21) - 1L) * 24L + (long)var21.getHours();
               var12 *= 60L;
               var12 += (long)var21.getMinutes();
               var12 *= 60L;
               var12 += (long)var21.getSeconds();
               var12 *= 1000L;
               var12 += (long)var21.getMillis();
               var14 = var21.getYear();
               if (var14 <= 0) {
                  assert var23 == gcal;

                  var14 = 1 - var14;
               }

               if (var15 < var12) {
                  --var14;
               }
            }
            break;
         case 2:
            if (!var3.isCutoverYear(var6)) {
               var14 = 11;
            } else {
               do {
                  ++var6;
                  var15 = gcal.getFixedDate(var6, 1, 1, (BaseCalendar.Date)null);
               } while(var15 < this.gregorianCutoverDate);

               BaseCalendar.Date var22 = (BaseCalendar.Date)var4.clone();
               var5.getCalendarDateFromFixedDate(var22, var15 - 1L);
               var14 = var22.getMonth() - 1;
            }
            break;
         case 3:
            if (!var3.isCutoverYear(var6)) {
               var18 = var5.newCalendarDate(TimeZone.NO_TIMEZONE);
               var18.setDate(var4.getYear(), 1, 1);
               var9 = var5.getDayOfWeek(var18);
               var9 -= this.getFirstDayOfWeek();
               if (var9 < 0) {
                  var9 += 7;
               }

               var14 = 52;
               var10 = var9 + this.getMinimalDaysInFirstWeek() - 1;
               if (var10 == 6 || var4.isLeapYear() && (var10 == 5 || var10 == 12)) {
                  ++var14;
               }
            } else {
               if (var3 == this) {
                  var3 = (GregorianCalendar)var3.clone();
               }

               var8 = this.getActualMaximum(6);
               var3.set(6, var8);
               var14 = var3.get(3);
               if (this.internalGet(1) != var3.getWeekYear()) {
                  var3.set(6, var8 - 7);
                  var14 = var3.get(3);
               }
            }
            break;
         case 4:
            if (!var3.isCutoverYear(var6)) {
               var18 = var5.newCalendarDate((TimeZone)null);
               var18.setDate(var4.getYear(), var4.getMonth(), 1);
               var9 = var5.getDayOfWeek(var18);
               var10 = var5.getMonthLength(var18);
               var9 -= this.getFirstDayOfWeek();
               if (var9 < 0) {
                  var9 += 7;
               }

               var19 = 7 - var9;
               var14 = 3;
               if (var19 >= this.getMinimalDaysInFirstWeek()) {
                  ++var14;
               }

               var10 -= var19 + 21;
               if (var10 > 0) {
                  ++var14;
                  if (var10 > 7) {
                     ++var14;
                  }
               }
               break;
            } else {
               if (var3 == this) {
                  var3 = (GregorianCalendar)var3.clone();
               }

               var8 = var3.internalGet(1);
               var9 = var3.internalGet(2);

               do {
                  var14 = var3.get(4);
                  var3.add(4, 1);
               } while(var3.get(1) == var8 && var3.get(2) == var9);

               return var14;
            }
         case 5:
            var14 = var5.getMonthLength(var4);
            if (var3.isCutoverYear(var6) && var4.getDayOfMonth() != var14) {
               var15 = var3.getCurrentFixedDate();
               if (var15 < this.gregorianCutoverDate) {
                  var10 = var3.actualMonthLength();
                  long var20 = var3.getFixedDateMonth1(var3.cdate, var15) + (long)var10 - 1L;
                  BaseCalendar.Date var13 = var3.getCalendarDate(var20);
                  var14 = var13.getDayOfMonth();
               }
            }
            break;
         case 6:
            if (!var3.isCutoverYear(var6)) {
               var14 = var5.getYearLength(var4);
            } else {
               if (this.gregorianCutoverYear == this.gregorianCutoverYearJulian) {
                  BaseCalendar var16 = var3.getCutoverCalendarSystem();
                  var15 = var16.getFixedDate(var6, 1, 1, (BaseCalendar.Date)null);
               } else if (var6 == this.gregorianCutoverYearJulian) {
                  var15 = var5.getFixedDate(var6, 1, 1, (BaseCalendar.Date)null);
               } else {
                  var15 = this.gregorianCutoverDate;
               }

               ++var6;
               var17 = gcal.getFixedDate(var6, 1, 1, (BaseCalendar.Date)null);
               if (var17 < this.gregorianCutoverDate) {
                  var17 = this.gregorianCutoverDate;
               }

               assert var15 <= var5.getFixedDate(var4.getNormalizedYear(), var4.getMonth(), var4.getDayOfMonth(), var4);

               assert var17 >= var5.getFixedDate(var4.getNormalizedYear(), var4.getMonth(), var4.getDayOfMonth(), var4);

               var14 = (int)(var17 - var15);
            }
            break;
         case 7:
         default:
            throw new ArrayIndexOutOfBoundsException(var1);
         case 8:
            var10 = var4.getDayOfWeek();
            if (!var3.isCutoverYear(var6)) {
               BaseCalendar.Date var11 = (BaseCalendar.Date)var4.clone();
               var8 = var5.getMonthLength(var11);
               var11.setDayOfMonth(1);
               var5.normalize(var11);
               var9 = var11.getDayOfWeek();
            } else {
               if (var3 == this) {
                  var3 = (GregorianCalendar)this.clone();
               }

               var8 = var3.actualMonthLength();
               var3.set(5, var3.getActualMinimum(5));
               var9 = var3.get(7);
            }

            var19 = var10 - var9;
            if (var19 < 0) {
               var19 += 7;
            }

            var8 -= var19;
            var14 = (var8 + 6) / 7;
         }

         return var14;
      }
   }

   private long getYearOffsetInMillis() {
      long var1 = (long)((this.internalGet(6) - 1) * 24);
      var1 += (long)this.internalGet(11);
      var1 *= 60L;
      var1 += (long)this.internalGet(12);
      var1 *= 60L;
      var1 += (long)this.internalGet(13);
      var1 *= 1000L;
      return var1 + (long)this.internalGet(14) - (long)(this.internalGet(15) + this.internalGet(16));
   }

   public Object clone() {
      GregorianCalendar var1 = (GregorianCalendar)super.clone();
      var1.gdate = (BaseCalendar.Date)this.gdate.clone();
      if (this.cdate != null) {
         if (this.cdate != this.gdate) {
            var1.cdate = (BaseCalendar.Date)this.cdate.clone();
         } else {
            var1.cdate = var1.gdate;
         }
      }

      var1.originalFields = null;
      var1.zoneOffsets = null;
      return var1;
   }

   public TimeZone getTimeZone() {
      TimeZone var1 = super.getTimeZone();
      this.gdate.setZone(var1);
      if (this.cdate != null && this.cdate != this.gdate) {
         this.cdate.setZone(var1);
      }

      return var1;
   }

   public void setTimeZone(TimeZone var1) {
      super.setTimeZone(var1);
      this.gdate.setZone(var1);
      if (this.cdate != null && this.cdate != this.gdate) {
         this.cdate.setZone(var1);
      }

   }

   public final boolean isWeekDateSupported() {
      return true;
   }

   public int getWeekYear() {
      int var1 = this.get(1);
      if (this.internalGetEra() == 0) {
         var1 = 1 - var1;
      }

      int var2;
      if (var1 > this.gregorianCutoverYear + 1) {
         var2 = this.internalGet(3);
         if (this.internalGet(2) == 0) {
            if (var2 >= 52) {
               --var1;
            }
         } else if (var2 == 1) {
            ++var1;
         }

         return var1;
      } else {
         var2 = this.internalGet(6);
         int var3 = this.getActualMaximum(6);
         int var4 = this.getMinimalDaysInFirstWeek();
         if (var2 > var4 && var2 < var3 - 6) {
            return var1;
         } else {
            GregorianCalendar var5 = (GregorianCalendar)this.clone();
            var5.setLenient(true);
            var5.setTimeZone(TimeZone.getTimeZone("GMT"));
            var5.set(6, 1);
            var5.complete();
            int var6 = this.getFirstDayOfWeek() - var5.get(7);
            if (var6 != 0) {
               if (var6 < 0) {
                  var6 += 7;
               }

               var5.add(6, var6);
            }

            int var7 = var5.get(6);
            if (var2 < var7) {
               if (var7 <= var4) {
                  --var1;
               }
            } else {
               var5.set(1, var1 + 1);
               var5.set(6, 1);
               var5.complete();
               int var8 = this.getFirstDayOfWeek() - var5.get(7);
               if (var8 != 0) {
                  if (var8 < 0) {
                     var8 += 7;
                  }

                  var5.add(6, var8);
               }

               var7 = var5.get(6) - 1;
               if (var7 == 0) {
                  var7 = 7;
               }

               if (var7 >= var4) {
                  int var9 = var3 - var2 + 1;
                  if (var9 <= 7 - var7) {
                     ++var1;
                  }
               }
            }

            return var1;
         }
      }
   }

   public void setWeekDate(int var1, int var2, int var3) {
      if (var3 >= 1 && var3 <= 7) {
         GregorianCalendar var4 = (GregorianCalendar)this.clone();
         var4.setLenient(true);
         int var5 = var4.get(0);
         var4.clear();
         var4.setTimeZone(TimeZone.getTimeZone("GMT"));
         var4.set(0, var5);
         var4.set(1, var1);
         var4.set(3, 1);
         var4.set(7, this.getFirstDayOfWeek());
         int var6 = var3 - this.getFirstDayOfWeek();
         if (var6 < 0) {
            var6 += 7;
         }

         var6 += 7 * (var2 - 1);
         if (var6 != 0) {
            var4.add(6, var6);
         } else {
            var4.complete();
         }

         if (this.isLenient() || var4.getWeekYear() == var1 && var4.internalGet(3) == var2 && var4.internalGet(7) == var3) {
            this.set(0, var4.internalGet(0));
            this.set(1, var4.internalGet(1));
            this.set(2, var4.internalGet(2));
            this.set(5, var4.internalGet(5));
            this.internalSet(3, var2);
            this.complete();
         } else {
            throw new IllegalArgumentException();
         }
      } else {
         throw new IllegalArgumentException("invalid dayOfWeek: " + var3);
      }
   }

   public int getWeeksInWeekYear() {
      GregorianCalendar var1 = this.getNormalizedCalendar();
      int var2 = var1.getWeekYear();
      if (var2 == var1.internalGet(1)) {
         return var1.getActualMaximum(3);
      } else {
         if (var1 == this) {
            var1 = (GregorianCalendar)var1.clone();
         }

         var1.setWeekDate(var2, 2, this.internalGet(7));
         return var1.getActualMaximum(3);
      }
   }

   protected void computeFields() {
      int var1;
      if (this.isPartiallyNormalized()) {
         var1 = this.getSetStateFields();
         int var2 = ~var1 & 131071;
         if (var2 != 0 || this.calsys == null) {
            var1 |= this.computeFields(var2, var1 & 98304);

            assert var1 == 131071;
         }
      } else {
         var1 = 131071;
         this.computeFields(var1, 0);
      }

      this.setFieldsComputed(var1);
   }

   private int computeFields(int var1, int var2) {
      int var3 = 0;
      TimeZone var4 = this.getZone();
      if (this.zoneOffsets == null) {
         this.zoneOffsets = new int[2];
      }

      if (var2 != 98304) {
         if (var4 instanceof ZoneInfo) {
            var3 = ((ZoneInfo)var4).getOffsets(this.time, this.zoneOffsets);
         } else {
            var3 = var4.getOffset(this.time);
            this.zoneOffsets[0] = var4.getRawOffset();
            this.zoneOffsets[1] = var3 - this.zoneOffsets[0];
         }
      }

      if (var2 != 0) {
         if (isFieldSet(var2, 15)) {
            this.zoneOffsets[0] = this.internalGet(15);
         }

         if (isFieldSet(var2, 16)) {
            this.zoneOffsets[1] = this.internalGet(16);
         }

         var3 = this.zoneOffsets[0] + this.zoneOffsets[1];
      }

      long var5 = (long)var3 / 86400000L;
      int var7 = var3 % 86400000;
      var5 += this.time / 86400000L;
      var7 += (int)(this.time % 86400000L);
      if ((long)var7 >= 86400000L) {
         var7 = (int)((long)var7 - 86400000L);
         ++var5;
      } else {
         while(var7 < 0) {
            var7 = (int)((long)var7 + 86400000L);
            --var5;
         }
      }

      var5 += 719163L;
      byte var8 = 1;
      int var9;
      if (var5 >= this.gregorianCutoverDate) {
         assert this.cachedFixedDate == Long.MIN_VALUE || this.gdate.isNormalized() : "cache control: not normalized";

         assert this.cachedFixedDate == Long.MIN_VALUE || gcal.getFixedDate(this.gdate.getNormalizedYear(), this.gdate.getMonth(), this.gdate.getDayOfMonth(), this.gdate) == this.cachedFixedDate : "cache control: inconsictency, cachedFixedDate=" + this.cachedFixedDate + ", computed=" + gcal.getFixedDate(this.gdate.getNormalizedYear(), this.gdate.getMonth(), this.gdate.getDayOfMonth(), this.gdate) + ", date=" + this.gdate;

         if (var5 != this.cachedFixedDate) {
            gcal.getCalendarDateFromFixedDate(this.gdate, var5);
            this.cachedFixedDate = var5;
         }

         var9 = this.gdate.getYear();
         if (var9 <= 0) {
            var9 = 1 - var9;
            var8 = 0;
         }

         this.calsys = gcal;
         this.cdate = this.gdate;

         assert this.cdate.getDayOfWeek() > 0 : "dow=" + this.cdate.getDayOfWeek() + ", date=" + this.cdate;
      } else {
         this.calsys = getJulianCalendarSystem();
         this.cdate = jcal.newCalendarDate(this.getZone());
         jcal.getCalendarDateFromFixedDate(this.cdate, var5);
         Era var10 = this.cdate.getEra();
         if (var10 == jeras[0]) {
            var8 = 0;
         }

         var9 = this.cdate.getYear();
      }

      this.internalSet(0, var8);
      this.internalSet(1, var9);
      int var30 = var1 | 3;
      int var11 = this.cdate.getMonth() - 1;
      int var12 = this.cdate.getDayOfMonth();
      if ((var1 & 164) != 0) {
         this.internalSet(2, var11);
         this.internalSet(5, var12);
         this.internalSet(7, this.cdate.getDayOfWeek());
         var30 |= 164;
      }

      int var13;
      if ((var1 & 32256) != 0) {
         if (var7 != 0) {
            var13 = var7 / 3600000;
            this.internalSet(11, var13);
            this.internalSet(9, var13 / 12);
            this.internalSet(10, var13 % 12);
            int var14 = var7 % 3600000;
            this.internalSet(12, var14 / '\uea60');
            var14 %= 60000;
            this.internalSet(13, var14 / 1000);
            this.internalSet(14, var14 % 1000);
         } else {
            this.internalSet(11, 0);
            this.internalSet(9, 0);
            this.internalSet(10, 0);
            this.internalSet(12, 0);
            this.internalSet(13, 0);
            this.internalSet(14, 0);
         }

         var30 |= 32256;
      }

      if ((var1 & 98304) != 0) {
         this.internalSet(15, this.zoneOffsets[0]);
         this.internalSet(16, this.zoneOffsets[1]);
         var30 |= 98304;
      }

      if ((var1 & 344) != 0) {
         var13 = this.cdate.getNormalizedYear();
         long var31 = this.calsys.getFixedDate(var13, 1, 1, this.cdate);
         int var16 = (int)(var5 - var31) + 1;
         long var17 = var5 - (long)var12 + 1L;
         boolean var19 = false;
         int var20 = this.calsys == gcal ? this.gregorianCutoverYear : this.gregorianCutoverYearJulian;
         int var21 = var12 - 1;
         int var22;
         if (var13 == var20) {
            if (this.gregorianCutoverYearJulian <= this.gregorianCutoverYear) {
               var31 = this.getFixedDateJan1(this.cdate, var5);
               if (var5 >= this.gregorianCutoverDate) {
                  var17 = this.getFixedDateMonth1(this.cdate, var5);
               }
            }

            var22 = (int)(var5 - var31) + 1;
            int var10000 = var16 - var22;
            var16 = var22;
            var21 = (int)(var5 - var17);
         }

         this.internalSet(6, var16);
         this.internalSet(8, var21 / 7 + 1);
         var22 = this.getWeekNumber(var31, var5);
         long var23;
         long var25;
         if (var22 == 0) {
            var23 = var31 - 1L;
            var25 = var31 - 365L;
            if (var13 > var20 + 1) {
               if (CalendarUtils.isGregorianLeapYear(var13 - 1)) {
                  --var25;
               }
            } else if (var13 <= this.gregorianCutoverYearJulian) {
               if (CalendarUtils.isJulianLeapYear(var13 - 1)) {
                  --var25;
               }
            } else {
               BaseCalendar var35 = this.calsys;
               int var28 = this.getCalendarDate(var23).getNormalizedYear();
               if (var28 == this.gregorianCutoverYear) {
                  var35 = this.getCutoverCalendarSystem();
                  if (var35 == jcal) {
                     var25 = var35.getFixedDate(var28, 1, 1, (BaseCalendar.Date)null);
                  } else {
                     var25 = this.gregorianCutoverDate;
                     Gregorian var36 = gcal;
                  }
               } else if (var28 <= this.gregorianCutoverYearJulian) {
                  var35 = getJulianCalendarSystem();
                  var25 = var35.getFixedDate(var28, 1, 1, (BaseCalendar.Date)null);
               }
            }

            var22 = this.getWeekNumber(var25, var23);
         } else if (var13 <= this.gregorianCutoverYear && var13 >= this.gregorianCutoverYearJulian - 1) {
            BaseCalendar var32 = this.calsys;
            int var24 = var13 + 1;
            if (var24 == this.gregorianCutoverYearJulian + 1 && var24 < this.gregorianCutoverYear) {
               var24 = this.gregorianCutoverYear;
            }

            if (var24 == this.gregorianCutoverYear) {
               var32 = this.getCutoverCalendarSystem();
            }

            if (var24 <= this.gregorianCutoverYear && this.gregorianCutoverYearJulian != this.gregorianCutoverYear && var24 != this.gregorianCutoverYearJulian) {
               var25 = this.gregorianCutoverDate;
               Gregorian var33 = gcal;
            } else {
               var25 = var32.getFixedDate(var24, 1, 1, (BaseCalendar.Date)null);
            }

            long var34 = BaseCalendar.getDayOfWeekDateOnOrBefore(var25 + 6L, this.getFirstDayOfWeek());
            int var29 = (int)(var34 - var25);
            if (var29 >= this.getMinimalDaysInFirstWeek() && var5 >= var34 - 7L) {
               var22 = 1;
            }
         } else if (var22 >= 52) {
            var23 = var31 + 365L;
            if (this.cdate.isLeapYear()) {
               ++var23;
            }

            var25 = BaseCalendar.getDayOfWeekDateOnOrBefore(var23 + 6L, this.getFirstDayOfWeek());
            int var27 = (int)(var25 - var23);
            if (var27 >= this.getMinimalDaysInFirstWeek() && var5 >= var25 - 7L) {
               var22 = 1;
            }
         }

         this.internalSet(3, var22);
         this.internalSet(4, this.getWeekNumber(var17, var5));
         var30 |= 344;
      }

      return var30;
   }

   private int getWeekNumber(long var1, long var3) {
      long var5 = Gregorian.getDayOfWeekDateOnOrBefore(var1 + 6L, this.getFirstDayOfWeek());
      int var7 = (int)(var5 - var1);

      assert var7 <= 7;

      if (var7 >= this.getMinimalDaysInFirstWeek()) {
         var5 -= 7L;
      }

      int var8 = (int)(var3 - var5);
      return var8 >= 0 ? var8 / 7 + 1 : CalendarUtils.floorDivide(var8, 7) + 1;
   }

   protected void computeTime() {
      int var1;
      int var2;
      if (!this.isLenient()) {
         if (this.originalFields == null) {
            this.originalFields = new int[17];
         }

         for(var1 = 0; var1 < 17; ++var1) {
            var2 = this.internalGet(var1);
            if (this.isExternallySet(var1) && (var2 < this.getMinimum(var1) || var2 > this.getMaximum(var1))) {
               throw new IllegalArgumentException(getFieldName(var1));
            }

            this.originalFields[var1] = var2;
         }
      }

      var1 = this.selectFields();
      var2 = this.isSet(1) ? this.internalGet(1) : 1970;
      int var3 = this.internalGetEra();
      if (var3 == 0) {
         var2 = 1 - var2;
      } else if (var3 != 1) {
         throw new IllegalArgumentException("Invalid era");
      }

      if (var2 <= 0 && !this.isSet(0)) {
         var1 |= 1;
         this.setFieldsComputed(1);
      }

      long var4 = 0L;
      if (isFieldSet(var1, 11)) {
         var4 += (long)this.internalGet(11);
      } else {
         var4 += (long)this.internalGet(10);
         if (isFieldSet(var1, 9)) {
            var4 += (long)(12 * this.internalGet(9));
         }
      }

      var4 *= 60L;
      var4 += (long)this.internalGet(12);
      var4 *= 60L;
      var4 += (long)this.internalGet(13);
      var4 *= 1000L;
      var4 += (long)this.internalGet(14);
      long var6 = var4 / 86400000L;

      for(var4 %= 86400000L; var4 < 0L; --var6) {
         var4 += 86400000L;
      }

      long var8;
      label187: {
         long var10;
         if (var2 > this.gregorianCutoverYear && var2 > this.gregorianCutoverYearJulian) {
            var8 = var6 + this.getFixedDate(gcal, var2, var1);
            if (var8 >= this.gregorianCutoverDate) {
               var6 = var8;
               break label187;
            }

            var10 = var6 + this.getFixedDate(getJulianCalendarSystem(), var2, var1);
         } else if (var2 < this.gregorianCutoverYear && var2 < this.gregorianCutoverYearJulian) {
            var10 = var6 + this.getFixedDate(getJulianCalendarSystem(), var2, var1);
            if (var10 < this.gregorianCutoverDate) {
               var6 = var10;
               break label187;
            }

            var8 = var10;
         } else {
            var10 = var6 + this.getFixedDate(getJulianCalendarSystem(), var2, var1);
            var8 = var6 + this.getFixedDate(gcal, var2, var1);
         }

         if (isFieldSet(var1, 6) || isFieldSet(var1, 3)) {
            if (this.gregorianCutoverYear == this.gregorianCutoverYearJulian) {
               var6 = var10;
               break label187;
            }

            if (var2 == this.gregorianCutoverYear) {
               var6 = var8;
               break label187;
            }
         }

         if (var8 >= this.gregorianCutoverDate) {
            if (var10 >= this.gregorianCutoverDate) {
               var6 = var8;
            } else if (this.calsys != gcal && this.calsys != null) {
               var6 = var10;
            } else {
               var6 = var8;
            }
         } else if (var10 < this.gregorianCutoverDate) {
            var6 = var10;
         } else {
            if (!this.isLenient()) {
               throw new IllegalArgumentException("the specified date doesn't exist");
            }

            var6 = var10;
         }
      }

      var8 = (var6 - 719163L) * 86400000L + var4;
      TimeZone var15 = this.getZone();
      if (this.zoneOffsets == null) {
         this.zoneOffsets = new int[2];
      }

      int var11 = var1 & 98304;
      int var12;
      if (var11 != 98304) {
         if (var15 instanceof ZoneInfo) {
            ((ZoneInfo)var15).getOffsetsByWall(var8, this.zoneOffsets);
         } else {
            var12 = isFieldSet(var1, 15) ? this.internalGet(15) : var15.getRawOffset();
            var15.getOffsets(var8 - (long)var12, this.zoneOffsets);
         }
      }

      if (var11 != 0) {
         if (isFieldSet(var11, 15)) {
            this.zoneOffsets[0] = this.internalGet(15);
         }

         if (isFieldSet(var11, 16)) {
            this.zoneOffsets[1] = this.internalGet(16);
         }
      }

      var8 -= (long)(this.zoneOffsets[0] + this.zoneOffsets[1]);
      this.time = var8;
      var12 = this.computeFields(var1 | this.getSetStateFields(), var11);
      if (!this.isLenient()) {
         for(int var13 = 0; var13 < 17; ++var13) {
            if (this.isExternallySet(var13) && this.originalFields[var13] != this.internalGet(var13)) {
               String var14 = this.originalFields[var13] + " -> " + this.internalGet(var13);
               System.arraycopy(this.originalFields, 0, this.fields, 0, this.fields.length);
               throw new IllegalArgumentException(getFieldName(var13) + ": " + var14);
            }
         }
      }

      this.setFieldsNormalized(var12);
   }

   private long getFixedDate(BaseCalendar var1, int var2, int var3) {
      int var4 = 0;
      if (isFieldSet(var3, 2)) {
         var4 = this.internalGet(2);
         if (var4 > 11) {
            var2 += var4 / 12;
            var4 %= 12;
         } else if (var4 < 0) {
            int[] var5 = new int[1];
            var2 += CalendarUtils.floorDivide(var4, 12, var5);
            var4 = var5[0];
         }
      }

      long var10 = var1.getFixedDate(var2, var4 + 1, 1, var1 == gcal ? this.gdate : null);
      long var7;
      int var9;
      if (isFieldSet(var3, 2)) {
         if (isFieldSet(var3, 5)) {
            if (this.isSet(5)) {
               var10 += (long)this.internalGet(5);
               --var10;
            }
         } else if (isFieldSet(var3, 4)) {
            var7 = BaseCalendar.getDayOfWeekDateOnOrBefore(var10 + 6L, this.getFirstDayOfWeek());
            if (var7 - var10 >= (long)this.getMinimalDaysInFirstWeek()) {
               var7 -= 7L;
            }

            if (isFieldSet(var3, 7)) {
               var7 = BaseCalendar.getDayOfWeekDateOnOrBefore(var7 + 6L, this.internalGet(7));
            }

            var10 = var7 + (long)(7 * (this.internalGet(4) - 1));
         } else {
            int var11;
            if (isFieldSet(var3, 7)) {
               var11 = this.internalGet(7);
            } else {
               var11 = this.getFirstDayOfWeek();
            }

            int var8;
            if (isFieldSet(var3, 8)) {
               var8 = this.internalGet(8);
            } else {
               var8 = 1;
            }

            if (var8 >= 0) {
               var10 = BaseCalendar.getDayOfWeekDateOnOrBefore(var10 + (long)(7 * var8) - 1L, var11);
            } else {
               var9 = this.monthLength(var4, var2) + 7 * (var8 + 1);
               var10 = BaseCalendar.getDayOfWeekDateOnOrBefore(var10 + (long)var9 - 1L, var11);
            }
         }
      } else {
         if (var2 == this.gregorianCutoverYear && var1 == gcal && var10 < this.gregorianCutoverDate && this.gregorianCutoverYear != this.gregorianCutoverYearJulian) {
            var10 = this.gregorianCutoverDate;
         }

         if (isFieldSet(var3, 6)) {
            var10 += (long)this.internalGet(6);
            --var10;
         } else {
            var7 = BaseCalendar.getDayOfWeekDateOnOrBefore(var10 + 6L, this.getFirstDayOfWeek());
            if (var7 - var10 >= (long)this.getMinimalDaysInFirstWeek()) {
               var7 -= 7L;
            }

            if (isFieldSet(var3, 7)) {
               var9 = this.internalGet(7);
               if (var9 != this.getFirstDayOfWeek()) {
                  var7 = BaseCalendar.getDayOfWeekDateOnOrBefore(var7 + 6L, var9);
               }
            }

            var10 = var7 + 7L * ((long)this.internalGet(3) - 1L);
         }
      }

      return var10;
   }

   private GregorianCalendar getNormalizedCalendar() {
      GregorianCalendar var1;
      if (this.isFullyNormalized()) {
         var1 = this;
      } else {
         var1 = (GregorianCalendar)this.clone();
         var1.setLenient(true);
         var1.complete();
      }

      return var1;
   }

   private static synchronized BaseCalendar getJulianCalendarSystem() {
      if (jcal == null) {
         jcal = (JulianCalendar)CalendarSystem.forName("julian");
         jeras = jcal.getEras();
      }

      return jcal;
   }

   private BaseCalendar getCutoverCalendarSystem() {
      return (BaseCalendar)(this.gregorianCutoverYearJulian < this.gregorianCutoverYear ? gcal : getJulianCalendarSystem());
   }

   private boolean isCutoverYear(int var1) {
      int var2 = this.calsys == gcal ? this.gregorianCutoverYear : this.gregorianCutoverYearJulian;
      return var1 == var2;
   }

   private long getFixedDateJan1(BaseCalendar.Date var1, long var2) {
      assert var1.getNormalizedYear() == this.gregorianCutoverYear || var1.getNormalizedYear() == this.gregorianCutoverYearJulian;

      if (this.gregorianCutoverYear != this.gregorianCutoverYearJulian && var2 >= this.gregorianCutoverDate) {
         return this.gregorianCutoverDate;
      } else {
         BaseCalendar var4 = getJulianCalendarSystem();
         return var4.getFixedDate(var1.getNormalizedYear(), 1, 1, (BaseCalendar.Date)null);
      }
   }

   private long getFixedDateMonth1(BaseCalendar.Date var1, long var2) {
      assert var1.getNormalizedYear() == this.gregorianCutoverYear || var1.getNormalizedYear() == this.gregorianCutoverYearJulian;

      BaseCalendar.Date var4 = this.getGregorianCutoverDate();
      if (var4.getMonth() == 1 && var4.getDayOfMonth() == 1) {
         return var2 - (long)var1.getDayOfMonth() + 1L;
      } else {
         long var5;
         if (var1.getMonth() == var4.getMonth()) {
            BaseCalendar.Date var7 = this.getLastJulianDate();
            if (this.gregorianCutoverYear == this.gregorianCutoverYearJulian && var4.getMonth() == var7.getMonth()) {
               var5 = jcal.getFixedDate(var1.getNormalizedYear(), var1.getMonth(), 1, (BaseCalendar.Date)null);
            } else {
               var5 = this.gregorianCutoverDate;
            }
         } else {
            var5 = var2 - (long)var1.getDayOfMonth() + 1L;
         }

         return var5;
      }
   }

   private BaseCalendar.Date getCalendarDate(long var1) {
      Object var3 = var1 >= this.gregorianCutoverDate ? gcal : getJulianCalendarSystem();
      BaseCalendar.Date var4 = (BaseCalendar.Date)((BaseCalendar)var3).newCalendarDate(TimeZone.NO_TIMEZONE);
      ((BaseCalendar)var3).getCalendarDateFromFixedDate(var4, var1);
      return var4;
   }

   private BaseCalendar.Date getGregorianCutoverDate() {
      return this.getCalendarDate(this.gregorianCutoverDate);
   }

   private BaseCalendar.Date getLastJulianDate() {
      return this.getCalendarDate(this.gregorianCutoverDate - 1L);
   }

   private int monthLength(int var1, int var2) {
      return this.isLeapYear(var2) ? LEAP_MONTH_LENGTH[var1] : MONTH_LENGTH[var1];
   }

   private int monthLength(int var1) {
      int var2 = this.internalGet(1);
      if (this.internalGetEra() == 0) {
         var2 = 1 - var2;
      }

      return this.monthLength(var1, var2);
   }

   private int actualMonthLength() {
      int var1 = this.cdate.getNormalizedYear();
      if (var1 != this.gregorianCutoverYear && var1 != this.gregorianCutoverYearJulian) {
         return this.calsys.getMonthLength(this.cdate);
      } else {
         Object var2 = (BaseCalendar.Date)this.cdate.clone();
         long var3 = this.calsys.getFixedDate((CalendarDate)var2);
         long var5 = this.getFixedDateMonth1((BaseCalendar.Date)var2, var3);
         long var7 = var5 + (long)this.calsys.getMonthLength((CalendarDate)var2);
         if (var7 < this.gregorianCutoverDate) {
            return (int)(var7 - var5);
         } else {
            if (this.cdate != this.gdate) {
               var2 = gcal.newCalendarDate(TimeZone.NO_TIMEZONE);
            }

            gcal.getCalendarDateFromFixedDate((CalendarDate)var2, var7);
            var7 = this.getFixedDateMonth1((BaseCalendar.Date)var2, var7);
            return (int)(var7 - var5);
         }
      }
   }

   private int yearLength(int var1) {
      return this.isLeapYear(var1) ? 366 : 365;
   }

   private int yearLength() {
      int var1 = this.internalGet(1);
      if (this.internalGetEra() == 0) {
         var1 = 1 - var1;
      }

      return this.yearLength(var1);
   }

   private void pinDayOfMonth() {
      int var1 = this.internalGet(1);
      int var2;
      if (var1 <= this.gregorianCutoverYear && var1 >= this.gregorianCutoverYearJulian) {
         GregorianCalendar var3 = this.getNormalizedCalendar();
         var2 = var3.getActualMaximum(5);
      } else {
         var2 = this.monthLength(this.internalGet(2));
      }

      int var4 = this.internalGet(5);
      if (var4 > var2) {
         this.set(5, var2);
      }

   }

   private long getCurrentFixedDate() {
      return this.calsys == gcal ? this.cachedFixedDate : this.calsys.getFixedDate(this.cdate);
   }

   private static int getRolledValue(int var0, int var1, int var2, int var3) {
      assert var0 >= var2 && var0 <= var3;

      int var4 = var3 - var2 + 1;
      var1 %= var4;
      int var5 = var0 + var1;
      if (var5 > var3) {
         var5 -= var4;
      } else if (var5 < var2) {
         var5 += var4;
      }

      assert var5 >= var2 && var5 <= var3;

      return var5;
   }

   private int internalGetEra() {
      return this.isSet(0) ? this.internalGet(0) : 1;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      if (this.gdate == null) {
         this.gdate = gcal.newCalendarDate(this.getZone());
         this.cachedFixedDate = Long.MIN_VALUE;
      }

      this.setGregorianChange(this.gregorianCutover);
   }

   public ZonedDateTime toZonedDateTime() {
      return ZonedDateTime.ofInstant(Instant.ofEpochMilli(this.getTimeInMillis()), this.getTimeZone().toZoneId());
   }

   public static GregorianCalendar from(ZonedDateTime var0) {
      GregorianCalendar var1 = new GregorianCalendar(TimeZone.getTimeZone(var0.getZone()));
      var1.setGregorianChange(new Date(Long.MIN_VALUE));
      var1.setFirstDayOfWeek(2);
      var1.setMinimalDaysInFirstWeek(4);

      try {
         var1.setTimeInMillis(Math.addExact(Math.multiplyExact(var0.toEpochSecond(), 1000L), (long)var0.get(ChronoField.MILLI_OF_SECOND)));
         return var1;
      } catch (ArithmeticException var3) {
         throw new IllegalArgumentException(var3);
      }
   }
}
