package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.jaxp.datatype.DatatypeFactoryImpl;
import com.sun.org.apache.xerces.internal.xs.datatypes.XSDateTime;
import java.math.BigDecimal;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

public abstract class AbstractDateTimeDV extends TypeValidator {
   private static final boolean DEBUG = false;
   protected static final int YEAR = 2000;
   protected static final int MONTH = 1;
   protected static final int DAY = 1;
   protected static final DatatypeFactory datatypeFactory = new DatatypeFactoryImpl();

   public short getAllowedFacets() {
      return 2552;
   }

   public boolean isIdentical(Object value1, Object value2) {
      if (value1 instanceof AbstractDateTimeDV.DateTimeData && value2 instanceof AbstractDateTimeDV.DateTimeData) {
         AbstractDateTimeDV.DateTimeData v1 = (AbstractDateTimeDV.DateTimeData)value1;
         AbstractDateTimeDV.DateTimeData v2 = (AbstractDateTimeDV.DateTimeData)value2;
         return v1.timezoneHr == v2.timezoneHr && v1.timezoneMin == v2.timezoneMin ? v1.equals(v2) : false;
      } else {
         return false;
      }
   }

   public int compare(Object value1, Object value2) {
      return this.compareDates((AbstractDateTimeDV.DateTimeData)value1, (AbstractDateTimeDV.DateTimeData)value2, true);
   }

   protected short compareDates(AbstractDateTimeDV.DateTimeData date1, AbstractDateTimeDV.DateTimeData date2, boolean strict) {
      if (date1.utc == date2.utc) {
         return this.compareOrder(date1, date2);
      } else {
         AbstractDateTimeDV.DateTimeData tempDate = new AbstractDateTimeDV.DateTimeData((String)null, this);
         short c1;
         short c2;
         if (date1.utc == 90) {
            this.cloneDate(date2, tempDate);
            tempDate.timezoneHr = 14;
            tempDate.timezoneMin = 0;
            tempDate.utc = 43;
            this.normalize(tempDate);
            c1 = this.compareOrder(date1, tempDate);
            if (c1 == -1) {
               return c1;
            } else {
               this.cloneDate(date2, tempDate);
               tempDate.timezoneHr = -14;
               tempDate.timezoneMin = 0;
               tempDate.utc = 45;
               this.normalize(tempDate);
               c2 = this.compareOrder(date1, tempDate);
               return c2 == 1 ? c2 : 2;
            }
         } else if (date2.utc == 90) {
            this.cloneDate(date1, tempDate);
            tempDate.timezoneHr = -14;
            tempDate.timezoneMin = 0;
            tempDate.utc = 45;
            this.normalize(tempDate);
            c1 = this.compareOrder(tempDate, date2);
            if (c1 == -1) {
               return c1;
            } else {
               this.cloneDate(date1, tempDate);
               tempDate.timezoneHr = 14;
               tempDate.timezoneMin = 0;
               tempDate.utc = 43;
               this.normalize(tempDate);
               c2 = this.compareOrder(tempDate, date2);
               return c2 == 1 ? c2 : 2;
            }
         } else {
            return 2;
         }
      }
   }

   protected short compareOrder(AbstractDateTimeDV.DateTimeData date1, AbstractDateTimeDV.DateTimeData date2) {
      if (date1.position < 1) {
         if (date1.year < date2.year) {
            return -1;
         }

         if (date1.year > date2.year) {
            return 1;
         }
      }

      if (date1.position < 2) {
         if (date1.month < date2.month) {
            return -1;
         }

         if (date1.month > date2.month) {
            return 1;
         }
      }

      if (date1.day < date2.day) {
         return -1;
      } else if (date1.day > date2.day) {
         return 1;
      } else if (date1.hour < date2.hour) {
         return -1;
      } else if (date1.hour > date2.hour) {
         return 1;
      } else if (date1.minute < date2.minute) {
         return -1;
      } else if (date1.minute > date2.minute) {
         return 1;
      } else if (date1.second < date2.second) {
         return -1;
      } else if (date1.second > date2.second) {
         return 1;
      } else if (date1.utc < date2.utc) {
         return -1;
      } else {
         return (short)(date1.utc > date2.utc ? 1 : 0);
      }
   }

