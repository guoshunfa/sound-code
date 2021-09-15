package com.sun.corba.se.impl.protocol;

public class RequestCanceledException extends RuntimeException {
   private int requestId = 0;

   public RequestCanceledException(int var1) {
      this.requestId = var1;
   }

   public int getRequestId() {
      return this.requestId;
   }
}
