package sun.security.rsa;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.interfaces.RSAPrivateKey;
import sun.security.pkcs.PKCS8Key;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public final class RSAPrivateKeyImpl extends PKCS8Key implements RSAPrivateKey {
   private static final long serialVersionUID = -33106691987952810L;
   private final BigInteger n;
   private final BigInteger d;

   RSAPrivateKeyImpl(BigInteger var1, BigInteger var2) throws InvalidKeyException {
      this.n = var1;
      this.d = var2;
      RSAKeyFactory.checkRSAProviderKeyLengths(var1.bitLength(), (BigInteger)null);
      this.algid = RSAPrivateCrtKeyImpl.rsaId;

      try {
         DerOutputStream var3 = new DerOutputStream();
         var3.putInteger(0);
         var3.putInteger(var1);
         var3.putInteger(0);
         var3.putInteger(var2);
         var3.putInteger(0);
         var3.putInteger(0);
         var3.putInteger(0);
         var3.putInteger(0);
         var3.putInteger(0);
         DerValue var4 = new DerValue((byte)48, var3.toByteArray());
         this.key = var4.toByteArray();
      } catch (IOException var5) {
         throw new InvalidKeyException(var5);
      }
   }

   public String getAlgorithm() {
      return "RSA";
   }

   public BigInteger getModulus() {
      return this.n;
   }

   public BigInteger getPrivateExponent() {
      return this.d;
   }
}
