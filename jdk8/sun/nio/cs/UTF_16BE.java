package sun.nio.cs;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

class UTF_16BE extends Unicode {
   public UTF_16BE() {
      super("UTF-16BE", StandardCharsets.aliases_UTF_16BE);
   }

   public String historicalName() {
      return "UnicodeBigUnmarked";
   }

   public CharsetDecoder newDecoder() {
      return new UTF_16BE.Decoder(this);
   }

   public CharsetEncoder newEncoder() {
      return new UTF_16BE.Encoder(this);
   }

   private static class Encoder extends UnicodeEncoder {
      public Encoder(Charset var1) {
         super(var1, 0, false);
      }
   }

   private static class Decoder extends UnicodeDecoder {
      public Decoder(Charset var1) {
         super(var1, 1);
      }
   }
}
