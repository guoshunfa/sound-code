package java.security;

public class SignatureException extends GeneralSecurityException {
   private static final long serialVersionUID = 7509989324975124438L;

   public SignatureException() {
   }

   public SignatureException(String var1) {
      super(var1);
   }

   public SignatureException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public SignatureException(Throwable var1) {
      super(var1);
   }
}
