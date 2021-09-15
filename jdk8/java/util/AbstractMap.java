package java.util;

import java.io.Serializable;

public abstract class AbstractMap<K, V> implements Map<K, V> {
   transient Set<K> keySet;
   transient Collection<V> values;

   protected AbstractMap() {
   }

   public int size() {
      return this.entrySet().size();
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public boolean containsValue(Object var1) {
      Iterator var2 = this.entrySet().iterator();
      Map.Entry var3;
      if (var1 == null) {
         while(var2.hasNext()) {
            var3 = (Map.Entry)var2.next();
            if (var3.getValue() == null) {
               return true;
            }
         }
      } else {
         while(var2.hasNext()) {
            var3 = (Map.Entry)var2.next();
            if (var1.equals(var3.getValue())) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean containsKey(Object var1) {
      Iterator var2 = this.entrySet().iterator();
      Map.Entry var3;
      if (var1 == null) {
         while(var2.hasNext()) {
            var3 = (Map.Entry)var2.next();
            if (var3.getKey() == null) {
               return true;
            }
         }
      } else {
         while(var2.hasNext()) {
            var3 = (Map.Entry)var2.next();
            if (var1.equals(var3.getKey())) {
               return true;
            }
         }
      }

      return false;
   }

   public V get(Object var1) {
      Iterator var2 = this.entrySet().iterator();
      Map.Entry var3;
      if (var1 == null) {
         while(var2.hasNext()) {
            var3 = (Map.Entry)var2.next();
            if (var3.getKey() == null) {
               return var3.getValue();
            }
         }
      } else {
         while(var2.hasNext()) {
            var3 = (Map.Entry)var2.next();
            if (var1.equals(var3.getKey())) {
               return var3.getValue();
            }
         }
      }

      return null;
   }

   public V put(K var1, V var2) {
      throw new UnsupportedOperationException();
   }

   public V remove(Object var1) {
      Iterator var2 = this.entrySet().iterator();
      Map.Entry var3 = null;
      Map.Entry var4;
      if (var1 == null) {
         while(var3 == null && var2.hasNext()) {
            var4 = (Map.Entry)var2.next();
            if (var4.getKey() == null) {
               var3 = var4;
            }
         }
      } else {
         while(var3 == null && var2.hasNext()) {
            var4 = (Map.Entry)var2.next();
            if (var1.equals(var4.getKey())) {
               var3 = var4;
            }
         }
      }

      Object var5 = null;
      if (var3 != null) {
         var5 = var3.getValue();
         var2.remove();
      }

      return var5;
   }

   public void putAll(Map<? extends K, ? extends V> var1) {
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         this.put(var3.getKey(), var3.getValue());
      }

   }

   public void clear() {
      this.entrySet().clear();
   }

   public Set<K> keySet() {
      Object var1 = this.keySet;
      if (var1 == null) {
         var1 = new AbstractSet<K>() {
            public Iterator<K> iterator() {
               return new Iterator<K>() {
                  private Iterator<Map.Entry<K, V>> i = AbstractMap.this.entrySet().iterator();

                  public boolean hasNext() {
                     return this.i.hasNext();
                  }

                  public K next() {
                     return ((Map.Entry)this.i.next()).getKey();
                  }

                  public void remove() {
                     this.i.remove();
                  }
               };
            }

            public int size() {
               return AbstractMap.this.size();
            }

            public boolean isEmpty() {
               return AbstractMap.this.isEmpty();
            }

            public void clear() {
               AbstractMap.this.clear();
            }

            public boolean contains(Object var1) {
               return AbstractMap.this.containsKey(var1);
            }
         };
         this.keySet = (Set)var1;
      }

      return (Set)var1;
   }

   public Collection<V> values() {
      Object var1 = this.values;
      if (var1 == null) {
         var1 = new AbstractCollection<V>() {
            public Iterator<V> iterator() {
               return new Iterator<V>() {
                  private Iterator<Map.Entry<K, V>> i = AbstractMap.this.entrySet().iterator();

                  public boolean hasNext() {
                     return this.i.hasNext();
                  }

                  public V next() {
                     return ((Map.Entry)this.i.next()).getValue();
                  }

                  public void remove() {
                     this.i.remove();
                  }
               };
            }

            public int size() {
               return AbstractMap.this.size();
            }

            public boolean isEmpty() {
               return AbstractMap.this.isEmpty();
            }

            public void clear() {
               AbstractMap.this.clear();
            }

            public boolean contains(Object var1) {
               return AbstractMap.this.containsValue(var1);
            }
         };
         this.values = (Collection)var1;
      }

      return (Collection)var1;
   }

   public abstract Set<Map.Entry<K, V>> entrySet();

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Map)) {
         return false;
      } else {
         Map var2 = (Map)var1;
         if (var2.size() != this.size()) {
            return false;
         } else {
            try {
               Iterator var3 = this.entrySet().iterator();

               Object var5;
               label43:
               do {
                  Object var6;
                  do {
                     if (!var3.hasNext()) {
                        return true;
                     }

                     Map.Entry var4 = (Map.Entry)var3.next();
                     var5 = var4.getKey();
                     var6 = var4.getValue();
                     if (var6 == null) {
                        continue label43;
                     }
                  } while(var6.equals(var2.get(var5)));

                  return false;
               } while(var2.get(var5) == null && var2.containsKey(var5));

               return false;
            } catch (ClassCastException var7) {
               return false;
            } catch (NullPointerException var8) {
               return false;
            }
         }
      }
   }

