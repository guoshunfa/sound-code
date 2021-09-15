package org.omg.CORBA;

public final class BAD_PARAM extends SystemException {
   public BAD_PARAM() {
      this("");
   }

   public BAD_PARAM(String var1) {
      this(var1, 0, CompletionStatus.COMPLETED_NO);
   }

   public BAD_PARAM(int var1, CompletionStatus var2) {
      this("", var1, var2);
   }

   public BAD_PARAM(String var1, int var2, CompletionStatus var3) {
      super(var1, var2, var3);
   }
}
