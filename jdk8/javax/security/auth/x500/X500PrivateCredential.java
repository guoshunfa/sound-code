package javax.security.auth.x500;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.security.auth.Destroyable;

public final class X500PrivateCredential implements Destroyable {
   private X509Certificate cert;
   private PrivateKey key;
   private String alias;

   public X500PrivateCredential(X509Certificate var1, PrivateKey var2) {
      if (var1 != null && var2 != null) {
         this.cert = var1;
         this.key = var2;
         this.alias = null;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public X500PrivateCredential(X509Certificate var1, PrivateKey var2, String var3) {
      if (var1 != null && var2 != null && var3 != null) {
         this.cert = var1;
         this.key = var2;
         this.alias = var3;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public X509Certificate getCertificate() {
      return this.cert;
   }

   public PrivateKey getPrivateKey() {
      return this.key;
   }

   public String getAlias() {
      return this.alias;
   }

   public void destroy() {
      this.cert = null;
      this.key = null;
      this.alias = null;
   }

   public boolean isDestroyed() {
      return this.cert == null && this.key == null && this.alias == null;
   }
}
