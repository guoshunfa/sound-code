package javax.management;

public class RuntimeOperationsException extends JMRuntimeException {
   private static final long serialVersionUID = -8408923047489133588L;
   private RuntimeException runtimeException;

   public RuntimeOperationsException(RuntimeException var1) {
      this.runtimeException = var1;
   }

   public RuntimeOperationsException(RuntimeException var1, String var2) {
      super(var2);
      this.runtimeException = var1;
   }

   public RuntimeException getTargetException() {
      return this.runtimeException;
   }

   public Throwable getCause() {
      return this.runtimeException;
   }
}
