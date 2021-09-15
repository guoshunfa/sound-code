package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.datatype.Duration;

class YearMonthDurationDV extends DurationDV {
   public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
      try {
         return this.parse(content, 1);
      } catch (Exception var4) {
         throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "yearMonthDuration"});
      }
   }

   protected Duration getDuration(AbstractDateTimeDV.DateTimeData date) {
      int sign = 1;
      if (date.year < 0 || date.month < 0) {
         sign = -1;
      }

      return datatypeFactory.newDuration(sign == 1, date.year != Integer.MIN_VALUE ? BigInteger.valueOf((long)(sign * date.year)) : null, date.month != Integer.MIN_VALUE ? BigInteger.valueOf((long)(sign * date.month)) : null, (BigInteger)null, (BigInteger)null, (BigInteger)null, (BigDecimal)null);
   }
}
