package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import sun.misc.Unsafe;

public class ConcurrentLinkedDeque<E> extends AbstractCollection<E> implements Deque<E>, Serializable {
   private static final long serialVersionUID = 876323262645176354L;
   private transient volatile ConcurrentLinkedDeque.Node<E> head;
   private transient volatile ConcurrentLinkedDeque.Node<E> tail;
   private static final ConcurrentLinkedDeque.Node<Object> PREV_TERMINATOR = new ConcurrentLinkedDeque.Node();
   private static final ConcurrentLinkedDeque.Node<Object> NEXT_TERMINATOR;
   private static final int HOPS = 2;
   private static final Unsafe UNSAFE;
   private static final long headOffset;
   private static final long tailOffset;

   ConcurrentLinkedDeque.Node<E> prevTerminator() {
      return PREV_TERMINATOR;
   }

   ConcurrentLinkedDeque.Node<E> nextTerminator() {
      return NEXT_TERMINATOR;
   }

   private void linkFirst(E var1) {
      checkNotNull(var1);
      ConcurrentLinkedDeque.Node var2 = new ConcurrentLinkedDeque.Node(var1);

      label34:
      while(true) {
         ConcurrentLinkedDeque.Node var3 = this.head;
         ConcurrentLinkedDeque.Node var4 = var3;

         while(true) {
            while(true) {
               ConcurrentLinkedDeque.Node var5;
               if ((var5 = var4.prev) != null) {
                  var4 = var5;
                  if ((var5 = var5.prev) != null) {
                     var4 = var3 != (var3 = this.head) ? var3 : var5;
                     continue;
                  }
               }

               if (var4.next == var4) {
                  continue label34;
               }

               var2.lazySetNext(var4);
               if (var4.casPrev((ConcurrentLinkedDeque.Node)null, var2)) {
                  if (var4 != var3) {
                     this.casHead(var3, var2);
                  }

                  return;
               }
            }
         }
      }
   }

   private void linkLast(E var1) {
      checkNotNull(var1);
      ConcurrentLinkedDeque.Node var2 = new ConcurrentLinkedDeque.Node(var1);

      label34:
      while(true) {
         ConcurrentLinkedDeque.Node var3 = this.tail;
         ConcurrentLinkedDeque.Node var4 = var3;

         while(true) {
            while(true) {
               ConcurrentLinkedDeque.Node var5;
               if ((var5 = var4.next) != null) {
                  var4 = var5;
                  if ((var5 = var5.next) != null) {
                     var4 = var3 != (var3 = this.tail) ? var3 : var5;
                     continue;
                  }
               }

               if (var4.prev == var4) {
                  continue label34;
               }

               var2.lazySetPrev(var4);
               if (var4.casNext((ConcurrentLinkedDeque.Node)null, var2)) {
                  if (var4 != var3) {
                     this.casTail(var3, var2);
                  }

                  return;
               }
            }
         }
      }
   }

   void unlink(ConcurrentLinkedDeque.Node<E> var1) {
      ConcurrentLinkedDeque.Node var2 = var1.prev;
      ConcurrentLinkedDeque.Node var3 = var1.next;
      if (var2 == null) {
         this.unlinkFirst(var1, var3);
      } else if (var3 == null) {
         this.unlinkLast(var1, var2);
      } else {
         int var8 = 1;

         ConcurrentLinkedDeque.Node var4;
         boolean var6;
         ConcurrentLinkedDeque.Node var9;
         ConcurrentLinkedDeque.Node var10;
         label95: {
            for(var9 = var2; var9.item == null; ++var8) {
               var10 = var9.prev;
               if (var10 == null) {
                  if (var9.next == var9) {
                     return;
                  }

                  var4 = var9;
                  var6 = true;
                  break label95;
               }

               if (var9 == var10) {
                  return;
               }

               var9 = var10;
            }

            var4 = var9;
            var6 = false;
         }

         ConcurrentLinkedDeque.Node var5;
         boolean var7;
         label85: {
            for(var9 = var3; var9.item == null; ++var8) {
               var10 = var9.next;
               if (var10 == null) {
                  if (var9.prev == var9) {
                     return;
                  }

                  var5 = var9;
                  var7 = true;
                  break label85;
               }

               if (var9 == var10) {
                  return;
               }

               var9 = var10;
            }

            var5 = var9;
            var7 = false;
         }

         if (var8 < 2 && var6 | var7) {
            return;
         }

         this.skipDeletedSuccessors(var4);
         this.skipDeletedPredecessors(var5);
         if (var6 | var7 && var4.next == var5 && var5.prev == var4) {
            if (var6) {
               if (var4.prev != null) {
                  return;
               }
            } else if (var4.item == null) {
               return;
            }

            if (var7) {
               if (var5.next != null) {
                  return;
               }
            } else if (var5.item == null) {
               return;
            }

            this.updateHead();
            this.updateTail();
            var1.lazySetPrev(var6 ? this.prevTerminator() : var1);
            var1.lazySetNext(var7 ? this.nextTerminator() : var1);
         }
      }

   }

