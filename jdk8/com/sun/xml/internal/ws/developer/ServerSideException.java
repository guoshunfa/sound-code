package com.sun.xml.internal.ws.developer;

public class ServerSideException extends Exception {
   private final String className;

   public ServerSideException(String className, String message) {
      super(message);
      this.className = className;
   }

   public String getMessage() {
      return "Client received an exception from server: " + super.getMessage() + " Please see the server log to find more detail regarding exact cause of the failure.";
   }

   public String toString() {
      String s = this.className;
      String message = this.getLocalizedMessage();
      return message != null ? s + ": " + message : s;
   }
}
