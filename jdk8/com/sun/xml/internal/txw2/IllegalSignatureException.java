package com.sun.xml.internal.txw2;

public class IllegalSignatureException extends TxwException {
   private static final long serialVersionUID = 1L;

   public IllegalSignatureException(String message) {
      super(message);
   }

   public IllegalSignatureException(String message, Throwable cause) {
      super(message, cause);
   }

   public IllegalSignatureException(Throwable cause) {
      super(cause);
   }
}
