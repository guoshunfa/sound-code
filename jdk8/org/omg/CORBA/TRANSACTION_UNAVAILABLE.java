package org.omg.CORBA;

public final class TRANSACTION_UNAVAILABLE extends SystemException {
   public TRANSACTION_UNAVAILABLE() {
      this("");
   }

   public TRANSACTION_UNAVAILABLE(String var1) {
      this(var1, 0, CompletionStatus.COMPLETED_NO);
   }

   public TRANSACTION_UNAVAILABLE(int var1, CompletionStatus var2) {
      this("", var1, var2);
   }

   public TRANSACTION_UNAVAILABLE(String var1, int var2, CompletionStatus var3) {
      super(var1, var2, var3);
   }
}
