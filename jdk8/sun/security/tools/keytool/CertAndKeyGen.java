package sun.security.tools.keytool;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Random;
import sun.security.pkcs10.PKCS10;
import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateExtensions;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;
import sun.security.x509.X509Key;

public final class CertAndKeyGen {
   private SecureRandom prng;
   private String sigAlg;
   private KeyPairGenerator keyGen;
   private PublicKey publicKey;
   private PrivateKey privateKey;

   public CertAndKeyGen(String var1, String var2) throws NoSuchAlgorithmException {
      this.keyGen = KeyPairGenerator.getInstance(var1);
      this.sigAlg = var2;
   }

   public CertAndKeyGen(String var1, String var2, String var3) throws NoSuchAlgorithmException, NoSuchProviderException {
      if (var3 == null) {
         this.keyGen = KeyPairGenerator.getInstance(var1);
      } else {
         try {
            this.keyGen = KeyPairGenerator.getInstance(var1, var3);
         } catch (Exception var5) {
            this.keyGen = KeyPairGenerator.getInstance(var1);
         }
      }

      this.sigAlg = var2;
   }

   public void setRandom(SecureRandom var1) {
      this.prng = var1;
   }

   public void generate(int var1) throws InvalidKeyException {
      KeyPair var2;
      try {
         if (this.prng == null) {
            this.prng = new SecureRandom();
         }

         this.keyGen.initialize(var1, this.prng);
         var2 = this.keyGen.generateKeyPair();
      } catch (Exception var4) {
         throw new IllegalArgumentException(var4.getMessage());
      }

      this.publicKey = var2.getPublic();
      this.privateKey = var2.getPrivate();
      if (!"X.509".equalsIgnoreCase(this.publicKey.getFormat())) {
         throw new IllegalArgumentException("publicKey's is not X.509, but " + this.publicKey.getFormat());
      }
   }

   public X509Key getPublicKey() {
      return !(this.publicKey instanceof X509Key) ? null : (X509Key)this.publicKey;
   }

   public PublicKey getPublicKeyAnyway() {
      return this.publicKey;
   }

   public PrivateKey getPrivateKey() {
      return this.privateKey;
   }

   public X509Certificate getSelfCertificate(X500Name var1, Date var2, long var3) throws CertificateException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, NoSuchProviderException {
      return this.getSelfCertificate(var1, var2, var3, (CertificateExtensions)null);
   }

   public X509Certificate getSelfCertificate(X500Name var1, Date var2, long var3, CertificateExtensions var5) throws CertificateException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, NoSuchProviderException {
      try {
         Date var7 = new Date();
         var7.setTime(var2.getTime() + var3 * 1000L);
         CertificateValidity var8 = new CertificateValidity(var2, var7);
         X509CertInfo var9 = new X509CertInfo();
         var9.set("version", new CertificateVersion(2));
         var9.set("serialNumber", new CertificateSerialNumber((new Random()).nextInt() & Integer.MAX_VALUE));
         AlgorithmId var10 = AlgorithmId.get(this.sigAlg);
         var9.set("algorithmID", new CertificateAlgorithmId(var10));
         var9.set("subject", var1);
         var9.set("key", new CertificateX509Key(this.publicKey));
         var9.set("validity", var8);
         var9.set("issuer", var1);
         if (var5 != null) {
            var9.set("extensions", var5);
         }

         X509CertImpl var6 = new X509CertImpl(var9);
         var6.sign(this.privateKey, this.sigAlg);
         return var6;
      } catch (IOException var11) {
         throw new CertificateEncodingException("getSelfCert: " + var11.getMessage());
      }
   }

   public X509Certificate getSelfCertificate(X500Name var1, long var2) throws CertificateException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, NoSuchProviderException {
      return this.getSelfCertificate(var1, new Date(), var2);
   }

   public PKCS10 getCertRequest(X500Name var1) throws InvalidKeyException, SignatureException {
      PKCS10 var2 = new PKCS10(this.publicKey);

      try {
         Signature var3 = Signature.getInstance(this.sigAlg);
         var3.initSign(this.privateKey);
         var2.encodeAndSign(var1, var3);
         return var2;
      } catch (CertificateException var4) {
         throw new SignatureException(this.sigAlg + " CertificateException");
      } catch (IOException var5) {
         throw new SignatureException(this.sigAlg + " IOException");
      } catch (NoSuchAlgorithmException var6) {
         throw new SignatureException(this.sigAlg + " unavailable?");
      }
   }
}
