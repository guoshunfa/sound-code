package java.lang.reflect;

public class InvocationTargetException extends ReflectiveOperationException {
   private static final long serialVersionUID = 4085088731926701167L;
   private Throwable target;

   protected InvocationTargetException() {
      super((Throwable)null);
   }

   public InvocationTargetException(Throwable var1) {
      super((Throwable)null);
      this.target = var1;
   }

   public InvocationTargetException(Throwable var1, String var2) {
      super(var2, (Throwable)null);
      this.target = var1;
   }

   public Throwable getTargetException() {
      return this.target;
   }

   public Throwable getCause() {
      return this.target;
   }
}