   protected void getTime(String buffer, int start, int end, AbstractDateTimeDV.DateTimeData data) throws RuntimeException {
      int stop = start + 2;
      data.hour = this.parseInt(buffer, start, stop);
      if (buffer.charAt(stop++) != ':') {
         throw new RuntimeException("Error in parsing time zone");
      } else {
         start = stop;
         stop += 2;
         data.minute = this.parseInt(buffer, start, stop);
         if (buffer.charAt(stop++) != ':') {
            throw new RuntimeException("Error in parsing time zone");
         } else {
            int sign = this.findUTCSign(buffer, start, end);
            start = stop;
            stop = sign < 0 ? end : sign;
            data.second = this.parseSecond(buffer, start, stop);
            if (sign > 0) {
               this.getTimeZone(buffer, data, sign, end);
            }

         }
      }
   }

   protected int getDate(String buffer, int start, int end, AbstractDateTimeDV.DateTimeData date) throws RuntimeException {
      start = this.getYearMonth(buffer, start, end, date);
      if (buffer.charAt(start++) != '-') {
         throw new RuntimeException("CCYY-MM must be followed by '-' sign");
      } else {
         int stop = start + 2;
         date.day = this.parseInt(buffer, start, stop);
         return stop;
      }
   }

   protected int getYearMonth(String buffer, int start, int end, AbstractDateTimeDV.DateTimeData date) throws RuntimeException {
      if (buffer.charAt(0) == '-') {
         ++start;
      }

      int i = this.indexOf(buffer, start, end, '-');
      if (i == -1) {
         throw new RuntimeException("Year separator is missing or misplaced");
      } else {
         int length = i - start;
         if (length < 4) {
            throw new RuntimeException("Year must have 'CCYY' format");
         } else if (length > 4 && buffer.charAt(start) == '0') {
            throw new RuntimeException("Leading zeros are required if the year value would otherwise have fewer than four digits; otherwise they are forbidden");
         } else {
            date.year = this.parseIntYear(buffer, i);
            if (buffer.charAt(i) != '-') {
               throw new RuntimeException("CCYY must be followed by '-' sign");
            } else {
               ++i;
               start = i;
               i += 2;
               date.month = this.parseInt(buffer, start, i);
               return i;
            }
         }
      }
   }

   protected void parseTimeZone(String buffer, int start, int end, AbstractDateTimeDV.DateTimeData date) throws RuntimeException {
      if (start < end) {
         if (!this.isNextCharUTCSign(buffer, start, end)) {
            throw new RuntimeException("Error in month parsing");
         }

         this.getTimeZone(buffer, date, start, end);
      }

   }

   protected void getTimeZone(String buffer, AbstractDateTimeDV.DateTimeData data, int sign, int end) throws RuntimeException {
      data.utc = buffer.charAt(sign);
      if (buffer.charAt(sign) == 'Z') {
         ++sign;
         if (end > sign) {
            throw new RuntimeException("Error in parsing time zone");
         }
      } else if (sign <= end - 6) {
         int negate = buffer.charAt(sign) == '-' ? -1 : 1;
         ++sign;
         int stop = sign + 2;
         data.timezoneHr = negate * this.parseInt(buffer, sign, stop);
         if (buffer.charAt(stop++) != ':') {
            throw new RuntimeException("Error in parsing time zone");
         } else {
            data.timezoneMin = negate * this.parseInt(buffer, stop, stop + 2);
            if (stop + 2 != end) {
               throw new RuntimeException("Error in parsing time zone");
            } else {
               if (data.timezoneHr != 0 || data.timezoneMin != 0) {
                  data.normalized = false;
               }

            }
         }
      } else {
         throw new RuntimeException("Error in parsing time zone");
      }
   }

