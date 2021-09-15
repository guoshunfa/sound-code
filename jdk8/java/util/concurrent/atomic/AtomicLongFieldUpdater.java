package java.util.concurrent.atomic;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Objects;
import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;
import sun.misc.Unsafe;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;

public abstract class AtomicLongFieldUpdater<T> {
   @CallerSensitive
   public static <U> AtomicLongFieldUpdater<U> newUpdater(Class<U> var0, String var1) {
      Class var2 = Reflection.getCallerClass();
      return (AtomicLongFieldUpdater)(AtomicLong.VM_SUPPORTS_LONG_CAS ? new AtomicLongFieldUpdater.CASUpdater(var0, var1, var2) : new AtomicLongFieldUpdater.LockedUpdater(var0, var1, var2));
   }

   protected AtomicLongFieldUpdater() {
   }

   public abstract boolean compareAndSet(T var1, long var2, long var4);

   public abstract boolean weakCompareAndSet(T var1, long var2, long var4);

   public abstract void set(T var1, long var2);

   public abstract void lazySet(T var1, long var2);

   public abstract long get(T var1);

   public long getAndSet(T var1, long var2) {
      long var4;
      do {
         var4 = this.get(var1);
      } while(!this.compareAndSet(var1, var4, var2));

      return var4;
   }

   public long getAndIncrement(T var1) {
      long var2;
      long var4;
      do {
         var2 = this.get(var1);
         var4 = var2 + 1L;
      } while(!this.compareAndSet(var1, var2, var4));

      return var2;
   }

   public long getAndDecrement(T var1) {
      long var2;
      long var4;
      do {
         var2 = this.get(var1);
         var4 = var2 - 1L;
      } while(!this.compareAndSet(var1, var2, var4));

      return var2;
   }

   public long getAndAdd(T var1, long var2) {
      long var4;
      long var6;
      do {
         var4 = this.get(var1);
         var6 = var4 + var2;
      } while(!this.compareAndSet(var1, var4, var6));

      return var4;
   }

   public long incrementAndGet(T var1) {
      long var2;
      long var4;
      do {
         var2 = this.get(var1);
         var4 = var2 + 1L;
      } while(!this.compareAndSet(var1, var2, var4));

      return var4;
   }

   public long decrementAndGet(T var1) {
      long var2;
      long var4;
      do {
         var2 = this.get(var1);
         var4 = var2 - 1L;
      } while(!this.compareAndSet(var1, var2, var4));

      return var4;
   }

   public long addAndGet(T var1, long var2) {
      long var4;
      long var6;
      do {
         var4 = this.get(var1);
         var6 = var4 + var2;
      } while(!this.compareAndSet(var1, var4, var6));

      return var6;
   }

   public final long getAndUpdate(T var1, LongUnaryOperator var2) {
      long var3;
      long var5;
      do {
         var3 = this.get(var1);
         var5 = var2.applyAsLong(var3);
      } while(!this.compareAndSet(var1, var3, var5));

      return var3;
   }

   public final long updateAndGet(T var1, LongUnaryOperator var2) {
      long var3;
      long var5;
      do {
         var3 = this.get(var1);
         var5 = var2.applyAsLong(var3);
      } while(!this.compareAndSet(var1, var3, var5));

      return var5;
   }

   public final long getAndAccumulate(T var1, long var2, LongBinaryOperator var4) {
      long var5;
      long var7;
      do {
         var5 = this.get(var1);
         var7 = var4.applyAsLong(var5, var2);
      } while(!this.compareAndSet(var1, var5, var7));

      return var5;
   }

   public final long accumulateAndGet(T var1, long var2, LongBinaryOperator var4) {
      long var5;
      long var7;
      do {
         var5 = this.get(var1);
         var7 = var4.applyAsLong(var5, var2);
      } while(!this.compareAndSet(var1, var5, var7));

      return var7;
   }

