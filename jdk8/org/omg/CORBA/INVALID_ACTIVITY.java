package org.omg.CORBA;

public final class INVALID_ACTIVITY extends SystemException {
   public INVALID_ACTIVITY() {
      this("");
   }

   public INVALID_ACTIVITY(String var1) {
      this(var1, 0, CompletionStatus.COMPLETED_NO);
   }

   public INVALID_ACTIVITY(int var1, CompletionStatus var2) {
      this("", var1, var2);
   }

   public INVALID_ACTIVITY(String var1, int var2, CompletionStatus var3) {
      super(var1, var2, var3);
   }
}
