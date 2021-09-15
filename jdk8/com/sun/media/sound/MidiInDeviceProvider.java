package com.sun.media.sound;

import javax.sound.midi.MidiDevice;

public final class MidiInDeviceProvider extends AbstractMidiDeviceProvider {
   private static AbstractMidiDeviceProvider.Info[] infos = null;
   private static MidiDevice[] devices = null;
   private static final boolean enabled;

   AbstractMidiDeviceProvider.Info createInfo(int var1) {
      return !enabled ? null : new MidiInDeviceProvider.MidiInDeviceInfo(var1, MidiInDeviceProvider.class);
   }

   MidiDevice createDevice(AbstractMidiDeviceProvider.Info var1) {
      return enabled && var1 instanceof MidiInDeviceProvider.MidiInDeviceInfo ? new MidiInDevice(var1) : null;
   }

   int getNumDevices() {
      if (!enabled) {
         return 0;
      } else {
         int var1 = nGetNumDevices();
         return var1;
      }
   }

   MidiDevice[] getDeviceCache() {
      return devices;
   }

   void setDeviceCache(MidiDevice[] var1) {
      devices = var1;
   }

   AbstractMidiDeviceProvider.Info[] getInfoCache() {
      return infos;
   }

   void setInfoCache(AbstractMidiDeviceProvider.Info[] var1) {
      infos = var1;
   }

   private static native int nGetNumDevices();

   private static native String nGetName(int var0);

   private static native String nGetVendor(int var0);

   private static native String nGetDescription(int var0);

   private static native String nGetVersion(int var0);

   static {
      Platform.initialize();
      enabled = Platform.isMidiIOEnabled();
   }

   static final class MidiInDeviceInfo extends AbstractMidiDeviceProvider.Info {
      private final Class providerClass;

      private MidiInDeviceInfo(int var1, Class var2) {
         super(MidiInDeviceProvider.nGetName(var1), MidiInDeviceProvider.nGetVendor(var1), MidiInDeviceProvider.nGetDescription(var1), MidiInDeviceProvider.nGetVersion(var1), var1);
         this.providerClass = var2;
      }

      // $FF: synthetic method
      MidiInDeviceInfo(int var1, Class var2, Object var3) {
         this(var1, var2);
      }
   }
}
