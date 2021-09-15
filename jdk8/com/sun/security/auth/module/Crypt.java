package com.sun.security.auth.module;

import java.io.UnsupportedEncodingException;

class Crypt {
   private static final byte[] IP = new byte[]{58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 30, 22, 14, 6, 64, 56, 48, 40, 32, 24, 16, 8, 57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19, 11, 3, 61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7};
   private static final byte[] FP = new byte[]{40, 8, 48, 16, 56, 24, 64, 32, 39, 7, 47, 15, 55, 23, 63, 31, 38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45, 13, 53, 21, 61, 29, 36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11, 51, 19, 59, 27, 34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 49, 17, 57, 25};
   private static final byte[] PC1_C = new byte[]{57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 27, 19, 11, 3, 60, 52, 44, 36};
   private static final byte[] PC1_D = new byte[]{63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22, 14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 28, 20, 12, 4};
   private static final byte[] shifts = new byte[]{1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};
   private static final byte[] PC2_C = new byte[]{14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21, 10, 23, 19, 12, 4, 26, 8, 16, 7, 27, 20, 13, 2};
   private static final byte[] PC2_D = new byte[]{41, 52, 31, 37, 47, 55, 30, 40, 51, 45, 33, 48, 44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29, 32};
   private byte[] C = new byte[28];
   private byte[] D = new byte[28];
   private byte[] KS;
   private byte[] E = new byte[48];
   private static final byte[] e2 = new byte[]{32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9, 8, 9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17, 16, 17, 18, 19, 20, 21, 20, 21, 22, 23, 24, 25, 24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32, 1};
   private static final byte[][] S = new byte[][]{{14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7, 0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8, 4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0, 15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13}, {15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10, 3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5, 0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15, 13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9}, {10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8, 13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1, 13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7, 1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12}, {7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15, 13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9, 10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4, 3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14}, {2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9, 14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6, 4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14, 11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3}, {12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11, 10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8, 9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6, 4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13}, {4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1, 13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6, 1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2, 6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12}, {13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7, 1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2, 7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8, 2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11}};
   private static final byte[] P = new byte[]{16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 23, 26, 5, 18, 31, 10, 2, 8, 24, 14, 32, 27, 3, 9, 19, 13, 30, 6, 22, 11, 4, 25};
   private byte[] L = new byte[64];
   private byte[] tempL = new byte[32];
   private byte[] f = new byte[32];
   private byte[] preS = new byte[48];

   private void setkey(byte[] var1) {
      if (this.KS == null) {
         this.KS = new byte[768];
      }

      int var2;
      for(var2 = 0; var2 < 28; ++var2) {
         this.C[var2] = var1[PC1_C[var2] - 1];
         this.D[var2] = var1[PC1_D[var2] - 1];
      }

      for(var2 = 0; var2 < 16; ++var2) {
         int var3;
         for(int var4 = 0; var4 < shifts[var2]; ++var4) {
            byte var5 = this.C[0];

            for(var3 = 0; var3 < 27; ++var3) {
               this.C[var3] = this.C[var3 + 1];
            }

            this.C[27] = var5;
            var5 = this.D[0];

            for(var3 = 0; var3 < 27; ++var3) {
               this.D[var3] = this.D[var3 + 1];
            }

            this.D[27] = var5;
         }

         for(var3 = 0; var3 < 24; ++var3) {
            int var6 = var2 * 48;
            this.KS[var6 + var3] = this.C[PC2_C[var3] - 1];
            this.KS[var6 + var3 + 24] = this.D[PC2_D[var3] - 28 - 1];
         }
      }

      for(var2 = 0; var2 < 48; ++var2) {
         this.E[var2] = e2[var2];
      }

   }

