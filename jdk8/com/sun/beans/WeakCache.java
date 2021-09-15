package com.sun.beans;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

public final class WeakCache<K, V> {
   private final Map<K, Reference<V>> map = new WeakHashMap();

   public V get(K var1) {
      Reference var2 = (Reference)this.map.get(var1);
      if (var2 == null) {
         return null;
      } else {
         Object var3 = var2.get();
         if (var3 == null) {
            this.map.remove(var1);
         }

         return var3;
      }
   }

   public void put(K var1, V var2) {
      if (var2 != null) {
         this.map.put(var1, new WeakReference(var2));
      } else {
         this.map.remove(var1);
      }

   }

   public void clear() {
      this.map.clear();
   }
}
