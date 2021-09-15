package javax.sound.midi;

public interface MidiChannel {
   void noteOn(int var1, int var2);

   void noteOff(int var1, int var2);

   void noteOff(int var1);

   void setPolyPressure(int var1, int var2);

   int getPolyPressure(int var1);

   void setChannelPressure(int var1);

   int getChannelPressure();

   void controlChange(int var1, int var2);

   int getController(int var1);

   void programChange(int var1);

   void programChange(int var1, int var2);

   int getProgram();

   void setPitchBend(int var1);

   int getPitchBend();

   void resetAllControllers();

   void allNotesOff();

   void allSoundOff();

   boolean localControl(boolean var1);

   void setMono(boolean var1);

   boolean getMono();

   void setOmni(boolean var1);

   boolean getOmni();

   void setMute(boolean var1);

   boolean getMute();

   void setSolo(boolean var1);

   boolean getSolo();
}
