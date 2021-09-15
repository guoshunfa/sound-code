package org.omg.CORBA;

public final class UnknownUserException extends UserException {
   public Any except;

   public UnknownUserException() {
   }

   public UnknownUserException(Any var1) {
      this.except = var1;
   }
}