   public int hashCode() {
      int var1 = 0;

      for(Iterator var2 = this.entrySet().iterator(); var2.hasNext(); var1 += ((Map.Entry)var2.next()).hashCode()) {
      }

      return var1;
   }

   public String toString() {
      Iterator var1 = this.entrySet().iterator();
      if (!var1.hasNext()) {
         return "{}";
      } else {
         StringBuilder var2 = new StringBuilder();
         var2.append('{');

         while(true) {
            Map.Entry var3 = (Map.Entry)var1.next();
            Object var4 = var3.getKey();
            Object var5 = var3.getValue();
            var2.append(var4 == this ? "(this Map)" : var4);
            var2.append('=');
            var2.append(var5 == this ? "(this Map)" : var5);
            if (!var1.hasNext()) {
               return var2.append('}').toString();
            }

            var2.append(',').append(' ');
         }
      }
   }

   protected Object clone() throws CloneNotSupportedException {
      AbstractMap var1 = (AbstractMap)super.clone();
      var1.keySet = null;
      var1.values = null;
      return var1;
   }

   private static boolean eq(Object var0, Object var1) {
      return var0 == null ? var1 == null : var0.equals(var1);
   }

   public static class SimpleImmutableEntry<K, V> implements Map.Entry<K, V>, Serializable {
      private static final long serialVersionUID = 7138329143949025153L;
      private final K key;
      private final V value;

      public SimpleImmutableEntry(K var1, V var2) {
         this.key = var1;
         this.value = var2;
      }

      public SimpleImmutableEntry(Map.Entry<? extends K, ? extends V> var1) {
         this.key = var1.getKey();
         this.value = var1.getValue();
      }

      public K getKey() {
         return this.key;
      }

      public V getValue() {
         return this.value;
      }

      public V setValue(V var1) {
         throw new UnsupportedOperationException();
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof Map.Entry)) {
            return false;
         } else {
            Map.Entry var2 = (Map.Entry)var1;
            return AbstractMap.eq(this.key, var2.getKey()) && AbstractMap.eq(this.value, var2.getValue());
         }
      }

      public int hashCode() {
         return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
      }

      public String toString() {
         return this.key + "=" + this.value;
      }
   }

   public static class SimpleEntry<K, V> implements Map.Entry<K, V>, Serializable {
      private static final long serialVersionUID = -8499721149061103585L;
      private final K key;
      private V value;

      public SimpleEntry(K var1, V var2) {
         this.key = var1;
         this.value = var2;
      }

      public SimpleEntry(Map.Entry<? extends K, ? extends V> var1) {
         this.key = var1.getKey();
         this.value = var1.getValue();
      }

      public K getKey() {
         return this.key;
      }

      public V getValue() {
         return this.value;
      }

      public V setValue(V var1) {
         Object var2 = this.value;
         this.value = var1;
         return var2;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof Map.Entry)) {
            return false;
         } else {
            Map.Entry var2 = (Map.Entry)var1;
            return AbstractMap.eq(this.key, var2.getKey()) && AbstractMap.eq(this.value, var2.getValue());
         }
      }

      public int hashCode() {
         return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
      }

      public String toString() {
         return this.key + "=" + this.value;
      }
   }
}
