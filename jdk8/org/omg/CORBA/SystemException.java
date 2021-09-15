package org.omg.CORBA;

public abstract class SystemException extends RuntimeException {
   public int minor;
   public CompletionStatus completed;

   protected SystemException(String var1, int var2, CompletionStatus var3) {
      super(var1);
      this.minor = var2;
      this.completed = var3;
   }

   public String toString() {
      String var1 = super.toString();
      int var2 = this.minor & -4096;
      switch(var2) {
      case 1330446336:
         var1 = var1 + "  vmcid: OMG";
         break;
      case 1398079488:
         var1 = var1 + "  vmcid: SUN";
         break;
      default:
         var1 = var1 + "  vmcid: 0x" + Integer.toHexString(var2);
      }

      int var3 = this.minor & 4095;
      var1 = var1 + "  minor code: " + var3;
      switch(this.completed.value()) {
      case 0:
         var1 = var1 + "  completed: Yes";
         break;
      case 1:
         var1 = var1 + "  completed: No";
         break;
      case 2:
      default:
         var1 = var1 + " completed: Maybe";
      }

      return var1;
   }
}
