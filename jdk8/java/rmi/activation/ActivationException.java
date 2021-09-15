package java.rmi.activation;

public class ActivationException extends Exception {
   public Throwable detail;
   private static final long serialVersionUID = -4320118837291406071L;

   public ActivationException() {
      this.initCause((Throwable)null);
   }

   public ActivationException(String var1) {
      super(var1);
      this.initCause((Throwable)null);
   }

   public ActivationException(String var1, Throwable var2) {
      super(var1);
      this.initCause((Throwable)null);
      this.detail = var2;
   }

   public String getMessage() {
      return this.detail == null ? super.getMessage() : super.getMessage() + "; nested exception is: \n\t" + this.detail.toString();
   }

   public Throwable getCause() {
      return this.detail;
   }
}
