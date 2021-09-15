package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import sun.misc.SharedSecrets;

public class EnumMap<K extends Enum<K>, V> extends AbstractMap<K, V> implements Serializable, Cloneable {
   private final Class<K> keyType;
   private transient K[] keyUniverse;
   private transient Object[] vals;
   private transient int size = 0;
   private static final Object NULL = new Object() {
      public int hashCode() {
         return 0;
      }

      public String toString() {
         return "java.util.EnumMap.NULL";
      }
   };
   private static final Enum<?>[] ZERO_LENGTH_ENUM_ARRAY = new Enum[0];
   private transient Set<Map.Entry<K, V>> entrySet;
   private static final long serialVersionUID = 458661240069192865L;

   private Object maskNull(Object var1) {
      return var1 == null ? NULL : var1;
   }

   private V unmaskNull(Object var1) {
      return var1 == NULL ? null : var1;
   }

   public EnumMap(Class<K> var1) {
      this.keyType = var1;
      this.keyUniverse = getKeyUniverse(var1);
      this.vals = new Object[this.keyUniverse.length];
   }

   public EnumMap(EnumMap<K, ? extends V> var1) {
      this.keyType = var1.keyType;
      this.keyUniverse = var1.keyUniverse;
      this.vals = (Object[])var1.vals.clone();
      this.size = var1.size;
   }

   public EnumMap(Map<K, ? extends V> var1) {
      if (var1 instanceof EnumMap) {
         EnumMap var2 = (EnumMap)var1;
         this.keyType = var2.keyType;
         this.keyUniverse = var2.keyUniverse;
         this.vals = (Object[])var2.vals.clone();
         this.size = var2.size;
      } else {
         if (var1.isEmpty()) {
            throw new IllegalArgumentException("Specified map is empty");
         }

         this.keyType = ((Enum)var1.keySet().iterator().next()).getDeclaringClass();
         this.keyUniverse = getKeyUniverse(this.keyType);
         this.vals = new Object[this.keyUniverse.length];
         this.putAll(var1);
      }

   }

   public int size() {
      return this.size;
   }

   public boolean containsValue(Object var1) {
      var1 = this.maskNull(var1);
      Object[] var2 = this.vals;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Object var5 = var2[var4];
         if (var1.equals(var5)) {
            return true;
         }
      }

