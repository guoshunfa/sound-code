package sun.security.provider;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.DigestException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.interfaces.DSAParams;
import java.util.Arrays;
import sun.security.jca.JCAUtil;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

abstract class DSA extends SignatureSpi {
   private static final boolean debug = false;
   private static final int BLINDING_BITS = 7;
   private static final BigInteger BLINDING_CONSTANT = BigInteger.valueOf(128L);
   private DSAParams params;
   private BigInteger presetP;
   private BigInteger presetQ;
   private BigInteger presetG;
   private BigInteger presetY;
   private BigInteger presetX;
   private java.security.SecureRandom signingRandom;
   private final MessageDigest md;

   DSA(MessageDigest var1) {
      this.md = var1;
   }

   private static void checkKey(DSAParams var0, int var1, String var2) throws InvalidKeyException {
      int var3 = var0.getQ().bitLength();
      if (var3 > var1) {
         throw new InvalidKeyException("The security strength of " + var2 + " digest algorithm is not sufficient for this key size");
      }
   }

   protected void engineInitSign(PrivateKey var1) throws InvalidKeyException {
      if (!(var1 instanceof java.security.interfaces.DSAPrivateKey)) {
         throw new InvalidKeyException("not a DSA private key: " + var1);
      } else {
         java.security.interfaces.DSAPrivateKey var2 = (java.security.interfaces.DSAPrivateKey)var1;
         DSAParams var3 = var2.getParams();
         if (var3 == null) {
            throw new InvalidKeyException("DSA private key lacks parameters");
         } else {
            if (this.md.getAlgorithm() != "NullDigest20") {
               checkKey(var3, this.md.getDigestLength() * 8, this.md.getAlgorithm());
            }

            this.params = var3;
            this.presetX = var2.getX();
            this.presetY = null;
            this.presetP = var3.getP();
            this.presetQ = var3.getQ();
            this.presetG = var3.getG();
            this.md.reset();
         }
      }
   }

   protected void engineInitVerify(PublicKey var1) throws InvalidKeyException {
      if (!(var1 instanceof java.security.interfaces.DSAPublicKey)) {
         throw new InvalidKeyException("not a DSA public key: " + var1);
      } else {
         java.security.interfaces.DSAPublicKey var2 = (java.security.interfaces.DSAPublicKey)var1;
         DSAParams var3 = var2.getParams();
         if (var3 == null) {
            throw new InvalidKeyException("DSA public key lacks parameters");
         } else {
            this.params = var3;
            this.presetY = var2.getY();
            this.presetX = null;
            this.presetP = var3.getP();
            this.presetQ = var3.getQ();
            this.presetG = var3.getG();
            this.md.reset();
         }
      }
   }

   protected void engineUpdate(byte var1) {
      this.md.update(var1);
   }

   protected void engineUpdate(byte[] var1, int var2, int var3) {
      this.md.update(var1, var2, var3);
   }

   protected void engineUpdate(ByteBuffer var1) {
      this.md.update(var1);
   }

   protected byte[] engineSign() throws SignatureException {
      BigInteger var1 = this.generateK(this.presetQ);
      BigInteger var2 = this.generateR(this.presetP, this.presetQ, this.presetG, var1);
      BigInteger var3 = this.generateS(this.presetX, this.presetQ, var2, var1);

      try {
         DerOutputStream var4 = new DerOutputStream(100);
         var4.putInteger(var2);
         var4.putInteger(var3);
         DerValue var5 = new DerValue((byte)48, var4.toByteArray());
         return var5.toByteArray();
      } catch (IOException var6) {
         throw new SignatureException("error encoding signature");
      }
   }

   protected boolean engineVerify(byte[] var1) throws SignatureException {
      return this.engineVerify(var1, 0, var1.length);
   }

