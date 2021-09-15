package com.sun.media.sound;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.spi.MidiDeviceProvider;

public final class RealTimeSequencerProvider extends MidiDeviceProvider {
   public MidiDevice.Info[] getDeviceInfo() {
      MidiDevice.Info[] var1 = new MidiDevice.Info[]{RealTimeSequencer.info};
      return var1;
   }

   public MidiDevice getDevice(MidiDevice.Info var1) {
      if (var1 != null && !var1.equals(RealTimeSequencer.info)) {
         return null;
      } else {
         try {
            return new RealTimeSequencer();
         } catch (MidiUnavailableException var3) {
            return null;
         }
      }
   }
}
