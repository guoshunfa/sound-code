package sun.nio.cs;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

class UTF_16LE_BOM extends Unicode {
   public UTF_16LE_BOM() {
      super("x-UTF-16LE-BOM", StandardCharsets.aliases_UTF_16LE_BOM);
   }

   public String historicalName() {
      return "UnicodeLittle";
   }

   public CharsetDecoder newDecoder() {
      return new UTF_16LE_BOM.Decoder(this);
   }

   public CharsetEncoder newEncoder() {
      return new UTF_16LE_BOM.Encoder(this);
   }

   private static class Encoder extends UnicodeEncoder {
      public Encoder(Charset var1) {
         super(var1, 1, true);
      }
   }

   private static class Decoder extends UnicodeDecoder {
      public Decoder(Charset var1) {
         super(var1, 0, 2);
      }
   }
}
