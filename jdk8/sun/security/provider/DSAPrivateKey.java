package sun.security.provider;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.interfaces.DSAParams;
import java.security.spec.DSAParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import sun.security.pkcs.PKCS8Key;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.x509.AlgIdDSA;

public final class DSAPrivateKey extends PKCS8Key implements java.security.interfaces.DSAPrivateKey, Serializable {
   private static final long serialVersionUID = -3244453684193605938L;
   private BigInteger x;

   public DSAPrivateKey() {
   }

   public DSAPrivateKey(BigInteger var1, BigInteger var2, BigInteger var3, BigInteger var4) throws InvalidKeyException {
      this.x = var1;
      this.algid = new AlgIdDSA(var2, var3, var4);

      try {
         this.key = (new DerValue((byte)2, var1.toByteArray())).toByteArray();
         this.encode();
      } catch (IOException var7) {
         InvalidKeyException var6 = new InvalidKeyException("could not DER encode x: " + var7.getMessage());
         var6.initCause(var7);
         throw var6;
      }
   }

   public DSAPrivateKey(byte[] var1) throws InvalidKeyException {
      this.clearOldKey();
      this.decode(var1);
   }

   public DSAParams getParams() {
      try {
         if (this.algid instanceof DSAParams) {
            return (DSAParams)this.algid;
         } else {
            AlgorithmParameters var2 = this.algid.getParameters();
            if (var2 == null) {
               return null;
            } else {
               DSAParameterSpec var1 = (DSAParameterSpec)var2.getParameterSpec(DSAParameterSpec.class);
               return var1;
            }
         }
      } catch (InvalidParameterSpecException var3) {
         return null;
      }
   }

   public BigInteger getX() {
      return this.x;
   }

   private void clearOldKey() {
      int var1;
      if (this.encodedKey != null) {
         for(var1 = 0; var1 < this.encodedKey.length; ++var1) {
            this.encodedKey[var1] = 0;
         }
      }

      if (this.key != null) {
         for(var1 = 0; var1 < this.key.length; ++var1) {
            this.key[var1] = 0;
         }
      }

   }

   protected void parseKeyBits() throws InvalidKeyException {
      try {
         DerInputStream var1 = new DerInputStream(this.key);
         this.x = var1.getBigInteger();
      } catch (IOException var3) {
         InvalidKeyException var2 = new InvalidKeyException(var3.getMessage());
         var2.initCause(var3);
         throw var2;
      }
   }
}
