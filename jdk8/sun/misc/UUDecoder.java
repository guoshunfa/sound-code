package sun.misc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;

public class UUDecoder extends CharacterDecoder {
   public String bufferName;
   public int mode;
   private byte[] decoderBuffer = new byte[4];

   protected int bytesPerAtom() {
      return 3;
   }

   protected int bytesPerLine() {
      return 45;
   }

   protected void decodeAtom(PushbackInputStream var1, OutputStream var2, int var3) throws IOException {
      StringBuffer var12 = new StringBuffer();

      for(int var4 = 0; var4 < 4; ++var4) {
         int var5 = var1.read();
         if (var5 == -1) {
            throw new CEStreamExhausted();
         }

         var12.append((char)var5);
         this.decoderBuffer[var4] = (byte)(var5 - 32 & 63);
      }

      int var9 = this.decoderBuffer[0] << 2 & 252 | this.decoderBuffer[1] >>> 4 & 3;
      int var10 = this.decoderBuffer[1] << 4 & 240 | this.decoderBuffer[2] >>> 2 & 15;
      int var11 = this.decoderBuffer[2] << 6 & 192 | this.decoderBuffer[3] & 63;
      var2.write((byte)(var9 & 255));
      if (var3 > 1) {
         var2.write((byte)(var10 & 255));
      }

      if (var3 > 2) {
         var2.write((byte)(var11 & 255));
      }

   }

   protected void decodeBufferPrefix(PushbackInputStream var1, OutputStream var2) throws IOException {
      StringBuffer var4 = new StringBuffer(32);
      boolean var6 = true;

      while(true) {
         int var3 = var1.read();
         if (var3 == -1) {
            throw new CEFormatException("UUDecoder: No begin line.");
         }

         if (var3 == 98 && var6) {
            var3 = var1.read();
            if (var3 == 101) {
               while(var3 != 10 && var3 != 13) {
                  var3 = var1.read();
                  if (var3 == -1) {
                     throw new CEFormatException("UUDecoder: No begin line.");
                  }

                  if (var3 != 10 && var3 != 13) {
                     var4.append((char)var3);
                  }
               }

               String var5 = var4.toString();
               if (var5.indexOf(32) != 3) {
                  throw new CEFormatException("UUDecoder: Malformed begin line.");
               }

               this.mode = Integer.parseInt(var5.substring(4, 7));
               this.bufferName = var5.substring(var5.indexOf(32, 6) + 1);
               if (var3 == 13) {
                  var3 = var1.read();
                  if (var3 != 10 && var3 != -1) {
                     var1.unread(var3);
                  }
               }

               return;
            }
         }

         var6 = var3 == 10 || var3 == 13;
      }
   }

   protected int decodeLinePrefix(PushbackInputStream var1, OutputStream var2) throws IOException {
      int var3 = var1.read();
      if (var3 == 32) {
         var3 = var1.read();
         var3 = var1.read();
         if (var3 != 10 && var3 != -1) {
            var1.unread(var3);
         }

         throw new CEStreamExhausted();
      } else if (var3 == -1) {
         throw new CEFormatException("UUDecoder: Short Buffer.");
      } else {
         var3 = var3 - 32 & 63;
         if (var3 > this.bytesPerLine()) {
            throw new CEFormatException("UUDecoder: Bad Line Length.");
         } else {
            return var3;
         }
      }
   }

   protected void decodeLineSuffix(PushbackInputStream var1, OutputStream var2) throws IOException {
      while(true) {
         int var3 = var1.read();
         if (var3 == -1) {
            throw new CEStreamExhausted();
         }

         if (var3 != 10) {
            if (var3 != 13) {
               continue;
            }

            var3 = var1.read();
            if (var3 != 10 && var3 != -1) {
               var1.unread(var3);
            }
         }

         return;
      }
   }

   protected void decodeBufferSuffix(PushbackInputStream var1, OutputStream var2) throws IOException {
      var1.read(this.decoderBuffer);
      if (this.decoderBuffer[0] != 101 || this.decoderBuffer[1] != 110 || this.decoderBuffer[2] != 100) {
         throw new CEFormatException("UUDecoder: Missing 'end' line.");
      }
   }
}
