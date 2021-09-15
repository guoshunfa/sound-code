package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.util.XMLChar;

public class IDREFDV extends TypeValidator {
   public short getAllowedFacets() {
      return 2079;
   }

   public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
      if (!XMLChar.isValidNCName(content)) {
         throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "NCName"});
      } else {
         return content;
      }
   }

   public void checkExtraRules(Object value, ValidationContext context) throws InvalidDatatypeValueException {
      context.addIdRef((String)value);
   }
}
