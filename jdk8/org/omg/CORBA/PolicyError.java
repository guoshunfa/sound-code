package org.omg.CORBA;

public final class PolicyError extends UserException {
   public short reason;

   public PolicyError() {
   }

   public PolicyError(short var1) {
      this.reason = var1;
   }

   public PolicyError(String var1, short var2) {
      super(var1);
      this.reason = var2;
   }
}
