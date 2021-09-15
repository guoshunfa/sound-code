package java.util;

public interface SortedMap<K, V> extends Map<K, V> {
   Comparator<? super K> comparator();

   SortedMap<K, V> subMap(K var1, K var2);

   SortedMap<K, V> headMap(K var1);

   SortedMap<K, V> tailMap(K var1);

   K firstKey();

   K lastKey();

   Set<K> keySet();

   Collection<V> values();

   Set<Map.Entry<K, V>> entrySet();
}