   private void unlinkFirst(ConcurrentLinkedDeque.Node<E> var1, ConcurrentLinkedDeque.Node<E> var2) {
      ConcurrentLinkedDeque.Node var3 = null;

      ConcurrentLinkedDeque.Node var4;
      ConcurrentLinkedDeque.Node var5;
      for(var4 = var2; var4.item == null && (var5 = var4.next) != null; var4 = var5) {
         if (var4 == var5) {
            return;
         }

         var3 = var4;
      }

      if (var3 != null && var4.prev != var4 && var1.casNext(var2, var4)) {
         this.skipDeletedPredecessors(var4);
         if (var1.prev == null && (var4.next == null || var4.item != null) && var4.prev == var1) {
            this.updateHead();
            this.updateTail();
            var3.lazySetNext(var3);
            var3.lazySetPrev(this.prevTerminator());
         }
      }

   }

   private void unlinkLast(ConcurrentLinkedDeque.Node<E> var1, ConcurrentLinkedDeque.Node<E> var2) {
      ConcurrentLinkedDeque.Node var3 = null;

      ConcurrentLinkedDeque.Node var4;
      ConcurrentLinkedDeque.Node var5;
      for(var4 = var2; var4.item == null && (var5 = var4.prev) != null; var4 = var5) {
         if (var4 == var5) {
            return;
         }

         var3 = var4;
      }

      if (var3 != null && var4.next != var4 && var1.casPrev(var2, var4)) {
         this.skipDeletedSuccessors(var4);
         if (var1.next == null && (var4.prev == null || var4.item != null) && var4.next == var1) {
            this.updateHead();
            this.updateTail();
            var3.lazySetPrev(var3);
            var3.lazySetNext(this.nextTerminator());
         }
      }

   }

   private final void updateHead() {
      label28:
      while(true) {
         ConcurrentLinkedDeque.Node var1;
         ConcurrentLinkedDeque.Node var2;
         if ((var1 = this.head).item == null && (var2 = var1.prev) != null) {
            ConcurrentLinkedDeque.Node var3;
            while((var3 = var2.prev) != null) {
               var2 = var3;
               if ((var3 = var3.prev) == null) {
                  break;
               }

               if (var1 != this.head) {
                  continue label28;
               }

               var2 = var3;
            }

            if (!this.casHead(var1, var2)) {
               continue;
            }

            return;
         }

         return;
      }
   }

   private final void updateTail() {
      label28:
      while(true) {
         ConcurrentLinkedDeque.Node var1;
         ConcurrentLinkedDeque.Node var2;
         if ((var1 = this.tail).item == null && (var2 = var1.next) != null) {
            ConcurrentLinkedDeque.Node var3;
            while((var3 = var2.next) != null) {
               var2 = var3;
               if ((var3 = var3.next) == null) {
                  break;
               }

               if (var1 != this.tail) {
                  continue label28;
               }

               var2 = var3;
            }

            if (!this.casTail(var1, var2)) {
               continue;
            }

            return;
         }

         return;
      }
   }

