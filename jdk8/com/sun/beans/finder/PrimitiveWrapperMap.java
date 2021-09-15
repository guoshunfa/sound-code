package com.sun.beans.finder;

import java.util.HashMap;
import java.util.Map;

public final class PrimitiveWrapperMap {
   private static final Map<String, Class<?>> map = new HashMap(9);

   static void replacePrimitivesWithWrappers(Class<?>[] var0) {
      for(int var1 = 0; var1 < var0.length; ++var1) {
         if (var0[var1] != null && var0[var1].isPrimitive()) {
            var0[var1] = getType(var0[var1].getName());
         }
      }

   }

   public static Class<?> getType(String var0) {
      return (Class)map.get(var0);
   }

   private PrimitiveWrapperMap() {
   }

   static {
      map.put(Boolean.TYPE.getName(), Boolean.class);
      map.put(Character.TYPE.getName(), Character.class);
      map.put(Byte.TYPE.getName(), Byte.class);
      map.put(Short.TYPE.getName(), Short.class);
      map.put(Integer.TYPE.getName(), Integer.class);
      map.put(Long.TYPE.getName(), Long.class);
      map.put(Float.TYPE.getName(), Float.class);
      map.put(Double.TYPE.getName(), Double.class);
      map.put(Void.TYPE.getName(), Void.class);
   }
}
