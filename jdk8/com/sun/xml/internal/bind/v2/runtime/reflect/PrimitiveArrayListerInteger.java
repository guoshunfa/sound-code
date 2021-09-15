package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;

final class PrimitiveArrayListerInteger<BeanT> extends Lister<BeanT, int[], Integer, PrimitiveArrayListerInteger.IntegerArrayPack> {
   private PrimitiveArrayListerInteger() {
   }

   static void register() {
      Lister.primitiveArrayListers.put(Integer.TYPE, new PrimitiveArrayListerInteger());
   }

   public ListIterator<Integer> iterator(final int[] objects, XMLSerializer context) {
      return new ListIterator<Integer>() {
         int idx = 0;

         public boolean hasNext() {
            return this.idx < objects.length;
         }

         public Integer next() {
            return objects[this.idx++];
         }
      };
   }

   public PrimitiveArrayListerInteger.IntegerArrayPack startPacking(BeanT current, Accessor<BeanT, int[]> acc) {
      return new PrimitiveArrayListerInteger.IntegerArrayPack();
   }

   public void addToPack(PrimitiveArrayListerInteger.IntegerArrayPack objects, Integer o) {
      objects.add(o);
   }

   public void endPacking(PrimitiveArrayListerInteger.IntegerArrayPack pack, BeanT bean, Accessor<BeanT, int[]> acc) throws AccessorException {
      acc.set(bean, pack.build());
   }

   public void reset(BeanT o, Accessor<BeanT, int[]> acc) throws AccessorException {
      acc.set(o, new int[0]);
   }

   static final class IntegerArrayPack {
      int[] buf = new int[16];
      int size;

      void add(Integer b) {
         if (this.buf.length == this.size) {
            int[] nb = new int[this.buf.length * 2];
            System.arraycopy(this.buf, 0, nb, 0, this.buf.length);
            this.buf = nb;
         }

         if (b != null) {
            this.buf[this.size++] = b;
         }

      }

      int[] build() {
         if (this.buf.length == this.size) {
            return this.buf;
         } else {
            int[] r = new int[this.size];
            System.arraycopy(this.buf, 0, r, 0, this.size);
            return r;
         }
      }
   }
}
