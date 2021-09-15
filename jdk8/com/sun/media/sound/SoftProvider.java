package com.sun.media.sound;

import java.util.Arrays;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.spi.MidiDeviceProvider;

public final class SoftProvider extends MidiDeviceProvider {
   static final MidiDevice.Info softinfo;
   private static final MidiDevice.Info[] softinfos;

   public MidiDevice.Info[] getDeviceInfo() {
      return (MidiDevice.Info[])Arrays.copyOf((Object[])softinfos, softinfos.length);
   }

   public MidiDevice getDevice(MidiDevice.Info var1) {
      return var1 == softinfo ? new SoftSynthesizer() : null;
   }

   static {
      softinfo = SoftSynthesizer.info;
      softinfos = new MidiDevice.Info[]{softinfo};
   }
}
