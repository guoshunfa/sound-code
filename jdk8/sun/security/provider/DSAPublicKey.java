package sun.security.provider;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.interfaces.DSAParams;
import java.security.spec.DSAParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import sun.security.util.BitArray;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.x509.AlgIdDSA;
import sun.security.x509.X509Key;

public class DSAPublicKey extends X509Key implements java.security.interfaces.DSAPublicKey, Serializable {
   private static final long serialVersionUID = -2994193307391104133L;
   private BigInteger y;

   public DSAPublicKey() {
   }

   public DSAPublicKey(BigInteger var1, BigInteger var2, BigInteger var3, BigInteger var4) throws InvalidKeyException {
      this.y = var1;
      this.algid = new AlgIdDSA(var2, var3, var4);

      try {
         byte[] var5 = (new DerValue((byte)2, var1.toByteArray())).toByteArray();
         this.setKey(new BitArray(var5.length * 8, var5));
         this.encode();
      } catch (IOException var6) {
         throw new InvalidKeyException("could not DER encode y: " + var6.getMessage());
      }
   }

   public DSAPublicKey(byte[] var1) throws InvalidKeyException {
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

   public BigInteger getY() {
      return this.y;
   }

   public String toString() {
      return "Sun DSA Public Key\n    Parameters:" + this.algid + "\n  y:\n" + Debug.toHexString(this.y) + "\n";
   }

   protected void parseKeyBits() throws InvalidKeyException {
      try {
         DerInputStream var1 = new DerInputStream(this.getKey().toByteArray());
         this.y = var1.getBigInteger();
      } catch (IOException var2) {
         throw new InvalidKeyException("Invalid key: y value\n" + var2.getMessage());
      }
   }
}
