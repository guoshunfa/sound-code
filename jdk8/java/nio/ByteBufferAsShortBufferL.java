package java.nio;

class ByteBufferAsShortBufferL extends ShortBuffer {
   protected final ByteBuffer bb;
   protected final int offset;

   ByteBufferAsShortBufferL(ByteBuffer var1) {
      super(-1, 0, var1.remaining() >> 1, var1.remaining() >> 1);
      this.bb = var1;
      int var2 = this.capacity();
      this.limit(var2);
      int var3 = this.position();

      assert var3 <= var2;

      this.offset = var3;
   }

   ByteBufferAsShortBufferL(ByteBuffer var1, int var2, int var3, int var4, int var5, int var6) {
      super(var2, var3, var4, var5);
      this.bb = var1;
      this.offset = var6;
   }

   public ShortBuffer slice() {
      int var1 = this.position();
      int var2 = this.limit();

      assert var1 <= var2;

      int var3 = var1 <= var2 ? var2 - var1 : 0;
      int var4 = (var1 << 1) + this.offset;

      assert var4 >= 0;

      return new ByteBufferAsShortBufferL(this.bb, -1, 0, var3, var3, var4);
   }

   public ShortBuffer duplicate() {
      return new ByteBufferAsShortBufferL(this.bb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
   }

   public ShortBuffer asReadOnlyBuffer() {
      return new ByteBufferAsShortBufferRL(this.bb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
   }

   protected int ix(int var1) {
      return (var1 << 1) + this.offset;
   }

   public short get() {
      return Bits.getShortL(this.bb, this.ix(this.nextGetIndex()));
   }

   public short get(int var1) {
      return Bits.getShortL(this.bb, this.ix(this.checkIndex(var1)));
   }

   public ShortBuffer put(short var1) {
      Bits.putShortL(this.bb, this.ix(this.nextPutIndex()), var1);
      return this;
   }

   public ShortBuffer put(int var1, short var2) {
      Bits.putShortL(this.bb, this.ix(this.checkIndex(var1)), var2);
      return this;
   }

   public ShortBuffer compact() {
      int var1 = this.position();
      int var2 = this.limit();

      assert var1 <= var2;

      int var3 = var1 <= var2 ? var2 - var1 : 0;
      ByteBuffer var4 = this.bb.duplicate();
      var4.limit(this.ix(var2));
      var4.position(this.ix(0));
      ByteBuffer var5 = var4.slice();
      var5.position(var1 << 1);
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
