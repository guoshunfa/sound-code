package java.lang.reflect;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import sun.misc.ProxyGenerator;
import sun.misc.VM;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;
import sun.security.util.SecurityConstants;

public class Proxy implements Serializable {
   private static final long serialVersionUID = -2222568056686623797L;
   private static final Class<?>[] constructorParams = new Class[]{InvocationHandler.class};
   private static final WeakCache<ClassLoader, Class<?>[], Class<?>> proxyClassCache = new WeakCache(new Proxy.KeyFactory(), new Proxy.ProxyClassFactory());
   protected InvocationHandler h;
   private static final Object key0 = new Object();

   private Proxy() {
   }

   protected Proxy(InvocationHandler var1) {
      Objects.requireNonNull(var1);
      this.h = var1;
   }

   @CallerSensitive
   public static Class<?> getProxyClass(ClassLoader var0, Class<?>... var1) throws IllegalArgumentException {
      Class[] var2 = (Class[])var1.clone();
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         checkProxyAccess(Reflection.getCallerClass(), var0, var2);
      }

      return getProxyClass0(var0, var2);
   }

   private static void checkProxyAccess(Class<?> var0, ClassLoader var1, Class<?>... var2) {
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         ClassLoader var4 = var0.getClassLoader();
         if (VM.isSystemDomainLoader(var1) && !VM.isSystemDomainLoader(var4)) {
            var3.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
         }

         ReflectUtil.checkProxyPackageAccess(var4, var2);
      }

   }

   private static Class<?> getProxyClass0(ClassLoader var0, Class<?>... var1) {
      if (var1.length > 65535) {
         throw new IllegalArgumentException("interface limit exceeded");
      } else {
         return (Class)proxyClassCache.get(var0, var1);
      }
   }

   @CallerSensitive
   public static Object newProxyInstance(ClassLoader var0, Class<?>[] var1, InvocationHandler var2) throws IllegalArgumentException {
      Objects.requireNonNull(var2);
      Class[] var3 = (Class[])var1.clone();
      SecurityManager var4 = System.getSecurityManager();
      if (var4 != null) {
         checkProxyAccess(Reflection.getCallerClass(), var0, var3);
      }

      Class var5 = getProxyClass0(var0, var3);

      try {
         if (var4 != null) {
            checkNewProxyPermission(Reflection.getCallerClass(), var5);
         }

         final Constructor var6 = var5.getConstructor(constructorParams);
         if (!Modifier.isPublic(var5.getModifiers())) {
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
               public Void run() {
                  var6.setAccessible(true);
                  return null;
               }
            });
         }

         return var6.newInstance(var2);
      } catch (InstantiationException | IllegalAccessException var8) {
         throw new InternalError(var8.toString(), var8);
      } catch (InvocationTargetException var9) {
         Throwable var7 = var9.getCause();
         if (var7 instanceof RuntimeException) {
            throw (RuntimeException)var7;
         } else {
            throw new InternalError(var7.toString(), var7);
         }
      } catch (NoSuchMethodException var10) {
         throw new InternalError(var10.toString(), var10);
      }
   }

   private static void checkNewProxyPermission(Class<?> var0, Class<?> var1) {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null && ReflectUtil.isNonPublicProxyClass(var1)) {
         ClassLoader var3 = var0.getClassLoader();
         ClassLoader var4 = var1.getClassLoader();
         int var5 = var1.getName().lastIndexOf(46);
         String var6 = var5 == -1 ? "" : var1.getName().substring(0, var5);
         var5 = var0.getName().lastIndexOf(46);
         String var7 = var5 == -1 ? "" : var0.getName().substring(0, var5);
         if (var4 != var3 || !var6.equals(var7)) {
            var2.checkPermission(new ReflectPermission("newProxyInPackage." + var6));
         }
      }

   }

   public static boolean isProxyClass(Class<?> var0) {
      return Proxy.class.isAssignableFrom(var0) && proxyClassCache.containsValue(var0);
   }

   @CallerSensitive
   public static InvocationHandler getInvocationHandler(Object var0) throws IllegalArgumentException {
      if (!isProxyClass(var0.getClass())) {
         throw new IllegalArgumentException("not a proxy instance");
      } else {
         Proxy var1 = (Proxy)var0;
         InvocationHandler var2 = var1.h;
         if (System.getSecurityManager() != null) {
            Class var3 = var2.getClass();
            Class var4 = Reflection.getCallerClass();
            if (ReflectUtil.needsPackageAccessCheck(var4.getClassLoader(), var3.getClassLoader())) {
               ReflectUtil.checkPackageAccess(var3);
            }
         }

         return var2;
      }
   }

   private static native Class<?> defineClass0(ClassLoader var0, String var1, byte[] var2, int var3, int var4);

   private static final class ProxyClassFactory implements BiFunction<ClassLoader, Class<?>[], Class<?>> {
      private static final String proxyClassNamePrefix = "$Proxy";
      private static final AtomicLong nextUniqueNumber = new AtomicLong();

      private ProxyClassFactory() {
      }

      public Class<?> apply(ClassLoader var1, Class<?>[] var2) {
         IdentityHashMap var3 = new IdentityHashMap(var2.length);
         Class[] var4 = var2;
         int var5 = var2.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Class var7 = var4[var6];
            Class var8 = null;

            try {
               var8 = Class.forName(var7.getName(), false, var1);
            } catch (ClassNotFoundException var15) {
            }

            if (var8 != var7) {
               throw new IllegalArgumentException(var7 + " is not visible from class loader");
            }

            if (!var8.isInterface()) {
               throw new IllegalArgumentException(var8.getName() + " is not an interface");
            }

            if (var3.put(var8, Boolean.TRUE) != null) {
               throw new IllegalArgumentException("repeated interface: " + var8.getName());
            }
         }

         String var16 = null;
         byte var17 = 17;
         Class[] var18 = var2;
         int var20 = var2.length;

         for(int var21 = 0; var21 < var20; ++var21) {
            Class var9 = var18[var21];
            int var10 = var9.getModifiers();
            if (!Modifier.isPublic(var10)) {
               var17 = 16;
               String var11 = var9.getName();
               int var12 = var11.lastIndexOf(46);
               String var13 = var12 == -1 ? "" : var11.substring(0, var12 + 1);
               if (var16 == null) {
                  var16 = var13;
               } else if (!var13.equals(var16)) {
                  throw new IllegalArgumentException("non-public interfaces from different packages");
               }
            }
         }

         if (var16 == null) {
            var16 = "com.sun.proxy.";
         }

         long var19 = nextUniqueNumber.getAndIncrement();
         String var23 = var16 + "$Proxy" + var19;
         byte[] var22 = ProxyGenerator.generateProxyClass(var23, var2, var17);

         try {
            return Proxy.defineClass0(var1, var23, var22, 0, var22.length);
         } catch (ClassFormatError var14) {
            throw new IllegalArgumentException(var14.toString());
         }
      }

      // $FF: synthetic method
      ProxyClassFactory(Object var1) {
         this();
      }
   }

   private static final class KeyFactory implements BiFunction<ClassLoader, Class<?>[], Object> {
      private KeyFactory() {
      }

      public Object apply(ClassLoader var1, Class<?>[] var2) {
         switch(var2.length) {
         case 0:
            return Proxy.key0;
         case 1:
            return new Proxy.Key1(var2[0]);
         case 2:
            return new Proxy.Key2(var2[0], var2[1]);
         default:
            return new Proxy.KeyX(var2);
         }
      }

      // $FF: synthetic method
      KeyFactory(Object var1) {
         this();
      }
   }

   private static final class KeyX {
      private final int hash;
      private final WeakReference<Class<?>>[] refs;

      KeyX(Class<?>[] var1) {
         this.hash = Arrays.hashCode((Object[])var1);
         this.refs = (WeakReference[])(new WeakReference[var1.length]);

         for(int var2 = 0; var2 < var1.length; ++var2) {
            this.refs[var2] = new WeakReference(var1[var2]);
         }

      }

      public int hashCode() {
         return this.hash;
      }

      public boolean equals(Object var1) {
         return this == var1 || var1 != null && var1.getClass() == Proxy.KeyX.class && equals(this.refs, ((Proxy.KeyX)var1).refs);
      }

      private static boolean equals(WeakReference<Class<?>>[] var0, WeakReference<Class<?>>[] var1) {
         if (var0.length != var1.length) {
            return false;
         } else {
            for(int var2 = 0; var2 < var0.length; ++var2) {
               Class var3 = (Class)var0[var2].get();
               if (var3 == null || var3 != var1[var2].get()) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   private static final class Key2 extends WeakReference<Class<?>> {
      private final int hash;
      private final WeakReference<Class<?>> ref2;

      Key2(Class<?> var1, Class<?> var2) {
         super(var1);
         this.hash = 31 * var1.hashCode() + var2.hashCode();
         this.ref2 = new WeakReference(var2);
      }

      public int hashCode() {
         return this.hash;
      }

      public boolean equals(Object var1) {
         Class var2;
         Class var3;
         return this == var1 || var1 != null && var1.getClass() == Proxy.Key2.class && (var2 = (Class)this.get()) != null && var2 == ((Proxy.Key2)var1).get() && (var3 = (Class)this.ref2.get()) != null && var3 == ((Proxy.Key2)var1).ref2.get();
      }
   }

   private static final class Key1 extends WeakReference<Class<?>> {
      private final int hash;

      Key1(Class<?> var1) {
         super(var1);
         this.hash = var1.hashCode();
      }

      public int hashCode() {
         return this.hash;
      }

      public boolean equals(Object var1) {
         Class var2;
         return this == var1 || var1 != null && var1.getClass() == Proxy.Key1.class && (var2 = (Class)this.get()) != null && var2 == ((Proxy.Key1)var1).get();
      }
   }
}
