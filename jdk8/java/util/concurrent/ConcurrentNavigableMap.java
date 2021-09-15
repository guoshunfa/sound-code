package java.util.concurrent;

import java.util.NavigableMap;
import java.util.NavigableSet;

public interface ConcurrentNavigableMap<K, V> extends ConcurrentMap<K, V>, NavigableMap<K, V> {
   ConcurrentNavigableMap<K, V> subMap(K var1, boolean var2, K var3, boolean var4);

   ConcurrentNavigableMap<K, V> headMap(K var1, boolean var2);

   ConcurrentNavigableMap<K, V> tailMap(K var1, boolean var2);

   ConcurrentNavigableMap<K, V> subMap(K var1, K var2);

   ConcurrentNavigableMap<K, V> headMap(K var1);

   ConcurrentNavigableMap<K, V> tailMap(K var1);

   ConcurrentNavigableMap<K, V> descendingMap();

   NavigableSet<K> navigableKeySet();

   NavigableSet<K> keySet();

   NavigableSet<K> descendingKeySet();
}
