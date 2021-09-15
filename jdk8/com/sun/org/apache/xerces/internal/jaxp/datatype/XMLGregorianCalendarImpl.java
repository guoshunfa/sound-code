package com.sun.org.apache.xerces.internal.jaxp.datatype;

import com.sun.org.apache.xerces.internal.util.DatatypeMessageFormatter;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

public class XMLGregorianCalendarImpl extends XMLGregorianCalendar implements Serializable, Cloneable {
   private BigInteger eon = null;
   private int year = Integer.MIN_VALUE;
   private int month = Integer.MIN_VALUE;
   private int day = Integer.MIN_VALUE;
   private int timezone = Integer.MIN_VALUE;
   private int hour = Integer.MIN_VALUE;
   private int minute = Integer.MIN_VALUE;
   private int second = Integer.MIN_VALUE;
   private BigDecimal fractionalSecond = null;
   private static final BigInteger BILLION = new BigInteger("1000000000");
   private static final Date PURE_GREGORIAN_CHANGE = new Date(Long.MIN_VALUE);
   private static final int YEAR = 0;
   private static final int MONTH = 1;
   private static final int DAY = 2;
   private static final int HOUR = 3;
   private static final int MINUTE = 4;
   private static final int SECOND = 5;
   private static final int MILLISECOND = 6;
   private static final int TIMEZONE = 7;
   private static final String[] FIELD_NAME = new String[]{"Year", "Month", "Day", "Hour", "Minute", "Second", "Millisecond", "Timezone"};
   private static final long serialVersionUID = 1L;
   public static final XMLGregorianCalendar LEAP_YEAR_DEFAULT = createDateTime(400, 1, 1, 0, 0, 0, Integer.MIN_VALUE, Integer.MIN_VALUE);
   private static final BigInteger FOUR = BigInteger.valueOf(4L);
   private static final BigInteger HUNDRED = BigInteger.valueOf(100L);
   private static final BigInteger FOUR_HUNDRED = BigInteger.valueOf(400L);
   private static final BigInteger SIXTY = BigInteger.valueOf(60L);
   private static final BigInteger TWENTY_FOUR = BigInteger.valueOf(24L);
   private static final BigInteger TWELVE = BigInteger.valueOf(12L);
   private static final BigDecimal DECIMAL_ZERO = new BigDecimal("0");
   private static final BigDecimal DECIMAL_ONE = new BigDecimal("1");
   private static final BigDecimal DECIMAL_SIXTY = new BigDecimal("60");
   private static int[] daysInMonth = new int[]{0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

   protected XMLGregorianCalendarImpl(String lexicalRepresentation) throws IllegalArgumentException {
      String format = null;
      String lexRep = lexicalRepresentation;
      int NOT_FOUND = true;
      int lexRepLength = lexicalRepresentation.length();
      if (lexicalRepresentation.indexOf(84) != -1) {
         format = "%Y-%M-%DT%h:%m:%s%z";
      } else if (lexRepLength >= 3 && lexicalRepresentation.charAt(2) == ':') {
         format = "%h:%m:%s%z";
      } else if (lexicalRepresentation.startsWith("--")) {
         if (lexRepLength >= 3 && lexicalRepresentation.charAt(2) == '-') {
            format = "---%D%z";
         } else if (lexRepLength != 4 && lexRepLength != 5 && lexRepLength != 10) {
            format = "--%M-%D%z";
         } else {
            format = "--%M%z";
         }
      } else {
         int countSeparator = 0;
         int timezoneOffset = lexicalRepresentation.indexOf(58);
         if (timezoneOffset != -1) {
            lexRepLength -= 6;
         }

         for(int i = 1; i < lexRepLength; ++i) {
            if (lexRep.charAt(i) == '-') {
               ++countSeparator;
            }
         }

         if (countSeparator == 0) {
            format = "%Y%z";
         } else if (countSeparator == 1) {
            format = "%Y-%M%z";
         } else {
            format = "%Y-%M-%D%z";
         }
      }

      XMLGregorianCalendarImpl.Parser p = new XMLGregorianCalendarImpl.Parser(format, lexRep);
      p.parse();
      if (!this.isValid()) {
         throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage((Locale)null, "InvalidXGCRepresentation", new Object[]{lexicalRepresentation}));
      }
   }

   public XMLGregorianCalendarImpl() {
   }

