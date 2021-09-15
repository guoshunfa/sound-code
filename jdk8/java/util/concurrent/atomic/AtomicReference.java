package java.util.concurrent.atomic;

import java.io.Serializable;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import sun.misc.Unsafe;

public class AtomicReference<V> implements Serializable {
   private static final long serialVersionUID = -1848883965231344442L;
   private static final Unsafe unsafe = Unsafe.getUnsafe();
   private static final long valueOffset;
   private volatile V value;

   public AtomicReference(V var1) {
      this.value = var1;
   }

   public AtomicReference() {
   }

   public final V get() {
      return this.value;
   }

   public final void set(V var1) {
      this.value = var1;
   }

   public final void lazySet(V var1) {
      unsafe.putOrderedObject(this, valueOffset, var1);
   }

   public final boolean compareAndSet(V var1, V var2) {
      return unsafe.compareAndSwapObject(this, valueOffset, var1, var2);
   }

   public final boolean weakCompareAndSet(V var1, V var2) {
      return unsafe.compareAndSwapObject(this, valueOffset, var1, var2);
   }

   public final V getAndSet(V var1) {
      return unsafe.getAndSetObject(this, valueOffset, var1);
   }

   public final V getAndUpdate(UnaryOperator<V> var1) {
      Object var2;
      Object var3;
      do {
         var2 = this.get();
         var3 = var1.apply(var2);
      } while(!this.compareAndSet(var2, var3));

      return var2;
   }

   public final V updateAndGet(UnaryOperator<V> var1) {
      Object var2;
      Object var3;
      do {
         var2 = this.get();
         var3 = var1.apply(var2);
      } while(!this.compareAndSet(var2, var3));

      return var3;
   }

   public final V getAndAccumulate(V var1, BinaryOperator<V> var2) {
      Object var3;
      Object var4;
      do {
         var3 = this.get();
         var4 = var2.apply(var3, var1);
      } while(!this.compareAndSet(var3, var4));

      return var3;
   }

   public final V accumulateAndGet(V var1, BinaryOperator<V> var2) {
      Object var3;
      Object var4;
      do {
         var3 = this.get();
         var4 = var2.apply(var3, var1);
      } while(!this.compareAndSet(var3, var4));

      return var4;
   }

   public String toString() {
      return String.valueOf(this.get());
   }

   static {
      try {
         valueOffset = unsafe.objectFieldOffset(AtomicReference.class.getDeclaredField("value"));
      } catch (Exception var1) {
         throw new Error(var1);
      }
   }
}
