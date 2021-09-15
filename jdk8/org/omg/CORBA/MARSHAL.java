package org.omg.CORBA;

public final class MARSHAL extends SystemException {
   public MARSHAL() {
      this("");
   }

   public MARSHAL(String var1) {
      this(var1, 0, CompletionStatus.COMPLETED_NO);
   }

   public MARSHAL(int var1, CompletionStatus var2) {
      this("", var1, var2);
   }

   public MARSHAL(String var1, int var2, CompletionStatus var3) {
      super(var1, var2, var3);
   }
}
