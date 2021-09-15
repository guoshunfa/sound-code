package org.omg.CORBA;

public final class REBIND extends SystemException {
   public REBIND() {
      this("");
   }

   public REBIND(String var1) {
      this(var1, 0, CompletionStatus.COMPLETED_NO);
   }

   public REBIND(int var1, CompletionStatus var2) {
      this("", var1, var2);
   }

   public REBIND(String var1, int var2, CompletionStatus var3) {
      super(var1, var2, var3);
   }
}
