package sun.security.krb5.internal.crypto;

import sun.security.krb5.Confounder;
import sun.security.krb5.KrbCryptoException;
import sun.security.krb5.internal.KrbApErrException;

abstract class DesCbcEType extends EType {
   protected abstract byte[] calculateChecksum(byte[] var1, int var2) throws KrbCryptoException;

   public int blockSize() {
      return 8;
   }

   public int keyType() {
      return 1;
   }

   public int keySize() {
      return 8;
   }

   public byte[] encrypt(byte[] var1, byte[] var2, int var3) throws KrbCryptoException {
      byte[] var4 = new byte[this.keySize()];
      return this.encrypt(var1, var2, var4, var3);
   }

   public byte[] encrypt(byte[] var1, byte[] var2, byte[] var3, int var4) throws KrbCryptoException {
      if (var2.length > 8) {
         throw new KrbCryptoException("Invalid DES Key!");
      } else {
         int var5 = var1.length + this.confounderSize() + this.checksumSize();
         byte[] var6;
         byte var7;
         if (var5 % this.blockSize() == 0) {
            var6 = new byte[var5 + this.blockSize()];
            var7 = 8;
         } else {
            var6 = new byte[var5 + this.blockSize() - var5 % this.blockSize()];
            var7 = (byte)(this.blockSize() - var5 % this.blockSize());
         }

         for(int var8 = var5; var8 < var6.length; ++var8) {
            var6[var8] = var7;
         }

         byte[] var11 = Confounder.bytes(this.confounderSize());
         System.arraycopy(var11, 0, var6, 0, this.confounderSize());
         System.arraycopy(var1, 0, var6, this.startOfData(), var1.length);
         byte[] var9 = this.calculateChecksum(var6, var6.length);
         System.arraycopy(var9, 0, var6, this.startOfChecksum(), this.checksumSize());
         byte[] var10 = new byte[var6.length];
         Des.cbc_encrypt(var6, var10, var2, var3, true);
         return var10;
      }
   }

   public byte[] decrypt(byte[] var1, byte[] var2, int var3) throws KrbApErrException, KrbCryptoException {
      byte[] var4 = new byte[this.keySize()];
      return this.decrypt(var1, var2, var4, var3);
   }

   public byte[] decrypt(byte[] var1, byte[] var2, byte[] var3, int var4) throws KrbApErrException, KrbCryptoException {
      if (var2.length > 8) {
         throw new KrbCryptoException("Invalid DES Key!");
      } else {
         byte[] var5 = new byte[var1.length];
         Des.cbc_encrypt(var1, var5, var2, var3, false);
         if (!this.isChecksumValid(var5)) {
            throw new KrbApErrException(31);
         } else {
            return var5;
         }
      }
   }

   private void copyChecksumField(byte[] var1, byte[] var2) {
      for(int var3 = 0; var3 < this.checksumSize(); ++var3) {
         var1[this.startOfChecksum() + var3] = var2[var3];
      }

   }

   private byte[] checksumField(byte[] var1) {
      byte[] var2 = new byte[this.checksumSize()];

      for(int var3 = 0; var3 < this.checksumSize(); ++var3) {
         var2[var3] = var1[this.startOfChecksum() + var3];
      }

      return var2;
   }

   private void resetChecksumField(byte[] var1) {
      for(int var2 = this.startOfChecksum(); var2 < this.startOfChecksum() + this.checksumSize(); ++var2) {
         var1[var2] = 0;
      }

   }

   private byte[] generateChecksum(byte[] var1) throws KrbCryptoException {
      byte[] var2 = this.checksumField(var1);
      this.resetChecksumField(var1);
      byte[] var3 = this.calculateChecksum(var1, var1.length);
      this.copyChecksumField(var1, var2);
      return var3;
   }

   private boolean isChecksumEqual(byte[] var1, byte[] var2) {
      if (var1 == var2) {
         return true;
      } else if ((var1 != null || var2 == null) && (var1 == null || var2 != null)) {
         if (var1.length != var2.length) {
            return false;
         } else {
            for(int var3 = 0; var3 < var1.length; ++var3) {
               if (var1[var3] != var2[var3]) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   protected boolean isChecksumValid(byte[] var1) throws KrbCryptoException {
      byte[] var2 = this.checksumField(var1);
      byte[] var3 = this.generateChecksum(var1);
      return this.isChecksumEqual(var2, var3);
   }
}
