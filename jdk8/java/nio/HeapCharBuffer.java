package java.nio;

class HeapCharBuffer extends CharBuffer {
   HeapCharBuffer(int var1, int var2) {
      super(-1, 0, var2, var1, new char[var1], 0);
   }

   HeapCharBuffer(char[] var1, int var2, int var3) {
      super(-1, var2, var2 + var3, var1.length, var1, 0);
   }

   protected HeapCharBuffer(char[] var1, int var2, int var3, int var4, int var5, int var6) {
      super(var2, var3, var4, var5, var1, var6);
   }

   public CharBuffer slice() {
      return new HeapCharBuffer(this.hb, -1, 0, this.remaining(), this.remaining(), this.position() + this.offset);
   }

   public CharBuffer duplicate() {
      return new HeapCharBuffer(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
   }

   public CharBuffer asReadOnlyBuffer() {
      return new HeapCharBufferR(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
   }

   protected int ix(int var1) {
      return var1 + this.offset;
   }

   public char get() {
      return this.hb[this.ix(this.nextGetIndex())];
   }

   public char get(int var1) {
      return this.hb[this.ix(this.checkIndex(var1))];
   }

   char getUnchecked(int var1) {
      return this.hb[this.ix(var1)];
   }

   public CharBuffer get(char[] var1, int var2, int var3) {
      checkBounds(var2, var3, var1.length);
      if (var3 > this.remaining()) {
         throw new BufferUnderflowException();
      } else {
         System.arraycopy(this.hb, this.ix(this.position()), var1, var2, var3);
         this.position(this.position() + var3);
         return this;
      }
   }

   public boolean isDirect() {
      return false;
   }

   public boolean isReadOnly() {
      return false;
   }

   public CharBuffer put(char var1) {
      this.hb[this.ix(this.nextPutIndex())] = var1;
      return this;
   }

   public CharBuffer put(int var1, char var2) {
      this.hb[this.ix(this.checkIndex(var1))] = var2;
      return this;
   }

   public CharBuffer put(char[] var1, int var2, int var3) {
      checkBounds(var2, var3, var1.length);
      if (var3 > this.remaining()) {
         throw new BufferOverflowException();
      } else {
         System.arraycopy(var1, var2, this.hb, this.ix(this.position()), var3);
         this.position(this.position() + var3);
         return this;
      }
   }

   public CharBuffer put(CharBuffer var1) {
      if (var1 instanceof HeapCharBuffer) {
         if (var1 == this) {
            throw new IllegalArgumentException();
         }

         HeapCharBuffer var2 = (HeapCharBuffer)var1;
         int var3 = var2.remaining();
         if (var3 > this.remaining()) {
            throw new BufferOverflowException();
         }

         System.arraycopy(var2.hb, var2.ix(var2.position()), this.hb, this.ix(this.position()), var3);
         var2.position(var2.position() + var3);
         this.position(this.position() + var3);
      } else if (var1.isDirect()) {
         int var4 = var1.remaining();
         if (var4 > this.remaining()) {
            throw new BufferOverflowException();
         }

         var1.get(this.hb, this.ix(this.position()), var4);
         this.position(this.position() + var4);
      } else {
         super.put(var1);
      }

      return this;
   }

   public CharBuffer compact() {
      System.arraycopy(this.hb, this.ix(this.position()), this.hb, this.ix(0), this.remaining());
      this.position(this.remaining());
      this.limit(this.capacity());
      this.discardMark();
      return this;
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
         return new HeapCharBuffer(this.hb, -1, var3 + var1, var3 + var2, this.capacity(), this.offset);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public ByteOrder order() {
      return ByteOrder.nativeOrder();
   }
}
