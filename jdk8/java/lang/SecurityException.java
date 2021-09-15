package java.lang;

public class SecurityException extends RuntimeException {
   private static final long serialVersionUID = 6878364983674394167L;

   public SecurityException() {
   }

   public SecurityException(String var1) {
      super(var1);
   }

   public SecurityException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public SecurityException(Throwable var1) {
      super(var1);
   }
}