   protected int indexOf(String buffer, int start, int end, char ch) {
      for(int i = start; i < end; ++i) {
         if (buffer.charAt(i) == ch) {
            return i;
         }
      }

      return -1;
   }

   protected void validateDateTime(AbstractDateTimeDV.DateTimeData data) {
      if (data.year == 0) {
         throw new RuntimeException("The year \"0000\" is an illegal year value");
      } else if (data.month >= 1 && data.month <= 12) {
         if (data.day <= this.maxDayInMonthFor(data.year, data.month) && data.day >= 1) {
            if (data.hour > 23 || data.hour < 0) {
               if (data.hour != 24 || data.minute != 0 || data.second != 0.0D) {
                  throw new RuntimeException("Hour must have values 0-23, unless 24:00:00");
               }

               data.hour = 0;
               if (++data.day > this.maxDayInMonthFor(data.year, data.month)) {
                  data.day = 1;
                  if (++data.month > 12) {
                     data.month = 1;
                     if (++data.year == 0) {
                        data.year = 1;
                     }
                  }
               }
            }

            if (data.minute <= 59 && data.minute >= 0) {
               if (data.second < 60.0D && data.second >= 0.0D) {
                  if (data.timezoneHr <= 14 && data.timezoneHr >= -14) {
                     if ((data.timezoneHr == 14 || data.timezoneHr == -14) && data.timezoneMin != 0) {
                        throw new RuntimeException("Time zone should have range -14:00 to +14:00");
                     } else if (data.timezoneMin > 59 || data.timezoneMin < -59) {
                        throw new RuntimeException("Minute must have values 0-59");
                     }
                  } else {
                     throw new RuntimeException("Time zone should have range -14:00 to +14:00");
                  }
               } else {
                  throw new RuntimeException("Second must have values 0-59");
               }
            } else {
               throw new RuntimeException("Minute must have values 0-59");
            }
         } else {
            throw new RuntimeException("The day must have values 1 to 31");
         }
      } else {
         throw new RuntimeException("The month must have values 1 to 12");
      }
   }

   protected int findUTCSign(String buffer, int start, int end) {
      for(int i = start; i < end; ++i) {
         int c = buffer.charAt(i);
         if (c == 'Z' || c == '+' || c == '-') {
            return i;
         }
      }

      return -1;
   }

   protected final boolean isNextCharUTCSign(String buffer, int start, int end) {
      if (start >= end) {
         return false;
      } else {
         char c = buffer.charAt(start);
         return c == 'Z' || c == '+' || c == '-';
      }
   }

   protected int parseInt(String buffer, int start, int end) throws NumberFormatException {
      int radix = 10;
      int result = 0;
      int digit = false;
      int limit = -2147483647;
      int multmin = limit / radix;
      int i = start;

      do {
         int digit = getDigit(buffer.charAt(i));
         if (digit < 0) {
            throw new NumberFormatException("'" + buffer + "' has wrong format");
         }

         if (result < multmin) {
            throw new NumberFormatException("'" + buffer + "' has wrong format");
         }

         result *= radix;
         if (result < limit + digit) {
            throw new NumberFormatException("'" + buffer + "' has wrong format");
         }

         result -= digit;
         ++i;
      } while(i < end);

      return -result;
   }

   protected int parseIntYear(String buffer, int end) {
      int radix = 10;
      int result = 0;
      boolean negative = false;
      int i = 0;
      int digit = false;
      int limit;
      if (buffer.charAt(0) == '-') {
         negative = true;
         limit = Integer.MIN_VALUE;
         ++i;
      } else {
         limit = -2147483647;
      }

      int digit;
      for(int multmin = limit / radix; i < end; result -= digit) {
         digit = getDigit(buffer.charAt(i++));
         if (digit < 0) {
            throw new NumberFormatException("'" + buffer + "' has wrong format");
         }

         if (result < multmin) {
            throw new NumberFormatException("'" + buffer + "' has wrong format");
         }

         result *= radix;
         if (result < limit + digit) {
            throw new NumberFormatException("'" + buffer + "' has wrong format");
         }
      }

      if (negative) {
         if (i > 1) {
            return result;
         } else {
            throw new NumberFormatException("'" + buffer + "' has wrong format");
         }
      } else {
         return -result;
      }
   }

