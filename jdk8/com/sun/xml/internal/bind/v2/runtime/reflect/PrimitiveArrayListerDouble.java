package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;

final class PrimitiveArrayListerDouble<BeanT> extends Lister<BeanT, double[], Double, PrimitiveArrayListerDouble.DoubleArrayPack> {
   private PrimitiveArrayListerDouble() {
   }

   static void register() {
      Lister.primitiveArrayListers.put(Double.TYPE, new PrimitiveArrayListerDouble());
   }

   public ListIterator<Double> iterator(final double[] objects, XMLSerializer context) {
      return new ListIterator<Double>() {
         int idx = 0;

         public boolean hasNext() {
            return this.idx < objects.length;
         }

         public Double next() {
            return objects[this.idx++];
         }
      };
   }

   public PrimitiveArrayListerDouble.DoubleArrayPack startPacking(BeanT current, Accessor<BeanT, double[]> acc) {
      return new PrimitiveArrayListerDouble.DoubleArrayPack();
   }

   public void addToPack(PrimitiveArrayListerDouble.DoubleArrayPack objects, Double o) {
      objects.add(o);
   }

   public void endPacking(PrimitiveArrayListerDouble.DoubleArrayPack pack, BeanT bean, Accessor<BeanT, double[]> acc) throws AccessorException {
      acc.set(bean, pack.build());
   }

   public void reset(BeanT o, Accessor<BeanT, double[]> acc) throws AccessorException {
      acc.set(o, new double[0]);
   }

   static final class DoubleArrayPack {
      double[] buf = new double[16];
      int size;

      void add(Double b) {
         if (this.buf.length == this.size) {
            double[] nb = new double[this.buf.length * 2];
            System.arraycopy(this.buf, 0, nb, 0, this.buf.length);
            this.buf = nb;
         }

         if (b != null) {
            this.buf[this.size++] = b;
         }

      }

      double[] build() {
         if (this.buf.length == this.size) {
            return this.buf;
         } else {
            double[] r = new double[this.size];
            System.arraycopy(this.buf, 0, r, 0, this.size);
            return r;
         }
      }
   }
}
