package java.nio;

class HeapByteBuffer extends ByteBuffer {
   HeapByteBuffer(int var1, int var2) {
      super(-1, 0, var2, var1, new byte[var1], 0);
   }

   HeapByteBuffer(byte[] var1, int var2, int var3) {
      super(-1, var2, var2 + var3, var1.length, var1, 0);
   }

   protected HeapByteBuffer(byte[] var1, int var2, int var3, int var4, int var5, int var6) {
      super(var2, var3, var4, var5, var1, var6);
   }

   public ByteBuffer slice() {
      return new HeapByteBuffer(this.hb, -1, 0, this.remaining(), this.remaining(), this.position() + this.offset);
   }

   public ByteBuffer duplicate() {
      return new HeapByteBuffer(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
   }

   public ByteBuffer asReadOnlyBuffer() {
      return new HeapByteBufferR(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
   }

   protected int ix(int var1) {
      return var1 + this.offset;
   }

   public byte get() {
      return this.hb[this.ix(this.nextGetIndex())];
   }

   public byte get(int var1) {
      return this.hb[this.ix(this.checkIndex(var1))];
   }

   public ByteBuffer get(byte[] var1, int var2, int var3) {
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

   public ByteBuffer put(byte var1) {
      this.hb[this.ix(this.nextPutIndex())] = var1;
      return this;
   }

   public ByteBuffer put(int var1, byte var2) {
      this.hb[this.ix(this.checkIndex(var1))] = var2;
      return this;
   }

   public ByteBuffer put(byte[] var1, int var2, int var3) {
      checkBounds(var2, var3, var1.length);
      if (var3 > this.remaining()) {
         throw new BufferOverflowException();
      } else {
         System.arraycopy(var1, var2, this.hb, this.ix(this.position()), var3);
         this.position(this.position() + var3);
         return this;
      }
   }

   public ByteBuffer put(ByteBuffer var1) {
      if (var1 instanceof HeapByteBuffer) {
         if (var1 == this) {
            throw new IllegalArgumentException();
         }

         HeapByteBuffer var2 = (HeapByteBuffer)var1;
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

   public ByteBuffer compact() {
      System.arraycopy(this.hb, this.ix(this.position()), this.hb, this.ix(0), this.remaining());
      this.position(this.remaining());
      this.limit(this.capacity());
      this.discardMark();
      return this;
   }

   byte _get(int var1) {
      return this.hb[var1];
   }

   void _put(int var1, byte var2) {
      this.hb[var1] = var2;
   }

   public char getChar() {
      return Bits.getChar(this, this.ix(this.nextGetIndex(2)), this.bigEndian);
   }

   public char getChar(int var1) {
      return Bits.getChar(this, this.ix(this.checkIndex(var1, 2)), this.bigEndian);
   }

   public ByteBuffer putChar(char var1) {
      Bits.putChar(this, this.ix(this.nextPutIndex(2)), var1, this.bigEndian);
      return this;
   }

   public ByteBuffer putChar(int var1, char var2) {
      Bits.putChar(this, this.ix(this.checkIndex(var1, 2)), var2, this.bigEndian);
      return this;
   }

   public CharBuffer asCharBuffer() {
      int var1 = this.remaining() >> 1;
      int var2 = this.offset + this.position();
      return (CharBuffer)(this.bigEndian ? new ByteBufferAsCharBufferB(this, -1, 0, var1, var1, var2) : new ByteBufferAsCharBufferL(this, -1, 0, var1, var1, var2));
   }

   public short getShort() {
      return Bits.getShort(this, this.ix(this.nextGetIndex(2)), this.bigEndian);
   }

   public short getShort(int var1) {
      return Bits.getShort(this, this.ix(this.checkIndex(var1, 2)), this.bigEndian);
   }

   public ByteBuffer putShort(short var1) {
      Bits.putShort(this, this.ix(this.nextPutIndex(2)), var1, this.bigEndian);
      return this;
   }

   public ByteBuffer putShort(int var1, short var2) {
      Bits.putShort(this, this.ix(this.checkIndex(var1, 2)), var2, this.bigEndian);
      return this;
   }

   public ShortBuffer asShortBuffer() {
      int var1 = this.remaining() >> 1;
      int var2 = this.offset + this.position();
      return (ShortBuffer)(this.bigEndian ? new ByteBufferAsShortBufferB(this, -1, 0, var1, var1, var2) : new ByteBufferAsShortBufferL(this, -1, 0, var1, var1, var2));
   }

   public int getInt() {
      return Bits.getInt(this, this.ix(this.nextGetIndex(4)), this.bigEndian);
   }

   public int getInt(int var1) {
      return Bits.getInt(this, this.ix(this.checkIndex(var1, 4)), this.bigEndian);
   }

   public ByteBuffer putInt(int var1) {
      Bits.putInt(this, this.ix(this.nextPutIndex(4)), var1, this.bigEndian);
      return this;
   }

   public ByteBuffer putInt(int var1, int var2) {
      Bits.putInt(this, this.ix(this.checkIndex(var1, 4)), var2, this.bigEndian);
      return this;
   }

   public IntBuffer asIntBuffer() {
      int var1 = this.remaining() >> 2;
      int var2 = this.offset + this.position();
      return (IntBuffer)(this.bigEndian ? new ByteBufferAsIntBufferB(this, -1, 0, var1, var1, var2) : new ByteBufferAsIntBufferL(this, -1, 0, var1, var1, var2));
   }

   public long getLong() {
      return Bits.getLong(this, this.ix(this.nextGetIndex(8)), this.bigEndian);
   }

   public long getLong(int var1) {
      return Bits.getLong(this, this.ix(this.checkIndex(var1, 8)), this.bigEndian);
   }

   public ByteBuffer putLong(long var1) {
      Bits.putLong(this, this.ix(this.nextPutIndex(8)), var1, this.bigEndian);
      return this;
   }

   public ByteBuffer putLong(int var1, long var2) {
      Bits.putLong(this, this.ix(this.checkIndex(var1, 8)), var2, this.bigEndian);
      return this;
   }

   public LongBuffer asLongBuffer() {
      int var1 = this.remaining() >> 3;
      int var2 = this.offset + this.position();
      return (LongBuffer)(this.bigEndian ? new ByteBufferAsLongBufferB(this, -1, 0, var1, var1, var2) : new ByteBufferAsLongBufferL(this, -1, 0, var1, var1, var2));
   }

   public float getFloat() {
      return Bits.getFloat(this, this.ix(this.nextGetIndex(4)), this.bigEndian);
   }

   public float getFloat(int var1) {
      return Bits.getFloat(this, this.ix(this.checkIndex(var1, 4)), this.bigEndian);
   }

   public ByteBuffer putFloat(float var1) {
      Bits.putFloat(this, this.ix(this.nextPutIndex(4)), var1, this.bigEndian);
      return this;
   }

   public ByteBuffer putFloat(int var1, float var2) {
      Bits.putFloat(this, this.ix(this.checkIndex(var1, 4)), var2, this.bigEndian);
      return this;
   }

   public FloatBuffer asFloatBuffer() {
      int var1 = this.remaining() >> 2;
      int var2 = this.offset + this.position();
      return (FloatBuffer)(this.bigEndian ? new ByteBufferAsFloatBufferB(this, -1, 0, var1, var1, var2) : new ByteBufferAsFloatBufferL(this, -1, 0, var1, var1, var2));
   }

   public double getDouble() {
      return Bits.getDouble(this, this.ix(this.nextGetIndex(8)), this.bigEndian);
   }

   public double getDouble(int var1) {
      return Bits.getDouble(this, this.ix(this.checkIndex(var1, 8)), this.bigEndian);
   }

   public ByteBuffer putDouble(double var1) {
      Bits.putDouble(this, this.ix(this.nextPutIndex(8)), var1, this.bigEndian);
      return this;
   }

   public ByteBuffer putDouble(int var1, double var2) {
      Bits.putDouble(this, this.ix(this.checkIndex(var1, 8)), var2, this.bigEndian);
      return this;
   }

   public DoubleBuffer asDoubleBuffer() {
      int var1 = this.remaining() >> 3;
      int var2 = this.offset + this.position();
      return (DoubleBuffer)(this.bigEndian ? new ByteBufferAsDoubleBufferB(this, -1, 0, var1, var1, var2) : new ByteBufferAsDoubleBufferL(this, -1, 0, var1, var1, var2));
   }
}
