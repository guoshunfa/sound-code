package sun.nio.cs;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

class ISO_8859_1 extends Charset implements HistoricallyNamedCharset {
   public ISO_8859_1() {
      super("ISO-8859-1", StandardCharsets.aliases_ISO_8859_1);
   }

   public String historicalName() {
      return "ISO8859_1";
   }

   public boolean contains(Charset var1) {
      return var1 instanceof US_ASCII || var1 instanceof ISO_8859_1;
   }

   public CharsetDecoder newDecoder() {
      return new ISO_8859_1.Decoder(this);
   }

   public CharsetEncoder newEncoder() {
      return new ISO_8859_1.Encoder(this);
   }

   private static class Encoder extends CharsetEncoder implements ArrayEncoder {
      private final Surrogate.Parser sgp;
      private byte repl;

      private Encoder(Charset var1) {
         super(var1, 1.0F, 1.0F);
         this.sgp = new Surrogate.Parser();
         this.repl = 63;
      }

      public boolean canEncode(char var1) {
         return var1 <= 255;
      }

      public boolean isLegalReplacement(byte[] var1) {
         return true;
      }

      private static int encodeISOArray(char[] var0, int var1, byte[] var2, int var3, int var4) {
         int var5;
         for(var5 = 0; var5 < var4; ++var5) {
            char var6 = var0[var1++];
            if (var6 > 255) {
               break;
            }

            var2[var3++] = (byte)var6;
         }

         return var5;
      }

      private CoderResult encodeArrayLoop(CharBuffer var1, ByteBuffer var2) {
         char[] var3 = var1.array();
         int var4 = var1.arrayOffset();
         int var5 = var4 + var1.position();
         int var6 = var4 + var1.limit();

         assert var5 <= var6;

         var5 = var5 <= var6 ? var5 : var6;
         byte[] var7 = var2.array();
         int var8 = var2.arrayOffset();
         int var9 = var8 + var2.position();
         int var10 = var8 + var2.limit();

         assert var9 <= var10;

         var9 = var9 <= var10 ? var9 : var10;
         int var11 = var10 - var9;
         int var12 = var6 - var5;
         int var13 = var11 < var12 ? var11 : var12;

         CoderResult var15;
         try {
            int var14 = var13 <= 0 ? 0 : encodeISOArray(var3, var5, var7, var9, var13);
            var5 += var14;
            var9 += var14;
            if (var14 == var13) {
               if (var13 < var12) {
                  var15 = CoderResult.OVERFLOW;
                  return var15;
               }

               var15 = CoderResult.UNDERFLOW;
               return var15;
            }

            if (this.sgp.parse(var3[var5], var3, var5, var6) < 0) {
               var15 = this.sgp.error();
               return var15;
            }

            var15 = this.sgp.unmappableResult();
         } finally {
            var1.position(var5 - var4);
            var2.position(var9 - var8);
         }

         return var15;
      }

      private CoderResult encodeBufferLoop(CharBuffer var1, ByteBuffer var2) {
         int var3 = var1.position();

         try {
            while(true) {
               if (var1.hasRemaining()) {
                  char var9 = var1.get();
                  CoderResult var5;
                  if (var9 <= 255) {
                     if (!var2.hasRemaining()) {
                        var5 = CoderResult.OVERFLOW;
                        return var5;
                     }

                     var2.put((byte)var9);
                     ++var3;
                     continue;
                  }

                  if (this.sgp.parse(var9, var1) < 0) {
                     var5 = this.sgp.error();
                     return var5;
                  }

                  var5 = this.sgp.unmappableResult();
                  return var5;
               }

               CoderResult var4 = CoderResult.UNDERFLOW;
               return var4;
            }
         } finally {
            var1.position(var3);
         }
      }

      protected CoderResult encodeLoop(CharBuffer var1, ByteBuffer var2) {
         return var1.hasArray() && var2.hasArray() ? this.encodeArrayLoop(var1, var2) : this.encodeBufferLoop(var1, var2);
      }

      protected void implReplaceWith(byte[] var1) {
         this.repl = var1[0];
      }

      public int encode(char[] var1, int var2, int var3, byte[] var4) {
         int var5 = 0;
         int var6 = Math.min(var3, var4.length);
         int var7 = var2 + var6;

         while(var2 < var7) {
            int var8 = var6 <= 0 ? 0 : encodeISOArray(var1, var2, var4, var5, var6);
            var2 += var8;
            var5 += var8;
            if (var8 != var6) {
               char var9 = var1[var2++];
               if (Character.isHighSurrogate(var9) && var2 < var7 && Character.isLowSurrogate(var1[var2])) {
                  if (var3 > var4.length) {
                     ++var7;
                     --var3;
                  }

                  ++var2;
               }

               var4[var5++] = this.repl;
               var6 = Math.min(var7 - var2, var4.length - var5);
            }
         }

         return var5;
      }

      // $FF: synthetic method
      Encoder(Charset var1, Object var2) {
         this(var1);
      }
   }

   private static class Decoder extends CharsetDecoder implements ArrayDecoder {
      private Decoder(Charset var1) {
         super(var1, 1.0F, 1.0F);
      }

      private CoderResult decodeArrayLoop(ByteBuffer var1, CharBuffer var2) {
         byte[] var3 = var1.array();
         int var4 = var1.arrayOffset() + var1.position();
         int var5 = var1.arrayOffset() + var1.limit();

         assert var4 <= var5;

         var4 = var4 <= var5 ? var4 : var5;
         char[] var6 = var2.array();
         int var7 = var2.arrayOffset() + var2.position();
         int var8 = var2.arrayOffset() + var2.limit();

         assert var7 <= var8;

         var7 = var7 <= var8 ? var7 : var8;

         try {
            while(var4 < var5) {
               byte var9 = var3[var4];
               if (var7 >= var8) {
                  CoderResult var10 = CoderResult.OVERFLOW;
                  return var10;
               }

               var6[var7++] = (char)(var9 & 255);
               ++var4;
            }

            CoderResult var14 = CoderResult.UNDERFLOW;
            return var14;
         } finally {
            var1.position(var4 - var1.arrayOffset());
            var2.position(var7 - var2.arrayOffset());
         }
      }

      private CoderResult decodeBufferLoop(ByteBuffer var1, CharBuffer var2) {
         int var3 = var1.position();

         CoderResult var9;
         try {
            while(var1.hasRemaining()) {
               byte var4 = var1.get();
               if (!var2.hasRemaining()) {
                  CoderResult var5 = CoderResult.OVERFLOW;
                  return var5;
               }

               var2.put((char)(var4 & 255));
               ++var3;
            }

            var9 = CoderResult.UNDERFLOW;
         } finally {
            var1.position(var3);
         }

         return var9;
      }

      protected CoderResult decodeLoop(ByteBuffer var1, CharBuffer var2) {
         return var1.hasArray() && var2.hasArray() ? this.decodeArrayLoop(var1, var2) : this.decodeBufferLoop(var1, var2);
      }

      public int decode(byte[] var1, int var2, int var3, char[] var4) {
         if (var3 > var4.length) {
            var3 = var4.length;
         }

         int var5;
         for(var5 = 0; var5 < var3; var4[var5++] = (char)(var1[var2++] & 255)) {
         }

         return var5;
      }

      // $FF: synthetic method
      Decoder(Charset var1, Object var2) {
         this(var1);
      }
   }
}
