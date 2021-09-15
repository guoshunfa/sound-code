package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import sun.misc.SharedSecrets;
import sun.misc.Unsafe;

public class CopyOnWriteArrayList<E> implements List<E>, RandomAccess, Cloneable, Serializable {
   private static final long serialVersionUID = 8673264195747942595L;
   final transient ReentrantLock lock = new ReentrantLock();
   private transient volatile Object[] array;
   private static final Unsafe UNSAFE;
   private static final long lockOffset;

   final Object[] getArray() {
      return this.array;
   }

   final void setArray(Object[] var1) {
      this.array = var1;
   }

   public CopyOnWriteArrayList() {
      this.setArray(new Object[0]);
   }

   public CopyOnWriteArrayList(Collection<? extends E> var1) {
      Object[] var2;
      if (var1.getClass() == CopyOnWriteArrayList.class) {
         var2 = ((CopyOnWriteArrayList)var1).getArray();
      } else {
         var2 = var1.toArray();
         if (var2.getClass() != Object[].class) {
            var2 = Arrays.copyOf(var2, var2.length, Object[].class);
         }
      }

      this.setArray(var2);
   }

   public CopyOnWriteArrayList(E[] var1) {
      this.setArray(Arrays.copyOf(var1, var1.length, Object[].class));
   }

