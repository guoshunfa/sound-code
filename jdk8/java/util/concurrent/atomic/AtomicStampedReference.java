package java.util.concurrent.atomic;

import sun.misc.Unsafe;

public class AtomicStampedReference<V> {
   private volatile AtomicStampedReference.Pair<V> pair;
   private static final Unsafe UNSAFE = Unsafe.getUnsafe();
   private static final long pairOffset;

   public AtomicStampedReference(V var1, int var2) {
      this.pair = AtomicStampedReference.Pair.of(var1, var2);
   }

   public V getReference() {
      return this.pair.reference;
   }

   public int getStamp() {
      return this.pair.stamp;
   }

   public V get(int[] var1) {
      AtomicStampedReference.Pair var2 = this.pair;
      var1[0] = var2.stamp;
      return var2.reference;
   }

   public boolean weakCompareAndSet(V var1, V var2, int var3, int var4) {
      return this.compareAndSet(var1, var2, var3, var4);
   }

   public boolean compareAndSet(V var1, V var2, int var3, int var4) {
      AtomicStampedReference.Pair var5 = this.pair;
      return var1 == var5.reference && var3 == var5.stamp && (var2 == var5.reference && var4 == var5.stamp || this.casPair(var5, AtomicStampedReference.Pair.of(var2, var4)));
   }

   public void set(V var1, int var2) {
      AtomicStampedReference.Pair var3 = this.pair;
      if (var1 != var3.reference || var2 != var3.stamp) {
         this.pair = AtomicStampedReference.Pair.of(var1, var2);
      }

   }

   public boolean attemptStamp(V var1, int var2) {
      AtomicStampedReference.Pair var3 = this.pair;
      return var1 == var3.reference && (var2 == var3.stamp || this.casPair(var3, AtomicStampedReference.Pair.of(var1, var2)));
   }

   private boolean casPair(AtomicStampedReference.Pair<V> var1, AtomicStampedReference.Pair<V> var2) {
      return UNSAFE.compareAndSwapObject(this, pairOffset, var1, var2);
   }

   static long objectFieldOffset(Unsafe var0, String var1, Class<?> var2) {
      try {
         return var0.objectFieldOffset(var2.getDeclaredField(var1));
      } catch (NoSuchFieldException var5) {
         NoSuchFieldError var4 = new NoSuchFieldError(var1);
         var4.initCause(var5);
         throw var4;
      }
   }

   static {
      pairOffset = objectFieldOffset(UNSAFE, "pair", AtomicStampedReference.class);
   }

   private static class Pair<T> {
      final T reference;
      final int stamp;

      private Pair(T var1, int var2) {
         this.reference = var1;
         this.stamp = var2;
      }

      static <T> AtomicStampedReference.Pair<T> of(T var0, int var1) {
         return new AtomicStampedReference.Pair(var0, var1);
      }
   }
}
