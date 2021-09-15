package org.omg.CORBA;

public final class DATA_CONVERSION extends SystemException {
   public DATA_CONVERSION() {
      this("");
   }

   public DATA_CONVERSION(String var1) {
      this(var1, 0, CompletionStatus.COMPLETED_NO);
   }

   public DATA_CONVERSION(int var1, CompletionStatus var2) {
      this("", var1, var2);
   }

   public DATA_CONVERSION(String var1, int var2, CompletionStatus var3) {
      super(var1, var2, var3);
   }
}