   protected void normalize(AbstractDateTimeDV.DateTimeData date) {
      int negate = -1;
      int temp = date.minute + negate * date.timezoneMin;
      int carry = this.fQuotient(temp, 60);
      date.minute = this.mod(temp, 60, carry);
      temp = date.hour + negate * date.timezoneHr + carry;
      carry = this.fQuotient(temp, 24);
      date.hour = this.mod(temp, 24, carry);
      date.day += carry;

      while(true) {
         do {
            temp = this.maxDayInMonthFor(date.year, date.month);
            byte carry;
            if (date.day < 1) {
               date.day += this.maxDayInMonthFor(date.year, date.month - 1);
               carry = -1;
            } else {
               if (date.day <= temp) {
                  date.utc = 90;
                  return;
               }

               date.day -= temp;
               carry = 1;
            }

            temp = date.month + carry;
            date.month = this.modulo(temp, 1, 13);
            date.year += this.fQuotient(temp, 1, 13);
         } while(date.year != 0);

         date.year = date.timezoneHr >= 0 && date.timezoneMin >= 0 ? -1 : 1;
      }
   }

   protected void saveUnnormalized(AbstractDateTimeDV.DateTimeData date) {
      date.unNormYear = date.year;
      date.unNormMonth = date.month;
      date.unNormDay = date.day;
      date.unNormHour = date.hour;
      date.unNormMinute = date.minute;
      date.unNormSecond = date.second;
   }

   protected void resetDateObj(AbstractDateTimeDV.DateTimeData data) {
      data.year = 0;
      data.month = 0;
      data.day = 0;
      data.hour = 0;
      data.minute = 0;
      data.second = 0.0D;
      data.utc = 0;
      data.timezoneHr = 0;
      data.timezoneMin = 0;
   }

   protected int maxDayInMonthFor(int year, int month) {
      if (month != 4 && month != 6 && month != 9 && month != 11) {
         if (month == 2) {
            return this.isLeapYear(year) ? 29 : 28;
         } else {
            return 31;
         }
      } else {
         return 30;
      }
   }

   private boolean isLeapYear(int year) {
      return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
   }

   protected int mod(int a, int b, int quotient) {
      return a - quotient * b;
   }

   protected int fQuotient(int a, int b) {
      return (int)Math.floor((double)((float)a / (float)b));
   }

   protected int modulo(int temp, int low, int high) {
      int a = temp - low;
      int b = high - low;
      return this.mod(a, b, this.fQuotient(a, b)) + low;
   }

   protected int fQuotient(int temp, int low, int high) {
      return this.fQuotient(temp - low, high - low);
   }

   protected String dateToString(AbstractDateTimeDV.DateTimeData date) {
      StringBuffer message = new StringBuffer(25);
      this.append(message, date.year, 4);
      message.append('-');
      this.append(message, date.month, 2);
      message.append('-');
      this.append(message, date.day, 2);
      message.append('T');
      this.append(message, date.hour, 2);
      message.append(':');
      this.append(message, date.minute, 2);
      message.append(':');
      this.append(message, date.second);
      this.append(message, (char)date.utc, 0);
      return message.toString();
   }

   protected final void append(StringBuffer message, int value, int nch) {
      if (value == Integer.MIN_VALUE) {
         message.append(value);
      } else {
         if (value < 0) {
            message.append('-');
            value = -value;
         }

         if (nch == 4) {
            if (value < 10) {
               message.append("000");
            } else if (value < 100) {
               message.append("00");
            } else if (value < 1000) {
               message.append('0');
            }

            message.append(value);
         } else if (nch == 2) {
            if (value < 10) {
               message.append('0');
            }

            message.append(value);
         } else if (value != 0) {
            message.append((char)value);
         }

      }
   }

   protected final void append(StringBuffer message, double value) {
      if (value < 0.0D) {
         message.append('-');
         value = -value;
      }

      if (value < 10.0D) {
         message.append('0');
      }

      this.append2(message, value);
   }

