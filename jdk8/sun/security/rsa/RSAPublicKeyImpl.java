package sun.security.rsa;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyRep;
import java.security.interfaces.RSAPublicKey;
import sun.security.util.BitArray;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.x509.X509Key;

public final class RSAPublicKeyImpl extends X509Key implements RSAPublicKey {
   private static final long serialVersionUID = 2644735423591199609L;
   private static final BigInteger THREE = BigInteger.valueOf(3L);
   private BigInteger n;
   private BigInteger e;

   public RSAPublicKeyImpl(BigInteger var1, BigInteger var2) throws InvalidKeyException {
      this.n = var1;
      this.e = var2;
      RSAKeyFactory.checkRSAProviderKeyLengths(var1.bitLength(), var2);
      this.checkExponentRange();
      this.algid = RSAPrivateCrtKeyImpl.rsaId;

      try {
         DerOutputStream var3 = new DerOutputStream();
         var3.putInteger(var1);
         var3.putInteger(var2);
         byte[] var4 = (new DerValue((byte)48, var3.toByteArray())).toByteArray();
         this.setKey(new BitArray(var4.length * 8, var4));
      } catch (IOException var5) {
         throw new InvalidKeyException(var5);
      }
   }

   public RSAPublicKeyImpl(byte[] var1) throws InvalidKeyException {
      this.decode(var1);
      RSAKeyFactory.checkRSAProviderKeyLengths(this.n.bitLength(), this.e);
      this.checkExponentRange();
   }

   private void checkExponentRange() throws InvalidKeyException {
      if (this.e.compareTo(this.n) >= 0) {
         throw new InvalidKeyException("exponent is larger than modulus");
      } else if (this.e.compareTo(THREE) < 0) {
         throw new InvalidKeyException("exponent is smaller than 3");
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

   protected void parseKeyBits() throws InvalidKeyException {
      try {
         DerInputStream var1 = new DerInputStream(this.getKey().toByteArray());
         DerValue var2 = var1.getDerValue();
         if (var2.tag != 48) {
            throw new IOException("Not a SEQUENCE");
         } else {
            DerInputStream var3 = var2.data;
            this.n = var3.getPositiveBigInteger();
            this.e = var3.getPositiveBigInteger();
            if (var2.data.available() != 0) {
               throw new IOException("Extra data available");
            }
         }
      } catch (IOException var4) {
         throw new InvalidKeyException("Invalid RSA public key", var4);
      }
   }

   public String toString() {
      return "Sun RSA public key, " + this.n.bitLength() + " bits\n  modulus: " + this.n + "\n  public exponent: " + this.e;
   }

   protected Object writeReplace() throws ObjectStreamException {
      return new KeyRep(KeyRep.Type.PUBLIC, this.getAlgorithm(), this.getFormat(), this.getEncoded());
   }
}
