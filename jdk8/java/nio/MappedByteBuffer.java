package java.nio;

import java.io.FileDescriptor;
import sun.misc.Unsafe;

public abstract class MappedByteBuffer extends ByteBuffer {
   private final FileDescriptor fd;
   private static byte unused;

   MappedByteBuffer(int var1, int var2, int var3, int var4, FileDescriptor var5) {
      super(var1, var2, var3, var4);
      this.fd = var5;
   }

   MappedByteBuffer(int var1, int var2, int var3, int var4) {
      super(var1, var2, var3, var4);
      this.fd = null;
   }

   private void checkMapped() {
      if (this.fd == null) {
         throw new UnsupportedOperationException();
      }
   }

   private long mappingOffset() {
      int var1 = Bits.pageSize();
      long var2 = this.address % (long)var1;
      return var2 >= 0L ? var2 : (long)var1 + var2;
   }

   private long mappingAddress(long var1) {
      return this.address - var1;
   }

   private long mappingLength(long var1) {
      return (long)this.capacity() + var1;
   }

   public final boolean isLoaded() {
      this.checkMapped();
      if (this.address != 0L && this.capacity() != 0) {
         long var1 = this.mappingOffset();
         long var3 = this.mappingLength(var1);
         return this.isLoaded0(this.mappingAddress(var1), var3, Bits.pageCount(var3));
      } else {
         return true;
      }
   }

   public final MappedByteBuffer load() {
      this.checkMapped();
      if (this.address != 0L && this.capacity() != 0) {
         long var1 = this.mappingOffset();
         long var3 = this.mappingLength(var1);
         this.load0(this.mappingAddress(var1), var3);
         Unsafe var5 = Unsafe.getUnsafe();
         int var6 = Bits.pageSize();
         int var7 = Bits.pageCount(var3);
         long var8 = this.mappingAddress(var1);
         byte var10 = 0;

         for(int var11 = 0; var11 < var7; ++var11) {
            var10 ^= var5.getByte(var8);
            var8 += (long)var6;
         }

         if (unused != 0) {
            unused = var10;
         }

         return this;
      } else {
         return this;
      }
   }

   public final MappedByteBuffer force() {
      this.checkMapped();
      if (this.address != 0L && this.capacity() != 0) {
         long var1 = this.mappingOffset();
         this.force0(this.fd, this.mappingAddress(var1), this.mappingLength(var1));
      }

      return this;
   }

   private native boolean isLoaded0(long var1, long var3, int var5);

   private native void load0(long var1, long var3);

   private native void force0(FileDescriptor var1, long var2, long var4);
}
