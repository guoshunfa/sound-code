package org.omg.CORBA;

public final class INITIALIZE extends SystemException {
   public INITIALIZE() {
      this("");
   }

   public INITIALIZE(String var1) {
      this(var1, 0, CompletionStatus.COMPLETED_NO);
   }

   public INITIALIZE(int var1, CompletionStatus var2) {
      this("", var1, var2);
   }

   public INITIALIZE(String var1, int var2, CompletionStatus var3) {
      super(var1, var2, var3);
   }
}
