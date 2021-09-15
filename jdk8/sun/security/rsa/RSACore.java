package sun.security.rsa;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.WeakHashMap;
import javax.crypto.BadPaddingException;
import sun.security.jca.JCAUtil;

public final class RSACore {
   private static final boolean ENABLE_BLINDING = true;
   private static final Map<BigInteger, RSACore.BlindingParameters> blindingCache = new WeakHashMap();

   private RSACore() {
   }

   public static int getByteLength(BigInteger var0) {
      int var1 = var0.bitLength();
      return var1 + 7 >> 3;
   }

   public static int getByteLength(RSAKey var0) {
      return getByteLength(var0.getModulus());
   }

   public static byte[] convert(byte[] var0, int var1, int var2) {
      if (var1 == 0 && var2 == var0.length) {
         return var0;
      } else {
         byte[] var3 = new byte[var2];
         System.arraycopy(var0, var1, var3, 0, var2);
         return var3;
      }
   }

   public static byte[] rsa(byte[] var0, RSAPublicKey var1) throws BadPaddingException {
      return crypt(var0, var1.getModulus(), var1.getPublicExponent());
   }

   /** @deprecated */
   @Deprecated
   public static byte[] rsa(byte[] var0, RSAPrivateKey var1) throws BadPaddingException {
      return rsa(var0, var1, true);
   }

   public static byte[] rsa(byte[] var0, RSAPrivateKey var1, boolean var2) throws BadPaddingException {
      return var1 instanceof RSAPrivateCrtKey ? crtCrypt(var0, (RSAPrivateCrtKey)var1, var2) : priCrypt(var0, var1.getModulus(), var1.getPrivateExponent());
   }

   private static byte[] crypt(byte[] var0, BigInteger var1, BigInteger var2) throws BadPaddingException {
      BigInteger var3 = parseMsg(var0, var1);
      BigInteger var4 = var3.modPow(var2, var1);
      return toByteArray(var4, getByteLength(var1));
   }

   private static byte[] priCrypt(byte[] var0, BigInteger var1, BigInteger var2) throws BadPaddingException {
      BigInteger var3 = parseMsg(var0, var1);
      RSACore.BlindingRandomPair var4 = null;
      var4 = getBlindingRandomPair((BigInteger)null, var2, var1);
      var3 = var3.multiply(var4.u).mod(var1);
      BigInteger var5 = var3.modPow(var2, var1);
      var5 = var5.multiply(var4.v).mod(var1);
      return toByteArray(var5, getByteLength(var1));
   }

   private static byte[] crtCrypt(byte[] var0, RSAPrivateCrtKey var1, boolean var2) throws BadPaddingException {
      BigInteger var3 = var1.getModulus();
      BigInteger var4 = parseMsg(var0, var3);
      BigInteger var6 = var1.getPrimeP();
      BigInteger var7 = var1.getPrimeQ();
      BigInteger var8 = var1.getPrimeExponentP();
      BigInteger var9 = var1.getPrimeExponentQ();
      BigInteger var10 = var1.getCrtCoefficient();
      BigInteger var11 = var1.getPublicExponent();
      BigInteger var12 = var1.getPrivateExponent();
      RSACore.BlindingRandomPair var13 = getBlindingRandomPair(var11, var12, var3);
      BigInteger var5 = var4.multiply(var13.u).mod(var3);
      BigInteger var14 = var5.modPow(var8, var6);
      BigInteger var15 = var5.modPow(var9, var7);
      BigInteger var16 = var14.subtract(var15);
      if (var16.signum() < 0) {
         var16 = var16.add(var6);
      }

      BigInteger var17 = var16.multiply(var10).mod(var6);
      BigInteger var18 = var17.multiply(var7).add(var15);
      var18 = var18.multiply(var13.v).mod(var3);
      if (var2 && !var4.equals(var18.modPow(var11, var3))) {
         throw new BadPaddingException("RSA private key operation failed");
      } else {
         return toByteArray(var18, getByteLength(var3));
      }
   }

