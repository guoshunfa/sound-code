package sun.nio.cs;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

class UTF_16 extends Unicode {
   public UTF_16() {
      super("UTF-16", StandardCharsets.aliases_UTF_16);
   }

   public String historicalName() {
      return "UTF-16";
   }

   public CharsetDecoder newDecoder() {
      return new UTF_16.Decoder(this);
   }

   public CharsetEncoder newEncoder() {
      return new UTF_16.Encoder(this);
   }

   private static class Encoder extends UnicodeEncoder {
      public Encoder(Charset var1) {
         super(var1, 0, true);
      }
   }

   private static class Decoder extends UnicodeDecoder {
      public Decoder(Charset var1) {
         super(var1, 0);
      }
   }
}
