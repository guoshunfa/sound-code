package com.sun.xml.internal.txw2;

public class TxwException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public TxwException(String message) {
      super(message);
   }

   public TxwException(Throwable cause) {
      super(cause);
   }

   public TxwException(String message, Throwable cause) {
      super(message, cause);
   }
}
