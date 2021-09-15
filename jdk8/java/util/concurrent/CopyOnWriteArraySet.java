package java.util.concurrent;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CopyOnWriteArraySet<E> extends AbstractSet<E> implements Serializable {
   private static final long serialVersionUID = 5457747651344034263L;
   private final CopyOnWriteArrayList<E> al;

   public CopyOnWriteArraySet() {
      this.al = new CopyOnWriteArrayList();
   }

   public CopyOnWriteArraySet(Collection<? extends E> var1) {
      if (var1.getClass() == CopyOnWriteArraySet.class) {
         CopyOnWriteArraySet var2 = (CopyOnWriteArraySet)var1;
         this.al = new CopyOnWriteArrayList(var2.al);
      } else {
         this.al = new CopyOnWriteArrayList();
         this.al.addAllAbsent(var1);
      }

   }

   public int size() {
      return this.al.size();
   }

   public boolean isEmpty() {
      return this.al.isEmpty();
   }

   public boolean contains(Object var1) {
      return this.al.contains(var1);
   }

   public Object[] toArray() {
      return this.al.toArray();
   }

   public <T> T[] toArray(T[] var1) {
      return this.al.toArray(var1);
   }

   public void clear() {
      this.al.clear();
   }

   public boolean remove(Object var1) {
      return this.al.remove(var1);
   }

   public boolean add(E var1) {
      return this.al.addIfAbsent(var1);
   }

   public boolean containsAll(Collection<?> var1) {
      return this.al.containsAll(var1);
   }

   public boolean addAll(Collection<? extends E> var1) {
      return this.al.addAllAbsent(var1) > 0;
   }

   public boolean removeAll(Collection<?> var1) {
      return this.al.removeAll(var1);
   }

   public boolean retainAll(Collection<?> var1) {
      return this.al.retainAll(var1);
   }

   public Iterator<E> iterator() {
      return this.al.iterator();
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Set)) {
         return false;
      } else {
         Set var2 = (Set)((Set)var1);
         Iterator var3 = var2.iterator();
         Object[] var4 = this.al.getArray();
         int var5 = var4.length;
         boolean[] var6 = new boolean[var5];

         int var7;
         int var9;
         label42:
         for(var7 = 0; var3.hasNext(); var6[var9] = true) {
            ++var7;
            if (var7 > var5) {
               return false;
            }

            Object var8 = var3.next();

            for(var9 = 0; var9 < var5; ++var9) {
               if (!var6[var9] && eq(var8, var4[var9])) {
                  continue label42;
               }
            }

            return false;
         }

         return var7 == var5;
      }
   }

   public boolean removeIf(Predicate<? super E> var1) {
      return this.al.removeIf(var1);
   }

   public void forEach(Consumer<? super E> var1) {
      this.al.forEach(var1);
   }

   public Spliterator<E> spliterator() {
      return Spliterators.spliterator((Object[])this.al.getArray(), 1025);
   }

   private static boolean eq(Object var0, Object var1) {
      return var0 == null ? var1 == null : var0.equals(var1);
   }
}
