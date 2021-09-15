package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

public class AnySimpleDV extends TypeValidator {
   public short getAllowedFacets() {
      return 0;
   }

   public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
      return content;
   }
}
