package sun.security.krb5.internal.crypto;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import javax.crypto.spec.DESKeySpec;
import sun.security.krb5.Confounder;
import sun.security.krb5.KrbCryptoException;

public final class RsaMd5DesCksumType extends CksumType {
   public int confounderSize() {
      return 8;
   }

   public int cksumType() {
      return 8;
   }

   public boolean isSafe() {
      return true;
   }

   public int cksumSize() {
      return 24;
   }

   public int keyType() {
      return 1;
   }

   public int keySize() {
      return 8;
   }

   public byte[] calculateKeyedChecksum(byte[] var1, int var2, byte[] var3, int var4) throws KrbCryptoException {
      byte[] var5 = new byte[var2 + this.confounderSize()];
      byte[] var6 = Confounder.bytes(this.confounderSize());
      System.arraycopy(var6, 0, var5, 0, this.confounderSize());
      System.arraycopy(var1, 0, var5, this.confounderSize(), var2);
      byte[] var7 = this.calculateChecksum(var5, var5.length);
      byte[] var8 = new byte[this.cksumSize()];
      System.arraycopy(var6, 0, var8, 0, this.confounderSize());
      System.arraycopy(var7, 0, var8, this.confounderSize(), this.cksumSize() - this.confounderSize());
      byte[] var9 = new byte[this.keySize()];
      System.arraycopy(var3, 0, var9, 0, var3.length);

      for(int var10 = 0; var10 < var9.length; ++var10) {
         var9[var10] = (byte)(var9[var10] ^ 240);
      }

      try {
         if (DESKeySpec.isWeak(var9, 0)) {
            var9[7] = (byte)(var9[7] ^ 240);
         }
      } catch (InvalidKeyException var12) {
      }

      byte[] var13 = new byte[var9.length];
      byte[] var11 = new byte[var8.length];
      Des.cbc_encrypt(var8, var11, var9, var13, true);
      return var11;
   }

   public boolean verifyKeyedChecksum(byte[] var1, int var2, byte[] var3, byte[] var4, int var5) throws KrbCryptoException {
      byte[] var6 = this.decryptKeyedChecksum(var4, var3);
      byte[] var7 = new byte[var2 + this.confounderSize()];
      System.arraycopy(var6, 0, var7, 0, this.confounderSize());
      System.arraycopy(var1, 0, var7, this.confounderSize(), var2);
      byte[] var8 = this.calculateChecksum(var7, var7.length);
      byte[] var9 = new byte[this.cksumSize() - this.confounderSize()];
      System.arraycopy(var6, this.confounderSize(), var9, 0, this.cksumSize() - this.confounderSize());
      return isChecksumEqual(var9, var8);
   }

   private byte[] decryptKeyedChecksum(byte[] var1, byte[] var2) throws KrbCryptoException {
      byte[] var3 = new byte[this.keySize()];
      System.arraycopy(var2, 0, var3, 0, var2.length);

      for(int var4 = 0; var4 < var3.length; ++var4) {
         var3[var4] = (byte)(var3[var4] ^ 240);
      }

      try {
         if (DESKeySpec.isWeak(var3, 0)) {
            var3[7] = (byte)(var3[7] ^ 240);
         }
      } catch (InvalidKeyException var6) {
      }

      byte[] var7 = new byte[var3.length];
      byte[] var5 = new byte[var1.length];
      Des.cbc_encrypt(var1, var5, var3, var7, false);
      return var5;
   }

   public byte[] calculateChecksum(byte[] var1, int var2) throws KrbCryptoException {
      Object var4 = null;

      MessageDigest var3;
      try {
         var3 = MessageDigest.getInstance("MD5");
      } catch (Exception var7) {
         throw new KrbCryptoException("JCE provider may not be installed. " + var7.getMessage());
      }

      try {
         var3.update(var1);
         byte[] var8 = var3.digest();
         return var8;
      } catch (Exception var6) {
         throw new KrbCryptoException(var6.getMessage());
      }
   }
}
