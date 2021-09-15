package sun.audio;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiFileFormat;
import javax.sound.midi.MidiSystem;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class AudioStream extends FilterInputStream {
   AudioInputStream ais = null;
   AudioFormat format = null;
   MidiFileFormat midiformat = null;
   InputStream stream = null;

   public AudioStream(InputStream var1) throws IOException {
      super(var1);
      this.stream = var1;
      if (!var1.markSupported()) {
         this.stream = new BufferedInputStream(var1, 1024);
      }

      try {
         this.ais = AudioSystem.getAudioInputStream(this.stream);
         this.format = this.ais.getFormat();
         this.in = this.ais;
      } catch (UnsupportedAudioFileException var5) {
         try {
            this.midiformat = MidiSystem.getMidiFileFormat(this.stream);
         } catch (InvalidMidiDataException var4) {
            throw new IOException("could not create audio stream from input stream");
         }
      }

   }

   public AudioData getData() throws IOException {
      int var1 = this.getLength();
      if (var1 < 1048576) {
         byte[] var2 = new byte[var1];

         try {
            this.ais.read(var2, 0, var1);
         } catch (IOException var4) {
            throw new IOException("Could not create AudioData Object");
         }

         return new AudioData(this.format, var2);
      } else {
         throw new IOException("could not create AudioData object");
      }
   }

   public int getLength() {
      if (this.ais != null && this.format != null) {
         return (int)(this.ais.getFrameLength() * (long)this.ais.getFormat().getFrameSize());
      } else {
         return this.midiformat != null ? this.midiformat.getByteLength() : -1;
      }
   }
}
