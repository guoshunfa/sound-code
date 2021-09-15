package org.omg.CORBA;

public final class INTERNAL extends SystemException {
   public INTERNAL() {
      this("");
   }

   public INTERNAL(String var1) {
      this(var1, 0, CompletionStatus.COMPLETED_NO);
   }

   public INTERNAL(int var1, CompletionStatus var2) {
      this("", var1, var2);
   }

   public INTERNAL(String var1, int var2, CompletionStatus var3) {
      super(var1, var2, var3);
   }
}
