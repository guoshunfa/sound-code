package com.sun.org.apache.xerces.internal.jaxp.datatype;

import com.sun.org.apache.xerces.internal.util.DatatypeMessageFormatter;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

class DurationImpl extends Duration implements Serializable {
   private static final int FIELD_NUM = 6;
   private static final DatatypeConstants.Field[] FIELDS;
   private static final int[] FIELD_IDS;
   private static final TimeZone GMT;
   private static final BigDecimal ZERO;
   protected int signum;
   protected BigInteger years;
   protected BigInteger months;
   protected BigInteger days;
   protected BigInteger hours;
   protected BigInteger minutes;
   protected BigDecimal seconds;
   private static final XMLGregorianCalendar[] TEST_POINTS;
   private static final BigDecimal[] FACTORS;
   private static final long serialVersionUID = 1L;

   public int getSign() {
      return this.signum;
   }

   protected int calcSignum(boolean isPositive) {
      if ((this.years == null || this.years.signum() == 0) && (this.months == null || this.months.signum() == 0) && (this.days == null || this.days.signum() == 0) && (this.hours == null || this.hours.signum() == 0) && (this.minutes == null || this.minutes.signum() == 0) && (this.seconds == null || this.seconds.signum() == 0)) {
         return 0;
      } else {
         return isPositive ? 1 : -1;
      }
   }

