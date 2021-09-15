package org.omg.CORBA;

public final class UNKNOWN extends SystemException {
   public UNKNOWN() {
      this("");
   }

   public UNKNOWN(String var1) {
      this(var1, 0, CompletionStatus.COMPLETED_NO);
   }

   public UNKNOWN(int var1, CompletionStatus var2) {
      this("", var1, var2);
   }

   public UNKNOWN(String var1, int var2, CompletionStatus var3) {
      super(var1, var2, var3);
   }
}