   protected final void append2(StringBuffer message, double value) {
      int intValue = (int)value;
      if (value == (double)intValue) {
         message.append(intValue);
      } else {
         this.append3(message, value);
      }

   }

   private void append3(StringBuffer message, double value) {
      String d = String.valueOf(value);
      int eIndex = d.indexOf(69);
      if (eIndex == -1) {
         message.append(d);
      } else {
         int exp;
         int end;
         char c;
         int i;
         if (value < 1.0D) {
            try {
               exp = this.parseInt(d, eIndex + 2, d.length());
            } catch (Exception var11) {
               message.append(d);
               return;
            }

            message.append("0.");

            for(end = 1; end < exp; ++end) {
               message.append('0');
            }

            for(end = eIndex - 1; end > 0; --end) {
               char c = d.charAt(end);
               if (c != '0') {
                  break;
               }
            }

            for(i = 0; i <= end; ++i) {
               c = d.charAt(i);
               if (c != '.') {
                  message.append(c);
               }
            }
         } else {
            try {
               exp = this.parseInt(d, eIndex + 1, d.length());
            } catch (Exception var10) {
               message.append(d);
               return;
            }

            end = exp + 2;

            for(i = 0; i < eIndex; ++i) {
               c = d.charAt(i);
               if (c != '.') {
                  if (i == end) {
                     message.append('.');
                  }

                  message.append(c);
               }
            }

            for(i = end - eIndex; i > 0; --i) {
               message.append('0');
            }
         }

      }
   }

   protected double parseSecond(String buffer, int start, int end) throws NumberFormatException {
      int dot = -1;

      for(int i = start; i < end; ++i) {
         char ch = buffer.charAt(i);
         if (ch == '.') {
            dot = i;
         } else if (ch > '9' || ch < '0') {
            throw new NumberFormatException("'" + buffer + "' has wrong format");
         }
      }

      if (dot == -1) {
         if (start + 2 != end) {
            throw new NumberFormatException("'" + buffer + "' has wrong format");
         }
      } else if (start + 2 != dot || dot + 1 == end) {
         throw new NumberFormatException("'" + buffer + "' has wrong format");
      }

      return Double.parseDouble(buffer.substring(start, end));
   }

   private void cloneDate(AbstractDateTimeDV.DateTimeData finalValue, AbstractDateTimeDV.DateTimeData tempDate) {
      tempDate.year = finalValue.year;
      tempDate.month = finalValue.month;
      tempDate.day = finalValue.day;
      tempDate.hour = finalValue.hour;
      tempDate.minute = finalValue.minute;
      tempDate.second = finalValue.second;
      tempDate.utc = finalValue.utc;
      tempDate.timezoneHr = finalValue.timezoneHr;
      tempDate.timezoneMin = finalValue.timezoneMin;
   }

   protected XMLGregorianCalendar getXMLGregorianCalendar(AbstractDateTimeDV.DateTimeData data) {
      return null;
   }

   protected Duration getDuration(AbstractDateTimeDV.DateTimeData data) {
      return null;
   }

   protected final BigDecimal getFractionalSecondsAsBigDecimal(AbstractDateTimeDV.DateTimeData data) {
      StringBuffer buf = new StringBuffer();
      this.append3(buf, data.unNormSecond);
      String value = buf.toString();
      int index = value.indexOf(46);
      if (index == -1) {
         return null;
      } else {
         value = value.substring(index);
         BigDecimal _val = new BigDecimal(value);
         return _val.compareTo(BigDecimal.valueOf(0L)) == 0 ? null : _val;
      }
   }

   static final class DateTimeData implements XSDateTime {
      int year;
      int month;
      int day;
      int hour;
      int minute;
      int utc;
      double second;
      int timezoneHr;
      int timezoneMin;
      private String originalValue;
      boolean normalized = true;
      int unNormYear;
      int unNormMonth;
      int unNormDay;
      int unNormHour;
      int unNormMinute;
      double unNormSecond;
      int position;
      final AbstractDateTimeDV type;
      private volatile String canonical;

