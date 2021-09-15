package java.util;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;
import sun.security.action.GetPropertyAction;

public final class SplittableRandom {
   private static final long GOLDEN_GAMMA = -7046029254386353131L;
   private static final double DOUBLE_UNIT = 1.1102230246251565E-16D;
   private long seed;
   private final long gamma;
   private static final AtomicLong defaultGen = new AtomicLong(initialSeed());
   static final String BadBound = "bound must be positive";
   static final String BadRange = "bound must be greater than origin";
   static final String BadSize = "size must be non-negative";

   private SplittableRandom(long var1, long var3) {
      this.seed = var1;
      this.gamma = var3;
   }

   private static long mix64(long var0) {
      var0 = (var0 ^ var0 >>> 30) * -4658895280553007687L;
      var0 = (var0 ^ var0 >>> 27) * -7723592293110705685L;
      return var0 ^ var0 >>> 31;
   }

   private static int mix32(long var0) {
      var0 = (var0 ^ var0 >>> 33) * 7109453100751455733L;
      return (int)((var0 ^ var0 >>> 28) * -3808689974395783757L >>> 32);
   }

   private static long mixGamma(long var0) {
      var0 = (var0 ^ var0 >>> 33) * -49064778989728563L;
      var0 = (var0 ^ var0 >>> 33) * -4265267296055464877L;
      var0 = var0 ^ var0 >>> 33 | 1L;
      int var2 = Long.bitCount(var0 ^ var0 >>> 1);
      return var2 < 24 ? var0 ^ -6148914691236517206L : var0;
   }

   private long nextSeed() {
      return this.seed += this.gamma;
   }

   private static long initialSeed() {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.util.secureRandomSeed")));
      if (var0 != null && var0.equalsIgnoreCase("true")) {
         byte[] var1 = SecureRandom.getSeed(8);
         long var2 = (long)var1[0] & 255L;

         for(int var4 = 1; var4 < 8; ++var4) {
            var2 = var2 << 8 | (long)var1[var4] & 255L;
         }

         return var2;
      } else {
         return mix64(System.currentTimeMillis()) ^ mix64(System.nanoTime());
      }
   }

   final long internalNextLong(long var1, long var3) {
      long var5 = mix64(this.nextSeed());
      if (var1 < var3) {
         long var7 = var3 - var1;
         long var9 = var7 - 1L;
         if ((var7 & var9) == 0L) {
            var5 = (var5 & var9) + var1;
         } else if (var7 > 0L) {
            for(long var11 = var5 >>> 1; var11 + var9 - (var5 = var11 % var7) < 0L; var11 = mix64(this.nextSeed()) >>> 1) {
            }

            var5 += var1;
         } else {
            while(var5 < var1 || var5 >= var3) {
               var5 = mix64(this.nextSeed());
            }
         }
      }

      return var5;
   }

   final int internalNextInt(int var1, int var2) {
      int var3 = mix32(this.nextSeed());
      if (var1 < var2) {
         int var4 = var2 - var1;
         int var5 = var4 - 1;
         if ((var4 & var5) == 0) {
            var3 = (var3 & var5) + var1;
         } else if (var4 > 0) {
            for(int var6 = var3 >>> 1; var6 + var5 - (var3 = var6 % var4) < 0; var6 = mix32(this.nextSeed()) >>> 1) {
            }

            var3 += var1;
         } else {
            while(var3 < var1 || var3 >= var2) {
               var3 = mix32(this.nextSeed());
            }
         }
      }

      return var3;
   }

   final double internalNextDouble(double var1, double var3) {
      double var5 = (double)(this.nextLong() >>> 11) * 1.1102230246251565E-16D;
      if (var1 < var3) {
         var5 = var5 * (var3 - var1) + var1;
         if (var5 >= var3) {
            var5 = Double.longBitsToDouble(Double.doubleToLongBits(var3) - 1L);
         }
      }

      return var5;
   }

   public SplittableRandom(long var1) {
      this(var1, -7046029254386353131L);
   }

   public SplittableRandom() {
      long var1 = defaultGen.getAndAdd(4354685564936845354L);
      this.seed = mix64(var1);
      this.gamma = mixGamma(var1 + -7046029254386353131L);
   }

   public SplittableRandom split() {
      return new SplittableRandom(this.nextLong(), mixGamma(this.nextSeed()));
   }

   public int nextInt() {
      return mix32(this.nextSeed());
   }

   public int nextInt(int var1) {
      if (var1 <= 0) {
         throw new IllegalArgumentException("bound must be positive");
      } else {
         int var2 = mix32(this.nextSeed());
         int var3 = var1 - 1;
         if ((var1 & var3) == 0) {
            var2 &= var3;
         } else {
            for(int var4 = var2 >>> 1; var4 + var3 - (var2 = var4 % var1) < 0; var4 = mix32(this.nextSeed()) >>> 1) {
            }
         }

         return var2;
      }
   }

