package org.omg.CORBA;

public final class WrongTransaction extends UserException {
   public WrongTransaction() {
      super(WrongTransactionHelper.id());
   }

   public WrongTransaction(String var1) {
      super(WrongTransactionHelper.id() + "  " + var1);
   }
}
