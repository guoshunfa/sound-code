package java.nio;

class HeapDoubleBufferR extends HeapDoubleBuffer {
   HeapDoubleBufferR(int var1, int var2) {
      super(var1, var2);
      this.isReadOnly = true;
   }

   HeapDoubleBufferR(double[] var1, int var2, int var3) {
      super(var1, var2, var3);
      this.isReadOnly = true;
   }

   protected HeapDoubleBufferR(double[] var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
      this.isReadOnly = true;
   }

   public DoubleBuffer slice() {
      return new HeapDoubleBufferR(this.hb, -1, 0, this.remaining(), this.remaining(), this.position() + this.offset);
   }

   public DoubleBuffer duplicate() {
      return new HeapDoubleBufferR(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
   }

   public DoubleBuffer asReadOnlyBuffer() {
      return this.duplicate();
   }

   public boolean isReadOnly() {
      return true;
   }

   public DoubleBuffer put(double var1) {
      throw new ReadOnlyBufferException();
   }

   public DoubleBuffer put(int var1, double var2) {
      throw new ReadOnlyBufferException();
   }

   public DoubleBuffer put(double[] var1, int var2, int var3) {
      throw new ReadOnlyBufferException();
   }

   public DoubleBuffer put(DoubleBuffer var1) {
      throw new ReadOnlyBufferException();
   }

   public DoubleBuffer compact() {
      throw new ReadOnlyBufferException();
   }

   public ByteOrder order() {
      return ByteOrder.nativeOrder();
   }
}
