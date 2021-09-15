package java.util.concurrent.atomic;

import java.io.Serializable;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;
import sun.misc.Unsafe;

public class AtomicInteger extends Number implements Serializable {
   private static final long serialVersionUID = 6214790243416807050L;
   private static final Unsafe unsafe = Unsafe.getUnsafe();
   private static final long valueOffset;
   private volatile int value;

   public AtomicInteger(int var1) {
      this.value = var1;
   }

   public AtomicInteger() {
   }

   public final int get() {
      return this.value;
   }

   public final void set(int var1) {
      this.value = var1;
   }

   public final void lazySet(int var1) {
      unsafe.putOrderedInt(this, valueOffset, var1);
   }

   public final int getAndSet(int var1) {
      return unsafe.getAndSetInt(this, valueOffset, var1);
   }

   public final boolean compareAndSet(int var1, int var2) {
      return unsafe.compareAndSwapInt(this, valueOffset, var1, var2);
   }

   public final boolean weakCompareAndSet(int var1, int var2) {
      return unsafe.compareAndSwapInt(this, valueOffset, var1, var2);
   }

   public final int getAndIncrement() {
      return unsafe.getAndAddInt(this, valueOffset, 1);
   }

   public final int getAndDecrement() {
      return unsafe.getAndAddInt(this, valueOffset, -1);
   }

   public final int getAndAdd(int var1) {
      return unsafe.getAndAddInt(this, valueOffset, var1);
   }

   public final int incrementAndGet() {
      return unsafe.getAndAddInt(this, valueOffset, 1) + 1;
   }

   public final int decrementAndGet() {
      return unsafe.getAndAddInt(this, valueOffset, -1) - 1;
   }

   public final int addAndGet(int var1) {
      return unsafe.getAndAddInt(this, valueOffset, var1) + var1;
   }

   public final int getAndUpdate(IntUnaryOperator var1) {
      int var2;
      int var3;
      do {
         var2 = this.get();
         var3 = var1.applyAsInt(var2);
      } while(!this.compareAndSet(var2, var3));

      return var2;
   }

   public final int updateAndGet(IntUnaryOperator var1) {
      int var2;
      int var3;
      do {
         var2 = this.get();
         var3 = var1.applyAsInt(var2);
      } while(!this.compareAndSet(var2, var3));

      return var3;
   }

   public final int getAndAccumulate(int var1, IntBinaryOperator var2) {
      int var3;
      int var4;
      do {
         var3 = this.get();
         var4 = var2.applyAsInt(var3, var1);
      } while(!this.compareAndSet(var3, var4));

      return var3;
   }

   public final int accumulateAndGet(int var1, IntBinaryOperator var2) {
      int var3;
      int var4;
      do {
         var3 = this.get();
         var4 = var2.applyAsInt(var3, var1);
      } while(!this.compareAndSet(var3, var4));

      return var4;
   }

   public String toString() {
      return Integer.toString(this.get());
   }

   public int intValue() {
      return this.get();
   }

   public long longValue() {
      return (long)this.get();
   }

   public float floatValue() {
      return (float)this.get();
   }

   public double doubleValue() {
      return (double)this.get();
   }

   static {
      try {
         valueOffset = unsafe.objectFieldOffset(AtomicInteger.class.getDeclaredField("value"));
      } catch (Exception var1) {
         throw new Error(var1);
      }
   }
}