   public int nextInt(int var1, int var2) {
      if (var1 >= var2) {
         throw new IllegalArgumentException("bound must be greater than origin");
      } else {
         return this.internalNextInt(var1, var2);
      }
   }

   public long nextLong() {
      return mix64(this.nextSeed());
   }

   public long nextLong(long var1) {
      if (var1 <= 0L) {
         throw new IllegalArgumentException("bound must be positive");
      } else {
         long var3 = mix64(this.nextSeed());
         long var5 = var1 - 1L;
         if ((var1 & var5) == 0L) {
            var3 &= var5;
         } else {
            for(long var7 = var3 >>> 1; var7 + var5 - (var3 = var7 % var1) < 0L; var7 = mix64(this.nextSeed()) >>> 1) {
            }
         }

         return var3;
      }
   }

   public long nextLong(long var1, long var3) {
      if (var1 >= var3) {
         throw new IllegalArgumentException("bound must be greater than origin");
      } else {
         return this.internalNextLong(var1, var3);
      }
   }

   public double nextDouble() {
      return (double)(mix64(this.nextSeed()) >>> 11) * 1.1102230246251565E-16D;
   }

   public double nextDouble(double var1) {
      if (var1 <= 0.0D) {
         throw new IllegalArgumentException("bound must be positive");
      } else {
         double var3 = (double)(mix64(this.nextSeed()) >>> 11) * 1.1102230246251565E-16D * var1;
         return var3 < var1 ? var3 : Double.longBitsToDouble(Double.doubleToLongBits(var1) - 1L);
      }
   }

   public double nextDouble(double var1, double var3) {
      if (var1 >= var3) {
         throw new IllegalArgumentException("bound must be greater than origin");
      } else {
         return this.internalNextDouble(var1, var3);
      }
   }

   public boolean nextBoolean() {
      return mix32(this.nextSeed()) < 0;
   }

