package com.sun.xml.internal.ws.policy;

public class PolicyException extends Exception {
   public PolicyException(String message) {
      super(message);
   }

   public PolicyException(String message, Throwable cause) {
      super(message, cause);
   }

   public PolicyException(Throwable cause) {
      super(cause);
   }
}
