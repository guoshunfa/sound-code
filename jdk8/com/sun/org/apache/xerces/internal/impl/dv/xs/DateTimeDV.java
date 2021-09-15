package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import java.math.BigInteger;
import javax.xml.datatype.XMLGregorianCalendar;

public class DateTimeDV extends AbstractDateTimeDV {
   public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
      try {
         return this.parse(content);
      } catch (Exception var4) {
         throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "dateTime"});
      }
   }

   protected AbstractDateTimeDV.DateTimeData parse(String str) throws SchemaDateTimeException {
      AbstractDateTimeDV.DateTimeData date = new AbstractDateTimeDV.DateTimeData(str, this);
      int len = str.length();
      int end = this.indexOf(str, 0, len, 'T');
      int dateEnd = this.getDate(str, 0, end, date);
      this.getTime(str, end + 1, len, date);
      if (dateEnd != end) {
         throw new RuntimeException(str + " is an invalid dateTime dataype value. Invalid character(s) seprating date and time values.");
      } else {
         this.validateDateTime(date);
         this.saveUnnormalized(date);
         if (date.utc != 0 && date.utc != 90) {
            this.normalize(date);
         }

         return date;
      }
   }

   protected XMLGregorianCalendar getXMLGregorianCalendar(AbstractDateTimeDV.DateTimeData date) {
      return datatypeFactory.newXMLGregorianCalendar(BigInteger.valueOf((long)date.unNormYear), date.unNormMonth, date.unNormDay, date.unNormHour, date.unNormMinute, (int)date.unNormSecond, date.unNormSecond != 0.0D ? this.getFractionalSecondsAsBigDecimal(date) : null, date.hasTimeZone() ? date.timezoneHr * 60 + date.timezoneMin : Integer.MIN_VALUE);
   }
}
