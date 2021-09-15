package sun.security.provider;

import java.util.Arrays;

public final class MD2 extends DigestBase {
   private int[] X = new int[48];
   private int[] C = new int[16];
   private byte[] cBytes = new byte[16];
   private static final int[] S = new int[]{41, 46, 67, 201, 162, 216, 124, 1, 61, 54, 84, 161, 236, 240, 6, 19, 98, 167, 5, 243, 192, 199, 115, 140, 152, 147, 43, 217, 188, 76, 130, 202, 30, 155, 87, 60, 253, 212, 224, 22, 103, 66, 111, 24, 138, 23, 229, 18, 190, 78, 196, 214, 218, 158, 222, 73, 160, 251, 245, 142, 187, 47, 238, 122, 169, 104, 121, 145, 21, 178, 7, 63, 148, 194, 16, 137, 11, 34, 95, 33, 128, 127, 93, 154, 90, 144, 50, 39, 53, 62, 204, 231, 191, 247, 151, 3, 255, 25, 48, 179, 72, 165, 181, 209, 215, 94, 146, 42, 172, 86, 170, 198, 79, 184, 56, 210, 150, 164, 125, 182, 118, 252, 107, 226, 156, 116, 4, 241, 69, 157, 112, 89, 100, 113, 135, 32, 134, 91, 207, 101, 230, 45, 168, 2, 27, 96, 37, 173, 174, 176, 185, 246, 28, 70, 97, 105, 52, 64, 126, 15, 85, 71, 163, 35, 221, 81, 175, 58, 195, 92, 249, 206, 186, 197, 234, 38, 44, 83, 13, 110, 133, 40, 132, 9, 211, 223, 205, 244, 65, 129, 77, 82, 106, 220, 55, 200, 108, 193, 171, 250, 36, 225, 123, 8, 12, 189, 177, 74, 120, 136, 149, 139, 227, 99, 232, 109, 233, 203, 213, 254, 59, 0, 29, 57, 242, 239, 183, 14, 102, 88, 208, 228, 166, 119, 114, 248, 235, 117, 75, 10, 49, 68, 80, 180, 143, 237, 31, 26, 219, 153, 141, 51, 159, 17, 131, 20};
   private static final byte[][] PADDING = new byte[17][];

   public MD2() {
      super("MD2", 16, 16);
   }

   public Object clone() throws CloneNotSupportedException {
      MD2 var1 = (MD2)super.clone();
      var1.X = (int[])var1.X.clone();
      var1.C = (int[])var1.C.clone();
      var1.cBytes = new byte[16];
      return var1;
   }

   void implReset() {
      Arrays.fill((int[])this.X, (int)0);
      Arrays.fill((int[])this.C, (int)0);
   }

   void implDigest(byte[] var1, int var2) {
      int var3 = 16 - ((int)this.bytesProcessed & 15);
      this.engineUpdate(PADDING[var3], 0, var3);

      int var4;
      for(var4 = 0; var4 < 16; ++var4) {
         this.cBytes[var4] = (byte)this.C[var4];
      }

      this.implCompress(this.cBytes, 0);

      for(var4 = 0; var4 < 16; ++var4) {
         var1[var2 + var4] = (byte)this.X[var4];
      }

   }

   void implCompress(byte[] var1, int var2) {
      int var3;
      int var4;
      for(var3 = 0; var3 < 16; ++var3) {
         var4 = var1[var2 + var3] & 255;
         this.X[16 + var3] = var4;
         this.X[32 + var3] = var4 ^ this.X[var3];
      }

      var3 = this.C[15];

      int[] var10000;
      for(var4 = 0; var4 < 16; ++var4) {
         var10000 = this.C;
         var3 = var10000[var4] ^= S[this.X[16 + var4] ^ var3];
      }

      var3 = 0;

      for(var4 = 0; var4 < 18; ++var4) {
         for(int var5 = 0; var5 < 48; ++var5) {
            var10000 = this.X;
            var3 = var10000[var5] ^= S[var3];
         }

         var3 = var3 + var4 & 255;
      }

   }

   static {
      for(int var0 = 1; var0 < 17; ++var0) {
         byte[] var1 = new byte[var0];
         Arrays.fill(var1, (byte)var0);
         PADDING[var0] = var1;
      }

   }
}
