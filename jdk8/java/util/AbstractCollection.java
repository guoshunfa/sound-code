package java.util;

import java.lang.reflect.Array;

public abstract class AbstractCollection<E> implements Collection<E> {
   private static final int MAX_ARRAY_SIZE = 2147483639;

   protected AbstractCollection() {
   }

   public abstract Iterator<E> iterator();

   public abstract int size();

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public boolean contains(Object var1) {
      Iterator var2 = this.iterator();
      if (var1 == null) {
         while(var2.hasNext()) {
            if (var2.next() == null) {
               return true;
            }
         }
      } else {
         while(var2.hasNext()) {
            if (var1.equals(var2.next())) {
               return true;
            }
         }
      }

      return false;
   }

   public Object[] toArray() {
      Object[] var1 = new Object[this.size()];
      Iterator var2 = this.iterator();

      for(int var3 = 0; var3 < var1.length; ++var3) {
         if (!var2.hasNext()) {
            return Arrays.copyOf(var1, var3);
         }

         var1[var3] = var2.next();
      }

      return var2.hasNext() ? finishToArray(var1, var2) : var1;
   }

   public <T> T[] toArray(T[] var1) {
      int var2 = this.size();
      Object[] var3 = var1.length >= var2 ? var1 : (Object[])((Object[])Array.newInstance(var1.getClass().getComponentType(), var2));
      Iterator var4 = this.iterator();

      for(int var5 = 0; var5 < var3.length; ++var5) {
         if (!var4.hasNext()) {
            if (var1 == var3) {
               var3[var5] = null;
            } else {
               if (var1.length < var5) {
                  return Arrays.copyOf(var3, var5);
               }

               System.arraycopy(var3, 0, var1, 0, var5);
               if (var1.length > var5) {
                  var1[var5] = null;
               }
            }

            return var1;
         }

         var3[var5] = var4.next();
      }

      return var4.hasNext() ? finishToArray(var3, var4) : var3;
   }

   private static <T> T[] finishToArray(T[] var0, Iterator<?> var1) {
      int var2;
      for(var2 = var0.length; var1.hasNext(); var0[var2++] = var1.next()) {
         int var3 = var0.length;
         if (var2 == var3) {
            int var4 = var3 + (var3 >> 1) + 1;
            if (var4 - 2147483639 > 0) {
               var4 = hugeCapacity(var3 + 1);
            }

            var0 = Arrays.copyOf(var0, var4);
         }
      }

      return var2 == var0.length ? var0 : Arrays.copyOf(var0, var2);
   }

   private static int hugeCapacity(int var0) {
      if (var0 < 0) {
         throw new OutOfMemoryError("Required array size too large");
      } else {
         return var0 > 2147483639 ? Integer.MAX_VALUE : 2147483639;
      }
   }

   public boolean add(E var1) {
      throw new UnsupportedOperationException();
   }

   public boolean remove(Object var1) {
      Iterator var2 = this.iterator();
      if (var1 == null) {
         while(var2.hasNext()) {
            if (var2.next() == null) {
               var2.remove();
               return true;
            }
         }
      } else {
         while(var2.hasNext()) {
            if (var1.equals(var2.next())) {
               var2.remove();
               return true;
            }
         }
      }

      return false;
   }

   public boolean containsAll(Collection<?> var1) {
      Iterator var2 = var1.iterator();

      Object var3;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         var3 = var2.next();
      } while(this.contains(var3));

      return false;
   }

   public boolean addAll(Collection<? extends E> var1) {
      boolean var2 = false;
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Object var4 = var3.next();
         if (this.add(var4)) {
            var2 = true;
         }
      }

      return var2;
   }

   public boolean removeAll(Collection<?> var1) {
      Objects.requireNonNull(var1);
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

   public boolean retainAll(Collection<?> var1) {
      Objects.requireNonNull(var1);
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

   public void clear() {
      Iterator var1 = this.iterator();

      while(var1.hasNext()) {
         var1.next();
         var1.remove();
      }

   }

   public String toString() {
      Iterator var1 = this.iterator();
      if (!var1.hasNext()) {
         return "[]";
      } else {
         StringBuilder var2 = new StringBuilder();
         var2.append('[');

         while(true) {
            Object var3 = var1.next();
            var2.append(var3 == this ? "(this Collection)" : var3);
            if (!var1.hasNext()) {
               return var2.append(']').toString();
            }

            var2.append(',').append(' ');
         }
      }
   }
}