   public IntStream ints(long var1) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("size must be non-negative");
      } else {
         return StreamSupport.intStream(new SplittableRandom.RandomIntsSpliterator(this, 0L, var1, Integer.MAX_VALUE, 0), false);
      }
   }

   public IntStream ints() {
      return StreamSupport.intStream(new SplittableRandom.RandomIntsSpliterator(this, 0L, Long.MAX_VALUE, Integer.MAX_VALUE, 0), false);
   }

   public IntStream ints(long var1, int var3, int var4) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("size must be non-negative");
      } else if (var3 >= var4) {
         throw new IllegalArgumentException("bound must be greater than origin");
      } else {
         return StreamSupport.intStream(new SplittableRandom.RandomIntsSpliterator(this, 0L, var1, var3, var4), false);
      }
   }

   public IntStream ints(int var1, int var2) {
      if (var1 >= var2) {
         throw new IllegalArgumentException("bound must be greater than origin");
      } else {
         return StreamSupport.intStream(new SplittableRandom.RandomIntsSpliterator(this, 0L, Long.MAX_VALUE, var1, var2), false);
      }
   }

   public LongStream longs(long var1) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("size must be non-negative");
      } else {
         return StreamSupport.longStream(new SplittableRandom.RandomLongsSpliterator(this, 0L, var1, Long.MAX_VALUE, 0L), false);
      }
   }

   public LongStream longs() {
      return StreamSupport.longStream(new SplittableRandom.RandomLongsSpliterator(this, 0L, Long.MAX_VALUE, Long.MAX_VALUE, 0L), false);
   }

   public LongStream longs(long var1, long var3, long var5) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("size must be non-negative");
      } else if (var3 >= var5) {
         throw new IllegalArgumentException("bound must be greater than origin");
      } else {
         return StreamSupport.longStream(new SplittableRandom.RandomLongsSpliterator(this, 0L, var1, var3, var5), false);
      }
   }

   public LongStream longs(long var1, long var3) {
      if (var1 >= var3) {
         throw new IllegalArgumentException("bound must be greater than origin");
      } else {
         return StreamSupport.longStream(new SplittableRandom.RandomLongsSpliterator(this, 0L, Long.MAX_VALUE, var1, var3), false);
      }
   }

   public DoubleStream doubles(long var1) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("size must be non-negative");
      } else {
         return StreamSupport.doubleStream(new SplittableRandom.RandomDoublesSpliterator(this, 0L, var1, Double.MAX_VALUE, 0.0D), false);
      }
   }

   public DoubleStream doubles() {
      return StreamSupport.doubleStream(new SplittableRandom.RandomDoublesSpliterator(this, 0L, Long.MAX_VALUE, Double.MAX_VALUE, 0.0D), false);
   }

   public DoubleStream doubles(long var1, double var3, double var5) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("size must be non-negative");
      } else if (var3 >= var5) {
         throw new IllegalArgumentException("bound must be greater than origin");
      } else {
         return StreamSupport.doubleStream(new SplittableRandom.RandomDoublesSpliterator(this, 0L, var1, var3, var5), false);
      }
   }

   public DoubleStream doubles(double var1, double var3) {
      if (var1 >= var3) {
         throw new IllegalArgumentException("bound must be greater than origin");
      } else {
         return StreamSupport.doubleStream(new SplittableRandom.RandomDoublesSpliterator(this, 0L, Long.MAX_VALUE, var1, var3), false);
      }
   }

   static final class RandomDoublesSpliterator implements Spliterator.OfDouble {
      final SplittableRandom rng;
      long index;
      final long fence;
      final double origin;
      final double bound;

      RandomDoublesSpliterator(SplittableRandom var1, long var2, long var4, double var6, double var8) {
         this.rng = var1;
         this.index = var2;
         this.fence = var4;
         this.origin = var6;
         this.bound = var8;
      }

      public SplittableRandom.RandomDoublesSpliterator trySplit() {
         long var1 = this.index;
         long var3 = var1 + this.fence >>> 1;
         return var3 <= var1 ? null : new SplittableRandom.RandomDoublesSpliterator(this.rng.split(), var1, this.index = var3, this.origin, this.bound);
      }

      public long estimateSize() {
         return this.fence - this.index;
      }

      public int characteristics() {
         return 17728;
      }

      public boolean tryAdvance(DoubleConsumer var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            long var2 = this.index;
            long var4 = this.fence;
            if (var2 < var4) {
               var1.accept(this.rng.internalNextDouble(this.origin, this.bound));
               this.index = var2 + 1L;
               return true;
            } else {
               return false;
            }
         }
      }

      public void forEachRemaining(DoubleConsumer var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            long var2 = this.index;
            long var4 = this.fence;
            if (var2 < var4) {
               this.index = var4;
               SplittableRandom var6 = this.rng;
               double var7 = this.origin;
               double var9 = this.bound;

               do {
                  var1.accept(var6.internalNextDouble(var7, var9));
               } while(++var2 < var4);
            }

         }
      }
   }

   static final class RandomLongsSpliterator implements Spliterator.OfLong {
      final SplittableRandom rng;
      long index;
      final long fence;
      final long origin;
      final long bound;

      RandomLongsSpliterator(SplittableRandom var1, long var2, long var4, long var6, long var8) {
         this.rng = var1;
         this.index = var2;
         this.fence = var4;
         this.origin = var6;
         this.bound = var8;
      }

      public SplittableRandom.RandomLongsSpliterator trySplit() {
         long var1 = this.index;
         long var3 = var1 + this.fence >>> 1;
         return var3 <= var1 ? null : new SplittableRandom.RandomLongsSpliterator(this.rng.split(), var1, this.index = var3, this.origin, this.bound);
      }

      public long estimateSize() {
         return this.fence - this.index;
      }

      public int characteristics() {
         return 17728;
      }

      public boolean tryAdvance(LongConsumer var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            long var2 = this.index;
            long var4 = this.fence;
            if (var2 < var4) {
               var1.accept(this.rng.internalNextLong(this.origin, this.bound));
               this.index = var2 + 1L;
               return true;
            } else {
               return false;
            }
         }
      }

      public void forEachRemaining(LongConsumer var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            long var2 = this.index;
            long var4 = this.fence;
            if (var2 < var4) {
               this.index = var4;
               SplittableRandom var6 = this.rng;
               long var7 = this.origin;
               long var9 = this.bound;

               do {
                  var1.accept(var6.internalNextLong(var7, var9));
               } while(++var2 < var4);
            }

         }
      }
   }

   static final class RandomIntsSpliterator implements Spliterator.OfInt {
      final SplittableRandom rng;
      long index;
      final long fence;
      final int origin;
      final int bound;

      RandomIntsSpliterator(SplittableRandom var1, long var2, long var4, int var6, int var7) {
         this.rng = var1;
         this.index = var2;
         this.fence = var4;
         this.origin = var6;
         this.bound = var7;
      }

      public SplittableRandom.RandomIntsSpliterator trySplit() {
         long var1 = this.index;
         long var3 = var1 + this.fence >>> 1;
         return var3 <= var1 ? null : new SplittableRandom.RandomIntsSpliterator(this.rng.split(), var1, this.index = var3, this.origin, this.bound);
      }

      public long estimateSize() {
         return this.fence - this.index;
      }

      public int characteristics() {
         return 17728;
      }

      public boolean tryAdvance(IntConsumer var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            long var2 = this.index;
            long var4 = this.fence;
            if (var2 < var4) {
               var1.accept(this.rng.internalNextInt(this.origin, this.bound));
               this.index = var2 + 1L;
               return true;
            } else {
               return false;
            }
         }
      }

      public void forEachRemaining(IntConsumer var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            long var2 = this.index;
            long var4 = this.fence;
            if (var2 < var4) {
               this.index = var4;
               SplittableRandom var6 = this.rng;
               int var7 = this.origin;
               int var8 = this.bound;

               do {
                  var1.accept(var6.internalNextInt(var7, var8));
               } while(++var2 < var4);
            }

         }
      }
   }
}
