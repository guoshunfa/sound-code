package java.nio;

import java.io.FileDescriptor;
import sun.misc.Cleaner;
import sun.misc.Unsafe;
import sun.misc.VM;
import sun.nio.ch.DirectBuffer;

class DirectByteBuffer extends MappedByteBuffer implements DirectBuffer {
   protected static final Unsafe unsafe = Bits.unsafe();
   private static final long arrayBaseOffset;
   protected static final boolean unaligned;
   private final Object att;
   private final Cleaner cleaner;

   public Object attachment() {
      return this.att;
   }

   public Cleaner cleaner() {
      return this.cleaner;
   }

   DirectByteBuffer(int var1) {
      super(-1, 0, var1, var1);
      boolean var2 = VM.isDirectMemoryPageAligned();
      int var3 = Bits.pageSize();
      long var4 = Math.max(1L, (long)var1 + (long)(var2 ? var3 : 0));
      Bits.reserveMemory(var4, var1);
      long var6 = 0L;

      try {
         var6 = unsafe.allocateMemory(var4);
      } catch (OutOfMemoryError var9) {
         Bits.unreserveMemory(var4, var1);
         throw var9;
      }

      unsafe.setMemory(var6, var4, (byte)0);
      if (var2 && var6 % (long)var3 != 0L) {
         this.address = var6 + (long)var3 - (var6 & (long)(var3 - 1));
      } else {
         this.address = var6;
      }

      this.cleaner = Cleaner.create(this, new DirectByteBuffer.Deallocator(var6, var4, var1));
      this.att = null;
   }

   DirectByteBuffer(long var1, int var3, Object var4) {
      super(-1, 0, var3, var3);
      this.address = var1;
      this.cleaner = null;
      this.att = var4;
   }

   private DirectByteBuffer(long var1, int var3) {
      super(-1, 0, var3, var3);
      this.address = var1;
      this.cleaner = null;
      this.att = null;
   }

   protected DirectByteBuffer(int var1, long var2, FileDescriptor var4, Runnable var5) {
      super(-1, 0, var1, var1, var4);
      this.address = var2;
      this.cleaner = Cleaner.create(this, var5);
      this.att = null;
   }

   DirectByteBuffer(DirectBuffer var1, int var2, int var3, int var4, int var5, int var6) {
      super(var2, var3, var4, var5);
      this.address = var1.address() + (long)var6;
      this.cleaner = null;
      this.att = var1;
   }

   public ByteBuffer slice() {
      int var1 = this.position();
      int var2 = this.limit();

      assert var1 <= var2;

      int var3 = var1 <= var2 ? var2 - var1 : 0;
      int var4 = var1 << 0;

      assert var4 >= 0;

      return new DirectByteBuffer(this, -1, 0, var3, var3, var4);
   }

   public ByteBuffer duplicate() {
      return new DirectByteBuffer(this, this.markValue(), this.position(), this.limit(), this.capacity(), 0);
   }

   public ByteBuffer asReadOnlyBuffer() {
      return new DirectByteBufferR(this, this.markValue(), this.position(), this.limit(), this.capacity(), 0);
   }

   public long address() {
      return this.address;
   }

   private long ix(int var1) {
      return this.address + ((long)var1 << 0);
   }

   public byte get() {
      return unsafe.getByte(this.ix(this.nextGetIndex()));
   }

   public byte get(int var1) {
      return unsafe.getByte(this.ix(this.checkIndex(var1)));
   }

   public ByteBuffer get(byte[] var1, int var2, int var3) {
      if ((long)var3 << 0 > 6L) {
         checkBounds(var2, var3, var1.length);
         int var4 = this.position();
         int var5 = this.limit();

         assert var4 <= var5;

         int var6 = var4 <= var5 ? var5 - var4 : 0;
         if (var3 > var6) {
            throw new BufferUnderflowException();
         }

         Bits.copyToArray(this.ix(var4), var1, arrayBaseOffset, (long)var2 << 0, (long)var3 << 0);
         this.position(var4 + var3);
      } else {
         super.get(var1, var2, var3);
      }

      return this;
   }

   public ByteBuffer put(byte var1) {
      unsafe.putByte(this.ix(this.nextPutIndex()), var1);
      return this;
   }

   public ByteBuffer put(int var1, byte var2) {
      unsafe.putByte(this.ix(this.checkIndex(var1)), var2);
      return this;
   }