   static boolean isAncestor(ClassLoader var0, ClassLoader var1) {
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

   private static final class LockedUpdater<T> extends AtomicLongFieldUpdater<T> {
      private static final Unsafe U = Unsafe.getUnsafe();
      private final long offset;
      private final Class<?> cclass;
      private final Class<T> tclass;

      LockedUpdater(final Class<T> var1, final String var2, Class<?> var3) {
         Field var4 = null;
         boolean var5 = false;

         int var10;
         try {
            var4 = (Field)AccessController.doPrivileged(new PrivilegedExceptionAction<Field>() {
               public Field run() throws NoSuchFieldException {
                  return var1.getDeclaredField(var2);
               }
            });
            var10 = var4.getModifiers();
            ReflectUtil.ensureMemberAccess(var3, var1, (Object)null, var10);
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

         if (var4.getType() != Long.TYPE) {
            throw new IllegalArgumentException("Must be long type");
         } else if (!Modifier.isVolatile(var10)) {
            throw new IllegalArgumentException("Must be volatile type");
         } else {
            this.cclass = Modifier.isProtected(var10) && var1.isAssignableFrom(var3) && !AtomicLongFieldUpdater.isSamePackage(var1, var3) ? var3 : var1;
            this.tclass = var1;
            this.offset = U.objectFieldOffset(var4);
         }
      }

      private final void accessCheck(T var1) {
         if (!this.cclass.isInstance(var1)) {
            throw this.accessCheckException(var1);
         }
      }

      private final RuntimeException accessCheckException(T var1) {
         return (RuntimeException)(this.cclass == this.tclass ? new ClassCastException() : new RuntimeException(new IllegalAccessException("Class " + this.cclass.getName() + " can not access a protected member of class " + this.tclass.getName() + " using an instance of " + var1.getClass().getName())));
      }

      public final boolean compareAndSet(T var1, long var2, long var4) {
         this.accessCheck(var1);
         synchronized(this) {
            long var7 = U.getLong(var1, this.offset);
            if (var7 != var2) {
               return false;
            } else {
               U.putLong(var1, this.offset, var4);
               return true;
            }
         }
      }

      public final boolean weakCompareAndSet(T var1, long var2, long var4) {
         return this.compareAndSet(var1, var2, var4);
      }

      public final void set(T var1, long var2) {
         this.accessCheck(var1);
         synchronized(this) {
            U.putLong(var1, this.offset, var2);
         }
      }

      public final void lazySet(T var1, long var2) {
         this.set(var1, var2);
      }

      public final long get(T var1) {
         this.accessCheck(var1);
         synchronized(this) {
            return U.getLong(var1, this.offset);
         }
      }
   }

   private static final class CASUpdater<T> extends AtomicLongFieldUpdater<T> {
      private static final Unsafe U = Unsafe.getUnsafe();
      private final long offset;
      private final Class<?> cclass;
      private final Class<T> tclass;

      CASUpdater(final Class<T> var1, final String var2, Class<?> var3) {
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

         if (var4.getType() != Long.TYPE) {
            throw new IllegalArgumentException("Must be long type");
         } else if (!Modifier.isVolatile(var5)) {
            throw new IllegalArgumentException("Must be volatile type");
         } else {
            this.cclass = Modifier.isProtected(var5) && var1.isAssignableFrom(var3) && !AtomicLongFieldUpdater.isSamePackage(var1, var3) ? var3 : var1;
            this.tclass = var1;
            this.offset = U.objectFieldOffset(var4);
         }
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

      public final boolean compareAndSet(T var1, long var2, long var4) {
         this.accessCheck(var1);
         return U.compareAndSwapLong(var1, this.offset, var2, var4);
      }

      public final boolean weakCompareAndSet(T var1, long var2, long var4) {
         this.accessCheck(var1);
         return U.compareAndSwapLong(var1, this.offset, var2, var4);
      }

      public final void set(T var1, long var2) {
         this.accessCheck(var1);
         U.putLongVolatile(var1, this.offset, var2);
      }

      public final void lazySet(T var1, long var2) {
         this.accessCheck(var1);
         U.putOrderedLong(var1, this.offset, var2);
      }

      public final long get(T var1) {
         this.accessCheck(var1);
         return U.getLongVolatile(var1, this.offset);
      }

      public final long getAndSet(T var1, long var2) {
         this.accessCheck(var1);
         return U.getAndSetLong(var1, this.offset, var2);
      }

      public final long getAndAdd(T var1, long var2) {
         this.accessCheck(var1);
         return U.getAndAddLong(var1, this.offset, var2);
      }

      public final long getAndIncrement(T var1) {
         return this.getAndAdd(var1, 1L);
      }

      public final long getAndDecrement(T var1) {
         return this.getAndAdd(var1, -1L);
      }

      public final long incrementAndGet(T var1) {
         return this.getAndAdd(var1, 1L) + 1L;
      }

      public final long decrementAndGet(T var1) {
         return this.getAndAdd(var1, -1L) - 1L;
      }

      public final long addAndGet(T var1, long var2) {
         return this.getAndAdd(var1, var2) + var2;
      }
   }
}
