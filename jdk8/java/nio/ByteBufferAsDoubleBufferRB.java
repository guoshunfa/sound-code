package java.nio;

class ByteBufferAsDoubleBufferRB extends ByteBufferAsDoubleBufferB {
   ByteBufferAsDoubleBufferRB(ByteBuffer var1) {
      super(var1);
   }

   ByteBufferAsDoubleBufferRB(ByteBuffer var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public DoubleBuffer slice() {
      int var1 = this.position();
      int var2 = this.limit();

      assert var1 <= var2;

      int var3 = var1 <= var2 ? var2 - var1 : 0;
      int var4 = (var1 << 3) + this.offset;

      assert var4 >= 0;

      return new ByteBufferAsDoubleBufferRB(this.bb, -1, 0, var3, var3, var4);
   }

   public DoubleBuffer duplicate() {
      return new ByteBufferAsDoubleBufferRB(this.bb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
   }

   public DoubleBuffer asReadOnlyBuffer() {
      return this.duplicate();
   }

   public DoubleBuffer put(double var1) {
      throw new ReadOnlyBufferException();
   }

   public DoubleBuffer put(int var1, double var2) {
      throw new ReadOnlyBufferException();
   }

   public DoubleBuffer compact() {
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
