package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import sun.util.calendar.BaseCalendar;
import sun.util.calendar.CalendarDate;
import sun.util.calendar.CalendarSystem;
import sun.util.calendar.CalendarUtils;
import sun.util.calendar.Era;
import sun.util.calendar.Gregorian;
import sun.util.calendar.LocalGregorianCalendar;
import sun.util.calendar.ZoneInfo;
import sun.util.locale.provider.CalendarDataUtility;

class JapaneseImperialCalendar extends Calendar {
   public static final int BEFORE_MEIJI = 0;
   public static final int MEIJI = 1;
   public static final int TAISHO = 2;
   public static final int SHOWA = 3;
   public static final int HEISEI = 4;
   private static final int EPOCH_OFFSET = 719163;
   private static final int EPOCH_YEAR = 1970;
   private static final int ONE_SECOND = 1000;
   private static final int ONE_MINUTE = 60000;
   private static final int ONE_HOUR = 3600000;
   private static final long ONE_DAY = 86400000L;
   private static final long ONE_WEEK = 604800000L;
   private static final LocalGregorianCalendar jcal = (LocalGregorianCalendar)CalendarSystem.forName("japanese");
   private static final Gregorian gcal = CalendarSystem.getGregorianCalendar();
   private static final Era BEFORE_MEIJI_ERA = new Era("BeforeMeiji", "BM", Long.MIN_VALUE, false);
   private static final Era[] eras;
   private static final long[] sinceFixedDates;
   static final int[] MIN_VALUES = new int[]{0, -292275055, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, -46800000, 0};
   static final int[] LEAST_MAX_VALUES = new int[]{0, 0, 0, 0, 4, 28, 0, 7, 4, 1, 11, 23, 59, 59, 999, 50400000, 1200000};
   static final int[] MAX_VALUES = new int[]{0, 292278994, 11, 53, 6, 31, 366, 7, 6, 1, 11, 23, 59, 59, 999, 50400000, 7200000};
   private static final long serialVersionUID = -3364572813905467929L;
   private transient LocalGregorianCalendar.Date jdate;
   private transient int[] zoneOffsets;
   private transient int[] originalFields;
   private transient long cachedFixedDate = Long.MIN_VALUE;

   JapaneseImperialCalendar(TimeZone var1, Locale var2) {
      super(var1, var2);
      this.jdate = jcal.newCalendarDate(var1);
      this.setTimeInMillis(System.currentTimeMillis());
   }

   JapaneseImperialCalendar(TimeZone var1, Locale var2, boolean var3) {
      super(var1, var2);
      this.jdate = jcal.newCalendarDate(var1);
   }

   public String getCalendarType() {
      return "japanese";
   }

   public boolean equals(Object var1) {
      return var1 instanceof JapaneseImperialCalendar && super.equals(var1);
   }

   public int hashCode() {
      return super.hashCode() ^ this.jdate.hashCode();
   }

