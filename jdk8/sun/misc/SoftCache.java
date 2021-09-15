package sun.misc;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class SoftCache extends AbstractMap implements Map {
   private Map hash;
   private ReferenceQueue queue = new ReferenceQueue();
   private Set entrySet = null;

   private void processQueue() {
      SoftCache.ValueCell var1;
      while((var1 = (SoftCache.ValueCell)this.queue.poll()) != null) {
         if (var1.isValid()) {
            this.hash.remove(var1.key);
         } else {
            SoftCache.ValueCell.dropped--;
         }
      }

   }

   public SoftCache(int var1, float var2) {
      this.hash = new HashMap(var1, var2);
   }

   public SoftCache(int var1) {
      this.hash = new HashMap(var1);
   }

   public SoftCache() {
      this.hash = new HashMap();
   }

   public int size() {
      return this.entrySet().size();
   }

   public boolean isEmpty() {
      return this.entrySet().isEmpty();
   }

   public boolean containsKey(Object var1) {
      return SoftCache.ValueCell.strip(this.hash.get(var1), false) != null;
   }

   protected Object fill(Object var1) {
      return null;
   }

   public Object get(Object var1) {
      this.processQueue();
      Object var2 = this.hash.get(var1);
      if (var2 == null) {
         var2 = this.fill(var1);
         if (var2 != null) {
            this.hash.put(var1, SoftCache.ValueCell.create(var1, var2, this.queue));
            return var2;
         }
      }

      return SoftCache.ValueCell.strip(var2, false);
   }

   public Object put(Object var1, Object var2) {
      this.processQueue();
      SoftCache.ValueCell var3 = SoftCache.ValueCell.create(var1, var2, this.queue);
      return SoftCache.ValueCell.strip(this.hash.put(var1, var3), true);
   }

   public Object remove(Object var1) {
      this.processQueue();
      return SoftCache.ValueCell.strip(this.hash.remove(var1), true);
   }

   public void clear() {
      this.processQueue();
      this.hash.clear();
   }

   private static boolean valEquals(Object var0, Object var1) {
      return var0 == null ? var1 == null : var0.equals(var1);
   }

   public Set entrySet() {
      if (this.entrySet == null) {
         this.entrySet = new SoftCache.EntrySet();
      }

      return this.entrySet;
   }

   private class EntrySet extends AbstractSet {
      Set hashEntries;

      private EntrySet() {
         this.hashEntries = SoftCache.this.hash.entrySet();
      }

      public Iterator iterator() {
         return new Iterator() {
            Iterator hashIterator;
            SoftCache.Entry next;

            {
               this.hashIterator = EntrySet.this.hashEntries.iterator();
               this.next = null;
            }

            public boolean hasNext() {
               while(true) {
                  if (this.hashIterator.hasNext()) {
                     Map.Entry var1 = (Map.Entry)this.hashIterator.next();
                     SoftCache.ValueCell var2 = (SoftCache.ValueCell)var1.getValue();
                     Object var3 = null;
                     if (var2 != null && (var3 = var2.get()) == null) {
                        continue;
                     }

                     this.next = SoftCache.this.new Entry(var1, var3);
                     return true;
                  }

                  return false;
               }
            }

            public Object next() {
               if (this.next == null && !this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  SoftCache.Entry var1 = this.next;
                  this.next = null;
                  return var1;
               }
            }

            public void remove() {
               this.hashIterator.remove();
            }
         };
      }

      public boolean isEmpty() {
         return !this.iterator().hasNext();
      }

      public int size() {
         int var1 = 0;
         Iterator var2 = this.iterator();

         while(var2.hasNext()) {
            ++var1;
            var2.next();
         }

         return var1;
      }

      public boolean remove(Object var1) {
         SoftCache.this.processQueue();
         return var1 instanceof SoftCache.Entry ? this.hashEntries.remove(((SoftCache.Entry)var1).ent) : false;
      }

      // $FF: synthetic method
      EntrySet(Object var2) {
         this();
      }
   }

   private class Entry implements Map.Entry {
      private Map.Entry ent;
      private Object value;

      Entry(Map.Entry var2, Object var3) {
         this.ent = var2;
         this.value = var3;
      }

      public Object getKey() {
         return this.ent.getKey();
      }

      public Object getValue() {
         return this.value;
      }

      public Object setValue(Object var1) {
         return this.ent.setValue(SoftCache.ValueCell.create(this.ent.getKey(), var1, SoftCache.this.queue));
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof Map.Entry)) {
            return false;
         } else {
            Map.Entry var2 = (Map.Entry)var1;
            return SoftCache.valEquals(this.ent.getKey(), var2.getKey()) && SoftCache.valEquals(this.value, var2.getValue());
         }
      }

      public int hashCode() {
         Object var1;
         return ((var1 = this.getKey()) == null ? 0 : var1.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
      }
   }

   private static class ValueCell extends SoftReference {
      private static Object INVALID_KEY = new Object();
      private static int dropped = 0;
      private Object key;

      private ValueCell(Object var1, Object var2, ReferenceQueue var3) {
         super(var2, var3);
         this.key = var1;
      }

      private static SoftCache.ValueCell create(Object var0, Object var1, ReferenceQueue var2) {
         return var1 == null ? null : new SoftCache.ValueCell(var0, var1, var2);
      }

      private static Object strip(Object var0, boolean var1) {
         if (var0 == null) {
            return null;
         } else {
            SoftCache.ValueCell var2 = (SoftCache.ValueCell)var0;
            Object var3 = var2.get();
            if (var1) {
               var2.drop();
            }

            return var3;
         }
      }

      private boolean isValid() {
         return this.key != INVALID_KEY;
      }

      private void drop() {
         super.clear();
         this.key = INVALID_KEY;
         ++dropped;
      }
   }
}
