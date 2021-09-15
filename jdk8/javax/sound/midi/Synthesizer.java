package javax.sound.midi;

public interface Synthesizer extends MidiDevice {
   int getMaxPolyphony();

   long getLatency();

   MidiChannel[] getChannels();

   VoiceStatus[] getVoiceStatus();

   boolean isSoundbankSupported(Soundbank var1);

   boolean loadInstrument(Instrument var1);

   void unloadInstrument(Instrument var1);

   boolean remapInstrument(Instrument var1, Instrument var2);

   Soundbank getDefaultSoundbank();

   Instrument[] getAvailableInstruments();

   Instrument[] getLoadedInstruments();

   boolean loadAllInstruments(Soundbank var1);

   void unloadAllInstruments(Soundbank var1);

   boolean loadInstruments(Soundbank var1, Patch[] var2);

   void unloadInstruments(Soundbank var1, Patch[] var2);
}
