package java.lang.invoke;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import sun.invoke.WrapperInstance;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;

public class MethodHandleProxies {
   private MethodHandleProxies() {
   }

   @CallerSensitive
   public static <T> T asInterfaceInstance(final Class<T> var0, final MethodHandle var1) {
      if (var0.isInterface() && Modifier.isPublic(var0.getModifiers())) {
         MethodHandle var2;
         ClassLoader var4;
         if (System.getSecurityManager() != null) {
            Class var3 = Reflection.getCallerClass();
            var4 = var3 != null ? var3.getClassLoader() : null;
            ReflectUtil.checkProxyPackageAccess(var4, var0);
            var2 = var4 != null ? bindCaller(var1, var3) : var1;
         } else {
            var2 = var1;
         }

         final ClassLoader var10 = var0.getClassLoader();
         if (var10 == null) {
            var4 = Thread.currentThread().getContextClassLoader();
            var10 = var4 != null ? var4 : ClassLoader.getSystemClassLoader();
         }

         final Method[] var11 = getSingleNameMethods(var0);
         if (var11 == null) {
            throw MethodHandleStatics.newIllegalArgumentException("not a single-method interface", var0.getName());
         } else {
            final MethodHandle[] var5 = new MethodHandle[var11.length];

            for(int var6 = 0; var6 < var11.length; ++var6) {
               Method var7 = var11[var6];
               MethodType var8 = MethodType.methodType(var7.getReturnType(), var7.getParameterTypes());
               MethodHandle var9 = var2.asType(var8);
               var9 = var9.asType(var9.type().changeReturnType(Object.class));
               var5[var6] = var9.asSpreader(Object[].class, var8.parameterCount());
            }

            final InvocationHandler var12 = new InvocationHandler() {
               private Object getArg(String var1x) {
                  if (var1x == "getWrapperInstanceTarget") {
                     return var1;
                  } else if (var1x == "getWrapperInstanceType") {
                     return var0;
                  } else {
                     throw new AssertionError();
                  }
               }

               public Object invoke(Object var1x, Method var2, Object[] var3) throws Throwable {
                  for(int var4 = 0; var4 < var11.length; ++var4) {
                     if (var2.equals(var11[var4])) {
                        return var5[var4].invokeExact(var3);
                     }
                  }

                  if (var2.getDeclaringClass() == WrapperInstance.class) {
                     return this.getArg(var2.getName());
                  } else if (MethodHandleProxies.isObjectMethod(var2)) {
                     return MethodHandleProxies.callObjectMethod(var1x, var2, var3);
                  } else {
                     throw MethodHandleStatics.newInternalError("bad proxy method: " + var2);
                  }
               }
            };
            Object var13;
            if (System.getSecurityManager() != null) {
               var13 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
                  public Object run() {
                     return Proxy.newProxyInstance(var10, new Class[]{var0, WrapperInstance.class}, var12);
                  }
               });
            } else {
               var13 = Proxy.newProxyInstance(var10, new Class[]{var0, WrapperInstance.class}, var12);
            }

            return var0.cast(var13);
         }
      } else {
         throw MethodHandleStatics.newIllegalArgumentException("not a public interface", var0.getName());
      }
   }

   private static MethodHandle bindCaller(MethodHandle var0, Class<?> var1) {
      MethodHandle var2 = MethodHandleImpl.bindCaller(var0, var1);
      if (var0.isVarargsCollector()) {
         MethodType var3 = var2.type();
         int var4 = var3.parameterCount();
         return var2.asVarargsCollector(var3.parameterType(var4 - 1));
      } else {
         return var2;
      }
   }

   public static boolean isWrapperInstance(Object var0) {
      return var0 instanceof WrapperInstance;
   }

   private static WrapperInstance asWrapperInstance(Object var0) {
      try {
         if (var0 != null) {
            return (WrapperInstance)var0;
         }
      } catch (ClassCastException var2) {
      }

      throw MethodHandleStatics.newIllegalArgumentException("not a wrapper instance");
   }

   public static MethodHandle wrapperInstanceTarget(Object var0) {
      return asWrapperInstance(var0).getWrapperInstanceTarget();
   }

   public static Class<?> wrapperInstanceType(Object var0) {
      return asWrapperInstance(var0).getWrapperInstanceType();
   }

   private static boolean isObjectMethod(Method var0) {
      String var1 = var0.getName();
      byte var2 = -1;
      switch(var1.hashCode()) {
      case -1776922004:
         if (var1.equals("toString")) {
            var2 = 0;
         }
         break;
      case -1295482945:
         if (var1.equals("equals")) {
            var2 = 2;
         }
         break;
      case 147696667:
         if (var1.equals("hashCode")) {
            var2 = 1;
         }
      }

      switch(var2) {
      case 0:
         return var0.getReturnType() == String.class && var0.getParameterTypes().length == 0;
      case 1:
         return var0.getReturnType() == Integer.TYPE && var0.getParameterTypes().length == 0;
      case 2:
         return var0.getReturnType() == Boolean.TYPE && var0.getParameterTypes().length == 1 && var0.getParameterTypes()[0] == Object.class;
      default:
         return false;
      }
   }

   private static Object callObjectMethod(Object var0, Method var1, Object[] var2) {
      assert isObjectMethod(var1) : var1;

      String var3 = var1.getName();
      byte var4 = -1;
      switch(var3.hashCode()) {
      case -1776922004:
         if (var3.equals("toString")) {
            var4 = 0;
         }
         break;
      case -1295482945:
         if (var3.equals("equals")) {
            var4 = 2;
         }
         break;
      case 147696667:
         if (var3.equals("hashCode")) {
            var4 = 1;
         }
      }

      switch(var4) {
      case 0:
         return var0.getClass().getName() + "@" + Integer.toHexString(var0.hashCode());
      case 1:
         return System.identityHashCode(var0);
      case 2:
         return var0 == var2[0];
      default:
         return null;
      }
   }

   private static Method[] getSingleNameMethods(Class<?> var0) {
      ArrayList var1 = new ArrayList();
      String var2 = null;
      Method[] var3 = var0.getMethods();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Method var6 = var3[var5];
         if (!isObjectMethod(var6) && Modifier.isAbstract(var6.getModifiers())) {
            String var7 = var6.getName();
            if (var2 == null) {
               var2 = var7;
            } else if (!var2.equals(var7)) {
               return null;
            }

            var1.add(var6);
         }
      }

      if (var2 == null) {
         return null;
      } else {
         return (Method[])var1.toArray(new Method[var1.size()]);
      }
   }
}
