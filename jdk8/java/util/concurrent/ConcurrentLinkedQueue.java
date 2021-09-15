package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import sun.misc.Unsafe;

public class ConcurrentLinkedQueue<E> extends AbstractQueue<E> implements Queue<E>, Serializable {
   private static final long serialVersionUID = 196745693267521676L;
   private transient volatile ConcurrentLinkedQueue.Node<E> head;
   private transient volatile ConcurrentLinkedQueue.Node<E> tail;
   private static final Unsafe UNSAFE;
   private static final long headOffset;
   private static final long tailOffset;

   public ConcurrentLinkedQueue() {
      this.head = this.tail = new ConcurrentLinkedQueue.Node((Object)null);
   }

   public ConcurrentLinkedQueue(Collection<? extends E> var1) {
      ConcurrentLinkedQueue.Node var2 = null;
      ConcurrentLinkedQueue.Node var3 = null;
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         Object var5 = var4.next();
         checkNotNull(var5);
         ConcurrentLinkedQueue.Node var6 = new ConcurrentLinkedQueue.Node(var5);
         if (var2 == null) {
            var3 = var6;
            var2 = var6;
         } else {
            var3.lazySetNext(var6);
            var3 = var6;
         }
      }

      if (var2 == null) {
         var2 = var3 = new ConcurrentLinkedQueue.Node((Object)null);
      }