   public int size() {
      return this.getArray().length;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   private static boolean eq(Object var0, Object var1) {
      return var0 == null ? var1 == null : var0.equals(var1);
   }

   private static int indexOf(Object var0, Object[] var1, int var2, int var3) {
      int var4;
      if (var0 == null) {
         for(var4 = var2; var4 < var3; ++var4) {
            if (var1[var4] == null) {
               return var4;
            }
         }
      } else {
         for(var4 = var2; var4 < var3; ++var4) {
            if (var0.equals(var1[var4])) {
               return var4;
            }
         }
      }

      return -1;
   }

   private static int lastIndexOf(Object var0, Object[] var1, int var2) {
      int var3;
      if (var0 == null) {
         for(var3 = var2; var3 >= 0; --var3) {
            if (var1[var3] == null) {
               return var3;
            }
         }
      } else {
         for(var3 = var2; var3 >= 0; --var3) {
            if (var0.equals(var1[var3])) {
               return var3;
            }
         }
      }

      return -1;
   }

   public boolean contains(Object var1) {
      Object[] var2 = this.getArray();
      return indexOf(var1, var2, 0, var2.length) >= 0;
   }

   public int indexOf(Object var1) {
      Object[] var2 = this.getArray();
      return indexOf(var1, var2, 0, var2.length);
   }

   public int indexOf(E var1, int var2) {
      Object[] var3 = this.getArray();
      return indexOf(var1, var3, var2, var3.length);
   }

   public int lastIndexOf(Object var1) {
      Object[] var2 = this.getArray();
      return lastIndexOf(var1, var2, var2.length - 1);
   }

   public int lastIndexOf(E var1, int var2) {
      Object[] var3 = this.getArray();
      return lastIndexOf(var1, var3, var2);
   }

   public Object clone() {
      try {
         CopyOnWriteArrayList var1 = (CopyOnWriteArrayList)super.clone();
         var1.resetLock();
         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError();
      }
   }

   public Object[] toArray() {
      Object[] var1 = this.getArray();
      return Arrays.copyOf(var1, var1.length);
   }

   public <T> T[] toArray(T[] var1) {
      Object[] var2 = this.getArray();
      int var3 = var2.length;
      if (var1.length < var3) {
         return (Object[])Arrays.copyOf(var2, var3, var1.getClass());
      } else {
         System.arraycopy(var2, 0, var1, 0, var3);
         if (var1.length > var3) {
            var1[var3] = null;
         }

         return var1;
      }
   }

   private E get(Object[] var1, int var2) {
      return var1[var2];
   }

   public E get(int var1) {
      return this.get(this.getArray(), var1);
   }

   public E set(int var1, E var2) {
      ReentrantLock var3 = this.lock;
      var3.lock();

      Object var11;
      try {
         Object[] var4 = this.getArray();
         Object var5 = this.get(var4, var1);
         if (var5 != var2) {
            int var6 = var4.length;
            Object[] var7 = Arrays.copyOf(var4, var6);
            var7[var1] = var2;
            this.setArray(var7);
         } else {
            this.setArray(var4);
         }

         var11 = var5;
      } finally {
         var3.unlock();
      }

      return var11;
   }

   public boolean add(E var1) {
      ReentrantLock var2 = this.lock;
      var2.lock();

      boolean var6;
      try {
         Object[] var3 = this.getArray();
         int var4 = var3.length;
         Object[] var5 = Arrays.copyOf(var3, var4 + 1);
         var5[var4] = var1;
         this.setArray(var5);
         var6 = true;
      } finally {
         var2.unlock();
      }

      return var6;
   }

   public void add(int var1, E var2) {
      ReentrantLock var3 = this.lock;
      var3.lock();

      try {
         Object[] var4 = this.getArray();
         int var5 = var4.length;
         if (var1 > var5 || var1 < 0) {
            throw new IndexOutOfBoundsException("Index: " + var1 + ", Size: " + var5);
         }

         int var7 = var5 - var1;
         Object[] var6;
         if (var7 == 0) {
            var6 = Arrays.copyOf(var4, var5 + 1);
         } else {
            var6 = new Object[var5 + 1];
            System.arraycopy(var4, 0, var6, 0, var1);
            System.arraycopy(var4, var1, var6, var1 + 1, var7);
         }

         var6[var1] = var2;
         this.setArray(var6);
      } finally {
         var3.unlock();
      }

   }

   public E remove(int var1) {
      ReentrantLock var2 = this.lock;
      var2.lock();

      Object var11;
      try {
         Object[] var3 = this.getArray();
         int var4 = var3.length;
         Object var5 = this.get(var3, var1);
         int var6 = var4 - var1 - 1;
         if (var6 == 0) {
            this.setArray(Arrays.copyOf(var3, var4 - 1));
         } else {
            Object[] var7 = new Object[var4 - 1];
            System.arraycopy(var3, 0, var7, 0, var1);
            System.arraycopy(var3, var1 + 1, var7, var1, var6);
            this.setArray(var7);
         }

         var11 = var5;
      } finally {
         var2.unlock();
      }

      return var11;
   }

   public boolean remove(Object var1) {
      Object[] var2 = this.getArray();
      int var3 = indexOf(var1, var2, 0, var2.length);
      return var3 < 0 ? false : this.remove(var1, var2, var3);
   }

   private boolean remove(Object var1, Object[] var2, int var3) {
      ReentrantLock var4 = this.lock;
      var4.lock();

      try {
         Object[] var5 = this.getArray();
         int var6 = var5.length;
         boolean var13;
         if (var2 != var5) {
            int var7 = Math.min(var3, var6);
            int var8 = 0;

            while(true) {
               if (var8 >= var7) {
                  if (var3 >= var6) {
                     var13 = false;
                     return var13;
                  }

                  if (var5[var3] != var1) {
                     var3 = indexOf(var1, var5, var3, var6);
                     if (var3 < 0) {
                        var13 = false;
                        return var13;
                     }
                  }
                  break;
               }

               if (var5[var8] != var2[var8] && eq(var1, var5[var8])) {
                  var3 = var8;
                  break;
               }

               ++var8;
            }
         }

         Object[] var12 = new Object[var6 - 1];
         System.arraycopy(var5, 0, var12, 0, var3);
         System.arraycopy(var5, var3 + 1, var12, var3, var6 - var3 - 1);
         this.setArray(var12);
         var13 = true;
         return var13;
      } finally {
         var4.unlock();
      }
   }

   void removeRange(int var1, int var2) {
      ReentrantLock var3 = this.lock;
      var3.lock();

      try {
         Object[] var4 = this.getArray();
         int var5 = var4.length;
         if (var1 < 0 || var2 > var5 || var2 < var1) {
            throw new IndexOutOfBoundsException();
         }

         int var6 = var5 - (var2 - var1);
         int var7 = var5 - var2;
         if (var7 == 0) {
            this.setArray(Arrays.copyOf(var4, var6));
         } else {
            Object[] var8 = new Object[var6];
            System.arraycopy(var4, 0, var8, 0, var1);
            System.arraycopy(var4, var2, var8, var1, var7);
            this.setArray(var8);
         }
      } finally {
         var3.unlock();
      }

   }

   public boolean addIfAbsent(E var1) {
      Object[] var2 = this.getArray();
      return indexOf(var1, var2, 0, var2.length) >= 0 ? false : this.addIfAbsent(var1, var2);
   }

   private boolean addIfAbsent(E var1, Object[] var2) {
      ReentrantLock var3 = this.lock;
      var3.lock();

      boolean var13;
      try {
         Object[] var4 = this.getArray();
         int var5 = var4.length;
         if (var2 != var4) {
            int var6 = Math.min(var2.length, var5);

            for(int var7 = 0; var7 < var6; ++var7) {
               if (var4[var7] != var2[var7] && eq(var1, var4[var7])) {
                  boolean var8 = false;
                  return var8;
               }
            }

            if (indexOf(var1, var4, var6, var5) >= 0) {
               var13 = false;
               return var13;
            }
         }

         Object[] var12 = Arrays.copyOf(var4, var5 + 1);
         var12[var5] = var1;
         this.setArray(var12);
         var13 = true;
      } finally {
         var3.unlock();
      }

      return var13;
   }

   public boolean containsAll(Collection<?> var1) {
      Object[] var2 = this.getArray();
      int var3 = var2.length;
      Iterator var4 = var1.iterator();

      Object var5;
      do {
         if (!var4.hasNext()) {
            return true;
         }

         var5 = var4.next();
      } while(indexOf(var5, var2, 0, var3) >= 0);

      return false;
   }

   public boolean removeAll(Collection<?> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         ReentrantLock var2 = this.lock;
         var2.lock();

         boolean var12;
         try {
            Object[] var3 = this.getArray();
            int var4 = var3.length;
            if (var4 != 0) {
               int var5 = 0;
               Object[] var6 = new Object[var4];
               int var7 = 0;

               while(true) {
                  if (var7 >= var4) {
                     if (var5 != var4) {
                        this.setArray(Arrays.copyOf(var6, var5));
                        boolean var13 = true;
                        return var13;
                     }
                     break;
                  }

                  Object var8 = var3[var7];
                  if (!var1.contains(var8)) {
                     var6[var5++] = var8;
                  }

                  ++var7;
               }
            }

            var12 = false;
         } finally {
            var2.unlock();
         }

         return var12;
      }
   }

