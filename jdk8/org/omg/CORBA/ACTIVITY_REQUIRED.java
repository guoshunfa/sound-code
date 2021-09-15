package org.omg.CORBA;

public final class ACTIVITY_REQUIRED extends SystemException {
   public ACTIVITY_REQUIRED() {
      this("");
   }

   public ACTIVITY_REQUIRED(String var1) {
      this(var1, 0, CompletionStatus.COMPLETED_NO);
   }

   public ACTIVITY_REQUIRED(int var1, CompletionStatus var2) {
      this("", var1, var2);
   }

   public ACTIVITY_REQUIRED(String var1, int var2, CompletionStatus var3) {
      super(var1, var2, var3);
   }
}
