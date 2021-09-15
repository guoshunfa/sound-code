package com.sun.media.sound;

import javax.sound.midi.MidiChannel;

public final class SoftChannelProxy implements MidiChannel {
   private MidiChannel channel = null;

   public MidiChannel getChannel() {
      return this.channel;
   }

   public void setChannel(MidiChannel var1) {
      this.channel = var1;
   }

   public void allNotesOff() {
      if (this.channel != null) {
         this.channel.allNotesOff();
      }
   }

   public void allSoundOff() {
      if (this.channel != null) {
         this.channel.allSoundOff();
      }
   }

   public void controlChange(int var1, int var2) {
      if (this.channel != null) {
         this.channel.controlChange(var1, var2);
      }
   }

   public int getChannelPressure() {
      return this.channel == null ? 0 : this.channel.getChannelPressure();
   }

   public int getController(int var1) {
      return this.channel == null ? 0 : this.channel.getController(var1);
   }

   public boolean getMono() {
      return this.channel == null ? false : this.channel.getMono();
   }

   public boolean getMute() {
      return this.channel == null ? false : this.channel.getMute();
   }

   public boolean getOmni() {
      return this.channel == null ? false : this.channel.getOmni();
   }

   public int getPitchBend() {
      return this.channel == null ? 8192 : this.channel.getPitchBend();
   }

   public int getPolyPressure(int var1) {
      return this.channel == null ? 0 : this.channel.getPolyPressure(var1);
   }

   public int getProgram() {
      return this.channel == null ? 0 : this.channel.getProgram();
   }

   public boolean getSolo() {
      return this.channel == null ? false : this.channel.getSolo();
   }

   public boolean localControl(boolean var1) {
      return this.channel == null ? false : this.channel.localControl(var1);
   }

   public void noteOff(int var1) {
      if (this.channel != null) {
         this.channel.noteOff(var1);
      }
   }

   public void noteOff(int var1, int var2) {
      if (this.channel != null) {
         this.channel.noteOff(var1, var2);
      }
   }

   public void noteOn(int var1, int var2) {
      if (this.channel != null) {
         this.channel.noteOn(var1, var2);
      }
   }

   public void programChange(int var1) {
      if (this.channel != null) {
         this.channel.programChange(var1);
      }
   }

   public void programChange(int var1, int var2) {
      if (this.channel != null) {
         this.channel.programChange(var1, var2);
      }
   }

   public void resetAllControllers() {
      if (this.channel != null) {
         this.channel.resetAllControllers();
      }
   }

   public void setChannelPressure(int var1) {
      if (this.channel != null) {
         this.channel.setChannelPressure(var1);
      }
   }

   public void setMono(boolean var1) {
      if (this.channel != null) {
         this.channel.setMono(var1);
      }
   }

   public void setMute(boolean var1) {
      if (this.channel != null) {
         this.channel.setMute(var1);
      }
   }

   public void setOmni(boolean var1) {
      if (this.channel != null) {
         this.channel.setOmni(var1);
      }
   }

   public void setPitchBend(int var1) {
      if (this.channel != null) {
         this.channel.setPitchBend(var1);
      }
   }

   public void setPolyPressure(int var1, int var2) {
      if (this.channel != null) {
         this.channel.setPolyPressure(var1, var2);
      }
   }

   public void setSolo(boolean var1) {
      if (this.channel != null) {
         this.channel.setSolo(var1);
      }
   }
}
