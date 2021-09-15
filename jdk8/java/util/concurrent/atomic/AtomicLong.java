package java.util.concurrent.atomic;

import java.io.Serializable;
import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;
import sun.misc.Unsafe;

public class AtomicLong extends Number implements Serializable {
   private static final long serialVersionUID = 1927816293512124184L;
   private static final Unsafe unsafe = Unsafe.getUnsafe();
   private static final long valueOffset;
   static final boolean VM_SUPPORTS_LONG_CAS = VMSupportsCS8();
   private volatile long value;

   private static native boolean VMSupportsCS8();

   public AtomicLong(long var1) {
      this.value = var1;
   }

   public AtomicLong() {
   }

   public final long get() {
      return this.value;
   }

   public final void set(long var1) {
      this.value = var1;
   }

   public final void lazySet(long var1) {
      unsafe.putOrderedLong(this, valueOffset, var1);
   }

   public final long getAndSet(long var1) {
      return unsafe.getAndSetLong(this, valueOffset, var1);
   }

   public final boolean compareAndSet(long var1, long var3) {
      return unsafe.compareAndSwapLong(this, valueOffset, var1, var3);
   }

   public final boolean weakCompareAndSet(long var1, long var3) {
      return unsafe.compareAndSwapLong(this, valueOffset, var1, var3);
   }

   public final long getAndIncrement() {
      return unsafe.getAndAddLong(this, valueOffset, 1L);
   }

   public final long getAndDecrement() {
      return unsafe.getAndAddLong(this, valueOffset, -1L);
   }

   public final long getAndAdd(long var1) {
      return unsafe.getAndAddLong(this, valueOffset, var1);
   }

   public final long incrementAndGet() {
      return unsafe.getAndAddLong(this, valueOffset, 1L) + 1L;
   }

   public final long decrementAndGet() {
      return unsafe.getAndAddLong(this, valueOffset, -1L) - 1L;
   }

   public final long addAndGet(long var1) {
      return unsafe.getAndAddLong(this, valueOffset, var1) + var1;
   }

   public final long getAndUpdate(LongUnaryOperator var1) {
      long var2;
      long var4;
      do {
         var2 = this.get();
         var4 = var1.applyAsLong(var2);
      } while(!this.compareAndSet(var2, var4));

      return var2;
   }

   public final long updateAndGet(LongUnaryOperator var1) {
      long var2;
      long var4;
      do {
         var2 = this.get();
         var4 = var1.applyAsLong(var2);
      } while(!this.compareAndSet(var2, var4));

      return var4;
   }

   public final long getAndAccumulate(long var1, LongBinaryOperator var3) {
      long var4;
      long var6;
      do {
         var4 = this.get();
         var6 = var3.applyAsLong(var4, var1);
      } while(!this.compareAndSet(var4, var6));

      return var4;
   }

   public final long accumulateAndGet(long var1, LongBinaryOperator var3) {
      long var4;
      long var6;
      do {
         var4 = this.get();
         var6 = var3.applyAsLong(var4, var1);
      } while(!this.compareAndSet(var4, var6));

      return var6;
   }

   public String toString() {
      return Long.toString(this.get());
   }

   public int intValue() {
      return (int)this.get();
   }

   public long longValue() {
      return this.get();
   }

   public float floatValue() {
      return (float)this.get();
   }

   public double doubleValue() {
      return (double)this.get();
   }

   static {
      try {
         valueOffset = unsafe.objectFieldOffset(AtomicLong.class.getDeclaredField("value"));
      } catch (Exception var1) {
         throw new Error(var1);
      }
   }
}
