package sun.rmi.server;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.WeakHashMap;

public abstract class WeakClassHashMap<V> {
   private Map<Class<?>, WeakClassHashMap.ValueCell<V>> internalMap = new WeakHashMap();

   protected WeakClassHashMap() {
   }

   public V get(Class<?> var1) {
      WeakClassHashMap.ValueCell var2;
      synchronized(this.internalMap) {
         var2 = (WeakClassHashMap.ValueCell)this.internalMap.get(var1);
         if (var2 == null) {
            var2 = new WeakClassHashMap.ValueCell();
            this.internalMap.put(var1, var2);
         }
      }

      synchronized(var2) {
         Object var4 = null;
         if (var2.ref != null) {
            var4 = var2.ref.get();
         }

         if (var4 == null) {
            var4 = this.computeValue(var1);
            var2.ref = new SoftReference(var4);
         }

         return var4;
      }
   }

   protected abstract V computeValue(Class<?> var1);

   private static class ValueCell<T> {
      Reference<T> ref = null;

      ValueCell() {
      }
   }
}
