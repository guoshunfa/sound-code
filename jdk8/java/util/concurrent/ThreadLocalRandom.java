package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.SecureRandom;
import java.util.Random;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;
import sun.misc.Unsafe;
import sun.misc.VM;

public class ThreadLocalRandom extends Random {
   private static final AtomicInteger probeGenerator = new AtomicInteger();
   private static final AtomicLong seeder = new AtomicLong(initialSeed());
   private static final long GAMMA = -7046029254386353131L;
   private static final int PROBE_INCREMENT = -1640531527;
   private static final long SEEDER_INCREMENT = -4942790177534073029L;
   private static final double DOUBLE_UNIT = 1.1102230246251565E-16D;
   private static final float FLOAT_UNIT = 5.9604645E-8F;
   private static final ThreadLocal<Double> nextLocalGaussian = new ThreadLocal();
   boolean initialized = true;
   static final ThreadLocalRandom instance = new ThreadLocalRandom();
   static final String BadBound = "bound must be positive";
   static final String BadRange = "bound must be greater than origin";
   static final String BadSize = "size must be non-negative";
   private static final long serialVersionUID = -5851777807851030925L;
   private static final ObjectStreamField[] serialPersistentFields;
   private static final Unsafe UNSAFE;
   private static final long SEED;
   private static final long PROBE;
   private static final long SECONDARY;

   private static long initialSeed() {
      String var0 = VM.getSavedProperty("java.util.secureRandomSeed");
      if (!Boolean.parseBoolean(var0)) {
         return mix64(System.currentTimeMillis()) ^ mix64(System.nanoTime());
      } else {
         byte[] var1 = SecureRandom.getSeed(8);
         long var2 = (long)var1[0] & 255L;

         for(int var4 = 1; var4 < 8; ++var4) {
            var2 = var2 << 8 | (long)var1[var4] & 255L;
         }

         return var2;
      }
   }

   private static long mix64(long var0) {
      var0 = (var0 ^ var0 >>> 33) * -49064778989728563L;
      var0 = (var0 ^ var0 >>> 33) * -4265267296055464877L;
      return var0 ^ var0 >>> 33;
   }

   private static int mix32(long var0) {
      var0 = (var0 ^ var0 >>> 33) * -49064778989728563L;
      return (int)((var0 ^ var0 >>> 33) * -4265267296055464877L >>> 32);
   }

   private ThreadLocalRandom() {
   }

   static final void localInit() {
      int var0 = probeGenerator.addAndGet(-1640531527);
      int var1 = var0 == 0 ? 1 : var0;
      long var2 = mix64(seeder.getAndAdd(-4942790177534073029L));
      Thread var4 = Thread.currentThread();
      UNSAFE.putLong(var4, SEED, var2);
      UNSAFE.putInt(var4, PROBE, var1);
   }

   public static ThreadLocalRandom current() {
      if (UNSAFE.getInt(Thread.currentThread(), PROBE) == 0) {
         localInit();
      }

      return instance;
   }

   public void setSeed(long var1) {
      if (this.initialized) {
         throw new UnsupportedOperationException();
      }
   }

   final long nextSeed() {
      Thread var1;
      long var2;
      UNSAFE.putLong(var1 = Thread.currentThread(), SEED, var2 = UNSAFE.getLong(var1, SEED) + -7046029254386353131L);
      return var2;
   }

