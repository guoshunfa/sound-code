package javax.management;

public class RuntimeErrorException extends JMRuntimeException {
   private static final long serialVersionUID = 704338937753949796L;
   private Error error;

   public RuntimeErrorException(Error var1) {
      this.error = var1;
   }

   public RuntimeErrorException(Error var1, String var2) {
      super(var2);
      this.error = var1;
   }

   public Error getTargetError() {
      return this.error;
   }

   public Throwable getCause() {
      return this.error;
   }
}
