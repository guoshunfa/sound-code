package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.datatype.Duration;

class DayTimeDurationDV extends DurationDV {
   public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
      try {
         return this.parse(content, 2);
      } catch (Exception var4) {
         throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "dayTimeDuration"});
      }
   }

   protected Duration getDuration(AbstractDateTimeDV.DateTimeData date) {
      int sign = 1;
      if (date.day < 0 || date.hour < 0 || date.minute < 0 || date.second < 0.0D) {
         sign = -1;
      }

      return datatypeFactory.newDuration(sign == 1, (BigInteger)null, (BigInteger)null, date.day != Integer.MIN_VALUE ? BigInteger.valueOf((long)(sign * date.day)) : null, date.hour != Integer.MIN_VALUE ? BigInteger.valueOf((long)(sign * date.hour)) : null, date.minute != Integer.MIN_VALUE ? BigInteger.valueOf((long)(sign * date.minute)) : null, date.second != -2.147483648E9D ? new BigDecimal(String.valueOf((double)sign * date.second)) : null);
   }
}
