package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import javax.xml.datatype.XMLGregorianCalendar;

public class YearDV extends AbstractDateTimeDV {
   public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
      try {
         return this.parse(content);
      } catch (Exception var4) {
         throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "gYear"});
      }
   }

   protected AbstractDateTimeDV.DateTimeData parse(String str) throws SchemaDateTimeException {
      AbstractDateTimeDV.DateTimeData date = new AbstractDateTimeDV.DateTimeData(str, this);
      int len = str.length();
      int start = 0;
      if (str.charAt(0) == '-') {
         start = 1;
      }

      int sign = this.findUTCSign(str, start, len);
      int length = (sign == -1 ? len : sign) - start;
      if (length < 4) {
         throw new RuntimeException("Year must have 'CCYY' format");
      } else if (length > 4 && str.charAt(start) == '0') {
         throw new RuntimeException("Leading zeros are required if the year value would otherwise have fewer than four digits; otherwise they are forbidden");
      } else {
         if (sign == -1) {
            date.year = this.parseIntYear(str, len);
         } else {
            date.year = this.parseIntYear(str, sign);
            this.getTimeZone(str, date, sign, len);
         }

         date.month = 1;
         date.day = 1;
         this.validateDateTime(date);
         this.saveUnnormalized(date);
         if (date.utc != 0 && date.utc != 90) {
            this.normalize(date);
         }

         date.position = 0;
         return date;
      }
   }

   protected String dateToString(AbstractDateTimeDV.DateTimeData date) {
      StringBuffer message = new StringBuffer(5);
      this.append(message, date.year, 4);
      this.append(message, (char)date.utc, 0);
      return message.toString();
   }

   protected XMLGregorianCalendar getXMLGregorianCalendar(AbstractDateTimeDV.DateTimeData date) {
      return datatypeFactory.newXMLGregorianCalendar(date.unNormYear, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, date.hasTimeZone() ? date.timezoneHr * 60 + date.timezoneMin : Integer.MIN_VALUE);
   }
}
