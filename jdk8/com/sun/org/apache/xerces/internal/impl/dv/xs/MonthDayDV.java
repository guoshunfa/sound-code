package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import javax.xml.datatype.XMLGregorianCalendar;

public class MonthDayDV extends AbstractDateTimeDV {
   private static final int MONTHDAY_SIZE = 7;

   public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
      try {
         return this.parse(content);
      } catch (Exception var4) {
         throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "gMonthDay"});
      }
   }

   protected AbstractDateTimeDV.DateTimeData parse(String str) throws SchemaDateTimeException {
      AbstractDateTimeDV.DateTimeData date = new AbstractDateTimeDV.DateTimeData(str, this);
      int len = str.length();
      date.year = 2000;
      if (str.charAt(0) == '-' && str.charAt(1) == '-') {
         date.month = this.parseInt(str, 2, 4);
         int start = 4;
         byte var10001 = start;
         int start = start + 1;
         if (str.charAt(var10001) != '-') {
            throw new SchemaDateTimeException("Invalid format for gMonthDay: " + str);
         } else {
            date.day = this.parseInt(str, start, start + 2);
            if (7 < len) {
               if (!this.isNextCharUTCSign(str, 7, len)) {
                  throw new SchemaDateTimeException("Error in month parsing:" + str);
               }

               this.getTimeZone(str, date, 7, len);
            }

            this.validateDateTime(date);
            this.saveUnnormalized(date);
            if (date.utc != 0 && date.utc != 90) {
               this.normalize(date);
            }

            date.position = 1;
            return date;
         }
      } else {
         throw new SchemaDateTimeException("Invalid format for gMonthDay: " + str);
      }
   }

   protected String dateToString(AbstractDateTimeDV.DateTimeData date) {
      StringBuffer message = new StringBuffer(8);
      message.append('-');
      message.append('-');
      this.append(message, date.month, 2);
      message.append('-');
      this.append(message, date.day, 2);
      this.append(message, (char)date.utc, 0);
      return message.toString();
   }

   protected XMLGregorianCalendar getXMLGregorianCalendar(AbstractDateTimeDV.DateTimeData date) {
      return datatypeFactory.newXMLGregorianCalendar(Integer.MIN_VALUE, date.unNormMonth, date.unNormDay, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, date.hasTimeZone() ? date.timezoneHr * 60 + date.timezoneMin : Integer.MIN_VALUE);
   }
}
