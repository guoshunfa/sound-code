package org.omg.CORBA;

public final class NO_RESOURCES extends SystemException {
   public NO_RESOURCES() {
      this("");
   }

   public NO_RESOURCES(String var1) {
      this(var1, 0, CompletionStatus.COMPLETED_NO);
   }

   public NO_RESOURCES(int var1, CompletionStatus var2) {
      this("", var1, var2);
   }

   public NO_RESOURCES(String var1, int var2, CompletionStatus var3) {
      super(var1, var2, var3);
   }
}
