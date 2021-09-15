package org.omg.CORBA;

public final class COMM_FAILURE extends SystemException {
   public COMM_FAILURE() {
      this("");
   }

   public COMM_FAILURE(String var1) {
      this(var1, 0, CompletionStatus.COMPLETED_NO);
   }

   public COMM_FAILURE(int var1, CompletionStatus var2) {
      this("", var1, var2);
   }

   public COMM_FAILURE(String var1, int var2, CompletionStatus var3) {
      super(var1, var2, var3);
   }
}
