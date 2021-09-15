package org.omg.CORBA;

public final class BAD_CONTEXT extends SystemException {
   public BAD_CONTEXT() {
      this("");
   }

   public BAD_CONTEXT(String var1) {
      this(var1, 0, CompletionStatus.COMPLETED_NO);
   }

   public BAD_CONTEXT(int var1, CompletionStatus var2) {
      this("", var1, var2);
   }

   public BAD_CONTEXT(String var1, int var2, CompletionStatus var3) {
      super(var1, var2, var3);
   }
}
