package java.nio;

class StringCharBuffer extends CharBuffer {
   CharSequence str;

   StringCharBuffer(CharSequence var1, int var2, int var3) {
      super(-1, var2, var3, var1.length());
      int var4 = var1.length();
      if (var2 >= 0 && var2 <= var4 && var3 >= var2 && var3 <= var4) {
         this.str = var1;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public CharBuffer slice() {
      return new StringCharBuffer(this.str, -1, 0, this.remaining(), this.remaining(), this.offset + this.position());
   }

   private StringCharBuffer(CharSequence var1, int var2, int var3, int var4, int var5, int var6) {
      super(var2, var3, var4, var5, (char[])null, var6);
      this.str = var1;
   }

   public CharBuffer duplicate() {
      return new StringCharBuffer(this.str, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
   }

   public CharBuffer asReadOnlyBuffer() {
      return this.duplicate();
   }

   public final char get() {
      return this.str.charAt(this.nextGetIndex() + this.offset);
   }

   public final char get(int var1) {
      return this.str.charAt(this.checkIndex(var1) + this.offset);
   }

   char getUnchecked(int var1) {
      return this.str.charAt(var1 + this.offset);
   }

   public final CharBuffer put(char var1) {
      throw new ReadOnlyBufferException();
   }

   public final CharBuffer put(int var1, char var2) {
      throw new ReadOnlyBufferException();
   }

   public final CharBuffer compact() {
      throw new ReadOnlyBufferException();
   }

   public final boolean isReadOnly() {
      return true;
   }

   final String toString(int var1, int var2) {
      return this.str.toString().substring(var1 + this.offset, var2 + this.offset);
   }

   public final CharBuffer subSequence(int var1, int var2) {
      try {
         int var3 = this.position();
         return new StringCharBuffer(this.str, -1, var3 + this.checkIndex(var1, var3), var3 + this.checkIndex(var2, var3), this.capacity(), this.offset);
      } catch (IllegalArgumentException var4) {
         throw new IndexOutOfBoundsException();
      }
   }

   public boolean isDirect() {
      return false;
   }

   public ByteOrder order() {
      return ByteOrder.nativeOrder();
   }
}
