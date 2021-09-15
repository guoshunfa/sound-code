package java.lang.reflect;

public class UndeclaredThrowableException extends RuntimeException {
   static final long serialVersionUID = 330127114055056639L;
   private Throwable undeclaredThrowable;

   public UndeclaredThrowableException(Throwable var1) {
      super((Throwable)null);
      this.undeclaredThrowable = var1;
   }

   public UndeclaredThrowableException(Throwable var1, String var2) {
      super(var2, (Throwable)null);
      this.undeclaredThrowable = var1;
   }

   public Throwable getUndeclaredThrowable() {
      return this.undeclaredThrowable;
   }

   public Throwable getCause() {
      return this.undeclaredThrowable;
   }
}
