package com.sun.beans.finder;

import com.sun.beans.TypeResolver;
import com.sun.beans.util.Cache;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import sun.reflect.misc.ReflectUtil;

public final class MethodFinder extends AbstractFinder<Method> {
   private static final Cache<Signature, Method> CACHE;
   private final String name;

   public static Method findMethod(Class<?> var0, String var1, Class<?>... var2) throws NoSuchMethodException {
      if (var1 == null) {
         throw new IllegalArgumentException("Method name is not set");
      } else {
         PrimitiveWrapperMap.replacePrimitivesWithWrappers(var2);
         Signature var3 = new Signature(var0, var1, var2);

         try {
            Method var4 = (Method)CACHE.get(var3);
            return var4 != null && !ReflectUtil.isPackageAccessible(var4.getDeclaringClass()) ? (Method)CACHE.create(var3) : var4;
         } catch (SignatureException var5) {
            throw var5.toNoSuchMethodException("Method '" + var1 + "' is not found");
         }
      }
   }

   public static Method findInstanceMethod(Class<?> var0, String var1, Class<?>... var2) throws NoSuchMethodException {
      Method var3 = findMethod(var0, var1, var2);
      if (Modifier.isStatic(var3.getModifiers())) {
         throw new NoSuchMethodException("Method '" + var1 + "' is static");
      } else {
         return var3;
      }
   }

   public static Method findStaticMethod(Class<?> var0, String var1, Class<?>... var2) throws NoSuchMethodException {
      Method var3 = findMethod(var0, var1, var2);
      if (!Modifier.isStatic(var3.getModifiers())) {
         throw new NoSuchMethodException("Method '" + var1 + "' is not static");
      } else {
         return var3;
      }
   }

   public static Method findAccessibleMethod(Method var0) throws NoSuchMethodException {
      Class var1 = var0.getDeclaringClass();
      if (Modifier.isPublic(var1.getModifiers()) && ReflectUtil.isPackageAccessible(var1)) {
         return var0;
      } else if (Modifier.isStatic(var0.getModifiers())) {
         throw new NoSuchMethodException("Method '" + var0.getName() + "' is not accessible");
      } else {
         Type[] var2 = var1.getGenericInterfaces();
         int var3 = var2.length;
         int var4 = 0;

         while(var4 < var3) {
            Type var5 = var2[var4];

            try {
               return findAccessibleMethod(var0, var5);
            } catch (NoSuchMethodException var7) {
               ++var4;
            }
         }

         return findAccessibleMethod(var0, var1.getGenericSuperclass());
      }
   }

   private static Method findAccessibleMethod(Method var0, Type var1) throws NoSuchMethodException {
      String var2 = var0.getName();
      Class[] var3 = var0.getParameterTypes();
      if (var1 instanceof Class) {
         Class var12 = (Class)var1;
         return findAccessibleMethod(var12.getMethod(var2, var3));
      } else {
         if (var1 instanceof ParameterizedType) {
            ParameterizedType var4 = (ParameterizedType)var1;
            Class var5 = (Class)var4.getRawType();
            Method[] var6 = var5.getMethods();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               Method var9 = var6[var8];
               if (var9.getName().equals(var2)) {
                  Class[] var10 = var9.getParameterTypes();
                  if (var10.length == var3.length) {
                     if (Arrays.equals((Object[])var3, (Object[])var10)) {
                        return findAccessibleMethod(var9);
                     }

                     Type[] var11 = var9.getGenericParameterTypes();
                     if (var3.length == var11.length && Arrays.equals((Object[])var3, (Object[])TypeResolver.erase(TypeResolver.resolve(var4, (Type[])var11)))) {
                        return findAccessibleMethod(var9);
                     }
                  }
               }
            }
         }

         throw new NoSuchMethodException("Method '" + var2 + "' is not accessible");
      }
   }

   private MethodFinder(String var1, Class<?>[] var2) {
      super(var2);
      this.name = var1;
   }

   protected boolean isValid(Method var1) {
      return super.isValid(var1) && var1.getName().equals(this.name);
   }

   // $FF: synthetic method
   MethodFinder(String var1, Class[] var2, Object var3) {
      this(var1, var2);
   }

   static {
      CACHE = new Cache<Signature, Method>(Cache.Kind.SOFT, Cache.Kind.SOFT) {
         public Method create(Signature var1) {
            try {
               MethodFinder var2 = new MethodFinder(var1.getName(), var1.getArgs());
               return MethodFinder.findAccessibleMethod((Method)var2.find(var1.getType().getMethods()));
            } catch (Exception var3) {
               throw new SignatureException(var3);
            }
         }
      };
   }
}
