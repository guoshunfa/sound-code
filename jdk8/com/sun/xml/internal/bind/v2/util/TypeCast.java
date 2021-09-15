package com.sun.xml.internal.bind.v2.util;

import java.util.Iterator;
import java.util.Map;

public class TypeCast {
   public static <K, V> Map<K, V> checkedCast(Map<?, ?> m, Class<K> keyType, Class<V> valueType) {
      if (m == null) {
         return null;
      } else {
         Iterator var3 = m.entrySet().iterator();

         Map.Entry e;
         do {
            if (!var3.hasNext()) {
               return m;
            }

            e = (Map.Entry)var3.next();
            if (!keyType.isInstance(e.getKey())) {
               throw new ClassCastException(e.getKey().getClass().toString());
            }
         } while(valueType.isInstance(e.getValue()));

         throw new ClassCastException(e.getValue().getClass().toString());
      }
   }
}
