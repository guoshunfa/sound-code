package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import sun.misc.SharedSecrets;

public class Hashtable<K, V> extends Dictionary<K, V> implements Map<K, V>, Cloneable, Serializable {
   private transient Hashtable.Entry<?, ?>[] table;
   private transient int count;
   private int threshold;
   private float loadFactor;
   private transient int modCount;
   private static final long serialVersionUID = 1421746759512286392L;
   private static final int MAX_ARRAY_SIZE = 2147483639;
   private transient volatile Set<K> keySet;
   private transient volatile Set<Map.Entry<K, V>> entrySet;
   private transient volatile Collection<V> values;
   private static final int KEYS = 0;
   private static final int VALUES = 1;
   private static final int ENTRIES = 2;

   public Hashtable(int var1, float var2) {
      this.modCount = 0;
      if (var1 < 0) {
         throw new IllegalArgumentException("Illegal Capacity: " + var1);
      } else if (var2 > 0.0F && !Float.isNaN(var2)) {
         if (var1 == 0) {
            var1 = 1;
         }

         this.loadFactor = var2;
         this.table = new Hashtable.Entry[var1];
         this.threshold = (int)Math.min((float)var1 * var2, 2.14748365E9F);
      } else {
         throw new IllegalArgumentException("Illegal Load: " + var2);
      }
   }

   public Hashtable(int var1) {
      this(var1, 0.75F);
   }

   public Hashtable() {
      this(11, 0.75F);
   }

   public Hashtable(Map<? extends K, ? extends V> var1) {
      this(Math.max(2 * var1.size(), 11), 0.75F);
      this.putAll(var1);
   }

   public synchronized int size() {
      return this.count;
   }

   public synchronized boolean isEmpty() {
      return this.count == 0;
   }

   public synchronized Enumeration<K> keys() {
      return this.getEnumeration(0);
   }

   public synchronized Enumeration<V> elements() {
      return this.getEnumeration(1);
   }

