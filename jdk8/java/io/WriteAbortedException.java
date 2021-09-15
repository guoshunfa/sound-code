package java.io;

public class WriteAbortedException extends ObjectStreamException {
   private static final long serialVersionUID = -3326426625597282442L;
   public Exception detail;

   public WriteAbortedException(String var1, Exception var2) {
      super(var1);
      this.initCause((Throwable)null);
      this.detail = var2;
   }

   public String getMessage() {
      return this.detail == null ? super.getMessage() : super.getMessage() + "; " + this.detail.toString();
   }

   public Throwable getCause() {
      return this.detail;
   }
}