   private static BigInteger parseMsg(byte[] var0, BigInteger var1) throws BadPaddingException {
      BigInteger var2 = new BigInteger(1, var0);
      if (var2.compareTo(var1) >= 0) {
         throw new BadPaddingException("Message is larger than modulus");
      } else {
         return var2;
      }
   }

   private static byte[] toByteArray(BigInteger var0, int var1) {
      byte[] var2 = var0.toByteArray();
      int var3 = var2.length;
      if (var3 == var1) {
         return var2;
      } else {
         byte[] var4;
         if (var3 == var1 + 1 && var2[0] == 0) {
            var4 = new byte[var1];
            System.arraycopy(var2, 1, var4, 0, var1);
            return var4;
         } else {
            assert var3 < var1;

            var4 = new byte[var1];
            System.arraycopy(var2, 0, var4, var1 - var3, var3);
            return var4;
         }
      }
   }

   private static RSACore.BlindingRandomPair getBlindingRandomPair(BigInteger var0, BigInteger var1, BigInteger var2) {
      RSACore.BlindingParameters var3 = null;
      synchronized(blindingCache) {
         var3 = (RSACore.BlindingParameters)blindingCache.get(var2);
      }

      if (var3 == null) {
         var3 = new RSACore.BlindingParameters(var0, var1, var2);
         synchronized(blindingCache) {
            blindingCache.putIfAbsent(var2, var3);
         }
      }

      RSACore.BlindingRandomPair var4 = var3.getBlindingRandomPair(var0, var1, var2);
      if (var4 == null) {
         var3 = new RSACore.BlindingParameters(var0, var1, var2);
         synchronized(blindingCache) {
            blindingCache.replace(var2, var3);
         }

         var4 = var3.getBlindingRandomPair(var0, var1, var2);
      }

      return var4;
   }

   private static final class BlindingParameters {
      private static final BigInteger BIG_TWO = BigInteger.valueOf(2L);
      private final BigInteger e;
      private final BigInteger d;
      private BigInteger u = null;
      private BigInteger v = null;

      BlindingParameters(BigInteger var1, BigInteger var2, BigInteger var3) {
         this.e = var1;
         this.d = var2;
         int var4 = var3.bitLength();
         SecureRandom var5 = JCAUtil.getSecureRandom();
         this.u = (new BigInteger(var4, var5)).mod(var3);
         if (this.u.equals(BigInteger.ZERO)) {
            this.u = BigInteger.ONE;
         }

         try {
            this.v = this.u.modInverse(var3);
         } catch (ArithmeticException var7) {
            this.u = BigInteger.ONE;
            this.v = BigInteger.ONE;
         }

         if (var1 != null) {
            this.u = this.u.modPow(var1, var3);
         } else {
            this.v = this.v.modPow(var2, var3);
         }

      }

      RSACore.BlindingRandomPair getBlindingRandomPair(BigInteger var1, BigInteger var2, BigInteger var3) {
         if (this.e != null && this.e.equals(var1) || this.d != null && this.d.equals(var2)) {
            RSACore.BlindingRandomPair var4 = null;
            synchronized(this) {
               if (!this.u.equals(BigInteger.ZERO) && !this.v.equals(BigInteger.ZERO)) {
                  var4 = new RSACore.BlindingRandomPair(this.u, this.v);
                  if (this.u.compareTo(BigInteger.ONE) > 0 && this.v.compareTo(BigInteger.ONE) > 0) {
                     this.u = this.u.modPow(BIG_TWO, var3);
                     this.v = this.v.modPow(BIG_TWO, var3);
                  } else {
                     this.u = BigInteger.ZERO;
                     this.v = BigInteger.ZERO;
                  }
               }

               return var4;
            }
         } else {
            return null;
         }
      }
   }

   private static final class BlindingRandomPair {
      final BigInteger u;
      final BigInteger v;

      BlindingRandomPair(BigInteger var1, BigInteger var2) {
         this.u = var1;
         this.v = var2;
      }
   }
}