   public ByteBuffer put(ByteBuffer var1) {
      int var3;
      int var4;
      if (var1 instanceof DirectByteBuffer) {
         if (var1 == this) {
            throw new IllegalArgumentException();
         }

         DirectByteBuffer var2 = (DirectByteBuffer)var1;
         var3 = var2.position();
         var4 = var2.limit();

         assert var3 <= var4;

         int var5 = var3 <= var4 ? var4 - var3 : 0;
         int var6 = this.position();
         int var7 = this.limit();

         assert var6 <= var7;

         int var8 = var6 <= var7 ? var7 - var6 : 0;
         if (var5 > var8) {
            throw new BufferOverflowException();
         }

         unsafe.copyMemory(var2.ix(var3), this.ix(var6), (long)var5 << 0);
         var2.position(var3 + var5);
         this.position(var6 + var5);
      } else if (var1.hb != null) {
         int var9 = var1.position();
         var3 = var1.limit();

         assert var9 <= var3;

         var4 = var9 <= var3 ? var3 - var9 : 0;
         this.put(var1.hb, var1.offset + var9, var4);
         var1.position(var9 + var4);
      } else {
         super.put(var1);
      }

      return this;
   }

   public ByteBuffer put(byte[] var1, int var2, int var3) {
      if ((long)var3 << 0 > 6L) {
         checkBounds(var2, var3, var1.length);
         int var4 = this.position();
         int var5 = this.limit();

         assert var4 <= var5;

         int var6 = var4 <= var5 ? var5 - var4 : 0;
         if (var3 > var6) {
            throw new BufferOverflowException();
         }

         Bits.copyFromArray(var1, arrayBaseOffset, (long)var2 << 0, this.ix(var4), (long)var3 << 0);
         this.position(var4 + var3);
      } else {
         super.put(var1, var2, var3);
      }

      return this;
   }

   public ByteBuffer compact() {
      int var1 = this.position();
      int var2 = this.limit();

      assert var1 <= var2;

      int var3 = var1 <= var2 ? var2 - var1 : 0;
      unsafe.copyMemory(this.ix(var1), this.ix(0), (long)var3 << 0);
      this.position(var3);
      this.limit(this.capacity());
      this.discardMark();
      return this;
   }

   public boolean isDirect() {
      return true;
   }

   public boolean isReadOnly() {
      return false;
   }

   byte _get(int var1) {
      return unsafe.getByte(this.address + (long)var1);
   }

   void _put(int var1, byte var2) {
      unsafe.putByte(this.address + (long)var1, var2);
   }

   private char getChar(long var1) {
      if (unaligned) {
         char var3 = unsafe.getChar(var1);
         return this.nativeByteOrder ? var3 : Bits.swap(var3);
      } else {
         return Bits.getChar(var1, this.bigEndian);
      }
   }

   public char getChar() {
      return this.getChar(this.ix(this.nextGetIndex(2)));
   }

   public char getChar(int var1) {
      return this.getChar(this.ix(this.checkIndex(var1, 2)));
   }

   private ByteBuffer putChar(long var1, char var3) {
      if (unaligned) {
         unsafe.putChar(var1, this.nativeByteOrder ? var3 : Bits.swap(var3));
      } else {
         Bits.putChar(var1, var3, this.bigEndian);
      }

      return this;
   }

   public ByteBuffer putChar(char var1) {
      this.putChar(this.ix(this.nextPutIndex(2)), var1);
      return this;
   }

   public ByteBuffer putChar(int var1, char var2) {
      this.putChar(this.ix(this.checkIndex(var1, 2)), var2);
      return this;
   }

   public CharBuffer asCharBuffer() {
      int var1 = this.position();
      int var2 = this.limit();

      assert var1 <= var2;

      int var3 = var1 <= var2 ? var2 - var1 : 0;
      int var4 = var3 >> 1;
      if (!unaligned && (this.address + (long)var1) % 2L != 0L) {
         return (CharBuffer)(this.bigEndian ? new ByteBufferAsCharBufferB(this, -1, 0, var4, var4, var1) : new ByteBufferAsCharBufferL(this, -1, 0, var4, var4, var1));
      } else {
         return (CharBuffer)(this.nativeByteOrder ? new DirectCharBufferU(this, -1, 0, var4, var4, var1) : new DirectCharBufferS(this, -1, 0, var4, var4, var1));
      }
   }

   private short getShort(long var1) {
      if (unaligned) {
         short var3 = unsafe.getShort(var1);
         return this.nativeByteOrder ? var3 : Bits.swap(var3);
      } else {
         return Bits.getShort(var1, this.bigEndian);
      }
   }

   public short getShort() {
      return this.getShort(this.ix(this.nextGetIndex(2)));
   }

   public short getShort(int var1) {
      return this.getShort(this.ix(this.checkIndex(var1, 2)));
   }

   private ByteBuffer putShort(long var1, short var3) {
      if (unaligned) {
         unsafe.putShort(var1, this.nativeByteOrder ? var3 : Bits.swap(var3));
      } else {
         Bits.putShort(var1, var3, this.bigEndian);
      }

      return this;
   }

