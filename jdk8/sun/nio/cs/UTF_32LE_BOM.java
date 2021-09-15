package sun.nio.cs;

import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class UTF_32LE_BOM extends Unicode {
   public UTF_32LE_BOM() {
      super("X-UTF-32LE-BOM", StandardCharsets.aliases_UTF_32LE_BOM);
   }

   public String historicalName() {
      return "X-UTF-32LE-BOM";
   }

   public CharsetDecoder newDecoder() {
      return new UTF_32Coder.Decoder(this, 2);
   }

   public CharsetEncoder newEncoder() {
      return new UTF_32Coder.Encoder(this, 2, true);
   }
}
