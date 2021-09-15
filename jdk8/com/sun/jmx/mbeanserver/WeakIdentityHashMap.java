package com.sun.jmx.mbeanserver;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;

class WeakIdentityHashMap<K, V> {
   private Map<WeakReference<K>, V> map = Util.newMap();
   private ReferenceQueue<K> refQueue = new ReferenceQueue();

   private WeakIdentityHashMap() {
   }

   static <K, V> WeakIdentityHashMap<K, V> make() {
      return new WeakIdentityHashMap();
   }

   V get(K var1) {
      this.expunge();
      WeakReference var2 = this.makeReference(var1);
      return this.map.get(var2);
   }

   public V put(K var1, V var2) {
      this.expunge();
      if (var1 == null) {
         throw new IllegalArgumentException("Null key");
      } else {
         WeakReference var3 = this.makeReference(var1, this.refQueue);
         return this.map.put(var3, var2);
      }
   }

   public V remove(K var1) {
      this.expunge();
      WeakReference var2 = this.makeReference(var1);
      return this.map.remove(var2);
   }

   private void expunge() {
      Reference var1;
      while((var1 = this.refQueue.poll()) != null) {
         this.map.remove(var1);
      }

   }

   private WeakReference<K> makeReference(K var1) {
      return new WeakIdentityHashMap.IdentityWeakReference(var1);
   }

   private WeakReference<K> makeReference(K var1, ReferenceQueue<K> var2) {
      return new WeakIdentityHashMap.IdentityWeakReference(var1, var2);
   }

   private static class IdentityWeakReference<T> extends WeakReference<T> {
      private final int hashCode;

      IdentityWeakReference(T var1) {
         this(var1, (ReferenceQueue)null);
      }

      IdentityWeakReference(T var1, ReferenceQueue<T> var2) {
         super(var1, var2);
         this.hashCode = var1 == null ? 0 : System.identityHashCode(var1);
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof WeakIdentityHashMap.IdentityWeakReference)) {
            return false;
         } else {
            WeakIdentityHashMap.IdentityWeakReference var2 = (WeakIdentityHashMap.IdentityWeakReference)var1;
            Object var3 = this.get();
            return var3 != null && var3 == var2.get();
         }
      }

      public int hashCode() {
         return this.hashCode;
      }
   }
}
