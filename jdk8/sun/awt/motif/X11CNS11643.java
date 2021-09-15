package sun.awt.motif;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public abstract class X11CNS11643 extends Charset {
   private final int plane;

   public X11CNS11643(int var1, String var2) {
      super(var2, (String[])null);
      switch(var1) {
      case 1:
         this.plane = 0;
         break;
      case 2:
      case 3:
         this.plane = var1;
         break;
      default:
         throw new IllegalArgumentException("Only planes 1, 2, and 3 supported");
      }

   }

   public CharsetEncoder newEncoder() {
      return new X11CNS11643.Encoder(this, this.plane);
   }

   public CharsetDecoder newDecoder() {
      return new X11CNS11643.Decoder(this, this.plane);
   }

   public boolean contains(Charset var1) {
      return var1 instanceof X11CNS11643;
   }

   private class Decoder extends sun.nio.cs.ext.EUC_TW.Decoder {
      int plane;
      private String table;

      protected Decoder(Charset var2, int var3) {
         super(var2);
         if (var3 == 0) {
            this.plane = var3;
         } else {
            if (var3 != 2 && var3 != 3) {
               throw new IllegalArgumentException("Only planes 1, 2, and 3 supported");
            }

            this.plane = var3 - 1;
         }

      }

      protected CoderResult decodeLoop(ByteBuffer var1, CharBuffer var2) {
         byte[] var3 = var1.array();
         int var4 = var1.arrayOffset() + var1.position();
         int var5 = var1.arrayOffset() + var1.limit();
         char[] var6 = var2.array();
         int var7 = var2.arrayOffset() + var2.position();
         int var8 = var2.arrayOffset() + var2.limit();

         try {
            CoderResult var16;
            while(var4 < var5) {
               if (var5 - var4 < 2) {
                  var16 = CoderResult.UNDERFLOW;
                  return var16;
               }

               int var9 = var3[var4] & 255 | 128;
               int var10 = var3[var4 + 1] & 255 | 128;
               char[] var11 = this.toUnicode(var9, var10, this.plane);
               CoderResult var12;
               if (var11 == null || var11.length == 2) {
                  var12 = CoderResult.unmappableForLength(2);
                  return var12;
               }

               if (var8 - var7 < 1) {
                  var12 = CoderResult.OVERFLOW;
                  return var12;
               }

               var6[var7++] = var11[0];
               var4 += 2;
            }

            var16 = CoderResult.UNDERFLOW;
            return var16;
         } finally {
            var1.position(var4 - var1.arrayOffset());
            var2.position(var7 - var2.arrayOffset());
         }
      }
   }

   private class Encoder extends sun.nio.cs.ext.EUC_TW.Encoder {
      private int plane;
      private byte[] bb = new byte[4];

      public Encoder(Charset var2, int var3) {
         super(var2);
         this.plane = var3;
      }

      public boolean canEncode(char var1) {
         if (var1 <= 127) {
            return false;
         } else {
            int var2 = this.toEUC(var1, this.bb);
            if (var2 == -1) {
               return false;
            } else {
               int var3 = 0;
               if (var2 == 4) {
                  var3 = (this.bb[1] & 255) - 160;
               }

               return var3 == this.plane;
            }
         }
      }

      public boolean isLegalReplacement(byte[] var1) {
         return true;
      }

      protected CoderResult encodeLoop(CharBuffer var1, ByteBuffer var2) {
         char[] var3 = var1.array();
         int var4 = var1.arrayOffset() + var1.position();
         int var5 = var1.arrayOffset() + var1.limit();
         byte[] var6 = var2.array();
         int var7 = var2.arrayOffset() + var2.position();
         int var8 = var2.arrayOffset() + var2.limit();

         CoderResult var17;
         try {
            while(true) {
               if (var4 >= var5) {
                  CoderResult var16 = CoderResult.UNDERFLOW;
                  return var16;
               }

               char var9 = var3[var4];
               if (var9 > 127 && var9 < '\ufffe') {
                  int var10 = this.toEUC(var9, this.bb);
                  if (var10 != -1) {
                     int var11 = 0;
                     if (var10 == 4) {
                        var11 = (this.bb[1] & 255) - 160;
                     }

                     if (var11 == this.plane) {
                        if (var8 - var7 >= 2) {
                           if (var10 == 2) {
                              var6[var7++] = (byte)(this.bb[0] & 127);
                              var6[var7++] = (byte)(this.bb[1] & 127);
                           } else {
                              var6[var7++] = (byte)(this.bb[2] & 127);
                              var6[var7++] = (byte)(this.bb[3] & 127);
                           }

                           ++var4;
                           continue;
                        } else {
                           CoderResult var12 = CoderResult.OVERFLOW;
                           return var12;
                        }
                     }
                  }
               }
               break;
            }

            var17 = CoderResult.unmappableForLength(1);
         } finally {
            var1.position(var4 - var1.arrayOffset());
            var2.position(var7 - var2.arrayOffset());
         }

         return var17;
      }
   }
}
