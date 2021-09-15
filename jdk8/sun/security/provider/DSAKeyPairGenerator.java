package sun.security.provider;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.interfaces.DSAParams;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAParameterSpec;
import sun.security.jca.JCAUtil;
import sun.security.util.SecurityProviderConstants;

class DSAKeyPairGenerator extends KeyPairGenerator {
   private int plen;
   private int qlen;
   boolean forceNewParameters;
   private DSAParameterSpec params;
   private java.security.SecureRandom random;

   DSAKeyPairGenerator(int var1) {
      super("DSA");
      this.initialize(var1, (java.security.SecureRandom)null);
   }

   private static void checkStrength(int var0, int var1) {
      if ((var0 < 512 || var0 > 1024 || var0 % 64 != 0 || var1 != 160) && (var0 != 2048 || var1 != 224 && var1 != 256) && (var0 != 3072 || var1 != 256)) {
         throw new InvalidParameterException("Unsupported prime and subprime size combination: " + var0 + ", " + var1);
      }
   }

   public void initialize(int var1, java.security.SecureRandom var2) {
      this.init(var1, var2, false);
   }

   public void initialize(AlgorithmParameterSpec var1, java.security.SecureRandom var2) throws InvalidAlgorithmParameterException {
      if (!(var1 instanceof DSAParameterSpec)) {
         throw new InvalidAlgorithmParameterException("Inappropriate parameter");
      } else {
         this.init((DSAParameterSpec)var1, var2, false);
      }
   }

   void init(int var1, java.security.SecureRandom var2, boolean var3) {
      int var4 = SecurityProviderConstants.getDefDSASubprimeSize(var1);
      checkStrength(var1, var4);
      this.plen = var1;
      this.qlen = var4;
      this.params = null;
      this.random = var2;
      this.forceNewParameters = var3;
   }

   void init(DSAParameterSpec var1, java.security.SecureRandom var2, boolean var3) {
      int var4 = var1.getP().bitLength();
      int var5 = var1.getQ().bitLength();
      checkStrength(var4, var5);
      this.plen = var4;
      this.qlen = var5;
      this.params = var1;
      this.random = var2;
      this.forceNewParameters = var3;
   }

   public KeyPair generateKeyPair() {
      if (this.random == null) {
         this.random = JCAUtil.getSecureRandom();
      }

      DSAParameterSpec var1;
      try {
         if (this.forceNewParameters) {
            var1 = ParameterCache.getNewDSAParameterSpec(this.plen, this.qlen, this.random);
         } else {
            if (this.params == null) {
               this.params = ParameterCache.getDSAParameterSpec(this.plen, this.qlen, this.random);
            }

            var1 = this.params;
         }
      } catch (GeneralSecurityException var3) {
         throw new ProviderException(var3);
      }

      return this.generateKeyPair(var1.getP(), var1.getQ(), var1.getG(), this.random);
   }

   private KeyPair generateKeyPair(BigInteger var1, BigInteger var2, BigInteger var3, java.security.SecureRandom var4) {
      BigInteger var5 = this.generateX(var4, var2);
      BigInteger var6 = this.generateY(var5, var1, var3);

      try {
         Object var7;
         if (DSAKeyFactory.SERIAL_INTEROP) {
            var7 = new DSAPublicKey(var6, var1, var2, var3);
         } else {
            var7 = new DSAPublicKeyImpl(var6, var1, var2, var3);
         }

         DSAPrivateKey var8 = new DSAPrivateKey(var5, var1, var2, var3);
         KeyPair var9 = new KeyPair((PublicKey)var7, var8);
         return var9;
      } catch (InvalidKeyException var10) {
         throw new ProviderException(var10);
      }
   }

   private BigInteger generateX(java.security.SecureRandom var1, BigInteger var2) {
      BigInteger var3 = null;
      byte[] var4 = new byte[this.qlen];

      do {
         do {
            var1.nextBytes(var4);
            var3 = (new BigInteger(1, var4)).mod(var2);
         } while(var3.signum() <= 0);
      } while(var3.compareTo(var2) >= 0);

      return var3;
   }

   BigInteger generateY(BigInteger var1, BigInteger var2, BigInteger var3) {
      BigInteger var4 = var3.modPow(var1, var2);
      return var4;
   }

   public static final class Legacy extends DSAKeyPairGenerator implements java.security.interfaces.DSAKeyPairGenerator {
      public Legacy() {
         super(1024);
      }

      public void initialize(int var1, boolean var2, java.security.SecureRandom var3) throws InvalidParameterException {
         if (var2) {
            super.init(var1, var3, true);
         } else {
            DSAParameterSpec var4 = ParameterCache.getCachedDSAParameterSpec(var1, SecurityProviderConstants.getDefDSASubprimeSize(var1));
            if (var4 == null) {
               throw new InvalidParameterException("No precomputed parameters for requested modulus size available");
            }

            super.init(var4, var3, false);
         }

      }

      public void initialize(DSAParams var1, java.security.SecureRandom var2) throws InvalidParameterException {
         if (var1 == null) {
            throw new InvalidParameterException("Params must not be null");
         } else {
            DSAParameterSpec var3 = new DSAParameterSpec(var1.getP(), var1.getQ(), var1.getG());
            super.init(var3, var2, false);
         }
      }
   }

   public static final class Current extends DSAKeyPairGenerator {
      public Current() {
         super(SecurityProviderConstants.DEF_DSA_KEY_SIZE);
      }
   }
}
