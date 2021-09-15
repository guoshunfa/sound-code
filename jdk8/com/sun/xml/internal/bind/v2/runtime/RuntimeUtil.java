package com.sun.xml.internal.bind.v2.runtime;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class RuntimeUtil {
   public static final Map<Class, Class> boxToPrimitive;
   public static final Map<Class, Class> primitiveToBox;

   private static String getTypeName(Object o) {
      return o.getClass().getName();
   }

   static {
      Map<Class, Class> b = new HashMap();
      b.put(Byte.TYPE, Byte.class);
      b.put(Short.TYPE, Short.class);
      b.put(Integer.TYPE, Integer.class);
      b.put(Long.TYPE, Long.class);
      b.put(Character.TYPE, Character.class);
      b.put(Boolean.TYPE, Boolean.class);
      b.put(Float.TYPE, Float.class);
      b.put(Double.TYPE, Double.class);
      b.put(Void.TYPE, Void.class);
      primitiveToBox = Collections.unmodifiableMap(b);
      Map<Class, Class> p = new HashMap();
      Iterator var2 = b.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry<Class, Class> e = (Map.Entry)var2.next();
         p.put(e.getValue(), e.getKey());
      }

      boxToPrimitive = Collections.unmodifiableMap(p);
   }

   public static final class ToStringAdapter extends XmlAdapter<String, Object> {
      public Object unmarshal(String s) {
         throw new UnsupportedOperationException();
      }

      public String marshal(Object o) {
         return o == null ? null : o.toString();
      }
   }
}
