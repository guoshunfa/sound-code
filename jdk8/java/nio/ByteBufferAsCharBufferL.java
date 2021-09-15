package java.nio;

class ByteBufferAsCharBufferL extends CharBuffer {
   protected final ByteBuffer bb;
   protected final int offset;

   ByteBufferAsCharBufferL(ByteBuffer var1) {
      super(-1, 0, var1.remaining() >> 1, var1.remaining() >> 1);
      this.bb = var1;
      int var2 = this.capacity();
      this.limit(var2);
      int var3 = this.position();

      assert var3 <= var2;

      this.offset = var3;
   }

   ByteBufferAsCharBufferL(ByteBuffer var1, int var2, int var3, int var4, int var5, int var6) {
      super(var2, var3, var4, var5);
      this.bb = var1;
      this.offset = var6;
   }

   public CharBuffer slice() {
      int var1 = this.position();
      int var2 = this.limit();

      assert var1 <= var2;

      int var3 = var1 <= var2 ? var2 - var1 : 0;
      int var4 = (var1 << 1) + this.offset;

      assert var4 >= 0;

      return new ByteBufferAsCharBufferL(this.bb, -1, 0, var3, var3, var4);
   }

   public CharBuffer duplicate() {
      return new ByteBufferAsCharBufferL(this.bb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
   }

   public CharBuffer asReadOnlyBuffer() {
      return new ByteBufferAsCharBufferRL(this.bb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
   }

   protected int ix(int var1) {
      return (var1 << 1) + this.offset;
   }

   public char get() {
      return Bits.getCharL(this.bb, this.ix(this.nextGetIndex()));
   }

   public char get(int var1) {
      return Bits.getCharL(this.bb, this.ix(this.checkIndex(var1)));
   }

   char getUnchecked(int var1) {
      return Bits.getCharL(this.bb, this.ix(var1));
   }

   public CharBuffer put(char var1) {
      Bits.putCharL(this.bb, this.ix(this.nextPutIndex()), var1);
      return this;
   }

   public CharBuffer put(int var1, char var2) {
      Bits.putCharL(this.bb, this.ix(this.checkIndex(var1)), var2);
      return this;
   }

   public CharBuffer compact() {
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

   public String toString(int var1, int var2) {
      if (var2 <= this.limit() && var1 <= var2) {
         try {
            int var3 = var2 - var1;
            char[] var4 = new char[var3];
            CharBuffer var5 = CharBuffer.wrap(var4);
            CharBuffer var6 = this.duplicate();
            var6.position(var1);
            var6.limit(var2);
            var5.put(var6);
            return new String(var4);
         } catch (StringIndexOutOfBoundsException var7) {
            throw new IndexOutOfBoundsException();
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public CharBuffer subSequence(int var1, int var2) {
      int var3 = this.position();
      int var4 = this.limit();

      assert var3 <= var4;

      var3 = var3 <= var4 ? var3 : var4;
      int var5 = var4 - var3;
      if (var1 >= 0 && var2 <= var5 && var1 <= var2) {
         return new ByteBufferAsCharBufferL(this.bb, -1, var3 + var1, var3 + var2, this.capacity(), this.offset);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public ByteOrder order() {
      return ByteOrder.LITTLE_ENDIAN;
   }
}
