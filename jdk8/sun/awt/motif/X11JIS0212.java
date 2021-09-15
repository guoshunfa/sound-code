package sun.awt.motif;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import sun.nio.cs.ext.JIS_X_0212;

public class X11JIS0212 extends Charset {
   private static Charset jis0212 = new JIS_X_0212();

   public X11JIS0212() {
      super("X11JIS0212", (String[])null);
   }

   public CharsetEncoder newEncoder() {
      return jis0212.newEncoder();
   }

   public CharsetDecoder newDecoder() {
      return jis0212.newDecoder();
   }

   public boolean contains(Charset var1) {
      return var1 instanceof X11JIS0212;
   }
}
