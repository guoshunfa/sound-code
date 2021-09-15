package java.nio;

class HeapIntBufferR extends HeapIntBuffer {
   HeapIntBufferR(int var1, int var2) {
      super(var1, var2);
      this.isReadOnly = true;
   }

   HeapIntBufferR(int[] var1, int var2, int var3) {
      super(var1, var2, var3);
      this.isReadOnly = true;
   }

   protected HeapIntBufferR(int[] var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
      this.isReadOnly = true;
   }

   public IntBuffer slice() {
      return new HeapIntBufferR(this.hb, -1, 0, this.remaining(), this.remaining(), this.position() + this.offset);
   }

   public IntBuffer duplicate() {
      return new HeapIntBufferR(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
   }

   public IntBuffer asReadOnlyBuffer() {
      return this.duplicate();
   }

   public boolean isReadOnly() {
      return true;
   }

   public IntBuffer put(int var1) {
      throw new ReadOnlyBufferException();
   }

   public IntBuffer put(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   public IntBuffer put(int[] var1, int var2, int var3) {
      throw new ReadOnlyBufferException();
   }

   public IntBuffer put(IntBuffer var1) {
      throw new ReadOnlyBufferException();
   }

   public IntBuffer compact() {
      throw new ReadOnlyBufferException();
   }

   public ByteOrder order() {
      return ByteOrder.nativeOrder();
   }
}
