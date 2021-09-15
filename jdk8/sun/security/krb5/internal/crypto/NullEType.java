package sun.security.krb5.internal.crypto;

import sun.security.krb5.internal.KrbApErrException;

public class NullEType extends EType {
   public int eType() {
      return 0;
   }

   public int minimumPadSize() {
      return 0;
   }

   public int confounderSize() {
      return 0;
   }

   public int checksumType() {
      return 0;
   }

   public int checksumSize() {
      return 0;
   }

   public int blockSize() {
      return 1;
   }

   public int keyType() {
      return 0;
   }

   public int keySize() {
      return 0;
   }

   public byte[] encrypt(byte[] var1, byte[] var2, int var3) {
      byte[] var4 = new byte[var1.length];
      System.arraycopy(var1, 0, var4, 0, var1.length);
      return var4;
   }

   public byte[] encrypt(byte[] var1, byte[] var2, byte[] var3, int var4) {
      byte[] var5 = new byte[var1.length];
      System.arraycopy(var1, 0, var5, 0, var1.length);
      return var5;
   }

   public byte[] decrypt(byte[] var1, byte[] var2, int var3) throws KrbApErrException {
      return (byte[])var1.clone();
   }

   public byte[] decrypt(byte[] var1, byte[] var2, byte[] var3, int var4) throws KrbApErrException {
      return (byte[])var1.clone();
   }
}
