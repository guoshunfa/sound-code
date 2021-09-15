package java.util.prefs;

import java.util.Arrays;
import java.util.Random;

class Base64 {
   private static final char[] intToBase64 = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
   private static final char[] intToAltBase64 = new char[]{'!', '"', '#', '$', '%', '&', '\'', '(', ')', ',', '-', '.', ':', ';', '<', '>', '@', '[', ']', '^', '`', '_', '{', '|', '}', '~', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '?'};
   private static final byte[] base64ToInt = new byte[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51};
   private static final byte[] altBase64ToInt = new byte[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, -1, 62, 9, 10, 11, -1, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 12, 13, 14, -1, 15, 63, 16, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 17, -1, 18, 19, 21, 20, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 22, 23, 24, 25};

   static String byteArrayToBase64(byte[] var0) {
      return byteArrayToBase64(var0, false);
   }

   static String byteArrayToAltBase64(byte[] var0) {
      return byteArrayToBase64(var0, true);
   }

   private static String byteArrayToBase64(byte[] var0, boolean var1) {
      int var2 = var0.length;
      int var3 = var2 / 3;
      int var4 = var2 - 3 * var3;
      int var5 = 4 * ((var2 + 2) / 3);
      StringBuffer var6 = new StringBuffer(var5);
      char[] var7 = var1 ? intToAltBase64 : intToBase64;
      int var8 = 0;

      int var9;
      int var10;
      for(var9 = 0; var9 < var3; ++var9) {
         var10 = var0[var8++] & 255;
         int var11 = var0[var8++] & 255;
         int var12 = var0[var8++] & 255;
         var6.append(var7[var10 >> 2]);
         var6.append(var7[var10 << 4 & 63 | var11 >> 4]);
         var6.append(var7[var11 << 2 & 63 | var12 >> 6]);
         var6.append(var7[var12 & 63]);
      }

      if (var4 != 0) {
         var9 = var0[var8++] & 255;
         var6.append(var7[var9 >> 2]);
         if (var4 == 1) {
            var6.append(var7[var9 << 4 & 63]);
            var6.append("==");
         } else {
            var10 = var0[var8++] & 255;
            var6.append(var7[var9 << 4 & 63 | var10 >> 4]);
            var6.append(var7[var10 << 2 & 63]);
            var6.append('=');
         }
      }

      return var6.toString();
   }

   static byte[] base64ToByteArray(String var0) {
      return base64ToByteArray(var0, false);
   }

   static byte[] altBase64ToByteArray(String var0) {
      return base64ToByteArray(var0, true);
   }

   private static byte[] base64ToByteArray(String var0, boolean var1) {
      byte[] var2 = var1 ? altBase64ToInt : base64ToInt;
      int var3 = var0.length();
      int var4 = var3 / 4;
      if (4 * var4 != var3) {
         throw new IllegalArgumentException("String length must be a multiple of four.");
      } else {
         int var5 = 0;
         int var6 = var4;
         if (var3 != 0) {
            if (var0.charAt(var3 - 1) == '=') {
               ++var5;
               var6 = var4 - 1;
            }

            if (var0.charAt(var3 - 2) == '=') {
               ++var5;
            }
         }

         byte[] var7 = new byte[3 * var4 - var5];
         int var8 = 0;
         int var9 = 0;

         int var10;
         int var11;
         int var12;
         for(var10 = 0; var10 < var6; ++var10) {
            var11 = base64toInt(var0.charAt(var8++), var2);
            var12 = base64toInt(var0.charAt(var8++), var2);
            int var13 = base64toInt(var0.charAt(var8++), var2);
            int var14 = base64toInt(var0.charAt(var8++), var2);
            var7[var9++] = (byte)(var11 << 2 | var12 >> 4);
            var7[var9++] = (byte)(var12 << 4 | var13 >> 2);
            var7[var9++] = (byte)(var13 << 6 | var14);
         }

         if (var5 != 0) {
            var10 = base64toInt(var0.charAt(var8++), var2);
            var11 = base64toInt(var0.charAt(var8++), var2);
            var7[var9++] = (byte)(var10 << 2 | var11 >> 4);
            if (var5 == 1) {
               var12 = base64toInt(var0.charAt(var8++), var2);
               var7[var9++] = (byte)(var11 << 4 | var12 >> 2);
            }
         }

         return var7;
      }
   }

   private static int base64toInt(char var0, byte[] var1) {
      byte var2 = var1[var0];
      if (var2 < 0) {
         throw new IllegalArgumentException("Illegal character " + var0);
      } else {
         return var2;
      }
   }

   public static void main(String[] var0) {
      int var1 = Integer.parseInt(var0[0]);
      int var2 = Integer.parseInt(var0[1]);
      Random var3 = new Random();

      for(int var4 = 0; var4 < var1; ++var4) {
         for(int var5 = 0; var5 < var2; ++var5) {
            byte[] var6 = new byte[var5];

            for(int var7 = 0; var7 < var5; ++var7) {
               var6[var7] = (byte)var3.nextInt();
            }

            String var9 = byteArrayToBase64(var6);
            byte[] var8 = base64ToByteArray(var9);
            if (!Arrays.equals(var6, var8)) {
               System.out.println("Dismal failure!");
            }

            var9 = byteArrayToAltBase64(var6);
            var8 = altBase64ToByteArray(var9);
            if (!Arrays.equals(var6, var8)) {
               System.out.println("Alternate dismal failure!");
            }
         }
      }

   }
}
