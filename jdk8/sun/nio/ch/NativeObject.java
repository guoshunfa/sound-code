package sun.nio.ch;

import java.nio.ByteOrder;
import sun.misc.Unsafe;

class NativeObject {
   protected static final Unsafe unsafe = Unsafe.getUnsafe();
   protected long allocationAddress;
   private final long address;
   private static ByteOrder byteOrder = null;
   private static int pageSize = -1;

   NativeObject(long var1) {
      this.allocationAddress = var1;
      this.address = var1;
   }

   NativeObject(long var1, long var3) {
      this.allocationAddress = var1;
      this.address = var1 + var3;
   }

   protected NativeObject(int var1, boolean var2) {
      if (!var2) {
         this.allocationAddress = unsafe.allocateMemory((long)var1);
         this.address = this.allocationAddress;
      } else {
         int var3 = pageSize();
         long var4 = unsafe.allocateMemory((long)(var1 + var3));
         this.allocationAddress = var4;
         this.address = var4 + (long)var3 - (var4 & (long)(var3 - 1));
      }

   }

   long address() {
      return this.address;
   }

   long allocationAddress() {
      return this.allocationAddress;
   }

   NativeObject subObject(int var1) {
      return new NativeObject((long)var1 + this.address);
   }

   NativeObject getObject(int var1) {
      long var2 = 0L;
      switch(addressSize()) {
      case 4:
         var2 = (long)(unsafe.getInt((long)var1 + this.address) & -1);
         break;
      case 8:
         var2 = unsafe.getLong((long)var1 + this.address);
         break;
      default:
         throw new InternalError("Address size not supported");
      }

      return new NativeObject(var2);
   }

   void putObject(int var1, NativeObject var2) {
      switch(addressSize()) {
      case 4:
         this.putInt(var1, (int)(var2.address & -1L));
         break;
      case 8:
         this.putLong(var1, var2.address);
         break;
      default:
         throw new InternalError("Address size not supported");
      }

   }

   final byte getByte(int var1) {
      return unsafe.getByte((long)var1 + this.address);
   }

   final void putByte(int var1, byte var2) {
      unsafe.putByte((long)var1 + this.address, var2);
   }

   final short getShort(int var1) {
      return unsafe.getShort((long)var1 + this.address);
   }

   final void putShort(int var1, short var2) {
      unsafe.putShort((long)var1 + this.address, var2);
   }

   final char getChar(int var1) {
      return unsafe.getChar((long)var1 + this.address);
   }

   final void putChar(int var1, char var2) {
      unsafe.putChar((long)var1 + this.address, var2);
   }

   final int getInt(int var1) {
      return unsafe.getInt((long)var1 + this.address);
   }

   final void putInt(int var1, int var2) {
      unsafe.putInt((long)var1 + this.address, var2);
   }

   final long getLong(int var1) {
      return unsafe.getLong((long)var1 + this.address);
   }

   final void putLong(int var1, long var2) {
      unsafe.putLong((long)var1 + this.address, var2);
   }

   final float getFloat(int var1) {
      return unsafe.getFloat((long)var1 + this.address);
   }

   final void putFloat(int var1, float var2) {
      unsafe.putFloat((long)var1 + this.address, var2);
   }

   final double getDouble(int var1) {
      return unsafe.getDouble((long)var1 + this.address);
   }

   final void putDouble(int var1, double var2) {
      unsafe.putDouble((long)var1 + this.address, var2);
   }

   static int addressSize() {
      return unsafe.addressSize();
   }

   static ByteOrder byteOrder() {
      if (byteOrder != null) {
         return byteOrder;
      } else {
         long var0 = unsafe.allocateMemory(8L);

         try {
            unsafe.putLong(var0, 72623859790382856L);
            byte var2 = unsafe.getByte(var0);
            switch(var2) {
            case 1:
               byteOrder = ByteOrder.BIG_ENDIAN;
               break;
            case 8:
               byteOrder = ByteOrder.LITTLE_ENDIAN;
               break;
            default:
               assert false;
            }
         } finally {
            unsafe.freeMemory(var0);
         }

         return byteOrder;
      }
   }

   static int pageSize() {
      if (pageSize == -1) {
         pageSize = unsafe.pageSize();
      }

      return pageSize;
   }
}
