package com.sun.beans.finder;

final class SignatureException extends RuntimeException {
   SignatureException(Throwable var1) {
      super(var1);
   }

   NoSuchMethodException toNoSuchMethodException(String var1) {
      Throwable var2 = this.getCause();
      if (var2 instanceof NoSuchMethodException) {
         return (NoSuchMethodException)var2;
      } else {
         NoSuchMethodException var3 = new NoSuchMethodException(var1);
         var3.initCause(var2);
         return var3;
      }
   }
}