   private void skipDeletedPredecessors(ConcurrentLinkedDeque.Node<E> var1) {
      do {
         ConcurrentLinkedDeque.Node var2 = var1.prev;
         ConcurrentLinkedDeque.Node var3 = var2;

         while(true) {
            if (var3.item == null) {
               ConcurrentLinkedDeque.Node var4 = var3.prev;
               if (var4 != null) {
                  if (var3 != var4) {
                     var3 = var4;
                     continue;
                  }
                  break;
               }

               if (var3.next == var3) {
                  break;
               }
            }

            if (var2 != var3 && !var1.casPrev(var2, var3)) {
               break;
            }

            return;
         }
      } while(var1.item != null || var1.next == null);

   }

   private void skipDeletedSuccessors(ConcurrentLinkedDeque.Node<E> var1) {
      do {
         ConcurrentLinkedDeque.Node var2 = var1.next;
         ConcurrentLinkedDeque.Node var3 = var2;

         while(true) {
            if (var3.item == null) {
               ConcurrentLinkedDeque.Node var4 = var3.next;
               if (var4 != null) {
                  if (var3 != var4) {
                     var3 = var4;
                     continue;
                  }
                  break;
               }

               if (var3.prev == var3) {
                  break;
               }
            }

            if (var2 != var3 && !var1.casNext(var2, var3)) {
               break;
            }

            return;
         }
      } while(var1.item != null || var1.prev == null);

   }

   final ConcurrentLinkedDeque.Node<E> succ(ConcurrentLinkedDeque.Node<E> var1) {
      ConcurrentLinkedDeque.Node var2 = var1.next;
      return var1 == var2 ? this.first() : var2;
   }

   final ConcurrentLinkedDeque.Node<E> pred(ConcurrentLinkedDeque.Node<E> var1) {
      ConcurrentLinkedDeque.Node var2 = var1.prev;
      return var1 == var2 ? this.last() : var2;
   }

   ConcurrentLinkedDeque.Node<E> first() {
      ConcurrentLinkedDeque.Node var1;
      ConcurrentLinkedDeque.Node var2;
      do {
         var1 = this.head;

         ConcurrentLinkedDeque.Node var3;
         for(var2 = var1; (var3 = var2.prev) != null; var2 = var1 != (var1 = this.head) ? var1 : var3) {
            var2 = var3;
            if ((var3 = var3.prev) == null) {
               break;
            }
         }
      } while(var2 != var1 && !this.casHead(var1, var2));

      return var2;
   }

   ConcurrentLinkedDeque.Node<E> last() {
      ConcurrentLinkedDeque.Node var1;
      ConcurrentLinkedDeque.Node var2;
      do {
         var1 = this.tail;

         ConcurrentLinkedDeque.Node var3;
         for(var2 = var1; (var3 = var2.next) != null; var2 = var1 != (var1 = this.tail) ? var1 : var3) {
            var2 = var3;
            if ((var3 = var3.next) == null) {
               break;
            }
         }
      } while(var2 != var1 && !this.casTail(var1, var2));

      return var2;
   }

   private static void checkNotNull(Object var0) {
      if (var0 == null) {
         throw new NullPointerException();
      }
   }

   private E screenNullResult(E var1) {
      if (var1 == null) {
         throw new NoSuchElementException();
      } else {
         return var1;
      }
   }

   private ArrayList<E> toArrayList() {
      ArrayList var1 = new ArrayList();

      for(ConcurrentLinkedDeque.Node var2 = this.first(); var2 != null; var2 = this.succ(var2)) {
         Object var3 = var2.item;
         if (var3 != null) {
            var1.add(var3);
         }
      }

      return var1;
   }

   public ConcurrentLinkedDeque() {
      this.head = this.tail = new ConcurrentLinkedDeque.Node((Object)null);
   }