   public ByteBuffer putShort(short var1) {
      this.putShort(this.ix(this.nextPutIndex(2)), var1);
      return this;
   }

   public ByteBuffer putShort(int var1, short var2) {
      this.putShort(this.ix(this.checkIndex(var1, 2)), var2);
      return this;
   }

   public ShortBuffer asShortBuffer() {
      int var1 = this.position();
      int var2 = this.limit();

      assert var1 <= var2;

      int var3 = var1 <= var2 ? var2 - var1 : 0;
      int var4 = var3 >> 1;
      if (!unaligned && (this.address + (long)var1) % 2L != 0L) {
         return (ShortBuffer)(this.bigEndian ? new ByteBufferAsShortBufferB(this, -1, 0, var4, var4, var1) : new ByteBufferAsShortBufferL(this, -1, 0, var4, var4, var1));
      } else {
         return (ShortBuffer)(this.nativeByteOrder ? new DirectShortBufferU(this, -1, 0, var4, var4, var1) : new DirectShortBufferS(this, -1, 0, var4, var4, var1));
      }
   }

   private int getInt(long var1) {
      if (unaligned) {
         int var3 = unsafe.getInt(var1);
         return this.nativeByteOrder ? var3 : Bits.swap(var3);
      } else {
         return Bits.getInt(var1, this.bigEndian);
      }
   }

   public int getInt() {
      return this.getInt(this.ix(this.nextGetIndex(4)));
   }

   public int getInt(int var1) {
      return this.getInt(this.ix(this.checkIndex(var1, 4)));
   }

   private ByteBuffer putInt(long var1, int var3) {
      if (unaligned) {
         unsafe.putInt(var1, this.nativeByteOrder ? var3 : Bits.swap(var3));
      } else {
         Bits.putInt(var1, var3, this.bigEndian);
      }

      return this;
   }

   public ByteBuffer putInt(int var1) {
      this.putInt(this.ix(this.nextPutIndex(4)), var1);
      return this;
   }

   public ByteBuffer putInt(int var1, int var2) {
      this.putInt(this.ix(this.checkIndex(var1, 4)), var2);
      return this;
   }

   public IntBuffer asIntBuffer() {
      int var1 = this.position();
      int var2 = this.limit();

      assert var1 <= var2;

      int var3 = var1 <= var2 ? var2 - var1 : 0;
      int var4 = var3 >> 2;
      if (!unaligned && (this.address + (long)var1) % 4L != 0L) {
         return (IntBuffer)(this.bigEndian ? new ByteBufferAsIntBufferB(this, -1, 0, var4, var4, var1) : new ByteBufferAsIntBufferL(this, -1, 0, var4, var4, var1));
      } else {
         return (IntBuffer)(this.nativeByteOrder ? new DirectIntBufferU(this, -1, 0, var4, var4, var1) : new DirectIntBufferS(this, -1, 0, var4, var4, var1));
      }
   }

   private long getLong(long var1) {
      if (unaligned) {
         long var3 = unsafe.getLong(var1);
         return this.nativeByteOrder ? var3 : Bits.swap(var3);
      } else {
         return Bits.getLong(var1, this.bigEndian);
      }
   }

   public long getLong() {
      return this.getLong(this.ix(this.nextGetIndex(8)));
   }

   public long getLong(int var1) {
      return this.getLong(this.ix(this.checkIndex(var1, 8)));
   }

   private ByteBuffer putLong(long var1, long var3) {
      if (unaligned) {
         unsafe.putLong(var1, this.nativeByteOrder ? var3 : Bits.swap(var3));
      } else {
         Bits.putLong(var1, var3, this.bigEndian);
      }

      return this;
   }

   public ByteBuffer putLong(long var1) {
      this.putLong(this.ix(this.nextPutIndex(8)), var1);
      return this;
   }

   public ByteBuffer putLong(int var1, long var2) {
      this.putLong(this.ix(this.checkIndex(var1, 8)), var2);
      return this;
   }

   public LongBuffer asLongBuffer() {
      int var1 = this.position();
      int var2 = this.limit();

      assert var1 <= var2;

      int var3 = var1 <= var2 ? var2 - var1 : 0;
      int var4 = var3 >> 3;
      if (!unaligned && (this.address + (long)var1) % 8L != 0L) {
         return (LongBuffer)(this.bigEndian ? new ByteBufferAsLongBufferB(this, -1, 0, var4, var4, var1) : new ByteBufferAsLongBufferL(this, -1, 0, var4, var4, var1));
      } else {
         return (LongBuffer)(this.nativeByteOrder ? new DirectLongBufferU(this, -1, 0, var4, var4, var1) : new DirectLongBufferS(this, -1, 0, var4, var4, var1));
      }
   }

