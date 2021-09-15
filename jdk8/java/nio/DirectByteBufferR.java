package java.nio;

import java.io.FileDescriptor;
import sun.nio.ch.DirectBuffer;

class DirectByteBufferR extends DirectByteBuffer implements DirectBuffer {
   DirectByteBufferR(int var1) {
      super(var1);
   }

   protected DirectByteBufferR(int var1, long var2, FileDescriptor var4, Runnable var5) {
      super(var1, var2, var4, var5);
   }

   DirectByteBufferR(DirectBuffer var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public ByteBuffer slice() {
      int var1 = this.position();
      int var2 = this.limit();

      assert var1 <= var2;

      int var3 = var1 <= var2 ? var2 - var1 : 0;
      int var4 = var1 << 0;

      assert var4 >= 0;

      return new DirectByteBufferR(this, -1, 0, var3, var3, var4);
   }

   public ByteBuffer duplicate() {
      return new DirectByteBufferR(this, this.markValue(), this.position(), this.limit(), this.capacity(), 0);
   }

   public ByteBuffer asReadOnlyBuffer() {
      return this.duplicate();
   }

   public ByteBuffer put(byte var1) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuffer put(int var1, byte var2) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuffer put(ByteBuffer var1) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuffer put(byte[] var1, int var2, int var3) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuffer compact() {
      throw new ReadOnlyBufferException();
   }

   public boolean isDirect() {
      return true;
   }

   public boolean isReadOnly() {
      return true;
   }

   byte _get(int var1) {
      return unsafe.getByte(this.address + (long)var1);
   }

   void _put(int var1, byte var2) {
      throw new ReadOnlyBufferException();
   }

   private ByteBuffer putChar(long var1, char var3) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuffer putChar(char var1) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuffer putChar(int var1, char var2) {
      throw new ReadOnlyBufferException();
   }

   public CharBuffer asCharBuffer() {
      int var1 = this.position();
      int var2 = this.limit();

      assert var1 <= var2;

      int var3 = var1 <= var2 ? var2 - var1 : 0;
      int var4 = var3 >> 1;
      if (!unaligned && (this.address + (long)var1) % 2L != 0L) {
         return (CharBuffer)(this.bigEndian ? new ByteBufferAsCharBufferRB(this, -1, 0, var4, var4, var1) : new ByteBufferAsCharBufferRL(this, -1, 0, var4, var4, var1));
      } else {
         return (CharBuffer)(this.nativeByteOrder ? new DirectCharBufferRU(this, -1, 0, var4, var4, var1) : new DirectCharBufferRS(this, -1, 0, var4, var4, var1));
      }
   }

   private ByteBuffer putShort(long var1, short var3) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuffer putShort(short var1) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuffer putShort(int var1, short var2) {
      throw new ReadOnlyBufferException();
   }

   public ShortBuffer asShortBuffer() {
      int var1 = this.position();
      int var2 = this.limit();

      assert var1 <= var2;

      int var3 = var1 <= var2 ? var2 - var1 : 0;
      int var4 = var3 >> 1;
      if (!unaligned && (this.address + (long)var1) % 2L != 0L) {
         return (ShortBuffer)(this.bigEndian ? new ByteBufferAsShortBufferRB(this, -1, 0, var4, var4, var1) : new ByteBufferAsShortBufferRL(this, -1, 0, var4, var4, var1));
      } else {
         return (ShortBuffer)(this.nativeByteOrder ? new DirectShortBufferRU(this, -1, 0, var4, var4, var1) : new DirectShortBufferRS(this, -1, 0, var4, var4, var1));
      }
   }

   private ByteBuffer putInt(long var1, int var3) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuffer putInt(int var1) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuffer putInt(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   public IntBuffer asIntBuffer() {
      int var1 = this.position();
      int var2 = this.limit();

      assert var1 <= var2;

      int var3 = var1 <= var2 ? var2 - var1 : 0;
      int var4 = var3 >> 2;
      if (!unaligned && (this.address + (long)var1) % 4L != 0L) {
         return (IntBuffer)(this.bigEndian ? new ByteBufferAsIntBufferRB(this, -1, 0, var4, var4, var1) : new ByteBufferAsIntBufferRL(this, -1, 0, var4, var4, var1));
      } else {
         return (IntBuffer)(this.nativeByteOrder ? new DirectIntBufferRU(this, -1, 0, var4, var4, var1) : new DirectIntBufferRS(this, -1, 0, var4, var4, var1));
      }
   }

   private ByteBuffer putLong(long var1, long var3) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuffer putLong(long var1) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuffer putLong(int var1, long var2) {
      throw new ReadOnlyBufferException();
   }

   public LongBuffer asLongBuffer() {
      int var1 = this.position();
      int var2 = this.limit();

      assert var1 <= var2;

      int var3 = var1 <= var2 ? var2 - var1 : 0;
      int var4 = var3 >> 3;
      if (!unaligned && (this.address + (long)var1) % 8L != 0L) {
         return (LongBuffer)(this.bigEndian ? new ByteBufferAsLongBufferRB(this, -1, 0, var4, var4, var1) : new ByteBufferAsLongBufferRL(this, -1, 0, var4, var4, var1));
      } else {
         return (LongBuffer)(this.nativeByteOrder ? new DirectLongBufferRU(this, -1, 0, var4, var4, var1) : new DirectLongBufferRS(this, -1, 0, var4, var4, var1));
      }
   }

   private ByteBuffer putFloat(long var1, float var3) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuffer putFloat(float var1) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuffer putFloat(int var1, float var2) {
      throw new ReadOnlyBufferException();
   }

   public FloatBuffer asFloatBuffer() {
      int var1 = this.position();
      int var2 = this.limit();

      assert var1 <= var2;

      int var3 = var1 <= var2 ? var2 - var1 : 0;
      int var4 = var3 >> 2;
      if (!unaligned && (this.address + (long)var1) % 4L != 0L) {
         return (FloatBuffer)(this.bigEndian ? new ByteBufferAsFloatBufferRB(this, -1, 0, var4, var4, var1) : new ByteBufferAsFloatBufferRL(this, -1, 0, var4, var4, var1));
      } else {
         return (FloatBuffer)(this.nativeByteOrder ? new DirectFloatBufferRU(this, -1, 0, var4, var4, var1) : new DirectFloatBufferRS(this, -1, 0, var4, var4, var1));
      }
   }

   private ByteBuffer putDouble(long var1, double var3) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuffer putDouble(double var1) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuffer putDouble(int var1, double var2) {
      throw new ReadOnlyBufferException();
   }

   public DoubleBuffer asDoubleBuffer() {
      int var1 = this.position();
      int var2 = this.limit();

      assert var1 <= var2;

      int var3 = var1 <= var2 ? var2 - var1 : 0;
      int var4 = var3 >> 3;
      if (!unaligned && (this.address + (long)var1) % 8L != 0L) {
         return (DoubleBuffer)(this.bigEndian ? new ByteBufferAsDoubleBufferRB(this, -1, 0, var4, var4, var1) : new ByteBufferAsDoubleBufferRL(this, -1, 0, var4, var4, var1));
      } else {
         return (DoubleBuffer)(this.nativeByteOrder ? new DirectDoubleBufferRU(this, -1, 0, var4, var4, var1) : new DirectDoubleBufferRS(this, -1, 0, var4, var4, var1));
      }
   }
}
