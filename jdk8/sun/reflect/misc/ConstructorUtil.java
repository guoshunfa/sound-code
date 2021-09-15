package sun.reflect.misc;

import java.lang.reflect.Constructor;

public final class ConstructorUtil {
   private ConstructorUtil() {
   }

   public static Constructor<?> getConstructor(Class<?> var0, Class<?>[] var1) throws NoSuchMethodException {
      ReflectUtil.checkPackageAccess(var0);
      return var0.getConstructor(var1);
   }

   public static Constructor<?>[] getConstructors(Class<?> var0) {
      ReflectUtil.checkPackageAccess(var0);
      return var0.getConstructors();
   }
}
