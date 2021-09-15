package java.lang;

public class InternalError extends VirtualMachineError {
   private static final long serialVersionUID = -9062593416125562365L;

   public InternalError() {
   }

   public InternalError(String var1) {
      super(var1);
   }

   public InternalError(String var1, Throwable var2) {
      super(var1, var2);
   }

   public InternalError(Throwable var1) {
      super(var1);
   }
}
