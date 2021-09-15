package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

public class IntegerDV extends DecimalDV {
   public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
      try {
         return new DecimalDV.XDecimal(content, true);
      } catch (NumberFormatException var4) {
         throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "integer"});
      }
   }
}
