package sun.java2d.pipe;

import sun.misc.Unsafe;

public class RenderBuffer {
   protected static final long SIZEOF_BYTE = 1L;
   protected static final long SIZEOF_SHORT = 2L;
   protected static final long SIZEOF_INT = 4L;
   protected static final long SIZEOF_FLOAT = 4L;
   protected static final long SIZEOF_LONG = 8L;
   protected static final long SIZEOF_DOUBLE = 8L;
   private static final int COPY_FROM_ARRAY_THRESHOLD = 6;
   protected final Unsafe unsafe = Unsafe.getUnsafe();
   protected final long baseAddress;
   protected final long endAddress;
   protected long curAddress;
   protected final int capacity;

   protected RenderBuffer(int var1) {
      this.curAddress = this.baseAddress = this.unsafe.allocateMemory((long)var1);
      this.endAddress = this.baseAddress + (long)var1;
      this.capacity = var1;
   }

   public static RenderBuffer allocate(int var0) {
      return new RenderBuffer(var0);
   }

   public final long getAddress() {
      return this.baseAddress;
   }

   public final int capacity() {
      return this.capacity;
   }

   public final int remaining() {
      return (int)(this.endAddress - this.curAddress);
   }

   public final int position() {
      return (int)(this.curAddress - this.baseAddress);
   }

   public final void position(long var1) {
      this.curAddress = this.baseAddress + var1;
   }

   public final void clear() {
      this.curAddress = this.baseAddress;
   }

   public final RenderBuffer skip(long var1) {
      this.curAddress += var1;
      return this;
   }

   public final RenderBuffer putByte(byte var1) {
      this.unsafe.putByte(this.curAddress, var1);
      ++this.curAddress;
      return this;
   }

   public RenderBuffer put(byte[] var1) {
      return this.put((byte[])var1, 0, var1.length);
   }

   public RenderBuffer put(byte[] var1, int var2, int var3) {
      if (var3 > 6) {
         long var4 = (long)var2 * 1L + (long)Unsafe.ARRAY_BYTE_BASE_OFFSET;
         long var6 = (long)var3 * 1L;
         this.unsafe.copyMemory(var1, var4, (Object)null, this.curAddress, var6);
         this.position((long)this.position() + var6);
      } else {
         int var8 = var2 + var3;

         for(int var5 = var2; var5 < var8; ++var5) {
            this.putByte(var1[var5]);
         }
      }

      return this;
   }

   public final RenderBuffer putShort(short var1) {
      this.unsafe.putShort(this.curAddress, var1);
      this.curAddress += 2L;
      return this;
   }

   public RenderBuffer put(short[] var1) {
      return this.put((short[])var1, 0, var1.length);
   }

   public RenderBuffer put(short[] var1, int var2, int var3) {
      if (var3 > 6) {
         long var4 = (long)var2 * 2L + (long)Unsafe.ARRAY_SHORT_BASE_OFFSET;
         long var6 = (long)var3 * 2L;
         this.unsafe.copyMemory(var1, var4, (Object)null, this.curAddress, var6);
         this.position((long)this.position() + var6);
      } else {
         int var8 = var2 + var3;

         for(int var5 = var2; var5 < var8; ++var5) {
            this.putShort(var1[var5]);
         }
      }

      return this;
   }

   public final RenderBuffer putInt(int var1, int var2) {
      this.unsafe.putInt(this.baseAddress + (long)var1, var2);
      return this;
   }

   public final RenderBuffer putInt(int var1) {
      this.unsafe.putInt(this.curAddress, var1);
      this.curAddress += 4L;
      return this;
   }

   public RenderBuffer put(int[] var1) {
      return this.put((int[])var1, 0, var1.length);
   }

   public RenderBuffer put(int[] var1, int var2, int var3) {
      if (var3 > 6) {
         long var4 = (long)var2 * 4L + (long)Unsafe.ARRAY_INT_BASE_OFFSET;
         long var6 = (long)var3 * 4L;
         this.unsafe.copyMemory(var1, var4, (Object)null, this.curAddress, var6);
         this.position((long)this.position() + var6);
      } else {
         int var8 = var2 + var3;

         for(int var5 = var2; var5 < var8; ++var5) {
            this.putInt(var1[var5]);
         }
      }

      return this;
   }

   public final RenderBuffer putFloat(float var1) {
      this.unsafe.putFloat(this.curAddress, var1);
      this.curAddress += 4L;
      return this;
   }

   public RenderBuffer put(float[] var1) {
      return this.put((float[])var1, 0, var1.length);
   }

   public RenderBuffer put(float[] var1, int var2, int var3) {
      if (var3 > 6) {
         long var4 = (long)var2 * 4L + (long)Unsafe.ARRAY_FLOAT_BASE_OFFSET;
         long var6 = (long)var3 * 4L;
         this.unsafe.copyMemory(var1, var4, (Object)null, this.curAddress, var6);
         this.position((long)this.position() + var6);
      } else {
         int var8 = var2 + var3;

         for(int var5 = var2; var5 < var8; ++var5) {
            this.putFloat(var1[var5]);
         }
      }

      return this;
   }

   public final RenderBuffer putLong(long var1) {
      this.unsafe.putLong(this.curAddress, var1);
      this.curAddress += 8L;
      return this;
   }

   public RenderBuffer put(long[] var1) {
      return this.put((long[])var1, 0, var1.length);
   }

   public RenderBuffer put(long[] var1, int var2, int var3) {
      if (var3 > 6) {
         long var4 = (long)var2 * 8L + (long)Unsafe.ARRAY_LONG_BASE_OFFSET;
         long var6 = (long)var3 * 8L;
         this.unsafe.copyMemory(var1, var4, (Object)null, this.curAddress, var6);
         this.position((long)this.position() + var6);
      } else {
         int var8 = var2 + var3;

         for(int var5 = var2; var5 < var8; ++var5) {
            this.putLong(var1[var5]);
         }
      }

      return this;
   }

   public final RenderBuffer putDouble(double var1) {
      this.unsafe.putDouble(this.curAddress, var1);
      this.curAddress += 8L;
      return this;
   }
}
