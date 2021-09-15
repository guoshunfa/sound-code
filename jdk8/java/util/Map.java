package java.util;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Map<K, V> {
   int size();

   boolean isEmpty();

   boolean containsKey(Object var1);

   boolean containsValue(Object var1);

   V get(Object var1);

   V put(K var1, V var2);

   V remove(Object var1);

   void putAll(Map<? extends K, ? extends V> var1);

   void clear();

   Set<K> keySet();

   Collection<V> values();

   Set<Map.Entry<K, V>> entrySet();

   boolean equals(Object var1);

   int hashCode();

   default V getOrDefault(Object var1, V var2) {
      Object var3;
      return (var3 = this.get(var1)) == null && !this.containsKey(var1) ? var2 : var3;
   }

   default void forEach(BiConsumer<? super K, ? super V> var1) {
      Objects.requireNonNull(var1);

      Object var4;
      Object var5;
      for(Iterator var2 = this.entrySet().iterator(); var2.hasNext(); var1.accept(var4, var5)) {
         Map.Entry var3 = (Map.Entry)var2.next();

         try {
            var4 = var3.getKey();
            var5 = var3.getValue();
         } catch (IllegalStateException var7) {
            throw new ConcurrentModificationException(var7);
         }
      }

   }

   default void replaceAll(BiFunction<? super K, ? super V, ? extends V> var1) {
      Objects.requireNonNull(var1);
      Iterator var2 = this.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();

         Object var4;
         Object var5;
         try {
            var4 = var3.getKey();
            var5 = var3.getValue();
         } catch (IllegalStateException var8) {
            throw new ConcurrentModificationException(var8);
         }

         var5 = var1.apply(var4, var5);

         try {
            var3.setValue(var5);
         } catch (IllegalStateException var7) {
            throw new ConcurrentModificationException(var7);
         }
      }

   }

   default V putIfAbsent(K var1, V var2) {
      Object var3 = this.get(var1);
      if (var3 == null) {
         var3 = this.put(var1, var2);
      }

      return var3;
   }

   default boolean remove(Object var1, Object var2) {
      Object var3 = this.get(var1);
      if (Objects.equals(var3, var2) && (var3 != null || this.containsKey(var1))) {
         this.remove(var1);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(K var1, V var2, V var3) {
      Object var4 = this.get(var1);
      if (Objects.equals(var4, var2) && (var4 != null || this.containsKey(var1))) {
         this.put(var1, var3);
         return true;
      } else {
         return false;
      }
   }

   default V replace(K var1, V var2) {
      Object var3;
      if ((var3 = this.get(var1)) != null || this.containsKey(var1)) {
         var3 = this.put(var1, var2);
      }

      return var3;
   }

   default V computeIfAbsent(K var1, Function<? super K, ? extends V> var2) {
      Objects.requireNonNull(var2);
      Object var3;
      Object var4;
      if ((var3 = this.get(var1)) == null && (var4 = var2.apply(var1)) != null) {
         this.put(var1, var4);
         return var4;
      } else {
         return var3;
      }
   }

   default V computeIfPresent(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
      Objects.requireNonNull(var2);
      Object var3;
      if ((var3 = this.get(var1)) != null) {
         Object var4 = var2.apply(var1, var3);
         if (var4 != null) {
            this.put(var1, var4);
            return var4;
         } else {
            this.remove(var1);
            return null;
         }
      } else {
         return null;
      }
   }

   default V compute(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
      Objects.requireNonNull(var2);
      Object var3 = this.get(var1);
      Object var4 = var2.apply(var1, var3);
      if (var4 == null) {
         if (var3 == null && !this.containsKey(var1)) {
            return null;
         } else {
            this.remove(var1);
            return null;
         }
      } else {
         this.put(var1, var4);
         return var4;
      }
   }

   default V merge(K var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
      Objects.requireNonNull(var3);
      Objects.requireNonNull(var2);
      Object var4 = this.get(var1);
      Object var5 = var4 == null ? var2 : var3.apply(var4, var2);
      if (var5 == null) {
         this.remove(var1);
      } else {
         this.put(var1, var5);
      }

      return var5;
   }

   public interface Entry<K, V> {
      K getKey();

      V getValue();

      V setValue(V var1);

      boolean equals(Object var1);

      int hashCode();

      static <K extends Comparable<? super K>, V> Comparator<Map.Entry<K, V>> comparingByKey() {
         return (Comparator)((Serializable)((var0x, var1x) -> {
            return ((Comparable)var0x.getKey()).compareTo(var1x.getKey());
         }));
      }

      static <K, V extends Comparable<? super V>> Comparator<Map.Entry<K, V>> comparingByValue() {
         return (Comparator)((Serializable)((var0x, var1x) -> {
            return ((Comparable)var0x.getValue()).compareTo(var1x.getValue());
         }));
      }

      static <K, V> Comparator<Map.Entry<K, V>> comparingByKey(Comparator<? super K> var0) {
         Objects.requireNonNull(var0);
         return (Comparator)((Serializable)((var1x, var2x) -> {
            return var0.compare(var1x.getKey(), var2x.getKey());
         }));
      }

      static <K, V> Comparator<Map.Entry<K, V>> comparingByValue(Comparator<? super V> var0) {
         Objects.requireNonNull(var0);
         return (Comparator)((Serializable)((var1x, var2x) -> {
            return var0.compare(var1x.getValue(), var2x.getValue());
         }));
      }

      // $FF: synthetic method
      private static Object $deserializeLambda$(SerializedLambda var0) {
         String var1 = var0.getImplMethodName();
         byte var2 = -1;
         switch(var1.hashCode()) {
         case -724508797:
            if (var1.equals("lambda$comparingByKey$bbdbfea9$1")) {
               var2 = 0;
            }
            break;
         case -524237750:
            if (var1.equals("lambda$comparingByValue$1065357e$1")) {
               var2 = 2;
            }
            break;
         case 864375927:
            if (var1.equals("lambda$comparingByValue$827a17d5$1")) {
               var2 = 3;
            }
            break;
         case 1397794443:
            if (var1.equals("lambda$comparingByKey$6d558cbf$1")) {
               var2 = 1;
            }
         }

         switch(var2) {
         case 0:
            if (var0.getImplMethodKind() == 6 && var0.getFunctionalInterfaceClass().equals("java/util/Comparator") && var0.getFunctionalInterfaceMethodName().equals("compare") && var0.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)I") && var0.getImplClass().equals("java/util/Map$Entry") && var0.getImplMethodSignature().equals("(Ljava/util/Map$Entry;Ljava/util/Map$Entry;)I")) {
               return (var0x, var1x) -> {
                  return ((Comparable)var0x.getKey()).compareTo(var1x.getKey());
               };
            }
            break;
         case 1:
            if (var0.getImplMethodKind() == 6 && var0.getFunctionalInterfaceClass().equals("java/util/Comparator") && var0.getFunctionalInterfaceMethodName().equals("compare") && var0.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)I") && var0.getImplClass().equals("java/util/Map$Entry") && var0.getImplMethodSignature().equals("(Ljava/util/Comparator;Ljava/util/Map$Entry;Ljava/util/Map$Entry;)I")) {
               return (var1x, var2x) -> {
                  return var0.compare(var1x.getKey(), var2x.getKey());
               };
            }
            break;
         case 2:
            if (var0.getImplMethodKind() == 6 && var0.getFunctionalInterfaceClass().equals("java/util/Comparator") && var0.getFunctionalInterfaceMethodName().equals("compare") && var0.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)I") && var0.getImplClass().equals("java/util/Map$Entry") && var0.getImplMethodSignature().equals("(Ljava/util/Map$Entry;Ljava/util/Map$Entry;)I")) {
               return (var0x, var1x) -> {
                  return ((Comparable)var0x.getValue()).compareTo(var1x.getValue());
               };
            }
            break;
         case 3:
            if (var0.getImplMethodKind() == 6 && var0.getFunctionalInterfaceClass().equals("java/util/Comparator") && var0.getFunctionalInterfaceMethodName().equals("compare") && var0.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)I") && var0.getImplClass().equals("java/util/Map$Entry") && var0.getImplMethodSignature().equals("(Ljava/util/Comparator;Ljava/util/Map$Entry;Ljava/util/Map$Entry;)I")) {
               return (var1x, var2x) -> {
                  return var0.compare(var1x.getValue(), var2x.getValue());
               };
            }
         }

         throw new IllegalArgumentException("Invalid lambda deserialization");
      }
   }
}
