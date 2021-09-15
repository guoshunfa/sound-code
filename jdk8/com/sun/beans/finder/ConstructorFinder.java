package com.sun.beans.finder;

import com.sun.beans.util.Cache;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import sun.reflect.misc.ReflectUtil;

public final class ConstructorFinder extends AbstractFinder<Constructor<?>> {
   private static final Cache<Signature, Constructor<?>> CACHE;

   public static Constructor<?> findConstructor(Class<?> var0, Class<?>... var1) throws NoSuchMethodException {
      if (var0.isPrimitive()) {
         throw new NoSuchMethodException("Primitive wrapper does not contain constructors");
      } else if (var0.isInterface()) {
         throw new NoSuchMethodException("Interface does not contain constructors");
      } else if (Modifier.isAbstract(var0.getModifiers())) {
         throw new NoSuchMethodException("Abstract class cannot be instantiated");
      } else if (Modifier.isPublic(var0.getModifiers()) && ReflectUtil.isPackageAccessible(var0)) {
         PrimitiveWrapperMap.replacePrimitivesWithWrappers(var1);
         Signature var2 = new Signature(var0, var1);

         try {
            return (Constructor)CACHE.get(var2);
         } catch (SignatureException var4) {
            throw var4.toNoSuchMethodException("Constructor is not found");
         }
      } else {
         throw new NoSuchMethodException("Class is not accessible");
      }
   }

   private ConstructorFinder(Class<?>[] var1) {
      super(var1);
   }

   // $FF: synthetic method
   ConstructorFinder(Class[] var1, Object var2) {
      this(var1);
   }

   static {
      CACHE = new Cache<Signature, Constructor<?>>(Cache.Kind.SOFT, Cache.Kind.SOFT) {
         public Constructor create(Signature var1) {
            try {
               ConstructorFinder var2 = new ConstructorFinder(var1.getArgs());
               return (Constructor)var2.find(var1.getType().getConstructors());
            } catch (Exception var3) {
               throw new SignatureException(var3);
            }
         }
      };
   }
}
