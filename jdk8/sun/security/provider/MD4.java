package sun.security.provider;

import java.security.AccessController;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.ProviderException;

public final class MD4 extends DigestBase {
   private int[] state = new int[4];
   private int[] x = new int[16];
   private static final int S11 = 3;
   private static final int S12 = 7;
   private static final int S13 = 11;
   private static final int S14 = 19;
   private static final int S21 = 3;
   private static final int S22 = 5;
   private static final int S23 = 9;
   private static final int S24 = 13;
   private static final int S31 = 3;
   private static final int S32 = 9;
   private static final int S33 = 11;
   private static final int S34 = 15;
   private static final Provider md4Provider = new Provider("MD4Provider", 1.8D, "MD4 MessageDigest") {
      private static final long serialVersionUID = -8850464997518327965L;
   };

   public static MessageDigest getInstance() {
      try {
         return MessageDigest.getInstance("MD4", md4Provider);
      } catch (NoSuchAlgorithmException var1) {
         throw new ProviderException(var1);
      }
   }

   public MD4() {
      super("MD4", 16, 64);
      this.implReset();
   }

   public Object clone() throws CloneNotSupportedException {
      MD4 var1 = (MD4)super.clone();
      var1.state = (int[])var1.state.clone();
      var1.x = new int[16];
      return var1;
   }

   void implReset() {
      this.state[0] = 1732584193;
      this.state[1] = -271733879;
      this.state[2] = -1732584194;
      this.state[3] = 271733878;
   }

   void implDigest(byte[] var1, int var2) {
      long var3 = this.bytesProcessed << 3;
      int var5 = (int)this.bytesProcessed & 63;
      int var6 = var5 < 56 ? 56 - var5 : 120 - var5;
      this.engineUpdate(padding, 0, var6);
      ByteArrayAccess.i2bLittle4((int)var3, this.buffer, 56);
      ByteArrayAccess.i2bLittle4((int)(var3 >>> 32), this.buffer, 60);
      this.implCompress(this.buffer, 0);
      ByteArrayAccess.i2bLittle(this.state, 0, var1, var2, 16);
   }

   private static int FF(int var0, int var1, int var2, int var3, int var4, int var5) {
      var0 += (var1 & var2 | ~var1 & var3) + var4;
      return var0 << var5 | var0 >>> 32 - var5;
   }

   private static int GG(int var0, int var1, int var2, int var3, int var4, int var5) {
      var0 += (var1 & var2 | var1 & var3 | var2 & var3) + var4 + 1518500249;
      return var0 << var5 | var0 >>> 32 - var5;
   }

   private static int HH(int var0, int var1, int var2, int var3, int var4, int var5) {
      var0 += (var1 ^ var2 ^ var3) + var4 + 1859775393;
      return var0 << var5 | var0 >>> 32 - var5;
   }

   void implCompress(byte[] var1, int var2) {
      ByteArrayAccess.b2iLittle64(var1, var2, this.x);
      int var3 = this.state[0];
      int var4 = this.state[1];
      int var5 = this.state[2];
      int var6 = this.state[3];
      var3 = FF(var3, var4, var5, var6, this.x[0], 3);
      var6 = FF(var6, var3, var4, var5, this.x[1], 7);
      var5 = FF(var5, var6, var3, var4, this.x[2], 11);
      var4 = FF(var4, var5, var6, var3, this.x[3], 19);
      var3 = FF(var3, var4, var5, var6, this.x[4], 3);
      var6 = FF(var6, var3, var4, var5, this.x[5], 7);
      var5 = FF(var5, var6, var3, var4, this.x[6], 11);
      var4 = FF(var4, var5, var6, var3, this.x[7], 19);
      var3 = FF(var3, var4, var5, var6, this.x[8], 3);
      var6 = FF(var6, var3, var4, var5, this.x[9], 7);
      var5 = FF(var5, var6, var3, var4, this.x[10], 11);
      var4 = FF(var4, var5, var6, var3, this.x[11], 19);
      var3 = FF(var3, var4, var5, var6, this.x[12], 3);
      var6 = FF(var6, var3, var4, var5, this.x[13], 7);
      var5 = FF(var5, var6, var3, var4, this.x[14], 11);
      var4 = FF(var4, var5, var6, var3, this.x[15], 19);
      var3 = GG(var3, var4, var5, var6, this.x[0], 3);
      var6 = GG(var6, var3, var4, var5, this.x[4], 5);
      var5 = GG(var5, var6, var3, var4, this.x[8], 9);
      var4 = GG(var4, var5, var6, var3, this.x[12], 13);
      var3 = GG(var3, var4, var5, var6, this.x[1], 3);
      var6 = GG(var6, var3, var4, var5, this.x[5], 5);
      var5 = GG(var5, var6, var3, var4, this.x[9], 9);
      var4 = GG(var4, var5, var6, var3, this.x[13], 13);
      var3 = GG(var3, var4, var5, var6, this.x[2], 3);
      var6 = GG(var6, var3, var4, var5, this.x[6], 5);
      var5 = GG(var5, var6, var3, var4, this.x[10], 9);
      var4 = GG(var4, var5, var6, var3, this.x[14], 13);
      var3 = GG(var3, var4, var5, var6, this.x[3], 3);
      var6 = GG(var6, var3, var4, var5, this.x[7], 5);
      var5 = GG(var5, var6, var3, var4, this.x[11], 9);
      var4 = GG(var4, var5, var6, var3, this.x[15], 13);
      var3 = HH(var3, var4, var5, var6, this.x[0], 3);
      var6 = HH(var6, var3, var4, var5, this.x[8], 9);
      var5 = HH(var5, var6, var3, var4, this.x[4], 11);
      var4 = HH(var4, var5, var6, var3, this.x[12], 15);
      var3 = HH(var3, var4, var5, var6, this.x[2], 3);
      var6 = HH(var6, var3, var4, var5, this.x[10], 9);
      var5 = HH(var5, var6, var3, var4, this.x[6], 11);
      var4 = HH(var4, var5, var6, var3, this.x[14], 15);
      var3 = HH(var3, var4, var5, var6, this.x[1], 3);
      var6 = HH(var6, var3, var4, var5, this.x[9], 9);
      var5 = HH(var5, var6, var3, var4, this.x[5], 11);
      var4 = HH(var4, var5, var6, var3, this.x[13], 15);
      var3 = HH(var3, var4, var5, var6, this.x[3], 3);
      var6 = HH(var6, var3, var4, var5, this.x[11], 9);
      var5 = HH(var5, var6, var3, var4, this.x[7], 11);
      var4 = HH(var4, var5, var6, var3, this.x[15], 15);
      int[] var10000 = this.state;
      var10000[0] += var3;
      var10000 = this.state;
      var10000[1] += var4;
      var10000 = this.state;
      var10000[2] += var5;
      var10000 = this.state;
      var10000[3] += var6;
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            MD4.md4Provider.put("MessageDigest.MD4", "sun.security.provider.MD4");
            return null;
         }
      });
   }
}
