package sun.security.provider;

public final class SHA extends DigestBase {
   private int[] W = new int[80];
   private int[] state = new int[5];
   private static final int round1_kt = 1518500249;
   private static final int round2_kt = 1859775393;
   private static final int round3_kt = -1894007588;
   private static final int round4_kt = -899497514;

   public SHA() {
      super("SHA-1", 20, 64);
      this.implReset();
   }

   public Object clone() throws CloneNotSupportedException {
      SHA var1 = (SHA)super.clone();
      var1.state = (int[])var1.state.clone();
      var1.W = new int[80];
      return var1;
   }

   void implReset() {
      this.state[0] = 1732584193;
      this.state[1] = -271733879;
      this.state[2] = -1732584194;
      this.state[3] = 271733878;
      this.state[4] = -1009589776;
   }

   void implDigest(byte[] var1, int var2) {
      long var3 = this.bytesProcessed << 3;
      int var5 = (int)this.bytesProcessed & 63;
      int var6 = var5 < 56 ? 56 - var5 : 120 - var5;
      this.engineUpdate(padding, 0, var6);
      ByteArrayAccess.i2bBig4((int)(var3 >>> 32), this.buffer, 56);
      ByteArrayAccess.i2bBig4((int)var3, this.buffer, 60);
      this.implCompress(this.buffer, 0);
      ByteArrayAccess.i2bBig(this.state, 0, var1, var2, 20);
   }

   void implCompress(byte[] var1, int var2) {
      ByteArrayAccess.b2iBig64(var1, var2, this.W);

      int var3;
      int var4;
      for(var3 = 16; var3 <= 79; ++var3) {
         var4 = this.W[var3 - 3] ^ this.W[var3 - 8] ^ this.W[var3 - 14] ^ this.W[var3 - 16];
         this.W[var3] = var4 << 1 | var4 >>> 31;
      }

      var3 = this.state[0];
      var4 = this.state[1];
      int var5 = this.state[2];
      int var6 = this.state[3];
      int var7 = this.state[4];

      int var8;
      int var9;
      for(var8 = 0; var8 < 20; ++var8) {
         var9 = (var3 << 5 | var3 >>> 27) + (var4 & var5 | ~var4 & var6) + var7 + this.W[var8] + 1518500249;
         var7 = var6;
         var6 = var5;
         var5 = var4 << 30 | var4 >>> 2;
         var4 = var3;
         var3 = var9;
      }

      for(var8 = 20; var8 < 40; ++var8) {
         var9 = (var3 << 5 | var3 >>> 27) + (var4 ^ var5 ^ var6) + var7 + this.W[var8] + 1859775393;
         var7 = var6;
         var6 = var5;
         var5 = var4 << 30 | var4 >>> 2;
         var4 = var3;
         var3 = var9;
      }

      for(var8 = 40; var8 < 60; ++var8) {
         var9 = (var3 << 5 | var3 >>> 27) + (var4 & var5 | var4 & var6 | var5 & var6) + var7 + this.W[var8] + -1894007588;
         var7 = var6;
         var6 = var5;
         var5 = var4 << 30 | var4 >>> 2;
         var4 = var3;
         var3 = var9;
      }

      for(var8 = 60; var8 < 80; ++var8) {
         var9 = (var3 << 5 | var3 >>> 27) + (var4 ^ var5 ^ var6) + var7 + this.W[var8] + -899497514;
         var7 = var6;
         var6 = var5;
         var5 = var4 << 30 | var4 >>> 2;
         var4 = var3;
         var3 = var9;
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
   }
}
