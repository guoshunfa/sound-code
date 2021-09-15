package org.omg.CORBA;

public final class NO_PERMISSION extends SystemException {
   public NO_PERMISSION() {
      this("");
   }

   public NO_PERMISSION(String var1) {
      this(var1, 0, CompletionStatus.COMPLETED_NO);
   }

   public NO_PERMISSION(int var1, CompletionStatus var2) {
      this("", var1, var2);
   }

   public NO_PERMISSION(String var1, int var2, CompletionStatus var3) {
      super(var1, var2, var3);
   }
}