   public synchronized boolean contains(Object var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         Hashtable.Entry[] var2 = this.table;
         int var3 = var2.length;

         while(var3-- > 0) {
            for(Hashtable.Entry var4 = var2[var3]; var4 != null; var4 = var4.next) {
               if (var4.value.equals(var1)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean containsValue(Object var1) {
      return this.contains(var1);
   }

   public synchronized boolean containsKey(Object var1) {
      Hashtable.Entry[] var2 = this.table;
      int var3 = var1.hashCode();
      int var4 = (var3 & Integer.MAX_VALUE) % var2.length;

      for(Hashtable.Entry var5 = var2[var4]; var5 != null; var5 = var5.next) {
         if (var5.hash == var3 && var5.key.equals(var1)) {
            return true;
         }
      }

      return false;
   }

   public synchronized V get(Object var1) {
      Hashtable.Entry[] var2 = this.table;
      int var3 = var1.hashCode();
      int var4 = (var3 & Integer.MAX_VALUE) % var2.length;

      for(Hashtable.Entry var5 = var2[var4]; var5 != null; var5 = var5.next) {
         if (var5.hash == var3 && var5.key.equals(var1)) {
            return var5.value;
         }
      }

      return null;
   }

   protected void rehash() {
      int var1 = this.table.length;
      Hashtable.Entry[] var2 = this.table;
      int var3 = (var1 << 1) + 1;
      if (var3 - 2147483639 > 0) {
         if (var1 == 2147483639) {
            return;
         }

         var3 = 2147483639;
      }

      Hashtable.Entry[] var4 = new Hashtable.Entry[var3];
      ++this.modCount;
      this.threshold = (int)Math.min((float)var3 * this.loadFactor, 2.14748365E9F);
      this.table = var4;
      int var5 = var1;

      Hashtable.Entry var7;
      int var8;
      while(var5-- > 0) {
         for(Hashtable.Entry var6 = var2[var5]; var6 != null; var4[var8] = var7) {
            var7 = var6;
            var6 = var6.next;
            var8 = (var7.hash & Integer.MAX_VALUE) % var3;
            var7.next = var4[var8];
         }
      }

   }

   private void addEntry(int var1, K var2, V var3, int var4) {
      ++this.modCount;
      Hashtable.Entry[] var5 = this.table;
      if (this.count >= this.threshold) {
         this.rehash();
         var5 = this.table;
         var1 = var2.hashCode();
         var4 = (var1 & Integer.MAX_VALUE) % var5.length;
      }

      Hashtable.Entry var6 = var5[var4];
      var5[var4] = new Hashtable.Entry(var1, var2, var3, var6);
      ++this.count;
   }

   public synchronized V put(K var1, V var2) {
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         Hashtable.Entry[] var3 = this.table;
         int var4 = var1.hashCode();
         int var5 = (var4 & Integer.MAX_VALUE) % var3.length;

         for(Hashtable.Entry var6 = var3[var5]; var6 != null; var6 = var6.next) {
            if (var6.hash == var4 && var6.key.equals(var1)) {
               Object var7 = var6.value;
               var6.value = var2;
               return var7;
            }
         }

         this.addEntry(var4, var1, var2, var5);
         return null;
      }
   }

   public synchronized V remove(Object var1) {
      Hashtable.Entry[] var2 = this.table;
      int var3 = var1.hashCode();
      int var4 = (var3 & Integer.MAX_VALUE) % var2.length;
      Hashtable.Entry var5 = var2[var4];

      for(Hashtable.Entry var6 = null; var5 != null; var5 = var5.next) {
         if (var5.hash == var3 && var5.key.equals(var1)) {
            ++this.modCount;
            if (var6 != null) {
               var6.next = var5.next;
            } else {
               var2[var4] = var5.next;
            }

            --this.count;
            Object var7 = var5.value;
            var5.value = null;
            return var7;
         }

         var6 = var5;
      }

      return null;
   }

   public synchronized void putAll(Map<? extends K, ? extends V> var1) {
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         this.put(var3.getKey(), var3.getValue());
      }

   }

   public synchronized void clear() {
      Hashtable.Entry[] var1 = this.table;
      ++this.modCount;
      int var2 = var1.length;

      while(true) {
         --var2;
         if (var2 < 0) {
            this.count = 0;
            return;
         }

         var1[var2] = null;
      }
   }

   public synchronized Object clone() {
      try {
         Hashtable var1 = (Hashtable)super.clone();
         var1.table = new Hashtable.Entry[this.table.length];

         for(int var2 = this.table.length; var2-- > 0; var1.table[var2] = this.table[var2] != null ? (Hashtable.Entry)this.table[var2].clone() : null) {
         }

         var1.keySet = null;
         var1.entrySet = null;
         var1.values = null;
         var1.modCount = 0;
         return var1;
      } catch (CloneNotSupportedException var3) {
         throw new InternalError(var3);
      }
   }

   public synchronized String toString() {
      int var1 = this.size() - 1;
      if (var1 == -1) {
         return "{}";
      } else {
         StringBuilder var2 = new StringBuilder();
         Iterator var3 = this.entrySet().iterator();
         var2.append('{');
         int var4 = 0;

         while(true) {
            Map.Entry var5 = (Map.Entry)var3.next();
            Object var6 = var5.getKey();
            Object var7 = var5.getValue();
            var2.append(var6 == this ? "(this Map)" : var6.toString());
            var2.append('=');
            var2.append(var7 == this ? "(this Map)" : var7.toString());
            if (var4 == var1) {
               return var2.append('}').toString();
            }

            var2.append(", ");
            ++var4;
         }
      }
   }

   private <T> Enumeration<T> getEnumeration(int var1) {
      return (Enumeration)(this.count == 0 ? Collections.emptyEnumeration() : new Hashtable.Enumerator(var1, false));
   }

   private <T> Iterator<T> getIterator(int var1) {
      return (Iterator)(this.count == 0 ? Collections.emptyIterator() : new Hashtable.Enumerator(var1, true));
   }

   public Set<K> keySet() {
      if (this.keySet == null) {
         this.keySet = Collections.synchronizedSet(new Hashtable.KeySet(), this);
      }

      return this.keySet;
   }

   public Set<Map.Entry<K, V>> entrySet() {
      if (this.entrySet == null) {
         this.entrySet = Collections.synchronizedSet(new Hashtable.EntrySet(), this);
      }

      return this.entrySet;
   }

   public Collection<V> values() {
      if (this.values == null) {
         this.values = Collections.synchronizedCollection(new Hashtable.ValueCollection(), this);
      }

      return this.values;
   }

   public synchronized boolean equals(Object var1) {
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

   public synchronized int hashCode() {
      int var1 = 0;
      if (this.count != 0 && this.loadFactor >= 0.0F) {
         this.loadFactor = -this.loadFactor;
         Hashtable.Entry[] var2 = this.table;
         Hashtable.Entry[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            for(Hashtable.Entry var6 = var3[var5]; var6 != null; var6 = var6.next) {
               var1 += var6.hashCode();
            }
         }

         this.loadFactor = -this.loadFactor;
         return var1;
      } else {
         return var1;
      }
   }

   public synchronized V getOrDefault(Object var1, V var2) {
      Object var3 = this.get(var1);
      return null == var3 ? var2 : var3;
   }

   public synchronized void forEach(BiConsumer<? super K, ? super V> var1) {
      Objects.requireNonNull(var1);
      int var2 = this.modCount;
      Hashtable.Entry[] var3 = this.table;
      Hashtable.Entry[] var4 = var3;
      int var5 = var3.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Hashtable.Entry var7 = var4[var6];

         while(var7 != null) {
            var1.accept(var7.key, var7.value);
            var7 = var7.next;
            if (var2 != this.modCount) {
               throw new ConcurrentModificationException();
            }
         }
      }

   }

   public synchronized void replaceAll(BiFunction<? super K, ? super V, ? extends V> var1) {
      Objects.requireNonNull(var1);
      int var2 = this.modCount;
      Hashtable.Entry[] var3 = (Hashtable.Entry[])this.table;
      Hashtable.Entry[] var4 = var3;
      int var5 = var3.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Hashtable.Entry var7 = var4[var6];

         while(var7 != null) {
            var7.value = Objects.requireNonNull(var1.apply(var7.key, var7.value));
            var7 = var7.next;
            if (var2 != this.modCount) {
               throw new ConcurrentModificationException();
            }
         }
      }

   }

   public synchronized V putIfAbsent(K var1, V var2) {
      Objects.requireNonNull(var2);
      Hashtable.Entry[] var3 = this.table;
      int var4 = var1.hashCode();
      int var5 = (var4 & Integer.MAX_VALUE) % var3.length;

      for(Hashtable.Entry var6 = var3[var5]; var6 != null; var6 = var6.next) {
         if (var6.hash == var4 && var6.key.equals(var1)) {
            Object var7 = var6.value;
            if (var7 == null) {
               var6.value = var2;
            }

            return var7;
         }
      }

      this.addEntry(var4, var1, var2, var5);
      return null;
   }

   public synchronized boolean remove(Object var1, Object var2) {
      Objects.requireNonNull(var2);
      Hashtable.Entry[] var3 = this.table;
      int var4 = var1.hashCode();
      int var5 = (var4 & Integer.MAX_VALUE) % var3.length;
      Hashtable.Entry var6 = var3[var5];

      for(Hashtable.Entry var7 = null; var6 != null; var6 = var6.next) {
         if (var6.hash == var4 && var6.key.equals(var1) && var6.value.equals(var2)) {
            ++this.modCount;
            if (var7 != null) {
               var7.next = var6.next;
            } else {
               var3[var5] = var6.next;
            }

            --this.count;
            var6.value = null;
            return true;
         }

         var7 = var6;
      }

      return false;
   }

   public synchronized boolean replace(K var1, V var2, V var3) {
      Objects.requireNonNull(var2);
      Objects.requireNonNull(var3);
      Hashtable.Entry[] var4 = this.table;
      int var5 = var1.hashCode();
      int var6 = (var5 & Integer.MAX_VALUE) % var4.length;

      for(Hashtable.Entry var7 = var4[var6]; var7 != null; var7 = var7.next) {
         if (var7.hash == var5 && var7.key.equals(var1)) {
            if (var7.value.equals(var2)) {
               var7.value = var3;
               return true;
            }

            return false;
         }
      }

      return false;
   }

   public synchronized V replace(K var1, V var2) {
      Objects.requireNonNull(var2);
      Hashtable.Entry[] var3 = this.table;
      int var4 = var1.hashCode();
      int var5 = (var4 & Integer.MAX_VALUE) % var3.length;

      for(Hashtable.Entry var6 = var3[var5]; var6 != null; var6 = var6.next) {
         if (var6.hash == var4 && var6.key.equals(var1)) {
            Object var7 = var6.value;
            var6.value = var2;
            return var7;
         }
      }

      return null;
   }

   public synchronized V computeIfAbsent(K var1, Function<? super K, ? extends V> var2) {
      Objects.requireNonNull(var2);
      Hashtable.Entry[] var3 = this.table;
      int var4 = var1.hashCode();
      int var5 = (var4 & Integer.MAX_VALUE) % var3.length;

      for(Hashtable.Entry var6 = var3[var5]; var6 != null; var6 = var6.next) {
         if (var6.hash == var4 && var6.key.equals(var1)) {
            return var6.value;
         }
      }

      Object var7 = var2.apply(var1);
      if (var7 != null) {
         this.addEntry(var4, var1, var7, var5);
      }

      return var7;
   }

   public synchronized V computeIfPresent(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
      Objects.requireNonNull(var2);
      Hashtable.Entry[] var3 = this.table;
      int var4 = var1.hashCode();
      int var5 = (var4 & Integer.MAX_VALUE) % var3.length;
      Hashtable.Entry var6 = var3[var5];

      for(Hashtable.Entry var7 = null; var6 != null; var6 = var6.next) {
         if (var6.hash == var4 && var6.key.equals(var1)) {
            Object var8 = var2.apply(var1, var6.value);
            if (var8 == null) {
               ++this.modCount;
               if (var7 != null) {
                  var7.next = var6.next;
               } else {
                  var3[var5] = var6.next;
               }

               --this.count;
            } else {
               var6.value = var8;
            }

            return var8;
         }

         var7 = var6;
      }

      return null;
   }

   public synchronized V compute(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
      Objects.requireNonNull(var2);
      Hashtable.Entry[] var3 = this.table;
      int var4 = var1.hashCode();
      int var5 = (var4 & Integer.MAX_VALUE) % var3.length;
      Hashtable.Entry var6 = var3[var5];

      for(Hashtable.Entry var7 = null; var6 != null; var6 = var6.next) {
         if (var6.hash == var4 && Objects.equals(var6.key, var1)) {
            Object var8 = var2.apply(var1, var6.value);
            if (var8 == null) {
               ++this.modCount;
               if (var7 != null) {
                  var7.next = var6.next;
               } else {
                  var3[var5] = var6.next;
               }

               --this.count;
            } else {
               var6.value = var8;
            }

            return var8;
         }

         var7 = var6;
      }

      Object var9 = var2.apply(var1, (Object)null);
      if (var9 != null) {
         this.addEntry(var4, var1, var9, var5);
      }

      return var9;
   }

   public synchronized V merge(K var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
      Objects.requireNonNull(var3);
      Hashtable.Entry[] var4 = this.table;
      int var5 = var1.hashCode();
      int var6 = (var5 & Integer.MAX_VALUE) % var4.length;
      Hashtable.Entry var7 = var4[var6];

      for(Hashtable.Entry var8 = null; var7 != null; var7 = var7.next) {
         if (var7.hash == var5 && var7.key.equals(var1)) {
            Object var9 = var3.apply(var7.value, var2);
            if (var9 == null) {
               ++this.modCount;
               if (var8 != null) {
                  var8.next = var7.next;
               } else {
                  var4[var6] = var7.next;
               }

               --this.count;
            } else {
               var7.value = var9;
            }

            return var9;
         }

         var8 = var7;
      }

      if (var2 != null) {
         this.addEntry(var5, var1, var2, var6);
      }

      return var2;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      Hashtable.Entry var2 = null;
      synchronized(this) {
         var1.defaultWriteObject();
         var1.writeInt(this.table.length);
         var1.writeInt(this.count);
         int var4 = 0;

         while(true) {
            if (var4 >= this.table.length) {
               break;
            }

            for(Hashtable.Entry var5 = this.table[var4]; var5 != null; var5 = var5.next) {
               var2 = new Hashtable.Entry(0, var5.key, var5.value, var2);
            }

            ++var4;
         }
      }

      while(var2 != null) {
         var1.writeObject(var2.key);
         var1.writeObject(var2.value);
         var2 = var2.next;
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      if (this.loadFactor > 0.0F && !Float.isNaN(this.loadFactor)) {
         int var2 = var1.readInt();
         int var3 = var1.readInt();
         if (var3 < 0) {
            throw new StreamCorruptedException("Illegal # of Elements: " + var3);
         } else {
            var2 = Math.max(var2, (int)((float)var3 / this.loadFactor) + 1);
            int var4 = (int)((float)(var3 + var3 / 20) / this.loadFactor) + 3;
            if (var4 > var3 && (var4 & 1) == 0) {
               --var4;
            }

            var4 = Math.min(var4, var2);
            if (var4 < 0) {
               var4 = var2;
            }

            SharedSecrets.getJavaOISAccess().checkArray(var1, Map.Entry[].class, var4);
            this.table = new Hashtable.Entry[var4];
            this.threshold = (int)Math.min((float)var4 * this.loadFactor, 2.14748365E9F);

            for(this.count = 0; var3 > 0; --var3) {
               Object var5 = var1.readObject();
               Object var6 = var1.readObject();
               this.reconstitutionPut(this.table, var5, var6);
            }

         }
      } else {
         throw new StreamCorruptedException("Illegal Load: " + this.loadFactor);
      }
   }

   private void reconstitutionPut(Hashtable.Entry<?, ?>[] var1, K var2, V var3) throws StreamCorruptedException {
      if (var3 == null) {
         throw new StreamCorruptedException();
      } else {
         int var4 = var2.hashCode();
         int var5 = (var4 & Integer.MAX_VALUE) % var1.length;

         Hashtable.Entry var6;
         for(var6 = var1[var5]; var6 != null; var6 = var6.next) {
            if (var6.hash == var4 && var6.key.equals(var2)) {
               throw new StreamCorruptedException();
            }
         }

         var6 = var1[var5];
         var1[var5] = new Hashtable.Entry(var4, var2, var3, var6);
         ++this.count;
      }
   }

   private class Enumerator<T> implements Enumeration<T>, Iterator<T> {
      Hashtable.Entry<?, ?>[] table;
      int index;
      Hashtable.Entry<?, ?> entry;
      Hashtable.Entry<?, ?> lastReturned;
      int type;
      boolean iterator;
      protected int expectedModCount;

      Enumerator(int var2, boolean var3) {
         this.table = Hashtable.this.table;
         this.index = this.table.length;
         this.expectedModCount = Hashtable.this.modCount;
         this.type = var2;
         this.iterator = var3;
      }

      public boolean hasMoreElements() {
         Hashtable.Entry var1 = this.entry;
         int var2 = this.index;

         for(Hashtable.Entry[] var3 = this.table; var1 == null && var2 > 0; var1 = var3[var2]) {
            --var2;
         }

         this.entry = var1;
         this.index = var2;
         return var1 != null;
      }

      public T nextElement() {
         Hashtable.Entry var1 = this.entry;
         int var2 = this.index;

         for(Hashtable.Entry[] var3 = this.table; var1 == null && var2 > 0; var1 = var3[var2]) {
            --var2;
         }

         this.entry = var1;
         this.index = var2;
         if (var1 != null) {
            Hashtable.Entry var4 = this.lastReturned = this.entry;
            this.entry = var4.next;
            return this.type == 0 ? var4.key : (this.type == 1 ? var4.value : var4);
         } else {
            throw new NoSuchElementException("Hashtable Enumerator");
         }
      }

      public boolean hasNext() {
         return this.hasMoreElements();
      }

      public T next() {
         if (Hashtable.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else {
            return this.nextElement();
         }
      }

      public void remove() {
         if (!this.iterator) {
            throw new UnsupportedOperationException();
         } else if (this.lastReturned == null) {
            throw new IllegalStateException("Hashtable Enumerator");
         } else if (Hashtable.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else {
            synchronized(Hashtable.this) {
               Hashtable.Entry[] var2 = Hashtable.this.table;
               int var3 = (this.lastReturned.hash & Integer.MAX_VALUE) % var2.length;
               Hashtable.Entry var4 = var2[var3];

               for(Hashtable.Entry var5 = null; var4 != null; var4 = var4.next) {
                  if (var4 == this.lastReturned) {
                     Hashtable.this.modCount++;
                     ++this.expectedModCount;
                     if (var5 == null) {
                        var2[var3] = var4.next;
                     } else {
                        var5.next = var4.next;
                     }

                     Hashtable.this.count--;
                     this.lastReturned = null;
                     return;
                  }

                  var5 = var4;
               }

               throw new ConcurrentModificationException();
            }
         }
      }
   }

   private static class Entry<K, V> implements Map.Entry<K, V> {
      final int hash;
      final K key;
      V value;
      Hashtable.Entry<K, V> next;

      protected Entry(int var1, K var2, V var3, Hashtable.Entry<K, V> var4) {
         this.hash = var1;
         this.key = var2;
         this.value = var3;
         this.next = var4;
      }

      protected Object clone() {
         return new Hashtable.Entry(this.hash, this.key, this.value, this.next == null ? null : (Hashtable.Entry)this.next.clone());
      }

      public K getKey() {
         return this.key;
      }

      public V getValue() {
         return this.value;
      }

      public V setValue(V var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            Object var2 = this.value;
            this.value = var1;
            return var2;
         }
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof Map.Entry)) {
            return false;
         } else {
            boolean var10000;
            label38: {
               label27: {
                  Map.Entry var2 = (Map.Entry)var1;
                  if (this.key == null) {
                     if (var2.getKey() != null) {
                        break label27;
                     }
                  } else if (!this.key.equals(var2.getKey())) {
                     break label27;
                  }

                  if (this.value == null) {
                     if (var2.getValue() == null) {
                        break label38;
                     }
                  } else if (this.value.equals(var2.getValue())) {
                     break label38;
                  }
               }

               var10000 = false;
               return var10000;
            }

            var10000 = true;
            return var10000;
         }
      }

      public int hashCode() {
         return this.hash ^ Objects.hashCode(this.value);
      }

      public String toString() {
         return this.key.toString() + "=" + this.value.toString();
      }
   }

   private class ValueCollection extends AbstractCollection<V> {
      private ValueCollection() {
      }

      public Iterator<V> iterator() {
         return Hashtable.this.getIterator(1);
      }

      public int size() {
         return Hashtable.this.count;
      }

      public boolean contains(Object var1) {
         return Hashtable.this.containsValue(var1);
      }

      public void clear() {
         Hashtable.this.clear();
      }

      // $FF: synthetic method
      ValueCollection(Object var2) {
         this();
      }
   }

   private class EntrySet extends AbstractSet<Map.Entry<K, V>> {
      private EntrySet() {
      }

      public Iterator<Map.Entry<K, V>> iterator() {
         return Hashtable.this.getIterator(2);
      }

      public boolean add(Map.Entry<K, V> var1) {
         return super.add(var1);
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof Map.Entry)) {
            return false;
         } else {
            Map.Entry var2 = (Map.Entry)var1;
            Object var3 = var2.getKey();
            Hashtable.Entry[] var4 = Hashtable.this.table;
            int var5 = var3.hashCode();
            int var6 = (var5 & Integer.MAX_VALUE) % var4.length;

            for(Hashtable.Entry var7 = var4[var6]; var7 != null; var7 = var7.next) {
               if (var7.hash == var5 && var7.equals(var2)) {
                  return true;
               }
            }

            return false;
         }
      }

