package com.sun.xml.internal.bind.v2.util;

import com.sun.xml.internal.bind.v2.runtime.Name;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.xml.namespace.QName;

public final class QNameMap<V> {
   private static final int DEFAULT_INITIAL_CAPACITY = 16;
   private static final int MAXIMUM_CAPACITY = 1073741824;
   transient QNameMap.Entry<V>[] table = new QNameMap.Entry[16];
   transient int size;
   private int threshold = 12;
   private static final float DEFAULT_LOAD_FACTOR = 0.75F;
   private Set<QNameMap.Entry<V>> entrySet = null;

   public QNameMap() {
      this.table = new QNameMap.Entry[16];
   }

   public void put(String namespaceUri, String localname, V value) {
      assert localname != null;

      assert namespaceUri != null;

      assert localname == localname.intern();

      assert namespaceUri == namespaceUri.intern();

      int hash = hash(localname);
      int i = indexFor(hash, this.table.length);

      for(QNameMap.Entry e = this.table[i]; e != null; e = e.next) {
         if (e.hash == hash && localname == e.localName && namespaceUri == e.nsUri) {
            e.value = value;
            return;
         }
      }

      this.addEntry(hash, namespaceUri, localname, value, i);
   }

   public void put(QName name, V value) {
      this.put(name.getNamespaceURI(), name.getLocalPart(), value);
   }

   public void put(Name name, V value) {
      this.put(name.nsUri, name.localName, value);
   }

   public V get(String nsUri, String localPart) {
      QNameMap.Entry<V> e = this.getEntry(nsUri, localPart);
      return e == null ? null : e.value;
   }

   public V get(QName name) {
      return this.get(name.getNamespaceURI(), name.getLocalPart());
   }

   public int size() {
      return this.size;
   }

   public QNameMap<V> putAll(QNameMap<? extends V> map) {
      int numKeysToBeAdded = map.size();
      if (numKeysToBeAdded == 0) {
         return this;
      } else {
         if (numKeysToBeAdded > this.threshold) {
            int targetCapacity = numKeysToBeAdded;
            if (numKeysToBeAdded > 1073741824) {
               targetCapacity = 1073741824;
            }

            int newCapacity;
            for(newCapacity = this.table.length; newCapacity < targetCapacity; newCapacity <<= 1) {
            }

            if (newCapacity > this.table.length) {
               this.resize(newCapacity);
            }
         }

         Iterator var5 = map.entrySet().iterator();

         while(var5.hasNext()) {
            QNameMap.Entry<? extends V> e = (QNameMap.Entry)var5.next();
            this.put(e.nsUri, e.localName, e.getValue());
         }

         return this;
      }
   }

   private static int hash(String x) {
      int h = x.hashCode();
      h += ~(h << 9);
      h ^= h >>> 14;
      h += h << 4;
      h ^= h >>> 10;
      return h;
   }

   private static int indexFor(int h, int length) {
      return h & length - 1;
   }

   private void addEntry(int hash, String nsUri, String localName, V value, int bucketIndex) {
      QNameMap.Entry<V> e = this.table[bucketIndex];
      this.table[bucketIndex] = new QNameMap.Entry(hash, nsUri, localName, value, e);
      if (this.size++ >= this.threshold) {
         this.resize(2 * this.table.length);
      }

   }

   private void resize(int newCapacity) {
      QNameMap.Entry[] oldTable = this.table;
      int oldCapacity = oldTable.length;
      if (oldCapacity == 1073741824) {
         this.threshold = Integer.MAX_VALUE;
      } else {
         QNameMap.Entry[] newTable = new QNameMap.Entry[newCapacity];
         this.transfer(newTable);
         this.table = newTable;
         this.threshold = newCapacity;
      }
   }

   private void transfer(QNameMap.Entry<V>[] newTable) {
      QNameMap.Entry<V>[] src = this.table;
      int newCapacity = newTable.length;

      for(int j = 0; j < src.length; ++j) {
         QNameMap.Entry<V> e = src[j];
         if (e != null) {
            src[j] = null;

            QNameMap.Entry next;
            do {
               next = e.next;
               int i = indexFor(e.hash, newCapacity);
               e.next = newTable[i];
               newTable[i] = e;
               e = next;
            } while(next != null);
         }
      }

   }

   public QNameMap.Entry<V> getOne() {
      QNameMap.Entry[] var1 = this.table;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         QNameMap.Entry<V> e = var1[var3];
         if (e != null) {
            return e;
         }
      }

