package sun.audio;

import java.io.ByteArrayInputStream;

public class AudioDataStream extends ByteArrayInputStream {
   private final AudioData ad;

   public AudioDataStream(AudioData var1) {
      super(var1.buffer);
      this.ad = var1;
   }

   final AudioData getAudioData() {
      return this.ad;
   }
}
