package sun.audio;

import java.io.IOException;
import java.io.InputStream;

public final class AudioTranslatorStream extends NativeAudioStream {
   private final int length = 0;

   public AudioTranslatorStream(InputStream var1) throws IOException {
      super(var1);
      throw new InvalidAudioFormatException();
   }

   public int getLength() {
      return 0;
   }
}