   public void add(int var1, int var2) {
      if (var2 != 0) {
         if (var1 >= 0 && var1 < 15) {
            this.complete();
            LocalGregorianCalendar.Date var3;
            if (var1 == 1) {
               var3 = (LocalGregorianCalendar.Date)this.jdate.clone();
               var3.addYear(var2);
               this.pinDayOfMonth(var3);
               this.set(0, getEraIndex(var3));
               this.set(1, var3.getYear());
               this.set(2, var3.getMonth() - 1);
               this.set(5, var3.getDayOfMonth());
            } else if (var1 == 2) {
               var3 = (LocalGregorianCalendar.Date)this.jdate.clone();
               var3.addMonth(var2);
               this.pinDayOfMonth(var3);
               this.set(0, getEraIndex(var3));
               this.set(1, var3.getYear());
               this.set(2, var3.getMonth() - 1);
               this.set(5, var3.getDayOfMonth());
            } else if (var1 == 0) {
               int var12 = this.internalGet(0) + var2;
               if (var12 < 0) {
                  var12 = 0;
               } else if (var12 > eras.length - 1) {
                  var12 = eras.length - 1;
               }

               this.set(0, var12);
            } else {
               long var13 = (long)var2;
               long var5 = 0L;
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
                  var5 = (long)(12 * (var2 % 2));
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

               long var7 = this.cachedFixedDate;
               var5 += (long)this.internalGet(11);
               var5 *= 60L;
               var5 += (long)this.internalGet(12);
               var5 *= 60L;
               var5 += (long)this.internalGet(13);
               var5 *= 1000L;
               var5 += (long)this.internalGet(14);
               if (var5 >= 86400000L) {
                  ++var7;
                  var5 -= 86400000L;
               } else if (var5 < 0L) {
                  --var7;
                  var5 += 86400000L;
               }

               var7 += var13;
               int var9 = this.internalGet(15) + this.internalGet(16);
               this.setTimeInMillis((var7 - 719163L) * 86400000L + var5 - (long)var9);
               var9 -= this.internalGet(15) + this.internalGet(16);
               if (var9 != 0) {
                  this.setTimeInMillis(this.time + (long)var9);
                  long var10 = this.cachedFixedDate;
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
            this.complete();
            int var3 = this.getMinimum(var1);
            int var4 = this.getMaximum(var1);
            int var5;
            int var6;
            int var7;
            LocalGregorianCalendar.Date var8;
            int var9;
            int var10;
            int var11;
            LocalGregorianCalendar.Date var13;
            long var17;
            long var18;
            long var20;
            int var21;
            long var22;
            long var23;
            switch(var1) {
            case 0:
            case 9:
            case 12:
            case 13:
            case 14:
            default:
               break;
            case 1:
               var3 = this.getActualMinimum(var1);
               var4 = this.getActualMaximum(var1);
               break;
            case 2:
               if (!this.isTransitionYear(this.jdate.getNormalizedYear())) {
                  var5 = this.jdate.getYear();
                  LocalGregorianCalendar.Date var24;
                  LocalGregorianCalendar.Date var25;
                  if (var5 == this.getMaximum(1)) {
                     var25 = jcal.getCalendarDate(this.time, this.getZone());
                     var24 = jcal.getCalendarDate(Long.MAX_VALUE, this.getZone());
                     var4 = var24.getMonth() - 1;
                     var21 = getRolledValue(this.internalGet(var1), var2, var3, var4);
                     if (var21 == var4) {
                        var25.addYear(-400);
                        var25.setMonth(var21 + 1);
                        if (var25.getDayOfMonth() > var24.getDayOfMonth()) {
                           var25.setDayOfMonth(var24.getDayOfMonth());
                           jcal.normalize(var25);
                        }

                        if (var25.getDayOfMonth() == var24.getDayOfMonth() && var25.getTimeOfDay() > var24.getTimeOfDay()) {
                           var25.setMonth(var21 + 1);
                           var25.setDayOfMonth(var24.getDayOfMonth() - 1);
                           jcal.normalize(var25);
                           var21 = var25.getMonth() - 1;
                        }

                        this.set(5, var25.getDayOfMonth());
                     }

                     this.set(2, var21);
                  } else if (var5 == this.getMinimum(1)) {
                     var25 = jcal.getCalendarDate(this.time, this.getZone());
                     var24 = jcal.getCalendarDate(Long.MIN_VALUE, this.getZone());
                     var3 = var24.getMonth() - 1;
                     var21 = getRolledValue(this.internalGet(var1), var2, var3, var4);
                     if (var21 == var3) {
                        var25.addYear(400);
                        var25.setMonth(var21 + 1);
                        if (var25.getDayOfMonth() < var24.getDayOfMonth()) {
                           var25.setDayOfMonth(var24.getDayOfMonth());
                           jcal.normalize(var25);
                        }

                        if (var25.getDayOfMonth() == var24.getDayOfMonth() && var25.getTimeOfDay() < var24.getTimeOfDay()) {
                           var25.setMonth(var21 + 1);
                           var25.setDayOfMonth(var24.getDayOfMonth() + 1);
                           jcal.normalize(var25);
                           var21 = var25.getMonth() - 1;
                        }

                        this.set(5, var25.getDayOfMonth());
                     }

                     this.set(2, var21);
                  } else {
                     var6 = (this.internalGet(2) + var2) % 12;
                     if (var6 < 0) {
                        var6 += 12;
                     }

                     this.set(2, var6);
                     var7 = this.monthLength(var6);
                     if (this.internalGet(5) > var7) {
                        this.set(5, var7);
                     }
                  }
               } else {
                  var5 = getEraIndex(this.jdate);
                  CalendarDate var28 = null;
                  if (this.jdate.getYear() == 1) {
                     var28 = eras[var5].getSinceDate();
                     var3 = var28.getMonth() - 1;
                  } else if (var5 < eras.length - 1) {
                     var28 = eras[var5 + 1].getSinceDate();
                     if (var28.getYear() == this.jdate.getNormalizedYear()) {
                        var4 = var28.getMonth() - 1;
                        if (var28.getDayOfMonth() == 1) {
                           --var4;
                        }
                     }
                  }

                  if (var3 == var4) {
                     return;
                  }

                  var7 = getRolledValue(this.internalGet(var1), var2, var3, var4);
                  this.set(2, var7);
                  if (var7 == var3) {
                     if ((var28.getMonth() != 1 || var28.getDayOfMonth() != 1) && this.jdate.getDayOfMonth() < var28.getDayOfMonth()) {
                        this.set(5, var28.getDayOfMonth());
                     }
                  } else if (var7 == var4 && var28.getMonth() - 1 == var7) {
                     var21 = var28.getDayOfMonth();
                     if (this.jdate.getDayOfMonth() >= var21) {
                        this.set(5, var21 - 1);
                     }
                  }
               }

               return;
            case 3:
               var5 = this.jdate.getNormalizedYear();
               var4 = this.getActualMaximum(3);
               this.set(7, this.internalGet(7));
               var6 = this.internalGet(3);
               var7 = var6 + var2;
               if (this.isTransitionYear(this.jdate.getNormalizedYear())) {
                  var22 = this.cachedFixedDate;
                  long var27 = var22 - (long)(7 * (var6 - var3));
                  LocalGregorianCalendar.Date var31 = getCalendarDate(var27);
                  if (var31.getEra() != this.jdate.getEra() || var31.getYear() != this.jdate.getYear()) {
                     ++var3;
                  }

                  var22 += (long)(7 * (var4 - var6));
                  jcal.getCalendarDateFromFixedDate(var31, var22);
                  if (var31.getEra() != this.jdate.getEra() || var31.getYear() != this.jdate.getYear()) {
                     --var4;
                  }

                  var7 = getRolledValue(var6, var2, var3, var4) - 1;
                  var31 = getCalendarDate(var27 + (long)(var7 * 7));
                  this.set(2, var31.getMonth() - 1);
                  this.set(5, var31.getDayOfMonth());
                  return;
               }

               var21 = this.jdate.getYear();
               if (var21 == this.getMaximum(1)) {
                  var4 = this.getActualMaximum(3);
               } else if (var21 == this.getMinimum(1)) {
                  var3 = this.getActualMinimum(3);
                  var4 = this.getActualMaximum(3);
                  if (var7 > var3 && var7 < var4) {
                     this.set(3, var7);
                     return;
                  }
               }

               if (var7 > var3 && var7 < var4) {
                  this.set(3, var7);
                  return;
               }

               var23 = this.cachedFixedDate;
               long var30 = var23 - (long)(7 * (var6 - var3));
               if (var21 != this.getMinimum(1)) {
                  if (gcal.getYearFromFixedDate(var30) != var5) {
                     ++var3;
                  }
               } else {
                  var13 = jcal.getCalendarDate(Long.MIN_VALUE, this.getZone());
                  if (var30 < jcal.getFixedDate(var13)) {
                     ++var3;
                  }
               }

               var23 += (long)(7 * (var4 - this.internalGet(3)));
               if (gcal.getYearFromFixedDate(var23) != var5) {
                  --var4;
               }
               break;
            case 4:
               boolean var19 = this.isTransitionYear(this.jdate.getNormalizedYear());
               var6 = this.internalGet(7) - this.getFirstDayOfWeek();
               if (var6 < 0) {
                  var6 += 7;
               }

               var20 = this.cachedFixedDate;
               if (var19) {
                  var23 = this.getFixedDateMonth1(this.jdate, var20);
                  var11 = this.actualMonthLength();
               } else {
                  var23 = var20 - (long)this.internalGet(5) + 1L;
                  var11 = jcal.getMonthLength(this.jdate);
               }

               long var29 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(var23 + 6L, this.getFirstDayOfWeek());
               if ((int)(var29 - var23) >= this.getMinimalDaysInFirstWeek()) {
                  var29 -= 7L;
               }

               var4 = this.getActualMaximum(var1);
               int var14 = getRolledValue(this.internalGet(var1), var2, 1, var4) - 1;
               long var15 = var29 + (long)(var14 * 7) + (long)var6;
               if (var15 < var23) {
                  var15 = var23;
               } else if (var15 >= var23 + (long)var11) {
                  var15 = var23 + (long)var11 - 1L;
               }

               this.set(5, (int)(var15 - var23) + 1);
               return;
            case 5:
               if (this.isTransitionYear(this.jdate.getNormalizedYear())) {
                  var17 = this.getFixedDateMonth1(this.jdate, this.cachedFixedDate);
                  var7 = getRolledValue((int)(this.cachedFixedDate - var17), var2, 0, this.actualMonthLength() - 1);
                  var8 = getCalendarDate(var17 + (long)var7);
                  if ($assertionsDisabled || getEraIndex(var8) == this.internalGetEra() && var8.getYear() == this.internalGet(1) && var8.getMonth() - 1 == this.internalGet(2)) {
                     this.set(5, var8.getDayOfMonth());
                     return;
                  }

                  throw new AssertionError();
               }

               var4 = jcal.getMonthLength(this.jdate);
               break;
            case 6:
               var4 = this.getActualMaximum(var1);
               if (this.isTransitionYear(this.jdate.getNormalizedYear())) {
                  var5 = getRolledValue(this.internalGet(6), var2, var3, var4);
                  var18 = this.cachedFixedDate - (long)this.internalGet(6);
                  var8 = getCalendarDate(var18 + (long)var5);
                  if ($assertionsDisabled || getEraIndex(var8) == this.internalGetEra() && var8.getYear() == this.internalGet(1)) {
                     this.set(2, var8.getMonth() - 1);
                     this.set(5, var8.getDayOfMonth());
                     return;
                  }

                  throw new AssertionError();
               }
               break;
            case 7:
               var5 = this.jdate.getNormalizedYear();
               if (!this.isTransitionYear(var5) && !this.isTransitionYear(var5 - 1)) {
                  var6 = this.internalGet(3);
                  if (var6 > 1 && var6 < 52) {
                     this.set(3, this.internalGet(3));
                     var4 = 7;
                     break;
                  }
               }

               var2 %= 7;
               if (var2 == 0) {
                  return;
               }

               var18 = this.cachedFixedDate;
               var22 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(var18, this.getFirstDayOfWeek());
               var18 += (long)var2;
               if (var18 < var22) {
                  var18 += 7L;
               } else if (var18 >= var22 + 7L) {
                  var18 -= 7L;
               }

               LocalGregorianCalendar.Date var26 = getCalendarDate(var18);
               this.set(0, getEraIndex(var26));
               this.set(var26.getYear(), var26.getMonth() - 1, var26.getDayOfMonth());
               return;
            case 8:
               var3 = 1;
               if (this.isTransitionYear(this.jdate.getNormalizedYear())) {
                  var17 = this.cachedFixedDate;
                  var20 = this.getFixedDateMonth1(this.jdate, var17);
                  var9 = this.actualMonthLength();
                  var10 = var9 % 7;
                  var4 = var9 / 7;
                  var11 = (int)(var17 - var20) % 7;
                  if (var11 < var10) {
                     ++var4;
                  }

                  int var12 = getRolledValue(this.internalGet(var1), var2, var3, var4) - 1;
                  var17 = var20 + (long)(var12 * 7) + (long)var11;
                  var13 = getCalendarDate(var17);
                  this.set(5, var13.getDayOfMonth());
                  return;
               }

               var5 = this.internalGet(5);
               var6 = jcal.getMonthLength(this.jdate);
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
               var8 = jcal.getCalendarDate(this.time, this.getZone());
               if (this.internalGet(5) != var8.getDayOfMonth()) {
                  var8.setEra(this.jdate.getEra());
                  var8.setDate(this.internalGet(1), this.internalGet(2) + 1, this.internalGet(5));
                  if (var1 == 10) {
                     assert this.internalGet(9) == 1;

                     var8.addHours(12);
                  }

                  this.time = jcal.getTime(var8);
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
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   public String getDisplayName(int var1, int var2, Locale var3) {
      if (!this.checkDisplayNameParams(var1, var2, 1, 4, var3, 647)) {
         return null;
      } else {
         int var4 = this.get(var1);
         if (var1 != 1 || this.getBaseStyle(var2) == 2 && var4 == 1 && this.get(0) != 0) {
            String var5 = CalendarDataUtility.retrieveFieldValueName(this.getCalendarType(), var1, var4, var2, var3);
            if (var5 == null && var1 == 0 && var4 < eras.length) {
               Era var6 = eras[var4];
               var5 = var2 == 1 ? var6.getAbbreviation() : var6.getName();
            }

            return var5;
         } else {
            return null;
         }
      }
   }

   public Map<String, Integer> getDisplayNames(int var1, int var2, Locale var3) {
      if (!this.checkDisplayNameParams(var1, var2, 0, 4, var3, 647)) {
         return null;
      } else {
         Map var4 = CalendarDataUtility.retrieveFieldValueNames(this.getCalendarType(), var1, var2, var3);
         if (var4 != null && var1 == 0) {
            int var5 = var4.size();
            if (var2 == 0) {
               HashSet var6 = new HashSet();
               Iterator var7 = var4.keySet().iterator();

               while(var7.hasNext()) {
                  String var8 = (String)var7.next();
                  var6.add(var4.get(var8));
               }

               var5 = var6.size();
            }

            if (var5 < eras.length) {
               int var9 = this.getBaseStyle(var2);

               for(int var10 = var5; var10 < eras.length; ++var10) {
                  Era var11 = eras[var10];
                  if (var9 == 0 || var9 == 1 || var9 == 4) {
                     var4.put(var11.getAbbreviation(), var10);
                  }

                  if (var9 == 0 || var9 == 2) {
                     var4.put(var11.getName(), var10);
                  }
               }
            }
         }

         return var4;
      }
   }

   public int getMinimum(int var1) {
      return MIN_VALUES[var1];
   }

   public int getMaximum(int var1) {
      switch(var1) {
      case 1:
         LocalGregorianCalendar.Date var2 = jcal.getCalendarDate(Long.MAX_VALUE, this.getZone());
         return Math.max(LEAST_MAX_VALUES[1], var2.getYear());
      default:
         return MAX_VALUES[var1];
      }
   }

   public int getGreatestMinimum(int var1) {
      return var1 == 1 ? 1 : MIN_VALUES[var1];
   }

   public int getLeastMaximum(int var1) {
      switch(var1) {
      case 1:
         return Math.min(LEAST_MAX_VALUES[1], this.getMaximum(1));
      default:
         return LEAST_MAX_VALUES[var1];
      }
   }

   public int getActualMinimum(int var1) {
      if (!isFieldSet(14, var1)) {
         return this.getMinimum(var1);
      } else {
         int var2 = 0;
         JapaneseImperialCalendar var3 = this.getNormalizedCalendar();
         LocalGregorianCalendar.Date var4 = jcal.getCalendarDate(var3.getTimeInMillis(), this.getZone());
         int var5 = getEraIndex(var4);
         LocalGregorianCalendar.Date var6;
         LocalGregorianCalendar.Date var8;
         long var14;
         switch(var1) {
         case 1:
            if (var5 > 0) {
               var2 = 1;
               var14 = eras[var5].getSince(this.getZone());
               var8 = jcal.getCalendarDate(var14, this.getZone());
               var4.setYear(var8.getYear());
               jcal.normalize(var4);

               assert var4.isLeapYear() == var8.isLeapYear();

               if (this.getYearOffsetInMillis(var4) < this.getYearOffsetInMillis(var8)) {
                  ++var2;
               }
            } else {
               var2 = this.getMinimum(var1);
               var6 = jcal.getCalendarDate(Long.MIN_VALUE, this.getZone());
               int var15 = var6.getYear();
               if (var15 > 400) {
                  var15 -= 400;
               }

               var4.setYear(var15);
               jcal.normalize(var4);
               if (this.getYearOffsetInMillis(var4) < this.getYearOffsetInMillis(var6)) {
                  ++var2;
               }
            }
            break;
         case 2:
            if (var5 > 1 && var4.getYear() == 1) {
               var14 = eras[var5].getSince(this.getZone());
               var8 = jcal.getCalendarDate(var14, this.getZone());
               var2 = var8.getMonth() - 1;
               if (var4.getDayOfMonth() < var8.getDayOfMonth()) {
                  ++var2;
               }
            }
            break;
         case 3:
            var2 = 1;
            var6 = jcal.getCalendarDate(Long.MIN_VALUE, this.getZone());
            var6.addYear(400);
            jcal.normalize(var6);
            var4.setEra(var6.getEra());
            var4.setYear(var6.getYear());
            jcal.normalize(var4);
            long var7 = jcal.getFixedDate(var6);
            long var9 = jcal.getFixedDate(var4);
            int var11 = this.getWeekNumber(var7, var9);
            long var12 = var9 - (long)(7 * (var11 - 1));
            if (var12 < var7 || var12 == var7 && var4.getTimeOfDay() < var6.getTimeOfDay()) {
               ++var2;
            }
         }

         return var2;
      }
   }

   public int getActualMaximum(int var1) {
      if ((130689 & 1 << var1) != 0) {
         return this.getMaximum(var1);
      } else {
         JapaneseImperialCalendar var3 = this.getNormalizedCalendar();
         LocalGregorianCalendar.Date var4 = var3.jdate;
         int var5 = var4.getNormalizedYear();
         boolean var6 = true;
         int var7;
         int var9;
         int var11;
         int var16;
         long var17;
         LocalGregorianCalendar.Date var18;
         LocalGregorianCalendar.Date var19;
         Gregorian.Date var20;
         long var21;
         long var22;
         int var23;
         long var24;
         switch(var1) {
         case 1:
            var18 = jcal.getCalendarDate(var3.getTimeInMillis(), this.getZone());
            var9 = getEraIndex(var4);
            if (var9 == eras.length - 1) {
               var19 = jcal.getCalendarDate(Long.MAX_VALUE, this.getZone());
               var16 = var19.getYear();
               if (var16 > 400) {
                  var18.setYear(var16 - 400);
               }
            } else {
               var19 = jcal.getCalendarDate(eras[var9 + 1].getSince(this.getZone()) - 1L, this.getZone());
               var16 = var19.getYear();
               var18.setYear(var16);
            }

            jcal.normalize(var18);
            if (this.getYearOffsetInMillis(var18) > this.getYearOffsetInMillis(var19)) {
               --var16;
            }
            break;
         case 2:
            var16 = 11;
            if (this.isTransitionYear(var4.getNormalizedYear())) {
               var7 = getEraIndex(var4);
               if (var4.getYear() != 1) {
                  ++var7;

                  assert var7 < eras.length;
               }

               var17 = sinceFixedDates[var7];
               var22 = var3.cachedFixedDate;
               if (var22 < var17) {
                  LocalGregorianCalendar.Date var25 = (LocalGregorianCalendar.Date)var4.clone();
                  jcal.getCalendarDateFromFixedDate(var25, var17 - 1L);
                  var16 = var25.getMonth() - 1;
               }
            } else {
               var18 = jcal.getCalendarDate(Long.MAX_VALUE, this.getZone());
               if (var4.getEra() == var18.getEra() && var4.getYear() == var18.getYear()) {
                  var16 = var18.getMonth() - 1;
               }
            }
            break;
         case 3:
            if (!this.isTransitionYear(var4.getNormalizedYear())) {
               var18 = jcal.getCalendarDate(Long.MAX_VALUE, this.getZone());
               if (var4.getEra() == var18.getEra() && var4.getYear() == var18.getYear()) {
                  var17 = jcal.getFixedDate(var18);
                  var22 = this.getFixedDateJan1(var18, var17);
                  var16 = this.getWeekNumber(var22, var17);
               } else if (var4.getEra() == null && var4.getYear() == this.getMinimum(1)) {
                  var19 = jcal.getCalendarDate(Long.MIN_VALUE, this.getZone());
                  var19.addYear(400);
                  jcal.normalize(var19);
                  var18.setEra(var19.getEra());
                  var18.setDate(var19.getYear() + 1, 1, 1);
                  jcal.normalize(var18);
                  var21 = jcal.getFixedDate(var19);
                  var24 = jcal.getFixedDate(var18);
                  long var13 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(var24 + 6L, this.getFirstDayOfWeek());
                  int var15 = (int)(var13 - var24);
                  if (var15 >= this.getMinimalDaysInFirstWeek()) {
                     var13 -= 7L;
                  }

                  var16 = this.getWeekNumber(var21, var13);
               } else {
                  var20 = gcal.newCalendarDate(TimeZone.NO_TIMEZONE);
                  var20.setDate(var4.getNormalizedYear(), 1, 1);
                  var9 = gcal.getDayOfWeek(var20);
                  var9 -= this.getFirstDayOfWeek();
                  if (var9 < 0) {
                     var9 += 7;
                  }

                  var16 = 52;
                  var23 = var9 + this.getMinimalDaysInFirstWeek() - 1;
                  if (var23 == 6 || var4.isLeapYear() && (var23 == 5 || var23 == 12)) {
                     ++var16;
                  }
               }
            } else {
               if (var3 == this) {
                  var3 = (JapaneseImperialCalendar)var3.clone();
               }

               var7 = this.getActualMaximum(6);
               var3.set(6, var7);
               var16 = var3.get(3);
               if (var16 == 1 && var7 > 7) {
                  var3.add(3, -1);
                  var16 = var3.get(3);
               }
            }
            break;
         case 4:
            var18 = jcal.getCalendarDate(Long.MAX_VALUE, this.getZone());
            if (var4.getEra() == var18.getEra() && var4.getYear() == var18.getYear()) {
               var17 = jcal.getFixedDate(var18);
               var22 = var17 - (long)var18.getDayOfMonth() + 1L;
               var16 = this.getWeekNumber(var22, var17);
            } else {
               var20 = gcal.newCalendarDate(TimeZone.NO_TIMEZONE);
               var20.setDate(var4.getNormalizedYear(), var4.getMonth(), 1);
               var9 = gcal.getDayOfWeek(var20);
               var23 = gcal.getMonthLength(var20);
               var9 -= this.getFirstDayOfWeek();
               if (var9 < 0) {
                  var9 += 7;
               }

               var11 = 7 - var9;
               var16 = 3;
               if (var11 >= this.getMinimalDaysInFirstWeek()) {
                  ++var16;
               }

               var23 -= var11 + 21;
               if (var23 > 0) {
                  ++var16;
                  if (var23 > 7) {
                     ++var16;
                  }
               }
            }
            break;
         case 5:
            var16 = jcal.getMonthLength(var4);
            break;
         case 6:
            if (this.isTransitionYear(var4.getNormalizedYear())) {
               var7 = getEraIndex(var4);
               if (var4.getYear() != 1) {
                  ++var7;

                  assert var7 < eras.length;
               }

               var17 = sinceFixedDates[var7];
               var22 = var3.cachedFixedDate;
               Gregorian.Date var12 = gcal.newCalendarDate(TimeZone.NO_TIMEZONE);
               var12.setDate(var4.getNormalizedYear(), 1, 1);
               if (var22 < var17) {
                  var16 = (int)(var17 - gcal.getFixedDate(var12));
               } else {
                  var12.addYear(1);
                  var16 = (int)(gcal.getFixedDate(var12) - var17);
               }
            } else {
               var18 = jcal.getCalendarDate(Long.MAX_VALUE, this.getZone());
               if (var4.getEra() == var18.getEra() && var4.getYear() == var18.getYear()) {
                  var17 = jcal.getFixedDate(var18);
                  var22 = this.getFixedDateJan1(var18, var17);
                  var16 = (int)(var17 - var22) + 1;
               } else if (var4.getYear() == this.getMinimum(1)) {
                  var19 = jcal.getCalendarDate(Long.MIN_VALUE, this.getZone());
                  var21 = jcal.getFixedDate(var19);
                  var19.addYear(1);
                  var19.setMonth(1).setDayOfMonth(1);
                  jcal.normalize(var19);
                  var24 = jcal.getFixedDate(var19);
                  var16 = (int)(var24 - var21);
               } else {
                  var16 = jcal.getYearLength(var4);
               }
            }
            break;
         case 7:
         default:
            throw new ArrayIndexOutOfBoundsException(var1);
         case 8:
            var9 = var4.getDayOfWeek();
            BaseCalendar.Date var10 = (BaseCalendar.Date)var4.clone();
            var7 = jcal.getMonthLength(var10);
            var10.setDayOfMonth(1);
            jcal.normalize(var10);
            int var8 = var10.getDayOfWeek();
            var11 = var9 - var8;
            if (var11 < 0) {
               var11 += 7;
            }

            var7 -= var11;
            var16 = (var7 + 6) / 7;
         }

         return var16;
      }
   }

   private long getYearOffsetInMillis(CalendarDate var1) {
      long var2 = (jcal.getDayOfYear(var1) - 1L) * 86400000L;
      return var2 + var1.getTimeOfDay() - (long)var1.getZoneOffset();
   }

   public Object clone() {
      JapaneseImperialCalendar var1 = (JapaneseImperialCalendar)super.clone();
      var1.jdate = (LocalGregorianCalendar.Date)this.jdate.clone();
      var1.originalFields = null;
      var1.zoneOffsets = null;
      return var1;
   }

   public TimeZone getTimeZone() {
      TimeZone var1 = super.getTimeZone();
      this.jdate.setZone(var1);
      return var1;
   }

   public void setTimeZone(TimeZone var1) {
      super.setTimeZone(var1);
      this.jdate.setZone(var1);
   }

   protected void computeFields() {
      boolean var1 = false;
      int var3;
      if (this.isPartiallyNormalized()) {
         var3 = this.getSetStateFields();
         int var2 = ~var3 & 131071;
         if (var2 != 0 || this.cachedFixedDate == Long.MIN_VALUE) {
            var3 |= this.computeFields(var2, var3 & 98304);

            assert var3 == 131071;
         }
      } else {
         var3 = 131071;
         this.computeFields(var3, 0);
      }

      this.setFieldsComputed(var3);
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
      if (var5 != this.cachedFixedDate || var5 < 0L) {
         jcal.getCalendarDateFromFixedDate(this.jdate, var5);
         this.cachedFixedDate = var5;
      }

      int var8 = getEraIndex(this.jdate);
      int var9 = this.jdate.getYear();
      this.internalSet(0, var8);
      this.internalSet(1, var9);
      int var10 = var1 | 3;
      int var11 = this.jdate.getMonth() - 1;
      int var12 = this.jdate.getDayOfMonth();
      if ((var1 & 164) != 0) {
         this.internalSet(2, var11);
         this.internalSet(5, var12);
         this.internalSet(7, this.jdate.getDayOfWeek());
         var10 |= 164;
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

         var10 |= 32256;
      }

      if ((var1 & 98304) != 0) {
         this.internalSet(15, this.zoneOffsets[0]);
         this.internalSet(16, this.zoneOffsets[1]);
         var10 |= 98304;
      }

      if ((var1 & 344) != 0) {
         var13 = this.jdate.getNormalizedYear();
         boolean var27 = this.isTransitionYear(this.jdate.getNormalizedYear());
         int var15;
         long var16;
         if (var27) {
            var16 = this.getFixedDateJan1(this.jdate, var5);
            var15 = (int)(var5 - var16) + 1;
         } else if (var13 == MIN_VALUES[1]) {
            LocalGregorianCalendar.Date var18 = jcal.getCalendarDate(Long.MIN_VALUE, this.getZone());
            var16 = jcal.getFixedDate(var18);
            var15 = (int)(var5 - var16) + 1;
         } else {
            var15 = (int)jcal.getDayOfYear(this.jdate);
            var16 = var5 - (long)var15 + 1L;
         }

         long var28 = var27 ? this.getFixedDateMonth1(this.jdate, var5) : var5 - (long)var12 + 1L;
         this.internalSet(6, var15);
         this.internalSet(8, (var12 - 1) / 7 + 1);
         int var20 = this.getWeekNumber(var16, var5);
         long var21;
         long var23;
         if (var20 != 0) {
            if (!var27) {
               if (var20 >= 52) {
                  var21 = var16 + 365L;
                  if (this.jdate.isLeapYear()) {
                     ++var21;
                  }

                  var23 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(var21 + 6L, this.getFirstDayOfWeek());
                  int var31 = (int)(var23 - var21);
                  if (var31 >= this.getMinimalDaysInFirstWeek() && var5 >= var23 - 7L) {
                     var20 = 1;
                  }
               }
            } else {
               LocalGregorianCalendar.Date var29 = (LocalGregorianCalendar.Date)this.jdate.clone();
               long var22;
               if (this.jdate.getYear() == 1) {
                  var29.addYear(1);
                  var29.setMonth(1).setDayOfMonth(1);
                  var22 = jcal.getFixedDate(var29);
               } else {
                  int var24 = getEraIndex(var29) + 1;
                  CalendarDate var32 = eras[var24].getSinceDate();
                  var29.setEra(eras[var24]);
                  var29.setDate(1, var32.getMonth(), var32.getDayOfMonth());
                  jcal.normalize(var29);
                  var22 = jcal.getFixedDate(var29);
               }

               long var30 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(var22 + 6L, this.getFirstDayOfWeek());
               int var33 = (int)(var30 - var22);
               if (var33 >= this.getMinimalDaysInFirstWeek() && var5 >= var30 - 7L) {
                  var20 = 1;
               }
            }
         } else {
            var21 = var16 - 1L;
            LocalGregorianCalendar.Date var25 = getCalendarDate(var21);
            if (!var27 && !this.isTransitionYear(var25.getNormalizedYear())) {
               var23 = var16 - 365L;
               if (var25.isLeapYear()) {
                  --var23;
               }
            } else {
               CalendarDate var26;
               if (var27) {
                  if (this.jdate.getYear() == 1) {
                     if (var8 > 4) {
                        var26 = eras[var8 - 1].getSinceDate();
                        if (var13 == var26.getYear()) {
                           var25.setMonth(var26.getMonth()).setDayOfMonth(var26.getDayOfMonth());
                        }
                     } else {
                        var25.setMonth(1).setDayOfMonth(1);
                     }

                     jcal.normalize(var25);
                     var23 = jcal.getFixedDate(var25);
                  } else {
                     var23 = var16 - 365L;
                     if (var25.isLeapYear()) {
                        --var23;
                     }
                  }
               } else {
                  var26 = eras[getEraIndex(this.jdate)].getSinceDate();
                  var25.setMonth(var26.getMonth()).setDayOfMonth(var26.getDayOfMonth());
                  jcal.normalize(var25);
                  var23 = jcal.getFixedDate(var25);
               }
            }

            var20 = this.getWeekNumber(var23, var21);
         }

         this.internalSet(3, var20);
         this.internalSet(4, this.getWeekNumber(var28, var5));
         var10 |= 344;
      }

      return var10;
   }

   private int getWeekNumber(long var1, long var3) {
      long var5 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(var1 + 6L, this.getFirstDayOfWeek());
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
      int var3;
      if (this.isSet(0)) {
         var3 = this.internalGet(0);
         var2 = this.isSet(1) ? this.internalGet(1) : 1;
      } else if (this.isSet(1)) {
         var3 = eras.length - 1;
         var2 = this.internalGet(1);
      } else {
         var3 = 3;
         var2 = 45;
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

      var6 += this.getFixedDate(var3, var2, var1);
      long var8 = (var6 - 719163L) * 86400000L + var4;
      TimeZone var10 = this.getZone();
      if (this.zoneOffsets == null) {
         this.zoneOffsets = new int[2];
      }

      int var11 = var1 & 98304;
      if (var11 != 98304) {
         if (var10 instanceof ZoneInfo) {
            ((ZoneInfo)var10).getOffsetsByWall(var8, this.zoneOffsets);
         } else {
            var10.getOffsets(var8 - (long)var10.getRawOffset(), this.zoneOffsets);
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
      int var12 = this.computeFields(var1 | this.getSetStateFields(), var11);
      if (!this.isLenient()) {
         for(int var13 = 0; var13 < 17; ++var13) {
            if (this.isExternallySet(var13) && this.originalFields[var13] != this.internalGet(var13)) {
               int var14 = this.internalGet(var13);
               System.arraycopy(this.originalFields, 0, this.fields, 0, this.fields.length);
               throw new IllegalArgumentException(getFieldName(var13) + "=" + var14 + ", expected " + this.originalFields[var13]);
            }
         }
      }

      this.setFieldsNormalized(var12);
   }

   private long getFixedDate(int var1, int var2, int var3) {
      int var4 = 0;
      int var5 = 1;
      if (isFieldSet(var3, 2)) {
         var4 = this.internalGet(2);
         if (var4 > 11) {
            var2 += var4 / 12;
            var4 %= 12;
         } else if (var4 < 0) {
            int[] var6 = new int[1];
            var2 += CalendarUtils.floorDivide(var4, 12, var6);
            var4 = var6[0];
         }
      } else if (var2 == 1 && var1 != 0) {
         CalendarDate var12 = eras[var1].getSinceDate();
         var4 = var12.getMonth() - 1;
         var5 = var12.getDayOfMonth();
      }

      LocalGregorianCalendar.Date var13;
      if (var2 == MIN_VALUES[1]) {
         var13 = jcal.getCalendarDate(Long.MIN_VALUE, this.getZone());
         int var7 = var13.getMonth() - 1;
         if (var4 < var7) {
            var4 = var7;
         }

         if (var4 == var7) {
            var5 = var13.getDayOfMonth();
         }
      }

      var13 = jcal.newCalendarDate(TimeZone.NO_TIMEZONE);
      var13.setEra(var1 > 0 ? eras[var1] : null);
      var13.setDate(var2, var4 + 1, var5);
      jcal.normalize(var13);
      long var14 = jcal.getFixedDate(var13);
      long var9;
      int var11;
      if (isFieldSet(var3, 2)) {
         if (isFieldSet(var3, 5)) {
            if (this.isSet(5)) {
               var14 += (long)this.internalGet(5);
               var14 -= (long)var5;
            }
         } else if (isFieldSet(var3, 4)) {
            var9 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(var14 + 6L, this.getFirstDayOfWeek());
            if (var9 - var14 >= (long)this.getMinimalDaysInFirstWeek()) {
               var9 -= 7L;
            }

            if (isFieldSet(var3, 7)) {
               var9 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(var9 + 6L, this.internalGet(7));
            }

            var14 = var9 + (long)(7 * (this.internalGet(4) - 1));
         } else {
            int var15;
            if (isFieldSet(var3, 7)) {
               var15 = this.internalGet(7);
            } else {
               var15 = this.getFirstDayOfWeek();
            }

            int var10;
            if (isFieldSet(var3, 8)) {
               var10 = this.internalGet(8);
            } else {
               var10 = 1;
            }

            if (var10 >= 0) {
               var14 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(var14 + (long)(7 * var10) - 1L, var15);
            } else {
               var11 = this.monthLength(var4, var2) + 7 * (var10 + 1);
               var14 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(var14 + (long)var11 - 1L, var15);
            }
         }
      } else if (isFieldSet(var3, 6)) {
         if (this.isTransitionYear(var13.getNormalizedYear())) {
            var14 = this.getFixedDateJan1(var13, var14);
         }

         var14 += (long)this.internalGet(6);
         --var14;
      } else {
         var9 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(var14 + 6L, this.getFirstDayOfWeek());
         if (var9 - var14 >= (long)this.getMinimalDaysInFirstWeek()) {
            var9 -= 7L;
         }

         if (isFieldSet(var3, 7)) {
            var11 = this.internalGet(7);
            if (var11 != this.getFirstDayOfWeek()) {
               var9 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(var9 + 6L, var11);
            }
         }

         var14 = var9 + 7L * ((long)this.internalGet(3) - 1L);
      }

      return var14;
   }

   private long getFixedDateJan1(LocalGregorianCalendar.Date var1, long var2) {
      Era var4 = var1.getEra();
      if (var1.getEra() != null && var1.getYear() == 1) {
         for(int var5 = getEraIndex(var1); var5 > 0; --var5) {
            CalendarDate var6 = eras[var5].getSinceDate();
            long var7 = gcal.getFixedDate(var6);
            if (var7 <= var2) {
               return var7;
            }
         }
      }

      Gregorian.Date var9 = gcal.newCalendarDate(TimeZone.NO_TIMEZONE);
      var9.setDate(var1.getNormalizedYear(), 1, 1);
      return gcal.getFixedDate(var9);
   }

   private long getFixedDateMonth1(LocalGregorianCalendar.Date var1, long var2) {
      int var4 = getTransitionEraIndex(var1);
      if (var4 != -1) {
         long var5 = sinceFixedDates[var4];
         if (var5 <= var2) {
            return var5;
         }
      }

      return var2 - (long)var1.getDayOfMonth() + 1L;
   }

   private static LocalGregorianCalendar.Date getCalendarDate(long var0) {
      LocalGregorianCalendar.Date var2 = jcal.newCalendarDate(TimeZone.NO_TIMEZONE);
      jcal.getCalendarDateFromFixedDate(var2, var0);
      return var2;
   }

   private int monthLength(int var1, int var2) {
      return CalendarUtils.isGregorianLeapYear(var2) ? GregorianCalendar.LEAP_MONTH_LENGTH[var1] : GregorianCalendar.MONTH_LENGTH[var1];
   }

   private int monthLength(int var1) {
      assert this.jdate.isNormalized();

      return this.jdate.isLeapYear() ? GregorianCalendar.LEAP_MONTH_LENGTH[var1] : GregorianCalendar.MONTH_LENGTH[var1];
   }

   private int actualMonthLength() {
      int var1 = jcal.getMonthLength(this.jdate);
      int var2 = getTransitionEraIndex(this.jdate);
      if (var2 == -1) {
         long var3 = sinceFixedDates[var2];
         CalendarDate var5 = eras[var2].getSinceDate();
         if (var3 <= this.cachedFixedDate) {
            var1 -= var5.getDayOfMonth() - 1;
         } else {
            var1 = var5.getDayOfMonth() - 1;
         }
      }

      return var1;
   }

   private static int getTransitionEraIndex(LocalGregorianCalendar.Date var0) {
      int var1 = getEraIndex(var0);
      CalendarDate var2 = eras[var1].getSinceDate();
      if (var2.getYear() == var0.getNormalizedYear() && var2.getMonth() == var0.getMonth()) {
         return var1;
      } else {
         if (var1 < eras.length - 1) {
            ++var1;
            var2 = eras[var1].getSinceDate();
            if (var2.getYear() == var0.getNormalizedYear() && var2.getMonth() == var0.getMonth()) {
               return var1;
            }
         }

         return -1;
      }
   }

   private boolean isTransitionYear(int var1) {
      for(int var2 = eras.length - 1; var2 > 0; --var2) {
         int var3 = eras[var2].getSinceDate().getYear();
         if (var1 == var3) {
            return true;
         }

         if (var1 > var3) {
            break;
         }
      }

      return false;
   }

   private static int getEraIndex(LocalGregorianCalendar.Date var0) {
      Era var1 = var0.getEra();

      for(int var2 = eras.length - 1; var2 > 0; --var2) {
         if (eras[var2] == var1) {
            return var2;
         }
      }

      return 0;
   }

   private JapaneseImperialCalendar getNormalizedCalendar() {
      JapaneseImperialCalendar var1;
      if (this.isFullyNormalized()) {
         var1 = this;
      } else {
         var1 = (JapaneseImperialCalendar)this.clone();
         var1.setLenient(true);
         var1.complete();
      }

      return var1;
   }

   private void pinDayOfMonth(LocalGregorianCalendar.Date var1) {
      int var2 = var1.getYear();
      int var3 = var1.getDayOfMonth();
      if (var2 != this.getMinimum(1)) {
         var1.setDayOfMonth(1);
         jcal.normalize(var1);
         int var4 = jcal.getMonthLength(var1);
         if (var3 > var4) {
            var1.setDayOfMonth(var4);
         } else {
            var1.setDayOfMonth(var3);
         }

         jcal.normalize(var1);
      } else {
         LocalGregorianCalendar.Date var9 = jcal.getCalendarDate(Long.MIN_VALUE, this.getZone());
         LocalGregorianCalendar.Date var5 = jcal.getCalendarDate(this.time, this.getZone());
         long var6 = var5.getTimeOfDay();
         var5.addYear(400);
         var5.setMonth(var1.getMonth());
         var5.setDayOfMonth(1);
         jcal.normalize(var5);
         int var8 = jcal.getMonthLength(var5);
         if (var3 > var8) {
            var5.setDayOfMonth(var8);
         } else if (var3 < var9.getDayOfMonth()) {
            var5.setDayOfMonth(var9.getDayOfMonth());
         } else {
            var5.setDayOfMonth(var3);
         }

         if (var5.getDayOfMonth() == var9.getDayOfMonth() && var6 < var9.getTimeOfDay()) {
            var5.setDayOfMonth(Math.min(var3 + 1, var8));
         }

         var1.setDate(var2, var5.getMonth(), var5.getDayOfMonth());
      }

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
      return this.isSet(0) ? this.internalGet(0) : eras.length - 1;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      if (this.jdate == null) {
         this.jdate = jcal.newCalendarDate(this.getZone());
         this.cachedFixedDate = Long.MIN_VALUE;
      }

   }

   static {
      Era[] var0 = jcal.getEras();
      int var1 = var0.length + 1;
      eras = new Era[var1];
      sinceFixedDates = new long[var1];
      byte var2 = 0;
      sinceFixedDates[var2] = gcal.getFixedDate(BEFORE_MEIJI_ERA.getSinceDate());
      int var14 = var2 + 1;
      eras[var2] = BEFORE_MEIJI_ERA;
      Era[] var3 = var0;
      int var4 = var0.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Era var6 = var3[var5];
         CalendarDate var7 = var6.getSinceDate();
         sinceFixedDates[var14] = gcal.getFixedDate(var7);
         eras[var14++] = var6;
      }

      LEAST_MAX_VALUES[0] = MAX_VALUES[0] = eras.length - 1;
      int var15 = Integer.MAX_VALUE;
      var4 = Integer.MAX_VALUE;
      Gregorian.Date var16 = gcal.newCalendarDate(TimeZone.NO_TIMEZONE);

      for(int var17 = 1; var17 < eras.length; ++var17) {
         long var18 = sinceFixedDates[var17];
         CalendarDate var9 = eras[var17].getSinceDate();
         var16.setDate(var9.getYear(), 1, 1);
         long var10 = gcal.getFixedDate(var16);
         if (var18 != var10) {
            var4 = Math.min((int)(var18 - var10) + 1, var4);
         }

         var16.setDate(var9.getYear(), 12, 31);
         var10 = gcal.getFixedDate(var16);
         if (var18 != var10) {
            var4 = Math.min((int)(var10 - var18) + 1, var4);
         }

         LocalGregorianCalendar.Date var12 = getCalendarDate(var18 - 1L);
         int var13 = var12.getYear();
         if (var12.getMonth() != 1 || var12.getDayOfMonth() != 1) {
            --var13;
         }

         var15 = Math.min(var13, var15);
      }

      LEAST_MAX_VALUES[1] = var15;
      LEAST_MAX_VALUES[6] = var4;
   }
}
