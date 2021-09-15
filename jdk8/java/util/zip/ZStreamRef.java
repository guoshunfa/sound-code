package java.util.zip;

class ZStreamRef {
   private volatile long address;

   ZStreamRef(long var1) {
      this.address = var1;
   }

   long address() {
      return this.address;
   }

   void clear() {
      this.address = 0L;
   }
}