      return false;
   }

   public boolean containsKey(Object var1) {
      return this.isValidKey(var1) && this.vals[((Enum)var1).ordinal()] != null;
   }

   private boolean containsMapping(Object var1, Object var2) {
      return this.isValidKey(var1) && this.maskNull(var2).equals(this.vals[((Enum)var1).ordinal()]);
   }

   public V get(Object var1) {
      return this.isValidKey(var1) ? this.unmaskNull(this.vals[((Enum)var1).ordinal()]) : null;
   }

   public V put(K var1, V var2) {
      this.typeCheck(var1);
      int var3 = var1.ordinal();
      Object var4 = this.vals[var3];
      this.vals[var3] = this.maskNull(var2);
      if (var4 == null) {
         ++this.size;
      }

      return this.unmaskNull(var4);
   }

   public V remove(Object var1) {
      if (!this.isValidKey(var1)) {
         return null;
      } else {
         int var2 = ((Enum)var1).ordinal();
         Object var3 = this.vals[var2];
         this.vals[var2] = null;
         if (var3 != null) {
            --this.size;
         }

         return this.unmaskNull(var3);
      }
   }

   private boolean removeMapping(Object var1, Object var2) {
      if (!this.isValidKey(var1)) {
         return false;
      } else {
         int var3 = ((Enum)var1).ordinal();
         if (this.maskNull(var2).equals(this.vals[var3])) {
            this.vals[var3] = null;
            --this.size;
            return true;
         } else {
            return false;
         }
      }
   }

   private boolean isValidKey(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         Class var2 = var1.getClass();
         return var2 == this.keyType || var2.getSuperclass() == this.keyType;
      }
   }

   public void putAll(Map<? extends K, ? extends V> var1) {
      if (var1 instanceof EnumMap) {
         EnumMap var2 = (EnumMap)var1;
         if (var2.keyType != this.keyType) {
            if (var2.isEmpty()) {
               return;
            }

            throw new ClassCastException(var2.keyType + " != " + this.keyType);
         }

         for(int var3 = 0; var3 < this.keyUniverse.length; ++var3) {
            Object var4 = var2.vals[var3];
            if (var4 != null) {
               if (this.vals[var3] == null) {
                  ++this.size;
               }

               this.vals[var3] = var4;
            }
         }
      } else {
         super.putAll(var1);
      }

   }

   public void clear() {
      Arrays.fill(this.vals, (Object)null);
      this.size = 0;
   }

   public Set<K> keySet() {
      Object var1 = this.keySet;
      if (var1 == null) {
         var1 = new EnumMap.KeySet();
         this.keySet = (Set)var1;
      }

      return (Set)var1;
   }

   public Collection<V> values() {
      Object var1 = this.values;
      if (var1 == null) {
         var1 = new EnumMap.Values();
         this.values = (Collection)var1;
      }

      return (Collection)var1;
   }

   public Set<Map.Entry<K, V>> entrySet() {
      Set var1 = this.entrySet;
      return var1 != null ? var1 : (this.entrySet = new EnumMap.EntrySet());
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof EnumMap) {
         return this.equals((EnumMap)var1);
      } else if (!(var1 instanceof Map)) {
         return false;
      } else {
         Map var2 = (Map)var1;
         if (this.size != var2.size()) {
            return false;
         } else {
            for(int var3 = 0; var3 < this.keyUniverse.length; ++var3) {
               if (null != this.vals[var3]) {
                  Enum var4 = this.keyUniverse[var3];
                  Object var5 = this.unmaskNull(this.vals[var3]);
                  if (null == var5) {
                     if (null != var2.get(var4) || !var2.containsKey(var4)) {
                        return false;
                     }
                  } else if (!var5.equals(var2.get(var4))) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }

   private boolean equals(EnumMap<?, ?> var1) {
      if (var1.keyType != this.keyType) {
         return this.size == 0 && var1.size == 0;
      } else {
         for(int var2 = 0; var2 < this.keyUniverse.length; ++var2) {
            Object var3 = this.vals[var2];
            Object var4 = var1.vals[var2];
            if (var4 != var3 && (var4 == null || !var4.equals(var3))) {
               return false;
            }
         }

         return true;
      }
   }

   public int hashCode() {
      int var1 = 0;

      for(int var2 = 0; var2 < this.keyUniverse.length; ++var2) {
         if (null != this.vals[var2]) {
            var1 += this.entryHashCode(var2);
         }
      }

      return var1;
   }

   private int entryHashCode(int var1) {
      return this.keyUniverse[var1].hashCode() ^ this.vals[var1].hashCode();
   }

   public EnumMap<K, V> clone() {
      EnumMap var1 = null;

      try {
         var1 = (EnumMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new AssertionError();
      }

      var1.vals = (Object[])var1.vals.clone();
      var1.entrySet = null;
      return var1;
   }

   private void typeCheck(K var1) {
      Class var2 = var1.getClass();
      if (var2 != this.keyType && var2.getSuperclass() != this.keyType) {
         throw new ClassCastException(var2 + " != " + this.keyType);
      }
   }

   private static <K extends Enum<K>> K[] getKeyUniverse(Class<K> var0) {
      return SharedSecrets.getJavaLangAccess().getEnumConstantsShared(var0);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeInt(this.size);
      int var2 = this.size;

      for(int var3 = 0; var2 > 0; ++var3) {
         if (null != this.vals[var3]) {
            var1.writeObject(this.keyUniverse[var3]);
            var1.writeObject(this.unmaskNull(this.vals[var3]));
            --var2;
         }
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.keyUniverse = getKeyUniverse(this.keyType);
      this.vals = new Object[this.keyUniverse.length];
      int var2 = var1.readInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         Enum var4 = (Enum)var1.readObject();
         Object var5 = var1.readObject();
         this.put(var4, var5);
      }

   }

   private class EntryIterator extends EnumMap<K, V>.EnumMapIterator<Map.Entry<K, V>> {
      private EnumMap<K, V>.EntryIterator.Entry lastReturnedEntry;

      private EntryIterator() {
         super(null);
      }

      public Map.Entry<K, V> next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.lastReturnedEntry = new EnumMap.EntryIterator.Entry(this.index++);
            return this.lastReturnedEntry;
         }
      }

      public void remove() {
         this.lastReturnedIndex = null == this.lastReturnedEntry ? -1 : this.lastReturnedEntry.index;
         super.remove();
         this.lastReturnedEntry.index = this.lastReturnedIndex;
         this.lastReturnedEntry = null;
      }

      // $FF: synthetic method
      EntryIterator(Object var2) {
         this();
      }

      private class Entry implements Map.Entry<K, V> {
         private int index;

         private Entry(int var2) {
            this.index = var2;
         }

         public K getKey() {
            this.checkIndexForEntryUse();
            return EnumMap.this.keyUniverse[this.index];
         }

         public V getValue() {
            this.checkIndexForEntryUse();
            return EnumMap.this.unmaskNull(EnumMap.this.vals[this.index]);
         }

         public V setValue(V var1) {
            this.checkIndexForEntryUse();
            Object var2 = EnumMap.this.unmaskNull(EnumMap.this.vals[this.index]);
            EnumMap.this.vals[this.index] = EnumMap.this.maskNull(var1);
            return var2;
         }

         public boolean equals(Object var1) {
            if (this.index < 0) {
               return var1 == this;
            } else if (!(var1 instanceof Map.Entry)) {
               return false;
            } else {
               Map.Entry var2 = (Map.Entry)var1;
               Object var3 = EnumMap.this.unmaskNull(EnumMap.this.vals[this.index]);
               Object var4 = var2.getValue();
               return var2.getKey() == EnumMap.this.keyUniverse[this.index] && (var3 == var4 || var3 != null && var3.equals(var4));
            }
         }

         public int hashCode() {
            return this.index < 0 ? super.hashCode() : EnumMap.this.entryHashCode(this.index);
         }

         public String toString() {
            return this.index < 0 ? super.toString() : EnumMap.this.keyUniverse[this.index] + "=" + EnumMap.this.unmaskNull(EnumMap.this.vals[this.index]);
         }

         private void checkIndexForEntryUse() {
            if (this.index < 0) {
               throw new IllegalStateException("Entry was removed");
            }
         }

         // $FF: synthetic method
         Entry(int var2, Object var3) {
            this(var2);
         }
      }
   }

   private class ValueIterator extends EnumMap<K, V>.EnumMapIterator<V> {
      private ValueIterator() {
         super(null);
      }

      public V next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.lastReturnedIndex = this.index++;
            return EnumMap.this.unmaskNull(EnumMap.this.vals[this.lastReturnedIndex]);
         }
      }

      // $FF: synthetic method
      ValueIterator(Object var2) {
         this();
      }
   }

   private class KeyIterator extends EnumMap<K, V>.EnumMapIterator<K> {
      private KeyIterator() {
         super(null);
      }

      public K next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.lastReturnedIndex = this.index++;
            return EnumMap.this.keyUniverse[this.lastReturnedIndex];
         }
      }

      // $FF: synthetic method
      KeyIterator(Object var2) {
         this();
      }
   }

   private abstract class EnumMapIterator<T> implements Iterator<T> {
      int index;
      int lastReturnedIndex;

      private EnumMapIterator() {
         this.index = 0;
         this.lastReturnedIndex = -1;
      }

      public boolean hasNext() {
         while(this.index < EnumMap.this.vals.length && EnumMap.this.vals[this.index] == null) {
            ++this.index;
         }

         return this.index != EnumMap.this.vals.length;
      }

      public void remove() {
         this.checkLastReturnedIndex();
         if (EnumMap.this.vals[this.lastReturnedIndex] != null) {
            EnumMap.this.vals[this.lastReturnedIndex] = null;
            EnumMap.this.size--;
         }

         this.lastReturnedIndex = -1;
      }

      private void checkLastReturnedIndex() {
         if (this.lastReturnedIndex < 0) {
            throw new IllegalStateException();
         }
      }

      // $FF: synthetic method
      EnumMapIterator(Object var2) {
         this();
      }
   }

   private class EntrySet extends AbstractSet<Map.Entry<K, V>> {
      private EntrySet() {
      }

      public Iterator<Map.Entry<K, V>> iterator() {
         return EnumMap.this.new EntryIterator();
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof Map.Entry)) {
            return false;
         } else {
            Map.Entry var2 = (Map.Entry)var1;
            return EnumMap.this.containsMapping(var2.getKey(), var2.getValue());
         }
      }

      public boolean remove(Object var1) {
         if (!(var1 instanceof Map.Entry)) {
            return false;
         } else {
            Map.Entry var2 = (Map.Entry)var1;
            return EnumMap.this.removeMapping(var2.getKey(), var2.getValue());
         }
      }

      public int size() {
         return EnumMap.this.size;
      }

      public void clear() {
         EnumMap.this.clear();
      }

      public Object[] toArray() {
         return this.fillEntryArray(new Object[EnumMap.this.size]);
      }

      public <T> T[] toArray(T[] var1) {
         int var2 = this.size();
         if (var1.length < var2) {
            var1 = (Object[])((Object[])Array.newInstance(var1.getClass().getComponentType(), var2));
         }

         if (var1.length > var2) {
            var1[var2] = null;
         }

         return (Object[])this.fillEntryArray(var1);
      }

      private Object[] fillEntryArray(Object[] var1) {
         int var2 = 0;

         for(int var3 = 0; var3 < EnumMap.this.vals.length; ++var3) {
            if (EnumMap.this.vals[var3] != null) {
               var1[var2++] = new AbstractMap.SimpleEntry(EnumMap.this.keyUniverse[var3], EnumMap.this.unmaskNull(EnumMap.this.vals[var3]));
            }
         }

         return var1;
      }

      // $FF: synthetic method
      EntrySet(Object var2) {
         this();
      }
   }

   private class Values extends AbstractCollection<V> {
      private Values() {
      }

      public Iterator<V> iterator() {
         return EnumMap.this.new ValueIterator();
      }

      public int size() {
         return EnumMap.this.size;
      }

      public boolean contains(Object var1) {
         return EnumMap.this.containsValue(var1);
      }

      public boolean remove(Object var1) {
         var1 = EnumMap.this.maskNull(var1);

         for(int var2 = 0; var2 < EnumMap.this.vals.length; ++var2) {
            if (var1.equals(EnumMap.this.vals[var2])) {
               EnumMap.this.vals[var2] = null;
               EnumMap.this.size--;
               return true;
            }
         }

         return false;
      }

      public void clear() {
         EnumMap.this.clear();
      }

      // $FF: synthetic method
      Values(Object var2) {
         this();
      }
   }

   private class KeySet extends AbstractSet<K> {
      private KeySet() {
      }

      public Iterator<K> iterator() {
         return EnumMap.this.new KeyIterator();
      }

      public int size() {
         return EnumMap.this.size;
      }

      public boolean contains(Object var1) {
         return EnumMap.this.containsKey(var1);
      }

      public boolean remove(Object var1) {
         int var2 = EnumMap.this.size;
         EnumMap.this.remove(var1);
         return EnumMap.this.size != var2;
      }

      public void clear() {
         EnumMap.this.clear();
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }
}
