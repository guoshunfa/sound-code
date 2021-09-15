package javax.sound.midi.spi;

import javax.sound.midi.MidiDevice;

public abstract class MidiDeviceProvider {
   public boolean isDeviceSupported(MidiDevice.Info var1) {
      MidiDevice.Info[] var2 = this.getDeviceInfo();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var1.equals(var2[var3])) {
            return true;
         }
      }

      return false;
   }

   public abstract MidiDevice.Info[] getDeviceInfo();

   public abstract MidiDevice getDevice(MidiDevice.Info var1);
}
