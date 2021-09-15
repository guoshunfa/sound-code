package com.sun.corba.se.impl.ior;

public class ByteBuffer {
   protected byte[] elementData;
   protected int elementCount;
   protected int capacityIncrement;

   public ByteBuffer(int var1, int var2) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Illegal Capacity: " + var1);
      } else {
         this.elementData = new byte[var1];
         this.capacityIncrement = var2;
      }
   }

   public ByteBuffer(int var1) {
      this(var1, 0);
   }

   public ByteBuffer() {
      this(200);
   }

   public void trimToSize() {
      int var1 = this.elementData.length;
      if (this.elementCount < var1) {
         byte[] var2 = this.elementData;
         this.elementData = new byte[this.elementCount];
         System.arraycopy(var2, 0, this.elementData, 0, this.elementCount);
      }

   }

   private void ensureCapacityHelper(int var1) {
      int var2 = this.elementData.length;
      if (var1 > var2) {
         byte[] var3 = this.elementData;
         int var4 = this.capacityIncrement > 0 ? var2 + this.capacityIncrement : var2 * 2;
         if (var4 < var1) {
            var4 = var1;
         }

         this.elementData = new byte[var4];
         System.arraycopy(var3, 0, this.elementData, 0, this.elementCount);
      }

   }

   public int capacity() {
      return this.elementData.length;
   }

   public int size() {
      return this.elementCount;
   }

   public boolean isEmpty() {
      return this.elementCount == 0;
   }

   public void append(byte var1) {
      this.ensureCapacityHelper(this.elementCount + 1);
      this.elementData[this.elementCount++] = var1;
   }

   public void append(int var1) {
      this.ensureCapacityHelper(this.elementCount + 4);
      this.doAppend(var1);
   }

   private void doAppend(int var1) {
      int var2 = var1;

      for(int var3 = 0; var3 < 4; ++var3) {
         this.elementData[this.elementCount + var3] = (byte)(var2 & 255);
         var2 >>= 8;
      }

      this.elementCount += 4;
   }

   public void append(String var1) {
      byte[] var2 = var1.getBytes();
      this.ensureCapacityHelper(this.elementCount + var2.length + 4);
      this.doAppend(var2.length);
      System.arraycopy(var2, 0, this.elementData, this.elementCount, var2.length);
      this.elementCount += var2.length;
   }

   public byte[] toArray() {
      return this.elementData;
   }
}
