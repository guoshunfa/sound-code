package java.util;

class RandomAccessSubList<E> extends SubList<E> implements RandomAccess {
   RandomAccessSubList(AbstractList<E> var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   public List<E> subList(int var1, int var2) {
      return new RandomAccessSubList(this, var1, var2);
   }
}
