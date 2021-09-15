package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import javax.xml.datatype.XMLGregorianCalendar;

public class MonthDV extends AbstractDateTimeDV {
   public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
      try {
         return this.parse(content);
      } catch (Exception var4) {
         throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "gMonth"});
      }
   }

   protected AbstractDateTimeDV.DateTimeData parse(String str) throws SchemaDateTimeException {
      AbstractDateTimeDV.DateTimeData date = new AbstractDateTimeDV.DateTimeData(str, this);
      int len = str.length();
      date.year = 2000;
      date.day = 1;
      if (str.charAt(0) == '-' && str.charAt(1) == '-') {
         int stop = 4;
         date.month = this.parseInt(str, 2, stop);
         if (str.length() >= stop + 2 && str.charAt(stop) == '-' && str.charAt(stop + 1) == '-') {
            stop += 2;
         }

         if (stop < len) {
            if (!this.isNextCharUTCSign(str, stop, len)) {
               throw new SchemaDateTimeException("Error in month parsing: " + str);
            }

            this.getTimeZone(str, date, stop, len);
         }

         this.validateDateTime(date);
         this.saveUnnormalized(date);
         if (date.utc != 0 && date.utc != 90) {
            this.normalize(date);
         }

         date.position = 1;
         return date;
      } else {
         throw new SchemaDateTimeException("Invalid format for gMonth: " + str);
      }
   }

   protected String dateToString(AbstractDateTimeDV.DateTimeData date) {
      StringBuffer message = new StringBuffer(5);
      message.append('-');
      message.append('-');
      this.append(message, date.month, 2);
      this.append(message, (char)date.utc, 0);
      return message.toString();
   }

   protected XMLGregorianCalendar getXMLGregorianCalendar(AbstractDateTimeDV.DateTimeData date) {
      return datatypeFactory.newXMLGregorianCalendar(Integer.MIN_VALUE, date.unNormMonth, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, date.hasTimeZone() ? date.timezoneHr * 60 + date.timezoneMin : Integer.MIN_VALUE);
   }
}
