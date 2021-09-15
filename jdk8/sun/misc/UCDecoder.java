package sun.misc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;

public class UCDecoder extends CharacterDecoder {
   private static final byte[] map_array = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 40, 41};
   private int sequence;
   private byte[] tmp = new byte[2];
   private CRC16 crc = new CRC16();
   private ByteArrayOutputStream lineAndSeq = new ByteArrayOutputStream(2);

   protected int bytesPerAtom() {
      return 2;
   }

   protected int bytesPerLine() {
      return 48;
   }

   protected void decodeAtom(PushbackInputStream var1, OutputStream var2, int var3) throws IOException {
      byte var9 = -1;
      byte var10 = -1;
      byte var11 = -1;
      byte[] var14 = new byte[3];
      int var4 = var1.read(var14);
      if (var4 != 3) {
         throw new CEStreamExhausted();
      } else {
         for(var4 = 0; var4 < 64 && (var9 == -1 || var10 == -1 || var11 == -1); ++var4) {
            if (var14[0] == map_array[var4]) {
               var9 = (byte)var4;
            }

            if (var14[1] == map_array[var4]) {
               var10 = (byte)var4;
            }

            if (var14[2] == map_array[var4]) {
               var11 = (byte)var4;
            }
         }

         byte var12 = (byte)(((var9 & 56) << 2) + (var10 & 31));
         byte var13 = (byte)(((var9 & 7) << 5) + (var11 & 31));
         int var5 = 0;
         int var6 = 0;

         for(var4 = 1; var4 < 256; var4 *= 2) {
            if ((var12 & var4) != 0) {
               ++var5;
            }

            if ((var13 & var4) != 0) {
               ++var6;
            }
         }

         int var7 = (var10 & 32) / 32;
         int var8 = (var11 & 32) / 32;
         if ((var5 & 1) != var7) {
            throw new CEFormatException("UCDecoder: High byte parity error.");
         } else if ((var6 & 1) != var8) {
            throw new CEFormatException("UCDecoder: Low byte parity error.");
         } else {
            var2.write(var12);
            this.crc.update(var12);
            if (var3 == 2) {
               var2.write(var13);
               this.crc.update(var13);
            }

         }
      }
   }

   protected void decodeBufferPrefix(PushbackInputStream var1, OutputStream var2) {
      this.sequence = 0;
   }

   protected int decodeLinePrefix(PushbackInputStream var1, OutputStream var2) throws IOException {
      this.crc.value = 0;

      do {
         int var7 = var1.read(this.tmp, 0, 1);
         if (var7 == -1) {
            throw new CEStreamExhausted();
         }
      } while(this.tmp[0] != 42);

      this.lineAndSeq.reset();
      this.decodeAtom(var1, this.lineAndSeq, 2);
      byte[] var6 = this.lineAndSeq.toByteArray();
      int var4 = var6[0] & 255;
      int var5 = var6[1] & 255;
      if (var5 != this.sequence) {
         throw new CEFormatException("UCDecoder: Out of sequence line.");
      } else {
         this.sequence = this.sequence + 1 & 255;
         return var4;
      }
   }

   protected void decodeLineSuffix(PushbackInputStream var1, OutputStream var2) throws IOException {
      int var4 = this.crc.value;
      this.lineAndSeq.reset();
      this.decodeAtom(var1, this.lineAndSeq, 2);
      byte[] var6 = this.lineAndSeq.toByteArray();
      int var5 = (var6[0] << 8 & '\uff00') + (var6[1] & 255);
      if (var5 != var4) {
         throw new CEFormatException("UCDecoder: CRC check failed.");
      }
   }
}
