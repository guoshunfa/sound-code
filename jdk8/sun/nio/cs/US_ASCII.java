package sun.nio.cs;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public class US_ASCII extends Charset implements HistoricallyNamedCharset {
   public US_ASCII() {
      super("US-ASCII", StandardCharsets.aliases_US_ASCII);
   }

   public String historicalName() {
      return "ASCII";
   }

   public boolean contains(Charset var1) {
      return var1 instanceof US_ASCII;
   }

   public CharsetDecoder newDecoder() {
      return new US_ASCII.Decoder(this);
   }

   public CharsetEncoder newEncoder() {
      return new US_ASCII.Encoder(this);
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
         return var1 < 128;
      }

      public boolean isLegalReplacement(byte[] var1) {
         return var1.length == 1 && var1[0] >= 0 || super.isLegalReplacement(var1);
      }

      private CoderResult encodeArrayLoop(CharBuffer var1, ByteBuffer var2) {
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
               if (var9 >= 128) {
                  if (this.sgp.parse(var9, var3, var4, var5) < 0) {
                     var10 = this.sgp.error();
                     return var10;
                  }

                  var10 = this.sgp.unmappableResult();
                  return var10;
               }

               if (var7 >= var8) {
                  var10 = CoderResult.OVERFLOW;
                  return var10;
               }

               var6[var7] = (byte)var9;
               ++var4;
               ++var7;
            }

            CoderResult var14 = CoderResult.UNDERFLOW;
            return var14;
         } finally {
            var1.position(var4 - var1.arrayOffset());
            var2.position(var7 - var2.arrayOffset());
         }
      }

      private CoderResult encodeBufferLoop(CharBuffer var1, ByteBuffer var2) {
         int var3 = var1.position();

         try {
            while(var1.hasRemaining()) {
               char var4 = var1.get();
               CoderResult var5;
               if (var4 >= 128) {
                  if (this.sgp.parse(var4, var1) < 0) {
                     var5 = this.sgp.error();
                     return var5;
                  }

                  var5 = this.sgp.unmappableResult();
                  return var5;
               }

               if (!var2.hasRemaining()) {
                  var5 = CoderResult.OVERFLOW;
                  return var5;
               }

               var2.put((byte)var4);
               ++var3;
            }

            CoderResult var9 = CoderResult.UNDERFLOW;
            return var9;
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
         int var6 = var2 + Math.min(var3, var4.length);

         while(var2 < var6) {
            char var7 = var1[var2++];
            if (var7 < 128) {
               var4[var5++] = (byte)var7;
            } else {
               if (Character.isHighSurrogate(var7) && var2 < var6 && Character.isLowSurrogate(var1[var2])) {
                  if (var3 > var4.length) {
                     ++var6;
                     --var3;
                  }

                  ++var2;
               }

               var4[var5++] = this.repl;
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
      private char repl;

      private Decoder(Charset var1) {
         super(var1, 1.0F, 1.0F);
         this.repl = '�';
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
               CoderResult var10;
               if (var9 < 0) {
                  var10 = CoderResult.malformedForLength(1);
                  return var10;
               }

               if (var7 >= var8) {
                  var10 = CoderResult.OVERFLOW;
                  return var10;
               }

               var6[var7++] = (char)var9;
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

         try {
            while(var1.hasRemaining()) {
               byte var4 = var1.get();
               CoderResult var5;
               if (var4 < 0) {
                  var5 = CoderResult.malformedForLength(1);
                  return var5;
               }

               if (!var2.hasRemaining()) {
                  var5 = CoderResult.OVERFLOW;
                  return var5;
               }

               var2.put((char)var4);
               ++var3;
            }

            CoderResult var9 = CoderResult.UNDERFLOW;
            return var9;
         } finally {
            var1.position(var3);
         }
      }

      protected CoderResult decodeLoop(ByteBuffer var1, CharBuffer var2) {
         return var1.hasArray() && var2.hasArray() ? this.decodeArrayLoop(var1, var2) : this.decodeBufferLoop(var1, var2);
      }

      protected void implReplaceWith(String var1) {
         this.repl = var1.charAt(0);
      }

      public int decode(byte[] var1, int var2, int var3, char[] var4) {
         int var5 = 0;
         var3 = Math.min(var3, var4.length);

         while(var5 < var3) {
            byte var6 = var1[var2++];
            if (var6 >= 0) {
               var4[var5++] = (char)var6;
            } else {
               var4[var5++] = this.repl;
            }
         }

         return var5;
      }

      // $FF: synthetic method
      Decoder(Charset var1, Object var2) {
         this(var1);
      }
   }
}
