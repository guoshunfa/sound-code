package java.util;

class RegularEnumSet<E extends Enum<E>> extends EnumSet<E> {
   private static final long serialVersionUID = 3411599620347842686L;
   private long elements = 0L;

   RegularEnumSet(Class<E> var1, Enum<?>[] var2) {
      super(var1, var2);
   }

   void addRange(E var1, E var2) {
      this.elements = -1L >>> var1.ordinal() - var2.ordinal() - 1 << var1.ordinal();
   }

   void addAll() {
      if (this.universe.length != 0) {
         this.elements = -1L >>> -this.universe.length;
      }

   }

   void complement() {
      if (this.universe.length != 0) {
         this.elements = ~this.elements;
         this.elements &= -1L >>> -this.universe.length;
      }

   }

   public Iterator<E> iterator() {
      return new RegularEnumSet.EnumSetIterator();
   }

   public int size() {
      return Long.bitCount(this.elements);
   }

   public boolean isEmpty() {
      return this.elements == 0L;
   }

   public boolean contains(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         Class var2 = var1.getClass();
         if (var2 != this.elementType && var2.getSuperclass() != this.elementType) {
            return false;
         } else {
            return (this.elements & 1L << ((Enum)var1).ordinal()) != 0L;
         }
      }
   }

   public boolean add(E var1) {
      this.typeCheck(var1);
      long var2 = this.elements;
      this.elements |= 1L << var1.ordinal();
      return this.elements != var2;
   }

   public boolean remove(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         Class var2 = var1.getClass();
         if (var2 != this.elementType && var2.getSuperclass() != this.elementType) {
            return false;
         } else {
            long var3 = this.elements;
            this.elements &= ~(1L << ((Enum)var1).ordinal());
            return this.elements != var3;
         }
      }
   }

   public boolean containsAll(Collection<?> var1) {
      if (!(var1 instanceof RegularEnumSet)) {
         return super.containsAll(var1);
      } else {
         RegularEnumSet var2 = (RegularEnumSet)var1;
         if (var2.elementType != this.elementType) {
            return var2.isEmpty();
         } else {
            return (var2.elements & ~this.elements) == 0L;
         }
      }
   }

   public boolean addAll(Collection<? extends E> var1) {
      if (!(var1 instanceof RegularEnumSet)) {
         return super.addAll(var1);
      } else {
         RegularEnumSet var2 = (RegularEnumSet)var1;
         if (var2.elementType != this.elementType) {
            if (var2.isEmpty()) {
               return false;
            } else {
               throw new ClassCastException(var2.elementType + " != " + this.elementType);
            }
         } else {
            long var3 = this.elements;
            this.elements |= var2.elements;
            return this.elements != var3;
         }
      }
   }

   public boolean removeAll(Collection<?> var1) {
      if (!(var1 instanceof RegularEnumSet)) {
         return super.removeAll(var1);
      } else {
         RegularEnumSet var2 = (RegularEnumSet)var1;
         if (var2.elementType != this.elementType) {
            return false;
         } else {
            long var3 = this.elements;
            this.elements &= ~var2.elements;
            return this.elements != var3;
         }
      }
   }

   public boolean retainAll(Collection<?> var1) {
      if (!(var1 instanceof RegularEnumSet)) {
         return super.retainAll(var1);
      } else {
         RegularEnumSet var2 = (RegularEnumSet)var1;
         if (var2.elementType != this.elementType) {
            boolean var5 = this.elements != 0L;
            this.elements = 0L;
            return var5;
         } else {
            long var3 = this.elements;
            this.elements &= var2.elements;
            return this.elements != var3;
         }
      }
   }

   public void clear() {
      this.elements = 0L;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof RegularEnumSet)) {
         return super.equals(var1);
      } else {
         RegularEnumSet var2 = (RegularEnumSet)var1;
         if (var2.elementType == this.elementType) {
            return var2.elements == this.elements;
         } else {
            return this.elements == 0L && var2.elements == 0L;
         }
      }
   }

   private class EnumSetIterator<E extends Enum<E>> implements Iterator<E> {
      long unseen;
      long lastReturned = 0L;

      EnumSetIterator() {
         this.unseen = RegularEnumSet.this.elements;
      }

      public boolean hasNext() {
         return this.unseen != 0L;
      }

      public E next() {
         if (this.unseen == 0L) {
            throw new NoSuchElementException();
         } else {
            this.lastReturned = this.unseen & -this.unseen;
            this.unseen -= this.lastReturned;
            return RegularEnumSet.this.universe[Long.numberOfTrailingZeros(this.lastReturned)];
         }
      }

      public void remove() {
         if (this.lastReturned == 0L) {
            throw new IllegalStateException();
         } else {
            RegularEnumSet.this.elements = RegularEnumSet.this.elements & ~this.lastReturned;
            this.lastReturned = 0L;
         }
      }
   }
}
