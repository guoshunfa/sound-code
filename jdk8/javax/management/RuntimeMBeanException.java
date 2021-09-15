package javax.management;

public class RuntimeMBeanException extends JMRuntimeException {
   private static final long serialVersionUID = 5274912751982730171L;
   private RuntimeException runtimeException;

   public RuntimeMBeanException(RuntimeException var1) {
      this.runtimeException = var1;
   }

   public RuntimeMBeanException(RuntimeException var1, String var2) {
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
