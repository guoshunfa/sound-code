package java.lang;

public class IllegalArgumentException extends RuntimeException {
   private static final long serialVersionUID = -5365630128856068164L;

   public IllegalArgumentException() {
   }

   public IllegalArgumentException(String var1) {
      super(var1);
   }

   public IllegalArgumentException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public IllegalArgumentException(Throwable var1) {
      super(var1);
   }
}
