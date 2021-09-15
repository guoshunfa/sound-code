package sun.nio.cs;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public class SingleByte {
   private static final CoderResult withResult(CoderResult var0, Buffer var1, int var2, Buffer var3, int var4) {
      var1.position(var2 - var1.arrayOffset());
      var3.position(var4 - var3.arrayOffset());
      return var0;
   }

   public static void initC2B(char[] var0, char[] var1, char[] var2, char[] var3) {
      int var4;
      for(var4 = 0; var4 < var3.length; ++var4) {
         var3[var4] = '�';
      }

      for(var4 = 0; var4 < var2.length; ++var4) {
         var2[var4] = '�';
      }

      var4 = 0;

      int var5;
      char var6;
      for(var5 = 0; var5 < var0.length; ++var5) {
         var6 = var0[var5];
         if (var6 != '�') {
            int var7 = var6 >> 8;
            if (var3[var7] == '�') {
               var3[var7] = (char)var4;
               var4 += 256;
            }

            var7 = var3[var7] + (var6 & 255);
            var2[var7] = (char)(var5 >= 128 ? var5 - 128 : var5 + 128);
         }
      }

      int var8;
      if (var1 != null) {
         for(var5 = 0; var5 < var1.length; var2[var8] = var6) {
            var6 = var1[var5++];
            char var9 = var1[var5++];
            var8 = var9 >> 8;
            if (var3[var8] == '�') {
               var3[var8] = (char)var4;
               var4 += 256;
            }

            var8 = var3[var8] + (var9 & 255);
         }
      }

   }

   public static final class Encoder extends CharsetEncoder implements ArrayEncoder {
      private Surrogate.Parser sgp;
      private final char[] c2b;
      private final char[] c2bIndex;
      private byte repl = 63;

      public Encoder(Charset var1, char[] var2, char[] var3) {
         super(var1, 1.0F, 1.0F);
         this.c2b = var2;
         this.c2bIndex = var3;
      }

      public boolean canEncode(char var1) {
         return this.encode(var1) != 65533;
      }

      public boolean isLegalReplacement(byte[] var1) {
         return var1.length == 1 && var1[0] == 63 || super.isLegalReplacement(var1);
      }

      private CoderResult encodeArrayLoop(CharBuffer var1, ByteBuffer var2) {
         char[] var3 = var1.array();
         int var4 = var1.arrayOffset() + var1.position();
         int var5 = var1.arrayOffset() + var1.limit();
         byte[] var6 = var2.array();
         int var7 = var2.arrayOffset() + var2.position();
         int var8 = var2.arrayOffset() + var2.limit();
         CoderResult var9 = CoderResult.UNDERFLOW;
         if (var8 - var7 < var5 - var4) {
            var5 = var4 + (var8 - var7);
            var9 = CoderResult.OVERFLOW;
         }

         while(var4 < var5) {
            char var10 = var3[var4];
            int var11 = this.encode(var10);
            if (var11 == 65533) {
               if (Character.isSurrogate(var10)) {
                  if (this.sgp == null) {
                     this.sgp = new Surrogate.Parser();
                  }

                  if (this.sgp.parse(var10, var3, var4, var5) < 0) {
                     return SingleByte.withResult(this.sgp.error(), var1, var4, var2, var7);
                  }

                  return SingleByte.withResult(this.sgp.unmappableResult(), var1, var4, var2, var7);
               }

               return SingleByte.withResult(CoderResult.unmappableForLength(1), var1, var4, var2, var7);
            }

            var6[var7++] = (byte)var11;
            ++var4;
         }

         return SingleByte.withResult(var9, var1, var4, var2, var7);
      }

      private CoderResult encodeBufferLoop(CharBuffer var1, ByteBuffer var2) {
         int var3 = var1.position();

         try {
            while(true) {
               if (var1.hasRemaining()) {
                  char var10 = var1.get();
                  int var5 = this.encode(var10);
                  CoderResult var6;
                  if (var5 != 65533) {
                     if (var2.hasRemaining()) {
                        var2.put((byte)var5);
                        ++var3;
                        continue;
                     }

                     var6 = CoderResult.OVERFLOW;
                     return var6;
                  }

                  if (!Character.isSurrogate(var10)) {
                     var6 = CoderResult.unmappableForLength(1);
                     return var6;
                  }

                  if (this.sgp == null) {
                     this.sgp = new Surrogate.Parser();
                  }

                  if (this.sgp.parse(var10, var1) >= 0) {
                     var6 = this.sgp.unmappableResult();
                     return var6;
                  }

                  var6 = this.sgp.error();
                  return var6;
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

      public final int encode(char var1) {
         char var2 = this.c2bIndex[var1 >> 8];
         return var2 == '�' ? '�' : this.c2b[var2 + (var1 & 255)];
      }

      protected void implReplaceWith(byte[] var1) {
         this.repl = var1[0];
      }

      public int encode(char[] var1, int var2, int var3, byte[] var4) {
         int var5 = 0;
         int var6 = var2 + Math.min(var3, var4.length);

         while(var2 < var6) {
            char var7 = var1[var2++];
            int var8 = this.encode(var7);
            if (var8 != 65533) {
               var4[var5++] = (byte)var8;
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
   }

   public static final class Decoder extends CharsetDecoder implements ArrayDecoder {
      private final char[] b2c;
      private char repl = '�';

      public Decoder(Charset var1, char[] var2) {
         super(var1, 1.0F, 1.0F);
         this.b2c = var2;
      }

      private CoderResult decodeArrayLoop(ByteBuffer var1, CharBuffer var2) {
         byte[] var3 = var1.array();
         int var4 = var1.arrayOffset() + var1.position();
         int var5 = var1.arrayOffset() + var1.limit();
         char[] var6 = var2.array();
         int var7 = var2.arrayOffset() + var2.position();
         int var8 = var2.arrayOffset() + var2.limit();
         CoderResult var9 = CoderResult.UNDERFLOW;
         if (var8 - var7 < var5 - var4) {
            var5 = var4 + (var8 - var7);
            var9 = CoderResult.OVERFLOW;
         }

         while(var4 < var5) {
            char var10 = this.decode(var3[var4]);
            if (var10 == '�') {
               return SingleByte.withResult(CoderResult.unmappableForLength(1), var1, var4, var2, var7);
            }

            var6[var7++] = var10;
            ++var4;
         }

         return SingleByte.withResult(var9, var1, var4, var2, var7);
      }

      private CoderResult decodeBufferLoop(ByteBuffer var1, CharBuffer var2) {
         int var3 = var1.position();

         try {
            while(var1.hasRemaining()) {
               char var4 = this.decode(var1.get());
               CoderResult var5;
               if (var4 == '�') {
                  var5 = CoderResult.unmappableForLength(1);
                  return var5;
               }

               if (!var2.hasRemaining()) {
                  var5 = CoderResult.OVERFLOW;
                  return var5;
               }

               var2.put(var4);
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

      public final char decode(int var1) {
         return this.b2c[var1 + 128];
      }

      protected void implReplaceWith(String var1) {
         this.repl = var1.charAt(0);
      }

      public int decode(byte[] var1, int var2, int var3, char[] var4) {
         if (var3 > var4.length) {
            var3 = var4.length;
         }

         int var5;
         for(var5 = 0; var5 < var3; ++var5) {
            var4[var5] = this.decode(var1[var2++]);
            if (var4[var5] == '�') {
               var4[var5] = this.repl;
            }
         }

         return var5;
      }
   }
}
