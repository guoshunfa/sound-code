package java.nio;

class HeapFloatBufferR extends HeapFloatBuffer {
   HeapFloatBufferR(int var1, int var2) {
      super(var1, var2);
      this.isReadOnly = true;
   }

   HeapFloatBufferR(float[] var1, int var2, int var3) {
      super(var1, var2, var3);
      this.isReadOnly = true;
   }

   protected HeapFloatBufferR(float[] var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
      this.isReadOnly = true;
   }

   public FloatBuffer slice() {
      return new HeapFloatBufferR(this.hb, -1, 0, this.remaining(), this.remaining(), this.position() + this.offset);
   }

   public FloatBuffer duplicate() {
      return new HeapFloatBufferR(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
   }

   public FloatBuffer asReadOnlyBuffer() {
      return this.duplicate();
   }

   public boolean isReadOnly() {
      return true;
   }

   public FloatBuffer put(float var1) {
      throw new ReadOnlyBufferException();
   }

   public FloatBuffer put(int var1, float var2) {
      throw new ReadOnlyBufferException();
   }

   public FloatBuffer put(float[] var1, int var2, int var3) {
      throw new ReadOnlyBufferException();
   }

   public FloatBuffer put(FloatBuffer var1) {
      throw new ReadOnlyBufferException();
   }

   public FloatBuffer compact() {
      throw new ReadOnlyBufferException();
   }

   public ByteOrder order() {
      return ByteOrder.nativeOrder();
   }
}
