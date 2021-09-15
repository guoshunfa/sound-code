package java.util;

public class ConcurrentModificationException extends RuntimeException {
   private static final long serialVersionUID = -3666751008965953603L;

   public ConcurrentModificationException() {
   }

   public ConcurrentModificationException(String var1) {
      super(var1);
   }

   public ConcurrentModificationException(Throwable var1) {
      super(var1);
   }

   public ConcurrentModificationException(String var1, Throwable var2) {
      super(var1, var2);
   }
}