   private void encrypt(byte[] var1, int var2) {
      byte var7 = 32;
      if (this.KS == null) {
         this.KS = new byte[768];
      }

      int var5;
      for(var5 = 0; var5 < 64; ++var5) {
         this.L[var5] = var1[IP[var5] - 1];
      }

      for(int var3 = 0; var3 < 16; ++var3) {
         int var8 = var3 * 48;

         for(var5 = 0; var5 < 32; ++var5) {
            this.tempL[var5] = this.L[var7 + var5];
         }

         for(var5 = 0; var5 < 48; ++var5) {
            this.preS[var5] = (byte)(this.L[var7 + this.E[var5] - 1] ^ this.KS[var8 + var5]);
         }

         for(var5 = 0; var5 < 8; ++var5) {
            int var4 = 6 * var5;
            byte var6 = S[var5][(this.preS[var4 + 0] << 5) + (this.preS[var4 + 1] << 3) + (this.preS[var4 + 2] << 2) + (this.preS[var4 + 3] << 1) + (this.preS[var4 + 4] << 0) + (this.preS[var4 + 5] << 4)];
            var4 = 4 * var5;
            this.f[var4 + 0] = (byte)(var6 >> 3 & 1);
            this.f[var4 + 1] = (byte)(var6 >> 2 & 1);
            this.f[var4 + 2] = (byte)(var6 >> 1 & 1);
            this.f[var4 + 3] = (byte)(var6 >> 0 & 1);
         }

         for(var5 = 0; var5 < 32; ++var5) {
            this.L[var7 + var5] = (byte)(this.L[var5] ^ this.f[P[var5] - 1]);
         }

         for(var5 = 0; var5 < 32; ++var5) {
            this.L[var5] = this.tempL[var5];
         }
      }

      for(var5 = 0; var5 < 32; ++var5) {
         byte var9 = this.L[var5];
         this.L[var5] = this.L[var7 + var5];
         this.L[var7 + var5] = (byte)var9;
      }

      for(var5 = 0; var5 < 64; ++var5) {
         var1[var5] = this.L[FP[var5] - 1];
      }

   }

   public Crypt() {
   }

   public synchronized byte[] crypt(byte[] var1, byte[] var2) {
      byte[] var8 = new byte[66];
      byte[] var9 = new byte[13];
      int var6 = 0;

      int var4;
      int var5;
      for(var4 = 0; var6 < var1.length && var4 < 64; ++var6) {
         byte var3 = var1[var6];

         for(var5 = 0; var5 < 7; ++var4) {
            var8[var4] = (byte)(var3 >> 6 - var5 & 1);
            ++var5;
         }

         ++var4;
      }

      this.setkey(var8);

      for(var4 = 0; var4 < 66; ++var4) {
         var8[var4] = 0;
      }

      int var10;
      for(var4 = 0; var4 < 2; ++var4) {
         var10 = var2[var4];
         var9[var4] = (byte)var10;
         if (var10 > 90) {
            var10 -= 6;
         }

         if (var10 > 57) {
            var10 -= 7;
         }

         var10 -= 46;

         for(var5 = 0; var5 < 6; ++var5) {
            if ((var10 >> var5 & 1) != 0) {
               byte var7 = this.E[6 * var4 + var5];
               this.E[6 * var4 + var5] = this.E[6 * var4 + var5 + 24];
               this.E[6 * var4 + var5 + 24] = var7;
            }
         }
      }

      for(var4 = 0; var4 < 25; ++var4) {
         this.encrypt(var8, 0);
      }

      for(var4 = 0; var4 < 11; ++var4) {
         var10 = 0;

         for(var5 = 0; var5 < 6; ++var5) {
            var10 <<= 1;
            var10 |= var8[6 * var4 + var5];
         }

         var10 += 46;
         if (var10 > 57) {
            var10 += 7;
         }

         if (var10 > 90) {
            var10 += 6;
         }

         var9[var4 + 2] = (byte)var10;
      }

      if (var9[1] == 0) {
         var9[1] = var9[0];
      }

      return var9;
   }

   public static void main(String[] var0) {
      if (var0.length != 2) {
         System.err.println("usage: Crypt password salt");
         System.exit(1);
      }

      Crypt var1 = new Crypt();

      try {
         byte[] var2 = var1.crypt(var0[0].getBytes("ISO-8859-1"), var0[1].getBytes("ISO-8859-1"));

         for(int var3 = 0; var3 < var2.length; ++var3) {
            System.out.println(" " + var3 + " " + (char)var2[var3]);
         }
      } catch (UnsupportedEncodingException var4) {
      }

   }
}
