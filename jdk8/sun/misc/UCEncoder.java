package sun.misc;

import java.io.IOException;
import java.io.OutputStream;

public class UCEncoder extends CharacterEncoder {
   private static final byte[] map_array = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 40, 41};
   private int sequence;
   private byte[] tmp = new byte[2];
   private CRC16 crc = new CRC16();

   protected int bytesPerAtom() {
      return 2;
   }

   protected int bytesPerLine() {
      return 48;
   }

   protected void encodeAtom(OutputStream var1, byte[] var2, int var3, int var4) throws IOException {
      byte var8 = var2[var3];
      byte var9;
      if (var4 == 2) {
         var9 = var2[var3 + 1];
      } else {
         var9 = 0;
      }

      this.crc.update(var8);
      if (var4 == 2) {
         this.crc.update(var9);
      }

      var1.write(map_array[(var8 >>> 2 & 56) + (var9 >>> 5 & 7)]);
      int var6 = 0;
      int var7 = 0;

      for(int var5 = 1; var5 < 256; var5 *= 2) {
         if ((var8 & var5) != 0) {
            ++var6;
         }

         if ((var9 & var5) != 0) {
            ++var7;
         }
      }

      var6 = (var6 & 1) * 32;
      var7 = (var7 & 1) * 32;
      var1.write(map_array[(var8 & 31) + var6]);
      var1.write(map_array[(var9 & 31) + var7]);
   }

   protected void encodeLinePrefix(OutputStream var1, int var2) throws IOException {
      var1.write(42);
      this.crc.value = 0;
      this.tmp[0] = (byte)var2;
      this.tmp[1] = (byte)this.sequence;
      this.sequence = this.sequence + 1 & 255;
      this.encodeAtom(var1, this.tmp, 0, 2);
   }

   protected void encodeLineSuffix(OutputStream var1) throws IOException {
      this.tmp[0] = (byte)(this.crc.value >>> 8 & 255);
      this.tmp[1] = (byte)(this.crc.value & 255);
      this.encodeAtom(var1, this.tmp, 0, 2);
      super.pStream.println();
   }

   protected void encodeBufferPrefix(OutputStream var1) throws IOException {
      this.sequence = 0;
      super.encodeBufferPrefix(var1);
   }
}
