package com.sun.xml.internal.ws.spi.db;

public class DatabindingException extends RuntimeException {
   public DatabindingException() {
   }

   public DatabindingException(String message) {
      super(message);
   }

   public DatabindingException(Throwable cause) {
      super(cause);
   }

   public DatabindingException(String message, Throwable cause) {
      super(message, cause);
   }
}