   protected int next(int var1) {
      return (int)(mix64(this.nextSeed()) >>> 64 - var1);
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

   public float nextFloat() {
      return (float)(mix32(this.nextSeed()) >>> 8) * 5.9604645E-8F;
   }

   public double nextGaussian() {
      Double var1 = (Double)nextLocalGaussian.get();
      if (var1 != null) {
         nextLocalGaussian.set((Object)null);
         return var1;
      } else {
         double var2;
         double var4;
         double var6;
         do {
            do {
               var2 = 2.0D * this.nextDouble() - 1.0D;
               var4 = 2.0D * this.nextDouble() - 1.0D;
               var6 = var2 * var2 + var4 * var4;
            } while(var6 >= 1.0D);
         } while(var6 == 0.0D);

         double var8 = StrictMath.sqrt(-2.0D * StrictMath.log(var6) / var6);
         nextLocalGaussian.set(new Double(var4 * var8));
         return var2 * var8;
      }
   }

   public IntStream ints(long var1) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("size must be non-negative");
      } else {
         return StreamSupport.intStream(new ThreadLocalRandom.RandomIntsSpliterator(0L, var1, Integer.MAX_VALUE, 0), false);
      }
   }

   public IntStream ints() {
      return StreamSupport.intStream(new ThreadLocalRandom.RandomIntsSpliterator(0L, Long.MAX_VALUE, Integer.MAX_VALUE, 0), false);
   }

   public IntStream ints(long var1, int var3, int var4) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("size must be non-negative");
      } else if (var3 >= var4) {
         throw new IllegalArgumentException("bound must be greater than origin");
      } else {
         return StreamSupport.intStream(new ThreadLocalRandom.RandomIntsSpliterator(0L, var1, var3, var4), false);
      }
   }

   public IntStream ints(int var1, int var2) {
      if (var1 >= var2) {
         throw new IllegalArgumentException("bound must be greater than origin");
      } else {
         return StreamSupport.intStream(new ThreadLocalRandom.RandomIntsSpliterator(0L, Long.MAX_VALUE, var1, var2), false);
      }
   }

   public LongStream longs(long var1) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("size must be non-negative");
      } else {
         return StreamSupport.longStream(new ThreadLocalRandom.RandomLongsSpliterator(0L, var1, Long.MAX_VALUE, 0L), false);
      }
   }

   public LongStream longs() {
      return StreamSupport.longStream(new ThreadLocalRandom.RandomLongsSpliterator(0L, Long.MAX_VALUE, Long.MAX_VALUE, 0L), false);
   }

   public LongStream longs(long var1, long var3, long var5) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("size must be non-negative");
      } else if (var3 >= var5) {
         throw new IllegalArgumentException("bound must be greater than origin");
      } else {
         return StreamSupport.longStream(new ThreadLocalRandom.RandomLongsSpliterator(0L, var1, var3, var5), false);
      }
   }

   public LongStream longs(long var1, long var3) {
      if (var1 >= var3) {
         throw new IllegalArgumentException("bound must be greater than origin");
      } else {
         return StreamSupport.longStream(new ThreadLocalRandom.RandomLongsSpliterator(0L, Long.MAX_VALUE, var1, var3), false);
      }
   }

   public DoubleStream doubles(long var1) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("size must be non-negative");
      } else {
         return StreamSupport.doubleStream(new ThreadLocalRandom.RandomDoublesSpliterator(0L, var1, Double.MAX_VALUE, 0.0D), false);
      }
   }

   public DoubleStream doubles() {
      return StreamSupport.doubleStream(new ThreadLocalRandom.RandomDoublesSpliterator(0L, Long.MAX_VALUE, Double.MAX_VALUE, 0.0D), false);
   }

   public DoubleStream doubles(long var1, double var3, double var5) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("size must be non-negative");
      } else if (var3 >= var5) {
         throw new IllegalArgumentException("bound must be greater than origin");
      } else {
         return StreamSupport.doubleStream(new ThreadLocalRandom.RandomDoublesSpliterator(0L, var1, var3, var5), false);
      }
   }

   public DoubleStream doubles(double var1, double var3) {
      if (var1 >= var3) {
         throw new IllegalArgumentException("bound must be greater than origin");
      } else {
         return StreamSupport.doubleStream(new ThreadLocalRandom.RandomDoublesSpliterator(0L, Long.MAX_VALUE, var1, var3), false);
      }
   }

   static final int getProbe() {
      return UNSAFE.getInt(Thread.currentThread(), PROBE);
   }

   static final int advanceProbe(int var0) {
      var0 ^= var0 << 13;
      var0 ^= var0 >>> 17;
      var0 ^= var0 << 5;
      UNSAFE.putInt(Thread.currentThread(), PROBE, var0);
      return var0;
   }

   static final int nextSecondarySeed() {
      Thread var1 = Thread.currentThread();
      int var0;
      if ((var0 = UNSAFE.getInt(var1, SECONDARY)) != 0) {
         var0 ^= var0 << 13;
         var0 ^= var0 >>> 17;
         var0 ^= var0 << 5;
      } else {
         localInit();
         if ((var0 = (int)UNSAFE.getLong(var1, SEED)) == 0) {
            var0 = 1;
         }
      }

      UNSAFE.putInt(var1, SECONDARY, var0);
      return var0;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      ObjectOutputStream.PutField var2 = var1.putFields();
      var2.put("rnd", UNSAFE.getLong(Thread.currentThread(), SEED));
      var2.put("initialized", true);
      var1.writeFields();
   }

   private Object readResolve() {
      return current();
   }

   static {
      serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("rnd", Long.TYPE), new ObjectStreamField("initialized", Boolean.TYPE)};

      try {
         UNSAFE = Unsafe.getUnsafe();
         Class var0 = Thread.class;
         SEED = UNSAFE.objectFieldOffset(var0.getDeclaredField("threadLocalRandomSeed"));
         PROBE = UNSAFE.objectFieldOffset(var0.getDeclaredField("threadLocalRandomProbe"));
         SECONDARY = UNSAFE.objectFieldOffset(var0.getDeclaredField("threadLocalRandomSecondarySeed"));
      } catch (Exception var1) {
         throw new Error(var1);
      }
   }

   static final class RandomDoublesSpliterator implements Spliterator.OfDouble {
      long index;
      final long fence;
      final double origin;
      final double bound;

      RandomDoublesSpliterator(long var1, long var3, double var5, double var7) {
         this.index = var1;
         this.fence = var3;
         this.origin = var5;
         this.bound = var7;
      }

      public ThreadLocalRandom.RandomDoublesSpliterator trySplit() {
         long var1 = this.index;
         long var3 = var1 + this.fence >>> 1;
         return var3 <= var1 ? null : new ThreadLocalRandom.RandomDoublesSpliterator(var1, this.index = var3, this.origin, this.bound);
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
               var1.accept(ThreadLocalRandom.current().internalNextDouble(this.origin, this.bound));
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
               double var6 = this.origin;
               double var8 = this.bound;
               ThreadLocalRandom var10 = ThreadLocalRandom.current();

               do {
                  var1.accept(var10.internalNextDouble(var6, var8));
               } while(++var2 < var4);
            }

         }
      }
   }

   static final class RandomLongsSpliterator implements Spliterator.OfLong {
      long index;
      final long fence;
      final long origin;
      final long bound;

      RandomLongsSpliterator(long var1, long var3, long var5, long var7) {
         this.index = var1;
         this.fence = var3;
         this.origin = var5;
         this.bound = var7;
      }

      public ThreadLocalRandom.RandomLongsSpliterator trySplit() {
         long var1 = this.index;
         long var3 = var1 + this.fence >>> 1;
         return var3 <= var1 ? null : new ThreadLocalRandom.RandomLongsSpliterator(var1, this.index = var3, this.origin, this.bound);
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
               var1.accept(ThreadLocalRandom.current().internalNextLong(this.origin, this.bound));
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
               long var6 = this.origin;
               long var8 = this.bound;
               ThreadLocalRandom var10 = ThreadLocalRandom.current();

               do {
                  var1.accept(var10.internalNextLong(var6, var8));
               } while(++var2 < var4);
            }

         }
      }
   }

   static final class RandomIntsSpliterator implements Spliterator.OfInt {
      long index;
      final long fence;
      final int origin;
      final int bound;

      RandomIntsSpliterator(long var1, long var3, int var5, int var6) {
         this.index = var1;
         this.fence = var3;
         this.origin = var5;
         this.bound = var6;
      }

      public ThreadLocalRandom.RandomIntsSpliterator trySplit() {
         long var1 = this.index;
         long var3 = var1 + this.fence >>> 1;
         return var3 <= var1 ? null : new ThreadLocalRandom.RandomIntsSpliterator(var1, this.index = var3, this.origin, this.bound);
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
               var1.accept(ThreadLocalRandom.current().internalNextInt(this.origin, this.bound));
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
               int var6 = this.origin;
               int var7 = this.bound;
               ThreadLocalRandom var8 = ThreadLocalRandom.current();

               do {
                  var1.accept(var8.internalNextInt(var6, var7));
               } while(++var2 < var4);
            }

         }
      }
   }
}