      this.head = var2;
      this.tail = var3;
   }

   public boolean add(E var1) {
      return this.offer(var1);
   }

   final void updateHead(ConcurrentLinkedQueue.Node<E> var1, ConcurrentLinkedQueue.Node<E> var2) {
      if (var1 != var2 && this.casHead(var1, var2)) {
         var1.lazySetNext(var1);
      }

   }

   final ConcurrentLinkedQueue.Node<E> succ(ConcurrentLinkedQueue.Node<E> var1) {
      ConcurrentLinkedQueue.Node var2 = var1.next;
      return var1 == var2 ? this.head : var2;
   }

   public boolean offer(E var1) {
      checkNotNull(var1);
      ConcurrentLinkedQueue.Node var2 = new ConcurrentLinkedQueue.Node(var1);
      ConcurrentLinkedQueue.Node var3 = this.tail;
      ConcurrentLinkedQueue.Node var4 = var3;

      do {
         while(true) {
            ConcurrentLinkedQueue.Node var5 = var4.next;
            if (var5 == null) {
               break;
            }

            if (var4 == var5) {
               var4 = var3 != (var3 = this.tail) ? var3 : this.head;
            } else {
               var4 = var4 != var3 && var3 != (var3 = this.tail) ? var3 : var5;
            }
         }
      } while(!var4.casNext((ConcurrentLinkedQueue.Node)null, var2));

      if (var4 != var3) {
         this.casTail(var3, var2);
      }

      return true;
   }

   public E poll() {
      while(true) {
         ConcurrentLinkedQueue.Node var1 = this.head;
         ConcurrentLinkedQueue.Node var2 = var1;

         while(true) {
            Object var4 = var2.item;
            ConcurrentLinkedQueue.Node var3;
            if (var4 != null && var2.casItem(var4, (Object)null)) {
               if (var2 != var1) {
                  this.updateHead(var1, (var3 = var2.next) != null ? var3 : var2);
               }

               return var4;
            }

            if ((var3 = var2.next) == null) {
               this.updateHead(var1, var2);
               return null;
            }

            if (var2 == var3) {
               break;
            }

            var2 = var3;
         }
      }
   }

   public E peek() {
      while(true) {
         ConcurrentLinkedQueue.Node var1 = this.head;
         ConcurrentLinkedQueue.Node var2 = var1;

         while(true) {
            Object var4 = var2.item;
            ConcurrentLinkedQueue.Node var3;
            if (var4 != null || (var3 = var2.next) == null) {
               this.updateHead(var1, var2);
               return var4;
            }

            if (var2 == var3) {
               break;
            }

            var2 = var3;
         }
      }
   }

   ConcurrentLinkedQueue.Node<E> first() {
      while(true) {
         ConcurrentLinkedQueue.Node var1 = this.head;
         ConcurrentLinkedQueue.Node var2 = var1;

         while(true) {
            boolean var4 = var2.item != null;
            ConcurrentLinkedQueue.Node var3;
            if (var4 || (var3 = var2.next) == null) {
               this.updateHead(var1, var2);
               return var4 ? var2 : null;
            }

            if (var2 == var3) {
               break;
            }

            var2 = var3;
         }
      }
   }

   public boolean isEmpty() {
      return this.first() == null;
   }

   public int size() {
      int var1 = 0;

      for(ConcurrentLinkedQueue.Node var2 = this.first(); var2 != null; var2 = this.succ(var2)) {
         if (var2.item != null) {
            ++var1;
            if (var1 == Integer.MAX_VALUE) {
               break;
            }
         }
      }

      return var1;
   }

   public boolean contains(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         for(ConcurrentLinkedQueue.Node var2 = this.first(); var2 != null; var2 = this.succ(var2)) {
            Object var3 = var2.item;
            if (var3 != null && var1.equals(var3)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean remove(Object var1) {
      if (var1 != null) {
         ConcurrentLinkedQueue.Node var3 = null;

         ConcurrentLinkedQueue.Node var2;
         for(ConcurrentLinkedQueue.Node var4 = this.first(); var4 != null; var4 = var2) {
            label36: {
               boolean var5 = false;
               Object var6 = var4.item;
               if (var6 != null) {
                  if (!var1.equals(var6)) {
                     var2 = this.succ(var4);
                     break label36;
                  }

                  var5 = var4.casItem(var6, (Object)null);
               }

               var2 = this.succ(var4);
               if (var3 != null && var2 != null) {
                  var3.casNext(var4, var2);
               }

               if (var5) {
                  return true;
               }
            }

            var3 = var4;
         }
      }

      return false;
   }

   public boolean addAll(Collection<? extends E> var1) {
      if (var1 == this) {
         throw new IllegalArgumentException();
      } else {
         ConcurrentLinkedQueue.Node var2 = null;
         ConcurrentLinkedQueue.Node var3 = null;
         Iterator var4 = var1.iterator();

         ConcurrentLinkedQueue.Node var6;
         while(var4.hasNext()) {
            Object var5 = var4.next();
            checkNotNull(var5);
            var6 = new ConcurrentLinkedQueue.Node(var5);
            if (var2 == null) {
               var3 = var6;
               var2 = var6;
            } else {
               var3.lazySetNext(var6);
               var3 = var6;
            }
         }

         if (var2 == null) {
            return false;
         } else {
            ConcurrentLinkedQueue.Node var7 = this.tail;
            ConcurrentLinkedQueue.Node var8 = var7;

            do {
               while(true) {
                  var6 = var8.next;
                  if (var6 == null) {
                     break;
                  }

                  if (var8 == var6) {
                     var8 = var7 != (var7 = this.tail) ? var7 : this.head;
                  } else {
                     var8 = var8 != var7 && var7 != (var7 = this.tail) ? var7 : var6;
                  }
               }
            } while(!var8.casNext((ConcurrentLinkedQueue.Node)null, var2));

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

   public Object[] toArray() {
      ArrayList var1 = new ArrayList();

      for(ConcurrentLinkedQueue.Node var2 = this.first(); var2 != null; var2 = this.succ(var2)) {
         Object var3 = var2.item;
         if (var3 != null) {
            var1.add(var3);
         }
      }

      return var1.toArray();
   }

   public <T> T[] toArray(T[] var1) {
      int var2 = 0;

      ConcurrentLinkedQueue.Node var3;
      for(var3 = this.first(); var3 != null && var2 < var1.length; var3 = this.succ(var3)) {
         Object var4 = var3.item;
         if (var4 != null) {
            var1[var2++] = var4;
         }
      }

      if (var3 == null) {
         if (var2 < var1.length) {
            var1[var2] = null;
         }

         return var1;
      } else {
         ArrayList var7 = new ArrayList();

         for(ConcurrentLinkedQueue.Node var5 = this.first(); var5 != null; var5 = this.succ(var5)) {
            Object var6 = var5.item;
            if (var6 != null) {
               var7.add(var6);
            }
         }

         return var7.toArray(var1);
      }
   }

   public Iterator<E> iterator() {
      return new ConcurrentLinkedQueue.Itr();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(ConcurrentLinkedQueue.Node var2 = this.first(); var2 != null; var2 = this.succ(var2)) {
         Object var3 = var2.item;
         if (var3 != null) {
            var1.writeObject(var3);
         }
      }

      var1.writeObject((Object)null);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      ConcurrentLinkedQueue.Node var2 = null;
      ConcurrentLinkedQueue.Node var3 = null;

      Object var4;
      while((var4 = var1.readObject()) != null) {
         ConcurrentLinkedQueue.Node var5 = new ConcurrentLinkedQueue.Node(var4);
         if (var2 == null) {
            var3 = var5;
            var2 = var5;
         } else {
            var3.lazySetNext(var5);
            var3 = var5;
         }
      }

      if (var2 == null) {
         var2 = var3 = new ConcurrentLinkedQueue.Node((Object)null);
      }

      this.head = var2;
      this.tail = var3;
   }

   public Spliterator<E> spliterator() {
      return new ConcurrentLinkedQueue.CLQSpliterator(this);
   }

   private static void checkNotNull(Object var0) {
      if (var0 == null) {
         throw new NullPointerException();
      }
   }

   private boolean casTail(ConcurrentLinkedQueue.Node<E> var1, ConcurrentLinkedQueue.Node<E> var2) {
      return UNSAFE.compareAndSwapObject(this, tailOffset, var1, var2);
   }

   private boolean casHead(ConcurrentLinkedQueue.Node<E> var1, ConcurrentLinkedQueue.Node<E> var2) {
      return UNSAFE.compareAndSwapObject(this, headOffset, var1, var2);
   }

   static {
      try {
         UNSAFE = Unsafe.getUnsafe();
         Class var0 = ConcurrentLinkedQueue.class;
         headOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("head"));
         tailOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("tail"));
      } catch (Exception var1) {
         throw new Error(var1);
      }
   }

   static final class CLQSpliterator<E> implements Spliterator<E> {
      static final int MAX_BATCH = 33554432;
      final ConcurrentLinkedQueue<E> queue;
      ConcurrentLinkedQueue.Node<E> current;
      int batch;
      boolean exhausted;

      CLQSpliterator(ConcurrentLinkedQueue<E> var1) {
         this.queue = var1;
      }

      public Spliterator<E> trySplit() {
         ConcurrentLinkedQueue var2 = this.queue;
         int var3 = this.batch;
         int var4 = var3 <= 0 ? 1 : (var3 >= 33554432 ? 33554432 : var3 + 1);
         ConcurrentLinkedQueue.Node var1;
         if (!this.exhausted && ((var1 = this.current) != null || (var1 = var2.first()) != null) && var1.next != null) {
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

         return null;
      }

      public void forEachRemaining(Consumer<? super E> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            ConcurrentLinkedQueue var3 = this.queue;
            ConcurrentLinkedQueue.Node var2;
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
            ConcurrentLinkedQueue var3 = this.queue;
            ConcurrentLinkedQueue.Node var2;
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

   private class Itr implements Iterator<E> {
      private ConcurrentLinkedQueue.Node<E> nextNode;
      private E nextItem;
      private ConcurrentLinkedQueue.Node<E> lastRet;

      Itr() {
         this.advance();
      }

      private E advance() {
         this.lastRet = this.nextNode;
         Object var1 = this.nextItem;
         ConcurrentLinkedQueue.Node var2;
         ConcurrentLinkedQueue.Node var3;
         if (this.nextNode == null) {
            var3 = ConcurrentLinkedQueue.this.first();
            var2 = null;
         } else {
            var2 = this.nextNode;
            var3 = ConcurrentLinkedQueue.this.succ(this.nextNode);
         }

         ConcurrentLinkedQueue.Node var5;
         for(; var3 != null; var3 = var5) {
            Object var4 = var3.item;
            if (var4 != null) {
               this.nextNode = var3;
               this.nextItem = var4;
               return var1;
            }

            var5 = ConcurrentLinkedQueue.this.succ(var3);
            if (var2 != null && var5 != null) {
               var2.casNext(var3, var5);
            }
         }

         this.nextNode = null;
         this.nextItem = null;
         return var1;
      }

      public boolean hasNext() {
         return this.nextNode != null;
      }

      public E next() {
         if (this.nextNode == null) {
            throw new NoSuchElementException();
         } else {
            return this.advance();
         }
      }

      public void remove() {
         ConcurrentLinkedQueue.Node var1 = this.lastRet;
         if (var1 == null) {
            throw new IllegalStateException();
         } else {
            var1.item = null;
            this.lastRet = null;
         }
      }
   }

   private static class Node<E> {
      volatile E item;
      volatile ConcurrentLinkedQueue.Node<E> next;
      private static final Unsafe UNSAFE;
      private static final long itemOffset;
      private static final long nextOffset;

      Node(E var1) {
         UNSAFE.putObject(this, itemOffset, var1);
      }

      boolean casItem(E var1, E var2) {
         return UNSAFE.compareAndSwapObject(this, itemOffset, var1, var2);
      }

      void lazySetNext(ConcurrentLinkedQueue.Node<E> var1) {
         UNSAFE.putOrderedObject(this, nextOffset, var1);
      }

      boolean casNext(ConcurrentLinkedQueue.Node<E> var1, ConcurrentLinkedQueue.Node<E> var2) {
         return UNSAFE.compareAndSwapObject(this, nextOffset, var1, var2);
      }

      static {
         try {
            UNSAFE = Unsafe.getUnsafe();
            Class var0 = ConcurrentLinkedQueue.Node.class;
            itemOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("item"));
            nextOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("next"));
         } catch (Exception var1) {
            throw new Error(var1);
         }
      }
   }
}
