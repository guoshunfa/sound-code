package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;

final class PrimitiveArrayListerByte<BeanT> extends Lister<BeanT, byte[], Byte, PrimitiveArrayListerByte.ByteArrayPack> {
   private PrimitiveArrayListerByte() {
   }

   static void register() {
      Lister.primitiveArrayListers.put(Byte.TYPE, new PrimitiveArrayListerByte());
   }

   public ListIterator<Byte> iterator(final byte[] objects, XMLSerializer context) {
      return new ListIterator<Byte>() {
         int idx = 0;

         public boolean hasNext() {
            return this.idx < objects.length;
         }

         public Byte next() {
            return objects[this.idx++];
         }
      };
   }

   public PrimitiveArrayListerByte.ByteArrayPack startPacking(BeanT current, Accessor<BeanT, byte[]> acc) {
      return new PrimitiveArrayListerByte.ByteArrayPack();
   }

   public void addToPack(PrimitiveArrayListerByte.ByteArrayPack objects, Byte o) {
      objects.add(o);
   }

   public void endPacking(PrimitiveArrayListerByte.ByteArrayPack pack, BeanT bean, Accessor<BeanT, byte[]> acc) throws AccessorException {
      acc.set(bean, pack.build());
   }

   public void reset(BeanT o, Accessor<BeanT, byte[]> acc) throws AccessorException {
      acc.set(o, new byte[0]);
   }

   static final class ByteArrayPack {
      byte[] buf = new byte[16];
      int size;

      void add(Byte b) {
         if (this.buf.length == this.size) {
            byte[] nb = new byte[this.buf.length * 2];
            System.arraycopy(this.buf, 0, nb, 0, this.buf.length);
            this.buf = nb;
         }

         if (b != null) {
            this.buf[this.size++] = b;
         }

      }

      byte[] build() {
         if (this.buf.length == this.size) {
            return this.buf;
         } else {
            byte[] r = new byte[this.size];
            System.arraycopy(this.buf, 0, r, 0, this.size);
            return r;
         }
      }
   }
}
