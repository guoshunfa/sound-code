package java.security.cert;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import sun.security.x509.X509CRLImpl;

public abstract class X509CRL extends CRL implements X509Extension {
   private transient X500Principal issuerPrincipal;

   protected X509CRL() {
      super("X.509");
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof X509CRL)) {
         return false;
      } else {
         try {
            byte[] var2 = X509CRLImpl.getEncodedInternal(this);
            byte[] var3 = X509CRLImpl.getEncodedInternal((X509CRL)var1);
            return Arrays.equals(var2, var3);
         } catch (CRLException var4) {
            return false;
         }
      }
   }

   public int hashCode() {
      int var1 = 0;

      try {
         byte[] var2 = X509CRLImpl.getEncodedInternal(this);

         for(int var3 = 1; var3 < var2.length; ++var3) {
            var1 += var2[var3] * var3;
         }

         return var1;
      } catch (CRLException var4) {
         return var1;
      }
   }

   public abstract byte[] getEncoded() throws CRLException;

   public abstract void verify(PublicKey var1) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException;

   public abstract void verify(PublicKey var1, String var2) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException;

   public void verify(PublicKey var1, Provider var2) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
      X509CRLImpl.verify(this, var1, var2);
   }

   public abstract int getVersion();

   public abstract Principal getIssuerDN();

   public X500Principal getIssuerX500Principal() {
      if (this.issuerPrincipal == null) {
         this.issuerPrincipal = X509CRLImpl.getIssuerX500Principal(this);
      }

      return this.issuerPrincipal;
   }

   public abstract Date getThisUpdate();

   public abstract Date getNextUpdate();

   public abstract X509CRLEntry getRevokedCertificate(BigInteger var1);

   public X509CRLEntry getRevokedCertificate(X509Certificate var1) {
      X500Principal var2 = var1.getIssuerX500Principal();
      X500Principal var3 = this.getIssuerX500Principal();
      return !var2.equals(var3) ? null : this.getRevokedCertificate(var1.getSerialNumber());
   }

   public abstract Set<? extends X509CRLEntry> getRevokedCertificates();

   public abstract byte[] getTBSCertList() throws CRLException;

   public abstract byte[] getSignature();

   public abstract String getSigAlgName();

   public abstract String getSigAlgOID();

   public abstract byte[] getSigAlgParams();
}