   protected DurationImpl(boolean isPositive, BigInteger years, BigInteger months, BigInteger days, BigInteger hours, BigInteger minutes, BigDecimal seconds) {
      this.years = years;
      this.months = months;
      this.days = days;
      this.hours = hours;
      this.minutes = minutes;
      this.seconds = seconds;
      this.signum = this.calcSignum(isPositive);
      if (years == null && months == null && days == null && hours == null && minutes == null && seconds == null) {
         throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage((Locale)null, "AllFieldsNull", (Object[])null));
      } else {
         testNonNegative(years, DatatypeConstants.YEARS);
         testNonNegative(months, DatatypeConstants.MONTHS);
         testNonNegative(days, DatatypeConstants.DAYS);
         testNonNegative(hours, DatatypeConstants.HOURS);
         testNonNegative(minutes, DatatypeConstants.MINUTES);
         testNonNegative(seconds, DatatypeConstants.SECONDS);
      }
   }

   protected static void testNonNegative(BigInteger n, DatatypeConstants.Field f) {
      if (n != null && n.signum() < 0) {
         throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage((Locale)null, "NegativeField", new Object[]{f.toString()}));
      }
   }

   protected static void testNonNegative(BigDecimal n, DatatypeConstants.Field f) {
      if (n != null && n.signum() < 0) {
         throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage((Locale)null, "NegativeField", new Object[]{f.toString()}));
      }
   }

   protected DurationImpl(boolean isPositive, int years, int months, int days, int hours, int minutes, int seconds) {
      this(isPositive, wrap(years), wrap(months), wrap(days), wrap(hours), wrap(minutes), seconds != Integer.MIN_VALUE ? new BigDecimal(String.valueOf(seconds)) : null);
   }

   protected static BigInteger wrap(int i) {
      return i == Integer.MIN_VALUE ? null : new BigInteger(String.valueOf(i));
   }

   protected DurationImpl(long durationInMilliSeconds) {
      long l = durationInMilliSeconds;
      if (durationInMilliSeconds > 0L) {
         this.signum = 1;
      } else if (durationInMilliSeconds < 0L) {
         this.signum = -1;
         if (durationInMilliSeconds == Long.MIN_VALUE) {
            l = durationInMilliSeconds + 1L;
         }

         l *= -1L;
      } else {
         this.signum = 0;
      }

      GregorianCalendar gregorianCalendar = new GregorianCalendar(GMT);
      gregorianCalendar.setTimeInMillis(l);
      long int2long = 0L;
      int2long = (long)(gregorianCalendar.get(1) - 1970);
      this.years = BigInteger.valueOf(int2long);
      int2long = (long)gregorianCalendar.get(2);
      this.months = BigInteger.valueOf(int2long);
      int2long = (long)(gregorianCalendar.get(5) - 1);
      this.days = BigInteger.valueOf(int2long);
      int2long = (long)gregorianCalendar.get(11);
      this.hours = BigInteger.valueOf(int2long);
      int2long = (long)gregorianCalendar.get(12);
      this.minutes = BigInteger.valueOf(int2long);
      int2long = (long)(gregorianCalendar.get(13) * 1000 + gregorianCalendar.get(14));
      this.seconds = BigDecimal.valueOf(int2long, 3);
   }

   protected DurationImpl(String lexicalRepresentation) throws IllegalArgumentException {
      String s = lexicalRepresentation;
      int[] idx = new int[1];
      int length = lexicalRepresentation.length();
      boolean timeRequired = false;
      if (lexicalRepresentation == null) {
         throw new NullPointerException();
      } else {
         idx[0] = 0;
         boolean positive;
         if (length != idx[0] && lexicalRepresentation.charAt(idx[0]) == '-') {
            int var10002 = idx[0]++;
            positive = false;
         } else {
            positive = true;
         }

         int var10001;
         int var10004;
         if (length != idx[0]) {
            var10004 = idx[0];
            var10001 = idx[0];
            idx[0] = var10004 + 1;
            if (lexicalRepresentation.charAt(var10001) != 'P') {
               throw new IllegalArgumentException(lexicalRepresentation);
            }
         }

         int dateLen = 0;
         String[] dateParts = new String[3];

         int[] datePartsIndex;
         for(datePartsIndex = new int[3]; length != idx[0] && isDigit(s.charAt(idx[0])) && dateLen < 3; dateParts[dateLen++] = parsePiece(s, idx)) {
            datePartsIndex[dateLen] = idx[0];
         }

         if (length != idx[0]) {
            var10004 = idx[0];
            var10001 = idx[0];
            idx[0] = var10004 + 1;
            if (s.charAt(var10001) != 'T') {
               throw new IllegalArgumentException(s);
            }

            timeRequired = true;
         }

         int timeLen = 0;
         String[] timeParts = new String[3];

         int[] timePartsIndex;
         for(timePartsIndex = new int[3]; length != idx[0] && isDigitOrPeriod(s.charAt(idx[0])) && timeLen < 3; timeParts[timeLen++] = parsePiece(s, idx)) {
            timePartsIndex[timeLen] = idx[0];
         }

         if (timeRequired && timeLen == 0) {
            throw new IllegalArgumentException(s);
         } else if (length != idx[0]) {
            throw new IllegalArgumentException(s);
         } else if (dateLen == 0 && timeLen == 0) {
            throw new IllegalArgumentException(s);
         } else {
            organizeParts(s, dateParts, datePartsIndex, dateLen, "YMD");
            organizeParts(s, timeParts, timePartsIndex, timeLen, "HMS");
            this.years = parseBigInteger(s, dateParts[0], datePartsIndex[0]);
            this.months = parseBigInteger(s, dateParts[1], datePartsIndex[1]);
            this.days = parseBigInteger(s, dateParts[2], datePartsIndex[2]);
            this.hours = parseBigInteger(s, timeParts[0], timePartsIndex[0]);
            this.minutes = parseBigInteger(s, timeParts[1], timePartsIndex[1]);
            this.seconds = parseBigDecimal(s, timeParts[2], timePartsIndex[2]);
            this.signum = this.calcSignum(positive);
         }
      }
   }

   private static boolean isDigit(char ch) {
      return '0' <= ch && ch <= '9';
   }

   private static boolean isDigitOrPeriod(char ch) {
      return isDigit(ch) || ch == '.';
   }

   private static String parsePiece(String whole, int[] idx) throws IllegalArgumentException {
      int start;
      int var10002;
      for(start = idx[0]; idx[0] < whole.length() && isDigitOrPeriod(whole.charAt(idx[0])); var10002 = idx[0]++) {
      }

      if (idx[0] == whole.length()) {
         throw new IllegalArgumentException(whole);
      } else {
         var10002 = idx[0]++;
         return whole.substring(start, idx[0]);
      }
   }

   private static void organizeParts(String whole, String[] parts, int[] partsIndex, int len, String tokens) throws IllegalArgumentException {
      int idx = tokens.length();

      for(int i = len - 1; i >= 0; --i) {
         int nidx = tokens.lastIndexOf(parts[i].charAt(parts[i].length() - 1), idx - 1);
         if (nidx == -1) {
            throw new IllegalArgumentException(whole);
         }

         for(int j = nidx + 1; j < idx; ++j) {
            parts[j] = null;
         }

         idx = nidx;
         parts[nidx] = parts[i];
         partsIndex[nidx] = partsIndex[i];
      }

      --idx;

      while(idx >= 0) {
         parts[idx] = null;
         --idx;
      }

   }

   private static BigInteger parseBigInteger(String whole, String part, int index) throws IllegalArgumentException {
      if (part == null) {
         return null;
      } else {
         part = part.substring(0, part.length() - 1);
         return new BigInteger(part);
      }
   }

   private static BigDecimal parseBigDecimal(String whole, String part, int index) throws IllegalArgumentException {
      if (part == null) {
         return null;
      } else {
         part = part.substring(0, part.length() - 1);
         return new BigDecimal(part);
      }
   }

   public int compare(Duration rhs) {
      BigInteger maxintAsBigInteger = BigInteger.valueOf(2147483647L);
      BigInteger minintAsBigInteger = BigInteger.valueOf(-2147483648L);
      if (this.years != null && this.years.compareTo(maxintAsBigInteger) == 1) {
         throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage((Locale)null, "TooLarge", new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.YEARS.toString(), this.years.toString()}));
      } else if (this.months != null && this.months.compareTo(maxintAsBigInteger) == 1) {
         throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage((Locale)null, "TooLarge", new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.MONTHS.toString(), this.months.toString()}));
      } else if (this.days != null && this.days.compareTo(maxintAsBigInteger) == 1) {
         throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage((Locale)null, "TooLarge", new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.DAYS.toString(), this.days.toString()}));
      } else if (this.hours != null && this.hours.compareTo(maxintAsBigInteger) == 1) {
         throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage((Locale)null, "TooLarge", new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.HOURS.toString(), this.hours.toString()}));
      } else if (this.minutes != null && this.minutes.compareTo(maxintAsBigInteger) == 1) {
         throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage((Locale)null, "TooLarge", new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.MINUTES.toString(), this.minutes.toString()}));
      } else if (this.seconds != null && this.seconds.toBigInteger().compareTo(maxintAsBigInteger) == 1) {
         throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage((Locale)null, "TooLarge", new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.SECONDS.toString(), this.seconds.toString()}));
      } else {
         BigInteger rhsYears = (BigInteger)rhs.getField(DatatypeConstants.YEARS);
         if (rhsYears != null && rhsYears.compareTo(maxintAsBigInteger) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage((Locale)null, "TooLarge", new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.YEARS.toString(), rhsYears.toString()}));
         } else {
            BigInteger rhsMonths = (BigInteger)rhs.getField(DatatypeConstants.MONTHS);
            if (rhsMonths != null && rhsMonths.compareTo(maxintAsBigInteger) == 1) {
               throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage((Locale)null, "TooLarge", new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.MONTHS.toString(), rhsMonths.toString()}));
            } else {
               BigInteger rhsDays = (BigInteger)rhs.getField(DatatypeConstants.DAYS);
               if (rhsDays != null && rhsDays.compareTo(maxintAsBigInteger) == 1) {
                  throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage((Locale)null, "TooLarge", new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.DAYS.toString(), rhsDays.toString()}));
               } else {
                  BigInteger rhsHours = (BigInteger)rhs.getField(DatatypeConstants.HOURS);
                  if (rhsHours != null && rhsHours.compareTo(maxintAsBigInteger) == 1) {
                     throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage((Locale)null, "TooLarge", new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.HOURS.toString(), rhsHours.toString()}));
                  } else {
                     BigInteger rhsMinutes = (BigInteger)rhs.getField(DatatypeConstants.MINUTES);
                     if (rhsMinutes != null && rhsMinutes.compareTo(maxintAsBigInteger) == 1) {
                        throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage((Locale)null, "TooLarge", new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.MINUTES.toString(), rhsMinutes.toString()}));
                     } else {
                        BigDecimal rhsSecondsAsBigDecimal = (BigDecimal)rhs.getField(DatatypeConstants.SECONDS);
                        BigInteger rhsSeconds = null;
                        if (rhsSecondsAsBigDecimal != null) {
                           rhsSeconds = rhsSecondsAsBigDecimal.toBigInteger();
                        }

                        if (rhsSeconds != null && rhsSeconds.compareTo(maxintAsBigInteger) == 1) {
                           throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage((Locale)null, "TooLarge", new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.SECONDS.toString(), rhsSeconds.toString()}));
                        } else {
                           GregorianCalendar lhsCalendar = new GregorianCalendar(1970, 1, 1, 0, 0, 0);
                           lhsCalendar.add(1, this.getYears() * this.getSign());
                           lhsCalendar.add(2, this.getMonths() * this.getSign());
                           lhsCalendar.add(6, this.getDays() * this.getSign());
                           lhsCalendar.add(11, this.getHours() * this.getSign());
                           lhsCalendar.add(12, this.getMinutes() * this.getSign());
                           lhsCalendar.add(13, this.getSeconds() * this.getSign());
                           GregorianCalendar rhsCalendar = new GregorianCalendar(1970, 1, 1, 0, 0, 0);
                           rhsCalendar.add(1, rhs.getYears() * rhs.getSign());
                           rhsCalendar.add(2, rhs.getMonths() * rhs.getSign());
                           rhsCalendar.add(6, rhs.getDays() * rhs.getSign());
                           rhsCalendar.add(11, rhs.getHours() * rhs.getSign());
                           rhsCalendar.add(12, rhs.getMinutes() * rhs.getSign());
                           rhsCalendar.add(13, rhs.getSeconds() * rhs.getSign());
                           return lhsCalendar.equals(rhsCalendar) ? 0 : this.compareDates(this, rhs);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private int compareDates(Duration duration1, Duration duration2) {
      int resultA = true;
      int resultB = true;
      XMLGregorianCalendar tempA = (XMLGregorianCalendar)TEST_POINTS[0].clone();
      XMLGregorianCalendar tempB = (XMLGregorianCalendar)TEST_POINTS[0].clone();
      tempA.add(duration1);
      tempB.add(duration2);
      int resultA = tempA.compare(tempB);
      if (resultA == 2) {
         return 2;
      } else {
         tempA = (XMLGregorianCalendar)TEST_POINTS[1].clone();
         tempB = (XMLGregorianCalendar)TEST_POINTS[1].clone();
         tempA.add(duration1);
         tempB.add(duration2);
         int resultB = tempA.compare(tempB);
         resultA = this.compareResults(resultA, resultB);
         if (resultA == 2) {
            return 2;
         } else {
            tempA = (XMLGregorianCalendar)TEST_POINTS[2].clone();
            tempB = (XMLGregorianCalendar)TEST_POINTS[2].clone();
            tempA.add(duration1);
            tempB.add(duration2);
            resultB = tempA.compare(tempB);
            resultA = this.compareResults(resultA, resultB);
            if (resultA == 2) {
               return 2;
            } else {
               tempA = (XMLGregorianCalendar)TEST_POINTS[3].clone();
               tempB = (XMLGregorianCalendar)TEST_POINTS[3].clone();
               tempA.add(duration1);
               tempB.add(duration2);
               resultB = tempA.compare(tempB);
               resultA = this.compareResults(resultA, resultB);
               return resultA;
            }
         }
      }
   }

   private int compareResults(int resultA, int resultB) {
      if (resultB == 2) {
         return 2;
      } else {
         return resultA != resultB ? 2 : resultA;
      }
   }

   public int hashCode() {
      Calendar cal = TEST_POINTS[0].toGregorianCalendar();
      this.addTo((Calendar)cal);
      return (int)getCalendarTimeInMillis(cal);
   }

   public String toString() {
      StringBuffer buf = new StringBuffer();
      if (this.signum < 0) {
         buf.append('-');
      }

      buf.append('P');
      if (this.years != null) {
         buf.append(this.years + "Y");
      }

      if (this.months != null) {
         buf.append(this.months + "M");
      }

      if (this.days != null) {
         buf.append(this.days + "D");
      }

      if (this.hours != null || this.minutes != null || this.seconds != null) {
         buf.append('T');
         if (this.hours != null) {
            buf.append(this.hours + "H");
         }

         if (this.minutes != null) {
            buf.append(this.minutes + "M");
         }

         if (this.seconds != null) {
            buf.append(this.toString(this.seconds) + "S");
         }
      }

      return buf.toString();
   }

   private String toString(BigDecimal bd) {
      String intString = bd.unscaledValue().toString();
      int scale = bd.scale();
      if (scale == 0) {
         return intString;
      } else {
         int insertionPoint = intString.length() - scale;
         if (insertionPoint == 0) {
            return "0." + intString;
         } else {
            StringBuffer buf;
            if (insertionPoint > 0) {
               buf = new StringBuffer(intString);
               buf.insert(insertionPoint, '.');
            } else {
               buf = new StringBuffer(3 - insertionPoint + intString.length());
               buf.append("0.");

               for(int i = 0; i < -insertionPoint; ++i) {
                  buf.append('0');
               }

               buf.append(intString);
            }

            return buf.toString();
         }
      }
   }

   public boolean isSet(DatatypeConstants.Field field) {
      String methodName;
      if (field == null) {
         methodName = "javax.xml.datatype.Duration#isSet(DatatypeConstants.Field field)";
         throw new NullPointerException(DatatypeMessageFormatter.formatMessage((Locale)null, "FieldCannotBeNull", new Object[]{methodName}));
      } else if (field == DatatypeConstants.YEARS) {
         return this.years != null;
      } else if (field == DatatypeConstants.MONTHS) {
         return this.months != null;
      } else if (field == DatatypeConstants.DAYS) {
         return this.days != null;
      } else if (field == DatatypeConstants.HOURS) {
         return this.hours != null;
      } else if (field == DatatypeConstants.MINUTES) {
         return this.minutes != null;
      } else if (field == DatatypeConstants.SECONDS) {
         return this.seconds != null;
      } else {
         methodName = "javax.xml.datatype.Duration#isSet(DatatypeConstants.Field field)";
         throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage((Locale)null, "UnknownField", new Object[]{methodName, field.toString()}));
      }
   }

   public Number getField(DatatypeConstants.Field field) {
      String methodName;
      if (field == null) {
         methodName = "javax.xml.datatype.Duration#isSet(DatatypeConstants.Field field) ";
         throw new NullPointerException(DatatypeMessageFormatter.formatMessage((Locale)null, "FieldCannotBeNull", new Object[]{methodName}));
      } else if (field == DatatypeConstants.YEARS) {
         return this.years;
      } else if (field == DatatypeConstants.MONTHS) {
         return this.months;
      } else if (field == DatatypeConstants.DAYS) {
         return this.days;
      } else if (field == DatatypeConstants.HOURS) {
         return this.hours;
      } else if (field == DatatypeConstants.MINUTES) {
         return this.minutes;
      } else if (field == DatatypeConstants.SECONDS) {
         return this.seconds;
      } else {
         methodName = "javax.xml.datatype.Duration#(getSet(DatatypeConstants.Field field)";
         throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage((Locale)null, "UnknownField", new Object[]{methodName, field.toString()}));
      }
   }

   public int getYears() {
      return this.getInt(DatatypeConstants.YEARS);
   }

   public int getMonths() {
      return this.getInt(DatatypeConstants.MONTHS);
   }

   public int getDays() {
      return this.getInt(DatatypeConstants.DAYS);
   }

   public int getHours() {
      return this.getInt(DatatypeConstants.HOURS);
   }

   public int getMinutes() {
      return this.getInt(DatatypeConstants.MINUTES);
   }

   public int getSeconds() {
      return this.getInt(DatatypeConstants.SECONDS);
   }

   private int getInt(DatatypeConstants.Field field) {
      Number n = this.getField(field);
      return n == null ? 0 : n.intValue();
   }

   public long getTimeInMillis(Calendar startInstant) {
      Calendar cal = (Calendar)startInstant.clone();
      this.addTo(cal);
      return getCalendarTimeInMillis(cal) - getCalendarTimeInMillis(startInstant);
   }

   public long getTimeInMillis(Date startInstant) {
      Calendar cal = new GregorianCalendar();
      cal.setTime(startInstant);
      this.addTo((Calendar)cal);
      return getCalendarTimeInMillis(cal) - startInstant.getTime();
   }

   public Duration normalizeWith(Calendar startTimeInstant) {
      Calendar c = (Calendar)startTimeInstant.clone();
      c.add(1, this.getYears() * this.signum);
      c.add(2, this.getMonths() * this.signum);
      c.add(5, this.getDays() * this.signum);
      long diff = getCalendarTimeInMillis(c) - getCalendarTimeInMillis(startTimeInstant);
      int days = (int)(diff / 86400000L);
      return new DurationImpl(days >= 0, (BigInteger)null, (BigInteger)null, wrap(Math.abs(days)), (BigInteger)this.getField(DatatypeConstants.HOURS), (BigInteger)this.getField(DatatypeConstants.MINUTES), (BigDecimal)this.getField(DatatypeConstants.SECONDS));
   }

   public Duration multiply(int factor) {
      return this.multiply(BigDecimal.valueOf((long)factor));
   }

   public Duration multiply(BigDecimal factor) {
      BigDecimal carry = ZERO;
      int factorSign = factor.signum();
      factor = factor.abs();
      BigDecimal[] buf = new BigDecimal[6];

      for(int i = 0; i < 5; ++i) {
         BigDecimal bd = this.getFieldAsBigDecimal(FIELDS[i]);
         bd = bd.multiply(factor).add(carry);
         buf[i] = bd.setScale(0, 1);
         bd = bd.subtract(buf[i]);
         if (i == 1) {
            if (bd.signum() != 0) {
               throw new IllegalStateException();
            }

            carry = ZERO;
         } else {
            carry = bd.multiply(FACTORS[i]);
         }
      }

      if (this.seconds != null) {
         buf[5] = this.seconds.multiply(factor).add(carry);
      } else {
         buf[5] = carry;
      }

      return new DurationImpl(this.signum * factorSign >= 0, toBigInteger(buf[0], null == this.years), toBigInteger(buf[1], null == this.months), toBigInteger(buf[2], null == this.days), toBigInteger(buf[3], null == this.hours), toBigInteger(buf[4], null == this.minutes), buf[5].signum() == 0 && this.seconds == null ? null : buf[5]);
   }

   private BigDecimal getFieldAsBigDecimal(DatatypeConstants.Field f) {
      if (f == DatatypeConstants.SECONDS) {
         return this.seconds != null ? this.seconds : ZERO;
      } else {
         BigInteger bi = (BigInteger)this.getField(f);
         return bi == null ? ZERO : new BigDecimal(bi);
      }
   }

   private static BigInteger toBigInteger(BigDecimal value, boolean canBeNull) {
      return canBeNull && value.signum() == 0 ? null : value.unscaledValue();
   }

   public Duration add(Duration rhs) {
      BigDecimal[] buf = new BigDecimal[]{sanitize((BigInteger)this.getField(DatatypeConstants.YEARS), this.getSign()).add(sanitize((BigInteger)rhs.getField(DatatypeConstants.YEARS), rhs.getSign())), sanitize((BigInteger)this.getField(DatatypeConstants.MONTHS), this.getSign()).add(sanitize((BigInteger)rhs.getField(DatatypeConstants.MONTHS), rhs.getSign())), sanitize((BigInteger)this.getField(DatatypeConstants.DAYS), this.getSign()).add(sanitize((BigInteger)rhs.getField(DatatypeConstants.DAYS), rhs.getSign())), sanitize((BigInteger)this.getField(DatatypeConstants.HOURS), this.getSign()).add(sanitize((BigInteger)rhs.getField(DatatypeConstants.HOURS), rhs.getSign())), sanitize((BigInteger)this.getField(DatatypeConstants.MINUTES), this.getSign()).add(sanitize((BigInteger)rhs.getField(DatatypeConstants.MINUTES), rhs.getSign())), sanitize((BigDecimal)this.getField(DatatypeConstants.SECONDS), this.getSign()).add(sanitize((BigDecimal)rhs.getField(DatatypeConstants.SECONDS), rhs.getSign()))};
      alignSigns(buf, 0, 2);
      alignSigns(buf, 2, 6);
      int s = 0;

      for(int i = 0; i < 6; ++i) {
         if (s * buf[i].signum() < 0) {
            throw new IllegalStateException();
         }

         if (s == 0) {
            s = buf[i].signum();
         }
      }

      return new DurationImpl(s >= 0, toBigInteger(sanitize(buf[0], s), this.getField(DatatypeConstants.YEARS) == null && rhs.getField(DatatypeConstants.YEARS) == null), toBigInteger(sanitize(buf[1], s), this.getField(DatatypeConstants.MONTHS) == null && rhs.getField(DatatypeConstants.MONTHS) == null), toBigInteger(sanitize(buf[2], s), this.getField(DatatypeConstants.DAYS) == null && rhs.getField(DatatypeConstants.DAYS) == null), toBigInteger(sanitize(buf[3], s), this.getField(DatatypeConstants.HOURS) == null && rhs.getField(DatatypeConstants.HOURS) == null), toBigInteger(sanitize(buf[4], s), this.getField(DatatypeConstants.MINUTES) == null && rhs.getField(DatatypeConstants.MINUTES) == null), buf[5].signum() == 0 && this.getField(DatatypeConstants.SECONDS) == null && rhs.getField(DatatypeConstants.SECONDS) == null ? null : sanitize(buf[5], s));
   }

   private static void alignSigns(BigDecimal[] buf, int start, int end) {
      boolean touched;
      do {
         touched = false;
         int s = 0;

         for(int i = start; i < end; ++i) {
            if (s * buf[i].signum() < 0) {
               touched = true;
               BigDecimal borrow = buf[i].abs().divide(FACTORS[i - 1], 0);
               if (buf[i].signum() > 0) {
                  borrow = borrow.negate();
               }

               buf[i - 1] = buf[i - 1].subtract(borrow);
               buf[i] = buf[i].add(borrow.multiply(FACTORS[i - 1]));
            }

            if (buf[i].signum() != 0) {
               s = buf[i].signum();
            }
         }
      } while(touched);

   }

   private static BigDecimal sanitize(BigInteger value, int signum) {
      if (signum != 0 && value != null) {
         return signum > 0 ? new BigDecimal(value) : new BigDecimal(value.negate());
      } else {
         return ZERO;
      }
   }

   static BigDecimal sanitize(BigDecimal value, int signum) {
      if (signum != 0 && value != null) {
         return signum > 0 ? value : value.negate();
      } else {
         return ZERO;
      }
   }

   public Duration subtract(Duration rhs) {
      return this.add(rhs.negate());
   }

   public Duration negate() {
      return new DurationImpl(this.signum <= 0, this.years, this.months, this.days, this.hours, this.minutes, this.seconds);
   }

   public int signum() {
      return this.signum;
   }

   public void addTo(Calendar calendar) {
      calendar.add(1, this.getYears() * this.signum);
      calendar.add(2, this.getMonths() * this.signum);
      calendar.add(5, this.getDays() * this.signum);
      calendar.add(10, this.getHours() * this.signum);
      calendar.add(12, this.getMinutes() * this.signum);
      calendar.add(13, this.getSeconds() * this.signum);
      if (this.seconds != null) {
         BigDecimal fraction = this.seconds.subtract(this.seconds.setScale(0, 1));
         int millisec = fraction.movePointRight(3).intValue();
         calendar.add(14, millisec * this.signum);
      }

   }

   public void addTo(Date date) {
      Calendar cal = new GregorianCalendar();
      cal.setTime(date);
      this.addTo((Calendar)cal);
      date.setTime(getCalendarTimeInMillis(cal));
   }

   private Object writeReplace() throws IOException {
      return new DurationImpl.DurationStream(this.toString());
   }

   private static long getCalendarTimeInMillis(Calendar cal) {
      return cal.getTime().getTime();
   }

   static {
      FIELDS = new DatatypeConstants.Field[]{DatatypeConstants.YEARS, DatatypeConstants.MONTHS, DatatypeConstants.DAYS, DatatypeConstants.HOURS, DatatypeConstants.MINUTES, DatatypeConstants.SECONDS};
      FIELD_IDS = new int[]{DatatypeConstants.YEARS.getId(), DatatypeConstants.MONTHS.getId(), DatatypeConstants.DAYS.getId(), DatatypeConstants.HOURS.getId(), DatatypeConstants.MINUTES.getId(), DatatypeConstants.SECONDS.getId()};
      GMT = TimeZone.getTimeZone("GMT");
      ZERO = BigDecimal.valueOf(0L);
      TEST_POINTS = new XMLGregorianCalendar[]{XMLGregorianCalendarImpl.parse("1696-09-01T00:00:00Z"), XMLGregorianCalendarImpl.parse("1697-02-01T00:00:00Z"), XMLGregorianCalendarImpl.parse("1903-03-01T00:00:00Z"), XMLGregorianCalendarImpl.parse("1903-07-01T00:00:00Z")};
      FACTORS = new BigDecimal[]{BigDecimal.valueOf(12L), null, BigDecimal.valueOf(24L), BigDecimal.valueOf(60L), BigDecimal.valueOf(60L)};
   }

   private static class DurationStream implements Serializable {
      private final String lexical;
      private static final long serialVersionUID = 1L;

      private DurationStream(String _lexical) {
         this.lexical = _lexical;
      }

      private Object readResolve() throws ObjectStreamException {
         return new DurationImpl(this.lexical);
      }

      // $FF: synthetic method
      DurationStream(String x0, Object x1) {
         this(x0);
      }
   }
}
