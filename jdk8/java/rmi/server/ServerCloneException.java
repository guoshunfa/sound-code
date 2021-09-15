package java.rmi.server;

public class ServerCloneException extends CloneNotSupportedException {
   public Exception detail;
   private static final long serialVersionUID = 6617456357664815945L;

   public ServerCloneException(String var1) {
      super(var1);
      this.initCause((Throwable)null);
   }

   public ServerCloneException(String var1, Exception var2) {
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
