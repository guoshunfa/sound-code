package java.nio;

import sun.nio.ch.DirectBuffer;

class DirectIntBufferRU extends DirectIntBufferU implements DirectBuffer {
   DirectIntBufferRU(DirectBuffer var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public IntBuffer slice() {
      int var1 = this.position();
      int var2 = this.limit();

      assert var1 <= var2;

      int var3 = var1 <= var2 ? var2 - var1 : 0;
      int var4 = var1 << 2;

      assert var4 >= 0;

      return new DirectIntBufferRU(this, -1, 0, var3, var3, var4);
   }

   public IntBuffer duplicate() {
      return new DirectIntBufferRU(this, this.markValue(), this.position(), this.limit(), this.capacity(), 0);
   }

   public IntBuffer asReadOnlyBuffer() {
      return this.duplicate();
   }

   public IntBuffer put(int var1) {
      throw new ReadOnlyBufferException();
   }

   public IntBuffer put(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   public IntBuffer put(IntBuffer var1) {
      throw new ReadOnlyBufferException();
   }

   public IntBuffer put(int[] var1, int var2, int var3) {
      throw new ReadOnlyBufferException();
   }

   public IntBuffer compact() {
      throw new ReadOnlyBufferException();
   }

   public boolean isDirect() {
      return true;
   }

   public boolean isReadOnly() {
      return true;
   }

   public ByteOrder order() {
      return ByteOrder.nativeOrder() != ByteOrder.BIG_ENDIAN ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
   }
}
