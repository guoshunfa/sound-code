package sun.util;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public abstract class PreHashedMap<V> extends AbstractMap<String, V> {
   private final int rows;
   private final int size;
   private final int shift;
   private final int mask;
   private final Object[] ht;

   protected PreHashedMap(int var1, int var2, int var3, int var4) {
      this.rows = var1;
      this.size = var2;
      this.shift = var3;
      this.mask = var4;
      this.ht = new Object[var1];
      this.init(this.ht);
   }

   protected abstract void init(Object[] var1);

   private V toV(Object var1) {
      return var1;
   }

   public V get(Object var1) {
      int var2 = var1.hashCode() >> this.shift & this.mask;
      Object[] var3 = (Object[])((Object[])this.ht[var2]);
      if (var3 == null) {
         return null;
      } else {
         while(!var3[0].equals(var1)) {
            if (var3.length < 3) {
               return null;
            }

            var3 = (Object[])((Object[])var3[2]);
         }

         return this.toV(var3[1]);
      }
   }

   public V put(String var1, V var2) {
      int var3 = var1.hashCode() >> this.shift & this.mask;
      Object[] var4 = (Object[])((Object[])this.ht[var3]);
      if (var4 == null) {
         throw new UnsupportedOperationException(var1);
      } else {
         while(!var4[0].equals(var1)) {
            if (var4.length < 3) {
               throw new UnsupportedOperationException(var1);
            }

            var4 = (Object[])((Object[])var4[2]);
         }

         Object var5 = this.toV(var4[1]);
         var4[1] = var2;
         return var5;
      }
   }

   public Set<String> keySet() {
      return new AbstractSet<String>() {
         public int size() {
            return PreHashedMap.this.size;
         }

         public Iterator<String> iterator() {
            return new Iterator<String>() {
               private int i = -1;
               Object[] a = null;
               String cur = null;

               private boolean findNext() {
                  if (this.a != null) {
                     if (this.a.length == 3) {
                        this.a = (Object[])((Object[])this.a[2]);
                        this.cur = (String)this.a[0];
                        return true;
                     }

                     ++this.i;
                     this.a = null;
                  }

                  this.cur = null;
                  if (this.i >= PreHashedMap.this.rows) {
                     return false;
                  } else {
                     if (this.i < 0 || PreHashedMap.this.ht[this.i] == null) {
                        do {
                           if (++this.i >= PreHashedMap.this.rows) {
                              return false;
                           }
                        } while(PreHashedMap.this.ht[this.i] == null);
                     }

                     this.a = (Object[])((Object[])PreHashedMap.this.ht[this.i]);
                     this.cur = (String)this.a[0];
                     return true;
                  }
               }

               public boolean hasNext() {
                  return this.cur != null ? true : this.findNext();
               }

               public String next() {
                  if (this.cur == null && !this.findNext()) {
                     throw new NoSuchElementException();
                  } else {
                     String var1 = this.cur;
                     this.cur = null;
                     return var1;
                  }
               }

               public void remove() {
                  throw new UnsupportedOperationException();
               }
            };
         }
      };
   }

   public Set<Map.Entry<String, V>> entrySet() {
      return new AbstractSet<Map.Entry<String, V>>() {
         public int size() {
            return PreHashedMap.this.size;
         }

         public Iterator<Map.Entry<String, V>> iterator() {
            return new Iterator<Map.Entry<String, V>>() {
               final Iterator<String> i = PreHashedMap.this.keySet().iterator();

               public boolean hasNext() {
                  return this.i.hasNext();
               }

               public Map.Entry<String, V> next() {
                  return new Map.Entry<String, V>() {
                     String k;

                     {
                        this.k = (String)i.next();
                     }

                     public String getKey() {
                        return this.k;
                     }

                     public V getValue() {
                        return PreHashedMap.this.get(this.k);
                     }

                     public int hashCode() {
                        Object var1 = PreHashedMap.this.get(this.k);
                        return this.k.hashCode() + (var1 == null ? 0 : var1.hashCode());
                     }

                     public boolean equals(Object var1) {
                        if (var1 == this) {
                           return true;
                        } else if (!(var1 instanceof Map.Entry)) {
                           return false;
                        } else {
                           boolean var10000;
                           label43: {
                              label29: {
                                 Map.Entry var2 = (Map.Entry)var1;
                                 if (this.getKey() == null) {
                                    if (var2.getKey() != null) {
                                       break label29;
                                    }
                                 } else if (!this.getKey().equals(var2.getKey())) {
                                    break label29;
                                 }

                                 if (this.getValue() == null) {
                                    if (var2.getValue() == null) {
                                       break label43;
                                    }
                                 } else if (this.getValue().equals(var2.getValue())) {
                                    break label43;
                                 }
                              }

                              var10000 = false;
                              return var10000;
                           }

                           var10000 = true;
                           return var10000;
                        }
                     }

                     public V setValue(V var1) {
                        throw new UnsupportedOperationException();
                     }
                  };
               }

               public void remove() {
                  throw new UnsupportedOperationException();
               }
            };
         }
      };
   }
}
