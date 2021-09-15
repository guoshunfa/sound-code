package org.omg.CORBA;

public final class OBJECT_NOT_EXIST extends SystemException {
   public OBJECT_NOT_EXIST() {
      this("");
   }

   public OBJECT_NOT_EXIST(String var1) {
      this(var1, 0, CompletionStatus.COMPLETED_NO);
   }

   public OBJECT_NOT_EXIST(int var1, CompletionStatus var2) {
      this("", var1, var2);
   }

   public OBJECT_NOT_EXIST(String var1, int var2, CompletionStatus var3) {
      super(var1, var2, var3);
   }
}
