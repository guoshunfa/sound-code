package sun.awt.util;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

public class IdentityArrayList<E> extends AbstractList<E> implements List<E>, RandomAccess {
   private transient Object[] elementData;
   private int size;

   public IdentityArrayList(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Illegal Capacity: " + var1);
      } else {
         this.elementData = new Object[var1];
      }
   }

   public IdentityArrayList() {
      this(10);
   }

   public IdentityArrayList(Collection<? extends E> var1) {
      this.elementData = var1.toArray();
      this.size = this.elementData.length;
      if (this.elementData.getClass() != Object[].class) {
         this.elementData = Arrays.copyOf(this.elementData, this.size, Object[].class);
      }

   }

   public void trimToSize() {
      ++this.modCount;
      int var1 = this.elementData.length;
      if (this.size < var1) {
         this.elementData = Arrays.copyOf(this.elementData, this.size);
      }

   }

   public void ensureCapacity(int var1) {
      ++this.modCount;
      int var2 = this.elementData.length;
      if (var1 > var2) {
         Object[] var3 = this.elementData;
         int var4 = var2 * 3 / 2 + 1;
         if (var4 < var1) {
            var4 = var1;
         }

         this.elementData = Arrays.copyOf(this.elementData, var4);
      }

   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public boolean contains(Object var1) {
      return this.indexOf(var1) >= 0;
   }

   public int indexOf(Object var1) {
      for(int var2 = 0; var2 < this.size; ++var2) {
         if (var1 == this.elementData[var2]) {
            return var2;
         }
      }

      return -1;
   }

   public int lastIndexOf(Object var1) {
      for(int var2 = this.size - 1; var2 >= 0; --var2) {
         if (var1 == this.elementData[var2]) {
            return var2;
         }
      }

      return -1;
   }

   public Object[] toArray() {
      return Arrays.copyOf(this.elementData, this.size);
   }

   public <T> T[] toArray(T[] var1) {
      if (var1.length < this.size) {
         return (Object[])Arrays.copyOf(this.elementData, this.size, var1.getClass());
      } else {
         System.arraycopy(this.elementData, 0, var1, 0, this.size);
         if (var1.length > this.size) {
            var1[this.size] = null;
         }

         return var1;
      }
   }

   public E get(int var1) {
      this.rangeCheck(var1);
      return this.elementData[var1];
   }

   public E set(int var1, E var2) {
      this.rangeCheck(var1);
      Object var3 = this.elementData[var1];
      this.elementData[var1] = var2;
      return var3;
   }

   public boolean add(E var1) {
      this.ensureCapacity(this.size + 1);
      this.elementData[this.size++] = var1;
      return true;
   }

   public void add(int var1, E var2) {
      this.rangeCheckForAdd(var1);
      this.ensureCapacity(this.size + 1);
      System.arraycopy(this.elementData, var1, this.elementData, var1 + 1, this.size - var1);
      this.elementData[var1] = var2;
      ++this.size;
   }

   public E remove(int var1) {
      this.rangeCheck(var1);
      ++this.modCount;
      Object var2 = this.elementData[var1];
      int var3 = this.size - var1 - 1;
      if (var3 > 0) {
         System.arraycopy(this.elementData, var1 + 1, this.elementData, var1, var3);
      }

      this.elementData[--this.size] = null;
      return var2;
   }

   public boolean remove(Object var1) {
      for(int var2 = 0; var2 < this.size; ++var2) {
         if (var1 == this.elementData[var2]) {
            this.fastRemove(var2);
            return true;
         }
      }

      return false;
   }

   private void fastRemove(int var1) {
      ++this.modCount;
      int var2 = this.size - var1 - 1;
      if (var2 > 0) {
         System.arraycopy(this.elementData, var1 + 1, this.elementData, var1, var2);
      }

      this.elementData[--this.size] = null;
   }

   public void clear() {
      ++this.modCount;

      for(int var1 = 0; var1 < this.size; ++var1) {
         this.elementData[var1] = null;
      }

      this.size = 0;
   }

   public boolean addAll(Collection<? extends E> var1) {
      Object[] var2 = var1.toArray();
      int var3 = var2.length;
      this.ensureCapacity(this.size + var3);
      System.arraycopy(var2, 0, this.elementData, this.size, var3);
      this.size += var3;
      return var3 != 0;
   }

   public boolean addAll(int var1, Collection<? extends E> var2) {
      this.rangeCheckForAdd(var1);
      Object[] var3 = var2.toArray();
      int var4 = var3.length;
      this.ensureCapacity(this.size + var4);
      int var5 = this.size - var1;
      if (var5 > 0) {
         System.arraycopy(this.elementData, var1, this.elementData, var1 + var4, var5);
      }

      System.arraycopy(var3, 0, this.elementData, var1, var4);
      this.size += var4;
      return var4 != 0;
   }

   protected void removeRange(int var1, int var2) {
      ++this.modCount;
      int var3 = this.size - var2;
      System.arraycopy(this.elementData, var2, this.elementData, var1, var3);

      for(int var4 = this.size - (var2 - var1); this.size != var4; this.elementData[--this.size] = null) {
      }

   }

   private void rangeCheck(int var1) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException(this.outOfBoundsMsg(var1));
      }
   }

   private void rangeCheckForAdd(int var1) {
      if (var1 > this.size || var1 < 0) {
         throw new IndexOutOfBoundsException(this.outOfBoundsMsg(var1));
      }
   }

   private String outOfBoundsMsg(int var1) {
      return "Index: " + var1 + ", Size: " + this.size;
   }
}
