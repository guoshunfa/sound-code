package org.omg.CORBA;

public final class BAD_OPERATION extends SystemException {
   public BAD_OPERATION() {
      this("");
   }

   public BAD_OPERATION(String var1) {
      this(var1, 0, CompletionStatus.COMPLETED_NO);
   }

   public BAD_OPERATION(int var1, CompletionStatus var2) {
      this("", var1, var2);
   }

   public BAD_OPERATION(String var1, int var2, CompletionStatus var3) {
      super(var1, var2, var3);
   }
}
