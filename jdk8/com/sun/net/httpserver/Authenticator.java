package com.sun.net.httpserver;

import jdk.Exported;

@Exported
public abstract class Authenticator {
   public abstract Authenticator.Result authenticate(HttpExchange var1);

   @Exported
   public static class Retry extends Authenticator.Result {
      private int responseCode;

      public Retry(int var1) {
         this.responseCode = var1;
      }

      public int getResponseCode() {
         return this.responseCode;
      }
   }

   @Exported
   public static class Success extends Authenticator.Result {
      private HttpPrincipal principal;

      public Success(HttpPrincipal var1) {
         this.principal = var1;
      }

      public HttpPrincipal getPrincipal() {
         return this.principal;
      }
   }

   @Exported
   public static class Failure extends Authenticator.Result {
      private int responseCode;

      public Failure(int var1) {
         this.responseCode = var1;
      }

      public int getResponseCode() {
         return this.responseCode;
      }
   }

   public abstract static class Result {
   }
}
