package sun.reflect;

class ByteVectorFactory {
   static ByteVector create() {
      return new ByteVectorImpl();
   }

   static ByteVector create(int var0) {
      return new ByteVectorImpl(var0);
   }
}
