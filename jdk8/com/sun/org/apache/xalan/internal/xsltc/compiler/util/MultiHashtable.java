package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class MultiHashtable<K, V> {
   static final long serialVersionUID = -6151608290510033572L;
   private final Map<K, Set<V>> map = new HashMap();
   private boolean modifiable = true;

   public Set<V> put(K key, V value) {
      if (this.modifiable) {
         Set<V> set = (Set)this.map.get(key);
         if (set == null) {
            set = new HashSet();
            this.map.put(key, set);
         }

         ((Set)set).add(value);
         return (Set)set;
      } else {
         throw new UnsupportedOperationException("The MultiHashtable instance is not modifiable.");
      }
   }

   public V maps(K key, V value) {
      if (key == null) {
         return null;
      } else {
         Set<V> set = (Set)this.map.get(key);
         if (set != null) {
            Iterator var4 = set.iterator();

            while(var4.hasNext()) {
               V v = var4.next();
               if (v.equals(value)) {
                  return v;
               }
            }
         }

         return null;
      }
   }

   public void makeUnmodifiable() {
      this.modifiable = false;
   }
}
