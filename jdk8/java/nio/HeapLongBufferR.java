package java.nio;

class HeapLongBufferR extends HeapLongBuffer {
   HeapLongBufferR(int var1, int var2) {
      super(var1, var2);
      this.isReadOnly = true;
   }

   HeapLongBufferR(long[] var1, int var2, int var3) {
      super(var1, var2, var3);
      this.isReadOnly = true;
   }

   protected HeapLongBufferR(long[] var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
      this.isReadOnly = true;
   }

   public LongBuffer slice() {
      return new HeapLongBufferR(this.hb, -1, 0, this.remaining(), this.remaining(), this.position() + this.offset);
   }

   public LongBuffer duplicate() {
      return new HeapLongBufferR(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
   }

   public LongBuffer asReadOnlyBuffer() {
      return this.duplicate();
   }

   public boolean isReadOnly() {
      return true;
   }

   public LongBuffer put(long var1) {
      throw new ReadOnlyBufferException();
   }

   public LongBuffer put(int var1, long var2) {
      throw new ReadOnlyBufferException();
   }

   public LongBuffer put(long[] var1, int var2, int var3) {
      throw new ReadOnlyBufferException();
   }

   public LongBuffer put(LongBuffer var1) {
      throw new ReadOnlyBufferException();
   }

   public LongBuffer compact() {
      throw new ReadOnlyBufferException();
   }

   public ByteOrder order() {
      return ByteOrder.nativeOrder();
   }
}
