package java.lang;

public class ExceptionInInitializerError extends LinkageError {
   private static final long serialVersionUID = 1521711792217232256L;
   private Throwable exception;

   public ExceptionInInitializerError() {
      this.initCause((Throwable)null);
   }

   public ExceptionInInitializerError(Throwable var1) {
      this.initCause((Throwable)null);
      this.exception = var1;
   }

   public ExceptionInInitializerError(String var1) {
      super(var1);
      this.initCause((Throwable)null);
   }

   public Throwable getException() {
      return this.exception;
   }

   public Throwable getCause() {
      return this.exception;
   }
}
