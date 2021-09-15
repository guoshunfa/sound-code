package sun.security.krb5.internal.crypto;

import java.security.InvalidKeyException;
import javax.crypto.spec.DESKeySpec;
import sun.security.krb5.Confounder;
import sun.security.krb5.KrbCryptoException;

public class DesMacCksumType extends CksumType {
   public int confounderSize() {
      return 8;
   }

   public int cksumType() {
      return 4;
   }

   public boolean isSafe() {
      return true;
   }

   public int cksumSize() {
      return 16;
   }

   public int keyType() {
      return 1;
   }

   public int keySize() {
      return 8;
   }

   public byte[] calculateChecksum(byte[] var1, int var2) {
      return null;
   }

   public byte[] calculateKeyedChecksum(byte[] var1, int var2, byte[] var3, int var4) throws KrbCryptoException {
      byte[] var5 = new byte[var2 + this.confounderSize()];
      byte[] var6 = Confounder.bytes(this.confounderSize());
      System.arraycopy(var6, 0, var5, 0, this.confounderSize());
      System.arraycopy(var1, 0, var5, this.confounderSize(), var2);

      try {
         if (DESKeySpec.isWeak(var3, 0)) {
            var3[7] = (byte)(var3[7] ^ 240);
         }
      } catch (InvalidKeyException var14) {
      }

      byte[] var7 = new byte[var3.length];
      byte[] var8 = Des.des_cksum(var7, var5, var3);
      byte[] var9 = new byte[this.cksumSize()];
      System.arraycopy(var6, 0, var9, 0, this.confounderSize());
      System.arraycopy(var8, 0, var9, this.confounderSize(), this.cksumSize() - this.confounderSize());
      byte[] var10 = new byte[this.keySize()];
      System.arraycopy(var3, 0, var10, 0, var3.length);

      for(int var11 = 0; var11 < var10.length; ++var11) {
         var10[var11] = (byte)(var10[var11] ^ 240);
      }

      try {
         if (DESKeySpec.isWeak(var10, 0)) {
            var10[7] = (byte)(var10[7] ^ 240);
         }
      } catch (InvalidKeyException var13) {
      }

      byte[] var15 = new byte[var10.length];
      byte[] var12 = new byte[var9.length];
      Des.cbc_encrypt(var9, var12, var10, var15, true);
      return var12;
   }

   public boolean verifyKeyedChecksum(byte[] var1, int var2, byte[] var3, byte[] var4, int var5) throws KrbCryptoException {
      byte[] var6 = this.decryptKeyedChecksum(var4, var3);
      byte[] var7 = new byte[var2 + this.confounderSize()];
      System.arraycopy(var6, 0, var7, 0, this.confounderSize());
      System.arraycopy(var1, 0, var7, this.confounderSize(), var2);

      try {
         if (DESKeySpec.isWeak(var3, 0)) {
            var3[7] = (byte)(var3[7] ^ 240);
         }
      } catch (InvalidKeyException var11) {
      }

      byte[] var8 = new byte[var3.length];
      byte[] var9 = Des.des_cksum(var8, var7, var3);
      byte[] var10 = new byte[this.cksumSize() - this.confounderSize()];
      System.arraycopy(var6, this.confounderSize(), var10, 0, this.cksumSize() - this.confounderSize());
      return isChecksumEqual(var10, var9);
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
}
