package sun.security.rsa;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import javax.crypto.BadPaddingException;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;

public abstract class RSASignature extends SignatureSpi {
   private static final int baseLength = 8;
   private final ObjectIdentifier digestOID;
   private final int encodedLength;
   private final MessageDigest md;
   private boolean digestReset;
   private RSAPrivateKey privateKey;
   private RSAPublicKey publicKey;
   private RSAPadding padding;

   RSASignature(String var1, ObjectIdentifier var2, int var3) {
      this.digestOID = var2;

      try {
         this.md = MessageDigest.getInstance(var1);
      } catch (NoSuchAlgorithmException var5) {
         throw new ProviderException(var5);
      }

      this.digestReset = true;
      this.encodedLength = 8 + var3 + this.md.getDigestLength();
   }

   protected void engineInitVerify(PublicKey var1) throws InvalidKeyException {
      RSAPublicKey var2 = (RSAPublicKey)RSAKeyFactory.toRSAKey(var1);
      this.privateKey = null;
      this.publicKey = var2;
      this.initCommon(var2, (SecureRandom)null);
   }

   protected void engineInitSign(PrivateKey var1) throws InvalidKeyException {
      this.engineInitSign(var1, (SecureRandom)null);
   }

   protected void engineInitSign(PrivateKey var1, SecureRandom var2) throws InvalidKeyException {
      RSAPrivateKey var3 = (RSAPrivateKey)RSAKeyFactory.toRSAKey(var1);
      this.privateKey = var3;
      this.publicKey = null;
      this.initCommon(var3, var2);
   }

   private void initCommon(RSAKey var1, SecureRandom var2) throws InvalidKeyException {
      this.resetDigest();
      int var3 = RSACore.getByteLength(var1);

      try {
         this.padding = RSAPadding.getInstance(1, var3, var2);
      } catch (InvalidAlgorithmParameterException var5) {
         throw new InvalidKeyException(var5.getMessage());
      }

      int var4 = this.padding.getMaxDataSize();
      if (this.encodedLength > var4) {
         throw new InvalidKeyException("Key is too short for this signature algorithm");
      }
   }

   private void resetDigest() {
      if (!this.digestReset) {
         this.md.reset();
         this.digestReset = true;
      }

   }

   private byte[] getDigestValue() {
      this.digestReset = true;
      return this.md.digest();
   }

   protected void engineUpdate(byte var1) throws SignatureException {
      this.md.update(var1);
      this.digestReset = false;
   }

   protected void engineUpdate(byte[] var1, int var2, int var3) throws SignatureException {
      this.md.update(var1, var2, var3);
      this.digestReset = false;
   }

   protected void engineUpdate(ByteBuffer var1) {
      this.md.update(var1);
      this.digestReset = false;
   }

   protected byte[] engineSign() throws SignatureException {
      byte[] var1 = this.getDigestValue();

      try {
         byte[] var2 = encodeSignature(this.digestOID, var1);
         byte[] var3 = this.padding.pad(var2);
         byte[] var4 = RSACore.rsa(var3, this.privateKey, true);
         return var4;
      } catch (GeneralSecurityException var5) {
         throw new SignatureException("Could not sign data", var5);
      } catch (IOException var6) {
         throw new SignatureException("Could not encode data", var6);
      }
   }

   protected boolean engineVerify(byte[] var1) throws SignatureException {
      if (var1.length != RSACore.getByteLength((RSAKey)this.publicKey)) {
         throw new SignatureException("Signature length not correct: got " + var1.length + " but was expecting " + RSACore.getByteLength((RSAKey)this.publicKey));
      } else {
         byte[] var2 = this.getDigestValue();

         try {
            byte[] var3 = RSACore.rsa(var1, this.publicKey);
            byte[] var4 = this.padding.unpad(var3);
            byte[] var5 = decodeSignature(this.digestOID, var4);
            return MessageDigest.isEqual(var2, var5);
         } catch (BadPaddingException var6) {
            return false;
         } catch (IOException var7) {
            throw new SignatureException("Signature encoding error", var7);
         }
      }
   }

   public static byte[] encodeSignature(ObjectIdentifier var0, byte[] var1) throws IOException {
      DerOutputStream var2 = new DerOutputStream();
      (new AlgorithmId(var0)).encode(var2);
      var2.putOctetString(var1);
      DerValue var3 = new DerValue((byte)48, var2.toByteArray());
      return var3.toByteArray();
   }

   public static byte[] decodeSignature(ObjectIdentifier var0, byte[] var1) throws IOException {
      DerInputStream var2 = new DerInputStream(var1, 0, var1.length, false);
      DerValue[] var3 = var2.getSequence(2);
      if (var3.length == 2 && var2.available() == 0) {
         AlgorithmId var4 = AlgorithmId.parse(var3[0]);
         if (!var4.getOID().equals((Object)var0)) {
            throw new IOException("ObjectIdentifier mismatch: " + var4.getOID());
         } else if (var4.getEncodedParams() != null) {
            throw new IOException("Unexpected AlgorithmId parameters");
         } else {
            byte[] var5 = var3[1].getOctetString();
            return var5;
         }
      } else {
         throw new IOException("SEQUENCE length error");
      }
   }

   /** @deprecated */
   @Deprecated
   protected void engineSetParameter(String var1, Object var2) throws InvalidParameterException {
      throw new UnsupportedOperationException("setParameter() not supported");
   }

   /** @deprecated */
   @Deprecated
   protected Object engineGetParameter(String var1) throws InvalidParameterException {
      throw new UnsupportedOperationException("getParameter() not supported");
   }

   public static final class SHA512withRSA extends RSASignature {
      public SHA512withRSA() {
         super("SHA-512", AlgorithmId.SHA512_oid, 11);
      }
   }

   public static final class SHA384withRSA extends RSASignature {
      public SHA384withRSA() {
         super("SHA-384", AlgorithmId.SHA384_oid, 11);
      }
   }

   public static final class SHA256withRSA extends RSASignature {
      public SHA256withRSA() {
         super("SHA-256", AlgorithmId.SHA256_oid, 11);
      }
   }

   public static final class SHA224withRSA extends RSASignature {
      public SHA224withRSA() {
         super("SHA-224", AlgorithmId.SHA224_oid, 11);
      }
   }

   public static final class SHA1withRSA extends RSASignature {
      public SHA1withRSA() {
         super("SHA-1", AlgorithmId.SHA_oid, 7);
      }
   }

   public static final class MD5withRSA extends RSASignature {
      public MD5withRSA() {
         super("MD5", AlgorithmId.MD5_oid, 10);
      }
   }

   public static final class MD2withRSA extends RSASignature {
      public MD2withRSA() {
         super("MD2", AlgorithmId.MD2_oid, 10);
      }
   }
}
