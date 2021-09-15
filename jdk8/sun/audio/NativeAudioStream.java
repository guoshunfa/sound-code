package sun.audio;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class NativeAudioStream extends FilterInputStream {
   public NativeAudioStream(InputStream var1) throws IOException {
      super(var1);
   }

   public int getLength() {
      return 0;
   }
}
