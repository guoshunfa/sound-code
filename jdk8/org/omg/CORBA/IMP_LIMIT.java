package org.omg.CORBA;

public final class IMP_LIMIT extends SystemException {
   public IMP_LIMIT() {
      this("");
   }

   public IMP_LIMIT(String var1) {
      this(var1, 0, CompletionStatus.COMPLETED_NO);
   }

   public IMP_LIMIT(int var1, CompletionStatus var2) {
      this("", var1, var2);
   }

   public IMP_LIMIT(String var1, int var2, CompletionStatus var3) {
      super(var1, var2, var3);
   }
}
