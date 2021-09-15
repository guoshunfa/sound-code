package java.security;

public class NoSuchAlgorithmException extends GeneralSecurityException {
   private static final long serialVersionUID = -7443947487218346562L;

   public NoSuchAlgorithmException() {
   }

   public NoSuchAlgorithmException(String var1) {
      super(var1);
   }

   public NoSuchAlgorithmException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public NoSuchAlgorithmException(Throwable var1) {
      super(var1);
   }
}
