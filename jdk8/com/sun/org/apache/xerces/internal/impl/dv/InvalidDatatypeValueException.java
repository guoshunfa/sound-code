package com.sun.org.apache.xerces.internal.impl.dv;

public class InvalidDatatypeValueException extends DatatypeException {
   static final long serialVersionUID = -5523739426958236125L;

   public InvalidDatatypeValueException(String key, Object[] args) {
      super(key, args);
   }
}
