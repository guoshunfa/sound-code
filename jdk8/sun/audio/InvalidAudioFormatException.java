package sun.audio;

import java.io.IOException;

final class InvalidAudioFormatException extends IOException {
   InvalidAudioFormatException() {
   }

   InvalidAudioFormatException(String var1) {
      super(var1);
   }
}
