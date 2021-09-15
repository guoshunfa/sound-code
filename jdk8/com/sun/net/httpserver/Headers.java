package com.sun.net.httpserver;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jdk.Exported;

@Exported
public class Headers implements Map<String, List<String>> {
   HashMap<String, List<String>> map = new HashMap(32);

   private String normalize(String var1) {
      if (var1 == null) {
         return null;
      } else {
         int var2 = var1.length();
         if (var2 == 0) {
            return var1;
         } else {
            char[] var3 = var1.toCharArray();
            if (var3[0] >= 'a' && var3[0] <= 'z') {
               var3[0] = (char)(var3[0] - 32);
            }

            for(int var4 = 1; var4 < var2; ++var4) {
               if (var3[var4] >= 'A' && var3[var4] <= 'Z') {
                  var3[var4] = (char)(var3[var4] + 32);
               }
            }

            return new String(var3);
         }
      }
   }

   public int size() {
      return this.map.size();
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   public boolean containsKey(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         return !(var1 instanceof String) ? false : this.map.containsKey(this.normalize((String)var1));
      }
   }

   public boolean containsValue(Object var1) {
      return this.map.containsValue(var1);
   }

   public List<String> get(Object var1) {
      return (List)this.map.get(this.normalize((String)var1));
   }

   public String getFirst(String var1) {
      List var2 = (List)this.map.get(this.normalize(var1));
      return var2 == null ? null : (String)var2.get(0);
   }

   public List<String> put(String var1, List<String> var2) {
      return (List)this.map.put(this.normalize(var1), var2);
   }

   public void add(String var1, String var2) {
      String var3 = this.normalize(var1);
      Object var4 = (List)this.map.get(var3);
      if (var4 == null) {
         var4 = new LinkedList();
         this.map.put(var3, var4);
      }

      ((List)var4).add(var2);
   }

   public void set(String var1, String var2) {
      LinkedList var3 = new LinkedList();
      var3.add(var2);
      this.put((String)var1, (List)var3);
   }

   public List<String> remove(Object var1) {
      return (List)this.map.remove(this.normalize((String)var1));
   }

   public void putAll(Map<? extends String, ? extends List<String>> var1) {
      this.map.putAll(var1);
   }

   public void clear() {
      this.map.clear();
   }

   public Set<String> keySet() {
      return this.map.keySet();
   }

   public Collection<List<String>> values() {
      return this.map.values();
   }

   public Set<Map.Entry<String, List<String>>> entrySet() {
      return this.map.entrySet();
   }

   public boolean equals(Object var1) {
      return this.map.equals(var1);
   }

   public int hashCode() {
      return this.map.hashCode();
   }
}
