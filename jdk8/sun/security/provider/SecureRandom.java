package sun.security.provider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandomSpi;

public final class SecureRandom extends SecureRandomSpi implements Serializable {
   private static final long serialVersionUID = 3581829991155417889L;
   private static final int DIGEST_SIZE = 20;
   private transient MessageDigest digest;
   private byte[] state;
   private byte[] remainder;
   private int remCount;

   public SecureRandom() {
      this.init((byte[])null);
   }

   private SecureRandom(byte[] var1) {
      this.init(var1);
   }

   private void init(byte[] var1) {
      try {
         this.digest = MessageDigest.getInstance("SHA", "SUN");
      } catch (NoSuchAlgorithmException | NoSuchProviderException var5) {
         try {
            this.digest = MessageDigest.getInstance("SHA");
         } catch (NoSuchAlgorithmException var4) {
            throw new InternalError("internal error: SHA-1 not available.", var4);
         }
      }

      if (var1 != null) {
         this.engineSetSeed(var1);
      }

   }

   public byte[] engineGenerateSeed(int var1) {
      byte[] var2 = new byte[var1];
      SeedGenerator.generateSeed(var2);
      return var2;
   }

   public synchronized void engineSetSeed(byte[] var1) {
      if (this.state != null) {
         this.digest.update(this.state);

         for(int var2 = 0; var2 < this.state.length; ++var2) {
            this.state[var2] = 0;
         }
      }

      this.state = this.digest.digest(var1);
   }

   private static void updateState(byte[] var0, byte[] var1) {
      int var2 = 1;
      boolean var5 = false;

      for(int var6 = 0; var6 < var0.length; ++var6) {
         int var3 = var0[var6] + var1[var6] + var2;
         byte var4 = (byte)var3;
         var5 |= var0[var6] != var4;
         var0[var6] = var4;
         var2 = var3 >> 8;
      }

      if (!var5) {
         ++var0[0];
      }

   }

   public synchronized void engineNextBytes(byte[] var1) {
      int var2 = 0;
      byte[] var4 = this.remainder;
      if (this.state == null) {
         byte[] var5 = new byte[20];
         SecureRandom.SeederHolder.seeder.engineNextBytes(var5);
         this.state = this.digest.digest(var5);
      }

      int var7 = this.remCount;
      int var3;
      int var6;
      if (var7 > 0) {
         var3 = var1.length - var2 < 20 - var7 ? var1.length - var2 : 20 - var7;

         for(var6 = 0; var6 < var3; ++var6) {
            var1[var6] = var4[var7];
            var4[var7++] = 0;
         }

         this.remCount += var3;
         var2 += var3;
      }

      while(var2 < var1.length) {
         this.digest.update(this.state);
         var4 = this.digest.digest();
         updateState(this.state, var4);
         var3 = var1.length - var2 > 20 ? 20 : var1.length - var2;

         for(var6 = 0; var6 < var3; ++var6) {
            var1[var2++] = var4[var6];
            var4[var6] = 0;
         }

         this.remCount += var3;
      }

      this.remainder = var4;
      this.remCount %= 20;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();

      try {
         this.digest = MessageDigest.getInstance("SHA", "SUN");
      } catch (NoSuchAlgorithmException | NoSuchProviderException var5) {
         try {
            this.digest = MessageDigest.getInstance("SHA");
         } catch (NoSuchAlgorithmException var4) {
            throw new InternalError("internal error: SHA-1 not available.", var4);
         }
      }

   }

   // $FF: synthetic method
   SecureRandom(byte[] var1, Object var2) {
      this(var1);
   }

   private static class SeederHolder {
      private static final SecureRandom seeder = new SecureRandom(SeedGenerator.getSystemEntropy());

      static {
         byte[] var0 = new byte[20];
         SeedGenerator.generateSeed(var0);
         seeder.engineSetSeed(var0);
      }
   }
}
