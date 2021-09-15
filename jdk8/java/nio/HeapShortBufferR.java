package java.nio;

class HeapShortBufferR extends HeapShortBuffer {
   HeapShortBufferR(int var1, int var2) {
      super(var1, var2);
      this.isReadOnly = true;
   }

   HeapShortBufferR(short[] var1, int var2, int var3) {
      super(var1, var2, var3);
      this.isReadOnly = true;
   }

   protected HeapShortBufferR(short[] var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
      this.isReadOnly = true;
   }

   public ShortBuffer slice() {
      return new HeapShortBufferR(this.hb, -1, 0, this.remaining(), this.remaining(), this.position() + this.offset);
   }

   public ShortBuffer duplicate() {
      return new HeapShortBufferR(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
   }

   public ShortBuffer asReadOnlyBuffer() {
      return this.duplicate();
   }

   public boolean isReadOnly() {
      return true;
   }

   public ShortBuffer put(short var1) {
      throw new ReadOnlyBufferException();
   }

   public ShortBuffer put(int var1, short var2) {
      throw new ReadOnlyBufferException();
   }

   public ShortBuffer put(short[] var1, int var2, int var3) {
      throw new ReadOnlyBufferException();
   }

   public ShortBuffer put(ShortBuffer var1) {
      throw new ReadOnlyBufferException();
   }

   public ShortBuffer compact() {
      throw new ReadOnlyBufferException();
   }

   public ByteOrder order() {
      return ByteOrder.nativeOrder();
   }
}
