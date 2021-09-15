package java.util.concurrent;

public class CompletionException extends RuntimeException {
   private static final long serialVersionUID = 7830266012832686185L;

   protected CompletionException() {
   }

   protected CompletionException(String var1) {
      super(var1);
   }

   public CompletionException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public CompletionException(Throwable var1) {
      super(var1);
   }
}
