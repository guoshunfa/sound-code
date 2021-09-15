package sun.nio.cs;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

class UTF_16LE extends Unicode {
   public UTF_16LE() {
      super("UTF-16LE", StandardCharsets.aliases_UTF_16LE);
   }

   public String historicalName() {
      return "UnicodeLittleUnmarked";
   }

   public CharsetDecoder newDecoder() {
      return new UTF_16LE.Decoder(this);
   }

   public CharsetEncoder newEncoder() {
      return new UTF_16LE.Encoder(this);
   }

   private static class Encoder extends UnicodeEncoder {
      public Encoder(Charset var1) {
         super(var1, 1, false);
      }
   }

   private static class Decoder extends UnicodeDecoder {
      public Decoder(Charset var1) {
         super(var1, 2);
      }
   }
}
