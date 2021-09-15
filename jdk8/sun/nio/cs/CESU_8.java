package sun.nio.cs;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

class CESU_8 extends Unicode {
   public CESU_8() {
      super("CESU-8", StandardCharsets.aliases_CESU_8);
   }

   public String historicalName() {
      return "CESU8";
   }

   public CharsetDecoder newDecoder() {
      return new CESU_8.Decoder(this);
   }

   public CharsetEncoder newEncoder() {
      return new CESU_8.Encoder(this);
   }

   private static final void updatePositions(Buffer var0, int var1, Buffer var2, int var3) {
      var0.position(var1 - var0.arrayOffset());
      var2.position(var3 - var2.arrayOffset());
   }

   private static class Encoder extends CharsetEncoder implements ArrayEncoder {
      private Surrogate.Parser sgp;
      private char[] c2;

      private Encoder(Charset var1) {
         super(var1, 1.1F, 3.0F);
      }

      public boolean canEncode(char var1) {
         return !Character.isSurrogate(var1);
      }

      public boolean isLegalReplacement(byte[] var1) {
         return var1.length == 1 && var1[0] >= 0 || super.isLegalReplacement(var1);
      }

      private static CoderResult overflow(CharBuffer var0, int var1, ByteBuffer var2, int var3) {
         CESU_8.updatePositions(var0, var1, var2, var3);
         return CoderResult.OVERFLOW;
      }

      private static CoderResult overflow(CharBuffer var0, int var1) {
         var0.position(var1);
         return CoderResult.OVERFLOW;
      }

      private static void to3Bytes(byte[] var0, int var1, char var2) {
         var0[var1] = (byte)(224 | var2 >> 12);
         var0[var1 + 1] = (byte)(128 | var2 >> 6 & 63);
         var0[var1 + 2] = (byte)(128 | var2 & 63);
      }

      private static void to3Bytes(ByteBuffer var0, char var1) {
         var0.put((byte)(224 | var1 >> 12));
         var0.put((byte)(128 | var1 >> 6 & 63));
         var0.put((byte)(128 | var1 & 63));
      }

      private CoderResult encodeArrayLoop(CharBuffer var1, ByteBuffer var2) {
         char[] var3 = var1.array();
         int var4 = var1.arrayOffset() + var1.position();
         int var5 = var1.arrayOffset() + var1.limit();
         byte[] var6 = var2.array();
         int var7 = var2.arrayOffset() + var2.position();
         int var8 = var2.arrayOffset() + var2.limit();

         for(int var9 = var7 + Math.min(var5 - var4, var8 - var7); var7 < var9 && var3[var4] < 128; var6[var7++] = (byte)var3[var4++]) {
         }

         for(; var4 < var5; ++var4) {
            char var10 = var3[var4];
            if (var10 < 128) {
               if (var7 >= var8) {
                  return overflow(var1, var4, var2, var7);
               }

               var6[var7++] = (byte)var10;
            } else if (var10 < 2048) {
               if (var8 - var7 < 2) {
                  return overflow(var1, var4, var2, var7);
               }

               var6[var7++] = (byte)(192 | var10 >> 6);
               var6[var7++] = (byte)(128 | var10 & 63);
            } else if (Character.isSurrogate(var10)) {
               if (this.sgp == null) {
                  this.sgp = new Surrogate.Parser();
               }

               int var11 = this.sgp.parse(var10, var3, var4, var5);
               if (var11 < 0) {
                  CESU_8.updatePositions(var1, var4, var2, var7);
                  return this.sgp.error();
               }

               if (var8 - var7 < 6) {
                  return overflow(var1, var4, var2, var7);
               }

               to3Bytes(var6, var7, Character.highSurrogate(var11));
               var7 += 3;
               to3Bytes(var6, var7, Character.lowSurrogate(var11));
               var7 += 3;
               ++var4;
            } else {
               if (var8 - var7 < 3) {
                  return overflow(var1, var4, var2, var7);
               }

               to3Bytes(var6, var7, var10);
               var7 += 3;
            }
         }

         CESU_8.updatePositions(var1, var4, var2, var7);
         return CoderResult.UNDERFLOW;
      }

