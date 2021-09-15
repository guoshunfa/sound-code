package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.util.XML11Char;

public class XML11NMTOKENDatatypeValidator extends NMTOKENDatatypeValidator {
   public void validate(String content, ValidationContext context) throws InvalidDatatypeValueException {
      if (!XML11Char.isXML11ValidNmtoken(content)) {
         throw new InvalidDatatypeValueException("NMTOKENInvalid", new Object[]{content});
      }
   }
}
