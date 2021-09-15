package com.sun.xml.internal.bind.api;

public final class AccessorException extends Exception {
   public AccessorException() {
   }

   public AccessorException(String message) {
      super(message);
   }

   public AccessorException(String message, Throwable cause) {
      super(message, cause);
   }

   public AccessorException(Throwable cause) {
      super(cause);
   }
}
