package org.omg.CORBA;

public final class OBJ_ADAPTER extends SystemException {
   public OBJ_ADAPTER() {
      this("");
   }

   public OBJ_ADAPTER(String var1) {
      this(var1, 0, CompletionStatus.COMPLETED_NO);
   }

   public OBJ_ADAPTER(int var1, CompletionStatus var2) {
      this("", var1, var2);
   }

   public OBJ_ADAPTER(String var1, int var2, CompletionStatus var3) {
      super(var1, var2, var3);
   }
}
