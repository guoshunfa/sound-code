package sun.util.resources;

import java.util.AbstractSet;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicMarkableReference;

public abstract class ParallelListResourceBundle extends ResourceBundle {
   private volatile ConcurrentMap<String, Object> lookup;
   private volatile Set<String> keyset;
   private final AtomicMarkableReference<Object[][]> parallelContents = new AtomicMarkableReference((Object)null, false);

   protected ParallelListResourceBundle() {
   }

   protected abstract Object[][] getContents();

   ResourceBundle getParent() {
      return this.parent;
   }

   public void setParallelContents(OpenListResourceBundle var1) {
      if (var1 == null) {
         this.parallelContents.compareAndSet((Object)null, (Object)null, false, true);
      } else {
         this.parallelContents.compareAndSet((Object)null, var1.getContents(), false, false);
      }

   }

   boolean areParallelContentsComplete() {
      if (this.parallelContents.isMarked()) {
         return true;
      } else {
         boolean[] var1 = new boolean[1];
         Object[][] var2 = (Object[][])this.parallelContents.get(var1);
         return var2 != null || var1[0];
      }
   }

   protected Object handleGetObject(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.loadLookupTablesIfNecessary();
         return this.lookup.get(var1);
      }
   }

   public Enumeration<String> getKeys() {
      return Collections.enumeration(this.keySet());
   }

   public boolean containsKey(String var1) {
      return this.keySet().contains(var1);
   }

   protected Set<String> handleKeySet() {
      this.loadLookupTablesIfNecessary();
      return this.lookup.keySet();
   }

   public Set<String> keySet() {
      Set var1;
      while((var1 = this.keyset) == null) {
         ParallelListResourceBundle.KeySet var5 = new ParallelListResourceBundle.KeySet(this.handleKeySet(), this.parent);
         synchronized(this) {
            if (this.keyset == null) {
               this.keyset = var5;
            }
         }
      }

      return var1;
   }

   synchronized void resetKeySet() {
      this.keyset = null;
   }

   void loadLookupTablesIfNecessary() {
      Object var1 = this.lookup;
      Object[][] var2;
      int var4;
      if (var1 == null) {
         var1 = new ConcurrentHashMap();
         var2 = this.getContents();
         int var3 = var2.length;

         for(var4 = 0; var4 < var3; ++var4) {
            Object[] var5 = var2[var4];
            ((ConcurrentMap)var1).put((String)var5[0], var5[1]);
         }
      }

      var2 = (Object[][])this.parallelContents.getReference();
      if (var2 != null) {
         Object[][] var9 = var2;
         var4 = var2.length;

         for(int var10 = 0; var10 < var4; ++var10) {
            Object[] var6 = var9[var10];
            ((ConcurrentMap)var1).putIfAbsent((String)var6[0], var6[1]);
         }

         this.parallelContents.set((Object)null, true);
      }

      if (this.lookup == null) {
         synchronized(this) {
            if (this.lookup == null) {
               this.lookup = (ConcurrentMap)var1;
            }
         }
      }

   }

   private static class KeySet extends AbstractSet<String> {
      private final Set<String> set;
      private final ResourceBundle parent;

      private KeySet(Set<String> var1, ResourceBundle var2) {
         this.set = var1;
         this.parent = var2;
      }

      public boolean contains(Object var1) {
         if (this.set.contains(var1)) {
            return true;
         } else {
            return this.parent != null ? this.parent.containsKey((String)var1) : false;
         }
      }

      public Iterator<String> iterator() {
         return this.parent == null ? this.set.iterator() : new Iterator<String>() {
            private Iterator<String> itr;
            private boolean usingParent;

            {
               this.itr = KeySet.this.set.iterator();
            }

            public boolean hasNext() {
               if (this.itr.hasNext()) {
                  return true;
               } else {
                  if (!this.usingParent) {
                     HashSet var1 = new HashSet(KeySet.this.parent.keySet());
                     var1.removeAll(KeySet.this.set);
                     this.itr = var1.iterator();
                     this.usingParent = true;
                  }

                  return this.itr.hasNext();
               }
            }

            public String next() {
               if (this.hasNext()) {
                  return (String)this.itr.next();
               } else {
                  throw new NoSuchElementException();
               }
            }

            public void remove() {
               throw new UnsupportedOperationException();
            }
         };
      }

      public int size() {
         if (this.parent == null) {
            return this.set.size();
         } else {
            HashSet var1 = new HashSet(this.set);
            var1.addAll(this.parent.keySet());
            return var1.size();
         }
      }

      // $FF: synthetic method
      KeySet(Set var1, ResourceBundle var2, Object var3) {
         this(var1, var2);
      }
   }
}
