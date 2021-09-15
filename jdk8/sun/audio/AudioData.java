package sun.audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class AudioData {
   private static final AudioFormat DEFAULT_FORMAT;
   AudioFormat format;
   byte[] buffer;

   public AudioData(byte[] var1) {
      this(DEFAULT_FORMAT, var1);

      try {
         AudioInputStream var2 = AudioSystem.getAudioInputStream((InputStream)(new ByteArrayInputStream(var1)));
         this.format = var2.getFormat();
         var2.close();
      } catch (IOException var3) {
      } catch (UnsupportedAudioFileException var4) {
      }

   }

   AudioData(AudioFormat var1, byte[] var2) {
      this.format = var1;
      if (var2 != null) {
         this.buffer = Arrays.copyOf(var2, var2.length);
      }

   }

   static {
      DEFAULT_FORMAT = new AudioFormat(AudioFormat.Encoding.ULAW, 8000.0F, 8, 1, 1, 8000.0F, true);
   }
}
