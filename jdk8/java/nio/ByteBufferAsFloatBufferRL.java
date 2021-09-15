package java.nio;

class ByteBufferAsFloatBufferRL extends ByteBufferAsFloatBufferL {
   ByteBufferAsFloatBufferRL(ByteBuffer var1) {
      super(var1);
   }

   ByteBufferAsFloatBufferRL(ByteBuffer var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public FloatBuffer slice() {
      int var1 = this.position();
      int var2 = this.limit();

      assert var1 <= var2;

      int var3 = var1 <= var2 ? var2 - var1 : 0;
      int var4 = (var1 << 2) + this.offset;

      assert var4 >= 0;

      return new ByteBufferAsFloatBufferRL(this.bb, -1, 0, var3, var3, var4);
   }

   public FloatBuffer duplicate() {
      return new ByteBufferAsFloatBufferRL(this.bb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
   }

   public FloatBuffer asReadOnlyBuffer() {
      return this.duplicate();
   }

   public FloatBuffer put(float var1) {
      throw new ReadOnlyBufferException();
   }

   public FloatBuffer put(int var1, float var2) {
      throw new ReadOnlyBufferException();
   }

   public FloatBuffer compact() {
      throw new ReadOnlyBufferException();
   }

   public boolean isDirect() {
      return this.bb.isDirect();
   }

   public boolean isReadOnly() {
      return true;
   }

   public ByteOrder order() {
      return ByteOrder.LITTLE_ENDIAN;
   }
}
