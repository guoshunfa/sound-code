package sun.security.pkcs10;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Base64;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;
import sun.security.x509.X500Name;
import sun.security.x509.X509Key;

public class PKCS10 {
   private X500Name subject;
   private PublicKey subjectPublicKeyInfo;
   private String sigAlg;
   private PKCS10Attributes attributeSet;
   private byte[] encoded;

   public PKCS10(PublicKey var1) {
      this.subjectPublicKeyInfo = var1;
      this.attributeSet = new PKCS10Attributes();
   }

   public PKCS10(PublicKey var1, PKCS10Attributes var2) {
      this.subjectPublicKeyInfo = var1;
      this.attributeSet = var2;
   }

   public PKCS10(byte[] var1) throws IOException, SignatureException, NoSuchAlgorithmException {
      this.encoded = var1;
      DerInputStream var2 = new DerInputStream(var1);
      DerValue[] var3 = var2.getSequence(3);
      if (var3.length != 3) {
         throw new IllegalArgumentException("not a PKCS #10 request");
      } else {
         var1 = var3[0].toByteArray();
         AlgorithmId var4 = AlgorithmId.parse(var3[1]);
         byte[] var5 = var3[2].getBitString();
         BigInteger var7 = var3[0].data.getBigInteger();
         if (!var7.equals(BigInteger.ZERO)) {
            throw new IllegalArgumentException("not PKCS #10 v1");
         } else {
            this.subject = new X500Name(var3[0].data);
            this.subjectPublicKeyInfo = X509Key.parse(var3[0].data.getDerValue());
            if (var3[0].data.available() != 0) {
               this.attributeSet = new PKCS10Attributes(var3[0].data);
            } else {
               this.attributeSet = new PKCS10Attributes();
            }

            if (var3[0].data.available() != 0) {
               throw new IllegalArgumentException("illegal PKCS #10 data");
            } else {
               try {
                  this.sigAlg = var4.getName();
                  Signature var6 = Signature.getInstance(this.sigAlg);
                  var6.initVerify(this.subjectPublicKeyInfo);
                  var6.update(var1);
                  if (!var6.verify(var5)) {
                     throw new SignatureException("Invalid PKCS #10 signature");
                  }
               } catch (InvalidKeyException var10) {
                  throw new SignatureException("invalid key");
               }
            }
         }
      }
   }

   public void encodeAndSign(X500Name var1, Signature var2) throws CertificateException, IOException, SignatureException {
      if (this.encoded != null) {
         throw new SignatureException("request is already signed");
      } else {
         this.subject = var1;
         DerOutputStream var4 = new DerOutputStream();
         var4.putInteger(BigInteger.ZERO);
         var1.encode(var4);
         var4.write(this.subjectPublicKeyInfo.getEncoded());
         this.attributeSet.encode(var4);
         DerOutputStream var3 = new DerOutputStream();
         var3.write((byte)48, (DerOutputStream)var4);
         byte[] var5 = var3.toByteArray();
         var4 = var3;
         var2.update(var5, 0, var5.length);
         byte[] var6 = var2.sign();
         this.sigAlg = var2.getAlgorithm();
         AlgorithmId var7 = null;

         try {
            var7 = AlgorithmId.get(var2.getAlgorithm());
         } catch (NoSuchAlgorithmException var9) {
            throw new SignatureException(var9);
         }

         var7.encode(var3);
         var3.putBitString(var6);
         var3 = new DerOutputStream();
         var3.write((byte)48, (DerOutputStream)var4);
         this.encoded = var3.toByteArray();
      }
   }

   public X500Name getSubjectName() {
      return this.subject;
   }

   public PublicKey getSubjectPublicKeyInfo() {
      return this.subjectPublicKeyInfo;
   }

   public String getSigAlg() {
      return this.sigAlg;
   }

   public PKCS10Attributes getAttributes() {
      return this.attributeSet;
   }

   public byte[] getEncoded() {
      return this.encoded != null ? (byte[])this.encoded.clone() : null;
   }

   public void print(PrintStream var1) throws IOException, SignatureException {
      if (this.encoded == null) {
         throw new SignatureException("Cert request was not signed");
      } else {
         byte[] var2 = new byte[]{13, 10};
         var1.println("-----BEGIN NEW CERTIFICATE REQUEST-----");
         var1.println(Base64.getMimeEncoder(64, var2).encodeToString(this.encoded));
         var1.println("-----END NEW CERTIFICATE REQUEST-----");
      }
   }

   public String toString() {
      return "[PKCS #10 certificate request:\n" + this.subjectPublicKeyInfo.toString() + " subject: <" + this.subject + ">\n attributes: " + this.attributeSet.toString() + "\n]";
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof PKCS10)) {
         return false;
      } else if (this.encoded == null) {
         return false;
      } else {
         byte[] var2 = ((PKCS10)var1).getEncoded();
         return var2 == null ? false : Arrays.equals(this.encoded, var2);
      }
   }

   public int hashCode() {
      int var1 = 0;
      if (this.encoded != null) {
         for(int var2 = 1; var2 < this.encoded.length; ++var2) {
            var1 += this.encoded[var2] * var2;
         }
      }

      return var1;
   }
}
