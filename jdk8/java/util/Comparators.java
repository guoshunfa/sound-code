package java.util;

import java.io.Serializable;

class Comparators {
   private Comparators() {
      throw new AssertionError("no instances");
   }

   static final class NullComparator<T> implements Comparator<T>, Serializable {
      private static final long serialVersionUID = -7569533591570686392L;
      private final boolean nullFirst;
      private final Comparator<T> real;

      NullComparator(boolean var1, Comparator<? super T> var2) {
         this.nullFirst = var1;
         this.real = var2;
      }

      public int compare(T var1, T var2) {
         if (var1 == null) {
            return var2 == null ? 0 : (this.nullFirst ? -1 : 1);
         } else if (var2 == null) {
            return this.nullFirst ? 1 : -1;
         } else {
            return this.real == null ? 0 : this.real.compare(var1, var2);
         }
      }

      public Comparator<T> thenComparing(Comparator<? super T> var1) {
         Objects.requireNonNull(var1);
         return new Comparators.NullComparator(this.nullFirst, this.real == null ? var1 : this.real.thenComparing(var1));
      }

      public Comparator<T> reversed() {
         return new Comparators.NullComparator(!this.nullFirst, this.real == null ? null : this.real.reversed());
      }
   }

   static enum NaturalOrderComparator implements Comparator<Comparable<Object>> {
      INSTANCE;

      public int compare(Comparable<Object> var1, Comparable<Object> var2) {
         return var1.compareTo(var2);
      }

      public Comparator<Comparable<Object>> reversed() {
         return Comparator.reverseOrder();
      }
   }
}
