package sun.security.provider;

abstract class SHA2 extends DigestBase {
   private static final int ITERATION = 64;
   private static final int[] ROUND_CONSTS = new int[]{1116352408, 1899447441, -1245643825, -373957723, 961987163, 1508970993, -1841331548, -1424204075, -670586216, 310598401, 607225278, 1426881987, 1925078388, -2132889090, -1680079193, -1046744716, -459576895, -272742522, 264347078, 604807628, 770255983, 1249150122, 1555081692, 1996064986, -1740746414, -1473132947, -1341970488, -1084653625, -958395405, -710438585, 113926993, 338241895, 666307205, 773529912, 1294757372, 1396182291, 1695183700, 1986661051, -2117940946, -1838011259, -1564481375, -1474664885, -1035236496, -949202525, -778901479, -694614492, -200395387, 275423344, 430227734, 506948616, 659060556, 883997877, 958139571, 1322822218, 1537002063, 1747873779, 1955562222, 2024104815, -2067236844, -1933114872, -1866530822, -1538233109, -1090935817, -965641998};
   private int[] W;
   private int[] state;
   private final int[] initialHashes;

   SHA2(String var1, int var2, int[] var3) {
      super(var1, var2, 64);
      this.initialHashes = var3;
      this.state = new int[8];
      this.W = new int[64];
      this.implReset();
   }

   void implReset() {
      System.arraycopy(this.initialHashes, 0, this.state, 0, this.state.length);
   }

   void implDigest(byte[] var1, int var2) {
      long var3 = this.bytesProcessed << 3;
      int var5 = (int)this.bytesProcessed & 63;
      int var6 = var5 < 56 ? 56 - var5 : 120 - var5;
      this.engineUpdate(padding, 0, var6);
      ByteArrayAccess.i2bBig4((int)(var3 >>> 32), this.buffer, 56);
      ByteArrayAccess.i2bBig4((int)var3, this.buffer, 60);
      this.implCompress(this.buffer, 0);
      ByteArrayAccess.i2bBig(this.state, 0, var1, var2, this.engineGetDigestLength());
   }

   private static int lf_ch(int var0, int var1, int var2) {
      return var0 & var1 ^ ~var0 & var2;
   }

   private static int lf_maj(int var0, int var1, int var2) {
      return var0 & var1 ^ var0 & var2 ^ var1 & var2;
   }

   private static int lf_R(int var0, int var1) {
      return var0 >>> var1;
   }

   private static int lf_S(int var0, int var1) {
      return var0 >>> var1 | var0 << 32 - var1;
   }

   private static int lf_sigma0(int var0) {
      return lf_S(var0, 2) ^ lf_S(var0, 13) ^ lf_S(var0, 22);
   }

   private static int lf_sigma1(int var0) {
      return lf_S(var0, 6) ^ lf_S(var0, 11) ^ lf_S(var0, 25);
   }

   private static int lf_delta0(int var0) {
      return lf_S(var0, 7) ^ lf_S(var0, 18) ^ lf_R(var0, 3);
   }

   private static int lf_delta1(int var0) {
      return lf_S(var0, 17) ^ lf_S(var0, 19) ^ lf_R(var0, 10);
   }

   void implCompress(byte[] var1, int var2) {
      ByteArrayAccess.b2iBig64(var1, var2, this.W);

      int var3;
      for(var3 = 16; var3 < 64; ++var3) {
         this.W[var3] = lf_delta1(this.W[var3 - 2]) + this.W[var3 - 7] + lf_delta0(this.W[var3 - 15]) + this.W[var3 - 16];
      }

      var3 = this.state[0];
      int var4 = this.state[1];
      int var5 = this.state[2];
      int var6 = this.state[3];
      int var7 = this.state[4];
      int var8 = this.state[5];
      int var9 = this.state[6];
      int var10 = this.state[7];

      for(int var11 = 0; var11 < 64; ++var11) {
         int var12 = var10 + lf_sigma1(var7) + lf_ch(var7, var8, var9) + ROUND_CONSTS[var11] + this.W[var11];
         int var13 = lf_sigma0(var3) + lf_maj(var3, var4, var5);
         var10 = var9;
         var9 = var8;
         var8 = var7;
         var7 = var6 + var12;
         var6 = var5;
         var5 = var4;
         var4 = var3;
         var3 = var12 + var13;
      }

      int[] var10000 = this.state;
      var10000[0] += var3;
      var10000 = this.state;
      var10000[1] += var4;
      var10000 = this.state;
      var10000[2] += var5;
      var10000 = this.state;
      var10000[3] += var6;
      var10000 = this.state;
      var10000[4] += var7;
      var10000 = this.state;
      var10000[5] += var8;
      var10000 = this.state;
      var10000[6] += var9;
      var10000 = this.state;
      var10000[7] += var10;
   }

   public Object clone() throws CloneNotSupportedException {
      SHA2 var1 = (SHA2)super.clone();
      var1.state = (int[])var1.state.clone();
      var1.W = new int[64];
      return var1;
   }

   public static final class SHA256 extends SHA2 {
      private static final int[] INITIAL_HASHES = new int[]{1779033703, -1150833019, 1013904242, -1521486534, 1359893119, -1694144372, 528734635, 1541459225};

      public SHA256() {
         super("SHA-256", 32, INITIAL_HASHES);
      }
   }

   public static final class SHA224 extends SHA2 {
      private static final int[] INITIAL_HASHES = new int[]{-1056596264, 914150663, 812702999, -150054599, -4191439, 1750603025, 1694076839, -1090891868};

      public SHA224() {
         super("SHA-224", 28, INITIAL_HASHES);
      }
   }
}
