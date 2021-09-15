package java.util.concurrent;

public class ExecutionException extends Exception {
   private static final long serialVersionUID = 7830266012832686185L;

   protected ExecutionException() {
   }

   protected ExecutionException(String var1) {
      super(var1);
   }

   public ExecutionException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public ExecutionException(Throwable var1) {
      super(var1);
   }
}
