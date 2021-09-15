package com.sun.media.sound;

import java.util.Map;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;

public interface AudioSynthesizer extends Synthesizer {
   AudioFormat getFormat();

   AudioSynthesizerPropertyInfo[] getPropertyInfo(Map<String, Object> var1);

   void open(SourceDataLine var1, Map<String, Object> var2) throws MidiUnavailableException;

   AudioInputStream openStream(AudioFormat var1, Map<String, Object> var2) throws MidiUnavailableException;
}
