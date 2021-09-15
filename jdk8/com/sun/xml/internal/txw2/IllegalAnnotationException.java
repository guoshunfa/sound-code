package com.sun.xml.internal.txw2;

public class IllegalAnnotationException extends TxwException {
   private static final long serialVersionUID = 1L;

   public IllegalAnnotationException(String message) {
      super(message);
   }

   public IllegalAnnotationException(Throwable cause) {
      super(cause);
   }

   public IllegalAnnotationException(String message, Throwable cause) {
      super(message, cause);
   }
}
