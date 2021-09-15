package java.security;

public class InvalidAlgorithmParameterException extends GeneralSecurityException {
   private static final long serialVersionUID = 2864672297499471472L;

   public InvalidAlgorithmParameterException() {
   }

   public InvalidAlgorithmParameterException(String var1) {
      super(var1);
   }

   public InvalidAlgorithmParameterException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public InvalidAlgorithmParameterException(Throwable var1) {
      super(var1);
   }
}
