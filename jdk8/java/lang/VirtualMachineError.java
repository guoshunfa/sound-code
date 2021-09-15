package java.lang;

public abstract class VirtualMachineError extends Error {
   private static final long serialVersionUID = 4161983926571568670L;

   public VirtualMachineError() {
   }

   public VirtualMachineError(String var1) {
      super(var1);
   }

   public VirtualMachineError(String var1, Throwable var2) {
      super(var1, var2);
   }

   public VirtualMachineError(Throwable var1) {
      super(var1);
   }
}