      private CoderResult encodeBufferLoop(CharBuffer var1, ByteBuffer var2) {
         int var3;
         for(var3 = var1.position(); var1.hasRemaining(); ++var3) {
            char var4 = var1.get();
            if (var4 < 128) {
               if (!var2.hasRemaining()) {
                  return overflow(var1, var3);
               }

               var2.put((byte)var4);
            } else if (var4 < 2048) {
               if (var2.remaining() < 2) {
                  return overflow(var1, var3);
               }

               var2.put((byte)(192 | var4 >> 6));
               var2.put((byte)(128 | var4 & 63));
            } else if (Character.isSurrogate(var4)) {
               if (this.sgp == null) {
                  this.sgp = new Surrogate.Parser();
               }

               int var5 = this.sgp.parse(var4, var1);
               if (var5 < 0) {
                  var1.position(var3);
                  return this.sgp.error();
               }

               if (var2.remaining() < 6) {
                  return overflow(var1, var3);
               }

               to3Bytes(var2, Character.highSurrogate(var5));
               to3Bytes(var2, Character.lowSurrogate(var5));
               ++var3;
            } else {
               if (var2.remaining() < 3) {
                  return overflow(var1, var3);
               }

               to3Bytes(var2, var4);
            }
         }

         var1.position(var3);
         return CoderResult.UNDERFLOW;
      }

      protected final CoderResult encodeLoop(CharBuffer var1, ByteBuffer var2) {
         return var1.hasArray() && var2.hasArray() ? this.encodeArrayLoop(var1, var2) : this.encodeBufferLoop(var1, var2);
      }

