package java.security;

public class InvalidKeyException extends KeyException {
   private static final long serialVersionUID = 5698479920593359816L;

   public InvalidKeyException() {
   }

   public InvalidKeyException(String var1) {
      super(var1);
   }

   public InvalidKeyException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public InvalidKeyException(Throwable var1) {
      super(var1);
   }
}
