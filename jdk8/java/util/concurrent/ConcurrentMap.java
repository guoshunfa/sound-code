package java.util.concurrent;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface ConcurrentMap<K, V> extends Map<K, V> {
   default V getOrDefault(Object var1, V var2) {
      Object var3;
      return (var3 = this.get(var1)) != null ? var3 : var2;
   }

   default void forEach(BiConsumer<? super K, ? super V> var1) {
      Objects.requireNonNull(var1);
      Iterator var2 = this.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();

         Object var4;
         Object var5;
         try {
            var4 = var3.getKey();
            var5 = var3.getValue();
         } catch (IllegalStateException var7) {
            continue;
         }

         var1.accept(var4, var5);
      }

   }

   V putIfAbsent(K var1, V var2);

   boolean remove(Object var1, Object var2);

   boolean replace(K var1, V var2, V var3);

   V replace(K var1, V var2);

   default void replaceAll(BiFunction<? super K, ? super V, ? extends V> var1) {
      Objects.requireNonNull(var1);
      this.forEach((var2, var3) -> {
         while(!this.replace(var2, var3, var1.apply(var2, var3)) && (var3 = this.get(var2)) != null) {
         }

      });
   }

   default V computeIfAbsent(K var1, Function<? super K, ? extends V> var2) {
      Objects.requireNonNull(var2);
      Object var3;
      Object var4;
      return (var3 = this.get(var1)) == null && (var4 = var2.apply(var1)) != null && (var3 = this.putIfAbsent(var1, var4)) == null ? var4 : var3;
   }

   default V computeIfPresent(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
      Objects.requireNonNull(var2);

      Object var3;
      while((var3 = this.get(var1)) != null) {
         Object var4 = var2.apply(var1, var3);
         if (var4 != null) {
            if (this.replace(var1, var3, var4)) {
               return var4;
            }
         } else if (this.remove(var1, var3)) {
            return null;
         }
      }

      return var3;
   }

   default V compute(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
      Objects.requireNonNull(var2);
      Object var3 = this.get(var1);

      while(true) {
         while(true) {
            Object var4 = var2.apply(var1, var3);
            if (var4 == null) {
               if (var3 == null && !this.containsKey(var1)) {
                  return null;
               }

               if (this.remove(var1, var3)) {
                  return null;
               }

               var3 = this.get(var1);
            } else if (var3 != null) {
               if (this.replace(var1, var3, var4)) {
                  return var4;
               }

               var3 = this.get(var1);
            } else if ((var3 = this.putIfAbsent(var1, var4)) == null) {
               return var4;
            }
         }
      }
   }

   default V merge(K var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
      Objects.requireNonNull(var3);
      Objects.requireNonNull(var2);
      Object var4 = this.get(var1);

      while(true) {
         while(var4 == null) {
            if ((var4 = this.putIfAbsent(var1, var2)) == null) {
               return var2;
            }
         }

         Object var5 = var3.apply(var4, var2);
         if (var5 != null) {
            if (this.replace(var1, var4, var5)) {
               return var5;
            }
         } else if (this.remove(var1, var4)) {
            return null;
         }

         var4 = this.get(var1);
      }
   }
}
