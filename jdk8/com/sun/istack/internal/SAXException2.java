package com.sun.istack.internal;

import org.xml.sax.SAXException;

public class SAXException2 extends SAXException {
   public SAXException2(String message) {
      super(message);
   }

   public SAXException2(Exception e) {
      super(e);
   }

   public SAXException2(String message, Exception e) {
      super(message, e);
   }

   public Throwable getCause() {
      return this.getException();
   }
}