   protected boolean engineVerify(byte[] var1, int var2, int var3) throws SignatureException {
      BigInteger var4 = null;
      BigInteger var5 = null;

      try {
         DerInputStream var6 = new DerInputStream(var1, var2, var3, false);
         DerValue[] var7 = var6.getSequence(2);
         if (var7.length != 2 || var6.available() != 0) {
            throw new IOException("Invalid encoding for signature");
         }

         var4 = var7[0].getBigInteger();
         var5 = var7[1].getBigInteger();
      } catch (IOException var8) {
         throw new SignatureException("Invalid encoding for signature", var8);
      }

      if (var4.signum() < 0) {
         var4 = new BigInteger(1, var4.toByteArray());
      }

      if (var5.signum() < 0) {
         var5 = new BigInteger(1, var5.toByteArray());
      }

      if (var4.compareTo(this.presetQ) == -1 && var5.compareTo(this.presetQ) == -1) {
         BigInteger var9 = this.generateW(this.presetP, this.presetQ, this.presetG, var5);
         BigInteger var10 = this.generateV(this.presetY, this.presetP, this.presetQ, this.presetG, var9, var4);
         return var10.equals(var4);
      } else {
         throw new SignatureException("invalid signature: out of range values");
      }
   }

   /** @deprecated */
   @Deprecated
   protected void engineSetParameter(String var1, Object var2) {
      throw new InvalidParameterException("No parameter accepted");
   }

   /** @deprecated */
   @Deprecated
   protected Object engineGetParameter(String var1) {
      return null;
   }

   private BigInteger generateR(BigInteger var1, BigInteger var2, BigInteger var3, BigInteger var4) {
      java.security.SecureRandom var5 = this.getSigningRandom();
      BigInteger var6 = new BigInteger(7, var5);
      var6 = var6.add(BLINDING_CONSTANT);
      var4 = var4.add(var2.multiply(var6));
      BigInteger var7 = var3.modPow(var4, var1);
      return var7.mod(var2);
   }

   private BigInteger generateS(BigInteger var1, BigInteger var2, BigInteger var3, BigInteger var4) throws SignatureException {
      byte[] var5;
      try {
         var5 = this.md.digest();
      } catch (RuntimeException var9) {
         throw new SignatureException(var9.getMessage());
      }

      int var6 = var2.bitLength() / 8;
      if (var6 < var5.length) {
         var5 = Arrays.copyOfRange((byte[])var5, 0, var6);
      }

      BigInteger var7 = new BigInteger(1, var5);
      BigInteger var8 = var4.modInverse(var2);
      return var1.multiply(var3).add(var7).multiply(var8).mod(var2);
   }

   private BigInteger generateW(BigInteger var1, BigInteger var2, BigInteger var3, BigInteger var4) {
      return var4.modInverse(var2);
   }

   private BigInteger generateV(BigInteger var1, BigInteger var2, BigInteger var3, BigInteger var4, BigInteger var5, BigInteger var6) throws SignatureException {
      byte[] var7;
      try {
         var7 = this.md.digest();
      } catch (RuntimeException var16) {
         throw new SignatureException(var16.getMessage());
      }

      int var8 = var3.bitLength() / 8;
      if (var8 < var7.length) {
         var7 = Arrays.copyOfRange((byte[])var7, 0, var8);
      }

      BigInteger var9 = new BigInteger(1, var7);
      BigInteger var10 = var9.multiply(var5).mod(var3);
      BigInteger var11 = var6.multiply(var5).mod(var3);
      BigInteger var12 = var4.modPow(var10, var2);
      BigInteger var13 = var1.modPow(var11, var2);
      BigInteger var14 = var12.multiply(var13);
      BigInteger var15 = var14.mod(var2);
      return var15.mod(var3);
   }

   protected BigInteger generateK(BigInteger var1) {
      java.security.SecureRandom var2 = this.getSigningRandom();
      byte[] var3 = new byte[(var1.bitLength() + 7) / 8 + 8];
      var2.nextBytes(var3);
      return (new BigInteger(1, var3)).mod(var1.subtract(BigInteger.ONE)).add(BigInteger.ONE);
   }

