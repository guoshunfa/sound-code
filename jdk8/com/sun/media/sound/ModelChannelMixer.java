package com.sun.media.sound;

import javax.sound.midi.MidiChannel;

public interface ModelChannelMixer extends MidiChannel {
   boolean process(float[][] var1, int var2, int var3);

   void stop();
}
