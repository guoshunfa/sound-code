package sun.java2d.cmm;

public class ProfileDataVerifier {
   private static final int MAX_TAG_COUNT = 100;
   private static final int HEADER_SIZE = 128;
   private static final int TOC_OFFSET = 132;
   private static final int TOC_RECORD_SIZE = 12;
   private static final int PROFILE_FILE_SIGNATURE = 1633907568;

   public static void verify(byte[] var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("Invalid ICC Profile Data");
      } else if (var0.length < 132) {
         throw new IllegalArgumentException("Invalid ICC Profile Data");
      } else {
         int var1 = readInt32(var0, 0);
         int var2 = readInt32(var0, 128);
         if (var2 >= 0 && var2 <= 100) {
            if (var1 >= 132 + var2 * 12 && var1 <= var0.length) {
               int var3 = readInt32(var0, 36);
               if (1633907568 != var3) {
                  throw new IllegalArgumentException("Invalid ICC Profile Data");
               } else {
                  int var4 = 0;

                  while(var4 < var2) {
                     int var5 = getTagOffset(var4, var0);
                     int var6 = getTagSize(var4, var0);
                     if (var5 >= 132 && var5 <= var1) {
                        if (var6 >= 0 && var6 <= Integer.MAX_VALUE - var5 && var6 + var5 <= var1) {
                           ++var4;
                           continue;
                        }

                        throw new IllegalArgumentException("Invalid ICC Profile Data");
                     }

                     throw new IllegalArgumentException("Invalid ICC Profile Data");
                  }

               }
            } else {
               throw new IllegalArgumentException("Invalid ICC Profile Data");
            }
         } else {
            throw new IllegalArgumentException("Invalid ICC Profile Data");
         }
      }
   }

   private static int getTagOffset(int var0, byte[] var1) {
      int var2 = 132 + var0 * 12 + 4;
      return readInt32(var1, var2);
   }

   private static int getTagSize(int var0, byte[] var1) {
      int var2 = 132 + var0 * 12 + 8;
      return readInt32(var1, var2);
   }

   private static int readInt32(byte[] var0, int var1) {
      int var2 = 0;

      for(int var3 = 0; var3 < 4; ++var3) {
         var2 <<= 8;
         var2 |= 255 & var0[var1++];
      }

      return var2;
   }
}
