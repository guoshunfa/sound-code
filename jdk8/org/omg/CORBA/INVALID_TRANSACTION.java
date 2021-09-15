package org.omg.CORBA;

public final class INVALID_TRANSACTION extends SystemException {
   public INVALID_TRANSACTION() {
      this("");
   }

   public INVALID_TRANSACTION(String var1) {
      this(var1, 0, CompletionStatus.COMPLETED_NO);
   }

   public INVALID_TRANSACTION(int var1, CompletionStatus var2) {
      this("", var1, var2);
   }

   public INVALID_TRANSACTION(String var1, int var2, CompletionStatus var3) {
      super(var1, var2, var3);
   }
}
