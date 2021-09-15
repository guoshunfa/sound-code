package sun.security.provider;

abstract class SHA5 extends DigestBase {
   private static final int ITERATION = 80;
   private static final long[] ROUND_CONSTS = new long[]{4794697086780616226L, 8158064640168781261L, -5349999486874862801L, -1606136188198331460L, 4131703408338449720L, 6480981068601479193L, -7908458776815382629L, -6116909921290321640L, -2880145864133508542L, 1334009975649890238L, 2608012711638119052L, 6128411473006802146L, 8268148722764581231L, -9160688886553864527L, -7215885187991268811L, -4495734319001033068L, -1973867731355612462L, -1171420211273849373L, 1135362057144423861L, 2597628984639134821L, 3308224258029322869L, 5365058923640841347L, 6679025012923562964L, 8573033837759648693L, -7476448914759557205L, -6327057829258317296L, -5763719355590565569L, -4658551843659510044L, -4116276920077217854L, -3051310485924567259L, 489312712824947311L, 1452737877330783856L, 2861767655752347644L, 3322285676063803686L, 5560940570517711597L, 5996557281743188959L, 7280758554555802590L, 8532644243296465576L, -9096487096722542874L, -7894198246740708037L, -6719396339535248540L, -6333637450476146687L, -4446306890439682159L, -4076793802049405392L, -3345356375505022440L, -2983346525034927856L, -860691631967231958L, 1182934255886127544L, 1847814050463011016L, 2177327727835720531L, 2830643537854262169L, 3796741975233480872L, 4115178125766777443L, 5681478168544905931L, 6601373596472566643L, 7507060721942968483L, 8399075790359081724L, 8693463985226723168L, -8878714635349349518L, -8302665154208450068L, -8016688836872298968L, -6606660893046293015L, -4685533653050689259L, -4147400797238176981L, -3880063495543823972L, -3348786107499101689L, -1523767162380948706L, -757361751448694408L, 500013540394364858L, 748580250866718886L, 1242879168328830382L, 1977374033974150939L, 2944078676154940804L, 3659926193048069267L, 4368137639120453308L, 4836135668995329356L, 5532061633213252278L, 6448918945643986474L, 6902733635092675308L, 7801388544844847127L};
   private long[] W;
   private long[] state;
   private final long[] initialHashes;

   SHA5(String var1, int var2, long[] var3) {
      super(var1, var2, 128);
      this.initialHashes = var3;
      this.state = new long[8];
      this.W = new long[80];
      this.implReset();
   }

   final void implReset() {
      System.arraycopy(this.initialHashes, 0, this.state, 0, this.state.length);
   }

   final void implDigest(byte[] var1, int var2) {
      long var3 = this.bytesProcessed << 3;
      int var5 = (int)this.bytesProcessed & 127;
      int var6 = var5 < 112 ? 112 - var5 : 240 - var5;
      this.engineUpdate(padding, 0, var6 + 8);
      ByteArrayAccess.i2bBig4((int)(var3 >>> 32), this.buffer, 120);
      ByteArrayAccess.i2bBig4((int)var3, this.buffer, 124);
      this.implCompress(this.buffer, 0);
      ByteArrayAccess.l2bBig(this.state, 0, var1, var2, this.engineGetDigestLength());
   }

   private static long lf_ch(long var0, long var2, long var4) {
      return var0 & var2 ^ ~var0 & var4;
   }

   private static long lf_maj(long var0, long var2, long var4) {
      return var0 & var2 ^ var0 & var4 ^ var2 & var4;
   }

   private static long lf_R(long var0, int var2) {
      return var0 >>> var2;
   }

   private static long lf_S(long var0, int var2) {
      return var0 >>> var2 | var0 << 64 - var2;
   }

   private static long lf_sigma0(long var0) {
      return lf_S(var0, 28) ^ lf_S(var0, 34) ^ lf_S(var0, 39);
   }

   private static long lf_sigma1(long var0) {
      return lf_S(var0, 14) ^ lf_S(var0, 18) ^ lf_S(var0, 41);
   }

   private static long lf_delta0(long var0) {
      return lf_S(var0, 1) ^ lf_S(var0, 8) ^ lf_R(var0, 7);
   }

   private static long lf_delta1(long var0) {
      return lf_S(var0, 19) ^ lf_S(var0, 61) ^ lf_R(var0, 6);
   }

   final void implCompress(byte[] var1, int var2) {
      ByteArrayAccess.b2lBig128(var1, var2, this.W);

      for(int var3 = 16; var3 < 80; ++var3) {
         this.W[var3] = lf_delta1(this.W[var3 - 2]) + this.W[var3 - 7] + lf_delta0(this.W[var3 - 15]) + this.W[var3 - 16];
      }

      long var24 = this.state[0];
      long var5 = this.state[1];
      long var7 = this.state[2];
      long var9 = this.state[3];
      long var11 = this.state[4];
      long var13 = this.state[5];
      long var15 = this.state[6];
      long var17 = this.state[7];

      for(int var19 = 0; var19 < 80; ++var19) {
         long var20 = var17 + lf_sigma1(var11) + lf_ch(var11, var13, var15) + ROUND_CONSTS[var19] + this.W[var19];
         long var22 = lf_sigma0(var24) + lf_maj(var24, var5, var7);
         var17 = var15;
         var15 = var13;
         var13 = var11;
         var11 = var9 + var20;
         var9 = var7;
         var7 = var5;
         var5 = var24;
         var24 = var20 + var22;
      }

      long[] var10000 = this.state;
      var10000[0] += var24;
      var10000 = this.state;
      var10000[1] += var5;
      var10000 = this.state;
      var10000[2] += var7;
      var10000 = this.state;
      var10000[3] += var9;
      var10000 = this.state;
      var10000[4] += var11;
      var10000 = this.state;
      var10000[5] += var13;
      var10000 = this.state;
      var10000[6] += var15;
      var10000 = this.state;
      var10000[7] += var17;
   }

   public Object clone() throws CloneNotSupportedException {
      SHA5 var1 = (SHA5)super.clone();
      var1.state = (long[])var1.state.clone();
      var1.W = new long[80];
      return var1;
   }

   public static final class SHA384 extends SHA5 {
      private static final long[] INITIAL_HASHES = new long[]{-3766243637369397544L, 7105036623409894663L, -7973340178411365097L, 1526699215303891257L, 7436329637833083697L, -8163818279084223215L, -2662702644619276377L, 5167115440072839076L};

      public SHA384() {
         super("SHA-384", 48, INITIAL_HASHES);
      }
   }

   public static final class SHA512 extends SHA5 {
      private static final long[] INITIAL_HASHES = new long[]{7640891576956012808L, -4942790177534073029L, 4354685564936845355L, -6534734903238641935L, 5840696475078001361L, -7276294671716946913L, 2270897969802886507L, 6620516959819538809L};

      public SHA512() {
         super("SHA-512", 64, INITIAL_HASHES);
      }
   }
}
