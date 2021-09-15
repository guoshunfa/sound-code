package java.nio;

class HeapCharBufferR extends HeapCharBuffer {
   HeapCharBufferR(int var1, int var2) {
      super(var1, var2);
      this.isReadOnly = true;
   }

   HeapCharBufferR(char[] var1, int var2, int var3) {
      super(var1, var2, var3);
      this.isReadOnly = true;
   }

   protected HeapCharBufferR(char[] var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
      this.isReadOnly = true;
   }

   public CharBuffer slice() {
      return new HeapCharBufferR(this.hb, -1, 0, this.remaining(), this.remaining(), this.position() + this.offset);
   }

   public CharBuffer duplicate() {
      return new HeapCharBufferR(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
   }

   public CharBuffer asReadOnlyBuffer() {
      return this.duplicate();
   }

   public boolean isReadOnly() {
      return true;
   }

   public CharBuffer put(char var1) {
      throw new ReadOnlyBufferException();
   }

   public CharBuffer put(int var1, char var2) {
      throw new ReadOnlyBufferException();
   }

   public CharBuffer put(char[] var1, int var2, int var3) {
      throw new ReadOnlyBufferException();
   }

   public CharBuffer put(CharBuffer var1) {
      throw new ReadOnlyBufferException();
   }

   public CharBuffer compact() {
      throw new ReadOnlyBufferException();
   }

   String toString(int var1, int var2) {
      try {
         return new String(this.hb, var1 + this.offset, var2 - var1);
      } catch (StringIndexOutOfBoundsException var4) {
         throw new IndexOutOfBoundsException();
      }
   }

   public CharBuffer subSequence(int var1, int var2) {
      if (var1 >= 0 && var2 <= this.length() && var1 <= var2) {
         int var3 = this.position();
         return new HeapCharBufferR(this.hb, -1, var3 + var1, var3 + var2, this.capacity(), this.offset);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public ByteOrder order() {
      return ByteOrder.nativeOrder();
   }
}
