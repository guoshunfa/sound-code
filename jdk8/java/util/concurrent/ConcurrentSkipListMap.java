package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import sun.misc.Unsafe;

public class ConcurrentSkipListMap<K, V> extends AbstractMap<K, V> implements ConcurrentNavigableMap<K, V>, Cloneable, Serializable {
   private static final long serialVersionUID = -8627078645895051609L;
   private static final Object BASE_HEADER = new Object();
   private transient volatile ConcurrentSkipListMap.HeadIndex<K, V> head;
   final Comparator<? super K> comparator;
   private transient ConcurrentSkipListMap.KeySet<K> keySet;
   private transient ConcurrentSkipListMap.EntrySet<K, V> entrySet;
   private transient ConcurrentSkipListMap.Values<V> values;
   private transient ConcurrentNavigableMap<K, V> descendingMap;
   private static final int EQ = 1;
   private static final int LT = 2;
   private static final int GT = 0;
   private static final Unsafe UNSAFE;
   private static final long headOffset;
   private static final long SECONDARY;

   private void initialize() {
      this.keySet = null;
      this.entrySet = null;
      this.values = null;
      this.descendingMap = null;
      this.head = new ConcurrentSkipListMap.HeadIndex(new ConcurrentSkipListMap.Node((Object)null, BASE_HEADER, (ConcurrentSkipListMap.Node)null), (ConcurrentSkipListMap.Index)null, (ConcurrentSkipListMap.Index)null, 1);
   }

   private boolean casHead(ConcurrentSkipListMap.HeadIndex<K, V> var1, ConcurrentSkipListMap.HeadIndex<K, V> var2) {
      return UNSAFE.compareAndSwapObject(this, headOffset, var1, var2);
   }

   static final int cpr(Comparator var0, Object var1, Object var2) {
      return var0 != null ? var0.compare(var1, var2) : ((Comparable)var1).compareTo(var2);
   }

