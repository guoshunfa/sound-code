package sun.awt.motif;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import sun.nio.cs.SingleByte;
import sun.nio.cs.Surrogate;
import sun.nio.cs.ext.JIS_X_0201;

public class X11JIS0201 extends Charset {
   private static Charset jis0201 = new JIS_X_0201();
   private static SingleByte.Encoder enc;

   public X11JIS0201() {
      super("X11JIS0201", (String[])null);
   }

   public CharsetEncoder newEncoder() {
      return new X11JIS0201.Encoder(this);
   }

   public CharsetDecoder newDecoder() {
      return jis0201.newDecoder();
   }

   public boolean contains(Charset var1) {
      return var1 instanceof X11JIS0201;
   }

   static {
      enc = (SingleByte.Encoder)jis0201.newEncoder();
   }

   private class Encoder extends CharsetEncoder {
      private Surrogate.Parser sgp;

      public Encoder(Charset var2) {
         super(var2, 1.0F, 1.0F);
      }

      public boolean canEncode(char var1) {
         return var1 >= '｡' && var1 <= 'ﾟ' || var1 == 8254 || var1 == 165;
      }

      protected CoderResult encodeLoop(CharBuffer var1, ByteBuffer var2) {
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

         try {
            while(true) {
               if (var4 < var5) {
                  char var16 = var3[var4];
                  int var11 = X11JIS0201.enc.encode(var16);
                  if (var11 != 65533) {
                     var6[var7++] = (byte)var11;
                     ++var4;
                     continue;
                  }

                  CoderResult var12;
                  if (Character.isSurrogate(var16)) {
                     if (this.sgp == null) {
                        this.sgp = new Surrogate.Parser();
                     }

                     if (this.sgp.parse(var16, var3, var4, var5) >= 0) {
                        var12 = CoderResult.unmappableForLength(2);
                        return var12;
                     }
                  }

                  var12 = CoderResult.unmappableForLength(1);
                  return var12;
               }

               CoderResult var10 = var9;
               return var10;
            }
         } finally {
            var1.position(var4 - var1.arrayOffset());
            var2.position(var7 - var2.arrayOffset());
         }
      }
   }
}
