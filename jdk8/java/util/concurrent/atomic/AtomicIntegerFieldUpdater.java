package java.util.concurrent.atomic;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Objects;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;
import sun.misc.Unsafe;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;

public abstract class AtomicIntegerFieldUpdater<T> {
   @CallerSensitive
   public static <U> AtomicIntegerFieldUpdater<U> newUpdater(Class<U> var0, String var1) {
      return new AtomicIntegerFieldUpdater.AtomicIntegerFieldUpdaterImpl(var0, var1, Reflection.getCallerClass());
   }

   protected AtomicIntegerFieldUpdater() {
   }

   public abstract boolean compareAndSet(T var1, int var2, int var3);

   public abstract boolean weakCompareAndSet(T var1, int var2, int var3);

   public abstract void set(T var1, int var2);

   public abstract void lazySet(T var1, int var2);

   public abstract int get(T var1);

   public int getAndSet(T var1, int var2) {
      int var3;
      do {
         var3 = this.get(var1);
      } while(!this.compareAndSet(var1, var3, var2));

      return var3;
   }

   public int getAndIncrement(T var1) {
      int var2;
      int var3;
      do {
         var2 = this.get(var1);
         var3 = var2 + 1;
      } while(!this.compareAndSet(var1, var2, var3));

      return var2;
   }

   public int getAndDecrement(T var1) {
      int var2;
      int var3;
      do {
         var2 = this.get(var1);
         var3 = var2 - 1;
      } while(!this.compareAndSet(var1, var2, var3));

      return var2;
   }

   public int getAndAdd(T var1, int var2) {
      int var3;
      int var4;
      do {
         var3 = this.get(var1);
         var4 = var3 + var2;
      } while(!this.compareAndSet(var1, var3, var4));

      return var3;
   }

   public int incrementAndGet(T var1) {
      int var2;
      int var3;
      do {
         var2 = this.get(var1);
         var3 = var2 + 1;
      } while(!this.compareAndSet(var1, var2, var3));

      return var3;
   }

   public int decrementAndGet(T var1) {
      int var2;
      int var3;
      do {
         var2 = this.get(var1);
         var3 = var2 - 1;
      } while(!this.compareAndSet(var1, var2, var3));

      return var3;
   }

   public int addAndGet(T var1, int var2) {
      int var3;
      int var4;
      do {
         var3 = this.get(var1);
         var4 = var3 + var2;
      } while(!this.compareAndSet(var1, var3, var4));

      return var4;
   }

   public final int getAndUpdate(T var1, IntUnaryOperator var2) {
      int var3;
      int var4;
      do {
         var3 = this.get(var1);
         var4 = var2.applyAsInt(var3);
      } while(!this.compareAndSet(var1, var3, var4));

      return var3;
   }

   public final int updateAndGet(T var1, IntUnaryOperator var2) {
      int var3;
      int var4;
      do {
         var3 = this.get(var1);
         var4 = var2.applyAsInt(var3);
      } while(!this.compareAndSet(var1, var3, var4));

      return var4;
   }

   public final int getAndAccumulate(T var1, int var2, IntBinaryOperator var3) {
      int var4;
      int var5;
      do {
         var4 = this.get(var1);
         var5 = var3.applyAsInt(var4, var2);
      } while(!this.compareAndSet(var1, var4, var5));

      return var4;
   }

   public final int accumulateAndGet(T var1, int var2, IntBinaryOperator var3) {
      int var4;
      int var5;
      do {
         var4 = this.get(var1);
         var5 = var3.applyAsInt(var4, var2);
      } while(!this.compareAndSet(var1, var4, var5));

      return var5;
   }

   private static final class AtomicIntegerFieldUpdaterImpl<T> extends AtomicIntegerFieldUpdater<T> {
      private static final Unsafe U = Unsafe.getUnsafe();
      private final long offset;
      private final Class<?> cclass;
      private final Class<T> tclass;

