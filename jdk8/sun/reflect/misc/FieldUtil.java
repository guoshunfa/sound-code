package sun.reflect.misc;

import java.lang.reflect.Field;

public final class FieldUtil {
   private FieldUtil() {
   }

   public static Field getField(Class<?> var0, String var1) throws NoSuchFieldException {
      ReflectUtil.checkPackageAccess(var0);
      return var0.getField(var1);
   }

   public static Field[] getFields(Class<?> var0) {
      ReflectUtil.checkPackageAccess(var0);
      return var0.getFields();
   }

   public static Field[] getDeclaredFields(Class<?> var0) {
      ReflectUtil.checkPackageAccess(var0);
      return var0.getDeclaredFields();
   }
}
