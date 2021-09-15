package java.security.cert;

public abstract class CRL {
   private String type;

   protected CRL(String var1) {
      this.type = var1;
   }

   public final String getType() {
      return this.type;
   }

   public abstract String toString();

   public abstract boolean isRevoked(Certificate var1);
}