      public DateTimeData(String originalValue, AbstractDateTimeDV type) {
         this.originalValue = originalValue;
         this.type = type;
      }

      public DateTimeData(int year, int month, int day, int hour, int minute, double second, int utc, String originalValue, boolean normalized, AbstractDateTimeDV type) {
         this.year = year;
         this.month = month;
         this.day = day;
         this.hour = hour;
         this.minute = minute;
         this.second = second;
         this.utc = utc;
         this.type = type;
         this.originalValue = originalValue;
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof AbstractDateTimeDV.DateTimeData)) {
            return false;
         } else {
            return this.type.compareDates(this, (AbstractDateTimeDV.DateTimeData)obj, true) == 0;
         }
      }

      public int hashCode() {
         AbstractDateTimeDV.DateTimeData tempDate = new AbstractDateTimeDV.DateTimeData((String)null, this.type);
         this.type.cloneDate(this, tempDate);
         this.type.normalize(tempDate);
         return this.type.dateToString(tempDate).hashCode();
      }

      public String toString() {
         if (this.canonical == null) {
            this.canonical = this.type.dateToString(this);
         }

         return this.canonical;
      }

      public int getYears() {
         if (this.type instanceof DurationDV) {
            return 0;
         } else {
            return this.normalized ? this.year : this.unNormYear;
         }
      }

      public int getMonths() {
         if (this.type instanceof DurationDV) {
            return this.year * 12 + this.month;
         } else {
            return this.normalized ? this.month : this.unNormMonth;
         }
      }

      public int getDays() {
         if (this.type instanceof DurationDV) {
            return 0;
         } else {
            return this.normalized ? this.day : this.unNormDay;
         }
      }

      public int getHours() {
         if (this.type instanceof DurationDV) {
            return 0;
         } else {
            return this.normalized ? this.hour : this.unNormHour;
         }
      }

      public int getMinutes() {
         if (this.type instanceof DurationDV) {
            return 0;
         } else {
            return this.normalized ? this.minute : this.unNormMinute;
         }
      }

      public double getSeconds() {
         if (this.type instanceof DurationDV) {
            return (double)(this.day * 24 * 60 * 60 + this.hour * 60 * 60 + this.minute * 60) + this.second;
         } else {
            return this.normalized ? this.second : this.unNormSecond;
         }
      }

      public boolean hasTimeZone() {
         return this.utc != 0;
      }

      public int getTimeZoneHours() {
         return this.timezoneHr;
      }

      public int getTimeZoneMinutes() {
         return this.timezoneMin;
      }

      public String getLexicalValue() {
         return this.originalValue;
      }

      public XSDateTime normalize() {
         if (!this.normalized) {
            AbstractDateTimeDV.DateTimeData dt = (AbstractDateTimeDV.DateTimeData)this.clone();
            dt.normalized = true;
            return dt;
         } else {
            return this;
         }
      }

      public boolean isNormalized() {
         return this.normalized;
      }

      public Object clone() {
         AbstractDateTimeDV.DateTimeData dt = new AbstractDateTimeDV.DateTimeData(this.year, this.month, this.day, this.hour, this.minute, this.second, this.utc, this.originalValue, this.normalized, this.type);
         dt.canonical = this.canonical;
         dt.position = this.position;
         dt.timezoneHr = this.timezoneHr;
         dt.timezoneMin = this.timezoneMin;
         dt.unNormYear = this.unNormYear;
         dt.unNormMonth = this.unNormMonth;
         dt.unNormDay = this.unNormDay;
         dt.unNormHour = this.unNormHour;
         dt.unNormMinute = this.unNormMinute;
         dt.unNormSecond = this.unNormSecond;
         return dt;
      }

      public XMLGregorianCalendar getXMLGregorianCalendar() {
         return this.type.getXMLGregorianCalendar(this);
      }

      public Duration getDuration() {
         return this.type.getDuration(this);
      }
   }
}
