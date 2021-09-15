package javax.xml.datatype;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.namespace.QName;

public abstract class Duration {
   private static final boolean DEBUG = true;

   public QName getXMLSchemaType() {
      boolean yearSet = this.isSet(DatatypeConstants.YEARS);
      boolean monthSet = this.isSet(DatatypeConstants.MONTHS);
      boolean daySet = this.isSet(DatatypeConstants.DAYS);
      boolean hourSet = this.isSet(DatatypeConstants.HOURS);
      boolean minuteSet = this.isSet(DatatypeConstants.MINUTES);
      boolean secondSet = this.isSet(DatatypeConstants.SECONDS);
      if (yearSet && monthSet && daySet && hourSet && minuteSet && secondSet) {
         return DatatypeConstants.DURATION;
      } else if (!yearSet && !monthSet && daySet && hourSet && minuteSet && secondSet) {
         return DatatypeConstants.DURATION_DAYTIME;
      } else if (yearSet && monthSet && !daySet && !hourSet && !minuteSet && !secondSet) {
         return DatatypeConstants.DURATION_YEARMONTH;
      } else {
         throw new IllegalStateException("javax.xml.datatype.Duration#getXMLSchemaType(): this Duration does not match one of the XML Schema date/time datatypes: year set = " + yearSet + " month set = " + monthSet + " day set = " + daySet + " hour set = " + hourSet + " minute set = " + minuteSet + " second set = " + secondSet);
      }
   }

   public abstract int getSign();

   public int getYears() {
      return this.getField(DatatypeConstants.YEARS).intValue();
   }

   public int getMonths() {
      return this.getField(DatatypeConstants.MONTHS).intValue();
   }

   public int getDays() {
      return this.getField(DatatypeConstants.DAYS).intValue();
   }

   public int getHours() {
      return this.getField(DatatypeConstants.HOURS).intValue();
   }

   public int getMinutes() {
      return this.getField(DatatypeConstants.MINUTES).intValue();
   }

   public int getSeconds() {
      return this.getField(DatatypeConstants.SECONDS).intValue();
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

   public abstract Number getField(DatatypeConstants.Field var1);

   public abstract boolean isSet(DatatypeConstants.Field var1);

   public abstract Duration add(Duration var1);

   public abstract void addTo(Calendar var1);

   public void addTo(Date date) {
      if (date == null) {
         throw new NullPointerException("Cannot call " + this.getClass().getName() + "#addTo(Date date) with date == null.");
      } else {
         Calendar cal = new GregorianCalendar();
         cal.setTime(date);
         this.addTo((Calendar)cal);
         date.setTime(getCalendarTimeInMillis(cal));
      }
   }

   public Duration subtract(Duration rhs) {
      return this.add(rhs.negate());
   }

   public Duration multiply(int factor) {
      return this.multiply(new BigDecimal(String.valueOf(factor)));
   }

   public abstract Duration multiply(BigDecimal var1);

   public abstract Duration negate();

   public abstract Duration normalizeWith(Calendar var1);

   public abstract int compare(Duration var1);

   public boolean isLongerThan(Duration duration) {
      return this.compare(duration) == 1;
   }

   public boolean isShorterThan(Duration duration) {
      return this.compare(duration) == -1;
   }

   public boolean equals(Object duration) {
      if (duration != null && duration instanceof Duration) {
         return this.compare((Duration)duration) == 0;
      } else {
         return false;
      }
   }

   public abstract int hashCode();

   public String toString() {
      StringBuffer buf = new StringBuffer();
      if (this.getSign() < 0) {
         buf.append('-');
      }

      buf.append('P');
      BigInteger years = (BigInteger)this.getField(DatatypeConstants.YEARS);
      if (years != null) {
         buf.append(years + "Y");
      }

      BigInteger months = (BigInteger)this.getField(DatatypeConstants.MONTHS);
      if (months != null) {
         buf.append(months + "M");
      }

      BigInteger days = (BigInteger)this.getField(DatatypeConstants.DAYS);
      if (days != null) {
         buf.append(days + "D");
      }

      BigInteger hours = (BigInteger)this.getField(DatatypeConstants.HOURS);
      BigInteger minutes = (BigInteger)this.getField(DatatypeConstants.MINUTES);
      BigDecimal seconds = (BigDecimal)this.getField(DatatypeConstants.SECONDS);
      if (hours != null || minutes != null || seconds != null) {
         buf.append('T');
         if (hours != null) {
            buf.append(hours + "H");
         }

         if (minutes != null) {
            buf.append(minutes + "M");
         }

         if (seconds != null) {
            buf.append(this.toString(seconds) + "S");
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

   private static long getCalendarTimeInMillis(Calendar cal) {
      return cal.getTime().getTime();
   }
}
