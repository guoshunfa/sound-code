package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

public class BooleanDV extends TypeValidator {
   private static final String[] fValueSpace = new String[]{"false", "true", "0", "1"};

   public short getAllowedFacets() {
      return 24;
   }

   public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
      Boolean ret = null;
      if (!content.equals(fValueSpace[0]) && !content.equals(fValueSpace[2])) {
         if (!content.equals(fValueSpace[1]) && !content.equals(fValueSpace[3])) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "boolean"});
         }

         ret = Boolean.TRUE;
      } else {
         ret = Boolean.FALSE;
      }

      return ret;
   }
}
