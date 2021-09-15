package org.omg.CORBA;

public final class TIMEOUT extends SystemException {
   public TIMEOUT() {
      this("");
   }

   public TIMEOUT(String var1) {
      this(var1, 0, CompletionStatus.COMPLETED_NO);
   }

   public TIMEOUT(int var1, CompletionStatus var2) {
      this("", var1, var2);
   }

   public TIMEOUT(String var1, int var2, CompletionStatus var3) {
      super(var1, var2, var3);
   }
}