      public boolean remove(Object var1) {
         if (!(var1 instanceof Map.Entry)) {
            return false;
         } else {
            Map.Entry var2 = (Map.Entry)var1;
            Object var3 = var2.getKey();
            Hashtable.Entry[] var4 = Hashtable.this.table;
            int var5 = var3.hashCode();
            int var6 = (var5 & Integer.MAX_VALUE) % var4.length;
            Hashtable.Entry var7 = var4[var6];

            for(Hashtable.Entry var8 = null; var7 != null; var7 = var7.next) {
               if (var7.hash == var5 && var7.equals(var2)) {
                  Hashtable.this.modCount++;
                  if (var8 != null) {
                     var8.next = var7.next;
                  } else {
                     var4[var6] = var7.next;
                  }

                  Hashtable.this.count--;
                  var7.value = null;
                  return true;
               }

               var8 = var7;
            }

            return false;
         }
      }

      public int size() {
         return Hashtable.this.count;
      }

      public void clear() {
         Hashtable.this.clear();
      }

      // $FF: synthetic method
      EntrySet(Object var2) {
         this();
      }
   }

   private class KeySet extends AbstractSet<K> {
      private KeySet() {
      }

      public Iterator<K> iterator() {
         return Hashtable.this.getIterator(0);
      }

      public int size() {
         return Hashtable.this.count;
      }

      public boolean contains(Object var1) {
         return Hashtable.this.containsKey(var1);
      }

      public boolean remove(Object var1) {
         return Hashtable.this.remove(var1) != null;
      }

      public void clear() {
         Hashtable.this.clear();
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }
}
