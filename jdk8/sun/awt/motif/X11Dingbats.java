package sun.awt.motif;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public class X11Dingbats extends Charset {
   public X11Dingbats() {
      super("X11Dingbats", (String[])null);
   }

   public CharsetEncoder newEncoder() {
      return new X11Dingbats.Encoder(this);
   }

   public CharsetDecoder newDecoder() {
      throw new Error("Decoder is not supported by X11Dingbats Charset");
   }

   public boolean contains(Charset var1) {
      return var1 instanceof X11Dingbats;
   }

   private static class Encoder extends CharsetEncoder {
      private static byte[] table = new byte[]{-95, -94, -93, -92, -91, -90, -89, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -74, -73, -72, -71, -70, -69, -68, -67, -66, -65, -64, -63, -62, -61, -60, -59, -58, -57, -56, -55, -54, -53, -52, -51, -50, -49, -48, -47, -46, -45, -44, 0, 0, 0, -40, -39, -38, -37, -36, -35, -34, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

      public Encoder(Charset var1) {
         super(var1, 1.0F, 1.0F);
      }

      public boolean canEncode(char var1) {
         if (var1 >= 9985 && var1 <= 10078) {
            return true;
         } else if (var1 >= 10081 && var1 <= 10174) {
            return table[var1 - 10081] != 0;
         } else {
            return false;
         }
      }

      protected CoderResult encodeLoop(CharBuffer var1, ByteBuffer var2) {
         char[] var3 = var1.array();
         int var4 = var1.arrayOffset() + var1.position();
         int var5 = var1.arrayOffset() + var1.limit();

         assert var4 <= var5;

         var4 = var4 <= var5 ? var4 : var5;
         byte[] var6 = var2.array();
         int var7 = var2.arrayOffset() + var2.position();
         int var8 = var2.arrayOffset() + var2.limit();

         assert var7 <= var8;

         var7 = var7 <= var8 ? var7 : var8;

         try {
            while(var4 < var5) {
               char var9 = var3[var4];
               CoderResult var10;
               if (var8 - var7 < 1) {
                  var10 = CoderResult.OVERFLOW;
                  return var10;
               }

               if (!this.canEncode(var9)) {
                  var10 = CoderResult.unmappableForLength(1);
                  return var10;
               }

               ++var4;
               if (var9 >= 10081) {
                  var6[var7++] = table[var9 - 10081];
               } else {
                  var6[var7++] = (byte)(var9 + 32 - 9984);
               }
            }

            CoderResult var14 = CoderResult.UNDERFLOW;
            return var14;
         } finally {
            var1.position(var4 - var1.arrayOffset());
            var2.position(var7 - var2.arrayOffset());
         }
      }

      public boolean isLegalReplacement(byte[] var1) {
         return true;
      }
   }
}
