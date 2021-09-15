package java.util;

public abstract class Dictionary<K, V> {
   public abstract int size();

   public abstract boolean isEmpty();

   public abstract Enumeration<K> keys();

   public abstract Enumeration<V> elements();

   public abstract V get(Object var1);

   public abstract V put(K var1, V var2);

   public abstract V remove(Object var1);
}
