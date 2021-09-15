package sun.nio.cs;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public abstract class UnicodeEncoder extends CharsetEncoder {
   protected static final char BYTE_ORDER_MARK = '\ufeff';
   protected static final char REVERSED_MARK = '\ufffe';
   protected static final int BIG = 0;
   protected static final int LITTLE = 1;
   private int byteOrder;
   private boolean usesMark;
   private boolean needsMark;
   private final Surrogate.Parser sgp = new Surrogate.Parser();

   protected UnicodeEncoder(Charset var1, int var2, boolean var3) {
      super(var1, 2.0F, var3 ? 4.0F : 2.0F, var2 == 0 ? new byte[]{-1, -3} : new byte[]{-3, -1});
      this.usesMark = this.needsMark = var3;
      this.byteOrder = var2;
   }

   private void put(char var1, ByteBuffer var2) {
      if (this.byteOrder == 0) {
         var2.put((byte)(var1 >> 8));
         var2.put((byte)(var1 & 255));
      } else {
         var2.put((byte)(var1 & 255));
         var2.put((byte)(var1 >> 8));
      }

   }

   protected CoderResult encodeLoop(CharBuffer var1, ByteBuffer var2) {
      int var3 = var1.position();
      if (this.needsMark && var1.hasRemaining()) {
         if (var2.remaining() < 2) {
            return CoderResult.OVERFLOW;
         }

         this.put('\ufeff', var2);
         this.needsMark = false;
      }

      CoderResult var10;
      try {
         while(var1.hasRemaining()) {
            char var4 = var1.get();
            if (!Character.isSurrogate(var4)) {
               if (var2.remaining() < 2) {
                  CoderResult var11 = CoderResult.OVERFLOW;
                  return var11;
               }

               ++var3;
               this.put(var4, var2);
            } else {
               int var5 = this.sgp.parse(var4, var1);
               CoderResult var6;
               if (var5 < 0) {
                  var6 = this.sgp.error();
                  return var6;
               }

               if (var2.remaining() < 4) {
                  var6 = CoderResult.OVERFLOW;
                  return var6;
               }

               var3 += 2;
               this.put(Character.highSurrogate(var5), var2);
               this.put(Character.lowSurrogate(var5), var2);
            }
         }

         var10 = CoderResult.UNDERFLOW;
      } finally {
         var1.position(var3);
      }

      return var10;
   }

   protected void implReset() {
      this.needsMark = this.usesMark;
   }

   public boolean canEncode(char var1) {
      return !Character.isSurrogate(var1);
   }
}
