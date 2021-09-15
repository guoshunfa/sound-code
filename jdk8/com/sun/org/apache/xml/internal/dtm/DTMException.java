package com.sun.org.apache.xml.internal.dtm;

public class DTMException extends RuntimeException {
   static final long serialVersionUID = -775576419181334734L;

   public DTMException(String message) {
      super(message);
   }

   public DTMException(Throwable e) {
      super(e);
   }

   public DTMException(String message, Throwable e) {
      super(message, e);
   }
}
