package java.util.zip;

import java.nio.ByteBuffer;
import sun.nio.ch.DirectBuffer;

public class CRC32 implements Checksum {
   private int crc;

   public void update(int var1) {
      this.crc = update(this.crc, var1);
   }

   public void update(byte[] var1, int var2, int var3) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var3 >= 0 && var2 <= var1.length - var3) {
         this.crc = updateBytes(this.crc, var1, var2, var3);
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public void update(byte[] var1) {
      this.crc = updateBytes(this.crc, var1, 0, var1.length);
   }

   public void update(ByteBuffer var1) {
      int var2 = var1.position();
      int var3 = var1.limit();

      assert var2 <= var3;

      int var4 = var3 - var2;
      if (var4 > 0) {
         if (var1 instanceof DirectBuffer) {
            this.crc = updateByteBuffer(this.crc, ((DirectBuffer)var1).address(), var2, var4);
         } else if (var1.hasArray()) {
            this.crc = updateBytes(this.crc, var1.array(), var2 + var1.arrayOffset(), var4);
         } else {
            byte[] var5 = new byte[var4];
            var1.get(var5);
            this.crc = updateBytes(this.crc, var5, 0, var5.length);
         }

         var1.position(var3);
      }
   }

   public void reset() {
      this.crc = 0;
   }

   public long getValue() {
      return (long)this.crc & 4294967295L;
   }

   private static native int update(int var0, int var1);

   private static native int updateBytes(int var0, byte[] var1, int var2, int var3);

   private static native int updateByteBuffer(int var0, long var1, int var3, int var4);
}
