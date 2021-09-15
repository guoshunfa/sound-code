package com.sun.beans.finder;

import java.util.HashMap;
import java.util.Map;

final class PrimitiveTypeMap {
   private static final Map<String, Class<?>> map = new HashMap(9);

   static Class<?> getType(String var0) {
      return (Class)map.get(var0);
   }

   private PrimitiveTypeMap() {
   }

   static {
      map.put(Boolean.TYPE.getName(), Boolean.TYPE);
      map.put(Character.TYPE.getName(), Character.TYPE);
      map.put(Byte.TYPE.getName(), Byte.TYPE);
      map.put(Short.TYPE.getName(), Short.TYPE);
      map.put(Integer.TYPE.getName(), Integer.TYPE);
      map.put(Long.TYPE.getName(), Long.TYPE);
      map.put(Float.TYPE.getName(), Float.TYPE);
      map.put(Double.TYPE.getName(), Double.TYPE);
      map.put(Void.TYPE.getName(), Void.TYPE);
   }
}
