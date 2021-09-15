package sun.security.pkcs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyRep;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import sun.security.util.Debug;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;

public class PKCS8Key implements PrivateKey {
   private static final long serialVersionUID = -3836890099307167124L;
   protected AlgorithmId algid;
   protected byte[] key;
   protected byte[] encodedKey;
   public static final BigInteger version;

   public PKCS8Key() {
   }

   private PKCS8Key(AlgorithmId var1, byte[] var2) throws InvalidKeyException {
      this.algid = var1;
      this.key = var2;
      this.encode();
   }

   public static PKCS8Key parse(DerValue var0) throws IOException {
      PrivateKey var1 = parseKey(var0);
      if (var1 instanceof PKCS8Key) {
         return (PKCS8Key)var1;
      } else {
         throw new IOException("Provider did not return PKCS8Key");
      }
   }

   public static PrivateKey parseKey(DerValue var0) throws IOException {
      if (var0.tag != 48) {
         throw new IOException("corrupt private key");
      } else {
         BigInteger var3 = var0.data.getBigInteger();
         if (!version.equals(var3)) {
            throw new IOException("version mismatch: (supported: " + Debug.toHexString(version) + ", parsed: " + Debug.toHexString(var3));
         } else {
            AlgorithmId var1 = AlgorithmId.parse(var0.data.getDerValue());

            PrivateKey var2;
            try {
               var2 = buildPKCS8Key(var1, var0.data.getOctetString());
            } catch (InvalidKeyException var5) {
               throw new IOException("corrupt private key");
            }

            if (var0.data.available() != 0) {
               throw new IOException("excess private key");
            } else {
               return var2;
            }
         }
      }
   }

   protected void parseKeyBits() throws IOException, InvalidKeyException {
      this.encode();
   }

   static PrivateKey buildPKCS8Key(AlgorithmId var0, byte[] var1) throws IOException, InvalidKeyException {
      DerOutputStream var2 = new DerOutputStream();
      encode(var2, var0, var1);
      PKCS8EncodedKeySpec var3 = new PKCS8EncodedKeySpec(var2.toByteArray());

      try {
         KeyFactory var18 = KeyFactory.getInstance(var0.getName());
         return var18.generatePrivate(var3);
      } catch (NoSuchAlgorithmException var11) {
      } catch (InvalidKeySpecException var12) {
      }

      String var4 = "";

      try {
         Provider var7 = Security.getProvider("SUN");
         if (var7 == null) {
            throw new InstantiationException();
         }

         var4 = var7.getProperty("PrivateKey.PKCS#8." + var0.getName());
         if (var4 == null) {
            throw new InstantiationException();
         }

         Class var8 = null;

         try {
            var8 = Class.forName(var4);
         } catch (ClassNotFoundException var13) {
            ClassLoader var10 = ClassLoader.getSystemClassLoader();
            if (var10 != null) {
               var8 = var10.loadClass(var4);
            }
         }

         Object var9 = null;
         if (var8 != null) {
            var9 = var8.newInstance();
         }

         if (var9 instanceof PKCS8Key) {
            PKCS8Key var17 = (PKCS8Key)var9;
            var17.algid = var0;
            var17.key = var1;
            var17.parseKeyBits();
            return var17;
         }
      } catch (ClassNotFoundException var14) {
      } catch (InstantiationException var15) {
      } catch (IllegalAccessException var16) {
         throw new IOException(var4 + " [internal error]");
      }

      PKCS8Key var5 = new PKCS8Key();
      var5.algid = var0;
      var5.key = var1;
      return var5;
   }

   public String getAlgorithm() {
      return this.algid.getName();
   }

   public AlgorithmId getAlgorithmId() {
      return this.algid;
   }

   public final void encode(DerOutputStream var1) throws IOException {
      encode(var1, this.algid, this.key);
   }

   public synchronized byte[] getEncoded() {
      byte[] var1 = null;

      try {
         var1 = this.encode();
      } catch (InvalidKeyException var3) {
      }

      return var1;
   }

   public String getFormat() {
      return "PKCS#8";
   }

   public byte[] encode() throws InvalidKeyException {
      if (this.encodedKey == null) {
         try {
            DerOutputStream var1 = new DerOutputStream();
            this.encode(var1);
            this.encodedKey = var1.toByteArray();
         } catch (IOException var2) {
            throw new InvalidKeyException("IOException : " + var2.getMessage());
         }
      }

      return (byte[])this.encodedKey.clone();
   }

   public void decode(InputStream var1) throws InvalidKeyException {
      try {
         DerValue var2 = new DerValue(var1);
         if (var2.tag != 48) {
            throw new InvalidKeyException("invalid key format");
         } else {
            BigInteger var3 = var2.data.getBigInteger();
            if (!var3.equals(version)) {
               throw new IOException("version mismatch: (supported: " + Debug.toHexString(version) + ", parsed: " + Debug.toHexString(var3));
            } else {
               this.algid = AlgorithmId.parse(var2.data.getDerValue());
               this.key = var2.data.getOctetString();
               this.parseKeyBits();
               if (var2.data.available() != 0) {
               }

            }
         }
      } catch (IOException var4) {
         throw new InvalidKeyException("IOException : " + var4.getMessage());
      }
   }

   public void decode(byte[] var1) throws InvalidKeyException {
      this.decode((InputStream)(new ByteArrayInputStream(var1)));
   }

   protected Object writeReplace() throws ObjectStreamException {
      return new KeyRep(KeyRep.Type.PRIVATE, this.getAlgorithm(), this.getFormat(), this.getEncoded());
   }

   private void readObject(ObjectInputStream var1) throws IOException {
      try {
         this.decode((InputStream)var1);
      } catch (InvalidKeyException var3) {
         var3.printStackTrace();
         throw new IOException("deserialized key is invalid: " + var3.getMessage());
      }
   }

   static void encode(DerOutputStream var0, AlgorithmId var1, byte[] var2) throws IOException {
      DerOutputStream var3 = new DerOutputStream();
      var3.putInteger(version);
      var1.encode(var3);
      var3.putOctetString(var2);
      var0.write((byte)48, (DerOutputStream)var3);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof Key) {
         byte[] var2;
         if (this.encodedKey != null) {
            var2 = this.encodedKey;
         } else {
            var2 = this.getEncoded();
         }

         byte[] var3 = ((Key)var1).getEncoded();
         return MessageDigest.isEqual(var2, var3);
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = 0;
      byte[] var2 = this.getEncoded();

      for(int var3 = 1; var3 < var2.length; ++var3) {
         var1 += var2[var3] * var3;
      }

      return var1;
   }

   static {
      version = BigInteger.ZERO;
   }
}
