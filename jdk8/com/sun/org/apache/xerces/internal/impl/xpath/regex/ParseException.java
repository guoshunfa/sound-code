package com.sun.org.apache.xerces.internal.impl.xpath.regex;

public class ParseException extends RuntimeException {
   static final long serialVersionUID = -7012400318097691370L;
   int location;

   public ParseException(String mes, int location) {
      super(mes);
      this.location = location;
   }

   public int getLocation() {
      return this.location;
   }
}
