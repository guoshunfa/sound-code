package java.util.concurrent.atomic;

import sun.misc.Unsafe;

public class AtomicMarkableReference<V> {
   private volatile AtomicMarkableReference.Pair<V> pair;
   private static final Unsafe UNSAFE = Unsafe.getUnsafe();
   private static final long pairOffset;

   public AtomicMarkableReference(V var1, boolean var2) {
      this.pair = AtomicMarkableReference.Pair.of(var1, var2);
   }

   public V getReference() {
      return this.pair.reference;
   }

   public boolean isMarked() {
      return this.pair.mark;
   }

   public V get(boolean[] var1) {
      AtomicMarkableReference.Pair var2 = this.pair;
      var1[0] = var2.mark;
      return var2.reference;
   }

   public boolean weakCompareAndSet(V var1, V var2, boolean var3, boolean var4) {
      return this.compareAndSet(var1, var2, var3, var4);
   }

   public boolean compareAndSet(V var1, V var2, boolean var3, boolean var4) {
      AtomicMarkableReference.Pair var5 = this.pair;
      return var1 == var5.reference && var3 == var5.mark && (var2 == var5.reference && var4 == var5.mark || this.casPair(var5, AtomicMarkableReference.Pair.of(var2, var4)));
   }

   public void set(V var1, boolean var2) {
      AtomicMarkableReference.Pair var3 = this.pair;
      if (var1 != var3.reference || var2 != var3.mark) {
         this.pair = AtomicMarkableReference.Pair.of(var1, var2);
      }

   }

   public boolean attemptMark(V var1, boolean var2) {
      AtomicMarkableReference.Pair var3 = this.pair;
      return var1 == var3.reference && (var2 == var3.mark || this.casPair(var3, AtomicMarkableReference.Pair.of(var1, var2)));
   }

   private boolean casPair(AtomicMarkableReference.Pair<V> var1, AtomicMarkableReference.Pair<V> var2) {
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
      pairOffset = objectFieldOffset(UNSAFE, "pair", AtomicMarkableReference.class);
   }

   private static class Pair<T> {
      final T reference;
      final boolean mark;

      private Pair(T var1, boolean var2) {
         this.reference = var1;
         this.mark = var2;
      }

      static <T> AtomicMarkableReference.Pair<T> of(T var0, boolean var1) {
         return new AtomicMarkableReference.Pair(var0, var1);
      }
   }
}
