package javax.xml.datatype;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class DatatypeFactory {
   public static final String DATATYPEFACTORY_PROPERTY = "javax.xml.datatype.DatatypeFactory";
   public static final String DATATYPEFACTORY_IMPLEMENTATION_CLASS = new String("com.sun.org.apache.xerces.internal.jaxp.datatype.DatatypeFactoryImpl");
   private static final Pattern XDTSCHEMA_YMD = Pattern.compile("[^DT]*");
   private static final Pattern XDTSCHEMA_DTD = Pattern.compile("[^YM]*[DT].*");

   protected DatatypeFactory() {
   }

   public static DatatypeFactory newInstance() throws DatatypeConfigurationException {
      return (DatatypeFactory)FactoryFinder.find(DatatypeFactory.class, DATATYPEFACTORY_IMPLEMENTATION_CLASS);
   }

   public static DatatypeFactory newInstance(String factoryClassName, ClassLoader classLoader) throws DatatypeConfigurationException {
      return (DatatypeFactory)FactoryFinder.newInstance(DatatypeFactory.class, factoryClassName, classLoader, false);
   }

   public abstract Duration newDuration(String var1);

   public abstract Duration newDuration(long var1);

   public abstract Duration newDuration(boolean var1, BigInteger var2, BigInteger var3, BigInteger var4, BigInteger var5, BigInteger var6, BigDecimal var7);

   public Duration newDuration(boolean isPositive, int years, int months, int days, int hours, int minutes, int seconds) {
      BigInteger realYears = years != Integer.MIN_VALUE ? BigInteger.valueOf((long)years) : null;
      BigInteger realMonths = months != Integer.MIN_VALUE ? BigInteger.valueOf((long)months) : null;
      BigInteger realDays = days != Integer.MIN_VALUE ? BigInteger.valueOf((long)days) : null;
      BigInteger realHours = hours != Integer.MIN_VALUE ? BigInteger.valueOf((long)hours) : null;
      BigInteger realMinutes = minutes != Integer.MIN_VALUE ? BigInteger.valueOf((long)minutes) : null;
      BigDecimal realSeconds = seconds != Integer.MIN_VALUE ? BigDecimal.valueOf((long)seconds) : null;
      return this.newDuration(isPositive, realYears, realMonths, realDays, realHours, realMinutes, realSeconds);
   }

   public Duration newDurationDayTime(String lexicalRepresentation) {
      if (lexicalRepresentation == null) {
         throw new NullPointerException("Trying to create an xdt:dayTimeDuration with an invalid lexical representation of \"null\"");
      } else {
         Matcher matcher = XDTSCHEMA_DTD.matcher(lexicalRepresentation);
         if (!matcher.matches()) {
            throw new IllegalArgumentException("Trying to create an xdt:dayTimeDuration with an invalid lexical representation of \"" + lexicalRepresentation + "\", data model requires years and months only.");
         } else {
            return this.newDuration(lexicalRepresentation);
         }
      }
   }

   public Duration newDurationDayTime(long durationInMilliseconds) {
      return this.newDuration(durationInMilliseconds);
   }

   public Duration newDurationDayTime(boolean isPositive, BigInteger day, BigInteger hour, BigInteger minute, BigInteger second) {
      return this.newDuration(isPositive, (BigInteger)null, (BigInteger)null, day, hour, minute, second != null ? new BigDecimal(second) : null);
   }

   public Duration newDurationDayTime(boolean isPositive, int day, int hour, int minute, int second) {
      return this.newDurationDayTime(isPositive, BigInteger.valueOf((long)day), BigInteger.valueOf((long)hour), BigInteger.valueOf((long)minute), BigInteger.valueOf((long)second));
   }

   public Duration newDurationYearMonth(String lexicalRepresentation) {
      if (lexicalRepresentation == null) {
         throw new NullPointerException("Trying to create an xdt:yearMonthDuration with an invalid lexical representation of \"null\"");
      } else {
         Matcher matcher = XDTSCHEMA_YMD.matcher(lexicalRepresentation);
         if (!matcher.matches()) {
            throw new IllegalArgumentException("Trying to create an xdt:yearMonthDuration with an invalid lexical representation of \"" + lexicalRepresentation + "\", data model requires days and times only.");
         } else {
            return this.newDuration(lexicalRepresentation);
         }
      }
   }

   public Duration newDurationYearMonth(long durationInMilliseconds) {
      Duration fullDuration = this.newDuration(durationInMilliseconds);
      boolean isPositive = fullDuration.getSign() != -1;
      BigInteger years = (BigInteger)fullDuration.getField(DatatypeConstants.YEARS);
      if (years == null) {
         years = BigInteger.ZERO;
      }

      BigInteger months = (BigInteger)fullDuration.getField(DatatypeConstants.MONTHS);
      if (months == null) {
         months = BigInteger.ZERO;
      }

      return this.newDurationYearMonth(isPositive, years, months);
   }

   public Duration newDurationYearMonth(boolean isPositive, BigInteger year, BigInteger month) {
      return this.newDuration(isPositive, year, month, (BigInteger)null, (BigInteger)null, (BigInteger)null, (BigDecimal)null);
   }

   public Duration newDurationYearMonth(boolean isPositive, int year, int month) {
      return this.newDurationYearMonth(isPositive, BigInteger.valueOf((long)year), BigInteger.valueOf((long)month));
   }

   public abstract XMLGregorianCalendar newXMLGregorianCalendar();

   public abstract XMLGregorianCalendar newXMLGregorianCalendar(String var1);

   public abstract XMLGregorianCalendar newXMLGregorianCalendar(GregorianCalendar var1);

   public abstract XMLGregorianCalendar newXMLGregorianCalendar(BigInteger var1, int var2, int var3, int var4, int var5, int var6, BigDecimal var7, int var8);

   public XMLGregorianCalendar newXMLGregorianCalendar(int year, int month, int day, int hour, int minute, int second, int millisecond, int timezone) {
      BigInteger realYear = year != Integer.MIN_VALUE ? BigInteger.valueOf((long)year) : null;
      BigDecimal realMillisecond = null;
      if (millisecond != Integer.MIN_VALUE) {
         if (millisecond < 0 || millisecond > 1000) {
            throw new IllegalArgumentException("javax.xml.datatype.DatatypeFactory#newXMLGregorianCalendar(int year, int month, int day, int hour, int minute, int second, int millisecond, int timezone)with invalid millisecond: " + millisecond);
         }

         realMillisecond = BigDecimal.valueOf((long)millisecond).movePointLeft(3);
      }

      return this.newXMLGregorianCalendar(realYear, month, day, hour, minute, second, realMillisecond, timezone);
   }

   public XMLGregorianCalendar newXMLGregorianCalendarDate(int year, int month, int day, int timezone) {
      return this.newXMLGregorianCalendar(year, month, day, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, timezone);
   }

   public XMLGregorianCalendar newXMLGregorianCalendarTime(int hours, int minutes, int seconds, int timezone) {
      return this.newXMLGregorianCalendar(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, hours, minutes, seconds, Integer.MIN_VALUE, timezone);
   }

   public XMLGregorianCalendar newXMLGregorianCalendarTime(int hours, int minutes, int seconds, BigDecimal fractionalSecond, int timezone) {
      return this.newXMLGregorianCalendar((BigInteger)null, Integer.MIN_VALUE, Integer.MIN_VALUE, hours, minutes, seconds, fractionalSecond, timezone);
   }

   public XMLGregorianCalendar newXMLGregorianCalendarTime(int hours, int minutes, int seconds, int milliseconds, int timezone) {
      BigDecimal realMilliseconds = null;
      if (milliseconds != Integer.MIN_VALUE) {
         if (milliseconds < 0 || milliseconds > 1000) {
            throw new IllegalArgumentException("javax.xml.datatype.DatatypeFactory#newXMLGregorianCalendarTime(int hours, int minutes, int seconds, int milliseconds, int timezone)with invalid milliseconds: " + milliseconds);
         }

         realMilliseconds = BigDecimal.valueOf((long)milliseconds).movePointLeft(3);
      }

      return this.newXMLGregorianCalendarTime(hours, minutes, seconds, realMilliseconds, timezone);
   }
}
