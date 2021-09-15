package java.nio;

class HeapByteBufferR extends HeapByteBuffer {
   HeapByteBufferR(int var1, int var2) {
      super(var1, var2);
      this.isReadOnly = true;
   }

   HeapByteBufferR(byte[] var1, int var2, int var3) {
      super(var1, var2, var3);
      this.isReadOnly = true;
   }

   protected HeapByteBufferR(byte[] var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
      this.isReadOnly = true;
   }

   public ByteBuffer slice() {
      return new HeapByteBufferR(this.hb, -1, 0, this.remaining(), this.remaining(), this.position() + this.offset);
   }

   public ByteBuffer duplicate() {
      return new HeapByteBufferR(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
   }

   public ByteBuffer asReadOnlyBuffer() {
      return this.duplicate();
   }

   public boolean isReadOnly() {
      return true;
   }

   public ByteBuffer put(byte var1) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuffer put(int var1, byte var2) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuffer put(byte[] var1, int var2, int var3) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuffer put(ByteBuffer var1) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuffer compact() {
      throw new ReadOnlyBufferException();
   }

   byte _get(int var1) {
      return this.hb[var1];
   }

   void _put(int var1, byte var2) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuffer putChar(char var1) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuffer putChar(int var1, char var2) {
      throw new ReadOnlyBufferException();
   }

   public CharBuffer asCharBuffer() {
      int var1 = this.remaining() >> 1;
      int var2 = this.offset + this.position();
      return (CharBuffer)(this.bigEndian ? new ByteBufferAsCharBufferRB(this, -1, 0, var1, var1, var2) : new ByteBufferAsCharBufferRL(this, -1, 0, var1, var1, var2));
   }

   public ByteBuffer putShort(short var1) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuffer putShort(int var1, short var2) {
      throw new ReadOnlyBufferException();
   }

   public ShortBuffer asShortBuffer() {
      int var1 = this.remaining() >> 1;
      int var2 = this.offset + this.position();
      return (ShortBuffer)(this.bigEndian ? new ByteBufferAsShortBufferRB(this, -1, 0, var1, var1, var2) : new ByteBufferAsShortBufferRL(this, -1, 0, var1, var1, var2));
   }

   public ByteBuffer putInt(int var1) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuffer putInt(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   public IntBuffer asIntBuffer() {
      int var1 = this.remaining() >> 2;
      int var2 = this.offset + this.position();
      return (IntBuffer)(this.bigEndian ? new ByteBufferAsIntBufferRB(this, -1, 0, var1, var1, var2) : new ByteBufferAsIntBufferRL(this, -1, 0, var1, var1, var2));
   }

   public ByteBuffer putLong(long var1) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuffer putLong(int var1, long var2) {
      throw new ReadOnlyBufferException();
   }

   public LongBuffer asLongBuffer() {
      int var1 = this.remaining() >> 3;
      int var2 = this.offset + this.position();
      return (LongBuffer)(this.bigEndian ? new ByteBufferAsLongBufferRB(this, -1, 0, var1, var1, var2) : new ByteBufferAsLongBufferRL(this, -1, 0, var1, var1, var2));
   }

   public ByteBuffer putFloat(float var1) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuffer putFloat(int var1, float var2) {
      throw new ReadOnlyBufferException();
   }

   public FloatBuffer asFloatBuffer() {
      int var1 = this.remaining() >> 2;
      int var2 = this.offset + this.position();
      return (FloatBuffer)(this.bigEndian ? new ByteBufferAsFloatBufferRB(this, -1, 0, var1, var1, var2) : new ByteBufferAsFloatBufferRL(this, -1, 0, var1, var1, var2));
   }

   public ByteBuffer putDouble(double var1) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuffer putDouble(int var1, double var2) {
      throw new ReadOnlyBufferException();
   }

   public DoubleBuffer asDoubleBuffer() {
      int var1 = this.remaining() >> 3;
      int var2 = this.offset + this.position();
      return (DoubleBuffer)(this.bigEndian ? new ByteBufferAsDoubleBufferRB(this, -1, 0, var1, var1, var2) : new ByteBufferAsDoubleBufferRL(this, -1, 0, var1, var1, var2));
   }
}
