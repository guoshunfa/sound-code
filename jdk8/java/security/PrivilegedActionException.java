package java.security;

public class PrivilegedActionException extends Exception {
   private static final long serialVersionUID = 4724086851538908602L;
   private Exception exception;

   public PrivilegedActionException(Exception var1) {
      super((Throwable)null);
      this.exception = var1;
   }

   public Exception getException() {
      return this.exception;
   }

   public Throwable getCause() {
      return this.exception;
   }

   public String toString() {
      String var1 = this.getClass().getName();
      return this.exception != null ? var1 + ": " + this.exception.toString() : var1;
   }
}
