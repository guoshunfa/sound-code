package sun.awt;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public class Symbol extends Charset {
   public Symbol() {
      super("Symbol", (String[])null);
   }

   public CharsetEncoder newEncoder() {
      return new Symbol.Encoder(this);
   }

   public CharsetDecoder newDecoder() {
      throw new Error("Decoder is not implemented for Symbol Charset");
   }

   public boolean contains(Charset var1) {
      return var1 instanceof Symbol;
   }

   private static class Encoder extends CharsetEncoder {
      private static byte[] table_math = new byte[]{34, 0, 100, 36, 0, -58, 68, -47, -50, -49, 0, 0, 0, 39, 0, 80, 0, -27, 45, 0, 0, -92, 0, 42, -80, -73, -42, 0, 0, -75, -91, 0, 0, 0, 0, -67, 0, 0, 0, -39, -38, -57, -56, -14, 0, 0, 0, 0, 0, 0, 0, 0, 92, 0, 0, 0, 0, 0, 0, 0, 126, 0, 0, 0, 0, 0, 0, 0, 0, 64, 0, 0, -69, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -71, -70, 0, 0, -93, -77, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -52, -55, -53, 0, -51, -54, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -59, 0, -60, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 94, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -32, -41, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -68};
      private static byte[] table_greek = new byte[]{65, 66, 71, 68, 69, 90, 72, 81, 73, 75, 76, 77, 78, 88, 79, 80, 82, 0, 83, 84, 85, 70, 67, 89, 87, 0, 0, 0, 0, 0, 0, 0, 97, 98, 103, 100, 101, 122, 104, 113, 105, 107, 108, 109, 110, 120, 111, 112, 114, 86, 115, 116, 117, 102, 99, 121, 119, 0, 0, 0, 0, 0, 0, 0, 74, -95, 0, 0, 106, 118};

      public Encoder(Charset var1) {
         super(var1, 1.0F, 1.0F);
      }

      public boolean canEncode(char var1) {
         if (var1 >= 8704 && var1 <= 8943) {
            if (table_math[var1 - 8704] != 0) {
               return true;
            }
         } else if (var1 >= 913 && var1 <= 982 && table_greek[var1 - 913] != 0) {
            return true;
         }

         return false;
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
               if (var9 >= 8704 && var9 <= 8943) {
                  var6[var7++] = table_math[var9 - 8704];
               } else if (var9 >= 913 && var9 <= 982) {
                  var6[var7++] = table_greek[var9 - 913];
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
