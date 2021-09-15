package org.omg.CORBA;

public final class TRANSACTION_REQUIRED extends SystemException {
   public TRANSACTION_REQUIRED() {
      this("");
   }

   public TRANSACTION_REQUIRED(String var1) {
      this(var1, 0, CompletionStatus.COMPLETED_NO);
   }

   public TRANSACTION_REQUIRED(int var1, CompletionStatus var2) {
      this("", var1, var2);
   }

   public TRANSACTION_REQUIRED(String var1, int var2, CompletionStatus var3) {
      super(var1, var2, var3);
   }
}
