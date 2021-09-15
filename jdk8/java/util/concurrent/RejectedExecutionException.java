package java.util.concurrent;

public class RejectedExecutionException extends RuntimeException {
   private static final long serialVersionUID = -375805702767069545L;

   public RejectedExecutionException() {
   }

   public RejectedExecutionException(String var1) {
      super(var1);
   }

   public RejectedExecutionException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public RejectedExecutionException(Throwable var1) {
      super(var1);
   }
}
