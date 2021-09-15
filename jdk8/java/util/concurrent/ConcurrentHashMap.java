package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.LongBinaryOperator;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongBiFunction;
import java.util.function.ToLongFunction;
import sun.misc.Contended;
import sun.misc.Unsafe;

public class ConcurrentHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable {
   private static final long serialVersionUID = 7249069246763182397L;
   private static final int MAXIMUM_CAPACITY = 1073741824;
   private static final int DEFAULT_CAPACITY = 16;
   static final int MAX_ARRAY_SIZE = 2147483639;
   private static final int DEFAULT_CONCURRENCY_LEVEL = 16;
   private static final float LOAD_FACTOR = 0.75F;
   static final int TREEIFY_THRESHOLD = 8;
   static final int UNTREEIFY_THRESHOLD = 6;
   static final int MIN_TREEIFY_CAPACITY = 64;
   private static final int MIN_TRANSFER_STRIDE = 16;
   private static int RESIZE_STAMP_BITS = 16;
   private static final int MAX_RESIZERS;
   private static final int RESIZE_STAMP_SHIFT;
   static final int MOVED = -1;
   static final int TREEBIN = -2;
   static final int RESERVED = -3;
   static final int HASH_BITS = Integer.MAX_VALUE;
   static final int NCPU;
   private static final ObjectStreamField[] serialPersistentFields;
   transient volatile ConcurrentHashMap.Node<K, V>[] table;
   private transient volatile ConcurrentHashMap.Node<K, V>[] nextTable;
   private transient volatile long baseCount;
   private transient volatile int sizeCtl;
   private transient volatile int transferIndex;
   private transient volatile int cellsBusy;
   private transient volatile ConcurrentHashMap.CounterCell[] counterCells;
   private transient ConcurrentHashMap.KeySetView<K, V> keySet;
   private transient ConcurrentHashMap.ValuesView<K, V> values;
   private transient ConcurrentHashMap.EntrySetView<K, V> entrySet;
   private static final Unsafe U;
   private static final long SIZECTL;
   private static final long TRANSFERINDEX;
   private static final long BASECOUNT;
   private static final long CELLSBUSY;
   private static final long CELLVALUE;
   private static final long ABASE;
   private static final int ASHIFT;

   static final int spread(int var0) {
      return (var0 ^ var0 >>> 16) & Integer.MAX_VALUE;
   }

   private static final int tableSizeFor(int var0) {
      int var1 = var0 - 1;
      var1 |= var1 >>> 1;
      var1 |= var1 >>> 2;
      var1 |= var1 >>> 4;
      var1 |= var1 >>> 8;
      var1 |= var1 >>> 16;
      return var1 < 0 ? 1 : (var1 >= 1073741824 ? 1073741824 : var1 + 1);
   }

   static Class<?> comparableClassFor(Object var0) {
      if (var0 instanceof Comparable) {
         Class var1;
         if ((var1 = var0.getClass()) == String.class) {
            return var1;
         }

         Type[] var2;
         if ((var2 = var1.getGenericInterfaces()) != null) {
            for(int var6 = 0; var6 < var2.length; ++var6) {
               Type[] var3;
               Type var4;
               ParameterizedType var5;
               if ((var4 = var2[var6]) instanceof ParameterizedType && (var5 = (ParameterizedType)var4).getRawType() == Comparable.class && (var3 = var5.getActualTypeArguments()) != null && var3.length == 1 && var3[0] == var1) {
                  return var1;
               }
            }
         }
      }

      return null;
   }

   static int compareComparables(Class<?> var0, Object var1, Object var2) {
      return var2 != null && var2.getClass() == var0 ? ((Comparable)var1).compareTo(var2) : 0;
   }

   static final <K, V> ConcurrentHashMap.Node<K, V> tabAt(ConcurrentHashMap.Node<K, V>[] var0, int var1) {
      return (ConcurrentHashMap.Node)U.getObjectVolatile(var0, ((long)var1 << ASHIFT) + ABASE);
   }

   static final <K, V> boolean casTabAt(ConcurrentHashMap.Node<K, V>[] var0, int var1, ConcurrentHashMap.Node<K, V> var2, ConcurrentHashMap.Node<K, V> var3) {
      return U.compareAndSwapObject(var0, ((long)var1 << ASHIFT) + ABASE, var2, var3);
   }

   static final <K, V> void setTabAt(ConcurrentHashMap.Node<K, V>[] var0, int var1, ConcurrentHashMap.Node<K, V> var2) {
      U.putObjectVolatile(var0, ((long)var1 << ASHIFT) + ABASE, var2);
   }

   public ConcurrentHashMap() {
   }

