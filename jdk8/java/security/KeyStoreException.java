package java.security;

public class KeyStoreException extends GeneralSecurityException {
   private static final long serialVersionUID = -1119353179322377262L;

   public KeyStoreException() {
   }

   public KeyStoreException(String var1) {
      super(var1);
   }

   public KeyStoreException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public KeyStoreException(Throwable var1) {
      super(var1);
   }
}