      public int encode(char[] var1, int var2, int var3, byte[] var4) {
         int var5 = var2 + var3;
         int var6 = 0;

         for(int var7 = var6 + Math.min(var3, var4.length); var6 < var7 && var1[var2] < 128; var4[var6++] = (byte)var1[var2++]) {
         }

         while(var2 < var5) {
            char var8 = var1[var2++];
            if (var8 < 128) {
               var4[var6++] = (byte)var8;
            } else if (var8 < 2048) {
               var4[var6++] = (byte)(192 | var8 >> 6);
               var4[var6++] = (byte)(128 | var8 & 63);
            } else if (Character.isSurrogate(var8)) {
               if (this.sgp == null) {
                  this.sgp = new Surrogate.Parser();
               }

               int var9 = this.sgp.parse(var8, var1, var2 - 1, var5);
               if (var9 < 0) {
                  if (this.malformedInputAction() != CodingErrorAction.REPLACE) {
                     return -1;
                  }

                  var4[var6++] = this.replacement()[0];
               } else {
                  to3Bytes(var4, var6, Character.highSurrogate(var9));
                  var6 += 3;
                  to3Bytes(var4, var6, Character.lowSurrogate(var9));
                  var6 += 3;
                  ++var2;
               }
            } else {
               to3Bytes(var4, var6, var8);
               var6 += 3;
            }
         }

         return var6;
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

      private static boolean isNotContinuation(int var0) {
         return (var0 & 192) != 128;
      }

      private static boolean isMalformed3(int var0, int var1, int var2) {
         return var0 == -32 && (var1 & 224) == 128 || (var1 & 192) != 128 || (var2 & 192) != 128;
      }

      private static boolean isMalformed3_2(int var0, int var1) {
         return var0 == -32 && (var1 & 224) == 128 || (var1 & 192) != 128;
      }

      private static boolean isMalformed4(int var0, int var1, int var2) {
         return (var0 & 192) != 128 || (var1 & 192) != 128 || (var2 & 192) != 128;
      }

      private static boolean isMalformed4_2(int var0, int var1) {
         return var0 == 240 && var1 == 144 || (var1 & 192) != 128;
      }

      private static boolean isMalformed4_3(int var0) {
         return (var0 & 192) != 128;
      }

      private static CoderResult malformedN(ByteBuffer var0, int var1) {
         switch(var1) {
         case 1:
         case 2:
            return CoderResult.malformedForLength(1);
         case 3:
            byte var4 = var0.get();
            byte var5 = var0.get();
            return CoderResult.malformedForLength((var4 != -32 || (var5 & 224) != 128) && !isNotContinuation(var5) ? 2 : 1);
         case 4:
            int var2 = var0.get() & 255;
            int var3 = var0.get() & 255;
            if (var2 <= 244 && (var2 != 240 || var3 >= 144 && var3 <= 191) && (var2 != 244 || (var3 & 240) == 128) && !isNotContinuation(var3)) {
               if (isNotContinuation(var0.get())) {
                  return CoderResult.malformedForLength(2);
               }

               return CoderResult.malformedForLength(3);
            }

            return CoderResult.malformedForLength(1);
         default:
            assert false;

            return null;
         }
      }

      private static CoderResult malformed(ByteBuffer var0, int var1, CharBuffer var2, int var3, int var4) {
         var0.position(var1 - var0.arrayOffset());
         CoderResult var5 = malformedN(var0, var4);
         CESU_8.updatePositions(var0, var1, var2, var3);
         return var5;
      }

      private static CoderResult malformed(ByteBuffer var0, int var1, int var2) {
         var0.position(var1);
         CoderResult var3 = malformedN(var0, var2);
         var0.position(var1);
         return var3;
      }

      private static CoderResult malformedForLength(ByteBuffer var0, int var1, CharBuffer var2, int var3, int var4) {
         CESU_8.updatePositions(var0, var1, var2, var3);
         return CoderResult.malformedForLength(var4);
      }

      private static CoderResult malformedForLength(ByteBuffer var0, int var1, int var2) {
         var0.position(var1);
         return CoderResult.malformedForLength(var2);
      }

      private static CoderResult xflow(Buffer var0, int var1, int var2, Buffer var3, int var4, int var5) {
         CESU_8.updatePositions(var0, var1, var3, var4);
         return var5 != 0 && var2 - var1 >= var5 ? CoderResult.OVERFLOW : CoderResult.UNDERFLOW;
      }

      private static CoderResult xflow(Buffer var0, int var1, int var2) {
         var0.position(var1);
         return var2 != 0 && var0.remaining() >= var2 ? CoderResult.OVERFLOW : CoderResult.UNDERFLOW;
      }

      private CoderResult decodeArrayLoop(ByteBuffer var1, CharBuffer var2) {
         byte[] var3 = var1.array();
         int var4 = var1.arrayOffset() + var1.position();
         int var5 = var1.arrayOffset() + var1.limit();
         char[] var6 = var2.array();
         int var7 = var2.arrayOffset() + var2.position();
         int var8 = var2.arrayOffset() + var2.limit();

         for(int var9 = var7 + Math.min(var5 - var4, var8 - var7); var7 < var9 && var3[var4] >= 0; var6[var7++] = (char)var3[var4++]) {
         }

         while(true) {
            while(var4 < var5) {
               byte var10 = var3[var4];
               if (var10 < 0) {
                  if (var10 >> 5 == -2 && (var10 & 30) != 0) {
                     if (var5 - var4 < 2 || var7 >= var8) {
                        return xflow(var1, var4, var5, var2, var7, 2);
                     }

                     byte var14 = var3[var4 + 1];
                     if (isNotContinuation(var14)) {
                        return malformedForLength(var1, var4, var2, var7, 1);
                     }

                     var6[var7++] = (char)(var10 << 6 ^ var14 ^ 3968);
                     var4 += 2;
                  } else {
                     if (var10 >> 4 != -2) {
                        return malformed(var1, var4, var2, var7, 1);
                     }

                     int var11 = var5 - var4;
                     if (var11 < 3 || var7 >= var8) {
                        if (var11 > 1 && isMalformed3_2(var10, var3[var4 + 1])) {
                           return malformedForLength(var1, var4, var2, var7, 1);
                        }

                        return xflow(var1, var4, var5, var2, var7, 3);
                     }

                     byte var12 = var3[var4 + 1];
                     byte var13 = var3[var4 + 2];
                     if (isMalformed3(var10, var12, var13)) {
                        return malformed(var1, var4, var2, var7, 3);
                     }

                     var6[var7++] = (char)(var10 << 12 ^ var12 << 6 ^ var13 ^ -123008);
                     var4 += 3;
                  }
               } else {
                  if (var7 >= var8) {
                     return xflow(var1, var4, var5, var2, var7, 1);
                  }

                  var6[var7++] = (char)var10;
                  ++var4;
               }
            }

            return xflow(var1, var4, var5, var2, var7, 0);
         }
      }

      private CoderResult decodeBufferLoop(ByteBuffer var1, CharBuffer var2) {
         int var3 = var1.position();
         int var4 = var1.limit();

         while(true) {
            while(var3 < var4) {
               byte var5 = var1.get();
               if (var5 < 0) {
                  if (var5 >> 5 == -2 && (var5 & 30) != 0) {
                     if (var4 - var3 < 2 || var2.remaining() < 1) {
                        return xflow(var1, var3, 2);
                     }

                     byte var9 = var1.get();
                     if (isNotContinuation(var9)) {
                        return malformedForLength(var1, var3, 1);
                     }

                     var2.put((char)(var5 << 6 ^ var9 ^ 3968));
                     var3 += 2;
                  } else {
                     if (var5 >> 4 != -2) {
                        return malformed(var1, var3, 1);
                     }

                     int var6 = var4 - var3;
                     if (var6 < 3 || var2.remaining() < 1) {
                        if (var6 > 1 && isMalformed3_2(var5, var1.get())) {
                           return malformedForLength(var1, var3, 1);
                        }

                        return xflow(var1, var3, 3);
                     }

                     byte var7 = var1.get();
                     byte var8 = var1.get();
                     if (isMalformed3(var5, var7, var8)) {
                        return malformed(var1, var3, 3);
                     }

                     var2.put((char)(var5 << 12 ^ var7 << 6 ^ var8 ^ -123008));
                     var3 += 3;
                  }
               } else {
                  if (var2.remaining() < 1) {
                     return xflow(var1, var3, 1);
                  }

                  var2.put((char)var5);
                  ++var3;
               }
            }

            return xflow(var1, var3, 0);
         }
      }

      protected CoderResult decodeLoop(ByteBuffer var1, CharBuffer var2) {
         return var1.hasArray() && var2.hasArray() ? this.decodeArrayLoop(var1, var2) : this.decodeBufferLoop(var1, var2);
      }

      private static ByteBuffer getByteBuffer(ByteBuffer var0, byte[] var1, int var2) {
         if (var0 == null) {
            var0 = ByteBuffer.wrap(var1);
         }

         var0.position(var2);
         return var0;
      }

      public int decode(byte[] var1, int var2, int var3, char[] var4) {
         int var5 = var2 + var3;
         int var6 = 0;
         int var7 = Math.min(var3, var4.length);

         ByteBuffer var8;
         for(var8 = null; var6 < var7 && var1[var2] >= 0; var4[var6++] = (char)var1[var2++]) {
         }

         while(true) {
            while(true) {
               while(true) {
                  while(true) {
                     while(var2 < var5) {
                        byte var9 = var1[var2++];
                        if (var9 < 0) {
                           byte var10;
                           if (var9 >> 5 != -2 || (var9 & 30) == 0) {
                              if (var9 >> 4 == -2) {
                                 if (var2 + 1 >= var5) {
                                    if (this.malformedInputAction() != CodingErrorAction.REPLACE) {
                                       return -1;
                                    }

                                    if (var2 >= var5 || !isMalformed3_2(var9, var1[var2])) {
                                       var4[var6++] = this.replacement().charAt(0);
                                       return var6;
                                    }

                                    var4[var6++] = this.replacement().charAt(0);
                                 } else {
                                    var10 = var1[var2++];
                                    byte var11 = var1[var2++];
                                    if (isMalformed3(var9, var10, var11)) {
                                       if (this.malformedInputAction() != CodingErrorAction.REPLACE) {
                                          return -1;
                                       }

                                       var4[var6++] = this.replacement().charAt(0);
                                       var2 -= 3;
                                       var8 = getByteBuffer(var8, var1, var2);
                                       var2 += malformedN(var8, 3).length();
                                    } else {
                                       var4[var6++] = (char)(var9 << 12 ^ var10 << 6 ^ var11 ^ -123008);
                                    }
                                 }
                              } else {
                                 if (this.malformedInputAction() != CodingErrorAction.REPLACE) {
                                    return -1;
                                 }

                                 var4[var6++] = this.replacement().charAt(0);
                              }
                           } else {
                              if (var2 >= var5) {
                                 if (this.malformedInputAction() != CodingErrorAction.REPLACE) {
                                    return -1;
                                 }

                                 var4[var6++] = this.replacement().charAt(0);
                                 return var6;
                              }

                              var10 = var1[var2++];
                              if (isNotContinuation(var10)) {
                                 if (this.malformedInputAction() != CodingErrorAction.REPLACE) {
                                    return -1;
                                 }

                                 var4[var6++] = this.replacement().charAt(0);
                                 --var2;
                              } else {
                                 var4[var6++] = (char)(var9 << 6 ^ var10 ^ 3968);
                              }
                           }
                        } else {
                           var4[var6++] = (char)var9;
                        }
                     }

                     return var6;
                  }
               }
            }
         }
      }

      // $FF: synthetic method
      Decoder(Charset var1, Object var2) {
         this(var1);
      }
   }
}
