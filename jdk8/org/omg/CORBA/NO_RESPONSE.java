package org.omg.CORBA;

public final class NO_RESPONSE extends SystemException {
   public NO_RESPONSE() {
      this("");
   }

   public NO_RESPONSE(String var1) {
      this(var1, 0, CompletionStatus.COMPLETED_NO);
   }

   public NO_RESPONSE(int var1, CompletionStatus var2) {
      this("", var1, var2);
   }

   public NO_RESPONSE(String var1, int var2, CompletionStatus var3) {
      super(var1, var2, var3);
   }
}
