package com.sun.corba.se.spi.protocol;

public enum RetryType {
   NONE(false),
   BEFORE_RESPONSE(true),
   AFTER_RESPONSE(true);

   private final boolean isRetry;

   private RetryType(boolean var3) {
      this.isRetry = var3;
   }

   public boolean isRetry() {
      return this.isRetry;
   }
}
