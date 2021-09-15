package java.nio;

class ByteBufferAsShortBufferRB extends ByteBufferAsShortBufferB {
   ByteBufferAsShortBufferRB(ByteBuffer var1) {
      super(var1);
   }

   ByteBufferAsShortBufferRB(ByteBuffer var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public ShortBuffer slice() {
      int var1 = this.position();
      int var2 = this.limit();

      assert var1 <= var2;

      int var3 = var1 <= var2 ? var2 - var1 : 0;
      int var4 = (var1 << 1) + this.offset;

      assert var4 >= 0;

      return new ByteBufferAsShortBufferRB(this.bb, -1, 0, var3, var3, var4);
   }

   public ShortBuffer duplicate() {
      return new ByteBufferAsShortBufferRB(this.bb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
   }

   public ShortBuffer asReadOnlyBuffer() {
      return this.duplicate();
   }

   public ShortBuffer put(short var1) {
      throw new ReadOnlyBufferException();
   }

   public ShortBuffer put(int var1, short var2) {
      throw new ReadOnlyBufferException();
   }

   public ShortBuffer compact() {
      throw new ReadOnlyBufferException();
   }

   public boolean isDirect() {
      return this.bb.isDirect();
   }

   public boolean isReadOnly() {
      return true;
   }

   public ByteOrder order() {
      return ByteOrder.BIG_ENDIAN;
   }
}
