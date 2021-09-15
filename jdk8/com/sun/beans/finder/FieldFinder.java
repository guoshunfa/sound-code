package com.sun.beans.finder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import sun.reflect.misc.ReflectUtil;

public final class FieldFinder {
   public static Field findField(Class<?> var0, String var1) throws NoSuchFieldException {
      if (var1 == null) {
         throw new IllegalArgumentException("Field name is not set");
      } else {
         Field var2 = var0.getField(var1);
         if (!Modifier.isPublic(var2.getModifiers())) {
            throw new NoSuchFieldException("Field '" + var1 + "' is not public");
         } else {
            var0 = var2.getDeclaringClass();
            if (Modifier.isPublic(var0.getModifiers()) && ReflectUtil.isPackageAccessible(var0)) {
               return var2;
            } else {
               throw new NoSuchFieldException("Field '" + var1 + "' is not accessible");
            }
         }
      }
   }

   public static Field findInstanceField(Class<?> var0, String var1) throws NoSuchFieldException {
      Field var2 = findField(var0, var1);
      if (Modifier.isStatic(var2.getModifiers())) {
         throw new NoSuchFieldException("Field '" + var1 + "' is static");
      } else {
         return var2;
      }
   }

   public static Field findStaticField(Class<?> var0, String var1) throws NoSuchFieldException {
      Field var2 = findField(var0, var1);
      if (!Modifier.isStatic(var2.getModifiers())) {
         throw new NoSuchFieldException("Field '" + var1 + "' is not static");
      } else {
         return var2;
      }
   }

   private FieldFinder() {
   }
}