   public ConcurrentLinkedDeque(Collection<? extends E> var1) {
      ConcurrentLinkedDeque.Node var2 = null;
      ConcurrentLinkedDeque.Node var3 = null;
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         Object var5 = var4.next();
         checkNotNull(var5);
         ConcurrentLinkedDeque.Node var6 = new ConcurrentLinkedDeque.Node(var5);
         if (var2 == null) {
            var3 = var6;
            var2 = var6;
         } else {
            var3.lazySetNext(var6);
            var6.lazySetPrev(var3);
            var3 = var6;
         }
      }

      this.initHeadTail(var2, var3);
   }

   private void initHeadTail(ConcurrentLinkedDeque.Node<E> var1, ConcurrentLinkedDeque.Node<E> var2) {
      if (var1 == var2) {
         if (var1 == null) {
            var1 = var2 = new ConcurrentLinkedDeque.Node((Object)null);
         } else {
            ConcurrentLinkedDeque.Node var3 = new ConcurrentLinkedDeque.Node((Object)null);
            var2.lazySetNext(var3);
            var3.lazySetPrev(var2);
            var2 = var3;
         }
      }

      this.head = var1;
      this.tail = var2;
   }

   public void addFirst(E var1) {
      this.linkFirst(var1);
   }

   public void addLast(E var1) {
      this.linkLast(var1);
   }

   public boolean offerFirst(E var1) {
      this.linkFirst(var1);
      return true;
   }

   public boolean offerLast(E var1) {
      this.linkLast(var1);
      return true;
   }

   public E peekFirst() {
      for(ConcurrentLinkedDeque.Node var1 = this.first(); var1 != null; var1 = this.succ(var1)) {
         Object var2 = var1.item;
         if (var2 != null) {
            return var2;
         }
      }

      return null;
   }

   public E peekLast() {
      for(ConcurrentLinkedDeque.Node var1 = this.last(); var1 != null; var1 = this.pred(var1)) {
         Object var2 = var1.item;
         if (var2 != null) {
            return var2;
         }
      }

      return null;
   }

   public E getFirst() {
      return this.screenNullResult(this.peekFirst());
   }

   public E getLast() {
      return this.screenNullResult(this.peekLast());
   }

   public E pollFirst() {
      for(ConcurrentLinkedDeque.Node var1 = this.first(); var1 != null; var1 = this.succ(var1)) {
         Object var2 = var1.item;
         if (var2 != null && var1.casItem(var2, (Object)null)) {
            this.unlink(var1);
            return var2;
         }
      }

      return null;
   }

   public E pollLast() {
      for(ConcurrentLinkedDeque.Node var1 = this.last(); var1 != null; var1 = this.pred(var1)) {
         Object var2 = var1.item;
         if (var2 != null && var1.casItem(var2, (Object)null)) {
            this.unlink(var1);
            return var2;
         }
      }

      return null;
   }

   public E removeFirst() {
      return this.screenNullResult(this.pollFirst());
   }

   public E removeLast() {
      return this.screenNullResult(this.pollLast());
   }

   public boolean offer(E var1) {
      return this.offerLast(var1);
   }

   public boolean add(E var1) {
      return this.offerLast(var1);
   }

   public E poll() {
      return this.pollFirst();
   }

   public E peek() {
      return this.peekFirst();
   }

   public E remove() {
      return this.removeFirst();
   }

   public E pop() {
      return this.removeFirst();
   }

   public E element() {
      return this.getFirst();
   }

   public void push(E var1) {
      this.addFirst(var1);
   }

   public boolean removeFirstOccurrence(Object var1) {
      checkNotNull(var1);

      for(ConcurrentLinkedDeque.Node var2 = this.first(); var2 != null; var2 = this.succ(var2)) {
         Object var3 = var2.item;
         if (var3 != null && var1.equals(var3) && var2.casItem(var3, (Object)null)) {
            this.unlink(var2);
            return true;
         }
      }

      return false;
   }

   public boolean removeLastOccurrence(Object var1) {
      checkNotNull(var1);

      for(ConcurrentLinkedDeque.Node var2 = this.last(); var2 != null; var2 = this.pred(var2)) {
         Object var3 = var2.item;
         if (var3 != null && var1.equals(var3) && var2.casItem(var3, (Object)null)) {
            this.unlink(var2);
            return true;
         }
      }

      return false;
   }

   public boolean contains(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         for(ConcurrentLinkedDeque.Node var2 = this.first(); var2 != null; var2 = this.succ(var2)) {
            Object var3 = var2.item;
            if (var3 != null && var1.equals(var3)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean isEmpty() {
      return this.peekFirst() == null;
   }

   public int size() {
      int var1 = 0;

      for(ConcurrentLinkedDeque.Node var2 = this.first(); var2 != null; var2 = this.succ(var2)) {
         if (var2.item != null) {
            ++var1;
            if (var1 == Integer.MAX_VALUE) {
               break;
            }
         }
      }

      return var1;
   }

   public boolean remove(Object var1) {
      return this.removeFirstOccurrence(var1);
   }

   public boolean addAll(Collection<? extends E> var1) {
      if (var1 == this) {
         throw new IllegalArgumentException();
      } else {
         ConcurrentLinkedDeque.Node var2 = null;
         ConcurrentLinkedDeque.Node var3 = null;
         Iterator var4 = var1.iterator();

         ConcurrentLinkedDeque.Node var6;
         while(var4.hasNext()) {
            Object var5 = var4.next();
            checkNotNull(var5);
            var6 = new ConcurrentLinkedDeque.Node(var5);
            if (var2 == null) {
               var3 = var6;
               var2 = var6;
            } else {
               var3.lazySetNext(var6);
               var6.lazySetPrev(var3);
               var3 = var6;
            }
         }

         if (var2 == null) {
            return false;
         } else {
            label49:
            while(true) {
               ConcurrentLinkedDeque.Node var7 = this.tail;
               ConcurrentLinkedDeque.Node var8 = var7;

               while(true) {
                  while(true) {
                     if ((var6 = var8.next) != null) {
                        var8 = var6;
                        if ((var6 = var6.next) != null) {
                           var8 = var7 != (var7 = this.tail) ? var7 : var6;
                           continue;
                        }
                     }

                     if (var8.prev == var8) {
                        continue label49;
                     }

                     var2.lazySetPrev(var8);
                     if (var8.casNext((ConcurrentLinkedDeque.Node)null, var2)) {
                        if (!this.casTail(var7, var3)) {
                           var7 = this.tail;
                           if (var3.next == null) {
                              this.casTail(var7, var3);
                           }
                        }

                        return true;
                     }
                  }
               }
            }
         }
      }
   }

   public void clear() {
      while(this.pollFirst() != null) {
      }

   }

   public Object[] toArray() {
      return this.toArrayList().toArray();
   }

   public <T> T[] toArray(T[] var1) {
      return this.toArrayList().toArray(var1);
   }

   public Iterator<E> iterator() {
      return new ConcurrentLinkedDeque.Itr();
   }

   public Iterator<E> descendingIterator() {
      return new ConcurrentLinkedDeque.DescendingItr();
   }

   public Spliterator<E> spliterator() {
      return new ConcurrentLinkedDeque.CLDSpliterator(this);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(ConcurrentLinkedDeque.Node var2 = this.first(); var2 != null; var2 = this.succ(var2)) {
         Object var3 = var2.item;
         if (var3 != null) {
            var1.writeObject(var3);
         }
      }

      var1.writeObject((Object)null);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      ConcurrentLinkedDeque.Node var2 = null;
      ConcurrentLinkedDeque.Node var3 = null;

      Object var4;
      while((var4 = var1.readObject()) != null) {
         ConcurrentLinkedDeque.Node var5 = new ConcurrentLinkedDeque.Node(var4);
         if (var2 == null) {
            var3 = var5;
            var2 = var5;
         } else {
            var3.lazySetNext(var5);
            var5.lazySetPrev(var3);
            var3 = var5;
         }
      }

      this.initHeadTail(var2, var3);
   }

   private boolean casHead(ConcurrentLinkedDeque.Node<E> var1, ConcurrentLinkedDeque.Node<E> var2) {
      return UNSAFE.compareAndSwapObject(this, headOffset, var1, var2);
   }

   private boolean casTail(ConcurrentLinkedDeque.Node<E> var1, ConcurrentLinkedDeque.Node<E> var2) {
      return UNSAFE.compareAndSwapObject(this, tailOffset, var1, var2);
   }

   static {
      PREV_TERMINATOR.next = PREV_TERMINATOR;
      NEXT_TERMINATOR = new ConcurrentLinkedDeque.Node();
      NEXT_TERMINATOR.prev = NEXT_TERMINATOR;

      try {
         UNSAFE = Unsafe.getUnsafe();
         Class var0 = ConcurrentLinkedDeque.class;
         headOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("head"));
         tailOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("tail"));
      } catch (Exception var1) {
         throw new Error(var1);
      }
   }

   static final class CLDSpliterator<E> implements Spliterator<E> {
      static final int MAX_BATCH = 33554432;
      final ConcurrentLinkedDeque<E> queue;
      ConcurrentLinkedDeque.Node<E> current;
      int batch;
      boolean exhausted;

      CLDSpliterator(ConcurrentLinkedDeque<E> var1) {
         this.queue = var1;
      }

      public Spliterator<E> trySplit() {
         ConcurrentLinkedDeque var2 = this.queue;
         int var3 = this.batch;
         int var4 = var3 <= 0 ? 1 : (var3 >= 33554432 ? 33554432 : var3 + 1);
         ConcurrentLinkedDeque.Node var1;
         if (!this.exhausted && ((var1 = this.current) != null || (var1 = var2.first()) != null)) {
            if (var1.item == null && var1 == (var1 = var1.next)) {
               this.current = var1 = var2.first();
            }

            if (var1 != null && var1.next != null) {
               Object[] var5 = new Object[var4];
               int var6 = 0;

               do {
                  if ((var5[var6] = var1.item) != null) {
                     ++var6;
                  }

                  if (var1 == (var1 = var1.next)) {
                     var1 = var2.first();
                  }
               } while(var1 != null && var6 < var4);

               if ((this.current = var1) == null) {
                  this.exhausted = true;
               }

               if (var6 > 0) {
                  this.batch = var6;
                  return Spliterators.spliterator((Object[])var5, 0, var6, 4368);
               }
            }
         }

         return null;
      }

      public void forEachRemaining(Consumer<? super E> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            ConcurrentLinkedDeque var3 = this.queue;
            ConcurrentLinkedDeque.Node var2;
            if (!this.exhausted && ((var2 = this.current) != null || (var2 = var3.first()) != null)) {
               this.exhausted = true;

               do {
                  Object var4 = var2.item;
                  if (var2 == (var2 = var2.next)) {
                     var2 = var3.first();
                  }

                  if (var4 != null) {
                     var1.accept(var4);
                  }
               } while(var2 != null);
            }

         }
      }

      public boolean tryAdvance(Consumer<? super E> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            ConcurrentLinkedDeque var3 = this.queue;
            ConcurrentLinkedDeque.Node var2;
            if (!this.exhausted && ((var2 = this.current) != null || (var2 = var3.first()) != null)) {
               Object var4;
               do {
                  var4 = var2.item;
                  if (var2 == (var2 = var2.next)) {
                     var2 = var3.first();
                  }
               } while(var4 == null && var2 != null);

               if ((this.current = var2) == null) {
                  this.exhausted = true;
               }

               if (var4 != null) {
                  var1.accept(var4);
                  return true;
               }
            }

            return false;
         }
      }

      public long estimateSize() {
         return Long.MAX_VALUE;
      }

      public int characteristics() {
         return 4368;
      }
   }

   private class DescendingItr extends ConcurrentLinkedDeque<E>.AbstractItr {
      private DescendingItr() {
         super();
      }

      ConcurrentLinkedDeque.Node<E> startNode() {
         return ConcurrentLinkedDeque.this.last();
      }

      ConcurrentLinkedDeque.Node<E> nextNode(ConcurrentLinkedDeque.Node<E> var1) {
         return ConcurrentLinkedDeque.this.pred(var1);
      }

      // $FF: synthetic method
      DescendingItr(Object var2) {
         this();
      }
   }

   private class Itr extends ConcurrentLinkedDeque<E>.AbstractItr {
      private Itr() {
         super();
      }

      ConcurrentLinkedDeque.Node<E> startNode() {
         return ConcurrentLinkedDeque.this.first();
      }

      ConcurrentLinkedDeque.Node<E> nextNode(ConcurrentLinkedDeque.Node<E> var1) {
         return ConcurrentLinkedDeque.this.succ(var1);
      }

      // $FF: synthetic method
      Itr(Object var2) {
         this();
      }
   }

   private abstract class AbstractItr implements Iterator<E> {
      private ConcurrentLinkedDeque.Node<E> nextNode;
      private E nextItem;
      private ConcurrentLinkedDeque.Node<E> lastRet;

      abstract ConcurrentLinkedDeque.Node<E> startNode();

      abstract ConcurrentLinkedDeque.Node<E> nextNode(ConcurrentLinkedDeque.Node<E> var1);

      AbstractItr() {
         this.advance();
      }

      private void advance() {
         this.lastRet = this.nextNode;
         ConcurrentLinkedDeque.Node var1 = this.nextNode == null ? this.startNode() : this.nextNode(this.nextNode);

         while(true) {
            if (var1 == null) {
               this.nextNode = null;
               this.nextItem = null;
               break;
            }

            Object var2 = var1.item;
            if (var2 != null) {
               this.nextNode = var1;
               this.nextItem = var2;
               break;
            }

            var1 = this.nextNode(var1);
         }

      }

      public boolean hasNext() {
         return this.nextItem != null;
      }

      public E next() {
         Object var1 = this.nextItem;
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            this.advance();
            return var1;
         }
      }

      public void remove() {
         ConcurrentLinkedDeque.Node var1 = this.lastRet;
         if (var1 == null) {
            throw new IllegalStateException();
         } else {
            var1.item = null;
            ConcurrentLinkedDeque.this.unlink(var1);
            this.lastRet = null;
         }
      }
   }

   static final class Node<E> {
      volatile ConcurrentLinkedDeque.Node<E> prev;
      volatile E item;
      volatile ConcurrentLinkedDeque.Node<E> next;
      private static final Unsafe UNSAFE;
      private static final long prevOffset;
      private static final long itemOffset;
      private static final long nextOffset;

      Node() {
      }

      Node(E var1) {
         UNSAFE.putObject(this, itemOffset, var1);
      }

      boolean casItem(E var1, E var2) {
         return UNSAFE.compareAndSwapObject(this, itemOffset, var1, var2);
      }

      void lazySetNext(ConcurrentLinkedDeque.Node<E> var1) {
         UNSAFE.putOrderedObject(this, nextOffset, var1);
      }

      boolean casNext(ConcurrentLinkedDeque.Node<E> var1, ConcurrentLinkedDeque.Node<E> var2) {
         return UNSAFE.compareAndSwapObject(this, nextOffset, var1, var2);
      }

      void lazySetPrev(ConcurrentLinkedDeque.Node<E> var1) {
         UNSAFE.putOrderedObject(this, prevOffset, var1);
      }

      boolean casPrev(ConcurrentLinkedDeque.Node<E> var1, ConcurrentLinkedDeque.Node<E> var2) {
         return UNSAFE.compareAndSwapObject(this, prevOffset, var1, var2);
      }

      static {
         try {
            UNSAFE = Unsafe.getUnsafe();
            Class var0 = ConcurrentLinkedDeque.Node.class;
            prevOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("prev"));
            itemOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("item"));
            nextOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("next"));
         } catch (Exception var1) {
            throw new Error(var1);
         }
      }
   }
}
