package sun.security.krb5.internal.crypto;

public class Crc32CksumType extends CksumType {
   public int confounderSize() {
      return 0;
   }

   public int cksumType() {
      return 1;
   }

   public boolean isSafe() {
      return false;
   }

   public int cksumSize() {
      return 4;
   }

   public int keyType() {
      return 0;
   }

   public int keySize() {
      return 0;
   }

   public byte[] calculateChecksum(byte[] var1, int var2) {
      return crc32.byte2crc32sum_bytes(var1, var2);
   }

   public byte[] calculateKeyedChecksum(byte[] var1, int var2, byte[] var3, int var4) {
      return null;
   }

   public boolean verifyKeyedChecksum(byte[] var1, int var2, byte[] var3, byte[] var4, int var5) {
      return false;
   }

   public static byte[] int2quad(long var0) {
      byte[] var2 = new byte[4];

      for(int var3 = 0; var3 < 4; ++var3) {
         var2[var3] = (byte)((int)(var0 >>> var3 * 8 & 255L));
      }

      return var2;
   }

   public static long bytes2long(byte[] var0) {
      long var1 = 0L;
      var1 |= ((long)var0[0] & 255L) << 24;
      var1 |= ((long)var0[1] & 255L) << 16;
      var1 |= ((long)var0[2] & 255L) << 8;
      var1 |= (long)var0[3] & 255L;
      return var1;
   }
}