      AtomicIntegerFieldUpdaterImpl(final Class<T> var1, final String var2, Class<?> var3) {
         Field var4;
         int var5;
         try {
            var4 = (Field)AccessController.doPrivileged(new PrivilegedExceptionAction<Field>() {
               public Field run() throws NoSuchFieldException {
                  return var1.getDeclaredField(var2);
               }
            });
            var5 = var4.getModifiers();
            ReflectUtil.ensureMemberAccess(var3, var1, (Object)null, var5);
            ClassLoader var6 = var1.getClassLoader();
            ClassLoader var7 = var3.getClassLoader();
            if (var7 != null && var7 != var6 && (var6 == null || !isAncestor(var6, var7))) {
               ReflectUtil.checkPackageAccess(var1);
            }
         } catch (PrivilegedActionException var8) {
            throw new RuntimeException(var8.getException());
         } catch (Exception var9) {
            throw new RuntimeException(var9);
         }

         if (var4.getType() != Integer.TYPE) {
            throw new IllegalArgumentException("Must be integer type");
         } else if (!Modifier.isVolatile(var5)) {
            throw new IllegalArgumentException("Must be volatile type");
         } else {
            this.cclass = Modifier.isProtected(var5) && var1.isAssignableFrom(var3) && !isSamePackage(var1, var3) ? var3 : var1;
            this.tclass = var1;
            this.offset = U.objectFieldOffset(var4);
         }
      }

      private static boolean isAncestor(ClassLoader var0, ClassLoader var1) {
         ClassLoader var2 = var0;

         do {
            var2 = var2.getParent();
            if (var1 == var2) {
               return true;
            }
         } while(var2 != null);

         return false;
      }

      private static boolean isSamePackage(Class<?> var0, Class<?> var1) {
         return var0.getClassLoader() == var1.getClassLoader() && Objects.equals(getPackageName(var0), getPackageName(var1));
      }

      private static String getPackageName(Class<?> var0) {
         String var1 = var0.getName();
         int var2 = var1.lastIndexOf(46);
         return var2 != -1 ? var1.substring(0, var2) : "";
      }

      private final void accessCheck(T var1) {
         if (!this.cclass.isInstance(var1)) {
            this.throwAccessCheckException(var1);
         }

      }

      private final void throwAccessCheckException(T var1) {
         if (this.cclass == this.tclass) {
            throw new ClassCastException();
         } else {
            throw new RuntimeException(new IllegalAccessException("Class " + this.cclass.getName() + " can not access a protected member of class " + this.tclass.getName() + " using an instance of " + var1.getClass().getName()));
         }
      }

      public final boolean compareAndSet(T var1, int var2, int var3) {
         this.accessCheck(var1);
         return U.compareAndSwapInt(var1, this.offset, var2, var3);
      }

      public final boolean weakCompareAndSet(T var1, int var2, int var3) {
         this.accessCheck(var1);
         return U.compareAndSwapInt(var1, this.offset, var2, var3);
      }

      public final void set(T var1, int var2) {
         this.accessCheck(var1);
         U.putIntVolatile(var1, this.offset, var2);
      }

      public final void lazySet(T var1, int var2) {
         this.accessCheck(var1);
         U.putOrderedInt(var1, this.offset, var2);
      }

      public final int get(T var1) {
         this.accessCheck(var1);
         return U.getIntVolatile(var1, this.offset);
      }

      public final int getAndSet(T var1, int var2) {
         this.accessCheck(var1);
         return U.getAndSetInt(var1, this.offset, var2);
      }

      public final int getAndAdd(T var1, int var2) {
         this.accessCheck(var1);
         return U.getAndAddInt(var1, this.offset, var2);
      }

      public final int getAndIncrement(T var1) {
         return this.getAndAdd(var1, 1);
      }

      public final int getAndDecrement(T var1) {
         return this.getAndAdd(var1, -1);
      }

      public final int incrementAndGet(T var1) {
         return this.getAndAdd(var1, 1) + 1;
      }

      public final int decrementAndGet(T var1) {
         return this.getAndAdd(var1, -1) - 1;
      }

      public final int addAndGet(T var1, int var2) {
         return this.getAndAdd(var1, var2) + var2;
      }
   }
}
