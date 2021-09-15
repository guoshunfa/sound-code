package java.lang;

public class BootstrapMethodError extends LinkageError {
   private static final long serialVersionUID = 292L;

   public BootstrapMethodError() {
   }

   public BootstrapMethodError(String var1) {
      super(var1);
   }

   public BootstrapMethodError(String var1, Throwable var2) {
      super(var1, var2);
   }

   public BootstrapMethodError(Throwable var1) {
      super(var1 == null ? null : var1.toString());
      this.initCause(var1);
   }
}
