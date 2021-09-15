package java.util;

class JumboEnumSet<E extends Enum<E>> extends EnumSet<E> {
   private static final long serialVersionUID = 334349849919042784L;
   private long[] elements;
   private int size = 0;

   JumboEnumSet(Class<E> var1, Enum<?>[] var2) {
      super(var1, var2);
      this.elements = new long[var2.length + 63 >>> 6];
   }

   void addRange(E var1, E var2) {
      int var3 = var1.ordinal() >>> 6;
      int var4 = var2.ordinal() >>> 6;
      if (var3 == var4) {
         this.elements[var3] = -1L >>> var1.ordinal() - var2.ordinal() - 1 << var1.ordinal();
      } else {
         this.elements[var3] = -1L << var1.ordinal();

         for(int var5 = var3 + 1; var5 < var4; ++var5) {
            this.elements[var5] = -1L;
         }

         this.elements[var4] = -1L >>> 63 - var2.ordinal();
      }

      this.size = var2.ordinal() - var1.ordinal() + 1;
   }

   void addAll() {
      for(int var1 = 0; var1 < this.elements.length; ++var1) {
         this.elements[var1] = -1L;
      }

      long[] var10000 = this.elements;
      int var10001 = this.elements.length - 1;
      var10000[var10001] >>>= -this.universe.length;
      this.size = this.universe.length;
   }

   void complement() {
      for(int var1 = 0; var1 < this.elements.length; ++var1) {
         this.elements[var1] = ~this.elements[var1];
      }

      long[] var10000 = this.elements;
      int var10001 = this.elements.length - 1;
      var10000[var10001] &= -1L >>> -this.universe.length;
      this.size = this.universe.length - this.size;
   }

   public Iterator<E> iterator() {
      return new JumboEnumSet.EnumSetIterator();
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public boolean contains(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         Class var2 = var1.getClass();
         if (var2 != this.elementType && var2.getSuperclass() != this.elementType) {
            return false;
         } else {
            int var3 = ((Enum)var1).ordinal();
            return (this.elements[var3 >>> 6] & 1L << var3) != 0L;
         }
      }
   }

   public boolean add(E var1) {
      this.typeCheck(var1);
      int var2 = var1.ordinal();
      int var3 = var2 >>> 6;
      long var4 = this.elements[var3];
      long[] var10000 = this.elements;
      var10000[var3] |= 1L << var2;
      boolean var6 = this.elements[var3] != var4;
      if (var6) {
         ++this.size;
      }

      return var6;
   }

   public boolean remove(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         Class var2 = var1.getClass();
         if (var2 != this.elementType && var2.getSuperclass() != this.elementType) {
            return false;
         } else {
            int var3 = ((Enum)var1).ordinal();
            int var4 = var3 >>> 6;
            long var5 = this.elements[var4];
            long[] var10000 = this.elements;
            var10000[var4] &= ~(1L << var3);
            boolean var7 = this.elements[var4] != var5;
            if (var7) {
               --this.size;
            }

            return var7;
         }
      }
   }

   public boolean containsAll(Collection<?> var1) {
      if (!(var1 instanceof JumboEnumSet)) {
         return super.containsAll(var1);
      } else {
         JumboEnumSet var2 = (JumboEnumSet)var1;
         if (var2.elementType != this.elementType) {
            return var2.isEmpty();
         } else {
            for(int var3 = 0; var3 < this.elements.length; ++var3) {
               if ((var2.elements[var3] & ~this.elements[var3]) != 0L) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public boolean addAll(Collection<? extends E> var1) {
      if (!(var1 instanceof JumboEnumSet)) {
         return super.addAll(var1);
      } else {
         JumboEnumSet var2 = (JumboEnumSet)var1;
         if (var2.elementType != this.elementType) {
            if (var2.isEmpty()) {
               return false;
            } else {
               throw new ClassCastException(var2.elementType + " != " + this.elementType);
            }
         } else {
            for(int var3 = 0; var3 < this.elements.length; ++var3) {
               long[] var10000 = this.elements;
               var10000[var3] |= var2.elements[var3];
            }

            return this.recalculateSize();
         }
      }
   }

   public boolean removeAll(Collection<?> var1) {
      if (!(var1 instanceof JumboEnumSet)) {
         return super.removeAll(var1);
      } else {
         JumboEnumSet var2 = (JumboEnumSet)var1;
         if (var2.elementType != this.elementType) {
            return false;
         } else {
            for(int var3 = 0; var3 < this.elements.length; ++var3) {
               long[] var10000 = this.elements;
               var10000[var3] &= ~var2.elements[var3];
            }

            return this.recalculateSize();
         }
      }
   }

   public boolean retainAll(Collection<?> var1) {
      if (!(var1 instanceof JumboEnumSet)) {
         return super.retainAll(var1);
      } else {
         JumboEnumSet var2 = (JumboEnumSet)var1;
         if (var2.elementType != this.elementType) {
            boolean var4 = this.size != 0;
            this.clear();
            return var4;
         } else {
            for(int var3 = 0; var3 < this.elements.length; ++var3) {
               long[] var10000 = this.elements;
               var10000[var3] &= var2.elements[var3];
            }

            return this.recalculateSize();
         }
      }
   }

   public void clear() {
      Arrays.fill(this.elements, 0L);
      this.size = 0;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof JumboEnumSet)) {
         return super.equals(var1);
      } else {
         JumboEnumSet var2 = (JumboEnumSet)var1;
         if (var2.elementType == this.elementType) {
            return Arrays.equals(var2.elements, this.elements);
         } else {
            return this.size == 0 && var2.size == 0;
         }
      }
   }

   private boolean recalculateSize() {
      int var1 = this.size;
      this.size = 0;
      long[] var2 = this.elements;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         long var5 = var2[var4];
         this.size += Long.bitCount(var5);
      }

      return this.size != var1;
   }

   public EnumSet<E> clone() {
      JumboEnumSet var1 = (JumboEnumSet)super.clone();
      var1.elements = (long[])var1.elements.clone();
      return var1;
   }

   private class EnumSetIterator<E extends Enum<E>> implements Iterator<E> {
      long unseen;
      int unseenIndex = 0;
      long lastReturned = 0L;
      int lastReturnedIndex = 0;

      EnumSetIterator() {
         this.unseen = JumboEnumSet.this.elements[0];
      }

      public boolean hasNext() {
         while(this.unseen == 0L && this.unseenIndex < JumboEnumSet.this.elements.length - 1) {
            this.unseen = JumboEnumSet.this.elements[++this.unseenIndex];
         }

         return this.unseen != 0L;
      }

      public E next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.lastReturned = this.unseen & -this.unseen;
            this.lastReturnedIndex = this.unseenIndex;
            this.unseen -= this.lastReturned;
            return JumboEnumSet.this.universe[(this.lastReturnedIndex << 6) + Long.numberOfTrailingZeros(this.lastReturned)];
         }
      }

      public void remove() {
         if (this.lastReturned == 0L) {
            throw new IllegalStateException();
         } else {
            long var1 = JumboEnumSet.this.elements[this.lastReturnedIndex];
            long[] var10000 = JumboEnumSet.this.elements;
            int var10001 = this.lastReturnedIndex;
            var10000[var10001] &= ~this.lastReturned;
            if (var1 != JumboEnumSet.this.elements[this.lastReturnedIndex]) {
               JumboEnumSet.this.size--;
            }

            this.lastReturned = 0L;
         }
      }
   }
}
