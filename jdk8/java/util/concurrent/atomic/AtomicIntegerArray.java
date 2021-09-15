package java.util.concurrent.atomic;

import java.io.Serializable;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;
import sun.misc.Unsafe;

public class AtomicIntegerArray implements Serializable {
   private static final long serialVersionUID = 2862133569453604235L;
   private static final Unsafe unsafe = Unsafe.getUnsafe();
   private static final int base;
   private static final int shift;
   private final int[] array;

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

   public AtomicIntegerArray(int var1) {
      this.array = new int[var1];
   }

   public AtomicIntegerArray(int[] var1) {
      this.array = (int[])var1.clone();
   }

   public final int length() {
      return this.array.length;
   }

   public final int get(int var1) {
      return this.getRaw(this.checkedByteOffset(var1));
   }

   private int getRaw(long var1) {
      return unsafe.getIntVolatile(this.array, var1);
   }

   public final void set(int var1, int var2) {
      unsafe.putIntVolatile(this.array, this.checkedByteOffset(var1), var2);
   }

   public final void lazySet(int var1, int var2) {
      unsafe.putOrderedInt(this.array, this.checkedByteOffset(var1), var2);
   }

   public final int getAndSet(int var1, int var2) {
      return unsafe.getAndSetInt(this.array, this.checkedByteOffset(var1), var2);
   }

   public final boolean compareAndSet(int var1, int var2, int var3) {
      return this.compareAndSetRaw(this.checkedByteOffset(var1), var2, var3);
   }

   private boolean compareAndSetRaw(long var1, int var3, int var4) {
      return unsafe.compareAndSwapInt(this.array, var1, var3, var4);
   }

   public final boolean weakCompareAndSet(int var1, int var2, int var3) {
      return this.compareAndSet(var1, var2, var3);
   }

   public final int getAndIncrement(int var1) {
      return this.getAndAdd(var1, 1);
   }

   public final int getAndDecrement(int var1) {
      return this.getAndAdd(var1, -1);
   }

   public final int getAndAdd(int var1, int var2) {
      return unsafe.getAndAddInt(this.array, this.checkedByteOffset(var1), var2);
   }

   public final int incrementAndGet(int var1) {
      return this.getAndAdd(var1, 1) + 1;
   }

   public final int decrementAndGet(int var1) {
      return this.getAndAdd(var1, -1) - 1;
   }

   public final int addAndGet(int var1, int var2) {
      return this.getAndAdd(var1, var2) + var2;
   }

   public final int getAndUpdate(int var1, IntUnaryOperator var2) {
      long var3 = this.checkedByteOffset(var1);

      int var5;
      int var6;
      do {
         var5 = this.getRaw(var3);
         var6 = var2.applyAsInt(var5);
      } while(!this.compareAndSetRaw(var3, var5, var6));

      return var5;
   }

   public final int updateAndGet(int var1, IntUnaryOperator var2) {
      long var3 = this.checkedByteOffset(var1);

      int var5;
      int var6;
      do {
         var5 = this.getRaw(var3);
         var6 = var2.applyAsInt(var5);
      } while(!this.compareAndSetRaw(var3, var5, var6));

      return var6;
   }

   public final int getAndAccumulate(int var1, int var2, IntBinaryOperator var3) {
      long var4 = this.checkedByteOffset(var1);

      int var6;
      int var7;
      do {
         var6 = this.getRaw(var4);
         var7 = var3.applyAsInt(var6, var2);
      } while(!this.compareAndSetRaw(var4, var6, var7));

      return var6;
   }

   public final int accumulateAndGet(int var1, int var2, IntBinaryOperator var3) {
      long var4 = this.checkedByteOffset(var1);

      int var6;
      int var7;
      do {
         var6 = this.getRaw(var4);
         var7 = var3.applyAsInt(var6, var2);
      } while(!this.compareAndSetRaw(var4, var6, var7));

      return var7;
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
      base = unsafe.arrayBaseOffset(int[].class);
      int var0 = unsafe.arrayIndexScale(int[].class);
      if ((var0 & var0 - 1) != 0) {
         throw new Error("data type scale not a power of two");
      } else {
         shift = 31 - Integer.numberOfLeadingZeros(var0);
      }
   }
}
