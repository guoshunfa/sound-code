package org.omg.CORBA;

public final class FREE_MEM extends SystemException {
   public FREE_MEM() {
      this("");
   }

   public FREE_MEM(String var1) {
      this(var1, 0, CompletionStatus.COMPLETED_NO);
   }

   public FREE_MEM(int var1, CompletionStatus var2) {
      this("", var1, var2);
   }

   public FREE_MEM(String var1, int var2, CompletionStatus var3) {
      super(var1, var2, var3);
   }
}
