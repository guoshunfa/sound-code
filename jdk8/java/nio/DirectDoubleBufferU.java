package java.nio;

import sun.misc.Cleaner;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

class DirectDoubleBufferU extends DoubleBuffer implements DirectBuffer {
   protected static final Unsafe unsafe = Bits.unsafe();
   private static final long arrayBaseOffset;
   protected static final boolean unaligned;
   private final Object att;

   public Object attachment() {
      return this.att;
   }

   public Cleaner cleaner() {
      return null;
   }

   DirectDoubleBufferU(DirectBuffer var1, int var2, int var3, int var4, int var5, int var6) {
      super(var2, var3, var4, var5);
      this.address = var1.address() + (long)var6;
      this.att = var1;
   }

   public DoubleBuffer slice() {
      int var1 = this.position();
      int var2 = this.limit();

      assert var1 <= var2;

      int var3 = var1 <= var2 ? var2 - var1 : 0;
      int var4 = var1 << 3;

      assert var4 >= 0;

      return new DirectDoubleBufferU(this, -1, 0, var3, var3, var4);
   }

   public DoubleBuffer duplicate() {
      return new DirectDoubleBufferU(this, this.markValue(), this.position(), this.limit(), this.capacity(), 0);
   }

   public DoubleBuffer asReadOnlyBuffer() {
      return new DirectDoubleBufferRU(this, this.markValue(), this.position(), this.limit(), this.capacity(), 0);
   }

   public long address() {
      return this.address;
   }

   private long ix(int var1) {
      return this.address + ((long)var1 << 3);
   }

   public double get() {
      return unsafe.getDouble(this.ix(this.nextGetIndex()));
   }

   public double get(int var1) {
      return unsafe.getDouble(this.ix(this.checkIndex(var1)));
   }

   public DoubleBuffer get(double[] var1, int var2, int var3) {
      if ((long)var3 << 3 > 6L) {
         checkBounds(var2, var3, var1.length);
         int var4 = this.position();
         int var5 = this.limit();

         assert var4 <= var5;

         int var6 = var4 <= var5 ? var5 - var4 : 0;
         if (var3 > var6) {
            throw new BufferUnderflowException();
         }

         if (this.order() != ByteOrder.nativeOrder()) {
            Bits.copyToLongArray(this.ix(var4), var1, (long)var2 << 3, (long)var3 << 3);
         } else {
            Bits.copyToArray(this.ix(var4), var1, arrayBaseOffset, (long)var2 << 3, (long)var3 << 3);
         }

         this.position(var4 + var3);
      } else {
         super.get(var1, var2, var3);
      }

      return this;
   }

   public DoubleBuffer put(double var1) {
      unsafe.putDouble(this.ix(this.nextPutIndex()), var1);
      return this;
   }

   public DoubleBuffer put(int var1, double var2) {
      unsafe.putDouble(this.ix(this.checkIndex(var1)), var2);
      return this;
   }

   public DoubleBuffer put(DoubleBuffer var1) {
      int var3;
      int var4;
      if (var1 instanceof DirectDoubleBufferU) {
         if (var1 == this) {
            throw new IllegalArgumentException();
         }

         DirectDoubleBufferU var2 = (DirectDoubleBufferU)var1;
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

         unsafe.copyMemory(var2.ix(var3), this.ix(var6), (long)var5 << 3);
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

   public DoubleBuffer put(double[] var1, int var2, int var3) {
      if ((long)var3 << 3 > 6L) {
         checkBounds(var2, var3, var1.length);
         int var4 = this.position();
         int var5 = this.limit();

         assert var4 <= var5;

         int var6 = var4 <= var5 ? var5 - var4 : 0;
         if (var3 > var6) {
            throw new BufferOverflowException();
         }

         if (this.order() != ByteOrder.nativeOrder()) {
            Bits.copyFromLongArray(var1, (long)var2 << 3, this.ix(var4), (long)var3 << 3);
         } else {
            Bits.copyFromArray(var1, arrayBaseOffset, (long)var2 << 3, this.ix(var4), (long)var3 << 3);
         }

         this.position(var4 + var3);
      } else {
         super.put(var1, var2, var3);
      }

      return this;
   }

   public DoubleBuffer compact() {
      int var1 = this.position();
      int var2 = this.limit();

      assert var1 <= var2;

      int var3 = var1 <= var2 ? var2 - var1 : 0;
      unsafe.copyMemory(this.ix(var1), this.ix(0), (long)var3 << 3);
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

   public ByteOrder order() {
      return ByteOrder.nativeOrder() != ByteOrder.BIG_ENDIAN ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
   }

   static {
      arrayBaseOffset = (long)unsafe.arrayBaseOffset(double[].class);
      unaligned = Bits.unaligned();
   }
}
