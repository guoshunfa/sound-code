package org.omg.CORBA;

public final class CODESET_INCOMPATIBLE extends SystemException {
   public CODESET_INCOMPATIBLE() {
      this("");
   }

   public CODESET_INCOMPATIBLE(String var1) {
      this(var1, 0, CompletionStatus.COMPLETED_NO);
   }

   public CODESET_INCOMPATIBLE(int var1, CompletionStatus var2) {
      this("", var1, var2);
   }

   public CODESET_INCOMPATIBLE(String var1, int var2, CompletionStatus var3) {
      super(var1, var2, var3);
   }
}
