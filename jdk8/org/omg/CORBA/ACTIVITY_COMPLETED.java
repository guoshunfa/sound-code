package org.omg.CORBA;

public final class ACTIVITY_COMPLETED extends SystemException {
   public ACTIVITY_COMPLETED() {
      this("");
   }

   public ACTIVITY_COMPLETED(String var1) {
      this(var1, 0, CompletionStatus.COMPLETED_NO);
   }

   public ACTIVITY_COMPLETED(int var1, CompletionStatus var2) {
      this("", var1, var2);
   }

   public ACTIVITY_COMPLETED(String var1, int var2, CompletionStatus var3) {
      super(var1, var2, var3);
   }
}
