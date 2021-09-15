package java.util;

public interface NavigableMap<K, V> extends SortedMap<K, V> {
   Map.Entry<K, V> lowerEntry(K var1);

   K lowerKey(K var1);

   Map.Entry<K, V> floorEntry(K var1);

   K floorKey(K var1);

   Map.Entry<K, V> ceilingEntry(K var1);

   K ceilingKey(K var1);

   Map.Entry<K, V> higherEntry(K var1);

   K higherKey(K var1);

   Map.Entry<K, V> firstEntry();

   Map.Entry<K, V> lastEntry();

   Map.Entry<K, V> pollFirstEntry();

   Map.Entry<K, V> pollLastEntry();

   NavigableMap<K, V> descendingMap();

   NavigableSet<K> navigableKeySet();

   NavigableSet<K> descendingKeySet();

   NavigableMap<K, V> subMap(K var1, boolean var2, K var3, boolean var4);

   NavigableMap<K, V> headMap(K var1, boolean var2);

   NavigableMap<K, V> tailMap(K var1, boolean var2);

   SortedMap<K, V> subMap(K var1, K var2);

   SortedMap<K, V> headMap(K var1);

   SortedMap<K, V> tailMap(K var1);
}
