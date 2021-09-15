package java.util.concurrent.locks;

import java.util.concurrent.ThreadLocalRandom;
import sun.misc.Unsafe;

public class LockSupport {
   private static final Unsafe UNSAFE;
   private static final long parkBlockerOffset;
   private static final long SEED;
   private static final long PROBE;
   private static final long SECONDARY;

   private LockSupport() {
   }

   private static void setBlocker(Thread var0, Object var1) {
      UNSAFE.putObject(var0, parkBlockerOffset, var1);
   }

   public static void unpark(Thread var0) {
      if (var0 != null) {
         UNSAFE.unpark(var0);
      }

   }

   public static void park(Object var0) {
      Thread var1 = Thread.currentThread();
      setBlocker(var1, var0);
      UNSAFE.park(false, 0L);
      setBlocker(var1, (Object)null);
   }

   public static void parkNanos(Object var0, long var1) {
      if (var1 > 0L) {
         Thread var3 = Thread.currentThread();
         setBlocker(var3, var0);
         UNSAFE.park(false, var1);
         setBlocker(var3, (Object)null);
      }

   }

   public static void parkUntil(Object var0, long var1) {
      Thread var3 = Thread.currentThread();
      setBlocker(var3, var0);
      UNSAFE.park(true, var1);
      setBlocker(var3, (Object)null);
   }

   public static Object getBlocker(Thread var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return UNSAFE.getObjectVolatile(var0, parkBlockerOffset);
      }
   }

   public static void park() {
      UNSAFE.park(false, 0L);
   }

   public static void parkNanos(long var0) {
      if (var0 > 0L) {
         UNSAFE.park(false, var0);
      }

   }

   public static void parkUntil(long var0) {
      UNSAFE.park(true, var0);
   }

   static final int nextSecondarySeed() {
      Thread var1 = Thread.currentThread();
      int var0;
      if ((var0 = UNSAFE.getInt(var1, SECONDARY)) != 0) {
         var0 ^= var0 << 13;
         var0 ^= var0 >>> 17;
         var0 ^= var0 << 5;
      } else if ((var0 = ThreadLocalRandom.current().nextInt()) == 0) {
         var0 = 1;
      }

      UNSAFE.putInt(var1, SECONDARY, var0);
      return var0;
   }

   static {
      try {
         UNSAFE = Unsafe.getUnsafe();
         Class var0 = Thread.class;
         parkBlockerOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("parkBlocker"));
         SEED = UNSAFE.objectFieldOffset(var0.getDeclaredField("threadLocalRandomSeed"));
         PROBE = UNSAFE.objectFieldOffset(var0.getDeclaredField("threadLocalRandomProbe"));
         SECONDARY = UNSAFE.objectFieldOffset(var0.getDeclaredField("threadLocalRandomSecondarySeed"));
      } catch (Exception var1) {
         throw new Error(var1);
      }
   }
}
