package java.nio;

class ByteBufferAsDoubleBufferL extends DoubleBuffer {
   protected final ByteBuffer bb;
   protected final int offset;

   ByteBufferAsDoubleBufferL(ByteBuffer var1) {
      super(-1, 0, var1.remaining() >> 3, var1.remaining() >> 3);
      this.bb = var1;
      int var2 = this.capacity();
      this.limit(var2);
      int var3 = this.position();

      assert var3 <= var2;

      this.offset = var3;
   }

   ByteBufferAsDoubleBufferL(ByteBuffer var1, int var2, int var3, int var4, int var5, int var6) {
      super(var2, var3, var4, var5);
      this.bb = var1;
      this.offset = var6;
   }

   public DoubleBuffer slice() {
      int var1 = this.position();
      int var2 = this.limit();

      assert var1 <= var2;

      int var3 = var1 <= var2 ? var2 - var1 : 0;
      int var4 = (var1 << 3) + this.offset;

      assert var4 >= 0;

      return new ByteBufferAsDoubleBufferL(this.bb, -1, 0, var3, var3, var4);
   }

   public DoubleBuffer duplicate() {
      return new ByteBufferAsDoubleBufferL(this.bb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
   }

   public DoubleBuffer asReadOnlyBuffer() {
      return new ByteBufferAsDoubleBufferRL(this.bb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
   }

   protected int ix(int var1) {
      return (var1 << 3) + this.offset;
   }

   public double get() {
      return Bits.getDoubleL(this.bb, this.ix(this.nextGetIndex()));
   }

   public double get(int var1) {
      return Bits.getDoubleL(this.bb, this.ix(this.checkIndex(var1)));
   }

   public DoubleBuffer put(double var1) {
      Bits.putDoubleL(this.bb, this.ix(this.nextPutIndex()), var1);
      return this;
   }

   public DoubleBuffer put(int var1, double var2) {
      Bits.putDoubleL(this.bb, this.ix(this.checkIndex(var1)), var2);
      return this;
   }

   public DoubleBuffer compact() {
      int var1 = this.position();
      int var2 = this.limit();

      assert var1 <= var2;

      int var3 = var1 <= var2 ? var2 - var1 : 0;
      ByteBuffer var4 = this.bb.duplicate();
      var4.limit(this.ix(var2));
      var4.position(this.ix(0));
      ByteBuffer var5 = var4.slice();
      var5.position(var1 << 3);
      var5.compact();
      this.position(var3);
      this.limit(this.capacity());
      this.discardMark();
      return this;
   }

   public boolean isDirect() {
      return this.bb.isDirect();
   }

   public boolean isReadOnly() {
      return false;
   }

   public ByteOrder order() {
      return ByteOrder.LITTLE_ENDIAN;
   }
}