   protected XMLGregorianCalendarImpl(BigInteger year, int month, int day, int hour, int minute, int second, BigDecimal fractionalSecond, int timezone) {
      this.setYear(year);
      this.setMonth(month);
      this.setDay(day);
      this.setTime(hour, minute, second, fractionalSecond);
      this.setTimezone(timezone);
      if (!this.isValid()) {
         throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage((Locale)null, "InvalidXGCValue-fractional", new Object[]{year, new Integer(month), new Integer(day), new Integer(hour), new Integer(minute), new Integer(second), fractionalSecond, new Integer(timezone)}));
      }
   }

   private XMLGregorianCalendarImpl(int year, int month, int day, int hour, int minute, int second, int millisecond, int timezone) {
      this.setYear(year);
      this.setMonth(month);
      this.setDay(day);
      this.setTime(hour, minute, second);
      this.setTimezone(timezone);
      this.setMillisecond(millisecond);
      if (!this.isValid()) {
         throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage((Locale)null, "InvalidXGCValue-milli", new Object[]{new Integer(year), new Integer(month), new Integer(day), new Integer(hour), new Integer(minute), new Integer(second), new Integer(millisecond), new Integer(timezone)}));
      }
   }

   public XMLGregorianCalendarImpl(GregorianCalendar cal) {
      int year = cal.get(1);
      if (cal.get(0) == 0) {
         year = -year;
      }

      this.setYear(year);
      this.setMonth(cal.get(2) + 1);
      this.setDay(cal.get(5));
      this.setTime(cal.get(11), cal.get(12), cal.get(13), cal.get(14));
      int offsetInMinutes = (cal.get(15) + cal.get(16)) / '\uea60';
      this.setTimezone(offsetInMinutes);
   }

   public static XMLGregorianCalendar createDateTime(BigInteger year, int month, int day, int hours, int minutes, int seconds, BigDecimal fractionalSecond, int timezone) {
      return new XMLGregorianCalendarImpl(year, month, day, hours, minutes, seconds, fractionalSecond, timezone);
   }

   public static XMLGregorianCalendar createDateTime(int year, int month, int day, int hour, int minute, int second) {
      return new XMLGregorianCalendarImpl(year, month, day, hour, minute, second, Integer.MIN_VALUE, Integer.MIN_VALUE);
   }

   public static XMLGregorianCalendar createDateTime(int year, int month, int day, int hours, int minutes, int seconds, int milliseconds, int timezone) {
      return new XMLGregorianCalendarImpl(year, month, day, hours, minutes, seconds, milliseconds, timezone);
   }

   public static XMLGregorianCalendar createDate(int year, int month, int day, int timezone) {
      return new XMLGregorianCalendarImpl(year, month, day, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, timezone);
   }

   public static XMLGregorianCalendar createTime(int hours, int minutes, int seconds, int timezone) {
      return new XMLGregorianCalendarImpl(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, hours, minutes, seconds, Integer.MIN_VALUE, timezone);
   }

   public static XMLGregorianCalendar createTime(int hours, int minutes, int seconds, BigDecimal fractionalSecond, int timezone) {
      return new XMLGregorianCalendarImpl((BigInteger)null, Integer.MIN_VALUE, Integer.MIN_VALUE, hours, minutes, seconds, fractionalSecond, timezone);
   }

   public static XMLGregorianCalendar createTime(int hours, int minutes, int seconds, int milliseconds, int timezone) {
      return new XMLGregorianCalendarImpl(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, hours, minutes, seconds, milliseconds, timezone);
   }

   public BigInteger getEon() {
      return this.eon;
   }

   public int getYear() {
      return this.year;
   }

   public BigInteger getEonAndYear() {
      if (this.year != Integer.MIN_VALUE && this.eon != null) {
         return this.eon.add(BigInteger.valueOf((long)this.year));
      } else {
         return this.year != Integer.MIN_VALUE && this.eon == null ? BigInteger.valueOf((long)this.year) : null;
      }
   }

   public int getMonth() {
      return this.month;
   }

   public int getDay() {
      return this.day;
   }

   public int getTimezone() {
      return this.timezone;
   }

   public int getHour() {
      return this.hour;
   }

   public int getMinute() {
      return this.minute;
   }

   public int getSecond() {
      return this.second;
   }

   private BigDecimal getSeconds() {
      if (this.second == Integer.MIN_VALUE) {
         return DECIMAL_ZERO;
      } else {
         BigDecimal result = BigDecimal.valueOf((long)this.second);
         return this.fractionalSecond != null ? result.add(this.fractionalSecond) : result;
      }
   }

   public int getMillisecond() {
      return this.fractionalSecond == null ? Integer.MIN_VALUE : this.fractionalSecond.movePointRight(3).intValue();
   }

   public BigDecimal getFractionalSecond() {
      return this.fractionalSecond;
   }

   public void setYear(BigInteger year) {
      if (year == null) {
         this.eon = null;
         this.year = Integer.MIN_VALUE;
      } else {
         BigInteger temp = year.remainder(BILLION);
         this.year = temp.intValue();
         this.setEon(year.subtract(temp));
      }

   }

   public void setYear(int year) {
      if (year == Integer.MIN_VALUE) {
         this.year = Integer.MIN_VALUE;
         this.eon = null;
      } else if (Math.abs(year) < BILLION.intValue()) {
         this.year = year;
         this.eon = null;
      } else {
         BigInteger theYear = BigInteger.valueOf((long)year);
         BigInteger remainder = theYear.remainder(BILLION);
         this.year = remainder.intValue();
         this.setEon(theYear.subtract(remainder));
      }

   }

   private void setEon(BigInteger eon) {
      if (eon != null && eon.compareTo(BigInteger.ZERO) == 0) {
         this.eon = null;
      } else {
         this.eon = eon;
      }

   }

   public void setMonth(int month) {
      if ((month < 1 || 12 < month) && month != Integer.MIN_VALUE) {
         this.invalidFieldValue(1, month);
      }

      this.month = month;
   }

   public void setDay(int day) {
      if ((day < 1 || 31 < day) && day != Integer.MIN_VALUE) {
         this.invalidFieldValue(2, day);
      }

      this.day = day;
   }

   public void setTimezone(int offset) {
      if ((offset < -840 || 840 < offset) && offset != Integer.MIN_VALUE) {
         this.invalidFieldValue(7, offset);
      }

      this.timezone = offset;
   }

   public void setTime(int hour, int minute, int second) {
      this.setTime(hour, minute, second, (BigDecimal)null);
   }

   private void invalidFieldValue(int field, int value) {
      throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage((Locale)null, "InvalidFieldValue", new Object[]{new Integer(value), FIELD_NAME[field]}));
   }

   private void testHour() {
      if (this.getHour() == 24) {
         if (this.getMinute() != 0 || this.getSecond() != 0) {
            this.invalidFieldValue(3, this.getHour());
         }

         this.setHour(0, false);
         this.add(new DurationImpl(true, 0, 0, 1, 0, 0, 0));
      }

   }

   public void setHour(int hour) {
      this.setHour(hour, true);
   }

   private void setHour(int hour, boolean validate) {
      if ((hour < 0 || hour > 24) && hour != Integer.MIN_VALUE) {
         this.invalidFieldValue(3, hour);
      }

      this.hour = hour;
      if (validate) {
         this.testHour();
      }

   }

   public void setMinute(int minute) {
      if ((minute < 0 || 59 < minute) && minute != Integer.MIN_VALUE) {
         this.invalidFieldValue(4, minute);
      }

      this.minute = minute;
   }

   public void setSecond(int second) {
      if ((second < 0 || 60 < second) && second != Integer.MIN_VALUE) {
         this.invalidFieldValue(5, second);
      }

      this.second = second;
   }

   public void setTime(int hour, int minute, int second, BigDecimal fractional) {
      this.setHour(hour, false);
      this.setMinute(minute);
      if (second != 60) {
         this.setSecond(second);
      } else if ((hour != 23 || minute != 59) && (hour != 0 || minute != 0)) {
         this.invalidFieldValue(5, second);
      } else {
         this.setSecond(second);
      }

      this.setFractionalSecond(fractional);
      this.testHour();
   }

   public void setTime(int hour, int minute, int second, int millisecond) {
      this.setHour(hour, false);
      this.setMinute(minute);
      if (second != 60) {
         this.setSecond(second);
      } else if ((hour != 23 || minute != 59) && (hour != 0 || minute != 0)) {
         this.invalidFieldValue(5, second);
      } else {
         this.setSecond(second);
      }

      this.setMillisecond(millisecond);
      this.testHour();
   }

   public int compare(XMLGregorianCalendar rhs) {
      int result = true;
      XMLGregorianCalendarImpl P = (XMLGregorianCalendarImpl)this;
      XMLGregorianCalendarImpl Q = (XMLGregorianCalendarImpl)rhs;
      if (P.getTimezone() == Q.getTimezone()) {
         return internalCompare(P, Q);
      } else if (P.getTimezone() != Integer.MIN_VALUE && Q.getTimezone() != Integer.MIN_VALUE) {
         P = (XMLGregorianCalendarImpl)P.normalize();
         Q = (XMLGregorianCalendarImpl)Q.normalize();
         return internalCompare(P, Q);
      } else {
         XMLGregorianCalendar MaxP;
         XMLGregorianCalendar MinP;
         int result;
         if (P.getTimezone() != Integer.MIN_VALUE) {
            if (P.getTimezone() != 0) {
               P = (XMLGregorianCalendarImpl)P.normalize();
            }

            MaxP = Q.normalizeToTimezone(840);
            result = internalCompare(P, MaxP);
            if (result == -1) {
               return result;
            } else {
               MinP = Q.normalizeToTimezone(-840);
               result = internalCompare(P, MinP);
               return result == 1 ? result : 2;
            }
         } else {
            if (Q.getTimezone() != 0) {
               Q = (XMLGregorianCalendarImpl)Q.normalizeToTimezone(Q.getTimezone());
            }

            MaxP = P.normalizeToTimezone(-840);
            result = internalCompare(MaxP, Q);
            if (result == -1) {
               return result;
            } else {
               MinP = P.normalizeToTimezone(840);
               result = internalCompare(MinP, Q);
               return result == 1 ? result : 2;
            }
         }
      }
   }

   public XMLGregorianCalendar normalize() {
      XMLGregorianCalendar normalized = this.normalizeToTimezone(this.timezone);
      if (this.getTimezone() == Integer.MIN_VALUE) {
         normalized.setTimezone(Integer.MIN_VALUE);
      }

      if (this.getMillisecond() == Integer.MIN_VALUE) {
         normalized.setMillisecond(Integer.MIN_VALUE);
      }

      return normalized;
   }

   private XMLGregorianCalendar normalizeToTimezone(int timezone) {
      XMLGregorianCalendar result = (XMLGregorianCalendar)this.clone();
      int minutes = -timezone;
      Duration d = new DurationImpl(minutes >= 0, 0, 0, 0, 0, minutes < 0 ? -minutes : minutes, 0);
      result.add(d);
      result.setTimezone(0);
      return result;
   }

   private static int internalCompare(XMLGregorianCalendar P, XMLGregorianCalendar Q) {
      int result;
      if (P.getEon() == Q.getEon()) {
         result = compareField(P.getYear(), Q.getYear());
         if (result != 0) {
            return result;
         }
      } else {
         result = compareField(P.getEonAndYear(), Q.getEonAndYear());
         if (result != 0) {
            return result;
         }
      }

      result = compareField(P.getMonth(), Q.getMonth());
      if (result != 0) {
         return result;
      } else {
         result = compareField(P.getDay(), Q.getDay());
         if (result != 0) {
            return result;
         } else {
            result = compareField(P.getHour(), Q.getHour());
            if (result != 0) {
               return result;
            } else {
               result = compareField(P.getMinute(), Q.getMinute());
               if (result != 0) {
                  return result;
               } else {
                  result = compareField(P.getSecond(), Q.getSecond());
                  if (result != 0) {
                     return result;
                  } else {
                     result = compareField(P.getFractionalSecond(), Q.getFractionalSecond());
                     return result;
                  }
               }
            }
         }
      }
   }

   private static int compareField(int Pfield, int Qfield) {
      if (Pfield == Qfield) {
         return 0;
      } else if (Pfield != Integer.MIN_VALUE && Qfield != Integer.MIN_VALUE) {
         return Pfield < Qfield ? -1 : 1;
      } else {
         return 2;
      }
   }

   private static int compareField(BigInteger Pfield, BigInteger Qfield) {
      if (Pfield == null) {
         return Qfield == null ? 0 : 2;
      } else {
         return Qfield == null ? 2 : Pfield.compareTo(Qfield);
      }
   }

   private static int compareField(BigDecimal Pfield, BigDecimal Qfield) {
      if (Pfield == Qfield) {
         return 0;
      } else {
         if (Pfield == null) {
            Pfield = DECIMAL_ZERO;
         }

         if (Qfield == null) {
            Qfield = DECIMAL_ZERO;
         }

         return Pfield.compareTo(Qfield);
      }
   }

   public boolean equals(Object obj) {
      if (obj != null && obj instanceof XMLGregorianCalendar) {
         return this.compare((XMLGregorianCalendar)obj) == 0;
      } else {
         return false;
      }
   }

   public int hashCode() {
      int timezone = this.getTimezone();
      if (timezone == Integer.MIN_VALUE) {
         timezone = 0;
      }

      XMLGregorianCalendar gc = this;
      if (timezone != 0) {
         gc = this.normalizeToTimezone(this.getTimezone());
      }

      return ((XMLGregorianCalendar)gc).getYear() + ((XMLGregorianCalendar)gc).getMonth() + ((XMLGregorianCalendar)gc).getDay() + ((XMLGregorianCalendar)gc).getHour() + ((XMLGregorianCalendar)gc).getMinute() + ((XMLGregorianCalendar)gc).getSecond();
   }

   public static XMLGregorianCalendar parse(String lexicalRepresentation) {
      return new XMLGregorianCalendarImpl(lexicalRepresentation);
   }

   public String toXMLFormat() {
      QName typekind = this.getXMLSchemaType();
      String formatString = null;
      if (typekind == DatatypeConstants.DATETIME) {
         formatString = "%Y-%M-%DT%h:%m:%s%z";
      } else if (typekind == DatatypeConstants.DATE) {
         formatString = "%Y-%M-%D%z";
      } else if (typekind == DatatypeConstants.TIME) {
         formatString = "%h:%m:%s%z";
      } else if (typekind == DatatypeConstants.GMONTH) {
         formatString = "--%M%z";
      } else if (typekind == DatatypeConstants.GDAY) {
         formatString = "---%D%z";
      } else if (typekind == DatatypeConstants.GYEAR) {
         formatString = "%Y%z";
      } else if (typekind == DatatypeConstants.GYEARMONTH) {
         formatString = "%Y-%M%z";
      } else if (typekind == DatatypeConstants.GMONTHDAY) {
         formatString = "--%M-%D%z";
      }

      return this.format(formatString);
   }

   public QName getXMLSchemaType() {
      int mask = (this.year != Integer.MIN_VALUE ? 32 : 0) | (this.month != Integer.MIN_VALUE ? 16 : 0) | (this.day != Integer.MIN_VALUE ? 8 : 0) | (this.hour != Integer.MIN_VALUE ? 4 : 0) | (this.minute != Integer.MIN_VALUE ? 2 : 0) | (this.second != Integer.MIN_VALUE ? 1 : 0);
      switch(mask) {
      case 7:
         return DatatypeConstants.TIME;
      case 8:
         return DatatypeConstants.GDAY;
      case 16:
         return DatatypeConstants.GMONTH;
      case 24:
         return DatatypeConstants.GMONTHDAY;
      case 32:
         return DatatypeConstants.GYEAR;
      case 48:
         return DatatypeConstants.GYEARMONTH;
      case 56:
         return DatatypeConstants.DATE;
      case 63:
         return DatatypeConstants.DATETIME;
      default:
         throw new IllegalStateException(this.getClass().getName() + "#getXMLSchemaType() :" + DatatypeMessageFormatter.formatMessage((Locale)null, "InvalidXGCFields", (Object[])null));
      }
   }

   public boolean isValid() {
      if (this.getMonth() == 2) {
         int maxDays = 29;
         if (this.eon == null) {
            if (this.year != Integer.MIN_VALUE) {
               maxDays = maximumDayInMonthFor(this.year, this.getMonth());
            }
         } else {
            BigInteger years = this.getEonAndYear();
            if (years != null) {
               maxDays = maximumDayInMonthFor(this.getEonAndYear(), 2);
            }
         }

         if (this.getDay() > maxDays) {
            return false;
         }
      }

      if (this.getHour() == 24) {
         if (this.getMinute() != 0) {
            return false;
         }

         if (this.getSecond() != 0) {
            return false;
         }
      }

      if (this.eon == null) {
         if (this.year == 0) {
            return false;
         }
      } else {
         BigInteger yearField = this.getEonAndYear();
         if (yearField != null) {
            int result = compareField(yearField, BigInteger.ZERO);
            if (result == 0) {
               return false;
            }
         }
      }

      return true;
   }

   public void add(Duration duration) {
      boolean[] fieldUndefined = new boolean[]{false, false, false, false, false, false};
      int signum = duration.getSign();
      int startMonth = this.getMonth();
      if (startMonth == Integer.MIN_VALUE) {
         startMonth = 1;
         fieldUndefined[1] = true;
      }

      BigInteger dMonths = sanitize(duration.getField(DatatypeConstants.MONTHS), signum);
      BigInteger temp = BigInteger.valueOf((long)startMonth).add(dMonths);
      this.setMonth(temp.subtract(BigInteger.ONE).mod(TWELVE).intValue() + 1);
      BigInteger carry = (new BigDecimal(temp.subtract(BigInteger.ONE))).divide(new BigDecimal(TWELVE), 3).toBigInteger();
      BigInteger startYear = this.getEonAndYear();
      if (startYear == null) {
         fieldUndefined[0] = true;
         startYear = BigInteger.ZERO;
      }

      BigInteger dYears = sanitize(duration.getField(DatatypeConstants.YEARS), signum);
      BigInteger endYear = startYear.add(dYears).add(carry);
      this.setYear(endYear);
      BigDecimal startSeconds;
      if (this.getSecond() == Integer.MIN_VALUE) {
         fieldUndefined[5] = true;
         startSeconds = DECIMAL_ZERO;
      } else {
         startSeconds = this.getSeconds();
      }

      BigDecimal dSeconds = DurationImpl.sanitize((BigDecimal)duration.getField(DatatypeConstants.SECONDS), signum);
      BigDecimal tempBD = startSeconds.add(dSeconds);
      BigDecimal fQuotient = new BigDecimal((new BigDecimal(tempBD.toBigInteger())).divide(DECIMAL_SIXTY, 3).toBigInteger());
      BigDecimal endSeconds = tempBD.subtract(fQuotient.multiply(DECIMAL_SIXTY));
      carry = fQuotient.toBigInteger();
      this.setSecond(endSeconds.intValue());
      BigDecimal tempFracSeconds = endSeconds.subtract(new BigDecimal(BigInteger.valueOf((long)this.getSecond())));
      if (tempFracSeconds.compareTo(DECIMAL_ZERO) < 0) {
         this.setFractionalSecond(DECIMAL_ONE.add(tempFracSeconds));
         if (this.getSecond() == 0) {
            this.setSecond(59);
            carry = carry.subtract(BigInteger.ONE);
         } else {
            this.setSecond(this.getSecond() - 1);
         }
      } else {
         this.setFractionalSecond(tempFracSeconds);
      }

      int startMinutes = this.getMinute();
      if (startMinutes == Integer.MIN_VALUE) {
         fieldUndefined[4] = true;
         startMinutes = 0;
      }

      BigInteger dMinutes = sanitize(duration.getField(DatatypeConstants.MINUTES), signum);
      temp = BigInteger.valueOf((long)startMinutes).add(dMinutes).add(carry);
      this.setMinute(temp.mod(SIXTY).intValue());
      carry = (new BigDecimal(temp)).divide(DECIMAL_SIXTY, 3).toBigInteger();
      int startHours = this.getHour();
      if (startHours == Integer.MIN_VALUE) {
         fieldUndefined[3] = true;
         startHours = 0;
      }

      BigInteger dHours = sanitize(duration.getField(DatatypeConstants.HOURS), signum);
      temp = BigInteger.valueOf((long)startHours).add(dHours).add(carry);
      this.setHour(temp.mod(TWENTY_FOUR).intValue(), false);
      carry = (new BigDecimal(temp)).divide(new BigDecimal(TWENTY_FOUR), 3).toBigInteger();
      int startDay = this.getDay();
      if (startDay == Integer.MIN_VALUE) {
         fieldUndefined[2] = true;
         startDay = 1;
      }

      BigInteger dDays = sanitize(duration.getField(DatatypeConstants.DAYS), signum);
      int maxDayInMonth = maximumDayInMonthFor(this.getEonAndYear(), this.getMonth());
      BigInteger tempDays;
      if (startDay > maxDayInMonth) {
         tempDays = BigInteger.valueOf((long)maxDayInMonth);
      } else if (startDay < 1) {
         tempDays = BigInteger.ONE;
      } else {
         tempDays = BigInteger.valueOf((long)startDay);
      }

      BigInteger endDays = tempDays.add(dDays).add(carry);

      while(true) {
         byte monthCarry;
         int i;
         if (endDays.compareTo(BigInteger.ONE) < 0) {
            BigInteger mdimf = null;
            if (this.month >= 2) {
               mdimf = BigInteger.valueOf((long)maximumDayInMonthFor(this.getEonAndYear(), this.getMonth() - 1));
            } else {
               mdimf = BigInteger.valueOf((long)maximumDayInMonthFor(this.getEonAndYear().subtract(BigInteger.valueOf(1L)), 12));
            }

            endDays = endDays.add(mdimf);
            monthCarry = -1;
         } else {
            if (endDays.compareTo(BigInteger.valueOf((long)maximumDayInMonthFor(this.getEonAndYear(), this.getMonth()))) <= 0) {
               this.setDay(endDays.intValue());

               for(i = 0; i <= 5; ++i) {
                  if (fieldUndefined[i]) {
                     switch(i) {
                     case 0:
                        this.setYear(Integer.MIN_VALUE);
                        break;
                     case 1:
                        this.setMonth(Integer.MIN_VALUE);
                        break;
                     case 2:
                        this.setDay(Integer.MIN_VALUE);
                        break;
                     case 3:
                        this.setHour(Integer.MIN_VALUE, false);
                        break;
                     case 4:
                        this.setMinute(Integer.MIN_VALUE);
                        break;
                     case 5:
                        this.setSecond(Integer.MIN_VALUE);
                        this.setFractionalSecond((BigDecimal)null);
                     }
                  }
               }

               return;
            }

            endDays = endDays.add(BigInteger.valueOf((long)(-maximumDayInMonthFor(this.getEonAndYear(), this.getMonth()))));
            monthCarry = 1;
         }

         int intTemp = this.getMonth() + monthCarry;
         i = (intTemp - 1) % 12;
         int quotient;
         if (i < 0) {
            i = 12 + i + 1;
            quotient = (new BigDecimal(intTemp - 1)).divide(new BigDecimal(TWELVE), 0).intValue();
         } else {
            quotient = (intTemp - 1) / 12;
            ++i;
         }

         this.setMonth(i);
         if (quotient != 0) {
            this.setYear(this.getEonAndYear().add(BigInteger.valueOf((long)quotient)));
         }
      }
   }

   private static int maximumDayInMonthFor(BigInteger year, int month) {
      if (month != 2) {
         return daysInMonth[month];
      } else {
         return !year.mod(FOUR_HUNDRED).equals(BigInteger.ZERO) && (year.mod(HUNDRED).equals(BigInteger.ZERO) || !year.mod(FOUR).equals(BigInteger.ZERO)) ? daysInMonth[month] : 29;
      }
   }

   private static int maximumDayInMonthFor(int year, int month) {
      if (month != 2) {
         return daysInMonth[month];
      } else {
         return year % 400 != 0 && (year % 100 == 0 || year % 4 != 0) ? daysInMonth[2] : 29;
      }
   }

   public GregorianCalendar toGregorianCalendar() {
      GregorianCalendar result = null;
      int DEFAULT_TIMEZONE_OFFSET = Integer.MIN_VALUE;
      TimeZone tz = this.getTimeZone(Integer.MIN_VALUE);
      Locale locale = this.getDefaultLocale();
      result = new GregorianCalendar(tz, locale);
      result.clear();
      result.setGregorianChange(PURE_GREGORIAN_CHANGE);
      BigInteger year = this.getEonAndYear();
      if (year != null) {
         result.set(0, year.signum() == -1 ? 0 : 1);
         result.set(1, year.abs().intValue());
      }

      if (this.month != Integer.MIN_VALUE) {
         result.set(2, this.month - 1);
      }

      if (this.day != Integer.MIN_VALUE) {
         result.set(5, this.day);
      }

      if (this.hour != Integer.MIN_VALUE) {
         result.set(11, this.hour);
      }

      if (this.minute != Integer.MIN_VALUE) {
         result.set(12, this.minute);
      }

      if (this.second != Integer.MIN_VALUE) {
         result.set(13, this.second);
      }

      if (this.fractionalSecond != null) {
         result.set(14, this.getMillisecond());
      }

      return result;
   }

   private Locale getDefaultLocale() {
      String lang = SecuritySupport.getSystemProperty("user.language.format");
      String country = SecuritySupport.getSystemProperty("user.country.format");
      String variant = SecuritySupport.getSystemProperty("user.variant.format");
      Locale locale = null;
      if (lang != null) {
         if (country != null) {
            if (variant != null) {
               locale = new Locale(lang, country, variant);
            } else {
               locale = new Locale(lang, country);
            }
         } else {
            locale = new Locale(lang);
         }
      }

      if (locale == null) {
         locale = Locale.getDefault();
      }

      return locale;
   }

   public GregorianCalendar toGregorianCalendar(TimeZone timezone, Locale aLocale, XMLGregorianCalendar defaults) {
      GregorianCalendar result = null;
      TimeZone tz = timezone;
      if (timezone == null) {
         int defaultZoneoffset = Integer.MIN_VALUE;
         if (defaults != null) {
            defaultZoneoffset = defaults.getTimezone();
         }

         tz = this.getTimeZone(defaultZoneoffset);
      }

      if (aLocale == null) {
         aLocale = Locale.getDefault();
      }

      result = new GregorianCalendar(tz, aLocale);
      result.clear();
      result.setGregorianChange(PURE_GREGORIAN_CHANGE);
      BigInteger year = this.getEonAndYear();
      if (year != null) {
         result.set(0, year.signum() == -1 ? 0 : 1);
         result.set(1, year.abs().intValue());
      } else {
         BigInteger defaultYear = defaults != null ? defaults.getEonAndYear() : null;
         if (defaultYear != null) {
            result.set(0, defaultYear.signum() == -1 ? 0 : 1);
            result.set(1, defaultYear.abs().intValue());
         }
      }

      int defaultSecond;
      if (this.month != Integer.MIN_VALUE) {
         result.set(2, this.month - 1);
      } else {
         defaultSecond = defaults != null ? defaults.getMonth() : Integer.MIN_VALUE;
         if (defaultSecond != Integer.MIN_VALUE) {
            result.set(2, defaultSecond - 1);
         }
      }

      if (this.day != Integer.MIN_VALUE) {
         result.set(5, this.day);
      } else {
         defaultSecond = defaults != null ? defaults.getDay() : Integer.MIN_VALUE;
         if (defaultSecond != Integer.MIN_VALUE) {
            result.set(5, defaultSecond);
         }
      }

      if (this.hour != Integer.MIN_VALUE) {
         result.set(11, this.hour);
      } else {
         defaultSecond = defaults != null ? defaults.getHour() : Integer.MIN_VALUE;
         if (defaultSecond != Integer.MIN_VALUE) {
            result.set(11, defaultSecond);
         }
      }

      if (this.minute != Integer.MIN_VALUE) {
         result.set(12, this.minute);
      } else {
         defaultSecond = defaults != null ? defaults.getMinute() : Integer.MIN_VALUE;
         if (defaultSecond != Integer.MIN_VALUE) {
            result.set(12, defaultSecond);
         }
      }

      if (this.second != Integer.MIN_VALUE) {
         result.set(13, this.second);
      } else {
         defaultSecond = defaults != null ? defaults.getSecond() : Integer.MIN_VALUE;
         if (defaultSecond != Integer.MIN_VALUE) {
            result.set(13, defaultSecond);
         }
      }

      if (this.fractionalSecond != null) {
         result.set(14, this.getMillisecond());
      } else {
         BigDecimal defaultFractionalSecond = defaults != null ? defaults.getFractionalSecond() : null;
         if (defaultFractionalSecond != null) {
            result.set(14, defaults.getMillisecond());
         }
      }

      return result;
   }

   public TimeZone getTimeZone(int defaultZoneoffset) {
      TimeZone result = null;
      int zoneoffset = this.getTimezone();
      if (zoneoffset == Integer.MIN_VALUE) {
         zoneoffset = defaultZoneoffset;
      }

      if (zoneoffset == Integer.MIN_VALUE) {
         result = TimeZone.getDefault();
      } else {
         char sign = zoneoffset < 0 ? 45 : 43;
         if (sign == 45) {
            zoneoffset = -zoneoffset;
         }

         int hour = zoneoffset / 60;
         int minutes = zoneoffset - hour * 60;
         StringBuffer customTimezoneId = new StringBuffer(8);
         customTimezoneId.append("GMT");
         customTimezoneId.append((char)sign);
         customTimezoneId.append(hour);
         if (minutes != 0) {
            if (minutes < 10) {
               customTimezoneId.append('0');
            }

            customTimezoneId.append(minutes);
         }

         result = TimeZone.getTimeZone(customTimezoneId.toString());
      }

      return result;
   }

   public Object clone() {
      return new XMLGregorianCalendarImpl(this.getEonAndYear(), this.month, this.day, this.hour, this.minute, this.second, this.fractionalSecond, this.timezone);
   }

   public void clear() {
      this.eon = null;
      this.year = Integer.MIN_VALUE;
      this.month = Integer.MIN_VALUE;
      this.day = Integer.MIN_VALUE;
      this.timezone = Integer.MIN_VALUE;
      this.hour = Integer.MIN_VALUE;
      this.minute = Integer.MIN_VALUE;
      this.second = Integer.MIN_VALUE;
      this.fractionalSecond = null;
   }

   public void setMillisecond(int millisecond) {
      if (millisecond == Integer.MIN_VALUE) {
         this.fractionalSecond = null;
      } else {
         if ((millisecond < 0 || 999 < millisecond) && millisecond != Integer.MIN_VALUE) {
            this.invalidFieldValue(6, millisecond);
         }

         this.fractionalSecond = (new BigDecimal((long)millisecond)).movePointLeft(3);
      }

   }

   public void setFractionalSecond(BigDecimal fractional) {
      if (fractional == null || fractional.compareTo(DECIMAL_ZERO) >= 0 && fractional.compareTo(DECIMAL_ONE) <= 0) {
         this.fractionalSecond = fractional;
      } else {
         throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage((Locale)null, "InvalidFractional", new Object[]{fractional}));
      }
   }

   private static boolean isDigit(char ch) {
      return '0' <= ch && ch <= '9';
   }

   private String format(String format) {
      char[] buf = new char[32];
      int bufPtr = 0;
      int fidx = 0;
      int flen = format.length();

      while(true) {
         while(fidx < flen) {
            char fch = format.charAt(fidx++);
            if (fch == '%') {
               int offset;
               String frac;
               switch(format.charAt(fidx++)) {
               case 'D':
                  bufPtr = this.print2Number(buf, bufPtr, this.getDay());
                  break;
               case 'M':
                  bufPtr = this.print2Number(buf, bufPtr, this.getMonth());
                  break;
               case 'Y':
                  if (this.eon == null) {
                     offset = this.getYear();
                     if (offset < 0) {
                        buf[bufPtr++] = '-';
                        offset = -offset;
                     }

                     bufPtr = this.print4Number(buf, bufPtr, offset);
                     break;
                  }

                  frac = this.getEonAndYear().toString();
                  char[] n = new char[buf.length + frac.length()];
                  System.arraycopy(buf, 0, n, 0, bufPtr);
                  buf = n;

                  for(int i = frac.length(); i < 4; ++i) {
                     buf[bufPtr++] = '0';
                  }

                  frac.getChars(0, frac.length(), buf, bufPtr);
                  bufPtr += frac.length();
                  break;
               case 'h':
                  bufPtr = this.print2Number(buf, bufPtr, this.getHour());
                  break;
               case 'm':
                  bufPtr = this.print2Number(buf, bufPtr, this.getMinute());
                  break;
               case 's':
                  bufPtr = this.print2Number(buf, bufPtr, this.getSecond());
                  if (this.getFractionalSecond() == null) {
                     break;
                  }

                  frac = this.getFractionalSecond().toString();
                  int pos = frac.indexOf("E-");
                  if (pos >= 0) {
                     String zeros = frac.substring(pos + 2);
                     frac = frac.substring(0, pos);
                     pos = frac.indexOf(".");
                     if (pos >= 0) {
                        frac = frac.substring(0, pos) + frac.substring(pos + 1);
                     }

                     int count = Integer.parseInt(zeros);
                     if (count < 40) {
                        frac = "00000000000000000000000000000000000000000".substring(0, count - 1) + frac;
                     } else {
                        while(count > 1) {
                           frac = "0" + frac;
                           --count;
                        }
                     }

                     frac = "0." + frac;
                  }

                  char[] n = new char[buf.length + frac.length()];
                  System.arraycopy(buf, 0, n, 0, bufPtr);
                  buf = n;
                  frac.getChars(1, frac.length(), n, bufPtr);
                  bufPtr += frac.length() - 1;
                  break;
               case 'z':
                  offset = this.getTimezone();
                  if (offset == 0) {
                     buf[bufPtr++] = 'Z';
                  } else if (offset != Integer.MIN_VALUE) {
                     if (offset < 0) {
                        buf[bufPtr++] = '-';
                        offset *= -1;
                     } else {
                        buf[bufPtr++] = '+';
                     }

                     bufPtr = this.print2Number(buf, bufPtr, offset / 60);
                     buf[bufPtr++] = ':';
                     bufPtr = this.print2Number(buf, bufPtr, offset % 60);
                  }
                  break;
               default:
                  throw new InternalError();
               }
            } else {
               buf[bufPtr++] = fch;
            }
         }

         return new String(buf, 0, bufPtr);
      }
   }

   private int print2Number(char[] out, int bufptr, int number) {
      out[bufptr++] = (char)(48 + number / 10);
      out[bufptr++] = (char)(48 + number % 10);
      return bufptr;
   }

   private int print4Number(char[] out, int bufptr, int number) {
      out[bufptr + 3] = (char)(48 + number % 10);
      number /= 10;
      out[bufptr + 2] = (char)(48 + number % 10);
      number /= 10;
      out[bufptr + 1] = (char)(48 + number % 10);
      number /= 10;
      out[bufptr] = (char)(48 + number % 10);
      return bufptr + 4;
   }

   static BigInteger sanitize(Number value, int signum) {
      if (signum != 0 && value != null) {
         return signum < 0 ? ((BigInteger)value).negate() : (BigInteger)value;
      } else {
         return BigInteger.ZERO;
      }
   }

   public void reset() {
   }

   private final class Parser {
      private final String format;
      private final String value;
      private final int flen;
      private final int vlen;
      private int fidx;
      private int vidx;

      private Parser(String format, String value) {
         this.format = format;
         this.value = value;
         this.flen = format.length();
         this.vlen = value.length();
      }

      public void parse() throws IllegalArgumentException {
         while(this.fidx < this.flen) {
            char fch = this.format.charAt(this.fidx++);
            if (fch != '%') {
               this.skip(fch);
            } else {
               switch(this.format.charAt(this.fidx++)) {
               case 'D':
                  XMLGregorianCalendarImpl.this.setDay(this.parseInt(2, 2));
                  break;
               case 'M':
                  XMLGregorianCalendarImpl.this.setMonth(this.parseInt(2, 2));
                  break;
               case 'Y':
                  this.parseAndSetYear(4);
                  break;
               case 'h':
                  XMLGregorianCalendarImpl.this.setHour(this.parseInt(2, 2), false);
                  break;
               case 'm':
                  XMLGregorianCalendarImpl.this.setMinute(this.parseInt(2, 2));
                  break;
               case 's':
                  XMLGregorianCalendarImpl.this.setSecond(this.parseInt(2, 2));
                  if (this.peek() == '.') {
                     XMLGregorianCalendarImpl.this.setFractionalSecond(this.parseBigDecimal());
                  }
                  break;
               case 'z':
                  char vch = this.peek();
                  if (vch == 'Z') {
                     ++this.vidx;
                     XMLGregorianCalendarImpl.this.setTimezone(0);
                  } else if (vch == '+' || vch == '-') {
                     ++this.vidx;
                     int h = this.parseInt(2, 2);
                     this.skip(':');
                     int m = this.parseInt(2, 2);
                     XMLGregorianCalendarImpl.this.setTimezone((h * 60 + m) * (vch == '+' ? 1 : -1));
                  }
                  break;
               default:
                  throw new InternalError();
               }
            }
         }

         if (this.vidx != this.vlen) {
            throw new IllegalArgumentException(this.value);
         } else {
            XMLGregorianCalendarImpl.this.testHour();
         }
      }

      private char peek() throws IllegalArgumentException {
         return this.vidx == this.vlen ? '\uffff' : this.value.charAt(this.vidx);
      }

      private char read() throws IllegalArgumentException {
         if (this.vidx == this.vlen) {
            throw new IllegalArgumentException(this.value);
         } else {
            return this.value.charAt(this.vidx++);
         }
      }

      private void skip(char ch) throws IllegalArgumentException {
         if (this.read() != ch) {
            throw new IllegalArgumentException(this.value);
         }
      }

      private int parseInt(int minDigits, int maxDigits) throws IllegalArgumentException {
         int n = 0;

         char ch;
         int vstart;
         for(vstart = this.vidx; XMLGregorianCalendarImpl.isDigit(ch = this.peek()) && this.vidx - vstart <= maxDigits; n = n * 10 + ch - 48) {
            ++this.vidx;
         }

         if (this.vidx - vstart < minDigits) {
            throw new IllegalArgumentException(this.value);
         } else {
            return n;
         }
      }

      private void parseAndSetYear(int minDigits) throws IllegalArgumentException {
         int vstart = this.vidx;
         int n = 0;
         boolean neg = false;
         if (this.peek() == '-') {
            ++this.vidx;
            neg = true;
         }

         while(true) {
            char ch = this.peek();
            if (!XMLGregorianCalendarImpl.isDigit(ch)) {
               if (this.vidx - vstart < minDigits) {
                  throw new IllegalArgumentException(this.value);
               } else {
                  if (this.vidx - vstart < 7) {
                     if (neg) {
                        n = -n;
                     }

                     XMLGregorianCalendarImpl.this.year = n;
                     XMLGregorianCalendarImpl.this.eon = null;
                  } else {
                     XMLGregorianCalendarImpl.this.setYear(new BigInteger(this.value.substring(vstart, this.vidx)));
                  }

                  return;
               }
            }

            ++this.vidx;
            n = n * 10 + ch - 48;
         }
      }

      private BigDecimal parseBigDecimal() throws IllegalArgumentException {
         int vstart = this.vidx;
         if (this.peek() != '.') {
            throw new IllegalArgumentException(this.value);
         } else {
            ++this.vidx;

            while(XMLGregorianCalendarImpl.isDigit(this.peek())) {
               ++this.vidx;
            }

            return new BigDecimal(this.value.substring(vstart, this.vidx));
         }
      }

      // $FF: synthetic method
      Parser(String x1, String x2, Object x3) {
         this(x1, x2);
      }
   }
}
