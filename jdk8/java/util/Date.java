package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.time.Instant;
import sun.util.calendar.BaseCalendar;
import sun.util.calendar.CalendarDate;
import sun.util.calendar.CalendarSystem;
import sun.util.calendar.CalendarUtils;
import sun.util.calendar.ZoneInfo;

public class Date implements Serializable, Cloneable, Comparable<Date> {
   private static final BaseCalendar gcal = CalendarSystem.getGregorianCalendar();
   private static BaseCalendar jcal;
   private transient long fastTime;
   private transient BaseCalendar.Date cdate;
   private static int defaultCenturyStart;
   private static final long serialVersionUID = 7523967970034938905L;
   private static final String[] wtb = new String[]{"am", "pm", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday", "january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december", "gmt", "ut", "utc", "est", "edt", "cst", "cdt", "mst", "mdt", "pst", "pdt"};
   private static final int[] ttb = new int[]{14, 1, 0, 0, 0, 0, 0, 0, 0, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 10000, 10000, 10000, 10300, 10240, 10360, 10300, 10420, 10360, 10480, 10420};

   public Date() {
      this(System.currentTimeMillis());
   }

   public Date(long var1) {
      this.fastTime = var1;
   }

   /** @deprecated */
   @Deprecated
   public Date(int var1, int var2, int var3) {
      this(var1, var2, var3, 0, 0, 0);
   }

   /** @deprecated */
   @Deprecated
   public Date(int var1, int var2, int var3, int var4, int var5) {
      this(var1, var2, var3, var4, var5, 0);
   }

   /** @deprecated */
   @Deprecated
   public Date(int var1, int var2, int var3, int var4, int var5, int var6) {
      int var7 = var1 + 1900;
      if (var2 >= 12) {
         var7 += var2 / 12;
         var2 %= 12;
      } else if (var2 < 0) {
         var7 += CalendarUtils.floorDivide(var2, 12);
         var2 = CalendarUtils.mod(var2, 12);
      }

      BaseCalendar var8 = getCalendarSystem(var7);
      this.cdate = (BaseCalendar.Date)var8.newCalendarDate(TimeZone.getDefaultRef());
      this.cdate.setNormalizedDate(var7, var2 + 1, var3).setTimeOfDay(var4, var5, var6, 0);
      this.getTimeImpl();
      this.cdate = null;
   }

   /** @deprecated */
   @Deprecated
   public Date(String var1) {
      this(parse(var1));
   }

   public Object clone() {
      Date var1 = null;

      try {
         var1 = (Date)super.clone();
         if (this.cdate != null) {
            var1.cdate = (BaseCalendar.Date)this.cdate.clone();
         }
      } catch (CloneNotSupportedException var3) {
      }

      return var1;
   }

   /** @deprecated */
   @Deprecated
   public static long UTC(int var0, int var1, int var2, int var3, int var4, int var5) {
      int var6 = var0 + 1900;
      if (var1 >= 12) {
         var6 += var1 / 12;
         var1 %= 12;
      } else if (var1 < 0) {
         var6 += CalendarUtils.floorDivide(var1, 12);
         var1 = CalendarUtils.mod(var1, 12);
      }

      int var7 = var1 + 1;
      BaseCalendar var8 = getCalendarSystem(var6);
      BaseCalendar.Date var9 = (BaseCalendar.Date)var8.newCalendarDate((TimeZone)null);
      var9.setNormalizedDate(var6, var7, var2).setTimeOfDay(var3, var4, var5, 0);
      Date var10 = new Date(0L);
      var10.normalize(var9);
      return var10.fastTime;
   }

   /** @deprecated */
   @Deprecated
   public static long parse(String var0) {
      int var1 = Integer.MIN_VALUE;
      byte var2 = -1;
      byte var3 = -1;
      int var4 = -1;
      byte var5 = -1;
      byte var6 = -1;
      boolean var7 = true;
      boolean var8 = true;
      int var9 = 0;
      boolean var10 = true;
      boolean var11 = true;
      int var12 = -1;
      char var13 = 0;
      if (var0 != null) {
         int var14 = var0.length();

         while(true) {
            while(true) {
               char var20;
               do {
                  do {
                     if (var9 >= var14) {
                        if (var1 != Integer.MIN_VALUE && var2 >= 0 && var3 >= 0) {
                           if (var1 < 100) {
                              Class var22 = Date.class;
                              synchronized(Date.class) {
                                 if (defaultCenturyStart == 0) {
                                    defaultCenturyStart = gcal.getCalendarDate().getYear() - 80;
                                 }
                              }

                              var1 += defaultCenturyStart / 100 * 100;
                              if (var1 < defaultCenturyStart) {
                                 var1 += 100;
                              }
                           }

                           if (var6 < 0) {
                              var6 = 0;
                           }

                           if (var5 < 0) {
                              var5 = 0;
                           }

                           if (var4 < 0) {
                              var4 = 0;
                           }

                           BaseCalendar var23 = getCalendarSystem(var1);
                           BaseCalendar.Date var24;
                           if (var12 == -1) {
                              var24 = (BaseCalendar.Date)var23.newCalendarDate(TimeZone.getDefaultRef());
                              var24.setDate(var1, var2 + 1, var3);
                              var24.setTimeOfDay(var4, var5, var6, 0);
                              return var23.getTime(var24);
                           }

                           var24 = (BaseCalendar.Date)var23.newCalendarDate((TimeZone)null);
                           var24.setDate(var1, var2 + 1, var3);
                           var24.setTimeOfDay(var4, var5, var6, 0);
                           return var23.getTime(var24) + (long)(var12 * '\uea60');
                        }

                        throw new IllegalArgumentException();
                     }

                     var20 = var0.charAt(var9);
                     ++var9;
                  } while(var20 <= ' ');
               } while(var20 == ',');

               int var15;
               if (var20 == '(') {
                  var15 = 1;

                  while(var9 < var14) {
                     var20 = var0.charAt(var9);
                     ++var9;
                     if (var20 == '(') {
                        ++var15;
                     } else if (var20 == ')') {
                        --var15;
                        if (var15 <= 0) {
                           break;
                        }
                     }
                  }
               } else if ('0' <= var20 && var20 <= '9') {
                  int var21;
                  for(var21 = var20 - 48; var9 < var14 && '0' <= (var20 = var0.charAt(var9)) && var20 <= '9'; ++var9) {
                     var21 = var21 * 10 + var20 - 48;
                  }

                  if (var13 != '+' && (var13 != '-' || var1 == Integer.MIN_VALUE)) {
                     if (var21 >= 70) {
                        if (var1 != Integer.MIN_VALUE || var20 > ' ' && var20 != ',' && var20 != '/' && var9 < var14) {
                           throw new IllegalArgumentException();
                        }

                        var1 = var21;
                     } else if (var20 == ':') {
                        if (var4 < 0) {
                           var4 = (byte)var21;
                        } else {
                           if (var5 >= 0) {
                              throw new IllegalArgumentException();
                           }

                           var5 = (byte)var21;
                        }
                     } else if (var20 == '/') {
                        if (var2 < 0) {
                           var2 = (byte)(var21 - 1);
                        } else {
                           if (var3 >= 0) {
                              throw new IllegalArgumentException();
                           }

                           var3 = (byte)var21;
                        }
                     } else {
                        if (var9 < var14 && var20 != ',' && var20 > ' ' && var20 != '-') {
                           throw new IllegalArgumentException();
                        }

                        if (var4 >= 0 && var5 < 0) {
                           var5 = (byte)var21;
                        } else if (var5 >= 0 && var6 < 0) {
                           var6 = (byte)var21;
                        } else if (var3 < 0) {
                           var3 = (byte)var21;
                        } else {
                           if (var1 != Integer.MIN_VALUE || var2 < 0 || var3 < 0) {
                              throw new IllegalArgumentException();
                           }

                           var1 = var21;
                        }
                     }
                  } else {
                     if (var21 < 24) {
                        var21 *= 60;
                     } else {
                        var21 = var21 % 100 + var21 / 100 * 60;
                     }

                     if (var13 == '+') {
                        var21 = -var21;
                     }

                     if (var12 != 0 && var12 != -1) {
                        throw new IllegalArgumentException();
                     }

                     var12 = var21;
                  }

                  var13 = 0;
               } else if (var20 != '/' && var20 != ':' && var20 != '+' && var20 != '-') {
                  for(var15 = var9 - 1; var9 < var14; ++var9) {
                     var20 = var0.charAt(var9);
                     if (('A' > var20 || var20 > 'Z') && ('a' > var20 || var20 > 'z')) {
                        break;
                     }
                  }

                  if (var9 <= var15 + 1) {
                     throw new IllegalArgumentException();
                  }

                  int var16 = wtb.length;

                  while(true) {
                     --var16;
                     if (var16 < 0) {
                        break;
                     }

                     if (wtb[var16].regionMatches(true, 0, var0, var15, var9 - var15)) {
                        int var17 = ttb[var16];
                        if (var17 != 0) {
                           if (var17 == 1) {
                              if (var4 > 12 || var4 < 1) {
                                 throw new IllegalArgumentException();
                              }

                              if (var4 < 12) {
                                 var4 += 12;
                              }
                           } else if (var17 == 14) {
                              if (var4 > 12 || var4 < 1) {
                                 throw new IllegalArgumentException();
                              }

                              if (var4 == 12) {
                                 var4 = 0;
                              }
                           } else if (var17 <= 13) {
                              if (var2 >= 0) {
                                 throw new IllegalArgumentException();
                              }

                              var2 = (byte)(var17 - 2);
                           } else {
                              var12 = var17 - 10000;
                           }
                        }
                        break;
                     }
                  }

                  if (var16 < 0) {
                     throw new IllegalArgumentException();
                  }

                  var13 = 0;
               } else {
                  var13 = var20;
               }
            }
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   /** @deprecated */
   @Deprecated
   public int getYear() {
      return this.normalize().getYear() - 1900;
   }

   /** @deprecated */
   @Deprecated
   public void setYear(int var1) {
      this.getCalendarDate().setNormalizedYear(var1 + 1900);
   }

   /** @deprecated */
   @Deprecated
   public int getMonth() {
      return this.normalize().getMonth() - 1;
   }

   /** @deprecated */
   @Deprecated
   public void setMonth(int var1) {
      int var2 = 0;
      if (var1 >= 12) {
         var2 = var1 / 12;
         var1 %= 12;
      } else if (var1 < 0) {
         var2 = CalendarUtils.floorDivide(var1, 12);
         var1 = CalendarUtils.mod(var1, 12);
      }

      BaseCalendar.Date var3 = this.getCalendarDate();
      if (var2 != 0) {
         var3.setNormalizedYear(var3.getNormalizedYear() + var2);
      }

      var3.setMonth(var1 + 1);
   }

   /** @deprecated */
   @Deprecated
   public int getDate() {
      return this.normalize().getDayOfMonth();
   }

   /** @deprecated */
   @Deprecated
   public void setDate(int var1) {
      this.getCalendarDate().setDayOfMonth(var1);
   }

   /** @deprecated */
   @Deprecated
   public int getDay() {
      return this.normalize().getDayOfWeek() - 1;
   }

   /** @deprecated */
   @Deprecated
   public int getHours() {
      return this.normalize().getHours();
   }

   /** @deprecated */
   @Deprecated
   public void setHours(int var1) {
      this.getCalendarDate().setHours(var1);
   }

   /** @deprecated */
   @Deprecated
   public int getMinutes() {
      return this.normalize().getMinutes();
   }

   /** @deprecated */
   @Deprecated
   public void setMinutes(int var1) {
      this.getCalendarDate().setMinutes(var1);
   }

   /** @deprecated */
   @Deprecated
   public int getSeconds() {
      return this.normalize().getSeconds();
   }

   /** @deprecated */
   @Deprecated
   public void setSeconds(int var1) {
      this.getCalendarDate().setSeconds(var1);
   }

   public long getTime() {
      return this.getTimeImpl();
   }

   private final long getTimeImpl() {
      if (this.cdate != null && !this.cdate.isNormalized()) {
         this.normalize();
      }

      return this.fastTime;
   }

   public void setTime(long var1) {
      this.fastTime = var1;
      this.cdate = null;
   }

   public boolean before(Date var1) {
      return getMillisOf(this) < getMillisOf(var1);
   }

   public boolean after(Date var1) {
      return getMillisOf(this) > getMillisOf(var1);
   }

   public boolean equals(Object var1) {
      return var1 instanceof Date && this.getTime() == ((Date)var1).getTime();
   }

   static final long getMillisOf(Date var0) {
      if (var0.cdate != null && !var0.cdate.isNormalized()) {
         BaseCalendar.Date var1 = (BaseCalendar.Date)var0.cdate.clone();
         return gcal.getTime(var1);
      } else {
         return var0.fastTime;
      }
   }

   public int compareTo(Date var1) {
      long var2 = getMillisOf(this);
      long var4 = getMillisOf(var1);
      return var2 < var4 ? -1 : (var2 == var4 ? 0 : 1);
   }

   public int hashCode() {
      long var1 = this.getTime();
      return (int)var1 ^ (int)(var1 >> 32);
   }

   public String toString() {
      BaseCalendar.Date var1 = this.normalize();
      StringBuilder var2 = new StringBuilder(28);
      int var3 = var1.getDayOfWeek();
      if (var3 == 1) {
         var3 = 8;
      }

      convertToAbbr(var2, wtb[var3]).append(' ');
      convertToAbbr(var2, wtb[var1.getMonth() - 1 + 2 + 7]).append(' ');
      CalendarUtils.sprintf0d((StringBuilder)var2, var1.getDayOfMonth(), 2).append(' ');
      CalendarUtils.sprintf0d((StringBuilder)var2, var1.getHours(), 2).append(':');
      CalendarUtils.sprintf0d((StringBuilder)var2, var1.getMinutes(), 2).append(':');
      CalendarUtils.sprintf0d((StringBuilder)var2, var1.getSeconds(), 2).append(' ');
      TimeZone var4 = var1.getZone();
      if (var4 != null) {
         var2.append(var4.getDisplayName(var1.isDaylightTime(), 0, Locale.US));
      } else {
         var2.append("GMT");
      }

      var2.append(' ').append(var1.getYear());
      return var2.toString();
   }

   private static final StringBuilder convertToAbbr(StringBuilder var0, String var1) {
      var0.append(Character.toUpperCase(var1.charAt(0)));
      var0.append(var1.charAt(1)).append(var1.charAt(2));
      return var0;
   }

   /** @deprecated */
   @Deprecated
   public String toLocaleString() {
      DateFormat var1 = DateFormat.getDateTimeInstance();
      return var1.format(this);
   }

   /** @deprecated */
   @Deprecated
   public String toGMTString() {
      long var1 = this.getTime();
      BaseCalendar var3 = getCalendarSystem(var1);
      BaseCalendar.Date var4 = (BaseCalendar.Date)var3.getCalendarDate(this.getTime(), (TimeZone)null);
      StringBuilder var5 = new StringBuilder(32);
      CalendarUtils.sprintf0d((StringBuilder)var5, var4.getDayOfMonth(), 1).append(' ');
      convertToAbbr(var5, wtb[var4.getMonth() - 1 + 2 + 7]).append(' ');
      var5.append(var4.getYear()).append(' ');
      CalendarUtils.sprintf0d((StringBuilder)var5, var4.getHours(), 2).append(':');
      CalendarUtils.sprintf0d((StringBuilder)var5, var4.getMinutes(), 2).append(':');
      CalendarUtils.sprintf0d((StringBuilder)var5, var4.getSeconds(), 2);
      var5.append(" GMT");
      return var5.toString();
   }

   /** @deprecated */
   @Deprecated
   public int getTimezoneOffset() {
      int var1;
      if (this.cdate == null) {
         TimeZone var2 = TimeZone.getDefaultRef();
         if (var2 instanceof ZoneInfo) {
            var1 = ((ZoneInfo)var2).getOffsets(this.fastTime, (int[])null);
         } else {
            var1 = var2.getOffset(this.fastTime);
         }
      } else {
         this.normalize();
         var1 = this.cdate.getZoneOffset();
      }

      return -var1 / '\uea60';
   }

   private final BaseCalendar.Date getCalendarDate() {
      if (this.cdate == null) {
         BaseCalendar var1 = getCalendarSystem(this.fastTime);
         this.cdate = (BaseCalendar.Date)var1.getCalendarDate(this.fastTime, TimeZone.getDefaultRef());
      }

      return this.cdate;
   }

   private final BaseCalendar.Date normalize() {
      if (this.cdate == null) {
         BaseCalendar var3 = getCalendarSystem(this.fastTime);
         this.cdate = (BaseCalendar.Date)var3.getCalendarDate(this.fastTime, TimeZone.getDefaultRef());
         return this.cdate;
      } else {
         if (!this.cdate.isNormalized()) {
            this.cdate = this.normalize(this.cdate);
         }

         TimeZone var1 = TimeZone.getDefaultRef();
         if (var1 != this.cdate.getZone()) {
            this.cdate.setZone(var1);
            BaseCalendar var2 = getCalendarSystem(this.cdate);
            var2.getCalendarDate(this.fastTime, (CalendarDate)this.cdate);
         }

         return this.cdate;
      }
   }

   private final BaseCalendar.Date normalize(BaseCalendar.Date var1) {
      int var2 = var1.getNormalizedYear();
      int var3 = var1.getMonth();
      int var4 = var1.getDayOfMonth();
      int var5 = var1.getHours();
      int var6 = var1.getMinutes();
      int var7 = var1.getSeconds();
      int var8 = var1.getMillis();
      TimeZone var9 = var1.getZone();
      BaseCalendar var11;
      if (var2 != 1582 && var2 <= 280000000 && var2 >= -280000000) {
         BaseCalendar var12 = getCalendarSystem(var2);
         if (var12 != getCalendarSystem(var1)) {
            var1 = (BaseCalendar.Date)var12.newCalendarDate(var9);
            var1.setNormalizedDate(var2, var3, var4).setTimeOfDay(var5, var6, var7, var8);
         }

         this.fastTime = var12.getTime(var1);
         var11 = getCalendarSystem(this.fastTime);
         if (var11 != var12) {
            var1 = (BaseCalendar.Date)var11.newCalendarDate(var9);
            var1.setNormalizedDate(var2, var3, var4).setTimeOfDay(var5, var6, var7, var8);
            this.fastTime = var11.getTime(var1);
         }

         return var1;
      } else {
         if (var9 == null) {
            var9 = TimeZone.getTimeZone("GMT");
         }

         GregorianCalendar var10 = new GregorianCalendar(var9);
         var10.clear();
         var10.set(14, var8);
         var10.set(var2, var3 - 1, var4, var5, var6, var7);
         this.fastTime = var10.getTimeInMillis();
         var11 = getCalendarSystem(this.fastTime);
         var1 = (BaseCalendar.Date)var11.getCalendarDate(this.fastTime, var9);
         return var1;
      }
   }

   private static final BaseCalendar getCalendarSystem(int var0) {
      return var0 >= 1582 ? gcal : getJulianCalendar();
   }

   private static final BaseCalendar getCalendarSystem(long var0) {
      return var0 < 0L && var0 < -12219292800000L - (long)TimeZone.getDefaultRef().getOffset(var0) ? getJulianCalendar() : gcal;
   }

   private static final BaseCalendar getCalendarSystem(BaseCalendar.Date var0) {
      if (jcal == null) {
         return gcal;
      } else {
         return var0.getEra() != null ? jcal : gcal;
      }
   }

   private static final synchronized BaseCalendar getJulianCalendar() {
      if (jcal == null) {
         jcal = (BaseCalendar)CalendarSystem.forName("julian");
      }

      return jcal;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.writeLong(this.getTimeImpl());
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      this.fastTime = var1.readLong();
   }

   public static Date from(Instant var0) {
      try {
         return new Date(var0.toEpochMilli());
      } catch (ArithmeticException var2) {
         throw new IllegalArgumentException(var2);
      }
   }

   public Instant toInstant() {
      return Instant.ofEpochMilli(this.getTime());
   }
}
