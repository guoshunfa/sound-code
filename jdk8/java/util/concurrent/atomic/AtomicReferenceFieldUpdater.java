package java.util.concurrent.atomic;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import sun.misc.Unsafe;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;

public abstract class AtomicReferenceFieldUpdater<T, V> {
   @CallerSensitive
   public static <U, W> AtomicReferenceFieldUpdater<U, W> newUpdater(Class<U> var0, Class<W> var1, String var2) {
      return new AtomicReferenceFieldUpdater.AtomicReferenceFieldUpdaterImpl(var0, var1, var2, Reflection.getCallerClass());
   }

   protected AtomicReferenceFieldUpdater() {
   }

   public abstract boolean compareAndSet(T var1, V var2, V var3);

   public abstract boolean weakCompareAndSet(T var1, V var2, V var3);

   public abstract void set(T var1, V var2);

   public abstract void lazySet(T var1, V var2);

   public abstract V get(T var1);

   public V getAndSet(T var1, V var2) {
      Object var3;
      do {
         var3 = this.get(var1);
      } while(!this.compareAndSet(var1, var3, var2));

      return var3;
   }

   public final V getAndUpdate(T var1, UnaryOperator<V> var2) {
      Object var3;
      Object var4;
      do {
         var3 = this.get(var1);
         var4 = var2.apply(var3);
      } while(!this.compareAndSet(var1, var3, var4));

      return var3;
   }

   public final V updateAndGet(T var1, UnaryOperator<V> var2) {
      Object var3;
      Object var4;
      do {
         var3 = this.get(var1);
         var4 = var2.apply(var3);
      } while(!this.compareAndSet(var1, var3, var4));

      return var4;
   }

   public final V getAndAccumulate(T var1, V var2, BinaryOperator<V> var3) {
      Object var4;
      Object var5;
      do {
         var4 = this.get(var1);
         var5 = var3.apply(var4, var2);
      } while(!this.compareAndSet(var1, var4, var5));

      return var4;
   }

   public final V accumulateAndGet(T var1, V var2, BinaryOperator<V> var3) {
      Object var4;
      Object var5;
      do {
         var4 = this.get(var1);
         var5 = var3.apply(var4, var2);
      } while(!this.compareAndSet(var1, var4, var5));

      return var5;
   }

   private static final class AtomicReferenceFieldUpdaterImpl<T, V> extends AtomicReferenceFieldUpdater<T, V> {
      private static final Unsafe U = Unsafe.getUnsafe();
      private final long offset;
      private final Class<?> cclass;
      private final Class<T> tclass;
      private final Class<V> vclass;

      AtomicReferenceFieldUpdaterImpl(final Class<T> var1, Class<V> var2, final String var3, Class<?> var4) {
         Field var5;
         Class var6;
         int var7;
         try {
            var5 = (Field)AccessController.doPrivileged(new PrivilegedExceptionAction<Field>() {
               public Field run() throws NoSuchFieldException {
                  return var1.getDeclaredField(var3);
               }
            });
            var7 = var5.getModifiers();
            ReflectUtil.ensureMemberAccess(var4, var1, (Object)null, var7);
            ClassLoader var8 = var1.getClassLoader();
            ClassLoader var9 = var4.getClassLoader();
            if (var9 != null && var9 != var8 && (var8 == null || !isAncestor(var8, var9))) {
               ReflectUtil.checkPackageAccess(var1);
            }

            var6 = var5.getType();
         } catch (PrivilegedActionException var10) {
            throw new RuntimeException(var10.getException());
         } catch (Exception var11) {
            throw new RuntimeException(var11);
         }

         if (var2 != var6) {
            throw new ClassCastException();
         } else if (var2.isPrimitive()) {
            throw new IllegalArgumentException("Must be reference type");
         } else if (!Modifier.isVolatile(var7)) {
            throw new IllegalArgumentException("Must be volatile type");
         } else {
            this.cclass = Modifier.isProtected(var7) && var1.isAssignableFrom(var4) && !isSamePackage(var1, var4) ? var4 : var1;
            this.tclass = var1;
            this.vclass = var2;
            this.offset = U.objectFieldOffset(var5);
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

      private final void valueCheck(V var1) {
         if (var1 != null && !this.vclass.isInstance(var1)) {
            throwCCE();
         }

      }

      static void throwCCE() {
         throw new ClassCastException();
      }

      public final boolean compareAndSet(T var1, V var2, V var3) {
         this.accessCheck(var1);
         this.valueCheck(var3);
         return U.compareAndSwapObject(var1, this.offset, var2, var3);
      }

      public final boolean weakCompareAndSet(T var1, V var2, V var3) {
         this.accessCheck(var1);
         this.valueCheck(var3);
         return U.compareAndSwapObject(var1, this.offset, var2, var3);
      }

      public final void set(T var1, V var2) {
         this.accessCheck(var1);
         this.valueCheck(var2);
         U.putObjectVolatile(var1, this.offset, var2);
      }

      public final void lazySet(T var1, V var2) {
         this.accessCheck(var1);
         this.valueCheck(var2);
         U.putOrderedObject(var1, this.offset, var2);
      }

      public final V get(T var1) {
         this.accessCheck(var1);
         return U.getObjectVolatile(var1, this.offset);
      }

      public final V getAndSet(T var1, V var2) {
         this.accessCheck(var1);
         this.valueCheck(var2);
         return U.getAndSetObject(var1, this.offset, var2);
      }
   }
}