      return null;
   }

   public Collection<QName> keySet() {
      Set<QName> r = new HashSet();
      Iterator var2 = this.entrySet().iterator();

      while(var2.hasNext()) {
         QNameMap.Entry<V> e = (QNameMap.Entry)var2.next();
         r.add(e.createQName());
      }

      return r;
   }

   public boolean containsKey(String nsUri, String localName) {
      return this.getEntry(nsUri, localName) != null;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public Set<QNameMap.Entry<V>> entrySet() {
      Set<QNameMap.Entry<V>> es = this.entrySet;
      return es != null ? es : (this.entrySet = new QNameMap.EntrySet());
   }

   private Iterator<QNameMap.Entry<V>> newEntryIterator() {
      return new QNameMap.EntryIterator();
   }

   private QNameMap.Entry<V> getEntry(String nsUri, String localName) {
      assert nsUri == nsUri.intern();

      assert localName == localName.intern();

      int hash = hash(localName);
      int i = indexFor(hash, this.table.length);

      QNameMap.Entry e;
      for(e = this.table[i]; e != null && (localName != e.localName || nsUri != e.nsUri); e = e.next) {
      }

      return e;
   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append('{');
      Iterator var2 = this.entrySet().iterator();

      while(var2.hasNext()) {
         QNameMap.Entry<V> e = (QNameMap.Entry)var2.next();
         if (buf.length() > 1) {
            buf.append(',');
         }

         buf.append('[');
         buf.append((Object)e);
         buf.append(']');
      }

      buf.append('}');
      return buf.toString();
   }

   private class EntrySet extends AbstractSet<QNameMap.Entry<V>> {
      private EntrySet() {
      }

      public Iterator<QNameMap.Entry<V>> iterator() {
         return QNameMap.this.newEntryIterator();
      }

      public boolean contains(Object o) {
         if (!(o instanceof QNameMap.Entry)) {
            return false;
         } else {
            QNameMap.Entry<V> e = (QNameMap.Entry)o;
            QNameMap.Entry<V> candidate = QNameMap.this.getEntry(e.nsUri, e.localName);
            return candidate != null && candidate.equals(e);
         }
      }

      public boolean remove(Object o) {
         throw new UnsupportedOperationException();
      }

      public int size() {
         return QNameMap.this.size;
      }

      // $FF: synthetic method
      EntrySet(Object x1) {
         this();
      }
   }

   private class EntryIterator extends QNameMap<V>.HashIterator<QNameMap.Entry<V>> {
      private EntryIterator() {
         super();
      }

      public QNameMap.Entry<V> next() {
         return this.nextEntry();
      }

      // $FF: synthetic method
      EntryIterator(Object x1) {
         this();
      }
   }

   public static final class Entry<V> {
      public final String nsUri;
      public final String localName;
      V value;
      final int hash;
      QNameMap.Entry<V> next;

      Entry(int h, String nsUri, String localName, V v, QNameMap.Entry<V> n) {
         this.value = v;
         this.next = n;
         this.nsUri = nsUri;
         this.localName = localName;
         this.hash = h;
      }

      public QName createQName() {
         return new QName(this.nsUri, this.localName);
      }

      public V getValue() {
         return this.value;
      }

      public V setValue(V newValue) {
         V oldValue = this.value;
         this.value = newValue;
         return oldValue;
      }

      public boolean equals(Object o) {
         if (!(o instanceof QNameMap.Entry)) {
            return false;
         } else {
            QNameMap.Entry e = (QNameMap.Entry)o;
            String k1 = this.nsUri;
            String k2 = e.nsUri;
            String k3 = this.localName;
            String k4 = e.localName;
            if (k1 == k2 || k1 != null && k1.equals(k2) && (k3 == k4 || k3 != null && k3.equals(k4))) {
               Object v1 = this.getValue();
               Object v2 = e.getValue();
               if (v1 == v2 || v1 != null && v1.equals(v2)) {
                  return true;
               }
            }

            return false;
         }
      }

      public int hashCode() {
         return this.localName.hashCode() ^ (this.value == null ? 0 : this.value.hashCode());
      }

      public String toString() {
         return '"' + this.nsUri + "\",\"" + this.localName + "\"=" + this.getValue();
      }
   }

   private abstract class HashIterator<E> implements Iterator<E> {
      QNameMap.Entry<V> next;
      int index;

      HashIterator() {
         QNameMap.Entry<V>[] t = QNameMap.this.table;
         int i = t.length;
         QNameMap.Entry<V> n = null;
         if (QNameMap.this.size != 0) {
            while(i > 0) {
               --i;
               if ((n = t[i]) != null) {
                  break;
               }
            }
         }

         this.next = n;
         this.index = i;
      }

      public boolean hasNext() {
         return this.next != null;
      }

      QNameMap.Entry<V> nextEntry() {
         QNameMap.Entry<V> e = this.next;
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            QNameMap.Entry<V> n = e.next;
            QNameMap.Entry<V>[] t = QNameMap.this.table;

            int i;
            for(i = this.index; n == null && i > 0; n = t[i]) {
               --i;
            }

            this.index = i;
            this.next = n;
            return e;
         }
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }
   }
}