   public ConcurrentHashMap(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException();
      } else {
         int var2 = var1 >= 536870912 ? 1073741824 : tableSizeFor(var1 + (var1 >>> 1) + 1);
         this.sizeCtl = var2;
      }
   }

   public ConcurrentHashMap(Map<? extends K, ? extends V> var1) {
      this.sizeCtl = 16;
      this.putAll(var1);
   }

   public ConcurrentHashMap(int var1, float var2) {
      this(var1, var2, 1);
   }

   public ConcurrentHashMap(int var1, float var2, int var3) {
      if (var2 > 0.0F && var1 >= 0 && var3 > 0) {
         if (var1 < var3) {
            var1 = var3;
         }

         long var4 = (long)(1.0D + (double)((float)((long)var1) / var2));
         int var6 = var4 >= 1073741824L ? 1073741824 : tableSizeFor((int)var4);
         this.sizeCtl = var6;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int size() {
      long var1 = this.sumCount();
      return var1 < 0L ? 0 : (var1 > 2147483647L ? Integer.MAX_VALUE : (int)var1);
   }

   public boolean isEmpty() {
      return this.sumCount() <= 0L;
   }

   public V get(Object var1) {
      int var8 = spread(var1.hashCode());
      ConcurrentHashMap.Node[] var2;
      ConcurrentHashMap.Node var3;
      int var5;
      if ((var2 = this.table) != null && (var5 = var2.length) > 0 && (var3 = tabAt(var2, var5 - 1 & var8)) != null) {
         int var6;
         Object var7;
         if ((var6 = var3.hash) == var8) {
            if ((var7 = var3.key) == var1 || var7 != null && var1.equals(var7)) {
               return var3.val;
            }
         } else if (var6 < 0) {
            ConcurrentHashMap.Node var4;
            return (var4 = var3.find(var8, var1)) != null ? var4.val : null;
         }

         while((var3 = var3.next) != null) {
            if (var3.hash == var8 && ((var7 = var3.key) == var1 || var7 != null && var1.equals(var7))) {
               return var3.val;
            }
         }
      }

      return null;
   }

   public boolean containsKey(Object var1) {
      return this.get(var1) != null;
   }

   public boolean containsValue(Object var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         ConcurrentHashMap.Node[] var2;
         if ((var2 = this.table) != null) {
            ConcurrentHashMap.Traverser var3 = new ConcurrentHashMap.Traverser(var2, var2.length, 0, var2.length);

            ConcurrentHashMap.Node var4;
            while((var4 = var3.advance()) != null) {
               Object var5;
               if ((var5 = var4.val) == var1 || var5 != null && var1.equals(var5)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public V put(K var1, V var2) {
      return this.putVal(var1, var2, false);
   }

   final V putVal(K var1, V var2, boolean var3) {
      if (var1 != null && var2 != null) {
         int var4 = spread(var1.hashCode());
         int var5 = 0;
         ConcurrentHashMap.Node[] var6 = this.table;

         while(true) {
            int var8;
            while(var6 == null || (var8 = var6.length) == 0) {
               var6 = this.initTable();
            }

            ConcurrentHashMap.Node var7;
            int var9;
            if ((var7 = tabAt(var6, var9 = var8 - 1 & var4)) == null) {
               if (casTabAt(var6, var9, (ConcurrentHashMap.Node)null, new ConcurrentHashMap.Node(var4, var1, var2, (ConcurrentHashMap.Node)null))) {
                  break;
               }
            } else {
               int var10;
               if ((var10 = var7.hash) == -1) {
                  var6 = this.helpTransfer(var6, var7);
               } else {
                  Object var11 = null;
                  synchronized(var7) {
                     if (tabAt(var6, var9) == var7) {
                        if (var10 < 0) {
                           if (var7 instanceof ConcurrentHashMap.TreeBin) {
                              var5 = 2;
                              ConcurrentHashMap.TreeNode var18;
                              if ((var18 = ((ConcurrentHashMap.TreeBin)var7).putTreeVal(var4, var1, var2)) != null) {
                                 var11 = var18.val;
                                 if (!var3) {
                                    var18.val = var2;
                                 }
                              }
                           }
                        } else {
                           label103: {
                              var5 = 1;

                              ConcurrentHashMap.Node var13;
                              Object var14;
                              for(var13 = var7; var13.hash != var4 || (var14 = var13.key) != var1 && (var14 == null || !var1.equals(var14)); ++var5) {
                                 ConcurrentHashMap.Node var15 = var13;
                                 if ((var13 = var13.next) == null) {
                                    var15.next = new ConcurrentHashMap.Node(var4, var1, var2, (ConcurrentHashMap.Node)null);
                                    break label103;
                                 }
                              }

                              var11 = var13.val;
                              if (!var3) {
                                 var13.val = var2;
                              }
                           }
                        }
                     }
                  }

                  if (var5 != 0) {
                     if (var5 >= 8) {
                        this.treeifyBin(var6, var9);
                     }

                     if (var11 != null) {
                        return var11;
                     }
                     break;
                  }
               }
            }
         }

         this.addCount(1L, var5);
         return null;
      } else {
         throw new NullPointerException();
      }
   }

   public void putAll(Map<? extends K, ? extends V> var1) {
      this.tryPresize(var1.size());
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         this.putVal(var3.getKey(), var3.getValue(), false);
      }

   }

   public V remove(Object var1) {
      return this.replaceNode(var1, (Object)null, (Object)null);
   }

   final V replaceNode(Object var1, V var2, Object var3) {
      int var4 = spread(var1.hashCode());
      ConcurrentHashMap.Node[] var5 = this.table;

      ConcurrentHashMap.Node var6;
      int var7;
      int var8;
      while(var5 != null && (var7 = var5.length) != 0 && (var6 = tabAt(var5, var8 = var7 - 1 & var4)) != null) {
         int var9;
         if ((var9 = var6.hash) == -1) {
            var5 = this.helpTransfer(var5, var6);
         } else {
            Object var10 = null;
            boolean var11 = false;
            synchronized(var6) {
               if (tabAt(var5, var8) == var6) {
                  Object var16;
                  if (var9 < 0) {
                     if (var6 instanceof ConcurrentHashMap.TreeBin) {
                        var11 = true;
                        ConcurrentHashMap.TreeBin var19 = (ConcurrentHashMap.TreeBin)var6;
                        ConcurrentHashMap.TreeNode var20;
                        ConcurrentHashMap.TreeNode var21;
                        if ((var20 = var19.root) != null && (var21 = var20.findTreeNode(var4, var1, (Class)null)) != null) {
                           var16 = var21.val;
                           if (var3 == null || var3 == var16 || var16 != null && var3.equals(var16)) {
                              var10 = var16;
                              if (var2 != null) {
                                 var21.val = var2;
                              } else if (var19.removeTreeNode(var21)) {
                                 setTabAt(var5, var8, untreeify(var19.first));
                              }
                           }
                        }
                     }
                  } else {
                     label121: {
                        var11 = true;
                        ConcurrentHashMap.Node var13 = var6;
                        ConcurrentHashMap.Node var14 = null;

                        Object var15;
                        while(var13.hash != var4 || (var15 = var13.key) != var1 && (var15 == null || !var1.equals(var15))) {
                           var14 = var13;
                           if ((var13 = var13.next) == null) {
                              break label121;
                           }
                        }

                        var16 = var13.val;
                        if (var3 == null || var3 == var16 || var16 != null && var3.equals(var16)) {
                           var10 = var16;
                           if (var2 != null) {
                              var13.val = var2;
                           } else if (var14 != null) {
                              var14.next = var13.next;
                           } else {
                              setTabAt(var5, var8, var13.next);
                           }
                        }
                     }
                  }
               }
            }

            if (var11) {
               if (var10 != null) {
                  if (var2 == null) {
                     this.addCount(-1L, -1);
                  }

                  return var10;
               }
               break;
            }
         }
      }

      return null;
   }

   public void clear() {
      long var1 = 0L;
      int var3 = 0;
      ConcurrentHashMap.Node[] var4 = this.table;

      while(var4 != null && var3 < var4.length) {
         ConcurrentHashMap.Node var6 = tabAt(var4, var3);
         if (var6 == null) {
            ++var3;
         } else {
            int var5;
            if ((var5 = var6.hash) == -1) {
               var4 = this.helpTransfer(var4, var6);
               var3 = 0;
            } else {
               synchronized(var6) {
                  if (tabAt(var4, var3) == var6) {
                     for(Object var8 = var5 >= 0 ? var6 : (var6 instanceof ConcurrentHashMap.TreeBin ? ((ConcurrentHashMap.TreeBin)var6).first : null); var8 != null; var8 = ((ConcurrentHashMap.Node)var8).next) {
                        --var1;
                     }

                     setTabAt(var4, var3++, (ConcurrentHashMap.Node)null);
                  }
               }
            }
         }
      }

      if (var1 != 0L) {
         this.addCount(var1, -1);
      }

   }

   public ConcurrentHashMap.KeySetView<K, V> keySet() {
      ConcurrentHashMap.KeySetView var1;
      return (var1 = this.keySet) != null ? var1 : (this.keySet = new ConcurrentHashMap.KeySetView(this, (Object)null));
   }

   public Collection<V> values() {
      ConcurrentHashMap.ValuesView var1;
      return (var1 = this.values) != null ? var1 : (this.values = new ConcurrentHashMap.ValuesView(this));
   }

   public Set<Map.Entry<K, V>> entrySet() {
      ConcurrentHashMap.EntrySetView var1;
      return (var1 = this.entrySet) != null ? var1 : (this.entrySet = new ConcurrentHashMap.EntrySetView(this));
   }

   public int hashCode() {
      int var1 = 0;
      ConcurrentHashMap.Node[] var2;
      ConcurrentHashMap.Node var4;
      if ((var2 = this.table) != null) {
         for(ConcurrentHashMap.Traverser var3 = new ConcurrentHashMap.Traverser(var2, var2.length, 0, var2.length); (var4 = var3.advance()) != null; var1 += var4.key.hashCode() ^ var4.val.hashCode()) {
         }
      }

      return var1;
   }

   public String toString() {
      ConcurrentHashMap.Node[] var1;
      int var2 = (var1 = this.table) == null ? 0 : var1.length;
      ConcurrentHashMap.Traverser var3 = new ConcurrentHashMap.Traverser(var1, var2, 0, var2);
      StringBuilder var4 = new StringBuilder();
      var4.append('{');
      ConcurrentHashMap.Node var5;
      if ((var5 = var3.advance()) != null) {
         while(true) {
            Object var6 = var5.key;
            Object var7 = var5.val;
            var4.append(var6 == this ? "(this Map)" : var6);
            var4.append('=');
            var4.append(var7 == this ? "(this Map)" : var7);
            if ((var5 = var3.advance()) == null) {
               break;
            }

            var4.append(',').append(' ');
         }
      }

      return var4.append('}').toString();
   }

   public boolean equals(Object var1) {
      if (var1 != this) {
         if (!(var1 instanceof Map)) {
            return false;
         } else {
            Map var2 = (Map)var1;
            ConcurrentHashMap.Node[] var3;
            int var4 = (var3 = this.table) == null ? 0 : var3.length;
            ConcurrentHashMap.Traverser var5 = new ConcurrentHashMap.Traverser(var3, var4, 0, var4);

            Object var7;
            Object var8;
            do {
               ConcurrentHashMap.Node var6;
               if ((var6 = var5.advance()) == null) {
                  Iterator var11 = var2.entrySet().iterator();

                  Object var9;
                  Object var10;
                  Map.Entry var12;
                  do {
                     if (!var11.hasNext()) {
                        return true;
                     }

                     var12 = (Map.Entry)var11.next();
                  } while((var8 = var12.getKey()) != null && (var9 = var12.getValue()) != null && (var10 = this.get(var8)) != null && (var9 == var10 || var9.equals(var10)));

                  return false;
               }

               var7 = var6.val;
               var8 = var2.get(var6.key);
            } while(var8 != null && (var8 == var7 || var8.equals(var7)));

            return false;
         }
      } else {
         return true;
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      int var2 = 0;

      int var3;
      for(var3 = 1; var3 < 16; var3 <<= 1) {
         ++var2;
      }

      int var4 = 32 - var2;
      int var5 = var3 - 1;
      ConcurrentHashMap.Segment[] var6 = (ConcurrentHashMap.Segment[])(new ConcurrentHashMap.Segment[16]);

      for(int var7 = 0; var7 < var6.length; ++var7) {
         var6[var7] = new ConcurrentHashMap.Segment(0.75F);
      }

      var1.putFields().put("segments", var6);
      var1.putFields().put("segmentShift", var4);
      var1.putFields().put("segmentMask", var5);
      var1.writeFields();
      ConcurrentHashMap.Node[] var10;
      if ((var10 = this.table) != null) {
         ConcurrentHashMap.Traverser var8 = new ConcurrentHashMap.Traverser(var10, var10.length, 0, var10.length);

         ConcurrentHashMap.Node var9;
         while((var9 = var8.advance()) != null) {
            var1.writeObject(var9.key);
            var1.writeObject(var9.val);
         }
      }

      var1.writeObject((Object)null);
      var1.writeObject((Object)null);
      var6 = null;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      this.sizeCtl = -1;
      var1.defaultReadObject();
      long var2 = 0L;
      ConcurrentHashMap.Node var4 = null;

      while(true) {
         Object var5 = var1.readObject();
         Object var6 = var1.readObject();
         if (var5 == null || var6 == null) {
            if (var2 == 0L) {
               this.sizeCtl = 0;
            } else {
               int var22;
               if (var2 >= 536870912L) {
                  var22 = 1073741824;
               } else {
                  int var23 = (int)var2;
                  var22 = tableSizeFor(var23 + (var23 >>> 1) + 1);
               }

               ConcurrentHashMap.Node[] var24 = (ConcurrentHashMap.Node[])(new ConcurrentHashMap.Node[var22]);
               int var7 = var22 - 1;

               long var8;
               ConcurrentHashMap.Node var11;
               for(var8 = 0L; var4 != null; var4 = var11) {
                  var11 = var4.next;
                  int var13 = var4.hash;
                  int var14 = var13 & var7;
                  boolean var10;
                  ConcurrentHashMap.Node var12;
                  if ((var12 = tabAt(var24, var14)) == null) {
                     var10 = true;
                  } else {
                     Object var15 = var4.key;
                     if (var12.hash < 0) {
                        ConcurrentHashMap.TreeBin var25 = (ConcurrentHashMap.TreeBin)var12;
                        if (var25.putTreeVal(var13, var15, var4.val) == null) {
                           ++var8;
                        }

                        var10 = false;
                     } else {
                        int var16 = 0;
                        var10 = true;

                        ConcurrentHashMap.Node var17;
                        for(var17 = var12; var17 != null; var17 = var17.next) {
                           Object var18;
                           if (var17.hash == var13 && ((var18 = var17.key) == var15 || var18 != null && var15.equals(var18))) {
                              var10 = false;
                              break;
                           }

                           ++var16;
                        }

                        if (var10 && var16 >= 8) {
                           var10 = false;
                           ++var8;
                           var4.next = var12;
                           ConcurrentHashMap.TreeNode var19 = null;
                           ConcurrentHashMap.TreeNode var20 = null;

                           for(var17 = var4; var17 != null; var17 = var17.next) {
                              ConcurrentHashMap.TreeNode var21 = new ConcurrentHashMap.TreeNode(var17.hash, var17.key, var17.val, (ConcurrentHashMap.Node)null, (ConcurrentHashMap.TreeNode)null);
                              if ((var21.prev = var20) == null) {
                                 var19 = var21;
                              } else {
                                 var20.next = var21;
                              }

                              var20 = var21;
                           }

                           setTabAt(var24, var14, new ConcurrentHashMap.TreeBin(var19));
                        }
                     }
                  }

                  if (var10) {
                     ++var8;
                     var4.next = var12;
                     setTabAt(var24, var14, var4);
                  }
               }

               this.table = var24;
               this.sizeCtl = var22 - (var22 >>> 2);
               this.baseCount = var8;
            }

            return;
         }

         var4 = new ConcurrentHashMap.Node(spread(var5.hashCode()), var5, var6, var4);
         ++var2;
      }
   }

   public V putIfAbsent(K var1, V var2) {
      return this.putVal(var1, var2, true);
   }

   public boolean remove(Object var1, Object var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return var2 != null && this.replaceNode(var1, (Object)null, var2) != null;
      }
   }

   public boolean replace(K var1, V var2, V var3) {
      if (var1 != null && var2 != null && var3 != null) {
         return this.replaceNode(var1, var3, var2) != null;
      } else {
         throw new NullPointerException();
      }
   }

   public V replace(K var1, V var2) {
      if (var1 != null && var2 != null) {
         return this.replaceNode(var1, var2, (Object)null);
      } else {
         throw new NullPointerException();
      }
   }

   public V getOrDefault(Object var1, V var2) {
      Object var3;
      return (var3 = this.get(var1)) == null ? var2 : var3;
   }

   public void forEach(BiConsumer<? super K, ? super V> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         ConcurrentHashMap.Node[] var2;
         if ((var2 = this.table) != null) {
            ConcurrentHashMap.Traverser var3 = new ConcurrentHashMap.Traverser(var2, var2.length, 0, var2.length);

            ConcurrentHashMap.Node var4;
            while((var4 = var3.advance()) != null) {
               var1.accept(var4.key, var4.val);
            }
         }

      }
   }

   public void replaceAll(BiFunction<? super K, ? super V, ? extends V> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         ConcurrentHashMap.Node[] var2;
         if ((var2 = this.table) != null) {
            ConcurrentHashMap.Traverser var3 = new ConcurrentHashMap.Traverser(var2, var2.length, 0, var2.length);

            ConcurrentHashMap.Node var4;
            while((var4 = var3.advance()) != null) {
               Object var5 = var4.val;
               Object var6 = var4.key;

               while(true) {
                  Object var7 = var1.apply(var6, var5);
                  if (var7 == null) {
                     throw new NullPointerException();
                  }

                  if (this.replaceNode(var6, var7, var5) != null || (var5 = this.get(var6)) == null) {
                     break;
                  }
               }
            }
         }

      }
   }

   public V computeIfAbsent(K var1, Function<? super K, ? extends V> var2) {
      if (var1 != null && var2 != null) {
         int var3 = spread(var1.hashCode());
         Object var4 = null;
         int var5 = 0;
         ConcurrentHashMap.Node[] var6 = this.table;

         while(true) {
            int var8;
            while(var6 == null || (var8 = var6.length) == 0) {
               var6 = this.initTable();
            }

            ConcurrentHashMap.Node var7;
            int var9;
            ConcurrentHashMap.Node var13;
            if ((var7 = tabAt(var6, var9 = var8 - 1 & var3)) == null) {
               ConcurrentHashMap.ReservationNode var24 = new ConcurrentHashMap.ReservationNode();
               synchronized(var24) {
                  if (casTabAt(var6, var9, (ConcurrentHashMap.Node)null, var24)) {
                     var5 = 1;
                     var13 = null;

                     try {
                        if ((var4 = var2.apply(var1)) != null) {
                           var13 = new ConcurrentHashMap.Node(var3, var1, var4, (ConcurrentHashMap.Node)null);
                        }
                     } finally {
                        setTabAt(var6, var9, var13);
                     }
                  }
               }

               if (var5 != 0) {
                  break;
               }
            } else {
               int var10;
               if ((var10 = var7.hash) == -1) {
                  var6 = this.helpTransfer(var6, var7);
               } else {
                  boolean var11 = false;
                  synchronized(var7) {
                     if (tabAt(var6, var9) == var7) {
                        if (var10 < 0) {
                           if (var7 instanceof ConcurrentHashMap.TreeBin) {
                              var5 = 2;
                              ConcurrentHashMap.TreeBin var25 = (ConcurrentHashMap.TreeBin)var7;
                              ConcurrentHashMap.TreeNode var15;
                              ConcurrentHashMap.TreeNode var26;
                              if ((var26 = var25.root) != null && (var15 = var26.findTreeNode(var3, var1, (Class)null)) != null) {
                                 var4 = var15.val;
                              } else if ((var4 = var2.apply(var1)) != null) {
                                 var11 = true;
                                 var25.putTreeVal(var3, var1, var4);
                              }
                           }
                        } else {
                           label268: {
                              var5 = 1;

                              Object var14;
                              for(var13 = var7; var13.hash != var3 || (var14 = var13.key) != var1 && (var14 == null || !var1.equals(var14)); ++var5) {
                                 ConcurrentHashMap.Node var16 = var13;
                                 if ((var13 = var13.next) == null) {
                                    if ((var4 = var2.apply(var1)) != null) {
                                       var11 = true;
                                       var16.next = new ConcurrentHashMap.Node(var3, var1, var4, (ConcurrentHashMap.Node)null);
                                    }
                                    break label268;
                                 }
                              }

                              var4 = var13.val;
                           }
                        }
                     }
                  }

                  if (var5 != 0) {
                     if (var5 >= 8) {
                        this.treeifyBin(var6, var9);
                     }

                     if (!var11) {
                        return var4;
                     }
                     break;
                  }
               }
            }
         }

         if (var4 != null) {
            this.addCount(1L, var5);
         }

         return var4;
      } else {
         throw new NullPointerException();
      }
   }

   public V computeIfPresent(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
      if (var1 != null && var2 != null) {
         int var3 = spread(var1.hashCode());
         Object var4 = null;
         byte var5 = 0;
         int var6 = 0;
         ConcurrentHashMap.Node[] var7 = this.table;

         while(true) {
            int var9;
            while(var7 == null || (var9 = var7.length) == 0) {
               var7 = this.initTable();
            }

            ConcurrentHashMap.Node var8;
            int var10;
            if ((var8 = tabAt(var7, var10 = var9 - 1 & var3)) == null) {
               break;
            }

            int var11;
            if ((var11 = var8.hash) == -1) {
               var7 = this.helpTransfer(var7, var8);
            } else {
               synchronized(var8) {
                  if (tabAt(var7, var10) == var8) {
                     if (var11 < 0) {
                        if (var8 instanceof ConcurrentHashMap.TreeBin) {
                           var6 = 2;
                           ConcurrentHashMap.TreeBin var19 = (ConcurrentHashMap.TreeBin)var8;
                           ConcurrentHashMap.TreeNode var20;
                           ConcurrentHashMap.TreeNode var21;
                           if ((var20 = var19.root) != null && (var21 = var20.findTreeNode(var3, var1, (Class)null)) != null) {
                              var4 = var2.apply(var1, var21.val);
                              if (var4 != null) {
                                 var21.val = var4;
                              } else {
                                 var5 = -1;
                                 if (var19.removeTreeNode(var21)) {
                                    setTabAt(var7, var10, untreeify(var19.first));
                                 }
                              }
                           }
                        }
                     } else {
                        label107: {
                           var6 = 1;
                           ConcurrentHashMap.Node var13 = var8;

                           ConcurrentHashMap.Node var14;
                           Object var15;
                           for(var14 = null; var13.hash != var3 || (var15 = var13.key) != var1 && (var15 == null || !var1.equals(var15)); ++var6) {
                              var14 = var13;
                              if ((var13 = var13.next) == null) {
                                 break label107;
                              }
                           }

                           var4 = var2.apply(var1, var13.val);
                           if (var4 != null) {
                              var13.val = var4;
                           } else {
                              var5 = -1;
                              ConcurrentHashMap.Node var16 = var13.next;
                              if (var14 != null) {
                                 var14.next = var16;
                              } else {
                                 setTabAt(var7, var10, var16);
                              }
                           }
                        }
                     }
                  }
               }

               if (var6 != 0) {
                  break;
               }
            }
         }

         if (var5 != 0) {
            this.addCount((long)var5, var6);
         }

         return var4;
      } else {
         throw new NullPointerException();
      }
   }

   public V compute(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
      if (var1 != null && var2 != null) {
         int var3 = spread(var1.hashCode());
         Object var4 = null;
         byte var5 = 0;
         int var6 = 0;
         ConcurrentHashMap.Node[] var7 = this.table;

         while(true) {
            int var9;
            while(var7 == null || (var9 = var7.length) == 0) {
               var7 = this.initTable();
            }

            ConcurrentHashMap.Node var8;
            int var10;
            ConcurrentHashMap.Node var14;
            if ((var8 = tabAt(var7, var10 = var9 - 1 & var3)) == null) {
               ConcurrentHashMap.ReservationNode var12 = new ConcurrentHashMap.ReservationNode();
               synchronized(var12) {
                  if (casTabAt(var7, var10, (ConcurrentHashMap.Node)null, var12)) {
                     var6 = 1;
                     var14 = null;

                     try {
                        if ((var4 = var2.apply(var1, (Object)null)) != null) {
                           var5 = 1;
                           var14 = new ConcurrentHashMap.Node(var3, var1, var4, (ConcurrentHashMap.Node)null);
                        }
                     } finally {
                        setTabAt(var7, var10, var14);
                     }
                  }
               }

               if (var6 != 0) {
                  break;
               }
            } else {
               int var11;
               if ((var11 = var8.hash) == -1) {
                  var7 = this.helpTransfer(var7, var8);
               } else {
                  synchronized(var8) {
                     if (tabAt(var7, var10) == var8) {
                        if (var11 < 0) {
                           if (var8 instanceof ConcurrentHashMap.TreeBin) {
                              var6 = 1;
                              ConcurrentHashMap.TreeBin var24 = (ConcurrentHashMap.TreeBin)var8;
                              ConcurrentHashMap.TreeNode var25;
                              ConcurrentHashMap.TreeNode var26;
                              if ((var25 = var24.root) != null) {
                                 var26 = var25.findTreeNode(var3, var1, (Class)null);
                              } else {
                                 var26 = null;
                              }

                              Object var27 = var26 == null ? null : var26.val;
                              var4 = var2.apply(var1, var27);
                              if (var4 != null) {
                                 if (var26 != null) {
                                    var26.val = var4;
                                 } else {
                                    var5 = 1;
                                    var24.putTreeVal(var3, var1, var4);
                                 }
                              } else if (var26 != null) {
                                 var5 = -1;
                                 if (var24.removeTreeNode(var26)) {
                                    setTabAt(var7, var10, untreeify(var24.first));
                                 }
                              }
                           }
                        } else {
                           label295: {
                              var6 = 1;
                              ConcurrentHashMap.Node var13 = var8;

                              Object var15;
                              for(var14 = null; var13.hash != var3 || (var15 = var13.key) != var1 && (var15 == null || !var1.equals(var15)); ++var6) {
                                 var14 = var13;
                                 if ((var13 = var13.next) == null) {
                                    var4 = var2.apply(var1, (Object)null);
                                    if (var4 != null) {
                                       var5 = 1;
                                       var14.next = new ConcurrentHashMap.Node(var3, var1, var4, (ConcurrentHashMap.Node)null);
                                    }
                                    break label295;
                                 }
                              }

                              var4 = var2.apply(var1, var13.val);
                              if (var4 != null) {
                                 var13.val = var4;
                              } else {
                                 var5 = -1;
                                 ConcurrentHashMap.Node var16 = var13.next;
                                 if (var14 != null) {
                                    var14.next = var16;
                                 } else {
                                    setTabAt(var7, var10, var16);
                                 }
                              }
                           }
                        }
                     }
                  }

                  if (var6 != 0) {
                     if (var6 >= 8) {
                        this.treeifyBin(var7, var10);
                     }
                     break;
                  }
               }
            }
         }

         if (var5 != 0) {
            this.addCount((long)var5, var6);
         }

         return var4;
      } else {
         throw new NullPointerException();
      }
   }

   public V merge(K var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
      if (var1 != null && var2 != null && var3 != null) {
         int var4 = spread(var1.hashCode());
         Object var5 = null;
         byte var6 = 0;
         int var7 = 0;
         ConcurrentHashMap.Node[] var8 = this.table;

         while(true) {
            int var10;
            while(var8 == null || (var10 = var8.length) == 0) {
               var8 = this.initTable();
            }

            ConcurrentHashMap.Node var9;
            int var11;
            if ((var9 = tabAt(var8, var11 = var10 - 1 & var4)) == null) {
               if (casTabAt(var8, var11, (ConcurrentHashMap.Node)null, new ConcurrentHashMap.Node(var4, var1, var2, (ConcurrentHashMap.Node)null))) {
                  var6 = 1;
                  var5 = var2;
                  break;
               }
            } else {
               int var12;
               if ((var12 = var9.hash) == -1) {
                  var8 = this.helpTransfer(var8, var9);
               } else {
                  synchronized(var9) {
                     if (tabAt(var8, var11) == var9) {
                        if (var12 < 0) {
                           if (var9 instanceof ConcurrentHashMap.TreeBin) {
                              var7 = 2;
                              ConcurrentHashMap.TreeBin var20 = (ConcurrentHashMap.TreeBin)var9;
                              ConcurrentHashMap.TreeNode var21 = var20.root;
                              ConcurrentHashMap.TreeNode var22 = var21 == null ? null : var21.findTreeNode(var4, var1, (Class)null);
                              var5 = var22 == null ? var2 : var3.apply(var22.val, var2);
                              if (var5 != null) {
                                 if (var22 != null) {
                                    var22.val = var5;
                                 } else {
                                    var6 = 1;
                                    var20.putTreeVal(var4, var1, var5);
                                 }
                              } else if (var22 != null) {
                                 var6 = -1;
                                 if (var20.removeTreeNode(var22)) {
                                    setTabAt(var8, var11, untreeify(var20.first));
                                 }
                              }
                           }
                        } else {
                           label128: {
                              var7 = 1;
                              ConcurrentHashMap.Node var14 = var9;

                              ConcurrentHashMap.Node var15;
                              Object var16;
                              for(var15 = null; var14.hash != var4 || (var16 = var14.key) != var1 && (var16 == null || !var1.equals(var16)); ++var7) {
                                 var15 = var14;
                                 if ((var14 = var14.next) == null) {
                                    var6 = 1;
                                    var5 = var2;
                                    var15.next = new ConcurrentHashMap.Node(var4, var1, var2, (ConcurrentHashMap.Node)null);
                                    break label128;
                                 }
                              }

                              var5 = var3.apply(var14.val, var2);
                              if (var5 != null) {
                                 var14.val = var5;
                              } else {
                                 var6 = -1;
                                 ConcurrentHashMap.Node var17 = var14.next;
                                 if (var15 != null) {
                                    var15.next = var17;
                                 } else {
                                    setTabAt(var8, var11, var17);
                                 }
                              }
                           }
                        }
                     }
                  }

                  if (var7 != 0) {
                     if (var7 >= 8) {
                        this.treeifyBin(var8, var11);
                     }
                     break;
                  }
               }
            }
         }

         if (var6 != 0) {
            this.addCount((long)var6, var7);
         }

         return var5;
      } else {
         throw new NullPointerException();
      }
   }

   public boolean contains(Object var1) {
      return this.containsValue(var1);
   }

   public Enumeration<K> keys() {
      ConcurrentHashMap.Node[] var1;
      int var2 = (var1 = this.table) == null ? 0 : var1.length;
      return new ConcurrentHashMap.KeyIterator(var1, var2, 0, var2, this);
   }

   public Enumeration<V> elements() {
      ConcurrentHashMap.Node[] var1;
      int var2 = (var1 = this.table) == null ? 0 : var1.length;
      return new ConcurrentHashMap.ValueIterator(var1, var2, 0, var2, this);
   }

   public long mappingCount() {
      long var1 = this.sumCount();
      return var1 < 0L ? 0L : var1;
   }

   public static <K> ConcurrentHashMap.KeySetView<K, Boolean> newKeySet() {
      return new ConcurrentHashMap.KeySetView(new ConcurrentHashMap(), Boolean.TRUE);
   }

   public static <K> ConcurrentHashMap.KeySetView<K, Boolean> newKeySet(int var0) {
      return new ConcurrentHashMap.KeySetView(new ConcurrentHashMap(var0), Boolean.TRUE);
   }

   public ConcurrentHashMap.KeySetView<K, V> keySet(V var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return new ConcurrentHashMap.KeySetView(this, var1);
      }
   }

   static final int resizeStamp(int var0) {
      return Integer.numberOfLeadingZeros(var0) | 1 << RESIZE_STAMP_BITS - 1;
   }

   private final ConcurrentHashMap.Node<K, V>[] initTable() {
      ConcurrentHashMap.Node[] var1;
      while((var1 = this.table) == null || var1.length == 0) {
         int var2;
         if ((var2 = this.sizeCtl) < 0) {
            Thread.yield();
         } else if (U.compareAndSwapInt(this, SIZECTL, var2, -1)) {
            try {
               if ((var1 = this.table) == null || var1.length == 0) {
                  int var3 = var2 > 0 ? var2 : 16;
                  ConcurrentHashMap.Node[] var4 = (ConcurrentHashMap.Node[])(new ConcurrentHashMap.Node[var3]);
                  var1 = var4;
                  this.table = var4;
                  var2 = var3 - (var3 >>> 2);
               }
               break;
            } finally {
               this.sizeCtl = var2;
            }
         }
      }

      return var1;
   }

   private final void addCount(long var1, int var3) {
      ConcurrentHashMap.CounterCell[] var4;
      long var5;
      long var7;
      int var12;
      if ((var4 = this.counterCells) != null || !U.compareAndSwapLong(this, BASECOUNT, var5 = this.baseCount, var7 = var5 + var1)) {
         boolean var13 = true;
         ConcurrentHashMap.CounterCell var9;
         long var10;
         if (var4 == null || (var12 = var4.length - 1) < 0 || (var9 = var4[ThreadLocalRandom.getProbe() & var12]) == null || !(var13 = U.compareAndSwapLong(var9, CELLVALUE, var10 = var9.value, var10 + var1))) {
            this.fullAddCount(var1, var13);
            return;
         }

         if (var3 <= 1) {
            return;
         }

         var7 = this.sumCount();
      }

      int var11;
      ConcurrentHashMap.Node[] var14;
      if (var3 >= 0) {
         for(; var7 >= (long)(var12 = this.sizeCtl) && (var14 = this.table) != null && (var11 = var14.length) < 1073741824; var7 = this.sumCount()) {
            int var16 = resizeStamp(var11);
            if (var12 < 0) {
               ConcurrentHashMap.Node[] var15;
               if (var12 >>> RESIZE_STAMP_SHIFT != var16 || var12 == var16 + 1 || var12 == var16 + MAX_RESIZERS || (var15 = this.nextTable) == null || this.transferIndex <= 0) {
                  break;
               }

               if (U.compareAndSwapInt(this, SIZECTL, var12, var12 + 1)) {
                  this.transfer(var14, var15);
               }
            } else if (U.compareAndSwapInt(this, SIZECTL, var12, (var16 << RESIZE_STAMP_SHIFT) + 2)) {
               this.transfer(var14, (ConcurrentHashMap.Node[])null);
            }
         }
      }

   }

   final ConcurrentHashMap.Node<K, V>[] helpTransfer(ConcurrentHashMap.Node<K, V>[] var1, ConcurrentHashMap.Node<K, V> var2) {
      ConcurrentHashMap.Node[] var3;
      if (var1 != null && var2 instanceof ConcurrentHashMap.ForwardingNode && (var3 = ((ConcurrentHashMap.ForwardingNode)var2).nextTable) != null) {
         int var5 = resizeStamp(var1.length);

         int var4;
         while(var3 == this.nextTable && this.table == var1 && (var4 = this.sizeCtl) < 0 && var4 >>> RESIZE_STAMP_SHIFT == var5 && var4 != var5 + 1 && var4 != var5 + MAX_RESIZERS && this.transferIndex > 0) {
            if (U.compareAndSwapInt(this, SIZECTL, var4, var4 + 1)) {
               this.transfer(var1, var3);
               break;
            }
         }

         return var3;
      } else {
         return this.table;
      }
   }

   private final void tryPresize(int var1) {
      int var2 = var1 >= 536870912 ? 1073741824 : tableSizeFor(var1 + (var1 >>> 1) + 1);

      int var3;
      while((var3 = this.sizeCtl) >= 0) {
         ConcurrentHashMap.Node[] var4 = this.table;
         int var5;
         if (var4 != null && (var5 = var4.length) != 0) {
            if (var2 <= var3 || var5 >= 1073741824) {
               break;
            }

            if (var4 == this.table) {
               int var10 = resizeStamp(var5);
               if (var3 < 0) {
                  ConcurrentHashMap.Node[] var7;
                  if (var3 >>> RESIZE_STAMP_SHIFT != var10 || var3 == var10 + 1 || var3 == var10 + MAX_RESIZERS || (var7 = this.nextTable) == null || this.transferIndex <= 0) {
                     break;
                  }

                  if (U.compareAndSwapInt(this, SIZECTL, var3, var3 + 1)) {
                     this.transfer(var4, var7);
                  }
               } else if (U.compareAndSwapInt(this, SIZECTL, var3, (var10 << RESIZE_STAMP_SHIFT) + 2)) {
                  this.transfer(var4, (ConcurrentHashMap.Node[])null);
               }
            }
         } else {
            var5 = var3 > var2 ? var3 : var2;
            if (U.compareAndSwapInt(this, SIZECTL, var3, -1)) {
               try {
                  if (this.table == var4) {
                     ConcurrentHashMap.Node[] var6 = (ConcurrentHashMap.Node[])(new ConcurrentHashMap.Node[var5]);
                     this.table = var6;
                     var3 = var5 - (var5 >>> 2);
                  }
               } finally {
                  this.sizeCtl = var3;
               }
            }
         }
      }

   }

   private final void transfer(ConcurrentHashMap.Node<K, V>[] var1, ConcurrentHashMap.Node<K, V>[] var2) {
      int var3 = var1.length;
      int var4;
      if ((var4 = NCPU > 1 ? (var3 >>> 3) / NCPU : var3) < 16) {
         var4 = 16;
      }

      if (var2 == null) {
         try {
            ConcurrentHashMap.Node[] var5 = (ConcurrentHashMap.Node[])(new ConcurrentHashMap.Node[var3 << 1]);
            var2 = var5;
         } catch (Throwable var27) {
            this.sizeCtl = Integer.MAX_VALUE;
            return;
         }

         this.nextTable = var2;
         this.transferIndex = var3;
      }

      int var29 = var2.length;
      ConcurrentHashMap.ForwardingNode var6 = new ConcurrentHashMap.ForwardingNode(var2);
      boolean var7 = true;
      boolean var8 = false;
      int var9 = 0;
      int var10 = 0;

      while(true) {
         while(true) {
            int var13;
            while(!var7) {
               if (var9 >= 0 && var9 < var3 && var9 + var3 < var29) {
                  ConcurrentHashMap.Node var11;
                  if ((var11 = tabAt(var1, var9)) == null) {
                     var7 = casTabAt(var1, var9, (ConcurrentHashMap.Node)null, var6);
                  } else {
                     int var12;
                     if ((var12 = var11.hash) == -1) {
                        var7 = true;
                     } else {
                        synchronized(var11) {
                           if (tabAt(var1, var9) == var11) {
                              if (var12 >= 0) {
                                 int var16 = var12 & var3;
                                 ConcurrentHashMap.Node var17 = var11;

                                 ConcurrentHashMap.Node var18;
                                 int var19;
                                 for(var18 = var11.next; var18 != null; var18 = var18.next) {
                                    var19 = var18.hash & var3;
                                    if (var19 != var16) {
                                       var16 = var19;
                                       var17 = var18;
                                    }
                                 }

                                 ConcurrentHashMap.Node var14;
                                 ConcurrentHashMap.Node var15;
                                 if (var16 == 0) {
                                    var14 = var17;
                                    var15 = null;
                                 } else {
                                    var15 = var17;
                                    var14 = null;
                                 }

                                 for(var18 = var11; var18 != var17; var18 = var18.next) {
                                    var19 = var18.hash;
                                    Object var20 = var18.key;
                                    Object var21 = var18.val;
                                    if ((var19 & var3) == 0) {
                                       var14 = new ConcurrentHashMap.Node(var19, var20, var21, var14);
                                    } else {
                                       var15 = new ConcurrentHashMap.Node(var19, var20, var21, var15);
                                    }
                                 }

                                 setTabAt(var2, var9, var14);
                                 setTabAt(var2, var9 + var3, var15);
                                 setTabAt(var1, var9, var6);
                                 var7 = true;
                              } else if (var11 instanceof ConcurrentHashMap.TreeBin) {
                                 ConcurrentHashMap.TreeBin var32 = (ConcurrentHashMap.TreeBin)var11;
                                 ConcurrentHashMap.TreeNode var34 = null;
                                 ConcurrentHashMap.TreeNode var35 = null;
                                 ConcurrentHashMap.TreeNode var36 = null;
                                 ConcurrentHashMap.TreeNode var37 = null;
                                 int var38 = 0;
                                 int var22 = 0;

                                 for(Object var23 = var32.first; var23 != null; var23 = ((ConcurrentHashMap.Node)var23).next) {
                                    int var24 = ((ConcurrentHashMap.Node)var23).hash;
                                    ConcurrentHashMap.TreeNode var25 = new ConcurrentHashMap.TreeNode(var24, ((ConcurrentHashMap.Node)var23).key, ((ConcurrentHashMap.Node)var23).val, (ConcurrentHashMap.Node)null, (ConcurrentHashMap.TreeNode)null);
                                    if ((var24 & var3) == 0) {
                                       if ((var25.prev = var35) == null) {
                                          var34 = var25;
                                       } else {
                                          var35.next = var25;
                                       }

                                       var35 = var25;
                                       ++var38;
                                    } else {
                                       if ((var25.prev = var37) == null) {
                                          var36 = var25;
                                       } else {
                                          var37.next = var25;
                                       }

                                       var37 = var25;
                                       ++var22;
                                    }
                                 }

                                 Object var30 = var38 <= 6 ? untreeify(var34) : (var22 != 0 ? new ConcurrentHashMap.TreeBin(var34) : var32);
                                 Object var33 = var22 <= 6 ? untreeify(var36) : (var38 != 0 ? new ConcurrentHashMap.TreeBin(var36) : var32);
                                 setTabAt(var2, var9, (ConcurrentHashMap.Node)var30);
                                 setTabAt(var2, var9 + var3, (ConcurrentHashMap.Node)var33);
                                 setTabAt(var1, var9, var6);
                                 var7 = true;
                              }
                           }
                        }
                     }
                  }
               } else {
                  if (var8) {
                     this.nextTable = null;
                     this.table = var2;
                     this.sizeCtl = (var3 << 1) - (var3 >>> 1);
                     return;
                  }

                  if (U.compareAndSwapInt(this, SIZECTL, var13 = this.sizeCtl, var13 - 1)) {
                     if (var13 - 2 != resizeStamp(var3) << RESIZE_STAMP_SHIFT) {
                        return;
                     }

                     var7 = true;
                     var8 = true;
                     var9 = var3;
                  }
               }
            }

            --var9;
            if (var9 < var10 && !var8) {
               if ((var13 = this.transferIndex) <= 0) {
                  var9 = -1;
                  var7 = false;
               } else {
                  int var31;
                  if (U.compareAndSwapInt(this, TRANSFERINDEX, var13, var31 = var13 > var4 ? var13 - var4 : 0)) {
                     var10 = var31;
                     var9 = var13 - 1;
                     var7 = false;
                  }
               }
            } else {
               var7 = false;
            }
         }
      }
   }

   final long sumCount() {
      ConcurrentHashMap.CounterCell[] var1 = this.counterCells;
      long var3 = this.baseCount;
      if (var1 != null) {
         for(int var5 = 0; var5 < var1.length; ++var5) {
            ConcurrentHashMap.CounterCell var2;
            if ((var2 = var1[var5]) != null) {
               var3 += var2.value;
            }
         }
      }

      return var3;
   }

   private final void fullAddCount(long var1, boolean var3) {
      int var4;
      if ((var4 = ThreadLocalRandom.getProbe()) == 0) {
         ThreadLocalRandom.localInit();
         var4 = ThreadLocalRandom.getProbe();
         var3 = true;
      }

      boolean var5 = false;

      while(true) {
         ConcurrentHashMap.CounterCell[] var6;
         int var8;
         long var9;
         if ((var6 = this.counterCells) != null && (var8 = var6.length) > 0) {
            ConcurrentHashMap.CounterCell var7;
            if ((var7 = var6[var8 - 1 & var4]) == null) {
               if (this.cellsBusy == 0) {
                  ConcurrentHashMap.CounterCell var31 = new ConcurrentHashMap.CounterCell(var1);
                  if (this.cellsBusy == 0 && U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                     boolean var32 = false;

                     try {
                        ConcurrentHashMap.CounterCell[] var13;
                        int var14;
                        int var15;
                        if ((var13 = this.counterCells) != null && (var14 = var13.length) > 0 && var13[var15 = var14 - 1 & var4] == null) {
                           var13[var15] = var31;
                           var32 = true;
                        }
                     } finally {
                        this.cellsBusy = 0;
                     }

                     if (var32) {
                        break;
                     }
                     continue;
                  }
               }

               var5 = false;
            } else if (!var3) {
               var3 = true;
            } else {
               if (U.compareAndSwapLong(var7, CELLVALUE, var9 = var7.value, var9 + var1)) {
                  break;
               }

               if (this.counterCells == var6 && var8 < NCPU) {
                  if (!var5) {
                     var5 = true;
                  } else if (this.cellsBusy == 0 && U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                     try {
                        if (this.counterCells == var6) {
                           ConcurrentHashMap.CounterCell[] var33 = new ConcurrentHashMap.CounterCell[var8 << 1];

                           for(int var34 = 0; var34 < var8; ++var34) {
                              var33[var34] = var6[var34];
                           }

                           this.counterCells = var33;
                        }
                     } finally {
                        this.cellsBusy = 0;
                     }

                     var5 = false;
                     continue;
                  }
               } else {
                  var5 = false;
               }
            }

            var4 = ThreadLocalRandom.advanceProbe(var4);
         } else if (this.cellsBusy == 0 && this.counterCells == var6 && U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
            boolean var11 = false;

            try {
               if (this.counterCells == var6) {
                  ConcurrentHashMap.CounterCell[] var12 = new ConcurrentHashMap.CounterCell[2];
                  var12[var4 & 1] = new ConcurrentHashMap.CounterCell(var1);
                  this.counterCells = var12;
                  var11 = true;
               }
            } finally {
               this.cellsBusy = 0;
            }

            if (var11) {
               break;
            }
         } else if (U.compareAndSwapLong(this, BASECOUNT, var9 = this.baseCount, var9 + var1)) {
            break;
         }
      }

   }

   private final void treeifyBin(ConcurrentHashMap.Node<K, V>[] var1, int var2) {
      if (var1 != null) {
         int var4;
         if ((var4 = var1.length) < 64) {
            this.tryPresize(var4 << 1);
         } else {
            ConcurrentHashMap.Node var3;
            if ((var3 = tabAt(var1, var2)) != null && var3.hash >= 0) {
               synchronized(var3) {
                  if (tabAt(var1, var2) == var3) {
                     ConcurrentHashMap.TreeNode var7 = null;
                     ConcurrentHashMap.TreeNode var8 = null;

                     for(ConcurrentHashMap.Node var9 = var3; var9 != null; var9 = var9.next) {
                        ConcurrentHashMap.TreeNode var10 = new ConcurrentHashMap.TreeNode(var9.hash, var9.key, var9.val, (ConcurrentHashMap.Node)null, (ConcurrentHashMap.TreeNode)null);
                        if ((var10.prev = var8) == null) {
                           var7 = var10;
                        } else {
                           var8.next = var10;
                        }

                        var8 = var10;
                     }

                     setTabAt(var1, var2, new ConcurrentHashMap.TreeBin(var7));
                  }
               }
            }
         }
      }

   }

   static <K, V> ConcurrentHashMap.Node<K, V> untreeify(ConcurrentHashMap.Node<K, V> var0) {
      ConcurrentHashMap.Node var1 = null;
      ConcurrentHashMap.Node var2 = null;

      for(ConcurrentHashMap.Node var3 = var0; var3 != null; var3 = var3.next) {
         ConcurrentHashMap.Node var4 = new ConcurrentHashMap.Node(var3.hash, var3.key, var3.val, (ConcurrentHashMap.Node)null);
         if (var2 == null) {
            var1 = var4;
         } else {
            var2.next = var4;
         }

         var2 = var4;
      }

      return var1;
   }

   final int batchFor(long var1) {
      long var3;
      if (var1 != Long.MAX_VALUE && (var3 = this.sumCount()) > 1L && var3 >= var1) {
         int var5 = ForkJoinPool.getCommonPoolParallelism() << 2;
         return var1 > 0L && (var3 /= var1) < (long)var5 ? (int)var3 : var5;
      } else {
         return 0;
      }
   }

   public void forEach(long var1, BiConsumer<? super K, ? super V> var3) {
      if (var3 == null) {
         throw new NullPointerException();
      } else {
         (new ConcurrentHashMap.ForEachMappingTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, var3)).invoke();
      }
   }

   public <U> void forEach(long var1, BiFunction<? super K, ? super V, ? extends U> var3, Consumer<? super U> var4) {
      if (var3 != null && var4 != null) {
         (new ConcurrentHashMap.ForEachTransformedMappingTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, var3, var4)).invoke();
      } else {
         throw new NullPointerException();
      }
   }

   public <U> U search(long var1, BiFunction<? super K, ? super V, ? extends U> var3) {
      if (var3 == null) {
         throw new NullPointerException();
      } else {
         return (new ConcurrentHashMap.SearchMappingsTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, var3, new AtomicReference())).invoke();
      }
   }

   public <U> U reduce(long var1, BiFunction<? super K, ? super V, ? extends U> var3, BiFunction<? super U, ? super U, ? extends U> var4) {
      if (var3 != null && var4 != null) {
         return (new ConcurrentHashMap.MapReduceMappingsTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, (ConcurrentHashMap.MapReduceMappingsTask)null, var3, var4)).invoke();
      } else {
         throw new NullPointerException();
      }
   }

   public double reduceToDouble(long var1, ToDoubleBiFunction<? super K, ? super V> var3, double var4, DoubleBinaryOperator var6) {
      if (var3 != null && var6 != null) {
         return (Double)(new ConcurrentHashMap.MapReduceMappingsToDoubleTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, (ConcurrentHashMap.MapReduceMappingsToDoubleTask)null, var3, var4, var6)).invoke();
      } else {
         throw new NullPointerException();
      }
   }

   public long reduceToLong(long var1, ToLongBiFunction<? super K, ? super V> var3, long var4, LongBinaryOperator var6) {
      if (var3 != null && var6 != null) {
         return (Long)(new ConcurrentHashMap.MapReduceMappingsToLongTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, (ConcurrentHashMap.MapReduceMappingsToLongTask)null, var3, var4, var6)).invoke();
      } else {
         throw new NullPointerException();
      }
   }

   public int reduceToInt(long var1, ToIntBiFunction<? super K, ? super V> var3, int var4, IntBinaryOperator var5) {
      if (var3 != null && var5 != null) {
         return (Integer)(new ConcurrentHashMap.MapReduceMappingsToIntTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, (ConcurrentHashMap.MapReduceMappingsToIntTask)null, var3, var4, var5)).invoke();
      } else {
         throw new NullPointerException();
      }
   }

   public void forEachKey(long var1, Consumer<? super K> var3) {
      if (var3 == null) {
         throw new NullPointerException();
      } else {
         (new ConcurrentHashMap.ForEachKeyTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, var3)).invoke();
      }
   }

   public <U> void forEachKey(long var1, Function<? super K, ? extends U> var3, Consumer<? super U> var4) {
      if (var3 != null && var4 != null) {
         (new ConcurrentHashMap.ForEachTransformedKeyTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, var3, var4)).invoke();
      } else {
         throw new NullPointerException();
      }
   }

   public <U> U searchKeys(long var1, Function<? super K, ? extends U> var3) {
      if (var3 == null) {
         throw new NullPointerException();
      } else {
         return (new ConcurrentHashMap.SearchKeysTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, var3, new AtomicReference())).invoke();
      }
   }

   public K reduceKeys(long var1, BiFunction<? super K, ? super K, ? extends K> var3) {
      if (var3 == null) {
         throw new NullPointerException();
      } else {
         return (new ConcurrentHashMap.ReduceKeysTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, (ConcurrentHashMap.ReduceKeysTask)null, var3)).invoke();
      }
   }

   public <U> U reduceKeys(long var1, Function<? super K, ? extends U> var3, BiFunction<? super U, ? super U, ? extends U> var4) {
      if (var3 != null && var4 != null) {
         return (new ConcurrentHashMap.MapReduceKeysTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, (ConcurrentHashMap.MapReduceKeysTask)null, var3, var4)).invoke();
      } else {
         throw new NullPointerException();
      }
   }

   public double reduceKeysToDouble(long var1, ToDoubleFunction<? super K> var3, double var4, DoubleBinaryOperator var6) {
      if (var3 != null && var6 != null) {
         return (Double)(new ConcurrentHashMap.MapReduceKeysToDoubleTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, (ConcurrentHashMap.MapReduceKeysToDoubleTask)null, var3, var4, var6)).invoke();
      } else {
         throw new NullPointerException();
      }
   }

   public long reduceKeysToLong(long var1, ToLongFunction<? super K> var3, long var4, LongBinaryOperator var6) {
      if (var3 != null && var6 != null) {
         return (Long)(new ConcurrentHashMap.MapReduceKeysToLongTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, (ConcurrentHashMap.MapReduceKeysToLongTask)null, var3, var4, var6)).invoke();
      } else {
         throw new NullPointerException();
      }
   }

   public int reduceKeysToInt(long var1, ToIntFunction<? super K> var3, int var4, IntBinaryOperator var5) {
      if (var3 != null && var5 != null) {
         return (Integer)(new ConcurrentHashMap.MapReduceKeysToIntTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, (ConcurrentHashMap.MapReduceKeysToIntTask)null, var3, var4, var5)).invoke();
      } else {
         throw new NullPointerException();
      }
   }

   public void forEachValue(long var1, Consumer<? super V> var3) {
      if (var3 == null) {
         throw new NullPointerException();
      } else {
         (new ConcurrentHashMap.ForEachValueTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, var3)).invoke();
      }
   }

   public <U> void forEachValue(long var1, Function<? super V, ? extends U> var3, Consumer<? super U> var4) {
      if (var3 != null && var4 != null) {
         (new ConcurrentHashMap.ForEachTransformedValueTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, var3, var4)).invoke();
      } else {
         throw new NullPointerException();
      }
   }

   public <U> U searchValues(long var1, Function<? super V, ? extends U> var3) {
      if (var3 == null) {
         throw new NullPointerException();
      } else {
         return (new ConcurrentHashMap.SearchValuesTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, var3, new AtomicReference())).invoke();
      }
   }

   public V reduceValues(long var1, BiFunction<? super V, ? super V, ? extends V> var3) {
      if (var3 == null) {
         throw new NullPointerException();
      } else {
         return (new ConcurrentHashMap.ReduceValuesTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, (ConcurrentHashMap.ReduceValuesTask)null, var3)).invoke();
      }
   }

   public <U> U reduceValues(long var1, Function<? super V, ? extends U> var3, BiFunction<? super U, ? super U, ? extends U> var4) {
      if (var3 != null && var4 != null) {
         return (new ConcurrentHashMap.MapReduceValuesTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, (ConcurrentHashMap.MapReduceValuesTask)null, var3, var4)).invoke();
      } else {
         throw new NullPointerException();
      }
   }

   public double reduceValuesToDouble(long var1, ToDoubleFunction<? super V> var3, double var4, DoubleBinaryOperator var6) {
      if (var3 != null && var6 != null) {
         return (Double)(new ConcurrentHashMap.MapReduceValuesToDoubleTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, (ConcurrentHashMap.MapReduceValuesToDoubleTask)null, var3, var4, var6)).invoke();
      } else {
         throw new NullPointerException();
      }
   }

   public long reduceValuesToLong(long var1, ToLongFunction<? super V> var3, long var4, LongBinaryOperator var6) {
      if (var3 != null && var6 != null) {
         return (Long)(new ConcurrentHashMap.MapReduceValuesToLongTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, (ConcurrentHashMap.MapReduceValuesToLongTask)null, var3, var4, var6)).invoke();
      } else {
         throw new NullPointerException();
      }
   }

   public int reduceValuesToInt(long var1, ToIntFunction<? super V> var3, int var4, IntBinaryOperator var5) {
      if (var3 != null && var5 != null) {
         return (Integer)(new ConcurrentHashMap.MapReduceValuesToIntTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, (ConcurrentHashMap.MapReduceValuesToIntTask)null, var3, var4, var5)).invoke();
      } else {
         throw new NullPointerException();
      }
   }

   public void forEachEntry(long var1, Consumer<? super Map.Entry<K, V>> var3) {
      if (var3 == null) {
         throw new NullPointerException();
      } else {
         (new ConcurrentHashMap.ForEachEntryTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, var3)).invoke();
      }
   }

   public <U> void forEachEntry(long var1, Function<Map.Entry<K, V>, ? extends U> var3, Consumer<? super U> var4) {
      if (var3 != null && var4 != null) {
         (new ConcurrentHashMap.ForEachTransformedEntryTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, var3, var4)).invoke();
      } else {
         throw new NullPointerException();
      }
   }

   public <U> U searchEntries(long var1, Function<Map.Entry<K, V>, ? extends U> var3) {
      if (var3 == null) {
         throw new NullPointerException();
      } else {
         return (new ConcurrentHashMap.SearchEntriesTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, var3, new AtomicReference())).invoke();
      }
   }

   public Map.Entry<K, V> reduceEntries(long var1, BiFunction<Map.Entry<K, V>, Map.Entry<K, V>, ? extends Map.Entry<K, V>> var3) {
      if (var3 == null) {
         throw new NullPointerException();
      } else {
         return (Map.Entry)(new ConcurrentHashMap.ReduceEntriesTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, (ConcurrentHashMap.ReduceEntriesTask)null, var3)).invoke();
      }
   }

   public <U> U reduceEntries(long var1, Function<Map.Entry<K, V>, ? extends U> var3, BiFunction<? super U, ? super U, ? extends U> var4) {
      if (var3 != null && var4 != null) {
         return (new ConcurrentHashMap.MapReduceEntriesTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, (ConcurrentHashMap.MapReduceEntriesTask)null, var3, var4)).invoke();
      } else {
         throw new NullPointerException();
      }
   }

   public double reduceEntriesToDouble(long var1, ToDoubleFunction<Map.Entry<K, V>> var3, double var4, DoubleBinaryOperator var6) {
      if (var3 != null && var6 != null) {
         return (Double)(new ConcurrentHashMap.MapReduceEntriesToDoubleTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, (ConcurrentHashMap.MapReduceEntriesToDoubleTask)null, var3, var4, var6)).invoke();
      } else {
         throw new NullPointerException();
      }
   }

   public long reduceEntriesToLong(long var1, ToLongFunction<Map.Entry<K, V>> var3, long var4, LongBinaryOperator var6) {
      if (var3 != null && var6 != null) {
         return (Long)(new ConcurrentHashMap.MapReduceEntriesToLongTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, (ConcurrentHashMap.MapReduceEntriesToLongTask)null, var3, var4, var6)).invoke();
      } else {
         throw new NullPointerException();
      }
   }

   public int reduceEntriesToInt(long var1, ToIntFunction<Map.Entry<K, V>> var3, int var4, IntBinaryOperator var5) {
      if (var3 != null && var5 != null) {
         return (Integer)(new ConcurrentHashMap.MapReduceEntriesToIntTask((ConcurrentHashMap.BulkTask)null, this.batchFor(var1), 0, 0, this.table, (ConcurrentHashMap.MapReduceEntriesToIntTask)null, var3, var4, var5)).invoke();
      } else {
         throw new NullPointerException();
      }
   }

   static {
      MAX_RESIZERS = (1 << 32 - RESIZE_STAMP_BITS) - 1;
      RESIZE_STAMP_SHIFT = 32 - RESIZE_STAMP_BITS;
      NCPU = Runtime.getRuntime().availableProcessors();
      serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("segments", ConcurrentHashMap.Segment[].class), new ObjectStreamField("segmentMask", Integer.TYPE), new ObjectStreamField("segmentShift", Integer.TYPE)};

      try {
         U = Unsafe.getUnsafe();
         Class var0 = ConcurrentHashMap.class;
         SIZECTL = U.objectFieldOffset(var0.getDeclaredField("sizeCtl"));
         TRANSFERINDEX = U.objectFieldOffset(var0.getDeclaredField("transferIndex"));
         BASECOUNT = U.objectFieldOffset(var0.getDeclaredField("baseCount"));
         CELLSBUSY = U.objectFieldOffset(var0.getDeclaredField("cellsBusy"));
         Class var1 = ConcurrentHashMap.CounterCell.class;
         CELLVALUE = U.objectFieldOffset(var1.getDeclaredField("value"));
         Class var2 = ConcurrentHashMap.Node[].class;
         ABASE = (long)U.arrayBaseOffset(var2);
         int var3 = U.arrayIndexScale(var2);
         if ((var3 & var3 - 1) != 0) {
            throw new Error("data type scale not a power of two");
         } else {
            ASHIFT = 31 - Integer.numberOfLeadingZeros(var3);
         }
      } catch (Exception var4) {
         throw new Error(var4);
      }
   }

   static final class MapReduceMappingsToIntTask<K, V> extends ConcurrentHashMap.BulkTask<K, V, Integer> {
      final ToIntBiFunction<? super K, ? super V> transformer;
      final IntBinaryOperator reducer;
      final int basis;
      int result;
      ConcurrentHashMap.MapReduceMappingsToIntTask<K, V> rights;
      ConcurrentHashMap.MapReduceMappingsToIntTask<K, V> nextRight;

      MapReduceMappingsToIntTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, ConcurrentHashMap.MapReduceMappingsToIntTask<K, V> var6, ToIntBiFunction<? super K, ? super V> var7, int var8, IntBinaryOperator var9) {
         super(var1, var2, var3, var4, var5);
         this.nextRight = var6;
         this.transformer = var7;
         this.basis = var8;
         this.reducer = var9;
      }

      public final Integer getRawResult() {
         return this.result;
      }

      public final void compute() {
         ToIntBiFunction var1;
         IntBinaryOperator var2;
         if ((var1 = this.transformer) != null && (var2 = this.reducer) != null) {
            int var3 = this.basis;
            int var4 = this.baseIndex;

            int var5;
            int var6;
            while(this.batch > 0 && (var6 = (var5 = this.baseLimit) + var4 >>> 1) > var4) {
               this.addToPendingCount(1);
               (this.rights = new ConcurrentHashMap.MapReduceMappingsToIntTask(this, this.batch >>>= 1, this.baseLimit = var6, var5, this.tab, this.rights, var1, var3, var2)).fork();
            }

            ConcurrentHashMap.Node var7;
            while((var7 = this.advance()) != null) {
               var3 = var2.applyAsInt(var3, var1.applyAsInt(var7.key, var7.val));
            }

            this.result = var3;

            for(CountedCompleter var8 = this.firstComplete(); var8 != null; var8 = var8.nextComplete()) {
               ConcurrentHashMap.MapReduceMappingsToIntTask var9 = (ConcurrentHashMap.MapReduceMappingsToIntTask)var8;

               for(ConcurrentHashMap.MapReduceMappingsToIntTask var10 = var9.rights; var10 != null; var10 = var9.rights = var10.nextRight) {
                  var9.result = var2.applyAsInt(var9.result, var10.result);
               }
            }
         }

      }
   }

   static final class MapReduceEntriesToIntTask<K, V> extends ConcurrentHashMap.BulkTask<K, V, Integer> {
      final ToIntFunction<Map.Entry<K, V>> transformer;
      final IntBinaryOperator reducer;
      final int basis;
      int result;
      ConcurrentHashMap.MapReduceEntriesToIntTask<K, V> rights;
      ConcurrentHashMap.MapReduceEntriesToIntTask<K, V> nextRight;

      MapReduceEntriesToIntTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, ConcurrentHashMap.MapReduceEntriesToIntTask<K, V> var6, ToIntFunction<Map.Entry<K, V>> var7, int var8, IntBinaryOperator var9) {
         super(var1, var2, var3, var4, var5);
         this.nextRight = var6;
         this.transformer = var7;
         this.basis = var8;
         this.reducer = var9;
      }

      public final Integer getRawResult() {
         return this.result;
      }

      public final void compute() {
         ToIntFunction var1;
         IntBinaryOperator var2;
         if ((var1 = this.transformer) != null && (var2 = this.reducer) != null) {
            int var3 = this.basis;
            int var4 = this.baseIndex;

            int var5;
            int var6;
            while(this.batch > 0 && (var6 = (var5 = this.baseLimit) + var4 >>> 1) > var4) {
               this.addToPendingCount(1);
               (this.rights = new ConcurrentHashMap.MapReduceEntriesToIntTask(this, this.batch >>>= 1, this.baseLimit = var6, var5, this.tab, this.rights, var1, var3, var2)).fork();
            }

            ConcurrentHashMap.Node var7;
            while((var7 = this.advance()) != null) {
               var3 = var2.applyAsInt(var3, var1.applyAsInt(var7));
            }

            this.result = var3;

            for(CountedCompleter var8 = this.firstComplete(); var8 != null; var8 = var8.nextComplete()) {
               ConcurrentHashMap.MapReduceEntriesToIntTask var9 = (ConcurrentHashMap.MapReduceEntriesToIntTask)var8;

               for(ConcurrentHashMap.MapReduceEntriesToIntTask var10 = var9.rights; var10 != null; var10 = var9.rights = var10.nextRight) {
                  var9.result = var2.applyAsInt(var9.result, var10.result);
               }
            }
         }

      }
   }

   static final class MapReduceValuesToIntTask<K, V> extends ConcurrentHashMap.BulkTask<K, V, Integer> {
      final ToIntFunction<? super V> transformer;
      final IntBinaryOperator reducer;
      final int basis;
      int result;
      ConcurrentHashMap.MapReduceValuesToIntTask<K, V> rights;
      ConcurrentHashMap.MapReduceValuesToIntTask<K, V> nextRight;

      MapReduceValuesToIntTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, ConcurrentHashMap.MapReduceValuesToIntTask<K, V> var6, ToIntFunction<? super V> var7, int var8, IntBinaryOperator var9) {
         super(var1, var2, var3, var4, var5);
         this.nextRight = var6;
         this.transformer = var7;
         this.basis = var8;
         this.reducer = var9;
      }

      public final Integer getRawResult() {
         return this.result;
      }

      public final void compute() {
         ToIntFunction var1;
         IntBinaryOperator var2;
         if ((var1 = this.transformer) != null && (var2 = this.reducer) != null) {
            int var3 = this.basis;
            int var4 = this.baseIndex;

            int var5;
            int var6;
            while(this.batch > 0 && (var6 = (var5 = this.baseLimit) + var4 >>> 1) > var4) {
               this.addToPendingCount(1);
               (this.rights = new ConcurrentHashMap.MapReduceValuesToIntTask(this, this.batch >>>= 1, this.baseLimit = var6, var5, this.tab, this.rights, var1, var3, var2)).fork();
            }

            ConcurrentHashMap.Node var7;
            while((var7 = this.advance()) != null) {
               var3 = var2.applyAsInt(var3, var1.applyAsInt(var7.val));
            }

            this.result = var3;

            for(CountedCompleter var8 = this.firstComplete(); var8 != null; var8 = var8.nextComplete()) {
               ConcurrentHashMap.MapReduceValuesToIntTask var9 = (ConcurrentHashMap.MapReduceValuesToIntTask)var8;

               for(ConcurrentHashMap.MapReduceValuesToIntTask var10 = var9.rights; var10 != null; var10 = var9.rights = var10.nextRight) {
                  var9.result = var2.applyAsInt(var9.result, var10.result);
               }
            }
         }

      }
   }

   static final class MapReduceKeysToIntTask<K, V> extends ConcurrentHashMap.BulkTask<K, V, Integer> {
      final ToIntFunction<? super K> transformer;
      final IntBinaryOperator reducer;
      final int basis;
      int result;
      ConcurrentHashMap.MapReduceKeysToIntTask<K, V> rights;
      ConcurrentHashMap.MapReduceKeysToIntTask<K, V> nextRight;

      MapReduceKeysToIntTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, ConcurrentHashMap.MapReduceKeysToIntTask<K, V> var6, ToIntFunction<? super K> var7, int var8, IntBinaryOperator var9) {
         super(var1, var2, var3, var4, var5);
         this.nextRight = var6;
         this.transformer = var7;
         this.basis = var8;
         this.reducer = var9;
      }

      public final Integer getRawResult() {
         return this.result;
      }

      public final void compute() {
         ToIntFunction var1;
         IntBinaryOperator var2;
         if ((var1 = this.transformer) != null && (var2 = this.reducer) != null) {
            int var3 = this.basis;
            int var4 = this.baseIndex;

            int var5;
            int var6;
            while(this.batch > 0 && (var6 = (var5 = this.baseLimit) + var4 >>> 1) > var4) {
               this.addToPendingCount(1);
               (this.rights = new ConcurrentHashMap.MapReduceKeysToIntTask(this, this.batch >>>= 1, this.baseLimit = var6, var5, this.tab, this.rights, var1, var3, var2)).fork();
            }

            ConcurrentHashMap.Node var7;
            while((var7 = this.advance()) != null) {
               var3 = var2.applyAsInt(var3, var1.applyAsInt(var7.key));
            }

            this.result = var3;

            for(CountedCompleter var8 = this.firstComplete(); var8 != null; var8 = var8.nextComplete()) {
               ConcurrentHashMap.MapReduceKeysToIntTask var9 = (ConcurrentHashMap.MapReduceKeysToIntTask)var8;

               for(ConcurrentHashMap.MapReduceKeysToIntTask var10 = var9.rights; var10 != null; var10 = var9.rights = var10.nextRight) {
                  var9.result = var2.applyAsInt(var9.result, var10.result);
               }
            }
         }

      }
   }

   static final class MapReduceMappingsToLongTask<K, V> extends ConcurrentHashMap.BulkTask<K, V, Long> {
      final ToLongBiFunction<? super K, ? super V> transformer;
      final LongBinaryOperator reducer;
      final long basis;
      long result;
      ConcurrentHashMap.MapReduceMappingsToLongTask<K, V> rights;
      ConcurrentHashMap.MapReduceMappingsToLongTask<K, V> nextRight;

      MapReduceMappingsToLongTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, ConcurrentHashMap.MapReduceMappingsToLongTask<K, V> var6, ToLongBiFunction<? super K, ? super V> var7, long var8, LongBinaryOperator var10) {
         super(var1, var2, var3, var4, var5);
         this.nextRight = var6;
         this.transformer = var7;
         this.basis = var8;
         this.reducer = var10;
      }

      public final Long getRawResult() {
         return this.result;
      }

      public final void compute() {
         ToLongBiFunction var1;
         LongBinaryOperator var2;
         if ((var1 = this.transformer) != null && (var2 = this.reducer) != null) {
            long var3 = this.basis;
            int var5 = this.baseIndex;

            int var6;
            int var7;
            while(this.batch > 0 && (var7 = (var6 = this.baseLimit) + var5 >>> 1) > var5) {
               this.addToPendingCount(1);
               (this.rights = new ConcurrentHashMap.MapReduceMappingsToLongTask(this, this.batch >>>= 1, this.baseLimit = var7, var6, this.tab, this.rights, var1, var3, var2)).fork();
            }

            ConcurrentHashMap.Node var8;
            while((var8 = this.advance()) != null) {
               var3 = var2.applyAsLong(var3, var1.applyAsLong(var8.key, var8.val));
            }

            this.result = var3;

            for(CountedCompleter var9 = this.firstComplete(); var9 != null; var9 = var9.nextComplete()) {
               ConcurrentHashMap.MapReduceMappingsToLongTask var10 = (ConcurrentHashMap.MapReduceMappingsToLongTask)var9;

               for(ConcurrentHashMap.MapReduceMappingsToLongTask var11 = var10.rights; var11 != null; var11 = var10.rights = var11.nextRight) {
                  var10.result = var2.applyAsLong(var10.result, var11.result);
               }
            }
         }

      }
   }

   static final class MapReduceEntriesToLongTask<K, V> extends ConcurrentHashMap.BulkTask<K, V, Long> {
      final ToLongFunction<Map.Entry<K, V>> transformer;
      final LongBinaryOperator reducer;
      final long basis;
      long result;
      ConcurrentHashMap.MapReduceEntriesToLongTask<K, V> rights;
      ConcurrentHashMap.MapReduceEntriesToLongTask<K, V> nextRight;

      MapReduceEntriesToLongTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, ConcurrentHashMap.MapReduceEntriesToLongTask<K, V> var6, ToLongFunction<Map.Entry<K, V>> var7, long var8, LongBinaryOperator var10) {
         super(var1, var2, var3, var4, var5);
         this.nextRight = var6;
         this.transformer = var7;
         this.basis = var8;
         this.reducer = var10;
      }

      public final Long getRawResult() {
         return this.result;
      }

      public final void compute() {
         ToLongFunction var1;
         LongBinaryOperator var2;
         if ((var1 = this.transformer) != null && (var2 = this.reducer) != null) {
            long var3 = this.basis;
            int var5 = this.baseIndex;

            int var6;
            int var7;
            while(this.batch > 0 && (var7 = (var6 = this.baseLimit) + var5 >>> 1) > var5) {
               this.addToPendingCount(1);
               (this.rights = new ConcurrentHashMap.MapReduceEntriesToLongTask(this, this.batch >>>= 1, this.baseLimit = var7, var6, this.tab, this.rights, var1, var3, var2)).fork();
            }

            ConcurrentHashMap.Node var8;
            while((var8 = this.advance()) != null) {
               var3 = var2.applyAsLong(var3, var1.applyAsLong(var8));
            }

            this.result = var3;

            for(CountedCompleter var9 = this.firstComplete(); var9 != null; var9 = var9.nextComplete()) {
               ConcurrentHashMap.MapReduceEntriesToLongTask var10 = (ConcurrentHashMap.MapReduceEntriesToLongTask)var9;

               for(ConcurrentHashMap.MapReduceEntriesToLongTask var11 = var10.rights; var11 != null; var11 = var10.rights = var11.nextRight) {
                  var10.result = var2.applyAsLong(var10.result, var11.result);
               }
            }
         }

      }
   }

   static final class MapReduceValuesToLongTask<K, V> extends ConcurrentHashMap.BulkTask<K, V, Long> {
      final ToLongFunction<? super V> transformer;
      final LongBinaryOperator reducer;
      final long basis;
      long result;
      ConcurrentHashMap.MapReduceValuesToLongTask<K, V> rights;
      ConcurrentHashMap.MapReduceValuesToLongTask<K, V> nextRight;

      MapReduceValuesToLongTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, ConcurrentHashMap.MapReduceValuesToLongTask<K, V> var6, ToLongFunction<? super V> var7, long var8, LongBinaryOperator var10) {
         super(var1, var2, var3, var4, var5);
         this.nextRight = var6;
         this.transformer = var7;
         this.basis = var8;
         this.reducer = var10;
      }

      public final Long getRawResult() {
         return this.result;
      }

      public final void compute() {
         ToLongFunction var1;
         LongBinaryOperator var2;
         if ((var1 = this.transformer) != null && (var2 = this.reducer) != null) {
            long var3 = this.basis;
            int var5 = this.baseIndex;

            int var6;
            int var7;
            while(this.batch > 0 && (var7 = (var6 = this.baseLimit) + var5 >>> 1) > var5) {
               this.addToPendingCount(1);
               (this.rights = new ConcurrentHashMap.MapReduceValuesToLongTask(this, this.batch >>>= 1, this.baseLimit = var7, var6, this.tab, this.rights, var1, var3, var2)).fork();
            }

            ConcurrentHashMap.Node var8;
            while((var8 = this.advance()) != null) {
               var3 = var2.applyAsLong(var3, var1.applyAsLong(var8.val));
            }

            this.result = var3;

            for(CountedCompleter var9 = this.firstComplete(); var9 != null; var9 = var9.nextComplete()) {
               ConcurrentHashMap.MapReduceValuesToLongTask var10 = (ConcurrentHashMap.MapReduceValuesToLongTask)var9;

               for(ConcurrentHashMap.MapReduceValuesToLongTask var11 = var10.rights; var11 != null; var11 = var10.rights = var11.nextRight) {
                  var10.result = var2.applyAsLong(var10.result, var11.result);
               }
            }
         }

      }
   }

   static final class MapReduceKeysToLongTask<K, V> extends ConcurrentHashMap.BulkTask<K, V, Long> {
      final ToLongFunction<? super K> transformer;
      final LongBinaryOperator reducer;
      final long basis;
      long result;
      ConcurrentHashMap.MapReduceKeysToLongTask<K, V> rights;
      ConcurrentHashMap.MapReduceKeysToLongTask<K, V> nextRight;

      MapReduceKeysToLongTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, ConcurrentHashMap.MapReduceKeysToLongTask<K, V> var6, ToLongFunction<? super K> var7, long var8, LongBinaryOperator var10) {
         super(var1, var2, var3, var4, var5);
         this.nextRight = var6;
         this.transformer = var7;
         this.basis = var8;
         this.reducer = var10;
      }

      public final Long getRawResult() {
         return this.result;
      }

      public final void compute() {
         ToLongFunction var1;
         LongBinaryOperator var2;
         if ((var1 = this.transformer) != null && (var2 = this.reducer) != null) {
            long var3 = this.basis;
            int var5 = this.baseIndex;

            int var6;
            int var7;
            while(this.batch > 0 && (var7 = (var6 = this.baseLimit) + var5 >>> 1) > var5) {
               this.addToPendingCount(1);
               (this.rights = new ConcurrentHashMap.MapReduceKeysToLongTask(this, this.batch >>>= 1, this.baseLimit = var7, var6, this.tab, this.rights, var1, var3, var2)).fork();
            }

            ConcurrentHashMap.Node var8;
            while((var8 = this.advance()) != null) {
               var3 = var2.applyAsLong(var3, var1.applyAsLong(var8.key));
            }

            this.result = var3;

            for(CountedCompleter var9 = this.firstComplete(); var9 != null; var9 = var9.nextComplete()) {
               ConcurrentHashMap.MapReduceKeysToLongTask var10 = (ConcurrentHashMap.MapReduceKeysToLongTask)var9;

               for(ConcurrentHashMap.MapReduceKeysToLongTask var11 = var10.rights; var11 != null; var11 = var10.rights = var11.nextRight) {
                  var10.result = var2.applyAsLong(var10.result, var11.result);
               }
            }
         }

      }
   }

   static final class MapReduceMappingsToDoubleTask<K, V> extends ConcurrentHashMap.BulkTask<K, V, Double> {
      final ToDoubleBiFunction<? super K, ? super V> transformer;
      final DoubleBinaryOperator reducer;
      final double basis;
      double result;
      ConcurrentHashMap.MapReduceMappingsToDoubleTask<K, V> rights;
      ConcurrentHashMap.MapReduceMappingsToDoubleTask<K, V> nextRight;

      MapReduceMappingsToDoubleTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, ConcurrentHashMap.MapReduceMappingsToDoubleTask<K, V> var6, ToDoubleBiFunction<? super K, ? super V> var7, double var8, DoubleBinaryOperator var10) {
         super(var1, var2, var3, var4, var5);
         this.nextRight = var6;
         this.transformer = var7;
         this.basis = var8;
         this.reducer = var10;
      }

      public final Double getRawResult() {
         return this.result;
      }

      public final void compute() {
         ToDoubleBiFunction var1;
         DoubleBinaryOperator var2;
         if ((var1 = this.transformer) != null && (var2 = this.reducer) != null) {
            double var3 = this.basis;
            int var5 = this.baseIndex;

            int var6;
            int var7;
            while(this.batch > 0 && (var7 = (var6 = this.baseLimit) + var5 >>> 1) > var5) {
               this.addToPendingCount(1);
               (this.rights = new ConcurrentHashMap.MapReduceMappingsToDoubleTask(this, this.batch >>>= 1, this.baseLimit = var7, var6, this.tab, this.rights, var1, var3, var2)).fork();
            }

            ConcurrentHashMap.Node var8;
            while((var8 = this.advance()) != null) {
               var3 = var2.applyAsDouble(var3, var1.applyAsDouble(var8.key, var8.val));
            }

            this.result = var3;

            for(CountedCompleter var9 = this.firstComplete(); var9 != null; var9 = var9.nextComplete()) {
               ConcurrentHashMap.MapReduceMappingsToDoubleTask var10 = (ConcurrentHashMap.MapReduceMappingsToDoubleTask)var9;

               for(ConcurrentHashMap.MapReduceMappingsToDoubleTask var11 = var10.rights; var11 != null; var11 = var10.rights = var11.nextRight) {
                  var10.result = var2.applyAsDouble(var10.result, var11.result);
               }
            }
         }

      }
   }

   static final class MapReduceEntriesToDoubleTask<K, V> extends ConcurrentHashMap.BulkTask<K, V, Double> {
      final ToDoubleFunction<Map.Entry<K, V>> transformer;
      final DoubleBinaryOperator reducer;
      final double basis;
      double result;
      ConcurrentHashMap.MapReduceEntriesToDoubleTask<K, V> rights;
      ConcurrentHashMap.MapReduceEntriesToDoubleTask<K, V> nextRight;

      MapReduceEntriesToDoubleTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, ConcurrentHashMap.MapReduceEntriesToDoubleTask<K, V> var6, ToDoubleFunction<Map.Entry<K, V>> var7, double var8, DoubleBinaryOperator var10) {
         super(var1, var2, var3, var4, var5);
         this.nextRight = var6;
         this.transformer = var7;
         this.basis = var8;
         this.reducer = var10;
      }

      public final Double getRawResult() {
         return this.result;
      }

      public final void compute() {
         ToDoubleFunction var1;
         DoubleBinaryOperator var2;
         if ((var1 = this.transformer) != null && (var2 = this.reducer) != null) {
            double var3 = this.basis;
            int var5 = this.baseIndex;

            int var6;
            int var7;
            while(this.batch > 0 && (var7 = (var6 = this.baseLimit) + var5 >>> 1) > var5) {
               this.addToPendingCount(1);
               (this.rights = new ConcurrentHashMap.MapReduceEntriesToDoubleTask(this, this.batch >>>= 1, this.baseLimit = var7, var6, this.tab, this.rights, var1, var3, var2)).fork();
            }

            ConcurrentHashMap.Node var8;
            while((var8 = this.advance()) != null) {
               var3 = var2.applyAsDouble(var3, var1.applyAsDouble(var8));
            }

            this.result = var3;

            for(CountedCompleter var9 = this.firstComplete(); var9 != null; var9 = var9.nextComplete()) {
               ConcurrentHashMap.MapReduceEntriesToDoubleTask var10 = (ConcurrentHashMap.MapReduceEntriesToDoubleTask)var9;

               for(ConcurrentHashMap.MapReduceEntriesToDoubleTask var11 = var10.rights; var11 != null; var11 = var10.rights = var11.nextRight) {
                  var10.result = var2.applyAsDouble(var10.result, var11.result);
               }
            }
         }

      }
   }

   static final class MapReduceValuesToDoubleTask<K, V> extends ConcurrentHashMap.BulkTask<K, V, Double> {
      final ToDoubleFunction<? super V> transformer;
      final DoubleBinaryOperator reducer;
      final double basis;
      double result;
      ConcurrentHashMap.MapReduceValuesToDoubleTask<K, V> rights;
      ConcurrentHashMap.MapReduceValuesToDoubleTask<K, V> nextRight;

      MapReduceValuesToDoubleTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, ConcurrentHashMap.MapReduceValuesToDoubleTask<K, V> var6, ToDoubleFunction<? super V> var7, double var8, DoubleBinaryOperator var10) {
         super(var1, var2, var3, var4, var5);
         this.nextRight = var6;
         this.transformer = var7;
         this.basis = var8;
         this.reducer = var10;
      }

      public final Double getRawResult() {
         return this.result;
      }

      public final void compute() {
         ToDoubleFunction var1;
         DoubleBinaryOperator var2;
         if ((var1 = this.transformer) != null && (var2 = this.reducer) != null) {
            double var3 = this.basis;
            int var5 = this.baseIndex;

            int var6;
            int var7;
            while(this.batch > 0 && (var7 = (var6 = this.baseLimit) + var5 >>> 1) > var5) {
               this.addToPendingCount(1);
               (this.rights = new ConcurrentHashMap.MapReduceValuesToDoubleTask(this, this.batch >>>= 1, this.baseLimit = var7, var6, this.tab, this.rights, var1, var3, var2)).fork();
            }

            ConcurrentHashMap.Node var8;
            while((var8 = this.advance()) != null) {
               var3 = var2.applyAsDouble(var3, var1.applyAsDouble(var8.val));
            }

            this.result = var3;

            for(CountedCompleter var9 = this.firstComplete(); var9 != null; var9 = var9.nextComplete()) {
               ConcurrentHashMap.MapReduceValuesToDoubleTask var10 = (ConcurrentHashMap.MapReduceValuesToDoubleTask)var9;

               for(ConcurrentHashMap.MapReduceValuesToDoubleTask var11 = var10.rights; var11 != null; var11 = var10.rights = var11.nextRight) {
                  var10.result = var2.applyAsDouble(var10.result, var11.result);
               }
            }
         }

      }
   }

   static final class MapReduceKeysToDoubleTask<K, V> extends ConcurrentHashMap.BulkTask<K, V, Double> {
      final ToDoubleFunction<? super K> transformer;
      final DoubleBinaryOperator reducer;
      final double basis;
      double result;
      ConcurrentHashMap.MapReduceKeysToDoubleTask<K, V> rights;
      ConcurrentHashMap.MapReduceKeysToDoubleTask<K, V> nextRight;

      MapReduceKeysToDoubleTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, ConcurrentHashMap.MapReduceKeysToDoubleTask<K, V> var6, ToDoubleFunction<? super K> var7, double var8, DoubleBinaryOperator var10) {
         super(var1, var2, var3, var4, var5);
         this.nextRight = var6;
         this.transformer = var7;
         this.basis = var8;
         this.reducer = var10;
      }

      public final Double getRawResult() {
         return this.result;
      }

      public final void compute() {
         ToDoubleFunction var1;
         DoubleBinaryOperator var2;
         if ((var1 = this.transformer) != null && (var2 = this.reducer) != null) {
            double var3 = this.basis;
            int var5 = this.baseIndex;

            int var6;
            int var7;
            while(this.batch > 0 && (var7 = (var6 = this.baseLimit) + var5 >>> 1) > var5) {
               this.addToPendingCount(1);
               (this.rights = new ConcurrentHashMap.MapReduceKeysToDoubleTask(this, this.batch >>>= 1, this.baseLimit = var7, var6, this.tab, this.rights, var1, var3, var2)).fork();
            }

            ConcurrentHashMap.Node var8;
            while((var8 = this.advance()) != null) {
               var3 = var2.applyAsDouble(var3, var1.applyAsDouble(var8.key));
            }

            this.result = var3;

            for(CountedCompleter var9 = this.firstComplete(); var9 != null; var9 = var9.nextComplete()) {
               ConcurrentHashMap.MapReduceKeysToDoubleTask var10 = (ConcurrentHashMap.MapReduceKeysToDoubleTask)var9;

               for(ConcurrentHashMap.MapReduceKeysToDoubleTask var11 = var10.rights; var11 != null; var11 = var10.rights = var11.nextRight) {
                  var10.result = var2.applyAsDouble(var10.result, var11.result);
               }
            }
         }

      }
   }

   static final class MapReduceMappingsTask<K, V, U> extends ConcurrentHashMap.BulkTask<K, V, U> {
      final BiFunction<? super K, ? super V, ? extends U> transformer;
      final BiFunction<? super U, ? super U, ? extends U> reducer;
      U result;
      ConcurrentHashMap.MapReduceMappingsTask<K, V, U> rights;
      ConcurrentHashMap.MapReduceMappingsTask<K, V, U> nextRight;

      MapReduceMappingsTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, ConcurrentHashMap.MapReduceMappingsTask<K, V, U> var6, BiFunction<? super K, ? super V, ? extends U> var7, BiFunction<? super U, ? super U, ? extends U> var8) {
         super(var1, var2, var3, var4, var5);
         this.nextRight = var6;
         this.transformer = var7;
         this.reducer = var8;
      }

      public final U getRawResult() {
         return this.result;
      }

      public final void compute() {
         BiFunction var1;
         BiFunction var2;
         if ((var1 = this.transformer) != null && (var2 = this.reducer) != null) {
            int var3 = this.baseIndex;

            int var4;
            int var5;
            while(this.batch > 0 && (var5 = (var4 = this.baseLimit) + var3 >>> 1) > var3) {
               this.addToPendingCount(1);
               (this.rights = new ConcurrentHashMap.MapReduceMappingsTask(this, this.batch >>>= 1, this.baseLimit = var5, var4, this.tab, this.rights, var1, var2)).fork();
            }

            Object var9 = null;

            ConcurrentHashMap.Node var10;
            while((var10 = this.advance()) != null) {
               Object var12;
               if ((var12 = var1.apply(var10.key, var10.val)) != null) {
                  var9 = var9 == null ? var12 : var2.apply(var9, var12);
               }
            }

            this.result = var9;

            for(CountedCompleter var11 = this.firstComplete(); var11 != null; var11 = var11.nextComplete()) {
               ConcurrentHashMap.MapReduceMappingsTask var13 = (ConcurrentHashMap.MapReduceMappingsTask)var11;

               for(ConcurrentHashMap.MapReduceMappingsTask var6 = var13.rights; var6 != null; var6 = var13.rights = var6.nextRight) {
                  Object var8;
                  if ((var8 = var6.result) != null) {
                     Object var7;
                     var13.result = (var7 = var13.result) == null ? var8 : var2.apply(var7, var8);
                  }
               }
            }
         }

      }
   }

   static final class MapReduceEntriesTask<K, V, U> extends ConcurrentHashMap.BulkTask<K, V, U> {
      final Function<Map.Entry<K, V>, ? extends U> transformer;
      final BiFunction<? super U, ? super U, ? extends U> reducer;
      U result;
      ConcurrentHashMap.MapReduceEntriesTask<K, V, U> rights;
      ConcurrentHashMap.MapReduceEntriesTask<K, V, U> nextRight;

      MapReduceEntriesTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, ConcurrentHashMap.MapReduceEntriesTask<K, V, U> var6, Function<Map.Entry<K, V>, ? extends U> var7, BiFunction<? super U, ? super U, ? extends U> var8) {
         super(var1, var2, var3, var4, var5);
         this.nextRight = var6;
         this.transformer = var7;
         this.reducer = var8;
      }

      public final U getRawResult() {
         return this.result;
      }

      public final void compute() {
         Function var1;
         BiFunction var2;
         if ((var1 = this.transformer) != null && (var2 = this.reducer) != null) {
            int var3 = this.baseIndex;

            int var4;
            int var5;
            while(this.batch > 0 && (var5 = (var4 = this.baseLimit) + var3 >>> 1) > var3) {
               this.addToPendingCount(1);
               (this.rights = new ConcurrentHashMap.MapReduceEntriesTask(this, this.batch >>>= 1, this.baseLimit = var5, var4, this.tab, this.rights, var1, var2)).fork();
            }

            Object var9 = null;

            ConcurrentHashMap.Node var10;
            while((var10 = this.advance()) != null) {
               Object var12;
               if ((var12 = var1.apply(var10)) != null) {
                  var9 = var9 == null ? var12 : var2.apply(var9, var12);
               }
            }

            this.result = var9;

            for(CountedCompleter var11 = this.firstComplete(); var11 != null; var11 = var11.nextComplete()) {
               ConcurrentHashMap.MapReduceEntriesTask var13 = (ConcurrentHashMap.MapReduceEntriesTask)var11;

               for(ConcurrentHashMap.MapReduceEntriesTask var6 = var13.rights; var6 != null; var6 = var13.rights = var6.nextRight) {
                  Object var8;
                  if ((var8 = var6.result) != null) {
                     Object var7;
                     var13.result = (var7 = var13.result) == null ? var8 : var2.apply(var7, var8);
                  }
               }
            }
         }

      }
   }

   static final class MapReduceValuesTask<K, V, U> extends ConcurrentHashMap.BulkTask<K, V, U> {
      final Function<? super V, ? extends U> transformer;
      final BiFunction<? super U, ? super U, ? extends U> reducer;
      U result;
      ConcurrentHashMap.MapReduceValuesTask<K, V, U> rights;
      ConcurrentHashMap.MapReduceValuesTask<K, V, U> nextRight;

      MapReduceValuesTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, ConcurrentHashMap.MapReduceValuesTask<K, V, U> var6, Function<? super V, ? extends U> var7, BiFunction<? super U, ? super U, ? extends U> var8) {
         super(var1, var2, var3, var4, var5);
         this.nextRight = var6;
         this.transformer = var7;
         this.reducer = var8;
      }

      public final U getRawResult() {
         return this.result;
      }

      public final void compute() {
         Function var1;
         BiFunction var2;
         if ((var1 = this.transformer) != null && (var2 = this.reducer) != null) {
            int var3 = this.baseIndex;

            int var4;
            int var5;
            while(this.batch > 0 && (var5 = (var4 = this.baseLimit) + var3 >>> 1) > var3) {
               this.addToPendingCount(1);
               (this.rights = new ConcurrentHashMap.MapReduceValuesTask(this, this.batch >>>= 1, this.baseLimit = var5, var4, this.tab, this.rights, var1, var2)).fork();
            }

            Object var9 = null;

            ConcurrentHashMap.Node var10;
            while((var10 = this.advance()) != null) {
               Object var12;
               if ((var12 = var1.apply(var10.val)) != null) {
                  var9 = var9 == null ? var12 : var2.apply(var9, var12);
               }
            }

            this.result = var9;

            for(CountedCompleter var11 = this.firstComplete(); var11 != null; var11 = var11.nextComplete()) {
               ConcurrentHashMap.MapReduceValuesTask var13 = (ConcurrentHashMap.MapReduceValuesTask)var11;

               for(ConcurrentHashMap.MapReduceValuesTask var6 = var13.rights; var6 != null; var6 = var13.rights = var6.nextRight) {
                  Object var8;
                  if ((var8 = var6.result) != null) {
                     Object var7;
                     var13.result = (var7 = var13.result) == null ? var8 : var2.apply(var7, var8);
                  }
               }
            }
         }

      }
   }

   static final class MapReduceKeysTask<K, V, U> extends ConcurrentHashMap.BulkTask<K, V, U> {
      final Function<? super K, ? extends U> transformer;
      final BiFunction<? super U, ? super U, ? extends U> reducer;
      U result;
      ConcurrentHashMap.MapReduceKeysTask<K, V, U> rights;
      ConcurrentHashMap.MapReduceKeysTask<K, V, U> nextRight;

      MapReduceKeysTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, ConcurrentHashMap.MapReduceKeysTask<K, V, U> var6, Function<? super K, ? extends U> var7, BiFunction<? super U, ? super U, ? extends U> var8) {
         super(var1, var2, var3, var4, var5);
         this.nextRight = var6;
         this.transformer = var7;
         this.reducer = var8;
      }

      public final U getRawResult() {
         return this.result;
      }

      public final void compute() {
         Function var1;
         BiFunction var2;
         if ((var1 = this.transformer) != null && (var2 = this.reducer) != null) {
            int var3 = this.baseIndex;

            int var4;
            int var5;
            while(this.batch > 0 && (var5 = (var4 = this.baseLimit) + var3 >>> 1) > var3) {
               this.addToPendingCount(1);
               (this.rights = new ConcurrentHashMap.MapReduceKeysTask(this, this.batch >>>= 1, this.baseLimit = var5, var4, this.tab, this.rights, var1, var2)).fork();
            }

            Object var9 = null;

            ConcurrentHashMap.Node var10;
            while((var10 = this.advance()) != null) {
               Object var12;
               if ((var12 = var1.apply(var10.key)) != null) {
                  var9 = var9 == null ? var12 : var2.apply(var9, var12);
               }
            }

            this.result = var9;

            for(CountedCompleter var11 = this.firstComplete(); var11 != null; var11 = var11.nextComplete()) {
               ConcurrentHashMap.MapReduceKeysTask var13 = (ConcurrentHashMap.MapReduceKeysTask)var11;

               for(ConcurrentHashMap.MapReduceKeysTask var6 = var13.rights; var6 != null; var6 = var13.rights = var6.nextRight) {
                  Object var8;
                  if ((var8 = var6.result) != null) {
                     Object var7;
                     var13.result = (var7 = var13.result) == null ? var8 : var2.apply(var7, var8);
                  }
               }
            }
         }

      }
   }

   static final class ReduceEntriesTask<K, V> extends ConcurrentHashMap.BulkTask<K, V, Map.Entry<K, V>> {
      final BiFunction<Map.Entry<K, V>, Map.Entry<K, V>, ? extends Map.Entry<K, V>> reducer;
      Map.Entry<K, V> result;
      ConcurrentHashMap.ReduceEntriesTask<K, V> rights;
      ConcurrentHashMap.ReduceEntriesTask<K, V> nextRight;

      ReduceEntriesTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, ConcurrentHashMap.ReduceEntriesTask<K, V> var6, BiFunction<Map.Entry<K, V>, Map.Entry<K, V>, ? extends Map.Entry<K, V>> var7) {
         super(var1, var2, var3, var4, var5);
         this.nextRight = var6;
         this.reducer = var7;
      }

      public final Map.Entry<K, V> getRawResult() {
         return this.result;
      }

      public final void compute() {
         BiFunction var1;
         if ((var1 = this.reducer) != null) {
            int var2 = this.baseIndex;

            int var3;
            int var4;
            while(this.batch > 0 && (var4 = (var3 = this.baseLimit) + var2 >>> 1) > var2) {
               this.addToPendingCount(1);
               (this.rights = new ConcurrentHashMap.ReduceEntriesTask(this, this.batch >>>= 1, this.baseLimit = var4, var3, this.tab, this.rights, var1)).fork();
            }

            Object var8;
            ConcurrentHashMap.Node var9;
            for(var8 = null; (var9 = this.advance()) != null; var8 = var8 == null ? var9 : (Map.Entry)var1.apply(var8, var9)) {
            }

            this.result = (Map.Entry)var8;

            for(CountedCompleter var10 = this.firstComplete(); var10 != null; var10 = var10.nextComplete()) {
               ConcurrentHashMap.ReduceEntriesTask var11 = (ConcurrentHashMap.ReduceEntriesTask)var10;

               for(ConcurrentHashMap.ReduceEntriesTask var5 = var11.rights; var5 != null; var5 = var11.rights = var5.nextRight) {
                  Map.Entry var7;
                  if ((var7 = var5.result) != null) {
                     Map.Entry var6;
                     var11.result = (var6 = var11.result) == null ? var7 : (Map.Entry)var1.apply(var6, var7);
                  }
               }
            }
         }

      }
   }

   static final class ReduceValuesTask<K, V> extends ConcurrentHashMap.BulkTask<K, V, V> {
      final BiFunction<? super V, ? super V, ? extends V> reducer;
      V result;
      ConcurrentHashMap.ReduceValuesTask<K, V> rights;
      ConcurrentHashMap.ReduceValuesTask<K, V> nextRight;

      ReduceValuesTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, ConcurrentHashMap.ReduceValuesTask<K, V> var6, BiFunction<? super V, ? super V, ? extends V> var7) {
         super(var1, var2, var3, var4, var5);
         this.nextRight = var6;
         this.reducer = var7;
      }

      public final V getRawResult() {
         return this.result;
      }

      public final void compute() {
         BiFunction var1;
         if ((var1 = this.reducer) != null) {
            int var2 = this.baseIndex;

            int var3;
            int var4;
            while(this.batch > 0 && (var4 = (var3 = this.baseLimit) + var2 >>> 1) > var2) {
               this.addToPendingCount(1);
               (this.rights = new ConcurrentHashMap.ReduceValuesTask(this, this.batch >>>= 1, this.baseLimit = var4, var3, this.tab, this.rights, var1)).fork();
            }

            Object var8;
            ConcurrentHashMap.Node var9;
            Object var11;
            for(var8 = null; (var9 = this.advance()) != null; var8 = var8 == null ? var11 : var1.apply(var8, var11)) {
               var11 = var9.val;
            }

            this.result = var8;

            for(CountedCompleter var10 = this.firstComplete(); var10 != null; var10 = var10.nextComplete()) {
               ConcurrentHashMap.ReduceValuesTask var12 = (ConcurrentHashMap.ReduceValuesTask)var10;

               for(ConcurrentHashMap.ReduceValuesTask var5 = var12.rights; var5 != null; var5 = var12.rights = var5.nextRight) {
                  Object var7;
                  if ((var7 = var5.result) != null) {
                     Object var6;
                     var12.result = (var6 = var12.result) == null ? var7 : var1.apply(var6, var7);
                  }
               }
            }
         }

      }
   }

   static final class ReduceKeysTask<K, V> extends ConcurrentHashMap.BulkTask<K, V, K> {
      final BiFunction<? super K, ? super K, ? extends K> reducer;
      K result;
      ConcurrentHashMap.ReduceKeysTask<K, V> rights;
      ConcurrentHashMap.ReduceKeysTask<K, V> nextRight;

      ReduceKeysTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, ConcurrentHashMap.ReduceKeysTask<K, V> var6, BiFunction<? super K, ? super K, ? extends K> var7) {
         super(var1, var2, var3, var4, var5);
         this.nextRight = var6;
         this.reducer = var7;
      }

      public final K getRawResult() {
         return this.result;
      }

      public final void compute() {
         BiFunction var1;
         if ((var1 = this.reducer) != null) {
            int var2 = this.baseIndex;

            int var3;
            int var4;
            while(this.batch > 0 && (var4 = (var3 = this.baseLimit) + var2 >>> 1) > var2) {
               this.addToPendingCount(1);
               (this.rights = new ConcurrentHashMap.ReduceKeysTask(this, this.batch >>>= 1, this.baseLimit = var4, var3, this.tab, this.rights, var1)).fork();
            }

            Object var8;
            ConcurrentHashMap.Node var9;
            Object var11;
            for(var8 = null; (var9 = this.advance()) != null; var8 = var8 == null ? var11 : (var11 == null ? var8 : var1.apply(var8, var11))) {
               var11 = var9.key;
            }

            this.result = var8;

            for(CountedCompleter var10 = this.firstComplete(); var10 != null; var10 = var10.nextComplete()) {
               ConcurrentHashMap.ReduceKeysTask var12 = (ConcurrentHashMap.ReduceKeysTask)var10;

               for(ConcurrentHashMap.ReduceKeysTask var5 = var12.rights; var5 != null; var5 = var12.rights = var5.nextRight) {
                  Object var7;
                  if ((var7 = var5.result) != null) {
                     Object var6;
                     var12.result = (var6 = var12.result) == null ? var7 : var1.apply(var6, var7);
                  }
               }
            }
         }

      }
   }

   static final class SearchMappingsTask<K, V, U> extends ConcurrentHashMap.BulkTask<K, V, U> {
      final BiFunction<? super K, ? super V, ? extends U> searchFunction;
      final AtomicReference<U> result;

      SearchMappingsTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, BiFunction<? super K, ? super V, ? extends U> var6, AtomicReference<U> var7) {
         super(var1, var2, var3, var4, var5);
         this.searchFunction = var6;
         this.result = var7;
      }

      public final U getRawResult() {
         return this.result.get();
      }

      public final void compute() {
         BiFunction var1;
         AtomicReference var2;
         if ((var1 = this.searchFunction) != null && (var2 = this.result) != null) {
            int var3 = this.baseIndex;

            int var4;
            int var5;
            while(this.batch > 0 && (var5 = (var4 = this.baseLimit) + var3 >>> 1) > var3) {
               if (var2.get() != null) {
                  return;
               }

               this.addToPendingCount(1);
               (new ConcurrentHashMap.SearchMappingsTask(this, this.batch >>>= 1, this.baseLimit = var5, var4, this.tab, var1, var2)).fork();
            }

            while(var2.get() == null) {
               ConcurrentHashMap.Node var7;
               if ((var7 = this.advance()) == null) {
                  this.propagateCompletion();
                  break;
               }

               Object var6;
               if ((var6 = var1.apply(var7.key, var7.val)) != null) {
                  if (var2.compareAndSet((Object)null, var6)) {
                     this.quietlyCompleteRoot();
                  }
                  break;
               }
            }
         }

      }
   }

   static final class SearchEntriesTask<K, V, U> extends ConcurrentHashMap.BulkTask<K, V, U> {
      final Function<Map.Entry<K, V>, ? extends U> searchFunction;
      final AtomicReference<U> result;

      SearchEntriesTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, Function<Map.Entry<K, V>, ? extends U> var6, AtomicReference<U> var7) {
         super(var1, var2, var3, var4, var5);
         this.searchFunction = var6;
         this.result = var7;
      }

      public final U getRawResult() {
         return this.result.get();
      }

      public final void compute() {
         Function var1;
         AtomicReference var2;
         if ((var1 = this.searchFunction) != null && (var2 = this.result) != null) {
            int var3 = this.baseIndex;

            int var4;
            int var5;
            while(this.batch > 0 && (var5 = (var4 = this.baseLimit) + var3 >>> 1) > var3) {
               if (var2.get() != null) {
                  return;
               }

               this.addToPendingCount(1);
               (new ConcurrentHashMap.SearchEntriesTask(this, this.batch >>>= 1, this.baseLimit = var5, var4, this.tab, var1, var2)).fork();
            }

            while(var2.get() == null) {
               ConcurrentHashMap.Node var7;
               if ((var7 = this.advance()) == null) {
                  this.propagateCompletion();
                  break;
               }

               Object var6;
               if ((var6 = var1.apply(var7)) != null) {
                  if (var2.compareAndSet((Object)null, var6)) {
                     this.quietlyCompleteRoot();
                  }

                  return;
               }
            }
         }

      }
   }

   static final class SearchValuesTask<K, V, U> extends ConcurrentHashMap.BulkTask<K, V, U> {
      final Function<? super V, ? extends U> searchFunction;
      final AtomicReference<U> result;

      SearchValuesTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, Function<? super V, ? extends U> var6, AtomicReference<U> var7) {
         super(var1, var2, var3, var4, var5);
         this.searchFunction = var6;
         this.result = var7;
      }

      public final U getRawResult() {
         return this.result.get();
      }

      public final void compute() {
         Function var1;
         AtomicReference var2;
         if ((var1 = this.searchFunction) != null && (var2 = this.result) != null) {
            int var3 = this.baseIndex;

            int var4;
            int var5;
            while(this.batch > 0 && (var5 = (var4 = this.baseLimit) + var3 >>> 1) > var3) {
               if (var2.get() != null) {
                  return;
               }

               this.addToPendingCount(1);
               (new ConcurrentHashMap.SearchValuesTask(this, this.batch >>>= 1, this.baseLimit = var5, var4, this.tab, var1, var2)).fork();
            }

            while(var2.get() == null) {
               ConcurrentHashMap.Node var7;
               if ((var7 = this.advance()) == null) {
                  this.propagateCompletion();
                  break;
               }

               Object var6;
               if ((var6 = var1.apply(var7.val)) != null) {
                  if (var2.compareAndSet((Object)null, var6)) {
                     this.quietlyCompleteRoot();
                  }
                  break;
               }
            }
         }

      }
   }

   static final class SearchKeysTask<K, V, U> extends ConcurrentHashMap.BulkTask<K, V, U> {
      final Function<? super K, ? extends U> searchFunction;
      final AtomicReference<U> result;

      SearchKeysTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, Function<? super K, ? extends U> var6, AtomicReference<U> var7) {
         super(var1, var2, var3, var4, var5);
         this.searchFunction = var6;
         this.result = var7;
      }

      public final U getRawResult() {
         return this.result.get();
      }

      public final void compute() {
         Function var1;
         AtomicReference var2;
         if ((var1 = this.searchFunction) != null && (var2 = this.result) != null) {
            int var3 = this.baseIndex;

            int var4;
            int var5;
            while(this.batch > 0 && (var5 = (var4 = this.baseLimit) + var3 >>> 1) > var3) {
               if (var2.get() != null) {
                  return;
               }

               this.addToPendingCount(1);
               (new ConcurrentHashMap.SearchKeysTask(this, this.batch >>>= 1, this.baseLimit = var5, var4, this.tab, var1, var2)).fork();
            }

            while(var2.get() == null) {
               ConcurrentHashMap.Node var7;
               if ((var7 = this.advance()) == null) {
                  this.propagateCompletion();
                  break;
               }

               Object var6;
               if ((var6 = var1.apply(var7.key)) != null) {
                  if (var2.compareAndSet((Object)null, var6)) {
                     this.quietlyCompleteRoot();
                  }
                  break;
               }
            }
         }

      }
   }

   static final class ForEachTransformedMappingTask<K, V, U> extends ConcurrentHashMap.BulkTask<K, V, Void> {
      final BiFunction<? super K, ? super V, ? extends U> transformer;
      final Consumer<? super U> action;

      ForEachTransformedMappingTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, BiFunction<? super K, ? super V, ? extends U> var6, Consumer<? super U> var7) {
         super(var1, var2, var3, var4, var5);
         this.transformer = var6;
         this.action = var7;
      }

      public final void compute() {
         BiFunction var1;
         Consumer var2;
         if ((var1 = this.transformer) != null && (var2 = this.action) != null) {
            int var3 = this.baseIndex;

            int var4;
            int var5;
            while(this.batch > 0 && (var5 = (var4 = this.baseLimit) + var3 >>> 1) > var3) {
               this.addToPendingCount(1);
               (new ConcurrentHashMap.ForEachTransformedMappingTask(this, this.batch >>>= 1, this.baseLimit = var5, var4, this.tab, var1, var2)).fork();
            }

            ConcurrentHashMap.Node var6;
            while((var6 = this.advance()) != null) {
               Object var7;
               if ((var7 = var1.apply(var6.key, var6.val)) != null) {
                  var2.accept(var7);
               }
            }

            this.propagateCompletion();
         }

      }
   }

   static final class ForEachTransformedEntryTask<K, V, U> extends ConcurrentHashMap.BulkTask<K, V, Void> {
      final Function<Map.Entry<K, V>, ? extends U> transformer;
      final Consumer<? super U> action;

      ForEachTransformedEntryTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, Function<Map.Entry<K, V>, ? extends U> var6, Consumer<? super U> var7) {
         super(var1, var2, var3, var4, var5);
         this.transformer = var6;
         this.action = var7;
      }

      public final void compute() {
         Function var1;
         Consumer var2;
         if ((var1 = this.transformer) != null && (var2 = this.action) != null) {
            int var3 = this.baseIndex;

            int var4;
            int var5;
            while(this.batch > 0 && (var5 = (var4 = this.baseLimit) + var3 >>> 1) > var3) {
               this.addToPendingCount(1);
               (new ConcurrentHashMap.ForEachTransformedEntryTask(this, this.batch >>>= 1, this.baseLimit = var5, var4, this.tab, var1, var2)).fork();
            }

            ConcurrentHashMap.Node var6;
            while((var6 = this.advance()) != null) {
               Object var7;
               if ((var7 = var1.apply(var6)) != null) {
                  var2.accept(var7);
               }
            }

            this.propagateCompletion();
         }

      }
   }

   static final class ForEachTransformedValueTask<K, V, U> extends ConcurrentHashMap.BulkTask<K, V, Void> {
      final Function<? super V, ? extends U> transformer;
      final Consumer<? super U> action;

      ForEachTransformedValueTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, Function<? super V, ? extends U> var6, Consumer<? super U> var7) {
         super(var1, var2, var3, var4, var5);
         this.transformer = var6;
         this.action = var7;
      }

      public final void compute() {
         Function var1;
         Consumer var2;
         if ((var1 = this.transformer) != null && (var2 = this.action) != null) {
            int var3 = this.baseIndex;

            int var4;
            int var5;
            while(this.batch > 0 && (var5 = (var4 = this.baseLimit) + var3 >>> 1) > var3) {
               this.addToPendingCount(1);
               (new ConcurrentHashMap.ForEachTransformedValueTask(this, this.batch >>>= 1, this.baseLimit = var5, var4, this.tab, var1, var2)).fork();
            }

            ConcurrentHashMap.Node var6;
            while((var6 = this.advance()) != null) {
               Object var7;
               if ((var7 = var1.apply(var6.val)) != null) {
                  var2.accept(var7);
               }
            }

            this.propagateCompletion();
         }

      }
   }

   static final class ForEachTransformedKeyTask<K, V, U> extends ConcurrentHashMap.BulkTask<K, V, Void> {
      final Function<? super K, ? extends U> transformer;
      final Consumer<? super U> action;

      ForEachTransformedKeyTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, Function<? super K, ? extends U> var6, Consumer<? super U> var7) {
         super(var1, var2, var3, var4, var5);
         this.transformer = var6;
         this.action = var7;
      }

      public final void compute() {
         Function var1;
         Consumer var2;
         if ((var1 = this.transformer) != null && (var2 = this.action) != null) {
            int var3 = this.baseIndex;

            int var4;
            int var5;
            while(this.batch > 0 && (var5 = (var4 = this.baseLimit) + var3 >>> 1) > var3) {
               this.addToPendingCount(1);
               (new ConcurrentHashMap.ForEachTransformedKeyTask(this, this.batch >>>= 1, this.baseLimit = var5, var4, this.tab, var1, var2)).fork();
            }

            ConcurrentHashMap.Node var6;
            while((var6 = this.advance()) != null) {
               Object var7;
               if ((var7 = var1.apply(var6.key)) != null) {
                  var2.accept(var7);
               }
            }

            this.propagateCompletion();
         }

      }
   }

   static final class ForEachMappingTask<K, V> extends ConcurrentHashMap.BulkTask<K, V, Void> {
      final BiConsumer<? super K, ? super V> action;

      ForEachMappingTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, BiConsumer<? super K, ? super V> var6) {
         super(var1, var2, var3, var4, var5);
         this.action = var6;
      }

      public final void compute() {
         BiConsumer var1;
         if ((var1 = this.action) != null) {
            int var2 = this.baseIndex;

            int var3;
            int var4;
            while(this.batch > 0 && (var4 = (var3 = this.baseLimit) + var2 >>> 1) > var2) {
               this.addToPendingCount(1);
               (new ConcurrentHashMap.ForEachMappingTask(this, this.batch >>>= 1, this.baseLimit = var4, var3, this.tab, var1)).fork();
            }

            ConcurrentHashMap.Node var5;
            while((var5 = this.advance()) != null) {
               var1.accept(var5.key, var5.val);
            }

            this.propagateCompletion();
         }

      }
   }

   static final class ForEachEntryTask<K, V> extends ConcurrentHashMap.BulkTask<K, V, Void> {
      final Consumer<? super Map.Entry<K, V>> action;

      ForEachEntryTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, Consumer<? super Map.Entry<K, V>> var6) {
         super(var1, var2, var3, var4, var5);
         this.action = var6;
      }

      public final void compute() {
         Consumer var1;
         if ((var1 = this.action) != null) {
            int var2 = this.baseIndex;

            int var3;
            int var4;
            while(this.batch > 0 && (var4 = (var3 = this.baseLimit) + var2 >>> 1) > var2) {
               this.addToPendingCount(1);
               (new ConcurrentHashMap.ForEachEntryTask(this, this.batch >>>= 1, this.baseLimit = var4, var3, this.tab, var1)).fork();
            }

            ConcurrentHashMap.Node var5;
            while((var5 = this.advance()) != null) {
               var1.accept(var5);
            }

            this.propagateCompletion();
         }

      }
   }

   static final class ForEachValueTask<K, V> extends ConcurrentHashMap.BulkTask<K, V, Void> {
      final Consumer<? super V> action;

      ForEachValueTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, Consumer<? super V> var6) {
         super(var1, var2, var3, var4, var5);
         this.action = var6;
      }

      public final void compute() {
         Consumer var1;
         if ((var1 = this.action) != null) {
            int var2 = this.baseIndex;

            int var3;
            int var4;
            while(this.batch > 0 && (var4 = (var3 = this.baseLimit) + var2 >>> 1) > var2) {
               this.addToPendingCount(1);
               (new ConcurrentHashMap.ForEachValueTask(this, this.batch >>>= 1, this.baseLimit = var4, var3, this.tab, var1)).fork();
            }

            ConcurrentHashMap.Node var5;
            while((var5 = this.advance()) != null) {
               var1.accept(var5.val);
            }

            this.propagateCompletion();
         }

      }
   }

   static final class ForEachKeyTask<K, V> extends ConcurrentHashMap.BulkTask<K, V, Void> {
      final Consumer<? super K> action;

      ForEachKeyTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5, Consumer<? super K> var6) {
         super(var1, var2, var3, var4, var5);
         this.action = var6;
      }

      public final void compute() {
         Consumer var1;
         if ((var1 = this.action) != null) {
            int var2 = this.baseIndex;

            int var3;
            int var4;
            while(this.batch > 0 && (var4 = (var3 = this.baseLimit) + var2 >>> 1) > var2) {
               this.addToPendingCount(1);
               (new ConcurrentHashMap.ForEachKeyTask(this, this.batch >>>= 1, this.baseLimit = var4, var3, this.tab, var1)).fork();
            }

            ConcurrentHashMap.Node var5;
            while((var5 = this.advance()) != null) {
               var1.accept(var5.key);
            }

            this.propagateCompletion();
         }

      }
   }

   abstract static class BulkTask<K, V, R> extends CountedCompleter<R> {
      ConcurrentHashMap.Node<K, V>[] tab;
      ConcurrentHashMap.Node<K, V> next;
      ConcurrentHashMap.TableStack<K, V> stack;
      ConcurrentHashMap.TableStack<K, V> spare;
      int index;
      int baseIndex;
      int baseLimit;
      final int baseSize;
      int batch;

      BulkTask(ConcurrentHashMap.BulkTask<K, V, ?> var1, int var2, int var3, int var4, ConcurrentHashMap.Node<K, V>[] var5) {
         super(var1);
         this.batch = var2;
         this.index = this.baseIndex = var3;
         if ((this.tab = var5) == null) {
            this.baseSize = this.baseLimit = 0;
         } else if (var1 == null) {
            this.baseSize = this.baseLimit = var5.length;
         } else {
            this.baseLimit = var4;
            this.baseSize = var1.baseSize;
         }

      }

      final ConcurrentHashMap.Node<K, V> advance() {
         Object var1;
         if ((var1 = this.next) != null) {
            var1 = ((ConcurrentHashMap.Node)var1).next;
         }

         while(var1 == null) {
            ConcurrentHashMap.Node[] var2;
            int var3;
            int var4;
            if (this.baseIndex >= this.baseLimit || (var2 = this.tab) == null || (var4 = var2.length) <= (var3 = this.index) || var3 < 0) {
               return this.next = null;
            }

            if ((var1 = ConcurrentHashMap.tabAt(var2, var3)) != null && ((ConcurrentHashMap.Node)var1).hash < 0) {
               if (var1 instanceof ConcurrentHashMap.ForwardingNode) {
                  this.tab = ((ConcurrentHashMap.ForwardingNode)var1).nextTable;
                  var1 = null;
                  this.pushState(var2, var3, var4);
                  continue;
               }

               if (var1 instanceof ConcurrentHashMap.TreeBin) {
                  var1 = ((ConcurrentHashMap.TreeBin)var1).first;
               } else {
                  var1 = null;
               }
            }

            if (this.stack != null) {
               this.recoverState(var4);
            } else if ((this.index = var3 + this.baseSize) >= var4) {
               this.index = ++this.baseIndex;
            }
         }

         return this.next = (ConcurrentHashMap.Node)var1;
      }

      private void pushState(ConcurrentHashMap.Node<K, V>[] var1, int var2, int var3) {
         ConcurrentHashMap.TableStack var4 = this.spare;
         if (var4 != null) {
            this.spare = var4.next;
         } else {
            var4 = new ConcurrentHashMap.TableStack();
         }

         var4.tab = var1;
         var4.length = var3;
         var4.index = var2;
         var4.next = this.stack;
         this.stack = var4;
      }

      private void recoverState(int var1) {
         ConcurrentHashMap.TableStack var2;
         int var3;
         while((var2 = this.stack) != null && (this.index += var3 = var2.length) >= var1) {
            var1 = var3;
            this.index = var2.index;
            this.tab = var2.tab;
            var2.tab = null;
            ConcurrentHashMap.TableStack var4 = var2.next;
            var2.next = this.spare;
            this.stack = var4;
            this.spare = var2;
         }

         if (var2 == null && (this.index += this.baseSize) >= var1) {
            this.index = ++this.baseIndex;
         }

      }
   }

   static final class EntrySetView<K, V> extends ConcurrentHashMap.CollectionView<K, V, Map.Entry<K, V>> implements Set<Map.Entry<K, V>>, Serializable {
      private static final long serialVersionUID = 2249069246763182397L;

      EntrySetView(ConcurrentHashMap<K, V> var1) {
         super(var1);
      }

      public boolean contains(Object var1) {
         Object var2;
         Object var3;
         Object var4;
         Map.Entry var5;
         return var1 instanceof Map.Entry && (var2 = (var5 = (Map.Entry)var1).getKey()) != null && (var4 = this.map.get(var2)) != null && (var3 = var5.getValue()) != null && (var3 == var4 || var3.equals(var4));
      }

      public boolean remove(Object var1) {
         Object var2;
         Object var3;
         Map.Entry var4;
         return var1 instanceof Map.Entry && (var2 = (var4 = (Map.Entry)var1).getKey()) != null && (var3 = var4.getValue()) != null && this.map.remove(var2, var3);
      }

      public Iterator<Map.Entry<K, V>> iterator() {
         ConcurrentHashMap var1 = this.map;
         ConcurrentHashMap.Node[] var2;
         int var3 = (var2 = var1.table) == null ? 0 : var2.length;
         return new ConcurrentHashMap.EntryIterator(var2, var3, 0, var3, var1);
      }

      public boolean add(Map.Entry<K, V> var1) {
         return this.map.putVal(var1.getKey(), var1.getValue(), false) == null;
      }

      public boolean addAll(Collection<? extends Map.Entry<K, V>> var1) {
         boolean var2 = false;
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            Map.Entry var4 = (Map.Entry)var3.next();
            if (this.add(var4)) {
               var2 = true;
            }
         }

         return var2;
      }

      public final int hashCode() {
         int var1 = 0;
         ConcurrentHashMap.Node[] var2;
         ConcurrentHashMap.Node var4;
         if ((var2 = this.map.table) != null) {
            for(ConcurrentHashMap.Traverser var3 = new ConcurrentHashMap.Traverser(var2, var2.length, 0, var2.length); (var4 = var3.advance()) != null; var1 += var4.hashCode()) {
            }
         }

         return var1;
      }

      public final boolean equals(Object var1) {
         Set var2;
         return var1 instanceof Set && ((var2 = (Set)var1) == this || this.containsAll(var2) && var2.containsAll(this));
      }

      public Spliterator<Map.Entry<K, V>> spliterator() {
         ConcurrentHashMap var2 = this.map;
         long var3 = var2.sumCount();
         ConcurrentHashMap.Node[] var1;
         int var5 = (var1 = var2.table) == null ? 0 : var1.length;
         return new ConcurrentHashMap.EntrySpliterator(var1, var5, 0, var5, var3 < 0L ? 0L : var3, var2);
      }

      public void forEach(Consumer<? super Map.Entry<K, V>> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            ConcurrentHashMap.Node[] var2;
            if ((var2 = this.map.table) != null) {
               ConcurrentHashMap.Traverser var3 = new ConcurrentHashMap.Traverser(var2, var2.length, 0, var2.length);

               ConcurrentHashMap.Node var4;
               while((var4 = var3.advance()) != null) {
                  var1.accept(new ConcurrentHashMap.MapEntry(var4.key, var4.val, this.map));
               }
            }

         }
      }
   }

   static final class ValuesView<K, V> extends ConcurrentHashMap.CollectionView<K, V, V> implements Collection<V>, Serializable {
      private static final long serialVersionUID = 2249069246763182397L;

      ValuesView(ConcurrentHashMap<K, V> var1) {
         super(var1);
      }

      public final boolean contains(Object var1) {
         return this.map.containsValue(var1);
      }

      public final boolean remove(Object var1) {
         if (var1 != null) {
            Iterator var2 = this.iterator();

            while(var2.hasNext()) {
               if (var1.equals(var2.next())) {
                  var2.remove();
                  return true;
               }
            }
         }

         return false;
      }

      public final Iterator<V> iterator() {
         ConcurrentHashMap var1 = this.map;
         ConcurrentHashMap.Node[] var2;
         int var3 = (var2 = var1.table) == null ? 0 : var2.length;
         return new ConcurrentHashMap.ValueIterator(var2, var3, 0, var3, var1);
      }

      public final boolean add(V var1) {
         throw new UnsupportedOperationException();
      }

      public final boolean addAll(Collection<? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public Spliterator<V> spliterator() {
         ConcurrentHashMap var2 = this.map;
         long var3 = var2.sumCount();
         ConcurrentHashMap.Node[] var1;
         int var5 = (var1 = var2.table) == null ? 0 : var1.length;
         return new ConcurrentHashMap.ValueSpliterator(var1, var5, 0, var5, var3 < 0L ? 0L : var3);
      }

      public void forEach(Consumer<? super V> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            ConcurrentHashMap.Node[] var2;
            if ((var2 = this.map.table) != null) {
               ConcurrentHashMap.Traverser var3 = new ConcurrentHashMap.Traverser(var2, var2.length, 0, var2.length);

               ConcurrentHashMap.Node var4;
               while((var4 = var3.advance()) != null) {
                  var1.accept(var4.val);
               }
            }

         }
      }
   }

   public static class KeySetView<K, V> extends ConcurrentHashMap.CollectionView<K, V, K> implements Set<K>, Serializable {
      private static final long serialVersionUID = 7249069246763182397L;
      private final V value;

      KeySetView(ConcurrentHashMap<K, V> var1, V var2) {
         super(var1);
         this.value = var2;
      }

      public V getMappedValue() {
         return this.value;
      }

      public boolean contains(Object var1) {
         return this.map.containsKey(var1);
      }

      public boolean remove(Object var1) {
         return this.map.remove(var1) != null;
      }

      public Iterator<K> iterator() {
         ConcurrentHashMap var2 = this.map;
         ConcurrentHashMap.Node[] var1;
         int var3 = (var1 = var2.table) == null ? 0 : var1.length;
         return new ConcurrentHashMap.KeyIterator(var1, var3, 0, var3, var2);
      }

      public boolean add(K var1) {
         Object var2;
         if ((var2 = this.value) == null) {
            throw new UnsupportedOperationException();
         } else {
            return this.map.putVal(var1, var2, true) == null;
         }
      }

      public boolean addAll(Collection<? extends K> var1) {
         boolean var2 = false;
         Object var3;
         if ((var3 = this.value) == null) {
            throw new UnsupportedOperationException();
         } else {
            Iterator var4 = var1.iterator();

            while(var4.hasNext()) {
               Object var5 = var4.next();
               if (this.map.putVal(var5, var3, true) == null) {
                  var2 = true;
               }
            }

            return var2;
         }
      }

      public int hashCode() {
         int var1 = 0;

         Object var3;
         for(Iterator var2 = this.iterator(); var2.hasNext(); var1 += var3.hashCode()) {
            var3 = var2.next();
         }

         return var1;
      }

      public boolean equals(Object var1) {
         Set var2;
         return var1 instanceof Set && ((var2 = (Set)var1) == this || this.containsAll(var2) && var2.containsAll(this));
      }

      public Spliterator<K> spliterator() {
         ConcurrentHashMap var2 = this.map;
         long var3 = var2.sumCount();
         ConcurrentHashMap.Node[] var1;
         int var5 = (var1 = var2.table) == null ? 0 : var1.length;
         return new ConcurrentHashMap.KeySpliterator(var1, var5, 0, var5, var3 < 0L ? 0L : var3);
      }

      public void forEach(Consumer<? super K> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            ConcurrentHashMap.Node[] var2;
            if ((var2 = this.map.table) != null) {
               ConcurrentHashMap.Traverser var3 = new ConcurrentHashMap.Traverser(var2, var2.length, 0, var2.length);

               ConcurrentHashMap.Node var4;
               while((var4 = var3.advance()) != null) {
                  var1.accept(var4.key);
               }
            }

         }
      }
   }

   abstract static class CollectionView<K, V, E> implements Collection<E>, Serializable {
      private static final long serialVersionUID = 7249069246763182397L;
      final ConcurrentHashMap<K, V> map;
      private static final String oomeMsg = "Required array size too large";

      CollectionView(ConcurrentHashMap<K, V> var1) {
         this.map = var1;
      }

      public ConcurrentHashMap<K, V> getMap() {
         return this.map;
      }

      public final void clear() {
         this.map.clear();
      }

      public final int size() {
         return this.map.size();
      }

      public final boolean isEmpty() {
         return this.map.isEmpty();
      }

      public abstract Iterator<E> iterator();

      public abstract boolean contains(Object var1);

      public abstract boolean remove(Object var1);

      public final Object[] toArray() {
         long var1 = this.map.mappingCount();
         if (var1 > 2147483639L) {
            throw new OutOfMemoryError("Required array size too large");
         } else {
            int var3 = (int)var1;
            Object[] var4 = new Object[var3];
            int var5 = 0;

            Object var7;
            for(Iterator var6 = this.iterator(); var6.hasNext(); var4[var5++] = var7) {
               var7 = var6.next();
               if (var5 == var3) {
                  if (var3 >= 2147483639) {
                     throw new OutOfMemoryError("Required array size too large");
                  }

                  if (var3 >= 1073741819) {
                     var3 = 2147483639;
                  } else {
                     var3 += (var3 >>> 1) + 1;
                  }

                  var4 = Arrays.copyOf(var4, var3);
               }
            }

            return var5 == var3 ? var4 : Arrays.copyOf(var4, var5);
         }
      }

      public final <T> T[] toArray(T[] var1) {
         long var2 = this.map.mappingCount();
         if (var2 > 2147483639L) {
            throw new OutOfMemoryError("Required array size too large");
         } else {
            int var4 = (int)var2;
            Object[] var5 = var1.length >= var4 ? var1 : (Object[])((Object[])Array.newInstance(var1.getClass().getComponentType(), var4));
            int var6 = var5.length;
            int var7 = 0;

            Object var9;
            for(Iterator var8 = this.iterator(); var8.hasNext(); var5[var7++] = var9) {
               var9 = var8.next();
               if (var7 == var6) {
                  if (var6 >= 2147483639) {
                     throw new OutOfMemoryError("Required array size too large");
                  }

                  if (var6 >= 1073741819) {
                     var6 = 2147483639;
                  } else {
                     var6 += (var6 >>> 1) + 1;
                  }

                  var5 = Arrays.copyOf(var5, var6);
               }
            }

            if (var1 == var5 && var7 < var6) {
               var5[var7] = null;
               return var5;
            } else {
               return var7 == var6 ? var5 : Arrays.copyOf(var5, var7);
            }
         }
      }

      public final String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append('[');
         Iterator var2 = this.iterator();
         if (var2.hasNext()) {
            while(true) {
               Object var3 = var2.next();
               var1.append(var3 == this ? "(this Collection)" : var3);
               if (!var2.hasNext()) {
                  break;
               }

               var1.append(',').append(' ');
            }
         }

         return var1.append(']').toString();
      }

      public final boolean containsAll(Collection<?> var1) {
         if (var1 != this) {
            Iterator var2 = var1.iterator();

            while(var2.hasNext()) {
               Object var3 = var2.next();
               if (var3 == null || !this.contains(var3)) {
                  return false;
               }
            }
         }

         return true;
      }

      public final boolean removeAll(Collection<?> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            boolean var2 = false;
            Iterator var3 = this.iterator();

            while(var3.hasNext()) {
               if (var1.contains(var3.next())) {
                  var3.remove();
                  var2 = true;
               }
            }

            return var2;
         }
      }

      public final boolean retainAll(Collection<?> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            boolean var2 = false;
            Iterator var3 = this.iterator();

            while(var3.hasNext()) {
               if (!var1.contains(var3.next())) {
                  var3.remove();
                  var2 = true;
               }
            }

            return var2;
         }
      }
   }

   static final class EntrySpliterator<K, V> extends ConcurrentHashMap.Traverser<K, V> implements Spliterator<Map.Entry<K, V>> {
      final ConcurrentHashMap<K, V> map;
      long est;

      EntrySpliterator(ConcurrentHashMap.Node<K, V>[] var1, int var2, int var3, int var4, long var5, ConcurrentHashMap<K, V> var7) {
         super(var1, var2, var3, var4);
         this.map = var7;
         this.est = var5;
      }

      public Spliterator<Map.Entry<K, V>> trySplit() {
         int var1;
         int var2;
         int var3;
         return (var3 = (var1 = this.baseIndex) + (var2 = this.baseLimit) >>> 1) <= var1 ? null : new ConcurrentHashMap.EntrySpliterator(this.tab, this.baseSize, this.baseLimit = var3, var2, this.est >>>= 1, this.map);
      }

      public void forEachRemaining(Consumer<? super Map.Entry<K, V>> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            ConcurrentHashMap.Node var2;
            while((var2 = this.advance()) != null) {
               var1.accept(new ConcurrentHashMap.MapEntry(var2.key, var2.val, this.map));
            }

         }
      }

      public boolean tryAdvance(Consumer<? super Map.Entry<K, V>> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            ConcurrentHashMap.Node var2;
            if ((var2 = this.advance()) == null) {
               return false;
            } else {
               var1.accept(new ConcurrentHashMap.MapEntry(var2.key, var2.val, this.map));
               return true;
            }
         }
      }

      public long estimateSize() {
         return this.est;
      }

      public int characteristics() {
         return 4353;
      }
   }

   static final class ValueSpliterator<K, V> extends ConcurrentHashMap.Traverser<K, V> implements Spliterator<V> {
      long est;

      ValueSpliterator(ConcurrentHashMap.Node<K, V>[] var1, int var2, int var3, int var4, long var5) {
         super(var1, var2, var3, var4);
         this.est = var5;
      }

      public Spliterator<V> trySplit() {
         int var1;
         int var2;
         int var3;
         return (var3 = (var1 = this.baseIndex) + (var2 = this.baseLimit) >>> 1) <= var1 ? null : new ConcurrentHashMap.ValueSpliterator(this.tab, this.baseSize, this.baseLimit = var3, var2, this.est >>>= 1);
      }

      public void forEachRemaining(Consumer<? super V> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            ConcurrentHashMap.Node var2;
            while((var2 = this.advance()) != null) {
               var1.accept(var2.val);
            }

         }
      }

      public boolean tryAdvance(Consumer<? super V> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            ConcurrentHashMap.Node var2;
            if ((var2 = this.advance()) == null) {
               return false;
            } else {
               var1.accept(var2.val);
               return true;
            }
         }
      }

      public long estimateSize() {
         return this.est;
      }

      public int characteristics() {
         return 4352;
      }
   }

   static final class KeySpliterator<K, V> extends ConcurrentHashMap.Traverser<K, V> implements Spliterator<K> {
      long est;

      KeySpliterator(ConcurrentHashMap.Node<K, V>[] var1, int var2, int var3, int var4, long var5) {
         super(var1, var2, var3, var4);
         this.est = var5;
      }

      public Spliterator<K> trySplit() {
         int var1;
         int var2;
         int var3;
         return (var3 = (var1 = this.baseIndex) + (var2 = this.baseLimit) >>> 1) <= var1 ? null : new ConcurrentHashMap.KeySpliterator(this.tab, this.baseSize, this.baseLimit = var3, var2, this.est >>>= 1);
      }

      public void forEachRemaining(Consumer<? super K> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            ConcurrentHashMap.Node var2;
            while((var2 = this.advance()) != null) {
               var1.accept(var2.key);
            }

         }
      }

      public boolean tryAdvance(Consumer<? super K> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            ConcurrentHashMap.Node var2;
            if ((var2 = this.advance()) == null) {
               return false;
            } else {
               var1.accept(var2.key);
               return true;
            }
         }
      }

      public long estimateSize() {
         return this.est;
      }

      public int characteristics() {
         return 4353;
      }
   }

   static final class MapEntry<K, V> implements Map.Entry<K, V> {
      final K key;
      V val;
      final ConcurrentHashMap<K, V> map;

      MapEntry(K var1, V var2, ConcurrentHashMap<K, V> var3) {
         this.key = var1;
         this.val = var2;
         this.map = var3;
      }

      public K getKey() {
         return this.key;
      }

      public V getValue() {
         return this.val;
      }

      public int hashCode() {
         return this.key.hashCode() ^ this.val.hashCode();
      }

      public String toString() {
         return this.key + "=" + this.val;
      }

      public boolean equals(Object var1) {
         Object var2;
         Object var3;
         Map.Entry var4;
         return var1 instanceof Map.Entry && (var2 = (var4 = (Map.Entry)var1).getKey()) != null && (var3 = var4.getValue()) != null && (var2 == this.key || var2.equals(this.key)) && (var3 == this.val || var3.equals(this.val));
      }

      public V setValue(V var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            Object var2 = this.val;
            this.val = var1;
            this.map.put(this.key, var1);
            return var2;
         }
      }
   }

   static final class EntryIterator<K, V> extends ConcurrentHashMap.BaseIterator<K, V> implements Iterator<Map.Entry<K, V>> {
      EntryIterator(ConcurrentHashMap.Node<K, V>[] var1, int var2, int var3, int var4, ConcurrentHashMap<K, V> var5) {
         super(var1, var2, var3, var4, var5);
      }

      public final Map.Entry<K, V> next() {
         ConcurrentHashMap.Node var1;
         if ((var1 = this.next) == null) {
            throw new NoSuchElementException();
         } else {
            Object var2 = var1.key;
            Object var3 = var1.val;
            this.lastReturned = var1;
            this.advance();
            return new ConcurrentHashMap.MapEntry(var2, var3, this.map);
         }
      }
   }

   static final class ValueIterator<K, V> extends ConcurrentHashMap.BaseIterator<K, V> implements Iterator<V>, Enumeration<V> {
      ValueIterator(ConcurrentHashMap.Node<K, V>[] var1, int var2, int var3, int var4, ConcurrentHashMap<K, V> var5) {
         super(var1, var2, var3, var4, var5);
      }

      public final V next() {
         ConcurrentHashMap.Node var1;
         if ((var1 = this.next) == null) {
            throw new NoSuchElementException();
         } else {
            Object var2 = var1.val;
            this.lastReturned = var1;
            this.advance();
            return var2;
         }
      }

      public final V nextElement() {
         return this.next();
      }
   }

   static final class KeyIterator<K, V> extends ConcurrentHashMap.BaseIterator<K, V> implements Iterator<K>, Enumeration<K> {
      KeyIterator(ConcurrentHashMap.Node<K, V>[] var1, int var2, int var3, int var4, ConcurrentHashMap<K, V> var5) {
         super(var1, var2, var3, var4, var5);
      }

      public final K next() {
         ConcurrentHashMap.Node var1;
         if ((var1 = this.next) == null) {
            throw new NoSuchElementException();
         } else {
            Object var2 = var1.key;
            this.lastReturned = var1;
            this.advance();
            return var2;
         }
      }

      public final K nextElement() {
         return this.next();
      }
   }

   static class BaseIterator<K, V> extends ConcurrentHashMap.Traverser<K, V> {
      final ConcurrentHashMap<K, V> map;
      ConcurrentHashMap.Node<K, V> lastReturned;

      BaseIterator(ConcurrentHashMap.Node<K, V>[] var1, int var2, int var3, int var4, ConcurrentHashMap<K, V> var5) {
         super(var1, var2, var3, var4);
         this.map = var5;
         this.advance();
      }

      public final boolean hasNext() {
         return this.next != null;
      }

      public final boolean hasMoreElements() {
         return this.next != null;
      }

      public final void remove() {
         ConcurrentHashMap.Node var1;
         if ((var1 = this.lastReturned) == null) {
            throw new IllegalStateException();
         } else {
            this.lastReturned = null;
            this.map.replaceNode(var1.key, (Object)null, (Object)null);
         }
      }
   }

   static class Traverser<K, V> {
      ConcurrentHashMap.Node<K, V>[] tab;
      ConcurrentHashMap.Node<K, V> next;
      ConcurrentHashMap.TableStack<K, V> stack;
      ConcurrentHashMap.TableStack<K, V> spare;
      int index;
      int baseIndex;
      int baseLimit;
      final int baseSize;

      Traverser(ConcurrentHashMap.Node<K, V>[] var1, int var2, int var3, int var4) {
         this.tab = var1;
         this.baseSize = var2;
         this.baseIndex = this.index = var3;
         this.baseLimit = var4;
         this.next = null;
      }

      final ConcurrentHashMap.Node<K, V> advance() {
         Object var1;
         if ((var1 = this.next) != null) {
            var1 = ((ConcurrentHashMap.Node)var1).next;
         }

         while(var1 == null) {
            ConcurrentHashMap.Node[] var2;
            int var3;
            int var4;
            if (this.baseIndex >= this.baseLimit || (var2 = this.tab) == null || (var4 = var2.length) <= (var3 = this.index) || var3 < 0) {
               return this.next = null;
            }

            if ((var1 = ConcurrentHashMap.tabAt(var2, var3)) != null && ((ConcurrentHashMap.Node)var1).hash < 0) {
               if (var1 instanceof ConcurrentHashMap.ForwardingNode) {
                  this.tab = ((ConcurrentHashMap.ForwardingNode)var1).nextTable;
                  var1 = null;
                  this.pushState(var2, var3, var4);
                  continue;
               }

               if (var1 instanceof ConcurrentHashMap.TreeBin) {
                  var1 = ((ConcurrentHashMap.TreeBin)var1).first;
               } else {
                  var1 = null;
               }
            }

            if (this.stack != null) {
               this.recoverState(var4);
            } else if ((this.index = var3 + this.baseSize) >= var4) {
               this.index = ++this.baseIndex;
            }
         }

         return this.next = (ConcurrentHashMap.Node)var1;
      }

      private void pushState(ConcurrentHashMap.Node<K, V>[] var1, int var2, int var3) {
         ConcurrentHashMap.TableStack var4 = this.spare;
         if (var4 != null) {
            this.spare = var4.next;
         } else {
            var4 = new ConcurrentHashMap.TableStack();
         }

         var4.tab = var1;
         var4.length = var3;
         var4.index = var2;
         var4.next = this.stack;
         this.stack = var4;
      }

      private void recoverState(int var1) {
         ConcurrentHashMap.TableStack var2;
         int var3;
         while((var2 = this.stack) != null && (this.index += var3 = var2.length) >= var1) {
            var1 = var3;
            this.index = var2.index;
            this.tab = var2.tab;
            var2.tab = null;
            ConcurrentHashMap.TableStack var4 = var2.next;
            var2.next = this.spare;
            this.stack = var4;
            this.spare = var2;
         }

         if (var2 == null && (this.index += this.baseSize) >= var1) {
            this.index = ++this.baseIndex;
         }

      }
   }

   static final class TableStack<K, V> {
      int length;
      int index;
      ConcurrentHashMap.Node<K, V>[] tab;
      ConcurrentHashMap.TableStack<K, V> next;
   }

   static final class TreeBin<K, V> extends ConcurrentHashMap.Node<K, V> {
      ConcurrentHashMap.TreeNode<K, V> root;
      volatile ConcurrentHashMap.TreeNode<K, V> first;
      volatile Thread waiter;
      volatile int lockState;
      static final int WRITER = 1;
      static final int WAITER = 2;
      static final int READER = 4;
      private static final Unsafe U;
      private static final long LOCKSTATE;

      static int tieBreakOrder(Object var0, Object var1) {
         int var2;
         if (var0 == null || var1 == null || (var2 = var0.getClass().getName().compareTo(var1.getClass().getName())) == 0) {
            var2 = System.identityHashCode(var0) <= System.identityHashCode(var1) ? -1 : 1;
         }

         return var2;
      }

      TreeBin(ConcurrentHashMap.TreeNode<K, V> var1) {
         super(-2, (Object)null, (Object)null, (ConcurrentHashMap.Node)null);
         this.first = var1;
         ConcurrentHashMap.TreeNode var2 = null;

         ConcurrentHashMap.TreeNode var4;
         for(ConcurrentHashMap.TreeNode var3 = var1; var3 != null; var3 = var4) {
            var4 = (ConcurrentHashMap.TreeNode)var3.next;
            var3.left = var3.right = null;
            if (var2 == null) {
               var3.parent = null;
               var3.red = false;
               var2 = var3;
            } else {
               Object var5 = var3.key;
               int var6 = var3.hash;
               Class var7 = null;
               ConcurrentHashMap.TreeNode var8 = var2;

               int var9;
               ConcurrentHashMap.TreeNode var12;
               do {
                  Object var11 = var8.key;
                  int var10;
                  if ((var10 = var8.hash) > var6) {
                     var9 = -1;
                  } else if (var10 < var6) {
                     var9 = 1;
                  } else if (var7 == null && (var7 = ConcurrentHashMap.comparableClassFor(var5)) == null || (var9 = ConcurrentHashMap.compareComparables(var7, var5, var11)) == 0) {
                     var9 = tieBreakOrder(var5, var11);
                  }

                  var12 = var8;
               } while((var8 = var9 <= 0 ? var8.left : var8.right) != null);

               var3.parent = var12;
               if (var9 <= 0) {
                  var12.left = var3;
               } else {
                  var12.right = var3;
               }

               var2 = balanceInsertion(var2, var3);
            }
         }

         this.root = var2;

         assert checkInvariants(this.root);

      }

      private final void lockRoot() {
         if (!U.compareAndSwapInt(this, LOCKSTATE, 0, 1)) {
            this.contendedLock();
         }

      }

      private final void unlockRoot() {
         this.lockState = 0;
      }

      private final void contendedLock() {
         boolean var1 = false;

         int var2;
         do {
            while(((var2 = this.lockState) & -3) != 0) {
               if ((var2 & 2) == 0) {
                  if (U.compareAndSwapInt(this, LOCKSTATE, var2, var2 | 2)) {
                     var1 = true;
                     this.waiter = Thread.currentThread();
                  }
               } else if (var1) {
                  LockSupport.park(this);
               }
            }
         } while(!U.compareAndSwapInt(this, LOCKSTATE, var2, 1));

         if (var1) {
            this.waiter = null;
         }

      }

      final ConcurrentHashMap.Node<K, V> find(int var1, Object var2) {
         if (var2 != null) {
            Object var3 = this.first;

            while(var3 != null) {
               int var4;
               if (((var4 = this.lockState) & 3) != 0) {
                  Object var5;
                  if (((ConcurrentHashMap.Node)var3).hash == var1 && ((var5 = ((ConcurrentHashMap.Node)var3).key) == var2 || var5 != null && var2.equals(var5))) {
                     return (ConcurrentHashMap.Node)var3;
                  }

                  var3 = ((ConcurrentHashMap.Node)var3).next;
               } else if (U.compareAndSwapInt(this, LOCKSTATE, var4, var4 + 4)) {
                  boolean var12 = false;

                  ConcurrentHashMap.TreeNode var7;
                  try {
                     var12 = true;
                     ConcurrentHashMap.TreeNode var6;
                     var7 = (var6 = this.root) == null ? null : var6.findTreeNode(var1, var2, (Class)null);
                     var12 = false;
                  } finally {
                     if (var12) {
                        Thread var10;
                        if (U.getAndAddInt(this, LOCKSTATE, -4) == 6 && (var10 = this.waiter) != null) {
                           LockSupport.unpark(var10);
                        }

                     }
                  }

                  Thread var8;
                  if (U.getAndAddInt(this, LOCKSTATE, -4) == 6 && (var8 = this.waiter) != null) {
                     LockSupport.unpark(var8);
                  }

                  return var7;
               }
            }
         }

         return null;
      }

      final ConcurrentHashMap.TreeNode<K, V> putTreeVal(int var1, K var2, V var3) {
         Class var4 = null;
         boolean var5 = false;
         ConcurrentHashMap.TreeNode var6 = this.root;

         while(true) {
            if (var6 == null) {
               this.first = this.root = new ConcurrentHashMap.TreeNode(var1, var2, var3, (ConcurrentHashMap.Node)null, (ConcurrentHashMap.TreeNode)null);
               break;
            }

            int var7;
            int var8;
            ConcurrentHashMap.TreeNode var10;
            ConcurrentHashMap.TreeNode var11;
            if ((var8 = var6.hash) > var1) {
               var7 = -1;
            } else if (var8 < var1) {
               var7 = 1;
            } else {
               Object var9;
               if ((var9 = var6.key) == var2 || var9 != null && var2.equals(var9)) {
                  return var6;
               }

               if (var4 == null && (var4 = ConcurrentHashMap.comparableClassFor(var2)) == null || (var7 = ConcurrentHashMap.compareComparables(var4, var2, var9)) == 0) {
                  if (!var5) {
                     var5 = true;
                     if ((var11 = var6.left) != null && (var10 = var11.findTreeNode(var1, var2, var4)) != null || (var11 = var6.right) != null && (var10 = var11.findTreeNode(var1, var2, var4)) != null) {
                        return var10;
                     }
                  }

                  var7 = tieBreakOrder(var2, var9);
               }
            }

            var10 = var6;
            if ((var6 = var7 <= 0 ? var6.left : var6.right) == null) {
               ConcurrentHashMap.TreeNode var12 = this.first;
               this.first = var11 = new ConcurrentHashMap.TreeNode(var1, var2, var3, var12, var10);
               if (var12 != null) {
                  var12.prev = var11;
               }

               if (var7 <= 0) {
                  var10.left = var11;
               } else {
                  var10.right = var11;
               }

               if (!var10.red) {
                  var11.red = true;
               } else {
                  this.lockRoot();

                  try {
                     this.root = balanceInsertion(this.root, var11);
                  } finally {
                     this.unlockRoot();
                  }
               }
               break;
            }
         }

         assert checkInvariants(this.root);

         return null;
      }

      final boolean removeTreeNode(ConcurrentHashMap.TreeNode<K, V> var1) {
         ConcurrentHashMap.TreeNode var2 = (ConcurrentHashMap.TreeNode)var1.next;
         ConcurrentHashMap.TreeNode var3 = var1.prev;
         if (var3 == null) {
            this.first = var2;
         } else {
            var3.next = var2;
         }

         if (var2 != null) {
            var2.prev = var3;
         }

         if (this.first == null) {
            this.root = null;
            return true;
         } else {
            ConcurrentHashMap.TreeNode var4;
            ConcurrentHashMap.TreeNode var5;
            if ((var4 = this.root) != null && var4.right != null && (var5 = var4.left) != null && var5.left != null) {
               this.lockRoot();

               try {
                  ConcurrentHashMap.TreeNode var7 = var1.left;
                  ConcurrentHashMap.TreeNode var8 = var1.right;
                  ConcurrentHashMap.TreeNode var6;
                  ConcurrentHashMap.TreeNode var9;
                  if (var7 != null && var8 != null) {
                     ConcurrentHashMap.TreeNode var10;
                     for(var9 = var8; (var10 = var9.left) != null; var9 = var10) {
                     }

                     boolean var11 = var9.red;
                     var9.red = var1.red;
                     var1.red = var11;
                     ConcurrentHashMap.TreeNode var12 = var9.right;
                     ConcurrentHashMap.TreeNode var13 = var1.parent;
                     if (var9 == var8) {
                        var1.parent = var9;
                        var9.right = var1;
                     } else {
                        ConcurrentHashMap.TreeNode var14 = var9.parent;
                        if ((var1.parent = var14) != null) {
                           if (var9 == var14.left) {
                              var14.left = var1;
                           } else {
                              var14.right = var1;
                           }
                        }

                        if ((var9.right = var8) != null) {
                           var8.parent = var9;
                        }
                     }

                     var1.left = null;
                     if ((var1.right = var12) != null) {
                        var12.parent = var1;
                     }

                     if ((var9.left = var7) != null) {
                        var7.parent = var9;
                     }

                     if ((var9.parent = var13) == null) {
                        var4 = var9;
                     } else if (var1 == var13.left) {
                        var13.left = var9;
                     } else {
                        var13.right = var9;
                     }

                     if (var12 != null) {
                        var6 = var12;
                     } else {
                        var6 = var1;
                     }
                  } else if (var7 != null) {
                     var6 = var7;
                  } else if (var8 != null) {
                     var6 = var8;
                  } else {
                     var6 = var1;
                  }

                  if (var6 != var1) {
                     var9 = var6.parent = var1.parent;
                     if (var9 == null) {
                        var4 = var6;
                     } else if (var1 == var9.left) {
                        var9.left = var6;
                     } else {
                        var9.right = var6;
                     }

                     var1.left = var1.right = var1.parent = null;
                  }

                  this.root = var1.red ? var4 : balanceDeletion(var4, var6);
                  if (var1 == var6 && (var9 = var1.parent) != null) {
                     if (var1 == var9.left) {
                        var9.left = null;
                     } else if (var1 == var9.right) {
                        var9.right = null;
                     }

                     var1.parent = null;
                  }
               } finally {
                  this.unlockRoot();
               }

               assert checkInvariants(this.root);

               return false;
            } else {
               return true;
            }
         }
      }

      static <K, V> ConcurrentHashMap.TreeNode<K, V> rotateLeft(ConcurrentHashMap.TreeNode<K, V> var0, ConcurrentHashMap.TreeNode<K, V> var1) {
         ConcurrentHashMap.TreeNode var2;
         if (var1 != null && (var2 = var1.right) != null) {
            ConcurrentHashMap.TreeNode var4;
            if ((var4 = var1.right = var2.left) != null) {
               var4.parent = var1;
            }

            ConcurrentHashMap.TreeNode var3;
            if ((var3 = var2.parent = var1.parent) == null) {
               var0 = var2;
               var2.red = false;
            } else if (var3.left == var1) {
               var3.left = var2;
            } else {
               var3.right = var2;
            }

            var2.left = var1;
            var1.parent = var2;
         }

         return var0;
      }

      static <K, V> ConcurrentHashMap.TreeNode<K, V> rotateRight(ConcurrentHashMap.TreeNode<K, V> var0, ConcurrentHashMap.TreeNode<K, V> var1) {
         ConcurrentHashMap.TreeNode var2;
         if (var1 != null && (var2 = var1.left) != null) {
            ConcurrentHashMap.TreeNode var4;
            if ((var4 = var1.left = var2.right) != null) {
               var4.parent = var1;
            }

            ConcurrentHashMap.TreeNode var3;
            if ((var3 = var2.parent = var1.parent) == null) {
               var0 = var2;
               var2.red = false;
            } else if (var3.right == var1) {
               var3.right = var2;
            } else {
               var3.left = var2;
            }

            var2.right = var1;
            var1.parent = var2;
         }

         return var0;
      }

      static <K, V> ConcurrentHashMap.TreeNode<K, V> balanceInsertion(ConcurrentHashMap.TreeNode<K, V> var0, ConcurrentHashMap.TreeNode<K, V> var1) {
         var1.red = true;

         ConcurrentHashMap.TreeNode var2;
         while((var2 = var1.parent) != null) {
            ConcurrentHashMap.TreeNode var3;
            if (!var2.red || (var3 = var2.parent) == null) {
               return var0;
            }

            ConcurrentHashMap.TreeNode var4;
            if (var2 == (var4 = var3.left)) {
               ConcurrentHashMap.TreeNode var5;
               if ((var5 = var3.right) != null && var5.red) {
                  var5.red = false;
                  var2.red = false;
                  var3.red = true;
                  var1 = var3;
               } else {
                  if (var1 == var2.right) {
                     var1 = var2;
                     var0 = rotateLeft(var0, var2);
                     var3 = (var2 = var2.parent) == null ? null : var2.parent;
                  }

                  if (var2 != null) {
                     var2.red = false;
                     if (var3 != null) {
                        var3.red = true;
                        var0 = rotateRight(var0, var3);
                     }
                  }
               }
            } else if (var4 != null && var4.red) {
               var4.red = false;
               var2.red = false;
               var3.red = true;
               var1 = var3;
            } else {
               if (var1 == var2.left) {
                  var1 = var2;
                  var0 = rotateRight(var0, var2);
                  var3 = (var2 = var2.parent) == null ? null : var2.parent;
               }

               if (var2 != null) {
                  var2.red = false;
                  if (var3 != null) {
                     var3.red = true;
                     var0 = rotateLeft(var0, var3);
                  }
               }
            }
         }

         var1.red = false;
         return var1;
      }

      static <K, V> ConcurrentHashMap.TreeNode<K, V> balanceDeletion(ConcurrentHashMap.TreeNode<K, V> var0, ConcurrentHashMap.TreeNode<K, V> var1) {
         while(var1 != null && var1 != var0) {
            ConcurrentHashMap.TreeNode var2;
            if ((var2 = var1.parent) == null) {
               var1.red = false;
               return var1;
            }

            if (var1.red) {
               var1.red = false;
               return var0;
            }

            ConcurrentHashMap.TreeNode var3;
            ConcurrentHashMap.TreeNode var5;
            ConcurrentHashMap.TreeNode var6;
            if ((var3 = var2.left) == var1) {
               ConcurrentHashMap.TreeNode var4;
               if ((var4 = var2.right) != null && var4.red) {
                  var4.red = false;
                  var2.red = true;
                  var0 = rotateLeft(var0, var2);
                  var4 = (var2 = var1.parent) == null ? null : var2.right;
               }

               if (var4 == null) {
                  var1 = var2;
               } else {
                  var5 = var4.left;
                  var6 = var4.right;
                  if (var6 != null && var6.red || var5 != null && var5.red) {
                     if (var6 == null || !var6.red) {
                        if (var5 != null) {
                           var5.red = false;
                        }

                        var4.red = true;
                        var0 = rotateRight(var0, var4);
                        var4 = (var2 = var1.parent) == null ? null : var2.right;
                     }

                     if (var4 != null) {
                        var4.red = var2 == null ? false : var2.red;
                        if ((var6 = var4.right) != null) {
                           var6.red = false;
                        }
                     }

                     if (var2 != null) {
                        var2.red = false;
                        var0 = rotateLeft(var0, var2);
                     }

                     var1 = var0;
                  } else {
                     var4.red = true;
                     var1 = var2;
                  }
               }
            } else {
               if (var3 != null && var3.red) {
                  var3.red = false;
                  var2.red = true;
                  var0 = rotateRight(var0, var2);
                  var3 = (var2 = var1.parent) == null ? null : var2.left;
               }

               if (var3 == null) {
                  var1 = var2;
               } else {
                  var5 = var3.left;
                  var6 = var3.right;
                  if ((var5 == null || !var5.red) && (var6 == null || !var6.red)) {
                     var3.red = true;
                     var1 = var2;
                  } else {
                     if (var5 == null || !var5.red) {
                        if (var6 != null) {
                           var6.red = false;
                        }

                        var3.red = true;
                        var0 = rotateLeft(var0, var3);
                        var3 = (var2 = var1.parent) == null ? null : var2.left;
                     }

                     if (var3 != null) {
                        var3.red = var2 == null ? false : var2.red;
                        if ((var5 = var3.left) != null) {
                           var5.red = false;
                        }
                     }

                     if (var2 != null) {
                        var2.red = false;
                        var0 = rotateRight(var0, var2);
                     }

                     var1 = var0;
                  }
               }
            }
         }

         return var0;
      }

      static <K, V> boolean checkInvariants(ConcurrentHashMap.TreeNode<K, V> var0) {
         ConcurrentHashMap.TreeNode var1 = var0.parent;
         ConcurrentHashMap.TreeNode var2 = var0.left;
         ConcurrentHashMap.TreeNode var3 = var0.right;
         ConcurrentHashMap.TreeNode var4 = var0.prev;
         ConcurrentHashMap.TreeNode var5 = (ConcurrentHashMap.TreeNode)var0.next;
         if (var4 != null && var4.next != var0) {
            return false;
         } else if (var5 != null && var5.prev != var0) {
            return false;
         } else if (var1 != null && var0 != var1.left && var0 != var1.right) {
            return false;
         } else if (var2 != null && (var2.parent != var0 || var2.hash > var0.hash)) {
            return false;
         } else if (var3 == null || var3.parent == var0 && var3.hash >= var0.hash) {
            if (var0.red && var2 != null && var2.red && var3 != null && var3.red) {
               return false;
            } else if (var2 != null && !checkInvariants(var2)) {
               return false;
            } else {
               return var3 == null || checkInvariants(var3);
            }
         } else {
            return false;
         }
      }

      static {
         try {
            U = Unsafe.getUnsafe();
            Class var0 = ConcurrentHashMap.TreeBin.class;
            LOCKSTATE = U.objectFieldOffset(var0.getDeclaredField("lockState"));
         } catch (Exception var1) {
            throw new Error(var1);
         }
      }
   }

   static final class TreeNode<K, V> extends ConcurrentHashMap.Node<K, V> {
      ConcurrentHashMap.TreeNode<K, V> parent;
      ConcurrentHashMap.TreeNode<K, V> left;
      ConcurrentHashMap.TreeNode<K, V> right;
      ConcurrentHashMap.TreeNode<K, V> prev;
      boolean red;

      TreeNode(int var1, K var2, V var3, ConcurrentHashMap.Node<K, V> var4, ConcurrentHashMap.TreeNode<K, V> var5) {
         super(var1, var2, var3, var4);
         this.parent = var5;
      }

      ConcurrentHashMap.Node<K, V> find(int var1, Object var2) {
         return this.findTreeNode(var1, var2, (Class)null);
      }

      final ConcurrentHashMap.TreeNode<K, V> findTreeNode(int var1, Object var2, Class<?> var3) {
         if (var2 != null) {
            ConcurrentHashMap.TreeNode var4 = this;

            do {
               ConcurrentHashMap.TreeNode var9 = var4.left;
               ConcurrentHashMap.TreeNode var10 = var4.right;
               int var5;
               if ((var5 = var4.hash) > var1) {
                  var4 = var9;
               } else if (var5 < var1) {
                  var4 = var10;
               } else {
                  Object var7;
                  if ((var7 = var4.key) == var2 || var7 != null && var2.equals(var7)) {
                     return var4;
                  }

                  if (var9 == null) {
                     var4 = var10;
                  } else if (var10 == null) {
                     var4 = var9;
                  } else {
                     int var6;
                     if ((var3 != null || (var3 = ConcurrentHashMap.comparableClassFor(var2)) != null) && (var6 = ConcurrentHashMap.compareComparables(var3, var2, var7)) != 0) {
                        var4 = var6 < 0 ? var9 : var10;
                     } else {
                        ConcurrentHashMap.TreeNode var8;
                        if ((var8 = var10.findTreeNode(var1, var2, var3)) != null) {
                           return var8;
                        }

                        var4 = var9;
                     }
                  }
               }
            } while(var4 != null);
         }

         return null;
      }
   }

   @Contended
   static final class CounterCell {
      volatile long value;

      CounterCell(long var1) {
         this.value = var1;
      }
   }

   static final class ReservationNode<K, V> extends ConcurrentHashMap.Node<K, V> {
      ReservationNode() {
         super(-3, (Object)null, (Object)null, (ConcurrentHashMap.Node)null);
      }

      ConcurrentHashMap.Node<K, V> find(int var1, Object var2) {
         return null;
      }
   }

   static final class ForwardingNode<K, V> extends ConcurrentHashMap.Node<K, V> {
      final ConcurrentHashMap.Node<K, V>[] nextTable;

      ForwardingNode(ConcurrentHashMap.Node<K, V>[] var1) {
         super(-1, (Object)null, (Object)null, (ConcurrentHashMap.Node)null);
         this.nextTable = var1;
      }

      ConcurrentHashMap.Node<K, V> find(int var1, Object var2) {
         ConcurrentHashMap.Node[] var3 = this.nextTable;

         label41:
         while(true) {
            ConcurrentHashMap.Node var4;
            int var5;
            if (var2 != null && var3 != null && (var5 = var3.length) != 0 && (var4 = ConcurrentHashMap.tabAt(var3, var5 - 1 & var1)) != null) {
               int var6;
               Object var7;
               while((var6 = var4.hash) != var1 || (var7 = var4.key) != var2 && (var7 == null || !var2.equals(var7))) {
                  if (var6 < 0) {
                     if (!(var4 instanceof ConcurrentHashMap.ForwardingNode)) {
                        return var4.find(var1, var2);
                     }

                     var3 = ((ConcurrentHashMap.ForwardingNode)var4).nextTable;
                     continue label41;
                  }

                  if ((var4 = var4.next) == null) {
                     return null;
                  }
               }

               return var4;
            }

            return null;
         }
      }
   }

   static class Segment<K, V> extends ReentrantLock implements Serializable {
      private static final long serialVersionUID = 2249069246763182397L;
      final float loadFactor;

      Segment(float var1) {
         this.loadFactor = var1;
      }
   }

   static class Node<K, V> implements Map.Entry<K, V> {
      final int hash;
      final K key;
      volatile V val;
      volatile ConcurrentHashMap.Node<K, V> next;

      Node(int var1, K var2, V var3, ConcurrentHashMap.Node<K, V> var4) {
         this.hash = var1;
         this.key = var2;
         this.val = var3;
         this.next = var4;
      }

      public final K getKey() {
         return this.key;
      }

      public final V getValue() {
         return this.val;
      }

      public final int hashCode() {
         return this.key.hashCode() ^ this.val.hashCode();
      }

      public final String toString() {
         return this.key + "=" + this.val;
      }

      public final V setValue(V var1) {
         throw new UnsupportedOperationException();
      }

      public final boolean equals(Object var1) {
         Object var2;
         Object var3;
         Object var4;
         Map.Entry var5;
         return var1 instanceof Map.Entry && (var2 = (var5 = (Map.Entry)var1).getKey()) != null && (var3 = var5.getValue()) != null && (var2 == this.key || var2.equals(this.key)) && (var3 == (var4 = this.val) || var3.equals(var4));
      }

      ConcurrentHashMap.Node<K, V> find(int var1, Object var2) {
         ConcurrentHashMap.Node var3 = this;
         if (var2 != null) {
            do {
               Object var4;
               if (var3.hash == var1 && ((var4 = var3.key) == var2 || var4 != null && var2.equals(var4))) {
                  return var3;
               }
            } while((var3 = var3.next) != null);
         }

         return null;
      }
   }
}
