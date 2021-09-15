package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

public class UnionDV extends TypeValidator {
   public short getAllowedFacets() {
      return 2056;
   }

   public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
      return content;
   }
}
