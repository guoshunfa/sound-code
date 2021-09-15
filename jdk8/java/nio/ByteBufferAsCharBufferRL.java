package java.nio;

class ByteBufferAsCharBufferRL extends ByteBufferAsCharBufferL {
   ByteBufferAsCharBufferRL(ByteBuffer var1) {
      super(var1);
   }

   ByteBufferAsCharBufferRL(ByteBuffer var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public CharBuffer slice() {
      int var1 = this.position();
      int var2 = this.limit();

      assert var1 <= var2;

      int var3 = var1 <= var2 ? var2 - var1 : 0;
      int var4 = (var1 << 1) + this.offset;

      assert var4 >= 0;

      return new ByteBufferAsCharBufferRL(this.bb, -1, 0, var3, var3, var4);
   }

   public CharBuffer duplicate() {
      return new ByteBufferAsCharBufferRL(this.bb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
   }

   public CharBuffer asReadOnlyBuffer() {
      return this.duplicate();
   }

   public CharBuffer put(char var1) {
      throw new ReadOnlyBufferException();
   }

   public CharBuffer put(int var1, char var2) {
      throw new ReadOnlyBufferException();
   }

   public CharBuffer compact() {
      throw new ReadOnlyBufferException();
   }

   public boolean isDirect() {
      return this.bb.isDirect();
   }

   public boolean isReadOnly() {
      return true;
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
         return new ByteBufferAsCharBufferRL(this.bb, -1, var3 + var1, var3 + var2, this.capacity(), this.offset);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public ByteOrder order() {
      return ByteOrder.LITTLE_ENDIAN;
   }
}
