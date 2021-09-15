package org.omg.CORBA;

public final class BAD_TYPECODE extends SystemException {
   public BAD_TYPECODE() {
      this("");
   }

   public BAD_TYPECODE(String var1) {
      this(var1, 0, CompletionStatus.COMPLETED_NO);
   }

   public BAD_TYPECODE(int var1, CompletionStatus var2) {
      this("", var1, var2);
   }

   public BAD_TYPECODE(String var1, int var2, CompletionStatus var3) {
      super(var1, var2, var3);
   }
}
