package javax.script;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SimpleBindings implements Bindings {
   private Map<String, Object> map;

   public SimpleBindings(Map<String, Object> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.map = var1;
      }
   }

   public SimpleBindings() {
      this(new HashMap());
   }

   public Object put(String var1, Object var2) {
      this.checkKey(var1);
      return this.map.put(var1, var2);
   }

   public void putAll(Map<? extends String, ? extends Object> var1) {
      if (var1 == null) {
         throw new NullPointerException("toMerge map is null");
      } else {
         Iterator var2 = var1.entrySet().iterator();

         while(var2.hasNext()) {
            Map.Entry var3 = (Map.Entry)var2.next();
            String var4 = (String)var3.getKey();
            this.checkKey(var4);
            this.put(var4, var3.getValue());
         }

      }
   }

   public void clear() {
      this.map.clear();
   }

   public boolean containsKey(Object var1) {
      this.checkKey(var1);
      return this.map.containsKey(var1);
   }

   public boolean containsValue(Object var1) {
      return this.map.containsValue(var1);
   }

   public Set<Map.Entry<String, Object>> entrySet() {
      return this.map.entrySet();
   }

   public Object get(Object var1) {
      this.checkKey(var1);
      return this.map.get(var1);
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   public Set<String> keySet() {
      return this.map.keySet();
   }

   public Object remove(Object var1) {
      this.checkKey(var1);
      return this.map.remove(var1);
   }

   public int size() {
      return this.map.size();
   }

   public Collection<Object> values() {
      return this.map.values();
   }

   private void checkKey(Object var1) {
      if (var1 == null) {
         throw new NullPointerException("key can not be null");
      } else if (!(var1 instanceof String)) {
         throw new ClassCastException("key should be a String");
      } else if (var1.equals("")) {
         throw new IllegalArgumentException("key can not be empty");
      }
   }
}
