package org.omg.CORBA;

public final class INV_POLICY extends SystemException {
   public INV_POLICY() {
      this("");
   }

   public INV_POLICY(String var1) {
      this(var1, 0, CompletionStatus.COMPLETED_NO);
   }

   public INV_POLICY(int var1, CompletionStatus var2) {
      this("", var1, var2);
   }

   public INV_POLICY(String var1, int var2, CompletionStatus var3) {
      super(var1, var2, var3);
   }
}
