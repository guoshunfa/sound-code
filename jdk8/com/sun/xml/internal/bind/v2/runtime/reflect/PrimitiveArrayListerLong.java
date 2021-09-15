package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;

final class PrimitiveArrayListerLong<BeanT> extends Lister<BeanT, long[], Long, PrimitiveArrayListerLong.LongArrayPack> {
   private PrimitiveArrayListerLong() {
   }

   static void register() {
      Lister.primitiveArrayListers.put(Long.TYPE, new PrimitiveArrayListerLong());
   }

   public ListIterator<Long> iterator(final long[] objects, XMLSerializer context) {
      return new ListIterator<Long>() {
         int idx = 0;

         public boolean hasNext() {
            return this.idx < objects.length;
         }

         public Long next() {
            return objects[this.idx++];
         }
      };
   }

   public PrimitiveArrayListerLong.LongArrayPack startPacking(BeanT current, Accessor<BeanT, long[]> acc) {
      return new PrimitiveArrayListerLong.LongArrayPack();
   }

   public void addToPack(PrimitiveArrayListerLong.LongArrayPack objects, Long o) {
      objects.add(o);
   }

   public void endPacking(PrimitiveArrayListerLong.LongArrayPack pack, BeanT bean, Accessor<BeanT, long[]> acc) throws AccessorException {
      acc.set(bean, pack.build());
   }

   public void reset(BeanT o, Accessor<BeanT, long[]> acc) throws AccessorException {
      acc.set(o, new long[0]);
   }

   static final class LongArrayPack {
      long[] buf = new long[16];
      int size;

      void add(Long b) {
         if (this.buf.length == this.size) {
            long[] nb = new long[this.buf.length * 2];
            System.arraycopy(this.buf, 0, nb, 0, this.buf.length);
            this.buf = nb;
         }

         if (b != null) {
            this.buf[this.size++] = b;
         }

      }

      long[] build() {
         if (this.buf.length == this.size) {
            return this.buf;
         } else {
            long[] r = new long[this.size];
            System.arraycopy(this.buf, 0, r, 0, this.size);
            return r;
         }
      }
   }
}
