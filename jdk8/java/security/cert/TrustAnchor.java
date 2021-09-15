package java.security.cert;

import java.io.IOException;
import java.security.PublicKey;
import javax.security.auth.x500.X500Principal;
import sun.security.x509.NameConstraintsExtension;

public class TrustAnchor {
   private final PublicKey pubKey;
   private final String caName;
   private final X500Principal caPrincipal;
   private final X509Certificate trustedCert;
   private byte[] ncBytes;
   private NameConstraintsExtension nc;

   public TrustAnchor(X509Certificate var1, byte[] var2) {
      if (var1 == null) {
         throw new NullPointerException("the trustedCert parameter must be non-null");
      } else {
         this.trustedCert = var1;
         this.pubKey = null;
         this.caName = null;
         this.caPrincipal = null;
         this.setNameConstraints(var2);
      }
   }

   public TrustAnchor(X500Principal var1, PublicKey var2, byte[] var3) {
      if (var1 != null && var2 != null) {
         this.trustedCert = null;
         this.caPrincipal = var1;
         this.caName = var1.getName();
         this.pubKey = var2;
         this.setNameConstraints(var3);
      } else {
         throw new NullPointerException();
      }
   }

   public TrustAnchor(String var1, PublicKey var2, byte[] var3) {
      if (var2 == null) {
         throw new NullPointerException("the pubKey parameter must be non-null");
      } else if (var1 == null) {
         throw new NullPointerException("the caName parameter must be non-null");
      } else if (var1.length() == 0) {
         throw new IllegalArgumentException("the caName parameter must be a non-empty String");
      } else {
         this.caPrincipal = new X500Principal(var1);
         this.pubKey = var2;
         this.caName = var1;
         this.trustedCert = null;
         this.setNameConstraints(var3);
      }
   }

   public final X509Certificate getTrustedCert() {
      return this.trustedCert;
   }

   public final X500Principal getCA() {
      return this.caPrincipal;
   }

   public final String getCAName() {
      return this.caName;
   }

   public final PublicKey getCAPublicKey() {
      return this.pubKey;
   }

   private void setNameConstraints(byte[] var1) {
      if (var1 == null) {
         this.ncBytes = null;
         this.nc = null;
      } else {
         this.ncBytes = (byte[])var1.clone();

         try {
            this.nc = new NameConstraintsExtension(Boolean.FALSE, var1);
         } catch (IOException var4) {
            IllegalArgumentException var3 = new IllegalArgumentException(var4.getMessage());
            var3.initCause(var4);
            throw var3;
         }
      }

   }

   public final byte[] getNameConstraints() {
      return this.ncBytes == null ? null : (byte[])this.ncBytes.clone();
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append("[\n");
      if (this.pubKey != null) {
         var1.append("  Trusted CA Public Key: " + this.pubKey.toString() + "\n");
         var1.append("  Trusted CA Issuer Name: " + this.caName + "\n");
      } else {
         var1.append("  Trusted CA cert: " + this.trustedCert.toString() + "\n");
      }

      if (this.nc != null) {
         var1.append("  Name Constraints: " + this.nc.toString() + "\n");
      }

      return var1.toString();
   }
}
