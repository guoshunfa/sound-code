package java.security;

public class ProviderException extends RuntimeException {
   private static final long serialVersionUID = 5256023526693665674L;

   public ProviderException() {
   }

   public ProviderException(String var1) {
      super(var1);
   }

   public ProviderException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public ProviderException(Throwable var1) {
      super(var1);
   }
}
