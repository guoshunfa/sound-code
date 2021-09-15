package com.sun.media.sound;

import java.io.IOException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.VoiceStatus;

public interface ModelOscillatorStream {
   void setPitch(float var1);

   void noteOn(MidiChannel var1, VoiceStatus var2, int var3, int var4);

   void noteOff(int var1);

   int read(float[][] var1, int var2, int var3) throws IOException;

   void close() throws IOException;
}
