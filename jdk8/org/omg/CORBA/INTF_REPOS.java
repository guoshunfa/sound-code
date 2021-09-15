package org.omg.CORBA;

public final class INTF_REPOS extends SystemException {
   public INTF_REPOS() {
      this("");
   }

   public INTF_REPOS(String var1) {
      this(var1, 0, CompletionStatus.COMPLETED_NO);
   }

   public INTF_REPOS(int var1, CompletionStatus var2) {
      this("", var1, var2);
   }

   public INTF_REPOS(String var1, int var2, CompletionStatus var3) {
      super(var1, var2, var3);
   }
}
