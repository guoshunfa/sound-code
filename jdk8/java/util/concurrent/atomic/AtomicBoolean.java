package java.util.concurrent.atomic;

import java.io.Serializable;
import sun.misc.Unsafe;

public class AtomicBoolean implements Serializable {
   private static final long serialVersionUID = 4654671469794556979L;
   private static final Unsafe unsafe = Unsafe.getUnsafe();
   private static final long valueOffset;
   private volatile int value;

   public AtomicBoolean(boolean var1) {
      this.value = var1 ? 1 : 0;
   }

   public AtomicBoolean() {
   }

   public final boolean get() {
      return this.value != 0;
   }

   public final boolean compareAndSet(boolean var1, boolean var2) {
      int var3 = var1 ? 1 : 0;
      int var4 = var2 ? 1 : 0;
      return unsafe.compareAndSwapInt(this, valueOffset, var3, var4);
   }

   public boolean weakCompareAndSet(boolean var1, boolean var2) {
      int var3 = var1 ? 1 : 0;
      int var4 = var2 ? 1 : 0;
      return unsafe.compareAndSwapInt(this, valueOffset, var3, var4);
   }

   public final void set(boolean var1) {
      this.value = var1 ? 1 : 0;
   }

   public final void lazySet(boolean var1) {
      int var2 = var1 ? 1 : 0;
      unsafe.putOrderedInt(this, valueOffset, var2);
   }

   public final boolean getAndSet(boolean var1) {
      boolean var2;
      do {
         var2 = this.get();
      } while(!this.compareAndSet(var2, var1));

      return var2;
   }

   public String toString() {
      return Boolean.toString(this.get());
   }

   static {
      try {
         valueOffset = unsafe.objectFieldOffset(AtomicBoolean.class.getDeclaredField("value"));
      } catch (Exception var1) {
         throw new Error(var1);
      }
   }
}
