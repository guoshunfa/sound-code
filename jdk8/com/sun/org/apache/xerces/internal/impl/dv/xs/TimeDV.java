package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import java.math.BigInteger;
import javax.xml.datatype.XMLGregorianCalendar;

public class TimeDV extends AbstractDateTimeDV {
   public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
      try {
         return this.parse(content);
      } catch (Exception var4) {
         throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "time"});
      }
   }

   protected AbstractDateTimeDV.DateTimeData parse(String str) throws SchemaDateTimeException {
      AbstractDateTimeDV.DateTimeData date = new AbstractDateTimeDV.DateTimeData(str, this);
      int len = str.length();
      date.year = 2000;
      date.month = 1;
      date.day = 15;
      this.getTime(str, 0, len, date);
      this.validateDateTime(date);
      this.saveUnnormalized(date);
      if (date.utc != 0 && date.utc != 90) {
         this.normalize(date);
      }

      date.position = 2;
      return date;
   }

   protected String dateToString(AbstractDateTimeDV.DateTimeData date) {
      StringBuffer message = new StringBuffer(16);
      this.append(message, date.hour, 2);
      message.append(':');
      this.append(message, date.minute, 2);
      message.append(':');
      this.append(message, date.second);
      this.append(message, (char)date.utc, 0);
      return message.toString();
   }

   protected XMLGregorianCalendar getXMLGregorianCalendar(AbstractDateTimeDV.DateTimeData date) {
      return datatypeFactory.newXMLGregorianCalendar((BigInteger)null, Integer.MIN_VALUE, Integer.MIN_VALUE, date.unNormHour, date.unNormMinute, (int)date.unNormSecond, date.unNormSecond != 0.0D ? this.getFractionalSecondsAsBigDecimal(date) : null, date.hasTimeZone() ? date.timezoneHr * 60 + date.timezoneMin : Integer.MIN_VALUE);
   }
}
