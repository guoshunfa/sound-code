package java.security.cert;

import java.io.ByteArrayInputStream;
import java.io.NotSerializableException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Arrays;
import sun.security.x509.X509CertImpl;

public abstract class Certificate implements Serializable {
   private static final long serialVersionUID = -3585440601605666277L;
   private final String type;
   private int hash = -1;

   protected Certificate(String var1) {
      this.type = var1;
   }

   public final String getType() {
      return this.type;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Certificate)) {
         return false;
      } else {
         try {
            byte[] var2 = X509CertImpl.getEncodedInternal(this);
            byte[] var3 = X509CertImpl.getEncodedInternal((Certificate)var1);
            return Arrays.equals(var2, var3);
         } catch (CertificateException var4) {
            return false;
         }
      }
   }

   public int hashCode() {
      int var1 = this.hash;
      if (var1 == -1) {
         try {
            var1 = Arrays.hashCode(X509CertImpl.getEncodedInternal(this));
         } catch (CertificateException var3) {
            var1 = 0;
         }

         this.hash = var1;
      }

      return var1;
   }

   public abstract byte[] getEncoded() throws CertificateEncodingException;

   public abstract void verify(PublicKey var1) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException;

   public abstract void verify(PublicKey var1, String var2) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException;

   public void verify(PublicKey var1, Provider var2) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
      throw new UnsupportedOperationException();
   }

   public abstract String toString();

   public abstract PublicKey getPublicKey();

   protected Object writeReplace() throws ObjectStreamException {
      try {
         return new Certificate.CertificateRep(this.type, this.getEncoded());
      } catch (CertificateException var2) {
         throw new NotSerializableException("java.security.cert.Certificate: " + this.type + ": " + var2.getMessage());
      }
   }

   protected static class CertificateRep implements Serializable {
      private static final long serialVersionUID = -8563758940495660020L;
      private String type;
      private byte[] data;

      protected CertificateRep(String var1, byte[] var2) {
         this.type = var1;
         this.data = var2;
      }

      protected Object readResolve() throws ObjectStreamException {
         try {
            CertificateFactory var1 = CertificateFactory.getInstance(this.type);
            return var1.generateCertificate(new ByteArrayInputStream(this.data));
         } catch (CertificateException var2) {
            throw new NotSerializableException("java.security.cert.Certificate: " + this.type + ": " + var2.getMessage());
         }
      }
   }
}