   private float getFloat(long var1) {
      if (unaligned) {
         int var3 = unsafe.getInt(var1);
         return Float.intBitsToFloat(this.nativeByteOrder ? var3 : Bits.swap(var3));
      } else {
         return Bits.getFloat(var1, this.bigEndian);
      }
   }

   public float getFloat() {
      return this.getFloat(this.ix(this.nextGetIndex(4)));
   }

   public float getFloat(int var1) {
      return this.getFloat(this.ix(this.checkIndex(var1, 4)));
   }

   private ByteBuffer putFloat(long var1, float var3) {
      if (unaligned) {
         int var4 = Float.floatToRawIntBits(var3);
         unsafe.putInt(var1, this.nativeByteOrder ? var4 : Bits.swap(var4));
      } else {
         Bits.putFloat(var1, var3, this.bigEndian);
      }

      return this;
   }

   public ByteBuffer putFloat(float var1) {
      this.putFloat(this.ix(this.nextPutIndex(4)), var1);
      return this;
   }

   public ByteBuffer putFloat(int var1, float var2) {
      this.putFloat(this.ix(this.checkIndex(var1, 4)), var2);
      return this;
   }

   public FloatBuffer asFloatBuffer() {
      int var1 = this.position();
      int var2 = this.limit();

      assert var1 <= var2;

      int var3 = var1 <= var2 ? var2 - var1 : 0;
      int var4 = var3 >> 2;
      if (!unaligned && (this.address + (long)var1) % 4L != 0L) {
         return (FloatBuffer)(this.bigEndian ? new ByteBufferAsFloatBufferB(this, -1, 0, var4, var4, var1) : new ByteBufferAsFloatBufferL(this, -1, 0, var4, var4, var1));
      } else {
         return (FloatBuffer)(this.nativeByteOrder ? new DirectFloatBufferU(this, -1, 0, var4, var4, var1) : new DirectFloatBufferS(this, -1, 0, var4, var4, var1));
      }
   }

   private double getDouble(long var1) {
      if (unaligned) {
         long var3 = unsafe.getLong(var1);
         return Double.longBitsToDouble(this.nativeByteOrder ? var3 : Bits.swap(var3));
      } else {
         return Bits.getDouble(var1, this.bigEndian);
      }
   }

   public double getDouble() {
      return this.getDouble(this.ix(this.nextGetIndex(8)));
   }

   public double getDouble(int var1) {
      return this.getDouble(this.ix(this.checkIndex(var1, 8)));
   }

   private ByteBuffer putDouble(long var1, double var3) {
      if (unaligned) {
         long var5 = Double.doubleToRawLongBits(var3);
         unsafe.putLong(var1, this.nativeByteOrder ? var5 : Bits.swap(var5));
      } else {
         Bits.putDouble(var1, var3, this.bigEndian);
      }

      return this;
   }

   public ByteBuffer putDouble(double var1) {
      this.putDouble(this.ix(this.nextPutIndex(8)), var1);
      return this;
   }

   public ByteBuffer putDouble(int var1, double var2) {
      this.putDouble(this.ix(this.checkIndex(var1, 8)), var2);
      return this;
   }

   public DoubleBuffer asDoubleBuffer() {
      int var1 = this.position();
      int var2 = this.limit();

      assert var1 <= var2;

      int var3 = var1 <= var2 ? var2 - var1 : 0;
      int var4 = var3 >> 3;
      if (!unaligned && (this.address + (long)var1) % 8L != 0L) {
         return (DoubleBuffer)(this.bigEndian ? new ByteBufferAsDoubleBufferB(this, -1, 0, var4, var4, var1) : new ByteBufferAsDoubleBufferL(this, -1, 0, var4, var4, var1));
      } else {
         return (DoubleBuffer)(this.nativeByteOrder ? new DirectDoubleBufferU(this, -1, 0, var4, var4, var1) : new DirectDoubleBufferS(this, -1, 0, var4, var4, var1));
      }
   }

   static {
      arrayBaseOffset = (long)unsafe.arrayBaseOffset(byte[].class);
      unaligned = Bits.unaligned();
   }

   private static class Deallocator implements Runnable {
      private static Unsafe unsafe = Unsafe.getUnsafe();
      private long address;
      private long size;
      private int capacity;

      private Deallocator(long var1, long var3, int var5) {
         assert var1 != 0L;

         this.address = var1;
         this.size = var3;
         this.capacity = var5;
      }

      public void run() {
         if (this.address != 0L) {
            unsafe.freeMemory(this.address);
            this.address = 0L;
            Bits.unreserveMemory(this.size, this.capacity);
         }
      }

      // $FF: synthetic method
      Deallocator(long var1, long var3, int var5, Object var6) {
         this(var1, var3, var5);
      }
   }
}
