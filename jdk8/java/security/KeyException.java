package java.security;

public class KeyException extends GeneralSecurityException {
   private static final long serialVersionUID = -7483676942812432108L;

   public KeyException() {
   }

   public KeyException(String var1) {
      super(var1);
   }

   public KeyException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public KeyException(Throwable var1) {
      super(var1);
   }
}
