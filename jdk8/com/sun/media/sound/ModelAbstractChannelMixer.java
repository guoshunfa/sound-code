package com.sun.media.sound;

public abstract class ModelAbstractChannelMixer implements ModelChannelMixer {
   public abstract boolean process(float[][] var1, int var2, int var3);

   public abstract void stop();

   public void allNotesOff() {
   }

   public void allSoundOff() {
   }

   public void controlChange(int var1, int var2) {
   }

   public int getChannelPressure() {
      return 0;
   }

   public int getController(int var1) {
      return 0;
   }

   public boolean getMono() {
      return false;
   }

   public boolean getMute() {
      return false;
   }

   public boolean getOmni() {
      return false;
   }

   public int getPitchBend() {
      return 0;
   }

   public int getPolyPressure(int var1) {
      return 0;
   }

   public int getProgram() {
      return 0;
   }

   public boolean getSolo() {
      return false;
   }

   public boolean localControl(boolean var1) {
      return false;
   }

   public void noteOff(int var1) {
   }

   public void noteOff(int var1, int var2) {
   }

   public void noteOn(int var1, int var2) {
   }

   public void programChange(int var1) {
   }

   public void programChange(int var1, int var2) {
   }

   public void resetAllControllers() {
   }

   public void setChannelPressure(int var1) {
   }

   public void setMono(boolean var1) {
   }

   public void setMute(boolean var1) {
   }

   public void setOmni(boolean var1) {
   }

   public void setPitchBend(int var1) {
   }

   public void setPolyPressure(int var1, int var2) {
   }

   public void setSolo(boolean var1) {
   }
}
