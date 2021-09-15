package javax.management;

public class ReflectionException extends JMException {
   private static final long serialVersionUID = 9170809325636915553L;
   private Exception exception;

   public ReflectionException(Exception var1) {
      this.exception = var1;
   }

   public ReflectionException(Exception var1, String var2) {
      super(var2);
      this.exception = var1;
   }

   public Exception getTargetException() {
      return this.exception;
   }

   public Throwable getCause() {
      return this.exception;
   }
}
