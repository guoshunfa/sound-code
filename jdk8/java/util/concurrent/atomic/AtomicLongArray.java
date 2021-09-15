package java.util.concurrent.atomic;

import java.io.Serializable;
import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;
import sun.misc.Unsafe;

public class AtomicLongArray implements Serializable {
   private static final long serialVersionUID = -2308431214976778248L;
   private static final Unsafe unsafe = Unsafe.getUnsafe();
   private static final int base;
   private static final int shift;
   private final long[] array;

   private long checkedByteOffset(int var1) {
      if (var1 >= 0 && var1 < this.array.length) {
         return byteOffset(var1);
      } else {
         throw new IndexOutOfBoundsException("index " + var1);
      }
   }

   private static long byteOffset(int var0) {
      return ((long)var0 << shift) + (long)base;
   }

   public AtomicLongArray(int var1) {
      this.array = new long[var1];
   }

   public AtomicLongArray(long[] var1) {
      this.array = (long[])var1.clone();
   }

   public final int length() {
      return this.array.length;
   }

   public final long get(int var1) {
      return this.getRaw(this.checkedByteOffset(var1));
   }

   private long getRaw(long var1) {
      return unsafe.getLongVolatile(this.array, var1);
   }

   public final void set(int var1, long var2) {
      unsafe.putLongVolatile(this.array, this.checkedByteOffset(var1), var2);
   }

   public final void lazySet(int var1, long var2) {
      unsafe.putOrderedLong(this.array, this.checkedByteOffset(var1), var2);
   }

   public final long getAndSet(int var1, long var2) {
      return unsafe.getAndSetLong(this.array, this.checkedByteOffset(var1), var2);
   }

   public final boolean compareAndSet(int var1, long var2, long var4) {
      return this.compareAndSetRaw(this.checkedByteOffset(var1), var2, var4);
   }

   private boolean compareAndSetRaw(long var1, long var3, long var5) {
      return unsafe.compareAndSwapLong(this.array, var1, var3, var5);
   }

   public final boolean weakCompareAndSet(int var1, long var2, long var4) {
      return this.compareAndSet(var1, var2, var4);
   }

   public final long getAndIncrement(int var1) {
      return this.getAndAdd(var1, 1L);
   }

   public final long getAndDecrement(int var1) {
      return this.getAndAdd(var1, -1L);
   }

   public final long getAndAdd(int var1, long var2) {
      return unsafe.getAndAddLong(this.array, this.checkedByteOffset(var1), var2);
   }

   public final long incrementAndGet(int var1) {
      return this.getAndAdd(var1, 1L) + 1L;
   }

   public final long decrementAndGet(int var1) {
      return this.getAndAdd(var1, -1L) - 1L;
   }

   public long addAndGet(int var1, long var2) {
      return this.getAndAdd(var1, var2) + var2;
   }

   public final long getAndUpdate(int var1, LongUnaryOperator var2) {
      long var3 = this.checkedByteOffset(var1);

      long var5;
      long var7;
      do {
         var5 = this.getRaw(var3);
         var7 = var2.applyAsLong(var5);
      } while(!this.compareAndSetRaw(var3, var5, var7));

      return var5;
   }

   public final long updateAndGet(int var1, LongUnaryOperator var2) {
      long var3 = this.checkedByteOffset(var1);

      long var5;
      long var7;
      do {
         var5 = this.getRaw(var3);
         var7 = var2.applyAsLong(var5);
      } while(!this.compareAndSetRaw(var3, var5, var7));

      return var7;
   }

   public final long getAndAccumulate(int var1, long var2, LongBinaryOperator var4) {
      long var5 = this.checkedByteOffset(var1);

      long var7;
      long var9;
      do {
         var7 = this.getRaw(var5);
         var9 = var4.applyAsLong(var7, var2);
      } while(!this.compareAndSetRaw(var5, var7, var9));

      return var7;
   }

   public final long accumulateAndGet(int var1, long var2, LongBinaryOperator var4) {
      long var5 = this.checkedByteOffset(var1);

      long var7;
      long var9;
      do {
         var7 = this.getRaw(var5);
         var9 = var4.applyAsLong(var7, var2);
      } while(!this.compareAndSetRaw(var5, var7, var9));

      return var9;
   }

   public String toString() {
      int var1 = this.array.length - 1;
      if (var1 == -1) {
         return "[]";
      } else {
         StringBuilder var2 = new StringBuilder();
         var2.append('[');
         int var3 = 0;

         while(true) {
            var2.append(this.getRaw(byteOffset(var3)));
            if (var3 == var1) {
               return var2.append(']').toString();
            }

            var2.append(',').append(' ');
            ++var3;
         }
      }
   }

   static {
      base = unsafe.arrayBaseOffset(long[].class);
      int var0 = unsafe.arrayIndexScale(long[].class);
      if ((var0 & var0 - 1) != 0) {
         throw new Error("data type scale not a power of two");
      } else {
         shift = 31 - Integer.numberOfLeadingZeros(var0);
      }
   }
}
