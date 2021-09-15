package java.nio;

class HeapDoubleBuffer extends DoubleBuffer {
   HeapDoubleBuffer(int var1, int var2) {
      super(-1, 0, var2, var1, new double[var1], 0);
   }

   HeapDoubleBuffer(double[] var1, int var2, int var3) {
      super(-1, var2, var2 + var3, var1.length, var1, 0);
   }

   protected HeapDoubleBuffer(double[] var1, int var2, int var3, int var4, int var5, int var6) {
      super(var2, var3, var4, var5, var1, var6);
   }

   public DoubleBuffer slice() {
      return new HeapDoubleBuffer(this.hb, -1, 0, this.remaining(), this.remaining(), this.position() + this.offset);
   }

   public DoubleBuffer duplicate() {
      return new HeapDoubleBuffer(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
   }

   public DoubleBuffer asReadOnlyBuffer() {
      return new HeapDoubleBufferR(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
   }

   protected int ix(int var1) {
      return var1 + this.offset;
   }

   public double get() {
      return this.hb[this.ix(this.nextGetIndex())];
   }

   public double get(int var1) {
      return this.hb[this.ix(this.checkIndex(var1))];
   }

   public DoubleBuffer get(double[] var1, int var2, int var3) {
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

   public DoubleBuffer put(double var1) {
      this.hb[this.ix(this.nextPutIndex())] = var1;
      return this;
   }

   public DoubleBuffer put(int var1, double var2) {
      this.hb[this.ix(this.checkIndex(var1))] = var2;
      return this;
   }

   public DoubleBuffer put(double[] var1, int var2, int var3) {
      checkBounds(var2, var3, var1.length);
      if (var3 > this.remaining()) {
         throw new BufferOverflowException();
      } else {
         System.arraycopy(var1, var2, this.hb, this.ix(this.position()), var3);
         this.position(this.position() + var3);
         return this;
      }
   }

   public DoubleBuffer put(DoubleBuffer var1) {
      if (var1 instanceof HeapDoubleBuffer) {
         if (var1 == this) {
            throw new IllegalArgumentException();
         }

         HeapDoubleBuffer var2 = (HeapDoubleBuffer)var1;
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

   public DoubleBuffer compact() {
      System.arraycopy(this.hb, this.ix(this.position()), this.hb, this.ix(0), this.remaining());
      this.position(this.remaining());
      this.limit(this.capacity());
      this.discardMark();
      return this;
   }

   public ByteOrder order() {
      return ByteOrder.nativeOrder();
   }
}