   protected java.security.SecureRandom getSigningRandom() {
      if (this.signingRandom == null) {
         if (this.appRandom != null) {
            this.signingRandom = this.appRandom;
         } else {
            this.signingRandom = JCAUtil.getSecureRandom();
         }
      }

      return this.signingRandom;
   }

   public String toString() {
      String var1 = "DSA Signature";
      if (this.presetP != null && this.presetQ != null && this.presetG != null) {
         var1 = var1 + "\n\tp: " + Debug.toHexString(this.presetP);
         var1 = var1 + "\n\tq: " + Debug.toHexString(this.presetQ);
         var1 = var1 + "\n\tg: " + Debug.toHexString(this.presetG);
      } else {
         var1 = var1 + "\n\t P, Q or G not initialized.";
      }

      if (this.presetY != null) {
         var1 = var1 + "\n\ty: " + Debug.toHexString(this.presetY);
      }

      if (this.presetY == null && this.presetX == null) {
         var1 = var1 + "\n\tUNINIIALIZED";
      }

      return var1;
   }

   private static void debug(Exception var0) {
   }

   private static void debug(String var0) {
   }

   public static final class RawDSA extends DSA {
      public RawDSA() throws NoSuchAlgorithmException {
         super(new DSA.RawDSA.NullDigest20());
      }

      public static final class NullDigest20 extends MessageDigest {
         private final byte[] digestBuffer = new byte[20];
         private int ofs = 0;

         protected NullDigest20() {
            super("NullDigest20");
         }

         protected void engineUpdate(byte var1) {
            if (this.ofs == this.digestBuffer.length) {
               this.ofs = Integer.MAX_VALUE;
            } else {
               this.digestBuffer[this.ofs++] = var1;
            }

         }

         protected void engineUpdate(byte[] var1, int var2, int var3) {
            if (this.ofs + var3 > this.digestBuffer.length) {
               this.ofs = Integer.MAX_VALUE;
            } else {
               System.arraycopy(var1, var2, this.digestBuffer, this.ofs, var3);
               this.ofs += var3;
            }

         }

         protected final void engineUpdate(ByteBuffer var1) {
            int var2 = var1.remaining();
            if (this.ofs + var2 > this.digestBuffer.length) {
               this.ofs = Integer.MAX_VALUE;
            } else {
               var1.get(this.digestBuffer, this.ofs, var2);
               this.ofs += var2;
            }

         }

         protected byte[] engineDigest() throws RuntimeException {
            if (this.ofs != this.digestBuffer.length) {
               throw new RuntimeException("Data for RawDSA must be exactly 20 bytes long");
            } else {
               this.reset();
               return this.digestBuffer;
            }
         }

         protected int engineDigest(byte[] var1, int var2, int var3) throws DigestException {
            if (this.ofs != this.digestBuffer.length) {
               throw new DigestException("Data for RawDSA must be exactly 20 bytes long");
            } else if (var3 < this.digestBuffer.length) {
               throw new DigestException("Output buffer too small; must be at least 20 bytes");
            } else {
               System.arraycopy(this.digestBuffer, 0, var1, var2, this.digestBuffer.length);
               this.reset();
               return this.digestBuffer.length;
            }
         }

         protected void engineReset() {
            this.ofs = 0;
         }

         protected final int engineGetDigestLength() {
            return this.digestBuffer.length;
         }
      }
   }

   public static final class SHA1withDSA extends DSA {
      public SHA1withDSA() throws NoSuchAlgorithmException {
         super(MessageDigest.getInstance("SHA-1"));
      }
   }

   public static final class SHA256withDSA extends DSA {
      public SHA256withDSA() throws NoSuchAlgorithmException {
         super(MessageDigest.getInstance("SHA-256"));
      }
   }

   public static final class SHA224withDSA extends DSA {
      public SHA224withDSA() throws NoSuchAlgorithmException {
         super(MessageDigest.getInstance("SHA-224"));
      }
   }
}
