package org.omg.CORBA;

public final class INV_OBJREF extends SystemException {
   public INV_OBJREF() {
      this("");
   }

   public INV_OBJREF(String var1) {
      this(var1, 0, CompletionStatus.COMPLETED_NO);
   }

   public INV_OBJREF(int var1, CompletionStatus var2) {
      this("", var1, var2);
   }

   public INV_OBJREF(String var1, int var2, CompletionStatus var3) {
      super(var1, var2, var3);
   }
}
