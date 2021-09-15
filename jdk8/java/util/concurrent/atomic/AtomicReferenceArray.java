package java.util.concurrent.atomic;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import sun.misc.Unsafe;

public class AtomicReferenceArray<E> implements Serializable {
   private static final long serialVersionUID = -6209656149925076980L;
   private static final Unsafe unsafe;
   private static final int base;
   private static final int shift;
   private static final long arrayFieldOffset;
   private final Object[] array;

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

   public AtomicReferenceArray(int var1) {
      this.array = new Object[var1];
   }

   public AtomicReferenceArray(E[] var1) {
      this.array = Arrays.copyOf(var1, var1.length, Object[].class);
   }

   public final int length() {
      return this.array.length;
   }

   public final E get(int var1) {
      return this.getRaw(this.checkedByteOffset(var1));
   }

   private E getRaw(long var1) {
      return unsafe.getObjectVolatile(this.array, var1);
   }

   public final void set(int var1, E var2) {
      unsafe.putObjectVolatile(this.array, this.checkedByteOffset(var1), var2);
   }

   public final void lazySet(int var1, E var2) {
      unsafe.putOrderedObject(this.array, this.checkedByteOffset(var1), var2);
   }

   public final E getAndSet(int var1, E var2) {
      return unsafe.getAndSetObject(this.array, this.checkedByteOffset(var1), var2);
   }

   public final boolean compareAndSet(int var1, E var2, E var3) {
      return this.compareAndSetRaw(this.checkedByteOffset(var1), var2, var3);
   }

   private boolean compareAndSetRaw(long var1, E var3, E var4) {
      return unsafe.compareAndSwapObject(this.array, var1, var3, var4);
   }

   public final boolean weakCompareAndSet(int var1, E var2, E var3) {
      return this.compareAndSet(var1, var2, var3);
   }

   public final E getAndUpdate(int var1, UnaryOperator<E> var2) {
      long var3 = this.checkedByteOffset(var1);

      Object var5;
      Object var6;
      do {
         var5 = this.getRaw(var3);
         var6 = var2.apply(var5);
      } while(!this.compareAndSetRaw(var3, var5, var6));

      return var5;
   }

   public final E updateAndGet(int var1, UnaryOperator<E> var2) {
      long var3 = this.checkedByteOffset(var1);

      Object var5;
      Object var6;
      do {
         var5 = this.getRaw(var3);
         var6 = var2.apply(var5);
      } while(!this.compareAndSetRaw(var3, var5, var6));

      return var6;
   }

   public final E getAndAccumulate(int var1, E var2, BinaryOperator<E> var3) {
      long var4 = this.checkedByteOffset(var1);

      Object var6;
      Object var7;
      do {
         var6 = this.getRaw(var4);
         var7 = var3.apply(var6, var2);
      } while(!this.compareAndSetRaw(var4, var6, var7));

      return var6;
   }

   public final E accumulateAndGet(int var1, E var2, BinaryOperator<E> var3) {
      long var4 = this.checkedByteOffset(var1);

      Object var6;
      Object var7;
      do {
         var6 = this.getRaw(var4);
         var7 = var3.apply(var6, var2);
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

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException, InvalidObjectException {
      Object var2 = var1.readFields().get("array", (Object)null);
      if (var2 != null && var2.getClass().isArray()) {
         if (var2.getClass() != Object[].class) {
            var2 = Arrays.copyOf((Object[])((Object[])var2), Array.getLength(var2), Object[].class);
         }

         unsafe.putObjectVolatile(this, arrayFieldOffset, var2);
      } else {
         throw new InvalidObjectException("Not array type");
      }
   }

   static {
      try {
         unsafe = Unsafe.getUnsafe();
         arrayFieldOffset = unsafe.objectFieldOffset(AtomicReferenceArray.class.getDeclaredField("array"));
         base = unsafe.arrayBaseOffset(Object[].class);
         int var0 = unsafe.arrayIndexScale(Object[].class);
         if ((var0 & var0 - 1) != 0) {
            throw new Error("data type scale not a power of two");
         } else {
            shift = 31 - Integer.numberOfLeadingZeros(var0);
         }
      } catch (Exception var1) {
         throw new Error(var1);
      }
   }
}