   private ConcurrentSkipListMap.Node<K, V> findPredecessor(Object var1, Comparator<? super K> var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         label33:
         while(true) {
            Object var3 = this.head;
            ConcurrentSkipListMap.Index var4 = ((ConcurrentSkipListMap.Index)var3).right;

            while(true) {
               while(true) {
                  if (var4 != null) {
                     ConcurrentSkipListMap.Node var6 = var4.node;
                     Object var7 = var6.key;
                     if (var6.value == null) {
                        if (!((ConcurrentSkipListMap.Index)var3).unlink(var4)) {
                           continue label33;
                        }

                        var4 = ((ConcurrentSkipListMap.Index)var3).right;
                        continue;
                     }

                     if (cpr(var2, var1, var7) > 0) {
                        var3 = var4;
                        var4 = var4.right;
                        continue;
                     }
                  }

                  ConcurrentSkipListMap.Index var5;
                  if ((var5 = ((ConcurrentSkipListMap.Index)var3).down) == null) {
                     return ((ConcurrentSkipListMap.Index)var3).node;
                  }

                  var3 = var5;
                  var4 = var5.right;
               }
            }
         }
      }
   }

   private ConcurrentSkipListMap.Node<K, V> findNode(Object var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         Comparator var2 = this.comparator;

         label43:
         while(true) {
            ConcurrentSkipListMap.Node var3 = this.findPredecessor(var1, var2);

            ConcurrentSkipListMap.Node var7;
            for(ConcurrentSkipListMap.Node var4 = var3.next; var4 != null; var4 = var7) {
               var7 = var4.next;
               if (var4 != var3.next) {
                  continue label43;
               }

               Object var5;
               if ((var5 = var4.value) == null) {
                  var4.helpDelete(var3, var7);
                  continue label43;
               }

               if (var3.value == null || var5 == var4) {
                  continue label43;
               }

               int var6;
               if ((var6 = cpr(var2, var1, var4.key)) == 0) {
                  return var4;
               }

               if (var6 < 0) {
                  return null;
               }

               var3 = var4;
            }

            return null;
         }
      }
   }

   private V doGet(Object var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         Comparator var2 = this.comparator;

         label43:
         while(true) {
            ConcurrentSkipListMap.Node var3 = this.findPredecessor(var1, var2);

            ConcurrentSkipListMap.Node var7;
            for(ConcurrentSkipListMap.Node var4 = var3.next; var4 != null; var4 = var7) {
               var7 = var4.next;
               if (var4 != var3.next) {
                  continue label43;
               }

               Object var5;
               if ((var5 = var4.value) == null) {
                  var4.helpDelete(var3, var7);
                  continue label43;
               }

               if (var3.value == null || var5 == var4) {
                  continue label43;
               }

               int var6;
               if ((var6 = cpr(var2, var1, var4.key)) == 0) {
                  return var5;
               }

               if (var6 < 0) {
                  return null;
               }

               var3 = var4;
            }

            return null;
         }
      }
   }

   private V doPut(K var1, V var2, boolean var3) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         Comparator var5 = this.comparator;

         ConcurrentSkipListMap.Node var7;
         Object var8;
         label158:
         do {
            while(true) {
               ConcurrentSkipListMap.Node var6 = this.findPredecessor(var1, var5);
               var7 = var6.next;

               while(true) {
                  if (var7 != null) {
                     ConcurrentSkipListMap.Node var10 = var7.next;
                     if (var7 != var6.next) {
                        break;
                     }

                     if ((var8 = var7.value) == null) {
                        var7.helpDelete(var6, var10);
                        break;
                     }

                     if (var6.value == null || var8 == var7) {
                        break;
                     }

                     int var9;
                     if ((var9 = cpr(var5, var1, var7.key)) > 0) {
                        var6 = var7;
                        var7 = var10;
                        continue;
                     }

                     if (var9 == 0) {
                        continue label158;
                     }
                  }

                  ConcurrentSkipListMap.Node var4 = new ConcurrentSkipListMap.Node(var1, var2, var7);
                  if (var6.casNext(var7, var4)) {
                     int var18 = ThreadLocalRandom.nextSecondarySeed();
                     if ((var18 & -2147483647) == 0) {
                        int var19;
                        for(var19 = 1; ((var18 >>>= 1) & 1) != 0; ++var19) {
                        }

                        ConcurrentSkipListMap.Index var21 = null;
                        ConcurrentSkipListMap.HeadIndex var22 = this.head;
                        int var12;
                        int var20;
                        int var23;
                        if (var19 <= (var20 = var22.level)) {
                           for(var23 = 1; var23 <= var19; ++var23) {
                              var21 = new ConcurrentSkipListMap.Index(var4, var21, (ConcurrentSkipListMap.Index)null);
                           }
                        } else {
                           var19 = var20 + 1;
                           ConcurrentSkipListMap.Index[] var11 = (ConcurrentSkipListMap.Index[])(new ConcurrentSkipListMap.Index[var19 + 1]);

                           for(var12 = 1; var12 <= var19; ++var12) {
                              var11[var12] = var21 = new ConcurrentSkipListMap.Index(var4, var21, (ConcurrentSkipListMap.Index)null);
                           }

                           while(true) {
                              var22 = this.head;
                              var12 = var22.level;
                              if (var19 <= var12) {
                                 break;
                              }

                              ConcurrentSkipListMap.HeadIndex var13 = var22;
                              ConcurrentSkipListMap.Node var14 = var22.node;

                              for(int var15 = var12 + 1; var15 <= var19; ++var15) {
                                 var13 = new ConcurrentSkipListMap.HeadIndex(var14, var13, var11[var15], var15);
                              }

                              if (this.casHead(var22, var13)) {
                                 var22 = var13;
                                 var19 = var12;
                                 var21 = var11[var12];
                                 break;
                              }
                           }
                        }

                        var23 = var19;

                        label100:
                        while(true) {
                           var12 = var22.level;
                           Object var24 = var22;
                           ConcurrentSkipListMap.Index var25 = var22.right;
                           ConcurrentSkipListMap.Index var26 = var21;

                           while(true) {
                              while(var24 != null && var26 != null) {
                                 if (var25 != null) {
                                    ConcurrentSkipListMap.Node var16 = var25.node;
                                    int var17 = cpr(var5, var1, var16.key);
                                    if (var16.value == null) {
                                       if (!((ConcurrentSkipListMap.Index)var24).unlink(var25)) {
                                          continue label100;
                                       }

                                       var25 = ((ConcurrentSkipListMap.Index)var24).right;
                                       continue;
                                    }

                                    if (var17 > 0) {
                                       var24 = var25;
                                       var25 = var25.right;
                                       continue;
                                    }
                                 }

                                 if (var12 == var23) {
                                    if (!((ConcurrentSkipListMap.Index)var24).link(var25, var26)) {
                                       continue label100;
                                    }

                                    if (var26.node.value == null) {
                                       this.findNode(var1);
                                       return null;
                                    }

                                    --var23;
                                    if (var23 == 0) {
                                       return null;
                                    }
                                 }

                                 --var12;
                                 if (var12 >= var23 && var12 < var19) {
                                    var26 = var26.down;
                                 }

                                 var24 = ((ConcurrentSkipListMap.Index)var24).down;
                                 var25 = ((ConcurrentSkipListMap.Index)var24).right;
                              }

                              return null;
                           }
                        }
                     }

                     return null;
                  }
                  break;
               }
            }
         } while(!var3 && !var7.casValue(var8, var2));

         return var8;
      }
   }

   final V doRemove(Object var1, Object var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         Comparator var3 = this.comparator;

         label63:
         while(true) {
            ConcurrentSkipListMap.Node var4 = this.findPredecessor(var1, var3);

            ConcurrentSkipListMap.Node var8;
            for(ConcurrentSkipListMap.Node var5 = var4.next; var5 != null; var5 = var8) {
               var8 = var5.next;
               if (var5 != var4.next) {
                  continue label63;
               }

               Object var6;
               if ((var6 = var5.value) == null) {
                  var5.helpDelete(var4, var8);
                  continue label63;
               }

               if (var4.value == null || var6 == var5) {
                  continue label63;
               }

               int var7;
               if ((var7 = cpr(var3, var1, var5.key)) < 0) {
                  return null;
               }

               if (var7 <= 0) {
                  if (var2 != null && !var2.equals(var6)) {
                     return null;
                  }

                  if (var5.casValue(var6, (Object)null)) {
                     if (var5.appendMarker(var8) && var4.casNext(var5, var8)) {
                        this.findPredecessor(var1, var3);
                        if (this.head.right == null) {
                           this.tryReduceLevel();
                        }
                     } else {
                        this.findNode(var1);
                     }

                     return var6;
                  }
                  continue label63;
               }

               var4 = var5;
            }

            return null;
         }
      }
   }

   private void tryReduceLevel() {
      ConcurrentSkipListMap.HeadIndex var1 = this.head;
      ConcurrentSkipListMap.HeadIndex var2;
      ConcurrentSkipListMap.HeadIndex var3;
      if (var1.level > 3 && (var2 = (ConcurrentSkipListMap.HeadIndex)var1.down) != null && (var3 = (ConcurrentSkipListMap.HeadIndex)var2.down) != null && var3.right == null && var2.right == null && var1.right == null && this.casHead(var1, var2) && var1.right != null) {
         this.casHead(var2, var1);
      }

   }

   final ConcurrentSkipListMap.Node<K, V> findFirst() {
      ConcurrentSkipListMap.Node var1;
      ConcurrentSkipListMap.Node var2;
      while((var2 = (var1 = this.head.node).next) != null) {
         if (var2.value != null) {
            return var2;
         }

         var2.helpDelete(var1, var2.next);
      }

      return null;
   }

   private Map.Entry<K, V> doRemoveFirstEntry() {
      ConcurrentSkipListMap.Node var1;
      ConcurrentSkipListMap.Node var2;
      while((var2 = (var1 = this.head.node).next) != null) {
         ConcurrentSkipListMap.Node var3 = var2.next;
         if (var2 == var1.next) {
            Object var4 = var2.value;
            if (var4 == null) {
               var2.helpDelete(var1, var3);
            } else if (var2.casValue(var4, (Object)null)) {
               if (!var2.appendMarker(var3) || !var1.casNext(var2, var3)) {
                  this.findFirst();
               }

               this.clearIndexToFirst();
               return new AbstractMap.SimpleImmutableEntry(var2.key, var4);
            }
         }
      }

      return null;
   }

   private void clearIndexToFirst() {
      label24:
      while(true) {
         Object var1 = this.head;

         do {
            ConcurrentSkipListMap.Index var2 = ((ConcurrentSkipListMap.Index)var1).right;
            if (var2 != null && var2.indexesDeletedNode() && !((ConcurrentSkipListMap.Index)var1).unlink(var2)) {
               continue label24;
            }
         } while((var1 = ((ConcurrentSkipListMap.Index)var1).down) != null);

         if (this.head.right == null) {
            this.tryReduceLevel();
         }

         return;
      }
   }

   private Map.Entry<K, V> doRemoveLastEntry() {
      while(true) {
         ConcurrentSkipListMap.Node var1 = this.findPredecessorOfLast();
         ConcurrentSkipListMap.Node var2 = var1.next;
         if (var2 == null) {
            if (var1.isBaseHeader()) {
               return null;
            }
         } else {
            while(true) {
               ConcurrentSkipListMap.Node var3 = var2.next;
               if (var2 != var1.next) {
                  break;
               }

               Object var4 = var2.value;
               if (var4 == null) {
                  var2.helpDelete(var1, var3);
                  break;
               }

               if (var1.value == null || var4 == var2) {
                  break;
               }

               if (var3 == null) {
                  if (!var2.casValue(var4, (Object)null)) {
                     break;
                  }

                  Object var5 = var2.key;
                  if (var2.appendMarker(var3) && var1.casNext(var2, var3)) {
                     this.findPredecessor(var5, this.comparator);
                     if (this.head.right == null) {
                        this.tryReduceLevel();
                     }
                  } else {
                     this.findNode(var5);
                  }

                  return new AbstractMap.SimpleImmutableEntry(var5, var4);
               }

               var1 = var2;
               var2 = var3;
            }
         }
      }
   }

   final ConcurrentSkipListMap.Node<K, V> findLast() {
      Object var1 = this.head;

      while(true) {
         ConcurrentSkipListMap.Index var3;
         while((var3 = ((ConcurrentSkipListMap.Index)var1).right) == null) {
            ConcurrentSkipListMap.Index var2;
            if ((var2 = ((ConcurrentSkipListMap.Index)var1).down) != null) {
               var1 = var2;
            } else {
               ConcurrentSkipListMap.Node var4 = ((ConcurrentSkipListMap.Index)var1).node;
               ConcurrentSkipListMap.Node var5 = var4.next;

               while(true) {
                  if (var5 == null) {
                     return var4.isBaseHeader() ? null : var4;
                  }

                  ConcurrentSkipListMap.Node var6 = var5.next;
                  if (var5 != var4.next) {
                     break;
                  }

                  Object var7 = var5.value;
                  if (var7 == null) {
                     var5.helpDelete(var4, var6);
                     break;
                  }

                  if (var4.value == null || var7 == var5) {
                     break;
                  }

                  var4 = var5;
                  var5 = var6;
               }

               var1 = this.head;
            }
         }

         if (var3.indexesDeletedNode()) {
            ((ConcurrentSkipListMap.Index)var1).unlink(var3);
            var1 = this.head;
         } else {
            var1 = var3;
         }
      }
   }

   private ConcurrentSkipListMap.Node<K, V> findPredecessorOfLast() {
      label25:
      while(true) {
         Object var1 = this.head;

         while(true) {
            while(true) {
               ConcurrentSkipListMap.Index var3;
               if ((var3 = ((ConcurrentSkipListMap.Index)var1).right) != null) {
                  if (var3.indexesDeletedNode()) {
                     ((ConcurrentSkipListMap.Index)var1).unlink(var3);
                     continue label25;
                  }

                  if (var3.node.next != null) {
                     var1 = var3;
                     continue;
                  }
               }

               ConcurrentSkipListMap.Index var2;
               if ((var2 = ((ConcurrentSkipListMap.Index)var1).down) == null) {
                  return ((ConcurrentSkipListMap.Index)var1).node;
               }

               var1 = var2;
            }
         }
      }
   }

   final ConcurrentSkipListMap.Node<K, V> findNear(K var1, int var2, Comparator<? super K> var3) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         label62:
         while(true) {
            ConcurrentSkipListMap.Node var4 = this.findPredecessor(var1, var3);

            ConcurrentSkipListMap.Node var7;
            for(ConcurrentSkipListMap.Node var5 = var4.next; var5 != null; var5 = var7) {
               var7 = var5.next;
               if (var5 != var4.next) {
                  continue label62;
               }

               Object var6;
               if ((var6 = var5.value) == null) {
                  var5.helpDelete(var4, var7);
                  continue label62;
               }

               if (var4.value == null || var6 == var5) {
                  continue label62;
               }

               int var8 = cpr(var3, var1, var5.key);
               if (var8 == 0 && (var2 & 1) != 0 || var8 < 0 && (var2 & 2) == 0) {
                  return var5;
               }

               if (var8 <= 0 && (var2 & 2) != 0) {
                  return var4.isBaseHeader() ? null : var4;
               }

               var4 = var5;
            }

            return (var2 & 2) != 0 && !var4.isBaseHeader() ? var4 : null;
         }
      }
   }

   final AbstractMap.SimpleImmutableEntry<K, V> getNear(K var1, int var2) {
      Comparator var3 = this.comparator;

      AbstractMap.SimpleImmutableEntry var5;
      do {
         ConcurrentSkipListMap.Node var4 = this.findNear(var1, var2, var3);
         if (var4 == null) {
            return null;
         }

         var5 = var4.createSnapshot();
      } while(var5 == null);

      return var5;
   }

   public ConcurrentSkipListMap() {
      this.comparator = null;
      this.initialize();
   }

   public ConcurrentSkipListMap(Comparator<? super K> var1) {
      this.comparator = var1;
      this.initialize();
   }

   public ConcurrentSkipListMap(Map<? extends K, ? extends V> var1) {
      this.comparator = null;
      this.initialize();
      this.putAll(var1);
   }

   public ConcurrentSkipListMap(SortedMap<K, ? extends V> var1) {
      this.comparator = var1.comparator();
      this.initialize();
      this.buildFromSorted(var1);
   }

   public ConcurrentSkipListMap<K, V> clone() {
      try {
         ConcurrentSkipListMap var1 = (ConcurrentSkipListMap)super.clone();
         var1.initialize();
         var1.buildFromSorted(this);
         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError();
      }
   }

   private void buildFromSorted(SortedMap<K, ? extends V> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         ConcurrentSkipListMap.HeadIndex var2 = this.head;
         ConcurrentSkipListMap.Node var3 = var2.node;
         ArrayList var4 = new ArrayList();

         for(int var5 = 0; var5 <= var2.level; ++var5) {
            var4.add((Object)null);
         }

         Object var15 = var2;

         for(int var6 = var2.level; var6 > 0; --var6) {
            var4.set(var6, var15);
            var15 = ((ConcurrentSkipListMap.Index)var15).down;
         }

         Iterator var16 = var1.entrySet().iterator();

         while(var16.hasNext()) {
            Map.Entry var7 = (Map.Entry)var16.next();
            int var8 = ThreadLocalRandom.current().nextInt();
            int var9 = 0;
            if ((var8 & -2147483647) == 0) {
               do {
                  ++var9;
               } while(((var8 >>>= 1) & 1) != 0);

               if (var9 > var2.level) {
                  var9 = var2.level + 1;
               }
            }

            Object var10 = var7.getKey();
            Object var11 = var7.getValue();
            if (var10 == null || var11 == null) {
               throw new NullPointerException();
            }

            ConcurrentSkipListMap.Node var12 = new ConcurrentSkipListMap.Node(var10, var11, (ConcurrentSkipListMap.Node)null);
            var3.next = var12;
            var3 = var12;
            if (var9 > 0) {
               ConcurrentSkipListMap.Index var13 = null;

               for(int var14 = 1; var14 <= var9; ++var14) {
                  var13 = new ConcurrentSkipListMap.Index(var12, var13, (ConcurrentSkipListMap.Index)null);
                  if (var14 > var2.level) {
                     var2 = new ConcurrentSkipListMap.HeadIndex(var2.node, var2, var13, var14);
                  }

                  if (var14 < var4.size()) {
                     ((ConcurrentSkipListMap.Index)var4.get(var14)).right = var13;
                     var4.set(var14, var13);
                  } else {
                     var4.add(var13);
                  }
               }
            }
         }

         this.head = var2;
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(ConcurrentSkipListMap.Node var2 = this.findFirst(); var2 != null; var2 = var2.next) {
         Object var3 = var2.getValidValue();
         if (var3 != null) {
            var1.writeObject(var2.key);
            var1.writeObject(var3);
         }
      }

      var1.writeObject((Object)null);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.initialize();
      ConcurrentSkipListMap.HeadIndex var2 = this.head;
      ConcurrentSkipListMap.Node var3 = var2.node;
      ArrayList var4 = new ArrayList();

      for(int var5 = 0; var5 <= var2.level; ++var5) {
         var4.add((Object)null);
      }

      Object var15 = var2;

      for(int var6 = var2.level; var6 > 0; --var6) {
         var4.set(var6, var15);
         var15 = ((ConcurrentSkipListMap.Index)var15).down;
      }

      while(true) {
         Object var16 = var1.readObject();
         if (var16 == null) {
            this.head = var2;
            return;
         }

         Object var7 = var1.readObject();
         if (var7 == null) {
            throw new NullPointerException();
         }

         int var10 = ThreadLocalRandom.current().nextInt();
         int var11 = 0;
         if ((var10 & -2147483647) == 0) {
            do {
               ++var11;
            } while(((var10 >>>= 1) & 1) != 0);

            if (var11 > var2.level) {
               var11 = var2.level + 1;
            }
         }

         ConcurrentSkipListMap.Node var12 = new ConcurrentSkipListMap.Node(var16, var7, (ConcurrentSkipListMap.Node)null);
         var3.next = var12;
         var3 = var12;
         if (var11 > 0) {
            ConcurrentSkipListMap.Index var13 = null;

            for(int var14 = 1; var14 <= var11; ++var14) {
               var13 = new ConcurrentSkipListMap.Index(var12, var13, (ConcurrentSkipListMap.Index)null);
               if (var14 > var2.level) {
                  var2 = new ConcurrentSkipListMap.HeadIndex(var2.node, var2, var13, var14);
               }

               if (var14 < var4.size()) {
                  ((ConcurrentSkipListMap.Index)var4.get(var14)).right = var13;
                  var4.set(var14, var13);
               } else {
                  var4.add(var13);
               }
            }
         }
      }
   }

   public boolean containsKey(Object var1) {
      return this.doGet(var1) != null;
   }

   public V get(Object var1) {
      return this.doGet(var1);
   }

   public V getOrDefault(Object var1, V var2) {
      Object var3;
      return (var3 = this.doGet(var1)) == null ? var2 : var3;
   }

   public V put(K var1, V var2) {
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         return this.doPut(var1, var2, false);
      }
   }

   public V remove(Object var1) {
      return this.doRemove(var1, (Object)null);
   }

   public boolean containsValue(Object var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         for(ConcurrentSkipListMap.Node var2 = this.findFirst(); var2 != null; var2 = var2.next) {
            Object var3 = var2.getValidValue();
            if (var3 != null && var1.equals(var3)) {
               return true;
            }
         }

         return false;
      }
   }

   public int size() {
      long var1 = 0L;

      for(ConcurrentSkipListMap.Node var3 = this.findFirst(); var3 != null; var3 = var3.next) {
         if (var3.getValidValue() != null) {
            ++var1;
         }
      }

      return var1 >= 2147483647L ? Integer.MAX_VALUE : (int)var1;
   }

   public boolean isEmpty() {
      return this.findFirst() == null;
   }

   public void clear() {
      while(true) {
         ConcurrentSkipListMap.HeadIndex var3 = this.head;
         ConcurrentSkipListMap.HeadIndex var4 = (ConcurrentSkipListMap.HeadIndex)var3.down;
         if (var4 != null) {
            this.casHead(var3, var4);
         } else {
            ConcurrentSkipListMap.Node var1;
            ConcurrentSkipListMap.Node var2;
            if ((var1 = var3.node) == null || (var2 = var1.next) == null) {
               return;
            }

            ConcurrentSkipListMap.Node var5 = var2.next;
            if (var2 == var1.next) {
               Object var6 = var2.value;
               if (var6 == null) {
                  var2.helpDelete(var1, var5);
               } else if (var2.casValue(var6, (Object)null) && var2.appendMarker(var5)) {
                  var1.casNext(var2, var5);
               }
            }
         }
      }
   }

   public V computeIfAbsent(K var1, Function<? super K, ? extends V> var2) {
      if (var1 != null && var2 != null) {
         Object var3;
         Object var5;
         if ((var3 = this.doGet(var1)) == null && (var5 = var2.apply(var1)) != null) {
            Object var4;
            var3 = (var4 = this.doPut(var1, var5, true)) == null ? var5 : var4;
         }

         return var3;
      } else {
         throw new NullPointerException();
      }
   }

   public V computeIfPresent(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
      if (var1 != null && var2 != null) {
         ConcurrentSkipListMap.Node var3;
         while((var3 = this.findNode(var1)) != null) {
            Object var4;
            if ((var4 = var3.value) != null) {
               Object var6 = var2.apply(var1, var4);
               if (var6 != null) {
                  if (var3.casValue(var4, var6)) {
                     return var6;
                  }
               } else if (this.doRemove(var1, var4) != null) {
                  break;
               }
            }
         }

         return null;
      } else {
         throw new NullPointerException();
      }
   }

   public V compute(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
      if (var1 != null && var2 != null) {
         while(true) {
            ConcurrentSkipListMap.Node var3;
            Object var5;
            if ((var3 = this.findNode(var1)) == null) {
               if ((var5 = var2.apply(var1, (Object)null)) == null) {
                  break;
               }

               if (this.doPut(var1, var5, true) == null) {
                  return var5;
               }
            } else {
               Object var4;
               if ((var4 = var3.value) != null) {
                  if ((var5 = var2.apply(var1, var4)) != null) {
                     if (var3.casValue(var4, var5)) {
                        return var5;
                     }
                  } else if (this.doRemove(var1, var4) != null) {
                     break;
                  }
               }
            }
         }

         return null;
      } else {
         throw new NullPointerException();
      }
   }

   public V merge(K var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
      if (var1 != null && var2 != null && var3 != null) {
         ConcurrentSkipListMap.Node var4;
         do {
            while((var4 = this.findNode(var1)) != null) {
               Object var5;
               if ((var5 = var4.value) != null) {
                  Object var6;
                  if ((var6 = var3.apply(var5, var2)) != null) {
                     if (var4.casValue(var5, var6)) {
                        return var6;
                     }
                  } else if (this.doRemove(var1, var5) != null) {
                     return null;
                  }
               }
            }
         } while(this.doPut(var1, var2, true) != null);

         return var2;
      } else {
         throw new NullPointerException();
      }
   }

   public NavigableSet<K> keySet() {
      ConcurrentSkipListMap.KeySet var1 = this.keySet;
      return var1 != null ? var1 : (this.keySet = new ConcurrentSkipListMap.KeySet(this));
   }

   public NavigableSet<K> navigableKeySet() {
      ConcurrentSkipListMap.KeySet var1 = this.keySet;
      return var1 != null ? var1 : (this.keySet = new ConcurrentSkipListMap.KeySet(this));
   }

   public Collection<V> values() {
      ConcurrentSkipListMap.Values var1 = this.values;
      return var1 != null ? var1 : (this.values = new ConcurrentSkipListMap.Values(this));
   }

   public Set<Map.Entry<K, V>> entrySet() {
      ConcurrentSkipListMap.EntrySet var1 = this.entrySet;
      return var1 != null ? var1 : (this.entrySet = new ConcurrentSkipListMap.EntrySet(this));
   }

   public ConcurrentNavigableMap<K, V> descendingMap() {
      ConcurrentNavigableMap var1 = this.descendingMap;
      return var1 != null ? var1 : (this.descendingMap = new ConcurrentSkipListMap.SubMap(this, (Object)null, false, (Object)null, false, true));
   }

   public NavigableSet<K> descendingKeySet() {
      return this.descendingMap().navigableKeySet();
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Map)) {
         return false;
      } else {
         Map var2 = (Map)var1;

         try {
            Iterator var3 = this.entrySet().iterator();

            Map.Entry var4;
            while(var3.hasNext()) {
               var4 = (Map.Entry)var3.next();
               if (!var4.getValue().equals(var2.get(var4.getKey()))) {
                  return false;
               }
            }

            var3 = var2.entrySet().iterator();

            Object var5;
            Object var6;
            do {
               if (!var3.hasNext()) {
                  return true;
               }

               var4 = (Map.Entry)var3.next();
               var5 = var4.getKey();
               var6 = var4.getValue();
            } while(var5 != null && var6 != null && var6.equals(this.get(var5)));

            return false;
         } catch (ClassCastException var7) {
            return false;
         } catch (NullPointerException var8) {
            return false;
         }
      }
   }

   public V putIfAbsent(K var1, V var2) {
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         return this.doPut(var1, var2, true);
      }
   }

   public boolean remove(Object var1, Object var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return var2 != null && this.doRemove(var1, var2) != null;
      }
   }

   public boolean replace(K var1, V var2, V var3) {
      if (var1 != null && var2 != null && var3 != null) {
         ConcurrentSkipListMap.Node var4;
         while((var4 = this.findNode(var1)) != null) {
            Object var5;
            if ((var5 = var4.value) != null) {
               if (!var2.equals(var5)) {
                  return false;
               }

               if (var4.casValue(var5, var3)) {
                  return true;
               }
            }
         }

         return false;
      } else {
         throw new NullPointerException();
      }
   }

   public V replace(K var1, V var2) {
      if (var1 != null && var2 != null) {
         ConcurrentSkipListMap.Node var3;
         Object var4;
         do {
            if ((var3 = this.findNode(var1)) == null) {
               return null;
            }
         } while((var4 = var3.value) == null || !var3.casValue(var4, var2));

         return var4;
      } else {
         throw new NullPointerException();
      }
   }

   public Comparator<? super K> comparator() {
      return this.comparator;
   }

   public K firstKey() {
      ConcurrentSkipListMap.Node var1 = this.findFirst();
      if (var1 == null) {
         throw new NoSuchElementException();
      } else {
         return var1.key;
      }
   }

   public K lastKey() {
      ConcurrentSkipListMap.Node var1 = this.findLast();
      if (var1 == null) {
         throw new NoSuchElementException();
      } else {
         return var1.key;
      }
   }

   public ConcurrentNavigableMap<K, V> subMap(K var1, boolean var2, K var3, boolean var4) {
      if (var1 != null && var3 != null) {
         return new ConcurrentSkipListMap.SubMap(this, var1, var2, var3, var4, false);
      } else {
         throw new NullPointerException();
      }
   }

   public ConcurrentNavigableMap<K, V> headMap(K var1, boolean var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return new ConcurrentSkipListMap.SubMap(this, (Object)null, false, var1, var2, false);
      }
   }

   public ConcurrentNavigableMap<K, V> tailMap(K var1, boolean var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return new ConcurrentSkipListMap.SubMap(this, var1, var2, (Object)null, false, false);
      }
   }

   public ConcurrentNavigableMap<K, V> subMap(K var1, K var2) {
      return this.subMap(var1, true, var2, false);
   }

   public ConcurrentNavigableMap<K, V> headMap(K var1) {
      return this.headMap(var1, false);
   }

   public ConcurrentNavigableMap<K, V> tailMap(K var1) {
      return this.tailMap(var1, true);
   }

   public Map.Entry<K, V> lowerEntry(K var1) {
      return this.getNear(var1, 2);
   }

   public K lowerKey(K var1) {
      ConcurrentSkipListMap.Node var2 = this.findNear(var1, 2, this.comparator);
      return var2 == null ? null : var2.key;
   }

   public Map.Entry<K, V> floorEntry(K var1) {
      return this.getNear(var1, 3);
   }

   public K floorKey(K var1) {
      ConcurrentSkipListMap.Node var2 = this.findNear(var1, 3, this.comparator);
      return var2 == null ? null : var2.key;
   }

   public Map.Entry<K, V> ceilingEntry(K var1) {
      return this.getNear(var1, 1);
   }

   public K ceilingKey(K var1) {
      ConcurrentSkipListMap.Node var2 = this.findNear(var1, 1, this.comparator);
      return var2 == null ? null : var2.key;
   }

   public Map.Entry<K, V> higherEntry(K var1) {
      return this.getNear(var1, 0);
   }

   public K higherKey(K var1) {
      ConcurrentSkipListMap.Node var2 = this.findNear(var1, 0, this.comparator);
      return var2 == null ? null : var2.key;
   }

   public Map.Entry<K, V> firstEntry() {
      AbstractMap.SimpleImmutableEntry var2;
      do {
         ConcurrentSkipListMap.Node var1 = this.findFirst();
         if (var1 == null) {
            return null;
         }

         var2 = var1.createSnapshot();
      } while(var2 == null);

      return var2;
   }

   public Map.Entry<K, V> lastEntry() {
      AbstractMap.SimpleImmutableEntry var2;
      do {
         ConcurrentSkipListMap.Node var1 = this.findLast();
         if (var1 == null) {
            return null;
         }

         var2 = var1.createSnapshot();
      } while(var2 == null);

      return var2;
   }

   public Map.Entry<K, V> pollFirstEntry() {
      return this.doRemoveFirstEntry();
   }

   public Map.Entry<K, V> pollLastEntry() {
      return this.doRemoveLastEntry();
   }

   Iterator<K> keyIterator() {
      return new ConcurrentSkipListMap.KeyIterator();
   }

   Iterator<V> valueIterator() {
      return new ConcurrentSkipListMap.ValueIterator();
   }

   Iterator<Map.Entry<K, V>> entryIterator() {
      return new ConcurrentSkipListMap.EntryIterator();
   }

   static final <E> List<E> toList(Collection<E> var0) {
      ArrayList var1 = new ArrayList();
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         Object var3 = var2.next();
         var1.add(var3);
      }

      return var1;
   }

   public void forEach(BiConsumer<? super K, ? super V> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         for(ConcurrentSkipListMap.Node var3 = this.findFirst(); var3 != null; var3 = var3.next) {
            Object var2;
            if ((var2 = var3.getValidValue()) != null) {
               var1.accept(var3.key, var2);
            }
         }

      }
   }

   public void replaceAll(BiFunction<? super K, ? super V, ? extends V> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         Object var2;
         for(ConcurrentSkipListMap.Node var3 = this.findFirst(); var3 != null; var3 = var3.next) {
            while((var2 = var3.getValidValue()) != null) {
               Object var4 = var1.apply(var3.key, var2);
               if (var4 == null) {
                  throw new NullPointerException();
               }

               if (var3.casValue(var2, var4)) {
                  break;
               }
            }
         }

      }
   }

   final ConcurrentSkipListMap.KeySpliterator<K, V> keySpliterator() {
      Comparator var1 = this.comparator;

      while(true) {
         ConcurrentSkipListMap.HeadIndex var2;
         ConcurrentSkipListMap.Node var4 = (var2 = this.head).node;
         ConcurrentSkipListMap.Node var3;
         if ((var3 = var4.next) == null || var3.value != null) {
            return new ConcurrentSkipListMap.KeySpliterator(var1, var2, var3, (Object)null, var3 == null ? 0 : Integer.MAX_VALUE);
         }

         var3.helpDelete(var4, var3.next);
      }
   }

   final ConcurrentSkipListMap.ValueSpliterator<K, V> valueSpliterator() {
      Comparator var1 = this.comparator;

      while(true) {
         ConcurrentSkipListMap.HeadIndex var2;
         ConcurrentSkipListMap.Node var4 = (var2 = this.head).node;
         ConcurrentSkipListMap.Node var3;
         if ((var3 = var4.next) == null || var3.value != null) {
            return new ConcurrentSkipListMap.ValueSpliterator(var1, var2, var3, (Object)null, var3 == null ? 0 : Integer.MAX_VALUE);
         }

         var3.helpDelete(var4, var3.next);
      }
   }

   final ConcurrentSkipListMap.EntrySpliterator<K, V> entrySpliterator() {
      Comparator var1 = this.comparator;

      while(true) {
         ConcurrentSkipListMap.HeadIndex var2;
         ConcurrentSkipListMap.Node var4 = (var2 = this.head).node;
         ConcurrentSkipListMap.Node var3;
         if ((var3 = var4.next) == null || var3.value != null) {
            return new ConcurrentSkipListMap.EntrySpliterator(var1, var2, var3, (Object)null, var3 == null ? 0 : Integer.MAX_VALUE);
         }

         var3.helpDelete(var4, var3.next);
      }
   }

   static {
      try {
         UNSAFE = Unsafe.getUnsafe();
         Class var0 = ConcurrentSkipListMap.class;
         headOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("head"));
         Class var1 = Thread.class;
         SECONDARY = UNSAFE.objectFieldOffset(var1.getDeclaredField("threadLocalRandomSecondarySeed"));
      } catch (Exception var2) {
         throw new Error(var2);
      }
   }

   static final class EntrySpliterator<K, V> extends ConcurrentSkipListMap.CSLMSpliterator<K, V> implements Spliterator<Map.Entry<K, V>> {
      EntrySpliterator(Comparator<? super K> var1, ConcurrentSkipListMap.Index<K, V> var2, ConcurrentSkipListMap.Node<K, V> var3, K var4, int var5) {
         super(var1, var2, var3, var4, var5);
      }

      public Spliterator<Map.Entry<K, V>> trySplit() {
         Comparator var3 = this.comparator;
         Object var4 = this.fence;
         ConcurrentSkipListMap.Node var1;
         Object var2;
         if ((var1 = this.current) != null && (var2 = var1.key) != null) {
            for(ConcurrentSkipListMap.Index var5 = this.row; var5 != null; var5 = this.row = var5.down) {
               ConcurrentSkipListMap.Index var6;
               ConcurrentSkipListMap.Node var7;
               ConcurrentSkipListMap.Node var8;
               Object var9;
               if ((var6 = var5.right) != null && (var7 = var6.node) != null && (var8 = var7.next) != null && var8.value != null && (var9 = var8.key) != null && ConcurrentSkipListMap.cpr(var3, var9, var2) > 0 && (var4 == null || ConcurrentSkipListMap.cpr(var3, var9, var4) < 0)) {
                  this.current = var8;
                  ConcurrentSkipListMap.Index var10 = var5.down;
                  this.row = var6.right != null ? var6 : var6.down;
                  this.est -= this.est >>> 2;
                  return new ConcurrentSkipListMap.EntrySpliterator(var3, var10, var1, var9, this.est);
               }
            }
         }

         return null;
      }

      public void forEachRemaining(Consumer<? super Map.Entry<K, V>> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            Comparator var2 = this.comparator;
            Object var3 = this.fence;
            ConcurrentSkipListMap.Node var4 = this.current;

            Object var5;
            for(this.current = null; var4 != null && ((var5 = var4.key) == null || var3 == null || ConcurrentSkipListMap.cpr(var2, var3, var5) > 0); var4 = var4.next) {
               Object var6;
               if ((var6 = var4.value) != null && var6 != var4) {
                  var1.accept(new AbstractMap.SimpleImmutableEntry(var5, var6));
               }
            }

         }
      }

      public boolean tryAdvance(Consumer<? super Map.Entry<K, V>> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            Comparator var2 = this.comparator;
            Object var3 = this.fence;

            ConcurrentSkipListMap.Node var4;
            for(var4 = this.current; var4 != null; var4 = var4.next) {
               Object var5;
               if ((var5 = var4.key) != null && var3 != null && ConcurrentSkipListMap.cpr(var2, var3, var5) <= 0) {
                  var4 = null;
                  break;
               }

               Object var6;
               if ((var6 = var4.value) != null && var6 != var4) {
                  this.current = var4.next;
                  var1.accept(new AbstractMap.SimpleImmutableEntry(var5, var6));
                  return true;
               }
            }

            this.current = var4;
            return false;
         }
      }

      public int characteristics() {
         return 4373;
      }

      public final Comparator<Map.Entry<K, V>> getComparator() {
         return this.comparator != null ? Map.Entry.comparingByKey(this.comparator) : (Comparator)((Serializable)((var0x, var1x) -> {
            Comparable var2 = (Comparable)var0x.getKey();
            return var2.compareTo(var1x.getKey());
         }));
      }

      // $FF: synthetic method
      private static Object $deserializeLambda$(SerializedLambda var0) {
         String var1 = var0.getImplMethodName();
         byte var2 = -1;
         switch(var1.hashCode()) {
         case 1620203517:
            if (var1.equals("lambda$getComparator$d5a01062$1")) {
               var2 = 0;
            }
         default:
            switch(var2) {
            case 0:
               if (var0.getImplMethodKind() == 6 && var0.getFunctionalInterfaceClass().equals("java/util/Comparator") && var0.getFunctionalInterfaceMethodName().equals("compare") && var0.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)I") && var0.getImplClass().equals("java/util/concurrent/ConcurrentSkipListMap$EntrySpliterator") && var0.getImplMethodSignature().equals("(Ljava/util/Map$Entry;Ljava/util/Map$Entry;)I")) {
                  return (var0x, var1x) -> {
                     Comparable var2 = (Comparable)var0x.getKey();
                     return var2.compareTo(var1x.getKey());
                  };
               }
            default:
               throw new IllegalArgumentException("Invalid lambda deserialization");
            }
         }
      }
   }

   static final class ValueSpliterator<K, V> extends ConcurrentSkipListMap.CSLMSpliterator<K, V> implements Spliterator<V> {
      ValueSpliterator(Comparator<? super K> var1, ConcurrentSkipListMap.Index<K, V> var2, ConcurrentSkipListMap.Node<K, V> var3, K var4, int var5) {
         super(var1, var2, var3, var4, var5);
      }

      public Spliterator<V> trySplit() {
         Comparator var3 = this.comparator;
         Object var4 = this.fence;
         ConcurrentSkipListMap.Node var1;
         Object var2;
         if ((var1 = this.current) != null && (var2 = var1.key) != null) {
            for(ConcurrentSkipListMap.Index var5 = this.row; var5 != null; var5 = this.row = var5.down) {
               ConcurrentSkipListMap.Index var6;
               ConcurrentSkipListMap.Node var7;
               ConcurrentSkipListMap.Node var8;
               Object var9;
               if ((var6 = var5.right) != null && (var7 = var6.node) != null && (var8 = var7.next) != null && var8.value != null && (var9 = var8.key) != null && ConcurrentSkipListMap.cpr(var3, var9, var2) > 0 && (var4 == null || ConcurrentSkipListMap.cpr(var3, var9, var4) < 0)) {
                  this.current = var8;
                  ConcurrentSkipListMap.Index var10 = var5.down;
                  this.row = var6.right != null ? var6 : var6.down;
                  this.est -= this.est >>> 2;
                  return new ConcurrentSkipListMap.ValueSpliterator(var3, var10, var1, var9, this.est);
               }
            }
         }

         return null;
      }

      public void forEachRemaining(Consumer<? super V> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            Comparator var2 = this.comparator;
            Object var3 = this.fence;
            ConcurrentSkipListMap.Node var4 = this.current;

            Object var5;
            for(this.current = null; var4 != null && ((var5 = var4.key) == null || var3 == null || ConcurrentSkipListMap.cpr(var2, var3, var5) > 0); var4 = var4.next) {
               Object var6;
               if ((var6 = var4.value) != null && var6 != var4) {
                  var1.accept(var6);
               }
            }

         }
      }

      public boolean tryAdvance(Consumer<? super V> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            Comparator var2 = this.comparator;
            Object var3 = this.fence;

            ConcurrentSkipListMap.Node var4;
            for(var4 = this.current; var4 != null; var4 = var4.next) {
               Object var5;
               if ((var5 = var4.key) != null && var3 != null && ConcurrentSkipListMap.cpr(var2, var3, var5) <= 0) {
                  var4 = null;
                  break;
               }

               Object var6;
               if ((var6 = var4.value) != null && var6 != var4) {
                  this.current = var4.next;
                  var1.accept(var6);
                  return true;
               }
            }

            this.current = var4;
            return false;
         }
      }

      public int characteristics() {
         return 4368;
      }
   }

   static final class KeySpliterator<K, V> extends ConcurrentSkipListMap.CSLMSpliterator<K, V> implements Spliterator<K> {
      KeySpliterator(Comparator<? super K> var1, ConcurrentSkipListMap.Index<K, V> var2, ConcurrentSkipListMap.Node<K, V> var3, K var4, int var5) {
         super(var1, var2, var3, var4, var5);
      }

      public Spliterator<K> trySplit() {
         Comparator var3 = this.comparator;
         Object var4 = this.fence;
         ConcurrentSkipListMap.Node var1;
         Object var2;
         if ((var1 = this.current) != null && (var2 = var1.key) != null) {
            for(ConcurrentSkipListMap.Index var5 = this.row; var5 != null; var5 = this.row = var5.down) {
               ConcurrentSkipListMap.Index var6;
               ConcurrentSkipListMap.Node var7;
               ConcurrentSkipListMap.Node var8;
               Object var9;
               if ((var6 = var5.right) != null && (var7 = var6.node) != null && (var8 = var7.next) != null && var8.value != null && (var9 = var8.key) != null && ConcurrentSkipListMap.cpr(var3, var9, var2) > 0 && (var4 == null || ConcurrentSkipListMap.cpr(var3, var9, var4) < 0)) {
                  this.current = var8;
                  ConcurrentSkipListMap.Index var10 = var5.down;
                  this.row = var6.right != null ? var6 : var6.down;
                  this.est -= this.est >>> 2;
                  return new ConcurrentSkipListMap.KeySpliterator(var3, var10, var1, var9, this.est);
               }
            }
         }

         return null;
      }

      public void forEachRemaining(Consumer<? super K> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            Comparator var2 = this.comparator;
            Object var3 = this.fence;
            ConcurrentSkipListMap.Node var4 = this.current;

            Object var5;
            for(this.current = null; var4 != null && ((var5 = var4.key) == null || var3 == null || ConcurrentSkipListMap.cpr(var2, var3, var5) > 0); var4 = var4.next) {
               Object var6;
               if ((var6 = var4.value) != null && var6 != var4) {
                  var1.accept(var5);
               }
            }

         }
      }

      public boolean tryAdvance(Consumer<? super K> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            Comparator var2 = this.comparator;
            Object var3 = this.fence;

            ConcurrentSkipListMap.Node var4;
            for(var4 = this.current; var4 != null; var4 = var4.next) {
               Object var5;
               if ((var5 = var4.key) != null && var3 != null && ConcurrentSkipListMap.cpr(var2, var3, var5) <= 0) {
                  var4 = null;
                  break;
               }

               Object var6;
               if ((var6 = var4.value) != null && var6 != var4) {
                  this.current = var4.next;
                  var1.accept(var5);
                  return true;
               }
            }

            this.current = var4;
            return false;
         }
      }

      public int characteristics() {
         return 4373;
      }

      public final Comparator<? super K> getComparator() {
         return this.comparator;
      }
   }

   abstract static class CSLMSpliterator<K, V> {
      final Comparator<? super K> comparator;
      final K fence;
      ConcurrentSkipListMap.Index<K, V> row;
      ConcurrentSkipListMap.Node<K, V> current;
      int est;

      CSLMSpliterator(Comparator<? super K> var1, ConcurrentSkipListMap.Index<K, V> var2, ConcurrentSkipListMap.Node<K, V> var3, K var4, int var5) {
         this.comparator = var1;
         this.row = var2;
         this.current = var3;
         this.fence = var4;
         this.est = var5;
      }

      public final long estimateSize() {
         return (long)this.est;
      }
   }

   static final class SubMap<K, V> extends AbstractMap<K, V> implements ConcurrentNavigableMap<K, V>, Cloneable, Serializable {
      private static final long serialVersionUID = -7647078645895051609L;
      private final ConcurrentSkipListMap<K, V> m;
      private final K lo;
      private final K hi;
      private final boolean loInclusive;
      private final boolean hiInclusive;
      private final boolean isDescending;
      private transient ConcurrentSkipListMap.KeySet<K> keySetView;
      private transient Set<Map.Entry<K, V>> entrySetView;
      private transient Collection<V> valuesView;

      SubMap(ConcurrentSkipListMap<K, V> var1, K var2, boolean var3, K var4, boolean var5, boolean var6) {
         Comparator var7 = var1.comparator;
         if (var2 != null && var4 != null && ConcurrentSkipListMap.cpr(var7, var2, var4) > 0) {
            throw new IllegalArgumentException("inconsistent range");
         } else {
            this.m = var1;
            this.lo = var2;
            this.hi = var4;
            this.loInclusive = var3;
            this.hiInclusive = var5;
            this.isDescending = var6;
         }
      }

      boolean tooLow(Object var1, Comparator<? super K> var2) {
         int var3;
         return this.lo != null && ((var3 = ConcurrentSkipListMap.cpr(var2, var1, this.lo)) < 0 || var3 == 0 && !this.loInclusive);
      }

      boolean tooHigh(Object var1, Comparator<? super K> var2) {
         int var3;
         return this.hi != null && ((var3 = ConcurrentSkipListMap.cpr(var2, var1, this.hi)) > 0 || var3 == 0 && !this.hiInclusive);
      }

      boolean inBounds(Object var1, Comparator<? super K> var2) {
         return !this.tooLow(var1, var2) && !this.tooHigh(var1, var2);
      }

      void checkKeyBounds(K var1, Comparator<? super K> var2) {
         if (var1 == null) {
            throw new NullPointerException();
         } else if (!this.inBounds(var1, var2)) {
            throw new IllegalArgumentException("key out of range");
         }
      }

      boolean isBeforeEnd(ConcurrentSkipListMap.Node<K, V> var1, Comparator<? super K> var2) {
         if (var1 == null) {
            return false;
         } else if (this.hi == null) {
            return true;
         } else {
            Object var3 = var1.key;
            if (var3 == null) {
               return true;
            } else {
               int var4 = ConcurrentSkipListMap.cpr(var2, var3, this.hi);
               return var4 <= 0 && (var4 != 0 || this.hiInclusive);
            }
         }
      }

      ConcurrentSkipListMap.Node<K, V> loNode(Comparator<? super K> var1) {
         if (this.lo == null) {
            return this.m.findFirst();
         } else {
            return this.loInclusive ? this.m.findNear(this.lo, 1, var1) : this.m.findNear(this.lo, 0, var1);
         }
      }

      ConcurrentSkipListMap.Node<K, V> hiNode(Comparator<? super K> var1) {
         if (this.hi == null) {
            return this.m.findLast();
         } else {
            return this.hiInclusive ? this.m.findNear(this.hi, 3, var1) : this.m.findNear(this.hi, 2, var1);
         }
      }

      K lowestKey() {
         Comparator var1 = this.m.comparator;
         ConcurrentSkipListMap.Node var2 = this.loNode(var1);
         if (this.isBeforeEnd(var2, var1)) {
            return var2.key;
         } else {
            throw new NoSuchElementException();
         }
      }

      K highestKey() {
         Comparator var1 = this.m.comparator;
         ConcurrentSkipListMap.Node var2 = this.hiNode(var1);
         if (var2 != null) {
            Object var3 = var2.key;
            if (this.inBounds(var3, var1)) {
               return var3;
            }
         }

         throw new NoSuchElementException();
      }

      Map.Entry<K, V> lowestEntry() {
         Comparator var1 = this.m.comparator;

         AbstractMap.SimpleImmutableEntry var3;
         do {
            ConcurrentSkipListMap.Node var2 = this.loNode(var1);
            if (!this.isBeforeEnd(var2, var1)) {
               return null;
            }

            var3 = var2.createSnapshot();
         } while(var3 == null);

         return var3;
      }

      Map.Entry<K, V> highestEntry() {
         Comparator var1 = this.m.comparator;

         AbstractMap.SimpleImmutableEntry var3;
         do {
            ConcurrentSkipListMap.Node var2 = this.hiNode(var1);
            if (var2 == null || !this.inBounds(var2.key, var1)) {
               return null;
            }

            var3 = var2.createSnapshot();
         } while(var3 == null);

         return var3;
      }

      Map.Entry<K, V> removeLowest() {
         Comparator var1 = this.m.comparator;

         Object var3;
         Object var4;
         do {
            ConcurrentSkipListMap.Node var2 = this.loNode(var1);
            if (var2 == null) {
               return null;
            }

            var3 = var2.key;
            if (!this.inBounds(var3, var1)) {
               return null;
            }

            var4 = this.m.doRemove(var3, (Object)null);
         } while(var4 == null);

         return new AbstractMap.SimpleImmutableEntry(var3, var4);
      }

      Map.Entry<K, V> removeHighest() {
         Comparator var1 = this.m.comparator;

         Object var3;
         Object var4;
         do {
            ConcurrentSkipListMap.Node var2 = this.hiNode(var1);
            if (var2 == null) {
               return null;
            }

            var3 = var2.key;
            if (!this.inBounds(var3, var1)) {
               return null;
            }

            var4 = this.m.doRemove(var3, (Object)null);
         } while(var4 == null);

         return new AbstractMap.SimpleImmutableEntry(var3, var4);
      }

      Map.Entry<K, V> getNearEntry(K var1, int var2) {
         Comparator var3 = this.m.comparator;
         if (this.isDescending) {
            if ((var2 & 2) == 0) {
               var2 |= 2;
            } else {
               var2 &= -3;
            }
         }

         if (this.tooLow(var1, var3)) {
            return (var2 & 2) != 0 ? null : this.lowestEntry();
         } else if (this.tooHigh(var1, var3)) {
            return (var2 & 2) != 0 ? this.highestEntry() : null;
         } else {
            Object var5;
            Object var6;
            do {
               ConcurrentSkipListMap.Node var4 = this.m.findNear(var1, var2, var3);
               if (var4 == null || !this.inBounds(var4.key, var3)) {
                  return null;
               }

               var5 = var4.key;
               var6 = var4.getValidValue();
            } while(var6 == null);

            return new AbstractMap.SimpleImmutableEntry(var5, var6);
         }
      }

      K getNearKey(K var1, int var2) {
         Comparator var3 = this.m.comparator;
         if (this.isDescending) {
            if ((var2 & 2) == 0) {
               var2 |= 2;
            } else {
               var2 &= -3;
            }
         }

         ConcurrentSkipListMap.Node var4;
         if (this.tooLow(var1, var3)) {
            if ((var2 & 2) == 0) {
               var4 = this.loNode(var3);
               if (this.isBeforeEnd(var4, var3)) {
                  return var4.key;
               }
            }

            return null;
         } else {
            Object var5;
            if (this.tooHigh(var1, var3)) {
               if ((var2 & 2) != 0) {
                  var4 = this.hiNode(var3);
                  if (var4 != null) {
                     var5 = var4.key;
                     if (this.inBounds(var5, var3)) {
                        return var5;
                     }
                  }
               }

               return null;
            } else {
               Object var6;
               do {
                  var4 = this.m.findNear(var1, var2, var3);
                  if (var4 == null || !this.inBounds(var4.key, var3)) {
                     return null;
                  }

                  var5 = var4.key;
                  var6 = var4.getValidValue();
               } while(var6 == null);

               return var5;
            }
         }
      }

      public boolean containsKey(Object var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            return this.inBounds(var1, this.m.comparator) && this.m.containsKey(var1);
         }
      }

      public V get(Object var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            return !this.inBounds(var1, this.m.comparator) ? null : this.m.get(var1);
         }
      }

      public V put(K var1, V var2) {
         this.checkKeyBounds(var1, this.m.comparator);
         return this.m.put(var1, var2);
      }

      public V remove(Object var1) {
         return !this.inBounds(var1, this.m.comparator) ? null : this.m.remove(var1);
      }

      public int size() {
         Comparator var1 = this.m.comparator;
         long var2 = 0L;

         for(ConcurrentSkipListMap.Node var4 = this.loNode(var1); this.isBeforeEnd(var4, var1); var4 = var4.next) {
            if (var4.getValidValue() != null) {
               ++var2;
            }
         }

         return var2 >= 2147483647L ? Integer.MAX_VALUE : (int)var2;
      }

      public boolean isEmpty() {
         Comparator var1 = this.m.comparator;
         return !this.isBeforeEnd(this.loNode(var1), var1);
      }

      public boolean containsValue(Object var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            Comparator var2 = this.m.comparator;

            for(ConcurrentSkipListMap.Node var3 = this.loNode(var2); this.isBeforeEnd(var3, var2); var3 = var3.next) {
               Object var4 = var3.getValidValue();
               if (var4 != null && var1.equals(var4)) {
                  return true;
               }
            }

            return false;
         }
      }

      public void clear() {
         Comparator var1 = this.m.comparator;

         for(ConcurrentSkipListMap.Node var2 = this.loNode(var1); this.isBeforeEnd(var2, var1); var2 = var2.next) {
            if (var2.getValidValue() != null) {
               this.m.remove(var2.key);
            }
         }

      }

      public V putIfAbsent(K var1, V var2) {
         this.checkKeyBounds(var1, this.m.comparator);
         return this.m.putIfAbsent(var1, var2);
      }

      public boolean remove(Object var1, Object var2) {
         return this.inBounds(var1, this.m.comparator) && this.m.remove(var1, var2);
      }

      public boolean replace(K var1, V var2, V var3) {
         this.checkKeyBounds(var1, this.m.comparator);
         return this.m.replace(var1, var2, var3);
      }

      public V replace(K var1, V var2) {
         this.checkKeyBounds(var1, this.m.comparator);
         return this.m.replace(var1, var2);
      }

      public Comparator<? super K> comparator() {
         Comparator var1 = this.m.comparator();
         return this.isDescending ? Collections.reverseOrder(var1) : var1;
      }

      ConcurrentSkipListMap.SubMap<K, V> newSubMap(K var1, boolean var2, K var3, boolean var4) {
         Comparator var5 = this.m.comparator;
         if (this.isDescending) {
            Object var6 = var1;
            var1 = var3;
            var3 = var6;
            boolean var7 = var2;
            var2 = var4;
            var4 = var7;
         }

         int var8;
         if (this.lo != null) {
            if (var1 == null) {
               var1 = this.lo;
               var2 = this.loInclusive;
            } else {
               var8 = ConcurrentSkipListMap.cpr(var5, var1, this.lo);
               if (var8 < 0 || var8 == 0 && !this.loInclusive && var2) {
                  throw new IllegalArgumentException("key out of range");
               }
            }
         }

         if (this.hi != null) {
            if (var3 == null) {
               var3 = this.hi;
               var4 = this.hiInclusive;
            } else {
               var8 = ConcurrentSkipListMap.cpr(var5, var3, this.hi);
               if (var8 > 0 || var8 == 0 && !this.hiInclusive && var4) {
                  throw new IllegalArgumentException("key out of range");
               }
            }
         }

         return new ConcurrentSkipListMap.SubMap(this.m, var1, var2, var3, var4, this.isDescending);
      }

      public ConcurrentSkipListMap.SubMap<K, V> subMap(K var1, boolean var2, K var3, boolean var4) {
         if (var1 != null && var3 != null) {
            return this.newSubMap(var1, var2, var3, var4);
         } else {
            throw new NullPointerException();
         }
      }

      public ConcurrentSkipListMap.SubMap<K, V> headMap(K var1, boolean var2) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            return this.newSubMap((Object)null, false, var1, var2);
         }
      }

      public ConcurrentSkipListMap.SubMap<K, V> tailMap(K var1, boolean var2) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            return this.newSubMap(var1, var2, (Object)null, false);
         }
      }

      public ConcurrentSkipListMap.SubMap<K, V> subMap(K var1, K var2) {
         return this.subMap(var1, true, var2, false);
      }

      public ConcurrentSkipListMap.SubMap<K, V> headMap(K var1) {
         return this.headMap(var1, false);
      }

      public ConcurrentSkipListMap.SubMap<K, V> tailMap(K var1) {
         return this.tailMap(var1, true);
      }

      public ConcurrentSkipListMap.SubMap<K, V> descendingMap() {
         return new ConcurrentSkipListMap.SubMap(this.m, this.lo, this.loInclusive, this.hi, this.hiInclusive, !this.isDescending);
      }

      public Map.Entry<K, V> ceilingEntry(K var1) {
         return this.getNearEntry(var1, 1);
      }

      public K ceilingKey(K var1) {
         return this.getNearKey(var1, 1);
      }

      public Map.Entry<K, V> lowerEntry(K var1) {
         return this.getNearEntry(var1, 2);
      }

      public K lowerKey(K var1) {
         return this.getNearKey(var1, 2);
      }

      public Map.Entry<K, V> floorEntry(K var1) {
         return this.getNearEntry(var1, 3);
      }

      public K floorKey(K var1) {
         return this.getNearKey(var1, 3);
      }

      public Map.Entry<K, V> higherEntry(K var1) {
         return this.getNearEntry(var1, 0);
      }

      public K higherKey(K var1) {
         return this.getNearKey(var1, 0);
      }

      public K firstKey() {
         return this.isDescending ? this.highestKey() : this.lowestKey();
      }

      public K lastKey() {
         return this.isDescending ? this.lowestKey() : this.highestKey();
      }

      public Map.Entry<K, V> firstEntry() {
         return this.isDescending ? this.highestEntry() : this.lowestEntry();
      }

      public Map.Entry<K, V> lastEntry() {
         return this.isDescending ? this.lowestEntry() : this.highestEntry();
      }

      public Map.Entry<K, V> pollFirstEntry() {
         return this.isDescending ? this.removeHighest() : this.removeLowest();
      }

      public Map.Entry<K, V> pollLastEntry() {
         return this.isDescending ? this.removeLowest() : this.removeHighest();
      }

      public NavigableSet<K> keySet() {
         ConcurrentSkipListMap.KeySet var1 = this.keySetView;
         return var1 != null ? var1 : (this.keySetView = new ConcurrentSkipListMap.KeySet(this));
      }

      public NavigableSet<K> navigableKeySet() {
         ConcurrentSkipListMap.KeySet var1 = this.keySetView;
         return var1 != null ? var1 : (this.keySetView = new ConcurrentSkipListMap.KeySet(this));
      }

      public Collection<V> values() {
         Collection var1 = this.valuesView;
         return var1 != null ? var1 : (this.valuesView = new ConcurrentSkipListMap.Values(this));
      }

      public Set<Map.Entry<K, V>> entrySet() {
         Set var1 = this.entrySetView;
         return var1 != null ? var1 : (this.entrySetView = new ConcurrentSkipListMap.EntrySet(this));
      }

      public NavigableSet<K> descendingKeySet() {
         return this.descendingMap().navigableKeySet();
      }

      Iterator<K> keyIterator() {
         return new ConcurrentSkipListMap.SubMap.SubMapKeyIterator();
      }

      Iterator<V> valueIterator() {
         return new ConcurrentSkipListMap.SubMap.SubMapValueIterator();
      }

      Iterator<Map.Entry<K, V>> entryIterator() {
         return new ConcurrentSkipListMap.SubMap.SubMapEntryIterator();
      }

      final class SubMapEntryIterator extends ConcurrentSkipListMap.SubMap<K, V>.SubMapIter<Map.Entry<K, V>> {
         SubMapEntryIterator() {
            super();
         }

         public Map.Entry<K, V> next() {
            ConcurrentSkipListMap.Node var1 = this.next;
            Object var2 = this.nextValue;
            this.advance();
            return new AbstractMap.SimpleImmutableEntry(var1.key, var2);
         }

         public int characteristics() {
            return 1;
         }
      }

      final class SubMapKeyIterator extends ConcurrentSkipListMap.SubMap<K, V>.SubMapIter<K> {
         SubMapKeyIterator() {
            super();
         }

         public K next() {
            ConcurrentSkipListMap.Node var1 = this.next;
            this.advance();
            return var1.key;
         }

         public int characteristics() {
            return 21;
         }

         public final Comparator<? super K> getComparator() {
            return SubMap.this.comparator();
         }
      }

      final class SubMapValueIterator extends ConcurrentSkipListMap.SubMap<K, V>.SubMapIter<V> {
         SubMapValueIterator() {
            super();
         }

         public V next() {
            Object var1 = this.nextValue;
            this.advance();
            return var1;
         }

         public int characteristics() {
            return 0;
         }
      }

      abstract class SubMapIter<T> implements Iterator<T>, Spliterator<T> {
         ConcurrentSkipListMap.Node<K, V> lastReturned;
         ConcurrentSkipListMap.Node<K, V> next;
         V nextValue;

         SubMapIter() {
            Comparator var2 = SubMap.this.m.comparator;

            while(true) {
               this.next = SubMap.this.isDescending ? SubMap.this.hiNode(var2) : SubMap.this.loNode(var2);
               if (this.next == null) {
                  break;
               }

               Object var3 = this.next.value;
               if (var3 != null && var3 != this.next) {
                  if (!SubMap.this.inBounds(this.next.key, var2)) {
                     this.next = null;
                  } else {
                     this.nextValue = var3;
                  }
                  break;
               }
            }

         }

         public final boolean hasNext() {
            return this.next != null;
         }

         final void advance() {
            if (this.next == null) {
               throw new NoSuchElementException();
            } else {
               this.lastReturned = this.next;
               if (SubMap.this.isDescending) {
                  this.descend();
               } else {
                  this.ascend();
               }

            }
         }

         private void ascend() {
            Comparator var1 = SubMap.this.m.comparator;

            while(true) {
               this.next = this.next.next;
               if (this.next == null) {
                  break;
               }

               Object var2 = this.next.value;
               if (var2 != null && var2 != this.next) {
                  if (SubMap.this.tooHigh(this.next.key, var1)) {
                     this.next = null;
                  } else {
                     this.nextValue = var2;
                  }
                  break;
               }
            }

         }

         private void descend() {
            Comparator var1 = SubMap.this.m.comparator;

            while(true) {
               this.next = SubMap.this.m.findNear(this.lastReturned.key, 2, var1);
               if (this.next == null) {
                  break;
               }

               Object var2 = this.next.value;
               if (var2 != null && var2 != this.next) {
                  if (SubMap.this.tooLow(this.next.key, var1)) {
                     this.next = null;
                  } else {
                     this.nextValue = var2;
                  }
                  break;
               }
            }

         }

         public void remove() {
            ConcurrentSkipListMap.Node var1 = this.lastReturned;
            if (var1 == null) {
               throw new IllegalStateException();
            } else {
               SubMap.this.m.remove(var1.key);
               this.lastReturned = null;
            }
         }

         public Spliterator<T> trySplit() {
            return null;
         }

         public boolean tryAdvance(Consumer<? super T> var1) {
            if (this.hasNext()) {
               var1.accept(this.next());
               return true;
            } else {
               return false;
            }
         }

         public void forEachRemaining(Consumer<? super T> var1) {
            while(this.hasNext()) {
               var1.accept(this.next());
            }

         }

         public long estimateSize() {
            return Long.MAX_VALUE;
         }
      }
   }

   static final class EntrySet<K1, V1> extends AbstractSet<Map.Entry<K1, V1>> {
      final ConcurrentNavigableMap<K1, V1> m;

      EntrySet(ConcurrentNavigableMap<K1, V1> var1) {
         this.m = var1;
      }

      public Iterator<Map.Entry<K1, V1>> iterator() {
         return this.m instanceof ConcurrentSkipListMap ? ((ConcurrentSkipListMap)this.m).entryIterator() : ((ConcurrentSkipListMap.SubMap)this.m).entryIterator();
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof Map.Entry)) {
            return false;
         } else {
            Map.Entry var2 = (Map.Entry)var1;
            Object var3 = this.m.get(var2.getKey());
            return var3 != null && var3.equals(var2.getValue());
         }
      }

      public boolean remove(Object var1) {
         if (!(var1 instanceof Map.Entry)) {
            return false;
         } else {
            Map.Entry var2 = (Map.Entry)var1;
            return this.m.remove(var2.getKey(), var2.getValue());
         }
      }

      public boolean isEmpty() {
         return this.m.isEmpty();
      }

      public int size() {
         return this.m.size();
      }

      public void clear() {
         this.m.clear();
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof Set)) {
            return false;
         } else {
            Collection var2 = (Collection)var1;

            try {
               return this.containsAll(var2) && var2.containsAll(this);
            } catch (ClassCastException var4) {
               return false;
            } catch (NullPointerException var5) {
               return false;
            }
         }
      }

      public Object[] toArray() {
         return ConcurrentSkipListMap.toList(this).toArray();
      }

      public <T> T[] toArray(T[] var1) {
         return ConcurrentSkipListMap.toList(this).toArray(var1);
      }

      public Spliterator<Map.Entry<K1, V1>> spliterator() {
         return (Spliterator)(this.m instanceof ConcurrentSkipListMap ? ((ConcurrentSkipListMap)this.m).entrySpliterator() : (Spliterator)((ConcurrentSkipListMap.SubMap)this.m).entryIterator());
      }
   }

   static final class Values<E> extends AbstractCollection<E> {
      final ConcurrentNavigableMap<?, E> m;

      Values(ConcurrentNavigableMap<?, E> var1) {
         this.m = var1;
      }

      public Iterator<E> iterator() {
         return this.m instanceof ConcurrentSkipListMap ? ((ConcurrentSkipListMap)this.m).valueIterator() : ((ConcurrentSkipListMap.SubMap)this.m).valueIterator();
      }

      public boolean isEmpty() {
         return this.m.isEmpty();
      }

      public int size() {
         return this.m.size();
      }

      public boolean contains(Object var1) {
         return this.m.containsValue(var1);
      }

      public void clear() {
         this.m.clear();
      }

      public Object[] toArray() {
         return ConcurrentSkipListMap.toList(this).toArray();
      }

      public <T> T[] toArray(T[] var1) {
         return ConcurrentSkipListMap.toList(this).toArray(var1);
      }

      public Spliterator<E> spliterator() {
         return (Spliterator)(this.m instanceof ConcurrentSkipListMap ? ((ConcurrentSkipListMap)this.m).valueSpliterator() : (Spliterator)((ConcurrentSkipListMap.SubMap)this.m).valueIterator());
      }
   }

   static final class KeySet<E> extends AbstractSet<E> implements NavigableSet<E> {
      final ConcurrentNavigableMap<E, ?> m;

      KeySet(ConcurrentNavigableMap<E, ?> var1) {
         this.m = var1;
      }

      public int size() {
         return this.m.size();
      }

      public boolean isEmpty() {
         return this.m.isEmpty();
      }

      public boolean contains(Object var1) {
         return this.m.containsKey(var1);
      }

      public boolean remove(Object var1) {
         return this.m.remove(var1) != null;
      }

      public void clear() {
         this.m.clear();
      }

      public E lower(E var1) {
         return this.m.lowerKey(var1);
      }

      public E floor(E var1) {
         return this.m.floorKey(var1);
      }

      public E ceiling(E var1) {
         return this.m.ceilingKey(var1);
      }

      public E higher(E var1) {
         return this.m.higherKey(var1);
      }

      public Comparator<? super E> comparator() {
         return this.m.comparator();
      }

      public E first() {
         return this.m.firstKey();
      }

      public E last() {
         return this.m.lastKey();
      }

      public E pollFirst() {
         Map.Entry var1 = this.m.pollFirstEntry();
         return var1 == null ? null : var1.getKey();
      }

      public E pollLast() {
         Map.Entry var1 = this.m.pollLastEntry();
         return var1 == null ? null : var1.getKey();
      }

      public Iterator<E> iterator() {
         return this.m instanceof ConcurrentSkipListMap ? ((ConcurrentSkipListMap)this.m).keyIterator() : ((ConcurrentSkipListMap.SubMap)this.m).keyIterator();
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof Set)) {
            return false;
         } else {
            Collection var2 = (Collection)var1;

            try {
               return this.containsAll(var2) && var2.containsAll(this);
            } catch (ClassCastException var4) {
               return false;
            } catch (NullPointerException var5) {
               return false;
            }
         }
      }

      public Object[] toArray() {
         return ConcurrentSkipListMap.toList(this).toArray();
      }

      public <T> T[] toArray(T[] var1) {
         return ConcurrentSkipListMap.toList(this).toArray(var1);
      }

      public Iterator<E> descendingIterator() {
         return this.descendingSet().iterator();
      }

      public NavigableSet<E> subSet(E var1, boolean var2, E var3, boolean var4) {
         return new ConcurrentSkipListMap.KeySet(this.m.subMap(var1, var2, var3, var4));
      }

      public NavigableSet<E> headSet(E var1, boolean var2) {
         return new ConcurrentSkipListMap.KeySet(this.m.headMap(var1, var2));
      }

      public NavigableSet<E> tailSet(E var1, boolean var2) {
         return new ConcurrentSkipListMap.KeySet(this.m.tailMap(var1, var2));
      }

      public NavigableSet<E> subSet(E var1, E var2) {
         return this.subSet(var1, true, var2, false);
      }

      public NavigableSet<E> headSet(E var1) {
         return this.headSet(var1, false);
      }

      public NavigableSet<E> tailSet(E var1) {
         return this.tailSet(var1, true);
      }

      public NavigableSet<E> descendingSet() {
         return new ConcurrentSkipListMap.KeySet(this.m.descendingMap());
      }

      public Spliterator<E> spliterator() {
         return (Spliterator)(this.m instanceof ConcurrentSkipListMap ? ((ConcurrentSkipListMap)this.m).keySpliterator() : (Spliterator)((ConcurrentSkipListMap.SubMap)this.m).keyIterator());
      }
   }

   final class EntryIterator extends ConcurrentSkipListMap<K, V>.Iter<Map.Entry<K, V>> {
      EntryIterator() {
         super();
      }

      public Map.Entry<K, V> next() {
         ConcurrentSkipListMap.Node var1 = this.next;
         Object var2 = this.nextValue;
         this.advance();
         return new AbstractMap.SimpleImmutableEntry(var1.key, var2);
      }
   }

   final class KeyIterator extends ConcurrentSkipListMap<K, V>.Iter<K> {
      KeyIterator() {
         super();
      }

      public K next() {
         ConcurrentSkipListMap.Node var1 = this.next;
         this.advance();
         return var1.key;
      }
   }

   final class ValueIterator extends ConcurrentSkipListMap<K, V>.Iter<V> {
      ValueIterator() {
         super();
      }

      public V next() {
         Object var1 = this.nextValue;
         this.advance();
         return var1;
      }
   }

   abstract class Iter<T> implements Iterator<T> {
      ConcurrentSkipListMap.Node<K, V> lastReturned;
      ConcurrentSkipListMap.Node<K, V> next;
      V nextValue;

      Iter() {
         while((this.next = ConcurrentSkipListMap.this.findFirst()) != null) {
            Object var2 = this.next.value;
            if (var2 != null && var2 != this.next) {
               this.nextValue = var2;
               break;
            }
         }

      }

      public final boolean hasNext() {
         return this.next != null;
      }

      final void advance() {
         if (this.next == null) {
            throw new NoSuchElementException();
         } else {
            this.lastReturned = this.next;

            while((this.next = this.next.next) != null) {
               Object var1 = this.next.value;
               if (var1 != null && var1 != this.next) {
                  this.nextValue = var1;
                  break;
               }
            }

         }
      }

      public void remove() {
         ConcurrentSkipListMap.Node var1 = this.lastReturned;
         if (var1 == null) {
            throw new IllegalStateException();
         } else {
            ConcurrentSkipListMap.this.remove(var1.key);
            this.lastReturned = null;
         }
      }
   }

   static final class HeadIndex<K, V> extends ConcurrentSkipListMap.Index<K, V> {
      final int level;

      HeadIndex(ConcurrentSkipListMap.Node<K, V> var1, ConcurrentSkipListMap.Index<K, V> var2, ConcurrentSkipListMap.Index<K, V> var3, int var4) {
         super(var1, var2, var3);
         this.level = var4;
      }
   }

   static class Index<K, V> {
      final ConcurrentSkipListMap.Node<K, V> node;
      final ConcurrentSkipListMap.Index<K, V> down;
      volatile ConcurrentSkipListMap.Index<K, V> right;
      private static final Unsafe UNSAFE;
      private static final long rightOffset;

      Index(ConcurrentSkipListMap.Node<K, V> var1, ConcurrentSkipListMap.Index<K, V> var2, ConcurrentSkipListMap.Index<K, V> var3) {
         this.node = var1;
         this.down = var2;
         this.right = var3;
      }

      final boolean casRight(ConcurrentSkipListMap.Index<K, V> var1, ConcurrentSkipListMap.Index<K, V> var2) {
         return UNSAFE.compareAndSwapObject(this, rightOffset, var1, var2);
      }

      final boolean indexesDeletedNode() {
         return this.node.value == null;
      }

      final boolean link(ConcurrentSkipListMap.Index<K, V> var1, ConcurrentSkipListMap.Index<K, V> var2) {
         ConcurrentSkipListMap.Node var3 = this.node;
         var2.right = var1;
         return var3.value != null && this.casRight(var1, var2);
      }

      final boolean unlink(ConcurrentSkipListMap.Index<K, V> var1) {
         return this.node.value != null && this.casRight(var1, var1.right);
      }

      static {
         try {
            UNSAFE = Unsafe.getUnsafe();
            Class var0 = ConcurrentSkipListMap.Index.class;
            rightOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("right"));
         } catch (Exception var1) {
            throw new Error(var1);
         }
      }
   }

   static final class Node<K, V> {
      final K key;
      volatile Object value;
      volatile ConcurrentSkipListMap.Node<K, V> next;
      private static final Unsafe UNSAFE;
      private static final long valueOffset;
      private static final long nextOffset;

      Node(K var1, Object var2, ConcurrentSkipListMap.Node<K, V> var3) {
         this.key = var1;
         this.value = var2;
         this.next = var3;
      }

      Node(ConcurrentSkipListMap.Node<K, V> var1) {
         this.key = null;
         this.value = this;
         this.next = var1;
      }

      boolean casValue(Object var1, Object var2) {
         return UNSAFE.compareAndSwapObject(this, valueOffset, var1, var2);
      }

      boolean casNext(ConcurrentSkipListMap.Node<K, V> var1, ConcurrentSkipListMap.Node<K, V> var2) {
         return UNSAFE.compareAndSwapObject(this, nextOffset, var1, var2);
      }

      boolean isMarker() {
         return this.value == this;
      }

      boolean isBaseHeader() {
         return this.value == ConcurrentSkipListMap.BASE_HEADER;
      }

      boolean appendMarker(ConcurrentSkipListMap.Node<K, V> var1) {
         return this.casNext(var1, new ConcurrentSkipListMap.Node(var1));
      }

      void helpDelete(ConcurrentSkipListMap.Node<K, V> var1, ConcurrentSkipListMap.Node<K, V> var2) {
         if (var2 == this.next && this == var1.next) {
            if (var2 != null && var2.value == var2) {
               var1.casNext(this, var2.next);
            } else {
               this.casNext(var2, new ConcurrentSkipListMap.Node(var2));
            }
         }

      }

      V getValidValue() {
         Object var1 = this.value;
         return var1 != this && var1 != ConcurrentSkipListMap.BASE_HEADER ? var1 : null;
      }

      AbstractMap.SimpleImmutableEntry<K, V> createSnapshot() {
         Object var1 = this.value;
         return var1 != null && var1 != this && var1 != ConcurrentSkipListMap.BASE_HEADER ? new AbstractMap.SimpleImmutableEntry(this.key, var1) : null;
      }

      static {
         try {
            UNSAFE = Unsafe.getUnsafe();
            Class var0 = ConcurrentSkipListMap.Node.class;
            valueOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("value"));
            nextOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("next"));
         } catch (Exception var1) {
            throw new Error(var1);
         }
      }
   }
}
