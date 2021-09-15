package java.security;

public class GeneralSecurityException extends Exception {
   private static final long serialVersionUID = 894798122053539237L;

   public GeneralSecurityException() {
   }

   public GeneralSecurityException(String var1) {
      super(var1);
   }

   public GeneralSecurityException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public GeneralSecurityException(Throwable var1) {
      super(var1);
   }
}
