package org.omg.CORBA;

public final class PERSIST_STORE extends SystemException {
   public PERSIST_STORE() {
      this("");
   }

   public PERSIST_STORE(String var1) {
      this(var1, 0, CompletionStatus.COMPLETED_NO);
   }

   public PERSIST_STORE(int var1, CompletionStatus var2) {
      this("", var1, var2);
   }

   public PERSIST_STORE(String var1, int var2, CompletionStatus var3) {
      super(var1, var2, var3);
   }
}