   public boolean retainAll(Collection<?> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         ReentrantLock var2 = this.lock;
         var2.lock();

         try {
            Object[] var3 = this.getArray();
            int var4 = var3.length;
            if (var4 != 0) {
               int var5 = 0;
               Object[] var6 = new Object[var4];
               int var7 = 0;

               while(true) {
                  if (var7 >= var4) {
                     if (var5 != var4) {
                        this.setArray(Arrays.copyOf(var6, var5));
                        boolean var13 = true;
                        return var13;
                     }
                     break;
                  }

                  Object var8 = var3[var7];
                  if (var1.contains(var8)) {
                     var6[var5++] = var8;
                  }

                  ++var7;
               }
            }

            boolean var12 = false;
            return var12;
         } finally {
            var2.unlock();
         }
      }
   }

   public int addAllAbsent(Collection<? extends E> var1) {
      Object[] var2 = var1.toArray();
      if (var2.length == 0) {
         return 0;
      } else {
         ReentrantLock var3 = this.lock;
         var3.lock();

         try {
            Object[] var4 = this.getArray();
            int var5 = var4.length;
            int var6 = 0;

            int var7;
            for(var7 = 0; var7 < var2.length; ++var7) {
               Object var8 = var2[var7];
               if (indexOf(var8, var4, 0, var5) < 0 && indexOf(var8, var2, 0, var6) < 0) {
                  var2[var6++] = var8;
               }
            }

            if (var6 > 0) {
               Object[] var12 = Arrays.copyOf(var4, var5 + var6);
               System.arraycopy(var2, 0, var12, var5, var6);
               this.setArray(var12);
            }

            var7 = var6;
            return var7;
         } finally {
            var3.unlock();
         }
      }
   }

   public void clear() {
      ReentrantLock var1 = this.lock;
      var1.lock();

      try {
         this.setArray(new Object[0]);
      } finally {
         var1.unlock();
      }

   }

   public boolean addAll(Collection<? extends E> var1) {
      Object[] var2 = var1.getClass() == CopyOnWriteArrayList.class ? ((CopyOnWriteArrayList)var1).getArray() : var1.toArray();
      if (var2.length == 0) {
         return false;
      } else {
         ReentrantLock var3 = this.lock;
         var3.lock();

         boolean var10;
         try {
            Object[] var4 = this.getArray();
            int var5 = var4.length;
            if (var5 == 0 && var2.getClass() == Object[].class) {
               this.setArray(var2);
            } else {
               Object[] var6 = Arrays.copyOf(var4, var5 + var2.length);
               System.arraycopy(var2, 0, var6, var5, var2.length);
               this.setArray(var6);
            }

            var10 = true;
         } finally {
            var3.unlock();
         }

         return var10;
      }
   }

   public boolean addAll(int var1, Collection<? extends E> var2) {
      Object[] var3 = var2.toArray();
      ReentrantLock var4 = this.lock;
      var4.lock();

      boolean var9;
      try {
         Object[] var5 = this.getArray();
         int var6 = var5.length;
         if (var1 > var6 || var1 < 0) {
            throw new IndexOutOfBoundsException("Index: " + var1 + ", Size: " + var6);
         }

         if (var3.length == 0) {
            boolean var13 = false;
            return var13;
         }

         int var7 = var6 - var1;
         Object[] var8;
         if (var7 == 0) {
            var8 = Arrays.copyOf(var5, var6 + var3.length);
         } else {
            var8 = new Object[var6 + var3.length];
            System.arraycopy(var5, 0, var8, 0, var1);
            System.arraycopy(var5, var1, var8, var1 + var3.length, var7);
         }

         System.arraycopy(var3, 0, var8, var1, var3.length);
         this.setArray(var8);
         var9 = true;
      } finally {
         var4.unlock();
      }

      return var9;
   }

   public void forEach(Consumer<? super E> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         Object[] var2 = this.getArray();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Object var5 = var2[var4];
            var1.accept(var5);
         }

      }
   }

   public boolean removeIf(Predicate<? super E> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         ReentrantLock var2 = this.lock;
         var2.lock();

         try {
            Object[] var3 = this.getArray();
            int var4 = var3.length;
            if (var4 != 0) {
               int var5 = 0;
               Object[] var6 = new Object[var4];
               int var7 = 0;

               while(true) {
                  if (var7 >= var4) {
                     if (var5 != var4) {
                        this.setArray(Arrays.copyOf(var6, var5));
                        boolean var13 = true;
                        return var13;
                     }
                     break;
                  }

                  Object var8 = var3[var7];
                  if (!var1.test(var8)) {
                     var6[var5++] = var8;
                  }

                  ++var7;
               }
            }

            boolean var12 = false;
            return var12;
         } finally {
            var2.unlock();
         }
      }
   }

   public void replaceAll(UnaryOperator<E> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         ReentrantLock var2 = this.lock;
         var2.lock();

         try {
            Object[] var3 = this.getArray();
            int var4 = var3.length;
            Object[] var5 = Arrays.copyOf(var3, var4);

            for(int var6 = 0; var6 < var4; ++var6) {
               Object var7 = var3[var6];
               var5[var6] = var1.apply(var7);
            }

            this.setArray(var5);
         } finally {
            var2.unlock();
         }
      }
   }

   public void sort(Comparator<? super E> var1) {
      ReentrantLock var2 = this.lock;
      var2.lock();

      try {
         Object[] var3 = this.getArray();
         Object[] var4 = Arrays.copyOf(var3, var3.length);
         Object[] var5 = (Object[])var4;
         Arrays.sort(var5, var1);
         this.setArray(var4);
      } finally {
         var2.unlock();
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      Object[] var2 = this.getArray();
      var1.writeInt(var2.length);
      Object[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Object var6 = var3[var5];
         var1.writeObject(var6);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.resetLock();
      int var2 = var1.readInt();
      SharedSecrets.getJavaOISAccess().checkArray(var1, Object[].class, var2);
      Object[] var3 = new Object[var2];

      for(int var4 = 0; var4 < var2; ++var4) {
         var3[var4] = var1.readObject();
      }

      this.setArray(var3);
   }

   public String toString() {
      return Arrays.toString(this.getArray());
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof List)) {
         return false;
      } else {
         List var2 = (List)((List)var1);
         Iterator var3 = var2.iterator();
         Object[] var4 = this.getArray();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            if (!var3.hasNext() || !eq(var4[var6], var3.next())) {
               return false;
            }
         }

         if (var3.hasNext()) {
            return false;
         } else {
            return true;
         }
      }
   }

   public int hashCode() {
      int var1 = 1;
      Object[] var2 = this.getArray();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Object var5 = var2[var4];
         var1 = 31 * var1 + (var5 == null ? 0 : var5.hashCode());
      }

      return var1;
   }

   public Iterator<E> iterator() {
      return new CopyOnWriteArrayList.COWIterator(this.getArray(), 0);
   }

   public ListIterator<E> listIterator() {
      return new CopyOnWriteArrayList.COWIterator(this.getArray(), 0);
   }

   public ListIterator<E> listIterator(int var1) {
      Object[] var2 = this.getArray();
      int var3 = var2.length;
      if (var1 >= 0 && var1 <= var3) {
         return new CopyOnWriteArrayList.COWIterator(var2, var1);
      } else {
         throw new IndexOutOfBoundsException("Index: " + var1);
      }
   }

   public Spliterator<E> spliterator() {
      return Spliterators.spliterator((Object[])this.getArray(), 1040);
   }

   public List<E> subList(int var1, int var2) {
      ReentrantLock var3 = this.lock;
      var3.lock();

      CopyOnWriteArrayList.COWSubList var6;
      try {
         Object[] var4 = this.getArray();
         int var5 = var4.length;
         if (var1 < 0 || var2 > var5 || var1 > var2) {
            throw new IndexOutOfBoundsException();
         }

         var6 = new CopyOnWriteArrayList.COWSubList(this, var1, var2);
      } finally {
         var3.unlock();
      }

      return var6;
   }

   private void resetLock() {
      UNSAFE.putObjectVolatile(this, lockOffset, new ReentrantLock());
   }

   static {
      try {
         UNSAFE = Unsafe.getUnsafe();
         Class var0 = CopyOnWriteArrayList.class;
         lockOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("lock"));
      } catch (Exception var1) {
         throw new Error(var1);
      }
   }

   private static class COWSubListIterator<E> implements ListIterator<E> {
      private final ListIterator<E> it;
      private final int offset;
      private final int size;

      COWSubListIterator(List<E> var1, int var2, int var3, int var4) {
         this.offset = var3;
         this.size = var4;
         this.it = var1.listIterator(var2 + var3);
      }

      public boolean hasNext() {
         return this.nextIndex() < this.size;
      }

      public E next() {
         if (this.hasNext()) {
            return this.it.next();
         } else {
            throw new NoSuchElementException();
         }
      }

      public boolean hasPrevious() {
         return this.previousIndex() >= 0;
      }

      public E previous() {
         if (this.hasPrevious()) {
            return this.it.previous();
         } else {
            throw new NoSuchElementException();
         }
      }

      public int nextIndex() {
         return this.it.nextIndex() - this.offset;
      }

      public int previousIndex() {
         return this.it.previousIndex() - this.offset;
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }

      public void set(E var1) {
         throw new UnsupportedOperationException();
      }

      public void add(E var1) {
         throw new UnsupportedOperationException();
      }

      public void forEachRemaining(Consumer<? super E> var1) {
         Objects.requireNonNull(var1);
         int var2 = this.size;
         ListIterator var3 = this.it;

         while(this.nextIndex() < var2) {
            var1.accept(var3.next());
         }

      }
   }

   private static class COWSubList<E> extends AbstractList<E> implements RandomAccess {
      private final CopyOnWriteArrayList<E> l;
      private final int offset;
      private int size;
      private Object[] expectedArray;

      COWSubList(CopyOnWriteArrayList<E> var1, int var2, int var3) {
         this.l = var1;
         this.expectedArray = this.l.getArray();
         this.offset = var2;
         this.size = var3 - var2;
      }

      private void checkForComodification() {
         if (this.l.getArray() != this.expectedArray) {
            throw new ConcurrentModificationException();
         }
      }

      private void rangeCheck(int var1) {
         if (var1 < 0 || var1 >= this.size) {
            throw new IndexOutOfBoundsException("Index: " + var1 + ",Size: " + this.size);
         }
      }

      public E set(int var1, E var2) {
         ReentrantLock var3 = this.l.lock;
         var3.lock();

         Object var5;
         try {
            this.rangeCheck(var1);
            this.checkForComodification();
            Object var4 = this.l.set(var1 + this.offset, var2);
            this.expectedArray = this.l.getArray();
            var5 = var4;
         } finally {
            var3.unlock();
         }

         return var5;
      }

      public E get(int var1) {
         ReentrantLock var2 = this.l.lock;
         var2.lock();

         Object var3;
         try {
            this.rangeCheck(var1);
            this.checkForComodification();
            var3 = this.l.get(var1 + this.offset);
         } finally {
            var2.unlock();
         }

         return var3;
      }

      public int size() {
         ReentrantLock var1 = this.l.lock;
         var1.lock();

         int var2;
         try {
            this.checkForComodification();
            var2 = this.size;
         } finally {
            var1.unlock();
         }

         return var2;
      }

      public void add(int var1, E var2) {
         ReentrantLock var3 = this.l.lock;
         var3.lock();

         try {
            this.checkForComodification();
            if (var1 < 0 || var1 > this.size) {
               throw new IndexOutOfBoundsException();
            }

            this.l.add(var1 + this.offset, var2);
            this.expectedArray = this.l.getArray();
            ++this.size;
         } finally {
            var3.unlock();
         }

      }

      public void clear() {
         ReentrantLock var1 = this.l.lock;
         var1.lock();

         try {
            this.checkForComodification();
            this.l.removeRange(this.offset, this.offset + this.size);
            this.expectedArray = this.l.getArray();
            this.size = 0;
         } finally {
            var1.unlock();
         }

      }

      public E remove(int var1) {
         ReentrantLock var2 = this.l.lock;
         var2.lock();

         Object var4;
         try {
            this.rangeCheck(var1);
            this.checkForComodification();
            Object var3 = this.l.remove(var1 + this.offset);
            this.expectedArray = this.l.getArray();
            --this.size;
            var4 = var3;
         } finally {
            var2.unlock();
         }

         return var4;
      }

      public boolean remove(Object var1) {
         int var2 = this.indexOf(var1);
         if (var2 == -1) {
            return false;
         } else {
            this.remove(var2);
            return true;
         }
      }

      public Iterator<E> iterator() {
         ReentrantLock var1 = this.l.lock;
         var1.lock();

         CopyOnWriteArrayList.COWSubListIterator var2;
         try {
            this.checkForComodification();
            var2 = new CopyOnWriteArrayList.COWSubListIterator(this.l, 0, this.offset, this.size);
         } finally {
            var1.unlock();
         }

         return var2;
      }

      public ListIterator<E> listIterator(int var1) {
         ReentrantLock var2 = this.l.lock;
         var2.lock();

         CopyOnWriteArrayList.COWSubListIterator var3;
         try {
            this.checkForComodification();
            if (var1 < 0 || var1 > this.size) {
               throw new IndexOutOfBoundsException("Index: " + var1 + ", Size: " + this.size);
            }

            var3 = new CopyOnWriteArrayList.COWSubListIterator(this.l, var1, this.offset, this.size);
         } finally {
            var2.unlock();
         }

         return var3;
      }

      public List<E> subList(int var1, int var2) {
         ReentrantLock var3 = this.l.lock;
         var3.lock();

         CopyOnWriteArrayList.COWSubList var4;
         try {
            this.checkForComodification();
            if (var1 < 0 || var2 > this.size || var1 > var2) {
               throw new IndexOutOfBoundsException();
            }

            var4 = new CopyOnWriteArrayList.COWSubList(this.l, var1 + this.offset, var2 + this.offset);
         } finally {
            var3.unlock();
         }

         return var4;
      }

      public void forEach(Consumer<? super E> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            int var2 = this.offset;
            int var3 = this.offset + this.size;
            Object[] var4 = this.expectedArray;
            if (this.l.getArray() != var4) {
               throw new ConcurrentModificationException();
            } else if (var2 >= 0 && var3 <= var4.length) {
               for(int var5 = var2; var5 < var3; ++var5) {
                  Object var6 = var4[var5];
                  var1.accept(var6);
               }

            } else {
               throw new IndexOutOfBoundsException();
            }
         }
      }

      public void replaceAll(UnaryOperator<E> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            ReentrantLock var2 = this.l.lock;
            var2.lock();

            try {
               int var3 = this.offset;
               int var4 = this.offset + this.size;
               Object[] var5 = this.expectedArray;
               if (this.l.getArray() != var5) {
                  throw new ConcurrentModificationException();
               } else {
                  int var6 = var5.length;
                  if (var3 < 0 || var4 > var6) {
                     throw new IndexOutOfBoundsException();
                  } else {
                     Object[] var7 = Arrays.copyOf(var5, var6);

                     for(int var8 = var3; var8 < var4; ++var8) {
                        Object var9 = var5[var8];
                        var7[var8] = var1.apply(var9);
                     }

                     this.l.setArray(this.expectedArray = var7);
                  }
               }
            } finally {
               var2.unlock();
            }
         }
      }

      public void sort(Comparator<? super E> var1) {
         ReentrantLock var2 = this.l.lock;
         var2.lock();

         try {
            int var3 = this.offset;
            int var4 = this.offset + this.size;
            Object[] var5 = this.expectedArray;
            if (this.l.getArray() != var5) {
               throw new ConcurrentModificationException();
            }

            int var6 = var5.length;
            if (var3 < 0 || var4 > var6) {
               throw new IndexOutOfBoundsException();
            }

            Object[] var7 = Arrays.copyOf(var5, var6);
            Object[] var8 = (Object[])var7;
            Arrays.sort(var8, var3, var4, var1);
            this.l.setArray(this.expectedArray = var7);
         } finally {
            var2.unlock();
         }

      }

      public boolean removeAll(Collection<?> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            boolean var2 = false;
            ReentrantLock var3 = this.l.lock;
            var3.lock();

            try {
               int var4 = this.size;
               if (var4 > 0) {
                  int var5 = this.offset;
                  int var6 = this.offset + var4;
                  Object[] var7 = this.expectedArray;
                  if (this.l.getArray() != var7) {
                     throw new ConcurrentModificationException();
                  }

                  int var8 = var7.length;
                  if (var5 < 0 || var6 > var8) {
                     throw new IndexOutOfBoundsException();
                  }

                  int var9 = 0;
                  Object[] var10 = new Object[var4];

                  for(int var11 = var5; var11 < var6; ++var11) {
                     Object var12 = var7[var11];
                     if (!var1.contains(var12)) {
                        var10[var9++] = var12;
                     }
                  }

                  if (var9 != var4) {
                     Object[] var16 = new Object[var8 - var4 + var9];
                     System.arraycopy(var7, 0, var16, 0, var5);
                     System.arraycopy(var10, 0, var16, var5, var9);
                     System.arraycopy(var7, var6, var16, var5 + var9, var8 - var6);
                     this.size = var9;
                     var2 = true;
                     this.l.setArray(this.expectedArray = var16);
                  }
               }
            } finally {
               var3.unlock();
            }

            return var2;
         }
      }

      public boolean retainAll(Collection<?> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            boolean var2 = false;
            ReentrantLock var3 = this.l.lock;
            var3.lock();

            try {
               int var4 = this.size;
               if (var4 > 0) {
                  int var5 = this.offset;
                  int var6 = this.offset + var4;
                  Object[] var7 = this.expectedArray;
                  if (this.l.getArray() != var7) {
                     throw new ConcurrentModificationException();
                  }

                  int var8 = var7.length;
                  if (var5 < 0 || var6 > var8) {
                     throw new IndexOutOfBoundsException();
                  }

                  int var9 = 0;
                  Object[] var10 = new Object[var4];

                  for(int var11 = var5; var11 < var6; ++var11) {
                     Object var12 = var7[var11];
                     if (var1.contains(var12)) {
                        var10[var9++] = var12;
                     }
                  }

                  if (var9 != var4) {
                     Object[] var16 = new Object[var8 - var4 + var9];
                     System.arraycopy(var7, 0, var16, 0, var5);
                     System.arraycopy(var10, 0, var16, var5, var9);
                     System.arraycopy(var7, var6, var16, var5 + var9, var8 - var6);
                     this.size = var9;
                     var2 = true;
                     this.l.setArray(this.expectedArray = var16);
                  }
               }
            } finally {
               var3.unlock();
            }

            return var2;
         }
      }

      public boolean removeIf(Predicate<? super E> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            boolean var2 = false;
            ReentrantLock var3 = this.l.lock;
            var3.lock();

            try {
               int var4 = this.size;
               if (var4 > 0) {
                  int var5 = this.offset;
                  int var6 = this.offset + var4;
                  Object[] var7 = this.expectedArray;
                  if (this.l.getArray() != var7) {
                     throw new ConcurrentModificationException();
                  }

                  int var8 = var7.length;
                  if (var5 < 0 || var6 > var8) {
                     throw new IndexOutOfBoundsException();
                  }

                  int var9 = 0;
                  Object[] var10 = new Object[var4];

                  for(int var11 = var5; var11 < var6; ++var11) {
                     Object var12 = var7[var11];
                     if (!var1.test(var12)) {
                        var10[var9++] = var12;
                     }
                  }

                  if (var9 != var4) {
                     Object[] var16 = new Object[var8 - var4 + var9];
                     System.arraycopy(var7, 0, var16, 0, var5);
                     System.arraycopy(var10, 0, var16, var5, var9);
                     System.arraycopy(var7, var6, var16, var5 + var9, var8 - var6);
                     this.size = var9;
                     var2 = true;
                     this.l.setArray(this.expectedArray = var16);
                  }
               }
            } finally {
               var3.unlock();
            }

            return var2;
         }
      }

      public Spliterator<E> spliterator() {
         int var1 = this.offset;
         int var2 = this.offset + this.size;
         Object[] var3 = this.expectedArray;
         if (this.l.getArray() != var3) {
            throw new ConcurrentModificationException();
         } else if (var1 >= 0 && var2 <= var3.length) {
            return Spliterators.spliterator((Object[])var3, var1, var2, 1040);
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   static final class COWIterator<E> implements ListIterator<E> {
      private final Object[] snapshot;
      private int cursor;

      private COWIterator(Object[] var1, int var2) {
         this.cursor = var2;
         this.snapshot = var1;
      }

      public boolean hasNext() {
         return this.cursor < this.snapshot.length;
      }

      public boolean hasPrevious() {
         return this.cursor > 0;
      }

      public E next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            return this.snapshot[this.cursor++];
         }
      }

      public E previous() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            return this.snapshot[--this.cursor];
         }
      }

      public int nextIndex() {
         return this.cursor;
      }

      public int previousIndex() {
         return this.cursor - 1;
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }

      public void set(E var1) {
         throw new UnsupportedOperationException();
      }

      public void add(E var1) {
         throw new UnsupportedOperationException();
      }

      public void forEachRemaining(Consumer<? super E> var1) {
         Objects.requireNonNull(var1);
         Object[] var2 = this.snapshot;
         int var3 = var2.length;

         for(int var4 = this.cursor; var4 < var3; ++var4) {
            Object var5 = var2[var4];
            var1.accept(var5);
         }

         this.cursor = var3;
      }

      // $FF: synthetic method
      COWIterator(Object[] var1, int var2, Object var3) {
         this(var1, var2);
      }
   }
}
