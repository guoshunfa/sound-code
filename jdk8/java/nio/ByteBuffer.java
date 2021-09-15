package java.nio;

public abstract class ByteBuffer extends Buffer implements Comparable<ByteBuffer> {
   final byte[] hb;
   final int offset;
   boolean isReadOnly;
   boolean bigEndian;
   boolean nativeByteOrder;

   ByteBuffer(int var1, int var2, int var3, int var4, byte[] var5, int var6) {
      super(var1, var2, var3, var4);
      this.bigEndian = true;
      this.nativeByteOrder = Bits.byteOrder() == ByteOrder.BIG_ENDIAN;
      this.hb = var5;
      this.offset = var6;
   }

   ByteBuffer(int var1, int var2, int var3, int var4) {
      this(var1, var2, var3, var4, (byte[])null, 0);
   }

   public static ByteBuffer allocateDirect(int var0) {
      return new DirectByteBuffer(var0);
   }

   public static ByteBuffer allocate(int var0) {
      if (var0 < 0) {
         throw new IllegalArgumentException();
      } else {
         return new HeapByteBuffer(var0, var0);
      }
   }

   public static ByteBuffer wrap(byte[] var0, int var1, int var2) {
      try {
         return new HeapByteBuffer(var0, var1, var2);
      } catch (IllegalArgumentException var4) {
         throw new IndexOutOfBoundsException();
      }
   }

   public static ByteBuffer wrap(byte[] var0) {
      return wrap(var0, 0, var0.length);
   }

   public abstract ByteBuffer slice();

   public abstract ByteBuffer duplicate();

   public abstract ByteBuffer asReadOnlyBuffer();

   public abstract byte get();

   public abstract ByteBuffer put(byte var1);

   public abstract byte get(int var1);

   public abstract ByteBuffer put(int var1, byte var2);

   public ByteBuffer get(byte[] var1, int var2, int var3) {
      checkBounds(var2, var3, var1.length);
      if (var3 > this.remaining()) {
         throw new BufferUnderflowException();
      } else {
         int var4 = var2 + var3;

         for(int var5 = var2; var5 < var4; ++var5) {
            var1[var5] = this.get();
         }

         return this;
      }
   }

   public ByteBuffer get(byte[] var1) {
      return this.get(var1, 0, var1.length);
   }

   public ByteBuffer put(ByteBuffer var1) {
      if (var1 == this) {
         throw new IllegalArgumentException();
      } else if (this.isReadOnly()) {
         throw new ReadOnlyBufferException();
      } else {
         int var2 = var1.remaining();
         if (var2 > this.remaining()) {
            throw new BufferOverflowException();
         } else {
            for(int var3 = 0; var3 < var2; ++var3) {
               this.put(var1.get());
            }

            return this;
         }
      }
   }

   public ByteBuffer put(byte[] var1, int var2, int var3) {
      checkBounds(var2, var3, var1.length);
      if (var3 > this.remaining()) {
         throw new BufferOverflowException();
      } else {
         int var4 = var2 + var3;

         for(int var5 = var2; var5 < var4; ++var5) {
            this.put(var1[var5]);
         }

         return this;
      }
   }

   public final ByteBuffer put(byte[] var1) {
      return this.put(var1, 0, var1.length);
   }

   public final boolean hasArray() {
      return this.hb != null && !this.isReadOnly;
   }

   public final byte[] array() {
      if (this.hb == null) {
         throw new UnsupportedOperationException();
      } else if (this.isReadOnly) {
         throw new ReadOnlyBufferException();
      } else {
         return this.hb;
      }
   }

   public final int arrayOffset() {
      if (this.hb == null) {
         throw new UnsupportedOperationException();
      } else if (this.isReadOnly) {
         throw new ReadOnlyBufferException();
      } else {
         return this.offset;
      }
   }

   public abstract ByteBuffer compact();

   public abstract boolean isDirect();

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append(this.getClass().getName());
      var1.append("[pos=");
      var1.append(this.position());
      var1.append(" lim=");
      var1.append(this.limit());
      var1.append(" cap=");
      var1.append(this.capacity());
      var1.append("]");
      return var1.toString();
   }

   public int hashCode() {
      int var1 = 1;
      int var2 = this.position();

      for(int var3 = this.limit() - 1; var3 >= var2; --var3) {
         var1 = 31 * var1 + this.get(var3);
      }

      return var1;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ByteBuffer)) {
         return false;
      } else {
         ByteBuffer var2 = (ByteBuffer)var1;
         if (this.remaining() != var2.remaining()) {
            return false;
         } else {
            int var3 = this.position();
            int var4 = this.limit() - 1;

            for(int var5 = var2.limit() - 1; var4 >= var3; --var5) {
               if (!equals(this.get(var4), var2.get(var5))) {
                  return false;
               }

               --var4;
            }

            return true;
         }
      }
   }

   private static boolean equals(byte var0, byte var1) {
      return var0 == var1;
   }

   public int compareTo(ByteBuffer var1) {
      int var2 = this.position() + Math.min(this.remaining(), var1.remaining());
      int var3 = this.position();

      for(int var4 = var1.position(); var3 < var2; ++var4) {
         int var5 = compare(this.get(var3), var1.get(var4));
         if (var5 != 0) {
            return var5;
         }

         ++var3;
      }

      return this.remaining() - var1.remaining();
   }

   private static int compare(byte var0, byte var1) {
      return Byte.compare(var0, var1);
   }

   public final ByteOrder order() {
      return this.bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
   }

   public final ByteBuffer order(ByteOrder var1) {
      this.bigEndian = var1 == ByteOrder.BIG_ENDIAN;
      this.nativeByteOrder = this.bigEndian == (Bits.byteOrder() == ByteOrder.BIG_ENDIAN);
      return this;
   }

   abstract byte _get(int var1);

   abstract void _put(int var1, byte var2);

   public abstract char getChar();

   public abstract ByteBuffer putChar(char var1);

   public abstract char getChar(int var1);

   public abstract ByteBuffer putChar(int var1, char var2);

   public abstract CharBuffer asCharBuffer();

   public abstract short getShort();

   public abstract ByteBuffer putShort(short var1);

   public abstract short getShort(int var1);

   public abstract ByteBuffer putShort(int var1, short var2);

   public abstract ShortBuffer asShortBuffer();

   public abstract int getInt();

   public abstract ByteBuffer putInt(int var1);

   public abstract int getInt(int var1);

   public abstract ByteBuffer putInt(int var1, int var2);

   public abstract IntBuffer asIntBuffer();

   public abstract long getLong();

   public abstract ByteBuffer putLong(long var1);

   public abstract long getLong(int var1);

   public abstract ByteBuffer putLong(int var1, long var2);

   public abstract LongBuffer asLongBuffer();

   public abstract float getFloat();

   public abstract ByteBuffer putFloat(float var1);

   public abstract float getFloat(int var1);

   public abstract ByteBuffer putFloat(int var1, float var2);

   public abstract FloatBuffer asFloatBuffer();

   public abstract double getDouble();

   public abstract ByteBuffer putDouble(double var1);

   public abstract double getDouble(int var1);

   public abstract ByteBuffer putDouble(int var1, double var2);

   public abstract DoubleBuffer asDoubleBuffer();
}
