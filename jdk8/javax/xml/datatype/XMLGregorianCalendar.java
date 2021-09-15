package javax.xml.datatype;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import javax.xml.namespace.QName;

public abstract class XMLGregorianCalendar implements Cloneable {
   public abstract void clear();

   public abstract void reset();

   public abstract void setYear(BigInteger var1);

   public abstract void setYear(int var1);

   public abstract void setMonth(int var1);

   public abstract void setDay(int var1);

   public abstract void setTimezone(int var1);

   public void setTime(int hour, int minute, int second) {
      this.setTime(hour, minute, second, (BigDecimal)null);
   }

   public abstract void setHour(int var1);

   public abstract void setMinute(int var1);

   public abstract void setSecond(int var1);

   public abstract void setMillisecond(int var1);

   public abstract void setFractionalSecond(BigDecimal var1);

   public void setTime(int hour, int minute, int second, BigDecimal fractional) {
      this.setHour(hour);
      this.setMinute(minute);
      this.setSecond(second);
      this.setFractionalSecond(fractional);
   }

   public void setTime(int hour, int minute, int second, int millisecond) {
      this.setHour(hour);
      this.setMinute(minute);
      this.setSecond(second);
      this.setMillisecond(millisecond);
   }

   public abstract BigInteger getEon();

   public abstract int getYear();

   public abstract BigInteger getEonAndYear();

   public abstract int getMonth();

   public abstract int getDay();

   public abstract int getTimezone();

   public abstract int getHour();

   public abstract int getMinute();

   public abstract int getSecond();

   public int getMillisecond() {
      BigDecimal fractionalSeconds = this.getFractionalSecond();
      return fractionalSeconds == null ? Integer.MIN_VALUE : this.getFractionalSecond().movePointRight(3).intValue();
   }

   public abstract BigDecimal getFractionalSecond();

   public abstract int compare(XMLGregorianCalendar var1);

   public abstract XMLGregorianCalendar normalize();

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
         gc = this.normalize();
      }

      return gc.getYear() + gc.getMonth() + gc.getDay() + gc.getHour() + gc.getMinute() + gc.getSecond();
   }

   public abstract String toXMLFormat();

   public abstract QName getXMLSchemaType();

   public String toString() {
      return this.toXMLFormat();
   }

   public abstract boolean isValid();

   public abstract void add(Duration var1);

   public abstract GregorianCalendar toGregorianCalendar();

   public abstract GregorianCalendar toGregorianCalendar(TimeZone var1, Locale var2, XMLGregorianCalendar var3);

   public abstract TimeZone getTimeZone(int var1);

   public abstract Object clone();
}
