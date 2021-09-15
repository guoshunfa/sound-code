package java.beans;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;

class NameGenerator {
   private Map<Object, String> valueToName = new IdentityHashMap();
   private Map<String, Integer> nameToCount = new HashMap();

   public NameGenerator() {
   }

   public void clear() {
      this.valueToName.clear();
      this.nameToCount.clear();
   }

   public static String unqualifiedClassName(Class var0) {
      if (var0.isArray()) {
         return unqualifiedClassName(var0.getComponentType()) + "Array";
      } else {
         String var1 = var0.getName();
         return var1.substring(var1.lastIndexOf(46) + 1);
      }
   }

   public static String capitalize(String var0) {
      return var0 != null && var0.length() != 0 ? var0.substring(0, 1).toUpperCase(Locale.ENGLISH) + var0.substring(1) : var0;
   }

   public String instanceName(Object var1) {
      if (var1 == null) {
         return "null";
      } else if (var1 instanceof Class) {
         return unqualifiedClassName((Class)var1);
      } else {
         String var2 = (String)this.valueToName.get(var1);
         if (var2 != null) {
            return var2;
         } else {
            Class var3 = var1.getClass();
            String var4 = unqualifiedClassName(var3);
            Integer var5 = (Integer)this.nameToCount.get(var4);
            int var6 = var5 == null ? 0 : var5 + 1;
            this.nameToCount.put(var4, new Integer(var6));
            var2 = var4 + var6;
            this.valueToName.put(var1, var2);
            return var2;
         }
      }
   }
}
