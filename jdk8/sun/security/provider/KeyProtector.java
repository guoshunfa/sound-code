package sun.security.provider;

import java.io.IOException;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Arrays;
import sun.security.pkcs.EncryptedPrivateKeyInfo;
import sun.security.pkcs.PKCS8Key;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;

final class KeyProtector {
   private static final int SALT_LEN = 20;
   private static final String DIGEST_ALG = "SHA";
   private static final int DIGEST_LEN = 20;
   private static final String KEY_PROTECTOR_OID = "1.3.6.1.4.1.42.2.17.1.1";
   private byte[] passwdBytes;
   private MessageDigest md;

   public KeyProtector(char[] var1) throws NoSuchAlgorithmException {
      if (var1 == null) {
         throw new IllegalArgumentException("password can't be null");
      } else {
         this.md = MessageDigest.getInstance("SHA");
         this.passwdBytes = new byte[var1.length * 2];
         int var2 = 0;

         for(int var3 = 0; var2 < var1.length; ++var2) {
            this.passwdBytes[var3++] = (byte)(var1[var2] >> 8);
            this.passwdBytes[var3++] = (byte)var1[var2];
         }

      }
   }

   protected void finalize() {
      if (this.passwdBytes != null) {
         Arrays.fill((byte[])this.passwdBytes, (byte)0);
         this.passwdBytes = null;
      }

   }

   public byte[] protect(Key var1) throws KeyStoreException {
      byte var6 = 0;
      if (var1 == null) {
         throw new IllegalArgumentException("plaintext key can't be null");
      } else if (!"PKCS#8".equalsIgnoreCase(var1.getFormat())) {
         throw new KeyStoreException("Cannot get key bytes, not PKCS#8 encoded");
      } else {
         byte[] var7 = var1.getEncoded();
         if (var7 == null) {
            throw new KeyStoreException("Cannot get key bytes, encoding not supported");
         } else {
            int var3 = var7.length / 20;
            if (var7.length % 20 != 0) {
               ++var3;
            }

            byte[] var8 = new byte[20];
            java.security.SecureRandom var9 = new java.security.SecureRandom();
            var9.nextBytes(var8);
            byte[] var10 = new byte[var7.length];
            int var2 = 0;
            int var5 = 0;

            byte[] var4;
            for(var4 = var8; var2 < var3; var5 += 20) {
               this.md.update(this.passwdBytes);
               this.md.update(var4);
               var4 = this.md.digest();
               this.md.reset();
               if (var2 < var3 - 1) {
                  System.arraycopy(var4, 0, var10, var5, var4.length);
               } else {
                  System.arraycopy(var4, 0, var10, var5, var10.length - var5);
               }

               ++var2;
            }

            byte[] var11 = new byte[var7.length];

            for(var2 = 0; var2 < var11.length; ++var2) {
               var11[var2] = (byte)(var7[var2] ^ var10[var2]);
            }

            byte[] var12 = new byte[var8.length + var11.length + 20];
            System.arraycopy(var8, 0, var12, var6, var8.length);
            int var16 = var6 + var8.length;
            System.arraycopy(var11, 0, var12, var16, var11.length);
            var16 += var11.length;
            this.md.update(this.passwdBytes);
            Arrays.fill((byte[])this.passwdBytes, (byte)0);
            this.passwdBytes = null;
            this.md.update(var7);
            var4 = this.md.digest();
            this.md.reset();
            System.arraycopy(var4, 0, var12, var16, var4.length);

            try {
               AlgorithmId var13 = new AlgorithmId(new ObjectIdentifier("1.3.6.1.4.1.42.2.17.1.1"));
               return (new EncryptedPrivateKeyInfo(var13, var12)).getEncoded();
            } catch (IOException var15) {
               throw new KeyStoreException(var15.getMessage());
            }
         }
      }
   }

   public Key recover(EncryptedPrivateKeyInfo var1) throws UnrecoverableKeyException {
      AlgorithmId var7 = var1.getAlgorithm();
      if (!var7.getOID().toString().equals("1.3.6.1.4.1.42.2.17.1.1")) {
         throw new UnrecoverableKeyException("Unsupported key protection algorithm");
      } else {
         byte[] var8 = var1.getEncryptedData();
         byte[] var9 = new byte[20];
         System.arraycopy(var8, 0, var9, 0, 20);
         int var6 = var8.length - 20 - 20;
         int var4 = var6 / 20;
         if (var6 % 20 != 0) {
            ++var4;
         }

         byte[] var10 = new byte[var6];
         System.arraycopy(var8, 20, var10, 0, var6);
         byte[] var11 = new byte[var10.length];
         int var2 = 0;
         int var5 = 0;

         byte[] var3;
         for(var3 = var9; var2 < var4; var5 += 20) {
            this.md.update(this.passwdBytes);
            this.md.update(var3);
            var3 = this.md.digest();
            this.md.reset();
            if (var2 < var4 - 1) {
               System.arraycopy(var3, 0, var11, var5, var3.length);
            } else {
               System.arraycopy(var3, 0, var11, var5, var11.length - var5);
            }

            ++var2;
         }

         byte[] var12 = new byte[var10.length];

         for(var2 = 0; var2 < var12.length; ++var2) {
            var12[var2] = (byte)(var10[var2] ^ var11[var2]);
         }

         this.md.update(this.passwdBytes);
         Arrays.fill((byte[])this.passwdBytes, (byte)0);
         this.passwdBytes = null;
         this.md.update(var12);
         var3 = this.md.digest();
         this.md.reset();

         for(var2 = 0; var2 < var3.length; ++var2) {
            if (var3[var2] != var8[20 + var6 + var2]) {
               throw new UnrecoverableKeyException("Cannot recover key");
            }
         }

         try {
            return PKCS8Key.parseKey(new DerValue(var12));
         } catch (IOException var14) {
            throw new UnrecoverableKeyException(var14.getMessage());
         }
      }
   }
}
