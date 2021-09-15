package sun.security.rsa;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import sun.security.pkcs.PKCS8Key;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;

public final class RSAPrivateCrtKeyImpl extends PKCS8Key implements RSAPrivateCrtKey {
   private static final long serialVersionUID = -1326088454257084918L;
   private BigInteger n;
   private BigInteger e;
   private BigInteger d;
   private BigInteger p;
   private BigInteger q;
   private BigInteger pe;
   private BigInteger qe;
   private BigInteger coeff;
   static final AlgorithmId rsaId;

   public static RSAPrivateKey newKey(byte[] var0) throws InvalidKeyException {
      RSAPrivateCrtKeyImpl var1 = new RSAPrivateCrtKeyImpl(var0);
      return (RSAPrivateKey)(var1.getPublicExponent().signum() == 0 ? new RSAPrivateKeyImpl(var1.getModulus(), var1.getPrivateExponent()) : var1);
   }

   RSAPrivateCrtKeyImpl(byte[] var1) throws InvalidKeyException {
      this.decode(var1);
      RSAKeyFactory.checkRSAProviderKeyLengths(this.n.bitLength(), this.e);
   }

   RSAPrivateCrtKeyImpl(BigInteger var1, BigInteger var2, BigInteger var3, BigInteger var4, BigInteger var5, BigInteger var6, BigInteger var7, BigInteger var8) throws InvalidKeyException {
      this.n = var1;
      this.e = var2;
      this.d = var3;
      this.p = var4;
      this.q = var5;
      this.pe = var6;
      this.qe = var7;
      this.coeff = var8;
      RSAKeyFactory.checkRSAProviderKeyLengths(var1.bitLength(), var2);
      this.algid = rsaId;

      try {
         DerOutputStream var9 = new DerOutputStream();
         var9.putInteger(0);
         var9.putInteger(var1);
         var9.putInteger(var2);
         var9.putInteger(var3);
         var9.putInteger(var4);
         var9.putInteger(var5);
         var9.putInteger(var6);
         var9.putInteger(var7);
         var9.putInteger(var8);
         DerValue var10 = new DerValue((byte)48, var9.toByteArray());
         this.key = var10.toByteArray();
      } catch (IOException var11) {
         throw new InvalidKeyException(var11);
      }
   }

   public String getAlgorithm() {
      return "RSA";
   }

   public BigInteger getModulus() {
      return this.n;
   }

   public BigInteger getPublicExponent() {
      return this.e;
   }

   public BigInteger getPrivateExponent() {
      return this.d;
   }

   public BigInteger getPrimeP() {
      return this.p;
   }

   public BigInteger getPrimeQ() {
      return this.q;
   }

   public BigInteger getPrimeExponentP() {
      return this.pe;
   }

   public BigInteger getPrimeExponentQ() {
      return this.qe;
   }

   public BigInteger getCrtCoefficient() {
      return this.coeff;
   }

   protected void parseKeyBits() throws InvalidKeyException {
      try {
         DerInputStream var1 = new DerInputStream(this.key);
         DerValue var2 = var1.getDerValue();
         if (var2.tag != 48) {
            throw new IOException("Not a SEQUENCE");
         } else {
            DerInputStream var3 = var2.data;
            int var4 = var3.getInteger();
            if (var4 != 0) {
               throw new IOException("Version must be 0");
            } else {
               this.n = var3.getPositiveBigInteger();
               this.e = var3.getPositiveBigInteger();
               this.d = var3.getPositiveBigInteger();
               this.p = var3.getPositiveBigInteger();
               this.q = var3.getPositiveBigInteger();
               this.pe = var3.getPositiveBigInteger();
               this.qe = var3.getPositiveBigInteger();
               this.coeff = var3.getPositiveBigInteger();
               if (var2.data.available() != 0) {
                  throw new IOException("Extra data available");
               }
            }
         }
      } catch (IOException var5) {
         throw new InvalidKeyException("Invalid RSA private key", var5);
      }
   }

   static {
      rsaId = new AlgorithmId(AlgorithmId.RSAEncryption_oid);
   }
}
